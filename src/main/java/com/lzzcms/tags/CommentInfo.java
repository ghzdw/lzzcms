 package com.lzzcms.tags;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.lzzcms.dao.ColumnInfoDao;
import com.lzzcms.dao.CommentDao;
import com.lzzcms.dao.SystemParamDao;
import com.lzzcms.listeners.SpringBeanFactory;
import com.lzzcms.model.ColumnInfo;
import com.lzzcms.utils.LzzConstants;

/**
 * <zdw:comment docid="33"/>
 */
public class CommentInfo extends SimpleTagSupport{
	@Override
	public void doTag() throws JspException, IOException {
		JspContext jspContext = getJspContext();
		int s =  (int) jspContext.getAttributesScope("onecont");//生成文档的地方设置的
		if (s!=0) {
			Map<String, Object>	contentMap=(Map<String, Object>)  jspContext.getAttribute("onecont", s);
			String docid=contentMap.get("cont_comm_id").toString();
			CommentDao commentDao = SpringBeanFactory.getBean("commentDaoImpl", CommentDao.class);
			StringBuffer sBuffer=new StringBuffer();
			sBuffer.append(" select c.id,c.comm_id,c.reply_id,c.pub_name,c.pub_ip,c.pub_location,c.comment_cont ");
			sBuffer.append(" ,date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time from lzz_comment c where c.comm_id=?  ");
			sBuffer.append(" order by create_time desc ");
			List<Map<String, Object>> list = commentDao.queryForList(sBuffer.toString(),docid);
			int size = list.size();
			Map<String, Map<String, Object>> mapTool=new HashMap<>();
			StringBuffer allComments=new StringBuffer();
			getStyle(allComments);
			getBasic(allComments,docid);
			if (list!=null&&size!=0) {
				for (int i = 0; i < size; i++) {
					mapTool.put(list.get(i).get("id").toString(), list.get(i));
				}
				for (int i = 0; i < size; i++) {
					Map<String, Object> oneCommentMap = list.get(i);
					recursionComment(allComments,oneCommentMap,null,mapTool);
				}
			}
			allComments.append(" 		</div> ");//<div id='lc_comment_loadarea'>
			allComments.append(" 	</div> ");//<div id='lzzcms_comment'>
			getJs(allComments);
			jspContext.getOut().write(allComments.toString());
		}else {
			jspContext.getOut().write("");
			throw new RuntimeException("comment标签只能在内容页面使用");
		}
	}
	private void getBasic(StringBuffer allComments, String docid) {
		allComments.append(" <div id='lzzcms_comment'>                                                                      ");
		allComments.append(" 	  <!-- 模板 -->                                                                             ");
		allComments.append(" 		<section class='lc_oneitem_building_tpl hiddenele'>                                     ");
		allComments.append(" 			<section class='lc_onerealitem'>                                                    ");
		allComments.append(" 				<div class='lc_visitorimgshow'><img src='"+getBasePath()+"user.png'/></div>                      ");
		allComments.append(" 				<div class='lc_oneitem'>                                                        ");
		allComments.append(" 					<span class='lc_username'>你说</span>                                     ");
		allComments.append(" 					<span class='lc_userlocation'>[来自远方]</span>                           ");
		allComments.append(" 					<span class='lc_publishdate lc_gray'>刚刚</span>             ");
		allComments.append(" 					<div class='lc_comment'>test0</div>                                         ");
		allComments.append(" 					<div class='lc_interactive'>                                                ");
		allComments.append(" 						<span class='lc_reply'>回复</span>                                      ");
		allComments.append(" 					</div>                                                                      ");
		allComments.append(" 				</div>                                                                          ");
		allComments.append(" 			</section>                                                                          ");
		allComments.append(" 		</section>                                                                              ");
		allComments.append(" 		<!-- 发布评论 -->                                                                       ");
		allComments.append(" 		<span class='lc_visitorimg'>                                                            ");
		allComments.append(" 			<img src='"+getBasePath()+"user.png'/>                                                               ");
		allComments.append(" 		</span>                                                                                 ");
		allComments.append(" 		<span class='lc_gai'></span>                                                            ");
		allComments.append(" 		<span class='lc_radian'></span>                                                         ");
		allComments.append(" 		<span class='lc_visitor'>乐之者：</span>                                                ");
		allComments.append(" 		<div class='lc_normalareaparent'>                                                       ");
		allComments.append(" 		  <section class='lc_normalarea'>                                                       ");
		allComments.append(" 			<textarea class='lc_txtarea lc_gray'>别看了，写点吧....</textarea>                  ");
		allComments.append(" 			<div class='lc_rowsection'>                                                         ");
		allComments.append(" 				<div class='lc_facemap hiddenele'>                                              ");
		allComments.append(" <ul class='lc_ul lc_paddingtop'> " );
		allComments.append(" 	<li><span data-text='[/擦汗]' class='lc_face lc_face01'></span></li> " );
		allComments.append(" 	<li><span data-text='[/钞票]'  class='lc_face lc_face02'></span></li> " );
		allComments.append(" 	<li><span data-text='[/发怒]'  class='lc_face lc_face03'></span></li> " );
		allComments.append(" 	<li><span data-text='[/浮云]'  class='lc_face lc_face04'></span></li> " );
		allComments.append(" 	<li><span data-text='[/给力]'  class='lc_face lc_face05'></span></li> " );
		allComments.append(" 	<li><span data-text='[/大哭]'  class='lc_face lc_face06'></span></li> " );
		allComments.append(" 	<li><span data-text='[/憨笑]'  class='lc_face lc_face07'></span></li> " );
		allComments.append(" 	<li><span data-text='[/色鬼]'  class='lc_face lc_face08'></span></li> " );
		allComments.append(" </ul> " );
		allComments.append(" <ul class='lc_ul'> " );
		allComments.append(" 	<li><span data-text='[/奋斗]'  class='lc_face lc_face09'></span></li> " );
		allComments.append(" 	<li><span data-text='[/鼓掌]'  class='lc_face lc_face10'></span></li> " );
		allComments.append(" 	<li><span data-text='[/鄙视]'  class='lc_face lc_face11'></span></li> " );
		allComments.append(" 	<li><span data-text='[/可爱]'  class='lc_face lc_face12'></span></li> " );
		allComments.append(" 	<li><span data-text='[/闭嘴]'  class='lc_face lc_face13'></span></li> " );
		allComments.append(" 	<li><span data-text='[/疑问]'  class='lc_face lc_face14'></span></li> " );
		allComments.append(" 	<li><span data-text='[/抓狂]'  class='lc_face lc_face15'></span></li> " );
		allComments.append(" 	<li><span data-text='[/惊讶]'  class='lc_face lc_face16'></span></li> " );
		allComments.append(" </ul> " );
		allComments.append(" <ul class='lc_ul'> " );
		allComments.append(" 	<li><span data-text='[/可怜]'  class='lc_face lc_face17'></span></li> " );
		allComments.append(" 	<li><span data-text='[/弱小]'  class='lc_face lc_face18'></span></li> " );
		allComments.append(" 	<li><span data-text='[/强大]'  class='lc_face lc_face19'></span></li> " );
		allComments.append(" 	<li><span data-text='[/握手]'  class='lc_face lc_face20'></span></li> " );
		allComments.append(" 	<li><span data-text='[/肌肉]'  class='lc_face lc_face21'></span></li> " );
		allComments.append(" 	<li><span data-text='[/喝酒]'  class='lc_face lc_face22'></span></li> " );
		allComments.append(" 	<li><span data-text='[/玫瑰]'  class='lc_face lc_face23'></span></li> " );
		allComments.append(" 	<li><span data-text='[/打酱油]'  class='lc_face lc_face24'></span></li> " );
		allComments.append(" </ul> " );
		allComments.append(" 				</div>                                                                          ");
		allComments.append(" 				<span class='lc_biaoqing'></span>                                               ");
		allComments.append(" 				<div class='lc_submitpost'><img class='submitimg' src='"+getBasePath()+"submitpost.png'/></div>  ");
		allComments.append(" 			</div>                                                                              ");
		allComments.append(" 		  </section>	                                                                        ");
		allComments.append(" 		</div>                                                                                  ");
		allComments.append(" 		<div id='tmp' data-docid="+docid+" class='hiddenele'></div>                                                  ");
		allComments.append(" 		<!-- 评论加载区域 -->                                                                   ");
		allComments.append(" 		<div id='lc_comment_loadarea'>                                                          ");
		allComments.append(" 			<div class='lc_tiptitle_parent'>                                                    ");
		allComments.append(" 				<div class='lc_tiptitle'>评论</div>                                             ");
		allComments.append(" 			</div>                                                                              ");
		allComments.append(" 			<div class='lc_clear'></div>                                                        ");
		allComments.append(" 			<div id='lc_tagstyle'>最新评论</div>                                                ");
		
	}

	private String getBasePath() {
		return getRealBasePath()+"/resources/imgs/";
	}
	private String getRealBasePath() {
		int scope =  (int) getJspContext().getAttributesScope("basePath");//拦截器里在request中设置的
		String basePath = (String)getJspContext().getAttribute("basePath", scope);
		return basePath;
	}

	private void getJs(StringBuffer allComments) {
		allComments.append(" <script type='text/javascript'>                                                                    ");
		//每一个emoji标情的点击
		allComments.append(" $('#lzzcms_comment').on('click','span[data-text]',function(){ " );
		allComments.append(" 	var cxt=$(this).parents('.lc_normalarea'); " );
		allComments.append(" 	var org=$('.lc_txtarea',cxt).val(); " );
		allComments.append(" 	$('.lc_txtarea',cxt).val(org+$(this).attr('data-text')); " );
		allComments.append(" 	$('.lc_facemap',cxt).addClass('hiddenele'); " );
		allComments.append(" }); " );
		//初始和动态增加的评论框获得焦点事件处理      
		allComments.append(" 		$('#lzzcms_comment').on('focus','.lc_txtarea',function(){                                   ");
		allComments.append(" 			$(this).text('');                                                                       ");
		allComments.append(" 			$(this).removeClass('lc_gray');                                                         ");
		allComments.append(" 		});                                                                                         ");
		//标签图标点击处理 
		allComments.append(" 		$('#lzzcms_comment').on('click','.lc_biaoqing',function(){                                  ");
		allComments.append(" 			var context=$(this).parents('.lc_rowsection');                                          ");
		allComments.append(" 			if($('.lc_facemap',context).hasClass('hiddenele')){                                     ");
		allComments.append(" 				$('.lc_facemap',context).removeClass('hiddenele');                                  ");
		allComments.append(" 			}else{                                                                                  ");
		allComments.append(" 				$('.lc_facemap',context).addClass('hiddenele');                                     ");
		allComments.append(" 			}	                                                                                    ");
		allComments.append(" 		});                                                                                         ");
		//提交评论点击事件处理(回复+首发)开始     
		allComments.append(" 		$('#lzzcms_comment').on('click','.submitimg',function(){                                    ");
		allComments.append(" 		    var reply_id='';                                   ");
		allComments.append(" 			var context=$(this).parents('.lc_normalarea');                                          ");
		allComments.append(" 			var inputVal=$('.lc_txtarea',context).val();                                            ");
		//xss前端过滤
		allComments.append(" var reg=/(<)\\s*[^>]+(>)[\\s\\S]*(<)\\s*[^>]+(>)/gim; ");
		allComments.append(" var matchArr; ");
		allComments.append(" var finalStr=''; ");
		allComments.append(" while((matchArr=reg.exec(inputVal))!=null){ ");
		allComments.append(" 	finalStr=inputVal.replace(/</gim,'&lt;'); ");
		allComments.append(" 	finalStr=finalStr.replace(/>/gim,'&gt;'); ");
		allComments.append(" } ");
		allComments.append(" if(finalStr){ ");
		allComments.append(" 	inputVal=finalStr; ");
		allComments.append(" } ");
		allComments.append(" if(!inputVal){ ");
		allComments.append(" 	alert('评论内容不能为空'); ");
		allComments.append(" 	return false; ");
		allComments.append(" }else{ ");
		allComments.append(" 	if(inputVal.length<10){ ");
		allComments.append(" 		alert('评论内容不得少于10个字符'); ");
		allComments.append(" 		return false; ");
		allComments.append(" 	} ");
		allComments.append(" } ");
		allComments.append(" 			if(context.parent('.lc_normalareaparent').length!=0){   ");//首发
		allComments.append(" 				$('.lc_txtarea',context).val('');                                                   ");
		allComments.append(" 				$('.lc_oneitem_building_tpl').find('.lc_comment').html(inputVal);                   ");
		allComments.append(" 				$('#lc_tagstyle').after($('.lc_oneitem_building_tpl').html());                      ");
		allComments.append(" 			}else{ ");//回复           
		//被回复的内容 :可能多个框框 
		allComments.append(" 				var repliedBuilding=context.parent('.lc_oneitem').parent('.lc_onerealitem');        ");
		allComments.append(" 				reply_id=repliedBuilding.attr('data-commentid');       ");
		allComments.append(" 				repliedBuilding.find('.lc_reply').text('回复');                                     ");
		allComments.append(" 				var originalHtml=repliedBuilding.prop('outerHTML');                                 ");
		allComments.append(" 				$('#tmp').html(originalHtml);                                                       ");
		allComments.append(" 				$('#tmp').find('.lc_onerealitem:eq(0)').addClass('borderrect');	                    ");
		allComments.append(" 				$('#tmp').find('.lc_normalarea').remove();                                          ");
		allComments.append(" 				context.remove();                                                                   ");
		//模板 
		allComments.append(" 				$('.lc_oneitem_building_tpl').find('.lc_comment').html($('#tmp').html()+inputVal);  ");
		allComments.append(" 				$('#lc_tagstyle').after($('.lc_oneitem_building_tpl').html());                      ");
		allComments.append(" 			}                                                                                       ");
		//真正加入
		allComments.append(" 			var obj={};                                  ");
		allComments.append(" 			obj['comm_id']=$('#tmp').attr('data-docid'); ");
		allComments.append(" 			obj['comment_cont']=inputVal;            ");
		allComments.append(" 			obj['reply_id']=reply_id;                    ");
		allComments.append(" 			$.ajax({                                     ");
		allComments.append(" 				type:'post',                              ");
		allComments.append(" 				url:'"+getRealBasePath()+"/commentServlet',  ");
		allComments.append(" 				data:obj,                                 ");
		allComments.append(" 				success:function(result){                ");
		allComments.append("					if(result.status=='warning'){  ");
		allComments.append("					   alert(result.info);  ");
		allComments.append("					}  ");
		allComments.append(" 				} ");
		allComments.append(" 			});                                          ");
		allComments.append(" 		});    ");//提交评论点击事件处理(回复+首发)结束
		//回复处理
		allComments.append(" 		$('#lzzcms_comment').on('click','.lc_reply',function(){                                     ");
		allComments.append(" 			var cxt=$(this).parent('.lc_interactive').parent('.lc_oneitem');                        ");
		allComments.append(" 			if('回复'==$(this).text()){                                                             ");
		allComments.append(" 				cxt.append($('.lc_normalareaparent').html());                                       ");
		allComments.append(" 				$(this).text('取消回复');                                                           ");
		allComments.append(" 			}else if('取消回复'==$(this).text()){                                                   ");
		allComments.append(" 				cxt.find('.lc_normalarea').remove();                                                ");
		allComments.append(" 				$(this).text('回复');                                                               ");
		allComments.append(" 			}                                                                                       ");
		allComments.append(" 		});                                                                                         ");
		allComments.append(" </script>");
	}

	private String getBackPrefix() {
		return LzzConstants.getInstance().getBackServletPath();
	}

	private void getStyle(StringBuffer allComments) {
		allComments.append(" <style>                                                                                                                          ");
		allComments.append("  #lzzcms_comment{                                                                                                                ");
		allComments.append("  	margin:0;                                                                                                                     ");
		allComments.append("  	padding:0;                                                                                                                    ");
		allComments.append("  	position: relative;                                                                                                           ");
		allComments.append("  	font-family:'Helvetica Neue',Helvetica,Arial,'Microsoft Yahei UI','Microsoft Yahei',SimHei,'宋体',simsun,sans-serif;    ");
		allComments.append("  	margin-top:45px;                                                                                                                              ");
		allComments.append("  }                                                                                                                               ");
		allComments.append("   div,span,textarea,ul,li,section{                                                                                               ");
		allComments.append("   	margin:0;                                                                                                                     ");
		allComments.append("  	padding:0;                                                                                                                    ");
		allComments.append("  	font-family:'Helvetica Neue',Helvetica,Arial,'Microsoft Yahei UI','Microsoft Yahei',SimHei,'宋体',simsun,sans-serif;     ");
		allComments.append("   }                                                                                                                              ");
		allComments.append("  textarea.lc_txtarea{                                                                                                            ");
		allComments.append("      width: 100%;                                                                                                                ");
		allComments.append("     height: 100px;                                                                                                               ");
		allComments.append("     overflow-x: hidden;                                                                                                          ");
		allComments.append("     overflow-y: auto;                                                                                                            ");
		allComments.append("     border-radius:10px;                                                                                                          ");
		allComments.append(" 	    border:1px solid #447bf3;                                                                                                 ");
		allComments.append(" 	    font-size:13px;                                                                                                           ");
		allComments.append(" 	    outline:none;                                                                                                             ");
		allComments.append("     font-size: 14px;                                                                                                             ");
		allComments.append("     color: #bfbfbf;                                                                                                              ");
		allComments.append("     resize: none;                                                                                                                ");
		allComments.append("     line-height: normal;                                                                                                         ");
		allComments.append("     text-align: left;                                                                                                            ");
		allComments.append("     padding-left: 22px;                                                                                                          ");
		allComments.append("     padding-top: 22px;                                                                                                           ");
		allComments.append("     color:#000;                                                                                                                  ");
		allComments.append("     box-sizing:border-box;                                                                                                       ");
		allComments.append("  }                                                                                                                               ");
		allComments.append("   textarea.lc_txtarea:focus{                                                                                                     ");
		allComments.append(" 	    box-shadow:0 0 4px rgba(102,175,233,.9);                                                                                  ");
		allComments.append(" 	    transition: box-shadow  .5s;                                                                                              ");
		allComments.append(" 	}                                                                                                                             ");
		allComments.append(" .lc_visitor{                                                                                                                     ");
		allComments.append(" 	position: absolute;                                                                                                           ");
		allComments.append(" 	top:-12px;                                                                                                                    ");
		allComments.append(" 	left:55px;                                                                                                                    ");
		allComments.append(" 	background: #fff;                                                                                                             ");
		allComments.append(" 	width:75px;                                                                                                                   ");
		allComments.append(" 	height: 25px;                                                                                                                 ");
		allComments.append(" 	text-align: center;                                                                                                           ");
		allComments.append(" 	display: inline-block;                                                                                                        ");
		allComments.append(" 	color: #447bf3;                                                                                                               ");
		allComments.append(" }	                                                                                                                              ");
		allComments.append(" .lc_radian{                                                                                                                      ");
		allComments.append(" 	position: absolute;                                                                                                           ");
		allComments.append(" 	top:-22px;                                                                                                                    ");
		allComments.append(" 	left:8px;                                                                                                                     ");
		allComments.append(" 	background: #fff;                                                                                                             ");
		allComments.append(" 	width:40px;                                                                                                                   ");
		allComments.append(" 	height: 34px;                                                                                                                 ");
		allComments.append(" 	display: inline-block;                                                                                                        ");
		allComments.append(" 	border-radius:20px;                                                                                                           ");
		allComments.append(" 	border:1px solid #447bf3;                                                                                                     ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_gai{                                                                                                                         ");
		allComments.append(" 	position: absolute;                                                                                                           ");
		allComments.append(" 	top:-22px;                                                                                                                    ");
		allComments.append(" 	left:8px;                                                                                                                     ");
		allComments.append(" 	background: #fff;                                                                                                             ");
		allComments.append(" 	width:42px;                                                                                                                   ");
		allComments.append(" 	height: 22px;                                                                                                                 ");
		allComments.append(" 	z-index: 99;                                                                                                                  ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_visitorimg{                                                                                                                  ");
		allComments.append(" 	position: absolute;                                                                                                           ");
		allComments.append(" 	top:-34px;                                                                                                                    ");
		allComments.append(" 	left:8px;                                                                                                                     ");
		allComments.append(" 	z-index: 999;                                                                                                                 ");
		allComments.append(" 	width:50px;                                                                                                                   ");
		allComments.append(" 	height: 50px;                                                                                                                 ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_visitorimg img{                                                                                                              ");
		allComments.append(" 	width: 42px;                                                                                                                  ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_gray{                                                                                                                        ");
		allComments.append(" 	color:#666 !important;                                                                                                        ");
		allComments.append(" }                                                                                                                                ");
		allComments.append("                                                                                                                                  ");
		allComments.append(" #lc_comment_loadarea{                                                                                                            ");
		allComments.append(" padding-top:45px;                                                                                                                ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_tiptitle_parent{                                                                                                             ");
		allComments.append(" 	border-bottom:1px solid #447bf3;                                                                                              ");
		allComments.append(" 	position: relative;                                                                                                           ");
		allComments.append(" 	height: 30px;                                                                                                                 ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_tiptitle{                                                                                                                    ");
		allComments.append(" 	color:#447bf3;                                                                                                                ");
		allComments.append(" 	border:1px solid #447bf3;                                                                                                     ");
		allComments.append(" 	border-top-left-radius:10px;                                                                                                  ");
		allComments.append(" 	border-top-right-radius:10px;                                                                                                 ");
		allComments.append(" 	text-align: center;                                                                                                           ");
		allComments.append(" 	height: 30px;                                                                                                                 ");
		allComments.append(" 	width:50px;                                                                                                                   ");
		allComments.append(" 	line-height: 30px;                                                                                                            ");
		allComments.append(" 	border-bottom: 0;                                                                                                             ");
		allComments.append(" 	position: absolute;                                                                                                           ");
		allComments.append(" 	top:0px;                                                                                                                      ");
		allComments.append(" 	background: #fff;                                                                                                             ");
		allComments.append(" 	                                                                                                                              ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_clear{                                                                                                                       ");
		allComments.append(" 	clear: both;                                                                                                                  ");
		allComments.append(" 	height: 14px;                                                                                                                 ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" #lc_comment_loadarea #lc_tagstyle{                                                                                               ");
		allComments.append(" 	color:#447bf3;                                                                                                                ");
		allComments.append(" 	padding-left: 10px;                                                                                                           ");
		allComments.append(" 	border-left:3px solid #447bf3;                                                                                                ");
		allComments.append(" 	height:18px;                                                                                                                  ");
		allComments.append(" 	line-height: 18px;                                                                                                            ");
		allComments.append(" 	margin-bottom: 14px;                                                                                                          ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_username{                                                                                                                    ");
		allComments.append(" 	color:#447bf3;                                                                                                                ");
		allComments.append(" 	font-size: 13px;                                                                                                              ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_userlocation{                                                                                                                ");
		allComments.append(" 	font-size: 13px;                                                                                                              ");
		allComments.append(" 	color:#777;                                                                                                                   ");
		allComments.append(" 	margin-left: 5px;                                                                                                             ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_publishdate{                                                                                                                 ");
		allComments.append(" 	font-size: 13px;                                                                                                              ");
		allComments.append(" 	float: right;                                                                                                                 ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_comment{                                                                                                                     ");
		allComments.append(" 	font-size: 14px;                                                                                                              ");
		allComments.append(" 	padding-top: 25px;                                                                                                            ");
		allComments.append(" 	padding-bottom: 25px;                                                                                                         ");
		allComments.append(" 	word-wrap: break-word;                                                                                                        ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_visitorimgshow{                                                                                                              ");
		allComments.append(" 	width: 50px;                                                                                                                  ");
		allComments.append(" 	float: left;                                                                                                                  ");
		allComments.append(" 	padding-right: 10px;                                                                                                          ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_visitorimgshow img{                                                                                                          ");
		allComments.append(" 	width: 42px;                                                                                                                  ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_oneitem{                                                                                                                     ");
		allComments.append(" 	padding-left: 60px;                                                                                                           ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_interactive{                                                                                                                 ");
		allComments.append(" 	padding-bottom: 12px;                                                                                                         ");
		allComments.append(" 	text-align: right;                                                                                                            ");
		allComments.append(" 	color:#777;                                                                                                                   ");
		allComments.append(" 	font-size: 13px;                                                                                                              ");
		allComments.append(" 	cursor: pointer;                                                                                                              ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_reply{ " );
		allComments.append(" 	margin-right:95px; " );
		allComments.append(" } " );
		allComments.append(" .lc_rowsection{                                                                                                                  ");
		allComments.append(" 	padding-top: 10px;                                                                                                            ");
		allComments.append(" 	padding-bottom: 0px;                                                                                                          ");
		allComments.append(" 	position: relative;                                                                                                           ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_biaoqing{                                                                                                                    ");
		allComments.append(" 	background:url("+getBasePath()+"face.png) 0 0;                                                                                                 ");
		allComments.append(" 	width: 18px;                                                                                                                  ");
		allComments.append(" 	height: 18px;                                                                                                                 ");
		allComments.append(" 	display: inline-block;                                                                                                        ");
		allComments.append(" 	cursor: pointer;                                                                                                              ");
		allComments.append(" 	margin-left: 14px;                                                                                                            ");
		allComments.append(" 	float:left;                                                                                                                   ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_biaoqing:hover{                                                                                                              ");
		allComments.append(" 	background:url("+getBasePath()+"face-active.png) 0 0;                                                                                          ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_submitpost{                                                                                                                  ");
		allComments.append(" 	text-align: right;                                                                                                            ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_submitpost img{                                                                                                              ");
		allComments.append(" 	cursor: pointer;	                                                                                                          ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_facemap{                                                                                                                     ");
		allComments.append(" 	background:url("+getBasePath()+"face-bg.png) 0 0;                                                                                              ");
		allComments.append(" 	width:300px;                                                                                                                  ");
		allComments.append(" 	height: 145px;                                                                                                                ");
		allComments.append(" 	position: absolute;                                                                                                           ");
		allComments.append(" 	top:31px;                                                                                                                     ");
		allComments.append(" 	left:0px;                                                                                                                     ");
		allComments.append(" 	z-index: 1000;                                                                                                                ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_face{                                                                                                                        ");
		allComments.append(" 	background: url("+getBasePath()+"face-map.png) no-repeat center;                                                                               ");
		allComments.append(" 	width:22px;                                                                                                                   ");
		allComments.append(" 	height: 22px;                                                                                                                 ");
		allComments.append(" 	display: inline-block;                                                                                                        ");
		allComments.append(" 	margin: 5px;                                                                                                                  ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_paddingtop{                                                                                                                  ");
		allComments.append(" 	margin-top: 5px;                                                                                                              ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_ul{                                                                                                                          ");
		allComments.append(" 	list-style: none;                                                                                                             ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_ul li{                                                                                                                       ");
		allComments.append(" 	border-bottom: 1px dashed #777;                                                                                               ");
		allComments.append(" 	border-right: 1px dashed #777;                                                                                                ");
		allComments.append(" 	display: inline-block;                                                                                                        ");
		allComments.append(" 	width:32px;                                                                                                                   ");
		allComments.append(" 	height: 32px;                                                                                                                 ");
		allComments.append(" 	cursor: pointer;                                                                                                              ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_face01{                                                                                                                      ");
		allComments.append(" 	background-position:0px 0px;                                                                                                  ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_face02{                                                                                                                      ");
		allComments.append(" 	background-position:-2px -22px;                                                                                               ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_face03{                                                                                                                      ");
		allComments.append(" 	background-position:-2px -47px;                                                                                               ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_face04{                                                                                                                      ");
		allComments.append(" 	background-position:-2px -70px;                                                                                               ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_face05{                                                                                                                      ");
		allComments.append(" 	background-position:-2px -95px;                                                                                               ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_face06{                                                                                                                      ");
		allComments.append(" 	background-position:-2px -118px;                                                                                              ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_face07{                                                                                                                      ");
		allComments.append(" 	background-position:-2px -142px;                                                                                              ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_face08{                                                                                                                      ");
		allComments.append(" 	background-position:-2px -166px;                                                                                              ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_face09{                                                                                                                      ");
		allComments.append(" 	background-position:-2px -191px;                                                                                              ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_face10{                                                                                                                      ");
		allComments.append(" 	background-position:-2px -214px;                                                                                              ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_face11{                                                                                                                      ");
		allComments.append(" 	background-position:-2px -237px;                                                                                              ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_face12{                                                                                                                      ");
		allComments.append(" 	background-position:-2px -260px;                                                                                              ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_face13{                                                                                                                      ");
		allComments.append(" 	background-position:-2px -284px;                                                                                              ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_face14{                                                                                                                      ");
		allComments.append(" 	background-position:-2px -308px;                                                                                              ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_face15{                                                                                                                      ");
		allComments.append(" 	background-position:-2px -331px;                                                                                              ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_face16{                                                                                                                      ");
		allComments.append(" 	background-position:-2px -355px;                                                                                              ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_face17{                                                                                                                      ");
		allComments.append(" 	background-position:-2px -378px;                                                                                              ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_face18{                                                                                                                      ");
		allComments.append(" 	background-position:-2px -401px;                                                                                              ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_face19{                                                                                                                      ");
		allComments.append(" 	background-position:-2px -425px;                                                                                              ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_face20{                                                                                                                      ");
		allComments.append(" 	background-position:-2px -445px;                                                                                              ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_face21{                                                                                                                      ");
		allComments.append(" 	background-position:-2px -464px;                                                                                              ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_face22{                                                                                                                      ");
		allComments.append(" 	background-position:-2px -486px;                                                                                              ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_face23{                                                                                                                      ");
		allComments.append(" 	background-position:-2px -510px;                                                                                              ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_face24{                                                                                                                      ");
		allComments.append(" 	background-position:-2px -534px;                                                                                              ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .hiddenele{                                                                                                                      ");
		allComments.append(" 	display: none;                                                                                                                ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .lc_onerealitem{                                                                                                                 ");
		allComments.append(" 	border-bottom: 1px dashed #aaa;                                                                                               ");
		allComments.append(" 	padding:5px;                                                                                                                  ");
		allComments.append(" 	padding-top: 12px;                                                                                                            ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" .borderrect{                                                                                                                     ");
		allComments.append(" 	border:1px solid #ccc !important;                                                                                             ");
		allComments.append(" 	padding: 4px 4px 0px 4px;                                                                                                     ");
		allComments.append(" 	margin-bottom: 8px;                                                                                                           ");
		allComments.append(" }                                                                                                                                ");
		allComments.append(" </style>                                                                                                                         ");
	}

	private void recursionComment(StringBuffer allComments,Map<String, Object> oneCommentMap
			,StringBuffer tmp,Map<String, Map<String, Object>> mapTool){
		Object object=oneCommentMap.get("reply_id");
		if (object!=null) {
			if (tmp==null) {
				tmp=new StringBuffer();
				tmp.append(getOneLcOnerealitem(true, oneCommentMap,false));
			}else{
				String str=tmp.toString().replaceAll("\\[zdwflagzdw\\]",getOneLcOnerealitem( true, oneCommentMap,true));
				tmp.setLength(0);
				tmp.append(str);
			}
			oneCommentMap=mapTool.get(object.toString());
			if (oneCommentMap==null) {//被删除了10--9--7(x)-3-2:10--9--引用已被删除!
				String str = tmp.toString().replaceAll("\\[zdwflagzdw\\]", "引用已被删除!");
				allComments.append(str);
			}else {
				recursionComment(allComments,oneCommentMap,tmp,mapTool);
			}
		}else{
			String oneLcOnerealitem ="";
			if (tmp!=null) {
				oneLcOnerealitem=getOneLcOnerealitem(false, oneCommentMap,true);
				String str=tmp.toString().replaceAll("\\[zdwflagzdw\\]", oneLcOnerealitem);
				allComments.append(str);
			}else {
				oneLcOnerealitem=getOneLcOnerealitem(false, oneCommentMap,false);
				allComments.append(oneLcOnerealitem);
			}
		}
	}
	private String getOneLcOnerealitem(Boolean hasChildren,Map<String, Object> oneCommentMap
			,Boolean hasBorder){
		StringBuffer sb=new StringBuffer();
		if (hasBorder) {
			sb.append(" <section data-commentid="+oneCommentMap.get("id")+" class='lc_onerealitem borderrect'> ");
		}else {
			sb.append(" <section data-commentid="+oneCommentMap.get("id")+" class='lc_onerealitem'> ");
		}
		sb.append(" 	<div class='lc_visitorimgshow'><img src='"+getBasePath()+"user.png'/></div> ");
		sb.append(" 	<div class='lc_oneitem'>  ");
		sb.append(" 		<span class='lc_username'>"+oneCommentMap.get("pub_name")+"</span>  ");
		sb.append(" 		<span class='lc_userlocation'>["+oneCommentMap.get("pub_location")+"]</span> ");
		sb.append(" 		<span class='lc_publishdate lc_gray'>"+oneCommentMap.get("create_time")+"</span>  ");
		if (hasChildren) {
			sb.append(" 		<div class='lc_comment'>"+"[zdwflagzdw]"+oneCommentMap.get("comment_cont")+"</div> ");
		}else {
			sb.append(" 		<div class='lc_comment'>"+oneCommentMap.get("comment_cont")+"</div> ");
		}
		sb.append(" 		<div class='lc_interactive'>                                     ");
		sb.append(" 			<span class='lc_reply'>回复</span>                           ");
		sb.append(" 		</div>                                                           ");
		sb.append(" 	</div>                                                               ");
		sb.append(" </section>                                                               ");
		return sb.toString();
	}
	
}
