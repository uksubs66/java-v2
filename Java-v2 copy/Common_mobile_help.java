/***************************************************************************************
 *   Common_mobile_help:  This servlet will process the 'Mobile Help' request from Member_maintop.
 *
 *
 *   called by:  Member_maintop - Mobile help Link
 *               Proshop_maintop
 *
 *   created: 1/14/2010   Bob P.
 *
 *   last updated:      ******* keep this accurate *******
 * 
 *    12/03/13  Add a notice to inform members that this is not an app.
 *     1/17/13  Add the Request object to outputPageEnd so we can get the session object to test caller.
 *    12/12/12  Add clarification for FLexRez and Dining access.
 *     2/03/10  Add some larger fonts to draw the users' attention.
 *     2/02/10  Add the url to allow members to start using the interface.
 *     1/26/10  Add proshop info so proshop users can view this as well.  Also, allow proshop user 
 *              the ability to enable the mobile interface for their club.
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


public class Common_mobile_help extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //
 // Process the initial call from Member_maintop
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
   
   ResultSet rs = null;
   Statement stmt = null;
   PreparedStatement pstmt = null;
   HttpSession session = null;

   
   String user = "";
   
   
   session = req.getSession(false);  // Get user's session object (no new one)

   if (session != null) {

      user = (String)session.getAttribute("user");   // get username

      if (!user.startsWith( "proshop" )) {

         String sess_id = (String)session.getAttribute("sess_id");   // get session id

         if (!sess_id.equals( "foretees" ) || user.equals( "" )) {

            session = null;
         }
      }
   }

   if (session == null) {
      return;
   }


   //
   //  get the club name
   //
   String club = (String)session.getAttribute("club");      // get club name
   
    int sess_activity_id = 0;

    if (session.getAttribute("activity_id") != null) {      
    
       sess_activity_id = (Integer)session.getAttribute("activity_id");
    }

   
   Connection con = SystemUtils.getCon(session);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR><input type=\"button\" value=\"Exit\" onClick='self.close();'><BR><BR>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   
   //
   //  Check if proshop user wants to enable/disable the mobile interface
   //
   if (req.getParameter("enable") != null)  {  
      
      String enable = req.getParameter("enable");      // get the yes or no
      
      switchMobile(enable, out, con);
      return;
   }
   

   int allow_mobile = 0;
   
   
   //
   //  Get the allow_mobile flag for this club - is Mobile supported?
   //
   try {
      
      stmt = con.createStatement();           

      rs = stmt.executeQuery("SELECT allow_mobile FROM club5");

      if (rs.next()) {

         allow_mobile = rs.getInt("allow_mobile");         
      }
    
      stmt.close();
      
   }
   catch (Exception exc) {
   }

   
     
   //
   //  Output the help page
   //
   Common_skin.outputHeader(club, sess_activity_id, "Mobile Help", true, out, req, 0, "");     // output the page start - no refresh or URL to jump to                  
   
    out.println("<body>");
    out.println("<div id=\"wrapper_login\" align=\"center\">");
    out.println("<div id=\"title\">Mobile Help</div>");
    out.println("<div id=\"main_login\" align=\"center\">");
    out.println("<div class=\"main_message\">");
    out.println("<center>");

   
   out.println("<H2>How to Access ForeTees From a Mobile Device</H2><BR><BR>");
   
   if (allow_mobile == 0 && !user.startsWith("proshop")) {     // if member and not allowed
      
      out.println("<p align=center>Sorry, but your club has not opted to utilize the mobile interface at this time.");    
      out.println("<BR><BR>Thank you!");
      out.println("<BR><BR><input type=\"button\" value=\"Exit\" onClick='self.close();'><BR><BR></p>");
      
   } else {
   
      if (user.startsWith("proshop")) {     // if proshop user
         
         out.println("<BR><H3>FOR MEMBERS ONLY</H3><BR>");
   
         out.println("<p align=center>The Mobile Interface is available for <b>members only</b> at this time, <b>not proshop users</b>.<BR><BR>" +
                     "The following information is provided so that you may assist your members should they have questions.<BR>" +
                     "You may use the interface by adding a member account for yourself (use Admin).</p><BR><BR>");     

         out.println("<table border=1 align=\"center\" cellpadding=5><tr>");
         out.println("<td align=center><font size=\"2\">");

         if (allow_mobile == 0) {     // if not allowed
            
            out.println("<b>NOTE:</b> The Mobile Interface is currently <b>DISABLED</b> for your club.<BR>" +
                        "You can enable it, but be advised that it will then be available to your<BR>" +
                        "members and they will be notified when they login to ForeTees.");    
            
            out.println("<br><br><a href=\"Common_mobile_help?enable=yes\">Click Here To ENABLE The Mobile Interface</a><BR><BR>");
            
         } else {
            
            out.println("<b>NOTE:</b> The Mobile Interface is currently <b>ENABLED</b> for your club.<BR>" +
                        "You can disable it, but be advised that it will then NOT be available to your members.");
            
            out.println("<br><br><a href=\"Common_mobile_help?enable=no\">Click Here To DISABLE The Mobile Interface</a><BR><BR>");
         }        
         out.println("</font></td></tr></table>");

         out.println("<BR><BR><H3>Mobile Directions For Member Access</H3><BR><BR>");
      }
      
      out.println("<table border=\"0\" width=\"600\"><tr>");
      out.println("<td align=\"left\"><font size=\"2\">");

      out.println("ForeTees is proud to introduce a site designed exclusively for mobile devices.&nbsp;&nbsp;This site will allow you to easily view your reservations, make new reservations, and manage your existing requests and reservations.");
      out.println("<BR><BR>Access to the Tee Time, Dining and the FlxRez (Tennis, etc) systems is available from your mobile device, however only the Tee Time system is available in both the full sized and mobile formats.");
      
      out.println("<BR><BR><table style=\"border-spacing:10\" border=\"0\" width=\"600\" bgcolor=\"yellow\"><tr>");
      out.println("<td align=\"left\"><font size=\"2\">");
      out.println("<b>NOTICE:</b>&nbsp This is a mobile-enabled site, NOT an 'App'.&nbsp; There is no need to download anything and no additional charge from ForeTees.&nbsp;&nbsp;" +
                  "However, you may incur data charges from your cellular service provider, as with any site visited from your mobile device.");      
      out.println("</font></td></tr></table>");

      
      out.println("<BR><b>Phones and Mobile Devices Supported:</b>&nbsp;&nbsp;Due to the high number of mobile manufacturers and devices available, it is nearly impossible for us to test all mobile appliances.&nbsp;&nbsp;" +
                   "We have verified several of the current makes & models (i.e. iPhone/iPad, Blackberry, Droid, etc) and found they work very well.&nbsp;&nbsp;If you have a mobile device that is capable of browsing websites, " +
                   "then we suggest that you try our mobile site.");

         out.println("<BR><BR><H3>PLEASE READ THE FOLLOWING INSTRUCTIONS (see Print button below)</H3><BR>");
   
      out.println("Here is what you will need to do (while logged in on your PC):<BR><BR>");
      out.println("<ol><li><b>Update your partner list</b>.&nbsp;&nbsp;Select the <b>'Partner List'</b> tab in the navigation panel.&nbsp;&nbsp;" +
                  "It is highly recommended that you add all your playing partners to your partner list as this will make it much easier (faster) to book your tee times.</li>");
      out.println("<BR><li><b>Create your mobile credentials</b> (username & password).&nbsp;&nbsp;Select the <b>'Settings'</b> tab in the navigation panel.&nbsp;&nbsp;" +
                  "The mobile credentials will be used for your mobile access only.&nbsp;&nbsp;However, these values can be the same as those used to login to ForeTees or your website, as long as the username is unique.&nbsp;&nbsp;" +
                  "To set or change your mobile credentials, look for the 'Mobile Credentials' section near the bottom of the Settings page.</li>");
      out.println("</ol>");

      out.println("<BR>You will then be ready to <b>login from your smart phone</b>.&nbsp;&nbsp;To access the ForeTees Mobile Site from your smart phone, type the following address into your phone's browser:");
      out.println("</font><font size=\"3\">");
      out.println("<BR><BR><p align=center><b>http://m.foretees.com/" +club+ "</b></p>");
      out.println("</font><font size=\"2\"><BR>");

      out.println("<p align=center><a href=\"http://www.foretees.com/messages/iphone_demo.pdf\" target=\"_blank\">Click here to see how to book a tee time from your smart phone.</a></p>");

      out.println("<BR>Be sure to <b>bookmark</b> the Login page on your smart phone!");

      out.println("<br><br>iPhone Users - <a href=\"http://www.foretees.com/messages/iphone_help.pdf\" target=\"_blank\">Click here to see how.</a>");

      out.println("<BR><BR><b>Notice:</b>&nbsp;&nbsp;ForeTees cannot guarantee that our mobile site will work with all smart phones.&nbsp;&nbsp;" +
                  "If you experience difficulties and would like to contact support, please click on the 'Need Assistance' link on the mobile login page (from your smart phone).");


      out.println("</font></td></tr></table>");

      out.println("<BR><BR>Thank you!");
      out.println("<BR><BR><input type=\"button\" value=\"Exit\" onClick='self.close();'><BR><BR>");
      out.println("<form method=\"link\" action=\"javascript:self.print()\">");
      out.println("<button type=\"submit\" style=\"text-decoration:underline; background:white\">Print This Page</button>");
      out.println("</form>");
   }
 
   //out.println("</font></CENTER></BODY></HTML>");
    out.println("</center></div></div>");
    Common_skin.outputPageEnd(club, sess_activity_id, out, req);    // finish the page       
   out.close();

 }      // end of doGet
 
 
 // ***************************************************************************
 //  Process a request to Enable or Disable the Mobile Interface for this club
 // ***************************************************************************

 private void switchMobile(String enable, PrintWriter out, Connection con) {


   Statement stmt = null;

   
   out.println(SystemUtils.HeadTitle("Mobile Help Page"));
   out.println("<BODY><CENTER>");
   out.println("<img src=\"/" +rev+ "/images/foretees.gif\">");
   out.println("<font size=\"3\" face=\"Arial, Times New Roman, Helvetica\">");
   out.println("<BR><BR><H2>Enable/Disable Mobile Interface</H2>");
     
   
   try {
    
      stmt = con.createStatement();     
      
      if (enable.equalsIgnoreCase("yes")) {

         stmt.executeUpdate("UPDATE club5 SET allow_mobile = 1");     // enable Mobile
         
      } else {
         
         stmt.executeUpdate("UPDATE club5 SET allow_mobile = 0");     // disable Mobile
      }
         
      stmt.close();
      
   }
   catch (Exception exc) {
      
      out.println("<BR><BR>Thank you!");
      out.println("<BR><BR><input type=\"button\" value=\"Exit\" onClick='self.close();'><BR><BR>");
      out.println("<form method=\"link\" action=\"javascript:self.print()\">");      
   }

   if (enable.equalsIgnoreCase("yes")) {

      out.println("<p align=center>The Mobile Interface is now ENABLED.");    

   } else {

      out.println("<p align=center>The Mobile Interface is now DISABLED.");    
   }     
      
   out.println("<BR><BR>Thank you!");
   out.println("<BR><BR><a href=\"Common_mobile_help\">Return</a><BR>");
         
   out.println("</font></CENTER></BODY></HTML>");
   out.close();         
 } 

}










