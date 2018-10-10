 package com.lzzcms.tags;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.lzzcms.dao.ContentInfoDao;
import com.lzzcms.listeners.SpringBeanFactory;

/**
 * 下一个，只在内容页使用
	<zdw:next>
		<zdw:field name="docurl"/><zdw:field name="comm_title"/>
	</zdw:next>
 */
public class NextDoc extends SimpleTagSupport{
	@Override
	public void doTag() throws JspException, IOException {
		int s =  (int) getJspContext().getAttributesScope("onecont");//生成文档的地方设置的
		if (s!=0) {//内容页面
			Map<String, Object> nowCont  =(Map<String, Object>)  getJspContext().getAttribute("onecont", s);
			StringBuffer sbBuffer=new StringBuffer();
			sbBuffer.append(" select comm_title,comm_htmlpath  from lzz_commoncontent where comm_id>? and column_id=? order by comm_id asc limit 0,1");
			ContentInfoDao contentInfoDao = SpringBeanFactory.getBean("contentInfoDaoImpl", ContentInfoDao.class);
			Map<String, Object> preCont2Map=null;
			try {
				preCont2Map = contentInfoDao.queryForMap(sbBuffer.toString(), Integer.valueOf(nowCont.get("cont_comm_id").toString())
						, Integer.valueOf(nowCont.get("column_id").toString()));
			} catch (Exception e) {//Incorrect result size: expected 1, actual 0
				preCont2Map=new HashMap<String, Object>();
				preCont2Map.put("comm_title","没有了" );
				preCont2Map.put("comm_htmlpath","javascript:void(0);" );
			}
			getJspContext().setAttribute("preOrNextCont2Map", preCont2Map);
			getJspBody().invoke(null);
		}else {//本标签不允许在非内容页使用
			throw new RuntimeException("next不允许在非内容页使用");
		}
	}
}
