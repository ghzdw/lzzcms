 package com.lzzcms.tags;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Where;

import com.lzzcms.dao.ColumnInfoDao;
import com.lzzcms.dao.FriendLinkDao;
import com.lzzcms.listeners.SpringBeanFactory;
import com.lzzcms.model.FriendLink;
import com.lzzcms.service.FriendLinkService;
import com.lzzcms.service.impl.FriendLinkServiceImpl;

/**
 <zdw:flinks type="img/text">
	 	<zdw:flink name="linkdesc"/>
 </zdw:flinks>
 * @author zhao 
 * 跟sonlist类似，只不过本标签通过指定栏目id取出栏目信息，而sonlist默认取正在生成栏目的子栏目
 */
public class Flinks extends SimpleTagSupport{
	private String type;//默认为text
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public void doTag() throws JspException, IOException {
		Byte flag=1;
		StringBuffer sb=new StringBuffer();
		sb.append(" from FriendLink where type=? ");
		if (StringUtils.isNotBlank(type)) {
			if ("img".equals(type)) {
				flag=2;//2代表图片类型的链接
			}
		}
//		FriendLinkDao friendLinkDao = SpringBeanFactory.getBean("friendLinkDaoImpl", FriendLinkDao.class);
//		List<FriendLink> list =friendLinkDao.findByHql(sb.toString(),flag);
		
		FriendLinkService friendLinkService = SpringBeanFactory.getBean("friendLinkServiceImpl", FriendLinkService.class);
		List<FriendLink> list =friendLinkService.getFlinks(sb.toString(),flag);
		for (int i = 0; i < list.size(); i++) {
			FriendLink friendLink = list.get(i);
			getJspContext().setAttribute("oneFriendLink",friendLink);
			getJspBody().invoke(null);//调用标签体
		}
	}
}
