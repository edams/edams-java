<%@page import='edams.comm.CommUserObj'%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% CommUserObj apShUserObj = (CommUserObj) session.getAttribute("CommUserObj"); %>
<script>

//공유 설정 팝업 모달의 프로젝트 목록.
function prjShareOpt(pn) {

	//모달 안의 공유 제안 생성 시작
	var frm = document.shProvideFrm;
	frm.prjId.value=prjInfo[pn].prj_id;
	var prvBdTxt = "";
	prvBdTxt += "<div class='row center-block'>";
	prvBdTxt += "<h4>프로젝트를 공유할 그룹을 선택하세요.</h4>";
	prvBdTxt += "<p class='text-danger'>별도의 승인과정 없이 바로 공유됩니다.</p>";
	for(var mi=0; mi<grpInfo.length; mi++){
		if(grpInfo[mi].grp_id !== '<%=apShUserObj.getGrpId()+""%>'){
			var isSharing = false;
			var isNoUser = false;
			for(var pni=0; pni < prjInfo[pn].pj_prj_grp_shared.length; pni++){
				if(prjInfo[pn].pj_prj_grp_shared[pni].use_grp_id === grpInfo[mi].grp_id){
					isSharing = true;
				}
			}
			if(grpInfo[mi].user_cnt === 0){
				isNoUser = true;
			}
			prvBdTxt += "<div class='row'>";
			if(isNoUser){
				prvBdTxt += "<div class='col-sm-7'>";
				prvBdTxt += "	<label title='"+grpInfo[mi].grp_text+"'>";
				prvBdTxt += "		<input type='checkbox' disabled='disabled'> "+grpInfo[mi].grp_name;
				prvBdTxt += "	</label>";
				prvBdTxt += "</div>";
				prvBdTxt += "<div class='col-sm-5'>";
				prvBdTxt += "	<p class='text-danger'>그룹 사용자 0</p>";
				prvBdTxt += "</div>";
			} else if(isSharing){
				prvBdTxt += "<div class='col-sm-9'>";
				prvBdTxt += "	<label title='"+grpInfo[mi].grp_text+"'>";
				prvBdTxt += "		<input type='checkbox' disabled='disabled' checked='checked'> "+grpInfo[mi].grp_name;
				prvBdTxt += "	</label>";
				prvBdTxt += "</div>";
				prvBdTxt += "<div class='col-sm-3'>";
				prvBdTxt += "	<p class='text-danger'>공유중</p>";
				prvBdTxt += "</div>";
			} else {
				prvBdTxt += "<div class='col-sm-7'>";
				prvBdTxt += "	<label title='"+grpInfo[mi].grp_text+"'>";
				prvBdTxt += "		<input type='checkbox' name='prvidGrpId' value='"+grpInfo[mi].grp_id+"'> "+grpInfo[mi].grp_name;
				prvBdTxt += "	</label>";
				prvBdTxt += "</div>";
				prvBdTxt += "<div class='col-sm-5'>";
				prvBdTxt += "	<label>";
				prvBdTxt += "		<input type='radio' checked='checked' value='VIEWER' name='provRd_"+grpInfo[mi].grp_id+"'> 조회";
				prvBdTxt += "	</label>";
				prvBdTxt += "	<label>";
				prvBdTxt += "		<input type='radio' value='USER' name='provRd_"+grpInfo[mi].grp_id+"'> 조회/입력";
				prvBdTxt += "	</label>";
				prvBdTxt += "	<input type='hidden' value='"+grpInfo[mi].admin_email+"' name='admEmail_"+grpInfo[mi].grp_id+"'>";
				prvBdTxt += "</div>";
			}
			prvBdTxt += "</div>";
		}
	}
	prvBdTxt += "</div>";
	$("#shProvideModBody").html(prvBdTxt);
	//모달 안의 공유 제안 생성 끝 
	
	var shareModTxt = '';
	shareModTxt += "<div class='row center-block'>";
	
	shareModTxt += "<table class='table table-condensed'>";
	shareModTxt += "<thead>";
	shareModTxt += "	<tr>";
	shareModTxt += "		<th class='text-center' width='36%'>그룹명 (담당자)</th>";
	shareModTxt += "		<th class='text-center' width='14%'>권한</th>";
	shareModTxt += "		<th class='text-center' width='26%' colspan='2' >공유 요청</th>";
	shareModTxt += "		<th class='text-center' width='24%'>상태</th>";
	shareModTxt += "	</tr>";
	shareModTxt += "</thead>";
	shareModTxt += "<tbody class='prjTbBody'>";
	
	for(var i=0; i < prjInfo[pn].pj_prj_grp_shared.length; i++){
		if(prjInfo[pn].pj_prj_grp_shared[i].status === 'REQUEST'){
			shareModTxt += "<tr class='warning'>";
		} else if(prjInfo[pn].pj_prj_grp_shared[i].status === 'DENIED'){
			shareModTxt += "<tr class='danger'>";
		} else if(prjInfo[pn].pj_prj_grp_shared[i].status === 'APPROVED' || prjInfo[pn].pj_prj_grp_shared[i].status === 'PROVIDED'){
			shareModTxt += "<tr class='success'>";
		} else {
			shareModTxt += "<tr>";
		}
		shareModTxt += "<td class=''>"+prjInfo[pn].pj_prj_grp_shared[i].pj_prj_grp_req_user.grp_name+" ("+prjInfo[pn].pj_prj_grp_shared[i].pj_prj_grp_req_user.nicname+")</td>";
		
		if(prjInfo[pn].pj_prj_grp_shared[i].grp_type === 'VIEWER'){
			shareModTxt += "<td class='text-center'>조회</td>";
		} else if(prjInfo[pn].pj_prj_grp_shared[i].grp_type === 'USER'){
			shareModTxt += "<td class='text-center'>조회/입력</td>";
		} else if(prjInfo[pn].pj_prj_grp_shared[i].grp_type === 'OWNER'){
			shareModTxt += "<td class='text-center'>소유자</td>";
		}
		
		if(prjInfo[pn].grp_type === 'OWNER'){
			var reqMsg = prjInfo[pn].pj_prj_grp_shared[i].req_msg;
			if(reqMsg == null) reqMsg = '';
			var resMsg = prjInfo[pn].pj_prj_grp_shared[i].res_msg;
			if(resMsg == null) resMsg = '';
			if(prjInfo[pn].pj_prj_grp_shared[i].status === 'REQUEST'){
				shareModTxt += "<td>";
				shareModTxt += "	<button class='btn btn-default btn-sm btn-block' type='button' data-container='body' data-toggle='popover' data-placement='bottom' data-content='"+reqMsg+"' data-original-title title >요청메시지</button>";
				shareModTxt += "</td>";
				shareModTxt += "<td>";
				shareModTxt += "	<button class='btn btn-default btn-sm btn-block' type='button' data-container='body' data-toggle='popover' data-placement='bottom' data-content='"+resMsg+"' data-original-title title >응답메시지</button>";
				shareModTxt += "</td>";
				
				shareModTxt += "<td class='text-center'>";
				shareModTxt += "	<button class='btn btn-success btn-sm' type='button' onclick='shPrjReplyMod(\""+prjInfo[pn].prj_id+"\",\""+prjInfo[pn].pj_prj_grp_shared[i].use_grp_id+"\",\"APPROVED\")'><i class='glyphicon glyphicon-ok'></i> 승인</button>";
				shareModTxt += "	<button class='btn btn-danger btn-sm' type='button' onclick='shPrjReplyMod(\""+prjInfo[pn].prj_id+"\",\""+prjInfo[pn].pj_prj_grp_shared[i].use_grp_id+"\",\"DENIED\")'><i class='glyphicon glyphicon-remove'></i> 반려</button>";
				shareModTxt += "</td>";
			} else if(prjInfo[pn].pj_prj_grp_shared[i].status === 'APPROVED' || prjInfo[pn].pj_prj_grp_shared[i].status === 'PROVIDED'){
				shareModTxt += "<td>";
				shareModTxt += "	<button class='btn btn-default btn-sm btn-block' type='button' data-container='body' data-toggle='popover' data-placement='bottom' data-content='"+reqMsg+"' data-original-title title >요청메시지</button>";
				shareModTxt += "</td>";
				shareModTxt += "<td>";
				shareModTxt += "	<button class='btn btn-default btn-sm btn-block' type='button' data-container='body' data-toggle='popover' data-placement='bottom' data-content='"+resMsg+"' data-original-title title >응답메시지</button>";
				shareModTxt += "</td>";
				
				shareModTxt += "<td>";
				shareModTxt += "	<button class='btn btn-success btn-sm' type='button' disabled='disabled' >공유됨</button>";
				shareModTxt += "	<button class='btn btn-danger btn-sm' type='button' onclick='shPrjReplyMod(\""+prjInfo[pn].prj_id+"\",\""+prjInfo[pn].pj_prj_grp_shared[i].use_grp_id+"\",\"DENIED\")'><i class='glyphicon glyphicon-remove'></i> 반려</button>";
				shareModTxt += "</td>";
			} else if(prjInfo[pn].pj_prj_grp_shared[i].status === 'DENIED'){
				shareModTxt += "<td>";
				shareModTxt += "	<button class='btn btn-default btn-sm btn-block' type='button' data-container='body' data-toggle='popover' data-placement='bottom' data-content='"+reqMsg+"' data-original-title title >요청메시지</button>";
				shareModTxt += "</td>";
				shareModTxt += "<td>";
				shareModTxt += "	<button class='btn btn-default btn-sm btn-block' type='button' data-container='body' data-toggle='popover' data-placement='bottom' data-content='"+resMsg+"' data-original-title title >응답메시지</button>";
				shareModTxt += "</td>";
				
				shareModTxt += "<td>";
				shareModTxt += "	<button class='btn btn-danger btn-sm btn-block' type='button' disabled='disabled' >반려됨</button>";
				shareModTxt += "</td>";
			} else if(prjInfo[pn].pj_prj_grp_shared[i].grp_type === 'OWNER'){
				shareModTxt += "<td></td>";
				shareModTxt += "<td></td>";
				shareModTxt += "<td>";
				shareModTxt += "	<button class='btn btn-default btn-sm btn-block' type='button' disabled='disabled' >소유자</button>";
				shareModTxt += "</td>";
			} else {
				shareModTxt += "<td></td>";
				shareModTxt += "<td></td>";
				shareModTxt += "<td></td>";
			}
		} else {
		}
		shareModTxt += "</tr>";
	}
	shareModTxt += "</tbody>";
	shareModTxt += "</table>";
	
	shareModTxt += "</div>";
	
	$("#shareBtnModBody").html(shareModTxt);
	$('#shareBtnMod').modal('show');
	
	$("[data-toggle='popover']").popover();
	
}

function prjShareRemove(owner_grp_id,prj_id){
	if(confirm('공유된 프로젝트를 제거하시겠습니까?')){
		var frm = document.reqShPrjFrm;
		frm.method = 'POST';
		frm.action = '/PrjConfirm';
		frm.cmd.value='prjShareRemove';
		frm.sharePrjId.value=prj_id;
		frm.ownGrpId.value=owner_grp_id;
		frm.submit();
	}
}

function shPrjReplyMod(prj_id,reqGrpId,answer){
	var frm = document.shPrjRepFrm;
	frm.prjId.value=prj_id;
	frm.prjRepType.value=answer;
	frm.reqGrpId.value=reqGrpId;
	if(answer === 'APPROVED'){
		if(confirm('공유 요청을 승인하시겠습니까?')){
			frm.submit();
		}
	} else if(answer === 'DENIED'){
		$('#shPrjReplyMod').modal('show');
	}
}

function shProvide(prj_id){
	$('#shProvideMod').modal('show');
}

function shProvideSubmit(){
	if(typeof($("input[name=prvidGrpId]")) === 'undefined' || $("input[name=prvidGrpId]:checked").length === 0){
		alert('선택된 업체가 없습니다.');
		return;
	} else {
		if(confirm('선택한 업체와 프로젝트를 공유하시겠습니까?')){
			var frm = document.shProvideFrm;
			frm.action = '/PrjConfirm';
			frm.submit();
		}
	}
}
</script>
<!-- 공유 설정 버튼 모달 시작 -->
<div class='modal fade' id='shareBtnMod' tabindex='-1' role='dialog' aria-labelledby='shareBtnModLabel' aria-hidden='true'>
<div class='modal-dialog'>
<div class='modal-content'>

<div class='modal-header'>
	<button type='button' class='close' data-dismiss='modal'>
		<span aria-hidden='true'></span><span class='sr-only'>Close</span></button>
		<button type='button' class='close' data-dismiss='modal' aria-hidden='true'>×</button>
	<h4 class='modal-title' id='shareBtnModLabel'>공유 설정
	<button class='btn btn-sm btn-warning' onclick='shProvide();'>
		<i class='glyphicon glyphicon-refresh'></i> 프로젝트 공유
	</button>
	</h4>
</div>

<div class='modal-body form-signin' id='shareBtnModBody'>
	
</div>

</div>
</div>
</div>
<!-- 공유 설정 버튼 모달 끝 -->

<!-- 공유 제안 모달 시작 -->
<form role='form' name='shProvideFrm' method='POST' action='javascript:shProvideSubmit();'>
<div class='modal' id='shProvideMod' tabindex='-1' role='dialog' aria-labelledby='shProvideLabel' aria-hidden='true'>
<div class='modal-dialog shreply'>
<div class='modal-content'>

<div class='modal-header'>
	<button type='button' class='close' data-dismiss='modal'>
		<span aria-hidden='true'></span><span class='sr-only'>Close</span></button>
		<button type='button' class='close' data-dismiss='modal' aria-hidden='true'>×</button>
	<h4 class='modal-title' id='shProvideLabel'>프로젝트 공유</h4>
</div>

<div class='modal-body form-signin' id='shProvideModBody'>
	
</div>

<div class='modal-footer'>
	<button type='submit' class='btn btn-success'>공유</button>
	<button type='button' class='btn btn-danger' data-dismiss='modal'>취소</button>
</div>

</div>
</div>
</div>
<input type='hidden' name='prjId' value='' />
<input type='hidden' name='cmd' value='prjShProvide' />
<input type='hidden' name='toUrl' value='/Admin/Project' />
</form>
<!-- 공유 제안 모달 끝 -->

<!-- 공유 반려 모달 시작 -->
<form role='form' name='shPrjRepFrm' method='POST' action='/PrjConfirm'>
<div class='modal' id='shPrjReplyMod' tabindex='-1' role='dialog' aria-labelledby='myModalLabel' aria-hidden='true'>
<div class='modal-dialog shreply'>
<div class='modal-content'>

<div class='modal-header'>
	<button type='button' class='close' data-dismiss='modal'>
		<span aria-hidden='true'></span><span class='sr-only'>Close</span></button>
		<button type='button' class='close' data-dismiss='modal' aria-hidden='true'>×</button>
	<h4 class='modal-title' id='myModalLabel'>공유요청 반려</h4>
</div>

<div class='modal-body form-signin'>
	<div class='row center-block'>
		<div class='col-sm-12 form-signin-col'>
			<textarea class='form-control' name='resMsg' rows='2' required='required'></textarea>
		</div>
	</div>
</div>

<div class='modal-footer'>
	<button type='submit' class='btn btn-success'>반려</button>
	<button type='button' class='btn btn-danger' data-dismiss='modal'>취소</button>
</div>

</div>
</div>
</div>
<input type='hidden' name='prjId' value='' />
<input type='hidden' name='prjRepType' value='' />
<input type='hidden' name='reqGrpId' value='' />
<input type='hidden' name='cmd' value='prjShareReply' />
<input type='hidden' name='toUrl' value='/Admin/Project' />
</form>
<!-- 공유 반려 모달 끝 -->