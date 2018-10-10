 package com.lzzcms.tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.annotations.Where;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.lzzcms.dao.ColumnInfoDao;
import com.lzzcms.dao.ContentInfoDao;
import com.lzzcms.listeners.SpringBeanFactory;
import com.lzzcms.model.ContRedisVo;
import com.lzzcms.service.impl.StaticServiceImpl;
import com.lzzcms.utils.SerializationUtil;

/**
 取出相关文章，只在内容页使用
 */
public class Relations extends SimpleTagSupport{
	private Logger logger=Logger.getLogger(SimpleTagSupport.class);
	private String splitTag="@zdw@";
	private Integer count; 

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	@Override
	public void doTag() throws JspException, IOException {
		ContentInfoDao contentInfoDao = SpringBeanFactory.getBean("contentInfoDaoImpl", ContentInfoDao.class);
		PageContext jspContext = (PageContext)getJspContext();
		int s =  (int) jspContext.getAttributesScope("onecont");
		Map<String, Object> map  =null;
		if (s!=0) {
			map=(Map<String, Object>) jspContext.getAttribute("onecont",s);
		}
		if (map==null) {
			return ;
		}
		String comm_keywords =map.get("comm_keywords")==null?"":map.get("comm_keywords").toString();
		if (StringUtils.isBlank(comm_keywords)) {
			getJspContext().getOut().write("");
		}else {
			//最终相似的doc:list.add(0,"0.4@zdw@http@zdw@xxx");
			List<String> finalList=new LinkedList<String>();
			int scope =  (int) getJspContext().getAttributesScope("basePath");//拦截器里在request中设置的
			String basePath = (String)getJspContext().getAttribute("basePath", scope);
			comm_keywords=comm_keywords.toLowerCase();
			//得到其他doc列表
			getFinalList(map.get("comm_id").toString(),comm_keywords,basePath,finalList);
			int finalListSize = finalList.size();
			finalListSize=finalListSize>count?count:finalListSize;
			finalList=finalList.subList(0, finalListSize);
			for (int i = 0; i <finalListSize; i++) {//"0.4@zdw@http://...s/...@zdw@xxx@zdw@1"
				String oneRelation =finalList.get(i)+splitTag+(i+1);
				getJspContext().setAttribute("relation",oneRelation);
				//if标签使用
				String[] split = oneRelation.split("@zdw@");
				getJspContext().setAttribute("docurl",split[1]);
				getJspContext().setAttribute("comm_title",split[2]);
				getJspContext().setAttribute("index",split[3]);
				getJspBody().invoke(null);//调用标签体
			}
		}
		
	}
	/*
	 * 执行到这里的时候：redis里面是所有的doc，这里需要排除掉本身id,和里面htmlpath为空的,
	 * 每一个文章都要比较总文章个数的次数
	 */
	private void getFinalList(String id,String comm_keywords, String basePath,
			List<String> finalList) {
		Jedis jedis=null;
        try {
        	JedisPool jedisPool= SpringBeanFactory.getBean("jedisPool", JedisPool.class);
        	jedis=jedisPool.getResource();
        	//allMap:comm_id vo,每一个Entry对应一个vo，即返回的Map<String, Object>
        	Map<byte[], byte[]> allMap = jedis.hgetAll("relation_use_map".getBytes("utf-8"));
        	//改为随机取几篇吧，不然太慢了
        	int size = allMap.size();
        	int cnt=size>count?count:size;
        	List<Integer> relationsList=new ArrayList<>();
        	Random random=new Random();
        	for(int i=0;i<cnt;i++){
        		relationsList.add(random.nextInt(size));
        	}
        	Set<Entry<byte[], byte[]>> entrySet = allMap.entrySet();
        	int index=0;
        	for (Iterator<Entry<byte[], byte[]>> iterator = entrySet.iterator(); iterator.hasNext();) {
        		if (finalList.size()==cnt) {
					break;
				}
				Entry<byte[], byte[]> entry =iterator.next();
				if (relationsList.contains(index)) {
					ContRedisVo vo=(ContRedisVo) SerializationUtil.deserialize(entry.getValue()); 
					finalList.add("0.0"+splitTag+basePath+"/"+vo.getComm_htmlpath()+splitTag+vo.getComm_title());
				}
				index++;
//				String crtId = new String(entry.getKey(),"utf-8");
//				if (!crtId.equals(id)) {//排除掉自己
//					ContRedisVo vo=(ContRedisVo) SerializationUtil.deserialize(entry.getValue()); 
//					if (StringUtils.isNotBlank(vo.getComm_htmlpath())) {//排除掉htmlpath为空的
//						double similar = getSimilar(comm_keywords,vo.getComm_keywords().toLowerCase());
//						updateFinalList(finalList,similar,basePath+"/"+vo.getComm_htmlpath()
//								,vo.getComm_title());
//					}
//				}
			}
		} catch (Exception e) {
			logger.error("计算关联文章出错:", e);
		}finally{
			if (jedis!=null) {
				jedis.close();
			}
		}
	}

	// list.add(0,"0.4@zdw@http@zdw@xxx");
	private void updateFinalList(List<String> finalList, double similar
			,String comm_htmlpath,String comm_title) {
		int size = finalList.size();
		size=size>count?count:size;
		boolean addFlag=false;
		for (int i = 0; i <size; i++) {
			String str = finalList.get(i);
			String[] split = str.split(splitTag);
			Double similarI=Double.valueOf(split[0]);//获取当前条目的相似度
			if (similar>similarI) {//加到当前条目的位置，所有的都后移 了
				finalList.add(i, similar+splitTag+comm_htmlpath+splitTag+comm_title);
				addFlag=true;
				break;
			}
		}
		if (!addFlag) {//加在最后
			finalList.add(similar+splitTag+comm_htmlpath+splitTag+comm_title);
		}
	}

	private double getSimilar(String src, String target) {
		String[] srcArr = src.split(",");
		double similar=0;
		double targetLen=target.length();
		int length = srcArr.length;
		for (int i = 0; i < length; i++) {
			String oneSrc=srcArr[i];
			if (target.indexOf(oneSrc)>-1) {
				similar+=oneSrc.length()/targetLen;
			}
		}
		return similar;
	}
}
