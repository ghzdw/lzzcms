package com.lzzcms.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.lzzcms.dao.AuthorDao;
import com.lzzcms.dao.DefineFlagDao;
import com.lzzcms.dao.SourceDao;
import com.lzzcms.service.AuthorService;
import com.lzzcms.service.SourceService;
import com.lzzcms.utils.PageContext;

@Service
public class SourceServiceImpl implements SourceService{
	private Logger logger=Logger.getLogger(SourceServiceImpl.class);
	@Resource
	private SourceDao sourceDao;
	@Override
	public List<Map<String, Object>> listSources(HttpServletRequest request) {
		StringBuffer sb=new StringBuffer();
		sb.append(" select id,come_from from lzz_source ");
		String sort = request.getParameter("sort");
		String order = request.getParameter("order");
		if (StringUtils.isNotBlank(sort)&&StringUtils.isNotBlank(order)) {
			sb.append(" order by "+sort+" "+order);
		}
		if (PageContext.getPageUtil().isNeedPage()) {
			int pageNow=PageContext.getPageUtil().getPageNow();
			int pageSize=PageContext.getPageUtil().getPageSize();
			int start=(pageNow-1)*pageSize;
			sb.append(" limit "+start+","+pageSize);
		}
		logger.info("查询来源："+sb.toString());
		return sourceDao.queryForList(sb.toString());
	}
	@Override
	public Long getSourceCount() {
		StringBuffer sb=new StringBuffer();
		sb.append(" select count(*) from lzz_source ");
		return sourceDao.queryForLong(sb.toString());
	}
	@Override
	public void addSource(Map<String, Object> map) {
		StringBuffer sb=new StringBuffer();
		sb.append(" insert into  lzz_source(come_from) values(?) ");
		sourceDao.executeSql(sb.toString(), map.get("comeFrom"));
	}
	@Override
	public void deleteSource(String ids) {
		StringBuffer sb=new StringBuffer();
		sb.append(" delete from   lzz_source where id in ("+ids+") ");
		sourceDao.executeSql(sb.toString());
	}
	

	
}
