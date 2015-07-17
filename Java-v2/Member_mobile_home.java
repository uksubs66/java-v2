/***************************************************************************************
 *   Member_mobile_home:  This servlet will provide the Home page for ForeTees Mobile.
 *
 *
 *   called by:  Login or the Home tab in Mobile Site
 *
 *   created: 12/12/2012   Bob P.
 *
 *   last updated:
 *
 *      4/17/14  Add old_mobile_count to countMobile so we count these logins separately.
 *      4/15/14  Add processing to switch user to RWD full site.
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import org.apache.commons.lang.*;

        
import com.foretees.common.Utilities;
import com.foretees.client.SystemLingo;
import com.foretees.common.Connect;
import com.foretees.common.ProcessConstants;


public class Member_mobile_home extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //*****************************************************
 // Process the Home request
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   //
   //  Prevent caching so sessions are not mangled
   //
   resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
   resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
   resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server
   
   resp.setContentType("text/html");  
   PrintWriter out = resp.getWriter();
   
   HttpSession session = SystemUtils.verifyMem(req, out);             // check for intruder

   if (session == null) {
      return;
   }

   String club = (String)session.getAttribute("club");               // get club name
   String user = (String)session.getAttribute("user");
   //String caller = (String)session.getAttribute("caller");
   //String mship = (String)session.getAttribute("mship");             // get member's mship type
   //String mtype = (String)session.getAttribute("mtype");             // get member's mtype
   int activity_id = (Integer)session.getAttribute("activity_id");     // should be Golf (0)  
   
   Connection con = Connect.getCon(req);               // get a connection to db
      
   
   if(session.getAttribute("counted_old_mobile") == null){
       countMobile( user, req, con);      // bump mobile counter and track mobile device 
       session.setAttribute("counted_old_mobile", true);
   }

   if (req.getParameter("switch") == null) {      // if request for Mobile Home page

      //  Check club/member RWD settings
      boolean allowRwd = Utilities.isResponsiveAllowed(con);
            
      //
      //  Mobile Home Page
      //
      out.println(SystemUtils.HeadTitleMobile("ForeTees Mobile Home Page"));
      out.println(SystemUtils.BannerMobile());

        out.println("<div class=\"headertext\">Tee Time Management</div>");
        out.println("<div class=\"smheadertext\"><BR>Please select from the following:<BR></div>");
        out.println("<div class=\"content\">");
        out.println("<ul>");
        out.println("<li><a href=\"Member_select\">Make/Change A Tee Time . .</a></li>");
        out.println("<li><a href=\"Member_teelist_mobile\">My Tee Times . . . . . . . . . . .</a></li>");
        out.println("<li><a href=\"Member_services\">Settings . . . . . . . . . . . . . . . .</a></li>");
        
        if (allowRwd) {
           
           out.println("<li><a href=\"Member_mobile_home?switch&rwd\">Go To *FULL MOBILE* Site . . . .</a></li>");

        } else {
        
           out.println("<li><a href=\"Member_mobile_home?switch\">Go To Full Site . . . . . . . . . .</a></li>");
        }
        
        out.println("<li><a href=\"Logout\">Log Out . . . . . . . . . . . . . . . .</a></li>");

      out.println("</ul></div>");
      out.println("<div><p>&nbsp;</p><p>&nbsp;</p></div>");    //  add a couple of blank lines at the bottom to allow for the IOS Nav Bar (iPhones)
      out.println("</form></body>");
      out.println("</html>");

   } else {       // request is to switch to the Full Site
       
        session.setAttribute("mobile", 0);          // clear mobile flag if we are going to full site 
        session.setAttribute("mobile_login", 0);    // and clear mobile_login flag
                   
        int currentAppMode = Utilities.getRequestInteger(req, ProcessConstants.RQA_APPMODE, 0);
        int rwdOn = Utilities.setBit(currentAppMode, ProcessConstants.APPMODE_RWD);
            
        // flash a quick message and route to full site
        out.println("<HTML xmlns='http://www.w3.org/1999/xhtml'><HEAD><Title>ForeTees Mobile Site Switch</Title>");
        out.println("<meta http-equiv=\"Refresh\" content=\"0; url="+Utilities.getBaseUrl(req, activity_id, club, rwdOn) +"Member_announce?switchView\">");
        out.println("<meta name=\"viewport\" id=\"viewport\" content=\"width=device-width, user-scalable=no\">");
        out.println("<LINK REL=StyleSheet HREF=\"/" +rev+ "/mobile/style.css\" TYPE=\"text/css\" MEDIA=screen></HEAD>");
        out.println("<BODY><CENTER>");
        out.println("<BR><BR><div class=\"headertext\">Thank you.  We will now route you to the ForeTees site.");
        out.println("<BR><BR></div>");
        out.println("<form method=\"get\" action=\""+Utilities.getBaseUrl(req, activity_id, club, rwdOn) +"Member_announce\">");
        out.println("<input type=\"hidden\" name=\"switchView\">");
        out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form>");
        out.println("</CENTER></BODY></HTML>");
   }
   out.close();

 }  // end of doGet
 
  //
 //  Mobile count method - bump counter and gather mobile device data
 //
 private static void countMobile(String user, HttpServletRequest req, Connection con) {
     
 
   //Connection con = Connect.getCon(req);
   PreparedStatement stmt = null;
   
   boolean iphone = false;
 
   //
   //  Gather the User Agent String from the request header
   //
   String ua = req.getHeader("user-agent").toLowerCase();

   if (ua.indexOf("iphone") > -1 || ua.indexOf("ipod") > -1) {

	// found an iphone or ipod
      iphone = true;

   } else if(ua.indexOf("ipad") > -1) {                   // checks for future stats !!!!!!!!!!!

	// found an iPad

   } else if(ua.indexOf("android") > -1) {

	// found an android device

   } else if(ua.indexOf("blackberry") > -1) {

	// found a blackberry device

   } else if(ua.indexOf("opera mini") > -1) {

	// found opera mini browser

   } else if(ua.indexOf("windows ce") > -1 || ua.indexOf("smartphone") > -1 || ua.indexOf("iemobile") > -1) {

	// found windows mobile device
   }

   
   
   //
   //   Increment the mobile counter for this member and update the account
   //
   //   Bump both mobile counters - mobile_count will include ALL mobile logins
   //                             - old_mobile_count will ONLY include these logins (from the mobile login page)
   
   try {
 
      stmt = con.prepareStatement (
         "UPDATE member2b SET mobile_count = mobile_count + 1, mobile_iphone = mobile_iphone + ?, "
              + "old_mobile_count = old_mobile_count + 1 WHERE username = ?");

      stmt.clearParameters();          
      stmt.setInt(1, iphone?1:0);    // new iphone count  
      stmt.setString(2, user);          // username 
      stmt.executeUpdate();

      stmt.close();
            
   } catch (Exception ignore) { 
        
   } finally {

     if (stmt != null) {
        try {
           stmt.close();
        } catch (SQLException ignored) {}
     }      
   }
            
 }       // end of countMobile
 


}
