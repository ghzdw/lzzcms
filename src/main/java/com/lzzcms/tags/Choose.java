 package com.lzzcms.tags;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * @author zhao:注意标签类是原型的
 */
public class Choose extends SimpleTagSupport{
	//不是标签属性，是子标签之间传递参数用的。所以需要get，set方法。
    private boolean flag;
    public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	@Override
    public void doTag() throws JspException, IOException {
        this.getJspBody().invoke(null);
    }
		
}
