<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html lang="zh_cn">
<html>
<head>
<head>
    <meta charset="utf-8">
        <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/bootstrap-3.3.5/css/bootstrap.min.css">
	<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/stepbar.css">
	<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-1.10.2.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/resources/bootstrap-3.3.5/js/bootstrap.min.js"></script>
<style>
	.panel-body{
		text-align: center;
	}
</style>
<title>乐之者cms安装-安装完成</title>
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
	                    <h3 class="panel-title">安装完成</h3>
	                </div>
	                <div class="panel-body">
	                	<div class="row">
	                		<p>安装已经完成，请重新启动服务器</p>
	                		<div class="col-md-6">
	                			网站首页:<a href="${requestScope.basePath }/" target="_blank">${requestScope.basePath }/</a>
	                		</div>
	                		<div class="col-md-6">
	                			网站后台:<a href="${requestScope.backstagePath }/" target="_blank">${requestScope.backstagePath }/</a>
	                		</div>
	                	</div>
	                </div>
	            </div><!--/panel-->
	        </div><!--/col-md-10 col-md-offset-1-->
	    </div>
	</section>
<script type="text/javascript">
	$("#stepbar ol li:lt(3)").removeClass();
	$("#stepbar ol li:lt(3)").addClass("step-done");
	$("#stepbar ol li").eq(3).addClass("step-active");
</script>
</body>
</html>