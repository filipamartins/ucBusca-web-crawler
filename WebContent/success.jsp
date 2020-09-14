<%@ taglib prefix="s" uri="/struts-tags"%>
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
		<br><h3>Thank you for registering in ucBusca!</h3>
		<p>Your registration information: <s:property value="userBean" /></p>
		<br>
		<p><a href="<s:url action='index' />" >Return to home page.</a></p>
		<p><a href="login.jsp">Login to ucBusca.</a></p>
	</div>
	<div class="fixed-bottom text-center bg-light py-3">
		Universidade de Coimbra - 2019
	</div>
 </body>
</html>