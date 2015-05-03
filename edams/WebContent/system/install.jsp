<%@page import="jm.com.JmProperties"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String propertyFile = "edams.properties";
JmProperties jmProp = new JmProperties();
if(!jmProp.setResource(propertyFile) || jmProp.get("sysPassswd") == null){
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>에담스 설치 화면</title>
<!-- 공통 라이브러리 시작 -->
<%@include file="/common/header.jsp"%>
<!-- 공통 라이브러리 끝 -->
<link rel="stylesheet" href="/resource/css/signin.css">
</head>
<body class="body-gray">
<div class="container form-signin">
<form role="form" name="signinFrm" method="POST" action="/Install">

<h3>EDAMS 시스템 설정</h3>
<div class="panel panel-default">
	<div class="panel-heading">
		<h3 class="panel-title">DB 정보</h3>
	</div>
	<div class="panel-body">
	
<div class="row center-block">
	<h4>
		<label for="dbType"><span class="text-red">*</span> DB 종류 <small>Mysql, MariaDB 의 사용이 가능합니다.</small></label>
	</h4>
	<div class="col-sm-3 form-signin-col">
		<select class="form-control" id="dbType" name="dbType">
			<option value="mysql">MySQL</option>
			<option value="maria">MariaDB</option>
		</select>
	</div>
</div>

<div class="row center-block">
	<h4>
		<label for="dbUrl"><span class="text-red">*</span> DB 주소 <small>웹 서버와 동일한 시스템에 설치된 경우 localhost를 입력하십시오.</small></label>
	</h4>
	<div class="col-sm-4 form-signin-col">
		<input type="text" class="form-control" id="dbUrl" name="dbUrl" placeholder="localhost" required="required" maxlength="30" />
	</div>
</div>

<div class="row center-block">
	<h4>
		<label for="dbDb"><span class="text-red">*</span> DB명 </label>
	</h4>
	<div class="col-sm-4 form-signin-col">
		<input type="text" class="form-control" id="dbDb" name="dbDb" placeholder="edams" required="required" maxlength="30" />
	</div>
</div>

<div class="row center-block">
	
	<div class="col-sm-4 form-signin-col">
		<h4>
			<label for="dbUser"><span class="text-red">*</span> DB 사용자명 </label>
		</h4>
		<input type="text" class="form-control" id="dbUser" name="dbUser" placeholder="edams" required="required" maxlength="30" />
	</div>
	<div class="col-sm-2 form-signin-col"></div>
	<div class="col-sm-4 form-signin-col">
		<h4>
			<label for="dbPassswd"><span class="text-red">*</span> DB 암호 </label>
		</h4>
		<input type="password" class="form-control" id="dbPassswd" name="dbPassswd" required="required" maxlength="30" />
	</div>
</div>
	
    </div>
</div>

<%--
<!-- 엘라스틱서치 설정 시작 -->
<div class="panel panel-default">
	<div class="panel-heading">
		<h3 class="panel-title">엘라스틱서치 설정</h3>
	</div>
	<div class="panel-body">
	
<div class="row center-block">
	<div class="col-sm-3 form-signin-col">
		<label>
			<input type='radio' name='esType' value='common' onchange='setEsField();' checked='checked'> 공통 설정 적용
		</label>
	</div>
	<div class="col-sm-3 form-signin-col">
		<label>
			<input type='radio' name='esType' value='each' onchange='setEsField();'> 프로젝트별 설정 적용
		</label>
	</div>
</div>

<div class='row center-block'>
	<h4>
		<label><small>EDAMS가 설치된 서버 기준입니다. 동일 서버이면 localhost 입력하세요.</small></label>
	</h4>
	<div class='col-sm-2'>
		<h5 class='text-right'>
			<label for='esUrl'><span class='text-red'>*</span> URL</label>
		</h5>
	</div>
	<div class='col-sm-3 form-signin-col'>
		<input type='text' class='form-control' id='esUrl' name='esUrl' placeholder='localhost' required='required' maxlength='100' />
	</div>
	<div class='col-sm-3'>
		<h5 class='text-right'>
			<label for='esPort'>Http Port</label>
		</h5>
	</div>
	<div class='col-sm-2 form-signin-col'>
		<input type='number' class='form-control' id='esPort' name='esPort' required='required' value='9200' disabled='disabled'/>
	</div>
	<div class='col-sm-2'>
		<h5>
			<label>
				<input id='esPortChk' name='esPortChk' value='esPortChk'  type='checkbox' onclick='esPortClear();' checked='checked'> 자동
			</label>
		</h5>
	</div>
</div>

<div class='row center-block '>
	<div class='col-sm-2'>
		<h5 class='text-right'>
			<label for='esCluster'><span class='text-red'>*</span> 클러스터</label>
		</h5>
	</div>
	<div class='col-sm-3 form-signin-col'>
		<input type='text' class='form-control' id='esCluster' name='esCluster' required='required' placeholder='elasticsearch' maxlength='50' />
	</div>
	<div class='col-sm-3'>
		<h5 class='text-right'>
			<label for='esDPort'>Data Port</label>
		</h5>
	</div>
	<div class='col-sm-2 form-signin-col'>
		<input type='number' class='form-control' id='esDPort' name='esDPort' value='9300' disabled='disabled'/>
	</div>
	<div class='col-sm-2'>
		<h5>
			<label>
				<input id='esDPortChk' name='esDPortChk' value='esDPortChk'  type='checkbox' onclick='esDPortClear();' checked='checked'> 자동
			</label>
		</h5>
	</div>
</div>

    </div>
</div>
<script>
function setEsField(){
	var radVal = $("input[name=esType]:checked").val();
	if(radVal === 'common'){
		$("#esUrl").prop('disabled',false);
		$("#esPort").prop('disabled',false);
		$("#esCluster").prop('disabled',false);
		$("#esDPort").prop('disabled',false);
		$("#esPortChk").prop('disabled',false);
		$("#esDPortChk").prop('disabled',false);
		esPortClear();
		esDPortClear();
	} else {
		$("#esUrl").prop('disabled',true);
		$("#esPort").prop('disabled',true);
		$("#esCluster").prop('disabled',true);
		$("#esDPort").prop('disabled',true);
		$("#esPortChk").prop('disabled',true);
		$("#esDPortChk").prop('disabled',true);
	}
}
function esPortClear(){
	if($('#esPortChk').is(':checked') == true){
		$('#esPort').prop('disabled',true);
	} else {
		$('#esPort').prop('disabled',false);
	}
}
function esDPortClear(){
	if($('#esDPortChk').is(':checked') == true){
		$('#esDPort').prop('disabled',true);
	} else {
		$('#esDPort').prop('disabled',false);
	}
}
</script>
<!-- 엘라스틱서치 설정 끝 -->
--%>
 
<div class="panel panel-default">
	<div class="panel-heading">
		<h3 class="panel-title">EDAMS 시스템 디렉토리</h3>
	</div>
	<div class="panel-body">
	
<div class="row center-block">
	<p>Edams의 설정파일 및 데이터 파일들이 저장될 디렉토리 경로를 입력하십시오.
	<br>Edams를 실행시키는 계정에게 해당 디렉토리의 읽기/쓰기 권한이 있어야 합니다.</p>
	<p class='text-danger'>시스템 디렉토리 경로는 최초 입력 후 변경 할 수 없습니다.</p>
	<div class="col-sm-4 form-signin-col">
		<input type="text" class="form-control" id="sysPath" name="sysPath" placeholder="/etc" required="required" maxlength="30" />
	</div>
	<div class="col-sm-5 form-signin-col">
		<h5>/EDAMS</h5>
	</div>
</div>

    </div>
</div>

<div class="panel panel-default">
	<div class="panel-body">
		<h4>
			<label for="sysPassswd"><span class="text-red">*</span> 시스템 설정 암호 <small>시스템 변경시 필요합니다. 반드시 기억하세요.</small></label>
		</h4>
		<div class="col-sm-4 form-signin-col">
			<input type="password" class="form-control" id="sysPassswd" name="sysPassswd" required="required" maxlength="30" />
		</div>
		<div class="col-sm-5 form-signin-col"></div>
		<div class="col-sm-3 form-signin-col">
			<button class="btn btn-success btn-block" type="submit" data-toggle='modal' data-target='#saveInitMod'>저장</button>
		</div>
	</div>
</div>

<div class='modal' id='saveInitMod' tabindex='-1' role='dialog' aria-labelledby='myModalLabel' aria-hidden='true'>
<div class='modal-dialog'>
	<div class='modal-content'>
	
		<div class='modal-body form-signin'>
			<h3>EDAMS 설정을 저장하는 중입니다...</h3>
			<h4 class='text-red'>네트워크 속도에 따라 1~10분 가량 걸릴 수 있습니다.<br>이 페이지를 닫거나 새로고침 하지 마십시오.</h4>
		</div>
	
	</div>
</div>
</div>

<input type="hidden" name="cmd" value="saveInit">
</form>
</div>
</body>
</html>
<% } %>