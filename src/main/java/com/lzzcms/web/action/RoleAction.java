package com.lzzcms.web.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.lzzcms.service.RoleService;

@Controller
public class RoleAction {
	@Resource
	private RoleService roleService ;
	@RequestMapping("/roleManage")
	public String toListTables(){
		return "role/roleManage";
	}
	@RequestMapping("/listRoles") @ResponseBody
	public List<Map<String, Object>> trueList(){
	    return 	roleService.trueList();
	}
	@RequestMapping("/getRightsByRoleId") @ResponseBody
	public List<Map<String, Object>> getRightsByRoleId(HttpServletRequest request){
		Integer roleId=Integer.valueOf(request.getParameter("roleId"));
		 	List<Map<String, Object>> list = roleService.getRights(roleId);
		 	return list;
	}
	@RequestMapping(value="/assignRights",method=RequestMethod.POST) @ResponseBody
	public Map<String, String> assignRights( @RequestBody Map<String, String> map,HttpServletRequest request){
		Map<String, String> returnMap=new HashMap<String, String>();
		try {
			roleService.assignRights(map);
			returnMap.put("info", "ok");
		} catch (Exception e) {
			returnMap.put("info", "error");
			returnMap.put("errinfo", e.getMessage());
		}
	    return	returnMap;
	}
	@RequestMapping("/trueAddRole") @ResponseBody
	public Map<String, Object> trueAddRole(HttpServletRequest request){
		 Map<String, Object> map=new HashMap<String, Object>();
		 String roleName = request.getParameter("roleName");
		 String roleDesc = request.getParameter("roleDesc");
		 try {
			roleService.trueAddRole(roleName,roleDesc);
			map.put("type", "info");
			map.put("info", "添加角色成功");
		} catch (Exception e) {
			map.put("type", "error");
			map.put("info", e.getMessage());
		}
		return map;
	}
	@RequestMapping("/deleteRoleById") @ResponseBody
	public Map<String, Object> deleteRoleById(HttpServletRequest request){
		Map<String, Object> map=new HashMap<String, Object>();
		String roleIds = request.getParameter("param");
		try {
			roleService.deleteRoleById(roleIds);
			map.put("type", "info");
			map.put("info", "删除角色成功");
		} catch (Exception e) {
			map.put("type", "error");
			map.put("info", e.getMessage());
		}
		return map;
	}
}
