package com.lzzcms.service.impl;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.lzzcms.listeners.SpringBeanFactory;
import com.lzzcms.service.SearchService;
import com.lzzcms.service.StaticService;
import com.lzzcms.utils.LzzConstants;
import com.lzzcms.utils.LzzcmsUtils;

@Service
public class SearchServiceImpl implements SearchService{
	private Logger logger=Logger.getLogger(SearchServiceImpl.class);
	private String searchBakJspStr=null;
	@Override
	public Map<String, Object> manualClear(HttpServletRequest request) throws Exception{
		Map<String, Object> retMap=new HashMap<>();
		    String dir = LzzcmsUtils.getRealPath(request,LzzConstants.SEARCH_INDEIES);
		    File dirFile= new File(dir);
		    if (!dirFile.exists()) {
				dirFile.mkdirs();
			}
		    Directory directory=null;
			IndexWriter iwriter=null;
			try {
				directory = FSDirectory.open(Paths.get(dir));
				SmartChineseAnalyzer analyzer=new SmartChineseAnalyzer();
				IndexWriterConfig conf=new IndexWriterConfig(analyzer);
				iwriter = new IndexWriter(directory, conf);
				iwriter.deleteAll();
				iwriter.forceMergeDeletes();
				String s = LzzcmsUtils.getRealPath(request,LzzConstants.HTML_ROOTPATH);
				File file= new File(s);// /s
				File[] dirs = file.listFiles();
				for(File colDirFile:dirs){
					wirteDocForOneColDir(request,colDirFile.getName(),iwriter);
				}
				retMap.put("info", "索引"+iwriter.numDocs()+"个文档");
				retMap.put("type", "info");
			} catch (Exception e) {
				logger.info(e);
				retMap.put("info", e.getMessage());
				retMap.put("type","error");
			}finally{
				if (iwriter!=null) {
					iwriter.close();
				}
				if(directory!=null) directory.close();
			}
		    return retMap;
	}

	@Override
	public String search(HttpServletRequest request,HttpServletResponse response, String queryString) throws Exception {
		    String dir = LzzcmsUtils.getRealPath(request,LzzConstants.SEARCH_INDEIES);
		    File dirFile= new File(dir);
		    if (!dirFile.exists()) {
				dirFile.mkdirs();
			}
		    Directory directory=null;
		    IndexReader ir=null;
		    List<Map<String, Object>> list=new ArrayList<Map<String, Object>>();
		    try {
			    directory = FSDirectory.open(Paths.get(dir));
			    ir=DirectoryReader.open(directory);
				IndexSearcher isearcher = new  IndexSearcher(ir);
				//得到query对象
				SmartChineseAnalyzer analyzer=new SmartChineseAnalyzer();
				QueryParser parser = new QueryParser("cont", analyzer);
				parser.setDefaultOperator(Operator.OR);//默认空格就是OR
				parser.setAllowLeadingWildcard(true);//设置通配符能在第一位
				Query query = parser.parse(queryString); 
				
				TopDocs tds = isearcher.search(query,10);
				ScoreDoc[] sds = tds.scoreDocs;
				int length = sds.length;
				if (length>0) {
					//高亮配置得分，格式化，最大字数
					QueryScorer queryScorer=new QueryScorer(query);
					//指定取出的最佳匹配的最大字数
					Fragmenter fragmenter=new SimpleSpanFragmenter(queryScorer, LzzConstants.BEST_FRAGMENT_LEN);
				    SimpleHTMLFormatter formatter=new SimpleHTMLFormatter("<b><font color='red'>", "</font></b>");
				    Highlighter highlighter=new Highlighter(formatter, queryScorer);
				    highlighter.setTextFragmenter(fragmenter);
					for (int i = 0; i < length; i++) {
					  Document hitDoc = isearcher.doc(sds[i].doc);
					  String docid = hitDoc.get("id");
					  String docAbsolutePath=LzzcmsUtils.getRealPath(request,docid);
					  org.jsoup.nodes.Document jsoupDocument = getJsoupDocument(new File(docAbsolutePath));
					  String filteredCont =getContFilteredHtml(jsoupDocument);
					  String bestFragment = highlighter.getBestFragment(analyzer, "cont", filteredCont);
					  String basePath = LzzcmsUtils.getBasePath(request);
					  String docurl = basePath+docid;
					  String title = hitDoc.get("title");
					  Map<String, Object> map=new HashMap<String, Object>();
					  map.put("intro", bestFragment+"...");
					  map.put("docurl", docurl);
					  map.put("title", title);
					  list.add(map);
					}
				}
				searchBakJspStr=getSearchBakJspStr(request);
				String searchJspPath =LzzcmsUtils.getRealPath(request, "/WEB-INF/lzzcms/jsps/search/search.jsp");
				FileOutputStream fos=null;
				BufferedOutputStream bos=null;
				try {
					fos=new FileOutputStream(searchJspPath);
					bos=new BufferedOutputStream(fos);
					bos.write(searchBakJspStr.getBytes("utf-8"));
					bos.flush();
				} catch (IOException e) {
					logger.error("写入到文件"+searchJspPath+"出错:",e);
					return e.getMessage();
				}finally{
					LzzcmsUtils.closeOs(bos);
					LzzcmsUtils.closeOs(fos);
				}
			} catch (Exception e) {
				logger.error("搜索出错:",e);
				return e.getMessage();
			}finally{
				if (ir!=null) {
						ir.close();
				}
				if(directory!=null) directory.close();
				request.setAttribute("searchList", list);
			}
		    return null;
	}
	//获取searchbak.jsp的内容
	private String getSearchBakJspStr(HttpServletRequest request) {
		if (searchBakJspStr==null) {
			String searchBakJspPath =LzzcmsUtils.getRealPath(request,"/WEB-INF/lzzcms/jsps/search/searchbak.jsp");
			File searchBakJspFile = new File(searchBakJspPath);
			try {
				searchBakJspStr=FileUtils.readFileToString(searchBakJspFile, "utf-8");
			} catch (IOException e) {
				logger.error("读取searchbak.jsp文件内容出错:"+e);
			}//得到searchbak.jsp的内容
			StaticService staticService = SpringBeanFactory.getBean("staticServiceImpl", StaticService.class);
			searchBakJspStr=staticService.includeFile(request, searchBakJspStr);//替换掉searchbak.jsp里面的include标签
		}
		return searchBakJspStr;
	}

	/**
	 * 
	 * @param request
	 * @param colDir：要写入索引的目录，比如 传入“cmsjiaocheng”
	 * @param iWriter
	 * @throws Exception
	 */
	private void wirteDocForOneColDir(HttpServletRequest request,String colDir,IndexWriter iWriter) throws Exception{
		String pathForColDir=LzzcmsUtils.getRealPath(request,LzzConstants.HTML_ROOTPATH+"/"+colDir);
		File fileForColDir=new File(pathForColDir);// pathForColDir比如：f.../s/cmsjiaocheng
		File[] listFiles = fileForColDir.listFiles();
		for(File fileOrDir:listFiles){
			if (fileOrDir.isDirectory()) {//比如：f.../s/cmsjiaocheng/get
				wirteDocForOneColDir(request,colDir+"/"+fileOrDir.getName(),iWriter);
			}else {//fileOrDir可能是index.html等。封面栏目的index/list_pg_x或者列表栏目的文章xxx.html
				if (fileOrDir.length()>0L) {
					Document document=new Document();
					//以  "/s/cmsjiaocheng/get/2016/09/xx.html"作为id
					String value = LzzConstants.HTML_ROOTPATH+"/"+colDir+"/"+fileOrDir.getName();
					document.add(new StringField("id",value, Field.Store.YES));
//					String basePath = (String) request.getSession().getAttribute("basePath");
//					document.add(new StringField("docurl",basePath + value, Field.Store.YES));//少存一个，降低索引文件的大小
					org.jsoup.nodes.Document jsoupDocument = getJsoupDocument(fileOrDir);
					document.add(new StringField("title", getHtmlFileTitle(jsoupDocument), Field.Store.YES));
					//若未存储hitDoc.get("id_url")肯定得不到，返回null,如果这里把getContFilteredHtml(originalHtmlCont)
					//换成originalHtmlCont那么就会多很多字母了之类的额外无用的跟搜索无关的索引了(如html标签)索引文件会大很多
//					System.out.println("文件名："+value);
					document.add(new TextField("cont", getContFilteredHtml(jsoupDocument), Field.Store.NO));
					iWriter.addDocument(document);
				}
			}
		}
	}
	/**
	 * 得到jsoup文档对象
	 */
	private  org.jsoup.nodes.Document  getJsoupDocument(File file) throws IOException  {
		return Jsoup.parse(file,"utf-8");
	}
	/**
	 * 得到给定html内容过滤掉所有html标签，script标签，style标签，以及替换这些标签后剩余的大面积留白部分也要替换掉.只剩下纯文本内容，供全文检索
	 */
	private String getContFilteredHtml(org.jsoup.nodes.Document jsoupDocument) {
		Elements elements = jsoupDocument.getElementsByTag("html");
		Element element = elements.get(0);
		return element.text();
	}
	/**
	 * 得到给定的html内容中的标题
	 */
	private String getHtmlFileTitle(org.jsoup.nodes.Document jsoupDocument) {
		Elements elements =  jsoupDocument.getElementsByTag("title");
		Element titleElement = elements.get(0);
		return titleElement.text();
	}
}
