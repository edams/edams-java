<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="panel panel-default" id="mapListPanel">
	<div class="panel-heading">
		<h3 class="panel-title" id="mapListTitle">업로드 파일 목록</h3>
	</div>
	<div class="panel-body">
		<div class="col-xs-12">

<p class="text-muted">
CSV, JSON 형식만 가능하며 확장자는 각각 .csv .json 이어야 합니다.<br>
파일 내용은 한 줄에 한 건씩, 새로운 건은 줄바꿈 되어야 합니다.
</p>

	<!-- The file upload form used as target for the file upload widget -->
	<form id="fileupload" action="//jquery-file-upload.appspot.com/" method="POST" enctype="multipart/form-data">
		<!-- Redirect browsers with JavaScript disabled to the origin page -->
		<noscript><input type="hidden" name="redirect" value="http://blueimp.github.io/jQuery-File-Upload/"></noscript>
		<!-- The fileupload-buttonbar contains buttons to add/delete files and start/cancel the upload -->
		<div class="fileupload-buttonbar">
			<!-- The global progress state -->
			<div class="row fileupload-progress fade">
				<span class="fileupload-process"></span>
				<!-- The global progress bar -->
				<div class="progress progress-sm progress-striped active" role="progressbar" aria-valuemin="0" aria-valuemax="100">
					<div class="progress-bar progress-bar-success" style="width:0%;"></div>
				</div>
				<!-- The extended global progress state -->
				<div class="progress-extended">&nbsp;</div>
			</div>
			<div class="row">
				<!-- The fileinput-button span is used to style the file input field as button -->
				<input type="checkbox" class="toggle">
				<span class="btn btn-success btn-sm fileinput-button">
					<i class="glyphicon glyphicon-plus"></i>
					<span>파일 추가</span>
					<input type="file" name="files[]" multiple>
				</span>
				<button type="submit" class="btn btn-primary btn-sm start">
					<i class="glyphicon glyphicon-upload"></i>
					<span>업로드 시작</span>
				</button>
				<button type="reset" class="btn btn-warning btn-sm cancel">
					<i class="glyphicon glyphicon-ban-circle"></i>
					<span>업로드 취소</span>
				</button>
				
				<button type="button" class="btn btn-danger btn-sm" onclick="if(confirm('선택한 파일들을 삭제하시겠습니까?')){$('#del-btn-all').click();}">
					<i class="glyphicon glyphicon-trash"></i>
					<span>삭제</span>
				</button>
				
				<button style="display:none;" id="del-btn-all" type="button" class="btn btn-danger btn-sm delete">
					<i class="glyphicon glyphicon-trash"></i>
					<span>삭제</span>
				</button>
				<!-- The global file processing state -->
			</div>
		</div>
		<!-- The table listing the files available for upload/download -->
		<div class="row" data-toggle="buttons">
			<table role="presentation" class="table"><tbody class="files"></tbody></table>
		</div>
	</form>
	
		</div>
	</div>
</div>
	
<!-- The blueimp Gallery widget -->
<div id="blueimp-gallery" class="blueimp-gallery blueimp-gallery-controls" data-filter=":even">
	<div class="slides"></div>
	<h3 class="title"></h3>
	<a class="prev">‹</a>
	<a class="next">›</a>
	<a class="close">×</a>
	<a class="play-pause"></a>
	<ol class="indicator"></ol>
</div>
<!-- The template to display files available for upload -->
<script id="template-upload" type="text/x-tmpl">
{% for (var i=0, file; file=o.files[i]; i++) { %}
	<tr class="template-upload fade">
		<td>
			<span class="name">{%=file.name%}</span>
			<strong class="error text-danger"></strong>
			<div class="progress progress-sm progress-striped active" role="progressbar" aria-valuemin="0" aria-valuemax="100" aria-valuenow="0">
				<div class="progress-bar progress-bar-success" style="width:0%;"></div>
			</div>
		</td>
		<td width="80px" class="text-right">
			<span class="size">Processing...</span>
		</td>
		<td width="80px" class="text-right">
			<span>
			{% if (!i && !o.options.autoUpload) { %}
				<button class="btn btn-primary btn-sm start" disabled>
					<i class="glyphicon glyphicon-upload"></i>
				</button>
			{% } %}
			</span>
			<span>
			{% if (!i) { %}
				<button class="btn btn-warning btn-sm cancel">
					<i class="glyphicon glyphicon-ban-circle"></i>
				</button>
			{% } %}
			</span>
		</td>
	</tr>
{% } %}
</script>
<!-- The template to display files available for download -->
<script id="template-download" type="text/x-tmpl">
{% for (var i=0, file; file=o.files[i]; i++) { %}
	<tr class="template-download fade file-list-tr file-list-tr-{%=file.id%}">
		<td>
			<input type="checkbox" name="delete" value="1" class="toggle">
			{% if (file.type==='CSV') { %}
					<span class="label label-success">CSV</span>
			{% } else if (file.type==='JSON'){ %}
					<span class="label label-warning">JSON</span>
			{% } %}
			<span class="name">
				{% if (file.url) { %}
					<a href="{%=file.url%}" title="{%=file.name%}" download="{%=file.name%}" {%=file.thumbnailUrl?'data-gallery':''%}>{%=file.name%}</a>
				{% } else { %}
					<span>{%=file.name%}</span>
				{% } %}
			</span>
			{% if (file.error) { %}
				<div><span class="label label-danger">Error</span> {%=file.error%}</div>
			{% } %}
		</td>
		<td width="80px" class="text-right">
			<span class="size">{%=o.formatFileSize(file.size)%}</span>
		</td>
		<td width="80px" class="text-right">
			<span>
			{% if (file.deleteUrl) { %}
				<button type="button" class="btn btn-danger btn-sm" onclick="if(confirm('{%=file.name%} 파일을 삭제하시겠습니까?')){$('#del-btn-{%=file.id%}').click();}">
					<i class="glyphicon glyphicon-trash"></i>
				</button>
				<button style="display:none;" id="del-btn-{%=file.id%}" class="btn btn-danger btn-sm delete" data-type="{%=file.deleteType%}" data-url="{%=file.deleteUrl%}"{% if (file.deleteWithCredentials) { %} data-xhr-fields='{"withCredentials":true}'{% } %}>
				</button>
			{% } else { %}
				<button class="btn btn-warning btn-sm cancel">
					<i class="glyphicon glyphicon-ban-circle"></i>
				</button>
			{% } %}
			</span>
			<span>
			{% if (file.deleteUrl) { %}
			<button class="btn btn-sm btn-info file-list-btn file-list-btn-{%=file.id%}" onclick="selFileInfo('{%=file.id%}','{%=file.name%}','{%=file.type%}');">
				<i class="glyphicon glyphicon-search"></i>
			</button>
			{% } %}
			</span>
		</td>
	</tr>
{% } %}
</script>
<script src="/resource/fileuploader/blueimp/jquery.min.js"></script>
<!-- The jQuery UI widget factory, can be omitted if jQuery UI is already included -->
<script src="/resource/fileuploader/js/vendor/jquery.ui.widget.js"></script>
<!-- The Templates plugin is included to render the upload/download listings -->
<script src="/resource/fileuploader/blueimp/tmpl.min.js"></script>
<!-- The Load Image plugin is included for the preview images and image resizing functionality -->
<script src="/resource/fileuploader/blueimp/load-image.all.min.js"></script>
<!-- The Canvas to Blob plugin is included for image resizing functionality -->
<script src="/resource/fileuploader/blueimp/canvas-to-blob.min.js"></script>
<!-- Bootstrap JS is not required, but included for the responsive demo navigation -->
<script src="/resource/fileuploader/blueimp/bootstrap.min.js"></script>
<!-- blueimp Gallery script -->
<script src="/resource/fileuploader/blueimp/jquery.blueimp-gallery.min.js"></script>
<!-- The Iframe Transport is required for browsers without support for XHR file uploads -->
<script src="/resource/fileuploader/js/jquery.iframe-transport.js"></script>
<!-- The basic File Upload plugin -->
<script src="/resource/fileuploader/js/jquery.fileupload.js"></script>
<!-- The File Upload processing plugin -->
<script src="/resource/fileuploader/js/jquery.fileupload-process.js"></script>
<!-- The File Upload image preview & resize plugin -->
<script src="/resource/fileuploader/js/jquery.fileupload-image.js"></script>
<!-- The File Upload audio preview plugin -->
<script src="/resource/fileuploader/js/jquery.fileupload-audio.js"></script>
<!-- The File Upload video preview plugin -->
<script src="/resource/fileuploader/js/jquery.fileupload-video.js"></script>
<!-- The File Upload validation plugin -->
<script src="/resource/fileuploader/js/jquery.fileupload-validate.js"></script>
<!-- The File Upload user interface plugin -->
<script src="/resource/fileuploader/js/jquery.fileupload-ui.js"></script>
<!-- The main application script -->
<script src="/resource/fileuploader/js/main.js"></script>