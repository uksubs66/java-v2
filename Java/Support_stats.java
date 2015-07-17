/***************************************************************************************
 *   Support_stats:  This class will display the number of logins and tee sheets displayed
 *                   since the server was last bounced.
 *
 *
 *   called by:  support_main2.htm and sales_main.htm
 *
 *   created: 5/01/2005   Bob P.
 *
 *   last updated:
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import java.text.NumberFormat;


public class Support_stats extends HttpServlet {

    
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
  
 
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   NumberFormat nf = NumberFormat.getNumberInstance();

   int count = 0;
   int total1 = 0;
   int total2 = 0;
   int total3 = 0;
   int total4 = 0;

   HttpSession session = null;

   //
   // Make sure user didn't enter illegally
   //
   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   String user = (String)session.getAttribute("user");   // get username

   if (!user.equals( "support" ) && !user.startsWith( "sales" )) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   //
   //  Check if call is for Age Counts
   //
   if (req.getParameter("agereport") != null) {

      dispAgeReport(out, user);
      return;
   }

   //
   //   Display the counts
   //
   out.println("<HTML><HEAD><TITLE>Support Display Stats</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Login and Tee Sheet Counts</H3>");
     
   if (user.startsWith( "sales" )) {
      out.println("<A HREF=\"/" +rev+ "/sales_main.htm\">Return</A><BR><BR>");
   } else {
      out.println("<A HREF=\"/" +rev+ "/support_main2.htm\">Return</A><BR><BR>");
   }
     
   out.println("First Login Received (after last server bounce): <b>" +SystemUtils.startDate+ "</b>");

   out.println("<BR><BR>");

   out.println("<Table border=\"1\" cellpadding=\"2\" cellspacing=\"2\" align=\"center\" bgcolor=\"#F5F5DC\">");
   out.println("<tr bgcolor=\"#336633\"><td>");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("<p><b>Hour of Day</b></p></font></td>");
   out.println("<td><font color=\"#FFFFFF\" size=\"2\"><p><b>Pro Logins</b></p></font></td>");
   out.println("<td><font color=\"#FFFFFF\" size=\"2\"><p><b>Mem Logins</b></p></font></td>");
   out.println("<td><font color=\"#FFFFFF\" size=\"2\"><p><b>Pro Tee Sheets</b></p></font></td>");
   out.println("<td><font color=\"#FFFFFF\" size=\"2\"><p><b>Mem Tee Sheets</b></p></font></td>");
   out.println("</td></tr>");

   for (int i = 0; i < 24; i++) {          // do every hour of day
     
      out.println("<tr>");
      out.println("<td><font size=\"2\">" + i + "</font></td>");
      out.println("<td><font size=\"2\">" + nf.format(SystemUtils.loginCountsPro[i]) + "</font></td>");
         total1+= SystemUtils.loginCountsPro[i];
      out.println("<td><font size=\"2\">" + nf.format(SystemUtils.loginCountsMem[i]) + "</font></td>");
         total2+= SystemUtils.loginCountsMem[i];
      out.println("<td><font size=\"2\">" + nf.format(SystemUtils.sheetCountsPro[i]) + "</font></td>");
         total3+= SystemUtils.sheetCountsPro[i];
      out.println("<td><font size=\"2\">" + nf.format(SystemUtils.sheetCountsMem[i]) + "</font></td>");
         total4+= SystemUtils.sheetCountsMem[i];
      out.println("</td></tr>");
   }

   out.println("<tr>");
   out.println("<td><font size=\"2\"><b>Totals:</b></font></td>");
   out.println("<td><font size=\"2\"><b>" + nf.format(total1) + "</b></font></td>");
   out.println("<td><font size=\"2\"><b>" + nf.format(total2) + "</b></font></td>");
   out.println("<td><font size=\"2\"><b>" + nf.format(total3) + "</b></font></td>");
   out.println("<td><font size=\"2\"><b>" + nf.format(total4) + "</b></font></td>");
   out.println("</td></tr>");

   out.println("</TABLE>");
     
   if (user.startsWith( "sales" )) {
      out.println("<BR><A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>");
   } else {
      out.println("<BR><A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
   }
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 // *********************************************************
 //  Display an Age Report - # of logins per age group
 // *********************************************************

 private void dispAgeReport(PrintWriter out, String user) {

   Connection con = null;                 // init DB objects
   Connection con2 = null;
   Statement stmt = null;
   Statement stmt2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   String club = "";

   int count = 0;
   int clubCount = 0;
   int memCount = 0;
   int birth = 0;
   int grandTotal = 0;
   int total1 = 0;   // < 40
   int total2 = 0;   // 40 - 44
   int total3 = 0;   // 45 - 49
   int total4 = 0;   // 50 - 54
   int total5 = 0;   // 55 - 59
   int total6 = 0;   // 60 - 64
   int total7 = 0;   // 65 - 69
   int total8 = 0;   // 70 +

   int date = 0;    // today  
   int date1 = 0;   // -40 yrs
   int date2 = 0;   // -45 yrs
   int date3 = 0;   // -50 yrs
   int date4 = 0;   // -55 yrs
   int date5 = 0;   // -60 yrs
   int date6 = 0;   // -65 yrs
   int date7 = 0;   // -70 yrs

   //
   //  Get today's date and caluclate birth date values
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   int year = cal.get(Calendar.YEAR);
   int month = cal.get(Calendar.MONTH) + 1;
   int day = cal.get(Calendar.DAY_OF_MONTH);

   date = (year * 10000) + (month * 100) + day;   // create a date field of yyyymmdd

   date1 = date - 400000;
   date2 = date - 450000;
   date3 = date - 500000;
   date4 = date - 550000;
   date5 = date - 600000;
   date6 = date - 650000;
   date7 = date - 700000;

   //
   // Load the JDBC Driver and connect to DB
   //
   club = rev;

   try {

      con2 = dbConn.Connect(club);
   }
   catch (Exception exc) {

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
      if (user.startsWith( "sales" )) {
         out.println("<BR><BR> <A HREF=\"/v5/sales_main.htm\">Return</A>.");
      } else {
         out.println("<BR><BR> <A HREF=\"/v5/support_main2.htm\">Return</A>.");
      }
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //   Get each club's login count and total them
   //
   try {
      stmt2 = con2.createStatement();              // create a statement

      rs2 = stmt2.executeQuery("SELECT clubname FROM clubs ORDER BY clubname");

      while (rs2.next()) {

         club = rs2.getString(1);                 // get a club name

         clubCount++;                             // number of clubs

         con = dbConn.Connect(club);         // get a connection to this club's db

         stmt = con.createStatement();           // create a statement

         try {
            rs = stmt.executeQuery("SELECT count, birth FROM member2b WHERE count > 0 AND birth > 0");  // get # of logins

            while (rs.next()) {

               count = rs.getInt(1);
               birth = rs.getInt(2);
                 
               memCount++;              // # of members counted
                 
               if (birth > date1) {                                      // if under 40
                 
                  total1 += count;           // add to total for this age group
               }
               if (birth <= date1 && birth > date2) {                      // 40 - 44

                  total2 += count;           // add to total for this age group
               }
               if (birth <= date2 && birth > date3) {                      // 45 - 49

                  total3 += count;           // add to total for this age group
               }
               if (birth <= date3 && birth > date4) {                      // 50 - 54

                  total4 += count;           // add to total for this age group
               }
               if (birth <= date4 && birth > date5) {                      // 55 - 59

                  total5 += count;           // add to total for this age group
               }
               if (birth <= date5 && birth > date6) {                      // 60 - 64

                  total6 += count;           // add to total for this age group
               }
               if (birth <= date6 && birth > date7) {                      // 65 - 69

                  total7 += count;           // add to total for this age group
               }
               if (birth <= date7) {                                       // 70 +

                  total8 += count;           // add to total for this age group
               }
            }

         }
         catch (Exception ignore) {
         }

         stmt.close();

         con.close();                           // close the connection to the club db
      }                                         // do all clubs
      stmt2.close();
      con2.close();
   }
   catch (Exception ignore) {
   }

   grandTotal = total1 + total2 + total3 + total4 + total5 + total6 + total7 + total8;   // Grand Total 

   //
   //  Display the results
   //
   out.println("<HTML><HEAD><TITLE>Support Display Stats</TITLE></HEAD>");
   out.println("<BODY><CENTER><BR><BR><H3>Login Counts For All Clubs By Age</H3><BR>");

   out.println(+memCount+ " Members counted from " +clubCount+ " clubs.");
   out.println("<BR><BR>");

   out.println("<Table border=\"1\" cellpadding=\"2\" cellspacing=\"2\" align=\"center\" bgcolor=\"#F5F5DC\">");
   out.println("<tr bgcolor=\"#336633\">");
   out.println("<td><font color=\"#FFFFFF\" size=\"2\"><p><b>Age Group</b></p></font></td>");
   out.println("<td><font color=\"#FFFFFF\" size=\"2\"><p><b>Number of Logins</b></p></font></td>");
   out.println("<td><font color=\"#FFFFFF\" size=\"2\"><p><b>Percentage</b></p></font></td>");
   out.println("</tr>");

   out.println("<tr>");
   out.println("<td><font size=\"2\">Under 40</font></td>");
   out.println("<td align=\"center\"><font size=\"2\">" +total1+ "</font></td>");
   out.println("<td align=\"center\"><font size=\"2\">" +(total1 * 100) / grandTotal+ "%</font></td>");
   out.println("</td></tr>");

   out.println("<tr>");
   out.println("<td><font size=\"2\">41 to 44</font></td>");
   out.println("<td align=\"center\"><font size=\"2\">" +total2+ "</font></td>");
   out.println("<td align=\"center\"><font size=\"2\">" +(total2 * 100) / grandTotal+ "%</font></td>");
   out.println("</td></tr>");

   out.println("<tr>");
   out.println("<td><font size=\"2\">45 to 49</font></td>");
   out.println("<td align=\"center\"><font size=\"2\">" +total3+ "</font></td>");
   out.println("<td align=\"center\"><font size=\"2\">" +(total3 * 100) / grandTotal+ "%</font></td>");
   out.println("</td></tr>");

   out.println("<tr>");
   out.println("<td><font size=\"2\">50 to 54</font></td>");
   out.println("<td align=\"center\"><font size=\"2\">" +total4+ "</font></td>");
   out.println("<td align=\"center\"><font size=\"2\">" +(total4 * 100) / grandTotal+ "%</font></td>");
   out.println("</td></tr>");

   out.println("<tr>");
   out.println("<td><font size=\"2\">55 to 59</font></td>");
   out.println("<td align=\"center\"><font size=\"2\">" +total5+ "</font></td>");
   out.println("<td align=\"center\"><font size=\"2\">" +(total5 * 100) / grandTotal+ "%</font></td>");
   out.println("</td></tr>");

   out.println("<tr>");
   out.println("<td><font size=\"2\">60 to 64</font></td>");
   out.println("<td align=\"center\"><font size=\"2\">" +total6+ "</font></td>");
   out.println("<td align=\"center\"><font size=\"2\">" +(total6 * 100) / grandTotal+ "%</font></td>");
   out.println("</td></tr>");

   out.println("<tr>");
   out.println("<td><font size=\"2\">65 to 69</font></td>");
   out.println("<td align=\"center\"><font size=\"2\">" +total7+ "</font></td>");
   out.println("<td align=\"center\"><font size=\"2\">" +(total7 * 100) / grandTotal+ "%</font></td>");
   out.println("</td></tr>");

   out.println("<tr>");
   out.println("<td><font size=\"2\">70 and over</font></td>");
   out.println("<td align=\"center\"><font size=\"2\">" +total8+ "</font></td>");
   out.println("<td align=\"center\"><font size=\"2\">" +(total8 * 100) / grandTotal+ "%</font></td>");
   out.println("</td></tr>");

   out.println("<tr>");
   out.println("<td><font size=\"2\"><b>Total Logins</b></font></td>");
   out.println("<td align=\"center\"><font size=\"2\"><b>" +grandTotal+ "</b></font></td>");
   out.println("<td align=\"center\"><font size=\"2\">&nbsp;</font></td>");
   out.println("</td></tr>");

   out.println("</TABLE>");

   if (user.startsWith( "sales" )) {
      out.println("<BR><A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>");
   } else {
      out.println("<BR><A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
   }
   out.println("</CENTER></BODY></HTML>");
   out.close();

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
   out.println("<BR><BR> <FORM>");
   out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'></INPUT>");
   out.println("</FORM></CENTER></BODY></HTML>");

 }

}
