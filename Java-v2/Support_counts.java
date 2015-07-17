/***************************************************************************************
 *   Support_counts:  This servlet will display the login counts for each club.
 *
 *
 *
 ***************************************************************************************
 */

import com.foretees.common.Common_Server;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import com.foretees.common.Connect;
import com.foretees.common.Utilities;
import com.foretees.common.sendEmail;


public class Support_counts extends HttpServlet {

 public static String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
 
 static int TIMER_SERVER = 101;                              // Timer Server ID value (timer server)
 static int TIMER_SERVER2 = 102;                             // additional Timer Server ID value (node 1, instance 1)


 // Process the form request from support_upgrade.htm.....

 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Connection con = null;                  // init DB objects
   Connection con2 = null;                  // init DB objects
   Statement stmt = null;
   Statement stmt2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   String support = "support";             // valid username
   String sales = "sales";
   String fullname = "";         
           
   HttpSession session = null;

   int cnum = 0;
   int tcount = 0;
   int ccount = 0;
   int count = 0;
   int mem_count = 0;
   int use_count = 0;
   

   // Make sure user didn't enter illegally.........

   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   String user = (String)session.getAttribute("user");   // get username

   if (!user.equals( support ) && !user.startsWith ( sales )) {

      invalidUser(out);            // Intruder - reject
      return;
   }
   
   
   //
   //  Use this to manually run the monthly stats job below (comment out check for servier id below)
   //
   /*
   if (user.equals("support")) {
       
       run_Logreports();    // to force the monthly run
       
       out.println("<HTML><HEAD><TITLE>Stats Job Complete</TITLE></HEAD>");
       out.println("<BODY><CENTER><H3>Stats Job Complete</H3>");
       out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>.");
       out.println("</CENTER></BODY></HTML>");
       return;
   }
   * 
   */

   //
   // Load the JDBC Driver and connect to DB
   //
   String club = "v5";

   try {

      con2 = dbConn.Connect(club);
   }
   catch (Exception exc) {

      // Error connecting to db....

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   
   //
   //  Output to Excel
   //
   try{

      resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
    
   }
   catch (Exception exc) {
   }

   
   //
   //  Build the HTML page to prompt Proshop for report options
   //
   out.println("<html><head><title>Support Usage Reports Page</title></head>");

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");
   out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<br><b>Member Login Reports</b><br>");
   out.println("</font>");
   out.println("<br>");

   out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
   out.println("<tr><td align=\"center\" bgcolor=\"#336633\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("#");
   out.println("</font></td>");
   out.println("<td align=\"center\" bgcolor=\"#336633\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("Club Name");
   out.println("</font></td>");
   out.println("<td align=\"center\" bgcolor=\"#336633\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("Number of Members");
   out.println("</font></td>");
   out.println("<td align=\"center\" bgcolor=\"#336633\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("Number of Logins");
   out.println("</font></td>");
   out.println("</tr>");

   //
   // Get the club names from the 'clubs' table
   //
   //  Process each club in the table
   //
   try {

      stmt2 = con2.createStatement();              // create a statement

      rs2 = stmt2.executeQuery("SELECT clubname, fullname FROM clubs ORDER BY fullname");

      while (rs2.next()) {

         club = rs2.getString(1);                 // get club name
         fullname = rs2.getString(2);             // get club's full name

         //  weed out the demo clubs, etc.
         
         boolean skip = false;
         
         if (club.startsWith("demo") || club.startsWith("mfirst") || club.startsWith("testJonas") || 
              club.startsWith("notify") || club.startsWith("test") || club.equals("admiralscove2") ) {   
            
            skip = true;      // skip it
         }
         
         if (skip == false) {

            con = dbConn.Connect(club);         // get a connection to this club's db

            stmt = con.createStatement();        // create a statement

            rs = stmt.executeQuery("SELECT COUNT(*) FROM member2b WHERE count > 0");

            if (rs.next()) {

               use_count = rs.getInt(1);      // get number of members with at least one login
            }

            rs = stmt.executeQuery("SELECT SUM(count) FROM member2b WHERE count > 0");

            if (rs.next()) {

               ccount = rs.getInt(1);    // login count for this club
            }

            stmt.close();

            tcount += ccount;          // keep total count of all clubs

            mem_count += use_count;    // total of members counted

            cnum++;                    // club number (counter)

            out.println("<tr><td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(cnum);
            out.println("</font></td>");
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(fullname);
            out.println("</font></td>");
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(use_count);
            out.println("</font></td>");
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(ccount);
            out.println("</font></td>");
            out.println("</tr>");

            ccount = 0;           // init for next club
            use_count = 0;

            con.close();                           // close the connection to the club db
         }
      }                                         // do all clubs
      stmt2.close();
      con2.close();

      out.println("<tr><td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("&nbsp;");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Grand Totals:</b>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println(mem_count);
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println(tcount);
      out.println("</font></td>");
      out.println("</tr>");
      out.println("</table>");

   }
   catch (Exception e2) {

      // Error connecting to db....

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the DB - error2.");
      out.println("<BR>Exception: "+ e2.getMessage());
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   out.println("</CENTER></BODY></HTML>");
 }


 // *********************************************************
 // Illegal access by user - force user to login....
 // *********************************************************

 private void invalidUser(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H2>Access Error</H2><BR>");
   out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
   out.println("<BR><BR>Please <A HREF=\"Logout\">login</A>");
   out.println("</CENTER></BODY></HTML>");

 }
 
 public static void run_Logreports(){


     Connection conA = null;                  // init DB objects
     Connection conB = null;                  // init DB objects
     Statement stmtA = null;
     Statement stmtB = null;
     PreparedStatement pstmtA = null;
     PreparedStatement pstmtA2 = null;
     PreparedStatement pstmtB = null;
     PreparedStatement pstmtB2 = null;

     ResultSet rsA = null;
     ResultSet rsB = null;
     String club = "v5";
     String fullname = "";
     String messageBody = "";

     int use_count = 0;     // get number of members with at least one login
     int club_count = 0;
     int members = 0;
     int old_mems = 0;
     int ccount = 0;
     int mobile = 0;
     int iphone = 0;
     int old_mobile = 0;

     int use_count_tot = 0;     //get total number of members with at least one default login
     int members_tot = 0;       //get total number of members with at least one mobile login
     int old_mems_tot = 0;      //get total number of members with at least one old_mobile login    
     int ccount_tot = 0;        //total number of logins standard
     int mobile_tot = 0;        //total number of logins mobile
     int iphone_tot = 0;        //total number of logins Iphone
     int old_mobile_tot = 0;    //total number of logins old mobile
     
     int app_mems = 0;
     int app_logins = 0;
     int total_app_mems = 0;
     int total_app_logins = 0;

     Calendar cal = new GregorianCalendar();       // get todays date
     int year = cal.get(Calendar.YEAR);
     int month = cal.get(Calendar.MONTH) + 1;
     int day = cal.get(Calendar.DAY_OF_MONTH);
     
     if (day <= 2) {          // if the first of the month
         
         month -= 1;          // we are gathering last month's stats
         
         if (month == 0) {    // if Jan and gathering Dec's stats
             
             month = 12;      // change month to Dec
             year--;          //    of prev year
         }
     }

     //if ((day == 1)) {  //run the report on the first of the month
     if (Common_Server.SERVER_ID == TIMER_SERVER2 && (day == 1)) {  //run the report on the first of the month
         
         messageBody = "Running Monthly Login Stat Logging:\n\n";
         
         try {

             conA = dbConn.Connect(club);
             stmtA = conA.createStatement();              // create a statement
             rsA = stmtA.executeQuery("SELECT clubname, fullname FROM clubs ORDER BY fullname");

             while (rsA.next()) {

                 club = rsA.getString(1);                 // get club name
                 fullname = rsA.getString(2);             // get club's full name

                 //  weed out the demo clubs, etc.

                 boolean skip = false;

                 if ((club.startsWith("demo") && !club.equalsIgnoreCase("demowill")) || club.startsWith("mfirst") || club.startsWith("testJonas")
                         || club.startsWith("notify") || club.startsWith("test") || club.equals("admiralscove2")) {

                     skip = true;      // skip it
                 }

                 if (skip == false) {

                     use_count = 0;     // reset counts
                     members = 0;
                     old_mems = 0;
                     ccount = 0;
                     mobile = 0;
                     iphone = 0;
                     old_mobile = 0;
                     app_mems = 0;
                     app_logins = 0;

                     try {

                         conB = dbConn.Connect(club);         // get a connection to this club's db
                         stmtB = conB.createStatement();        // create a statement

                         rsB = stmtB.executeQuery("SELECT SUM(IF(count>0,1,0)) AS mem_count_standard, Sum(IF(mobile_count>0,1,0)) AS mem_count_mobile, "
                                 + "Sum(IF(old_mobile_count>0,1,0)) AS mem_count_oldmobile, Sum(IF(mobile_app_count>0,1,0)) AS mem_count_app, "
                                 + "SUM(count) AS logins_standard , SUM(mobile_count) AS logins_mobile, SUM(mobile_iphone) AS logins_mobile_ios, "
                                 + "SUM(old_mobile_count) AS logins_oldmobile, SUM(mobile_app_count) AS logins_app  FROM member2b;");
                         if (rsB.next()) {

                             use_count = rsB.getInt("mem_count_standard");      // get number of members with at least one login
                             members = rsB.getInt("mem_count_mobile");
                             old_mems = rsB.getInt("mem_count_oldmobile");
                             ccount = rsB.getInt("logins_standard");
                             mobile = rsB.getInt("logins_mobile");
                             iphone = rsB.getInt("logins_mobile_ios");
                             old_mobile = rsB.getInt("logins_oldmobile");
                             app_mems = rsB.getInt("mem_count_app");
                             app_logins = rsB.getInt("logins_app");

                         }

                         use_count_tot += use_count;     //get total number of members with at least one default login
                         members_tot += members;       //get total number of members with at least one mobile login
                         old_mems_tot += old_mems;      //get total number of members with at least one old_mobile login    
                         ccount_tot += ccount;        //total number of logins standard
                         mobile_tot += mobile;        //total number of logins mobile
                         iphone_tot += iphone;        //total number of logins Iphone
                         old_mobile_tot += old_mobile;    //total number of logins old mobile
                         total_app_mems += app_mems;
                         total_app_logins += app_logins;





                         pstmtB = conB.prepareStatement("INSERT IGNORE INTO login_stats (YEAR, MONTH) VALUES (?, ?);");        // create a statement for club database
                         pstmtB.clearParameters();        // clear the parms
                         pstmtB.setInt(1, year);
                         pstmtB.setInt(2, month);
                         pstmtB.executeUpdate();
                         pstmtB.close();

                         pstmtB2 = conB.prepareStatement("UPDATE login_stats SET mem_count_standard = ? , logins_standard = ?, mem_count_mobile = ? , logins_mobile = ? , "
                                 + "logins_mobile_ios = ? , logins_oldmobile = ? , mem_count_oldmobile = ?, mem_count_app = ?, logins_app = ? "
                                 + "WHERE year = ? AND month = ?;");        // create a statement for club database
                         pstmtB2.clearParameters();        // clear the parms
                         pstmtB2.setInt(1, use_count);
                         pstmtB2.setInt(2, ccount);
                         pstmtB2.setInt(3, members);
                         pstmtB2.setInt(4, mobile);
                         pstmtB2.setInt(5, iphone);
                         pstmtB2.setInt(6, old_mobile);
                         pstmtB2.setInt(7, old_mems);
                         pstmtB2.setInt(8, app_mems);
                         pstmtB2.setInt(9, app_logins);
                         
                         pstmtB2.setInt(10, year);
                         pstmtB2.setInt(11, month);
                         pstmtB2.executeUpdate();
                         pstmtB2.close();


                         messageBody += "" + club + ":Success \n";


                     } catch (Exception e3) {
                         Utilities.logError("Support_counts--DB Connection Error--" + club + "--" + e3.getMessage());
                     } finally {
                         Connect.close(rsB, stmtB, conB);
                     }

                     club_count++;  
                 }
             }
             pstmtA = conA.prepareStatement("INSERT IGNORE INTO login_stats_totals (YEAR, MONTH) VALUES (?, ?);");        // create a statement for v5 
             pstmtA.clearParameters();        // clear the parms
             pstmtA.setInt(1, year);
             pstmtA.setInt(2, month);
             pstmtA.executeUpdate();
             pstmtA.close();

             pstmtA2 = conA.prepareStatement("UPDATE login_stats_totals SET mem_count_standard = ? , logins_standard = ?, mem_count_mobile = ? , logins_mobile = ? , "
                     + "logins_mobile_ios = ? , logins_oldmobile = ? , mem_count_oldmobile = ?, mem_count_app = ?, logins_app = ? "
                     + "WHERE year = ? AND month = ?;");        // create a statement for v5 
             pstmtA2.clearParameters();        // clear the parms
             pstmtA2.setInt(1, use_count_tot);
             pstmtA2.setInt(2, ccount_tot);
             pstmtA2.setInt(3, members_tot);
             pstmtA2.setInt(4, mobile_tot);
             pstmtA2.setInt(5, iphone_tot);
             pstmtA2.setInt(6, old_mobile_tot);
             pstmtA2.setInt(7, old_mems_tot);
             pstmtA2.setInt(8, total_app_mems);
             pstmtA2.setInt(9, total_app_logins);
             
             pstmtA2.setInt(10, year);
             pstmtA2.setInt(11, month);
             pstmtA2.executeUpdate();
             pstmtA2.close();

             messageBody += "\n" + club + ": Success \n";
         } catch (Exception exc) {

             Utilities.logError("Support_counts.run_Logreports --DB Connection Error--" + club + "--" + exc.getMessage());

         } finally {
             Connect.close(rsA, stmtA, conA);
         }
         ArrayList<String> toAddresses = new ArrayList<String>();
         toAddresses.add("dev@foretees.com");
         toAddresses.add("support@foretees.com");
         toAddresses.add("bparise@foretees.com");

         sendEmail.sendFTNotification(toAddresses, "", "", "log_Reports", messageBody, "", conA);

         Utilities.logError("Support_counts.run_Logreports --Process Complete for " +club_count+ " clubs--");
     }
 }

}
