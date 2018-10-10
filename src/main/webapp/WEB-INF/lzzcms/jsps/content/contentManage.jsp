<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="zdw" uri="/lzzcms" %> 
 <script type="text/javascript">
   function addforinfo(comm_id,channel_id){
		var param={};
		param['comm_id']=comm_id;
		param['channel_id']=channel_id;
		$("#contM_addforinfowin").load('getAddforinfo',param);
		$("#contM_addforinfowin").window("open");
	}
$(function(){
	//usedHeight：上边用的125+下边用的50
	var docHeight=$(document).outerHeight(true),usedHeight=175,trHeight=31;
	var rowCnt=Math.floor((docHeight-usedHeight)/trHeight);
	var rowCntList=[rowCnt,rowCnt*2,rowCnt*3,rowCnt*4,rowCnt*5,rowCnt*10];
	//给表格每一列放上去时加上浮动显示全部内容
	function addTitle(v){
		if(v){
			return "<span title='"+v+"'>"+v+"</span>";
		}
	}
	//内容管理表格定义
	$("#contentManage_contentGrid").datagrid({
		striped:true,
		rownumbers:true,
		pagination:true,
		pageList:rowCntList,
		pageSize:rowCnt,
		method:"post",
	    columns:[[    
			{field:'',checkbox:true},           
	        {field:'comm_id',title:'附加信息',formatter:function(value,row,index){
	        	return   "<zdw:hasRight url='addFieldDia'><a class='likeacont' href='#' onclick='addforinfo(\""+row.comm_id+"\",\""+row.channel_id+"\")'>附加信息</a></zdw:hasRight>";
	        }},    
	        {field:'comm_title',title:'标题',halign:'center',sortable:true,resizable:true,
	         width:200,formatter:function(v,row,index){
        		if(v){
        			if(row.abs_comm_htmlpath){
	        			return "<a target='_blank' href='"+row.abs_comm_htmlpath+"'><span title='"+v+"'>"+v+"</span></a>";
        			}else{
        				return "<span title='"+v+"'>"+v+"</span>";
        			}
        		}	
	        }},    
	        {field:'comm_shorttitle',title:'简短标题',halign:'center',sortable:true,resizable:true,width:100,formatter:addTitle},    
	        {field:'comm_click',title:'点击量',halign:'center',sortable:true,resizable:true,width:80,formatter:addTitle},    
	        {field:'author_name',title:'作者',halign:'center',sortable:true,resizable:true,width:90,formatter:addTitle},    
	        {field:'comm_publishdate',title:'发布日期',halign:'center',sortable:true,resizable:true,width:130,formatter:addTitle},    
	        {field:'comm_keywords',title:'网页关键字',halign:'center',sortable:true,resizable:true,width:200,formatter:addTitle},    
	        {field:'comm_desc',title:'网页描述',halign:'center',sortable:true,resizable:true,width:200,formatter:addTitle},    
	        {field:'come_from',title:'来源',halign:'center',sortable:true,resizable:true,width:80,formatter:addTitle},    
	        {field:'comm_thumbpic',title:'缩略图地址',halign:'center',sortable:true,resizable:true,width:200,formatter:addTitle},    
	        {field:'define_flag',title:'自定义标记',halign:'center',sortable:true,resizable:true,width:100,formatter:addTitle},    
	        {field:'comm_intro',title:'摘要',halign:'center',sortable:true,resizable:true,width:200,formatter:addTitle},    
	        {field:'comm_htmlpath',title:'静态文件位置',halign:'center',sortable:true,resizable:true,width:200,formatter:addTitle},    
	        {field:'channelname',title:'所属频道',halign:'center',sortable:true,resizable:true,width:120,formatter:addTitle},  
	        {field:'name',title:'所属栏目',halign:'center',sortable:true,resizable:true,width:120,formatter:addTitle}
	    ]],    
		url:"listConts",
		remoteSort:false,//利用下面的onSortColumn:function事件实现
		onSortColumn:function(sort,order){
			queryCont({
				"sort":sort,
				"order":order
			});
		}
	});
	//初始化查询条件
	$("#contentManage_channel_id").combobox({    //模型下拉框
	    url:'getForCombobox?page=contM',    
	    valueField:'id',    
	    textField:'channelname',
	    editable:false,
	    value:"",
	    height:30,
	    onChange:function(newValue, oldValue){
	    	if(newValue==""){
	    		$("#contentManage_column_id").combobox("select","");
	    	}else{
	    		$("#contentManage_column_id").combobox("reload","getColumnByChanelId?chanelId="+newValue);
	    	}
	    }
	});  
	$("#contentManage_column_id").combobox({//栏目下拉框
	    valueField:'id',    
	    textField:'name',
	    editable:false,
	    height:30,
	    data:[{id: '',name: '---所有---'}],
		value:""
	});
	$("#contentManage_comm_click_compare").combobox({//点击量符号下拉框
	    editable:false,
	    width:70,
	    height:30,
	    value:""
	});
	$("#contentManage_comm_src").combobox({//来源下拉框
		 url:'getSrcForCombobox?frompage=contM',    
	    valueField:'id',    
	    textField:'come_from',
	    editable:false,
	    height:30,
	     value:""
	});
	$("#contentManage_comm_author").combobox({//作者下拉框
		 url:'getAuthorForCombobox?frompage=contM',    
		    valueField:'id',    
		    textField:'author_name',
		    editable:false,
		    height:30,
		    value:""
	});
	$("#contentManage_comm_defineflag").combobox({//自定义标记下拉框
		 url:'getDefineFlagForCombobox?frompage=contM',    
		    valueField:'id',    
		    textField:'define_flag',
		    height:30,
		    editable:false,
		    value:""
	});
	//设置日期的格式
	$("#contentManage_comm_publishdate").datebox({   
		height:30,
		formatter:function(date){
			var y = date.getFullYear();
			var m = check(date.getMonth()+1);
			var d = check(date.getDate());
			return y+'-'+m+'-'+d;
		}
	});
	function check(val) {  
        if (val < 10) {  
            return "0" + val;  
        } else {  
            return val;  
        }  
	} 
	$("#contentManage_query").on("click",queryCont);
	$("#contentManage_reset").click(function(){//处理重置按钮
	  	$("#contentManage_query_form").form("reset");
	});
	function queryCont(obj){ //处理查询按钮 obj用来接收点击排序时候的参数
		if($("#contentManage_query_form").form('validate')){
			var data=str2Json("#contentManage_query_form");
			if(obj){
				data["sort"]=obj["sort"];//排序的字段名称
				data["order"]=obj["order"];//排序方式
			}
			var clickNum=data["contentManage_comm_click"];
			if(clickNum&&!(/^\d+$/.test(clickNum))){
				$.messager.alert('错误','点击量只能是正整数!','error');
				return;
			}
			if((data["contentManage_comm_click_compare"]==""&&clickNum!="")
					||(clickNum!=""&&data["contentManage_comm_click"]=="")){
				$.messager.alert('错误','点击量符号和点击量要么都选择要么都不选择!','error');
				return ;
			}
			//默认的：排序参数需要单独通过request获取了,查询过一次后再点击排序这时候发送的参数也包含已经传过的参数(即针对筛选后的结果排序)
			//我准备处理下onSortColumn事件
			$("#contentManage_contentGrid").datagrid("options").queryParams={"param":JSON.stringify(data)};
			$("#contentManage_contentGrid").datagrid("load");
			$("#contentManage_querywin").window("close");
		}else{
			$.messager.alert('错误','输入不合法!','error');
		}
	}	
	$("#addContBtn").click(function(){//增加内容
 		addTab("新增内容","toAdd",{method:'get'});
	});	
 	$("#deleteContBtn").click(function(){//删除内容
 		var selections=$("#contentManage_contentGrid").datagrid("getSelections");
		var ids=[];
		if(selections.length==0){
			$.messager.alert("提示","请先选择要删除的行!","error");
		}else{
			$.messager.confirm('确认对话框', '确定要删除文档信息吗？', function(r){
				if (r){
					for(var i=0;i<selections.length;i++){
						var obj=new Object();
						obj["comm_id"]=selections[i]["comm_id"];
						obj["chl_id"]=selections[i]["channel_id"];
						obj["comm_thumbpic"]=selections[i]["comm_thumbpic"];
						obj["comm_htmlpath"]=selections[i]["comm_htmlpath"];
						ids.push(obj);//[{"comm_id":1,"chl_id":1},{"comm_id":17,"chl_id":1}]
					}
				  	var params={};
				    params["param"]=JSON.stringify(ids);
					$.ajax({
						type:"post",
						url:"deleteContent",
						data:params,
						success:function(result){
							if(result=="success"){
								queryCont();
							}else{
					        	$.messager.alert('错误',result,'error');
					        }
						}
					});
				}
			});
	    }
	});	
 	$("#editContBtn").click(function(){//编辑内容
 		var selections=$("#contentManage_contentGrid").datagrid("getSelections");
		if(selections.length!=1){
			$.messager.alert("提示","请先选择要编辑的行且一次只能编辑一个文档!","error");
		}else{
			addTab("修改内容","toUpdate",{method:'get',queryParams:{id:selections[0]["comm_id"]}});
	    }
	});	
 	$("#searchContBtn").click(function(){//搜索内容
 		$("#contentManage_querywin").window("open");
	});	
});
</script>
<style>
	.datagrid-cell{
		overflow: hidden;
		white-space: nowrap;
		text-overflow:ellipsis;
	}
	.contentManage_table{
	    width:100%;
	    padding:0;
	    margin:0;
	}
	.contentManage_table tr{
	    height:35px;
	    line-height:35px;
	}
	.contentManage_table label{
		width:90px;
	    display: inline-block;
	}
	.contentManage_table input{
		width:170px;
		outline:none;
		font-size:14px !important;
		font-weight:normal;
	}
	.contentManage_table input:last-child{
		width:550px;
		height:30px;
		border-radius:4px;  
	    border:1px solid rgba(102,175,233,.75); 
	    font-size:13px;
	    outline:none;  
	}
	.contentManage_table input:last-child:hover{
		outline:none;  
	    border-color:rgba(102,175,233,.75);  
	    -webkit-box-shadow:0 0 4px rgba(102,175,233,.5);  
	    -moz-box-shadow:0 0 4px rgba(102,175,233,.5);  
	    -o-box-shadow:0 0 4px rgba(102,175,233,.5);  
	    box-shadow:0 0 4px rgba(102,175,233,.5);  
	    -webkit-transition:border  .2s, -webkit-box-shadow  .5s;  
	    -moz-transition:border  .2s, -moz-box-shadow  .5s;  
	    -o-transition: border  .2s, -o-box-shadow  .5s;  
	    transition:border  .2s, box-shadow  .5s;  
	}
	.contentManage_table td{
		text-align:left;
		padding:0;
		margin:0;
	}
	.contentManage_table td:last-child{
		text-align:center;
	}
	.contentManage_table td:last-child a:first-child{
		margin-left:-100px;
	}
	.contentManage_table td:last-child a:last-child{
		margin-left:100px;
	}
	.contentManage_table td:nth-of-type(6n+1){
		width:120px;
	}
	.opera{
		padding-bottom:3px;
	}
	.opera span{
		display: inline-block;
		padding-right:8px;
	}
	.likeacont{
		width:70px;
		display: inline-block;
		height:30px;
		line-height: 30px;
		color:#7D26CD;
		text-decoration: none;
		margin: 0;
		padding:0;
	}
</style>
<div id="contentManage_querywin" class="easyui-window" title="请输入查询条件进行筛选" style="width:1000px;height:'auto';padding:10px;background:#fff;"   
        data-options="iconCls:'icon-search',modal:true,collapsible:false,minimizable:false,closed:true">   
	 <form action="" method="post" id="contentManage_query_form">
	 	<table  class="contentManage_table">
		  <tr>
		    <td><label for="contentManage_channel_id">模型</label></td>
		      <td><input type="text" id="contentManage_channel_id" name="contentManage_channel_id"/>
		    </td>
		    <td><label for="contentManage_column_id">栏目</label></td>
		    	<td><input type="text" id="contentManage_column_id" name="contentManage_column_id"/>
		    </td>
		   <td><label for="contentManage_comm_click" style="width:40px;">点击量</label></td>
			   <td style="text-align:left"> <select   name="contentManage_comm_click_compare" id="contentManage_comm_click_compare">
			    	<option value="">未选择</option>
			    	<option value=">">大于</option>
			    	<option value="=">等于</option>
			    	<option value="<">小于</option>
			     </select>
			    <input type="text" name="contentManage_comm_click"  class="easyui-numberbox" style="height:30px;" id="contentManage_comm_click" />
		    </td>
		  </tr>
		  <tr>
		    <td><label for="contentManage_comm_author">作者</label> </td>
		    <td><input type="text"  name="contentManage_comm_author" id="contentManage_comm_author"/>
		    </td>
		    <td><label for="contentManage_comm_publishdate">发布日期</label></td>
		    <td>
		    	<input class="easyui-datebox" type="text" data-options="editable:false" name="contentManage_comm_publishdate" id="contentManage_comm_publishdate"/>
		    </td>
		     <td><label for="contentManage_comm_src">来源</label></td>
		     	<td style="text-align:left"><input type="text" name="contentManage_comm_src" id="contentManage_comm_src"/></td>
		  </tr>
		  <tr>
		    <td><label for="contentManage_comm_defineflag">自定义标记</label></td>
		    <td><input type="text" name="contentManage_comm_defineflag" id="contentManage_comm_defineflag"/></td>
		    <td><label for="contentManage_commmonSelect">通用查询</label></td>
		    <td colspan="3" style="text-align:left"><input  placeholder="标题/简短标题/网页关键字/网页描述/缩略图地址/摘要/静态文件位置" type="text" name="contentManage_commmonSelect" id="contentManage_commmonSelect"/>
		    </td>
		  </tr>
		  <tr>
		    <td colspan="6">
		    <a href="#"  id="contentManage_reset" class="easyui-linkbutton" data-options="iconCls:'icon-search'" >重置</a>
		    <a href="#" id="contentManage_query"  class="easyui-linkbutton" data-options="iconCls:'icon-search'" >查询</a>
		    </td>
		  </tr>
		</table>
	 </form>
</div> 
<div class="opera">
	<zdw:hasRight url='toAdd'>
		<span>
			<a href="#" id="addContBtn"  class="easyui-linkbutton" data-options="iconCls:'icon-add'">&nbsp;新增内容</a>
		</span>
	</zdw:hasRight>
	<zdw:hasRight url='deleteContent'>
		<span>	
			<a href="#" id="deleteContBtn"  class="easyui-linkbutton" data-options="iconCls:'icon-clear'">&nbsp;删除内容</a>
		</span>	
	</zdw:hasRight>
	<zdw:hasRight url='toUpdate'>
		<span>	
			<a href="#" id="editContBtn"  class="easyui-linkbutton" data-options="iconCls:'icon-edit'">&nbsp;编辑内容</a>
		</span>	
	</zdw:hasRight>
	<span>	
		<a href="#" id="searchContBtn"  class="easyui-linkbutton" data-options="iconCls:'icon-search'">&nbsp;查询</a>
	</span>	
</div>
 <div id="contentManage_contentGrid"></div>
 <div id="contM_addforinfowin" class="easyui-window" title="文档附加信息查看" style="width:600px;height:400px"   
        data-options="iconCls:'icon-more',modal:true,collapsible:false,minimizable:false,closed:true">
 </div>