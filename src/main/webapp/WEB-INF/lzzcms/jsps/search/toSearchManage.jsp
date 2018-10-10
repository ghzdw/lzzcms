<%@ page language="java" contentType="text/html; charset=utf-8"  pageEncoding="utf-8"%>
<%@ taglib prefix="zdw" uri="/lzzcms" %> 
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<link rel="stylesheet" href="<%=request.getContextPath() %>/resources/easyui/themes/default/easyui.css">
<link rel="stylesheet" href="<%=request.getContextPath() %>/resources/easyui/themes/icon.css">
<link href="<%=request.getContextPath() %>/resources/css/reset.css" rel="stylesheet">
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-1.10.2.js"></script>
<script src="<%=request.getContextPath() %>/resources/easyui/jquery.easyui.min.js"></script>
<script src="<%=request.getContextPath() %>/resources/easyui/easyui-lang-zh_CN.js"></script>
<script type="text/javascript">
$(function(){  
	$("#manualClear").on("click",function(){
		$.messager.progress({msg:"正在清除缓存....",title:"请稍等"});
		$.ajax({
			type:"post",
			url:'manualClear',
			success:function(result){
				$.messager.progress("close");
				$.messager.alert("结果反馈",result.info,result.type);
			}
		});
	});
});
</script>
<style>
	.center{
		text-align: center;
		padding-top:50px;
	}
	.tiptxt{
		font-size: 14px;
		line-height:1.4;
		width:80%;
		margin: 0 auto;
		background: #eee;
		border:1px solid #bbb;
		border-radius:5px;
		padding:10px;
		position: relative;
		margin-top: 60px;
		border-top-left-radius:0;
	}
	.tipflag{
		position: absolute;
		top:-37px;
		left:-1px;
		height: 16px;
		width:50px;
		padding:10px;
		border-radius:5px;
		background: rgb(200,250,200);
		 border-bottom-left-radius:0;
		 border-bottom-right-radius:0;
	}
	.tipflag i{
		width: 16px;
		height: 16px;
		display: inline-block;
		position: absolute;
		left:5px;
	}
	.msg{
	   position: absolute;
	   left:22px;
	}
</style>
</head>
<body>
	<div class="tiptxt">
		<span class="tipflag"><i class="icon-tip2"></i><span class="msg">提示</span></span>
		&nbsp;&nbsp;&nbsp;&nbsp;当生成新的html文件(即静态化)之后，需要执行"清除缓存"之后才能在搜索中搜索到新增加的内容，否则搜索不到。因为每次更新会扫描所有的html文件，
		所以当你的html文件比较多时，可能会耗费几分钟的时间，请耐心等待。
	</div>
	<zdw:hasRight url='manualClear'>
		<div class="center">
			<a href="javascript:void(0)"  data-options="iconCls:'icon-broom'" 
			 class="easyui-linkbutton" id="manualClear">清除缓存</a>
		</div>
	</zdw:hasRight>
</body>
</html>
