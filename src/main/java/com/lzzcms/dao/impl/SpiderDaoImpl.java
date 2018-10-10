package com.lzzcms.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.lzzcms.dao.SpiderDao;
@Repository
public class SpiderDaoImpl implements SpiderDao {
	@Resource
	private JdbcTemplate jdbcTemplate;
	public List<Map<String, Object>> queryForList(String sql,Object...objects){
		return	jdbcTemplate.queryForList(sql, objects);
	}
	/**
	 * desc xxx
	 */
	@Override
	public SqlRowSet queryforSet(String sql,Object...args) {
		return jdbcTemplate.queryForRowSet(sql, args);
	}
	/**
	 * show create table xxx
	 */
	public String queryforString(String sql) {
		return jdbcTemplate.queryForObject(sql, new RowMapper<String>(){
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString(2);
			}
		});
	}
	@Override
	public List<String> queryForListString(String sql, Object... args) {
		return jdbcTemplate.queryForList(sql, String.class, args);
	}
	public void executeSql(String sql,Object... args){
		jdbcTemplate.update(sql, args);
	}
	public int queryForInt(String sql){
		return jdbcTemplate.queryForObject(sql,Integer.class);
	}
	@Override
	public void saveSpiderCfg(Map<String, Object> fromJson) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public long queryForLong(String sql, Object... args) {
		return jdbcTemplate.queryForObject(sql, Long.class, args);
	}
}
