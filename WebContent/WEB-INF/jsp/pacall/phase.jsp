<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:import url="inc/define.inc.jsp" />
<!doctype html>
<html>
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>PACALL: Passive Capture for Learning Log</title>
<link rel="stylesheet" href="${ctx}/css/jquery.fileupload-ui.css" />
<link rel="stylesheet" href="${ctx}/js/bootstrap/css/bootstrap.min.css" />
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script>
<script type="${ctx}/js/bootstrap/js/bootstrap.min.js"></script>
</head>
<body style="position: relative; padding-top: 40px;">
	<div class="navbar navbar-inverse navbar-fixed-top">
		<div class="navbar-inner">
			<div class="container">
				<button type="button" class="btn btn-navbar" data-toggle="collapse"
					data-target=".nav-collapse">
					<span class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="brand" href="${ctx}/pacall">PACALL</a>
				<div class="nav-collapse collapse">
					<ul class="nav">
						<li class=""><a href="${ctx}/pacall">Home</a></li>
					</ul>
				</div>
			</div>
		</div>
	</div>
	<header class="" id="overview">
		<div class="container">
			<h1>PACALL</h1>
			<p class="lead">Passive Capture for Learning Log</p>
		</div>
	</header>
	<div class="container">
		<div class="navigator">
			<button class="btn btn-primary"
				onclick="location.href='${ctx}/pacall/upload_photos'">Upload
				Photos</button>
			<button class="btn btn-info"
				onclick="location.href='${ctx}/pacall/upload_sensor'">Upload
				Sensor Data</button>
			<button class="btn btn-success"
				onclick="location.href='${ctx}/pacall/upload_gps'">Upload
				GPS Data</button>
			<button class="btn btn-large btn-danger pull-right"
				onclick="location.href='${ctx}/pacall/process/process_sensor'">Refresh List</button>
		</div>
		<table class="table table-striped table-hover">
			<thead>
				<tr>
					<th>Date</th>
					<th>Picture Number</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${result.content}" var="phase" varStatus="status">
					<tr>
						<td><a
							href="${ctx}/pacall/photos/<fmt:formatDate value="${phase.pdate}" pattern="yyyyMMdd"/>"><fmt:formatDate
									value="${phase.pdate}" pattern="yyyy/MM/dd" /></a></td>
						<td>${phase.total}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</body>
</html>