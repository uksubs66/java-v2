/***************************************************************************************
 *   Admin_services:  This servlet will process the 'change password' request from Admin's
 *                   services page.
 *
 *
 *   called by:  admin_services.htm
 *
 *   created: 12/05/2001   Bob P.
 *
 *   last updated:
 *
 *        4/24/08  Update Connection object to use SystemUtils.getCon()
 *        7/18/03  Enhancements for Version 3 of the software.
 *        1/14/03  Enhancements for Version 2 of the software.
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

public class Admin_services extends HttpServlet {

 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 // Process the form request from admin_services.htm.....
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    //
    //  Prevent caching so sessions are not mangled
    //
    resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Connection con = null;                 // init DB objects
   Statement stmt = null;
   ResultSet rs = null;

   String admin = "admin";

   HttpSession session = null;

   //
   // Make sure user didn't enter illegally
   //
   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   String user = (String)session.getAttribute("user");   // get username

   if (!user.equalsIgnoreCase( admin )) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   String club = (String)session.getAttribute("club");   // get club name

   con = SystemUtils.getCon(session);            // get DB connection
      
   if (con == null) {

      out.println(SystemUtils.HeadTitleAdmin("DB Connection Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Admin_announce\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }


   //
   // Get all 3 passwords entered
   //
   String old_password = req.getParameter("oldpw");
   String new_password = req.getParameter("newpw");
   String conf_password = req.getParameter("newpwc");
   String curr_password = "";

   int pw_length = new_password.length();               // length of new password

   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT password FROM login2 WHERE username = 'admin'"); // get admin's pw...

      if (rs.next()) {

         curr_password = rs.getString("password");
      }
      stmt.close();

   }
   catch (Exception exc) {             // SQL Error

      out.println(SystemUtils.HeadTitleAdmin("DB Access Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Access Error 1</H3>");
      out.println("<BR><BR>Unable to process database change at this time.");
      out.println("<BR>Exception: "+ exc.getMessage());
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/admin_mainleft.htm\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      return;

   }

   //
   // Verify the passwords
   //
   if ((old_password.equals( curr_password )) && (new_password.equals( conf_password )) &&
       (pw_length > 3) && (pw_length < 11)) {

      if (club.equalsIgnoreCase( "demo" )) {        // do not change pw if demo site

         out.println(SystemUtils.HeadTitleAdmin("Admin Password Changed"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>New Password Accepted</H3>");
         out.println("<BR><BR>Thank you.  For demo purposes the password has not been changed.");
        out.println("</CENTER></BODY></HTML>");

      } else {

         try {

            PreparedStatement pstmt = con.prepareStatement (
                  "UPDATE login2 SET password = ? WHERE username = 'admin'");

            // Set admin's new pw

            pstmt.clearParameters();            // clear the parms
            pstmt.setString(1, new_password);   // put the password in statement
            int count = pstmt.executeUpdate();  // execute the prepared stmt

            out.println(SystemUtils.HeadTitleAdmin("Admin Password Changed"));
            out.println("<BODY><CENTER>");
            out.println("<BR><BR><H3>Password Has Been Changed</H3>");
            out.println("<BR><BR>The password you just specified must be entered the next time you login.");
              out.println("</CENTER></BODY></HTML>");

            pstmt.close();

         }
         catch (Exception exc) {             // SQL Error

               out.println(SystemUtils.HeadTitleAdmin("DB Access Error"));
               out.println("<BODY><CENTER>");
               out.println("<BR><BR><H3>Database Access Error 2</H3>");
               out.println("<BR><BR>Unable to process database change at this time.");
               out.println("<BR>Exception: "+ exc.getMessage());
               out.println("<BR>Please try again later.");
               out.println("<BR><BR>If problem persists, contact customer support.");
               out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Admin_announce\">Return</a>");
               out.println("</CENTER></BODY></HTML>");
         }
      }    // end of if demo site

   } else {

      out.println(SystemUtils.HeadTitleAdmin("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Data Entry Error</H3>");
      out.println("<BR>The data you entered is incorrect.");
      out.println("<BR><BR>The Old Password must equal your Current Password that you logged in with.");
      out.println("<BR>The New Password must be at least 4 characters but no more than 10 characters.");
      out.println("<BR>Avoid special characters as some are not accepted.");
      out.println("<BR><BR>Please try again.");
      out.println("<BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"/" +rev+ "/admin_services.htm\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#F0E68C\">");
      out.println("</input></form></font>");
      out.println("</CENTER></BODY></HTML>");

   }

   //
   // Done - return.......
   //
 }

 // *********************************************************
 // Illegal access by user - force user to login....
 // *********************************************************

 private void invalidUser(PrintWriter out) {

   out.println(SystemUtils.HeadTitleAdmin("Access Error - Redirect"));
   out.println("<BODY><CENTER>");
   out.println("<BR><H2>Access Error</H2><BR>");
   out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
   out.println("<BR><BR>");
   out.println("<a href=\"/" +rev+ "/servlet/Logout\" target=\"_top\">Return</a>");
   out.println("</CENTER></BODY></HTML>");

 }

}
