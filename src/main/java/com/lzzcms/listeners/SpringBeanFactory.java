package com.lzzcms.listeners;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import com.lzzcms.model.RightInfo;
import com.lzzcms.service.RightInfoService;
import com.lzzcms.service.SystemParamService;
import com.lzzcms.utils.LzzConstants;
/**
 * 初始化spring的applicationcontext
 * @author zhao
 */
@Component  //只有被spring管理了，才能监听到spring的事件，否则只能监听到servlet事件了
public class SpringBeanFactory implements ApplicationListener<ContextRefreshedEvent>,ServletContextAware{//ServletContextListener
	private static ApplicationContext ac=null;
	private static ServletContext sc=null;//servlet初始化完成，spring初始化完成，springmvc初始化完成
	private static Logger logger=Logger.getLogger(SpringBeanFactory.class);
	@Override
	//因为先初始化spring的容器，再springmvc,springmvc的容器[ac]初始化完成时也会调用这个方法
	public void onApplicationEvent(ContextRefreshedEvent event) {
		logger.info("spring初始化完成事件被触发");
		if (ac==null) {//springmvc的容器初始化完成时就不用做了
			ac=event.getApplicationContext();
			if (sc!=null) {
				//设置权限
				RightInfoService rightInfoService = SpringBeanFactory.getBean("rightInfoServiceImpl", RightInfoService.class);
				Map<String, Object> rightMap=new HashMap<String, Object>();
				List<Map<String, Object>>  list=rightInfoService.getRightsForSc();
				for (Iterator<Map<String, Object>> iterator = list.iterator(); iterator.hasNext();) {
					Map<String, Object> map =iterator.next();
					RightInfo rightInfo=new RightInfo();
					String rightUrl = String.valueOf(map.get("righturl"));
					rightInfo.setRightUrl(rightUrl);
					rightInfo.setRightName(String.valueOf(map.get("rightname")));
					rightInfo.setCanAssign(String.valueOf(map.get("canassign")));
					rightInfo.setRightGroup(Integer.valueOf(map.get("rightgroup").toString()));
					rightInfo.setRightCode(Long.valueOf(map.get("rightcode").toString()));
					rightInfo.setCommon(Boolean.valueOf(map.get("common").toString()));
					rightMap.put(rightUrl, rightInfo);
				}
				sc.setAttribute("all_rights_map", rightMap);
				//可以在这里设置一些常量
				SystemParamService systemParamService=SpringBeanFactory.getBean("systemParamServiceImpl", SystemParamService.class);
				List<Map<String, Object>> initContants = systemParamService.getInitContants();
				for(Map<String, Object> oneConstant:initContants){
					String oneConstantName = oneConstant.get("paramname").toString();
					String oneConstantValue = oneConstant.get("paramvalue").toString();
					if ("servletPath".equals(oneConstantName)) {
						LzzConstants.getInstance().setBackServletPath(oneConstantValue);
					}else if ("dbName".equals(oneConstantName)) {
						LzzConstants.getInstance().setDbName(oneConstantValue);
					}
				}
			}
		}
	}
	public static <T> T  getBean(String beanId,Class<T> clazz){
		return ac.getBean(beanId, clazz);
	}
	@Override
	public void setServletContext(ServletContext servletContext) {
		logger.info("触发servlet初始化完成事件");
		sc=servletContext;
	}
}
