<%@page import="java.util.*"%>
<%@page import="java.util.Map.Entry"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="zdw" uri="/lzzcms" %>    
 <script type="text/javascript">
$(function(){
	$("#toAddCln_typename").combobox({//栏目类型下拉框
		    url:'getClnTypeForCombobox',    
		    valueField:'id',    
		    textField:'typeName',
		    editable:false,
		    value:"0",
		    height:30,
		    onChange:function(newValue, oldValue){
		    			$("#changetr").css("display","");
		    		if(newValue=='1'){//封面:默认模板  
		    			$("#toAddCln_mytpltd").css("display","");
		    			$("#toAddCln_singleContenttd,#toAddCln_outLinktd,#toAddCln_contentTplNametd").css("display","none");
		    			$("#toAddCln_mytpltd input").val("cover_${requestScope.clnInfo.channelInfo.enName}.html");
		    		}else if(newValue=='2'){//列表： 默认模板   内容模板
		    			$("#toAddCln_mytpltd,#toAddCln_contentTplNametd").css("display","");
		    			$("#toAddCln_singleContenttd,#toAddCln_outLinktd").css("display","none");
		    			$("#toAddCln_mytpltd input").val("list_${requestScope.clnInfo.channelInfo.enName}.html");
		    			$("#toAddCln_contentTplNametd input").val("content_${requestScope.clnInfo.channelInfo.enName}.html");
		    		}else if(newValue=='3'){//外部 ： 外部链接
		    			$("#toAddCln_outLinktd").css("display","");
		    			$("#toAddCln_singleContenttd,#toAddCln_mytpltd,#toAddCln_contentTplNametd").css("display","none");
		    		}else if(newValue=='4'){//单页面 ： 默认模板   单页面的内容
		    			$("#toAddCln_singleContenttd,#toAddCln_mytpltd").css("display","");
		    			$("#toAddCln_outLinktd,#toAddCln_contentTplNametd").css("display","none");
		    			$("#toAddCln_mytpltd input").val("single_page.html");
		    		}else{
		    			$("#toAddCln_singleContenttd,#changetr").css("display","none");
		    		}
		    }
	});
	$("#toAddCln_submit").click(function(){
		$('#toAddCln_form').form({    
		    url:'trueAddColumn',   
		    onSubmit:function(){
		    	if(!gt0Int($("#toAddCln_orderNo").val())){
		    		$.messager.alert('错误',"排序优先级需要正整数!",'error');
		    		return false;
		    	}
		    	if($("#toAddCln_typename").combobox("getValue")==0){
		    		$.messager.alert('错误',"请先选择栏目类别!",'error');
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
		$('#toAddCln_form').submit();  
	});
	$("#toAddCln_orderNo").val(randomInt());
	//监听栏目名称失去焦点生成拼音首字母dir
	$("#toAddCln_name").blur(function() {
		var clnTitle = $("#toAddCln_name").val();
		if (clnTitle) {
			$.post('getDirByTopClnName',{'clnName' : clnTitle},function(data) {
					if (data.info == "error") {
						$.messager.alert('错误',data.errinfo,'error');
					} else if (data.info = "ok") {
						$("#toAddCln_htmlDir").val(data.dirName);
					}
				});
		}
	});
	//模板选择对话框
	$("#toAddCln_contentTplName,#toAddCln_mytpl").click(function(e){
		$("#scanWin").attr("data-for",$(this).prop("id"));
		$("#scanWin").window({
			left:e.pageX,
			top:e.pageY
		});
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
</script>
<style>
	.toAddCln_table{
	    width:100%;
	    padding:0;
	    margin:0;
	}
	.toAddCln_table table{
		width:100%;
	}
	.toAddCln_table tr{
	    height:35px;
	    line-height:35px;
	}
	.toAddCln_table label{
		width:90px;
	    display: inline-block;
	}
	.toAddCln_table .txetinput{
		width:170px;
		outline:none;
		font-size:14px !important;
		font-weight:normal;
		height:30px;
		border-radius:4px;  
	    border:1px solid rgba(102,175,233,.75); 
	}
	.toAddCln_table .txetinput:hover{
	    border-color:rgba(102,175,233,.75);  
	    box-shadow:0 0 4px rgba(102,175,233,.5);  
	    transition:border  .2s, box-shadow  .5s;  
	}
	.toAddCln_table textarea{
		width:100%;
		height:80px;
		border-radius:4px;  
	    border:1px solid rgba(102,175,233,.75); 
	    font-size:13px;
	    outline:none;   
	}
	.toAddCln_table textarea:hover{
		outline:none;  
	    border-color:rgba(102,175,233,.75);  
	    box-shadow:0 0 4px rgba(102,175,233,.5);  
	    transition:border  .2s, box-shadow  .5s;  
	}
	.toAddCln_table td{
		text-align:left;
		padding:0;
		margin:0;
	}
	#toAddClnContainer{
		padding:5px 10px ;
	}
</style>
<div id="toAddClnContainer" > 
	 <form action="" method="post" id="toAddCln_form" enctype="multipart/form-data">
	 	<input type="hidden" name="toAddCln_chlid" value="${requestScope.clnInfo.channelInfo.id}"/>
	 	<input type="hidden" name="toAddCln_parentid" value="${requestScope.clnInfo.id }"/>
	 	<input type="hidden" name="toAddCln_parentdir" value="${requestScope.clnInfo.htmlDir}/"/>
	 	<table  class="toAddCln_table" >
		  <tr>
		    <td><label>所在模型:</label></td>
		    <td>${requestScope.clnInfo.channelInfo.channelName }</td>
		    <td><label>父级栏目:</label></td>
		    <td>${requestScope.clnInfo.name }</td>
		   <td><label for="toAddCln_name">栏目名称:</label></td>
		   <td><input type="text" name="toAddCln_name" class="txetinput" id="toAddCln_name"/></td>
		  </tr>
		  <tr>
		    <td><label for="toAddCln_typename">html父级目录:</label></td>
		    <td>${requestScope.clnInfo.htmlDir}/</td>
		    <td><label for="toAddCln_htmlDir">html存放目录:</label></td>
		    <td><input type="text" name="toAddCln_htmlDir"  class="txetinput"  id="toAddCln_htmlDir" /></td>
		    <td><label for="toAddCln_typename">栏目类别:</label></td>
		    <td><input type="text" name="toAddCln_typename" class="txetinput"  id="toAddCln_typename"/></td>
		  </tr>
		  <tr id="changetr" style="display:none">
		  	<td colspan="2" id="toAddCln_outLinktd">
		  		<table>
		  			<tr>
		  				<td ><label for="toAddCln_outLink">外部链接地址:</label></td>
		   				 <td><input type="text" name="toAddCln_outLink"  id="toAddCln_outLink"  class="txetinput" /></td>
		  			</tr>
		  		</table>
		  	</td>
  		    <td colspan="2" id="toAddCln_mytpltd">
		  		<table>
		  			<tr>
		  			   <td><label for="toAddCln_mytpl">默认模版:</label></td>
		 			   <td><input type="text" name="toAddCln_mytpl" id="toAddCln_mytpl" class="txetinput" /></td>
		  			</tr>
		  		</table>
		  	</td>
		  	<td colspan="2" id="toAddCln_contentTplNametd">
		  		<table>
		  			<tr>
		  				 <td><label for="toAddCln_contentTplName">内容模板:</label></td>
		 				   <td><input type="text" name="toAddCln_contentTplName" id="toAddCln_contentTplName"  class="txetinput" /></td>
		  			</tr>
		  		</table>
		  	</td>
		  </tr>
		  <tr>
		    <td><label for="toAddCln_clnTitle">页面标题:</label></td>
		    <td><input type="text" name="toAddCln_clnTitle"  class="txetinput" id="toAddCln_clnTitle"/></td>
		     <td><label for="toAddCln_orderNo">排序优先级:</label></td>
		     <td><input type="text" name="toAddCln_orderNo"  class="txetinput" id="toAddCln_orderNo"/></td>
		  </tr>
		  <tr>
		  	<td colspan="6">
			    <table  style="width: 100%;"  cellspacing="0">
			   			<tr>
			   				<td style="width:90px;"> <label for="toAddCln_clnKeyWords">页面关键字:</label></td>
			   				<td style="width:100%;height: 50px;">
			   				<textarea style="height:50px;" name="toAddCln_clnKeyWords" id="toAddCln_clnKeyWords" class="textareamini"></textarea>
			   				</td>
			   			</tr>
			   	</table>
		    </td>
		  </tr>
		   <tr>
		   	<td colspan="6">
			    <table  style="width: 100%;"  cellspacing="0">
			   			<tr>
			   				<td style="width:90px;"> <label for="toAddCln_clnDesc">页面描述:</label></td>
			   				<td style="width:100%;"><textarea name="toAddCln_clnDesc" id="toAddCln_clnDesc" class="textarea"></textarea></td>
			   			</tr>
			   	</table>
		    </td>
		  </tr>
		  <tr>
		   	<td colspan="6" id="toAddCln_singleContenttd" style="display: none;">
			    <table  style="width: 100%;"  cellspacing="0">
			   			<tr>
			   				<td style="width:90px;"> <label for="toAddCln_singleContent">单页面的内容:</label></td>
			   				<td style="width:100%;"><textarea name="toAddCln_singleContent" id="toAddCln_singleContent" class="textarea"></textarea>
			   				 <script type="text/javascript">
								
							 </script>
			   				</td>
			   			</tr>
			   	</table>
		    </td>
		  </tr>
		</table>
		<zdw:hasRight url="trueAddColumn">
			<div class="divcenter"><a href="#" id="toAddCln_submit"  class="easyui-linkbutton" data-options="iconCls:'icon-ok'" >提交</a></div>
		</zdw:hasRight>
	 </form>
	 <!-- 浏览模板选择框 -->
		<div id="scanWin" class="easyui-window" title="选择模板" style="width:350px;height:300px;overflow-y:auto;padding:10px;background:#fff;"   
	        data-options="modal:true,collapsible:false,minimizable:false,closed:true">   
			 <div id="tpls"></div>
		</div> 
</div> 