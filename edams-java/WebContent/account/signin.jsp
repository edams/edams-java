<%@page import="jm.net.DataEntity,edams.dao.AcDao"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
AcDao dao = AcDao.getInstance();
DataEntity[] grpDatas = dao.getGrp();
%>
<!DOCTYPE html>
<html>
<head>
<title>Edams 회원가입</title>
<!-- 공통 라이브러리 시작 -->
<%@include file="/common/header.jsp"%>
<!-- 공통 라이브러리 끝 -->
<link rel="stylesheet" href="/resource/css/signin.css">
<script src="/resource/js/signin.js"></script>
<script>
var grpInfo;
/**
 * 그룹 정보 불러오기
 */
$(document).ready(function(){
	$.ajax({
		type : "GET",
		url : "/Ajax/Account?cmd=getGrpInfo",
		dataType:"json",
		success : function(dataGrpInfo) {
			grpInfo = dataGrpInfo;
			for(var i=0; i < grpInfo.length; i++){
				if(i==0){
					$("#grpId").append('<option value="'+grpInfo[i].grp_id+'" title="'+grpInfo[i].grp_text+'" selected="selected">'+grpInfo[i].grp_name+'</option>');
					$("#grpTitle").html(grpInfo[i].grp_text);
					aGrpTitle();
				} else {
					$("#grpId").append('<option value="'+grpInfo[i].grp_id+'" title="'+grpInfo[i].grp_text+'">'+grpInfo[i].grp_name+'</option>');
				}
			}
		}, error:function(e){  
			console.log(e.responseText);  
		}
	});	
});

/**
 * 그룹명 선택시 우측에 툴팁 표시.
 */
function aGrpTitle(){
	var gTitle = $("#grpId option:selected").attr('title');
	$("#grpTitle").html(gTitle);
	var gId = $("#grpId option:selected").attr('value');
	for(var i=0; i < grpInfo.length; i++){
		if(grpInfo[i].grp_id === gId){
			if(grpInfo[i].user_cnt === 0){
				console.log(grpInfo[i].grp_name);
				$("#usrTypeInfo").html("회원수가 0입니다. 최초는 관리자로만 가입이 가능합니다.");
				$(".usrTypeRdo:eq(1)").prop("checked",true);
				$(".usrTypeRdo:eq(0)").prop("checked",false);
				$(".usrTypeRdo:eq(0)").prop("disabled",true);
				$("#usrTypeLbl").addClass("text-muted");
			} else {
				$("#usrTypeInfo").html("");
				$(".usrTypeRdo:eq(0)").prop("checked",true);
				$(".usrTypeRdo:eq(1)").prop("checked",false);
				$(".usrTypeRdo:eq(0)").prop("disabled",false);
				$("#usrTypeLbl").removeClass("text-muted");
			}
		}
	}
}
</script>
</head>
<body class="body-gray">
<div class="container">
<br>
<div class="panel panel-default form-signin">
	<div class="panel-heading">
    	<h3>
    		회원 가입 <small><span class="text-red">*</span> 표시는 필수 항목입니다.</small>
    	</h3>
  	</div>
  <div class="panel-body">

<!-- 회원 가입 폼 시작 -->
<form role="form" name="signinFrm" action="javascript:signin();">
	<div class="row center-block">
		<h4>
			<label for="aGrp"><span class="text-red">*</span> 그룹 <small>소속된 그룹을 선택하거나 새로 생성하십시오.</small></label>
			<button type="button" class="btn btn-primary btn-sm" data-toggle="modal" data-target="#newGrp">신규그룹 등록</button>
		</h4>
		<div class="col-sm-4 form-signin-col">
			<select class="form-control" id="grpId" name="grpId" onchange="aGrpTitle();">
			</select>
		</div>
		<div class="col-sm-8">
			<h5 id="grpTitle" class="text-blue"></h5>
		</div>
	</div>
	
	<div class="row center-block">
		<h4>
			<label><span class="text-red">*</span> 계정 유형 <small>관리자 계정으로 가입하는 경우 시스템 운영자의 승인이 필요합니다.</small></label>
		</h4>
		<div class="col-sm-2 form-signin-col">
			<div class="radio">
				<label id="usrTypeLbl">
					<input class="usrTypeRdo" type="radio" name="usrType" value="USER"> 일반 사용자
				</label>
			</div>
		</div>
		<div class="col-sm-2 form-signin-col">		
			<div class="radio">
				<label>
					<input class="usrTypeRdo" type="radio" name="usrType" value="ADMIN"> 관리자
				</label>
			</div>
		</div>
		<div class="col-sm-8">
			<h5 id="usrTypeInfo" class="text-blue"></h5>
		</div>
	</div>
	
	<div class="row center-block">
		<h4>
			<label for="email1"><span class="text-red">*</span> 이메일 <small>아이디로 사용됩니다.</small></label>
		</h4>
		<div class="col-sm-5 form-signin-col">
			<div class="col-sm-5 form-signin-col">
				<input type="text" class="form-control" id="email1" name="email1" required="required" maxlength="60" onkeyup="putEmail();" />
			</div>
			<div class="col-sm-1 form-signin-col text-center"><h5>@</h5></div>
			<div class="col-sm-6 form-signin-col">
				<input type="text" class="form-control" id="email2" name="email2" required="required" maxlength="40" onchange="putEmail();" />
			</div>
		</div>
		<div class="col-sm-3 form-signin-col">
		<select class="form-control" id="email3" name="email3" onchange="setMail(this);">
			<option value="write" selected="selected">직접입력</option>
			<option value="gmail.com">gmail.com</option>
			<option value="naver.com">naver.com</option>
			<option value="daum.net">daum.net</option>
			<option value="hotmail.com">hotmail.com</option>
			<option value="nate.com">nate.com</option>
			<option value="empas.com">empas.com</option>
			<option value="dreamwiz.com">dreamwiz.com</option>
		</select>
		</div>
		<div class="col-sm-2">
			<button type="button" class="btn btn-primary btn-block" id="chIdBtn" onclick="checkMail();">중복확인</button>
		</div>
		<input type="hidden" id="email" name="email"/>
	</div>
	
	<div class="row center-block">
		<h4>
			<label for="id"><span class="text-red">*</span> 비밀번호</label>
		</h4>
		<div class="col-sm-4 form-signin-col">
			<input type="password" class="form-control" id="passwd" name="passwd" placeholder="비밀번호" required="required" maxlength="30" onkeyup="putPasswd();" />
		</div>
	</div>
	
	<div class="row center-block">
		<h4>
			<label for="passwd2"><span class="text-red">*</span> 비밀번호 확인</label>
			<small><span id="chkPw"></span></small>
		</h4>
		<div class="col-sm-4 form-signin-col">
		<input type="password" class="form-control" id="passwd2" name="passwd2" placeholder="비밀번호 확인" required="required" maxlength="30" onkeyup="chkPw();" />
		</div>
	</div>
	
	<div class="row center-block">
		<h4>
			<label for="name"><span class="text-red">*</span> 이름 (본명)</label>
		</h4>
		<div class="col-sm-4 form-signin-col">
			<input type="text" class="form-control" id="name" name="name" placeholder="이름" required="required" maxlength="6" />
		</div>
	</div>
	
	<div class="row center-block">
		<h4>
			<label for="nicname"><span class="text-red">*</span> 닉네임 <small>10자 이내</small></label>
		</h4>
		<div class="col-sm-4 form-signin-col">
			<input type="text" class="form-control" id="nicname" name="nicname" placeholder="닉네임" required="required" maxlength="10"  onkeyup="putNicname();"/>
		</div>
		<div class="col-sm-2">
			<button type="button" class="btn btn-primary btn-block" id="chNickBtn" onclick="checkNick();">중복확인</button>
		</div>
	</div>
	
	<div class="row center-block">
		<h4>
			<label for="birthday"><span class="text-red">*</span> 생년월일</label>
		</h4>
		<div class="col-sm-2 form-signin-col">
			<div class="col-xs-10 form-signin-col">
				<select class="form-control" id="birthY" name="birthY" onchange="setBDate();"></select>
			</div>
			<div class="col-xs-2 form-signin-col"><h5>년</h5></div> 
		</div>
		<div class="col-sm-2 form-signin-col">
			<div class="col-xs-10 form-signin-col">
				<select class="form-control" id="birthM" name="birthM" onchange="setBDate();"></select>
			</div>
			<div class="col-xs-2 form-signin-col"><h5>월</h5></div> 
		</div> 
		<div class="col-sm-2 form-signin-col">
			<div class="col-xs-10 form-signin-col">
				<select class="form-control" id="birthD" name="birthD"></select>
			</div>
			<div class="col-xs-2 form-signin-col"><h5>일</h5></div> 
		</div>
		<script src="/resource/js/datetime.js"></script>
	</div>
	
	<div class="row center-block">
		<h4>
			<label for="birthday">연락처 <small>(-) 없이 숫자만 입력하세요.</small></label>
		</h4>
		<div class="col-sm-4 form-signin-col">
			<input type="number" class="form-control" id="phone" name="phone">
		</div>
	</div>
	<br>
	<div class="row center-block">
		<div class="col-sm-4 form-signin-col"></div>
		<div class="col-sm-4 form-signin-col">
			<div class="col-xs-6 form-signin-col">
				<button type="submit" class="btn btn-success btn-block">회원 가입</button>
			</div>
			<div class="col-xs-6 form-signin-col">
				<a type="button" class="btn btn-danger btn-block" href="javascript:history.go(-1);">취소</a>
			</div>
		</div>
		<div class="col-sm-4 form-signin-col"></div>
		<input type="hidden" name="cmd" value="signin" />
		<input type="hidden" name="toUrl" value="/View" />
	</div>
	
</form>
<!-- 회원 가입 폼 끝 -->

<!-- 그룹 등록 모달 폼 시작 -->
<form role="form" name="newGrpFrm" action="javascript:newGrp();">
<div class="modal fade" id="newGrp" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
<div class="modal-dialog">
<div class="modal-content">

<div class="modal-header">
	<button type="button" class="close" data-dismiss="modal">
		<span aria-hidden="true"></span><span class="sr-only">Close</span></button>
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	<h4 class="modal-title" id="myModalLabel">신규그룹 등록</h4>
</div>

<div class="modal-body">

	<div class="row center-block">
		<h4>
			<label for="grpId"><span class="text-red">*</span> 그룹 아이디 <small>20자 이내 영문 소문자</small></label>
		</h4>
		<div class="col-sm-3 form-signin-col">
			<input type="text" class="form-control" id="grpGrpId" name="grpId" placeholder="그룹 아이디" required="required" maxlength="20" onkeyup="putGrpId();"/>
		</div>
		<div class="col-sm-2">
			<button type="button" class="btn btn-primary btn-block" id="chIdBtn" onclick="checkGrp();">중복확인</button>
		</div>
	</div>
	
	<div class="row center-block">
		<h4>
			<label for="grpName"><span class="text-red">*</span> 그룹명 <small>50자 이내</small></label>
		</h4>
		<div class="col-sm-4 form-signin-col">
			<input type="text" class="form-control" id="grpName" name="grpName" placeholder="그룹명" required="required" maxlength="50" />
		</div>
	</div>
	
	<div class="row center-block">
		<h4>
			<label for="grpText"><span class="text-red">*</span> 설명 <small>100자 이내</small></label>
		</h4>
		<div class="col-sm-10 form-signin-col">
			<input type="text" class="form-control" id="grpText" name="grpText" placeholder="그룹 설명" required="required" maxlength="100" />
		</div>
	</div>

<%--
<% DataEntity[] grpDeptDatas = dao.getGrpDept(); %>
	<div class="row center-block">
		<h4>
			<label for="grpDept"><span class="text-red">*</span> 구분 <small>그룹이 속한 범위 선택</small></label>
		</h4>
		<div class="col-sm-4 form-signin-col">
			<select class="form-control" id="grpDept" name="grpDept" onchange="aGrpDeptTitle();">
<%
String tempGrpDeptTxt = "";
String grpDeptOptSel = "";
for(int i=0; i < grpDeptDatas.length; i++){
	if(i==0){
		tempGrpDeptTxt = grpDeptDatas[i].get("grp_dept_text")+"";
		grpDeptOptSel = "selected='selected'";
	} else { 
		grpDeptOptSel = "";
	}
%>				<option <%=grpDeptOptSel%> value="<%=grpDeptDatas[i].get("grp_dept_id")+""%>" title="<%=grpDeptDatas[i].get("grp_dept_text")+""%>"><%=grpDeptDatas[i].get("grp_dept_name")+""%></option>
<% } %>
			</select>
		</div>
		<div class="col-sm-8">
			<h5 id="grpDeptTitle" class="text-blue"><%=tempGrpDeptTxt%></h5>
		</div>
	</div>
--%>
 
</div>

<div class="modal-footer">
	<button type="submit" class="btn btn-success">입력 완료</button>
	<button type="button" class="btn btn-danger" data-dismiss="modal">취소</button>
</div>
	
</div>
</div>
</div>

<input type="hidden" name="cmd" value="insertAcGrp" />
<input type="hidden" name="toUrl" value="/Signin" />
</form>
<!-- 그룹 등록 모달 폼 끝 -->

</div>
</div>
</div>
<!-- 하단 푸터 시작 -->
<%@include file="/common/footer.jsp"%>
<!-- 하단 푸터 끝 -->
</body>
</html>