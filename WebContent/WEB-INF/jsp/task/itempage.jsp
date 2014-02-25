<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="pageParameter" class="java.util.LinkedHashMap" />
<c:set target="${pageParameter}" property="title" value="${task.title}" />
<c:set target="${pageParameter}" property="place" value="${task.place}" />
<c:set target="${pageParameter}" property="level" value="${task.level}" />
<c:set var="shownPageNum" value="8" />
<c:set var="startPage" value="1" />
<c:set var="endPage" value="${itemPage.totalPages}" />
<c:if test="${itemPage.totalPages>shownPageNum}">
	<c:set var="startPage" value="${itemPage.pageNo-(shownPageNum-shownPageNum%2)/2}" />
	<c:set var="endPage" value="${startPage+shownPageNum-1}" />

	<c:if test="${startPage<1}">
		<c:set var="pageOffset" value="${1-startPage}" />
		<c:set var="startPage" value="${startPage+pageOffset}" />
		<c:set var="endPage" value="${endPage+pageOffset}" />
	</c:if>
	<c:if test="${endPage>itemPage.totalPages}">
		<c:set var="pageOffset" value="${endPage-itemPage.totalPages}" />
		<c:set var="startPage" value="${startPage-pageOffset}" />
		<c:set var="endPage" value="${endPage-pageOffset}" />
	</c:if>
</c:if>

<c:if test="${itemPage.number+1>1}">
		<c:url var="prevUrl" value="/task/taskitem">
		           <c:param name="page" value="1" />
					<c:forEach items="${pageParameter}" var="p">
						<c:if test="${!empty p.value}">
						<c:param name="${p.key}" value="${p.value}" />
						</c:if>
					</c:forEach>
       </c:url>
    <a href="${prevUrl}">&laquo;</a>
</c:if>
<c:forEach begin="${startPage}" end="${endPage}" var="pageNo">
    <c:choose>
        <c:when test="${pageNo eq itemPage.number+1}">
            <span class="now-page">${pageNo}</span>
        </c:when>
        <c:otherwise>
	        <c:url var="pageNumUrl" value="/task/taskitem">
		            <c:param name="page" value="${pageNo}" />
					<c:forEach items="${pageParameter}" var="p">
						<c:if test="${!empty p.value}">
						<c:param name="${p.key}" value="${p.value}" />
						</c:if>
					</c:forEach>
            </c:url>
            <a href="${pageNumUrl}">${pageNo}</a>
        </c:otherwise>
    </c:choose>
</c:forEach>
<c:if test="${itemPage.number+1<itemPage.totalPages}">
	<c:url var="nextUrl" value="/task/taskitem">
		<c:param name="page" value="${itemPage.totalPages}" />
		<c:forEach items="${pageParameter}" var="p">
			<c:if test="${!empty p.value}">
				<c:param name="${p.key}" value="${p.value}" />
			</c:if>
		</c:forEach>
	</c:url>
    <a href="${nextUrl}">&raquo;</a>
</c:if>
<a href="#">${itemPage.number+1}/${itemPage.totalPages}</a>