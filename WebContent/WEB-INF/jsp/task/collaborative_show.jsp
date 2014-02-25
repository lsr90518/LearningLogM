<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../include/taglibs.jsp"%>
<c:set var="userId">
	<shiro:principal property="id" />
</c:set>
<!doctype html>
<c:import var="pageLinks" url="itempage.jsp">
	<c:param name="searchCond" value="${searchCond}" />
	<c:param name="page" value="${page}" />
</c:import>
<html>
<head>
<c:import url="../include/head.jsp">
	<c:param name="title" value="All Logs" />
	<c:param name="content">
		<style type="text/css">
#itemSearchFormLine th {
	border: none;
}

#itemSearchFormLine td {
	border: none;
}
</style>
		<script src="http://www.google.com/jsapi"></script>
		<script type="text/javascript">
                $(function(){
                    $('#dateFrom').datepicker({
                        dateFormat: 'yy-mm-dd'
                    });
                    $('#dateTo').datepicker({
                        dateFormat: 'yy-mm-dd'
                    });
                    $(".item").click(function(){
                    	window.location.href=$(this).attr("data-url");
                    }).css("cursor", "pointer");
                });
            </script>
		<script src="http://maps.google.com/maps/api/js?sensor=false"></script>
		<script type="text/javascript">
                $(function(){
                	var map = new google.maps.Map(document.getElementById("searchMap"), {
                        disableDefaultUI: true,
                        scaleControl: true,
                        navigationControl: true,
                        mapTypeId: google.maps.MapTypeId.ROADMAP,
                    });
                    
                    var bounds = new google.maps.LatLngBounds();
                    var marker;
	                <c:forEach items="${itemPage.content}" var="item" varStatus="sta" >
	                    <c:if test="${(!empty item.itemLat) and (!empty item.itemLng)}">
                            marker = new google.maps.Marker({position:new google.maps.LatLng(${item.itemLat}, ${item.itemLng}), map:map});
                            bounds.extend(marker.getPosition());
	                    </c:if>
	                </c:forEach>
                   map.fitBounds(bounds);
                   google.maps.event.addListener(map, "bounds_changed", function(){
                       $('#x1').val(map.getBounds().getNorthEast().lat());
                       $('#y1').val(map.getBounds().getNorthEast().lng());
                       $('#x2').val(map.getBounds().getSouthWest().lat());
                       $('#y2').val(map.getBounds().getSouthWest().lng());
                   });
                });
            </script>
		<script>
            $('.carousel').carousel('prev')({
          	  interval: 200
          	})
            
            </script>
	</c:param>
</c:import>
<script src="${ctx}/js/bootstrap/js/bootstrap.js" type="text/javascript"></script>
<script src="${ctx}/js/bootstrap/js/bootstrap.min.js"
	type="text/javascript"></script>
<head>
<link rel="stylesheet" href="${ctx}/js/bootstrap/css/bootstrap.css" />
<link rel="stylesheet" href="${ctx}/js/bootstrap/css/bootstrap.min.css" />
<link rel="stylesheet"
	href="${ctx}/js/bootstrap/css/bootstrap-responsive.css" />
</head>
<body id="page_diary_list">
	<div id="Body">
		<div id="Container">
			<c:import url="../include/header.jsp" />

			<div id="Contents">
				<div id="ContentsContainer">
					<div id="LayoutA" class="Layout">
						<div id="Left">
							<div id="itemSearchFormLine" class="parts searchFormLine">
								<div class="partsHeading">
									<h3
										style="font-size: 20px; font-weight: bolder; line-height: 150%">Task
										Search</h3>
								</div>


							</div>

							<c:url value="/task/taskitem" var="itemSearchUrl" />
							<form:form commandName="task" action="${itemSearchUrl}"
								method="post" id="searchForm">
								<table>

									<tr>
										<td><label for="title">Task name </label></td>
										<td><center>
												<form:input id="title" path="title" cssClass="input text"
													size="15" cssStyle="width:70%" />
											</center></td>
									</tr>

									<tr>
										<td><label for="title">Place </label></td>
										<td><center>
												<form:input id="title" path="place" cssClass="input text"
													size="15" cssStyle="width:70%" />
											</center></td>
									</tr>

									<tr>
										<td><label for="title">Level </label></td>
										<td><center>
												<form:input id="title" path="level" cssClass="input text"
													size="15" cssStyle="width:70%" />
											</center></td>
									</tr>

									<tr>
										<div class="operation">
											<ul class="moreInfo button">
												<td colspan="2"><input type="submit"
													class="btn btn-primary" value="Search" /></td>
											</ul>
										</div>

									</tr>

								</table>
							</form:form>




						</div>
						<!-- Left -->
						<div id="Center">
							<div class="dparts searchResultList">
								<div class="parts">
									<div class="partsHeading">
										<h3
											style="font-size: 20px; font-weight: bolder; line-height: 150%">Sorted
											by Task</h3>
										<a class="addlink" href="<c:url value="/task/add" />">Add
											new Task</a>
									</div>

									<div class="pagerRelative">${pageLinks}</div>
									<div class="block">
										<c:forEach items="${taskitems}" var="item">
											<c:set var="privateFlg" value="false" />

											<div class="ditem">
												<div class="item"
													data-url="<c:url value="/task/${item.id}" />">
													<table>
														<tbody>
															<tr>

																<td colspan="2"><c:choose>
																		<c:when test="${empty item.title}">
                                                                            NO NAME
                                                                        </c:when>
																		<c:otherwise>

																			<table>

																				<tr>
																					<td style="width: 70px;">Task name</td>
																					<td>${item.title}</td>
																				</tr>
																				<tr>
																					<td style="width: 70px;">Create time</td>
																					<td>${item.create_time}</td>
																				</tr>
																				<tr>
																					<td style="width: 70px;">Place</td>
																					<td>${item.place}</td>
																				</tr>
																				<tr>
																					<td style="width: 70px;">Level</td>
																					<td>${item.level}</td>
																				</tr>
																			</table>

																		</c:otherwise>
																	</c:choose></td>
															</tr>

														</tbody>
													</table>
												</div>
											</div>
											<hr />
										</c:forEach>
									</div>
									<div class="pagerRelative">
										${pageLinks}
										<p class="number">
											<!--7件中 1～7件目を表示-->
										</p>
									</div>
								</div>
							</div>
						</div>
						<!-- Center -->
					</div>
					<!-- Layout -->
					<div id="sideBanner">
						<%--
                            <form action="/member/changeLanguage" method="post"><label for="language_culture">言語</label>:
                                <select name="language[culture]" onchange="submit(this.form)" id="language_culture">

                                    <option value="en">English</option>
                                    <option value="ja_JP" selected="selected">日本語 (日本)</option>
                                </select><input value="diary/index" type="hidden" name="language[next_uri]" id="language_next_uri" /></form>
                            --%>
					</div>
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


</body>
</html>
