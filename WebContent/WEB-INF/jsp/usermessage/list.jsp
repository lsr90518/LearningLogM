<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../include/taglibs.jsp" %>
<!doctype html>
<html>
    <c:import url="../include/head.jsp">
        <c:param name="title" value="Messages" />
        <c:param name="javascript">
        	<c:url value="/usermessage/send" var="usermessagesenduri"/>
        	<script type="text/javascript">
        		$(function(){
        			$("#msgbox").dialog({
        				autoOpen:false,
        				modal:true
        			});
        		});
        		function showMsg(target, msgId){
        			$.post("<c:url value="/usermessage/readmessage" />",{'msgId':msgId} , function(data){
        				if(typeof(data.message)!="undefined"){
	        				refreshUnreadMsg();
	        				$(target).parent().removeClass("unreadMsg");
	        				$("#msgboxcontent").html(data.message.content.replace(/\\n/g,"<br />"));
	        				$("#msgboxreply").attr("onclick", "replyMsg('"+data.message.sendFromId+"','"+data.message.sendFromName+"')");
	        				$("#msgbox").dialog( "option", "title", "Message from "+data.message.sendFromName).dialog("open");
        				}
        			},"json");
        		}

        		function replyMsg(sendToUid, sendToUname){
        			$("#msgbox").dialog("close");
	            	$("<div id=\"usermsgdlg\">"
			                +"<div><form id=\"usermessageform\" action=\"#\" method=\"post\">"
			            	+"<input id=\"umsg_sendto\" name=\"umsg_sendto\" type=\"hidden\" value=\""+sendToUid+"\" />"
			            	+"<textarea id=\"umsg_content\" name=\"content\" style=\"width:100%;height:100%;\" rows=\"5\"></textarea>"
			            	+"<input type=\"submit\" value=\"Send\" onclick=\"sendUserMessage();return false;\"/>"
					        +"</form></div>"
					        +"</div>").dialog({
					        	title:"Send A Message To <span style=\"color:green\">"+sendToUname+"</span>",
					        	modal:true,
					        	resizable:false});
        		}
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
                                                <img alt="LearningUser" src="${ctx}/images/no_image.gif" height="180" width="180" />
                                            </c:when>
                                            <c:otherwise>
                                                <img alt="LearningUser" src="<tags:static filename="${user.avatar}_320x240.png" />" width="180" />
                                            </c:otherwise>
                                        </c:choose>
                                    </p>
                                    <p class="text"><shiro:principal property="nickname" /></p>
                                    <p class="text">Level : ${user.userLevel}</p>
                                    <p class="text">EXP : ${nowExperiencePoint} / Next : ${nextExperiencePoint}</p>
                                </div><!-- parts -->
                            </div><!-- Left -->
                            <div id="Center">
                                <div id="homeRecentList_11" class="dparts homeRecentList"><div class="parts">
                                        <div class="partsHeading">
                                            <h3>Messages</h3>
                                        </div>
                                        <div class="block">
                                            <ul class="articleList">
                                                <c:forEach items="${messageList}" var="msg">
                                                    <li <c:if test="${!msg.readFlag}">class="unreadMsg"</c:if>><a class="msgitem" href="#" onclick="showMsg(this, '${msg.id}')" ><fmt:formatDate value="${msg.createTime}"  type="both" dateStyle="default" /> from ${msg.sendFromUser.nickname}</a></li>
                                                </c:forEach>
                                            </ul>
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
        <div id="msgbox">
        	<div id="msgboxcontent"></div>
        	<button id="msgboxreply">Reply</button>
        </div>
    </body>
</html>
