<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="zdw" uri="/lzzcms" %> 
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
	    <meta http-equiv="X-UA-Compatible" content="IE=edge">
	    <meta name="viewport" content="width=device-width, initial-scale=1">
	    <link rel="stylesheet" href="<%=request.getContextPath() %>/resources/bootstrap-3.3.5/css/bootstrap.min.css">
	    <link rel="stylesheet" href="<%=request.getContextPath() %>/resources/bootstrap-3.3.5/css/bootstrap-table.min.css">
<link href="<%=request.getContextPath()%>/resources/css/reset.css" rel="stylesheet">
<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/lzzcmsstyle.css">
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-1.10.2.js"></script>
<script src="<%=request.getContextPath()%>/resources/js/functions-lzzcms-lib.js"></script>
<script src="<%=request.getContextPath()%>/resources/bootstrap-3.3.5/js/bootstrap.min.js"></script>
	    <script src="<%=request.getContextPath() %>/resources/bootstrap-3.3.5/js/bootstrap-table.min.js" ></script>
	    <script src="<%=request.getContextPath() %>/resources/bootstrap-3.3.5/js/bootstrap-table-zh-CN.min.js" ></script>
		<title>任务配置管理</title>
		<style>
			.h45{
				height:45px;
				line-height:45px;
				border-bottom: 1px solid #ccc;
				font-size:18px;
				font-weight: bold;
			}
			.addpro{
				display: inline-block;
				margin-left: 50px;
				font-size:14px;
				font-weight: normal;
				color:blue;
			}
			.datagrid{
				padding-top:15px
			}
			.container-fluid{
				padding:0;
			}
			.lspace{
				margin-left:6px;
			}
			.red{
				color:red;
			}
		</style>
	</head>
	<body>
	 	<div class="container-fluid">
	 	  <zdw:hasRight url='addCrawl'> 
			<button type="button" class="btn btn-default" id="showAddCrawlDiaBtn">
				添加采集配置
			</button>
			<button type="button" class="btn btn-default" id="handCrawlBtn">
				规则采集
			</button>
			<button type="button" class="btn btn-default" id="showSyncStatus"></button>
		  </zdw:hasRight>	 
	 		<section class="datagrid">
		 		<table id="table"></table>
	 		</section>
	 	<!-- 新增模态框 -->
		<div class="modal fade" data-backdrop="static" data-keyboard="false" tabindex="-1" id="addCrawlDia">
		  <div class="modal-dialog">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal" ><span>&times;</span></button>
		        <h4 class="modal-title">新增爬虫配置</h4>
		      </div>
		      <div class="modal-body">
		          <form class="form-horizontal" id="addCrawlForm">
		          	<div class="form-group">
					    <label  class="col-sm-2  control-label">选择频道:</label>
					    <div class="col-sm-6">
							<select class="form-control input-sm inline" name="channel" id="channel"></select>
						</div>
				     </div>
		          	<div class="form-group">
					    <label  class="col-sm-2  control-label">选择栏目:</label>
					    <div class="col-sm-6">
							<select class="form-control input-sm inline" id="column" name="column_id"></select>
						</div>
				     </div>
					  <div class="form-group">
					    <label for="inputEmail3" class="col-sm-2 control-label">列表地址</label>
					    <div class="col-sm-10">
					      <input type="text" class="form-control" name="list_url">
					    </div>
					  </div>
					  <div class="form-group">
					    <label for="inputPassword3" class="col-sm-2 control-label">条目选择器</label>
					    <div class="col-sm-10">
					      <input type="text" class="form-control" name="list_item_selector">
					    </div>
					  </div>
					  <div class="form-group">
					    <label for="inputPassword3" class="col-sm-2 control-label">代理ip</label>
					    <div class="col-sm-10">
					      <textarea name="proxy_ip" style="width: 100%;height: 50px;"
					      placeholder="多个代理之间使用;分割，一个代理的ip和端口之间使用:分割"
					      ></textarea>
					    </div>
					  </div>
					  <div class="form-group">
					    <div class="col-sm-offset-3 col-sm-6">
					      <input type="button" class="btn btn-default" style="margin-left:40px" data-dismiss="modal"  value="取消">
					      <input type="button" id="btnAddCrawl" class="btn btn-primary" style="margin-left:40px" value="确认">
					    </div>
					  </div>
					</form>
		      </div>
		    </div><!-- /.modal-content -->
		  </div><!-- /.modal-dialog -->
		</div><!-- /.modal -->	
	 	<!-- 修改模态框 -->
		<div class="modal fade" data-backdrop="static" data-keyboard="false" tabindex="-1" id="editCrawlDia">
		  <div class="modal-dialog">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal" ><span>&times;</span></button>
		        <h4 class="modal-title">编辑采集配置</h4>
		      </div>
		      <div class="modal-body">
		          <form class="form-horizontal" id="editCrawlForm">
		          	<div class="form-group">
					    <label  class="col-sm-2  control-label">选择频道:</label>
					    <div class="col-sm-6">
							<select class="form-control input-sm inline" name="channel" id="channel_edit"></select>
						</div>
				     </div>
		          	<div class="form-group">
					    <label  class="col-sm-2  control-label">选择栏目:</label>
					    <div class="col-sm-6">
							<select class="form-control input-sm inline" id="column_edit" name="column_id"></select>
						</div>
				     </div>
					  <div class="form-group">
					    <label for="inputEmail3" class="col-sm-2 control-label">列表地址</label>
					    <div class="col-sm-10">
					      <input type="text" class="form-control" name="list_url">
					       <input type="hidden" name="crawl_id_update_crawl"  id="crawl_id_update_crawl" >
					    </div>
					  </div>
					  <div class="form-group">
					    <label for="inputPassword3" class="col-sm-2 control-label">条目选择器</label>
					    <div class="col-sm-10">
					      <input type="text" class="form-control" name="list_item_selector">
					    </div>
					  </div>
					  <div class="form-group">
					    <label for="inputPassword3" class="col-sm-2 control-label">代理ip</label>
					    <div class="col-sm-10">
					      <textarea name="proxy_ip" style="width: 100%;height: 50px;"
					      placeholder="多个代理之间使用;分割，一个代理的ip和端口之间使用:分割"
					      ></textarea>
					    </div>
					  </div>
					  <div class="form-group">
					    <div class="col-sm-offset-3 col-sm-6">
					      <input type="button" class="btn btn-default" style="margin-left:40px" data-dismiss="modal"  value="取消">
					      <input type="button" id="btnEditCrawl" class="btn btn-primary" style="margin-left:40px" value="确认">
					    </div>
					  </div>
					</form>
		      </div>
		    </div><!-- /.modal-content -->
		  </div><!-- /.modal-dialog -->
		</div><!-- /.modal -->	
	 	<!-- 新增明细模态框 -->
		<div class="modal fade" data-backdrop="static" data-keyboard="false" tabindex="-1" id="addCrawlDetailDia">
		  <div class="modal-dialog">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal" ><span>&times;</span></button>
		        <h4 class="modal-title">新增配置明细</h4>
		      </div>
		      <div class="modal-body">
		          <form class="form-horizontal" id="addCrawlDetailForm">
					  <div class="form-group">
					    <label for="inputEmail3" class="col-sm-2 control-label">标题选择器</label>
					    <div class="col-sm-10">
					      <input type="text" class="form-control" name="comm_title_selector" placeholder="多个使用@[zdw]@分开">
					      <input type="hidden"  name="crawl_id" id="crawl_id">
					    </div>
					  </div>
					  <div class="form-group">
					    <label for="inputPassword3" class="col-sm-2 control-label">内容选择器</label>
					    <div class="col-sm-10">
					      <input type="text" class="form-control" name="mainbody_selector" placeholder="多个使用@[zdw]@分开">
					    </div>
					  </div>
					  <div class="form-group">
					    <label for="inputPassword3" class="col-sm-2 control-label">排除选择器</label>
					    <div class="col-sm-10">
					      <input type="text" class="form-control" name="will_exclude_selector" placeholder="多个使用@[zdw]@分开">
					    </div>
					  </div>
					  <div class="form-group">
					    <div class="col-sm-offset-3 col-sm-6">
					      <input type="button" class="btn btn-default" style="margin-left:40px" data-dismiss="modal"  value="取消">
					      <input type="button" id="btnAddCrawlDetail" class="btn btn-primary" style="margin-left:40px" value="确认">
					    </div>
					  </div>
					</form>
		      </div>
		    </div><!-- /.modal-content -->
		  </div><!-- /.modal-dialog -->
		</div><!-- /.modal -->	
	 	<!-- 修改明细模态框 -->
		<div class="modal fade" data-backdrop="static" data-keyboard="false" tabindex="-1" 
		  id="updateCrawlDetailDia">
		  <div class="modal-dialog">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal" ><span>&times;</span></button>
		        <h4 class="modal-title">修改配置明细</h4>
		      </div>
		      <div class="modal-body">
		          <form class="form-horizontal" id="updateCrawlDetailForm">
					  <div class="form-group">
					    <label for="inputEmail3" class="col-sm-2 control-label">标题选择器</label>
					    <div class="col-sm-10">
					      <input type="text" class="form-control" name="comm_title_selector" placeholder="多个使用@[zdw]@分开">
					      <input type="hidden"  name="crawl_id" id="crawl_id_update" >
					    </div>
					  </div>
					  <div class="form-group">
					    <label for="inputPassword3" class="col-sm-2 control-label">内容选择器</label>
					    <div class="col-sm-10">
					      <input type="text" class="form-control" name="mainbody_selector" placeholder="多个使用@[zdw]@分开">
					    </div>
					  </div>
					  <div class="form-group">
					    <label for="inputPassword3" class="col-sm-2 control-label">排除选择器</label>
					    <div class="col-sm-10">
					      <input type="text" class="form-control" name="will_exclude_selector" placeholder="多个使用@[zdw]@分开">
					    </div>
					  </div>
					  <div class="form-group">
					    <div class="col-sm-offset-3 col-sm-6">
					      <input type="button" class="btn btn-default" style="margin-left:40px" data-dismiss="modal"  value="取消">
					      <input type="button" id="btnUpdateCrawlDetail" class="btn btn-primary" style="margin-left:40px" value="确认">
					    </div>
					  </div>
					</form>
		      </div>
		    </div><!-- /.modal-content -->
		  </div><!-- /.modal-dialog -->
		</div><!-- /.modal -->	
	 		<!-- 删除对话框-->
	 		 <div class="modal fade" id="deleteModal">
    		  <div class="modal-dialog" >
    		    <div class="modal-content">
    		      <div class="modal-header">
    		        <button type="button" class="close" data-dismiss="modal" ><span>&times;</span></button>
    		        <h4 class="modal-title">删除</h4>
    		      </div>
				  <form class="form-horizontal">
        		      <div class="modal-body">
        		    		      确定要删除吗？
        		      </div>
        		      <div class="modal-footer">
        		        <input type="button" class="btn btn-info" id="delBtn" data-dismiss="modal" value="确认">
        		      </div>
				    </form>	  
    		    </div><!-- /.modal-content -->
    		  </div><!-- /.modal-dialog -->
    		</div><!-- /弹窗 -->
    		<!-- 提示对话框-->
	 		 <div class="modal fade" id="tipModal">
    		  <div class="modal-dialog" >
    		    <div class="modal-content">
    		      <div class="modal-header">
    		        <button type="button" class="close" data-dismiss="modal" ><span>&times;</span></button>
    		        <h4 class="modal-title">提示</h4>
    		      </div>
        		      <div class="modal-body" id="tipcont"></div>
        		      <div class="modal-footer">
        		        <input type="button" class="btn btn-info" data-dismiss="modal" value="确认">
        		      </div>
    		    </div><!-- /.modal-content -->
    		  </div><!-- /.modal-dialog -->
    		</div><!-- /弹窗 -->
	 	</div>
	<script type="text/javascript">
	$("#showAddCrawlDiaBtn").click(function(){
		$("#addCrawlForm")[0].reset();
		$("#addCrawlDia").modal("show");
	});
	//手动采集按钮点击
	$("#handCrawlBtn").click(function(){
		if(!$("#handCrawlBtn").prop("disabled")){
			$("#handCrawlBtn").prop("disabled","disabled");
			$.ajax({
				type:"get",
				url:'crawlTask?from=page',
				success:function(data){
					$("#handCrawlBtn").removeProp("disabled");
					if(data.hand_crawl_status=="success"){
					   $("#tipcont").text("成功");
					}else{
					   $("#tipcont").text("失败");
					}
				  	$("#tipModal").modal("show");
				}
			}); 
		}
	});
	
	//获取同步状态
	$.ajax({
		type:"get",
		url:'getSyncStatus',
		success:function(data){//<option value="1">普通文章</option>
			if(data.run_status=="success"){
				var tip="";
				if(data.status==0){
					tip+="未执行";
				}else if(data.status==1){
					tip+="正在执行,上次执行时间:"+data["last_sync_date"];
					$("#handCrawlBtn").prop("disabled","disabled");
				}else{
					tip+="已完成,上次执行时间:"+data["last_sync_date"];
				}
				$("#showSyncStatus").text(tip);
			}
		}
	});
	//模型加载
	$.ajax({
		type:"get",
		url:'getForCombobox?page=toAdd',
		success:function(data){//<option value="1">普通文章</option>
			$.each(data,function(i,obj){
				if(i==0){
					$("#channel,#channel_edit").append("<option selected data-addfortb='"+obj["additionaltable"]+"' value='"+obj["id"]+"'>"+obj["channelname"]+"</option>");
				}else{
					$("#channel,#channel_edit").append("<option data-addfortb='"+obj["additionaltable"]+"'  value='"+obj["id"]+"'>"+obj["channelname"]+"</option>");
				}
			});
			var selectedChnId=$("#channel").val();
			//加载栏目
			getColumnByChanelId(selectedChnId,"#column");
			getColumnByChanelId($("#channel_edit").val(),"#column_edit");
		}
	});
	$("#channel").change(function(){
		var selectedChnId=$("#channel").val();
		//加载栏目
		getColumnByChanelId(selectedChnId,"#column");
	});
	$("#channel_edit").change(function(){
		var selectedChnId=$("#channel_edit").val();
		//加载栏目
		getColumnByChanelId(selectedChnId,"#column_edit");
	});
	function getColumnByChanelId(channelId,clnSelector){
		$(clnSelector).empty();
		$.ajax({
			type:"post",
			url:'getColumnByChanelIdForSpider',
			data:{"chanelId":channelId},
			success:function(data){
				$.each(data,function(i,obj){
					if(i==0){
						$(clnSelector).append("<option selected  value='"+obj["id"]+"'>"+obj["name"]+"</option>");
					}else{
						$(clnSelector).append("<option value='"+obj["id"]+"'>"+obj["name"]+"</option>");
					}
				});
			}
		});	
	}
	//添加爬虫配置
	$("#btnAddCrawl").click(function(){
		$.ajax({
		   type: "POST",
		   url: "addCrawl",
		   data:str2Json("#addCrawlForm"),
		   success: function(data){
			   if(data.status=="success"){
				    $('#table').bootstrapTable('refreshOptions',{page:0,rows:10});
		    		$("#tipcont").text("成功");
		    		$("#addCrawlDia").modal('hide');
	    		}else{
	    			$("#tipcont").text("失败");
	    		}
			   $("#tipModal").modal("show");
		   }
		});
	});
 	function deleteItem(id){
 		$("#deleteModal").modal();
 		$("#deleteModal").attr("data-id",id);
 	}
 	//删除任务配置
 	$("#delBtn").click(function(){
		 $.ajax({
		   type: "POST",
		   url: "deleteCrawlById",
		   data:"id="+$("#deleteModal").attr("data-id"),
		   success: function(data){
			   if(data.status=="success"){
				    $('#table').bootstrapTable('refreshOptions',{page:0,rows:10});
		    		$("#tipcont").text("成功");
		    		$("#deleteModal").modal('hide');
	    		}else{
	    			$("#tipcont").text("失败");
	    		}
			   $("#tipModal").modal();
		   }
	  });
	});
 	function showAddCrawlDetailDia(id){
 	  $("#addCrawlDetailForm")[0].reset();	
 	  $("#addCrawlDetailDia").find("#crawl_id").val(id);	
 	  $("#addCrawlDetailDia").modal("show");	
 	}
 	function showUpdateDetialDia(id){
 	  $("#updateCrawlDetailForm")[0].reset();	
 	  $("#updateCrawlDetailDia").find("#crawl_id_update").val(id);	
 	  $.ajax({
 		   type: "POST",
 		   url: "showCrawlDetailByCrawlId",
 		   data:{"crawl_id":id},
 		   success: function(data){
 			   if(data.status=="success"){//
 				  $("#updateCrawlDetailDia").find("input[name=comm_title_selector]")
 				  .val(data.comm_title_selector); 
 				  $("#updateCrawlDetailDia").find("input[name=mainbody_selector]")
 				  .val(data.mainbody_selector); 
 				  $("#updateCrawlDetailDia").find("input[name=will_exclude_selector]")
 				  .val(data.will_exclude_selector); 
 	    		}
 		   }
 	  });
 	  $("#updateCrawlDetailDia").modal("show");	
 	}
 	//修改采集配置
 	function showUpdateCrawDia(id){
 	  $("#editCrawlForm")[0].reset();	
 	  $("#editCrawlDia").find("#crawl_id_update_crawl").val(id);	
 	  $.ajax({
 		   type: "POST",
 		   url: "showCrawlByCrawlId",
 		   data:{"crawl_id":id},
 		   success: function(data){
 			   if(data.status=="success"){//
 				  $.each($("#column_edit").find("option"),function(i,o){
 					 if($(o).prop("value")==data.column_id){
 						 $(o).prop("selected",true);
 						 return false;
 					 } 
 				  });
 				  $("#editCrawlDia").find("input[name=list_url]").val(data.list_url); 
 				  $("#editCrawlDia").find("input[name=list_item_selector]").val(data.list_item_selector); 
 				  $("#editCrawlDia").find("textarea[name=proxy_ip]").val(data.proxy_ip); 
 	    		}
 		   }
 	  });
 	  $("#editCrawlDia").modal("show");	
 	}
 	//添加明细
 	$("#btnAddCrawlDetail").click(function(){
	 $.ajax({
	   type: "POST",
	   url: "addCrawlDetail",
	   data:str2Json("#addCrawlDetailForm"),
	   success: function(data){
		   if(data.status=="success"){
			   $("#tipcont").text("成功");
	    	   $("#addCrawlDetailDia").modal('hide');
    		}else{
    			$("#tipcont").text("失败");
    		}
			$("#tipModal").modal();
	   }
	  });
 	});
 	//更新明细
 	$("#btnUpdateCrawlDetail").click(function(){
	 $.ajax({
	   type: "POST",
	   url: "updateCrawlDetail",
	   data:str2Json("#updateCrawlDetailForm"),
	   success: function(data){
		   if(data.status=="success"){
			   $("#tipcont").text("成功");
	    	   $("#updateCrawlDetailDia").modal('hide');
    		}else{
    			$("#tipcont").text("失败");
    		}
			$("#tipModal").modal();
	   }
	  });
 	});
 	//更新采集配置
 	$("#btnEditCrawl").click(function(){
	 $.ajax({
	   type: "POST",
	   url: "updateCrawl",
	   data:str2Json("#editCrawlForm"),
	   success: function(data){
		   if(data.status=="success"){
			   $("#tipcont").text("成功");
	    	   $("#editCrawlDia").modal('hide');
    		}else{
    			$("#tipcont").text("失败");
    		}
			$("#tipModal").modal();
	   }
	  });
 	});
 	function onOffCrawl(id,is_deleted){
 		$.ajax({
			type: "POST",
			url: "onOffCrawl",
			data:{"id":id,"is_deleted":is_deleted},
			success: function(data){
				if(data.status=="success"){
					$('#table').bootstrapTable('refreshOptions',{page:0,rows:10});
	    		}
			}
		}); 
 	}
 	$('#table').bootstrapTable({
 		sortable: true, 
 		sortName : 'gmt_created',
 		sortOrder : 'desc',
 		method: 'post',
        contentType: "application/x-www-form-urlencoded",
 	    dataType: "json",
 	    pagination: true,  
 	    singleSelect: false,
 	    striped: true,
 	    sidePagination: "server", //服务端处理分页
 	    url: 'getCrawls',
 	   queryParams:function queryParams(params){
	         return {
	        	rows: params.limit,
	        	page:params.offset,
	        	sortOrder: params.order,//排序
	            sortName:params.sort//排序字段
	         };
 	    },
 	    columns: [{
 	        field: 'id',
 	        title: '编号'
 	    }, {
 	        field: 'list_url',
 	        title: '列表地址'
 	    }, {
 	        field: 'list_item_selector',
 	        title: '条目选择器'
 	    }, {
 	        field: 'name',
 	        title: '对应栏目'
 	    }, {
 	        field: 'is_deleted',
 	        title: '是否有效',
 	       formatter:function(v,r,i){
 	    	  if(v=='N'){
    	    	str="有效";
    	     }else{
    	    	str="无效";
    	     }
 	    	  return str;
 	       }
 	    },{
 	        field: 'xx',
 	        title: '操作',
 	       formatter:function(v,r,i){
 	    	   var str= "<a href='javascript:void(0)'  onclick='showAddCrawlDetailDia(\""+r.id+"\")'>添加明细</a>";
 	    	    str+="<a href='javascript:void(0)' class='lspace' onclick='showUpdateDetialDia(\""+r.id+"\")'>修改明细</a>";
 	    	    str+="<a href='javascript:void(0)' class='lspace' onclick='showUpdateCrawDia(\""+r.id+"\")'>修改配置</a>";
 	    	    str+="<a href='javascript:void(0)' class='lspace' onclick='deleteItem(\""+r.id+"\")'>删除配置</a>";
 	    	    if(r.is_deleted=='N'){
 	    	    	str+="<a href='javascript:void(0)' class='lspace' onclick='onOffCrawl(\""+r.id+"\",\"Y\")'>停用</a>";
 	    	    }else{
 	    	    	str+="<a href='javascript:void(0)' class='lspace' onclick='onOffCrawl(\""+r.id+"\",\"N\")'>启用</a>";
 	    	    }
 	    	   return str;
 	       }
 	    }],
 	   onLoadSuccess:function(){
 	   }
 	});
</script>
	</body>
</html>