<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="zdw" uri="/lzzcms" %> 
 <script type="text/javascript">
 function fileSelect() {
     document.getElementById("toUp_comm_thumbpic_file").click(); 
 }
 //只提交缩略图
 function fileSelected() {
	 $('#toUp_form').form({    
		    url:'trueUpContnet',   
		    onSubmit:function(param){
		    	$.messager.progress({msg:"正在提交....",title:"请稍等"});
		    },
		    success:function(data){    
		    	data=$.parseJSON(data);
		    	$.messager.progress("close");
		        if(data.status=="success"){
		        	$("#plusimg").attr("src","<%=request.getContextPath() %>"+data.info);
		        	$("#toUp_comm_thumbpic_new").val(data.info);
		        }else{
		        	$.messager.alert('错误',"修改缩略图出错，请查看系统日志",'error');
		        }
		    }    
		});    
		$('#toUp_form').submit(); 
 }
$(function(){
	$("#toUp_channel_id2").combobox({    //模型下拉框
	    valueField:'channel_id',    
	    textField:'channelname',
	    value:'${commInfo.channel_id }',
	    data:[{channel_id:'${commInfo.channel_id }',channelname:'${commInfo.channelname }'}],
	    disabled:true,
	    height:30,
	    onLoadSuccess:function(){
	    	var arr=$("#toUp_channel_id2").combobox("getData");
	    	var channel_id=arr[0].channel_id;
	    	var comm_id="${commInfo.comm_id}";
	    	$("#toUp_extral").load('toUpExtral',{
	    		'channel_id':channel_id,'comm_id':comm_id});
	    }
	});  
	$("#toUp_column_id").combobox({//栏目下拉框
		valueField:'id',
		textField:'name',
		value:'${commInfo.column_id }',
		url:"getColumnByChanelId?chanelId=${commInfo.channel_id }"+",toUpdate",
		height:30
	});
	$("#toUp_comm_authorname").combobox({//作者下拉框
		 url:'getAuthorForCombobox?frompage=toUpdate',    
		    valueField:'id',    
		    textField:'author_name',
		    editable:false,
		    height:30,
		    onLoadSuccess:function(){
		    	var nowAuthorId="${commInfo.comm_author}";
		    	console.log(nowAuthorId);
		    	var arr=$("#toUp_comm_authorname").combobox("getData");
		    	console.log(arr);
		    	$.each(arr,function(i,obj){
		    		if(obj["id"]==nowAuthorId){
		    			$("#toUp_comm_authorname").combobox("select",nowAuthorId);
		    		}
		    	});
		    }
	});
	$("#toUp_comm_srcname").combobox({//来源下拉框
		 url:'getSrcForCombobox?frompage=toUpdate',    
		    valueField:'id',   
		    height:30,
		    textField:'come_from',
		    editable:false,
		    onLoadSuccess:function(){
		    	var nowAuthorId="${commInfo.comm_src}";
		    	var arr=$("#toUp_comm_srcname").combobox("getData");
		    	$.each(arr,function(i,obj){
		    		if(obj["id"]==nowAuthorId){
		    			$("#toUp_comm_srcname").combobox("select",nowAuthorId);
		    		}
		    	});
		    }
	});
	$("#toUp_comm_defineflagname").combobox({//自定义标记下拉框
		 url:'getDefineFlagForCombobox?frompage=toUpdate',    
		    valueField:'en_name',    
		    textField:'define_flag',
		    editable:false,
		    height:30,
		    onLoadSuccess:function(){
		    	var nowAuthorId="${commInfo.comm_defineflag}";
		    	var arr=$("#toUp_comm_defineflagname").combobox("getData");
		    	$.each(arr,function(i,obj){
		    		if(obj["en_name"]==nowAuthorId){
		    			$("#toUp_comm_defineflagname").combobox("select",nowAuthorId);
		    		}
		    	});
		    }
	});
	$("#toUp_submit").click(function(){
		$("#toUp_comm_thumbpic_file").val("");
		$('#toUp_form').form({    
		    url:'trueUpContnet',   
		    onSubmit:function(){
		    	if(!$.isNumeric($("#toUp_comm_click").val())){
		    		$.messager.alert('错误',"点击量需要整数!",'error');
		    		return false;
		    	}
		    	$.messager.progress({msg:"正在提交....",title:"请稍等"});
		    },
		    success:function(data){  
		    	data=$.parseJSON(data);
		    	$.messager.progress("close");
		        if(data.status=="success"){
		        	$.messager.alert('提示','更新成功!','info');
		        }else{
		        	$.messager.alert('错误',"更新文档出错",'error');
		        }
		    }    
		});    
		$('#toUp_form').submit();  
	});
});
</script>
<style>
	.plus{
            display:inline-block;
            width: 94px;
            height: 94px;
            line-height: 94px;
            border:1px solid #ccc;
            text-align: center;
            cursor: pointer;
            overflow: hidden;
        }
       .plus img{
            width: 92px;
        }
	.toUp_table{
	    width:100%;
	    padding:0;
	    margin:0;
	}
	.toUp_table tr{
	    height:35px;
	}
	.toUp_table label{
		width:90px;
	    display: inline-block;
	}
	.toUp_table textarea{
		 width:100%;
		height:80px;
		border-radius:4px;  
	    border:1px solid rgba(102,175,233,.75); 
	    font-size:13px;
	    outline:none;  
	}
	textarea#toUp_comm_desc{
		height:45px;
	}
	.toUp_table textarea:focus{
		 box-shadow:0 0 4px rgba(102,175,233,.5);  
	    transition: box-shadow  .5s;  
	}
	.toUp_table td{
		text-align:left;
		padding:0;
		margin:0;
	}
	#toUpdateContainer{
		padding:5px 10px ;
	}
	#toUpdateContainer h3{
		background: #ccc;
		font-weight: 400;
		font-size:17px;
		font-family:"微软雅黑";
		height:30px;
		line-height:30px;
	}
	#toUp_form{
		border: 1px solid #ccc;
	}
</style>
<div id="toUpdateContainer" > 
   	<h3>共有字段</h3>
	 <form action="" method="post" id="toUp_form" enctype="multipart/form-data">
	 <input type="hidden" value="${commInfo.comm_id}" id="toUp_comm_id"  name="toUp_comm_id" />
	 <input type="hidden" value="${commInfo.channel_id}" id="toUp_channel_id"  name="toUp_channel_id" />
	 	<table  class="toUp_table" >
		  <tr>
		    <td><label for="toUp_channel_id2">模型</label>
		    <input type="text" id="toUp_channel_id2"  name="toUp_channel_id2"/>   
		    </td>
		    <td><label for="toUp_column_id">栏目</label>
		    <input type="text" id="toUp_column_id" name="toUp_column_id"  class="easyui-combobox"/>   
		    </td>
		   <td>
		   		<table  style="width: 100%"  cellspacing="0">
		   			<tr>
		   				<td style="width:90px;"><label  for="toUp_comm_title">标题</label></td>
		   				<td><input type="text" class="easyui-textbox" id="toUp_comm_title" name="toUp_comm_title" style="width:90%;height:30px;" value="${commInfo.comm_title }"/></td>
		   			</tr>
		   		</table>
		   </td>
		  </tr>
		  <tr>
		    <td><label for="toUp_comm_shorttitle">简短标题</label><input  style="height:30px;" class="easyui-textbox"  type="text" name="toUp_comm_shorttitle" id="toUp_comm_shorttitle" value="${commInfo.comm_shorttitle}"/></td>
		    <td><label for="toUp_comm_click">点击量</label><input style="height:30px;" class="easyui-numberbox"  type="text" name="toUp_comm_click" id="toUp_comm_click" value="${commInfo.comm_click }"/></td>
		    <td><label for="toUp_comm_authorname">作者</label>
		    <input type="text" name="toUp_comm_authorname" id="toUp_comm_authorname" />   
		    </td>
		  </tr>
		  <tr>
		   <td><label for="toUp_comm_publishdate">发布日期</label><input class="easyui-datebox" type="text" name="toUp_comm_publishdate" id="toUp_comm_publishdate" 
		   data-options="value:'${commInfo.comm_publishdate }',height:30" disabled="disabled"/></td>
		   <td><label for="toUp_comm_defineflagname">自定义标记</label>
		    <input type="text" name="toUp_comm_defineflagname" id="toUp_comm_defineflagname" />   </td>
		    <td><label for="toUp_comm_srcname">来源</label>
		    <input type="text" name="toUp_comm_srcname" id="toUp_comm_srcname"/>   </td>
		  </tr>
		  <tr>
		    <td colspan="2"><label for="toUp_comm_keywords">网页关键字</label>
		    <input  class="easyui-textbox" style="height:30px;width:550px;" type="text" name="toUp_comm_keywords"  id="toUp_comm_keywords" value="${commInfo.comm_keywords }"/></td>
		  </tr>
		  <tr>
		  	<td valign="top" colspan="3">
		  		<table>
		  			<tr>
		  				<td><label>缩略图</label></td>
		  				<td>
					  	 	<span class="plus">
					            <img id="plusimg" onclick="fileSelect()"  data-path=""
					                          src="<%=request.getContextPath() %>${commInfo.imgshow }">
					              <input type="hidden" name="toUp_comm_thumbpic_org"  id="toUp_comm_thumbpic_org" value="${commInfo.comm_thumbpic }"/>
					              <input type="hidden" name="toUp_comm_thumbpic_new"  id="toUp_comm_thumbpic_new" value="${commInfo.comm_thumbpic }"/>
			         			<input type="file" name="toUp_comm_thumbpic_file" id="toUp_comm_thumbpic_file"
			        		 		 style="display:none;" onchange="fileSelected()"/>
					        </span>
					  	 </td>
		  			</tr>
		  		</table>
		  	</td>	
		  </tr>
		  <tr>
		    <td colspan="3">
			    <table  style="width: 100%;"  cellspacing="0">
			   			<tr>
			   				<td style="width:90px;"> <label for="toUp_comm_desc">网页描述</label></td>
			   				<td style="width:100%;"><textarea style="width:95%" name="toUp_comm_desc" id="toUp_comm_desc" >${commInfo.comm_desc }</textarea></td>
			   			</tr>
			   	</table>
		    </td>
		   </tr> 
		  <tr>
		    <td colspan="3">
		    	 <table  style="width: 100%;"  cellspacing="0">
			   			<tr>
			   				<td style="width:90px;"><label for="toUp_comm_intro">摘要</label></td>
			   				<td style="width:100%;"><textarea style="width:95%" name="toUp_comm_intro" id="toUp_comm_intro" >${commInfo.comm_intro }</textarea></td>
			   			</tr>
			   	</table>
		   	</td>
		  </tr>
		</table>
		<!-- 这里根据模型的不同显示不同的模型附加字段 -->
		<div id="toUp_extral"></div>
		<zdw:hasRight url='trueUpContnet'>
			<div class="divcenter">
				<a href="#" id="toUp_submit"  class="easyui-linkbutton" 
				data-options="iconCls:'icon-ok'" >提交</a>
			</div>
		</zdw:hasRight>
	 </form>
</div> 