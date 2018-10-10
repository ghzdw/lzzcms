package com.lzzcms.web.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lzzcms.dto.TreeDto;
import com.lzzcms.model.ColumnInfo;
import com.lzzcms.service.ColumnInfoService;

@Controller
public class ColumnInfoAction {
	@Resource
	private ColumnInfoService columnInfoService;
	
	public ColumnInfoService getColumnInfoService() {
		return columnInfoService;
	}
	public void setColumnInfoService(ColumnInfoService columnInfoService) {
		this.columnInfoService = columnInfoService;
	}
	@RequestMapping("/columnManage")
	public String columnManage(){
		return "column/columnManage";
	}
	//构造栏目的树形表格
	@RequestMapping(value="/columnInfo",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> columnInfo(){
		List<Map<String, Object>> list=columnInfoService.columnInfo();
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("rows", list);
		map.put("total", list.size());
		return map;
	}
	@RequestMapping("/getColumnByChanelId") @ResponseBody//来自contentManage.jsp否则来自toAdd.jsp
	public List<Map<String, Object>> getColumnByChanelId(HttpServletRequest request){
		String chanelId=request.getParameter("chanelId");
		String chlId=chanelId;
		List<Map<String, Object>>  list=null;
		if (chanelId.indexOf(",")>-1) {
			String[] split = chanelId.split(",");
			chlId=split[0];//增加内容时只有列表栏目可以增加
			list=columnInfoService.getListColumnByChanelId(Integer.valueOf(chlId));
		}
		if (chanelId.indexOf(",")==-1) {//查询内容时也是只有列表栏目可以供选择
			list=columnInfoService.getListColumnByChanelId(Integer.valueOf(chlId));
			Map<String, Object> map=new HashMap<String, Object>();
			map.put("id", "");
			map.put("name", "---所有---");
			list.add(0, map);
		}
		return list;
	}
	@RequestMapping("/getColumnByChanelIdForSpider") @ResponseBody
	public List<Map<String, Object>> getColumnByChanelIdForSpider(HttpServletRequest request){
		String chanelId=request.getParameter("chanelId");
		List<Map<String, Object>>  list= columnInfoService.getColumnByChanelIdForSpider(Integer.valueOf(chanelId));
		return list;
	}
	@RequestMapping("/toAddCln") //添加子栏目
	public String toAddCln(Model model,HttpServletRequest request){
		String idString=request.getParameter("clnid");
		ColumnInfo c=columnInfoService.getColumnById(Integer.valueOf(idString));
		model.addAttribute("clnInfo", c);
		return "column/toAddCln";
	}
	//添加子栏目的提交
	@RequestMapping(value="/trueAddColumn",method=RequestMethod.POST,produces="text/plain;charset=UTF-8") @ResponseBody
	public String trueAddColumn(HttpServletRequest request){
		columnInfoService.trueAddColumn(request);
		return "success";
	}
	//删除栏目
	@RequestMapping(value="/deleteClnById",produces="text/plain;charset=UTF-8") @ResponseBody
	public String deleteClnById(HttpServletRequest request){
		String columnid = request.getParameter("columnid");
		String chlid = request.getParameter("chlid");
		columnInfoService.deleteClnById(columnid,chlid,request);
		return "success";
	}
	//to更新栏目
	@RequestMapping(value="/toUpCln",produces="text/plain;charset=UTF-8") 
	public String toUpCln(Model map,HttpServletRequest request){
		String clnid = request.getParameter("clnid");
		ColumnInfo c=columnInfoService.getColumnById(Integer.valueOf(clnid));
		map.addAttribute("clnInfo", c);
		return "column/toUpCln";
	}
	//更新栏目
	@RequestMapping(value="/trueUpColumn",produces="text/plain;charset=UTF-8") @ResponseBody
	public String trueUpColumn(HttpServletRequest request){
		columnInfoService.trueUpColumn(request);
		return "success";
	}
	//栏目明细
	@RequestMapping(value="/getClnDetails",produces="text/plain;charset=UTF-8")
	public String getClnDetails(Model map,HttpServletRequest request){
		String clnid = request.getParameter("clnid");
		ColumnInfo c=columnInfoService.getColumnById(Integer.valueOf(clnid));
		map.addAttribute("clnInfo", c);
		return "column/clnDetails";
	}
	@RequestMapping("/getColumns") @ResponseBody
	public List<TreeDto> getColumns(HttpServletRequest request){
		List<TreeDto>  list= columnInfoService.getColumns();
		return list;
	}
	@RequestMapping("/addTopCln") //添加顶级栏目
	public String addTopClnIframe(Model model,HttpServletRequest request){
		String parameter = request.getParameter("a");
		model.addAttribute("a", parameter+"haha");
		return "column/addTopCln";
	}
	//添加顶级栏目的提交
	@RequestMapping(value="/trueAddTopColumn",method=RequestMethod.POST,produces="text/plain;charset=UTF-8") @ResponseBody
	public String trueAddTopColumn(HttpServletRequest request){
		columnInfoService.trueAddTopColumn(request);
		return "success";
	}
	@RequestMapping(value="/getDirByTopClnName",method=RequestMethod.POST) @ResponseBody
	public Map<String, Object> getDirByTopClnName(HttpServletRequest request){
		String topName=request.getParameter("clnName");
		String ret =null; 
		Map<String, Object> map=new HashMap<String, Object>();
		try {
			ret=columnInfoService.getDirByTopClnName(topName);
			map.put("info", "ok");
			map.put("dirName", ret);
		} catch (Exception e) {
			map.put("info", "error");
			map.put("errinfo", e.getMessage());
		}
		return map;
	}
}
