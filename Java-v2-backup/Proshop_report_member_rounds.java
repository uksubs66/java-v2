/***************************************************************************************
 *   Proshop_report_member_rounds: This servlet will ouput the member rounds report
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
 *            11/20/13 RDP  Add custom to display the members' email addresses (boolean turns on or off).
 *            10/24/13 BSK  Added an "Inactive" column to the various member round reports to indiacte which members are active and inactive.
 *             5/20/13 BSK  Fixed issue where month values were not being forced to be 2 digits long in the start and end date for the report.
 *            11/18/12 PTS  MySQL 5.5 enhancements - utilize the get_918_teepast_by_player function & simplify the parameters
 *             4/23/10 BSK  Added option to report rounds by member number instead of individual username.  Add club names to custom in displayWarning method
 *             4/16/10      Add custom for Oakmont to record all the current tee times for each member (vs past times).
 *             4/20/09 RDP  Always list all members regardless of number of rounds.
 *            10/06/08 RDP  Add a custom date range option to be customized for each club on demand (for dev server only).
 *                          Look for period = 'custom'.
 *             7/18/08 BSK  Added limited access proshop users checks
 *             4/07/08 PTS  Added flush call to hopefully prevent the load balancer from timing out
 *             2/08/08 PTS  Add 9/18 hole breakdown for Case # 1189
 *             1/05/07 RDP  Add an option for last year.                  
 *            10/26/05 RDP  Add an option to display counts for the current year.
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
import java.text.SimpleDateFormat;

// foretees imports
import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.parmPOS;
import com.foretees.common.getClub;
import com.foretees.common.Utilities;
import com.foretees.client.action.ActionHelper;
import com.foretees.common.Connect;


public class Proshop_report_member_rounds extends HttpServlet {

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
    
    boolean zeros = true;   // Since it's always on now, default to true, set to false here if desired for custom reports
    
        
    String excel = (req.getParameter("excel") != null) ? "yes" : "";
        
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
       
    String period = (req.getParameter("period") != null) ? req.getParameter("period")  : "";
    
    
    long sdate = 0;    // for custom date range
    long edate = 0;
    // handle excel output
    try{
        if (excel.equals("yes")) {                // if user requested Excel Spreadsheet Format
            resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
            resp.setHeader("Content-Disposition", "attachment;filename=\""+club+".xls\"");
        }
    }
    catch (Exception exc) {
    }
    
    // strict enforce either value, default to this month
    if (!period.equals("last") && !period.equals("year") && !period.equals("lstyr") && !period.startsWith("custom")) {
    
       period = "this";
    }
    
    
    // if Custom Date Range selection (first entry) - prompt user for dates
    
    if (period.equals("custom")) {
    
       promptDates(club, out);              // prompt user for date range
       return;                              // exit and wait for dates
       
    } else if (req.getParameter("custom2") != null) {   // custom date range form does not include the 'period' parm, only custom2

       //  Custom date range specified (user selected the dates)
       
       period = "custom2";         // Custom Date Range selected
       
       //     Get the dates selected
       
       String smonth = req.getParameter("smonth");
       String sday = req.getParameter("sday");
       String syear = req.getParameter("syear");

       String emonth = req.getParameter("emonth");
       String eday = req.getParameter("eday");
       String eyear = req.getParameter("eyear");

       int mm  = 0;
       int dd = 0;
       int yy = 0;
       
       try {
         mm = Integer.parseInt(smonth);
         dd = Integer.parseInt(sday);
         yy = Integer.parseInt(syear);
       }
       catch (NumberFormatException e) {
         // ignore error
       }

       sdate = (yy * 10000) + (mm * 100) + dd;      // create a date field of yyyymmdd

       try {
         mm = Integer.parseInt(emonth);
         dd = Integer.parseInt(eday);
         yy = Integer.parseInt(eyear);
       }
       catch (NumberFormatException e) {
         // ignore error
       }

       edate = (yy * 10000) + (mm * 100) + dd;      // create a date field of yyyymmdd
    }
    
    

    // 
    //  If first time here - display warning message to inform user that this report can take several minutes!!!!
    //
    if (req.getParameter("continue") == null) {

       displayWarning(club, period, sdate, edate, out);              // display warning message
       return;                                   // exit and wait for continue
    }

    
    //
    //  User was already warned and prompted for Web Page or Excel output
    //
    
    //
    //   Get date range if custom dates requested and specified
    //
    if (period.equals("custom2")) {

       if (req.getParameter("sdate") != null) {
          
          String temp = req.getParameter("sdate");      
          sdate = Long.parseLong(temp);
       }

       if (req.getParameter("edate") != null) {
          
          String temp = req.getParameter("edate");      
          edate = Long.parseLong(temp);
       }
    }

    
    //
    //   Do report by Member Number if requested
    //
    if (req.getParameter("byMnum") != null) {

       reportByMemNum(req, excel, lottery, zeros, period, sdate, edate, con, out);
       return;                                
    }

    //
    //  Declare our local variables
    //
    Statement stmt1 = null;
    PreparedStatement pstmtc1 = null;
    ResultSet rs1 = null;
    Statement stmt2 = null;
    PreparedStatement pstmtc2 = null;
    ResultSet rs2 = null;
    
    GregorianCalendar cal = new GregorianCalendar();            // get todays date
    
    int month = 0;  // will hold last month value
    int year = 0;   // will hold current year
    int p9 = 0;     // 9 hole counter
    int p18 = 0;    // 18 hole counter
    int inactive = 0;   // inactive indicator
    
    String username = "";
    String name_last = "";
    String name_first = "";
    String name_mi = "";
    String m_ship = "";
    String m_type = "";
    String mem_num = "";
    String sql;
    String start_date = "";
    String end_date = "";
    String email = "";
    
    // set the calendar to last month if needed
    if (period.equals("last")) cal.add(cal.MONTH, -1);
    
    // populate our date part variables for use in the sql statement
    month = cal.get(cal.MONTH) + 1;
    year = cal.get(cal.YEAR);
    
    // set the year to last year if needed
    if (period.equals("lstyr")) year--;

    
    
    
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

    } else if (period.equals("custom2")) {

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
       
       if (!period.equals("custom2")) {       // skip if custom date range

          out.println("<form method=\"post\" action=\"Proshop_report_member_rounds\" target=\"_blank\">");
          out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
          out.println("<input type=\"hidden\" name=\"period\" value=\"" + period + "\">");
          out.println("<input type=\"hidden\" name=\"continue\" value=\"yes\">");
          out.println("<td>");
          out.println("<input type=\"submit\" value=\"Excel\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp;");
          out.println("</td>");
          out.println("</form>");
       }
       out.println("<td>");
       out.println("<input type=\"button\" value=\"Exit\" onclick=\"document.location.href='Proshop_announce'\" style=\"text-decoration:underline; background:#8B8970\">");
       out.println("</td></tr></table><br>");

    }
    
    
    
    //
    //  CUSTOM to add EMAIL addresses - turn on or off manually !!!!!!!!!!!!!!!!!
    //
    boolean includeEmails = false;
    
    //if (club.equals("ballenisles")) includeEmails = true;        // temp !!!!!
    
    
    
    
    // start table output
    out.println("<table align=\"center\" cellpadding=\"3\" bgcolor=\"#F5F5DC\" style=\"border: 1px solid black\">");
    out.println("<tr bgcolor=\"#336633\" style=\"font-family:verdana;color:white;font-size:.9em\">" +
                "<td><b>Member Name&nbsp;&nbsp;&nbsp;&nbsp;</b></td>" +
                "<td><b>Membership&nbsp;&nbsp;</b></td>" +
                "<td><b>Member Type&nbsp;&nbsp;&nbsp;</b></td>" +
                "<td><b>Member Number&nbsp;</b></td>" +
                "<td><b>9 Hole&nbsp;</b></td>" +
                "<td><b>18 Hole&nbsp;</b></td>" +
                "<td><b>Inactive&nbsp;</b></td>");
    
    if (includeEmails == true) {
       
        out.println("<td><b>Email Address&nbsp;</b></td>");
    }
    out.println("</tr>");
    
    int alt_row = 0;
        
    try {
        //sql = "SELECT username, name_last, name_first, name_mi, m_ship, m_type, memNum FROM member2b ORDER BY name_last, name_first, name_mi";
        //stmt1 = con.createStatement();
        //rs1 = stmt1.executeQuery(sql);
        
        //while (rs1.next()) {
            
            //username = rs1.getString("username");
            
            sql = "SELECT m.username, m.name_last, m.name_first, m.name_mi, m.m_ship, m.m_type, m.email, m.memNum, inact, " +
                    "get_918_teepast_by_player(m.username,?,?) AS p918 " +
                    "FROM member2b m " +
                    "ORDER BY m.name_last, m.name_first, m.name_mi";
            
            pstmtc2 = con.prepareStatement(sql);
            pstmtc2.clearParameters();
              
            if (period.equals("year") || period.equals("lstyr")) {            // if for whole year

               start_date = Integer.toString(year) + "0101";
               end_date = Integer.toString(year) + "1231";

            } else if (period.equals("custom2")) {

               start_date = Long.toString(sdate);
               end_date = Long.toString(edate);
               
            } else {
                
               start_date = Integer.toString(year) + (month < 10 ? "0" : "") + Integer.toString(month) + "01";
               end_date = Integer.toString(year) + (month < 10 ? "0" : "") + Integer.toString(month) + "31";

            }
            
            pstmtc2.setString(1, start_date);
            pstmtc2.setString(2, end_date);
            
            rs2 = pstmtc2.executeQuery();      // execute the prepared stmt

            while (rs2.next()) {

                String[] p918 = rs2.getString("p918").split(";");
                
                p9 = Integer.parseInt( p918[0] );
                p18 = Integer.parseInt( p918[1] );

                if ( ((p9 + p18) != 0) || zeros == true ) {
                    
                    name_last = rs2.getString("name_last");
                    name_first = rs2.getString("name_first");
                    name_mi = rs2.getString("name_mi");
                    m_ship = rs2.getString("m_ship");
                    m_type = rs2.getString("m_type");
                    mem_num = rs2.getString("memNum");
                    email = rs2.getString("email");
                    inactive = rs2.getInt("inact");
                    alt_row = (alt_row == 0) ? 1 : 0;
                    out.println("<tr" + ((alt_row == 0) ? " bgcolor=\"#FDFDEF\"" : "") + " style=\"font-family:arial;color:black;font-size:.8em\">");
                    out.println("<td nowrap>" + name_last + ", " + name_first + " " + name_mi + "</td>");
                    out.println("<td>" + m_ship + "</td><td>" + m_type + "</td><td align=\"center\">" + mem_num + "</td>" +
                            "<td align=\"center\">" + p9 + "</td><td align=\"center\">" + p18 + "</td><td align=\"center\">" + (inactive == 0 ? "Active" : "Inactive") + "</td>");
                                              
                    if (includeEmails == true) {

                        out.println("<td nowrap>" +email+ "</td>");
                    }
                    out.println("</tr>");
                    
                } // end if tee times where found block

            } // end nested rs2 loop (tee times)

            out.flush(); // try to prevent the load balancer from timing out (12 minutes)
            
            pstmtc2.close();
            
            /*
            if (period.equals("year") || period.equals("lstyr")) {            // if for whole year

                sql = "SELECT " + 
                        "(SELECT COUNT(mm) " + 
                        "FROM teepast2 " + 
                        "WHERE yy = ? AND " + 
                        "(" + 
                          "(username1 = ? AND show1 = 1 AND p91 = 1) OR (username2 = ? AND show2 = 1 AND p92 = 1) OR " + 
                          "(username3 = ? AND show3 = 1 AND p93 = 1) OR (username4 = ? AND show4 = 1 AND p94 = 1) OR " + 
                          "(username5 = ? AND show5 = 1 AND p95 = 1)" +
                        ")) AS nine, " + 
                        "(SELECT COUNT(mm) " + 
                        "FROM teepast2 " + 
                        "WHERE yy = ? AND " + 
                        "(" + 
                          "(username1 = ? AND show1 = 1 AND p91 = 0) OR (username2 = ? AND show2 = 1 AND p92 = 0) OR " + 
                          "(username3 = ? AND show3 = 1 AND p93 = 0) OR (username4 = ? AND show4 = 1 AND p94 = 0) OR " + 
                          "(username5 = ? AND show5 = 1 AND p95 = 0)" + 
                        ")) AS eighteen";
                
            } else if (period.equals("custom2")) {

                sql = "SELECT " + 
                        "(SELECT COUNT(mm) " + 
                        "FROM teepast2 " + 
                        "WHERE date >= ? AND date <= ? AND " + 
                        "(" + 
                          "(username1 = ? AND show1 = 1 AND p91 = 1) OR (username2 = ? AND show2 = 1 AND p92 = 1) OR " + 
                          "(username3 = ? AND show3 = 1 AND p93 = 1) OR (username4 = ? AND show4 = 1 AND p94 = 1) OR " + 
                          "(username5 = ? AND show5 = 1 AND p95 = 1)" +
                        ")) AS nine, " + 
                        "(SELECT COUNT(mm) " + 
                        "FROM teepast2 " + 
                        "WHERE date >= ? AND date <= ? AND " + 
                        "(" + 
                          "(username1 = ? AND show1 = 1 AND p91 = 0) OR (username2 = ? AND show2 = 1 AND p92 = 0) OR " + 
                          "(username3 = ? AND show3 = 1 AND p93 = 0) OR (username4 = ? AND show4 = 1 AND p94 = 0) OR " + 
                          "(username5 = ? AND show5 = 1 AND p95 = 0)" + 
                        ")) AS eighteen";
                              
            } else {

                sql = "SELECT " + 
                        "(SELECT COUNT(mm) " + 
                        "FROM teepast2 " + 
                        "WHERE mm = ? AND yy = ? AND " + 
                        "(" + 
                          "(username1 = ? AND show1 = 1 AND p91 = 1) OR (username2 = ? AND show2 = 1 AND p92 = 1) OR " + 
                          "(username3 = ? AND show3 = 1 AND p93 = 1) OR (username4 = ? AND show4 = 1 AND p94 = 1) OR " + 
                          "(username5 = ? AND show5 = 1 AND p95 = 1)" + 
                        ")) AS nine, " + 
                        "(SELECT COUNT(mm) " + 
                        "FROM teepast2 " + 
                        "WHERE mm = ? AND yy = ? AND " + 
                        "(" + 
                          "(username1 = ? AND show1 = 1 AND p91 = 0) OR (username2 = ? AND show2 = 1 AND p92 = 0) OR " + 
                          "(username3 = ? AND show3 = 1 AND p93 = 0) OR (username4 = ? AND show4 = 1 AND p94 = 0) OR " + 
                          "(username5 = ? AND show5 = 1 AND p95 = 0)" + 
                        ")) AS eighteen";
                
            }

            pstmtc2 = con.prepareStatement(sql);
            pstmtc2.clearParameters();
              
            if (period.equals("year") || period.equals("lstyr")) {            // if for whole year

               pstmtc2.setInt(1, year);
               pstmtc2.setString(2, username);
               pstmtc2.setString(3, username);
               pstmtc2.setString(4, username);
               pstmtc2.setString(5, username);
               pstmtc2.setString(6, username);
               pstmtc2.setInt(7, year);
               pstmtc2.setString(8, username);
               pstmtc2.setString(9, username);
               pstmtc2.setString(10, username);
               pstmtc2.setString(11, username);
               pstmtc2.setString(12, username);

            } else if (period.equals("custom2")) {

               pstmtc2.setLong(1, sdate);
               pstmtc2.setLong(2, edate);
               pstmtc2.setString(3, username);
               pstmtc2.setString(4, username);
               pstmtc2.setString(5, username);
               pstmtc2.setString(6, username);
               pstmtc2.setString(7, username);
               pstmtc2.setLong(8, sdate);
               pstmtc2.setLong(9, edate);
               pstmtc2.setString(10, username);
               pstmtc2.setString(11, username);
               pstmtc2.setString(12, username);
               pstmtc2.setString(13, username);
               pstmtc2.setString(14, username);
               
            } else {

               pstmtc2.setInt(1, month);
               pstmtc2.setInt(2, year);
               pstmtc2.setString(3, username);
               pstmtc2.setString(4, username);
               pstmtc2.setString(5, username);
               pstmtc2.setString(6, username);
               pstmtc2.setString(7, username);
               pstmtc2.setInt(8, month);
               pstmtc2.setInt(9, year);
               pstmtc2.setString(10, username);
               pstmtc2.setString(11, username);
               pstmtc2.setString(12, username);
               pstmtc2.setString(13, username);
               pstmtc2.setString(14, username);
            }
           */

            
            /*
       //   start of temp to gather counts from teecurr2 - for Oakmont  (MUST comment out the above statements)             
            
             sql = "SELECT " + 
                        "(SELECT COUNT(mm) " + 
                        "FROM teecurr2 " + 
                        "WHERE yy = ? AND " + 
                        "(" + 
                          "(username1 = ? AND p91 = 1) OR (username2 = ? AND p92 = 1) OR " + 
                          "(username3 = ? AND p93 = 1) OR (username4 = ? AND p94 = 1) OR " + 
                          "(username5 = ? AND p95 = 1)" +
                        ")) AS nine, " + 
                        "(SELECT COUNT(mm) " + 
                        "FROM teecurr2 " + 
                        "WHERE yy = ? AND " + 
                        "(" + 
                          "(username1 = ? AND p91 = 0) OR (username2 = ? AND p92 = 0) OR " + 
                          "(username3 = ? AND p93 = 0) OR (username4 = ? AND p94 = 0) OR " + 
                          "(username5 = ? AND p95 = 0)" + 
                        ")) AS eighteen";
                
            pstmtc2 = con.prepareStatement(sql);
            pstmtc2.clearParameters();
                
            pstmtc2.setInt(1, year);
            pstmtc2.setString(2, username);
            pstmtc2.setString(3, username);
            pstmtc2.setString(4, username);
            pstmtc2.setString(5, username);
            pstmtc2.setString(6, username);
            pstmtc2.setInt(7, year);
            pstmtc2.setString(8, username);
            pstmtc2.setString(9, username);
            pstmtc2.setString(10, username);
            pstmtc2.setString(11, username);
            pstmtc2.setString(12, username);
                         
             */
       //  end of temp     
            
            /*
            rs2 = pstmtc2.executeQuery();      // execute the prepared stmt

            while (rs2.next()) {

                p9 = rs2.getInt("nine");
                p18 = rs2.getInt("eighteen");

                if ( ((p9 + p18) != 0) || zeros == true ) {
                    
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
                            "<td align=\"center\">" + p9 + "</td><td align=\"center\">" + p18 + "</td></tr>");
                    
                } // end if tee times where found block

            } // end nested rs2 loop (tee times)

            out.flush(); // try to prevent the load balancer from timing out (12 minutes)
            
            pstmtc2.close();
              */ 
        //} // end main rs1 loop (members)
        
        //stmt1.close();
    }
    catch (Exception e) {
        displayDatabaseErrMsg("Error loading member information for report.", e.getMessage(), out);
        return;
        
    }
    
    out.println("</table><br><br>");
    
    endPageOutput(out);
    
 } // end of doPost routine


 private void reportByMemNum(HttpServletRequest req, String excel, int lottery, boolean zeros, String period, long sdate, long edate, Connection con, PrintWriter out) {

    //
    //  Declare our local variables
    //
    Statement stmt1 = null;
    PreparedStatement pstmt = null;
    PreparedStatement pstmt2 = null;

    ResultSet rs = null;
    ResultSet rs2 = null;
    ResultSet rs3 = null;

    GregorianCalendar cal = new GregorianCalendar();            // get todays date

    int month = 0;  // will hold last month value
    int year = 0;   // will hold current year
    int p9 = 0;     // 9 hole counter
    int p18 = 0;    // 18 hole counter

    // long sdate = 0;    // for custom date range (custom reports to run one time)
    // long edate = 0;

    String mNum = "";
    String username = "";
    String name_last = "";
    String name_first = "";
    String name_mi = "";
    String m_ship = "";
    String m_type = "";
    String mem_num = "";
    String sql;

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
   /*      NO LONGER NEEDED - we now have a custom date range option
    * 
    period = "custom2";         // indicate custom report

    sdate = 20071001;          // change these dates as needed and un-comment this block - the rest will work as is.
    edate = 20080930;
    */
    //                   END OF CUSTOM


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

    } else if (period.equals("custom2")) {

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
       
       if (!period.equals("custom2")) {       // skip if custom date range

          out.println("<form method=\"post\" action=\"Proshop_report_member_rounds\" target=\"_blank\">");
          out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
          out.println("<input type=\"hidden\" name=\"byMnum\" value=\"1\">");
          out.println("<input type=\"hidden\" name=\"period\" value=\"" + period + "\">");
          out.println("<input type=\"hidden\" name=\"continue\" value=\"yes\">");
          out.println("<td>");
          out.println("<input type=\"submit\" value=\"Excel\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp;");
          out.println("</td>");
          out.println("</form>");
       }
       out.println("<td>");
       out.println("<input type=\"button\" value=\"Exit\" onclick=\"document.location.href='Proshop_announce'\" style=\"text-decoration:underline; background:#8B8970\">");
       out.println("</td></tr></table><br>");

    }

    // start table output
    out.println("<table align=\"center\" cellpadding=\"3\" bgcolor=\"#F5F5DC\" style=\"border: 1px solid black\">");
    out.println("<tr bgcolor=\"#336633\" style=\"font-family:verdana;color:white;font-size:.9em\">" +
                "<td><b>Member Number&nbsp;</b></td>" +
                "<td><b>9 Hole&nbsp;</b></td>" +
                "<td><b>18 Hole&nbsp;</b></td>" +
                "</tr>");

    int alt_row = 0;

    try {
        sql = "SELECT memNum FROM member2b GROUP BY memNum ORDER BY memNum";
        stmt1 = con.createStatement();
        rs = stmt1.executeQuery(sql);

        // Loop through all member numbers found
        while (rs.next()) {

            mNum = rs.getString("memNum");
            p9 = 0;
            p18 = 0;

            pstmt = con.prepareStatement("SELECT username FROM member2b WHERE memNum = ?");
            pstmt.clearParameters();
            pstmt.setString(1, mNum);

            rs2 = pstmt.executeQuery();

            while (rs2.next()) {

                username = rs2.getString("username");

                if (period.equals("year") || period.equals("lstyr")) {            // if for whole year

                    sql = "SELECT " +
                            "(SELECT COUNT(mm) " +
                            "FROM teepast2 " +
                            "WHERE yy = ? AND " +
                            "(" +
                              "(username1 = ? AND show1 = 1 AND p91 = 1) OR (username2 = ? AND show2 = 1 AND p92 = 1) OR " +
                              "(username3 = ? AND show3 = 1 AND p93 = 1) OR (username4 = ? AND show4 = 1 AND p94 = 1) OR " +
                              "(username5 = ? AND show5 = 1 AND p95 = 1)" +
                            ")) AS nine, " +
                            "(SELECT COUNT(mm) " +
                            "FROM teepast2 " +
                            "WHERE yy = ? AND " +
                            "(" +
                              "(username1 = ? AND show1 = 1 AND p91 = 0) OR (username2 = ? AND show2 = 1 AND p92 = 0) OR " +
                              "(username3 = ? AND show3 = 1 AND p93 = 0) OR (username4 = ? AND show4 = 1 AND p94 = 0) OR " +
                              "(username5 = ? AND show5 = 1 AND p95 = 0)" +
                            ")) AS eighteen";

                } else if (period.equals("custom2")) {

                    sql = "SELECT " +
                            "(SELECT COUNT(mm) " +
                            "FROM teepast2 " +
                            "WHERE date >= ? AND date <= ? AND " +
                            "(" +
                              "(username1 = ? AND show1 = 1 AND p91 = 1) OR (username2 = ? AND show2 = 1 AND p92 = 1) OR " +
                              "(username3 = ? AND show3 = 1 AND p93 = 1) OR (username4 = ? AND show4 = 1 AND p94 = 1) OR " +
                              "(username5 = ? AND show5 = 1 AND p95 = 1)" +
                            ")) AS nine, " +
                            "(SELECT COUNT(mm) " +
                            "FROM teepast2 " +
                            "WHERE date >= ? AND date <= ? AND " +
                            "(" +
                              "(username1 = ? AND show1 = 1 AND p91 = 0) OR (username2 = ? AND show2 = 1 AND p92 = 0) OR " +
                              "(username3 = ? AND show3 = 1 AND p93 = 0) OR (username4 = ? AND show4 = 1 AND p94 = 0) OR " +
                              "(username5 = ? AND show5 = 1 AND p95 = 0)" +
                            ")) AS eighteen";

                } else {

                    sql = "SELECT " +
                            "(SELECT COUNT(mm) " +
                            "FROM teepast2 " +
                            "WHERE mm = ? AND yy = ? AND " +
                            "(" +
                              "(username1 = ? AND show1 = 1 AND p91 = 1) OR (username2 = ? AND show2 = 1 AND p92 = 1) OR " +
                              "(username3 = ? AND show3 = 1 AND p93 = 1) OR (username4 = ? AND show4 = 1 AND p94 = 1) OR " +
                              "(username5 = ? AND show5 = 1 AND p95 = 1)" +
                            ")) AS nine, " +
                            "(SELECT COUNT(mm) " +
                            "FROM teepast2 " +
                            "WHERE mm = ? AND yy = ? AND " +
                            "(" +
                              "(username1 = ? AND show1 = 1 AND p91 = 0) OR (username2 = ? AND show2 = 1 AND p92 = 0) OR " +
                              "(username3 = ? AND show3 = 1 AND p93 = 0) OR (username4 = ? AND show4 = 1 AND p94 = 0) OR " +
                              "(username5 = ? AND show5 = 1 AND p95 = 0)" +
                            ")) AS eighteen";

                }

                pstmt2 = con.prepareStatement(sql);
                pstmt2.clearParameters();

                if (period.equals("year") || period.equals("lstyr")) {            // if for whole year

                   pstmt2.setInt(1, year);
                   pstmt2.setString(2, username);
                   pstmt2.setString(3, username);
                   pstmt2.setString(4, username);
                   pstmt2.setString(5, username);
                   pstmt2.setString(6, username);
                   pstmt2.setInt(7, year);
                   pstmt2.setString(8, username);
                   pstmt2.setString(9, username);
                   pstmt2.setString(10, username);
                   pstmt2.setString(11, username);
                   pstmt2.setString(12, username);

                } else if (period.equals("custom2")) {

                   pstmt2.setLong(1, sdate);
                   pstmt2.setLong(2, edate);
                   pstmt2.setString(3, username);
                   pstmt2.setString(4, username);
                   pstmt2.setString(5, username);
                   pstmt2.setString(6, username);
                   pstmt2.setString(7, username);
                   pstmt2.setLong(8, sdate);
                   pstmt2.setLong(9, edate);
                   pstmt2.setString(10, username);
                   pstmt2.setString(11, username);
                   pstmt2.setString(12, username);
                   pstmt2.setString(13, username);
                   pstmt2.setString(14, username);

                } else {

                   pstmt2.setInt(1, month);
                   pstmt2.setInt(2, year);
                   pstmt2.setString(3, username);
                   pstmt2.setString(4, username);
                   pstmt2.setString(5, username);
                   pstmt2.setString(6, username);
                   pstmt2.setString(7, username);
                   pstmt2.setInt(8, month);
                   pstmt2.setInt(9, year);
                   pstmt2.setString(10, username);
                   pstmt2.setString(11, username);
                   pstmt2.setString(12, username);
                   pstmt2.setString(13, username);
                   pstmt2.setString(14, username);
                }

                rs3 = pstmt2.executeQuery();      // execute the prepared stmt

                while (rs3.next()) {
                    p9 += rs3.getInt("nine");
                    p18 += rs3.getInt("eighteen");
                }

                pstmt2.close();
            }
            
            // Done gathering rounds for all members under this member number, prints totals.
            if ( ((p9 + p18) != 0) || zeros == true ) {

                alt_row = (alt_row == 0) ? 1 : 0;
                out.println("<tr" + ((alt_row == 0) ? " bgcolor=\"#FDFDEF\"" : "") + " style=\"font-family:arial;color:black;font-size:.8em\">");
                out.println("<td align=\"center\">" + (!mNum.equals("") ? mNum : "Unknown") + "</td><td align=\"center\">" + p9 + "</td><td align=\"center\">" + p18 + "</td></tr>");

            } // end if tee times where found block

            out.flush(); // try to prevent the load balancer from timing out (12 minutes)

            pstmt.close();

        } // end main rs loop (members)

        stmt1.close();
    }
    catch (Exception e) {
        displayDatabaseErrMsg("Error loading member information for report.", e.getMessage(), out);
        return;

    }

    out.println("</table><br><br>");

    endPageOutput(out);
 }

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


 // *********************************************************
 //  Prompt user for Custom Date Range Dates
 // *********************************************************

 private void promptDates(String club, PrintWriter out) {


   //
   //  Prompt user for date range
   //
   out.println(SystemUtils.HeadTitle("Report Date Prompt"));
   out.println("<BODY><CENTER>");
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td>");  

      out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" align=\"center\">");
      out.println("<tr><td align=\"center\">");
      out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("<b>Member Rounds Report</b><br>");
      out.println("<br>Select the date range below.<br>");
      out.println("<b>Note:</b>  Only rounds before today will be included in the counts.<br><br>");
      out.println("Click on <b>Continue</b> to generate the report.");
      out.println("</font></td></tr></table><br>");
      
      //
      // Build the custom date range calendars and form
      //
      Common_Config.buildReportCals("Proshop_report_member_rounds", out);
      
      out.println("</font></td></tr></form></table>");         // end of main page table

      out.println("<form method=\"get\" action=\"Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
   out.println("</center></font></body></html>");
   out.close();

 }  // end of promptDates


//
//   Warning Message
//
 private void displayWarning(String club, String period, long sdate, long edate, PrintWriter out) {

    out.println(SystemUtils.HeadTitle("Report Warning"));
    out.println("<BODY><CENTER>");
    out.println("<BR><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
    out.println("<hr width=\"40%\">");
    out.println("<BR><H2>WARNING</H2>");
    out.println("This report may take several minutes to complete.");
    out.println("<BR>Please be patient.");
    out.println("<BR><BR>Contact ForeTees Support if you receive a timeout error message.");
    out.println("<BR><BR>To continue, select the format you would like.");
    out.println("<BR><BR>");
    out.println("<form method=\"post\" action=\"Proshop_report_member_rounds\">");
    out.println("<input type=\"hidden\" name=\"period\" value=\"" +period+ "\">");
    
    if (sdate > 0) {       // if custom dates provided
       
       out.println("<input type=\"hidden\" name=\"sdate\" value=\"" +sdate+ "\">");
       out.println("<input type=\"hidden\" name=\"edate\" value=\"" +edate+ "\">");
    }
    out.println("<input type=\"hidden\" name=\"continue\" value=\"yes\">");
    if (club.equals("colletonriverclub")) out.println("<input type=\"checkbox\" name=\"byMnum\" value=\"1\">&nbsp;&nbsp;By member number<br><br>");
    out.println("<input type=\"submit\" name=\"webpage\" value=\"Web Page\" style=\"text-decoration:underline; background:#F5F5DC\">");
    out.println("<br><br><input type=\"submit\" name=\"excel\" value=\"Excel\" style=\"text-decoration:underline; background:#F5F5DC\">");
    out.println("</form>");
    out.println("<BR>");
    out.println("<form method=\"get\" action=\"Proshop_announce\">");
    out.println("<input type=\"submit\" value=\"Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
    out.println("</form>");
    out.println("</CENTER></BODY></HTML>");
 }

} // end servlet public class
