/***************************************************************************************
 *   Logout:  This servlet will process a logout request.
 *
 *   called by:  all to logout of system
 *
 *   created: 6/13/2002   Bob P.
 *
 *   last updated:
 * 
 *        9/12/13   Do not close the con and kill the session if proshop user exiting Member View (caller = proshop).
 *        3/28/13   Close the window after logout if site uses FT Connect (web site opens new window for FT).
 *        1/17/13   Add the Request object to outputBanner, outputSubNav, and outputPageEnd so we can get the session object to test caller.
 *       12/12/12   Check mobile_login session setting to determine if user came from mobile login page.
 *        5/14/12   New Skin - Ext Login updates - pull in new skin header (CSS) and close window on exit.
 *        1/15/12   New Skin updates.
 *        2/16/11   Add check for external login user (do not route to login page).
 *        9/10/10   Sawgrass CC (sawgrass) - If proshop user, redirect them to /sawgrass/proshop.jsp instead of the normal login page
 *        8/02/10   Fort Collins CC (fortcollins) - Updated customs to include Fox Hill CC
 *        1/17/10   Updated html output
 *        7/15/09   Add support for Mobile users.
 *        5/05/08   Add seesion.invalidate call
 *        5/10/07   Added serverId to connerror output
 *        2/07/07   Return to Greeley CC login page if user is from Greeley (they share the Fort Collins db).
 *        2/06/07   Change index2.htm to index.htm to allow for new login pages.
 *        3/06/06   Remove calls to SystemUtils.sessionLog - log the event here.
 *        9/22/05   Add a login log to track all logins and logouts.
 *        1/24/05   Ver 5 - change club2 to club5.
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

// foretees imports
import com.foretees.common.ProcessConstants;
import com.foretees.common.Utilities;

public class Logout extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 //*****************************************************
 // Perform doGet processing - someone is logging out
 //*****************************************************

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

    //
    //  Get user's session
    //
    HttpSession session = null;

    session = req.getSession(false);  // Get user's session object (no new one)

    if (session == null) {

        Connerror(out);              // go process connection error......
        return;
    }

    //
    //  Get the name of the club for user
    //
    String club = (String)session.getAttribute("club");       // get club name
    String caller = (String)session.getAttribute("caller");   // get caller's name
    String user = (String)session.getAttribute("user");       // get user
    
    boolean new_skin = false;
    
    if (session.getAttribute("new_skin") != null) {          // WON'T BE HERE FOR SUPPORT, ADMIN AND PROSHOP USERS !!!!!!!!
    
       new_skin = ((String) session.getAttribute("new_skin")).equals("1");
    }

    int sess_activity_id = 0;

    if (session.getAttribute("activity_id") != null) {          // WON'T BE HERE FOR SUPPORT USERS !!!!!!!!
    
       sess_activity_id = (Integer)session.getAttribute("activity_id");
    }

    int ftConnect = 0;                    // FT Connect club indicator

    if (session.getAttribute("ftConnect") != null) {   //  if ftConnect included in session (set in Login.memberUser only)

        ftConnect = (Integer)session.getAttribute("ftConnect");   // get value (1 = yes)
    }
    

    int mobile = 0;
    int mobile_login = 0;

    //
    //  See if Mobile user
    //
    try {

       mobile = (Integer)session.getAttribute("mobile");

    }
    catch (Exception ignore) {
        mobile = 0;
    }

    try {

       mobile_login = (Integer)session.getAttribute("mobile_login");    // user come from mobile login page?

    }
    catch (Exception ignore) {
        mobile_login = 0;
    }

    //
    //   Check for external login user (came from email link)
    //
    boolean ext_login = false;            // not external login (from email link)
   
    if ((String)session.getAttribute("ext-user") != null) {

       ext_login = true;        // member came from link in email message  (via Login.ProcessExtLogin)
    }

    
    
    String omit = "";
    String msg = "User Logout";

    if (club == null || club.equals( "" )) {

        Connerror(out);              // go process connection error......
        return;
    }

    //
    //  Release the connection if it exists
    //
    Connection con = null;

    //
    // Get the connection holder saved in the session object
    //
    ConnHolder holder = (ConnHolder) session.getAttribute("connect");

    if (holder != null) {

        con = holder.getConn();      // get the connection
    }
    
    String clubName = "";
    
    if (con != null) {
       
       clubName = Utilities.getClubName(con, true);        // get the full name of this club   
    }


    //
    //  If Fort Collins - check for Greeley user (must change club name for Greeley - they share).
    //
    if (club.equals( "fortcollins" )) {        // if normal foretees user

      if (user.startsWith( "proshop" ) || user.startsWith( "admin" ) || user.startsWith( "support" )) {    // if NOT member

         if (user.equalsIgnoreCase( "proshop4" ) || user.equalsIgnoreCase( "proshop5" )) {    // if Greeley Proshop user

            club = "greeleycc";

         } else if (user.equalsIgnoreCase("proshopfox")) {

            //club = "foxhill";
         }

      } else {        // must be member

         String mtype = (String)session.getAttribute("mtype");       // get mtype

         if (!mtype.equals( "" ) && mtype != null) {                 // if specified

            if (mtype.endsWith( "Greeley" )) {                // if Greeley member

               club = "greeleycc";

            } else if (mtype.endsWith("Fox Hill")) {

               //club = "foxhill";
            }
         }
      }
    }

    //
    //  Exit
    //
    if (caller.equals( "none" ) && ftConnect == 0) {        // if normal foretees user

        //
        //  Track all logouts
        //
        sessionLog(msg, user, omit, club, omit, con);

        if (mobile_login == 0) {        // was (if mobile == 0)

            if (new_skin) {     // WILL BE FALSE FOR ADMIN AND PROSHOP USERS !!!!!!!!!!!!!!!!!

               if (ext_login == false) {

                  // If Sawgrass and a proshop user, redirect to proshop.jsp instead
                  if (club.equals("sawgrass") && ProcessConstants.isProshopUser(user)) {
                     
                     Common_skin.outputHeader(club, sess_activity_id, "Logout", true, out, req, 2, "/" + club + "proshop.jsp");     // output the page start
                     
                  } else {
                     
                     Common_skin.outputHeader(club, sess_activity_id, "Logout", true, out, req, 2, "/" + club);     // output the page start
                  }
                  
               } else {    // External user (from email or other)
                   
                  Common_skin.outputHeader(club, sess_activity_id, "Logout", true, out, req, 0, "");     // output the page start - no refresh or URL to jump to                  
               }

               out.println("<body>");
               out.println("<div id=\"wrapper_login\" align=\"center\">");
               out.println("<div id=\"title\">" +clubName+ "</div>");
               out.println("<div id=\"main_login\" align=\"center\">");
               out.println("<div class=\"main_message\">");
               out.println("<h2>Logout Complete</h2><br /><br />");
               out.println("<center><div class=\"sub_instructions\">");

               out.println("<BR>Thank you for using ForeTees.");      
               out.println("<br /></div>");

               if (ext_login == false) {

                  // If Sawgrass and a proshop user, redirect to proshop.jsp instead
                  if (club.equals("sawgrass") && ProcessConstants.isProshopUser(user)) {
                      out.println("<form method=\"get\" action=\"/" + club + "/proshop.jsp\">");
                  } else {
                      out.println("<form method=\"get\" action=\"/" + club + "\">");
                  }

                  out.println("<input type=\"submit\" value=\"Continue\" id=\"submit\">");
                  
               } else {
                   
                   // Can't close a window when it didn't come from another window (in this case it came from an email link)
                 //  out.println("<form><input type=\"button\" style=\"background:#8B8970\" Value=\" Exit \" onClick='self.close()' alt=\"Close\">");            
               }
               out.println("</form></center></div></div>");

               Common_skin.outputPageEnd(club, sess_activity_id, out, req);    // finish the page       

            } else {     // admin and proshop users
      
               out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"");
               out.println("      \"http://www.w3.org/TR/html4/loose.dtd\">");
               out.println("<html>");
               out.println("<head>");
               out.println("<!-- SERVER " + Common_Server.SERVER_ID + " -->");

               if (ext_login == false) {

                  // If Sawgrass and a proshop user, redirect to proshop.jsp instead
                  if (club.equals("sawgrass") && ProcessConstants.isProshopUser(user)) {
                      out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" + club + "/proshop.jsp\">");
                  } else {
                      out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" + club + "\">");
                  }
               }
               out.println("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=iso-8859-1\">");
               out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
               out.println("<title>Logout Confirmation Page</title>");
               out.println("<base target=\"_top\">");
               out.println("</head>");
               out.println("<body>");
               out.println("<center><img src=\"/" +rev+ "/images/foretees.gif\" width=\"200\" height=\"72\" alt=\"\"><br>");
               out.println("<hr width=\"40%\">");
               out.println("<p>&nbsp;</p><br><br>");
               out.println("<h2>Logout Processed</h2><br>");
               out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"12\"><tr><td align=\"center\">");
               out.println("Thank you for using ForeTees.");
               out.println("</td></tr></table>");

               if (ext_login == false) {

                  out.println("<br><br><br>");

                  // If Sawgrass and a proshop user, redirect to proshop.jsp instead
                  if (club.equals("sawgrass") && ProcessConstants.isProshopUser(user)) {
                      out.println("<form method=\"get\" action=\"/" + club + "/proshop.jsp\">");
                  } else {
                      out.println("<form method=\"get\" action=\"/" + club + "\">");
                  }

                  out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
                  out.println("</form>");
               }
               out.println("</center>");
               out.println("</body>");
               out.println("</html>");
            }

        } else {    // mobile device

            
            out.println("<HTML xmlns='http://www.w3.org/1999/xhtml'><HEAD><Title>ForeTees Mobile Logout</Title>");
            out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" + club + "/mlogin.jsp\">");
            out.println("<meta name=\"viewport\" id=\"viewport\" content=\"width=device-width, user-scalable=no\">");
            out.println("<LINK REL=StyleSheet HREF=\"/" +rev+ "/mobile/style.css\" TYPE=\"text/css\" MEDIA=screen></HEAD>");
            out.println("<BODY><CENTER>");
            out.println("<img src=\"/v5/mobile/images/logo_large.gif\" alt=\"ForeTees\" /><br />");
            out.println("<img src=\"/v5/mobile/images/Mobile.gif\" alt=\"Mobile Version\" />");
            out.println("<BR><BR><div class=\"headertext\">Thank you for using ForeTees!");
            out.println("<BR><BR></div>");
            out.println("</CENTER></BODY></HTML>");

            /*
            
            out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"");
            out.println("      \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
            out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">");
            out.println("<head>");
            out.println("<!-- SERVER " + Common_Server.SERVER_ID + " -->");
            out.println("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=iso-8859-1\" />");
            out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" + club + "/mlogin.jsp\" />");
            out.println("<meta name=\"viewport\" id=\"viewport\" content=\"width=device-width, user-scalable=yes\" />");
            out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/mobile/style.css\" type=\"text/css\" media=\"screen\" />");
            out.println("<title>Logout Page</title>");
            out.println("<style type=\"text/css\">");
            out.println(".login {");
            out.println("  font-family:Arial, Helvetica, sans-serif;");
            out.println("  font-size:1em;");
            out.println("  text-align:center;");
            out.println("}");
            out.println(".login dev {");
            out.println("  margin: 5px 0px;");
            out.println("  text-align:center;");
            out.println("}");
            out.println(".blank {");
            out.println("  font-family:Arial, Helvetica, sans-serif;");
            out.println("  font-size:.5em;");
            out.println("}");
            out.println(".headertext {");
            out.println("  font-family:Arial, Helvetica, sans-serif;");
            out.println("  font-size:1.2em;");
            out.println("}");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<table align=\"center\" border=\"0\">");
            out.println("<tr>");
            out.println("<td align=\"center\"><img src=\"/v5/mobile/images/logo_large.gif\" alt=\"ForeTees\" /><br />");
            out.println("<img src=\"/v5/mobile/images/Mobile.gif\" alt=\"Mobile Version\" /></td>");
            out.println("<td>&nbsp; &nbsp; &nbsp; &nbsp;</td>");
            out.println("<td><img src=\"images/logo.jpg\" alt=\"" + club + "\" /></td>");
            out.println("</tr>");
            out.println("</table>");
            out.println("<div id=\"login\">");
            //out.println("<div class=\"blank\">&nbsp;</div>");
            //out.println("<div class=\"headertext\">Logout Processed</div>");
            out.println("<br /><br /><div class=\"headertext\">Logout Processed<br /><br />");
            out.println("Thank you for using ForeTees.");
            out.println("<br /><br /></div>");
            out.println("<form method=\"get\" action=\"/" + club + "/mlogin.jsp\" target=\"_top\">");
            out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\" />");
            out.println("</form>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
            * 
            */
        }

        out.close();

    } else {      // caller from remote site

        //
        //  Trace all logouts
        //
        sessionLog(msg, user, omit, club, caller, con);                   // log it

        if (mobile == 0) {

            if (new_skin) {            // WILL BE FALSE FOR ADMIN AND PROSHOP USERS !!!!!!!!!!!!!!!!!

               Common_skin.outputHeader(club, sess_activity_id, "Logout", true, out, req);     // output the page start

               out.println("<body>");
               out.println("<div id=\"wrapper_login\" align=\"center\">");
               out.println("<div id=\"title\">" +clubName+ "</div>");
               out.println("<div id=\"main_login\" align=\"center\">");
               out.println("<div class=\"main_message\">");
               out.println("<h2>Exit Confirmation</h2><br /><br />");
               out.println("<center><div class=\"sub_instructions\">");

               out.println("<BR>Thank you for using ForeTees.");      
               out.println("<br><br>Click on the 'Return' button below to close this window");
               out.println("<br>and return to your member services site.");
               out.println("<br /></div>");

               out.println("<form action=\"#\" onsubmit=\"self.close()\"><input type=\"submit\" value=\"RETURN\" id=\"submit\" onClick='self.close();'>");
               out.println("</form></center></div></div>");

               Common_skin.outputPageEnd(club, sess_activity_id, out, req);    // finish the page       

            } else {     // admin and proshop users
      
              out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"");
              out.println("      \"http://www.w3.org/TR/html4/loose.dtd\">");
              out.println("<html>");
              out.println("<head>");
              out.println("<!-- SERVER " + Common_Server.SERVER_ID + " -->");
              out.println("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=iso-8859-1\">");
              out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
              out.println("<title>Exit Confirmation Page</title>");
              out.println("</head>");
              out.println("<body><center><img src=\"/" +rev+ "/images/foretees.gif\" width=\"200\" height=\"72\" alt=\"\"><br>");
              out.println("<hr width=\"40%\">");
              out.println("<p>&nbsp;</p><br>");
              out.println("<h2>System Exit Processed</h2><br>");
              out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"12\"><tr><td align=\"center\">");
              out.println("Thank you for using ForeTees.");
              out.println("<br><br>Click on the 'Return' button below to close this window");
              out.println("<br>and return to your member services site.");
              out.println("</td></tr></table>");
              out.println("<br><br><br>");
              out.println("<form action=\"#\" onsubmit=\"self.close()\"><input type=\"submit\" value=\"RETURN\" style=\"text-decoration:underline; background:#8B8970\" onClick='self.close();'></form>");
              out.println("</center>");
              out.println("</body>");
              out.println("</html>");
            }
           
        } else {    // mobile device

            out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"");
            out.println("      \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
            out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">");
            out.println("<head>");
            out.println("<!-- SERVER " + Common_Server.SERVER_ID + " -->");
            out.println("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=iso-8859-1\" />");
            if (caller.equals("MYCLUB")) out.println("<meta http-equiv=\"Refresh\" content=\"1; url=http://myclub.userfinity.com\" />");
            out.println("<meta name=\"viewport\" id=\"viewport\" content=\"width=device-width, user-scalable=yes\" />");
            out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/mobile/style.css\" type=\"text/css\" media=\"screen\" />");
            out.println("<title>Logout Page</title>");
            out.println("<style type=\"text/css\">");
            out.println(".login {");
            out.println("  font-family:Arial, Helvetica, sans-serif;");
            out.println("  font-size:1em;");
            out.println("  text-align:center;");
            out.println("}");
            out.println(".login dev {");
            out.println("  margin: 5px 0px;");
            out.println("  text-align:center;");
            out.println("}");
            out.println(".blank {");
            out.println("  font-family:Arial, Helvetica, sans-serif;");
            out.println("  font-size:.5em;");
            out.println("}");
            out.println(".headertext {");
            out.println("  font-family:Arial, Helvetica, sans-serif;");
            out.println("  font-size:1.2em;");
            out.println("}");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<table align=\"center\" border=\"0\">");
            out.println("<tr>");
            out.println("<td align=\"center\" style=\"background:#F5F5DC\"><img src=\"/v5/mobile/images/logo_large.gif\" alt=\"ForeTees\" /><br />");
            out.println("<img src=\"/v5/mobile/images/Mobile.gif\" alt=\"Mobile Version\" /></td>");
            out.println("</tr>");
            out.println("</table>");
            out.println("<div id=\"login\">");
            out.println("<br /><br /><div class=\"headertext\">Logout Processed<br /><br />");
            out.println("Thank you for using ForeTees.");
            out.println("<br /><br /></div>");

            if (caller.equals("MYCLUB")) {      // caller from MyClub site

                out.println("<form method=\"get\" action=\"http://myclub.userfinity.com\" target=\"_top\">");
                out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\" />");
                out.println("</form>");
            }

            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }

        out.close();

    }
    
    if (!caller.equals("proshop")) {     // skip the following if proshop user exiting Member View (refer to Proshop_memberView)

       if (con != null) {

         // adjust the number of users logged in
         countLogout(con);
         /*
         // abandon any unfinished transactions
         try { con.rollback(); }
         catch (Exception ignore) {}
         */
         // close/release the connection
         try { con.close(); }
         catch (Exception ignore) {}

       }

       // clear the users session variables
       session.removeAttribute("user");
       session.removeAttribute("club");
       session.removeAttribute("connect");

       // end the users session
       session.invalidate();
    }

 }


 // *********************************************************
 // Connection error - inform user ....
 // *********************************************************

 private void Connerror(PrintWriter out) {

    out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"");
    out.println("      \"http://www.w3.org/TR/html4/loose.dtd\">");
    out.println("<html>");
    out.println("<head>");
    out.println("<!-- SERVER " + Common_Server.SERVER_ID + " -->");
    out.println("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=iso-8859-1\">");
    out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
    out.println("<title>Exit Confirmation Page</title>");
    out.println("</head>");
    out.println("<body>");
    out.println("<center><img src=\"/" +rev+ "/images/foretees.gif\" width=\"200\" height=\"72\" alt=\"\"><br>");
    out.println("<hr width=\"40%\">");
    out.println("<br><H2>Connection Error</H2><br>");
    out.println("<br>Sorry, we are unable to direct you to your point of entry at this time.<br>");
    out.println("<br>Either your session was lost or you never logged in to begin with.<br>");
    out.println("<br><br>Server: " + Common_Server.SERVER_ID);
    out.println("</center>");
    out.println("</body>");
    out.println("</html>");

    out.close();

 }


 //************************************************************************
 //
 //  sessionLog - logs each time a user logs out - to a text file
 //
 //************************************************************************

 private void sessionLog(String msg, String user, String pw, String club, String caller, Connection con) {


   if (con != null) {

      //
      //   Get current date
      //
      Calendar cal = new GregorianCalendar();

      long date = (cal.get(Calendar.YEAR) * 10000) + ((cal.get(Calendar.MONTH) + 1) * 100) + cal.get(Calendar.DAY_OF_MONTH);         // date value for today

      String sdate = String.valueOf(new java.util.Date());         // get date and time string

      PreparedStatement pstmt = null;

      try {

         //
         //  Save the session message in the db table
         //
         pstmt = con.prepareStatement (
              "INSERT INTO sessionlog (date, sdate, msg) " +
              "VALUES (?,?,?)");

         pstmt.clearParameters();
         pstmt.setLong(1, date);
         pstmt.setString(2, sdate);
         pstmt.setString(3, msg + " User=" +user+ ", PW=" +pw+ ", Club=" +club+ ", Caller=" +caller);
         pstmt.executeUpdate();

      } catch (Exception ignore) {

      } finally {

        try { pstmt.close(); }
        catch (Exception ignore) {}

      }
   }

 }  // end of sessionLog


 // ***************************************************************
 //  countLogout
 //
 //      Track the number of users logged in for each club.
 //
 // ***************************************************************

 private void countLogout(Connection con) {


    Statement stmt = null;

    try {

        stmt = con.createStatement();
        stmt.executeUpdate("UPDATE club5 SET logins = (logins - 1) WHERE logins > 0");

    } catch (Exception ignore) {

    } finally {

        try { stmt.close(); }
        catch (Exception ignore) {}

    }

 }

}
