package com.lzzcms.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.tools.zip.ZipFile;

import com.lzzcms.components.HttpClientPool;
import com.lzzcms.listeners.SpringBeanFactory;

import net.sf.json.JSONObject;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class LzzcmsUtils {
	private static Logger logger=Logger.getLogger(LzzcmsUtils.class);
	private LzzcmsUtils(){	}
	/**
	 * 关闭inputstream
	 * @param is
	 */
	public static void closeIs(InputStream is){
		if (is!=null) {
			try {
				is.close();
			} catch (IOException e) {
				logger.error("关闭InputStream出错", e);
			}
		}
	}
	/**
	 * 关闭outputstream
	 * @param os
	 */
	public static void closeOs(OutputStream os){
		if (os!=null) {
			try {
				os.close();
			} catch (IOException e) {
				logger.error("关闭OutputStream出错", e);
			}
		}
	}
	public static void closeReader(Reader reader){
		if (reader!=null) {
			try {
				reader.close();
			} catch (IOException e) {
				logger.error("关闭Reader出错", e);
			}
		}
	}
	public static void closeWriter(Writer writer){
		if (writer!=null) {
			try {
				writer.close();
			} catch (IOException e) {
				logger.error("关闭Writer出错", e);
			}
		}
	}
	public static void closeZipFile(ZipFile zipFile){
		if (zipFile!=null) {
			try {
				zipFile.close();
			} catch (IOException e) {
				logger.error("关闭ZipFile出错:",e);
			}
		}
	}
	/**
	 * 静态化时生成临时的jsp文件名
	 * @return
	 */
	public static String random(){
		 Random random=new Random();
		 int nextInt = random.nextInt();
		 return Integer.toHexString(nextInt);
	}
	
	/**
	 * 将汉字转换为全拼
	 * @param src：要得到全拼的字符串
	 * @return String：返回的全拼
	 */
	public static String getQuanPin(String src) {
		char[] srcCharArr = src.toCharArray();
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);//不要声调
		format.setVCharType(HanyuPinyinVCharType.WITH_V);//设置考虑的显示方式
		String strToReturn = "";
		try {
			for (int i = 0; i < srcCharArr.length; i++) {
				if (String.valueOf(srcCharArr[i]).matches("[\\u4E00-\\u9FA5]+")) {//汉字
					//返回汉字的汉语拼音，有几个音，返回的数组长度就是几如中返回数组长度2：zhong1 zhong4
					//赵返回数组长度1：zhao4
					String[] arrForPerChar = PinyinHelper.toHanyuPinyinStringArray(srcCharArr[i], format);
					strToReturn += arrForPerChar[0];
				} else {//非汉字
					if (!String.valueOf(srcCharArr[i]).matches("\\w")) {
						continue;
					}
					strToReturn += Character.toString(srcCharArr[i]);
				}
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			logger.error("得到全拼出错:",e);
		}
		return strToReturn.toLowerCase();
	}

	/**
	 * 提取每个汉字的首字母
	 * 
	 * @param str
	 * @return String
	 */
	public static String getPinYinHeadChar(String str) {
		String convert = "";
		for (int j = 0; j < str.length(); j++) {
			char word = str.charAt(j);
			String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
			if (pinyinArray != null) {//汉字返回数组，非汉字返回null
				convert += pinyinArray[0].charAt(0);
			} else {
				if (!String.valueOf(word).matches("\\w")) {//[0-9][a-z][A-Z]_
					continue;
				}
				convert += word;
			}
		}
		return convert.toLowerCase();
	}
	/**
	 * @param pattern:yyyy-MM-dd_HH-mm-ss
	 * @param date
	 * @return
	 */
	public static String getPatternDateString(String pattern,Date date){
		if (pattern==null) pattern="yyyy-MM-dd_HH-mm-ss";
		SimpleDateFormat sdf=new SimpleDateFormat(pattern);
		return sdf.format(date);
	}
	public static Date getPatternDate(String pattern,String date){
		if (pattern==null) pattern="yyyy-MM-dd_HH-mm-ss";
		SimpleDateFormat sdf=new SimpleDateFormat(pattern);
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			logger.error("日期格式化出错:",e);
		}
		return null;
	}
	public static void closeIwriter(IndexWriter iwriter){
		if (iwriter!=null) {
			try {
				iwriter.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}
	public static void closeDirectory(Directory directory){
		if(directory!=null)
			try {
				directory.close();
			} catch (IOException e) {
				logger.error(e);
			}
	}
	public static void closeIr(IndexReader ir){
		if(ir!=null)
			try {
				ir.close();
			} catch (IOException e) {
				logger.error(e);
			}
	}
	public static void deleteFiles(File...files ){
		for(File file:files){
			if (file.exists()) {
				file.delete();
			}
		}
	}
	public static String getRealIp(HttpServletRequest request){
		String ip = request.getHeader("x-forwarded-for");
		if (StringUtils.isBlank(ip)
				|| "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (StringUtils.isBlank(ip)
				|| "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (StringUtils.isBlank(ip)
				|| "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
			if (ip.equals("127.0.0.1")) {
				InetAddress inet = null;// 根据网卡取本机配置的IP
				try {
					inet = InetAddress.getLocalHost();//idea-PC/192.168.212.144
				} catch (UnknownHostException e) {
					logger.error("获取ip出错", e);
				}
				ip = inet.getHostAddress();//192.168.212.144
			}
		}
		// 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割,多级代理的时候会得到多个以,分割的ip，这时候第一个是
		//真实的客户端ip
		if (ip != null && ip.length() > 15) { // "***.***.***.***".length()
			if (ip.indexOf(",") > 0) {
				ip = ip.substring(0, ip.indexOf(","));
			}
		}
		return ip;
	}
	/*
	 * {"code":0,"data":{"ip":"58.246.145.226","country":"中国","area":"","region":"上海",
	 * "city":"上海","county":"XX","isp":"联通","country_id":"CN","area_id":"",
	 * "region_id":"310000","city_id":"310100","county_id":"xx","isp_id":"100026"}}
	 */
	public static String getAreaInfoByIp(String pub_ip) {
		String city="";
		HttpClientPool pool = SpringBeanFactory.getBean("httpClientPool", HttpClientPool.class);
		String url = LzzConstants.getInstance().getTaobaoIpUrl()+"?ip="+pub_ip;
		String retStr = pool.executeGet(url, null);
		JSONObject jsonObject = JSONObject.fromObject(retStr);
		if (jsonObject!=null&&jsonObject.size()!=0) {
			JSONObject data = jsonObject.getJSONObject("data");
			if (data!=null&&data.size()!=0) {
				city = data.getString("city");
			}
		}
		return city;
	}
	/** 
     * 区分中英文截取一段字符的长度 
     *  中文：2
     *  英文：1
     *  eg getSubStringSensitive("乐之者cms使用教程[共2篇]", 7):乐之者c
     * @param origin 
     *          原始字符串 
     * @param len 
     *          截取长度(一个汉字长度按1算的) 
	 * @param append 
     * @return String, 返回的字符串 
     */  
    public static String getSubStringSensitive(String origin, int len
    		, String append) {  
        try {  
        	boolean doAppend=false;
            if (StringUtils.isBlank(origin) || (len < 1)) {  
                return "";  
            }  
            if (getStrLenSensitive(origin)<=len) {
				return origin;
			}else {
				doAppend=true;
			}
            StringBuffer buffer = new StringBuffer();  
            char[] array = origin.toCharArray();  
            double currentLength = 0;    
            for(char c : array){  
                // 字符长度  
                int charlen = String.valueOf(c).getBytes("utf-8").length;  
                // 汉字按2个长度累加，字母数字按1个长度累加  
                if(charlen == 3){  
                    currentLength += 2;  
                }else {  
                    currentLength += 1;  
                }  
                if(currentLength <= len){  
                    buffer.append(c);  
                } else {  
                    break;  
                }  
            }  
            if (doAppend) {
            	if (StringUtils.isNotBlank(append)) {
            		buffer.append(append);
				}
			}
            return buffer.toString();  
        } catch (Exception e) {  
           logger.error("截取字符串区分中英文出错:", e);
        }  
        return null;  
    }  
    /**
     * 
     * @param str :被计算的字符串
     * @return  区分中英文的字符串长度
     * 好abc的:7
     */
    public static int getStrLenSensitive(String str){
    	if (StringUtils.isBlank(str) ) {  
            return 0;  
        }  
    	 char[] array = str.toCharArray();  
    	 int totalLen=0;
    	 try {
    		 for(char c : array){  
                 int charlen = String.valueOf(c).getBytes("utf-8").length;  
                 // 汉字按2个长度累加，字母数字按1个长度累加  
                 if(charlen == 3){  
                	 totalLen += 2;  
                 }else {  
                	 totalLen += 1;  
                 }  
             }  
		} catch (Exception e) {
			logger.error("得到区分中英文的字符串长度出错：", e);
		}
    	 return totalLen;
    }
    private static Cipher getCipher(int type,String seed){
        try {
            KeyGenerator  kgen = KeyGenerator.getInstance("AES");
              SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" );
              secureRandom.setSeed(seed.getBytes());
              kgen.init(128, secureRandom);
            SecretKey  secretKey = kgen.generateKey(); 
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec  specKey = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(type, specKey);
            return cipher;
        } catch (Exception e) {
        	logger.error("得到Cipher出错：", e);
        } 
         return null;
    }
    /**
     * 加密
     * @param content :要加密的内容
     * @param seed :种子
     * @return  :加密后的字节数组
     */
    private static byte[] encrypt(String content, String seed) {
        try {
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE,seed);
            byte[] contentBytes = content.getBytes("utf-8");
            byte[] result = cipher.doFinal(contentBytes);
            return result;
        } catch (Exception e) {
        	logger.error("encrypt出错：", e);
        } 
        return null;
    }
    /**将字节数组转换成16进制字符组成的字符串 
     * @param buf ：
     * @return 
     */  
    private static String parseByte2HexStr(byte buf[]) {  
        StringBuffer sb = new StringBuffer();  
        int length = buf.length;
        for (int i = 0; i < length; i++) {  
            String hex = Integer.toHexString(buf[i]&0xFF);  
            if (hex.length() == 1) {  
                hex = '0' + hex;  
            }  
            sb.append(hex.toUpperCase());  
        }  
        return sb.toString();  
    } 
   
    public static String getEncryptResult(String content){
    	if (StringUtils.isBlank(content)) {
			return "";
		}
    	byte[] bytes = encrypt(content, LzzConstants.getInstance().getSeed());
    	String result = parseByte2HexStr(bytes);
    	return result;
    }
    /**
     * 把16进制字符组成的字符串转为字节数组并返回
     * 这里传入的十六进制一定是偶数的长度，因为parseByte2HexStr处理了
     * @param hexStr
     * @return
     */
    private static byte[] parseHexStr2Byte(String hexStr) {  
        if (hexStr.length() < 1)  return null;  
        byte[] result = new byte[hexStr.length()/2];  
        for (int i = 0;i< hexStr.length()/2; i++) {  
                //两个十六进制字符构成一个byte.high表示高4位，low表示低四位。Integer.parseInt(s,radix),
            //作用：把s转为10进制，radix指定s是什么进制
                int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);  
                int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);  
                result[i] = (byte) (high * 16 + low);  
        }  
        return result;  
    }
    /**
     *  解密AES加密过的字节数组
     * @param contentBytes :AES加密过的字节数组
     * @param seed  :加密时传入的种子
     * @return  :解密后的字节数组
     */
    private static byte[] decrypt(byte[] contentBytes, String seed) {
        try {
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE,seed);
            byte[] result = cipher.doFinal(contentBytes);  
            return result;  
        } catch (Exception e) {
            logger.error("解密AES加密过的字节数组出错：",e);
        }
        return null;
    }
    public static String getDecryptResult(String content){
    	if (StringUtils.isBlank(content)) {
    		return "";
    	}
    	byte[] bytes = parseHexStr2Byte(content);
    	byte[] decrypt = decrypt(bytes,LzzConstants.getInstance().getSeed());
    	 String ret =null;
    	 try {
			ret=new String(decrypt,"utf-8");
		} catch (Exception e) {
			logger.error("解密出错了:",e);
		}
    	return ret;
    }
    public static String getBasePath(HttpServletRequest request){
    	Object attribute = request.getSession().getAttribute("basePath");
    	if (attribute!=null) {
			return attribute.toString();
		}
    	return null;
    }
    public static String getRealPath(HttpServletRequest request,String relativePath){
    	return request.getSession().getServletContext().getRealPath(relativePath);
    }
}
