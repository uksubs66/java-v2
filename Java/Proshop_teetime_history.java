/***************************************************************************************
 *   Proshop_teetime_history: This servlet will implement the history functionality
 *
 *
 *   Called by:     Proshop_sheet
 *                  Proshop_oldsheets
 *
 *
 *   Created:       03/23/2006 by Paul
 *
 *
 *   Revisions:     
 *
 *        2/07/07   Minor fixes for TLT system
 *        1/20/07   Changes for TLT system
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


public class Proshop_teetime_history extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
    
    
 //*****************************************************
 // Process the a get method on this page as a post call
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

    doPost(req, resp);

 } // end of doGet routine
 
 
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

    if (req.getParameter("history") == null || (!req.getParameter("history").equals("yes"))) {
        
        // not called correctly - exit
        //return;
    }
    
    resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server
    resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setContentType("text/html");
    
    PrintWriter out = resp.getWriter();

    ResultSet rs = null;

    HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

    if (session == null) return;

    Connection con = SystemUtils.getCon(session);                      // get DB connection

    if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
    }

    //
    //  See if we are in the timeless tees mode
    //
    boolean IS_TLT = ((Integer)session.getAttribute("tlt") == 1) ? true : false;
    
    out.println(SystemUtils.HeadTitle("Tee Time History"));
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    out.println("");
    
    boolean x = false;
    boolean h = false;
    
    int i = 0;
    int fb = 0;
    int time = 0;
    int index = 0;
    int day = 0;
    int month = 0;
    int year = 0;
    int hr = 0;
    int min = 0;
    int teecurr_id = 0;
    int teepast_id = 0;
    
    long date = 0; 
    
    String ampm = " AM";
    String bgcolor = "";   
    String course = req.getParameter("course");
    String sdate = req.getParameter("date");
    String stime = req.getParameter("time");
    String sfb = req.getParameter("fb");
    String sindex = req.getParameter("index");
    String stid = req.getParameter("tid");      // teecurr_id
    String stpid = req.getParameter("tpid");    // teepast_id
    String scalDate = req.getParameter("calDate");

    if (course == null || stime == null || sfb == null) return;
    
    try {
        
        time = Integer.parseInt(stime);
        fb = Integer.parseInt(sfb);
        teecurr_id = Integer.parseInt(stid);
    } catch (Exception Ignore) {}
    
    try {
        
        teepast_id = Integer.parseInt(stpid);
    } catch (Exception Ignore) {}
    
    // see which request variables we going to use to determin the date
    if (scalDate == null) {
        
        // this history request is coming from Proshop_sheet (get date from index)
        
        //
        //  Convert the index value from string to int
        //
        if (sindex == null || sindex.equals( "" ) || sindex.equalsIgnoreCase( "null" )) {

            index = 0;

        } else {

            index = Integer.parseInt(sindex);

        }

        //
        //  Get today's date and then use the value passed to locate the requested date
        //
        Calendar cal = new GregorianCalendar();       // get todays date
        cal.add(Calendar.DATE,index);                  // roll ahead 'index' days
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = month + 1;                            // month starts at zero
        
        date = (year * 10000) + (month * 100) + day;    // create a date field of yyyymmdd
                
    } else {
        
        // this history request is coming from Proshop_oldsheets (get date from calDate)
                
        String[] tmpDate = scalDate.split("/");
        
        month = Integer.parseInt(tmpDate[0]);
        day = Integer.parseInt(tmpDate[1]);
        year = Integer.parseInt(tmpDate[2]);
        
        date = (year * 10000) + (month * 100) + day;    // create a date field of yyyymmdd
        
    }

    //
    //  create a time string for display
    //
    hr = time / 100;
    min = time - (hr * 100);   
        
    if (hr > 11) {
        ampm = " PM";
        if (hr > 12) hr = hr - 12;
    }
    
    stime = hr + ":" + ((min < 10) ? "0" : "") + min + ampm;
        
    out.println("<center><p><font size=5>Tee Time History</font></p>");
    out.println("<b>Date:</b>&nbsp;&nbsp;" + month + "/" + day + "/" + year);
    out.println("&nbsp;&nbsp;&nbsp;<b>Time:</b>&nbsp;&nbsp;" + stime);
    out.println("&nbsp;&nbsp;&nbsp;<b>Location:</b>&nbsp;&nbsp;" + ((fb == 0) ? "Front" : "Back"));
    if (!course.equals( "" )) out.println("&nbsp;&nbsp;&nbsp;<b>Course:</b>&nbsp;&nbsp;" + course);
    out.println("</center><br>");

    out.println("<center><form>");
    out.println("<input type=image src=\"/" +rev+ "/images/print.gif\" width=\"80\" height=\"18\" border=\"0\" onclick=\"window.print(); return false;\" alt=\"Click here to print this history.\">");
    out.println("&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;");
    out.println("<input type=button value=\"  Close  \" onclick=\"window.close();\">");
    out.println("</form></center>");
    
    try {
    
        PreparedStatement pstmtc = con.prepareStatement (
            "SELECT type, sdate, mname, user, player1, player2, player3, player4, player5 " + 
            "FROM teehist " +
            "WHERE date = ? and time = ? AND fb = ? AND courseName = ? " +
            "ORDER BY mdate DESC, time DESC");

        pstmtc.clearParameters();        // clear the parms
        pstmtc.setLong(1, date);
        pstmtc.setInt(2, time);
        pstmtc.setInt(3, fb);
        pstmtc.setString(4, course);
        rs = pstmtc.executeQuery();      // execute the prepared stmt
        
        out.println("<table align=center border=1 cellspacing=5 cellpadding=3>");
        
        while (rs.next()) {
            
           if (h == false) {
              out.println("<tr bgcolor=\"#336633\" style=\"color: white\"><td nowrap><b>Date</b></td><td><b>Name</b></td><td><b>User</b></td><td nowrap><b>Player 1</b></td><td nowrap><b>Player 2</b></td><td nowrap><b>Player 3</b></td><td nowrap><b>Player 4</b></td><td nowrap><b>Player 5</b></td></tr>");
              h = true;
           }
           
           i++;
           x = (x==false);
           bgcolor = (x==true) ? "#F9F9F9" : "#F2F2F2";
           
           out.println("<tr bgcolor=\"" + bgcolor + "\">");
            out.print("<td nowrap>" + rs.getString(2) + "</td>"); // date & time stamp
            out.print("<td nowrap>" + rs.getString(3) + "</td>"); // mname
            out.print("<td nowrap>" + rs.getString(4) + "</td>"); // username
            out.print("<td nowrap>" + rs.getString(5) + "</td>"); // player1
            out.print("<td nowrap>" + rs.getString(6) + "</td>"); // player2
            out.print("<td nowrap>" + rs.getString(7) + "</td>"); // player3
            out.print("<td nowrap>" + rs.getString(8) + "</td>"); // player4
            out.print("<td nowrap>" + rs.getString(9) + "</td>"); // player5
           out.println("</tr>");
           
        }
        
        pstmtc.close();

        out.println("</table>");
           
        if (i==0 && !IS_TLT) {
            out.println("<center><br><p><i><b><font color=red>No history found for this tee time.</font></b></i></p>");
            //out.println("<br><form><input type=button value=\"  Close  \" onclick=\"window.close();\"></form><br>");
            out.println("<font size=-1>(this window will auto close in 3 seconds)</font></center>");
            out.println("<script>setTimeout('window.close()', 3000)</script>");
        } else {
            out.println("<center><p><i><b>&nbsp;Found "+ i +" historical " + ((i>1) ? "entries" : "entry") + " for this tee time.</b></i></p></center>");
            //out.println("<br><form><input type=button value=\"  Close  \" onclick=\"window.close();\"></form>");
        }
        
        out.println("<br>");
        
    }
    catch (Exception exp) {
        displayDatabaseErrMsg("Error looking up tee time history.", exp.getMessage(), out);
    }
    

    if (IS_TLT) {

        out.println("<!-- teecurr_id=" + teecurr_id + " -->");
        out.println("<!-- teepast_id=" + teepast_id + " -->");
        
        String tmp_created = "";
        String tmp_reqest = "";
        String tmp_converted = "";
        String tmp_converted_by = "";
        String tmp_created_by = "";
        
        try {
            
            PreparedStatement pstmtc = con.prepareStatement (
                "SELECT " +
                    "DATE_FORMAT(created_datetime, \"%W, %b %e, %Y at %l:%i %p\") AS created, " +
                    "DATE_FORMAT(req_datetime, \"%W, %b %e, %Y at %l:%i %p\") AS request, " +
                    "DATE_FORMAT(converted_at, \"%W, %b %e, %Y at %l:%i %p\") AS converted, " +
                    "converted_by, created_by " +
                "FROM notifications " +
                "WHERE " + ((teepast_id != 0) ? "teepast_id" : "teecurr_id") + " = ?");

            pstmtc.clearParameters();        // clear the parms
            pstmtc.setInt(1, ((teepast_id != 0) ? teepast_id : teecurr_id));
            rs = pstmtc.executeQuery();
        
            if (rs.next()) {
                
                tmp_created = rs.getString("created");
                tmp_reqest = rs.getString("request");
                tmp_converted = rs.getString("converted");
                tmp_converted_by = rs.getString("converted_by");
                tmp_created_by = rs.getString("created_by");
                
                out.println("<!-- tmp_created_by=" + tmp_created_by + " -->");
                
                if (!tmp_created_by.startsWith("proshop")) {
                    
                    PreparedStatement pstmt = con.prepareStatement (
                        "SELECT CONCAT(name_first, ' ', name_last, ' (', username, ')') AS name " +
                        "FROM member2b " +
                        "WHERE username = ?");

                    pstmt.clearParameters();
                    pstmt.setString(1, tmp_created_by);
                    rs = pstmt.executeQuery();

                    if (rs.next()) tmp_created_by = rs.getString(1);
                    
                    out.println("<!-- tmp_created_by=" + tmp_created_by + " -->");
                    
                }
                
                out.println("<br>");
                
                out.println("<center><p><font size=5>Original Notification History</font></p>");
                
                out.println("<table align=center>");
                
                out.println("<tr><td><b>Created at:</b> " + tmp_created + " <b>by</b> " + tmp_created_by + "</td></tr>");
                out.println("<tr><td><b>Requested Time:</b> " + tmp_reqest + "</td></tr>");
                out.println("<tr><td><b>Converted at:</b> " + tmp_converted + " <b>by</b> " + tmp_converted_by + "</td></tr>");
                
                out.println("</table>");
            }
        }
        catch (Exception exp) {
            displayDatabaseErrMsg("Error looking up notification history.", exp.getMessage(), out);
        }
        
    } // end IS_TLT
    
    
    out.println("</body></html>");
    
 } // end of doPost routine
 
 
private void displayDatabaseErrMsg(String pMessage, String pException, PrintWriter out) {
    out.println(SystemUtils.HeadTitle("Database Error"));
    out.println("<BODY><CENTER>");
    out.println("<BR><BR><H1>Database Access Error</H1>");
    out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
    out.println("<BR>Please try again later.");
    out.println("<BR><br>Fatal Error: " + pMessage);
    out.println("<BR><br>Exception: " + pException);
    out.println("<BR><BR>If problem persists, contact customer support.");
    out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
    out.println("</CENTER></BODY></HTML>");
}

} // end servlet public class