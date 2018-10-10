<%@ page language="java" contentType="text/html; charset=utf-8"  pageEncoding="utf-8"%>
<%@ taglib prefix="zdw" uri="/lzzcms" %> 
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>乐之者cms-主页静态化</title>
<link rel="stylesheet" href="<%=request.getContextPath() %>/resources/easyui/themes/default/easyui.css">
<link rel="stylesheet" href="<%=request.getContextPath() %>/resources/easyui/themes/icon.css">
<link href="<%=request.getContextPath()%>/resources/css/reset.css" rel="stylesheet">
<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/lzzcmsstyle.css">
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-1.10.2.js"></script>
<script src="<%=request.getContextPath() %>/resources/easyui/jquery.easyui.min.js"></script>
<script src="<%=request.getContextPath() %>/resources/easyui/easyui-lang-zh_CN.js"></script>
<script type="text/javascript">
$(function(){  
	$("#toMkIndex_make").on("click",function(){
		$.messager.progress({msg:"正在生成....",title:"请稍等"});
		$.ajax({
			type:"post",
			url:'makeIndex',
			data:{"name":$("input[name=toMkIndex_indexTpl]").val()},
			success:function(result){
				$.messager.progress("close");
				if(result.info=="ok"){
					$.messager.alert("提示","生成完成!","info");
				}else if(result.info=="error"){
					 $.messager.alert("提示",result.errinfo,"error");
				}
			}
		});
	});
	$("#tpls").tree({
		url:"getTpls",
		lines:true,
		onClick: function(node){
			var url=node.attributes.url;
			if(url){
				$("input[name=toMkIndex_indexTpl]").val(url);
				$("#scanWin").window("close");
			}
		}
	});
	$("#scan").click(function(){
		$("#scanWin").window("open");
	});
});
</script>
<style>
	.toMkIndex_container{
		
	}
	.toMkIndex_container div{
		margin-top:5px;
		height:30px;
		line-height: 30px;
	}
	.toMkIndex_container .txetinput{
		width:300px;
		outline:none;
		font-size:14px !important;
		font-weight:normal;
		height:30px;
		border-left:0;
		border-top-right-radius:4px;  
		border-bottom-right-radius:4px;  
	    border-top:1px solid rgba(102,175,233,.75); 
	    border-right:1px solid rgba(102,175,233,.75); 
	    border-bottom:1px solid rgba(102,175,233,.75); 
	}
	.toMkIndex_container .txetinput:focus{
	    box-shadow:0 0 4px rgba(102,175,233,.5);  
	    transition: box-shadow  .5s;  
	}
	
	.toMkIndex_container .pre{
		height:30px;
		line-height: 30px;
		width:44px;
		background: rgba(102,175,233,.25);
		display: inline-block;
		border-top-left-radius:4px;  
		border-bottom-left-radius:4px; 
		border-right:0;
	    border-top:1px solid rgba(102,175,233,.75); 
	    border-left:1px solid rgba(102,175,233,.75); 
	    border-bottom:1px solid rgba(102,175,233,.75); 
	}
	.toMkIndex_container  .pos{
		width:600px;
		margin:10px auto;
	}
	.toMkIndex_container  .pos>span{
		float:left;
		margin-left: 14px;
	}
	.toMkIndex_container  .pos>span:nth-child(3){
		margin-left: 0px;
	}
	section{
		text-align: center;
	}
	
</style>
</head>
<body>
<div class="toMkIndex_container">
	<div class="pos">
			<span>主页模板:</span>
			<span class="pre">tpls/</span>
			<span><input type="text"  name="toMkIndex_indexTpl" value="index.html" class="txetinput"/></span>
			<span><a class="easyui-linkbutton"  id="scan" data-options="iconCls:'icon-search'">浏览...</a></span>
	</div>
	<zdw:hasRight url='makeIndex'>
		<section>
				<a href="javascript:void(0)" class="easyui-linkbutton"   id="toMkIndex_make">生成主页html</a>
		</section>
	</zdw:hasRight>
	
	<div id="scanWin" class="easyui-window" title="选择模板" style="width:350px;height:300px;overflow-y:auto;padding:10px;background:#fff;"   
        data-options="modal:true,collapsible:false,minimizable:false,closed:true">   
		 <div id="tpls"></div>
	</div> 
</div>
</body>
</html>