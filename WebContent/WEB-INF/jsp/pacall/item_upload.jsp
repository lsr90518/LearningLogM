<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@include file="inc/define.inc.jsp" %>
<%@include file="inc/taglibs.jsp" %>
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
		<script src="https://maps.googleapis.com/maps/api/js?sensor=true"></script>
		<script src="http://ajax.aspnetcdn.com/ajax/jquery.validate/1.10.0/jquery.validate.min.js"></script>
		<script src="http://ajax.aspnetcdn.com/ajax/jquery.validate/1.10.0/additional-methods.min.js"></script>
		<script type="text/javascript">
		$(function(){
			$(".popinfo").tooltip();
/* 			var map;
		    var mapOptions = {
		      zoom: 8,
		      center: new google.maps.LatLng(-34.397, 150.644),
		      mapTypeId: google.maps.MapTypeId.ROADMAP
		    };
		    map = new google.maps.Map(document.getElementById('map'),
		          mapOptions);
		    
			if(navigator.geolocation) {
				browserSupportFlag = true;
				navigator.geolocation.getCurrentPosition(function(position) {
					initialLocation = new google.maps.LatLng(position.coords.latitude,position.coords.longitude);
					map.setCenter(initialLocation);
				}, function() {
				});
			} */
			$("#uploadItemForm").validate();
			
			setInterval( "activelog()",30000);
		});
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
		<div class="well well-small">
		<div class="container">
			<button class="btn pull-left" onclick="window.close();">Close</button>
		</div>
		</div>
		<div class="container">
			<c:if test="${info!=null}">
				<div class="alert alert-info">
					${info}
				</div>
			</c:if>
			<c:if test="${error!=null}">
				<div class="alert alert-info">
					${error}
				</div>
			</c:if>
			
			<div class="row">
				<div class="span6">
					<div id="mainPhoto" class="thumbnail">
						<img src="<tags:staticpacall filename="${photo.id}_800x600.png" />" />
					</div>
				</div>
				
				<div class="span6">
					<fieldset>
						<legend><h3>${photo.filename}.jpg</h3></legend>
						<a class="btn btn-large btn-primary pull-right" href="#uploadItem" data-toggle="modal">Upload Now!</a>
						<ul>
							<li>Date: <fmt:formatDate value="${photo.photodate}" pattern="yyyy/MM/dd HH:mm:ss"/></li>
							<li>
								<c:if test="${photo.brightness<30}">
									<span class="label popinfo" data-title="Dark"><i class="icon-adjust"></i></span>
								</c:if>
								<c:if test="${photo.reason=='M' || photo.reason=='MAN'}">
									<span class="label label-important popinfo" data-title="Manual"><i class="icon-hand-up"></i></span>
								</c:if>
								<c:if test="${photo.textcontent!=null && photo.textcontent!=''}">
									<span class="label label-info popinfo" data-title="Text:${photo.textcontent}"><i class="icon-text-width"></i></span>
								</c:if>
								<c:if test="${photo.facenum>0}">
									<span class="label label-info popinfo" data-title="Face"><i class="icon-user"></i></span>
								</c:if>
								<c:if test="${photo.featurenum>0}">
									<span class="label label-important popinfo" data-title="Has features"><i class="icon-glass"></i></span>
								</c:if>
								<c:if test="${photo.parentId!=null}">
									<span class="label duplicated popinfo" data-title="Duplicated" data-group="${info.groupId}"><i class="icon-th"></i></span>
								</c:if>
								<c:forEach var="similar" items="${photo.mySimilars}">
									<span class="label label-success popinfo" data-placement="top" data-title="You uploaded a similar log <tags:prettytime date="${similar.item.createTime}" />"><i class="icon-upload"></i></span>
								</c:forEach>
								<c:forEach var="similar" items="${photo.otherSimilars}">
									<span class="btn btn-primary btn-mini tooltip" data-placement="top" data-title="${similar.item.author.nickname} uploaded a similar log <tags:prettytime date="${similar.item.createTime}" />"><i class="icon-upload"></i></span>
								</c:forEach>
							</li>
							<c:if test="${photo.textcontent!=null && photo.textcontent!=''}">
							<li>Text: <h4>${photo.textcontent}</h4></li>
							</c:if>
						</ul>
					</fieldset>
				</div>
			</div>
			
			<div class="row-fluid">
				<fieldset>
					<legend>Similar logs (By you)</legend>
					<ul class="thumbnails">
					<c:forEach items="${photo.mySimilars}" var="similar" varStatus="status">
					<li class="span2">
						<a href="${ctx}/item/${similar.item.id}" target="_blank"  class="thumbnail">
							<img src="<tags:static filename="${similar.item.image}_160x120.png" />" alt="">
							<div class="caption">
								<p><tags:prettytime date="${similar.item.createTime}" /></p>
								<p>
									<h4>
									<c:forEach items="${similar.item.titles}" var="title">
									  ${title.content}
								    </c:forEach>
								    </h4>
								</p>
							</div>
						</a>
					</li>
					</c:forEach>
					</ul>
				</fieldset>
			</div>
			
			<div class="row">
				<fieldset>
					<legend>Similar logs (By others)</legend>
					<ul class="thumbnails">
					<c:forEach items="${photo.otherSimilars}" var="similar" varStatus="status">
					<li class="span2">
						<a href="${ctx}/item/${similar.item.id}" target="_blank"  class="thumbnail">
							<img src="<tags:static filename="${similar.item.image}_160x120.png" />" alt="">
							<div class="caption">
								<p><tags:prettytime date="${similar.item.createTime}" /></p>
								<p>
									<h4>
									<c:forEach items="${similar.item.titles}" var="title">
									  ${title.content}<br/>
								    </c:forEach>
								    </h4>
								</p>
							</div>
						</a>
					</li>
					</c:forEach>
					</ul>
				</fieldset>
			</div>
			
			<div class="row">
				<fieldset>
					<legend>Duplicated Photos</legend>
					<ul class="thumbnails">
					<c:forEach items="${photo.children}" var="duplicate" varStatus="status">
						<li class="span2" <c:if test="${duplicate.lat!=null && duplicate.lng!=null}">data-location="${duplicate.lat}, ${duplicate.lng}"</c:if>>
							<div class="thumbnail">
							<a href="${ctx}/pacall/item_upload/${duplicate.id}" target="_blank">
								<img src="<tags:staticpacall filename="${duplicate.id}_160x120.png" />" alt="">
							</a>
							</div>
						</li>
					</c:forEach>
					</ul>
				</fieldset>
			</div>
		</div>
		
		
<div id="uploadItem" class="modal hide fade" tabindex="-1">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal">×</button>
		<h3 id="myModalLabel">Upload GPS Data File</h3>
	</div>
	<form id="uploadItemForm" class="form-horizontal" action="${ctx}/pacall/item_upload/${photo.id}" method="post">
	<div class="modal-body">
		<input type="hidden" name="photo_id" value="photo.id" />
		<div class="control-group">
			<label class="control-label" for="inputKnow">Did you notice it?</label>
			<div class="controls">
				<select id="inputKnow" class="required" name="know">
					<option value="">Please select</option>
					<option value="1">Yes, I noticed it</option>
					<option value="2">No, I didn't notice it</option>
				</select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="inputKnow">Have you already learned it?</label>
			<div class="controls">
				<select id="inputWhat" class="required" name="what">
					<option value="">Please select</option>
					<option value="1">Yes, I have already known it before.</option>
					<option value="2">No, I just learned it by this system.</option>
				</select>
			</div>
		</div>
	</div>
	<div class="modal-footer">
		<button class="btn" data-dismiss="modal">Close</button>
		<button class="btn btn-primary">Upload</button>
	</div>
	</form>
</div>
	</body>
</html>