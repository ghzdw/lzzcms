package com.lzzcms.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;
import org.hibernate.loader.custom.CustomLoader.ScalarResultColumnProcessor;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.lzzcms.dao.BackupDao;
import com.lzzcms.install.DbUtils;
import com.lzzcms.service.BackupService;
import com.lzzcms.utils.LzzConstants;
import com.lzzcms.utils.LzzcmsUtils;

@Service
public class BackupServiceImpl implements BackupService{
		private Logger logger=Logger.getLogger(BackupServiceImpl.class);
		
		@Resource
		private BackupDao dbDao;

		@Override
		public List<Map<String, Object>> listBackUps() {
			StringBuffer stringBuffer=new StringBuffer();
			stringBuffer.append(" select backup_id,backup_name,DATE_FORMAT(backup_date,'%Y-%m-%d %H:%i:%s') backup_date ");
			stringBuffer.append(" ,backup_user from lzz_backup order by ");
			stringBuffer.append(" DATE_FORMAT(backup_date,'%Y-%m-%d %H:%i:%s')  desc ");
			return dbDao.queryForList(stringBuffer.toString());
		}
		//清除path一级目录下的所有文件
		private void clearFiles(String path) {
			File file = new File(path);
			File[] files = file.listFiles();
			int len = files.length;
			for (int i = 0; i < len; i++) {
				File f = files[i];
				if(f.exists()){
					f.delete();
				}
			}
		}
		/*
		 * 生成_mysqllook.sql文件
		 */
		private String generateLook(File fileLook,Writer fw,BufferedWriter bw,List<String> tables){
			try {
				fw = new FileWriterWithEncoding(fileLook,"utf-8");
				bw=new BufferedWriter(fw);
				for(String tableName:tables){
					this.writeln(bw, "SET FOREIGN_KEY_CHECKS = 0"+LzzConstants.SQLDELIMITER);
					this.writeln(bw,"drop table if exists "+tableName+LzzConstants.SQLDELIMITER);
					String showCreateTableSql="show create  table "+tableName;
					String createTableSql = dbDao.queryforString(showCreateTableSql);
					createTableSql=createTableSql.toUpperCase().replaceAll("AUTO_INCREMENT=\\d{1,}", "");
					this.writeln(bw,createTableSql+LzzConstants.SQLDELIMITER);
					this.writeln(bw,"SET FOREIGN_KEY_CHECKS = 1"+LzzConstants.SQLDELIMITER);
				}
			} catch (IOException e) {
				logger.error(e);
				return e.getMessage();
			}finally{
				LzzcmsUtils.closeWriter(bw);
				LzzcmsUtils.closeWriter(fw);
			}
			return null;
		}
		/*
		 * 生成_mysqlexportinit.sql和_mysqlexportcont.sql文件
		 */
		private String generateData(File fileExportInit,File fileExportCont
				,Writer fwInit,Writer fw,BufferedWriter bwInit,BufferedWriter bw,List<String> tables){
			try {
				fwInit=new FileWriterWithEncoding(fileExportInit,"utf-8");
				fw = new FileWriterWithEncoding(fileExportCont,"utf-8");
				bwInit=new BufferedWriter(fwInit);
				bw=new BufferedWriter(fw);
				this.writeln(bwInit, "SET FOREIGN_KEY_CHECKS = 0"+LzzConstants.SQLDELIMITER);//外键时不能插入
				this.writeln(bw, "SET FOREIGN_KEY_CHECKS = 0"+LzzConstants.SQLDELIMITER);//外键时不能插入
				for(String tableName:tables){
					String sql="desc "+tableName;
					String fieldNames="";
					SqlRowSet descSet = dbDao.queryforSet(sql);
					Map<String, String> filedTypeMap=new HashMap<String, String>();
					while(descSet.next()){
						String fieldName = descSet.getString(1);
						String fieldType = descSet.getString(2);
						filedTypeMap.put(fieldName, fieldType);//{id=int(11)}, {ttttt=int(11)}, {comm_id=int(11)}, {ttttt2=varchar(0)}, {tttt3=varchar(0)}, {ttt4=varchar(23)}, {ttt45=varchar(23)}
						fieldNames+=fieldName+",";
					}
					fieldNames=fieldNames.substring(0,fieldNames.length()-1);//ID,COLNAME,COLTYPE,SHOWTIP,ADDITIONALTABLE,ALLOWNULL,DEFAULTVAL
					sql="select * from "+tableName;
					List<Map<String, Object>> tableDataList = dbDao.queryForList(sql);//一个表的数据
					int count=tableDataList.size();
					for (int i = 0; i < count; i++) {
						StringBuffer sb=new StringBuffer();
						sb.append("insert into "+tableName +" ("+fieldNames+") values (");
						Map<String, Object> oneData = tableDataList.get(i);
						String [] fieldArr= fieldNames.split(",");
						int len=fieldArr.length;
						for (int j = 0; j <len; j++) {
							String oneFieldName = fieldArr[j];//ALLOWNULL
							Object fieldValue =oneData.get(oneFieldName);
							if (fieldValue!=null) {
								String oneFieldType=filedTypeMap.get(oneFieldName);//tinyint(1)
								if(oneFieldType.toUpperCase().contains("INT")){//tinyint(1) 代表布尔类型,不能加''
									sb.append(fieldValue+",");
								}else {
									fieldValue=processSpecialChar(fieldValue.toString());
									sb.append("'"+fieldValue+"',");
								}
							}else {
									sb.append(fieldValue+",");
							}
						}
						sb=sb.deleteCharAt(sb.lastIndexOf(","));
						sb.append(")"+LzzConstants.SQLDELIMITER);
						if (tableName.equalsIgnoreCase("lzz_role")||tableName.equalsIgnoreCase("lzz_right")
							||tableName.equalsIgnoreCase("lzz_author")||tableName.equalsIgnoreCase("lzz_channelinfo")
						||tableName.equalsIgnoreCase("lzz_columntype")||tableName.equalsIgnoreCase("lzz_define_flag")
						||tableName.equalsIgnoreCase("lzz_extracolumnsdescforchl")
						||tableName.equalsIgnoreCase("lzz_source")||tableName.equalsIgnoreCase("lzz_channelinfo")
						||tableName.equalsIgnoreCase("lzz_systemparam")) {
							this.writeln(bwInit, sb.toString());
							continue;
						}
							this.writeln(bw, sb.toString());
					}
				}
				this.writeln(bwInit, "SET FOREIGN_KEY_CHECKS = 1"+LzzConstants.SQLDELIMITER);//恢复默认
				this.writeln(bw, "SET FOREIGN_KEY_CHECKS = 1"+LzzConstants.SQLDELIMITER);//恢复默认
			} catch (IOException e) {
				logger.error(e);
				return e.getMessage();
			}finally{
				LzzcmsUtils.closeWriter(bwInit);
				LzzcmsUtils.closeWriter(bw);
				LzzcmsUtils.closeWriter(fw);
				LzzcmsUtils.closeWriter(fwInit);
			}
			return null;
		}
		private Object processSpecialChar(String fieldValue) {
			return fieldValue.replace("'", "\\'");
		}
		/*
		 * 三个sql文件+uploads+tpls放到zip中
		 */
		private String generateZip(File fileLook,File fileExportInit,File fileExportCont,ServletContext sc
				,File retZipFile){
			OutputStream fos=null;
			ZipOutputStream zos=null;
			try {
				Map<String, File> pathFileMap=new HashMap<>();//backup/... uploads/... tpls/...
				pathFileMap.put("backup/"+fileLook.getName(),fileLook);
				pathFileMap.put("backup/"+fileExportInit.getName(),fileExportInit);
				pathFileMap.put("backup/"+fileExportCont.getName(),fileExportCont);
				String uploadsPath = sc.getRealPath("/uploads");//上传的文件 +缩略图
				File fileUploadsDir=new File(uploadsPath);
				recursionDir(pathFileMap,fileUploadsDir,"uploads");
				String tplsPath = sc.getRealPath("/tpls");//模板
				File fileTplsDir=new File(tplsPath);
				recursionDir(pathFileMap,fileTplsDir,"tpls");
				fos=new FileOutputStream(retZipFile);
				zos=new ZipOutputStream(fos);
				Set<String> keySet = pathFileMap.keySet();
				for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
					String path = iterator.next();
					InputStream fis=new FileInputStream(pathFileMap.get(path));
					zos.putNextEntry(new ZipEntry(path));
					int len=0;
					byte[] buff=new byte[1024];
					while ((len=fis.read(buff))!=-1) {
						zos.write(buff, 0, len);
					}
					LzzcmsUtils.closeIs(fis);
					zos.closeEntry();
				}
			} catch (Exception e) {
				logger.error("生成压缩文件出错:",e);
				return e.getMessage();
			}finally{
				LzzcmsUtils.closeOs(zos);
				LzzcmsUtils.closeOs(fos);
			}
			return null;
		}
		@Override
		public String backUp(ServletContext sc,String userName) {
			String backupRealPath = sc.getRealPath("/backup");
			File backupDirFile=new File(backupRealPath);
			if (!backupDirFile.exists()) {
				backupDirFile.mkdirs();
			}
			clearFiles(backupRealPath);//方法出异常了可能执行不到最后删除文件的地方
			String queryTbNamesql="select ts.table_name from  information_schema.tables ts where ts.table_schema=?";
			List<String> tables=dbDao.queryForListString(queryTbNamesql, LzzConstants.getInstance().getDbName());
			String crtDate=LzzcmsUtils.getPatternDateString(null, new Date());
			String fileNameLook=crtDate+"_mysqllook.sql";
			String fileNameExportInit=crtDate+"_mysqlexportinit.sql";//用户安装时要用 角色表(admin) 权限表 
			String fileNameExportCont=crtDate+"_mysqlexportcont.sql";//如果用户安装时选择安装数据，就读取这个，否则不读取
			File fileLook=new File(backupRealPath+"/"+fileNameLook);
			File fileExportInit=new File(backupRealPath+"/"+fileNameExportInit);
			File fileExportCont=new File(backupRealPath+"/"+fileNameExportCont);
			File retZipFile=new File(sc.getRealPath("/")+"备份文件"+crtDate+".zip");//备份文件的名字
			Writer fwInit=null;//写_mysqlexportinit文件的流
			BufferedWriter bwInit=null;//写_mysqlexportinit文件的流
			Writer fw=null;//写_mysqllook、_mysqlexportcont文件的流
			BufferedWriter bw=null;//写_mysqllook、_mysqlexportcont文件的流
			String ret =null;//返回值
			File file=new File(backupRealPath);
			if (!file.exists()) {
				file.mkdirs();
			}
			//把备份信息插入到数据库:这个要放在生成备份文件之前，不然就放不到备份的sql文件中去了
			this.updateBackUpTbAndZipFile(sc, userName, retZipFile);
			//写入表的创建语句
			 ret=this.generateLook(fileLook, fw, bw, tables);
			if (ret!=null) {
				return ret;
			}
			//写入表数据(内容这一部分cont+初始化这一部分init[安装必须安装的部分称之为初始化这一部分])的语句
			ret=this.generateData(fileExportInit,fileExportCont,fwInit,fw,bwInit,bw,tables);
			if (ret!=null) {
				return ret;
			}
			//上面生成的三个sql文件+uploads+tpls放到zip中
			ret=this.generateZip(fileLook, fileExportInit, fileExportCont, sc, retZipFile);
			if (ret!=null) {
				return ret;
			}
			//删除sql文件
			LzzcmsUtils.deleteFiles(fileLook,fileExportInit,fileExportCont);
			return ret;
		}
		/*1.更新备份表只保留最新的3条(现在的1条+"原先最新的两条")
		 * 2.删除掉名字不等于“原先最新的两条”的zip文件
		 */
		private void updateBackUpTbAndZipFile(ServletContext sc,String userName, File retZipFile) {
			StringBuffer sBuffer=new StringBuffer();
			sBuffer.append(" insert into lzz_backup(backup_name,backup_date,backup_user) ");
			sBuffer.append(" values(?,?,?) "); 
			dbDao.executeSql(sBuffer.toString(),retZipFile.getName(),new Date(),userName);
			sBuffer.setLength(0);
			sBuffer.append(" select count(*) count from lzz_backup ");
			Long totalCount = dbDao.queryForLong(sBuffer.toString());
			if (totalCount>3) {//确保只保留三条备份记录
				sBuffer.setLength(0);//删除最后一条的写法，不要写成limit 0,1
				sBuffer.append(" delete from lzz_backup order by backup_date asc limit 1 ");
				dbDao.executeSql(sBuffer.toString());
				sBuffer.setLength(0);//确保只保留3个备份文件
				sBuffer.append(" select  backup_name from lzz_backup  ");
				final List<String> backUpNames = dbDao.queryForListString(sBuffer.toString());
				String webRootRealPath=sc.getRealPath("/");
				File webRootFileDir=new File(webRootRealPath);
				File[] listFiles = webRootFileDir.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {//得到不在backUpNames中的zip文件
						if (FilenameUtils.getExtension(name).equals("zip")&&!backUpNames.contains(name)) {
							return true;
						}else {
							return false;
						}
					}
				});
				LzzcmsUtils.deleteFiles(listFiles);
			}
		}

		private void writeln(BufferedWriter bwInit, String string)
				throws IOException {
			bwInit.write(string);
			bwInit.newLine();
			bwInit.flush();
		}
	/**
	 * 	
	 * @param pathFileMap
	 * @param fileUploadsDir
	 * @param dirName:"uploads" tpls
	 * @throws IOException
	 */
	private void recursionDir(Map<String, File> pathFileMap, File fileUploadsDir, String dirName) throws IOException {
		File[] listFiles = fileUploadsDir.listFiles();
		for(File file:listFiles){
			if (file.isDirectory()) {
				recursionDir(pathFileMap, new File(file.getCanonicalPath()),dirName);
			}else {
				String canonicalPath = file.getCanonicalPath();//形如：f:\a\b\xx.docx
				canonicalPath=canonicalPath.replace("\\", "/");
				pathFileMap.put(canonicalPath.substring(canonicalPath.indexOf(dirName)), file);
			}
		}
	}
	@Override
	public String backTo(ServletContext servletContext, String backupName) {
		String backupPath = servletContext.getRealPath("/"+backupName);
		File zFile = new File(backupPath);
		ZipFile zipFile=null;
		String ret = null;//返回值
		try {
			zipFile=new ZipFile(zFile, "utf-8");
			Enumeration<ZipEntry> entries = zipFile.getEntries(); 
			while(entries.hasMoreElements()){
				 ZipEntry zipEntry = entries.nextElement();
				 String name = zipEntry.getName();//uploads/image/2017/05/1494602960930016387.png
				InputStream inputStream = zipFile.getInputStream(zipEntry);
				String realPath =servletContext.getRealPath("/"+name);
				 if (!name.endsWith(".sql")) {//uploads和模板
					 FileUtils.copyInputStreamToFile(inputStream, new File(realPath));
				}else {
					if (name.endsWith("_mysqllook.sql")) {
						 ret = executeSqlByFis(inputStream);
						if (ret!=null) {
							return ret;
						}
					}
				}
			}
			entries=zipFile.getEntries();
			while(entries.hasMoreElements()){
				 ZipEntry zipEntry = entries.nextElement();
				 String name = zipEntry.getName();//uploads/image/2017/05/1494602960930016387.png
				InputStream inputStream = zipFile.getInputStream(zipEntry);
					if (name.endsWith(".sql")&&!name.endsWith("_mysqllook.sql")) {//_mysqlexportinit  _mysqlexportcont
					    ret = executeSqlByFis(inputStream);
						if (ret!=null) {
							return ret;
						}
					}
			}
		} catch (IOException e) {
			logger.error(e);
			return e.getMessage();
		}finally{
			LzzcmsUtils.closeZipFile(zipFile);
		}
		return null;
	}

	private String executeSqlByFis(InputStream inputStream)
			throws UnsupportedEncodingException, IOException {
		PreparedStatement ps=null;
		InputStreamReader isr=new InputStreamReader(inputStream, "utf-8");
		BufferedReader br=new BufferedReader(isr);
		String line=null;
		StringBuffer sb=new StringBuffer();
		List<String>  sqlList=new ArrayList<String>();
		while(br.ready()){
			line=br.readLine();
			if(StringUtils.isBlank(line)){
				continue;
			}
			//生成sql文件的时候用的是writeln,如果内容过长，或是内容有分行，实际上还是分成多行来写了
			if (line.trim().endsWith(LzzConstants.SQLDELIMITER)) {
				sb.append(line.substring(0, line.lastIndexOf(LzzConstants.SQLDELIMITER)));
				sqlList.add(sb.toString());
				sb.setLength(0);
			}else {
				sb.append(line);
			}
		}
		Connection connection = DbUtils.getConn();
		String tmpString=null;
		try {
			connection.setAutoCommit(false);//对ddl不起作用，只对dml起作用
			for(String sql:sqlList){
				tmpString=sql;
				ps=connection.prepareStatement(sql);
				ps.executeUpdate();
			}
			connection.commit();
		} catch (Exception e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
				logger.error(e1);
				return e1.getMessage();
			}
			logger.error("执行sql:"+tmpString+",出错:",e);
			return e.getMessage();
		}finally{
			DbUtils.releasePs(ps);
			DbUtils.releaseConn(connection);
		}
		return null;
	}
}
