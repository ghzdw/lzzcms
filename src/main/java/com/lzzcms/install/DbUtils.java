package com.lzzcms.install;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Properties;
/**
 * 数据库操作工具类
 * @author zhao
 *
 */


import org.apache.log4j.Logger;
public class DbUtils {
	private static String PROP_FILE_NAME="param.properties";
    private static String MYSQL_DRIVER="com.mysql.jdbc.Driver";
    private static Properties props=new Properties();
    private static String userName=null;
    private static String password=null;
    private static Logger logger=Logger.getLogger(DbUtils.class);
    private static String  ip=null;
    private static String port=null;
    private static String db=null;  
    public static void setVar(String dbServerIp, String dbPort, String dbName, String dbAccount, String dbPwd){
    	ip=dbServerIp;
    	port=dbPort;
    	db=dbName;
    	userName=dbAccount;
    	password=dbPwd;
    }
    public static void storeToFile(){
		FileOutputStream fos=null;
		try {
			Properties properties=new Properties();
    	    InputStream is = DbUtils.class.getClassLoader().getResourceAsStream(PROP_FILE_NAME);
    	    properties.load(is);
			properties.setProperty("c3p0.jdbcUrl", "jdbc:mysql://"+ip+":"+port+"/"+db);
			properties.setProperty("c3p0.user",userName);
			properties.setProperty("c3p0.password",password);
			String path = DbUtils.class.getClassLoader().getResource(PROP_FILE_NAME).getPath();
			fos=new FileOutputStream(path);
			properties.store(fos, null);
			fos.flush();
		} catch (Exception e1) {
			logger.error("存储到"+PROP_FILE_NAME+"文件出错",e1);
		}finally{
			try {
				if (fos!=null) {
					fos.close();
				}
			} catch (IOException e) {
				logger.error("关闭输出流出错",e);
			}
		}
    }
	public static Connection getConnnection(String dbServerIp,String dbPort,String dbName,String dbAccount,String dbPwd) {
		Connection conn=null;
		try {
			String jdbcUrl=null;
			if (dbName!=null) {
				jdbcUrl="jdbc:mysql://"+dbServerIp+":"+dbPort+"/"+dbName;
			}else {
				jdbcUrl="jdbc:mysql://"+dbServerIp+":"+dbPort;
			}
			Class.forName(MYSQL_DRIVER);
			conn=DriverManager.getConnection(jdbcUrl,dbAccount,dbPwd);
		} catch (Exception e1) {
			logger.error(e1);
			e1.printStackTrace();
		}
		return conn;
	}
	//替换了param.properties和web.xml重启了，需要从文件里读取
	public static Connection getConn() {
		Connection conn=null;
    	InputStream is = DbUtils.class.getClassLoader().getResourceAsStream(PROP_FILE_NAME);
		try {
			props.load(is);
			String jdbcUrl=(String) props.get("c3p0.jdbcUrl");
	    	String dbUser=(String) props.get("c3p0.user");
	    	String	dbPass=(String) props.get("c3p0.password");
			Class.forName(MYSQL_DRIVER);
			conn=DriverManager.getConnection(jdbcUrl,dbUser,dbPass);
		} catch (Exception e1) {
			logger.error(e1);
		}
		return conn;
	}
	/**
	 * 关闭数据库连接
	 * @param connection
	 */
	public static void releaseConn(Connection connection) {
		 if (connection!=null) {
			 try {
				 if (!connection.isClosed()) {
						connection.close();
					}
			} catch (Exception e) {
				logger.error("关闭数据库连接出错:",e);
			}
		}
	}
	/**
	 * 关闭ps
	 * @param ps
	 */
	public static void releasePs(PreparedStatement ps) {
		if (ps!=null) {
			try {
				if (!ps.isClosed()) {
					ps.close();
				}
			} catch (Exception e) {
				logger.error("关闭ps:",e);
			}
		}
	}
}
