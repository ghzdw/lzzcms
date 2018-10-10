<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="zdw" uri="/lzzcms" %>    
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<link rel="stylesheet" href="<%=request.getContextPath() %>/resources/easyui/themes/default/easyui.css">
<link rel="stylesheet" href="<%=request.getContextPath() %>/resources/easyui/themes/icon.css">
 <link rel="stylesheet" href="<%=request.getContextPath() %>/resources/poshytip/tip-yellow/tip-yellow.css">
<link href="<%=request.getContextPath() %>/resources/css/reset.css" rel="stylesheet">
<link rel="stylesheet" href="<%=request.getContextPath() %>/resources/css/lzzcmsstyle.css">

<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-1.10.2.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/JSON2.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/UUID.js"></script>
<script src="<%=request.getContextPath() %>/resources/easyui/jquery.easyui.min.js"></script>
<script src="<%=request.getContextPath() %>/resources/easyui/easyui-lang-zh_CN.js"></script>
<script src="<%=request.getContextPath() %>/resources/js/plugins-lzzcms-lib.js"></script>
<script src="<%=request.getContextPath() %>/resources/js/functions-lzzcms-lib.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/ueditor/ueditor.config.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/ueditor/ueditor.all.min.js"></script>
<script type="text/javascript" charset="utf-8" src="<%=request.getContextPath()%>/resources/ueditor/lang/zh-cn/zh-cn.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath() %>/resources/poshytip/jquery.poshytip.min.js"></script>

<script type="text/javascript">
//添加iframe形式的tab
function addTabIframe(conf){
	var defaults={//txt,url
		method:'get'
	};
	var opts=$.extend({},defaults,conf);
	if($('#centerArea').tabs("exists",opts.txt)){
		$('#centerArea').tabs("select",opts.txt);
	}else{
	   //	var content="<iframe   frameborder=0 scrolling='no' width='100%' height='800px' src='"+url+"'></iframe>";
		$('#centerArea').tabs("add",{
			title:opts.txt,
			narrow:true,
			closable:true,
			method:opts.method,
			content:"<iframe   frameborder=0 scrolling='auto' width='100%' id='"+opts.uuid+"' height='99%' src='"+opts.url+"'></iframe>"
		});
	}
}
	$(function(){
		$("#layout").layout({
			fit:true
		});
		$("#layout").layout("panel","center").panel({
			onResize:function(width, height){
				$("#centerArea").tabs("resize","auto");
			}
		});
		$("#functions").tree({
			url:"loadRights",
			lines:true,
			onClick: function(node){
				var url=node.attributes.url;
				if(url){
					if(url.indexOf("Log")>-1||url.indexOf("SearchWordManage")>-1||url.indexOf("Iframe")>-1
							||url.indexOf("tag")>-1||url.indexOf("toBackup")>-1||url.indexOf("toMakeIndex")>-1
							||url.indexOf("toMakeCln")>-1||url.indexOf("toMakeCont")>-1
							||url.indexOf("spiderEntrance")>-1||url.indexOf("toSearchManage")>-1
							||url.indexOf("flManage")>-1||url.indexOf("toPageCfg")>-1
							||url.indexOf("roleManage")>-1||url.indexOf("adminManage")>-1
							||url.indexOf("comboxCfg")>-1||url.indexOf("downloadManage")>-1
							||url.indexOf("toGlobalCfg")>-1||url.indexOf("spiderHand")>-1
							||url.indexOf("toGetCrawls")>-1){
						var id=UUID.generate();
						 addTabIframe({txt:node.text,url:url,uuid:id});
						 document.getElementById(id).onload=function(){
							 $("#"+id).contents().find("html,body").css("height","100%");
						 };
					}else{
						addTab(node.text,url);
					}
				}
			}
		});
		$("#centerArea").tabs({
			border:false,
			fit:true,
			onContextMenu: function(e, title, index){
				  e.preventDefault();
				  $('#closeCxt').menu('show', {
		                 left: e.pageX,
		                 top: e.pageY
		          });
			}
		});
		 $("#closeCxt").menu({
             onClick : function (item) {
                 closeTab(item.id);
             }
         });
	});
</script>
<style type="text/css">
.tabs-title {
font-size: 12px;
}
.north{
	height:37px;
	background: #e6f0ff;
	font-size: 14px;
	font-family:"微软雅黑";
}
.logo{
	display:inline-block;
	width:300px;
	height:30px;
	background:url(../resources/imgs/logo2.png) no-repeat 0px 3px;
}
   @font-face {
		font-family: 'zdw_font';
		src: url('../resources/fonts/zdw_font.eot'); /* 写两个src：IE9 的兼容模式 */
		src: url('../resources/fonts/zdw_font.eot?#iefix') format('embedded-opentype'), /* IE6-IE8 */
	             url('../resources/fonts/zdw_font.woff') format('woff'), /* 主流浏览器 */
	             url('../resources/fonts/zdw_font.ttf')  format('truetype'), /* 主流浏览器 */
	             url('../resources/fonts/zdw_font.svg#zdw_font') format('svg'); /* Legacy iOS */
	    font-weight: normal;
	    font-style: normal;         
	 }
	   [class*="zdwfont"] {
		    font-family: 'zdw_font' !important;
		    speak:none;
		    font-style: normal;
		    font-weight: normal;
		    font-variant:normal; 
		    text-transform: none;
		    line-height: 1;
		    -webkit-font-smoothing: antialiased;
		    -moz-osx-font-smoothing: grayscale;
		}
		.zdwfont-user{
			position: relative;
			top:2px;
		}
		.zdwfont-logout{
		 position: relative;
			top:3px;
		}
		.zdwfont-home{
		position: relative;
			top:2px;
		}
	   .zdwfont-user:before{
	   		color:#666;
		   	content:"\e971";
		   	font-size:20px;
	    }
	   .zdwfont-logout:before{
	   		color:#666;
	     	content:"\ea14";
	     	font-size:18px;
	   }
	   .zdwfont-home:before{
	   		color:#666;
	     	content:"\e901";
	     	font-size:18px;
	   }
	   .likea{
			display: inline-block;
			color:black;
			text-decoration: none;
		}
		.northinner{
			height: 37px;
			overflow: hidden;
		}
		.tb{
			float:right;
			margin:0;
			padding:0;
			margin-right:80px;
		}
		.tb tr,.tb tr td{
		    margin:0;
			padding:0;
			height: 37px;
			line-height: 37px;
		}
		.tb tr td:nth-of-type(2n){
			padding-left:3px;
		}
		.tb tr td:nth-of-type(2n+1){
			padding-left:8px;
		}
</style>
<title>乐之者cms-管理主界面</title>
</head>
<body >
  <div id="layout">   
    <div data-options="region:'north',border:false" class="north">
       	<div class="northinner">
       	   <section class="logo"></section>
       	   <table  class="tb" >
       	   		<tr>
       	   			<td><span class="zdwfont zdwfont-user"></span></td>
       	   			<td><span>${sessionScope.admin.realName }</span></td> 
       	   			<td><span class="zdwfont zdwfont-logout"></span></td>
       	   			<td><span><a class="likea" href="logout">退出</a></span></td>
       	   			 <td><span class="zdwfont zdwfont-home"></span></td>
       	   			<td><span><a class="likea" target="_blank" href="${sessionScope.basePath }" >
       	   			    网站主页</a></span></td>      
       	   		</tr>
       	   </table>
       	</div>
    </div>   
    <div   data-options="region:'west',title:'功能区',split:true,border:false" style="width:auto;max-width:200px;">
	    <div id="functions"></div>
    </div>   
    
    <div id="outercenter" data-options="region:'center'">
    	<div  id="centerArea" style="overflow:auto;"></div> 
    	<!--  <iframe  id="centerArea" src=""  frameborder=0 scrolling='auto' width='100%' height='100%'></iframe> -->
    </div>   
    <div data-options="region:'south',border:false" style="height:45px;">
    	 <div id="footer"> </div>
    </div>   
</div>  
   <div id="closeCxt" class="easyui-menu" >
	  <div id="closeCxt_crt" data-options="iconCls:'icon_cancle'" >关闭当前</div>
      <div id="closeCxt_all" data-options="iconCls:'icon_cross'" >关闭全部</div>
      <div id="closeCxt_other" data-options="iconCls:'icon_no'">关闭其他</div>
   </div>

</body>
</html>