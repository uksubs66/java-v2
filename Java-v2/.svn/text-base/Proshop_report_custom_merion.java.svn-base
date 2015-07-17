/***************************************************************************************
 *   Proshop_report_custom_merion: This servlet will ouput 2 custom reports for Merion to track
 *                                 member and guest play, mainly on the East course.
 *
 *
 *   Called by:     called by manually typing the url while logged in as a proshop user
 * 
 *                  use:  v5/servlet/Proshop_report_custom_merion    (add ?excel to go right to excel - much faster!!)
 * 
 * 
 *            ****** FOR CUSTOM REPORTS ONLY - NOT AVAILABLE TO CLUBS *********
 *            ****** RUN ON DEV SERVER!!! *********
 *
 *
 *   Created:     10/05/2013 by Bob
 *
 *       For:     Merion originally, but can be used for any club
 * 
 * 
 *   Report 1 (default):  mNum, Name, mship, rounds on East, gsts on East, rounds on West, gsts on West  (one line for each Primary member)
 * 
 *   Report 2 (?mem-east):  mNum, Name, mship, member rounds, A-rate gsts, B-rate gsts, C-rate gsts  (on East, one line for each Primary member)
 * 
 *   Report 3 (?times):  date, day, time, # of mems, # of NR, # of House, # of guests
 * 
 *   Report 4 (?history):  date, day, time, player1 - player4, username, name, date/time of booking/change, long date/time, type (0=new, 1=mod, 2=cancel), mship1 - mship4
 * 
 *
 *   NOTE:  use &excel=yes to output excel file
 * 
 * 
 *   Before Running:    Change the date fields below (sdate, edate, year)
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
import com.foretees.common.Connect;


public class Proshop_report_custom_merion extends HttpServlet {

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
    
    String excel = "";
            
    if (req.getParameter("excel") != null) excel = "yes";
    
    HttpSession session = SystemUtils.verifyPro(req, out);          // check for intruder
    if (session == null) { return; }
    String club = (String)session.getAttribute("club");
    
    // handle excel output
    try{
        if (excel.equals("yes")) {                // if user requested Excel Spreadsheet Format
            resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
            resp.setHeader("Content-Disposition", "attachment;filename=\""+club+".xls\"");
        }
    }
    catch (Exception exc) {
    }
    
    
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
    
    String templott = (String)session.getAttribute("lottery");      // get lottery support indicator
    int lottery = Integer.parseInt(templott);
       
    // start page output
    startPageOutput(excel, out);
    
    if (!excel.equals("yes")) {                // if not Excel Spreadsheet Format

       SystemUtils.getProshopSubMenu(req, out, lottery);               // required to allow submenus on this page
    }
    
    
    if (req.getParameter("mem-east") != null) {
        
        doMemEastReport(club, excel, out, con);
        return;
    }

    if (req.getParameter("times") != null) {
        
        doTimesReport(club, excel, out, con);
        return;
    }

    if (req.getParameter("interlachen") != null) {
        
        doInterlachenReport(club, excel, out, con);
        return;
    }

    if (req.getParameter("history") != null) {
        
        doHistoryReport(club, excel, out, con);
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

    
    Calendar cal = new GregorianCalendar();       // get todays date

    int year = cal.get(Calendar.YEAR);
    
    //int year = 2013;
    long sdate = (year * 10000) + 101;          // for date range      *****  Change these *****
    long edate = (year * 10000) + 1231;
    
     
    String username = "";
    String user1 = "";
    String user2 = "";
    String user3 = "";
    String user4 = "";
    String user5 = "";
    String userg1 = "";
    String userg2 = "";
    String userg3 = "";
    String userg4 = "";
    String userg5 = "";
    String name_last = "";
    String name_first = "";
    String name_mi = "";
    String m_ship = "";
    String m_type = "";
    String mem_num = "";
    String course = "";
    String lastmNum = "";
    String sql = "";
    String sql2 = "";
        
    long date = 0;
    int time = 0;
    int erounds = 0;
    int eguests = 0;
    int wrounds = 0;
    int wguests = 0;
    int rcount = 0;
    int gcount = 0;
    
    boolean firstMem = true;
        
        
    // output report title
    out.println("<font face=\"Arial, Helvetica, Sans-serif\"><b>Member Rounds Report For Jan 1 - Dec 31 " +year);
      
    out.println("</b></font></center>");
    
    if (!excel.equals("yes")) {                // if not Excel Spreadsheet Format

       out.println("<br><table border=\"0\" align=\"center\">");
       out.println("<tr><td>");
       out.println("<input type=\"button\" value=\"Print\" onclick=\"window.print();\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp;");
       out.println("</td>");
       out.println("<form method=\"post\" action=\"Proshop_report_custom_merion\" target=\"_blank\">");
       out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
       out.println("<td>");
       out.println("<input type=\"submit\" value=\"Excel\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp;");
       out.println("</td>");
       out.println("</form>");
       out.println("<td>");
       out.println("<input type=\"button\" value=\"Exit\" onclick=\"document.location.href='Proshop_announce'\" style=\"text-decoration:underline; background:#8B8970\">");
       out.println("</td></tr></table><br>");
    }
    
    
    //   Report Output:  mNum  Name  mship  Rounds on East  Guests on East  Rounds on West  Guests on West  
    //
    // start table output
    out.println("<table align=\"center\" cellpadding=\"3\" bgcolor=\"#F5F5DC\" style=\"border: 1px solid black\">");
    out.println("<tr bgcolor=\"#336633\" style=\"font-family:verdana;color:white;font-size:.9em\">" +
                "<td><b>Member Id&nbsp;&nbsp;&nbsp;</b></td>" +
                "<td><b>Member Name&nbsp;&nbsp;&nbsp;</b></td>" +
                "<td><b>Category&nbsp;&nbsp;</b></td>" +
                "<td><b>E Rounds&nbsp;&nbsp;</b></td>" +
                "<td><b>E Guests&nbsp;&nbsp;&nbsp;</b></td>" +
                "<td><b>W Rounds&nbsp;&nbsp;</b></td>" +
                "<td><b>W Guests&nbsp;</b></td>" +
                "</tr>");
    
    int alt_row = 0;
        
    try {
        sql = "SELECT username, name_last, name_first, name_mi, m_ship, m_type, memNum "
                + "FROM member2b "
                + "ORDER BY memNum, name_last, name_first, name_mi";

        sql2 = "SELECT * " +
                    "FROM teepast2 WHERE date > ? AND date < ? AND " +
                    "((username1 = ? AND show1 = 1) OR (username2 = ? AND show2 = 1) OR " + 
                    "(username3 = ? AND show3 = 1) OR (username4 = ? AND show4 = 1) OR " + 
                    "(username5 = ? AND show5 = 1) OR " +
                    "(userg1 = ? AND show1 = 1) OR (userg2 = ? AND show2 = 1) OR " + 
                    "(userg3 = ? AND show3 = 1) OR (userg4 = ? AND show4 = 1) OR " + 
                    "(userg5 = ? AND show5 = 1))" +
                    " ORDER BY date, time";
               
        stmt1 = con.createStatement();
        rs1 = stmt1.executeQuery(sql);      
        
        while (rs1.next()) {                      // get each member

            mem_num = rs1.getString("memNum");
            
            if (!mem_num.equals("")) {       // skip memebers w/o a mnum
           
               if (!lastmNum.equals("") && !mem_num.equals(lastmNum)) {    // if new family (group counts by family) - then print the last one

                  //   Report Output:  mNum  Name  mship  Rounds on East  Guests on East  Rounds on West  Guests on West  

                  alt_row = (alt_row == 0) ? 1 : 0;
                  out.println("<tr" + ((alt_row == 0) ? " bgcolor=\"#FDFDEF\"" : "") + " style=\"font-family:arial;color:black;font-size:.8em\">");
                  out.println("<td align=\"center\">" + lastmNum + "</td>");
                  out.println("<td align=\"left\" nowrap>" + name_last + ", " + name_first + " " + name_mi + "</td>");
                  out.println("<td align=\"center\">" + m_ship + "</td>");
                  out.println("<td align=\"center\">" + erounds + "</td>");
                  out.println("<td align=\"center\">" + eguests + "</td>");
                  out.println("<td align=\"center\">" + wrounds + "</td>");
                  out.println("<td align=\"center\">" + wguests + "</td>");
                  out.println("</tr>");

                  erounds = 0;    // init counts for each family
                  eguests = 0;
                  wrounds = 0;
                  wguests = 0;

                  lastmNum = mem_num;      //  new family
                  firstMem = true;
               }

               username = rs1.getString("username");
               m_ship = rs1.getString("m_ship");
               m_type = rs1.getString("m_type");

               if (lastmNum.equals("")) {    // if first record

                  lastmNum = mem_num;        // save this one so we check for more family members
               }


               if (m_type.startsWith("Primary") || firstMem == true) {    // get Primary's name or first name

                  name_last = rs1.getString("name_last");
                  name_first = rs1.getString("name_first");
                  name_mi = rs1.getString("name_mi");
                  
                  if (firstMem == true) firstMem = false;
               }


               pstmtc2 = con.prepareStatement(sql2);     // find tee times for this user
               pstmtc2.clearParameters();

               pstmtc2.setLong(1, sdate);
               pstmtc2.setLong(2, edate);
               pstmtc2.setString(3, username);
               pstmtc2.setString(4, username);
               pstmtc2.setString(5, username);
               pstmtc2.setString(6, username);
               pstmtc2.setString(7, username);
               pstmtc2.setString(8, username);
               pstmtc2.setString(9, username);
               pstmtc2.setString(10, username);
               pstmtc2.setString(11, username);
               pstmtc2.setString(12, username);

               rs2 = pstmtc2.executeQuery();      // get the tee times

               while (rs2.next()) {

                  date = rs2.getLong("date");
                  time = rs2.getInt("time");
                  user1 = rs2.getString("username1");
                  user2 = rs2.getString("username2");
                  user3 = rs2.getString("username3");
                  user4 = rs2.getString("username4");
                  user5 = rs2.getString("username5");
                  userg1 = rs2.getString("userg1");
                  userg2 = rs2.getString("userg2");
                  userg3 = rs2.getString("userg3");
                  userg4 = rs2.getString("userg4");
                  userg5 = rs2.getString("userg5");
                  course = rs2.getString("courseName");

                  rcount = 0;
                  gcount = 0;

                  if (user1.equals( username )) {   
                     rcount++;
                  }
                  if (user2.equals( username )) {
                     rcount++;
                  }
                  if (user3.equals( username )) {
                     rcount++;
                  }
                  if (user4.equals( username )) {
                     rcount++;
                  }
                  if (user5.equals( username )) {
                     rcount++;
                  }

                  if (userg1.equals( username )) {
                     gcount++;
                  }
                  if (userg2.equals( username )) {
                     gcount++;
                  }
                  if (userg3.equals( username )) {
                     gcount++;
                  }
                  if (userg4.equals( username )) {
                     gcount++;
                  }
                  if (userg5.equals( username )) {
                     gcount++;
                  }

                  if (course.equals("East")) {

                        erounds += rcount;  
                        eguests += gcount;

                  } else if (course.equals("West")) {   // check both because they have a Driving Range defined as a course

                        wrounds += rcount;
                        wguests += gcount;                   
                  }

               }    // end nested rs2 loop (tee times for each member)


               pstmtc2.close();
            }
               
        } // end main rs1 loop (members)
        
        stmt1.close();
    }
    catch (Exception e) {
        displayDatabaseErrMsg("Error loading member information for report.", e.getMessage(), out);
        return;
        
    }
    
   // finish the last family if we ended with a family

   //   Report Output:  mNum  Name  mship  Rounds on East  Guests on East  Rounds on West  Guests on West  

   alt_row = (alt_row == 0) ? 1 : 0;
   out.println("<tr" + ((alt_row == 0) ? " bgcolor=\"#FDFDEF\"" : "") + " style=\"font-family:arial;color:black;font-size:.8em\">");
   out.println("<td align=\"center\">" + mem_num + "</td>");
   out.println("<td align=\"left\" nowrap>" + name_last + ", " + name_first + " " + name_mi + "</td>");
   out.println("<td align=\"center\">" + m_ship + "</td>");
   out.println("<td align=\"center\">" + erounds + "</td>");
   out.println("<td align=\"center\">" + eguests + "</td>");
   out.println("<td align=\"center\">" + wrounds + "</td>");
   out.println("<td align=\"center\">" + wguests + "</td>");
   out.println("</tr>");
  
        
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
 //   run a member rounds report for the East Course only
 //
 private void doMemEastReport (String club, String excel, PrintWriter out, Connection con) {
     
    Statement stmt1 = null;
    PreparedStatement pstmtc1 = null;
    ResultSet rs1 = null;
    Statement stmt2 = null;
    PreparedStatement pstmtc2 = null;
    ResultSet rs2 = null;

    
    Calendar cal = new GregorianCalendar();       // get todays date

    int year = cal.get(Calendar.YEAR);
    
    //int year = 2013;
    long sdate = (year * 10000) + 101;          // for date range      *****  Change these *****
    long edate = (year * 10000) + 1231;
        
     
    String username = "";
    String user1 = "";
    String user2 = "";
    String user3 = "";
    String user4 = "";
    String user5 = "";
    String userg1 = "";
    String userg2 = "";
    String userg3 = "";
    String userg4 = "";
    String userg5 = "";
    String player1 = "";
    String player2 = "";
    String player3 = "";
    String player4 = "";
    String player5 = "";
    String name_last = "";
    String name_first = "";
    String name_mi = "";
    String m_ship = "";
    String m_type = "";
    String mem_num = "";
    String course = "";
    String lastmNum = "";
    String sql = "";
    String sql2 = "";
        
    long date = 0;
    int time = 0;
    int mrounds = 0;
    int aguests = 0;
    int bguests = 0;
    int cguests = 0;
    int oguests = 0;
    
    boolean firstMem = true;
        
        
    // output report title
    out.println("<font face=\"Arial, Helvetica, Sans-serif\"><b>Member Rounds Report For Jan 1 - Dec 31 " +year);
      
    out.println("</b></font></center>");
    
    if (!excel.equals("yes")) {                // if not Excel Spreadsheet Format

       out.println("<br><table border=\"0\" align=\"center\">");
       out.println("<tr><td>");
       out.println("<input type=\"button\" value=\"Print\" onclick=\"window.print();\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp;");
       out.println("</td>");
       out.println("<form method=\"post\" action=\"Proshop_report_custom_merion\" target=\"_blank\">");
       out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
       out.println("<td>");
       out.println("<input type=\"submit\" value=\"Excel\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp;");
       out.println("</td>");
       out.println("</form>");
       out.println("<td>");
       out.println("<input type=\"button\" value=\"Exit\" onclick=\"document.location.href='Proshop_announce'\" style=\"text-decoration:underline; background:#8B8970\">");
       out.println("</td></tr></table><br>");
    }
    
    
    //   Report 2:  mNum, Name, mship, member rounds, A-rate gsts, B-rate gsts, C-rate gsts  (on East, one line for each Primary member)
    //
    // start table output
    out.println("<table align=\"center\" cellpadding=\"3\" bgcolor=\"#F5F5DC\" style=\"border: 1px solid black\">");
    out.println("<tr bgcolor=\"#336633\" style=\"font-family:verdana;color:white;font-size:.9em\">" +
                "<td><b>Member Id&nbsp;&nbsp;&nbsp;</b></td>" +
                "<td><b>Member Name&nbsp;&nbsp;&nbsp;</b></td>" +
                "<td><b>Category&nbsp;&nbsp;</b></td>" +
                "<td><b>Rounds&nbsp;&nbsp;</b></td>" +
                "<td><b>A Guests&nbsp;&nbsp;&nbsp;</b></td>" +
                "<td><b>B Guests&nbsp;&nbsp;</b></td>" +
                "<td><b>C Guests&nbsp;</b></td>" +
                "<td><b>Other Guests&nbsp;</b></td>" +
                "</tr>");
    
    int alt_row = 0;
        
    try {
        sql = "SELECT username, name_last, name_first, name_mi, m_ship, m_type, memNum "
                + "FROM member2b "
                + "ORDER BY memNum, name_last, name_first, name_mi";

        sql2 = "SELECT * " +
                    "FROM teepast2 WHERE date > ? AND date < ? AND courseName = 'East' AND " +
                    "((username1 = ? AND show1 = 1) OR (username2 = ? AND show2 = 1) OR " + 
                    "(username3 = ? AND show3 = 1) OR (username4 = ? AND show4 = 1) OR " + 
                    "(username5 = ? AND show5 = 1) OR " +
                    "(userg1 = ? AND show1 = 1) OR (userg2 = ? AND show2 = 1) OR " + 
                    "(userg3 = ? AND show3 = 1) OR (userg4 = ? AND show4 = 1) OR " + 
                    "(userg5 = ? AND show5 = 1))" +
                    " ORDER BY date, time";
               
        stmt1 = con.createStatement();
        rs1 = stmt1.executeQuery(sql);      
        
        while (rs1.next()) {                      // get each member

            mem_num = rs1.getString("memNum");
            
            if (!mem_num.equals("")) {       // skip memebers w/o a mnum
           
               if (!lastmNum.equals("") && !mem_num.equals(lastmNum)) {    // if new family (group counts by family) - then print the last one

                  //  mNum, Name, mship, member rounds, A-rate gsts, B-rate gsts, C-rate gsts 

                  alt_row = (alt_row == 0) ? 1 : 0;
                  out.println("<tr" + ((alt_row == 0) ? " bgcolor=\"#FDFDEF\"" : "") + " style=\"font-family:arial;color:black;font-size:.8em\">");
                  out.println("<td align=\"center\">" + lastmNum + "</td>");
                  out.println("<td align=\"left\" nowrap>" + name_last + ", " + name_first + " " + name_mi + "</td>");
                  out.println("<td align=\"center\">" + m_ship + "</td>");
                  out.println("<td align=\"center\">" + mrounds + "</td>");
                  out.println("<td align=\"center\">" + aguests + "</td>");
                  out.println("<td align=\"center\">" + bguests + "</td>");
                  out.println("<td align=\"center\">" + cguests + "</td>");
                  out.println("<td align=\"center\">" + oguests + "</td>");
                  out.println("</tr>");

                  mrounds = 0;    // init counts for each family
                  aguests = 0;
                  bguests = 0;
                  cguests = 0;
                  oguests = 0;

                  lastmNum = mem_num;      //  new family
                  firstMem = true;
               }

               username = rs1.getString("username");
               m_ship = rs1.getString("m_ship");
               m_type = rs1.getString("m_type");

               if (lastmNum.equals("")) {    // if first record

                  lastmNum = mem_num;        // save this one so we check for more family members
               }


               if (m_type.startsWith("Primary") || firstMem == true) {    // get Primary's name or first name

                  name_last = rs1.getString("name_last");
                  name_first = rs1.getString("name_first");
                  name_mi = rs1.getString("name_mi");
                  
                  if (firstMem == true) firstMem = false;
               }


               pstmtc2 = con.prepareStatement(sql2);     // find tee times for this user
               pstmtc2.clearParameters();

               pstmtc2.setLong(1, sdate);
               pstmtc2.setLong(2, edate);
               pstmtc2.setString(3, username);
               pstmtc2.setString(4, username);
               pstmtc2.setString(5, username);
               pstmtc2.setString(6, username);
               pstmtc2.setString(7, username);
               pstmtc2.setString(8, username);
               pstmtc2.setString(9, username);
               pstmtc2.setString(10, username);
               pstmtc2.setString(11, username);
               pstmtc2.setString(12, username);

               rs2 = pstmtc2.executeQuery();      // get the tee times

               while (rs2.next()) {

                  date = rs2.getLong("date");
                  time = rs2.getInt("time");
                  player1 = rs2.getString("player1");
                  player2 = rs2.getString("player2");
                  player3 = rs2.getString("player3");
                  player4 = rs2.getString("player4");
                  player5 = rs2.getString("player5");
                  user1 = rs2.getString("username1");
                  user2 = rs2.getString("username2");
                  user3 = rs2.getString("username3");
                  user4 = rs2.getString("username4");
                  user5 = rs2.getString("username5");
                  userg1 = rs2.getString("userg1");
                  userg2 = rs2.getString("userg2");
                  userg3 = rs2.getString("userg3");
                  userg4 = rs2.getString("userg4");
                  userg5 = rs2.getString("userg5");
                  course = rs2.getString("courseName");

                  if (user1.equals( username )) {   
                     mrounds++;
                  }
                  if (user2.equals( username )) {
                     mrounds++;
                  }
                  if (user3.equals( username )) {
                     mrounds++;
                  }
                  if (user4.equals( username )) {
                     mrounds++;
                  }
                  if (user5.equals( username )) {
                     mrounds++;
                  }

                  if (userg1.equals( username )) {
                   
                     if (player1.startsWith("A rate Guest")) {
                        
                        aguests++;
                        
                     } else if (player1.startsWith("B rate Guest")) {
                        
                        bguests++;
                        
                     } else if (player1.startsWith("C rate Guest")) {
                        
                        cguests++;
                        
                     } else {
                        
                        oguests++;
                     }
                  }
                  if (userg2.equals( username )) {
                     
                     if (player2.startsWith("A rate Guest")) {
                        
                        aguests++;
                        
                     } else if (player2.startsWith("B rate Guest")) {
                        
                        bguests++;
                        
                     } else if (player2.startsWith("C rate Guest")) {
                        
                        cguests++;
                        
                     } else {
                        
                        oguests++;
                     }
                  }
                  if (userg3.equals( username )) {
                     
                     if (player3.startsWith("A rate Guest")) {
                        
                        aguests++;
                        
                     } else if (player3.startsWith("B rate Guest")) {
                        
                        bguests++;
                        
                     } else if (player3.startsWith("C rate Guest")) {
                        
                        cguests++;
                        
                     } else {
                        
                        oguests++;
                     }
                  }
                  if (userg4.equals( username )) {
                     
                     if (player4.startsWith("A rate Guest")) {
                        
                        aguests++;
                        
                     } else if (player4.startsWith("B rate Guest")) {
                        
                        bguests++;
                        
                     } else if (player4.startsWith("C rate Guest")) {
                        
                        cguests++;
                        
                     } else {
                        
                        oguests++;
                     }
                  }
                  if (userg5.equals( username )) {
                     
                     if (player5.startsWith("A rate Guest")) {
                        
                        aguests++;
                        
                     } else if (player5.startsWith("B rate Guest")) {
                        
                        bguests++;
                        
                     } else if (player5.startsWith("C rate Guest")) {
                        
                        cguests++;
                        
                     } else {
                        
                        oguests++;
                     }
                  }

               }    // end nested rs2 loop (tee times for each member)

               pstmtc2.close();
            }
               
        } // end main rs1 loop (members)
        
        stmt1.close();
    }
    catch (Exception e) {
        displayDatabaseErrMsg("Error loading member information for report.", e.getMessage(), out);
        return;
        
    }
    
   // finish the last family if we ended with a family

   //   Report Output:  mNum  Name  mship  Rounds on East  Guests on East  Rounds on West  Guests on West  

   alt_row = (alt_row == 0) ? 1 : 0;
   out.println("<tr" + ((alt_row == 0) ? " bgcolor=\"#FDFDEF\"" : "") + " style=\"font-family:arial;color:black;font-size:.8em\">");
   out.println("<td align=\"center\">" + mem_num + "</td>");
   out.println("<td align=\"left\" nowrap>" + name_last + ", " + name_first + " " + name_mi + "</td>");
   out.println("<td align=\"center\">" + m_ship + "</td>");
   out.println("<td align=\"center\">" + mrounds + "</td>");
   out.println("<td align=\"center\">" + aguests + "</td>");
   out.println("<td align=\"center\">" + bguests + "</td>");
   out.println("<td align=\"center\">" + cguests + "</td>");
   out.println("<td align=\"center\">" + oguests + "</td>");
   out.println("</tr>");
  
        
    out.println("</table><br><br>");
    
    endPageOutput(out);
 
 }     // end of doMemEastReport

       
 
 //
 //   run a tee time History report for the East Course only
 //
 private void doHistoryReport (String club, String excel, PrintWriter out, Connection con) {
     
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     

     long date = 0;
     long mdate = 0;
     
     Calendar cal = new GregorianCalendar();       // get todays date

     int year = cal.get(Calendar.YEAR);
    
     long sdate = (year * 10000) + 101;          // for date range      *****  Change these ? *****
     long edate = (year * 10000) + 1231;
    
       
     int time = 0;
     int type = 0;
    
     String user = "";
     String user1 = "";
     String user2 = "";
     String user3 = "";
     String user4 = "";
     String player1 = "";
     String player2 = "";
     String player3 = "";
     String player4 = "";
     String mship1 = "";
     String mship2 = "";
     String mship3 = "";
     String mship4 = "";
     String day = "";
     String uname = "";
     String datetime = "";
     
     
     // output report title
     out.println("<br><font face=\"Arial, Helvetica, Sans-serif\"><b>East Course Tee Time History Report For Jan - Dec " +year);
      
     out.println("</b></font><br><br>");
    
     //   Report:  date, day, time, player1 - player4, username, name, date of booking/change, long date/time, type (0=new, 1=mod, 2=cancel), mship1 - mship4
     
     out.println("<table align=\"center\" cellpadding=\"3\" bgcolor=\"#F5F5DC\" style=\"border: 1px solid black\">");
     out.println("<tr bgcolor=\"#336633\" style=\"font-family:verdana;color:white;font-size:.9em\">");
     out.println("<td>Date</td><td>Day</td><td>Time</td><td>Player 1</td><td>Player 2</td><td>Player 3</td><td>Player 4</td>"
               + "<td>User Id</td><td>User Name</td><td>Date of Change</td><td>Date & Time of Change</td><td>New/Mod/Can</td><td>Player1 Type</td><td>Player2 Type</td><td>Player3 Type</td><td>Player4 Type</td>");
     out.println("</tr>");
     
     try {
      
         String sql = "SELECT * "
                 + "FROM teehist "
                 + "WHERE date >= ? AND date <= ? AND courseName='East' "
                 + "ORDER BY date, time, mdate";
      
         pstmt = con.prepareStatement(sql);
         pstmt.clearParameters();
         pstmt.setLong(1, sdate);
         pstmt.setLong(2, edate);
         
         rs = pstmt.executeQuery();
         
         while (rs.next()) {
         
             mship1 = " ";           // init
             mship2 = " ";
             mship3 = " ";
             mship4 = " ";
            
             date = rs.getLong("date");                 // date of tee time
             day = rs.getString("day");                 // day of tee time
             time = rs.getInt("time");                  // time of tee time
             player1 = rs.getString("player1");
             player2 = rs.getString("player2");
             player3 = rs.getString("player3");
             player4 = rs.getString("player4");
             user = rs.getString("user");               // username of person making change
             uname = rs.getString("mname");             // name of person making the change
             mdate = rs.getLong("mdate");               // date of change
             datetime = rs.getString("sdate");          // date & time of change
             type = rs.getInt("type");                  // type of change (0=new, 1=mod)
             
             if (player1.equals("") && player2.equals("") && player3.equals("") && player4.equals("")) {
                
                type = 2;        // no players so it must be a Cancel
                
                player1 = " ";   // use space for display
                player2 = " ";
                player3 = " ";
                player4 = " ";
                
             } else {
                
                //
                //  players exist - determine if member or guest and get mship types of members
                //
                if (!player1.equals("")) {
                   
                   mship1 = getMship(player1, con);     // get player info
                }
                if (!player2.equals("")) {
                   
                   mship2 = getMship(player2, con);     
                }
                if (!player3.equals("")) {
                   
                   mship3 = getMship(player3, con);     
                }
                if (!player4.equals("")) {
                   
                   mship4 = getMship(player4, con);    
                }
             }
            
             //   Report:  date, day, time, player1 - player4, username, name, date of booking/change, long date/time, type (0=new, 1=mod, 2=cancel), mship1 - mship4
     
             out.println("<tr>");
             out.println("<td align=\"center\">" + date + "</td>");
             out.println("<td align=\"center\">" + day + "</td>");
             out.println("<td align=\"center\">" + time + "</td>");
             out.println("<td align=\"center\">" + player1 + "</td>");
             out.println("<td align=\"center\">" + player2 + "</td>");
             out.println("<td align=\"center\">" + player3 + "</td>");
             out.println("<td align=\"center\">" + player4 + "</td>");
             out.println("<td align=\"center\">" + user + "</td>");
             out.println("<td align=\"center\">" + uname + "</td>");
             out.println("<td align=\"center\">" + mdate + "</td>");
             out.println("<td align=\"center\">" + datetime + "</td>");
             out.println("<td align=\"center\">" + type + "</td>");
             out.println("<td align=\"center\">" + mship1 + "</td>");
             out.println("<td align=\"center\">" + mship2 + "</td>");
             out.println("<td align=\"center\">" + mship3 + "</td>");
             out.println("<td align=\"center\">" + mship4 + "</td>");
             out.println("</tr>");
         }
         
     } catch (Exception exc) {
         Utilities.logDebug("BP", "Proshop_report_custom_merion.doHistoryReport ERR: " + exc.toString());
     } finally {
         
         try { rs.close(); }
         catch (Exception ignore) { }
         
         try { pstmt.close(); }
         catch (Exception ignore) { }
     }
     
     out.println("</table></center>");
     
 }   // end of doHistoryReport
 

 
 //
 //   run a tee time report
 //
 private void doTimesReport (String club, String excel, PrintWriter out, Connection con) {
     
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     

     long date = 0;
     
     Calendar cal = new GregorianCalendar();       // get todays date

     int year = cal.get(Calendar.YEAR);
     
     //year--;     // if after Dec 31
    
     long sdate = (year * 10000) + 101;          // for date range      *****  Change these ? *****
     long edate = (year * 10000) + 1231;
    
     
     int mems = 0;
     int NRmems = 0;
     int Housemems = 0;
     int guests = 0;
     int show1 = 0;
     int show2 = 0;
     int show3 = 0;
     int show4 = 0;
     int show5 = 0;
    
     String user1 = "";
     String user2 = "";
     String user3 = "";
     String user4 = "";
     String user5 = "";
     String player1 = "";
     String player2 = "";
     String player3 = "";
     String player4 = "";
     String player5 = "";
     String mship1 = "";
     String mship2 = "";
     String mship3 = "";
     String mship4 = "";
     String mship5 = "";
     String queryparm = "";
     
     
     // output report title
     if (club.equals("interlachen")) {

         out.println("<br><font face=\"Arial, Helvetica, Sans-serif\"><b>Interlachen Tee Times Report For Jan - Dec " +year);
         
     } else {
                  
         out.println("<br><font face=\"Arial, Helvetica, Sans-serif\"><b>East Course Tee Times Report For Jan - Dec " +year);
         
         queryparm = "AND courseName='East'";
     }
      
     out.println("</b></font><br><br>");
    
     //   Report:  Date, Day, Time, # of members, # of NR mems, # of House mems, # of Guests
     
     out.println("<table align=\"center\" cellpadding=\"3\" bgcolor=\"#F5F5DC\" style=\"border: 1px solid black\">");
     out.println("<tr bgcolor=\"#336633\" style=\"font-family:verdana;color:white;font-size:.9em\">");
     out.println("<td>Date</td><td>Day</td><td>Time</td>");
     
     
     if (club.equals("interlachen")) {

         out.println("<td># of Members</td><td># of Guest-L</td><td># of Guest-H</td><td># of Other Guests</td>");
         
     } else {
                  
         out.println("<td># of Members</td><td># of NR</td><td># of House</td><td># of Guests</td>");
     }
         
     out.println("</tr>");
     
     
     try {
      
         pstmt = con.prepareStatement(""
                 + "SELECT * "
                 + "FROM teepast2 "
                 + "WHERE date >= ? AND date <= ? AND player1<>'' "+queryparm+ " "
                 + "ORDER BY date, time");
         pstmt.clearParameters();
         pstmt.setLong(1, sdate);
         pstmt.setLong(2, edate);
         
         rs = pstmt.executeQuery();
         
         while (rs.next()) {
         
             mems = 0;
             NRmems = 0;
             Housemems = 0;
             guests = 0;
             
             player1 = rs.getString("player1");
             player2 = rs.getString("player2");
             player3 = rs.getString("player3");
             player4 = rs.getString("player4");
             player5 = rs.getString("player5");
             user1 = rs.getString("username1");
             user2 = rs.getString("username2");
             user3 = rs.getString("username3");
             user4 = rs.getString("username4");
             user5 = rs.getString("username5");
             show1 = rs.getInt("show1");
             show2 = rs.getInt("show2");
             show3 = rs.getInt("show3");
             show4 = rs.getInt("show4");
             show5 = rs.getInt("show5");
             mship1 = rs.getString("mship1");
             mship2 = rs.getString("mship2");
             mship3 = rs.getString("mship3");
             mship4 = rs.getString("mship4");
             mship5 = rs.getString("mship5");
             
             if (!player1.equals("") && show1 > 0) {
                
                if (!mship1.equals("")) {
                   
                   mems++;
                   
                   if (mship1.equals("Non-Resident")) {

                      NRmems++;

                   } else if (mship1.equals("House")) {
                      
                      Housemems++;
                   }
                   
                } else {
                                      
                   if (club.equals("interlachen")) {

                       if (player1.startsWith("Guest-L")) {
                           
                            NRmems++;

                       } else if (player1.startsWith("Guest-H")) {

                            Housemems++;
                           
                       } else {
                            
                           guests++;     // other guests
                       }
                       
                   } else {
                       
                       guests++;
                   }
                }                
             }
             if (!player2.equals("") && show2 > 0) {
                
                if (!mship2.equals("")) {
                   
                   mems++;
                   
                   if (mship2.equals("Non-Resident")) {

                      NRmems++;

                   } else if (mship2.equals("House")) {
                      
                      Housemems++;
                   }
                   
                } else {
                   
                   if (club.equals("interlachen")) {

                       if (player2.startsWith("Guest-L")) {
                           
                            NRmems++;

                       } else if (player2.startsWith("Guest-H")) {

                            Housemems++;
                           
                       } else {
                            
                           guests++;     // other guests
                       }
                       
                   } else {
                       
                       guests++;
                   }
                }
             }
             if (!player3.equals("") && show3 > 0) {
                
                if (!mship3.equals("")) {
                   
                   mems++;
                   
                   if (mship3.equals("Non-Resident")) {

                      NRmems++;

                   } else if (mship3.equals("House")) {
                      
                      Housemems++;
                   }
                   
                } else {
                   
                   if (club.equals("interlachen")) {

                       if (player3.startsWith("Guest-L")) {
                           
                            NRmems++;

                       } else if (player3.startsWith("Guest-H")) {

                            Housemems++;
                           
                       } else {
                            
                           guests++;     // other guests
                       }
                       
                   } else {
                       
                       guests++;
                   }
                }
             }
             if (!player4.equals("") && show4 > 0) {
                
                if (!mship4.equals("")) {
                   
                   mems++;
                   
                   if (mship4.equals("Non-Resident")) {

                      NRmems++;

                   } else if (mship4.equals("House")) {
                      
                      Housemems++;
                   }
                   
                } else {
                   
                   if (club.equals("interlachen")) {

                       if (player4.startsWith("Guest-L")) {
                           
                            NRmems++;

                       } else if (player4.startsWith("Guest-H")) {

                            Housemems++;
                           
                       } else {
                            
                           guests++;     // other guests
                       }
                       
                   } else {
                       
                       guests++;
                   }
                }
             }
             if (!player5.equals("") && show5 > 0) {
                
                if (!mship5.equals("")) {
                   
                   mems++;
                   
                   if (mship5.equals("Non-Resident")) {

                      NRmems++;

                   } else if (mship5.equals("House")) {
                      
                      Housemems++;
                   }
                   
                } else {
                   
                   if (club.equals("interlachen")) {

                       if (player5.startsWith("Guest-L")) {
                           
                            NRmems++;

                       } else if (player5.startsWith("Guest-H")) {

                            Housemems++;
                           
                       } else {
                            
                           guests++;     // other guests
                       }
                       
                   } else {
                       
                       guests++;
                   }
                }
             }
             
             if (mems > 0 || guests > 0) {
             
                  out.println("<tr>");
                  out.println("<td align=\"center\">" + rs.getLong("date") + "</td>");
                  out.println("<td align=\"center\">" + rs.getString("day") + "</td>");
                  out.println("<td align=\"center\">" + rs.getInt("time") + "</td>");
                  out.println("<td align=\"center\">" + mems + "</td>");
                  out.println("<td align=\"center\">" + NRmems + "</td>");
                  out.println("<td align=\"center\">" + Housemems + "</td>");
                  out.println("<td align=\"center\">" + guests + "</td>");
                  out.println("</tr>");
             }
         }
         
     } catch (Exception exc) {
         Utilities.logDebug("BP", "Proshop_report_custom_merion ERR: " + exc.toString());
     } finally {
         
         try { rs.close(); }
         catch (Exception ignore) { }
         
         try { pstmt.close(); }
         catch (Exception ignore) { }
     }
     
     out.println("</table></center>");
     
 }   // end of doTimesReport
 
 

 
 //
 //   run a tee time report
 //
 private void doInterlachenReport (String club, String excel, PrintWriter out, Connection con) {
     
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     

     Calendar cal = new GregorianCalendar();       // get todays date

     int year = cal.get(Calendar.YEAR);
     
   //  year--;     // if after Dec 31
   //  year--;     // let's go back 2 years
    
     long sdate = (year * 10000) + 101;          // for date range      *****  Change these ? *****
     long edate = (year * 10000) + 1231;
    
     
     int mems = 0;
     int guests = 0;
     int show1 = 0;
     int show2 = 0;
     int show3 = 0;
     int show4 = 0;
     int show5 = 0;
     int time = 0;
     int wkday150mems = 0;
     int wkday200mems = 0;
     int sat150mems = 0;
     int sat100mems = 0;
     int sun200mems = 0;
     int sun150mems = 0;
     int sun100mems = 0;
     int monmems = 0;
     int monguests = 0;
     int i = 0;
     
     long date = 0;
    
     String user1 = "";
     String user2 = "";
     String user3 = "";
     String user4 = "";
     String user5 = "";
     String player1 = "";
     String player2 = "";
     String player3 = "";
     String player4 = "";
     String player5 = "";
     String mship1 = "";
     String mship2 = "";
     String mship3 = "";
     String mship4 = "";
     String mship5 = "";
     String day = "";
     
     String [] gst_table = { "Guest-H", "Guest-L", "Guest-9 holes", "Guest-Booklet", "Guest-Jr" };      // guest types to track

     int [] wkday150 = new int [6];      // counts for guest types (1 extra for 'others')
     int [] wkday200 = new int [6];     
     int [] sat150 = new int [6];     
     int [] sat100 = new int [6];     
     int [] sun200 = new int [6];     
     int [] sun150 = new int [6];     
     int [] sun100 = new int [6];     
     
     boolean found = false;
     
     
     
     // output report title

     out.println("<br><font face=\"Arial, Helvetica, Sans-serif\"><b>Interlachen Tee Times Report For Jan - Dec " +year);
         
     out.println("</b></font><br><br>");
    
     out.println("<table align=\"center\" cellpadding=\"3\" bgcolor=\"#F5F5DC\" style=\"border: 1px solid black\">");
     out.println("<tr bgcolor=\"#336633\" style=\"font-family:verdana;color:white;font-size:.9em\">");
     out.println("<td>Day & Time</td><td>Total Members</td>");

     for (i=0; i<5; i++) {
         
         out.println("<td>" +gst_table[i]+ "</td>");
     }
     
     out.println("<td>Other Guests</td></tr>");
     
     
     try {
      
         pstmt = con.prepareStatement(""
                 + "SELECT * "
                 + "FROM teepast2 "
                 + "WHERE date >= ? AND date <= ? AND event = '' "
                 + "ORDER BY date, time");
         pstmt.clearParameters();
         pstmt.setLong(1, sdate);
         pstmt.setLong(2, edate);
         
         rs = pstmt.executeQuery();
         
         while (rs.next()) {
         
             day = rs.getString("day");
             date = rs.getLong("date");
             time = rs.getInt("time");
             player1 = rs.getString("player1");
             player2 = rs.getString("player2");
             player3 = rs.getString("player3");
             player4 = rs.getString("player4");
             player5 = rs.getString("player5");
             user1 = rs.getString("username1");
             user2 = rs.getString("username2");
             user3 = rs.getString("username3");
             user4 = rs.getString("username4");
             user5 = rs.getString("username5");
             show1 = rs.getInt("show1");
             show2 = rs.getInt("show2");
             show3 = rs.getInt("show3");
             show4 = rs.getInt("show4");
             show5 = rs.getInt("show5");

             
             //
             //   Saturday 
             //
             if (day.equals("Saturday")) {        // Saturday

                if (!player1.equals("") && show1 > 0) {

                    if (time > 1359) {            //  PM

                        if (!user1.equals("")) {

                            sat100mems++;
                            
                        } else {
                            
                            found = false;
                            
                            for (i=0; i<5; i++) {
                                
                                if (player1.startsWith(gst_table[i])) {
                                    
                                    sat100[i]++;
                                    found = true;
                                    break;
                                }
                            }
                            
                            if (!found) sat100[5]++;    // other                        
                        }

                    } else if (time > 1059) {    // mid day

                        if (!user1.equals("")) {

                            sat150mems++;

                        } else {

                            found = false;

                            for (i=0; i<5; i++) {

                                if (player1.startsWith(gst_table[i])) {

                                    sat150[i]++;
                                    found = true;
                                    break;
                                }
                            }

                            if (!found) sat150[5]++;    // other                        
                        }
                    } 
                }
                    
                if (!player2.equals("") && show2 > 0) {

                    if (time > 1359) {      //  PM

                        if (!user2.equals("")) {

                            sat100mems++;
                            
                        } else {
                            
                            found = false;
                            
                            for (i=0; i<5; i++) {
                                
                                if (player2.startsWith(gst_table[i])) {
                                    
                                    sat100[i]++;
                                    found = true;
                                    break;
                                }
                            }
                            
                            if (!found) sat100[5]++;    // other                        
                        }

                    } else if (time > 1059) {    // mid day

                        if (!user2.equals("")) {

                            sat150mems++;

                        } else {

                            found = false;

                            for (i=0; i<5; i++) {

                                if (player2.startsWith(gst_table[i])) {

                                    sat150[i]++;
                                    found = true;
                                    break;
                                }
                            }

                            if (!found) sat150[5]++;    // other                        
                        }
                    } 
                }
                    
                if (!player3.equals("") && show3 > 0) {

                    if (time > 1359) {      // AM or PM

                        if (!user3.equals("")) {

                            sat100mems++;
                            
                        } else {
                            
                            found = false;
                            
                            for (i=0; i<5; i++) {
                                
                                if (player3.startsWith(gst_table[i])) {
                                    
                                    sat100[i]++;
                                    found = true;
                                    break;
                                }
                            }
                            
                            if (!found) sat100[5]++;    // other                        
                        }

                    } else if (time > 1059) {    // mid day

                        if (!user3.equals("")) {

                            sat150mems++;

                        } else {

                            found = false;

                            for (i=0; i<5; i++) {

                                if (player3.startsWith(gst_table[i])) {

                                    sat150[i]++;
                                    found = true;
                                    break;
                                }
                            }

                            if (!found) sat150[5]++;    // other                        
                        }
                    } 
                }
                    
                if (!player4.equals("") && show4 > 0) {

                    if (time > 1359) {      // AM or PM

                        if (!user4.equals("")) {

                            sat100mems++;
                            
                        } else {
                            
                            found = false;
                            
                            for (i=0; i<5; i++) {
                                
                                if (player4.startsWith(gst_table[i])) {
                                    
                                    sat100[i]++;
                                    found = true;
                                    break;
                                }
                            }
                            
                            if (!found) sat100[5]++;    // other                        
                        }

                    } else if (time > 1059) {    // mid day

                        if (!user4.equals("")) {

                            sat150mems++;

                        } else {

                            found = false;

                            for (i=0; i<5; i++) {

                                if (player4.startsWith(gst_table[i])) {

                                    sat150[i]++;
                                    found = true;
                                    break;
                                }
                            }

                            if (!found) sat150[5]++;    // other                        
                        }
                    } 
                }
                    
                if (!player5.equals("") && show5 > 0) {

                    if (time > 1359) {      // AM or PM

                        if (!user5.equals("")) {

                            sat100mems++;
                            
                        } else {
                            
                            found = false;
                            
                            for (i=0; i<5; i++) {
                                
                                if (player5.startsWith(gst_table[i])) {
                                    
                                    sat100[i]++;
                                    found = true;
                                    break;
                                }
                            }
                            
                            if (!found) sat100[5]++;    // other                        
                        }

                    } else if (time > 1059) {    // mid day

                        if (!user5.equals("")) {

                            sat150mems++;

                        } else {

                            found = false;

                            for (i=0; i<5; i++) {

                                if (player5.startsWith(gst_table[i])) {

                                    sat150[i]++;
                                    found = true;
                                    break;
                                }
                            }

                            if (!found) sat150[5]++;    // other                        
                        }
                    } 
                }
                                    

            } else if (day.equals("Sunday") || date == 20140704 || date == 20140526 || date == 20140901 ) {   // Sunday or holiday
                
                //
                //  Sunday and Holidays 
                //
                if (!player1.equals("") && show1 > 0) {

                    if (time < 1100) {      // AM

                        if (!user1.equals("")) {

                            sun200mems++;
                            
                        } else {
                            
                            found = false;
                            
                            for (i=0; i<5; i++) {
                                
                                if (player1.startsWith(gst_table[i])) {
                                    
                                    sun200[i]++;
                                    found = true;
                                    break;
                                }
                            }
                            
                            if (!found) sun200[5]++;    // other                        
                        }

                    } else if (time > 1359) {

                        if (!user1.equals("")) {

                            sun100mems++;
                            
                        } else {
                            
                            found = false;
                            
                            for (i=0; i<5; i++) {
                                
                                if (player1.startsWith(gst_table[i])) {
                                    
                                    sun100[i]++;
                                    found = true;
                                    break;
                                }
                            }
                            
                            if (!found) sun100[5]++;    // other                        
                        }                       

                    } else {    // mid day

                        if (!user1.equals("")) {

                            sun150mems++;

                        } else {

                            found = false;

                            for (i=0; i<5; i++) {

                                if (player1.startsWith(gst_table[i])) {

                                    sun150[i]++;
                                    found = true;
                                    break;
                                }
                            }

                            if (!found) sun150[5]++;    // other                        
                        }
                    } 
                }
                    
                if (!player2.equals("") && show2 > 0) {

                    if (time < 1100) {      // AM

                        if (!user2.equals("")) {

                            sun200mems++;
                            
                        } else {
                            
                            found = false;
                            
                            for (i=0; i<5; i++) {
                                
                                if (player2.startsWith(gst_table[i])) {
                                    
                                    sun200[i]++;
                                    found = true;
                                    break;
                                }
                            }
                            
                            if (!found) sun200[5]++;    // other                        
                        }

                    } else if (time > 1359) {

                        if (!user2.equals("")) {

                            sun100mems++;
                            
                        } else {
                            
                            found = false;
                            
                            for (i=0; i<5; i++) {
                                
                                if (player2.startsWith(gst_table[i])) {
                                    
                                    sun100[i]++;
                                    found = true;
                                    break;
                                }
                            }
                            
                            if (!found) sun100[5]++;    // other                        
                        }                       

                    } else {    // mid day

                        if (!user2.equals("")) {

                            sun150mems++;

                        } else {

                            found = false;

                            for (i=0; i<5; i++) {

                                if (player2.startsWith(gst_table[i])) {

                                    sun150[i]++;
                                    found = true;
                                    break;
                                }
                            }

                            if (!found) sun150[5]++;    // other                        
                        }
                    } 
                }
                    
                if (!player3.equals("") && show3 > 0) {

                    if (time < 1100) {      // AM

                        if (!user3.equals("")) {

                            sun200mems++;
                            
                        } else {
                            
                            found = false;
                            
                            for (i=0; i<5; i++) {
                                
                                if (player3.startsWith(gst_table[i])) {
                                    
                                    sun200[i]++;
                                    found = true;
                                    break;
                                }
                            }
                            
                            if (!found) sun200[5]++;    // other                        
                        }

                    } else if (time > 1359) {

                        if (!user3.equals("")) {

                            sun100mems++;
                            
                        } else {
                            
                            found = false;
                            
                            for (i=0; i<5; i++) {
                                
                                if (player3.startsWith(gst_table[i])) {
                                    
                                    sun100[i]++;
                                    found = true;
                                    break;
                                }
                            }
                            
                            if (!found) sun100[5]++;    // other                        
                        }                       

                    } else {    // mid day

                        if (!user3.equals("")) {

                            sun150mems++;

                        } else {

                            found = false;

                            for (i=0; i<5; i++) {

                                if (player3.startsWith(gst_table[i])) {

                                    sun150[i]++;
                                    found = true;
                                    break;
                                }
                            }

                            if (!found) sun150[5]++;    // other                        
                        }
                    } 
                }
                    
                if (!player4.equals("") && show4 > 0) {

                    if (time < 1100) {      // AM

                        if (!user4.equals("")) {

                            sun200mems++;
                            
                        } else {
                            
                            found = false;
                            
                            for (i=0; i<5; i++) {
                                
                                if (player4.startsWith(gst_table[i])) {
                                    
                                    sun200[i]++;
                                    found = true;
                                    break;
                                }
                            }
                            
                            if (!found) sun200[5]++;    // other                        
                        }

                    } else if (time > 1359) {

                        if (!user4.equals("")) {

                            sun100mems++;
                            
                        } else {
                            
                            found = false;
                            
                            for (i=0; i<5; i++) {
                                
                                if (player4.startsWith(gst_table[i])) {
                                    
                                    sun100[i]++;
                                    found = true;
                                    break;
                                }
                            }
                            
                            if (!found) sun100[5]++;    // other                        
                        }                       

                    } else {    // mid day

                        if (!user4.equals("")) {

                            sun150mems++;

                        } else {

                            found = false;

                            for (i=0; i<5; i++) {

                                if (player4.startsWith(gst_table[i])) {

                                    sun150[i]++;
                                    found = true;
                                    break;
                                }
                            }

                            if (!found) sun150[5]++;    // other                        
                        }
                    } 
                }
                    
                if (!player5.equals("") && show5 > 0) {

                    if (time < 1100) {      // AM

                        if (!user5.equals("")) {

                            sun200mems++;
                            
                        } else {
                            
                            found = false;
                            
                            for (i=0; i<5; i++) {
                                
                                if (player5.startsWith(gst_table[i])) {
                                    
                                    sun200[i]++;
                                    found = true;
                                    break;
                                }
                            }
                            
                            if (!found) sun200[5]++;    // other                        
                        }

                    } else if (time > 1359) {

                        if (!user5.equals("")) {

                            sun100mems++;
                            
                        } else {
                            
                            found = false;
                            
                            for (i=0; i<5; i++) {
                                
                                if (player5.startsWith(gst_table[i])) {
                                    
                                    sun100[i]++;
                                    found = true;
                                    break;
                                }
                            }
                            
                            if (!found) sun100[5]++;    // other                        
                        }                       

                    } else {    // mid day

                        if (!user5.equals("")) {

                            sun150mems++;

                        } else {

                            found = false;

                            for (i=0; i<5; i++) {

                                if (player5.startsWith(gst_table[i])) {

                                    sun150[i]++;
                                    found = true;
                                    break;
                                }
                            }

                            if (!found) sun150[5]++;    // other                        
                        }
                    } 
                }
                                    
                

            } else if (!day.equals("Monday")) {   // Tues - Fri

                //
                //  Tuesday - Friday 
                //
                if (!player1.equals("") && show1 > 0) {

                    if (time < 1300 || time > 1359) {      // AM or PM

                        if (!user1.equals("")) {

                            wkday150mems++;
                            
                        } else {
                            
                            found = false;
                            
                            for (i=0; i<5; i++) {
                                
                                if (player1.startsWith(gst_table[i])) {
                                    
                                    wkday150[i]++;
                                    found = true;
                                    break;
                                }
                            }
                            
                            if (!found) wkday150[5]++;    // other                        
                        }

                    } else {    // mid day

                        if (!user1.equals("")) {

                            wkday200mems++;

                        } else {

                            found = false;

                            for (i=0; i<5; i++) {

                                if (player1.startsWith(gst_table[i])) {

                                    wkday200[i]++;
                                    found = true;
                                    break;
                                }
                            }

                            if (!found) wkday200[5]++;    // other                        
                        }
                    } 
                }
                    
                if (!player2.equals("") && show2 > 0) {

                    if (time < 1300 || time > 1359) {      // AM or PM

                        if (!user2.equals("")) {

                            wkday150mems++;
                            
                        } else {
                            
                            found = false;
                            
                            for (i=0; i<5; i++) {
                                
                                if (player2.startsWith(gst_table[i])) {
                                    
                                    wkday150[i]++;
                                    found = true;
                                    break;
                                }
                            }
                            
                            if (!found) wkday150[5]++;    // other                        
                        }

                    } else {    // mid day

                        if (!user2.equals("")) {

                            wkday200mems++;

                        } else {

                            found = false;

                            for (i=0; i<5; i++) {

                                if (player2.startsWith(gst_table[i])) {

                                    wkday200[i]++;
                                    found = true;
                                    break;
                                }
                            }

                            if (!found) wkday200[5]++;    // other                        
                        }
                    } 
                }
                
                if (!player3.equals("") && show3 > 0) {

                    if (time < 1300 || time > 1359) {      // AM or PM

                        if (!user3.equals("")) {

                            wkday150mems++;
                            
                        } else {
                            
                            found = false;
                            
                            for (i=0; i<5; i++) {
                                
                                if (player3.startsWith(gst_table[i])) {
                                    
                                    wkday150[i]++;
                                    found = true;
                                    break;
                                }
                            }
                            
                            if (!found) wkday150[5]++;    // other                        
                        }

                    } else {    // mid day

                        if (!user3.equals("")) {

                            wkday200mems++;

                        } else {

                            found = false;

                            for (i=0; i<5; i++) {

                                if (player3.startsWith(gst_table[i])) {

                                    wkday200[i]++;
                                    found = true;
                                    break;
                                }
                            }

                            if (!found) wkday200[5]++;    // other                        
                        }
                    } 
                }
                
                if (!player4.equals("") && show4 > 0) {

                    if (time < 1300 || time > 1359) {      // AM or PM

                        if (!user4.equals("")) {

                            wkday150mems++;
                            
                        } else {
                            
                            found = false;
                            
                            for (i=0; i<5; i++) {
                                
                                if (player4.startsWith(gst_table[i])) {
                                    
                                    wkday150[i]++;
                                    found = true;
                                    break;
                                }
                            }
                            
                            if (!found) wkday150[5]++;    // other                        
                        }

                    } else {    // mid day

                        if (!user4.equals("")) {

                            wkday200mems++;

                        } else {

                            found = false;

                            for (i=0; i<5; i++) {

                                if (player4.startsWith(gst_table[i])) {

                                    wkday200[i]++;
                                    found = true;
                                    break;
                                }
                            }

                            if (!found) wkday200[5]++;    // other                        
                        }
                    } 
                }
                
                if (!player5.equals("") && show5 > 0) {

                    if (time < 1300 || time > 1359) {      // AM or PM

                        if (!user5.equals("")) {

                            wkday150mems++;
                            
                        } else {
                            
                            found = false;
                            
                            for (i=0; i<5; i++) {
                                
                                if (player5.startsWith(gst_table[i])) {
                                    
                                    wkday150[i]++;
                                    found = true;
                                    break;
                                }
                            }
                            
                            if (!found) wkday150[5]++;    // other                        
                        }

                    } else {    // mid day

                        if (!user5.equals("")) {

                            wkday200mems++;

                        } else {

                            found = false;

                            for (i=0; i<5; i++) {

                                if (player5.startsWith(gst_table[i])) {

                                    wkday200[i]++;
                                    found = true;
                                    break;
                                }
                            }

                            if (!found) wkday200[5]++;    // other                        
                        }
                    } 
                }
                             

            }   // end of Days of Week checks
                         
         }      // end of WHILE tee times
         
     } catch (Exception exc) {
         Utilities.logDebug("BP", "Proshop_report_custom_merion ERR: " + exc.toString());
     } finally {
         
         try { rs.close(); }
         catch (Exception ignore) { }
         
         try { pstmt.close(); }
         catch (Exception ignore) { }
     }
     
     out.println("<tr><td align=\"left\">Tues - Fri Before 1:00 and After 2:00</td><td align=\"center\"><b>" +wkday150mems+ "</b></td>");         
     for (i=0; i<6; i++) {
         
         out.println("<td align=\"center\"><b>" +wkday150[i]+ "</b></td>");
     }   
     out.println("</tr>");      
     
     out.println("<tr><td align=\"left\">Tues - Fri Between 1:00 and 1:50</td><td align=\"center\"><b>" +wkday200mems+ "</b></td>");
     for (i=0; i<6; i++) {
         
         out.println("<td align=\"center\"><b>" +wkday200[i]+ "</b></td>");
     }   
     out.println("</tr>");
     
     out.println("<tr><td align=\"left\">Saturday After 2:00</td><td align=\"center\"><b>" +sat100mems+ "</b></td>");
     for (i=0; i<6; i++) {
         
         out.println("<td align=\"center\"><b>" +sat100[i]+ "</b></td>");
     }   
     out.println("</tr>");
     
     out.println("<tr><td align=\"left\">Saturday Between 11:00 and After 1:50</td><td align=\"center\"><b>" +sat150mems+ "</b></td>");
     for (i=0; i<6; i++) {
         
         out.println("<td align=\"center\"><b>" +sat150[i]+ "</b></td>");
     }   
     out.println("</tr>");
     
     out.println("<tr><td align=\"left\">Sunday & Holidays Before 11:00</td><td align=\"center\"><b>" +sun200mems+ "</b></td>");
     for (i=0; i<6; i++) {
         
         out.println("<td align=\"center\"><b>" +sun200[i]+ "</b></td>");
     }   
     out.println("</tr>");
     
     out.println("<tr><td align=\"left\">Sunday & Holidays Between 11:00 and 1:50</td><td align=\"center\"><b>" +sun150mems+ "</b></td>");
     for (i=0; i<6; i++) {
         
         out.println("<td align=\"center\"><b>" +sun150[i]+ "</b></td>");
     }   
     out.println("</tr>");
          
     out.println("<tr><td align=\"left\">Sunday & Holidays After 2:00</td><td align=\"center\"><b>" +sun100mems+ "</b></td>");
     for (i=0; i<6; i++) {
         
         out.println("<td align=\"center\"><b>" +sun100[i]+ "</b></td>");
     }   
     out.println("</tr>");
     
     out.println("</table></center>");
     
 }   // end of doInterlachenReport
 
 

 
 //
 //   run a tee time report
 //
 private String getMship (String player, Connection con) {
     
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     
     String mship = " ";
     String fname = "";
     String lname = "";
     String mi = "";
     
     try {

         //
         //  parm block to hold the club parameters (guest types, etc)
         //
         parmClub parm = new parmClub(0, con);

         getClub.getParms(con, parm, 0, true);        // get the club parms - get ALL guest types

     
         if (!player.equals("")) {       // make sure we have a name

               // check if player is a guest

               int i = 0;
               loop1:
               while (i < parm.MAX_Guests) {

                  if (player.startsWith(parm.guest[i])) {

                     mship = "Guest";   // indicate its a guest

                     break loop1;       // exit loop
                  }
                  i++;
               }         // end of while loop

               if (!mship.equals("Guest")) {      // if not a guest

                  // parse the member's name

                  StringTokenizer tok = new StringTokenizer(player);     // space is the default token

                  if (tok.countTokens() > 3 || tok.countTokens() == 1) {     // too many or not enough name fields

                     return ("Unknown");
                  }

                  if (tok.countTokens() == 2) {         // first name, last name

                     fname = tok.nextToken();
                     lname = tok.nextToken();
                  }

                  if (tok.countTokens() == 3) {         // first name, mi, last name

                     fname = tok.nextToken();
                     mi = tok.nextToken();
                     lname = tok.nextToken();
                  }

                  pstmt = con.prepareStatement(
                           "SELECT m_ship FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ?");

                  pstmt.clearParameters();        // use full name to get the mship type
                  pstmt.setString(1, lname);
                  pstmt.setString(2, fname);
                  pstmt.setString(3, mi);
                  rs = pstmt.executeQuery();      

                  if (rs.next()) {

                     mship = rs.getString("m_ship");
                  }

               }
         }

     } catch (Exception exc) {
            Utilities.logDebug("BP", "Proshop_report_custom_merion.getMship ERR: " + exc.toString());
     } finally {

         try { rs.close(); }
         catch (Exception ignore) { }

         try { pstmt.close(); }
         catch (Exception ignore) { }
     }
     
     return(mship);

 }   // end of getMship
 
 
} // end servlet public class
