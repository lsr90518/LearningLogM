<%@page contentType="text/html" pageEncoding="UTF-8"
	trimDirectiveWhitespaces="true"%>
<%@include file="../include/taglibs.jsp"%>
<!doctype html>
<html>
<c:import url="../include/head.jsp">
	<c:param name="title" value="Edit object" />
	<c:param name="content">
		<style>
.titleLangName {
	width: 70px;
}

.titleMap {
	width: 60%;
}

#titleTable button {
	width: 30%;
}
</style>
		<link rel="stylesheet" type="text/css" media="screen"
			href="<c:url value='/js/mediaelement/mediaelementplayer.min.css' />" />
		<script
			src="<c:url value='/js/mediaelement/mediaelement-and-player.min.js' />"></script>
		<script src="http://www.google.com/jsapi"></script>
		<script src="http://maps.google.com/maps/api/js?sensor=true"></script>
		<script src="<c:url value='/js/LLMap.js' />"></script>
		<script>
                $(function(){
                    $("video, audio").mediaelementplayer();
                });
            </script>
		<script>
                function translateTitle(code){
                    var translateTitleUri = "<c:url value='/api/translate/itemTitle' />";
                    var titles = "{";
                    $.each($(".titleMap"),function(i, item){
                        titles+=$(item).attr("lang")+":\""+$(item).val()+"\"";
                        if(i<$(".titleMap").length-1)titles+=",";
                    });
                    titles+="}";
                    var inputdata={ 'target':code, 'titles': titles};
                    $.get(translateTitleUri,inputdata ,function(data){
                        $("#inputTitle_"+code).val(data);
                    });
                }
                
                function addLangTitle(){
                    var code = $("#addLangSelect").val();
                    var name = $("#addLangSelect").find("option:selected").text();
                    $("<tr><td class=\"titleLangName\">"+name+"</td><td><input name=\"titleMap['"+code+"']\" id=\"inputTitle_"+code+"\" class=\"titleMap\" lang=\""+code+"\" />&nbsp;<button onclick=\"translateTitle('"+code+"');return false;\">Translate</button></td></tr>").appendTo($("#titleTable"));
                    $("#addLangSelect option[value='"+code+"']").remove();
                }
                
                var map;
                $(function(){
                	<c:choose>
                	<c:when test="${(empty tasksingleitem.lng) || (empty tasksingleitem.lat) || (empty tasksingleitem.zoom)}">
                    map = new LLMap("map", {
                        onchange:function(lat, lng, zoom){
                            $("#itemLat").val(lat);
                            $("#itemLng").val(lng);
                            $("#itemZoom").val(zoom);
                        }
                    });
                    </c:when>
                    <c:otherwise>
                    map = new LLMap("map", {
                    	lat: ${tasksingleitem.lat},
                    	lng: ${tasksingleitem.lng},
                    	zoom: ${tasksingleitem.zoom},
                        onchange:function(lat, lng, zoom){
                            $("#itemLat").val(lat);
                            $("#itemLng").val(lng);
                            $("#itemZoom").val(zoom);
                        }
                    });
                    </c:otherwise>
                    </c:choose>
                    
                    $("#generateQrcode").click(function(){
                        if($("#qrcode").val()!=""){
                            if(!confirm("Generate a new QR code?")){
                                return;
                            }
                        }
                        $.get("<c:url value="/api/qrcode/generate" />", function(data){
                            $("#qrcode").val(data);
                            $("#qrcodeArea").html("<img src=\"http://chart.apis.google.com/chart?cht=qr&chs=120x120&chl=learninglog://item?qrcode="+data+"\"/>");
                        });
                        return false;
                    });
                    $("#clearQrcode").click(function(){
                        $("#qrcode").val("");
                        $("#qrcodeArea").html("");
                       });
                    $("#printQrcode").click(function(){
                        $.get("<c:url value="/api/qrcode/generate" />", function(data){
                            $("#qrcode").val(data);
                            $("#qrcodeArea").html("<img src=\"http://chart.apis.google.com/chart?cht=qr&chs=120x120&chl=learninglog://item?qrcode="+data+"\"/>");
                            window.open("<c:url value='/qrcodeprint?content=' />"+$("#qrcode").val(),"", "height=170, width=170" );
                        });
                    });
                    $("#qrcode").change(function(){
                        if($.trim($("#qrcode").val())==""){
                            $("#qrcodeArea").html("");
                            return;
                        }
                        $("#qrcodeArea").html("<img src=\"http://chart.apis.google.com/chart?cht=qr&chs=120x120&chl=learninglog://item?qrcode="+$("#qrcode").val()+"\"/>");
                    });
                });
            </script>
	</c:param>
</c:import>

<head>
<link rel="stylesheet" href="${ctx}/js/bootstrap/css/bootstrap.css" />
<link rel="stylesheet" href="${ctx}/js/bootstrap/css/bootstrap.min.css" />
<link rel="stylesheet"
	href="${ctx}/js/bootstrap/css/bootstrap-responsive.css" />
<script src="${ctx}/js/bootstrap/js/bootstrap.js" type="text/javascript"></script>
<script src="${ctx}/js/bootstrap/js/bootstrap.min.js"
	type="text/javascript"></script>
</head>
<body id="page_member_profile">
	<div id="Body">
		<c:url value="/task/${tasksingleitem.id}/edit" var="itemresource" />
		<form:form commandName="task" action="${itemresource}" method="post">
			<div id="Container">
				<c:import url="../include/header.jsp" />
				<div id="Contents">
					<div id="ContentsContainer">
						<div id="localNav"></div>
						<!-- localNav -->
						<div id="LayoutTask" class="Layout">
                            <div id="Top">
						<div class="navbar navbar-inverse" style="position: static;">
							<div class="navbar-inner">
								<div class="container">
									<a class="btn btn-navbar" data-toggle="collapse"
										data-target=".navbar-inverse-collapse"> <span
										class="icon-bar"></span> <span class="icon-bar"></span> <span
										class="icon-bar"></span>
									</a>

									<div class="nav-collapse collapse navbar-inverse-collapse">
										<ul class="nav">
												<li class="active"><a href="${ctx}/task/taskitem"><i class="icon-home"></i>Task info</a></li>

												<li><a href="${ctx}/task/add"><i class=" icon-user"></i>Personal</a></li>
												<li><a href="${ctx}/task/collaborative_add"><i class=" icon-eye-open"></i>Collaborative</a></li>
												<li><a href="#"><i class=" icon-map-marker"></i>Recommend</a></li>

										</ul>
										
									</div>
									<!-- /.nav-collapse -->
								</div>
							</div>
							<!-- /navbar-inner -->
						</div>
						<!-- /navbar -->
						<!-- parts -->

						
					</div>
					</div>
						<div id="LayoutD" class="Layout">
							<div id="Top"></div>
							<!-- Top -->


							<div id="">
								<div id="profile" class="dparts listBox">

									<div class="navbar navbar-inner" style="position: static;">
											<div class="navbar-inverse">
										<h3
											style="font-size: 17px; font-weight: bolder; line-height: 150%">Edit
											Information</h3>
									</div></div>

									<c:url value="/item/${item.id}" var="itemUrl" />
									<table>

										<tr>
											<th style="width: 120px">Task title</th>
											<td><form:input path="title" cssClass="input_text"
													id="title" value="${tasksingleitem.title}" /></td>
										</tr>
										<tr>
											<th>Place</th>
											<td><form:input path="place" cssClass="input_text"
													id="place" value="${tasksingleitem.place}" /></td>
										</tr>
										<tr>
											<th><label for="tag1">Level</label></th>
											<td><form:input path="level" cssClass="input_text"
													id="level" value="${tasksingleitem.level}" /></td>
										</tr>
										<tr>
											<th rowspan="2">Location</th>

										</tr>
										<tr>
											<td><form:hidden path="lat" id="itemLat" /> <form:hidden
													path="lng" id="itemLng" /> <form:hidden path="zoom"
													id="itemZoom" />
												<div id="map" style="width: 450px; height: 300px"></div></td>
										</tr>
										<form:form commandName="taskscript" action="${itemresource}"
											method="post">
											<c:forEach items="${taskscriptallitem}" var="script">
												<c:if test="${!empty script.script}">
													<tr>
														<td>${script.num}</td>
														<td><form:textarea path="process${script.num}"
																id="script" value="${script.script}" cols="20" rows="4"
																cssStyle="width:98%" /></td>
													</tr>
												</c:if>
											</c:forEach>
										</form:form>
									</table>
								</div>



							</div>

						</div>
						<!-- Layout -->
						<div class="block"></div>
						<div id="sideBanner"></div>
						<!-- sideBanner -->

					</div>
					<!-- ContentsContainer -->




				</div>
				<!-- Contents -->

				<div id="Contents">
					<div id="ContentsContainer">
						<div id="LayoutA" class="Layout">
							<div class="bs-docs-example">
								<div id="myCarousel" class="carousel slide">
									<ol class="carousel-indicators">
										<li data-target="#myCarousel" data-slide-to="0" class="active"></li>
										<li data-target="#myCarousel" data-slide-to="1"></li>
										<li data-target="#myCarousel" data-slide-to="2"></li>
									</ol>
									<div class="carousel-inner">
										<div class="item active">
											<center>
												<img
													src="http://ll.is.tokushima-u.ac.jp/static/learninglog_dev/173/d1b/e9c/c5c/420/29e/479/f83/c20/468/31/173d1be9cc5c42029e479f83c2046831_800x600.png"
													alt="noname" width="270" height="400">
											</center>
											<div class="carousel-caption">
												<c:forEach items="${taskscriptallitem}" var="script">

													<c:if test="${script.num=='1'}">
														<h4>Task Script.1 ${script.image_name}</h4>
														<p>${script.script}</p>
													</c:if>
												</c:forEach>

											</div>
										</div>
										<div class="item">
											<center>
												<img
													src="http://ll.is.tokushima-u.ac.jp/static/learninglog_dev/d08/3a9/a35/474/447/7b8/3d4/e01/3e9/960/86/d083a9a354744477b83d4e013e996086_800x600.png"
													alt="" width="270" height="400">
											</center>
											<div class="carousel-caption">

												<c:forEach items="${taskscriptallitem}" var="script">

													<c:if test="${script.num=='2'}">
														<h4>Task Script.2 ${script.image_name}</h4>
														<p>${script.script}</p>
													</c:if>
												</c:forEach>
											</div>
										</div>
										<div class="item">
											<center>
												<img
													src="http://ll.is.tokushima-u.ac.jp/static/learninglog_dev/358/915/0ae/595/438/496/fa7/89b/818/a14/8d/3589150ae595438496fa789b818a148d_800x600.png"
													alt="" width="270" height="400">
											</center>
											<div class="carousel-caption">

												<c:forEach items="${taskscriptallitem}" var="script">

													<c:if test="${script.num=='3'}">
														<h4>Task Script.3 ${script.image_name}</h4>
														<p>${script.script}</p>
													</c:if>
												</c:forEach>
											</div>
										</div>
									</div>
									<a class="left carousel-control" href="#myCarousel"
										data-slide="prev">&lsaquo;</a> <a
										class="right carousel-control" href="#myCarousel"
										data-slide="next">&rsaquo;</a>
								</div>
							</div>
							<div class="operation">
								<ul class="moreInfo button">
									<li><input type="submit" class="btn btn-primary"
										value="Save" /></li>
									<li><a href="<c:url value="/task/${tasksingleitem.id}" />"
										class="btn">Return</a></li>
								</ul>
							</div>
		</form:form>
	</div>
	</div>
	</div>

	<c:import url="../include/footer.jsp" />
	</div>
	<!-- Container -->

	</div>
	<!-- Body -->
</body>
</html>
