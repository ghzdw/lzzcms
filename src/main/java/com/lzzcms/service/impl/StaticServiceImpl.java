package com.lzzcms.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.lzzcms.dao.ColumnInfoDao;
import com.lzzcms.dao.ContentInfoDao;
import com.lzzcms.dto.TreeDto;
import com.lzzcms.listeners.SpringBeanFactory;
import com.lzzcms.model.AdminInfo;
import com.lzzcms.model.ColumnInfo;
import com.lzzcms.model.ContRedisVo;
import com.lzzcms.service.StaticService;
import com.lzzcms.utils.LzzConstants;
import com.lzzcms.utils.LzzResponseWrapper;
import com.lzzcms.utils.LzzcmsUtils;
import com.lzzcms.utils.SerializationUtil;

@Service
public class StaticServiceImpl implements StaticService{
	private final Logger logger=Logger.getLogger(StaticServiceImpl.class);
	@Resource
	private JedisPool jedisPool;
	@Resource
	private ContentInfoDao contentInfoDao;
	@Resource
	private ThreadPoolTaskExecutor executor;
	/*
	 * //...<zdw:include file="search.html"/>-->search.html的内容fileNameHtmlStr
	 */
	public String includeFile(HttpServletRequest request,String src){
		String reg="<\\s*zdw:include\\s+file=['|\"](\\w+\\.html)['|\"]/>";
			Pattern pattern=Pattern.compile(reg,Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
			Matcher matcher = pattern.matcher(src);
			String regText=null;
			String fileName=null;
			while(matcher.find()){//文件中可能有多个该标签
				regText=matcher.group(0);
				fileName=matcher.group(1);
				String fileNamePath=getRealPath(request, "/tpls/"+fileName);
				String fileNameHtmlStr = null;
				try {
					fileNameHtmlStr=FileUtils.readFileToString(new File(fileNamePath),"utf-8");
					Matcher matcher2 = pattern.matcher(fileNameHtmlStr);
					if(matcher2.find()){
						fileNameHtmlStr=includeFile(request, fileNameHtmlStr);
					} 
						src=src.replaceAll(regText, fileNameHtmlStr);
				} catch (IOException e) {
					logger.info("包含出错：",e);
				}
			}
		return src;
	}
	private String tplHeadStr=null;
	//获取tplHead.jsp的内容
	private String getTplHeadJspStr(HttpServletRequest request) {
		if (tplHeadStr==null) {
			String tplHeadJspPath =getRealPath(request, LzzConstants.TPLHEADPATH);
			File tplHeadJspFile = new File(tplHeadJspPath);
			try {
				tplHeadStr=FileUtils.readFileToString(tplHeadJspFile, "utf-8");
			} catch (IOException e) {
				logger.error("读取tplHead.jsp文件内容出错:",e);
			}
		}
		return tplHeadStr;
	}
	/**
	 * 由jsp模版头内容+相应html模版内容生成output.jsp文件的内容，原先
	 * 是随机生成jsp名字，千万不要这样，一直生成新的类，导致内存一直增长
	 * @param request
	 * @param tplName:模版名称,比如index.html
	 * @return
	 */
	private String generateOutputjspContent(HttpServletRequest request,String tplName
			,String outputJspName){
		String tplHeadStr=getTplHeadJspStr(request);
		String tplPath =getRealPath(request, "/tpls/"+tplName);//index.html
		File tplFile = new File(tplPath);
		if (!tplFile.exists()) {
			return "模板文件不存在";
		}
		String outputFilePath=getRealPath(request, getMyTmpDir(request) + outputJspName);//abcd23f.jsp
		String tplStr = null;
		String outputFileContent=null;
		FileOutputStream fos=null;
		BufferedOutputStream bos=null;
		try {
			tplStr = FileUtils.readFileToString(tplFile,"utf-8");
			outputFileContent=tplHeadStr+tplStr;
			outputFileContent=includeFile(request, outputFileContent);
			fos=new FileOutputStream(outputFilePath);
			bos=new BufferedOutputStream(fos);
			bos.write(outputFileContent.getBytes("utf-8"));
			bos.flush();
		} catch (IOException e) {
			logger.error("出错：", e);
			return e.getMessage();
		}finally{
			LzzcmsUtils.closeOs(bos);
			LzzcmsUtils.closeOs(fos);
		}
		return null;
	}
	private String getMyTmpDir(HttpServletRequest request) {
		//一个用户(只能一个在线)一个文件夹，确保生成前的清除不影响其他人
		AdminInfo admin = (AdminInfo) request.getSession().getAttribute("admin");
		String tmpDir ="";
		if (admin!=null) {
			tmpDir="/tmp/"+admin.getUserName()+"/";
		}else {//评论的时候
			tmpDir="/tmp/comment/";
		}
		File file = new File(getRealPath(request, tmpDir));
		if (!file.exists()) {
			file.mkdirs();
		}
		return tmpDir;
	}
	/**
	 * 加synchronized是为了防止一个人开两个页面同时点击生成按钮执行静态化
	 * 首页、内容、栏目使用不同的output.jsp是为了防止点首页的时候再点内容output.jsp内容就乱了
	 * 另外：每个用户的tmpdir不同，里面各有一份这3类output.jsp文件，本身就不影响
	 */
	@Override
	public synchronized String makeIndex(HttpServletRequest request, HttpServletResponse response) {
		String output=LzzConstants.OUTPUTINDEXJSP;
		String tplName = request.getParameter("name");
		String ret= generateOutputjspContent(request,tplName,output);//"index.html"
		if (ret!=null) {
			return ret;
		}
		//得到上一步生成的临时jsp解析后的内容
		RequestDispatcher dispatcher = request.getSession().getServletContext().getRequestDispatcher(getMyTmpDir(request)+output);//"/tmp/admin/1234.jsp"
		LzzResponseWrapper lzzResponse=new LzzResponseWrapper(response);
		try {
			dispatcher.include(request, lzzResponse);
		} catch (Exception e) {
			logger.error("解析首页出错",e);
			return "解析出错";
		}
		String resHtml = lzzResponse.getResHtml();
		//把得到的内容写入到相应目录下的相应文件中
		String destHtmlPath=getRealPath(request, "/index.html");//最终生成的html
		ret=writeToDestHtml(destHtmlPath,resHtml);
		if (ret!=null) {
			return ret;
		}
		return null;
	}
	/**
	 * @param destHtmlPath：要生成的html文件的绝对路径
	 * @param resHtml：要生成的html文件的内容
	 * @return
	 */
	private String writeToDestHtml(String destHtmlPath,String resHtml) {
		FileOutputStream fos=null;
		BufferedOutputStream bos=null;
		try {
			File destFile=new File(destHtmlPath);
			if (destFile.exists()) {
				destFile.delete();
			}
			fos=new FileOutputStream(destFile);
			bos=new BufferedOutputStream(fos);
			bos.write(resHtml.getBytes("utf-8"));
			bos.flush();
		} catch (IOException e) {
			logger.error("写入目标文件"+destHtmlPath+"出错:",e);
			return e.getMessage();
		}finally{
			LzzcmsUtils.closeOs(bos);
			LzzcmsUtils.closeOs(fos);
		}
		return null;
	}
	//得到根目录下面文件路径
	private String getRealPath(HttpServletRequest request,String path){
	   return	request.getSession().getServletContext().getRealPath(path);
	}
	
	@Override
	public synchronized String makeCln(HttpServletRequest request,HttpServletResponse response, String clnid) {//页面传来的不可能是外部链接，肯定都有模版
		//得到栏目htmlDir
		ColumnInfoDao columnInfoDao = SpringBeanFactory.getBean("columnInfoDaoImpl", ColumnInfoDao.class);
		Map<String, Object> cachedOutputMap=new HashMap<String,Object>();
		if (StringUtils.isNotBlank(clnid)&&!"0".equals(clnid)) {
			ColumnInfo c = columnInfoDao.getEntityById(Integer.valueOf(clnid));
			updateCommHtmlIsupdated2YesPerCln(c.getId());
			return makeOneCln(request, response,c,cachedOutputMap);
		}else {//0:所有
			String type = request.getParameter("type");
			StringBuffer sb=new StringBuffer();
			List<ColumnInfo> list = null;
			if ("incr".equals(type)) {
				sb.append("select c from ColumnInfo c where c.clnType.enName!=? and c.commHtmlIsUpdated=? ");	
				list = columnInfoDao.findByHql(sb.toString(),"outlink","no");
			}else {
			  sb.append("select c from ColumnInfo c where c.clnType.enName!=?");	
			  list = columnInfoDao.findByHql(sb.toString(),"outlink");
			}
			int len=list.size();
			for (int i = 0; i <len; i++) {
				request.removeAttribute("list_pg_x");//如果不加这一句，比方说：前面有个栏目生成的时候已经在doc中存储了该属性，
				//因为多次调用的话是一个request，这样的话已进入makeOneCln就会取到上个栏目放入的list_pg_x值，会导致生成不了index.html了
				ColumnInfo c=list.get(i);
				String ret= makeOneCln(request, response, c,cachedOutputMap);
				if (ret!=null) {
					return ret;
				}
				updateCommHtmlIsupdated2YesPerCln(c.getId());
			}
		}
		return null;
	}
	private void updateCommHtmlIsupdated2YesPerCln(int id) {
		StringBuffer sb=new StringBuffer();
		sb.append(" update lzz_columninfo set COMM_HTML_ISUPDATED='yes' where id=? ");
		contentInfoDao.executeSql(sb.toString(), id);
	}
	//在doclist中调用，故改为public
	 public String makeOneCln(HttpServletRequest request,HttpServletResponse response,
			 ColumnInfo cInfo,Map<String, Object> cachedOutputMap) {
		 String output=LzzConstants.OUTPUTCLNJSP+FilenameUtils.getBaseName(cInfo.getMyTpl())+"_";
		 String seq="index";
		 //针对列表页作出处理
		 Object object = request.getAttribute("list_pg_x");
		 String list_pg_x="/index.html";
		 if (object!=null) {//list_pg_3
			list_pg_x="/"+String.valueOf(object)+".html";
			seq=String.valueOf(object);
		}
		 output+=seq+LzzConstants.SUFFIX;//output_cln_ list_article_ list_pg_3.jsp
		 String ret=null;
		 if (cachedOutputMap.get(output)==null) {
			cachedOutputMap.put(output, output);
		    ret=generateOutputjspContent(request, cInfo.getMyTpl(),output);//"/tpls/cover_article.html"
		    if (ret!=null) {
				return ret;
			}
		}
		//得到jsp解析后的内容
		request.setAttribute("cachedOutputMap", cachedOutputMap); 
		request.setAttribute("clnid", cInfo.getId());
		request.setAttribute("now_list_pg_x", object!=null?String.valueOf(object):"index");//仅在列表页中的doclist中使用
		RequestDispatcher dispatcher = request.getSession().getServletContext().getRequestDispatcher(getMyTmpDir(request)+output);//"/tmp/admin/1234.jsp"
		LzzResponseWrapper lzzResponse=new LzzResponseWrapper(response);
		try {
			dispatcher.include(request, lzzResponse);
		} catch (Exception e) {
			logger.info("请求生成栏目html的jsp出错:",e);
			return e.getMessage();
		}
		String resHtml = lzzResponse.getResHtml();
		 //把得到的内容写入到相应目录下的相应文件中
		String destHtmlParentDir=getRealPath(request, cInfo.getHtmlDir());
		File destHtmlParentDirFile=new File(destHtmlParentDir);
		if (!destHtmlParentDirFile.exists()) {
			destHtmlParentDirFile.mkdirs();
		}
		 String destHtmlPath=getRealPath(request, cInfo.getHtmlDir()+list_pg_x);//最终生成的html:/s/shujuku/index.html
		 ret=writeToDestHtml(destHtmlPath, resHtml);
		 if (ret!=null) {
			return ret;
		}
		return null;
	}
	@Override
	public synchronized String makeCont(HttpServletRequest request, HttpServletResponse response
			,String clnid) throws Exception{//排除掉外部链接栏目和单页面栏目
		logger.info("开始生成文档");
		long startTime=System.currentTimeMillis();
		//通过未生成的文章判定有哪些栏目需要增量生成
		updateClnInr();
		//得到所有的comm_title,comm_keywords,comm_htmlpath放入redis中
		putInContVoRedis();
		ColumnInfoDao columnInfoDao = SpringBeanFactory.getBean("columnInfoDaoImpl", ColumnInfoDao.class);
		Map<String, Object> cachedOutputMap=new HashMap<String,Object>();
		//关联频道得到附加表
		if (StringUtils.isNotBlank(clnid)&&!"0".equals(clnid)) {
			StringBuffer sb=new StringBuffer();
			sb.append("select i.id,i.contenttplname,i.htmldir,chl.additionaltable,t.enname,i.name from lzz_columninfo i left join lzz_columntype t on i.clntype=t.id  ");
			sb.append(" join lzz_channelinfo chl on i.channel_id=chl.id where i.id=?  ");
			Map<String, Object> map = columnInfoDao.queryForMap(sb.toString(), clnid);
			if (map.get("enname")!=null&&map.get("enname").equals("cover")) {//封面栏目下面没文章
				return null;
			}else {
				return makePerContInOneCln(request, response,map,cachedOutputMap);
			}
		}else {//选择所有，只有列表栏目有文章
			StringBuffer sb=new StringBuffer();
			sb.append("select i.id,i.contenttplname,i.htmldir,chl.additionaltable,i.name from lzz_columninfo i left join lzz_columntype t on i.clntype=t.id  ");
			sb.append(" join lzz_channelinfo chl on i.channel_id=chl.id   ");
			sb.append(" where t.enname not in ('outlink','singlepage','cover') ");
			List<Map<String, Object> > list = columnInfoDao.queryForList (sb.toString());
			int len=list.size();
			for (int i = 0; i <len; i++) {
				Map<String, Object> map = list.get(i);
				String ret= makePerContInOneCln(request, response, map,cachedOutputMap);
				if (ret!=null) {
					return ret;
				}
			}
		}
		removeContVoRedis();
		//把每个栏目的最新一篇的comm_html_isupdated字段设置为no,解决增量更新时无法更新之前的"没有了"
		updateLastest2NoForPerCln();
		logger.info("生成文档结束,共耗时"+(System.currentTimeMillis() - startTime)/1000+"秒");
		return null;
	}
	private void updateClnInr() {
		StringBuffer sb=new StringBuffer();
		sb.append(" select DISTINCT column_id from lzz_commoncontent WHERE COMM_HTMLPATH='' ");
		List<Integer> clnIds = contentInfoDao.queryForListInteger(sb.toString());
		Set<Integer> finalSet=new HashSet<Integer>();
		for(Integer oneClnId:clnIds){
			sb.setLength(0);
			//得到id=?的栏目的id以及它所有的祖先元素的id
			sb.append(" SELECT T2.id ");   
			sb.append(" FROM ( ");
			sb.append("    SELECT ");
			sb.append("         @r AS _id, ");
			sb.append("         (SELECT @r := parentid FROM lzz_columninfo WHERE id = _id) AS parent_id, ");
			sb.append("         @l := @l + 1 AS lvl ");
			sb.append("     FROM  ");
			sb.append("         (SELECT @r := ?, @l := 0) vars, ");
			sb.append("         lzz_columninfo h ");
			sb.append(" ) T1  ");
			sb.append(" JOIN lzz_columninfo T2 ");
			sb.append(" ON T1._id = T2.id    ");
			sb.append(" ORDER BY T1.lvl DESC  ");
			List<Integer> allIdsForThis = contentInfoDao.queryForListInteger(sb.toString(),oneClnId);
			finalSet.addAll(allIdsForThis);//去重
		}
		if (!finalSet.isEmpty()) {
			sb.setLength(0);
			sb.append(" update lzz_columninfo set COMM_HTML_ISUPDATED='no' WHERE id IN (:ids) ");
			Map<String, Object> idsMap=new HashMap<String, Object>();
			idsMap.put("ids", finalSet);
			contentInfoDao.updateByIn(sb.toString(), idsMap);
		}
	}
	private void updateLastest2NoForPerCln() {
		StringBuffer sb=new StringBuffer();
		sb.append(" SELECT max(a.COMM_ID) FROM lzz_commoncontent a WHERE a.COMM_HTML_ISUPDATED='yes' GROUP BY a.COLUMN_ID ");
		List<Integer> comm_ids = contentInfoDao.queryForListInteger(sb.toString());
		sb.setLength(0);
		sb.append(" UPDATE lzz_commoncontent SET COMM_HTML_ISUPDATED='no' WHERE COMM_ID IN (:comm_ids) ");
    	Map<String, Object> idsMap=new HashMap<String, Object>();
    	idsMap.put("comm_ids", comm_ids);
        contentInfoDao.updateByIn(sb.toString(), idsMap);
	}
	/*
	 * 本次执行完成，清除relation_use_map
	 */
	private void removeContVoRedis() {
		Jedis jedis=null;
        try {
        	jedis=jedisPool.getResource();
        	//allMap:comm_id vo,每一个Entry对应一个vo，即返回的Map<String, Object>
        	Map<byte[], byte[]> allMap = jedis.hgetAll("relation_use_map".getBytes("utf-8"));
        	List<byte[]> fields=new ArrayList<byte[]>();
        	Set<Entry<byte[], byte[]>> entrySet = allMap.entrySet();
        	for (Iterator<Entry<byte[], byte[]>> iterator = entrySet.iterator(); iterator.hasNext();) {
				Entry<byte[], byte[]> entry =iterator.next();
				fields.add(entry.getKey());
			}
        	for (byte[] bytes:fields) {
				jedis.hdel("relation_use_map".getBytes("utf-8"), bytes);
			}
        	logger.info("把文章标题、html路径、关键字缓存从redis中清除完成");
		} catch (Exception e) {
			logger.error("从redis清空relation_use_map hash出错:", e);
		}finally{
			if (jedis!=null) {
				jedis.close();
			}
		}
	}
	/*
	 * 所有的doc，包含已经生成的、未生成的，供关联文章标签来使用
	 * relation_use_map  comm_id  vo(comm_id,comm_title,comm_keywords，comm_htmlpath)
	 */
	private void putInContVoRedis() {
		Jedis jedis=null;
        try {
        	jedis=jedisPool.getResource();
        	StringBuffer sb=new StringBuffer();
			sb.append(" select c.comm_id,c.comm_title,c.comm_keywords,c.comm_htmlpath  ");
			sb.append(" from lzz_commoncontent c  where  c.comm_keywords is not null ");
			List<Map<String, Object>> list = contentInfoDao.queryForList(sb.toString());
			for(Map<String, Object> m:list){
				ContRedisVo vo=new ContRedisVo();
				vo.setComm_id(m.get("comm_id").toString());
				vo.setComm_title(m.get("comm_title").toString());
				vo.setComm_keywords(m.get("comm_keywords").toString());
				vo.setComm_htmlpath(m.get("comm_htmlpath")!=null?m.get("comm_htmlpath").toString():"");
				jedis.hset("relation_use_map".getBytes("utf-8"), vo.getComm_id().getBytes("utf-8"),
						SerializationUtil.serialize(vo));
			}
			logger.info("把文章标题、html路径、关键字缓存到redis完成");
		} catch (Exception e) {
			logger.error("把文章标题、html路径、关键字缓存到redis出错:", e);
		}finally{
			if (jedis!=null) {
				jedis.close();
			}
		}
	}
	/*
	 * vo中原先有从未生成过的没有comm_htmlpath,这里更新进去,存在于多个栏目
	 */
	private void updateRedisVo(String id,String comm_htmlpath) {
		Jedis jedis=null;
        try {
        	jedis=jedisPool.getResource();
        	byte[] voBytes = jedis.hget("relation_use_map".getBytes("utf-8"), id.getBytes());
        	if (voBytes!=null) {
        		ContRedisVo vo = (ContRedisVo) SerializationUtil.deserialize(voBytes);
        		if (StringUtils.isBlank(vo.getComm_htmlpath())) {
        			vo.setComm_htmlpath(comm_htmlpath);
            		jedis.hset("relation_use_map".getBytes("utf-8"), id.getBytes("utf-8"),
            				SerializationUtil.serialize(vo));
				}
			}
		} catch (Exception e) {
			logger.error("更新reids中vo对象文档html路径出错:", e);
		}finally{
			if (jedis!=null) {
				jedis.close();
			}
		}
	}
	/**
	 * @param request
	 * @param response
	 * @param map:栏目等信息
	 * @return
	 */
	private  String makePerContInOneCln(HttpServletRequest request,HttpServletResponse response
			,Map<String, Object> map,Map<String, Object> cachedOutputMap) throws Exception{
		logger.info("正在生成栏目"+map.get("name")+"的文档....");
		String ret = null;//返回值
		StringBuffer sb=new StringBuffer();
		String type = request.getParameter("type");//是否是增量
		// cont.column_id其他标签要用到，即便页面不获取也要查出来
		generateDocSqlComm(map.get("additionaltable").toString(), sb);
		sb.append("   where cont.column_id=? and cont.comm_title is not null ");
		if ("incr".equals(type)) {
			sb.append(" and cont.comm_html_isupdated='no' ");
		}
		List<Map<String, Object>> list=contentInfoDao.queryForList(sb.toString(),map.get("id").toString());
		int len=list.size();//当前栏目下需要更新的文档数量
		if (len>0) {
			String tplName = map.get("contenttplname").toString();
			//output_cont_ content_article_ .jsp
			String output=LzzConstants.OUTPUTCONTJSP+FilenameUtils.getBaseName(tplName)+"_"+LzzConstants.SUFFIX;
			if (cachedOutputMap.get(output)==null) {//每个栏目下的文章的模板都一样，所以output.jsp的内容就生成一次就好了
				cachedOutputMap.put(output, output);
				ret = generateOutputjspContent(request, tplName,output);
				if (ret!=null) {
					return ret;
				}
			}
			//更新文档属性htmlpath到数据库，让之后的上一篇下一篇标签能一次就取到值 
			updateContField2Db(map,list,request,len);
			staticCont(len,list,request,response,output,map.get("name").toString());
		}
		logger.info("栏目"+map.get("name")+"的文档生成完成!....");
		return null;
	}
	/*
	 * 本来改为了多线程但是事务不起作用，还是改回来吧，以后再也不更新cms的代码了，此次是最后一次2018-8-04 17:39
	 */
	private void staticCont(int len, List<Map<String, Object>> list,
		final HttpServletRequest request,final HttpServletResponse response,final String output
		, String clnName)throws Exception{
	  logger.info("开始生成栏目"+clnName+"的静态文件");
	  final String basePath = LzzcmsUtils.getBasePath(request);
	  final String backServletPath = LzzConstants.getInstance().getBackServletPath();	
	  for (final Map<String, Object> contentMap:list) {//生成每一个文档的静态文件
		  try {
			request.setAttribute("onecont", contentMap);
			contentMap.put("comm_click", getClickScript(basePath, backServletPath, contentMap));
			//if标签使用
			Set<String> mapkeySet = contentMap.keySet();
			for (Iterator<String> iterator = mapkeySet.iterator(); iterator.hasNext();) {
				String mapKey = iterator.next();
				request.setAttribute(mapKey,contentMap.get(mapKey));
			}
			RequestDispatcher dispatcher = 
			  request.getSession().getServletContext().getRequestDispatcher(getMyTmpDir(request)+output);//"/tmp/admin/123.jsp"
			LzzResponseWrapper lzzResponse=new LzzResponseWrapper(response);
			dispatcher.include(request, lzzResponse);
			String resHtml = lzzResponse.getResHtml();
			writeToDestHtml(contentMap.get("destHtmlPath").toString(), resHtml);
		  } catch (Exception e) {
			 logger.error("得到html串出错：",e);
		  }
	  }//生成每一个文档的静态文件
	  logger.info("栏目"+clnName+"的静态文件生成完成");
	}
	private void updateContField2Db(Map<String, Object> map, List<Map<String, Object>> list
			, HttpServletRequest request, int len) {
		logger.info("开始更新栏目"+map.get("name")+"的文档属性信息....");
		List<Object[]> batchArgs=new ArrayList<Object[]>();
		Date nowDate = new Date();
		//得到附加表的富文本字段list，让等下更新comm_thumbpic字段使用
		StringBuffer sb=new StringBuffer();
		sb.append("select e.colname from lzz_extracolumnsdescforchl e where e.additionaltable=? ");
		sb.append(" and e.coltype='richtext' ");
		List<Map<String, Object>> extracolumnsList=contentInfoDao.queryForList(sb.toString(),
				map.get("additionaltable"));
		for (int i = 0; i <len; i++) {//构造每一个文档的htmlpath参数以供更新到数据库
			Map<String, Object> contentMap=list.get(i);
			String relativeHtmlPath = getHtmlPathForOneCont(request, map, nowDate, contentMap);
			//以前更新了位置，修复了上一篇下一篇取不到htmlpath的问题，现在当前文章连接（针对新增的文章才有这个问题）也取不到
			//可能是数据库事务的问题，这里使用下面方法解决
			contentMap.put("comm_htmlpath", relativeHtmlPath);
			//使用生成的relativeHtmlPath拼接basePath（数据库的生成方法）解决
			
			Object[] object=new Object[4];
			object[0]=relativeHtmlPath;
			object[1]="yes";//更新过了html 
			object[2]=getThumPic(contentMap,extracolumnsList);
			object[3]=contentMap.get("cont_comm_id");
			batchArgs.add(object);
			updateRedisVo(contentMap.get("cont_comm_id").toString(),relativeHtmlPath);
		}
		String sql="update lzz_commoncontent set comm_htmlpath=?,comm_html_isupdated=?,comm_thumbpic=? where comm_id=?";
		contentInfoDao.batchExecuteSql(sql, batchArgs);
		logger.info("更新栏目"+map.get("name")+"的文档属性信息完成!....");
	}
	private String getThumPic(Map<String, Object> contentMap,
			List<Map<String, Object>> extracolumnsList) {
		//处理缩略图字段comm_thumb，该字段如果在增加文档、更新文档上传了就是实际的值，这里不需要处理，否则值是/uploads/thumb/
		//提取附加字段的第一个富文本字段里面的第一张图片作为头图，如果头图没有，则仍然保持/uploads/thumb/不变
		String thumbPic=contentMap.get("comm_thumbpic").toString();//不会为空的
		if ((LzzConstants.THUMB_UPLOAD_PRE).equals(thumbPic)) {//默认值需要处理
			int size = extracolumnsList.size();
			if (size!=0) {//附加字段没有富文本类型，不需要处理
				Map<String,Object> oneExtraColumn=new LinkedCaseInsensitiveMap<Object>();
				oneExtraColumn=extracolumnsList.get(0);//用第一个富文本字段
				Set<Entry<String, Object>> entrySet = oneExtraColumn.entrySet();
				Iterator<Entry<String, Object>> iterator = entrySet.iterator();
				if (iterator.hasNext()) {
					Entry<String, Object> next = iterator.next();
					String value = next.getValue().toString();//colname=mainbody
					Object addforCont = contentMap.get(value);
					if (addforCont!=null) {
						Document document = Jsoup.parse(addforCont.toString());
						//有些是富文本里面的标签，是http开头的，这里限制汉语/uploads
						String cssSelect="img[src*="+LzzConstants.UPLOADS+"]";
						Elements imgs = document.select(cssSelect);
						if (imgs.size()!=0) {
							thumbPic= imgs.get(0).attr("src");
							thumbPic=thumbPic.substring(thumbPic.indexOf(LzzConstants.UPLOADS));
						}
					}
				}
			}
		}
		return thumbPic;
	}
	private void generateDocSqlComm(String additionaltable, StringBuffer sb) {
		sb.append(" select cont.comm_id cont_comm_id,cont.column_id,cont.comm_title,cont.comm_shorttitle,cont.comm_click,DATE_FORMAT(cont.comm_publishdate,'%Y-%m-%d') comm_publishdate ");
		sb.append(" ,cont.comm_keywords,cont.comm_desc,cont.comm_thumbpic,cont.comm_intro,cont.comm_htmlpath,cont.comm_src_url ");
		sb.append("  ,cln.name,chl.channelname,s_src.come_from,s_author.author_name ");
		sb.append(" ,s_defineflag.define_flag,addi.*  ");
		sb.append(" from lzz_commoncontent cont LEFT JOIN lzz_columninfo  cln on cont.column_id=cln.id LEFT JOIN  lzz_channelinfo chl ON cln.channel_id=chl.id ");
		sb.append(" left join lzz_source s_src on cont.comm_src=s_src.id ");
		sb.append(" left join lzz_author s_author on cont.comm_author=s_author.id ");
		sb.append(" left join lzz_define_flag  s_defineflag on cont.comm_defineflag=s_defineflag.id ");
		sb.append(" left join "+additionaltable+" addi on cont.comm_id=addi.comm_id ");
	}
	//新增评论或者新增回复的时候需要重新生成html页面：这个生成肯定是只有评论区发生变化了，所有没有makeCont那么复杂，
	//新增方法来实现
	public String makeContForComment(String docid,HttpServletRequest request
			,HttpServletResponse response){
		Map<String, Object> cachedOutputMap=new HashMap<String,Object>();//makeCln makeCont 都有，
		//不要写成成员变量不然修改了模板缓存了就不生效了。
		//获取附加表
		StringBuffer sb=new StringBuffer();
		sb.append(" select chl.additionaltable,i.contenttplname from lzz_channelinfo  chl  ");
		sb.append(" join lzz_columninfo i  on i.channel_id=chl.id   ");
		sb.append(" join lzz_commoncontent  cont  on i.id=cont.column_id   ");
		sb.append(" where cont.COMM_ID=? ");
		ContentInfoDao contentInfoDao = SpringBeanFactory.getBean("contentInfoDaoImpl", ContentInfoDao.class);
		Map<String, Object> infoMap = contentInfoDao.queryForMap(sb.toString(), docid);
		sb.setLength(0);
		//获取本篇docid对应文档的信息
		generateDocSqlComm(infoMap.get("additionaltable").toString(), sb);
		sb.append(" where cont.COMM_ID=? ");
		Map<String, Object> contentMap=contentInfoDao.queryForMap(sb.toString(), docid);
		 request.setAttribute("onecont", contentMap);
		 String basePath = LzzcmsUtils.getBasePath(request);
		 String backServletPath = LzzConstants.getInstance().getBackServletPath();
		 contentMap.put("comm_click", getClickScript(basePath, backServletPath, contentMap));
		 //处理文档对应模板 jsp的生成
		 String tplName=infoMap.get("contenttplname").toString();
		//output_cont_ content_article_ comment_ .jsp
		 String output=LzzConstants.OUTPUTCONTJSP+FilenameUtils.getBaseName(tplName)+"_"
				 +"comment_"+LzzConstants.SUFFIX;
		 if (cachedOutputMap.get(output)==null) {//每个栏目下的文章的模板都一样，所以output.jsp的内容就生成一次就好了
			 cachedOutputMap.put(output, output);
			 String ret = generateOutputjspContent(request, tplName,output);
			 if (ret!=null) {
				return ret;
			 }
		 }
		 RequestDispatcher dispatcher = request.getSession().getServletContext().getRequestDispatcher(getMyTmpDir(request)+output);//"/tmp/admin/123.jsp"
		 LzzResponseWrapper lzzResponse=new LzzResponseWrapper(response);
		 try {
			dispatcher.include(request, lzzResponse);
		 } catch (Exception e) {
			logger.info("得到html串出错：",e);
			return e.getMessage();
		 }
		 String resHtml = lzzResponse.getResHtml();
		 String destHtmlPath=getRealPath(request,contentMap.get("comm_htmlpath").toString());
		 writeToDestHtml(destHtmlPath, resHtml);
		 return null;
	}
	private String getHtmlPathForOneCont(HttpServletRequest request, Map<String, Object> map, Date nowDate,
			Map<String, Object> contentMap) {
		//如果原先有html文件且不再同一个目录的不要删除（其他如果有此文章连接就找不到了）
		String destHtmlPath=null;
		String relativeHtmlPath=null;
		String destHtmlParentDir=null;
		if(contentMap.get("comm_htmlpath")!=null&&StringUtils.isNotBlank(contentMap.get("comm_htmlpath").toString())){
			String existCommHtmlpath = contentMap.get("comm_htmlpath").toString();
			destHtmlParentDir=existCommHtmlpath.substring(0, existCommHtmlpath.lastIndexOf("/")+1);//s/shujuku/2016/07/
			destHtmlPath = getRealPath(request, existCommHtmlpath);
			relativeHtmlPath=existCommHtmlpath;
		}else{
			String curYear=LzzcmsUtils.getPatternDateString("yyyy", nowDate);//2016
			String curMonth=LzzcmsUtils.getPatternDateString("MM", nowDate);//07
			String destHtmlName=LzzcmsUtils.getPinYinHeadChar(contentMap.get("comm_title").toString());//最终文件名，拼音首字母,文章可能重名，不能一样前端控制
			destHtmlParentDir=map.get("htmldir").toString()+"/"+curYear+"/"+curMonth+"/";
			String generateCommHtmlPath = destHtmlParentDir+destHtmlName+".html";
			destHtmlPath=getRealPath(request, generateCommHtmlPath);//最终生成的html:s/shujuku/2016/07/拼音首字母.html
			relativeHtmlPath=generateCommHtmlPath;
		}
		File  destHtmlParentDirFile=new File(getRealPath(request, destHtmlParentDir));
		if (!destHtmlParentDirFile.exists()) {
			destHtmlParentDirFile.mkdirs();
		}
		contentMap.put("comm_htmlpath", destHtmlPath);
		contentMap.put("destHtmlPath", destHtmlPath);
		return relativeHtmlPath;
	}
	private String getClickScript(String basePath, String backServletPath, Map<String, Object> contentMap) {
		//处理点击量
		StringBuffer jsSb=new StringBuffer();
		jsSb.append("<span id='zdwlzzcmsclickid' style='display:inline;margin:0;padding:0'>    ");
		jsSb.append(" \r\n");
		jsSb.append("<script>                                                                 ");
		jsSb.append(" \r\n");
		jsSb.append(" window.onload=function(){ ");
		jsSb.append(" \r\n");
		jsSb.append(" var zdwlzzxmlhttp;                                                                  ");
		jsSb.append(" \r\n");
		jsSb.append(" if (window.XMLHttpRequest){                                                         ");
		jsSb.append(" \r\n");
		jsSb.append("   zdwlzzxmlhttp=new XMLHttpRequest();                                               ");
		jsSb.append(" \r\n");
		jsSb.append(" }else{                                                                              ");
		jsSb.append(" \r\n");
		jsSb.append("   zdwlzzxmlhttp=new ActiveXObject('Microsoft.XMLHTTP');                             ");
		jsSb.append(" \r\n");
		jsSb.append(" }                                                                                   ");
		jsSb.append(" \r\n");
		jsSb.append(" zdwlzzxmlhttp.onreadystatechange=function(){                                        ");
		jsSb.append(" \r\n");
		jsSb.append("   if (zdwlzzxmlhttp.readyState==4 && zdwlzzxmlhttp.status==200){                    ");
		jsSb.append(" \r\n");
		jsSb.append(" 	  document.getElementById('zdwlzzcmsclickid').innerHTML=zdwlzzxmlhttp.responseText;                                     ");
		jsSb.append(" \r\n");
		jsSb.append("   }                                                                                 ");
		jsSb.append(" \r\n");
		jsSb.append(" }                                                                                   ");
		jsSb.append(" \r\n");
		jsSb.append(" zdwlzzxmlhttp.open('POST','"+basePath+backServletPath+"/updateAndgetClick',true);                                     ");
		jsSb.append(" \r\n");
		jsSb.append(" zdwlzzxmlhttp.setRequestHeader('Content-type','application/x-www-form-urlencoded'); ");
		jsSb.append(" \r\n");
		jsSb.append(" zdwlzzxmlhttp.send('comm_id="+contentMap.get("comm_id")+"');                                                      ");
		jsSb.append(" \r\n");
		jsSb.append("  }                                                    ");
		jsSb.append(" \r\n");
		jsSb.append(" </script>                                                     ");
		jsSb.append(" \r\n");
		jsSb.append(" </span>");
		return jsSb.toString();
	}
	public static void main(String[] args) {
		
	}
	@Override
	public List<TreeDto> getTpls(HttpServletRequest request) {
		List<TreeDto> list=new ArrayList<>();
		String tplRealPath=request.getSession().getServletContext().getRealPath("/tpls");
		File file=new File(tplRealPath);
		TreeDto rootTreeDto=new TreeDto();
		rootTreeDto.setText("tpls");
		rootTreeDto.setState("open");
		list.add(rootTreeDto);
		this.recursionDir(file,rootTreeDto);
		return list;
	}
	private void recursionDir(File file,TreeDto parentTreeDto) {
		File[] files = file.listFiles();
		List<TreeDto> crtSubList=new ArrayList<TreeDto>();
		for(File f:files){
			TreeDto treeDto=new TreeDto();
			treeDto.setText(f.getName());
			if (f.isDirectory()) {
				treeDto.setState("closed");
				this.recursionDir(f,treeDto);
			}else {
				Map<String, String> urlMap=new HashMap<String, String>();
				String canonicalPath=null;
				try {
					canonicalPath = f.getCanonicalPath();//F:\Program\workspaces\EclipseWS\lzzcms\web\tpls\about.html
					canonicalPath=canonicalPath.replaceAll("\\\\", "/");
				} catch (IOException e) {
					logger.error("getCanonicalPath出错：",e);
				}
				urlMap.put("url", canonicalPath.substring(canonicalPath.indexOf("tpls")+"tpls".length()+1));
				treeDto.setAttributes(urlMap);
			}
			crtSubList.add(treeDto);
		}
		parentTreeDto.setChildren(crtSubList);
	}
}
