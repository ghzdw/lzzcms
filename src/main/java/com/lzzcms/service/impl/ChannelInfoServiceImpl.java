package com.lzzcms.service.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Delayed;

import javax.annotation.Resource;
import javax.print.DocFlavor.STRING;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.lzzcms.dao.ChannelInfoDao;
import com.lzzcms.dto.TreeDto;
import com.lzzcms.model.ChannelInfo;
import com.lzzcms.service.ChannelInfoService;
import com.lzzcms.tags.Field;
import com.lzzcms.utils.LzzConstants;
import com.lzzcms.utils.PageContext;

@Service
public class ChannelInfoServiceImpl implements ChannelInfoService{
	private Logger logger=Logger.getLogger(ChannelInfoServiceImpl.class);
	@Resource
	private ChannelInfoDao channelInfoDao;
	public ChannelInfoDao getChannelInfoDao() {
		return channelInfoDao;
	}
	public void setChannelInfoDao(ChannelInfoDao channelInfoDao) {
		this.channelInfoDao = channelInfoDao;
	}
	@Override
	public List<Map<String, Object>> trueList(Map<String, Object> paramMap) {
		StringBuffer sb=new StringBuffer();
		sb.append(" select id,channelname,commontable,additionaltable,enname from lzz_channelinfo");
		if (paramMap.get("sortField")!=null&&paramMap.get("sortDirection")!=null) {
			sb.append(" order by "+paramMap.get("sortField")+" "+paramMap.get("sortDirection"));
		}
		if (PageContext.getPageUtil().isNeedPage()) {
			int pageNow=PageContext.getPageUtil().getPageNow();
			int pageSize=PageContext.getPageUtil().getPageSize();
			int start=(pageNow-1)*pageSize;
			sb.append(" limit "+start+","+pageSize);
		}
		return channelInfoDao.queryForList(sb.toString());
	}
	@Override
	public List<Map<String, Object>> getForCombobox() {
		StringBuffer sb=new StringBuffer();
		sb.append("select id,channelname,enname,additionaltable from lzz_channelinfo");
		return channelInfoDao.queryForList(sb.toString());
	}
	@Override
	public Map<String, Object> toAddChannelInfo() {
		String sql="select case when c.id is not null then  max(c.id)+1 else 1 end from lzz_channelinfo c";
		Integer maxChannelId= channelInfoDao.queryForInt(sql);
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("maxChannelId", String.valueOf(maxChannelId));
		map.put("channelName", "频道"+String.valueOf(maxChannelId));
		map.put("commonTableName", "lzz_commoncontent");
		map.put("additionalTableName", "lzz_addforchannel"+String.valueOf(maxChannelId));
		map.put("enName", "channel"+String.valueOf(maxChannelId));
		return map;
	}
	@Override
	public void trueAddChannelInfo(ChannelInfo channelInfo) {
		channelInfoDao.saveEntity(channelInfo);
	}
	public ChannelInfo getChannelById(Integer id) {
		return channelInfoDao.getEntityById(id);
	}
	
	
	public void trueEditChannelInfo(ChannelInfo channelInfo) throws Exception{
//		ChannelInfo original = channelInfoDao.getEntityById(channelInfo.getId());
		String originalTbName = channelInfoDao.queryForString("select additionaltable from lzz_channelinfo where id=? ",channelInfo.getId());
		StringBuffer sb=new StringBuffer();
		sb.append(" delete from  lzz_extracolumnsdescforchl  where additionaltable=?");
		//更新附加表的字段信息,待优化为主键外键
		channelInfoDao.executeSql(sb.toString(),originalTbName);
		//新旧oid一样，出错,跟注释的第一句连用的话
		/*
		 *  update
		        lzz_channelinfo 
		    set
		        channelname=?,
		        commontable=?,
		        additionaltable=?,
		        enname=? 
		    where
		        id=?
		 */
		//channelInfoDao.updateEntity(channelInfo);
		/*
		 *  update
		        lzz_channelinfo 
		    set
		        additionaltable=?,
		        enname=? 
		    where
		        id=?
		 */
		ChannelInfo originalChlInfo = channelInfoDao.getEntityById(channelInfo.getId());
		originalChlInfo.setAdditionalTable(channelInfo.getAdditionalTable());
		originalChlInfo.setChannelName(channelInfo.getChannelName());
		originalChlInfo.setEnName(channelInfo.getEnName());
		//删除掉旧表
		sb.setLength(0);
		sb.append(" drop table if exists "+originalTbName);
		logger.info("更新频道基本信息时删除掉原先的附加表："+sb.toString());
		channelInfoDao.executeSql(sb.toString());
	}
	//查看附加表的字段信息
	public List<Map<String, Object>> channelAdvancedCfg(String channelId) {
		List<Map<String, Object>>  list=new ArrayList<Map<String, Object>>();
		try {
			StringBuffer sb=new StringBuffer();
			sb.append("SELECT id,colname,coltype,showtip,additionaltable,allownull,defaultval from lzz_extracolumnsdescforchl  ");
			sb.append(" WHERE additionaltable=(SELECT  additionaltable from lzz_channelinfo WHERE  id=?) ");
			list=channelInfoDao.queryForList(sb.toString(),channelId);
		} catch (Exception e) {
			logger.error("查看附加表的字段信息:",e);
		    Map<String, Object> map=new HashMap<String,Object>();
		    map.put("info", "error");		
		    map.put("errinfo", e.getMessage());		
		    list.add(map);
		}
		return list;
	}
	//执行添加字段的逻辑
	public void addFieldDia(Map<String,String> params) {
		String channelId=params.get("channelId");
		String showTip=params.get("fieldTip");
		String colName=params.get("fieldName");
		String colType=params.get("fieldType");
		String lenStr=params.get("strLength");
		int len=0;
		if (StringUtils.isNotBlank(lenStr)) {
			len=Integer.valueOf(lenStr);
		}
		String allowNull=params.get("fieldNull");
		String defaultVal=params.get("fieldDefault");
		ChannelInfo c=channelInfoDao.getEntityById(Integer.valueOf(channelId));
		String additionalTable=c.getAdditionalTable();
		//判断附加表是否已经存在，是则修改，否则创建
			String sql="select count(*) from information_schema.tables where table_schema=? and table_name=? ";
			 int count = channelInfoDao.queryForInt(sql, LzzConstants.getInstance().getDbName(),additionalTable);
			 if (count==1) {
				 sql="alter TABLE "+additionalTable+" add COLUMN ";
				 if (colType.equals("int")) {
						sql+=colName +" int(11)";
					}else if(colType.equals("varchar")){
						sql+=colName +" varchar("+len+")";
					}else if (colType.equals("richtext")) {
						sql+=colName +" longtext";
					}
					 if (allowNull.equals("noNull")) {
						 sql+=" not null ";
					 }
					if (StringUtils.isNotBlank(defaultVal)) {
						if (colType.equals("int")) {
							sql+=" default "+defaultVal;
						}else {
							sql+=" default '"+defaultVal+"'";
						}
					}
				logger.info("增加字段sql:"+sql);
				 channelInfoDao.executeSql(sql);
			}else {
				StringBuffer sb=new StringBuffer();
				sb.append("create table  if not exists "+additionalTable+" (");
				sb.append("id int(11) not null auto_increment, ");
				if (colType.equals("int")) { 
					sb.append(colName +" int(11)");
				}else if(colType.equals("varchar")){
					sb.append(colName +" varchar("+len+")");
				}else if (colType.equals("richtext")) {
					sb.append(colName +" longtext");
				}
				if (allowNull.equals("noNull")) {
					sb.append(" not null ");
				}
				if (StringUtils.isNotBlank(defaultVal)) {
					if (colType.equals("int")) {
						sb.append(" default "+defaultVal+" ,");
					}else {
						sb.append(" default '"+defaultVal+"' ,");
					}
				}else {
					sb.append(" ,");
				}
				sb.append("comm_id int(11) default NULL,");
				sb.append(" PRIMARY KEY  (id)");
				sb.append(" ) ENGINE=InnoDB DEFAULT CHARSET=utf8 ");
				logger.info("建表sql:"+sb.toString());
				channelInfoDao.executeSql(sb.toString());
			}
		StringBuffer sBuffer=new StringBuffer();
		sBuffer.append(" insert into lzz_extracolumnsdescforchl(colname,coltype,showtip,additionaltable,allownull,defaultval) values(?,?,?,?,?,?)");
		logger.info("把附加表字段信息存入字段信息表:"+sBuffer.toString());
		channelInfoDao.executeSql(sBuffer.toString(), colName,colType,showTip,additionalTable,allowNull.equals("yesNull")?true:false,defaultVal);
	}
	@Override
	public long getTotalCount() {
		StringBuffer sBuffer=new StringBuffer();
		sBuffer.append(" select count(*) from ChannelInfo");
		long count = (long)channelInfoDao.uniqueResult(sBuffer.toString());
		return count;
	}
	@Override
	public void deleteChl(List<Map<String, Object>> list,ServletContext sc) throws Exception {
		int size = list.size();
		for(int i=0;i<size;i++){
			Map<String, Object> oneChl = list.get(i);
			Integer chlId = new  BigDecimal(oneChl.get("id").toString()).intValue();  //Integer.valueOf(oneChl.get("id").toString());//gson会把1--1.0
			String  additionalTable=oneChl.get("additionaltable").toString();
			StringBuffer sb=new StringBuffer();
			//删除附加表的字段信息
			sb.append("delete from lzz_extracolumnsdescforchl where additionaltable=? ");
			channelInfoDao.executeSql(sb.toString(),additionalTable);
			//删除频道附加表下的记录,附加表未添加字段时并不存在
			sb.setLength(0);
			sb.append("select count(*) from information_schema.tables where table_schema=? and table_name=?");
			 int count = channelInfoDao.queryForInt(sb.toString(), LzzConstants.getInstance().getDbName(),additionalTable);
			 if (count==1) {
				 sb.setLength(0);
				 sb.append(" drop table "+additionalTable);
				 channelInfoDao.executeSql(sb.toString());
			}
			//删除频道公共表下的记录的缩略图
			sb.setLength(0);
			sb.append(" select cont.comm_thumbpic from lzz_commoncontent  cont where cont.COLUMN_ID IN ( ");
			sb.append("      SELECT id from lzz_columninfo cln WHERE cln.CHANNEL_ID=?  ");
			sb.append("   ) and cont.comm_thumbpic!=? ");//  /uploads/thumb/2017/05/4005140109726619_wxjt_20170509215305.png....
			List<String> thumbpics = channelInfoDao.queryForListString(sb.toString(), chlId,LzzConstants.THUMB_UPLOAD_PRE);
			for (Iterator<String> iterator = thumbpics.iterator(); iterator.hasNext();) {
				String thumbpic = iterator.next();
				String realPath = sc.getRealPath(thumbpic);
				File file=new File(realPath);
				if (file.exists()) {
					file.delete();
				}
			}
			//删除与频道公共表下的记录关联的上传文件：图片，file，vedio等
			sb.setLength(0);
			sb.append(" select u.file_path from lzz_uploadfile_info  u where u.cont_id IN ( ");
			sb.append("   SELECT cont.COMM_ID FROM lzz_commoncontent cont WHERE cont.COLUMN_ID IN (  ");
			sb.append("      SELECT id from lzz_columninfo cln WHERE cln.CHANNEL_ID=?  ");
			sb.append("   )  ");
			sb.append(" )  ");//  /uploads/image/2017/05/1494603217748037371.png
			List<String> filePaths = channelInfoDao.queryForListString(sb.toString(), chlId);
			for (Iterator<String> iterator = filePaths.iterator(); iterator.hasNext();) {
				String filePath = iterator.next();
				String realPath = sc.getRealPath(filePath);
				File file=new File(realPath);
				if (file.exists()) {
					file.delete();
				}
			}
			//删除与频道公共表下的记录关联的上传文件的记录
			sb.setLength(0);
			sb.append(" delete from lzz_uploadfile_info  where cont_id IN ( ");
			sb.append("   SELECT cont.COMM_ID FROM lzz_commoncontent cont WHERE cont.COLUMN_ID IN (  ");
			sb.append("      SELECT id from lzz_columninfo cln WHERE cln.CHANNEL_ID=?  ");
			sb.append("   )  ");
			sb.append(" )  ");
			channelInfoDao.executeSql(sb.toString(),chlId);
			//删除频道公共表下的表数据
			sb.setLength(0);
			sb.append(" DELETE FROM  lzz_commoncontent  WHERE COLUMN_ID IN (  ");
			sb.append("      SELECT id from lzz_columninfo cln WHERE cln.CHANNEL_ID=?  ");
			sb.append("   )  ");
			channelInfoDao.executeSql(sb.toString(),chlId);
			//删除频道下的栏目对应的静态文件夹
			sb.setLength(0);
			sb.append(" select cln.htmldir from lzz_columninfo cln where cln.CHANNEL_ID=?  ");//   s/cmsjiaocheng
			List<String> htmlDirs = channelInfoDao.queryForListString(sb.toString(), chlId);
			for (Iterator<String> iterator = htmlDirs.iterator(); iterator.hasNext();) {
				String htmlDir = iterator.next();
				String realPath = sc.getRealPath("/"+htmlDir);
				File file=new File(realPath);
				deleteDir(file);
			}
			//删除频道下的栏目:栏目存在自关联，不能直接DELETE FROM lzz_columninfo  WHERE CHANNEL_ID=? 删除
			sb.setLength(0);
			sb.append(" select id from lzz_columninfo  where channel_id=?  ");
			List<Integer> columnIds= channelInfoDao.queryForListInteger(sb.toString(),chlId);
			for(Integer columnid:columnIds){
				recursionDelCln(columnid);
			}
			//删除频道
			sb.setLength(0);
			sb.append(" DELETE FROM lzz_channelinfo  WHERE id=?  ");
			channelInfoDao.executeSql(sb.toString(),chlId);
		}
	}
	//删除一个目录：file.delete只能删除文件或空目录
	private void deleteDir(File file) {
		if (file.exists()) {
			File[] files = file.listFiles();
			for(File f:files){
				if (f.isDirectory()) {
					if (!f.delete()) {//空目录，此句已删除
						this.deleteDir(f);
					}
				}else {
					f.delete();
				}
			}
			file.delete();
		}	
	}
	/*递归删除栏目
	 * * 1java教程
		     2javase		
		     3前端教程
			      4js
	 */
	private void recursionDelCln(Integer columnid) {
		StringBuffer sb=new StringBuffer();
		sb.append("select id from lzz_columninfo where parentid=? ");//查询出所有子栏目
		List<Integer> subClnIds = channelInfoDao.queryForListInteger(sb.toString(), columnid);
		if (subClnIds.size()>0) {
			for(Integer subClnId:subClnIds){
				recursionDelCln(subClnId);
			}
		}
		//不管是封面、列表、单页面，外部链接栏目类型都执行删除(子栏目进入时就是删除子栏目，父级栏目执行到这里时就是删除父级栏目)
		sb.setLength(0);
		sb.append("delete from lzz_columninfo where  id=?  ");
		channelInfoDao.executeSql(sb.toString(),columnid);
	}
}
