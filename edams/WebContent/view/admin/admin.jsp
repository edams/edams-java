<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%

String adminTab = request.getParameter("adminTab");
if(adminTab == null || "".equals(adminTab)){
	adminTab = "project";
}

%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>EDAMS</title>
<!-- 공통 라이브러리 시작 -->
<%@include file="/common/header.jsp"%>
<!-- 공통 라이브러리 끝 -->
<script>
</script>
</head>
<body>
<!-- 상단 네비게이션 메뉴 시작 -->	
<%@include file="/common/navbar.jsp"%>
<!-- 상단 네비게이션 메뉴 끝 -->
<div class="container">

<div class="col-md-2">
	<br>
	<div class="list-group">
		<a href="/Admin/Project" class="list-group-item <%if("project".equals(adminTab)){out.print("active");}%>">프로젝트 관리</a>
		<a href="/Admin/User" class="list-group-item <%if("user".equals(adminTab)){out.print("active");}%> ">사용자 관리</a>
		<a href="/Admin/Group" class="list-group-item <%if("group".equals(adminTab)){out.print("active");}%> ">그룹 관리</a>
	</div>
</div>

<div class="col-md-10">
<%if("project".equals(adminTab)){ %>
<!-- 프로젝트 관리 시작 -->
<%@include file="/view/admin/adminProject.jsp"%>
<!-- 프로젝트 관리 끝 -->
<% } else if("user".equals(adminTab)){ %>
<!-- 사용자 관리 시작 -->
<%@include file="/view/admin/adminUser.jsp"%>
<!-- 사용자 관리 끝 -->
<% } else if("group".equals(adminTab)){ %>
<!-- 그룹 관리 시작 -->
<%@include file="/view/admin/adminGroup.jsp"%>
<!-- 그룹 관리 끝 -->
<% } %>
</div>
	
</div>
<!-- 하단 푸터 시작 -->
<%@include file="/common/footer.jsp"%>
<!-- 하단 푸터 끝 -->
</body>
</html>