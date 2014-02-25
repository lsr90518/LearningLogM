<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../include/taglibs.jsp" %>
	<link rel="stylesheet" type="text/css" media="screen" href="${baseURL}/css/nyroModal.css" />
		<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
	    <script src="${baseURL}/js/jquery/jquery.nyroModal.custom.min.js"></script>
		<!--[if IE 6]>
			<script type="text/javascript" src="${baseURL}/js/jquery/jquery.nyroModal-ie6.min.js"></script>
		<![endif]-->
		<script>
	            $(function() {
	            	  $('.nyroModal').nyroModal();
            	});
    </script>
    <link rel="stylesheet" type="text/css" media="screen" href="${baseURL}/js/jquery/stars/jquery.ui.stars.min.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="${baseURL}/js/mediaelement/mediaelementplayer.min.css" />

<div style="font-family: arial,meiryo,simsun,sans-serif;font-size: 22px;  word-wrap: break-word;  word-break: normal; font-weight:bold; color: gray; margin-bottom:10px;">
	<img src="<c:url value='/images/system-help.png' />" alt="●"/>Which image can be linked to <br/>&nbsp;&nbsp;&nbsp;&nbsp;<span style="color:black">${quiz.content}</span>?
</div>
<table style="border-style: none;">
	<c:forEach items="${quiz.quizChoices}" var="choice" varStatus="status">
		<c:choose>
			<c:when test="${!answered}">
				<c:if test="${status.index%2==0}">
					<tr>
				</c:if>
				<td width="5%" style="text-align: center;"><input
					id="answeroption${status.index}" type="radio" name="answer"
					value="${choice.number}"
					<c:if test= "${status.index == 0}"> checked="checked"</c:if> /></td>
				<td style="text-align: center; padding: 10px;">
				  		 <c:choose>
                             	<c:when test="${choice.item.image.fileType eq 'image'}">
                                  <p class="photo">
                                      <a id="itemImage" class="nyroModal"  href="<tags:static filename="${choice.content}_800x600.png" />">
                                     	<img alt="" src="<tags:static filename="${choice.content}_320x240.png" />" width="240px" style="max-width:95%;height:auto;" />
                                     </a>
                                  </p>
                                 </c:when>
                                
                                 <c:when test="${choice.item.image.fileType eq 'video'}">
                                 	<video id="itemvideo" class="video"
                                 		width="240px" controls preload="auto"
                                 		poster=" <tags:static filename="${choice.content}_320x240.png" />"
                                 		style="background-color:black"
                                 		onclick="this.play();">
                                 		<source src=" <tags:static filename="${choice.content}_320x240.mp4" />"></source>
                                 	</video>
                                 </c:when>
                                 <c:when test="${choice.item.image.fileType eq 'audio'}">
                                 		<audio controls style="width:240px">
                                 			<source src=" <tags:static filename="${choice.content}.mp3" />"></source>
                                 		</audio>
                                 </c:when>
                                 <c:when test="${choice.item.image.fileType eq 'pdf'}">
                                 	<p class="photo">
                                 		<a id="itemImage" href=" <tags:static filename="${choice.content}.pdf" />">
                                         <img alt="" src=" <tags:static filename="${choice.content}_320x240.png" />" width="240px" />
                                      </a>
                                 	</p>
                                 </c:when>
                                 <c:otherwise>
                                 	<p class="photo">
                                 		<img width="240px" alt="" src="<c:url value="/images/no_image.gif" />" />
                                 	</p>
                                 </c:otherwise>
                             </c:choose>
				</td>
				<c:if test="${status.index%2==1}">
					</tr>
				</c:if>
			</c:when>
			<c:otherwise>
				<c:if test="${status.index%2==0}">
					<tr>
				</c:if>
				<td style="padding: 10px;">
					<c:if test="${rightanswer == status.index+1}">
						<img src="<c:url value='/images/right_icon.png' />" alt="(Right)" />
					</c:if> 
					<c:if test="${!result && youranswer == status.index+1}">
						<img src="<c:url value='/images/wrong_icon.png' />" alt="(Wrong)" />
					</c:if> 
					<span style=" font-size: 13px; font-weight: bold">
						${choice.note}
					</span>
					<c:url value="/item/${choice.item.id}" var="itemurl" />
					<c:choose>
                             	<c:when test="${choice.item.image.fileType eq 'image'}">
                                  <p class="photo">
                                  	<a href="${itemurl}" target="_blank">	
                                     	<img alt="" src="<tags:static filename="${choice.content}_320x240.png" />" width="240px" style="max-width:90%;height:auto;" />
                                    </a> 
                                  </p>
                                 </c:when>
                                 <c:when test="${choice.item.image.fileType eq 'video'}">
                                 	<video id="itemvideo" class="video"
                                 		width="240px" controls preload="auto"
                                 		poster="<tags:static filename="${choice.content}_320x240.png" />"
                                 		style="background-color:black"
                                 		onclick="this.play();">
                                 		<source src="<tags:static filename="${choice.content}_320x240.mp4" />"></source>
                                 	</video>
                                 </c:when>
                                 <c:when test="${choice.item.image.fileType eq 'audio'}">
                                 		<audio controls style="width:240px">
                                 			<source src="<tags:static filename="${choice.content}.mp3" />" ></source>
                                 		</audio>
                                 </c:when>
                                 <c:when test="${choice.item.image.fileType eq 'pdf'}">
                                 	<p class="photo">
                                 		<a id="itemImage" href="<tags:static filename="${choice.content}.pdf" />">
                                         <img alt="" src="<tags:static filename="${choice.content}_320x240.png" />" width="240px" />
                                      </a>
                                 	</p>
                                 </c:when>
                                 <c:otherwise>
                                 	<p class="photo">
                                 		<img width="240px" alt="" src="<c:url value="/images/no_image.gif" />" />
                                 	</p>
                                 </c:otherwise>
                             </c:choose>
				<c:if test="${status.index%2==1}">
					</tr>
				</c:if>
			</c:otherwise>
		</c:choose>
	</c:forEach>
</table>
<c:choose>
	<c:when test= "${!answered}">
		<div style="margin-top:30px;text-align:center">
		<input type="submit" class="buttonSubmit" value="Answer"/>
		<input type="submit" class="buttonSubmit" value="Too Easy" onclick="return fncEasy();"/>
		<input type="submit" class="buttonSubmit" value="Too Difficult" onclick="return fncDifficult();"/>
		<input type="button" class="buttonSubmit" value="No Good" onclick="return fncPass();"/>
		</div>
	</c:when>
	<c:otherwise>
    <div style="display:block; border: 8px ridge orange; width: 250px; height: 80px;margin-left: 110px; font-family: arial; font-size: 22px; font-weight: bold;">
    	<a href="<c:url value="/quiz" />" style="TEXT-DECORATION:none">
   		<span style="word-break: normal;color:<c:if test='${result}'>green</c:if><c:if test='${!result}'>red</c:if>">
   			${comment}
   		</span>
   		</a>
   	</div>
       	<div style="margin-top: -30px;">
       		<a href="<c:url value="/quiz" />" >
       			<img src="<c:url value='/images/${faceicon}.png' />" alt="" />
       		</a>
       	</div>
        <div style="text-align:center">
      	<input type="button" class="buttonSubmit" value="More Quiz" onclick="return fncMore();"/> 	
      	</div>
     </c:otherwise>
</c:choose>