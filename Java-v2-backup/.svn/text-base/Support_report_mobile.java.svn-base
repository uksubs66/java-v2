/***************************************************************************************     
 *   Support_report_mobile:  List the number of mobile logins for each club.
 *
 *
 *   called by:  support_main2.htm
 *              
 *
 *   created: 1/22/2010   Bob P.
 *
 *   last updated:
 *
 *     4/27/14 BP  Add column for old mobile counts - now tracked separately.
 *     4/01/14 BP  Removed check for clubs that have mobile disabled.  We want to include
 *                 clubs that might have allowed mobile prior to disabling it.
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


public class Support_report_mobile extends HttpServlet {
                
 String omit = "";

 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 

 //****************************************************
 // Process the initial request 
 //****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Statement stmt = null;
   ResultSet rs = null;

   HttpSession sess = verifyUser(req, out);       // check for intruder

   if (sess == null) {

      return;
   }

   Connection con = null;        
   Connection con2 = null;        

   
   String club = "v5";
   String fullname = "";

   String user = (String)sess.getAttribute("user");   // get username

   try {

      con2 = dbConn.Connect(club);          // get con to V5
   }
   catch (Exception exc) {
   }

   try{

      resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
    
   }
   catch (Exception exc) {
   }

   
   if (req.getParameter("caller") == null) {     // if NOT caller report

      doMobileReport(user, out, con2);      

      try {

         con2.close();

      }
      catch (Exception exc) {

         out.println("<BR><BR><H1>Database Access Error</H1>");
         out.println("<BR><BR>Error processing the clubs.");
         out.println("<BR><BR>Exception: " + exc.getMessage());
         if (user.equals("support")) {
             out.println("<BR><BR><a href=\"/" +rev+ "/support_main2.htm\">Return</a>");
         } else {
             out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Support_sales_main\">Return</a>");             
         }
         return;

      }   // end of member name list


   } else {
      
      //
      //  Build Caller report (display the caller value in club5 for each club)
      //
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<table border=\"1\" cellpadding=\"5\">");
      out.println("<tr><td align=\"left\">");
      out.println("<font size=\"2\">");
      out.println("<b>Club Name</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Site Name</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Caller</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Seamless</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Rsync</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Mobile</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Email</b></font></td>");
      out.println("</tr>");


      //
      //  Get each club in the system and get its parms
      //
      try {

         stmt = con2.createStatement();           

         rs = stmt.executeQuery("SELECT clubname, fullname FROM clubs WHERE non_billable = 0 ORDER BY clubname");      // get club names from V5 db

         while (rs.next()) {

            club = rs.getString(1);             // get a club name
            fullname = rs.getString(2);         // get the full name

            //  weed out the demo clubs, etc.

            boolean skip = false;

            /*
            if (club.startsWith("demo") || club.startsWith("mfirst") || club.startsWith("testJonas") || 
                 club.startsWith("notify") || club.startsWith("test") || club.equals("admiralscove2") ) {   

               skip = true;      // skip it
            }
            *       // added non-billable filter in query
            */

            if (skip == false) {

               con = dbConn.Connect(club);          // get con to this club

               doCallerReport(user, club, fullname, out, con);      

               con.close();                           // close the connection to the club db
            }
         }                   // do all clubs

         stmt.close();
         con2.close();

      }
      catch (Exception exc) {

         out.println("<BR><BR><H1>Database Access Error</H1>");
         out.println("<BR><BR>Error processing the clubs.");
         out.println("<BR><BR>Exception: " + exc.getMessage());
         if (user.equals("support")) {
             out.println("<BR><BR><a href=\"/" +rev+ "/support_main2.htm\">Return</a>");
         } else {
             out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Support_sales_main\">Return</a>");             
         }
         return;

      }   // end of member name list

      out.println("</table>");
      out.close();
   }  
      
 }  // end of doGet
 
 
 
 // *********************************************************
 //   Do mobile report
 // *********************************************************
 private void doMobileReport(String user, PrintWriter out, Connection con2) {

   Connection con = null;
   Statement stmt = null;
   Statement stmt2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;
   
   
   String course = "";
   String club = "";
   String fullname = "";
   String msac = "";
   int count = 0;
   int members = 0;
   int act_members = 0;
   int members_w_logins = 0;
   int old_mems = 0;
   int mobile = 0;
   int iphone = 0;
   int old_mobile = 0;
   int total_mems = 0;
   int total_old_mems = 0;
   int total_mobile = 0;
   int total_iphone = 0;
   int total_old = 0;
   int app_mems = 0;
   int total_app_mems = 0;
   int app_logins = 0;
   int total_app_logins = 0;
   int app_clubs_live = 0;
   int total_act_mems = 0;
   int total_mems_w_logins = 0;
   
   //
   //  Build Mobile Login report
   //
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"1\" cellpadding=\"5\">");
   out.println("<tr><td align=\"left\">");
   out.println("<font size=\"2\">");
   out.println("<b>Club Name</b></font></td>");
   out.println("<td align=\"center\"><font size=\"2\">");
   out.println("<b>Site Name</b></font></td>");
   out.println("<td align=\"center\"><font size=\"2\">");
   out.println("<b>Active Mems</b></font></td>");
   out.println("<td align=\"center\"><font size=\"2\">");
   out.println("<b>Act Mems w/Logins</b></font></td>");
   out.println("<td align=\"center\"><font size=\"2\">");
   out.println("<b>Mobile Mems</b></font></td>");
   out.println("<td align=\"center\"><font size=\"2\">");
   out.println("<b>Mobile Logins</b></font></td>");
   out.println("<td align=\"center\"><font size=\"2\">");
   out.println("<b>iPhone Logins</b></font></td>");
   out.println("<td align=\"center\"><font size=\"2\">");
   out.println("<b>Old Mobile Logins</b></font></td>");
   out.println("<td align=\"center\"><font size=\"2\">");
   out.println("<b>Old Mobile Mems</b></font></td>");
   out.println("<td align=\"center\"><font size=\"2\">");
   out.println("<b>App Logins</b></font></td>");
   out.println("<td align=\"center\"><font size=\"2\">");
   out.println("<b>App Mems</b></font></td>");
   out.println("<td align=\"center\"><font size=\"2\">");
   out.println("<b>% of Act Mems on App</b></font></td>");
   out.println("<td align=\"center\"><font size=\"2\">");
   out.println("<b>% of Act Mems w/Logins on App</b></font></td>");
   out.println("<td align=\"center\"><font size=\"2\">");
   out.println("<b>Logins Per App Mem</b></font></td>");
   out.println("</tr>");


   //
   //  Get each club in the system and get its parms
   //
   try {

      stmt2 = con2.createStatement();           

      rs2 = stmt2.executeQuery("SELECT COUNT(*) FROM clubs WHERE non_billable = 0 AND msac = 'Live'");      // get # of clubs live with the app

      if (rs2.next()) {

         app_clubs_live = rs2.getInt(1);             // get count
      }
         
      rs2 = stmt2.executeQuery("SELECT clubname, fullname, msac FROM clubs WHERE non_billable = 0 ORDER BY clubname");      // get club names from V5 db

      while (rs2.next()) {

         club = rs2.getString(1);             // get a club name
         fullname = rs2.getString(2);         // get the full name
         msac = rs2.getString(3);             // get the app status (null, Staging, or Live) - in case we want to identify the Live clubs

         //  weed out the demo clubs, etc.

         boolean skip = false;

         /*
         if (club.startsWith("demo") || club.startsWith("mfirst") || club.startsWith("testJonas") || 
               club.startsWith("notify") || club.startsWith("test") || club.equals("admiralscove2") ) {   

            skip = true;      // skip it
         }
         *       // added non_billable filter in query
         */

         if (skip == false) {
            
            members = 0;
            old_mems = 0;
            app_mems = 0;

            con = dbConn.Connect(club);          // get con to this club
    
            stmt = con.createStatement();           

            rs = stmt.executeQuery("SELECT COUNT(*) FROM member2b WHERE mobile_count > 0");    // get # of members that have logged into Mobile (all)

            if (rs.next()) {

               members = rs.getInt(1);         
            }

            rs = stmt.executeQuery("SELECT COUNT(*) FROM member2b WHERE inact = 0 AND billable > 0 AND deleted = 0");    // get # of members that are active for this club

            if (rs.next()) {

               act_members = rs.getInt(1);         
            }

            rs = stmt.executeQuery("SELECT COUNT(*) FROM member2b WHERE count > 0 AND inact = 0 AND billable > 0 AND deleted = 0");    // get # of active members that have logged into FT at least once

            if (rs.next()) {

               members_w_logins = rs.getInt(1);         
            }

            rs = stmt.executeQuery("SELECT COUNT(*) FROM member2b WHERE old_mobile_count > 0");    // get # of members that have logged into the Old Mobile site

            if (rs.next()) {

               old_mems = rs.getInt(1);         
            }

            rs = stmt.executeQuery("SELECT COUNT(*) FROM member2b WHERE mobile_app_count > 0");    // get # of members that have logged in via the app

            if (rs.next()) {

               app_mems = rs.getInt(1);         
            }

            rs = stmt.executeQuery("SELECT SUM(mobile_count), SUM(mobile_iphone), SUM(old_mobile_count), SUM(mobile_app_count) FROM member2b");

            if (rs.next()) {

               mobile = rs.getInt(1);         
               iphone = rs.getInt(2);         
               old_mobile = rs.getInt(3);         
               app_logins = rs.getInt(4);   
               
               double logins_per = 0;
               double percent_mems = 0;
               double percent_mems_logins = 0;
               
               if (app_mems > 0 && app_logins > 0) {
                   
                   // app logins per member with the app
                   logins_per = (double)Math.round(((double)app_logins/(double)app_mems) * 10) / 10;  // round to 1 decimal
                                      
                   // percentage of active members using the app
                   percent_mems = (double)(Math.round(((double)app_mems/(double)act_members) * 1000) / 10);    // convert to a percentage value
                                      
                   // percentage of members w/logins using the app
                   percent_mems_logins = (double)(Math.round(((double)app_mems/(double)members_w_logins) * 1000) / 10);    // convert to a percentage value
               }
               
               out.println("<tr><td align=\"left\">");
               out.println("<font size=\"2\">");
               out.println(fullname + "</font></td>");
               out.println("<td align=\"center\"><font size=\"2\">");
               out.println(club + "</font></td>");
               out.println("<td align=\"center\"><font size=\"2\">");
               out.println(act_members + "</font></td>");
               out.println("<td align=\"center\"><font size=\"2\">");
               out.println(members_w_logins + "</font></td>");
               out.println("<td align=\"center\"><font size=\"2\">");
               out.println(members + "</font></td>");
               out.println("<td align=\"center\"><font size=\"2\">");
               out.println(mobile + "</font></td>");
               out.println("<td align=\"center\"><font size=\"2\">");
               out.println(iphone + "</font></td>");
               out.println("<td align=\"center\"><font size=\"2\">");
               out.println(old_mobile + "</font></td>");
               out.println("<td align=\"center\"><font size=\"2\">");
               out.println(old_mems + "</font></td>");
               out.println("<td align=\"center\"><font size=\"2\">");
               out.println(app_logins + "</font></td>");
               out.println("<td align=\"center\"><font size=\"2\">");
               out.println(app_mems + "</font></td>");
               out.println("<td align=\"center\"><font size=\"2\">");
               if (percent_mems > 0) {
                   out.println(" " + percent_mems + "% </font></td>");
               } else {              
                   out.println(" &nbsp;</font></td>");
               }
               out.println("<td align=\"center\"><font size=\"2\">");
               if (percent_mems_logins > 0) {
                   out.println(" " + percent_mems_logins + "% </font></td>");
               } else {              
                   out.println(" &nbsp;</font></td>");
               }
               out.println("<td align=\"center\"><font size=\"2\">");
               if (logins_per > 0) {
                   out.println(logins_per + "</font></td>");
               } else {              
                   out.println(" &nbsp;</font></td>");
               }
               out.println("</tr>");      

               total_mems += members;     // track total counts
               total_old_mems += old_mems;
               total_mobile += mobile;
               total_iphone += iphone;
               total_old += old_mobile;
               total_app_mems += app_mems;
               total_app_logins += app_logins;
               total_act_mems += act_members;
               total_mems_w_logins += members_w_logins;
            }

            stmt.close();
            con.close();
            
         }   // end of IF skip
         
      }   // end of WHILE clubs
    
      stmt2.close();
         
   }
   catch (Exception exc) {
      out.println("<BR><BR><H1>Database Access Error</H1>");
      out.println("<BR><BR>Error getting counts for " +club+ ".");
      out.println("<BR><BR>Exception: " + exc.getMessage());
        if (user.equals("support")) {
            out.println("<BR><BR><a href=\"/" +rev+ "/support_main2.htm\">Return</a>");
        } else {
            out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Support_sales_main\">Return</a>");             
        }
   }
   
   out.println("<tr><td align=\"right\">");
   out.println("<font size=\"3\">");
   out.println("<b>TOTAL:</b></font></td>");
   out.println("<td align=\"center\"><font size=\"3\"><b>");
   out.println("&nbsp;</b></font></td>");
   out.println("<td align=\"center\"><font size=\"3\"><b>");
   out.println(total_act_mems + "</b></font></td>");
   out.println("<td align=\"center\"><font size=\"3\"><b>");
   out.println(total_mems_w_logins + "</b></font></td>");
   out.println("<td align=\"center\"><font size=\"3\"><b>");
   out.println(total_mems + "</b></font></td>");
   out.println("<td align=\"center\"><font size=\"3\"><b>");
   out.println(total_mobile + "</b></font></td>");
   out.println("<td align=\"center\"><font size=\"3\"><b>");
   out.println(total_iphone + "</b></font></td>");
   out.println("<td align=\"center\"><font size=\"3\"><b>");
   out.println(total_old + "</b></font></td>");
   out.println("<td align=\"center\"><font size=\"3\"><b>");
   out.println(total_old_mems + "</b></font></td>");
   out.println("<td align=\"center\"><font size=\"3\"><b>");
   out.println(total_app_logins + "</b></font></td>");
   out.println("<td align=\"center\"><font size=\"3\"><b>");
   out.println(total_app_mems + "</b></font></td>");
   out.println("<td align=\"center\"><font size=\"2\">");
   out.println("&nbsp;</font></td>");
   out.println("<td align=\"center\"><font size=\"2\">");
   out.println("&nbsp;</font></td>");
   out.println("<td align=\"center\"><font size=\"2\">");
   out.println("&nbsp;</font></td>");
   out.println("</tr>");      
   out.println("<tr><td align=\"left\">");
   out.println("<font size=\"2\">");
   out.println("&nbsp;</font></td>");
   out.println("<td align=\"center\"><font size=\"2\">");
   out.println("&nbsp;</font></td>");
   out.println("<td align=\"center\"><font size=\"2\">");
   out.println("<b>Active Mems</b></font></td>");
   out.println("<td align=\"center\"><font size=\"2\">");
   out.println("<b>Act Mems w/Logins</b></font></td>");
   out.println("<td align=\"center\"><font size=\"2\">");
   out.println("<b>Mobile Mems</b></font></td>");
   out.println("<td align=\"center\"><font size=\"2\">");
   out.println("<b>Mobile Logins</b></font></td>");
   out.println("<td align=\"center\"><font size=\"2\">");
   out.println("<b>iPhone Logins</b></font></td>");
   out.println("<td align=\"center\"><font size=\"2\">");
   out.println("<b>Old Mobile Logins</b></font></td>");
   out.println("<td align=\"center\"><font size=\"2\">");
   out.println("<b>Old Mobile Mems</b></font></td>");
   out.println("<td align=\"center\"><font size=\"2\">");
   out.println("<b>App Logins</b></font></td>");
   out.println("<td align=\"center\"><font size=\"2\">");
   out.println("<b>App Mems</b></font></td>");
   out.println("<td align=\"center\"><font size=\"2\">");
   out.println("<b>% of Act Mems on App</b></font></td>");
   out.println("<td align=\"center\"><font size=\"2\">");
   out.println("<b>% of Act Mems w/Logins on App</b></font></td>");
   out.println("<td align=\"center\"><font size=\"2\">");
   out.println("<b>Logins Per App Mem</b></font></td>");
   out.println("</tr>");
   out.println("</table>");
   
   out.println("<font size=\"3\"><br><strong>Total Number of Clubs Live with the APP: " +app_clubs_live+ "</strong></font>");
   
   out.close();    

 }


 // *********************************************************
 //   Do Caller report
 // *********************************************************
 private void doCallerReport(String user, String club, String fullname, PrintWriter out, Connection con) {

   Statement stmt = null;
   ResultSet rs = null;
   
   
   String caller = "";
   String email = "";
   int seamless = 0;
   int mobile = 0;
   int rsync = 0;
   
   try {
    
      stmt = con.createStatement();           

      rs = stmt.executeQuery("SELECT email, rsync, seamless, seamless_caller, allow_mobile FROM club5");

      if (rs.next()) {

         email = rs.getString(1);         
         rsync = rs.getInt(2);         
         seamless = rs.getInt(3);         
         caller = rs.getString(4);         
         mobile = rs.getInt(5);         
         
         out.println("<tr>");
         out.println("<td align=\"left\"><font size=\"2\">");
         out.println(fullname + "</font></td>");
         out.println("<td align=\"left\"><font size=\"2\">");
         out.println(club + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(caller + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         if (seamless == 0) {
            out.println("No</font></td>");
         } else {
            out.println("Yes</font></td>");
         }
         out.println("<td align=\"center\"><font size=\"2\">");
         if (rsync == 0) {
            out.println("No</font></td>");
         } else {
            out.println("Yes</font></td>");
         }
         out.println("<td align=\"center\"><font size=\"2\">");
         if (mobile == 0) {
            out.println("No</font></td>");
         } else {
            out.println("Yes</font></td>");
         }
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(email + "</font></td>");
         out.println("</tr>");      
      }
      
      /*
      //
      //  Now set the mobile flag based on seamless
      //
      if (caller.equals("") && seamless == 0 && rsync == 0) {
         
         stmt.executeUpdate("UPDATE club5 SET allow_mobile = 1");
         
      }
       */
    
      stmt.close();
      
   }
   catch (Exception exc) {
      out.println("<BR><BR><H1>Database Access Error</H1>");
      out.println("<BR><BR>Error getting counts for " +club+ ".");
      out.println("<BR><BR>Exception: " + exc.getMessage());
        if (user.equals("support")) {
            out.println("<BR><BR><a href=\"/" +rev+ "/support_main2.htm\">Return</a>");
        } else {
            out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Support_sales_main\">Return</a>");             
        }
   }

 }


 // *********************************************************
 // Check for illegal access by user
 // *********************************************************

 private HttpSession verifyUser(HttpServletRequest verreq, PrintWriter out) {

   HttpSession session = null;

   String support = "support";
   String sales = "sales";

   //
   // Make sure user didn't enter illegally
   //
   session = verreq.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      invalidUser(out);            // Intruder - reject

   } else {

      String user = (String)session.getAttribute("user");   // get username

      if (!user.equalsIgnoreCase( support ) && !user.startsWith( sales )) {

         invalidUser(out);            // Intruder - reject
         session = null;
      }
   }
   return session;
 }
    
 
 // *********************************************************
 // Illegal access by user - force user to login....
 // *********************************************************

 private void invalidUser(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
   out.println("<BODY><CENTER>");
   out.println("<BR><H2>Access Error</H2><BR>");
   out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
   out.println("</CENTER></BODY></HTML>");

 }

 // *********************************************************
 // Database Error
 // *********************************************************

 private void dbError(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Database Error"));
   out.println("<BODY><CENTER>");
   out.println("<BR><BR><H3>Database Access Error</H3>");
   out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
   out.println("<BR>Please try again later.");
   out.println("<BR><BR>If problem persists, contact customer support.");
   out.println("</CENTER></BODY></HTML>");

 }

}
