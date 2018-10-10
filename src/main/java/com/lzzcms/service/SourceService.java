package com.lzzcms.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;



public interface SourceService {

	List<Map<String, Object>> listSources(HttpServletRequest request);
	Long getSourceCount();
	void addSource(Map<String, Object> map);
	void deleteSource(String ids);
}
