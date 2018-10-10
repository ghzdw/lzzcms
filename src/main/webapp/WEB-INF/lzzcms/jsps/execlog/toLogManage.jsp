<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>乐之者cms-日志管理</title>
    <link href="<%=request.getContextPath()%>/resources/css/reset.css" rel="stylesheet">
	<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/lzzcmsstyle.css">
	<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/easyui/themes/default/easyui.css">
	<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/easyui/themes/icon.css">
	<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-1.10.2.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/JSON2.js"></script>
	<script src="<%=request.getContextPath()%>/resources/easyui/jquery.easyui.min.js"></script>
	<script src="<%=request.getContextPath()%>/resources/easyui/easyui-lang-zh_CN.js"></script>
	<script src="<%=request.getContextPath()%>/resources/js/functions-lzzcms-lib.js"></script>
</head>
<body>
 <div id="grid"></div>
</body>
<script type="text/javascript">
$("#grid").datagrid({
	fit:true,
	striped:true,
	rownumbers:true,
	pagination:true,
	pageList:[10,20,30],
	pageSize:20,
	method:"post",
    columns:[[    
        {field:'execdate',title:'发生日期',halign:'center',sortable:true,resizable:true,width:200},    
        {field:'realname',title:'操作人',halign:'center',sortable:true,resizable:true,width:200},    
        {field:'execurldesc',title:'执行操作描述',halign:'center',sortable:true,resizable:true,width:200},    
        {field:'execurl',title:'操作地址',halign:'center',sortable:true,resizable:true,width:200},    
        {field:'exectype',title:'日志级别',halign:'center',sortable:true,resizable:true,width:200}   
    ]],    
	url:"listLogs",
	remoteSort:false,//利用下面的onSortColumn:function事件实现
	onSortColumn:function(sort,order){
		query({
			"sort":sort,
			"order":order
		});
	}
});
function query(obj){ //处理查询按钮 obj用来接收点击排序时候的参数
	var data={};
		if(obj){
			data["sort"]=obj["sort"];//排序的字段名称
			data["order"]=obj["order"];//排序方式
		}
		$("#grid").datagrid("options").queryParams={"param":JSON.stringify(data)};
		$("#grid").datagrid("load");
}	
	</script>
</html>















<script type="text/javascript">
	
	</script>
</html>

