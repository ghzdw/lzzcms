<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib prefix="zdw" uri="/lzzcms" %> 
<!doctype html>
<html>
<head>
<meta charset="utf-8">
<title>选择要爬取的元素</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/poshytip/tip-yellow/tip-yellow.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/cfgSpider.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/scrollerbar/jquery.mCustomScrollbar.min.css">
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/UUID.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-1.10.2.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/JSON2.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/element-xpath.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/generate.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/poshytip/jquery.poshytip.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/scrollerbar/jquery.mCustomScrollbar.concat.min.js"></script>
</head>
<body>
<div class="backdroplayer out">
	<img src="<%=request.getContextPath()%>/resources/imgs/loading.gif">
</div>
<div class="main">
		<div class="top">
			<div class="title">
				<div class="targetTitleParent">正在爬取: <span id="targetTitle"></span></div>
				<div class="tip">左键可配合选取列表复选框选择要抓取的数据,右键点击翻页按钮</div>
			</div>
			<zdw:hasRight url='startSpiderById'>
				<div class="start" id="start">
					<span>开始爬取</span>
				</div>
			</zdw:hasRight>
		</div>
		
		<iframe id="iframe" src="${requestScope.src }"  height="100%" width="100%"></iframe>
		<section id="listtip">
			<div id="listtipdiv">
				<table>
					<caption><h4>选取列表复选框</h4></caption>
					<tr><td>使用场景:</td><td><span>鼠标左键点击时</span></td></tr>
					<tr><td>是否勾选:</td><td><span>1.不勾选:记录鼠标左键点击的元素</span></td></tr>
					<tr><td></td><td><span>2.勾选:记录鼠标左键点击的元素以及与该点击元素类似的元素</span></td></tr>
				</table>
			</div>
		</section>
		<section id="fliptip">
			<div id="fliptipdiv">
				<table>
					<caption><h4>翻页配置</h4></caption>
					<tr><td>使用场景:</td><td><span>鼠标右键点击爬取页面的翻页按钮</span></td></tr>
					<tr><td>是否配置:</td><td><span>1.未配置翻页:只爬取当前页面</span></td></tr>
					<tr><td></td><td><span>2.已配置翻页:爬取当前页面以及后10页</span></td></tr>
				</table>
			</div>
		</section>
		
		<div class="bottom">
			<div class="headLine">
				<span>正在配置&nbsp;&nbsp;<span class="nowcfgcolname"></span>&nbsp;&nbsp;字段，配置预览如下</span>
				<span class="listcfg"><label>
					<input type="checkbox" name="checkList" id="checkList">&nbsp;选取列表
					<span class="questionimg">
						<img src="<%=request.getContextPath()%>/resources/imgs/help.png">
					</span>
				</label></span>
				<span class="flipcfg">
						翻页<span id="fliptiptext">未配置</span>
						<span class="questionimg">
							<img src="<%=request.getContextPath()%>/resources/imgs/help.png">
						</span>
				</span>
				<span class="statussign"><img src="<%=request.getContextPath()%>/resources/imgs/status_up.png"></span>
			</div>

			<div>
				<div class="sliderLeftDiv disabled"><img src="<%=request.getContextPath()%>/resources/imgs/angle-left.png"></div>
				<div class="sliderRightDiv disabled"><img src="<%=request.getContextPath()%>/resources/imgs/angle-right.png"></div>
				<section class="contouter">
					<section class="continner">
						<div class="headarea"></div>
						<div class="bodyarea"></div>
					</section>
				</section>
			</div>
		</div>
		<input type="hidden" id="cfgId" name="cfgId" value="${requestScope.cfgId }">
		<input type="hidden" id="columnId" name="columnId">
		<input type="hidden" id="addfortb" name="addfortb">
		<input type="hidden" id="website" name="website">
</div>
<script>
	
</script>
</body>
</html>