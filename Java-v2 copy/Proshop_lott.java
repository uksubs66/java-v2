/***************************************************************************************
 *   Proshop_lott:  This servlet will process the Lottery request from
 *                    the Proshop's Sheet page.
 *
 *
 *   called by:  Proshop_sheet (doPost)
 *               Proshop_searchmem (doPost)
 *
 *
 *   created: 7/24/2003   Bob P.
 *
 *   last updated:       ******* keep this accurate *******
 *
 *        2/18/14   Call the rechain method when updating recurred requests
 *        2/05/14   Desert Mountain (desertmountain) - allow proshop users to book up to 5 groups (members can do 2).
 *       12/13/13   Rio Verde CC (rioverdecc) - Do not display the 6:24 time as a selectable time option in the lottery registration config screen (case 2333).
 *       11/05/13   Check for recurring requests when a request is updated.  If so, then prompt user to update the future requests too.
 *       11/04/13   Fixed an issue that was causing an error when changing courses on an existing lottery request.
 *        9/17/13   Return to Proshop_searchmem via Proshop_jump so we get back into frame mode.
 *        9/09/13   Make sure there is a time value when the user submits the first page of a request - fixes a null error.
 *        8/30/13   Add support for recurring requests.  Also, change the 'hide notes' option to a checkbox (from Yes/No) so it matches the recurring option
 *                  and is easier to select for the pro.
 *        5/20/13   Cherry Hills CC (cherryhills) - Updated custom to also not allow Non-Resident members from being a part of lottery requests.
 *        2/19/13   Remove the custom to force the mins before and mins after values for Pecan Plantation. Also, do not allow proshop users to override the config values.
 *        1/10/13   Undo the disabling of the guest db for iPad users
 *       11/27/12   Tweak iframe resize code
 *        9/06/12   Check for letter return before checking for a member notice (prevents duplicate display of member notice and allows letter change to work).
 *        6/26/12   Lottery request options - move the course selection so its before the tee time selection so we can update the available times if the
 *                                            course is changed.
 *        5/24/12   Removed no longer needed debug messages.
 *        5/08/12   Cherry Hills CC (cherryhills) - Added custom to prompt proshop users if a Resident Emeritus member is placed in player slot 1 of a lottery request (case 1661).
 *        3/19/12   Added custom processing for dataw to track debug info on new and edited lottery requests for troubleshooting.
 *       10/06/11   Modified alphaTable.nameList() calls to pass an additional parameter used for displaying inact members when modifying past tee times.
 *        3/29/11   Allow for X's in lottery requests.
 *        1/19/11   Dataw Island Club (dataw) - Set default MoT depending on guest type.
 *       12/21/10   Mirasol CC - remove custom forcing mins before and mins after to 3 hrs, handle normally from now on (case 1175).
 *       11/23/10   Change the order that we process the date and time for processing a lottery - do date first so we can determine if it is in DST or not.
 *       10/20/10   Populate new parmEmail fields
 *        6/24/10   Modified alphaTable calls to pass the new enableAdvAssist parameter which is used for iPad compatability
 *        6/08/10   Added code to guest tracking verification to allow for the guest "TBA" option
 *        5/24/10   Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *        4/22/10   Brae Burn CC (braeburncc) - set default mode of trans to 'NAP' for all guest types.
 *        4/15/10   Added guest tracking processing
 *        1/29/10   Fixed the updating of notes (was not saving any changes)
 *        1/25/10   Updated moveguest Javascript function to handle the new use_guestdb value being passed to it.
 *       12/09/09   Finished needed changes for REST_OVERRIDE limited access implementation.  Removed unused checkGuests() method (Common_lott version is used instead)
 *        9/24/09   Black Diamond Ranch - force guests mode of trans to CCT.
 *        9/17/09   Added support for the REST_OVERRIDE limited access proshop user field to block users without access from overriding restrictions.
 *        9/03/09   Changed processing to pull mships from mship5 instead of club5.
 *        8/31/09   Do not include lottery times that have been pre-booked (case 1703).
 *        8/20/09   Change how 'slots' value is passed when course name is changed to prevent # of slots selection from resetting each time
 *        7/30/09   Add focus setting for moveguest js method to improve efficiency and disabled return key in player positions
 *       12/05/08   Added restriction suspension checking for member restrictions
 *       10/06/08   Save the course selected in the lottery req so the pro will know when approving the req (case 1557).
 *       10/03/08   Add a space after any guest names are put in a tee time slot (case 1551).
 *        8/05/08   Added javascript to reload the page when the course selection is changed while setting up a lottery request
 *        8/01/08   Add mins-before and mins-after options - (case #1459).
 *        7/18/08   Added limited access proshop users checks
 *        4/29/08   Pass p5rest after displaying the member message.
 *        3/07/08   Sugar Mill - remove mins before and mins after - force to 4 hours (case #1391).
 *        3/07/08   Mirasol CC - change mins before and mins after to 6 hours.
 *        1/10/08   Trim incoming player names
 *       11/17/07   Pecan Plantation - change default mins before and after to 7 hours.
 *       10/09/07   Fix bug with modifiying lottery requests when a memNotice is in effect.
 *        9/24/07   Pinery CC - Hide front/back text in ltime select box for signup - new bln_hideFrontBack var (Case #1045)
 *        8/20/07   Added Member Notice proshop side display as a configurable option (per notice basis)
 *        8/08/07   Muirfield & Inverness GC - Added Member Notice display to Proshop side
 *        5/24/07   Mirasol CC - default mins before and mins after to 6 hours (case #1175).
 *        2/15/07   Modified the call to alphaTable.nameList to include new boolean for ghin integration
 *       12/13/06   Do not include cross-over times in list of tee times for lottery request.
 *       10/24/06   Set the course name in lreqs3 when updating a request in case it changed.
 *       10/10/06   Use checkGuests in Common_lott as it has been updated.
 *        9/18/06   Move checkGuestQuota, checkMnums & countGuests to Common_lott.
 *        9/05/06   Rancho Bernardo - do not send email notifications.
 *        6/22/06   Lakewood CC - remove mins before and mins after - default them to high value.
 *        6/08/06   Added confirmation to 'Cancel Request' button
 *        5/17/06   Rancho Bernardo - force the mode of trans to 'CCH' for all guest types.
 *        5/02/06   Rancho Bernardo - remove mins before and mins after - default them to high value.
 *        3/10/06   Cherry Hills - add a custom restriction for member types.
 *        3/07/06   Add ability to list names by mship and/or mtype groups.
 *        3/07/06   Cherry Hills - remove mins before and mins after - default them to high value.
 *        9/27/05   Use getParms.getTmodes instead of getCourse to get the modes of trans.
 *        8/07/05   Mission Viejo and Pecan Plantation - change default mins before & after to 4 hours.
 *        6/17/05   Westchester - do not send emails if lstate = 3 (after cut-off).
 *        6/06/05   Allow for multiple courses and 'Weighted Proximity' lottery type.
 *        4/26/05   Do not display tee times if event or blocker in teecurr.
 *        4/11/05   Do not filter the Lottery name - caused problems with times not displayed.
 *        2/11/05   Ver 5 - Northridge - change default before and after minutes to 240.
 *        2/11/05   Ver 5 - change default before and after minutes to 60 (was 0).
 *        1/24/05   Ver 5 - change club2 to club5.
 *       11/16/04   Ver 5 - Allow member to schedule more than one tee time per day.
 *                         Improve layout to provide quicker access to member names.
 *        9/16/04   Ver 5 - change getClub from SystemUtils to common.
 *        2/09/04   Add separate 9-hole option.
 *        2/06/04   Add support for configurable transportation modes.
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

// foretees imports
import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.getClub;
import com.foretees.common.parmSlot;
import com.foretees.common.verifySlot;
import com.foretees.common.parmEmail;
import com.foretees.common.sendEmail;
import com.foretees.common.alphaTable;
import com.foretees.common.Utilities;


public class Proshop_lott extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 //*****************************************************
 // Process the request from Proshop_sheet
 //*****************************************************
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


   PreparedStatement pstmt3 = null;
   Statement stmt = null;
   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

   if (session == null) {

      return;
   }

   Connection con = SystemUtils.getCon(session);                      // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, please contact customer support.");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }
   
   
   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "LOTT_UPDATE", con, out)) {
       SystemUtils.restrictProshop("LOTT_UPDATE", out);
       return;
   }


   //
   // Process request according to which 'submit' button was selected
   //
   //      'time:fb'   - request from Proshop_sheet or Proshop_searchmem
   //      'continue'  - request details from Proshop_lott
   //      'submitForm'    - lottery request from Proshop_lott
   //      'letter'    - request to list member names from Proshop_lott
   //      'remove'    - a 'cancel lottery req' request from Proshop_lott (remove all names)
   //      'cancel'    - user clicked on the 'Go Back' button (return w/o changes)
   //      'return'  - a return to Proshop_lott from verify
   //
   if (req.getParameter("cancel") != null) {

      cancel(req, out, con);                      // process cancel request
      return;
   }

   if (req.getParameter("submitForm") != null) {

      verify(req, out, con, session, resp);                 // process reservation requests
      return;
   }

   if (req.getParameter("recurPrompt") != null) {

      doRecur(req, out, con, session, resp);                 // process recurrence prompt response
      return;
   }

   if (req.getParameter("updateRecur") != null) {

      updateRecur(req, out, con, session, resp);                 // process recurrence prompt response
      return;
   }

   String returnCourse = "";

   //
   //  Get this session's username
   //
   String club = (String)session.getAttribute("club");
   String user = (String)session.getAttribute("user");
   String mshipOpt = (String)session.getAttribute("mshipOpt");
   String mtypeOpt = (String)session.getAttribute("mtypeOpt");

   if (mshipOpt.equals( "" ) || mshipOpt == null) {

      mshipOpt = "ALL";
   }
   if (mtypeOpt.equals( "" ) || mtypeOpt == null) {

      mtypeOpt = "ALL";
   }

   boolean enableAdvAssist = Utilities.enableAdvAssist(req);
  
   String index = req.getParameter("index");          //  index value of day (needed by Proshop_sheet when returning)
   String course = req.getParameter("course");        //  Name of Course

   if (req.getParameter("returnCourse") != null) {        // if returnCourse provided

      returnCourse = req.getParameter("returnCourse");
   }
     
   int recur_count = 0;
   long lottid = 0;
   
   String slottid = "";
   String index2 = "";

   boolean bln_hideFrontBack = false; // hide front/back text in ltime select box during signup
   if ( club.equals("pinery") ) bln_hideFrontBack = true;
   
   if (index.equals( "777" )) {                      // if originated from Proshop_mlottery

      index2 = req.getParameter("index2");           //  index value of day (needed by Proshop_mlottery on return)
   }

   if (req.getParameter("remove") != null) {         // user wants to delete the request 

      slottid = req.getParameter("lottid");          // get the lottery id

      try {
         lottid = Long.parseLong(slottid);
      }
      catch (NumberFormatException e) {
         // ignore error
      }

      if (lottid > 0 && req.getParameter("remove-yes") == null && req.getParameter("remove-no") == null) {
         
         //
         //  user wants to delete a request - check if there are any recurring requests following this one
         //
         recur_count = Common_Lott.checkRecurReq(lottid, con);   // get the number of recurring requests after this one

         if (recur_count > 0) {       // if any - inform user
                  
            out.println("<HTML>");
            out.println("<HEAD><link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">");
            out.println("<Title>Proshop Lottery Request Page</Title></HEAD>");
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Cancel Requested</H3>");
            
            if (recur_count > 1) {
               out.println("<strong>ATTENTION:</strong> &nbsp;There are " +recur_count+ " recurring requests scheduled after this one.<BR>");
               out.println("Would you like to remove those requests too?<BR>");
            } else {
               out.println("<strong>ATTENTION:</strong> &nbsp;There is 1 recurring request scheduled after this one.<BR>");
               out.println("Would you like to remove the other request too?<BR>");
            }
            out.println("<BR><form method=\"post\" action=\"Proshop_lott\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" +index+ "\">");
            out.println("<input type=\"hidden\" name=\"lottid\" value=\"" +lottid+ "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" +course+ "\">");
            
            if (index.equals( "777" )) {                      // if originated from Proshop_mlottery

               out.println("<input type=\"hidden\" name=\"index2\" value=\"" +index2+ "\">");
            }
            if (req.getParameter("returnCourse") != null) {        // if returnCourse provided

               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" +returnCourse+ "\">");
            }
            out.println("<input type=\"hidden\" name=\"remove\" value=\"yes\">");
            out.println("<input type=\"submit\" name=\"remove-yes\" value=\"Yes, Remove Other Requests Too\" style=\"text-decoration:underline; background:#8B8970\">&nbsp;&nbsp;&nbsp;");
            out.println("<input type=\"submit\" name=\"remove-no\" value=\"No, Only Remove this Request\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form><BR>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;          // return back here again after user selects yes or no (falls through to code directly below)     
         }
      }
         
      if (lottid > 0) {
         
         if (req.getParameter("remove-yes") == null) {

            try {

               pstmt3 = con.prepareStatement (
                        "Delete FROM lreqs3 WHERE id = ?");

               pstmt3.clearParameters();               // clear the parms
               pstmt3.setLong(1, lottid);
               pstmt3.executeUpdate();                 // execute the prepared stmt

               pstmt3.close();
            }
            catch (Exception e1) {
               dbError(out, e1);
               return;
            }
            
         } else {
            
            //
            //   User wants to delete this request and all other recurring requests that follow it
            //
            long recur_id = 0;
            long first_date = 0;

            try {    

               pstmt3 = con.prepareStatement (
                  "SELECT date, recur_id " +
                  "FROM lreqs3 WHERE id = ?");

               pstmt3.clearParameters();      
               pstmt3.setLong(1, lottid);      
               rs = pstmt3.executeQuery();    

               if (rs.next()) {

                  first_date = rs.getLong("date");  
                  recur_id = rs.getLong("recur_id");   // get this request's recur_id
               }
    
               pstmt3 = con.prepareStatement (
                        "Delete FROM lreqs3 WHERE date >= ? AND recur_id = ?");

               pstmt3.clearParameters();               // clear the parms
               pstmt3.setLong(1, first_date);
               pstmt3.setLong(2, recur_id);
               pstmt3.executeUpdate();                 // execute the prepared stmt

               pstmt3.close();
            }
            catch (Exception e1) {
               dbError(out, e1);
               return;
            }
            
         }
      }
      
      
      //
      //  Prompt user to return to Proshop_sheet or Proshop_searchmem (index = 888)
      //
      //  These returns will pause for 1 second, then return automatically if meta supported, else user clicks on 'return'
      //
      if (index.equals( "888" )) {       // if originated from Proshop_searchmem

         out.println("<HTML>");
         out.println("<HEAD><link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">");
         out.println("<Title>Proshop Lottery Request Page</Title>");
         out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Proshop_jump?search=yes\">");
         out.println("</HEAD>");
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><BR><H3>Cancel Requested</H3>");
         if (lottid > 0) {
            if (req.getParameter("remove-yes") != null) {    // if we deleted recurring requests too
               out.println("<BR><BR>Thank you, the lottery request and the recurring requests have been removed from the system.");
            } else {
               out.println("<BR><BR>Thank you, the lottery request has been removed from the system.");
            }
         } else {
            out.println("<BR><BR>You are attempting to cancel a lottery request that does not yet exist.");
         }
         out.println("<BR><BR>");

         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"Proshop_jump\">");
         out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font>");

      } else {

         if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
            course = returnCourse;
         }
              
         if (index.equals( "777" )) {       // if originated from Proshop_mlottery

            out.println("<HTML>");
            out.println("<HEAD><link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">");
            out.println("<Title>Proshop Lottery Request Page</Title>");
            out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Proshop_jump?course=" + course + "&index2=" + index2 + "\">");
            out.println("</HEAD>");
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Cancel Requested</H3>");
            if (lottid > 0) {
               if (req.getParameter("remove-yes") != null) {    // if we deleted recurring requests too
                  out.println("<BR><BR>Thank you, the lottery request and the recurring requests have been removed from the system.");
               } else {
                  out.println("<BR><BR>Thank you, the lottery request has been removed from the system.");
               }
            } else {
               out.println("<BR><BR>You are attempting to cancel a lottery request that does not yet exist.");
            }
            out.println("<BR><BR>");

            out.println("<font size=\"2\">");
            out.println("<form method=\"post\" action=\"Proshop_jump\">");
            out.println("<input type=\"hidden\" name=\"index2\" value=\"" + index2 + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");

         } else {

            out.println("<HTML>");
            out.println("<HEAD><link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">");
            out.println("<Title>Proshop Lottery Request Page</Title>");
            out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Proshop_jump?index=" + index + "&course=" + course + "\">");
            out.println("</HEAD>");
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Return/Cancel Requested</H3>");
            if (lottid > 0) {
               if (req.getParameter("remove-yes") != null) {    // if we deleted recurring requests too
                  out.println("<BR><BR>Thank you, the lottery request and the recurring requests have been removed from the system.");
               } else {
                  out.println("<BR><BR>Thank you, the lottery request has been removed from the system.");
               }
            } else {
               out.println("<BR><BR>You are attempting to cancel a lottery request that does not yet exist.");
            }
            out.println("<BR><BR>");

            out.println("<font size=\"2\">");
            out.println("<form action=\"Proshop_jump\" method=\"post\">");
            out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");
         }
      }
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   //
   //  Request from Proshop_sheet, Proshop_lott or Proshop_searchmem
   //
   int hr = 0;
   int min = 0;
   int time = 0;
   int fb = 0;
   int courseCount = 0;
   int checkothers = 0;
   int i = 0;
   int hide = 0;
   int slots = 0;
   int mins_before = 0;
   int mins_after = 0;
   int players = 0;
   int lstate = 0;
   int dhr = 0;
   int dmin = 0;
   int lmin = 0;
   int dfb = 0;
   int in_use = 0;
   int groups = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int p96 = 0;
   int p97 = 0;
   int p98 = 0;
   int p99 = 0;
   int p910 = 0;
   int p911 = 0;
   int p912 = 0;
   int p913 = 0;
   int p914 = 0;
   int p915 = 0;
   int p916 = 0;
   int p917 = 0;
   int p918 = 0;
   int p919 = 0;
   int p920 = 0;
   int p921 = 0;
   int p922 = 0;
   int p923 = 0;
   int p924 = 0;
   int p925 = 0;
   int guest_id1 = 0;
   int guest_id2 = 0;
   int guest_id3 = 0;
   int guest_id4 = 0;
   int guest_id5 = 0;
   int guest_id6 = 0;
   int guest_id7 = 0;
   int guest_id8 = 0;
   int guest_id9= 0;
   int guest_id10 = 0;
   int guest_id11 = 0;
   int guest_id12 = 0;
   int guest_id13 = 0;
   int guest_id14 = 0;
   int guest_id15 = 0;
   int guest_id16 = 0;
   int guest_id17 = 0;
   int guest_id18 = 0;
   int guest_id19 = 0;
   int guest_id20 = 0;
   int guest_id21 = 0;
   int guest_id22 = 0;
   int guest_id23 = 0;
   int guest_id24 = 0;
   int guest_id25 = 0;
   int allowmins = 0;    // config options from lottery3
   int minsbefore = 0;
   int minsafter = 0;
   int slotSelection = 0;
   int allowx = 0;
   int recurrpro = 0;

   long mm = 0;
   long dd = 0;
   long yy = 0;
   long temp = 0;
   long date = 0;
   long edate = 0;
     
   String player1 = "";            // allow for 5 groups of five
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String player6 = "";
   String player7 = "";
   String player8 = "";
   String player9 = "";
   String player10 = "";
   String player11 = "";
   String player12 = "";
   String player13 = "";
   String player14 = "";
   String player15 = "";
   String player16 = "";
   String player17 = "";
   String player18 = "";
   String player19 = "";
   String player20 = "";
   String player21 = "";
   String player22 = "";
   String player23 = "";
   String player24 = "";
   String player25 = "";
   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";
   String p6cw = "";
   String p7cw = "";
   String p8cw = "";
   String p9cw = "";
   String p10cw = "";
   String p11cw = "";
   String p12cw = "";
   String p13cw = "";
   String p14cw = "";
   String p15cw = "";
   String p16cw = "";
   String p17cw = "";
   String p18cw = "";
   String p19cw = "";
   String p20cw = "";
   String p21cw = "";
   String p22cw = "";
   String p23cw = "";
   String p24cw = "";
   String p25cw = "";

   String dampm = "";
   String sdate = "";
   String stime = "";
   String ltime = "";
   String sfb = "";
   String dsfb = "";
   String notes = "";
   String jump = "0";                     // jump index - default to zero (for _sheet)
   String smins_before = "";              // 'minutes before' selected time
   String smins_after = "";               // 'minutes after' selected time
   String shr = "";
   String smin = "";
   String ampm = "";
   String in_use_by = "";
   String courseName = "";
   String courseL = "";
   String temps = "";

   boolean reload = false;
   boolean isRecurr = false;

   //
   //  array to hold course names
   //
   ArrayList<String> courseA = new ArrayList<String>();

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con); // golf only feature

   //
   //  parm block to hold the course parameters
   //
   parmCourse parmc = new parmCourse();          // allocate a parm block

   //
   // Get all the parameters entered
   //
   String day_name = req.getParameter("day");         //  name of the day
   String p5 = req.getParameter("p5");                //  5-somes supported
   String p5rest = req.getParameter("p5rest");        //  5-somes restricted
   String lottName = req.getParameter("lname");       //  Name of the Lottery
   String sslots = "";                                //  # of groups allowed for the Lottery
   if (req.getParameter("reload") != null) {
       sslots = req.getParameter("maxSlots");
       slotSelection = Integer.parseInt(req.getParameter("slots"));
       reload = true;
   } else {
       sslots = req.getParameter("slots");
       slotSelection = 0;
       reload = false;
   }
   String slstate = req.getParameter("lstate");       //  Current state of the Lottery (must be < 4 to get here)
                                        //    1 = before time to take requests (too early for requests)
                                        //    2 = after start time, before stop time (ok to take requests)
                                        //    3 = after stop time, before process time (late, but still ok for pro)
                                        //    4 = requests have already been processed (ok for all tee times now)

     
   //
   //  Convert the string values to ints
   //
   try {
      slots = Integer.parseInt(sslots);
      lstate = Integer.parseInt(slstate);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   //  
   //  check if lottery req id was passed - if not, new request
   //
   lottid = 0;          // new request
        
   if (req.getParameter("lottid") != null) {            

      slottid = req.getParameter("lottid");       // get lottery id if provided
        
      try {
         lottid = Long.parseLong(slottid);
      }
      catch (NumberFormatException e) {
         // ignore error
      }
   }

   if (req.getParameter("jump") != null) {            // if jump index provided

      jump = req.getParameter("jump");
   }

   //
   //  Get this year
   //
   Calendar cal = new GregorianCalendar();       // get todays date
   int thisYear = cal.get(Calendar.YEAR);            // get the year


   //
   //  Get the 'X' option for this lottery
   //
   allowx = getXoption(lottName, con);


   if (((req.getParameter("letter") != null) || (req.getParameter("mins_before") != null) ||
       (req.getParameter("return") != null) || (req.getParameter("mtypeopt") != null) || 
       (req.getParameter("memNotice") != null)) && req.getParameter("reload") == null) {
      //
      // a re-entry - user prompted for details or user clicked on a name letter
      //
      sdate = req.getParameter("sdate");

      if (req.getParameter("stime") != null) {

         stime = req.getParameter("stime");
         sfb = req.getParameter("fb");
           
      } else {                                        // time and fb contained in ltime (from lottery info prompt) 

         if (req.getParameter("ltime") != null) {

             ltime = req.getParameter("ltime");
         }
      }
      smins_before = req.getParameter("mins_before");
      smins_after = req.getParameter("mins_after");
      temps = req.getParameter("checkothers");

      try {
         mins_before = Integer.parseInt(smins_before);
         mins_after = Integer.parseInt(smins_after);
         checkothers = Integer.parseInt(temps);
      }
      catch (NumberFormatException e) {
         // ignore error
      }

      if (req.getParameter("mtypeopt") != null) {

         mtypeOpt = req.getParameter("mtypeopt");
         session.setAttribute("mtypeOpt", mtypeOpt);   //  Save the member class options in the session for next time
      }
      if (req.getParameter("mshipopt") != null) {
         mshipOpt = req.getParameter("mshipopt");
         session.setAttribute("mshipOpt", mshipOpt);
      }

      //
      //  determine # of players allowed for this request
      //
      //   Players will = 4, 5, 8, 10, 12, 15, 16, 20 or 25 (based on 4 or 5-somes and number of slots/groups)
      //
      if (p5.equals( "Yes" ) && p5rest.equals( "No" )) {   // 5-somes allowed and not restricted ?

         players = slots * 5;                              // Yes, set total # of players

      } else {

         p5 = "No";
         players = slots * 4;                              // No
      }

   } else {
      
      //
      //    This is the first call (from _sheet, etc.) -
      //
      mins_before = 999;         // indicate new request (will get overridden if not)
      mins_after = 999;

      if (lottid > 0) {     // if not a new request

         //
         //  Check if this request is already in use
         //
         int in_use_count = 0;
         
         if (req.getParameter("reload") == null) {
             
             try {

                PreparedStatement pstmt1 = con.prepareStatement (
                   "UPDATE lreqs3 SET in_use = 1, in_use_by = ? WHERE id = ? AND in_use = 0");

                pstmt1.clearParameters();        // clear the parms
                pstmt1.setString(1, user);
                pstmt1.setLong(2, lottid);

                in_use_count = pstmt1.executeUpdate();      // count should be 1 if updated ok (not already in use)

                pstmt1.close();
             }
             catch (Exception e2) {
                dbError(out, e2);
                return;
             }

             if (in_use_count == 0) {              // if time slot already in use (update failed)

                out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
                out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
                out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                out.println("<CENTER><BR><BR><H3>Lottery Request is Busy</H3>");
                out.println("<BR><BR>Sorry, but this request is currently busy.<BR>");
                out.println("<BR>Please try again later.");
                out.println("<BR><BR>");
                out.println("<font size=\"2\">");
                out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
                out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
                out.println("</form></font>");
                out.println("</CENTER></BODY></HTML>");
                out.close();
                return;
             }
         }
         
         //
         //   Ok - get existing values from request
         //
         try {
            PreparedStatement pstmt = con.prepareStatement (
               "SELECT minsbefore, minsafter, groups, checkothers " +
               "FROM lreqs3 WHERE id = ?");

            pstmt.clearParameters();        // clear the parms
            pstmt.setLong(1, lottid);       // put the parm in pstmt
            rs = pstmt.executeQuery();      // execute the prepared stmt

            while (rs.next()) {

               mins_before = rs.getInt(1);
               mins_after = rs.getInt(2);
               groups = rs.getInt(3);         // # of tee times requested 
               checkothers = rs.getInt(4);
            }
            pstmt.close();

         }
         catch (Exception e1) {
            dbError(out, e1);
            return;
         }
         
         
         /*       OLD WAY - changed to above process on 9/03/2013 to get rid of the synchronized block below - BP
         
         synchronized(this) {        // ************ NOTE:  redo this to get rid of the sync !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

            try {
               PreparedStatement pstmt = con.prepareStatement (
                  "SELECT minsbefore, minsafter, in_use, groups, checkothers, in_use_by " +
                  "FROM lreqs3 WHERE id = ?");

               pstmt.clearParameters();        // clear the parms
               pstmt.setLong(1, lottid);       // put the parm in pstmt
               rs = pstmt.executeQuery();      // execute the prepared stmt

               while (rs.next()) {

                  mins_before = rs.getInt(1);
                  mins_after = rs.getInt(2);
                  in_use = rs.getInt(3);
                  groups = rs.getInt(4);       // # of tee times requested 
                  checkothers = rs.getInt(5);
                  in_use_by = rs.getString(6);
               }

               pstmt.close();

               if (in_use != 0 && !in_use_by.equalsIgnoreCase( user )) {              // if time slot already in use

                  out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
                  out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
                  out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                  out.println("<CENTER><BR><BR><H3>Lottery Request is Busy</H3>");
                  out.println("<BR><BR>Sorry, but this request is currently busy.<BR>");
                  out.println("<BR>Please try again later.");
                  out.println("<BR><BR>");
                  out.println("<font size=\"2\">");
                  out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
                  out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
                  out.println("</form></font>");
                  out.println("</CENTER></BODY></HTML>");
                  out.close();
                  return;
               }
            }
            catch (Exception e1) {
               dbError(out, e1);
               return;
            }

            //
            //  Request is not in use - set it in use now
            //
            try {

               PreparedStatement pstmt1 = con.prepareStatement (
                  "UPDATE lreqs3 SET in_use = 1, in_use_by = ? WHERE id = ?");

               pstmt1.clearParameters();        // clear the parms
               pstmt1.setString(1, user);
               pstmt1.setLong(2, lottid);
               pstmt1.executeUpdate();      // execute the prepared stmt

               pstmt1.close();
            }
            catch (Exception e2) {
               dbError(out, e2);
               return;
            }
         }                   // end of synch
         * 
         */
         
      }                // end of IF lottid
      
      if (req.getParameter("reload") != null && req.getParameter("stime") != null) {     // if a reload of the page

         stime = req.getParameter("stime");
         sfb = req.getParameter("fb");

      } else {
         
          //
          //    The name of the submit button (time) has the front/back indicator appended to it ('time:fb')
          //    The value contains the time value.
          //
          Enumeration enum1 = req.getParameterNames();        // get the parm name passed

          while (enum1.hasMoreElements()) {

             String pname = (String) enum1.nextElement();

             if (pname.startsWith( "time" )) {

                stime = req.getParameter(pname);              //  get value: time of tee time requested (hh:mm AM/PM)

                StringTokenizer tok = new StringTokenizer( pname, ":" );     // space is the default token, use ':'

                sfb = tok.nextToken();                        // skip past 'time:'
                sfb = tok.nextToken();                        // get the front/back indicator from name of submit button
             }
          }
          
          //
          //  Custom for Desert Mountain to always allow proshop users to book up to 5 groups (members can only book 2)
          //
          if (lottid == 0 && club.equals("desertmountain")) {
             
             slots = 5;  
          }
      }
      
      if (!stime.equals( "" )) {
        
         StringTokenizer tok = new StringTokenizer( stime, ": " );     // space is the default token

         shr = tok.nextToken();
         smin = tok.nextToken();
         ampm = tok.nextToken();
      }

      sdate = req.getParameter("date");           //  date of tee time requested (yyyymmdd)

      //
      //  Convert the values from string to int
      //
      try {
         date = Long.parseLong(sdate);
         fb = Integer.parseInt(sfb);
         hr = Integer.parseInt(shr);
         min = Integer.parseInt(smin);
      }
      catch (NumberFormatException e) {
         // ignore error
      }

      //
      //  isolate yy, mm, dd
      //
      yy = date / 10000;
      temp = yy * 10000;
      mm = date - temp;
      temp = mm / 100;
      temp = temp * 100;
      dd = mm - temp;
      mm = mm / 100;

      try {
         //
         //  Get the names of all courses if multiple
         //
         if (!course.equals( "" )) {           // if course specified, then multiple courses supported for this club

            courseA = Utilities.getCourseNames(con);     // get all the course names

            courseCount = courseA.size();               // save the total # of courses for later
         }

         //
         //  Get the config options for this lottery
         //
         pstmt3 = con.prepareStatement (
                  "SELECT edate, courseName, minsbefore, minsafter, allowmins, recurrpro FROM lottery3 WHERE name = ?");

         pstmt3.clearParameters();        // clear the parms
         pstmt3.setString(1, lottName);       // put the parm in stmt
         rs = pstmt3.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            edate = rs.getLong("edate");             // end date for lottery
            courseL = rs.getString("courseName");    // course supported
            minsbefore = rs.getInt("minsbefore");    
            minsafter = rs.getInt("minsafter");    
            allowmins = rs.getInt("allowmins");    
            recurrpro = rs.getInt("recurrpro");      // Can the pro recurr this request ?
         }
         pstmt3.close();              // close the stmt

      }
      catch (Exception e1) {

         dbError(out, e1);
         return;
      }



      //
      //********************************************************************
      //   Build a page to prompt user for lottery request details
      //********************************************************************
      //
      out.println("<HTML>");
      out.println("<HEAD><link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">");
      out.println("<Title>Proshop Lottery Registration Page</Title>");
      
      //
      //**********************************************************************************
      //  Handle onChange event from course selection drop down window
      //**********************************************************************************
      //
      out.println("<script type=\"text/javascript\">");
      out.println("<!--");
      out.println("function courseChange() {");
      out.println("  var e1 = document.createElement(\"input\");");
      out.println("  e1.type = \"hidden\";");
      out.println("  e1.name = \"reload\";");
      out.println("  document.forms[\"reqForm\"].appendChild(e1);");
      out.println("  var e2 = document.createElement(\"input\");");
      out.println("  e2.type = \"hidden\";");
      out.println("  e2.name = \"date\";");
      out.println("  e2.value = \"" + date + "\";");
      out.println("  document.forms[\"reqForm\"].appendChild(e2);");
      out.println("  var e3 = document.createElement(\"input\");");
      out.println("  e3.type = \"hidden\";");
      out.println("  e3.name = \"stime\";");
      out.println("  e3.value = \"" + stime + "\";");
      out.println("  document.forms[\"reqForm\"].appendChild(e3);");
      out.println("  var e4 = document.createElement(\"input\");");
      out.println("  e4.type = \"hidden\";");
      out.println("  e4.name = \"sfb\";");
      out.println("  e4.value = \"" + sfb + "\";");
      out.println("  document.forms[\"reqForm\"].appendChild(e4);");
      out.println("  var e5 = document.createElement(\"input\");");
      out.println("  e5.type = \"hidden\";");
      out.println("  e5.name = \"maxSlots\";");
      out.println("  e5.value = \"" + slots + "\";");
      out.println("  document.forms[\"reqForm\"].appendChild(e5);");
      out.println("  document.forms[\"reqForm\"].submit();");
      out.println("}");        // End of function onCourseChange()
      out.println("// -->");
      out.println("</script>");    // End of script
   
      out.println("</HEAD>");

      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\" topmargin=\"0\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<table border=\"0\" width=\"100%\" valign=\"top\" align=\"center\">");  // large table for whole page
      out.println("<tr><td valign=\"top\" align=\"center\">");
         out.println("<br><br>");

      out.println("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#CCCCAA\" align=\"center\" valign=\"top\">");
        out.println("<tr><td align=\"left\" width=\"300\">");
        out.println("&nbsp;&nbsp;&nbsp;<b>ForeTees</b>");
        out.println("</td>");

        out.println("<td align=\"center\">");
        out.println("<font size=\"5\">Golf Shop Lottery Request</font>");
        out.println("</font></td>");

        out.println("<td align=\"center\" width=\"300\">");
        out.println("<font size=\"1\" color=\"#000000\">Copyright&nbsp;</font>");
        out.println("<font size=\"2\" color=\"#000000\">&#169;&nbsp;</font>");
        out.println("<font size=\"1\" color=\"#000000\">ForeTees, LLC <br> " +thisYear+ " All rights reserved.");
        out.println("</font></td>");
      out.println("</tr></table>");

      out.println("<br><table border=\"1\" cols=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"3\">");
         out.println("<tr>");
         out.println("<td width=\"620\" align=\"center\">");
         out.println("<font size=\"3\">");
         out.println("<b>Lottery Registration</b><br></font>");

         //
         //  Header msg is dependent on the current lottery state
         //
         if (lstate < 2) {
            out.println("<font size=\"4\">");
            out.println("<b>*** Warning ***</b></font><font size=\"2\">&nbsp;&nbsp;You are posting a lottery ");
            out.println("request before the date or time<br>specified to start accepting requests. ");
            out.println(" Click on 'Go Back' if you want to return and wait.</font><br><br>");
         }
         if (lstate == 3) {
            out.println("<font size=\"4\">");
            out.println("<b>*** Warning ***</b></font><font size=\"2\">&nbsp;&nbsp;You are posting a lottery ");
            out.println("request after the date or time<br>specified to stop accepting requests. ");
            out.println(" Click on 'Go Back' if you want to return.</font><br><br>");
         }
         out.println("<font size=\"2\">");
         out.println("Provide the requested information below and click on 'Continue With Request' to continue.");
         out.println("<br>OR click on 'Cancel Request' to delete the request.");

         out.println("</font></td></tr>");
         out.println("</table>");

         out.println("<font size=\"2\"><br><br>");
         out.println("Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy + "</b>");
         if (!course.equals( "" )) {
            out.println(" &nbsp;&nbsp;&nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + course + "</b>");
         }
         out.println("</font>");

         out.println("<table border=\"0\" align=\"center\" cellpadding=\"5\" cellspacing=\"5\">"); // table to contain 2 tables below

          out.println("<tr>");
          out.println("<form action=\"Proshop_lott\" method=\"post\" name=\"reqForm\">");
               out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + sdate + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
               out.println("<input type=\"hidden\" name=\"index2\" value=\"" + index2 + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"p5\" value=\"" + p5 + "\">");
               out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + p5rest + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
               out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lottName + "\">");
               out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate + "\">");
               out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + lottid + "\">");
          out.println("<td align=\"center\" valign=\"top\">");

            out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\" width=\"100%\">");  // table for request details
            out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<b>Lottery Request Details</b>");
            out.println("</font></td></tr>");

            out.println("<tr><td align=\"left\">");
               out.println("<font size=\"2\"><br>");

               if (courseCount > 0 && courseL.equals( "-ALL-" )) {     // if multiple courses supported for this club and lottery

                  out.println("&nbsp;&nbsp;Course Requested:&nbsp;&nbsp;");
                  out.println("<b>Course:</b>&nbsp;&nbsp;");
                  out.println("<select size=\"1\" name=\"course\" onChange=\"courseChange()\">");

                  for (i=0; i < courseA.size(); i++) {

                     courseName = courseA.get(i);      // get course name from array
                     
                     if (courseName.equals( course )) {
                        out.println("<option selected value=\"" + courseName + "\">" + courseName + "</option>");
                     } else {
                        out.println("<option value=\"" + courseName + "\">" + courseName + "</option>");
                     }
                  }
                  out.println("</select>");
                  out.println("&nbsp;&nbsp;<br><br>");

                  if (courseCount > 2) {
                     out.println("&nbsp;&nbsp;Try other courses if times not available?:&nbsp;&nbsp;");
                  } else {
                     out.println("&nbsp;&nbsp;Try the other course if times not available?:&nbsp;&nbsp;");
                  }
                  out.println("<select size=\"1\" name=\"checkothers\">");

                  if (mins_before == 999) {                            // if new request (default to Yes)
                     out.println("<option value=\"0\">No</option>");
                     out.println("<option selected value=\"1\">Yes</option>");
                  } else {
                     if (checkothers == 0) {
                        out.println("<option selected value=\"0\">No</option>");
                        out.println("<option value=\"1\">Yes</option>");
                     } else {
                        out.println("<option value=\"0\">No</option>");
                        out.println("<option selected value=\"1\">Yes</option>");
                     }
                  }
                  out.println("</select>");
                  out.println("&nbsp;&nbsp;<br><br>");

               } else {

                  out.println("<input type=\"hidden\" name=\"course\" value=\"" +course+ "\">");
                  out.println("<input type=\"hidden\" name=\"checkothers\" value=\"0\">");
               }

               out.println(" &nbsp;&nbsp;Time and Tee Requested:&nbsp;&nbsp;");
               out.println("<select size=\"1\" name=\"ltime\">");                    // ltime parm (time and f/b)
               //
               //  Find all the matching available tee times for this lottery
               //
               try {
                  PreparedStatement pstmtd1 = con.prepareStatement (
                     "SELECT hr, min, fb " +
                     "FROM teecurr2 WHERE date = ? AND event = '' AND lottery = ? AND courseName = ? AND fb < 2 AND player1 = '' AND blocker = '' " +
                     "ORDER BY time, fb");

                  pstmtd1.clearParameters();          // clear the parms
                  pstmtd1.setLong(1, date);
                  pstmtd1.setString(2, lottName);
                  pstmtd1.setString(3, course);

                  rs = pstmtd1.executeQuery();      // find all matching lottery times

                  while (rs.next()) {

                    dhr  = rs.getInt(1);
                    dmin  = rs.getInt(2);
                    dfb  = rs.getInt(3);
                
                    if (club.equals("rioverdecc") && dhr == 6 && dmin == 24) {
                        continue;
                    }

                    if (dfb < 2) {          // not a cross-over time

                       dampm = "AM";
                       if (dhr == 12) {
                          dampm = "PM";
                       }
                       if (dhr > 12) {
                          dhr = dhr - 12;
                          dampm = "PM";
                       }
                       if (dfb == 0) {
                          dsfb = " Front";
                       } else {
                          dsfb = " Back";
                       }

                       if (bln_hideFrontBack) dsfb = ""; // Hide Front/Back text in select box
                       
                       if (dmin < 10) {
                          if ((dhr == hr) && (dmin == min) && (dfb == fb) && (dampm.equals( ampm ))) {
                             out.println("<option selected value=\"" + dhr + ":" + dmin + " " + dampm + " " + dfb + "\">" + dhr + ":0" + dmin + " " + dampm + " " + dsfb + "</option>");
                          } else {
                             out.println("<option value=\"" + dhr + ":" + dmin + " " + dampm + " " + dfb + "\">" + dhr + ":0" + dmin + " " + dampm + " " + dsfb + "</option>");
                          }
                       } else {
                          if ((dhr == hr) && (dmin == min) && (dfb == fb) && (dampm.equals( ampm ))) {
                             out.println("<option selected value=\"" + dhr + ":" + dmin + " " + dampm + " " + dfb + "\">" + dhr + ":" + dmin + " " + dampm + " " + dsfb + "</option>");
                          } else {
                             out.println("<option value=\"" + dhr + ":" + dmin + " " + dampm + " " + dfb + "\">" + dhr + ":" + dmin + " " + dampm + " " + dsfb + "</option>");
                          }
                       }
                    }
                  }                  // end of while
                  pstmtd1.close();

                  out.println("</select>");
                  out.println("&nbsp;&nbsp;<br><br>");

               }
               catch (Exception e1) {

                  dbError(out, e1);
                  return;
               }
                 
               if (slots > 1) {                // if lottery allows more than 1 consecutive tee time
                 
                  out.println("&nbsp;&nbsp;Number of consecutive tee times you wish to request:&nbsp;&nbsp;");
                  out.println("<select size=\"1\" name=\"slots\">");
                    if ((!reload && groups < 2) || (reload && slotSelection == 1)) {
                       out.println("<option selected value=\"01\">1</option>");
                    } else {
                       out.println("<option value=\"01\">1</option>");
                    }
                    if ((!reload && groups == 2) || (reload && slotSelection == 2)) {
                       out.println("<option selected value=\"02\">2</option>");
                    } else {
                       out.println("<option value=\"02\">2</option>");
                    }
                      
                    if (slots > 2) {
                       if ((!reload && groups == 3) || (reload && slotSelection == 3)) {
                          out.println("<option selected value=\"03\">3</option>");
                       } else {
                         out.println("<option value=\"03\">3</option>");
                       }
                    }
                    if (slots > 3) {
                       if ((!reload && groups == 4) || (reload && slotSelection == 4)) {
                          out.println("<option selected value=\"04\">4</option>");
                       } else {
                          out.println("<option value=\"04\">4</option>");
                       }
                    }
                    if (slots > 4) {
                       if ((!reload && groups == 5) || (reload && slotSelection == 5)) {
                          out.println("<option selected value=\"05\">5</option>");
                       } else {
                          out.println("<option value=\"05\">5</option>");
                       }
                    }
                  out.println("</select>");
                  out.println("&nbsp;&nbsp;<br><br>");
               } else {
                  out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots + "\">");
               }
                 
               if (!club.equals( "pecanplantation" )) {   // if NOT Pecan Plantation

                    if (!club.equals( "cherryhills" ) && !club.equals( "ranchobernardo" ) && !club.equals( "lakewood" )) {   // if NOT Cherry Hills & Rancho Bernardo

                        if (mins_before == 999) {   // if new request (default to 120)
                            if (club.equals( "northridge" ) || club.equals( "missionviejo" ) || club.equals( "sugarmill" )) {
                                mins_before = 240;                // 4 hrs
                            } else {
                                if (club.equals( "westchester" )) {
                                mins_before = 180;                // westchester = 3 hrs
                                } else {
                                mins_before = minsbefore;
                                }
                            }
                        }

                        out.println("&nbsp;&nbsp;Number of hours/minutes <b>before</b> this time that are acceptable:&nbsp;&nbsp;");
                        out.println("<select size=\"1\" name=\"mins_before\">");
                        if (mins_before == 0) {
                            out.println("<option selected value=\"0\">None</option>");
                        } else {
                            out.println("<option value=\"0\">None</option>");
                        }
                        if (mins_before == 10) {
                            out.println("<option selected value=\"10\">10 mins</option>");
                        } else {
                            out.println("<option value=\"10\">10 mins</option>");
                        }
                        if (mins_before == 20) {
                            out.println("<option selected value=\"20\">20 mins</option>");
                        } else {
                            out.println("<option value=\"20\">20 mins</option>");
                        }
                        if (mins_before == 30) {
                            out.println("<option selected value=\"30\">30 mins</option>");
                        } else {
                            out.println("<option value=\"30\">30 mins</option>");
                        }
                        if (mins_before == 40) {
                            out.println("<option selected value=\"40\">40 mins</option>");
                        } else {
                            out.println("<option value=\"40\">40 mins</option>");
                        }
                        if (mins_before == 50) {
                            out.println("<option selected value=\"50\">50 mins</option>");
                        } else {
                            out.println("<option value=\"50\">50 mins</option>");
                        }
                        if (mins_before == 60) {
                            out.println("<option selected value=\"60\">1 hr</option>");
                        } else {
                            out.println("<option value=\"60\">1 hr</option>");
                        }
                        if (mins_before == 75) {
                            out.println("<option selected value=\"75\">1 hr 15 mins</option>");
                        } else {
                            out.println("<option value=\"75\">1 hr 15 mins</option>");
                        }
                        if (mins_before == 90) {
                            out.println("<option selected value=\"90\">1 hr 30 mins</option>");
                        } else {
                            out.println("<option value=\"90\">1 hr 30 mins</option>");
                        }
                        if (mins_before == 120) {
                            out.println("<option selected value=\"120\">2 hrs</option>");
                        } else {
                            out.println("<option value=\"120\">2 hrs</option>");
                        }
                        if (mins_before == 150) {
                            out.println("<option selected value=\"150\">2 hrs 30 mins</option>");
                        } else {
                            out.println("<option value=\"150\">2 hrs 30 mins</option>");
                        }
                        if (mins_before == 180) {
                            out.println("<option selected value=\"180\">3 hrs</option>");
                        } else {
                            out.println("<option value=\"180\">3 hrs</option>");
                        }
                        if (mins_before == 210) {
                            out.println("<option selected value=\"210\">3 hrs 30 mins</option>");
                        } else {
                            out.println("<option value=\"210\">3 hrs 30 mins</option>");
                        }
                        if (mins_before == 240) {
                            out.println("<option selected value=\"240\">4 hrs</option>");
                        } else {
                            out.println("<option value=\"240\">4 hrs</option>");
                        }
                        if (mins_before == 300) {
                            out.println("<option selected value=\"300\">5 hrs</option>");
                        } else {
                            out.println("<option value=\"300\">5 hrs</option>");
                        }
                        if (mins_before == 360) {
                            out.println("<option selected value=\"360\">6 hrs</option>");
                        } else {
                            out.println("<option value=\"360\">6 hrs</option>");
                        }
                        out.println("</select>");
                        out.println("&nbsp;&nbsp;<br><br>");

                        if (mins_after == 999) {   // if new request (default to 120)

                            mins_after = minsafter;                // default = 2 hrs (or that configured)

                            if (club.equals( "northridge" ) || club.equals( "sugarmill" )) {
                                mins_after = 240;                //  4 hrs
                            }
                            if (club.equals( "missionviejo" )) {
                                mins_after = 360;                //  6 hrs
                            }
                            if (club.equals( "westchester" )) {
                                mins_after = 180;                // westchester = 3 hrs
                            }
                        }

                        out.println("&nbsp;&nbsp;Number of hours/minutes <b>after</b> this time that are acceptable:&nbsp;&nbsp;");
                        out.println("<select size=\"1\" name=\"mins_after\">");
                        if (mins_after == 0) {
                            out.println("<option selected value=\"0\">None</option>");
                        } else {
                            out.println("<option value=\"0\">None</option>");
                        }
                        if (mins_after == 10) {
                            out.println("<option selected value=\"10\">10 mins</option>");
                        } else {
                            out.println("<option value=\"10\">10 mins</option>");
                        }
                        if (mins_after == 20) {
                            out.println("<option selected value=\"20\">20 mins</option>");
                        } else {
                            out.println("<option value=\"20\">20 mins</option>");
                        }
                        if (mins_after == 30) {
                            out.println("<option selected value=\"30\">30 mins</option>");
                        } else {
                            out.println("<option value=\"30\">30 mins</option>");
                        }
                        if (mins_after == 40) {
                            out.println("<option selected value=\"40\">40 mins</option>");
                        } else {
                            out.println("<option value=\"40\">40 mins</option>");
                        }
                        if (mins_after == 50) {
                            out.println("<option selected value=\"50\">50 mins</option>");
                        } else {
                            out.println("<option value=\"50\">50 mins</option>");
                        }
                        if (mins_after == 60) {
                            out.println("<option selected value=\"60\">1 hr</option>");
                        } else {
                            out.println("<option value=\"60\">1 hr</option>");
                        }
                        if (mins_after == 75) {
                            out.println("<option selected value=\"75\">1 hr 15 mins</option>");
                        } else {
                            out.println("<option value=\"75\">1 hr 15 mins</option>");
                        }
                        if (mins_after == 90) {
                            out.println("<option selected value=\"90\">1 hr 30 mins</option>");
                        } else {
                            out.println("<option value=\"90\">1 hr 30 mins</option>");
                        }
                        if (mins_after == 120) {
                            out.println("<option selected value=\"120\">2 hrs</option>");
                        } else {
                            out.println("<option value=\"120\">2 hrs</option>");
                        }
                        if (mins_after == 150) {
                            out.println("<option selected value=\"150\">2 hrs 30 mins</option>");
                        } else {
                            out.println("<option value=\"150\">2 hrs 30 mins</option>");
                        }
                        if (mins_after == 180) {
                            out.println("<option selected value=\"180\">3 hrs</option>");
                        } else {
                            out.println("<option value=\"180\">3 hrs</option>");
                        }
                        if (mins_after == 210) {
                            out.println("<option selected value=\"210\">3 hrs 30 mins</option>");
                        } else {
                            out.println("<option value=\"210\">3 hrs 30 mins</option>");
                        }
                        if (mins_after == 240) {
                            out.println("<option selected value=\"240\">4 hrs</option>");
                        } else {
                            out.println("<option value=\"240\">4 hrs</option>");
                        }
                        if (mins_after == 300) {
                            out.println("<option selected value=\"300\">5 hrs</option>");
                        } else {
                            out.println("<option value=\"300\">5 hrs</option>");
                        }
                        if (mins_after == 360) {
                            out.println("<option selected value=\"360\">6 hrs</option>");
                        } else {
                            out.println("<option value=\"360\">6 hrs</option>");
                        }
                        out.println("</select>");
                        out.println("&nbsp;&nbsp;<br><br>");

                    } else {

                        //
                        //  Cherry Hills & Rancho Bernardo - do not allow them to specify the mins before or after
                        //
                        out.println("<input type=\"hidden\" name=\"mins_before\" value=\"360\">");    // hard code to 6 hours (always)
                        out.println("<input type=\"hidden\" name=\"mins_after\" value=\"360\">");
                    }

               } else {

                    //
                    //  Pecan Plantation - do not allow them to specify the mins before or after - use the config'd values
                    //
                    out.println("<input type=\"hidden\" name=\"mins_before\" value=\"" +minsbefore+ "\">");   
                    out.println("<input type=\"hidden\" name=\"mins_after\" value=\"" +minsafter+ "\">");
               }
               

               out.println("&nbsp;&nbsp;<b>Note:</b> Tee times to be awarded within the boundaries established for this lottery.<br><br>");
               out.println("</font></td></tr>");

               if (lottid > 0) {

                  recur_count = Common_Lott.checkRecurReq(lottid, con);   // get the number of recurring requests after this one
                  
                  if (recur_count > 0) {       // if any - inform user
                  
                     out.println("<tr><td align=\"center\" bgcolor=\"yellow\">");
                     out.println("<font size=\"2\"><br>");
                  
                     out.println("&nbsp;&nbsp;<b>WARNING:</b> There are " +recur_count+ " recurring request(s) associated with this request at a later date.<br>" +
                                  "&nbsp;&nbsp;If you update this request you will be asked if you would also like to update the recurring requests.&nbsp;&nbsp;<br>" +
                                  "If you elect to only update this request, then it will be separated from the others.<BR><BR>");

                  } else {
                  
                     out.println("<tr><td align=\"center\">");
                     out.println("<font size=\"2\"><br>");
                  }
                  
                  out.println("<input type=submit value=\"Cancel Request\" name=\"remove\" onclick=\"return confirm('Are you sure you want to permanently remove this lottery request?')\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");

               } else {     // new request
                  
                  out.println("<tr><td align=\"center\">");
                  out.println("<font size=\"2\"><br>");
               }
               
               out.println("<input type=submit value=\"Continue With Request\" name=\"continue\">");
               out.println("<br></font></td></tr>");
               out.println("</table>");
          out.println("</td></form>");
          out.println("</tr><tr>");
          out.println("<td align=\"center\">");

          out.println("<font size=\"2\">");

         if (lottid > 0) {
           
            out.println("<form action=\"Proshop_lott\" method=\"post\" name=\"can\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
            out.println("<input type=\"hidden\" name=\"index2\" value=\"" + index2 + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
            out.println("<input type=\"hidden\" name=\"time\" value=" + time + ">");
            out.println("<input type=\"hidden\" name=\"fb\" value=" + fb + ">");
            out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + lottid + "\">");
  
         } else {            // go directly back to caller
           
            if (index.equals( "888" )) {      // if from Proshop_searchmem via proshop_main

               out.println("<form method=\"get\" action=\"Proshop_jump\">");
               out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");

            } else {

               if (index.equals( "777" )) {      // if from Proshop_mlottery

                  out.println("<form method=\"post\" action=\"Proshop_jump\">");
                  out.println("<input type=\"hidden\" name=\"index2\" value=\"" + index2 + "\">");
                  if (!returnCourse.equals( "" )) {
                     out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
                  } else {
                     out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                  }

               } else {

                  out.println("<form action=\"Proshop_jump\" method=\"post\">");
                  out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
                  if (!returnCourse.equals( "" )) {
                     out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
                  } else {
                     out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                  }
               }
            }
         }
         out.println("Return w/o Changes:<br>");
         out.println("<input type=\"submit\" value=\"Go Back\" name=\"cancel\"></form>");

          out.println("</font></td>");
          out.println("</tr>");

        out.println("</table>");
         out.println("</td>");
         out.println("</tr>");
      out.println("</table>");
      out.println("</font></center></body></html>");
      out.close();
      return;                 // exit and wait for reply
   }

   
   
   //
   //  NOT the first call to this class
   //
   
   //
   //   Check if pro is allowed to recur lottery requests (lottery configuration setting)
   //
   if (lottid == 0) {     // if new request
      
      recurrpro = Utilities.getRecurOption(user, lottName, con);
   }     
   
   
   
   if (req.getParameter("return") != null || req.getParameter("memNotice") != null) {     // if this is a return from verify - time = hhmm

      try {
         time = Integer.parseInt(stime);
         date = Integer.parseInt(sdate);
         fb = Integer.parseInt(sfb);
      }
      catch (NumberFormatException e) {
         // ignore error
      }

      //
      //  create a time string for display
      //
      hr = time / 100;
      min = time - (hr * 100);

      ampm = "AM";

      if (hr > 11) {

         ampm = "PM";

         if (hr > 12) {

            hr = hr - 12;
         }
      }
      if (min < 10) {
         stime = hr + ":0" + min + " " + ampm;
      } else {
         stime = hr + ":" + min + " " + ampm;
      }

   } else {

      if (!stime.equals( "" )) {
         //
         //  Parse the time parm to separate hh, mm, am/pm and convert to military time
         //  (received as 'hh:mm xx'   where xx = am or pm)
         //
         StringTokenizer tok = new StringTokenizer( stime, ": " );     // space is the default token

         shr = tok.nextToken();
         smin = tok.nextToken();
         ampm = tok.nextToken();

      } else {        // call is from lottery info prompt - time is in ltime
                
         //
         //  Make sure we have a time value.  If a course was selected where no lottery times are available that day, the pro could hit Continue w/o a time value.
         //
         if (ltime == null || ltime.equals("")) {
            
            out.println(SystemUtils.HeadTitle("Lottery Time Not Selected"));
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<CENTER><BR><BR><H3>Lottery Request Error - No Time Selected</H3>");
            out.println("<BR><BR>Sorry, but you must select a time for this request.");
            out.println("<BR><BR>It is likely that there are no times available for the day or course selected.");
            out.println("<BR><BR>Please go back and check the times available.");
            out.println("<BR><BR>");
            out.println("<font size=\"2\">");
            out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;            
         }
             
         //
         //  Parse the time parm to separate hh, mm, am/pm and convert to military time
         //  (received as 'hh:mm xx fb'   where xx = am or pm, and fb = 0 for front and 1 for back)
         //
         StringTokenizer tok = new StringTokenizer( ltime, ": " );     // space is the default token

         shr = tok.nextToken();
         smin = tok.nextToken();
         ampm = tok.nextToken();
         sfb = tok.nextToken();

         try {
            lmin = Integer.parseInt(smin);
         }
         catch (NumberFormatException e) {
         }

         if (lmin > 9) {
            stime = shr + ":" + smin + " " + ampm;          // create stime value
         } else {
            stime = shr + ":0" + smin + " " + ampm;          // create stime value
         }

         if (lottid > 0) {          // if request already exists
            //
            //  Check if this request is still 'in use' and still in use by this user??
            //
            //  This is necessary because the user may have gone away while holding this req.  If the
            //  slot timed out (system timer), the slot would be marked 'not in use' and another
            //  user could pick it up.  The original holder could be trying to use it now.
            //
            try {

               PreparedStatement pstmt = con.prepareStatement (
                  "SELECT in_use, in_use_by " +
                  "FROM lreqs3 WHERE id = ?");

               pstmt.clearParameters();        // clear the parms
               pstmt.setLong(1, lottid);         // put the parm in pstmt
               rs = pstmt.executeQuery();      // execute the prepared stmt

               if (rs.next()) {

                  in_use = rs.getInt(1);
                  in_use_by = rs.getString(2);
               }

               pstmt.close();

               if ((in_use == 0) || (!in_use_by.equalsIgnoreCase( user ))) {    // if time slot in use and not by this user

                  out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
                  out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
                  out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                  out.println("<CENTER><BR><BR><H3>Reservation Timer Expired</H3>");
                  out.println("<BR><BR>Sorry, but this lottery request has been returned to the system.<BR>");
                  out.println("<BR>The system timed out and released the request.");
                  out.println("<BR><BR>");

                  if (index.equals( "888" )) {      // if from Proshop_searchmem via proshop_main

                     out.println("<font size=\"2\">");
                     out.println("<form method=\"get\" action=\"Proshop_jump\">");
                     out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");
                     out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
                     out.println("</form></font>");

                  } else {

                     if (index.equals( "777" )) {      // if from Proshop_mlottery

                        out.println("<font size=\"2\">");
                        out.println("<form method=\"post\" action=\"Proshop_jump\">");
                        out.println("<input type=\"hidden\" name=\"index2\" value=\"" + index2 + "\">");
                        if (!returnCourse.equals( "" )) {
                           out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
                        } else {
                           out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                        }
                        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
                        out.println("</form></font>");

                     } else {

                        out.println("<font size=\"2\">");
                        out.println("<form action=\"Proshop_jump\" method=\"post\">");
                        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
                        out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
                        if (!returnCourse.equals( "" )) {
                           out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
                        } else {
                           out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                        }
                        out.println("</form></font>");
                     }
                  }
                  out.println("</CENTER></BODY></HTML>");
                  out.close();
                  return;
               }
            }
            catch (Exception ignore) {
            }
            
         } // end if request already exists
         
         // put Check for Member Notice from Pro here ??
         
      } // end time in either stime/ltime

      //
      //  Convert the values from string to int
      //
      try {
         date = Long.parseLong(sdate);
         fb = Integer.parseInt(sfb);
         hr = Integer.parseInt(shr);
         min = Integer.parseInt(smin);
      }
      catch (NumberFormatException e) {
         // ignore error
      }

      if (ampm.equalsIgnoreCase ( "PM" )) {

         if (hr != 12) {

            hr = hr + 12;
         }
      }

      time = hr * 100;
      time = time + min;          // military time

     //
     //   Check for Member Notice from Pro
     //
     if ((req.getParameter("letter") == null) && (req.getParameter("mtypeopt") == null) ) {   // if first time here from above

        String memNotice = verifySlot.checkMemNotice(date, time, fb, course, day_name, "teetime", true, con);    

        if (!memNotice.equals( "" )) {      // if message to display

            //
            //  Display the Pro's Message and then prompt the user to either accept or return to the tee sheet
            //
            out.println("<HTML><HEAD>");
            out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">");
            out.println("<Title>Member Notice For Tee Time Request</Title>");
            out.println("</HEAD>");

            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

            out.println("<table border=\"0\" width=\"100%\" align=\"center\" valign=\"top\">");  // large table for whole page
            out.println("<tr><td valign=\"top\" align=\"center\">");
                out.println("<p>&nbsp;&nbsp;</p>");
                out.println("<p>&nbsp;&nbsp;</p>");
                out.println("<font size=\"3\">");
                out.println("<b>NOTICE FROM YOUR GOLF SHOP</b><br><br><br></font>");

            out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"3\">");
                out.println("<tr>");
                out.println("<td width=\"580\" align=\"center\">");
                out.println("<font size=\"2\">");
                out.println("<br>" + memNotice);
                out.println("</font></td></tr>");
                out.println("</table><br>");

                out.println("</font><font size=\"2\">");
                out.println("<br>Would you like to continue with this request?<br>");
                out.println("<br><b>Please select from the following. DO NOT use you browser's BACK button!</b><br><br>");

                out.println("<table border=\"0\" cols=\"1\" cellpadding=\"3\">");
                out.println("<tr><td align=\"center\">");
                out.println("<font size=\"2\">");

                if (lottid > 0) {

                    out.println("<form action=\"Proshop_lott\" method=\"post\" name=\"can\">");
                    out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                    out.println("<input type=\"hidden\" name=\"index2\" value=\"" + index2 + "\">");
                    out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                    out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
                    out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
                    out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
                    out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                    out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + lottid + "\">");

                } else {            // go directly back to caller

                    // return to Proshop_sheet - must rebuild frames first
                    out.println("<form action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
                    out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                    out.println("<input type=\"hidden\" name=\"index2\" value=\"" + index2 + "\">");
                    out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                    out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");

                }
                out.println("<input type=\"submit\" value=\"NO - Return\" name=\"cancel\"></form>");

                out.println("</font></td>");

                out.println("<td align=\"center\">");
                out.println("<font size=\"2\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                out.println("</font></td>");

                out.println("<td align=\"center\">");
                out.println("<font size=\"2\">");
                    out.println("<form action=\"Proshop_lott\" method=\"post\">");
                    out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                    out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lottName + "\">");
                    out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate + "\">");
                    out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + lottid + "\">");
                    out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                    out.println("<input type=\"hidden\" name=\"index2\" value=\"" + index2 + "\">");
                    out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                    out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
                    out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
                    out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
                    out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
                    out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                    out.println("<input type=\"hidden\" name=\"p5\" value=\"" + p5 + "\">");
                    out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + p5rest + "\">");
                    out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots + "\">");
                    out.println("<input type=\"hidden\" name=\"mins_before\" value=\"" + mins_before + "\">");
                    out.println("<input type=\"hidden\" name=\"mins_after\" value=\"" + mins_after + "\">");
                    out.println("<input type=\"hidden\" name=\"checkothers\" value=\"" + checkothers + "\">");
                    out.println("<input type=\"hidden\" name=\"memNotice\" value=\"yes\">");
                    out.println("<input type=\"submit\" value=\"YES - Continue\"></form>");
                out.println("</font></td></tr>");
                out.println("</table>");

                out.println("</td>");
                out.println("</tr>");
            out.println("</table>");
            out.println("</font></center></body></html>");
            out.close();
            return;

        } // end if memNotice string not empty
        
     }    // end of IF letter or mtypeopt
         
   }
   
   //
   //  isolate yy, mm, dd
   //
   yy = date / 10000;
   temp = yy * 10000;
   mm = date - temp;
   temp = mm / 100;
   temp = temp * 100;
   dd = mm - temp;
   mm = mm / 100;

   //
   //  if user clicked on a letter, or this is a return from verify
   //
   if ((req.getParameter("letter") != null) || (req.getParameter("return") != null) || (req.getParameter("mtypeopt") != null) ) {   

      player1 = req.getParameter("player1");     // get the player info from the player table
      player2 = req.getParameter("player2");
      player3 = req.getParameter("player3");
      player4 = req.getParameter("player4");
      player5 = req.getParameter("player5");
      player6 = req.getParameter("player6");
      player7 = req.getParameter("player7");
      player8 = req.getParameter("player8");
      player9 = req.getParameter("player9");
      player10 = req.getParameter("player10");
      player11 = req.getParameter("player11");
      player12 = req.getParameter("player12");
      player13 = req.getParameter("player13");
      player14 = req.getParameter("player14");
      player15 = req.getParameter("player15");
      player16 = req.getParameter("player16");
      player17 = req.getParameter("player17");
      player18 = req.getParameter("player18");
      player19 = req.getParameter("player19");
      player20 = req.getParameter("player20");
      player21 = req.getParameter("player21");
      player22 = req.getParameter("player22");
      player23 = req.getParameter("player23");
      player24 = req.getParameter("player24");
      player25 = req.getParameter("player25");
      if (req.getParameter("p1cw") != null) {   
         p1cw = req.getParameter("p1cw");
      }
      if (req.getParameter("p2cw") != null) {
         p2cw = req.getParameter("p2cw");
      }
      if (req.getParameter("p3cw") != null) {
         p3cw = req.getParameter("p3cw");
      }
      if (req.getParameter("p4cw") != null) {
         p4cw = req.getParameter("p4cw");
      }
      if (req.getParameter("p5cw") != null) {
         p5cw = req.getParameter("p5cw");
      }
      if (req.getParameter("p6cw") != null) {
         p6cw = req.getParameter("p6cw");
      }
      if (req.getParameter("p7cw") != null) {
         p7cw = req.getParameter("p7cw");
      }
      if (req.getParameter("p8cw") != null) {
         p8cw = req.getParameter("p8cw");
      }
      if (req.getParameter("p9cw") != null) {
         p9cw = req.getParameter("p9cw");
      }
      if (req.getParameter("p10cw") != null) {
         p10cw = req.getParameter("p10cw");
      }
      if (req.getParameter("p11cw") != null) {
         p11cw = req.getParameter("p11cw");
      }
      if (req.getParameter("p12cw") != null) {
         p12cw = req.getParameter("p12cw");
      }
      if (req.getParameter("p13cw") != null) {
         p13cw = req.getParameter("p13cw");
      }
      if (req.getParameter("p14cw") != null) {
         p14cw = req.getParameter("p14cw");
      }
      if (req.getParameter("p15cw") != null) {
         p15cw = req.getParameter("p15cw");
      }
      if (req.getParameter("p16cw") != null) {
         p16cw = req.getParameter("p16cw");
      }
      if (req.getParameter("p17cw") != null) {
         p17cw = req.getParameter("p17cw");
      }
      if (req.getParameter("p18cw") != null) {
         p18cw = req.getParameter("p18cw");
      }
      if (req.getParameter("p19cw") != null) {
         p19cw = req.getParameter("p19cw");
      }
      if (req.getParameter("p20cw") != null) {
         p20cw = req.getParameter("p20cw");
      }
      if (req.getParameter("p21cw") != null) {
         p21cw = req.getParameter("p21cw");
      }
      if (req.getParameter("p22cw") != null) {
         p22cw = req.getParameter("p22cw");
      }
      if (req.getParameter("p23cw") != null) {
         p23cw = req.getParameter("p23cw");
      }
      if (req.getParameter("p24cw") != null) {
         p24cw = req.getParameter("p24cw");
      }
      if (req.getParameter("p25cw") != null) {
         p25cw = req.getParameter("p25cw");
      }
      notes = req.getParameter("notes");
      
      if (req.getParameter("hide") != null) {
         
         hide = 1;        // if Hide Notes checkbox selected
      }
      //hides = req.getParameter("hide");

      if (req.getParameter("mtypeopt") != null) {

         mtypeOpt = req.getParameter("mtypeopt");
         session.setAttribute("mtypeOpt", mtypeOpt);   //  Save the member class options in the session for next time
      }
      if (req.getParameter("mshipopt") != null) {
         mshipOpt = req.getParameter("mshipopt");
         session.setAttribute("mshipOpt", mshipOpt);
      }

      String p9s = "";

      if (req.getParameter("p91") != null) {
         p9s = req.getParameter("p91");
         p91 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p92") != null) {
         p9s = req.getParameter("p92");
         p92 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p93") != null) {
         p9s = req.getParameter("p93");
         p93 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p94") != null) {
         p9s = req.getParameter("p94");
         p94 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p95") != null) {
         p9s = req.getParameter("p95");
         p95 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p96") != null) {
         p9s = req.getParameter("p96");
         p96 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p97") != null) {
         p9s = req.getParameter("p97");
         p97 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p98") != null) {
         p9s = req.getParameter("p98");
         p98 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p99") != null) {
         p9s = req.getParameter("p99");
         p99 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p910") != null) {
         p9s = req.getParameter("p910");
         p910 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p911") != null) {
         p9s = req.getParameter("p911");
         p911 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p912") != null) {
         p9s = req.getParameter("p912");
         p912 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p913") != null) {
         p9s = req.getParameter("p913");
         p913 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p914") != null) {
         p9s = req.getParameter("p914");
         p914 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p915") != null) {
         p9s = req.getParameter("p915");
         p915 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p916") != null) {
         p9s = req.getParameter("p916");
         p916 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p917") != null) {
         p9s = req.getParameter("p917");
         p917 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p918") != null) {
         p9s = req.getParameter("p918");
         p918 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p919") != null) {
         p9s = req.getParameter("p919");
         p919 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p920") != null) {
         p9s = req.getParameter("p920");
         p920 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p921") != null) {
         p9s = req.getParameter("p921");
         p921 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p922") != null) {
         p9s = req.getParameter("p922");
         p922 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p923") != null) {
         p9s = req.getParameter("p923");
         p923 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p924") != null) {
         p9s = req.getParameter("p924");
         p924 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p925") != null) {
         p9s = req.getParameter("p925");
         p925 = Integer.parseInt(p9s);
      }

      guest_id1 = (req.getParameter("guest_id1") != null ? Integer.parseInt(req.getParameter("guest_id1")) : 0);
      guest_id2 = (req.getParameter("guest_id2") != null ? Integer.parseInt(req.getParameter("guest_id2")) : 0);
      guest_id3 = (req.getParameter("guest_id3") != null ? Integer.parseInt(req.getParameter("guest_id3")) : 0);
      guest_id4 = (req.getParameter("guest_id4") != null ? Integer.parseInt(req.getParameter("guest_id4")) : 0);
      guest_id5 = (req.getParameter("guest_id5") != null ? Integer.parseInt(req.getParameter("guest_id5")) : 0);
      guest_id6 = (req.getParameter("guest_id6") != null ? Integer.parseInt(req.getParameter("guest_id6")) : 0);
      guest_id7 = (req.getParameter("guest_id7") != null ? Integer.parseInt(req.getParameter("guest_id7")) : 0);
      guest_id8 = (req.getParameter("guest_id8") != null ? Integer.parseInt(req.getParameter("guest_id8")) : 0);
      guest_id9 = (req.getParameter("guest_id9") != null ? Integer.parseInt(req.getParameter("guest_id9")) : 0);
      guest_id10 = (req.getParameter("guest_id10") != null ? Integer.parseInt(req.getParameter("guest_id10")) : 0);
      guest_id11 = (req.getParameter("guest_id11") != null ? Integer.parseInt(req.getParameter("guest_id11")) : 0);
      guest_id12 = (req.getParameter("guest_id12") != null ? Integer.parseInt(req.getParameter("guest_id12")) : 0);
      guest_id13 = (req.getParameter("guest_id13") != null ? Integer.parseInt(req.getParameter("guest_id13")) : 0);
      guest_id14 = (req.getParameter("guest_id14") != null ? Integer.parseInt(req.getParameter("guest_id14")) : 0);
      guest_id15 = (req.getParameter("guest_id15") != null ? Integer.parseInt(req.getParameter("guest_id15")) : 0);
      guest_id16 = (req.getParameter("guest_id16") != null ? Integer.parseInt(req.getParameter("guest_id16")) : 0);
      guest_id17 = (req.getParameter("guest_id17") != null ? Integer.parseInt(req.getParameter("guest_id17")) : 0);
      guest_id18 = (req.getParameter("guest_id18") != null ? Integer.parseInt(req.getParameter("guest_id18")) : 0);
      guest_id19 = (req.getParameter("guest_id19") != null ? Integer.parseInt(req.getParameter("guest_id19")) : 0);
      guest_id20 = (req.getParameter("guest_id20") != null ? Integer.parseInt(req.getParameter("guest_id20")) : 0);
      guest_id21 = (req.getParameter("guest_id21") != null ? Integer.parseInt(req.getParameter("guest_id21")) : 0);
      guest_id22 = (req.getParameter("guest_id22") != null ? Integer.parseInt(req.getParameter("guest_id22")) : 0);
      guest_id23 = (req.getParameter("guest_id23") != null ? Integer.parseInt(req.getParameter("guest_id23")) : 0);
      guest_id24 = (req.getParameter("guest_id24") != null ? Integer.parseInt(req.getParameter("guest_id24")) : 0);
      guest_id25 = (req.getParameter("guest_id25") != null ? Integer.parseInt(req.getParameter("guest_id25")) : 0);

      //
      //  Convert hide from string to int
      //
      /*       changed to checkbox
      hide = 0;                       // init to No
      if (hides.equals( "Yes" )) {
         hide = 1;
      }
      * 
      */
        
      //
      //  Get recurrence parm, if specified
      //
      if (req.getParameter("isrecurr") != null) {    // if selected as a recurring request

         isRecurr = true;
      }
      
      
   } else {        // not a letter request
     
      //
      //  if existing lottery request, get the players, etc.
      //
      if (lottid > 0) {
        
         try {

            PreparedStatement pstmt = con.prepareStatement (
               "SELECT player1, player2, player3, player4, player5, player6, player7, player8, " +
               "player9, player10, player11, player12, player13, player14, player15, player16, player17, " +
               "player18, player19, player20, player21, player22, player23, player24, player25, " +
               "p1cw, p2cw, p3cw, p4cw, p5cw, p6cw, p7cw, p8cw, " +
               "p9cw, p10cw, p11cw, p12cw, p13cw, p14cw, p15cw, p16cw, p17cw, " +
               "p18cw, p19cw, p20cw, p21cw, p22cw, p23cw, p24cw, p25cw, " +
               "notes, hideNotes, " +
               "p91, p92, p93, p94, p95, p96, p97, p98, " +
               "p99, p910, p911, p912, p913, p914, p915, p916, p917, " +
               "p918, p919, p920, p921, p922, p923, p924, p925, " +
               "guest_id1, guest_id2, guest_id3, guest_id4, guest_id5, " +
               "guest_id6, guest_id7, guest_id8, guest_id9, guest_id10, " +
               "guest_id11, guest_id12, guest_id13, guest_id14, guest_id15, " +
               "guest_id16, guest_id17, guest_id18, guest_id19, guest_id20, " +
               "guest_id21, guest_id22, guest_id23, guest_id24, guest_id25 " +
               "FROM lreqs3 WHERE id = ?");

            pstmt.clearParameters();        // clear the parms
            pstmt.setLong(1, lottid);         // put the parm in pstmt
            rs = pstmt.executeQuery();      // execute the prepared stmt

            while (rs.next()) {

               player1 = rs.getString(1);
               player2 = rs.getString(2);
               player3 = rs.getString(3);
               player4 = rs.getString(4);
               player5 = rs.getString(5);
               player6 = rs.getString(6);
               player7 = rs.getString(7);
               player8 = rs.getString(8);
               player9 = rs.getString(9);
               player10 = rs.getString(10);
               player11 = rs.getString(11);
               player12 = rs.getString(12);
               player13 = rs.getString(13);
               player14 = rs.getString(14);
               player15 = rs.getString(15);
               player16 = rs.getString(16);
               player17 = rs.getString(17);
               player18 = rs.getString(18);
               player19 = rs.getString(19);
               player20 = rs.getString(20);
               player21 = rs.getString(21);
               player22 = rs.getString(22);
               player23 = rs.getString(23);
               player24 = rs.getString(24);
               player25 = rs.getString(25);
               p1cw = rs.getString(26);
               p2cw = rs.getString(27);
               p3cw = rs.getString(28);
               p4cw = rs.getString(29);
               p5cw = rs.getString(30);
               p6cw = rs.getString(31);
               p7cw = rs.getString(32);
               p8cw = rs.getString(33);
               p9cw = rs.getString(34);
               p10cw = rs.getString(35);
               p11cw = rs.getString(36);
               p12cw = rs.getString(37);
               p13cw = rs.getString(38);
               p14cw = rs.getString(39);
               p15cw = rs.getString(40);
               p16cw = rs.getString(41);
               p17cw = rs.getString(42);
               p18cw = rs.getString(43);
               p19cw = rs.getString(44);
               p20cw = rs.getString(45);
               p21cw = rs.getString(46);
               p22cw = rs.getString(47);
               p23cw = rs.getString(48);
               p24cw = rs.getString(49);
               p25cw = rs.getString(50);
               notes = rs.getString(51);
               hide = rs.getInt(52);
               p91 = rs.getInt(53);
               p92 = rs.getInt(54);
               p93 = rs.getInt(55);
               p94 = rs.getInt(56);
               p95 = rs.getInt(57);
               p96 = rs.getInt(58);
               p97 = rs.getInt(59);
               p98 = rs.getInt(60);
               p99 = rs.getInt(61);
               p910 = rs.getInt(62);
               p911 = rs.getInt(63);
               p912 = rs.getInt(64);
               p913 = rs.getInt(65);
               p914 = rs.getInt(66);
               p915 = rs.getInt(67);
               p916 = rs.getInt(68);
               p917 = rs.getInt(69);
               p918 = rs.getInt(70);
               p919 = rs.getInt(71);
               p920 = rs.getInt(72);
               p921 = rs.getInt(73);
               p922 = rs.getInt(74);
               p923 = rs.getInt(75);
               p924 = rs.getInt(76);
               p925 = rs.getInt(77);
               guest_id1 = rs.getInt("guest_id1");
               guest_id2 = rs.getInt("guest_id2");
               guest_id3 = rs.getInt("guest_id3");
               guest_id4 = rs.getInt("guest_id4");
               guest_id5 = rs.getInt("guest_id5");
               guest_id6 = rs.getInt("guest_id6");
               guest_id7 = rs.getInt("guest_id7");
               guest_id8 = rs.getInt("guest_id8");
               guest_id9 = rs.getInt("guest_id9");
               guest_id10 = rs.getInt("guest_id10");
               guest_id11 = rs.getInt("guest_id11");
               guest_id12 = rs.getInt("guest_id12");
               guest_id13 = rs.getInt("guest_id13");
               guest_id14 = rs.getInt("guest_id14");
               guest_id15 = rs.getInt("guest_id15");
               guest_id16 = rs.getInt("guest_id16");
               guest_id17 = rs.getInt("guest_id17");
               guest_id18 = rs.getInt("guest_id18");
               guest_id19 = rs.getInt("guest_id19");
               guest_id20 = rs.getInt("guest_id20");
               guest_id21 = rs.getInt("guest_id21");
               guest_id22 = rs.getInt("guest_id22");
               guest_id23 = rs.getInt("guest_id23");
               guest_id24 = rs.getInt("guest_id24");
               guest_id25 = rs.getInt("guest_id25");
            }
            pstmt.close();
         }
         catch (Exception e1) {
            dbError(out, e1);
            return;
         }
      }
   }              // end of 'letter' if
  
   //
   //  Get the walk/cart options available
   //
   try {

      getParms.getTmodes(con, parmc, course);
   }
   catch (Exception e1) {

      dbError(out, e1);
      return;
   }

   //
   //  Build the HTML page to prompt user for names
   //
   out.println("<HTML>");
   out.println("<HEAD><link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">");
   out.println("<Title>Proshop Lottery Registration Page</Title>");

   //  Add script code to allow modal windows to be used
   out.println("<!-- ******** BEGIN LIKNO WEB MODAL WINDOWS CODE FOR foretees-modal ******** -->" +
           "<script type=\"text/javascript\">var lwmwLinkedBy=\"LiknoWebModalWindows [1]\",lwmwName=\"foretees-modal\",lwmwBN=\"128\";awmAltUrl=\"\";</script>" +
           "<script charset=\"UTF-8\" src=\"/" + rev + "/web%20utilities/foretees-modal.js\" type=\"text/javascript\"></script>" +
           "<!-- ******** END LIKNO WEB MODAL WINDOWS CODE FOR foretees-modal ******** -->");

   out.println("<script type=\"text/javascript\">");
   out.println("<!--");
   out.println("function resizeIFrame(divHeight, iframeName) {");
   out.println("document.getElementById(iframeName).height = divHeight;");
   out.println("}");
   out.println("// -->");
   out.println("</script>");

   //
   //*******************************************************************
   //  User clicked on a letter - submit the form for the letter
   //*******************************************************************
   //
   out.println("<script language='JavaScript'>");            // Erase name script
   out.println("<!--");
   out.println("function subletter(x) {");

//   out.println("alert(x);");
   out.println("document.playerform.letter.value = x;");         // put the letter in the parm
   out.println("playerform.submit();");        // submit the form
   out.println("}");                  // end of script function
   out.println("// -->");
   out.println("</script>");          // End of script

   //
   //*******************************************************************
   //  Erase player name (erase button selected next to player's name)
   //
   //    Remove the player's name and shift any other names up starting at player1
   //*******************************************************************
   //
   out.println("<script language='JavaScript'>");            // Erase name script
   out.println("<!--");

   out.println("function erasename(pos1) {");

   out.println("document.playerform[pos1].value = '';");           // clear the player field

   out.println("var pos2 = pos1.replace('player', 'guest_id');");
   out.println("document.playerform[pos2].value = '0';");

   out.println("}");                  // end of script function
   out.println("// -->");
   out.println("</script>");          // End of script

   //
   //*******************************************************************
   //  Erase text area - (Notes)
   //*******************************************************************
   //
   out.println("<script language='JavaScript'>");            // Erase text area script
   out.println("<!--");
   out.println("function erasetext(pos1) {");
   out.println("document.playerform[pos1].value = '';");           // clear the text field
   out.println("}");                  // end of script function
   out.println("// -->");
   out.println("</script>");          // End of script
/*
   out.println("<script language='JavaScript'>");             // Move Notes into textarea
   out.println("<!--");
   out.println("function movenotes() {");
   out.println("var oldnotes = document.playerform.oldnotes.value;");
   out.println("document.playerform.notes.value = oldnotes;");   // put notes in text area
   out.println("}");                  // end of script function
   out.println("// -->");
   out.println("</script>");          // End of script
*/

   //
   //*******************************************************************
   //  Move a member name into the tee slot
   //*******************************************************************
   //
   out.println("<script language='JavaScript'>");            // Move name script
   out.println("<!--");

   out.println("function movename(namewc) {");

   out.println("del = ':';");                               // deliminator is a colon
   out.println("array = namewc.split(del);");                 // split string into 2 pieces (name, wc)
   out.println("var name = array[0];");
   out.println("var wc = array[1];");
   out.println("skip = 0;");

   out.println("var f = document.forms['playerform'];");

   out.println("var player1 = f.player1.value;");
   out.println("var player2 = f.player2.value;");
   out.println("var player3 = f.player3.value;");
   out.println("var player4 = f.player4.value;");

   if (players > 4) {
      out.println("var player5 = f.player5.value;");
   } else {
      out.println("var player5 = '';");
   }
   if (players > 5) {
      out.println("var player6 = f.player6.value;");
      out.println("var player7 = f.player7.value;");
      out.println("var player8 = f.player8.value;");
   } else {
      out.println("var player6 = '';");
      out.println("var player7 = '';");
      out.println("var player8 = '';");
   }
   if (players > 8) {
      out.println("var player9 = f.player9.value;");
      out.println("var player10 = f.player10.value;");
   } else {
      out.println("var player9 = '';");
      out.println("var player10 = '';");
   }
   if (players > 10) {
      out.println("var player11 = f.player11.value;");
      out.println("var player12 = f.player12.value;");
   } else {
      out.println("var player11 = '';");
      out.println("var player12 = '';");
   }
   if (players > 12) {
      out.println("var player13 = f.player13.value;");
      out.println("var player14 = f.player14.value;");
      out.println("var player15 = f.player15.value;");
   } else {
      out.println("var player13 = '';");
      out.println("var player14 = '';");
      out.println("var player15 = '';");
   }
   if (players > 15) {
      out.println("var player16 = f.player16.value;");
   } else {
      out.println("var player16 = '';");
   }
   if (players > 16) {
      out.println("var player17 = f.player17.value;");
      out.println("var player18 = f.player18.value;");
      out.println("var player19 = f.player19.value;");
      out.println("var player20 = f.player20.value;");
   } else {
      out.println("var player17 = '';");
      out.println("var player18 = '';");
      out.println("var player19 = '';");
      out.println("var player20 = '';");
   }
   if (players > 20) {
      out.println("var player21 = f.player21.value;");
      out.println("var player22 = f.player22.value;");
      out.println("var player23 = f.player23.value;");
      out.println("var player24 = f.player24.value;");
      out.println("var player25 = f.player25.value;");
   } else {
      out.println("var player21 = '';");
      out.println("var player22 = '';");
      out.println("var player23 = '';");
      out.println("var player24 = '';");
      out.println("var player25 = '';");
   }

   out.println("if (( name == player1) || ( name == player2) || ( name == player3) || ( name == player4) || ");
   out.println("    ( name == player5) || ( name == player6) || ( name == player7) || ( name == player8) || ");
   out.println("    ( name == player9) || ( name == player10) || ( name == player11) || ( name == player12) || ");
   out.println("    ( name == player13) || ( name == player14) || ( name == player15) || ( name == player16) || ");
   out.println("    ( name == player17) || ( name == player18) || ( name == player19) || ( name == player20) || ");
   out.println("    ( name == player21) || ( name == player22) || ( name == player23) || ( name == player24) || ");
   out.println("    ( name == player25)) {");
      out.println("skip = 1;");
   out.println("}");

   out.println("if (skip == 0) {");

     out.println("if (player1 == '') {");                    // if player1 is empty
        out.println("f.player1.value = name;");
        out.println("f.guest_id1.value = '0';");
        out.println("f.p1cw.value = wc;");
     out.println("} else {");

     out.println("if (player2 == '') {");                    // if player2 is empty
        out.println("f.player2.value = name;");
        out.println("f.guest_id2.value = '0';");
        out.println("f.p2cw.value = wc;");
     out.println("} else {");

     out.println("if (player3 == '') {");                    // if player3 is empty
        out.println("f.player3.value = name;");
        out.println("f.guest_id3.value = '0';");
        out.println("f.p3cw.value = wc;");
     out.println("} else {");

     out.println("if (player4 == '') {");                    // if player4 is empty
        out.println("f.player4.value = name;");
        out.println("f.guest_id4.value = '0';");
        out.println("f.p4cw.value = wc;");

   if (players > 4) {
     out.println("} else {");
     out.println("if (player5 == '') {");                    // if player5 is empty
        out.println("f.player5.value = name;");
        out.println("f.guest_id5.value = '0';");
        out.println("f.p5cw.value = wc;");
   }
   if (players > 5) {
     out.println("} else {");
     out.println("if (player6 == '') {");                    // if player6 is empty
        out.println("f.player6.value = name;");
        out.println("f.guest_id6.value = '0';");
        out.println("f.p6cw.value = wc;");
     out.println("} else {");
     out.println("if (player7 == '') {");                    // if player7 is empty
        out.println("f.player7.value = name;");
        out.println("f.guest_id7.value = '0';");
        out.println("f.p7cw.value = wc;");
     out.println("} else {");
     out.println("if (player8 == '') {");                    // if player8 is empty
        out.println("f.player8.value = name;");
        out.println("f.guest_id8.value = '0';");
        out.println("f.p8cw.value = wc;");
   }
   if (players > 8) {
     out.println("} else {");
     out.println("if (player9 == '') {");                    // if player9 is empty
        out.println("f.player9.value = name;");
        out.println("f.guest_id9.value = '0';");
        out.println("f.p9cw.value = wc;");
     out.println("} else {");
     out.println("if (player10 == '') {");                    // if player10 is empty
        out.println("f.player10.value = name;");
        out.println("f.guest_id10.value = '0';");
        out.println("f.p10cw.value = wc;");
   }
   if (players > 10) {
     out.println("} else {");
     out.println("if (player11 == '') {");                    // if player11 is empty
        out.println("f.player11.value = name;");
        out.println("f.guest_id11.value = '0';");
        out.println("f.p11cw.value = wc;");
     out.println("} else {");
     out.println("if (player12 == '') {");                    // if player12 is empty
        out.println("f.player12.value = name;");
        out.println("f.guest_id12.value = '0';");
        out.println("f.p12cw.value = wc;");
   }
   if (players > 12) {
     out.println("} else {");
     out.println("if (player13 == '') {");                    // if player13 is empty
        out.println("f.player13.value = name;");
        out.println("f.guest_id13.value = '0';");
        out.println("f.p13cw.value = wc;");
     out.println("} else {");
     out.println("if (player14 == '') {");                    // if player14 is empty
        out.println("f.player14.value = name;");
        out.println("f.guest_id14.value = '0';");
        out.println("f.p14cw.value = wc;");
     out.println("} else {");
     out.println("if (player15 == '') {");                    // if player15 is empty
        out.println("f.player15.value = name;");
        out.println("f.guest_id15.value = '0';");
        out.println("f.p15cw.value = wc;");
   }
   if (players > 15) {
     out.println("} else {");
     out.println("if (player16 == '') {");                    // if player16 is empty
        out.println("f.player16.value = name;");
        out.println("f.guest_id16.value = '0';");
        out.println("f.p16cw.value = wc;");
   }
   if (players > 16) {
     out.println("} else {");
     out.println("if (player17 == '') {");                    // if player17 is empty
        out.println("f.player17.value = name;");
        out.println("f.guest_id17.value = '0';");
        out.println("f.p17cw.value = wc;");
     out.println("} else {");
     out.println("if (player18 == '') {");                    // if player18 is empty
        out.println("f.player18.value = name;");
        out.println("f.guest_id18.value = '0';");
        out.println("f.p18cw.value = wc;");
     out.println("} else {");
     out.println("if (player19 == '') {");                    // if player19 is empty
        out.println("f.player19.value = name;");
        out.println("f.guest_id19.value = '0';");
        out.println("f.p19cw.value = wc;");
     out.println("} else {");
     out.println("if (player20 == '') {");                    // if player20 is empty
        out.println("f.player20.value = name;");
        out.println("f.guest_id20.value = '0';");
        out.println("f.p20cw.value = wc;");
   }
   if (players > 20) {
     out.println("} else {");
     out.println("if (player21 == '') {");                    // if player21 is empty
        out.println("f.player21.value = name;");
        out.println("f.guest_id21.value = '0';");
        out.println("f.p21cw.value = wc;");
     out.println("} else {");
     out.println("if (player22 == '') {");                    // if player22 is empty
        out.println("f.player22.value = name;");
        out.println("f.guest_id22.value = '0';");
        out.println("f.p22cw.value = wc;");
     out.println("} else {");
     out.println("if (player23 == '') {");                    // if player23 is empty
        out.println("f.player23.value = name;");
        out.println("f.guest_id23.value = '0';");
        out.println("f.p23cw.value = wc;");
     out.println("} else {");
     out.println("if (player24 == '') {");                    // if player24 is empty
        out.println("f.player24.value = name;");
        out.println("f.guest_id24.value = '0';");
        out.println("f.p24cw.value = wc;");
     out.println("} else {");
     out.println("if (player25 == '') {");                    // if player25 is empty
        out.println("f.player25.value = name;");
        out.println("f.guest_id25.value = '0';");
        out.println("f.p25cw.value = wc;");

      out.println("}");         // p25
      out.println("}");         // p24
      out.println("}");         // p23
      out.println("}");         // p22
      out.println("}");         // p21
      out.println("}");         // p20
      out.println("}");         // p19
      out.println("}");         // p18
      out.println("}");         // p17
      out.println("}");         // p16
      out.println("}");         // p15
      out.println("}");         // p14
      out.println("}");         // p13
      out.println("}");         // p12
      out.println("}");         // p11
      out.println("}");         // p10
      out.println("}");         // p9
      out.println("}");         // p8
      out.println("}");         // p7
      out.println("}");         // p6
      out.println("}");         // p5
    } else {
       if (players > 16) {
         out.println("}");         // p20
         out.println("}");         // p19
         out.println("}");         // p18
         out.println("}");         // p17
         out.println("}");         // p16
         out.println("}");         // p15
         out.println("}");         // p14
         out.println("}");         // p13
         out.println("}");         // p12
         out.println("}");         // p11
         out.println("}");         // p10
         out.println("}");         // p9
         out.println("}");         // p8
         out.println("}");         // p7
         out.println("}");         // p6
         out.println("}");         // p5
       } else {
          if (players > 15) {
            out.println("}");         // p16
            out.println("}");         // p15
            out.println("}");         // p14
            out.println("}");         // p13
            out.println("}");         // p12
            out.println("}");         // p11
            out.println("}");         // p10
            out.println("}");         // p9
            out.println("}");         // p8
            out.println("}");         // p7
            out.println("}");         // p6
            out.println("}");         // p5
          } else {
             if (players > 12) {
               out.println("}");         // p15
               out.println("}");         // p14
               out.println("}");         // p13
               out.println("}");         // p12
               out.println("}");         // p11
               out.println("}");         // p10
               out.println("}");         // p9
               out.println("}");         // p8
               out.println("}");         // p7
               out.println("}");         // p6
               out.println("}");         // p5
             } else {
                if (players > 10) {
                  out.println("}");         // p12
                  out.println("}");         // p11
                  out.println("}");         // p10
                  out.println("}");         // p9
                  out.println("}");         // p8
                  out.println("}");         // p7
                  out.println("}");         // p6
                  out.println("}");         // p5
                } else {
                   if (players > 8) {
                     out.println("}");         // p10
                     out.println("}");         // p9
                     out.println("}");         // p8
                     out.println("}");         // p7
                     out.println("}");         // p6
                     out.println("}");         // p5
                   } else {
                      if (players > 5) {
                        out.println("}");         // p8
                        out.println("}");         // p7
                        out.println("}");         // p6
                        out.println("}");         // p5
                      } else {
                         if (players > 4) {
                           out.println("}");         // p5
                         }
                      }
                   }
                }
             }
          }
       }
    }
    out.println("}");         // p4
    out.println("}");         // p3
    out.println("}");         // p2
    out.println("}");         // p1

   out.println("}");                  // end of dup name check (if skip = 0)

   out.println("}");                  // end of script function
   out.println("// -->");
   out.println("</script>");                               // End of script


   //
   //*******************************************************************
   //  Move a Guest Name into the tee slot
   //*******************************************************************
   //
   out.println("<script language='JavaScript'>");            // Move Guest Name script
   out.println("<!--");

   out.println("var guestid_slot;");
   out.println("var player_slot;");

   out.println("function moveguest(namewc) {");

   //out.println("var name = namewc;");

   out.println("array = namewc.split('|');"); // split string (partner_name, partner_id)
   out.println("var name = array[0];");

   //if (enableAdvAssist) {
       out.println("var use_guestdb = array[1];");
   //} else {
   //    out.println("var use_guestdb = 0; // force to off on iPad");
   //}

   out.println("var f = document.forms['playerform'];");

   out.println("var player1 = f.player1.value;");
   out.println("var player2 = f.player2.value;");
   out.println("var player3 = f.player3.value;");
   out.println("var player4 = f.player4.value;");

   if (players > 4) {
      out.println("var player5 = f.player5.value;");
   } else {
      out.println("var player5 = '';");
   }
   if (players > 5) {
      out.println("var player6 = f.player6.value;");
      out.println("var player7 = f.player7.value;");
      out.println("var player8 = f.player8.value;");
   } else {
      out.println("var player6 = '';");
      out.println("var player7 = '';");
      out.println("var player8 = '';");
   }
   if (players > 8) {
      out.println("var player9 = f.player9.value;");
      out.println("var player10 = f.player10.value;");
   } else {
      out.println("var player9 = '';");
      out.println("var player10 = '';");
   }
   if (players > 10) {
      out.println("var player11 = f.player11.value;");
      out.println("var player12 = f.player12.value;");
   } else {
      out.println("var player11 = '';");
      out.println("var player12 = '';");
   }
   if (players > 12) {
      out.println("var player13 = f.player13.value;");
      out.println("var player14 = f.player14.value;");
      out.println("var player15 = f.player15.value;");
   } else {
      out.println("var player13 = '';");
      out.println("var player14 = '';");
      out.println("var player15 = '';");
   }
   if (players > 15) {
      out.println("var player16 = f.player16.value;");
   } else {
      out.println("var player16 = '';");
   }
   if (players > 16) {
      out.println("var player17 = f.player17.value;");
      out.println("var player18 = f.player18.value;");
      out.println("var player19 = f.player19.value;");
      out.println("var player20 = f.player20.value;");
   } else {
      out.println("var player17 = '';");
      out.println("var player18 = '';");
      out.println("var player19 = '';");
      out.println("var player20 = '';");
   }
   if (players > 20) {
      out.println("var player21 = f.player21.value;");
      out.println("var player22 = f.player22.value;");
      out.println("var player23 = f.player23.value;");
      out.println("var player24 = f.player24.value;");
      out.println("var player25 = f.player25.value;");
   } else {
      out.println("var player21 = '';");
      out.println("var player22 = '';");
      out.println("var player23 = '';");
      out.println("var player24 = '';");
      out.println("var player25 = '';");
   }

   out.println("var defCW = '';");
   if (club.equals( "ranchobernardo" )) {
      out.println("defCW = 'CCH';");       // set default Mode of Trans
   } else if (club.equals( "blackdiamondranch" )) {
      out.println("defCW = 'CCT';");       // set default Mode of Trans - Club Cart
   } else if (club.equals("braeburncc")) {
      out.println("defCW = 'NAP';");
   } else if (club.equals("dataw")) {

       out.println("if (name == 'Blank' || name == 'Comp' || name == 'Experience Dataw') {");
       out.println("  defCW = 'TF';");
       out.println("} else if (name == 'Guest of Member' || name == 'Reciprocal' || name == 'Unaccompanied') {");
       out.println("  defCW = 'CR';");
       out.println("}");
   }

  // If guest tracking is turned on and in use for this guest type and at least one player slot is open, display the modal window
  out.println("if (use_guestdb == 1 && (player1 == '' || player2 == '' || player3 == '' || player4 == ''" +
          (players > 4 ? " || player5 == ''" : "") +
          (players > 5 ? " || player6 == '' || player7 == '' || player8 == ''" : "") +
          (players > 8 ? " || player9 == '' || player10 == ''" : "") +
          (players > 10 ? " || player11 == '' || player12 == ''" : "") +
          (players > 12 ? " || player13 == '' || player14 == '' || player15 == ''" : "") +
          (players > 15 ? " || player16 == ''" : "") +
          (players > 16 ? " || player17 == '' || player18 == '' || player19 == '' || player20 == ''" : "") +
          (players > 20 ? " || player21 == '' || player22 == '' || player23 == '' || player24 == '' || player25 == ''" : "") +
          ")) {");
  out.println("  loadmodal(0);");
  out.println("}");

   //  set spc to ' ' if name to move isn't an 'X'
   out.println("var spc = '';");
   out.println("if (name != 'X' && name != 'x') {");
   out.println("   spc = ' ';");
   out.println("}");

  out.println("if (player1 == '') {");                    // if player1 is empty
     out.println("if (use_guestdb == 1) {");
        out.println("player_slot = f.player1;");
        out.println("guestid_slot = f.guest_id1;");
        out.println("f.player1.value = name + spc;");
     out.println("} else {");
        out.println("f.player1.focus();"); // here for IE compat
        out.println("f.player1.value = name + spc;");
        out.println("f.player1.focus();");
     out.println("}");
     out.println("if (defCW != '') {");
        out.println("f.p1cw.value = defCW;");
     out.println("}");
  out.println("} else {");

  out.println("if (player2 == '') {");                    // if player2 is empty
     out.println("if (use_guestdb == 1) {");
        out.println("player_slot = f.player2;");
        out.println("guestid_slot = f.guest_id2;");
        out.println("f.player2.value = name + spc;");
     out.println("} else {");
        out.println("f.player2.focus();"); // here for IE compat
        out.println("f.player2.value = name + spc;");
        out.println("f.player2.focus();");
     out.println("}");
     out.println("if (defCW != '') {");
        out.println("f.p2cw.value = defCW;");
     out.println("}");
  out.println("} else {");

  out.println("if (player3 == '') {");                    // if player3 is empty
     out.println("if (use_guestdb == 1) {");
        out.println("player_slot = f.player3;");
        out.println("guestid_slot = f.guest_id3;");
        out.println("f.player3.value = name + spc;");
     out.println("} else {");
        out.println("f.player3.focus();"); // here for IE compat
        out.println("f.player3.value = name + spc;");
        out.println("f.player3.focus();");
     out.println("}");
     out.println("if (defCW != '') {");
        out.println("f.p3cw.value = defCW;");
     out.println("}");
  out.println("} else {");

  out.println("if (player4 == '') {");                    // if player4 is empty
     out.println("if (use_guestdb == 1) {");
        out.println("player_slot = f.player4;");
        out.println("guestid_slot = f.guest_id4;");
        out.println("f.player4.value = name + spc;");
     out.println("} else {");
        out.println("f.player4.focus();"); // here for IE compat
        out.println("f.player4.value = name + spc;");
        out.println("f.player4.focus();");
     out.println("}");
     out.println("if (defCW != '') {");
        out.println("f.p4cw.value = defCW;");
     out.println("}");

  if (players > 4) {
    out.println("} else {");
    out.println("if (player5 == '') {");                    // if player5 is empty
       out.println("if (use_guestdb == 1) {");
          out.println("player_slot = f.player5;");
          out.println("guestid_slot = f.guest_id5;");
          out.println("f.player5.value = name + spc;");
       out.println("} else {");
          out.println("f.player5.focus();"); // here for IE compat
          out.println("f.player5.value = name + spc;");
          out.println("f.player5.focus();");
       out.println("}");
       out.println("if (defCW != '') {");
          out.println("f.p5cw.value = defCW;");
       out.println("}");
  }
  if (players > 5) {
    out.println("} else {");
    out.println("if (player6 == '') {");                    // if player6 is empty
       out.println("if (use_guestdb == 1) {");
          out.println("player_slot = f.player6;");
          out.println("guestid_slot = f.guest_id6;");
          out.println("f.player6.value = name + spc;");
       out.println("} else {");
          out.println("f.player6.focus();"); // here for IE compat
          out.println("f.player6.value = name + spc;");
          out.println("f.player6.focus();");
       out.println("}");
       out.println("if (defCW != '') {");
          out.println("f.p6cw.value = defCW;");
       out.println("}");
    out.println("} else {");
    out.println("if (player7 == '') {");                    // if player7 is empty
       out.println("if (use_guestdb == 1) {");
          out.println("player_slot = f.player7;");
          out.println("guestid_slot = f.guest_id7;");
          out.println("f.player7.value = name + spc;");
       out.println("} else {");
          out.println("f.player7.focus();"); // here for IE compat
          out.println("f.player7.value = name + spc;");
          out.println("f.player7.focus();");
       out.println("}");
       out.println("if (defCW != '') {");
          out.println("f.p7cw.value = defCW;");
       out.println("}");
    out.println("} else {");
    out.println("if (player8 == '') {");                    // if player8 is empty
       out.println("if (use_guestdb == 1) {");
          out.println("player_slot = f.player8;");
          out.println("guestid_slot = f.guest_id8;");
          out.println("f.player8.value = name + spc;");
       out.println("} else {");
          out.println("f.player8.focus();"); // here for IE compat
          out.println("f.player8.value = name + spc;");
          out.println("f.player8.focus();");
       out.println("}");
       out.println("if (defCW != '') {");
          out.println("f.p8cw.value = defCW;");
       out.println("}");
  }
  if (players > 8) {
    out.println("} else {");
    out.println("if (player9 == '') {");                    // if player9 is empty
       out.println("if (use_guestdb == 1) {");
          out.println("player_slot = f.player9;");
          out.println("guestid_slot = f.guest_id9;");
          out.println("f.player9.value = name + spc;");
       out.println("} else {");
          out.println("f.player9.focus();"); // here for IE compat
          out.println("f.player9.value = name + spc;");
          out.println("f.player9.focus();");
       out.println("}");
       out.println("if (defCW != '') {");
          out.println("f.p9cw.value = defCW;");
       out.println("}");
    out.println("} else {");
    out.println("if (player10 == '') {");                    // if player10 is empty
       out.println("if (use_guestdb == 1) {");
          out.println("player_slot = f.player10;");
          out.println("guestid_slot = f.guest_id10;");
          out.println("f.player10.value = name + spc;");
       out.println("} else {");
          out.println("f.player10.focus();"); // here for IE compat
          out.println("f.player10.value = name + spc;");
          out.println("f.player10.focus();");
       out.println("}");
       out.println("if (defCW != '') {");
          out.println("f.p10cw.value = defCW;");
       out.println("}");
  }
  if (players > 10) {
    out.println("} else {");
    out.println("if (player11 == '') {");                    // if player11 is empty
       out.println("if (use_guestdb == 1) {");
          out.println("player_slot = f.player11;");
          out.println("guestid_slot = f.guest_id11;");
          out.println("f.player11.value = name + spc;");
       out.println("} else {");
          out.println("f.player11.focus();"); // here for IE compat
          out.println("f.player11.value = name + spc;");
          out.println("f.player11.focus();");
       out.println("}");
       out.println("if (defCW != '') {");
          out.println("f.p11cw.value = defCW;");
       out.println("}");
    out.println("} else {");
    out.println("if (player12 == '') {");                    // if player12 is empty
       out.println("if (use_guestdb == 1) {");
          out.println("player_slot = f.player12;");
          out.println("guestid_slot = f.guest_id12;");
          out.println("f.player12.value = name + spc;");
       out.println("} else {");
          out.println("f.player12.focus();"); // here for IE compat
          out.println("f.player12.value = name + spc;");
          out.println("f.player12.focus();");
       out.println("}");
       out.println("if (defCW != '') {");
          out.println("f.p12cw.value = defCW;");
       out.println("}");
  }
  if (players > 12) {
    out.println("} else {");
    out.println("if (player13 == '') {");                    // if player13 is empty
       out.println("if (use_guestdb == 1) {");
          out.println("player_slot = f.player13;");
          out.println("guestid_slot = f.guest_id13;");
          out.println("f.player13.value = name + spc;");
       out.println("} else {");
          out.println("f.player13.focus();"); // here for IE compat
          out.println("f.player13.value = name + spc;");
          out.println("f.player13.focus();");
       out.println("}");
       out.println("if (defCW != '') {");
          out.println("f.p13cw.value = defCW;");
       out.println("}");
    out.println("} else {");
    out.println("if (player14 == '') {");                    // if player14 is empty
       out.println("if (use_guestdb == 1) {");
          out.println("player_slot = f.player14;");
          out.println("guestid_slot = f.guest_id14;");
          out.println("f.player14.value = name + spc;");
       out.println("} else {");
          out.println("f.player14.focus();"); // here for IE compat
          out.println("f.player14.value = name + spc;");
          out.println("f.player14.focus();");
       out.println("}");
       out.println("if (defCW != '') {");
          out.println("f.p14cw.value = defCW;");
       out.println("}");
    out.println("} else {");
    out.println("if (player15 == '') {");                    // if player15 is empty
       out.println("if (use_guestdb == 1) {");
          out.println("player_slot = f.player15;");
          out.println("guestid_slot = f.guest_id15;");
          out.println("f.player15.value = name + spc;");
       out.println("} else {");
          out.println("f.player15.focus();"); // here for IE compat
          out.println("f.player15.value = name + spc;");
          out.println("f.player15.focus();");
       out.println("}");
       out.println("if (defCW != '') {");
          out.println("f.p15cw.value = defCW;");
       out.println("}");
  }
  if (players > 15) {
    out.println("} else {");
    out.println("if (player16 == '') {");                    // if player16 is empty
       out.println("if (use_guestdb == 1) {");
          out.println("player_slot = f.player16;");
          out.println("guestid_slot = f.guest_id16;");
          out.println("f.player16.value = name + spc;");
       out.println("} else {");
          out.println("f.player16.focus();"); // here for IE compat
          out.println("f.player16.value = name + spc;");
          out.println("f.player16.focus();");
       out.println("}");
       out.println("if (defCW != '') {");
          out.println("f.p16cw.value = defCW;");
       out.println("}");
  }
  if (players > 16) {
    out.println("} else {");
    out.println("if (player17 == '') {");                    // if player17 is empty
       out.println("if (use_guestdb == 1) {");
          out.println("player_slot = f.player17;");
          out.println("guestid_slot = f.guest_id17;");
          out.println("f.player17.value = name + spc;");
       out.println("} else {");
          out.println("f.player17.focus();"); // here for IE compat
          out.println("f.player17.value = name + spc;");
          out.println("f.player17.focus();");
       out.println("}");
       out.println("if (defCW != '') {");
          out.println("f.p17cw.value = defCW;");
       out.println("}");
    out.println("} else {");
    out.println("if (player18 == '') {");                    // if player18 is empty
       out.println("if (use_guestdb == 1) {");
          out.println("player_slot = f.player18;");
          out.println("guestid_slot = f.guest_id18;");
          out.println("f.player18.value = name + spc;");
       out.println("} else {");
          out.println("f.player18.focus();"); // here for IE compat
          out.println("f.player18.value = name + spc;");
          out.println("f.player18.focus();");
       out.println("}");
       out.println("if (defCW != '') {");
          out.println("f.p18cw.value = defCW;");
       out.println("}");
    out.println("} else {");
    out.println("if (player19 == '') {");                    // if player19 is empty
       out.println("if (use_guestdb == 1) {");
          out.println("player_slot = f.player19;");
          out.println("guestid_slot = f.guest_id19;");
          out.println("f.player19.value = name + spc;");
       out.println("} else {");
          out.println("f.player19.focus();"); // here for IE compat
          out.println("f.player19.value = name + spc;");
          out.println("f.player19.focus();");
       out.println("}");
       out.println("if (defCW != '') {");
          out.println("f.p19cw.value = defCW;");
       out.println("}");
    out.println("} else {");
    out.println("if (player20 == '') {");                    // if player20 is empty
       out.println("if (use_guestdb == 1) {");
          out.println("player_slot = f.player20;");
          out.println("guestid_slot = f.guest_id20;");
          out.println("f.player20.value = name + spc;");
       out.println("} else {");
          out.println("f.player20.focus();"); // here for IE compat
          out.println("f.player20.value = name + spc;");
          out.println("f.player20.focus();");
       out.println("}");
       out.println("if (defCW != '') {");
          out.println("f.p20cw.value = defCW;");
       out.println("}");
  }
  if (players > 20) {
    out.println("} else {");
    out.println("if (player21 == '') {");                    // if player21 is empty
       out.println("if (use_guestdb == 1) {");
          out.println("player_slot = f.player21;");
          out.println("guestid_slot = f.guest_id21;");
          out.println("f.player21.value = name + spc;");
       out.println("} else {");
          out.println("f.player21.focus();"); // here for IE compat
          out.println("f.player21.value = name + spc;");
          out.println("f.player21.focus();");
       out.println("}");
       out.println("if (defCW != '') {");
          out.println("f.p21cw.value = defCW;");
       out.println("}");
    out.println("} else {");
    out.println("if (player22 == '') {");                    // if player22 is empty
       out.println("if (use_guestdb == 1) {");
          out.println("player_slot = f.player22;");
          out.println("guestid_slot = f.guest_id22;");
          out.println("f.player22.value = name + spc;");
       out.println("} else {");
          out.println("f.player22.focus();"); // here for IE compat
          out.println("f.player22.value = name + spc;");
          out.println("f.player22.focus();");
       out.println("}");
       out.println("if (defCW != '') {");
          out.println("f.p22cw.value = defCW;");
       out.println("}");
    out.println("} else {");
    out.println("if (player23 == '') {");                    // if player23 is empty
       out.println("if (use_guestdb == 1) {");
          out.println("player_slot = f.player23;");
          out.println("guestid_slot = f.guest_id23;");
          out.println("f.player23.value = name + spc;");
       out.println("} else {");
          out.println("f.player23.focus();"); // here for IE compat
          out.println("f.player23.value = name + spc;");
          out.println("f.player23.focus();");
       out.println("}");
       out.println("if (defCW != '') {");
          out.println("f.p23cw.value = defCW;");
       out.println("}");
    out.println("} else {");
    out.println("if (player24 == '') {");                    // if player24 is empty
       out.println("if (use_guestdb == 1) {");
          out.println("player_slot = f.player24;");
          out.println("guestid_slot = f.guest_id24;");
          out.println("f.player24.value = name + spc;");
       out.println("} else {");
          out.println("f.player24.focus();"); // here for IE compat
          out.println("f.player24.value = name + spc;");
          out.println("f.player24.focus();");
       out.println("}");
       out.println("if (defCW != '') {");
          out.println("f.p24cw.value = defCW;");
       out.println("}");
    out.println("} else {");
    out.println("if (player25 == '') {");                    // if player25 is empty
       out.println("if (use_guestdb == 1) {");
          out.println("player_slot = f.player25;");
          out.println("guestid_slot = f.guest_id25;");
          out.println("f.player25.value = name + spc;");
       out.println("} else {");
          out.println("f.player25.focus();"); // here for IE compat
          out.println("f.player25.value = name + spc;");
          out.println("f.player25.focus();");
       out.println("}");
       out.println("if (defCW != '') {");
          out.println("f.p25cw.value = defCW;");
       out.println("}");

     out.println("}");         // p25
     out.println("}");         // p24
     out.println("}");         // p23
     out.println("}");         // p22
     out.println("}");         // p21
     out.println("}");         // p20
     out.println("}");         // p19
     out.println("}");         // p18
     out.println("}");         // p17
     out.println("}");         // p16
     out.println("}");         // p15
     out.println("}");         // p14
     out.println("}");         // p13
     out.println("}");         // p12
     out.println("}");         // p11
     out.println("}");         // p10
     out.println("}");         // p9
     out.println("}");         // p8
     out.println("}");         // p7
     out.println("}");         // p6
     out.println("}");         // p5
   } else {
      if (players > 16) {
        out.println("}");         // p20
        out.println("}");         // p19
        out.println("}");         // p18
        out.println("}");         // p17
        out.println("}");         // p16
        out.println("}");         // p15
        out.println("}");         // p14
        out.println("}");         // p13
        out.println("}");         // p12
        out.println("}");         // p11
        out.println("}");         // p10
        out.println("}");         // p9
        out.println("}");         // p8
        out.println("}");         // p7
        out.println("}");         // p6
        out.println("}");         // p5
      } else {
         if (players > 15) {
           out.println("}");         // p16
           out.println("}");         // p15
           out.println("}");         // p14
           out.println("}");         // p13
           out.println("}");         // p12
           out.println("}");         // p11
           out.println("}");         // p10
           out.println("}");         // p9
           out.println("}");         // p8
           out.println("}");         // p7
           out.println("}");         // p6
           out.println("}");         // p5
         } else {
            if (players > 12) {
              out.println("}");         // p15
              out.println("}");         // p14
              out.println("}");         // p13
              out.println("}");         // p12
              out.println("}");         // p11
              out.println("}");         // p10
              out.println("}");         // p9
              out.println("}");         // p8
              out.println("}");         // p7
              out.println("}");         // p6
              out.println("}");         // p5
            } else {
               if (players > 10) {
                 out.println("}");         // p12
                 out.println("}");         // p11
                 out.println("}");         // p10
                 out.println("}");         // p9
                 out.println("}");         // p8
                 out.println("}");         // p7
                 out.println("}");         // p6
                 out.println("}");         // p5
               } else {
                  if (players > 8) {
                    out.println("}");         // p10
                    out.println("}");         // p9
                    out.println("}");         // p8
                    out.println("}");         // p7
                    out.println("}");         // p6
                    out.println("}");         // p5
                  } else {
                     if (players > 5) {
                       out.println("}");         // p8
                       out.println("}");         // p7
                       out.println("}");         // p6
                       out.println("}");         // p5
                     } else {
                        if (players > 4) {
                          out.println("}");         // p5

                        }
                     }
                  }
               }
            }
         }
      }
   }
   out.println("}");         // p4
   out.println("}");         // p3
   out.println("}");         // p2
   out.println("}");         // p1

   out.println("}");                  // end of script function
   out.println("// -->");
   out.println("</script>");                               // End of script

   out.println("</HEAD>");

   // ********* end of scripts **********

   //out.println("<body onLoad=\"movenotes()\" bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\" topmargin=\"0\">");
   out.println("<body onLoad=\"movenotes()\" bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#000000\" vlink=\"#000000\" alink=\"#FF0000\" topmargin=\"0\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" width=\"100%\" align=\"center\" valign=\"top\">");  // large table for whole page
   out.println("<tr><td valign=\"top\" align=\"center\">");

   out.println("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#CCCCAA\" align=\"center\" valign=\"top\">");
     out.println("<tr><td align=\"left\" width=\"300\">");
     out.println("&nbsp;&nbsp;&nbsp;<b>ForeTees</b>");
     out.println("</td>");

     out.println("<td align=\"center\">");
     out.println("<font size=\"5\">Golf Shop Lottery Request</font>");
     out.println("</font></td>");

     out.println("<td align=\"center\" width=\"300\">");
     out.println("<font size=\"1\" color=\"#000000\">Copyright&nbsp;</font>");
     out.println("<font size=\"2\" color=\"#000000\">&#169;&nbsp;</font>");
     out.println("<font size=\"1\" color=\"#000000\">ForeTees, LLC <br> " +thisYear+ " All rights reserved.");
     out.println("</font></td>");
   out.println("</tr></table>");

   out.println("<table border=\"0\" align=\"center\">");                           // table for main page
   out.println("<tr><td align=\"center\">");

      out.println("<br>");
      out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"3\">");
         out.println("<tr>");
         out.println("<td width=\"620\" align=\"center\">");
         out.println("<font size=\"3\">");
         out.println("<b>Lottery Registration</b><br></font>");
         out.println("<font size=\"2\">");
         out.println(" Add players to the group(s) and click on 'Submit Request' to enter the request. ");
         out.println("</font></td></tr>");
      out.println("</table>");

      out.println("<table border=\"0\" align=\"center\" valign=\"top\" cellpadding=\"5\" cellspacing=\"5\">"); // table to contain 4 tables below

         out.println("<tr>");
         out.println("<td align=\"center\" valign=\"top\">");         // col for Instructions

            out.println("<br><br><br><br><font size=\"1\">");
            out.println("<a href=\"#\" onClick=\"window.open ('/" +rev+ "/proshop_help_lott_instruct.htm', 'newwindow', config='Height=460, width=500, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
            out.println("<img src=\"/" +rev+ "/images/instructions.gif\" border=0>");
            out.println("<br>Click for Help</a>");

            out.println("</font><font size=\"2\">");
            out.println("<br><br><br>");
              
            if (lottid > 0) {

               out.println("<form action=\"Proshop_lott\" method=\"post\" name=\"can\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
               out.println("<input type=\"hidden\" name=\"index2\" value=\"" + index2 + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
               out.println("<input type=\"hidden\" name=\"time\" value=" + time + ">");
               out.println("<input type=\"hidden\" name=\"fb\" value=" + fb + ">");
               out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + lottid + "\">");

            } else {            // go directly back to caller

               if (index.equals( "888" )) {      // if from Proshop_searchmem via proshop_main

                  out.println("<form method=\"get\" action=\"Proshop_jump\">");
                  out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");

               } else {

                  if (index.equals( "777" )) {      // if from Proshop_mlottery

                     out.println("<form method=\"post\" action=\"Proshop_jump\">");
                     out.println("<input type=\"hidden\" name=\"index2\" value=\"" + index2 + "\">");
                     if (!returnCourse.equals( "" )) {
                        out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
                     } else {
                        out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                     }

                  } else {

                     out.println("<form action=\"Proshop_jump\" method=\"post\">");
                     out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
                     if (!returnCourse.equals( "" )) {
                        out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
                     } else {
                        out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                     }
                  }
               }
            }
            out.println("Return<br>w/o Changes:<br>");
            out.println("<input type=\"submit\" value=\"Go Back\" name=\"cancel\"></form>");

          out.println("</font></td>");

          out.println("<form action=\"Proshop_lott\" method=\"post\" name=\"playerform\">");
          out.println("<td align=\"center\" valign=\"top\">");

            out.println("<font size=\"2\"><br>");
            out.println("Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy + "</b>");
            out.println("&nbsp;&nbsp;Time Requested:&nbsp;&nbsp;<b>" + stime + "</b>");
            if (!course.equals( "" )) {
               out.println("<br>Course:&nbsp;&nbsp;<b>" + course + "</b>");
            }
            out.println("<br><br>");

            out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\" width=\"370\">");  // table for player selection
            out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<b>Add or Remove Players</b>");
            out.println("</font></td></tr>");

            out.println("<tr><td align=\"center\">");
               out.println("<font size=\"2\">");

               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Players");
               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("&nbsp;&nbsp;&nbsp;Trans&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;9-Holes</b><br>");

               out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + guest_id1 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + guest_id2 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + guest_id3 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + guest_id4 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + guest_id5 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id6\" value=\"" + guest_id6 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id7\" value=\"" + guest_id7 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id8\" value=\"" + guest_id8 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id9\" value=\"" + guest_id9 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id10\" value=\"" + guest_id10 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id11\" value=\"" + guest_id11 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id12\" value=\"" + guest_id12 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id13\" value=\"" + guest_id13 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id14\" value=\"" + guest_id14 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id15\" value=\"" + guest_id15 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id16\" value=\"" + guest_id16 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id17\" value=\"" + guest_id17 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id18\" value=\"" + guest_id18 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id19\" value=\"" + guest_id19 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id20\" value=\"" + guest_id20 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id21\" value=\"" + guest_id21 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id22\" value=\"" + guest_id22 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id23\" value=\"" + guest_id23 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id24\" value=\"" + guest_id24 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id25\" value=\"" + guest_id25 + "\">");

               out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player1')\" style=\"cursor:hand\">");
               out.println("1: &nbsp;&nbsp;<input type=\"text\" name=\"player1\" value=\"" + player1 + "\" size=\"20\" maxlength=\"30\" onkeypress=\"return DYN_disableEnterKey(event)\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p1cw\">");
                 if (!p1cw.equals( "" )) {
                    out.println("<option selected value=" + p1cw + ">" + p1cw + "</option>");
                 }
                 for (i=0; i<parm.MAX_Tmodes; i++) {

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p1cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p91 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p91\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p91\" value=\"1\">");
                 }

               out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player2')\" style=\"cursor:hand\">");
               out.println("2: &nbsp;&nbsp;<input type=\"text\" name=\"player2\" value=\"" + player2 + "\" size=\"20\" maxlength=\"30\" onkeypress=\"return DYN_disableEnterKey(event)\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p2cw\">");
                 if (!p2cw.equals( "" )) {
                    out.println("<option selected value=" + p2cw + ">" + p2cw + "</option>");
                 }
                 for (i=0; i<parm.MAX_Tmodes; i++) {

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p2cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p92 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p92\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p92\" value=\"1\">");
                 }

               out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player3')\" style=\"cursor:hand\">");
               out.println("3: &nbsp;&nbsp;<input type=\"text\" name=\"player3\" value=\"" + player3 + "\" size=\"20\" maxlength=\"30\" onkeypress=\"return DYN_disableEnterKey(event)\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p3cw\">");
                 if (!p3cw.equals( "" )) {
                    out.println("<option selected value=" + p3cw + ">" + p3cw + "</option>");
                 }
                 for (i=0; i<parm.MAX_Tmodes; i++) {

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p3cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p93 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p93\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p93\" value=\"1\">");
                 }

               out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player4')\" style=\"cursor:hand\">");
               out.println("4: &nbsp;&nbsp;<input type=\"text\" name=\"player4\" value=\"" + player4 + "\" size=\"20\" maxlength=\"30\" onkeypress=\"return DYN_disableEnterKey(event)\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p4cw\">");
                 if (!p4cw.equals( "" )) {
                    out.println("<option selected value=" + p4cw + ">" + p4cw + "</option>");
                 }
                 for (i=0; i<parm.MAX_Tmodes; i++) {

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p4cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p94 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p94\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p94\" value=\"1\">");
                 }

               if ((p5.equals( "Yes" )) || (players > 4)) {

                  if (p5.equals( "No" )) {   // if 4-somes only

                    out.println("</font></td></tr>");

                    out.println("<tr><td align=\"center\">");     // new row for new group
                    out.println("<font size=\"2\">");
                  }

                  out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player5')\" style=\"cursor:hand\">");
                  out.println("5: &nbsp;&nbsp;<input type=\"text\" id=\"player5\" name=\"player5\" value=\"" + player5 + "\" size=\"20\" maxlength=\"30\" onkeypress=\"return DYN_disableEnterKey(event)\">");
                  out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p5cw\" id=\"p5cw\">");
                  if (!p5cw.equals( "" )) {
                     out.println("<option selected value=" + p5cw + ">" + p5cw + "</option>");
                  }
                  for (i=0; i<parm.MAX_Tmodes; i++) {

                     if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p5cw )) {
                        out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                     }
                  }
                  out.println("</select>");
                 if (p95 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p95\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p95\" value=\"1\">");
                 }
               } else {

                  out.println("<input type=\"hidden\" name=\"player5\" value=\"\">");
                  out.println("<input type=\"hidden\" name=\"p5cw\" value=\"\">");
               }

              if (players > 5) {

                 if (p5.equals( "Yes" )) {

                    out.println("</font></td></tr>");

                    out.println("<tr><td align=\"center\">");     // new row for new group
                    out.println("<font size=\"2\">");
                 }

                 out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player6')\" style=\"cursor:hand\">");
                 out.println("6: &nbsp;&nbsp;<input type=\"text\" name=\"player6\" value=\"" + player6 + "\" size=\"20\" maxlength=\"30\" onkeypress=\"return DYN_disableEnterKey(event)\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p6cw\">");
                 if (!p6cw.equals( "" )) {
                    out.println("<option selected value=" + p6cw + ">" + p6cw + "</option>");
                 }
                 for (i=0; i<parm.MAX_Tmodes; i++) {

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p6cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p96 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p96\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p96\" value=\"1\">");
                 }

                 out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player7')\" style=\"cursor:hand\">");
                 out.println("7: &nbsp;&nbsp;<input type=\"text\" name=\"player7\" value=\"" + player7 + "\" size=\"20\" maxlength=\"30\" onkeypress=\"return DYN_disableEnterKey(event)\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p7cw\">");
                 if (!p7cw.equals( "" )) {
                    out.println("<option selected value=" + p7cw + ">" + p7cw + "</option>");
                 }
                 for (i=0; i<parm.MAX_Tmodes; i++) {

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p7cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p97 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p97\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p97\" value=\"1\">");
                 }

                 out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player8')\" style=\"cursor:hand\">");
                 out.println("8: &nbsp;&nbsp;<input type=\"text\" name=\"player8\" value=\"" + player8 + "\" size=\"20\" maxlength=\"30\" onkeypress=\"return DYN_disableEnterKey(event)\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p8cw\">");
                 if (!p8cw.equals( "" )) {
                    out.println("<option selected value=" + p8cw + ">" + p8cw + "</option>");
                 }
                 for (i=0; i<parm.MAX_Tmodes; i++) {

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p8cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p98 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p98\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p98\" value=\"1\">");
                 }
              }

              if (players > 8) {

                if (p5.equals( "No" )) {   // if 4-somes only

                  out.println("</font></td></tr>");

                  out.println("<tr><td align=\"center\">");     // new row for new group
                  out.println("<font size=\"2\">");
                }

                 out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player9')\" style=\"cursor:hand\">");
                 out.println("9: &nbsp;&nbsp;<input type=\"text\" name=\"player9\" value=\"" + player9 + "\" size=\"20\" maxlength=\"30\" onkeypress=\"return DYN_disableEnterKey(event)\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p9cw\">");
                 if (!p9cw.equals( "" )) {
                    out.println("<option selected value=" + p9cw + ">" + p9cw + "</option>");
                 }
                 for (i=0; i<parm.MAX_Tmodes; i++) {

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p9cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p99 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p99\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p99\" value=\"1\">");
                 }

                 out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player10')\" style=\"cursor:hand\">");
                 out.println("10:&nbsp;&nbsp;<input type=\"text\" id=\"player10\" name=\"player10\" value=\"" + player10 + "\" size=\"20\" maxlength=\"30\" onkeypress=\"return DYN_disableEnterKey(event)\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p10cw\" id=\"p10cw\">");
                 if (!p10cw.equals( "" )) {
                    out.println("<option selected value=" + p10cw + ">" + p10cw + "</option>");
                 }
                 for (i=0; i<parm.MAX_Tmodes; i++) {

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p10cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p910 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p910\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p910\" value=\"1\">");
                 }
              }

              if (players > 10) {

                if (p5.equals( "Yes" )) {   // if 5-somes

                  out.println("</font></td></tr>");

                  out.println("<tr><td align=\"center\">");     // new row for new group
                  out.println("<font size=\"2\">");
                }


                 out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player11')\" style=\"cursor:hand\">");
                 out.println("11:&nbsp;&nbsp;<input type=\"text\" name=\"player11\" value=\"" + player11 + "\" size=\"20\" maxlength=\"30\" onkeypress=\"return DYN_disableEnterKey(event)\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p11cw\">");
                 if (!p11cw.equals( "" )) {
                    out.println("<option selected value=" + p11cw + ">" + p11cw + "</option>");
                 }
                 for (i=0; i<parm.MAX_Tmodes; i++) {

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p11cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p911 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p911\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p911\" value=\"1\">");
                 }

                 out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player12')\" style=\"cursor:hand\">");
                 out.println("12:&nbsp;&nbsp;<input type=\"text\" name=\"player12\" value=\"" + player12 + "\" size=\"20\" maxlength=\"30\" onkeypress=\"return DYN_disableEnterKey(event)\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p12cw\">");
                 if (!p12cw.equals( "" )) {
                    out.println("<option selected value=" + p12cw + ">" + p12cw + "</option>");
                 }
                 for (i=0; i<parm.MAX_Tmodes; i++) {

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p12cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p912 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p912\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p912\" value=\"1\">");
                 }
              }

              if (players > 12) {

                if (p5.equals( "No" )) {   // if 4-somes only

                  out.println("</font></td></tr>");

                  out.println("<tr><td align=\"center\">");     // new row for new group
                  out.println("<font size=\"2\">");
                }


                 out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player13')\" style=\"cursor:hand\">");
                 out.println("13:&nbsp;&nbsp;<input type=\"text\" name=\"player13\" value=\"" + player13 + "\" size=\"20\" maxlength=\"30\" onkeypress=\"return DYN_disableEnterKey(event)\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p13cw\">");
                 if (!p13cw.equals( "" )) {
                    out.println("<option selected value=" + p13cw + ">" + p13cw + "</option>");
                 }
                 for (i=0; i<parm.MAX_Tmodes; i++) {

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p13cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p913 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p913\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p913\" value=\"1\">");
                 }

                 out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player14')\" style=\"cursor:hand\">");
                 out.println("14:&nbsp;&nbsp;<input type=\"text\" name=\"player14\" value=\"" + player14 + "\" size=\"20\" maxlength=\"30\" onkeypress=\"return DYN_disableEnterKey(event)\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p14cw\">");
                 if (!p14cw.equals( "" )) {
                    out.println("<option selected value=" + p14cw + ">" + p14cw + "</option>");
                 }
                 for (i=0; i<parm.MAX_Tmodes; i++) {

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p14cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p914 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p914\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p914\" value=\"1\">");
                 }

               out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player15')\" style=\"cursor:hand\">");
               out.println("15:&nbsp;&nbsp;<input type=\"text\" id=\"player15\" name=\"player15\" value=\"" + player15 + "\" size=\"20\" maxlength=\"30\" onkeypress=\"return DYN_disableEnterKey(event)\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p15cw\" id=\"p15cw\">");
                 if (!p15cw.equals( "" )) {
                    out.println("<option selected value=" + p15cw + ">" + p15cw + "</option>");
                 }
                 for (i=0; i<parm.MAX_Tmodes; i++) {

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p15cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p915 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p915\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p915\" value=\"1\">");
                 }
              }

              if (players > 15) {

                if (p5.equals( "Yes" )) {   // if 5-somes

                  out.println("</font></td></tr>");

                  out.println("<tr><td align=\"center\">");     // new row for new group
                  out.println("<font size=\"2\">");
                }


                 out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player16')\" style=\"cursor:hand\">");
                 out.println("16:&nbsp;&nbsp;<input type=\"text\" name=\"player16\" value=\"" + player16 + "\" size=\"20\" maxlength=\"30\" onkeypress=\"return DYN_disableEnterKey(event)\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p16cw\">");
                 if (!p16cw.equals( "" )) {
                    out.println("<option selected value=" + p16cw + ">" + p16cw + "</option>");
                 }
                 for (i=0; i<parm.MAX_Tmodes; i++) {

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p16cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p916 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p916\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p916\" value=\"1\">");
                 }

              }
              if (players > 16) {

                if (p5.equals( "No" )) {   // if 4-somes only

                  out.println("</font></td></tr>");

                  out.println("<tr><td align=\"center\">");     // new row for new group
                  out.println("<font size=\"2\">");
                }

                 out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player17')\" style=\"cursor:hand\">");
                 out.println("17:&nbsp;&nbsp;<input type=\"text\" name=\"player17\" value=\"" + player17 + "\" size=\"20\" maxlength=\"30\" onkeypress=\"return DYN_disableEnterKey(event)\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p17cw\">");
                 if (!p17cw.equals( "" )) {
                    out.println("<option selected value=" + p17cw + ">" + p17cw + "</option>");
                 }
                 for (i=0; i<parm.MAX_Tmodes; i++) {

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p17cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p917 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p917\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p917\" value=\"1\">");
                 }

                 out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player18')\" style=\"cursor:hand\">");
                 out.println("18:&nbsp;&nbsp;<input type=\"text\" name=\"player18\" value=\"" + player18 + "\" size=\"20\" maxlength=\"30\" onkeypress=\"return DYN_disableEnterKey(event)\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p18cw\">");
                 if (!p18cw.equals( "" )) {
                    out.println("<option selected value=" + p18cw + ">" + p18cw + "</option>");
                 }
                 for (i=0; i<parm.MAX_Tmodes; i++) {

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p18cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p918 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p918\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p918\" value=\"1\">");
                 }

                 out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player19')\" style=\"cursor:hand\">");
                 out.println("19:&nbsp;&nbsp;<input type=\"text\" name=\"player19\" value=\"" + player19 + "\" size=\"20\" maxlength=\"30\" onkeypress=\"return DYN_disableEnterKey(event)\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p19cw\">");
                 if (!p19cw.equals( "" )) {
                    out.println("<option selected value=" + p19cw + ">" + p19cw + "</option>");
                 }
                 for (i=0; i<parm.MAX_Tmodes; i++) {

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p19cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p919 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p919\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p919\" value=\"1\">");
                 }

                 out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player20')\" style=\"cursor:hand\">");
                 out.println("20:&nbsp;&nbsp;<input type=\"text\" id=\"player20\" name=\"player20\" value=\"" + player20 + "\" size=\"20\" maxlength=\"30\" onkeypress=\"return DYN_disableEnterKey(event)\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p20cw\" id=\"p20cw\">");
                 if (!p20cw.equals( "" )) {
                    out.println("<option selected value=" + p20cw + ">" + p20cw + "</option>");
                 }
                 for (i=0; i<parm.MAX_Tmodes; i++) {

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p20cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p920 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p920\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p920\" value=\"1\">");
                 }
              }

              if (players > 20) {

                 out.println("</font></td></tr>");

                 out.println("<tr><td align=\"center\">");     // new row for new group
                 out.println("<font size=\"2\"><br>");

                 out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player21')\" style=\"cursor:hand\">");
                 out.println("21:&nbsp;&nbsp;<input type=\"text\" name=\"player21\" value=\"" + player21 + "\" size=\"20\" maxlength=\"30\" onkeypress=\"return DYN_disableEnterKey(event)\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p21cw\">");
                 if (!p21cw.equals( "" )) {
                    out.println("<option selected value=" + p21cw + ">" + p21cw + "</option>");
                 }
                 for (i=0; i<parm.MAX_Tmodes; i++) {

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p21cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p921 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p921\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p921\" value=\"1\">");
                 }

                 out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player22')\" style=\"cursor:hand\">");
                 out.println("22:&nbsp;&nbsp;<input type=\"text\" name=\"player22\" value=\"" + player22 + "\" size=\"20\" maxlength=\"30\" onkeypress=\"return DYN_disableEnterKey(event)\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p22cw\">");
                 if (!p22cw.equals( "" )) {
                    out.println("<option selected value=" + p22cw + ">" + p22cw + "</option>");
                 }
                 for (i=0; i<parm.MAX_Tmodes; i++) {

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p22cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p922 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p922\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p922\" value=\"1\">");
                 }

                 out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player23')\" style=\"cursor:hand\">");
                 out.println("23:&nbsp;&nbsp;<input type=\"text\" name=\"player23\" value=\"" + player23 + "\" size=\"20\" maxlength=\"30\" onkeypress=\"return DYN_disableEnterKey(event)\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p23cw\">");
                 if (!p23cw.equals( "" )) {
                    out.println("<option selected value=" + p23cw + ">" + p23cw + "</option>");
                 }
                 for (i=0; i<parm.MAX_Tmodes; i++) {

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p23cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p923 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p923\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p923\" value=\"1\">");
                 }

                 out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player24')\" style=\"cursor:hand\">");
                 out.println("24:&nbsp;&nbsp;<input type=\"text\" name=\"player24\" value=\"" + player24 + "\" size=\"20\" maxlength=\"30\" onkeypress=\"return DYN_disableEnterKey(event)\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p24cw\">");
                 if (!p24cw.equals( "" )) {
                    out.println("<option selected value=" + p24cw + ">" + p24cw + "</option>");
                 }
                 for (i=0; i<parm.MAX_Tmodes; i++) {

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p24cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p924 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p924\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p924\" value=\"1\">");
                 }

               out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player25')\" style=\"cursor:hand\">");
               out.println("25:&nbsp;&nbsp;<input type=\"text\" id=\"player25\" name=\"player25\" value=\"" + player25 + "\" size=\"20\" maxlength=\"30\" onkeypress=\"return DYN_disableEnterKey(event)\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p25cw\" id=\"p25cw\">");
                 if (!p25cw.equals( "" )) {
                    out.println("<option selected value=" + p25cw + ">" + p25cw + "</option>");
                 }
                 for (i=0; i<parm.MAX_Tmodes; i++) {

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p25cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p925 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p925\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p925\" value=\"1\">");
                 }
              }

                 out.println("</font></td></tr>");

                 out.println("<tr><td align=\"center\">");     // new row for notes, etc.
                 out.println("<font size=\"2\">");

               //
               //   Notes
               //
               //   Script will put any existing notes in the textarea (value= doesn't work)
               //
               //out.println("<input type=\"hidden\" name=\"oldnotes\" value=\"" + notes + "\">"); // hold notes for script

               out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasetext('notes')\" style=\"cursor:hand\">");
               out.println("Notes:&nbsp;<textarea name=\"notes\" value=\"\" id=\"notes\" cols=\"28\" rows=\"2\">");
               out.println(notes);
               out.println("</textarea>");

               out.println("<br><br>&nbsp;&nbsp;<strong>Hide Notes</strong> from Members?:&nbsp;&nbsp; ");
               if (hide != 0) {
                  out.println("<input type=\"checkbox\" checked name=\"hide\">");
               } else {
                  out.println("<input type=\"checkbox\" name=\"hide\">");
               }
               /*
               out.println("<select size=\"1\" name=\"hide\">");
               if (hide != 0) {
                 out.println("<option selected value=\"Yes\">Yes</option>");
                 out.println("<option value=\"No\">No</option>");
               } else {
                 out.println("<option selected value=\"No\">No</option>");
                 out.println("<option value=\"Yes\">Yes</option>");
               }
               out.println("</select>");
               * 
               */

               if (recurrpro > 0 && lottid == 0) {   // if pro can recurr the requests for this lottery and its a new request
               
                  out.println("<br><br>&nbsp;&nbsp;Would you like to <strong>recurr this request</strong>?:&nbsp;&nbsp; ");
                  if (isRecurr == true) {
                     out.println("<input type=\"checkbox\" checked name=\"isrecurr\">");
                  } else {
                     out.println("<input type=\"checkbox\" name=\"isrecurr\">");
                  }
               }
               
               out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
               out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + sdate + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
               out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
               out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
               out.println("<input type=\"hidden\" name=\"stime\" value=\"" + stime + "\">");
               out.println("<input type=\"hidden\" name=\"mm\" value=\"" + mm + "\">");
               out.println("<input type=\"hidden\" name=\"yy\" value=\"" + yy + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
               out.println("<input type=\"hidden\" name=\"index2\" value=\"" + index2 + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"p5\" value=\"" + p5 + "\">");
               out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + p5rest + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
               out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lottName + "\">");
               out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate + "\">");
               out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + lottid + "\">");
               out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots + "\">");
               out.println("<input type=\"hidden\" name=\"mins_before\" value=\"" + mins_before + "\">");
               out.println("<input type=\"hidden\" name=\"mins_after\" value=\"" + mins_after + "\">");
               out.println("<input type=\"hidden\" name=\"checkothers\" value=\"" + checkothers + "\">");
               out.println("<input type=\"hidden\" name=\"allowx\" value=\"" + allowx + "\">");

               out.println("<br><br><font size=\"1\">");
               for (i=0; i<parm.MAX_Tmodes; i++) {
                  if (!parmc.tmodea[i].equals( "" )) {
                     out.println(parmc.tmodea[i]+ " = " +parmc.tmode[i]+ "&nbsp;&nbsp;");
                  }
               }
               out.println("(9 = 9 holes)</font><br>");
               out.println("<input type=submit value=\"Submit Request\" name=\"submitForm\">");
               out.println("</font></td></tr>");
               out.println("</table>");
         out.println("</td>");
         out.println("<td valign=\"top\">");


   // ********************************************************************************
   //   If we got control from user clicking on a letter in the Member List,
   //   then we must build the name list.
   // ********************************************************************************
   String letter = "%";         // default is 'List All'

   if (req.getParameter("letter") != null) {

      letter = req.getParameter("letter");

      if (letter.equals( "List All" )) {
         letter = "%";
      } else {
         letter = letter + "%";
      }
   }

   //
   //   Output the List of Names
   //
   alphaTable.nameList(club, letter, mshipOpt, mtypeOpt, false, parmc, enableAdvAssist, false, out, con);


   out.println("</td>");                                      // end of this column
   out.println("<td valign=\"top\">");                        // add column for member list table
     

   //
   //   Output the Alphabit Table for Members' Last Names
   //
   alphaTable.getTable(out, user);


   //
   //   Output the Mship and Mtype Options
   //
   alphaTable.typeOptions(club, mshipOpt, mtypeOpt, out, con);
   

   boolean hidex = true;     // default for lottery (do not allow Xs)
   
   if (allowx > 0) hidex = false;    // do not hide the X if this lottery allows X's  

   //
   //   Output the List of Guests and possibly the X
   //
   alphaTable.guestList(club, course, day_name, time, parm, hidex, false, 0, enableAdvAssist, out, con);


   out.println("</td>");
   out.println("</form>");
   out.println("</tr>");
   out.println("</table>");      // end of large table containg 3 smaller tables

   out.println("</font></td></tr>");
   out.println("</table>");                      // end of main page table

   //
   //  End of HTML page
   //
   out.println("</td></tr>");
   out.println("</table>");                      // end of whole page table
   out.println("</font></center></body></html>");
   out.close();

 }  // end of doPost



 // *********************************************************
 //  Process reservation request from Proshop_lott (HTML)
 // *********************************************************

 private void verify(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session, HttpServletResponse resp) {


   ResultSet rs = null;

   //
   //  Get this session's user name
   //
   String user = (String)session.getAttribute("user");
   String club = (String)session.getAttribute("club");

   int players = 0;
   int slots = 0;
   int lstate = 0;
   int time = 0;
   int hr = 0;
   int min = 0;
   int dd = 0;
   int mm = 0;
   int yy = 0;
   int guests = 0;
   int fb = 0;
   int members = 0;
   int hide = 0;
   int proNew = 0;
   int proMod = 0;
   int i = 0;
   int year = 0;
   int dayNum = 0;
   int temp = 0;
   int sendEmail = 0;
   int emailNew = 0;
   int emailMod = 0;
   int mins_before = 0;
   int mins_after = 0;
   int in_use = 0;
   int gi = 0;
   int hit = 0;
   int ind = 0;
   int allowx = 0;

   int inval1 = 0;
   int inval2 = 0;
   int inval3 = 0;
   int inval4 = 0;
   int inval5 = 0;
   int inval6 = 0;
   int inval7 = 0;
   int inval8 = 0;
   int inval9 = 0;
   int inval10 = 0;
   int inval11 = 0;
   int inval12 = 0;
   int inval13 = 0;
   int inval14 = 0;
   int inval15 = 0;
   int inval16 = 0;
   int inval17 = 0;
   int inval18 = 0;
   int inval19 = 0;
   int inval20 = 0;
   int inval21 = 0;
   int inval22 = 0;
   int inval23 = 0;
   int inval24 = 0;
   int inval25 = 0;

   long date = 0;
   long edate = 0;
   long lottid = 0;

   String player = "";
   String day = "";
   String in_use_by = "";
   String err_name = "";
   String sfb = "";
   String memberName = "";
   String orig_by = user;
   String p9s = "";
   String skip = "";
 
   boolean hit2 = false;
   boolean check = false;
   boolean error = false;
   boolean invalidGuest = false;
   boolean guestdbTbaAllowed = false;
   boolean skipReturns = false;
   boolean updateReq = false;

   boolean overrideAccess = SystemUtils.verifyProAccess(req, "REST_OVERRIDE", con, out);

   String [] playerA = new String [25];   // array to hold all possible player names
   String [] userA = new String [25];     // and usernames
   String [] pcwA = new String [25];      // and transportation modes
   String [] userg = new String [25];     // and user guest names
   int [] guest_idA = new int [25];       // guest ids

   //
   //  Arrays to hold member & guest names to tie guests to members
   //
   String [] gstA = new String [25];     // guests
   String [] memA = new String [25];     // members
   String [] usergA = new String [25];   // guests' associated member (username)


   //
   // Get all the parameters entered
   //
   String slottid = req.getParameter("lottid");       //  id of the lottery request
   String sdate = req.getParameter("date");           //  date of tee time requested (yyyymmdd)
   String stime = req.getParameter("time");           //  time of tee time requested (hhmm)
   String smm = req.getParameter("mm");               //  month of tee time
   String syy = req.getParameter("yy");               //  year of tee time
   String index = req.getParameter("index");          //  day index value (needed by _sheet on return)
   String index2 = req.getParameter("index2");        //  day index value (needed by _mlottery)
   String p5 = req.getParameter("p5");                //  5-somes supported for this slot
   String p5rest = req.getParameter("p5rest");        //  5-somes restricted
   String course = req.getParameter("course");        //  name of course
   String returnCourse = req.getParameter("returnCourse");        //  name of course to return to (multi)
   String sslots = req.getParameter("slots");         //  # of groups allowed for the Lottery
   String slstate = req.getParameter("lstate");       //  Current state of the Lottery (must be < 4 to get here)
                                        //    1 = before time to take requests (too early for requests)
                                        //    2 = after start time, before stop time (ok to take requests)
                                        //    3 = after stop time, before process time (late, but still ok for pro)
                                        //    4 = requests have already been processed (ok for all tee times now)
   //
   //  parm block to hold the club parameters
   //
   parmClub parm2 = new parmClub(0, con); // golf only feature

   //
   //  Put all the parms in a parm block
   //
   parmLott parm = new parmLott();          // allocate a parm block


   //  If guest tracking is in use, determine whether names are optional or required
   if (Utilities.isGuestTrackingConfigured(0, con) && Utilities.isGuestTrackingTbaAllowed(0, true, con)) {
       guestdbTbaAllowed = true;
   }

   parm.player1 = req.getParameter("player1").trim();
   parm.player2 = req.getParameter("player2").trim();
   parm.player3 = req.getParameter("player3").trim();
   parm.player4 = req.getParameter("player4").trim();
   if (req.getParameter("player5") != null) {       
      parm.player5 = req.getParameter("player5").trim();
   }
   if (req.getParameter("player6") != null) {
      parm.player6 = req.getParameter("player6").trim();
   }
   if (req.getParameter("player7") != null) {
      parm.player7 = req.getParameter("player7").trim();
   }
   if (req.getParameter("player8") != null) {
      parm.player8 = req.getParameter("player8").trim();
   }
   if (req.getParameter("player9") != null) {
      parm.player9 = req.getParameter("player9").trim();
   }
   if (req.getParameter("player10") != null) {
      parm.player10 = req.getParameter("player10").trim();
   }
   if (req.getParameter("player11") != null) {
      parm.player11 = req.getParameter("player11").trim();
   }
   if (req.getParameter("player12") != null) {
      parm.player12 = req.getParameter("player12").trim();
   }
   if (req.getParameter("player13") != null) {
      parm.player13 = req.getParameter("player13").trim();
   }
   if (req.getParameter("player14") != null) {
      parm.player14 = req.getParameter("player14").trim();
   }
   if (req.getParameter("player15") != null) {
      parm.player15 = req.getParameter("player15").trim();
   }
   if (req.getParameter("player16") != null) {
      parm.player16 = req.getParameter("player16").trim();
   }
   if (req.getParameter("player17") != null) {
      parm.player17 = req.getParameter("player17").trim();
   }
   if (req.getParameter("player18") != null) {
      parm.player18 = req.getParameter("player18").trim();
   }
   if (req.getParameter("player19") != null) {
      parm.player19 = req.getParameter("player19").trim();
   }
   if (req.getParameter("player20") != null) {
      parm.player20 = req.getParameter("player20").trim();
   }
   if (req.getParameter("player21") != null) {
      parm.player21 = req.getParameter("player21").trim();
   }
   if (req.getParameter("player22") != null) {
      parm.player22 = req.getParameter("player22").trim();
   }
   if (req.getParameter("player23") != null) {
      parm.player23 = req.getParameter("player23").trim();
   }
   if (req.getParameter("player24") != null) {
      parm.player24 = req.getParameter("player24").trim();
   }
   if (req.getParameter("player25") != null) {
      parm.player25 = req.getParameter("player25").trim();
   }
   if (req.getParameter("p1cw") != null) {
      parm.pcw1 = req.getParameter("p1cw");
   }
   if (req.getParameter("p2cw") != null) {
      parm.pcw2 = req.getParameter("p2cw");
   }
   if (req.getParameter("p3cw") != null) {
      parm.pcw3 = req.getParameter("p3cw");
   }
   if (req.getParameter("p4cw") != null) {
      parm.pcw4 = req.getParameter("p4cw");
   }
   if (req.getParameter("p5cw") != null) {
      parm.pcw5 = req.getParameter("p5cw");
   }
   if (req.getParameter("p6cw") != null) {
      parm.pcw6 = req.getParameter("p6cw");
   }
   if (req.getParameter("p7cw") != null) {
      parm.pcw7 = req.getParameter("p7cw");
   }
   if (req.getParameter("p8cw") != null) {
      parm.pcw8 = req.getParameter("p8cw");
   }
   if (req.getParameter("p9cw") != null) {
      parm.pcw9 = req.getParameter("p9cw");
   }
   if (req.getParameter("p10cw") != null) {
      parm.pcw10 = req.getParameter("p10cw");
   }
   if (req.getParameter("p11cw") != null) {
      parm.pcw11 = req.getParameter("p11cw");
   }
   if (req.getParameter("p12cw") != null) {
      parm.pcw12 = req.getParameter("p12cw");
   }
   if (req.getParameter("p13cw") != null) {
      parm.pcw13 = req.getParameter("p13cw");
   }
   if (req.getParameter("p14cw") != null) {
      parm.pcw14 = req.getParameter("p14cw");
   }
   if (req.getParameter("p15cw") != null) {
      parm.pcw15 = req.getParameter("p15cw");
   }
   if (req.getParameter("p16cw") != null) {
      parm.pcw16 = req.getParameter("p16cw");
   }
   if (req.getParameter("p17cw") != null) {
      parm.pcw17 = req.getParameter("p17cw");
   }
   if (req.getParameter("p18cw") != null) {
      parm.pcw18 = req.getParameter("p18cw");
   }
   if (req.getParameter("p19cw") != null) {
      parm.pcw19 = req.getParameter("p19cw");
   }
   if (req.getParameter("p20cw") != null) {
      parm.pcw20 = req.getParameter("p20cw");
   }
   if (req.getParameter("p21cw") != null) {
      parm.pcw21 = req.getParameter("p21cw");
   }
   if (req.getParameter("p22cw") != null) {
      parm.pcw22 = req.getParameter("p22cw");
   }
   if (req.getParameter("p23cw") != null) {
      parm.pcw23 = req.getParameter("p23cw");
   }
   if (req.getParameter("p24cw") != null) {
      parm.pcw24 = req.getParameter("p24cw");
   }
   if (req.getParameter("p25cw") != null) {
      parm.pcw25 = req.getParameter("p25cw");
   }

   parm.guest_id1 = (req.getParameter("guest_id1") != null) ? Integer.parseInt(req.getParameter("guest_id1")) : 0;
   parm.guest_id2 = (req.getParameter("guest_id2") != null) ? Integer.parseInt(req.getParameter("guest_id2")) : 0;
   parm.guest_id3 = (req.getParameter("guest_id3") != null) ? Integer.parseInt(req.getParameter("guest_id3")) : 0;
   parm.guest_id4 = (req.getParameter("guest_id4") != null) ? Integer.parseInt(req.getParameter("guest_id4")) : 0;
   parm.guest_id5 = (req.getParameter("guest_id5") != null) ? Integer.parseInt(req.getParameter("guest_id5")) : 0;
   parm.guest_id6 = (req.getParameter("guest_id6") != null) ? Integer.parseInt(req.getParameter("guest_id6")) : 0;
   parm.guest_id7 = (req.getParameter("guest_id7") != null) ? Integer.parseInt(req.getParameter("guest_id7")) : 0;
   parm.guest_id8 = (req.getParameter("guest_id8") != null) ? Integer.parseInt(req.getParameter("guest_id8")) : 0;
   parm.guest_id9 = (req.getParameter("guest_id9") != null) ? Integer.parseInt(req.getParameter("guest_id9")) : 0;
   parm.guest_id10 = (req.getParameter("guest_id10") != null) ? Integer.parseInt(req.getParameter("guest_id10")) : 0;
   parm.guest_id11 = (req.getParameter("guest_id11") != null) ? Integer.parseInt(req.getParameter("guest_id11")) : 0;
   parm.guest_id12 = (req.getParameter("guest_id12") != null) ? Integer.parseInt(req.getParameter("guest_id12")) : 0;
   parm.guest_id13 = (req.getParameter("guest_id13") != null) ? Integer.parseInt(req.getParameter("guest_id13")) : 0;
   parm.guest_id14 = (req.getParameter("guest_id14") != null) ? Integer.parseInt(req.getParameter("guest_id14")) : 0;
   parm.guest_id15 = (req.getParameter("guest_id15") != null) ? Integer.parseInt(req.getParameter("guest_id15")) : 0;
   parm.guest_id16 = (req.getParameter("guest_id16") != null) ? Integer.parseInt(req.getParameter("guest_id16")) : 0;
   parm.guest_id17 = (req.getParameter("guest_id17") != null) ? Integer.parseInt(req.getParameter("guest_id17")) : 0;
   parm.guest_id18 = (req.getParameter("guest_id18") != null) ? Integer.parseInt(req.getParameter("guest_id18")) : 0;
   parm.guest_id19 = (req.getParameter("guest_id19") != null) ? Integer.parseInt(req.getParameter("guest_id19")) : 0;
   parm.guest_id20 = (req.getParameter("guest_id20") != null) ? Integer.parseInt(req.getParameter("guest_id20")) : 0;
   parm.guest_id21 = (req.getParameter("guest_id21") != null) ? Integer.parseInt(req.getParameter("guest_id21")) : 0;
   parm.guest_id22 = (req.getParameter("guest_id22") != null) ? Integer.parseInt(req.getParameter("guest_id22")) : 0;
   parm.guest_id23 = (req.getParameter("guest_id23") != null) ? Integer.parseInt(req.getParameter("guest_id23")) : 0;
   parm.guest_id24 = (req.getParameter("guest_id24") != null) ? Integer.parseInt(req.getParameter("guest_id24")) : 0;
   parm.guest_id25 = (req.getParameter("guest_id25") != null) ? Integer.parseInt(req.getParameter("guest_id25")) : 0;
        
   day = req.getParameter("day");                      // name of day
   sfb = req.getParameter("fb");                       // Front/Back indicator
   String notes = req.getParameter("notes").trim();                  // Proshop Notes
   String jump = req.getParameter("jump");             // jump index for _sheet
   String lottName = req.getParameter("lname");        // lottery name
   String smins_before = req.getParameter("mins_before");
   String smins_after = req.getParameter("mins_after");
   String checkothers = req.getParameter("checkothers");

   //
   //  set 9-hole options
   //
   parm.p91 = 0;                       // init to 18 holes
   parm.p92 = 0;
   parm.p93 = 0;
   parm.p94 = 0;
   parm.p95 = 0;
   parm.p96 = 0;
   parm.p97 = 0;
   parm.p98 = 0;
   parm.p99 = 0;
   parm.p910 = 0;
   parm.p911 = 0;                       // init to 18 holes
   parm.p912 = 0;
   parm.p913 = 0;
   parm.p914 = 0;
   parm.p915 = 0;
   parm.p916 = 0;
   parm.p917 = 0;
   parm.p918 = 0;
   parm.p919 = 0;
   parm.p920 = 0;
   parm.p921 = 0;                       // init to 18 holes
   parm.p922 = 0;
   parm.p923 = 0;
   parm.p924 = 0;
   parm.p925 = 0;

   if (req.getParameter("p91") != null) {             // get 9-hole indicators if they were checked
      p9s = req.getParameter("p91");
      parm.p91 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p92") != null) {
      p9s = req.getParameter("p92");
      parm.p92 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p93") != null) {
      p9s = req.getParameter("p93");
      parm.p93 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p94") != null) {
      p9s = req.getParameter("p94");
      parm.p94 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p95") != null) {
      p9s = req.getParameter("p95");
      parm.p95 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p96") != null) {
      p9s = req.getParameter("p96");
      parm.p96 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p97") != null) {
      p9s = req.getParameter("p97");
      parm.p97 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p98") != null) {
      p9s = req.getParameter("p98");
      parm.p98 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p99") != null) {
      p9s = req.getParameter("p99");
      parm.p99 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p910") != null) {
      p9s = req.getParameter("p910");
      parm.p910 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p911") != null) {             // get 9-hole indicators if they were checked
      p9s = req.getParameter("p911");
      parm.p911 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p912") != null) {
      p9s = req.getParameter("p912");
      parm.p912 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p913") != null) {
      p9s = req.getParameter("p913");
      parm.p913 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p914") != null) {
      p9s = req.getParameter("p914");
      parm.p914 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p915") != null) {
      p9s = req.getParameter("p915");
      parm.p915 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p916") != null) {
      p9s = req.getParameter("p916");
      parm.p916 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p917") != null) {
      p9s = req.getParameter("p917");
      parm.p917 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p918") != null) {
      p9s = req.getParameter("p918");
      parm.p918 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p919") != null) {
      p9s = req.getParameter("p919");
      parm.p919 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p920") != null) {
      p9s = req.getParameter("p920");
      parm.p920 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p921") != null) {             // get 9-hole indicators if they were checked
      p9s = req.getParameter("p921");
      parm.p921 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p922") != null) {
      p9s = req.getParameter("p922");
      parm.p922 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p923") != null) {
      p9s = req.getParameter("p923");
      parm.p923 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p924") != null) {
      p9s = req.getParameter("p924");
      parm.p924 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p925") != null) {
      p9s = req.getParameter("p925");
      parm.p925 = Integer.parseInt(p9s);
   }

   //
   //  Convert the string values to ints
   //
   try {
      lottid = Long.parseLong(slottid);
      slots = Integer.parseInt(sslots);
      lstate = Integer.parseInt(slstate);
      date = Long.parseLong(sdate);
      time = Integer.parseInt(stime);
      mm = Integer.parseInt(smm);
      yy = Integer.parseInt(syy);
      fb = Integer.parseInt(sfb);
      mins_before = Integer.parseInt(smins_before);
      mins_after = Integer.parseInt(smins_after);
      parm.checkothers = Integer.parseInt(checkothers);
      ind = Integer.parseInt(index);                        // get numeric value of index
   }
   catch (NumberFormatException e) {
      // ignore error
   }
   
   
   //  Get recurrence parms, if specified
   
   if (req.getParameter("isrecurr") != null) {       // if selected as a recurring request

       parm.isRecurr = true;
   }
   
      

   //
   //  See if user wants to hide any notes from the Members
   //
   hide = 0;      // init

   //if (hides.equals( "Yes" )) {

   if (req.getParameter("hide") != null) {       // if hide notes option selected (now a checkbox)

      hide = 1;
      parm.hides = req.getParameter("hide");            // Hide Notes Indicator - set this in case it is still referenced somewhere
   }

   //
   //  Save some of the parms in the parm table
   //
   parm.date = date;
   parm.time = time;
   parm.fb = fb;
   parm.mm = mm;
   parm.dd = dd;
   parm.yy = yy;
   parm.day = day;
   parm.course = course;
   parm.returnCourse = returnCourse;
   parm.hide = hide;
   parm.notes = notes;
   parm.lottName = lottName;
   parm.lottid = lottid;
   parm.lstate = lstate;
   parm.slots = slots;
   parm.mins_before = mins_before;
   parm.mins_after = mins_after;
   parm.index = index;
   parm.index2 = index2;
   parm.ind = ind;
   parm.p5 = p5;
   parm.p5rest = p5rest;
   parm.jump = jump;

   //
   //  Get the length of Notes (max length of 254 chars)
   //
   int notesL = 0;

   if (!notes.equals( "" )) {

      notesL = notes.length();       // get length of notes
   }

   //
   //   use yy and mm and date to determine dd (from tee time's date)
   //
   temp = yy * 10000;
   temp = temp + (mm * 100);
   parm.dd = (int) date - temp;            // get day of month from date

   if (lottid > 0) {          // if request already exists
      //
      //  Check if this request is still 'in use' and still in use by this user??
      //
      //  This is necessary because the user may have gone away while holding this req.  If the
      //  slot timed out (system timer), the slot would be marked 'not in use' and another
      //  user could pick it up.  The original holder could be trying to use it now.
      //
      try {

         PreparedStatement pstmt = con.prepareStatement (
            "SELECT player1, player2, player3, player4, player5, player6, player7, player8, " +
            "player9, player10, player11, player12, player13, player14, player15, player16, player17, " +
            "player18, player19, player20, player21, player22, player23, player24, player25, " +
            "user1, user2, user3, user4, user5, user6, user7, user8, " +
            "user9, user10, user11, user12, user13, user14, user15, user16, user17, " +
            "user18, user19, user20, user21, user22, user23, user24, user25, " +
            "p1cw, p2cw, p3cw, p4cw, p5cw, p6cw, p7cw, p8cw, " +
            "p9cw, p10cw, p11cw, p12cw, p13cw, p14cw, p15cw, p16cw, p17cw, " +
            "p18cw, p19cw, p20cw, p21cw, p22cw, p23cw, p24cw, p25cw, " +
            "notes, hideNotes, proNew, proMod, in_use, in_use_by, orig_by, " +
            "guest_id1, guest_id2, guest_id3, guest_id4, guest_id5, " +
            "guest_id6, guest_id7, guest_id8, guest_id9, guest_id10, " +
            "guest_id11, guest_id12, guest_id13, guest_id14, guest_id15, " +
            "guest_id16, guest_id17, guest_id18, guest_id19, guest_id20, " +
            "guest_id21, guest_id22, guest_id23, guest_id24, guest_id25 " +
            "FROM lreqs3 WHERE id = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setLong(1, lottid);         // put the parm in pstmt
         rs = pstmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.oldplayer1 = rs.getString(1);
            parm.oldplayer2 = rs.getString(2);
            parm.oldplayer3 = rs.getString(3);
            parm.oldplayer4 = rs.getString(4);
            parm.oldplayer5 = rs.getString(5);
            parm.oldplayer6 = rs.getString(6);
            parm.oldplayer7 = rs.getString(7);
            parm.oldplayer8 = rs.getString(8);
            parm.oldplayer9 = rs.getString(9);
            parm.oldplayer10 = rs.getString(10);
            parm.oldplayer11 = rs.getString(11);
            parm.oldplayer12 = rs.getString(12);
            parm.oldplayer13 = rs.getString(13);
            parm.oldplayer14 = rs.getString(14);
            parm.oldplayer15 = rs.getString(15);
            parm.oldplayer16 = rs.getString(16);
            parm.oldplayer17 = rs.getString(17);
            parm.oldplayer18 = rs.getString(18);
            parm.oldplayer19 = rs.getString(19);
            parm.oldplayer20 = rs.getString(20);
            parm.oldplayer21 = rs.getString(21);
            parm.oldplayer22 = rs.getString(22);
            parm.oldplayer23 = rs.getString(23);
            parm.oldplayer24 = rs.getString(24);
            parm.oldplayer25 = rs.getString(25);
            parm.olduser1 = rs.getString(26);
            parm.olduser2 = rs.getString(27);
            parm.olduser3 = rs.getString(28);
            parm.olduser4 = rs.getString(29);
            parm.olduser5 = rs.getString(30);
            parm.olduser6 = rs.getString(31);
            parm.olduser7 = rs.getString(32);
            parm.olduser8 = rs.getString(33);
            parm.olduser9 = rs.getString(34);
            parm.olduser10 = rs.getString(35);
            parm.olduser11 = rs.getString(36);
            parm.olduser12 = rs.getString(37);
            parm.olduser13 = rs.getString(38);
            parm.olduser14 = rs.getString(39);
            parm.olduser15 = rs.getString(40);
            parm.olduser16 = rs.getString(41);
            parm.olduser17 = rs.getString(42);
            parm.olduser18 = rs.getString(43);
            parm.olduser19 = rs.getString(44);
            parm.olduser20 = rs.getString(45);
            parm.olduser21 = rs.getString(46);
            parm.olduser22 = rs.getString(47);
            parm.olduser23 = rs.getString(48);
            parm.olduser24 = rs.getString(49);
            parm.olduser25 = rs.getString(50);
            parm.oldpcw1 = rs.getString(51);
            parm.oldpcw2 = rs.getString(52);
            parm.oldpcw3 = rs.getString(53);
            parm.oldpcw4 = rs.getString(54);
            parm.oldpcw5 = rs.getString(55);
            parm.oldpcw6 = rs.getString(56);
            parm.oldpcw7 = rs.getString(57);
            parm.oldpcw8 = rs.getString(58);
            parm.oldpcw9 = rs.getString(59);
            parm.oldpcw10 = rs.getString(60);
            parm.oldpcw11 = rs.getString(61);
            parm.oldpcw12 = rs.getString(62);
            parm.oldpcw13 = rs.getString(63);
            parm.oldpcw14 = rs.getString(64);
            parm.oldpcw15 = rs.getString(65);
            parm.oldpcw16 = rs.getString(66);
            parm.oldpcw17 = rs.getString(67);
            parm.oldpcw18 = rs.getString(68);
            parm.oldpcw19 = rs.getString(69);
            parm.oldpcw20 = rs.getString(70);
            parm.oldpcw21 = rs.getString(71);
            parm.oldpcw22 = rs.getString(72);
            parm.oldpcw23 = rs.getString(73);
            parm.oldpcw24 = rs.getString(74);
            parm.oldpcw25 = rs.getString(75);
            //notes = rs.getString(76);
            //hide = rs.getInt(77);           // this could override the new setting if pro changed it 
            proNew = rs.getInt(78);
            proMod = rs.getInt(79);
            in_use = rs.getInt(80);
            in_use_by = rs.getString(81);
            orig_by = rs.getString(82);
            parm.oldguest_id1 = rs.getInt("guest_id1");
            parm.oldguest_id2 = rs.getInt("guest_id2");
            parm.oldguest_id3 = rs.getInt("guest_id3");
            parm.oldguest_id4 = rs.getInt("guest_id4");
            parm.oldguest_id5 = rs.getInt("guest_id5");
            parm.oldguest_id6 = rs.getInt("guest_id6");
            parm.oldguest_id7 = rs.getInt("guest_id7");
            parm.oldguest_id8 = rs.getInt("guest_id8");
            parm.oldguest_id9 = rs.getInt("guest_id9");
            parm.oldguest_id10 = rs.getInt("guest_id10");
            parm.oldguest_id11 = rs.getInt("guest_id11");
            parm.oldguest_id12 = rs.getInt("guest_id12");
            parm.oldguest_id13 = rs.getInt("guest_id13");
            parm.oldguest_id14 = rs.getInt("guest_id14");
            parm.oldguest_id15 = rs.getInt("guest_id15");
            parm.oldguest_id16 = rs.getInt("guest_id16");
            parm.oldguest_id17 = rs.getInt("guest_id17");
            parm.oldguest_id18 = rs.getInt("guest_id18");
            parm.oldguest_id19 = rs.getInt("guest_id19");
            parm.oldguest_id20 = rs.getInt("guest_id20");
            parm.oldguest_id21 = rs.getInt("guest_id21");
            parm.oldguest_id22 = rs.getInt("guest_id22");
            parm.oldguest_id23 = rs.getInt("guest_id23");
            parm.oldguest_id24 = rs.getInt("guest_id24");
            parm.oldguest_id25 = rs.getInt("guest_id25");
         }
         pstmt.close();

         if (orig_by.equals( "" )) {    // if originator field still empty

            orig_by = user;             // set this user as the originator
         }

         if ((in_use == 0) || (!in_use_by.equalsIgnoreCase( user ))) {    // if time slot in use and not by this user

            out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<CENTER><BR><BR><H3>Reservation Timer Expired</H3>");
            out.println("<BR><BR>Sorry, but this lottery request has been returned to the system.<BR>");
            out.println("<BR>The system timed out and released the request.");
            out.println("<BR><BR>");

            if (index.equals( "888" )) {      // if from Proshop_searchmem via proshop_main

               out.println("<font size=\"2\">");
               out.println("<form method=\"get\" action=\"Proshop_jump\">");
               out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");
               out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</form></font>");

            } else {

               if (index.equals( "777" )) {      // if from Proshop_mlottery

                  out.println("<font size=\"2\">");
                  out.println("<form method=\"post\" action=\"Proshop_jump\">");
                  out.println("<input type=\"hidden\" name=\"index2\" value=\"" + index2 + "\">");
                  if (!returnCourse.equals( "" )) {
                     out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
                  } else {
                     out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                  }
                  out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
                  out.println("</form></font>");

               } else {

                  out.println("<font size=\"2\">");
                  out.println("<form action=\"Proshop_jump\" method=\"post\">");
                  out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
                  out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
                  if (!returnCourse.equals( "" )) {
                     out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
                  } else {
                     out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                  }
                  out.println("</form></font>");
               }
            }
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
         }
      }
      catch (Exception ignore) {
      }
   }
     
   //
   //  Slide players if necessary so they start at Player1 and run continuously.
   //
   i = 0;                     // init index
   while (i < 25) {
      playerA[i] = "";       // init the player arrays
      userA[i] = "";
      pcwA[i] = "";
      guest_idA[i] = 0;
      i++;
   }

   i = 0;                     // init index

   if (!parm.player1.equals( "" )) {

      playerA[i] = parm.player1;
      userA[i] = parm.user1;
      pcwA[i] = parm.pcw1;
      guest_idA[i] = parm.guest_id1;
      i++;
   }
   if (!parm.player2.equals( "" )) {

      playerA[i] = parm.player2;
      userA[i] = parm.user2;
      pcwA[i] = parm.pcw2;
      guest_idA[i] = parm.guest_id2;
      i++;
   }
   if (!parm.player3.equals( "" )) {

      playerA[i] = parm.player3;
      userA[i] = parm.user3;
      pcwA[i] = parm.pcw3;
      guest_idA[i] = parm.guest_id3;
      i++;
   }
   if (!parm.player4.equals( "" )) {

      playerA[i] = parm.player4;
      userA[i] = parm.user4;
      pcwA[i] = parm.pcw4;
      guest_idA[i] = parm.guest_id4;
      i++;
   }
   if (!parm.player5.equals( "" )) {

      playerA[i] = parm.player5;
      userA[i] = parm.user5;
      pcwA[i] = parm.pcw5;
      guest_idA[i] = parm.guest_id5;
      i++;
   }
   if (!parm.player6.equals( "" )) {

      playerA[i] = parm.player6;
      userA[i] = parm.user6;
      pcwA[i] = parm.pcw6;
      guest_idA[i] = parm.guest_id6;
      i++;
   }
   if (!parm.player7.equals( "" )) {

      playerA[i] = parm.player7;
      userA[i] = parm.user7;
      pcwA[i] = parm.pcw7;
      guest_idA[i] = parm.guest_id7;
      i++;
   }
   if (!parm.player8.equals( "" )) {

      playerA[i] = parm.player8;
      userA[i] = parm.user8;
      pcwA[i] = parm.pcw8;
      guest_idA[i] = parm.guest_id8;
      i++;
   }
   if (!parm.player9.equals( "" )) {

      playerA[i] = parm.player9;
      userA[i] = parm.user9;
      pcwA[i] = parm.pcw9;
      guest_idA[i] = parm.guest_id9;
      i++;
   }
   if (!parm.player10.equals( "" )) {

      playerA[i] = parm.player10;
      userA[i] = parm.user10;
      pcwA[i] = parm.pcw10;
      guest_idA[i] = parm.guest_id10;
      i++;
   }
   if (!parm.player11.equals( "" )) {

      playerA[i] = parm.player11;
      userA[i] = parm.user11;
      pcwA[i] = parm.pcw11;
      guest_idA[i] = parm.guest_id11;
      i++;
   }
   if (!parm.player12.equals( "" )) {

      playerA[i] = parm.player12;
      userA[i] = parm.user12;
      pcwA[i] = parm.pcw12;
      guest_idA[i] = parm.guest_id12;
      i++;
   }
   if (!parm.player13.equals( "" )) {

      playerA[i] = parm.player13;
      userA[i] = parm.user13;
      pcwA[i] = parm.pcw13;
      guest_idA[i] = parm.guest_id13;
      i++;
   }
   if (!parm.player14.equals( "" )) {

      playerA[i] = parm.player14;
      userA[i] = parm.user14;
      pcwA[i] = parm.pcw14;
      guest_idA[i] = parm.guest_id14;
      i++;
   }
   if (!parm.player15.equals( "" )) {

      playerA[i] = parm.player15;
      userA[i] = parm.user15;
      pcwA[i] = parm.pcw15;
      guest_idA[i] = parm.guest_id15;
      i++;
   }
   if (!parm.player16.equals( "" )) {

      playerA[i] = parm.player16;
      userA[i] = parm.user16;
      pcwA[i] = parm.pcw16;
      guest_idA[i] = parm.guest_id16;
      i++;
   }
   if (!parm.player17.equals( "" )) {

      playerA[i] = parm.player17;
      userA[i] = parm.user17;
      pcwA[i] = parm.pcw17;
      guest_idA[i] = parm.guest_id17;
      i++;
   }
   if (!parm.player18.equals( "" )) {

      playerA[i] = parm.player18;
      userA[i] = parm.user18;
      pcwA[i] = parm.pcw18;
      guest_idA[i] = parm.guest_id18;
      i++;
   }
   if (!parm.player19.equals( "" )) {

      playerA[i] = parm.player19;
      userA[i] = parm.user19;
      pcwA[i] = parm.pcw19;
      guest_idA[i] = parm.guest_id19;
      i++;
   }
   if (!parm.player20.equals( "" )) {

      playerA[i] = parm.player20;
      userA[i] = parm.user20;
      pcwA[i] = parm.pcw20;
      guest_idA[i] = parm.guest_id20;
      i++;
   }
   if (!parm.player21.equals( "" )) {

      playerA[i] = parm.player21;
      userA[i] = parm.user21;
      pcwA[i] = parm.pcw21;
      guest_idA[i] = parm.guest_id21;
      i++;
   }
   if (!parm.player22.equals( "" )) {

      playerA[i] = parm.player22;
      userA[i] = parm.user22;
      pcwA[i] = parm.pcw22;
      guest_idA[i] = parm.guest_id22;
      i++;
   }
   if (!parm.player23.equals( "" )) {

      playerA[i] = parm.player23;
      userA[i] = parm.user23;
      pcwA[i] = parm.pcw23;
      guest_idA[i] = parm.guest_id23;
      i++;
   }
   if (!parm.player24.equals( "" )) {

      playerA[i] = parm.player24;
      userA[i] = parm.user24;
      pcwA[i] = parm.pcw24;
      guest_idA[i] = parm.guest_id24;
      i++;
   }
   if (!parm.player25.equals( "" )) {

      playerA[i] = parm.player25;
      userA[i] = parm.user25;
      pcwA[i] = parm.pcw25;
      guest_idA[i] = parm.guest_id25;
      i++;
   }

   //
   //  determine actual number of groups requested
   //
   if (p5.equals( "Yes" )) {

      slots = (i + 4)/5;         // add 4 to allow for fractions

   } else {

      slots = (i + 3)/4;
   }
   players = i;                  // save count for request
   parm.players = i;             // save count for request

   //
   //  Reset the players starting with Player1
   //
   parm.player1 = playerA[0];
   parm.player2 = playerA[1];
   parm.player3 = playerA[2];
   parm.player4 = playerA[3];
   parm.player5 = playerA[4];
   parm.player6 = playerA[5];
   parm.player7 = playerA[6];
   parm.player8 = playerA[7];
   parm.player9 = playerA[8];
   parm.player10 = playerA[9];
   parm.player11 = playerA[10];
   parm.player12 = playerA[11];
   parm.player13 = playerA[12];
   parm.player14 = playerA[13];
   parm.player15 = playerA[14];
   parm.player16 = playerA[15];
   parm.player17 = playerA[16];
   parm.player18 = playerA[17];
   parm.player19 = playerA[18];
   parm.player20 = playerA[19];
   parm.player21 = playerA[20];
   parm.player22 = playerA[21];
   parm.player23 = playerA[22];
   parm.player24 = playerA[23];
   parm.player25 = playerA[24];

   parm.user1 = userA[0];
   parm.user2 = userA[1];
   parm.user3 = userA[2];
   parm.user4 = userA[3];
   parm.user5 = userA[4];
   parm.user6 = userA[5];
   parm.user7 = userA[6];
   parm.user8 = userA[7];
   parm.user9 = userA[8];
   parm.user10 = userA[9];
   parm.user11 = userA[10];
   parm.user12 = userA[11];
   parm.user13 = userA[12];
   parm.user14 = userA[13];
   parm.user15 = userA[14];
   parm.user16 = userA[15];
   parm.user17 = userA[16];
   parm.user18 = userA[17];
   parm.user19 = userA[18];
   parm.user20 = userA[19];
   parm.user21 = userA[20];
   parm.user22 = userA[21];
   parm.user23 = userA[22];
   parm.user24 = userA[23];
   parm.user25 = userA[24];

   parm.pcw1 = pcwA[0];
   parm.pcw2 = pcwA[1];
   parm.pcw3 = pcwA[2];
   parm.pcw4 = pcwA[3];
   parm.pcw5 = pcwA[4];
   parm.pcw6 = pcwA[5];
   parm.pcw7 = pcwA[6];
   parm.pcw8 = pcwA[7];
   parm.pcw9 = pcwA[8];
   parm.pcw10 = pcwA[9];
   parm.pcw11 = pcwA[10];
   parm.pcw12 = pcwA[11];
   parm.pcw13 = pcwA[12];
   parm.pcw14 = pcwA[13];
   parm.pcw15 = pcwA[14];
   parm.pcw16 = pcwA[15];
   parm.pcw17 = pcwA[16];
   parm.pcw18 = pcwA[17];
   parm.pcw19 = pcwA[18];
   parm.pcw20 = pcwA[19];
   parm.pcw21 = pcwA[20];
   parm.pcw22 = pcwA[21];
   parm.pcw23 = pcwA[22];
   parm.pcw24 = pcwA[23];
   parm.pcw25 = pcwA[24];

   parm.guest_id1 = guest_idA[0];
   parm.guest_id2 = guest_idA[1];
   parm.guest_id3 = guest_idA[2];
   parm.guest_id4 = guest_idA[3];
   parm.guest_id5 = guest_idA[4];
   parm.guest_id6 = guest_idA[5];
   parm.guest_id7 = guest_idA[6];
   parm.guest_id8 = guest_idA[7];
   parm.guest_id9 = guest_idA[8];
   parm.guest_id10 = guest_idA[9];
   parm.guest_id11 = guest_idA[10];
   parm.guest_id12 = guest_idA[11];
   parm.guest_id13 = guest_idA[12];
   parm.guest_id14 = guest_idA[13];
   parm.guest_id15 = guest_idA[14];
   parm.guest_id16 = guest_idA[15];
   parm.guest_id17 = guest_idA[16];
   parm.guest_id18 = guest_idA[17];
   parm.guest_id19 = guest_idA[18];
   parm.guest_id20 = guest_idA[19];
   parm.guest_id21 = guest_idA[20];
   parm.guest_id22 = guest_idA[21];
   parm.guest_id23 = guest_idA[22];
   parm.guest_id24 = guest_idA[23];
   parm.guest_id25 = guest_idA[24];

   //
   //  Make sure at least 1 player contains a name
   //
   if ((parm.player1.equals( "" )) && (parm.player2.equals( "" )) && (parm.player3.equals( "" )) && (parm.player4.equals( "" )) &&
       (parm.player5.equals( "" )) && (parm.player6.equals( "" )) && (parm.player7.equals( "" )) && (parm.player8.equals( "" )) &&
       (parm.player9.equals( "" )) && (parm.player10.equals( "" )) && (parm.player11.equals( "" )) && (parm.player12.equals( "" )) &&
       (parm.player13.equals( "" )) && (parm.player14.equals( "" )) && (parm.player15.equals( "" )) && (parm.player16.equals( "" )) &&
       (parm.player17.equals( "" )) && (parm.player18.equals( "" )) && (parm.player19.equals( "" )) && (parm.player20.equals( "" )) &&
       (parm.player21.equals( "" )) && (parm.player22.equals( "" )) && (parm.player23.equals( "" )) && (parm.player24.equals( "" )) &&
       (parm.player25.equals( "" ))) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<center>");
      out.println("<BR><BR><H3>Data Entry Error</H3>");
      out.println("<BR><BR>Required field has not been completed or is invalid.");
      out.println("<BR><BR>At least 1 Player field must contain a valid entry.");
      out.println("<BR><BR>");
      //
      //  Return to _lott
      //
      goReturn(out, parm);
      return;
   }

   //
   //  Make sure at least 1 player in the first 4 contains a name
   //
   if ((parm.player1.equals( "" )) && (parm.player2.equals( "" )) && (parm.player3.equals( "" )) && (parm.player4.equals( "" ))) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<center>");
      out.println("<BR><BR><H3>Data Entry Error</H3>");
      out.println("<BR><BR>Required field has not been completed or is invalid.");
      out.println("<BR><BR>At least 1 of the first 4 Player fields must contain a valid entry.");
      out.println("<BR><BR>");
      //
      //  Return to _lott
      //
      goReturn(out, parm);
      return;
   }

   //
   //  Check if X's are allowed for this lottery
   //
   allowx = getXoption(lottName, con);    // get the X option for this lottery
   

   //
   //  At least 1 Player field is present - Make sure a C/W was specified for all players
   //
   if ((!parm.player1.equals( "" ) && !parm.player1.equalsIgnoreCase( "x" ) && parm.pcw1.equals( "" )) ||
       (!parm.player2.equals( "" ) && !parm.player2.equalsIgnoreCase( "x" ) && parm.pcw2.equals( "" )) ||
       (!parm.player3.equals( "" ) && !parm.player3.equalsIgnoreCase( "x" ) && parm.pcw3.equals( "" )) ||
       (!parm.player4.equals( "" ) && !parm.player4.equalsIgnoreCase( "x" ) && parm.pcw4.equals( "" )) ||
       (!parm.player5.equals( "" ) && !parm.player5.equalsIgnoreCase( "x" ) && parm.pcw5.equals( "" )) ||
       (!parm.player6.equals( "" ) && !parm.player6.equalsIgnoreCase( "x" ) && parm.pcw6.equals( "" )) ||
       (!parm.player7.equals( "" ) && !parm.player7.equalsIgnoreCase( "x" ) && parm.pcw7.equals( "" )) ||
       (!parm.player8.equals( "" ) && !parm.player8.equalsIgnoreCase( "x" ) && parm.pcw8.equals( "" )) ||
       (!parm.player9.equals( "" ) && !parm.player9.equalsIgnoreCase( "x" ) && parm.pcw9.equals( "" )) ||
       (!parm.player10.equals( "" ) && !parm.player10.equalsIgnoreCase( "x" ) && parm.pcw10.equals( "" )) ||
       (!parm.player11.equals( "" ) && !parm.player11.equalsIgnoreCase( "x" ) && parm.pcw11.equals( "" )) ||
       (!parm.player12.equals( "" ) && !parm.player12.equalsIgnoreCase( "x" ) && parm.pcw12.equals( "" )) ||
       (!parm.player13.equals( "" ) && !parm.player13.equalsIgnoreCase( "x" ) && parm.pcw13.equals( "" )) ||
       (!parm.player14.equals( "" ) && !parm.player14.equalsIgnoreCase( "x" ) && parm.pcw14.equals( "" )) ||
       (!parm.player15.equals( "" ) && !parm.player15.equalsIgnoreCase( "x" ) && parm.pcw15.equals( "" )) ||
       (!parm.player16.equals( "" ) && !parm.player16.equalsIgnoreCase( "x" ) && parm.pcw16.equals( "" )) ||
       (!parm.player17.equals( "" ) && !parm.player17.equalsIgnoreCase( "x" ) && parm.pcw17.equals( "" )) ||
       (!parm.player18.equals( "" ) && !parm.player18.equalsIgnoreCase( "x" ) && parm.pcw18.equals( "" )) ||
       (!parm.player19.equals( "" ) && !parm.player19.equalsIgnoreCase( "x" ) && parm.pcw19.equals( "" )) ||
       (!parm.player20.equals( "" ) && !parm.player20.equalsIgnoreCase( "x" ) && parm.pcw20.equals( "" )) ||
       (!parm.player21.equals( "" ) && !parm.player21.equalsIgnoreCase( "x" ) && parm.pcw21.equals( "" )) ||
       (!parm.player22.equals( "" ) && !parm.player22.equalsIgnoreCase( "x" ) && parm.pcw22.equals( "" )) ||
       (!parm.player23.equals( "" ) && !parm.player23.equalsIgnoreCase( "x" ) && parm.pcw23.equals( "" )) ||
       (!parm.player24.equals( "" ) && !parm.player24.equalsIgnoreCase( "x" ) && parm.pcw24.equals( "" )) ||
       (!parm.player25.equals( "" ) && !parm.player25.equalsIgnoreCase( "x" ) && parm.pcw25.equals( "" ))) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<center>");
      out.println("<BR><BR><H3>Data Entry Error</H3>");
      out.println("<BR><BR>Required field has not been completed or is invalid.");
      out.println("<BR><BR>You must specify a Cart or Walk option for all players.");
      out.println("<BR><BR>");
      //
      //  Return to _lott
      //
      goReturn(out, parm);
      return;
   }

   //
   //   Get the guest names specified for this club
   //
   try {
      getClub.getParms(con, parm2);        // get the club parms
     
   }
   catch (Exception ignore) {
   }

   //
   //   Remove any guest types that are null - for tests below
   //
   i = 0;
   while (i < parm2.MAX_Guests) {

      if (parm2.guest[i].equals( "" )) {

         parm2.guest[i] = "$@#!^&*";      // make so it won't match player name
      }
      i++;
   }         // end of while loop

   //
   //  Check if any player names are guest names
   //
   i = 0;
   while (i < 25) {

      gstA[i] = "";    // init guest array and indicators
      i++;
   }

   parm.g[0] = "";
   if (!parm.player1.equals( "" )) {

      i = 0;
      loop1:
      while (i < parm2.MAX_Guests) {

         if (parm.player1.startsWith( parm2.guest[i] )) {

            parm.g[0] = parm2.guest[i];       // indicate player is a guest name and save name
            gstA[0] = parm.player1;    // save guest value
            guests++;            // increment number of guests this request
            parm.guestsg1++;            // increment number of guests this slot

            if (parm2.gDb[i] == 1) {

                if (!guestdbTbaAllowed || parm.guest_id1 != 0 || !parm.player1.equals(parm2.guest[i] + " TBA")) {

                    if (parm.guest_id1 == 0) {
                        invalidGuest = true;
                    } else {
                        invalidGuest = verifySlot.checkTrackedGuestName(parm.player1, parm.guest_id1, parm2.guest[i], parm.club, con);
                    }

                    if (invalidGuest) {
                        parm.gplayer = parm.player1;    // indicate error
                    }
                }
            }

            break loop1;
         }
         i++;
      }         // end of while loop
   }
   parm.g[1] = "";
   if (!parm.player2.equals( "" )) {

      i = 0;
      loop2:
      while (i < parm2.MAX_Guests) {

         if (parm.player2.startsWith( parm2.guest[i] )) {

            parm.g[1] = parm2.guest[i];       // indicate player is a guest name and save name
            gstA[1] = parm.player2;    // save guest value
            guests++;            // increment number of guests this request
            parm.guestsg1++;            // increment number of guests this slot

            if (parm2.gDb[i] == 1) {

                if (!guestdbTbaAllowed || parm.guest_id2 != 0 || !parm.player2.equals(parm2.guest[i] + " TBA")) {

                    if (parm.guest_id2 == 0) {
                        invalidGuest = true;
                    } else {
                        invalidGuest = verifySlot.checkTrackedGuestName(parm.player2, parm.guest_id2, parm2.guest[i], parm.club, con);
                    }

                    if (invalidGuest) {
                        parm.gplayer = parm.player2;    // indicate error
                    }
                }
            }

            break loop2;
         }
         i++;
      }         // end of while loop
   }
   parm.g[2] = "";
   if (!parm.player3.equals( "" )) {

      i = 0;
      loop3:
      while (i < parm2.MAX_Guests) {

         if (parm.player3.startsWith( parm2.guest[i] )) {

            parm.g[2] = parm2.guest[i];       // indicate player is a guest name and save name
            gstA[2] = parm.player3;    // save guest value
            guests++;            // increment number of guests this request
            parm.guestsg1++;            // increment number of guests this slot

            if (parm2.gDb[i] == 1) {

                if (!guestdbTbaAllowed || parm.guest_id3 != 0 || !parm.player3.equals(parm2.guest[i] + " TBA")) {

                    if (parm.guest_id3 == 0) {
                        invalidGuest = true;
                    } else {
                        invalidGuest = verifySlot.checkTrackedGuestName(parm.player3, parm.guest_id3, parm2.guest[i], parm.club, con);
                    }

                    if (invalidGuest) {
                        parm.gplayer = parm.player3;    // indicate error
                    }
                }
            }

            break loop3;
         }
         i++;
      }         // end of while loop
   }
   parm.g[3] = "";
   if (!parm.player4.equals( "" )) {

      i = 0;
      loop4:
      while (i < parm2.MAX_Guests) {

         if (parm.player4.startsWith( parm2.guest[i] )) {

            parm.g[3] = parm2.guest[i];       // indicate player is a guest name and save name
            gstA[3] = parm.player4;    // save guest value
            guests++;            // increment number of guests this request
            parm.guestsg1++;            // increment number of guests this slot

            if (parm2.gDb[i] == 1) {

                if (!guestdbTbaAllowed || parm.guest_id4 != 0 || !parm.player4.equals(parm2.guest[i] + " TBA")) {

                    if (parm.guest_id4 == 0) {
                        invalidGuest = true;
                    } else {
                        invalidGuest = verifySlot.checkTrackedGuestName(parm.player4, parm.guest_id4, parm2.guest[i], parm.club, con);
                    }

                    if (invalidGuest) {
                        parm.gplayer = parm.player4;    // indicate error
                    }
                }
            }

            break loop4;
         }
         i++;
      }         // end of while loop
   }
   parm.g[4] = "";
   if (!parm.player5.equals( "" )) {

      i = 0;
      loop5:
      while (i < parm2.MAX_Guests) {

         if (parm.player5.startsWith( parm2.guest[i] )) {

            parm.g[4] = parm2.guest[i];       // indicate player is a guest name and save name
            gstA[4] = parm.player5;    // save guest value
            guests++;            // increment number of guests this request
            if (p5.equals( "Yes" )) {
               parm.guestsg1++;            // increment number of guests this slot
            } else {
               parm.guestsg2++;            // increment number of guests this slot
            }

            if (parm2.gDb[i] == 1) {

                if (!guestdbTbaAllowed || parm.guest_id5 != 0 || !parm.player5.equals(parm2.guest[i] + " TBA")) {

                    if (parm.guest_id5 == 0) {
                        invalidGuest = true;
                    } else {
                        invalidGuest = verifySlot.checkTrackedGuestName(parm.player5, parm.guest_id5, parm2.guest[i], parm.club, con);
                    }

                    if (invalidGuest) {
                        parm.gplayer = parm.player5;    // indicate error
                    }
                }
            }

            break loop5;
         }
         i++;
      }         // end of while loop
   }
   parm.g[5] = "";
   if (!parm.player6.equals( "" )) {

      i = 0;
      loop6:
      while (i < parm2.MAX_Guests) {

         if (parm.player6.startsWith( parm2.guest[i] )) {

            parm.g[5] = parm2.guest[i];       // indicate player is a guest name and save name
            gstA[5] = parm.player6;    // save guest value
            guests++;            // increment number of guests this slot
            parm.guestsg2++;            // increment number of guests this slot

            if (parm2.gDb[i] == 1) {

                if (!guestdbTbaAllowed || parm.guest_id6 != 0 || !parm.player6.equals(parm2.guest[i] + " TBA")) {

                    if (parm.guest_id6 == 0) {
                        invalidGuest = true;
                    } else {
                        invalidGuest = verifySlot.checkTrackedGuestName(parm.player6, parm.guest_id6, parm2.guest[i], parm.club, con);
                    }

                    if (invalidGuest) {
                        parm.gplayer = parm.player6;    // indicate error
                    }
                }
            }

            break loop6;
         }
         i++;
      }         // end of while loop
   }
   parm.g[6] = "";
   if (!parm.player7.equals( "" )) {

      i = 0;
      loop7:
      while (i < parm2.MAX_Guests) {

         if (parm.player7.startsWith( parm2.guest[i] )) {

            parm.g[6] = parm2.guest[i];       // indicate player is a guest name and save name
            gstA[6] = parm.player7;    // save guest value
            guests++;            // increment number of guests this slot
            parm.guestsg2++;            // increment number of guests this slot

            if (parm2.gDb[i] == 1) {

                if (!guestdbTbaAllowed || parm.guest_id7 != 0 || !parm.player7.equals(parm2.guest[i] + " TBA")) {

                    if (parm.guest_id7 == 0) {
                        invalidGuest = true;
                    } else {
                        invalidGuest = verifySlot.checkTrackedGuestName(parm.player7, parm.guest_id7, parm2.guest[i], parm.club, con);
                    }

                    if (invalidGuest) {
                        parm.gplayer = parm.player7;    // indicate error
                    }
                }
            }

            break loop7;
         }
         i++;
      }         // end of while loop
   }
   parm.g[7] = "";
   if (!parm.player8.equals( "" )) {

      i = 0;
      loop8:
      while (i < parm2.MAX_Guests) {

         if (parm.player8.startsWith( parm2.guest[i] )) {

            parm.g[7] = parm2.guest[i];       // indicate player is a guest name and save name
            gstA[7] = parm.player8;    // save guest value
            guests++;            // increment number of guests this slot
            parm.guestsg2++;            // increment number of guests this slot

            if (parm2.gDb[i] == 1) {

                if (!guestdbTbaAllowed || parm.guest_id8 != 0 || !parm.player8.equals(parm2.guest[i] + " TBA")) {

                    if (parm.guest_id8 == 0) {
                        invalidGuest = true;
                    } else {
                        invalidGuest = verifySlot.checkTrackedGuestName(parm.player8, parm.guest_id8, parm2.guest[i], parm.club, con);
                    }

                    if (invalidGuest) {
                        parm.gplayer = parm.player8;    // indicate error
                    }
                }
            }

            break loop8;
         }
         i++;
      }         // end of while loop
   }
   parm.g[8] = "";
   if (!parm.player9.equals( "" )) {

      i = 0;
      loop9:
      while (i < parm2.MAX_Guests) {

         if (parm.player9.startsWith( parm2.guest[i] )) {

            parm.g[8] = parm2.guest[i];       // indicate player is a guest name and save name
            gstA[8] = parm.player9;    // save guest value
            guests++;            // increment number of guests this slot
            if (p5.equals( "Yes" )) {
               parm.guestsg2++;            // increment number of guests this slot
            } else {
               parm.guestsg3++;            // increment number of guests this slot
            }

            if (parm2.gDb[i] == 1) {

                if (!guestdbTbaAllowed || parm.guest_id9 != 0 || !parm.player9.equals(parm2.guest[i] + " TBA")) {

                    if (parm.guest_id9 == 0) {
                        invalidGuest = true;
                    } else {
                        invalidGuest = verifySlot.checkTrackedGuestName(parm.player9, parm.guest_id9, parm2.guest[i], parm.club, con);
                    }

                    if (invalidGuest) {
                        parm.gplayer = parm.player9;    // indicate error
                    }
                }
            }

            break loop9;
         }
         i++;
      }         // end of while loop
   }
   parm.g[9] = "";
   if (!parm.player10.equals( "" )) {

      i = 0;
      loop10:
      while (i < parm2.MAX_Guests) {

         if (parm.player10.startsWith( parm2.guest[i] )) {

            parm.g[9] = parm2.guest[i];       // indicate player is a guest name and save name
            gstA[9] = parm.player10;    // save guest value
            guests++;            // increment number of guests this slot
            if (p5.equals( "Yes" )) {
               parm.guestsg2++;            // increment number of guests this slot
            } else {
               parm.guestsg3++;            // increment number of guests this slot
            }

            if (parm2.gDb[i] == 1) {

                if (!guestdbTbaAllowed || parm.guest_id10 != 0 || !parm.player10.equals(parm2.guest[i] + " TBA")) {

                    if (parm.guest_id10 == 0) {
                        invalidGuest = true;
                    } else {
                        invalidGuest = verifySlot.checkTrackedGuestName(parm.player10, parm.guest_id10, parm2.guest[i], parm.club, con);
                    }

                    if (invalidGuest) {
                        parm.gplayer = parm.player10;    // indicate error
                    }
                }
            }

            break loop10;
         }
         i++;
      }         // end of while loop
   }
   parm.g[10] = "";
   if (!parm.player11.equals( "" )) {

      i = 0;
      loop11:
      while (i < parm2.MAX_Guests) {

         if (parm.player11.startsWith( parm2.guest[i] )) {

            parm.g[10] = parm2.guest[i];       // indicate player is a guest name and save name
            gstA[1] = parm.player1;    // save guest value
            guests++;            // increment number of guests this slot
            parm.guestsg3++;            // increment number of guests this slot

            if (parm2.gDb[i] == 1) {

                if (!guestdbTbaAllowed || parm.guest_id11 != 0 || !parm.player11.equals(parm2.guest[i] + " TBA")) {

                    if (parm.guest_id11 == 0) {
                        invalidGuest = true;
                    } else {
                        invalidGuest = verifySlot.checkTrackedGuestName(parm.player11, parm.guest_id11, parm2.guest[i], parm.club, con);
                    }

                    if (invalidGuest) {
                        parm.gplayer = parm.player11;    // indicate error
                    }
                }
            }

            break loop11;
         }
         i++;
      }         // end of while loop
   }
   parm.g[11] = "";
   if (!parm.player12.equals( "" )) {

      i = 0;
      loop12:
      while (i < parm2.MAX_Guests) {

         if (parm.player12.startsWith( parm2.guest[i] )) {

            parm.g[11] = parm2.guest[i];       // indicate player is a guest name and save name
            gstA[11] = parm.player12;    // save guest value
            guests++;            // increment number of guests this slot
            parm.guestsg3++;            // increment number of guests this slot

            if (parm2.gDb[i] == 1) {

                if (!guestdbTbaAllowed || parm.guest_id12 != 0 || !parm.player12.equals(parm2.guest[i] + " TBA")) {

                    if (parm.guest_id12 == 0) {
                        invalidGuest = true;
                    } else {
                        invalidGuest = verifySlot.checkTrackedGuestName(parm.player12, parm.guest_id12, parm2.guest[i], parm.club, con);
                    }

                    if (invalidGuest) {
                        parm.gplayer = parm.player12;    // indicate error
                    }
                }
            }

            break loop12;
         }
         i++;
      }         // end of while loop
   }
   parm.g[12] = "";
   if (!parm.player13.equals( "" )) {

      i = 0;
      loop13:
      while (i < parm2.MAX_Guests) {

         if (parm.player13.startsWith( parm2.guest[i] )) {

            parm.g[12] = parm2.guest[i];       // indicate player is a guest name and save name
            gstA[12] = parm.player13;    // save guest value
            guests++;            // increment number of guests this slot
            if (p5.equals( "Yes" )) {
               parm.guestsg3++;            // increment number of guests this slot
            } else {
               parm.guestsg4++;            // increment number of guests this slot
            }

            if (parm2.gDb[i] == 1) {

                if (!guestdbTbaAllowed || parm.guest_id13 != 0 || !parm.player13.equals(parm2.guest[i] + " TBA")) {

                    if (parm.guest_id13 == 0) {
                        invalidGuest = true;
                    } else {
                        invalidGuest = verifySlot.checkTrackedGuestName(parm.player13, parm.guest_id13, parm2.guest[i], parm.club, con);
                    }

                    if (invalidGuest) {
                        parm.gplayer = parm.player13;    // indicate error
                    }
                }
            }

            break loop13;
         }
         i++;
      }         // end of while loop
   }
   parm.g[13] = "";
   if (!parm.player14.equals( "" )) {

      i = 0;
      loop14:
      while (i < parm2.MAX_Guests) {

         if (parm.player14.startsWith( parm2.guest[i] )) {

            parm.g[13] = parm2.guest[i];       // indicate player is a guest name and save name
            gstA[13] = parm.player14;    // save guest value
            guests++;            // increment number of guests this slot
            if (p5.equals( "Yes" )) {
               parm.guestsg3++;            // increment number of guests this slot
            } else {
               parm.guestsg4++;            // increment number of guests this slot
            }

            if (parm2.gDb[i] == 1) {

                if (!guestdbTbaAllowed || parm.guest_id14 != 0 || !parm.player14.equals(parm2.guest[i] + " TBA")) {

                    if (parm.guest_id14 == 0) {
                        invalidGuest = true;
                    } else {
                        invalidGuest = verifySlot.checkTrackedGuestName(parm.player14, parm.guest_id14, parm2.guest[i], parm.club, con);
                    }

                    if (invalidGuest) {
                        parm.gplayer = parm.player14;    // indicate error
                    }
                }
            }

            break loop14;
         }
         i++;
      }         // end of while loop
   }
   parm.g[14] = "";
   if (!parm.player15.equals( "" )) {

      i = 0;
      loop15:
      while (i < parm2.MAX_Guests) {

         if (parm.player15.startsWith( parm2.guest[i] )) {

            parm.g[14] = parm2.guest[i];       // indicate player is a guest name and save name
            gstA[14] = parm.player15;    // save guest value
            guests++;            // increment number of guests this slot
            if (p5.equals( "Yes" )) {
               parm.guestsg3++;            // increment number of guests this slot
            } else {
               parm.guestsg4++;            // increment number of guests this slot
            }

            if (parm2.gDb[i] == 1) {

                if (!guestdbTbaAllowed || parm.guest_id15 != 0 || !parm.player15.equals(parm2.guest[i] + " TBA")) {

                    if (parm.guest_id15 == 0) {
                        invalidGuest = true;
                    } else {
                        invalidGuest = verifySlot.checkTrackedGuestName(parm.player15, parm.guest_id15, parm2.guest[i], parm.club, con);
                    }

                    if (invalidGuest) {
                        parm.gplayer = parm.player15;    // indicate error
                    }
                }
            }

            break loop15;
         }
         i++;
      }         // end of while loop
   }
   parm.g[15] = "";
   if (!parm.player16.equals( "" )) {

      i = 0;
      loop16:
      while (i < parm2.MAX_Guests) {

         if (parm.player16.startsWith( parm2.guest[i] )) {

            parm.g[15] = parm2.guest[i];       // indicate player is a guest name and save name
            gstA[15] = parm.player16;    // save guest value
            guests++;            // increment number of guests this slot
            parm.guestsg4++;            // increment number of guests this slot

            if (parm2.gDb[i] == 1) {

                if (!guestdbTbaAllowed || parm.guest_id16 != 0 || !parm.player16.equals(parm2.guest[i] + " TBA")) {

                    if (parm.guest_id16 == 0) {
                        invalidGuest = true;
                    } else {
                        invalidGuest = verifySlot.checkTrackedGuestName(parm.player16, parm.guest_id16, parm2.guest[i], parm.club, con);
                    }

                    if (invalidGuest) {
                        parm.gplayer = parm.player16;    // indicate error
                    }
                }
            }

            break loop16;
         }
         i++;
      }         // end of while loop
   }
   parm.g[16] = "";
   if (!parm.player17.equals( "" )) {

      i = 0;
      loop17:
      while (i < parm2.MAX_Guests) {

         if (parm.player17.startsWith( parm2.guest[i] )) {

            parm.g[16] = parm2.guest[i];       // indicate player is a guest name and save name
            gstA[16] = parm.player17;    // save guest value
            guests++;            // increment number of guests this slot
            if (p5.equals( "Yes" )) {
               parm.guestsg4++;            // increment number of guests this slot
            } else {
               parm.guestsg5++;            // increment number of guests this slot
            }

            if (parm2.gDb[i] == 1) {

                if (!guestdbTbaAllowed || parm.guest_id17 != 0 || !parm.player17.equals(parm2.guest[i] + " TBA")) {

                    if (parm.guest_id17 == 0) {
                        invalidGuest = true;
                    } else {
                        invalidGuest = verifySlot.checkTrackedGuestName(parm.player17, parm.guest_id17, parm2.guest[i], parm.club, con);
                    }

                    if (invalidGuest) {
                        parm.gplayer = parm.player17;    // indicate error
                    }
                }
            }

            break loop17;
         }
         i++;
      }         // end of while loop
   }
   parm.g[17] = "";
   if (!parm.player18.equals( "" )) {

      i = 0;
      loop18:
      while (i < parm2.MAX_Guests) {

         if (parm.player18.startsWith( parm2.guest[i] )) {

            parm.g[17] = parm2.guest[i];       // indicate player is a guest name and save name
            gstA[17] = parm.player18;    // save guest value
            guests++;            // increment number of guests this slot
            if (p5.equals( "Yes" )) {
               parm.guestsg4++;            // increment number of guests this slot
            } else {
               parm.guestsg5++;            // increment number of guests this slot
            }

            if (parm2.gDb[i] == 1) {

                if (!guestdbTbaAllowed || parm.guest_id18 != 0 || !parm.player18.equals(parm2.guest[i] + " TBA")) {

                    if (parm.guest_id18 == 0) {
                        invalidGuest = true;
                    } else {
                        invalidGuest = verifySlot.checkTrackedGuestName(parm.player18, parm.guest_id18, parm2.guest[i], parm.club, con);
                    }

                    if (invalidGuest) {
                        parm.gplayer = parm.player18;    // indicate error
                    }
                }
            }

            break loop18;
         }
         i++;
      }         // end of while loop
   }
   parm.g[18] = "";
   if (!parm.player19.equals( "" )) {

      i = 0;
      loop19:
      while (i < parm2.MAX_Guests) {

         if (parm.player19.startsWith( parm2.guest[i] )) {

            parm.g[18] = parm2.guest[i];       // indicate player is a guest name and save name
            gstA[18] = parm.player19;    // save guest value
            guests++;            // increment number of guests this slot
            if (p5.equals( "Yes" )) {
               parm.guestsg4++;            // increment number of guests this slot
            } else {
               parm.guestsg5++;            // increment number of guests this slot
            }

            if (parm2.gDb[i] == 1) {

                if (!guestdbTbaAllowed || parm.guest_id19 != 0 || !parm.player19.equals(parm2.guest[i] + " TBA")) {

                    if (parm.guest_id19 == 0) {
                        invalidGuest = true;
                    } else {
                        invalidGuest = verifySlot.checkTrackedGuestName(parm.player19, parm.guest_id19, parm2.guest[i], parm.club, con);
                    }

                    if (invalidGuest) {
                        parm.gplayer = parm.player19;    // indicate error
                    }
                }
            }

            break loop19;
         }
         i++;
      }         // end of while loop
   }
   parm.g[19] = "";
   if (!parm.player20.equals( "" )) {

      i = 0;
      loop20:
      while (i < parm2.MAX_Guests) {

         if (parm.player20.startsWith( parm2.guest[i] )) {

            parm.g[19] = parm2.guest[i];       // indicate player is a guest name and save name
            gstA[19] = parm.player20;    // save guest value
            guests++;            // increment number of guests this slot
            if (p5.equals( "Yes" )) {
               parm.guestsg4++;            // increment number of guests this slot
            } else {
               parm.guestsg5++;            // increment number of guests this slot
            }

            if (parm2.gDb[i] == 1) {

                if (!guestdbTbaAllowed || parm.guest_id20 != 0 || !parm.player20.equals(parm2.guest[i] + " TBA")) {

                    if (parm.guest_id20 == 0) {
                        invalidGuest = true;
                    } else {
                        invalidGuest = verifySlot.checkTrackedGuestName(parm.player20, parm.guest_id20, parm2.guest[i], parm.club, con);
                    }

                    if (invalidGuest) {
                        parm.gplayer = parm.player20;    // indicate error
                    }
                }
            }

            break loop20;
         }
         i++;
      }         // end of while loop
   }
   parm.g[20] = "";
   if (!parm.player21.equals( "" )) {

      i = 0;
      loop21:
      while (i < parm2.MAX_Guests) {

         if (parm.player21.startsWith( parm2.guest[i] )) {

            parm.g[20] = parm2.guest[i];       // indicate player is a guest name and save name
            gstA[20] = parm.player21;    // save guest value
            guests++;            // increment number of guests this slot
            parm.guestsg5++;            // increment number of guests this slot

            if (parm2.gDb[i] == 1) {

                if (!guestdbTbaAllowed || parm.guest_id21 != 0 || !parm.player21.equals(parm2.guest[i] + " TBA")) {

                    if (parm.guest_id21 == 0) {
                        invalidGuest = true;
                    } else {
                        invalidGuest = verifySlot.checkTrackedGuestName(parm.player21, parm.guest_id21, parm2.guest[i], parm.club, con);
                    }

                    if (invalidGuest) {
                        parm.gplayer = parm.player21;    // indicate error
                    }
                }
            }

            break loop21;
         }
         i++;
      }         // end of while loop
   }
   parm.g[21] = "";
   if (!parm.player22.equals( "" )) {

      i = 0;
      loop22:
      while (i < parm2.MAX_Guests) {

         if (parm.player22.startsWith( parm2.guest[i] )) {

            parm.g[21] = parm2.guest[i];       // indicate player is a guest name and save name
            gstA[21] = parm.player22;    // save guest value
            guests++;            // increment number of guests this slot
            parm.guestsg5++;            // increment number of guests this slot

            if (parm2.gDb[i] == 1) {

                if (!guestdbTbaAllowed || parm.guest_id22 != 0 || !parm.player22.equals(parm2.guest[i] + " TBA")) {

                    if (parm.guest_id22 == 0) {
                        invalidGuest = true;
                    } else {
                        invalidGuest = verifySlot.checkTrackedGuestName(parm.player22, parm.guest_id22, parm2.guest[i], parm.club, con);
                    }

                    if (invalidGuest) {
                        parm.gplayer = parm.player22;    // indicate error
                    }
                }
            }

            break loop22;
         }
         i++;
      }         // end of while loop
   }
   parm.g[22] = "";
   if (!parm.player23.equals( "" )) {

      i = 0;
      loop23:
      while (i < parm2.MAX_Guests) {

         if (parm.player23.startsWith( parm2.guest[i] )) {

            parm.g[22] = parm2.guest[i];       // indicate player is a guest name and save name
            gstA[22] = parm.player23;    // save guest value
            guests++;            // increment number of guests this slot
            parm.guestsg5++;            // increment number of guests this slot

            if (parm2.gDb[i] == 1) {

                if (!guestdbTbaAllowed || parm.guest_id23 != 0 || !parm.player23.equals(parm2.guest[i] + " TBA")) {

                    if (parm.guest_id23 == 0) {
                        invalidGuest = true;
                    } else {
                        invalidGuest = verifySlot.checkTrackedGuestName(parm.player23, parm.guest_id23, parm2.guest[i], parm.club, con);
                    }

                    if (invalidGuest) {
                        parm.gplayer = parm.player23;    // indicate error
                    }
                }
            }

            break loop23;
         }
         i++;
      }         // end of while loop
   }
   parm.g[23] = "";
   if (!parm.player24.equals( "" )) {

      i = 0;
      loop24:
      while (i < parm2.MAX_Guests) {

         if (parm.player24.startsWith( parm2.guest[i] )) {

            parm.g[23] = parm2.guest[i];       // indicate player is a guest name and save name
            gstA[23] = parm.player24;    // save guest value
            guests++;            // increment number of guests this slot
            parm.guestsg5++;            // increment number of guests this slot

            if (parm2.gDb[i] == 1) {

                if (!guestdbTbaAllowed || parm.guest_id24 != 0 || !parm.player24.equals(parm2.guest[i] + " TBA")) {

                    if (parm.guest_id24 == 0) {
                        invalidGuest = true;
                    } else {
                        invalidGuest = verifySlot.checkTrackedGuestName(parm.player24, parm.guest_id24, parm2.guest[i], parm.club, con);
                    }

                    if (invalidGuest) {
                        parm.gplayer = parm.player24;    // indicate error
                    }
                }
            }

            break loop24;
         }
         i++;
      }         // end of while loop
   }
   parm.g[24] = "";
   if (!parm.player25.equals( "" )) {

      i = 0;
      loop25:
      while (i < parm2.MAX_Guests) {

         if (parm.player25.startsWith( parm2.guest[i] )) {

            parm.g[24] = parm2.guest[i];       // indicate player is a guest name and save name
            gstA[24] = parm.player25;    // save guest value
            guests++;            // increment number of guests this slot
            parm.guestsg5++;            // increment number of guests this slot

            if (parm2.gDb[i] == 1) {

                if (!guestdbTbaAllowed || parm.guest_id25 != 0 || !parm.player25.equals(parm2.guest[i] + " TBA")) {

                    if (parm.guest_id25 == 0) {
                        invalidGuest = true;
                    } else {
                        invalidGuest = verifySlot.checkTrackedGuestName(parm.player25, parm.guest_id25, parm2.guest[i], parm.club, con);
                    }

                    if (invalidGuest) {
                        parm.gplayer = parm.player25;    // indicate error
                    }
                }
            }

            break loop25;
         }
         i++;
      }         // end of while loop
   }


  //
  //  See if an invalid tracked guest entry was present
  //
  if (!parm.gplayer.equals("")) {

     out.println(SystemUtils.HeadTitle("Data Entry Error"));
     out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
     out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
     out.println("<center>");
     out.println("<BR><BR><H3>Data Entry Error</H3>");
     out.println("<BR><BR><b>" + parm.gplayer + "</b> appears to have been manually entered or " +
             "<br>modified after selecting a different guest from the Guest Selection window.");
     out.println("<BR><BR>Since this guest type uses the Guest Tracking feature, please click 'erase' ");
     out.println("<BR>next to the current guest's name, then click the desired guest type from the Guest ");
     out.println("<BR>Types list, and finally select a guest from the displayed guest selection window.");
     out.println("<BR><BR>");
     out.println("<font size=\"2\">");

     skip = "skip1";
     goReturn(out, parm, overrideAccess, user, skip);

     out.println("</font></center></body></html>");
     out.close();
     return;
  }

   //
   //  Make sure there are no duplicate names.
   //  Also, Parse the names to separate first, last & mi
   //  (Proshop does not verify single tokens - check for guest)
   //
   error = parseNames(out, parm);

   if (error == true) {

      return;           // exit if error encountered and reported
   }

   //
   //  Get the usernames, membership types, etc. for players if matching name found
   //
   error = getUsers(out, parm, con);

   if (error == true) {

      return;           // exit if error encountered and reported
   }

   inval1 = parm.inval1;
   inval2 = parm.inval2;
   inval3 = parm.inval3;
   inval4 = parm.inval4;
   inval5 = parm.inval5;
   inval6 = parm.inval6;
   inval7 = parm.inval7;
   inval8 = parm.inval8;
   inval9 = parm.inval9;
   inval10 = parm.inval10;
   inval11 = parm.inval11;
   inval12 = parm.inval12;
   inval13 = parm.inval13;
   inval14 = parm.inval14;
   inval15 = parm.inval15;
   inval16 = parm.inval16;
   inval17 = parm.inval17;
   inval18 = parm.inval18;
   inval19 = parm.inval19;
   inval20 = parm.inval20;
   inval21 = parm.inval21;
   inval22 = parm.inval22;
   inval23 = parm.inval23;
   inval24 = parm.inval24;
   inval25 = parm.inval25;
   members = parm.members;   // get member counter

   //
   //  Save the members' usernames for guest association
   //
   memA[0] = parm.user1;
   memA[1] = parm.user2;
   memA[2] = parm.user3;
   memA[3] = parm.user4;
   memA[4] = parm.user5;
   memA[5] = parm.user6;
   memA[6] = parm.user7;
   memA[7] = parm.user8;
   memA[8] = parm.user9;
   memA[9] = parm.user10;
   memA[10] = parm.user11;
   memA[11] = parm.user12;
   memA[12] = parm.user13;
   memA[13] = parm.user14;
   memA[14] = parm.user15;
   memA[15] = parm.user16;
   memA[16] = parm.user17;
   memA[17] = parm.user18;
   memA[18] = parm.user19;
   memA[19] = parm.user20;
   memA[20] = parm.user21;
   memA[21] = parm.user22;
   memA[22] = parm.user23;
   memA[23] = parm.user24;
   memA[24] = parm.user25;

   //
   //  Check if proshop user requested that we skip the following name test.
   //
   //  If any skips are set, then we've already been through here.
   //
   if ((req.getParameter("skip1") == null) && (req.getParameter("skip2") == null) &&
       (req.getParameter("skip3") == null) && (req.getParameter("skip4") == null) &&
       (req.getParameter("skip5") == null) && (req.getParameter("skip6") == null) &&
       (req.getParameter("skip7") == null) && (req.getParameter("skip8") == null) &&
       (req.getParameter("skip9") == null) && (req.getParameter("skip10") == null)) {

      //
      //  Check if any of the names are invalid.  If so, ask proshop if they want to ignore the error.
      //
      if (inval25 != 0) {

         err_name = parm.player25;
      }
      if (inval24 != 0) {

         err_name = parm.player24;
      }
      if (inval23 != 0) {

         err_name = parm.player23;
      }
      if (inval22 != 0) {

         err_name = parm.player22;
      }
      if (inval21 != 0) {

         err_name = parm.player21;
      }
      if (inval20 != 0) {

         err_name = parm.player20;
      }
      if (inval19 != 0) {

         err_name = parm.player19;
      }
      if (inval18 != 0) {

         err_name = parm.player18;
      }
      if (inval17 != 0) {

         err_name = parm.player17;
      }
      if (inval16 != 0) {

         err_name = parm.player16;
      }
      if (inval15 != 0) {

         err_name = parm.player15;
      }
      if (inval14 != 0) {

         err_name = parm.player14;
      }
      if (inval13 != 0) {

         err_name = parm.player13;
      }
      if (inval12 != 0) {

         err_name = parm.player12;
      }
      if (inval11 != 0) {

         err_name = parm.player11;
      }
      if (inval10 != 0) {

         err_name = parm.player10;
      }
      if (inval9 != 0) {

         err_name = parm.player9;
      }
      if (inval8 != 0) {

         err_name = parm.player8;
      }
      if (inval7 != 0) {

         err_name = parm.player7;
      }
      if (inval6 != 0) {

         err_name = parm.player6;
      }
      if (inval5 != 0) {

         err_name = parm.player5;
      }
      if (inval4 != 0) {

         err_name = parm.player4;
      }
      if (inval3 != 0) {

         err_name = parm.player3;
      }
      if (inval2 != 0) {

         err_name = parm.player2;
      }
      if (inval1 != 0) {

         err_name = parm.player1;
      }

      if (!err_name.equals( "" )) {      // invalid name received

         out.println(SystemUtils.HeadTitle("Player Not Found - Prompt"));
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><H3>Player's Name Not Found in System</H3><BR>");
         out.println("<BR><BR>Warning:  " + err_name + " does not exist in the system database.");

         //
         //  Return to _lott
         //
         skip = "skip1";
         goReturn(out, parm, skip);
         return;
      }
   }       // end of skip1

   //
   //  Check if proshop user requested that we skip the mship test (member exceeded max and proshop
   //  wants to override the violation).
   //
   //  If this skip, or any of the following skips are set, then we've already been through these tests.
   //
   if ((req.getParameter("skip2") == null) && (req.getParameter("skip3") == null) &&
       (req.getParameter("skip4") == null) && (req.getParameter("skip5") == null) &&
       (req.getParameter("skip6") == null) && (req.getParameter("skip7") == null) && 
       (req.getParameter("skip8") == null) && (req.getParameter("skip9") == null) &&
       (req.getParameter("skip10") == null)) {

      //
      //************************************************************************
      //  No, normal request -
      //  Check any membership types for max rounds per week, month or year
      //************************************************************************
      //
      if (!parm.mship1.equals( "" ) ||
          !parm.mship2.equals( "" ) ||
          !parm.mship3.equals( "" ) ||
          !parm.mship4.equals( "" ) ||
          !parm.mship5.equals( "" ) ||
          !parm.mship6.equals( "" ) ||
          !parm.mship7.equals( "" ) ||
          !parm.mship8.equals( "" ) ||
          !parm.mship9.equals( "" ) ||
          !parm.mship10.equals( "" ) ||
          !parm.mship11.equals( "" ) ||
          !parm.mship12.equals( "" ) ||
          !parm.mship13.equals( "" ) ||
          !parm.mship14.equals( "" ) ||
          !parm.mship15.equals( "" ) ||
          !parm.mship16.equals( "" ) ||
          !parm.mship17.equals( "" ) ||
          !parm.mship18.equals( "" ) ||
          !parm.mship19.equals( "" ) ||
          !parm.mship20.equals( "" ) ||
          !parm.mship21.equals( "" ) ||
          !parm.mship22.equals( "" ) ||
          !parm.mship23.equals( "" ) ||
          !parm.mship24.equals( "" ) ||
          !parm.mship25.equals( "" )) {                // if at least one name exists then check number of rounds

         //  Cherry Hills CC - Check for a Resident Emeritus member in player slot 1. Restrict if present.
         if (club.equals("cherryhills")) {
             
             if (parm.mship1.equalsIgnoreCase("Resident Emeritus")) {
                 
                parm.player = parm.player1;
                 
                out.println(SystemUtils.HeadTitle("Data Entry Error"));
                out.println("<body>");
                out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                out.println("<center>");
                out.println("<BR><BR><BR><H3>Player Not Allowed</H3>");
                out.println("<BR><BR>Sorry, " + parm.player + " cannot make a lottery request.");
                out.println("<BR><BR>Resident Emeritus members are not allowed to make lottery requests.");
                
                //  Return to _lott
                skip = "skip2";
                goReturn(out, parm, overrideAccess, "", skip);
                return;
                
             } else if (parm.mship1.equalsIgnoreCase("Non-Resident") || parm.mship2.equalsIgnoreCase("Non-Resident") || parm.mship3.equalsIgnoreCase("Non-Resident") 
                     || parm.mship4.equalsIgnoreCase("Non-Resident") || parm.mship5.equalsIgnoreCase("Non-Resident")) {
                 
                 if (parm.mship1.equalsIgnoreCase("Non-Resident")) {
                     parm.player = parm.player1;
                 } else if (parm.mship2.equalsIgnoreCase("Non-Resident")) {
                     parm.player = parm.player2;
                 } else if (parm.mship3.equalsIgnoreCase("Non-Resident")) {
                     parm.player = parm.player3;
                 } else if (parm.mship4.equalsIgnoreCase("Non-Resident")) {
                     parm.player = parm.player4;
                 } else if (parm.mship5.equalsIgnoreCase("Non-Resident")) {
                     parm.player = parm.player5;
                 }
                 
                 out.println(SystemUtils.HeadTitle("Data Entry Error"));
                 out.println("<body>");
                 out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                 out.println("<center>");
                 out.println("<BR><BR><BR><H3>Player Not Allowed</H3>");
                 out.println("<BR><BR>Sorry, " + parm.player + " cannot be a part of lottery request.");
                 out.println("<BR><BR>Non-Resident members are not allowed to be a part of lottery requests.");
                 
                 //  Return to _lott
                 skip = "skip2";
                 goReturn(out, parm, overrideAccess, "", skip);
                 return;
             }
         }
          
         //
         // *******************************************************************************
         //  Check Membership Restrictions for Max Rounds
         // *******************************************************************************
         //
         check = checkMemship(con, out, parm, day);      // go check

         if (parm.error == true) {          // if we hit a db error

            return;
         }

         if (check == true) {      // a member exceed the max allowed tee times per month

            out.println(SystemUtils.HeadTitle("Max Limit Exceeded - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><H3>Member Exceeded Limit</H3><BR>");
            out.println("<BR><BR>Warning:  " + parm.player + " is a " + parm.mship + " member and has exceeded the<BR>");
            out.println("maximum number of tee times allowed for this " + parm.period + ".");
            //
            //  Return to _lott
            //
            skip = "skip2";
            goReturn(out, parm, overrideAccess, "", skip);
            return;
         }
      }      // end of mship if
   }         // end of skip2 if

   //
   //  Check if proshop user requested that we skip the max # of guests test
   //
   //  If this skip, or any of the following skips are set, then we've already been through these tests.
   //
   if ((req.getParameter("skip3") == null) && (req.getParameter("skip4") == null) &&
       (req.getParameter("skip5") == null) && (req.getParameter("skip6") == null) &&
       (req.getParameter("skip7") == null) && (req.getParameter("skip8") == null) &&
       (req.getParameter("skip9") == null) && (req.getParameter("skip10") == null)) {

      //
      //************************************************************************
      //  Check for max # of guests exceeded (per member)
      //************************************************************************
      //
      if (guests != 0) {      // if any guests were included

//         error = checkGuests(out, parm, con, guests);

//         if (error == true) {

//            return;           // exit if error encountered and reported
//         }
  
         //  
         //  use Common_Lott now
         //
         error = Common_Lott.checkGuests(parm, con, guests);      // go check in Common_Lott (updated method)

         if (error == true) {         // if too many guests

            out.println(SystemUtils.HeadTitle("Max Num Guests Exceeded - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><H3>Number of Guests Exceeded Limit</H3><BR>");
            out.println("<BR><BR>Sorry, the maximum number of guests allowed for the<BR>");
            out.println("time you are requesting is " + parm.grest_num + " per " +parm.period+ ".");
            out.println("<BR><BR>Restriction Name = " +parm.rest_name);
            out.println("<BR><BR>");
            //
            //  Return to _lott
            //
            skip = "skip3";
            goReturn(out, parm, skip);
            return;
         }
  
      }      // end of if guests

   }  // end of skip3 if

   //
   //  Check if proshop user requested that we skip the member restrictions test
   //
   //  If this skip, or any following skips are set, then we've already been through these tests.
   //
   if ((req.getParameter("skip4") == null) && (req.getParameter("skip5") == null) &&
       (req.getParameter("skip6") == null) && (req.getParameter("skip7") == null) && 
       (req.getParameter("skip8") == null) && (req.getParameter("skip9") == null) &&
       (req.getParameter("skip10") == null)) {

      //
      // *******************************************************************************
      //  Check Member Restrictions
      // *******************************************************************************
      //
      check = checkMemRes(con, out, parm, day);      // go check

      if (parm.error == true) {          // if we hit a db error

         return;
      }

      if (check == true) {          // if we hit on a restriction

         out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><BR><H3>Member Restricted</H3><BR>");
         out.println("<BR>Sorry, <b>" + parm.player + "</b> is restricted from playing during this time.<br><br>");
         out.println("This time slot has the following restriction:  <b>" + parm.rest_name + "</b><br><br>");
         //
         //  Return to _lott
         //
         skip = "skip4";
         goReturn(out, parm, overrideAccess, "", skip);
         return;
      }

      if (club.equals( "cherryhills" )) {

         //
         // *******************************************************************************
         //  Cherry Hills - custom member type and membership restrictions
         // *******************************************************************************
         //
         check = checkCherryRes(parm);          // go check

         if (check == true) {          // if we hit on a restriction

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body>");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><BR><H3>Player Not Allowed</H3>");
            out.println("<BR><BR>Sorry, one or more players are not allowed to be part of a tee time for this day and time.");
            if (day.equals( "Monday" ) || day.equals( "Wednesday" ) || day.equals( "Friday" )) {
               out.println("<BR><BR>A Member must be included when making the request more than 1 day in advance.");
            } else {
               if (day.equals( "Tuesday" )) {
                  if (time > 1100) {
                     out.println("<BR><BR>A Member must be included when making the request more than 1 day in advance.");
                  } else {
                     out.println("<BR><BR>Only Spouses may make a request more than 1 day in advance for a tee time before 11 AM on Tuesdays.");
                  }
               } else {
                  if (day.equals( "Thursday" )) {
                     if (time > 1000) {
                        out.println("<BR><BR>A Member must be included when making the request more than 1 day in advance.");
                     } else {
                        out.println("<BR><BR>Only Spouses may make a request more than 1 day in advance for a tee time before 10 AM on Thursdays.");
                     }
                  } else {
                     if (day.equals( "Sunday" )) {
                        if (time > 1000) {
                           out.println("<BR><BR>A Member must be included when making the request more than 1 day in advance.");
                        } else {
                           out.println("<BR><BR>Only Members may be included in a tee time before 10 AM on Sundays.");
                        }
                     } else {       // Saturday or Holiday
                        if (time > 1100) {
                           out.println("<BR><BR>A Member must be included when making the request more than 1 day in advance.");
                        } else {
                           out.println("<BR><BR>Player not allowed to make a tee time more than 24 hours in advance on Saturdays and Holidays before 11 AM.");
                        }
                     }
                  }
               }
            }
            //
            //  Return to _lott
            //
            skip = "skip4";
            goReturn(out, parm, overrideAccess, "", skip);
            return;
         }
      }

   }  // end of skip4 if (5-some check removed - skip 5)

   //
   //  Check if proshop user requested that we skip the following test
   //
   //  If either skip is set, then we've already been through these tests.
   //
   if ((req.getParameter("skip6") == null) && (req.getParameter("skip7") == null) && 
       (req.getParameter("skip8") == null) && (req.getParameter("skip9") == null) && (req.getParameter("skip10") == null)) {

      //
      // *******************************************************************************
      //  Check Member Number restrictions
      //
      // *******************************************************************************
      //
      check = checkMemNum(con, out, parm, day);      // go check

      if (parm.error == true) {          // if we hit a db error

         return;
      }

      if (check == true) {          // if we hit on a restriction

         out.println(SystemUtils.HeadTitle("Member Number Restricted - Reject"));
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><BR><H3>Member Restricted by Member Number</H3><BR>");
         out.println("<BR>Sorry, ");
         if (!parm.pNum1.equals( "" )) {
            out.println("<b>" + parm.pNum1 + "</b> ");
         }
         if (!parm.pNum2.equals( "" )) {
            out.println("<b>" + parm.pNum2 + "</b> ");
         }
         if (!parm.pNum3.equals( "" )) {
            out.println("<b>" + parm.pNum3 + "</b> ");
         }
         if (!parm.pNum4.equals( "" )) {
            out.println("<b>" + parm.pNum4 + "</b> ");
         }
         if (!parm.pNum5.equals( "" )) {
            out.println("<b>" + parm.pNum5 + "</b> ");
         }
         if (!parm.pNum6.equals( "" )) {
            out.println("<b>" + parm.pNum6 + "</b> ");
         }
         if (!parm.pNum7.equals( "" )) {
            out.println("<b>" + parm.pNum7 + "</b> ");
         }
         if (!parm.pNum8.equals( "" )) {
            out.println("<b>" + parm.pNum8 + "</b> ");
         }
         if (!parm.pNum9.equals( "" )) {
            out.println("<b>" + parm.pNum9 + "</b> ");
         }
         if (!parm.pNum10.equals( "" )) {
            out.println("<b>" + parm.pNum10 + "</b> ");
         }
         if (!parm.pNum11.equals( "" )) {
            out.println("<b>" + parm.pNum11 + "</b> ");
         }
         if (!parm.pNum12.equals( "" )) {
            out.println("<b>" + parm.pNum12 + "</b> ");
         }
         if (!parm.pNum13.equals( "" )) {
            out.println("<b>" + parm.pNum13 + "</b> ");
         }
         if (!parm.pNum14.equals( "" )) {
            out.println("<b>" + parm.pNum14 + "</b> ");
         }
         if (!parm.pNum15.equals( "" )) {
            out.println("<b>" + parm.pNum15 + "</b> ");
         }
         if (!parm.pNum16.equals( "" )) {
            out.println("<b>" + parm.pNum16 + "</b> ");
         }
         if (!parm.pNum17.equals( "" )) {
            out.println("<b>" + parm.pNum17 + "</b> ");
         }
         if (!parm.pNum18.equals( "" )) {
            out.println("<b>" + parm.pNum18 + "</b> ");
         }
         if (!parm.pNum19.equals( "" )) {
            out.println("<b>" + parm.pNum19 + "</b> ");
         }
         if (!parm.pNum20.equals( "" )) {
            out.println("<b>" + parm.pNum20 + "</b> ");
         }
         if (!parm.pNum21.equals( "" )) {
            out.println("<b>" + parm.pNum21 + "</b> ");
         }
         if (!parm.pNum22.equals( "" )) {
            out.println("<b>" + parm.pNum22 + "</b> ");
         }
         if (!parm.pNum23.equals( "" )) {
            out.println("<b>" + parm.pNum23 + "</b> ");
         }
         if (!parm.pNum24.equals( "" )) {
            out.println("<b>" + parm.pNum24 + "</b> ");
         }
         if (!parm.pNum25.equals( "" )) {
            out.println("<b>" + parm.pNum25 + "</b> ");
         }
         out.println("is/are restricted from playing during this time because the");
         out.println("<BR> number of members with the same member number has exceeded the maximum allowed.");
         out.println("<br><br>This time slot has the following restriction:  <b>" + parm.rest_name + "</b>");
         //
         //  Return to _lott
         //
         skip = "skip6";
         goReturn(out, parm, overrideAccess, "", skip);
         return;
      }
   }         // end of IF skip6


   //
   //  Check if proshop user requested that we skip the following test
   //
   //  If this skip is set, then we've already been through these tests.
   //
   if ((req.getParameter("skip7") == null) && (req.getParameter("skip8") == null) &&
       (req.getParameter("skip9") == null) && (req.getParameter("skip10") == null)) {

      //
      //***********************************************************************************************
      //
      //    Now check if any of the players are already scheduled today (only 1 res per day) 
      //
      //***********************************************************************************************
      //
      hit = 0;

      if (!parm.player1.equals( "" ) && !parm.player1.equalsIgnoreCase( "x" ) && parm.g[0].equals( "" )) {

         player = parm.player1;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player2.equals( "" ) && !parm.player2.equalsIgnoreCase( "x" ) && parm.g[1].equals( "" ) && hit == 0) {

         player = parm.player2;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player3.equals( "" ) && !parm.player3.equalsIgnoreCase( "x" ) && parm.g[2].equals( "" ) && hit == 0) {

         player = parm.player3;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player4.equals( "" ) && !parm.player4.equalsIgnoreCase( "x" ) && parm.g[3].equals( "" ) && hit == 0) {

         player = parm.player4;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player5.equals( "" ) && !parm.player5.equalsIgnoreCase( "x" ) && parm.g[4].equals( "" ) && hit == 0) {

         player = parm.player5;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player6.equals( "" ) && !parm.player6.equalsIgnoreCase( "x" ) && parm.g[5].equals( "" ) && hit == 0) {

         player = parm.player6;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player7.equals( "" ) && !parm.player7.equalsIgnoreCase( "x" ) && parm.g[6].equals( "" ) && hit == 0) {

         player = parm.player7;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player8.equals( "" ) && !parm.player8.equalsIgnoreCase( "x" ) && parm.g[7].equals( "" ) && hit == 0) {

         player = parm.player8;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player9.equals( "" ) && !parm.player9.equalsIgnoreCase( "x" ) && parm.g[8].equals( "" ) && hit == 0) {

         player = parm.player9;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player10.equals( "" ) && !parm.player10.equalsIgnoreCase( "x" ) && parm.g[9].equals( "" ) && hit == 0) {

         player = parm.player10;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player11.equals( "" ) && !parm.player11.equalsIgnoreCase( "x" ) && parm.g[10].equals( "" ) && hit == 0) {

         player = parm.player11;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player12.equals( "" ) && !parm.player12.equalsIgnoreCase( "x" ) && parm.g[11].equals( "" ) && hit == 0) {

         player = parm.player12;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player13.equals( "" ) && !parm.player13.equalsIgnoreCase( "x" ) && parm.g[12].equals( "" ) && hit == 0) {

         player = parm.player13;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player14.equals( "" ) && !parm.player14.equalsIgnoreCase( "x" ) && parm.g[13].equals( "" ) && hit == 0) {

         player = parm.player14;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player15.equals( "" ) && !parm.player15.equalsIgnoreCase( "x" ) && parm.g[14].equals( "" ) && hit == 0) {

         player = parm.player15;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player16.equals( "" ) && !parm.player16.equalsIgnoreCase( "x" ) && parm.g[15].equals( "" ) && hit == 0) {

         player = parm.player16;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player17.equals( "" ) && !parm.player17.equalsIgnoreCase( "x" ) && parm.g[16].equals( "" ) && hit == 0) {

         player = parm.player17;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player18.equals( "" ) && !parm.player18.equalsIgnoreCase( "x" ) && parm.g[17].equals( "" ) && hit == 0) {

         player = parm.player18;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player19.equals( "" ) && !parm.player19.equalsIgnoreCase( "x" ) && parm.g[18].equals( "" ) && hit == 0) {

         player = parm.player19;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player20.equals( "" ) && !parm.player20.equalsIgnoreCase( "x" ) && parm.g[19].equals( "" ) && hit == 0) {

         player = parm.player20;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player21.equals( "" ) && !parm.player21.equalsIgnoreCase( "x" ) && parm.g[20].equals( "" ) && hit == 0) {

         player = parm.player21;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player22.equals( "" ) && !parm.player22.equalsIgnoreCase( "x" ) && parm.g[21].equals( "" ) && hit == 0) {

         player = parm.player22;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player23.equals( "" ) && !parm.player23.equalsIgnoreCase( "x" ) && parm.g[22].equals( "" ) && hit == 0) {

         player = parm.player23;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player24.equals( "" ) && !parm.player24.equalsIgnoreCase( "x" ) && parm.g[23].equals( "" ) && hit == 0) {

         player = parm.player24;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player25.equals( "" ) && !parm.player25.equalsIgnoreCase( "x" ) && parm.g[24].equals( "" ) && hit == 0) {

         player = parm.player25;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (hit > 0) {          // if we hit on a duplicate res

         out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><BR><H3>Member Already Playing</H3><BR>");
         out.println("<BR>Sorry, <b>" + player + "</b> is already scheduled to play on this date.<br><br>");
         if (hit == 1) {
            out.println("The player is already scheduled the maximum number of times allowed per day.<br><br>");
         } else {
            if (hit == 2) {
               out.println("The player has another tee time that is too close to the time requested.<br><br>");
            } else {
               out.println("The player has another request that is too close to the time of this request.<br><br>");
            }
         }
         //
         //  Return to _lott
         //
         skip = "skip7";
         goReturn(out, parm, overrideAccess, "", skip);
         return;
      }
   }         // end of IF skip7

   //
   //  Check for minimum number of members & players in request
   //
   //  If this skip is set, then we've already been through these tests.
   //
   if ((req.getParameter("skip8") == null) && (req.getParameter("skip9") == null) && (req.getParameter("skip10") == null)) {

      hit2 = chkminPlayer(req, con, out, parm, lottName);

      if (hit2 == true) {          // if we hit on an error

         return;
      }
   }         // end of IF skip8

   //
   //  Check if user has approved of the member/guest sequence (guest association)
   //
   //  If this skip is set, then we've already been through these tests.
   //
   if ((req.getParameter("skip9") == null) && (req.getParameter("skip10") == null)) {

      //
      //***********************************************************************************************
      //
      //    Now check the order of guests and members (guests must follow a member) - prompt to verify order
      //
      //***********************************************************************************************
      //
      for (i = 0; i < 25; i++) {           // init the arrays

         userg[i] = "";
         usergA[i] = "";
         parm.userg[i] = "";
      }

      if (guests != 0 && members != 0) {      // if both guests and members were included

         if (members > 0) {                  // if at least one member in request

            //
            //  Both guests and members specified - determine guest owners by order
            //
            memberName = "";

            for (gi = 0; gi < 25; gi++) {            // cycle thru arrays and find guests/members

               if (!gstA[gi].equals( "" )) {

                  usergA[gi] = memberName;       // get last players username
               } else {
                  usergA[gi] = "";               // init array entry
                  memberName = memA[gi];        // get players username
               }
            }
              
            for (i = 0; i < 25; i++) {
               userg[i] = usergA[i];        // set usernames for guests in teecurr
               parm.userg[i] = userg[i];    // copy to parms
            }
         }

         if (!parm.g[0].equals( "" ) || members > 1) {     // if slot 1 is a guest OR more than 1 member

            //
            //  At least one guest and one member have been specified.
            //  Prompt user to verify the order.
            //
            out.println(SystemUtils.HeadTitle("Guests Specified - Prompt"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Player/Guest Association Prompt</H3><BR>");
            out.println("Guests must be specified <b>immediately after</b> the member they belong to.<br><br>");

            if (!parm.g[0].equals( "" )) {              // if slot 1 is not a guest

               out.println("You cannot have a guest in the first player position when one or more members are also specified.");
               out.println("<BR><BR>");
            } else {
               out.println("Please verify that the following order is correct:");
               out.println("<BR><BR>");
               out.println(parm.player1 + " <BR>");
               out.println(parm.player2 + " <BR>");
               if (!parm.player3.equals( "" )) {
                  out.println(parm.player3 + " <BR>");
               }
               if (!parm.player4.equals( "" )) {
                  out.println(parm.player4 + " <BR>");
               }
               if (!parm.player5.equals( "" )) {
                  out.println(parm.player5 + " <BR>");
               }
               if (!parm.player6.equals( "" )) {
                  out.println(parm.player6 + " <BR>");
               }
               if (!parm.player7.equals( "" )) {
                  out.println(parm.player7 + " <BR>");
               }
               if (!parm.player8.equals( "" )) {
                  out.println(parm.player8 + " <BR>");
               }
               if (!parm.player9.equals( "" )) {
                  out.println(parm.player9 + " <BR>");
               }
               if (!parm.player10.equals( "" )) {
                  out.println(parm.player10 + " <BR>");
               }
               if (!parm.player11.equals( "" )) {
                  out.println(parm.player11 + " <BR>");
               }
               if (!parm.player12.equals( "" )) {
                  out.println(parm.player12 + " <BR>");
               }
               if (!parm.player13.equals( "" )) {
                  out.println(parm.player13 + " <BR>");
               }
               if (!parm.player14.equals( "" )) {
                  out.println(parm.player14 + " <BR>");
               }
               if (!parm.player15.equals( "" )) {
                  out.println(parm.player15 + " <BR>");
               }
               if (!parm.player16.equals( "" )) {
                  out.println(parm.player16 + " <BR>");
               }
               if (!parm.player17.equals( "" )) {
                  out.println(parm.player17 + " <BR>");
               }
               if (!parm.player18.equals( "" )) {
                  out.println(parm.player18 + " <BR>");
               }
               if (!parm.player19.equals( "" )) {
                  out.println(parm.player19 + " <BR>");
               }
               if (!parm.player20.equals( "" )) {
                  out.println(parm.player20 + " <BR>");
               }
               if (!parm.player21.equals( "" )) {
                  out.println(parm.player21 + " <BR>");
               }
               if (!parm.player22.equals( "" )) {
                  out.println(parm.player22 + " <BR>");
               }
               if (!parm.player23.equals( "" )) {
                  out.println(parm.player23 + " <BR>");
               }
               if (!parm.player24.equals( "" )) {
                  out.println(parm.player24 + " <BR>");
               }
               if (!parm.player25.equals( "" )) {
                  out.println(parm.player25 + " <BR>");
               }

               out.println("<BR>Would you like to process the request as is?");
            }

            //
            //  Return to _lott to change the player order
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"Proshop_lott\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + day + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
            out.println("<input type=\"hidden\" name=\"index2\" value=\"" + index2 + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
            out.println("<input type=\"hidden\" name=\"p5\" value=\"" + p5 + "\">");
            out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + p5rest + "\">");
            out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
            out.println("<input type=\"hidden\" name=\"player1\" value=\"" + parm.player1 + "\">");
            out.println("<input type=\"hidden\" name=\"player2\" value=\"" + parm.player2 + "\">");
            out.println("<input type=\"hidden\" name=\"player3\" value=\"" + parm.player3 + "\">");
            out.println("<input type=\"hidden\" name=\"player4\" value=\"" + parm.player4 + "\">");
            out.println("<input type=\"hidden\" name=\"player5\" value=\"" + parm.player5 + "\">");
            out.println("<input type=\"hidden\" name=\"player6\" value=\"" + parm.player6 + "\">");
            out.println("<input type=\"hidden\" name=\"player7\" value=\"" + parm.player7 + "\">");
            out.println("<input type=\"hidden\" name=\"player8\" value=\"" + parm.player8 + "\">");
            out.println("<input type=\"hidden\" name=\"player9\" value=\"" + parm.player9 + "\">");
            out.println("<input type=\"hidden\" name=\"player10\" value=\"" + parm.player10 + "\">");
            out.println("<input type=\"hidden\" name=\"player11\" value=\"" + parm.player11 + "\">");
            out.println("<input type=\"hidden\" name=\"player12\" value=\"" + parm.player12 + "\">");
            out.println("<input type=\"hidden\" name=\"player13\" value=\"" + parm.player13 + "\">");
            out.println("<input type=\"hidden\" name=\"player14\" value=\"" + parm.player14 + "\">");
            out.println("<input type=\"hidden\" name=\"player15\" value=\"" + parm.player15 + "\">");
            out.println("<input type=\"hidden\" name=\"player16\" value=\"" + parm.player16 + "\">");
            out.println("<input type=\"hidden\" name=\"player17\" value=\"" + parm.player17 + "\">");
            out.println("<input type=\"hidden\" name=\"player18\" value=\"" + parm.player18 + "\">");
            out.println("<input type=\"hidden\" name=\"player19\" value=\"" + parm.player19 + "\">");
            out.println("<input type=\"hidden\" name=\"player20\" value=\"" + parm.player20 + "\">");
            out.println("<input type=\"hidden\" name=\"player21\" value=\"" + parm.player21 + "\">");
            out.println("<input type=\"hidden\" name=\"player22\" value=\"" + parm.player22 + "\">");
            out.println("<input type=\"hidden\" name=\"player23\" value=\"" + parm.player23 + "\">");
            out.println("<input type=\"hidden\" name=\"player24\" value=\"" + parm.player24 + "\">");
            out.println("<input type=\"hidden\" name=\"player25\" value=\"" + parm.player25 + "\">");
            out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + parm.pcw1 + "\">");
            out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + parm.pcw2 + "\">");
            out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + parm.pcw3 + "\">");
            out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + parm.pcw4 + "\">");
            out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + parm.pcw5 + "\">");
            out.println("<input type=\"hidden\" name=\"p6cw\" value=\"" + parm.pcw6 + "\">");
            out.println("<input type=\"hidden\" name=\"p7cw\" value=\"" + parm.pcw7 + "\">");
            out.println("<input type=\"hidden\" name=\"p8cw\" value=\"" + parm.pcw8 + "\">");
            out.println("<input type=\"hidden\" name=\"p9cw\" value=\"" + parm.pcw9 + "\">");
            out.println("<input type=\"hidden\" name=\"p10cw\" value=\"" + parm.pcw10 + "\">");
            out.println("<input type=\"hidden\" name=\"p11cw\" value=\"" + parm.pcw11 + "\">");
            out.println("<input type=\"hidden\" name=\"p12cw\" value=\"" + parm.pcw12 + "\">");
            out.println("<input type=\"hidden\" name=\"p13cw\" value=\"" + parm.pcw13 + "\">");
            out.println("<input type=\"hidden\" name=\"p14cw\" value=\"" + parm.pcw14 + "\">");
            out.println("<input type=\"hidden\" name=\"p15cw\" value=\"" + parm.pcw15 + "\">");
            out.println("<input type=\"hidden\" name=\"p16cw\" value=\"" + parm.pcw16 + "\">");
            out.println("<input type=\"hidden\" name=\"p17cw\" value=\"" + parm.pcw17 + "\">");
            out.println("<input type=\"hidden\" name=\"p18cw\" value=\"" + parm.pcw18 + "\">");
            out.println("<input type=\"hidden\" name=\"p19cw\" value=\"" + parm.pcw19 + "\">");
            out.println("<input type=\"hidden\" name=\"p20cw\" value=\"" + parm.pcw20 + "\">");
            out.println("<input type=\"hidden\" name=\"p21cw\" value=\"" + parm.pcw21 + "\">");
            out.println("<input type=\"hidden\" name=\"p22cw\" value=\"" + parm.pcw22 + "\">");
            out.println("<input type=\"hidden\" name=\"p23cw\" value=\"" + parm.pcw23 + "\">");
            out.println("<input type=\"hidden\" name=\"p24cw\" value=\"" + parm.pcw24 + "\">");
            out.println("<input type=\"hidden\" name=\"p25cw\" value=\"" + parm.pcw25 + "\">");
            out.println("<input type=\"hidden\" name=\"p91\" value=\"" + parm.p91 + "\">");
            out.println("<input type=\"hidden\" name=\"p92\" value=\"" + parm.p92 + "\">");
            out.println("<input type=\"hidden\" name=\"p93\" value=\"" + parm.p93 + "\">");
            out.println("<input type=\"hidden\" name=\"p94\" value=\"" + parm.p94 + "\">");
            out.println("<input type=\"hidden\" name=\"p95\" value=\"" + parm.p95 + "\">");
            out.println("<input type=\"hidden\" name=\"p96\" value=\"" + parm.p96 + "\">");
            out.println("<input type=\"hidden\" name=\"p97\" value=\"" + parm.p97 + "\">");
            out.println("<input type=\"hidden\" name=\"p98\" value=\"" + parm.p98 + "\">");
            out.println("<input type=\"hidden\" name=\"p99\" value=\"" + parm.p99 + "\">");
            out.println("<input type=\"hidden\" name=\"p910\" value=\"" + parm.p910 + "\">");
            out.println("<input type=\"hidden\" name=\"p911\" value=\"" + parm.p911 + "\">");
            out.println("<input type=\"hidden\" name=\"p912\" value=\"" + parm.p912 + "\">");
            out.println("<input type=\"hidden\" name=\"p913\" value=\"" + parm.p913 + "\">");
            out.println("<input type=\"hidden\" name=\"p914\" value=\"" + parm.p914 + "\">");
            out.println("<input type=\"hidden\" name=\"p915\" value=\"" + parm.p915 + "\">");
            out.println("<input type=\"hidden\" name=\"p916\" value=\"" + parm.p916 + "\">");
            out.println("<input type=\"hidden\" name=\"p917\" value=\"" + parm.p917 + "\">");
            out.println("<input type=\"hidden\" name=\"p918\" value=\"" + parm.p918 + "\">");
            out.println("<input type=\"hidden\" name=\"p919\" value=\"" + parm.p919 + "\">");
            out.println("<input type=\"hidden\" name=\"p920\" value=\"" + parm.p920 + "\">");
            out.println("<input type=\"hidden\" name=\"p921\" value=\"" + parm.p921 + "\">");
            out.println("<input type=\"hidden\" name=\"p922\" value=\"" + parm.p922 + "\">");
            out.println("<input type=\"hidden\" name=\"p923\" value=\"" + parm.p923 + "\">");
            out.println("<input type=\"hidden\" name=\"p924\" value=\"" + parm.p924 + "\">");
            out.println("<input type=\"hidden\" name=\"p925\" value=\"" + parm.p925 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + parm.guest_id1 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + parm.guest_id2 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + parm.guest_id3 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + parm.guest_id4 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + parm.guest_id5 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id6\" value=\"" + parm.guest_id6 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id7\" value=\"" + parm.guest_id7 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id8\" value=\"" + parm.guest_id8 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id9\" value=\"" + parm.guest_id9 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id10\" value=\"" + parm.guest_id10 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id11\" value=\"" + parm.guest_id11 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id12\" value=\"" + parm.guest_id12 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id13\" value=\"" + parm.guest_id13 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id14\" value=\"" + parm.guest_id14 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id15\" value=\"" + parm.guest_id15 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id16\" value=\"" + parm.guest_id16 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id17\" value=\"" + parm.guest_id17 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id18\" value=\"" + parm.guest_id18 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id19\" value=\"" + parm.guest_id19 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id20\" value=\"" + parm.guest_id20 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id21\" value=\"" + parm.guest_id21 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id22\" value=\"" + parm.guest_id22 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id23\" value=\"" + parm.guest_id23 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id24\" value=\"" + parm.guest_id24 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id25\" value=\"" + parm.guest_id25 + "\">");
            out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
            if (parm.hide > 0) {
               out.println("<input type=\"hidden\" name=\"hide\" value=\"1\">");
            }
            out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lottName + "\">");
            out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate + "\">");
            out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + lottid + "\">");
            out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots + "\">");
            out.println("<input type=\"hidden\" name=\"mins_before\" value=\"" + mins_before + "\">");
            out.println("<input type=\"hidden\" name=\"mins_after\" value=\"" + mins_after + "\">");
            out.println("<input type=\"hidden\" name=\"checkothers\" value=\"" + parm.checkothers + "\">");

            if (parm.isRecurr == true) {       // only include the recurrence parms if requested
               out.println("<input type=\"hidden\" name=\"isrecurr\" value=\"yes\">");
            }

            if (!parm.g[0].equals( "" )) {              // if slot 1 is a guest

               out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</form></font>");

            } else {

               out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</form></font>");

               //
               //  Return to process the players as they are
               //
               out.println("<font size=\"2\">");
               out.println("<form action=\"Proshop_lott\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"skip9\" value=\"yes\">");
               out.println("<input type=\"hidden\" name=\"player1\" value=\"" + parm.player1 + "\">");
               out.println("<input type=\"hidden\" name=\"player2\" value=\"" + parm.player2 + "\">");
               out.println("<input type=\"hidden\" name=\"player3\" value=\"" + parm.player3 + "\">");
               out.println("<input type=\"hidden\" name=\"player4\" value=\"" + parm.player4 + "\">");
               out.println("<input type=\"hidden\" name=\"player5\" value=\"" + parm.player5 + "\">");
               out.println("<input type=\"hidden\" name=\"player6\" value=\"" + parm.player6 + "\">");
               out.println("<input type=\"hidden\" name=\"player7\" value=\"" + parm.player7 + "\">");
               out.println("<input type=\"hidden\" name=\"player8\" value=\"" + parm.player8 + "\">");
               out.println("<input type=\"hidden\" name=\"player9\" value=\"" + parm.player9 + "\">");
               out.println("<input type=\"hidden\" name=\"player10\" value=\"" + parm.player10 + "\">");
               out.println("<input type=\"hidden\" name=\"player11\" value=\"" + parm.player11 + "\">");
               out.println("<input type=\"hidden\" name=\"player12\" value=\"" + parm.player12 + "\">");
               out.println("<input type=\"hidden\" name=\"player13\" value=\"" + parm.player13 + "\">");
               out.println("<input type=\"hidden\" name=\"player14\" value=\"" + parm.player14 + "\">");
               out.println("<input type=\"hidden\" name=\"player15\" value=\"" + parm.player15 + "\">");
               out.println("<input type=\"hidden\" name=\"player16\" value=\"" + parm.player16 + "\">");
               out.println("<input type=\"hidden\" name=\"player17\" value=\"" + parm.player17 + "\">");
               out.println("<input type=\"hidden\" name=\"player18\" value=\"" + parm.player18 + "\">");
               out.println("<input type=\"hidden\" name=\"player19\" value=\"" + parm.player19 + "\">");
               out.println("<input type=\"hidden\" name=\"player20\" value=\"" + parm.player20 + "\">");
               out.println("<input type=\"hidden\" name=\"player21\" value=\"" + parm.player21 + "\">");
               out.println("<input type=\"hidden\" name=\"player22\" value=\"" + parm.player22 + "\">");
               out.println("<input type=\"hidden\" name=\"player23\" value=\"" + parm.player23 + "\">");
               out.println("<input type=\"hidden\" name=\"player24\" value=\"" + parm.player24 + "\">");
               out.println("<input type=\"hidden\" name=\"player25\" value=\"" + parm.player25 + "\">");
               out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + parm.pcw1 + "\">");
               out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + parm.pcw2 + "\">");
               out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + parm.pcw3 + "\">");
               out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + parm.pcw4 + "\">");
               out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + parm.pcw5 + "\">");
               out.println("<input type=\"hidden\" name=\"p6cw\" value=\"" + parm.pcw6 + "\">");
               out.println("<input type=\"hidden\" name=\"p7cw\" value=\"" + parm.pcw7 + "\">");
               out.println("<input type=\"hidden\" name=\"p8cw\" value=\"" + parm.pcw8 + "\">");
               out.println("<input type=\"hidden\" name=\"p9cw\" value=\"" + parm.pcw9 + "\">");
               out.println("<input type=\"hidden\" name=\"p10cw\" value=\"" + parm.pcw10 + "\">");
               out.println("<input type=\"hidden\" name=\"p11cw\" value=\"" + parm.pcw11 + "\">");
               out.println("<input type=\"hidden\" name=\"p12cw\" value=\"" + parm.pcw12 + "\">");
               out.println("<input type=\"hidden\" name=\"p13cw\" value=\"" + parm.pcw13 + "\">");
               out.println("<input type=\"hidden\" name=\"p14cw\" value=\"" + parm.pcw14 + "\">");
               out.println("<input type=\"hidden\" name=\"p15cw\" value=\"" + parm.pcw15 + "\">");
               out.println("<input type=\"hidden\" name=\"p16cw\" value=\"" + parm.pcw16 + "\">");
               out.println("<input type=\"hidden\" name=\"p17cw\" value=\"" + parm.pcw17 + "\">");
               out.println("<input type=\"hidden\" name=\"p18cw\" value=\"" + parm.pcw18 + "\">");
               out.println("<input type=\"hidden\" name=\"p19cw\" value=\"" + parm.pcw19 + "\">");
               out.println("<input type=\"hidden\" name=\"p20cw\" value=\"" + parm.pcw20 + "\">");
               out.println("<input type=\"hidden\" name=\"p21cw\" value=\"" + parm.pcw21 + "\">");
               out.println("<input type=\"hidden\" name=\"p22cw\" value=\"" + parm.pcw22 + "\">");
               out.println("<input type=\"hidden\" name=\"p23cw\" value=\"" + parm.pcw23 + "\">");
               out.println("<input type=\"hidden\" name=\"p24cw\" value=\"" + parm.pcw24 + "\">");
               out.println("<input type=\"hidden\" name=\"p25cw\" value=\"" + parm.pcw25 + "\">");
               out.println("<input type=\"hidden\" name=\"p91\" value=\"" + parm.p91 + "\">");
               out.println("<input type=\"hidden\" name=\"p92\" value=\"" + parm.p92 + "\">");
               out.println("<input type=\"hidden\" name=\"p93\" value=\"" + parm.p93 + "\">");
               out.println("<input type=\"hidden\" name=\"p94\" value=\"" + parm.p94 + "\">");
               out.println("<input type=\"hidden\" name=\"p95\" value=\"" + parm.p95 + "\">");
               out.println("<input type=\"hidden\" name=\"p96\" value=\"" + parm.p96 + "\">");
               out.println("<input type=\"hidden\" name=\"p97\" value=\"" + parm.p97 + "\">");
               out.println("<input type=\"hidden\" name=\"p98\" value=\"" + parm.p98 + "\">");
               out.println("<input type=\"hidden\" name=\"p99\" value=\"" + parm.p99 + "\">");
               out.println("<input type=\"hidden\" name=\"p910\" value=\"" + parm.p910 + "\">");
               out.println("<input type=\"hidden\" name=\"p911\" value=\"" + parm.p911 + "\">");
               out.println("<input type=\"hidden\" name=\"p912\" value=\"" + parm.p912 + "\">");
               out.println("<input type=\"hidden\" name=\"p913\" value=\"" + parm.p913 + "\">");
               out.println("<input type=\"hidden\" name=\"p914\" value=\"" + parm.p914 + "\">");
               out.println("<input type=\"hidden\" name=\"p915\" value=\"" + parm.p915 + "\">");
               out.println("<input type=\"hidden\" name=\"p916\" value=\"" + parm.p916 + "\">");
               out.println("<input type=\"hidden\" name=\"p917\" value=\"" + parm.p917 + "\">");
               out.println("<input type=\"hidden\" name=\"p918\" value=\"" + parm.p918 + "\">");
               out.println("<input type=\"hidden\" name=\"p919\" value=\"" + parm.p919 + "\">");
               out.println("<input type=\"hidden\" name=\"p920\" value=\"" + parm.p920 + "\">");
               out.println("<input type=\"hidden\" name=\"p921\" value=\"" + parm.p921 + "\">");
               out.println("<input type=\"hidden\" name=\"p922\" value=\"" + parm.p922 + "\">");
               out.println("<input type=\"hidden\" name=\"p923\" value=\"" + parm.p923 + "\">");
               out.println("<input type=\"hidden\" name=\"p924\" value=\"" + parm.p924 + "\">");
               out.println("<input type=\"hidden\" name=\"p925\" value=\"" + parm.p925 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + parm.guest_id1 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + parm.guest_id2 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + parm.guest_id3 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + parm.guest_id4 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + parm.guest_id5 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id6\" value=\"" + parm.guest_id6 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id7\" value=\"" + parm.guest_id7 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id8\" value=\"" + parm.guest_id8 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id9\" value=\"" + parm.guest_id9 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id10\" value=\"" + parm.guest_id10 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id11\" value=\"" + parm.guest_id11 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id12\" value=\"" + parm.guest_id12 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id13\" value=\"" + parm.guest_id13 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id14\" value=\"" + parm.guest_id14 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id15\" value=\"" + parm.guest_id15 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id16\" value=\"" + parm.guest_id16 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id17\" value=\"" + parm.guest_id17 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id18\" value=\"" + parm.guest_id18 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id19\" value=\"" + parm.guest_id19 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id20\" value=\"" + parm.guest_id20 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id21\" value=\"" + parm.guest_id21 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id22\" value=\"" + parm.guest_id22 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id23\" value=\"" + parm.guest_id23 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id24\" value=\"" + parm.guest_id24 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id25\" value=\"" + parm.guest_id25 + "\">");
               out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
               out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
               out.println("<input type=\"hidden\" name=\"mm\" value=\"" + mm + "\">");
               out.println("<input type=\"hidden\" name=\"yy\" value=\"" + yy + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
               out.println("<input type=\"hidden\" name=\"index2\" value=\"" + index2 + "\">");
               out.println("<input type=\"hidden\" name=\"p5\" value=\"" + p5 + "\">");
               out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + p5rest + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + day + "\">");
               out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
               out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
               //out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hides + "\">");
               if (parm.hide > 0) {
                  out.println("<input type=\"hidden\" name=\"hide\" value=\"1\">");
               }
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
               out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lottName + "\">");
               out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate + "\">");
               out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + lottid + "\">");
               out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots + "\">");
               out.println("<input type=\"hidden\" name=\"mins_before\" value=\"" + mins_before + "\">");
               out.println("<input type=\"hidden\" name=\"mins_after\" value=\"" + mins_after + "\">");
               out.println("<input type=\"hidden\" name=\"checkothers\" value=\"" + parm.checkothers + "\">");
               
               if (parm.isRecurr == true) {       // only include the recurrence parms if requested
                  out.println("<input type=\"hidden\" name=\"isrecurr\" value=\"yes\">");
               }

               for (i = 0; i < 25; i++) {
                  out.println("<input type=\"hidden\" name=\"userg" + i + "\" value=\"" + userg[i] + "\">");
               }
               out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submitForm\">");
               out.println("</form></font>");
            }
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
         }

      }      // end of IF any guests specified

   } else {   // skip 9 or skip 10 requested
      //
      //  User has responded to the guest association prompt - process tee time request in specified order
      //
      for (i = 0; i < 25; i++) {
         userg[i] = req.getParameter("userg" + i);
         parm.userg[i] = userg[i];                   // save in parms
      }
   }         // end of IF skip9


   //
   //  If this skip is set, then we've already been through these tests.
   //
   if (req.getParameter("skip10") == null) {

      //
      //***********************************************************************************************
      //
      //  Now that the guests are assigned, check for any Guest Quotas - if any guests requested
      //
      //***********************************************************************************************
      //
      boolean guest_ass = false;

      //  Any guests assigned?
      for (i = 0; i < 25; i++) {
         if (!userg[i].equals( "" )) {
            guest_ass = true;
         }
      }

      if (guest_ass == true) {

         check = Common_Lott.checkGuestQuota(parm, con);      // go check
      }

      if (check == true) {          // if we hit on a violation

         out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><BR><H3>Guest Quota Exceeded for Member</H3><BR>");
         out.println("<BR>Sorry, requesting <b>" + parm.player + "</b> exceeds the guest quota established for this guest type.");
         out.println("<br><br>You will have to remove the guest in order to complete this request.");

         //
         //  Return to _lott
         //
         skip = "skip10";
         goReturn(out, parm, overrideAccess, "", skip);
      }
   }


   //
   //  Verification complete -
   //   Enter request and Send email notifications of request
   //
   sendEmail = 0;         // init email flags
   emailNew = 0;
   emailMod = 0;

   //
   //  If any player has changed, then set email flag
   //
   if ((!parm.player1.equals( parm.oldplayer1 )) || (!parm.player2.equals( parm.oldplayer2 )) || (!parm.player3.equals( parm.oldplayer3 )) ||
       (!parm.player4.equals( parm.oldplayer4 )) || (!parm.player5.equals( parm.oldplayer5 )) || (!parm.player6.equals( parm.oldplayer6 )) ||
       (!parm.player7.equals( parm.oldplayer7 )) || (!parm.player8.equals( parm.oldplayer8 )) || (!parm.player9.equals( parm.oldplayer9 )) ||
       (!parm.player10.equals( parm.oldplayer10 )) || (!parm.player11.equals( parm.oldplayer11 )) || (!parm.player12.equals( parm.oldplayer12 )) ||
       (!parm.player13.equals( parm.oldplayer13 )) || (!parm.player14.equals( parm.oldplayer14 )) || (!parm.player15.equals( parm.oldplayer15 )) ||
       (!parm.player16.equals( parm.oldplayer16 )) || (!parm.player17.equals( parm.oldplayer17 )) || (!parm.player18.equals( parm.oldplayer18 )) ||
       (!parm.player19.equals( parm.oldplayer19 )) || (!parm.player20.equals( parm.oldplayer20 )) || (!parm.player21.equals( parm.oldplayer21 )) ||
       (!parm.player22.equals( parm.oldplayer22 )) || (!parm.player23.equals( parm.oldplayer23 )) || (!parm.player24.equals( parm.oldplayer24 )) ||
       (!parm.player25.equals( parm.oldplayer25 ))) {

      sendEmail = 1;    // player changed - send email notification to all
   }

   //
   //  get time values
   //
   hr = time / 100;            // get hour value
   min = time - (hr * 100);    // get minute value

   //    
   //  Check if new request or update
   //
   if (lottid != 0) {

      proMod++;      // increment number of mods
      emailMod = 1;  // tee time was modified

      updateReq = true;   // indicate that this was a request to update the lottery req

      //
      //   Update the Lottery Request
      //
      try {
         PreparedStatement pstmt6 = con.prepareStatement (
            "UPDATE lreqs3 SET hr = ?, min = ?, time = ?, minsbefore = ?, minsafter = ?, " +
            "player1 = ?, player2 = ?, player3 = ?, player4 = ?, player5 = ?, " +
            "player6 = ?, player7 = ?, player8 = ?, player9 = ?, player10 = ?, " +
            "player11 = ?, player12 = ?, player13 = ?, player14 = ?, player15 = ?, " +
            "player16 = ?, player17 = ?, player18 = ?, player19 = ?, player20 = ?, " +
            "player21 = ?, player22 = ?, player23 = ?, player24 = ?, player25 = ?, " +
            "user1 = ?, user2 = ?, user3 = ?, user4 = ?, user5 = ?, " +
            "user6 = ?, user7 = ?, user8 = ?, user9 = ?, user10 = ?, " +
            "user11 = ?, user12 = ?, user13 = ?, user14 = ?, user15 = ?, " +
            "user16 = ?, user17 = ?, user18 = ?, user19 = ?, user20 = ?, " +
            "user21 = ?, user22 = ?, user23 = ?, user24 = ?, user25 = ?, " +
            "p1cw = ?, p2cw = ?, p3cw = ?, p4cw = ?, p5cw = ?, " +
            "p6cw = ?, p7cw = ?, p8cw = ?, p9cw = ?, p10cw = ?, " +
            "p11cw = ?, p12cw = ?, p13cw = ?, p14cw = ?, p15cw = ?, " +
            "p16cw = ?, p17cw = ?, p18cw = ?, p19cw = ?, p20cw = ?, " +
            "p21cw = ?, p22cw = ?, p23cw = ?, p24cw = ?, p25cw = ?, " +
            "notes = ?, hideNotes = ?, fb = ?, courseName = ?, proMod = ?, in_use = 0, groups = ?, p5 = ?, " +
            "players = ?, userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, userg6 = ?, " +
            "userg7 = ?, userg8 = ?, userg9 = ?, userg10 = ?, userg11 = ?, userg12 = ?, userg13 = ?, " +
            "userg14 = ?, userg15 = ?, userg16 = ?, userg17 = ?, userg18 = ?, userg19 = ?, userg20 = ?, " +
            "userg21 = ?, userg22 = ?, userg23 = ?, userg24 = ?, userg25 = ?, orig_by = ?, " +
            "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, p96 = ?, " +
            "p97 = ?, p98 = ?, p99 = ?, p910 = ?, p911 = ?, p912 = ?, p913 = ?, " +
            "p914 = ?, p915 = ?, p916 = ?, p917 = ?, p918 = ?, p919 = ?, p920 = ?, " +
            "p921 = ?, p922 = ?, p923 = ?, p924 = ?, p925 = ?, checkothers = ?, courseName = ?, " +
            "guest_id1 = ?, guest_id2 = ?, guest_id3 = ?, guest_id4 = ?, guest_id5 = ?, " +
            "guest_id6 = ?, guest_id7 = ?, guest_id8 = ?, guest_id9 = ?, guest_id10 = ?, " +
            "guest_id11 = ?, guest_id12 = ?, guest_id13 = ?, guest_id14 = ?, guest_id15 = ?, " +
            "guest_id16 = ?, guest_id17 = ?, guest_id18 = ?, guest_id19 = ?, guest_id20 = ?, " +
            "guest_id21 = ?, guest_id22 = ?, guest_id23 = ?, guest_id24 = ?, guest_id25 = ? " +
            "WHERE id = ?");

         pstmt6.clearParameters();        // clear the parms
         pstmt6.setInt(1, hr);
         pstmt6.setInt(2, min);
         pstmt6.setInt(3, time);
         pstmt6.setInt(4, mins_before);
         pstmt6.setInt(5, mins_after);
         pstmt6.setString(6, parm.player1);
         pstmt6.setString(7, parm.player2);
         pstmt6.setString(8, parm.player3);
         pstmt6.setString(9, parm.player4);
         pstmt6.setString(10, parm.player5);
         pstmt6.setString(11, parm.player6);
         pstmt6.setString(12, parm.player7);
         pstmt6.setString(13, parm.player8);
         pstmt6.setString(14, parm.player9);
         pstmt6.setString(15, parm.player10);
         pstmt6.setString(16, parm.player11);
         pstmt6.setString(17, parm.player12);
         pstmt6.setString(18, parm.player13);
         pstmt6.setString(19, parm.player14);
         pstmt6.setString(20, parm.player15);
         pstmt6.setString(21, parm.player16);
         pstmt6.setString(22, parm.player17);
         pstmt6.setString(23, parm.player18);
         pstmt6.setString(24, parm.player19);
         pstmt6.setString(25, parm.player20);
         pstmt6.setString(26, parm.player21);
         pstmt6.setString(27, parm.player22);
         pstmt6.setString(28, parm.player23);
         pstmt6.setString(29, parm.player24);
         pstmt6.setString(30, parm.player25);
         pstmt6.setString(31, parm.user1);
         pstmt6.setString(32, parm.user2);
         pstmt6.setString(33, parm.user3);
         pstmt6.setString(34, parm.user4);
         pstmt6.setString(35, parm.user5);
         pstmt6.setString(36, parm.user6);
         pstmt6.setString(37, parm.user7);
         pstmt6.setString(38, parm.user8);
         pstmt6.setString(39, parm.user9);
         pstmt6.setString(40, parm.user10);
         pstmt6.setString(41, parm.user11);
         pstmt6.setString(42, parm.user12);
         pstmt6.setString(43, parm.user13);
         pstmt6.setString(44, parm.user14);
         pstmt6.setString(45, parm.user15);
         pstmt6.setString(46, parm.user16);
         pstmt6.setString(47, parm.user17);
         pstmt6.setString(48, parm.user18);
         pstmt6.setString(49, parm.user19);
         pstmt6.setString(50, parm.user20);
         pstmt6.setString(51, parm.user21);
         pstmt6.setString(52, parm.user22);
         pstmt6.setString(53, parm.user23);
         pstmt6.setString(54, parm.user24);
         pstmt6.setString(55, parm.user25);
         pstmt6.setString(56, parm.pcw1);
         pstmt6.setString(57, parm.pcw2);
         pstmt6.setString(58, parm.pcw3);
         pstmt6.setString(59, parm.pcw4);
         pstmt6.setString(60, parm.pcw5);
         pstmt6.setString(61, parm.pcw6);
         pstmt6.setString(62, parm.pcw7);
         pstmt6.setString(63, parm.pcw8);
         pstmt6.setString(64, parm.pcw9);
         pstmt6.setString(65, parm.pcw10);
         pstmt6.setString(66, parm.pcw11);
         pstmt6.setString(67, parm.pcw12);
         pstmt6.setString(68, parm.pcw13);
         pstmt6.setString(69, parm.pcw14);
         pstmt6.setString(70, parm.pcw15);
         pstmt6.setString(71, parm.pcw16);
         pstmt6.setString(72, parm.pcw17);
         pstmt6.setString(73, parm.pcw18);
         pstmt6.setString(74, parm.pcw19);
         pstmt6.setString(75, parm.pcw20);
         pstmt6.setString(76, parm.pcw21);
         pstmt6.setString(77, parm.pcw22);
         pstmt6.setString(78, parm.pcw23);
         pstmt6.setString(79, parm.pcw24);
         pstmt6.setString(80, parm.pcw25);
         pstmt6.setString(81, notes);
         pstmt6.setInt(82, hide);
         pstmt6.setInt(83, fb);
         pstmt6.setString(84, course);
         pstmt6.setInt(85, proMod);
         pstmt6.setInt(86, slots);
         pstmt6.setString(87, p5);
         pstmt6.setInt(88, players);
         pstmt6.setString(89, userg[0]);
         pstmt6.setString(90, userg[1]);
         pstmt6.setString(91, userg[2]);
         pstmt6.setString(92, userg[3]);
         pstmt6.setString(93, userg[4]);
         pstmt6.setString(94, userg[5]);
         pstmt6.setString(95, userg[6]);
         pstmt6.setString(96, userg[7]);
         pstmt6.setString(97, userg[8]);
         pstmt6.setString(98, userg[9]);
         pstmt6.setString(99, userg[10]);
         pstmt6.setString(100, userg[11]);
         pstmt6.setString(101, userg[12]);
         pstmt6.setString(102, userg[13]);
         pstmt6.setString(103, userg[14]);
         pstmt6.setString(104, userg[15]);
         pstmt6.setString(105, userg[16]);
         pstmt6.setString(106, userg[17]);
         pstmt6.setString(107, userg[18]);
         pstmt6.setString(108, userg[19]);
         pstmt6.setString(109, userg[20]);
         pstmt6.setString(110, userg[21]);
         pstmt6.setString(111, userg[22]);
         pstmt6.setString(112, userg[23]);
         pstmt6.setString(113, userg[24]);
         pstmt6.setString(114, orig_by);
         pstmt6.setInt(115, parm.p91);
         pstmt6.setInt(116, parm.p92);
         pstmt6.setInt(117, parm.p93);
         pstmt6.setInt(118, parm.p94);
         pstmt6.setInt(119, parm.p95);
         pstmt6.setInt(120, parm.p96);
         pstmt6.setInt(121, parm.p97);
         pstmt6.setInt(122, parm.p98);
         pstmt6.setInt(123, parm.p99);
         pstmt6.setInt(124, parm.p910);
         pstmt6.setInt(125, parm.p911);
         pstmt6.setInt(126, parm.p912);
         pstmt6.setInt(127, parm.p913);
         pstmt6.setInt(128, parm.p914);
         pstmt6.setInt(129, parm.p915);
         pstmt6.setInt(130, parm.p916);
         pstmt6.setInt(131, parm.p917);
         pstmt6.setInt(132, parm.p918);
         pstmt6.setInt(133, parm.p919);
         pstmt6.setInt(134, parm.p920);
         pstmt6.setInt(135, parm.p921);
         pstmt6.setInt(136, parm.p922);
         pstmt6.setInt(137, parm.p923);
         pstmt6.setInt(138, parm.p924);
         pstmt6.setInt(139, parm.p925);
         pstmt6.setInt(140, parm.checkothers);
         pstmt6.setString(141, course);         // save requested course for pro approval process (drag-n-drop)
         pstmt6.setInt(142, parm.guest_id1);
         pstmt6.setInt(143, parm.guest_id2);
         pstmt6.setInt(144, parm.guest_id3);
         pstmt6.setInt(145, parm.guest_id4);
         pstmt6.setInt(146, parm.guest_id5);
         pstmt6.setInt(147, parm.guest_id6);
         pstmt6.setInt(148, parm.guest_id7);
         pstmt6.setInt(149, parm.guest_id8);
         pstmt6.setInt(150, parm.guest_id9);
         pstmt6.setInt(151, parm.guest_id10);
         pstmt6.setInt(152, parm.guest_id11);
         pstmt6.setInt(153, parm.guest_id12);
         pstmt6.setInt(154, parm.guest_id13);
         pstmt6.setInt(155, parm.guest_id14);
         pstmt6.setInt(156, parm.guest_id15);
         pstmt6.setInt(157, parm.guest_id16);
         pstmt6.setInt(158, parm.guest_id17);
         pstmt6.setInt(159, parm.guest_id18);
         pstmt6.setInt(160, parm.guest_id19);
         pstmt6.setInt(161, parm.guest_id20);
         pstmt6.setInt(162, parm.guest_id21);
         pstmt6.setInt(163, parm.guest_id22);
         pstmt6.setInt(164, parm.guest_id23);
         pstmt6.setInt(165, parm.guest_id24);
         pstmt6.setInt(166, parm.guest_id25);

         pstmt6.setLong(167, lottid);
         pstmt6.executeUpdate();      // execute the prepared stmt

         pstmt6.close();
      }
      catch (Exception e1) {
         out.println(SystemUtils.HeadTitle("DB Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<center>");
         out.println("<BR><BR><H2>Database Access Error</H2>");
         out.println("<BR><BR>Unable to access the Database.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR>" + e1.getMessage());
         out.println("<BR><BR>");
         //
         //  Return to _lott
         //
         goReturn(out, parm);
         return;
      }

   } else {          // new request

      proNew++;      // increment number of new tee times
      emailNew = 1;  // tee time is new

      //
      //  Get the next available id for the lottery request
      //
      lottid = SystemUtils.getLottId(con);      // allocate a new entry

      if (lottid == 0) {

         out.println(SystemUtils.HeadTitle("Lottery Request Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Lottery Request Error</H3>");
         out.println("<BR><BR>Sorry, we were unable to allocate a new entry for this Lottery Request.");
         out.println("<BR><BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact support.");
         out.println("<BR><BR>");
         //
         //  Return to _lott
         //
         goReturn(out, parm);
         return;
      }

      //
      //   Add a new Lottery Request
      //
      try {
         PreparedStatement pstmt3 = con.prepareStatement (
            "INSERT INTO lreqs3 (name, date, mm, dd, yy, day, hr, min, time, minsbefore, minsafter, " +
            "player1, player2, player3, player4, player5, player6, player7, player8, player9, player10, " +
            "player11, player12, player13, player14, player15, player16, player17, player18, player19, player20, " +
            "player21, player22, player23, player24, player25, " +
            "user1, user2, user3, user4, user5, user6, user7, user8, user9, user10, " +
            "user11, user12, user13, user14, user15, user16, user17, user18, user19, user20, " +
            "user21, user22, user23, user24, user25, " +
            "p1cw, p2cw, p3cw, p4cw, p5cw, p6cw, p7cw, p8cw, p9cw, p10cw, " +
            "p11cw, p12cw, p13cw, p14cw, p15cw, p16cw, p17cw, p18cw, p19cw, p20cw, " +
            "p21cw, p22cw, p23cw, p24cw, p25cw, notes, hideNotes, fb, courseName, proNew, " +
            "proMod, memNew, memMod, id, in_use, in_use_by, groups, type, state, atime1, " +
            "atime2, atime3, atime4, atime5, afb, p5, players, userg1, userg2, userg3, userg4, userg5, " +
            "userg6, userg7, userg8, userg9, userg10, userg11, userg12, userg13, userg14, userg15, " +
            "userg16, userg17, userg18, userg19, userg20, userg21, userg22, userg23, userg24, userg25, " +
            "weight, orig_by, p91, p92, p93, p94, p95, " +
            "p96, p97, p98, p99, p910, p911, p912, p913, p914, p915, " +
            "p916, p917, p918, p919, p920, p921, p922, p923, p924, p925, checkothers, courseReq, " +
            "guest_id1, guest_id2, guest_id3, guest_id4, guest_id5, " +
            "guest_id6, guest_id7, guest_id8, guest_id9, guest_id10, " +
            "guest_id11, guest_id12, guest_id13, guest_id14, guest_id15, " +
            "guest_id16, guest_id17, guest_id18, guest_id19, guest_id20, " +
            "guest_id21, guest_id22, guest_id23, guest_id24, guest_id25) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, " +                        
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +         
            "0, 0, 0, ?, 0, '', ?, '', 0, 0, " +
            "0, 0, 0, 0, 0, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "0, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?" +
            ")");

         pstmt3.clearParameters();        // clear the parms
         pstmt3.setString(1, lottName);
         pstmt3.setLong(2, date);       
         pstmt3.setInt(3, mm);
         pstmt3.setInt(4, parm.dd);
         pstmt3.setInt(5, yy);
         pstmt3.setString(6, day);
         pstmt3.setInt(7, hr);
         pstmt3.setInt(8, min);
         pstmt3.setInt(9, time);
         pstmt3.setInt(10, mins_before);
         pstmt3.setInt(11, mins_after);
         pstmt3.setString(12, parm.player1);
         pstmt3.setString(13, parm.player2);
         pstmt3.setString(14, parm.player3);
         pstmt3.setString(15, parm.player4);
         pstmt3.setString(16, parm.player5);
         pstmt3.setString(17, parm.player6);
         pstmt3.setString(18, parm.player7);
         pstmt3.setString(19, parm.player8);
         pstmt3.setString(20, parm.player9);
         pstmt3.setString(21, parm.player10);
         pstmt3.setString(22, parm.player11);
         pstmt3.setString(23, parm.player12);
         pstmt3.setString(24, parm.player13);
         pstmt3.setString(25, parm.player14);
         pstmt3.setString(26, parm.player15);
         pstmt3.setString(27, parm.player16);
         pstmt3.setString(28, parm.player17);
         pstmt3.setString(29, parm.player18);
         pstmt3.setString(30, parm.player19);
         pstmt3.setString(31, parm.player20);
         pstmt3.setString(32, parm.player21);
         pstmt3.setString(33, parm.player22);
         pstmt3.setString(34, parm.player23);
         pstmt3.setString(35, parm.player24);
         pstmt3.setString(36, parm.player25);
         pstmt3.setString(37, parm.user1);
         pstmt3.setString(38, parm.user2);
         pstmt3.setString(39, parm.user3);
         pstmt3.setString(40, parm.user4);
         pstmt3.setString(41, parm.user5);
         pstmt3.setString(42, parm.user6);
         pstmt3.setString(43, parm.user7);
         pstmt3.setString(44, parm.user8);
         pstmt3.setString(45, parm.user9);
         pstmt3.setString(46, parm.user10);
         pstmt3.setString(47, parm.user11);
         pstmt3.setString(48, parm.user12);
         pstmt3.setString(49, parm.user13);
         pstmt3.setString(50, parm.user14);
         pstmt3.setString(51, parm.user15);
         pstmt3.setString(52, parm.user16);
         pstmt3.setString(53, parm.user17);
         pstmt3.setString(54, parm.user18);
         pstmt3.setString(55, parm.user19);
         pstmt3.setString(56, parm.user20);
         pstmt3.setString(57, parm.user21);
         pstmt3.setString(58, parm.user22);
         pstmt3.setString(59, parm.user23);
         pstmt3.setString(60, parm.user24);
         pstmt3.setString(61, parm.user25);
         pstmt3.setString(62, parm.pcw1);
         pstmt3.setString(63, parm.pcw2);
         pstmt3.setString(64, parm.pcw3);
         pstmt3.setString(65, parm.pcw4);
         pstmt3.setString(66, parm.pcw5);
         pstmt3.setString(67, parm.pcw6);
         pstmt3.setString(68, parm.pcw7);
         pstmt3.setString(69, parm.pcw8);
         pstmt3.setString(70, parm.pcw9);
         pstmt3.setString(71, parm.pcw10);
         pstmt3.setString(72, parm.pcw11);
         pstmt3.setString(73, parm.pcw12);
         pstmt3.setString(74, parm.pcw13);
         pstmt3.setString(75, parm.pcw14);
         pstmt3.setString(76, parm.pcw15);
         pstmt3.setString(77, parm.pcw16);
         pstmt3.setString(78, parm.pcw17);
         pstmt3.setString(79, parm.pcw18);
         pstmt3.setString(80, parm.pcw19);
         pstmt3.setString(81, parm.pcw20);
         pstmt3.setString(82, parm.pcw21);
         pstmt3.setString(83, parm.pcw22);
         pstmt3.setString(84, parm.pcw23);
         pstmt3.setString(85, parm.pcw24);
         pstmt3.setString(86, parm.pcw25);
         pstmt3.setString(87, notes);
         pstmt3.setInt(88, hide);
         pstmt3.setInt(89, fb);
         pstmt3.setString(90, course);
         pstmt3.setInt(91, proNew);
         pstmt3.setLong(92, lottid);
         pstmt3.setInt(93, slots);
         pstmt3.setString(94, p5);
         pstmt3.setInt(95, players);
         pstmt3.setString(96, userg[0]);
         pstmt3.setString(97, userg[1]);
         pstmt3.setString(98, userg[2]);
         pstmt3.setString(99, userg[3]);
         pstmt3.setString(100, userg[4]);
         pstmt3.setString(101, userg[5]);
         pstmt3.setString(102, userg[6]);
         pstmt3.setString(103, userg[7]);
         pstmt3.setString(104, userg[8]);
         pstmt3.setString(105, userg[9]);
         pstmt3.setString(106, userg[10]);
         pstmt3.setString(107, userg[11]);
         pstmt3.setString(108, userg[12]);
         pstmt3.setString(109, userg[13]);
         pstmt3.setString(110, userg[14]);
         pstmt3.setString(111, userg[15]);
         pstmt3.setString(112, userg[16]);
         pstmt3.setString(113, userg[17]);
         pstmt3.setString(114, userg[18]);
         pstmt3.setString(115, userg[19]);
         pstmt3.setString(116, userg[20]);
         pstmt3.setString(117, userg[21]);
         pstmt3.setString(118, userg[22]);
         pstmt3.setString(119, userg[23]);
         pstmt3.setString(120, userg[24]);
         pstmt3.setString(121, orig_by);
         pstmt3.setInt(122, parm.p91);
         pstmt3.setInt(123, parm.p92);
         pstmt3.setInt(124, parm.p93);
         pstmt3.setInt(125, parm.p94);
         pstmt3.setInt(126, parm.p95);
         pstmt3.setInt(127, parm.p96);
         pstmt3.setInt(128, parm.p97);
         pstmt3.setInt(129, parm.p98);
         pstmt3.setInt(130, parm.p99);
         pstmt3.setInt(131, parm.p910);
         pstmt3.setInt(132, parm.p911);
         pstmt3.setInt(133, parm.p912);
         pstmt3.setInt(134, parm.p913);
         pstmt3.setInt(135, parm.p914);
         pstmt3.setInt(136, parm.p915);
         pstmt3.setInt(137, parm.p916);
         pstmt3.setInt(138, parm.p917);
         pstmt3.setInt(139, parm.p918);
         pstmt3.setInt(140, parm.p919);
         pstmt3.setInt(141, parm.p920);
         pstmt3.setInt(142, parm.p921);
         pstmt3.setInt(143, parm.p922);
         pstmt3.setInt(144, parm.p923);
         pstmt3.setInt(145, parm.p924);
         pstmt3.setInt(146, parm.p925);
         pstmt3.setInt(147, parm.checkothers);
         pstmt3.setString(148, course);
         pstmt3.setInt(149, parm.guest_id1);
         pstmt3.setInt(150, parm.guest_id2);
         pstmt3.setInt(151, parm.guest_id3);
         pstmt3.setInt(152, parm.guest_id4);
         pstmt3.setInt(153, parm.guest_id5);
         pstmt3.setInt(154, parm.guest_id6);
         pstmt3.setInt(155, parm.guest_id7);
         pstmt3.setInt(156, parm.guest_id8);
         pstmt3.setInt(157, parm.guest_id9);
         pstmt3.setInt(158, parm.guest_id10);
         pstmt3.setInt(159, parm.guest_id11);
         pstmt3.setInt(160, parm.guest_id12);
         pstmt3.setInt(161, parm.guest_id13);
         pstmt3.setInt(162, parm.guest_id14);
         pstmt3.setInt(163, parm.guest_id15);
         pstmt3.setInt(164, parm.guest_id16);
         pstmt3.setInt(165, parm.guest_id17);
         pstmt3.setInt(166, parm.guest_id18);
         pstmt3.setInt(167, parm.guest_id19);
         pstmt3.setInt(168, parm.guest_id20);
         pstmt3.setInt(169, parm.guest_id21);
         pstmt3.setInt(170, parm.guest_id22);
         pstmt3.setInt(171, parm.guest_id23);
         pstmt3.setInt(172, parm.guest_id24);
         pstmt3.setInt(173, parm.guest_id25);

         pstmt3.executeUpdate();        // execute the prepared stmt

         pstmt3.close();
      }
      catch (Exception e1) {
         out.println(SystemUtils.HeadTitle("DB Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<center>");
         out.println("<BR><BR><H2>Database Access Error</H2>");
         out.println("<BR><BR>Error encountered while attempting to save lottery request.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR>" + e1.getMessage());
         out.println("<BR><BR>");
         //
         //  Return to _lott
         //
         goReturn(out, parm);
         return;
      }
      
      
      //  Attempt to add hosts for any accompanied tracked guests
      if (parm.guest_id1 > 0 && !parm.userg[0].equals("")) Common_guestdb.addHost(parm.guest_id1, parm.userg[0], con);
      if (parm.guest_id2 > 0 && !parm.userg[1].equals("")) Common_guestdb.addHost(parm.guest_id2, parm.userg[1], con);
      if (parm.guest_id3 > 0 && !parm.userg[2].equals("")) Common_guestdb.addHost(parm.guest_id3, parm.userg[2], con);
      if (parm.guest_id4 > 0 && !parm.userg[3].equals("")) Common_guestdb.addHost(parm.guest_id4, parm.userg[3], con);
      if (parm.guest_id5 > 0 && !parm.userg[4].equals("")) Common_guestdb.addHost(parm.guest_id5, parm.userg[4], con);
      if (parm.guest_id6 > 0 && !parm.userg[5].equals("")) Common_guestdb.addHost(parm.guest_id6, parm.userg[5], con);
      if (parm.guest_id7 > 0 && !parm.userg[6].equals("")) Common_guestdb.addHost(parm.guest_id7, parm.userg[6], con);
      if (parm.guest_id8 > 0 && !parm.userg[7].equals("")) Common_guestdb.addHost(parm.guest_id8, parm.userg[7], con);
      if (parm.guest_id9 > 0 && !parm.userg[8].equals("")) Common_guestdb.addHost(parm.guest_id9, parm.userg[8], con);
      if (parm.guest_id10 > 0 && !parm.userg[9].equals("")) Common_guestdb.addHost(parm.guest_id10, parm.userg[9], con);
      if (parm.guest_id11 > 0 && !parm.userg[10].equals("")) Common_guestdb.addHost(parm.guest_id11, parm.userg[10], con);
      if (parm.guest_id12 > 0 && !parm.userg[11].equals("")) Common_guestdb.addHost(parm.guest_id12, parm.userg[11], con);
      if (parm.guest_id13 > 0 && !parm.userg[12].equals("")) Common_guestdb.addHost(parm.guest_id13, parm.userg[12], con);
      if (parm.guest_id14 > 0 && !parm.userg[13].equals("")) Common_guestdb.addHost(parm.guest_id14, parm.userg[13], con);
      if (parm.guest_id15 > 0 && !parm.userg[14].equals("")) Common_guestdb.addHost(parm.guest_id15, parm.userg[14], con);
      if (parm.guest_id16 > 0 && !parm.userg[15].equals("")) Common_guestdb.addHost(parm.guest_id16, parm.userg[15], con);
      if (parm.guest_id17 > 0 && !parm.userg[16].equals("")) Common_guestdb.addHost(parm.guest_id17, parm.userg[16], con);
      if (parm.guest_id18 > 0 && !parm.userg[17].equals("")) Common_guestdb.addHost(parm.guest_id18, parm.userg[17], con);
      if (parm.guest_id19 > 0 && !parm.userg[18].equals("")) Common_guestdb.addHost(parm.guest_id19, parm.userg[18], con);
      if (parm.guest_id20 > 0 && !parm.userg[19].equals("")) Common_guestdb.addHost(parm.guest_id20, parm.userg[19], con);
      if (parm.guest_id21 > 0 && !parm.userg[20].equals("")) Common_guestdb.addHost(parm.guest_id21, parm.userg[20], con);
      if (parm.guest_id22 > 0 && !parm.userg[21].equals("")) Common_guestdb.addHost(parm.guest_id22, parm.userg[21], con);
      if (parm.guest_id23 > 0 && !parm.userg[22].equals("")) Common_guestdb.addHost(parm.guest_id23, parm.userg[22], con);
      if (parm.guest_id24 > 0 && !parm.userg[23].equals("")) Common_guestdb.addHost(parm.guest_id24, parm.userg[23], con);
      if (parm.guest_id25 > 0 && !parm.userg[24].equals("")) Common_guestdb.addHost(parm.guest_id25, parm.userg[24], con);

      //
      //  Now see if a record exists for this in the Active Lotteries Table (actlott3).
      //  If not, add one so SystemUtils can process it when the time comes.
      //
      try {

         boolean skiplott = false;
           
         PreparedStatement pstmta = con.prepareStatement (
                  "SELECT pdate FROM actlott3 WHERE name = ? AND date = ? AND courseName = ?");

         pstmta.clearParameters();            // clear the parms
         pstmta.setString(1, lottName);       // Lottery Name
         pstmta.setLong(2, date);             // Date for this lottery request
         pstmta.setString(3, course);
         rs = pstmta.executeQuery();     

         if (rs.next()) {

            skiplott = true;        
         }
         pstmta.close();              // close the stmt
              
         if (skiplott == false) {
               
            //
            //  This lottery has not been entered for processing - enter it now.
            //
            //  Calculate the date to process this request - save date and time in record
            //
            int pdays = 0;
            int ptime = 0;
            int phr = 0;
            int pdd = parm.dd;
            int pmm = mm;
            int pyy = yy;
            long pdate = 0;

            PreparedStatement pstmt = con.prepareStatement (
                     "SELECT pdays, ptime FROM lottery3 WHERE name = ?");

            pstmt.clearParameters();        // clear the parms
            pstmt.setString(1, lottName);       // put the parm in stmt
            rs = pstmt.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               pdays = rs.getInt(1);            // days in advance to process requests
               ptime = rs.getInt(2);            // time of day to process lottery
            }
            pstmt.close();              // close the stmt

            //
            //  use the lottery's date and then roll back 'pdays' to determine the processing date
            //
            while (pdays > 0) {

               pdd--;                  // go back one day

               if (pdd == 0) {         // adjust month and year if necessary

                  if (pmm == 1) {      // must go back to 12/31 of prev year

                     pyy--;
                     pmm = 12;
                     pdd = 31;
                       
                  } else {

                     if (pmm == 2 || pmm == 4 || pmm == 6 || pmm == 8 || pmm == 9 || pmm == 11) { // new month has 31 days

                        pmm--;
                        pdd = 31;

                     } else {

                        if (pmm == 5 || pmm == 7 || pmm == 10 || pmm == 12) {     // new month has 30 days

                           pmm--;
                           pdd = 30;

                        } else {

                           if (pmm == 3) {     // new month to be Feb

                              pmm = 2;
                              pdd = 28;

                              if (pyy == 2004 ||  pyy == 2008 ||  pyy == 2012 ||  pyy == 2016 ||  pyy == 2020 ||  pyy == 2024 ||
                                  pyy == 2028 ||  pyy == 2032 ||  pyy == 2036 ||  pyy == 2040) {

                                 pdd = 29;           // leap year
                              }
                           }
                        }
                     }
                  }
               }
               pdays--;                  // continue for specified days in advance
            }                       // end of while

            pdate = (pyy * 10000) + (pmm * 100) + pdd;    // processing date = yyyymmdd (for comparisons)

            //
            //  adjust the time for the club's time zone (moved from before date because we need the date for this!!)
            //
            ptime = SystemUtils.adjustTimeBack(con, ptime, pdate);

            if (ptime < 0) {          // if negative, then roll back one day or ahead one day

               ptime = 0 - ptime;     // convert back to positive

               SystemUtils.logError("WARNING: " +club+ " has a lottery defined (" +lottName+ ") that processes near midnight.");      // shouldn't happen - log this
            }

            //
            //  Save the lottery info for processing 
            //
            PreparedStatement pstmt3a = con.prepareStatement (
               "INSERT INTO actlott3 (name, date, pdate, ptime, courseName) " +
               "VALUES (?, ?, ?, ?, ?)");

            pstmt3a.clearParameters();        // clear the parms
            pstmt3a.setString(1, lottName);
            pstmt3a.setLong(2, date);
            pstmt3a.setLong(3, pdate);
            pstmt3a.setInt(4, ptime);
            pstmt3a.setString(5, course);

            pstmt3a.executeUpdate();        // execute the prepared stmt

            pstmt3a.close();
         }
      }
      catch (Exception e1) {
         //
         //  save error message in /" +rev+ "/error.txt
         //
         String errorMsg = "Error in Proshop_lott: ";
         errorMsg = errorMsg + e1;                                 // build error msg
         SystemUtils.logError(errorMsg);                           // log it
      }

      
      //
      //   If recurr requested, prompt for additional request info
      //
      if (parm.isRecurr == true) {       // only include the recurrence parms if requested
         
         int cal_day = 0;
         int cal_month = 0;
         int cal_year = 0;
         
         if (!returnCourse.equals( "" )) {
            course = returnCourse;
         }

         //
         //  Get the end date for this lottery
         //
         try {
            PreparedStatement pstmt3 = con.prepareStatement (
                     "SELECT edate FROM lottery3 WHERE name = ?");

            pstmt3.clearParameters();        // clear the parms
            pstmt3.setString(1, lottName);       // put the parm in stmt
            rs = pstmt3.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               edate = rs.getLong("edate");             // end date for lottery
            }
            pstmt3.close();              // close the stmt
         
         }
         catch (Exception e1) {
            edate = 0;
         }
         
         if (edate == 0) {
            
            cal_day = parm.dd;    // use the date of the request if above failed
            cal_month = parm.mm;
            cal_year = parm.yy;
            
         } else {
            
            cal_year = (int)edate / 10000;      // convert edate for calendar
            temp = cal_year * 10000;
            cal_month = (int)edate - temp;
            temp = cal_month / 100;
            temp = temp * 100;
            cal_day = cal_month - temp;
            cal_month = cal_month / 100;
         }


         out.println(SystemUtils.HeadTitle2("Recurring Request Prompt"));

         // include files for dynamic calendars
         out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv30-styles.css\">");
         out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/calv30-scripts.js\"></script>");
     
         out.println("</head>");
         out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<table width=\"540\" border=\"1\" cellspacing=\"10\" cellpadding=\"10\" bgcolor=\"#F5F5DC\" align=\"center\">");
         out.println("<tr><td align=\"center\"><font size=\"3\">");
         out.println("<p><b>Thank you!</b><BR>Your tee time request has been accepted and processed.</p>");
         out.println("</font><font size=\"2\"><p><BR>You have elected to recur the request.<BR><BR>Please complete the following:</p>");
 
         out.println("<table align=center border=0 height=165>\n<tr valign=top>\n<td align=center>");  // build calendar for end date selection

         out.println(" <div id=cal_elem_0 style=\"width: 180px\"></div>");
         out.println("</td></tr>\n</table>");
         
          // start calendar javascript setup code
         out.println("<script type=\"text/javascript\">");
         out.println("<!--");
         
         out.println("var g_cal_bg_color = '#F5F5DC';");
         out.println("var g_cal_header_color = '#8B8970';");
         out.println("var g_cal_border_color = '#8B8970';");
         
         out.println("var g_cal_count = 1;");    // number of calendars on this page
         out.println("var g_cal_year = new Array(g_cal_count - 1);");
         out.println("var g_cal_month = new Array(g_cal_count - 1);");
         out.println("var g_cal_beginning_month = new Array(g_cal_count - 1);");
         out.println("var g_cal_ending_month = new Array(g_cal_count - 1);");
         out.println("var g_cal_beginning_day = new Array(g_cal_count - 1);");
         out.println("var g_cal_ending_day = new Array(g_cal_count - 1);");
         out.println("var g_cal_beginning_year = new Array(g_cal_count - 1);");
         out.println("var g_cal_ending_year = new Array(g_cal_count - 1);");
         
         // set calendar date parts in js
         out.println("g_cal_month[0] = " + parm.mm + ";");
         out.println("g_cal_year[0] = " + parm.yy + ";");
         out.println("g_cal_beginning_month[0] = " + parm.mm + ";");
         out.println("g_cal_beginning_year[0] = " + parm.yy + ";");
         out.println("g_cal_beginning_day[0] = " + parm.dd + ";");
         
         out.println("g_cal_ending_month[0] = " + cal_month + ";");
         out.println("g_cal_ending_day[0] = " + cal_day + ";");
         out.println("g_cal_ending_year[0] = " + cal_year + ";");

         // override the function that's called when user clicks day on calendar
         out.println("function sd(pCal, pMonth, pDay, pYear) {");
         out.println("  f = document.forms['frmPreview'];");
         out.println("  f.date.value = pYear + \"-\" + pMonth + \"-\" + pDay;");
         //out.println("  f.submit();");
         out.println("}");
         
         out.println("// -->");
         out.println("</script>");
         
         out.println("<script type=\"text/javascript\">\ndoCalendar('0');\n</script>");
         
         
         out.println("<form method=\"post\" action=\"Proshop_lott\" name=\"frmPreview\">");
         
         out.println("End Date (select from calendar): &nbsp;<input type=text name=date id=date value=\"\">");
         
         out.println("<BR><BR>Recurrence:&nbsp;&nbsp;");
         out.println("<select size=\"1\" name=\"eoweek\">");
         out.println("<option value=\"0\">Every Week</option>");
         out.println("<option value=\"1\">Every Other Week</option>");
         out.println("</select><BR><BR>");
           
         out.println("<input type=\"hidden\" name=\"recurPrompt\" value=\"yes\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
         out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + course + "\">");   
         out.println("<input type=\"hidden\" name=\"jump\" value=" + jump + ">");
         out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + lottid + "\">");
         out.println("<input type=\"submit\" value=\"Build Recurring Requests\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form><BR>");
                  
         out.println("<form action=\"Proshop_jump\" method=\"post\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
         out.println("<input type=\"hidden\" name=\"jump\" value=" + jump + ">");
         out.println("<input type=\"submit\" value=\"Return w/o Recurring the Request\">");
         out.println("</font></td></tr></table>");
         
         skipReturns = true;       // skip the return code below
                                   // continue below so we send emails, etc.           
      }  
      

   }  // end of if new or mod
   
   
   if (skipReturns == false) {     // if we haven't prompted the user for additional acction

      //
      //  Build the HTML page to confirm reservation for user
      //
      int recur_count = 0;
      
      if (updateReq == true) {

         //
         //  This was an update - check if original request was part of a recurrence.
         //  If so, then see if user wants to update all future recurrences too.
         //
         recur_count = Common_Lott.checkRecurReq(lottid, con);   // get the number of recurring requests after this one
         
         if (recur_count == 0) {
            
            Common_Lott.unchainRecurReq(lottid, con);   // make sure there is no recur_id (in case this is the last in a chain)
         }
      }

      //
      //  These returns will pause for 1 second, then return automatically if meta supported, else user clicks on 'return'
      //
      //
      if (index.equals( "888" )) {         // if came from proshop_searchmain

         out.println("<HTML>");
         out.println("<HEAD><link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">");
         out.println("<Title>Proshop Lottery Registration Page</Title>");
         if (recur_count == 0) {
            out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Proshop_jump?search\">");
         }
         out.println("</HEAD>");
         out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");

         out.println("<p>&nbsp;</p><p>&nbsp;<b>Thank you!</b>&nbsp;&nbsp;Your lottery request has been accepted and processed.</p>");

         if (notesL > 254) {

         out.println("<p>&nbsp;</p><b>Notice:</b>&nbsp;&nbsp;The notes you entered exceeded 254 characters in length.  All characters beyond 254 will be truncated.</p>");
         }
         out.println("<p>&nbsp;</p></font>");

         if (recur_count > 0) {

            if (recur_count > 1) {
               out.println("<strong>ATTENTION:</strong> &nbsp;There are " +recur_count+ " recurring requests scheduled after this one.<BR>");
               out.println("Would you like to update those requests too?<BR><BR>");
               out.println("Please note that those requests will be updated to be identical to this one.");
            } else {
               out.println("<strong>ATTENTION:</strong> &nbsp;There is 1 recurring request scheduled after this one.<BR>");
               out.println("Would you like to update the other request too?<BR><BR>");
               out.println("Please note that request will be updated to be identical to this one.");
            }
            out.println("<BR><BR><form method=\"post\" action=\"Proshop_lott\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" +index+ "\">");
            out.println("<input type=\"hidden\" name=\"lottid\" value=\"" +lottid+ "\">");
            out.println("<input type=\"hidden\" name=\"updateRecur\" value=\"yes\">");
            out.println("<input type=\"submit\" name=\"doUpdate\" value=\"Yes, Update Other Requests Too\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("<BR><BR>");
            out.println("If not, this request will be separated from the other recurring requests.<BR><BR>");
            out.println("<input type=\"submit\" name=\"doUpdate\" value=\"No, Only Update This Request\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form><BR>");
            
         } else {

            out.println("<font size=\"2\">");
            out.println("<form method=\"get\" action=\"Proshop_jump\">");
            out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");
         }

      } else {                          

         if (!returnCourse.equals( "" )) {
            course = returnCourse;
         }

         if (index.equals( "777" )) {         // if came from Proshop_mlottery

            out.println("<HTML>");
            out.println("<HEAD><link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">");
            out.println("<Title>Proshop Lottery Registration Page</Title>");
            if (recur_count == 0) {
               out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Proshop_jump?course=" + course + "&index2=" + index2 + "\">");
            }
            out.println("</HEAD>");
            out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");

            out.println("<p>&nbsp;</p><p>&nbsp;<b>Thank you!</b>&nbsp;&nbsp;Your lottery request has been accepted and processed.</p>");

            if (notesL > 254) {

            out.println("<p>&nbsp;</p><b>Notice:</b>&nbsp;&nbsp;The notes you entered exceeded 254 characters in length.  All characters beyond 254 will be truncated.</p>");
            }
            out.println("<p>&nbsp;</p></font>");

            if (recur_count > 0) {

               if (recur_count > 1) {
                  out.println("<strong>ATTENTION:</strong> &nbsp;There are " +recur_count+ " recurring requests scheduled after this one.<BR>");
                  out.println("Would you like to update those requests too?<BR><BR>");
                  out.println("Please note that those requests will be updated to be identical to this one.");
               } else {
                  out.println("<strong>ATTENTION:</strong> &nbsp;There is 1 recurring request scheduled after this one.<BR>");
                  out.println("Would you like to update the other request too?<BR><BR>");
                  out.println("Please note that request will be updated to be identical to this one.");
               }
               out.println("<BR><BR><form method=\"post\" action=\"Proshop_lott\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" +index+ "\">");
               out.println("<input type=\"hidden\" name=\"index2\" value=\"" +index2+ "\">");
               out.println("<input type=\"hidden\" name=\"lottid\" value=\"" +lottid+ "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
               out.println("<input type=\"hidden\" name=\"updateRecur\" value=\"yes\">");
               out.println("<input type=\"submit\" name=\"doUpdate\" value=\"Yes, Update Other Requests Too\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("<BR><BR>");
               out.println("If not, this request will be separated from the other recurring requests.");
               out.println("<BR><BR><input type=\"submit\" name=\"doUpdate\" value=\"No, Only Update This Request\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</form><BR>");

            } else {

               out.println("<font size=\"2\">");
               out.println("<form method=\"post\" action=\"Proshop_jump\">");
               out.println("<input type=\"hidden\" name=\"index2\" value=\"" + index2 + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");

               out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</form></font>");

            }

         } else {                             // came from proshop_sheet

            out.println("<HTML>");
            out.println("<HEAD><link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">");
            out.println("<Title>Proshop Lottery Request Page</Title>");
            if (recur_count == 0) {
               out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Proshop_jump?index=" + index + "&course=" + course + "&jump=" + jump + "\">");
            }
            out.println("</HEAD>");
            out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");

            out.println("<p>&nbsp;</p><p>&nbsp;<b>Thank you!</b>&nbsp;&nbsp;Your lottery request has been accepted and processed.</p>");

            if (notesL > 254) {

            out.println("<p>&nbsp;</p><b>Notice:</b>&nbsp;&nbsp;The notes you entered exceeded 254 characters in length.  All characters beyond 254 will be truncated.</p>");
            }
            out.println("<p>&nbsp;</p></font>");

            if (recur_count > 0) {

               if (recur_count > 1) {
                  out.println("<strong>ATTENTION:</strong> &nbsp;There are " +recur_count+ " recurring requests scheduled after this one.<BR>");
                  out.println("Would you like to update those requests too?<BR><BR>");
                  out.println("Please note that those requests will be updated to be identical to this one.");
               } else {
                  out.println("<strong>ATTENTION:</strong> &nbsp;There is 1 recurring request scheduled after this one.<BR>");
                  out.println("Would you like to update the other request too?<BR><BR>");
                  out.println("Please note that request will be updated to be identical to this one.");
               }
               out.println("<BR><BR><form method=\"post\" action=\"Proshop_lott\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" +index+ "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=" + jump + ">");
               out.println("<input type=\"hidden\" name=\"lottid\" value=\"" +lottid+ "\">");
               out.println("<input type=\"hidden\" name=\"updateRecur\" value=\"yes\">");
               out.println("<input type=\"submit\" name=\"doUpdate\" value=\"Yes, Update Other Requests Too\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("<BR><BR>");
               out.println("If not, this request will be separated from the other recurring requests.");
               out.println("<BR><BR><input type=\"submit\" name=\"doUpdate\" value=\"No, Only Update This Request\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</form><BR>");

            } else {

               out.println("<table border=\"0\" cols=\"1\" bgcolor=\"#336633\" cellpadding=\"8\">");
               out.println("<form action=\"Proshop_jump\" method=\"post\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
               out.println("<input type=\"hidden\" name=\"jump\" value=" + jump + ">");
               out.println("<tr><td><font color=\"#FFFFFF\" size=\"2\">");
               out.println("<input type=\"submit\" value=\"Return\">");
               out.println("</font></td></tr></form></table>");
            }
         }
      }
   }         // end of IF skipReturns

   //
   //  End of HTML page
   //
   out.println("</center></font></body></html>");
   out.close();

   try {

      resp.flushBuffer();      // force the repsonse to complete

   }
   catch (Exception ignore) {
   }

   //
   //***********************************************
   //  Send email notification if necessary
   //***********************************************
   //
   if (sendEmail != 0 && !club.equals( "ranchobernardo" )) {      // do not send for Rancho Bernardo

      if (!club.equals( "westchester" ) || lstate != 3) {      // if Westchester and lstate = 3, then skip emails

         sendMail(con, parm, emailNew, emailMod, user, club, guests);        // send emails
      }

   }     // end of IF sendEmail

 }       // end of Verify
 // *******************************************************************************


 // *******************************************************************************
 //  Parse Member Names
 // *******************************************************************************
 //
 private boolean parseNames(PrintWriter out, parmLott parm) {


   boolean error = false;


   if (!parm.player1.equals( "" ) && !parm.player1.equalsIgnoreCase( "x" ) && parm.g[0].equals( "" )) {

      if ((parm.player1.equalsIgnoreCase( parm.player2 )) || (parm.player1.equalsIgnoreCase( parm.player3 )) ||
          (parm.player1.equalsIgnoreCase( parm.player4 )) || (parm.player1.equalsIgnoreCase( parm.player5 )) ||
          (parm.player1.equalsIgnoreCase( parm.player6 )) || (parm.player1.equalsIgnoreCase( parm.player7 )) ||
          (parm.player1.equalsIgnoreCase( parm.player8 )) || (parm.player1.equalsIgnoreCase( parm.player9 )) ||
          (parm.player1.equalsIgnoreCase( parm.player10 )) || (parm.player1.equalsIgnoreCase( parm.player11 )) ||
          (parm.player1.equalsIgnoreCase( parm.player12 )) || (parm.player1.equalsIgnoreCase( parm.player13 )) ||
          (parm.player1.equalsIgnoreCase( parm.player14 )) || (parm.player1.equalsIgnoreCase( parm.player15 )) ||
          (parm.player1.equalsIgnoreCase( parm.player16 )) || (parm.player1.equalsIgnoreCase( parm.player17 )) ||
          (parm.player1.equalsIgnoreCase( parm.player18 )) || (parm.player1.equalsIgnoreCase( parm.player19 )) ||
          (parm.player1.equalsIgnoreCase( parm.player20 )) || (parm.player1.equalsIgnoreCase( parm.player21 )) ||
          (parm.player1.equalsIgnoreCase( parm.player22 )) || (parm.player1.equalsIgnoreCase( parm.player23 )) ||
          (parm.player1.equalsIgnoreCase( parm.player24 )) || (parm.player1.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player1, parm);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player1 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player1, parm);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname1 = tok.nextToken();
         parm.lname1 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname1 = tok.nextToken();
         parm.mi1 = tok.nextToken();
         parm.lname1 = tok.nextToken();
      }
   }

   if (!parm.player2.equals( "" ) && !parm.player2.equalsIgnoreCase( "x" ) && parm.g[1].equals( "" )) {

      if ((parm.player2.equalsIgnoreCase( parm.player3 )) ||
          (parm.player2.equalsIgnoreCase( parm.player4 )) || (parm.player2.equalsIgnoreCase( parm.player5 )) ||
          (parm.player2.equalsIgnoreCase( parm.player6 )) || (parm.player2.equalsIgnoreCase( parm.player7 )) ||
          (parm.player2.equalsIgnoreCase( parm.player8 )) || (parm.player2.equalsIgnoreCase( parm.player9 )) ||
          (parm.player2.equalsIgnoreCase( parm.player10 )) || (parm.player2.equalsIgnoreCase( parm.player11 )) ||
          (parm.player2.equalsIgnoreCase( parm.player12 )) || (parm.player2.equalsIgnoreCase( parm.player13 )) ||
          (parm.player2.equalsIgnoreCase( parm.player14 )) || (parm.player2.equalsIgnoreCase( parm.player15 )) ||
          (parm.player2.equalsIgnoreCase( parm.player16 )) || (parm.player2.equalsIgnoreCase( parm.player17 )) ||
          (parm.player2.equalsIgnoreCase( parm.player18 )) || (parm.player2.equalsIgnoreCase( parm.player19 )) ||
          (parm.player2.equalsIgnoreCase( parm.player20 )) || (parm.player2.equalsIgnoreCase( parm.player21 )) ||
          (parm.player2.equalsIgnoreCase( parm.player22 )) || (parm.player2.equalsIgnoreCase( parm.player23 )) ||
          (parm.player2.equalsIgnoreCase( parm.player24 )) || (parm.player2.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player2, parm);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player2 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player2, parm);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname2 = tok.nextToken();
         parm.lname2 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname2 = tok.nextToken();
         parm.mi2 = tok.nextToken();
         parm.lname2 = tok.nextToken();
      }
   }

   if (!parm.player3.equals( "" ) && !parm.player3.equalsIgnoreCase( "x" ) && parm.g[2].equals( "" )) {

      if ((parm.player3.equalsIgnoreCase( parm.player4 )) || (parm.player3.equalsIgnoreCase( parm.player5 )) ||
          (parm.player3.equalsIgnoreCase( parm.player6 )) || (parm.player3.equalsIgnoreCase( parm.player7 )) ||
          (parm.player3.equalsIgnoreCase( parm.player8 )) || (parm.player3.equalsIgnoreCase( parm.player9 )) ||
          (parm.player3.equalsIgnoreCase( parm.player10 )) || (parm.player3.equalsIgnoreCase( parm.player11 )) ||
          (parm.player3.equalsIgnoreCase( parm.player12 )) || (parm.player3.equalsIgnoreCase( parm.player13 )) ||
          (parm.player3.equalsIgnoreCase( parm.player14 )) || (parm.player3.equalsIgnoreCase( parm.player15 )) ||
          (parm.player3.equalsIgnoreCase( parm.player16 )) || (parm.player3.equalsIgnoreCase( parm.player17 )) ||
          (parm.player3.equalsIgnoreCase( parm.player18 )) || (parm.player3.equalsIgnoreCase( parm.player19 )) ||
          (parm.player3.equalsIgnoreCase( parm.player20 )) || (parm.player3.equalsIgnoreCase( parm.player21 )) ||
          (parm.player3.equalsIgnoreCase( parm.player22 )) || (parm.player3.equalsIgnoreCase( parm.player23 )) ||
          (parm.player3.equalsIgnoreCase( parm.player24 )) || (parm.player3.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player3, parm);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player3 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player3, parm);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname3 = tok.nextToken();
         parm.lname3 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname3 = tok.nextToken();
         parm.mi3 = tok.nextToken();
         parm.lname3 = tok.nextToken();
      }
   }

   if (!parm.player4.equals( "" ) && !parm.player4.equalsIgnoreCase( "x" ) && parm.g[3].equals( "" )) {

      if ((parm.player4.equalsIgnoreCase( parm.player5 )) ||
          (parm.player4.equalsIgnoreCase( parm.player6 )) || (parm.player4.equalsIgnoreCase( parm.player7 )) ||
          (parm.player4.equalsIgnoreCase( parm.player8 )) || (parm.player4.equalsIgnoreCase( parm.player9 )) ||
          (parm.player4.equalsIgnoreCase( parm.player10 )) || (parm.player4.equalsIgnoreCase( parm.player11 )) ||
          (parm.player4.equalsIgnoreCase( parm.player12 )) || (parm.player4.equalsIgnoreCase( parm.player13 )) ||
          (parm.player4.equalsIgnoreCase( parm.player14 )) || (parm.player4.equalsIgnoreCase( parm.player15 )) ||
          (parm.player4.equalsIgnoreCase( parm.player16 )) || (parm.player4.equalsIgnoreCase( parm.player17 )) ||
          (parm.player4.equalsIgnoreCase( parm.player18 )) || (parm.player4.equalsIgnoreCase( parm.player19 )) ||
          (parm.player4.equalsIgnoreCase( parm.player20 )) || (parm.player4.equalsIgnoreCase( parm.player21 )) ||
          (parm.player4.equalsIgnoreCase( parm.player22 )) || (parm.player4.equalsIgnoreCase( parm.player23 )) ||
          (parm.player4.equalsIgnoreCase( parm.player24 )) || (parm.player4.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player4, parm);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player4 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player4, parm);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname4 = tok.nextToken();
         parm.lname4 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname4 = tok.nextToken();
         parm.mi4 = tok.nextToken();
         parm.lname4 = tok.nextToken();
      }
   }

   if (!parm.player5.equals( "" ) && !parm.player5.equalsIgnoreCase( "x" ) && parm.g[4].equals( "" )) {

      if ((parm.player5.equalsIgnoreCase( parm.player6 )) || (parm.player5.equalsIgnoreCase( parm.player7 )) ||
          (parm.player5.equalsIgnoreCase( parm.player8 )) || (parm.player5.equalsIgnoreCase( parm.player9 )) ||
          (parm.player5.equalsIgnoreCase( parm.player10 )) || (parm.player5.equalsIgnoreCase( parm.player11 )) ||
          (parm.player5.equalsIgnoreCase( parm.player12 )) || (parm.player5.equalsIgnoreCase( parm.player13 )) ||
          (parm.player5.equalsIgnoreCase( parm.player14 )) || (parm.player5.equalsIgnoreCase( parm.player15 )) ||
          (parm.player5.equalsIgnoreCase( parm.player16 )) || (parm.player5.equalsIgnoreCase( parm.player17 )) ||
          (parm.player5.equalsIgnoreCase( parm.player18 )) || (parm.player5.equalsIgnoreCase( parm.player19 )) ||
          (parm.player5.equalsIgnoreCase( parm.player20 )) || (parm.player5.equalsIgnoreCase( parm.player21 )) ||
          (parm.player5.equalsIgnoreCase( parm.player22 )) || (parm.player5.equalsIgnoreCase( parm.player23 )) ||
          (parm.player5.equalsIgnoreCase( parm.player24 )) || (parm.player5.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player5, parm);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player5 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player5, parm);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname5 = tok.nextToken();
         parm.lname5 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname5 = tok.nextToken();
         parm.mi5 = tok.nextToken();
         parm.lname5 = tok.nextToken();
      }
   }

   if (!parm.player6.equals( "" ) && !parm.player6.equalsIgnoreCase( "x" ) && parm.g[5].equals( "" )) {

      if ((parm.player6.equalsIgnoreCase( parm.player7 )) ||
          (parm.player6.equalsIgnoreCase( parm.player8 )) || (parm.player6.equalsIgnoreCase( parm.player9 )) ||
          (parm.player6.equalsIgnoreCase( parm.player10 )) || (parm.player6.equalsIgnoreCase( parm.player11 )) ||
          (parm.player6.equalsIgnoreCase( parm.player12 )) || (parm.player6.equalsIgnoreCase( parm.player13 )) ||
          (parm.player6.equalsIgnoreCase( parm.player14 )) || (parm.player6.equalsIgnoreCase( parm.player15 )) ||
          (parm.player6.equalsIgnoreCase( parm.player16 )) || (parm.player6.equalsIgnoreCase( parm.player17 )) ||
          (parm.player6.equalsIgnoreCase( parm.player18 )) || (parm.player6.equalsIgnoreCase( parm.player19 )) ||
          (parm.player6.equalsIgnoreCase( parm.player20 )) || (parm.player6.equalsIgnoreCase( parm.player21 )) ||
          (parm.player6.equalsIgnoreCase( parm.player22 )) || (parm.player6.equalsIgnoreCase( parm.player23 )) ||
          (parm.player6.equalsIgnoreCase( parm.player24 )) || (parm.player6.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player6, parm);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player6 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player6, parm);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname6 = tok.nextToken();
         parm.lname6 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname6 = tok.nextToken();
         parm.mi6 = tok.nextToken();
         parm.lname6 = tok.nextToken();
      }
   }

   if (!parm.player7.equals( "" ) && !parm.player7.equalsIgnoreCase( "x" ) && parm.g[6].equals( "" )) {

      if ((parm.player7.equalsIgnoreCase( parm.player8 )) || (parm.player7.equalsIgnoreCase( parm.player9 )) ||
          (parm.player7.equalsIgnoreCase( parm.player10 )) || (parm.player7.equalsIgnoreCase( parm.player11 )) ||
          (parm.player7.equalsIgnoreCase( parm.player12 )) || (parm.player7.equalsIgnoreCase( parm.player13 )) ||
          (parm.player7.equalsIgnoreCase( parm.player14 )) || (parm.player7.equalsIgnoreCase( parm.player15 )) ||
          (parm.player7.equalsIgnoreCase( parm.player16 )) || (parm.player7.equalsIgnoreCase( parm.player17 )) ||
          (parm.player7.equalsIgnoreCase( parm.player18 )) || (parm.player7.equalsIgnoreCase( parm.player19 )) ||
          (parm.player7.equalsIgnoreCase( parm.player20 )) || (parm.player7.equalsIgnoreCase( parm.player21 )) ||
          (parm.player7.equalsIgnoreCase( parm.player22 )) || (parm.player7.equalsIgnoreCase( parm.player23 )) ||
          (parm.player7.equalsIgnoreCase( parm.player24 )) || (parm.player7.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player7, parm);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player7 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player7, parm);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname7 = tok.nextToken();
         parm.lname7 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname7 = tok.nextToken();
         parm.mi7 = tok.nextToken();
         parm.lname7 = tok.nextToken();
      }
   }

   if (!parm.player8.equals( "" ) && !parm.player8.equalsIgnoreCase( "x" ) && parm.g[7].equals( "" )) {

      if ((parm.player8.equalsIgnoreCase( parm.player9 )) ||
          (parm.player8.equalsIgnoreCase( parm.player10 )) || (parm.player8.equalsIgnoreCase( parm.player11 )) ||
          (parm.player8.equalsIgnoreCase( parm.player12 )) || (parm.player8.equalsIgnoreCase( parm.player13 )) ||
          (parm.player8.equalsIgnoreCase( parm.player14 )) || (parm.player8.equalsIgnoreCase( parm.player15 )) ||
          (parm.player8.equalsIgnoreCase( parm.player16 )) || (parm.player8.equalsIgnoreCase( parm.player17 )) ||
          (parm.player8.equalsIgnoreCase( parm.player18 )) || (parm.player8.equalsIgnoreCase( parm.player19 )) ||
          (parm.player8.equalsIgnoreCase( parm.player20 )) || (parm.player8.equalsIgnoreCase( parm.player21 )) ||
          (parm.player8.equalsIgnoreCase( parm.player22 )) || (parm.player8.equalsIgnoreCase( parm.player23 )) ||
          (parm.player8.equalsIgnoreCase( parm.player24 )) || (parm.player8.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player8, parm);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player8 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player8, parm);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname8 = tok.nextToken();
         parm.lname8 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname8 = tok.nextToken();
         parm.mi8 = tok.nextToken();
         parm.lname8 = tok.nextToken();
      }
   }

   if (!parm.player9.equals( "" ) && !parm.player9.equalsIgnoreCase( "x" ) && parm.g[8].equals( "" )) {

      if ((parm.player9.equalsIgnoreCase( parm.player10 )) || (parm.player9.equalsIgnoreCase( parm.player11 )) ||
          (parm.player9.equalsIgnoreCase( parm.player12 )) || (parm.player9.equalsIgnoreCase( parm.player13 )) ||
          (parm.player9.equalsIgnoreCase( parm.player14 )) || (parm.player9.equalsIgnoreCase( parm.player15 )) ||
          (parm.player9.equalsIgnoreCase( parm.player16 )) || (parm.player9.equalsIgnoreCase( parm.player17 )) ||
          (parm.player9.equalsIgnoreCase( parm.player18 )) || (parm.player9.equalsIgnoreCase( parm.player19 )) ||
          (parm.player9.equalsIgnoreCase( parm.player20 )) || (parm.player9.equalsIgnoreCase( parm.player21 )) ||
          (parm.player9.equalsIgnoreCase( parm.player22 )) || (parm.player9.equalsIgnoreCase( parm.player23 )) ||
          (parm.player9.equalsIgnoreCase( parm.player24 )) || (parm.player9.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player9, parm);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player9 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player9, parm);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname9 = tok.nextToken();
         parm.lname9 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname9 = tok.nextToken();
         parm.mi9 = tok.nextToken();
         parm.lname9 = tok.nextToken();
      }
   }

   if (!parm.player10.equals( "" ) && !parm.player10.equalsIgnoreCase( "x" ) && parm.g[9].equals( "" )) {

      if ((parm.player10.equalsIgnoreCase( parm.player11 )) ||
          (parm.player10.equalsIgnoreCase( parm.player12 )) || (parm.player10.equalsIgnoreCase( parm.player13 )) ||
          (parm.player10.equalsIgnoreCase( parm.player14 )) || (parm.player10.equalsIgnoreCase( parm.player15 )) ||
          (parm.player10.equalsIgnoreCase( parm.player16 )) || (parm.player10.equalsIgnoreCase( parm.player17 )) ||
          (parm.player10.equalsIgnoreCase( parm.player18 )) || (parm.player10.equalsIgnoreCase( parm.player19 )) ||
          (parm.player10.equalsIgnoreCase( parm.player20 )) || (parm.player10.equalsIgnoreCase( parm.player21 )) ||
          (parm.player10.equalsIgnoreCase( parm.player22 )) || (parm.player10.equalsIgnoreCase( parm.player23 )) ||
          (parm.player10.equalsIgnoreCase( parm.player24 )) || (parm.player10.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player10, parm);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player10 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player10, parm);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname10 = tok.nextToken();
         parm.lname10 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname10 = tok.nextToken();
         parm.mi10 = tok.nextToken();
         parm.lname10 = tok.nextToken();
      }
   }

   if (!parm.player11.equals( "" ) && !parm.player11.equalsIgnoreCase( "x" ) && parm.g[10].equals( "" )) {

      if ((parm.player11.equalsIgnoreCase( parm.player12 )) || (parm.player11.equalsIgnoreCase( parm.player13 )) ||
          (parm.player11.equalsIgnoreCase( parm.player14 )) || (parm.player11.equalsIgnoreCase( parm.player15 )) ||
          (parm.player11.equalsIgnoreCase( parm.player16 )) || (parm.player11.equalsIgnoreCase( parm.player17 )) ||
          (parm.player11.equalsIgnoreCase( parm.player18 )) || (parm.player11.equalsIgnoreCase( parm.player19 )) ||
          (parm.player11.equalsIgnoreCase( parm.player20 )) || (parm.player11.equalsIgnoreCase( parm.player21 )) ||
          (parm.player11.equalsIgnoreCase( parm.player22 )) || (parm.player11.equalsIgnoreCase( parm.player23 )) ||
          (parm.player11.equalsIgnoreCase( parm.player24 )) || (parm.player11.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player11, parm);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player11 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player11, parm);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname11 = tok.nextToken();
         parm.lname11 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname11 = tok.nextToken();
         parm.mi11 = tok.nextToken();
         parm.lname11 = tok.nextToken();
      }
   }

   if (!parm.player12.equals( "" ) && !parm.player12.equalsIgnoreCase( "x" ) && parm.g[11].equals( "" )) {

      if ((parm.player12.equalsIgnoreCase( parm.player13 )) ||
          (parm.player12.equalsIgnoreCase( parm.player14 )) || (parm.player12.equalsIgnoreCase( parm.player15 )) ||
          (parm.player12.equalsIgnoreCase( parm.player16 )) || (parm.player12.equalsIgnoreCase( parm.player17 )) ||
          (parm.player12.equalsIgnoreCase( parm.player18 )) || (parm.player12.equalsIgnoreCase( parm.player19 )) ||
          (parm.player12.equalsIgnoreCase( parm.player20 )) || (parm.player12.equalsIgnoreCase( parm.player21 )) ||
          (parm.player12.equalsIgnoreCase( parm.player22 )) || (parm.player12.equalsIgnoreCase( parm.player23 )) ||
          (parm.player12.equalsIgnoreCase( parm.player24 )) || (parm.player12.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player12, parm);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player12 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player12, parm);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname12 = tok.nextToken();
         parm.lname12 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname12 = tok.nextToken();
         parm.mi12 = tok.nextToken();
         parm.lname12 = tok.nextToken();
      }
   }

   if (!parm.player13.equals( "" ) && !parm.player13.equalsIgnoreCase( "x" ) && parm.g[12].equals( "" )) {

      if ((parm.player13.equalsIgnoreCase( parm.player14 )) || (parm.player13.equalsIgnoreCase( parm.player15 )) ||
          (parm.player13.equalsIgnoreCase( parm.player16 )) || (parm.player13.equalsIgnoreCase( parm.player17 )) ||
          (parm.player13.equalsIgnoreCase( parm.player18 )) || (parm.player13.equalsIgnoreCase( parm.player19 )) ||
          (parm.player13.equalsIgnoreCase( parm.player20 )) || (parm.player13.equalsIgnoreCase( parm.player21 )) ||
          (parm.player13.equalsIgnoreCase( parm.player22 )) || (parm.player13.equalsIgnoreCase( parm.player23 )) ||
          (parm.player13.equalsIgnoreCase( parm.player24 )) || (parm.player13.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player13, parm);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player13 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player13, parm);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname13 = tok.nextToken();
         parm.lname13 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname13 = tok.nextToken();
         parm.mi13 = tok.nextToken();
         parm.lname13 = tok.nextToken();
      }
   }

   if (!parm.player14.equals( "" ) && !parm.player14.equalsIgnoreCase( "x" ) && parm.g[13].equals( "" )) {

      if ((parm.player14.equalsIgnoreCase( parm.player15 )) ||
          (parm.player14.equalsIgnoreCase( parm.player16 )) || (parm.player14.equalsIgnoreCase( parm.player17 )) ||
          (parm.player14.equalsIgnoreCase( parm.player18 )) || (parm.player14.equalsIgnoreCase( parm.player19 )) ||
          (parm.player14.equalsIgnoreCase( parm.player20 )) || (parm.player14.equalsIgnoreCase( parm.player21 )) ||
          (parm.player14.equalsIgnoreCase( parm.player22 )) || (parm.player14.equalsIgnoreCase( parm.player23 )) ||
          (parm.player14.equalsIgnoreCase( parm.player24 )) || (parm.player14.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player14, parm);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player14 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player14, parm);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname14 = tok.nextToken();
         parm.lname14 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname14 = tok.nextToken();
         parm.mi14 = tok.nextToken();
         parm.lname14 = tok.nextToken();
      }
   }

   if (!parm.player15.equals( "" ) && !parm.player15.equalsIgnoreCase( "x" ) && parm.g[14].equals( "" )) {

      if ((parm.player15.equalsIgnoreCase( parm.player16 )) || (parm.player15.equalsIgnoreCase( parm.player17 )) ||
          (parm.player15.equalsIgnoreCase( parm.player18 )) || (parm.player15.equalsIgnoreCase( parm.player19 )) ||
          (parm.player15.equalsIgnoreCase( parm.player20 )) || (parm.player15.equalsIgnoreCase( parm.player21 )) ||
          (parm.player15.equalsIgnoreCase( parm.player22 )) || (parm.player15.equalsIgnoreCase( parm.player23 )) ||
          (parm.player15.equalsIgnoreCase( parm.player24 )) || (parm.player15.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player15, parm);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player15 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player15, parm);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname15 = tok.nextToken();
         parm.lname15 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname15 = tok.nextToken();
         parm.mi15 = tok.nextToken();
         parm.lname15 = tok.nextToken();
      }
   }

   if (!parm.player16.equals( "" ) && !parm.player16.equalsIgnoreCase( "x" ) && parm.g[15].equals( "" )) {

      if ((parm.player16.equalsIgnoreCase( parm.player17 )) ||
          (parm.player16.equalsIgnoreCase( parm.player18 )) || (parm.player16.equalsIgnoreCase( parm.player19 )) ||
          (parm.player16.equalsIgnoreCase( parm.player20 )) || (parm.player16.equalsIgnoreCase( parm.player21 )) ||
          (parm.player16.equalsIgnoreCase( parm.player22 )) || (parm.player16.equalsIgnoreCase( parm.player23 )) ||
          (parm.player16.equalsIgnoreCase( parm.player24 )) || (parm.player16.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player16, parm);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player16 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player16, parm);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname16 = tok.nextToken();
         parm.lname16 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname16 = tok.nextToken();
         parm.mi16 = tok.nextToken();
         parm.lname16 = tok.nextToken();
      }
   }

   if (!parm.player17.equals( "" ) && !parm.player17.equalsIgnoreCase( "x" ) && parm.g[16].equals( "" )) {

      if ((parm.player17.equalsIgnoreCase( parm.player18 )) || (parm.player17.equalsIgnoreCase( parm.player19 )) ||
          (parm.player17.equalsIgnoreCase( parm.player20 )) || (parm.player17.equalsIgnoreCase( parm.player21 )) ||
          (parm.player17.equalsIgnoreCase( parm.player22 )) || (parm.player17.equalsIgnoreCase( parm.player23 )) ||
          (parm.player17.equalsIgnoreCase( parm.player24 )) || (parm.player17.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player17, parm);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player17 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player17, parm);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname17 = tok.nextToken();
         parm.lname17 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname17 = tok.nextToken();
         parm.mi17 = tok.nextToken();
         parm.lname17 = tok.nextToken();
      }
   }

   if (!parm.player18.equals( "" ) && !parm.player18.equalsIgnoreCase( "x" ) && parm.g[17].equals( "" )) {

      if ((parm.player18.equalsIgnoreCase( parm.player19 )) ||
          (parm.player18.equalsIgnoreCase( parm.player20 )) || (parm.player18.equalsIgnoreCase( parm.player21 )) ||
          (parm.player18.equalsIgnoreCase( parm.player22 )) || (parm.player18.equalsIgnoreCase( parm.player23 )) ||
          (parm.player18.equalsIgnoreCase( parm.player24 )) || (parm.player18.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player18, parm);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player18 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player18, parm);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname18 = tok.nextToken();
         parm.lname18 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname18 = tok.nextToken();
         parm.mi18 = tok.nextToken();
         parm.lname18 = tok.nextToken();
      }
   }

   if (!parm.player19.equals( "" ) && !parm.player19.equalsIgnoreCase( "x" ) && parm.g[18].equals( "" )) {

      if ((parm.player19.equalsIgnoreCase( parm.player20 )) || (parm.player19.equalsIgnoreCase( parm.player21 )) ||
          (parm.player19.equalsIgnoreCase( parm.player22 )) || (parm.player19.equalsIgnoreCase( parm.player23 )) ||
          (parm.player19.equalsIgnoreCase( parm.player24 )) || (parm.player19.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player19, parm);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player19 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player19, parm);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname19 = tok.nextToken();
         parm.lname19 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname19 = tok.nextToken();
         parm.mi19 = tok.nextToken();
         parm.lname19 = tok.nextToken();
      }
   }

   if (!parm.player20.equals( "" ) && !parm.player20.equalsIgnoreCase( "x" ) && parm.g[19].equals( "" )) {

      if ((parm.player20.equalsIgnoreCase( parm.player21 )) ||
          (parm.player20.equalsIgnoreCase( parm.player22 )) || (parm.player20.equalsIgnoreCase( parm.player23 )) ||
          (parm.player20.equalsIgnoreCase( parm.player24 )) || (parm.player20.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player20, parm);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player20 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player20, parm);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname20 = tok.nextToken();
         parm.lname20 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname20 = tok.nextToken();
         parm.mi20 = tok.nextToken();
         parm.lname20 = tok.nextToken();
      }
   }

   if (!parm.player21.equals( "" ) && !parm.player21.equalsIgnoreCase( "x" ) && parm.g[20].equals( "" )) {

      if ((parm.player21.equalsIgnoreCase( parm.player22 )) || (parm.player21.equalsIgnoreCase( parm.player23 )) ||
          (parm.player21.equalsIgnoreCase( parm.player24 )) || (parm.player21.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player21, parm);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player21 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player21, parm);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname21 = tok.nextToken();
         parm.lname21 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname21 = tok.nextToken();
         parm.mi21 = tok.nextToken();
         parm.lname21 = tok.nextToken();
      }
   }

   if (!parm.player22.equals( "" ) && !parm.player22.equalsIgnoreCase( "x" ) && parm.g[21].equals( "" )) {

      if ((parm.player22.equalsIgnoreCase( parm.player23 )) ||
          (parm.player22.equalsIgnoreCase( parm.player24 )) || (parm.player22.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player22, parm);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player22 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player22, parm);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname22 = tok.nextToken();
         parm.lname22 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname22 = tok.nextToken();
         parm.mi22 = tok.nextToken();
         parm.lname22 = tok.nextToken();
      }
   }

   if (!parm.player23.equals( "" ) && !parm.player23.equalsIgnoreCase( "x" ) && parm.g[22].equals( "" )) {

      if ((parm.player23.equalsIgnoreCase( parm.player24 )) || (parm.player23.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player23, parm);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player23 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player23, parm);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname23 = tok.nextToken();
         parm.lname23 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname23 = tok.nextToken();
         parm.mi23 = tok.nextToken();
         parm.lname23 = tok.nextToken();
      }
   }

   if (!parm.player24.equals( "" ) && !parm.player24.equalsIgnoreCase( "x" ) && parm.g[23].equals( "" )) {

      if ((parm.player24.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player24, parm);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player24 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player24, parm);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname24 = tok.nextToken();
         parm.lname24 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname24 = tok.nextToken();
         parm.mi24 = tok.nextToken();
         parm.lname24 = tok.nextToken();
      }
   }

   if (!parm.player25.equals( "" ) && !parm.player25.equalsIgnoreCase( "x" ) && parm.g[24].equals( "" )) {

      StringTokenizer tok = new StringTokenizer( parm.player25 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player25, parm);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname25 = tok.nextToken();
         parm.lname25 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname25 = tok.nextToken();
         parm.mi25 = tok.nextToken();
         parm.lname25 = tok.nextToken();
      }
   }
   return(error);
 }

 // *******************************************************************************
 //  Get Member UserNames
 // *******************************************************************************
 //
 private boolean getUsers(PrintWriter out, parmLott parm, Connection con) {


   ResultSet rs = null;

   boolean error = false;

   int members = 0;

   //
   //  Get the usernames, membership types, etc. for players if matching name found
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
         "SELECT username, m_ship, m_type, memNum FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ?");

      if ((!parm.fname1.equals( "" )) && (!parm.lname1.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname1);
         pstmt1.setString(2, parm.fname1);
         pstmt1.setString(3, parm.mi1);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user1 = rs.getString(1);
            parm.mship1 = rs.getString(2);
            parm.mtype1 = rs.getString(3);
            parm.mNum1 = rs.getString(4);

            members++;         // increment number of members this request
            parm.memg1++;           // increment number of members this group/slot

         } else {
            parm.inval1 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname2.equals( "" )) && (!parm.lname2.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname2);
         pstmt1.setString(2, parm.fname2);
         pstmt1.setString(3, parm.mi2);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user2 = rs.getString(1);
            parm.mship2 = rs.getString(2);
            parm.mtype2 = rs.getString(3);
            parm.mNum2 = rs.getString(4);

            members++;         // increment number of members this request
            parm.memg1++;           // increment number of members this group/slot
           
         } else {
            parm.inval2 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname3.equals( "" )) && (!parm.lname3.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname3);
         pstmt1.setString(2, parm.fname3);
         pstmt1.setString(3, parm.mi3);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user3 = rs.getString(1);
            parm.mship3 = rs.getString(2);
            parm.mtype3 = rs.getString(3);
            parm.mNum3 = rs.getString(4);

            members++;         // increment number of members this request
            parm.memg1++;           // increment number of members this group/slot

         } else {
            parm.inval3 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname4.equals( "" )) && (!parm.lname4.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname4);
         pstmt1.setString(2, parm.fname4);
         pstmt1.setString(3, parm.mi4);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user4 = rs.getString(1);
            parm.mship4 = rs.getString(2);
            parm.mtype4 = rs.getString(3);
            parm.mNum4 = rs.getString(4);

            members++;         // increment number of members this request
            parm.memg1++;           // increment number of members this group/slot

         } else {
            parm.inval4 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname5.equals( "" )) && (!parm.lname5.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname5);
         pstmt1.setString(2, parm.fname5);
         pstmt1.setString(3, parm.mi5);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user5 = rs.getString(1);
            parm.mship5 = rs.getString(2);
            parm.mtype5 = rs.getString(3);
            parm.mNum5 = rs.getString(4);

            members++;         // increment number of members this request
            if (parm.p5.equals( "Yes" )) {
               parm.memg1++;           // increment number of members this group/slot
            } else {
               parm.memg2++;           // increment number of members this group/slot
            }
         } else {
            parm.inval5 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname6.equals( "" )) && (!parm.lname6.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname6);
         pstmt1.setString(2, parm.fname6);
         pstmt1.setString(3, parm.mi6);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user6 = rs.getString(1);
            parm.mship6 = rs.getString(2);
            parm.mtype6 = rs.getString(3);
            parm.mNum6 = rs.getString(4);

            members++;         // increment number of members this request
            parm.memg2++;           // increment number of members this group/slot
   
         } else {
            parm.inval6 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname7.equals( "" )) && (!parm.lname7.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname7);
         pstmt1.setString(2, parm.fname7);
         pstmt1.setString(3, parm.mi7);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user7 = rs.getString(1);
            parm.mship7 = rs.getString(2);
            parm.mtype7 = rs.getString(3);
            parm.mNum7 = rs.getString(4);

            members++;         // increment number of members this request
            parm.memg2++;           // increment number of members this group/slot

         } else {
            parm.inval7 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname8.equals( "" )) && (!parm.lname8.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname8);
         pstmt1.setString(2, parm.fname8);
         pstmt1.setString(3, parm.mi8);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user8 = rs.getString(1);
            parm.mship8 = rs.getString(2);
            parm.mtype8 = rs.getString(3);
            parm.mNum8 = rs.getString(4);

            members++;         // increment number of members this request
            parm.memg2++;           // increment number of members this group/slot

         } else {
            parm.inval8 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname9.equals( "" )) && (!parm.lname9.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname9);
         pstmt1.setString(2, parm.fname9);
         pstmt1.setString(3, parm.mi9);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user9 = rs.getString(1);
            parm.mship9 = rs.getString(2);
            parm.mtype9 = rs.getString(3);
            parm.mNum9 = rs.getString(4);

            members++;         // increment number of members this request
            if (parm.p5.equals( "Yes" )) {
               parm.memg2++;           // increment number of members this group/slot
            } else {
               parm.memg3++;           // increment number of members this group/slot
            }
         } else {
            parm.inval9 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname10.equals( "" )) && (!parm.lname10.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname10);
         pstmt1.setString(2, parm.fname10);
         pstmt1.setString(3, parm.mi10);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user10 = rs.getString(1);
            parm.mship10 = rs.getString(2);
            parm.mtype10 = rs.getString(3);
            parm.mNum10 = rs.getString(4);

            members++;         // increment number of members this request
            if (parm.p5.equals( "Yes" )) {
               parm.memg2++;           // increment number of members this group/slot
            } else {
               parm.memg3++;           // increment number of members this group/slot
            }
         } else {
            parm.inval10 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname11.equals( "" )) && (!parm.lname11.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname11);
         pstmt1.setString(2, parm.fname11);
         pstmt1.setString(3, parm.mi11);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user11 = rs.getString(1);
            parm.mship11 = rs.getString(2);
            parm.mtype11 = rs.getString(3);
            parm.mNum11 = rs.getString(4);

            members++;         // increment number of members this request
            parm.memg3++;           // increment number of members this group/slot
           
         } else {
            parm.inval11 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname12.equals( "" )) && (!parm.lname12.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname12);
         pstmt1.setString(2, parm.fname12);
         pstmt1.setString(3, parm.mi12);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user12 = rs.getString(1);
            parm.mship12 = rs.getString(2);
            parm.mtype12 = rs.getString(3);
            parm.mNum12 = rs.getString(4);

            members++;         // increment number of members this request
            parm.memg3++;           // increment number of members this group/slot

         } else {
            parm.inval12 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname13.equals( "" )) && (!parm.lname13.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname13);
         pstmt1.setString(2, parm.fname13);
         pstmt1.setString(3, parm.mi13);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user13 = rs.getString(1);
            parm.mship13 = rs.getString(2);
            parm.mtype13 = rs.getString(3);
            parm.mNum13 = rs.getString(4);

            members++;         // increment number of members this request
            if (parm.p5.equals( "Yes" )) {
               parm.memg3++;           // increment number of members this group/slot
            } else {
               parm.memg4++;           // increment number of members this group/slot
            }
         } else {
            parm.inval13 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname14.equals( "" )) && (!parm.lname14.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname14);
         pstmt1.setString(2, parm.fname14);
         pstmt1.setString(3, parm.mi14);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user14 = rs.getString(1);
            parm.mship14 = rs.getString(2);
            parm.mtype14 = rs.getString(3);
            parm.mNum14 = rs.getString(4);

            members++;         // increment number of members this res.
            if (parm.p5.equals( "Yes" )) {
               parm.memg3++;           // increment number of members this group/slot
            } else {
               parm.memg4++;           // increment number of members this group/slot
            }
         } else {
            parm.inval14 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname15.equals( "" )) && (!parm.lname15.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname15);
         pstmt1.setString(2, parm.fname15);
         pstmt1.setString(3, parm.mi15);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user15 = rs.getString(1);
            parm.mship15 = rs.getString(2);
            parm.mtype15 = rs.getString(3);
            parm.mNum15 = rs.getString(4);

            members++;         // increment number of members this res.
            if (parm.p5.equals( "Yes" )) {
               parm.memg3++;           // increment number of members this group/slot
            } else {
               parm.memg4++;           // increment number of members this group/slot
            }
         } else {
            parm.inval15 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname16.equals( "" )) && (!parm.lname16.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname16);
         pstmt1.setString(2, parm.fname16);
         pstmt1.setString(3, parm.mi16);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user16 = rs.getString(1);
            parm.mship16 = rs.getString(2);
            parm.mtype16 = rs.getString(3);
            parm.mNum16 = rs.getString(4);

            members++;         // increment number of members this res.
            parm.memg4++;           // increment number of members this group/slot
           
         } else {
            parm.inval16 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname17.equals( "" )) && (!parm.lname17.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname17);
         pstmt1.setString(2, parm.fname17);
         pstmt1.setString(3, parm.mi17);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user17 = rs.getString(1);
            parm.mship17 = rs.getString(2);
            parm.mtype17 = rs.getString(3);
            parm.mNum17 = rs.getString(4);

            members++;         // increment number of members this res.
            if (parm.p5.equals( "Yes" )) {
               parm.memg4++;           // increment number of members this group/slot
            } else {
               parm.memg5++;           // increment number of members this group/slot
            }
         } else {
            parm.inval17 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname18.equals( "" )) && (!parm.lname18.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname18);
         pstmt1.setString(2, parm.fname18);
         pstmt1.setString(3, parm.mi18);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user18 = rs.getString(1);
            parm.mship18 = rs.getString(2);
            parm.mtype18 = rs.getString(3);
            parm.mNum18 = rs.getString(4);

            members++;         // increment number of members this res.
            if (parm.p5.equals( "Yes" )) {
               parm.memg4++;           // increment number of members this group/slot
            } else {
               parm.memg5++;           // increment number of members this group/slot
            }
         } else {
            parm.inval18 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname19.equals( "" )) && (!parm.lname19.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname19);
         pstmt1.setString(2, parm.fname19);
         pstmt1.setString(3, parm.mi19);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user19 = rs.getString(1);
            parm.mship19 = rs.getString(2);
            parm.mtype19 = rs.getString(3);
            parm.mNum19 = rs.getString(4);

            members++;         // increment number of members this res.
            if (parm.p5.equals( "Yes" )) {
               parm.memg4++;           // increment number of members this group/slot
            } else {
               parm.memg5++;           // increment number of members this group/slot
            }
         } else {
            parm.inval19 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname20.equals( "" )) && (!parm.lname20.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname20);
         pstmt1.setString(2, parm.fname20);
         pstmt1.setString(3, parm.mi20);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user20 = rs.getString(1);
            parm.mship20 = rs.getString(2);
            parm.mtype20 = rs.getString(3);
            parm.mNum20 = rs.getString(4);

            members++;         // increment number of members this res.
            if (parm.p5.equals( "Yes" )) {
               parm.memg4++;           // increment number of members this group/slot
            } else {
               parm.memg5++;           // increment number of members this group/slot
            }
         } else {
            parm.inval20 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname21.equals( "" )) && (!parm.lname21.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname21);
         pstmt1.setString(2, parm.fname21);
         pstmt1.setString(3, parm.mi21);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user21 = rs.getString(1);
            parm.mship21 = rs.getString(2);
            parm.mtype21 = rs.getString(3);
            parm.mNum21 = rs.getString(4);

            members++;         // increment number of members this res.
            parm.memg5++;           // increment number of members this group/slot
           
         } else {
            parm.inval21 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname22.equals( "" )) && (!parm.lname22.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname22);
         pstmt1.setString(2, parm.fname22);
         pstmt1.setString(3, parm.mi22);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user22 = rs.getString(1);
            parm.mship22 = rs.getString(2);
            parm.mtype22 = rs.getString(3);
            parm.mNum22 = rs.getString(4);

            members++;         // increment number of members this res.
            parm.memg5++;           // increment number of members this group/slot

         } else {
            parm.inval22 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname23.equals( "" )) && (!parm.lname23.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname23);
         pstmt1.setString(2, parm.fname23);
         pstmt1.setString(3, parm.mi23);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user23 = rs.getString(1);
            parm.mship23 = rs.getString(2);
            parm.mtype23 = rs.getString(3);
            parm.mNum23 = rs.getString(4);

            members++;         // increment number of members this res.
            parm.memg5++;           // increment number of members this group/slot

         } else {
            parm.inval23 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname24.equals( "" )) && (!parm.lname24.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname24);
         pstmt1.setString(2, parm.fname24);
         pstmt1.setString(3, parm.mi24);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user24 = rs.getString(1);
            parm.mship24 = rs.getString(2);
            parm.mtype24 = rs.getString(3);
            parm.mNum24 = rs.getString(4);

            members++;         // increment number of members this res.
            parm.memg5++;           // increment number of members this group/slot

         } else {
            parm.inval24 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname25.equals( "" )) && (!parm.lname25.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname25);
         pstmt1.setString(2, parm.fname25);
         pstmt1.setString(3, parm.mi25);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user25 = rs.getString(1);
            parm.mship25 = rs.getString(2);
            parm.mtype25 = rs.getString(3);
            parm.mNum25 = rs.getString(4);

            members++;         // increment number of members this res.
            parm.memg5++;           // increment number of members this group/slot

         } else {
            parm.inval25 = 1;        // indicate invalid name entered
         }
      }

      pstmt1.close();

   }
   catch (Exception ignore) {
   }

   parm.members = members;           // save member count

   return(error);
 }


 // *******************************************************************************
 //  Check membership restrictions - max rounds per week, month or year
 // *******************************************************************************
 //
 private boolean checkMemship(Connection con, PrintWriter out, parmLott parm, String day) {


   ResultSet rs = null;

   boolean check = false;
   parm.error = false;               // init

   //String rest_name = "";
   //String rest_recurr = "";
   //String rest_course = "";
   //String rest_fb = "";
   //String sfb = "";
   String mship = "";
   String player = "";
   String period = "";
   String mperiod = "";
   //String course = parm.course;

   //int rest_stime = 0;
   //int rest_etime = 0;
   //int mems = 0;
   int mtimes = 0;
   int ind = 0;
   int i = 0;
   //int time = parm.time;
   int year = 0;
   int month = 0;
   int dayNum = 0;
   int count = 0;
   int mm = parm.mm;
   int yy = parm.yy;
   int dd = parm.dd;

   long date = parm.date;
   long dateEnd = 0;
   long dateStart = 0;

   //
   //  parm block to hold the club parameters
   //
   parmClub parmc = new parmClub(0, con); // golf only feature

   int [] mtimesA = new int [parmc.MAX_Mships+1];          // array to hold the membership time values
   String [] mshipA = new String [parmc.MAX_Mships+1];     // array to hold the membership names
   String [] periodA = new String [parmc.MAX_Mships+1];    // array to hold the membership periods

   //
   // Init the arrays
   //
   for (i=0; i<parmc.MAX_Mships+1; i++) {
       mtimesA[i] = 0;
       mshipA[i] = "";
       periodA[i] = "";
   }

    //
    //  Get this date's calendar and then determine start and end of week.
    //
    int calmm = mm - 1;                            // adjust month value for cal

    Calendar cal = new GregorianCalendar();       // get todays date

    //
    //  set cal to tee time's date
    //
    cal.set(Calendar.YEAR,yy);               // set year in cal
    cal.set(Calendar.MONTH,calmm);                // set month in cal
    cal.set(Calendar.DAY_OF_MONTH,dd);       // set day in cal

    ind = cal.get(Calendar.DAY_OF_WEEK);          // day of week (01 - 07)
    ind = 7 - ind;                                // number of days to end of week

    //
    // roll cal ahead to find Saturday's date (end of week)
    //
    if (ind != 0) {                               // if not today

       cal.add(Calendar.DATE,ind);                // roll ahead (ind) days
    }

    year = cal.get(Calendar.YEAR);
    month = cal.get(Calendar.MONTH);
    dayNum = cal.get(Calendar.DAY_OF_MONTH);

    month = month + 1;                            // month starts at zero

    dateEnd = year * 10000;                       // create a date field of yyyymmdd
    dateEnd = dateEnd + (month * 100);
    dateEnd = dateEnd + dayNum;                      // date = yyyymmdd (for comparisons)

    //
    // roll cal back 6 days to find Sunday's date (start of week)
    //
    cal.add(Calendar.DATE,-6);                    // roll back 6 days

    year = cal.get(Calendar.YEAR);
    month = cal.get(Calendar.MONTH);
    dayNum = cal.get(Calendar.DAY_OF_MONTH);

    month = month + 1;                            // month starts at zero

    dateStart = year * 10000;                     // create a date field of yyyymmdd
    dateStart = dateStart + (month * 100);
    dateStart = dateStart + dayNum;                  // date = yyyymmdd (for comparisons)

    //
    //  Get membership types, number of rounds and time periods (week, month, year)
    //
    try {

       Statement stmt = con.createStatement();

       rs = stmt.executeQuery("SELECT mship, mtimes, period FROM mship5 WHERE activity_id = 0 LIMIT " + parmc.MAX_Mships);

       i = 1;

       while (rs.next()) {

           mshipA[i] = rs.getString("mship");
           mtimesA[i] = rs.getInt("mtimes");
           periodA[i] = rs.getString("period");

           i++;
       }

       stmt.close();
           
       //
       //   Check each player's mship
       //
       if (!parm.mship1.equals( "" )) {          // check if player 1 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop1:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship1.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop1;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user1, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship1;
                player = parm.player1;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 1 if

       if (!parm.mship2.equals( "" )) {          // check if player 2 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop2:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship2.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop2;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user2, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship2;
                player = parm.player2;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 2 if

       if (!parm.mship3.equals( "" )) {          // check if player 3 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop3:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship3.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop3;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user3, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship3;
                player = parm.player3;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 3 if

       if (!parm.mship4.equals( "" )) {          // check if player 4 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop4:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship4.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop4;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user4, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship4;
                player = parm.player4;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 4 if

       if (!parm.mship5.equals( "" )) {          // check if player 5 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop5:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship5.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop5;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user5, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship5;
                player = parm.player5;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 5 if

       if (!parm.mship6.equals( "" )) {          // check if player 6 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop6:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship6.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop6;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user6, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship6;
                player = parm.player6;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 6 if

       if (!parm.mship7.equals( "" )) {          // check if player 7 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop7:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship7.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop7;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user7, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship7;
                player = parm.player7;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 7 if

       if (!parm.mship8.equals( "" )) {          // check if player 8 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop8:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship8.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop8;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user8, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship8;
                player = parm.player8;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 8 if

       if (!parm.mship9.equals( "" )) {          // check if player 9 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop9:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship9.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop9;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user9, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship9;
                player = parm.player9;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 9 if

       if (!parm.mship10.equals( "" )) {          // check if player 10 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop10:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship10.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop10;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user10, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship10;
                player = parm.player10;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 10 if

       if (!parm.mship11.equals( "" )) {          // check if player 11 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop11:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship11.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop11;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user11, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship11;
                player = parm.player11;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 11 if

       if (!parm.mship12.equals( "" )) {          // check if player 12 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop12:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship12.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop12;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user12, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship12;
                player = parm.player12;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 12 if

       if (!parm.mship13.equals( "" )) {          // check if player 13 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop13:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship13.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop13;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user13, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship13;
                player = parm.player13;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 13 if

       if (!parm.mship14.equals( "" )) {          // check if player 14 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop14:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship14.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop14;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user14, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship14;
                player = parm.player14;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 14 if

       if (!parm.mship15.equals( "" )) {          // check if player 15 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop15:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship15.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop15;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user15, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship15;
                player = parm.player15;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 15 if

       if (!parm.mship16.equals( "" )) {          // check if player 16 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop16:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship16.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop16;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user16, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship16;
                player = parm.player16;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 16 if

       if (!parm.mship17.equals( "" )) {          // check if player 17 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop17:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship17.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop17;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user17, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship17;
                player = parm.player17;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 17 if

       if (!parm.mship18.equals( "" )) {          // check if player 18 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop18:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship18.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop18;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user18, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship18;
                player = parm.player18;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 18 if

       if (!parm.mship19.equals( "" )) {          // check if player 19 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop19:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship19.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop19;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user19, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship19;
                player = parm.player19;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 19 if

       if (!parm.mship20.equals( "" )) {          // check if player 20 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop20:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship20.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop20;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user20, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship20;
                player = parm.player20;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 20 if

       if (!parm.mship21.equals( "" )) {          // check if player 21 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop21:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship21.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop21;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user21, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship21;
                player = parm.player21;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 21 if

       if (!parm.mship22.equals( "" )) {          // check if player 22 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop22:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship22.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop22;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user22, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship22;
                player = parm.player22;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 22 if

       if (!parm.mship23.equals( "" )) {          // check if player 23 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop23:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship23.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop23;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user23, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship23;
                player = parm.player23;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 23 if

       if (!parm.mship24.equals( "" )) {          // check if player 24 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop24:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship24.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop24;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user24, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship24;
                player = parm.player24;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 24 if

       if (!parm.mship25.equals( "" )) {          // check if player 25 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop25:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship25.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop25;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user25, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship25;
                player = parm.player25;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 25 if

   }
   catch (Exception e7) {

     dbError(out, e7);
     parm.error = true;               // inform caller of error
   }

   //
   //  save parms if error
   //
   parm.player = player;
   parm.mship= mship;
   parm.period = period;
     
   return(check);

 }         // end of checkMemRes



 // *******************************************************************************
 //  Check member restrictions
 //
 //     First, find all restrictions within date & time constraints on this course.
 //     Then, find the ones for this day.
 //     Then, find any for this member type or membership type (all 25 possible players).
 //
 // *******************************************************************************
 //
 private boolean checkMemRes(Connection con, PrintWriter out, parmLott parm, String day) {


   ResultSet rs = null;

   boolean check = false;
   parm.error = false;               // init

   String rest_name = "";
   String rest_recurr = "";
   //String rest_course = "";
   String rest_fb = "";
   String sfb = "";
   String player = "";
   String course = parm.course;

   //int rest_stime = 0;
   //int rest_etime = 0;
   //int mems = 0;
   int ind = 0;
   int time = parm.time;
   int mrest_id = 0;

   long date = parm.date;

   String [] mtypeA = new String [8];     // array to hold the member type names
   String [] mshipA = new String [8];     // array to hold the membership names

   try {

      PreparedStatement pstmt7 = con.prepareStatement (
         "SELECT * FROM restriction2 WHERE sdate <= ? AND edate >= ? AND " +
         "stime <= ? AND etime >= ? AND (courseName = ? OR courseName = '-ALL-')");


      pstmt7.clearParameters();          // clear the parms
      pstmt7.setLong(1, date);
      pstmt7.setLong(2, date);
      pstmt7.setInt(3, time);
      pstmt7.setInt(4, time);
      pstmt7.setString(5, course);

      rs = pstmt7.executeQuery();      // find all matching restrictions, if any

      check = false;                     // init 'hit' flag

      if (parm.fb == 0) {                   // is Tee time for Front 9?

         sfb = "Front";
      }

      if (parm.fb == 1) {                   // is it Back 9?

         sfb = "Back";
      }

      loop2:
      while (rs.next()) {              // check all matching restrictions for this day, mship, mtype & F/B

         rest_name = rs.getString("name");
         rest_recurr = rs.getString("recurr");
         mrest_id = rs.getInt("id");
         mtypeA[0] = rs.getString("mem1");
         mtypeA[1] = rs.getString("mem2");
         mtypeA[2] = rs.getString("mem3");
         mtypeA[3] = rs.getString("mem4");
         mtypeA[4] = rs.getString("mem5");
         mtypeA[5] = rs.getString("mem6");
         mtypeA[6] = rs.getString("mem7");
         mtypeA[7] = rs.getString("mem8");
         mshipA[0] = rs.getString("mship1");
         mshipA[1] = rs.getString("mship2");
         mshipA[2] = rs.getString("mship3");
         mshipA[3] = rs.getString("mship4");
         mshipA[4] = rs.getString("mship5");
         mshipA[5] = rs.getString("mship6");
         mshipA[6] = rs.getString("mship7");
         mshipA[7] = rs.getString("mship8");
         rest_fb = rs.getString("fb");

         //
         //  We must check the recurrence for this day (Monday, etc.)
         //
         if ((rest_recurr.equals( "Every " + day )) ||               // if this day
             (rest_recurr.equalsIgnoreCase( "every day" )) ||        // or everyday
             ((rest_recurr.equalsIgnoreCase( "all weekdays" )) &&    // or all weekdays (and this is one)
               (!day.equalsIgnoreCase( "saturday" )) &&
               (!day.equalsIgnoreCase( "sunday" ))) ||
             ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&    // or all weekends (and this is one)
              (day.equalsIgnoreCase( "saturday" ))) ||
             ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&
              (day.equalsIgnoreCase( "sunday" )))) {

            //
            //  Now check if F/B matches
            //
            if ((rest_fb.equals( "Both" )) || (rest_fb.equals( sfb ))) {

               check = false;
               
               //
               // Make sure restriction isn't suspended
               //
               if (!verifySlot.checkRestSuspend(mrest_id, -99, 0, (int)parm.date, parm.time, parm.day, parm.course, con)) {

                   //
                   //  Found a restriction that matches date, time, day & F/B - check mtype & mship of each member player
                   //
                   if (!parm.mship1.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player1;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship1.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype1.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 1 restrictions if

                   if (!parm.mship2.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player2;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship2.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype2.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 2 restrictions if

                   if (!parm.mship3.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player3;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship3.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype3.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 3 restrictions if

                   if (!parm.mship4.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player4;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship4.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype4.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 4 restrictions if

                   if (!parm.mship5.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player5;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship5.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype5.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 5 restrictions if

                   if (!parm.mship6.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player6;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship6.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype6.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 6 restrictions if

                   if (!parm.mship7.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player7;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship7.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype7.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 7 restrictions if

                   if (!parm.mship8.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player8;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship8.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype8.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 8 restrictions if

                   if (!parm.mship9.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player9;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship9.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype9.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 9 restrictions if

                   if (!parm.mship10.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player10;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship10.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype10.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 10 restrictions if

                   if (!parm.mship11.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player11;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship11.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype11.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 11 restrictions if

                   if (!parm.mship12.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player12;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship12.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype12.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 12 restrictions if

                   if (!parm.mship13.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player13;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship13.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype13.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 13 restrictions if

                   if (!parm.mship14.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player14;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship14.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype14.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 14 restrictions if

                   if (!parm.mship15.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player15;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship15.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype15.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 15 restrictions if

                   if (!parm.mship16.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player16;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship16.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype16.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 16 restrictions if

                   if (!parm.mship17.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player17;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship17.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype17.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 17 restrictions if

                   if (!parm.mship18.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player18;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship18.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype18.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 18 restrictions if

                   if (!parm.mship19.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player19;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship19.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype19.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 19 restrictions if

                   if (!parm.mship20.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player20;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship20.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype20.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 20 restrictions if

                   if (!parm.mship21.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player21;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship21.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype21.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 21 restrictions if

                   if (!parm.mship22.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player22;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship22.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype22.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 22 restrictions if

                   if (!parm.mship23.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player23;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship23.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype23.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 23 restrictions if

                   if (!parm.mship24.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player24;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship24.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype24.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 24 restrictions if

                   if (!parm.mship25.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player25;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship25.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype25.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 25 restrictions if
               }
            }     // end of IF F/B matches
         }     // end of 'day' if
      }       // end of while (no more restrictions)

      pstmt7.close();

   }
   catch (Exception e7) {

      dbError(out, e7);
      parm.error = true;               // inform caller of error
   }                

   //
   //  save parms if error
   //
   parm.player = player;
   parm.rest_name = rest_name;

   return(check);

 }         // end of checkMemRes


 // *******************************************************************************
 //  Check Member Number restrictions
 //
 //     First, find all restrictions within date & time constraints
 //     Then, find the ones for this day
 //     Then, check all players' member numbers against all others in the time period
 //
 // *******************************************************************************
 //
 private boolean checkMemNum(Connection con, PrintWriter out, parmLott parm, String day) {


   ResultSet rs = null;

   boolean check = false;
   parm.error = false;               // init

   String rest_name = "";
   String rest_recurr = "";
   String rest_course = "";
   String rest_fb = "";
   String sfb = "";
   String course = parm.course;
     
   int rest_stime = 0;
   int rest_etime = 0;
   int mems = 0;
   int ind = 0;
   int time = parm.time;
         
   long date = parm.date;


   try {

      PreparedStatement pstmt7b = con.prepareStatement (
         "SELECT name, stime, etime, recurr, courseName, fb, num_mems " +
         "FROM mnumres2 WHERE sdate <= ? AND edate >= ? AND " +
         "stime <= ? AND etime >= ? AND (courseName = ? OR courseName = '-ALL-')");


      pstmt7b.clearParameters();          // clear the parms
      pstmt7b.setLong(1, date);
      pstmt7b.setLong(2, date);
      pstmt7b.setInt(3, time);
      pstmt7b.setInt(4, time);
      pstmt7b.setString(5, course);

      rs = pstmt7b.executeQuery();      // find all matching restrictions, if any

      check = false;                    // init 'hit' flag
      ind = 0;                          // init matching member count

      if (parm.fb == 0) {                    // is Tee time for Front 9?

         sfb = "Front";
      }

      if (parm.fb == 1) {                    // is it Back 9?

         sfb = "Back";
      }

      loop3:
      while (rs.next()) {              // check all matching restrictions for this day & F/B

         rest_name = rs.getString("name");
         rest_stime = rs.getInt("stime");
         rest_etime = rs.getInt("etime");
         rest_recurr = rs.getString("recurr");
         rest_course = rs.getString("courseName");
         rest_fb = rs.getString("fb");
         mems = rs.getInt("num_mems");

         //
         //  We must check the recurrence for this day (Monday, etc.)
         //
         if ((rest_recurr.equals( "Every " + day )) ||               // if this day
             (rest_recurr.equalsIgnoreCase( "every day" )) ||        // or everyday
             ((rest_recurr.equalsIgnoreCase( "all weekdays" )) &&    // or all weekdays (and this is one)
               (!day.equalsIgnoreCase( "saturday" )) &&
               (!day.equalsIgnoreCase( "sunday" ))) ||
             ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&    // or all weekends (and this is one)
              (day.equalsIgnoreCase( "saturday" ))) ||
             ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&
              (day.equalsIgnoreCase( "sunday" )))) {

            //
            //  Now check if F/B matches this tee time
            //
            if (rest_fb.equals( "Both" ) || rest_fb.equals( sfb )) {

               //
               //  Found a restriction that matches date, time, day, course & F/B - check each member player
               //
               //   Check Player 1
               //
               if (!parm.mNum1.equals( "" )) {           // if this player is a member and member number exists

                  ind = checkmNum(parm.mNum1, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum1 = parm.player1;  // save this player name for error msg

                  if (parm.mNum1.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }

               }  // end of member 1 restrictions if

               //
               //   Check Player 2
               //
               if ((check == false) && (!parm.mNum2.equals( "" ))) {   // if this player is a member

                  ind = checkmNum(parm.mNum2, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum2 = parm.player2;  // save this player name for error msg

                  if (parm.mNum2.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }

               }  // end of member 2 restrictions if

               //
               //   Check Player 3
               //
               if ((check == false) && (!parm.mNum3.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum3, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum3 = parm.player3;  // save this player name for error msg

                  if (parm.mNum3.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }

               }  // end of member 3 restrictions if

               //
               //   Check Player 4
               //
               if ((check == false) && (!parm.mNum4.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum4, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum4 = parm.player4;  // save this player name for error msg

                  if (parm.mNum4.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 4 restrictions if

               //
               //   Check Player 5
               //
               if ((check == false) && (!parm.mNum5.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum5, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum5 = parm.player5;  // save this player name for error msg

                  if (parm.mNum5.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 5 restrictions if

               //
               //   Check Player 6
               //
               if ((check == false) && (!parm.mNum6.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum6, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum6 = parm.player6;  // save this player name for error msg

                  if (parm.mNum6.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 6 restrictions if

               //
               //   Check player 7
               //
               if ((check == false) && (!parm.mNum7.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum7, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum7 = parm.player7;  // save this player name for error msg

                  if (parm.mNum7.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 7 restrictions if

               //
               //   Check Player 8
               //
               if ((check == false) && (!parm.mNum8.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum8, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum8 = parm.player8;  // save this player name for error msg

                  if (parm.mNum8.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 8 restrictions if

               //
               //   Check Player 9
               //
               if ((check == false) && (!parm.mNum9.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum9, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum9 = parm.player9;  // save this player name for error msg

                  if (parm.mNum9.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 9 restrictions if

               //
               //   Check Player 10
               //
               if ((check == false) && (!parm.mNum10.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum10, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum10 = parm.player10;  // save this player name for error msg

                  if (parm.mNum10.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 10 restrictions if

               //
               //   Check Player 11
               //
               if ((check == false) && (!parm.mNum11.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum11, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum11 = parm.player11;  // save this player name for error msg

                  if (parm.mNum11.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 11 restrictions if

               //
               //   Check Player 12
               //
               if ((check == false) && (!parm.mNum12.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum12, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum12 = parm.player12;  // save this player name for error msg

                  if (parm.mNum12.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 12 restrictions if

               //
               //   Check Player 13
               //
               if ((check == false) && (!parm.mNum13.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum13, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum13 = parm.player13;  // save this player name for error msg

                  if (parm.mNum13.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 13 restrictions if

               //
               //   Check Player 14
               //
               if ((check == false) && (!parm.mNum14.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum14, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum14 = parm.player14;  // save this player name for error msg

                  if (parm.mNum14.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 14 restrictions if

               //
               //   Check Player 15
               //
               if ((check == false) && (!parm.mNum15.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum15, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum15 = parm.player15;  // save this player name for error msg

                  if (parm.mNum15.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 15 restrictions if

               //
               //   Check Player 16
               //
               if ((check == false) && (!parm.mNum16.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum16, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum16 = parm.player16;  // save this player name for error msg

                  if (parm.mNum16.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 16 restrictions if

               //
               //   Check player 17
               //
               if ((check == false) && (!parm.mNum17.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum17, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum17 = parm.player17;  // save this player name for error msg

                  if (parm.mNum17.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 17 restrictions if

               //
               //   Check Player 18
               //
               if ((check == false) && (!parm.mNum18.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum18, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum18 = parm.player18;  // save this player name for error msg

                  if (parm.mNum18.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 18 restrictions if

               //
               //   Check Player 19
               //
               if ((check == false) && (!parm.mNum19.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum19, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum19 = parm.player19;  // save this player name for error msg

                  if (parm.mNum19.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 19 restrictions if

               //
               //   Check Player 20
               //
               if ((check == false) && (!parm.mNum20.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum20, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum20 = parm.player20;  // save this player name for error msg

                  if (parm.mNum20.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 20 restrictions if

               //
               //   Check Player 21
               //
               if ((check == false) && (!parm.mNum21.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum21, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum21 = parm.player21;  // save this player name for error msg

                  if (parm.mNum21.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 21 restrictions if

               //
               //   Check Player 22
               //
               if ((check == false) && (!parm.mNum22.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum22, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum22 = parm.player22;  // save this player name for error msg

                  if (parm.mNum22.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 22 restrictions if

               //
               //   Check Player 23
               //
               if ((check == false) && (!parm.mNum23.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum23, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum23 = parm.player23;  // save this player name for error msg

                  if (parm.mNum23.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 23 restrictions if

               //
               //   Check Player 24
               //
               if ((check == false) && (!parm.mNum24.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum24, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum24 = parm.player24;  // save this player name for error msg

                  if (parm.mNum24.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;    // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }       // end of member 24 restrictions if

               //
               //   Check Player 25
               //
               if ((check == false) && (!parm.mNum25.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum25, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum25 = parm.player25;  // save this player name for error msg

                  if (parm.mNum25.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 25 restrictions if

               if (check == true ) {          // if restriction hit

                  break loop3;
               }
            }     // end of IF F/B matches
         }     // end of 'day' if
      }       // end of while (no more restrictions)

      pstmt7b.close();

   }
   catch (Exception e7) {

      dbError(out, e7);
      parm.error = true;               // inform caller of error
   }
     
   //
   //  save parms if error
   //
   parm.rest_name = rest_name;
         
   return(check);
 }          // end of member restriction tests


 // *******************************************************************************
 //  Check custom Cherry Hills member restrictions
 // *******************************************************************************
 //
 private boolean checkCherryRes(parmLott parm) {


   boolean error = false;
   boolean go = false;

   //
   //  Allocate a new parm block for each tee time and call common method to process each.
   //
   parmSlot parm1 = new parmSlot();          // allocate a parm block for a single tee time

   //
   //  Setup the new single parm block
   //
   parm1.date = parm.date;
   parm1.time = parm.time;
   parm1.mm = parm.mm;
   parm1.yy = parm.yy;
   parm1.dd = parm.dd;
   parm1.course = parm.course;
   parm1.p5 = parm.p5;
   parm1.day = parm.day;
   parm1.oldPlayer1 = "";       // always empty from here
   parm1.oldPlayer2 = "";
   parm1.oldPlayer3 = "";
   parm1.oldPlayer4 = "";
   parm1.oldPlayer5 = "";
   parm1.fb = parm.fb;
   parm1.ind = parm.ind;      // index value
   parm1.sfb = parm.sfb;


   //
   //  Do all players, one group at a time
   //
   go = false;                             // init to 'No Go'

   if (parm.p5.equals( "Yes" )) {

      if (!parm.player1.equals( "" ) || !parm.player2.equals( "" ) || !parm.player3.equals( "" ) ||
          !parm.player4.equals( "" ) || !parm.player5.equals( "" )) {

         go = true;                // go process this group

         //
         //  set parms for first group
         //
         parm1.player1 = parm.player1;
         parm1.player2 = parm.player2;
         parm1.player3 = parm.player3;
         parm1.player4 = parm.player4;
         parm1.player5 = parm.player5;
         parm1.user1 = parm.user1;
         parm1.user2 = parm.user2;
         parm1.user3 = parm.user3;
         parm1.user4 = parm.user4;
         parm1.user5 = parm.user5;
         parm1.mship1 = parm.mship1;
         parm1.mship2 = parm.mship2;
         parm1.mship3 = parm.mship3;
         parm1.mship4 = parm.mship4;
         parm1.mship5 = parm.mship5;
         parm1.mtype1 = parm.mtype1;
         parm1.mtype2 = parm.mtype2;
         parm1.mtype3 = parm.mtype3;
         parm1.mtype4 = parm.mtype4;
         parm1.mtype5 = parm.mtype5;
         parm1.mNum1 = parm.mNum1;
         parm1.mNum2 = parm.mNum2;
         parm1.mNum3 = parm.mNum3;
         parm1.mNum4 = parm.mNum4;
         parm1.mNum5 = parm.mNum5;
      }

   } else {                       // 4-somes only

      if (!parm.player1.equals( "" ) || !parm.player2.equals( "" ) || !parm.player3.equals( "" ) ||
          !parm.player4.equals( "" )) {

         go = true;                // go process this group

         //
         //  set parms for first group
         //
         parm1.player1 = parm.player1;
         parm1.player2 = parm.player2;
         parm1.player3 = parm.player3;
         parm1.player4 = parm.player4;
         parm1.user1 = parm.user1;
         parm1.user2 = parm.user2;
         parm1.user3 = parm.user3;
         parm1.user4 = parm.user4;
         parm1.mship1 = parm.mship1;
         parm1.mship2 = parm.mship2;
         parm1.mship3 = parm.mship3;
         parm1.mship4 = parm.mship4;
         parm1.mtype1 = parm.mtype1;
         parm1.mtype2 = parm.mtype2;
         parm1.mtype3 = parm.mtype3;
         parm1.mtype4 = parm.mtype4;
         parm1.mNum1 = parm.mNum1;
         parm1.mNum2 = parm.mNum2;
         parm1.mNum3 = parm.mNum3;
         parm1.mNum4 = parm.mNum4;
         parm1.player5 = "";
         parm1.user5 = "";
         parm1.mship5 = "";
         parm1.mtype5 = "";
         parm1.mNum5 = "";
      }
   }

   if (go == true) {          // if players found

      error = verifySlot.checkCherryHills(parm1);    // process custom restrictions
   }

   if (error == false) {           // if we can keep going

      //
      //  Do 2nd group
      //
      go = false;                             // init to 'No Go'

      if (parm.p5.equals( "Yes" )) {

         if (!parm.player6.equals( "" ) || !parm.player7.equals( "" ) || !parm.player8.equals( "" ) ||
             !parm.player9.equals( "" ) || !parm.player10.equals( "" )) {

            go = true;                // go process this group

            //
            //  set parms for this group
            //
            parm1.player1 = parm.player6;
            parm1.player2 = parm.player7;
            parm1.player3 = parm.player8;
            parm1.player4 = parm.player9;
            parm1.player5 = parm.player10;
            parm1.user1 = parm.user6;
            parm1.user2 = parm.user7;
            parm1.user3 = parm.user8;
            parm1.user4 = parm.user9;
            parm1.user5 = parm.user10;
            parm1.mship1 = parm.mship6;
            parm1.mship2 = parm.mship7;
            parm1.mship3 = parm.mship8;
            parm1.mship4 = parm.mship9;
            parm1.mship5 = parm.mship10;
            parm1.mtype1 = parm.mtype6;
            parm1.mtype2 = parm.mtype7;
            parm1.mtype3 = parm.mtype8;
            parm1.mtype4 = parm.mtype9;
            parm1.mtype5 = parm.mtype10;
            parm1.mNum1 = parm.mNum6;
            parm1.mNum2 = parm.mNum7;
            parm1.mNum3 = parm.mNum8;
            parm1.mNum4 = parm.mNum9;
            parm1.mNum5 = parm.mNum10;
         }

      } else {                       // 4-somes only

         if (!parm.player5.equals( "" ) || !parm.player6.equals( "" ) || !parm.player7.equals( "" ) ||
             !parm.player8.equals( "" )) {

            go = true;                // go process this group

            //
            //  set parms for this group
            //
            parm1.player1 = parm.player5;
            parm1.player2 = parm.player6;
            parm1.player3 = parm.player7;
            parm1.player4 = parm.player8;
            parm1.user1 = parm.user5;
            parm1.user2 = parm.user6;
            parm1.user3 = parm.user7;
            parm1.user4 = parm.user8;
            parm1.mship1 = parm.mship5;
            parm1.mship2 = parm.mship6;
            parm1.mship3 = parm.mship7;
            parm1.mship4 = parm.mship8;
            parm1.mtype1 = parm.mtype5;
            parm1.mtype2 = parm.mtype6;
            parm1.mtype3 = parm.mtype7;
            parm1.mtype4 = parm.mtype8;
            parm1.mNum1 = parm.mNum5;
            parm1.mNum2 = parm.mNum6;
            parm1.mNum3 = parm.mNum7;
            parm1.mNum4 = parm.mNum8;
            parm1.player5 = "";
            parm1.user5 = "";
            parm1.mship5 = "";
            parm1.mtype5 = "";
            parm1.mNum5 = "";
         }
      }

      if (go == true) {          // if mships found

         error = verifySlot.checkCherryHills(parm1);    // process custom restrictions
      }

      if (error == false) {           // if we can keep going

         //
         //  Do 3rd group
         //
         go = false;                             // init to 'No Go'

         if (parm.p5.equals( "Yes" )) {

            if (!parm.player11.equals( "" ) || !parm.player12.equals( "" ) || !parm.player13.equals( "" ) ||
                !parm.player14.equals( "" ) || !parm.player15.equals( "" )) {

               go = true;                // go process this group

               //
               //  set parms for this group
               //
               parm1.player1 = parm.player11;
               parm1.player2 = parm.player12;
               parm1.player3 = parm.player13;
               parm1.player4 = parm.player14;
               parm1.player5 = parm.player15;
               parm1.user1 = parm.user11;
               parm1.user2 = parm.user12;
               parm1.user3 = parm.user13;
               parm1.user4 = parm.user14;
               parm1.user5 = parm.user15;
               parm1.mship1 = parm.mship11;
               parm1.mship2 = parm.mship12;
               parm1.mship3 = parm.mship13;
               parm1.mship4 = parm.mship14;
               parm1.mship5 = parm.mship15;
               parm1.mtype1 = parm.mtype11;
               parm1.mtype2 = parm.mtype12;
               parm1.mtype3 = parm.mtype13;
               parm1.mtype4 = parm.mtype14;
               parm1.mtype5 = parm.mtype15;
               parm1.mNum1 = parm.mNum11;
               parm1.mNum2 = parm.mNum12;
               parm1.mNum3 = parm.mNum13;
               parm1.mNum4 = parm.mNum14;
               parm1.mNum5 = parm.mNum15;
            }

         } else {                       // 4-somes only

            if (!parm.player9.equals( "" ) || !parm.player10.equals( "" ) || !parm.player11.equals( "" ) ||
                !parm.player12.equals( "" )) {

               go = true;                // go process this group

               //
               //  set parms for this group
               //
               parm1.player1 = parm.player9;
               parm1.player2 = parm.player10;
               parm1.player3 = parm.player11;
               parm1.player4 = parm.player12;
               parm1.user1 = parm.user9;
               parm1.user2 = parm.user10;
               parm1.user3 = parm.user11;
               parm1.user4 = parm.user12;
               parm1.mship1 = parm.mship9;
               parm1.mship2 = parm.mship10;
               parm1.mship3 = parm.mship11;
               parm1.mship4 = parm.mship12;
               parm1.mtype1 = parm.mtype9;
               parm1.mtype2 = parm.mtype10;
               parm1.mtype3 = parm.mtype11;
               parm1.mtype4 = parm.mtype12;
               parm1.mNum1 = parm.mNum9;
               parm1.mNum2 = parm.mNum10;
               parm1.mNum3 = parm.mNum11;
               parm1.mNum4 = parm.mNum12;
               parm1.player5 = "";
               parm1.user5 = "";
               parm1.mship5 = "";
               parm1.mtype5 = "";
               parm1.mNum5 = "";
            }
         }

         if (go == true) {          // if mships found

            error = verifySlot.checkCherryHills(parm1);    // process custom restrictions
         }

         if (error == false) {           // if we can keep going

            //
            //  Do 4th group
            //
            go = false;                             // init to 'No Go'

            if (parm.p5.equals( "Yes" )) {

               if (!parm.player16.equals( "" ) || !parm.player17.equals( "" ) || !parm.player18.equals( "" ) ||
                   !parm.player19.equals( "" ) || !parm.player20.equals( "" )) {

                  go = true;                // go process this group

                  //
                  //  set parms for this group
                  //
                  parm1.player1 = parm.player16;
                  parm1.player2 = parm.player17;
                  parm1.player3 = parm.player18;
                  parm1.player4 = parm.player19;
                  parm1.player5 = parm.player20;
                  parm1.user1 = parm.user16;
                  parm1.user2 = parm.user17;
                  parm1.user3 = parm.user18;
                  parm1.user4 = parm.user19;
                  parm1.user5 = parm.user20;
                  parm1.mship1 = parm.mship16;
                  parm1.mship2 = parm.mship17;
                  parm1.mship3 = parm.mship18;
                  parm1.mship4 = parm.mship19;
                  parm1.mship5 = parm.mship20;
                  parm1.mtype1 = parm.mtype16;
                  parm1.mtype2 = parm.mtype17;
                  parm1.mtype3 = parm.mtype18;
                  parm1.mtype4 = parm.mtype19;
                  parm1.mtype5 = parm.mtype20;
                  parm1.mNum1 = parm.mNum16;
                  parm1.mNum2 = parm.mNum17;
                  parm1.mNum3 = parm.mNum18;
                  parm1.mNum4 = parm.mNum19;
                  parm1.mNum5 = parm.mNum20;
               }

            } else {                       // 4-somes only

               if (!parm.player13.equals( "" ) || !parm.player14.equals( "" ) || !parm.player15.equals( "" ) ||
                   !parm.player16.equals( "" )) {

                  go = true;                // go process this group

                  //
                  //  set parms for this group
                  //
                  parm1.player1 = parm.player13;
                  parm1.player2 = parm.player14;
                  parm1.player3 = parm.player15;
                  parm1.player4 = parm.player16;
                  parm1.user1 = parm.user13;
                  parm1.user2 = parm.user14;
                  parm1.user3 = parm.user15;
                  parm1.user4 = parm.user16;
                  parm1.mship1 = parm.mship13;
                  parm1.mship2 = parm.mship14;
                  parm1.mship3 = parm.mship15;
                  parm1.mship4 = parm.mship16;
                  parm1.mtype1 = parm.mtype13;
                  parm1.mtype2 = parm.mtype14;
                  parm1.mtype3 = parm.mtype15;
                  parm1.mtype4 = parm.mtype16;
                  parm1.mNum1 = parm.mNum13;
                  parm1.mNum2 = parm.mNum14;
                  parm1.mNum3 = parm.mNum15;
                  parm1.mNum4 = parm.mNum16;
                  parm1.player5 = "";
                  parm1.user5 = "";
                  parm1.mship5 = "";
                  parm1.mtype5 = "";
                  parm1.mNum5 = "";
               }
            }

            if (go == true) {          // if mships found

               error = verifySlot.checkCherryHills(parm1);    // process custom restrictions

            }

            if (error == false) {           // if we can keep going

               //
               //  Do 5th group
               //
               go = false;                             // init to 'No Go'

               if (parm.p5.equals( "Yes" )) {

                  if (!parm.player21.equals( "" ) || !parm.player22.equals( "" ) || !parm.player23.equals( "" ) ||
                      !parm.player24.equals( "" ) || !parm.player25.equals( "" )) {

                     go = true;                // go process this group

                     //
                     //  set parms for this group
                     //
                     parm1.player1 = parm.player21;
                     parm1.player2 = parm.player22;
                     parm1.player3 = parm.player23;
                     parm1.player4 = parm.player24;
                     parm1.player5 = parm.player25;
                     parm1.user1 = parm.user21;
                     parm1.user2 = parm.user22;
                     parm1.user3 = parm.user23;
                     parm1.user4 = parm.user24;
                     parm1.user5 = parm.user25;
                     parm1.mship1 = parm.mship21;
                     parm1.mship2 = parm.mship22;
                     parm1.mship3 = parm.mship23;
                     parm1.mship4 = parm.mship24;
                     parm1.mship5 = parm.mship25;
                     parm1.mtype1 = parm.mtype21;
                     parm1.mtype2 = parm.mtype22;
                     parm1.mtype3 = parm.mtype23;
                     parm1.mtype4 = parm.mtype24;
                     parm1.mtype5 = parm.mtype25;
                     parm1.mNum1 = parm.mNum21;
                     parm1.mNum2 = parm.mNum22;
                     parm1.mNum3 = parm.mNum23;
                     parm1.mNum4 = parm.mNum24;
                     parm1.mNum5 = parm.mNum25;
                  }

               } else {                       // 4-somes only

                  if (!parm.player17.equals( "" ) || !parm.player18.equals( "" ) || !parm.player19.equals( "" ) ||
                      !parm.player20.equals( "" )) {

                     go = true;                // go process this group

                     //
                     //  set parms for this group
                     //
                     parm1.player1 = parm.player17;
                     parm1.player2 = parm.player18;
                     parm1.player3 = parm.player19;
                     parm1.player4 = parm.player20;
                     parm1.user1 = parm.user17;
                     parm1.user2 = parm.user18;
                     parm1.user3 = parm.user19;
                     parm1.user4 = parm.user20;
                     parm1.mship1 = parm.mship17;
                     parm1.mship2 = parm.mship18;
                     parm1.mship3 = parm.mship19;
                     parm1.mship4 = parm.mship20;
                     parm1.mtype1 = parm.mtype17;
                     parm1.mtype2 = parm.mtype18;
                     parm1.mtype3 = parm.mtype19;
                     parm1.mtype4 = parm.mtype20;
                     parm1.mNum1 = parm.mNum17;
                     parm1.mNum2 = parm.mNum18;
                     parm1.mNum3 = parm.mNum19;
                     parm1.mNum4 = parm.mNum20;
                     parm1.player5 = "";
                     parm1.user5 = "";
                     parm1.mship5 = "";
                     parm1.mtype5 = "";
                     parm1.mNum5 = "";
                  }
               }

               if (go == true) {          // if mships found

                  error = verifySlot.checkCherryHills(parm1);    // process custom restrictions

               }
            }
         }
      }
   }

   return(error);

 }         // end of checkCherryRes


 // *********************************************************
 //  Send email to members in this request
 // *********************************************************

 private void sendMail(Connection con, parmLott parms, int emailNew, int emailMod, String user, String club, int guests) {


   //
   //  parm block to hold verify's parms
   //
   parmEmail parme = new parmEmail();          // allocate an Email parm block

   //
   //  Get the parms passed in the parm block and put them in the Email Parm Block
   //
   parme.activity_id = 0;
   parme.club = club;
   parme.guests = guests;
   parme.date = parms.date;
   parme.time = parms.time;
   parme.fb = parms.fb;
   parme.mm = parms.mm;
   parme.dd = parms.dd;
   parme.yy = parms.yy;

   parme.type = "lottery";          // indicate from lottery
   parme.user = user;
   parme.emailNew = emailNew;
   parme.emailMod = emailMod;
   parme.emailCan = 0;

   parme.p91 = parms.p91;
   parme.p92 = parms.p92;
   parme.p93 = parms.p93;
   parme.p94 = parms.p94;
   parme.p95 = parms.p95;
   parme.p96 = parms.p96;
   parme.p97 = parms.p97;
   parme.p98 = parms.p98;
   parme.p99 = parms.p99;
   parme.p910 = parms.p910;
   parme.p911 = parms.p911;
   parme.p912 = parms.p912;
   parme.p913 = parms.p913;
   parme.p914 = parms.p914;
   parme.p915 = parms.p915;
   parme.p916 = parms.p916;
   parme.p917 = parms.p917;
   parme.p918 = parms.p918;
   parme.p919 = parms.p919;
   parme.p920 = parms.p920;
   parme.p921 = parms.p921;
   parme.p922 = parms.p922;
   parme.p923 = parms.p923;
   parme.p924 = parms.p924;
   parme.p925 = parms.p925;

   parme.course = parms.course;
   parme.day = parms.day;

   parme.player1 = parms.player1;
   parme.player2 = parms.player2;
   parme.player3 = parms.player3;
   parme.player4 = parms.player4;
   parme.player5 = parms.player5;
   parme.player6 = parms.player6;
   parme.player7 = parms.player7;
   parme.player8 = parms.player8;
   parme.player9 = parms.player9;
   parme.player10 = parms.player10;
   parme.player11 = parms.player11;
   parme.player12 = parms.player12;
   parme.player13 = parms.player13;
   parme.player14 = parms.player14;
   parme.player15 = parms.player15;
   parme.player16 = parms.player16;
   parme.player17 = parms.player17;
   parme.player18 = parms.player18;
   parme.player19 = parms.player19;
   parme.player20 = parms.player20;
   parme.player21 = parms.player21;
   parme.player22 = parms.player22;
   parme.player23 = parms.player23;
   parme.player24 = parms.player24;
   parme.player25 = parms.player25;

   parme.oldplayer1 = parms.oldplayer1;
   parme.oldplayer2 = parms.oldplayer2;
   parme.oldplayer3 = parms.oldplayer3;
   parme.oldplayer4 = parms.oldplayer4;
   parme.oldplayer5 = parms.oldplayer5;
   parme.oldplayer6 = parms.oldplayer6;
   parme.oldplayer7 = parms.oldplayer7;
   parme.oldplayer8 = parms.oldplayer8;
   parme.oldplayer9 = parms.oldplayer9;
   parme.oldplayer10 = parms.oldplayer10;
   parme.oldplayer11 = parms.oldplayer11;
   parme.oldplayer12 = parms.oldplayer12;
   parme.oldplayer13 = parms.oldplayer13;
   parme.oldplayer14 = parms.oldplayer14;
   parme.oldplayer15 = parms.oldplayer15;
   parme.oldplayer16 = parms.oldplayer16;
   parme.oldplayer17 = parms.oldplayer17;
   parme.oldplayer18 = parms.oldplayer18;
   parme.oldplayer19 = parms.oldplayer19;
   parme.oldplayer20 = parms.oldplayer20;
   parme.oldplayer21 = parms.oldplayer21;
   parme.oldplayer22 = parms.oldplayer22;
   parme.oldplayer23 = parms.oldplayer23;
   parme.oldplayer24 = parms.oldplayer24;
   parme.oldplayer25 = parms.oldplayer25;

   parme.user1 = parms.user1;
   parme.user2 = parms.user2;
   parme.user3 = parms.user3;
   parme.user4 = parms.user4;
   parme.user5 = parms.user5;
   parme.user6 = parms.user6;
   parme.user7 = parms.user7;
   parme.user8 = parms.user8;
   parme.user9 = parms.user9;
   parme.user10 = parms.user10;
   parme.user11 = parms.user11;
   parme.user12 = parms.user12;
   parme.user13 = parms.user13;
   parme.user14 = parms.user14;
   parme.user15 = parms.user15;
   parme.user16 = parms.user16;
   parme.user17 = parms.user17;
   parme.user18 = parms.user18;
   parme.user19 = parms.user19;
   parme.user20 = parms.user20;
   parme.user21 = parms.user21;
   parme.user22 = parms.user22;
   parme.user23 = parms.user23;
   parme.user24 = parms.user24;
   parme.user25 = parms.user25;

   parme.olduser1 = parms.olduser1;
   parme.olduser2 = parms.olduser2;
   parme.olduser3 = parms.olduser3;
   parme.olduser4 = parms.olduser4;
   parme.olduser5 = parms.olduser5;
   parme.olduser6 = parms.olduser6;
   parme.olduser7 = parms.olduser7;
   parme.olduser8 = parms.olduser8;
   parme.olduser9 = parms.olduser9;
   parme.olduser10 = parms.olduser10;
   parme.olduser11 = parms.olduser11;
   parme.olduser12 = parms.olduser12;
   parme.olduser13 = parms.olduser13;
   parme.olduser14 = parms.olduser14;
   parme.olduser15 = parms.olduser15;
   parme.olduser16 = parms.olduser16;
   parme.olduser17 = parms.olduser17;
   parme.olduser18 = parms.olduser18;
   parme.olduser19 = parms.olduser19;
   parme.olduser20 = parms.olduser20;
   parme.olduser21 = parms.olduser21;
   parme.olduser22 = parms.olduser22;
   parme.olduser23 = parms.olduser23;
   parme.olduser24 = parms.olduser24;
   parme.olduser25 = parms.olduser25;

   parme.pcw1 = parms.pcw1;
   parme.pcw2 = parms.pcw2;
   parme.pcw3 = parms.pcw3;
   parme.pcw4 = parms.pcw4;
   parme.pcw5 = parms.pcw5;
   parme.pcw6 = parms.pcw6;
   parme.pcw7 = parms.pcw7;
   parme.pcw8 = parms.pcw8;
   parme.pcw9 = parms.pcw9;
   parme.pcw10 = parms.pcw10;
   parme.pcw11 = parms.pcw11;
   parme.pcw12 = parms.pcw12;
   parme.pcw13 = parms.pcw13;
   parme.pcw14 = parms.pcw14;
   parme.pcw15 = parms.pcw15;
   parme.pcw16 = parms.pcw16;
   parme.pcw17 = parms.pcw17;
   parme.pcw18 = parms.pcw18;
   parme.pcw19 = parms.pcw19;
   parme.pcw20 = parms.pcw20;
   parme.pcw21 = parms.pcw21;
   parme.pcw22 = parms.pcw22;
   parme.pcw23 = parms.pcw23;
   parme.pcw24 = parms.pcw24;
   parme.pcw25 = parms.pcw25;

   parme.oldpcw1 = parms.oldpcw1;
   parme.oldpcw2 = parms.oldpcw2;
   parme.oldpcw3 = parms.oldpcw3;
   parme.oldpcw4 = parms.oldpcw4;
   parme.oldpcw5 = parms.oldpcw5;
   parme.oldpcw6 = parms.oldpcw6;
   parme.oldpcw7 = parms.oldpcw7;
   parme.oldpcw8 = parms.oldpcw8;
   parme.oldpcw9 = parms.oldpcw9;
   parme.oldpcw10 = parms.oldpcw10;
   parme.oldpcw11 = parms.oldpcw11;
   parme.oldpcw12 = parms.oldpcw12;
   parme.oldpcw13 = parms.oldpcw13;
   parme.oldpcw14 = parms.oldpcw14;
   parme.oldpcw15 = parms.oldpcw15;
   parme.oldpcw16 = parms.oldpcw16;
   parme.oldpcw17 = parms.oldpcw17;
   parme.oldpcw18 = parms.oldpcw18;
   parme.oldpcw19 = parms.oldpcw19;
   parme.oldpcw20 = parms.oldpcw20;
   parme.oldpcw21 = parms.oldpcw21;
   parme.oldpcw22 = parms.oldpcw22;
   parme.oldpcw23 = parms.oldpcw23;
   parme.oldpcw24 = parms.oldpcw24;
   parme.oldpcw25 = parms.oldpcw25;

   parme.guest_id1 = parms.guest_id1;
   parme.guest_id2 = parms.guest_id2;
   parme.guest_id3 = parms.guest_id3;
   parme.guest_id4 = parms.guest_id4;
   parme.guest_id5 = parms.guest_id5;
   parme.guest_id6 = parms.guest_id6;
   parme.guest_id7 = parms.guest_id7;
   parme.guest_id8 = parms.guest_id8;
   parme.guest_id9 = parms.guest_id9;
   parme.guest_id10 = parms.guest_id10;
   parme.guest_id11 = parms.guest_id11;
   parme.guest_id12 = parms.guest_id12;
   parme.guest_id13 = parms.guest_id13;
   parme.guest_id14 = parms.guest_id14;
   parme.guest_id15 = parms.guest_id15;
   parme.guest_id16 = parms.guest_id16;
   parme.guest_id17 = parms.guest_id17;
   parme.guest_id18 = parms.guest_id18;
   parme.guest_id19 = parms.guest_id19;
   parme.guest_id20 = parms.guest_id20;
   parme.guest_id21 = parms.guest_id21;
   parme.guest_id22 = parms.guest_id22;
   parme.guest_id23 = parms.guest_id23;
   parme.guest_id24 = parms.guest_id24;
   parme.guest_id25 = parms.guest_id25;

   parme.oldguest_id1 = parms.oldguest_id1;
   parme.oldguest_id2 = parms.oldguest_id2;
   parme.oldguest_id3 = parms.oldguest_id3;
   parme.oldguest_id4 = parms.oldguest_id4;
   parme.oldguest_id5 = parms.oldguest_id5;
   parme.oldguest_id6 = parms.oldguest_id6;
   parme.oldguest_id7 = parms.oldguest_id7;
   parme.oldguest_id8 = parms.oldguest_id8;
   parme.oldguest_id9 = parms.oldguest_id9;
   parme.oldguest_id10 = parms.oldguest_id10;
   parme.oldguest_id11 = parms.oldguest_id11;
   parme.oldguest_id12 = parms.oldguest_id12;
   parme.oldguest_id13 = parms.oldguest_id13;
   parme.oldguest_id14 = parms.oldguest_id14;
   parme.oldguest_id15 = parms.oldguest_id15;
   parme.oldguest_id16 = parms.oldguest_id16;
   parme.oldguest_id17 = parms.oldguest_id17;
   parme.oldguest_id18 = parms.oldguest_id18;
   parme.oldguest_id19 = parms.oldguest_id19;
   parme.oldguest_id20 = parms.oldguest_id20;
   parme.oldguest_id21 = parms.oldguest_id21;
   parme.oldguest_id22 = parms.oldguest_id22;
   parme.oldguest_id23 = parms.oldguest_id23;
   parme.oldguest_id24 = parms.oldguest_id24;
   parme.oldguest_id25 = parms.oldguest_id25;

   //
   //  Send the email
   //
   sendEmail.sendIt(parme, con);      // in common

 }


 // *********************************************************
 // Check each member for # of rounds played in a period
 // *********************************************************

 private int checkRounds(Connection con, String mperiod, String user, long date, long dateStart, long dateEnd, int mm, int yy) {


   ResultSet rs = null;

   int count = 0;

   try {
      //
      // statements for week
      //
      PreparedStatement pstmt2 = con.prepareStatement (
         "SELECT dd FROM teecurr2 WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR " +
                    "username5 = ?) AND date != ? AND date >= ? AND date <= ?");

      PreparedStatement pstmt3 = con.prepareStatement (
         "SELECT dd FROM teepast2 WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR " +
                    "username5 = ?) AND date != ? AND date >= ? AND date <= ?");
      //
      // statements for month
      //
      PreparedStatement pstmt2m = con.prepareStatement (
         "SELECT dd FROM teecurr2 WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR " +
                    "username5 = ?) AND date != ? AND mm = ? AND yy = ?");

      PreparedStatement pstmt3m = con.prepareStatement (
         "SELECT dd FROM teepast2 WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR " +
                    "username5 = ?) AND date != ? AND mm = ? AND yy = ?");
      //
      // statements for year
      //
      PreparedStatement pstmt2y = con.prepareStatement (
         "SELECT dd FROM teecurr2 WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR " +
                    "username5 = ?) AND date != ? AND yy = ?");

      PreparedStatement pstmt3y = con.prepareStatement (
         "SELECT dd FROM teepast2 WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR " +
                    "username5 = ?) AND date != ? AND yy = ?");

      if (mperiod.equals( "Week" )) {       // if WEEK

         pstmt2.clearParameters();        // get count from teecurr
         pstmt2.setString(1, user);
         pstmt2.setString(2, user);
         pstmt2.setString(3, user);
         pstmt2.setString(4, user);
         pstmt2.setString(5, user);
         pstmt2.setLong(6, date);
         pstmt2.setLong(7, dateStart);
         pstmt2.setLong(8, dateEnd);
         rs = pstmt2.executeQuery();

         count = 0;

         while (rs.next()) {

            count++;                      // count number or tee times in this week
         }

         pstmt3.clearParameters();        // get count from teepast
         pstmt3.setString(1, user);
         pstmt3.setString(2, user);
         pstmt3.setString(3, user);
         pstmt3.setString(4, user);
         pstmt3.setString(5, user);
         pstmt3.setLong(6, date);
         pstmt3.setLong(7, dateStart);
         pstmt3.setLong(8, dateEnd);
         rs = pstmt3.executeQuery();

         while (rs.next()) {

            count++;                      // count number or tee times in this week
         }
      }       // end of IF mperiod = week

      if (mperiod.equals( "Month" )) {      // if MONTH

         pstmt2m.clearParameters();        // get count from teecurr
         pstmt2m.setString(1, user);
         pstmt2m.setString(2, user);
         pstmt2m.setString(3, user);
         pstmt2m.setString(4, user);
         pstmt2m.setString(5, user);
         pstmt2m.setLong(6, date);
         pstmt2m.setInt(7, mm);
         pstmt2m.setInt(8, yy);
         rs = pstmt2m.executeQuery();

         count = 0;

         while (rs.next()) {

            count++;                      // count number or tee times in this month
         }

         pstmt3m.clearParameters();        // get count from teepast
         pstmt3m.setString(1, user);
         pstmt3m.setString(2, user);
         pstmt3m.setString(3, user);
         pstmt3m.setString(4, user);
         pstmt3m.setString(5, user);
         pstmt3m.setLong(6, date);
         pstmt3m.setInt(7, mm);
         pstmt3m.setInt(8, yy);
         rs = pstmt3m.executeQuery();

         while (rs.next()) {

            count++;                         // count number or tee times in this month
         }
      }       // end of IF mperiod = Month

      if (mperiod.equals( "Year" )) {            // if Year

         pstmt2y.clearParameters();             // get count from teecurr
         pstmt2y.setString(1, user);
         pstmt2y.setString(2, user);
         pstmt2y.setString(3, user);
         pstmt2y.setString(4, user);
         pstmt2y.setString(5, user);
         pstmt2y.setLong(6, date);
         pstmt2y.setInt(7, mm);
         pstmt2y.setInt(8, yy);
         rs = pstmt2y.executeQuery();

         count = 0;

         while (rs.next()) {

            count++;                      // count number or tee times in this year
         }

         pstmt3y.clearParameters();        // get count from teepast
         pstmt3y.setString(1, user);
         pstmt3y.setString(2, user);
         pstmt3y.setString(3, user);
         pstmt3y.setString(4, user);
         pstmt3y.setString(5, user);
         pstmt3y.setLong(6, date);
         pstmt3y.setInt(7, mm);
         pstmt3y.setInt(8, yy);
         rs = pstmt3y.executeQuery();

         while (rs.next()) {

            count++;                      // count number or tee times in this year
         }
      }       // end of IF mperiod = Year

      pstmt2.close();
      pstmt3.close();
      pstmt2m.close();
      pstmt3m.close();
      pstmt2y.close();
      pstmt3y.close();

   }
   catch (Exception ignore) {
   }
   return count;
 }       // end of checkRounds


 // *********************************************************
 // Check for minimum number of players and members
 // *********************************************************

 private boolean chkminPlayer(HttpServletRequest req, Connection con, PrintWriter out, parmLott parm, String lottName) {

   ResultSet rs = null;

   boolean hit = false;
   int minMembers = 0;
   int minPlayers = 0;
     
   String skip = "skip8";

   boolean overrideAccess = SystemUtils.verifyProAccess(req, "REST_OVERRIDE", con, out);

   try {
      //
      PreparedStatement pstmtl3 = con.prepareStatement (
               "SELECT members, players FROM lottery3 WHERE name = ?");

      pstmtl3.clearParameters();        // clear the parms
      pstmtl3.setString(1, lottName);       // put the parm in stmt
      rs = pstmtl3.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         minMembers = rs.getInt(1);    // minimum # of members per request
         minPlayers = rs.getInt(2);    // minimum # of Players per request
      }
      pstmtl3.close();              // close the stmt

      if (minMembers > 0) {

         if (parm.p5.equals( "No" )) {

            if (minMembers > 4) {

               minMembers = 4;         // reduce for 4-somes
            }
         }
         //
         //  Reject request if not enough members in request
         //
         if (parm.members < minMembers) {
           
            hit = true;

            out.println(SystemUtils.HeadTitle("Not Enough Members"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><H3>Not Enough Members in Request</H3><BR>");
            out.println("<BR><BR>Warning: Your request does not contain the minimum number of members required.");
            out.println("<BR><BR>Your request contains " + parm.members + " members but you need " + minMembers + ".");
            //
            //  Return to _lott to change the player order
            //
            goReturn(out, parm, overrideAccess, "", skip);
            return hit;
         }
      }
      if (minPlayers > 0) {

         if (parm.p5.equals( "No" )) {

            if (minPlayers > 4) {

               minPlayers = 4;         // reduce for 4-somes
            }
         }
         //
         //  Reject request if not enough Players in request
         //
         if (parm.players < minPlayers) {

            hit = true;

            out.println(SystemUtils.HeadTitle("Not Enough Players"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><H3>Not Enough Players in Request</H3><BR>");
            out.println("<BR><BR>Warning: Your request does not contain the minimum number of Players required.");
            out.println("<BR><BR>Your request contains " + parm.players + " Players but you need " + minPlayers + ".");
            //
            //  Return to _lott to change the player order
            //
            goReturn(out, parm, overrideAccess, "", skip);
            return hit;
         }
      }
   }
      catch (Exception ignore) {
   }
   return hit;
 }


 // *********************************************************
 // Check if player already scheduled
 // *********************************************************

 private int chkPlayer(Connection con, String player, long date, int time, int fb, String course, long id) {


   ResultSet rs = null;

   int hit = 0;
   int time2 = 0;
   int fb2 = 0;
   int count = 0;
     
   long id2 = 0;

   String course2 = "";

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con); // golf only feature

   //
   //   Get the guest names specified for this club
   //
   try {
      getClub.getParms(con, parm);        // get the club parms

   }
   catch (Exception ignore) {
   }

   int max = parm.rnds;           // max allowed rounds per day for members (club option)
   int hrsbtwn = parm.hrsbtwn;    // minumum hours between tee times (club option when rnds > 1)


   try {

      PreparedStatement pstmt21 = con.prepareStatement (
         "SELECT time, fb, courseName FROM teecurr2 " +
         "WHERE (player1 = ? OR player2 = ? OR player3 = ? OR player4 = ? OR player5 = ?) AND date = ?");

      pstmt21.clearParameters();        // clear the parms and check player 1
      pstmt21.setString(1, player);
      pstmt21.setString(2, player);
      pstmt21.setString(3, player);
      pstmt21.setString(4, player);
      pstmt21.setString(5, player);
      pstmt21.setLong(6, date);
      rs = pstmt21.executeQuery();      // execute the prepared stmt

      while (rs.next()) {

         time2 = rs.getInt("time");
         fb2 = rs.getInt("fb");
         course2 = rs.getString("courseName");

         if ((time2 != time) || (fb2 != fb) || (!course2.equals( course ))) {      // if not this tee time

            count++;         // add to tee time counter for member

            //
            //  check if requested tee time is too close to this one
            //
            if (max > 1 && hrsbtwn > 0) {

               if (time2 < time) {            // if this tee time is before the time requested

                  if (time < (time2 + (hrsbtwn * 100))) {     // if this tee time is within range

                     hit = 2;                       // tee times not far enough apart
                  }

               } else {                                 // this time is after the requested time

                  if (time2 < (time + (hrsbtwn * 100))) {     // if this tee time is within range

                     hit = 2;                       // tee times not far enough apart
                  }
               }
            }
         }
      }
      pstmt21.close();
        
      //
      //  check if player already on a lottery request
      //
      PreparedStatement pstmt22 = con.prepareStatement (
         "SELECT time, id FROM lreqs3 " +
         "WHERE (player1 = ? OR player2 = ? OR player3 = ? OR player4 = ? OR player5 = ? OR " +
                "player6 = ? OR player7 = ? OR player8 = ? OR player9 = ? OR player10 = ? OR " +
                "player11 = ? OR player12 = ? OR player13 = ? OR player14 = ? OR player15 = ? OR " +
                "player16 = ? OR player17 = ? OR player18 = ? OR player19 = ? OR player20 = ? OR " +
                "player21 = ? OR player22 = ? OR player23 = ? OR player24 = ? OR player25 = ?) AND date = ?");

      pstmt22.clearParameters();        // clear the parms and check player 1
      pstmt22.setString(1, player);
      pstmt22.setString(2, player);
      pstmt22.setString(3, player);
      pstmt22.setString(4, player);
      pstmt22.setString(5, player);
      pstmt22.setString(6, player);
      pstmt22.setString(7, player);
      pstmt22.setString(8, player);
      pstmt22.setString(9, player);
      pstmt22.setString(10, player);
      pstmt22.setString(11, player);
      pstmt22.setString(12, player);
      pstmt22.setString(13, player);
      pstmt22.setString(14, player);
      pstmt22.setString(15, player);
      pstmt22.setString(16, player);
      pstmt22.setString(17, player);
      pstmt22.setString(18, player);
      pstmt22.setString(19, player);
      pstmt22.setString(20, player);
      pstmt22.setString(21, player);
      pstmt22.setString(22, player);
      pstmt22.setString(23, player);
      pstmt22.setString(24, player);
      pstmt22.setString(25, player);
      pstmt22.setLong(26, date);
      rs = pstmt22.executeQuery();      // execute the prepared stmt

      while (rs.next()) {

         time2 = rs.getInt("time");
         id2 = rs.getLong("id");

         if (id2 != id) {              // if not this req

            count++;         // add to tee time counter for member

            //
            //  check if requested tee time is too close to this one
            //
            if (max > 1 && hrsbtwn > 0) {

               if (time2 < time) {            // if this tee time is before the time requested

                  if (time < (time2 + (hrsbtwn * 100))) {     // if this tee time is within range

                     hit = 3;                       // tee times not far enough apart
                  }

               } else {                                 // this time is after the requested time

                  if (time2 < (time + (hrsbtwn * 100))) {     // if this tee time is within range

                     hit = 3;                       // tee times not far enough apart
                  }
               }
            }
         }
      }
      pstmt22.close();
        
      //
      //  See if we exceeded max allowed for day - if so, set indicator
      //
      if (count >= max) {

         hit = 1;                       // player already scheduled on this date (max times allowed)
      }

   }
   catch (Exception ignore) {
   }

   return hit;
 }


 // *********************************************************
 // Check Member Number Restrictions
 // *********************************************************

 private int checkmNum(String mNum, long date, int rest_etime, int rest_stime, int time, String course, String rest_fb, String rest_course, Connection con) {


   ResultSet rs7 = null;

   int ind = 0;
   int time2 = 0;
   int t_fb = 0;

   String course2 = "";
   String sfb2 = "";
   String rmNum1 = "";
   String rmNum2 = "";
   String rmNum3 = "";
   String rmNum4 = "";
   String rmNum5 = "";

   try {

      PreparedStatement pstmt7c = con.prepareStatement (
         "SELECT time, fb, courseName, mNum1, mNum2, mNum3, mNum4, mNum5 FROM teecurr2 " +
         "WHERE (mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?) AND date = ? " +
         "AND time <= ? AND time >= ?");

      pstmt7c.clearParameters();        // clear the parms and check player 1
      pstmt7c.setString(1, mNum);
      pstmt7c.setString(2, mNum);
      pstmt7c.setString(3, mNum);
      pstmt7c.setString(4, mNum);
      pstmt7c.setString(5, mNum);
      pstmt7c.setLong(6, date);
      pstmt7c.setInt(7, rest_etime);
      pstmt7c.setInt(8, rest_stime);
      rs7 = pstmt7c.executeQuery();      // execute the prepared stmt

      while (rs7.next()) {

         time2 = rs7.getInt("time");
         t_fb = rs7.getInt("fb");
         course2 = rs7.getString("courseName");
         rmNum1 = rs7.getString("mNum1");
         rmNum2 = rs7.getString("mNum2");
         rmNum3 = rs7.getString("mNum3");
         rmNum4 = rs7.getString("mNum4");
         rmNum5 = rs7.getString("mNum5");

         //
         //  matching member number found in teecurr - check if course and f/b match
         //
         if (t_fb == 0) {                   // is Tee time for Front 9?

            sfb2 = "Front";
         }

         if (t_fb == 1) {                   // is it Back 9?

            sfb2 = "Back";
         }

         //
         //  First make sure this is not this tee time before changes,
         //  Then check if it matches the criteria for the restriction.
         //
         if ((time2 != time) || (!course2.equals( course ))) {  // either time or course is diff

            if ((rest_fb.equals( "Both" ) || rest_fb.equals( sfb2 )) &&
                (rest_course.equals( "-ALL-" ) || rest_course.equals( course2 ))) {

               if (mNum.equals( rmNum1 )) {
                  ind++;
               }
               if (mNum.equals( rmNum2 )) {
                  ind++;
               }
               if (mNum.equals( rmNum3 )) {
                  ind++;
               }
               if (mNum.equals( rmNum4 )) {
                  ind++;
               }
               if (mNum.equals( rmNum5 )) {
                  ind++;
               }
            }
         }

      } // end of while members

      pstmt7c.close();

   }
   catch (Exception ignore) {
   }

   return (ind);
 }


 // *********************************************************
 //  Get the 'X' option for the specified lottery
 // *********************************************************

 private int getXoption(String name, Connection con) {


   ResultSet rs = null;

   int allowX = 0;

   try {

      PreparedStatement pstmt = con.prepareStatement (
         "SELECT allowx " +
         "FROM lottery3 " +
         "WHERE name = ?");

      pstmt.clearParameters();        
      pstmt.setString(1, name);
      rs = pstmt.executeQuery();     

      if (rs.next()) {

         allowX = rs.getInt("allowx");

      } // end of while members

      pstmt.close();

   }
   catch (Exception ignore) {
   }

   return (allowX);
 }

 

 // *********************************************************
 //  Return to _lott
 // *********************************************************

 private void goReturn(PrintWriter out, parmLott parm) {

     // call other method and pass default options so the user can not override
     goReturn(out, parm, false, "", "0");

 }

 private void goReturn(PrintWriter out, parmLott parm, String skip) {

     // call other method and pass default options so the user can override
     goReturn(out, parm, true, "", skip);

 }

 // *********************************************************
 //  Return to _lott
 // *********************************************************

 private void goReturn(PrintWriter out, parmLott parm, boolean allowOverride, String user, String skip) {

      if (allowOverride) {
         out.println("<BR><BR>Would you like to override this check and allow this reservation?");
      }
      out.println("<BR><BR>");

      out.println("<font size=\"2\">");
      out.println("<form action=\"Proshop_lott\" method=\"post\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + parm.date + "\">");
      out.println("<input type=\"hidden\" name=\"stime\" value=\"" + parm.time + "\">");
      out.println("<input type=\"hidden\" name=\"day\" value=\"" + parm.day + "\">");
      out.println("<input type=\"hidden\" name=\"index\" value=\"" + parm.index + "\">");
      out.println("<input type=\"hidden\" name=\"index2\" value=\"" + parm.index2 + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + parm.course + "\">");
      out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + parm.returnCourse + "\">");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" + parm.jump + "\">");
      out.println("<input type=\"hidden\" name=\"p5\" value=\"" + parm.p5 + "\">");
      out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + parm.p5rest + "\">");
      out.println("<input type=\"hidden\" name=\"fb\" value=\"" + parm.fb + "\">");
      out.println("<input type=\"hidden\" name=\"player1\" value=\"" + parm.player1 + "\">");
      out.println("<input type=\"hidden\" name=\"player2\" value=\"" + parm.player2 + "\">");
      out.println("<input type=\"hidden\" name=\"player3\" value=\"" + parm.player3 + "\">");
      out.println("<input type=\"hidden\" name=\"player4\" value=\"" + parm.player4 + "\">");
      out.println("<input type=\"hidden\" name=\"player5\" value=\"" + parm.player5 + "\">");
      out.println("<input type=\"hidden\" name=\"player6\" value=\"" + parm.player6 + "\">");
      out.println("<input type=\"hidden\" name=\"player7\" value=\"" + parm.player7 + "\">");
      out.println("<input type=\"hidden\" name=\"player8\" value=\"" + parm.player8 + "\">");
      out.println("<input type=\"hidden\" name=\"player9\" value=\"" + parm.player9 + "\">");
      out.println("<input type=\"hidden\" name=\"player10\" value=\"" + parm.player10 + "\">");
      out.println("<input type=\"hidden\" name=\"player11\" value=\"" + parm.player11 + "\">");
      out.println("<input type=\"hidden\" name=\"player12\" value=\"" + parm.player12 + "\">");
      out.println("<input type=\"hidden\" name=\"player13\" value=\"" + parm.player13 + "\">");
      out.println("<input type=\"hidden\" name=\"player14\" value=\"" + parm.player14 + "\">");
      out.println("<input type=\"hidden\" name=\"player15\" value=\"" + parm.player15 + "\">");
      out.println("<input type=\"hidden\" name=\"player16\" value=\"" + parm.player16 + "\">");
      out.println("<input type=\"hidden\" name=\"player17\" value=\"" + parm.player17 + "\">");
      out.println("<input type=\"hidden\" name=\"player18\" value=\"" + parm.player18 + "\">");
      out.println("<input type=\"hidden\" name=\"player19\" value=\"" + parm.player19 + "\">");
      out.println("<input type=\"hidden\" name=\"player20\" value=\"" + parm.player20 + "\">");
      out.println("<input type=\"hidden\" name=\"player21\" value=\"" + parm.player21 + "\">");
      out.println("<input type=\"hidden\" name=\"player22\" value=\"" + parm.player22 + "\">");
      out.println("<input type=\"hidden\" name=\"player23\" value=\"" + parm.player23 + "\">");
      out.println("<input type=\"hidden\" name=\"player24\" value=\"" + parm.player24 + "\">");
      out.println("<input type=\"hidden\" name=\"player25\" value=\"" + parm.player25 + "\">");
      out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + parm.pcw1 + "\">");
      out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + parm.pcw2 + "\">");
      out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + parm.pcw3 + "\">");
      out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + parm.pcw4 + "\">");
      out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + parm.pcw5 + "\">");
      out.println("<input type=\"hidden\" name=\"p6cw\" value=\"" + parm.pcw6 + "\">");
      out.println("<input type=\"hidden\" name=\"p7cw\" value=\"" + parm.pcw7 + "\">");
      out.println("<input type=\"hidden\" name=\"p8cw\" value=\"" + parm.pcw8 + "\">");
      out.println("<input type=\"hidden\" name=\"p9cw\" value=\"" + parm.pcw9 + "\">");
      out.println("<input type=\"hidden\" name=\"p10cw\" value=\"" + parm.pcw10 + "\">");
      out.println("<input type=\"hidden\" name=\"p11cw\" value=\"" + parm.pcw11 + "\">");
      out.println("<input type=\"hidden\" name=\"p12cw\" value=\"" + parm.pcw12 + "\">");
      out.println("<input type=\"hidden\" name=\"p13cw\" value=\"" + parm.pcw13 + "\">");
      out.println("<input type=\"hidden\" name=\"p14cw\" value=\"" + parm.pcw14 + "\">");
      out.println("<input type=\"hidden\" name=\"p15cw\" value=\"" + parm.pcw15 + "\">");
      out.println("<input type=\"hidden\" name=\"p16cw\" value=\"" + parm.pcw16 + "\">");
      out.println("<input type=\"hidden\" name=\"p17cw\" value=\"" + parm.pcw17 + "\">");
      out.println("<input type=\"hidden\" name=\"p18cw\" value=\"" + parm.pcw18 + "\">");
      out.println("<input type=\"hidden\" name=\"p19cw\" value=\"" + parm.pcw19 + "\">");
      out.println("<input type=\"hidden\" name=\"p20cw\" value=\"" + parm.pcw20 + "\">");
      out.println("<input type=\"hidden\" name=\"p21cw\" value=\"" + parm.pcw21 + "\">");
      out.println("<input type=\"hidden\" name=\"p22cw\" value=\"" + parm.pcw22 + "\">");
      out.println("<input type=\"hidden\" name=\"p23cw\" value=\"" + parm.pcw23 + "\">");
      out.println("<input type=\"hidden\" name=\"p24cw\" value=\"" + parm.pcw24 + "\">");
      out.println("<input type=\"hidden\" name=\"p25cw\" value=\"" + parm.pcw25 + "\">");
      out.println("<input type=\"hidden\" name=\"p91\" value=\"" + parm.p91 + "\">");
      out.println("<input type=\"hidden\" name=\"p92\" value=\"" + parm.p92 + "\">");
      out.println("<input type=\"hidden\" name=\"p93\" value=\"" + parm.p93 + "\">");
      out.println("<input type=\"hidden\" name=\"p94\" value=\"" + parm.p94 + "\">");
      out.println("<input type=\"hidden\" name=\"p95\" value=\"" + parm.p95 + "\">");
      out.println("<input type=\"hidden\" name=\"p96\" value=\"" + parm.p96 + "\">");
      out.println("<input type=\"hidden\" name=\"p97\" value=\"" + parm.p97 + "\">");
      out.println("<input type=\"hidden\" name=\"p98\" value=\"" + parm.p98 + "\">");
      out.println("<input type=\"hidden\" name=\"p99\" value=\"" + parm.p99 + "\">");
      out.println("<input type=\"hidden\" name=\"p910\" value=\"" + parm.p910 + "\">");
      out.println("<input type=\"hidden\" name=\"p911\" value=\"" + parm.p911 + "\">");
      out.println("<input type=\"hidden\" name=\"p912\" value=\"" + parm.p912 + "\">");
      out.println("<input type=\"hidden\" name=\"p913\" value=\"" + parm.p913 + "\">");
      out.println("<input type=\"hidden\" name=\"p914\" value=\"" + parm.p914 + "\">");
      out.println("<input type=\"hidden\" name=\"p915\" value=\"" + parm.p915 + "\">");
      out.println("<input type=\"hidden\" name=\"p916\" value=\"" + parm.p916 + "\">");
      out.println("<input type=\"hidden\" name=\"p917\" value=\"" + parm.p917 + "\">");
      out.println("<input type=\"hidden\" name=\"p918\" value=\"" + parm.p918 + "\">");
      out.println("<input type=\"hidden\" name=\"p919\" value=\"" + parm.p919 + "\">");
      out.println("<input type=\"hidden\" name=\"p920\" value=\"" + parm.p920 + "\">");
      out.println("<input type=\"hidden\" name=\"p921\" value=\"" + parm.p921 + "\">");
      out.println("<input type=\"hidden\" name=\"p922\" value=\"" + parm.p922 + "\">");
      out.println("<input type=\"hidden\" name=\"p923\" value=\"" + parm.p923 + "\">");
      out.println("<input type=\"hidden\" name=\"p924\" value=\"" + parm.p924 + "\">");
      out.println("<input type=\"hidden\" name=\"p925\" value=\"" + parm.p925 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + parm.guest_id1 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + parm.guest_id2 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + parm.guest_id3 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + parm.guest_id4 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + parm.guest_id5 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id6\" value=\"" + parm.guest_id6 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id7\" value=\"" + parm.guest_id7 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id8\" value=\"" + parm.guest_id8 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id9\" value=\"" + parm.guest_id9 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id10\" value=\"" + parm.guest_id10 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id11\" value=\"" + parm.guest_id11 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id12\" value=\"" + parm.guest_id12 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id13\" value=\"" + parm.guest_id13 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id14\" value=\"" + parm.guest_id14 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id15\" value=\"" + parm.guest_id15 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id16\" value=\"" + parm.guest_id16 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id17\" value=\"" + parm.guest_id17 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id18\" value=\"" + parm.guest_id18 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id19\" value=\"" + parm.guest_id19 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id20\" value=\"" + parm.guest_id20 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id21\" value=\"" + parm.guest_id21 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id22\" value=\"" + parm.guest_id22 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id23\" value=\"" + parm.guest_id23 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id24\" value=\"" + parm.guest_id24 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id25\" value=\"" + parm.guest_id25 + "\">");
      out.println("<input type=\"hidden\" name=\"notes\" value=\"" + parm.notes + "\">");
      //out.println("<input type=\"hidden\" name=\"hide\" value=\"" + parm.hides + "\">");
      if (parm.hide > 0) {
         out.println("<input type=\"hidden\" name=\"hide\" value=\"1\">");
      }
      out.println("<input type=\"hidden\" name=\"lname\" value=\"" + parm.lottName + "\">");
      out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + parm.lstate + "\">");
      out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + parm.lottid + "\">");
      out.println("<input type=\"hidden\" name=\"slots\" value=\"" + parm.slots + "\">");
      out.println("<input type=\"hidden\" name=\"mins_before\" value=\"" + parm.mins_before + "\">");
      out.println("<input type=\"hidden\" name=\"mins_after\" value=\"" + parm.mins_after + "\">");
      out.println("<input type=\"hidden\" name=\"checkothers\" value=\"" + parm.checkothers + "\">");

      if (parm.isRecurr == true) {       // only include the recurrence parms if requested
         out.println("<input type=\"hidden\" name=\"isrecurr\" value=\"yes\">");
      }

      if (!allowOverride) {

          out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
          out.println("</form></font>");

      } else {
          
          out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
          out.println("</form></font>");

          out.println("<form action=\"Proshop_lott\" method=\"post\">");

          out.println("<input type=\"hidden\" name=\"" +skip+ "\" value=\"yes\">");

          out.println("<input type=\"hidden\" name=\"player1\" value=\"" + parm.player1 + "\">");
          out.println("<input type=\"hidden\" name=\"player2\" value=\"" + parm.player2 + "\">");
          out.println("<input type=\"hidden\" name=\"player3\" value=\"" + parm.player3 + "\">");
          out.println("<input type=\"hidden\" name=\"player4\" value=\"" + parm.player4 + "\">");
          out.println("<input type=\"hidden\" name=\"player5\" value=\"" + parm.player5 + "\">");
          out.println("<input type=\"hidden\" name=\"player6\" value=\"" + parm.player6 + "\">");
          out.println("<input type=\"hidden\" name=\"player7\" value=\"" + parm.player7 + "\">");
          out.println("<input type=\"hidden\" name=\"player8\" value=\"" + parm.player8 + "\">");
          out.println("<input type=\"hidden\" name=\"player9\" value=\"" + parm.player9 + "\">");
          out.println("<input type=\"hidden\" name=\"player10\" value=\"" + parm.player10 + "\">");
          out.println("<input type=\"hidden\" name=\"player11\" value=\"" + parm.player11 + "\">");
          out.println("<input type=\"hidden\" name=\"player12\" value=\"" + parm.player12 + "\">");
          out.println("<input type=\"hidden\" name=\"player13\" value=\"" + parm.player13 + "\">");
          out.println("<input type=\"hidden\" name=\"player14\" value=\"" + parm.player14 + "\">");
          out.println("<input type=\"hidden\" name=\"player15\" value=\"" + parm.player15 + "\">");
          out.println("<input type=\"hidden\" name=\"player16\" value=\"" + parm.player16 + "\">");
          out.println("<input type=\"hidden\" name=\"player17\" value=\"" + parm.player17 + "\">");
          out.println("<input type=\"hidden\" name=\"player18\" value=\"" + parm.player18 + "\">");
          out.println("<input type=\"hidden\" name=\"player19\" value=\"" + parm.player19 + "\">");
          out.println("<input type=\"hidden\" name=\"player20\" value=\"" + parm.player20 + "\">");
          out.println("<input type=\"hidden\" name=\"player21\" value=\"" + parm.player21 + "\">");
          out.println("<input type=\"hidden\" name=\"player22\" value=\"" + parm.player22 + "\">");
          out.println("<input type=\"hidden\" name=\"player23\" value=\"" + parm.player23 + "\">");
          out.println("<input type=\"hidden\" name=\"player24\" value=\"" + parm.player24 + "\">");
          out.println("<input type=\"hidden\" name=\"player25\" value=\"" + parm.player25 + "\">");
          out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + parm.pcw1 + "\">");
          out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + parm.pcw2 + "\">");
          out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + parm.pcw3 + "\">");
          out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + parm.pcw4 + "\">");
          out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + parm.pcw5 + "\">");
          out.println("<input type=\"hidden\" name=\"p6cw\" value=\"" + parm.pcw6 + "\">");
          out.println("<input type=\"hidden\" name=\"p7cw\" value=\"" + parm.pcw7 + "\">");
          out.println("<input type=\"hidden\" name=\"p8cw\" value=\"" + parm.pcw8 + "\">");
          out.println("<input type=\"hidden\" name=\"p9cw\" value=\"" + parm.pcw9 + "\">");
          out.println("<input type=\"hidden\" name=\"p10cw\" value=\"" + parm.pcw10 + "\">");
          out.println("<input type=\"hidden\" name=\"p11cw\" value=\"" + parm.pcw11 + "\">");
          out.println("<input type=\"hidden\" name=\"p12cw\" value=\"" + parm.pcw12 + "\">");
          out.println("<input type=\"hidden\" name=\"p13cw\" value=\"" + parm.pcw13 + "\">");
          out.println("<input type=\"hidden\" name=\"p14cw\" value=\"" + parm.pcw14 + "\">");
          out.println("<input type=\"hidden\" name=\"p15cw\" value=\"" + parm.pcw15 + "\">");
          out.println("<input type=\"hidden\" name=\"p16cw\" value=\"" + parm.pcw16 + "\">");
          out.println("<input type=\"hidden\" name=\"p17cw\" value=\"" + parm.pcw17 + "\">");
          out.println("<input type=\"hidden\" name=\"p18cw\" value=\"" + parm.pcw18 + "\">");
          out.println("<input type=\"hidden\" name=\"p19cw\" value=\"" + parm.pcw19 + "\">");
          out.println("<input type=\"hidden\" name=\"p20cw\" value=\"" + parm.pcw20 + "\">");
          out.println("<input type=\"hidden\" name=\"p21cw\" value=\"" + parm.pcw21 + "\">");
          out.println("<input type=\"hidden\" name=\"p22cw\" value=\"" + parm.pcw22 + "\">");
          out.println("<input type=\"hidden\" name=\"p23cw\" value=\"" + parm.pcw23 + "\">");
          out.println("<input type=\"hidden\" name=\"p24cw\" value=\"" + parm.pcw24 + "\">");
          out.println("<input type=\"hidden\" name=\"p25cw\" value=\"" + parm.pcw25 + "\">");
          out.println("<input type=\"hidden\" name=\"p91\" value=\"" + parm.p91 + "\">");
          out.println("<input type=\"hidden\" name=\"p92\" value=\"" + parm.p92 + "\">");
          out.println("<input type=\"hidden\" name=\"p93\" value=\"" + parm.p93 + "\">");
          out.println("<input type=\"hidden\" name=\"p94\" value=\"" + parm.p94 + "\">");
          out.println("<input type=\"hidden\" name=\"p95\" value=\"" + parm.p95 + "\">");
          out.println("<input type=\"hidden\" name=\"p96\" value=\"" + parm.p96 + "\">");
          out.println("<input type=\"hidden\" name=\"p97\" value=\"" + parm.p97 + "\">");
          out.println("<input type=\"hidden\" name=\"p98\" value=\"" + parm.p98 + "\">");
          out.println("<input type=\"hidden\" name=\"p99\" value=\"" + parm.p99 + "\">");
          out.println("<input type=\"hidden\" name=\"p910\" value=\"" + parm.p910 + "\">");
          out.println("<input type=\"hidden\" name=\"p911\" value=\"" + parm.p911 + "\">");
          out.println("<input type=\"hidden\" name=\"p912\" value=\"" + parm.p912 + "\">");
          out.println("<input type=\"hidden\" name=\"p913\" value=\"" + parm.p913 + "\">");
          out.println("<input type=\"hidden\" name=\"p914\" value=\"" + parm.p914 + "\">");
          out.println("<input type=\"hidden\" name=\"p915\" value=\"" + parm.p915 + "\">");
          out.println("<input type=\"hidden\" name=\"p916\" value=\"" + parm.p916 + "\">");
          out.println("<input type=\"hidden\" name=\"p917\" value=\"" + parm.p917 + "\">");
          out.println("<input type=\"hidden\" name=\"p918\" value=\"" + parm.p918 + "\">");
          out.println("<input type=\"hidden\" name=\"p919\" value=\"" + parm.p919 + "\">");
          out.println("<input type=\"hidden\" name=\"p920\" value=\"" + parm.p920 + "\">");
          out.println("<input type=\"hidden\" name=\"p921\" value=\"" + parm.p921 + "\">");
          out.println("<input type=\"hidden\" name=\"p922\" value=\"" + parm.p922 + "\">");
          out.println("<input type=\"hidden\" name=\"p923\" value=\"" + parm.p923 + "\">");
          out.println("<input type=\"hidden\" name=\"p924\" value=\"" + parm.p924 + "\">");
          out.println("<input type=\"hidden\" name=\"p925\" value=\"" + parm.p925 + "\">");
          out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + parm.guest_id1 + "\">");
          out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + parm.guest_id2 + "\">");
          out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + parm.guest_id3 + "\">");
          out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + parm.guest_id4 + "\">");
          out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + parm.guest_id5 + "\">");
          out.println("<input type=\"hidden\" name=\"guest_id6\" value=\"" + parm.guest_id6 + "\">");
          out.println("<input type=\"hidden\" name=\"guest_id7\" value=\"" + parm.guest_id7 + "\">");
          out.println("<input type=\"hidden\" name=\"guest_id8\" value=\"" + parm.guest_id8 + "\">");
          out.println("<input type=\"hidden\" name=\"guest_id9\" value=\"" + parm.guest_id9 + "\">");
          out.println("<input type=\"hidden\" name=\"guest_id10\" value=\"" + parm.guest_id10 + "\">");
          out.println("<input type=\"hidden\" name=\"guest_id11\" value=\"" + parm.guest_id11 + "\">");
          out.println("<input type=\"hidden\" name=\"guest_id12\" value=\"" + parm.guest_id12 + "\">");
          out.println("<input type=\"hidden\" name=\"guest_id13\" value=\"" + parm.guest_id13 + "\">");
          out.println("<input type=\"hidden\" name=\"guest_id14\" value=\"" + parm.guest_id14 + "\">");
          out.println("<input type=\"hidden\" name=\"guest_id15\" value=\"" + parm.guest_id15 + "\">");
          out.println("<input type=\"hidden\" name=\"guest_id16\" value=\"" + parm.guest_id16 + "\">");
          out.println("<input type=\"hidden\" name=\"guest_id17\" value=\"" + parm.guest_id17 + "\">");
          out.println("<input type=\"hidden\" name=\"guest_id18\" value=\"" + parm.guest_id18 + "\">");
          out.println("<input type=\"hidden\" name=\"guest_id19\" value=\"" + parm.guest_id19 + "\">");
          out.println("<input type=\"hidden\" name=\"guest_id20\" value=\"" + parm.guest_id20 + "\">");
          out.println("<input type=\"hidden\" name=\"guest_id21\" value=\"" + parm.guest_id21 + "\">");
          out.println("<input type=\"hidden\" name=\"guest_id22\" value=\"" + parm.guest_id22 + "\">");
          out.println("<input type=\"hidden\" name=\"guest_id23\" value=\"" + parm.guest_id23 + "\">");
          out.println("<input type=\"hidden\" name=\"guest_id24\" value=\"" + parm.guest_id24 + "\">");
          out.println("<input type=\"hidden\" name=\"guest_id25\" value=\"" + parm.guest_id25 + "\">");
          out.println("<input type=\"hidden\" name=\"date\" value=\"" + parm.date + "\">");
          out.println("<input type=\"hidden\" name=\"time\" value=\"" + parm.time + "\">");
          out.println("<input type=\"hidden\" name=\"mm\" value=\"" + parm.mm + "\">");
          out.println("<input type=\"hidden\" name=\"yy\" value=\"" + parm.yy + "\">");
          out.println("<input type=\"hidden\" name=\"index\" value=\"" + parm.index + "\">");
          out.println("<input type=\"hidden\" name=\"index2\" value=\"" + parm.index2 + "\">");
          out.println("<input type=\"hidden\" name=\"day\" value=\"" + parm.day + "\">");
          out.println("<input type=\"hidden\" name=\"p5\" value=\"" + parm.p5 + "\">");
          out.println("<input type=\"hidden\" name=\"fb\" value=\"" + parm.fb + "\">");
          out.println("<input type=\"hidden\" name=\"notes\" value=\"" + parm.notes + "\">");
          //out.println("<input type=\"hidden\" name=\"hide\" value=\"" + parm.hides + "\">");
          if (parm.hide > 0) {
             out.println("<input type=\"hidden\" name=\"hide\" value=\"1\">");
          }
          out.println("<input type=\"hidden\" name=\"course\" value=\"" + parm.course + "\">");
          out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + parm.returnCourse + "\">");
          out.println("<input type=\"hidden\" name=\"jump\" value=\"" + parm.jump + "\">");
          out.println("<input type=\"hidden\" name=\"lname\" value=\"" + parm.lottName + "\">");
          out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + parm.lstate + "\">");
          out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + parm.lottid + "\">");
          out.println("<input type=\"hidden\" name=\"slots\" value=\"" + parm.slots + "\">");
          out.println("<input type=\"hidden\" name=\"mins_before\" value=\"" + parm.mins_before + "\">");
          out.println("<input type=\"hidden\" name=\"mins_after\" value=\"" + parm.mins_after + "\">");
          out.println("<input type=\"hidden\" name=\"checkothers\" value=\"" + parm.checkothers + "\">");

          if (parm.isRecurr == true) {       // only include the recurrence parms if requested
              out.println("<input type=\"hidden\" name=\"isrecurr\" value=\"yes\">");
          }

          out.println("<input type=\"submit\" value=\"Yes - Continue\" name=\"submitForm\" style=\"text-decoration:underline; background:#8B8970\"></form>");
      }

      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
 }


 // *********************************************************
 //  Process cancel request from Proshop_lott (HTML)
 // *********************************************************

 private void cancel(HttpServletRequest req, PrintWriter out, Connection con) {


   long lottid = 0;

   //
   // Get all the parameters entered
   //
   String index = req.getParameter("index");          //  index value of day (needed by Proshop_sheet when returning)
   String index2 = req.getParameter("index2");        //  index value of day (needed by Proshop_mlottery)
   String course = req.getParameter("course");        //  name of course
   String sid = req.getParameter("lottid");           //  lottery id
   String returnCourse = req.getParameter("returnCourse");        //  name of course to return to

   //
   //  Convert the values from string to int
   //
   try {
      lottid = Long.parseLong(sid);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   //
   //  Clear the 'in_use' flag for this request
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
         "UPDATE lreqs3 SET in_use = 0 WHERE id = ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setLong(1, lottid);         // put the parm in pstmt1
      pstmt1.executeUpdate();     

      pstmt1.close();

   }
   catch (Exception ignore) {
   }

   //
   //  Prompt user to return to Proshop_sheet or Proshop_searchmem (index = 888)
   //
   //  These returns will pause for 1 second, then return automatically if meta supported, else user clicks on 'return'
   //
   if (index.equals( "888" )) {       // if originated from Proshop_main

      out.println("<HTML>");
      out.println("<HEAD><link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">");
      out.println("<Title>Proshop Tee Slot Page</Title>");
      out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Proshop_jump?search=yes\">");
      out.println("</HEAD>");
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<BR><BR><H3>Return/Cancel Requested</H3>");
      out.println("<BR><BR>Thank you, the lottery request has been returned to the system without changes.");
      out.println("<BR><BR>");

      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"Proshop_jump\">");
      out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");

   } else {

      if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
         course = returnCourse;
      }

      if (index.equals( "777" )) {       // if originated from Proshop_mlottery

         out.println("<HTML>");
         out.println("<HEAD><link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">");
         out.println("<Title>Proshop Tee Slot Page</Title>");
         out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Proshop_jump?course=" + course + "&index2=" + index2 + "\">");
         out.println("</HEAD>");
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><BR><H3>Return/Cancel Requested</H3>");
         out.println("<BR><BR>Thank you, the lottery request has been returned to the system without changes.");
         out.println("<BR><BR>");

         out.println("<font size=\"2\">");
         out.println("<form method=\"post\" action=\"Proshop_jump\">");
         out.println("<input type=\"hidden\" name=\"index2\" value=\"" + index2 + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font>");

      } else {

         out.println("<HTML>");
         out.println("<HEAD><link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">");
         out.println("<Title>Proshop Tee Slot Page</Title>");
         out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Proshop_jump?index=" + index + "&course=" + course + "\">");
         out.println("</HEAD>");
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><BR><H3>Return/Cancel Requested</H3>");
         out.println("<BR><BR>Thank you, the lottery request has been returned to the system without changes.");
         out.println("<BR><BR>");

         out.println("<font size=\"2\">");
         out.println("<form action=\"Proshop_jump\" method=\"post\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("</form></font>");
      }
   }
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 // *********************************************************
 //  User wants to update recurring requests after the one they just updated
 // *********************************************************

 private void updateRecur(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session, HttpServletResponse resp) {


   ResultSet rs = null;

   //
   //  Get this session's user name
   //
   String user = (String)session.getAttribute("user");
   String club = (String)session.getAttribute("club");
   
   String course = "";  
   String jump = "";
   String index = "";           
   String index2 = "";           
   
   String doUpdate = req.getParameter("doUpdate");       //  user wants to update the recurring requests (yes or no)
   
   long lottid = Long.parseLong(req.getParameter("lottid"));   // id of request that was just updated
   
   if (req.getParameter("index") != null) {
      
      index = req.getParameter("index");     
   }
      
   if (req.getParameter("index2") != null) {
      
      index2 = req.getParameter("index2");     
   }
           
   if (req.getParameter("course") != null) {
      
      course = req.getParameter("course");         //  course (needed by _sheet on return)
   }
           
   if (req.getParameter("jump") != null) {
      
      jump = req.getParameter("jump");             //  jump index value (needed by _sheet on return)
   }
   
   ArrayList<String> recurr_dates = null;   
           
   //
   //  Does user want to update the recurring requests that follow this one?
   //
   if (doUpdate.startsWith("Yes")) {
      
      recurr_dates = Common_Lott.updateRecurReqs(lottid, con);
      Common_Lott.rechainRecurReq(lottid, con);
      
   } else {
      
      Common_Lott.unchainRecurReq(lottid, con);
   }
   
   //
   //  Return to originating page
   //
   out.println("<HTML>");
   out.println("<HEAD><link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">");
   out.println("<Title>Proshop Lottery Registration Page</Title>");
   out.println("</HEAD>");
   out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");

   out.println("<p>&nbsp;</p><p>&nbsp;<b>Thank you!</b>&nbsp;&nbsp;");

   if (doUpdate.startsWith("Yes")) {
      
      if (recurr_dates.size() > 0) {       // if multiple requests were built

         out.println("The following recurring lottery request(s) have been updated (<strong>VERIFY THE DATES</strong>):<BR><BR>");
         
         for (int k=0; k<recurr_dates.size(); k++) {

            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + recurr_dates.get(k) + "<BR>");  // list each date (mm/dd/yyyy)
         }
         
      } else {
         
         out.println("There were no additional recurring lottery requests updated.");
      }
      
   } else {
      
       out.println("The other lottery request(s) have NOT been updated and <BR>this request has been removed from the chain of recurring requests.");
   }
   
   out.println("</p><p>&nbsp;</p></font>");
   out.println("<font size=\"2\">");

   if (index.equals( "888" )) {         // if came from proshop_searchmain

      out.println("<form method=\"get\" action=\"Proshop_jump\">");
      out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");

   } else if (index.equals( "777" )) {         // if came from Proshop_mlottery

      out.println("<form method=\"post\" action=\"Proshop_jump\">");
      out.println("<input type=\"hidden\" name=\"index2\" value=\"" + index2 + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");

      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");

   } else {                             // came from proshop_sheet

      out.println("<table border=\"0\" cols=\"1\" bgcolor=\"#336633\" cellpadding=\"8\">");
      out.println("<form action=\"Proshop_jump\" method=\"post\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
      out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
      out.println("<input type=\"hidden\" name=\"jump\" value=" + jump + ">");
      out.println("<tr><td><font color=\"#FFFFFF\" size=\"2\">");
      out.println("<input type=\"submit\" value=\"Return\">");
      out.println("</font></td></tr></form></table>");     
   }
   
 }    // end of updateRecur
   


 // *********************************************************
 //  Process response to the Recurrence Prompt - user selected the recur options
 // *********************************************************

 private void doRecur(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session, HttpServletResponse resp) {


   ResultSet rs = null;

   //
   //  Get this session's user name
   //
   String user = (String)session.getAttribute("user");
   String club = (String)session.getAttribute("club");
   
   ArrayList<String> recurr_dates = new ArrayList<String>();

   parmLott parm = new parmLott();          // allocate a parm block
   
   //
   // Get all the parameters entered
   //
   String index = req.getParameter("index");           //  day index value (needed by _sheet on return)
   String course = req.getParameter("returnCourse");   //  name of course to return to (multi)
   String jump = req.getParameter("jump");             //  jump index value (needed by _sheet on return)
   
   parm.lottid = Long.parseLong(req.getParameter("lottid"));
   parm.eoweek = Integer.parseInt(req.getParameter("eoweek"));
   
   // get the end date for the recurrence
   
   String date = "";
           
   if (req.getParameter("date") != null) {
           
      date = req.getParameter("date");
   
      if (date != null && !date.equals("")) {

         StringTokenizer tok = new StringTokenizer(date, "-");

         if (tok.countTokens() == 3) {                
            parm.eyy = Integer.parseInt(tok.nextToken());
            parm.emm = Integer.parseInt(tok.nextToken());
            parm.edd = Integer.parseInt(tok.nextToken());
         } else {   
            parm.eyy = Integer.parseInt(date.substring(0,3));
            parm.emm = Integer.parseInt(date.substring(4,5));
            parm.edd = Integer.parseInt(date.substring(6,7));
         }

         //
         //  Go recur this request
         //
         recurr_dates = Common_Lott.addRecurrRequests(parm, con);  // go add the recurring requests (recurr_dates will contain the individual date strings that were added)
      }
   }
   
   //  inform the user that the recur is complete
   
   out.println(SystemUtils.HeadTitle("Recurring Request Prompt"));
   out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR><BR>");
   out.println("<table width=\"600\" border=\"1\" cellspacing=\"10\" cellpadding=\"10\" bgcolor=\"#F5F5DC\" align=\"center\">");
   out.println("<tr><td align=\"center\"><font size=\"3\">");
   out.println("<p>&nbsp;</p><p>&nbsp;<b>Thank you!</b></p><BR>");
          
   if (recurr_dates.size() > 0) {       // if multiple requests were built

      out.println("<p>The recurrence has been completed.</p>");

      out.println("<p>&nbsp;Requests for the following dates have been accepted:<BR><BR>");

      for (int k=0; k<recurr_dates.size(); k++) {

         out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + recurr_dates.get(k) + "<BR>");  // list each date (mm/dd/yyyy)
      }
      out.println("</p><p>To access these requests, <strong>go to Tools - Search - Player's Current Times,</strong><BR>and enter any member that was included in the request.</p>");
      out.println("<p><strong>PLEASE NOTE: </strong>The above requests were booked without any verification of times available<BR>or verification of member restrictions.</p>");
      
   } else {    // it must have failed
      
      out.println("<p>No additional requests were created.</p>");      
      out.println("<p>The most likely cause is the End Date value.</p>");      
      out.println("<p>To try again you must either delete the request you just created and start over,<BR>or create a new request for the group on the following week.</p>");      
   }

   
   out.println("<BR><form action=\"Proshop_jump\" method=\"post\">");
   out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
   out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
   out.println("<input type=\"hidden\" name=\"jump\" value=" + jump + ">");
   out.println("<input type=\"submit\" value=\"Continue\">");
   
   out.println("</font></td></tr></table></center></font></font></body></html>");
   out.close();
      
 }    // end of doRecur

 // *********************************************************
 //  Database Error
 // *********************************************************

 private void dbError(PrintWriter out, Exception e1) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, please contact customer support.");
      out.println("<BR><BR>" + e1.getMessage());
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;

 }


 // *********************************************************
 // Invalid data received - reject request
 // *********************************************************

 private void invData(PrintWriter out, String player, parmLott parm) {

   out.println(SystemUtils.HeadTitle("Invalid Data - Reject"));
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H3>Invalid Data Received</H3><BR>");
   out.println("<BR><BR>Sorry, a name you entered (<b>" + player + "</b>) is not valid.<BR>");
   out.println("Please check the names and try again.");
   out.println("<BR><BR>");
         //
         //  Return to _lott
         //
         goReturn(out, parm);
//   out.println("<font size=\"2\">");
//   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
//   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
//   out.println("</form></font>");
//   out.println("</CENTER></BODY></HTML>");
   return;
 }


 // *********************************************************
 // Invalid data received - reject request
 // *********************************************************

 private void dupData(PrintWriter out, String player, parmLott parm) {

   out.println(SystemUtils.HeadTitle("Data Entry Error"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<center>");
   out.println("<BR><BR><H3>Data Entry Error</H3>");
   out.println("<BR><BR><b>" + player + "</b> was specified more than once.");
   out.println("<BR><BR>Please correct this and try again.");
   out.println("<BR><BR>");
         //
         //  Return to _lott
         //
         goReturn(out, parm);
//   out.println("<font size=\"2\">");
//   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
//   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
//   out.println("</form></font>");
//   out.println("</CENTER></BODY></HTML>");
   return;
 }
}
