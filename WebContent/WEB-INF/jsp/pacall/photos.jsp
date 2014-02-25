<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
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
<script src="${ctx}/js/jquery/jquery.form.js"></script>
<script src="${ctx}/js/bootstrap/js/bootstrap.min.js"></script>
<script src="https://maps.googleapis.com/maps/api/js?sensor=true"></script>
<script type="text/javascript">
$(function(){
	if($("#tagmenu").height()>300){
		$("#map").css("height", $("#tagmenu").height());
	}
	var map;
    var mapOptions = {
      zoom: 8,
      center: new google.maps.LatLng(-34.397, 150.644),
      mapTypeId: google.maps.MapTypeId.ROADMAP,
      disableDefaultUI: true,
      zoomControl: true,
      scaleControl: true,
      zoomControlOptions: {
    	    style: google.maps.ZoomControlStyle.SMALL,
	        position: google.maps.ControlPosition.TOP_RIGHT
	  },
	  overviewMapControl: true
    };
    map = new google.maps.Map(document.getElementById('map'),
          mapOptions);
    var bounds = new google.maps.LatLngBounds();
    var marker;
    
    var locations = ${locations};
    for(var i in locations){
    	marker = new google.maps.Marker({
    		position: new google.maps.LatLng(locations[i].lat, locations[i].lng),
    		map: map,
    		title:''
    	});
    	bounds.extend(marker.getPosition());
    }
    map.fitBounds(bounds);
    
	if(navigator.geolocation) {
		browserSupportFlag = true;
		navigator.geolocation.getCurrentPosition(function(position) {
			initialLocation = new google.maps.LatLng(position.coords.latitude,position.coords.longitude);
			map.setCenter(initialLocation);
		}, function() {
		});
	}
	
	$(".duplicated").hover(function(){
		var group = $(this).attr("data-group");
		$(".duplicated[data-group=\""+group+"\"]").parentsUntil($("li")).css("background-color", "yellow");
		console.log(group);
	}, function(){
		var group = $(this).attr("data-group");
		$(".duplicated[data-group=\""+group+"\"]").parentsUntil($("li")).css("background-color", "white");
	});
	
	$(".popinfo").tooltip();
	
	checkStatus();
	setInterval( "checkStatus()",5000);
	
	$("#uploadSensorForm").ajaxForm({
		success: function(e){
			location.reload();
		}
	});
	$("#uploadGpsForm").ajaxForm({
		success: function(e){
			location.reload();
		}
	});
	
	setInterval( "activelog()",30000);
});

function activelog(){
	$.get("${ctx}/pacalllog/active", function(e){
		console.log("updated:"+e);
	});
}

var status = -1;
function checkStatus(){
	$.get("${ctx}/pacall/collection/${collectionId}/status", function(e){
		if(e.length>0){
			console.log(e);
			console.log(e[0]);
			console.log(e[1]);
			$("#btnAnalyzeData").attr("disabled", "disabled").text("Processing("+(e[0]+1)+" of 2)..."+e[1]+"%");
			status = e;
		}else{
			$("#btnAnalyzeData").removeAttr("disabled").text("2.Analyze data");
			if(status==[]){
				location.reload();
			}
		}
	}, "json");
}

function analyzeData(){
	$("#btnAnalyzeData").attr("disabled", "disabled");
	$.get("${ctx}/pacall/process/${collectionId}", function(){});
}

function resetFolderInput(){
	$("#photofolder").remove();
	$("<input type=\"file\" id=\"photofolder\" webkitdirectory directory multiple />").prependTo($("#selectFolder"));
	$("#uploadStatusWrapper").hide();
	$("#uploadStatus").css("width", "0%");
}

var files=[];
var uploadURL = "${ctx}/pacall/collection/${collectionId}/photos";
$(function(){	
	resetFolderInput();
	
	$(document).on("change","#photofolder", function(e){
		files = [];
		var fileList = e.target.files;
		for(var i=0;i<fileList.length;i++){
			if(fileList[i].type.indexOf("image")!=-1 || fileList[i].name=='SENSOR.CSV'){
				files.push(fileList[i]);
			}
		}
		resetFolderInput();
		if(files.length>0){
			$("#startUpload").removeAttr("disabled");
			$("#infoArea").show().text(files.length+ " files are selected");
		}else{
			$("#startUpload").attr("disabled", "disabled");
			$("#infoArea").hide().text("");
		}
	});
});

function startUpload(){
	if(files.length<1){
		return;
	}
	var successCount = 0, errorCount = 0, abortCount = 0;
	$("#selectFolder").attr("disabled", "disabled");
	$("#startUpload").attr("disabled", "disabled");
	$("#photofolder").attr("disabled", "disabled");
	$("#uploadPhotos button, #uploadPhotos .btn").attr("disabled", "disabled");
	
	function checkFinished(){
		var total = successCount+errorCount+abortCount;
		$("#uploadStatus").css("width", total/files.length*100+"%");
		if(total == files.length){
			$("#infoArea").text("Upload finished");
			$("#uploadStatusWrapper").removeClass("progress-striped").removeClass("active");
			$("#selectFolder").removeAttr("disabled");
			$("#photofolder").removeAttr("disabled");
			$("#uploadPhotos button, #uploadPhotos .btn").removeAttr("disabled");
			$.get("${ctx}/pacalllog/uploadfinished", {collectionId:'${collectionId}'}, function(e){
				console.log("end logged"+e);
			});
			setTimeout('location.reload()',3000);
		}else{
			$("#uploadStatusWrapper").addClass("progress-striped").addClass("active");
		}
	}
	
	$("#uploadStatusWrapper").show();
	
	$.get("${ctx}/pacalllog/uploadstarted", {collectionId:'${collectionId}'}, function(e){
		console.log("start logged"+e);
	});
	for(var i=0;i<files.length;i++){
		var xhr = new XMLHttpRequest();
		xhr.upload.file = files[i];
		xhr.upload.addEventListener("progress", function(e){
			//File progress
		}, false);
		xhr.upload.addEventListener("load", function(e){
			//Success
			successCount++;
			$("#infoArea").text(successCount+"/"+files.length+" files are uploaded.");
			checkFinished();
		}, false)
		xhr.upload.addEventListener("error", function(e){
			errorCount++;
			checkFinished();
		}, false);
		xhr.upload.addEventListener("abort", function(e){
			abortCount++;
			checkFinished();
		}, false);
		xhr.open("post", uploadURL, true);
		var formData = new FormData();
		formData.append('file', files[i]);
		xhr.send(formData);
	}
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
			<button class="btn pull-left" onclick="location.href='${ctx}/pacall'">Return</button>
			<span class="pull-right">
				<a class="btn btn-primary" href="#uploadPhotos" data-toggle="modal">1.Upload Photos</a>
				<!-- <a class="btn btn-info" href="#uploadSensorData" data-toggle="modal">Upload Sensor Data</a>
				<a class="btn btn-success" href="#uploadGps" data-toggle="modal">Upload GPS Data</a> -->
				<button class="btn btn-danger" onclick="analyzeData()" id="btnAnalyzeData">2.Analyze data</button>
				<button class="btn btn-info" onclick="location.reload()" id="btnRefresh">3.Refresh</button>
			</span>
		</div>
	</div>
	<div class="container">
		<%-- <div class="row well">
			Information:<br />
			<ul>
				<li>Sensor file: <c:forEach items="${sensorFiles}" var="sensor"><a href="<tags:staticpacall filename="${sensor.id}.csv" />"><i class="icon-download-alt"></i></a></c:forEach></li>
				<li>Gps file: <c:forEach items="${gpsFils}" var="sensor"><tags:staticpacall filename="${sensor.id}.csv" /></c:forEach></li>
			</ul>
		</div> --%>
		<div class="row">
			<div id="tagmenu" class="span3">
				<ul class="nav nav-list well" style="text-align: left">
					<li class="nav-header"><a href="?tag=all"><i class="icon-chevron-right"></i>All(${tagnum['all']})</a></li>
					<li class="nav-header"><a href="?tag=manual"><i class="icon-chevron-right"></i>Manual(${tagnum['manual']})</a></li>
					<li class="nav-header"><a href="?tag=recommended"><i class="icon-chevron-right"></i>Recommended(${tagnum['recommended']})</a></li>
					<li class="nav-header"><a href="?tag=ullolike"><i class="icon-chevron-right"></i>ULLO-Like(${tagnum['ullolike']})</a></li>
					<li class="nav-header"><a href="?tag=face"><i class="icon-chevron-right"></i>Face(${tagnum['face']})</a></li>
					<li class="nav-header"><a href="?tag=text"><i class="icon-chevron-right"></i>Text(${tagnum['text']})</a></li>
					<li class="nav-header"><a href="?tag=feature"><i class="icon-chevron-right"></i>Feature(${tagnum['feature']})</a></li>
					<li class="nav-header"><a href="?tag=normal"><i class="icon-chevron-right"></i>Others(${tagnum['normal']})</a></li>
					<li class="nav-header"><a href="?tag=duplicated"><i class="icon-chevron-right"></i>Duplicated(${tagnum['duplicated']})</a></li>
					<li class="nav-header"><a href="?tag=dark"><i class="icon-chevron-right"></i>Dark(${tagnum['dark']})</a></li>
				</ul>
			</div>
			<div id="map" class="span9" style="height:300px;">
			</div>
			<div class="span12">
				<div class="row pagination">
					<ul>
					<c:if test="${!result.firstPage}">
						<li><a href="?tag=${tag}&page=${result.number}${pageParam}">Prev</a></li>
					</c:if>
					<c:forEach begin="1" end="${result.totalPages}" var="pageNumber">
						<li <c:if test="${pageNumber==result.number+1}">class="disabled"</c:if>><a href="?tag=${tag}&page=${pageNumber}${pageParam}">${pageNumber}</a></li>
					</c:forEach>
					<c:if test="${!result.lastPage}">
						<li><a href="?tag=${tag}&page=${result.number+2}${pageParam}">Next</a></li>
					</c:if>
					</ul>
				</div>
				
				
				<c:forEach items="${result.content}" var="photo" varStatus="status">
				<c:if test="${status.index%6==0}"><div class="row"></c:if>
					<div class="span2" <c:if test="${photo.lat!=null && photo.lng!=null}">data-location="${photo.lat}, ${photo.lng}"</c:if>>
						<div>
						<a href="${ctx}/pacall/item_upload/${photo.id}" target="_blank" class="thumbnail">
							<img src="<tags:staticpacall filename="${photo.id}_160x120.png" />" alt="">
						</a>
							<span class="label popinfo" data-title="<fmt:formatDate value="${photo.photodate}" pattern="yyyy/MM/dd HH:mm:ss"/>"><fmt:formatDate value="${photo.photodate}" pattern="HH:mm"/></span>
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
								<span class="label label-success popinfo" data-placement="top" data-title="${similar.item.author.nickname} uploaded a similar log <tags:prettytime date="${similar.item.createTime}" />"><i class="icon-upload"></i></span>
							</c:forEach>
						</div>
					</div>
				<c:if test="${(status.index+1)%6==0}"></div></c:if>
				</c:forEach>
				</div>
				<div class="row pagination">
					<ul>
					<c:if test="${!result.firstPage}">
						<li><a href="?tag=${tag}&page=${result.number}${pageParam}">Prev</a></li>
					</c:if>
					<c:forEach begin="1" end="${result.totalPages}" var="pageNumber">
						<li <c:if test="${pageNumber==result.number+1}">class="disabled"</c:if>><a href="?tag=${tag}&page=${pageNumber}${pageParam}">${pageNumber}</a></li>
					</c:forEach>
					<c:if test="${!result.lastPage}">
						<li><a href="?tag=${tag}&page=${result.number+2}${pageParam}">Next</a></li>
					</c:if>
					</ul>
				</div>
			</div>
		</div>
	</div>
<form style="display:none">
	<input type="hidden" id="collectionId" name="collectionId" value="${collectionId}" />
</form>
	
<!-- Modal -->
<div id="uploadPhotos" class="modal hide fade" tabindex="-1">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal">×</button>
		<h3 id="myModalLabel">Upload Photos</h3>
	</div>
	<div class="modal-body">
		<div id="infoArea" class="alert alert-info" style="display:none"></div>
		<button id="selectFolder" class="btn btn-large btn-primary fileinput-button span3 offset1">
			<i class="icon-plus icon-white"></i>
			<span>Select photo folder</span>
		</button>
		<div id="uploadStatusWrapper" class="progress" style="width:100%;display:none">
			<div id="uploadStatus" class="bar" style="width: 0%;"></div>
		</div>
	</div>
	<div class="modal-footer">
		<button class="btn" data-dismiss="modal">Close</button>
		<button class="btn btn-primary" onclick="startUpload(0)">Upload</button>
	</div>
</div>

<div id="uploadSensorData" class="modal hide fade" tabindex="-1">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal">×</button>
		<h3 id="myModalLabel">Upload Sensor Data File</h3>
	</div>
	<form id="uploadSensorForm" action="${ctx}/pacall/collection/${collectionId}/sensor" method="post" enctype="multipart/form-data">
	<div class="modal-body">
		<c:if test="${not empty sensorFiles}">
		<div>
			<h5>Uploaded sensor data files</h5>
			<ul>
				<c:forEach items="${sensorFiles}" var="sensor" varStatus="status">
					<li><a href="${sensor.id}">File ${status.index+1}</a></li>
				</c:forEach>
			</ul>
		</div>
		</c:if>
		<div class="control-group">
			<label class="control-label" for="file">Please select SENSOR.CSV</label>
			<div class="controls">
				<input type="file" name="file"/>
			</div>
		</div>
	</div>
	<div class="modal-footer">
		<button class="btn" data-dismiss="modal">Close</button>
		<button class="btn btn-primary">Upload</button>
	</div>
	</form>
</div>

<div id="uploadGps" class="modal hide fade" tabindex="-1">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal">×</button>
		<h3 id="myModalLabel">Upload GPS Data File</h3>
	</div>
	<form id="uploadGpsForm" action="${ctx}/pacall/collection/${collectionId}/gps" method="post" enctype="multipart/form-data">
	<div class="modal-body">
		<c:if test="${not empty gpsFiles}">
		<div>
			<h5>Uploaded GPS data files</h5>
			<ul>
				<c:forEach items="${gpsFiles}" var="gps" varStatus="status">
					<li><a href="${ctx}/pacall/gps/${gps.id}">File ${status.index+1}</a></li>
				</c:forEach>
			</ul>
		</div>
		</c:if>
		<div class="control-group">
			<label class="control-label" for="file">Please select gps data file</label>
			<div class="controls">
				<input type="file" name="file"/>
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