<%@page import="edams.dao.PrjDao,jm.net.DataEntity"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String uri = request.getRequestURI();
uri = uri.replaceAll("Dashboard", "");
uri = uri.replaceAll("/", "");
CommUserObj userObj = (CommUserObj) session.getAttribute("CommUserObj");
if(!"".equals(uri)){
	session.setAttribute("ProjectId", uri);
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
</head>
<body>
<!-- 상단 네비게이션 메뉴 시작 -->	
<%@include file="/common/navbar.jsp"%>
<!-- 상단 네비게이션 메뉴 끝 -->

<% if("".equals(uri)){ %>
<div class="container">
	<div class="jumbotron">
		<div class="container">
			<h1>조회 가능한 프로젝트가 없습니다.</h1>
			<p>그룹 관리자가 프로젝트를 생성하고 사용 권한을 부여해야 합니다.</p>
			<p>그룹 관리자에게 문의하세요.</p>
		</div>
	</div>
</div>
<% } else { 
	PrjDao pjDao = PrjDao.getInstance();
	DataEntity[] pjDatas = pjDao.getPrjUser(userObj.getGrpId(), userObj.getEmail(),null);
%>
<ul class="nav nav-tabs">
<% for(DataEntity pjData : pjDatas){ 
	if(uri.equals(pjData.get("prj_id")+"")){
%>
	<li class="dash-nav-li active"><a><%=pjData.get("prj_name")+""%></a></li>
<%		
	} else {
%>
	<li class="dash-nav-li"><a href="/Dashboard/<%=pjData.get("prj_id")+""%>"><%=pjData.get("prj_name")+""%></a></li>
<%
	}
} %>
</ul>

<script>
function resizeTopIframe() {
	var iframe_content = $("#kibanaFrame").contents().find('body');
	var dynheight = iframe_content.height();
    document.getElementById("kibanaFrame").height = parseInt(dynheight) + 10;
}
//주기적으로 iframe 리사이징.
setInterval('resizeTopIframe()', 1000);
</script>
<!-- 키바나 iframe 시작 -->
<iframe id="kibanaFrame" src="/kibana/#/dashboard/elasticsearch/<%=uri%>-<%=userObj.getGrpId()%>" width="100%" scrolling="no" ></iframe>
<!-- 키바나 iframe 끝 -->

<% } %>
<!-- 하단 푸터 시작 -->
<%@include file="/common/footer.jsp"%>
<!-- 하단 푸터 끝 -->
</body>
</html>