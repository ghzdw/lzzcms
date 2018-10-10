package com.lzzcms.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lzzcms.dao.SearchWordDao;
import com.lzzcms.model.ExecLog;
import com.lzzcms.model.SearchWord;
import com.lzzcms.service.SearchWordService;
import com.lzzcms.utils.LzzcmsUtils;




@Service
public class SearchWordServiceImpl  implements SearchWordService{
	@Resource
	private SearchWordDao searchWordDao;
	
	public SearchWordDao getSearchWordDao() {
		return searchWordDao;
	}

	public void setSearchWordDao(SearchWordDao searchWordDao) {
		this.searchWordDao = searchWordDao;
	}

	@Override
	public void saveOrUpdate(String queryString) {
		SearchWord searchWord=new SearchWord();
		Date date = new Date();
		String crtDateStr = LzzcmsUtils.getPatternDateString("yyyy-MM-dd", date);
		StringBuffer sb=new StringBuffer();
		sb.append(" select count(*)  from lzz_searchword a where  ");
		sb.append(" DATE_FORMAT(searchdate,'%Y-%m-%d')=? and a.searchtext=?  ");
		Integer queryForInt = searchWordDao.queryForInt(sb.toString(), crtDateStr,queryString);
		if (queryForInt==0) {//新增
			crtDateStr = LzzcmsUtils.getPatternDateString("yyyy-MM-dd", date);
			Date crtDate=LzzcmsUtils.getPatternDate("yyyy-MM-dd", crtDateStr);
			searchWord.setSearchDate(crtDate);
			searchWord.setSearchText(queryString);
			searchWord.setSearchCount(1);
			searchWordDao.saveEntity(searchWord);
		}else {//修改
			sb.setLength(0);
			sb.append(" update lzz_searchword  set searchcount=searchcount+1 where  DATE_FORMAT(searchdate,'%Y-%m-%d')=? and searchtext=? ");
			searchWordDao.executeSql(sb.toString(), crtDateStr,queryString);
		}
	}

	@Override
	public  List<Map<String, Object>> trueList() {
	    StringBuffer sb=new StringBuffer();
	    sb.append(" select searchtext,sum(searchcount) searchcount from lzz_searchword                                            ");
	    sb.append(" where DATE_FORMAT(searchdate,'%Y-%m-%d')  BETWEEN  DATE_FORMAT(DATE_ADD(NOW(),INTERVAL -1 MONTH),'%Y-%m-%d')  ");
	    sb.append(" AND  DATE_FORMAT(NOW(),'%Y-%m-%d')  GROUP BY searchtext   ORDER BY  searchcount desc   limit 0,10          ");
	    List<Map<String, Object>> list = searchWordDao.queryForList(sb.toString());
		return list;
	}

	@Override
	public int getPageCount() {
		 StringBuffer sb=new StringBuffer();
	     sb.append(" select count(*) from lzz_searchword ");
	     int total= searchWordDao.queryForInt(sb.toString());
	     int pageCount= new Double(Math.ceil(total/new Double(10))).intValue();
		 return pageCount;
	}

	
	
}
