<%@page import="edams.comm.CommUserObj"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	CommUserObj userObjNav = (CommUserObj) session.getAttribute("CommUserObj");

String toUrl = request.getRequestURI();
if (toUrl == null || toUrl.equals("")){ toUrl = "/"; }
String pageName = "";
if (toUrl.indexOf("Admin") > 0){
	pageName = "Admin";
} else if (toUrl.indexOf("Dashboard") > 0){
	pageName = "Dashboard";
} else if (toUrl.indexOf("Report") > 0){
	pageName = "Report";
} else if (toUrl.indexOf("Files") > 0){
	pageName = "Files";
} else if (toUrl.indexOf("Elasticsearch") > 0){
	pageName = "Elasticsearch";
}
%>
<script>
function logout() {
	if(confirm("로그아웃 하시겠습니까?")){
		var frm = document.getElementById("navFrm");
		frm.cmd.value="logout";
		frm.action="/AcConfirm";
		frm.submit();
	}
}
</script>
<nav class="navbar navbar-default" role="navigation">
	<div class="navbar-header">
		<button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-ex1-collapse">
			<span class="glyphicon glyphicon-list"></span>
		</button>
		<a class="navbar-brand">
			EDAMS
		</a>
	</div>
	
	<div class="collapse navbar-collapse navbar-ex1-collapse">
		<ul class="nav navbar-nav">
			<li id="navLiDashboard" <%if("Dashboard".equals(pageName)){ out.print("class=\"active\"");} %>><a href="/Dashboard"><i class="glyphicon glyphicon-dashboard"></i> 대시보드</a></li>
			<li id="navLiFiles" <%if("Files".equals(pageName)){ out.print("class=\"active\"");} %>><a href="/Files"><i class="glyphicon glyphicon-floppy-disk"></i> 파일</a></li>
<% if("ADMIN".equals(userObjNav.getUsrType())) { %>
			<li id="navLiES" <%if("Elasticsearch".equals(pageName)){ out.print("class=\"active\"");} %>><a href="/Elasticsearch"><i class="glyphicon glyphicon-hdd"></i> 엘라스틱서치</a></li>
			<li id="navLiAdmin" <%if("Admin".equals(pageName)){ out.print("class=\"active\"");} %>><a href="/Admin"><i class="glyphicon glyphicon-briefcase"></i> 프로젝트</a></li>
<% } %>
		</ul>
		
		<ul class="nav navbar-nav navbar-right">
			<li class="dropdown">
				<a href="#" class="dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
					[<%=userObjNav.getGrpName()%>] <%=userObjNav.getNicname()%> 님 
					<b class="caret"></b>
				</a>
				<ul class="dropdown-menu">
					<li><a href="/Modify">사용자정보 변경</a></li>
					<li class="divider"></li>
					<li><a href="javascript:logout();">로그아웃</a></li>
				</ul>
			</li>
		</ul>			
				
	</div>
</nav>