 package com.lzzcms.tags;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.annotations.Where;

import com.lzzcms.dao.ColumnInfoDao;
import com.lzzcms.dao.FriendLinkDao;
import com.lzzcms.listeners.SpringBeanFactory;
import com.lzzcms.model.FriendLink;
import com.lzzcms.utils.LzzcmsUtils;

/**
 <zdw:flinks type="img/text">
	 	<zdw:flink name="linkdesc"/>
 </zdw:flinks>
 * @author zhao 
 * 跟sonlist类似，只不过本标签通过指定栏目id取出栏目信息，而sonlist默认取正在生成栏目的子栏目
 */
public class Flink extends SimpleTagSupport{
	private static Logger logger=Logger.getLogger(Flink.class);
	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void doTag() throws JspException, IOException {
		FriendLink friendLink= (FriendLink) getJspContext().getAttribute("oneFriendLink");
		Class<FriendLink> flClazz = (Class<FriendLink>) friendLink.getClass();
		Field[] declaredFields = flClazz.getDeclaredFields();
		String val="";
		for (int i = 0; i < declaredFields.length; i++) {
			Field field = declaredFields[i];
			field.setAccessible(true);
			if (name.toLowerCase().equals(field.getName().toLowerCase())) {
				try {
					val=String.valueOf(field.get(friendLink));
				} catch (Exception e) {
					logger.error("反射取值错误",e);
					throw new RuntimeException("反射取值错误");
				} 
			}
		}
		getJspContext().getOut().write(val);
	}
}
