package com.lzzcms.web.action;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lzzcms.service.SearchService;
import com.lzzcms.service.SearchWordService;

/**
 * 搜索处理
 * @author zhao
 */
@Controller
public class SearchAction {
	private Logger logger=Logger.getLogger(SearchAction.class);
	@Resource
	private SearchService searchService;
	@Resource
	private SearchWordService searchWordService;
	SaveQueryStringThread saveQueryStringThread=new SaveQueryStringThread();
	public SearchWordService getSearchWordService() {
		return searchWordService;
	}

	public void setSearchWordService(SearchWordService searchWordService) {
		this.searchWordService = searchWordService;
	}

	public SearchService getSearchService() {
		return searchService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	@RequestMapping("/toSearchManage")
	public String toMakeIndex(){
		return "search/toSearchManage";
	}
	/**
	 * 生成索引
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/manualClear") @ResponseBody
	public Map<String, Object> manualClear(HttpServletRequest request,HttpServletResponse response){
		Map<String, Object> map=new HashMap<String, Object>();
		try {
			map=searchService.manualClear(request);
		} catch (Exception e) {
			map.put("type", "error");
			map.put("info", e.getMessage());
		}
		return map;
	}
	@RequestMapping("/search") 
	public String search(HttpServletRequest request,HttpServletResponse response){
		String ret =null; 
		try {
			String queryString = request.getParameter("queryString");
			ret=searchService.search(request, response,queryString);
			//searchWordService.saveOrUpdate(queryString);
			saveQueryStringThread.offer(queryString);
			if (ret!=null) {
				logger.error(ret);
			}
		} catch (Exception e) {
			    logger.error(e.getMessage());
		}
		return "search/search";
	}
	
	public SearchAction(){
		Thread thread=new Thread(saveQueryStringThread);
		thread.start();
	}
	//使用新的线程插入搜索词
	class SaveQueryStringThread implements Runnable{
		private Queue<String> queue = new ConcurrentLinkedQueue<String>();
		public void offer(String str){
			queue.offer(str);
		}
		@Override
		public void run() {
			while(true){
				if (!queue.isEmpty()) {
					String poll = queue.poll();
					searchWordService.saveOrUpdate(poll);
				}
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					logger.error("线程睡眠异常:",e);
				}
			}
		}
		
	}
}

