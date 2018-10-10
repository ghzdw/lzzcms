package com.lzzcms.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import com.lzzcms.model.AdminInfo;
import com.lzzcms.model.ChannelInfo;
import com.lzzcms.model.ExtraColumnsDescForChl;



public interface ContentInfoService {

	List<Map<String, Object>> trueList(Map<String, String> paramMap, HttpServletRequest request);

	Map<String, Object> getAddforinfo(String comm_id, String channel_id);

	List<ExtraColumnsDescForChl> toAddExtral(String channel_id);

	String trueAddContnet(MultipartFile file, HttpServletRequest request) throws Exception;

	Map<String, Object> getCommById(String comm_id);

	Map<String, Object> getAddforinfoById(String comm_id, String channel_id, String additionalTable);

	String trueUpContnet(MultipartFile file, HttpServletRequest request) throws Exception;

	void deleteContent(List<Map<String, Object>> list, HttpServletRequest request,String needDelAddtionTable);


	Map<String, Object> autoGeKeywordsAndIntro(String txt, String dir);

	int validateTitle(String title);

	Long updateAndgetClick(String comm_id);

	String getBestFragment(String txt, String maxFreqStr, HttpServletRequest request);

	
}
