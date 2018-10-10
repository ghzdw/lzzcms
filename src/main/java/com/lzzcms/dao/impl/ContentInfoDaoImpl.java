package com.lzzcms.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.lzzcms.dao.ContentInfoDao;
import com.lzzcms.dao.base.AbstractBaseDao;
import com.lzzcms.model.CommonContent;
import com.lzzcms.model.ExtraColumnsDescForChl;

@Repository
public class ContentInfoDaoImpl extends AbstractBaseDao<CommonContent> implements ContentInfoDao {
	@Resource
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Override
	public List<ExtraColumnsDescForChl> getColumnDescForChl(String sql,Object... objects) {
		List<ExtraColumnsDescForChl> query = jdbcTemplate.query(sql, objects, new RowMapper<ExtraColumnsDescForChl>(){
			@Override
			public ExtraColumnsDescForChl mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				ExtraColumnsDescForChl e=new ExtraColumnsDescForChl();
				e.setId(rs.getInt("id"));
				e.setColName(rs.getString("colname"));
				e.setColType(rs.getString("coltype"));
				e.setShowTip(rs.getString("showtip"));
				e.setAdditionalTable(rs.getString("additionaltable"));
				return e;
			}
		});
		return query;
	}
//
//	@Override
//	public String getAdditonalTableName(String sql, Object... channel_id) {
//		String queryForObject = jdbcTemplate.queryForObject(sql,channel_id , String.class);
//		return queryForObject;
//	}
//
//	@Override
//	public Map<String, Object> getAddForInfoById(String sql, Object... comm_id) {
//		Map<String, Object> queryForMap = jdbcTemplate.queryForMap(sql, comm_id);
//		return queryForMap;
//	}

	@Override
	public List<Map<String, Object>> selectByIn(String sql,Map<String, Object> map) {
	   return namedParameterJdbcTemplate.queryForList(sql, map);
	}

	@Override
	public void updateByIn(String sql, Map<String, Object> idsMap) {
		namedParameterJdbcTemplate.update(sql, idsMap);
	}
}
