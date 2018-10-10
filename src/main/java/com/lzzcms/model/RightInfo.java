package com.lzzcms.model;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class RightInfo{
	private Integer rightId;
	private String rightName;
	private String canAssign;
	private String rightUrl;
	private RightInfo parentRightInfo;
	private Set<RightInfo> childrenRightInfos=new LinkedHashSet<RightInfo>();
	private Set<Role> roles=new HashSet<Role>();
	private Integer rightGroup;//权限组，从0开始
	private Long rightCode;	//权限码
	private Boolean common=false;	//是否是公共资源
	private String type;//权限的类型：粗粒度coarse一般用于菜单 细粒度fined一般用于页面上的请求
	private Integer orderNo;
	public RightInfo(){
		
	}
	
	public String getCanAssign() {
		return canAssign;
	}

	public void setCanAssign(String canAssign) {
		this.canAssign = canAssign;
	}

	public Integer getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}

	public RightInfo(Integer rightId, String rightName,String rightUrl) {
		super();
		this.rightId = rightId;
		this.rightName = rightName;
		this.rightUrl = rightUrl;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public RightInfo getParentRightInfo() {
		return parentRightInfo;
	}
	public void setParentRightInfo(RightInfo parentRightInfo) {
		this.parentRightInfo = parentRightInfo;
	}
	public Set<RightInfo> getChildrenRightInfos() {
		return childrenRightInfos;
	}
	public void setChildrenRightInfos(Set<RightInfo> childrenRightInfos) {
		this.childrenRightInfos = childrenRightInfos;
	}
	public Integer getRightId() {
		return rightId;
	}
	public void setRightId(Integer rightId) {
		this.rightId = rightId;
	}
	public String getRightName() {
		return rightName;
	}
	public void setRightName(String rightName) {
		this.rightName = rightName;
	}
	public String getRightUrl() {
		return rightUrl;
	}
	public void setRightUrl(String rightUrl) {
		this.rightUrl = rightUrl;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public Integer getRightGroup() {
		return rightGroup;
	}

	public void setRightGroup(Integer rightGroup) {
		this.rightGroup = rightGroup;
	}

	public Long getRightCode() {
		return rightCode;
	}

	public void setRightCode(Long rightCode) {
		this.rightCode = rightCode;
	}

	public Boolean getCommon() {
		return common;
	}

	public void setCommon(Boolean common) {
		this.common = common;
	}

}
