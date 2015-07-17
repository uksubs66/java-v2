/***************************************************************************************
 *   Proshop_dining_sendEmail: This servlet will process the 'Send Email' request
 *                             from the Dining Admin's Dashboard page.
 *
 *
 *   called by:  Login.proshopUser via Dining
 *
 *
 *   created: 5/08/2012   Bob P.
 *
 *   last updated:            ******* keep this accurate *******
 *
 *      2/19/14   Added code to pull in a new dining_user value from the session object, which contains the ID for the user in the dining system. Allows us to differentiate dining users from a particular club.
 *      6/20/12   Delete all Dining users from the ForeTees staff table, then add them all.  This will
 *                automatically remove any Dining users that have been deleted from Dining.
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.util.regex.*;
import java.sql.*;

import com.foretees.common.ProcessConstants;
import com.foretees.common.Utilities;
import com.foretees.common.Connect;

public class Proshop_dining_sendEmail extends HttpServlet {


 final String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
 
 static String DINING_USER = ProcessConstants.DINING_USER;               // Dining username for Admin user from Dining System


 //*****************************************************
 // Process the initial request from Login or a Return 
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

     
   PreparedStatement pstmt = null;     
   PreparedStatement stmt = null;     
   ResultSet rs = null;
   
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
   String user = (String)session.getAttribute("user");
   String dining_user = "";
   
   try { dining_user = (String)session.getAttribute("dining_user"); } catch (Exception ignore) {}
   
   int activity_id = (Integer)session.getAttribute("activity_id");
   
   boolean isDining = user.equals(DINING_USER);
   
   Connection con = Connect.getCon(req);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<input type=button value=\"  Exit  \" onclick=\"window.close();\">");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   
   if (isDining == true) {       // if Dining Admin
       
      //
      //  First, we need to update the ForeTees staff_list db table to make sure it includes the Dining staff
      //
      Connection con_d = null;

      String fname = "";
      String mi = "";
      String lname = "";
      String email = "";
      String fullName = "";
      String staff_email = "";

      int organization_id = Utilities.getOrganizationId(con);      // get the Dining org id for this club (identifes the dining database)
 
      if (organization_id > 0) {
          
          try {

             con_d = Connect.getDiningCon();

             if (con_d != null) {
                 
                 //
                 //  First, remove all Dining users from the ForeTees staff table
                 //
                 pstmt = con.prepareStatement (
                     "DELETE FROM staff_list WHERE activity_id = ?");

                 pstmt.clearParameters();         // clear the parms
                 pstmt.setInt(1, activity_id);
                 pstmt.executeUpdate();
                 
                 //  
                 //  Now - add all users from Dining - 
                 // SQL to query all users from the users table in dining
                 // note: this sql is dependant upon roles.role_id value of 6 always being the identifer for club members
                 String sql = "" +
                           "SELECT p.first_name, p.middle_name, p.last_name, p.email_address, r.name AS role_name " +
                           "FROM people p " +
                           "LEFT OUTER JOIN users AS u ON p.id = u.person_id " +
                           "LEFT OUTER JOIN roles AS r ON u.role_id = r.id " +
                           "WHERE u.role_id <> 6 AND p.organization_id = ? " +
                           "ORDER BY p.last_name, p.first_name, p.middle_name ";

                 pstmt = con_d.prepareStatement (sql);

                 pstmt.clearParameters();         // clear the parms
                 pstmt.setInt(1, organization_id);

                 rs = pstmt.executeQuery();      // execute the prepared stmt

                 while (rs.next()) {

                     fname = rs.getString(1);
                     mi = rs.getString(2);
                     lname = rs.getString(3);
                     email = rs.getString(4);     // not using the role name at this point - might use as the title in staff list later
                     
                     // Get the member's full name.......

                     StringBuffer mem_name = new StringBuffer(fname);         // get first name

                     if (!mi.equals( "" ) && mi != null) {
                        mem_name.append(" ");
                        mem_name.append(mi);                                  // mi if present
                     }
                     mem_name.append(" " + lname);                            // last name

                     fullName = mem_name.toString();                          // convert to one string

                     //
                     //  add Dining user to staff table
                     //
                     stmt = con.prepareStatement (
                       "INSERT INTO staff_list " +
                       "(activity_id, name, short_name, title, address1, address2, receive_backups1, receive_news1, cc_on_emails1, " +
                       "receive_backups2, receive_news2, cc_on_emails2, tee_time_list) " +
                       "VALUES (?,?,?,?,?,'',0,0,0,0,0,0,0)");

                     stmt.clearParameters();        // clear the parms
                     stmt.setInt(1, activity_id);
                     stmt.setString(2, fullName);       
                     stmt.setString(3, "");       
                     stmt.setString(4, "F & B Staff");  
                     stmt.setString(5, email);       

                     stmt.executeUpdate();          // execute the prepared stmt

                     stmt.close();   // close the stmt                         

                 }   // end of WHILE dining staff

             }   // end of IF no connection

          } catch (Exception exc) {

              Utilities.logError("Proshop_dining_sendEmail: Error gathering the Dining users for club " + club + ", err=" + exc.toString());
              
          } finally {

              try { rs.close(); }
              catch (Exception ignore) {}

              try { pstmt.close(); }
              catch (Exception ignore) {}

              try { stmt.close(); }
              catch (Exception ignore) {}

              try { con_d.close(); }
              catch (Exception ignore) {}
          }

      }      // end of IF no organization id
          
      //
      //  Build the HTML page (secondary menu)
      //
      out.println(SystemUtils.HeadTitle("Dining Send Email Menu"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"darkBlue\" alink=\"darkBlue\" vlink=\"darkBlue\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<br>");
      out.println("<table border=\"1\" align=\"center\" cellpadding=\"5\" bgcolor=\"F5F5DC\">");

         out.println("<tr><td align=\"center\" valign=\"top\" bgcolor=\"336633\">");
         out.println("<font color=\"#FFFFFF\" size=\"3\">");
         out.println("<b>Dining Send Email Menu</b><br>");
         out.println("</font>");
         out.println("</td>");
         out.println("</tr>");

         out.println("<tr>");
         out.println("<td align=\"left\"><font size=\"2\"><br>");
         out.println("Use this to manage your email distribution lists and to send emails to members.");
         out.println("<br>");
         out.println("</font></td></tr>");

         out.println("<tr>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println("<br>");

         out.println("<a href=\"Send_email\">Send an Email to Members</a>");
         out.println("<br>");
         out.println("</font></td></tr>");

         out.println("<tr>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println("<br>");
         out.println("<a href=\"Add_distributionlist\">Create a New Distribution List</a>");
         out.println("<br>");
         out.println("</font></td>");
         out.println("</tr>");
         
         out.println("<tr>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println("<br>");
         out.println("<a href=\"Communication\">Manage Distribution Lists</a>");
         out.println("<br><br>");
         out.println("</font></td>");
         out.println("</tr>");
         
      out.println("</table>");
      
      out.println("<br><br><center>");
      out.println("<form><input type=\"button\" style=\"background:#8B8970\" Value=\" Return To Dining System \" onClick='self.close()' alt=\"Close\">");
      out.println("</form>");
      out.println("</center>");      
      out.println("</body></html>");
      out.close();

   } else {
          
       out.println(SystemUtils.HeadTitle("Access Error"));
       out.println("<BODY><CENTER><BR>");
       out.println("<BR><BR><H3>Access Error</H3>");
       out.println("<BR><BR>Sorry, there seems to have been a routing error.");
       out.println("<BR><BR>If problem persists, please contact customer support.");
       out.println("<BR><BR>");
       out.println("<a href=\"javascript:history.back(1)\">Return</a>");
       out.println("</CENTER></BODY></HTML>");
       out.close();
       return;
   }  
      

 }  // end of doGet

}
