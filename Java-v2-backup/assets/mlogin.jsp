<%@page import="java.util.*" %><%@page import="java.sql.*" %><%
String path = request.getRealPath("");
String club = path.substring(path.lastIndexOf("/") + 1, path.length());
String label = "Tee Times";		// this may get changed if not traditional tee time site
String brand = "ForeTees";

// get the current year
Calendar cal = new GregorianCalendar();
int year = cal.get(Calendar.YEAR);

Properties props = System.getProperties();
int server_id = Integer.parseInt(props.getProperty("server_id"));

// populate these from club db
String zip = "";
String club_name = "";
int no_reservations = 0;
int foretees_mode = 0;
int genrez_mode = 0;

Connection con = null;
Statement stmt = null;
ResultSet rs = null;

try {

	con = DriverManager.getConnection("jdbc:mysql://10.0.5.1/" + club + "?jdbcCompliantTruncation=false&autoReconnect=true", "ftlogin", "grt57h3k");

	stmt = con.createStatement();

    rs = stmt.executeQuery("" +
      	"SELECT clubName, zipcode, no_reservations, foretees_mode, genrez_mode " +
       	"FROM club5 " +
       	"WHERE clubName <> '';");

    if (rs.next()) {

     	club_name = rs.getString("clubName");
      	zip = rs.getString("zipcode");
      	no_reservations = rs.getInt("no_reservations");
      	foretees_mode = rs.getInt("foretees_mode");
      	genrez_mode = rs.getInt("genrez_mode");

    }

} catch (Exception exc) {
	%><!-- ERROR <%=exc.toString()%> --><%
} finally {

    try { rs.close(); }
    catch (Exception ignore) {}

    try { stmt.close(); }
    catch (Exception ignore) {}

}

if (no_reservations == 1) {
    label = "Notification";
} else if (genrez_mode == 1) {
    label = "Reservation";
    if (foretees_mode == 0) brand = "FlxRez";
}

%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta name="ft-server-id" content="<%=server_id%>" />
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="viewport" id="viewport" content="width=device-width, user-scalable=yes" />
<title><%=brand%></title>
<style type="text/css">
#login 	{
		font-family:Arial, Helvetica, sans-serif;
		font-size:1em;
		text-align:center;
		}
#login div
		{
		margin: 5px 0px;
		text-align:center;
		}
#logo	{
		text-align:center;
		}

.headertext
		{
		font-family:Arial, Helvetica, sans-serif;
		font-size:1.2em;
		}
.headertext2
		{
		font-family:sans-serif, Arial, Helvetica;
        font-style:italic;
		font-size:1.5em;
		}
.prompt
		{
		font-family:Arial, Helvetica, sans-serif;
		font-size:1em;
		padding:7px;
		}
.blank
		{
		font-family:Arial, Helvetica, sans-serif;
		font-size:.5em;
		}
.login_button 
		{
		font-family:Arial, Helvetica, sans-serif;
		font-size:1em;
		text-decoration:underline;
		background:#8B8970;
		max-width:200px;
		width:55%;
		margin:5px
		}
.rwd_link a:link, .rwd_link a:visited
		{
		font-family:Arial, Helvetica, sans-serif;
		font-size:.8em;
		text-align:center;
		margin:0px auto !important;
		width:55%;
		max-width:250px;
		font-weight:bold;
		}
/*.rwd_link a:hover 
		{
		text-decoration:underline;
		}
*/
</style>
</head>
<body bgcolor="#F5F5DC">
    <table align="center" border="0">
           <tr>
               <td align="center"><img src="/v5/mobile/images/logo_large.gif" alt="ForeTees"/><!--<br />
                   <img src="/v5/mobile/images/Mobile.gif" alt="Mobile Version" />--></td>
               <td>&nbsp; &nbsp; &nbsp; &nbsp;</td>
               <td><img src="images/logo.jpg" alt="<%=club_name%>" border="1"/></td>
           </tr>
    </table>
<div id="login">
<!--<div class="blank">&nbsp;</div>-->
<div class="headertext"><%=club_name%></div>
<div class="headertext"><%=label%> System Login</div>
<!--
<div class="prompt">Please Login</div>
-->
<!--<div class="blank">&nbsp;</div>-->
<form action="/v5/servlet/Login" method="post" name="f" id="f">
    <input type="hidden" name="clubname" value="<%=club%>" />
    <input type="hidden" name="zipcode" value="<%=zip%>" />
    <input type="hidden" name="mobile" value="2" />
    <div class="prompt">Username:<br />
    <input type="text" name="user_name" size="15" maxlength="15" /></div>
    <div class="prompt">Password:<br />
    <input type="password" name="password" size="15" maxlength="15" /></div>
    <div class="blank">&nbsp;</div>
    <div class="prompt"><input type="submit" name="login" value="Login" class="login_button"/></div>
</form>
<!--<div class="rwd_link"><a href="../v5/servlet/LoginPrompt?cn=<%=club%>">Please consider upgrading your ForeTees experience with our new full featured mobile friendly ForeTees.  Touch here to try it now!</a></div>--><!-- Please consider trying the new version of ForeTees designed for all the devices you use to connect to the Internet today! --><!-- today's most popular devices  -->
<div class="blank">&nbsp;</div>
<div class="blank">&nbsp;</div>
<font size="2">
Copyright &#169; <%=year%>
<br />
ForeTees, LLC<br />All rights reserved.</font>
</div>
</body>
</html>
