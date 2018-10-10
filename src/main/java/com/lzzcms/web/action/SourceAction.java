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
import com.lzzcms.service.SourceService;
import com.lzzcms.utils.PageContext;
import com.lzzcms.utils.PageUtil;

@Controller
public class SourceAction {
	private Logger logger=Logger.getLogger(SourceAction.class);
	@Resource
	private SourceService sourceService;
	@RequestMapping("/listSources") @ResponseBody
	public  GridDto<Map<String, Object>> listSources(HttpServletRequest request){
		GridDto<Map<String, Object>> gridDto=new GridDto<Map<String, Object>>();
		 List<Map<String, Object>>  list=sourceService.listSources(request);
		 Long count = sourceService.getSourceCount();
		 gridDto.setTotal(count);
		 gridDto.setRows(list);
		 return gridDto;
	}
	@RequestMapping("/addSource") @ResponseBody
	public  Map<String, Object> addSource(HttpServletRequest request){
		Map<String, Object> retMap =new HashMap<String, Object>();
		String param = request.getParameter("param");
		Gson gson=new Gson();
		Map<String, Object> map = gson.fromJson(param, Map.class);
		try {
			sourceService.addSource(map);
			retMap.put("info", "增加成功");
			retMap.put("type", "info");
		} catch (Exception e) {
			logger.error(e);
			retMap.put("info", "增加失败");
			retMap.put("type", "error");
		}
		return retMap;
	}
	@RequestMapping("/deleteSource") @ResponseBody
	public  Map<String, Object> deleteSource(HttpServletRequest request){
		Map<String, Object> retMap =new HashMap<String, Object>();
		String ids=request.getParameter("param");
		try {
			sourceService.deleteSource(ids);
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
