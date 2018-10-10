package com.lzzcms.dao;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.support.rowset.SqlRowSet;

public interface SpiderDao  {
	List<Map<String, Object>> queryForList(String sql,Object...objects);

	SqlRowSet queryforSet(String sql,Object...args);
	
	String queryforString(String sql) ;
	List<String> queryForListString(String sql,Object...args) ;
	void executeSql(String sql,Object... args);
	int queryForInt(String sql);

	void saveSpiderCfg(Map<String, Object> fromJson);

	long queryForLong(String sql, Object... args);
}
