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

		<style type="text/css">
nav ul li {
	float: left;
	margin: 20px 20px 30px 20px;
}

nav ul li a {
	display: block;
	width: 260px;
	height: 130px;
	background-image: url(icons.png);
	background-repeat: no-repeat;
}

nav ul li:nth-child(1) a {
	background-color: #5bb2fc;
	background: transparent url(../images/tasknote.png) no-repeat scroll 0 0;
	background-position: 0px 0px;
}

nav ul li:nth-child(2) a {
	background-color: #58ebd3;
	background: transparent url(../images/taskadd.png) no-repeat scroll 0 0;
	background-position: 0px 15px;
}

nav ul li:nth-child(3) a {
	background-color: #ffa659;
	background: transparent url(../images/Administratetask.png) no-repeat
		scroll 0 0;
	background-position: 0px 10px;
}

nav ul li:nth-child(4) a {
	background: transparent url(../images/recommendtask.png) no-repeat
		scroll 0 0;
	background-position: 0px 10px;
}
</style>

		<script>
			var pw;
			function compare() {

				pw = prompt("パスワードを入れて下さい。", "");

				if (pw == "kousukemouri") {
					location.href = "${ctx}/task/add";
				} else {
					alert("パスワードが違います！");
					location.href = "${ctx}/task/topmenu";
				}
			}
		</script>

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
</head>
<body>

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
											<li class="active"><a href="${ctx}/task/taskitem">Task info</a></li>
											
											<li><a href="${ctx}/task/add">Personal</a></li>
											<li><a href="${ctx}/task/collaborative_add">Collaborative</a></li>
											<li><a href="#">Recommend</a></li>

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
					<!-- Top -->
					<div id="Center">



<!--						<nav>
							<ul>
								<li><a href="${ctx}/task/taskitem"> <span>All
											Task</span>
								</a></li>
								<li><a href="${ctx}/task/add"> <span>Personal
											Learning Task</span>
								</a></li>
								<li><a href="${ctx}/task/collaborative_add"> <span>Collaborative
											Learning by teacher</span>
								</a></li>
								<li><a href="#"> <span>Recommend Task</span>
								</a></li>

							</ul>
						</nav>
  -->



					</div>

					<!-- Layout -->
				</div>
				<!-- ContentsContainer -->
			</div>
			<!-- Contents -->


		</div>
		<!-- Container -->

	</div>
	<br>
	<c:import url="../include/footer.jsp" />
	<!-- Body -->
</body>
</html>
