package com.lzzcms.utils;

public class LzzConstants {
	private static LzzConstants lzzConstants;
	static{
		lzzConstants=new LzzConstants();
	}
	private LzzConstants(){}
	
	public static final String SEARCH_INDEIES = "/WEB-INF/indeies/search";//供搜索使用的索引目录
	public static final String KEYWORDSANDINTRO_INDEIES = "/WEB-INF/indeies/tmp";//生成关键字和摘要的索引目录
	public static final String HTML_ROOTPATH = "/s";
	public static final String HTML_ROOTPATH_STARTWITHS = "s/";
	public static String TMP="/tmp/";
	public static String TPLHEAD="tplHead.jsp";
	public static String TPLHEADPATH="/tmp/tplHead.jsp";
	public static String UPLOADS="/uploads/";//上传文件路径前缀
	public static String THUMB_UPLOAD_PRE="/uploads/thumb/";//缩略图文件路径前缀
	public static String SQLDELIMITER="]zdw;";//备份还原sql分隔符
	public static String OUTPUTINDEXJSP="output_index.jsp";//有tplHead.jsp和index模板内容组成，用于被请求得到html串
	public static String OUTPUTCONTJSP="output_cont_";//有tplHead.jsp和内容模板内容组成，
	//内容模板也不尽相同，只定义前缀用于被请求得到html串
	public static String OUTPUTCLNJSP="output_cln_";//有tplHead.jsp和栏目对应的模板内容组成，用于被请求得到html串,列表的时候有多个页面
	//不能只使用一个jsp，这里之定义前缀
	public static String SUFFIX=".jsp";
	public static String GLOBAL="global";
	private String backServletPath;//类似  /backstage
	private String dbName;//安装时填写的数据库名称
	private String taobaoIpUrl="http://ip.taobao.com/service/getIpInfo.php";
	private String seed="08049E323A8BEDB23C4CD5564B0EF679";
	public static int BEST_FRAGMENT_LEN=180;//最佳摘要的长度
	public static int KEYWORDS_NUM=8;//关键字的个数
	private String basePathForTask;//类似  http://localhost:8080/ 定时任务要用到
	
	
	public String getBasePathForTask() {
		return basePathForTask;
	}
	public void setBasePathForTask(String basePathForTask) {
		this.basePathForTask = basePathForTask;
	}
	public String getSeed() {
		return seed;
	}
	public void setSeed(String seed) {
		this.seed = seed;
	}
	public String getTaobaoIpUrl() {
		return taobaoIpUrl;
	}
	public void setTaobaoIpUrl(String taobaoIpUrl) {
		this.taobaoIpUrl = taobaoIpUrl;
	}
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public String getBackServletPath() {
		return backServletPath;
	}
	public void setBackServletPath(String backServletPath) {
		this.backServletPath = backServletPath;
	}
	public static LzzConstants getInstance(){
		return lzzConstants;
	}
}
