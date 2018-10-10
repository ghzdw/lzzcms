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
<script src="<%=request.getContextPath() %>/resources/js/JSON2.js"></script>
<style>
	body{
		position: relative;
	}
	.table{
		margin:0 auto;
		margin-top:20px;
		width:90%;
	}
	.table tr{
		 height:35px;
	    line-height:35px;
	}
	.table td:nth-child(1){
		padding-right:20px;
		text-align: right;
		width:100px;
	}
	.table tr:last-child>td{
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
	section{
		padding:10px;
	}
	.source{
		position: absolute;
		top:0px;
		right:0px;
	}
	.opera{
		padding-bottom:3px;
	}
	.opera span{
		display: inline-block;
		padding-right:8px;
	}
	.author{
		height:240px;
	}
</style>
</head>
<body>
	<section class="author">
		<div class="opera">
		  	<zdw:hasRight url='addAuthor'>
				<span>
					<a href="#" id="addAuthorBtn"  class="easyui-linkbutton" data-options="iconCls:'icon-add'">&nbsp;增加作者</a>
				</span>
			</zdw:hasRight>
			<zdw:hasRight url='deleteAuthor'>
				<span>	
					<a href="#" id="deleteAuthorBtn"  class="easyui-linkbutton" data-options="iconCls:'icon-clear'">&nbsp;删除作者</a>
				</span>	
			</zdw:hasRight>
		</div>
		<div class="easyui-panel" style="height: 100%;border:hidden;"><!-- 解决表格右边没边框 -->
		  <div id="authorGrid"></div>
		 </div>
		  <div id="authorAddWin">
		     <form action="" method="post" id="authorForm">
			 	<table  class="table" >
				  <tr>
				    <td>
				       <label for="authorName" >作者名称:<i class="necessary">*</i></label>
				    </td>
				    <td>   
				       <input type="text"  id="authorName" name="authorName" class="txtinput"/>
				    </td>
				  </tr>
					  <zdw:hasRight url='addAuthor'>
						    <tr>
					   			<td colspan="2">
					   				<a href="#" id="trueAddAuthorBtn"  class="easyui-linkbutton" data-options="iconCls:'icon-ok'" >提交</a>
					   			</td>
						    </tr>
					   </zdw:hasRight>
				   </table>
			 </form>
		 </div>
	</section>
	<section class="source">
	   <div class="opera">
	     <zdw:hasRight url='addSource'>
		     <span>
			 	<a href="#" id="addSourceBtn"  class="easyui-linkbutton" data-options="iconCls:'icon-add'" >&nbsp;增加来源</a>
			 </span>
		 </zdw:hasRight>
		 <zdw:hasRight url='deleteSource'>
			 <span>
				<a href="#" id="deleteSourceBtn"  class="easyui-linkbutton" data-options="iconCls:'icon-clear'" >&nbsp;删除来源</a>
			 </span>
		 </zdw:hasRight>
	  </div>	
	  <div class="easyui-panel" style="height: 100%;border:hidden;"><!-- 解决表格右边没边框 -->
		 <div id="sourceGrid"></div>
	  </div>	 
		  <div id="sourceAddWin">
		     <form action="" method="post" id="sourceForm">
			 	<table  class="table" >
				  <tr>
				    <td>
				       <label for="comeFrom" >来源:<i class="necessary">*</i></label>
				    </td>
				    <td>   
				       <input type="text"  id="comeFrom" name="comeFrom" class="txtinput"/>
				    </td>
				  </tr>
				  <zdw:hasRight url='addSource'>
					    <tr>
				   			<td colspan="2">
				   				<a href="#" id="trueAddSourceBtn"  class="easyui-linkbutton" data-options="iconCls:'icon-ok'" >提交</a>
				   			</td>
					     </tr>
				   </zdw:hasRight>
				   </table>
			 </form>
		 </div>
	</section>
	<section class="defineflag">
	     <div class="opera">
	      <zdw:hasRight url='addDefineFlag'>
		     <span>
				<a href="#" id="addDefineFlagBtn"  class="easyui-linkbutton" data-options="iconCls:'icon-add'" >&nbsp;增加自定义标记</a>
			</span>
		  </zdw:hasRight>
		  <zdw:hasRight url='deleteDefineFlag'>
				<span>
					<a href="#" id="deleteDefineFlagBtn"  class="easyui-linkbutton" data-options="iconCls:'icon-clear'">&nbsp;删除自定义标记</a>
				</span>
		   </zdw:hasRight>
		</div>
		<div class="easyui-panel" style="height: 100%;border:hidden;"><!-- 解决表格右边没边框 -->
		 <div id="defineFlagGrid"></div>
		</div> 
		  <div id="defineFlagAddWin">
		     <form action="" method="post" id="defineFlagForm">
			 	<table  class="table" >
				  <tr>
				    <td>
				       <label for="defineFlag" >标记名称:<i class="necessary">*</i></label>
				    </td>
				    <td>   
				       <input type="text"  id="defineFlag" name="defineFlag" class="txtinput"/>
				    </td>
				  </tr>
				  <tr>
				    <td>
				       <label for="enName" >对应英文简记:<i class="necessary">*</i></label>
				    </td>
				    <td>   
				       <input type="text"  id="enName" name="enName" class="txtinput"/>
				    </td>
				  </tr>
				   <zdw:hasRight url='addDefineFlag'>
					    <tr>
				   			<td colspan="2">
				   				<a href="#" id="trueAddDefineFlagBtn"  class="easyui-linkbutton" data-options="iconCls:'icon-ok'" >提交</a>
				   			</td>
					   </tr>
				   </zdw:hasRight>
				   </table>
			 </form>
		 </div>
	</section>
 </body>
 <script type="text/javascript">
	$("#authorGrid").datagrid({//作者表格初始化
		striped:true,
		rownumbers:true,
		pagination:true,
		pageSize:5,
		pageList:[5],
		height:210,
		width:500,
		method:"post",
	    columns:[[    
			{field:'',checkbox:true,width:40},           
	        {field:'author_name',title:'作者名字',sortable:true,resizable:true,width:300}
	    ]],    
		url:"listAuthors",
		remoteSort:true
	});
	$("#defineFlagGrid").datagrid({//自定义标记表格初始化
		striped:true,
		rownumbers:true,
		pagination:true,
		pageSize:5,
		pageList:[5],
		height:210,
		method:"post",
	    columns:[[    
			{field:'',checkbox:true},           
	        {field:'define_flag',title:'标记名字',halign:'center',sortable:true,resizable:true,width:300},
	        {field:'en_name',title:'标记简记',halign:'center',sortable:true,resizable:true,width:300}
	    ]],    
		url:"listDefineFlags",
		remoteSort:true
	});
	$("#sourceGrid").datagrid({//来源表格初始化
		striped:true,
		rownumbers:true,
		pagination:true,
		pageSize:5,
		pageList:[5],
		height:210,
		width:500,
		method:"post",
	    columns:[[    
			{field:'',checkbox:true},           
	        {field:'come_from',title:'来源',halign:'center',sortable:true,resizable:true,width:300}
	    ]],    
		url:"listSources",
		remoteSort:true
	});
	$("#authorAddWin").window({//作者添加窗口
		title:'添加作者',
		width:600,
		height:210,
		iconCls:'icon-more',
		modal:true,
		collapsible:false,
		minimizable:false,
		closed:true
	});
	$("#defineFlagAddWin").window({//自定义标记添加窗口
		title:'添加自定义标记',
		width:600,
		height:210,
		iconCls:'icon-more',
		modal:true,
		collapsible:false,
		minimizable:false,
		closed:true
	});
	$("#sourceAddWin").window({//来源添加窗口
		title:'添加来源',
		width:600,
		height:210,
		iconCls:'icon-more',
		modal:true,
		collapsible:false,
		minimizable:false,
		closed:true
	});
	$("#trueAddAuthorBtn").click(function(){//新增作者提交
		$.ajax({
			type:"post",
			url:"addAuthor",
			data:{"param":JSON.stringify(str2Json("#authorForm"))},
			beforeSend:function(){
				if(!$("#authorName").val()){
					$.messager.alert("提示","必填项不能为空",'info');
					return false;
				}
			},
			success:function(data){
				$.messager.alert("结果反馈",data.info,data.type);
				if(data.type=="info"){
					$("#authorAddWin").window("close");
					$("#authorGrid").datagrid("reload");
				}
			}
		});
	});
	$("#trueAddDefineFlagBtn").click(function(){//新增自定义标记提交
		$.ajax({
			type:"post",
			url:"addDefineFlag",
			data:{"param":JSON.stringify(str2Json("#defineFlagForm"))},
			beforeSend:function(){
				if(!($("#defineFlag").val()&&$("#enName").val())){
					$.messager.alert("提示","必填项不能为空",'info');
					return false;
				}
			},
			success:function(data){
				$.messager.alert("结果反馈",data.info,data.type);
				if(data.type=="info"){
					$("#defineFlagAddWin").window("close");
					$("#defineFlagGrid").datagrid("reload");
				}
			}
		});
	});
	$("#trueAddSourceBtn").click(function(){//新增作者提交
		$.ajax({
			type:"post",
			url:"addSource",
			data:{"param":JSON.stringify(str2Json("#sourceForm"))},
			beforeSend:function(){
				if(!$("#comeFrom").val()){
					$.messager.alert("提示","必填项不能为空",'info');
					return false;
				}
			},
			success:function(data){
				$.messager.alert("结果反馈",data.info,data.type);
				if(data.type=="info"){
					$("#sourceAddWin").window("close");
					$("#sourceGrid").datagrid("reload");
				}
			}
		});
	});
	//增加作者按钮
	$("#addAuthorBtn").click(function(){
		$("#authorAddWin").window("open");
	});
	//增加自定义标记按钮
	$("#addDefineFlagBtn").click(function(){
		$("#defineFlagAddWin").window("open");
	});
	//增加来源按钮
	$("#addSourceBtn").click(function(){
		$("#sourceAddWin").window("open");
	});
	//删除作者按钮
	$("#deleteAuthorBtn").click(function(){
		var selections=$("#authorGrid").datagrid("getSelections"),ids=[];
		if(selections.length==0){
			$.messager.alert("提示","请先选择要删除的行!","error");
		}else{
			$.messager.confirm('删除确认', '确定要删除所选行吗？', function(r){
				if (r){
					for(var i=0;i<selections.length;i++){
						ids.push(selections[i]["id"]);
					}
				  	var params={};
				    params["param"]=ids.join(",");
					$.ajax({
						type:"post",
						url:"deleteAuthor",
						data:params,
						success:function(result){
							$.messager.alert('结果反馈',result.info,result.type);
							if(result.type=="info"){
								$("#authorGrid").datagrid("reload");
							}
						}
					});
				}
			});
	   }
	});
	//删除自定义标记按钮
	$("#deleteDefineFlagBtn").click(function(){
		var selections=$("#defineFlagGrid").datagrid("getSelections"),ids=[];
		if(selections.length==0){
			$.messager.alert("提示","请先选择要删除的行!","error");
		}else{
			$.messager.confirm('删除确认', '确定要删除所选行吗？', function(r){
				if (r){
					for(var i=0;i<selections.length;i++){
						ids.push(selections[i]["id"]);
					}
				  	var params={};
				    params["param"]=ids.join(",");
					$.ajax({
						type:"post",
						url:"deleteDefineFlag",
						data:params,
						success:function(result){
							$.messager.alert('结果反馈',result.info,result.type);
							if(result.type=="info"){
								$("#defineFlagGrid").datagrid("reload");
							}
						}
					});
				}
			});
	   }
	});
	//删除来源按钮
	$("#deleteSourceBtn").click(function(){
		var selections=$("#sourceGrid").datagrid("getSelections"),ids=[];
		if(selections.length==0){
			$.messager.alert("提示","请先选择要删除的行!","error");
		}else{
			$.messager.confirm('删除确认', '确定要删除所选行吗？', function(r){
				if (r){
					for(var i=0;i<selections.length;i++){
						ids.push(selections[i]["id"]);
					}
				  	var params={};
				    params["param"]=ids.join(",");
					$.ajax({
						type:"post",
						url:"deleteSource",
						data:params,
						success:function(result){
							$.messager.alert('结果反馈',result.info,result.type);
							if(result.type=="info"){
								$("#sourceGrid").datagrid("reload");
							}
						}
					});
				}
			});
	   }
	});
</script>
 </html>