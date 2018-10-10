<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
   	<h3>附加字段</h3>
	 <table  class="toAdd_table" >
	 	<c:forEach items="${requestScope.extraCols}" var="extraCol">
	 	  <tr>
		    	<c:choose >
		    		<c:when test="${extraCol.colType eq 'varchar'}">
		    			<td style="width:90px;"><label for="extraCols_${extraCol.colName }">${extraCol.showTip }</label></td>
		   				<td>
		   						<input type="text" style="width:90%;height:30px" name="extraCols_${extraCol.colName}" id="extraCols_${extraCol.colName }">
		   				</td>
		    		</c:when>
		    		<c:when test="${extraCol.colType eq 'int'}">
		    			<td style="width:90px;"><label for="extraCols_${extraCol.colName }">${extraCol.showTip }</label></td>
		   				<td>
		   					<input type="text" style="width:90%;height:30px"  name="extraCols_${extraCol.colName}" id="extraCols_${extraCol.colName }">
		   				</td>
		    		</c:when>
		    		<c:when test="${extraCol.colType eq 'richtext'}">
	    		        <td style="width:90px;"><label for="extraCols_${extraCol.colName }">${extraCol.showTip }</label></td>
		   				<td style="width:90%;">
		   						<textarea name="extraCols_${extraCol.colName}" id="extraCols_${extraCol.colName }">输入内容......</textarea>
		   				</td>
		   				 <script type="text/javascript">
		   				 	$(function(){
		   				 		UE.delEditor("extraCols_${extraCol.colName}");
		   				 	    toAddUe = UE.getEditor("extraCols_${extraCol.colName}",{
									    autoClearinitialContent:true,
							            wordCount:false,
							            elementPathEnabled:false,
							            initialFrameHeight:300
								 });
								 //ue.addListener("blur",function(){ });  
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