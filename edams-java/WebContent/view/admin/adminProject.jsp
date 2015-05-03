<%@page import="edams.comm.CommProp"%>
<%@page import="jm.com.JmProperties"%>
<%@page import='edams.comm.CommUserObj,edams.dao.PrjDao,jm.net.DataEntity'%>
<%@ page language='java' contentType='text/html; charset=UTF-8' pageEncoding='UTF-8'%>
<% CommUserObj apUserObj = (CommUserObj) session.getAttribute("CommUserObj"); %>
<link rel='stylesheet' href='/resource/css/signin.css'>
<script>
var prjInfo;
var grpInfo;
var sysStatInfo;
$(document).ready(function(){
	//프로젝트 목록 조회.
	$.ajax({
		type : 'GET',
		url : '/Ajax/Project?cmd=prjInfo',
		dataType:'json',
		success : function(dataPrjInfo) {
			prjInfo = dataPrjInfo;
			setPrjTable();
			setSysStat();
		}, error:function(e){  
			console.log(e.responseText);
		}
	});
	
	//그룹 목록 조회 (공유 요청용)
	$.ajax({
		type : 'GET',
		url : '/Ajax/Account?cmd=getGrpInfo',
		dataType:'json',
		success : function(dataGrpInfo) {
			grpInfo = dataGrpInfo;
			setPrjShGrp();
		}, error:function(e){  
			console.log(e.responseText);
		}
	});
});

//프로젝트 목록 불러오기.
function setPrjTable(){
	var tblStr = '';
	if(prjInfo.length > 0){
		for(var tr=0; tr < prjInfo.length; tr++){
			var shStatus = '';
			var shStatResMsg = '';
			var reqCnt = 0;
			for(var g = 0; g < prjInfo[tr].pj_prj_grp.length; g++ ){
				if(prjInfo[tr].pj_prj_grp[g].use_grp_id === '<%=apUserObj.getGrpId()%>'){
					shStatus = prjInfo[tr].pj_prj_grp[g].status;
					shStatResMsg = prjInfo[tr].pj_prj_grp[g].res_msg;
				}
			}
			for(var g = 0; g < prjInfo[tr].pj_prj_grp_shared.length; g++ ){
				if(prjInfo[tr].pj_prj_grp_shared[g].use_grp_id !== '<%=apUserObj.getGrpId()%>' && prjInfo[tr].pj_prj_grp_shared[g].status === 'REQUEST'){
					reqCnt++;
				}
			}
			if(prjInfo[tr].grp_type === 'OWNER'){
				tblStr += "<tr>";
			} else {
				tblStr += "<tr class='warning'>";
			}
			
			if(prjInfo[tr].grp_type === 'OWNER'){
				tblStr += "<td class='text-center'>";
				tblStr += "<button type='button' class='btn btn-info btn-sm btn-block' onclick='modifPrj("+tr+")'>";
				tblStr += "<i class='glyphicon glyphicon-cog'></i> 설정";
				tblStr += "</button>";
				tblStr += "</td>";
			} else {
				tblStr += "<td class='text-center text-warning'>";
				if(prjInfo[tr].grp_type === 'VIEWER'){
					tblStr += "[조회]";
				} else if(prjInfo[tr].grp_type === 'USER'){
					tblStr += "[조회/입력]";
				}
				tblStr += "</td>";
			}
			tblStr += "<td class='text-center'>";
			tblStr += "<a title='"+prjInfo[tr].prj_text+"'>";
			tblStr += prjInfo[tr].prj_name;
			tblStr += "</a>";
			
			tblStr += "</td>";
			tblStr += "<td class=''>";
			tblStr += prjInfo[tr].prj_owner.grp_name;
			tblStr += " ("+prjInfo[tr].prj_owner.nicname+")";
			tblStr += "</td>";
			
			tblStr += "<td class='text-right'>";
			if(prjInfo[tr].grp_type === 'OWNER'){
				if(reqCnt > 0) {
					tblStr += "<button type='button' class='btn btn-default btn-sm btn-block jm-badge' data-badge='"+reqCnt+"' onclick='prjShareOpt("+tr+");'>";
				} else {
					tblStr += "<button type='button' class='btn btn-default btn-sm btn-block' onclick='prjShareOpt("+tr+");'>";
				}
				tblStr += "<i class='glyphicon glyphicon-refresh'></i> 공유 설정";
				tblStr += " ["+prjInfo[tr].pj_prj_grp_shared.length+"]";
				tblStr += " <span class='badge'>";
				tblStr += "</span>";
				tblStr += "</button>";
			} else {
				tblStr += "<button type='button' class='btn btn-danger btn-sm btn-block' onclick='prjShareRemove(\""+prjInfo[tr].own_grp_id+"\",\""+prjInfo[tr].prj_id+"\");'>";
				tblStr += "<i class='glyphicon glyphicon-remove'></i> 공유 제거";
				tblStr += "</button>";
			}
			
			tblStr += "</td>";
			
			tblStr += "<td class='text-right'>";
			if(shStatus === 'ACTIVE' || shStatus === 'APPROVED' || shStatus === 'PROVIDED'){
				tblStr += "<button type='button' class='btn btn-default btn-sm btn-block' onclick='prjUsrSet(\""+prjInfo[tr].own_grp_id+"\",\""+prjInfo[tr].prj_id+"\",\""+prjInfo[tr].grp_type+"\");'>";
				tblStr += "<i class='glyphicon glyphicon-user'></i> 사용자 설정";
				tblStr += " ["+prjInfo[tr].pj_prj_grp_user.length+"]";
				tblStr += "</button>";
			} else {
				tblStr += "<button type='button' class='btn btn-default btn-sm btn-block' disabled='disabled'>";
				tblStr += "<i class='glyphicon glyphicon-user'></i> 사용자 설정";
				tblStr += " ["+prjInfo[tr].pj_prj_grp_user.length+"]";
				tblStr += "</button>";
			}
			
			tblStr += "</td>";

			tblStr += "<td class='text-right'>";
			if(prjInfo[tr].grp_type === 'OWNER'){
				tblStr += "<button type='button' id='chSysBtn_"+prjInfo[tr].prj_id+"' class='btn btn-default btn-sm btn-block' onclick='sysState("+tr+");'>";
				tblStr += "</button> ";
			} else {
				if(shStatus === 'REQUEST'){
					tblStr += "<button type='button' class='btn btn-warning btn-sm btn-block' disabled='disabeld''>";
					tblStr += "<i class='glyphicon glyphicon-refresh'></i> 공유요청중";
					tblStr += "</button> ";
				} else if(shStatus === 'APPROVED' || shStatus === 'PROVIDED'){
					
					tblStr += "<button type='button' id='chSysBtn_"+prjInfo[tr].prj_id+"' class='btn btn-default btn-sm btn-block' onclick='sysState("+tr+");'>";
					tblStr += "</button> ";
					/*
					tblStr += "<button type='button' class='btn btn-success btn-sm btn-block'>";
					tblStr += "<i class='glyphicon glyphicon-ok'></i> 공유됨";
					tblStr += "</button> ";
					*/
				} else if(shStatus === 'DENIED'){
					tblStr += "<button type='button' class='btn btn-danger btn-sm btn-block' data-container='body' data-toggle='popover' data-placement='bottom' title='응답 메시지' data-content='"+shStatResMsg+"' data-original-title title >";
					tblStr += "<i class='glyphicon glyphicon-remove'></i> 반려됨";
					tblStr += "</button> ";
				}
			}
			tblStr += "</td>";
			
			tblStr += "</tr>";
		}
	} else {
		tblStr += "<tr>";
		tblStr += "<td class='text-center' colspan='7'>";
		tblStr += "<h4>사용중인 프로젝트가 없습니다.</h4>";
		tblStr += "</td>";
		tblStr += "</tr>";
	}
	
	$("#prjTbBody").append(tblStr);
	$("[data-toggle='popover']").popover();
}

//엘라스틱서치 클러스터, 로그스태시, 콘솔 값 가져오기.
function setSysStat(){
	sysStatInfo = new Array(prjInfo.length);
	for(var pj=0; pj < prjInfo.length; pj++){
		sysStatInfo[pj] = {cluster:0, logstash:0, index:0};
//		statCluster(pj);
		statLogstash(pj);
		statIndex(pj);
	}
}
function statCluster(prjNum){
	$.ajax({
		type : 'GET',
		url : '/Ajax/Elasticsearch?cmd=esStatus&prjId='+prjInfo[prjNum].prj_id,
		dataType:'json',
		success : function(clusterInfo) {
			sysStatInfo[prjNum].cluster = 1;
			setSysStatBtn();
		}, error:function(e){
			sysStatInfo[prjNum].cluster = 0;
			setSysStatBtn();
		}
	});
}

function statLogstash(prjNum){
	$.ajax({
		type : 'GET',
		url : '/Ajax/Elasticsearch?cmd=esLogstash&prjId='+prjInfo[prjNum].prj_id,
		dataType:'json',
		success : function(logstashInfo) {
//			console.log(logstashInfo);
			if(Object.keys(logstashInfo.nodes).length == 0){
				sysStatInfo[prjNum].logstash = 0;
			} else if(Object.keys(logstashInfo.nodes).length == 1){
				sysStatInfo[prjNum].logstash = 1;
			} else {
				sysStatInfo[prjNum].logstash = 1;
				$("#prjStatStatTxt").html('같은 클러스터에서 2개 이상의<br>로그스태시 프로세스가 실행중입니다.<br>시스템 관리자에게 문의하세요.');
			}
			sysStatInfo[prjNum].cluster = 1;
			setSysStatBtn();
		}, error:function(e){
			sysStatInfo[prjNum].logstash = 0;
			sysStatInfo[prjNum].cluster = 0;
			setSysStatBtn();
		}
	});
}
function statIndex(prjNum){
	$.ajax({
		type : 'GET',
		url : '/Ajax/Elasticsearch?cmd=esIndex&prjId='+prjInfo[prjNum].prj_id,
		dataType:'json',
		success : function(dataSysInfo) {
			if(Object.keys(dataSysInfo).length > 0){
				if(typeof(dataSysInfo.error) != 'undefined'){
					sysStatInfo[prjNum].index = 0;
				} else {
					sysStatInfo[prjNum].index = 1;
				}
			} else {
				sysStatInfo[prjNum].index = 0;
			}
			setSysStatBtn();
		}, error:function(e){
			sysStatInfo[prjNum].index = 0;
			setSysStatBtn();
		}
	});
}

//상태 버튼 셋팅.
function setSysStatBtn(){
	for(var pj=0; pj < prjInfo.length; pj++){
		var statTotal = sysStatInfo[pj].cluster + sysStatInfo[pj].logstash + sysStatInfo[pj].index;
//		console.log(prjInfo[pj].prj_id+" : "+statTotal);
		var btnHtml = "";
		btnHtml = "<i class='glyphicon glyphicon-off'></i> 확인 ["+statTotal+"/3]";
		$("#chSysBtn_"+prjInfo[pj].prj_id).html(btnHtml);
		if(statTotal === 3){
			$("#chSysBtn_"+prjInfo[pj].prj_id).removeClass("btn-default");
			$("#chSysBtn_"+prjInfo[pj].prj_id).removeClass("btn-danger");
			$("#chSysBtn_"+prjInfo[pj].prj_id).removeClass("btn-warning");
			$("#chSysBtn_"+prjInfo[pj].prj_id).addClass("btn-success");
		} else if(statTotal === 0){
			$("#chSysBtn_"+prjInfo[pj].prj_id).removeClass("btn-default");
			$("#chSysBtn_"+prjInfo[pj].prj_id).addClass("btn-danger");
			$("#chSysBtn_"+prjInfo[pj].prj_id).removeClass("btn-warning");
			$("#chSysBtn_"+prjInfo[pj].prj_id).removeClass("btn-success");
		} else {
			$("#chSysBtn_"+prjInfo[pj].prj_id).removeClass("btn-default");
			$("#chSysBtn_"+prjInfo[pj].prj_id).removeClass("btn-danger");
			$("#chSysBtn_"+prjInfo[pj].prj_id).addClass("btn-warning");
			$("#chSysBtn_"+prjInfo[pj].prj_id).removeClass("btn-success");
		}
	}
}

function sysState(pn){
	if(sysStatInfo[pn].cluster == 1){
		$("#prjStatEsTr").addClass("success");
		$("#prjStatEs").addClass("text-success");
		$("#prjStatEs").html("<i class='glyphicon glyphicon-ok'></i> 정상");
		$("#prjStatEsBtn").prop("disabled",true);
		
	} else {
		$("#prjStatEsTr").addClass("danger");
		$("#prjStatEs").addClass("text-danger");
		$("#prjStatEs").html("<i class='glyphicon glyphicon-remove'></i> 오류");
		$("#prjStatEsBtn").prop("disabled",false);
	}
	
	if(sysStatInfo[pn].logstash == 1){
		$("#prjStatLgsTr").addClass("success");
		$("#prjStatLgs").addClass("text-success");
		$("#prjStatLgs").html("<i class='glyphicon glyphicon-ok'></i> 정상");
		$("#prjStatLgsBtn").prop("disabled",true);
	} else {
		$("#prjStatLgsTr").addClass("danger");
		$("#prjStatLgs").addClass("text-danger");
		$("#prjStatLgs").html("<i class='glyphicon glyphicon-remove'></i> 오류");
		$("#prjStatLgsBtn").prop("disabled",false);
	}
	
	if(sysStatInfo[pn].index == 1){
		$("#prjStatIdxTr").addClass("success");
		$("#prjStatIdx").addClass("text-success");
		$("#prjStatIdx").html("<i class='glyphicon glyphicon-ok'></i>  정상");
		$("#prjStatIdxBtn").prop("disabled",true);
	} else {
		$("#prjStatIdxTr").addClass("danger");
		$("#prjStatIdx").addClass("text-danger");
		$("#prjStatIdx").html("<i class='glyphicon glyphicon-remove'></i> 오류");
		$("#prjStatIdxBtn").prop("disabled",false);
	}
	var frm = document.prjStatFrm;
	frm.prjId.value=prjInfo[pn].prj_id;
	frm.prjNum.value=pn;
	
	$("#prjStatMod").modal("show");
}
</script>

<h3>프로젝트 관리</h3>
<div class='col-md-8'>
	<blockquote>
		신규 프로젝트를 생성하거나 생성된 프로젝트를 공유할 수 있습니다.
		<br>다른 그룹의 프로젝트를 공유 요청을 해서 사용이 가능합니다.
	</blockquote>
</div>
<div class='col-md-4 text-right'>
	<button type='button' class='btn btn-primary' data-toggle='modal' data-target='#newPrjMod'>
		<i class='glyphicon glyphicon-plus'></i> 프로젝트 생성
	</button>
	<button type='button' class='btn btn-warning' data-toggle='modal' data-target='#reqShPrjMod'>
		<i class='glyphicon glyphicon-refresh'></i> 공유 요청
	</button>
</div>

<!-- 프로젝트 목록 시작 -->
<div class='row center-block'>

<table class='table table-condensed'>
	<thead>
		<tr>
			<th width='10%'></th>
			<th class='text-center' width='20%'>프로젝트명</th>
			<th class='text-center' width='25%'>소유그룹 (담당자)</th>
			<th class='text-center' width='15%'>프로젝트 공유</th>
			<th class='text-center' width='15%'>사용자</th>
			<th class='text-center' width='15%'>상태</th>
		</tr>
	</thead>
	<tbody class='prjTbBody' id='prjTbBody'>
	</tbody>
	<tfoot>
		<tr>
			<td colspan='6'></td>
		</tr>
	</tfoot>
</table>

</div>
<!-- 프로젝트 목록 끝 -->

<%@include file="/view/admin/adminProjectShare.jsp"%>

<%@include file="/view/admin/adminProjectUser.jsp"%>

<!-- 신규 프로젝트 생성 모달 폼 시작 -->
<form role='form' name='newPrjFrm' action='javascript:createPrj();'>
<div class='modal fade' id='newPrjMod' tabindex='-1' role='dialog' aria-labelledby='myModalLabel' aria-hidden='true'>
<div class='modal-dialog'>
<div class='modal-content'>

<div class='modal-header'>
	<button type='button' class='close' data-dismiss='modal'>
		<span aria-hidden='true'></span><span class='sr-only'>Close</span></button>
		<button type='button' class='close' data-dismiss='modal' aria-hidden='true'>×</button>
	<h4 class='modal-title' id='myModalLabel'>신규 프로젝트 생성 <small><span class='text-red'>*</span> 표시는 필수 항목입니다.</small></h4>
</div>

<div class='modal-body form-signin'>

<div class='row center-block'>
	<div class='col-sm-6 form-signin-col'>
		<h4>
			<label for='prjId'><span class='text-red'>*</span> 프로젝트 ID <small>20자 이내 영문자.</small></label>
		</h4>
		<div class='col-sm-6 form-signin-col'>
			<input type='text' class='form-control' id='prjId' name='prjId' placeholder='아이디' required='required' maxlength='20' onkeyup='putPrjId();' />
		</div>
		<div class='col-sm-4 form-signin-col'>
			<button class='btn btn-primary btn-block' type='button' onclick='checkPrj();'>중복확인</button>
		</div>
	</div>
	<div class='col-sm-6 form-signin-col'>
		<h4>
			<label for='prjName'><span class='text-red'>*</span> 프로젝트 이름 <small>50자 이내</small></label>
		</h4>
		<div class='col-sm-10 form-signin-col'>
			<input type='text' class='form-control' id='prjName' name='prjName' placeholder='프로젝트 이름' required='required' maxlength='50' />
		</div>
	</div>
</div>

<div class='row center-block '>
	<h4>
		<label for='prjText'>프로젝트 설명 <small>100자 이내</small></label>
	</h4>
	<div class='col-sm-11 form-signin-col'>
		<input type='text' class='form-control' id='prjText' name='prjText' placeholder='프로젝트 설명' maxlength='100' />
	</div>
</div>
<br>

<%
JmProperties jmProp = CommProp.getJmProperties();
if("each".equals(jmProp.get("esType"))){ 
%>
<div class='row center-block'>
	<h4>
		<label>엘라스틱서치 <small>EDAMS가 설치된 서버 기준입니다. 동일 서버이면 localhost 입력하세요.</small></label>
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
		<input type='number' class='form-control' id='esPort' name='esPort' required='required' placeholder='9200' disabled='disabled'/>
	</div>
	<div class='col-sm-2'>
		<h5>
			<label>
				<input id='esPortChk' type='checkbox' onclick='esPortClear();' checked='checked'> 자동
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
		<input type='number' class='form-control' id='esDPort' name='esDPort' placeholder='9300' disabled='disabled'/>
	</div>
	<div class='col-sm-2'>
		<h5>
			<label>
				<input id='esDPortChk' type='checkbox' onclick='esDPortClear();' checked='checked'> 자동
			</label>
		</h5>
	</div>
</div>
<script>
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
<% } else { %>
<input type='hidden' name='esUrl' value='<%=jmProp.get("esUrl")+""%>'>
<input type='hidden' name='esCluster' value='<%=jmProp.get("esCluster")+""%>'>
<input type='hidden' name='esPort' value='<%=jmProp.get("esPort")+""%>'>
<input type='hidden' name='esDPort' value='<%=jmProp.get("esDPort")+""%>'>
<% } %>

<div class='row center-block '>
	<h4>
		<label><span class='text-red'>*</span> 데이터 필드 <small class='text-red'>필드 정보는 최초 생성 이후 변경이 불가능합니다.</small></label>
	</h4>
	
	<table class='table table-condensed'>
		<thead>
			<tr>
				<th class='text-center' width='25%'>id ( 영문자, _ )</th>
				<th class='text-center' width='35%'>타입</th>
				<th class='text-center'>설명</th>
				<th class='text-center' width='9%'>
					<button class='btn btn-success btn-block btn-sm' type='button' onclick='addDataField();'>추가</button>
				</th>
			</tr>
		</thead>
		<tbody id='dataFieldBody'>
		</tbody>
	</table>
</div>

	</div>
	
<div class='modal-footer'>
	<button type='submit' class='btn btn-success'>입력 완료</button>
	<button type='button' class='btn btn-danger' data-dismiss='modal'>취소</button>
</div>
	
</div>
</div>
</div>

<input type='hidden' name='cmd' value='insertPrj' />
<input type='hidden' name='toUrl' value='/Admin/Project' />
</form>
<script>
var prjVal = 'N';
function checkPrj(){
	var prjIdVal = $('#prjId').val();
	if(prjIdVal === ''){
		alert('프로젝트 아이디를 입력하세요.');
		$('#prjId').focus();
		return;
	}
	$.ajax({
		type : 'GET',
		data : 'cmd=checkPrj&prjId=' + prjIdVal,
		url : '/Ajax/Project',
		dataType:'json',
		success : function(data) {
			if (data.result == 'OK') {
				alert('사용 가능한 프로젝트 아이디 입니다.');
				prjVal = 'Y';
			} else {
				alert('이미 존재하는 아이디 입니다.');
				prjVal = 'N';
			}
		}, error:function(e){  
			console.log(e.responseText);
		}
	});
}
function putPrjId(){
	prjVal = 'N';
}
function createPrj(){
	if(prjVal == 'N'){
		alert('프로젝트 아이디 중복 확인이 되지 않았습니다.\n프로젝트 아이디 중복을 확인하세요.');
		return;
	} else if($("input[name=fieldName]").length == 0) {
		alert('데이터 필드가 없습니다.\n최소 1개 이상의 데이터 필드를 입력하세요.');
		return;
	} else {
		if(confirm('프로젝트를 생성하시겠습니까?')){
<% if("each".equals(jmProp.get("esType"))){ %>
			if($('#esPortChk').is(':checked') == true){
				$('#esPort').val('');
			}
			if($('#esDPortChk').is(':checked') == true){
				$('#esDPort').val('');
			}
<% } %>
			var frm = document.newPrjFrm;
			frm.method = 'POST';
			frm.action = '/PrjConfirm';
			frm.submit();
		}
	}
}

function addDataField(){
	var flTxtVal = "";
	flTxtVal += "<tr>";
	flTxtVal += "<td>";
	flTxtVal += "	<input type='text' class='form-control' name='fieldName' maxlength='20' required='required'/>";
	flTxtVal += "</td>";
	flTxtVal += "<td>";
	flTxtVal += "	<select class='form-control' name='fieldType' onselected=''>";
	flTxtVal += "		<option value='string' selected='selected'>문자열 (Text)</option>";
	flTxtVal += "		<option value='long'>정수 (Long)</option>";
	flTxtVal += "		<option value='double'>실수 (Double)</option>";
	flTxtVal += "		<option value='date'>날짜/시간 (DateTime)</option>";
	flTxtVal += "		<option value='boolean'>불리언 (true/false)</option>";
//	flTxtVal += "		<option value='array'>배열 (Array)</option>";
//	flTxtVal += "		<option value='object'>객체 (Object)</option>";
	flTxtVal += "	</select>";
	flTxtVal += "</td>";
	flTxtVal += "<td>";
	flTxtVal += "	<input type='text' class='form-control' name='fieldTxt' maxlength='100' />";
	flTxtVal += "</td>";
	flTxtVal += "<td>";
	flTxtVal += "	<button class='btn btn-danger btn-block btn-sm btn-delField' type='button'>삭제</button>";
	flTxtVal += "</td>";
	flTxtVal += "</tr>";
	
	$("#dataFieldBody").append(flTxtVal);
	
	$(".btn-delField").click(function(){
		$(this).parent().parent().remove();
	});
}
addDataField();

</script>
<!-- 신규 프로젝트 생성 모달 폼 끝 -->

<!-- 프로젝트 공유 모달 폼 시작 -->
<form role='form' name='reqShPrjFrm' action='javascript:reqSharePrj();'>
<div class='modal fade' id='reqShPrjMod' tabindex='-1' role='dialog' aria-labelledby='myModalLabel' aria-hidden='true'>
<div class='modal-dialog'>
<div class='modal-content'>

<div class='modal-header'>
	<button type='button' class='close' data-dismiss='modal'>
		<span aria-hidden='true'></span><span class='sr-only'>Close</span></button>
		<button type='button' class='close' data-dismiss='modal' aria-hidden='true'>×</button>
	<h4 class='modal-title' id='myModalLabel'>프로젝트 공유 요청</h4>
</div>

<div class='modal-body form-signin'>

<div class='row center-block'>
	<h4>
		<label for='prjShGrp'>그룹 <small>공유 요청할 프로젝트를 소유하고 있는 그룹을 선택하세요.</small></label>
	</h4>
	<div class='col-sm-7'>
		<select id='prjShGrp' class='form-control'>
		</select>
	</div>
	<div class='col-sm-3'>
		<button class='btn btn-primary btn-block' type='button' onclick='shGrpPrjList();'>선택</button>
	</div>
</div>
<div class='row center-block'>
	<h4>
		<label for='prjShGrp'>프로젝트 <small>공유(요청)중인 건을 다시 요청하려면 먼저 기존 건을 삭제해야 합니다.</small></label>
	</h4>
	<div id='shGrpPrjList'></div>
</div>
<div class='row center-block'>
	<h4>
		<label for='prjShGrp'>요청 메시지 <small>500자 이내.</small></label>
	</h4>
	<div class='col-sm-10'>
		<textarea class='form-control' name='shGrpPrjMsg' maxlength="500"></textarea>
	</div>
</div>

</div>

<div class='modal-footer'>
	<button type='submit' class='btn btn-success'>요청 완료</button>
	<button type='button' class='btn btn-danger' data-dismiss='modal'>취소</button>
</div>
	
</div>
</div>
</div>
<input type='hidden' name='sharePrjId' value='' />
<input type='hidden' name='ownGrpId' value='' />
<input type='hidden' name='cmd' value='reqSharePrj' />
<input type='hidden' name='toUrl' value='/Admin/Project' />
</form>
<script>
function setPrjShGrp(){
	var optStr = '';
	if(grpInfo.length > 0){
		for(var tr=0; tr < grpInfo.length; tr++){
			optStr += "<option value='"+grpInfo[tr].grp_id+"' title='"+grpInfo[tr].grp_text+"'>"+grpInfo[tr].grp_name+"</option>";
		}
	}
	$("#prjShGrp").append(optStr);
}

var shGrpPrjVal;
function shGrpPrjList(){
	var grpIdVal = $("#prjShGrp option:selected").val();
	if(grpIdVal === '<%=apUserObj.getGrpId()+""%>'){
		var tblStr = '';
		tblStr += "<div class='col-sm-12'>";
		tblStr += "<p class='text-red'>자기 그룹의 프로젝트는 공유 요청 할 수 없습니다.";
		tblStr += "<br>자기 프로젝트를 다른 그룹과 공유하려면 프로젝트 목록의 공유설정 버튼을 클릭하세요.</p>";
		tblStr += "</div>";
		$("#shGrpPrjList").html(tblStr);
	} else {
		$.ajax({
			type : 'GET',
			data : 'cmd=shPrjInfo&grpId=' + grpIdVal,
			url : '/Ajax/Project',
			dataType:'json',
			success : function(data) {
				shGrpPrjVal = data;
				setShGrpPrjList();
			}, error:function(e){  
				console.log(e.responseText);
			}
		});
	}
}
function setShGrpPrjList(){
	var tblStr = '';
	if(shGrpPrjVal.length > 0){
		for(var tr=0; tr < shGrpPrjVal.length; tr++){
			var isShared = true;
			if(shGrpPrjVal[tr].pj_prj_grp_status == null){
				isShared = false;
			}
			tblStr += "<div class='col-sm-3 form-signin-col'>";
			tblStr += "<label>";
			if(isShared){
				tblStr += "<input type='checkbox' name='shPrjListChk' value='"+shGrpPrjVal[tr].prj_id+"' disabled='disabled'> "+shGrpPrjVal[tr].prj_name;
			} else {
				tblStr += "<input type='checkbox' name='shPrjListChk' value='"+shGrpPrjVal[tr].prj_id+"'> "+shGrpPrjVal[tr].prj_name;
			}
			tblStr += "</label>";
			tblStr += "</div>";
			tblStr += "<div class='col-sm-5 form-signin-col'>";
			tblStr += shGrpPrjVal[tr].prj_text;
			tblStr += "</div>";
			tblStr += "<div class='col-sm-3 form-signin-col text-right'>";
			
			if(!isShared){
				tblStr += "<label>";
				tblStr += "	<input type='radio' name='shRd_"+shGrpPrjVal[tr].prj_id+"' value='VIEWER' checked='checked'> 조회";
				tblStr += "</label> ";
				tblStr += "<label>";
				tblStr += "	<input type='radio' name='shRd_"+shGrpPrjVal[tr].prj_id+"' value='USER'> 조회/입력";
				tblStr += "</label>";
			} else {
				tblStr += "<p class='text-danger'>공유(요청)중</p>";
			}
			
			tblStr += "</div>";
			tblStr += "<div class='col-sm-1 text-center'>";
			tblStr += "</div>";
		}
	} else {
		tblStr += "<div class='col-sm-12'>";
		tblStr += "<p class='text-red'>그룹이 소유중인 프로젝트가 없습니다.</p>";
		tblStr += "</div>";
	}
	$("#shGrpPrjList").html(tblStr);
}

function reqSharePrj(){
	if(typeof($("input[name=shPrjListChk]")) === 'undefined' || $("input[name=shPrjListChk]:checked").length === 0){
		alert('선택된 프로젝트가 없습니다.');
		return;
	} else {
		if(confirm('프로젝트 공유를 요청하시겠습니까?')){
			var frm = document.reqShPrjFrm;
			frm.method = 'POST';
			frm.action = '/PrjConfirm';
			frm.submit();
		}
	}
}
</script>
<!-- 프로젝트 공유 모달 폼 끝 -->

<!-- 프로젝트 설정 모달 폼 시작 -->
<form role='form' name='modifPrjFrm' action='javascript:modifPrjSubmit();'>
<div class='modal fade' id='modifPrjMod' tabindex='-1' role='dialog' aria-labelledby='modifPrjLabel' aria-hidden='true'>
<div class='modal-dialog'>
<div class='modal-content'>

<div class='modal-header'>
	<button type='button' class='close' data-dismiss='modal' aria-hidden='true'>×</button>
	<h4 class='modal-title' id='modifPrjLabel'>프로젝트 설정
		<button type='button' class='btn btn-sm btn-danger' onclick='removePrj();'>
			<i class='glyphicon glyphicon-remove'></i> 프로젝트 삭제
		</button>
	</h4>
</div>

<div class='modal-body form-signin'>

<div class='row center-block'>
	<div class='col-sm-6 form-signin-col'>
		<h4>
			<label for='prjId'><span class='text-red'>*</span> 프로젝트 ID <small>수정 불가</small></label>
		</h4>
		<div class='col-sm-6 form-signin-col'>
			<input type='text' class='form-control' name='prjIdVal' value='' disabled='disabled' />
			<input type='hidden' name='prjId' value=''/>
		</div>
		<div class='col-sm-4 form-signin-col'>
		</div>
	</div>
	<div class='col-sm-6 form-signin-col'>
		<h4>
			<label for='prjName'><span class='text-red'>*</span> 프로젝트 이름 <small>50자 이내</small></label>
		</h4>
		<div class='col-sm-10 form-signin-col'>
			<input type='text' class='form-control' id='prjName' name='prjName' required='required' maxlength='50' />
		</div>
	</div>
</div>

<div class='row center-block '>
	<h4>
		<label for='prjText'>프로젝트 설명 <small>100자 이내</small></label>
	</h4>
	<div class='col-sm-11 form-signin-col'>
		<input type='text' class='form-control' id='prjText' name='prjText' maxlength='100' />
	</div>
</div>
<br>

<%
if("each".equals(jmProp.get("esType"))){
%>
<div class='row center-block'>
	<h4>
		<label>엘라스틱서치 <small>EDAMS가 설치된 서버 기준입니다. 동일 서버이면 localhost 입력하세요.</small></label>
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
		<input type='number' class='form-control' id='esPortMod' name='esPort' required='required' placeholder='9200' disabled='disabled'/>
	</div>
	<div class='col-sm-2'>
		<h5>
			<label>
				<input id='esPortChkMod' type='checkbox' onclick='esPortModClear();' checked='checked'> 자동
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
		<input type='number' class='form-control' id='esDPortMod' name='esDPort' placeholder='9300' disabled='disabled'/>
	</div>
	<div class='col-sm-2'>
		<h5>
			<label>
				<input id='esDPortChkMod' type='checkbox' onclick='esDPortModClear();' checked='checked'> 자동
			</label>
		</h5>
	</div>
</div>
<script>
function esPortModClear(){
	if($('#esPortChkMod').is(':checked') == true){
		$('#esPortMod').prop('disabled',true);
	} else {
		$('#esPortMod').prop('disabled',false);
	}
}
function esDPortModClear(){
	if($('#esDPortChkMod').is(':checked') == true){
		$('#esDPortMod').prop('disabled',true);
	} else {
		$('#esDPortMod').prop('disabled',false);
	}
}
</script>
<%
} else {
%>
<input type='hidden' name='esUrl' value='<%=jmProp.get("esUrl")+""%>'>
<input type='hidden' name='esCluster' value='<%=jmProp.get("esCluster")+""%>'>
<input type='hidden' name='esPort' value='<%=jmProp.get("esPort")+""%>'>
<input type='hidden' name='esDPort' value='<%=jmProp.get("esDPort")+""%>'>
<%
}
%>

<div class='row center-block '>
	<h4>
		<label>데이터 필드 <small>필드는 변경할 수 없습니다.</small></label>
	</h4>
	
	<table class='table table-condensed'>
		<thead>
			<tr>
				<th class='text-center' width='25%'>필드 id</th>
				<th class='text-center' width='35%'>타입</th>
				<th class='text-center'>설명</th>
			</tr>
		</thead>
		<tbody id='dataTypeBody'>
		</tbody>
	</table>
</div>

	</div>
	
<div class='modal-footer'>
	<button type='submit' class='btn btn-success'>정보 수정</button>
	<button type='button' class='btn btn-danger' data-dismiss='modal'>취소</button>
</div>
	
</div>
</div>
</div>

<input type='hidden' name='cmd' value='modifPrj' />
<input type='hidden' name='toUrl' value='/Admin/Project' />
</form>
<script>

//프로젝트 최초 셋팅
function modifPrj(prjNum){
	var frm = document.modifPrjFrm;
	frm.prjId.value = prjInfo[prjNum].prj_id;
	frm.prjIdVal.value = prjInfo[prjNum].prj_id;
	frm.prjName.value = prjInfo[prjNum].prj_name;
	frm.prjText.value = prjInfo[prjNum].prj_text;
	frm.esUrl.value = prjInfo[prjNum].prj_es_url;
	frm.esCluster.value = prjInfo[prjNum].prj_es_cluster;
	frm.esPort.value = prjInfo[prjNum].prj_es_port;
	frm.esDPort.value = prjInfo[prjNum].prj_es_dport;
	
	$("#dataTypeBody").html('');
	for(var fb=0; fb < prjInfo[prjNum].pj_prj_mapping.length; fb++){
		setMapping(prjInfo[prjNum].pj_prj_mapping[fb].field_id, prjInfo[prjNum].pj_prj_mapping[fb].field_type, prjInfo[prjNum].pj_prj_mapping[fb].field_comment);
	}
	
	$('#modifPrjMod').modal('show');
}

//데이터 타입 열 추가.
function setMapping(fldName, fldType, fieldTxt){
	if(fieldTxt == 'null') fieldTxt = '';
	var flTxtVal = "";
	flTxtVal += "<tr>";
	flTxtVal += "<td><input type=text class='form-control' value='"+fldName+"' disabled='dsabled'></td>";
	
	if(fldType === 'string'){
		flTxtVal += "<td><select class='form-control' disabled='disabled'><option>문자열 (Text)<option><select></td>";
	} else if(fldType === 'long'){
		flTxtVal += "<td><select class='form-control' disabled='disabled'><option>정수 (Long)<option><select></td>";
	} else if(fldType === 'double'){
		flTxtVal += "<td><select class='form-control' disabled='disabled'><option>실수 (Double)<option><select></td>";
	} else if(fldType === 'date'){
		flTxtVal += "<td><select class='form-control' disabled='disabled'><option>날짜/시간 (DateTime)<option><select></td>";
	} else if(fldType === 'boolean'){
		flTxtVal += "<td><select class='form-control' disabled='disabled'><option>불리언 (true/false)<option><select></td>";
	} else {
		flTxtVal += "<td></td>";
	}
	
	flTxtVal += "<td><input type=text class='form-control' value='"+fieldTxt+"' disabled='dsabled'></td>";
	flTxtVal += "<td></td>";
	
	flTxtVal += "</tr>";
	
	$("#dataTypeBody").append(flTxtVal);
}

//프로젝트 변경 정보 저장.
function modifPrjSubmit(){
	if(confirm('변경된 프로젝트 정보를 저장하시겠습니까?')){
		var frm = document.modifPrjFrm;
		frm.method = 'POST';
		frm.action = '/PrjConfirm';
		frm.submit();
	}
}

//프로젝트 삭제
function removePrj(){
	if(confirm('프로젝트를 삭제하시겠습니까?\n\n해당 프로젝트에 저장된 데이터는 모두 삭제되며\n공유된 그룹에서도 모두 제거됩니다.')){
		var frm = document.modifPrjFrm;
		frm.cmd.value='removePrj';
		frm.method = 'POST';
		frm.action = '/PrjConfirm';
		frm.submit();
	}
}
</script>
<!-- 프로젝트 설정 모달 폼 끝 -->

<!-- 프로젝트 상태 모달 폼 시작 -->
<form role='form' name='prjStatFrm' action='javascript:setPrjStatCommit();'>
<div class='modal fade' id='prjStatMod' tabindex='-1' role='dialog' aria-labelledby='prjStatLabel' aria-hidden='true'>
<div class='modal-dialog shreply'>
<div class='modal-content'>

<div class='modal-header'>
	<button type='button' class='close' data-dismiss='modal'>
		<span aria-hidden='true'></span><span class='sr-only'>Close</span></button>
		<button type='button' class='close' data-dismiss='modal' aria-hidden='true'>×</button>
	<h4 class='modal-title' id='prjStatLabel'>프로젝트 상태 조회</h4>
</div>

<div class='modal-body form-signin'>

<div class='row center-block'>

<table class='table table-condensed'>
<thead>
	<tr>
		<th width='5%' class='text-center'></th>
		<th width='25%' class='text-center'></th>
		<th width='50%' class='text-center'></th>
		<th width='20%' class='text-center'></th>
	</tr>
</thead>
<tbody>
	<tr id='prjStatEsTr'>
		<td></td>
		<td id='prjStatEs' class=''>
		</td>
		<td class=''>Elasticsearch</td>
		<td>
			<button type='button' id='prjStatEsBtn' class='btn btn-sm btn-success btn-block' onclick='alert("elasticsearch 관리자에게 문의하세요.");'>
				<i class='glyphicon glyphicon-off'></i> 실행
			</button>
		</td>
	</tr>
	<tr id='prjStatLgsTr'>
		<td></td>
		<td id='prjStatLgs' class=''></td>
		<td class=''>Logstash</td>
		<td>
			<button type='button' id='prjStatLgsBtn' class='btn btn-sm btn-success btn-block' onclick='runLogstash();'>
				<i class='glyphicon glyphicon-off'></i> 실행
			</button>
		</td>
	</tr>
	<tr id='prjStatIdxTr'>
		<td></td>
		<td id='prjStatIdx' class=''></td>
		<td class=''>Index</td>
		<td>
			<button type='button' id='prjStatIdxBtn' class='btn btn-sm btn-success btn-block' onclick='createIndex();'>
				<i class='glyphicon glyphicon-off'></i> 실행
			</button>
		</td>
	</tr>
</tbody>
</table>

<p id='prjStatStatTxt' class='text-danger text-center'></p>
</div>

</div>

<div class='modal-footer'>
	<button type='button' class='btn btn-danger' data-dismiss='modal'>닫기</button>
</div>
	
</div>
</div>
</div>
<input type='hidden' name='prjNum' />
<input type='hidden' name='prjId' />
<input type='hidden' name='cmd' value='setPrjStat' />
<input type='hidden' name='toUrl' value='/Admin/Project' />
</form>
<script>
function runLogstash(){
	var frm = document.prjStatFrm;
	var pn = frm.prjNum.value;
	if(sysStatInfo[pn].cluster == 0){
		alert('먼저 Elasticsearch가 실행중이어야 합니다.');
		return;
	} else {
		if(confirm('로그스태시를 재시작하시겠습니까?')){
			$("#prjStatStatTxt").html('로그스태시를 재시작 하는 중입니다.<br>평균 10~20초 정도 소요됩니다.<br>장시간 응답이 없는 경우 페이지를 새로고침 한 후<br>시스템 관리자에게 문의하세요.');
			var frm = document.prjStatFrm;
			frm.cmd.value='runLogstash';
			frm.method='POST';
			frm.action='/PrjConfirm';
			frm.submit();
		}
	}
}

function createIndex(){
	var frm = document.prjStatFrm;
	var pn = frm.prjNum.value;
	if(sysStatInfo[pn].cluster == 0){
		alert('먼저 Elasticsearch가 실행중이어야 합니다.');
		return;
	} else {
		if(confirm('인덱스를 생성하시겠습니까?')){
			var frm = document.prjStatFrm;
			frm.cmd.value='createIndex';
			frm.method='POST';
			frm.action='/PrjConfirm';
			frm.submit();
		}
	}
}
</script>
<!-- 프로젝트 상태 모달 폼 끝 -->