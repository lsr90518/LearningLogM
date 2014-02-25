<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="include/taglibs.jsp" %>
<!doctype html>
<html>
    <c:import url="include/head.jsp">
        <c:param name="title" value="Login" />
        <c:param name="content">
    		<link rel="stylesheet" type="text/css" href="${ctx}/js/bootstrap/css/bootstrap.min.css" />
        	<script type="text/javascript" src="${ctx}/js/bootstrap/js/bootstrap.min.js"></script>
        	<style type="text/css">
        	#abstract{
        		float: left;
        		width: 420px;
        		padding: 5px;
        		font-size: 16px;
        		word-wrap:break-word;
        		word-break:normal;
        		color: black; 
        		z-index:-300;
        		text-align:justify; 
        	}
        	.loginForm {
        		margin: 20px auto; 
        		float: left;
        	}
        	</style>
        	
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
    <body id="page_member_login">
        <div id="Body">
            <div id="Container">

                <div id="Header">
                    <div id="HeaderContainer">
                        <h1><a href="http://ll.is.tokushima-u.ac.jp">Learning Log</a></h1>
                        <div id="globalNav">
                            <ul>
                            </ul>
                        </div><!-- globalNav -->
                        <div id="topBanner">
                        </div>
                    </div><!-- HeaderContainer -->
                </div><!-- Header -->
                <div id="Contents">
                    <div id="ContentsContainer">
                        <div id="localNav">
                        </div><!-- localNav -->
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
                                <div id="abstract">
                                	Learning-Log means your record of what you have learned and this system
									makes it easy to remember new vocabularies for foreign language learners
									. It allows you to log, share and reuse your
									learning log with others. Also, you can receive personalized quizzes and
									answers for your questions, and navigate surrounding learning log
									supported by augmented reality view. Learning-Log system can be used on
									Android mobile phones or/and on PC using any web browser.
                                </div>
                            </div><!-- Top -->
                            <div id="Center">
                            </div><!-- Center -->
                        </div><!-- Layout -->
                        <div id="sideBanner" style="position:relative;left:-40px;">
                        	<img src="<c:url value="/images/llcollect.jpg"/>" style="width:300px;"/>
                        </div><!-- sideBanner -->
                    </div><!-- ContentsContainer -->
                </div><!-- Contents -->
                <c:import url="include/footer.jsp" />
            </div><!-- Container -->
        </div><!-- Body -->
    </body>
</html>
