<%@ page language="java" contentType="text/html; charset=utf-8"  pageEncoding="utf-8"%>
<%@ taglib prefix="zdw" uri="/lzzcms" %>    
<style>
.likeaclnm,.likeaminwidth{
	width:70px;
	display: inline-block;
	height:30px;
	line-height: 30px;
	color:#7D26CD;
	text-decoration: none;
	text-align: center;
	margin: 0;
	padding:0;
}
.likeaminwidth{
	width:40px;
}
.opera{
	padding-bottom:3px;
}
.opera span{
	display: inline-block;
	padding-right:8px;
}
</style>
<script type="text/javascript">
function addSubCln(clnid){
	   var param={};
	   param['clnid']=clnid;
	   addTab("新增子栏目",'toAddCln',{queryParams:param});
}
function deleteCln(clnid,channel_id){
	$.messager.confirm('确认对话框', '确定要删除该栏目信息吗？', function(r){
		if (r){
			$.ajax({
				type:"post",
				url:"deleteClnById",
				data:{columnid:clnid,chlid:channel_id},
				success:function(result){
					if(result=="success"){
						$('#columnManage_columntreegrid').treegrid("reload");
					}else{
			        	$.messager.alert('错误',result,'error');
			        }
				}
			});
		}
	});
}
function toUpCln(clnid){
	   var param={};
	   param['clnid']=clnid;
	   addTab("修改栏目",'toUpCln',{queryParams:param});
}
function clnDetails(clnid){
	   var param={};
	   param['clnid']=clnid;
	   addTab("栏目详细信息",'getClnDetails',{queryParams:param});
	}
$(function(){
		$('#columnManage_columntreegrid').treegrid({    
		    url:'columnInfo', 
		    idField:'id',    
		    treeField:'columnName', 
		    method:"post",
		    rownumbers: true,
		    showFooter: true,
		    animate: true,
		    columns:[[    
		        {title:'栏目编号',field:'id',halign:'center',sortable:true,resizable:true,width:70,align:'center'},
		        {title:'栏目名称',field:'columnName',halign:'center',sortable:true,resizable:true,width:200},
		        {title:'类型',field:'typename',halign:'center',sortable:true,resizable:true,width:70},
		        {title:'对应模版',field:'mytpl',halign:'center',sortable:true,resizable:true,width:170},
		        {title:'html存放目录',field:'htmldir',halign:'center',sortable:true,resizable:true,width:250},
		        {title:'所属模型',field:'channelname',halign:'center',sortable:true,resizable:true,width:100},
		        {title:'排序',field:'orderno',halign:'center',sortable:true,resizable:true,width:50,align:'center'},
		        {title:'操作',field:'caozuo',halign:'center',sortable:true,resizable:true,width:300,formatter:function(value,row,index){
		        	return "<zdw:hasRight url='toAddCln'> <a href='javascript:void(0)' class='likeaclnm' onclick='addSubCln(\""+row.id+"\")'>增加子栏目</a></zdw:hasRight>"
		        	+"<zdw:hasRight url='deleteClnById'><a href='javascript:void(0)' class='likeaminwidth' onclick='deleteCln(\""+row.id+"\",\""+row.channel_id+"\")'>删除</a></zdw:hasRight>"
		        	+"<zdw:hasRight url='toUpCln'><a href='javascript:void(0)'  class='likeaminwidth' onclick='toUpCln(\""+row.id+"\")'>修改</a></zdw:hasRight>"
		        	+"<zdw:hasRight url='getClnDetails'><a href='javascript:void(0)' class='likeaclnm'  onclick='clnDetails(\""+row.id+"\")'>详细信息</a></zdw:hasRight>";
		        }},
		    ]]
	});  
		$("#reloadClnBtn").click(function(){//刷新栏目列表按钮
			$('#columnManage_columntreegrid').treegrid("reload");
		});
		$("#addTopClnBtn").click(function(){//增加顶级栏目
			 var id=UUID.generate();
			 addTabIframe({txt:"新增顶级栏目",url:"addTopCln",uuid:id});
			 document.getElementById(id).onload=function(){
				 $("#"+id).contents().find("html,body").css("height","100%");
			 };
		});
});
</script>
<div class="opera">
	<span>
		<a href="#" id="reloadClnBtn"  class="easyui-linkbutton" data-options="iconCls:'icon-reload'">&nbsp;刷新列表</a>
	</span>
   <zdw:hasRight url="addTopCln">
	<span>	
		<a href="#" id="addTopClnBtn"  class="easyui-linkbutton" data-options="iconCls:'icon-add'">&nbsp;增加顶级栏目</a>
	</span>	
   </zdw:hasRight>	
</div>
<table id="columnManage_columntreegrid"></table>