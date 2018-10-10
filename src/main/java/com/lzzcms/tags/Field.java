 package com.lzzcms.tags;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang3.StringUtils;

import com.lzzcms.dao.ColumnInfoDao;
import com.lzzcms.dao.SystemParamDao;
import com.lzzcms.listeners.SpringBeanFactory;
import com.lzzcms.utils.LzzConstants;
import com.lzzcms.utils.LzzcmsUtils;

/**
 * <zdw:field name="属性名"/>
 * 场景：
 * 1.<zdw:nav>
			<a href="<zdw:field name='htmldir' />"><zdw:field name="name"/></a>
	</zdw:nav>
	2.<zdw:doclist>
	 		<zdw:field name="comm_title"/><br/>
	</zdw:doclist>
 3.<zdw:sonlist>
	 	<zdw:field name="name"/><br/>
	 	<zdw:doclist>
	 		<zdw:field name="comm_title"/><br/>
	 	</zdw:doclist>
	 </zdw:sonlist>		
 * @author zhao:注意标签类是原型的
 */
public class Field extends SimpleTagSupport{
	private String name;//要获取的属性名称
	private Integer len;//截取后长度，不管是英文还是中文都是算1个长度
	private String  append;
	private String  sensitive;//区分中英文：yes  no
	
	public String getSensitive() {
		return sensitive;
	}


	public void setSensitive(String sensitive) {
		this.sensitive = sensitive;
	}


	public Integer getLen() {
		return len;
	}


	public void setLen(Integer len) {
		this.len = len;
	}


	public String getAppend() {
		return append;
	}


	public void setAppend(String append) {
		this.append = append;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	@Override
	public void doTag() throws JspException, IOException {
		PageContext jspContext = (PageContext)getJspContext();
		JspTag parent = this.getParent();
		Map<String, Object> map  =null;
		if (parent!=null) {//field被包裹
			if (parent instanceof Nav) {//field在nav标签中使用
				 map  =(Map<String, Object>) jspContext.getAttribute("nav_cln2map");
			} else if (parent instanceof SonList) {//field在sonlist标签中使用
				map  =(Map<String, Object>)jspContext.getAttribute("sonlist_oneSonCln2Map");
			}else if (parent instanceof DocList) {//field在doclist标签中使用
				 map  =(Map<String, Object>) jspContext.getAttribute("doclist_oneDoc2Map");
			}else if (parent instanceof ClnList) {//field在clnlist标签中使用
				 map  =(Map<String, Object>) jspContext.getAttribute("clnList_oneCln2Map");
			}else if (parent instanceof PreDoc||parent instanceof NextDoc) {//field在pre或next标签中使用,一定在内容页
				 map  =(Map<String, Object>) jspContext.getAttribute("preOrNextCont2Map");
			}else {//在when标签里，choose--when---otherwise可以在relations(不用管)、doclist、clnlist、sonlist、内容页使用
				map  =(Map<String, Object>)jspContext.getAttribute("sonlist_oneSonCln2Map");
				if (map==null) {
					 map  =(Map<String, Object>) jspContext.getAttribute("doclist_oneDoc2Map");
					 if (map==null) {
						 map  =(Map<String, Object>) jspContext.getAttribute("clnList_oneCln2Map");
						 if (map==null) {
							 int s =  (int) jspContext.getAttributesScope("onecont");//生成文档的地方设置的
							 if (s!=0) {
								 map  =(Map<String, Object>)  jspContext.getAttribute("onecont",s);
							}
						}
					}
				}
			}
		}else {//field标签单独使用
			SystemParamDao systemParamDao = SpringBeanFactory.getBean("systemParamDaoImpl", SystemParamDao.class);
			//s=0:不存在该属性 
			int s =  (int) jspContext.getAttributesScope("onecont");//生成文档的地方设置的
			if (s!=0) {//内容页面.譬如head或foot里面仍旧有取<zdw:field name="indexname"/>这种非栏目信息的
				 map  =(Map<String, Object>)  jspContext.getAttribute("onecont", s);
				  //取出页面配置信息放到已经存在的map里
				 getPagecfg(systemParamDao,map);
			}else {//非内容页面
				int scope =  (int) jspContext.getAttributesScope("clnid");//makeOneCln中在request中设置的
				if (scope==0) {//主页
					map=new HashMap<String, Object>();
					getPagecfg(systemParamDao,map);
				}else {//取栏目信息.譬如head或foot里面仍旧有取<zdw:field name="indexname"/>这种非栏目信息的
					int clnid = (int)jspContext.getAttribute("clnid", scope);
					ColumnInfoDao columnInfoDao = SpringBeanFactory.getBean("columnInfoDaoImpl", ColumnInfoDao.class);
					StringBuffer sb=new StringBuffer();
					sb.append("select * from lzz_columninfo where id=?");
					map =columnInfoDao.queryForMap(sb.toString(), clnid);
					//取出页面配置信息放到已经存在的map里
					getPagecfg(systemParamDao, map);
				}
			}
		}
		this.renderVal(map);
	}


	private void getPagecfg(SystemParamDao systemParamDao,Map<String, Object> targetMap){
		StringBuffer sb=new StringBuffer();
	    sb.setLength(0);
		sb.append("select paramname,paramvalue from lzz_systemparam s where s.groupname='pagecfg'");
		List<Map<String, Object>> queryForList = systemParamDao.queryForList(sb.toString());
		for(Map<String, Object> m:queryForList){
			targetMap.put(String.valueOf(m.get("paramname")), m.get("paramvalue"));
		}
	}
	private void renderVal(Map<String, Object> map) throws IOException {
		int scope =  (int) getJspContext().getAttributesScope("basePath");//拦截器里在request中设置的
		String basePath = (String)getJspContext().getAttribute("basePath", scope);
		if ("url".equals(name)) {//页面上传来的url，即取htmldir或者outlink，栏目的地址
			Object object = map.get("htmldir");
			String url=null;
			if (object==null) {
				url=map.get("outlink").toString();
			}else {
				url=object.toString();
				url=basePath+"/"+url;
			}
			getJspContext().getOut().write(url);
		}else if ("docurl".equals(name)) {//包括在doclist标签中,文档的地址
			String comm_htmlpath= map.get("comm_htmlpath")==null?"":map.get("comm_htmlpath").toString();
			if (comm_htmlpath.startsWith("javascript:")) {//上一篇，下一篇的时候
				getJspContext().getOut().write(comm_htmlpath);
			}else {
				getJspContext().getOut().write(basePath+"/"+comm_htmlpath);
			}
		}else if ("comm_thumbpic".equals(name)) {
			String comm_thumbpic= map.get("comm_thumbpic").equals(LzzConstants.THUMB_UPLOAD_PRE)?"/resources/imgs/default.png":map.get("comm_thumbpic").toString();
			getJspContext().getOut().write(basePath+comm_thumbpic);
		}else {
			Object nameObj = map.get(name);
			String value=(nameObj==null)?"":nameObj.toString();
			Integer valueLen=value.length();
			if (len!=null) {
				if ("yes".equalsIgnoreCase(sensitive)) {//中英文敏感
					value=LzzcmsUtils.getSubStringSensitive(value, len,append);
				}else if ("no".equalsIgnoreCase(sensitive)
						||StringUtils.isBlank(sensitive)) {//不区分中英文，中英文都按1来数
					if (valueLen>len) {
						value=value.substring(0, len);
						if (append!=null) {
							value+=append;
						}
					}
				}
			}
			getJspContext().getOut().write(value);
		}
	}
}
