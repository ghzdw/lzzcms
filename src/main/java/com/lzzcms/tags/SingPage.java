 package com.lzzcms.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.lzzcms.dao.ColumnInfoDao;
import com.lzzcms.listeners.SpringBeanFactory;
import com.lzzcms.model.ColumnInfo;

/**
 * <zdw:content />取得单页面的内容
 * @author zhao
 */
public class SingPage extends SimpleTagSupport{
	private String name;
	
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	@Override
	public void doTag() throws JspException, IOException {
		//2：request 3：session 0:不可用
		int scope =  (int) getJspContext().getAttributesScope("clnid");//makeOneCln中在request中设置的
		int clnid = (int)getJspContext().getAttribute("clnid", scope);
		ColumnInfoDao columnInfoDao = SpringBeanFactory.getBean("columnInfoDaoImpl", ColumnInfoDao.class);
		ColumnInfo cInfo = columnInfoDao.getEntityById(clnid);
		getJspContext().getOut().write(cInfo.getSingleContent());
	}
		
}
