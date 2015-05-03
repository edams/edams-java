<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div class="panel panel-success" id="esListPanel">
	<div class="panel-heading">
		<h3 class="panel-title" id="esListTitle">엘라스틱서치 서버 목록</h3>
	</div>
	<div class="panel-body">
	
<table class="table">
	<thead>
		<tr>
			<th class="text-center" width="100px">아이디</th>
			<th class="text-center" width="80px">클러스터</th>
			<th class="text-center">URL</th>
			<th class="text-right" width="80px">
				<button class="btn btn-sm btn-success" type="button" onclick="newEsInfo();">
					<i class="glyphicon glyphicon-plus"></i>
				</button>
			</th>
		</tr>
	</thead>
	<tbody id="tbEsList"></tbody>
</table>
	
	</div>
</div>

<script>

$(document).ready(function() {
	geEsList();
});

function geEsList(){
	$.ajax({
		type : "GET",
		url : "/Ajax/Elasticsearch",
		dataType:"json",
		success : function(es_obj) {
			$("#tbEsList").html("");
			var tblStr = '';
			esListObj = es_obj.es_list;
			
			for(var tr=0; tr< esListObj.length; tr++){
				tblStr += '<tr class="es-list-tr">';
				tblStr += '	<td class="text-center">'+esListObj[tr].es_id+'</td>';
				tblStr += '	<td class="text-center">'+esListObj[tr].es_cluster+'</td>';
				tblStr += '	<td>'+esListObj[tr].es_url+'</td>';
				tblStr += '	<td class="text-right">';
				tblStr += '		<button class="btn btn-sm btn-danger" type="button" onclick="delEsInfo('+tr+')">';
				tblStr += '			<i class="glyphicon glyphicon-trash"></i>';
				tblStr += '		</button>';
				tblStr += '		<button class="btn btn-sm btn-info es-list-btn" onclick="selEsInfo('+tr+');">';
				tblStr += '			<i class="glyphicon glyphicon-search"></i>';
				tblStr += '		</button>';
				tblStr += '	</td>';
			}
			$("#tbEsList").append(tblStr);
			
		}, error:function(e){   
			console.log(e.responseText);  
		}
	});
}

/**
 * 매핑 정보 삭제.
 */
function delEsInfo(numVal){
	var num = Number(numVal);
	if(confirm(esListObj[num].es_id+" 연결을 삭제하시겠습니까?")){
		
		esPutObj = new Object();
		esPutObj.es_id = esListObj[num].es_id;
		
		$.ajax({
			type : "DELETE",
			data : JSON.stringify(esPutObj),
			url : "/Ajax/Elasticsearch",
			dataType:"json",
			success : function(es_obj) {
				if(es_obj.result ==="OK"){
					geEsList();
				} else {
					console.log("삭제 오류.");
				}
			}, error:function(e){   
				console.log(e.responseText);  
			}
		});
	}
}

</script>