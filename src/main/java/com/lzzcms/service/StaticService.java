package com.lzzcms.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lzzcms.dto.TreeDto;
import com.lzzcms.model.ColumnInfo;




public interface StaticService {
	String makeIndex( HttpServletRequest request, HttpServletResponse response);

	String makeCln(HttpServletRequest request, HttpServletResponse response, String clnid);

	String makeCont(HttpServletRequest request, HttpServletResponse response,
			String clnid)throws Exception;
	String makeOneCln(HttpServletRequest request,HttpServletResponse response,ColumnInfo cInfo,Map<String, Object> cachedOutputMap);
		/**
		 * 检验是否包含<zdw:include file='xxx.html'/>标签，有则使用标签包含的文件内容替换掉标签本身，否则返回原字符串src
		 * @param src
		 * @return
		 * SearchServiceImpl中要使用故改为public
		 */
	 String includeFile(HttpServletRequest request,String src);

		List<TreeDto> getTpls(HttpServletRequest request);
	String makeContForComment(String docid,HttpServletRequest request,HttpServletResponse response);
}
