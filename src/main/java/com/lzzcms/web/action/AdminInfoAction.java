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

import com.lzzcms.dto.GridDto;
import com.lzzcms.model.AdminInfo;
import com.lzzcms.service.AdminInfoService;
import com.lzzcms.utils.LzzcmsUtils;
import com.lzzcms.utils.PageContext;
import com.lzzcms.utils.PageUtil;

@Controller
public class AdminInfoAction {
	private Logger logger=Logger.getLogger(AdminInfoAction.class);
	@Resource
	private AdminInfoService adminInfoService;
	
	public AdminInfoService getAdminInfoService() {
		return adminInfoService;
	}
	public void setAdminInfoService(AdminInfoService adminInfoService) {
		this.adminInfoService = adminInfoService;
	}
	@RequestMapping("/")
	public String login(HttpServletRequest request){
		String lzzcms_u ="";
		String lzzcms_p="";
		Cookie[] cookies = request.getCookies();
		if (cookies!=null) {
			for(Cookie cookie:cookies){
				if ("lzzcms_u".equals(cookie.getName())) {
					lzzcms_u=cookie.getValue();
				}
				if ("lzzcms_p".equals(cookie.getName())) {
					lzzcms_p=cookie.getValue();
				}
			}
		}
		request.setAttribute("lzzcms_u", lzzcms_u);
		request.setAttribute("lzzcms_p", lzzcms_p);
		return "login";
	}
	@RequestMapping("/trueLogin")@ResponseBody
	public Map<String, String> trueLogin(HttpServletRequest request,HttpServletResponse response){
		Map<String, String> returnMap=new HashMap<String, String>();
		String uname = request.getParameter("uname");
		String pwd = request.getParameter("pwd");
		String remember = request.getParameter("remember");
		String code = request.getParameter("code");
		String flag = request.getParameter("flag");//自动登录标记 auto
		String orgCode = (String) request.getSession().getAttribute("code");
		if (!"auto".equals(flag)) {//判断验证码
			if (StringUtils.isNotBlank(code)) {
				if (!code.equalsIgnoreCase(orgCode)) {
					returnMap.put("info", "error");
					returnMap.put("errinfo", "验证码不正确!");
				    return	returnMap;
				}
			}else {
				returnMap.put("info", "error");
				returnMap.put("errinfo", "请填写验证码!");
				 return	returnMap;
			}
		}else {
			pwd=LzzcmsUtils.getDecryptResult(pwd);
		}
		try {
			AdminInfo admin=adminInfoService.trueLogin(uname,pwd);
			if (admin!=null) {
				int maxGroup=adminInfoService.getMaxGroup();
				long[] rightSum=new long[maxGroup+1];//权限组〉=0,不限制从哪开始
				admin.setRightSum(rightSum);
				admin.calculateRightSum();
				returnMap.put("info", "ok");
				request.getSession().setAttribute("admin", admin);
				if (StringUtils.isNotBlank(remember)) {//写入cookie，默认保存七天
		             this.returnCookie("lzzcms_u", uname, 7*24*60*60, response);//秒  
		             this.returnCookie("lzzcms_p", admin.getpWord(), 7*24*60*60, response);
				}
			}else{
				returnMap.put("info", "error");
				returnMap.put("errinfo", "请填写正确的用户名或密码!");
			}
		} catch (Exception e) {
			returnMap.put("info", "error");
			returnMap.put("errinfo", e.getMessage());
		}
	    return	returnMap;
	}
	@RequestMapping("/adminManage")
	public String toList(){
		return "admin/adminManage";
	}
	@RequestMapping("/toIndex")
	public String toIndex(){
		return "index";
	}
	@RequestMapping("/listAdmins") @ResponseBody
	public GridDto<Map<String, Object>> listAdmins(){
		GridDto<Map<String, Object>> gridDto=new GridDto<Map<String, Object>>();
	     try {
			 List<Map<String, Object>> list = adminInfoService.trueList();
			 //得到数量
			 PageUtil pageUtil = PageContext.getPageUtil();
			 pageUtil.setNeedPage(false);
			 List<Map<String, Object>>  countList=adminInfoService.trueList();
			 int count = Integer.valueOf(countList.get(0).get("count").toString());
			 gridDto.setTotal(count);
			 gridDto.setRows(list);
		} catch (NumberFormatException e) {
			logger.info(e);
		}
	     return gridDto;
	}
	@RequestMapping(value="/assignRoles",method=RequestMethod.POST) @ResponseBody
	public Map<String, String> assignRoles( @RequestBody Map<String, String> map,HttpServletRequest request){
		Map<String, String> returnMap=new HashMap<String, String>();
		try {
			adminInfoService.assignRoles(map);;
			returnMap.put("info", "ok");
		} catch (Exception e) {
			returnMap.put("info", "error");
			returnMap.put("errinfo", e.getMessage());
		}
	    return	returnMap;
	}
	@RequestMapping(value="/trueAddAdmin",method=RequestMethod.POST) @ResponseBody
	public Map<String, String> trueAddAdmin( @RequestBody Map<String, String> map){
		Map<String, String> returnMap=new HashMap<String, String>();
		try {
			adminInfoService.trueAddAdmin(map);;
			returnMap.put("info", "ok");
		} catch (Exception e) {
			returnMap.put("info", "error");
			returnMap.put("errinfo", e.getMessage());
		}
		return	returnMap;
	}
	@RequestMapping("/getRolesByAdminId") @ResponseBody
	public List<Map<String, Object>> getRoles(HttpServletRequest request){
		Integer adminId=Integer.valueOf(request.getParameter("adminId"));
         List<Map<String, Object>> list = adminInfoService.getRoles(adminId);
		 return list;
	}
	@RequestMapping(value="/deleteAdmin") @ResponseBody
	public Map<String, String> deleteAdmin(HttpServletRequest request){
		Map<String, String> returnMap=new HashMap<String, String>();
		try {
			String param = request.getParameter("param");
			String ret=adminInfoService.deleteAdmin(param);
			if (ret!=null) {
				returnMap.put("info", "error");
				returnMap.put("errinfo", ret);
			}else {
				returnMap.put("info", "ok");
			}
		} catch (Exception e) {
			returnMap.put("info", "error");
			returnMap.put("errinfo", e.getMessage());
		}
		return	returnMap;
	}
	@RequestMapping(value="/trueUpAdmin",method=RequestMethod.POST) @ResponseBody
	public Map<String, String> trueUpAdmin( @RequestBody Map<String, String> map){
		Map<String, String> returnMap=new HashMap<String, String>();
		try {
			returnMap=adminInfoService.trueUpAdmin(map);;
		} catch (Exception e) {
			returnMap.put("type", "error");
			returnMap.put("info", e.getMessage());
		}
		return	returnMap;
	}
	
	@RequestMapping("/logout") 
	public String logout(HttpSession session,HttpServletRequest request
			,HttpServletResponse response){
         this.returnCookie("lzzcms_u",null,0,response);
         this.returnCookie("lzzcms_p",null,0,response);
		session.invalidate();
		request.setAttribute("fromLogout", "fromLogout");
		return "login";
	}
	private void returnCookie(String cookieName, String cookieValue, int seconds
			, HttpServletResponse response) {
		 Cookie c = new Cookie(cookieName, cookieValue);  
		 c.setMaxAge(seconds);
		 c.setPath("/");
		 response.addCookie(c); 
	}
	@RequestMapping("/fromInterceptor") 
	public String fromInterceptor(HttpServletRequest request){
		String flag = request.getParameter("flag");
		if ("noLogin".equals(flag)) {
			request.setAttribute("to", request.getParameter("to"));
			return "login";
		}else if ("noAuth".equals(flag)) {
			request.setAttribute("tip", "非法访问！你的行为已被记录");
			return "error";
		}else {//noUrl
			request.setAttribute("tip", "访问地址不存在");
			return "error";
		}
	}
	@RequestMapping("/error/{resCode}") 
	public String fromInterceptor(@PathVariable String resCode,HttpServletRequest request){
	   if ("404".equals(resCode)) {
			request.setAttribute("tip", "访问地址不存在");
		}else if("500".equals(resCode)){
			request.setAttribute("tip", "服务器内部错误");
		}
			return "error";
	}
}
