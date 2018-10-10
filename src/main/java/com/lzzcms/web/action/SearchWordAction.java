package com.lzzcms.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lzzcms.service.SearchWordService;

/**
 * 搜索的内容
 * @author zhao
 */
@Controller
public class SearchWordAction {
	@Resource
	private SearchWordService searchWordService;
	
	public SearchWordService getSearchWordService() {
		return searchWordService;
	}

	public void setSearchWordService(SearchWordService searchWordService) {
		this.searchWordService = searchWordService;
	}

	@RequestMapping("/toSearchWordManage")
	public String toSearchWordManage(HttpServletRequest request){
		return "searchword/searchWordManage";
	}
	@RequestMapping("/searchWordBar")  @ResponseBody
	public Map<String, Object> searchWordBar(){
		Map<String, Object> retMap=new HashMap<>();
		List<Map<String, Object>> list=searchWordService.trueList();
		List<String> x_dataList=new ArrayList<>();
		List<String> y_dataList=new ArrayList<>();
		for(Map<String, Object> map:list){
			x_dataList.add(map.get("searchtext").toString());
			y_dataList.add(map.get("searchcount").toString());
		}
		retMap.put("x_data", x_dataList);
		retMap.put("y_data",y_dataList);
		return retMap;
	}
}
