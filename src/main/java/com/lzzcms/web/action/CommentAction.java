package com.lzzcms.web.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lzzcms.dto.GridDto;
import com.lzzcms.service.CommentService;

@Controller
public class CommentAction {
	private Logger logger=Logger.getLogger(CommentAction.class);
	@Resource
	private CommentService commentService;
	@RequestMapping("/addComment") @ResponseBody
	public  Map<String, Object> addComment(HttpServletRequest request,HttpServletResponse response){
		 Map<String, Object> retMap=new HashMap<String, Object>();
		 try {
			 String ret = commentService.addComment(request,response);
			 if (ret!=null) {
				retMap.put("status", "warning");
				retMap.put("info", ret);
			}
		} catch (Exception e) {
			retMap.put("status", "error");
			retMap.put("info", e.getMessage());
			logger.error("首发评论出错了:", e);
		}
		return retMap;
	}
	@RequestMapping("/listComments")
	@ResponseBody
	public GridDto<Map<String, Object>> listComments(HttpServletRequest request){
		Map<String, String> paramMap=new HashMap<String,String>();
		paramMap.put("sortField", request.getParameter("sort"));
		paramMap.put("sortDirection", request.getParameter("order"));
		
		String parameter = request.getParameter("param");
		if (StringUtils.isNotBlank(parameter)) {
			JSONObject jsonObject = JSONObject.fromObject(parameter);
			paramMap= (Map<String, String>) JSONObject.toBean(jsonObject, Map.class);
			paramMap.put("sortField", request.getParameter("sort"));
			paramMap.put("sortDirection", request.getParameter("order"));
			paramMap.put("comm_title", paramMap.get("cm_doc_title"));
			paramMap.put("comment_cont", paramMap.get("cm_comment_cont"));
			paramMap.put("pub_name", paramMap.get("cm_pub_name"));
			paramMap.put("pub_ip", paramMap.get("cm_pub_ip"));
			paramMap.put("pub_location", paramMap.get("cm_pub_location"));
			paramMap.put("create_time", paramMap.get("cm_create_time"));
		}
		
		List<Map<String, Object>> list=commentService.listComments(paramMap);
		long totalCount=commentService.listCommentsCount(paramMap);
		GridDto<Map<String, Object>> gridDto=new GridDto<Map<String, Object>>();
		gridDto.setTotal(totalCount);
		gridDto.setRows(list);
		return gridDto;
	}
	@RequestMapping(value="/deleteComment",method=RequestMethod.POST) @ResponseBody
	public  Map<String, Object> deleteComment(HttpServletRequest request){
		Map<String, Object> map=new HashMap<String, Object>();
		try {
			commentService.deleteComment(request);
			map.put("status", "sucess");
		} catch (Exception e) {
			logger.error("删除评论出错：",e);
			map.put("status", "error");
			map.put("info", e.getMessage());
		}
		return map;
	}
	@RequestMapping("/replyComment") @ResponseBody
	public  Map<String, Object> replyComment(HttpServletRequest request,HttpServletResponse response){
		 Map<String, Object> retMap=new HashMap<String, Object>();
		 try {
			 String ret = commentService.replyComment(request,response);
			 if (ret!=null) {
				retMap.put("status", "warning");
				retMap.put("info", ret);
			}
		} catch (Exception e) {
			retMap.put("status", "error");
			retMap.put("info", e.getMessage());
			logger.error("回复评论出错了:", e);
		}
		return retMap;
	}
	@RequestMapping("/toCommentManage")
	public String toCommentManage(){
		return "comment/commentManage";
	}
}
