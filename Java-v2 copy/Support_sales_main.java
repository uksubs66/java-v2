/***************************************************************************************     
 *   Support_sales_main:  This servlet will provide the main menu for sales and allow user to switch clubs.
 *
 *
 *   called by:  Support_sales_main.htm
 *
 *   created:  1/12/2013   BP
 *
 *   last updated:
 *
 *    1/17/13  Updated body tag with colors to match old page styles.
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

// foretees imports
import com.foretees.common.getClub;
import com.foretees.common.mTypeArrays;
import com.foretees.common.ProcessConstants;
import com.foretees.common.Utilities;


public class Support_sales_main extends HttpServlet {
 
       
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
 String support = "support";             // valid username
 String sales = "sales";



 //*********************************************************************************
 // Process the request from menu 
 //*********************************************************************************

 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
        
   Connection con = null;                  // init DB objects
     
   HttpSession session = null; 
   String itype = "";                      // invoice type

   
   // Make sure user didn't enter illegally.........

   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   String user = (String)session.getAttribute("user");   // get username

   if (!user.startsWith( sales )) {

      invalidUser(out);            // Intruder - reject
      return;
   }


   // Load the JDBC Driver and connect to DB.........

   String club = (String)session.getAttribute("club");   // get club name
   
   
   //
   //  See if user requested that we switch the club to process
   //
   if (req.getParameter("clubswitch") != null && req.getParameter("clubswitch").equals("1") && req.getParameter("club") != null) {

       //
       //  Request is to switch clubs - switch the db (TPC or Demo sites)
       //
       String newClub = req.getParameter("club");

       session.setAttribute("club", newClub);
       
       out.println("<HTML><HEAD><Title>Switching Sites</Title>");
       out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" +rev+ "/servlet/Support_sales_main?home=yes\">");
       out.println("</HEAD>");
       out.println("<BODY><CENTER><BR>");
       out.println("<BR><H2>Switching Sites</H2><BR>");
       out.println("<a href=\"/" +rev+ "/servlet/Support_sales_main?home=yes\">Continue</a><br>");
       out.println("</CENTER></BODY></HTML>");
       out.close();
       return;
   }
   
      
   //  Process Home page
   
    out.println("<HTML><HEAD><TITLE>Sales Main Menu</TITLE></HEAD>");
    out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#FFFFFF\" link=\"#000000\" vlink=\"#000000\" alink=\"#FF0000\">");
    out.println("<span style=\"color:black;\">");
    out.println("<CENTER><H3>Sales Main Menu</H3>");
    out.println("Current Club = " +club);

    out.println("<p>");    //  add drop down list of all clubs - for switch

    out.println("<form action=\"/" +rev+ "/servlet/Support_sales_main\" method=\"get\" name=\"cform\">");
    out.println("<input type=\"hidden\" name=\"clubswitch\" value=\"1\">");

    out.println("To switch sites, select the desired club name below.<br>");
    out.println("<br><b>Club:</b>&nbsp;&nbsp;");
    out.println("</span>");

    out.println("<select size=\"1\" name=\"club\" onChange=\"document.cform.submit()\">");

    String clubname = "";
    String fullname = "";

    Connection con2 = null;

    try {
        con2 = dbConn.Connect(rev);           // get connection to the Vx db

        //
        //  Get the club names for each club
        //
        //
        //  Get the club names for each TPC club
        //
        PreparedStatement pstmt = con2.prepareStatement("SELECT clubname, fullname FROM clubs WHERE inactive=0 ORDER BY fullname");

        pstmt.clearParameters();
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {

            clubname = rs.getString("clubname");             // get the club's site name             
            fullname = rs.getString("fullname");             // get the club's full name

            if (clubname.equals(club)) {
                out.println("<option selected value=\"" + clubname + "\">" + fullname + " (" +clubname+ ")</option>");    
            } else {
                out.println("<option value=\"" + clubname + "\">" + fullname + " (" +clubname+ ")</option>"); 
            }
        }  
        pstmt.close();

    }
    catch (Exception e) {
        
    } finally {

        try { con2.close(); }
        catch (SQLException ignored) {}
    }          

    out.println("</select><br /></p></form>");

    
    out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" width=\"450\">");
    out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
    out.println("<tr bgcolor=\"#336633\">");
        out.println("<td>");
                out.println("<p align=\"center\">");
                out.println("<b>Sales Menu</b>");
                out.println("</p>");
        out.println("</td>");
    out.println("</tr>");
    out.println("<tr>");
        out.println("<td>");
                out.println("<p align=\"center\">");
                out.println("<a href=\"/v5/servlet/Support_billing\" target=\"_top\">Get Billing Counts for Club</a>");
                out.println("</p>");
        out.println("</td>");
    out.println("</tr>");
    out.println("<tr>");
        out.println("<td>");
                out.println("<p align=\"center\">");
                out.println("<a href=\"/v5/servlet/Support_invoicing?home=yes\" target=\"_top\">Invoice Processing (Test Mode)</a>");
                out.println("</p>");
        out.println("</td>");
    out.println("</tr>");
    out.println("<tr>");
        out.println("<td>");
                out.println("<p align=\"center\">");
                out.println("<a href=\"/v5/servlet/Support_styles\" target=\"_top\">Add/Change Custom Style Template</a>");
                out.println("</p>");
        out.println("</td>");
    out.println("</tr>");
    out.println("<tr>");
        out.println("<td>");
                out.println("<p align=\"center\">");
                out.println("<a href=\"/v5/servlet/Support_time?login=yes\" target=\"_top\">Display Number of Users Logged In</a>");
                out.println("</p>");
        out.println("</td>");
    out.println("</tr>");
    out.println("<tr>");
        out.println("<td>");
                out.println("<p align=\"center\">");
                out.println("<a href=\"/v5/servlet/Support_counts\" target=\"_top\">Display Login Counts For All Clubs</a>");
                out.println("</p>");
        out.println("</td>");
    out.println("</tr>");
    out.println("<tr>");
        out.println("<td>");
                out.println("<p align=\"center\">");
                out.println("<a href=\"/v5/servlet/Support_tcounts\" target=\"_top\">Display Tee Time Counts For All Clubs</a>");
                out.println("</p>");
        out.println("</td>");
    out.println("</tr>");
    out.println("<tr>");
    out.println("    <td>");
    out.println("            <p align=\"center\">");
    out.println("            <a href=\"/v5/servlet/Support_report_allparms\" target=\"_top\">Display System Settings For One or All Clubs</a>");
    out.println("            </p>");
    out.println("    </td>");
    out.println("</tr>");
    out.println("<tr>");
    out.println("    <td>");
    out.println("            <p align=\"center\">");
    out.println("            <a href=\"/v5/servlet/Support_stats\" target=\"_top\">Display Login & Tee Sheet Counts</a>");
    out.println("            </p>");
    out.println("    </td>");
    out.println("</tr>");
    out.println("<tr>");
    out.println("    <td>");
    out.println("            <p align=\"center\">");
    out.println("            <a href=\"/v5/servlet/Support_stats?agereport=yes\" target=\"_top\">Display Login Counts By Age</a>");
    out.println("            </p>");
    out.println("    </td>");
    out.println("</tr>");
    out.println("<tr>");
    out.println("    <td>");
    out.println("            <p align=\"center\">");
    out.println("            <a href=\"/v5/servlet/Support_display?table=login\" target=\"_blank\">Display Login Table</a>");
    out.println("            </p>");
    out.println("    </td>");
    out.println("</tr>");
    out.println("<tr>");
    out.println("    <td>");
    out.println("            <p align=\"center\">");
    out.println("            <a href=\"/v5/servlet/Support_emails\" target=\"_top\">Enable/Disable Email Notifications</a>");
    out.println("            </p>");
    out.println("    </td>");
    out.println("</tr>");
    out.println("<tr>");
    out.println("    <td>");
    out.println("            <p align=\"center\">");
    out.println("            <a href=\"/v5/servlet/Support_tmode\" target=\"_top\">Set Default Modes of Trans For All Members</a>");
    out.println("            </p>");
    out.println("    </td>");
    out.println("</tr>");
    out.println("<tr>");
    out.println("    <td>");
    out.println("            <p align=\"center\">");
    out.println("            <a href=\"/v5/servlet/Support_sync?rtype=active\" target=\"_top\">Set All Members Active For This Club</a>");
    out.println("            </p>");
    out.println("    </td>");
    out.println("</tr>");
    out.println("<tr>");
    out.println("    <td>");
    out.println("            <p align=\"center\">");
    out.println("            <a href=\"/v5/servlet/Support_courses?default\" target=\"_top\">Default Course Configuration</a>");
    out.println("            </p>");
    out.println("    </td>");
    out.println("</tr>");
    out.println("<tr>");
    out.println("    <td>");
    out.println("            <p align=\"center\">");
    out.println("            <a href=\"/v5/servlet/Support_setCaddyAvail\" target=\"_top\">Set All Caddies Available For This Club (demo/notify only)</a>");
    out.println("            </p>");
    out.println("    </td>");
    out.println("</tr>");
    out.println("<tr>");
    out.println("    <td>");
    out.println("            <p align=\"center\">");
    out.println("            <a href=\"/v5/servlet/Support_sync?rtype=errorlogs\" target=\"_top\">View Roster Sync Error Logs</a>");
    out.println("            </p>");
    out.println("    </td>");
    out.println("</tr>");
    out.println("<tr>");
    out.println("    <td>");
    out.println("            <p align=\"center\">");
    out.println("            <a href=\"/v5/servlet/Support_sync?rtype=rosters\" target=\"_top\">View Rosters</a>");
    out.println("            </p>");
    out.println("    </td>");
    out.println("</tr>");
    out.println("<tr>");
    out.println("    <td>");
    out.println("            <p align=\"center\">");
    out.println("            <a href=\"/v5/servlet/Support_getProEmails\" target=\"_top\">Get All Pro Emails From Active Clubs</a>");
    out.println("            </p>");
    out.println("    </td>");
    out.println("</tr>");
    out.println("</font>");
    out.println("</table>");
    
        
    
    out.println("<br />");
    out.println("<p class=\"general_button\"><a href=\"/" +rev+ "/servlet/Logout\">Logout</a></p>");
    out.println("</body>");
    out.println("</html>");
    out.close();
   
   
 }        // end of doGet
   

 // *********************************************************
 // Illegal access by user - force user to login....
 // *********************************************************

 private void invalidUser(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
   out.println("<body class=\"serifFont\">");
   out.println("<div id=\"wrapper\">");
   out.println("<img src=\"/" +rev+ "/images/foretees.gif\" /><br />");
   out.println("<hr class=\"menu\">");
   out.println("<br /><h2>Access Error</h2><br />");
   out.println("<br /><br />Sorry, you must login before attempting to access these features.<br />");
   out.println("<br /><br />Please <a href=\"Logout\">login</a>");
   out.println("</div>    <!-- wrapper  -->");
   out.println("</body></html>");

 }
 
 
}
