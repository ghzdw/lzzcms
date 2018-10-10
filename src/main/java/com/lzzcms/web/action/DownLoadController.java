package com.lzzcms.web.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lzzcms.dto.GridDto;
import com.lzzcms.model.FriendLink;
import com.lzzcms.service.DownLoadService;
import com.lzzcms.service.impl.ChannelInfoServiceImpl;
import com.lzzcms.utils.LzzcmsUtils;

@Controller
public class DownLoadController {
	private Logger logger=Logger.getLogger(DownLoadController.class);
	@Resource
	private DownLoadService downLoadService;
	@RequestMapping("/downLoad")
	public void downLoad(HttpServletRequest request,HttpServletResponse response){
		String toDown = request.getParameter("toDown");
		String realPath = LzzcmsUtils.getRealPath(request,"/WEB-INF/download/"+toDown);
		File file=new File(realPath);
		FileInputStream fis=null;
		OutputStream os=null; 
		try {
			response.setHeader("Content-Disposition", "attachment; filename="
					+ URLEncoder.encode(toDown, "UTF-8"));
			response.setContentType("application/octet-stream;charset=UTF-8");
			response.setContentLength(new Double(file.length()).intValue());
			fis=new FileInputStream(file);
			os= response.getOutputStream();
			byte[] bytes=new byte[1024];
			int len=0;
			while((len=fis.read(bytes))!=-1){
				os.write(bytes, 0, len);
			}
			os.flush();
			downLoadService.downLoad(toDown,request);
		} catch (Exception e) {
			logger.error("下载出错:",e);
		}finally{
			LzzcmsUtils.closeIs(fis);
			LzzcmsUtils.closeOs(os);
		}
	}
	@RequestMapping("/downloadManage")
	public String flManage(){
		return "download/downloadManage";
	}
	@RequestMapping("/listDownloads")
	@ResponseBody
	public GridDto<Map<String, Object>> listDownloads(HttpServletRequest request){
		List<Map<String, Object>> list=downLoadService.listDownloads(request);
		long totalCount=downLoadService.getTotalCount(request);
		GridDto<Map<String, Object>> gridDto=new GridDto<Map<String, Object>>();
		gridDto.setTotal(totalCount);
		gridDto.setRows(list);
		return gridDto;
	}
}
