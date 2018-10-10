<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="zdw" uri="/lzzcms" %> 
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<link rel="stylesheet" href="<%=request.getContextPath() %>/resources/easyui/themes/default/easyui.css">
<link rel="stylesheet" href="<%=request.getContextPath() %>/resources/easyui/themes/icon.css">
<link href="<%=request.getContextPath() %>/resources/css/reset.css" rel="stylesheet">
<link rel="stylesheet" href="<%=request.getContextPath() %>/resources/css/lzzcmsstyle.css">
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-1.10.2.js"></script>
<script src="<%=request.getContextPath() %>/resources/easyui/jquery.easyui.min.js"></script>
<script src="<%=request.getContextPath() %>/resources/easyui/easyui-lang-zh_CN.js"></script>
<script src="<%=request.getContextPath() %>/resources/js/functions-lzzcms-lib.js"></script>
<style>
body input{
	font-family: Aria;
	font-size: 14px;
	padding-left:7px;
}
	.globalCfg_table{
		margin:0 auto;
		margin-top:20px;
	}
	.globalCfg_table tr{
		 height:35px;
	    line-height:35px;
	    text-align: left;
	}
	.globalCfg_table td,.globalCfg_table th {
		width:230px;
	}
	.globalCfg_table td:nth-child(2),.globalCfg_table th:nth-child(2) {
		width:330px;
	}
	.globalCfg_table input{
		width:200px;
		outline:none;
		font-weight:normal;
		height:30px;
		border-radius:4px;  
	    border:1px solid rgba(102,175,233,.75); 
	}
	.globalCfg_table input:focus{
	    border-color:rgba(102,175,233,.75);  
	    box-shadow:0 0 4px rgba(102,175,233,.5);  
	    transition:border  .2s, box-shadow  .5s;  
	}
	.marginleft{
		margin-left: 5px;
	}
	.tpltr,#delBtnDiv{
		display: none;
	}
</style>
</head>
<body>
<div> 
	 <form action="" method="post" id="globalCfg_form">
	 	<table  class="globalCfg_table" >
	 	  <tr>
		    <th>变量名(不要使用中文)</th>
		    <th>变量值</th>
		  </tr>
		  <tr class="tpltr" data-id="">
		    <td>   
		       <input placeholder="请输入全局变量名"  type="text" name="varname" />
		    </td>
		    <td>   
		    	<input placeholder="请输入全局变量的值" type="text" name="varvalue" />
		    	<a href="#"   class="easyui-linkbutton" onclick="confirmAdd(this)" data-options="width:50,height:30">确定</a>
		    	<a href="#"   class="easyui-linkbutton"  onclick="cancelAdd(this)" data-options="width:50,height:30">取消</a>
		    </td>
		  </tr>
		</table>
		<div id="delBtnDiv">
			<a href="#" class="easyui-linkbutton" onclick="delGlobalCfg(this)" data-options="width:50,height:30">删除</a>
		</div>
		<a href="#" id="globalCfg_add"  class="easyui-linkbutton" data-options="iconCls:'icon-add'" >新增</a>
	 </form>
</div> 
 <script type="text/javascript">
	$("#globalCfg_add").click(function(){
		addRow();
	});
	function addRow(){
		var lastRow=$('.tpltr')[0];
 		var lastRowClone=$(lastRow).clone();
 		 $(lastRowClone).removeClass("tpltr");
 		 $(lastRowClone).addClass("oneglobalitem");
 		 $('.globalCfg_table').append($(lastRowClone));
	}
	$.ajax({
		type:"post",
		url:"getGlobalCfg",
		success:function(result){
			 $.each(result,function(i,obj){
				 addRow();
				 var justRow=$(".globalCfg_table tr.oneglobalitem:last");
				 justRow.find("input[name=varname]").val(obj.paramname);
				 justRow.attr("data-id",obj.id);
				 justRow.find("input[name=varvalue]").val(obj.paramvalue);
				 justRow.find("input").prop("disabled","disabled");
				 justRow.find("a").remove();
				 justRow.find("td:last").append($("#delBtnDiv").html());
			 });
		}
	});
	//确认按钮点击
 	function confirmAdd(obj){
 		var crtTr=$(obj).parents("tr");
 		var nameVal=crtTr.find("input[name=varname]").val();
 		var valueVal=crtTr.find("input[name=varvalue]").val();
 		crtTr.find("input").prop("disabled","disabled");
 		var param={};
 		param["nameVal"]=nameVal;
 		param["valueVal"]=valueVal;
 		$.ajax({
			type:"post",
			url:"trueAddGlobalCfg",
			data:param,
			success:function(result){
				if(result.info=="ok"){
					$.messager.alert("提示","修改成功!","info");
					crtTr.attr("data-id",result.lastInsertId);
					crtTr.find("a").remove();
					crtTr.find("td:last").append($("#delBtnDiv").html());
				}else if(result.info=="error"){
					 $.messager.alert("失败",result.errinfo,"error");
				}
			}
		});
 	}
	//删除
 	function delGlobalCfg(obj){
 		var crtTr=$(obj).parents("tr");
 		var param={};
 		param["lastInsertId"]=crtTr.attr("data-id");
 		$.ajax({
			type:"post",
			url:"delGlobalCfg",
			data:param,
			success:function(result){
				if(result.info=="ok"){
					crtTr.remove();
				}else if(result.info=="error"){
					 $.messager.alert("失败",result.errinfo,"error");
				}
			}
		});
 	}
	function cancelAdd(obj){
 		$(obj).parents("tr").remove();
 	}
</script>
</body>
</html>