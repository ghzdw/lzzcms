 package com.lzzcms.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 <zdw:resources_path/>bootstrap-3.3.5-dist
 * @author zhao
 */
public class TplPath extends SimpleTagSupport{

	@Override
	public void doTag() throws JspException, IOException {
		//0无    1.page  2：request   3.session  4.application 
		int scope =  (int) getJspContext().getAttributesScope("basePath");//拦截器中设置的中在request中设置的
		String basePath = (String)getJspContext().getAttribute("basePath", scope);
		getJspContext().getOut().write(basePath+"/"+"tpls");
		}
}
