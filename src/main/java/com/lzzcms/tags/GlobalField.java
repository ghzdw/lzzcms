 package com.lzzcms.tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.lzzcms.dao.ColumnInfoDao;
import com.lzzcms.dao.SystemParamDao;
import com.lzzcms.listeners.SpringBeanFactory;
import com.lzzcms.model.ColumnInfo;
import com.lzzcms.utils.LzzConstants;

/**
 */
public class GlobalField extends SimpleTagSupport{
	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void doTag() throws JspException, IOException {
		SystemParamDao systemParamDao = SpringBeanFactory.getBean("systemParamDaoImpl", SystemParamDao.class);
		StringBuffer sBuffer=new StringBuffer();
		sBuffer.append(" select s.id,s.paramname,s.paramvalue,s.groupname from lzz_systemparam s ");
		sBuffer.append("  where s.groupname=? and s.paramname=? ");
		List<Map<String, Object>> list = systemParamDao.queryForList(sBuffer.toString(),
				LzzConstants.GLOBAL,name);
		if (list!=null&&list.size()==1) {
			Map<String, Object> map = list.get(0);
			Object object = map.get("paramvalue");
			String value=object==null?"":object.toString();
			getJspContext().getOut().write(value);
		}else {
			getJspContext().getOut().write("");
		}
	}
		
}
