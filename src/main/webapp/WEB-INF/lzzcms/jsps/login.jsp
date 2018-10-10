	<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
	<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>
	<!DOCTYPE html>
	<html>
	<head>
	 <meta http-equiv="X-UA-Compatible" content="IE=edge">
	 <meta name="viewport" content="width=device-width, initial-scale=1">
	 <title>乐之者cms-管理登录</title>
	 <link rel="shortcut icon" href="<%=request.getContextPath() %>/resources/imgs/favicon.ico" type="image/x-icon">
	<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/bootstrap-3.3.5/css/bootstrap.min.css">
	<link href="<%=request.getContextPath() %>/resources/css/reset.css" rel="stylesheet">
	<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-1.10.2.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/functions-lzzcms-lib.js"></script>
	<script src="<%=request.getContextPath()%>/resources/bootstrap-3.3.5/js/bootstrap.min.js"></script>
	<style>
	.loginbg{
		height: 623px;
		background: #1f2c52;
		margin-top: 5px;
		padding-top: 50px;
		padding-bottom: 50px;
	}
	.rightpart{
		margin-top: 90px;
		position: relative;
		padding-right: 0px;
	}
	.logo{
		position: absolute;
		top:-60px;
		left:-10px;
	}
	.errinfo{
		color:red;
		display: block;
		margin-top: 5px ;
	}
	.errinfo img{
		width:14px;
		margin-right: 5px;
	}
	.hid{
		visibility: hidden;
	}
	.contact{
		color:#b5c0ca;
		font-size: 12px !important;
	}
	.email{
		color:#5eaed8;
	}
	.contactdiv{
		text-align: center;
	}
	#code{
		display: inline;
		height:35px;
	}
	.codeImg{
		position: relative;
		top:-1px;
		left:-1px;
	}
	.codeImg img{
		cursor:pointer;
	}
	</style>
	</head>
	<body>
		<section class="container">
			<div class="row">
				<div class="col-md-12 loginbg">
				  <div class="col-md-8">
					<img src="<%=request.getContextPath() %>/resources/imgs/bg.png">
			  </div>
			  <section class="col-md-4 rightpart">
				<div class="logo"><img src="<%=request.getContextPath() %>/resources/imgs/logo.png"></div>
				<div class="panel panel-default">
				  <div class="panel-body">
			       <form class="form-horizontal" action="trueLogin">
			       	  <div class="form-group">
					    <div class="col-sm-10 col-sm-offset-1">
					      <span>Hi~</span><br><span>欢迎回来，请登录</span>
					      <span class="errinfo hid"><img  src="<%=request.getContextPath() %>/resources/imgs/login_error.svg">
					      		<span id="tiptxt"></span>
					      </span>
					    </div>
					  </div>
					  <div class="form-group">
					    <div class="col-sm-10 col-sm-offset-1">
					       <div class="input-group">
						      <div class="input-group-addon"><i class="glyphicon glyphicon-user"></i></div>
						      <input type="text" class="form-control" id="uname" name="uname" autocomplete="off"  placeholder="请输入用户名">
						    </div>
					    </div>
					  </div>
					  <div class="form-group">
					    <div class="col-sm-10 col-sm-offset-1">
					        <div class="input-group">
						      <div class="input-group-addon"><i class="glyphicon glyphicon-lock"></i></div>
							  <input type="password" class="form-control" id="pwd" name="pwd" autocomplete="off"  placeholder="请输入密码">
						    </div>
					    </div>
					  </div>
					  <div class="form-group">
					    <div class="col-sm-offset-1 col-sm-10">
					      <div class="input-group">
					      	<input  type="text" class="form-control" id="code" name="code" placeholder="输入验证码" />
					      	<div class="input-group-addon" style="padding:0;background:#fff;border:0;">
					      		<span class="codeImg" ><img src="<%=request.getContextPath()%>/codeServlet" /></span>
					      	</div>
					      </div>	
					    </div>
					  </div>
					  <div class="form-group">
					    <div class="col-sm-offset-1 col-sm-10">
					    <c:if test="${empty lzzcms_u}">
							<div class="checkbox">
						        <label>
						          <input type="checkbox" name="remember"  value="remember"> 七天内自动登录
						        </label>
						    </div>
						</c:if>
					    </div>
					  </div>
					  <div class="form-group">
					    <div class="col-sm-offset-1 col-sm-10">
					      <input id="truelogin" type="button" class="btn btn-primary btn-block" value="登录">
					    </div>
					  </div>
					  <input type="hidden" name="flag" >
					  <input type="hidden" name="fromLogout" id="fromLogout" 
					    value="${requestScope.fromLogout }" />
					</form>
				  </div>
				</div>
				<div class="contactdiv"><span class="contact">如果使用上有任何问题,请记得联系我们:
					<span class="email">
						<a href="http://www.roadjava.com" target="_blank">http://www.roadjava.com</a>
					</span></span>
				</div>
			  </section>
			</div>
		</div>
	</section>
	<script>
	$(document).keydown(function(e){
		if(e.keyCode==13){
			$("#truelogin").trigger("click");
		}
	});
	  var targ = "${requestScope.to}";	
	  if(targ){
	   top.location.href=targ;
	  }
	  window.onload=function(){
		//提交处理
		$("#truelogin").click(function(){
			var obj=str2Json("form");
			$.ajax({
				type:"post",
				data:obj,
				url:$($("form")[0]).attr("action"),
				success:function(data){
					if(data.info=="ok"){
						location.href="toIndex";
					}else if(data.info=="error"){
						$("#tiptxt").text(data.errinfo);
						$(".errinfo").removeClass("hid");
						$("#code").val("");
						$(".codeImg img").attr("src","<%=request.getContextPath()%>/codeServlet?"+Math.random());
					}
				}
			});
		});
		//更新验证码
		$(".codeImg").on("click","img",function(){
			$(this).attr("src","<%=request.getContextPath()%>/codeServlet?"+Math.random());
		});
		//得到cookie
		function getCookie(){
			var arr,reg=/\s*(\w+)=([^;]*);*/g,obj={};
			while((arr=reg.exec(document.cookie))!=null){
				obj[arr[1]]=arr[2];
			}
			return obj;
		}
	   //自动登录
	   function autoLogin(uname,pwd){
		   $(":hidden").val("auto");
		   $("#uname").val(uname);
		   $("#pwd").val(pwd);
		   $("#truelogin").trigger("click");
	   }
	   var obj=getCookie();
	   var uname=obj["lzzcms_u"];
	   var pwd=obj["lzzcms_p"];
	   if(uname&&pwd){
		   if(!$("#fromLogout").val()){
			  autoLogin(uname,pwd);
		   }
	   }
	  };
	</script>
	</body>
	</html>