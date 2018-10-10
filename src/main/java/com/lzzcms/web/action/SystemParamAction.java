package com.lzzcms.web.action;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.lzzcms.model.SystemParam;
import com.lzzcms.service.ContentInfoService;
import com.lzzcms.service.SystemParamService;
import com.lzzcms.service.impl.ColumnInfoServiceImpl;

/**
 * @author zhao
 */
@Controller
public class SystemParamAction {
	private static Logger logger=Logger.getLogger(SystemParamAction.class);
	@Resource
	private SystemParamService systemParamService;

	public SystemParamService getSystemParamService() {
		return systemParamService;
	}

	public void setSystemParamService(SystemParamService systemParamService) {
		this.systemParamService = systemParamService;
	}
	@RequestMapping("/getSrcForCombobox") @ResponseBody
	public List<Map<String, Object>> getSrcForCombobox(HttpServletRequest request){
		String frompage=request.getParameter("frompage");
		List<Map<String, Object>>  list= systemParamService.getSrcForCombobox();
		if ("contM".equals(frompage)) {//内容管理页面
			Map<String, Object> map=new HashMap<String, Object>();
			map.put("id", "");
			map.put("come_from", "---所有---");
			list.add(0, map);
		}
		return list;
	}
	@RequestMapping("/getAuthorForCombobox") @ResponseBody
	public List<Map<String, Object>> getAuthorForCombobox(HttpServletRequest request){
		String frompage=request.getParameter("frompage");
		List<Map<String, Object>>  list= systemParamService.getAuthorForCombobox();
		if ("contM".equals(frompage)) {
			Map<String, Object> map=new HashMap<String, Object>();
			map.put("id", "");
			map.put("author_name", "---所有---");
			list.add(0, map);
		}	
		return list;
	}
	@RequestMapping("/getDefineFlagForCombobox") @ResponseBody
	public List<Map<String, Object>> getDefineFlagForCombobox(HttpServletRequest request){
		String frompage=request.getParameter("frompage");
		List<Map<String, Object>>  list= systemParamService.getDefineFlagForCombobox();
		if ("contM".equals(frompage)) {
			Map<String, Object> map=new HashMap<String, Object>();
			map.put("en_name", "");
			map.put("define_flag", "---所有---");
			list.add(0, map);
		}else {
			Map<String, Object> map=new HashMap<String, Object>();
			map.put("en_name", "");
			map.put("define_flag", "不使用自定义标记");
			list.add(0, map);
		}	
		return list;
	}
	@RequestMapping("/toPageCfg") 
	public String toPageCfg(Model model){
		List<SystemParam>  list= systemParamService.toPageCfg();
		for(SystemParam systemParam:list){
			model.addAttribute(systemParam.getParamName(), systemParam.getParamValue());
		}
		return "syscfg/pageCfg";
	}
	@RequestMapping(value="/updatePageCfg",method=RequestMethod.POST) @ResponseBody
	public Map<String, String> updatePageCfg( @RequestBody Map<String, String> map){
		Map<String, String> returnMap=new HashMap<String, String>();
		try {
			systemParamService.updatePageCfg(map);;
			returnMap.put("info", "ok");
		} catch (Exception e) {
			returnMap.put("info", "error");
			returnMap.put("errinfo", e.getMessage());
		}
	    return	returnMap;
	}
	@RequestMapping("/comboxCfg") 
	public String comboxCfg(){
		return "syscfg/comboxCfg";
	}
	@RequestMapping("/toGlobalCfg") 
	public String toGlobalCfg(Model model){
		return "global/globalCfg";
	}
	@RequestMapping(value="/trueAddGlobalCfg",method=RequestMethod.POST) @ResponseBody
	public Map<String, String> trueAddGlobalCfg( HttpServletRequest request){
		Map<String, String> returnMap=new HashMap<String, String>();
		try {
			int lastInsertId = systemParamService.trueAddGlobalCfg(request);
			returnMap.put("info", "ok");
			returnMap.put("lastInsertId", lastInsertId+"");
		} catch (Exception e) {
			returnMap.put("info", "error");
			returnMap.put("errinfo", e.getMessage());
			logger.info("增加全局变量出错:",e);
		}
		return	returnMap;
	}
	@RequestMapping("/getGlobalCfg") @ResponseBody
	public List<Map<String, Object>> getGlobalCfg(HttpServletRequest request){
		List<Map<String, Object>>  list= systemParamService.getGlobalCfg();
		return list;
	}
	@RequestMapping(value="/delGlobalCfg",method=RequestMethod.POST) @ResponseBody
	public Map<String, String> delGlobalCfg( HttpServletRequest request){
		Map<String, String> returnMap=new HashMap<String, String>();
		try {
			systemParamService.delGlobalCfg(request);;
			returnMap.put("info", "ok");
		} catch (Exception e) {
			returnMap.put("info", "error");
			returnMap.put("errinfo", e.getMessage());
			logger.error("删除全局变量出错:",e);
		}
		return	returnMap;
	}
}
