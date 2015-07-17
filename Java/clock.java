/***************************************************************************************
 *   Clock: This servlet will implement a javascript clock to be shown on the client
 *          side.  This servlet takes the a date/time value from the server that's
 *          been adjusted to the clubs local time and derives a epoch timestamp.  We 
 *          also get an epoch timestamp from the client and find a difference between 
 *          the two.  We use this diff to continually adjust the date object on the 
 *          client side to that the clock we are displaying accurately represents the
 *          adjusted server time and not the client's computer time.  
 *
 *
 *   Called by:     called by self and start w/ direct call main menu option
 *
 *
 *   Created:       10/07/2008 by Paul
 *
 *
 *   Last Updated:  
 *
 *           2/04/2010  Changes to make valid HTML 4.01 Transitional
 *          12/02/2009  Add Saudi time zone.
 *          10/25/2008  Fix timezone offset bug
 *          10/14/2008  Updated so that club name is passed to this servlet so
 *                      that we do not need to access the users session which
 *                      could prevent their session from expiring since this
 *                      servlet refreshes itself to ensure clock is accurate.
 *
 *                  
 *                  
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


public class clock extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
    
    
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    //HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder
    //HttpSession session = req.getSession(false);
    //if (session == null) return;
    //Connection con = SystemUtils.getCon(session);
    //String club = (String)session.getAttribute("club");
    
    String bgcolor = req.getParameter("bgcolor");
    if (bgcolor == null || bgcolor.equals("")) bgcolor = "CCCCAA";
    
    String club = req.getParameter("club");
    if (club == null || club.equals("")) {
        
        doNothing(bgcolor, out);
        return;
    }
    
    Connection con = null;
    
    try {
        
        con = dbConn.Connect(club);

    } catch (Exception exc) {
        
        doNothing(bgcolor, out);
        return;
    }
    
    if (con == null) doNothing(bgcolor, out);
    
    boolean bold = req.getParameter("bold") != null;
    
    String url = "/" +rev+ "/servlet/clock?club=" + club + "&amp;bgcolor=" + bgcolor + "&amp;" + ((bold) ? "bold" : "");
    
    Calendar cal = new GregorianCalendar();
    int cal_hourDay = cal.get(Calendar.HOUR_OF_DAY);
    int cal_min = cal.get(Calendar.MINUTE);
    int cal_sec = cal.get(Calendar.SECOND);
    
    int time = SystemUtils.adjustTime(con, (cal_hourDay * 100) + cal_min);   // adjust for time zone

    if (time < 0) {                 // if negative, then we went back or ahead one day

        time = 0 - time;            // convert back to positive value (getDate above should have adjusted the date)
    }
    
    int hr = time / 100;
    int min = time - (hr * 100);
    
    int date = (int)SystemUtils.getDate(con);
    
    int year = date / 10000;
    int month = ((date - (year * 10000)) / 100);
    int day = (date - (year * 10000)) - (month * 100);
    month--;
    
    Calendar c = Calendar.getInstance();
    c.set(year, month, day, hr, min, cal_sec);
    long ms = c.getTimeInMillis();
    
    // our timezone offset
    int server_tz_offset = (c.get(c.ZONE_OFFSET) + c.get(c.DST_OFFSET)) / 1000 / 60 / 60;
    
    int club_tz_offset = getTimeZoneDiff(server_tz_offset, club, con, out);
    
    try { con.close(); } 
    catch (Exception ignore) { }
    
    out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
    //out.println("<!-- club time=" +time+ ", date=" +date+" -->");
    //out.println("<!-- club hr=" +hr+ ", min=" +min+", sec=" +cal_sec+" -->");
    out.println("<html>");
    out.println("<head>");
    out.println("<title></title>");
    out.println("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=iso-8859-1\">");
    out.println("<meta http-equiv=\"Refresh\" content=\"300; url=" + url + "\">"); // auto-refresh every 5 minutes
    out.println("<style type=\"text/css\">");
    out.println("html body {");
    out.println("  margin-left:0;");
    out.println("  margin-right:0;");
    out.println("  margin-top:0;");
    out.println("  margin-bottom:0;");
    out.println("  font-size:14px;");
    out.println("  font-family:times;");
    out.println("}");
    out.println("</style>");
    out.println("<script type=\"text/javascript\">");
    out.println("<!-- ");
    out.println("var clockID = 0;");
    out.println("var server_ms = " + ms + ";"); // adjust to client tz
    out.println("var d = new Date();");
    out.println("var client_ms = d.getTime();");
    out.println("var client_tz_offset = d.getTimezoneOffset() / 60;");
    out.println("var club_tz_offset = " + (club_tz_offset * -1) + ";");
    out.println("var client_to_club_offset = client_tz_offset - club_tz_offset;");
    out.println("var client_to_club_offset_ms = client_to_club_offset * 60 * 60 * 1000;");
    out.println("var server_tz_offset = "+(server_tz_offset * -1)+";");
    out.println("var server_client_diff = client_tz_offset - server_tz_offset;");
    out.println("server_ms += (server_client_diff * 60 * 60 * 1000)");
    out.println("var diff_ms = server_ms - client_ms - client_to_club_offset_ms;");
    
    out.println("function updateClock() {");
    out.println(" if(clockID) {");
    out.println("  clearTimeout(clockID);");
    out.println("  clockID = 0;");
    out.println(" }");
    out.println(" var clock = new Date();");
    out.println(" var curr_ms = clock.getTime();");
    out.println(" clock.setTime(curr_ms + diff_ms + client_to_club_offset_ms);");
    out.println(" var h=clock.getHours();");
    out.println(" var m=clock.getMinutes();");
    out.println(" var s=clock.getSeconds();");
    out.println(" var ampm = 'PM';");
    out.println(" m=(m<10)?'0'+m:m;");
    out.println(" s=(s<10)?'0'+s:s;");
    out.println(" if(h>12) {");
    out.println("  h=h-12;");
    out.println(" } else if (h==0) {");
    out.println("  h=12;");
    out.println("  ampm='AM';");
    out.println(" } else if(h!=12) { ");
    out.println("  ampm='AM';");
    out.println(" }");
    out.println(" document.getElementById('foo').innerHTML = h+':'+m+':'+s+' '+ampm;");
    out.println(" clockID = setTimeout('updateClock()', 500);");
    out.println("}");
    
    out.println("function startClock() {");
    out.println(" clockID = setTimeout('updateClock()', 500);");
    out.println("}");
    
    out.println("function stopClock() {");
    out.println(" if(clockID) {");
    out.println("  clearTimeout(clockID);");
    out.println("  clockID = 0;");
    out.println(" }");
    out.println("}");
    
    out.println("// -->");
    out.println("</script>");
    out.println("</head>");
    out.println("<body onload='startClock()' onunload='stopClock()' bgcolor=\"#" + bgcolor + "\">");
    out.println("<div id=\"foo\"" + ((bold) ? " style=\"font-weight:bold\"" : "") +"></div>");
    out.println("</body>");
    out.println("</html>");
    
    out.close();

 } // end of doGet routine
 
 
 private void doNothing(String bgcolor, PrintWriter out) {

    out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
    out.println("<html>");
    out.println("<head>");
    out.println("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=iso-8859-1\">");
    out.println("<title></title>");
    out.println("<body bgcolor=\"" + bgcolor + "\">");
    out.println("</body>");
    out.println("</html>");
    out.close();
 } 
 
 
 /*
  
        Eastern     (EST -0500) (EDT -0400)
        Central     (CST -0600) (CDT -0500)
        Mountain    (MST -0700) (MDT -0600)
        Pacific     (PST -0800) (PDT -0700) 
        Arizona     (MST -0700) (NO DST)
        Hawaiian    (HST -1000) (NO DST)
  
  */
 
 private int getTimeZoneDiff(int tz, String club, Connection con, PrintWriter out) {

    int offset = 0;
    String adv_zone = "";
    
    try {

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT adv_zone FROM club5");

        if (rs.next()) adv_zone = rs.getString(1);
        
        stmt.close();

    } catch (Exception exc) { 
        out.println(exc.toString());
    }


    if (adv_zone.equals( "Eastern" )) {

        offset = 1;

    } else if (adv_zone.equals( "Mountain" )) {

        offset = -1;

    } else if (adv_zone.equals( "Pacific" )) {

        offset = -2;

    } else if (adv_zone.equals( "Hawaiian" )) {

        offset = -4;

    } else if (adv_zone.equals( "Arizona" )) {

        offset = -1;

    }
    
    if (adv_zone.equals( "Saudi" )) {      // Saudi Time = +8 or +9 hrs (no DST)

        offset = 9;
    }
    
    
    out.println("<!-- " + adv_zone + " offset=" + offset + " -->");
    
    return tz + offset;
    
 }
 
} // end servlet public class