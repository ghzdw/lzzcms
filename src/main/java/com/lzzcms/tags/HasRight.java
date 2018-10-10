 package com.lzzcms.tags;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.lzzcms.model.AdminInfo;
import com.lzzcms.model.RightInfo;

/**
 后台使用,判断是否含有某个权限
 */
public class HasRight extends SimpleTagSupport{
	private String url; 
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext)getJspContext();
		HttpServletRequest request=(HttpServletRequest) pageContext.getRequest();
		HttpSession session = request.getSession();
		ServletContext sc = session.getServletContext();
		Map<String, Object> rightMap = (Map<String, Object>) sc.getAttribute("all_rights_map");
		RightInfo right= (RightInfo) rightMap.get(url);
		AdminInfo adminInfo = (AdminInfo) session.getAttribute("admin");
		if (adminInfo.hasRight(right)) {
			getJspBody().invoke(null);//调用标签体
		}
	}
		
}
