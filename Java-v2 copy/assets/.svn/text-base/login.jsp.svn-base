<%@page import="java.util.*,java.sql.*,java.io.*" %><%
String path = request.getRealPath("");
String club = path.substring(path.lastIndexOf("/") + 1, path.length());
String domain = request.getServerName();
String tmp = domain.substring(0, domain.indexOf("."));
String label = "Tee Times";
String brand = "ForeTees";
String login_club = club; // used for shared sites

Properties props = System.getProperties();
int server_id = Integer.parseInt(props.getProperty("server_id"));

// add shared sites here to redifine the login_club
if (club.equals("greeleycc")) {

    login_club = "fortcollins";
}

// if user is coming in using mobile domain (m.foretees.com) then redirect to the mobile login page
if (tmp.equalsIgnoreCase("m")) {
//if (true) {
    %>
<!DOCTYPE html>
<html lang="en-US">
    <head>
        <meta name="application-name" content="ForeTees Mobile" />
        <meta name="ft-server-id" content="<%=server_id%>" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta http-equiv="Refresh" content="1; url=/<%=club%>/mlogin.jsp" />
        <title>ForeTees Mobile Redirect</title>
        <style type="text/css">
        	body {
        		background: #FFF;
        		margin: 25px 0px;
        		padding: 10px;
        		text-align: center;
        		font-family: Arial; Helvetica, sans-serif;
        		font-size:18px;
        		color: #000;
        	}
        </style>
    </head>
    <body>
        <img src="/v5/mobile/images/logo_large.gif" alt="ForeTees" />
        <br><br>
            You are being redirect to our mobile login page.&nbsp;
            If your browser doesn't automatically refresh to the new page
            you can <a href="/<%=club%>/mlogin.jsp">click here to go there now</a>.
    </body>
</html>
    <%
    return;
}

// get the current year
Calendar cal = new GregorianCalendar();
int year = cal.get(Calendar.YEAR);

// populate these from club db
String zip = "";
String club_name = "";
String website_url = "";
String custom_styles = "";
int no_reservations = 0;
int foretees_mode = 0;
int genrez_mode = 0;
int new_skin = 1; // default new skin to ON

Connection con = null;
Statement stmt = null;
ResultSet rs = null;

try {

	con = DriverManager.getConnection("jdbc:mysql://10.0.5.1/" + club , "ftlogin", "grt57h3k");

	stmt = con.createStatement();

    rs = stmt.executeQuery("" +
      	"SELECT clubName, zipcode, no_reservations, foretees_mode, genrez_mode, website_url, custom_styles, " +
            "(new_skin_date <= DATE_FORMAT(now(), '%Y%m%d')) AS new_skin " +
       	"FROM club5 "); /* +
       	"WHERE clubName <> '';");*/

    if (rs.next()) {

     	club_name = rs.getString("clubName");
      	zip = rs.getString("zipcode");
      	no_reservations = rs.getInt("no_reservations");
      	foretees_mode = rs.getInt("foretees_mode");
      	genrez_mode = rs.getInt("genrez_mode");
        new_skin = rs.getInt("new_skin");
      	website_url = rs.getString("website_url");
      	custom_styles = rs.getString("custom_styles");

    }

} catch (Exception exc) {

} finally {

    try { rs.close(); }
    catch (Exception ignore) {}

    try { stmt.close(); }
    catch (Exception ignore) {}

}

// define our product label
if (no_reservations == 1) {
    label = "Notification";
} else if (genrez_mode == 1) {
    label = "Reservation";
    if (foretees_mode == 0) brand = "FlxRez";
}

// create the html css link if needed
if (!custom_styles.equals("")) {
	custom_styles = "<link href=\"/v5/assets/stylesheets/custom/" + custom_styles + "\" rel=\"stylesheet\" type=\"text/css\" media=\"all\" />";
}

String custom_css = "";
File checkFile = new File("/srv/webapps/" + club + "/assets/stylesheets/club.css");
if(checkFile != null && checkFile.isFile()){
    custom_css += "<link rel=\"stylesheet\" href=\"/" + club + "/assets/stylesheets/club.css\" type=\"text/css\" />";
}

if (new_skin == 1) {

//
// NEW SKIN
//

%><!DOCTYPE html>
<html lang="en-US">
<head>
<meta name="application-name" content="ForeTees" />
<meta name="ft-server-id" content="<%=server_id%>" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="/v5/assets/stylesheets/sitewide.css" rel="stylesheet" type="text/css" media="all" />
<%=custom_styles%>
<%=custom_css%>
<title><%=brand%> Login</title>
<script type="text/javascript">
window.onload = function(){document.getElementById("user_name").focus();};
</script>
</head>
<body>
<div id="login_body">
<div id="wrapper_login">
<div id="title"><%=club_name%></div>

<div id="main_login">
<div id="login_welcome">
<h2>Welcome to the ForeTees Reservation System</h2>
<br />
Enter your username and password, then click Log In.
</div>
<h1>Please Log In</h1>
<form action="/v5/servlet/Login" method="post" name="login" id="login">
<input type="hidden" name="clubname" value="<%=login_club%>">
<input type="hidden" name="zipcode" value="<%=zip%>">
User Name<div><input id="user_name" name="user_name" class="login" type="text" maxlength="15" /></div>
Password<div><input name="password" class="login" type="password"/></div><br />
<div id="login_help">
	<a href="/v5/servlet/Login?help=yes&amp;clubname=<%=club%>">Need Assistance?</a><br />
	<a href="mlogin.jsp">Use Mobile Login</a>
</div>
<input type="submit" value=" Log In " name="submit" class="login_button_lg">
</form>
</div>

<div id="footer">
<div id="local_links">
Other Links:<br />
<a href="http://<%=website_url%>" target="_blank">visit your club's website</a><br />
<a href="http://www.accuweather.com/adcbin2/local_index?zipcode=<%=zip%>" target="_blank">view local weather</a><br />
<a href="http://www.foretees.com/" target="_blank">visit our corporate site</a>
</div>
<div id="footertext"><strong>
FOR CLUB MEMBERS & STAFF ONLY!
Contact your club manager or professional staff for assistance.
Privacy Statement: We absolutely will not divulge any information from this site to a third party under any circumstances.</strong><br />
Copyright &copy; <%=year%> ForeTees, LLC , All rights reserved.<br />
</div>
</div>

</div>
</div>
</body>
</html>
<%


} else {

//
// OLD SKIN
//

%>
<html>
<head>
  <meta http-equiv="content-type" content="text/html;charset=ISO-8859-1">
  <title><%=brand%> Login</title>
   <script>
   <!--
   function cursor(){document.f.user_name.focus();}
   // -->
   </script>
</head>
<body leftmargin="0" marginheight="0" marginwidth="0" topmargin="0" link="#336633" alink="#336633" vlink="#336633" onLoad="cursor()">
<center>
<font face="Verdana, Arial, Helvetica">
<table border="0" cellspacing="0" cellpadding="0">
 <tbody valign="middle" align="center">
  <tr><td align="center">
    <table>
      <tr>
        <td align="center" valign="top"><img align="middle" src="/<%=club%>/images/<%=club%>.jpg" border="0"><br>
        </td>
      </tr>
    </table></td>
  </tr>
  <tr><td align="center">
    <table background="/v5/images/rough_bg.gif">
      <tr>
        <td rowspan="4" align="left" width="150" background="/v5/images/sand_bg.gif">
          <table width="120" border="0" cellspacing="6" cellpadding="0">
            <tr>
              <td><font face="Verdana, Arial, Helvetica" size="1"><br>
                <img src="/v5/images/title.gif" border=0><br>
                <p><a href="http://<%=website_url%>"><img src="/<%=club%>/images/<%=club%>_sm.jpg" border=0><br><%=club_name%></a>
                <br><br><br>
                <a href="http://www.foretees.com/" target="_blank"><img src="/v5/images/foretees_link.gif" border=0></a>
                <br><br><br>
                <a href="http://www.accuweather.com/adcbin2/local_index?zipcode=<%=zip%>" target="_blank"><img src="/v5/images/weather.gif" border=0></a>
                <br><br><br>
                <a href="/v5/servlet/Login?help=yes&clubname=<%=club%>">
                <img name="FAQs" src="/v5/images/FAQs.png" vspace="0" border="0" alt="<%=brand%> Help"><br>
                Need Assistance?</a>
                <br><br><br>
                <a href="javascript:window.external.AddFavorite('http://www1.foretees.com/<%=club%>')">
                <img src="/v5/images/Add_to_Favorites.png" vspace="0" border="0" alt="Add to Favorites" name="Add_to_Favorites"><br>
                Bookmark This Page</a>
                <br><br><br>
                Copyright &#169; <%=year%>
                <br>ForeTees, LLC <br>All rights reserved.</font></p>
              </td>
            </tr>
          </table>
        </td>
        <td align="center">
          <p><font size="1"><br></font>
             <font size="4" color="#F5F5DC">Welcome to the <i><%=brand%></i> <%=label%> System</font>
             <font size="1" color="#F5F5DC"><br><br>
                 Enter your username and password, then click on the golf ball to login.</font></p>
          <img src="/v5/images/green.gif" alt="" height="35" width="501" border="0"></td>
      </tr>
      <tr>
        <td align="center" valign="middle"><img src="/v5/images/sand.gif" alt="" height="74" width="501" border="0"></td>
      </tr>
      <tr>
        <td align="center" valign="middle">
          <form action="/v5/servlet/Login" method="post" name="f" id="f">

          <input type="hidden" name="clubname" value="<%=login_club%>">
          <input type="hidden" name="zipcode" value="<%=zip%>">

            <table border="0" cellspacing="0" cellpadding="0" background="/v5/images/login_form.gif">
              <tr>
                <td>
                  <table width="360" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td width="113"><img src="/v5/images/login_left.gif" alt="" height="149" width="112" border="0"></td>
                      <td valign="middle" align="left"><img src="/v5/images/login2.gif" alt="" height="31" width="194" border="0"><br>
                        <table width="122" border="0" cellspacing="0" cellpadding="0">
                          <tr>
                            <td>
                              &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                              <input type="text" name="user_name" size="15" maxlength="15"></td>
                          </tr>
                          <tr>
                            <td><img src="/v5/images/password.gif" alt="" height="26" width="194" border="0">
                              &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                              <input type="password" name="password" size="15" maxlength="15">
                              <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                              <font face="Verdana, Arial, Helvetica" size="1" color=\"#FFFFFF\">
                              <a href="/v5/servlet/Login?help=yes&clubname=<%=club%>" style="color:#FFFFFF">
                              Need Assistance?</a>
                              <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                              <a href="mlogin.jsp" style="color:#FFFFFF">
                              Use Mobile Login</a>
                              </font>
                            </td>
                          </tr>
                        </table>
                      </td>
                      <td valign="top"><input type="image" src="/v5/images/submit.gif" name="submit" border="0"></td>
                      <td><img src="/v5/images/login_right.gif" alt="" height="149" width="116" border="0"></td>
                    </tr>
                  </table>
                </td>
              </tr>
            </table>
          </form>
        </td>
      </tr>
      <tr>
        <td align="center" valign="bottom">
          <table width="500" border="0" cellspacing="0" cellpadding="0" background="/v5/images/rough_bg.gif">
            <tr>
              <td width="250"><img src="/v5/images/members_only.gif" alt="" height="62" width="200" border="0"></td>
              <td align="right" width="250"><img src="/v5/images/foretees_login.gif" alt="" height="62" width="200" border="0"></td>
            </tr>
          </table>
        </td>
      </tr>
    </table></td>
  </tr>
  <tr>
    <td>
      <font face=Verdana size=1><br>
      <b>Privacy Statement:</b> We <i>absolutely will not</i> divulge any information from this site to a third party under any circumstances.
    </font></td>
  </tr>
 </tbody>
</table>
</font>
</center>
</body>
</html>
<%
}
%>
