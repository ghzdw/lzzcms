 package com.lzzcms.tags;

import java.io.IOException;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * <zdw:path/>
 * @author zhao:注意标签类是原型的
 */
public class Path extends SimpleTagSupport{
	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void doTag() throws JspException, IOException {
		Map<String, String> map = (Map<String, String>)getJspContext().getAttribute("pathitem");
		getJspContext().getOut().write(map.get(name));
	}
		
}
