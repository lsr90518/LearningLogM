<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@include file="../include/taglibs.jsp" %>
<!doctype html>
<html>
    <c:import url="../include/head.jsp">
        <c:param name="title" value="My Logs" />
        <c:param name="javascript">
        	<script type="text/javascript">
        	$(function(){
        		$("#jumpTimemap").click(function(){
        			window.open("${ctx}/timemap/mylogs");
        		});
        		$("#jumpLogstate").click(
        				function(){
        					window.open("${ctx}/logstate");		
        				}
        		);
        	});
        	</script>
		</c:param>
    </c:import>

    <body id="page_member_home">
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
                                            <li><a href="<c:url value="/profile"/>">My Profile</a></li>
                                            <li><a href="<c:url value="/recommend/${user.nickname}/getSuggestion"/>">Learning Suggestion</a></li>
                                        </ul>
                                    </div>
                                </div><!-- parts -->
                                <div id="timemapBox" class="parts memberImageBox" style="text-align: center">
									<button id="jumpTimemap">Go to TimeMap</button>
                                </div>
                                <div id="timemapBox" class="parts memberImageBox" style="text-align: center">
									<button id="jumpLogstate">My Learning Log States</button>
                                </div>
                            </div><!-- Left -->
                            <div id="Center">
                                <div id="homeRecentList_11" class="dparts homeRecentList"><div class="parts">
                                        <div class="partsHeading">
                                            <h3>My Logs</h3>
                                            <a class="addlink" href="<c:url value="/item/add" />">Add new object</a>
                                        </div>
                                        <div class="block">
                                            <ul class="articleList">
                                                <c:forEach items="${myitems.content}" var="myitem">
                                                    <li><span class="date"><fmt:formatDate value="${myitem.createTime}"  type="date" pattern="yyyy/MM/dd" /></span><a href="<c:url value = "/item/${myitem.id}"/>">${myitem.defaultTitle}</a>
                                                    </li>
                                                    </c:forEach>
                                            </ul>
                                            <div class="moreInfo">
                                                <ul class="moreInfo">
                                                    <li><a href="<c:url value="/item"><c:param name="username" value="${user.nickname}" /></c:url>">More</a></li>
                                                </ul>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div id="homeRecentList_12" class="dparts homeRecentList"><div class="parts">
                                        <div class="partsHeading">
                                            <h3>Answered by Me</h3>
                                        </div>
                                        <div class="block">
                                            <ul class="articleList">
                                                <c:forEach items="${answeritems.content}" var="answeritem">
                                                    <li><span class="date"><fmt:formatDate value="${answeritem.createTime}"  type="date" pattern="yyyy/MM/dd" /></span>
                                                        <a href="<c:url value = "/item/${answeritem.id}"/>">${answeritem.defaultTitle}</a>(${answeritem.author.nickname})  </li>
                                                    </c:forEach>
                                            </ul>
                                            <div class="moreInfo">
                                                <ul class="moreInfo">
                                                    <li><a href="<c:url value="/item"><c:param name="answeruserId" value="${user.id}" /></c:url>">More</a></li>
                                                </ul>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div><!-- Center -->
                        </div><!-- Layout -->
                        <div id="sideBanner">
                        </div><!-- ContentsContainer -->
                    </div><!-- Contents -->
                    <c:import url="../include/footer.jsp" />
                </div><!-- Container -->
            </div><!-- Body -->
        </div>
    </body>
</html>
