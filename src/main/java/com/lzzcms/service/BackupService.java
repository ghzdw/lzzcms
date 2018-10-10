package com.lzzcms.service;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;


public interface BackupService {
	 List<Map<String, Object>> listBackUps();

	 String backTo(ServletContext sc, String backupName);

	 String backUp(ServletContext sc,String userName);
	
}
