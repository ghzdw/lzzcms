package com.lzzcms.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.lzzcms.dao.FriendLinkDao;
import com.lzzcms.model.ChannelInfo;
import com.lzzcms.model.FriendLink;
import com.lzzcms.service.FriendLinkService;
import com.lzzcms.utils.LzzConstants;
import com.lzzcms.utils.LzzcmsUtils;
import com.lzzcms.utils.PageContext;

@Service
public class FriendLinkServiceImpl implements FriendLinkService{
	private static Logger logger=Logger.getLogger(FriendLinkServiceImpl.class);
	@Resource
	private FriendLinkDao friendLinkDao;

	public FriendLinkDao getFriendLinkDao() {
		return friendLinkDao;
	}

	public void setFriendLinkDao(FriendLinkDao friendLinkDao) {
		this.friendLinkDao = friendLinkDao;
	}

	@Override
	public List<FriendLink> trueList(HttpServletRequest request) {
		StringBuffer sb=new StringBuffer();
		sb.append(" select id,linkdesc,type,url from lzz_friendlink ");
		String sort = request.getParameter("sort");
		String order = request.getParameter("order");
		if (StringUtils.isNotBlank(sort)&&StringUtils.isNotBlank(order)) {
			sb.append(" order by "+sort+" "+order);
		}
		if (PageContext.getPageUtil().isNeedPage()) {
			int pageNow=PageContext.getPageUtil().getPageNow();
			int pageSize=PageContext.getPageUtil().getPageSize();
			int start=(pageNow-1)*pageSize;
			sb.append(" limit "+start+","+pageSize);
		}
		return friendLinkDao.findBySql(sb.toString());
	}
	@Override
	public List<FriendLink> getFlinks(String hql,Byte type) {
		return friendLinkDao.findByHql(hql,type);
	}

	@Override
	public long getTotalCount() {
		StringBuffer sBuffer=new StringBuffer();
		sBuffer.append(" select count(*) from FriendLink");
		long count = (long)friendLinkDao.uniqueResult(sBuffer.toString());
		return count;
	}

	@Override
	public Map<String, Object> trueAdd(MultipartFile file,HttpServletRequest request) {
		Map<String, Object> retMap=new HashMap<String, Object>();
		String type = request.getParameter("fl_type");
		String name = request.getParameter("fl_linkname");//默认文字链接的内容
		if ("2".equals(type)) {//图片
			String originalFilename = file.getOriginalFilename();
			String finalThumbPath=LzzConstants.UPLOADS+"linkimg/";//"/uploads/linkimg/";
			if (StringUtils.isNotBlank(originalFilename)) {
				//得到文件存放的路径
				finalThumbPath+=LzzcmsUtils.getPatternDateString("yyyy/MM/", new Date());//  /uploads/linkimg/2017/05/
				File dir=new File(LzzcmsUtils.getRealPath(request,finalThumbPath));/*F:\\Program\\workspaces\\EclipseWS\\lzzcms\\web\\uploads\\linkimg\\2017\\06*/
				if (!dir.exists()) {
					dir.mkdirs();
				}
				String baseName2Py=LzzcmsUtils.getPinYinHeadChar(FilenameUtils.getBaseName(originalFilename));
				String suffix="."+FilenameUtils.getExtension(originalFilename);
				String newFileName=baseName2Py+suffix;//360jt20170511015353184.jpg
				finalThumbPath+=LzzcmsUtils.random()+"_"+newFileName;// /uploads/linkimg/2017/05/123456789_360jt20170511015353184.jpg
				//targetPath:F:\\Program\\workspaces\\EclipseWS\\lzzcms\\web\\uploads\\thumb\\2017\\05\\3942865675762594_360jt20170511015353184.jpg
				String targetPath = LzzcmsUtils.getRealPath(request,finalThumbPath);
				File destFile=new File(targetPath);
				//复制上传的文件到上边构建的目标文件
				CommonsMultipartFile commonsMultipartFile=(CommonsMultipartFile) file;
				DiskFileItem diskFileItem=(DiskFileItem) commonsMultipartFile.getFileItem();
				try {
					File remoteFile = new File(diskFileItem.getName());
				   File tmpFile = new File(dir.getAbsolutePath(), remoteFile.getName());
				    diskFileItem.write(tmpFile);
				    FileUtils.moveFile(tmpFile, destFile);
				} catch (Exception e) {
					logger.error("拷贝图片到上传目录出错:"+e);
					retMap.put("info", e.getMessage());
					retMap.put("type", "error");
					return retMap;
				}
				name=finalThumbPath;//如果是图片则修改为图片的相对路径
			}
		}
		String url = request.getParameter("fl_url");
		StringBuffer sbBuffer=new StringBuffer();
		sbBuffer.append(" insert into lzz_friendlink(linkdesc,type,url) values(?,?,?)");
		friendLinkDao.executeSql(sbBuffer.toString(), name,type,url);
		retMap.put("info", "添加超链接成功");
		retMap.put("type", "info");
		return retMap;
	}

	@Override
	public void delete(HttpServletRequest request) {
		 String ids = request.getParameter("param");
		StringBuffer sbBuffer=new StringBuffer();
		sbBuffer.append(" delete from lzz_friendlink where id in("+ids+")");
		friendLinkDao.executeSql(sbBuffer.toString());
	}

	@Override
	public void trueUpdate(HttpServletRequest request) {
		String url = request.getParameter("fl_upurl");
		String id = request.getParameter("fl_upid");
		StringBuffer sbBuffer=new StringBuffer();
		sbBuffer.append(" update lzz_friendlink set url=? where id=?");
		friendLinkDao.executeSql(sbBuffer.toString(), url,id);
	}

}
