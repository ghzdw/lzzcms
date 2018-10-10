<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>出错信息-乐之者cms</title>
	<script src="<%=request.getContextPath() %>/resources/js/jquery-1.10.2.js"></script>
<style>
	body,div,ul,li{
		margin:0;
		padding:0;
		background:rgb(255,255,255);
	}
	.container{
		background:url("<%=request.getContextPath() %>/resources/imgs/error_bg.jpg") no-repeat;
		width:1000px;
		height:620px;
		margin:0 auto;
		overflow:hidden;
		position:relative;
	}
	.info{
		font:normal 20px '微软雅黑';
		position:absolute;
		top:330px;
		left:560px;
		color:orange;
		font-style:italic;
	}
</style>
</head>
<body>
	<div class="container">
		<div class="info">
			<ul>
				<li id="show"></li>
			</ul>
		</div>
	</div>
</body>
<script>
	var s = "${requestScope.tip}";	
	$("#show").text(s);
</script>
</html>