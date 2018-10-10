package com.lzzcms.dto;

import java.util.ArrayList;
import java.util.List;

public class GridDto<T> {
	private long total;
	private List<T> rows=new ArrayList<T>();
	
	public GridDto(){
		
	}
	public GridDto(int total, List<T> list) {
		this.total = total;
		if (list!=null) {
			this.rows = list;
		}
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public List<T> getRows() {
		return rows;
	}
	public void setRows(List<T> rows) {
		this.rows = rows;
	}
	
	
}
