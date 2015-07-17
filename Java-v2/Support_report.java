/***************************************************************************************     
 *   Support_report:  This servlet will process the 'Generate Reports' request from
 *                    Support's main page.
 *
 *
 *   called by:  servlet/Support_main (calls doGet)
 *               self (calls doPost)
 *
 *   created: 1/31/2002   Bob P.
 *
 *   last updated:
 *
 *          4/24/08  Update Connection object to use SystemUtils.getCon()
 *          7/18/03  Enhancements for Version 3 of the software.
 *          9/18/02  Enhancements for Version 2 of the software.
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

import com.foretees.common.Connect;

public class Support_report extends HttpServlet {
                
 String omit = "";

 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 //*******************************************************
 // Process the initial request from servlet/Support_main page
 //
 //    also, doPost comes here!!!
 //*******************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Statement stmt = null;
   ResultSet rs = null;

   String fname = "";
   String lname = "";
   String mname = "";
   String i_fname = "";
   String i_lname = "";
   String i_mname = "";
   String user = "";
   String username = "";
 
   HttpSession sess = verifyUser(req, out);       // check for intruder

   if (sess == null) {

      return;
   }

   Connection con = Connect.getCon(req);           // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Support_main\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   int all = 0;
   int only = 0;
   int ind = 0;
   int tcount = 0;
   int icount = 0;
   int count = 0;
   int mem_count = 0;
   int use_count = 0;

   //
   // Get the parameters entered (if from doPost)
   //
   if (req.getParameter("all") != null) {              // all members, itemized              

      all = 1;
   }
   if (req.getParameter("only") != null) {              // all members, non-zero counts

      only = 1;
   }
   if (req.getParameter("ind") != null) {              // one member only

      ind = 1;
      username = req.getParameter("username");         //  username of member to count
   }

   if ((all == 1) || (only == 1)) {                  // if doPost and long output - build separate page

      //
      //  Build the HTML page to prompt Support for report options
      //
      out.println(SystemUtils.HeadTitle("Support Reports Page"));

      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");


      out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");

      out.println("<br>");
      out.println("</font>");
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"Support_report\">");
      out.println("<input type=\"submit\" value=\"Return to Reports\" style=\"text-decoration:underline; background:#CC9966\">");
      out.println("</form>");
      out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Support_main\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#CC9966\">");
      out.println("</form></font>");
      out.println("<br>");

      out.println("<table border=\"1\" bgcolor=\"#FFFFCC\" cellpadding=\"8\" width=\"450\">");
      out.println("<form action=\"Support_report\" method=\"post\">");

         out.println("<tr bgcolor=\"#CC9966\"><td align=\"center\" colspan=\"2\">");
            out.println("<font size=\"3\">");
            out.println("<b>Member Access Report</b></font></td></tr>");

         out.println("<tr bgcolor=\"#CC9966\"><td align=\"left\">");
            out.println("<font size=\"2\">");
            out.println("Member Name</td>");
            out.println("<td align=\"center\"><font size=\"2\">");
            out.println("Times Accessed System</td>");
            out.println("</font></td></tr>");

      count = 0;
      tcount = 0;
      mem_count = 0;

      try {

         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT name_last, name_first, name_mi, count FROM member2b " +
                                "ORDER BY name_last, name_first, name_mi");

         while (rs.next()) {

            lname = rs.getString(1);
            fname = rs.getString(2);
            mname = rs.getString(3);
            count = rs.getInt(4);
               

            if ((count != 0) || (all == 1)) {                  // skip if usage = 0 and req was 'only'

               tcount = tcount + count;                        // keep total count of usage
               mem_count++;                                    // count members being totaled

                  out.println("<tr><td align=\"left\">");
                  out.println("<font size=\"2\">");
                  out.println( lname + ", " + fname + " " + mname );
                  out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println( count + "</td></tr>");
            }
         }

         stmt.close();
            
         out.println("<tr><td align=\"right\">");
         out.println("<font size=\"2\">");
           
         if (all == 1) {
           
            out.println("Total for all <b>" + mem_count + "</b> members:&nbsp;&nbsp;");
         } else {
            
            out.println("Total for <b>" + mem_count + "</b> members:&nbsp;&nbsp;");
         }
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         out.println("<b>" + tcount + "</b></td></tr>");

      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H1>Database Access Error</H1>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Support_main\">Return</a>");
         out.println("</CENTER></BODY></HTML>");
         return;

      }   // end of member name list

      out.println("</font>");
      out.println("</table>");
        
      out.println("<br>");
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"Support_report\">");
      out.println("<input type=\"submit\" value=\"Return to Reports\" style=\"text-decoration:underline; background:#CC9966\">");
      out.println("</form>");
      out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Support_main\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#CC9966\">");
      out.println("</form></font>");

      out.println("</center></font></body></html>");


   } else {        // doGet or short output for doPost
      //
      //  Build the HTML page to prompt Support for report options
      //
      out.println(SystemUtils.HeadTitle("Support Reports Page"));

      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");

      out.println("<br><b>Generate Access Reports</b><br>");
      out.println("</font>");
      out.println("<font size=\"2\"><br>");

      out.println("<table border=\"1\" bgcolor=\"#FFFFCC\" cellpadding=\"5\" width=\"450\">");
      out.println("<form action=\"Support_report\" method=\"post\">");
      out.println("<tr><td align=\"center\" bgcolor=\"#CC9966\">");
      out.println("<font size=\"2\">");
      out.println("Use this report generator to determine who<br>");
      out.println("is using the system, and how often.<br>");
      out.println("</font></td></tr>");

      out.println("<tr><td><br>");
      out.println("<font size=\"2\">");
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Stats for individual member&nbsp;&nbsp;");
      out.println("<select size=\"1\" name=\"username\">&nbsp;&nbsp;");

      //
      // Get a list of members
      //
      try {

         count = 0;
         tcount = 0;
         icount = 0;
         mem_count = 0;
         use_count = 0;
           
         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT username, name_last, name_first, name_mi, count FROM member2b " +
                                "ORDER BY name_last, name_first, name_mi");

         while (rs.next()) {

            user = rs.getString(1);
            lname = rs.getString(2);
            fname = rs.getString(3);
            mname = rs.getString(4);
            count = rs.getInt(5);

            tcount = tcount + count;       // keep total count
            mem_count++;                   // count members

            out.println("<option value=" + user + ">" + lname + ", " + fname + " " + mname + "</option>");
              
            if ((ind == 1) && (user.equals( username ))) {
             
               icount = count;        // save count and name for this member
               i_lname = lname;
               i_fname = fname;
               i_mname = mname;
            }
            if (count != 0) {
               
               use_count++;          // bump number of members that have used the system at least once
            }
         }

         stmt.close();

      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H1>Database Access Error</H1>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Support_main\">Return</a>");
         out.println("</CENTER></BODY></HTML>");
         return;

      }   // end of member name list

      out.println("</select>");
      out.println("<input type=\"submit\" name=\"ind\" value=\"Go\"><br><br>");

      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Stats for all <b>" + mem_count + "</b> members, itemized&nbsp;&nbsp;");
      out.println("<input type=\"submit\" name=\"all\" value=\"Go\"><br><br>");

      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Stats for all members with non-zero counts&nbsp;&nbsp;");
      out.println("<input type=\"submit\" name=\"only\" value=\"Go\">");

      out.println("</font>");
      out.println("</form>");
      out.println("</td></tr>");

      out.println("<tr><td align=\"center\"><font size=\"2\"><br>");
      out.println("<b>" + use_count + " members</b> have accessed this system a total of <b>" + tcount + "</b> times.<br>");
      out.println("<br></font></td></tr>");

      if (ind == 1) {            // if individual member display requested

         out.println("<tr><td align=\"center\"><font size=\"2\"><br>");
         out.println("<b>" + i_fname + " " + i_mname + " " + i_lname + "</b> has accessed this system <b>" + icount + "</b> times.<br>");
         out.println("<br></font></td></tr>");
      }

      out.println("</table>");

      out.println("<br><br></font>");
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Support_main\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#CC9966\">");
      out.println("</form></font>");
      out.println("</center></body></html>");
 
   }  // end of if itemized request 

 }  // end of doGet

                               
 //****************************************************
 // Process the form request from doGet above (use doGet)
 //****************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   doGet(req, resp);     // call doGet processing
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
   out.println("<a href=\"/" +rev+ "/servlet/Support_main\">Return</a>");
   out.println("</CENTER></BODY></HTML>");

 }

 // *********************************************************
 // Missing or invalid data entered...
 // *********************************************************

 private void invData(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, some data is missing or invalid.<BR>");
   out.println("<BR>You must select at least one option.<BR>");
   out.println("If you select 'Individual Member', then you must also select the member name.<BR>");
   out.println("<BR>Please try again.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#CC9966\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
 }

 // *********************************************************
 // Member does not exists
 // *********************************************************

 private void noMem(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, the member you specified does not exist in the database.<BR>");
   out.println("<BR>Please check your data and try again.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#CC9966\">");
   out.println("</form></font>");
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
   out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Support_main\">Return</a>");
   out.println("</CENTER></BODY></HTML>");

 }

}
