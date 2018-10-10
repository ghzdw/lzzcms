package com.lzzcms.model;

import java.io.Serializable;

public class ContRedisVo implements Serializable {
	private static final long serialVersionUID = -8626958965094444724L;
	private String comm_id;
	private String comm_title;
	private String comm_keywords;
	private String comm_htmlpath;
	public String getComm_id() {
		return comm_id;
	}
	public void setComm_id(String comm_id) {
		this.comm_id = comm_id;
	}
	public String getComm_title() {
		return comm_title;
	}
	public void setComm_title(String comm_title) {
		this.comm_title = comm_title;
	}
	public String getComm_keywords() {
		return comm_keywords;
	}
	public void setComm_keywords(String comm_keywords) {
		this.comm_keywords = comm_keywords;
	}
	public String getComm_htmlpath() {
		return comm_htmlpath;
	}
	public void setComm_htmlpath(String comm_htmlpath) {
		this.comm_htmlpath = comm_htmlpath;
	}
	
}
