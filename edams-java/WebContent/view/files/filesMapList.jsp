<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div class="panel panel-success" id="mapListPanel">
	<div class="panel-heading">
		<h3 class="panel-title" id="mapListTitle">매핑 목록</h3>
	</div>
	<div class="panel-body">
	
<table class="table">
	<thead>
		<tr>
			<th class="text-center" width="100px">매핑 아이디</th>
			<th class="text-center">추출 파일</th>
			<th class="text-center" width="80px"></th>
		</tr>
	</thead>
	<tbody id="tbMapList"></tbody>
</table>
	
	</div>
</div>

<script>

$(document).ready(function() {
	getMapList();
});

function getMapList(){
	$.ajax({
		type : "GET",
		url : "/Ajax/Mapping",
		dataType:"json",
		success : function(mapList) {
			$("#tbMapList").html("");
			var tblStr = '';
			mapListObj = mapList.map_list;
			
			for(var tr=0; tr< mapListObj.length; tr++){
				tblStr += '<tr class="map-list-tr">';
				tblStr += '	<td class="text-center">'+mapListObj[tr].map_id+'</td>';
				tblStr += '	<td>'+mapListObj[tr].src_file+'</td>';
				tblStr += '	<td class="text-right">';
				tblStr += '		<button class="btn btn-sm btn-danger" type="button" onclick="delMapInfo('+tr+')">';
				tblStr += '			<i class="glyphicon glyphicon-trash"></i>';
				tblStr += '		</button>';
				tblStr += '		<button class="btn btn-sm btn-info map-list-btn" onclick="selMapinfo('+tr+');">';
				tblStr += '			<i class="glyphicon glyphicon-search"></i>';
				tblStr += '		</button>';
				tblStr += '	</td>';
			}
			$("#tbMapList").append(tblStr);
			
		}, error:function(e){   
			console.log(e.responseText);  
		}
	});
}

/**
 * 매핑 정보 삭제.
 */
function delMapInfo(numVal){
	var num = Number(numVal);
	if(confirm(mapListObj[num].map_id+" 매핑을 삭제하시겠습니까?")){
		
		mapPutObj = new Object();
		mapPutObj.map_id = mapListObj[num].map_id;
		
		$.ajax({
			type : "DELETE",
			data : JSON.stringify(mapPutObj),
			url : "/Ajax/Mapping",
			dataType:"json",
			success : function(mapListData) {
				if(mapListData.result ==="OK"){
					getMapList();
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