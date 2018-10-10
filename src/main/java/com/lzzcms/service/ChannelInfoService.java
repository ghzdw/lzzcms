package com.lzzcms.service;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import com.lzzcms.model.AdminInfo;
import com.lzzcms.model.ChannelInfo;



public interface ChannelInfoService {

	List<Map<String, Object>> trueList(Map<String, Object> paramMap);

	 List<Map<String, Object>> getForCombobox();

	Map<String, Object> toAddChannelInfo();

	void trueAddChannelInfo(ChannelInfo channelInfo);

	ChannelInfo getChannelById(Integer valueOf);

	void trueEditChannelInfo(ChannelInfo channelInfo) throws Exception;

	List<Map<String, Object>> channelAdvancedCfg(String channelId);

	void addFieldDia(Map<String, String> paramsMap);

	long getTotalCount();

	void deleteChl(List<Map<String, Object>> list, ServletContext servletContext) throws Exception;
}
