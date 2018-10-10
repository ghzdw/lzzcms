 package com.lzzcms.tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.lzzcms.dao.ColumnInfoDao;
import com.lzzcms.listeners.SpringBeanFactory;

/**
 <zdw:results>
	 	<zdw:searchitem name="name"/><br/>
	 </zdw:results>
 * @author zhao 
 * 取出搜索出来的列表
 */
public class SearchResults extends SimpleTagSupport{
	
	@Override
	public void doTag() throws JspException, IOException {
		int scope =  (int) getJspContext().getAttributesScope("searchList");//SearchServiceImpl中在request中设置的.放入null的话仍然scope=0
		if (scope!=0) {
			List<Map<String, Object>>  list= (List<Map<String, Object>>) getJspContext().getAttribute("searchList", scope);
			int size = list.size();
			if (size>0) {
				for (int i = 0; i < size; i++) {
					Map<String, Object> map = list.get(i);
					getJspContext().setAttribute("searchItem",map);
					getJspBody().invoke(null);//调用标签体
				}
			}else {
				getJspContext().getOut().write("没有你想要的结果");//直接把标签体覆盖了
			}
			
		}else {
			throw new RuntimeException("只能在搜索结果页面使用zdw:results");
		}
	}
		
}
