<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!doctype html>
<html>
<c:import url="../include/head.jsp">
    <c:param name="title" value="Add a new object" />
    <c:param name="content">
     	<link rel="stylesheet" type="text/css" href="${ctx}/js/bootstrap/css/bootstrap.min.css" />
       	<script type="text/javascript" src="${ctx}/js/bootstrap/js/bootstrap.min.js"></script>
        <style>
            .titleLangName{width:70px;}
            .titleMap{width:70%;}
            #titleTable button{width:20%;}
            .optional{display:none}
        </style>
            <script src="http://www.google.com/jsapi"></script>
            <script src="http://maps.google.com/maps/api/js?sensor=true"></script>
            <script src="<c:url value='/js/LLMap.js' />"></script>
            <script>
                function translateTitle(code){
                    var translateTitleUri = "${ctx}/api/translate/itemTitle";
                    var titles = "{";
                    $.each($(".titleMap"),function(i, item){
                        titles+=$(item).attr("lang")+":\""+$(item).val()+"\"";
                        if(i<$(".titleMap").length-1)titles+=",";
                    });
                    titles+="}";
                    var inputdata={ 'target':code, 'titles': titles};
                    $.get(translateTitleUri,inputdata ,function(data){
                        $("#inputTitle_"+code).val(data);
                    });
                }

                function translateQuestion(){
                    var translateTitleUri = "${ctx}/api/translate/text";
                    var inputdata={ 'src': $("#transFrom").val(), 'dest':$("#transTo").val(), 'text': $("#question").val()};
                    $.get(translateTitleUri,inputdata ,function(data){
                        $("#question").val($("#question").val()+"{"+data+"}");
                    });
                }
                
                var map;
                $(function(){
                	map = new LLMap("map", {
                		onchange:function(lat, lng, zoom){
                			$("#itemLat").val(lat);
                			$("#itemLng").val(lng);
                			$("#itemZoom").val(zoom);
                		}
                	});
                	
                	$(document).on("change", "#addLangSelect", function(){
                        var code = $("#addLangSelect").val();
                        if(code==null && code==-1)return;
                        var name = $("#addLangSelect").find("option:selected").text();
                        $("<tr><td class=\"titleLangName\">"+name+"</td><td><input name=\"titleMap['"+code+"']\" id=\"inputTitle_"+code+"\" type=\"text\" class=\"titleMap input-small\" lang=\""+code+"\" />&nbsp;<button class=\"btn btn-small\" onclick=\"translateTitle('"+code+"');return false;\">Translate</button></td></tr>").appendTo($("#titleTable"));
                        $("#addLangSelect option[value='"+code+"']").remove();
                        if($("#addLangSelect option").length<=1){
                            $("#addLangSelect").parent().hide();
                        }
                	}).on("click", "#showMoreButton",function(){
                		if($(".optional:first").is(":hidden")){
                			$(".optional").show();
                			$("#showMoreButton").text("Hide Details");
                		}else{
                			$(".optional").hide();
                			$("#showMoreButton").text("Show Details");
                		}
                	}).on("click", "#generateQrcode", function(){
                        if($("#qrcode").val()!=""){
                            if(!confirm("Generate a new QR code?")){
                                return;
                            }
                        }
                        $.get("<c:url value="/api/qrcode/generate" />", function(data){
                            $("#qrcode").val(data);
                            $("#qrcodeArea").html("<img src=\"http://chart.apis.google.com/chart?cht=qr&chs=120x120&chl="+data+"\"/>");
                        });
                        return false;
                    }).on("click", "#clearQrcode", function(){
                        $("#qrcode").val("");
                        $("#qrcodeArea").html("");
                    }).on("click", "#printQrcode", function(){
                        $.get("<c:url value="/api/qrcode/generate" />", function(data){
                            $("#qrcode").val(data);
                            $("#qrcodeArea").html("<img src=\"http://chart.apis.google.com/chart?cht=qr&chs=120x120&chl="+data+"\"/>");
                            window.open("<c:url value='/qrcodeprint?content=' />"+$("#qrcode").val(),"", "height=170, width=170" );
                        });
                    }).on("change", "#qrcode", function(){
                        if($.trim($("#qrcode").val())==""){
                            $("#qrcodeArea").html("");
                            return;
                        }
                        $("#qrcodeArea").html("<img src=\"http://chart.apis.google.com/chart?cht=qr&chs=120x120&chl="+$("#qrcode").val()+"\"/>");
                    });
                });
            </script>
            
    
	    <script type="text/javascript">
        function getUrlVars()
        {
            var vars = [], hash;
            var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
            for(var i = 0; i <hashes.length; i++)
            {
                hash = hashes[i].split('=');
                vars.push(hash[0]);
                vars[hash[0]] = hash[1];
            }
            return vars;
        }
	    
	    $(document).ready(function() {
	    	var vars = getUrlVars();
	    	if (vars["langlist"] === void 0) return ;
	   		var lang = vars["langlist"].split("|");
	    	$.each(lang, function(i, val){
		    	if (vars[val+"_title"] !== void 0) {
		    		$("#inputTitle_"+val).val(decodeURI(vars[val+"_title"]));
		    	}
	    	});
	    });
	    </script>

        </c:param>
    </c:import>
    <body>
        <div>
            <div>
                <c:import url="../include/header.jsp" />
                <div id="Contents">
                    <div id="ContentsContainer">
                        <div id="LayoutC" class="Layout">
                            <div id="Center">
                                <div id="diaryForm" class="dparts form">
                                    <div class="parts">
                                        <div class="partsHeading">Add new object</div>
                                        <c:url value="/item" var="itemUrl" />
                                        <form:form commandName="item" action="${itemUrl}" method="post" enctype="multipart/form-data">
                                            <strong>*</strong>&nbsp;Required.
                                            <button class="btn btn-info" style="float:right" id="showMoreButton" onclick="return false;">Show Details</button>　
                                            <table>
                                                <tr class="optional">
                                                    <th><label for="setting">Setting</label></th>
                                                    <td>
                                                        <table>
                                                            <tr>
                                                                <td style="width:90px;">Share Level</td>
                                                                <td>
                                                                    <form:select path="shareLevel" cssClass="span2">
                                                                        <form:option value="0" label="Public" />
                                                                        <form:option value="1" label="Private" />
                                                                    </form:select>
                                                                </td>
                                                                  <td style="width:90px;">Category</td>
                                                                <td>
                                                                    <c:choose>
                                                            <c:when test="${empty myCategoryList}">
                                                                <a href="<c:url value="/mysetting" />" target="_blank">Set categories</a>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <form:select path="categoryId" cssClass="span2">
                                                                    <form:option value="" label="Select category" />
                                                                    <c:forEach items="${myCategoryList}" var="cat">
                                                                      <form:option value="${cat.id}" label="${cat.name}" />
                                                                    </c:forEach>
                                                                </form:select>
                                                            </c:otherwise>
                                                        </c:choose>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                                <tr class="optional">
                                                    <th><label for="quiztype">Quiz Options</label></th>
                                                    <td>
                                                        <c:forEach items="${questionTypes}" var="qestype">
                                                        <label class="checkbox">
														  <form:checkbox path="questionTypeIds" value="${qestype.id}" checked="true"/>
														  ${qestype.title}
														</label>
                                                        </c:forEach>
                                                           <label class="checkbox"><form:checkbox path="locationBased" />Location Based</label>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <th>
                                                        <label for="diary_title">Title</label><strong>*</strong>
                                                    </th>
                                                    <td> 
                                                        <table id="titleTable">
                                                            <c:forEach items="${langs}" var="lang">
                                                                <tr>
                                                                    <td class="titleLangName">${lang.name}</td>
                                                                    <td>
                                                                        <form:input path="titleMap['${lang.code}']" id="inputTitle_${lang.code}" cssClass="titleMap input-small" lang="${lang.code}" />&nbsp;<button class="btn btn-small" onclick="translateTitle('${lang.code}');return false;">Translate</button>
                                                                    </td>
                                                                </tr>
                                                            </c:forEach>
                                                        </table>
                                                        <div>
                                                            <select id="addLangSelect">
                                                                <option value="-1">--Add a new language--</option>
                                                                <c:forEach items="${langList}" var="lang">
                                                                <c:if test="${!langs.contains(lang)}"><option id="addLang_${lang.code}" value="${lang.code}">${lang.name}</option></c:if>
                                                                </c:forEach>
                                                            </select>
                                                        </div>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <th><label for="photo">Photo|Video<br />Audio|PDF</label></th>
                                                    <td>
                                                        <input type="file" name="image" id="image" class="input_file" />
                                                        <form:errors cssClass="error" path="image" />
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <th><label for="note">Description</label></th>
                                                    <td>
                                                        <form:textarea path="note" cols="20" rows="4" cssStyle="width:98%" />
                                                    </td>
                                                </tr>
                                                <tr class="optional">
                                                    <th><label for="tag1">ID</label></th>
                                                    <td>
                                                        <table>
                                                            <tr>
                                                                <td style="width:70px;">Barcode</td>
                                                                <td><form:input path="barcode" id="barcode" /></td>
                                                            </tr>
                                                            <tr>
                                                                <td>QR code</td>
                                                                <td>
                                                                    <form:input path="qrcode" id="qrcode" />
                                                                    <button id="printQrcode" class="btn btn-small" onclick="return false;">Print</button>
                                                                    <button id="clearQrcode" class="btn btn-small" onclick="return false;">Clear</button>
                                                                    <div id="qrcodeArea"></div>
                                                                </td>
                                                            </tr>
                                                            <tr>
                                                                <td>RFID</td>
                                                                <td><form:input path="rfid" /></td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <th rowspan="2"><label for="place">Location</label></th>
                                                    <td>
                                                        <form:input path="place" cssClass="input_text span6" id="place" placeholder="(e.g.) The University of Tokushima" />
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td>
                                                        <form:hidden path="itemLat" id="itemLat" />
                                                        <form:hidden path="itemLng" id="itemLng"/>
                                                        <form:hidden path="itemZoom" id="itemZoom"/>
                                                        <div id="map" style="width: 98%; height: 350px"></div>
                                                    </td>
                                                </tr>
                                                <tr class="optional">
                                                    <th><label for="question">Question</label></th>
                                                    <td>
                                                        <form:textarea path="question" cols="30" rows="10" id="question" cssStyle="width:98%" ></form:textarea>
                                                        Ask to: <form:select path="quesLan" items="${langList}" itemValue="code" itemLabel="name" />
                                                        <select id="transFrom" name="transFrom">
                                                            <c:forEach items="${langList}" var="lang">
                                                            <option value="${lang.code}">${lang.name}</option>
                                                            </c:forEach>
                                                        </select>
                                                        -&gt;
                                                        <select id="transTo" name="transTo">
                                                            <c:forEach items="${langList}" var="lang">
                                                            <option value="${lang.code}">${lang.name}</option>
                                                            </c:forEach>
                                                        </select>
                                                        <button id="transButton" class="btn" onclick="translateQuestion();return false;">Translate Question</button>
                                                    </td>
                                                </tr>
                                                <tr class="optional">
                                                    <th><label for="tag1">Tag</label></th>
                                                    <td>
                                                        <input type="text" name="tag" id="tagInput" />
                                                    </td>
                                                </tr>
                                            </table>
                                            <div class="operation">
                                                <ul class="moreInfo button">
                                                    <li>
                                                        <input type="submit" class="btn btn-primary" value="Save" />
                                                    </li>
                                                    <li>
                                                        <a href="<c:url value="/item" />" class="btn">Return</a>
                                                    </li>
                                                </ul>
                                            </div>
                                        </form:form>
                                    </div><!-- parts -->
                                </div><!-- dparts -->
                            </div><!-- Center -->
                        </div><!-- Layout -->
                    </div><!-- ContentsContainer -->
                </div><!-- Contents -->
                <c:import url="../include/footer.jsp" />
            </div><!-- Container -->
        </div><!-- Body -->
    </body>
</html>
