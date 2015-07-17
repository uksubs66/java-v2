/***************************************************************************************     
 *   Admin_announce: This servlet will process the 'View Club Announcements' request
 *                     from the Admin's main page.  It will also (doPost) process the
 *                     request to save the edited announcement page from Active Edit (ae.jsp).
 *
 *
 *   called by:  Admin_main (doGet)
 *
 *
 *   created: 1/16/2004   Bob P.
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


public class Admin_announce extends HttpServlet {

 
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //*****************************************************
 // Process the initial request from Admin_main
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           

   //
   //  Prevent cacheing so sessions are not mangled
   //
   resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
   resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
   resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
        

   HttpSession session = SystemUtils.verifyAdmin(req, out);             // check for intruder

   if (session == null) {
     
      return;
   }

   String club = (String)session.getAttribute("club");   // get club name

   out.println("<html><head>");
   out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
   out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
   out.println("<title> \"ForeTees Proshop Announcement Page\"</title>");
//   out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees.css\" type=\"text/css\"></link>");
   out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>");
   out.println("</head>");
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   
   File f;
   FileReader fr;
   BufferedReader br;
   String tmp = "";
   String path = "";
   
   try {
       path = req.getRealPath("");
       tmp = "/announce/" +club+ "_announce.htm"; // "/" +rev+ 
       f = new File(path + tmp);
       fr = new FileReader(f);
       br = new BufferedReader(fr);
       if (!f.isFile()) {
           // do nothing
       }
   }
   catch (FileNotFoundException e) {
       out.println("<p>Missing Announcement Page.</p>");
       out.println("</BODY></HTML>");
       return;
   }
   catch (SecurityException se) {
       out.println("<p>Access Denied.</p>");
       out.println("</BODY></HTML>");
       return;
   }
   
   while( (tmp = br.readLine()) != null )
       out.println(tmp);
   
   br.close();
   
   out.println("</BODY></HTML>");
   
   /*
   //
   //  Call is to display the announcement page.
   //
   //  Display a page to provide a link to the club's announcement page
   //
   out.println("<HTML><HEAD><Title>Admin Display Announcements Page</Title>");
   out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" +rev+ "/announce/" +club+ "_announce.htm\">");
   out.println("</HEAD>");
   out.println("<BODY><CENTER>");
   out.println("<BR><BR><H3>Display Club Announcements</H3>");
   out.println("<BR><BR>");
   out.println("<BR><BR>Click on the link below to display the club announcments.");
   out.println("<BR><BR>");
   out.println("<a href=\"/" +rev+ "/announce/" +club+ "_announce.htm\">Club Announcements</a>");
   out.println("</CENTER></BODY></HTML>");
   */
   
 }  // end of doGet

}
