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
<div class="container">
	<div class="row">
	<div class="mx-auto">
	  	<div class="form-group">
	  	<br><h4>Register</h4><br>
		<s:form action="register" method="post">
			<s:text  name="Insert a username:" />
			<s:textfield cssClass="form-control" name="userBean.username" /><br>
			<s:text name="Insert a password:" />
			<s:textfield cssClass="form-control" name="userBean.password" /><br> 
			<s:fielderror cssClass="text-danger"/>
			<s:submit cssClass="btn btn-primary"/>
		</s:form>
		</div>
		<div>
		<p>Already registered?<a href="login.jsp"> <br>Click here</a> to login ucBusca.</p>
		</div>
	</div>
	</div>
</div>

<div class="fixed-bottom text-center bg-light py-3">
Universidade de Coimbra - 2019
</div>
</body>
</html>