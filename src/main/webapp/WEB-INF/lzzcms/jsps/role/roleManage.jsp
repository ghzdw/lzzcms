<%@ page language="java" contentType="text/html; charset=utf-8"  pageEncoding="utf-8"%>
<%@ taglib prefix="zdw" uri="/lzzcms" %> 
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<link rel="stylesheet" href="<%=request.getContextPath() %>/resources/easyui/themes/default/easyui.css">
<link rel="stylesheet" href="<%=request.getContextPath() %>/resources/easyui/themes/icon.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/scrollerbar/jquery.mCustomScrollbar.min.css">
<link href="<%=request.getContextPath() %>/resources/css/reset.css" rel="stylesheet">
<link rel="stylesheet" href="<%=request.getContextPath() %>/resources/css/lzzcmsstyle.css">
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-1.10.2.js"></script>
<script src="<%=request.getContextPath() %>/resources/easyui/jquery.easyui.min.js"></script>
<script src="<%=request.getContextPath() %>/resources/easyui/easyui-lang-zh_CN.js"></script>
<script src="<%=request.getContextPath() %>/resources/js/functions-lzzcms-lib.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/scrollerbar/jquery.mCustomScrollbar.concat.min.js"></script>
<script src="<%=request.getContextPath() %>/resources/js/plugins-lzzcms-lib.js"></script>
<script type="text/javascript">
$(function(){
	$("#roleManage_roleGrid").roleManage_roleGrid();
	$("#rightTree").tree({
		url:"allRights",
		lines:true,
		checkbox:true,
		cascadeCheck:true
	});
	$("#assginRightsBtn").click(function(){
		var selected=$("#roleManage_roleGrid").datagrid("getSelected");
		var params=new Object();
		params["roleId"]=selected.roleid;
		var nodes=$("#rightTree").tree("getChecked",['checked','indeterminate']);
		var ids="";
		$.each(nodes,function(i,n){
			//if($("#rightTree").tree("isLeaf",this.target))
			ids+=this.id+",";
		});
		ids=ids.substring(0,ids.length-1);
		params["ids"]=ids;
		$.ajax({
			type:"post",
			url:"assignRights",
			contentType:"application/json", 
			data:JSON.stringify(params),
			success:function(result){
				if(result.info=="ok"){
					$.messager.alert("提示","给角色分配权限成功!","info");
				}else if(result.info=="error"){
					 $.messager.alert("提示",result.errinfo,"error");
				}
			}
		});
	});
	 $(".fr").mCustomScrollbar({
		 theme:"dark-thin"
	 });
	 $("#addRoleWin").window({
		title:'添加角色',
		width:600,
		height:210,
		iconCls:'icon-more',
		modal:true,
		collapsible:false,
		minimizable:false,
		closed:true
	});
	 $("#addBtn").click(function(){//新增提交
			$.ajax({
				type:"post",
				url:"trueAddRole",
				data:str2Json("#form"),
				beforeSend:function(){
					if(!$("input[name=roleName]").val()){
						$.messager.alert("提示","必填项不能为空",'info');
						return false;
					}
				},
				success:function(data){
					$.messager.alert("结果反馈",data.info,data.type);
					if(data.type=="info"){
						$("#addRoleWin").window("close");
						$("#roleManage_roleGrid").datagrid("reload");
					}
				}
			});
		});
	 $("#addRoleBtn").click(function(){//增加角色
		 $("#addRoleWin").window("open");
     });	
	 $("#deleteRoleBtn").click(function(){//删除角色
		 var selections=$("#roleManage_roleGrid").datagrid("getSelections");
			var ids=[];
			if(selections.length==0){
				$.messager.alert("提示","请先选择要删除的行!","error");
			}else{
				$.messager.confirm('确认对话框', '角色被删除后,如果已经分配给用户,则该用户的该角色将也被删除，确定要删除吗？', function(r){
					if (r){
						for(var i=0;i<selections.length;i++){
							ids.push(selections[i]["roleid"]);
						}
					  	var params={};
					    params["param"]=ids.join(",");
						$.ajax({
							type:"post",
							url:"deleteRoleById",
							data:params,
							success:function(data){
								$.messager.alert("结果反馈",data.info,data.type);
								if(data.type=="info") $("#roleManage_roleGrid").datagrid("reload");
							}
						});
					}
				});//confirm
		   }
	 });	
});
</script>
<style>
	#roleManage{
		padding:10px 20px 20px 20px;
		position: relative;
	}
	.fr{
		position: absolute;
		top:10px;
		left:800px;
		height: 420px;
		overflow: hidden;
		min-width: 250px;
	}
	.middle{
		position: absolute;
		top:200px;
		left:485px;
		width:200px;
		text-align: center;
	}
	.tip{
		display: block;
		padding:10px;
		padding-left:20px;
		text-align: left;
	}
	#roleName{
		color:red;
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
	    padding-left:7px;
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
	.opera{
		padding-bottom:3px;
	}
	.opera span{
		display: inline-block;
		padding-right:8px;
	}
</style>
</head>
<body>
<div id="roleManage">
	 <section class="fr">
		 <ul  id="rightTree"></ul>  
	 </section>
	 <div class="opera">
	   <zdw:hasRight url='trueAddRole'>
		<span>
			<a href="#" id="addRoleBtn"  class="easyui-linkbutton" data-options="iconCls:'icon-add'">&nbsp;增加角色</a>
		</span>
	   </zdw:hasRight>	
	   <zdw:hasRight url='deleteRoleById'>
			<span>	
				<a href="#" id="deleteRoleBtn"  class="easyui-linkbutton" data-options="iconCls:'icon-clear'">&nbsp;删除角色</a>
			</span>	
	   </zdw:hasRight>	
	</div>
	 <div id="roleManage_roleGrid"></div>
	 <div class="middle">
	 	<span class="tip">
	 		当前操作的角色：<span id="roleName"></span>
	 	</span>
	 	<zdw:hasRight url='assignRights'>
	       <a href="#" id="assginRightsBtn" class="easyui-linkbutton" data-options="iconCls:'icon-ok'">
	       	   提交已分配的权限
	       </a>
       </zdw:hasRight>
	 </div>
	 
	 <div id="addRoleWin">
     <form action="" method="post" id="form" >
	 	<table  class="table" >
		  <tr>
		    <td>
		       <label for="roleName" >角色名称:<i class="necessary">*</i></label>
		    </td>
		    <td>   
		       <input type="text"  name="roleName" class="txtinput"/>
		    </td>
		  </tr>
		  <tr>  
		     <td>
		       <label for="roleDesc">角色详细描述:</label>
		        </td>
		    <td>
		       <input type="text"  style="width:300px;" name="roleDesc" class="txtinput"/>
		    </td>
		  </tr>  
		  <zdw:hasRight url='trueAddRole'>
			  <tr>
	   			<td colspan="2">
	   				<a href="#" id="addBtn"  class="easyui-linkbutton" 
	   				data-options="iconCls:'icon-ok'" >提交</a>
	   			</td>
			   </tr>
		   </zdw:hasRight>
		   </table>
	 </form>
 </div>
	 
 </div>
 </body>
 </html>