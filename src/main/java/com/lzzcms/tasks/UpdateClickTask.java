package com.lzzcms.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.lzzcms.dao.ContentInfoDao;
import com.lzzcms.dao.SpiderDao;
import com.lzzcms.service.SpiderService;
import com.lzzcms.utils.LzzConstants;
import com.lzzcms.utils.LzzcmsUtils;

@Component
public class UpdateClickTask {
	private Logger logger=Logger.getLogger(UpdateClickTask.class);
	@Resource
	private ContentInfoDao contentInfoDao;
	@Resource
	private JedisPool jedisPool;
	public void updateClick (){
		long startTime=System.currentTimeMillis();
		logger.info("从redis中获取文章点击量更新到数据库的定时任务开始执行...");
		Jedis jedis=null;
        try {
        	jedis=jedisPool.getResource();
        	Map<String, String> idClickMap = jedis.hgetAll("clickmap");//1(id)--99(click)，2-800
        	Map<String, String> idClickMapTmp = jedis.hgetAll("clickmap");//1(id)--99(click)
        	List<String> idList=new ArrayList<String>();
        	if (idClickMap!=null&&!idClickMap.isEmpty()) {//redis里面的clickmap这个hash有值时
        		Set<Entry<String, String>> entrySet = idClickMap.entrySet();
            	for (Iterator<Entry<String, String>> iterator = entrySet.iterator();iterator.hasNext();) {
    				Entry<String, String> entry = iterator.next();
    				idList.add(entry.getKey());
    				idClickMapTmp.put(entry.getKey(), entry.getValue());
    			}
        		//得到数据库中所有的id现在的click
        		StringBuffer sb=new StringBuffer();
            	sb.append(" select comm_id,comm_click from lzz_commoncontent where comm_id in (:ids) ");
            	Map<String, Object> idsMap=new HashMap<String, Object>();
            	idsMap.put("ids", idList);
            	List<Map<String, Object>> orignalList = contentInfoDao.selectByIn(sb.toString(), idsMap);
            	for(Map<String, Object> oneMap:orignalList){
            		int orignalClick = Integer.valueOf(oneMap.get("comm_click").toString());
            		Integer redisClick = Integer.valueOf(idClickMap.get(oneMap.get("comm_id").toString()));
					if (redisClick<=orignalClick) {//以免小于更新错了
						idClickMapTmp.remove(oneMap.get("comm_id").toString());
					}
            	}
            	//使用idClickMapTmp(真正需要更新的id_click组成的map)
            	List<Object[]> paramList=new ArrayList<Object[]>();
            	Set<Entry<String, String>> entrySetTmp = idClickMapTmp.entrySet();
            	for (Iterator<Entry<String, String>> iterator = entrySetTmp.iterator();iterator.hasNext();) {
    				Entry<String, String> entry = iterator.next();
    				Object[] objects=new Object[2];
    				objects[0]=entry.getValue();
    				objects[1]=entry.getKey();
    				paramList.add(objects);
    			}
            	sb.setLength(0);
            	sb.append("update lzz_commoncontent set comm_click=? where comm_id=?");
            	contentInfoDao.batchExecuteSql(sb.toString(), paramList);
            	for(String redisHashKey:idList){//删除redis里面所有的comm_id以及其值
            		jedis.hdel("clickmap", redisHashKey);
            	}
			}
		} catch (Exception e) {
			logger.error("从redis中获取文章点击量更新到数据库定时任务出错:", e);
		}finally{
			if (jedis!=null) {
				jedis.close();
			}
		}
        long endTime=System.currentTimeMillis();
		logger.info("定时任务执行完成，耗时:"+(endTime-startTime)/1000+"秒");
	}
}
