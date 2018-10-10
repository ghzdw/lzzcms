 package com.lzzcms.tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang3.StringUtils;

import com.lzzcms.dao.ColumnInfoDao;
import com.lzzcms.dao.ContentInfoDao;
import com.lzzcms.listeners.SpringBeanFactory;
import com.lzzcms.service.StaticService;

/**
	 <zdw:results>
	 	<zdw:searchitem name="name"/><br/>
	 </zdw:results>
 * @author zhao 
 * 取出搜索出来的列表中的某一项的内容
 */
public class SearchItem extends SimpleTagSupport{
	private String name;//文档标记

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void doTag() throws JspException, IOException {
		int scope =  (int) getJspContext().getAttributesScope("searchItem");//SearchResults中设置的
		if (scope!=0) {
			Map<String, Object>  map= (Map<String, Object>) getJspContext().getAttribute("searchItem", scope);
			Object object = map.get(name);
			getJspContext().getOut().write(String.valueOf(object));
		}else {
			throw new RuntimeException("只能在搜索结果页面使用zdw:searchitem");
		}
	}
		
}
