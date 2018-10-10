package com.lzzcms.model;

import java.util.HashSet;
import java.util.Set;

import com.lzzcms.model.ChannelInfo;

/**
 * 每种模型都有些除了共有字段之外的其他字段，本类保存的就是这些字段的信息
 * @author 赵道稳
 *
 */
public class ExtraColumnsDescForChl {
	private int id;
	private String colName;//保存附加表的列名称，如mainbody
	private String colType;
	private String showTip;//显示在页面上时的提示信息，如“内容主体”，“图片宽度”，“软件大小”等
	private boolean allowNull=true;
	private String defaultVal;
	private String additionalTable;//字段所属附加表,如addforarticle
	
	public boolean isAllowNull() {
		return allowNull;
	}
	public void setAllowNull(boolean allowNull) {
		this.allowNull = allowNull;
	}
	public String getDefaultVal() {
		return defaultVal;
	}
	public void setDefaultVal(String defaultVal) {
		this.defaultVal = defaultVal;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getColName() {
		return colName;
	}
	public void setColName(String colName) {
		this.colName = colName;
	}
	public String getColType() {
		return colType;
	}
	public void setColType(String colType) {
		this.colType = colType;
	}
	public String getShowTip() {
		return showTip;
	}
	public void setShowTip(String showTip) {
		this.showTip = showTip;
	}
	public String getAdditionalTable() {
		return additionalTable;
	}
	public void setAdditionalTable(String additionalTable) {
		this.additionalTable = additionalTable;
	}
	
	
}
