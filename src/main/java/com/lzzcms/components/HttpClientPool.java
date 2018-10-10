package com.lzzcms.components;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.lzzcms.utils.ProxyIpCxt;

import net.sf.json.JSONObject;

@Component
public class HttpClientPool {
   private static Logger logger=Logger.getLogger(HttpClientPool.class);
   private HttpClientBuilder builder=null;
   public HttpClientPool(){
	   builder=HttpClientBuilder.create();
		/*一、为HttpClientBuilder设置绕过不安全的https证书
		 */
		 Registry<ConnectionSocketFactory> registry 
	       = RegistryBuilder.<ConnectionSocketFactory>create()  
	         .register("http", PlainConnectionSocketFactory.INSTANCE)  
	         .register("https", trustHttpsCertificates())  
	         .build();
		/*二、为HttpClientBuilder设置PoolingHttpClientConnectionManager
		 */
		PoolingHttpClientConnectionManager cm=new PoolingHttpClientConnectionManager(registry);
		cm.setMaxTotal(200);
		cm.setDefaultMaxPerRoute(50);
		builder.setConnectionManager(cm);
		/*三、为HttpClientBuilder设置从连接池获取连接的超时时间、连接超时时间、获取数据响应超时时间
		 */
		RequestConfig requestConfig=RequestConfig.custom().
				setConnectionRequestTimeout(5000).
				setConnectTimeout(5000).
	            setSocketTimeout(5000).build();
		builder.setDefaultRequestConfig(requestConfig);
		/*
		 * 四、设置默认的header
		 */
		List<BasicHeader> basicHeaders=new ArrayList<>();
		BasicHeader basicHeader=new BasicHeader("User-Agent",
		"Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");
		basicHeaders.add(basicHeader);
		builder.setDefaultHeaders(basicHeaders);
   }
	public  void closeRes(CloseableHttpResponse response) {
		if (response!=null) {
			try {
				response.close();
			} catch (IOException e) {
				logger.error("关闭CloseableHttpResponse出错：",e);
			}
		}
	}
   public CloseableHttpResponse getCloseableHttpResponse(String url){
	   CloseableHttpClient client = builder.build();
		HttpGet httpGet=new HttpGet(url);
		 //破解防盗链设置
		 httpGet.addHeader("Referer",url);
		 CloseableHttpResponse response =null;
		 try {
			 response=client.execute(httpGet);
			 if (StringUtils.isNotBlank(ProxyIpCxt.getIp())) {
				 int tryTimes=0;
				 tryIt(httpGet, tryTimes, client, response);
			 }
		} catch (Exception e) {
			logger.error("得到CloseableHttpResponse出错",e);
		}
		 return response;
   }
   
   public String executeGet(String url,String charset){
	   if (StringUtils.isBlank(charset)) {
		  charset="utf-8";
	   }
	   String retStr =null;
	   CloseableHttpClient client = builder.build();
	   HttpGet httpGet=new HttpGet(url);
	   //破解防盗链设置
	   httpGet.addHeader("Referer",url);
	   CloseableHttpResponse response =null;
	   try {
		   response=client.execute(httpGet);
		   if (StringUtils.isNotBlank(ProxyIpCxt.getIp())) {
			 int tryTimes=0;
			 tryIt(httpGet, tryTimes, client, response);
		   }
		   if (response!=null) {
				 int statusCode = response.getStatusLine().getStatusCode();
				 if (statusCode==200) {
					 HttpEntity entity = response.getEntity();//这里charset要和html本身的charset一致，不然会乱码。所以才会再得到一次html的编码
					 retStr = EntityUtils.toString(entity,charset);
					 EntityUtils.consume(entity);
				}else{
					logger.info("response状态码:"+statusCode);
				}
			}else {
				logger.info("response为空");
			}
		} catch (Exception e) {
			retStr=null;
			logger.error("executeGet出错",e);
		}finally{
			closeRes(response);
		}
	   return retStr;
   }
   private  void tryIt(HttpGet httpGet, int tryTimes,CloseableHttpClient client
			, CloseableHttpResponse response) {
		String ip = ProxyIpCxt.getIp();
		int len =0;
		if (StringUtils.isNotBlank(ip)) {
			//多个代理之间使用;分割，一个代理的ip和端口之间使用:分割
			 String[] ips = ip.split(";",-1);
			 len = ips.length;
			 String useProxy=ips[tryTimes];
			 String[] ipAndPort = useProxy.split(":");
			 HttpHost proxy=new HttpHost(ipAndPort[0],Integer.valueOf(ipAndPort[1]));
			 logger.info("当前尝试次数,第"+(tryTimes+1)+"次，ip:"+ipAndPort[0]+",port:"+ipAndPort[1]);
			 RequestConfig config=RequestConfig.custom().setProxy(proxy).build();
			//设置代理ip
			 httpGet.setConfig(config);
		 }
		 try {
		   response = client.execute(httpGet);
		} catch (Exception e){
			logger.error("得到CloseableHttpResponse出错",e);
			if (StringUtils.isNotBlank(ip)) {
				logger.info("尝试使用其他代理ip");
				if (tryTimes+1<len) {
					tryTimes++;
					tryIt(httpGet, tryTimes, client,response);
				}
			}
		}
	}
   public String executePostApplicationJson(String url,List<Header> headers,
		   JSONObject parmMap,String charset){
	   String retStr =null;
	   CloseableHttpClient client = builder.build();
	   HttpPost httpPost =new HttpPost(url);
	   if (headers!=null) {
		 for(Header h:headers){
			 httpPost.addHeader(h);
		 }
	   }
		CloseableHttpResponse response = null;
		try {
			StringEntity s = new StringEntity(parmMap.toString(), charset);
			s.setContentEncoding(charset);
			s.setContentType("application/json;charset="+charset);
			httpPost.setEntity(s);
			// 执行
			response = client.execute(httpPost); // 处理响应部分
			HttpEntity entity = response.getEntity();
			retStr = EntityUtils.toString(entity, charset);
			EntityUtils.consume(entity);
		} catch (Exception e) {
			logger.error("executePostApplicationJson处理失败", e);
		} finally {
			closeRes(response);
		}
		return retStr;
   }
   
 //创建并返回SSLConnectionSocketFactory对象
 private  ConnectionSocketFactory trustHttpsCertificates() {  
     SSLConnectionSocketFactory socketFactory = null;  
     SSLContext sc = null;  
     try {  
         sc = SSLContext.getInstance("TLS");  
         sc.init(null, new TrustManager[]{new LzzcmsTrustManager()}, null);  
         socketFactory = new SSLConnectionSocketFactory(sc, NoopHostnameVerifier.INSTANCE);  
     } catch (Exception e) {  
         logger.error("获取ssl连接工厂出错",e);
     }  
     return socketFactory;  
 }  
}