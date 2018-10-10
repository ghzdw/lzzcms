//附加一个列头onehead
var appendOneheadAndOneitemul = function(i,obj) {
    var str='<span class="onehead" data-common="'+obj["is_common"]+'">';
    str+='<label><input type="radio"  data-for="'+obj["col_name"]+'" name="cols">'+obj["showtip"];
    str+='</label><i>(已选择<em class="selectedCnt">0</em>行)</i></span>';
    $(".bottom .headarea").append(str);
    var oneitemul='<ul class="oneitemul" data-for="'+obj["col_name"]+'"></ul>';
    $(".bottom .bodyarea").append(oneitemul);
    fireSlide();
};
//触发左右滑动
var fireSlide=function(){
    var colCount=getCnt();
    var totalLen=colCount*300;
    var leftLen=parseInt($(".continner").css("left"));//内容区绝对定位left的值，初始32px
    //。bodyarea的可视宽度，当改变.bodyarea的left值的时候，通过width()求出来的也会增加
    var bodyareaViewLen=$(".bodyarea").width();
    if(leftLen==32){
    	$(".sliderLeftDiv").addClass("disabled");
    	if(totalLen>bodyareaViewLen){
    		 $(".sliderRightDiv").removeClass("disabled");
    	}else{
       	    $(".sliderRightDiv").addClass("disabled");
    	}
    }
    if(leftLen<32){
    	$(".sliderLeftDiv").removeClass("disabled");
    	leftLen=-leftLen;
    	 var minus=totalLen-(leftLen+32);
       bodyareaViewLen=bodyareaViewLen-(leftLen+32);
        if(minus>bodyareaViewLen){
             $(".sliderRightDiv").removeClass("disabled");
        }else{
        	$(".sliderRightDiv").addClass("disabled");
        }
    }
};
//得到共选择几列
var getCnt=function(){
    return $(".onehead").length;
};
var getIndexInArrByVal=function(arr,val){
	for (var i = 0; i < arr.length; i++) {
		if (arr[i] == val) return i;
	}
	return -1;
}
$(function () {
	//显示遮罩
	$(".backdroplayer").removeClass("out");
	$(".backdroplayer").addClass("in");
	var objToSubmit={
		cols:[],
		flip:{}
	};
    var iframeCss ='<style>\n' +
        '.mouseenter {\n' +
        '   background: #eea63f;\n' +
        '}\n' +
        'html {\n' +
        '   cursor: pointer;\n' +
        '}\n' +
        '.singleclickcss {\n' +
        '   outline:#eea63f solid 1px;\n' +
        '}\n' +
        '.listclickcss {\n' +
        '   outline:yellowgreen solid 1px;\n' +
        '}\n' +
        '.flipcss {\n'  +
        '   outline:purple solid 1px;\n' +
        '}\n' +
        '</style>';

    document.getElementById('iframe').onload = function () {
    	//加载完成的时候去除遮罩
    	$(".backdroplayer").removeClass("in");
		$(".backdroplayer").addClass("out");
        var height = $("#iframe").contents().find("html").outerHeight(true)+10;
        $("#iframe").height(height);//防止iframe高度出现滚动条
        var flowId = UUID.generate();
        var domAttrs = getIframeClassNames();
        var iframe = document.getElementById('iframe');
        var doc = iframe.contentDocument || iframe.contentWindow.document;
        $("#targetTitle").text($('title', doc).text());
        $(doc).find('head').append(iframeCss);
        $(doc).find('body *[zdwid]').off();
        $(doc).find('a').click(function (e) {
            e.preventDefault();
            return false;
        });
        $(doc).find('body *[zdwid]').hover(function (e) {//移入移出事件
            $(doc).find('body *[zdwid]').removeClass('mouseenter'); // e.stopPropagation();//老是全选
            $(this).addClass('mouseenter'); 
        }, function (e) {
            $(this).removeClass('mouseenter');
        });
        $(doc, doc).on('contextmenu', function() { //阻止上下文菜单
            return false;
        });
        $("body *[zdwid]", doc).on('mousedown', function(e) {  //点击事件：right click: e.which == 3, left click: e.which == 1
            e.stopPropagation();
            if (e.which === 3){//翻页处理,用顺序定位可能会出现上一页就定位错了 css:a.c1.c2 txt:下一页  
                e.preventDefault();
                processRightClick(this,doc,e,domAttrs);
            } else if (e.which === 1) {
            	var css_ = getElementSelector(this, domAttrs);
                processLeftClick(this,css_,doc,domAttrs,e);
            } 
        });
    };//iframe加载完成
    //处理鼠标右键单击事件,
    var processRightClick=function (that,doc,e,domAttrs) {
        var href = $(that).attr('zdwhref');
        if (href){//翻页只是记录，不执行跳转
            if($(that).attr("uuid_flip")){//已经被选择了再次单击鼠标中键就删除选择
                var uuid_flip = $(that).attr('uuid_flip');
                $("#fliptiptext").text("未配置");
                objToSubmit["flip"]={};
                //删除iframe页面选中效果
                $("body *[zdwid][uuid_flip="+uuid_flip+"]",doc).removeClass("flipcss");
                $("body *[zdwid][uuid_flip="+uuid_flip+"]",doc).removeAttr("uuid_flip");
                e.stopPropagation();
                return;
            }
            var flipcss=$(that).get(0).tagName;
            var attrs=$(that).get(0).attributes;
            for(var k=0;k<attrs.length;k++){
                var attrName=attrs[k].name;
                var attrVal=attrs[k].value;
                if("class"==attrName.toLowerCase()){
                    attrVal = filterClassValue(attrVal.trim(), domAttrs);
                    if(attrVal){
                    	flipcss+="."+attrVal.split(' ').join('.');
                    }
                    break;
                }
            }
            objToSubmit["flip"]["flipcss"]=flipcss;
            objToSubmit["flip"]["fliptxt"]=$(that).text();
            objToSubmit["flip"]["fliphref"]=href;
            $("#fliptiptext").text("已配置");
            $(that).addClass("flipcss");//单选点击加入样式
            $(that).attr("uuid_flip",UUID.generate());
        }
    };
    //处理鼠标左键单击事件:that 当前点击元素  css_当前点击元素的css选择器绝对路径  doc:iframe的document对象
    //domAttrs:iframe的所有classname数组  e:mousedown时的事件对象
    var processLeftClick=function (that,css_,doc,domAttrs,e) {
    	var data_for=$(".onehead").find(":checked").attr("data-for");//必须先选中一列才能可视化选择
    	if(data_for){
    		 if($("#checkList").prop("checked")){//列表复选框被选中
    	            if($(that).attr("uuid_muliticheck")){//已经被选择了（多选）再次单击鼠标右键就删除选择
    	                var uuid = $(that).attr('uuid_muliticheck');
    	                //删除页面底部内容
    	                deleteAllRelationByUuid(uuid,css_,"uuid_muliticheck","listclickcss");
    	                e.stopPropagation();
    	                return;
    	            }
    	            var mainSteps = css_.split(" > ");
    	            var data = {
    	            	uuid:[],	
    	                colName: $(".headarea").find("span.onehead input:checked").attr("data-for"),
    	                selectors: [],
    	                txts: [],
    	                data_for:data_for,
    	                idName:'uuid_muliticheck'
    	            };
    	            var selected = [];
    	            $(that.tagName, doc).each(function(i, tag) {
    	                var _css = getElementSelector(tag, domAttrs);
    	                var steps = _css.split(" > ");
    	                if (mainSteps.length == steps.length) {
    	                    var d = diffArray(mainSteps, steps);
    	                    if (d.length > 0 && d.length < 2) {
    	                        selected.push([_css, d[0]]);
    	                    }
    	                }
    	            });
    	            var maxSeq = filterMostItem(selected);
    	            var i = 0;
    	            selected.push([css_, maxSeq]);
    	            selected.forEach(function(obj) {
    	                if (obj[1] == maxSeq) {
    	                	var uuid_muliticheck=UUID.generate();
    	                    data["selectors"][i] = obj[0];
    	                    data["txts"][i] = $(doc).find(obj[0]).text().trim();
    	                    data["uuid"][i] =uuid_muliticheck ;
    	                    $(obj[0], doc).addClass("listclickcss");
    	                    $(obj[0], doc).attr("uuid_muliticheck", uuid_muliticheck);
    	                    i ++;
    	                }
    	            });
    	            appendOneitemli(data);
    	        }else{//列表选择结束,单项选择开始
    	            if($(that).attr("uuid")){//已经被选择了（单选）再次单击鼠标左键就删除选择
    	                var uuid = $(that).attr('uuid');
    	                deleteAllRelationByUuid(uuid,css_,"uuid","singleclickcss");
    	                e.stopPropagation();
    	                return;
    	            }
    	            var id=UUID.generate();
    	            var data = {
    	            	uuid:[id],	
	            		colName: $(".headarea").find("span.onehead input:checked").attr("data-for"),
	            		selectors: [css_],
    	                txts: [$(doc).find(css_).text().trim()],
    	                data_for:data_for,
    	                idName:'uuid'
    	            };
    	            appendOneitemli(data);
    	            $(that).addClass("singleclickcss");//单选点击加入样式
    	            $(that).attr("uuid",id);
    	        }//单项选择结束
    	        fireSlide();
    	}else{
    		alert("请先选择要抓取内容的字段");
    	}
    };
  /*通过uuid删除页面选中和已经附加的列和全局对象里面的选择器:uuid:要删除元素的uuid的值或者uuid_muliticheck的值
   * CorrespondingSelector:要删除元素对应的选择器
   * idName:uuid或者uuid_muliticheck
   * className：singleclickcss或者listclickcss
   */
    var deleteAllRelationByUuid=function(uuid,CorrespondingSelector,idName,className){
    	 var iframe = document.getElementById('iframe');
         var doc = iframe.contentDocument || iframe.contentWindow.document;
    	//删除objToSubmit中cols里该列下的当前选择器和页面底部内容
        var pageBottomOneitemli=$(".bottom .bodyarea li.oneitemli["+idName+"='" + uuid + "']");
        var correspondingUl=pageBottomOneitemli.parents("ul");
        var colName=correspondingUl.attr("data-for");
        $.each(objToSubmit["cols"],function(i,obj){
        	if(obj["colName"]==colName){
        		var colNameSelectors=obj["selectors"];
        		var index=getIndexInArrByVal(colNameSelectors, CorrespondingSelector);
        		colNameSelectors.splice(index,1);
        		return false;
        	}
        });
        pageBottomOneitemli.remove();
      //更新当前列选中了多少行
    	$(":checked").parents(".onehead").find(".selectedCnt").html(correspondingUl.find("li").length);
        //删除iframe页面选中效果
        $("body *[zdwid]["+idName+"="+uuid+"]",doc).removeClass(className);
        $("body *[zdwid]["+idName+"="+uuid+"]",doc).removeAttr(idName);
    };
    //附加选中的
    var appendOneitemli = function(data) {
    	var tmpObj={};
    	tmpObj["colName"]=data["colName"];
    	tmpObj["selectors"]=data["selectors"];
    	var flag=true;
    	$.each(objToSubmit["cols"],function(i,obj){//判断objToSubmit的cols表示的数组里面是否有该字段
    		if(obj["colName"]==tmpObj["colName"]){//有，比如mainbody
				for(var i=0;i<tmpObj["selectors"].length;i++){
		    		obj["selectors"].push(tmpObj["selectors"][i]);//把选择器附加到已有colName对应的selectors选择器数组中
		    	}
    		   flag=false;
    		   return false;
    		}
    	});
    	if(flag){//objToSubmit的cols表示的数组里面原先没有该字段
    		objToSubmit["cols"].push(tmpObj);
    	}
    	var str='';
    	var idName=data["idName"];
    	for(var i=0;i<data["txts"].length;i++){
    		var txt=data["txts"][i];
    		var oneUuid=data["uuid"][i];
    		var oneSelector=data["selectors"][i];
    		str+='<li title="'+txt+'" '+idName+'="'+oneUuid+'" class="oneitemli">'+txt;
    		str+='<img '+idName+'="'+oneUuid+'" txtselector="'+oneSelector+'" class="remove" src="../resources/imgs/remove.png">';
    		str+='</li>';
    	}
    	//当前选中的列明对应的ul
    	var colNameCorrespondingUl=$(".bottom .bodyarea .oneitemul[data-for="+data["data_for"]+"]");
    	colNameCorrespondingUl.append(str);
    	//更新当前列选中了多少行
    	$(":checked").parents(".onehead").find(".selectedCnt").html(colNameCorrespondingUl.find("li").length);
    };
    //向右滑动
    $(".sliderRightDiv").click(function () {
        if(!$(".sliderRightDiv").hasClass("disabled")){
            var nowLeft=$(".continner").css("left");
            $(".continner").css("left",parseInt(nowLeft)-300+"px");
            fireSlide();
        }
    });
    //向左滑动
    $(".sliderLeftDiv").click(function () {
        if(!$(".sliderLeftDiv").hasClass("disabled")){
            var nowLeft=$(".continner").css("left");
           $(".continner").css("left",parseInt(nowLeft)+300+"px");
            fireSlide();
        }
    });
    //向上向下滑动点击
    $(".statussign").click(function () {
        var src=$(this).find("img").attr("src");
        if(src.indexOf("_up")!=-1){
            $(".bottom").css("bottom","0px");
            $("#iframe").css("margin-bottom","223px");
            $(this).find("img").attr("src",src.replace("_up","_down"));
        }else {
            $(".bottom").css("bottom","-180px");
            $("#iframe").css("margin-bottom","43px");
            $(this).find("img").attr("src",src.replace("_down","_up"));
        }
    });
    //提示
    $(".questionimg").first().poshytip({
        className:'tip-yellow',
        offsetY:30,
        content:function () {
            var tip= $("#listtip").html();
            return tip;
        }
    });
    $(".questionimg").last().poshytip({
    	className:'tip-yellow',
    	offsetY:30,
    	content:function () {
    		var tip= $("#fliptip").html();
    		return tip;
    	}
    });
    //附加选择的列
	$.ajax({
		type:"post",
		url:'getCfgById',
		data:{"cfgId":$("#cfgId").val()},
		success:function(data){
			$.each(data,function(i,obj){
				appendOneheadAndOneitemul(i,obj);		
				if(i==0){
					$("#columnId").val(obj["column_id"]);
					$("#addfortb").val(obj["addfortb"]);
					$("#website").val(obj["website"]);
				}
			});
		    //底部滚动条:要先把ul框架放到页面上了，再调用这个滚动条，才能放到滚动条添加的div里面
		    $(".bodyarea").mCustomScrollbar();
		}
	});
	/*
	var obj={
		id:uuid,
		columnId:2,
		website:http,
		addfortb:'lzz_addforarticle',
		commCols:"comm_title,comm_click...",
		extraCols:"mainbody,mainbody2..."
		cols:[{
				colName:comm_title,
				selectors:[]
				},{
					colName:mainbody,
					selectors:[]
				}
		       
		       ],
		flip:{
			flipcss:a.c1.c2,
			fliptxt:下一页,
			fliphref:http://df
		}       
	}
	*///提交
	$("#start").click(function(){
		if(objToSubmit["cols"].length==0){
			alert("请先配置再提交");
			return false;
		}
		objToSubmit["uuid"]=$("#cfgId").val();
		objToSubmit["columnId"]=$("#columnId").val();
		objToSubmit["website"]=$("#website").val();
		objToSubmit["addfortb"]=$("#addfortb").val();
		var commCols="";
		var extraCols="";
		$.each($(".headarea").find("span.onehead"),function(i,obj){ //得到公共表里面的列和附加表里面的列
			var is_common=$(this).attr("data-common");
			if(is_common=="1"){
				commCols+=$(this).find("input").attr("data-for")+",";
			}else if(is_common="0"){
				extraCols+=$(this).find("input").attr("data-for")+",";
			}
		});
		commCols=commCols.substring(0, commCols.length-1);
		extraCols=extraCols.substring(0, extraCols.length-1);
		objToSubmit["commCols"]=commCols;
		objToSubmit["extraCols"]=extraCols;
	//	console.log(objToSubmit);
		$.ajax({
			type:"post",
			url:'startSpiderById',
			data:{"param":JSON.stringify(objToSubmit)},
			success:function(data){
				if(data.status=="ok"){
					alert("执行完成");
				}else{
					alert(data.status);
				}
			}
		});
	});
	//列选择事件监听
	$(".headarea").on("click","span.onehead>label",function(e){
		$(".nowcfgcolname").html($(this).text());
	});
	//删除图片点击监听
	$(".bodyarea").on("click","img.remove",function(e){
		var uuid=$(this).attr("uuid");
		var uuid_muliticheck=$(this).attr("uuid_muliticheck");
		if(uuid){
			deleteAllRelationByUuid(uuid,$(this).attr("txtselector"),"uuid","singleclickcss");
		}else if(uuid_muliticheck){
			deleteAllRelationByUuid(uuid_muliticheck,$(this).attr("txtselector"),"uuid_muliticheck","listclickcss");
		}
	});
});