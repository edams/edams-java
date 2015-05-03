<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html">
<html>
<head>
<title>Edams 로그인</title>
<!-- 공통 라이브러리 시작 -->
<%@include file="/common/header.jsp"%>
<!-- 공통 라이브러리 끝 -->
<link rel="stylesheet" href="/resource/css/signin.css">
<script>
function login(){
	var frm = document.loginFrm;
	frm.method="POST";
	frm.action="/Install";
	frm.submit();
}
</script>
</head>
<body class="body-login body-gray">
<div class="container">
	<form class="form-login" role="form" name="loginFrm" action="javascript:login();">
		<h2 class="form-login-heading">시스템 로그인 화면입니다.</h2>
		<input type="password" name="sysPassswd" class="form-control" placeholder="비밀번호" required="required">
		<button class="btn btn-lg btn-primary btn-block" type="submit">로그인</button>
		<input type="hidden" name="cmd" value="syslogin" />
		<input type="hidden" name="toUrl" value="/View" />
	</form>
</div>

<!-- 하단 푸터 시작 -->
<%@include file="/common/footer.jsp"%>
<!-- 하단 푸터 끝 -->
</body>
</html>