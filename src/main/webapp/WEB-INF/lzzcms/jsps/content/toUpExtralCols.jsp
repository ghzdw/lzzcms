<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
   	<h3>附加字段</h3>
	 <table  class="toUp_table" >
	 	<c:forEach items="${requestScope.extraCols}" var="extraCol">
	 	   <c:set var="tmpColName" value="${extraCol.colName }" scope="page"></c:set>
	 	  <tr>
		    	<c:choose >
		    		<c:when test="${extraCol.colType eq 'varchar'}">
		    			<td style="width:90px;"><label for="toUpExtraCols_${extraCol.colName }">${extraCol.showTip }</label></td>
		   				<td>
		   						<input type="text" style="width:90%;height:30px" value="${oneAddFor[tmpColName]}" 
		   						 name="toUpExtraCols_${extraCol.colName}" id="toUpExtraCols_${extraCol.colName }">
		   				</td>
		    		</c:when>
		    		<c:when test="${extraCol.colType eq 'int'}">
		    			<td style="width:90px;"><label for="toUpExtraCols_${extraCol.colName }">${extraCol.showTip }</label></td>
		   				<td>
		   						<input type="text" style="width:90%;height:30px"   value="${oneAddFor[tmpColName]}" 
		   						 name="toUpExtraCols_${extraCol.colName}" id="toUpExtraCols_${extraCol.colName }">
		   				</td>
		    		</c:when>
		    		<c:when test="${extraCol.colType eq 'richtext'}">
	    		        <td style="width:90px;"><label for="toUpExtraCols_${extraCol.colName }">${extraCol.showTip }</label></td>
	    		        <!-- 内容不能直接放在这里，html先被去掉了 -->
		   				<td style="width:90%;"><textarea name="toUpExtraCols_${extraCol.colName}" 
		   				id="toUpExtraCols_${extraCol.colName }">${oneAddFor[tmpColName]}</textarea></td>
		   				<div id="tmpSaveCode" style="display:none;">
<%-- 					        ${oneAddFor[tmpColName]} --%>
					    </div>
		   				 <script type="text/javascript">
							$(function(){
								UE.delEditor("toUpExtraCols_${extraCol.colName}");
								var ue=UE.getEditor("toUpExtraCols_${extraCol.colName}",{
							            wordCount:false,
							            elementPathEnabled:false,
							            initialFrameHeight:300
								});
								//ue.ready(function() {
									//ue.setContent($("#tmpSaveCode").html());
								//});
							});
						 </script>
		    		</c:when>
		    		<c:otherwise>
	    				未知类型
		    		</c:otherwise>
		    	</c:choose>
		  </tr>
	 	</c:forEach>
	</table>