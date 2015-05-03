<%@page import="jm.com.Encrypt"%>
<%@page import="jm.com.JmProperties"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String propertyFile = "edams.properties";
JmProperties jmProp = new JmProperties();
String passwd = request.getParameter("sysPassswd");
passwd = Encrypt.getSha256(passwd);
jmProp.setResource(propertyFile);
if(passwd.equals(jmProp.get("sysPassswd"))){
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
			<option value="mysql" <%if("mysql".equals(jmProp.get("dbType"))){ out.print("selected='selected'");}%> >MySQL</option>
			<option value="maria" <%if("maria".equals(jmProp.get("dbType"))){ out.print("selected='selected'");}%> >MariaDB</option>
		</select>
	</div>
</div>

<div class="row center-block">
	<h4>
		<label for="dbUrl"><span class="text-red">*</span> DB 주소 <small>웹 서버와 동일한 시스템에 설치된 경우 localhost를 입력하십시오.</small></label>
	</h4>
	<div class="col-sm-4 form-signin-col">
		<input type="text" class="form-control" id="dbUrl" name="dbUrl" required="required" maxlength="30" value='<%=jmProp.get("dbUrl")%>'/>
	</div>
</div>

<div class="row center-block">
	<h4>
		<label for="dbDb"><span class="text-red">*</span> DB명 </label>
	</h4>
	<div class="col-sm-4 form-signin-col">
		<input type="text" class="form-control" id="dbDb" name="dbDb" required="required" maxlength="30" value='<%=jmProp.get("dbDb")%>'/>
	</div>
</div>

<div class="row center-block">
	
	<div class="col-sm-4 form-signin-col">
		<h4>
			<label for="dbUser"><span class="text-red">*</span> DB 사용자명 </label>
		</h4>
		<input type="text" class="form-control" id="dbUser" name="dbUser" required="required" maxlength="30" value='<%=jmProp.get("dbUser")%>'/>
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
			<input type='radio' name='esType' value='common' onchange='setEsField();' <%if("common".equals(jmProp.get("esType"))){ out.print("checked='checked'");}%> > 공통 설정 적용
		</label>
	</div>
	<div class="col-sm-3 form-signin-col">
		<label>
			<input type='radio' name='esType' value='each' onchange='setEsField();' <%if("each".equals(jmProp.get("esType"))){ out.print("checked='checked'");}%> > 프로젝트별 설정 적용
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
		<input type='text' class='form-control' id='esUrl' name='esUrl' value='<%=jmProp.get("esUrl")%>' required='required' maxlength='100' />
	</div>
	<div class='col-sm-3'>
		<h5 class='text-right'>
			<label for='esPort'>Http Port</label>
		</h5>
	</div>
	<div class='col-sm-2 form-signin-col'>
		<input type='number' class='form-control' id='esPort' name='esPort' required='required' value='<%=jmProp.get("esPort")%>' disabled='disabled'/>
	</div>
	<div class='col-sm-2'>
		<h5>
			<label>
				<input id='esPortChk' name='esPortChk' value='esPortChk' type='checkbox' onclick='esPortClear();' <%if(!"".equals(jmProp.get("esPortChk"))){ out.print("checked='checked'");}%>> 자동
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
		<input type='text' class='form-control' id='esCluster' name='esCluster' required='required' value='<%=jmProp.get("esCluster")%>'  maxlength='50' />
	</div>
	<div class='col-sm-3'>
		<h5 class='text-right'>
			<label for='esDPort'>Data Port</label>
		</h5>
	</div>
	<div class='col-sm-2 form-signin-col'>
		<input type='number' class='form-control' id='esDPort' name='esDPort' value='<%=jmProp.get("esDPort")%>' disabled='disabled'/>
	</div>
	<div class='col-sm-2'>
		<h5>
			<label>
				<input id='esDPortChk' name='esDPortChk' value='esDPortChk' type='checkbox' onclick='esDPortClear();' <%if(!"".equals(jmProp.get("esDPortChk"))){ out.print("checked='checked'");}%>> 자동
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

esPortClear();
esDPortClear();
</script>
<!-- 엘라스틱서치 설정 끝 -->
--%>
 
<div class="panel panel-default">
	<div class="panel-heading">
		<h3 class="panel-title">EDAMS 시스템 디렉토리</h3>
	</div>
	<div class="panel-body">
	
<div class="row center-block">
	<p class='text-danger'>시스템 디렉토리 경로는 변경 할 수 없습니다.</p>
	<div class="col-sm-4 form-signin-col">
		<input type="text" class="form-control" id="sysPath"  value='<%=jmProp.get("sysPath").replaceFirst("/EDAMS", "")%>' disabled='disabled'/>
		<input type="hidden" name="sysPath" value='<%=jmProp.get("sysPath").replaceFirst("/EDAMS", "")%>'/>
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
			<button class="btn btn-success btn-block" type="submit">저장</button>
		</div>
	</div>
</div>

<input type="hidden" name="cmd" value="modifySysSettings">
</form>
</div>
</body>
</html>
<% } %>