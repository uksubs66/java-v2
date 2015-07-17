/***************************************************************************************     
 *   Unsubscribe.java:  This servlet will provide members with quick management of their
 * 				email preferences via links in outgoing emails.
 *
 *
 *
 *   called by:  External Links
 *
 *       11/22/13   Add additional subscription settings for members.
 *        8/19/09   Comment out the login page links (causing confusion with seamless clubs)
 *        5/19/09   Add iCal attachment options to form
 * 
 ***************************************************************************************
 */
    
import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.foretees.member.Member;
import com.foretees.common.FeedBack;
import com.foretees.common.StringEncrypter;
import com.foretees.common.Utilities;
import com.foretees.common.Connect;

public class Unsubscribe extends HttpServlet {
                           

 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 public void doGet(HttpServletRequest req, HttpServletResponse resp) {


    resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server
    resp.setContentType("text/html");

    PrintWriter out = null;

    try { out = resp.getWriter(); }
    catch (Exception ignore) {}

    Connection con = null;
    Statement stmt = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    HttpSession session = req.getSession(false);

    if (session == null) {

        out.println("<HTML>");
        out.println("<HEAD>");
        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
        out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
        out.println("<meta http-equiv=\"Content-Style-Type\" content=\"text/css\">");
        out.println("<TITLE>Access Error</TITLE></HEAD>");
        out.println("<BODY><CENTER>");
        out.println("<H2>Access Error - Please Read</H2>");
        out.println("Sorry, your session either timed out, you didn't login, or your computer does not allow the use of Cookies.");
        out.println("<BR><BR>This site requires the use of Cookies for security purposes.");
        out.println("<BR>We use them to verify your session and prevent unauthorized access.");
        out.println("<BR><BR>Please check your 'Privacy' settings, under 'Tools', 'Internet Options'");
        out.println("<BR>(for MS Internet Explorer).  This must be set to 'Medium High' or lower.");
        out.println("<BR><BR>If you have a firewall, please check its settings as well.");
        out.println("<BR><BR><HR width=\"500\">");
        out.println("<b>NOTE:</b> You must be logged in to access the ForeTees system. You cannot bookmark a page");
        out.println("<BR>within ForeTees and then return to it later without logging in.  If you access ForeTees");
        out.println("<BR>from your club web site, then you must login to the web site.  If you access ForeTees");
        out.println("<BR>directly, then you must do so through the ForeTees Login page.");
        out.println("<BR><HR width=\"500\"><BR>");
        out.println("If you have tried all of the above and still receive this message,");
        out.println("<BR>please email us at <a href=\"mailto:support@foretees.com\">support@foretees.com</a>.");
        out.println("<BR><b>Provide your name or member number, the name of your club and a detailed description of your problem.</b>");
        out.println("<BR>Thank you.");
        out.println("<BR><BR>");
        out.println("<a href=\"Logout\" target=\"_top\">Return</a>");
        out.println("<BR><BR>");
        out.println("<CENTER>Server: " + Common_Server.SERVER_ID + "</CENTER>");
        out.println("</CENTER></BODY></HTML>");
        out.close();
    }

    con = SystemUtils.getCon(session);            // get DB connection

    if (con == null) return;

    String user = (String)session.getAttribute("ext-user");
    String club = (String)session.getAttribute("club");



    // see if we are here to process unsubcribe someone from ForeTees Dining emails
    if (req.getParameter("dining") != null && req.getParameter("dining").equals("ft")) {

        doDiningPage(req, user, club, con, out);
        return;
    }


    // see if we are here to process changes to their email
    if (req.getParameter("process") != null) {

        doEmailUpdate(req, user, club, con, out);
    }


    //String memNum = req.getParameter("memNum");
    //String email = req.getParameter("email");
    int i = 0;
    int emailOpt = 0;
    int emailOpt2 = 0;
    int clubEmailOpt1 = 0;
    int clubEmailOpt2 = 0;
    int memEmailOpt1 = 0;
    int memEmailOpt2 = 0;
    int iCal1 = 0;
    int iCal2 = 0;
    String email1 = "";
    String email2 = "";
    String fullName = "";
    boolean rsync = false;

    // find out if this club is using roster sync
    try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT rsync FROM club5");

        if (rs.next()) rsync = (rs.getInt("rsync") == 1);

        stmt.close();

    } catch (Exception ignore) {

    } finally {

        try { rs.close(); }
        catch (SQLException ignored) {}

        try { stmt.close(); }
        catch (SQLException ignored) {}
    }

    // load up this members email info
    try {

        pstmt = con.prepareStatement("" +
                "SELECT emailOpt, emailOpt2, clubEmailOpt1, clubEmailOpt2, memEmailOpt1, memEmailOpt2, email, email2, iCal1, iCal2, " +
                "CONCAT(name_first, ' ', name_last) AS fullName " +
                "FROM member2b " +
                "WHERE username = ? AND inact = 0 AND billable = 1");
        
        pstmt.clearParameters();
        pstmt.setString(1, user);
        rs = pstmt.executeQuery();

        while (rs.next()) {

            emailOpt = rs.getInt("emailOpt");
            emailOpt2 = rs.getInt("emailOpt2");
            clubEmailOpt1 = rs.getInt("clubEmailOpt1");
            clubEmailOpt2 = rs.getInt("clubEmailOpt2");
            memEmailOpt1 = rs.getInt("memEmailOpt1");
            memEmailOpt2 = rs.getInt("memEmailOpt2");
            email1 = rs.getString("email");
            email2 = rs.getString("email2");
            iCal1 = rs.getInt("iCal1");
            iCal2 = rs.getInt("iCal2");
            fullName = rs.getString("fullName");

            i++;
        }

        pstmt.close();

    } catch (Exception e) {

        out.println("Unsubcribe Error: " + e.getMessage() );

    } finally {

         try { rs.close(); }
         catch (SQLException ignored) {}

         try { pstmt.close(); }
         catch (SQLException ignored) {}
    }

    // make sure there is only 1 matching member, or reject it
    if (i > 1) {

        out.println("<HTML><BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<p>&nbsp;</p><p>&nbsp;</p>");
        out.println("<p>Unable to isolate your member record.</p>");
        out.println("<p>Please log in to ForeTees to make changes to your member record.</p>");
        //out.println("<br><p><a href=\"http://web.foretees.com/" + club + "\">ForeTees Login</a></p>");
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }
    

    // output the page and form for the user to change their email settings
    out.println("<HTML><BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
    out.println("<hr width=\"40%\">");
    out.println("<br><h2 align=center>Email Preferences</h2><br>For " + fullName + "<br><br>");
    out.println("<form>");
    out.println("<input type=hidden name=process value=yes>");
    out.println("<table bgcolor=\"F5F5DC\" cellpadding=\"10\">");
    
      out.println("<tr>");
      out.println("<td>&nbsp;</td>");
      out.println("<td align=center><strong>Primary Email</strong></td>");
      out.println("<td align=center><strong>Secondary Email</strong></td>");

      out.println("</tr><tr>");      
      out.println("<td align=right>Email:</td>");
      if (rsync) {
            out.println("<td><strong>" + email1 +"<strong><input type=\"hidden\" name=\"email1\" value=\"" + email1 + "\">&nbsp;&nbsp;</td>");
      } else {
            out.println("<td><input type=\"text\" name=\"email1\" size=\"32\" maxlength=\"50\" value=\"" + email1 +"\">&nbsp;&nbsp;</td>");
      }
      if (rsync) {
            out.println("<td><strong>" + email2 +"<strong><input type=\"hidden\" name=\"email2\" value=\"" + email2 + "\">&nbsp;&nbsp;</td>");
      } else {
            out.println("<td><input type=\"text\" name=\"email2\" size=\"32\" maxlength=\"50\" value=\"" + email2 +"\">&nbsp;&nbsp;</td>");
      }

      out.println("</tr><tr>");      
      out.println("<td align=right>Include iCal Attachments:</td>");
      out.println("<td style=\"text-align:center;\"><input type=\"checkbox\" " + ((iCal1 == 1) ? "checked " : "") + "name=\"iCal1\" value=\"1\"></td>");
      out.println("<td style=\"text-align:center;\"><input type=\"checkbox\" " + ((iCal2 == 1) ? "checked " : "") + "name=\"iCal2\" value=\"1\"></td>");

      out.println("</tr><tr>");      
      if (club.equals("aronimink")) {
         out.println("<td align=right>Receive Event Registration Notifications:</td>");  
      } else {
         out.println("<td align=right>Receive Confirmation Emails:</td>");
      }
      out.println("<td style=\"text-align:center;\"><input type=\"checkbox\" " + ((emailOpt == 1) ? "checked " : "") + "name=\"emailOpt\" value=\"1\"></td>");
      out.println("<td style=\"text-align:center;\"><input type=\"checkbox\" " + ((emailOpt2 == 1) ? "checked " : "") + "name=\"emailOpt2\" value=\"1\"></td>");

      out.println("</tr><tr>");      
      out.println("<td align=right>Receive Club Communications:</td>");
      out.println("<td style=\"text-align:center;\"><input type=\"checkbox\" " + ((clubEmailOpt1 == 1) ? "checked " : "") + "name=\"clubEmailOpt1\" value=\"1\"></td>");
      out.println("<td style=\"text-align:center;\"><input type=\"checkbox\" " + ((clubEmailOpt2 == 1) ? "checked " : "") + "name=\"clubEmailOpt2\" value=\"1\"></td>");

      out.println("</tr><tr>");      
      out.println("<td align=right>Receive Emails From Other Members:</td>");
      out.println("<td style=\"text-align:center;\"><input type=\"checkbox\" " + ((memEmailOpt1 == 1) ? "checked " : "") + "name=\"memEmailOpt1\" value=\"1\"></td>");
      out.println("<td style=\"text-align:center;\"><input type=\"checkbox\" " + ((memEmailOpt2 == 1) ? "checked " : "") + "name=\"memEmailOpt2\" value=\"1\"></td>");
      out.println("</tr>");
    
    out.println("<tr><td align=center colspan=3><br><input type=submit value=' Update '></td></tr>");
    out.println("</form>");
    
    out.println("<tr><td colspan=3>");
    
    out.println("<p><br><b><u>Important Notice</u></b><br>" +
                "<font size=2>Notification emails include emails generated by tee time reservations, " +
                "signing up for an event, lottery or wait list<br>and scheduling a lessons.&nbsp; ");

    out.println("Please note that even if you opt out of these notification emails the<br>golf shop staff " +
                "can still send an email to you from within ForeTees.</p>");

    out.println("<p><b>BlackBerry Users:</b> If you are receiving empty emails on your BlackBerry but you can see the entire<br>" +
                "email on your computer then you need to turn off the iCal Attachments using the options above.</p>");

    if (rsync) {

        out.println("<p>" +
                "Since your club is synchronizing their member roster to our database each night, " +
                "we are unable to accept changes to your email address(es) since any changes " +
                "we make on your behalf would be overwritten the next time your club syncs their roster to us. " +
                "Please contact your club directly and have them update your information for you.  " +
                "The changes done by your club will automatically appear in ForeTees the next day.</p>");
    }
    out.println("</td></tr></table>");

    //out.println("<br><p>");
    //out.println("<form action=\"#\" onsubmit=\"self.close()\"><input type=\"submit\" value=\"Exit - Close Window\" style=\"text-decoration:underline; background:#8B8970\" onClick='self.close();'></form></p>");
    out.println("</font></CENTER></BODY></HTML>");
    out.close();

    return;

 }


 public void doDiningPage(HttpServletRequest req, String user, String club, Connection con, PrintWriter out) {


    // see if we are here to process changes to their email
    if (req.getParameter("process") != null) {

        doDiningEmailUpdate(req, user, club, con, out);
    }

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    String email = "";
    String fullname = Utilities.getFullNameFromUsername(user, con);   // get user's full name
    int person_id = Utilities.getPersonId(user, con);
    int organization_id = Utilities.getOrganizationId(con);

    boolean emailOpt_dining = false;
    boolean emailOpt_events = false;

    Connection con_d = Connect.getDiningCon();

    if (con_d == null) {

        out.println(SystemUtils.HeadTitle("DB Connection Error"));
        out.println("<BODY><CENTER>");
        out.println("<BR><BR><H3>Database Connection Error</H3>");
        out.println("<BR><BR>Unable to connect to the Dining Database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<BR><BR>");
        //out.println("<a href=\"Member_announce\">Home</a>");
        out.println("</CENTER></BODY></HTML>");
        return;
    }

    // load up this members email info
    try {

        pstmt = con_d.prepareStatement("" +
                "SELECT email_address, auto_email_for_dining_reservations, auto_email_for_event_reservations " +
                "FROM people " +
                "WHERE id = ? AND organization_id = ?");
        pstmt.clearParameters();
        pstmt.setInt(1, person_id);
        pstmt.setInt(2, organization_id);

        rs = pstmt.executeQuery();

        if (rs.next()) {

            email = rs.getString("email_address");
            emailOpt_dining = rs.getBoolean("auto_email_for_dining_reservations");
            emailOpt_events = rs.getBoolean("auto_email_for_event_reservations");

        }

        pstmt.close();

    } catch (Exception e) {

        out.println("Error looking up email options: " + e.toString() );

    } finally {

         try { rs.close(); }
         catch (Exception ignored) {}

         try { pstmt.close(); }
         catch (Exception ignored) {}
    }


    // output the page and form for the user to change their email settings
    out.println("<HTML><BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
    out.println("<hr width=\"40%\">");
    out.println("<br><h2 align=center>Email Preferences</h2><br>");
    out.println("<form>");
    out.println("<input type=hidden name=process value=yes>");
    out.println("<input type=hidden name=dining value=ft>");
    out.println("<table>");
    out.println("<tr>");
    out.println("<td>Member Name: </td><td>" + fullname + "</td>");
    out.println("</tr>");
    out.println("<tr>");
    out.println("<td>Member Email: </td><td>" + email + "</td>");
    out.println("</tr>");
    out.println("<tr>");
    out.println("<td>Receive Dining (ala carte) Confirmation Emails: </td><td><select name=emailOpt_dining size=1><option value=0" + ((!emailOpt_dining) ? " selected" : "") + ">No<option value=1" + ((emailOpt_dining) ? " selected" : "") + ">Yes</select></td>");
    out.println("</tr>");
    out.println("<tr>");
    out.println("<td>Receive Dining Event Confirmation Emails: </td><td><select name=emailOpt_events size=1><option value=0" + ((!emailOpt_events) ? " selected" : "") + ">No<option value=1" + ((emailOpt_events) ? " selected" : "") + ">Yes</select></td>");
    out.println("</tr>");

    out.println("<tr><td align=center colspan=2><br><input type=submit value=' Update '></td></tr>");
    out.println("</form></table>");

    out.println("<p><b><u>Important Notice</u></b><br>" +
                "<font size=2>Notification emails include emails generated by Dining and Dining Event Reservations.&nbsp; ");

    out.println("Please note that even if you opt out of these notification emails the<br>club staff " +
                "can still send an email to you from within ForeTees.</p>");

    out.println("</font></CENTER></BODY></HTML>");
    out.close();
    return;
}


 public void doDiningEmailUpdate(HttpServletRequest req, String user, String club, Connection con, PrintWriter out) {


    PreparedStatement pstmt = null;

    int i = 0;
    int person_id = Utilities.getPersonId(user, con);
    int organization_id = Utilities.getOrganizationId(con);

    boolean emailOpt_dining = false;
    boolean emailOpt_events = false;

    String replyMsg = "";

    Connection con_d = Connect.getDiningCon();

    if (con_d == null) {

        out.println(SystemUtils.HeadTitle("DB Connection Error"));
        out.println("<BODY><CENTER>");
        out.println("<BR><BR><H3>Database Connection Error</H3>");
        out.println("<BR><BR>Unable to connect to the Dining Database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<BR><BR>");
        //out.println("<a href=\"Member_announce\">Home</a>");
        out.println("</CENTER></BODY></HTML>");
        return;
    }

    if (req.getParameter("emailOpt_dining") != null) {

        emailOpt_dining = (req.getParameter("emailOpt_dining").equals("1"));
    }
    if (req.getParameter("emailOpt_events") != null) {

        emailOpt_events = (req.getParameter("emailOpt_events").equals("1"));
    }

    //
    // Update member record
    //
    try {

        pstmt = con.prepareStatement( "" +
                "UPDATE people " +
                "SET auto_email_for_dining_reservations = ?, emaauto_email_for_event_reservations = ? " +
                "WHERE id = ? AND organization_id = ?");
        pstmt.clearParameters();
        pstmt.setBoolean(1, emailOpt_dining);
        pstmt.setBoolean(2, emailOpt_events);
        pstmt.setInt(3, person_id);
        pstmt.setInt(4, organization_id);

        i = pstmt.executeUpdate();

        pstmt.close();

    } catch (Exception ignore) {

        replyMsg = "<p>We encountered an error while attempting to update your member record.</p>";

    } finally {

         try { pstmt.close(); }
         catch (SQLException ignored) {}
    }


    out.println("<HTML><BODY><CENTER><img src=\"/" +rev+ "/assets/images/ft_dininglogo_hdr_blue.png\"><BR>");
    out.println("<hr width=\"40%\">");
    out.println("<p>&nbsp;</p><p>&nbsp;</p>");

    if (i == 0) {

        if (replyMsg.equals("")) {

            out.println("<p>Unable to update your member record.</p>");
            out.println("<p>Please contact the staff at your club to make changes to your member record.</p>");

        } else {

            out.println(replyMsg);
            out.println("<p>Please contact the staff at your club to make changes to your member record.</p>");

        }

    } else {

        out.println("<p>Your member record has been updated.</p>");

    }

    //out.println("<br><p>");
    //out.println("<form action=\"#\" onsubmit=\"self.close()\"><input type=\"submit\" value=\"Exit - Close Window\" style=\"text-decoration:underline; background:#8B8970\" onClick='self.close();'></form></p>");
    out.println("</CENTER></BODY></HTML>");
    out.close();

    return;

 }


 public void doEmailUpdate(HttpServletRequest req, String user, String club, Connection con, PrintWriter out) {

    Member member = new Member();

    PreparedStatement pstmt = null;

    String replyMsg = "";
    String email = "";
    String email2 = "";
    int emailOpt = 0;
    int emailOpt2 = 0;
    int clubEmailOpt1 = 0;
    int clubEmailOpt2 = 0;
    int memEmailOpt1 = 0;
    int memEmailOpt2 = 0;
    int iCal1 = 0;
    int iCal2 = 0;
    int i = 0;

    if (req.getParameter("email1") != null) {

        email = req.getParameter("email1");
    }
    if (req.getParameter("email2") != null) {

        email2 = req.getParameter("email2");
    }

    if (req.getParameter("iCal1") != null) iCal1 = Integer.parseInt(req.getParameter("iCal1"));
   
    if (req.getParameter("iCal2") != null) iCal2 = Integer.parseInt(req.getParameter("iCal2"));
   
    if (req.getParameter("emailOpt") != null) emailOpt = Integer.parseInt(req.getParameter("emailOpt"));
   
    if (req.getParameter("emailOpt2") != null) emailOpt2 = Integer.parseInt(req.getParameter("emailOpt2"));
   
    if (req.getParameter("clubEmailOpt1") != null) clubEmailOpt1 = Integer.parseInt(req.getParameter("clubEmailOpt1"));
   
    if (req.getParameter("clubEmailOpt2") != null) clubEmailOpt2 = Integer.parseInt(req.getParameter("clubEmailOpt2"));
   
    if (req.getParameter("memEmailOpt1") != null) memEmailOpt1 = Integer.parseInt(req.getParameter("memEmailOpt1"));
   
    if (req.getParameter("memEmailOpt2") != null) memEmailOpt2 = Integer.parseInt(req.getParameter("memEmailOpt2"));
      
   
    email = email.trim();              // remove any spaces
    email2 = email2.trim();            // remove any spaces


    //
    //  Verify the email address(es)
    //
    if (!email.equals( "" )) {                   // if specified

        FeedBack feedback = (member.isEmailValid(email));

        if (!feedback.isPositive()) {
            replyMsg = "The first email address you entered is not valid and has not been added to the system.";
            email = "";
        }
    }

    if (!email2.equals( "" )) {                    // if specified

        FeedBack feedback = (member.isEmailValid(email2));

        if (!feedback.isPositive()) {
            replyMsg = "The second email address you entered is not valid and has not been added to the system.";
            email2 = "";
        }
    }


    //
    // Update member record
    //
    try {

        pstmt = con.prepareStatement("" +
                   "UPDATE member2b " +
                   "SET email = ?, email2 = ?, emailOpt = ?, emailOpt2 = ?, clubEmailOpt1 = ?, clubEmailOpt2 = ?, " +
                   "memEmailOpt1 = ?, memEmailOpt2 = ?, iCal1 = ?, iCal2 = ? " +
                   "WHERE username = ?" );
        
        pstmt.clearParameters();
        pstmt.setString(1, email);
        pstmt.setString(2, email2);
        pstmt.setInt(3, emailOpt);
        pstmt.setInt(4, emailOpt2);
        pstmt.setInt(5, clubEmailOpt1);
        pstmt.setInt(6, clubEmailOpt2);
        pstmt.setInt(7, memEmailOpt1);
        pstmt.setInt(8, memEmailOpt2);
        pstmt.setInt(9, iCal1);
        pstmt.setInt(10, iCal2);
        pstmt.setString(11, user);

        i = pstmt.executeUpdate();

        pstmt.close();

    } catch (Exception ignore) {

        replyMsg = "<p>Sorry, we encountered an error while attempting to update your member record.</p>";

    } finally {

         try { pstmt.close(); }
         catch (SQLException ignored) {}
    }

    
    out.println("<HTML><BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
    out.println("<hr width=\"40%\">");
    out.println("<p>&nbsp;</p><p>&nbsp;</p>");

    if (i == 0) {

        if (replyMsg.equals("")) {

            out.println("<p>Unable to update your member record.</p>");
            out.println("<p>Please log in to ForeTees to make changes to your member record.</p>");

        } else {

            out.println(replyMsg);
            out.println("<p>Please log in to ForeTees to make changes to your member record.</p>");

        }

    } else {

        out.println("<p>Thank you, your email preferences have been updated.</p>");
        
    }

    //out.println("<br><p>");
    //out.println("<form action=\"#\" onsubmit=\"self.close()\"><input type=\"submit\" value=\"Exit - Close Window\" style=\"text-decoration:underline; background:#8B8970\" onClick='self.close();'></form></p>");
    out.println("</CENTER></BODY></HTML>");
    out.close();

    return;

 }

}