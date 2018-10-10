package com.lzzcms.tasks;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.lzzcms.components.HttpClientPool;
import com.lzzcms.dao.SpiderDao;
import com.lzzcms.service.SpiderService;
import com.lzzcms.utils.LzzConstants;

@Component
public class SpiderTask {
	private Logger logger=Logger.getLogger(SpiderTask.class);
	@Resource
	private SpiderService spiderService;
	@Resource
	private SpiderDao spiderDao;
	@Resource
	private HttpClientPool pool;
	public void spiderAuto (){
		long startTime=System.currentTimeMillis();
    	String basePathForTask = LzzConstants.getInstance().getBasePathForTask();
    	if (StringUtils.isBlank(basePathForTask)) {
    		logger.info("basePathForTask为空,定时任务不执行");
    	}else{
    		String url = basePathForTask+
    				LzzConstants.getInstance().getBackServletPath()+
    				"/crawlTask";
    		logger.info("定时任务开始执行,请求aciton路径:"+url);
    		pool.executeGet(url, null);
    	}
        long endTime=System.currentTimeMillis();
		logger.info("定时任务执行完成，耗时:"+(endTime-startTime)/1000+"秒");
	}
}
