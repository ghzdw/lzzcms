<%@page import="java.util.*"%>
<%@page import="java.util.Map.Entry"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="zdw" uri="/lzzcms" %> 
 <script type="text/javascript">
 function fileSelect() {
     document.getElementById("toAdd_comm_thumbpic_file").click(); 
 }
 //只提交缩略图
 function fileSelected() {
	 $('#toAdd_form').form({    
		    url:'trueAddContnet',   
		    onSubmit:function(param){
		    	$.messager.progress({msg:"正在提交....",title:"请稍等"});
		    },
		    success:function(data){    
		    	data=$.parseJSON(data);
		    	$.messager.progress("close");
		        if(data.status=="success"){
		        	$("#plusimg").attr("src","<%=request.getContextPath() %>"+data.info);
		        	$("#toAdd_comm_thumbpic").val(data.info);
		        }else{
		        	$.messager.alert('错误',"添加缩略图出错，请查看系统日志",'error');
		        }
		    }    
		});    
		$('#toAdd_form').submit(); 
 }
 var toAddUe;
$(function(){
	function selectFirst(slector){
		var arr=$(slector).combobox("getData");
    	if(arr[0]){
	    	$(slector).combobox("select",arr[0].id||arr[0].en_name);
    	}else{
    		$(slector).combobox("clear");
    	}
	}
	$("#toAdd_channel_id").combobox({    //模型下拉框
	    url:'getForCombobox?page=toAdd',    
	    valueField:'id',    
	    textField:'channelname',
	    editable:false,
	    height:30,
	    onLoadSuccess:function(){
	    	selectFirst("#toAdd_channel_id");
	    },
	    onChange:function(newValue, oldValue){
	    		var param=newValue+",toAdd";
	    		$("#toAdd_column_id").combobox("reload","getColumnByChanelId?chanelId="+param);
	    		$("#toAdd_extral").load('toAddExtral',{'channel_id':newValue});
	    }
	});  
	$("#toAdd_column_id").combobox({//栏目下拉框
	    valueField:'id',    
	    textField:'name',
	    editable:false,
	    height:30,
	    onLoadSuccess:function(){
	    	selectFirst("#toAdd_column_id");
	    }
	});
	$("#toAdd_comm_src").combobox({//来源下拉框
		 url:'getSrcForCombobox?frompage=toAdd',    
	    valueField:'id',   
	    height:30,
	    textField:'come_from',
	    editable:false,
	    onLoadSuccess:function(){selectFirst("#toAdd_comm_src");}
	});
	$("#toAdd_comm_author").combobox({//作者下拉框
		 url:'getAuthorForCombobox?frompage=toAdd',    
		    valueField:'id',    
		    textField:'author_name',
		    editable:false,
		    height:30,
		    onLoadSuccess:function(){selectFirst("#toAdd_comm_author");}
	});
	$("#toAdd_comm_defineflag").combobox({//自定义标记下拉框
		    url:'getDefineFlagForCombobox?frompage=toAdd',    
		    valueField:'en_name',    
		    textField:'define_flag',
		    editable:false,
		    height:30,
		    value:"",
		    onLoadSuccess:function(){selectFirst("#toAdd_comm_defineflag"); }
	});
	$("#toAdd_submit").click(function(){//提交操作
		$("#toAdd_comm_thumbpic_file").val("");
		$('#toAdd_form').form({    
		    url:'trueAddContnet',   
		    onSubmit:function(param){
		    	if(!gt0Int($("#toAdd_comm_click").val())){
		    		$.messager.alert('错误','点击量需要正整数','error');
		    		return false;
		    	}
		    	if(!$("#toAdd_comm_title").val()){
		    		$.messager.alert('错误','标题必填!','error');
		    		return false;
		    	}
		    	$.messager.progress({msg:"正在提交....",title:"请稍等"});
		    },
		    success:function(data){    
		    	$.messager.progress("close");
		    	data=$.parseJSON(data);
		    	if(data.status=="success"){
		        	$.messager.alert('提示',data.info,'info');
		        	var tab = $('#centerArea').tabs('getSelected');  // 获取选择的面板
		        	tab.panel('refresh');
		        }else{
		        	$.messager.alert('错误',"添加文档出错",'error');
		        }
		    }    
		});    
		$('#toAdd_form').submit();  
	});
	//提示
	$('.help').poshytip({
        content:function () {
            var tip= "如果不填写,当内容主体输入区域失去焦点时,会根据其内容自动生成";
            return tip;
        }
    });
	//自动生成
	$(".autogenerate").click(function(){
		var txt =toAddUe.getContentTxt(); 
        if(txt){//设置关键字、网页描述、摘要
       		var descVal=txt.length>100?txt.substring(0,100):txt;
       		$("#toAdd_comm_desc").val(descVal);
             $.ajax({
     			type:"post",
     			url:"autoGeKeywordsAndIntro",
     			data:{"txt":txt},
     			success:function(result){
     				$("#toAdd_comm_keywords").val(result.key);
     				$("#toAdd_comm_intro").val(result.intro);
     			}
     		});
        }
	});
	//点击量初始化一个值
	$("#toAdd_comm_click").val(randomInt());
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
	.toAdd_table{
	    width:100%;
	    padding:0;
	    margin:0;
	}
	.toAdd_table tr{
	    height:35px;
	}
	.toAdd_table label{
		width:90px;
	    display: inline-block;
	    position: relative;
	}
	.toAdd_table textarea{
		width:100%;
		height:80px;
		border-radius:4px;  
	    border:1px solid rgba(102,175,233,.75); 
	    font-size:13px;
	    outline:none;  
	}
	textarea#toAdd_comm_desc{
		height:45px;
	}
	.toAdd_table textarea:focus{
	    box-shadow:0 0 4px rgba(102,175,233,.5);  
	    transition: box-shadow  .5s;  
	}
	.toAdd_table td{
		text-align:left;
		padding:0;
		margin:0;
	}
	#toAddContainer{
		padding:5px 10px ;
	}
	#toAddContainer h3{
		background: #ccc;
		font-weight: 400;
		font-size:17px;
		font-family:"微软雅黑";
		height:30px;
		line-height:30px;
	}
	#toAdd_form{
		border: 1px solid #ccc;
	}
	.toAdd_table label i.necessary{
		color:red;
		font-weight: bold;
		font-size: 17px;
		position: relative;
		top:5px;
	}
	.toAdd_table label i.help{
		position: absolute;
		top:3px;
		left:64px;
	}
	.toAdd_table label i.help img{
		width:14px;
		border:0 ;
		outline:0 none;
	}
	.mixtable{
		width: 100%;
		border:1px solid rgba(102,175,233,.5);
		padding:5px;
		padding-bottom: 0;
		margin-top:25px;
		border-top: 0;
		margin-left: 10px;
		position: relative;
	}
	#mixtbparenttd{
		padding-right: 20px;
	}
	.autogenerate{
		display: inline-block;
		position: absolute;
		top:0px;
		left: 40%;
		background: rgb(139,192,223);
		font-size:14px;
		color:black;
		padding-left: 10px;
		padding-right:10px;
		cursor: pointer;
		border-radius:20px;
	}
	.autogenerate:hover{
		text-decoration: none;
		background: rgba(139,192,223,.7);
	}
</style>
<div id="toAddContainer" >
   	<h3>共有字段</h3>
	 <form action="" method="post" id="toAdd_form" enctype="multipart/form-data">
	 	<table  class="toAdd_table" >
		  <tr>
		    <td><label for="toAdd_channel_id">模型</label><input type="text" id="toAdd_channel_id" name="toAdd_channel_id"/></td>
		    <td><label for="toAdd_column_id">栏目</label><input type="text" id="toAdd_column_id" name="toAdd_column_id"/></td>
		   <td>
		   		<table  style="width: 100%"  cellspacing="0">
		   			<tr>
		   				<td style="width:90px;"><label  for="toAdd_comm_title">标题<i  class="necessary">*</i></label></td>
		   				<td>
		   					<input class="bootstrapinput" type="text" id="toAdd_comm_title" name="toAdd_comm_title" style="width:90%;height:30px"/>
		   				</td>
		   			</tr>
		   		</table>
		   </td>
		  </tr>
		  <tr>
		    <td><label for="toAdd_comm_shorttitle">简短标题</label><input  class="bootstrapinput"  style="height:30px;"   type="text" name="toAdd_comm_shorttitle" id="toAdd_comm_shorttitle"/></td>
		    <td><label for="toAdd_comm_click">点击量<i class="necessary">*</i></label>
		        <input style="height:30px;"  type="text" name="toAdd_comm_click" id="toAdd_comm_click" class="easyui-numberbox" /></td>
		    <td><label for="toAdd_comm_author">作者</label><input type="text" name="toAdd_comm_author" id="toAdd_comm_author"/></td>
		  </tr>
		  <tr>
		    <td><label for="toAdd_comm_defineflag">自定义标记</label><input type="text" name="toAdd_comm_defineflag" id="toAdd_comm_defineflag"/></td>
		    <td><label for="toAdd_comm_src">来源</label><input type="text" name="toAdd_comm_src" id="toAdd_comm_src"/></td>
		  </tr>
		  <tr>
		  	<td valign="top" colspan="3">
		  		<table>
		  			<tr>
		  				<td><label>缩略图</label></td>
		  				<td>
					  	 	<span class="plus">
					            <img id="plusimg" onclick="fileSelect()"  data-path=""
					                          src="<%=request.getContextPath() %>/resources/imgs/plus.png">
					              <input type="file"  onchange="fileSelected()"  style="display:none;"
					              name="toAdd_comm_thumbpic_file" id="toAdd_comm_thumbpic_file" />   
					              <input type="hidden" value="/uploads/thumb/" name="toAdd_comm_thumbpic" id="toAdd_comm_thumbpic"/>
					        </span>
					  	 </td>
		  			</tr>
		  		</table>
		  	</td>	
		  </tr>
		  <tr>
		  	<td colspan="3" id="mixtbparenttd">
		  	  <table class="mixtable">
		  	  	 <tr><td><a class="autogenerate">点击根据主题内容自动生成网页关键字、网页描述、摘要</a></td></tr>	
				  <tr>
				  	<td><label for="toAdd_comm_keywords">网页关键字
				    	<i class="help"><img src="<%=request.getContextPath() %>/resources/imgs/question.png"></i>
				    </label><input  style="height:30px;width:550px;"  class="bootstrapinput"   type="text" name="toAdd_comm_keywords"  id="toAdd_comm_keywords"/>
				    </td>
				  </tr>
				  <tr>
				    <td>
					    <table  style="width: 100%;"  cellspacing="0">
					   			<tr>
					   				<td style="width:90px;"> <label for="toAdd_comm_desc">网页描述
					   				<i class="help"><img src="<%=request.getContextPath() %>/resources/imgs/question.png"></i>
					   				</label></td>
					   				<td style="width:100%;"><textarea  name="toAdd_comm_desc" id="toAdd_comm_desc" class="textarea"></textarea></td>
					   			</tr>
					   	</table>
				    </td>
				   </tr> 
				  <tr>
				    <td>
				    	 <table  style="width: 100%;"  cellspacing="0">
					   			<tr>
					   				<td style="width:90px;"><label for="toAdd_comm_intro">摘要
					   					<i class="help"><img src="<%=request.getContextPath() %>/resources/imgs/question.png"></i>
					   				</label></td>
					   				<td style="width:100%;"><textarea  name="toAdd_comm_intro" id="toAdd_comm_intro" class="textarea"></textarea></td>
					   			</tr>
					   	</table>
				   	</td>
				  </tr>
			  </table>
			</td>  
		 </tr> 
		</table>
		<!-- 这里根据模型的不同显示不同的模型附加字段 -->
		<div id="toAdd_extral"></div>
		<zdw:hasRight url='trueAddContnet'>
			<div class="divcenter">
				<a href="#" id="toAdd_submit"  class="easyui-linkbutton" 
				data-options="iconCls:'icon-ok'" >提交</a>
			</div>
		</zdw:hasRight>
	 </form>
</div> 