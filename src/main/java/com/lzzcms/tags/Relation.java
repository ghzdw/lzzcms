 package com.lzzcms.tags;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * <zdw:relation/>
 * @author zhao:注意标签类是原型的
 */
public class Relation extends SimpleTagSupport{
	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	//"0.4@zdw@s/@zdw@xxx@zdw@1"
	@Override
	public void doTag() throws JspException, IOException {
		String oneRelation = (String)getJspContext().getAttribute("relation");
		JspWriter out = getJspContext().getOut();
		String[] split = oneRelation.split("@zdw@");
		if ("docurl".equals(name)) {
			out.write(split[1]);
		}else if ("comm_title".equals(name)) {
			out.write(split[2]);
		}else if ("index".equals(name)) {
			out.write(split[3]);
		}else {
			out.write("可以用的name值:docurl(文档连接)、comm_title(文档标题)、index(文档下标)");
		}
	}
		
}
