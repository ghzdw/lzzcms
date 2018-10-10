package com.lzzcms.dao;

import java.util.List;
import java.util.Map;

import com.lzzcms.dao.base.BaseDao;
import com.lzzcms.model.ChannelInfo;
import com.lzzcms.model.CommonContent;
import com.lzzcms.model.ExtraColumnsDescForChl;

public interface ContentInfoDao extends BaseDao<CommonContent>{
//	List<Map<String, Object>> trueList(String sql,Object... objects );
//
	List<ExtraColumnsDescForChl> getColumnDescForChl(String sql,Object... objects );
//
//	String getAdditonalTableName(String string, Object... channel_id);
//
//	Map<String, Object> getAddForInfoById(String string, Object... comm_id);
	List<Map<String, Object>> selectByIn(String sql,Map<String, Object> map);
	void updateByIn(String sql, Map<String, Object> idsMap);
}
