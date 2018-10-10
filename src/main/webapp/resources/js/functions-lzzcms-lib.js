	//添加一个选项卡到布局的中间区域
	function addTab(txt,url,conf){
		var defaults={
			method:'post',
			queryParams:{}	
		};
		var opts=$.extend({},defaults,conf);
		if($('#centerArea').tabs("exists",txt)){
			$('#centerArea').tabs("select",txt);
		}else{
		   //	var content="<iframe   frameborder=0 scrolling='no' width='100%' height='800px' src='"+url+"'></iframe>";
			$('#centerArea').tabs("add",{
				title:txt,
				narrow:true,
				closable:true,
				method:opts.method,
				queryParams:opts.queryParams,
				href:url
			});
		}
	}
	/*
		把表单的值转为json:对于复选框，下拉框等一名多值的情况也适用(通过,隔开)
	 * serialize得到：uname=admin&pwd=admi&code=&remember=remember&remember=remember2&a=a1&a=a2
       	执行此方法后{uname: "admin", pwd: "admi", code: "", remember: "remember2",a:"a1,a2"}
       	当表单内容含有&或者=就出问题了，使用serializeArray解决
	 */
	function str2Json(formSelector){
		var arr=$(formSelector).serializeArray();//"#pageCfg_form"
		var data={};
		$.each(arr,function(i,obj){//[{name:"a",value:"b"},{name:"f",value:"zuqiu"},{name:f,value:"lanqiu"},{name:"uname",value:"赵道稳"},{name:"record",value:"<a href='adf'..."}]
			if(data[obj["name"]]){//复选框可能提交多个
				data[obj["name"]]=data[obj["name"]]+","+obj["value"];
			}else{
				data[obj["name"]]=obj["value"];
			}
		});
		return data;
	}
	//校验是正整数
	function gt0Int(num){
		var reg=/^\d{1,}$/g;
		if(!reg.test(num)){
			return false;
		}
		return true;
	}
	//角色管理页面
	function recursionUnCheckTree(target){
		var children=$("#rightTree").tree("getChildren",target);
		$.each(children,function(index,value){
			var one=$("#rightTree").tree("find",value.id);
			if(!$("#rightTree").tree("isLeaf",one.target)){//非叶子
				recursionUnCheckTree(one.target);
			}else{
				$("#rightTree").tree("uncheck",one.target);
			}
		});
	}
	//关闭tab
	function closeTab(id){
		if(id=="closeCxt_crt"){
			var crtTab=$("#centerArea").tabs("getSelected");
			var crtIndex=$("#centerArea").tabs("getTabIndex",crtTab);
			$("#centerArea").tabs("close",crtIndex);
		}else if(id=="closeCxt_all"){
			var allTabs=$("#centerArea").tabs("tabs");
			var len=allTabs.length;
			while(len!=0){
				$("#centerArea").tabs("close",0);
				len=$("#centerArea").tabs("tabs").length;
			}
		}else if(id=="closeCxt_other"){
			var crtTab=$("#centerArea").tabs("getSelected");
			var crtIndex=$("#centerArea").tabs("getTabIndex",crtTab);//跟addtab时指定的id无关，索引动态改变
			var allTabs=$("#centerArea").tabs("tabs");
			var len=allTabs.length;
			for(var i=0;i<len;i++){
				if(i<crtIndex){
					$("#centerArea").tabs("close",0);
				}else if(i>crtIndex){
					$("#centerArea").tabs("close",1);
				}
			}
		}
	}
	function randomInt(){
		var ra=Math.random()*100;
		var raInt=Math.ceil(ra);
		return raInt;
	}	
