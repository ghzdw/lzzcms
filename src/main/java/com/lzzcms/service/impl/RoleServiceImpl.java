package com.lzzcms.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lzzcms.dao.RoleDao;
import com.lzzcms.model.RightInfo;
import com.lzzcms.model.Role;
import com.lzzcms.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService{
	@Resource
	private RoleDao roleDao;

	
	public List<Map<String, Object>> trueList() {
		String sql="select roleid,rolename,rolevalue,roledesc from lzz_role";
		return roleDao.queryForList(sql);
	}


	@Override
	public List<Map<String, Object>> getRights(Integer roleId) {
		String sql="select right_id,role_id from link_role_right lrr where lrr.role_id=?";
		return roleDao.queryForList(sql, roleId);
	}


	@Override
	public void assignRights(Map<String, String> map) {
		String sql="delete from link_role_right  where role_id=?";
		roleDao.executeSql(sql, map.get("roleId"));
		String ids=map.get("ids");
		String[] idArr = ids.split(",");
		int len=idArr.length;
		List<Object[]> list=new ArrayList<Object[]>();
		for (int i = 0; i < len; i++) {
			Object[] objects=new Object[2];
			objects[0]=map.get("roleId");
			objects[1]=idArr[i];
			list.add(objects);
		}
		sql="insert into link_role_right(role_id,right_id) values(?,?)";
		roleDao.batchExecuteSql(sql, list);
	}


	@Override
	public void trueAddRole(String roleName, String roleDesc) {
		 StringBuffer sb=new StringBuffer();
		 sb.append(" insert into lzz_role(rolename,roledesc,rolevalue) values(?,?,0) ");
		 roleDao.executeSql(sb.toString(), roleName,roleDesc);
	}


	@Override
	public void deleteRoleById(String roleIds) {
		 String[] split = roleIds.split(",");
		 for (int i = 0; i < split.length; i++) {
			Integer roleId=Integer.valueOf(split[i]);
			StringBuffer sb=new StringBuffer();
			//删除link_admin_role表中的该角色数据，用户不再拥有该角色
			sb.append(" delete from link_admin_role where role_id=? ");
			roleDao.executeSql(sb.toString(),roleId);
			//删除link_role_right中的该角色数据，该角色不再拥有任何权限
			sb.setLength(0);
			sb.append(" delete from link_role_right where role_id=? ");
			roleDao.executeSql(sb.toString(),roleId);
			//删除角色
			sb.setLength(0);
			sb.append(" delete from lzz_role where roleid=? ");
			roleDao.executeSql(sb.toString(),roleId);
		}
	}
	
}
