/***************************************************************************************
 *   Proshop_report_custom_member_rounds: This servlet will ouput a custom member rounds report
 *
 *
 *   Called by:     called by manually typing the url while logged in as a proshop user
 * 
 *                  use:  v5/servlet/Proshop_report_custom_member_rounds    (add ?excel=yes to go right to excel - much faster!!)
 * 
 * 
 *            ****** FOR CUSTOM REPORTS ONLY - NOT AVAILABLE TO CLUBS *********
 *            ****** RUN ON DEV SERVER!!! *********
 *
 *
 *   Created:     12/08/2008 by Bob
 *
 *       For:     Winged Foot originally, but can be used for any club
 * 
 * 
 *   Report Output:     Date  Time  Player  mNum  mtype  mship  9/18  (one line for each member tee time)
 * 
 *   Before Running:    Change the date fields below (sdate, edate, year)
 * 
 * 
 *        6/20/12   Rolling Hills GC - SA (rollinghillsgc) - Added custom report export which will produce a spreadsheet of all their tee times year to date.
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
import java.text.SimpleDateFormat;

// foretees imports
import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.parmPOS;
import com.foretees.common.getClub;
import com.foretees.common.Utilities;
import com.foretees.client.action.ActionHelper;


public class Proshop_report_custom_member_rounds extends HttpServlet {

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
    
    // Check Feature Access Rights for current proshop user
    if (!SystemUtils.verifyProAccess(req, "REPORTS", con, out)) {
        SystemUtils.restrictProshop("REPORTS", out);
        return;
    }
    
    String club = (String)session.getAttribute("club");
    String templott = (String)session.getAttribute("lottery");      // get lottery support indicator
    int lottery = Integer.parseInt(templott);
       
    if (club.equals("rollinghillsgc") && req.getParameter("rollinghillsgcreport") != null) {
        
        runCustomRollingHillsGCReport(out, con);
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

    
    
    long sdate = 20080101;              // for date range      *****  Change these *****
    long edate = 20081231;
    int year = 2008;
    
     
    String username = "";
    String user1 = "";
    String user2 = "";
    String user3 = "";
    String user4 = "";
    String user5 = "";
    String name_last = "";
    String name_first = "";
    String name_mi = "";
    String m_ship = "";
    String m_type = "";
    String mem_num = "";
    String sql;
    String sql2;
        
    long date = 0;
    int time = 0;
    int p91 = 0;
    int p92 = 0;
    int p93 = 0;
    int p94 = 0;
    int p95 = 0;
    int p9 = 0;
    
    boolean hit = false;
    
    
        
    // start page output
    startPageOutput(excel, out);
    
    if (!excel.equals("yes")) {                // if not Excel Spreadsheet Format

       SystemUtils.getProshopSubMenu(req, out, lottery);               // required to allow submenus on this page
    }
    
    
    // output report title
    out.println("<font face=\"Arial, Helvetica, Sans-serif\"><b>Member Rounds Report For " +year);
      
    out.println("</b></font></center>");
    
    if (!excel.equals("yes")) {                // if not Excel Spreadsheet Format

       out.println("<br><table border=\"0\" align=\"center\">");
       out.println("<tr><td>");
       out.println("<input type=\"button\" value=\"Print\" onclick=\"window.print();\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp;");
       out.println("</td>");
       out.println("<form method=\"post\" action=\"Proshop_report_custom_member_rounds\" target=\"_blank\">");
       out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
       out.println("<td>");
       out.println("<input type=\"submit\" value=\"Excel\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp;");
       out.println("</td>");
       out.println("</form>");
       out.println("<td>");
       out.println("<input type=\"button\" value=\"Exit\" onclick=\"document.location.href='Proshop_announce'\" style=\"text-decoration:underline; background:#8B8970\">");
       out.println("</td></tr></table><br>");

    }
    
    
    //   Report Output:     Date  Time  Player  mNum  mtype  mship  9/18  
    //
    // start table output
    out.println("<table align=\"center\" cellpadding=\"3\" bgcolor=\"#F5F5DC\" style=\"border: 1px solid black\">");
    out.println("<tr bgcolor=\"#336633\" style=\"font-family:verdana;color:white;font-size:.9em\">" +
                "<td><b>Date&nbsp;&nbsp;&nbsp;</b></td>" +
                "<td><b>Time&nbsp;&nbsp;&nbsp;</b></td>" +
                "<td><b>Member Name&nbsp;&nbsp;</b></td>" +
                "<td><b>Member Number&nbsp;&nbsp;</b></td>" +
                "<td><b>Member Type&nbsp;&nbsp;&nbsp;</b></td>" +
                "<td><b>Membership&nbsp;&nbsp;</b></td>" +
                "<td><b>Holes&nbsp;</b></td>" +
                "</tr>");
    
    int alt_row = 0;
        
    try {
        sql = "SELECT username, name_last, name_first, name_mi, m_ship, m_type, memNum FROM member2b ORDER BY name_last, name_first, name_mi";

        sql2 = "SELECT date, time, username1, username2, username3, username4, username5, p91, p92, p93, p94, p95 " +
                    "FROM teepast2 WHERE date > ? AND date < ? AND " +
                    "((username1 = ? AND show1 = 1) OR (username2 = ? AND show2 = 1) OR " + 
                    "(username3 = ? AND show3 = 1) OR (username4 = ? AND show4 = 1) OR " + 
                    "(username5 = ? AND show5 = 1))" +
                    " ORDER BY date, time";
               
        stmt1 = con.createStatement();
        rs1 = stmt1.executeQuery(sql);      
        
        while (rs1.next()) {                      // get each member
            
            username = rs1.getString("username");
            
            hit = false;      // init to no tee times


            pstmtc2 = con.prepareStatement(sql2);     // find tee times for this user
            pstmtc2.clearParameters();
              
            pstmtc2.setLong(1, sdate);
            pstmtc2.setLong(2, edate);
            pstmtc2.setString(3, username);
            pstmtc2.setString(4, username);
            pstmtc2.setString(5, username);
            pstmtc2.setString(6, username);
            pstmtc2.setString(7, username);
               
            rs2 = pstmtc2.executeQuery();      // get the tee times

            while (rs2.next()) {

                date = rs2.getLong("date");
                time = rs2.getInt("time");
                user1 = rs2.getString("username1");
                user2 = rs2.getString("username2");
                user3 = rs2.getString("username3");
                user4 = rs2.getString("username4");
                user5 = rs2.getString("username5");
                p91 = rs2.getInt("p91");
                p92 = rs2.getInt("p92");
                p93 = rs2.getInt("p93");
                p94 = rs2.getInt("p94");
                p95 = rs2.getInt("p95");
                
                p9 = 18;      // default to 18 holes
                
                if (user1.equals( username )) {      // 9 or 18 holes?
                   if (p91 == 1) p9 = 9;
                }
                if (user2.equals( username )) {
                   if (p92 == 1) p9 = 9;
                }
                if (user3.equals( username )) {
                   if (p93 == 1) p9 = 9;
                }
                if (user4.equals( username )) {
                   if (p94 == 1) p9 = 9;
                }
                if (user5.equals( username )) {
                   if (p95 == 1) p9 = 9;
                }
                
                hit = true;                       // indicate at least one tee time found for this member
                
                   
                 //   Report Output:     Date  Time  Player  mNum  mtype  mship  9/18  
                
                 name_last = rs1.getString("name_last");
                 name_first = rs1.getString("name_first");
                 name_mi = rs1.getString("name_mi");
                 m_ship = rs1.getString("m_ship");
                 m_type = rs1.getString("m_type");
                 mem_num = rs1.getString("memNum");
                 alt_row = (alt_row == 0) ? 1 : 0;
                 out.println("<tr" + ((alt_row == 0) ? " bgcolor=\"#FDFDEF\"" : "") + " style=\"font-family:arial;color:black;font-size:.8em\">");
                 out.println("<td align=\"center\">" + date + "</td>");
                 out.println("<td align=\"center\">" + time + "</td>");
                 out.println("<td nowrap>" + name_last + ", " + name_first + " " + name_mi + "</td>");
                 out.println("<td align=\"center\">" + mem_num + "</td><td>" + m_type + "</td><td>" + m_ship + "</td><td align=\"center\">" + p9 + "</td></tr>");

            }    // end nested rs2 loop (tee times for each member)
            
            //
            //  If no tee times found for this member - list him/her anyway with holes=0
            //
            if (hit == false) {    // if no tee times
               
                 name_last = rs1.getString("name_last");
                 name_first = rs1.getString("name_first");
                 name_mi = rs1.getString("name_mi");
                 m_ship = rs1.getString("m_ship");
                 m_type = rs1.getString("m_type");
                 mem_num = rs1.getString("memNum");
                 alt_row = (alt_row == 0) ? 1 : 0;
                 out.println("<tr" + ((alt_row == 0) ? " bgcolor=\"#FDFDEF\"" : "") + " style=\"font-family:arial;color:black;font-size:.8em\">");
                 out.println("<td align=\"center\">0</td><td align=\"center\">0</td>");
                 out.println("<td nowrap>" + name_last + ", " + name_first + " " + name_mi + "</td>");
                 out.println("<td align=\"center\">" + mem_num + "</td><td>" + m_type + "</td><td>" + m_ship + "</td>" +
                             "<td align=\"center\">0</td></tr>");
            }
            

            out.flush(); // try to prevent the load balancer from timing out (12 minutes)
            
            pstmtc2.close();
               
        } // end main rs1 loop (members)
        
        stmt1.close();
    }
    catch (Exception e) {
        displayDatabaseErrMsg("Error loading member information for report.", e.getMessage(), out);
        return;
        
    }
    
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

 
 // Rolling Hills GC - Saudia Arabia Custom to print out past tee sheets (and today's) info for their manual handicap figuring
 private void runCustomRollingHillsGCReport (PrintWriter out, Connection con) {
     
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     
     long date = Utilities.getDate(con);
     long yy = date / 10000;
     
     out.println("<table>");
     out.println("<tr>");
     out.println("<td>Date</td><td>Time</td><td>FB</td>"
             + "<td>Player 1</td><td>Player 2</td><td>Player 3</td><td>Player 4</td><td>Player 5</td>"
             + "<td>Username 1</td><td>Username 2</td><td>Username 3</td><td>Username 4</td><td>Username 5</td>"
             + "<td>p1cw</td><td>p2cw</td><td>p3cw</td><td>p4cw</td><td>p5cw</td>"
             + "<td>p1_9hole</td><td>p2_9hole</td><td>p3_9hole</td><td>p4_9hole</td><td>p5_9hole</td>"
             + "<td>userg1</td><td>userg2</td><td>userg3</td><td>userg4</td><td>userg5</td>"
             + "<td>show1</td><td>show2</td><td>show3</td><td>show4</td><td>show5</td>");
     out.println("</tr>");
     
     try {
      
         pstmt = con.prepareStatement(""
                 + "(SELECT date, time, fb, player1, player2, player3, player4, player5, "
                 + "username1, username2, username3, username4, username5, p1cw, p2cw, p3cw, p4cw, p5cw, "
                 + "p91 AS p1_9hole, p92 AS p2_9hole, p93 AS p3_9hole, p94 AS p4_9hole, p95 AS p5_9hole, "
                 + "userg1, userg2, userg3, userg4, userg5, show1, show2, show3, show4, show5 "
                 + "FROM teepast2 "
                 + "WHERE yy = ? AND player1<>'') "
                 + "UNION ALL "
                 + "(SELECT date, time, fb, player1, player2, player3, player4, player5, "
                 + "username1, username2, username3, username4, username5, p1cw, p2cw, p3cw, p4cw, p5cw, "
                 + "p91 AS p1_9hole, p92 AS p2_9hole, p93 AS p3_9hole, p94 AS p4_9hole, p95 AS p5_9hole, "
                 + "userg1, userg2, userg3, userg4, userg5, show1, show2, show3, show4, show5 "
                 + "FROM teecurr2 "
                 + "WHERE date = ? AND player1<>'') "
                 + "ORDER BY date, time, fb;");
         pstmt.clearParameters();
         pstmt.setLong(1, yy);
         pstmt.setLong(2, date);
         
         rs = pstmt.executeQuery();
         
         while (rs.next()) {
         
             out.println("<tr>");
             out.println("<td>" + rs.getInt("date") + "</td>");
             out.println("<td>" + rs.getInt("time") + "</td>");
             out.println("<td>" + rs.getInt("fb") + "</td>");
             out.println("<td>" + rs.getString("player1") + "</td>");
             out.println("<td>" + rs.getString("player2") + "</td>");
             out.println("<td>" + rs.getString("player3") + "</td>");
             out.println("<td>" + rs.getString("player4") + "</td>");
             out.println("<td>" + rs.getString("player5") + "</td>");
             out.println("<td>" + rs.getString("username1") + "</td>");
             out.println("<td>" + rs.getString("username2") + "</td>");
             out.println("<td>" + rs.getString("username3") + "</td>");
             out.println("<td>" + rs.getString("username4") + "</td>");
             out.println("<td>" + rs.getString("username5") + "</td>");
             out.println("<td>" + rs.getString("p1cw") + "</td>");
             out.println("<td>" + rs.getString("p2cw") + "</td>");
             out.println("<td>" + rs.getString("p3cw") + "</td>");
             out.println("<td>" + rs.getString("p4cw") + "</td>");
             out.println("<td>" + rs.getString("p5cw") + "</td>");
             out.println("<td>" + rs.getInt("p1_9hole") + "</td>");
             out.println("<td>" + rs.getInt("p2_9hole") + "</td>");
             out.println("<td>" + rs.getInt("p3_9hole") + "</td>");
             out.println("<td>" + rs.getInt("p4_9hole") + "</td>");
             out.println("<td>" + rs.getInt("p5_9hole") + "</td>");
             out.println("<td>" + rs.getString("userg1") + "</td>");
             out.println("<td>" + rs.getString("userg2") + "</td>");
             out.println("<td>" + rs.getString("userg3") + "</td>");
             out.println("<td>" + rs.getString("userg4") + "</td>");
             out.println("<td>" + rs.getString("userg5") + "</td>");
             out.println("<td>" + rs.getInt("show1") + "</td>");
             out.println("<td>" + rs.getInt("show2") + "</td>");
             out.println("<td>" + rs.getInt("show3") + "</td>");
             out.println("<td>" + rs.getInt("show4") + "</td>");
             out.println("<td>" + rs.getInt("show5") + "</td>");
             out.println("</tr>");
         }
         
     } catch (Exception exc) {
         Utilities.logDebug("BK", "ERR: " + exc.toString());
     } finally {
         
         try { rs.close(); }
         catch (Exception ignore) { }
         
         try { pstmt.close(); }
         catch (Exception ignore) { }
     }
     
     
     
     out.println("</table>");
     
 }
 

} // end servlet public class
