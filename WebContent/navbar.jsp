<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>


<nav class="navbar navbar-expand-lg navbar-light bg-light">
	<a class="navbar-brand" href="index.jsp">ucBUSCA</a>
	<div class="ml-auto">
		<s:if test='%{#session.loggedin == true}' >
       		<span data-username="<s:property value="#session.userBean.username" />" class="mr-4"> Signed in as  <strong><s:property value="#session.userBean.username" /></strong></span>
       		<s:if test='%{#session.userBean.admin == true}' >
       			<a href="<s:url action='list' />">
				<button class="btn btn-outline-info my-2 my-sm-0" type="button">Admin page</button>
				</a> 
			</s:if>
			<s:else>
				<a href="<s:url action='list' />">
				<button class="btn btn-outline-info my-2 my-sm-0" type="button">User page</button>
				</a> 
			</s:else>
			<a href="<s:url action='logout' />">
			<button class="btn btn-outline-info my-2 my-sm-0" type="button">Logout</button>
			</a>
		</s:if>
		<s:else>
			<a href="register.jsp">
				<button class="btn btn-outline-info my-2 my-sm-0" type="button">Sign-up</button>
			</a> 
			<a href="login.jsp">
				<button class="btn btn-info my-2 my-sm-0" type="button">Login</button>
			</a>
		</s:else>
	</div>
</nav>


	
 <div class="toast" id="myToast" data-autohide="false" style="position: absolute; top: 60px; right: 15px;">
     <div class="toast-header">
         <strong class="mr-auto"><i class="fa fa-grav"></i> New notification:</strong>
         <small></small>
         <button type="button" class="ml-2 mb-1 close" data-dismiss="toast">
             <span aria-hidden="true">&times;</span>
         </button>
     </div>
     <div class="toast-body">
         <div>You have been promoted to ADMIN!!</div>
     </div>
 </div>
	
	