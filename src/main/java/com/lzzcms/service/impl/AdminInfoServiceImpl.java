package com.lzzcms.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.lzzcms.dao.AdminInfoDao;
import com.lzzcms.model.AdminInfo;
import com.lzzcms.service.AdminInfoService;
import com.lzzcms.utils.LzzcmsUtils;
import com.lzzcms.utils.PageContext;

@Service
public class AdminInfoServiceImpl implements AdminInfoService{
	private Logger logger=Logger.getLogger(AdminInfoServiceImpl.class);
	@Resource
	private AdminInfoDao adminInfoDao;

	public AdminInfoDao getAdminInfoDao() {
		return adminInfoDao;
	}

	public void setAdminInfoDao(AdminInfoDao adminInfoDao) {
		this.adminInfoDao = adminInfoDao;
	}

	@Override
	public List<Map<String, Object>> trueList() {
		StringBuffer sb=new StringBuffer();
		if (PageContext.getPageUtil().isNeedPage()) {
			sb.append(" select id,username,realname,pword ");
		}else {
			sb.append(" select count(*) count ");
		}
		sb.append(" from lzz_admininfo ");
		if (PageContext.getPageUtil().isNeedPage()) {
			int pageNow=PageContext.getPageUtil().getPageNow();
			int pageSize=PageContext.getPageUtil().getPageSize();
			int start=(pageNow-1)*pageSize;
			sb.append(" limit "+start+","+pageSize);
		}
		logger.info("查询管理员："+sb.toString());
		return adminInfoDao.queryForList(sb.toString());
	}

	@Override
	public void assignRoles(Map<String, String> map) {
		String sql="delete from link_admin_role  where admin_id=?";
		adminInfoDao.executeSql(sql, Integer.valueOf(map.get("adminId")));
		String ids=map.get("ids");
		String[] idArr = ids.split(",");
		int len=idArr.length;
		List<Object[]> list=new ArrayList<Object[]>();
		for (int i = 0; i < len; i++) {
			Object[] objects=new Object[2];
			objects[0]=Integer.valueOf(map.get("adminId"));
			objects[1]=Integer.valueOf(idArr[i]);
			list.add(objects);
		}
		sql="insert into link_admin_role(admin_id,role_id) values(?,?)";
		adminInfoDao.batchExecuteSql(sql, list);
	}

	@Override
	public List<Map<String, Object>> getRoles(Integer adminId) {
		String sql="select lar.role_id from link_admin_role lar where lar.admin_id=?";
		return adminInfoDao.queryForList(sql, adminId);
	}

	@Override
	public AdminInfo trueLogin(String uname, String pwd) {
		String hql=" select a from AdminInfo  a left join fetch a.roles  b left join fetch b.rights where a.userName=?";
		AdminInfo adminInfo = adminInfoDao.uniqueResult(hql, uname);
		if (adminInfo!=null) {
			if(adminInfo.getpWord().equals(LzzcmsUtils.getEncryptResult(pwd))){
				return adminInfo;
			}
		}else {
			return null;
		}
		return null;
	}

	@Override
	public void trueAddAdmin(Map<String, String> map) {
		String pWord = map.get("adminManage_addpwd");
		String encryptResult = LzzcmsUtils.getEncryptResult(pWord);
		AdminInfo a=new AdminInfo(map.get("adminManage_addUname"), map.get("adminManage_addrealname")
				, encryptResult);
		adminInfoDao.saveEntity(a);
	}

	/**
	 * 先删除管理员与角色关联的表
	 */
	@Override
	public String deleteAdmin(String param) {
		String[] idArr = param.split(",");
		int len=idArr.length;
		StringBuffer sb=new StringBuffer();
		List<Object[]> list=new ArrayList<Object[]>();
		sb.append("delete from lzz_admininfo where id=?");
		for (int i = 0; i < len; i++) {
			Object[] objects=new Object[1];
			objects[0]=idArr[i];
			list.add(objects);
		}
		try {
			String sql="delete from link_admin_role where admin_id=?";
			adminInfoDao.batchExecuteSql(sql, list);
			String sql2="delete from lzz_execlog where admin_id=?";
			adminInfoDao.batchExecuteSql(sql2, list);
			adminInfoDao.batchExecuteSql(sb.toString(), list);
		} catch (Exception e) {
			logger.info(e);
			return "删除管理出错";
		}
		return null;
	}

	@Override
	public Map<String, String> trueUpAdmin(Map<String, String> map) {
		Map<String, String> retMap=new HashMap<>();
		//{adminManage_upid=8, adminManage_uprealname=dsf, adminManage_upoldpwd=adminsdf, adminManage_upnewpwd=aaaaadsf}
		StringBuffer sb=new StringBuffer();
		Integer adminManage_upid=Integer.valueOf(map.get("adminManage_upid"));
		String realName=map.get("adminManage_uprealname");
		String adminManage_upoldpwd=LzzcmsUtils.getEncryptResult(map.get("adminManage_upoldpwd"));
		String adminManage_upnewpwd=map.get("adminManage_upnewpwd");
		if (StringUtils.isNotBlank(adminManage_upoldpwd)) {
			sb.append(" select pword from lzz_admininfo where id = ? ");
			String pwd = adminInfoDao.queryForString(sb.toString(), adminManage_upid);
			if (!adminManage_upoldpwd.equals(pwd)) {
				retMap.put("type", "error");
				retMap.put("info", "原密码不正确");
				return retMap;
			}
			sb.setLength(0);
			sb.append(" update lzz_admininfo set realname=?,pwd=? where id=? ");
			adminInfoDao.executeSql(sb.toString(), realName,
					LzzcmsUtils.getEncryptResult(adminManage_upnewpwd),adminManage_upid);
		}else{
			sb.setLength(0);
			sb.append(" update lzz_admininfo set realname=? where id=? ");
			adminInfoDao.executeSql(sb.toString(), realName,adminManage_upid);
		}
		retMap.put("type", "info");
		retMap.put("info", "修改成功");
		return retMap;
	}

	@Override
	public int getMaxGroup() {
		StringBuffer sb=new StringBuffer();
		sb.append("select max(rightgroup) from lzz_right ");
		return adminInfoDao.queryForInt(sb.toString());
	}
	
}
