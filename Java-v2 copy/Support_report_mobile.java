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

      //
      //  Build Mobile Login report
      //
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<table border=\"1\" cellpadding=\"5\">");
      out.println("<tr><td align=\"center\" colspan=\"6\">");
      out.println("<font size=\"2\">");
      out.println("<b>All Clubs Listed Have Mobile Enabled</b></font></td></tr>");
      
      out.println("<tr><td align=\"left\">");
      out.println("<font size=\"2\">");
      out.println("<b>Club Name</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Site Name</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b># Mobile Mems</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b># of Mobile Logins</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b># of iPhone Logins</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Total Logins</b></font></td>");
      out.println("</tr>");


      //
      //  Get each club in the system and get its parms
      //
      try {

         stmt = con2.createStatement();           

         rs = stmt.executeQuery("SELECT clubname, fullname FROM clubs ORDER BY clubname");      // get club names from V5 db

         while (rs.next()) {

            club = rs.getString(1);             // get a club name
            fullname = rs.getString(2);         // get the full name

            //  weed out the demo clubs, etc.

            boolean skip = false;

            if (club.startsWith("demo") || club.startsWith("mfirst") || club.startsWith("testJonas") || 
                 club.startsWith("notify") || club.startsWith("test") || club.equals("admiralscove2") ) {   

               skip = true;      // skip it
            }

            if (skip == false) {

               con = dbConn.Connect(club);          // get con to this club

               doMobileReport(club, fullname, out, con);      

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
         out.println("<BR><BR><a href=\"/" +rev+ "/support_main2.htm\">Return</a>");
         return;

      }   // end of member name list

      out.println("</table>");


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

         rs = stmt.executeQuery("SELECT clubname, fullname FROM clubs ORDER BY clubname");      // get club names from V5 db

         while (rs.next()) {

            club = rs.getString(1);             // get a club name
            fullname = rs.getString(2);         // get the full name

            //  weed out the demo clubs, etc.

            boolean skip = false;

            if (club.startsWith("demo") || club.startsWith("mfirst") || club.startsWith("testJonas") || 
                 club.startsWith("notify") || club.startsWith("test") || club.equals("admiralscove2") ) {   

               skip = true;      // skip it
            }

            if (skip == false) {

               con = dbConn.Connect(club);          // get con to this club

               doCallerReport(club, fullname, out, con);      

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
         out.println("<BR><BR><a href=\"/" +rev+ "/support_main2.htm\">Return</a>");
         return;

      }   // end of member name list

      out.println("</table>");
   }  
      
 }  // end of doGet
 
 
 
 // *********************************************************
 //   Do mobile report
 // *********************************************************
 private void doMobileReport(String club, String fullname, PrintWriter out, Connection con) {

   Statement stmt = null;
   ResultSet rs = null;
   
   
   String course = "";
   int allow = 0;
   int count = 0;
   int members = 0;
   int mobile = 0;
   int iphone = 0;
   
   try {
    
      stmt = con.createStatement();           

      rs = stmt.executeQuery("SELECT allow_mobile FROM club5");

      if (rs.next()) {

         allow = rs.getInt(1);         
      }
      
      if (allow > 0) {        // if Mobile allowed for this club
      
         rs = stmt.executeQuery("SELECT COUNT(*) FROM member2b WHERE mobile_count > 0");    // get # of members that have logged into the Mobile site

         if (rs.next()) {

            members = rs.getInt(1);         
         }


         rs = stmt.executeQuery("SELECT SUM(count), SUM(mobile_count), SUM(mobile_iphone) FROM member2b");

         if (rs.next()) {

            count = rs.getInt(1);         
            mobile = rs.getInt(2);         
            iphone = rs.getInt(3);         

           // if (mobile > 0) {

               out.println("<tr><td align=\"left\">");
               out.println("<font size=\"2\">");
               out.println(fullname + "</font></td>");
               out.println("<td align=\"center\"><font size=\"2\">");
               out.println(club + "</font></td>");
               out.println("<td align=\"center\"><font size=\"2\">");
               out.println(members + "</font></td>");
               out.println("<td align=\"center\"><font size=\"2\">");
               out.println(mobile + "</font></td>");
               out.println("<td align=\"center\"><font size=\"2\">");
               out.println(iphone + "</font></td>");
               out.println("<td align=\"center\"><font size=\"2\">");
               out.println(count + "</font></td>");
               out.println("</tr>");      
           // }
         }
      }
      
      stmt.close();
    
   }
   catch (Exception exc) {
      out.println("<BR><BR><H1>Database Access Error</H1>");
      out.println("<BR><BR>Error getting counts for " +club+ ".");
      out.println("<BR><BR>Exception: " + exc.getMessage());
      out.println("<BR><BR><a href=\"/" +rev+ "/support_main2.htm\">Return</a>");
   }

 }


 // *********************************************************
 //   Do Caller report
 // *********************************************************
 private void doCallerReport(String club, String fullname, PrintWriter out, Connection con) {

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
      out.println("<BR><BR><a href=\"/" +rev+ "/support_main2.htm\">Return</a>");
   }

 }


 // *********************************************************
 // Check for illegal access by user
 // *********************************************************

 private HttpSession verifyUser(HttpServletRequest verreq, PrintWriter out) {

   HttpSession session = null;

   String support = "support";

   //
   // Make sure user didn't enter illegally
   //
   session = verreq.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      invalidUser(out);            // Intruder - reject

   } else {

      String user = (String)session.getAttribute("user");   // get username

      if (!user.equalsIgnoreCase( support )) {

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
   out.println("<BR><BR>");
   out.println("<a href=\"/" +rev+ "/support_main2.htm\">Return</a>");
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
   out.println("<BR><BR><a href=\"/" +rev+ "/support_main2.htm\">Return</a>");
   out.println("</CENTER></BODY></HTML>");

 }

}
