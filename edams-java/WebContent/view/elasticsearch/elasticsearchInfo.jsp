<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div class="panel panel-info" id="esInfoPanel" style="display:none">
	<div class="panel-heading">
		<h3 class="panel-title" id="esInfoTitle">엘라스틱서치 정보</h3>
	</div>
	<div class="panel-body">
	
<div class="col-sm-10">
	<p class="text-muted">
		EDAMS가 설치된 서버 기준입니다.<br>
		EDAMS와 동일한 서버에 설치되어 있으면 localhost를 입력하세요.
	</p>
</div>
	
<div class="col-sm-2 text-right">
	<button type="button" class="btn btn-sm btn-success" id="btnSaveEs">
		<i class="glyphicon glyphicon-save"></i>
		<span>저장</span>
	</button>
</div>
<form class="form-horizontal">
	<div class="form-group form-group-sm">
		<label class="col-sm-3 control-label" for="es_id">엘라스틱서치 아이디</label>
		<div class="col-sm-3">
			<input class="form-control es-input-form" type="text" id="es_id" placeholder="edams-1">
		</div>
		<label class="col-sm-2 control-label" for="es_cluster">클러스터</label>
		<div class="col-sm-3">
			<input class="form-control es-input-form" type="text" id="es_cluster" placeholder="elasticsearch">
		</div>
	</div>
	<div class="form-group form-group-sm">
		<label class="col-sm-3 control-label" for="es_url">서버 URL</label>
		<div class="col-sm-5">
			<input class="form-control es-input-form" type="text" id="es_url" placeholder="localhost">
		</div>
	</div>
	<div class="form-group form-group-sm">
		<label class="col-sm-3 control-label" for="es_port">http 포트</label>
		<div class="col-sm-3">
			<input class="form-control es-input-form" type="number" id="es_port" value="9200">
		</div>
		<label class="col-sm-2 control-label" for="es_dport">데이터 포트</label>
		<div class="col-sm-3">
			<input class="form-control es-input-form" type="number" id="es_dport" value="9300">
		</div>
	</div>
</form>
	
	
	</div>
</div>

<script>
function newEsInfo(){
	$("#esInfoPanel").show();
	$("#es_id").attr("disabled",false);
	$(".es-input-form").val('');
	$("#es_port").val('9200');
	$("#es_dport").val('9300');
	$("#esInfoTitle").text("엘라스틱서치 정보 - 새로운 서버 등록");
}

function selEsInfo(esNumVal){
	var esNum = Number(esNumVal);
	
	$(".es-list-btn").removeClass("active");
	$(".es-list-tr").removeClass("info");
	$(".es-list-btn").eq(esNum).addClass("active");
	$(".es-list-tr").eq(esNum).addClass("info");
	
	
	$("#esInfoPanel").show();
	$("#es_id").val(esListObj[esNum].es_id);
	$("#es_cluster").val(esListObj[esNum].es_cluster);
	$("#es_url").val(esListObj[esNum].es_url);
	$("#es_port").val(esListObj[esNum].es_port);
	$("#es_dport").val(esListObj[esNum].es_dport);
	$("#es_id").attr("disabled",true);
	$("#esInfoTitle").text("엘라스틱서치 정보 - "+esListObj[esNum].es_id);
}

$(document).ready(function() {
	
	/* 엘라스틱서치 저장 버튼 눌렀을 때 */
	$("#btnSaveEs").click(function(){
		//저장을 위한 ES Object 생성.
		
		if($("#es_id").val() == ""){
			alert("엘라스틱서치 아이디를 입력하세요.");
			$("#es_id").focus();
			return;
		} else {
			var isExist = false;
			for(var el=0; el < esListObj.length; el++){
				if(esListObj[el].es_id == $("#es_id").val()){
					isExist = true;
				}
			}
			if(isExist){
				if($("#es_id").is(':disabled') == false){
					alert("존재하는 엘라스틱서치 아이디 입니다.");
					$("#es_id").focus();
					return;
				}
			}
		}
		if($("#es_cluster").val() == ""){
			alert("엘라스틱서치 클러스터 이름을 입력하세요.");
			$("#es_url").focus();
			return;
		}
		if($("#es_url").val() == ""){
			alert("엘라스틱서치 서버 URL을 입력하세요.");
			$("#es_url").focus();
			return;
		}
		if($("#es_port").val() == ""){
			alert("http 포트를 입력하세요.");
			$("#es_port").focus();
			return;
		}
		if($("#es_dport").val() == ""){
			alert("데이터 포트를 입력하세요.");
			$("#es_dport").focus();
			return;
		}
		
		if($("#es_id").is(':disabled')){
			if(!confirm($("#es_id").val()+"의 엘라스틱서치 정보를 수정하시겠습니까?")){
				return;
			}
		}
		
		$("#btnSaveEs").prop("disabled",true);
		
		esPutObj = new Object();
		esPutObj.es_id = $("#es_id").val();
		esPutObj.es_cluster = $("#es_cluster").val();
		esPutObj.es_url = $("#es_url").val();
		esPutObj.es_port = $("#es_port").val();
		esPutObj.es_dport = $("#es_dport").val();
		
		$.ajax({
			type : "POST",
			data : JSON.stringify(esPutObj),
			url : "/Ajax/Elasticsearch",
			dataType:"json",
			success : function(mapListData) {
				$("#btnSaveEs").prop("disabled",false);
				
				if(mapListData.result ==="OK"){
					geEsList();
				} else {
					console.log("저장 오류.");
				}
			}, error:function(e){   
				console.log(e.responseText);  
			}
		});
		
	});
	
});
</script>