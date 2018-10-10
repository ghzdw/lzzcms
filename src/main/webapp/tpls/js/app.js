$(function(){
	var href=location.href;
	$("#mynavbarcollapse li>a").each(function(i,v){
		var ahref=$(v).attr("href");
		if(href.indexOf(ahref)>-1){
			$(this).parent("li").prevAll().removeClass("active");
			$(this).parent("li").addClass("active");
		}
	});
});