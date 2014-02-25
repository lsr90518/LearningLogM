<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../include/taglibs.jsp" %>
<c:set var="userId">
	<shiro:principal property="id" />
</c:set>
<!doctype html>
<c:import var="pageLinks" url="itempage.jsp">
	<c:param name="searchCond" value="${searchCond}" />
	<c:param name="page" value="${page}" />
</c:import>
<html>
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
	</c:param>
</c:import>
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
									<h3 style="font-size: 16px; font-weight: bolder">Search</h3>
								</div>
								<c:url value="/item" var="itemSearchUrl" />
								<form:form commandName="searchCond" action="${itemSearchUrl}"
									method="get" id="searchForm">
									<table>
										<tbody>
											<tr>
												<td style="width: 20%"><label for="title">Title</label></td>
												<td><form:input id="title" path="title"
														cssClass="input text" size="15" /> <form:hidden
														id="questionStatus" path="questionStatus" /> <form:hidden
														id="teacherConfirm" path="teacherConfirm" /></td>
											</tr>
											<tr>
												<td><label for="username">Author</label></td>
												<td><form:input id="username" path="username"
														cssClass="input text" size="15" /></td>
											</tr>
											<tr>
												<td><label for="tag">Tag</label></td>
												<td><form:input id="tag" path="tag"
														cssClass="input text" size="15" /></td>
											</tr>
											<tr>
												<td><label>Date</label></td>
												<td><form:input id="dateFrom" path="dateFrom" size="10"
														maxlength="10" />～ <form:input id="dateTo" path="dateTo"
														size="10" maxlength="10" /></td>
											</tr>
											<tr>
												<td colspan="2"><label for="includeRelog">Include
														Relog</label> <form:checkbox path="includeRelog" id="includeRelog" />
												</td>
											</tr>
											<tr>
												<td><label for="">Map</label></td>
												<td><form:checkbox path="mapenabled" id="mapenabled" />&nbsp;<label
													for="mapenabled">Search by map</label></td>
											</tr>
											<tr>
												<td colspan="2"><form:hidden path="x1" id="x1" /> <form:hidden
														path="y1" id="y1" /> <form:hidden path="x2" id="x2" /> <form:hidden
														path="y2" id="y2" /><br />
													<div id="searchMap" style="width: 250px; height: 250px;">
													</div></td>
											</tr>
											<tr>
												<td colspan="2"><input type="submit" value="Search" /></td>
											</tr>
										</tbody>
									</table>
								</form:form>
							</div>

							<div id="tagCloud" class="parts searchFormLine">
								<div class="partsHeading">
									<h3 style="font-size: 16px; font-weight: bolder">Tag Cloud</h3>
								</div>
								<c:url value="/item" var="itemSearchUrl" />
								<div class="tagcloud">
									<c:forEach items="${tagCloud}" var="tag">
										<a class="cloud${tag.value}"
											href="<c:url value="/item"><c:param name="tag" value="${tag.key}" /></c:url>">${tag.key}</a>
									</c:forEach>
								</div>
							</div>

						</div>
						<!-- Left -->
						<div id="Center">
							<div class="dparts searchResultList">
								<div class="parts">
									<div class="partsHeading">
										<h3>Sorted by date</h3>
										<a class="addlink" href="<c:url value="/item/add" />">Add
											new object</a>
									</div>
									<div class="operation">
										<ul class="moreInfo button" style="text-align: left">
											<li>Question Status:&nbsp;</li>
											<li><button class="input_submit"
													onclick="$('#questionStatus').val('');$('#searchForm').submit();"
													<c:if test="${empty searchCond.questionStatus}">disabled="disabled"</c:if>>All</button></li>
											<li><button class="input_submit"
													onclick="$('#questionStatus').val('inquestion');$('#searchForm').submit();"
													<c:if test="${searchCond.questionStatus=='inquestion'}">disabled="disabled"</c:if>>Answers
													awaited</button></li>
											<li><button class="input_submit"
													onclick="$('#questionStatus').val('resolved');$('#searchForm').submit();"
													<c:if test="${searchCond.questionStatus=='resolved'}">disabled="disabled"</c:if>>Resolved</button>
												<br />
											<br /></li>
											<li>Teacher Confirm:&nbsp;</li>
											<li><button class="input_submit"
													onclick="$('#teacherConfirm').val('');$('#searchForm').submit();"
													<c:if test="${empty searchCond.teacherConfirm}">disabled="disabled"</c:if>>All</button></li>
											<li><button class="input_submit"
													onclick="$('#teacherConfirm').val('confirmed');$('#searchForm').submit();"
													<c:if test="${searchCond.teacherConfirm=='confirmed'}">disabled="disabled"</c:if>>Confirmed</button></li>
											<li><button class="input_submit"
													onclick="$('#teacherConfirm').val('needfixing');$('#searchForm').submit();"
													<c:if test="${searchCond.teacherConfirm=='needfixing'}">disabled="disabled"</c:if>>Corrections
													needed</button></li>
										</ul>
									</div>
									<div class="pagerRelative">${pageLinks}</div>
									<div class="block">
										<c:forEach items="${itemPage.content}" var="item">
											<c:set var="privateFlg" value="false" />
											<c:if test="${item.shareLevel==1 && userId!=item.author}">
												<c:set var="privateFlg" value="true" />
											</c:if>
											<div class="ditem">
												<div class="item"
													data-url="<c:url value="/item/${item.id}" />">
													<table>
														<tbody>
															<tr>
																<td rowspan="5" class="photo"><c:choose>
																		<c:when test="${!privateFlg}">
																			<c:choose>
																				<c:when test="${empty item.image}">
																					<img height="70px" alt=""
																						src="<c:url value="/images/no_image.gif" />" />
																				</c:when>
																				<c:otherwise>
																					<img class="staticimage" height="70px" alt=""
																						src="<tags:static filename="${item.image}_160x120.png" />" />
																				</c:otherwise>
																			</c:choose>
																			<br />
																		</c:when>
																		<c:otherwise>
																			<img height="70px" alt=""
																				src="<c:url value="/images/locked.png" />"
																				title="Private" />
                                                                        		Details<img
																				alt="photo"
																				src="<c:url value="/images/icon_camera.gif" />" />
																		</c:otherwise>
																	</c:choose></td>
																<td colspan="2"><c:choose>
																		<c:when test="${empty item.titles}">
                                                                            NO NAME
                                                                        </c:when>
																		<c:otherwise>
																			<div class="titleTable">
																				<table>
																					<c:forEach items="${item.titles}" var="title">
																						<tr>
																							<td style="width: 70px;">${title.langName}</td>
																							<td>${title.content}</td>
																						</tr>
																					</c:forEach>
																				</table>
																			</div>
																		</c:otherwise>
																	</c:choose></td>
															</tr>
															<tr>
																<td
																	style="text-align: right; vertical-align: bottom; height: 10px; middle; display: table-cell"
																	colspan="2"><a class="userlink"
																	data-uid="${item.author.id}"
																	data-uname="${item.author.nickname}"
																	href="<c:url value="/item"><c:param name="username" value="${item.author.nickname}" /></c:url>">
																		<c:out value="${item.author.nickname}" />
																</a> <c:if test="${!empty item.relogItem}">
                                                                            &nbsp;(<a
																			href="<c:url value="/item/${item.relogItem}" />">ReLog</a> from <a
																			class="userlink"
																			data-uid="${item.relog.author.id}"
																			data-uname="${item.relog.author.nickname}"
																			href="<c:url value="/item"><c:param name="username" value="${item.relog.author.nickname}" /></c:url>"><c:out
																				value="${item.relog.author.nickname}" /></a>)
                                                                        </c:if>
																	&nbsp;<fmt:formatDate type="both"
																		pattern="yyyy/MM/dd HH:mm" value="${item.createTime}" />
																</td>
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
