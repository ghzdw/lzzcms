<%@page import="java.util.*"%>
<%@page import="java.util.Map.Entry"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<style>
.tr{
	padding:3px;
	font-size:14px;
}
</style>
<%
Map<String,Object>	addforinfo=(Map<String,Object>)request.getAttribute("addforinfo");
Set<Entry<String, Object>> entrySet = addforinfo.entrySet();
for (Iterator<Entry<String, Object>> iterator = entrySet.iterator(); iterator.hasNext();) {
	Entry<String, Object> entry =  iterator.next();
%>
	<div class="tr"><%=entry.getKey() %>:	<%=entry.getValue() %></div>
<%	
}
%>