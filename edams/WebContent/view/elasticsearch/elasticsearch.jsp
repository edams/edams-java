<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>엘라스틱서치 - EDAMS</title>
<!-- 공통 라이브러리 시작 -->
<%@include file="/common/header.jsp"%>
<!-- 공통 라이브러리 끝 -->
<script>
var esListObj;	//es 목록 객체.
var esPutObj;	//es 전달 데이터 객체.
</script>
</head>
<body>
<!-- 상단 네비게이션 메뉴 시작 -->	
<%@include file="/common/navbar.jsp"%>
<!-- 상단 네비게이션 메뉴 끝 -->

<div class="container">

	<div class="col-md-5">
		<div class="row">
			<%@include file="/view/elasticsearch/elasticsearchList.jsp"%>
		</div>
	</div>

	<div class="col-md-7">
		<%@include file="/view/elasticsearch/elasticsearchInfo.jsp"%>
	</div>

</div>

<!-- 하단 푸터 시작 -->
<%@include file="/common/footer.jsp"%>
<!-- 하단 푸터 끝 -->
</body>
</html>