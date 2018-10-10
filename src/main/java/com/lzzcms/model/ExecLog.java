package com.lzzcms.model;

import java.io.Serializable;
import java.util.Date;

public class ExecLog implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Date execDate;
	private AdminInfo adminInfo;
	private String   execUrlDesc;
	private String   execType;//非法访问等
	private String   execUrl;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Date getExecDate() {
		return execDate;
	}
	public void setExecDate(Date execDate) {
		this.execDate = execDate;
	}
	public AdminInfo getAdminInfo() {
		return adminInfo;
	}
	public void setAdminInfo(AdminInfo adminInfo) {
		this.adminInfo = adminInfo;
	}
	public String getExecUrlDesc() {
		return execUrlDesc;
	}
	public void setExecUrlDesc(String execUrlDesc) {
		this.execUrlDesc = execUrlDesc;
	}
	public String getExecType() {
		return execType;
	}
	public void setExecType(String execType) {
		this.execType = execType;
	}
	public String getExecUrl() {
		return execUrl;
	}
	public void setExecUrl(String execUrl) {
		this.execUrl = execUrl;
	}
	
}
