package com.lzzcms.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.lzzcms.dao.ColumnInfoDao;
import com.lzzcms.dto.TreeDto;
import com.lzzcms.model.ChannelInfo;
import com.lzzcms.model.ColumnInfo;
import com.lzzcms.model.ColumnType;
import com.lzzcms.service.ColumnInfoService;
import com.lzzcms.service.ContentInfoService;
import com.lzzcms.utils.LzzConstants;
import com.lzzcms.utils.LzzcmsUtils;

@Service
public class ColumnInfoServiceImpl implements ColumnInfoService{
	private static Logger logger=Logger.getLogger(ColumnInfoServiceImpl.class);

	@Resource
	private ColumnInfoDao columnInfoDao;
	
	public ColumnInfoDao getColumnInfoDao() {
		return columnInfoDao;
	}

	public void setColumnInfoDao(ColumnInfoDao columnInfoDao) {
		this.columnInfoDao = columnInfoDao;
	}

	@Override
	public List<Map<String, Object>> getColumnByChanelIdForSpider(Integer chanelId) {
		StringBuffer sb=new StringBuffer();
		sb.append("select id,name from lzz_columninfo where channel_id=? and clntype=? ");
		return columnInfoDao.queryForList(sb.toString(),chanelId,2);
	}

	@Override
	public List<Map<String, Object>> columnInfo() {
		StringBuffer sb=new StringBuffer();
		sb.append("select cln.id,cln.name columnName,cln.parentid _parentId,cln.channel_id,chl.channelname,cln.orderno,cln.htmldir,cln.clntype ");
		sb.append(",cln.outlink,cln.mytpl,cln.contenttplname,cln.clntitle,cln.clnkeywords,cln.clndesc,cln.singlecontent,clntype.typename ");
		sb.append(" from lzz_columninfo cln ");
		sb.append(" left join lzz_channelinfo chl on cln.channel_id=chl.id ");
		sb.append(" left join lzz_columntype clntype on cln.clntype=clntype.id ");
		logger.info("查询栏目sql:"+sb.toString());
		return columnInfoDao.queryForList(sb.toString());
	}

	@Override
	public ColumnInfo getColumnById(Integer valueOf) {
		ColumnInfo entityById = columnInfoDao.getEntityById(valueOf);
		return entityById;
	}

	//新增子栏目
	@Override
	public void trueAddColumn(HttpServletRequest request) {
		ColumnInfo columnInfo=new ColumnInfo();
		String toAddCln_name = request.getParameter("toAddCln_name");
		String toAddCln_chlid = request.getParameter("toAddCln_chlid");//所属模型
		String toAddCln_parentid = request.getParameter("toAddCln_parentid");//父级
		String toAddCln_orderNo = request.getParameter("toAddCln_orderNo");
		String toAddCln_parentdir = request.getParameter("toAddCln_parentdir");//html父级目录
		String toAddCln_htmlDir = request.getParameter("toAddCln_htmlDir");
		toAddCln_htmlDir=toAddCln_parentdir+toAddCln_htmlDir;//html父级目录+当前子栏目目录=子栏目目录
		String toAddCln_typename = request.getParameter("toAddCln_typename");//栏目类别，使用easyui的form提交直接得到combobox的id
		//这四个根据栏目类型的不同而需作出相应的设置
		String toAddCln_mytpl=request.getParameter("toAddCln_mytpl");
		String toAddCln_contentTplName=request.getParameter("toAddCln_contentTplName");
		String toAddCln_outLink=request.getParameter("toAddCln_outLink");
		String toAddCln_singleContent=request.getParameter("toAddCln_singleContent");
		
		String toAddCln_clnTitle = request.getParameter("toAddCln_clnTitle");
		String toAddCln_clnKeyWords = request.getParameter("toAddCln_clnKeyWords");
		String toAddCln_clnDesc = request.getParameter("toAddCln_clnDesc");
		//赋值
		columnInfo.setName(toAddCln_name);
		columnInfo.setChannelInfo(new ChannelInfo(Integer.valueOf(toAddCln_chlid)));
		columnInfo.setColumnInfo(new ColumnInfo(Integer.valueOf(toAddCln_parentid)));
		columnInfo.setOrderNo(Integer.valueOf(toAddCln_orderNo));
		columnInfo.setHtmlDir(toAddCln_htmlDir);
		columnInfo.setClnType(new ColumnType(Integer.valueOf(toAddCln_typename)));
		if ("1".equals(toAddCln_typename)) {//封面
			columnInfo.setMyTpl(toAddCln_mytpl);
		}else if ("2".equals(toAddCln_typename)) {//列表
			columnInfo.setMyTpl(toAddCln_mytpl);
			columnInfo.setContentTplName(toAddCln_contentTplName);
		}else if ("3".equals(toAddCln_typename)) {//外部链接
			columnInfo.setOutLink(toAddCln_outLink);
		}else if ("4".equals(toAddCln_typename)) {//单页面
			columnInfo.setMyTpl(toAddCln_mytpl);
			columnInfo.setSingleContent(toAddCln_singleContent);
		}
		columnInfo.setClnTitle(toAddCln_clnTitle);
		columnInfo.setClnKeyWords(toAddCln_clnKeyWords);
		columnInfo.setClnDesc(toAddCln_clnDesc);
		logger.info("添加子栏目");
		columnInfoDao.saveEntity(columnInfo);
		String realPath = LzzcmsUtils.getRealPath(request,toAddCln_htmlDir);
		File file=new File(realPath);
		if (!file.exists()) {
			file.mkdirs();
		}
	}
	/*
	 * 先找到该栏目的所有后代栏目，如果有，则删除每个后代栏目下的所有内容记录及其对应的缩略图和img  video file文件，
	 * 							     之后删除所有后代栏目，最后再删除该栏目的记录
	 * 						如果没有，则删除该栏目下的所有内容记录及其对应的缩略图和img  video file文件,
	 * 							     最后再删除该栏目的记录
	 */
	@Resource
	private ContentInfoService contentInfoService;
	@Override
	public void deleteClnById(String columnid, String chlid,HttpServletRequest request) {
		StringBuffer sb=new StringBuffer();
		sb.append("SELECT  additionaltable from lzz_channelinfo WHERE  id=?");
		String tableName=columnInfoDao.queryForString(sb.toString(),chlid);//得到该栏目所在模型的附加表
		sb.setLength(0);
		sb.append("select count(*) from information_schema.tables where table_schema=? and table_name=?");
		 int count = columnInfoDao.queryForInt(sb.toString(), LzzConstants.getInstance().getDbName(),tableName);
		recursionDelCln(columnid, chlid, request,count);
	}
	/*递归删除栏目
	 * * 1java教程
		     2javase		
		     3前端教程
			      4js
	 */
	private void recursionDelCln(String columnid, String chlid,
			HttpServletRequest request,  int count) {
		StringBuffer sb=new StringBuffer();
		sb.append("select id from lzz_columninfo where parentid=? ");//查询出所有子栏目
		List<Integer> subClnIds = columnInfoDao.queryForListInteger(sb.toString(), columnid);
		sb.setLength(0);
		if (subClnIds.size()>0) {
			for(Integer subClnId:subClnIds){
				recursionDelCln(subClnId+"", chlid, request,count);
			}
		}else {//没有子栏目，不管是封面、列表、单页面，外部链接栏目类型都执行删除
			sb.append("select comm_id,comm_thumbpic,comm_htmlpath,"+chlid+" chl_id from  lzz_commoncontent where column_id=? ");
			List<Map<String, Object>> contList = columnInfoDao.queryForList(sb.toString(), columnid);
			if (count==1) {//所属模型的附加表已经创建
				contentInfoService.deleteContent(contList, request, "need");
			}else {
				contentInfoService.deleteContent(contList, request, "noneed");
			}
		}
		//删除栏目(子栏目进入时就是删除子栏目，父级栏目执行到这里时就是删除父级栏目)
		sb.setLength(0);
		sb.append("delete from lzz_columninfo where  id=?  ");
		columnInfoDao.executeSql(sb.toString(),columnid);
	}
	@Override
	public void trueUpColumn(HttpServletRequest request) {
		String toUpCln_clnid = request.getParameter("toUpCln_clnid");
		ColumnInfo cInfo = columnInfoDao.getEntityById(Integer.valueOf(toUpCln_clnid));
		String toUpCln_name = request.getParameter("toUpCln_name");
		String toUpCln_orderNo = request.getParameter("toUpCln_orderNo");
		String toUpCln_htmlDir = request.getParameter("toUpCln_htmlDir");
		String toUpCln_clntype = request.getParameter("toUpCln_clntype");
		//这四个根据栏目类型的不同而需作出相应的设置
		String toUpCln_mytpl=request.getParameter("toUpCln_mytpl");
		String toUpCln_contentTplName=request.getParameter("toUpCln_contentTplName");
		String toUpCln_outLink=request.getParameter("toUpCln_outLink");
		String toUpCln_singleContent=request.getParameter("toUpCln_singleContent");
		
		String toUpCln_clnTitle = request.getParameter("toUpCln_clnTitle");
		String toUpCln_clnKeyWords = request.getParameter("toUpCln_clnKeyWords");
		String toUpCln_clnDesc = request.getParameter("toUpCln_clnDesc");
		//赋值
		cInfo.setName(toUpCln_name);
		cInfo.setOrderNo(Integer.valueOf(toUpCln_orderNo));
		cInfo.setHtmlDir(toUpCln_htmlDir);
		cInfo.setClnType(new ColumnType(Integer.valueOf(toUpCln_clntype)));
		if ("1".equals(toUpCln_clntype)) {//封面
			cInfo.setMyTpl(toUpCln_mytpl);
		}else if ("2".equals(toUpCln_clntype)) {//列表
			cInfo.setMyTpl(toUpCln_mytpl);
			cInfo.setContentTplName(toUpCln_contentTplName);
		}else if ("3".equals(toUpCln_clntype)) {//外部链接
			cInfo.setOutLink(toUpCln_outLink);
		}else if ("4".equals(toUpCln_clntype)) {//单页面
			cInfo.setMyTpl(toUpCln_mytpl);
			cInfo.setSingleContent(toUpCln_singleContent);
		}
		cInfo.setClnTitle(toUpCln_clnTitle);
		cInfo.setClnKeyWords(toUpCln_clnKeyWords);
		cInfo.setClnDesc(toUpCln_clnDesc);
		logger.info("更新栏目");
		String realPath = LzzcmsUtils.getRealPath(request,toUpCln_htmlDir);
		File file=new File(realPath);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	@Override
	public List<TreeDto> getColumns() {
		List<TreeDto> retList=new ArrayList<>();
		TreeDto rootTreeDto=new TreeDto();
		rootTreeDto.setId(0);
		rootTreeDto.setText("所有栏目");
		retList.add(rootTreeDto);
		StringBuffer sb=new StringBuffer();
		sb.append(" select c.id,c.name,c.parentid from lzz_columninfo c left join lzz_columntype t on c.clntype=t.id where t.enname!='outlink'");
		sb.append(" and c.parentid is null ");
		 List<Map<String, Object>> queryForList = columnInfoDao.queryForList(sb.toString());//得到所有的栏目
		 List<ColumnInfo> columnInfos =new ArrayList<>();
		 converListMapToCln(queryForList, columnInfos);
		this.recursionCln(columnInfos,rootTreeDto);
		 return retList;
	}

	private void converListMapToCln(List<Map<String, Object>> queryForList,
			List<ColumnInfo> columnInfos) {
		for(Map<String, Object> map:queryForList){
			 ColumnInfo c=new ColumnInfo();
			 c.setId(Integer.valueOf(map.get("id").toString()));
			 c.setName(map.get("name").toString());
			 ColumnInfo parentCln=new ColumnInfo();
			 Object object = map.get("parentid");
			 if (object!=null) {
				parentCln.setId(Integer.valueOf(object.toString()));
				c.setColumnInfo(parentCln);
			}else {
				c.setColumnInfo(null);
			}
			 columnInfos.add(c);
		 }
	}
	private void recursionCln(List<ColumnInfo> columnInfos,TreeDto parentTreeDto) {
		List<TreeDto> crtSubList=new ArrayList<TreeDto>();
		for(ColumnInfo columnInfo:columnInfos){
			StringBuffer sb=new StringBuffer();
			sb.append(" select c.id,c.name,c.parentid from lzz_columninfo c left join lzz_columntype t on c.clntype=t.id where t.enname!='outlink' ");
			sb.append(" and c.parentid=? ");
			List<Map<String, Object>> queryForList = columnInfoDao.queryForList(sb.toString(),columnInfo.getId());//得到当前栏目的子栏目
			List<ColumnInfo> sonClns = new ArrayList<>();
			converListMapToCln(queryForList, sonClns);
			TreeDto treeDto=new TreeDto();
			treeDto.setId(columnInfo.getId());
			treeDto.setText(columnInfo.getName());
			if (sonClns.size()!=0) {//columnInfo有子栏目
				treeDto.setState("open");
				this.recursionCln(sonClns,treeDto);
			}else {//
			}
			crtSubList.add(treeDto);
		}
		parentTreeDto.setChildren(crtSubList);
	}
	   //新增顶级栏目
		@Override
		public void trueAddTopColumn(HttpServletRequest request) {
			ColumnInfo columnInfo=new ColumnInfo();
			String name = request.getParameter("name");
			String channel_id = request.getParameter("channel_id");//所属模型
			String orderNo = request.getParameter("orderNo");
			String htmlDir = request.getParameter("htmlDir");
			String clnDir_s=LzzConstants.HTML_ROOTPATH_STARTWITHS+htmlDir;
			String typename = request.getParameter("typename");//栏目类别，使用easyui的form提交直接得到combobox的id
			
			String clnTitle = request.getParameter("clnTitle");
			String clnKeyWords = request.getParameter("clnKeyWords");
			String clnDesc = request.getParameter("clnDesc");
			//赋值
			columnInfo.setName(name);
			columnInfo.setChannelInfo(new ChannelInfo(Integer.valueOf(channel_id)));
			columnInfo.setOrderNo(Integer.valueOf(orderNo));
			columnInfo.setHtmlDir(clnDir_s);
			columnInfo.setClnType(new ColumnType(Integer.valueOf(typename)));
			if ("1".equals(typename)) {//封面
				columnInfo.setMyTpl(request.getParameter("cover_mytpl"));
			}else if ("2".equals(typename)) {//列表
				columnInfo.setMyTpl(request.getParameter("list_mytpl"));
				columnInfo.setContentTplName(request.getParameter("list_contentTplName"));
			}else if ("3".equals(typename)) {//外部链接
				String outLink=request.getParameter("outLink");
				columnInfo.setOutLink(outLink);
			}else if ("4".equals(typename)) {//单页面
				String singleContent=request.getParameter("singleContent");
				columnInfo.setMyTpl(request.getParameter("singlepage_mytpl"));
				columnInfo.setSingleContent(singleContent);
			}
			columnInfo.setClnTitle(clnTitle);
			columnInfo.setClnKeyWords(clnKeyWords);
			columnInfo.setClnDesc(clnDesc);
			logger.info("添加顶级栏目");
			columnInfoDao.saveEntity(columnInfo);
			String realPath = LzzcmsUtils.getRealPath(request,clnDir_s);
			File file=new File(realPath);
			if (!file.exists()) {
				file.mkdir();
			}
		}

		@Override
		public String getDirByTopClnName(String topName) {
			String pinYinHeadChar = LzzcmsUtils.getPinYinHeadChar(topName);
			return pinYinHeadChar;
		}

		@Override
		public List<Map<String, Object>> getListColumnByChanelId(Integer chanelId) {
			StringBuffer sb=new StringBuffer();
			sb.append(" select c.id,c.name from lzz_columninfo c join lzz_columntype ");
			sb.append("  t on c.clntype=t.id  where c.channel_id=?  and t.enname='list' ");
			return columnInfoDao.queryForList(sb.toString(),chanelId);
		}
}
