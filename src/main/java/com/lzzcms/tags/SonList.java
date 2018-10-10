 package com.lzzcms.tags;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.lzzcms.dao.ColumnInfoDao;
import com.lzzcms.listeners.SpringBeanFactory;

/**
 <zdw:sonlist>
	 	<zdw:field name="name"/><br/>
	 	<zdw:doclist>
	 		<zdw:field name="comm_title"/><br/>
	 	</zdw:doclist>
	 </zdw:sonlist>
 * @author zhao 
 * 取出一个栏目的子栏目
 */
public class SonList extends SimpleTagSupport{
	
	@Override
	public void doTag() throws JspException, IOException {
		int scope =  (int) getJspContext().getAttributesScope("clnid");//makeOneCln中在request中设置的
		int clnid = (int)getJspContext().getAttribute("clnid", scope);
		ColumnInfoDao columnInfoDao = SpringBeanFactory.getBean("columnInfoDaoImpl", ColumnInfoDao.class);
		StringBuffer sb=new StringBuffer();
		sb.append("select id,htmldir,name from lzz_columninfo where parentid=?");
		List<Map<String, Object>> list =columnInfoDao.queryForList(sb.toString(), clnid);
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);
			map.put("index", (i+1));//加入栏目序号
			getJspContext().setAttribute("sonlist_oneSonCln2Map",map);
			getJspContext().setAttribute("sonclnid",map.get("id"));//子栏目id
			//if标签使用
			Set<String> mapkeySet = map.keySet();
			for (Iterator<String> iterator = mapkeySet.iterator(); iterator.hasNext();) {
				String mapKey = iterator.next();
				getJspContext().setAttribute(mapKey,map.get(mapKey));
			}
			getJspBody().invoke(null);//调用标签体
		}
	}
		
}
