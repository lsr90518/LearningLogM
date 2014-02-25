<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../include/taglibs.jsp" %>
<!doctype html>
<html>
    <c:import url="../include/head.jsp">
        <c:param name="title" value="Profile" />
    </c:import>

    <body id="page_member_home">
        <div id="Body">
            <div id="Container">
                <c:import url="../include/header.jsp" />
                <div id="Contents">
                    <div id="ContentsContainer">
						<c:import url="../include/profile_submenu.jsp">
						</c:import>
                        <div id="LayoutA" class="Layout">
                            <div id="Left">
                                <div id="memberImageBox_22" class="parts memberImageBox">

                                    <p class="photo">
                                        <c:choose>
                                            <c:when test="${empty user.avatar}">
                                                <img alt="LearningUser" src="<c:url value="/images/no_image.gif" />" height="180" width="180" />
                                            </c:when>
                                            <c:otherwise>
                                                <img alt="LearningUser" src="<tags:static filename="${user.avatar}_320x240.png" />" height="180" />
                                            </c:otherwise>
                                        </c:choose>
                                    </p>
                                    <p class="text"><shiro:principal property="nickname" /></p>
                                    <p class="text">Level : ${user.userLevel}</p>
                                    <p class="text">EXP : ${nowExperiencePoint} / Next : ${nextExperiencePoint}</p>

                                    <div class="moreInfo">
                                        <ul class="moreInfo">
                                            <li><a href=" <c:url value="/profile/avataredit"/>">Edit Photo</a></li>
                                        </ul>
                                    </div>
                                </div><!-- parts -->
                            </div><!-- Left -->
                            <div id="Center">
                                <div class="partsHeading"><h3>Profile</h3>
                                    &nbsp;<a href="<c:url value="/profile/edit" />">Edit</a>
                                </div>
                                <div class="parts">
                                    <c:url value="/signup/${user.activecode}" var="signupFormUrl" />
                                    <table>
                                        <tr>
                                            <th><label for="pcEmail">Email</label></th>
                                            <td>${user.pcEmail}</td>
                                        </tr>
                                        <tr>
                                            <th><label for="nickname">Nickname</label></th>
                                            <td>${user.nickname}</td>
                                        </tr>
                                        <tr>
                                            <th><label for="firstName">Family Name</label></th>
                                            <td>
                                                ${user.firstName}
                                            </td>
                                        </tr>
                                        <tr>
                                            <th><label for="lastName">Given Name</label></th>
                                            <td>
                                                ${user.lastName}
                                            </td>
                                        </tr>
                                        <tr>
                                            <th><label for="interesting">Interests</label></th>
                                            <td>
                                                ${form.interesting}
                                            </td>
                                        </tr>
                                        <c:forEach items="${myLangs}" var="myLan" varStatus="lan">
                                            <tr>
                                                <th>
                                                    Native language(${lan.count})
                                                </th>
                                                <td>
                                                    ${myLan.name}
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        <c:forEach items="${studyLangs}" var="slan"  varStatus="status">
                                            <tr>
                                                <th>
                                                    <label>Language of study(${status.count})</label>
                                                </th>
                                                <td>
                                                    ${slan.name}
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        <tr>
                                            <th>
                                                <label>Categories</label>
                                            </th>
                                            <td>
                                            	<ul>
                                            	<c:forEach items="${myCategoryList}" var="cat">
                                            		<li <c:if test="${user.defaultCategory!=null && user.defaultCategory==cat.id}">style="font-weight:bold"</c:if>>
                                            			${cat.name}
                                            		</li>
                                            	</c:forEach>
                                            	</ul>
                                            </td>
                                        </tr>
                                    </table>
                                </div><!-- parts -->
                            </div><!-- Center -->
                        </div><!-- Layout -->
                        <div id="sideBanner">
                        </div><!-- ContentsContainer -->
                    </div><!-- Contents -->
                    <c:import url="../include/footer.jsp" />
                </div>
            </div><!-- Container -->
        </div><!-- Body -->
    </body>
</html>
