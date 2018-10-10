package com.lzzcms.service;

import java.util.List;
import java.util.Map;

import com.lzzcms.model.AdminInfo;



public interface AdminInfoService {

	List<Map<String, Object>> trueList();

	void assignRoles(Map<String, String> map);

	List<Map<String, Object>> getRoles(Integer adminId);

	AdminInfo trueLogin(String uname, String pwd);

	void trueAddAdmin(Map<String, String> map);

	String deleteAdmin(String param);

	Map<String, String> trueUpAdmin(Map<String, String> map);

	int getMaxGroup();
}
