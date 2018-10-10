package com.lzzcms.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.lzzcms.dao.RightInfoDao;
import com.lzzcms.dto.TreeDto;
import com.lzzcms.model.ColumnInfo;
import com.lzzcms.model.RightInfo;
import com.lzzcms.service.RightInfoService;

@Service
public class RightInfoServiceImpl implements RightInfoService{
	@Resource
	private RightInfoDao rightInfoDao;

	public RightInfoDao getRightInfoDao() {
		return rightInfoDao;
	}

	public void setRightInfoDao(RightInfoDao rightInfoDao) {
		this.rightInfoDao = rightInfoDao;
	}

	@Override //得到可分配的权限，权限不需要设置url，因为不用点击
	public List<TreeDto> allRights(String basePath) {
		List<TreeDto> retList=new ArrayList<>();
		StringBuffer sBuffer=new StringBuffer();
		sBuffer.append(" select  ri.* from lzz_right ri ");
		sBuffer.append(" where ri.canassign='yes' and ri.righturl is null order by ri.orderno asc ");
		List<RightInfo> rightInfos= rightInfoDao.findBySql(sBuffer.toString());
		for(RightInfo rightInfo:rightInfos){
			TreeDto treeDto=new TreeDto();
			treeDto.setId(rightInfo.getRightId());
			treeDto.setText(rightInfo.getRightName());
			treeDto.setState("open");
			retList.add(treeDto);
			sBuffer.setLength(0);
			sBuffer.append(" select  ri.* from lzz_right ri where ri.canassign='yes' ");
			sBuffer.append(" and ri.parent_id=? order by ri.orderno asc ");
			List<RightInfo> sons= rightInfoDao.findBySql(sBuffer.toString(),rightInfo.getRightId());
			this.recursionRight(sons, treeDto,sBuffer.toString(),null);
		}
		return retList;
	}
	//sql:加载权限、得到可分配的权限的sql条件不一样,adminId当不是超级管理员时就传入null
	private void recursionRight(List<RightInfo> sons,TreeDto parentTreeDto,String sql,Integer adminId) {
		List<TreeDto> crtSubList=new ArrayList<TreeDto>();
		for(RightInfo rInfo:sons){
			List<RightInfo> rInfoSons=null;
			if (adminId==null) {
				rInfoSons=rightInfoDao.findBySql(sql,rInfo.getRightId());
			}else {
				rInfoSons=rightInfoDao.findBySql(sql,rInfo.getRightId(),adminId);
			}
			TreeDto treeDto=new TreeDto();
			treeDto.setId(rInfo.getRightId());
			treeDto.setText(rInfo.getRightName());
			if (!rInfoSons.isEmpty()) {//rInfo有子栏目
				treeDto.setState("open");
				this.recursionRight(rInfoSons,treeDto,sql,adminId);
			}else {//这里只有加载权限的时候需要设置，得到可分配权限不需要设置，不过设置了也没关系
				if (StringUtils.isNotBlank(rInfo.getRightUrl())) {//是叶子
	        		Map<String, String> map=new HashMap<String, String>();
	        		map.put("url", rInfo.getRightUrl());//权限url比如：trueLogin
	        		treeDto.setAttributes(map);
				}
			}
			crtSubList.add(treeDto);
		}
		parentTreeDto.setChildren(crtSubList);
	}
	
	@Override //加载用户的权限
	public List<TreeDto> loadRights(int adminId) {
		List<TreeDto> retList =new ArrayList<TreeDto>(); 
		StringBuffer sb=new StringBuffer();
		sb.append("select rolevalue from lzz_role where roleid in(select role_id from link_admin_role where admin_id=?)");
		List<Integer> rolevalues = rightInfoDao.queryForListInteger(sb.toString(), adminId);
		sb.setLength(0);
		if (rolevalues==null||rolevalues.size()==0) {
		}else if(rolevalues.contains(-1)){//admin
			sb.append(" select  ri.* from lzz_right ri where ri.righttype='coarse' and ri.canassign='yes'  ");
			sb.append(" and ri.righturl is null order by ri.orderno asc ");
			List<RightInfo> rightInfos= rightInfoDao.findBySql(sb.toString());
			for(RightInfo rightInfo:rightInfos){
				TreeDto treeDto=new TreeDto();
				treeDto.setId(rightInfo.getRightId());
				treeDto.setText(rightInfo.getRightName());
				treeDto.setState("open");
				retList.add(treeDto);
				sb.setLength(0);
				sb.append(" select  ri.* from lzz_right ri where ri.righttype='coarse' and ri.canassign='yes' ");
				sb.append(" and ri.parent_id=? order by ri.orderno asc ");
				List<RightInfo> sons= rightInfoDao.findBySql(sb.toString(),rightInfo.getRightId());
				this.recursionRight(sons, treeDto,sb.toString(),null);
			}
		}else {
			sb.append("  select  ri.* from lzz_right ri where ri.righttype='coarse'  and ri.canassign='yes'  ");
			sb.append("  and ri.righturl is null  and ri.rightid IN ");
			sb.append(" (SELECT lrr.right_id FROM link_role_right lrr WHERE lrr.role_id IN ");
			sb.append(" (SELECT lar.role_id FROM link_admin_role lar where lar.admin_id=?)) order by ri.orderno asc ");
			List<RightInfo> rightInfos=rightInfoDao.findBySql(sb.toString(), adminId);
			for(RightInfo rightInfo:rightInfos){
				TreeDto treeDto=new TreeDto();
				treeDto.setId(rightInfo.getRightId());
				treeDto.setText(rightInfo.getRightName());
				treeDto.setState("open");
				retList.add(treeDto);
				sb.setLength(0);
				sb.append("  select  ri.* from lzz_right ri where ri.righttype='coarse'  and ri.canassign='yes'  ");
				sb.append("  and ri.parent_id=?  and ri.rightid IN ");
				sb.append(" (SELECT lrr.right_id FROM link_role_right lrr WHERE lrr.role_id IN ");
				sb.append(" (SELECT lar.role_id FROM link_admin_role lar where lar.admin_id=?)) order by ri.orderno asc ");
				List<RightInfo> sons= rightInfoDao.findBySql(sb.toString(),rightInfo.getRightId(),adminId);
				this.recursionRight(sons, treeDto,sb.toString(),adminId);
			}
		}
		return retList;
	}
	@Override
	public List<Map<String, Object>> getRightsForSc() {
		StringBuffer sb=new StringBuffer();
		sb.append("select  ri.righturl,ri.rightgroup,ri.rightcode,ri.common,rightname,canassign from lzz_right ri where ri.righturl is not null ");
		List<Map<String, Object>> queryForList = rightInfoDao.queryForList(sb.toString());
		return queryForList;
	}
}
