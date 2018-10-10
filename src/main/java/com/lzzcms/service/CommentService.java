package com.lzzcms.service;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface CommentService {
	 List<Map<String, Object>> listBackUps();

	 String backTo(ServletContext sc, String backupName);

	 String backUp(ServletContext sc,String userName);

	String addComment(HttpServletRequest request,HttpServletResponse response);

	List<Map<String, Object>> listComments(Map<String, String> paramMap);

	long listCommentsCount(Map<String, String> paramMap);

	void deleteComment(HttpServletRequest request);

	String replyComment(HttpServletRequest request, HttpServletResponse response);
	
}
