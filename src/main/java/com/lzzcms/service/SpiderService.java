package com.lzzcms.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lzzcms.model.AdminInfo;



public interface SpiderService {

	Map<String, String> parseUrl(HttpServletRequest request);

	List<Map<String, Object>> getCfgById(String cfgId);

	Map<String, String> startSpiderById(HttpServletRequest request);

	List<String> startSpiderHand(HttpServletRequest request,
			HttpServletResponse response);

	List<String> item_href_value_list(String list_url, String list_item_selector);

	void saveOneHref(String oneHref, String comm_titleSelector,
			String mainbodySelector, String column_id, HttpServletRequest request,String excludeSelector);

	List<Map<String, Object>> getCrawls(Map<String, Object> paramMap);

	Long getCrawlsCount(Map<String, Object> paramMap);

	void addCrawl(Map<String, Object> paramMap);

	void addCrawlDetail(List<Map<String, Object>> paramList);

	Map<String, Object> showCrawlDetailByCrawlId(Map<String, Object> paramMap);

	void deleteCrawlById(Map<String, Object> paramMap);

	void updateCrawlDetail(List<Map<String, Object>> paramList);

	void onOffItem(Map<String, Object> paramMap);

	Map<String, Object> showCrawlByCrawlId(Map<String, Object> paramMap);

	void updateCrawl(Map<String, Object> paramMap);

	
}
