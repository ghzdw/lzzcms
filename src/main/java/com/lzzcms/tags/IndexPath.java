 package com.lzzcms.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
<zdw:index_path/>取出网站首页地址
 * @author zhao
 */
public class IndexPath extends SimpleTagSupport{

	@Override
	public void doTag() throws JspException, IOException {
		int scope =  (int) getJspContext().getAttributesScope("basePath");//拦截器中设置的中在request中设置的
		String basePath = (String)getJspContext().getAttribute("basePath", scope);
		getJspContext().getOut().write(basePath);
		}
}
