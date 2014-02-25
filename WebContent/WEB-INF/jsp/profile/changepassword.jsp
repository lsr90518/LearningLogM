<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../include/taglibs.jsp" %>
<!doctype html>
<html>
    <c:import url="../include/head.jsp">
        <c:param name="title" value="Change Password" />
    </c:import>
    <body id="profile_edit">
        <div id="Body">
            <div id="Container">
                <c:import url="../include/header.jsp" />
                <div id="Contents">
                    <div id="ContentsContainer">
                    	<c:import url="../include/profile_submenu.jsp"></c:import>
                        <div id="localNav">
                        </div><!-- localNav -->
                        <div id="LayoutA" class="Layout">
                            <div id="Top">
                            </div><!-- Top -->
                            <div id="Left">
                                <div id="memberImageBox_22" class="parts memberImageBox">
                                    <p class="photo">
                                        <img alt="LearningUser" src="<tags:static filename="${user.avatar}_320x240.png" />" /></p>
                                    <p class="text"><shiro:principal property="nickname" /></p>
                                    <p class="text">Level : ${user.userLevel}</p>
                                    <p class="text">EXP : ${nowExperiencePoint} / Next : ${nextExperiencePoint}</p>

                                    <div class="moreInfo">
                                        <ul class="moreInfo">
                                            <li><a href=" <c:url value="/profile/avataredit"/>">Edit photo</a></li>
                                        </ul>
                                    </div>
                                </div><!-- parts -->
                            </div><!-- Left -->
                             <c:url value="/profile/changepassword" var="profileEditFormUrl" />
                             <form:form modelAttribute="form" action="${profileEditFormUrl}" method="post">
                                 <div id="Center" class="parts">
                                       <div class="partsHeading"><h3>Change password</h3></div>
                                 <input type="hidden" name="userid" value="${user.id}"/>
                                            <table>
                                                <tr>
                                                    <th><label for="password">Current password</label></th>
                                                    <td>
                                                        <form:password path="oldpassword" />
                                                        <form:errors path="oldpassword" cssClass="error" />
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <th><label for="password">New password</label></th>
                                                    <td>
                                                        <form:password path="password" />
                                                        <form:errors path="password" cssClass="error" />
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <th><label for="passwordConfirm">New password(Again)</label></th>
                                                    <td>
                                                        <form:password path="passwordConfirm" />
                                                        <form:errors path="passwordConfirm" cssClass="error" />
                                                    </td>
                                                </tr>
                                            </table>
                                            <div class="operation">
                                                <ul class="moreInfo button">
                                                    <li>
                                                        <input type="submit" class="input_submit" value="Update" />
                                                    </li>
                                                </ul>
                                            </div>
                                  </div>
                                        </form:form>
                        </div><!-- Layout -->
                        <div class="block">
                        </div>
                        <div id="sideBanner">
                        </div><!-- sideBanner -->

                    </div><!-- ContentsContainer -->
                </div><!-- Contents -->
                <c:import url="../include/footer.jsp" />
            </div><!-- Container -->
        </div><!-- Body -->
    </body>
</html>
