package com.lzzcms.dao;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.support.rowset.SqlRowSet;

public interface CommentDao  {
	List<Map<String, Object>> queryForList(String sql,Object...objects);
	void executeSql(String sql,Object... args);
	Long queryForLong(String sql,Object...objects);
}
