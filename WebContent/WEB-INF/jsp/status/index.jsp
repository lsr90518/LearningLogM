<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../include/taglibs.jsp" %>
<!doctype html>
<html>
    
    <c:import url="../include/head.jsp">
		<c:param name="title" value="My Status" />
		<c:param name="javascript">
		    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
			<script type="text/javascript">
				google.load("visualization", "1", {packages:["corechart"]});
				google.setOnLoadCallback(drawChart);
				function drawChart() {
					var data = new google.visualization.DataTable();
					var test1 = ${nextExperiencePoint};
					data.addColumn('string', 'Task');
					data.addColumn('number', 'EXP');
					data.addRows([
						['UploadObjects', ${user.experiencePoint}],
						['Quiz', ${quizInfos.allscores}],
						['AnserQuestion', ${answerCount}],
					]);

		        var options = {
					width: 430, height: 230,
					//title: 'My EXP',
					backgroundColor: 'transparent', // 背景色を透過
					colors: ['#ff7f50', '#ffa500', '#f5deb3'],
					legend: {textStyle: {fontSize: 11}}
				};

				var chart = new google.visualization.PieChart(document.getElementById('chart_div'));
				chart.draw(data, options);
				}
			</script>
		</c:param>
    </c:import>



    <body id="page_status_home">
        <div id="Body">
            <div id="Container">
                <c:import url="../include/header.jsp" />
                <div id="Contents">
                    <div id="ContentsContainer">
                        <div id="LayoutA" class="Layout">
                            <div id="Left">
                                <div id="memberImageBox_22" class="parts memberImageBox">
                                    <p class="photo">
                                        <c:choose>
                                            <c:when test="${empty user.avatar}">
                                                <img alt="LearningUser" src="<c:url value="/images/no_image.gif" />" height="180" width="180" />
                                            </c:when>
                                            <c:otherwise>
                                                <img alt="LearningUser" src="<tags:static filename="${user.avatar}_320x240.png" />" width="180" />
                                            </c:otherwise>
                                        </c:choose>
                                    </p>
                                    <p class="text"><shiro:principal property="nickname" /></p>
                                    <p class="text">Level : ${user.userLevel}</p>
                                    <p class="text">EXP : ${nowExperiencePoint} / Next : ${nextExperiencePoint}</p>

                                    <div class="moreInfo">
                                        <ul class="moreInfo">
                                            <li><a href=" <c:url value="/profile/avataredit"/>">Edit Photo</a></li>
                                            <li><a href=" <c:url value="/profile"/>">My Profile</a></li>
                                            <li><a href=" <c:url value="/status/graph"/>">My Graphs</a></li>
                                        </ul>
                                    </div>
                                </div><!-- parts -->
                            </div><!-- Left -->


                            <div id="Center">
                            <link class="include" rel="stylesheet" type="text/css" href="js/jqplot/js/jquery.jqplot.min.css" />
						    <link type="text/css" rel="stylesheet" href="js/jqplot/syntaxhighlighter/styles/shCoreDefault.min.css" />
						    <link type="text/css" rel="stylesheet" href="js/jqplot/syntaxhighlighter/styles/shThemejqPlot.min.css" />
						  
						  <!--[if lt IE 9]><script language="javascript" type="text/javascript" src="js/jqplot/js/excanvas.js"></script><![endif]-->
						    <script class="include" type="text/javascript" src="js/jqplot/js/jquery.min.js"></script>
                            	<!-- graph part LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL -->
                            	<div id="expRate" class="dparts homeRecentList">
									<div class="partsHeading"><h3>My graphs</h3></div>
										<div id="chart_div"></div>
								</div>

								<div id="levelRanking" class="parts panking">
									<div class="partsHeading"><h3>LevelRanking</h3></div>
									<div class="partsInfo" style="color: navy">
									<table>
									<tr>
									<td>
									TopRank
									<ol>
										<c:forEach items="${levelRanking}" var="levelRanking" end="4" varStatus="status">
                                            <li><strong><c:out value="${status.count} "/></strong><a href="<c:url value="/item"><c:param name="username" value="${levelRanking.nickname}" /></c:url>">${levelRanking.nickname}&nbsp;(${levelRanking.userLevel})</a></li>
                                        </c:forEach>
                                    </ol>
                                    </td>
                                    <td>
                                    YourRank
                                    <ol>
										<c:forEach items="${levelRanking}" var="levelRanking"  begin="${myLevelRankStart}" end="${myLevelRankEnd}"  varStatus="status">
										<c:if test="${status.count == 3}">
                                            <li><strong><c:out value="${status.index + 1} "/><a href="<c:url value="/item"><c:param name="username" value="${levelRanking.nickname}" /></c:url>">${levelRanking.nickname}&nbsp;(${levelRanking.userLevel})</a></strong></li>
                                        </c:if>
                                        <c:if test="${status.count != 3}">
                                            <li><strong><c:out value="${status.index + 1} "/></strong><a href="<c:url value="/item"><c:param name="username" value="${levelRanking.nickname}" /></c:url>">${levelRanking.nickname}&nbsp;(${levelRanking.userLevel})</a></li>
                                        </c:if>
                                        </c:forEach>
                                    </ol>
                                    </td>
                                    </tr>
                                    </table>
                                    </div>
								</div>

								<div id="uploadRanking" class="parts panking">
									<div class="partsHeading"><h3>UploadRanking</h3></div>
									<div class="partsInfo" style="color: navy">
									<table>
									<tr>
									<td>
									TopRank
									<ol>
										<c:forEach items="${uploadItemRanking}" var="uploadRanking" end="4" varStatus="status">
                                            <li><strong><c:out value="${status.count} "/></strong><a href="<c:url value="/item"><c:param name="username" value="${uploadRanking.nickname}" /></c:url>">${uploadRanking.nickname}&nbsp;(${uploadRanking.number})</a></li>
                                        </c:forEach>
                                    </ol>
                                    </td>
                                    <td>
                                    YourRank
                                    <ol>
										<c:forEach items="${uploadItemRanking}" var="uploadRanking"  begin="${myUploadRankStart}" end="${myUploadRankEnd}"  varStatus="status">
										<c:if test="${status.count == 3}">
                                            <li><strong><c:out value="${status.index + 1} "/><a href="<c:url value="/item"><c:param name="username" value="${uploadRanking.nickname}" /></c:url>">${uploadRanking.nickname}&nbsp;(${uploadRanking.number})</a></strong></li>
                                        </c:if>
                                        <c:if test="${status.count != 3}">
                                        	<li><strong><c:out value="${status.index + 1} "/></strong><a href="<c:url value="/item"><c:param name="username" value="${uploadRanking.nickname}" /></c:url>">${uploadRanking.nickname}&nbsp;(${uploadRanking.number})</a></li>
                                        </c:if>
                                        </c:forEach>
                                    </ol>
                                    </td>
                                    </tr>
                                    </table>
                                    </div>
								</div>

								<div id="uploadRanking" class="parts panking">
									<div class="partsHeading"><h3>AnswerRanking</h3></div>
									<div class="partsInfo" style="color: navy">
									<table>
									<tr>
									<td>
									TopRank
									<ol>
										<c:forEach items="${answerRanking}" var="answerRanking" end="4" varStatus="status">
                                            <li><strong><c:out value="${status.count} "/></strong><a href="<c:url value="/item"><c:param name="username" value="${answerRanking.nickname}" /></c:url>">${answerRanking.nickname}&nbsp;(${answerRanking.number})</a></li>
                                        </c:forEach>
                                    </ol>
                                    </td>
                                    <td>
                                    YourRank
                                    <ol>
										<c:forEach items="${answerRanking}" var="answerRanking" begin="${myAnswerRankStart}" end="${myAnswerRankEnd}" varStatus="status">
										<c:if test="${status.count == 3}">
                                            <li><strong><c:out value="${status.index + 1} "/><a href="<c:url value="/item"><c:param name="username" value="${answerRanking.nickname}" /></c:url>">${answerRanking.nickname}&nbsp;(${answerRanking.number})</a></strong></li>
                                        </c:if>
                                        <c:if test="${status.count != 3}">
                                        	<li><strong><c:out value="${status.index + 1} "/></strong><a href="<c:url value="/item"><c:param name="username" value="${answerRanking.nickname}" /></c:url>">${answerRanking.nickname}&nbsp;(${answerRanking.number})</a></li>
                                        </c:if>
                                        </c:forEach>
                                    </ol>
                                    </td>
                                    </tr>
                                    </table>
                                    </div>
								</div>


								<div id="quizRanking" class="parts panking">
									<div class="partsHeading"><h3>QuizRanking</h3></div>
									<div class="partsInfo" style="color: navy">
									<table>
									<tr>
									<td>
									TopRank
									<ol>
										<c:forEach items="${quizRanking}" var="quizRanking" end="4" varStatus="status">
                                            <li><strong><c:out value="${status.count} "/></strong><a href="<c:url value="/item"><c:param name="username" value="${quizRanking.nickname}" /></c:url>">${quizRanking.nickname}&nbsp;(${quizRanking.score})</a></li>
                                        </c:forEach>
                                    </ol>
                                    </td>
                                    <td>
                                    YourRank
                                    <ol>
										<c:forEach items="${quizRanking}" var="quizRanking" begin="${myQuizRankStart}" end="${myQuizRankEnd}" varStatus="status">
										<c:if test="${status.count == 3}">
                                            <li><strong><c:out value="${status.index + 1} "/><a href="<c:url value="/item"><c:param name="username" value="${quizRanking.nickname}" /></c:url>">${quizRanking.nickname}&nbsp;(${quizRanking.score})</a></strong></li>
                                        </c:if>
                                        <c:if test="${status.count != 3}">
                                        	<li><strong><c:out value="${status.index + 1} "/></strong><a href="<c:url value="/item"><c:param name="username" value="${quizRanking.nickname}" /></c:url>">${quizRanking.nickname}&nbsp;(${quizRanking.score})</a></li>
                                        </c:if>
                                        </c:forEach>
                                    </ol>
                                    </td>
                                    </tr>
                                    </table>
                                    </div>
								</div>
								
								


								<!--
								<div id="achievement" class="dparts homeRecentList">
									<div class="partsHeading"><h3>Achievements</h3></div>
								</div>
								-->


                            </div><!-- Center -->
                        </div><!-- Layout -->
                        <div id="sideBanner">
                        </div><!-- ContentsContainer -->
                    </div><!-- Contents -->
                    <c:import url="../include/footer.jsp" />
                </div><!-- Container -->
            </div><!-- Body -->
        </div>
        <script class="include" type="text/javascript" src="js/jqplot/js/jquery.jqplot.min.js"></script>
	    <script type="text/javascript" src="js/jqplot/syntaxhighlighter/scripts/shCore.min.js"></script>
	    <script type="text/javascript" src="js/jqplot/syntaxhighlighter/scripts/shBrushJScript.min.js"></script>
	    <script type="text/javascript" src="js/jqplot/syntaxhighlighter/scripts/shBrushXml.min.js"></script>
	<!-- End Don't touch this! -->
	
	<!-- Additional plugins go here -->
		  <script class="include" type="text/javascript" src="js/jqplot/plugins/jqplot.cursor.min.js"></script>
		  <script class="include" type="text/javascript" src="js/jqplot/plugins/jqplot.barRenderer.min.js"></script>
		  <script class="include" type="text/javascript" src="js/jqplot/plugins/jqplot.pieRenderer.min.js"></script>
		  <script class="include" type="text/javascript" src="js/jqplot/plugins/jqplot.ohlcRenderer.min.js"></script>
		  <script class="include" type="text/javascript" src="js/jqplot/plugins/jqplot.categoryAxisRenderer.min.js"></script>
		  <script class="include" type="text/javascript" src="js/jqplot/jquery-ui/js/jquery-ui.min.js"></script>
        
    </body>
</html>
