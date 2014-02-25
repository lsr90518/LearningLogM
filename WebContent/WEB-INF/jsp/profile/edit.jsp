<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../include/taglibs.jsp" %>
<!doctype html>
<html>
    <c:import url="../include/head.jsp">
        <c:param name="title" value="Profile" />
    </c:import>
    <body id="profile_edit">
        <div id="Body">
            <div id="Container">
                <c:import url="../include/header.jsp" />
                <div id="Contents">
                    <div id="ContentsContainer">
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
                                            <li><a href="${ctx}/profile/avataredit">Edit photo</a></li>
                                        </ul>
                                    </div>
                                </div><!-- parts -->
                            </div><!-- Left -->
                             <c:url value="/profile/edit" var="profileEditFormUrl" />
                             <form:form modelAttribute="form" action="${profileEditFormUrl}" method="post">
                                 <div id="Center" class="parts">
                                       <div class="partsHeading"><h3>Edit profile</h3></div>
                                 <input type="hidden" name="userid" value="${user.id}"/>
                                            <strong>*</strong>&nbsp;Required。
                                            <table>
                                                <tr>
                                                    <th><label for="pcEmail">PC E-mail:</label></th>
                                                    <td>${user.pcEmail}</td>
                                                </tr>
                                                <tr>
                                                    <th><label for="nickname">Nickname</label> <strong>*</strong></th>
                                                    <td>
                                                        <form:input path="nickname" cssClass="input_text" id="nickname" />
                                                        <form:errors path="nickname" cssClass="error" />
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <th><label for="firstName">First name</label></th>
                                                    <td>
                                                        <form:input path="firstName" />
                                                        <form:errors path="firstName" cssClass="error" />
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <th><label for="lastName">Last name</label></th>
                                                    <td>
                                                        <form:input path="lastName" />
                                                        <form:errors path="lastName" cssClass="error" />
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <th><label for="interesting">Interests</label></th>
                                                    <td>
                                                        <form:textarea rows="4" cols="30" path="interesting" id="interesting" />
                                                        <div><form:errors path="interesting" cssClass="error" /></div>
                                                    </td>
                                                </tr>

                                                <c:forEach begin="0" end="1" var="myLangsIndex" varStatus="status">
                                                    <tr>
                                                        <th>
                                                            <label for="myLangs[${status.index}]">Native language&nbsp;${status.count}</label>
                                                            <c:if test="${status.index eq 0}"><strong>*</strong></c:if>
                                                        </th>
                                                        <td>
                                                            <form:select path="myLangs[${myLangsIndex}]">
                                                                <option value="">-Please select-</option>
                                                                <form:options  items="${langList}" itemValue="code" itemLabel="name" />
                                                            </form:select>
                                                            <form:errors path="myLangs[${myLangsIndex}]" cssClass="error" />
                                                        </td>
                                                    </tr>
                                                </c:forEach>


                                                <c:forEach begin="0" end="1" var="studyLangsIndex" varStatus="st">
                                                    <tr>
                                                        <th>
                                                           <label for="studyLangs[${st.index}]">Language of study&nbsp;${st.count}</label>
                                                            <c:if test="${0 eq st.index}"><strong>*</strong></c:if>
                                                        </th>
                                                        <td>
                                                            <form:select path="studyLangs[${st.index}]">
                                                                <option value="">-Please select-</option>
                                                                <form:options  items="${langList}" itemValue="code" itemLabel="name" />
                                                            </form:select>
                                                            <form:errors path="studyLangs[${st.index}]" cssClass="error" />
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </table>
                                            <div class="operation">
                                                <ul class="moreInfo button">
                                                    <li>
                                                        <input type="submit" class="input_submit" value="Update" />
                                                    </li>
                                                    <li>
                                                    	<a href="<c:url value="/profile" />">Return</a>
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
