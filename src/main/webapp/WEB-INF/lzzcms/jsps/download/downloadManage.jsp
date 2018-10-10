<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
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
 <script type="text/javascript">
$(function(){
	$("#gird").datagrid({
		fit:true,
		fitColumns:true,
		striped:true,
		rownumbers:true,
		pagination:true,
		pageSize:20,
		method:"post",
	    columns:[[    
	        {field:'downdate',title:'下载时间',halign:'center',sortable:true,resizable:true,width:150},    
	        {field:'softname',title:'软件名称',halign:'center',sortable:true,resizable:true,width:100},    
	        {field:'ip',title:'ip地址',halign:'center',sortable:true,resizable:true,width:300}
	    ]],    
		url:"listDownloads",
		remoteSort:true
	});
});
</script>
<style>
body{
	position: relative;
}
.disabled{
	cursor: not-allowed;
}
	.fl_table{
		margin:0 auto;
		margin-top:20px;
		width:90%;
	}
	.fl_table tr{
		 height:35px;
	    line-height:35px;
	}
	.fl_table td:nth-child(1){
		padding-right:20px;
		text-align: right;
		width:100px;
	}
	.fl_table tr:last-child>td{
		text-align: center;
	}
	.txtinput{
		width:170px;
		outline:none;
		font-size:14px !important;
		font-weight:normal;
		height:30px;
		border-radius:4px;  
	    border:1px solid rgba(102,175,233,.75); 
	}
	.txtinput:focus{
	    border-color:rgba(102,175,233,.75);  
	    box-shadow:0 0 4px rgba(102,175,233,.5);  
	    transition:border  .2s, box-shadow  .5s;  
	}
	
	.fl_uptable{
		margin:0 auto;
		margin-top:20px;
		width:90%;
	}
	.fl_uptable tr{
		 height:35px;
	    line-height:35px;
	}
	.fl_uptable td:nth-child(1){
		padding-right:20px;
		text-align: right;
		width:100px;
	}
	.fl_uptable tr:last-child>td{
		text-align: center;
	}
	i.necessary{
		color:red;
		font-weight: bold;
		font-size: 17px;
		position: relative;
		top:5px;
	}
	input,select{
		padding-left:7px;
	}
</style>
</head>
<body>
 <div id="gird"></div>
 </body>
 </html>