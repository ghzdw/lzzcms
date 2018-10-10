package com.lzzcms.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lzzcms.model.ExecLog;
import com.lzzcms.model.SearchWord;





public interface SearchWordService {

	void saveOrUpdate(String queryString);

	List<Map<String, Object>> trueList();

	int getPageCount();
	
}
