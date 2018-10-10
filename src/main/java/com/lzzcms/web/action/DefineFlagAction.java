package com.lzzcms.web.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.lzzcms.dto.GridDto;
import com.lzzcms.model.AdminInfo;
import com.lzzcms.service.AdminInfoService;
import com.lzzcms.service.AuthorService;
import com.lzzcms.service.DefineFlagService;
import com.lzzcms.utils.PageContext;
import com.lzzcms.utils.PageUtil;

@Controller
public class DefineFlagAction {
	private Logger logger=Logger.getLogger(DefineFlagAction.class);
	@Resource
	private DefineFlagService defineFlagService;
	@RequestMapping("/listDefineFlags") @ResponseBody
	public  GridDto<Map<String, Object>> listDefineFlags(HttpServletRequest request){
		GridDto<Map<String, Object>> gridDto=new GridDto<Map<String, Object>>();
		 List<Map<String, Object>>  list=defineFlagService.listDefineFlags(request);
		 Long count = defineFlagService.getDefineFlagCount();
		 gridDto.setTotal(count);
		 gridDto.setRows(list);
		 return gridDto;
	}
	@RequestMapping("/addDefineFlag") @ResponseBody
	public  Map<String, Object> addDefineFlag(HttpServletRequest request){
		Map<String, Object> retMap =new HashMap<String, Object>();
		String param = request.getParameter("param");
		Gson gson=new Gson();
		Map<String, Object> map = gson.fromJson(param, Map.class);
		try {
			defineFlagService.addDefineFlag(map);
			retMap.put("info", "增加成功");
			retMap.put("type", "info");
		} catch (Exception e) {
			logger.error(e);
			retMap.put("info", "增加失败");
			retMap.put("type", "error");
		}
		return retMap;
	}
	@RequestMapping("/deleteDefineFlag") @ResponseBody
	public  Map<String, Object> deleteDefineFlag(HttpServletRequest request){
		Map<String, Object> retMap =new HashMap<String, Object>();
		String ids=request.getParameter("param");
		try {
			defineFlagService.deleteDefineFlag(ids);
			retMap.put("info", "删除成功");
			retMap.put("type", "info");
		} catch (Exception e) {
			logger.error(e);
			retMap.put("info", "删除失败");
			retMap.put("type", "error");
		}
		return retMap;
	}
}
