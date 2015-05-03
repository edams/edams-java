<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>파일 - EDAMS</title>
<!-- Generic page styles -->

<!-- 공통 라이브러리 시작 -->
<%@include file="/common/header.jsp"%>
<!-- 공통 라이브러리 끝 -->

<!-- blueimp Gallery styles -->
<link rel="stylesheet" href="/resource/fileuploader/blueimp/blueimp-gallery.min.css">
<!-- CSS to style the file input field as button and adjust the Bootstrap progress bars -->
<link rel="stylesheet" href="/resource/fileuploader/css/jquery.fileupload.css">
<link rel="stylesheet" href="/resource/fileuploader/css/jquery.fileupload-ui.css">
<!-- CSS adjustments for browsers with JavaScript disabled -->
<noscript><link rel="stylesheet" href="/resource/fileuploader/css/jquery.fileupload-noscript.css"></noscript>
<noscript><link rel="stylesheet" href="/resource/fileuploader/css/jquery.fileupload-ui-noscript.css"></noscript>

<script>
var mapInfoObj;	//매핑정보 Object;
var mapPutObj;	//매핑 입력을 위한 Object;
var mapListObj;	//매핑 목록 Object;
</script>

</head>
<body>
<!-- 상단 네비게이션 메뉴 시작 -->	
<%@include file="/common/navbar.jsp"%>
<!-- 상단 네비게이션 메뉴 끝 -->

<div class="container">

	<div class="col-md-5">
		<div class="row">
			<%@include file="/view/files/filesFileUpload.jsp"%>
		</div>
		<div class="row">
			<%@include file="/view/files/filesMapList.jsp"%>
		</div>
	</div>
	
	<div class="col-md-7">
		<%@include file="/view/files/filesMapInfo.jsp"%>
	</div>
</div>

<!-- 하단 푸터 시작 -->
<%@include file="/common/footer.jsp"%>
<!-- 하단 푸터 끝 -->
</body>
</html>