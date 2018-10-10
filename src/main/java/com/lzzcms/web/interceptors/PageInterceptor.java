package com.lzzcms.web.interceptors;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lzzcms.listeners.SpringBeanFactory;
import com.lzzcms.model.AdminInfo;
import com.lzzcms.model.ExecLog;
import com.lzzcms.model.RightInfo;
import com.lzzcms.service.ExecLogService;
import com.lzzcms.utils.LzzConstants;
import com.lzzcms.utils.LzzcmsUtils;
import com.lzzcms.utils.PageContext;
import com.lzzcms.utils.PageUtil;


public class PageInterceptor implements HandlerInterceptor {
	private Logger logger=Logger.getLogger(PageInterceptor.class);
	@Override
	public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception {
		String page = request.getParameter("page");
		String rows = request.getParameter("rows");
		PageUtil pageUtil=new PageUtil();
		if (StringUtils.isNotBlank(page)&&StringUtils.isNotBlank(rows)) {
			pageUtil.setPageNow(Integer.valueOf(page).intValue());
			pageUtil.setPageSize(Integer.valueOf(rows).intValue());
			pageUtil.setNeedPage(true);
		}else {
			pageUtil.setNeedPage(false);
		}
		PageContext.setPageUtil(pageUtil);
		/**
		 * uri:/lzzcms/backstage/systemParamAction/getDefineFlagForCombobox/contM
		   url:http://localhost:8080/lzzcms/backstage/systemParamAction/getDefineFlagForCombobox/contM
		   bestPattern:/systemParamAction/getDefineFlagForCombobox/{frompage}
		   contextPath:/lzzcms
		   scheme:http
			serverName:localhost
			serverPort:8080
		 */
//		String servletPath=request.getServletPath();// /backstage
//		String uri=request.getRequestURI();
//		String url=request.getRequestURL().toString();
		String scheme = request.getScheme();
		String serverName = request.getServerName();
		int serverPort = request.getServerPort();
		String contextPath = request.getContextPath();
//		System.out.println("uri:"+uri);
//		System.out.println("url:"+url);
//		System.out.println("bestPattern:"+bestPattern);
//		logger.info("contextPath:"+contextPath);
//		System.out.println("scheme:"+scheme);
//		System.out.println("serverName:"+serverName);
//		System.out.println("serverPort:"+serverPort);
		String basePath=null;
		if (serverPort==80) {
			basePath=scheme+"://"+serverName+contextPath;
		}else{
			basePath=scheme+"://"+serverName+":"+serverPort+contextPath;
		}
		//设置定时任务用到的basePath,定时任务里面没法获取到request
		String basePathForTask = LzzConstants.getInstance().getBasePathForTask();
		if (StringUtils.isBlank(basePathForTask)) {
			LzzConstants.getInstance().setBasePathForTask(basePath);
		}
		String str = LzzcmsUtils.getBasePath(request);
		if (StringUtils.isBlank(str)) {
			request.getSession().setAttribute("basePath", basePath);
		}
		return true;
	}
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		 
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		PageContext.removePageUtil();
	}
	public static void main(String[] args) {
		for (int i = 1; i <= 59; i++) {
			//System.out.println("1左移"+i+"位:"+(1L<<i));
		}
	}
}
