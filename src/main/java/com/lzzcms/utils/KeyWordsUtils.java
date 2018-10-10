package com.lzzcms.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.Logger;

import com.lzzcms.components.HttpClientPool;
import com.lzzcms.listeners.SpringBeanFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class KeyWordsUtils {
	private static Logger logger=Logger.getLogger(KeyWordsUtils.class);
	private static String lexerUrl="https://aip.baidubce.com/rpc/2.0/nlp/v1/lexer";
	/**
	 * now:提取关键词
	 * 参数：text,count
	 */
	public static String getKeywords(String txt){
		String ret=null;
		try {
			if (StringUtils.isNotBlank(txt)) {//<20000个字节
				char[] array = txt.toCharArray();  
	            int currentLength = 0;    
	            StringBuffer sb=new StringBuffer();
	            for(char c : array){  
	                // 字符长度,即字节个数  
	                int charlen = String.valueOf(c).getBytes("gbk").length;  
	                if ((currentLength+charlen)<20000) {
	                	currentLength+=charlen;
	                	sb.append(c);
					}else {
						break;
					}
	            }  
        		txt=sb.toString();
        		ret=keywords(txt);
			}
		} catch (Exception e) {
			logger.error("keywords关键词获取接口处理异常:",e);
		} 
		return ret;
	}
	private static String keywords(String txt) {
		int count=10;//取10个关键字
		JSONObject parmMap = new JSONObject();
		parmMap.put("text", txt);
		String retStr = postAppJsonGbk(lexerUrl,parmMap);
		JSONObject resultJsonObject = JSONObject.fromObject(retStr);
		JSONArray itemsArr = resultJsonObject.getJSONArray("items");
		Map<String, Integer> treeMap = new TreeMap<String, Integer>();// 分词
		// 因为items里面的对象并未去重，这里通过操作处理过的关键词的出现次数筛选关键词
		if (itemsArr != null && itemsArr.size() > 0) {
			int size = itemsArr.size();
			for (int i = 0; i < size; i++) {
				JSONObject oneItemObj = itemsArr.getJSONObject(i);
				String itemStr = oneItemObj.getString("item");
				if (StringUtils.isNotBlank(itemStr)) {
					String itemTrimed = itemStr.trim();
					String pos = oneItemObj.getString("pos");// 词性
					if (StringUtils.isNotBlank(pos)) {// 命名实体类型ne有值时词性为空
						if (getStopPos().contains(pos)) {// 相当于加入停用词功能
							continue;
						}
					}
					if (itemTrimed.length() > 1) {// 相当于加入停用词功能
						Integer total = treeMap.get(itemTrimed);
						if (total != null) {
							total += 1;
							treeMap.put(itemTrimed, total);
						} else {
							treeMap.put(itemTrimed, 1);
						}
					}
				}
			}
		}
		List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(treeMap.entrySet());
		// 按value（出现次数）降序排列
		Collections.sort(list, new Comparator<Entry<String, Integer>>() {
			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		int size = list.size();
		count=count>size?size:count;
		String[] keyList = new String[count];
		for (int i = 0; i < count; i++) {
			Entry<String, Integer> entry = list.get(i);
			keyList[i]=entry.getKey();
		}
		String join = StringUtils.join(keyList, ",");
		return join;
	}
	private static List<String> getStopPos() {
		List<String> stopPos = new ArrayList<String>();
		stopPos.add("f");// 方位名词
		stopPos.add("d");// 副词
		stopPos.add("m");// 数量词
		stopPos.add("q");// 量词
		stopPos.add("r");// 代词
		stopPos.add("p");// 介词
		stopPos.add("c");// 连词
		stopPos.add("u");// 助词
		stopPos.add("xc");// 其他虚词
		stopPos.add("w");// 标点符号
		return stopPos;
	}
	/**
	 * contentType: application/json ,编码：gbk
	 */
	private static String postAppJsonGbk(String url, JSONObject parmMap) {
		HttpClientPool pool = SpringBeanFactory.getBean("httpClientPool", HttpClientPool.class);
		url=url+ "?access_token="+ AuthUtil.getAuth();
		String charset="gbk";
		List<Header> headers=new ArrayList<Header>();
		Header header=new BasicHeader("Content-Type", "application/json;charset="+charset);
		headers.add(header);
		String retStr=pool.executePostApplicationJson(url, headers, parmMap, charset);
		return retStr;
	}
}
