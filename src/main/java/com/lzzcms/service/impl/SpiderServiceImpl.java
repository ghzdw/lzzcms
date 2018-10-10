package com.lzzcms.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.apache.tools.ant.types.FileList.FileName;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.baidu.ueditor.define.State;
import com.baidu.ueditor.upload.BinaryUploader;
import com.google.gson.Gson;
import com.lzzcms.components.HttpClientPool;
import com.lzzcms.dao.ContentInfoDao;
import com.lzzcms.dao.SpiderDao;
import com.lzzcms.install.DbUtils;
import com.lzzcms.model.AdminInfo;
import com.lzzcms.service.ChannelInfoService;
import com.lzzcms.service.ContentInfoService;
import com.lzzcms.service.SpiderService;
import com.lzzcms.utils.KeyWordsUtils;
import com.lzzcms.utils.LzzConstants;
import com.lzzcms.utils.LzzcmsUtils;

import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.MultimediaInfo;

@Service
public class SpiderServiceImpl implements SpiderService{
	private static Logger logger=Logger.getLogger(SpiderServiceImpl.class);
	@Resource
	private SpiderDao spiderDao;
	@Resource
	private ChannelInfoService channelInfoService;
	@Resource
	private ContentInfoService contentInfoService;
	@Resource
	private ContentInfoDao contentInfoDao;
	@Resource
	private HttpClientPool pool;
	@Override
	public Map<String, String> parseUrl(HttpServletRequest request) {
		Map<String, String> retMap=new HashMap<>();
		/*
		 * {id=d090fa9d-5bb7-46dc-acc0-4b9f6d5c03b7, columnId=14,
		 *  website=http://roll.mil.news.sina.com.cn/col/zgjq/index.shtml, 
		 *  addfortb=lzz_addforarticle, cols=[{colName=comm_title, showtip=标题, iscommon=1},
		 *   {colName=comm_shorttitle, showtip=简短标题, iscommon=1},
		 *    {colName=comm_click, showtip=点击量, iscommon=1}, 
		 *    {colName=comm_author, showtip=作者, iscommon=1},
		 *     {colName=comm_publishdate, showtip=发布时间, iscommon=1}, 
		 *     {colName=comm_src, showtip=来源, iscommon=1}, 
		 *     {colName=comm_intro, showtip=简介, iscommon=1}, {colName=mainbody, showtip=文章内容, iscommon=0}]}
		 */
		String param = request.getParameter("param");
		Gson gson=new Gson();
		Map<String,Object> fromJson = gson.fromJson(param, Map.class);
		String website = fromJson.get("website").toString();
		this.getCorrectHtml(website, retMap);
		String wesiteHtml=retMap.get("retStr").toString();
		String realpath= getTmpFilePath(request);//realpath为绝对的实际路径
		File  tmpFile=new File(realpath);
		try {
			FileUtils.writeStringToFile(tmpFile,wesiteHtml,"utf-8");//defaultCharset改为utf-8,因为这里的utf-8要和
			//cfgSprider.jsp的charset声明一致,这样，生成的html的charset本身如果不是utf-8，会乱码，但是在页面上显示的时候是正常的
			//我这里只需要用到显示的正常以及内存中的websiteHtml正常就可以了。
		} catch (IOException e) {
			logger.error(e);
			retMap.put("status",e.getMessage()) ;
		}
		AdminInfo admin = (AdminInfo) request.getSession().getAttribute("admin");
		String tmpDir = "/tmp/"+admin.getUserName()+"/";
		//realpath:F:\Program\workspaces\EclipseWS_mars\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\lzzcms\tmp\admin\50e54d1f.html
		realpath=realpath.replaceAll("\\\\", "/");
		retMap.put("src", LzzcmsUtils.getBasePath(request)+realpath.substring(realpath.indexOf(tmpDir)));
		//把配置保存到数据库中
		StringBuffer sBuffer=new StringBuffer();
		sBuffer.append(" insert into lzz_spider_cfg(id,column_id,col_name,showtip,addfortb,is_common,website) values(?,?,?,?,?,?,?) ");
		Integer columnId=Integer.valueOf(fromJson.get("columnId").toString());
		String uuid=fromJson.get("id").toString();
		String addfortb=fromJson.get("addfortb").toString();
		List<Map<String, String>> colsList= (List<Map<String, String>>) fromJson.get("cols");
		int len = colsList.size();
		for(int i=0;i<len;i++){
			Map<String, String> oneMap = colsList.get(i);
			String colName=oneMap.get("colName");
			String showtip=oneMap.get("showtip");
			Integer iscommon=Integer.valueOf(oneMap.get("iscommon").toString());
			spiderDao.executeSql(sBuffer.toString(), uuid,columnId,colName,showtip,addfortb,Boolean.valueOf(iscommon>0),website);
		}
		return retMap;
	}
	//处理link的href  script的src  a的href img的src 加上basePath,为每一个标签添加一个属性zdwid
	private String processHtml(String websiteHtml, String website) {
		Document document = Jsoup.parse(websiteHtml,website);
		Elements links = document.select("link[href]");//处理link的href  
		this.convertRelativePath(links, "href",null);
		Elements scripts = document.select("script[src]");//处理script的src 
		this.convertRelativePath(scripts, "src",null);
		Elements as = document.select("a[href]");//处理a的href
		this.convertRelativePath(as, "href","a");
		Elements imgs = document.select("img[src]");//处理img的src
		this.convertRelativePath(imgs, "src",null);
		//为元素添加zdwid
		Elements descendantForBody  = document.select("body *");
		int size = descendantForBody.size();
		for (int i = 0; i < size; i++) {
			Element element = descendantForBody.get(i);
			element.attr("zdwid", i+"");
		}
		return document.toString();
	}
	//相对路径转为绝对路径
	private void convertRelativePath(Elements elements,String attrToConvert,String eleToConvert){
		int size = elements.size();
		for (int i = 0; i < size; i++) {
			Element element = elements.get(i);
			String val =element.attr(attrToConvert);
			if (StringUtils.isNotBlank(val)&&!val.toLowerCase().startsWith("javascript:")) {
				val=element.absUrl(attrToConvert);
				element.attr(attrToConvert,val);
			}
			if (eleToConvert!=null) {//a需要去掉href，不然鼠标中间键点击还是能跳转，去掉后加一个zdwhref保存href的值
				val=element.attr(attrToConvert);//得到a的href值，此时已经是转换后的了
				element.attr("zdwhref", val);
				element.removeAttr(attrToConvert);
			}
		}
	}
	//组装临时的html文件路径
	private String getTmpFilePath(HttpServletRequest request) {
		//确保父级目录的存在
		AdminInfo admin = (AdminInfo) request.getSession().getAttribute("admin");
		String tmpDir = "/tmp/"+admin.getUserName()+"/";
		File file = new File(LzzcmsUtils.getRealPath(request,tmpDir));
		if (!file.exists()) {
			file.mkdirs();
		}
		//删除掉原先已经存在的html文件
		File[] files = file.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (FilenameUtils.getExtension(name).equals("html")) {
					return true;
				}else{
					return false;
				}
			}
		});
		for(File oneHtmlFile:files){
			oneHtmlFile.delete();
		}
		//得到随机生成的文件名的绝对路径
		String path=tmpDir+LzzcmsUtils.random()+".html";
		path=LzzcmsUtils.getRealPath(request,path);
		return path;
	}
	/*得到本地html字符串得到其中使用的编码
	 * <meta charset="utf-8">
	 * <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	 */
	private String getWebSiteCharset(String websiteHtml){
		String retCharset =null;
		Document document = Jsoup.parse(websiteHtml);
		Elements elements = document.select("meta[charset]");
		int size = elements.size();
		if (size==1) {//html5 
				Element element = elements.get(0);//只应该有一个
				String attrVal = element.attr("charset");
				if (StringUtils.isNotBlank(attrVal)) {
					retCharset = attrVal.trim().toLowerCase();
				}
		}else{//html
			elements = document.select("meta[content]");
			size = elements.size();
			if (size>0) {//关键字 网站描述导致>0
				for (int i = 0; i <size; i++) {
					Element element = elements.get(i);
					String content = element.attr("content");
					if (content.toLowerCase().contains("charset")) {
						String[] split = content.split("=");
						String val = split[1];
						if (StringUtils.isNotBlank(val)) {
							retCharset = val.trim().toLowerCase();
						}
						break;
					}
				}
			}//else:使用默认utf-8编码
		}
		return retCharset;
	}
	//以特定编码通过网址得到html字符串并返回
	private Map<String, String> getWebSiteHtml(String website,String charset){
		Map<String, String> retMap=new HashMap<>();
		String retStr =pool.executeGet(website, charset);
		if (StringUtils.isBlank(retStr)) {
			retMap.put("status", "error");
		}else {
			retMap.put("retStr", retStr);
		}
		return retMap;
	}
	@Override
	public List<Map<String, Object>> getCfgById(String cfgId) {
		StringBuffer stringBuffer=new StringBuffer();
		stringBuffer.append(" select id,column_id,col_name,showtip,addfortb,is_common,website from lzz_spider_cfg where id=? ");
		List<Map<String, Object>> queryForList = spiderDao.queryForList(stringBuffer.toString(), cfgId);
		return queryForList;
	}
	@Override
	public Map<String, String> startSpiderById(HttpServletRequest request) {
		Map<String, String> retMap=new HashMap<>();
		/*
		var obj={
			id:uuid,
			columnId:2,
			website:http,
			addfortb:'lzz_addforarticle',
			commCols:"comm_title,comm_click...",
			extraCols:"mainbody,mainbody2..."
			cols:[{
					colName:comm_title,
					selectors:[]
					},{
						colName:mainbody,
						selectors:[]
					}
			       
			       ]
		}
		举例：
		{"cols":[{"colName":"comm_title","selectors":["body > div.wrap > div.row.col2.clearfix > 
		div.main > div.fixList > ul.linkNews:first-child > li:nth-child(5) > a:first-child","body > div.wrap > 
		div.row.col2.clearfix > div.main > div.fixList > ul.linkNews:first-child > li:nth-child(3) > a:first-child",
		"body > div.wrap > div.row.col2.clearfix > div.main > div.fixList > ul.linkNews:nth-child(2) > li:nth-child(2) 
		> a:first-child","body > div.wrap > div.row.col2.clearfix > div.main > div.fixList > ul.linkNews:nth-child(2) 
		> li:nth-child(3)"]},{"colName":"mainbody","selectors":["body > div.wrap > div.row.col2.clearfix > div.main 
		> div.fixList > ul.linkNews:nth-child(3) > li:nth-child(4) > a:first-child","body > div.wrap 
		> div.row.col2.clearfix > div.main > div.fixList > ul.linkNews:nth-child(3) > li:nth-child(5) > a:first-child",
		"body > div.wrap > div.row.col2.clearfix > div.main > div.fixList > ul.linkNews:nth-child(4) > li:nth-child(3) 
		> a:first-child","body > div.wrap > div.row.col2.clearfix > div.main > div.fixList > ul.linkNews:nth-child(4) 
		> li:nth-child(4)","body > div.wrap > div.row.col2.clearfix > div.main > div.fixList > ul.linkNews:nth-child(4) 
		> li:nth-child(5) > a:first-child"]}],"uuid":"dae03780-d061-49de-a4fb-3ec8efb76d29",
		"columnId":"17","website":"http://roll.mil.news.sina.com.cn/col/zgjq/index.shtml",
		"addfortb":"lzz_addforarticle","commCols":"comm_title","extraCols":"mainbody"}
		*/
		String param = request.getParameter("param");
		Gson gson=new Gson();
		Map<String,Object> paramMap = gson.fromJson(param, Map.class);
//		String uuid=paramMap.get("uuid").toString();//清除配置表
		//通过网址再得到对应的html
		String website = paramMap.get("website").toString();
		this.getCorrectHtml(website,retMap);
		String websiteHtml=retMap.get("retStr");
		Document document = Jsoup.parse(websiteHtml);
		
		String columnId=paramMap.get("columnId").toString();// 
		String addfortb=paramMap.get("addfortb").toString();// 
		String commCols=paramMap.get("commCols").toString();//"a,b,c";//传过来的
		String extraCols=paramMap.get("extraCols").toString();//"d,e";//传过来的
		String[] commColsArr = commCols.split(",");
		String[] extraColsArr = extraCols.split(",");
		int commColsArrLen=commCols.equals("")?0:commColsArr.length;
		int extraColsArrLen=extraCols.equals("")?0:extraColsArr.length;
		List<Map<String, Object>> cols=(List<Map<String, Object>>) paramMap.get("cols");
		Map<String, Object> flipMap=(Map<String, Object>) paramMap.get("flip");
		int max=0;//通过比较每个列的选择器数组，得到最大的，代表本次总共要插入几条记录
		for(Map<String, Object> map:cols){
			List<String> list=(List<String>) map.get("selectors");
			if (list.size()>max) {
				max=list.size();
			}
		}
		if (flipMap.size()>0) {//有翻页
			//把html的内容串换一下
			int flip=0;
			Object object = null;
			while((object = flipMap.get("fliphref"))!=null&&(flip<3)){
				insertOnePage(max, commCols, columnId, cols, commColsArrLen, document, 
						commColsArr, addfortb, extraCols, extraColsArrLen, extraColsArr);
				String fliphref=(String) object;
				this.getCorrectHtml(fliphref,retMap);
				websiteHtml=retMap.get("retStr");
				document = Jsoup.parse(websiteHtml);
				Elements select = document.select(flipMap.get("flipcss").toString());
				for(int i=0;i<select.size();i++){
					Element element = select.get(i);
					if (element.text().contains(flipMap.get("fliptxt").toString())) {
						flipMap.put("fliphref", element.attr("zdwhref"));
					}
				}
				flip++;
			}
		}else {//没有翻页
			insertOnePage(max, commCols, columnId, cols, commColsArrLen, document, 
					commColsArr, addfortb, extraCols, extraColsArrLen, extraColsArr);
	    }
		//清除配置
		StringBuffer sBuffer=new StringBuffer();
		sBuffer.append(" delete from lzz_spider_cfg where 1=1 ");
		spiderDao.executeSql(sBuffer.toString());//按uuid清除有时没有点击执行
		retMap.remove("retStr");
		retMap.put("status", "ok");
		return retMap;
	}
	private void insertOnePage(int max,String commCols,String columnId,List<Map<String, Object>> cols
			,int commColsArrLen,Document document,String[] commColsArr,String addfortb,String extraCols
			,int extraColsArrLen,String[] extraColsArr){
		for (int i = 0; i < max; i++) {//每一行
			StringBuffer sBuffer=new StringBuffer();
			List<Object> params=new ArrayList<Object>();
			List<Object> extraparams=new ArrayList<Object>();
			String tmpClns="";
			String tmpConds="";
			if (StringUtils.isNotBlank(commCols)) {
				tmpClns="column_id,"+commCols;
				tmpConds="?,";
			}else{
				tmpClns="column_id";
				tmpConds="?";
			}
			sBuffer.append(" insert into lzz_commoncontent("+tmpClns+") values("+tmpConds);
			params.add(columnId);
			this.makeHolderAndVal(sBuffer,params,cols,commColsArrLen,i,document,commColsArr);
			sBuffer.append(" ) ");
			logger.info("爬虫插入公共表的sql:"+sBuffer.toString());
			spiderDao.executeSql(sBuffer.toString(), params.toArray());
			 int generateId = spiderDao.queryForInt("select LAST_INSERT_ID()");
			sBuffer.setLength(0);
			if (StringUtils.isNotBlank(extraCols)) {
				tmpClns="comm_id,"+extraCols;
				tmpConds="?,";
			}else{
				tmpClns="comm_id";
				tmpConds="?";
			}
			sBuffer.append(" insert into "+addfortb+"("+tmpClns+") values("+tmpConds);
			extraparams.add(generateId);
			this.makeHolderAndVal(sBuffer,extraparams,cols,extraColsArrLen,i,document,extraColsArr);
			sBuffer.append(" ) ");
			logger.info("爬虫插入附加表的sql:"+sBuffer.toString());
			spiderDao.executeSql(sBuffer.toString(), extraparams.toArray());
	    }
	}
	/*生成占位符?和加入对应的值到params中
	 * sb:动态生成的sql，包括公共表和附加表的插入语句
	 * params:和sb里面?对应的值
	 * cols:前端传来的数组，放的是列和其选择器的对象
	 * arrLen:公共字段组成的数组或者附加字段组成的数组的长度
	 * nowLine:当前正在插入第行数
	 * document：jsop对象
	 * arr:公共字段组成的数组或者附加字段组成的数组
	*/
	private void makeHolderAndVal(StringBuffer sb, List<Object> params,
			List<Map<String, Object>> cols, int arrLen, int nowLine, Document document, String[] arr) {
		for (int j = 0; j < arrLen; j++) {//公共表或附加表里面的每个字段,插入第几行的第几个字段
			List<String> selectorsList=null;//存放字段的选择器数组
			if (j==(arrLen-1)) {
				sb.append(" ? ");
			}else {
				sb.append(" ?, ");
			}
			for(Map<String, Object> map:cols){//map由一个字段和其选择器组成的,没有的话也不会循环
				Object objectColName = map.get("colName");
				if (objectColName.toString().equals(arr[j])) {//得到当前字段的选择器数组
					selectorsList=(List<String>) map.get("selectors");
					break;
				}
			}
			//前端虽然选择了某个字段，但是没有给他可视化配置内容，导致根本没有传过来该字段(在cols中没有该字段和其选择器数组的对象)
			if (selectorsList==null) {
				params.add(null);
			}else {
				//每个列选择的数量可能不一样，所以要做判断
				if (nowLine<selectorsList.size()) {//还没溢出
					String cssSelector = selectorsList.get(nowLine);//第n行就从字段的选择器数组里面取第n个选择器，并得到该选择器代表的值
					Elements elements = document.select(cssSelector);//这里的cssSelector采用路径定位，本身全局唯一，选到的元素也是全局唯一
					params.add(elements.get(0).text());
				}else {
					params.add(null);
				}
			}
		}
	}
	//得到正确编码且处理过的html放入retMap中
	private  Map<String, String> getCorrectHtml(String website, Map<String, String> retMap) {
		String defaultCharset="utf-8";
		Map<String, String> map = getWebSiteHtml(website, defaultCharset);
		if (map.get("status")!=null) {
			retMap.put("status",map.get("status")) ;
		}
		String websiteHtml=map.get("retStr");
		String webSiteCharset = getWebSiteCharset(websiteHtml);
		if (webSiteCharset!=null) {
			if (!webSiteCharset.equals(defaultCharset)) {
				map=getWebSiteHtml(website, webSiteCharset);
				websiteHtml=map.get("retStr");
				defaultCharset=webSiteCharset;//保持defaultCharset的正确性
			}
		 }//==null就使用默认编码读取的websiteHtml了
		websiteHtml=this.processHtml(websiteHtml,website);
		retMap.put("retStr", websiteHtml);
		return retMap;
	}
	@Override
	public List<String> startSpiderHand(HttpServletRequest request,HttpServletResponse response) {
		List<String> errorList=new ArrayList<String>();
		//得到website对应网址的html字符串
		String website = request.getParameter("website");
		//http://www.roadjava.com/linux/@[1-100]@.html
		if (website.contains("@[")&&website.contains("]@")) {
		 Pattern pattern = Pattern.compile("(.*)(@\\[(.*)-(.*)\\]@)(.*)",Pattern.CASE_INSENSITIVE);
	         Matcher matcher = pattern.matcher(website);
	         if (matcher.find()) {
	            Integer start = Integer.valueOf(matcher.group(3));
	            Integer end = Integer.valueOf(matcher.group(4));
	            for (int i = start; i <= end; i++) {
	            	String oneurl=matcher.group(1)+i+matcher.group(5);
	            	String ret = spiderOnePageHand(request, oneurl);
	            	if (ret!=null) {
	    				errorList.add(ret);
	    			}
				}
	         }  
		}else {
			String ret = spiderOnePageHand(request, website);
			if (ret!=null) {
				errorList.add(ret);
			}
		}
		return errorList;
	}
	@Override
	public List<String> item_href_value_list(String list_url,String list_item_selector){
		Map<String, String> retMap=new HashMap<>();
		this.getCorrectHtmlHand(list_url,retMap);
		String websiteHtml=retMap.get("retStr");
		if (StringUtils.isBlank(websiteHtml)) {
			logger.info("无法获取"+list_url+"的html");
			return null;
		}
		Document document = Jsoup.parse(websiteHtml,list_url);
		Elements as = document.select(list_item_selector);
		if (as.size()==0) {
			logger.info("获取不到一个crawl配置对应的N多个a的超链接值,当前获取的html内容为:"+websiteHtml
					+",选择器："+list_item_selector);
			return null;
		}
		List<String> retList=new ArrayList<String>();
		for(Element e:as){
			String absUrl = e.absUrl("href");
			retList.add(absUrl);
		}
		return retList;
	}
	private String spiderOnePageHand(HttpServletRequest request,
			String website) {
		Map<String, String> retMap=new HashMap<>();
		this.getCorrectHtmlHand(website,retMap);
		String websiteHtml=retMap.get("retStr");
		if (StringUtils.isBlank(websiteHtml)) {
			logger.info(website+"抓取失败");
			return website;
		}
		Document document = Jsoup.parse(websiteHtml,website);
		
		String channelId = request.getParameter("channel");
		String addfortb = request.getParameter("addfortb");
		//公共表
		StringBuffer sBuffer=new StringBuffer();
		List<Object> params=new ArrayList<Object>();
		List<Object> extraparams=new ArrayList<Object>();
		String clns="column_id,comm_title,comm_shorttitle,comm_click,comm_author";
			clns+=",comm_publishdate,comm_src,comm_intro,comm_thumbpic,comm_modifydate";
			clns+=",comm_keywords,comm_desc,comm_src_url";
		String conds="?,?,?,?,?,?,?,?,?,?,?,?,?";
		sBuffer.append(" insert into lzz_commoncontent("+clns+") values("+conds+" ) ");
		String comm_title_result=this.makeParam(params, request.getParameter("column"), document, website, 
				request.getParameter("comm_title"), 
				request, request.getParameter("mainbody"), request.getParameter("will_exclude_selector"));
		if (StringUtils.isBlank(comm_title_result)) {
			//附加表
			String clnsAddFor="comm_id";
			String condsAddFor="?";
			StringBuffer sbAddfor=new StringBuffer();
			List<Map<String, Object>> list = channelInfoService.channelAdvancedCfg(channelId);
			int extraColsCountForChl = list.size();
			if (extraColsCountForChl==1) {
				Map<String, Object> firstMap = list.get(0);
				if (firstMap.get("info")!=null) {//info!=null时是有异常或者附加表没有字段(没字段时附加表还未创建)
					clnsAddFor="";
					condsAddFor="";
				}else {
					clnsAddFor+=","+firstMap.get("colname");
					condsAddFor+=",?";
				}
			}else{
				for (int i = 0; i < extraColsCountForChl; i++) {
					Map<String, Object> map = list.get(i);
					clnsAddFor+=","+map.get("colname");
					condsAddFor+=",?";
				}
			}
			sbAddfor.append(" insert into "+addfortb+"("+clnsAddFor+") values("+condsAddFor+" ) ");
			if (StringUtils.isNotBlank(clnsAddFor)&&StringUtils.isNotBlank(condsAddFor)) {//comm_id,能进来的话这里必有一个字段
				this.makeExtraParam(extraparams,request,document,clnsAddFor); 
				logger.info("手工爬虫插入公共表的sql:"+sBuffer.toString());
				spiderDao.executeSql(sBuffer.toString(), params.toArray());
				int generateId = spiderDao.queryForInt("select LAST_INSERT_ID()");
				extraparams.add(0,generateId);
				logger.info("手工爬虫插入附加表的sql:"+sbAddfor.toString());
				spiderDao.executeSql(sbAddfor.toString(), extraparams.toArray());
				//更新图片保存信息的cont_id
				updateUploadContId(request, generateId);
			}
		}else {
			if (!"已采集".equals(comm_title_result)) {//不执行可能是因为已经采集过了
				logger.info("标题无法正常获取,当前条目跳过");
			}else {
				logger.info("当前条目已经采集过,当前条目跳过");
			}
		}		
		return null;
	}
	//基本字段就那几个字段嘛，不需要特殊处理，这个附加字段可能需要特殊处理，比如去除超链接，过滤某些内容
	private void makeExtraParam(List<Object> extraparams,
			HttpServletRequest request, Document document, String clns) {
		 String[] clnsArr = clns.split(",");//>=2
		 int length = clnsArr.length;
		 for (int i = 1; i < length; i++) {//i=0是为comm_id，放到extraparams里面了
			String extraClnName = clnsArr[i];
			String extraClnNameSeletor = request.getParameter(extraClnName);//通过附加字段名字得到对应的选择器		 
			if (StringUtils.isNotBlank(extraClnNameSeletor)) {
				Elements elements = document.select(extraClnNameSeletor); 
				if (elements.size()==0) {//页面给定的选择器并未选择到内容，不然报index 0 size 0的错误
					extraparams.add(null);
				}else{
					Element element = elements.get(0);
					saveNetImg2Local(request, element);
					//不能放在开头，如果放在开头，先把属性给去掉了，通过与该属性有关的选择器就获取不到内容了
					processHtmlHand(document);
					extraparams.add(element.outerHtml());
				}
			}else {
				extraparams.add(null);
			}
		 }
	}
	private void saveNetImg2Local(HttpServletRequest request, Element element) {
		//保存element下的照片到本地并修改img的src属性为本地路径
		Elements imgs = element.select("img");//有的是按需加载，没有src属性data-original-src
		int size = imgs.size();
		for (int j = 0; j < size; j++) {
			Element oneimgEle = imgs.get(j);
			String srcVal =oneimgEle.attr("src");
			String	absSrcVal=null;
			if (StringUtils.isNotBlank(srcVal)) {
			   	absSrcVal=oneimgEle.absUrl("src");
			}else {//如果没有src属性就取第一个含有src的属性的值去尝试
				Attributes attributes = oneimgEle.attributes();
				Iterator<Attribute> iterator = attributes.iterator();
				while (iterator.hasNext()) {
					Attribute oneAttr = iterator.next();
					if (oneAttr.getKey().toLowerCase().contains("src")) {
						absSrcVal=oneimgEle.absUrl(oneAttr.getKey());
						break;
					}
				}
			}
			if (StringUtils.isBlank(absSrcVal)) {
				continue;
			}
			String pathInUploadsDir = this.saveOneImg(absSrcVal,request);
			saveUploadInfo2Db(pathInUploadsDir, request);
			oneimgEle.attr("src",LzzcmsUtils.getBasePath(request)+pathInUploadsDir);
		}
	}
	//把img信息保存在数据库中
	private  void saveUploadInfo2Db(String pathInUploadsDir, HttpServletRequest request){
		StringBuffer sBuffer=new StringBuffer();
		sBuffer.append(" insert into lzz_uploadfile_info(original_name,file_size,file_type,width,height, ");
		sBuffer.append(" time_long,cont_id,file_path) values(?,?,?,?,?,?,?,?) ");
		String realPath = LzzcmsUtils.getRealPath(request, pathInUploadsDir);
		String original_name=FilenameUtils.getName(realPath);
		File input = new File(realPath);
		BufferedImage bi=null;
		int width=0;
		int height=0;
		try {
			bi = ImageIO.read(input);
			width=bi.getWidth();
			height=bi.getHeight();
		} catch (IOException e1) {
			logger.error("ImageIO读取图片出错",e1);
		}
		long file_size = input.length();
		int generateId = 0;//前边插入完成后生成的id
		Connection connection =null;
		PreparedStatement ps=null;
		try {
			connection=DbUtils.getConn();
			ps=connection.prepareStatement(sBuffer.toString());
			ps.setObject(1, original_name);
			ps.setObject(2, file_size);
			ps.setObject(3, "image");
			ps.setObject(4, width);
			ps.setObject(5, height);
			ps.setObject(6, 0);
			ps.setObject(7, 0);
			ps.setObject(8, pathInUploadsDir);
			ps.executeUpdate();
			ResultSet generatedKeys = ps.getGeneratedKeys();
			if(generatedKeys.next()){
				Object object = generatedKeys.getObject(1);
				if (object!=null) {
					generateId=Integer.valueOf(object.toString());
				}
			}
		} catch (SQLException e) {
			logger.error("把上传好的文件信息保存在数据库中:",e);
		}finally{
			DbUtils.releasePs(ps);
			DbUtils.releaseConn(connection);
		}
		if (generateId!=0) {//说明插入成功了
			List<Integer> list=(List<Integer>) request.getSession().getAttribute("spider_uploadIds");
			if (list==null) {
				list=new ArrayList<Integer>();
			}
			list.add(generateId);
			request.getSession().setAttribute("spider_uploadIds", list);
		}
	}	
	private String saveOneImg(String srcVal, HttpServletRequest request) {
		String uploadPath=null;
		CloseableHttpResponse response =null;
		try {
			response = pool.getCloseableHttpResponse(srcVal);
			HttpEntity entity = response.getEntity();
			if (entity!=null) {
				String value = entity.getContentType().getValue();
				if (!value.startsWith("image")) {
					return null;
				}
				int statusCode=response.getStatusLine().getStatusCode();
				if (statusCode!=200) {
					return null;
				}
				String suffix=".jpg";
				value=value.toLowerCase();
				if (value.contains("jpg")||value.contains("jpeg")) {
					suffix=".jpg";
				}else if (value.contains("bmp")||value.contains("bitmap")) {
					suffix=".bmp";
				}else if (value.contains("png")) {
					suffix=".png";
				}else if (value.contains("gif")) {
					suffix=".gif";
				}
				byte[] byteArray = EntityUtils.toByteArray(entity);
			    uploadPath = copyFileToUploadsDir(byteArray,request,suffix);
			}
			EntityUtils.consume(entity);
		} catch (Exception e) {
			logger.error("保存爬取过程中的图片出错:",e);
		} finally{
			pool.closeRes(response);
		}
		return uploadPath;
	}
	private String copyFileToUploadsDir(byte[] byteArray,HttpServletRequest request, String suffix) {
		/*360截图20170511015353184.jpg*/
		long nanoTime = System.nanoTime();
		String finalThumbPath=LzzConstants.UPLOADS+"spiderhand/";//"/uploads/thumb/";
		//构建要存放文件的目录的绝对路径
		finalThumbPath+=LzzcmsUtils.getPatternDateString("yyyy/MM/", new Date());///uploads/thumb/2017/05/
		File dir=new File(LzzcmsUtils.getRealPath(request,finalThumbPath));/*F:\\Program\\workspaces\\EclipseWS\\lzzcms\\web\\uploads\\thumb\\2017\\05*/
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String newFileName=nanoTime+suffix;//312.jpg
		finalThumbPath+=newFileName;// /uploads/thumb/2017/05/312.jpg
		//targetPath:F:\\Program\\workspaces\\EclipseWS\\lzzcms\\web\\uploads\\thumb\\2017\\05\\312.jpg
		String targetPath = LzzcmsUtils.getRealPath(request,finalThumbPath);
		File destFile=new File(targetPath);
		OutputStream os=null;
		try {
		  os=new FileOutputStream(destFile);
		  os.write(byteArray);
		} catch (Exception e) {
			logger.error("保存爬取过程中的图片出错:",e);
		}finally {
			LzzcmsUtils.closeOs(os);
		}
	   return finalThumbPath;
	}
	//得到正确编码且处理过的html放入retMap中
	private  Map<String, String> getCorrectHtmlHand(String website, Map<String, String> retMap) {
		String defaultCharset="utf-8";
		Map<String, String> map = getWebSiteHtmlHand(website, defaultCharset);
		if (map.get("status")!=null) {
			retMap.put("status",map.get("status")) ;
		}else {
			String websiteHtml=map.get("retStr");
			String webSiteCharset = getWebSiteCharset(websiteHtml);//可和自动化的函数共用
			if (webSiteCharset!=null) {
				if (!webSiteCharset.equals(defaultCharset)) {
					map=getWebSiteHtmlHand(website, webSiteCharset);
					websiteHtml=map.get("retStr");
					defaultCharset=webSiteCharset;//保持defaultCharset的正确性
				}
			 }//==null就使用默认编码读取的websiteHtml了
			retMap.put("retStr", websiteHtml);
		}
		return retMap;
	}
	//把<a id='aa' href='https://www.roadjava.com'>哈哈</a>用内容"哈哈"替换
	//去掉所有标签的title和alt属性
	private void processHtmlHand(Document document) {
		//处理掉a的href
		Elements as = document.select("a[href]");
		int size = as.size();
		for(int i=0;i<size;i++){
			Element aElement=as.get(i);
			aElement.replaceWith(new TextNode(aElement.text(), null));
		}
		//删除一些元素和一些无用属性
		Elements allElements = document.select("body *");
		int len=allElements.size();
		for (int i = 0; i < len; i++) {
			Element element = allElements.get(i);
			//去掉link和script标签
			if (element!=null) {
				String tagName = element.tagName();
				if (StringUtils.isNotBlank(tagName)) {
					tagName=tagName.toLowerCase();
					if ("link".equals(tagName)||"script".equals(tagName)) {//去掉link标签
						element.remove();
						continue;
					}
				}
			}
			//除了下面删除的这些属性，其他属性不知道有没有用，暂时不删除
			element.removeAttr("title");
			element.removeAttr("alt");
			//style(可能会影响布局)和class(没用)
			element.removeAttr("class");
			element.removeAttr("style");
			Attributes attributes = element.attributes();
			Iterator<Attribute> iterator = attributes.iterator();
			//java.util.ConcurrentModificationException
			List<String> attrToRemoveList=new ArrayList<String>();
			while(iterator.hasNext()){
				Attribute next = iterator.next();
				if (next!=null) {
					String key = next.getKey();
					if (key.toLowerCase().startsWith("data")) {
						attrToRemoveList.add(key);
					}
				}
			}
			//去掉所有data开头的属性
			for(String attr:attrToRemoveList){
				element.removeAttr(attr);
			}
		}
	}
	//以特定编码通过网址得到html字符串并返回
	private Map<String, String> getWebSiteHtmlHand(String website,String charset){
		Map<String, String> retMap=new HashMap<>();
		String retStr = pool.executeGet(website, charset);
		if (StringUtils.isBlank(retStr)) {
			retMap.put("status", "error");
		}else{
			retMap.put("retStr", retStr);
		}
		return retMap;
	}
	@Override
	public void saveOneHref(String oneHref, String comm_titleSelector,String mainbodySelector
			, String column_id, HttpServletRequest request,String excludeSelector) {
		Map<String, String> retMap=new HashMap<>();
		this.getCorrectHtmlHand(oneHref,retMap);
		String websiteHtml=retMap.get("retStr");
		if (StringUtils.isBlank(websiteHtml)) {
			logger.info("获取地址:"+oneHref+"html失败,当前爬取结束");
			return;
		}
		Document document = Jsoup.parse(websiteHtml,oneHref);
		//公共表
		StringBuffer sBuffer=new StringBuffer();
		List<Object> params=new ArrayList<Object>();
		List<Object> extraparams=new ArrayList<Object>();
		String clns="column_id,comm_title,comm_shorttitle,comm_click,comm_author";
			clns+=",comm_publishdate,comm_src,comm_intro,comm_thumbpic,comm_modifydate";
			clns+=",comm_keywords,comm_desc,comm_src_url";
		String conds="?,?,?,?,?,?,?,?,?,?,?,?,?";
		sBuffer.append(" insert into lzz_commoncontent("+clns+") values("+conds+" ) ");
		String comm_title_result=this.makeParam(params,column_id,document,oneHref,comm_titleSelector
				,request,mainbodySelector,excludeSelector); 
		//如果comm_title_result不为空，说明本次不进行插入了，如果不加这个判断，下面的makeExtraParamTask依然会保存图片
		if (StringUtils.isBlank(comm_title_result)) {
			//附加表
			String	clnsAddfor="comm_id,mainbody";
			String condsAddfor="?,?";
			StringBuffer sbAddfor=new StringBuffer();
			sbAddfor.append(" insert into lzz_addforarticle ("+clnsAddfor+") values("+condsAddfor+" ) ");
			String mainbody_result=this.makeExtraParamTask(extraparams,mainbodySelector,document,request); 
			if (StringUtils.isBlank(mainbody_result)) {
				logger.info("定时任务采集插入公共表的sql:"+sBuffer.toString());
				spiderDao.executeSql(sBuffer.toString(), params.toArray());
				int generateId = spiderDao.queryForInt("select LAST_INSERT_ID()");
				extraparams.add(0, generateId);//对应comm_id的值
				logger.info("定时采集任务插入附加表的sql:"+sbAddfor.toString());
				spiderDao.executeSql(sbAddfor.toString(), extraparams.toArray());
				//更新图片保存信息的cont_id
				updateUploadContId(request, generateId);
			}else {
			    logger.info("内容无法正常获取,当前条目跳过");
			}
		}else {
			if (!"已采集".equals(comm_title_result)) {//不执行可能是因为已经采集过了
				logger.info("标题无法正常获取,当前条目跳过");
			}else {
				logger.info("当前条目已经采集过,当前条目跳过");
			}
		}
	}
	private void updateUploadContId(HttpServletRequest request, int generateId) {
		List<Integer> uploadIdsList=(List<Integer>) request.getSession().getAttribute("spider_uploadIds");
		if (uploadIdsList!=null&&uploadIdsList.size()>0) {
			List<Object[]> arrList=new ArrayList<>();
			for (int i = 0; i <uploadIdsList.size(); i++) {
				Object[] objects=new Object[2];
				objects[0]=generateId;
				objects[1]=uploadIdsList.get(i);
				arrList.add(objects);
			}
		   contentInfoDao.batchExecuteSql("update lzz_uploadfile_info set cont_id=? where upload_id=?"
				   , arrList);
		   request.getSession().removeAttribute("spider_uploadIds");
     }
    }
	private String makeParam(List<Object> params, String column_id, Document document
			, String oneHref, String comm_titleSelector,HttpServletRequest request, String mainbodySelector
			, String excludeSelector) {
		column_id=StringUtils.isBlank(column_id)?"29":column_id;
		params.add(column_id);//29为其他的编号
		String[] split = comm_titleSelector.split("@\\[zdw\\]@");//多个使用@[zdw]@分开
		int length = split.length;
		for (int i = 0; i < length; i++) {
			Elements elements = document.select(split[i]); 
			 if (elements.size()==0&&(i+1)<length) {//不是最后一个
				 continue;
			}else if (elements.size()==0&&(i+1)==length) {//是最后一个了
				logger.info("网址"+oneHref+"无法获取标题,当前地址跳过");
				return "标题不能为空";
			}else{
			   String	title = elements.get(0).text();
			   StringBuffer sb=new StringBuffer();
			   sb.append(" select count(*) count from lzz_commoncontent c where c.COMM_SRC_URL=?  ");
			   Long hasCount=spiderDao.queryForLong(sb.toString(), oneHref);
			   if (hasCount.longValue()!=0L) {
				 logger.info("网址"+oneHref+"已采集,当前地址跳过");
				 return "已采集";//如果在这里return;本方法立即结束，本方法所在父方法还会继续执行
			   }
			   title = getNoRepeatTitle(column_id, title);
			   params.add(title);
			   break;
			}
		}
		//简短标题
		params.add(null);
		//点击量
		Random random = new Random();
		params.add(random.nextInt(100));
		//作者默认为2：其他
		params.add(2);
		//comm_publishdate
		String publishDateString=LzzcmsUtils.getPatternDateString("yyyy-MM-dd HH:mm:ss", new Date());
		params.add(publishDateString);
		//来源默认为3：网络
		params.add(3);
		//comm_intro摘要
		String text=getMainbodyText(document,mainbodySelector,excludeSelector);//跟选择的内容匹配不精确，document.text();
		String keywords = KeyWordsUtils.getKeywords(text);
		String intro="";
		if (StringUtils.isNotBlank(keywords)) {
			keywords=this.clearKeywords(keywords);
			intro=contentInfoService.getBestFragment(text,keywords.split(",")[0],request);
			intro=clearIntro(intro);
		}
		params.add(intro);
		//comm_thumbpic
		params.add(LzzConstants.THUMB_UPLOAD_PRE);
		//comm_modifydate
		params.add(publishDateString);
		//comm_keywords
		params.add(keywords);
		//comm_desc：也让描述等于摘要吧,最好不超过100个
		String comm_desc="";
		if (intro.length()>=100) {
			comm_desc=intro.substring(0,100);
		}else {
			comm_desc=intro;
		}
		comm_desc=clearDesc(comm_desc);
		params.add(comm_desc);
		//comm_src_url
		params.add(oneHref);
		return null;
	}
	private String clearDesc(String comm_desc) {
		comm_desc=filterStr("\"|'", comm_desc);//前端网页关键字、描述去掉"和'
		comm_desc=filterStr("[\\n|\\r\\n|\\r]", comm_desc);//去掉换行
		return comm_desc;
	}
	private String clearIntro(String intro) {
		intro=filterStr("<|>",intro);//有尖括号后台展示错位
		intro=filterStr("(?m)^\\s*$(\\n|\\r\\n)", intro);//去掉空行，针对简介和描述
		return intro;
	}
	private String clearKeywords(String keywords) {
		keywords=filterStr("<|>|\"|'",keywords);//有尖括号后台展示错位,前端网页关键字、描述去掉"和'
		return keywords;
	}
	private String getMainbodyText(Document document, String mainbodySelector, String excludeSelector) {
		//先删除掉指定元素
		if (StringUtils.isNotBlank(excludeSelector)) {
			String[] split = excludeSelector.split("@\\[zdw\\]@");
			int length = split.length;
			for (int i = 0; i < length; i++) {
				Elements elements = document.select(split[i]); 
				for (Element e:elements) {
					e.remove();
				}
			}
		}
		if (StringUtils.isNotBlank(mainbodySelector)) {
			//再获取内容的纯文本
			String[] split = mainbodySelector.split("@\\[zdw\\]@");
			int length = split.length;
			for (int i = 0; i < length; i++) {
				Elements elements = document.select(split[i]); 
				if (elements.size()==0&&(i+1)<length) {
					continue;
				}else if (elements.size()==0&&(i+1)==length) {
					return "";
				}else{
					Element element = elements.get(0);
					return element.text();
				}
			}
		}
		return "";
	}
	private String filterStr(String reg,String str) {
		str=str.replaceAll(reg, "");
		return str;
	}
	public static void main(String[] args) throws Exception{
		SpiderServiceImpl serviceImpl=new SpiderServiceImpl();
		System.out.println(serviceImpl.filterStr("\"|'","这里说的'是'它，你知道\"吗\"" ));
	}
	private String getNoRepeatTitle(String column_id, String title) {
		//校验本栏目下标题是否重复，如果重复就累加数字,这样避免html覆盖
		   boolean titleFlag =true;
		   while(titleFlag){
			   StringBuffer sb=new StringBuffer();
			   sb.append(" select count(*) count from lzz_commoncontent c where  ");
			   sb.append(" c.comm_title=? and c.column_id=? ");
			   long titleCount = spiderDao.queryForLong(sb.toString(), title,column_id);
			   if (titleCount!=0) {//标题已经存在
				   title+=LzzcmsUtils.random();
			   }else {
				 titleFlag=false;
			   }
		   }
		return title;
	}
	//基本字段就那几个字段嘛，不需要特殊处理，这个附加字段可能需要特殊处理，比如去除超链接，过滤某些内容
	private String makeExtraParamTask(List<Object> extraparams,String mainbodySelector, 
			Document document,HttpServletRequest request) {
		 //i=0时为comm_id，已经放入到extraparams里面了
		String[] split = mainbodySelector.split("@\\[zdw\\]@");
		int length = split.length;
		for (int i = 0; i < length; i++) {
			Elements elements = document.select(split[i]); 
			if (elements.size()==0&&(i+1)<length) {
				continue;
			}else if (elements.size()==0&&(i+1)==length) {
				return "通过内容选择器获取不到内容,跳过当前条目采集";
			}else{
				Element element = elements.get(0);
				saveNetImg2Local(request, element);
				//不能放在开头，如果放在开头，先把属性给去掉了，通过与该属性有关的选择器就获取不到内容了
				processHtmlHand(document);
				extraparams.add(element.outerHtml());
				break;
			}
		}
	    return null;
	}
	@Override
	public List<Map<String, Object>> getCrawls(Map<String, Object> paramMap) {
		StringBuffer sBuffer=new StringBuffer();
		sBuffer.append(" select a.*,b.name from lzz_crawl  a left join lzz_columninfo b ");
		sBuffer.append(" on a.column_id=b.id ");
		if (paramMap.get("order")!=null) {
			sBuffer.append(" order by "+paramMap.get("order")+" ");
		}
		sBuffer.append(" limit ?,? ");
		List<Map<String, Object>> queryForList = spiderDao.queryForList(sBuffer.toString()
				,paramMap.get("pageNow")
				,paramMap.get("pageSize"));
		return queryForList;
	}
	@Override
	public Long getCrawlsCount(Map<String, Object> paramMap) {
		StringBuffer sBuffer=new StringBuffer();
		sBuffer.append(" select count(*) from lzz_crawl ");
		long queryForLong = spiderDao.queryForLong(sBuffer.toString());
		return queryForLong;
	}
	@Override
	public void addCrawl(Map<String, Object> paramMap) {
		StringBuffer sBuffer=new StringBuffer();
		sBuffer.append(" insert into lzz_crawl (list_url,list_item_selector,column_id,gmt_created ");
		sBuffer.append(" ,gmt_modified,proxy_ip) values (?,?,?,?,?,?) ");
		spiderDao.executeSql(sBuffer.toString(),paramMap.get("list_url"),
				paramMap.get("list_item_selector"),paramMap.get("column_id"),
				paramMap.get("gmt_created"),paramMap.get("gmt_modified"),
				paramMap.get("proxy_ip"));
	}
	@Override
	public void addCrawlDetail(List<Map<String, Object>> paramList) {
		for (int i = 0; i < paramList.size(); i++) {
			Map<String, Object> paramMap=paramList.get(i);
			StringBuffer sBuffer=new StringBuffer();
			sBuffer.append(" insert into lzz_crawl_detail (crawl_id,field_name,field_selector ");
			sBuffer.append(" ,gmt_created,gmt_modified) values (?,?,?,?,?) ");
			spiderDao.executeSql(sBuffer.toString(),paramMap.get("crawl_id"),
					paramMap.get("field_name"),paramMap.get("field_selector"),
					paramMap.get("gmt_created"),paramMap.get("gmt_modified"));
		}
	}
	@Override
	public Map<String, Object> showCrawlDetailByCrawlId(Map<String, Object> paramMap) {
		StringBuffer sBuffer=new StringBuffer();
		sBuffer.append(" select * from lzz_crawl_detail where crawl_id=? ");
		List<Map<String, Object>> list = spiderDao.queryForList(sBuffer.toString(), 
				paramMap.get("crawl_id"));
		Map<String, Object> retMap=new HashMap<String, Object>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);
			if (map.get("field_name").toString().equals("comm_title")) {
				retMap.put("comm_title_selector", map.get("field_selector").toString());
			}
			if (map.get("field_name").toString().equals("mainbody")) {
				retMap.put("mainbody_selector", map.get("field_selector").toString());
			}
			if (map.get("field_name").toString().equals("will_exclude")) {
				retMap.put("will_exclude_selector", map.get("field_selector")==null?
						"":map.get("field_selector").toString());
			}
		}
		return retMap;
	}
	@Override
	public void deleteCrawlById(Map<String, Object> paramMap) {
		StringBuffer sBuffer=new StringBuffer();
		sBuffer.append(" delete from   lzz_crawl_detail where crawl_id=? ");
		spiderDao.executeSql(sBuffer.toString(),Integer.valueOf(paramMap.get("id").toString()));
		sBuffer.setLength(0);
		sBuffer.append(" delete from   lzz_crawl  where id=? ");
		spiderDao.executeSql(sBuffer.toString(),Integer.valueOf(paramMap.get("id").toString()));
	}
	@Override
	public void updateCrawlDetail(List<Map<String, Object>> paramList) {
		for (int i = 0; i < paramList.size(); i++) {
			Map<String, Object> paramMap=paramList.get(i);
			StringBuffer sBuffer=new StringBuffer();
			sBuffer.append(" update lzz_crawl_detail set field_selector=?,gmt_modified=? ");
			sBuffer.append(" where crawl_id=? and field_name=? ");
			spiderDao.executeSql(sBuffer.toString(),paramMap.get("field_selector")
					,paramMap.get("gmt_modified"),paramMap.get("crawl_id")
					,paramMap.get("field_name"));
		}
	}
	@Override
	public void onOffItem(Map<String, Object> paramMap) {
		StringBuffer sBuffer=new StringBuffer();
		sBuffer.append(" update  lzz_crawl set is_deleted=? where id=? ");
		spiderDao.executeSql(sBuffer.toString(),paramMap.get("is_deleted"),
				Integer.valueOf(paramMap.get("id").toString()));
	}
	@Override
	public Map<String, Object> showCrawlByCrawlId(Map<String, Object> paramMap) {
		StringBuffer sBuffer=new StringBuffer();
		sBuffer.append(" select * from lzz_crawl where id=? ");
		List<Map<String, Object>> list = spiderDao.queryForList(sBuffer.toString(), 
				paramMap.get("crawl_id"));
		return list.get(0);
	}
	@Override
	public void updateCrawl(Map<String, Object> paramMap) {
		StringBuffer sBuffer=new StringBuffer();
		sBuffer.append(" update lzz_crawl set list_url=?,gmt_modified=?,list_item_selector=?, ");
		sBuffer.append(" proxy_ip=?,column_id=? where id=?  ");
		spiderDao.executeSql(sBuffer.toString(),paramMap.get("list_url")
					,paramMap.get("gmt_modified"),paramMap.get("list_item_selector")
					,paramMap.get("proxy_ip"),paramMap.get("column_id"),paramMap.get("id"));
	}
}
