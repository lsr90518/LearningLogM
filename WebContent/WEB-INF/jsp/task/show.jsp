<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../include/taglibs.jsp"%>
<c:set var="userId">
	<shiro:principal property="id" />
</c:set>
<!doctype html>
<html>
<c:import url="../include/head.jsp">
	<c:param name="title" value="オブジェクト" />
	<c:param name="content">
		<script src="${ctx}/js/jQuery.jPlayer.2.0.0/jquery.jplayer.min.js"></script>
		<script src="${ctx}/js/jquery/jquery.linkify-1.0-min.js"></script>
		<script src="${ctx}/js/mediaelement/mediaelement-and-player.min.js"></script>
		<script src="${ctx}/js/jquery/stars/jquery.ui.stars.min.js"></script>
		<script>
                $(function(){
                	$("video, audio").mediaelementplayer();
                    $(".description").linkify();
                });
                
                function speak(title, lang){
                	$("#ttsTitleArea").html("<div id=\"ttsTitle\"></div>");
                	$('#ttsTitle').jPlayer({
                		ready:function(){
                        	$('#ttsTitle').jPlayer("setMedia", {
            					mp3: "<c:url value="/api/translate/tts" />?ie=UTF-8&lang="+lang+"&text="+encodeURIComponent(title)
            				}).jPlayer("play");
                		},
                		ended: function(){
                			$("#ttsTitle").jPlayer("destroy");
                			$("#ttsTitleArea").html("");
                		},
                		swfPath:"${ctx}/js/jQuery.jPlayer.2.0.0",
                		supplied: "mp3"
                	});
                }
            </script>
		<script>
                <c:choose>
                    <c:when test="${ratingExist}">
                        $(function(){
                            $("#rat").children().not("select, #messages").hide();
                            $("#rating_title").text("Rating");
                            var $caption = $('<div id="caption"/>');
                            $("#rat").stars({
                                inputType: "select",
                                cancelShow: false,
                                disabled: true
                            });
                            $("#rat").stars("select", Math.round(${avg}));
                            var $caption = $('<div id="caption"/>');
                            $caption.text(" (" + ${votes} + " votes; " + ${avg} + ")");
                            $caption.appendTo("#rat");
                        });
                    </c:when>
                    <c:otherwise>
                        $(function(){
                            $("#rat").children().not("select, #messages").hide();
                            var $caption = $('<div id="caption"/>');
                            $("#rat").stars({
                                inputType: "select",
                                cancelShow: false,
                                captionEI: $("#caption"),
                                oneVoteOnly: true,
                                callback: function(ui,type,value){
                                    ui.disable();
                                    $("#messages").text("Saving...").stop().css("opacity", 1).fadeIn(30);

                                    $.post("<c:url value="/itemrating/${item.id}?format=json" />", {rate: value}, function(json){
                                        $("#rating_title").text("Rating");
                                        ui.select(Math.round(json.avg));
                                        $caption.text(" (" + json.votes + " votes; " + json.avg + ")");
                                        $("#messages").text("Rating saved (" + value + "). Thanks!").stop().css("opacity", 1).fadeIn(30);
                                        setTimeout(function(){
                                            $("#messages").fadeOut(1000);
                                        }, 2000);
                                    }, "json");
                                }
                            });
                            $("#rat").stars("selectID", -1);
                            $caption.appendTo("#rat");
                            $('<div id="messages"/>').appendTo("#rat");
                        });
                    </c:otherwise>
                </c:choose>
            </script>
		<script src="${ctx}/js/jquery/jquery.nyroModal.custom.min.js"></script>
		<!--[if IE 6]>
				<script type="text/javascript" src="${ctx}/js/jquery/jquery.nyroModal-ie6.min.js"></script>
			<![endif]-->
		<script>
	            $(function() {
	            	  function preloadImg(image) {
	            	    var img = new Image();
	            	    img.src = image;
	            	  }
	
	            	  preloadImg('${ctx}/images/ajaxLoader.gif');
	            	  preloadImg('${ctx}/images/prev.gif');
	            	  preloadImg('${ctx}/images/next.gif');
	            	  preloadImg('${ctx}/images/close.gif');
	            	  $('.nyroModal').nyroModal();
            	});
            </script>
		<link rel="stylesheet" type="text/css" media="screen"
			href="${ctx}/js/jquery/stars/jquery.ui.stars.min.css" />
		<link rel="stylesheet" type="text/css" media="screen"
			href="${ctx}/js/mediaelement/mediaelementplayer.min.css" />
		<link rel="stylesheet" type="text/css" media="screen"
			href="${ctx}/css/nyroModal.css" />
	</c:param>
</c:import>
<head>
<link rel="stylesheet" href="${ctx}/js/bootstrap/css/bootstrap.css" />
<link rel="stylesheet" href="${ctx}/js/bootstrap/css/bootstrap.min.css" />
<link rel="stylesheet"
	href="${ctx}/js/bootstrap/css/bootstrap-responsive.css" />
</head>
<body id="page_member_profile">
	<div id="Body">
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
												<li class="active"><a href="${ctx}/task/taskitem"><i
														class="icon-home"></i>Task info</a></li>

												<li><a href="${ctx}/task/add"><i class=" icon-user"></i>Personal</a></li>
												<li><a href="${ctx}/task/collaborative_add"><i
														class=" icon-eye-open"></i>Collaborative</a></li>
												<li><a href="#"><i class=" icon-map-marker"></i>Recommend</a></li>

											</ul>
											<form class="navbar-search pull-left" action="">
												<input type="text" class="search-query span2"
													placeholder="Search">
											</form>

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

					<div id="LayoutC" class="Layout">
						<div id="Top"></div>
						<!-- Top -->

						<div id="Center">
							<div id="diaryForm" class="dparts form">
								<div class="parts">

									<div class="navbar navbar-inner" style="position: static;">
										<div class="navbar-inverse">
										
											<h3
												style="font-size: 17px; font-weight: bolder; line-height: 150%">Information</h3>

										
											<a href="<c:url value="/task/${tasksingleitem.id}/edit" />"><i
												class="icon-edit"></i>Edit</a> <a
												href="<c:url value="/task/${tasksingleitem.id}/delete" />"><i
												class="icon-remove"></i>Delete</a> <a
												href="<c:url value="/task/taskitem" />"><i
												class="icon-arrow-left"></i>Return</a>


										</div>
									</div>


									<table>


										<tr>
											<th>Title</th>
											<td>

												<table>

													<tr>
														<td><b>${tasksingleitem.title}</b></td>

													</tr>

												</table>
											</td>
										</tr>
										<tr>
											<th>Palce</th>

											<td>${tasksingleitem.place}</td>

										</tr>

										<tr>
											<th>Level</th>
											<td>${tasksingleitem.level}</td>
										</tr>
										<c:if test="${not empty tasksingleitem.number}">
											<tr>
												<th>Number of men</th>
												<td>${tasksingleitem.number}</td>
											</tr>
										</c:if>
										<c:if test="${not empty tasksingleitem.time_limit}">
											<tr>
												<th>Time limit</th>
												<td>${tasksingleitem.time_limit}</td>
											</tr>
										</c:if>
										<tr>
											<th>Created</th>
											<td><fmt:formatDate type="both"
													pattern="yyyy/MM/dd HH:mm"
													value="${tasksingleitem.create_time}" /></td>
										</tr>
										<tr>
											<th>Related Objects</th>
											<td><c:forEach items="${taskscriptallitem}" var="script">
													<c:if test="${!empty script.image_name}"> ${script.image_name}, </c:if>



												</c:forEach></td>
										</tr>

									</table>
								</div>
							</div>
							<!-- dparts -->


							<div class="dparts form">
								<div class="parts">
									<div class="navbar navbar-inner" style="position: static;">
										<div class="navbar-inverse">
											<h3
												style="font-size: 17px; font-weight: bolder; line-height: 150%">Task
												script flow</h3>
										</div>
									</div>
									<div class="block">

										<table>

											<tr>
												<td><center>
														<b>Script number flow</b>
													</center></td>
												<td><center>
														<b>Script content</b>
													</center></td>
											</tr>
											<c:forEach items="${taskscriptallitem}" var="script">

												<tr>
													<td>
														<center>
															<b>Script.${script.num}</b>
														</center>
													</td>
													<td>${script.script}</td>

												</tr>

											</c:forEach>

										</table>


									</div>
									<div class="block">
										<ul class="articleList">
										</ul>
										<div class="moreInfo">
											<ul class="moreInfo">
												<li><a href="<c:url value="/item" />">Return to
														Object List</a></li>
											</ul>
										</div>
									</div>
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
				<c:import url="../include/footer.jsp" />
			</div>
			<!-- Container -->
		</div>
		<!-- Body -->
	</div>
</body>
</html>