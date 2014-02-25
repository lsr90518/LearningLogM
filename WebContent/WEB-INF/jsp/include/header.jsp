<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<div id="Header">
    <div id="HeaderContainer">
        <h1><a href="http://ll.is.tokushima-u.ac.jp/${projectName}">Learning Log</a></h1>
        <div id="globalNav">
            <ul>
                <li class="<c:if test="${params.currentModule=='home'}">currentModule</c:if>"><a href="${ctx}/">Home</a></li>
                <li class="<c:if test="${params.currentModule=='myhome'}">currentModule</c:if>"><a href="${ctx}/member">My Logs</a></li>
                <li class="<c:if test="${params.currentModule=='item'}">currentModule</c:if>"><a href="${ctx}/item">All Logs</a></li>
                <li class="<c:if test="${params.currentModule=='forum'}">currentModule</c:if>"><a href="${ctx}/quiz">Quiz</a></li>
                <li class="<c:if test="${params.currentModule=='usermessage'}">currentModule</c:if>"><a href="${ctx}/usermessage">Message<span id="msgUnreadCount"></span></a></li>
                <li class="<c:if test="${params.currentModule=='learnighabit'}">currentModule</c:if>"><a href="${ctx}/myhabit">Habit</a></li>
                 <li class="<c:if test="${params.currentModule=='task'}">currentModule</c:if>"><a href="${ctx}/task/taskitem">Task</a></li>
                
                <%-- <li class="<c:if test="${params.currentModule=='mystatus'}">currentModule</c:if>"><a href="${ctx}/status">Status</a></li> --%>
                <li class="<c:if test="${params.currentModule=='pacall'}">currentModule</c:if>"><a href="${ctx}/pacall" target="_blank">PACALL</a></li>
                <shiro:hasRole name="admin">
                      <li class="<c:if test="${params.currentModule=='admin'}">currentModule</c:if>"><a href="<c:url value="/admin" />">Admin</a></li>
                </shiro:hasRole>
                 <li class="<c:if test="${params.currentModule=='profile'}">currentModule</c:if>"><a href="<c:url value="/profile" />">Setting</a></li>
                <li><a href="<c:url value="/logout" />">Logout</a></li>
            </ul>
        </div><!-- globalNav -->

        <div id="topBanner">
        </div>
    </div><!-- HeaderContainer -->
</div><!-- Header -->
