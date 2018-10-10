package com.lzzcms.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.lzzcms.dao.CommentDao;
import com.lzzcms.model.AdminInfo;
import com.lzzcms.service.CommentService;
import com.lzzcms.service.StaticService;
import com.lzzcms.utils.LzzcmsUtils;
import com.lzzcms.utils.PageContext;

@Service
public class CommentServiceImpl implements CommentService{
		private static Logger logger=Logger.getLogger(CommentServiceImpl.class);
		private static Map<String, String> emojiMap=new HashMap<String, String>();
		static{
			URL resource = CommentServiceImpl.class.getClassLoader().getResource("emoji.json");
        	try {
				String emojis = FileUtils.readFileToString(new File(resource.toURI()), "utf-8");
				JSONArray arr = JSONArray.fromObject(emojis);
				for (int i = 0; i < arr.size(); i++) {
					JSONObject oneEmoji = arr.getJSONObject(i);
					emojiMap.put(oneEmoji.getString("key"), oneEmoji.getString("value"));
				}
			} catch (Exception e) {
				logger.error("读取emoji.json出错", e);
			} 
        	
		}
		@Resource
		private CommentDao commentDao;
		@Resource
		private StaticService staticService;
		@Override
		public List<Map<String, Object>> listBackUps() {
			return null;
		}
		
		
		@Override
		public String backUp(ServletContext sc,String userName) {
			
			return null;
		}
	
	@Override
	public String backTo(ServletContext servletContext, String backupName) {
		
		return null;
	}
	@Override
	public String addComment(HttpServletRequest request,HttpServletResponse response) {
		AdminInfo adminInfo = (AdminInfo) request.getSession().getAttribute("admin");
		if (adminInfo==null) {
			String ret=validateFrequency(request);
			if (ret!=null) {
				return ret;
			}
		}
		StringBuffer sb=new StringBuffer();
		sb.append(" insert into lzz_comment(comm_id,comment_cont,pub_name,pub_ip ");
		sb.append(" ,pub_location,create_time,reply_id) ");
		sb.append(" values (?,?,?,?,?,?,?) ");
		String comm_id=request.getParameter("comm_id");
		String comment_cont=request.getParameter("comment_cont");
		comment_cont=convertLabel(comment_cont);
		comment_cont=convertEmoji(comment_cont);
		String pub_ip="";
		String pub_name="";
		String pub_location="";
		if (adminInfo!=null) {//管理员在前端回复
			pub_ip="";
			 pub_name="乐之者java";
			 pub_location="你不知道的地方";
		}else{
			 pub_ip=LzzcmsUtils.getRealIp(request);
			 pub_name="";
			if (StringUtils.isNotBlank(pub_ip)) {
				pub_name=pub_ip.substring(0, 3)+"***";
			}else {
				pub_name="神秘的访客";
			}
			 pub_location=LzzcmsUtils.getAreaInfoByIp(pub_ip);
			if (StringUtils.isBlank(pub_location)) {
				pub_location="神秘的远方";
			}
		}
		String reply_id=request.getParameter("reply_id");
		if (StringUtils.isBlank(reply_id)) {
			reply_id=null;
		}
		commentDao.executeSql(sb.toString(), comm_id,comment_cont,pub_name,pub_ip
				,pub_location,new Date(),reply_id);
		//生成一下本文章的html文件
		 staticService.makeContForComment(comm_id, request, response);
		 //写入cookie
		 if (adminInfo==null) {
			 createCookie(response);
		}
		 return null;
	}

	private String convertEmoji(String comment_cont) {
		//[/可爱] emoji表情替换
		Pattern pattern = Pattern.compile("\\[/.{2,3}\\]",
                Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
         Matcher matcher = pattern.matcher(comment_cont);
         while (matcher.find()) {
              String inputEmojiExpress = matcher.group();
              if (StringUtils.isNotBlank(emojiMap.get(inputEmojiExpress))) {
            	  comment_cont=comment_cont.replace(inputEmojiExpress, emojiMap.get(inputEmojiExpress));
			  }
         }  
		return comment_cont;
	}
	//ip+cookie
	private String validateFrequency(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		String lastTime="";
		if (cookies!=null) {
			for(Cookie cookie:cookies){
				if ("lzzcms_last_comment_time".equals(cookie.getName())) {
					lastTime=cookie.getValue();
					break;
				}
			}
		}
		if (StringUtils.isNotBlank(lastTime)) {
			Calendar calendar1=Calendar.getInstance();
			calendar1.setTime(LzzcmsUtils.getPatternDate("yyyy-MM-dd_HH:mm:ss", lastTime));
			calendar1.add(Calendar.MINUTE, 1);
			Calendar now=Calendar.getInstance();
			if (calendar1.after(now)) {//一分钟内一个人只能评论一次
				String res="评论太频繁了,请稍后再试试";
				return res;
			}
		}
		return null;
	}
	//xss后端端过滤
	private String convertLabel(String comment_cont) {
		//script style html等标签替换
		Pattern pattern = Pattern.compile("(<)\\s*[^>]+(>)[\\s\\S]*(<)\\s*[^>]+(>)",
                Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
         Matcher matcher = pattern.matcher(comment_cont);
         while (matcher.find()) {
             comment_cont=comment_cont.replace(matcher.group(1), "&lt;");
             comment_cont=comment_cont.replace(matcher.group(2), "&gt;");
         }  
		return comment_cont;
	}
	private void createCookie(HttpServletResponse response) {
		Cookie cookie1 = new Cookie("lzzcms_last_comment_time", 
				LzzcmsUtils.getPatternDateString("yyyy-MM-dd_HH:mm:ss", new Date()));  
        cookie1.setMaxAge(7*24*60*60);   //秒   7天               
        cookie1.setPath("/");
        response.addCookie(cookie1);  
	}


	@Override
	public List<Map<String, Object>> listComments(Map<String, String> paramMap) {
		StringBuffer sb=new StringBuffer();
		sb.append("  SELECT comm.comm_id,comm.id,cont.comm_title,comm.comment_cont,comm.pub_name,comm.pub_ip,comm.pub_location ");
		sb.append("  ,comm.ding_cnt,comm.cai_cnt,DATE_FORMAT(comm.create_time,'%Y-%m-%d %H:%i:%s') create_time  ");
		sb.append("  ,cont.comm_htmlpath  ");
		sb.append("  from lzz_comment comm  LEFT JOIN lzz_commoncontent cont on comm.comm_id=cont.COMM_ID  ");
		List<String> list=new ArrayList<String>();
		appendCond(sb,paramMap,list);
		if (StringUtils.isNotBlank(paramMap.get("sortField"))
				&&StringUtils.isNotBlank(paramMap.get("sortDirection"))) {
			sb.append(" order by "+paramMap.get("sortField")+" "+paramMap.get("sortDirection"));
		}else {
			sb.append(" order by comm.create_time desc ");
		}
		if (PageContext.getPageUtil().isNeedPage()) {
			int pageNow=PageContext.getPageUtil().getPageNow();
			int pageSize=PageContext.getPageUtil().getPageSize();
			int start=(pageNow-1)*pageSize;
			sb.append(" limit "+start+","+pageSize);
		}
		return commentDao.queryForList(sb.toString(),list.toArray());
	}


	private void appendCond(StringBuffer sb, Map<String, String> paramMap, List<String> list) {
		sb.append(" where comm.is_deleted='N' ");
		if (StringUtils.isNotBlank(paramMap.get("comm_title"))) {
			sb.append(" and cont.comm_title like ? ");
			list.add("%"+paramMap.get("comm_title")+"%");
		}
		if (StringUtils.isNotBlank(paramMap.get("comment_cont"))) {
			sb.append(" and comm.comment_cont  like ? ");
			list.add("%"+paramMap.get("comment_cont")+"%");
		}
		if (StringUtils.isNotBlank(paramMap.get("pub_name"))) {
			sb.append(" and comm.pub_name  like ? ");
			list.add("%"+paramMap.get("pub_name")+"%");
		}
		if (StringUtils.isNotBlank(paramMap.get("pub_ip"))) {
			sb.append(" and comm.pub_ip  like ? ");
			list.add("%"+paramMap.get("pub_ip")+"%");
		}
		if (StringUtils.isNotBlank(paramMap.get("pub_location"))) {
			sb.append(" and comm.pub_location  like ? ");
			list.add("%"+paramMap.get("pub_location")+"%");
		}
		if (StringUtils.isNotBlank(paramMap.get("create_time"))) {
			sb.append(" and DATE_FORMAT(comm.create_time,'%Y-%m-%d')=? ");
			list.add(paramMap.get("create_time"));
		}
	}


	@Override
	public long listCommentsCount(Map<String, String> paramMap) {
		StringBuffer sb=new StringBuffer();
		List<String> list=new ArrayList<String>();
		sb.append(" SELECT count(*) count  from lzz_comment comm ");
		sb.append(" LEFT JOIN lzz_commoncontent cont on comm.comm_id=cont.COMM_ID ");
		appendCond(sb, paramMap,list);
		long count = commentDao.queryForLong(sb.toString(),list.toArray());
		return count;
	}


	@Override
	public void deleteComment(HttpServletRequest request) {
		 String ids = request.getParameter("param");
		StringBuffer sbBuffer=new StringBuffer();
		sbBuffer.append(" delete from lzz_comment where id in("+ids+")");
		commentDao.executeSql(sbBuffer.toString());
	}
	
	@Override
	public String replyComment(HttpServletRequest request,HttpServletResponse response) {
		StringBuffer sb=new StringBuffer();
		sb.append(" insert into lzz_comment(comm_id,comment_cont,pub_name,pub_ip ");
		sb.append(" ,pub_location,create_time,reply_id) ");
		sb.append(" values (?,?,?,?,?,?,?) ");
		String comm_id=request.getParameter("comm_id");
		String comment_cont=request.getParameter("comment_cont");
		comment_cont=convertLabel(comment_cont);
		String pub_ip="";
		String pub_name="乐之者java";
		String pub_location="你不知道的地方";
		String reply_id=request.getParameter("reply_id");
		commentDao.executeSql(sb.toString(), comm_id,comment_cont,pub_name,pub_ip
				,pub_location,new Date(),reply_id);
		//生成一下本文章的html文件
		 staticService.makeContForComment(comm_id, request, response);
		 return null;
	}
}
