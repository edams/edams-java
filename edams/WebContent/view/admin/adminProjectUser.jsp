<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<script>

/**
 * 사용자 정보 Ajax로 불러온 후 설정 모달 값 입력.
 */
var grpAllUsrInfo;
function prjUsrSet(own_grp_id,prj_id,grp_type){
	var frm = document.prjUsrSetFrm;
	frm.prjId.value=prj_id;
	frm.prjOwnGrpId.value=own_grp_id;
	
	$.ajax({
		type : 'GET',
		url : '/Ajax/Project?cmd=prjUserInfo&prjId='+prj_id,
		dataType:'json',
		success : function(dataUsrInfo) {
			grpAllUsrInfo = dataUsrInfo;
			prjUsrModSet(grp_type);
		}, error:function(e){  
			console.log(e.responseText);
		}
	});
}
function prjUsrModSet(grp_type){
	var prjUsrModTxt = '';
	prjUsrModTxt += "<div class='row center-block'>";
	if(grp_type === 'VIEWER'){
		prjUsrModTxt += "<p class='text-red'>프로젝트가 조회 권한으로 공유되었기 때문에 조회만 가능합니다.</p>";
	}
	prjUsrModTxt += "<table class='table table-condensed'>";
	prjUsrModTxt += "<thead>";
	prjUsrModTxt += "	<tr>";
	prjUsrModTxt += "		<th class='' width='25%'>";
	prjUsrModTxt += "			<input type='checkbox' id='prjUsrChkAll'> 닉네임 (이름)</th>";
	prjUsrModTxt += "		<th class='' width='40%'>이메일</th>";
	prjUsrModTxt += "		<th class='' width='10%'>";
	prjUsrModTxt += "		</th>";
	prjUsrModTxt += "		<th class='' width='15%'>";
	prjUsrModTxt += "		</th>";
	prjUsrModTxt += "	</tr>";
	prjUsrModTxt += "</thead>";
	prjUsrModTxt += "<tbody class='prjTbBody'>";
	for(var i=0; i < grpAllUsrInfo.length; i++){
		var user_type = grpAllUsrInfo[i].pj_prj_grp_user_type;
		prjUsrModTxt += "	<tr>";
		prjUsrModTxt += "		<td class=''>";
		prjUsrModTxt += "			<label class='radio'>";
		if(user_type === 'VIEWER' || user_type === 'USER'){
			prjUsrModTxt += "			<input class='prjUsrChk' type='checkbox' checked='checked' name='prjUsrChk' value='"+grpAllUsrInfo[i].email+"'>";
		} else {
			prjUsrModTxt += "			<input class='prjUsrChk' type='checkbox' name='prjUsrChk' value='"+grpAllUsrInfo[i].email+"'>";
		}
		prjUsrModTxt += "		"+grpAllUsrInfo[i].nicname+" ("+grpAllUsrInfo[i].name+")";
		prjUsrModTxt += "			</label>";
		prjUsrModTxt += "		</td>";
		prjUsrModTxt += "		<td class=''>"+grpAllUsrInfo[i].email+"</td>";
		prjUsrModTxt += "		<td class='text-center'>";
		
		if(grp_type === 'VIEWER'){
			//조회 권한만 있는 경우, 조회만 선택 가능.
			prjUsrModTxt += "			<label class='radio'>";
			prjUsrModTxt += "				<input type='radio' checked='checked' name='rd_"+grpAllUsrInfo[i].email+"' value='VIEWER'> 조회만";
			prjUsrModTxt += "			</label>";
			prjUsrModTxt += "		</td>";
			prjUsrModTxt += "		<td class='text-center'>";
			prjUsrModTxt += "			<label class='radio'>";
			prjUsrModTxt += "				<input type='radio' name='rd_"+grpAllUsrInfo[i].email+"' disabled='disabled'> 조회/입력";
			prjUsrModTxt += "			</label>";
		} else {
			prjUsrModTxt += "			<label class='radio'>";
			if(user_type === 'USER'){
				prjUsrModTxt += "				<input type='radio' name='rd_"+grpAllUsrInfo[i].email+"' value='VIEWER'> 조회만";
			} else {
				prjUsrModTxt += "				<input type='radio' checked='checked' name='rd_"+grpAllUsrInfo[i].email+"' value='VIEWER'> 조회만";
			}
			prjUsrModTxt += "			</label>";
			prjUsrModTxt += "		</td>";
			prjUsrModTxt += "		<td class='text-center'>";
			prjUsrModTxt += "			<label class='radio'>";
			if(user_type === 'USER'){
				prjUsrModTxt += "				<input type='radio' checked='checked' name='rd_"+grpAllUsrInfo[i].email+"' value='USER'> 조회/입력";
			} else {
				prjUsrModTxt += "				<input type='radio' name='rd_"+grpAllUsrInfo[i].email+"' value='USER'> 조회/입력";
			}
			prjUsrModTxt += "			</label>";
		}
		
		prjUsrModTxt += "		</td>";
		prjUsrModTxt += "	</tr>";
	}
	prjUsrModTxt += "<tbody>";
	prjUsrModTxt += "</table>";
	prjUsrModTxt += "</div>";
	
	$("#prjUsrSetModBody").html(prjUsrModTxt);
	$('#prjUsrSetMod').modal('show');
	
	$("#prjUsrChkAll").click(function(){
        var chk = $(this).is(":checked");
        if(chk){
        	$(".prjUsrChk").prop('checked', true);
        }
        else{
        	$(".prjUsrChk").prop('checked', false);
        }
    });
	
}

function prjUsrSubmit(){
	if(typeof($("input[name=prjUsrChk]")) === 'undefined' || $("input[name=prjUsrChk]:checked").length === 0){
		if(confirm('선택된 사용자가 없습니다.\n저장시 기존 사용자들이 모두 삭제됩니다.\n계속 하시겠습니까?')){
			var frm = document.prjUsrSetFrm;
			frm.method = 'POST';
			frm.action = '/PrjConfirm';
			frm.submit();
		}
		return;
	} else {
		var frm = document.prjUsrSetFrm;
		frm.method = 'POST';
		frm.action = '/PrjConfirm';
		frm.submit();
	}
}
</script>

<!-- 사용자 관리 모달 시작 -->
<form role='form' name='prjUsrSetFrm' method='POST' action='javascript:prjUsrSubmit();'>
<div class='modal' id='prjUsrSetMod' tabindex='-1' role='dialog' aria-labelledby='myModalLabel' aria-hidden='true'>
<div class='modal-dialog'>
<div class='modal-content'>

<div class='modal-header'>
	<button type='button' class='close' data-dismiss='modal'>
		<span aria-hidden='true'></span><span class='sr-only'>Close</span></button>
		<button type='button' class='close' data-dismiss='modal' aria-hidden='true'>×</button>
	<h4 class='modal-title' id='myModalLabel'>사용자 설정</h4>
</div>

<div class='modal-body form-signin' id='prjUsrSetModBody'>
	
</div>

<div class='modal-footer'>
	<button type='submit' class='btn btn-success'>저장</button>
	<button type='button' class='btn btn-danger' data-dismiss='modal'>취소</button>
</div>

</div>
</div>
</div>
<input type='hidden' name='prjId' value='' />
<input type='hidden' name='prjOwnGrpId' value='' />
<input type='hidden' name='cmd' value='prjUserSet' />
<input type='hidden' name='toUrl' value='/Admin/Project' />
</form>
<!-- 사용자 관리 모달 끝 -->