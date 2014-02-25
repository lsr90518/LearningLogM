<%@page contentType="text/html" pageEncoding="UTF-8"
	trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!doctype html>
<html>
<c:import url="../include/head.jsp">
	<c:param name="title" value="Add a new object" />
	<c:param name="content">
		<style>
.optional {
	display: none
}
</style>
		<script src="http://www.google.com/jsapi"></script>
		<script src="http://maps.google.com/maps/api/js?sensor=true"></script>
		<script src="<c:url value='/js/LLMap.js' />"></script>
		<script>
			$(function() {
				if (!$('#location_based_cb').is(':checked'))
					$('#task_map').hide();
				else
					$('#task_map').show();
			});

			var map;
			$(function() {
				map = new LLMap("map", {
					onchange : function(lat, lng, zoom) {
						$("#lat").val(lat);
						$("#lng").val(lng);
						$("#zoom").val(zoom);
					}
				});
			});

			$(document).on("change", "#location_based_cb", function() {
				if ($('#location_based_cb').is(':checked'))
					$('#task_map').show();
				else
					$('#task_map').hide();
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
<body>
	<div>
		<div>
			<c:import url="../include/header.jsp" />
			<div id="Contents">
				<div id="ContentsContainer">
					
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
												<li><a href="${ctx}/task/taskitem"><i class="icon-home"></i>Task info</a></li>

												<li><a href="${ctx}/task/add"><i class=" icon-user"></i>Personal</a></li>
												<li class="active"><a href="${ctx}/task/collaborative_add"><i class=" icon-eye-open"></i>Collaborative</a></li>
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
						<!-- Top -->
						<div id="Center">
							<div id="diaryForm" class="dparts form">
								<div class="parts">
									<div class="navbar navbar-inner" style="position: static;">
											<div class="navbar-inverse">
										<h3
											style="font-size: 17px; font-weight: bolder; line-height: 150%">Add
											new Collaborative Task</h3>
									</div></div>
									<c:url value="/task/collaborative_add" var="taskUrl" />
									<form:form commandName="collaborativetask" action="${taskUrl}"
										method="post" enctype="multipart/form-data">
										<ul>
											<li><form:errors path="title"></form:errors></li>
											<li><form:errors path="languageId"></form:errors></li>
										</ul>
										<table>
											<tr>
												<th><label for="task_title">Task Title</label><strong>*</strong>
												</th>
												<td><form:input path="title" cssStyle="width:90%" />&nbsp;
												</td>
											</tr>
											<tr>
												<th><label for="target_language">Target
														Language</label><strong>*</strong></th>
												<td><form:select path="languageId">
														<form:option value="English" label="English" />
														<form:option value="Japanese" label="Japanese" />
														<form:option value="Afrikaans" label="Afrikaans" />
														<form:option value="Amharic" label="Amharic" />
														<form:option value="Chinese" label="Chinese" />
														<form:option value="Armenian" label="Armenian" />
														<form:option value="Basque" label="Basque" />
														<form:option value="Bengali" label="Bengali" />
														<form:option value="Bulgarian" label="Bulgarian" />
														<form:option value="Catalan" label="Catalan" />
														<form:option value="Cherokee" label="Cherokee" />
														<form:option value="Czech" label="Czech" />
													</form:select></td>
											</tr>
											<tr>
												<th><label for="place">place</label></th>
												<td><form:input path="place" cssClass="input_text"
														id="place" /> (e.g.) supermarket</td>
											</tr>

											<tr>
												<th><label for="place">number of men</label></th>
												<td><form:input path="number" cssClass="input_text"
														id="number" /> (e.g.) 1 or 2 or 3 and so on</td>
											</tr>

											<tr>
												<th><label for="place">time limit</label></th>
												<td><form:input path="time_limit" cssClass="input_text"
														id="time_limit" /> (e.g.) 30 or 60 or 90 and so on</td>
											</tr>
											<tr>
												<th><label for="level">Task Level</label></th>
												<td><form:input path="level" cssClass="input_text"
														id="level" /> (e.g.) 1 or 2 or 3 and so on</td>
											</tr>
											<tr>
												<th><label for="tag">Previous knowledge</label></th>
												<td><form:input path="tag" id="tagInput"
														cssStyle="width:50%" /></td>
											</tr>
											<tr>
												<th><label for="location_based_label">Location
														Based</label><strong>*</strong></th>
												<td><form:checkbox path="locationBased"
														id="location_based_cb" /></td>
											</tr>
											<tr id="task_map">
												<td colspan="2"><form:hidden path="lat" id="lat" /> <form:hidden
														path="lng" id="lng" /> <form:hidden path="zoom" id="zoom" />
													<div id="map" style="width: 98%; height: 350px"></div></td>
											</tr>

										</table>
										<div class="operation">
											<ul class="moreInfo button">
												<li><input type="submit" class="btn btn-primary"
													value="Next" /></li>
												<li><input type="submit" class="btn btn-inverse"
													value="Cancel" /></li>
											</ul>
										</div>


									</form:form>
								</div>
								<!-- parts -->
							</div>
							<!-- dparts -->
						</div>
						<!-- Center -->
					</div>
					<!-- Layout -->
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
