package com.lzzcms.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.lzzcms.dto.TreeDto;
import com.lzzcms.model.ColumnInfo;



public interface ColumnInfoService {

	List<Map<String, Object>> getColumnByChanelIdForSpider(Integer chanelId);

	List<Map<String, Object>> columnInfo();

	ColumnInfo getColumnById(Integer valueOf);

	void trueAddColumn(HttpServletRequest request);

	void deleteClnById(String columnid, String chlid,HttpServletRequest request);

   void trueUpColumn(HttpServletRequest request);

	List<TreeDto> getColumns();
	
	void trueAddTopColumn(HttpServletRequest request);
	
	String getDirByTopClnName(String topName);
	List<Map<String, Object>> getListColumnByChanelId(Integer chanelId);


	
}
