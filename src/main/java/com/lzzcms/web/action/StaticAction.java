package com.lzzcms.web.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lzzcms.dto.TreeDto;
import com.lzzcms.service.StaticService;
import com.lzzcms.service.impl.StaticServiceImpl;

/**
 * 静态化处理
 * @author zhao
 */
@Controller
public class StaticAction {
	private Logger logger=Logger.getLogger(StaticAction.class);
	@Resource
	private StaticService staticService;
	public StaticService getStaticService() {
		return staticService;
	}
	public void setStaticService(StaticService staticService) {
		this.staticService = staticService;
	}
	@RequestMapping("/toMakeIndex")
	public String toMakeIndex(){
		return "static/toMakeIndex";
	}
	@RequestMapping("/toMakeCln")
	public String toMakeCln(){
		return "static/toMakeCln";
	}
	@RequestMapping("/toMakeCont")
	public String toMakeCont(){
		return "static/toMakeCont";
	}
	//生成主页
	@RequestMapping("/makeIndex") @ResponseBody
	public Map<String, Object> makeIndex(HttpServletRequest request,HttpServletResponse response){
		String ret =null; 
		Map<String, Object> map=new HashMap<String, Object>();
		try {
			ret=staticService.makeIndex(request, response);
			if (ret==null) {
				map.put("info", "ok");
			}else {
				map.put("info", "error");
				map.put("errinfo", ret);
			}
		} catch (Exception e) {
			map.put("info", "error");
			map.put("errinfo", e.getMessage());
		}
		return map;
	}
	//生成栏目
	@RequestMapping("/makeCln") @ResponseBody
	public Map<String, Object> makeCln(HttpServletRequest request,HttpServletResponse response){
		String clnid = request.getParameter("clnid");
		String ret =null; 
		Map<String, Object> map=new HashMap<String, Object>();
		try {
			ret=staticService.makeCln(request, response,clnid);
			if (ret==null) {
				map.put("info", "ok");
			}else {
				map.put("info", "error");
				map.put("errinfo", ret);
			}
		} catch (Exception e) {
			map.put("info", "error");
			map.put("errinfo", e.getMessage());
			logger.error("生成栏目出错:",e);
		}
		return map;
	}
	//增量生成栏目
	@RequestMapping("/makeClnIncr") @ResponseBody
	public Map<String, Object> makeClnIncr(HttpServletRequest request,HttpServletResponse response){
		return makeCln(request, response);
	}
	//生成文档全量
	@RequestMapping("/makeCont") @ResponseBody
	public Map<String, Object> makeCont(HttpServletRequest request,HttpServletResponse response){
		String clnid = request.getParameter("clnid");
		String ret =null; 
		Map<String, Object> map=new HashMap<String, Object>();
		try {
			ret=staticService.makeCont(request, response,clnid);
			if (ret==null) {
				map.put("info", "ok");
			}else {
				map.put("info", "error");
				map.put("errinfo", ret);
			}
		} catch (Exception e) {
			map.put("info", "error");
			map.put("errinfo", e.getMessage());
			logger.error("生成文档出错:",e);
		}
		return map;
	}
	//生成文档增量
	@RequestMapping("/makeContIncr") @ResponseBody
	public Map<String, Object> makeContIncr(HttpServletRequest request,HttpServletResponse response){
		return this.makeCont(request, response);
	}
		/*
		//构造模板树的数据
		List<Map<String, Object>> list=new ArrayList<Map<String, Object>>();
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("id", 1);
		map.put("text", "核心功能");
		map.put("state", "open");
		
		Map<String, Object> map2=new HashMap<String, Object>();
		map2.put("id", 11);
		map2.put("text", "栏目管理");
		Map<String, Object> map2url=new HashMap<String, Object>();
		map2url.put("url", request.getContextPath()+"/backstage/columnInfoAction/columnManage");
		map2.put("attributes", map2url);
		
		Map<String, Object> map3=new HashMap<String, Object>();
		map3.put("id", 12);
		map3.put("text", "模型管理");
		Map<String, Object> map3url=new HashMap<String, Object>();
		map3url.put("url", request.getContextPath()+"/backstage/channelInfoAction/channelManage");
		map3.put("attributes", map3url);
		
		List<Map<String, Object>> subList=new ArrayList<Map<String, Object>>();
		subList.add(map2);
		subList.add(map3);
		map.put("children", subList);
		list.add(map);
		return list;*/
		@RequestMapping("/getTpls")
		@ResponseBody
		public List<TreeDto> getTpls(HttpServletRequest request){
			List<TreeDto> treeDtos =staticService.getTpls(request);
			return treeDtos;
		}
}
