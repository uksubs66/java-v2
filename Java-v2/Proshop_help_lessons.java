/***************************************************************************************
 *   Proshop_help_lessons: This servlet will process the 'Help - How Does This Work' 
 *                         Lesson Menu Item from the Proshop's main menu. 
 *
 *
 *   created: 4/28/2005   Bob P.
 *
 *   last updated:            ******* keep this accurate *******
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


public class Proshop_help_lessons extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //*****************************************************
 // Process the initial request from Proshop_main
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


   HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

   if (session == null) {

      return;
   }

   String club = (String)session.getAttribute("club");   // get club name
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   //
   //  Call is to display the announcement page.
   //
   //  Display a page to provide a link to the club's announcement page
   //
   out.println("<html><head>");
   out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
   out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
   out.println("<title> \"ForeTees Proshop Announcement Page\"</title>");
   out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees.css\" type=\"text/css\">");
   out.println("<script language=\"JavaScript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>");
   out.println("</head>");
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");

   SystemUtils.getProshopSubMenu(req, out, lottery);

   out.println("<iframe frameborder=\"0\" class=\"announce\" marginwidth=\"0\" marginheight=\"0\" scrolling=\"auto\" height=\"100%\" width=\"100%\" src=\"/" +rev+ "/proshop_help_lessons.htm\">");
   out.println("<!-- Alternate content for non-supporting browsers -->");
   out.println("<H2>The browser you are using does not support frames</H2>");
   out.println("</iframe>");
   out.println("</BODY></HTML>");

 }  // end of doGet

}
