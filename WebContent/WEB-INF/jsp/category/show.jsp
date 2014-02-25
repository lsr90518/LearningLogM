<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../include/taglibs.jsp" %>
<html>
    <c:import url="../include/head.jsp">
        <c:param name="title" value="Category" />
    </c:import>

<body id="page_diary_list">
    <div id="Body">
        <div id="Container">
            <c:import url="../include/header.jsp" />
                <div id="Contents">
                    <div id="ContentsContainer">
                    	<c:import url="../include/profile_submenu.jsp" />
                        <div id="LayoutC" class="Layout">
                            <div id="Center">
                                <div id="groupSearchFormLine" class="parts searchFormLine">
                                </div>
                                <div class="dparts searchResultList">
                                	<div class="parts">
                                        <div class="pagerRelative">
                                        </div>
                                        <c:url value="/category/${category.id}/delete" var="categoryDelete" />
                            			<form:form id="categoryDeleteForm" method="post" action="${categoryDelete}" />
                                        <div class="block">
                                        	<ul>
                                        		<li style="font-family: arial,sans-serif; font-size: 20px; font-weight:900">${category.name}
                                        			<shiro:hasRole name="admin">
                                        			<button onclick="if(confirm('Delete it?'))$('#categoryDeleteForm').submit()" >Delete</button>
                                        			</shiro:hasRole>
                                        		</li>
                                        		<li>
                                        			<a href="<c:url value='/category' />">Top</a>&gt;
                                        			<c:forEach items="${categoryPath}" var="node">
														<a href="<c:url value='/category/${node.id}' />">${node.name}</a>&gt;
													</c:forEach>
                                        		</li>
                                        	</ul>
                                        </div>
                                        <div class="pagerRelative">
                                            <p class="number"><!--7件中 1～7件目を表示--></p>
                                        </div>
                                    </div>
                                </div>
                                <div class="dparts searchResultList">
                                	<div class="parts">
                                		<c:if test="${!empty category.children}">
                                        <div class="partsHeading">
                                            <h3>Categories</h3>
                                        </div>
                                        </c:if>
                                        <div class="pagerRelative">
                                            <a href="<c:url value="/category/${category.id}/add" />">Create a category</a>
                                            <p class="number"><!--7件中 1～7件目を表示--></p>
                                        </div>
                                        <c:if test="${!empty category.children}">
                                        <div class="block" style="font-size:large">
											<c:forEach items="${category.children}" var="cat">
												<a href="<c:url value='/category/${cat.id}' />">${cat.name}</a><br />
											</c:forEach>
                                        </div>
                                        <div class="pagerRelative">
                                            <p class="number"><!--7件中 1～7件目を表示--></p>
                                        </div>
                                        </c:if>
                                    </div>
                                    <c:if test="${!empty itemList}">
                                    <div class="parts">
										<div class="partsHeading">
                                            <h3>Objects</h3>
                                        </div>
										<div class="pagerRelative">
                                            <p class="number"><!--7件中 1～7件目を表示--></p>
                                        </div>
                                        <div class="block">
											<c:forEach items="${itemList}" var="item">
                                                <div class="ditem">
                                                    <div class="item">
                                                        <table>
                                                            <tbody>
                                                                <tr>
                                                                    <td rowspan="5" class="photo">
                                                                        <a href="<c:url value="/item/${item.id}" />">
                                                                            <c:choose>
                                                                                <c:when test="${empty item.image}">
                                                                                    <img height="70px" alt="" src="<c:url value="/images/no_image.gif" />" />
                                                                                </c:when>
                                                                                <c:otherwise>
                                                                                    <img height="70px" alt="" src="<tags:static filename="${item.image}_160x120.png" />" />
                                                                                </c:otherwise>
                                                                            </c:choose>
                                                                        </a>
                                                                        <br />
                                                                        <a href="<c:url value="/item/${item.id}" />">Details<img alt="photo" src="<c:url value="/images/icon_camera.gif" />" /></a>
                                                                    </td>
                                                                    <th>Title</th>
                                                                    <td>
                                                                    	<c:choose>
                                                                        <c:when test="${empty item.titles}">
                                                                            NO NAME
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                        <table>
                                                                        	<c:forEach items="${item.titles}" var="title">
                                                                                <tr>
                                                                                    <td style="width:70px;">${title.language.name}</td>
                                                                                    <td>${title.content}</td>
                                                                                </tr>
                                                                        	</c:forEach>
                                                                        </table>
                                                                        </c:otherwise>
                                                                        </c:choose>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <th>Author</th>
                                                                    <td>
                                                                    	<c:url var="authorItemsUrl" value="/item">
                                                                    		<c:param name="username" value="${item.author.nickname}" />
                                                                    	</c:url>
                                                                        <a href="${authorItemsUrl}">
                                                                            <c:out value="${item.author.nickname}" />
                                                                        </a>
                                                                        <c:if test="${!empty item.relogItem}">
                                                                        	<c:url var="relogItemUrl" value="/item/${item.relogItem.id}" />
                                                                        	<c:url var="relogItemAuthorUrl" value="/item"><c:param name="username" value="${item.relogItem.author.nickname}" /></c:url>
                                                                            &nbsp;(<a href="${relogItemUrl}">ReLog</a> from <a href="${relogItemAuthorUrl}"><c:out value="${item.relogItem.author.nickname}" /></a>)
                                                                        </c:if>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <th>Created time</th>
                                                                    <td>
                                                                        <fmt:formatDate type="both" dateStyle="full" timeStyle="short" value="${item.createTime}" />
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <th>Updated time</th>
                                                                    <td>
                                                                        <fmt:formatDate type="both" dateStyle="full" timeStyle="short" value="${item.updateTime}" />
                                                                    </td>
                                                                </tr>
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>
                                        <div class="pagerRelative">
                                            <p class="number"><!--7件中 1～7件目を表示--></p>
                                        </div>
                                    </div>
                                    </c:if>
                                </div>
                            </div><!-- Center -->
                        </div><!-- Layout -->
                        <div id="sideBanner">
                            <%--
                            <form action="/member/changeLanguage" method="post"><label for="language_culture">言語</label>:
                                <select name="language[culture]" onchange="submit(this.form)" id="language_culture">

                                    <option value="en">English</option>
                                    <option value="ja_JP" selected="selected">日本語 (日本)</option>
                                </select><input value="diary/index" type="hidden" name="language[next_uri]" id="language_next_uri" /></form>
                            --%>
                        </div><!-- sideBanner -->
                    </div><!-- ContentsContainer -->
                </div><!-- Contents -->

                <c:import url="../include/footer.jsp" />
            </div><!-- Container -->
        </div><!-- Body -->
    </body>
</html>