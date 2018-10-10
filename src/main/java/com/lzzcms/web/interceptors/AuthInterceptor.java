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


public class AuthInterceptor implements HandlerInterceptor {
	private Logger logger=Logger.getLogger(AuthInterceptor.class);
	@Override
	public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception {
		String bestPattern=(String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		AdminInfo adminInfo = (AdminInfo) request.getSession().getAttribute("admin");
		//权限控制
		String crtUrl=bestPattern.substring(1);
		Map<String, RightInfo> rightsMap=(Map<String, RightInfo>) request.getSession().getServletContext().getAttribute("all_rights_map");
		RightInfo right=rightsMap.get(crtUrl);
		String str = LzzcmsUtils.getBasePath(request)+LzzConstants.getInstance().getBackServletPath();
		if (right != null) {
			if (right.getCommon()) {// 公共资源
				return true;
			} else {
				if (adminInfo == null) {// 未登录
					String toLogin=str + "/";
					response.sendRedirect(str + "/fromInterceptor?flag=noLogin&to=" + toLogin);
					return false;
				} else {// 已登录
					if (adminInfo.isSuperAdmin()) {// 超级管理员:角色值是-1
						return true;
					} else {
						if (adminInfo.hasRight(right)) {// 有权限
							return true;
						} else {// 没有权限,非法访问
							if ("yes".equals(right.getCanAssign())) {
								ExecLogService execLogService = SpringBeanFactory.getBean("execLogServiceImpl", ExecLogService.class);
								ExecLog execLog=new ExecLog();
								execLog.setExecType("非法访问");
								execLog.setExecUrl(crtUrl);
								execLog.setExecUrlDesc(right.getRightName());
								execLog.setAdminInfo(adminInfo);
								execLogService.saveOrUpdate(execLog);
								response.sendRedirect(str + "/fromInterceptor?flag=noAuth");
								return false;
							}else {//不可分配的权限要通过
								return true;
							}
							
						}
					}
				}
			}
		} else {//LzzConstants.URL_PATTERN下的404
			logger.info("请求不存在:"+crtUrl);
			response.sendRedirect(str + "/fromInterceptor?flag=noUrl");
			return false;
		}
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
	}
}
