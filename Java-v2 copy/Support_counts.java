/***************************************************************************************
 *   Support_counts:  This servlet will display the login counts for each club.
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


public class Support_counts extends HttpServlet {

 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


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

}
