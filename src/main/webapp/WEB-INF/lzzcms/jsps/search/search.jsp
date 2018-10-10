<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="zdw" uri="/lzzcms" %>    
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1,maximum-scale=1, user-scalable=no">
	 <link rel="shortcut icon" href="<zdw:tpl_path/>/imgs/favicon.ico" type="image/x-icon">
    <title>搜索结果-乐之者cms</title>
    <meta name="keywords" content="搜索结果-乐之者cms" />
    <meta name="description" content="搜索结果-乐之者cms" />
    <link href="<zdw:tpl_path/>/bootstrap-3.3.5-dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="<zdw:tpl_path/>/style/common.css" rel="stylesheet">
	 <script src="<zdw:tpl_path/>/bootstrap-3.3.5-dist/js/jquery.js"></script>
    <script src="<zdw:tpl_path/>/bootstrap-3.3.5-dist/js/bootstrap.min.js"></script>
    <script src="<zdw:tpl_path/>/js/app.js"></script>
    <style type="text/css">
    	.oneitem{
    		border:0;
    		height: 110px;
    		border-bottom: 1px solid #eee;
    	}
    	.conttitle{
    		font-size: 16px;
    	}
    	.contintro{
    		display: table-cell;
    		vertical-align: middle;
    		line-height: 25px;
    		margin-top: 4px;
    		text-indent: 1cm;
    	}
    	.panel{
    		margin-bottom: 5px;
    	}
    </style>
  </head>
  <body>
	  <!--导航条-->
 	<nav class="navbar navbar-default navbar-fixed-top" id="mynavbar">
	  <div class="container">
				<div class="navbar-header">
					<h1 class="lzztitle">
						<a  href="<zdw:index_path/>" class="navbar-brand lzztitletxt">乐之者java</a>
					</h1>
					<button id="iconbar" type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#mynavbarcollapse" >
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
				    </button>
				</div>
		     <div class="collapse navbar-collapse" id="mynavbarcollapse">
					<ul class="nav navbar-nav navbar-left">
						<li><a href="<zdw:index_path/>"><i class="glyphicon glyphicon-home"></i> <zdw:field name="indexname"/></a></li>
					<zdw:nav type="top">
						<li><a href="<zdw:field name='url'/>"> <zdw:field name='personalstyle'/>  <zdw:field name="name"/> </a></li>
					</zdw:nav>	
				    </ul> 
				    <form class="navbar-form navbar-right" action="<zdw:index_path/>/search" method="post">
				        <div class="form-group">
				          <input type="text" class="form-control" name="queryString" placeholder="请输入要搜索的内容">
				        </div>
				        <button type="submit" class="btn btn-default">查询</button>
					</form>
			     </div>
	  </div>
	</nav>
    <div class="container">
	    <div class="row">
	    	<div class="col-lg-9 col-md-9 col-sm-9 col-xs-9">
	    		<zdw:results>
	    		    <div   class="panel panel-info oneitem">
					  <div class="panel-body">
					  		<div class="row">
								<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
									<a href='<zdw:searchitem name="docurl"/>' class="conttitle">
					    			    <zdw:searchitem name="title"/> 
					    			</a>
								</div>
								<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 contintro">
									   <zdw:searchitem name="intro"/>
								</div>
						    </div>
					  </div>
					</div>
	    		</zdw:results>
	    	</div>
		</div>	
		<div class="row">
		<div class="col-md-12">
			<nav class=" navbar navbar-default  navbar-fixed-bottom footnavbar">
					<footer>
				        <p>友情链接：
				        <zdw:flinks type="text">
				        	<a href='<zdw:flink name="url"/>'><zdw:flink name="linkdesc"/></a>	
				        </zdw:flinks></p>
						<p>版权：<zdw:field name="powerby"/> 备案号:<zdw:field name="record"/></p>
				    </footer>
			</nav>
		</div>
</div>
	</div>
  </body>
</html>