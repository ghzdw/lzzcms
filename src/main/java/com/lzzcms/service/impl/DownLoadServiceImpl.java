package com.lzzcms.service.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.lzzcms.dao.DownLoadDao;
import com.lzzcms.model.DownLoadStatistics;
import com.lzzcms.service.DownLoadService;
import com.lzzcms.utils.LzzcmsUtils;
import com.lzzcms.utils.PageContext;

@Service
public class DownLoadServiceImpl implements DownLoadService {
	Logger logger = Logger.getLogger(DownLoadServiceImpl.class);
	@Resource
	private DownLoadDao downLoadDao;

	@Override
	public void downLoad(String toDown, HttpServletRequest request) {
		DownLoadStatistics d = new DownLoadStatistics();
		d.setDownDate(new Date());
		d.setSoftName(toDown);
//		System.out.println(request.getRemoteHost());//127.0.0.1
//		System.out.println(request.getRemoteAddr());//127.0.0.1
		String ip = LzzcmsUtils.getRealIp(request);
		d.setIp(ip);
		downLoadDao.saveEntity(d);
	}

	@Override
	public List<Map<String, Object>> listDownloads(HttpServletRequest request) {
		StringBuffer sBuffer=new StringBuffer();
		sBuffer.append(" select date_format(downdate,'%Y-%m-%d %H:%i:%s') downdate,softname,ip from lzz_download ");
		if (StringUtils.isNotBlank(request.getParameter("sort"))&&StringUtils.isNotBlank(request.getParameter("order"))) {
			sBuffer.append(" order by "+request.getParameter("sort")+" "+request.getParameter("order"));
		}
		if (PageContext.getPageUtil().isNeedPage()) {
			int pageNow=PageContext.getPageUtil().getPageNow();
			int pageSize=PageContext.getPageUtil().getPageSize();
			int start=(pageNow-1)*pageSize;
			sBuffer.append(" limit "+start+","+pageSize);
		}
		return downLoadDao.queryForList(sBuffer.toString());
	}

	@Override
	public long getTotalCount(HttpServletRequest request) {
		StringBuffer sBuffer=new StringBuffer();
		sBuffer.append(" select count(*) from lzz_download ");
		return downLoadDao.queryForLong(sBuffer.toString());
	}
}
