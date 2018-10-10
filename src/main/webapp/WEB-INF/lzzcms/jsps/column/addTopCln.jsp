<%@page import="java.util.*"%>
<%@page import="java.util.Map.Entry"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="zdw" uri="/lzzcms" %>    
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>乐之者cms-新增顶级栏目</title>
<link href="<%=request.getContextPath()%>/resources/css/reset.css" rel="stylesheet">
<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/lzzcmsstyle.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/easyui/themes/default/easyui.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/easyui/themes/icon.css">
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-1.10.2.js"></script>
<script src="<%=request.getContextPath()%>/resources/easyui/jquery.easyui.min.js"></script>
<script src="<%=request.getContextPath()%>/resources/easyui/easyui-lang-zh_CN.js"></script>
<script src="<%=request.getContextPath()%>/resources/js/functions-lzzcms-lib.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/ueditor/ueditor.config.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/ueditor/ueditor.all.min.js"></script>
<script type="text/javascript" charset="utf-8" src="<%=request.getContextPath()%>/resources/ueditor/lang/zh-cn/zh-cn.js"></script>
<script type="text/javascript">
	$(function() {
		//根据选择不同切换显示与隐藏
		function toggleShowAndHidden(selectedChlEnName,selectedClnTypeEnName){//article,list
			$("div[id^=dy_]").addClass("hiddenele");
			if (selectedClnTypeEnName == 'cover') {//封面
				$("#dy_cover").removeClass("hiddenele");
				$("#dy_keydesc").removeClass("hiddenele");
				$("#cover_mytpl").val("cover_"+ $("#enname").val() + ".html");
			} else if (selectedClnTypeEnName == 'list') {//列表
				$("#dy_list").removeClass("hiddenele");
				$("#dy_keydesc").removeClass("hiddenele");
				$("#list_mytpl").val("list_"+ $("#enname").val() + ".html");
				$("#list_contentTplName").val("content_"+ $("#enname").val() + ".html");
			} else if (selectedClnTypeEnName == 'outlink') {//外部
				$("#dy_outlink").removeClass("hiddenele");
			} else if (selectedClnTypeEnName == 'singlepage') {//单页面
				$("#dy_singlepage").removeClass("hiddenele");
				$("#singlepage_mytpl").val("single_page.html");
				$("#dy_keydesc").removeClass("hiddenele");
				$("#dy_singlepageContent").removeClass("hiddenele");
			} 
		}
		function getSelectedChlEnName(){//得到当前选中的频道的英文名
			var id = $("#channel_id").combobox("getValue");
			var arr = $("#channel_id").combobox("getData");
			var selectedChlEnName="";
			$.each(arr,function(i,obj){
				if(obj["id"]==id){
					selectedChlEnName=obj["enname"];
					return false;
				}
			});
			return selectedChlEnName;
		}
		function getSelectedClnTypeEnName(){//得到当前选中的栏目类别的英文名
			var id = $("#typename").combobox("getValue");
			var arr = $("#typename").combobox("getData");
			var selectedClnTypeEnName="";
			$.each(arr,function(i,obj){
				if(obj["id"]==id){
					selectedClnTypeEnName=obj["enName"];
					return false;
				}
			});
			return selectedClnTypeEnName;
		}
		$("#channel_id").combobox({ //模型下拉框
			url : 'getForCombobox?page=toAdd',
			valueField : 'id',
			textField : 'channelname',
			editable : false,
			height:30,
			onLoadSuccess : function() {
				var arr = $("#channel_id").combobox("getData");
				$("#channel_id").combobox("select",arr[0].id);
			},
			onChange : function(newValue, oldValue) {
				var selectedChlEnName=getSelectedChlEnName();
				var selectedClnTypeEnName=getSelectedClnTypeEnName();
				$("#enname").val(selectedChlEnName);
				toggleShowAndHidden(selectedChlEnName,selectedClnTypeEnName);
			}
		});
		//模板和表格初始化隐藏
		$("div[id^=dy_]").addClass("hiddenele");
		$("#typename").combobox({//栏目类型下拉框
			url : 'getClnTypeForCombobox',
			valueField : 'id',
			textField : 'typeName',
			editable : false,
			value : "0",
			height:30,
			onChange : function(newValue, oldValue) {
				var selectedChlEnName=getSelectedChlEnName();
				var selectedClnTypeEnName=getSelectedClnTypeEnName();
				toggleShowAndHidden(selectedChlEnName,selectedClnTypeEnName);
			}
		});
		$("#addTopCln_submit").click(function() {
				$('#form').form({
					url : 'trueAddTopColumn',
					onSubmit : function() {
						if (!gt0Int($("#orderNo").val())) {
							$.messager.alert('错误',"排序优先级需要整数!",'error');
							return false;
						}
						if ($("#typename").combobox("getValue") == 0) {
							$.messager.alert('错误',"请先选择栏目类别!",'error');
							return false;
						}
						$.messager.progress({msg : "正在提交....",title : "请稍等"});
					},
					success : function(data) {
						$.messager.progress("close");
						if (data == "success") {
							$.messager.alert('提示','提交成功!','info');
						} else {
							$.messager.alert('错误',data, 'error');
						}
					}
				});
				$('#form').submit();
			});
		//监听栏目名称失去焦点生成拼音首字母dir
		$("#name").blur(function() {
			var clnTitle = $("#name").val();
			if (clnTitle) {
				$.post('getDirByTopClnName',{'clnName' : clnTitle},function(data) {
						if (data.info == "error") {
							$.messager.alert('错误',data.errinfo,'error');
						} else if (data.info = "ok") {
							$("#htmlDir").val(data.dirName);
						}
					});
			}
		});
		$("#orderNo").val(randomInt());
		//模板选择对话框
		$("#cover_mytpl,#list_contentTplName,#list_mytpl,#singlepage_mytpl").click(function(e){
			$("#scanWin").attr("data-for",$(this).prop("id"));
			$("#scanWin").window({
				left:e.pageX,
				top:e.pageY
			});
			$("#scanWin").window("open");
		});
		$("#tpls").tree({
			url:"getTpls",
			lines:true,
			onClick: function(node){
				var url=node.attributes.url;
				if(url){
					$("#"+$("#scanWin").attr("data-for")).val(url);
					$("#scanWin").window("close");
				}
			}
		});
		
	});
</script>
<style type="text/css">
    #container{
		width:100%;
		margin: 0 auto;
	}
	dl{
	    height:30px;
	    padding:8px;
	    text-align: center;
	}
	dl dt,dl dd{
		 height:30px;
		 line-height:30px;
		float:left;
	}
	dl dt{
	   width:120px;
		text-align: right;
		padding-right: 12px;
	}
	dl label{
		width:120px;
	    display: inline-block;
	}
	.txtinput{
		width:170px;
		outline:none;
		font-size:14px !important;
		font-weight:normal;
		height:30px;
		border-radius:4px;  
	    border:1px solid rgba(102,175,233,.75); 
	}
	.txtinput:focus{
	    box-shadow:0 0 4px rgba(102,175,233,.5);  
	    transition:box-shadow  .5s;  
	}
	textarea{
		width:90%;
		height:80px;
		border-radius:4px;  
	    border:1px solid rgba(102,175,233,.75); 
	    font-size:13px;
	    outline:none;   
	}
	textarea:focus{
		outline:none;  
	    box-shadow:0 0 4px rgba(102,175,233,.5);  
	    transition: box-shadow  .5s;  
	}
	.divcenter{
		clear:both;
	}
	.pre{
		text-align:center;
		font-size:14px;
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
	.haspre{
		width:170px;
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
	i.necessary{
		color:red;
		font-weight: bold;
		font-size: 17px;
	}
	#outLink{
		width:500px;
	}
	#dy_keydesc dl:first-child{
	   height:50px;
	}
	#dy_keydesc dl:last-child{
	   height:70px;
	}
	#clnKeyWords{
		height:50px;
		float:left;
		width:840px;
	}
	#clnDesc{
	    height:70px;
		float:left;
		width:840px;
	}
	#dy_singlepageContent div{
		line-height: 20px;
		text-align: left;
	}
	html{
		overflow-x:hidden; 
	}
	#dy_singlepageContent tr td:first-child{
		width:120px;
		text-align: right;
		padding-right: 12px;
		display: block;
		margin-bottom: 10px;
	}
	#dy_singlepageContent tr td:last-child{
		width:80%;
		display: block;
		padding-left: 50px;
	}
</style>
</head>
<body>
	<div id="container">
		<form action="" method="post" id="form" enctype="multipart/form-data">
			<input type="hidden" name="enname" id="enname" />
	   <dl >
	  		<dt><label for="channel_id" >所在模型:</label></dt>
	  		<dd><input  type="text" id="channel_id" name="channel_id" /></dd>
	  		<dt> <label for="name">栏目名称:<i  style="left:90px;" class="necessary">*</i></label></dt>
	  		<dd><input  class="txtinput" type="text" name="name" id="name" /></dd>
	  		<dt><label for="htmlDir" style="width:100px;">html存放目录:
	  		<i   class="necessary">*</i></label></dt>
	  		<dd><span class="pre">s/</span><input  class="haspre" type="text" name="htmlDir" id="htmlDir" /> </dd>
	  </dl>
		<dl >
			<dt><label for="typename">栏目类别:</label> </dt>
	  		<dd><input   class="txtinput" type="text" name="typename" id="typename"/> </dd>
			<dt ><label for="orderNo" style="width:90px;padding-left:10px">排序优先级:<i  class="necessary">*</i></label></dt>
			<dd><input type="text" name="orderNo" id="orderNo"  class="txtinput"   /></dd>
			<dt> <label for="clnTitle" >页面标题:
			<i  style="left:90px;" class="necessary">*</i></label></dt>
			<dd>  <input type="text"  class="txtinput" style="width:215px;margin-left: 2px;" name="clnTitle" id="clnTitle" /> </dd>
		</dl>
		<div id="dy_cover" >
			<dl>
		      <dt><label for="cover_mytpl">默认模版:<i class="necessary">*</i></label> </dt>
		  	  <dd><input type="text" name="cover_mytpl" id="cover_mytpl" class="txtinput" /></dd>
    	    </dl>
		</div>	
		<div id="dy_list">
			<dl>
		      <dt><label for="list_mytpl">默认模版:<i class="necessary">*</i></label> </dt>
		  	  <dd><input type="text" name="list_mytpl" id="list_mytpl" class="txtinput" /></dd>
		      <dt><label for="list_contentTplName">内容模板:<i class="necessary">*</i></label></dt>
			  <dd> <input type="text"	name="list_contentTplName" id="list_contentTplName" class="txtinput" /></dd>
			 </dl>
		</div>
		<div id="dy_outlink">
			 <dl>	  
		  	  <dt><label for="outLink" style="width:100px">外部链接地址：<i class="necessary">*</i></label></dt>
		  	  <dd> <input type="text" name="outLink" id="outLink"  class="txtinput" /></dd>
		 	 </dl>
		</div>
		<div id="dy_singlepage">
			<dl>
		      <dt><label for="singlepage_mytpl">默认模版:<i  class="necessary">*</i></label> </dt>
		  	  <dd><input type="text" name="singlepage_mytpl" id="singlepage_mytpl" class="txtinput" /></dd>
		    </dl>
		</div>
		<div id="dy_keydesc">
		    <dl class="dydltextarea " >
		      <dt><label for="clnKeyWords">页面关键字:</label> </dt>
		  	  <dd><textarea name="clnKeyWords" id="clnKeyWords"></textarea></dd>
		    </dl>
		    <dl>
		      <dt><label for="clnDesc" >页面描述:</label> </dt>
		  	  <dd><textarea name="clnDesc" id="clnDesc"></textarea></dd>
		    </dl>
		</div>
		<div id="dy_singlepageContent">
			<table>
			    <tr >
			      <td><label for="singleContent">单页面的内容:</label></td>
			  	  <td><textarea name="singleContent" id="singleContent"></textarea>
			  	  	  <script type="text/javascript">
							$(function(){
								UE.delEditor("singleContent");
							    UE.getEditor("singleContent",{
							            wordCount:false,
							            elementPathEnabled:false,
							            initialFrameHeight:300,
							            autoHeightEnabled: true,
							            autoFloatEnabled: true
								 });
							});
						</script> 
			  	  </td>
			    </tr>
			</table>
		</div>	
		 <zdw:hasRight url="trueAddTopColumn">
			<div class="divcenter">
			  <button type="button" id="addTopCln_submit" class="easyui-linkbutton" data-options="iconCls:'icon-ok'">提交</button>
			</div>
		 </zdw:hasRight>
	  </form>
	  <!-- 浏览模板选择框 -->
		<div id="scanWin" class="easyui-window" title="选择模板" style="width:350px;height:300px;overflow-y:auto;padding:10px;background:#fff;"   
	        data-options="modal:true,collapsible:false,minimizable:false,closed:true">   
			 <div id="tpls"></div>
		</div> 
	</div>
</body>
</html>