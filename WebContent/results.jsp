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
	<h6>Number of results: <strong><s:property  value="searchBean.message.totalResults" /></strong></h6><br>
	<c:forEach items="${searchBean.results}" var="value">
		<div class="card">
		<div class="card-body">
		    <h5 class="card-title"><c:out value="${value.get('title')}" /></h5>
		    <h6 class="card-subtitle mb-2 text-muted"><c:out value="${value.get('url')}" /> </h6>
		    <p class="card-text"><c:out value="${value.get('citation')}" /></p>
			<p class="card-text">connections: <c:out value="${value.get('connections')}" /></p>
			<!-- comment the next 2 lines if you are not using Yandex API -->   
		   	<p class="card-text text-right">original language: <strong><c:out value="${value.get('original_lang')}" /></strong></p>
			<p class="card-text text-tiny font-weight-lighter text-right">powered by <a href="https://yandex.com/">yandex</a></p>
			<!-- -->
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