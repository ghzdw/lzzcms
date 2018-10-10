<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="zdw" uri="/lzzcms" %> 
<script type="text/javascript">
$(function(){
	//初始化添加字段的对话框
	$("#editchl_addFieldDia").dialog({
		title:"添加频道附加表字段",
		closed:true,
		width:700,
		height:400
	});
	loadDataForEditchl_fieldGrid();
	//添加字段对话框提交
	$("#editchl_addFieldDiaButtonCi").click(function(){
		var formData={};
		formData["channelId"]="${channelInfo.id }";
		formData["fieldTip"]= $("#editchl_addFieldDia :input[name=fieldTip]").val();
		formData["fieldName"]= $("#editchl_addFieldDia :input[name=fieldName]").val();
		formData["fieldType"]= $("#editchl_addFieldDia :input[name=fieldType]:checked").val();
		formData["strLength"]= $("#editchl_addFieldDia :input[name=strLength]").val();
		if("varchar"==formData["fieldType"]){
			var reg=/\d{1,}/g;
			if(!reg.test(formData["strLength"])){
				 $.messager.alert("提示","字符串长度必须是正整数","error");
				 return false;
			}
		}
		formData["fieldNull"]= $("#editchl_addFieldDia :input[name=fieldNull]:checked").val();
		formData["fieldDefault"]= $("#editchl_addFieldDia :input[name=fieldDefault]").val();
		formData["additionalTable"]=$("#editchl_additionalTable").val();
		var params={};
		params["formData"]=JSON.stringify(formData);
		$.ajax({
			type:"post",
			url:'addFieldDia',
			data:params,
			success:function(result){
				if(result.info=="ok"){
					$("#addFieldDia").dialog("close");
					$("#editchl_fieldGridwarp").css("display","");
					loadDataForEditchl_fieldGrid();
					$("#editchl_addFieldDia").dialog("close");
				}else if(result.info=="error"){
					 $.messager.alert("提示",result.errinfo,"error");
				}
			}
		});
	});
	//提交频道信息的编辑
	$("#editchl_commit").click(function(){
		var toUrl=$("#editchl_form").attr("action");
		var params={};
		params["id"]=$("#editchl_id").val();
		params["channelName"]=$("#editchl_channelName").val();
		params["commonTable"]=$("#editchl_commonTable").val();
		params["additionalTable"]=$("#editchl_additionalTable").val();
		params["enName"]=$("#editchl_enName").val();
		 $.ajax({
			 type:"post",
			 data:params,
			 url:toUrl,
			 success:function(result){
				 if(result.info=="ok"){
					 $.messager.alert("提示","编辑成功!","info");
				 }else if(result.info=="error"){
					 $.messager.alert("提示",result.errinfo,"error");
				 }
			 }
		 });
	});
	//字段类型点击事件
	$(":input[name='fieldType']").click(function(){
		if($(":input[value='varchar']").is(":checked")){
			$("#strLenTr").css("display","");
		}else{
			$("#strLenTr").css("display","none");
		}
		if($(":input[value='richtext']").is(":checked")){//mysql longtext类型不能有默认值
			$("#defaultValTr").css("display","none");
		}else{
			$("#defaultValTr").css("display","");
		}
	});
});
function openEditChl_addFieldDia(){
	$("#editchl_addFieldDia").dialog("open");
}
function loadDataForEditchl_fieldGrid(){//频道附加表字段表格加载
	$.ajax({
		method:"post",
		url:"getChannelExtralField",	
		data:{"chlid":"${channelInfo.id}"},
		success:function(result){
			if(result.length==1){
				var obj=result[0];
				if(obj.info=="hasNo"){
					$("#tipspan").html("");
					$("#tipspan").append("附加表还没有字段");
					$("#tip").removeClass("hiddenele");
				}else if(obj.info=="error"){
					 $.messager.alert("提示",obj.errinfo,"error");
				}else{//一条记录
					$("#tip").addClass("hiddenele");
					initEditChl_fieldGrid(result);
				}
			}else{
				$("#tip").addClass("hiddenele");
				initEditChl_fieldGrid(result);
			}
		}
	});
}
function initEditChl_fieldGrid(result){
	//初始化频道附加表对应的字段表格
	$("#editchl_fieldGrid").datagrid({
		fitColumns:true,
		columns:[[    
			        {field:'showtip',title:'表单提示文字',halign:'center',sortable:true,resizable:true,width:200},    
			        {field:'colname',title:'表字段',halign:'center',sortable:true,resizable:true,width:200},    
			        {field:'coltype',title:'字段类型',halign:'center',sortable:true,resizable:true,width:200},    
			        {field:'allownull',title:'是否允许为空',halign:'center',sortable:true,resizable:true,width:200},    
			        {field:'defaultval',title:'默认值',halign:'center',sortable:true,resizable:true,width:200}    
			    ]],
				toolbar: [{
					iconCls: 'icon-add',
					text:"增加字段",
					handler: function(){
						openEditChl_addFieldDia();
					}
				}]	    
	});
	$("#editchl_fieldGrid").datagrid("loadData",result);
}
</script>
<style type="text/css">
	.editchl_fieldtb,.editchl_chltb{
	    width:100%;
	    padding:0;
	    margin:0 auto;
	    margin-top:10px;
	}
	.editchl_chltb{
		width:70%;
	}
	.editchl_fieldtb tr,.editchl_chltb tr{
	    height:35px;
	    line-height:35px;
	}
	.editchl_fieldtb label,.editchl_chltb label{
		width:80px;
	    display: inline-block;
	}
	.editchl_fieldtb input[type=text],.editchl_chltb input[type=text]{
		width:170px;
		outline:none;
		font-size:14px !important;
		font-weight:normal;
		height:30px;
		border-radius:4px;  
	    border:1px solid rgba(102,175,233,.75); 
	    outline:none;  
	}
	.editchl_fieldtb input[type=text]:hover,.editchl_chltb input[type=text]:hover{
		outline:none;  
	    border-color:rgba(102,175,233,.75);  
	    box-shadow:0 0 4px rgba(102,175,233,.5);  
	    transition:border  .2s, box-shadow  .5s;  
	}
	.editchl_fieldtb td,.editchl_chltb td{
		text-align:left;
		padding:0;
		margin:0;
	}
	.editchl_fieldtb tr:last-child td:last-child,.editchl_chltb tr:last-child td:last-child{
		text-align:center;
	}
	.editchl_fieldtb td:nth-child(2n+1),.editchl_chltb td:nth-child(2n+1){
		padding-left:30px;
		width:80px;
	}
	.editchl_fieldtb input[type=radio]{
		width:15px;
		height:15px;
		position: absolute;
		top:10px;
	}
	.editchl_fieldtb .radiospan{
		display: inline-block;
		margin-left: 20px;
	}
	.editchl_fieldtb .radiotd{
		position: relative;
	}
	#editchl_addFieldDiaButtonCi{
		cursor:pointer;
		width:50px;
		height:25px;
		background:rgb(242,242,242);
		border-radius:4px;
	}
	input[type=text]{
		padding-left:5px;
	}
	label{
		cursor:pointer;
	}
	.hiddenele{
		display:none;
	}
</style>
<div class="editchl_container">	
   <form method="post"  id="editchl_form"  action="trueEditChannelInfo">
	   	<input  type="hidden" name="editchl_id" id="editchl_id" value="${channelInfo.id }"/>
	   	<input  type="hidden"  value="${channelInfo.commonTable }" name="editchl_commonTable"/>
	   	<input  type="hidden"  value="${channelInfo.additionalTable }" name="editchl_additionalTable"/>
	   <table class="editchl_chltb">
	   		<tr>
	   			<td><label class="control-label">频道ID:</label></td>
	   			<td><input  type="text"  value="${channelInfo.id }" disabled  class="form-control"   /></td>
	   			<td><label  for="editchl_channelName" class="control-label">频道名称：</label></td>
	   			<td><input id="editchl_channelName" type="text"  name="editchl_channelName"  value="${channelInfo.channelName }" /></td>
	   		</tr>
	   		<tr>
	   			<td> <label class="control-label">公用表名：</label></td>
	   			<td><input  type="text"  value="${channelInfo.commonTable }" disabled="disabled"    /></td>
	   			<td> <label  for="editchl_additionalTable">附加表名：</label></td>
	   			<td><input id="editchl_additionalTable" type="text" disabled="disabled"  name="editchl_additionalTable"  value="${channelInfo.additionalTable }" /></td>
	   		</tr>
	   		<tr>
	   			<td><label class="control-label">模版标识：</label></td>
	   			<td><input  type="text"  name="editchl_enName" id="editchl_enName" value="${channelInfo.enName }" /></td>
	   			<zdw:hasRight url="trueEditChannelInfo">
	   				<td colspan="2">
	   				  <button type="button"  id="editchl_commit"  class="easyui-linkbutton" 
	   				    data-options="iconCls:'icon-ok'">提交</button>
	   			    </td>
	   			</zdw:hasRight>
	   		</tr>
	   </table>	
   </form>
  	<div id="tip"  class="hiddenele" >
  	    <span id="tipspan"></span>&nbsp;
  	    <button type="button" class="easyui-linkbutton" data-options="iconCls:'icon-add'" onclick='openEditChl_addFieldDia()' >添加字段</button>
  	</div>
  	<!-- 字段表格 -->
  	<div id="editchl_fieldGrid"></div>
	<div id="editchl_addFieldDia" style="overflow: hidden;">
	  	<form action="" method="post" class="">
		  <table class="editchl_fieldtb" >
	  		<tr>
	  			<td><label for="fieldTip" >表单提示文字:</label></td>
	  			<td><input  name="fieldTip" type="text" /></td>
	  			<td> <label for="fieldName">表字段：</label></td>
	  			<td> <input  name="fieldName" type="text" /></td>
	  		</tr>
	  		<tr>
	  			<td><label>字段类型:</label></td>
	  			<td class="radiotd"><label><input  name="fieldType" type="radio" value="varchar" checked="checked" /><span class="radiospan">字符串类型</span></label></td>
	  			<td class="radiotd"><label> <input  name="fieldType" type="radio" value="int" style="width:20px"/><span class="radiospan">整数</span></label></td>
					<td class="radiotd"><label> <input  name="fieldType" type="radio" value="richtext" style="width:20px"/><span class="radiospan">富文本</span></label></td>
	  		</tr>
	  		<tr id="strLenTr">
	  			<td><label for="strLength">字符串长度:</label></td>
	  			<td colspan="3"><input  name="strLength" type="text" /></td>
	  		</tr>
	  		<tr  >
	  			<td><label>是否允许为空:</label></td>
	  			<td class="radiotd">
	  				<label> <input  name="fieldNull" type="radio" value="yesNull" checked="checked"/><span class="radiospan">是</span></label></td>
	  			    <td colspan="2" class="radiotd"><label><input  name="fieldNull" type="radio" value="noNull"/><span class="radiospan">否</span></label></td>	
	  		</tr>
	  		<tr id="defaultValTr">
	  			<td>
	  				 <label for="fieldDefault">默认值:</label>
	  			</td>
	  			<td><input  name="fieldDefault" type="text" /></td>
	  		</tr>
	  		<zdw:hasRight url="addFieldDia">
		  		<tr><td colspan="4">
		  		 <input type="button"  id="editchl_addFieldDiaButtonCi" value="提交">
		  		 </td>
		  	    </tr>
		   </zdw:hasRight>	    
	 	 </table>	
	  </form>
	</div>
 </div>	
