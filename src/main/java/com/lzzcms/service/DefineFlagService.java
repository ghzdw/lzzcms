package com.lzzcms.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;



public interface DefineFlagService {

	List<Map<String, Object>> listDefineFlags(HttpServletRequest request);
	Long getDefineFlagCount();
	void addDefineFlag(Map<String, Object> map);
	void deleteDefineFlag(String ids);
}
