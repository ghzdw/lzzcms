package com.lzzcms.dao;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.support.rowset.SqlRowSet;

public interface BackupDao  {
	List<Map<String, Object>> queryForList(String sql,Object...objects);

	SqlRowSet queryforSet(String sql,Object...args);
	
	String queryforString(String sql) ;
	List<String> queryForListString(String sql,Object...args) ;
	void executeSql(String sql,Object... args);
	Long queryForLong(String sql);
}
