/***************************************************************************************
 *   Proshop_diary: This servlet will ouput the member rounds report
 *
 *
 *   Called by:     called by main menu options
 *
 *
 *   Created:       3/10/2005 by Paul
 *
 *
 *   Last Updated:  
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
import java.lang.Math;
import java.text.DateFormat;

// foretees imports
import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.parmPOS;
import com.foretees.common.getClub;
import com.foretees.client.action.ActionHelper;


public class Proshop_report_member_rounds extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
    
    
 //*****************************************************
 // Process the a get method on this page as a post call
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

    doPost(req, resp);                                              // call doPost processing

 } // end of doGet routine
 

 public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
    
    resp.setHeader("Pragma","no-cache");                            // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                                // prevents caching at the proxy server
    resp.setContentType("text/html");                               
    
    PrintWriter out = resp.getWriter();                             // normal output stream
    
    String excel = (req.getParameter("excel") != null) ? req.getParameter("excel")  : "";
    
    // handle excel output
    try{
        if (excel.equals("yes")) {                // if user requested Excel Spreadsheet Format
            resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
        }
    }
    catch (Exception exc) {
    }
    
    HttpSession session = SystemUtils.verifyPro(req, out);          // check for intruder
    if (session == null) { return; }
    
    Connection con = SystemUtils.getCon(session);                   // get DB connection
    if (con == null) {
        displayDatabaseErrMsg("Can not establish connection.", "", out);
        return;
    }
    
    String templott = (String)session.getAttribute("lottery");      // get lottery support indicator
    int lottery = Integer.parseInt(templott);
   
    // see what we are here to do, or try to do
        
    startPageOutput(out);
    SystemUtils.getProshopSubMenu(req, out, lottery);               // required to allow submenus on this page
    
    String period = (req.getParameter("period") != null) ? req.getParameter("period")  : "";
    if (!period.equals("last")) period = "this";  // strict enforce either value, default to this month
        
    // 
    //  Declare our local variables
    //
    Statement stmt1 = null;
    PreparedStatement pstmtc1 = null;
    ResultSet rs1 = null;
    
    Statement stmt2 = null;
    PreparedStatement pstmtc2 = null;
    ResultSet rs2 = null;
    
    int month = 0;
    int day = 0;
    int year = 0;
    int total = 0;
    int i = 0;  // generic counter
    
    String username = "";
    String name_last = "";
    String name_first = "";
    String name_mi = "";
    String m_ship = "";
    String m_type = "";
    String mem_num = "";
    String sql;
    
    GregorianCalendar cal = new GregorianCalendar();            // get todays date
    if (period.equals("last")) cal.add(cal.MONTH, -1);   // set the calendar to last month if needed
    
    // populate our date part variables for use in the sql statement
    month = cal.get(cal.MONTH) + 1;
    day = cal.get(cal.DAY_OF_MONTH);
    year = cal.get(cal.YEAR);
    
    
    // 1  SELECT m.username, m.name_last, m.name_first, m.name_mi, m.m_ship, m.m_type, m.memNum FROM member2b m ORDER BY m.name_last, m.name_first, m.name_mi
    // 2  SELECT count(mm) AS total FROM teepast2 WHERE mm = 3 AND yy = 2005 AND ((username1 = 1900 AND show1 = 1) OR (username2 = 1900 AND show2 = 1) OR (username3 = 1900 AND show3 = 1) OR (username4 = 1900 AND show4 = 1))
    
    sql = "SELECT username, name_last, name_first, name_mi, m_ship, m_type, memNum " +
          "FROM member2b ORDER BY name_last, name_first, name_mi";
    
    out.println("<table width=700 border=1 align=center>");
    out.println("<tr><td><b>Member Name</b></td><td><b>Membership Type</b></td><td><b>Member Type</b></td><td><b>Member Number</b></td><td><b>Rounds</b></td></tr>");
    
    try {
        stmt1 = con.createStatement();
        rs1 = stmt1.executeQuery(sql);

        while (rs1.next()) {
            
          username = rs1.getString("username");

           sql = "SELECT COUNT(mm) FROM teepast2 WHERE mm = ? AND yy = ? AND " +
                 "((username1 = ? AND show1 = 1) OR (username2 = ? AND show2 = 1) OR " +
                 "(username3 = ? AND show3 = 1) OR (username4 = ? AND show4 = 1) OR " +
                 "(username5 = ? AND show5 = 1))";

           pstmtc2 = con.prepareStatement(sql);

           pstmtc2.clearParameters();        // clear the parms
           pstmtc2.setInt(1, month);
           pstmtc2.setInt(2, year);
           pstmtc2.setString(3, username);
           pstmtc2.setString(4, username);
           pstmtc2.setString(5, username);
           pstmtc2.setString(6, username);
           pstmtc2.setString(7, username);

           rs2 = pstmtc2.executeQuery();      // execute the prepared stmt

           while (rs2.next()) {

               total = rs2.getInt(1);

               if (total != 0) {

                   name_last = rs1.getString("name_last");
                   name_first = rs1.getString("name_first");
                   name_mi = rs1.getString("name_mi");
                   m_ship = rs1.getString("m_ship");
                   m_type = rs1.getString("m_type");
                   mem_num = rs1.getString("memNum");

                   out.println("<tr>");
                   out.println("<td nowrap>" + name_last + ", " + name_first + " " + name_mi + "</td>");
                   out.println("<td>" + m_ship + "</td><td>" + m_type + "</td><td>" + mem_num + "</td><td>" + total + "</td>");
                   out.println("</tr>");
               }
           }
           pstmtc2.close();
  
        }
        stmt1.close();
    }
    catch (Exception e) {
        displayDatabaseErrMsg("Error loading member information for report.", e.getMessage(), out);
        return;
    }
    
    out.println("</table>");
    
    endPageOutput(out);
    
 } // end of doPost routine
 
 private void startPageOutput(PrintWriter out) {
    out.println(SystemUtils.HeadTitle("Member Round Reports"));
    //out.println("<html><head><title>Member Round Reports</title></head><body bgcolor=white>");
 }
 
 private void endPageOutput(PrintWriter out) {
    out.println("</body></html>");
 }
 
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
