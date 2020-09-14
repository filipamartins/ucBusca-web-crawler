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
		
	<div class="container" style="margin-top:8%">
	<div class="col">
		<h1 class="display-4">ucBusca</h1>
		<s:form action="search" method="post">
			<div class="row pt-3">
			<div class="col-md-8">
				<s:textfield cssClass="form-control input-lg" name="searchBean.search" /> 
			</div>
			<div class="col-md-1">
				<s:submit cssClass="btn btn-outline-success my-2 my-sm-0" value="Search" />
			</div>
			</div>
		</s:form>
		<br><s:actionmessage />
	</div>
	</div>
	<div class="fixed-bottom text-center bg-light py-3">
		Universidade de Coimbra - 2019
	</div>
</body>
</html>