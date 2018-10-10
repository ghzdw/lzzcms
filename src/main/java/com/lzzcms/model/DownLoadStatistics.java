package com.lzzcms.model;

import java.io.Serializable;
import java.util.Date;


public class DownLoadStatistics implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Date downDate;
	private String softName;
	private String ip;
	public String getSoftName() {
		return softName;
	}
	public void setSoftName(String softName) {
		this.softName = softName;
	}
	public Date getDownDate() {
		return downDate;
	}
	public void setDownDate(Date downDate) {
		this.downDate = downDate;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((downDate == null) ? 0 : downDate.hashCode());
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result
				+ ((softName == null) ? 0 : softName.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DownLoadStatistics other = (DownLoadStatistics) obj;
		if (downDate == null) {
			if (other.downDate != null)
				return false;
		} else if (!downDate.equals(other.downDate))
			return false;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		if (softName == null) {
			if (other.softName != null)
				return false;
		} else if (!softName.equals(other.softName))
			return false;
		return true;
	}
	
}
