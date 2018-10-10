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
 <script type="text/javascript">
$(function(){
	$("#flManage_flGrid").datagrid({
		height:500,
		fitColumns:true,
		striped:true,
		rownumbers:true,
		pagination:true,
		pageList:[10,20,30],
		pageSize:10,
		method:"post",
	    columns:[[    
			{field:'',checkbox:true},           
	        {field:'linkDesc',title:'链接名称或图片',halign:'center',sortable:true,resizable:true,width:150},    
	        {field:'type',title:'链接类型',formatter:function(value,row,index){
	        	if(value==1)return   "文字";
	        	else if(value==2) return "图片";
	        },halign:'center',sortable:true,resizable:true,width:100},    
	        {field:'url',title:'地址',halign:'center',sortable:true,resizable:true,width:300}
	    ]],    
		url:"listFls",
		remoteSort:true
	});
	$("#fl_addwin").window({
		title:'添加友情链接',
		width:600,
		height:210,
		iconCls:'icon-more',
		modal:true,
		collapsible:false,
		minimizable:false,
		closed:true
	});
	$("#fl_upwin").window({
		title:'修改友情链接',
		width:600,
		height:210,
		iconCls:'icon-more',
		modal:true,
		collapsible:false,
		minimizable:false,
		closed:true
	});
	$("#fl_type").combobox({    //链接类型
	    valueField:'id',    
	    editable:false,
	    height:30,
	    width:170,
	    onLoadSuccess:function(){
	    	$("#fl_linkname").removeClass("hiddenele");
    		$("#fl_linknameimg").addClass("hiddenele");
	    },
	    onChange:function( newValue, oldValue ){
	    	if("1"==newValue){//文字
	    		$("#fl_linkname").removeClass("hiddenele");
	    		$("#fl_linknameimg").addClass("hiddenele");
	    	}else if("2"==newValue){//图片
	    		$("#fl_linkname").addClass("hiddenele");
	    		$("#fl_linknameimg").removeClass("hiddenele");
	    	}
	    }
	});  
	//增加提交
	$("#fl_submit").click(function(){
		$('#fl_form').form({    
		    url:'trueAddFl',   
		    onSubmit:function(param){
		    	var linkType=$("#fl_type").combobox("getValue");
				if("1"==linkType){
					if(!($("#fl_linkname").val()&&$("#fl_url").val())){
						$.messager.alert("提示","必填项不能为空",'info');
						return false;
					}
				}else{
					if(!($("#fl_linknameimg").val()&&$("#fl_url").val())){
						$.messager.alert("提示","必填项不能为空",'info');
						return false;
					}
				}
		    	$.messager.progress({msg:"正在提交....",title:"请稍等"});
		    },
		    success:function(data){//返回的是字符串
		    	data=$.parseJSON(data);
		    	$.messager.progress("close");
		    	$.messager.alert("结果反馈",data["info"],data["type"]);
		    	if(data["type"]=="info"){
					$("#fl_addwin").window("close");
					$("#flManage_flGrid").datagrid("reload");
					$("#fl_form").form("reset");
				}
		    }    
		});    
		$('#fl_form').submit(); 
	});
	$("#fl_upsubmit").click(function(){//修改提交
		$.ajax({
			type:"post",
			url:"trueUpdateFl",
			data:str2Json("#fl_upform"),
			beforeSend:function(){
				if(!$("#fl_upurl").val()){
					$.messager.alert("提示","必填项不能为空",'info');
					return false;
				}
			},
			success:function(data){
				$.messager.alert("结果反馈",data.info,data.type);
				if(data.type=="info"){
					$("#fl_upwin").window("close");
					$("#flManage_flGrid").datagrid("reload");
				}
			}
		});
	});
	//以原尺寸显示图片的div默认隐藏
	$(".imgshow").addClass("hiddenele");
	//图片放大显示
	$("body").on("mouseover mouseout",".datagrid-row img,#fl_uplinkname img",function(e){
		e.stopPropagation();
		if(e.type=="mouseover"){
			$(".imgshow").html($(this).clone());
			$(".imgshow").css({
				top:e.pageY+"px",
				left:e.pageX+"px"
			});
			$(".imgshow").removeClass("hiddenele");
		}else{
			$(".imgshow").addClass("hiddenele");
		}
	});
	$("#addFlBtn").click(function(){//增加友情链接
		$("#fl_addwin").window("open");
	});	
	$("#editFlBtn").click(function(){//编辑友情链接
		var selections=$("#flManage_flGrid").datagrid("getSelections");
		if(selections.length==0||selections.length!=1){
			$.messager.alert("提示","请先选择要编辑的行且一次只能编辑一条数据!","error");
		}else{
			var one =selections[0];
			$("#fl_upid").val(one["id"]);
			$("#fl_uplinkname").html(one["linkDesc"]);
			$("#fl_uptype").val(one["type"]=="1"?"文字":"图片");
			$("#fl_upurl").val(one["url"]);
			$("#fl_upwin").window("open");
	    }
	});	
	$("#deleteFlBtn").click(function(){//删除友情链接
		var selections=$("#flManage_flGrid").datagrid("getSelections");
		var ids=[];
		if(selections.length==0){
			$.messager.alert("提示","请先选择要删除的行!","error");
		}else{
			$.messager.confirm('确认对话框', '确定要删除该行吗？', function(r){
				if (r){
					for(var i=0;i<selections.length;i++){
						ids.push(selections[i]["id"]);
					}
				  	var params={};
				    params["param"]=ids.join(",");
					$.ajax({
						type:"post",
						url:"deleteFl",
						data:params,
						success:function(result){
							if(result.status=="sucess"){
								$("#flManage_flGrid").datagrid("reload");
							}else{
					        	$.messager.alert('错误',result.info,'error');
					        }
						}
					});
				}
			});
	   }
	});	
});
</script>
<style>
body{
	position: relative;
}
.imgshow{
	position: absolute;
	z-index: 9999;
}
.imgshow img{
	width:300px;
}
.datagrid-row img,#fl_uplinkname img{
	height: 30px;
	vertical-align: middle;
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
	<div class="imgshow"></div>
 	<div class="opera">
 	    <zdw:hasRight url='trueAddFl'>
			<span>
				<a href="#" id="addFlBtn"  class="easyui-linkbutton" data-options="iconCls:'icon-add'">&nbsp;增加友情链接</a>
			</span>
		</zdw:hasRight>
		 <zdw:hasRight url='trueUpdateFl'>
			<span>	
				<a href="#" id="editFlBtn"  class="easyui-linkbutton" data-options="iconCls:'icon-edit'">&nbsp;编辑友情链接</a>
			</span>	
		</zdw:hasRight>
		 <zdw:hasRight url='deleteFl'>
			<span>	
				<a href="#" id="deleteFlBtn"  class="easyui-linkbutton" data-options="iconCls:'icon-clear'">&nbsp;删除友情链接</a>
			</span>	
		</zdw:hasRight>
	</div>
 <div id="flManage_flGrid"></div>
  <div id="fl_addwin">
     <form action="" method="post" id="fl_form" enctype="multipart/form-data">
	 	<table  class="fl_table" >
		  <tr>
		    <td>
		       <label for="fl_linkname" >链接名称或图片:<i class="necessary">*</i></label>
		    </td>
		    <td>   
		       <input type="text"  id="fl_linkname" name="fl_linkname" class="txtinput"/>
		       <input type="file"  id="fl_linknameimg" name="fl_linknameimg"/>
		    </td>
		  </tr>
		  <tr>  
		     <td>
		       <label for="fl_type">链接类型:</label>
		        </td>
		    <td>
		       <select id="fl_type" name="fl_type">
		       		<option value="1" selected="selected">文字</option>
		       		<option value="2">图片</option>
		       </select>
		    </td>
		  </tr>  
		 <tr>  
		     <td>
		       <label for="fl_url">地址:<i class="necessary">*</i></label>
		        </td>
		    <td><input type="text"  id="fl_url" style="width:350px;"  name="fl_url" class="txtinput"/>
		    </td>
		  </tr> 
		  <zdw:hasRight url='trueAddFl'>
			    <tr>
		   			<td colspan="2">
		   				<a href="#" id="fl_submit"  class="easyui-linkbutton" data-options="iconCls:'icon-ok'" >提交</a>
		   			</td>
			   </tr>
		   </zdw:hasRight>
		   </table>
	 </form>
 </div>
 
 <div id="fl_upwin">
     <form action="" method="post" id="fl_upform">
      <input type="hidden"  id="fl_upid" name="fl_upid"/>
	 	<table  class="fl_uptable" >
		  <tr>
		    <td>
		       <label for="fl_uplinkname" >链接名称:</label>
		    </td>
		    <td>   
		       <span  id="fl_uplinkname"></span>
		    </td>
		  </tr>
		  <tr>  
		     <td>
		       <label for="fl_uptype">链接类型:</label>
		        </td>
		    <td>
		        <input type="text"  disabled="disabled" id="fl_uptype" class="txtinput disabled"/>
		    </td>
		  </tr>  
		 <tr>  
		     <td>
		       <label for="fl_upurl">地址:<i class="necessary">*</i></label>
		        </td>
		    <td><input type="text" style="width:350px;"  id="fl_upurl"  name="fl_upurl" class="txtinput"/>
		    </td>
		  </tr> 
		   <zdw:hasRight url='trueUpdateFl'>
			    <tr>
			   			<td colspan="2">
			   				<a href="#" id="fl_upsubmit"  class="easyui-linkbutton" data-options="iconCls:'icon-ok'" >提交</a>
			   			</td>
			   </tr>
		   </zdw:hasRight>
		   </table>
	 </form>
 </div>
 </body>
 </html>