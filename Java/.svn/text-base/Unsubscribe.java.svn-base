/***************************************************************************************     
 *   Unsubscribe.java:  This servlet will provide members with quick management of their
 * 				email preferences via links in outgoing emails.
 *
 *
 *
 *   called by:  External Links
 *
 *        8/19/09   Commen t out the login page links (causing confusion with seamless clubs)
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
        out.println("<a href=\"/" +rev+ "/servlet/Logout\" target=\"_top\">Return</a>");
        out.println("<BR><BR>");
        out.println("<CENTER>Server: " + Common_Server.SERVER_ID + "</CENTER>");
        out.println("</CENTER></BODY></HTML>");
        out.close();
    }

    con = SystemUtils.getCon(session);            // get DB connection

    if (con == null) return;

    String user = (String)session.getAttribute("ext-user");
    String club = (String)session.getAttribute("club");

    // see if we are here to process changes to their email
    if (req.getParameter("process") != null) {

        doEmailUpdate(req, user, club, con, out);
    }


    //String memNum = req.getParameter("memNum");
    //String email = req.getParameter("email");
    int i = 0;
    int emailOpt = 0;
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
                "SELECT emailOpt, email, email2, iCal1, iCal2, CONCAT(name_first, ' ', name_last) AS fullName " +
                "FROM member2b WHERE username = ? AND inact = 0 AND billable = 1");
        pstmt.clearParameters();
        pstmt.setString(1, user);
        rs = pstmt.executeQuery();

        while (rs.next()) {

            emailOpt = rs.getInt("emailOpt");
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
    out.println("<br><h2 align=center>Email Preferences</h2><br>");
    out.println("<form>");
    out.println("<input type=hidden name=process value=yes>");
    out.println("<table>");
    out.println("<tr>");
    out.println("<td>Member Name: </td><td>" + fullName + "</td>");
    out.println("</tr>");
    out.println("<tr>");
        out.println("<td>Receive Notifications Emails: </td><td><select name=emailOpt size=1><option value=0" + ((emailOpt == 0) ? " selected" : "") + ">No<option value=1" + ((emailOpt == 1) ? " selected" : "") + ">Yes</select></td>");
    out.println("</tr>");
    if (rsync) {

        out.println("<tr>");
            out.println("<td>Email #1 Address: </td><td>" + email1 + "<input type=hidden name=email1 value=\"" + email1 + "\"></td>");
        out.println("</tr>");
        out.println("<tr>");
            out.println("<td>Email #2 Address: </td><td>" + email2 + "<input type=hidden name=email2 value=\"" + email2 + "\"></td>");
        out.println("</tr>");

    } else {

        out.println("<tr>");
            out.println("<td>Email #1 Address: </td><td><input type=text name=email1 value=\"" + email1 + "\" size=42 maxlength=50></td>");
        out.println("</tr>");
        out.println("<tr>");
            out.println("<td>Email #2 Address: </td><td><input type=text name=email2 value=\"" + email2 + "\" size=42 maxlength=50></td>");
        out.println("</tr>");

    }

    out.println("<tr>");
        out.println("<td>Email #1 Attach iCal Files: </td><td><select name=iCal1 size=1><option value=0" + ((iCal1 != 1) ? " selected" : "") + ">No<option value=1" + ((iCal1 == 1) ? " selected" : "") + ">Yes</select></td>");
    out.println("</tr>");
    out.println("<tr>");
        out.println("<td>Email #2 Attach iCal Files: </td><td><select name=iCal2 size=1><option value=0" + ((iCal2 != 1) ? " selected" : "") + ">No<option value=1" + ((iCal2 == 1) ? " selected" : "") + ">Yes</select></td>");
    out.println("</tr>");

    out.println("<tr><td align=center colspan=2><br><input type=submit value=' Update '></td></tr>");
    out.println("</form></table>");

    out.println("<p><b><u>Important Notice</u></b><br>" +
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

    out.println("</font></CENTER></BODY></HTML>");
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
    int iCal1 = 0;
    int iCal2 = 0;
    int i = 0;

    if (req.getParameter("email1") != null) {

        email = req.getParameter("email1");
    }
    if (req.getParameter("email2") != null) {

        email2 = req.getParameter("email2");
    }
    if (req.getParameter("emailOpt") != null) {

        emailOpt = (req.getParameter("emailOpt").equals("1")) ? 1 : 0;
    }
    if (req.getParameter("iCal1") != null) {

        iCal1 = (req.getParameter("iCal1").equals("1")) ? 1 : 0;
    }
    if (req.getParameter("iCal2") != null) {

        iCal2 = (req.getParameter("iCal2").equals("1")) ? 1 : 0;
    }

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

        pstmt = con.prepareStatement( "UPDATE member2b SET email = ?, email2 = ?, emailOpt = ?, iCal1 = ?, iCal2 = ? WHERE username = ?" );
        pstmt.clearParameters();
        pstmt.setString(1, email);
        pstmt.setString(2, email2);
        pstmt.setInt(3, emailOpt);
        pstmt.setInt(4, iCal1);
        pstmt.setInt(5, iCal2);
        pstmt.setString(6, user);

        i = pstmt.executeUpdate();

        pstmt.close();

    } catch (Exception ignore) {

        replyMsg = "<p>We encountered an error while attempting to update your member record.</p>";

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

        out.println("<p>Your member record has been updated.</p>");
        
    }

    //out.println("<br><p><a href=\"http://web.foretees.com/" + club + "\">ForeTees Login</a></p>");
    out.println("</CENTER></BODY></HTML>");
    out.close();

    return;

 }

}