package com.lzzcms.service;

import java.util.List;
import java.util.Map;


public interface RoleService {

	List<Map<String, Object>> trueList();

	List<Map<String, Object>> getRights(Integer roleId);

	void assignRights(Map<String, String> map);

	void  trueAddRole(String roleName, String roleDesc);

	void deleteRoleById(String roleIds);

	
}
