 package com.lzzcms.tags;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;

/**
 * @author zhao:注意标签类是原型的
 */
public class When extends SimpleTagSupport{
	private static Logger logger=Logger.getLogger(When.class);
    private boolean test;
    public void setTest(boolean test) {
        this.test = test;
    }
    public boolean isTest() {
		return test;
	}

	public void doTag() throws JspException, IOException {
        //获取父标签。choose下的子标签，获取的是同一个父对象。
    	 JspTag parent = this.getParent();
         if (parent instanceof Choose) {
			Choose choose=(Choose) parent;
			choose.setFlag(test||choose.isFlag());
			if(test){
				this.getJspBody().invoke(null);
			}
		}else {
			logger.error("when 只能是choose的子标签");
		}
    }
}
