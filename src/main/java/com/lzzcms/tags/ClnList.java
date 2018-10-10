 package com.lzzcms.tags;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Where;

import com.lzzcms.dao.ColumnInfoDao;
import com.lzzcms.listeners.SpringBeanFactory;

/**
 <zdw:clnlist>
	 	<zdw:field name="name"/><br/>
	 	<zdw:doclist>
	 		<zdw:field name="comm_title"/><br/>
	 	</zdw:doclist>
	 </zdw:clnlist>
 * @author zhao 
 * 跟sonlist类似，只不过本标签通过指定栏目id取出栏目信息，而sonlist默认取正在生成栏目的子栏目
 */
public class ClnList extends SimpleTagSupport{
	private String ids;//栏目id，多个通过|分割
	private String type;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}
	@Override
	public void doTag() throws JspException, IOException {
		StringBuffer sb=new StringBuffer();
		sb.append("select cln.id,cln.htmldir,cln.name,COUNT(c.COLUMN_ID) docnum from lzz_columninfo cln  ");
		sb.append(" LEFT JOIN lzz_commoncontent c ON cln.ID=c.COLUMN_ID  ");
		if (StringUtils.isNotBlank(ids)) {
			appendType(sb);
			sb.append(" where cln.id in (  ");
			String[] idsArr = ids.split("\\|");
			for (int i = 0; i < idsArr.length; i++) {
				sb.append(idsArr[i]+",");
			}
			sb.deleteCharAt(sb.lastIndexOf(","));
			sb.append(")");
			if (StringUtils.isNotBlank(type)) {
				sb.append(" and clntype.enname='"+type+"' ");
			}
		}else {
			appendType(sb);
			if (StringUtils.isNotBlank(type)) {
				sb.append(" where clntype.enname='"+type+"' ");
			}
		}
		sb.append(" GROUP BY cln.id,cln.htmldir,cln.name  ");
		ColumnInfoDao columnInfoDao = SpringBeanFactory.getBean("columnInfoDaoImpl", ColumnInfoDao.class);
		List<Map<String, Object>> list =columnInfoDao.queryForList(sb.toString());
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);
			map.put("index", (i+1));//加入栏目序号
			getJspContext().setAttribute("clnList_oneCln2Map",map);
			getJspContext().setAttribute("nowClnId",Integer.valueOf(map.get("id").toString()));//栏目id
			//if标签使用
			Set<String> mapkeySet = map.keySet();
			for (Iterator<String> iterator = mapkeySet.iterator(); iterator.hasNext();) {
				String mapKey = iterator.next();
				getJspContext().setAttribute(mapKey,map.get(mapKey));
			}
			getJspBody().invoke(null);//调用标签体
		}
	}
	private void appendType(StringBuffer sb) {
		if (StringUtils.isNotBlank(type)) {
			sb.append(" join lzz_columntype clntype on cln.clntype=clntype.id  ");
		}
	}
		
}
