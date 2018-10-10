<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>乐之者cms-爬虫入口</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/bootstrap-3.3.5/css/bootstrap.min.css">
<link href="<%=request.getContextPath()%>/resources/css/reset.css" rel="stylesheet">
<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/lzzcmsstyle.css">
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-1.10.2.js"></script>
<script src="<%=request.getContextPath()%>/resources/js/functions-lzzcms-lib.js"></script>
<script src="<%=request.getContextPath()%>/resources/bootstrap-3.3.5/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/UUID.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/JSON2.js"></script>
<style type="text/css">
.container-fluid{
	padding-top: 30px;
}
.form-group{
	margin-bottom: 10px;
}
label{
	font-weight: normal;
}
span.label{
	cursor: pointer;
}
i.necessary{
		color:red;
		font-weight: bold;
		font-size: 17px;
		position: relative;
		top:5px;
}
</style>
</head>
<body>
	<div class="container-fluid">
		<div class="row">
			<div class="col-md-12">
				<form>
				    <div class="form-group row">
					    <label for="website" class="col-md-12 control-label">选择要配置的字段:</label>
						<div class="col-md-2">
							<select class="form-control" name="channel" id="channel">
							</select>
						</div>
						<div class="col-md-2">
							<select class="form-control" id="column" name="column">
							</select>
						</div>
						<div class="col-md-8" id="fieldsforchannel">
							<span class="label label-warning" id="allck">全&nbsp;选</span>
							<span class="label label-warning" id="allnock">全不选</span>
							<label class="checkbox-inline">
							   <input type="checkbox" data-text="标题" data-common="1" name="comm_title"   value="comm_title">标题
							 </label>
							<label class="checkbox-inline">
							   <input type="checkbox" data-text="简短标题" data-common="1" name="comm_shorttitle" value="comm_shorttitle">简短标题
							 </label>
							<label class="checkbox-inline">
							   <input type="checkbox" data-text="点击量" data-common="1" name="comm_click" value="comm_click">点击量
							 </label>
							<label class="checkbox-inline">
							   <input type="checkbox" data-text="作者" data-common="1" name="comm_author" value="comm_author">作者
							 </label>
							<label class="checkbox-inline">
							   <input type="checkbox" data-text="发布时间" data-common="1" name="comm_publishdate" value="comm_publishdate">发布时间
							 </label>
							<label class="checkbox-inline">
							   <input type="checkbox" data-text="来源" data-common="1" name="comm_src" value="comm_src">来源
							 </label>
							<label class="checkbox-inline">
							   <input type="checkbox"  data-text="简介" data-common="1" name="comm_intro" value="comm_intro">简介
							</label>
						</div>
				     </div>		
				     <div class="form-group row">
					    <label for="website" class="col-md-12 control-label">输入要爬取的网址:<i class="necessary">*</i></label>
					    <div class="col-md-10">
					      <input type="text" class="form-control" id="website" name="website" placeholder="输入要爬取的网址">
					    </div>
					    <div class="col-md-1">
					      <input type="hidden"  id="addfortb">
					      <input type="button" class="btn btn-default" id="parse" value="提交">
					    </div>
					  </div>
				</form>
			</div>
		</div>
			<!-- 进度条模态框 -->
			<div class="modal fade" data-backdrop="static" data-keyboard="false" tabindex="-1" id="progressModal">
			  <div class="modal-dialog">
			    <div class="modal-content" id="modal-content">
			      <div class="modal-body" id="modal-body">
			        	<div class="progress">
						  <div class="progress-bar progress-bar-success progress-bar-striped active" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%">
						  </div>
						</div>
			      </div>
			    </div><!-- /.modal-content -->
			  </div><!-- /.modal-dialog -->
			</div><!-- /.modal -->
			<!-- 提示模态框 -->
			<div class="modal fade" id="tipModal">
			  <div class="modal-dialog">
			    <div class="modal-content">
			      <div class="modal-header">
			        <button type="button" class="close" data-dismiss="modal" ><span>&times;</span></button>
			        <h4 class="modal-title">提示</h4>
			      </div>
			      <div class="modal-body">
			        <p id="tip"></p>
			      </div>
			      <div class="modal-footer">
			        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
			      </div>
			    </div><!-- /.modal-content -->
			  </div><!-- /.modal-dialog -->
			</div><!-- /.modal -->
	</div>
	<script type="text/javascript">
	/*
		var obj={
			id:uuid,
			columnId:2,
			website:http,
			addfortb:'lzz_addforarticle'
			cols:[{
					colName:comm_title,
					showtip:标题,
					iscommon:'1'
					},{
						colName:mainbody,
						showtip:文章内容,
						iscommon:"0"
					}
			       
			       ]
	}
	*/
	//初始聚焦
	$("#website").focus();
	//回车提交事件
	$(document).keydown(function(e){
		if(e.keyCode==13){
			$("#parse").trigger("click");
		}
	});
	//bootstrap提示框
	function showTipModal(tip){
		$("#tip").text(tip);
		$('#tipModal').modal("show");
	}
		$("#parse").click(function(){
			if(!$("#website").val()){
				showTipModal("网址不能为空");
				return false;
			}
			if($("input[type='checkbox']:checked").length==0){
				showTipModal("请先选择要配置的字段");
				return false;
			}
			var obj={};
			obj["id"]=UUID.generate();
			obj["columnId"]=$("#column").val();
			obj["website"]=$("#website").val();
			obj["addfortb"]=$("#addfortb").val();
			var colsArr=[];
			$.each($("input[type='checkbox']:checked"),function(i,obj){
				var tmpObj={};
				tmpObj["colName"]=$(obj).val();
				tmpObj["showtip"]=$(obj).attr("data-text");
				tmpObj["iscommon"]=$(obj).attr("data-common");
				colsArr.push(tmpObj);
			});
			obj["cols"]=colsArr;
			$.ajax({
				type:"post",
				url:'parseUrl',
				data:{"param":JSON.stringify(obj)},
				success:function(data){
					 var id=UUID.generate();
					 var txt="爬取配置";
					 var url="toCfgSpider?src="+data+"&cfgId="+obj["id"];
					 parent.addTabIframe({txt:txt,url:url,uuid:id});
				},
				beforeSend:function(){
					$("#progressModal").modal();
				},
				complete:function(){
					$("#progressModal").modal("hide");
				}
			});
		});
		//模型加载
		$.ajax({
			type:"get",
			url:'getForCombobox?page=toAdd',
			success:function(data){//<option value="1">普通文章</option>
				$.each(data,function(i,obj){
					if(i==0){
						$("#channel").append("<option selected data-addfortb='"+obj["additionaltable"]+"' value='"+obj["id"]+"'>"+obj["channelname"]+"</option>");
						$("#addfortb").val(obj["additionaltable"]);
					}else{
						$("#channel").append("<option data-addfortb='"+obj["additionaltable"]+"'  value='"+obj["id"]+"'>"+obj["channelname"]+"</option>");
					}
				});
				var selectedChnId=$("#channel").val();
				//加载栏目
				getColumnByChanelId(selectedChnId);
				loadFieldsByChannelId(selectedChnId);
			}
		});		
		//模型选择改变事件
		$("#channel").change(function(){
			var selectedChnId=$("#channel").val();
			var selectedAddfortb=$("#channel").find("option[value="+selectedChnId+"]").attr("data-addfortb");
			$("#addfortb").val(selectedAddfortb);
			//加载栏目
			getColumnByChanelId(selectedChnId);
			loadFieldsByChannelId(selectedChnId);
		});
		function getColumnByChanelId(channelId){
			$("#column").empty();
			$.ajax({
				type:"post",
				url:'getColumnByChanelIdForSpider',
				data:{"chanelId":channelId},
				success:function(data){
					$.each(data,function(i,obj){
						if(i==0){
							$("#column").append("<option selected  value='"+obj["id"]+"'>"+obj["name"]+"</option>");
						}else{
							$("#column").append("<option value='"+obj["id"]+"'>"+obj["name"]+"</option>");
						}
					});
				}
			});	
		}
		//选中模型的字段加载
		/*
		<label class="checkbox-inline">
							   <input type="checkbox"  value="option1"> 1
							 </label>
		*/
		function loadFieldsByChannelId(chnId){
			$("#fieldsforchannel label:gt(6)").remove();
			$.ajax({
				type:"post",
				url:'getChannelExtralField',
				data:{"chlid":chnId},
				success:function(data){
				   if(data.length==1){
					   var first=data[0];
					   if(first["info"]){
						  return; 
					   }
				   }
					$.each(data,function(i,obj){
						var str='<label class="checkbox-inline">';
						str+='<input type="checkbox" data-text="'+obj["showtip"]+'" data-common="0" value="'+obj["colname"]+'">'+obj["showtip"];
						str+='</label>';
						$("#fieldsforchannel").append(str);
					});
				}
			});		
		}
		//全选全不选
		$("#allck").click(function(){
			$(":checkbox").prop("checked",true);
		});
		$("#allnock").click(function(){
			$(":checkbox").prop("checked",false);
		});
	</script>
</body>
</html>
