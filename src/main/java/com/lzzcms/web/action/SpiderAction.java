package com.lzzcms.web.action;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lzzcms.dao.SpiderDao;
import com.lzzcms.service.SpiderService;
import com.lzzcms.utils.LzzcmsUtils;
import com.lzzcms.utils.PageContext;
import com.lzzcms.utils.ProxyIpCxt;

@Controller
public class SpiderAction {
	private Logger logger=Logger.getLogger(SpiderAction.class);
	@Resource
	private SpiderService spiderService;
	@Resource
	private SpiderDao spiderDao;
	@RequestMapping("/spiderEntrance")
	public String spiderEntrance(HttpServletRequest request){
		return "spider/spiderEntrance";
	}
	@RequestMapping("/parseUrl") @ResponseBody
	public String parseUrl(HttpServletRequest request,HttpServletResponse response){
		Map<String, String> map = spiderService.parseUrl(request);
		if (map.get("status")!=null) {
			logger.error("解析url出错："+map.get("status"));
		}
		 String targetUrl = map.get("src");
		return targetUrl;
	}
	@RequestMapping("/toCfgSpider")
	public String toCfgSpider(HttpServletRequest request){
		String targetHtmlUrl =request.getParameter("src");
		String cfgId =request.getParameter("cfgId");
		request.setAttribute("src", targetHtmlUrl);
		request.setAttribute("cfgId",cfgId);
		return "spider/cfgSpider";
	}
	@RequestMapping("/getCfgById") @ResponseBody
	public List<Map<String, Object>> getCfgById(HttpServletRequest request){
		List<Map<String, Object>> list=new ArrayList<>();
		String cfgId =request.getParameter("cfgId");
		list=spiderService.getCfgById(cfgId);
		return list;
	}
	@RequestMapping("/startSpiderById") @ResponseBody
	public Map<String, String> startSpiderById(HttpServletRequest request){
		
		 Map<String, String>  map=spiderService.startSpiderById(request);
		return map;
	}
	@RequestMapping("/spiderHand")
	public String spiderHand( ){
		return "spider/spiderHand";
	}
	//手工爬取任务启动
	@RequestMapping("/startSpiderHand") @ResponseBody
	public Map<String, Object> startSpiderHand(HttpServletRequest request,HttpServletResponse response){
		List<String> errorList=null; 
		Map<String, Object> map=new HashMap<String, Object>();
		try {
			errorList=spiderService.startSpiderHand(request, response);
			if (errorList.size()==0) {
				map.put("status", "ok");
				map.put("info", "执行完成");
			}else {
				map.put("status", "errorList");
				map.put("info", errorList);
			}
		} catch (Exception e) {
			logger.error("手工爬取任务执行失败:",e);
			map.put("status", "error");
			map.put("info", e.getMessage());
		}
		return map;
	}
	//定时采集或者页面手动点击按钮
	@RequestMapping("/crawlTask") 
	public Map<String, Object> crawlTask(HttpServletRequest request) throws IOException{
		String page = request.getParameter("from");
		if (StringUtils.isNotBlank(page)) {
			logger.info("页面手工启动规则采集任务开始....");
		}
		Map<String, Object> retMap=new HashMap<String, Object>();
		try {
			if(validateSyncStatus(request)){//没有进程在采集才可以执行采集任务
				writeStatus(request,1);
				StringBuffer sb=new StringBuffer();
				sb.append(" select id,list_url,list_item_selector,column_id,proxy_ip from lzz_crawl where ");
				sb.append(" is_deleted ='N' ");
				List<Map<String, Object>> crawlList = spiderDao.queryForList(sb.toString());
				int size = crawlList.size();
				if (size!=0) {
					/*
					  * 每一条lzz_crawl（列表）对应着N多个a,每一个a使用下面的comm_titleSelector
					  * 和mainbodySelector来获取内容
					  */
				  for(int i=0;i<size;i++){
					Map<String, Object> crawlMap=crawlList.get(i);
					ProxyIpCxt.setIp(crawlMap.get("proxy_ip")==null?"":
						crawlMap.get("proxy_ip").toString());
					processOneCrawl(crawlMap,request);
				  }
				}
			}else{
				logger.info("有采集任务正在执行,本次任务不执行");
			}
			retMap.put("hand_crawl_status", "success");
		} catch (Exception e) {
			retMap.put("hand_crawl_status", "error");
			logger.error("同步出错:",e);
		}finally{
			writeStatus(request, 2);
		}
		if (StringUtils.isNotBlank(page)) {
			logger.info("页面手工启动规则采集任务结束....");
		}
		return retMap;
	}
	private void processOneCrawl(Map<String, Object> crawlMap, HttpServletRequest request){
		StringBuffer sb=new StringBuffer();
		//获取当前列表对应着的N多个a点击进去之后的css选择器
		sb.append(" select id,crawl_id,field_name,field_selector from lzz_crawl_detail ");
		sb.append(" where is_deleted ='N' and crawl_id=? ");
		List<Map<String, Object>> crawlDetailList = spiderDao.queryForList(sb.toString(), 
				Integer.valueOf(crawlMap.get("id").toString()));
		//设置css选择器的值
		String comm_titleSelector="";
		String mainbodySelector="";
		String excludeSelector="";
		for (Map<String, Object> m:crawlDetailList) {
			if ("comm_title".equals(m.get("field_name").toString())) {
				comm_titleSelector=m.get("field_selector").toString();
			}
			if ("mainbody".equals(m.get("field_name").toString())) {
				mainbodySelector=m.get("field_selector").toString();
			}
			if ("will_exclude".equals(m.get("field_name").toString())) {
				excludeSelector=m.get("field_selector")==null?"":m.get("field_selector").toString();
			}
		}
		if (StringUtils.isNotBlank(comm_titleSelector)&&StringUtils.isNotBlank(mainbodySelector)) {
			//获取一个crawl配置对应的N多个a的超链接值
			List<String> hrefs=spiderService.item_href_value_list(crawlMap.get("list_url").toString(),
					crawlMap.get("list_item_selector").toString());
			if (hrefs!=null&&hrefs.size()>0) {
				for(String oneHref:hrefs){
					Object column_idObject= crawlMap.get("column_id");
					String column_id=null;
					if (column_idObject!=null) {
						column_id=column_idObject.toString();
					}
					try {//确保一条失败了可以继续执行下一条
						spiderService.saveOneHref(oneHref,comm_titleSelector,mainbodySelector,
								column_id,request,excludeSelector);
					} catch (Exception e) {
						logger.error("网址:"+oneHref+"采集失败",e);
					}
				}
			}else{
				logger.info("获取不到一个crawl配置对应的N多个a的超链接值");
			}
		}else {
			logger.info("标题或者内容的选择器不能为空,跳过当前采集配置");
		}
	}
	private boolean validateSyncStatus(HttpServletRequest request) throws IOException{
		String realPath = LzzcmsUtils.getRealPath(request, "/sync/sync.txt");
		logger.info("通过httpclient传入的请求是否可以正常的使用request呢?"+realPath);
		File file=new File(realPath);
		if (file.exists()) {
			String str = FileUtils.readFileToString(file, "utf-8");
			JSONObject jo = JSONObject.fromObject(str);
			int status = jo.getInt("status");
			if (status==1) {//正在同步
				return false;
			}
		}
		return true;
	}
	private JSONObject writeStatus(HttpServletRequest request, int i) throws IOException {
		String realPath = LzzcmsUtils.getRealPath(request, "/sync/sync.txt");
		File file=new File(realPath);
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("status", i);//0：未进行 1：正在同步 2：已完成
		if (i==2) {
			jsonObject.put("last_sync_date", LzzcmsUtils.getPatternDateString(null, new Date()));
		}
		if (i!=0) {//未进行不写入
			FileUtils.writeStringToFile(file, jsonObject.toString(), "utf-8");
		}else {
			if (file.exists()) {//0时存在要读取
				String str = FileUtils.readFileToString(file, "utf-8");
				jsonObject=JSONObject.fromObject(str);
			}
		}
		return jsonObject;
	}
	@RequestMapping("/getCrawls") @ResponseBody
	public Map<String, Object> getCrawls(HttpServletRequest request){
		Map<String, Object> retMap=new HashMap<String, Object>();
		try {
			Map<String, Object> paramMap=new HashMap<String, Object>();
			paramMap.put("pageNow",PageContext.getPageUtil().getPageNow());
			paramMap.put("pageSize",PageContext.getPageUtil().getPageSize() );
			if (StringUtils.isNotBlank(request.getParameter("sortName"))&&
					StringUtils.isNotBlank(request.getParameter("sortOrder"))) {
				paramMap.put("order",request.getParameter("sortName")+
						" "+request.getParameter("sortOrder"));
			}
			List<Map<String, Object>> retList=spiderService.getCrawls(paramMap);
			Long count=spiderService.getCrawlsCount(paramMap);
			retMap.put("total", count);
			retMap.put("rows", retList);
		} catch (Exception e) {
			logger.error("获取采集配置列表出错:",e);
		}
		
		return retMap;
	}
	@RequestMapping("/toGetCrawls") 
	public String toGetCrawls(HttpServletRequest request){
		return "crawl/crawlManage";
	}
	@RequestMapping("/addCrawl") @ResponseBody
	public Map<String, Object> addCrawl(HttpServletRequest request){
		Map<String, Object> retMap=new HashMap<String, Object>();
		try {
			Map<String, Object> paramMap=new HashMap<String, Object>();
			paramMap.put("list_url",request.getParameter("list_url"));
			paramMap.put("list_item_selector",request.getParameter("list_item_selector"));
			paramMap.put("proxy_ip",request.getParameter("proxy_ip"));
			paramMap.put("column_id",request.getParameter("column_id"));
			paramMap.put("gmt_created",LzzcmsUtils.getPatternDateString(null, new Date()));
			paramMap.put("gmt_modified",LzzcmsUtils.getPatternDateString(null, new Date()));
			spiderService.addCrawl(paramMap);
			retMap.put("status", "success");
		} catch (Exception e) {
			retMap.put("status", "error");
			logger.error("添加采集配置出错:",e);
		}
		return retMap;
	}
	@RequestMapping("/addCrawlDetail") @ResponseBody
	public Map<String, Object> addCrawlDetail(HttpServletRequest request){
		Map<String, Object> retMap=new HashMap<String, Object>();
		try {
			List<Map<String, Object>> paramList=new ArrayList<Map<String, Object>>();
			String date = LzzcmsUtils.getPatternDateString(null, new Date());
			
			Map<String, Object> titleMap=new HashMap<String, Object>();
			titleMap.put("crawl_id",request.getParameter("crawl_id"));
			titleMap.put("field_name","comm_title");
			titleMap.put("field_selector",request.getParameter("comm_title_selector"));
			titleMap.put("gmt_created",date);
			titleMap.put("gmt_modified",date);
			paramList.add(titleMap);
			
			Map<String, Object> mainbodyMap=new HashMap<String, Object>();
			mainbodyMap.put("crawl_id",request.getParameter("crawl_id"));
			mainbodyMap.put("field_name","mainbody");
			mainbodyMap.put("field_selector",request.getParameter("mainbody_selector"));
			mainbodyMap.put("gmt_created",date);
			mainbodyMap.put("gmt_modified",date);
			paramList.add(mainbodyMap);
			
			Map<String, Object> willExcludeMap=new HashMap<String, Object>();
			willExcludeMap.put("crawl_id",request.getParameter("crawl_id"));
			willExcludeMap.put("field_name","will_exclude");
			willExcludeMap.put("field_selector",request.getParameter("will_exclude_selector"));
			willExcludeMap.put("gmt_created",date);
			willExcludeMap.put("gmt_modified",date);
			paramList.add(willExcludeMap);
			
			spiderService.addCrawlDetail(paramList);
			retMap.put("status", "success");
		} catch (Exception e) {
			retMap.put("status", "error");
			logger.error("添加采集配置明细出错:",e);
		}
		return retMap;
	}
	@RequestMapping("/updateCrawlDetail") @ResponseBody
	public Map<String, Object> updateCrawlDetail(HttpServletRequest request){
		Map<String, Object> retMap=new HashMap<String, Object>();
		try {
			List<Map<String, Object>> paramList=new ArrayList<Map<String, Object>>();
			String date = LzzcmsUtils.getPatternDateString(null, new Date());
			
			Map<String, Object> titleMap=new HashMap<String, Object>();
			titleMap.put("crawl_id",request.getParameter("crawl_id"));
			titleMap.put("field_name","comm_title");
			titleMap.put("field_selector",request.getParameter("comm_title_selector"));
			titleMap.put("gmt_modified",date);
			paramList.add(titleMap);
			
			Map<String, Object> mainbodyMap=new HashMap<String, Object>();
			mainbodyMap.put("crawl_id",request.getParameter("crawl_id"));
			mainbodyMap.put("field_name","mainbody");
			mainbodyMap.put("field_selector",request.getParameter("mainbody_selector"));
			mainbodyMap.put("gmt_modified",date);
			paramList.add(mainbodyMap);
			
			Map<String, Object> willExcludeMap=new HashMap<String, Object>();
			willExcludeMap.put("crawl_id",request.getParameter("crawl_id"));
			willExcludeMap.put("field_name","will_exclude");
			willExcludeMap.put("field_selector",request.getParameter("will_exclude_selector"));
			willExcludeMap.put("gmt_modified",date);
			paramList.add(willExcludeMap);
			spiderService.updateCrawlDetail(paramList);
			retMap.put("status", "success");
		} catch (Exception e) {
			retMap.put("status", "error");
			logger.error("修改采集配置明细出错:",e);
		}
		return retMap;
	}
	@RequestMapping("/updateCrawl") @ResponseBody
	public Map<String, Object> updateCrawl(HttpServletRequest request){
		Map<String, Object> retMap=new HashMap<String, Object>();
		try {
			String date = LzzcmsUtils.getPatternDateString(null, new Date());
			Map<String, Object> paramMap=new HashMap<String, Object>();
			paramMap.put("id",request.getParameter("crawl_id_update_crawl"));
			paramMap.put("list_url",request.getParameter("list_url"));
			paramMap.put("list_item_selector",request.getParameter("list_item_selector"));
			paramMap.put("proxy_ip",request.getParameter("proxy_ip"));
			paramMap.put("column_id",request.getParameter("column_id"));
			paramMap.put("gmt_modified",date);
			spiderService.updateCrawl(paramMap);
			retMap.put("status", "success");
		} catch (Exception e) {
			retMap.put("status", "error");
			logger.error("修改采集配置出错:",e);
		}
		return retMap;
	}
	@RequestMapping("/showCrawlDetailByCrawlId") @ResponseBody
	public Map<String, Object> showCrawlDetailByCrawlId(HttpServletRequest request){
		Map<String, Object> retMap=null;
		try {
			Map<String, Object> paramMap=new HashMap<String, Object>();
			paramMap.put("crawl_id",request.getParameter("crawl_id"));
			retMap=spiderService.showCrawlDetailByCrawlId(paramMap);
			retMap.put("status", "success");
		} catch (Exception e) {
			retMap.put("status", "error");
			logger.error("查看采集配置明细出错:",e);
		}
		return retMap;
	}
	@RequestMapping("/showCrawlByCrawlId") @ResponseBody
	public Map<String, Object> showCrawlByCrawlId(HttpServletRequest request){
		Map<String, Object> retMap=null;
		try {
			Map<String, Object> paramMap=new HashMap<String, Object>();
			paramMap.put("crawl_id",request.getParameter("crawl_id"));
			retMap=spiderService.showCrawlByCrawlId(paramMap);
			retMap.put("status", "success");
		} catch (Exception e) {
			retMap.put("status", "error");
			logger.error("查看采集配置出错:",e);
		}
		return retMap;
	}
	@RequestMapping("/deleteCrawlById") @ResponseBody
	public Map<String, Object> deleteCrawlById(HttpServletRequest request){
		Map<String, Object> retMap=new HashMap<String, Object>();
		try {
			Map<String, Object> paramMap=new HashMap<String, Object>();
			paramMap.put("id",request.getParameter("id"));
			spiderService.deleteCrawlById(paramMap);
			retMap.put("status", "success");
		} catch (Exception e) {
			retMap.put("status", "error");
			logger.error("删除采集配置出错:",e);
		}
		return retMap;
	}
	@RequestMapping("/onOffCrawl") @ResponseBody
	public Map<String, Object> onOffCrawl(HttpServletRequest request){
		Map<String, Object> retMap=new HashMap<String, Object>();
		try {
			Map<String, Object> paramMap=new HashMap<String, Object>();
			paramMap.put("id",request.getParameter("id"));
			paramMap.put("is_deleted",request.getParameter("is_deleted"));
			spiderService.onOffItem(paramMap);
			retMap.put("status", "success");
		} catch (Exception e) {
			retMap.put("status", "error");
			logger.error("采集配置上下架出错:",e);
		}
		return retMap;
	}
	@RequestMapping("/getSyncStatus") @ResponseBody
	public JSONObject getSyncStatus(HttpServletRequest request){
		JSONObject retMap=new JSONObject();
		try {
			retMap=writeStatus(request, 0);
			retMap.put("run_status", "success");
		} catch (Exception e) {
			retMap.put("run_status", "error");
			logger.error("获取同步状态出错:",e);
		}
		return retMap;
	}
}
