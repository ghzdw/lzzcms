package com.lzzcms.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.lzzcms.dao.ContentInfoDao;
import com.lzzcms.model.ExtraColumnsDescForChl;
import com.lzzcms.model.OneTerm;
import com.lzzcms.service.ContentInfoService;
import com.lzzcms.utils.LzzConstants;
import com.lzzcms.utils.LzzcmsUtils;
import com.lzzcms.utils.PageContext;

@Service
public class ContentInfoServiceImpl implements ContentInfoService{
	private static Logger logger=Logger.getLogger(ContentInfoServiceImpl.class);
	@Resource
	private ContentInfoDao contentInfoDao;
	@Resource
	private JedisPool jedisPool;
	private String[] buff;
	public ContentInfoDao getContentInfoDao() {
		return contentInfoDao;
	}

	public void setContentInfoDao(ContentInfoDao contentInfoDao) {
		this.contentInfoDao = contentInfoDao;
	}
	@Override
	public List<Map<String, Object>> trueList(Map<String, String> paramMap, HttpServletRequest request) {
		StringBuffer sb=new StringBuffer();
		if (PageContext.getPageUtil().isNeedPage()) {
			sb.append(" select cont.comm_id,cont.comm_title,cont.comm_shorttitle,cont.comm_click, ");
			sb.append(" DATE_FORMAT(cont.comm_publishdate,'%Y-%m-%d %H:%i:%s') comm_publishdate, ");
			sb.append(" cont.comm_keywords,cont.comm_desc,cont.comm_thumbpic,cont.comm_intro, ");
			sb.append(" cont.comm_htmlpath,cont.column_id ,cln.name,cln.channel_id, ");
			sb.append(" chl.channelname,src.come_from,author.author_name,df.define_flag, ");
			sb.append(" cont.comm_src,cont.comm_author,cont.comm_defineflag ");
		}else {
			sb.append(" select count(*) count ");
		}
		sb.append(" from lzz_commoncontent cont LEFT JOIN lzz_columninfo  cln on cont.column_id=cln.id ");
		sb.append(" LEFT JOIN  lzz_channelinfo chl ON cln.channel_id=chl.id ");
		sb.append(" left join lzz_source src on cont.comm_src=src.id ");
		sb.append(" left join lzz_author author on cont.comm_author=author.id ");
		sb.append(" left join lzz_define_flag  df on cont.comm_defineflag=df.en_name  ");
		sb.append(" where 1=1 ");
		List<String> list=new ArrayList<String>();
		if (StringUtils.isNotBlank(paramMap.get("contentManage_channel_id"))) {
			sb.append(" and channel_id=? ");
			list.add(paramMap.get("contentManage_channel_id"));
		}
		if (StringUtils.isNotBlank(paramMap.get("contentManage_column_id"))) {
			sb.append(" and column_id=? ");
			list.add(paramMap.get("contentManage_column_id"));
		}
		if (StringUtils.isNotBlank(paramMap.get("contentManage_commmonSelect"))) {
			sb.append(" and (comm_title like ?  or  comm_shorttitle like ? or comm_keywords like ? or comm_desc like ? ");
			sb.append(" or comm_thumbpic like ? or comm_intro like ? or comm_htmlpath like ?  ) ");
			for (int i = 0; i <7; i++) {
				list.add("%"+paramMap.get("contentManage_commmonSelect")+"%");
			}
		}
		if (StringUtils.isNotBlank(paramMap.get("contentManage_comm_click"))) {
			sb.append(" and comm_click "+paramMap.get("contentManage_comm_click_compare")+" ? ");
			list.add(paramMap.get("contentManage_comm_click"));
		}
		if (StringUtils.isNotBlank(paramMap.get("contentManage_comm_author"))) {
			sb.append(" and comm_author=? ");
			list.add(paramMap.get("contentManage_comm_author"));
		}
		if (StringUtils.isNotBlank(paramMap.get("contentManage_comm_publishdate"))) {
			sb.append(" and DATE_FORMAT(comm_publishdate,'%Y-%m-%d')=? ");
			list.add(paramMap.get("contentManage_comm_publishdate"));
		}
		if (StringUtils.isNotBlank(paramMap.get("contentManage_comm_src"))) {
			sb.append(" and comm_src=? ");
			list.add(paramMap.get("contentManage_comm_src"));
		}
		if (StringUtils.isNotBlank(paramMap.get("contentManage_comm_defineflag"))) {
			sb.append(" and comm_defineflag=? ");
			list.add(paramMap.get("contentManage_comm_defineflag"));
		}
		if (StringUtils.isNotBlank(paramMap.get("sort"))&&StringUtils.isNotBlank(paramMap.get("order"))) {
			sb.append(" order by "+paramMap.get("sort")+" "+paramMap.get("order"));
		}else{
			sb.append(" order by cont.comm_id desc ");
		}
		if (PageContext.getPageUtil().isNeedPage()) {
			int pageNow=PageContext.getPageUtil().getPageNow();
			int pageSize=PageContext.getPageUtil().getPageSize();
			int start=(pageNow-1)*pageSize;
			sb.append(" limit "+start+","+pageSize);
		}
		logger.info("查询文档："+sb.toString());
		List<Map<String, Object>> resultList = contentInfoDao.queryForList(sb.toString(),list.toArray());
		if (PageContext.getPageUtil().isNeedPage()) {
			for(Map<String, Object> map:resultList){
				Object comm_htmlpathObj = map.get("comm_htmlpath");
				if (comm_htmlpathObj!=null&&StringUtils.isNotBlank(comm_htmlpathObj.toString())) {
					map.put("abs_comm_htmlpath", LzzcmsUtils.getBasePath(request)
							+"/"+comm_htmlpathObj.toString());
				}
			}
		}
		return resultList;
	}
	//查询附加信息
	@Override
	public Map<String, Object>  getAddforinfo(String comm_id,String channel_id) {
		Map<String, Object> retMap=new HashMap<String, Object>();
		StringBuffer sb=new StringBuffer();
		//得到指定模型附加表字段的信息
		sb.append("SELECT * from lzz_extracolumnsdescforchl WHERE additionaltable=(SELECT  additionaltable from lzz_channelinfo WHERE  id=?)");
		List<ExtraColumnsDescForChl> extraColumnsDescForChl=contentInfoDao.getColumnDescForChl(sb.toString(),channel_id);
		sb.setLength(0);
		sb.append("SELECT  additionaltable from lzz_channelinfo WHERE  id=?");
		String tableName=contentInfoDao.queryForString(sb.toString(),channel_id);
		sb.setLength(0);
		sb.append("SELECT * from "+tableName+" where comm_id= ?");
		Map<String, Object> map=contentInfoDao.queryForMap(sb.toString(),comm_id);
		for (Iterator<ExtraColumnsDescForChl> iterator = extraColumnsDescForChl.iterator(); iterator.hasNext();) {
			ExtraColumnsDescForChl e = iterator.next();
			retMap.put(e.getShowTip(), map.get(e.getColName()));
		}
		return retMap;
	}

	@Override
	public List<ExtraColumnsDescForChl> toAddExtral(String channel_id) {
		StringBuffer sb=new StringBuffer();
		//得到指定模型附加表字段的信息
		sb.append("SELECT * from lzz_extracolumnsdescforchl WHERE additionaltable=(SELECT  additionaltable from lzz_channelinfo WHERE  id=?)");
		List<ExtraColumnsDescForChl> extraColumnsDescForChl=contentInfoDao.getColumnDescForChl(sb.toString(),channel_id);
		return extraColumnsDescForChl;
	}
	/**
	 * 先保存文件，再保存共同信息，再保存附加表(通过频道id得到附加表的名称，再得到其字段，根据字段与页面提交参数的关系（多个extraCols_）得到字段对应的值)
	 */
	@Override
	public String trueAddContnet(MultipartFile file,HttpServletRequest request) throws Exception{
		String finalThumbPath=copyFileToUploadsDir(file, request);///uploads/thumb/2017/05/3942404401726951_360jt20170511015353184.jpg	
		if ((LzzConstants.THUMB_UPLOAD_PRE).equals(finalThumbPath)) {//没有文件，即普通内容的提交
			StringBuffer sb=new StringBuffer();
			 String title = request.getParameter("toAdd_comm_title");
			 String column_id=request.getParameter("toAdd_column_id");
			 title=this.getUniqueTitle(title,column_id,"add");
			 sb.append("insert into lzz_commoncontent (comm_title,comm_shorttitle,comm_click,comm_author,comm_modifydate,comm_keywords,");
			 sb.append("comm_desc,comm_src,comm_thumbpic,comm_defineflag,comm_intro,comm_htmlpath,column_id) values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
			 logger.info("保存共同字段:"+sb.toString());
			 String click = request.getParameter("toAdd_comm_click");
			contentInfoDao.executeSql(sb.toString(),title,request.getParameter("toAdd_comm_shorttitle"),
					 StringUtils.isBlank(click)==true?0:click,request.getParameter("toAdd_comm_author"),
							 LzzcmsUtils.getPatternDateString("yyyy-MM-dd HH:mm:ss", new Date()),
					 request.getParameter("toAdd_comm_keywords"),request.getParameter("toAdd_comm_desc"),request.getParameter("toAdd_comm_src"),
					 request.getParameter("toAdd_comm_thumbpic"),request.getParameter("toAdd_comm_defineflag"),request.getParameter("toAdd_comm_intro"),
					 "",column_id);
			 int generateId = contentInfoDao.queryForInt("select LAST_INSERT_ID()");
			 sb.setLength(0);//通过频道得到该频道模型的附加表
			 sb.append("SELECT  additionaltable from lzz_channelinfo WHERE  id=?");
			 String tableName=contentInfoDao.queryForString(sb.toString(),request.getParameter("toAdd_channel_id"));
			 sb.setLength(0);//得到模型附加字段中除了自增主键外的字段信息
			 sb.append("select c.COLUMN_NAME from information_schema.COLUMNS c where c.table_schema=? and ");
			 sb.append(" c.TABLE_NAME=? and COLUMN_KEY!='PRI' AND EXTRA!='auto_increment' ");
			 List<Map<String, Object>> list = contentInfoDao.queryForList(sb.toString(), LzzConstants.getInstance().getDbName(),tableName);
			 String cols="";
			 String questions="";//拼接?
			 List<String> values=new ArrayList<String>();
			 for (Iterator<Map<String, Object>> iterator = list.iterator(); iterator.hasNext();) {
				Map<String, Object> map =  iterator.next();
				String colName=String.valueOf(map.get("COLUMN_NAME")).toLowerCase();
				cols+=colName+",";
				questions+="?,";
				if (colName.equals("comm_id")) {
					values.add(String.valueOf(generateId));
				}else {
					values.add(request.getParameter("extraCols_"+colName));
				}
			 }
			 cols=cols.substring(0, cols.length()-1);//mainbody,comm_id-->gggg,generateId
			 questions=questions.substring(0, questions.length()-1);
			 sb.setLength(0);
			 sb.append("insert into "+tableName+"("+cols+") values ("+questions+")");//
			 logger.info("保存附加表信息:"+sb.toString());
			 contentInfoDao.executeSql(sb.toString(),values.toArray());//传个values可以测试事务的整体提交整体失败
			 List<Integer> uploadIdsList=(List<Integer>) request.getSession().getAttribute("uploadIds");
			 if (uploadIdsList!=null&&uploadIdsList.size()>0) {
				 List<Object[]> paramList=new ArrayList<>();
				for (int i = 0; i <uploadIdsList.size(); i++) {
					Object[] objects=new Object[2];
					objects[0]=generateId;
					objects[1]=uploadIdsList.get(i);
					paramList.add(objects);
				}
				sb.setLength(0);
				 sb.append("update lzz_uploadfile_info set cont_id=? where upload_id=?");
				 contentInfoDao.batchExecuteSql(sb.toString(), paramList);
				 request.getSession().removeAttribute("uploadIds");
			 }
			 return null;
		}else {
			return finalThumbPath;
		}
	}
	private String getUniqueTitle(String title, String column_id,String type) {
		//校验本栏目下标题是否重复，避免html覆盖
		 StringBuffer sb=new StringBuffer();
		 sb.append(" select count(*) count from lzz_commoncontent c where c.comm_title=? and ");
		 sb.append(" c.column_id=? ");
		 long exist = contentInfoDao.queryForLong(sb.toString(), title,column_id);
		 if ("add".equals(type)) {
			if (exist!=0) {
				title+=exist;
				getUniqueTitle(title, column_id,type);
			}
		 }else {//update
			if (exist!=0&&exist!=1) {
				title+=exist;
				getUniqueTitle(title, column_id,type);
			}
		}
		return title;
	}

	/**
	 * @param file
	 * @param request
	 * @param type:thumb
	 * @return
	 */
	private String copyFileToUploadsDir(MultipartFile file,HttpServletRequest request) {
		/*360截图20170511015353184.jpg*/
		String originalFilename = file.getOriginalFilename();
		long nanoTime = System.nanoTime();
		String finalThumbPath=LzzConstants.THUMB_UPLOAD_PRE;//"/uploads/thumb/";
		if (StringUtils.isNotBlank(originalFilename)) {
			//构建要存放文件的目录的绝对路径
			finalThumbPath+=LzzcmsUtils.getPatternDateString("yyyy/MM/", new Date());///uploads/thumb/2017/05/
			File dir=new File(LzzcmsUtils.getRealPath(request,finalThumbPath));/*F:\\Program\\workspaces\\EclipseWS\\lzzcms\\web\\uploads\\thumb\\2017\\05*/
			if (!dir.exists()) {
				dir.mkdirs();
			}
			//由上一步的父级目录构造新文件名的绝对路径
			String baseName2Py=LzzcmsUtils.getPinYinHeadChar(FilenameUtils.getBaseName(originalFilename));
			String suffix="."+FilenameUtils.getExtension(originalFilename);
			String newFileName=baseName2Py+suffix;//360jt20170511015353184.jpg
			finalThumbPath+=nanoTime+"_"+newFileName;// /uploads/thumb/2017/05/123456789_360jt20170511015353184.jpg
			//targetPath:F:\\Program\\workspaces\\EclipseWS\\lzzcms\\web\\uploads\\thumb\\2017\\05\\3942865675762594_360jt20170511015353184.jpg
			String targetPath = LzzcmsUtils.getRealPath(request,finalThumbPath);
			File destFile=new File(targetPath);
				//如果是上传的缩略图就先等比例压缩，再剪切为需要的长宽
				CommonsMultipartFile commonsMultipartFile=(CommonsMultipartFile) file;
				DiskFileItem diskFileItem=(DiskFileItem) commonsMultipartFile.getFileItem();
				/**diskFileItem:
				 * name=360截图20170511015353184.jpg, 
				 * StoreLocation=F:\\Program\\apache-tomcat-7.0.72\\work\\Catalina\\localhost\\lzzcms\\upload_cd99c1a3_8c96_4a4a_b8b1_f486629c274d_00000174.tmp,
				 * size=5183 bytes, isFormField=false, FieldName=toAdd_comm_thumbpic
				 *///文件太小springmvc不缓存导致read出错
				//File storeLocation = diskFileItem.getStoreLocation();
				BufferedImage bi=null;
				try {
					File remoteFile = new File(diskFileItem.getName());
				   File tmpFile = new File(dir.getAbsolutePath(), remoteFile.getName());
				    diskFileItem.write(tmpFile);
				    
					bi = ImageIO.read(tmpFile);
					logger.info("没进行压缩前的图片宽度:"+bi.getWidth());
					//450*200 0.33 0.5;768*1024  0.195  0.098
					double max = Math.max(150.0/bi.getWidth(), 100.0/bi.getHeight());
					//scale(min):min为长宽的缩放比例，缩放到原来的百分之几，比方说min=0.098，那么缩放后宽就是768*0.098=75了
					//连写的话Thumbnails.of(bi).scale(max).sourceRegion(Positions.CENTER, 150, 100).toFile(destFile)
					//在调用sourceRegion时还会再调用scale，导致压缩不正确
					 BufferedImage asBufferedImage = Thumbnails.of(bi).scale(max).asBufferedImage();
					 Thumbnails.of(asBufferedImage).scale(1.0).sourceRegion(Positions.CENTER, 150, 100).toFile(destFile);
					 tmpFile.delete();
				} catch (Exception e) {
					logger.error("处理缩略图出错:",e);
				}
		}
		return finalThumbPath;
	}

	@Override
	public Map<String, Object> getCommById(String comm_id) {
		StringBuffer sb=new StringBuffer();
		sb.append(" select cont.comm_id,cont.comm_title,cont.comm_shorttitle,cont.comm_click, ");
		sb.append("  DATE_FORMAT(cont.comm_publishdate,'%Y-%m-%d') comm_publishdate, ");
		sb.append(" cont.comm_keywords,cont.comm_desc,cont.comm_thumbpic,cont.comm_intro,cont.comm_htmlpath, ");
		sb.append("  cont.column_id,cont.comm_src,cont.comm_author,cont.comm_defineflag, ");
		sb.append("  cln.name,chl.id channel_id,chl.channelname,src.come_from,author.author_name,  ");
		sb.append("  df.define_flag  from lzz_commoncontent cont LEFT JOIN lzz_columninfo  cln ");
		sb.append(" on cont.column_id=cln.id LEFT JOIN  lzz_channelinfo chl ON cln.channel_id=chl.id ");
		sb.append(" left join lzz_source src on cont.comm_src=src.id ");
		sb.append(" left join lzz_author author on cont.comm_author=author.id ");
		sb.append(" left join lzz_define_flag  df on cont.comm_defineflag=df.id ");
		sb.append(" where cont.comm_id=? ");
		logger.info("根据内容id查询文档供内容修改页面显示："+sb.toString());
		 Map<String, Object> map = contentInfoDao.queryForMap(sb.toString(),comm_id);
		 Object object = map.get("comm_thumbpic");
		 String imgshow="/resources/imgs/plus.png";
		 if (object!=null&&!LzzConstants.THUMB_UPLOAD_PRE.equals(object.toString())) {
			imgshow=object.toString();
		 }
		 map.put("imgshow", imgshow);
		 return map;
	}
	//查询附加信息
	@Override
	public Map<String, Object>  getAddforinfoById(String comm_id,String channel_id, String additionalTable) {
		StringBuffer sb=new StringBuffer();
		sb.append("SELECT * from "+additionalTable+" where comm_id= ?");
		Map<String, Object> map=contentInfoDao.queryForMap(sb.toString(),comm_id);
		return map;
	}
	@Override
	public String trueUpContnet(MultipartFile file,HttpServletRequest request) throws Exception{
		String finalThumbPath=copyFileToUploadsDir(file, request);	
		if (LzzConstants.THUMB_UPLOAD_PRE.equals(finalThumbPath)) {
			String toUp_comm_thumbpic_org = request.getParameter("toUp_comm_thumbpic_org");//原先的缩略图路径
			String toUp_comm_thumbpic_new = request.getParameter("toUp_comm_thumbpic_new");//新的的缩略图路径
			if (!toUp_comm_thumbpic_new.equals(toUp_comm_thumbpic_org)) {
				if (!LzzConstants.THUMB_UPLOAD_PRE.equals(toUp_comm_thumbpic_org)) {//原先存在就删除原先的
					String realPath = LzzcmsUtils.getRealPath(request,toUp_comm_thumbpic_org);
					File orgThumbFile=new File(realPath);
					orgThumbFile.delete();
				}
			}
			 StringBuffer sb=new StringBuffer();
			 String title = request.getParameter("toUp_comm_title");
			 String column_id=request.getParameter("toUp_column_id");
			 title=this.getUniqueTitle(title,column_id,"update");
			 sb.append("update lzz_commoncontent  set column_id=?,comm_title=?,comm_shorttitle=?,comm_click=?,comm_author=?,comm_modifydate=?,comm_keywords=?,");
			 sb.append("comm_desc=?,comm_src=?,comm_thumbpic=?,comm_defineflag=?,comm_intro=?,comm_html_isupdated=? where comm_id=? ");
			 logger.info("更新共同字段表:"+sb.toString());
			 contentInfoDao.executeSql(sb.toString(),column_id,title,request.getParameter("toUp_comm_shorttitle"),
					 request.getParameter("toUp_comm_click"),request.getParameter("toUp_comm_authorname"), LzzcmsUtils.getPatternDateString("yyyy-MM-dd HH:mm:ss", new Date()),
					 request.getParameter("toUp_comm_keywords"),request.getParameter("toUp_comm_desc"),request.getParameter("toUp_comm_srcname"),
					 toUp_comm_thumbpic_new,request.getParameter("toUp_comm_defineflagname"),request.getParameter("toUp_comm_intro"),
					 "no",request.getParameter("toUp_comm_id"));//htmlpath需要原位置生成，故不需要改变
			 sb.setLength(0);//通过频道得到该频道模型的附加表
			 sb.append("SELECT  additionaltable from lzz_channelinfo WHERE  id=?");
			 String toUp_channel_id = request.getParameter("toUp_channel_id");
			 String tableName=contentInfoDao.queryForString(sb.toString(),toUp_channel_id);
			 sb.setLength(0);//得到模型附加字段中除了自增主键外的字段信息
			 sb.append("select c.COLUMN_NAME from information_schema.COLUMNS c where c.table_schema=? and ");
			 sb.append(" c.TABLE_NAME=? and COLUMN_KEY!='PRI' AND EXTRA!='auto_increment' and COLUMN_NAME!='COMM_ID' ");
			 List<Map<String, Object>> list = contentInfoDao.queryForList(sb.toString(), LzzConstants.getInstance().getDbName(),tableName);
			 String cols="";
			 List<String> values=new ArrayList<String>();
			 for (Iterator<Map<String, Object>> iterator = list.iterator(); iterator.hasNext();) {
				Map<String, Object> map =  iterator.next();
				String colName=String.valueOf(map.get("COLUMN_NAME")).toLowerCase();
				cols+=colName+"=?,";
				values.add(request.getParameter("toUpExtraCols_"+colName));
			 }
			 //执行完for循环后cols和values对应，现在再加入comm_id
			 values.add(request.getParameter("toUp_comm_id"));
			 cols=cols.substring(0, cols.length()-1);//mainbody,comm_id-->gggg,generateId
			 sb.setLength(0);
			 sb.append(" update "+tableName+" set "+cols+" where comm_id=?");
			 logger.info("更新附加表信息:"+sb.toString());
			 contentInfoDao.executeSql(sb.toString(),values.toArray());//传个values可以测试事务的整体提交整体失败
			 return null;
		}else {
			return finalThumbPath;
		}
		
	}
	/**
	 * 先根据内容id先删除附加表再删除主表再删除与该文档有关的缩略图、html静态文件、上传的资源(图片、视频、文件)
	 */
	@Override
	public void deleteContent(List<Map<String, Object>> list, HttpServletRequest request,String needDelAddtionTable) {
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map=list.get(i);
			long comm_id=new BigDecimal(map.get("comm_id").toString()).longValue();//文档的id
			long channel_id=new BigDecimal(map.get("chl_id").toString()).longValue();//所属频道
			StringBuffer sb=new StringBuffer();
			if ("need".equals(needDelAddtionTable)) {
				 sb.append("SELECT  additionaltable from lzz_channelinfo WHERE  id=?");
				 String tableName=contentInfoDao.queryForString(sb.toString(),channel_id);
				 sb.setLength(0);
				 sb.append(" delete from "+tableName+" where comm_id=? ");
				 contentInfoDao.executeSql(sb.toString(), comm_id);
			}
			 sb.setLength(0);
			 sb.append(" delete from lzz_commoncontent where comm_id=? ");
			 contentInfoDao.executeSql(sb.toString(), comm_id);
			 String thumb=map.get("comm_thumbpic")==null?"":map.get("comm_thumbpic").toString();
			 String htmlpath=map.get("comm_htmlpath")==null?"":map.get("comm_htmlpath").toString();
			 if (StringUtils.isNotBlank(thumb)) {//删除缩略图
				String thumbRealPath = LzzcmsUtils.getRealPath(request,thumb);
				File thumbFile=new File(thumbRealPath);
				if (thumbFile.exists()&&thumbFile.isFile()) {
					thumbFile.delete();
				}
			}
			 if (StringUtils.isNotBlank(htmlpath)) {//删除html静态文件
					String htmlRealPath = LzzcmsUtils.getRealPath(request,htmlpath);
					File htmlFile=new File(htmlRealPath);
					if (htmlFile.exists()&&htmlFile.isFile()) {
						htmlFile.delete();
					}
			}
			 sb.setLength(0);//删除上传关联的文件
			 sb.append(" select file_path from lzz_uploadfile_info where cont_id=? ");
			 List<String> uploadFilePaths = contentInfoDao.queryForListString(sb.toString(), comm_id);
			 for(String path:uploadFilePaths){
				  String uploadFileRealPath=  LzzcmsUtils.getRealPath(request,path);
					File uploadFile=new File(uploadFileRealPath);//image   video    file
					if (uploadFile.exists()&&uploadFile.isFile()) {
						uploadFile.delete();
					}
			 }
			 sb.setLength(0);//删除上传关联的记录
			 sb.append(" delete from lzz_uploadfile_info where cont_id=? ");
			 contentInfoDao.executeSql(sb.toString(), comm_id);
		}
	}
	//把内容主体放入索引中完成
	private void write2Doc(Directory directory,Analyzer analyzer,String txt){
		IndexWriter iwriter=null;
		try {
			IndexWriterConfig conf=new IndexWriterConfig(analyzer);
			iwriter = new IndexWriter(directory, conf);
			iwriter.deleteAll();
			iwriter.forceMergeDeletes();
			Document document=new Document();
			document.add(new TextField("cont", txt, Field.Store.YES));
			iwriter.addDocument(document);
			iwriter.commit();
		} catch (Exception e) {
			logger.error("内容写入索引出错",e);
		}finally{
			LzzcmsUtils.closeIwriter(iwriter);
		}
	}
	private String getKeyworlds(IndexReader ir,Directory directory){
		String keyWords="";
		try {
		    Terms terms = MultiFields.getTerms(ir, "cont");  //获取某个域分词后的所有terms
		    TermsEnum iterator = terms.iterator();
		    BytesRef bRef = null;
		    List<OneTerm> oneTerms=new ArrayList<>();
			while ((bRef=iterator.next())!=null) {
				String oneTermStr=new String(bRef.bytes, bRef.offset, bRef.length,
						Charset.forName("utf-8"));//一个term单元字符串 
				if (oneTermStr.length()==1) {//汉字去掉一个字的,一个字的不作关键字，不仅汉字，其他也去掉长度为1的
					continue;
				}//该term在所有文档中共出现多少次
				long totalTermFreq = ir.totalTermFreq(new Term("cont", bRef));
				OneTerm oneTerm=new OneTerm(totalTermFreq,oneTermStr);
				oneTerms.add(oneTerm);
			}
			Collections.sort(oneTerms);
			int size = oneTerms.size();
			size=size>LzzConstants.KEYWORDS_NUM?LzzConstants.KEYWORDS_NUM:size;
			oneTerms=oneTerms.subList(0, size);
			for (int i = 0; i <size; i++) {
				OneTerm oTerm = oneTerms.get(i);
				keyWords+=oTerm.getTerm()+",";
			}
			if (keyWords.endsWith(",")) {
				keyWords=keyWords.substring(0, keyWords.length()-1);
			}
		} catch (Exception e) {
			logger.error("读取索引组装关键字出错", e);
		}
		return keyWords;
	}
	@Override
	public Map<String, Object> autoGeKeywordsAndIntro(String txt, String dir) {
		Map<String, Object> retMap=new HashMap<>();
		File dirFile= new File(dir);
	    if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
	    Directory directory=null;
		IndexReader ir=null;
		try {
			Analyzer analyzer =getAnalyzer();
			directory=FSDirectory.open(Paths.get(dir));
			//内容写入索引
			write2Doc(directory, analyzer, txt);	
			//创建ir的目录必须有索引文件
			ir=DirectoryReader.open(directory);
			//读取关键字
			String keyWords=getKeyworlds(ir,directory);
			retMap.put("key", keyWords);//获取关键字(通过出现的频率)完成
			String maxFreqTerm=keyWords.split(",")[0];
			//获取摘要
		   String bestFragment =getBestFragment(ir,analyzer,maxFreqTerm);
	       retMap.put("intro", bestFragment);
		} catch (Exception e) {
			logger.error("自动生成关键字出错:",e);
		}finally{
			LzzcmsUtils.closeIr(ir);
			LzzcmsUtils.closeDirectory(directory);
		}
		return retMap;
	}
	@Override
	public String getBestFragment(String txt,String maxFreqStr, HttpServletRequest request){
		String bestFragment ="";
		String dir = LzzcmsUtils.getRealPath(request,LzzConstants.KEYWORDSANDINTRO_INDEIES);
		File dirFile= new File(dir);
	    if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
	    Directory directory=null;
		IndexReader ir=null;
		try {
			Analyzer analyzer =getAnalyzer();
			directory=FSDirectory.open(Paths.get(dir));
			//内容写入索引
			write2Doc(directory, analyzer, txt);	
			//创建ir的目录必须有索引文件
			ir=DirectoryReader.open(directory);
			//获取摘要
		    bestFragment =getBestFragment(ir,analyzer,maxFreqStr);
		} catch (Exception e) {
			logger.error("获取摘要出错:",e);
		}finally{
			LzzcmsUtils.closeIr(ir);
			LzzcmsUtils.closeDirectory(directory);
		}
		return bestFragment;
	}
	
	private String getBestFragment(IndexReader ir, Analyzer analyzer,String maxFreqTerm) {
		String bestFragment = "";
		try {
			IndexSearcher isearcher = new  IndexSearcher(ir);
			QueryParser parser = new QueryParser("cont", analyzer);
			parser.setDefaultOperator(Operator.OR);
			parser.setAllowLeadingWildcard(true);
			Query query = parser.parse(maxFreqTerm); //使用出现频率最高的词去查询,当然了肯定就一个文档啦
			TopDocs tds = isearcher.search(query,1);//只有1条
			ScoreDoc[] sds = tds.scoreDocs;
			if (sds.length==0) {
				logger.info("最佳摘要为空");
				return bestFragment;
			}
			//高亮配置
			QueryScorer queryScorer=new QueryScorer(query);
			Fragmenter fragmenter=new SimpleSpanFragmenter(queryScorer, LzzConstants.BEST_FRAGMENT_LEN);
			SimpleHTMLFormatter formatter=new SimpleHTMLFormatter("", "");
			Highlighter highlighter=new Highlighter(formatter,queryScorer);//默认是b
			highlighter.setTextFragmenter(fragmenter);
			Document hitDoc = isearcher.doc(sds[0].doc);
			String content = hitDoc.get("cont");
			//根据高亮配置获取最佳摘要
            TokenStream tStream=analyzer.tokenStream("cont", new StringReader(content));
			bestFragment = highlighter.getBestFragment(tStream, content);
		} catch (Exception e) {
			logger.error("获取最佳摘要出错",e);
		} 
		return bestFragment;
	}

	private SmartChineseAnalyzer getAnalyzer() throws Exception {
		//加入停用词，提取更准确
		CharArraySet cas = new CharArraySet(0, true);  
		//得到SmartChineseAnalyzer使用的默认停用词
		 Iterator<Object> itor = SmartChineseAnalyzer.getDefaultStopSet().iterator();  
		 while (itor.hasNext()) {
			char[] type = (char[]) itor.next();
			cas.add(type);
		}
		 //StopAnalyzer里面的停用词
		 StopAnalyzer stopAnalyzer=new StopAnalyzer();
		 Iterator<Object> stopIt = stopAnalyzer.getStopwordSet().iterator();  
		 while (stopIt.hasNext()) {
				char[] type = (char[]) stopIt.next();
				cas.add(type);
		 }
		 stopAnalyzer.close();
		//自己定义的停用词
		String[] self_stop_words = this.getSelfStopWords(); 
		int self_stop_words_len = self_stop_words.length;
		for (int i = 0; i < self_stop_words_len; i++) {  
		    cas.add(self_stop_words[i]);  
		}
		SmartChineseAnalyzer analyzer=new SmartChineseAnalyzer(cas);
		return analyzer;
	}
    private String[] getSelfStopWords() throws Exception{
    	if (buff==null) {
    		URL resource = this.getClass().getClassLoader().getResource("stopwords.dic");
        	List<String> readLines = FileUtils.readLines(new File(resource.toURI()), "utf-8");
        	buff=new String[readLines.size()];
        	return readLines.toArray(buff);
		}else {
			return buff;
		}
	}

	@Override
	public int validateTitle(String title) {
		StringBuffer sbBuffer=new StringBuffer();
		sbBuffer.append(" select count(comm_title) count from lzz_commoncontent where comm_title=? ");
		 Integer queryForInt = contentInfoDao.queryForInt(sbBuffer.toString(),title);
		return queryForInt;
	}

	/*
	 * clickmap:  comm_id   click
	 */
	@Override
	public Long updateAndgetClick(String comm_id) {
        Jedis jedis=null;
        Long newclick=0L;
        try {
        	jedis=jedisPool.getResource();
        	if (!jedis.hexists("clickmap", comm_id)) {//如果不存在就查出来放到redis的hash中进去
        		StringBuffer sbBuffer=new StringBuffer();
        		sbBuffer.append(" select comm_click from  lzz_commoncontent  where comm_id=? ");
        		Integer click = contentInfoDao.queryForInt(sbBuffer.toString(),comm_id);
        		jedis.hset("clickmap", comm_id, click+"");
			}
        	newclick=jedis.hincrBy("clickmap", comm_id, 1);
		} catch (Exception e) {
			logger.error("获取文章点击量出错", e);
		}finally{
			if (jedis!=null) {
				jedis.close();
			}
		}
        return newclick;
	}
}
