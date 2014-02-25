<%@page contentType="text/html" pageEncoding="UTF-8"
	trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!doctype html>
<html>
<c:import url="../include/head.jsp">
	<c:param name="title" value="Add a new object" />
	<c:param name="content">
		<style>
.optional {
	display: none
}
</style>
		<script src="http://www.google.com/jsapi"></script>
		<script src="http://maps.google.com/maps/api/js?sensor=true"></script>
		<script src="<c:url value='/js/LLMap.js' />"></script>
		<script>
                
                var map;
                $(function(){
                	map = new LLMap("map", {
                		onchange:function(lat, lng, zoom){
                			$("#lat").val(lat);
                			$("#lng").val(lng);
                			$("#zoom").val(zoom);
                		}
                	});
                });
                
                
                $(
       	            function(){
       	            		if(!$('#location_based_cb').is(':checked'))
       	                		$('#script_map').hide();
       	                	else
       	            			$('#script_map').show();
       	            	}	
       	         );
                
            	$(document).on("change", "#location_based_cb", function(){
            		if($('#location_based_cb').is(':checked'))
            			$('#script_map').show();
                	else
                		$('#script_map').hide();
            	});
            </script>

		<script type="text/javascript">
	           function onQuery(){
	        	   var i=0;
	        	   if($('#querylog').val() == "")
	        		   return;
	        	   <c:url value="/task/searchItem.json" var="searchItemUrl" />
	        	   $("#search_result_area").empty();
	        	   $.ajax({
	        	         type: "GET",
	        	         url: "${searchItemUrl}",
	        	         data: {taskId:'${task.id}', queryvalue:$('#querylog').val()},
	        	         dataType: "json",
	        	         success: function(data){
	        	        	
	        	        	for(var a=0;a<data.items[0].item_id.length;a++){
	        	        		 var result = "<li><input type='checkbox' name='itemId' onclick=\"onAddItemId('"+data.items[0].item_id[i]+"')\" value='"+data.items[0].item_id[i]+"'> ";
	        	        		 var temp = "";
	        	        		 
	        	        		
		        	        	
	        	        			
	        	        	 	 <c:url value="/item/" var="itemUrl" />
        	        			 result = result+ "<a href='${itemUrl}"+data.items[0].item_id[i]+"' target='_blank'>"+ data.items[1].content[i]+"</a>";
	        	        		 result +="</input>";
	        	        		 $("#search_result_area").append(result+"</li>");
	        	        		 i++;
	        	        	}
	        	          
	        	          },
	        	          error:function(jqXHR,textStatus){
	        	        	  
	        	          }
	        	         
	        	         
	        	         
	        	      });
	           }
	           
	           
	           function onAddItemId(itemId){
	        	   <c:url value="/task/${task.id}/additem.json" var="addItemUrl" />
        		   $.ajax({
        			   type:"POST",
        			   url:"${addItemUrl}",
        			   data: "itemIds="+itemId,
        			   success:function(data){
        				   refreshRelatedItem();
        			   } ,error:function(jqXHR,textStatus){
	        	        	  
	        	          }
        		   });
	           }
	           
	           function onRemoveItemId(itemId){
	        	   <c:url value="/task/${task.id}/removeitem.json" var="removeItemUrl" />
        		   $.ajax({
        			   type:"POST",
        			   url:"${removeItemUrl}",
        			   data: "itemIds="+itemId,
        			   success:function(data){
        				   refreshRelatedItem();
        			   }
        		   });
	           }
	           
	           $(refreshRelatedItem());
	           
	           function refreshRelatedItem(){
	        	   var i=0;
	        	   <c:url value="/task/${task.id}/items.json" var="getItemUrl" />
	        	   $.ajax({
        			   type:"GET",
        			   url:"${getItemUrl}",
        			   dataType: "json",
        			   success:function(data){
        				   $("#related_item_area").empty();
        				   for(var a=0;a<data.items[0].item_id.length;a++){
        					   var result = "<li><input type='checkbox' name='deleteitemId' onclick=\"onRemoveItemId('"+data.items[0].item_id[i]+"')\" value='"+data.items[0].item_id[i]+"'> ";
	        	        		 var temp = "";
	        	        		
	        	        		 
	        	        		 <c:url value="/item/" var="itemUrl" />
	        	        		 result = result+ "<a href='${itemUrl}"+data.items[0].item_id[i]+"' target='_blank'>"+ data.items[1].content[i]+"</a>";
	        	        		 result +="</input></li>";
	        	        		 $("#related_item_area").append(result);
	        	        		 i++;
        				   }				   
        			   }
        		   });
	           }
           </script>


	</c:param>
</c:import>
<head>
<link rel="stylesheet" href="${ctx}/js/bootstrap/css/bootstrap.css" />
<link rel="stylesheet" href="${ctx}/js/bootstrap/css/bootstrap.min.css" />
<link rel="stylesheet"
	href="${ctx}/js/bootstrap/css/bootstrap-responsive.css" />
<script src="${ctx}/js/bootstrap/js/bootstrap.js" type="text/javascript"></script>
<script src="${ctx}/js/bootstrap/js/bootstrap.min.js"
	type="text/javascript"></script>
</head>
<body>
	<div>
		<div>
			<c:import url="../include/header.jsp" />
			<div id="Contents">
				<div id="ContentsContainer">
				<div class="navbar navbar-inverse" style="position: static;">
								<div class="navbar-inner">
									<div class="container">
										<a class="btn btn-navbar" data-toggle="collapse"
											data-target=".navbar-inverse-collapse"> <span
											class="icon-bar"></span> <span class="icon-bar"></span> <span
											class="icon-bar"></span>
										</a>

										<div class="nav-collapse collapse navbar-inverse-collapse">
											<ul class="nav">
												<li><a href="${ctx}/task/taskitem"><i class="icon-home"></i>Task info</a></li>

												<li><a href="${ctx}/task/add"><i class=" icon-user"></i>Personal</a></li>
												<li class="active"><a href="${ctx}/task/collaborative_add"><i class=" icon-eye-open"></i>Collaborative</a></li>
												<li><a href="#"><i class=" icon-map-marker"></i>Recommend</a></li>

											</ul>
											<form class="navbar-search pull-left" action="">
												<input type="text" class="search-query span2"
													placeholder="Search">
											</form>

										</div>
										<!-- /.nav-collapse -->
									</div>
								</div>
								<!-- /navbar-inner -->
							</div>
					<div id="LayoutC" class="Layout">
						<div id="Top">
						
							<!-- parts informationBox -->
						</div>
						<!-- Top -->
						<div id="Center">
							<div id="diaryForm" class="dparts form">
								<div class="parts">
									 <div class="navbar navbar-inner" style="position: static;">
									<div class="navbar-inverse">
										<h3
											style="font-size: 17px; font-weight: bolder; line-height: 150%">
											Step of task script</h3>
									</div></div>
									<c:url value="/task/${task.id}/collaborative_addscript"
										var="taskUrl" />
									<form:form commandName="script" action="${taskUrl}"
										method="post" enctype="multipart/form-data">
										<table>
											<tr>
												<th><label for="task_title">Title</label></th>
												<td>${task.title} <form:hidden path="taskId"
														value="${task.id}" />
												</td>
											</tr>
											<tr>
												<th><label for="script">Script</label><strong>*</strong>
												</th>
												<td><form:textarea path="script" cols="20" rows="15"
														cssStyle="width:98%" />
													<form:errors cssClass="error" path="script" /></td>
											</tr>
											<tr>
												<th><label for="photo">Photo|Video<br />Audio|PDF
												</label></th>
												<td><input type="file" name="image" id="image"
													class="input_file" />
												<form:errors cssClass="error" path="image" /></td>
											</tr>
											<tr>
												<th><label for="image_name">Image name</label></th>
												<td><form:input path="image_name" cssStyle="width:90%" />&nbsp;
												
											</tr>


											<tr>
												<th><label for="location_based">Location Based</label>
												</th>
												<td><form:checkbox path="locationBased"
														id="location_based_cb" /></td>
											</tr>
											<tr id="script_map">
												<td colspan="2"><form:hidden path="lat" id="lat" /> <form:hidden
														path="lng" id="lng" /> <form:hidden path="zoom" id="zoom" />
													<div id="map" style="width: 98%; height: 350px"></div></td>
											</tr>
										</table>
										<div class="operation">
											<ul class="moreInfo button">
												<li><input type="submit" class="btn btn-primary"
													value="Next Step" /></li>
												<li><input type="submit" class="btn btn-inverse"
													value="Return" /></li>
											
												<li><input type="button" class="btn btn-success"
													value="Publish" onclick="location.href='http://ll.is.tokushima-u.ac.jp/learninglog2/task/taskitem'"/></li>
											
											</ul>
											</div>
									</form:form>
								</div>
								<!-- parts -->
							</div>
							<!-- dparts -->
						</div>
						<!-- Center -->
					</div>
					<!-- Layout -->
					<div id="sideBanner">
						 <div class="navbar navbar-inner" style="position: static;">
									<div class="navbar-inverse">
							<h3
								style="font-size: 13px; font-weight: bolder; line-height: 150%">Related
								Learning Logs</h3>
						</div></div>
						<div>
							<ul id="related_item_area"></ul>
						</div>

						<div id="search_area">
							<input name="querylog" id="querylog" /> <input type="button"
								value="Search" onClick="onQuery()" />
						</div>
						<div>
							<ul id="search_result_area"></ul>
						</div>
					</div>
				</div>
				<!-- ContentsContainer -->
			</div>
			<!-- Contents -->
			<c:import url="../include/footer.jsp" />
		</div>
		<!-- Container -->
	</div>
	<!-- Body -->
</body>
</html>
