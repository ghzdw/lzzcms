package com.lzzcms.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import com.lzzcms.model.ChannelInfo;
import com.lzzcms.model.FriendLink;



public interface FriendLinkService {
	List<FriendLink> trueList(HttpServletRequest request);
	long getTotalCount();
	List<FriendLink> getFlinks(String hql,Byte type);
	Map<String, Object> trueAdd(MultipartFile file, HttpServletRequest request);
	void delete(HttpServletRequest request);
	void trueUpdate(HttpServletRequest request);
}
