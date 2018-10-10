package com.lzzcms.service;

import java.util.List;
import java.util.Map;

import com.lzzcms.dto.TreeDto;
import com.lzzcms.model.RightInfo;



public interface RightInfoService {

	List<TreeDto> allRights(String basePath);

	List<TreeDto> loadRights(int adminId);
	/**
	 * 查出所有叶子权限供权限校验使用
	 * @return
	 */
	List<Map<String, Object>> getRightsForSc();


	
}
