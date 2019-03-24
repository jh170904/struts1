<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	request.setCharacterEncoding("UTF-8");
	String cp = request.getContextPath();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>이미지 게시판</title>
<link rel="stylesheet" href="<%=cp %>/imageTest/css/style.css" type="text/css" />
<link rel="stylesheet" href="<%=cp %>/imageTest/css/list.css" type="text/css" />

</head>
<body>

<br/><br/>

<table width="600" align="center" style="font-family: 돋움; font-size: 10pt;" cellspacing="2" cellpadding="1" >
<tr id="bbsList">
	<td id="bbsList_title" colspan="3">
	이미지 게시판
	</td>
</tr>
</table>

<table width="600" border="0" cellpadding="0" cellspacing="0" align="center">
   <tr height="30">
      <td align="left" width="50%">
        Total ${totalDataCount} articles, ${totalPage} pages / Now page is ${currentPage}
      </td>
      <td align="right">
        <input type="button" value=" 게시물 등록 " onclick="javascript:location.href='<%=cp%>/img.do?method=write'" class="btn1"/>
      </td>
    </tr>
</table>

<table width="600" border="0" cellpadding="0" cellspacing="0" align="center">
  <tr><td height="3" bgcolor="#DBDBDB" align="center"></td></tr>
</table>

<table width="600" border="0" cellspacing="1" cellpadding="3"
   bgColor="#FFFFFF" align="center">

	<c:set var="n" value="0"/>
	<c:forEach var="dto" items="${lists}">
		<c:if test="${n==0}">
			<tr bgcolor="#FFFFFF" ></tr>
		</c:if>
		<c:if test="${n!=0&&n%3==0 }">
			<tr bgcolor="#FFFFFF" ></tr>
		</c:if>
      <td width="200" align="center">
      <a href="${imagePath}/${dto.saveFileName}">
	    <img src="${imagePath}/${dto.saveFileName}" width="180" height="180" border="0" />
	   </a>
	    <br/>${dto.subject}&nbsp;<a href="<%=cp%>/img.do?method=delete&num=${dto.num}">삭제</a>
	</td>
	<c:set var="n" value="${n+1}"/>
	</c:forEach>
	
	<c:if test="${n>0||n%3!=0 }">
		<c:forEach var="i" begin="${n%3+1}" end="3" step="1">
			<td>&nbsp;</td>
		</c:forEach>
	</c:if>
	
	<c:if test="${n!=0 }">
		</tr>
	</c:if>
	
	<c:if test="${totalDataCount != 0}">
	<tr bgcolor="#FFFFFF">
	   <td align="center" height="30" colspan="3">${pageIndexList}</td>
    </tr>
	</c:if>
	
	<c:if test="${totalDataCount == 0}">
	<tr bgcolor="#FFFFFF">
       <td align="center" colspan="3" height="30">등록된 자료가 없습니다.</td>
    </tr>
	</c:if>
</table>

<table width="600" border="0" cellpadding="0" cellspacing="0" align="center">
  <tr><td height="3" bgcolor="#DBDBDB" align="center"></td></tr>
</table>
</body>
</html>
