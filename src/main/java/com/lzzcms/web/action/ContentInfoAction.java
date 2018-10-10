package com.lzzcms.web.action;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.lzzcms.dto.GridDto;
import com.lzzcms.model.ExtraColumnsDescForChl;
import com.lzzcms.service.ContentInfoService;
import com.lzzcms.utils.LzzConstants;
import com.lzzcms.utils.LzzcmsUtils;
import com.lzzcms.utils.PageContext;
import com.lzzcms.utils.PageUtil;

import net.sf.json.JSONObject;

/**
 * @author zhao
 */
@Controller
public class ContentInfoAction {
	private static Logger logger=Logger.getLogger(ContentInfoAction.class);
	@Resource
	private ContentInfoService contentInfoService;
	
	public ContentInfoService getContentInfoService() {
		return contentInfoService;
	}

	public void setContentInfoService(ContentInfoService contentInfoService) {
		this.contentInfoService = contentInfoService;
	}

	@RequestMapping("/contentManage")
	public String contentManage(){
		return "content/contentManage";
	}
	@RequestMapping("/listConts") @ResponseBody
	public GridDto<Map<String, Object>> listConts(HttpServletRequest request){
	    GridDto<Map<String, Object>> gridDto=new GridDto<Map<String, Object>>();
		Map<String, String> paramMap=new LinkedHashMap<String, String>();
		String parameter = request.getParameter("param");
		Gson gson=new Gson();
		if (parameter!=null) {
			paramMap = gson.fromJson(parameter, Map.class);
		}
		 List<Map<String, Object>>  list=contentInfoService.trueList(paramMap,request);
		 //得到数量
		 PageUtil pageUtil = PageContext.getPageUtil();
		 pageUtil.setNeedPage(false);
		 List<Map<String, Object>>  countList=contentInfoService.trueList(paramMap,request);
		 int count = Integer.valueOf(countList.get(0).get("count").toString());
		 gridDto.setTotal(count);
		 gridDto.setRows(list);
		 return gridDto;
	}
	@RequestMapping("/getAddforinfo") 
	public String getAddforinfo(Map<String, Object> map,HttpServletRequest request){
		String comm_id=request.getParameter("comm_id");
		String channel_id=request.getParameter("channel_id");
		Map<String, Object> addforinfo = contentInfoService.getAddforinfo(comm_id,channel_id);
		map.put("addforinfo",addforinfo);
		return "content/extrainfo";
	}
	@RequestMapping("/toAdd") 
	public String toAdd(){
		return "content/toAdd";
	}
	//新增内容页面获取不同模型的附加字段信息
	@RequestMapping("/toAddExtral") 
	public String toAddExtral(Map<String, Object> map,HttpServletRequest request){
		String channel_id=request.getParameter("channel_id");
		List<ExtraColumnsDescForChl> extraColumnsDescForChls = contentInfoService.toAddExtral(channel_id);
		map.put("extraCols",extraColumnsDescForChls);
		return "content/extraCols";
	}
	//内容的提交
	@RequestMapping(value="/trueAddContnet",produces="text/html;charset=UTF-8") @ResponseBody
	public String trueAddContnet(@RequestParam("toAdd_comm_thumbpic_file") MultipartFile file,HttpServletRequest request){
		Map<String, Object> returnMap=new HashMap<String, Object>();
		String ret=null;
		try {
			ret = contentInfoService.trueAddContnet(file,request);
			returnMap.put("status", "success");
			if (ret!=null) {//提交的是缩略图
				returnMap.put("info", ret);
			}else{
				returnMap.put("info", "添加文档成功");
			}
		} catch (Exception e) {
			logger.error("添加文档出错", e);
			returnMap.put("status", "error");
			returnMap.put("info", "添加文档或缩略图出错");
		}
		JSONObject jsonObject=JSONObject.fromObject(returnMap);
		return jsonObject.toString();
	}
	//内容的修改
	@RequestMapping(value="/toUpdate",method=RequestMethod.GET,produces="text/plain;charset=UTF-8") 
	public String toUpdate(HttpServletRequest request,Map<String, Map<String, Object>> map){
		String comm_id = request.getParameter("id");
		Map<String, Object>  commInfo= contentInfoService.getCommById(comm_id);
		map.put("commInfo", commInfo);
		return "content/toUpdate";
	}
	//修改内容页面获取不同模型的附加字段信息
	@RequestMapping("/toUpExtral") 
	public String toUpExtral(Map<String, Object> map,HttpServletRequest request){
		String channel_id=request.getParameter("channel_id");
		String comm_id=request.getParameter("comm_id");
		List<ExtraColumnsDescForChl> extraColumnsDescForChls = contentInfoService.toAddExtral(channel_id);
		map.put("extraCols",extraColumnsDescForChls);
		String additionalTable = extraColumnsDescForChls.get(0).getAdditionalTable();
		Map<String, Object> oneAddFor=contentInfoService.getAddforinfoById(comm_id, channel_id,additionalTable);
		for(ExtraColumnsDescForChl e:extraColumnsDescForChls){
			if ("richtext".equals(e.getColType())) {
				Object object = oneAddFor.get(e.getColName());
				if (object!=null) {
					String str=object.toString();
					str=str.replace("&", "&amp;");
					oneAddFor.put(e.getColName(), str);
				}
			}
		}
		map.put("oneAddFor",oneAddFor);
		return "content/toUpExtralCols";
	}
	//修改内容的提交
	@RequestMapping(value="/trueUpContnet",produces="text/plain;charset=UTF-8") @ResponseBody
	public String trueUpContnet(@RequestParam("toUp_comm_thumbpic_file") MultipartFile file,HttpServletRequest request){
		Map<String, Object> returnMap=new HashMap<String, Object>();
		String ret=null;
		try {
			ret = contentInfoService.trueUpContnet(file,request);
			returnMap.put("status", "success");
			if (ret!=null) {//提交的是缩略图
				returnMap.put("info", ret);
			}else{
				returnMap.put("info", "添加文档成功");
			}
		} catch (Exception e) {
			logger.error("更新文档出错", e);
			returnMap.put("status", "error");
			returnMap.put("info", "更新文档或缩略图出错");
		}
		JSONObject jsonObject=JSONObject.fromObject(returnMap);
		return jsonObject.toString();
	}
	//删除内容
	@RequestMapping(value="/deleteContent",produces="text/plain;charset=UTF-8") @ResponseBody
	public String deleteContent(HttpServletRequest request){
		String param = request.getParameter("param");//[{"comm_id":1,"chl_id":1},{"comm_id":17,"chl_id":1}]
		Gson gson=new Gson();
		List<Map<String, Object>> list=gson.fromJson(param, List.class);//[{comm_id=1.0, chl_id=1.0}, {comm_id=17.0, chl_id=1.0}]
		contentInfoService.deleteContent(list,request,"need");
		return "success";
	}
	@RequestMapping("/autoGeKeywordsAndIntro") @ResponseBody
	public Map<String, Object> autoGeKeywordsAndIntro(HttpServletRequest request){
		String txt = request.getParameter("txt");
		 String dir = LzzcmsUtils.getRealPath(request,LzzConstants.KEYWORDSANDINTRO_INDEIES);
		 Map<String, Object>  map=contentInfoService.autoGeKeywordsAndIntro(txt,dir);
		 return map;
	}
	//内容页field name='comm_click'获取点击量
	@RequestMapping(value="/updateAndgetClick",produces="text/plain;charset=UTF-8") @ResponseBody
	public String updateAndgetClick(HttpServletRequest request){
		String comm_id = request.getParameter("comm_id");
		Long click=contentInfoService.updateAndgetClick(comm_id);
		return click+"";
	}
}
