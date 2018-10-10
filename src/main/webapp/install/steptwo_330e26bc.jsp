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
	.panel-footer{
		text-align:right;
		padding-right:20px;
	}
	.panel-body{
		padding-bottom: 0;
	}
	label.control-label{
		font-weight: normal;
	}
</style>
<title>乐之者cms环境检测</title>
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
	                    <h3 class="panel-title">环境检测</h3>
	                </div>
	                <div class="panel-body">
	                    <form class="form-horizontal">
		                        <div class="form-group">
		                            <label  class="col-sm-2 control-label">服务器地址:</label>
		                            <div class="col-sm-10">
		                                <input type="text" class="form-control" disabled value="${hostName }">
		                            </div>
		                        </div>
		                        <div class="form-group">
		                            <label class="col-sm-2 control-label">操作系统及版本号:</label>
		                            <div class="col-sm-10">
		                                <input type="text" class="form-control " disabled value="${os }">
		                            </div>
		                        </div>
		                        <div class="form-group">
		                            <label  class="col-sm-2 control-label">jdk版本:</label>
		                            <div class="col-sm-10">
		                                <input type="text" class="form-control " disabled value="${jdkVersion }">
		                            </div>
		                        </div>
		                        <div class="form-group">
		                            <label  class="col-sm-2 control-label">web容器:</label>
		                            <div class="col-sm-10">
		                                <input type="text" class="form-control" disabled value="${serverType }">
		                            </div>
		                        </div>
		                    </form>
	                </div>
	                <div class="panel-footer">
	                	<input value="下一步" class="btn btn-default" type="button" id="next">
	                </div>
	            </div><!--/panel-->
	        </div><!--/col-md-10 col-md-offset-1-->
	    </div>
	</section>
<script type="text/javascript">
	$("#next").click(function(){
		location.href="<%=request.getContextPath()%>/install?flag=step3";
	});
	$("#stepbar ol li").first().removeClass();
	$("#stepbar ol li").first().addClass("step-done");
	$("#stepbar ol li").eq(1).addClass("step-active");
</script>
</body>
</html>