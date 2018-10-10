<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
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
	$("#adminInfoGrid").adminInfoGrid();
	$("#roleInfoGrid").adminManage_roleInfoGrid();
	$("#assignRolesBtn").click(function(){//分配角色
		var selectedAdmin=$("#adminInfoGrid").datagrid("getSelected");
		var params=new Object();
		params["adminId"]=selectedAdmin.id;
		var selections=$("#roleInfoGrid").datagrid("getSelections");
		var ids="";
		$.each(selections,function(i,n){
			ids+=this.roleid+",";
		});
		ids=ids.substring(0,ids.length-1);
		params["ids"]=ids;
		$.ajax({
			type:"post",
			url:"assignRoles",
			contentType:"application/json", 
			data:JSON.stringify(params),
			success:function(result){
				if(result.info=="ok"){
					$.messager.alert("提示","给用户分配角色成功!","info");
				}else if(result.info=="error"){
					 $.messager.alert("失败",result.errinfo,"error");
				}
			}
		});
	});
	$("#adminManage_addAdminDia").dialog({
		title:"添加管理员",
		closed:true,
		width:500,
		height:300
	});
	
	$("#oldpwdtr,#newpwdtr").addClass("hiddenele");
	$("#toggleuppwdtr").click(function(){
		var imgSrc=$("#toggleuppwdtr").find("img").prop("src");
		if(imgSrc.indexOf("status_down.png")>-1){
			$("#oldpwdtr,#newpwdtr").removeClass("hiddenele");
			$("#toggleuppwdtr").find("img").prop("src","<%=request.getContextPath() %>/resources/imgs/status_up.png")
		}else{
			$("#oldpwdtr,#newpwdtr").addClass("hiddenele");
			$("#toggleuppwdtr").find("img").prop("src","<%=request.getContextPath() %>/resources/imgs/status_down.png")
		}
	});
	$("#adminManage_upDia").dialog({
		title:"修改管理员信息",
		closed:true,
		width:500,
		height:400
	});
	//提交添加管理员
	$("#addcommit").click(function(){
		console.log(JSON.stringify(str2Json("#adminManage_addAdminForm")));
		 $.ajax({
			 type:"post",
			 data:JSON.stringify(str2Json("#adminManage_addAdminForm")),
			 contentType:"application/json",
			 url:"trueAddAdmin",
			 beforeSend:function(){
				var inputs=$("#adminManage_addAdminForm").find("input");
				$.each(inputs,function(i,obj){
					if(!$(obj).val()){
						$.messager.alert("提示","必填项不能为空",'info');
						return false;
					}
				});
				var c=$("input[name=adminManage_confrimpwd]").val();
				var pwd=$("input[name=adminManage_addpwd]").val();
				if(c!=pwd){
					$.messager.alert("提示","密码确认与输入密码不一致",'info');
					return false;
				}
			  },
			 success:function(result){
				 if(result.info=="ok"){
					 $.messager.alert("提示","添加成功!","info");
					 $("#adminManage_addAdminDia").dialog("close");
					 $("#adminInfoGrid").datagrid("reload");
				 }else if(result.info=="error"){
					 $.messager.alert("提示",result.errinfo,"error");
				 }
			 }
		 });
	});
	//提交修改管理员
	$("#adminManage_upsubmit").click(function(){
		 $.ajax({
			 type:"post",
			 data:JSON.stringify(str2Json("#adminManage_upForm")),
			 contentType:"application/json",
			 url:"trueUpAdmin",
			 beforeSend:function(){
				var realName=$("input[name=adminManage_uprealname]").val();
				if(!realName){
					$.messager.alert("提示","真实姓名不能为空",'error');
					return false;
				}
			  },
			 success:function(result){
				 $.messager.alert("结果反馈",result.info,result.type);
				 if(result.type=="info"){
					 $("#adminManage_upDia").dialog("close");
					 $("#adminInfoGrid").datagrid("reload");
				 }
			 }
		 });
	});
	$("#addAdminBtn").click(function(){//增加用户
		$("#adminManage_addAdminDia").dialog("open");
	});	
	$("#deleteAdminBtn").click(function(){//删除用户
		var selections=$("#adminInfoGrid").datagrid("getSelections");
		var ids="";
		if(selections.length==0){
			$.messager.alert("提示","请先选择要删除的行!","error");
		}else{
			$.messager.confirm('确认对话框', '确定要删除所选用户吗？', function(r){
				if (r){
					for(var i=0;i<selections.length;i++){
						ids+=selections[i]["id"]+",";
					}
					ids=ids.substring(0, ids.length-1);
				  	var params={};
				    params["param"]=ids;
					$.ajax({
						type:"post",
						url:"deleteAdmin",
						data:params,
						success:function(result){
							if(result.info=="ok"){
								$.messager.alert("提示","删除成功!","info");
								 $("#adminInfoGrid").datagrid("reload");
							}else if(result.info=="error"){
								 $.messager.alert("失败",result.errinfo,"error");
							}
						}
					});
				}
			});
	   }
	});	
	$("#editAdminBtn").click(function(){//修改用户信息
		var selections=$("#adminInfoGrid").datagrid("getSelections");
		if(selections.length!=1){
			$.messager.alert("提示","选中一行进行修改!","error");
		}else{
			var selected=selections[0];
			$("input[name=adminManage_upid]").val(selected.id);
			$("input[name=adminManage_upidshow]").val(selected.id);
			$("input[name=adminManage_upname]").val(selected.username);
			$("input[name=adminManage_uprealname]").val(selected.realname);
			$("#adminManage_upDia").dialog("open");
		}
	});	
});
</script>
<style>
	#toggleuppwdtr{
		position: absolute;
		top:120px;
		right:17px;
		width:80px;
		background: rgba(0,0,0,.75);
		border-radius:4px;
		cursor: pointer;
		color:#fff;
	}
	#toggleuppwdtr img{
		position:absolute;
		top:10px;
		width:16px;
	}
	#toggleuppwdtr span{
		position:absolute;
		top:1px;
		left:22px;
	}
	
	#adminManage{
		padding:10px 20px 20px 20px;
		position: relative;
	}
	.fr{
		position: absolute;
		top:10px;
		left:720px;
	}
	#adminManage_upDia{
		position: relative;
	}
	
	#adminManage_addAdminDia table,#adminManage_upDia table{
		margin-top:20px;
	}
	#adminManage_addAdminDia tr,#adminManage_upDia tr{
		 height:35px;
	    line-height:35px;
	}
	#adminManage_addAdminDia  td:nth-child(1),#adminManage_upDia td:nth-child(1){
		padding-right:10px;
		text-align: right;
	}
	#adminManage_addAdminDia tr:last-child>td,#adminManage_upDia tr:last-child>td{
		text-align: center;
	}
	#adminManage_addAdminDia input,#adminManage_upDia input{
		width:170px;
		outline:none;
		font-size:14px !important;
		font-weight:normal;
		height:30px;
		border-radius:4px;  
	    border:1px solid rgba(102,175,233,.75); 
	}
	#adminManage_addAdminDia input:hover,#adminManage_upDia input:hover{
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
	#adminName{
		color:red;
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
<div  id="adminManage">
 <section class="fr">
    <div id="roleInfoGrid"></div>
 </section>
 <div class="opera">
    <zdw:hasRight url='trueAddAdmin'>
		<span>
			<a href="#" id="addAdminBtn"  class="easyui-linkbutton" data-options="iconCls:'icon-add'">&nbsp;增加用户</a>
		</span>
	</zdw:hasRight>	
	<zdw:hasRight url='deleteAdmin'>
		<span>	
			<a href="#" id="deleteAdminBtn"  class="easyui-linkbutton" data-options="iconCls:'icon-clear'">&nbsp;删除用户</a>
		</span>	
	</zdw:hasRight>
	<zdw:hasRight url='trueUpAdmin'>
		<span>	
			<a href="#" id="editAdminBtn"  class="easyui-linkbutton" data-options="iconCls:'icon-edit'">&nbsp;修改用户信息</a>
		</span>	
	</zdw:hasRight>	
</div>
   <div id="adminInfoGrid"></div>
    <div class="middle">
	 	<span class="tip">
	 		当前操作的管理员：<span id="adminName"></span>
	 	</span>
	 	<zdw:hasRight url='assignRoles'>
	       <a href="#" id="assignRolesBtn"  class="easyui-linkbutton" data-options="iconCls:'icon-ok'" >
	       	提交已分配的角色
	      </a>
	    </zdw:hasRight>  
	 </div>
	<div id="adminManage_addAdminDia">
		<form action="" method="post" id="adminManage_addAdminForm">
		  <table class="comm_table">
		  	<tr>
		  		<td>
		  			<label>用户名:<i class="necessary">*</i></label>
		  		</td>
		  		<td>
		  			<input  name="adminManage_addUname" type="text"/>
		  		</td>
		  	</tr>
		  	<tr>
		  		<td>
		  			<label>真实姓名：<i class="necessary">*</i></label>
		  		</td>
		  		<td>
		  			<input  name="adminManage_addrealname" type="text"/>
		  		</td>
		  	</tr>
		  	<tr>
		  		<td>
		  			<label>密码:<i class="necessary">*</i></label>
		  		</td>
		  		<td>
		  			<input  name="adminManage_addpwd" type="password"/>
		  		</td>
		  	</tr>
		  	<tr>
		  		<td>
		  			<label>确认密码:<i class="necessary">*</i></label>
		  		</td>
		  		<td>
		  			<input  name="adminManage_confrimpwd" type="password"/>
		  		</td>
		  	</tr>
		  	<zdw:hasRight url='trueAddAdmin'>
		  	  	<tr>
			  		<td colspan="2">
			  			<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-ok'" id="addcommit">
			  				提交
			  			</a>
			  		</td>
			  	</tr>
		  	</zdw:hasRight>
		  </table>	
		</form>
	</div> 
	<div id="adminManage_upDia">
		<form action="" method="post" id="adminManage_upForm">
		  <table class="comm_table">
		  	<tr>
		  		<td>
		  			<label>编号:</label>
		  		</td>
		  		<td>
		  			<input  name="adminManage_upid" type="hidden" />
		  			<input  name="adminManage_upidshow" type="text" disabled="disabled"/>
		  		</td>
		  	</tr>
		  	<tr>
		  		<td>
		  			<label>用户名:</label>
		  		</td>
		  		<td>
		  			<input  name="adminManage_upname" type="text" disabled="disabled"/>
		  		</td>
		  	</tr>
		  	<tr>
		  		<td>
		  			<label>真实姓名：<i class="necessary">*</i></label>
		  		</td>
		  		<td>
		  			<input  name="adminManage_uprealname" type="text" />
		  		</td>
		  	</tr>
		  	<tr id="toggleuppwdtr">
		  		<td colspan="2">
		  		  <img src="<%=request.getContextPath() %>/resources/imgs/status_down.png"> 
		  			<span>修改密码</span>
		  		</td>
		  	</tr>
		  	<tr id="oldpwdtr">
		  		<td>
		  			<label>原始密码:</label>
		  		</td>
		  		<td>
		  			<input  name="adminManage_upoldpwd" type="password"/>
		  		</td>
		  	</tr>
		  	<tr id="newpwdtr">
		  		<td>
		  			<label>新密码:</label>
		  		</td>
		  		<td>
		  			<input  name="adminManage_upnewpwd" type="password"/>
		  		</td>
		  	</tr>
		  	<zdw:hasRight url='trueUpAdmin'>
		  	  	<tr>
			  		<td colspan="2">
			  			<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-ok'" id="adminManage_upsubmit">
			  				提交
			  			</a>
			  		</td>
			  	</tr>
		  	</zdw:hasRight>
		  </table>	
		</form>
	</div>
 </div>
 </body>
 </html>
