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
import com.lzzcms.service.AuthorService;
import com.lzzcms.service.DefineFlagService;
import com.lzzcms.utils.PageContext;

@Service
public class DefineFlagServiceImpl implements DefineFlagService{
	private Logger logger=Logger.getLogger(DefineFlagServiceImpl.class);
	@Resource
	private DefineFlagDao defineFlagDao;
	@Override
	public List<Map<String, Object>> listDefineFlags(HttpServletRequest request) {
		StringBuffer sb=new StringBuffer();
		sb.append(" select id,define_flag,en_name from lzz_define_flag ");
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
		logger.info("查询自定义标记："+sb.toString());
		return defineFlagDao.queryForList(sb.toString());
	}
	@Override
	public Long getDefineFlagCount() {
		StringBuffer sb=new StringBuffer();
		sb.append(" select count(*) from lzz_define_flag ");
		return defineFlagDao.queryForLong(sb.toString());
	}
	@Override
	public void addDefineFlag(Map<String, Object> map) {
		StringBuffer sb=new StringBuffer();
		sb.append(" insert into  lzz_define_flag(define_flag,en_name) values(?,?) ");
		defineFlagDao.executeSql(sb.toString(), map.get("defineFlag"),map.get("enName"));
	}
	@Override
	public void deleteDefineFlag(String ids) {
		StringBuffer sb=new StringBuffer();
		sb.append(" delete from   lzz_define_flag where id in ("+ids+") ");
		defineFlagDao.executeSql(sb.toString());
	}
	

	
}
