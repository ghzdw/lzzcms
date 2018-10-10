 package com.lzzcms.tags;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.springframework.jdbc.core.JdbcTemplate;

import com.lzzcms.dao.ColumnInfoDao;
import com.lzzcms.listeners.SpringBeanFactory;

/**
 <zdw:nav >
			<a href="<zdw:field name='htmldir' />"><zdw:field name="name"/></a>
	</zdw:nav>
 * @author zhao
 */
public class Nav extends SimpleTagSupport{
	private  String type;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public void doTag() throws JspException, IOException {
		ColumnInfoDao columnInfoDao = SpringBeanFactory.getBean("columnInfoDaoImpl", ColumnInfoDao.class);
		StringBuffer sb=new StringBuffer();
		sb.append("select id,name,htmldir,personalstyle from lzz_columninfo");
		if ("top".equals(type)) {
			sb.append(" where parentid is null");
		}
		sb.append(" order by orderno asc ");
		List<Map<String, Object>> list = columnInfoDao.queryForList(sb.toString());
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);
			getJspContext().setAttribute("nav_cln2map",map);//默认放在pageScope里
			getJspBody().invoke(null);//调用标签体
		}
//		getJspContext().getOut().write("caonima");
	}
		
}
