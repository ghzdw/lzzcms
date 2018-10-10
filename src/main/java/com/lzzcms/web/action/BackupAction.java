package com.lzzcms.web.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lzzcms.model.AdminInfo;
import com.lzzcms.service.BackupService;

@Controller
public class BackupAction {
	@Resource
	private BackupService dbService;
	@RequestMapping("/toBackup")
	public String toBackup(){
		return "backup/backup";
	}
	@RequestMapping("/listBackUps") @ResponseBody
	public List<Map<String, Object>> listBackUps(){
	    return 	dbService.listBackUps();
	}
	@RequestMapping(value="/backUp",method=RequestMethod.POST) @ResponseBody
	public Map<String, String> backUp(HttpServletRequest request){
	    String ret =null; 
		Map<String, String> map=new HashMap<String, String>();
		try {
			AdminInfo adminInfo=(AdminInfo) request.getSession().getAttribute("admin");
			ret=dbService.backUp(request.getSession().getServletContext(),adminInfo.getRealName());
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
	@RequestMapping(value="/backTo",method=RequestMethod.POST) @ResponseBody
	public Map<String, String> backTo(HttpServletRequest request){
		String ret =null; 
		Map<String, String> map=new HashMap<String, String>();
		String backupName = request.getParameter("backupName");
		try {
			ret=dbService.backTo(request.getSession().getServletContext(),backupName);
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
}
