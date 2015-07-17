/***************************************************************************************
 *   Proshop_report_member_rounds_by_course: This servlet will ouput the member rounds report
 *
 *
 *   Called by:     manually - custom report but can be run for any club !!!!!!
 *
 * 
 *
 *   Created:       9/29/2011 by Brad
 *
 *
 *   Last Updated:  
 *
 *                  
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
//import java.lang.Math;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

// foretees imports
import com.foretees.common.parmCourse;
import com.foretees.common.getParms;
import com.foretees.common.Utilities;
import com.foretees.common.Connect;


public class Proshop_report_member_rounds_by_course extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
    
    DateFormat df_full = DateFormat.getDateInstance(DateFormat.MEDIUM);
    
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
    

    
    HttpSession session = SystemUtils.verifyPro(req, out);          // check for intruder
    if (session == null) { return; }
    
    Connection con = Connect.getCon(req);                   // get DB connection
    if (con == null) {
        displayDatabaseErrMsg("Can not establish connection.", "", out);
        return;
    }
    
    // Check Feature Access Rights for current proshop user
    if (!SystemUtils.verifyProAccess(req, "REPORTS", con, out)) {
        SystemUtils.restrictProshop("REPORTS", out);
        return;
    }

    String club = (String)session.getAttribute("club");
    String templott = (String)session.getAttribute("lottery");      // get lottery support indicator
    int lottery = Integer.parseInt(templott);
    
        // handle excel output
    try{
        if (excel.equals("yes")) {                // if user requested Excel Spreadsheet Format
            resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
            resp.setHeader("Content-Disposition", "attachment;filename=\""+club+".xls\"");
        }
    }
    catch (Exception exc) {
    }
    
       
    String period = (req.getParameter("period") != null) ? req.getParameter("period")  : "";
      
    // strict enforce either value, default to this month
    if (!period.equals("last") && !period.equals("year") && !period.equals("lstyr")) {
    
       period = "this";
    }

    // 
    //  First time here - display warning message to inform user that this report can take several minutes!!!!
    //
    if (req.getParameter("continue") == null) {

       displayWarning(period, out);              // display warning message
       return;                                   // exit and wait for continue
    }


    //
    //  Declare our local variables
    //
    Statement stmt1 = null;
    ResultSet rs1 = null;
    PreparedStatement pstmtc2 = null;
    ResultSet rs2 = null;
    
    GregorianCalendar cal = new GregorianCalendar();            // get todays date
    
    int month = 0;  // will hold last month value
    int year = 0;   // will hold current year
    int p9 = 0;     // 9 hole counter
    int p18 = 0;    // 18 hole counter
    int p9t = 0;     // total 9 hole counter
    int p18t = 0;    // total 18 hole counter
    int i = 0;
    int p91 = 0;    
    int p92 = 0;    
    int p93 = 0;    
    int p94 = 0;    
    int p95 = 0;    
    int show1 = 0;    
    int show2 = 0;    
    int show3 = 0;    
    int show4 = 0;    
    int show5 = 0;    
    
    long sdate = 0;    // for custom date range (custom reports to run one time)
    long edate = 0;
    
    String username = "";
    String name_last = "";
    String name_first = "";
    String name_mi = "";
    String m_ship = "";
    String m_type = "";
    String mem_num = "";
    String sql;
    String sql2;
    String whereString = "";
    String user1 = "";
    String user2 = "";
    String user3 = "";
    String user4 = "";
    String user5 = "";
    String p1cw = "";
    String p2cw = "";
    String p3cw = "";
    String p4cw = "";
    String p5cw = "";
    String courseName = "";

    ArrayList<String> courses = new ArrayList<String>();
    ArrayList<Integer> course_counts = new ArrayList<Integer>();
    ArrayList<Integer> course_count_totals = new ArrayList<Integer>();

    try {

        stmt1 = con.createStatement();

        rs1 = stmt1.executeQuery("SELECT courseName FROM clubparm2 ORDER BY courseName");

        while (rs1.next()) {
            courses.add(rs1.getString("courseName"));
        }

    } catch (Exception exc) {
        Utilities.logError("Proshop_report_member_rounds_by_course.doPost - " + club + " - Error running report - Err: " + exc.toString());
    } finally {

        try { rs1.close(); }
        catch (Exception ignore) { }

        try { pstmtc2.close(); }
        catch (Exception ignore) { }
    }

    // Initialize course_count_totals ArrayList
    for (i=0; i<courses.size(); i++) {
        course_count_totals.add(0);
    }
    

    // set the calendar to last month if needed
    if (period.equals("last")) cal.add(cal.MONTH, -1);
    
    // populate our date part variables for use in the sql statement
    month = cal.get(cal.MONTH) + 1;
    year = cal.get(cal.YEAR);
    
    // set the year to last year if needed
    if (period.equals("lstyr")) year--;

    
    //
    //  The following should only be active when running a custom report for a single club on the dev server!!!!!
    //
   /*
    period = "custom";         // indicate custom report
    
    sdate = 20071001;          // change these dates as needed and un-comment this block - the rest will work as is.
    edate = 20080930;
    */
    //                   END OF CUSTOM
    
    
    
    // 1  SELECT m.username, m.name_last, m.name_first, m.name_mi, m.m_ship, m.m_type, m.memNum FROM member2b m ORDER BY m.name_last, m.name_first, m.name_mi
    // 2  SELECT count(mm) AS total FROM teepast2 WHERE mm = 3 AND yy = 2005 AND ((username1 = 1900 AND show1 = 1) OR (username2 = 1900 AND show2 = 1) OR (username3 = 1900 AND show3 = 1) OR (username4 = 1900 AND show4 = 1))
    
    // start page output
    startPageOutput(excel, out);
    
    if (!excel.equals("yes")) {                // if not Excel Spreadsheet Format

       SystemUtils.getProshopSubMenu(req, out, lottery);               // required to allow submenus on this page
    }
    
    SimpleDateFormat tmpA = new SimpleDateFormat("MMMM yyyy");
    
    // output report title
    out.println("<font face=\"Arial, Helvetica, Sans-serif\"><b>Member Rounds Report For ");
      
    if (period.equals("year") || period.equals("lstyr")) {     // if for whole year

       out.println(year);

    } else if (period.equals("custom")) {

       out.println(sdate+ " to " +edate);

    } else {

       out.println(tmpA.format(cal.getTime()));
    }

    out.println("</b></font></center>");
    
    if (!excel.equals("yes")) {                // if not Excel Spreadsheet Format

       out.println("<br><table border=\"0\" align=\"center\">");
       out.println("<tr><td>");
       out.println("<input type=\"button\" value=\"Print\" onclick=\"window.print();\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp;");
       out.println("</td>");
       out.println("<form method=\"post\" action=\"Proshop_report_member_rounds_by_course\" target=\"_blank\">");
       out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
       out.println("<input type=\"hidden\" name=\"period\" value=\"" + period + "\">");
       out.println("<input type=\"hidden\" name=\"continue\" value=\"yes\">");
       out.println("<td>");
       out.println("<input type=\"submit\" value=\"Excel\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp;");
       out.println("</td>");
       out.println("</form>");
       out.println("<td>");
       out.println("<input type=\"button\" value=\"Exit\" onclick=\"document.location.href='Proshop_announce'\" style=\"text-decoration:underline; background:#8B8970\">");
       out.println("</td></tr></table><br>");

    }
    
    // start table output
    out.println("<table align=\"center\" cellpadding=\"3\" bgcolor=\"#F5F5DC\" style=\"border: 1px solid black\">");
    out.println("<tr bgcolor=\"#336633\" style=\"font-family:verdana;color:white;font-size:.9em\">" +
                "<td><b>Member Name&nbsp;&nbsp;&nbsp;&nbsp;</b></td>" +
                "<td><b>Membership&nbsp;&nbsp;</b></td>" +
                "<td><b>Member Type&nbsp;&nbsp;&nbsp;</b></td>" +
                "<td><b>Member Number&nbsp;</b></td>" +
                "<td><b>9 Hole&nbsp;</b></td>" +
                "<td><b>18 Hole&nbsp;</b></td>");
                
                  for (i=0; i<courses.size(); i++) {     // add hdng for each course
                        out.println("<td><b>" + (!courses.get(i).equals("") ? courses.get(i) : "No Course Name") + "&nbsp;</b></td>");
                  }             
                out.println("</tr>");
    
    int alt_row = 0;
        
      if (period.equals("year") || period.equals("lstyr")) {            // if for whole year

         whereString = "yy = ? AND " +
                    "((username1 = ? AND show1 = 1) OR (username2 = ? AND show2 = 1) OR " + 
                    "(username3 = ? AND show3 = 1) OR (username4 = ? AND show4 = 1) OR " + 
                    "(username5 = ? AND show5 = 1))";

      } else if (period.equals("custom")) {

         whereString = "date >= ? AND date <= ? AND " +
                    "((username1 = ? AND show1 = 1) OR (username2 = ? AND show2 = 1) OR " + 
                    "(username3 = ? AND show3 = 1) OR (username4 = ? AND show4 = 1) OR " + 
                    "(username5 = ? AND show5 = 1))";

      } else {

         whereString = "mm = ? AND yy = ? AND " +
                    "((username1 = ? AND show1 = 1) OR (username2 = ? AND show2 = 1) OR " + 
                    "(username3 = ? AND show3 = 1) OR (username4 = ? AND show4 = 1) OR " + 
                    "(username5 = ? AND show5 = 1))";
      }

      sql2 = "SELECT " + 
               "username1, username2, username3, username4, username5,  " + 
               "p1cw, p2cw, p3cw, p4cw, p5cw, show1, show2, show3, show4, show5, " +
               "p91, p92, p93, p94, p95, courseName " +
               "FROM teepast2 " + 
               "WHERE " + whereString;
       
    try {
              
        sql = "SELECT username, name_last, name_first, name_mi, m_ship, m_type, memNum FROM member2b ORDER BY name_last, name_first, name_mi";
        stmt1 = con.createStatement();
        rs1 = stmt1.executeQuery(sql);
        
        while (rs1.next()) {         // check each member

            username = rs1.getString("username");       // get username to check

            p9 = 0;       // reset counters
            p18 = 0;

            // Reset and re-initialize course count ArrayList
            course_counts.clear();

            for (i=0; i<courses.size(); i++) {
                course_counts.add(0);
            }
              
            pstmtc2 = con.prepareStatement(sql2);
            pstmtc2.clearParameters();
              
            if (period.equals("year") || period.equals("lstyr")) {            // if for whole year

               pstmtc2.setInt(1, year);
               pstmtc2.setString(2, username);
               pstmtc2.setString(3, username);
               pstmtc2.setString(4, username);
               pstmtc2.setString(5, username);
               pstmtc2.setString(6, username);

            } else if (period.equals("custom")) {

               pstmtc2.setLong(1, sdate);
               pstmtc2.setLong(2, edate);
               pstmtc2.setString(3, username);
               pstmtc2.setString(4, username);
               pstmtc2.setString(5, username);
               pstmtc2.setString(6, username);
               pstmtc2.setString(7, username);
               
            } else {

               pstmtc2.setInt(1, month);
               pstmtc2.setInt(2, year);
               pstmtc2.setString(3, username);
               pstmtc2.setString(4, username);
               pstmtc2.setString(5, username);
               pstmtc2.setString(6, username);
               pstmtc2.setString(7, username);
            }

            rs2 = pstmtc2.executeQuery();      // execute the prepared stmt

            while (rs2.next()) {

                user1 = rs2.getString("username1");
                user2 = rs2.getString("username2");
                user3 = rs2.getString("username3");
                user4 = rs2.getString("username4");
                user5 = rs2.getString("username5");
                p1cw = rs2.getString("p1cw");
                p2cw = rs2.getString("p2cw");
                p3cw = rs2.getString("p3cw");
                p4cw = rs2.getString("p4cw");
                p5cw = rs2.getString("p5cw");
                p91 = rs2.getInt("p91");
                p92 = rs2.getInt("p92");
                p93 = rs2.getInt("p93");
                p94 = rs2.getInt("p94");
                p95 = rs2.getInt("p95");
                show1 = rs2.getInt("show1");
                show2 = rs2.getInt("show2");
                show3 = rs2.getInt("show3");
                show4 = rs2.getInt("show4");
                show5 = rs2.getInt("show5");
                courseName = rs2.getString("courseName");

               if (user1.equals(username) && show1 == 1) {       // if this user and checked in
                  
                  if (p91 == 1) {
                     p9++;         // add to 9 hole count  
                     p9t++;        // add to total
                  } else {
                     p18++;        // add to 18 hole count                
                     p18t++;       // add to total
                  }
                  
                  loop1:
                  for (i=0; i<courses.size(); i++) {         // locate the matching course

                     if (courses.get(i).equals(courseName)) {

                        course_counts.set(i, course_counts.get(i) + 1);
                        course_count_totals.set(i, course_count_totals.get(i) + 1);
                        break loop1;                       // done
                     }
                  }         
               }
               
               if (user2.equals(username) && show2 == 1) {       // if this user and checked in
                  
                  if (p92 == 1) {
                     p9++;         // add to 9 hole count                
                     p9t++;        // add to total
                  } else {
                     p18++;        // add to 18 hole count                
                     p18t++;        // add to total
                  }
                  
                  loop2:
                  for (i=0; i<courses.size(); i++) {         // locate the matching course

                     if (courses.get(i).equals(courseName)) {

                        course_counts.set(i, course_counts.get(i) + 1);
                        course_count_totals.set(i, course_count_totals.get(i) + 1);
                        break loop2;                       // done
                     }
                  }         
               }
               
               if (user3.equals(username) && show3 == 1) {       // if this user and checked in
                  
                  if (p93 == 1) {
                     p9++;         // add to 9 hole count                
                     p9t++;        // add to total
                  } else {
                     p18++;        // add to 18 hole count                
                     p18t++;        // add to total
                  }
                  
                  loop3:
                  for (i=0; i<courses.size(); i++) {         // locate the matching course

                     if (courses.get(i).equals(courseName)) {

                        course_counts.set(i, course_counts.get(i) + 1);
                        course_count_totals.set(i, course_count_totals.get(i) + 1);
                        break loop3;                       // done
                     }
                  }         
               }
               
               if (user4.equals(username) && show4 == 1) {       // if this user and checked in
                  
                  if (p94 == 1) {
                     p9++;         // add to 9 hole count                
                     p9t++;        // add to total
                  } else {
                     p18++;        // add to 18 hole count                
                     p18t++;        // add to total
                  }
                  
                  loop4:
                  for (i=0; i<courses.size(); i++) {         // locate the matching course

                     if (courses.get(i).equals(courseName)) {

                        course_counts.set(i, course_counts.get(i) + 1);
                        course_count_totals.set(i, course_count_totals.get(i) + 1);
                        break loop4;                       // done
                     }
                  }         
               }
               
               if (user5.equals(username) && show5 == 1) {       // if this user and checked in
                  
                  if (p95 == 1) {
                     p9++;         // add to 9 hole count                
                     p9t++;        // add to total
                  } else {
                     p18++;        // add to 18 hole count                
                     p18t++;        // add to total
                  }
                  
                  loop5:
                  for (i=0; i<courses.size(); i++) {         // locate the matching course

                     if (courses.get(i).equals(courseName)) {

                        course_counts.set(i, course_counts.get(i) + 1);
                        course_count_totals.set(i, course_count_totals.get(i) + 1);
                        break loop5;                       // done
                     }
                  }         
               }
               
               
            } // end nested rs2 loop (tee times)

               
            if (p9 > 0 || p18 > 0 ) {

                 name_last = rs1.getString("name_last");
                 name_first = rs1.getString("name_first");
                 name_mi = rs1.getString("name_mi");
                 m_ship = rs1.getString("m_ship");
                 m_type = rs1.getString("m_type");
                 mem_num = rs1.getString("memNum");
                 alt_row = (alt_row == 0) ? 1 : 0;
                 out.println("<tr" + ((alt_row == 0) ? " bgcolor=\"#FDFDEF\"" : "") + " style=\"font-family:arial;color:black;font-size:.8em\">");
                 out.println("<td nowrap>" + name_last + ", " + name_first + " " + name_mi + "</td>");
                 out.println("<td>" + m_ship + "</td><td>" + m_type + "</td><td align=\"center\">" + mem_num + "</td>" +
                         "<td align=\"center\">" + p9 + "</td><td align=\"center\">" + p18 + "</td>");

                  for (i=0; i<courses.size(); i++) {     // add count for each course

                     out.println("<td align=\"center\">" + course_counts.get(i) + "</td>");
                  }                                         
                 out.println("</tr>");    // end of this row (one per member)

            } // end of IF any rounds found

            out.flush(); // try to prevent the load balancer from timing out (12 minutes)
            
            pstmtc2.close();
            
        } // end main rs1 loop (members)
        
        stmt1.close();
    }
    catch (Exception e) {
        displayDatabaseErrMsg("Error loading member information for report.", e.getMessage(), out);
        return;
        
    }
    
    // output the Totals row
    
     out.println("<tr" + ((alt_row == 0) ? " bgcolor=\"#FDFDEF\"" : "") + " style=\"font-family:arial;color:black;font-size:.8em\">");
     out.println("<td nowrap><b>TOTALS</b></td>");
     out.println("<td>&nbsp;</td><td>&nbsp;</td><td align=\"center\">&nbsp;</td>" +
             "<td align=\"center\">" + p9t + "</td><td align=\"center\">" + p18t + "</td>");

      for (i=0; i<courses.size(); i++) {     // add total for each course

         out.println("<td align=\"center\">" + course_count_totals.get(i) + "</td>");
      }                                         
     out.println("</tr>");    // end of this row (one per member)

    out.println("</table><br><br>");
    
    endPageOutput(out);
    
 } // end of doPost routine
 

//
//  Start of Page
//
 private void startPageOutput(String excel, PrintWriter out) {
    
    if (!excel.equals("yes")) {                // if not Excel Spreadsheet Format

       out.println(SystemUtils.HeadTitle("Member Rounds Report"));
       
    } else {
    
       out.println("<html><head><title>Member Rounds Report</title></head>");
    }
    
    out.println("<BODY bgcolor=white><CENTER>");
 }
 

//
//  End of Page
//
 private void endPageOutput(PrintWriter out) {
    
    out.println("</body></html>");
    out.close();
 }
 

//
//   Error Message
//
 private void displayDatabaseErrMsg(String pMessage, String pException, PrintWriter out) {

    out.println(SystemUtils.HeadTitle("Database Error"));
    out.println("<BODY><CENTER>");
    out.println("<BR><BR><H2>Database Access Error</H2>");
    out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
    out.println("<BR>Please try again later.");
    out.println("<BR><br>Fatal Error: " + pMessage);
    out.println("<BR><br>Exception: " + pException);
    out.println("<BR><BR>If problem persists, contact customer support.");
    out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
    out.println("</CENTER></BODY></HTML>");
 }


//
//   Warning Message
//
 private void displayWarning(String period, PrintWriter out) {

    out.println(SystemUtils.HeadTitle("Report Warning"));
    out.println("<BODY><CENTER>");
    out.println("<BR><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
    out.println("<hr width=\"40%\">");
    out.println("<BR><H2>WARNING</H2>");
    out.println("This report may take several minutes to complete.");
    out.println("<BR>Please be patient.");
    out.println("<BR>Contact ForeTees Support if you receive a timeout error message.");
    out.println("<BR><BR>To continue, select the format you would like.");
    out.println("<BR><BR>");
    out.println("<form method=\"post\" action=\"Proshop_report_member_rounds_by_course\">");
    out.println("<input type=\"hidden\" name=\"period\" value=\"" +period+ "\">");
    out.println("<input type=\"hidden\" name=\"continue\" value=\"yes\">");
    out.println("<input type=\"submit\" value=\"Web Page\" style=\"text-decoration:underline; background:#8B8970\">");
    out.println("</form>");
    out.println("<form method=\"post\" action=\"Proshop_report_member_rounds_by_course\" target=\"_blank\">");
    out.println("<input type=\"hidden\" name=\"period\" value=\"" +period+ "\">");
    out.println("<input type=\"hidden\" name=\"continue\" value=\"yes\">");
    out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
    out.println("<input type=\"submit\" value=\"Excel\" style=\"text-decoration:underline; background:#8B8970\">");
    out.println("</form>");
    out.println("<BR>");
    out.println("<form method=\"get\" action=\"Proshop_announce\">");
    out.println("<input type=\"submit\" value=\"Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
    out.println("</form>");
    out.println("</CENTER></BODY></HTML>");
 }

} // end servlet public class
