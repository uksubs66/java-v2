/***************************************************************************************     
 *   Proshop_services:  This servlet will process the 'change password' request from
 *                      Proshop's services page.
 *
 *
 *   called by:  proshop_services.htm
 *
 *   created: 12/05/2001   Bob P.
 *
 *   last updated:
 *
 *        3/29/10   Minor code clean up
 *       10/17/04   Enhancements for Version 5 - move html file to doGet processing for menus.
 *        7/18/03   Enhancements for Version 3 of the software.
 *        9/18/02   Enhancements for Version 2 of the software.
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class Proshop_services extends HttpServlet {
                       

 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //
 // Process the call from the menu
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    HttpSession session = SystemUtils.verifyPro(req, out);           // check for intruder

    if (session == null) {

        return;
    }

    String user = (String)session.getAttribute("user");              // get user
    String templott = (String)session.getAttribute("lottery");       // get lottery support indicator
    int lottery = Integer.parseInt(templott);

    //
    //   output the html page
    //
    out.println(SystemUtils.HeadTitle("Proshop Services"));

    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    SystemUtils.getProshopSubMenu(req, out, lottery);                // required to allow submenus on this page
    out.println("<br><font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
    out.println("<center>");

    out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
    out.println("<tr>");                                                              
     out.println("<td align=\"center\" valign=\"top\" bgcolor=\"#336633\">");

      out.println("<font color=\"#FFFFFF\" size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");
       out.println("<p align=\"center\">Use this page to change the password for the '" + user + "' account.<br><br>Click on 'Submit' to process the change.</p>");
      out.println("</font>");
     out.println("</td>");
    out.println("</tr>");
    out.println("<tr>");
     out.println("<td align=\"center\"><br>");
      out.println("<font color=\"#FFFFFF\" size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_services\" method=\"post\" target=\"bot\" name=\"f\">");

       out.println("<table border=\"0\" cellpadding=\"5\" align=\"center\">");
       out.println("<tr><td align=\"right\">Old Password:</td>    <td><input type=\"password\" name=\"oldpw\"  size=\"12\" maxlength=\"10\"></td></tr>");
       out.println("<tr><td align=\"right\">New Password:</td>    <td><input type=\"password\" name=\"newpw\"  size=\"12\" maxlength=\"10\"></td></tr>");
       out.println("<tr><td align=\"right\">Confirm Password:</td><td><input type=\"password\" name=\"newpwc\" size=\"12\" maxlength=\"10\"></td></tr>");
       out.println("</table>");
       out.println("<br>");

       out.println("<input type=\"submit\" value=\" Update \">");
      out.println("</form></font>");
            
      out.println("</td>");
     out.println("</tr>");
    out.println("</table><br>");

    out.println("<font size=\"2\">");
    out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
    out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
    out.println("</form>");

    out.println("</CENTER></font></BODY></HTML>");
    out.close();
 }


 //
 // Process the form request from above
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    //
    // Make sure user didn't enter illegally
    //
    HttpSession session = SystemUtils.verifyPro(req, out);  // check for intruder

    if (session == null) {

        return;
    }

    String user = (String)session.getAttribute("user");     // get username
    String club = (String)session.getAttribute("club");     // get club name

    Connection con = SystemUtils.getCon(session);           // get DB connection

    if (con == null) {

        // Error connecting to db....
        out.println(SystemUtils.HeadTitle("DB Connection Error"));
        out.println("<BODY><CENTER>");
        out.println("<BR><BR><H3>Database Connection Error</H3>");
        out.println("<BR><BR>Unable to connect to the Database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<BR><BR>");
        out.println("<a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
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

    int pw_length = new_password.length();                  // length of new password

    try {

        pstmt = con.prepareStatement (
                "SELECT password FROM login2 WHERE username = ?");

        pstmt.clearParameters();
        pstmt.setString(1, user);
        rs = pstmt.executeQuery();

        if (rs.next()) curr_password = rs.getString("password");

   } catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("DB Access Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H1>Database Access Error 1</H1>");
      out.println("<BR><BR>Unable to process database change at this time.");
      out.println("<BR>Exception: "+ exc.getMessage());
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    //
    // Verify the passwords
    //
    if (old_password.equals( curr_password ) &&
        new_password.equals( conf_password ) &&
        pw_length > 3 && pw_length < 11) {

        if (club.startsWith( "demo" )) {        // do not change pw if demo site

            out.println(SystemUtils.HeadTitle("Proshop Password Changed"));
            out.println("<BODY><CENTER>");
            out.println("<BR><BR><H3>New Password Accepted</H3>");
            out.println("<BR><BR>Thank you.  For demo purposes the password has not been changed.");
            out.println("<BR><BR><BR><BR>");
            out.println("<font size=\"2\">");
            out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_services\">");
            out.println("<input type=\"submit\" value=\"Return to Settings\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form>");
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("&nbsp;&nbsp;&nbsp;or &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
            out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");
            out.println("</CENTER></BODY></HTML>");

        } else {

            try {

               pstmt = con.prepareStatement (
                     "UPDATE login2 SET password = ? WHERE username = ?");

               pstmt.clearParameters();
               pstmt.setString(1, new_password);
               pstmt.setString(2, user);
               pstmt.executeUpdate();

               out.println(SystemUtils.HeadTitle("Proshop Password Changed"));
               out.println("<BODY><CENTER><BR>");
               out.println("<BR><BR><H2>Password Has Been Changed</H2>");
               out.println("<BR><BR>The password you just specified must be entered the next time you login.");
               out.println("<BR><BR>");
               out.println("<font size=\"2\">");
               out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_services\">");
               out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</form></font>");
               out.println("</CENTER></BODY></HTML>");

            } catch (Exception exc) {

                out.println(SystemUtils.HeadTitle("DB Access Error"));
                out.println("<BODY><CENTER><BR>");
                out.println("<BR><BR><H1>Database Access Error 2</H1>");
                out.println("<BR><BR>Unable to process database change at this time.");
                out.println("<BR>Exception: "+ exc.getMessage());
                out.println("<BR>Please try again later.");
                out.println("<BR><BR>If problem persists, contact customer support.");
                out.println("<BR><BR>");
                out.println("<a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
                out.println("</CENTER></BODY></HTML>");
            } finally {

                try { rs.close(); }
                catch (Exception ignore) {}

                try { pstmt.close(); }
                catch (Exception ignore) {}

            }

        } // end of if demo site

    } else {

      // old password is wrong or new passwords didn't match or new password has an invalid length
      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H1>Data Entry Error</H1>");
      out.println("<BR><BR>The data you entered is incorrect.");
      out.println("<BR><BR>The Old Password must equal your Current Password that you logged in with.");
      out.println("<BR>The New Password must be at least 4 characters but no more than 10 characters.");
      out.println("<BR>Avoid special characters as some are not accepted.");
      out.println("<BR><BR>Please try again.");
      out.println("<BR>If problem persists, please contact customer support.");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_services\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
   }

 }
    
}
