 package com.lzzcms.tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang3.StringUtils;

import com.lzzcms.dao.ColumnInfoDao;
import com.lzzcms.dao.ContentInfoDao;
import com.lzzcms.listeners.SpringBeanFactory;
import com.lzzcms.model.ColumnInfo;
import com.lzzcms.service.StaticService;

/**
	<zdw:doclist>
	 		<zdw:field name="comm_title"/><br/>
	</zdw:doclist>
	
	 <zdw:sonlist>
	 	<zdw:field name="name"/><br/>
	 	<zdw:doclist>
	 		<zdw:field name="comm_title"/><br/>
	 	</zdw:doclist>
	 </zdw:sonlist>
 * @author zhao 
 * 文档(内容)列表
 */
public class DocList extends SimpleTagSupport{
	private int count=10;//取文档条数
	private String flag;//文档标记
	private String sort="asc";//asc或者desc
	private String sortby;//以哪个字段排序
	private String columns;
	
	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getSortby() {
		return sortby;
	}

	public void setSortby(String sortby) {
		this.sortby = sortby;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	@Override
	public void doTag() throws JspException, IOException {
		ContentInfoDao contentInfoDao = SpringBeanFactory.getBean("contentInfoDaoImpl", ContentInfoDao.class);
		StaticService staticService=SpringBeanFactory.getBean("staticServiceImpl", StaticService.class);
		ColumnInfoDao columnInfoDao=SpringBeanFactory.getBean("columnInfoDaoImpl", ColumnInfoDao.class);
		String indexOrlist_page =null;
		String finalClnId="-1";
		Object object = getJspContext().getAttribute("sonclnid");//SonList中设置的每一个子栏目的id
		JspTag parent = this.getParent();
		if (object==null) {//doclist没被sonlist包括
			//在封面/列表页面/单页面也可以使用clnlist,此时clnid也有值，导致scope!=0也成立，doclist被clnlist包括
			int scope_clnList =  (int) getJspContext().getAttributesScope("nowClnId");//ClnList中设置的
			if (scope_clnList!=0&&(parent instanceof ClnList)) {//本标签在clnlist中使用
				finalClnId=String.valueOf(getJspContext().getAttribute("nowClnId", scope_clnList));
			}else {//doclist单独使用,可能是在主页、封面、列表、单页面
				int scope =  (int) getJspContext().getAttributesScope("clnid");//makeOneCln中在request中设置的
				if (scope!=0) {//封面、列表、单页面
					finalClnId =String.valueOf(getJspContext().getAttribute("clnid", scope));
					ColumnInfo columnInfo = columnInfoDao.getEntityById(Integer.valueOf(finalClnId));
					if ("list".equals(columnInfo.getClnType().getEnName())) {//列表
						int now_list_pg_xScope =  (int) getJspContext().getAttributesScope("now_list_pg_x");//makeOneCln中在request中设置的
						indexOrlist_page = (String) getJspContext().getAttribute("now_list_pg_x", now_list_pg_xScope);
						Integer total = contentInfoDao.queryForInt("select count(*) from lzz_commoncontent where column_id=?", finalClnId); 
						int pageCount = new Double(Math.ceil(total/new Double(count))).intValue();
						//当total=0时pageCount=0，要改为1，不能共0页呀
						getJspContext().setAttribute("pageCount", pageCount==0?1:pageCount);//让pager使用,算pageCount要放在外面，不然list_pg的时候pager表签就无法取到值了
						getJspContext().setAttribute("indexOrlist_page", indexOrlist_page);//让pager使用
						if ("index".equals(indexOrlist_page)) {
							PageContext pageContext=  (PageContext) getJspContext();
							HttpServletRequest request= (HttpServletRequest) pageContext.getRequest();
							HttpServletResponse response= (HttpServletResponse) pageContext.getResponse();
							for (int i = 2; i <= pageCount; i++) {//pagecount=1时是index.html
								request.setAttribute("list_pg_x", "list_pg_"+i);//这个属性用作生成的列表页的名字
								Map<String, Object> cachedOutputMap = (Map<String, Object>) request.getAttribute("cachedOutputMap");
								staticService.makeOneCln(request, response, columnInfo,cachedOutputMap);
							}
						}
					}
				}//else{主页的时候
			}
		}else {//<zdw:doclist>本标签被sonlist包括在cover页使用
			finalClnId=String.valueOf(object);
		}
		
		StringBuffer sb=new StringBuffer();
		sb.append(" select cont.comm_id,cont.comm_title,cont.comm_shorttitle,cont.comm_click,DATE_FORMAT(cont.comm_publishdate,'%Y-%m-%d') comm_publishdate ");
		sb.append(" ,DATE_FORMAT(cont.comm_modifydate,'%Y-%m-%d') comm_modifydate ");
		sb.append(" ,cont.comm_keywords,cont.comm_desc,cont.comm_thumbpic,cont.comm_intro,cont.comm_htmlpath ");
		sb.append("  ,cln.name,chl.channelname,s_src.come_from,s_author.author_name ");
		sb.append(" ,s_defineflag.define_flag  ");
		sb.append(" from lzz_commoncontent cont LEFT JOIN lzz_columninfo  cln on cont.column_id=cln.id LEFT JOIN  lzz_channelinfo chl ON cln.channel_id=chl.id ");
		sb.append(" left join lzz_source s_src on cont.comm_src=s_src.id ");
		sb.append(" left join lzz_author s_author on cont.comm_author=s_author.id ");
		sb.append(" left join lzz_define_flag  s_defineflag on cont.comm_defineflag=s_defineflag.id ");
		List<Object> objects=new ArrayList<Object>();
		if (parent==null) {//doclist单独使用的时候可以使用columns属性
			if (StringUtils.isNotBlank(columns)) {
				if("all".equalsIgnoreCase(columns)){
					finalClnId="-1";
				}else {
					finalClnId="";
					String[] idsArr = columns.split("\\|");
					for (int i = 0; i < idsArr.length; i++) {
						finalClnId+=idsArr[i]+",";
					}
					finalClnId.substring(0, finalClnId.lastIndexOf(","));
				}
			}
		}
		if (!"-1".equals(finalClnId)) {
			sb.append(" where cont.column_id in (?) ");
			objects.add(finalClnId);
		}
		if (StringUtils.isNotBlank(flag)) {
			sb.append(getWhereOrAnd(sb.toString())+"  cont.comm_defineflag  = ? ");
			objects.add(flag);
		}
		if (StringUtils.isNotBlank(sortby)) {
			sb.append(" order by "+"cont.comm_"+sortby+" "+sort);
		}
		int pageNow=1;
		if (indexOrlist_page!=null&&!indexOrlist_page.equals("index")) {//比如：list_pg_3
			String[] split = indexOrlist_page.split("_");
			pageNow=Integer.valueOf(split[2]);
		}
		int pageSize=count;
		int start=(pageNow-1)*pageSize;
		sb.append(" limit "+start+","+pageSize);
		List<Map<String, Object>> list = contentInfoDao.queryForList(sb.toString(), objects.toArray());
		int len=list.size();
		for (int i = 0; i < len; i++) {
			Map<String, Object> map = list.get(i);
			map.put("index", (i+1));//加入文章序号
			getJspContext().setAttribute("doclist_oneDoc2Map",map);
			//if标签使用
			Set<String> mapkeySet = map.keySet();
			for (Iterator<String> iterator = mapkeySet.iterator(); iterator.hasNext();) {
				String mapKey = iterator.next();
				getJspContext().setAttribute(mapKey,map.get(mapKey));
			}
			getJspBody().invoke(null);//调用标签体
		}
	}
	//实现where 1=1的功能
	private String getWhereOrAnd(String string) {
		 String[] split = string.split("\\s+");
		 boolean flag=false;
		 for (int i = 0; i < split.length; i++) {
			if (split[i].equalsIgnoreCase("where")) {
				flag=true;
				break;
			}
		 }
		 if (flag) {
			return " and ";
		}else {
			return " where ";
		}
	}
		
}
