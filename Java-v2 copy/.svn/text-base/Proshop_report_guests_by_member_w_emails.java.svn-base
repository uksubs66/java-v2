/***************************************************************************************
 *   Proshop_report_guests_by_member_w_emails: This servlet will ouput a report to display the total number of guests
 *                                    for each member in the system.
 *
 *   Called by:     called by main menu options
 *
 *
 *
 *
 *    CUSTOM REPORT for Dallas Athletic Club.  This is the Guest by Member report 
 *                    that displays the members' emails addresses in place of their
 *                    member type and membership types.
 *                  
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.getClub;



public class Proshop_report_guests_by_member_w_emails extends HttpServlet {

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
    
    String excel = "no";
    
    if (req.getParameter("excel") != null) {
       
       excel = "yes";
    }
    
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
    
    String templott = (String)session.getAttribute("lottery");      // get lottery support indicator
    int lottery = Integer.parseInt(templott);

    String opt1 = "";     // radio button options (revbrkdn or showtypes)
            
    opt1 = (req.getParameter("opt1") != null) ? req.getParameter("opt1")  : "";

    String period = (req.getParameter("period") != null) ? req.getParameter("period")  : "";
      
    // strict enforce either value, default to this month
    if (!period.equals("last") && !period.equals("year") && !period.equals("lstyr")) {
    
       period = "this";
    }

    // 
    //  First time here - display warning message to inform user that this report can take several minutes!!!!
    //
    if (req.getParameter("continue") == null) {

       displayWarning(period, lottery, req, out);              // display warning message
       return;                                   // exit and wait for continue
    }


    //
    //  The following parm is only used for a custom report to display counts for each individual guest type.
    //  This must be enabled in the displayWarning method and run on the dev server !!!
    //
    if (opt1.equals("showtypes")) {

       showAll(period, lottery, excel, req, resp, out, con);              // display warning message
       return;                                   // exit and wait for continue
    }


    //
    //  Declare our local variables
    //
    ResultSet rs2 = null;
    PreparedStatement pstmtc2 = null;
    
    GregorianCalendar cal = new GregorianCalendar();            // get todays date
    
    int month = 0;  // will hold last month value
    int year = 0;   // will hold current year
    int gstcount = 0;
    int gstrev = 0;
    int gstnonrev = 0;
    
    long sdate = 0;    // for custom date range (custom reports to run one time)
    long edate = 0;
    
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
  /*
    period = "custom";         // indicate custom report
    
    sdate = 20080101;          // change these dates as needed and un-comment this block - the rest will work as is.
    edate = 20081231;
   */
    //                   END OF CUSTOM
    
    
    
    // start page output
    startPageOutput(excel, out);
    
    if (!excel.equals("yes")) {                // if not Excel Spreadsheet Format

       SystemUtils.getProshopSubMenu(req, out, lottery);               // required to allow submenus on this page
    }
    
    SimpleDateFormat tmpA = new SimpleDateFormat("MMMM yyyy");
    
    // output report title
    out.println("<font face=\"Arial, Helvetica, Sans-serif\"><b>Guests Per Member Report For ");
      
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
       out.println("<form method=\"post\" action=\"Proshop_report_guests_by_member\" target=\"_blank\">");
      // out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
       out.println("<input type=\"hidden\" name=\"period\" value=\"" + period + "\">");
       out.println("<input type=\"hidden\" name=\"continue\" value=\"yes\">");
       out.println("<td>");
       out.println("<input type=\"submit\" value=\"Excel\" name=\"excel\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp;");
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
                "<td><b>Email 1&nbsp;&nbsp;</b></td>" +
                "<td><b>Email 2&nbsp;&nbsp;&nbsp;</b></td>" +
                "<td><b>Member Number&nbsp;</b></td>" +
                "<td><b># of Guests&nbsp;</b></td>" +
                ((opt1.equals("revbrkdwn")) ? "<td><b>Revenue&nbsp;</b></td><td><b>Non-Revenue&nbsp;</b></td>" : "") +
                "</tr>");
    
    int alt_row = 0;
        
    try {
       
      String whereClause = "";
        
      if (period.equals("year") || period.equals("lstyr")) {            // if for whole year
      
         whereClause = "yy = ?";

      } else if (period.equals("custom")) {

         whereClause = "date >= ? AND date <= ?";
         
      } else {
      
         whereClause = "mm = ? AND yy = ?";
                           
      }       // end of IF date ranges

      
      String tmp_join_1 = "";
      String tmp_join_2 = "";
      String tmp_join_3 = "";
      String tmp_join_4 = "";
      String tmp_join_5 = "";
      String tmp_revsum = "";
      String tmp_subrevsum = "";

      if (opt1.equals("revbrkdwn")) {

          tmp_revsum = "SUM(rev) AS rev, SUM(nonrev) AS nonrev, ";
          tmp_subrevsum = "SUM(IF(revenue=0,1,0)) AS nonrev, SUM(IF(revenue=1,1,0)) AS rev,";
          tmp_join_1 = "LEFT OUTER JOIN guest5 ON guest5.guest = LEFT(teepast2.player1, LENGTH(guest5.guest)) ";
          tmp_join_2 = "LEFT OUTER JOIN guest5 ON guest5.guest = LEFT(teepast2.player2, LENGTH(guest5.guest)) ";
          tmp_join_3 = "LEFT OUTER JOIN guest5 ON guest5.guest = LEFT(teepast2.player3, LENGTH(guest5.guest)) ";
          tmp_join_4 = "LEFT OUTER JOIN guest5 ON guest5.guest = LEFT(teepast2.player4, LENGTH(guest5.guest)) ";
          tmp_join_5 = "LEFT OUTER JOIN guest5 ON guest5.guest = LEFT(teepast2.player5, LENGTH(guest5.guest)) ";

      }

      sql = "" +
        "SELECT SUM(rounds), " + tmp_revsum + " username, name_last, name_first, name_mi, email, email2, memNum FROM (" +
            "SELECT COUNT(*) AS rounds, " + tmp_subrevsum + " username, name_last, name_first, name_mi, email, email2, memNum " +
            "FROM teepast2 " +
            "LEFT OUTER JOIN member2b ON teepast2.userg1 = member2b.username " + tmp_join_1 +
            "WHERE " + whereClause + " AND show1 = 1 " +
            "GROUP BY userg1 " +
            "UNION ALL " +
            "SELECT COUNT(*) AS rounds, " + tmp_subrevsum + " username, name_last, name_first, name_mi, email, email2, memNum " +
            "FROM teepast2 " +
            "LEFT OUTER JOIN member2b ON teepast2.userg2 = member2b.username " + tmp_join_2 +
            "WHERE " + whereClause + " AND show2 = 1 " +
            "GROUP BY userg2 " +
            "UNION ALL " +
            "SELECT COUNT(*) AS rounds, " + tmp_subrevsum + " username, name_last, name_first, name_mi, email, email2, memNum " +
            "FROM teepast2 " +
            "LEFT OUTER JOIN member2b ON teepast2.userg3 = member2b.username " + tmp_join_3 +
            "WHERE " + whereClause + " AND show3 = 1 " +
            "GROUP BY userg3 " +
            "UNION ALL " +
            "SELECT COUNT(*) AS rounds, " + tmp_subrevsum + " username, name_last, name_first, name_mi, email, email2, memNum " +
            "FROM teepast2 " +
            "LEFT OUTER JOIN member2b ON teepast2.userg4 = member2b.username " + tmp_join_4 +
            "WHERE " + whereClause + " AND show4 = 1 " +
            "GROUP BY userg4 " +
            "UNION ALL " +
            "SELECT COUNT(*) AS rounds, " + tmp_subrevsum + " username, name_last, name_first, name_mi, email, email2, memNum " +
            "FROM teepast2 " +
            "LEFT OUTER JOIN member2b ON teepast2.userg5 = member2b.username " + tmp_join_5 +
            "WHERE " + whereClause + " AND show5 = 1 " +
            "GROUP BY userg5 " +
        ") AS t " +
        "GROUP BY username " +
        "ORDER BY name_last, name_first";

      
      out.println("<!--");
      out.println("period=" + period);
      out.println("opt1=" + opt1);
      out.println("sdate=" + sdate);
      out.println("edate=" + edate);
      out.println("month=" + month);
      out.println("year=" + year);
      out.println(tmpA.format(cal.getTime()));
      out.println(sql);
      out.println("-->");
      

      pstmtc2 = con.prepareStatement(sql);
      pstmtc2.clearParameters();

      if (period.equals("year") || period.equals("lstyr")) {            // if for whole year

         pstmtc2.setInt(1, year);
         pstmtc2.setInt(2, year);
         pstmtc2.setInt(3, year);
         pstmtc2.setInt(4, year);
         pstmtc2.setInt(5, year);

      } else if (period.equals("custom")) {

         pstmtc2.setLong(1, sdate);
         pstmtc2.setLong(2, edate);
         pstmtc2.setLong(3, sdate);
         pstmtc2.setLong(4, edate);
         pstmtc2.setLong(5, sdate);
         pstmtc2.setLong(6, edate);
         pstmtc2.setLong(7, sdate);
         pstmtc2.setLong(8, edate);
         pstmtc2.setLong(9, sdate);
         pstmtc2.setLong(10, edate);

      } else {

         pstmtc2.setInt(1, month);
         pstmtc2.setInt(2, year);
         pstmtc2.setInt(3, month);
         pstmtc2.setInt(4, year);
         pstmtc2.setInt(5, month);
         pstmtc2.setInt(6, year);
         pstmtc2.setInt(7, month);
         pstmtc2.setInt(8, year);
         pstmtc2.setInt(9, month);
         pstmtc2.setInt(10, year);
      }

      rs2 = pstmtc2.executeQuery();      // execute the prepared stmt

      while (rs2.next()) {

          gstcount = rs2.getInt(1);

          if (opt1.equals("revbrkdwn")) {
              gstrev = rs2.getInt("rev");
              gstnonrev = rs2.getInt("nonrev");
          }


         //
         //  Display this member if any guests found
         //
         if (gstcount > 0 ) {

            name_last = rs2.getString("name_last");
            name_first = rs2.getString("name_first");
            name_mi = rs2.getString("name_mi");
            m_ship = rs2.getString("email");
            m_type = rs2.getString("email2");
            mem_num = rs2.getString("memNum");
            
            // skip null row
            if (name_last != null) {
            
                alt_row = (alt_row == 0) ? 1 : 0;
                out.println("<tr" + ((alt_row == 0) ? " bgcolor=\"#FDFDEF\"" : "") + " style=\"font-family:arial;color:black;font-size:.8em\">");
                out.println("<td nowrap>" + name_last + ", " + name_first + " " + name_mi + "</td>");
                out.println("<td>" + m_ship + "</td><td>" + m_type + "</td><td align=\"center\">" + mem_num + "</td>" +
                          "<td align=\"center\">" + gstcount + "</td>");
                if (opt1.equals("revbrkdwn")) out.println("<td align=\"center\">" + gstrev + "</td><td align=\"center\">" + gstnonrev + "</td>");
                out.println("</tr>");
                out.flush();          // try to prevent the load balancer from timing out (12 minutes)
            }
         }

      } // end of WHILE guest times found

      pstmtc2.close();    // close stmt and go check next member

            
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

       out.println(SystemUtils.HeadTitle("Guests By Member Report"));  
       
    } else {
    
       out.println("<html><head><title>Guests Per Member Report</title></head>");
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
 //**********************************************************************
 //   Custom report to show individual guest type counts
 //**********************************************************************
 //
 private void showAll(String period, int lottery, String excel, HttpServletRequest req, HttpServletResponse resp, PrintWriter out, Connection con) {

    //
    //  Declare our local variables
    //
    ResultSet rs = null;
    ResultSet rs2 = null;
    PreparedStatement pstmt = null;
    Statement stmt = null;
    
    int month = 0;  // will hold last month value
    int year = 0;   // will hold current year
    int gstcount = 0;
    int i = 0;
    
    long sdate = 0;    // for custom date range (custom reports to run one time)
    long edate = 0;
    
    String user = "";
    String name_last = "";
    String name_first = "";
    String name_mi = "";
    String m_ship = "";
    String m_type = "";
    String mem_num = "";
    String sql;

    
    GregorianCalendar cal = new GregorianCalendar();            // get todays date
    
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
    
    sdate = 20080101;          // change these dates as needed and un-comment this block - the rest will work as is.
    edate = 20081030;
   */
    //                   END OF CUSTOM
    
    
   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con); // golf only report

   try {

      getClub.getParms(con, parm);        // get the guest types
   }
   catch (Exception ignore) {
   }

   int [] gstRnds = new int [parm.MAX_Guests];       // use array for the 36 guest types
   int [] show = new int [5];     
   String [] player = new String [5];     
   String [] userg = new String [5];     

   //
   //   Remove any guest types that are null - for tests below
   //
   for (i = 0; i < parm.MAX_Guests; i++) {

      if (parm.guest[i].equals( "" )) {

         parm.guest[i] = "$@#!^&*";      // make so it won't match player name
      }
   }

    // handle excel output
    try{
        if (excel.equals("yes")) {                // if user requested Excel Spreadsheet Format
            resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
        }
    }
    catch (Exception exc) {
    }
    
    // start page output
    startPageOutput(excel, out);
    
    if (!excel.equals("yes")) {                // if not Excel Spreadsheet Format

       SystemUtils.getProshopSubMenu(req, out, lottery);               // required to allow submenus on this page
    }
    
    SimpleDateFormat tmpA = new SimpleDateFormat("MMMM yyyy");
    
    // output report title
    out.println("<font face=\"Arial, Helvetica, Sans-serif\"><b>Guests Per Member Report For ");
      
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
       out.println("<form method=\"post\" action=\"Proshop_report_guests_by_member\" target=\"_blank\">");
       //out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
       out.println("<input type=\"hidden\" name=\"period\" value=\"" + period + "\">");
       out.println("<input type=\"hidden\" name=\"continue\" value=\"yes\">");
       out.println("<input type=\"hidden\" name=\"showtypes\" value=\"yes\">");
       out.println("<td>");
       out.println("<input type=\"submit\" value=\"Excel\" name=\"excel\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp;");
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
                "<td><b>Total Guests&nbsp;</b></td>");
                
               for (i = 0; i < parm.MAX_Guests; i++) {          // chack all 36 guest types - list the guest type

                  if (!parm.guest[i].equals( "" ) && !parm.guest[i].equals( "$@#!^&*" )) {

                     out.println("<td><b>" + parm.guest[i] + "&nbsp;</b></td>"); 
                  }
               }
                
               out.println("</tr>");
    
    int alt_row = 0;
        
    try {
       
      String whereClause = "";
        
      if (period.equals("year") || period.equals("lstyr")) {            // if for whole year
      
         whereClause = "yy = ?";

      } else if (period.equals("custom")) {

         whereClause = "date >= ? AND date <= ?";
         
      } else {
      
         whereClause = "mm = ? AND yy = ?";
                           
      }       // end of IF date ranges
      

      //
      //  Get each member
      //
      sql = "SELECT username, name_last, name_first, name_mi, m_ship, m_type, memNum FROM member2b ORDER BY name_last, name_first, name_mi";
      stmt = con.createStatement();
      rs = stmt.executeQuery(sql);

      while (rs.next()) {

         user = rs.getString("username");
         name_last = rs.getString("name_last");
         name_first = rs.getString("name_first");
         name_mi = rs.getString("name_mi");
         m_ship = rs.getString("m_ship");
         m_type = rs.getString("m_type");
         mem_num = rs.getString("memNum");
         
         gstcount = 0;       // init counts

         for (i = 0; i < parm.MAX_Guests; i++) {         

            gstRnds[i] = 0;
         }
         
         
         try {        
            //
            //  Now gather the guest counts for each member
            //
            sql = "" +
              "SELECT player1, player2, player3, player4, player5, show1, show2, show3, show4, show5, userg1, userg2, userg3, userg4, userg5 " +
                  "FROM teepast2 " +
                  "WHERE (userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?) AND " + whereClause;


            pstmt = con.prepareStatement(sql);
            pstmt.clearParameters();

            pstmt.setString(1, user);
            pstmt.setString(2, user);
            pstmt.setString(3, user);
            pstmt.setString(4, user);
            pstmt.setString(5, user);

            if (period.equals("year") || period.equals("lstyr")) {            // if for whole year

               pstmt.setInt(6, year);

            } else if (period.equals("custom")) {

               pstmt.setLong(6, sdate);
               pstmt.setLong(7, edate);

            } else {

               pstmt.setInt(6, month);
               pstmt.setInt(7, year);
            }

            rs2 = pstmt.executeQuery();      // execute the prepared stmt

            while (rs2.next()) {

                player[0] = rs2.getString("player1");
                player[1] = rs2.getString("player2");
                player[2] = rs2.getString("player3");
                player[3] = rs2.getString("player4");
                player[4] = rs2.getString("player5");
                show[0] = rs2.getInt("show1");
                show[1] = rs2.getInt("show2");
                show[2] = rs2.getInt("show3");
                show[3] = rs2.getInt("show4");
                show[4] = rs2.getInt("show5");
                userg[0] = rs2.getString("userg1");
                userg[1] = rs2.getString("userg2");
                userg[2] = rs2.getString("userg3");
                userg[3] = rs2.getString("userg4");
                userg[4] = rs2.getString("userg5");

                for (int i2=0; i2<5; i2++) {

                   if (userg[i2].equals(user) && show[i2] == 1) {   // if this player is a guest of the member

                      gstcount++;                  // add one to total guest count for this member

                      loop1:
                      for (i = 0; i < parm.MAX_Guests; i++) {          // find matching guest type

                         if (player[i2].startsWith( parm.guest[i] )) {

                            gstRnds[i]++;      // bump that counter
                            break loop1;
                         }
                      }
                   }
                }

            }               // end of each tee time
            pstmt.close();
      
          }
          catch (Exception e2) {
              displayDatabaseErrMsg("Error gathering guest counts for report.", e2.getMessage(), out);
              return;
          }
    
            
         alt_row = (alt_row == 0) ? 1 : 0;
         out.println("<tr" + ((alt_row == 0) ? " bgcolor=\"#FDFDEF\"" : "") + " style=\"font-family:arial;color:black;font-size:.8em\">");
         out.println("<td nowrap>" + name_last + ", " + name_first + " " + name_mi + "</td>");
         out.println("<td>" + m_ship + "</td><td>" + m_type + "</td><td align=\"center\">" + mem_num + "</td>" +
                    "<td align=\"center\">" + gstcount + "</td>");
                
         for (i = 0; i < parm.MAX_Guests; i++) {          // chack all 36 guest types - list the guest type

            if (!parm.guest[i].equals( "" ) && !parm.guest[i].equals( "$@#!^&*" )) {

               out.println("<td align=\"center\">" + gstRnds[i] + "</td>"); 
            }
         }
         out.println("</tr>");      
      
         
      }                       // end of members
      stmt.close();

            
    }
    catch (Exception e) {
        displayDatabaseErrMsg("Error loading member information for report.", e.getMessage(), out);
        return;       
    }
    
    out.println("</table><br><br>");
    
    endPageOutput(out);
        
 }


 //
 //   Warning Message
 //
 private void displayWarning(String period, int lottery, HttpServletRequest req, PrintWriter out) {

    out.println(SystemUtils.HeadTitle("Report Warning"));
    SystemUtils.getProshopSubMenu(req, out, lottery);
    out.println("<script type=\"text/javascript\">");
    out.println("function grabChkBox() {");
    out.println(" if (document.forms['f1'].revbrkdwn.checked) {");
    out.println("  document.forms['f2'].revbrkdwn.value='yes';");
    out.println(" }");
    out.println("}");
    out.println("</script>");
    out.println("<BODY><CENTER>");
    out.println("<BR><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
    out.println("<hr width=\"40%\">");
    out.println("<BR><H2>WARNING</H2>");
    out.println("This report may take several minutes to complete.");
    out.println("<BR>Please be patient.");
    out.println("<BR><BR>Select one or neither of the options below, then click on the report format you would like.");
    out.println("<BR><BR>");
    out.println("<form method=\"post\" action=\"Proshop_report_guests_by_member\" id=f1 name=f1>");
    out.println("<BR>");
    out.println("<span style=\"background-color:#CCCCAA;border:1px solid #336633; padding:7px\">");
    out.println("<input type=radio name=opt1 value='revbrkdwn'>&nbsp;Include Revenue/Non-Revenue Breakdown");
    out.println("<input type=radio name=opt1 value='showtypes'>&nbsp;Show Counts for each Guest Type");  // use this for custom report !!!!!!!!!!!
    out.println("</span>");
    out.println("<BR><BR><BR>");
    out.println("<input type=\"hidden\" name=\"period\" value=\"" +period+ "\">");
    out.println("<input type=\"hidden\" name=\"continue\" value=\"yes\">");
    out.println("<input type=\"submit\" value=\"Web Page\" style=\"text-decoration:underline; background:#8B8970; width:100px\">");
    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    out.println("<input type=\"submit\" value=\"Excel\" name=\"excel\" style=\"text-decoration:underline; background:#8B8970; width:100px\">");
    out.println("</form>");
    out.println("<BR>");
    out.println("<form method=\"get\" action=\"Proshop_announce\">");
    out.println("<input type=\"submit\" value=\"Cancel\" style=\"text-decoration:underline; background:#8B8970; width:100px\">");
    out.println("</form>");
    out.println("</CENTER></BODY></HTML>");
 }

} // end servlet public class
