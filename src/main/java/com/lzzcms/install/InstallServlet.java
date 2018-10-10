package com.lzzcms.install;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerMapping;

import com.lzzcms.utils.LzzConstants;
import com.lzzcms.utils.LzzcmsUtils;

/**
 * 安装处理
 */
public class InstallServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    Logger logger=Logger.getLogger(InstallServlet.class);   
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String flag=request.getParameter("flag");
		if (StringUtils.isBlank(flag)) {
			response.sendRedirect("install/stepone.jsp");
		}else if("step2".equals(flag)){
			String hostName= request.getServerName(); 
			String osName=System.getProperty("os.name");
			String osVersion=System.getProperty("os.version");
			String jdkVersion=System.getProperty("java.version");
			String serverType=ServerType.getServerId();
			request.setAttribute("hostName", hostName);
			request.setAttribute("os", osName+osVersion);
			request.setAttribute("jdkVersion", jdkVersion);
			request.setAttribute("serverType", serverType);
			request.getRequestDispatcher("install/steptwo.jsp").forward(request, response);
		}else if("step3".equals(flag)){
			response.sendRedirect("install/stepthree.jsp");
		}else if("cmtDbInfo".equals(flag)){
			String dbServerIp = request.getParameter("dbServerIp");
			String dbPort = request.getParameter("dbPort");
			String dbName = request.getParameter("dbName");
			String dbAccount = request.getParameter("dbAccount");
			String dbPwd = request.getParameter("dbPwd");
			String retStr="ok";
			Connection connection=DbUtils.getConnnection(dbServerIp, dbPort, dbName, dbAccount, dbPwd);
			if (connection!=null) {//数据库已经存在
				DbUtils.setVar(dbServerIp, dbPort, dbName, dbAccount, dbPwd);
				String sql="drop database if exists "+dbName;
				PreparedStatement ps=null;
				try {//删除并创建数据库
					connection.setAutoCommit(false);//默认是true
					ps=connection.prepareStatement(sql);
					ps.executeUpdate();
					DbUtils.releasePs(ps);
					sql="create database "+dbName+" CHARACTER SET UTF8 ";
					ps=connection.prepareStatement(sql);
					ps.executeUpdate();
					DbUtils.releasePs(ps);
					connection.commit();
				} catch (SQLException e) {
					try {
						connection.rollback();
					} catch (SQLException e1) {
						logger.error("回滚出错",e1);
						retStr=e1.getMessage();
					}
					logger.error("执行"+sql+"出错:",e);
					retStr=e.getMessage();
				}finally{
					DbUtils.releaseConn(connection);
				}
				//删除了原来的再重新创建了，重新得到一次连接
				connection=DbUtils.getConnnection(dbServerIp, dbPort, dbName, dbAccount, dbPwd);
			    retStr = initData(request, connection);
			}else {
				 connection=DbUtils.getConnnection(dbServerIp, dbPort, null, dbAccount, dbPwd);
				 if (connection!=null) {
					 String sql="create database "+dbName+" CHARACTER SET UTF8";
					 PreparedStatement ps=null;
					 try {//创建数据库
						connection.setAutoCommit(false);
						ps = connection.prepareStatement(sql);
						ps.executeUpdate();
						DbUtils.releasePs(ps);
						connection.commit();
					 } catch (SQLException e) {
						try {
							connection.rollback();
						} catch (SQLException e1) {
							logger.error("回滚出错",e1);
							retStr=e1.getMessage();
						}
						logger.error("执行"+sql+"出错:",e);
						retStr=e.getMessage();
					 }finally{
						DbUtils.releaseConn(connection);
					 }
					//新创建了数据库，重新得到一次连接
					 connection=DbUtils.getConnnection(dbServerIp, dbPort, dbName, dbAccount, dbPwd);
					if (connection!=null) {
						 DbUtils.setVar(dbServerIp, dbPort, dbName, dbAccount, dbPwd);
					}else {
						retStr="创建数据库失败";
					}
					 retStr=initData(request, connection);
				}else {
					retStr="数据库连接创建失败，请检查地址、端口、用户名、密码";
				}
			}
			outPut(response,retStr);
		}else if("done".equals(flag)){//重命名安装文件
			renameInstallFiles(request,"stepone");
			renameInstallFiles(request,"steptwo");
			renameInstallFiles(request,"stepthree");
			String backstage = request.getParameter("backstage");
			//在这里存储设置的变量到文件param.properties中，避免以前多次存储多次重启服务器的问题
			DbUtils.storeToFile();
			//处理web.xml
			String now = LzzcmsUtils.getRealPath(request,"/WEB-INF/web.xml");
			String web_bakPath = LzzcmsUtils.getRealPath(request,"/WEB-INF/web_bak.xml");
			String web_bakString = FileUtils.readFileToString(new File(web_bakPath), "utf-8");
			web_bakString=web_bakString.replaceAll("@backstage@", backstage);
			//这个需要在storeToFile之后啊
			Connection connection=null;
			PreparedStatement ps=null;
			String sql="insert into lzz_systemparam(paramname,paramvalue,groupname) values('servletPath','/"+backstage+"','initconstants')";
			try {
				connection=DbUtils.getConn();
				connection.setAutoCommit(false);
				ps=connection.prepareStatement(sql);
				ps.executeUpdate();
				connection.commit();
				FileUtils.write(new File(now), web_bakString, "utf-8");
			} catch (Exception e) {
				logger.error("保存后台管理路径和替换部署描述符出错:",e);
			}finally {
				DbUtils.releasePs(ps);
				DbUtils.releaseConn(connection);
			}
			
			String contextPath = request.getContextPath();
			String scheme = request.getScheme();
			String serverName = request.getServerName();
			int serverPort = request.getServerPort();
			String basePath=null;
			if (serverPort==80) {
				basePath=scheme+"://"+serverName+contextPath;
			}else{
				basePath=scheme+"://"+serverName+":"+serverPort+contextPath;
			}
			//替换ueditor配置文件config.json的访问前缀
			String cfgJsonPath = LzzcmsUtils.getRealPath(request,"/resources/ueditor/jsp/config.json");
			String cfgJsonString = FileUtils.readFileToString(new File(cfgJsonPath), "utf-8");
			cfgJsonString=cfgJsonString.replaceAll("@contextPath@", contextPath);
			FileUtils.write(new File(cfgJsonPath), cfgJsonString, "utf-8");
			
			request.setAttribute("basePath", basePath);
			request.setAttribute("backstagePath", basePath+"/"+backstage);
			request.getRequestDispatcher("install/stepfour.jsp").forward(request, response);
		}
	}

	private String renameInstallFiles(HttpServletRequest request,String installFileBaseName)
			throws IOException {
		String originalPath = LzzcmsUtils.getRealPath(request,"/install/"+installFileBaseName+".jsp");
		File srcFile = new File(originalPath);
		if (srcFile.exists()) {
			String destPath=LzzcmsUtils.getRealPath(request,
				"/install/"+FilenameUtils.getBaseName(originalPath)+"_"+LzzcmsUtils.random()+".jsp");
			FileUtils.moveFile(srcFile, new File(destPath));
			return FilenameUtils.getBaseName(destPath);
		}
		return null;
	}
	
	private void outPut(HttpServletResponse response,String resString){
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain;charset=utf-8");
		PrintWriter writer=null;
		try {
			writer = response.getWriter();
			writer.print(resString);//ln前端就不等于ok了，即便返回的是ok
			writer.flush();
		} catch (IOException e) {
			logger.error(e);
		}finally{
			if (writer!=null) {
				writer.close();
			}
		}
		
	}
	private String initData(HttpServletRequest request, Connection connection)
			throws FileNotFoundException, UnsupportedEncodingException,
			IOException {
		PreparedStatement ps=null;//先执行生成表结构的sql
		List<String>  lookSqlList=new ArrayList<String>();
		List<String>  intSqlList=new ArrayList<String>();
		String adminName = request.getParameter("adminName");
		String adminRealName = request.getParameter("adminRealName");
		String adminPwd = LzzcmsUtils.getEncryptResult(request.getParameter("adminPwd"));
		String dbName = request.getParameter("dbName");
		addToList(request,"/install/initSqlFile/look.sql",lookSqlList);
		//再执行生成表初始化数据的sql
		addToList(request,"/install/initSqlFile/init.sql",intSqlList);
		//执行
		try {
			for(String ddlSql:lookSqlList){
				ps=connection.prepareStatement(ddlSql);
				ps.executeUpdate();
			}
			connection.setAutoCommit(false);//对ddl不起作用，只对dml起作用
			for(String sql:intSqlList){
				ps=connection.prepareStatement(sql);
				ps.executeUpdate();
			}
			//读到的文件中文不是乱码，通过jdbc加入数据库中就成乱码了，需要修改my.ini:character-set-server=utf8 default-character-set=utf8
			String sql="insert into lzz_admininfo(id,username,realname,pword) values(1,'"+adminName+"','"+adminRealName+"','"+adminPwd+"');";
			ps=connection.prepareStatement(sql);
			ps.executeUpdate();
			sql="insert into link_admin_role(admin_id,role_id) values(1,1)";
			ps=connection.prepareStatement(sql);
			ps.executeUpdate();//保存数据库名到lzz_systemparam系统配置表
			sql="insert into lzz_systemparam(paramname,paramvalue,groupname) values('dbName','"+dbName+"','initconstants')";
			ps=connection.prepareStatement(sql);
			ps.executeUpdate();
			connection.commit();
		} catch (Exception e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
				logger.error(e1);
			}
			logger.error(e);
		}finally{
			DbUtils.releasePs(ps);
			DbUtils.releaseConn(connection);
		}
		return "ok";
	}

	private void addToList(HttpServletRequest request, String fileName,List<String> listToAdd) {
		String realPath = LzzcmsUtils.getRealPath(request,fileName);
		File file=new File(realPath);
		FileInputStream fis=null;
		InputStreamReader isr=null;
		BufferedReader br=null;
		String line=null;
		StringBuffer sb=new StringBuffer();
		try {
			fis=new FileInputStream(file);
			isr=new InputStreamReader(fis, "utf-8");
			br=new BufferedReader(isr);
			while(br.ready()){
				line=br.readLine();
				if(StringUtils.isBlank(line)){
					continue;
				}
				if (line.trim().endsWith(LzzConstants.SQLDELIMITER)) {
					sb.append(line.substring(0, line.lastIndexOf(LzzConstants.SQLDELIMITER)));
					listToAdd.add(sb.toString());
					sb.setLength(0);
				}else {
					sb.append(line);
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}finally {
			LzzcmsUtils.closeReader(br);
			LzzcmsUtils.closeReader(isr);
			LzzcmsUtils.closeIs(fis);
		}
	}

}
