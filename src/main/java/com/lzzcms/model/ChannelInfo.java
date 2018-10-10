package com.lzzcms.model;


/**
 * 频道：如普通文章，软件下载，商品等
 * @author zhao
 *
 */
public class ChannelInfo {
	private int id;
	private String channelName;
	private String commonTable;
	private String additionalTable; 
	private String enName;//跟cover,list,content组合生成模版名。
	
	public String getEnName() {
		return enName;
	}
	public void setEnName(String enName) {
		this.enName = enName;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getChannelName() {
		return channelName;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	public String getCommonTable() {
		return commonTable;
	}
	public void setCommonTable(String commonTable) {
		this.commonTable = commonTable;
	}
	public String getAdditionalTable() {
		return additionalTable;
	}
	public void setAdditionalTable(String additionalTable) {
		this.additionalTable = additionalTable;
	}
	public ChannelInfo(){
		
	}
	public ChannelInfo(int id){
		this.id=id;
	}
}
