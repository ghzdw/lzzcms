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
</style>
<div class="opera">
	<zdw:hasRight url="toAddChlInfo">
		<span>
			<a href="#" id="addChlBtn"  class="easyui-linkbutton" data-options="iconCls:'icon-add'">&nbsp;增加频道</a>
		</span>
	</zdw:hasRight>
	<zdw:hasRight url="toEditChannelById">
		<span>	
			<a href="#" id="editChlBtn"  class="easyui-linkbutton" data-options="iconCls:'icon-edit'">&nbsp;编辑频道</a>
		</span>	
	</zdw:hasRight>
	<zdw:hasRight url="deleteChl">
		<span>	
			<a href="#" id="deleteChlBtn"  class="easyui-linkbutton" data-options="iconCls:'icon-clear'">&nbsp;删除频道</a>
		</span>	
	</zdw:hasRight>
</div>
 <div id="channelManage_channelGrid"></div>
 <script type="text/javascript">
		$("#channelManage_channelGrid").datagrid({//频道/模型管理
			height:500,
			striped:true,
			rownumbers:true,
			pagination:true,
			pageList:[15,20,30],
			pageSize:15,
			method:"post",
		    columns:[[    
		        {field:'',checkbox:true},                
		        {field:'id',title:'编号',halign:'center',sortable:true,resizable:true,width:200},    
		        {field:'channelname',title:'频道名称',halign:'center',sortable:true,resizable:true,width:200},    
		        {field:'enname',title:'模版标识',halign:'center',sortable:true,resizable:true,width:200},    
		        {field:'commontable',title:'共用表名称',halign:'center',sortable:true,resizable:true,width:200},    
		        {field:'additionaltable',title:'附加表名称',halign:'center',sortable:true,resizable:true,width:200}   
		    ]],    
			url:"listChannels",
			remoteSort:true
	});
	$("#addChlBtn").click(function(){//增加频道
		addTab("增加频道","toAddChlInfo");
	});	
	$("#editChlBtn").click(function(){//编辑频道
		var selections=$("#channelManage_channelGrid").datagrid("getSelections");
		if(selections.length==0||selections.length!=1){
			$.messager.alert("提示","请先选择要编辑的行且一次只能编辑一个频道!","error");
		}else{
		    var chlid=selections[0]["id"];
		    addTab("编辑频道","toEditChannelById",{
		    	queryParams:{"chlid":chlid}
		    });
	   }
	});	
	$("#deleteChlBtn").click(function(){//删除频道
		var selections=$("#channelManage_channelGrid").datagrid("getSelections");
		if(selections.length==0){
			$.messager.alert("提示","请先选择要删除的行!","error");
		}else{
			$.messager.confirm('确认对话框', '确定要删除与频道有关的栏目、文档以及文件吗？', function(r){
				if (r){
					var list=[];
					for(var i=0;i<selections.length;i++){
						var obj={};
						obj["id"]=selections[i]["id"];
						obj["additionaltable"]=selections[i]["additionaltable"];
						list.push(obj);
					}
				  	var params={};
				    params["chls"]=JSON.stringify(list);
					$.ajax({
						type:"post",
						url:"deleteChl",
						data:params,
						success:function(result){
							if(result.status=="success"){
								$("#channelManage_channelGrid").datagrid("load");
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