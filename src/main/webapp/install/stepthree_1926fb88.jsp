<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh_cn">
<head>
    <meta charset="utf-8">
        <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/bootstrap-3.3.5/css/bootstrap.min.css">
        <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/poshytip/tip-yellow/tip-yellow.css">
	<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/stepbar.css">
	<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-1.10.2.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/resources/bootstrap-3.3.5/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/resources/poshytip/jquery.poshytip.min.js"></script>
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
		position: relative;
	}
	.necessary{
		font-size:17px;
		font-weight: bold;
		color:red;
		position: absolute;
		top:9px;
		left:145px;
	}
	html,body{
		height: 100%;
		margin:0;
		padding:0;
	}
	.backdroplayer{
		height:100%;
		background:black;
		width:100%;
		position: fixed;
		top:0;
		z-index: 1000;
	    opacity:.6;
	}
	.backdroplayer>img{ 
		position: relative;
		top:50%;
		left:50%;
	}
	.backdroplayer.out{
		display: none;
	}
	.backdroplayer.in{
		display: block;
	}
</style>
<title>乐之者cms-数据库配置</title>
</head>
<body>
	<div class="backdroplayer out">
		<img src="<%=request.getContextPath()%>/resources/imgs/loading.gif">
    </div>
		<section class="container" id="container">
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
		                            <label  class="col-sm-2 control-label">数据库地址:<i class="necessary">*</i></label>
		                            <div class="col-sm-10">
		                                <input type="text" class="form-control" name="dbServerIp" placeholder="输入数据库所在地址，比如:127.0.0.1">
		                            </div>
		                        </div>
		                         <div class="form-group">
		                            <label  class="col-sm-2 control-label">数据库端口:<i class="necessary">*</i></label>
		                            <div class="col-sm-10">
		                                <input type="text" class="form-control" name="dbPort" placeholder="请输入数据库端口,比如:3306">
		                            </div>
		                        </div>
		                         <div class="form-group">
		                            <label  class="col-sm-2 control-label">数据库名称:<i class="necessary">*</i></label>
		                            <div class="col-sm-10">
		                                <input type="text" class="form-control" name="dbName" placeholder="请输入使用的数据库名称(字母开头，其后可跟字母、数字、_),如果存在则先删除，否则创建，比如:lzzcms">
		                            </div>
		                        </div>
		                         <div class="form-group">
		                            <label  class="col-sm-2 control-label">数据库用户名:<i class="necessary">*</i></label>
		                            <div class="col-sm-10">
		                                <input type="text" class="form-control" name="dbAccount" placeholder="请输入数据库用户名,比如：root">
		                            </div>
		                        </div>
		                         <div class="form-group">
		                            <label  class="col-sm-2 control-label">数据库密码:<i class="necessary">*</i></label>
		                            <div class="col-sm-10">
		                                <input type="password" class="form-control" name="dbPwd" placeholder="请输入数据库密码,比如:root">
		                            </div>
		                        </div>
		                        <div class="form-group">
		                            <label  class="col-sm-2 control-label">后台地址配置:<i class="necessary">*</i></label>
		                            <div class="col-sm-10">
		                                <input type="text" class="form-control" name="backstage" placeholder="配置安装成功后的后台访问地址,如填写'xxx',则后台访问地址形如:http://localhost:8080/lzzcms/xxx">
		                            </div>
		                        </div>
		                         <div class="form-group">
		                            <label  class="col-sm-2 control-label">后台管理员用户名:<i class="necessary">*</i></label>
		                            <div class="col-sm-10">
		                                <input type="text" class="form-control" name="adminName" placeholder="用于安装成功后登录后台的用户名">
		                            </div>
		                        </div>
		                         <div class="form-group">
		                            <label  class="col-sm-2 control-label">管理员真实姓名:<i class="necessary">*</i></label>
		                            <div class="col-sm-10">
		                                <input type="text" class="form-control" name="adminRealName" placeholder="真实姓名">
		                            </div>
		                        </div>
		                         <div class="form-group">
		                            <label  class="col-sm-2 control-label">后台管理员密码:<i class="necessary">*</i></label>
		                            <div class="col-sm-10">
		                                <input type="password" class="form-control" name="adminPwd" placeholder="用于安装成功后登录后台的密码">
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
	$('input[name=dbPort]').poshytip({
		alignTo: 'target',
		alignX: 'right',
		alignY: 'center',
		offsetX: 5,
		showOn:"none",
		content:function(){
			return "必须是数字";
		}
	});
	$('input[name=dbPort]').focus(function(){
		$('input[name=dbPort]').poshytip("hide");
	});

	$("#next").click(function(){
		var necessary=true;
		var reg=/(.+):\*/;
		$("input.form-control").each(function(i,v){//必填校验
			if(!$(v).val()){
				var txt=$(v).parent().prev("label").text();
				var arr=reg.exec(txt);
				txt=arr[1];
				showTipModal(txt+"必填");
				necessary=false;
				return false;
			}
		});
		if(necessary){
			//端口是数字校验
			if(!gt0Int($('input[name=dbPort]').val())){
				$('input[name=dbPort]').poshytip("show");
				return;
			}
		}
		
		if(necessary){//提交
			$.ajax({
				type:"post",
				url:"<%=request.getContextPath()%>/install?flag=cmtDbInfo",
			    data:$("form").serialize(),
			    beforeSend:function(){
			    	$(".backdroplayer").removeClass("out");
					$(".backdroplayer").addClass("in");
			    },
			    complete:function(){
			    	$(".backdroplayer").removeClass("in");
					$(".backdroplayer").addClass("out");
			    },
			    success:function(data){
			    	if("ok"==data){
						location.href="<%=request.getContextPath()%>/install?flag=done&backstage="+$("input[name=backstage]").val();
					}else{
						showTipModal(data);
					}
			    }
			});
		}////提交完成
	});
	$("#stepbar ol li:lt(2)").removeClass();
	$("#stepbar ol li:lt(2)").addClass("step-done");
	$("#stepbar ol li").eq(2).addClass("step-active");
	//检验是数字
	function gt0Int(num){
		var reg=/^\d{1,}$/g;
		if(!reg.test(num)){
			return false;
		}
		return true;
	}
	function showTipModal(tip){
		$("#tip").text(tip);
		$('#tipModal').modal("show");
	}
</script>
</body>
</html>