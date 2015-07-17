/***************************************************************************************
 *   Proshop_features: This servlet will process the 'New Features' request
 *                     from the Proshop's main menu.  
 *
 *
 *   called by:  Proshop_maintop
 *
 *
 *   created: 4/29/2008   Bob P.
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


public class Proshop_features extends HttpServlet {


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
   //  Call is to display the new features page.
   //
   //  Display a page to provide a link to the new feature page
   //
   out.println("<html><head>");
   out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
   out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
   out.println("<title> \"ForeTees Proshop Announcement Page\"</title>");
//   out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees.css\" type=\"text/css\"></link>");
   out.println("<script language=\"JavaScript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>");
   out.println("</head>");
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");

   SystemUtils.getProshopSubMenu(req, out, lottery);
  
   File f;
   FileReader fr;
   BufferedReader br;
   String tmp = "";
   String path = "";
   
   try {
       path = req.getRealPath("");
       tmp = "/proshop_features.htm";      // "/" +rev+ 
       f = new File(path + tmp);
       fr = new FileReader(f);
       br = new BufferedReader(fr);
       if (!f.isFile()) {
           // do nothing
       }
   }
   catch (FileNotFoundException e) {
       out.println("<br><br><p align=center>Missing New Features Page.</p>");
       out.println("</BODY></HTML>");
       out.close();
       return;
   }
   catch (SecurityException se) {
       out.println("<br><br><p align=center>Access Denied.</p>");
       out.println("</BODY></HTML>");
       out.close();
       return;
   }
   
   while( (tmp = br.readLine()) != null )
       out.println(tmp);
   
   br.close();
   
   out.println("</BODY></HTML>");
   out.close();
   
 }  // end of doGet

}
