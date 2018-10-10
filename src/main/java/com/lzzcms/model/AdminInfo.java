package com.lzzcms.model;

import java.util.HashSet;
import java.util.Set;


public class AdminInfo {
	private int id;
	private String userName;
	private String realName;
	private String pWord;
	private Set<Role> roles=new HashSet<Role>();
	//存放用户的权限总和，在登录成功后初始化长度,不用映射到数据库,下标是权限的组
	private long[] rightSum=null;
	//是否是超级管理员，畅通无阻,也不用映射到数据库
	private boolean superAdmin;
	
	public long[] getRightSum() {
		return rightSum;
	}
	public void setRightSum(long[] rightSum) {
		this.rightSum = rightSum;
	}
	public boolean isSuperAdmin() {
		return superAdmin;
	}
	public void setSuperAdmin(boolean superAdmin) {
		this.superAdmin = superAdmin;
	}
	public Set<Role> getRoles() {
		return roles;
	}
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getpWord() {
		return pWord;
	}
	public void setpWord(String pWord) {
		this.pWord = pWord;
	}
	public AdminInfo(String userName, String realName, String pWord) {
		this.userName = userName;
		this.realName = realName;
		this.pWord = pWord;
	}
	public AdminInfo() {
	}
	/**
	 * 计算权限和
	 */
	public void calculateRightSum(){
		for(Role r:roles){//得到用户的角色集合
			if (r.getRoleValue().equals(-1)) {
				this.setSuperAdmin(true);
				break;
			}
			for(RightInfo authority:r.getRights()){//得到每个角色的每条权限
				int rightGroup=authority.getRightGroup();
				long rightCode=authority.getRightCode();
				this.rightSum[rightGroup]=this.rightSum[rightGroup]|rightCode;//求得用户在该组内的权限和
			}
		}
		this.setRoles(null);
	}
	/**
	 * 判断是否有某个权限
	 * @param right
	 * @return
	 */
	public boolean hasRight(RightInfo right) {
		if (this.isSuperAdmin()) {
			return true;
		}else {
			//总和与要被判断的权限做与运算，结果等于要被判断的权限的权限码就有权限，否则无
			return ((rightSum[right.getRightGroup()])&(right.getRightCode()))==right.getRightCode();
		}
	}
}
