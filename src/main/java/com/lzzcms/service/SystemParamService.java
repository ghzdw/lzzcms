package com.lzzcms.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.lzzcms.model.AdminInfo;
import com.lzzcms.model.ChannelInfo;
import com.lzzcms.model.SystemParam;



public interface SystemParamService {

	List<Map<String, Object>> getSrcForCombobox();

	List<Map<String, Object>> getAuthorForCombobox();
	List<Map<String, Object>> getDefineFlagForCombobox();
	void updatePageCfg(Map<String, String> map);

	List<SystemParam> toPageCfg();
	List<Map<String, Object>> getInitContants();

	int trueAddGlobalCfg(HttpServletRequest request);

	List<Map<String, Object>> getGlobalCfg();

	void delGlobalCfg(HttpServletRequest request);

	
}
