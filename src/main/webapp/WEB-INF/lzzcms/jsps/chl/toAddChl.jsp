<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="zdw" uri="/lzzcms" %> 
<script type="text/javascript">
$(function(){
	$("#toAddChl_commit").click(function(){
		var toAddChl_channelName=$('#toAddChl_channelName').val();
		var toAddChl_enName=$('#toAddChl_enName').val();
		var toAddChl_commonTable=$('#toAddChl_commonTable').val();
		var toAddChl_additionalTable=$('#toAddChl_additionalTable').val();
		var params={};
		params["channelName"]=toAddChl_channelName;
		params["enName"]=toAddChl_enName;
		params["commonTable"]=toAddChl_commonTable;
		params["additionalTable"]=toAddChl_additionalTable;
		//第一种方法：ajax结合springmvc在形参里面转javaben
		$.ajax({
			type:"post",
			url:$("#toAddChl_form").attr("action"),
			contentType:"application/json", 
			data:JSON.stringify(params),
			success:function(result){
				if(result.info=="ok"){
					$.messager.alert("提示消息","添加模型成功!","info");
					resetCon();
				}else{
					$.messager.alert("提示消息",result.info,"error");
				}
			}
		});
	});
});
function toAddChl_resetCon(){
	$('#toAddChl_channelName').val("");
	$('#toAddChl_enName').val("");
	$('#toAddChl_additionalTable').val("");
}
</script>
<style>
	.toAddChl_table{
	    width:70%;
	    padding:0;
	    margin:0 auto;
	    margin-top:10px;
	}
	.toAddChl_table tr{
	    height:35px;
	    line-height:35px;
	}
	.toAddChl_table label{
		width:90px;
	    display: inline-block;
	}
	.toAddChl_table input{
		width:170px;
		outline:none;
		font-size:14px !important;
		font-weight:normal;
	}
	.toAddChl_table td{
		text-align:left;
		padding:0;
		margin:0;
	}
	.toAddChl_table td:last-child{
		text-align:center;
	}
	.toAddChl_table td:nth-child(2n+1){
		padding-left:30px;
		width:80px;
	}
</style>
<div class="toAddChl_container">
	<form  method="post" id="toAddChl_form" action="trueAddChannelInfo">
	  <table class="toAddChl_table" >
	  		<tr>
	  			<td><label for="toAddChl_channelName">频道名称：</label></td>
	  			<td><input id="toAddChl_channelName" type="text" class="easyui-textbox"  name="toAddChl_channelName" 
   		style="height:30px" value="${requestScope.ret.channelName }"/></td>
	  			<td><label  for="toAddChl_enName">模版标识：</label></td>
	  			<td><input id="toAddChl_enName" type="text" class="easyui-textbox"   name="toAddChl_enName" 
  		    style="height:30px" value="${requestScope.ret.enName}"/></td>
	  		</tr>
	  		<tr>
	  			<td><label class="control-label">公用表名：</label></td>
	  			<td><input  type="text"   class="easyui-textbox"  value="${requestScope.ret.commonTableName }" 
        style="height:30px" disabled="disabled"/></td>
	  			<td><label  for="toAddChl_additionalTable" >附加表名：</label></td>
	  			<td><input id="toAddChl_additionalTable" type="text" name="toAddChl_commonTable"  class="easyui-textbox"  
        style="height:30px" value="${requestScope.ret.additionalTableName }"/></td>
	  		</tr>
	  		<zdw:hasRight url="trueAddChannelInfo">
		  		<tr>
		  			<td colspan="4">
			           <button type="button"  id="toAddChl_commit" class="easyui-linkbutton" data-options="iconCls:'icon-ok'">提交</button>
		  			</td>
		  		</tr>
	  		</zdw:hasRight>
	  </table>
	    <input id="toAddChl_commonTable" type="hidden" name="toAddChl_commonTable"  value="${requestScope.ret.commonTableName }" />
  	 </form>
</div>
