package com.lzzcms.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.lzzcms.dao.AuthorDao;
import com.lzzcms.service.AuthorService;
import com.lzzcms.utils.PageContext;

@Service
public class AuthorServiceImpl implements AuthorService{
	private Logger logger=Logger.getLogger(AuthorServiceImpl.class);
	@Resource
	private AuthorDao authorDao;
	@Override
	public List<Map<String, Object>> listAuthors(HttpServletRequest request) {
		StringBuffer sb=new StringBuffer();
		sb.append(" select id,author_name from lzz_author ");
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
		logger.info("查询作者："+sb.toString());
		return authorDao.queryForList(sb.toString());
	}
	@Override
	public Long getAuthorCount() {
		StringBuffer sb=new StringBuffer();
		sb.append(" select count(*) from lzz_author ");
		return authorDao.queryForLong(sb.toString());
	}
	@Override
	public void addAuthor(Map<String, Object> map) {
		StringBuffer sb=new StringBuffer();
		sb.append(" insert into  lzz_author(author_name) values(?) ");
		authorDao.executeSql(sb.toString(), map.get("authorName"));
	}
	@Override
	public void deleteAuthor(String ids) {
		StringBuffer sb=new StringBuffer();
		sb.append(" delete from   lzz_author where id in ("+ids+") ");
		authorDao.executeSql(sb.toString());
	}

	
}
