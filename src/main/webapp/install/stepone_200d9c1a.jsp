<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh_cn">
<head>
    <meta charset="utf-8">
        <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/bootstrap-3.3.5/css/bootstrap.min.css">
	<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/stepbar.css">
	<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-1.10.2.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/resources/bootstrap-3.3.5/js/bootstrap.min.js"></script>
<style>
	.cont span{
		display: block;
	}
	.titlespan{
		font-weight: 500;
	}
	.cont span:nth-child(n+2){
	  text-indent: 24px;
	}
	.cont  ul {
		list-style: none;
		margin:0;
		padding:0;
	}
	.cont  ul li {
		padding-left:100px;
	}
	.btn-default{
		float:right;
		margin-right:20px;
	}
	.panel-footer label {
		cursor: pointer;
	}
</style>
<title>乐之者cms安装协议</title>
</head>
<body>
	<section class="container">
       <div class="row">
        <div class="col-md-10 col-md-offset-1">
            <jsp:include page="stepbar.jsp"></jsp:include>
        </div><!--/步骤条-->
        <div class="col-md-10 col-md-offset-1">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title">安装许可</h3>
                </div>
                <div class="panel-body">
                    <section class="cont">
	                     <span class="titlespan">如您勾选同意并成功安装乐之者内容管理系统(以下称"乐之者cms")，则表示您同意并遵守以下协议:</span>
	                     <span>1.您安装乐之者cms后，可以免费使用里面的所有功能，但是仅供软件交流学习使用，不能用于商业传播。</span>
	                     <span>2.乐之者cms源码不开放，如您需要源码，请联系我们购买。</span>
	                     <span>3.乐之者cms需要您的支持，如您使用过程中发现可改进之处，您可以通过以下途径联系乐之者团队，我们会及时作出修改。</span>
	                     <ul>
	                          <li>邮箱：1018757039@qq.com</li>
	                          <li>乐之者客服QQ:1018757039</li>
	                      </ul>
	                     <span>4.购买源码后，可获得乐之者团队技术支持，包括系统的使用、二次开发指导、源码讲解。</span>
                    </section>
                </div>
                <div class="panel-footer"><label><input type="checkbox">　同意</label>
                	<input value="下一步" class="btn btn-default" type="button" id="next" style="background:rgb(245,245,245);cursor: not-allowed;">
                </div>
            </div>
        </div><!--/协议-->
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
</section>
<script type="text/javascript">
	function showTipModal(tip){
		$("#tip").text(tip);
		$('#tipModal').modal("show");
	}
	$(":checkbox").change(function(){
		var isCheck=$(":checkbox").prop("checked");
		if(!isCheck){
			$("#next").css({
				background:'rgb(245,245,245)',
				cursor:'not-allowed'
			});
		}else{
			$("#next").removeProp("style");
		}
	});
	$("#next").click(function(){
		var isCheck=$(":checkbox").prop("checked");
		if(!isCheck){
			showTipModal("请先同意安装许可再执行下一步!");
		}else{
			location.href="<%=request.getContextPath()%>/install?flag=step2";
		}
	});
</script>
</body>
</html>