package com.lzzcms.model;

import java.util.HashSet;
import java.util.Set;


/**
 * 角色类
 * @author zhao
 */
public class Role {
	private Integer roleId;
	private String roleName;
	private Integer roleValue=0;//当为-1时:用户的超级管理员为true,默认为0
	private String roleDesc;
	private Set<RightInfo> rights=new HashSet<RightInfo>();
	private Set<AdminInfo> adminInfos=new HashSet<AdminInfo>();
	public Set<RightInfo> getRights() {
		return rights;
	}

	public void setRights(Set<RightInfo> rights) {
		this.rights = rights;
	}
	
	public void addRight(RightInfo rightInfo){
		this.rights.add(rightInfo);
	}
	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}



	public Integer getRoleValue() {
		return roleValue;
	}

	public void setRoleValue(Integer roleValue) {
		this.roleValue = roleValue;
	}

	public String getRoleDesc() {
		return roleDesc;
	}

	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
	}

	public Set<AdminInfo> getAdminInfos() {
		return adminInfos;
	}

	public void setAdminInfos(Set<AdminInfo> adminInfos) {
		this.adminInfos = adminInfos;
	}
	
}
