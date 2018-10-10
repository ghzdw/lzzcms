package com.lzzcms.utils;
 
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
public class AuthUtil {
	private static Logger logger=Logger.getLogger(AuthUtil.class);
	private static String access_token="";
	private static Calendar inactiveCalendar=Calendar.getInstance();
	private static String clientId="l7TTZtQljL1b08ZzQbZHbdLQ";// 官网获取的 API Key 更新为你注册的
	private static String clientSecret="LwefLPCckdfz94nwkMRbn3131CWlH5kC";// 官网获取的 Secret Key 更新为你注册的
	/**
     * 获取权限token
     */
    public static String getAuth() {
    	Calendar now=Calendar.getInstance();
    	if (now.before(inactiveCalendar)) {
			return access_token;
		}else {
			return getAuth(clientId, clientSecret);
		}
    }

    /**
     * 获取API访问token
     * 该token有一定的有效期，需要自行管理，当失效时需重新获取.
     * @param ak - 百度云官网获取的 API Key
     * @param sk - 百度云官网获取的 Securet Key
     * @return assess_token 
     */
    private static String getAuth(String ak, String sk) {
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + ak
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + sk;
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            // 获取所有响应头字段
            /*
             * null--->[HTTP/1.1 200 OK]
				P3p--->[CP=" OTI DSP COR IVA OUR IND COM "]
				Transfer-Encoding--->[chunked]
				Vary--->[Accept-Encoding]
				Date--->[Wed, 16 May 2018 08:26:45 GMT]
				Set-Cookie--->[BAIDUID=E9AFFCDB1BE06253921D5DAC7B9E0518:FG=1; expires=Thu, 31-Dec-37 23:55:55 GMT; max-age=2145916555; path=/; domain=.baidu.com; version=1]
				Content-Type--->[application/json]
				Connection--->[keep-alive]
				Server--->[Apache]
				Cache-Control--->[no-store]
             */
//            Map<String, List<String>> map = connection.getHeaderFields();
//            for (String key : map.keySet()) {
//                System.err.println(key + "--->" + map.get(key));
//            }
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            /**
             * result:{
             * "access_token":"24.d31e22cdb2374e3629ee2584a9f58b30.2592000.1529051205.282335-11249931"
             * ,"session_key":"9mzdX+mwEm0mFl\/cOinuADpsWaxPaOf82OcUBUelJriA6Yjxhrk60GF77pY+T2WQ8OamWjhRr6SVRimvIbhtOpYLO6mcqA=="
             * ,"scope":"public nlp_simnet nlp_wordemb nlp_comtag nlp_dnnlm_cn brain_ocr_general_basic brain_nlp_lexer brain_all_scope brain_nlp_comment_tag brain_nlp_dnnlm_cn brain_nlp_word_emb_vec brain_nlp_word_emb_sim brain_nlp_sentiment_classify brain_nlp_simnet brain_nlp_depparser brain_nlp_wordembedding brain_nlp_dnnlm_cn_legacy brain_nlp_simnet_legacy brain_nlp_comment_tag_legacy brain_nlp_lexer_custom brain_nlp_keyword brain_nlp_topic wise_adapt lebo_resource_base lightservice_public hetu_basic lightcms_map_poi kaidian_kaidian ApsMisTest_Test\u6743\u9650 vis-classify_flower lpq_\u5f00\u653e cop_helloScope ApsMis_fangdi_permission smartapp_snsapi_base"
             * ,"refresh_token":"25.117963a367c2bbcd523b15201620f23b.315360000.1841819205.282335-11249931"
             * ,"session_secret":"2d68c816436a760206a34b1eb91ea132"
             * ,"expires_in":2592000
             * }
             */
//            System.err.println("result:" + result);
            JSONObject jsonObject = JSONObject.fromObject(result);
            access_token = jsonObject.getString("access_token");
            Integer expires_in = jsonObject.getInt("expires_in");
            inactiveCalendar.add(Calendar.SECOND, expires_in);
            return access_token;
        } catch (Exception e) {
           logger.error("获取token失败！",e);
        }
        return null;
    }
	
}