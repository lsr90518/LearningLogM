<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
<script src="${ctx}/js/bootstrap/js/bootstrap.min.js"></script>
<script src="${ctx}/js/jquery/jquery.dateFormat-1.0.js"></script>
<script type="text/javascript">
	$(function(){
		$('#createForm').on('shown', function () {
			$("#collectionName").val($.format.date(new Date().getTime(), 'yyyy/MM/dd HH:mm:ss')).select().focus();
		});
		setInterval( "activelog()",30000);
	});
	function saveCollection(){
		if($.trim($("#collectionName").val())==""){
			$("#collectionName").focus();
		}else{
			$.post("${ctx}/pacall/collection",{
				name: $("#collectionName").val()
			}, function(){
				$('#createForm').modal('hide');
				location.reload();
			});
		}
	}
	function deleteCollection(collectionId){
		if(!confirm("Are you sure?")) return false;
		$.post("${ctx}/pacall/collection/"+collectionId, {
			_method:"DELETE"
		}, function(){
			location.reload();
		});
	}
	function activelog(){
		$.get("${ctx}/pacalllog/active", function(e){
			console.log("updated:"+e);
		});
	}
</script>
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
			<a class="btn btn-large btn-danger pull-right"
				href="#createForm" data-toggle="modal">Create a new collection</a>
		</div>
		<table class="table table-striped table-hover">
			<thead>
				<tr>
					<th>Name</th>
					<th>Picture Number</th>
					<th>Period</th>
					<th>Date of creation</th>
					<th>Action</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${result.content}" var="collection" varStatus="status">
					<tr>
						<td><a href="${ctx}/pacall/collection/${collection.id}/photos">${collection.name}</a></td>
						<td>${collection.total}</td>
						<td><fmt:formatDate value="${collection.startDate}" pattern="yyyy/MM/dd" />~<fmt:formatDate value="${collection.endDate}" pattern="yyyy/MM/dd" /></td>
						<td><fmt:formatDate value="${collection.createTime}" pattern="yyyy/MM/dd HH:mm:ss"/> </td>
						<td>
							<c:if test="${collection.total==0}">
								<button class="btn" onclick="return deleteCollection('${collection.id}')">Delete</button>
							</c:if>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
 
<!-- Modal -->
<div id="createForm" class="modal hide fade" tabindex="-1">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal">×</button>
    <h3 id="myModalLabel">Create a new collection</h3>
  </div>
  <div class="modal-body">
    <p>
    	<input class="span5" type="text" id="collectionName" placeholder="Please input collection name here"/>
    </p>
  </div>
  <div class="modal-footer">
    <button class="btn" data-dismiss="modal">Close</button>
    <button class="btn btn-primary" onclick="saveCollection()">Save changes</button>
  </div>
</div>
</body>
</html>