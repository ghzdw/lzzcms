<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>乐之者cms-爬虫入口</title>
     <link rel="stylesheet" href="<%=request.getContextPath() %>/resources/poshytip/tip-yellow/tip-yellow.css">
	<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/bootstrap-3.3.5/css/bootstrap.min.css">
	<link href="<%=request.getContextPath()%>/resources/css/reset.css" rel="stylesheet">
	<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/lzzcmsstyle.css">
	<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-1.10.2.js"></script>
	<script src="<%=request.getContextPath()%>/resources/js/functions-lzzcms-lib.js"></script>
	<script src="<%=request.getContextPath()%>/resources/bootstrap-3.3.5/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/UUID.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/JSON2.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath() %>/resources/poshytip/jquery.poshytip.min.js"></script>
	<style type="text/css">
		i.help{
			position: relative;
			top:0px;
			left:8px;
		}
		i.help img{
			width:14px;
			border:0 ;
			outline:0 none;
		}
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
		label.control-label{
			text-align: left !important;
			padding-left:35px;
			font-weight: normal;
			cursor: pointer;
		}
		.btngrp input:last-child{
			margin-left:250px;
		}
		.inline{
			display: inline-block;
			width: 250px;
		}		
		.inline:last-child{
			margin-left: 50px;
		}
		.nec{
			color:red;
			position: relative;
			top:5px;
			left:3px;
			font-size: 16px;
			font-weight: bold;
			font-style: normal;
		}
		#errorlist .err{
			color:red;
		}
</style>
</head>
<body>
	<div class="container-fluid">
		<div class="row">
			<div class="col-md-12">
				<form class="form-horizontal" method="post" id="fieldsforchannel">
				 	 <div class="form-group">
					    <label  class="col-md-2 control-label">选择栏目:</label>
					    <div class="col-md-10">
							<select class="form-control input-sm inline" name="channel" id="channel"></select>
							<select class="form-control input-sm inline" id="column" name="column"></select>
						</div>
				     </div>
				     <div class="form-group">
					    <label for="website" class="col-md-2 control-label">爬取地址:<i class="nec">*</i>
					    <i class="help"><img src="<%=request.getContextPath() %>/resources/imgs/question.png"></i>
					    </label>
					    <div class="col-md-6">
					      <input type="text" class="form-control input-sm" name="website" id="website"
					      	placeholder="输入网址或符合一定规则的网址"
					      >
					    </div>
					  </div>
					  <div class="form-group commfields">
					    <label for="comm_title" class="col-md-2 control-label">标题:<i class="nec">*</i></label>
					    <div class="col-md-6">
					      <input type="text" class="form-control input-sm" name="comm_title" 
					      placeholder="请输入css选择器,下同" >
					    </div>
					  </div>
					  <div class="form-group commfields">
					    <label for="will_exclude_selector" class="col-md-2 control-label">排除选择器:</label>
					    <div class="col-md-6">
					      <input type="text" class="form-control input-sm"  name="will_exclude_selector" 
					      placeholder="多个使用@[zdw]@分开">
					      <input type="hidden" id="addfortb" name="addfortb">
					    </div>
					  </div>
					  <div class="form-group">
					    <div class="col-md-offset-2 col-md-10 btngrp">
					      <input type="button" class="btn btn-info" id="commit" value="启动任务">
					    </div>
					  </div>
				</form>
			</div>
			<section class="col-md-12 errorlist" id="errorlist">
				
			</section>
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
	$('.help').poshytip({
        content:function () {
            var tip= '如:http://www.roadjava.com/linux/xx.html,xx可以代表有效的地址表示只爬取一页,';
            tip+='比如http://www.roadjava.com/linux/123.html;也可是"@[规则]@",表示爬取所有符合规则的页面,';
            tip+='比如http://www.roadjava.com/linux/@[1-100]@.html,表示爬取1.html,2.html...100.html';
            return tip;
        }
    });
	//回车提交事件
	$(document).keydown(function(e){
		if(e.keyCode==13){
			$("#commit").trigger("click");
		}
	});
	$("#commit").click(function(){
		var website=$("input[name=website]").val();
		var comm_title=$("input[name=comm_title]").val();
		if(!website||!comm_title){
			showTipModal("网址和标题不能为空!");
			return;
		}
		$("#errorlist").empty();
		var obj=str2Json("#fieldsforchannel");
		$.ajax({
			type:"post",
			url:'startSpiderHand',
			data:obj,
			success:function(data){
				if(data.status!="errorList"){
					showTipModal(data.info);
				}else{
					var arr=data.info;
					$("#errorlist").append("<div class='err'>爬取失败列表:</div>");
					$.each(arr,function(i,str){
						$("#errorlist").append("<div>"+str+"</div>");
					});
				}
			},
			beforeSend:function(){
				$("#progressModal").modal();
			},
			complete:function(){
				$("#progressModal").modal("hide");
			},
			error:function(){
				$("#progressModal").modal("hide");
			}
		});
	});
	//bootstrap提示框
	function showTipModal(tip){
		$("#tip").text(tip);
		$('#tipModal').modal("show");
	}
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
			$("#fieldsforchannel .extrafields").remove();
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
				   /*
				     <div class="form-group commfields">
					    <label for="owner_bd_phone" class="col-md-2 control-label">简介:</label>
					    <div class="col-md-6">
					      <input type="text" class="form-control input-sm"  name="comm_intro">
					    </div>
					  </div>
				   */
					$.each(data,function(i,obj){
						var str='<div class="form-group extrafields">';
						str+='<label for="owner_bd_phone" class="col-md-2 control-label">'+obj["showtip"]+':</label>';
						str+='<div class="col-md-6">';
						str+='<input  type="text" class="form-control input-sm"  name="'+obj["colname"]+'">';
						str+='</div></div>';
						$(str).insertAfter($("#fieldsforchannel div.commfields:last"));
					});
				}
			});		
		}
	</script>
</body>
</html>
