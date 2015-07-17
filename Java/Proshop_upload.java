/***************************************************************************************     
 *   Proshop_upload:  This servlet will receive the Announcement file (announce.htm) from
 *                  the Proshop user.  This file is referenced by the main htm pages.
 *
 *   called by:  Proshop_announce (only!!)
 *
 *   created: 12/15/2001   Bob P.
 *
 *   last updated:
 *
 *        7/18/08   Added limited access proshop users checks
 *       11/11/04   Ver 5 - add support to upload a lesson pro bio page.
 *       11/20/03   Enhancements for Version 3 - not called from admin any longer (only proshop)
 *        7/18/03   Enhancements for Version 3 of the software.
 *        9/18/02   Enhancements for Version 2 of the software.
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


public class Proshop_upload extends HttpServlet {
 
                                 
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 //
 //  doGet from menu
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
   if (!SystemUtils.verifyProAccess(req, "TOOLS_ANNOUNCE", con, out)) {
       SystemUtils.restrictProshop("TOOLS_ANNOUNCE", out);
       return;
   }
   
   String club = (String)session.getAttribute("club");   // get club name
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
     
   int id = 0;

   String proid = "0";        // default

   //
   //  If the pro id was passed, then this is a call for a lesson pro's bio page
   //
   if (req.getParameter("proid") != null) {

      proid = req.getParameter("proid");                  //  lesson pro's id

      id = Integer.parseInt(proid);                       // get and convert the proid
   }

   session.setAttribute("proid", proid);              // save proid for doPost below


   //
   //   output the html page to start the upload process
   //
   out.println(SystemUtils.HeadTitle("Upload File"));
     
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<center>");

   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

   out.println("<br><table align=\"center\" cellpadding=\"7\" border=\"1\" bgcolor=\"#336633\">");
      out.println("<tr><td>");
         out.println("<font color=\"#FFFFFF\" size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");
         if (id == 0) {     // if announcement
            out.println("<p align=\"center\"><b>Upload the 'Announcements' File</b></p>");
         } else {
            out.println("<p align=\"center\"><b>Upload the 'Lesson Pro Bio' File</b></p>");
         }
         out.println("</font>");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<p align=\"center\">Create the file using a Word Processor and then save it as a web page<br>(use the 'SAVE AS WEB PAGE' feature).</p>");
         if (id == 0) {     // if announcement
            out.println("<p align=\"center\"><b>Note:</b>  This file must be named <b>'" +club+ "_announce.htm'</b> or <b>'" +club+ "_announce.html'</b>.</p>");
         } else {
            out.println("<p align=\"center\"><b>Note:</b>  This file must be named <b>'" +club+ "_bio" +id+ ".htm'</b> or <b>'" +club+ "_bio" +id+ ".html'</b>.</p>");
         }
      out.println("</font></td></tr></table>");
      out.println("<font size=\"2\"><br>");

   out.println("<br><table align=\"center\" cellpadding=\"5\" border=\"2\" bgcolor=\"#F5F5DC\">");
      out.println("<tr><td align=\"center\">");
      out.println("<font size=\"2\">");
    out.println("Enter the path and file name below<br>");
    out.println("or click on 'Browse' to locate the file.<br>");
   out.println("<form action=\"/" +rev+ "/servlet/Proshop_upload\" method=\"post\" enctype=\"multipart/form-data\">");
   out.println("<input type=\"file\" name=\"filename\">");
   out.println("</font></td></tr></table>");
   out.println("<br><br>");
   out.println("<input type=\"submit\" value=\"Send It\" name=\"send\" style=\"text-decoration:underline\" style=\"background:#8B8970\">");
   out.println("</form>");
   out.println("<br>");
   out.println("</font>");

   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline\" style=\"background:#8B8970\">");
   out.println("</input></form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 //
 // Process the form request from above
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
        

   HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   String club = (String)session.getAttribute("club");     // get club name
   String proid = (String)session.getAttribute("proid");   // get lesson pro id

   int id = Integer.parseInt(proid);                       // get and convert the proid


   //
   //  Use a copy of Oreilly's MultipartRequest class to to process the file received.
   //  Pass it the request, a directory to save the file to (our root), and the
   //  max file size to receive (1 MB).
   //
   //   refer to page 123 of OReilly's Java Servlet Programming book by Jason Hunter!!
   //
     
   String serverFile = "//usr//local//tomcat//webapps//" + rev + "//announce";         // New server
//   String serverFile2 = "//etc//jakarta-tomcat-4.1.24//webapps//" + rev + "//announce";  // server #2
   String testFile = "c:\\java\\tomcat\\webapps\\" + rev + "\\announce";               // local

   int fail = 0;

   try {
      
      ProshopMultipartRequest multi = new ProshopMultipartRequest(req, serverFile, 1024 * 1024, club);  // change for testing
   
   }   
   catch (Exception e1) {
      
      SystemUtils.logError("Proshop_upload: ERR=" + e1.getMessage());
      fail = 1;
   }

/*
   //
   //  if above failed, try server #2
   //
   if (fail != 0) {

      fail = 0;         // reset
      try {

         ProshopMultipartRequest multi = new ProshopMultipartRequest(req, serverFile2, 1024 * 1024, club);

      }
      catch (Exception e2) {

         fail = 1;
      }
   }
*/

   //
   //  if above failed, try local pc
   //
   if (fail != 0) {

      try {

         ProshopMultipartRequest multi = new ProshopMultipartRequest(req, testFile, 1024 * 1024, club);

      }
      catch (Exception e2) {

         out.println(SystemUtils.HeadTitle("File Error - Redirect"));
         out.println("<BODY><CENTER>");
         out.println("<BR><H2>File Transfer Error</H2><BR>");
         out.println("<BR><BR>Sorry, there was a problem transferring your file.<BR>");
         out.println("<BR>Exception: "+ e2.getMessage());
         out.println("<BR><BR>Please check the file name and try again.");
         out.println("<BR><BR>The file name you selected could be invalid. ");
         if (id == 0) {     
            out.println("It must be '" +club+ "_announce.htm'.<BR>");
         } else {
            out.println("It must be '" +club+ "_bio" +id+ ".htm'.<BR>");
         }
         out.println("<BR>If problem persists, please contact customer support.");
         out.println("<BR><BR>");
         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
         out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</input></form></font>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }
   }
       
   out.println(SystemUtils.HeadTitle("File Transfer Confirmation"));
   out.println("<BODY><CENTER>");
   out.println("<BR><H2>File Transfer Complete</H2><BR>");
   out.println("<BR><BR>Thank you, your file has been transferred.<BR>");
   if (id != 0) {
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/announce/" +club+ "_bio" +id+ ".htm\" target=\"_blank\">View the Bio Page</a>");
   }
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</input></form></font>");
   out.println("</CENTER></BODY></HTML>");
 }

}
