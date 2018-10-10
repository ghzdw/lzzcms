package com.lzzcms.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class LzzResponseWrapper extends HttpServletResponseWrapper{
	private LzzPrintWriter pw=null;
	private ByteArrayOutputStream baos=null;
	public LzzResponseWrapper(HttpServletResponse response) {
		super(response);//父类没有无参构造函数，要显示调用
		baos=new ByteArrayOutputStream();
		pw=new LzzPrintWriter(baos);
	}
	@Override
	public PrintWriter getWriter() throws IOException {
		return pw;
	}
	//得到解析后准备返回到前端的html内容
	public String getResHtml(){
		String html=null;
		pw.flush();//还是要加上
		//读出来的时候就不要用new String(this.pw.getBaos().toByteArray(),"utf-8")了，以什么编码写入内存就默认以那个编码读出来就行
		html=pw.getBaos().toString();
		pw.closeRs();
		return html;
	}
}
