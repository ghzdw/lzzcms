<%@ page language="java" contentType="text/html; charset=utf-8"  pageEncoding="utf-8"%>
<%@ taglib prefix="zdw" uri="/lzzcms" %> 
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>乐之者cms-栏目静态化</title>
<link rel="stylesheet" href="<%=request.getContextPath() %>/resources/easyui/themes/default/easyui.css">
<link rel="stylesheet" href="<%=request.getContextPath() %>/resources/easyui/themes/icon.css">
<link href="<%=request.getContextPath()%>/resources/css/reset.css" rel="stylesheet">
<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/lzzcmsstyle.css">
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-1.10.2.js"></script>
<script src="<%=request.getContextPath() %>/resources/easyui/jquery.easyui.min.js"></script>
<script src="<%=request.getContextPath() %>/resources/easyui/easyui-lang-zh_CN.js"></script>
<script type="text/javascript">
$(function(){  
	$("#toMkCln_make").on("click",function(){
		$.messager.progress({msg:"正在生成....",title:"请稍等"});
		var val = $('#makeCln_column_id').combotree('getValue');
		$.ajax({
			type:"post",
			data:{"clnid":val},
			url:'makeCln',
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
	$("#toMkClnIncr_make").on("click",function(){
		$.messager.progress({msg:"正在生成....",title:"请稍等"});
		var val = $('#makeCln_column_id').combotree('getValue');
		$.ajax({
			type:"post",
			data:{"clnid":val,"type":"incr"},
			url:'makeClnIncr',
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
	$('#makeCln_column_id').combotree({    
	    url: 'getColumns',    
	    valueField:'id',    
	    textField:'name',
	    value:0
	});  
});
</script>
<style type="text/css">
	.top{
		padding:10px;
		width:800px;
		margin: 0 auto;
		text-align: center;
	}
	.inline{
		display: inline-block;
	}
	#makeCln_column_id{
		width:200px;
	}
	.divcenter{
		width:300px;
	}
</style>
</head>
<body>
	<div class="top">
	    <span>选择要生成的栏目:</span>
	    <span><input type="text" id="makeCln_column_id" name="makeCln_column_id" style="height: 30px;"/></span>
	</div>	
	<div class="divcenter">  
		<zdw:hasRight url='makeClnIncr'>
		  <span class="inline"> 
			<a href="javascript:void(0)" class="easyui-linkbutton"    id="toMkClnIncr_make">栏目增量静态化</a>
		  </span>	
		</zdw:hasRight>
		<zdw:hasRight url='makeCln'>
		  <span class="inline"> 
			<a href="javascript:void(0)" class="easyui-linkbutton"    id="toMkCln_make">栏目静态化</a>
		  </span>	
		</zdw:hasRight>
	</div>	
</body>
</html>