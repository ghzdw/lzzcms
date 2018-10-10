package com.lzzcms.utils;

/**
 * 分页助手
 * @author zhao
 *
 */
public class PageUtil {
	private int pageNow;
	private int pageSize;
	private boolean needPage=false;//是否需要分页
	
	public boolean isNeedPage() {
		return needPage;
	}
	public void setNeedPage(boolean needPage) {
		this.needPage = needPage;
	}
	public int getPageNow() {
		return pageNow;
	}
	public void setPageNow(int pageNow) {
		this.pageNow = pageNow;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
}