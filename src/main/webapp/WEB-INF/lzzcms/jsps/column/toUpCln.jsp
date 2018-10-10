<%@page import="java.util.*"%>
<%@page import="java.util.Map.Entry"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="zdw" uri="/lzzcms" %>   
 <script type="text/javascript">
$(function(){
	showAndNo();
$("#toUpCln_submit").click(function(){
		$('#toUpCln_form').form({    
		    url:'trueUpColumn',   
		    onSubmit:function(){
		    	if(!$.isNumeric($("#toUpCln_orderNo").val())){
		    		$.messager.alert('错误',"排序优先级需要整数!",'error');
		    		return false;
		    	}
		    	$.messager.progress({msg:"正在提交....",title:"请稍等"});
		    },
		    success:function(data){    
		    	$.messager.progress("close");
		        if(data=="success"){
		        	$.messager.alert('提示','提交成功!','info');
		        }else{
		        	$.messager.alert('错误',data,'error');
		        }
		    }    
		});    
		$('#toUpCln_form').submit();  
	});
	$("span.scan").click(function(){
		$("#scanWin").attr("data-for",$(this).find("a").attr("data-for"));
		$("#scanWin").window("open");
	});
	$("#tpls").tree({
		url:"getTpls",
		lines:true,
		onClick: function(node){
			var url=node.attributes.url;
			if(url){
				$("#"+$("#scanWin").attr("data-for")).val(url);
				$("#scanWin").window("close");
			}
		}
	});
});
function toUpCln_hideTip(obj){
	//比较前后的值来动态决定列的显示，暂未找到val发生改变的事件
	var newV=$(obj).children("input").val();
	var oldV=$("#toUpCln_clntype").val();
	$("#toUpCln_clntype").val($(obj).children("input").val());//children只找儿子
	if(newV!=oldV){
		showAndNo();
	}
	$("#toUpCln_typename").val($(obj).children("span").text());
	$("#toUpCln_typename").qtip("hide");
}
function showAndNo(){
	var newValue=$("#toUpCln_clntype").val();
	if(newValue=='1'){//封面
		$("#toUpCln_mytpltd").css("display","");
		$("#toUpCln_singleContenttd,#toUpCln_outLinktd,#toUpCln_contentTplNametd").css("display","none");
	}else if(newValue=='2'){//列表
		$("#toUpCln_mytpltd,#toUpCln_contentTplNametd").css("display","");
		$("#toUpCln_singleContenttd,#toUpCln_outLinktd").css("display","none");
	}else if(newValue=='3'){//外部
		$("#toUpCln_outLinktd").css("display","");
		$("#toUpCln_singleContenttd,#toUpCln_mytpltd,#toUpCln_contentTplNametd").css("display","none");
	}else if(newValue=='4'){//单页面
		$("#toUpCln_singleContenttd,#toUpCln_mytpltd").css("display","");
		$("#toUpCln_outLinktd,#toUpCln_contentTplNametd").css("display","none");
	}else{
		$("#toUpCln_singleContenttd,#toUpCln_outLinktd,#toUpCln_mytpltd,#toUpCln_contentTplNametd").css("display","none");
	}
}
</script>
<style>
	.toUpCln_table{
	    width:100%;
	    padding:0;
	    margin:0;
	    font-size:14px;
	}
	.toUpCln_table table{
		width:100%;
	}
	.toUpCln_table tr{
	    height:25px;
	    line-height:1.5;
	}
	.toUpCln_table label{
		width:100px;
	    display: inline-block;
	}
	.toUpCln_table .txetinput{
		width:170px;
		outline:none;
		font-size:14px !important;
		font-weight:normal;
		height:30px;
		border-radius:4px;  
	    border:1px solid rgba(102,175,233,.75); 
	}
	.toUpCln_table .txetinput:focus{
	    box-shadow:0 0 4px rgba(102,175,233,.5);  
	    transition: box-shadow  .5s;  
	}
	.toUpCln_table textarea{
		width:100%;
		height:80px;
		border-radius:4px;  
	    border:1px solid rgba(102,175,233,.75); 
	    font-size:13px;
	    outline:none;   
	}
	.toUpCln_table textarea:focus{
	    box-shadow:0 0 4px rgba(102,175,233,.5);  
	    transition:box-shadow  .5s;  
	}
	.toUpCln_table td{
		text-align:left;
		padding:0;
		margin:0;
	}
	#toUpClnContainer{
		padding:5px 10px ;
	}
</style>
<div id="toUpClnContainer" > 
	 <form action="" method="post" id="toUpCln_form" enctype="multipart/form-data">
	 	<input type="hidden" name="toUpCln_clnid" value="${requestScope.clnInfo.id }"/>
	 	<table  class="toUpCln_table" >
		  <tr>
		    <td><label>所在模型:</label><input class="txetinput" type="text" name="toUpCln_chlname" id="toUpCln_chlname" value="${requestScope.clnInfo.channelInfo.channelName }" disabled="disabled"/></td>
		   <td><label for="toUpCln_name">栏目名称:</label> <input type="text" class="txetinput"  name="toUpCln_name" id="toUpCln_name" value="${requestScope.clnInfo.name }"/></td>
		    <td><label for="toUpCln_htmlDir">html存放目录:</label><input class="txetinput"  type="text" name="toUpCln_htmlDir" id="toUpCln_htmlDir" value="${requestScope.clnInfo.htmlDir }" /></td>
		  </tr>
		  <tr>
		   
		    <td><label for="toUpCln_typename">栏目类别:</label>
		    	<input type="hidden" name="toUpCln_clntype" class="txetinput"  id="toUpCln_clntype" value="${requestScope.clnInfo.clnType.id}"/>
		    	<input type="text" name="toUpCln_typename" id="toUpCln_typename" class="txetinput"  value="${requestScope.clnInfo.clnType.typeName}"/>
		    </td>
		      <td><label for="toUpCln_typename">上一级栏目:</label>
		    	<input type="hidden" name="toUpCln_parentid" id="toUpCln_parentid"  class="txetinput"  value="${requestScope.clnInfo.columnInfo.id}"/>
		    	<input type="text"  value="${requestScope.clnInfo.columnInfo.name}"  class="txetinput" disabled="disabled"/>
		    </td>
		  </tr>
		  <tr>
		    <td id="toUpCln_outLinktd"><label for="toUpCln_outLink">外部链接地址:</label><input class="txetinput"  type="text" name="toUpCln_outLink"  id="toUpCln_outLink" value="${requestScope.clnInfo.outLink}"/></td>
		    <td id="toUpCln_mytpltd"><label for="toUpCln_mytpl">默认模版:</label>
			    <input class="txetinput"  type="text" name="toUpCln_mytpl" id="toUpCln_mytpl" value="${requestScope.clnInfo.myTpl}"/>
			    <span  class="scan"><a class="easyui-linkbutton"  data-for="toUpCln_mytpl" data-options="iconCls:'icon-search'">浏览...</a></span>
		    </td>
		    <td id="toUpCln_contentTplNametd"><label for="toUpCln_contentTplName">内容模板:</label>
		    	<input  class="txetinput" type="text" name="toUpCln_contentTplName" id="toUpCln_contentTplName" value="${requestScope.clnInfo.contentTplName}"/>
		    	<span class="scan"><a class="easyui-linkbutton"   data-for="toUpCln_contentTplName" data-options="iconCls:'icon-search'">浏览...</a></span>
		    </td>
		  </tr>
		  <tr>
		    <td><label for="toUpCln_clnTitle">页面标题:</label><input type="text" class="txetinput"  name="toUpCln_clnTitle" id="toUpCln_clnTitle" value="${requestScope.clnInfo.clnTitle}"/></td>
		     <td><label for="toUpCln_orderNo">排序优先级:</label><input class="txetinput"  type="text" name="toUpCln_orderNo" id="toUpCln_orderNo" value="${requestScope.clnInfo.orderNo}"/></td>
		  </tr>
		  <tr>
		  	<td colspan="3">
			    <table  style="width: 100%;"  cellspacing="0">
			   			<tr>
			   				<td style="width:90px;"> <label for="toUpCln_clnKeyWords">页面关键字:</label></td>
			   				<td style="width:100%;"><textarea name="toUpCln_clnKeyWords" id="toUpCln_clnKeyWords" class="textareamini">${requestScope.clnInfo.clnKeyWords}</textarea></td>
			   			</tr>
			   	</table>
		    </td>
		  </tr>
		   <tr>
		   	<td colspan="3">
			    <table  style="width: 100%;"  cellspacing="0">
			   			<tr>
			   				<td style="width:90px;"> <label for="toUpCln_clnDesc">页面描述:</label></td>
			   				<td style="width:100%;"><textarea name="toUpCln_clnDesc" id="toUpCln_clnDesc" class="textarea">${requestScope.clnInfo.clnDesc}</textarea></td>
			   			</tr>
			   	</table>
		    </td>
		  </tr>
		  <tr>
		   	<td colspan="3" id="toUpCln_singleContenttd">
			    <table  style="width: 100%;"  cellspacing="0">
			   			<tr>
			   				<td style="width:90px;"> <label for="toUpCln_singleContent">单页面的内容:</label></td>
			   				<td style="width:100%;"><textarea name="toUpCln_singleContent" id="toUpCln_singleContent" class="textarea">${requestScope.clnInfo.singleContent}</textarea>
			   				 <script type="text/javascript">
				   				$(function(){
			   				 		UE.delEditor("toUpCln_singleContent");
									 var ue = UE.getEditor("toUpCln_singleContent",{
								            wordCount:false,
								            elementPathEnabled:false,
								            initialFrameHeight:300
									 });
			   				 	});
							 </script>
			   				</td>
			   			</tr>
			   	</table>
		    </td>
		  </tr>
		</table>
		<zdw:hasRight url="trueAddColumn">
			<div class="divcenter"><a href="#" id="toUpCln_submit"  class="easyui-linkbutton" data-options="iconCls:'icon-ok'" >提交</a></div>
		</zdw:hasRight>
	 </form>
</div> 
<div id="scanWin" class="easyui-window" title="选择模板" style="width:350px;height:300px;overflow-y:auto;padding:10px;background:#fff;"   
        data-options="modal:true,collapsible:false,minimizable:false,closed:true">   
		 <div id="tpls"></div>
</div> 