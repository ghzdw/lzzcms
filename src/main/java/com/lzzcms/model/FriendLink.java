package com.lzzcms.model;


/**
 * 友情链接
 * @author zhao
 *
 */
public class FriendLink {
	private int id;
	private String linkDesc;//1：文字2：图片地址
	private Byte type ;//1:文字2：图片
	private String url;//链接地址
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLinkDesc() {
		return linkDesc;
	}
	public void setLinkDesc(String linkDesc) {
		this.linkDesc = linkDesc;
	}
	public Byte getType() {
		return type;
	}
	public void setType(Byte type) {
		this.type = type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
