/***************************************************************************************
 *   Support_emails:  This class will enable or diable the email notifications for a selected club.
 *
 *
 *   called by:  support_main2.htm and sales_main.htm
 *
 *   created: 4/13/2006   Bob P.
 *
 *   last updated:
 *
 *          8/01/08  Removed valid email verification from manageBackupEmails
 *          7/30/08  Added manageBackupEmails method for displaying/adding/editing/deleting emails to receive backup teesheet emails
 *          5/24/07  Added call to SystemUtils.purgeBouncedEmails
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


public class Support_emails extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();


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
   String club = (String)session.getAttribute("club");

   if (!user.equals( "support" ) && !user.startsWith( "sales" )) {

      invalidUser(out);            // Intruder - reject
      return;
   }
   
   
   if (req.getParameter("purge") != null) {
       
       SystemUtils.purgeBouncedEmails();
       
       out.println("<HTML><HEAD><TITLE>Support Purge Bounced Emails</TITLE></HEAD>");
       out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
       out.println("<H2>Purge Bounced Emails</H2>");
       out.println("<p>DONE!</p>");
       out.println("<A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
       out.println("</CENTER></BODY></HTML>");
       out.close();
       return;
       
   } // end handle purge request
   
   if (req.getParameter("backup") != null) {
       
       manageBackupEmails(req, user, club, out);
       return;
   }
           
   //
   //   Display the menu
   //
   out.println("<HTML><HEAD><TITLE>Support Toggle Emails</TITLE></HEAD>");
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<H2>Toggle Email Notification Flags</H2>");
     
   out.println("Use this utility to enable or disable email notifications for ALL members at a club.");
   out.println("<BR><BR>");

   out.println("Club: <b>" +club+ "</b>");
   out.println("<BR><BR>");
   out.println("<b>NOTICE:</b> This should only be done for new clubs.<br>This will turn on or off the email notification flags for each individual member.");
   out.println("<br>If a member has already changed his/her setting, then this could change it again.");
   out.println("<BR><BR><BR>");

      out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Support_emails\">");
      out.println("<input type=\"hidden\" name=\"toggle\" value = \"enable\">");
      out.println("<input type=\"submit\" value=\"ENABLE All Email Notifications\" style=\"text-decoration:underline;\">");
      out.println("</input></form></p>");

      out.println("<BR>");
      out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Support_emails\">");
      out.println("<input type=\"hidden\" name=\"toggle\" value = \"disable\">");
      out.println("<input type=\"submit\" value=\"DISABLE All Email Notifications\" style=\"text-decoration:underline;\">");
      out.println("</input></form></p>");

     
   out.println("<BR><BR>");
   if (user.startsWith( "sales" )) {
      out.println("<A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>");
   } else {
      out.println("<A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
   }
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 // *********************************************************
 //  doPost - perform the enable or disable 
 // *********************************************************
   
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Connection con = null;                 // init DB objects
   Statement stmt = null;
   ResultSet rs = null;


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
   String club = (String)session.getAttribute("club");

   if (!user.equals( "support" ) && !user.startsWith( "sales" )) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   if (req.getParameter("backup") != null) {
       manageBackupEmails(req, user, club, out);
       return;
   }
   
   try {
      con = dbConn.Connect(club);

   }
   catch (Exception exc) {

      // Error connecting to db....

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
         out.println("<BR><BR>");
         if (user.startsWith( "sales" )) {
            out.println("<BR><A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>");
         } else {
            out.println("<BR><A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
         }
      out.println("</CENTER></BODY></HTML>");
      return;
   }



   String toggle = "";

   if (req.getParameter("toggle") != null) {

      toggle = req.getParameter("toggle");
   }

   //
   //   Enable or Disable the members' email notification flags
   //
   try {

      if (toggle.equals( "enable" )) {       // if Enable request
        
         stmt = con.createStatement();           // create a statement

         stmt.executeUpdate("UPDATE member2b SET emailOpt = 1"); 

         stmt.close();

         out.println("<HTML><HEAD><TITLE>Support Enable Email Notifications</TITLE></HEAD>");
         out.println("<BODY><CENTER><H3>Email Notifications Enabled</H3>");
         out.println("<BR>Email notifications have been enabled for all members - club: <b>" +club+ "</b>");
         out.println("<BR><BR>");

         if (user.startsWith( "sales" )) {
            out.println("<BR><A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>");
         } else {
            out.println("<BR><A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
         }
         out.println("</CENTER></BODY></HTML>");
         out.close();
      }

      if (toggle.equals( "disable" )) {       // if Disable request

         stmt = con.createStatement();           // create a statement

         stmt.executeUpdate("UPDATE member2b SET emailOpt = 0");

         stmt.close();

         out.println("<HTML><HEAD><TITLE>Support Disable Email Notifications</TITLE></HEAD>");
         out.println("<BODY><CENTER><H3>Email Notifications Disabled</H3>");
         out.println("<BR>Email notifications ahve been disabled for all members - club: <b>" +club+ "</b>");
         out.println("<BR><BR>");

         if (user.startsWith( "sales" )) {
            out.println("<BR><A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>");
         } else {
            out.println("<BR><A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
         }
         out.println("</CENTER></BODY></HTML>");
         out.close();
      }

   }
   catch (Exception e1) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<BR><H2>Database Error</H2><BR>");
      out.println("<BR><BR>Error trying to enable/disable emails. Error = " + e1.getMessage());
      out.println("<BR><BR><BR>");
      if (user.startsWith( "sales" )) {
         out.println("<BR><A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>");
      } else {
         out.println("<BR><A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
      }
      out.println("</CENTER></BODY></HTML>");
   }

 }


 // *********************************************************
 // Illegal access by user - force user to login....
 // *********************************************************

 private void invalidUser(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H2>Access Error</H2><BR>");
   out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
   out.println("<BR><BR> <FORM>");
   out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'></INPUT>");
   out.println("</FORM></CENTER></BODY></HTML>");

 }
 
 // *********************************************************
 // Manage Tee-sheet Backup email addresses
 // *********************************************************
 private void manageBackupEmails(HttpServletRequest req, String user, String club, PrintWriter out) {
     
     Connection con = null;
     PreparedStatement stmt = null;
     ResultSet rs = null;
             
     int result = 0;
     
     try {
         con = dbConn.Connect(club);
     } catch (Exception e1) {
         
         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><H2>Database Error</H2><BR>");
         out.println("<BR><BR>Error trying to connect to database. Error = " + e1.getMessage());
         out.println("<BR><BR><BR>");
         if (user.startsWith( "sales" )) {
             out.println("<BR><A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>");
         } else {
             out.println("<BR><A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
         }
         out.println("</CENTER></BODY></HTML>");
         return;
     }

     
     if (req.getParameter("addEmail") != null) {                // Add Backup Email
         
         // Print common header
         out.println(SystemUtils.HeadTitle("Add Backup Email"));
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<br><H2>Add Backup Email</H2>");
         
         if (req.getParameter("address") != "" && req.getParameter("address") != null) {               // Add new email to database
             
             String address = req.getParameter("address");

             try {
                 // First make sure address isn't already in database
                 stmt = con.prepareStatement("SELECT * FROM backup_emails WHERE address = ?");

                 stmt.clearParameters();
                 stmt.setString(1, address);
                 rs = stmt.executeQuery();

                 if (rs.next()) {           // Email address is already in the database as a backup!
                     out.println("<p>Email is aleady in the system!");
                 } else {                   // Email not found in DB, add it as new
                     stmt = con.prepareStatement("INSERT INTO backup_emails (address) VALUE (?)");

                     stmt.clearParameters();
                     stmt.setString(1, address);
                     stmt.executeUpdate();

                     out.println("<p>Email added successfully!");

                     stmt.close();
                 }
             } catch (Exception exc) {

                 out.println(SystemUtils.HeadTitle("Database Error"));
                 out.println("<br><br>");
                 out.println("<BR><H2>Database Error</H2><BR>");
                 out.println("<BR><BR>Error trying to add email to database. Error = " + exc.getMessage());
                 out.println("<BR><BR><BR>");
                 out.println("<br><br><a href=\"/" +rev+ "/servlet/Support_emails?backup\">Return</a>");
                 out.println("</CENTER></BODY></HTML>");
                 return;
             }
         } else {
             out.println("<p>No email entered!");
         }
         
         // Print common footer
         out.println("<br><br><a href=\"/" +rev+ "/servlet/Support_emails?backup\">Return</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
         
     } else if (req.getParameter("editEmail") != null) {        // Edit Backup Email
        
         // Print common header
         out.println(SystemUtils.HeadTitle("Edit Backup Email"));
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<br><H2>Edit Backup Email</H2>");
         
         if (req.getParameter("address") != null) {         // Update database records if email has been changed
             
             String address = req.getParameter("address");
             String old_address = req.getParameter("old_address");
             
             if (address != "") {     // field wasn't left blank
                 
                 if (!old_address.equalsIgnoreCase(address)) {          // changes have been made
                     try {
                         stmt = con.prepareStatement("UPDATE backup_emails SET address = ? WHERE address = ?");

                         stmt.clearParameters();
                         stmt.setString(1, address);
                         stmt.setString(2, old_address);

                         result = stmt.executeUpdate();

                         if (result > 0) {
                             out.println("<p>Changes applied successfully!");
                         } else {
                             out.println("<p>Changes not applied.  Please try again.");
                         }

                         stmt.close();

                         out.println("<br><br><a href=\"/" +rev+ "/servlet/Support_emails?backup\">Return</a>");
                         out.println("</CENTER></BODY></HTML>");
                         return;

                     } catch (Exception exc) {

                         out.println(SystemUtils.HeadTitle("Database Error"));
                         out.println("<br><br>");
                         out.println("<BR><H2>Database Error</H2><BR>");
                         out.println("<BR><BR>Error trying to update the email in the database. Error = " + exc.getMessage());
                         out.println("<BR><BR><BR>");
                         out.println("<br><br><a href=\"/" +rev+ "/servlet/Support_emails?backup\">Return</a>");
                         out.println("</CENTER></BODY></HTML>");
                         return; 
                     }
                 } else {
                     out.println("<p>No changes entered!");
                 }
             } else {
                 out.println("<p>No address entered!!");
             }
             
             out.println("<br><br><a href=\"/" +rev+ "/servlet/Support_emails?backup&editEmail=" + old_address + "\">Return</a>");
             out.println("</CENTER></BODY></HTML>");
             return;
             
         } else {         // Display editable email address
             
             String old_address = req.getParameter("editEmail");
             
             // display text box for changing the backup email
             out.println("<table bgcolor=\"#F5F5DC\" border=\"1\" cellpadding=\"3\" cellspacing=\"1\" style=\"font-size:10pt\">");
             out.println("<form action=\"/" +rev+ "/servlet/Support_emails?backup&editEmail\" method=\"post\" name=\"backupEditForm\">");
             out.println("<input type=\"hidden\" name=\"old_address\" value=\"" + old_address + "\">");
             out.println("<tr><td>");
             out.println("<input type=\"text\" size=\"25\" name=\"address\" value=\"" + old_address + "\">");
             out.println("&nbsp&nbsp");
             out.println("<input type=\"submit\" name=\"editEmailSubmit\" value=\"Submit Changes\">");
             out.println("</td></tr>");
             out.println("</form>");
             out.println("</table>");
             
             out.println("<br><a href=\"/" +rev+ "/servlet/Support_emails?backup\">Return</a>");
             out.println("</CENTER></BODY></HTML>");
             return;
         }
         
     } else if (req.getParameter("delEmail") != null) {         // Delete Backup Email

         try {
             // Attempt to remove designated email from database
             stmt = con.prepareStatement("DELETE FROM backup_emails WHERE address = ?");

             stmt.clearParameters();
             stmt.setString(1, req.getParameter("delEmail"));

             result = stmt.executeUpdate();

             stmt.close();
             
         } catch (Exception exc) {

             out.println(SystemUtils.HeadTitle("Database Error"));
             out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
             out.println("<hr width=\"40%\">");
             out.println("<br><br>");
             out.println("<BR><H2>Database Error</H2><BR>");
             out.println("<BR><BR>Error trying to delete email from database. Error = " + exc.getMessage());
             out.println("<BR><BR><BR>");
             if (user.startsWith( "sales" )) {
                 out.println("<BR><A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>");
             } else {
                 out.println("<BR><A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
             }
             out.println("</CENTER></BODY></HTML>");
             return;
         }

         out.println(SystemUtils.HeadTitle("Delete Backup Email"));
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<br><H2>Delete Backup Email</H2>");
         if (result == 0) {
             out.println("Email not found.");
         } else {
             out.println("Email removed successfully!");
         }
         out.println("<br><br><a href=\"/" +rev+ "/servlet/Support_emails?backup\">Return</a>");
         out.println("</CENTER></BODY></HTML>");
                 
     } else {           // Display Backup Emails
         
         out.println(SystemUtils.HeadTitle("Manage Backup Emails"));
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<br><H2>Manage Backup Emails</H2>");
         if (user.equals("support")) {
             out.println("<A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
         } else {
             out.println("<A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>");
         }
         out.println("<br><br>");

         try {
             // display text box for adding new backup emails
             out.println("<table bgcolor=\"#F5F5DC\" border=\"1\" cellpadding=\"3\" cellspacing=\"1\" style=\"font-size:10pt\">");
             out.println("<form action=\"/" +rev+ "/servlet/Support_emails?backup&addEmail\" method=\"post\" name=\"backupAddForm\">");
             out.println("<tr><td>");
             out.println("&nbsp<input type=\"text\" size=\"25\" name=\"address\">");
             out.println("&nbsp");
             out.println("<input type=\"submit\" name=\"addEmailSubmit\" value=\"Add New Email\">");
             out.println("</td></tr>");
             out.println("</form>");
             out.println("</table>");
             
             // retrieve the list of backup emails from the database and display
             stmt = con.prepareStatement("SELECT * from backup_emails ORDER BY address");
             rs = stmt.executeQuery();

             out.println("<br>");
             out.println("<table bgcolor=\"#F5F5DC\" border=\"1\" cellpadding=\"3\" cellspacing=\"1\" style=\"font-size:10pt\">");

             // print header row
             out.println("<tr>");
             out.println("<td bgcolor=\"#336633\" align=\"center\" style=\"font-weight:bold; color:white\">Email Address</td>");
             out.println("<td bgcolor=\"#336633\" align=\"center\" style=\"font-weight:bold; color:white\" colspan=\"2\">Actions</td>");
             out.println("</b></tr>");

             // print email address, followed by edit, and delete links
             while (rs.next()) {
                 out.println("<tr>");
                 out.println("<td>" + rs.getString("address") + "</td>");
                 out.println("<td><a href=\"/" +rev+ "/servlet/Support_emails?backup&editEmail=" + rs.getString("address") + "\">Edit</a></td>");
                 out.println("<td><a href=\"/" +rev+ "/servlet/Support_emails?backup&delEmail=" + rs.getString("address") + "\">Delete</a></td>");
                 out.println("</tr>");
             }

             out.println("</table>");

             stmt.close();
             
         } catch (Exception exc) {

             out.println(SystemUtils.HeadTitle("Database Error"));
             out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
             out.println("<hr width=\"40%\">");
             out.println("<BR><H2>Database Error</H2><BR>");
             out.println("<BR><BR>Error trying to retrieve emails. Error = " + exc.getMessage());
             out.println("<BR><BR><BR>");
             if (user.startsWith( "sales" )) {
                 out.println("<BR><A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>");
             } else {
                 out.println("<BR><A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
             }
             out.println("</CENTER></BODY></HTML>");
             return;
         }

         out.println("<br>");
         if (user.equals("support")) {
             out.println("<A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
         } else {
             out.println("<A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>");
         }
         out.println("</CENTER></BODY></HTML>");
         return;
     }
 }  // end manageBackupEmails
}
