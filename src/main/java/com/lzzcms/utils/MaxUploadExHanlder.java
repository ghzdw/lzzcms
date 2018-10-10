package com.lzzcms.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

//@Component
public class MaxUploadExHanlder implements HandlerExceptionResolver{

	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		 ModelAndView mv=new ModelAndView("upload_error");
		 if (ex instanceof MaxUploadSizeExceededException) {
//			 String callback = request.getParameter("CKEditorFuncNum");  //ckedit上传文件传递过来的
//			 response.setCharacterEncoding("utf-8");
//			 response.setContentType("text/html;charset=utf-8");
//			 StringBuffer sb=new StringBuffer();
//			sb.append("<script type=\"text/javascript\">");      
//			sb.append("window.parent.CKEDITOR.tools.callFunction(" + callback + ",'','文件大小不得大于5M');");     
//			sb.append("</script>"); 
//			try {
//				response.getWriter().write(sb.toString());
//				response.getWriter().flush();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			 mv.addObject("upload_error","大小不能超过5M");
		}
		return mv;
	}

}
