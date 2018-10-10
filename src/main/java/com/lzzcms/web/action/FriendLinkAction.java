package com.lzzcms.web.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.lzzcms.dto.GridDto;
import com.lzzcms.model.FriendLink;
import com.lzzcms.service.FriendLinkService;
import com.lzzcms.utils.LzzcmsUtils;

/**
 * @author zhao
 */
@Controller
public class FriendLinkAction {
	private Logger logger=Logger.getLogger(FriendLinkAction.class);
	@Resource
	private FriendLinkService friendLinkService;
	
	public FriendLinkService getFriendLinkService() {
		return friendLinkService;
	}
	public void setFriendLinkService(FriendLinkService friendLinkService) {
		this.friendLinkService = friendLinkService;
	}
	@RequestMapping("/flManage")
	public String flManage(){
		return "fl/flManage";
	}
	@RequestMapping("/listFls")
	@ResponseBody
	public GridDto<FriendLink> listFls(HttpServletRequest request){
		List<FriendLink> list=friendLinkService.trueList(request);
		String basePath = LzzcmsUtils.getBasePath(request);
		for (FriendLink fLink:list) {
			if (fLink.getType()==2) {
				StringBuffer img=new StringBuffer();
				img.append("<img src='"+basePath+fLink.getLinkDesc()+"'>");
				fLink.setLinkDesc(img.toString());
			}
		}
		long totalCount=friendLinkService.getTotalCount();
		GridDto<FriendLink> gridDto=new GridDto<FriendLink>();
		gridDto.setTotal(totalCount);
		gridDto.setRows(list);
		return gridDto;
	}
	@RequestMapping("/trueAddFl")
	@ResponseBody
	public Map<String, Object> trueAddFl(@RequestParam("fl_linknameimg") MultipartFile file,HttpServletRequest request){
		 Map<String, Object> map=new HashMap<String, Object>();
		 try {
			 map=friendLinkService.trueAdd(file,request);
		} catch (Exception e) {
			map.put("type", "error");
			map.put("info", e.getMessage());
		}
		return map;
	}
	@RequestMapping("/deleteFl")
	@ResponseBody
	public Map<String, Object> deleteFl(HttpServletRequest request){
		Map<String, Object> map=new HashMap<String, Object>();
		try {
			friendLinkService.delete(request);
			map.put("status", "sucess");
		} catch (Exception e) {
			map.put("status", "error");
			map.put("info", e.getMessage());
		}
		return map;
	}
	@RequestMapping("/trueUpdateFl")
	@ResponseBody
	public Map<String, Object> trueUpdateFl(HttpServletRequest request){
		Map<String, Object> map=new HashMap<String, Object>();
		try {
			friendLinkService.trueUpdate(request);
			map.put("info", "更新成功");
			map.put("type", "info");
		} catch (Exception e) {
			map.put("type", "error");
			map.put("info", e.getMessage());
		}
		return map;
	}
}
