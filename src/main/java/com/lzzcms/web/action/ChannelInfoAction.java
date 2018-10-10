package com.lzzcms.web.action;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.lzzcms.dto.GridDto;
import com.lzzcms.model.ChannelInfo;
import com.lzzcms.service.ChannelInfoService;

/**
 * @author zhao
 */
@Controller
public class ChannelInfoAction {
	private Logger logger=Logger.getLogger(ChannelInfoAction.class);
	@Resource
	private ChannelInfoService channelInfoService;
	public ChannelInfoService getChannelInfoService() {
		return channelInfoService;
	}
	public void setChannelInfoService(ChannelInfoService channelInfoService) {
		this.channelInfoService = channelInfoService;
	}
	@RequestMapping("/channelManage")
	public String channelManage(){
		return "chl/channelManage";
	}
	@RequestMapping("/listChannels")
	@ResponseBody
	public GridDto<Map<String, Object>> listChannels(HttpServletRequest request){
		Map<String, Object> paramMap=new HashMap<String,Object>();
		paramMap.put("sortField", request.getParameter("sort"));
		paramMap.put("sortDirection", request.getParameter("order"));
		List<Map<String, Object>> list=channelInfoService.trueList(paramMap);
		long totalCount=channelInfoService.getTotalCount();
		GridDto<Map<String, Object>> gridDto=new GridDto<Map<String, Object>>();
		gridDto.setTotal(totalCount);
		gridDto.setRows(list);
		return gridDto;
	}
	@RequestMapping("/getForCombobox")
	@ResponseBody
	public  List<Map<String, Object>> getForCombobox(HttpServletRequest request){
		String page=request.getParameter("page");
		 List<Map<String, Object>> list=channelInfoService.getForCombobox();
		 if ("contM".equals(page)) {//来自contentManage.jsp否则来自toAdd.jsp/addTopCln.jsp
			 Map<String, Object> map=new HashMap<String, Object>();
			 map.put("id", "");
			 map.put("channelname", "---所有---");
			 list.add(0, map);
		}
		return list;
	}
	//to增加频道页面
	@RequestMapping(value="/toAddChlInfo") 
	public String toAddChlInfoIframe(Map<String,Object> map,HttpServletRequest request) throws IllegalAccessException, InvocationTargetException{
		Map<String, Object> resultMap=channelInfoService.toAddChannelInfo();
		map.put("ret", resultMap);
		return "chl/toAddChl";
	}
	//提交增加频道
	@RequestMapping(value="/trueAddChannelInfo",method=RequestMethod.POST) @ResponseBody
	public Map<String, String> trueAddChannelInfo(@RequestBody ChannelInfo channelInfo){
		Map<String, String> map=new HashMap<String, String>();
		try {
			channelInfoService.trueAddChannelInfo(channelInfo);
			map.put("info", "ok");
			return map;
		} catch (Exception e) {
			logger.error("提交增加频道出错:",e);
			map.put("info", e.getMessage());
		}
		return  map;
	}
	@RequestMapping(value="/toEditChannelById") 
	public String toEditChannelByIdIframe(Map<String, Object> map,HttpServletRequest request){
			String chlid = request.getParameter("chlid");
			ChannelInfo channelById = channelInfoService.getChannelById(Integer.valueOf(chlid));
			map.put("channelInfo", channelById);
			return "chl/editChannelInfo";
	}
	
	@RequestMapping(value="/trueEditChannelInfo") @ResponseBody
	public  Map<String, String> trueEditChannelInfo(ChannelInfo channelInfo){
		Map<String, String> map=new HashMap<String, String>();
		try {
			channelInfoService.trueEditChannelInfo(channelInfo);
			map.put("info", "ok");
		} catch (Exception e) {
			logger.error("编辑频道出错:",e);
			map.put("info", "error");
			map.put("errinfo", e.getMessage());
		}
		return  map;
	}
	@RequestMapping(value="/getChannelExtralField",method=RequestMethod.POST) @ResponseBody
	public  List<Map<String, Object>> getChannelExtralField(HttpServletRequest request){
		String channelId = request.getParameter("chlid");
		List<Map<String, Object>> list=	channelInfoService.channelAdvancedCfg(channelId);
		if (list.isEmpty()) {
			Map<String, Object> map=new HashMap<String, Object>();
			map.put("info", "hasNo");
			list.add(map);
		}
		return list;
	}
	@RequestMapping(value="/addFieldDia",method=RequestMethod.POST) @ResponseBody
	public   Map<String, String> addFieldDia(@RequestParam String formData){
		Map<String, String> map=new HashMap<String, String>();
		try {
			Gson gson=new Gson();
			Map<String,String> paramsMap =gson.fromJson(formData, Map.class);
			channelInfoService.addFieldDia(paramsMap);
			map.put("info", "ok");
			return map;
		} catch (Exception e) {
			logger.error("增加字段出错:",e);
			map.put("info", "error");
			map.put("errinfo", e.getMessage());
		}
		return  map;
	}
	@RequestMapping(value="/deleteChl",method=RequestMethod.POST) @ResponseBody
	public  Map<String, Object> deleteChl(HttpServletRequest request){
		String chls = request.getParameter("chls");
		Gson gson=new Gson();
		List<Map<String, Object>> list=gson.fromJson(chls, List.class);
		Map<String, Object> retMap=new HashMap<>();
		try {
			channelInfoService.deleteChl(list,request.getSession().getServletContext());
			retMap.put("status", "success");
		} catch (Exception e) {
			logger.error("删除频道出错:",e);
			retMap.put("status", "error");
			retMap.put("info", e.getMessage());
		}
		return retMap;
	}
}
