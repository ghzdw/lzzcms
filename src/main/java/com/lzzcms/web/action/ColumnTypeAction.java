package com.lzzcms.web.action;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lzzcms.model.ColumnType;
import com.lzzcms.service.ColumnTypeService;
import com.lzzcms.utils.PageContext;
import com.lzzcms.utils.PageUtil;

@Controller
public class ColumnTypeAction {
	@Resource
	private ColumnTypeService columnTypeService;
	
	public ColumnTypeService getColumnTypeService() {
		return columnTypeService;
	}

	public void setColumnTypeService(ColumnTypeService columnTypeService) {
		this.columnTypeService = columnTypeService;
	}

	@RequestMapping("/getClnTypeForCombobox")
	@ResponseBody
	public  List<ColumnType> getClnTypeForCombobox(){
		 List<ColumnType> list=columnTypeService.getClnTypeForCombobox();
		ColumnType columnType=new ColumnType();
		columnType.setId(0);
		columnType.setTypeName("未选择");
		list.add(0, columnType);
		return list;
	}
}
