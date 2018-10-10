package com.lzzcms.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class LzzPrintWriter  extends PrintWriter{
	private Logger logger=Logger.getLogger(LzzPrintWriter.class);
	private ByteArrayOutputStream baos=null;
	public LzzPrintWriter(ByteArrayOutputStream out) {
		//这一句super导致父类的属性PrintStream用out初始化了，返回到前端调用
		//out.write或者out.print的时候就是写入到内存中了
		super(out, true);//writer不用再显式调用flush
		this.baos=out;
	}
	public ByteArrayOutputStream getBaos() {
		return baos;
	}
	public void closeRs() {
		if (this!=null) {
			this.close();
		}
		if (baos!=null) {
			try {
				baos.close();
				IOUtils.closeQuietly(baos);
				baos.reset();
				baos=null;
			} catch (IOException e) {
				logger.info("关闭baos出错",e);
			}
		}
	}
}
