/***************************************************************************************
 *   Member_clubdining: This servlet will link to the Club Dining System
 *
 *
 *   Called by:     called by dining link in Member_maintop
 *
 *
 *   Created:       3/03/2009 by Paul
 *
 *
 *   Last Updated:  
 *
 *                  
 *                  
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;



public class Member_clubdining extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
    
    
 //*****************************************************
 // Process the a get method on this page as a post call
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

    resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server
    resp.setContentType("text/html");

    PrintWriter out = null;

    try { out = resp.getWriter(); }
    catch (Exception ignore) {}
    
    HttpSession session = SystemUtils.verifyMem(req, out);             // check for intruder

    if (session == null) return;

    String club = (String)session.getAttribute("club");   // get club name
    String caller = (String)session.getAttribute("caller");

    if (club.equals( "" )) {

        invalidUser(out);            // Error - reject
        return;
    }
   
    //
    //  Display a page to provide a link to the club's announcement page
    //
    out.println("<html><head>");
    out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
    out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
    out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>");
    out.println("</head>");
    out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\">");

    SystemUtils.getMemberSubMenu(req, out, caller);

    // Load up the Club Dining System in this iframe
    out.println("<br><center><iframe src=\"http://foretees.clubdining.com/\" frameborder=\"0\" marginwidth=\"0\" marginheight=\"0\" style=\"align:center\" scrolling=\"auto\" height=\"90%\" width=\"90%\">");
    out.println("</iframe></center>");
    
    out.println("</BODY></HTML>");
    out.close();

 } // end of doGet routine


 // *********************************************************
 // Illegal access by user - force user to login....
 // *********************************************************
 private void invalidUser(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
   out.println("<BODY><CENTER>");
   out.println("<BR><H2>Access Error</H2><BR>");
   out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
   out.println("<BR>This site requires the use of Cookies for security purposes.  We use them to verify");
   out.println("<BR>your session and prevent unauthorized access.  Please check your 'Privacy' settings,");
   out.println("<BR>under 'Tools', 'Internet Options' (for MS Internet Explorer).  This must be set to");
   out.println("<BR>'Medium High' or lower.  Thank you.");
   out.println("<BR><BR>");
   out.println("<a href=\"Logout\" target=\"_top\">Return</a>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }

} // end servlet public class