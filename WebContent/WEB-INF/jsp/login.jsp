<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="include/taglibs.jsp" %>
<!doctype html>
<html>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <c:import url="include/head.jsp">
        <c:param name="title" value="Login" />
        <c:param name="content">
    		<!-- <link rel="stylesheet" type="text/css" href="${ctx}/js/bootstrap/css/bootstrap.min.css" />
        	<script type="text/javascript" src="${ctx}/js/bootstrap/js/bootstrap.min.js"></script> -->
        	
        	
        	
        	<script type="text/javascript">
        	$(function(){
        		$("form").validate({
        			rules:{
        				email:{
        					required: true,
        					email: true
        				},
        				password:{
        					required: true
        				}
        			},
        			messages:{
        				email:{
        					required: "Please enter your email address.",
        				},
        				password:{
        					required: "Please enter your password."
        				}
        			}
        		});
        	});
        	</script>
        </c:param>
    </c:import>
    <style type="text/css">
    	body{
    	background-color: rgb(250,185,55);
    	}
    	.head-text{
    		text-align: center;
    	}
    	.head-text-wrap{
    		margin-top:50px;
    	}
    	
    	.login-form-wrap{
    		margin-top:40px;
    	}
    	.login-form-wrap input{
    		margin-bottom:7px;
    		float:left;
    	}
    </style>
    <body>
	    <div class="container-fluid">
	      	<div class="head-text-wrap">
			  <div class="row head-text">
				<div class="col-xs-12 col-md-12"><h2>Learninglog</h2></div>
			  </div>
			  <div class="row head-text">
				<div class="col-xs-12 col-md-12">Log your learning log, Learn your learning log.</div>
			  </div>
		  	</div>
		  	
		  	<div class="login-form-wrap">
		  		<form action="<c:url value="/signin" />" method="post">
		  			<!-- head text -->
				  <div class="row head-text">
					<div class="col-xs-12 col-md-12">
						<input type="email" name="email" value="${email}" class="form-control" id="email" placeholder="Username">
					</div>
				  </div>
				  	<!-- login form-->
				  <div class="row head-text">
					<div class="col-xs-12 col-md-12">
						<input type="password" name="password" class="form-control" id="password" placeholder="Password">
					</div>
				  </div>
				  	<!-- login button -->
				  <div class="row head-text">
					<div class="col-xs-12 col-md-12">
						<input type="checkbox" name="rememberMe" checked style="display:none" />
						<input type="submit" class="btn btn-block" value="Login" style="background-color:rgb(22, 160, 133);color:white" />
						<input type="button" class="btn btn-block" value="Sign in" style="background-color: rgb(41, 128, 185);color:white" />
					</div>
				  </div>
				
				</form>
			</div>
		</div>
		
		
    			<!-- <div class="container">
                    <div class="row">
                        <div id="LayoutA" class="Layout">
                            <div id="Top">
                                <div id="MailAddressLogin" class="loginForm">
                                    <p style="font-size: 0.8em; color: red">${error}</p>
                                    <form action="<c:url value="/signin" />" method="post">
                                        <table>
                                            <tr>
                                                <th><label for="email">Email: </label></th>
                                                <td style="width:150px"><input type="email" name="email" value="${email}" style="width: 150px;" /></td>
                                            </tr>
                                            <tr>
                                                <th><label for="password">Password: </label></th>
                                                <td style="width:150px"><input type="password" name="password" style="width: 150px;" /></td>
                                            </tr>
                                            <tr>
                                                <th></th>
                                                <td>
										      		<label class="checkbox">
                                                    	<input type="checkbox" name="rememberMe" />Remember me
                                                    </label>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td colspan="2">
                                                	<input type="submit" class="btn" value="Login" />
                                                	&nbsp;<a href="${ctx}/signup/resetpassword">Forgot password?</a>
                                                </td>
                                            </tr>
                                        </table>
                                    </form>
                                    <div>
                                    	<br /><br />
                                    	<a href="<c:url value="/signup" />">
                                    		Create your account!<br />
                                    		(It's free and anyone can join.)
                                    	</a>
                                    </div>
                                </div>
                                
                            </div><!-- Top -->
                            <!--<div id="Center">
                            <!--</div><!-- Center -->
                        <!--</div><!-- Layout -->
                        <!--<div id="sideBanner" style="position:relative;left:-40px;">
                        	<img src="<c:url value="/images/llcollect.jpg"/>" style="width:300px;"/>
                        </div> sideBanner -->
                    <!--</div><!-- ContentsContainer -->
            <!--/div><!-- Container -->
    </body>
</html>
