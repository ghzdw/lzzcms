package com.lzzcms.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;





public interface SearchService {

	Map<String, Object> manualClear(HttpServletRequest request)throws Exception;

	String search(HttpServletRequest request, HttpServletResponse response, String queryString) throws Exception;
	
}
