package com.lzzcms.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;



public interface DownLoadService {

	void downLoad(String toDown, HttpServletRequest request);

	List<Map<String, Object>> listDownloads(HttpServletRequest request);

	long getTotalCount(HttpServletRequest request);

}
