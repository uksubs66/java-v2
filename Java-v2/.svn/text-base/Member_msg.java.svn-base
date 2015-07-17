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
 *        3/04/14   Remove mobile member message and the AOL message.
 *       12/04/13   Updated Utilities.isAGCClub calls to use the new Utilities.isClubInGroup method instead.
 *        9/17/13   Get member sub-type (msub_type) from member2b and save in the session for members.
 *        9/03/13   In promptIpad indicate that the mobile site is for Golf so FlxRez or Dining users don't go to our mobile site.
 *        8/22/13   Fixed bug regarding direction members to specific landing pages via the primary interface.
 *        8/07/13   Turned off AOL email notice.
 *        8/07/13   Small updates to AOL notice.
 *        8/06/13   Added AOL Email notice to inform members that emails are currently being throttled by AOL. Will only display when displayAolNotice is set to "true" and member has one email that ends with "aol.com".
 *        4/18/13   Dove Canyon Club (dovecanyonclub) - Separated dovecanyonclub from AGC club processing since they are no longer managed by AGC.
 *        2/28/13   Added support for both the "partners" and "reservations" eventname keywords to redirect incoming members to the Partners and Member_select/Member_gensheets/Dining_home, respectively.
 *        2/13/13   Rehoboth Beach CC (rehobothbeachcc) - Added custom to force members to enter at least one email address, but only prompt them one additional time. Prompt will only
 *                  display the first two times a member enters ForeTees, and will be skipped after that, regardless of whether or not the member has email addresses in ForeTees (case 2220).
 *        1/17/13   Add the Request object to outputBanner, outputSubNav, and outputPageEnd so we can get the session object to test caller.
 *        1/10/13   Add new mobile menu when users come from website on mobile using the primary interface.
 *       12/20/12   Added custom to ensure Mobile message will be always be displayed on demotom when logging in as a member.
 *       11/15/12   All American Golf Clubs - Set meta refresh time to 0 so members will get forwarded immediately.
 *       11/15/12   Passing an eventname parameter value of "lessons" will now bring the member to the Individual Lessons page for the passed activity (except dining).
 *       10/26/12   Passing an eventname parameter value of "calendar" will now bring you to the calendar page for the passed activity (golf if no activity passed).
 *        9/04/12   Changed calls to Member events2 to Member events
 *        8/24/12   Tonto Verde (tontoverde) - Set meta refresh time to 0 so members will get forwarded immediately.
 *        8/24/12   Re-enabled the meta refresh for primary logins if no other messages are to be displayed, and tied in forwarding to events for event links as well.
 *        8/08/12   Updates to allow website event links to work for clubs that use a primary interface.
 *        1/20/12   More new skin updates, and eliminate the Partner List message.
 *       11/13/11   New skin changes plus iPad fix
 *        9/06/11   Change latest_message to msg003 to trigger a resend of the mobile annoucement if user has not used it yet.
 *        8/19/11   Add support for ForeTees Dining - route to dining if activity id is dining.
 *        3/01/11   Do not prompt a mobile user in remoteUser if already detected as a mobile device (iPad prompt).
 *        2/09/11   Allow for Mobile user from primary seamless interface for MyClub (mobile app).
 *        1/20/11   Added organization_id to session block (for dining system)
 *        9/28/10   Prompt seamless interface users regarding bounced email flags (case 1720).
 *        9/16/10   Add processing for a link from iPad users - they can select the Mobile or Standard interface.
 *                  Both options will bypass the message checks, so none are displayed.  We simply route the user accordingly.
 *        9/08/10   Add "and iPad" to msg #2.
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
    
import com.foretees.common.Common_skin;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

// foretees imports
import com.foretees.common.DaysAdv;
import com.foretees.common.getActivity;
import com.foretees.common.Utilities;
import com.foretees.common.ProcessConstants;
import com.foretees.common.Connect;

public class Member_msg extends HttpServlet {
       

 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //
 //  The following is used for member messages to be displayed once when a member logs in. (******* See also Login *********)
 //
 //String previous_message = "msg001";      // previous message that was shown
 String latest_message = "msg003";        // message we want to show now ** NOTE:  Bump this number to force members to see the latest message again !!!

 static int dining_activity_id = ProcessConstants.DINING_ACTIVITY_ID;    // Global activity_id for Dining System  


 
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

    if (session == null) return;

    con = Connect.getCon(req);            // get DB connection
    if (con == null) return;

    String club = (String)session.getAttribute("club");   // get user's club
    String caller = (String)session.getAttribute("caller");   // get caller (mfirst, etc.)
    String user = (String)session.getAttribute("user");   // get user's username value
    
    String message = "";
    String last_message = "";
    String name = "";
    String mship = "";
    String mtype = "";
    String subtype = "";
    String wc = "";
    String email = "";
    String email2 = "";
    String clubName = "";
    String eventName = "";
    String courseName = "";
    int count = 0;
    int email_bounced = 0;
    int email2_bounced = 0;
    int rsync = 0;
    int iCal1 = 0;
    int iCal2 = 0;
    int default_activity_id = 0;         // inticator for default activity (0=golf)
    int tlt = 0;
    int foretees_mode = 0;
    int mobile_count = 0;
    int mobile_iphone = 0;
    int event_id = 0;
    
    long member_id = 0;

    boolean allowMobile = false;
    boolean aolEmailUser = false;
    boolean displayAolNotice = false;    // This is to be toggled on and off depending on if we want to display the AOL email throttling/blacklist message for members with "aol.com" email addresses

    int mobile = (Integer)session.getAttribute("mobile");
    int sess_activity_id = (Integer)session.getAttribute("activity_id");

    int organization_id = Utilities.getOrganizationId(con);

    boolean new_skin = ((String)session.getAttribute("new_skin")).equals("1");
    
    boolean rwd = Utilities.getRequestBoolean(req, ProcessConstants.RQA_RWD, false);
    
    if(req.getParameter("saveRwd") != null){
        // If "saveRwd" was sent, save rwd state in the cookie in the user's cookie.
       Utilities.setRwdCookie(resp, req, rwd);
    }
   
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

    if (req.getParameter("eventname") != null) {
        
        eventName = req.getParameter("eventname");
        
        if (req.getParameter("course") != null) {
            courseName = req.getParameter("course");
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
            "SELECT m_ship, m_type, email, count, wc, message, email2, email_bounced, email2_bounced, iCal1, iCal2, "
                 + "default_activity_id, mobile_count, mobile_iphone, msub_type, id, username " +
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
            mobile_count = rs.getInt("mobile_count");      // get # of mobile logins for this ueer
            mobile_iphone = rs.getInt("mobile_iphone");    // get # of iphone logins for this ueer
            subtype = rs.getString("msub_type");           // Get member sub_type
            member_id = rs.getLong("id"); 
            user = rs.getString("username");
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
/*
         if (mobile > 0) {
            
            countMobile(mobile_count, mobile_iphone, user, req, con);      // bump mobile counter and track mobile device 
         }
  */       
         
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
      
      // Detect if the user has an aol.com email address
      if (email.toLowerCase().endsWith("aol.com") || email2.toLowerCase().endsWith("aol.com")) {
          aolEmailUser = true;
      }

      //
      //  see if we should display a message to this member
      //
      last_message = message;                      // save last message sent to this user
      
      if (message.equals( latest_message ) || mobile > 0) {      // if newest message already displayed or mobile user

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
      } else if (sess_activity_id == dining_activity_id) {     // use dining if requested in login
          default_activity_id = dining_activity_id;
      }

      //
      //  over-write the name and user fields as they could be different now
      //
      session.setAttribute("user", user);           // save username
      session.setAttribute("member_id", member_id);
      session.setAttribute("name", name);           // save members full name
      session.setAttribute("mship", mship);         // save member's mship type
      session.setAttribute("mtype", mtype);         // save member's mtype
      session.setAttribute("msubtype", subtype);    // save member's sub_type
      session.setAttribute("wc", wc);               // save member's w/c pref (for _slot)
      
      // would these be changing???
      session.setAttribute("activity_id", default_activity_id);  // activity indicator
      session.setAttribute("organization_id", organization_id);  // organization_id (set if using ForeTeesDining system)
    //session.setAttribute("new_skin", (new_skin) ? "1" : "0");  // new skin flag

      
      //
      //   Check for iPad (or like) device and prompt for standard or mobile access.
      //
      boolean allowRwd = Utilities.isResponsiveAllowed(con);
      boolean forceRwd = Utilities.isResponsiveForced(con);
      boolean enableAdvAssist = Utilities.enableAdvAssist(req);     // check for iPad (or like) device - false indicates iPad

      if (!allowRwd && enableAdvAssist == false && allowMobile == true && default_activity_id == 0 && mobile == 0) {     // if Golf, iPad and mobile ok for this site, and user not already detected as mobile

         promptIpad(name, out);                 // prompt user for mobile or standard interface
         return;
      }

                  
                  
      //
      //  Output the response and route to system
      //
      if (mobile == 0) {
          
            int refresh_val = 0;
            String refresh_url = "";

            if ((!email.equals("") || rsync == 1 || (club.equals("rehobothbeachcc") && count > 2)) && message.equals("") && email_bounced == 0 && email2_bounced == 0 && (!aolEmailUser || !displayAolNotice)) {

               if (club.equals("tontoverde") || club.equals("dovecanyonclub") || Utilities.isClubInGroup(club, "AGC")) {
                     refresh_val = 0;
               } else {
                     refresh_val = 1;
               }

               if (default_activity_id == dining_activity_id) {     // if dining

                     refresh_url = "Dining_home";

                     if (!eventName.equals("")) {

                        // Check to make sure a valid event_id was passed as the eventName parameter.  If not, route them to the event listing instead.
                        try {
                           event_id = Integer.parseInt(eventName);
                        } catch (Exception exc) {
                           event_id = 0;
                        }

                        if (eventName.equalsIgnoreCase("reservations")) {
                           refresh_url = "Dining_slot?action=new";
                        } else if (eventName.equalsIgnoreCase("calendar")) {
                           refresh_url = "Member_teelist";  
                        } else if (eventName.equalsIgnoreCase("partners")) {
                           refresh_url = "Member_partner";  
                        } else if (eventName.equals("clubessential") || eventName.equalsIgnoreCase("none") || event_id == 0) {
                           refresh_url += "?view_events";
                        } else {
                           refresh_url += "?view_events&event_id=" + event_id;
                        }
                        
                     } else {
                         
                         // here for dining and there is no eventName being passed in
                         // so lets force all AGC clubs to skip the announcement page
                         // and go right into the make a reservation page
                        if (Utilities.isClubInGroup(club, "AGC")) {
                            
                            refresh_url = "Dining_slot?action=new";
                        }
                        
                     }
                     
               } else {

                     if (!eventName.equals("")) {

                        if (eventName.equalsIgnoreCase("reservations")) {
                           if (default_activity_id != 0) {
                                 refresh_url = "Member_gensheets";
                           } else {
                                 refresh_url = "Member_select";
                           }
                        } else if (eventName.equals("clubessential") || eventName.equalsIgnoreCase("none")) {
                           refresh_url = "Member_events";
                        } else if (eventName.equalsIgnoreCase("lessons")) {
                           refresh_url = "Member_lesson";  
                        } else if (eventName.equalsIgnoreCase("calendar")) {
                           refresh_url = "Member_teelist";
                        } else if (eventName.equalsIgnoreCase("partners")) {
                           refresh_url = "Member_partner";
                        } else {
                           refresh_url = "Member_events?name=" + eventName + "&course=" + courseName; // was Member_events2
                        }
                     } else {
                        refresh_url = "Member_announce";
                     }
               }
            }


            // new skin (no meta refresh - use js instead)
            Common_skin.outputHeader(club, default_activity_id, "Member Login Page", true, out, req, refresh_val, refresh_url);

            try {

               clubName = Utilities.getClubName(con);        // get the full name of this club

            } catch (Exception exc) {}

            Common_skin.outputBody(club, default_activity_id, out, req);

            //out.println("<body>");
            out.println("<div id=\"wrapper_login\" align=\"center\">");
            out.println("<div id=\"title\">" +clubName+ "</div>");
            out.println("<div id=\"main_login\" align=\"center\">");
            out.println("<h1>Member Access Accepted</h1>");
            out.println("<div class=\"main_message\">");
            out.println("<h2>Welcome " +name+ "</h2><br /><br />");
            out.println("<center><div class=\"sub_instructions\">");
         
          out.println("Please note that this session will terminate if inactive for more than " + (SystemUtils.MEMBER_TIMEOUT / 60) + " minutes.<BR><BR>");

          if (email_bounced == 1 || email2_bounced == 1) {

            out.println("<h3><b>WARNING: Email bouncing!!</b></h3>");
            out.println("We recently tried to send you an email at ");

            if (email_bounced == 1) {

               out.print(email);

            } else {

               out.print(email2);
            }
            out.print(" and it bounced back to us.<br>" +
                      "We've had to temporarily disable sending you any emails until you resolve this problem.");
            if (rsync == 1) {
                out.println("<BR><BR>To correct this, please verify that the following email addresses are correct.<br>" +
                        "If changes are needed, please contact the golf shop staff at your club to request the change<br>" +
                        " be made in their club records.");
            } else {
                out.println("<BR><BR>To correct this, update your email below, or select the 'Settings' tab from the<br>" +
                        "navigation bar on the top of most pages and follow the insructions in the email<br>" +
                        "section next to the word 'Important'.");
            }
            out.println("<br><br>If the current email is correct, simply click 'Continue' below and ForeTees will<br>" +
                    "attempt to continue using the same email.  If you would like to remove the current email<br>" +
                    "address all together, " + (rsync == 1 ? "please notify the golf shop staff at your club that it should be removed." : "erase it from the field below and click 'Continue'."));

            out.println("<br><br>");

            if (rsync == 1) {
                if (email.equals( "" )) {
                   out.println("Please contact the golf shop staff at your club to add email addresses.");
                } else {
                   out.println("Please verify the email address(es) below.");
                }
            } else {
                if (email.equals( "" )) {
                   out.println("Please add at least one valid email address below.");
                } else {
                   out.println("Please verify and/or change the email address(es) below.");
                }
            }
            out.print("&nbsp;&nbsp;");
            out.print("Thank you!");
            out.print("<br><br>");

            out.println("<form method=\"post\" action=\"Login\">");
            out.println("<input type=\"hidden\" name=\"message\" value=\"" +message+ "\">");
            out.println("<input type=\"hidden\" name=\"bounced\" value=\"" + (email_bounced == 1 ? "email" : "email2") + "\">");

            out.println("<b>Email Address " + (email_bounced == 1 ? "1" : "2") + ":</b>&nbsp;&nbsp;");
            if (rsync == 1) {
                out.println("<i>" + (email_bounced == 1 ? email : email2) + "</i>");
                out.println("<input type=\"hidden\" name=\"email\" value=\"" + (email_bounced == 1 ? email : email2) + "\">");
            } else {
                out.println("<input type=\"text\" name=\"email\" value=\"" + (email_bounced == 1 ? email : email2) + "\" size=\"40\" maxlength=\"50\">");
            }
            out.println("<br><br>");
            out.println("</font></td></tr></table>");
            
            out.println("<input type=\"submit\" value=\"Continue\" id=\"submit\">");
            out.println("</form></div>");
            out.println("</center></div></div>");        
            Common_skin.outputPageEnd(club, default_activity_id, out, req);    // finish the page       
            out.close();

         } else if (email.equals( "" ) && rsync == 0 && !club.equals("tcclub") && (!club.equals("rehobothbeachcc") || count <= 2)) {

            out.println("In order for us to send email notifcations when you make or change tee times, you must provide a current, ");
            out.println("working email address.");
            out.println("<br><br>");
            out.println("Please add at least one valid email address below.");                
            // out.println("To provide your email address, click on the <b>'Settings'</b> tab in the navigation bar on top of the next page.");
            out.println("<br><br>");
            out.println("Thank you!");

            out.println("<form method=\"post\" action=\"Login\">");
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
            
            out.println("<input type=\"submit\" value=\"Continue\" id=\"submit\">");
            out.println("</form></div>");
 
         } else {
              
            out.println("</div>");     // close out the sub_instructions above

            out.println("</td></tr></table><br>");

            if (last_message.equals( "" ) && (!aolEmailUser || !displayAolNotice)) {        // if no message sent to this user yet 

               addMsg(new_skin, out, con, req);                    // send msg #1
            }

            if (!last_message.equals( latest_message ) || (aolEmailUser && displayAolNotice)) {        // if msg #1 not sent yet 

                /*  only 1 msg now
               if (tlt == 0 && foretees_mode > 0 && allowMobile == true && mobile_count == 0) {    // if this is a golf tee time system & mobile supported

                  addMsg2(new_skin, out, con);                         // display Mobile message
               }
                 */
                
               if (aolEmailUser && displayAolNotice) {
                   addMsg2(new_skin, out, con);
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
            
            if (default_activity_id == dining_activity_id) {     // if dining

               String destination_url = "Dining_home";

               if (eventName.equalsIgnoreCase("reservations")) {
                  destination_url = "Dining_slot?action=new";
               } else if (eventName.equalsIgnoreCase("calendar")) {
                  destination_url = "Member_teelist";
               } else if (eventName.equalsIgnoreCase("partners")) {
                  destination_url = "Member_partner";
               }

               out.println("<form method=\"get\" action=\"" + destination_url + "\">");

               if (!eventName.equals("") && destination_url.equals("Dining_home")) {

                  // Check to make sure a valid event_id was passed as the eventName parameter.  If not, route them to the event listing instead.
                  try {
                     event_id = Integer.parseInt(eventName);
                  } catch (Exception exc) {
                     event_id = 0;
                  }

                  if (eventName.equals("clubessential") || eventName.equalsIgnoreCase("none") || event_id == 0) {
                     out.println("<input type=\"hidden\" name=\"view_events\">");
                  } else {

                     out.println("<input type=\"hidden\" name=\"view_events\">");     // Temporarily direct all event links to the event listings page
                     out.println("<input type=\"hidden\" name=\"event_id\" value=\"" + event_id + "\">");

                  }    
               }

            } else {


               if (!eventName.equals("")) {

                  if (eventName.equalsIgnoreCase("reservations")) {
                     if (default_activity_id > 0) {
                           out.println("<form method=\"get\" action=\"Member_gensheets\">");
                     } else {
                           out.println("<form method=\"get\" action=\"Member_select\">");
                     }
                  } else if (eventName.equalsIgnoreCase("lessons")) {
                     out.println("<form method=\"get\" action=\"Member_lesson\">");
                  } else if (eventName.equalsIgnoreCase("calendar")) {
                     out.println("<form method=\"get\" action=\"Member_teelist\">");
                  } else if (eventName.equalsIgnoreCase("partners")) {
                     out.println("<form method=\"get\" action=\"Member_partner\">");
                  } else if (eventName.equals("clubessential") || eventName.equalsIgnoreCase("none")) {
                     out.println("<form method=\"get\" action=\"Member_events\">");
                  } else {
                     out.println("<form method=\"get\" action=\"Member_events\">"); // was Member_events2
                     out.println("<input type=\"hidden\" name=\"name\" value=\"" + eventName + "\">");
                     out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName + "\">");
                  }
               } else {
                  out.println("<form method=\"get\" action=\"Member_announce\">");
               }
            }
            out.println("<input type=\"submit\" value=\"Continue\" id=\"submit\">");
            out.println("</form>");
         }

         out.println("</center></div></div>");        
         Common_skin.outputPageEnd(club, default_activity_id, out, req);    // finish the page       
         out.close();
         
      } else {
         
         //
         //  Mobile Device - go to mobile menu page
         //
         Login.promptMobileUser(club, name, session, out, req, con);   // prompt mobile user to see how they want to proceed
         
      }   // end of IF Mobile User

    } else if (req.getParameter("useMobile") != null) {       // if switching an iPad user

      //
      //  iPad user wants to use the Mobile Interface - go to mobile menu page
      //
      session.setAttribute("mobile", 1);                     // indicate mobile user

      out.println("<HTML xmlns='http://www.w3.org/1999/xhtml'><HEAD><Title>Member Welcome Page</Title>");
      out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" + rev + "/mobile/member_mobile_home.html\">");
      out.println("<meta name=\"viewport\" id=\"viewport\" content=\"width=device-width, user-scalable=no\">");
      out.println("<LINK REL=StyleSheet HREF=\"/" +rev+ "/mobile/style.css\" TYPE=\"text/css\" MEDIA=screen></HEAD>");
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><div class=\"headertext\">Thank you.  We will now switch you to the Mobile site.");
      out.println("<BR><BR></div>");
      out.println("<form method=\"get\" action=\"/" + rev + "/mobile/member_mobile_home.html\">");
      out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      

    } else if (req.getParameter("useStandard") != null || req.getParameter("useRwd") != null) {     // if switching a mobile

        //
        //   iPad user wants to use the standard interface - route to welcome page
        //
        String dest = "Member_announce";

        // if dining change page
        if (default_activity_id == dining_activity_id) {
               dest = "Dining_home";
        }

        Common_skin.jsRedirect(dest, out);         
        out.close();

    } else {

      //
      // Call is NOT for the primary interface or iPad user - Call is to generate a ForeTees message to the user.
      //
      try {

         pstmt = con.prepareStatement (
            "SELECT email, email2, message, mobile_count " +
            "FROM member2b WHERE username = ?");

         pstmt.clearParameters();         
         pstmt.setString(1, user);        // get this user's account info
         rs = pstmt.executeQuery();     

         if (rs.next()) {

            email = rs.getString(1);         // email addr
            email2 = rs.getString(2);
            message = rs.getString(3);       // Get last message displayed at login
            mobile_count = rs.getInt(4);       // mobile logins for user
         }
         pstmt.close();

      }
      catch (SQLException exc) {
      }
      
      // Detect if the user has an aol.com email address
      if (email.toLowerCase().endsWith("aol.com") || email2.toLowerCase().endsWith("aol.com")) {
          aolEmailUser = true;
      }

      //
      //   Display the message
      //
      Common_skin.outputHeader(club, sess_activity_id, "ForeTees Message", true, out, req);     // output the page start
      Common_skin.outputBody(club, sess_activity_id, out, req);

      //out.println("<body>");
      out.println("<div id=\"wrapper_login\" align=\"center\">");
      out.println("<div id=\"title\">" +clubName+ "</div>");
      out.println("<div id=\"main_login\" align=\"center\">");
      out.println("<h1>Important Message From ForeTees</h1>");
      out.println("<div class=\"main_message\">");
      out.println("<h2>Please Read</h2><br /><br />");
      // out.println("<center><div class=\"sub_instructions\">");
      out.println("<center>");
      
      //
      //  Attach member message if necessary
      //
      if ((message.equals( "" ) || club.equals("demotom")) && (!aolEmailUser || !displayAolNotice)) {           // if no message sent to this user yet 

         addMsg(new_skin, out, con, req);                    // send msg #1
      }

      if (!message.equals( latest_message ) || (aolEmailUser && displayAolNotice)) {        // if msg #1 not sent yet 

          /*   only 1 msg now
         if (tlt == 0 && foretees_mode > 0 && allowMobile == true && mobile_count == 0) {   // if this is a golf tee time system

            addMsg2(new_skin, out, con);                         // display Mobile message
         }
           */
                
          if (aolEmailUser && displayAolNotice) {
              addMsg2(new_skin, out, con);
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
            if (default_activity_id == dining_activity_id) {     // if dining
               out.println("<form method=\"get\" action=\"Dining_home\">");
            } else {
               out.println("<form method=\"get\" action=\"Member_announce\">");
            }
            
            out.println("<input type=\"submit\" value=\"Continue\" id=\"submit\">");
            out.println("</form>");
         out.println("</td>");
         out.println("</tr>");
      out.println("</table>");
      out.println("</center></div></div>");        
      Common_skin.outputPageEnd(club, default_activity_id, out, req);    // finish the page       
      out.close();
    }
 }
 
 

 // *********************************************************
 //   Add the latest message from ForeTees
 // *********************************************************

 private void addMsg(boolean new_skin, PrintWriter out, Connection con, HttpServletRequest req) {
    
     boolean rwd = Utilities.getRequestBoolean(req, ProcessConstants.RQA_RWD, false);
   //
   //  Mobile Access Help Notice (this should change for RWD mode)
   //
   if (new_skin) {

       out.println("<div class=\"sub_instructions\">");
       out.println("<table><tr><td>");
       
   } else {
       
       out.println("<BR><table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" width=\"650\">");
       out.println("<tr>");
       out.println("<td align=\"center\">");
       out.println("<H2>Announcing ForeTees Mobile!</H2>");
       out.println("<font size=\"4\" face=\"Arial, Helvetica, Sans-serif\">");
       out.println("<p><u>Please Read</u></p></font>");
       out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");
   }
   out.println("<p>");

   out.println("ForeTees has created a site for <b>Smart Phone</b> access.");
   out.println("<br><br>If you have a smart phone (with web access) or an iPad and would like to manage your <br>tee times from it, then select the ");
   if (new_skin) {
       out.print("<strong>Help?</strong> tab on the ForeTees navigation panel <br>in the upper right corner of the following pages for more information.");
   } else {
       out.print("<img src=\"/" +rev+ "/mobile/images/mobile-off.gif\" border=0>");
       out.print(" link on the ForeTees <br>navigation panel in the upper left corner of the next page for more information.");
   }
  
   out.println("<br><br>Thank you!</p>");

   if (new_skin) {

       out.println("</td></tr></table></div>");
       
   } else {
       
       out.println("</font></td></tr></table>");
   }
   
   
   /*   old msg - not need any longer (as of 1/20/2012)
    * 
   int activities = 0;
     
   //
   //  Determine how many activities this club as defined (Golf, Tennis, etc)
   //
   activities = getActivity.getActivityCount(con);
   
   if (new_skin) {

       out.println("<div class=\"sub_instructions\">");
       
   } else {
       
       out.println("<BR><table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" width=\"650\">");
       out.println("<tr>");
       out.println("<td align=\"center\">");
       out.println("<H2>Important Notice From ForeTees</H2>");
       out.println("<font size=\"4\" face=\"Arial, Helvetica, Sans-serif\">");
       out.println("<p><u>Please Read</u></p></font>");
       out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");     
   }
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

   out.println("<br><br>Thank you!</p>");

   if (new_skin) {
       
       out.println("</div>");
       
   } else {
       
       out.println("</font></td></tr></table>");
   }
    */

 }      // end of addMsg
 
 
 
 //
 //  Mobile count method - bump counter and gather mobile device data
 //
 private static void countMobile(int mobile_count, int mobile_iphone, String user, HttpServletRequest req, Connection con) {
     
 
   PreparedStatement stmt = null;
   
   boolean iphone = false;
 
 
   //
   //  Gather the User Agent String from the request header
   //
   String ua = req.getHeader("user-agent").toLowerCase();

   if (ua.indexOf("iphone") > -1 || ua.indexOf("ipod") > -1) {

	// found an iphone or ipod
      iphone = true;

   } else if(ua.indexOf("ipad") > -1) {                   // checks for future stats !!!!!!!!!!!

	// found an iPad

   } else if(ua.indexOf("android") > -1) {

	// found an android device

   } else if(ua.indexOf("blackberry") > -1) {

	// found a blackberry device

   } else if(ua.indexOf("opera mini") > -1) {

	// found opera mini browser

   } else if(ua.indexOf("windows ce") > -1 || ua.indexOf("smartphone") > -1 || ua.indexOf("iemobile") > -1) {

	// found windows mobile device
   }

   
   
   //
   //   Increment the mobile counter for this member and update the account
   //
   mobile_count++;
   
   if (iphone == true) mobile_iphone++;     // bump iphone counter if its an iPhone
 
   try {
 
      stmt = con.prepareStatement (
         "UPDATE member2b SET mobile_count = ?, mobile_iphone = ? WHERE username = ?");

      stmt.clearParameters();          
      stmt.setInt(1, mobile_count);     // new mobile count  
      stmt.setInt(2, mobile_iphone);    // new iphone count  
      stmt.setString(3, user);          // username 
      stmt.executeUpdate();

      stmt.close();
            
   } catch (Exception ignore) { 
        
   } finally {

     if (stmt != null) {
        try {
           stmt.close();
        } catch (SQLException ignored) {}
     }      
   }
            
 }       // end of countMobile
 

 
 // ***************************************************************************
 //  Prompt iPad user to see if they want to use Mobile or Standard Interface
 // ***************************************************************************
 private void promptIpad(String name, PrintWriter out) {

 
      out.println("<HTML><HEAD><Title>Member iPad Prompt</Title>");
      out.println("</HEAD>");
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<BR><H2>Login Accepted</H2><BR>");
      out.println("<table width=\"70%\" border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"6\"><tr><td align=\"center\">");
      out.println("<font size=\"3\">");
      out.println("<BR>Welcome <b>" + name );

      out.println("</b><BR><BR>");
      out.println("We have detected that you are using an iPad to access ForeTees.");
      out.println("<BR>Please be advised that our standard site is not yet optimized for this device, so you may encounter problems.");
      out.println("<BR><BR>We suggest that you use our Mobile site (for Golf) with this device to ensure full functionality.");
      out.println("<BR><BR>");

      out.println("<form method=\"get\" action=\"Member_msg\">");

      out.println("<input type=\"submit\" name=\"useMobile\" value=\"Go To Mobile Site (Golf)\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("<BR><BR>");
      out.println("<input type=\"submit\" name=\"useStandard\" value=\"Go To Standard Site\" style=\"text-decoration:underline; background:#8B8970\">");
      
      out.println("</form></font></td></tr></table>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
  }
 
 

 // *********************************************************
 //   Add the latest message from ForeTees
 // *********************************************************

 private void addMsg(boolean new_skin, PrintWriter out, Connection con) {
    
   //
   //  Mobile Access Help Notice
   //
    /*
   if (new_skin) {

       out.println("<div class=\"sub_instructions\">");
       out.println("<table><tr><td>");
       
   } else {
       
       out.println("<BR><table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" width=\"650\">");
       out.println("<tr>");
       out.println("<td align=\"center\">");
       out.println("<H2>Announcing ForeTees Mobile!</H2>");
       out.println("<font size=\"4\" face=\"Arial, Helvetica, Sans-serif\">");
       out.println("<p><u>Please Read</u></p></font>");
       out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");
   }
   out.println("<p>");

   out.println("ForeTees has created a site for <b>Smart Phone</b> access.");
   out.println("<br><br>If you have a smart phone (with web access) or an iPad and would like to manage your <br>tee times from it, then select the ");
   if (new_skin) {
       out.print("<strong>Help?</strong> tab on the ForeTees navigation panel <br>in the upper right corner of the following pages for more information.");
   } else {
       out.print("<img src=\"/" +rev+ "/mobile/images/mobile-off.gif\" border=0>");
       out.print(" link on the ForeTees <br>navigation panel in the upper left corner of the next page for more information.");
   }
  
   out.println("<br><br>Thank you!</p>");

   if (new_skin) {

       out.println("</td></tr></table></div>");
       
   } else {
       
       out.println("</font></td></tr></table>");
   }
   * 
   */
   
   
   /*   old msg - not need any longer (as of 1/20/2012)
    * 
   int activities = 0;
     
   //
   //  Determine how many activities this club as defined (Golf, Tennis, etc)
   //
   activities = getActivity.getActivityCount(con);
   
   if (new_skin) {

       out.println("<div class=\"sub_instructions\">");
       
   } else {
       
       out.println("<BR><table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" width=\"650\">");
       out.println("<tr>");
       out.println("<td align=\"center\">");
       out.println("<H2>Important Notice From ForeTees</H2>");
       out.println("<font size=\"4\" face=\"Arial, Helvetica, Sans-serif\">");
       out.println("<p><u>Please Read</u></p></font>");
       out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");     
   }
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

   out.println("<br><br>Thank you!</p>");

   if (new_skin) {
       
       out.println("</div>");
       
   } else {
       
       out.println("</font></td></tr></table>");
   }
    */

 }      // end of addMsg
 
 
 
 // *****************************************
 //   Add message #2 
 // *****************************************
 
 private void addMsg2(boolean new_skin, PrintWriter out, Connection con) {
    
    /*
     out.println("<div class=\"sub_instructions\">");
     out.println("<table><tr><td>");

     out.println("<h2 style=\"text-align:center;\">Notice regarding notification email delays to AOL email addresses.</h2>");

     out.println("<br><br>It has come to our attention that AOL is currently throttling all emails being sent out by ForeTees.com.  "
             + "This is resulting in emails sent to members with AOL email addresses being delayed significantly.  "
             + "The \"Sent\" timestamp you see in the email does NOT represent the time that ForeTees sent the email, "
             + "but is actually the time when AOL's mail server finally accepted the email and allowed it to go through.");

     out.println("<br><br>Unfortunately, this is completely out of our hands, and since ForeTees is not a customer of AOL, and "
             + "because they are considering emails from ForeTees to be spam, there isn't anything we can do in regards to directly resolving the issue.  "
             + "If they were to fully blacklist all emails from ForeTees, we'd then be able to submit a request to be taken off that blacklist, but they haven't done this yet.  "
             + "This is typically the result of too many AOL members marking ForeTees emails as \"spam\" when they receive them.  "
             + "Most don't realize that this communicates to their email provider that these emails are from a spammer, and once enough of these have been received, "
             + "the email provider (in this case, AOL) start to take action against the assumed spammer (in this case, ForeTees).");

     out.println("<br><br>The best option would be to contact AOL support and let them know that you need to receive emails sent out from foretees.com "
             + "in a timely fashion as they are now being delayed significantly (details on how to contact AOL support can be found by "
             + "clicking <a href=\"http://help.aol.com/help/microsites/microsite.do?cmd=displayKC&docType=kc&externalId=217480\" target=\"_blank\">here</a>).  "
             + "It's possible their lowest tier of support may write off the issue, however hopefully if enough "
             + "of their customers report issues, they will cease the throttling of ForeTees emails.");

     out.append("<br><br>If you have an alternate email address, we suggest that you add it to your ForeTees account, or replace your AOL email address in ForeTees.  "
             + "This can typically be done by contacting the golf shop staff at your club.  While this ideally shouldn't be necessary, it will allow the "
             + "emails sent out from ForeTees to go through to the alternate address immediately, instead of being subjected to AOL\'s delays.");

     out.println("<br><br>Thank you for your continued use of ForeTees.  The ForeTees Team");

     out.println("</td></tr></table></div>");
     * 
     */
   
 }    // end of addMsg2
 
}
