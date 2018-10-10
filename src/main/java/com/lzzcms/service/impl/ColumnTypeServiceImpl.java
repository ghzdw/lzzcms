package com.lzzcms.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lzzcms.dao.ColumnTypeDao;
import com.lzzcms.model.ColumnType;
import com.lzzcms.service.ColumnTypeService;

@Service
public class ColumnTypeServiceImpl implements ColumnTypeService{

	@Resource
	private ColumnTypeDao columnTypeDao;
	
	public ColumnTypeDao getColumnTypeDao() {
		return columnTypeDao;
	}

	public void setColumnTypeDao(ColumnTypeDao columnTypeDao) {
		this.columnTypeDao = columnTypeDao;
	}

	@Override
	public List<ColumnType> getClnTypeForCombobox() {
		String hqlString="from ColumnType";
		return columnTypeDao.findByHql(hqlString);
	}

}
