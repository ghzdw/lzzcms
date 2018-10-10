package com.lzzcms.model;

import java.util.Date;

/**
 * @author zhao
 * 系统参数表
 */
public class SystemParam {
	private int id;
	private String groupName;
	private String paramName;
	private String paramValue;
	public int getId() {
		return id;
	}
	
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setId(int id) {
		this.id = id;
	}
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	public String getParamValue() {
		return paramValue;
	}
	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
	
}
