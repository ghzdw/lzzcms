package com.lzzcms.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;



public interface AuthorService {

	List<Map<String, Object>> listAuthors(HttpServletRequest request);
	Long getAuthorCount();
	void addAuthor(Map<String, Object> map);
	void deleteAuthor(String ids);
}
