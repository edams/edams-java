<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<script>

/**
 * 파일 목록 선택
 */
function selFileInfo(file_id,file_name,file_type){
//	var fNum = Number(rd.value);
//	$(".mapListTr").prop("checked", false);
	$(".map-list-btn").removeClass("active");
	$(".map-list-tr").removeClass("info");
	
	$(".file-list-btn").removeClass("active");
	$(".file-list-btn-"+file_id).addClass("active");
	
	$(".file-list-tr").removeClass("info");
	$(".file-list-tr-"+file_id).addClass("info");
	
	$("#map_id").attr('disabled',false);
	$("#map_id").val('');
	$("#src_file").val(file_name);
	$("#mapTitle").text("매핑 정보 - "+file_name);
	$.ajax({
		type : "GET",
		url : "/Ajax/File?cmd=mapInfo&fileName="+file_name+"&fileType="+file_type,
		dataType:"json",
		success : function(mapInfoAjax) {
			mapInfoObj = mapInfoAjax.map_info;
			setMapInfo();
			
		}, error:function(e){   
			console.log(e.responseText);  
		}
	});
}

/**
 * 매핑 목록 선택 
 */
function selMapinfo(numVal){
	var num = Number(numVal);
	
	$(".file-list-btn").removeClass("active");
	$(".map-list-btn").removeClass("active");
	$(".map-list-btn").eq(num).addClass("active");
	
	$(".file-list-tr").removeClass("info");
	$(".map-list-tr").removeClass("info");
	$(".map-list-tr").eq(num).addClass("info");
	
	$("#map_id").attr('disabled',true);
	$("#map_id").val(mapListObj[num].map_id);
	$("#src_file").val(mapListObj[num].src_file);
	$("#mapTitle").text("매핑 정보 - "+mapListObj[num].map_id);
	
	mapInfoObj = mapListObj[num].field_info;
	setMapInfo();
}

function setMapInfo(){
	$("#mapPanel").show();
	$("#tbMapInfo").html("");
	var tblStr = '';
	
	/* 매핑정보 테이블 생성 */
	for(var tr=0; tr<mapInfoObj.length; tr++){
		var valStr = mapInfoObj[tr].value;
		if(valStr.length > 35){
			valStr = valStr.substring(0, 33)+"...";
		}
		tblStr += '<tr class="mapRow">';
		tblStr += '	<th class="text-center">'+(tr+1)+'</th>';
		tblStr += '	<td><input type="text" maxlength="50" class="form-control input-sm map-control-name" value="'+mapInfoObj[tr].name+'"/></td>';
		tblStr += '	<td>';
		tblStr += '		<select class="form-control input-sm map-control-sel">';
		tblStr += '			<option value="string">문자열 (String)</option>';
		tblStr += '			<option value="long">정수 (Long)</option>';
		tblStr += '			<option value="double">실수 (Double)</option>';
		tblStr += '			<option value="datetime">날짜/시간 (DateTime)</option>';
		tblStr += '			<option value="boolean">불리언 (Boolean)</option>';
		tblStr += '		</select>';
		tblStr += '<input type="text" class="form-control input-sm map-control-date" value="'+mapInfoObj[tr].date_format+'"/>';
		tblStr += '<input type="hidden" class="map-control-value" value="'+valStr+'"/>';
		tblStr += '	</td>';
		tblStr += '	<td>'+valStr+'</td>';
		tblStr += "</tr>";
	}
	$("#tbMapInfo").append(tblStr);
	
	//형식 셀렉트박스 날짜 선택 한 경우 날짜 포맷 폼 보이도록 설정.
	$(".map-control-sel").change(function(){
		if($(this).val() == 'datetime'){
			$(this).next().show();
		} else {
			$(this).next().hide();
		}
	});
	
	for(var tr=0; tr<mapInfoObj.length; tr++){
		$(".map-control-sel").eq(tr).val(mapInfoObj[tr].type);
		if($(".map-control-sel").eq(tr).val() == 'datetime'){
			$(".map-control-sel").eq(tr).next().show();
		} else {
			$(".map-control-sel").eq(tr).next().hide();
		}
	}
	
}

$(document).ready(function() {

	/* 깜빡임 효과 */
	setInterval(function() { 
		$('.blink').fadeOut('slow').fadeIn('slow'); 
	},1500);
	
	/* 매핑 저장 버튼 눌렀을 때 */
	$("#btnSaveMap").click(function(){
		//저장을 위한 Map Object 생성.
		
		if($("#map_id").val() == ""){
			alert("매핑 아이디를 입력하세요.");
			$("#map_id").focus();
			return;
		} else {
			var isExist = false;
			for(var ml=0; ml < mapListObj.length; ml++){
				if(mapListObj[ml].map_id == $("#map_id").val()){
					isExist = true;
				}
			}
			if(isExist){
				if($("#map_id").is(':disabled') == false){
					alert("존재하는 매핑 아이디 입니다.");
					$("#map_id").focus();
					return;
				} else {
					if(!confirm($("#map_id").val()+" 매핑을 수정하시겠습니까?")){
						return;
					}
				}
			}

			$("#btnSaveMap").prop("disabled",true);
			mapPutObj = new Object();
			mapPutObj.src_file = $("#src_file").val();
			mapPutObj.map_id = $("#map_id").val();
			mapPutObj.map_info = new Array();
			for(var i=0; i < $(".map-control-name").length; i++){
				var mapInfoUnit = {
						"name":$(".map-control-name").eq(i).val(),
						"type":$(".map-control-sel").eq(i).val(),
						"date_format":$(".map-control-date").eq(i).val(),
						"value":$(".map-control-value").eq(i).val()
				};
				mapPutObj.map_info[i] = mapInfoUnit;
			}
			
			$.ajax({
				type : "POST",
				data : JSON.stringify(mapPutObj),
				url : "/Ajax/Mapping",
				dataType:"json",
				success : function(mapListData) {
					$("#btnSaveMap").prop("disabled",false);
					$("#map_id").val("");
					$("#src_file").val("");
					$("#tbMapInfo").html("");
					
					$(".map-list-btn").removeClass("active");
					$(".map-list-tr").removeClass("info");
					$(".file-list-btn").removeClass("active");
					$(".file-list-tr").removeClass("info");
					
					if(mapListData.result ==="OK"){
						getMapList();
					} else {
						console.log("저장 오류.");
					}
				}, error:function(e){   
					console.log(e.responseText);  
				}
			});
			
		} 
		
	});
	
});
</script>

<div class="panel panel-info" id="mapPanel" style="display:none">
	<div class="panel-heading">
		<h3 class="panel-title" id="mapTitle"></h3>
	</div>
	<div class="panel-body">
	
<p class="text-muted">
매핑 아이디를 입력하고 저장 버튼을 누르면 매핑 목록에 추가됩니다.<br>
파일의 첫 라인에서 데이터 형식을 추출합니다.<br>
CSV파일의 첫 라인이 모두 문자열이면 이 값은 필드명으로 하고 두번째 라인에서 데이터를 추출합니다.<br>
CSV파일은 입력 순서대로 필드가 생성되며 JSON파일은 필드 순서에 영향을 받지 않습니다.
</p>

<form class="form-inline pull-right">
	<div class="form-group">
		<label class="sr-only" for="map_id">매핑 아이디</label>
		<input type="text" size="13" maxlength="20" class="form-control input-sm" id="map_id" placeholder="매핑 아이디 (20자 이내)">
		<input type="hidden" id="src_file" value="">
	</div>
	<button type="button" class="btn btn-sm btn-success" id="btnSaveMap">
		<i class="glyphicon glyphicon-save"></i>
		<span>저장</span>
	</button>
</form>
<br>
<br>
<table class="table">
	<thead>
		<tr>
			<th class="text-center" width="10px"></th>
			<th class="text-center" width="100px">필드명</th>
			<th class="text-center" width="180px">형식</th>
			<th class="text-center">추출 데이터</th>
		</tr>
	</thead>
	<tbody id="tbMapInfo"></tbody>
</table>
	
	</div>
</div>

