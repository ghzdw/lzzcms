package com.lzzcms.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.lzzcms.dao.SystemParamDao;
import com.lzzcms.model.SystemParam;
import com.lzzcms.service.SystemParamService;
import com.lzzcms.utils.LzzConstants;
import com.lzzcms.utils.LzzcmsUtils;

@Service
public class SystemParamServiceImpl implements SystemParamService{
	private static Logger logger=Logger.getLogger(SystemParamServiceImpl.class);
	@Resource
	private SystemParamDao systemParamDao;

	public SystemParamDao getSystemParamDao() {
		return systemParamDao;
	}

	public void setSystemParamDao(SystemParamDao systemParamDao) {
		this.systemParamDao = systemParamDao;
	}

	@Override
	public List<Map<String, Object>> getSrcForCombobox() {
		StringBuffer sb=new StringBuffer();
		sb.append("select id,come_from from lzz_source");
		return systemParamDao.queryForList(sb.toString());
	}

	@Override
	public List<Map<String, Object>> getAuthorForCombobox() {
		StringBuffer sb=new StringBuffer();
		sb.append("select id,author_name from lzz_author");
		return systemParamDao.queryForList(sb.toString());
	}
	@Override
	public List<Map<String, Object>> getDefineFlagForCombobox() {
		StringBuffer sb=new StringBuffer();
		sb.append("select en_name,define_flag from lzz_define_flag");
		return systemParamDao.queryForList(sb.toString());
	}
	@Override
	public void updatePageCfg(Map<String, String> map) {
		List<Object[]> list =new ArrayList<Object[]>();
		Set<Entry<String, String>> entrySet = map.entrySet();
		for (Iterator<Entry<String, String>> iterator = entrySet.iterator(); iterator.hasNext();) {
			Entry<String, String> entry = iterator.next();
			String paramName=entry.getKey();
			paramName=paramName.substring(paramName.indexOf("_")+1);//pageCfg_indexname
			String paramVal=entry.getValue();
			Object[] objects=new Object[3];
			objects[0]=paramVal;
			objects[1]="pagecfg";
			objects[2]=paramName;
			list.add(objects);
		}
		StringBuffer sb=new StringBuffer();
		sb.append("update lzz_systemparam set paramvalue=? where groupname=? and paramname=? ");
		systemParamDao.batchExecuteSql(sb.toString(), list);
	}
	@Override
	public List<SystemParam> toPageCfg() {
		StringBuffer sb=new StringBuffer();
		sb.append("  from SystemParam where groupName=?");
		return systemParamDao.findByHql(sb.toString(), "pagecfg");
	}
	
	@Override
	public List<Map<String, Object>> getInitContants() {
		StringBuffer sb=new StringBuffer();
		sb.append("  select paramname,paramvalue from lzz_systemparam where groupname=? ");
		return systemParamDao.queryForList(sb.toString(), "initconstants");
	}

	@Override
	public int trueAddGlobalCfg(HttpServletRequest request) {
		StringBuffer sb=new StringBuffer();
		sb.append("insert into lzz_systemparam(paramname,paramvalue,groupname) ");
		sb.append(" values(?,?,?) ");
		String name=request.getParameter("nameVal");
		String value=request.getParameter("valueVal");
		String group=LzzConstants.GLOBAL;
		systemParamDao.executeSql(sb.toString(), name,value,group);
		sb.setLength(0);
		sb.append("select LAST_INSERT_ID()");
		int generateId = systemParamDao.queryForInt(sb.toString());
		return generateId;
	}

	@Override
	public List<Map<String, Object>> getGlobalCfg() {
		StringBuffer sb=new StringBuffer();
		sb.append(" select  s.id,s.paramname,s.paramvalue,s.groupname from  lzz_systemparam s ");
		sb.append(" where s.groupname=? ");
		return systemParamDao.queryForList(sb.toString(),LzzConstants.GLOBAL);
	}

	@Override
	public void delGlobalCfg(HttpServletRequest request) {
		StringBuffer sb=new StringBuffer();
		sb.append("delete from  lzz_systemparam  where id=? ");
		String lastInsertId=request.getParameter("lastInsertId");
		systemParamDao.executeSql(sb.toString(), lastInsertId);
	}
}
