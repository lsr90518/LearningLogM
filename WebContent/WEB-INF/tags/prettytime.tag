<%@ tag import="org.ocpsoft.prettytime.PrettyTime"  import="java.util.Date" import="java.util.Locale"%>
<%@ attribute name="date" required="true" type="java.util.Date" %>
<%
 PrettyTime p = new PrettyTime(new Locale("en"));
 String prettier = p.format(date);
 out.println(prettier);
%>