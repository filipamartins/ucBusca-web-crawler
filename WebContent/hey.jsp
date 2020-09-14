<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<s:include value="head.jsp" />

</head>
<body>
 	<s:include value="navbar.jsp" />
 	<div class="ml-2">
		<c:choose>
			<c:when test="${session.loggedin == true}">
				<br><h3>Welcome to ucBusca, <s:property value="#session.userBean.username" />.</h3><br>
			</c:when>
			<c:otherwise>
				<p>Welcome, anonymous user. Say HEY to someone.</p>
			</c:otherwise>
		</c:choose>
		<p><a href="<s:url action='index' />" >Return to home page</a></p>
	</div>
	<div class="fixed-bottom text-center bg-light py-3">
		Universidade de Coimbra - 2019
	</div>
</body>
</html>