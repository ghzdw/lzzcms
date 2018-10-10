package com.lzzcms.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeDto {
	private int id;
	private String text;
	private String state;
	private List<TreeDto> children=new ArrayList<TreeDto>();
	private boolean checked;
	private Map<String, String> attributes=new HashMap<String, String>();
	
	
	public TreeDto(){
		
	}
	public TreeDto(int id) {
		super();
		this.id = id;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		TreeDto other = (TreeDto) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public List<TreeDto> getChildren() {
		return children;
	}
	public void setChildren(List<TreeDto> children) {
		this.children = children;
	}
	public void addChild(TreeDto treeDto){
		this.children.add(treeDto);
	}
	public boolean getChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public Map<String, String> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	
}
