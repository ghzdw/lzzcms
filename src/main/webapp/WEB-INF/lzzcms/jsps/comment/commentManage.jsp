<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="zdw" uri="/lzzcms" %>  
<style>
.opera{
	padding-bottom:3px;
}
.opera span{
	display: inline-block;
	padding-right:8px;
}
i.necessary{
		color:red;
		font-weight: bold;
		font-size: 17px;
		position: relative;
		top:5px;
	}
#cm_comment_cont{
    width: 100%;
    height: 100px;
    overflow-x: hidden;
    overflow-y: auto;
	 border:1px solid #447bf3; 
	 outline:none;  
    resize: none;
 }
#cm_comment_cont:focus{
	    box-shadow:0 0 4px rgba(102,175,233,.9);  
	    transition: box-shadow  .5s;  
	}	
.cm_table{
	width: 95%;
}	
.cm_table tr{
	    height:35px;
	    line-height:35px;
	}
	.cm_table label{
		width:90px;
	    display: inline-block;
	}
	.cm_table input{
		width:170px;
		outline:none;
		font-size:14px !important;
		font-weight:normal;
		height:30px;
	}
	#commentcontainer .datagrid-cell{
		word-wrap: break-word;
		white-space: normal;
	}
	.likeacomment{
		display: inline-block;
		height:30px;
		line-height: 30px;
		color:#7D26CD;
		text-decoration: none;
		margin: 0;
		padding:0;
	}
</style>
<section id="commentcontainer">
<div class="opera">
	<zdw:hasRight url="replyComment">
		<span>
			<a href="#" id="replyCommentBtn"  class="easyui-linkbutton" data-options="iconCls:'icon-add'">&nbsp;回复评论</a>
		</span>
	</zdw:hasRight>
	<zdw:hasRight url="deleteComment">
		<span>	
			<a href="#" id="deleteCommentBtn"  class="easyui-linkbutton" data-options="iconCls:'icon-clear'">&nbsp;删除评论</a>
		</span>	
	</zdw:hasRight>
	<zdw:hasRight url="listComments">
		<span>	
			<a href="#" id="queryCommentBtn"  class="easyui-linkbutton" data-options="iconCls:'icon-search'">&nbsp;查询</a>
		</span>	
	</zdw:hasRight>
</div>
<div id="commentmanage_reply" class="easyui-window" title="回复评论" 
		style="width:600px;height:210px;"   
        data-options="iconCls:'icon-more',modal:true,collapsible:false,minimizable:false,closed:true">
     <form action="" method="post" id="cm_form">
	 	<table  class="cm_table">
		  <tr>
		    <td valign="top" style="width:100px">
		       <label for="cm_comment_cont" >输入回复内容:<i class="necessary">*</i></label>
		    </td>
		    <td style="width:480px">   
		        <textarea name="cm_comment_cont" id="cm_comment_cont"></textarea>
		        <input type="hidden" name="cm_comm_id" id="cm_comm_id">
		        <input type="hidden" name="cm_reply_id" id="cm_reply_id">
		    </td>
		  </tr>
		  <zdw:hasRight url='replyComment'>
			    <tr>
		   			<td colspan="2" style="text-align: center;">
		   				<a href="#" id="replySubmitBtn"  class="easyui-linkbutton" data-options="iconCls:'icon-ok'" >提交</a>
		   			</td>
			   </tr>
		   </zdw:hasRight>
		   </table>
	 </form>
 </div>
 <div id="cm_querywin" class="easyui-window" title="请输入查询条件进行筛选" style="width:1000px;height:'auto';padding:10px;"   
        data-options="iconCls:'icon-search',modal:true,collapsible:false,minimizable:false,closed:true">   
	 <form action="" method="post" id="cm_query_form">
	 	<table  class="cm_table">
		  <tr>
		    <td><label for="cm_doc_title">所属文章</label> </td>
		    <td><input type="text"  name="cm_doc_title" class="bootstrapinput"/></td>
		     <td><label for="cm_comment_cont">评论内容</label> </td>
		    <td><input type="text"  name="cm_comment_cont" class="bootstrapinput"/></td>
		  </tr>
		  <tr>
		  		<td><label for="cm_pub_name">发布人</label> </td>
		    	<td><input type="text"  name="cm_pub_name" class="bootstrapinput"/></td>
		    	<td><label for="cm_pub_ip">发布ip</label> </td>
		    	<td><input type="text"  name="cm_pub_ip" class="bootstrapinput"/></td>
		  </tr>
		  <tr>
		     <td><label for="cm_pub_location">位置</label> </td>
		    <td><input type="text"  name="cm_pub_location" class="bootstrapinput"/></td>
		    <td><label>评论时间</label></td>
		    <td>
		    	<input style="height: 30px" class="easyui-datebox" type="text" data-options="editable:false"
		    	 name="cm_create_time" />
		    </td>
		  </tr>
		  <tr>
		    <td colspan="6" style="text-align: center;">
		    <a href="#"  id="cm_reset" class="easyui-linkbutton" data-options="iconCls:'icon-search'" >重置</a>
		    <a href="#" id="cm_queryBtn"  class="easyui-linkbutton" data-options="iconCls:'icon-search'" >查询</a>
		    </td>
		  </tr>
		</table>
	 </form>
</div> 
 
 <div id="commentManage_commentGrid"></div>
</section> 
 <script type="text/javascript">
 //弹出搜索对话框
	 $("#queryCommentBtn").click(function(){//搜索内容
			$("#cm_querywin").window("open");
	});	
	 $("#cm_reset").click(function(){//处理重置按钮
		  	$("#cm_query_form").form("reset");
	});
	 $("#cm_queryBtn").on("click",function(){
		 var data=str2Json("#cm_query_form");
		$("#commentManage_commentGrid").datagrid("options").queryParams={"param":JSON.stringify(data)};
		$("#commentManage_commentGrid").datagrid("load");
		$("#cm_querywin").window("close");
	 });
 $("#replySubmitBtn").click(function(){
	 var obj={};
	 obj['comm_id']=$('#cm_comm_id').val();
	 obj['comment_cont']=$('#cm_comment_cont').val();
	 if(!obj['comment_cont']){
		 $.messager.alert("提示","回复不能为空","error"); 
		 return false;
	 }
	 obj['reply_id']=$('#cm_reply_id').val();
	 $.ajax({
		type:'post',
		url:'replyComment',
		data:obj,
		success:function(result){
			if(!result.status){
				$("#commentmanage_reply").window("close");
				$("#commentManage_commentGrid").datagrid("load");
			}else{
				$.messager.alert("提示",result.info,"error"); 
			}					
		}
	 });		
 });
		$("#commentManage_commentGrid").datagrid({ 
			height:500,
			striped:true,
			rownumbers:true,
			pagination:true,
			pageList:[15,20,30],
			pageSize:15,
			method:"post",
		    columns:[[    
		        {field:'',checkbox:true},                
		        {field:'id',title:'评论编号',halign:'center',sortable:true,resizable:true,width:80},    
		        {field:'comm_title',title:'所属文章',halign:'center',sortable:true,resizable:true,
		        	width:250,formatter:function(value,row,index){
			        	return   "<a target='_blank' href='${basePath}/"+row.comm_htmlpath+"' class='likeacomment'>"+value+"</a>";
			        }},    
		        {field:'comment_cont',title:'评论内容',halign:'center',sortable:true,resizable:true,width:260},    
		        {field:'create_time',title:'评论时间',halign:'center',sortable:true,resizable:true,width:140},    
		        {field:'pub_name',title:'发布人',halign:'center',sortable:true,resizable:true,width:80},    
		        {field:'pub_ip',title:'发布ip',halign:'center',sortable:true,resizable:true,width:120},   
		        {field:'pub_location',title:'位置',halign:'center',sortable:true,resizable:true,width:120},    
		        {field:'ding_cnt',title:'赞次数',halign:'center',sortable:true,resizable:true,width:50},    
		        {field:'cai_cnt',title:'踩次数',halign:'center',sortable:true,resizable:true,width:50}   
		    ]],    
			url:"listComments",
			remoteSort:true
	});
	$("#replyCommentBtn").click(function(){//回复
		var selections=$("#commentManage_commentGrid").datagrid("getSelections");
		if(selections.length==0){
			$.messager.alert("提示","请先选择要回复的评论!","error");
		}else if(selections.length!=1){
			$.messager.alert("提示","一次只能回复一条评论!","error");
	    }else{
	    	$('#cm_comm_id').val(selections[0]["comm_id"]);
	   	    $('#cm_reply_id').val(selections[0]["id"]);
			$("#commentmanage_reply").window("open");
	    }
	});	
	$("#deleteCommentBtn").click(function(){//删除频道
		var selections=$("#commentManage_commentGrid").datagrid("getSelections");
		var ids=[];
		if(selections.length==0){
			$.messager.alert("提示","请先选择要删除的行!","error");
		}else{
			$.messager.confirm('确认对话框', '确定要删除该评论吗？', function(r){
				if (r){
					for(var i=0;i<selections.length;i++){
						ids.push(selections[i]["id"]);
					}
					var params={};
				    params["param"]=ids.join(",");
					$.ajax({
						type:"post",
						url:"deleteComment",
						data:params,
						success:function(result){
							if(result["status"]=="sucess"){
								$("#commentManage_commentGrid").datagrid("load");
							}else{
					        	$.messager.alert('错误',result.info,'error');
					        }
						}
					});
				}
			});
	    }
	});	
</script>