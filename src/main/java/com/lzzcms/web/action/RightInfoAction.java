package com.lzzcms.web.action;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lzzcms.dto.TreeDto;
import com.lzzcms.model.AdminInfo;
import com.lzzcms.service.RightInfoService;
import com.lzzcms.utils.LzzcmsUtils;

/**
 * @author zhao
 */
@Controller
public class RightInfoAction {
	@Resource
	private RightInfoService rightInfoService;
	
	public RightInfoService getRightInfoService() {
		return rightInfoService;
	}
	public void setRightInfoService(RightInfoService rightInfoService) {
		this.rightInfoService = rightInfoService;
	}
	//构造权限树的数据(由用户的角色决定)
	@RequestMapping("/loadRights")
	@ResponseBody
	public List<TreeDto> loadRights(HttpServletRequest request){
		AdminInfo adInfo = (AdminInfo) request.getSession().getAttribute("admin");
		List<TreeDto> treeDtos = rightInfoService.loadRights(adInfo.getId());
		return treeDtos;
	}
	//构造权限树的数据(所有的)，供角色分配权限使用
	@RequestMapping("/allRights") @ResponseBody
	public List<TreeDto> allRights(HttpServletRequest request){
		String basePath = LzzcmsUtils.getBasePath(request);
		List<TreeDto> allRights = rightInfoService.allRights(basePath);
		return allRights;
	}
}
