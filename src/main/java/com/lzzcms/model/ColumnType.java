package com.lzzcms.model;


/**
 * 栏目
 * @author zhao
 *
 */
public class ColumnType {
	private int id;
	private String typeName;
	private String enName;
	
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
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public ColumnType() {
		super();
	}
	public ColumnType(int id) {
		super();
		this.id = id;
	}
	
	
}
