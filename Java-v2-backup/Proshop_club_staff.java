/***************************************************************************************     
 *   Proshop_club_staff:  This servlet will process the Club Contact List configuration.
 *
 *
 *   called by:  the Club Options config page to build or edit the contacts
 *
 *   created: 6/08/2010   Bob P.
 *
 *   last updated:
 *
 *
 *   6/28/12  Add Invoice options for email address items.
 *   2/22/11  Updated to filter staff list by sess_activity_id, include a different set of selectable titles when on FlxRez, and replace Golf terminology
 *
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

// foretees imports
import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.common.Utilities;
import com.foretees.common.FeedBack;
import com.foretees.common.getActivity;
import com.foretees.member.Member;
import com.foretees.member.MemberHelper;
import com.foretees.common.Connect;

public class Proshop_club_staff extends HttpServlet {

    
   String zero = "00";
   String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

  
 //************************************************************************
 //
 // Process the initial request from Proshop config - Club Options
 //
 //  doGet:  Get control to list all current contacts (opens in new window!)
 //
 //************************************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   PreparedStatement pstmt = null;
   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

   if (session == null) {
     
      return;
   }

   Connection con = Connect.getCon(req);                      // get DB connection

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
   
   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_CLUBCONFIG", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_CLUBCONFIG", out);
   }
   
   
   int sess_activity_id = 0;

   try { sess_activity_id = (Integer)session.getAttribute("activity_id"); }
   catch (Exception ignore) { }

   
   String club = (String)session.getAttribute("club");      // get club name

   
   //String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   //int lottery = Integer.parseInt(templott);

   //
   // Define some parms to use in the html
   //
   int staff_id = 0;             // record id
   int activity_id = 0;
   int tee_time_list = 0;
   int email_bounced1 = 0;
   int email_bounced2 = 0;
       
   String name = ""; 
   String short_name = "";
   String title = "";
   String address1 = "";
   String address2 = "";
   String teelist = ""; 
   
   boolean b = false;


   //
   //  Build the HTML page to display the existing contacts
   //
   out.println(SystemUtils.HeadTitle("Proshop Contact List Page"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   //SystemUtils.getProshopSubMenu(req, out, lottery);        // not needed when opened in new window
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");

      out.println("<table cellpadding=\"5\" border=\"0\" bgcolor=\"#336633\">");
      out.println("<tr><td align=\"center\">");
      out.println("<font color=\"#FFFFFF\" size=\"3\">");
      out.println("<b>Club Staff and Options</b><br>");
      out.println("</font>");
      out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("<br>Use this utility to add or configure all club staff and/or members that you want included.");
      out.println("<br>Use the option settings to specify which contacts are to receive backup " + (sess_activity_id == 0 ? "tee" : "time") + " sheets and newsletters.");
      out.println("<br>If the Staff Id List feature is being used, then be sure to specify a short name (must be unique).");
      out.println("<br><b>Note:</b> If an email address is highlighted, then we have been unable to send to it.");
      out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Please update the record and correct the email address.");
      out.println("<br>");
      out.println("</font></td></tr></table><br><br>");

      out.println("<table border=\"2\" cellpadding=\"5\" width=\"820\">");
      out.println("<tr bgcolor=\"#8B8970\" style=\"text-align:center; font-size:12px;\"><td>");
      out.println("<p><b>Full Name</b></p>");
      out.println("</td>");
      out.println("<td>");
      out.println("<p><b>Short Name</b></p>");
      out.println("</td>");
      out.println("<td>");
      out.println("<p><b>Title</b></p>");
      out.println("</td>");
      out.println("<td>");
      out.println("<p><b>Email 1</b></p>");
      out.println("</td>");
      out.println("<td>");
      out.println("<p><b>Email 2</b></p>");
      out.println("</td>");
      out.println("<td>");
      out.println("<p><b>In ID List?</b></p>");
      out.println("</td>");
      out.println("<td>");
      out.println("<p>&nbsp;</p>");      // empty for select button
      out.println("</td></tr>");

   //
   //  Get and display the existing contacts
   //
   try {

      pstmt = con.prepareStatement("SELECT * FROM staff_list WHERE activity_id = ? ORDER BY name");        // create a statement
      pstmt.clearParameters();
      pstmt.setInt(1, sess_activity_id);

      rs = pstmt.executeQuery();

      while (rs.next()) {

         b = true;                     // indicate notices exist

         staff_id = rs.getInt("staff_id");
         activity_id = rs.getInt("activity_id");      // not used initially
         name = rs.getString("name");
         short_name = rs.getString("short_name");
         title = rs.getString("title");
         address1 = rs.getString("address1");
         address2 = rs.getString("address2");
         email_bounced1 = rs.getInt("email_bounced1");
         email_bounced2 = rs.getInt("email_bounced2");
         tee_time_list = rs.getInt("tee_time_list");
         
         
         //
         //  some values must be converted for display
         //
         teelist = "No";
         
         if (tee_time_list == 1) {
            teelist = "Yes";
         }


         out.println("<tr bgcolor=\"#F5F5DC\" style=\"text-align:center; font-size:11px;\">");
         out.println("<form method=\"post\" action=\"Proshop_club_staff\">");
         out.println("<td><p>" +name+ "</p></td>");
         out.println("<td><p>" +short_name+ "</p></td>");
         out.println("<td><p>" + title + "</p></td>");
         if (email_bounced1 > 0) {
            out.println("<td bgcolor=\"yellow\">");
         } else {
            out.println("<td>");
         }
         out.println("<p>" + address1 + "</p></td>");
         if (email_bounced2 > 0) {
            out.println("<td bgcolor=\"yellow\">");
         } else {
            out.println("<td>");
         }
         out.println("<p>" + address2 + "</p></td>");
         out.println("<td><p>" + teelist + "</p></td>");

         out.println("<input type=\"hidden\" name=\"staff_id\" value=\"" + staff_id + "\">");
         out.println("<input type=\"hidden\" name=\"staff_name\" value=\"" + name + "\">");
         out.println("<td>");
         out.println("<input type=\"submit\" name=\"edit\" value=\"Update\">&nbsp;&nbsp;");
         out.println("<input type=\"submit\" name=\"delete\" value=\"Delete\">");
         out.println("</td></form></tr>");

      }  // end of while loop

      pstmt.close();

      out.println("</table></font>");                   // end of staff_list table
      
      if (!b) {
        
         out.println("<p>No Staff Records Currently Exist</p>");
      }

      out.println("<form method=\"post\" action=\"Proshop_club_staff\">");
      out.println("<p align=\"center\"><input type=\"submit\" name=\"add\" value=\"Add New Entry\"></p>");
      
   }
   catch (Exception e2) {

      out.println("<BR><BR><H1>Database Access Error</H1>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR><BR>Exception: " + e2);
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      //out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
      out.println("<BR><BR><input type=button value=\"  Close  \" onclick=\"window.close();\">");

   }  // end of try

   //
   //  End of HTML page
   //
   out.println("</td></tr></table>");                // end of main page table
   
   out.println("<input type=button value=\"  Done  \" onclick=\"window.close();\">");
  
   out.println("</center></font></body></html>");

 }  // end of doGet



 //
 //**********************************************************************************
 //
 //  doPost:  Get control from above to Update or Delete an existing staff contact,
 //           or to add a new contact.
 //
 //         This is called from a new window opened from Club Options config page.
 //
 //**********************************************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Statement stmt = null;
   ResultSet rs = null;
     
   HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

   if (session == null) {

      return;
   }

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

   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_MEMBERNOTICES", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_MEMBERNOTICES", out);
   }

   //String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   //int lottery = Integer.parseInt(templott);
   
   
   int sess_activity_id = 0;

   try { sess_activity_id = (Integer)session.getAttribute("activity_id"); }
   catch (Exception ignore) { }

   
   String club = (String)session.getAttribute("club");      // get club name

   
   //
   // If request is to delete a record, then go prompt to make sure, or delete it if confirmed
   //
   if (req.getParameter("delete") != null) {
     
      doDelete(req, out, con);        // go process the delete request
      return;                              
   }
   

   //
   //  Must be Update request or Add request
   //
   int staff_id = 0;            
   int activity_id = 0;
   int tee_time_list = 0;
   int email_bounced1 = 0;
   int email_bounced2 = 0;
   int receive_backups1 = 0;
   int receive_news1 = 0;
   int cc_on_emails1 = 0;
   int receive_backups2 = 0;
   int receive_news2 = 0;
   int cc_on_emails2 = 0;
   int invoice_golf1 = 0;         // Invoice Options - send notices when an invoice is ready for this activity         
   int invoice_flxrez1 = 0;         
   int invoice_dining1 = 0;         
   int invoice_golf2 = 0;                
   int invoice_flxrez2 = 0;         
   int invoice_dining2 = 0;     
       
   String name = ""; 
   String short_name = "";
   String title = "";
   String address1 = "";
   String address2 = "";
   String teelist = ""; 
   String temp = "";
   String activity_name = "";
   
   boolean add_contact = false;

   
   //
   // Get the record id, if present call is for edit (if not, then its an 'add')
   //
   if (req.getParameter("staff_id") != null) {
     
      temp = req.getParameter("staff_id");
      
      try {
         staff_id = Integer.parseInt(temp);
      }
      catch (NumberFormatException e) {
      }
      
   } else {
      
      add_contact = true;         // must be an add
   }

   
   //
   // If this is the 2nd step (user submitted a change or new record), then go process it
   //
   if (req.getParameter("step2") != null) {
      
      doUpdate(add_contact, staff_id, sess_activity_id, req, out, con);      // go process the update request
      return;            
   }

   activity_name = getActivity.getActivityName(sess_activity_id, con);
   
   //
   //  Check for systems supported for this club
   //
   boolean golf_mode = getActivity.isGolfEnabled(con);              // is Golf supported?
   boolean flxrez_mode = getActivity.isConfigured(con);             // is FlxRez supported?
   boolean dining_mode = (Utilities.getOrganizationId(con) != 0);   // is Dining supported?
   
   //
   //  If Update requested, then get the current values for the id that was passed
   //
   if (add_contact == false) {

      try {

         PreparedStatement pstmt = con.prepareStatement (
                  "SELECT * FROM staff_list WHERE staff_id = ?");

         pstmt.clearParameters();      
         pstmt.setInt(1, staff_id);     
         rs = pstmt.executeQuery();    

         if (rs.next()) {

            staff_id = rs.getInt("staff_id");
            activity_id = rs.getInt("activity_id");      // not used initially
            name = rs.getString("name");
            short_name = rs.getString("short_name");
            title = rs.getString("title");
            address1 = rs.getString("address1");
            address2 = rs.getString("address2");
            email_bounced1 = rs.getInt("email_bounced1");
            email_bounced2 = rs.getInt("email_bounced2");
            receive_backups1 = rs.getInt("receive_backups1");
            receive_news1 = rs.getInt("receive_news1");
            cc_on_emails1 = rs.getInt("cc_on_emails1");
            receive_backups2 = rs.getInt("receive_backups2");
            receive_news2 = rs.getInt("receive_news2");
            cc_on_emails2 = rs.getInt("cc_on_emails2");
            tee_time_list = rs.getInt("tee_time_list");
//            invoice_golf1 = rs.getInt("invoice_golf1");
//            invoice_flxrez1 = rs.getInt("invoice_flxrez1");
//            invoice_dining1 = rs.getInt("invoice_dining1");
//            invoice_golf2 = rs.getInt("invoice_golf2");
//            invoice_flxrez2 = rs.getInt("invoice_flxrez2");
//            invoice_dining2 = rs.getInt("invoice_dining2");
         }
         pstmt.close();              // close the stmt
           
      }                                         
      catch (Exception exc) {

         dbError(out, exc);
         return;
      }
   }      // end of IF Update      
      

   //
   //  Output the form page to add or edit a record
   //
   out.println(SystemUtils.HeadTitle("Proshop Edit Staff List"));

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   //SystemUtils.getProshopSubMenu(req, out, lottery);        // not needed when opened in new window
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
              
         out.println("<table cellpadding=\"5\" border=\"1\" bgcolor=\"#336633\" align=\"center\">");
         out.println("<tr><td align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
           
         if (add_contact == false) {
            out.println("<b>Edit Staff Record</b><br>");
            out.println("<br>Change the desired information in the staff record below.");
            out.println("<br>Click on <b>Update</b> to submit the changes.");
            out.println("<br>Click on <b>Cancel</b> to abort any changes and return.");
         } else {
            out.println("<b>Add Staff Member Record</b><br>");
            out.println("<br>Set the desired information for the staff record below.<br>");
            out.println("Click on <b>Add</b> to create the new notice.");
            out.println("<br>Click on <b>Cancel</b> to abort any changes and return.");
         }
         out.println("</font></td></tr></table><br>");

         out.println("<table cellpadding=\"5\" border=\"1\" bgcolor=\"#F5F5DC\" width=\"600\">");
            out.println("<form action=\"Proshop_club_staff\" method=\"post\">");
            if (add_contact == false) {
               out.println("<input type=\"hidden\" name=\"staff_id\" value=\"" + staff_id + "\">");  // staff to edit
            }
            out.println("<input type=\"hidden\" name=\"step2\" value=\"yes\">");        // indicate this is 2nd step
            
                        
            out.println("<tr style=\"font-size:11px;\"><td align=\"right\">");
            out.println("<b>Full Name</b> ('first last' or 'first mi last'):&nbsp;&nbsp;");
            out.println("</td><td align=\"left\">");
            out.println("&nbsp;&nbsp;<input type=\"text\" name=\"staff_name\" value=\"" +name+ "\" size=\"30\" maxlength=\"40\">");
            out.println("</td></tr>");

            out.println("<tr style=\"font-size:11px;\"><td align=\"right\">");
            out.println("<b>Short Name</b> (initials or other common name):&nbsp;&nbsp;");
            out.println("</td><td align=\"left\">");
            out.println("&nbsp;&nbsp;<input type=\"text\" name=\"short_name\" value=\"" +short_name+ "\" size=\"12\" maxlength=\"12\">");
            out.println("</td></tr>");

            out.println("<tr style=\"font-size:11px;\"><td align=\"right\">");
            out.println("<b>Title</b> (select one from list):&nbsp;&nbsp;");
            out.println("</td><td align=\"left\">");
            out.println("&nbsp;&nbsp;<select size=\"1\" name=\"title\">");

            if (sess_activity_id == 0) {

                if (title.equals( "Assistant Golf Professional" )) {
                   out.println("<option selected value=\"Assistant Golf Professional\">Assistant Golf Professional</option>");
                } else {
                   out.println("<option value=\"Assistant Golf Professional\">Assistant Golf Professional</option>");
                }
                if (title.equals( "Bag Room Attendant" )) {
                   out.println("<option selected value=\"Bag Room Attendant\">Bag Room Attendant</option>");
                } else {
                   out.println("<option value=\"Bag Room Attendant\">Bag Room Attendant</option>");
                }
                if (title.equals( "Board Member" )) {
                   out.println("<option selected value=\"Board Member\">Board Member</option>");
                } else {
                   out.println("<option value=\"Board Member\">Board Member</option>");
                }
                if (title.equals( "Caddie Master" )) {
                   out.println("<option selected value=\"Caddie Master\">Caddie Master</option>");
                } else {
                   out.println("<option value=\"Caddie Master\">Caddie Master</option>");
                }
                if (title.equals( "CFO" )) {
                   out.println("<option selected value=\"CFO\">CFO</option>");
                } else {
                   out.println("<option value=\"CFO\">CFO</option>");
                }
                if (title.equals( "Controller" )) {
                   out.println("<option selected value=\"Controller\">Controller</option>");
                } else {
                   out.println("<option value=\"Controller\">Controller</option>");
                }
                if (title.equals( "Director of Golf" )) {             
                   out.println("<option selected value=\"Director of Golf\">Director of Golf</option>");
                } else {
                   out.println("<option value=\"Director of Golf\">Director of Golf</option>");
                }
                if (title.equals( "F & B Manager" )) {
                   out.println("<option selected value=\"F & B Manager\">F & B Manager</option>");
                } else {
                   out.println("<option value=\"F & B Manager\">F & B Manager</option>");
                }
                if (title.equals( "F & B Staff" )) {
                   out.println("<option selected value=\"F & B Staff\">F & B Staff</option>");
                } else {
                   out.println("<option value=\"F & B Staff\">F & B Staff</option>");
                }
                if (title.equals( "General Manager" )) {
                   out.println("<option selected value=\"General Manager\">General Manager</option>");
                } else {
                   out.println("<option value=\"General Manager\">General Manager</option>");
                }
                if (title.equals( "Golf Committee Member" )) {
                   out.println("<option selected value=\"Golf Committee Member\">Golf Committee Member</option>");
                } else {
                   out.println("<option value=\"Golf Committee Member\">Golf Committee Member</option>");
                }
                if (title.equals( "Golf Shop Staff" )) {
                   out.println("<option selected value=\"Golf Shop Staff\">Golf Shop Staff</option>");
                } else {
                   out.println("<option value=\"Golf Shop Staff\">Golf Shop Staff</option>");
                }
                if (title.equals( "Head Golf Professional" )) {
                   out.println("<option selected value=\"Head Golf Professional\">Head Golf Professional</option>");
                } else {
                   out.println("<option value=\"Head Golf Professional\">Head Golf Professional</option>");
                }
                if (title.equals( "IT Specialist" )) {
                   out.println("<option selected value=\"IT Specialist\">IT Specialist</option>");
                } else {
                   out.println("<option value=\"IT Specialist\">IT Specialist</option>");
                }
                if (title.equals( "Membership Director" )) {
                   out.println("<option selected value=\"Membership Director\">Membership Director</option>");
                } else {
                   out.println("<option value=\"Membership Director\">Membership Director</option>");
                }
                if (title.equals( "Starter" )) {
                   out.println("<option selected value=\"Starter\">Starter</option>");
                } else {
                   out.println("<option value=\"Starter\">Starter</option>");
                }
                if (title.equals( "Website Administrator" )) {
                   out.println("<option selected value=\"Website Administrator\">Website Administrator</option>");
                } else {
                   out.println("<option value=\"Website Administrator\">Website Administrator</option>");
                }
                if (title.equals( "Other" )) {
                   out.println("<option selected value=\"Other\">Other</option>");
                } else {
                   out.println("<option value=\"Other\">Other</option>");
                }
                
            } else {        // Not a golf user
               
                if (title.equals( "Assistant " + activity_name + " Professional" )) {
                   out.println("<option selected value=\"Assistant " + activity_name + " Professional\">Assistant " + activity_name + " Professional</option>");
                } else {
                   out.println("<option value=\"Assistant " + activity_name + " Professional\">Assistant " + activity_name + " Professional</option>");
                }
                if (title.equals( "Board Member" )) {
                   out.println("<option selected value=\"Board Member\">Board Member</option>");
                } else {
                   out.println("<option value=\"Board Member\">Board Member</option>");
                }
                if (title.equals( "CFO" )) {
                   out.println("<option selected value=\"CFO\">CFO</option>");
                } else {
                   out.println("<option value=\"CFO\">CFO</option>");
                }
                if (title.equals( "Controller" )) {
                   out.println("<option selected value=\"Controller\">Controller</option>");
                } else {
                   out.println("<option value=\"Controller\">Controller</option>");
                }
                if (title.equals( "Director of " + activity_name)) {
                   out.println("<option selected value=\"Director of " + activity_name + "\">Director of " + activity_name + "</option>");
                } else {
                   out.println("<option value=\"Director of " + activity_name + "\">Director of " + activity_name + "</option>");
                }
                if (title.equals( "General Manager" )) {
                   out.println("<option selected value=\"General Manager\">General Manager</option>");
                } else {
                   out.println("<option value=\"General Manager\">General Manager</option>");
                }
                if (title.equals( "" + activity_name + " Committee Member" )) {
                   out.println("<option selected value=\"" + activity_name + " Committee Member\">" + activity_name + " Committee Member</option>");
                } else {
                   out.println("<option value=\"" + activity_name + " Committee Member\">" + activity_name + " Committee Member</option>");
                }
                if (title.equals( "" + activity_name + " Shop Staff" )) {
                   out.println("<option selected value=\"" + activity_name + " Shop Staff\">" + activity_name + " Shop Staff</option>");
                } else {
                   out.println("<option value=\"" + activity_name + " Shop Staff\">" + activity_name + " Shop Staff</option>");
                }
                if (title.equals( "Head " + activity_name + " Professional" )) {
                   out.println("<option selected value=\"Head " + activity_name + " Professional\">Head " + activity_name + " Professional</option>");
                } else {
                   out.println("<option value=\"Head " + activity_name + " Professional\">Head " + activity_name + " Professional</option>");
                }
                if (title.equals( "IT Specialist" )) {
                   out.println("<option selected value=\"IT Specialist\">IT Specialist</option>");
                } else {
                   out.println("<option value=\"IT Specialist\">IT Specialist</option>");
                }
                if (title.equals( "Membership Director" )) {
                   out.println("<option selected value=\"Membership Director\">Membership Director</option>");
                } else {
                   out.println("<option value=\"Membership Director\">Membership Director</option>");
                }
                if (title.equals( "Website Administrator" )) {
                   out.println("<option selected value=\"Website Administrator\">Website Administrator</option>");
                } else {
                   out.println("<option value=\"Website Administrator\">Website Administrator</option>");
                }
                if (title.equals( "Other" )) {
                   out.println("<option selected value=\"Other\">Other</option>");
                } else {
                   out.println("<option value=\"Other\">Other</option>");
                }
            }
            
            out.println("</select>");
            out.println("</td></tr>");

            out.println("<tr style=\"font-size:11px;\"><td align=\"right\" valign=\"top\">");            
            out.println("<b>Email Address 1</b>:&nbsp;&nbsp;");
            out.println("<br><br><br>");
            out.println("Options For This Email Address:&nbsp;&nbsp;<br>");            
            out.println("</td><td align=\"left\">");
            out.println("&nbsp;&nbsp;<input type=\"text\" name=\"address1\" value=\"" +address1+ "\" size=\"40\" maxlength=\"50\">");
            out.println("<br><br>");
            if (receive_backups1 > 0) {    
               out.println("&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"backups1\" value=\"1\">&nbsp;&nbsp;Send <b>Backup " + (sess_activity_id == 0 ? "Tee" : "Time") + " Sheets</b> to this Address");
            } else {
               out.println("&nbsp;&nbsp;<input type=\"checkbox\" name=\"backups1\" value=\"1\">&nbsp;&nbsp;Send <b>Backup " + (sess_activity_id == 0 ? "Tee" : "Time") + " Sheets</b> to this Address");
            }
            if (receive_news1 > 0) {    
               out.println("<br>&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"news1\" value=\"1\">&nbsp;&nbsp;Send ForeTees <b>Newsletters</b> to this Address");
            } else {
               out.println("<br>&nbsp;&nbsp;<input type=\"checkbox\" name=\"news1\" value=\"1\">&nbsp;&nbsp;Send ForeTees <b>Newsletters</b> to this Address");
            }
            if (cc_on_emails1 > 0) {    
               out.println("<br>&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"ccemails1\" value=\"1\">&nbsp;&nbsp;<b>CC this Address</b> on All Emails Sent to Members");
            } else {
               out.println("<br>&nbsp;&nbsp;<input type=\"checkbox\" name=\"ccemails1\" value=\"1\">&nbsp;&nbsp;<b>CC this Address</b> on All Emails Sent to Members");
            }
            /*
            if (golf_mode) {
               if (invoice_golf1 > 0) {    
                  out.println("<br>&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"inv_golf1\" value=\"1\">&nbsp;&nbsp;Send <b>ForeTees Invoice Notices</b> to this address");
               } else {
                  out.println("<br>&nbsp;&nbsp;<input type=\"checkbox\" name=\"inv_golf1\" value=\"1\">&nbsp;&nbsp;Send <b>ForeTees Invoice Notices</b> to this address");
               }
            }
            if (flxrez_mode) {
               if (invoice_flxrez1 > 0) {    
                  out.println("<br>&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"inv_flx1\" value=\"1\">&nbsp;&nbsp;Send <b>FlxRez Invoice Notices</b> to this address");
               } else {
                  out.println("<br>&nbsp;&nbsp;<input type=\"checkbox\" name=\"inv_flx1\" value=\"1\">&nbsp;&nbsp;Send <b>FlxRez Invoice Notices</b> to this address");
               }
            }
            if (dining_mode) {
               if (invoice_dining1 > 0) {    
                  out.println("<br>&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"inv_dining1\" value=\"1\">&nbsp;&nbsp;Send <b>Dining Invoice Notices</b> to this address");
               } else {
                  out.println("<br>&nbsp;&nbsp;<input type=\"checkbox\" name=\"inv_dining1\" value=\"1\">&nbsp;&nbsp;Send <b>Dining Invoice Notices</b> to this address");
               }
            }
            * 
            */
            out.println("</td></tr>");

            out.println("<tr style=\"font-size:11px;\"><td align=\"right\" valign=\"top\">");            
            out.println("&nbsp;&nbsp;<b>Email Address 2</b>:");
            out.println("<br><br><br>");
            out.println("Options For This Email Address:&nbsp;&nbsp;<br>");            
            out.println("</td><td align=\"left\">");
            out.println("&nbsp;&nbsp;<input type=\"text\" name=\"address2\" value=\"" +address2+ "\" size=\"40\" maxlength=\"50\">");
            out.println("<br><br>");
            if (receive_backups2 > 0) {    
               out.println("&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"backups2\" value=\"1\">&nbsp;&nbsp;Send <b>Backup " + (sess_activity_id == 0 ? "Tee" : "Time") + " Sheets</b> to this Address");
            } else {
               out.println("&nbsp;&nbsp;<input type=\"checkbox\" name=\"backups2\" value=\"1\">&nbsp;&nbsp;Send <b>Backup " + (sess_activity_id == 0 ? "Tee" : "Time") + " Sheets</b> to this Address");
            }
            if (receive_news2 > 0) {    
               out.println("<br>&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"news2\" value=\"1\">&nbsp;&nbsp;Send ForeTees <b>Newsletters</b> to this Address");
            } else {
               out.println("<br>&nbsp;&nbsp;<input type=\"checkbox\" name=\"news2\" value=\"1\">&nbsp;&nbsp;Send ForeTees <b>Newsletters</b> to this Address");
            }
            if (cc_on_emails2 > 0) {    
               out.println("<br>&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"ccemails2\" value=\"1\">&nbsp;&nbsp;<b>CC this Address</b> on All Emails Sent to Members");
            } else {
               out.println("<br>&nbsp;&nbsp;<input type=\"checkbox\" name=\"ccemails2\" value=\"1\">&nbsp;&nbsp;<b>CC this Address</b> on All Emails Sent to Members");
            }
            /*
            if (golf_mode) {
               if (invoice_golf2 > 0) {    
                  out.println("<br>&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"inv_golf2\" value=\"1\">&nbsp;&nbsp;Send <b>ForeTees Invoice Notices</b> to this address");
               } else {
                  out.println("<br>&nbsp;&nbsp;<input type=\"checkbox\" name=\"inv_golf2\" value=\"1\">&nbsp;&nbsp;Send <b>ForeTees Invoice Notices</b> to this address");
               }
            }
            if (flxrez_mode) {
               if (invoice_flxrez2 > 0) {    
                  out.println("<br>&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"inv_flx2\" value=\"1\">&nbsp;&nbsp;Send <b>FlxRez Invoice Notices</b> to this address");
               } else {
                  out.println("<br>&nbsp;&nbsp;<input type=\"checkbox\" name=\"inv_flx2\" value=\"1\">&nbsp;&nbsp;Send <b>FlxRez Invoice Notices</b> to this address");
               }
            }
            if (dining_mode) {
               if (invoice_dining2 > 0) {    
                  out.println("<br>&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"inv_dining2\" value=\"1\">&nbsp;&nbsp;Send <b>Dining Invoice Notices</b> to this address");
               } else {
                  out.println("<br>&nbsp;&nbsp;<input type=\"checkbox\" name=\"inv_dining2\" value=\"1\">&nbsp;&nbsp;Send <b>Dining Invoice Notices</b> to this address");
               }
            }
            * 
            */
            out.println("</td></tr>");

            out.println("<tr style=\"font-size:11px;\"><td align=\"right\">");
            out.println("Include this person in <b>name list for identification:</b>&nbsp;&nbsp;<BR> (when making or changing " + (sess_activity_id == 0 ? "tee times" : "reservations") + ")&nbsp;&nbsp;&nbsp;&nbsp;<BR>");
            out.println("</td><td align=\"left\">");            
            if (tee_time_list > 0) {    
               out.println("&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"teetimelist\" value=\"1\">&nbsp;&nbsp;(requires a unique Short Name)");
            } else {
               out.println("&nbsp;&nbsp;<input type=\"checkbox\" name=\"teetimelist\" value=\"1\">&nbsp;&nbsp;(requires a unique Short Name)");
            }
            out.println("</td></tr>");
         out.println("</table>");
      
         out.println("<p align=\"center\">");
         if (add_contact == false) {
            out.println("<input type=\"submit\" name=\"Update\" value=\"Update\">");
         } else {
            out.println("<input type=\"submit\" name=\"Add\" value=\"Add\">");
         }
         out.println("</form>");
                 
      out.println("</td></tr></table>");                       // end of main page table
  
   out.println("<font size=\"2\"><BR>");
   out.println("<form method=\"get\" action=\"Proshop_club_staff\">");
   out.println("<input type=\"submit\" value=\"Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
    

   out.println("</center></font></body></html>");
   out.close();

 }   // end of doPost   


 // ***************************************************************************
 //  Process the Delete request from doGet
 // ***************************************************************************

 private void doDelete(HttpServletRequest req, PrintWriter out, Connection con) {

   PreparedStatement stmt = null;

   String name = "";         // name of notice
   String temp = "";

   int staff_id = 0;
     
   boolean b = false;

   //
   // Get the record id
   //
   if (req.getParameter("staff_id") != null) {

      temp = req.getParameter("staff_id");

      try {
         staff_id = Integer.parseInt(temp);
      }
      catch (NumberFormatException e) {
      }
   }

   
   out.println(SystemUtils.HeadTitle("Proshop Staff List Page"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   out.println("<center><font face=\"Arial, Helvetica, Sans-serif\">");

   //
   // Prompt user to confirm if not already done
   //
   if (req.getParameter("step2") == null) {
      
      if (req.getParameter("staff_name") != null) {
      
         name = req.getParameter("staff_name");
      }
      
      //
      //  Prompt user to confirm
      //
      out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr><td align=\"center\" colspan=2>");
      out.println("<font size=\"2\">");
      out.println("<br><br>");
      out.println("You are about to remove the following record: " +name+ ".<br>");
      out.println("Are you sure you want to remove this person's record?");
      out.println("<br><br>");
      out.println("</font></td></tr>");
      out.println("<tr><td align=\"center\">");
      out.println("<form method=\"get\" action=\"Proshop_club_staff\">");
      out.println("<input type=\"submit\" value=\"No - Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></td><td align=\"center\">");
      out.println("<form action=\"Proshop_club_staff\" method=\"post\">");
      out.println("<input type=\"hidden\" name=\"delete\" value=\"yes\">");    
      out.println("<input type=\"hidden\" name=\"step2\" value=\"yes\">");    
      out.println("<input type=\"hidden\" name=\"staff_id\" value=\"" +staff_id+ "\">");    
      out.println("<input type=\"submit\" value=\"Yes - Continue\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font></td></tr></table>");
      
   } else {

      //
      //  Confirmation received - Delete this record
      //
      try {

         stmt = con.prepareStatement (
                  "Delete FROM staff_list WHERE staff_id = ?");

         stmt.clearParameters();               // clear the parms
         stmt.setInt(1, staff_id);
         stmt.executeUpdate();

         stmt.close();

         out.println("<table border=\"0\" align=\"center\">");
         out.println("<tr><td align=\"center\">");
         out.println("<font size=\"2\">");
         out.println("<br><br>");
         out.println("Thank you.  The record has been removed.<br>");
         out.println("<br><br>");
         out.println("</font></td></tr>");
         out.println("<tr><td align=\"center\">");
         out.println("<form action=\"Proshop_club_staff\" method=\"get\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font></td></tr></table>");
         
      }
      catch (Exception exc) {

         out.println("<BR><BR><H1>Database Access Error</H1>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");

      }  // end of try      
   }
   
   out.println("</center></font></body></html>");
   out.close();
   
 }


 // ***************************************************************************
 //  Process the add or edit request from doPost 
 // ***************************************************************************

 private void doUpdate(boolean add_contact, int staff_id, int activity_id, HttpServletRequest req, PrintWriter out, Connection con) {

   PreparedStatement stmt = null;
   ResultSet rs = null;

   Member member = new Member();


   String name = "";
   String short_name = "";
   String title = "";
   String address1 = "";
   String address2 = "";
   String msg = "";

   int backups1 = 0;
   int news1 = 0;
   int ccemails1 = 0;
   int backups2 = 0;
   int news2 = 0;
   int ccemails2 = 0;
   int teetimelist = 0;
   int invoice_golf1 = 0;         // Invoice Options - send notices when an invoice is ready for this activity         
   int invoice_flxrez1 = 0;         
   int invoice_dining1 = 0;         
   int invoice_golf2 = 0;                
   int invoice_flxrez2 = 0;         
   int invoice_dining2 = 0;         
   
   boolean error = false;


   //
   //  User wishes to edit the record - get parms passed
   //
   if (req.getParameter("staff_name") != null) {

      name = req.getParameter("staff_name");
   }
   
   if (req.getParameter("short_name") != null) {

      short_name = req.getParameter("short_name");      
   }
   
   if (req.getParameter("title") != null) {

      title = req.getParameter("title");      
   }
   
   if (req.getParameter("address1") != null) {

      address1 = req.getParameter("address1");      
   }
   
   if (req.getParameter("address2") != null) {

      address2 = req.getParameter("address2");      
   }
   
   if (req.getParameter("teetimelist") != null) {     // see if any checkboxes were selected

      teetimelist = 1;      
   }
   
   if (req.getParameter("backups1") != null) {

      backups1 = 1;      
   }
   
   if (req.getParameter("backups2") != null) {

      backups2 = 1;      
   }
   
   if (req.getParameter("news1") != null) {

      news1 = 1;      
   }
   
   if (req.getParameter("news2") != null) {

      news2 = 1;      
   }
   
   if (req.getParameter("ccemails1") != null) {

      ccemails1 = 1;      
   }
   
   if (req.getParameter("ccemails2") != null) {

      ccemails2 = 1;      
   }
   
   if (req.getParameter("inv_golf1") != null) {

      invoice_golf1 = 1;      
   }
   if (req.getParameter("inv_golf2") != null) {

      invoice_golf2 = 1;      
   }
   if (req.getParameter("inv_flx1") != null) {

      invoice_flxrez1 = 1;      
   }
   if (req.getParameter("inv_flx2") != null) {

      invoice_flxrez2 = 1;      
   }
   if (req.getParameter("inv_dining1") != null) {

      invoice_dining1 = 1;      
   }
   if (req.getParameter("inv_dining2") != null) {

      invoice_dining2 = 1;      
   }
   
   
   //
   //  verify the entry values
   //
   if (address1.equals("") && (backups1 == 1 || news1 == 1 || ccemails1 == 1 || invoice_golf1 == 1 || invoice_flxrez1 == 1 || invoice_dining1 == 1)) {

      msg = "Sorry, you cannot select an email option without providing an email address (#1).";       // error message
      invData(msg, out);    // inform the user and return
      return;
   }
   if (address2.equals("") && (backups2 == 1 || news2 == 1 || ccemails2 == 1 || invoice_golf2 == 1 || invoice_flxrez2 == 1 || invoice_dining2 == 1)) {

      msg = "Sorry, you cannot select an email option without providing an email address (#2).";       // error message
      invData(msg, out);    // inform the user and return
      return;
   }

   //
   //  Verify the email addresses
   //
   if (!address1.equals("")) {
      
      address1 = address1.trim();           // remove spaces

      FeedBack feedback = (member.isEmailValid(address1));

      if (!feedback.isPositive()) {    // if error

         msg = "Sorry, the email address you provided (" +address1+ ") is not a valid email address.";       // error message
         invData(msg, out);    // inform the user and return
         return;        
      }      
   }
   if (!address2.equals("")) {
      
      address2 = address2.trim();           // remove spaces

      FeedBack feedback = (member.isEmailValid(address2));

      if (!feedback.isPositive()) {    // if error

         msg = "Sorry, the email address you provided (" +address2+ ") is not a valid email address.";       // error message
         invData(msg, out);    // inform the user and return
         return;        
      }      
   }
   
   if (teetimelist == 1) {

      if (short_name.equals("")) {

         msg = "Sorry, you must specify a value in the Short Name field in order for this person to be included in the identification list for " + (activity_id == 0 ? "tee time" : "reservation") + " updates.";       // error message
         invData(msg, out);    // inform the user and return
         return;
         
      } else {
         
         //
         //   make sure the short name is unique when they want to use the tee time id feature
         //
         try {
            
            if (add_contact == true) {         // if this is an add

               stmt = con.prepareStatement (
                        "SELECT staff_id FROM staff_list WHERE short_name = ?");

               stmt.clearParameters();      
               stmt.setString(1, short_name);     
               rs = stmt.executeQuery();    

               if (rs.next()) {

                  error = true;
               }
               stmt.close();              // close the stmt
               
            } else {
               
               stmt = con.prepareStatement (
                        "SELECT staff_id FROM staff_list WHERE staff_id != ? AND short_name = ?");

               stmt.clearParameters();      
               stmt.setInt(1, staff_id);     
               stmt.setString(2, short_name);     
               rs = stmt.executeQuery();    

               if (rs.next()) {

                  error = true;
               }
               stmt.close();              // close the stmt               
            }

         }                                         
         catch (Exception exc) {

            dbError(out, exc);
            return;
         }
         
         if (error == true) {
            
            msg = "Sorry, the Short Name must be unique if you wish to use the identification list for " + (activity_id == 0 ? "tee time" : "reservation") + " updates.";       // error message
            invData(msg, out);    // inform the user and return
            return;
         }
      }
   }
   
   //
   //  Always use address1 if only one email address provided
   //
   if (address1.equals("") && !address2.equals("")) {
   
      address1 = address2;
      backups1 = backups2;
      news1 = news2;
      ccemails1 = ccemails2;
      
      address2 = "";
      backups2 = 0;
      news2 = 0;
      ccemails2 = 0;
   }
   

   //
   //  entry ok - add it to the database or update it
   //
   try {

      if (add_contact == true) {         // if this is an add

         stmt = con.prepareStatement (
           "INSERT INTO staff_list " +
           "(activity_id, name, short_name, title, address1, address2, receive_backups1, receive_news1, cc_on_emails1, " +
           "receive_backups2, receive_news2, cc_on_emails2, tee_time_list) " +
           "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
//           "invoice_golf1, invoice_flxrez1, invoice_dining1, invoice_golf2, invoice_flxrez2, invoice_dining2) " +
//           "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

         stmt.clearParameters();        // clear the parms
         stmt.setInt(1, activity_id);
         stmt.setString(2, name);       
         stmt.setString(3, short_name);       
         stmt.setString(4, title);  
         stmt.setString(5, address1);       
         stmt.setString(6, address2);      
         stmt.setInt(7, backups1);
         stmt.setInt(8, news1);
         stmt.setInt(9, ccemails1);
         stmt.setInt(10, backups2);
         stmt.setInt(11, news2);
         stmt.setInt(12, ccemails2);
         stmt.setInt(13, teetimelist);
//         stmt.setInt(14, invoice_golf1);
//         stmt.setInt(15, invoice_flxrez1);
//         stmt.setInt(16, invoice_dining1);
//         stmt.setInt(17, invoice_golf2);
//         stmt.setInt(18, invoice_flxrez2);
//         stmt.setInt(19, invoice_dining2);

         stmt.executeUpdate();          // execute the prepared stmt

         stmt.close();   // close the stmt
         
      } else {
       
         //
         //  Update the record (always clear the email bounced flags)
         //
         stmt = con.prepareStatement (
           "UPDATE staff_list SET name = ?, short_name = ?, title = ?, address1 = ?, address2 = ?, " +
           "email_bounced1 = 0, email_bounced2 = 0, receive_backups1 = ?, receive_news1 = ?, cc_on_emails1 = ?, " +
           "receive_backups2 = ?, receive_news2 = ?, cc_on_emails2 = ?, tee_time_list = ? " +
//           "invoice_golf1 = ?, invoice_flxrez1 = ?, invoice_dining1 = ?, invoice_golf2 = ?, invoice_flxrez2 = ?, invoice_dining2 = ? " +
           "WHERE staff_id = ?");

         stmt.clearParameters();        // clear the parms         
         stmt.setString(1, name);       
         stmt.setString(2, short_name);       
         stmt.setString(3, title);  
         stmt.setString(4, address1);       
         stmt.setString(5, address2);      
         stmt.setInt(6, backups1);
         stmt.setInt(7, news1);
         stmt.setInt(8, ccemails1);
         stmt.setInt(9, backups2);
         stmt.setInt(10, news2);
         stmt.setInt(11, ccemails2);
         stmt.setInt(12, teetimelist);
         stmt.setInt(13, staff_id);
//         stmt.setInt(14, invoice_golf1);
//         stmt.setInt(15, invoice_flxrez1);
//         stmt.setInt(16, invoice_dining1);
//         stmt.setInt(17, invoice_golf2);
//         stmt.setInt(18, invoice_flxrez2);
//         stmt.setInt(19, invoice_dining2);
         
         stmt.executeUpdate();          

         stmt.close();   // close the stmt
      }

   }
   catch (Exception exc) {

      dbError(out, exc);
      return;
   }

   //
   // Database updated - inform user
   //
   out.println(SystemUtils.HeadTitle("Proshop Add Staff Member"));
   out.println("<BODY>");
   out.println("<CENTER><BR>");
   if (add_contact == true) {         // if this is an add
      out.println("<BR><BR><H3>Staff Record Has Been Added</H3>");
      out.println("<BR><BR>Thank you, the Staff Record has been added to the system database.");
   } else {
      out.println("<BR><BR><H3>Staff Record Has Been Updated</H3>");
      out.println("<BR><BR>Thank you, the Staff Record has been updated in the system database.");
   }
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"Proshop_club_staff\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }



 // *********************************************************
 // Database Error
 // *********************************************************

 private void dbError(PrintWriter out, Exception exc) {

   out.println(SystemUtils.HeadTitle("Database Error"));
   out.println("<BODY><CENTER><BR>");
   out.println("<BR><BR><H3>Database Access Error</H3>");
   out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
   out.println("<BR>Exception: " + exc);
   out.println("<BR>Please try again later.");
   out.println("<BR><BR>If problem persists, contact customer support.");
   out.println("<BR><BR>");
   out.println("<input type=button value=\"  Exit  \" onclick=\"window.close();\">");
   out.println("</CENTER></BODY></HTML>");

 }

 // *********************************************************
 // Missing or invalid data entered...
 // *********************************************************

 private void invData(String msg, PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>" + msg + "<BR>");
   out.println("<BR>Please try again.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
 }

 // *********************************************************
 // Notice already exists
 // *********************************************************

 private void dupMem(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, the <b>name</b> you specified already exists in the database.<BR>");
   out.println("<BR>Please change the name to a unique value.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
 }

}
