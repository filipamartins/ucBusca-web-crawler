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
	
	<div class="container">
	<s:if test='%{#session.userBean.admin == true}' >
		<br><h2><strong><s:property value="#session.userBean.username" /></strong>'s Admin Page</h2><br>
	</s:if>	
	<s:else>
		<br><h2><strong><s:property value="#session.userBean.username" /></strong>'s Page</h2><br>
	</s:else>
	</div>
	<div class="container">
	<div class="row">
	  	<div class="form-group col-md-3"><br>
	  	<s:if test='%{#session.userBean.admin == true}' >
	  		<s:form action="promoteuser" method="post">
			<h6><s:text  name="Promote user to Admin:" /></h6>
			<s:textfield cssClass="form-control" name="userToPromote" placeholder="username" />
			<s:submit cssClass="btn btn-primary mt-2 ml-1"/>
			</s:form>
			<br>
			<br>
			<s:form action="indexurl" method="post">
			<h6><s:text  name="Index new URL:" /></h6>
			<s:textfield cssClass="form-control" name="urlToIndex" placeholder="url" />
			<s:submit cssClass="btn btn-primary mt-2 ml-1"/>
			</s:form>
			<br><br>
		</s:if>	
			<s:form action="connections" method="post">
			<h6><s:text  name="See list of pages linked to URL:" /></h6>
			<s:textfield cssClass="form-control" name="urlConnections" placeholder="url"/>
			<s:submit cssClass="btn btn-primary mt-2 ml-1"/>
		</s:form>
		<br><s:fielderror cssClass="text-danger"/>
		<s:actionmessage />
		</div>
		<div class="col-md-1">
		</div>
		<div class="col-md-3">
			<br>
			<h6>Search history:</h6>
			<table class="table">
				<thead>
				  	<tr>
				    <th scope="col">#</th>
				    <th scope="col">Search</th>
				    </tr>
			  	</thead>
				<tbody>
					<s:if test='%{searchBean.message.sucess.equals("true")}' >
			    		<c:set var="count" value="1" scope="page" />
			  			<c:forEach items="${searchBean.message.list}" var="value">
				    		<tr>
				    		<th scope="row"><c:out value="${count}" /></th>
				    		<td><c:out value="${value}" /></td>
				    		</tr>
				    		<c:set var="count" value="${count + 1}" scope="page"/>
			    		</c:forEach>
			  		</s:if>
			  		<s:else>
			    		<tr>
			    		<th scope="row">1</th>
			    		<td>Your research history is empty.</td>
			    		</tr>
			  		</s:else>
				</tbody>
			</table>
		</div>
		<s:if test='%{#session.userBean.admin == true}' >
		<div class="col-md-1">
		</div>
		<div class="col-md-3">
			<br>
			<h6>Users registered in ucBusca:</h6>
			<table class="table">
			  <thead>
			  <tr>
			  <th scope="col">#</th>
			  <th scope="col">User</th>
			  </tr>
			  </thead>
			  <tbody>
			  	<s:if test='%{userBean.message.sucess.equals("true")}' >
		    		<c:set var="count" value="1" scope="page" />
		  			<c:forEach items="${userBean.message.list}" var="value">
			    		<tr>
			    		<th scope="row"><c:out value="${count}" /></th>
			    		<td><c:out value="${value}" /></td>
			    		</tr>
			    		<c:set var="count" value="${count + 1}" scope="page"/>
		    		</c:forEach>
			  	</s:if>
			  	<s:else>
		    		<tr>
		    		<th scope="row">1</th>
		    		<td>No users to display.</td>
		    		</tr>
		  		</s:else>
			  </tbody>
			</table>
		</div>
		</s:if>
	</div>
	<p><a href="<s:url action='index' />" >Return to home page.</a></p>
	</div>
	<div class="fixed-bottom text-center bg-light py-3">
		Universidade de Coimbra - 2019
	</div>
</body>
</html>