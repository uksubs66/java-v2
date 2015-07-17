<%@page import="java.util.*,java.sql.*,java.io.*" %><%
String path = request.getRealPath("");
String club = path.substring(path.lastIndexOf("/") + 1, path.length());
//String domain = request.getServerName();

// Redirect to v5 LoginPrompt servlet so we have access to the full application.
response.setStatus(303);
response.setHeader( "Location", "../v5/servlet/LoginPrompt?cn="+club);
response.setHeader( "Connection", "close" );


%>