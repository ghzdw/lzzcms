package com.lzzcms.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.lzzcms.dao.ExecLogDao;
import com.lzzcms.model.ExecLog;
import com.lzzcms.service.ExecLogService;
import com.lzzcms.service.SearchWordService;
import com.lzzcms.utils.PageContext;




@Service
public class ExecLogServiceImpl  implements ExecLogService{
	@Resource
	private ExecLogDao execLogDao;
	
	public ExecLogDao getExecLogDao() {
		return execLogDao;
	}

	public void setExecLogDao(ExecLogDao execLogDao) {
		this.execLogDao = execLogDao;
	}

	@Override
	public void saveOrUpdate(ExecLog execLog) {
		StringBuffer sBuffer=new StringBuffer();
		sBuffer.append(" insert into lzz_execlog(admin_id,execurldesc,execurl,exectype) values(?,?,?,?) ");
		execLogDao.executeSql(sBuffer.toString(),execLog.getAdminInfo().getId(),execLog.getExecUrlDesc()
				,execLog.getExecUrl(),execLog.getExecType());
	}

	@Override
	public List<Map<String,Object>>  trueList(Map<String, String> paramMap) {
	    StringBuffer sb=new StringBuffer();
	    sb.append(" select DATE_FORMAT(execdate,'%Y-%m-%d %H:%i:%s') execdate,execurldesc,execurl,exectype,realname from lzz_execlog a ");
	    sb.append("  left join lzz_admininfo b on a.admin_id=b.id ");
		if (StringUtils.isNotBlank(paramMap.get("sort"))&&StringUtils.isNotBlank(paramMap.get("order"))) {
			sb.append(" order by "+paramMap.get("sort")+" "+paramMap.get("order"));
		}
		if (PageContext.getPageUtil().isNeedPage()) {
			int pageNow=PageContext.getPageUtil().getPageNow();
			int pageSize=PageContext.getPageUtil().getPageSize();
			int start=(pageNow-1)*pageSize;
			sb.append(" limit "+start+","+pageSize);
		}
		List<Map<String,Object>>  list = execLogDao.queryForList(sb.toString());
		return list;
	}

	@Override
	public int getPageCount() {
		 StringBuffer sb=new StringBuffer();
	     sb.append(" select count(*) from lzz_execlog ");
	     int total= execLogDao.queryForInt(sb.toString());
		 return total;
	}
	
}
