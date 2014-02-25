<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@attribute name="filename" required="true" rtexprvalue="true" type="java.lang.String" %>
<spring:eval expression="@staticServerService.accessurl(filename)" />