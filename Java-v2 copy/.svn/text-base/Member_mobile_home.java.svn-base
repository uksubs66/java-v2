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
 *
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


   if (req.getParameter("switch") == null) {      // if request for Mobile Home page

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
        out.println("<li><a href=\"Member_mobile_home?switch\">Go To Full Site . . . . . . . . . .</a></li>");
        out.println("<li><a href=\"Logout\">Log Out . . . . . . . . . . . . . . . .</a></li>");

      out.println("</ul></div>");
      out.println("<div><p>&nbsp;</p><p>&nbsp;</p></div>");    //  add a couple of blank lines at the bottom to allow for the IOS Nav Bar (iPhones)
      out.println("</form></body>");
      out.println("</html>");

   } else {       // request is to switch to the Full Site
       
       session.setAttribute("mobile", 0);  // clear mobile flag if we are going to full site 
       
        // flash a quick message and route to FlxRez

        out.println("<HTML xmlns='http://www.w3.org/1999/xhtml'><HEAD><Title>ForeTees Mobile Site Switch</Title>");
        out.println("<meta http-equiv=\"Refresh\" content=\"0; url=Member_announce\">");
        out.println("<meta name=\"viewport\" id=\"viewport\" content=\"width=device-width, user-scalable=no\">");
        out.println("<LINK REL=StyleSheet HREF=\"/" +rev+ "/mobile/style.css\" TYPE=\"text/css\" MEDIA=screen></HEAD>");
        out.println("<BODY><CENTER>");
        out.println("<BR><BR><div class=\"headertext\">Thank you.  We will now route you to the ForeTees site.");
        out.println("<BR><BR></div>");
        out.println("<form method=\"get\" action=\"Member_announce\">");
        out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form>");
        out.println("</CENTER></BODY></HTML>");
   }
   out.close();

 }  // end of doGet

}
