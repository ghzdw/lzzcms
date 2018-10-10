<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="zdw" uri="/lzzcms" %> 
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<link rel="stylesheet" href="<%=request.getContextPath() %>/resources/easyui/themes/default/easyui.css">
<link rel="stylesheet" href="<%=request.getContextPath() %>/resources/easyui/themes/icon.css">
<link href="<%=request.getContextPath() %>/resources/css/reset.css" rel="stylesheet">
<link rel="stylesheet" href="<%=request.getContextPath() %>/resources/css/lzzcmsstyle.css">
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-1.10.2.js"></script>
<script src="<%=request.getContextPath() %>/resources/easyui/jquery.easyui.min.js"></script>
<script src="<%=request.getContextPath() %>/resources/easyui/easyui-lang-zh_CN.js"></script>
<script src="<%=request.getContextPath() %>/resources/js/functions-lzzcms-lib.js"></script>
 <script type="text/javascript">
$(function(){
	$("#pageCfg_submit").click(function(){
		var json=str2Json("#pageCfg_form");
		$.ajax({
			type:"post",
			url:"updatePageCfg",
			contentType:"application/json", 
			data:JSON.stringify(json),
			success:function(result){
				if(result.info=="ok"){
					$.messager.alert("提示","修改成功!","info");
				}else if(result.info=="error"){
					 $.messager.alert("失败",result.errinfo,"error");
				}
			}
		});
	});
	
});
</script>
<style>
body input,body textarea{
	font-family: Aria;
	font-size: 14px;
	padding-left:7px;
}
	.pageCfg_table{
		margin:0 auto;
		margin-top:20px;
		width:90%;
	}
	.pageCfg_table tr{
		 height:35px;
	    line-height:35px;
	}
	.pageCfg_table td:nth-child(1){
		padding-right:20px;
		text-align: right;
		width:100px;
	}
	.pageCfg_table tr:last-child>td{
		text-align: center;
	}
	.pageCfg_table input{
		width:50%;
		outline:none;
		font-weight:normal;
		height:30px;
		border-radius:4px;  
	    border:1px solid rgba(102,175,233,.75); 
	}
	.pageCfg_table input:focus{
	    border-color:rgba(102,175,233,.75);  
	    box-shadow:0 0 4px rgba(102,175,233,.5);  
	    transition:border  .2s, box-shadow  .5s;  
	}
	.pageCfg_table textarea{
		width:100%;
		height:80px;
		border-radius:4px;  
	    border:1px solid rgba(102,175,233,.75); 
	    outline:none;   
	}
	.pageCfg_table textarea:focus{
		outline:none;  
	    border-color:rgba(102,175,233,.75);  
	    box-shadow:0 0 4px rgba(102,175,233,.5);  
	    transition:border  .2s, box-shadow  .5s;  
	}
</style>
</head>
<body>
<div id="pagecfg_container" > 
	 <form action="" method="post" id="pageCfg_form">
	 	<table  class="pageCfg_table" >
		  <tr>
		    <td>
		       <label for="pageCfg_indexname" >主页名称:</label>
		    </td>
		    <td>   
		       <input type="text" value="${requestScope.indexname }" id="pageCfg_indexname" name="pageCfg_indexname"/>
		    </td>
		  </tr>
		  <tr>  
		     <td>
		       <label for="pageCfg_indextitle">主页标题:</label>
		        </td>
		    <td><input type="text"  value="${requestScope.indextitle }"  id="pageCfg_indextitle"  name="pageCfg_indextitle"/>
		    </td>
		  </tr>  
		  <tr>  
		     <td>
		       <label for="pageCfg_indexkeys">主页关键字:</label>
		        </td>
		    <td><textarea  class="textarea"  id="pageCfg_indexkeys"  name="pageCfg_indexkeys">${requestScope.indexkeys }</textarea>
		    </td>
		   </tr>
		   <tr>     
		     <td>
		       <label for="pageCfg_indexdesc">主页描述:</label>
		        </td>
		    <td><textarea    class="textarea" id="pageCfg_indexdesc" name="pageCfg_indexdesc">${requestScope.indexdesc }</textarea>
		    </td>
		   </tr>   
		   <tr>     
		     <td>
		       <label for="pageCfg_powerby">版权信息:</label>
		        </td>
		    <td><input type="text"  id="pageCfg_powerby" value="${requestScope.powerby }" name="pageCfg_powerby"/>
		    </td>
		   </tr>  
		      <tr>     
		     <td>
		       <label for="pageCfg_record">备案信息:</label>
		        </td>
		    <td>
		    	<input type="text"  id="pageCfg_record" value="${requestScope.record }" name="pageCfg_record"/>
		    </td>
		   </tr>  
		   <zdw:hasRight url='updatePageCfg'>
			   <tr>
			   			<td colspan="2">
			   				<a href="#" id="pageCfg_submit"  class="easyui-linkbutton" data-options="iconCls:'icon-ok'" >提交</a>
			   			</td>
			   </tr>
		   </zdw:hasRight>
		   </table>
	 </form>
</div> 
</body>
</html>