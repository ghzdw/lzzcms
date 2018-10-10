<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="zdw" uri="/lzzcms" %> 
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>乐之者cms-站点备份</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/bootstrap-3.3.5/css/bootstrap.min.css">
 <link rel="stylesheet" href="<%=request.getContextPath() %>/resources/poshytip/tip-yellow/tip-yellow.css">
<link href="<%=request.getContextPath()%>/resources/css/reset.css" rel="stylesheet">
<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/lzzcmsstyle.css">
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-1.10.2.js"></script>
<script src="<%=request.getContextPath()%>/resources/js/functions-lzzcms-lib.js"></script>
<script src="<%=request.getContextPath()%>/resources/bootstrap-3.3.5/js/bootstrap.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/resources/poshytip/jquery.poshytip.min.js"></script>
<script type="text/javascript">
$(function(){  
	$("#backupBtn").on("click",function(){
		$.ajax({
			type:"post",
			url:'backUp',
			success:function(result){
				if(result.info=="ok"){
					showTipModal("备份完成");
					loadTb();
				}else if(result.info=="error"){
					showTipModal(result.errinfo);
				}
			},
			beforeSend:function(){
				$("#progressModal").modal();
			},
			complete:function(){
				$("#progressModal").modal("hide");
			}
		});
	});
	$("#backtoBtn").on("click",function(){
		var checked=$(":checked").length;
		if(checked==1){
			var txt=$(":checked").parent().next().text();
			$.ajax({
				type:"post",
				url:'backTo',
				data:{"backupName":txt},
				success:function(result){
					if(result.info=="ok"){
						showTipModal("还原成功");
					}else if(result.info=="error"){
						showTipModal(result.errinfo);
					}
				},
				beforeSend:function(){
					$("#progressModal").modal();
				},
				complete:function(){
					$("#progressModal").modal("hide");
				}
			});
		}else{
			showTipModal("请选择一个备份文件进行还原");
		}
	});
	function showTipModal(tip){
		$("#tip").text(tip);
		$('#tipModal').modal("show");
	}
	//加载备份表格
	function loadTb(){
		$("#backuptb tr:gt(0)").remove();
		$.ajax({
			type:"post",
			url:'listBackUps',
			success:function(result){// <tr><th>备份名称</th><th>时间</th><th>大小</th><th>用户</th></tr>
				//$.messager.progress("close");
				var str="";
				$.each(result,function(i,obj){
					str+="<tr><td><input type='checkbox'></td><td>"+formatVal(obj["backup_name"])+"</td><td>"+formatVal(obj["backup_date"])
					+"</td><td>"+formatVal(obj["backup_user"])+"</td></tr>";
				});
				if(str){
					$("#backuptb").append(str);
				}
			}
		});
	}
	loadTb();
	function formatVal(str){
		if(str){
			return str;
		}else{
			return "";
		}
	}
	$('.help').first().poshytip({
        content:function () {
            var tip= "最终在工程根目录生成zip文件,包含：1.表结构 2.表数据<br>3.uploads文件夹所有内容  4.tpls文件夹所有内容";
            return tip;
        }
    });
	$('.help').last().poshytip({
        content:function () {
            var tip= "还原系统数据为所选压缩包的状态";
            return tip;
        }
    });
});
</script>
<style>
	#modal-content,#modal-body{
		background: transparent;
		border: 0;
		box-shadow:none;
	}
	table,table tr,table tr th{
		text-align: center;
	}
	table tr th:first-child{
		width:50px;
	}
	.help{
		padding-left:3px;
	}
</style>
</head>
<body>
	<section class="container-fluid">
		<div class=row>
			<div class="col-md-12">
				<div class="btn-group" >
				   <zdw:hasRight url='backUp'> 
					<button type="button" class="btn btn-default"   id="backupBtn">
						备份<i class="help"><img src="../resources/imgs/question.png"></i>
					</button>
				   </zdw:hasRight>
				  <zdw:hasRight url='backTo'> 
					<button type="button" class="btn btn-default"   id="backtoBtn">
						还原<i class="help"><img src="../resources/imgs/question.png"></i>
					</button>
				   </zdw:hasRight>	 
				</div>
			</div>
		</div>
		<div class=row>
			<div class="col-md-12">
				<table class="table table-striped table-bordered table-hover table-condensed" id="backuptb">
				   <tr><th>选择</th><th>备份名称</th><th>时间</th><th>用户</th></tr>
				</table>
			</div>
		</div>
		<!-- 提示模态框 -->
		<div class="modal fade" tabindex="-1" id="tipModal">
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
	</section>
	
</body>
</html>