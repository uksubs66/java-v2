/***************************************************************************************
 *   Member_announce:  This servlet will process the 'View Club Announcements' request
 *                     from the Member's main page.
 *
 *
 *   called by:  member_main.htm (doGet)
 *
 *   created: 10/01/2002   Bob P.
 *
 *   last updated:            ******* keep this accurate *******
 *
 *        6/09/10   Add a div tag with align=center to force all announcement pages to center alignment.
 *        2/24/10   By Pass Announcement Page for Palo Alto Hills - Case# 1792
 *        9/25/09   Add support for Activity announcement pages
 *       12/21/07   By Pass Announcement Page for Denver CC - Case# 1333
 *        4/14/06   Changed to read in the announcement page from disk then output
 *                  it to the user instead of loading it in an iframe and having
 *                  the client request the page.
 *       10/05/04   Changes for Version 5.
 *        1/16/04   Changes for Version 4.
 *        7/18/03   Enhancements for Version 3 of the software.
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


public class Member_announce extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 //*****************************************************
 // Process the initial request from member_main.htm
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

   String club = (String)session.getAttribute("club");   // get club name
   String caller = (String)session.getAttribute("caller");
   int activity_id = (Integer)session.getAttribute("activity_id");

   if (club.equals( "" )) {

      invalidUser(out);            // Error - reject
      return;
   }

   // By Pass Announcement Page for Denver CC & Palo Alto Hills - Case# 1333 & 1792
   if ((club.equals( "denvercc" ) || club.equals( "paloaltohills" )) && activity_id == 0) {
       
       out.println("<html><head>");
       out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" +rev+ "/servlet/Member_select\">");
       out.println("<script type=\"text/javascript\">window.location.href='/" +rev+ "/servlet/Member_select';</script>");
       out.println("</head>");
       out.println("<body></body></html>");
       out.close();
       return;
   }
   
   //
   //  Display a page to provide a link to the club's announcement page
   //
   out.println("<html><head>");
   out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
   out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
   out.println("<title> \"ForeTees Member Announcement Page\"</title>");
//   out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees.css\" type=\"text/css\"></link>");
   out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>");

   //out.println("<style type=\"text/css\"> body {width: 80%; margin-right: auto; margin-left: auto;} </style>");      // so body will align on center
   out.println("<style type=\"text/css\"> body {text-align:center} </style>");      // so body will align on center
     
   out.println("</head>");
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");

   SystemUtils.getMemberSubMenu(req, out, caller);

   out.println("<div style=\"align:center; margin:0px auto;\">");

   File f;
   FileReader fr;
   BufferedReader br;
   String tmp = "";
   String path = "";
   
   try {
       path = req.getRealPath("");
       tmp = "/announce/" +club+ "_announce" + ((activity_id == 0) ? "" : "_" + activity_id) + ".htm"; // "/" +rev+ 
       f = new File(path + tmp);
       fr = new FileReader(f);
       br = new BufferedReader(fr);
       if (!f.isFile()) {
           // do nothing
       }
   }
   catch (FileNotFoundException e) {
       out.println("<p>&nbsp;</p><p align=center><i>Missing Announcement Page.</i></p>");
       out.println("</div></BODY></HTML>");
       out.close();
       return;
   }
   catch (SecurityException se) {
       out.println("<p>&nbsp;</p><p align=center><i>Access Denied.</i></p>");
       out.println("</div></BODY></HTML>");
       out.close();
       return;
   }
   
   while( (tmp = br.readLine()) != null )
       out.println(tmp);
   
   br.close();
   
   /*
   out.println("<iframe src=\"/" +rev+ "/announce/" +club+ "_announce.htm\" frameborder=\"0\" class=\"announce\" marginwidth=\"0\" marginheight=\"15\" scrolling=\"auto\" height=\"100%\" width=\"100%\" allowtransparency=\"true\">");
   out.println("<!-- Alternate content for non-supporting browsers -->");
   out.println("<H2>The browser you are using does not support frames</H2>");
   out.println("</iframe>");
   */
   
   out.println("</div></BODY></HTML>");
   out.close();

 }  // end of doGet


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
   out.println("<a href=\"/" +rev+ "/servlet/Logout\" target=\"_top\">Return</a>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }

}