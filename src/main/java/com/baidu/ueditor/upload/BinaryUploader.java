package com.baidu.ueditor.upload;

import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.MultimediaInfo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import com.baidu.ueditor.PathFormat;
import com.baidu.ueditor.define.AppInfo;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.FileType;
import com.baidu.ueditor.define.State;
import com.google.gson.Gson;
import com.lzzcms.install.DbUtils;
import com.lzzcms.utils.LzzConstants;

public class BinaryUploader {
	private static Logger logger=Logger.getLogger(BinaryUploader.class);
	public static final State save(HttpServletRequest request,
			Map<String, Object> conf) {
		FileItemStream fileStream = null;
		boolean isAjaxUpload = request.getHeader( "X_Requested_With" ) != null;

		if (!ServletFileUpload.isMultipartContent(request)) {
			return new BaseState(false, AppInfo.NOT_MULTIPART_CONTENT);
		}

		ServletFileUpload upload = new ServletFileUpload(
				new DiskFileItemFactory());

        if ( isAjaxUpload ) {
            upload.setHeaderEncoding( "UTF-8" );
        }

		try {
			//List<FileItem> parseRequest = upload.parseRequest(request);
			FileItemIterator iterator = upload.getItemIterator(request);

			while (iterator.hasNext()) {
				fileStream = iterator.next();

				if (!fileStream.isFormField())
					break;
				fileStream = null;
			}

			if (fileStream == null) {
				return new BaseState(false, AppInfo.NOTFOUND_UPLOAD_DATA);
			}

			String savePath = (String) conf.get("savePath");
			String originFileName = fileStream.getName();
			String suffix = FileType.getSuffixByFilename(originFileName);

			originFileName = originFileName.substring(0,
					originFileName.length() - suffix.length());
			savePath = savePath + suffix;

			long maxSize = ((Long) conf.get("maxSize")).longValue();

			if (!validType(suffix, (String[]) conf.get("allowFiles"))) {
				return new BaseState(false, AppInfo.NOT_ALLOW_FILE_TYPE);
			}

			savePath = PathFormat.parse(savePath, originFileName);

			String physicalPath = (String) conf.get("rootPath") + savePath;

			InputStream is = fileStream.openStream();
			State storageState = StorageManager.saveFileByInputStream(is,
					physicalPath, maxSize);
			is.close();

			if (storageState.isSuccess()) {
				storageState.putInfo("url", PathFormat.format(savePath));
				storageState.putInfo("type", suffix);
				storageState.putInfo("original", originFileName + suffix);
			}
			//处理自己的逻辑的函数
			saveUploadInfo2Db(physicalPath, storageState,request);
			return storageState;
		} catch (FileUploadException e) {
			return new BaseState(false, AppInfo.PARSE_REQUEST_ERROR);
		} catch (IOException e) {
		}
		return new BaseState(false, AppInfo.IO_ERROR);
	}
	//在原有的逻辑(图片(单个或多个)、涂鸦、视频、文件保存成功后)上加入自己的逻辑:把上传好的文件信息保存在数据库中
	private static void saveUploadInfo2Db(String physicalPath,
			State storageState, HttpServletRequest request) throws IOException {
		StringBuffer sBuffer=new StringBuffer();
		sBuffer.append(" insert into lzz_uploadfile_info(original_name,file_size,file_type,width,height, ");
		sBuffer.append(" time_long,cont_id,file_path) values(?,?,?,?,?,?,?,?) ");
		/*
		 * storageState:
		 * {"state": "SUCCESS","title": "1494528557329029684.png","original": "logo2.png","type": ".png",
		 * "url": "/uploads/image/2017/05/1494528557329029684.png","size": "5513"}
		conf:
		{savePath=/uploads/image/{yyyy}/{mm}/{time}{rand:6}, rootPath=F:/Program/workspaces/EclipseWS/lzzcms/web/,
		 allowFiles=[Ljava.lang.String;@5ca88b3, isBase64=false, fieldName=upfile, maxSize=2048000}
		 */
		Gson gson=new Gson();
		Map<String,Object> map = gson.fromJson(storageState.toJSONString(), Map.class);
		String my_url = map.get("url").toString();
		Pattern pattern=Pattern.compile(LzzConstants.UPLOADS+"(\\w+)/", Pattern.CASE_INSENSITIVE);//匹配到类型 image/video/file
		Matcher matcher = pattern.matcher(my_url);
		matcher.find();
		String my_type = matcher.group(1);
		int my_width=0;
		int my_height=0;
		String my_time_long="";
		if ("image".equals(my_type)) {
			BufferedImage bi = ImageIO.read(new File(physicalPath));
			my_width=bi.getWidth();
			my_height=bi.getHeight();
		}else if ("video".equals(my_type)) {
			File videoFile=new File(physicalPath);
			Encoder encoder=new Encoder();
			try {
				MultimediaInfo info = encoder.getInfo(videoFile);
				long duration = info.getDuration();//毫秒数
				my_time_long=(duration/1000/60)+"分钟";
			} catch (Exception e) {
				logger.error("获取video时长出错:",e);
			}
			
		}//文件类型
		int generateId = 0;//前边插入完成后生成的id
		Connection connection =null;
		PreparedStatement ps=null;
		try {
			 connection=DbUtils.getConn();
			ps=connection.prepareStatement(sBuffer.toString());
			ps.setObject(1, map.get("original"));
			ps.setObject(2, map.get("size"));
			ps.setObject(3, my_type);
			ps.setObject(4, my_width);
			ps.setObject(5, my_height);
			ps.setObject(6, my_time_long);
			ps.setObject(7, 0);
			ps.setObject(8, my_url);
			ps.executeUpdate();
			ResultSet generatedKeys = ps.getGeneratedKeys();
			if(generatedKeys.next()){
				Object object = generatedKeys.getObject(1);
				if (object!=null) {
					generateId=Integer.valueOf(object.toString());
				}
			}
		} catch (SQLException e) {
			logger.error("把上传好的文件信息保存在数据库中:",e);
		}finally{
			DbUtils.releasePs(ps);
			DbUtils.releaseConn(connection);
		}
		synchronized(BinaryUploader.class){//图片上传是异步的
			List<Integer> list=(List<Integer>) request.getSession().getAttribute("uploadIds");
			if (list==null) {
				list=new ArrayList<Integer>();
			}
			list.add(generateId);
			request.getSession().setAttribute("uploadIds", list);
		}
		
	}

	private static boolean validType(String type, String[] allowTypes) {
		List<String> list = Arrays.asList(allowTypes);

		return list.contains(type);
	}
}
