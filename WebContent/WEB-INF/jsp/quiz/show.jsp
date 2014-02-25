<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../include/taglibs.jsp" %>
<!doctype html>
<html>
	<script type="text/javascript">
	function fncPass() {
		$("#pass").val("1");
		$("#form").submit();
	}
	function fncEasy() {
		$("#pass").val("2");
		$("#form").submit();
	}
	function fncDifficult() {
		$("#pass").val("3");
		$("#form").submit();
	}
	
	function fncMore() {
		var myform  = document.getElementById("form");
		
		myform.method="get";
		myform.submit();  
	}

	function resizeImgX(img,iwidth,iheight)
	{
	   var _img = new Image();
	   _img.src = img.src;
	   if(_img.width > _img.height)
	   {
	      img.width = (_img.width > iwidth) ? iwidth : _img.width;
	      img.height = (_img.height / _img.width) * img.width;
	   }
	   else if(_img.width < _img.height)
	   {
	      img.height = (_img.height > iheight) ? iheight : _img.height;
	      img.width = (_img.width / _img.height) * img.height ;
	   }
	   else
	   {
	      img.height = (_img.height > iheight) ? iheight : _img.height;
	      img.width = (_img.width > iwidth) ? iwidth : _img.width;
	   }
	}
	
</script>

    <c:import url="../include/head.jsp">
        <c:param name="javascript">
            <script type="text/javascript" src="<c:url value="/js/jquery/fancyzoom/js/fancyzoom.min.js" />"></script>
            <script type="text/javascript">
                $(function(){
                    $('a.zoom').fancyZoom({
                        directory:'<c:url value="/js/jquery/fancyzoom/images" />'
                    });
                });
            </script>
        </c:param>
    </c:import>
    <body id="page_member_profile">
        <div id="Body">
      		 <div id="Container">
          		 <c:import url="../include/header.jsp" />
                 <div id="Contents">
                   	<div id="ContentsContainer">
                      	<div id="LayoutA" class="Layout">
                            <div id="Left">
                      		 <c:if test="${!empty quiz}">
                      		      <c:if test="${quiz.questiontypeid!=2}">
                                <div id="memberImageBox_30" class="parts memberImageBox">
	                                    <p class="photo">
	                                        <a id="itemImage" class="zoom" href="#itemImageZoom">
	                                            <c:choose>
	                                                <c:when test="${empty quiz.item.image}">
	                                                    <img width="240px" alt="" src="<c:url value="/images/no_image.gif" />" />
	                                                </c:when>
	                                                <c:otherwise>
	                                                    <img alt="" src="<tags:static filename="${quiz.item.image}_320x240.png" />" width="240px" />
	                                                </c:otherwise>
	                                            </c:choose>
	                                        </a>
	                                    </p>
	                                    <div id="itemImageZoom">
	                                        <c:choose>
	                                            <c:when test="${empty quiz.item.image}">
	                                                <img width="240px" alt="" src="<c:url value="/images/no_image.gif" />" />
	                                            </c:when>
	                                            <c:otherwise>
	                                                <img alt="" src="<tags:static filename="${quiz.item.image}_800x600.png" />" />
	                                            </c:otherwise>
	                                        </c:choose>
	                                    </div><!-- itemImageZoom -->
	                                    <p class="text"></p>
	                                    <div class="moreInfo">
	                                        <ul class="moreInfo">
	                                            <li><a class="zoom" href="#itemImageZoom">もっと写真を見る</a></li>
	                                        </ul>
	                                    </div><!-- moreInfo -->
									</div><!-- memberImageBox_30 -->
									</c:if>
									<c:if test="${!empty quiz.item.itemLat && !empty quiz.item.itemLng && !empty quiz.item.itemZoom}">
	                                <div id="mapbox" class="dparts nineTable">
                                    <div class="parts">
                                        <div class="partsHeading"><h3>Map</h3></div>
                                        <table>
                                            <tr>
                                                <td>
                                                    <div id="map" style="height: 200px;">
                                                        <img src="http://maps.google.com/maps/api/staticmap?size=260x200&sensor=false&center=${quiz.item.itemLat},${quiz.item.itemLng}&zoom=${quiz.item.itemZoom}&mobile=true&markers=${quiz.item.itemLat},${quiz.item.itemLng}" />
                                                    </div>
                                                </td>
                                            </tr>
                                            <c:if test="${!empty quiz.item.place}"><tr><td>Place：${quiz.item.place}</td></tr></c:if>
                                        </table>
                                    </div><!-- parts -->
                                </div><!-- dparts -->
                                </c:if>
                                </c:if>
                            </div><!-- Left -->
                            <div id="Center">
                                <div class="dparts homeRecentList">
                                    <div class="parts">
                                        <div class="partsHeading"><h3>Quiz</h3></div>
                                          <c:url value="/quiz" var="url" />
                                         <c:if test="${!empty quiz}">
                                          <form:form commandName="form" action="${url}" method="post" id="form">
                                          	 <form:hidden path="language"/>
                                          	 <form:hidden path="pass" id="pass"/>
                                          	 <input name="alarmtype" type="hidden" value="8"/>
                                             <input type="hidden" name="quizid" value="${quiz.id}"></input>
                                                <c:if test="${quiz.questiontypeid == 1}">
                                             		 <c:import url="textchoice.jsp" />
                                                </c:if>	
                                                <c:if test="${quiz.questiontypeid == 2}">
                                               		<c:import url="imagechoice.jsp" />
                                                </c:if>
                                                <c:if test="${quiz.questiontypeid == 3}">
                                               		<c:import url="yesno.jsp" />
                                                </c:if>
                                          </form:form>
                                          <br/>
                                          <!-- 
                                          <form:form commandName="form"  action="${url}" method="get">
	                                          <c:if test= "${answered}">
                                             <table>
                                                 <tr class="main-table" >
                                                     <td width="120" style="text-align:center;">
                                                     	 <input type="submit" value="More Quizzes" class="buttonSubmit"></input>
                                                         Learning Language:
                                                        <form:select path="language" itemLabel="name" itemValue="code" items="${languages}" ></form:select>
                                                     </td>
                                             </table>
                                             </c:if>
                                         </form:form>
                                          -->
                                        </c:if>
                                   	    <c:if test="${empty quiz}">
                                   	     <c:import url="noquiz.jsp" />
                                      </c:if>
                                     </div> <!-- parts -->
                                </div><!-- dparts homeRecentList -->
                            </div><!-- Center -->
                      	</div><!-- LayoutA -->
                            <div style="text-align: center;">
                               <span style="font-family: arial,meiryo,simsun,sans-serif;font-size: 22px; font-weight:bold; ">
                               			<br/>
                                      	Today's Score:<br/>&nbsp;<font color="green" size="30px">${quizinfos.scores}</font><br/><br/>
	                                  	Your All Score:<br/>&nbsp;<font color="green" size="30px">${quizinfos.allscores}</font>
	                                  </span>
	                        </div>          
                    </div><!-- ContentsContainer -->
				 </div><!-- Contents -->
            </div><!-- Container -->
        </div><!-- Body -->
    </body>
</html>
