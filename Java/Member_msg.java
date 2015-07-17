/***************************************************************************************     
 *   Member_msg:  This servlet will display a system message to all members that login.
 *
 *
 *   called by:  Login
 *
 *   created: 4/14/2003   Bob P.
 *
 *   last updated:      ****** keep this accurate *******
 *
 *        3/10/10   Added check to see if sess_activity_id = -999, if so, set default activity id to 0
 *        2/16/10   If negative activity_id is passed in the session, use that value (as positive value) instead of value from member2b
 *        1/26/10   Replace Denver custom with mobile support check (allow_mobile in club5).
 *        1/22/10   Denver CC - skip the Mobile announcement (msg002) - they don't want members to bypass website.
 *        1/15/10   Add msg002 to announce the Mobile Interface.
 *       12/18/09   Reset the member message feature to display a message regarding the partner list updates.
 *       10/29/09   Add users default activity_id to session block
 *       10/14/08   Brooklawn - prompt remote member to verify/change email address if it has bounced (case 1568) - on hold.
 *        6/02/08   Get member timeout value (for display) from SystemUtils.MEMBER_TIMEOUT
 *        5/09/07   Moved daysInAdv call to SystemUtils instead of Login - 
 *                  also DaysAdv array no longer put session
 *        2/15/07   Comment out the member welcome message processing as we don't need it now (save for future).
 *        1/12/07   Add mtype parm to call to Login.daysInAdv.
 *       11/16/06   Do not process MF clubs differently - always go to announce page.
 *       10/20/06   Do not display email message if AGT.
 *       11/17/05   Add message to prompt for email address if none.
 *       11/22/04   Add support for Primary-Only logins from CE.  Only the primary member resides
 *                  in their database so we must prompt for the family member.
 *        7/18/03   Enhancements for Version 3 of the software.
 *                                                               
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

// foretees imports
import com.foretees.common.DaysAdv;
import com.foretees.common.getActivity;
import com.foretees.common.Utilities;


public class Member_msg extends HttpServlet {
       

 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //
 //  The following is used for member messages to be displayed once when a member logs in. (******* See also Login *********)
 //
 String previous_message = "msg001";      // previous message that was shown
 String latest_message = "msg002";        // message we want to show now


 
 //
 // Process the initial call from Login
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();


   Connection con = null;                  // init DB objects
   Statement stmt = null;
   PreparedStatement pstmt = null;
   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyMem(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   con = SystemUtils.getCon(session);            // get DB connection

   if (con == null) {

      return;
   }


   String club = (String)session.getAttribute("club");   // get user's club
   String caller = (String)session.getAttribute("caller");   // get caller (mfirst, etc.)
   String user = (String)session.getAttribute("user");   // get user's username value
   String message = ""; 
   String last_message = ""; 
   String name = "";
   String mship = "";
   String mtype = "";
   String wc = "";
   String email = "";
   String email2 = "";
   int count = 0;
   int email_bounced = 0;
   int email2_bounced = 0;
   int rsync = 0;
   int iCal1 = 0;
   int iCal2 = 0;
   int default_activity_id = 0;         // inticator for default activity (0=golf)
   int tlt = 0;
   int foretees_mode = 0;
   
   boolean allowMobile = false;

   int sess_activity_id = (Integer)session.getAttribute("activity_id");
   
    //
    //  Check if Mobile is allowed (for messages)
    //
    allowMobile = Utilities.checkMobileSupport (con);      //  show the Mobile messages?


   //
   //  Get TLT and ForeTees indicators
   //
   try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT no_reservations, foretees_mode FROM club5");
        if (rs.next()) {
           
           tlt = rs.getInt(1);
           foretees_mode = rs.getInt("foretees_mode");    // golf indicator
        }
        stmt.close();
        
   } catch (Exception ignore) {
        
   } finally {

        if (rs != null) {
           try {
              rs.close();
           } catch (SQLException ignored) {}
        }
        
        if (stmt != null) {
           try {
              stmt.close();
           } catch (SQLException ignored) {}
        }
   }
       
   
   //
   //  If user provided in parm, then call was from Login after user prompted for actual member name.
   //  This is when the caller only has primary names in their roster and we must prompt for actual member.
   //
   if (req.getParameter("user") != null) {

      user = req.getParameter("user");
      name = req.getParameter("name");
      // message = req.getParameter("message");  (get last message displayed to the selected member)
        
      try {

         //
         //  Get this member's info
         //
         pstmt = con.prepareStatement (
            "SELECT m_ship, m_type, email, count, wc, message, email2, email_bounced, email2_bounced, iCal1, iCal2, default_activity_id " +
            "FROM member2b WHERE username = ?");

         pstmt.clearParameters();         // clear the parms
         pstmt.setString(1, user);        // put the username field in statement
         rs = pstmt.executeQuery();       // execute the prepared stmt

         if (rs.next()) {

            mship = rs.getString(1);       // Get mship type
            mtype = rs.getString(2);       // Get member type
            email = rs.getString(3);       // Get email addr
            count = rs.getInt(4);          // Get count
            wc = rs.getString(5);          // w/c pref
            message = rs.getString("message");     // get this member's last message
            email2 = rs.getString("email2");
            email_bounced = rs.getInt("email_bounced");       
            email2_bounced = rs.getInt("email2_bounced");
            iCal1 = rs.getInt("iCal1");
            iCal2 = rs.getInt("iCal2");
            default_activity_id = rs.getInt("default_activity_id");
         }
           
         pstmt.close();

         count++;          // bump login counter
           
         pstmt = con.prepareStatement (
            "UPDATE member2b SET count = ? WHERE username = ?");
         
         pstmt.clearParameters();           
         pstmt.setInt(1, count);            // set the new count
         pstmt.setString(2, user);         
         pstmt.executeUpdate();    

         pstmt.close();

         //  get rsync value for this club
         pstmt = con.prepareStatement (
                  "SELECT rsync " +
                  "FROM club5");

         pstmt.clearParameters();        // clear the parms
         rs = pstmt.executeQuery();

         if (rs.next()) {          // get club options 

            rsync = rs.getInt(1);
         }
         pstmt.close();              // close the stmt

      }
      catch (SQLException exc) {
      }

      //
      //  see if we should display a message to this member
      //
      last_message = message;                      // save last message sent to this user
      
      if (message.equals( latest_message )) {      // if newest message already displayed

         message = "";                              // no message to send
      }

      //
      //  Count the number of users logged in
      //
      Login.countLogin("mem", con);

      // Check to see if an overrided activity_id (negative value) has been passed from Login, use that instead if so
      if (sess_activity_id == -999) {
          default_activity_id = 0;
      } else if (sess_activity_id < 0) {
          default_activity_id = sess_activity_id * -1;
      }

      //
      //  over-write the name and user fields as they could be different now
      //
      session.setAttribute("user", user);           // save username
      session.setAttribute("name", name);           // save members full name
      session.setAttribute("mship", mship);         // save member's mship type
      session.setAttribute("mtype", mtype);         // save member's mtype
      session.setAttribute("wc", wc);               // save member's w/c pref (for _slot)
      session.setAttribute("activity_id", default_activity_id);  // activity indicator

      
      //
      //  Output the response and route to system
      //
      out.println("<HTML><HEAD><Title>Member Login Page</Title>");
      if ((!email.equals( "" ) || rsync == 1) && message.equals( "" )) {
         out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/member_welcome.htm\">");
      }
      out.println("</HEAD>");
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<p>&nbsp;</p>");
      out.println("<BR><H2>Member Access Accepted</H2><BR>");
      out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"6\"><tr><td align=\"center\">");
      out.println("<BR>Welcome <b>" + name );
      out.println("</b><BR><BR>");
      out.println("Please note that this session will terminate if inactive for more than " + (SystemUtils.MEMBER_TIMEOUT / 60) + " minutes.<BR><BR>");

      if (email.equals( "" ) && rsync == 0) {

         out.println("In order for us to send email notifcations when you make or change tee times, you must provide a current, ");
         out.println("working email address.");
         out.println("<br><br>");
         out.println("Please add at least one valid email address below.");                
         // out.println("To provide your email address, click on the <b>'Settings'</b> tab in the navigation bar on top of the next page.");
         out.println("<br><br>");
         out.println("Thank you!");

         out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Login\">");
         out.println("<input type=\"hidden\" name=\"message\" value=\"" +message+ "\">");
         // out.println("<input type=\"hidden\" name=\"message\" value=\"\">");                 // nothing for now

         out.println("<b>Email Address 1:</b>&nbsp;&nbsp;");
         out.println("<input type=\"text\" name=\"email\" value=\"" +email+ "\" size=\"40\" maxlength=\"50\">");

         out.println("<br>&nbsp;&nbsp;&nbsp;Receive <a href=\"/"+rev+"/member_help_icalendar.htm\" target=_memberHelp>iCal attachments</a> at this email address? ");
         out.println("<select size=\"1\" name=\"iCal1\">");
          out.println("<option value=1" + ((iCal1 == 1) ? " selected" : "") + ">Yes</option>");
          out.println("<option value=0" + ((iCal1 != 1) ? " selected" : "") + ">No</option>");
         out.println("</select>");

         out.println("<br><br>");

         out.println("<b>Email Address 2:</b>&nbsp;&nbsp;");
         out.println("<input type=\"text\" name=\"email2\" value=\"" +email2+ "\" size=\"40\" maxlength=\"50\">");

         out.println("<br>&nbsp;&nbsp;&nbsp;Receive <a href=\"/"+rev+"/member_help_icalendar.htm\" target=_memberHelp>iCal attachments</a> at this email address? ");
         out.println("<select size=\"1\" name=\"iCal2\">");
          out.println("<option value=1" + ((iCal2 == 1) ? " selected" : "") + ">Yes</option>");
          out.println("<option value=0" + ((iCal2 != 1) ? " selected" : "") + ">No</option>");
         out.println("</select>");

         out.println("<br><br>");
         out.println("</td></tr></table><br>");
         out.println("<br><br>");
         out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</input></form>");

      } else {

         out.println("</td></tr></table><br>");

         if (last_message.equals( "" )) {        // if no message sent to this user yet 

            addMsg(out, con);                    // send msg #1
         }

         if (!last_message.equals( latest_message )) {        // if msg #2 not sent yet 

            if (tlt == 0 && foretees_mode > 0 && allowMobile == true) {    // if this is a golf tee time system & mobile supported
               
               addMsg2(out, con);                         // display Mobile message
            }
            
            try {
            
               pstmt = con.prepareStatement (
                  "UPDATE member2b SET message = ? WHERE username = ?");

               pstmt.clearParameters();           
               pstmt.setString(1, latest_message);     // set latest message sent
               pstmt.setString(2, user);         
               pstmt.executeUpdate();    

               pstmt.close();

            }
            catch (SQLException exc) {
            }            
         }

         out.println("<br><br><font size=\"2\">");
         out.println("<form method=\"get\" action=\"/" + rev + "/member_welcome.htm\">");
         out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</input></form></font>");
      }
      out.println("</CENTER></BODY></HTML>");
      out.close();
    
         
   } else {

      //
      // Call is NOT for the primary interface - Call is to generate a ForeTees message to the user.
      //
      try {

         pstmt = con.prepareStatement (
            "SELECT email, message " +
            "FROM member2b WHERE username = ?");

         pstmt.clearParameters();         
         pstmt.setString(1, user);        // get this user's account info
         rs = pstmt.executeQuery();     

         if (rs.next()) {

            email = rs.getString(1);         // email addr
            message = rs.getString(2);       // Get last message displayed at login
         }
         pstmt.close();

      }
      catch (SQLException exc) {
      }

      //
      //   Display the message
      //
      out.println("<HTML>");
      out.println("<HEAD>");
         out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
         out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
         out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
         out.println("<title>\"ForeTees Message Page\"</title>");
      out.println("</head>");

      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER>");
      out.println("<img src=\"/" +rev+ "/images/foretees.gif\"><BR>");

      //
      //  Attach member message if necessary
      //
      if (message.equals( "" )) {           // if no message sent to this user yet 

         addMsg(out, con);                    // send msg #1
      }

      if (!message.equals( latest_message )) {        // if msg #2 not sent yet 

         if (tlt == 0 && foretees_mode > 0 && allowMobile == true) {          // if this is a golf tee time system

            addMsg2(out, con);                         // display Mobile message
         }
            
         try {

            pstmt = con.prepareStatement (
               "UPDATE member2b SET message = ? WHERE username = ?");

            pstmt.clearParameters();           
            pstmt.setString(1, latest_message);     // set latest message sent
            pstmt.setString(2, user);         
            pstmt.executeUpdate();    

            pstmt.close();

         }
         catch (SQLException exc) {
            //out.println("<br>Error updating the account record!!<BR>");
         }            
      }

      out.println("<br>");
      out.println("<table border=\"0\" cols=\"1\">");
         out.println("<tr>");
         out.println("<td width=\"150\" align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("<form method=\"get\" action=\"/" + rev + "/member_welcome.htm\">");
            out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</input></form></font>");
         out.println("</td>");
         out.println("</tr>");
      out.println("</table>");
      out.println("</center></font></body></html>");
      out.close();
    }        
 }
 
 

 // *********************************************************
 //   Add the latest message from ForeTees
 // *********************************************************

 private void addMsg(PrintWriter out, Connection con) {
    
   int activities = 0;
     
   //
   //  Determine how many activities this club as defined (Golf, Tennis, etc)
   //
   activities = getActivity.getActivityCount(con);
   
   
   out.println("<BR><table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" width=\"650\">");
   out.println("<tr>");
   out.println("<td align=\"center\">");
   out.println("<H2>Important Notice From ForeTees</H2>");
   out.println("<font size=\"4\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<p><u>Please Read</u></p></font>");
   out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<p>");

   out.println("We have made some changes to the Partner List page (Partners tab).");
   out.println("<br>It is now even easier to maintain your partner list<BR>and you are no longer limited to only 25 partners.");
   //
   //   Add link to pdf here !!!!!!!!!!!!!
   //
   if (activities > 1) {            // if multiple activities - requires different instructions
      
      out.println("<br><br><a href=\"http://www.foretees.com/messages/partners_multi.pdf\" target=\"_blank\">Click here to see how easy it is.</a>");
      
   } else {
      
      out.println("<br><br><a href=\"http://www.foretees.com/messages/partners.pdf\" target=\"_blank\">Click here to see how easy it is.</a>");
   }

   out.println("<br><br>Thank you!");

   out.println("</p></font></td></tr></table>");
 }
 
 // *********************************************************
 //   Add the MOBILE message 
 // *********************************************************

 private void addMsg2(PrintWriter out, Connection con) {
    
   out.println("<BR><table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" width=\"650\">");
   out.println("<tr>");
   out.println("<td align=\"center\">");
   out.println("<H2>Announcing ForeTees Mobile!</H2>");
   out.println("<font size=\"4\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<p><u>Please Read</u></p></font>");
   out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<p>");

   out.println("ForeTees has created a site for <b>Smart Phone</b> access.");
   out.println("<br><br>If you have a smart phone (with web access) and would like to manage your <br>tee times from it, then select the ");
   out.print("<img src=\"/" +rev+ "/mobile/images/MobileNav.gif\" border=0>");
   out.print(" link on the ForeTees <br>navigation panel in the upper left corner of the next page for more information.");
  
   out.println("<br><br>Thank you!");

   out.println("</p></font></td></tr></table>");
 }
 
}
