<%@page import="edams.comm.CommUserObj"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<!--[if lt IE 7]><html class="no-js lt-ie9 lt-ie8 lt-ie7"><![endif]-->
<!--[if IE 7]><html class="no-js lt-ie9 lt-ie8"><![endif]-->
<!--[if IE 8]><html class="no-js lt-ie9"><![endif]-->
<!--[if gt IE 8]><!-->
<html class="no-js">
<!--<![endif]-->
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="viewport" content="width=device-width">
<title>Kibana 3{{dashboard.current.title ? " - "+dashboard.current.title : ""}}</title>
<link rel="stylesheet" href="css/bootstrap.light.min.css" title="Light">
<link rel="stylesheet" href="css/timepicker.css">
<link rel="stylesheet" href="css/animate.min.css">
<link rel="stylesheet" href="css/normalize.min.css">
<script src="vendor/require/require.js"></script>
<script src="app/components/require.config.js"></script>
<script>
	require([ 'app' ], function() { })
</script>
<style></style>
</head>
<body>
	<noscript>
		<div class="container">
			<center>
				<h3>You must enable javascript to use Kibana</h3>
			</center>
		</div>
	</noscript>
	<link rel="stylesheet" ng-href="css/bootstrap.{{dashboard.current.style||'dark'}}.min.css">
	<link rel="stylesheet" href="css/bootstrap-responsive.min.css">
	<link rel="stylesheet" href="css/font-awesome.min.css">
	<link rel="stylesheet" href="css/edams-kibana.css">
	<div ng-cloak="" ng-repeat="alert in dashAlerts.list" class="alert-{{alert.severity}} dashboard-notice" ng-show="$last">
		<button type="button" class="close" ng-click="dashAlerts.clear(alert)" style="padding-right: 50px">&times;</button>
		<strong>{{alert.title}}</strong> <span ng-bind-html="alert.text"></span>
		<div style="padding-right: 10px" class="pull-right small">{{$index + 1}} alert(s)</div>
	</div>
<%
CommUserObj userObj = (CommUserObj) session.getAttribute("CommUserObj");
if("ADMIN".equals(userObj.getUsrType())){
	//시스템 운영자 일 때만 네비게이션 바 보이도록 추후 수정.
%>
	<div ng-cloak="" class="navbar navbar-static-top">
		<div class="navbar-inner">
			<div class="container-fluid">
				<span class="brand"><img src="img/small.png" bs-tooltip="'Kibana '+(kbnVersion=='@REV@'?'master':kbnVersion)" data-placement="bottom"> {{dashboard.current.title}}</span>
				<ul class="nav pull-right" ng-controller="dashLoader" ng-init="init()" ng-include="'app/partials/dashLoader.html'"></ul>
			</div>
		</div>
	</div>
<% } %>
	<div ng-cloak="" ng-view=""></div>
</body>
</html>