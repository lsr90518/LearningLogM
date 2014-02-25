<%@page contentType="text/html" pageEncoding="UTF-8"
	trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<c:import url="../include/head.jsp">
	<c:param name="title" value="Add a new object" />
	<c:param name="content">
		<style>
.optional {
	display: none
}
</style>
	</c:param>
</c:import>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>publish</title>
</head>
<body>
	<div>
		<div>
			<c:import url="../include/header.jsp" />
			<div id="Contents">
				<div id="ContentsContainer">
					<div id="LayoutC" class="Layout">
						<div id="Top">
							<div id="information_21" class="parts informationBox">
								<div class="body"></div>
							</div>
							<!-- parts -->
						</div>
						<!-- Top -->
						<div id="Center">
                                <div id="diaryForm" class="dparts form">
                                    <div class="parts">
                                        <div class="partsHeading"><h3>Task information</h3></div>
                                     
                                             <table>
                                                <tr>
                                                    <th>
                                                        <label for="task_title">Task</label>
                                                    </th>
                                                    <td> 
                                                        ${task.title}	
                                                    </td>
                                                    </tr>
                                                    <tr>
                                                     <th>
                                                        <label for="task_title">place</label>
                                                    </th>
                                                    <td> 
                                                        ${task.place}	
                                                    </td>
                                                    </tr>
                                                    <tr>
                                                    <th>
                                                        <label for="task_title">Level</label>
                                                    </th>
                                                    <td> 
                                                        ${task.level}	
                                                    </td>
                                                </tr>
                                                <tr>
                                                 <th>
                                                        <label for="task_title">Create Time</label>
                                                    </th>
                                                    <td> 
                                                        ${task.create_time}	
                                                    </td>
                                                    </tr>
                                                    <tr>
                                                    <th>
                                                        <label for="task_title">Update Time</label>
                                                    </th>
                                                    <td> 
                                                        ${task.update_time}	
                                                    </td>
                                                </tr>
                                                
                                         
                                            </table>
                                     
                                     
                                     
                               
                                    </div><!-- parts -->
                                </div><!-- dparts -->
                            </div><!-- Center -->
                            <br>
                            <br>
                            <div id="Center">
                                <div id="diaryForm" class="dparts form">
                                    <div class="parts">
                                        <div class="partsHeading"><h3>Task Script information</h3></div>
                                     
                                             <table>
                                                <tr>
                                                    <th>
                                                        <label for="task_title">Task Script</label>
                                                    </th>
                                                   </tr>
                                                  
                                                         <c:forEach items="${taskscript}" var="script">
                                                          <tr>
                                                         <th>
                                                         <b>STEP ${script.num} </b>
                                                         			</th>
                                                         			<td>	
                                                         				 ${script.script}
                                                         				</td>
                                                                </tr>
                                                                    </c:forEach>
                                               
                                         
                                                    
                                         
                                            </table>
                                    </div><!-- parts -->
                                </div><!-- dparts -->
                            </div><!-- Center -->
                            <br>
                            <br>
                              <div id="Center">
                                <div id="diaryForm" class="dparts form">
                                    <div class="parts">
                                        <div class="partsHeading"><h3>Related Task Items</h3></div>
                                     
                                          
                                     
                                     
                               
                                    </div><!-- parts -->
                                </div><!-- dparts -->
                            </div><!-- Center -->
                            <br><br><br>
                            
                            
                            
							</div>
							<!-- Layout -->
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