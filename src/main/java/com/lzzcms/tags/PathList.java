 package com.lzzcms.tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang3.StringUtils;

import com.lzzcms.dao.ColumnInfoDao;
import com.lzzcms.dao.ContentInfoDao;
import com.lzzcms.dao.SystemParamDao;
import com.lzzcms.listeners.SpringBeanFactory;

/**
	<zdw:pathlist>
	 		<zdw:path/>
	</zdw:pathlist>
 * @author zhao 
 * 面包屑导航
 */
public class PathList extends SimpleTagSupport{

	@Override
	public void doTag() throws JspException, IOException {
		int clnIdScope =  (int) getJspContext().getAttributesScope("clnid");//makeOneCln中在request中设置的
		if (clnIdScope!=0) {//本标签在列表页或者封面页或单页面（三种合起来即在栏目中使用）使用
			List<Map<String, String>> paths=new ArrayList<Map<String, String>>();
			int clnid = (int)getJspContext().getAttribute("clnid", clnIdScope);
			getColNameAndPid(clnid,paths);
			for(Map<String, String> m:paths){
				getJspContext().setAttribute("pathitem",m);
				getJspBody().invoke(null);//调用标签体
			}
		}else {
			int oneContScope =  (int) getJspContext().getAttributesScope("onecont");//生成文档的地方设置的
			if (oneContScope!=0) {//内容页
				Map<String, Object> onecont  =(Map<String, Object>)  getJspContext().getAttribute("onecont", oneContScope);
				String contTitle = onecont.get("comm_title").toString();
				String basePath = getBasePath();
				List<Map<String, String>> paths=new ArrayList<Map<String, String>>();
				Map<String, String> mapToPut=new HashMap<String,String>();
				mapToPut.put("name", contTitle);
				mapToPut.put("link", basePath+"/"+onecont.get("comm_htmlpath").toString());
				paths.add(0,mapToPut);
				Integer cont_colid = Integer.valueOf(onecont.get("column_id").toString());
				getColNameAndPid(cont_colid,paths);
				for(Map<String, String> m:paths){
					getJspContext().setAttribute("pathitem",m);
					getJspBody().invoke(null);//调用标签体
				}
			}else {//主页
				SystemParamDao systemParamDao = SpringBeanFactory.getBean("systemParamDaoImpl", SystemParamDao.class);
				StringBuffer sb=new StringBuffer();
				sb.append("select paramvalue from lzz_systemparam where groupname='pagecfg' and paramname='indexname' ");
				String indexname = systemParamDao.queryForString(sb.toString());
				Map<String, String> mapToPut=new HashMap<String, String>();
				mapToPut.put("name", indexname);
				String basePath = getBasePath();
				mapToPut.put("link", basePath);
				getJspContext().setAttribute("pathitem",mapToPut);
				getJspBody().invoke(null);//调用标签体
			}
		}
	}

	private void getColNameAndPid(int finalClnId,List<Map<String, String>> paths) {
		StringBuffer sb=new StringBuffer();
		sb.append("select parentid,name,htmldir from lzz_columninfo where id=? ");
		ColumnInfoDao columnInfoDao = SpringBeanFactory.getBean("columnInfoDaoImpl", ColumnInfoDao.class);
		Map<String, Object> colMap = columnInfoDao.queryForMap(sb.toString(), finalClnId);
		Map<String, String> mapToPut=new HashMap<String,String>();
		String basePath = getBasePath();
		mapToPut.put("name", colMap.get("name").toString());
		mapToPut.put("link", basePath+"/"+colMap.get("htmldir").toString());
		paths.add(0,mapToPut);
		if (colMap.get("parentid")!=null) {
			getColNameAndPid(Integer.valueOf(colMap.get("parentid").toString()),paths);
		}
	}

	private String getBasePath() {
		int scope =  (int) getJspContext().getAttributesScope("basePath");//拦截器中设置的中在request中设置的
		String basePath = (String)getJspContext().getAttribute("basePath", scope);
		return basePath;
	}
		
}
