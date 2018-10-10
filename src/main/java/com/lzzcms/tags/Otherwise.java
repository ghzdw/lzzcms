 package com.lzzcms.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;

/**
 * @author zhao:注意标签类是原型的
 */
public class Otherwise extends SimpleTagSupport{
	private static Logger logger=Logger.getLogger(Otherwise.class);
	 //用来获取其他子标签的属性。所以不需要set和get
    private boolean test;
    public void doTag() throws JspException, IOException {
        //获取父标签,choose下的子标签，获取的是同一个父对象。
         JspTag parent = this.getParent();
         if (parent instanceof Choose) {
			Choose choose=(Choose) parent;
			this.test = choose.isFlag();
			if(!test){
				this.getJspBody().invoke(null);
			}
		}else {
			logger.error("otherwise 只能是choose的子标签");
		}
    }
		
}
