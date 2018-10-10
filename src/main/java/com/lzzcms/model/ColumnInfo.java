package com.lzzcms.model;

import java.util.HashSet;
import java.util.Set;

import com.lzzcms.model.ChannelInfo;

/**
 * 栏目
 * @author zhao
 *
 */
public class ColumnInfo {
	private int id;
	private String name;
	private ChannelInfo channelInfo;
	private Set<CommonContent> contents;
	private ColumnInfo columnInfo;//可能有父级，多队一
	private int orderNo;
	private String htmlDir;//即栏目url
	//不同类型的栏目对应的默认模版：封面栏目(cover_模型标识.html),列表栏目(list_模型标识.html),外部链接，单页面（single_page.html）
	//private String clnType;//栏目类别
	private ColumnType clnType;
	private String outLink;//外部链接地址
	private String singleContent;//单页面的内容
	//private String indexTplName;//封面栏目对应的模版名称
	//private String listTplName;//列表栏目对应得模版名称
	private String myTpl;//本栏目对应的模版。当选择栏目类型后，出来自己默认的模版，可自己设置.(实际上是封面、列表、单页面之一)
	private String contentTplName;//该栏目下的内容文档对应得模版名称，默认(content_模型标识).(封面、列表都有)
	private String clnTitle;//栏目页面标题
	private String clnKeyWords;//栏目关键字
	private String clnDesc;//栏目网页描述
	private String personalStyle;//用于设置css,如伪类<i class="glyphicon glyphicon-book"></i>  或者单独的glyphicon-book
	private String commHtmlIsUpdated;
	
	public String getCommHtmlIsUpdated() {
		return commHtmlIsUpdated;
	}
	public void setCommHtmlIsUpdated(String commHtmlIsUpdated) {
		this.commHtmlIsUpdated = commHtmlIsUpdated;
	}
	public String getPersonalStyle() {
		return personalStyle;
	}
	public void setPersonalStyle(String personalStyle) {
		this.personalStyle = personalStyle;
	}
	public int getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(int orderNo) {
		this.orderNo = orderNo;
	}
	public String getHtmlDir() {
		return htmlDir;
	}
	public void setHtmlDir(String htmlDir) {
		this.htmlDir = htmlDir;
	}
	public ColumnType getClnType() {
		return clnType;
	}
	public void setClnType(ColumnType clnType) {
		this.clnType = clnType;
	}
	public String getOutLink() {
		return outLink;
	}
	public void setOutLink(String outLink) {
		this.outLink = outLink;
	}
	
	public String getMyTpl() {
		return myTpl;
	}
	public void setMyTpl(String myTpl) {
		this.myTpl = myTpl;
	}
	public String getContentTplName() {
		return contentTplName;
	}
	public void setContentTplName(String contentTplName) {
		this.contentTplName = contentTplName;
	}
	public String getClnTitle() {
		return clnTitle;
	}
	public void setClnTitle(String clnTitle) {
		this.clnTitle = clnTitle;
	}
	public String getClnKeyWords() {
		return clnKeyWords;
	}
	public void setClnKeyWords(String clnKeyWords) {
		this.clnKeyWords = clnKeyWords;
	}
	public String getClnDesc() {
		return clnDesc;
	}
	public void setClnDesc(String clnDesc) {
		this.clnDesc = clnDesc;
	}
	public String getSingleContent() {
		return singleContent;
	}
	public void setSingleContent(String singleContent) {
		this.singleContent = singleContent;
	}
	public ColumnInfo getColumnInfo() {
		return columnInfo;
	}
	public void setColumnInfo(ColumnInfo columnInfo) {
		this.columnInfo = columnInfo;
	}
	public Set<CommonContent> getContents() {
		return contents;
	}
	public void setContents(Set<CommonContent> contents) {
		this.contents = contents;
	}
	public ChannelInfo getChannelInfo() {
		return channelInfo;
	}
	public void setChannelInfo(ChannelInfo channelInfo) {
		this.channelInfo = channelInfo;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ColumnInfo() {
		super();
	}
	public ColumnInfo(int id) {
		super();
		this.id = id;
	}
	
}
