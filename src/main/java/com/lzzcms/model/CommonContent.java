package com.lzzcms.model;

import java.util.Date;

/**
 * 抽取出所有频道/模型（channel）的共有属性(文档的),即存放文档内容的公共表
 * @author zhao
 */
public class CommonContent {
	private int id;
	private String title;
	private String shortTitle;//简略标题
	private Integer click;
	private SystemParam author;
	private Date publishDate;
	private String keyWords;
	private String desc;//网页里的描述
	private SystemParam src;//来源
	private String thumbPic;
	private SystemParam defineFlag;//头条，置顶，推荐，幻灯，特荐，滚动等
	private String intro;//内容简介，商品简介等
	private ColumnInfo columnInfo;//属于哪个栏目
	private String htmlPath;//html文件所在路径
	public String getHtmlPath() {
		return htmlPath;
	}
	public void setHtmlPath(String htmlPath) {
		this.htmlPath = htmlPath;
	}
	public ColumnInfo getColumnInfo() {
		return columnInfo;
	}
	public void setColumnInfo(ColumnInfo columnInfo) {
		this.columnInfo = columnInfo;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getShortTitle() {
		return shortTitle;
	}
	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}
	public Integer getClick() {
		return click;
	}
	public void setClick(Integer click) {
		this.click = click;
	}
	public SystemParam getAuthor() {
		return author;
	}
	public void setAuthor(SystemParam author) {
		this.author = author;
	}
	public Date getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}
	public String getKeyWords() {
		return keyWords;
	}
	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public SystemParam getSrc() {
		return src;
	}
	public void setSrc(SystemParam src) {
		this.src = src;
	}
	public String getThumbPic() {
		return thumbPic;
	}
	public void setThumbPic(String thumbPic) {
		this.thumbPic = thumbPic;
	}
	public SystemParam getDefineFlag() {
		return defineFlag;
	}
	public void setDefineFlag(SystemParam defineFlag) {
		this.defineFlag = defineFlag;
	}
	public String getIntro() {
		return intro;
	}
	public void setIntro(String intro) {
		this.intro = intro;
	}
}
