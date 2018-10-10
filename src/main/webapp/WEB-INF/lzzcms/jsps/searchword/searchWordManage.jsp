<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>乐之者cms-搜索词管理</title>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/echarts/echarts.min.js"></script>
	<script src="<%=request.getContextPath() %>/resources/js/jquery-1.10.2.js"></script>
<style type="text/css">
    #searchWordBar{
		height:370px;
		width:800px;
		margin: 0 auto;
		margin-top:100px;
	}
</style>
</head>
<body>
	<div id="searchWordBar"></div>
	<script type="text/javascript">
			var searchWordBar = echarts.init($('#searchWordBar').get(0));
			var searchWordBarOption = {
			   title: {
			        text: '近一个月搜索词排名TOP10',
			        textStyle:{
			        	fontSize:20
			        },
			        left: 'center'
			    },
			    legend: {
			        data:["近一个月搜索词排名TOP10"],
			        bottom:0
			    },
			    grid:{
			        top:70,//图表区域与标题区域的间距
			        height:240//图表的高度
			    },
			    xAxis: {
			    	name:"搜索内容",
			        data: []
			    },
			    yAxis:  [{
					         name:"搜索次数"
					    }],
				barWidth:"40%",
			    tooltip:{},
			    series: [{
				            name: '近一个月搜索词排名TOP10',
				            type: 'bar',
				            data: [],
				            itemStyle:{
								normal:{
									color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
				                	  offset: 0, color: '#a287be' // 0% 处的颜色
				                	}, {
				                	  offset: 1, color: '#a5a5a5' // 100% 处的颜色
				                	}], false)
								}                	
			                }
			    		}]//series
			};//option
			searchWordBar.setOption(searchWordBarOption);
			$.post("searchWordBar").done(function(data){
				searchWordBar.setOption({
					   xAxis: {
					        data: data.x_data
					    },
					    series: [{
				            name: '近一个月搜索词排名TOP10',
				            data: data.y_data
			    		}]
				});
			});
	</script>
</body>
</html>
