 package com.lzzcms.tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.lzzcms.dao.ColumnInfoDao;
import com.lzzcms.listeners.SpringBeanFactory;
import com.lzzcms.model.ColumnInfo;

/**
	<zdw:pager/>
 * @author zhao 
 * 分页标签:只在列表页使用
 * 	<nav>
	  <ul class="pagination pagination-sm">
		<li>
		  <a href="#" >
			<span >&laquo;</span>
		  </a>
		</li>
		<li><a href="#">1</a></li>
		<li class="active"><a href="#" >2</a></li>
		<li><a href="#">3</a></li>
		<li><a href="#">4</a></li>
		<li><a href="#">5</a></li>
		<li>
		  <a href="#" >
			<span >&raquo;</span>
		  </a>
		</li>
	  </ul>
	</nav>
 */
public class Pager extends SimpleTagSupport{
	@Override
	public void doTag() throws JspException, IOException {
		     ColumnInfoDao columnInfoDao=SpringBeanFactory.getBean("columnInfoDaoImpl", ColumnInfoDao.class);
			int scope =  (int) getJspContext().getAttributesScope("clnid");//makeOneCln中在request中设置的
			if (scope!=0) {//<zdw:doclist>标签在列表页使用
				int basePathScope =  (int) getJspContext().getAttributesScope("basePath");//拦截器中设置的中在request中设置的
				String basePath = (String)getJspContext().getAttribute("basePath", basePathScope);
				int finalClnId = (int)getJspContext().getAttribute("clnid", scope);
				ColumnInfo columnInfo = columnInfoDao.getEntityById(finalClnId);
				PageContext pageContext=  (PageContext) getJspContext();
				HttpServletRequest request= (HttpServletRequest) pageContext.getRequest();
				int pageCount=(int) getJspContext().getAttribute("pageCount");
				String nowPage =(String) getJspContext().getAttribute("indexOrlist_page");//index list_pg_2 list_pg_3
				int num=1;
				if (!"index".equals(nowPage)) {
					num=getListPgNum(nowPage);
				}
				StringBuffer sb=new StringBuffer();
				sb.append("<nav class='pagernav' >");
				sb.append("<ul class=\"pagerul\">");
				if (num!=1) {
					String preHref=basePath+"/"+columnInfo.getHtmlDir()+"/";
					sb.append("<li onclick='pagerPrev()' class='pagerpre'>");
					sb.append("<a href='"+preHref+getPrevHref(nowPage)+"' >");
					sb.append("<span >&laquo;</span>");
					sb.append("</a>");
					sb.append("</li>");
				}
				if (num<=10) {//前10页
					int lenlt10=10;
					if (pageCount<=10) {
						lenlt10=pageCount;
					}
					for (int i = 1; i <= lenlt10; i++) {//分页按钮一页显示最多显示10个
						String preHref=basePath+"/"+columnInfo.getHtmlDir()+"/";
						if (i==num) {
							sb.append("<li class='pagernum active'>");
						}else {
							sb.append("<li class='pagernum'>");
						}
						preHref+=i==1?"index.html":"list_pg_"+i+".html";
						sb.append("<a href='"+preHref+"' >"+i+"</a></li>");
					}
				}else{//num>10，前10页后边的
					List<String> list=new ArrayList<>();
					for (int i = num; i >= (num-9); i--) {//分页按钮一页显示最多显示10个
						String preHref=basePath+"/"+columnInfo.getHtmlDir()+"/";
						StringBuffer sBuffer=new StringBuffer();
						if (i==num) {
							sBuffer.append("<li class='pagernum active'>");
						}else {
							sBuffer.append("<li class='pagernum'>");
						}
						preHref+="list_pg_"+i+".html";
						sBuffer.append("<a href='"+preHref+"' >"+i+"</a></li>");
						list.add(sBuffer.toString());
					}
					Collections.reverse(list);
					int size = list.size();
					for (int i = 0; i < size; i++) {
						sb.append(list.get(i));
					}
				}
				if (num!=pageCount) {
					String preHref=basePath+"/"+columnInfo.getHtmlDir()+"/";
					sb.append("<li  class='pagernext'>");
					sb.append("<a href='"+preHref+getNextHref(nowPage)+"'>");
					sb.append("<span >&raquo;</span>");
					sb.append("</a>");
					sb.append("</li>");
				}
				//共多少页
				sb.append("<li class='pagertotal'>");
				sb.append("<span >共"+pageCount+"页</span>");
				sb.append("</li>");
				sb.append("</ul>");
				sb.append("</nav>");
				//css:active和hiddenele
				sb.append("<style type='text/css'>.hiddenele{display:none;}</style>");
				getJspContext().getOut().write(sb.toString());
			}else{//不是栏目模版
				throw new RuntimeException("请在列表页模版中使用分页标签pager");
			}
		
	}

	private int getListPgNum(String nowPage) {
		String[] split = nowPage.split("_");
		return Integer.valueOf(split[2]);
	}

	private String getNextHref(String nowPage) {
		if (!"index".equals(nowPage)) {
			int num=getListPgNum(nowPage);
			return "list_pg_"+(num+1)+".html";
		}else{
			return "list_pg_2.html";
		}
	}

	private String getPrevHref(String nowPage) {//此时传来的一定不是index
		int num=getListPgNum(nowPage);
		if (num==2) {
			return "index.html";
		}else {
			return "list_pg_"+(num-1)+".html";
		}
	}
		
}
