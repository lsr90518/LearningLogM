<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../include/taglibs.jsp" %>
<!doctype html>
<html>
    
    <c:import url="../include/head.jsp">
		<c:param name="title" value="My Status" />
		<c:param name="javascript">
		    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
			<script type="text/javascript">
				google.load("visualization", "1", {packages:["corechart"]});
				google.setOnLoadCallback(drawChart);
				function drawChart() {
					var data = new google.visualization.DataTable();
					var test1 = ${nextExperiencePoint};
					data.addColumn('string', 'Task');
					data.addColumn('number', 'EXP');
					data.addRows([
						['UploadObjects', ${user.experiencePoint}],
						['Quiz', ${quizInfos.allscores}],
						['AnserQuestion', ${answerCount}],
					]);

		        var options = {
					width: 430, height: 230,
					//title: 'My EXP',
					backgroundColor: 'transparent', // 背景色を透過
					colors: ['#ff7f50', '#ffa500', '#f5deb3'],
					legend: {textStyle: {fontSize: 11}}
				};

				var chart = new google.visualization.PieChart(document.getElementById('chart_div'));
				chart.draw(data, options);
				}
			</script>
		</c:param>
    </c:import>



    <body id="page_status_home">
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
                                                <img alt="LearningUser" src="<c:url value="/images/no_image.gif" />" height="180" width="180" />
                                            </c:when>
                                            <c:otherwise>
                                                <img alt="LearningUser" src="<tags:static filename="${user.avatar}_320x240.png" />" width="180" />
                                            </c:otherwise>
                                        </c:choose>
                                    </p>
                                    <p class="text"><shiro:principal property="nickname" /></p>
                                    <p class="text">Level : ${user.userLevel}</p>
                                    <p class="text">EXP : ${nowExperiencePoint} / Next : ${nextExperiencePoint}</p>

                                    <div class="moreInfo">
                                        <ul class="moreInfo">
                                            <li><a href=" <c:url value="/profile/avataredit"/>">Edit Photo</a></li>
                                            <li><a href=" <c:url value="/profile"/>">My Profile</a></li>
                                            <li><a href=" <c:url value="/status/graph"/>">My Graphs</a></li>
                                        </ul>
                                    </div>
                                </div><!-- parts -->
                            </div><!-- Left -->


							
                            <div id="Center">
                            <link class="include" rel="stylesheet" type="text/css" href="${ctx}/js/jqplot/js/jquery.jqplot.min.css" />
						    <link type="text/css" rel="stylesheet" href="${ctx}/js/jqplot/syntaxhighlighter/styles/shCoreDefault.min.css" />
						    <link type="text/css" rel="stylesheet" href="${ctx}/js/jqplot/syntaxhighlighter/styles/shThemejqPlot.min.css" />
						    <link type="text/css" rel="stylesheet" href="${ctx}/js/bootstrap/css/bootstrap.css" />
						    <link type="text/css" rel="stylesheet" href="${ctx}/js/bootstrap/css/bootstrap-responsive.css" /> 
						  
						  <!--[if lt IE 9]><script language="javascript" type="text/javascript" src="js/jqplot/js/excanvas.js"></script><![endif]-->
						    <script class="include" type="text/javascript" src="${ctx}/js/jqplot/js/jquery.min.js"></script>
                            	<!-- graph part LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL -->
                            	
                            		<style type="text/css">
                            			#tabs{
                            				width:700px;
                            				border:none;
                            			}
                            			
                            			.date{
                            				width:100px;
                            			}
                            			#wordlistDialog{
                            				height:300px;
                            				width:700px;
                            			}
                            			
                            			#wlul li{
                            				float:left;
                            				padding:2px;
                            				margin-right:10px;
                            				margin-bottom:5px;
                            				border:1px solid;
                            			}
                            		</style>
									<input type="hidden" id="userid" value="${user.id}" />
									<!-- date picker -->
									<div>
										From:<input class="date" type="text" id="startdate" /> To <input class="date" type="text" id="enddate" />
									</div>
									<div id="tabs">
									    <ul>
									      <li><a href="#expTab">exp</a></li>
									      <li><a href="#tabs-1">Tab 1</a></li>
									      <li><a href="#tabs-2">Tab 2</a></li>
									      <li><a href="#tabs-3">Tab 3</a></li>
									    </ul>
									    <div id="expTab">
									    	<div id="chart_div"></div>
									    </div>
									    <div id="tabs-1">
										  <div id="chart1" style="text-align:center; width:600px; margin-top:20px; margin-left:20px;">
										  	
										  </div>
									    </div>
									    
									    <div id="tabs-2">
									        <div id="chart2" data-height="260px" data-width="550px" style="text-align:center;margin-top:20px; margin-left:20px;">
									        </div>
									    </div>
									    
									    <div id="tabs-3">
									        <div id="chart3" style="text-align:center;width:600px; margin-top:20px; margin-left:20px;">
									        </div>
									    </div>
									    
									  </div>
									  <!-- modal dialog -->
									  <div id="wordlistDialog" style="display:none;width:700px;">
										  
									  </div>
									  
									  <script type="text/javascript">
									    //点击列表不完全，饼图还不显示内容。
									  	$(document).ready(function() {
									    	
									    	var ddd = new Date();
									    	var tempYear = ddd.getFullYear();
									    	var tempMonth = ddd.getMonth()+1;
									    	if(tempMonth<10){
									    		tempMonth = "0" + tempMonth;
									    	}
									    	var tempDate = ddd.getDate();
									    	if(tempDate < 10){
									    		tempDate = "0" + tempDate;
									    	}
									    	var tempToday = tempYear+"-"+tempMonth+"-"+tempDate;
									    	$('#enddate').val(tempToday);
									    	
									    	//datepicker
									    	$('.date').datepicker({
									            changeMonth: true,
									            changeYear: true
									        });
									    	
									    	//charts
									    	$.jqplot.config.enablePlugins = false;
									    	//initialize value
									    	var b = new Array();
									    	var l = new Array();
									    	var p = new Array();
									    	var i = 0;
									    	var ticks = new Array();
									    	//use ajax to get data
									    	
									    	function getBar(){
									    		document.getElementById("chart1").innerHTML="<img class='loading' src='${ctx}/images/status-ajax-loader.gif' />"
									    		$.post("getBarData",{id:"${user.id}",startdate:$('#startdate').val(),enddate:$('#enddate').val()},function(data){
									    			document.getElementById("chart1").innerHTML="";
										    		for(var i = 0;i<data.length;i++){
										    			ticks[i] = "level "+data[i].attr;
										    			var temp = new Number(data[i].num);
										    			b[i] = temp;
										    		}
											        plot1 = $.jqplot('chart1', [b], {
											        	title:'登録単語とレベルのヒストグラム',
											            animate: !$.jqplot.use_excanvas,
											            seriesDefaults:{
											                renderer:$.jqplot.BarRenderer,
											                pointLabels: { show: true }
											            },
											            axes: {
											                xaxis: {
											                    renderer: $.jqplot.CategoryAxisRenderer,
											                    ticks: ticks
											                }
											            },
											            highlighter: { show: false }
											        });
										    	},"json");
									    	}
									    	
									    	//bind click
									    	$('#chart1').bind('jqplotDataClick', 
									            function (ev, seriesIndex, pointIndex, data) {
									                $.post("getWordlistByLevel",{level:data[0]},function(wordlist){
									                	var tablelist = "<span><center><strong>単語</strong></center></span><ul id='wlul'>";
									                	for(var i = 0 ; i < wordlist.length ; i++){
									                		tablelist = tablelist + "<li class='wlli'>" + "<a href='/learninglog/item/"+wordlist[i].id+"'>" + wordlist[i].content + "</a>" + "</li>"
									                	}
									                	tablelist = tablelist + "</ul>"
									                	 $( "#wordlistDialog" ).html(tablelist);
									                },"json");
									                //tips
									                $( "#wordlistDialog" ).dialog({
									                    modal: true
									                });
									            }
									        );
									    	
									    	function getLine(){
									    		document.getElementById("chart2").innerHTML="<img class='loading' src='${ctx}/images/status-ajax-loader.gif' />"
									    		$.post("getLineData",{id:"${user.id}",startdate:$('#startdate').val(),enddate:$('#enddate').val()},function(data){
									    			document.getElementById("chart2").innerHTML="";
									    			for(var i = 0;i<data.length;i++){
										    			var xTemp = [1 , 1];
										    			var temp0 = new Number(data[i].attr);
										    			xTemp[0] = temp0;
										    			var temp1 = new Number(data[i].num);
										    			xTemp[1] = temp1;
										    			l[i] = xTemp;
										    		}
										    		plot2 = $.jqplot('chart2', [l],  {
										    				title:'登録単語数と日付の関係図',
												        	lengend:{show:true},
												        	series:[{},{yaxis:'y2axis'}, {yaxis:'y3axis'}],
												        	cursor:{show:true, zoom:true},
												         	axes:{
												         		tickOptions: {
												                    mark: 'outside',    // Where to put the tick mark on the axis
												                                        // 'outside', 'inside' or 'cross',
												                    showMark: true,
												                    showGridline: true, // wether to draw a gridline (across the whole grid) at this tick,
												                    markSize: 4,        // length the tick will extend beyond the grid in pixels.  For
												                                        // 'cross', length will be added above and below the grid boundary,
												                    show: true,         // wether to show the tick (mark and label),
												                    showLabel: true,    // wether to show the text label at the tick,
												                    formatString: '',   // format string to use with the axis tick formatter
												                },
												         		pad: 1.2,
												         		useSeriesColor:true, 
												         		rendererOptions: { alignTicks: true},
													        	xaxis: {
													              label: "日本にいる時間",
													              pad: 0
													            },
													            yaxis: {
													              label: "単語登録数",
													            }
												         	}
												        });
										    		//10,1910月份，19个，有错误
										    	},"json");
									    	}
									    	
									    	function getPie(){
									    		document.getElementById("chart3").innerHTML="<img class='loading' src='${ctx}/images/status-ajax-loader.gif' />"
									    		$.post("getPieData",{id:"${user.id}",startdate:$('#startdate').val(),enddate:$('#enddate').val()},function(data){
									    			document.getElementById("chart3").innerHTML="";
										    		for(var i = 0;i<data.length;i++){
										    			var xTemp = ['level 1', 1];
										    			xTemp[0] = "level "+data[i].attr;
										    			var temp = new Number(data[i].num);
										    			xTemp[1] = temp;
										    			p[i] = xTemp;
										    		}
										    		plot3 = $.jqplot('chart3', [p], {
										    				title:'登録単語とレベルの円グラフ',
												          	height: 200,
												          	width: 300,
												          	series:[{renderer:$.jqplot.PieRenderer,
												        	  	rendererOptions: {
												                  showDataLabels: true
												              }}],
												          legend:{show:true}
												        });
										    	},"json");
									    	}
									    	
									    	$('#chart3').bind('jqplotDataClick', 
										            function (ev, seriesIndex, pointIndex, data) {
										                $.post("getWordlistByLevel",{level:pointIndex+1},function(wordlist){
										                	var tablelist = "<span><center><strong>単語</strong></center></span><ul id='wlul'>";
										                	for(var i = 0 ; i < wordlist.length ; i++){
										                		tablelist = tablelist + "<li>" + "<a href='/learninglog/item/"+wordlist[i].id+"'>" + wordlist[i].content + "</a>" + "</li>"
										                	}
										                	tablelist = tablelist + "</ul>"
										                	 $( "#wordlistDialog" ).html(tablelist);
										                },"json");
										                //tips
										                $( "#wordlistDialog" ).dialog({
										                    modal: true
										                });
										            }
										        );
										        //終わり
									    	
									
									        $("#tabs").tabs();
									
									        
									        
									        
									        //line chart
									
									        $('#tabs').bind('tabsshow', function(event, ui) {
									        	
											  if(ui.index == 1) {
												  getBar();
												//plot1.replot();
											  }
									          else if (ui.index == 2) {
									        	 getLine();
									            //plot2.replot();
									          }
									          else if (ui.index == 3) {
									        	  getPie();
									          }
									        });
									
									    
									        //check date
									        $('.date').change(function(){
									        	$( ".date" ).datepicker( "option", "dateFormat", "yy-mm-dd" );
									        	var startdate = new Date();
									        	var enddate = new Date();
									        	startdate = $('#startdate').val();
									        	enddate = $('#enddate').val();
									        	if(enddate > startdate){
									        	} else {
									        		alert("please choose the right date");
									        		$('#startdate').val("");
									        	}
									        	
									        });
									    });
									</script> 

								<!--
								<div id="achievement" class="dparts homeRecentList">
									<div class="partsHeading"><h3>Achievements</h3></div>
								</div>
								-->

                            </div><!-- Center -->
                        </div><!-- Layout -->
                        <div id="sideBanner">
                        </div><!-- ContentsContainer -->
                    </div><!-- Contents -->
                    <c:import url="../include/footer.jsp" />
                </div><!-- Container -->
            </div><!-- Body -->
        </div>
        <script class="include" type="text/javascript" src="${ctx}/js/jqplot/js/jquery.jqplot.min.js"></script>
	    <script type="text/javascript" src="${ctx}/js/jqplot/syntaxhighlighter/scripts/shCore.min.js"></script>
	    <script type="text/javascript" src="${ctx}/js/jqplot/syntaxhighlighter/scripts/shBrushJScript.min.js"></script>
	    <script type="text/javascript" src="${ctx}/js/jqplot/syntaxhighlighter/scripts/shBrushXml.min.js"></script>
	<!-- End Don't touch this! -->
	
	<!-- Additional plugins go here -->
		  <script class="include" type="text/javascript" src="${ctx}/js/jqplot/plugins/jqplot.cursor.min.js"></script>
		  <script class="include" type="text/javascript" src="${ctx}/js/jqplot/plugins/jqplot.barRenderer.min.js"></script>
		  <script class="include" type="text/javascript" src="${ctx}/js/jqplot/plugins/jqplot.pieRenderer.min.js"></script>
		  <script class="include" type="text/javascript" src="${ctx}/js/jqplot/plugins/jqplot.ohlcRenderer.min.js"></script>
		  <script class="include" type="text/javascript" src="${ctx}/js/jqplot/plugins/jqplot.categoryAxisRenderer.min.js"></script>
		  <script class="include" type="text/javascript" src="${ctx}/js/jqplot/jquery-ui/js/jquery-ui.min.js"></script>
        
    </body>
</html>
