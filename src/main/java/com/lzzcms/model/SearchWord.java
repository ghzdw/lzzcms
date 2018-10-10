package com.lzzcms.model;

import java.io.Serializable;
import java.util.Date;

public class SearchWord  implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Date searchDate;
	private String searchText;
	private int   searchCount;
	public Date getSearchDate() {
		return searchDate;
	}
	public void setSearchDate(Date searchDate) {
		this.searchDate = searchDate;
	}
	public String getSearchText() {
		return searchText;
	}
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
	public int getSearchCount() {
		return searchCount;
	}
	public void setSearchCount(int searchCount) {
		this.searchCount = searchCount;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((searchDate == null) ? 0 : searchDate.hashCode());
		result = prime * result
				+ ((searchText == null) ? 0 : searchText.hashCode());
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
		SearchWord other = (SearchWord) obj;
		if (searchDate == null) {
			if (other.searchDate != null)
				return false;
		} else if (!searchDate.equals(other.searchDate))
			return false;
		if (searchText == null) {
			if (other.searchText != null)
				return false;
		} else if (!searchText.equals(other.searchText))
			return false;
		return true;
	}
	
}
