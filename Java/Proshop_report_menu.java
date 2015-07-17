/***************************************************************************************     
 *   Proshop_report_menu:  This is a temp file for testing the new reports. 
 *                         
 *        ******* TEMP REPORT MENU ***********
 * 
 *        7/18/08   Added limited access proshop users checks
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


public class Proshop_report_menu extends HttpServlet {
 
                                 
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //****************************************************
 // Process the request from Proshop Reports Menu
 //****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();


   HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

   if (session == null) {

      return;
   }
      
   Connection con = SystemUtils.getCon(session);                     // get DB connection
   
   if (con == null) {
       
       out.println(SystemUtils.HeadTitle("DB Connection Error"));
       out.println("<BODY><CENTER><BR>");
       out.println("<BR><BR><H3>Database Connection Error</H3>");
       out.println("<BR><BR>Unable to connect to the Database.");
       out.println("<BR>Please try again later.");
       out.println("<BR><BR>If problem persists, contact customer support.");
       out.println("<BR><BR>");
       out.println("<a href=\"javascript:history.back(1)\">Return</a>");
       out.println("</CENTER></BODY></HTML>");
       out.close();
       return;
   }
   
   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "REPORTS", con, out)) {
       SystemUtils.restrictProshop("REPORTS", out);
       return;
   }

   String club = (String)session.getAttribute("club");    
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   //
   //  Build the html page for the menu
   //
   out.println(SystemUtils.HeadTitle("Proshop - Temp Reports Menu"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<center><br><br>");

   out.println("<table border=\"2\" align=\"center\" cellpadding=\"5\" bgcolor=\"f5f5dc\" width=\"400\">");

   out.println("<tr><td align=\"center\" valign=\"top\" bgcolor=\"336633\">");
   out.println("<font color=\"ffffff\" size=\"4\">");
   out.println("<b>Temporary Rounds Report Menu</b><br>");
   out.println("</font>");

   out.println("<font color=\"ffffff\" size=\"2\"> <br>");
   out.println("Select the desired report from the list below.<br>");
   out.println("</font></td></tr>");

   out.println("<tr>");
   out.println("<td align=\"center\"><font size=\"2\">");
   out.println("<br>");

      out.println("<a href=\"/v5/servlet/Proshop_report_course_rounds\">LM, MTD, YTD Summary Report</a>");
      out.println("<br><br>");

      out.println("<a href=\"/v5/servlet/Proshop_report_course_rounds?today\">Today Summary Report</a>");
      out.println("<br><br>");

      out.println("<a href=\"/v5/servlet/Proshop_report_course_rounds?custom\">Custom Date Range Report</a>");
      out.println("<br><br>");

   out.println("</font></td></tr>");
   out.println("</font>");
   out.println("</table>");
   out.println("<br>");
   out.println("<form method=\"get\" action=\"/v5/servlet/Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline;\">");
   out.println("</form>");
   out.println("<font size=\"3\">");
   out.println("<br>");
   out.println("<b>Notice:</b> These reports will soon replace the existing Rounds Reports.  We are providing them here <br>");
   out.println("temporarily so you can try them and get accustomed to the new format before we replace the current reports.<br>");
   out.println("We would like your feedback. <b>Please email all comments and suggestions to support@foretees.com.</b><br><br>");
   out.println("Thank you!");
     
   if (club.startsWith( "demo" )) {
      out.println("<br><br>");
      out.println("<a href=\"/v5/servlet/Proshop_report_course_utilization\">**New** Click Here to Test Course Utilization Report</a>");
   }
   out.println("</font>");

   out.println("</center>");
   out.println("</body>");
   out.println("</html>");

 }   // end of doGet

}
