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
	<br>
	<div class="container">
	<h5>Pages linked to <strong><s:property value="urlConnections" /></strong>:</h5><br>
	<c:forEach items="${urlBean.message.list}" var="value">
		<div class="card">
		<div class="card-body">
		    <h5 class="card-title"><c:out value="${value}" /></h5>
  		</div>
  		</div>	
		<br>
	</c:forEach>
	</div>
	<br><br>
	<div class="fixed-bottom text-center bg-light py-3">
		Universidade de Coimbra - 2019
	</div>
</body>
</html>