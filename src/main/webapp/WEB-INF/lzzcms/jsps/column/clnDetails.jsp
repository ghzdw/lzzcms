<%@page import="java.util.*"%>
<%@page import="java.util.Map.Entry"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
 <script type="text/javascript">
$(function(){
	var newValue=$("#clnDetails_clntype").val();
	if(newValue=='1'){//封面
		$("#clnDetails_mytpltd,#clnDetails_contentTplNametd").css("display","");
		$("#clnDetails_singleContenttd,#clnDetails_outLinktd").css("display","none");
	}else if(newValue=='2'){//列表
		$("#clnDetails_mytpltd,#clnDetails_contentTplNametd").css("display","");
		$("#clnDetails_singleContenttd,#clnDetails_outLinktd").css("display","none");
	}else if(newValue=='3'){//外部
		$("#clnDetails_outLinktd").css("display","");
		$("#clnDetails_singleContenttd,#clnDetails_mytpltd,#clnDetails_contentTplNametd").css("display","none");
	}else if(newValue=='4'){//单页面
		$("#clnDetails_singleContenttd,#clnDetails_mytpltd").css("display","");
		$("#clnDetails_outLinktd,#clnDetails_mytpltd").css("display","none");
	}else{
		$("#clnDetails_singleContenttd,#clnDetails_outLinktd,#clnDetails_mytpltd,#clnDetails_contentTplNametd").css("display","none");
	}
});
</script>
<style>
	.clnDetails_table{
	    width:100%;
	    padding:0;
	    margin:0;
	    font-size:14px;
	}
	.clnDetails_table table{
		width:100%;
	}
	.clnDetails_table tr{
	    height:25px;
	    line-height:25px;
	}
	.clnDetails_table label{
		width:100px;
	    display: inline-block;
	}
	.clnDetails_table .txetinput{
		width:170px;
		outline:none;
		font-size:14px !important;
		font-weight:normal;
		height:30px;
		border-radius:4px;  
	    border:1px solid rgba(102,175,233,.75); 
	}
	.clnDetails_table .txetinput:hover{
		width:170px;
		outline:none;
		font-size:14px !important;
		font-weight:normal;
		height:30px;
		border-radius:4px;  
	    border:1px solid rgba(102,175,233,.75); 
	}
	.clnDetails_table textarea{
		width:100%;
		height:80px;
		border-radius:4px;  
	    border:1px solid rgba(102,175,233,.75); 
	    font-size:13px;
	    outline:none;   
	}
	.clnDetails_table textarea:hover{
		outline:none;  
	    border-color:rgba(102,175,233,.75);  
	    box-shadow:0 0 4px rgba(102,175,233,.5);  
	    transition:border  .2s, box-shadow  .5s;  
	}
	.clnDetails_table td{
		text-align:left;
		padding:0;
		margin:0;
	}
	#clnDetailsContainer{
		padding:5px 10px ;
	}
</style>
<div id="clnDetailsContainer" > 
		<input type="hidden" value="${requestScope.clnInfo.clnType.id}" id="clnDetails_clntype">	
	 	<table  class="clnDetails_table" >
		  <tr>
		    <td><label>所在模型:</label>${requestScope.clnInfo.channelInfo.channelName }</td>
		   <td><label for="clnDetails_name">栏目名称:</label> ${requestScope.clnInfo.name }</td>
		    <td><label for="clnDetails_htmlDir">html存放目录:</label>${requestScope.clnInfo.htmlDir }</td>
		  </tr>
		  <tr>
		   
		    <td><label for="clnDetails_typename">栏目类别:</label>${requestScope.clnInfo.clnType.typeName}
		    </td>
		      <td><label for="clnDetails_typename">上一级栏目:</label>${requestScope.clnInfo.columnInfo.name}
		    </td>
		  </tr>
		  <tr>
		    <td id="clnDetails_outLinktd"><label for="clnDetails_outLink">外部链接地址:</label>${requestScope.clnInfo.outLink}</td>
		    <td id="clnDetails_mytpltd"><label for="clnDetails_mytpl">默认模版:</label>${requestScope.clnInfo.myTpl}</td>
		    <td id="clnDetails_contentTplNametd"><label for="clnDetails_contentTplName">内容模板:</label>${requestScope.clnInfo.contentTplName}</td>
		  </tr>
		  <tr>
		    <td><label for="clnDetails_clnTitle">页面标题:</label>${requestScope.clnInfo.clnTitle}</td>
		     <td><label for="clnDetails_orderNo">排序优先级:</label>${requestScope.clnInfo.orderNo}</td>
		  </tr>
		  <tr>
		  	<td colspan="3">
			    <table  style="width: 100%;"  cellspacing="0">
			   			<tr>
			   				<td style="width:90px;"> <label for="clnDetails_clnKeyWords">页面关键字:</label></td>
			   				<td style="width:100%;">${requestScope.clnInfo.clnKeyWords}</td>
			   			</tr>
			   	</table>
		    </td>
		  </tr>
		   <tr>
		   	<td colspan="3">
			    <table  style="width: 100%;"  cellspacing="0">
			   			<tr>
			   				<td style="width:90px;"> <label for="clnDetails_clnDesc">页面描述:</label></td>
			   				<td style="width:100%;">${requestScope.clnInfo.clnDesc}</td>
			   			</tr>
			   	</table>
		    </td>
		  </tr>
		  <tr>
		   	<td colspan="3" id="clnDetails_singleContenttd">
			    <table  style="width: 100%;"  cellspacing="0">
			   			<tr>
			   				<td style="width:90px;"> <label for="clnDetails_singleContent">单页面的内容:</label></td>
			   				<td style="width:100%;">${requestScope.clnInfo.singleContent}
			   				</td>
			   			</tr>
			   	</table>
		    </td>
		  </tr>
		</table>
</div> 