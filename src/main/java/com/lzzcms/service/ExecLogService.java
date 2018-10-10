package com.lzzcms.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lzzcms.model.ExecLog;





public interface ExecLogService {

	void saveOrUpdate(ExecLog execLog);

	List<Map<String, Object>> trueList(Map<String, String> paramMap);

	int getPageCount();
	
}
