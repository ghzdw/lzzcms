<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!doctype html>
<html>
<head>
<meta charset="utf-8">
<title>选择要爬取的元素</title>
<script type="text/javascript" src="<%=request.getContextPath()%>/rs/js/jquery-1.12.4.min.js"></script>
<style type="text/css">

</style>
</head>
<body>
<div id="cont">
	<button id="btn">来一个uuid</button>
	<iframe id="tab_iframe" src="iframe.jsp" frameborder="0" height="100%" width="100%"></iframe>
</div>
<section>
</section>
<script>
        //测试Object.keys(obj)方法:返回数组，元素为对象的可用属性
		/*
		var obj={};
        var obj2={
			a:"a的值",
			b:333
		};
        delete  obj2["a"];
        console.log(Object.keys(obj));
        console.log(Object.keys(obj2));
		 */
		//测试生成uuid
		/*
        document.getElementById("btn").onclick=function(){
            var id=UUID.generate();
            alert(id);
        }
*/
        //测试访问iframe里的元素的3种方式，不能用jqeury的$(function(){})
		/*
      window.onload=function() {
            var iframeEle=$("#tab_iframe").get(0);
            var iframeDocumentObj=iframeEle.contentDocument||iframeEle.contentWindow.document;// contentDocument:获取iframe的document对象 contentWindow:获取iframe的window对象
            var selected=$(iframeDocumentObj).find("#div1");
            selected.text("通过方式一访问到iframe里面的东西");


            $("#tab_iframe").contents().find(".divclass").text("通过方式二访问到iframe里面的东西");

		  //指定上下文
            $(".divclass3",iframeDocumentObj).text("通过方式三访问到iframe里面的东西");

            $("#btn").css("color","red");
			//$(选择器，[context]):默认context是document
            $("#btn",document).css("color","blue");
			console.log( "默认doucment："+$("#btn",document).length);//1
			console.log( "指定一个cont："+$("#btn",$("#cont")).length);//1
             console.log("指定一个section："+ $("#btn",$("section")).length);//0
        };
*/
      // 测试 $.inArray(value,array,[fromIndex])
        //确定第一个参数在数组中的位置，从0开始计数(如果没有找到则返回 -1 )。
		/*
        var arr = [ 4, "Pete", 8, "John" ];
        console.log(jQuery.inArray("John", arr));  //3
        console.log(jQuery.inArray("John", arr,1));  //3
*/
		//测试ownerDocument
        /**包含俩：
		 * 一是  <!DocType>
         二是  documentElement
         */
      //  console.log($("#btn").get(0).ownerDocument);
	</script>
</body>
</html>