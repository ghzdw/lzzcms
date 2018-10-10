package com.lzzcms.dao.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.lzzcms.dao.CommentDao;
@Repository
public class CommentDaoImpl implements CommentDao {
	@Resource
	private JdbcTemplate jdbcTemplate;
	public List<Map<String, Object>> queryForList(String sql,Object...objects){
		return	jdbcTemplate.queryForList(sql, objects);
	}
	public void executeSql(String sql,Object... args){
		jdbcTemplate.update(sql, args);
	}
	public Long queryForLong(String sql,Object...objects){
		return jdbcTemplate.queryForObject(sql, objects, Long.class);
	}
}
