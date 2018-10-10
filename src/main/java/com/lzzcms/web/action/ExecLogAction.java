package com.lzzcms.web.action;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.lzzcms.dto.GridDto;
import com.lzzcms.model.ExecLog;
import com.lzzcms.service.ExecLogService;

/**
 * 搜索的内容
 * @author zhao
 */
@Controller
public class ExecLogAction {
	@Resource
	private ExecLogService execLogService;
	
	public ExecLogService getExecLogService() {
		return execLogService;
	}

	public void setExecLogService(ExecLogService execLogService) {
		this.execLogService = execLogService;
	}

	@RequestMapping("/toLogManage") 
	public  String  toLogManage(){
		 return "execlog/toLogManage";
	}
	
	@RequestMapping("/listLogs")  @ResponseBody
	public GridDto<Map<String,Object>>  listLogs(HttpServletRequest request){
		GridDto<Map<String,Object>> gridDto=new GridDto<Map<String,Object>>();
		Map<String, String> paramMap=new LinkedHashMap<String, String>();
		String parameter = request.getParameter("param");
		Gson gson=new Gson();
		if (parameter!=null) {
			paramMap = gson.fromJson(parameter, Map.class);
		}
		List<Map<String,Object>>  list=execLogService.trueList(paramMap);
		int pageCount=execLogService.getPageCount();
		gridDto.setTotal(pageCount);
		gridDto.setRows(list);
		return gridDto;
	}
}
