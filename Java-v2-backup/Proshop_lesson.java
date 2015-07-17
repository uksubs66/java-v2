/***************************************************************************************     
 *   Proshop_lesson:  This servlet will process all lesson requests from the menu.
 *
 *
 *   created: 10/18/2004   Bob P.
 *
 *   last updated: --
 *
 *        2/19/14  Fixed normal and group lesson booking name lists so that they do not include inactive members.
 *        2/07/14  Notes will now be cleared out when canceling all lessons that are part of a lesson set.
 *        9/04/13  CC of North Carolina (ccofnc) - Added custom to sort lesson types by length, then name.
 *        8/30/13  Updated proshop lesson book so that it will take blockers into account when determining what lesson types are available for selection.
 *        4/24/13  Autoblock Times option and location selection will now be hidden automatically if activity doesn't have time sheets associated with it.
 *        2/26/13  Fix for yesterday's update to avoid an additional error message.
 *        2/25/13  Updated group lesson configuration so that if no courts exist for the current activity, the location prompt will not be shown on the config page, and locations verification will be skipped when submitted.
 *        1/08/13  Updated the Lesson Bio preview to match what members see (modal iframe) and use new common method for outputting the bio pages
 *       12/12/12  Elmcrest CC (elmcrestcc) - Added them to custom to allow them to use the autoblock_times custom for FlxRez lessons.
 *       11/15/12  Updated Add/Edit Pro pages so that the "Advance Signup Limit" field is a text field instead of a select drop-down.
 *       10/29/12  Updated verbiage on the Group Lesson configuration page to make it more obvious what each Clinic option actually does.
 *        8/23/12  Lesson booking process will no longer try to book court times for lessons if the autoblock_times setting for that lesson type is set to No (0).
 *        8/23/12  Added custom option when creating lesson types to toggle the automatic blocking of time sheet slots when that lesson type is booked.
 *        3/06/12  Removed a few outstanding tee time references.
 *        1/16/12  Fixed issue so cancellation history entries are properly created when lessons are canceled while deleting a lesson set.
 *        1/05/12  Updated lesson booking page to change the "Submit" button to say "Please Wait" and be disabled once you click it.  This will help prevent unintended duplicate lesson sets from being built.
 *        1/05/12  Enhanced the Lesson Set feature to allow for Sets to be edited (name & description), and Sets can now be deleted as well. Lessons within a set will also be 
 *                 displayed in two groups, one for upcoming lessons, and one for lessons that have already passed.
 *       12/08/11  Ballantyne CC (ballantyne) - For FlxRez lessons, perform a check to make sure the member doesn't have a conflicting tennis reservation (the same check we do for tennis reservations) (case 2087).
 *       12/05/11  Ballantyne CC (ballantyne) - For FlxRez lessons, display 3 days out in yellow on the calendar, which is the days members have access to (case 2086).
 *       11/06/11  Change paths for lesson bio pages - now using NFS and discrete club folders
 *       11/01/11  Charlotte CC (charlottecc) - Updated 30 min buffer custom to only apply it on the golf side (case 2017).
 *        8/31/11  Added Lesson History feature processing to allow for history entries to be viewed on lesson time slots.  Accessible from either the lesson book or a lesson set including that time.
 *        8/31/11  Charlotte CC (charlottecc) - Add a 30 minute buffer following all lesson bookings, and factor this 30 minutes into calculations when determining available times (case 2017).
 *        8/31/11  Added Recurring Lesson capability to allow for any number of weeks of lessons to be booked at once.
 *        8/31/11  Added Lesson Set feature processing to allow grouping lessons together into a common area, such as for a recurring lesson time.
 *        8/17/11  PGM UNL (pgmunl) - when logged into one of 4 specific proshop users, only that person's lesson pro will show up in the pro list (case 2019).
 *        3/24/11  The Minikahda Club (minikahda) - Sort lesson types by length, then description.
 *        3/18/11  Updated calls to getActivity.buildActivitySelect to include new parameters.
 *        3/11/11  Updated lesson book calendar to show green/red color coding based on availability for all 365 days available for viewing, instead of only 100.
 *       12/06/10  When adding/updating a lesson book blocker, past times will not be blocked, and lesson book blockers will not display on past days when viewing
 *                 the lesson book, even if their date range includes that day.
 *       10/20/10  Populate new parmEmail fields
 *       10/11/10  Changed pro drop-down lists to order by the new 'sort_by' field in lessonpro5.
 *        9/16/10  Allow iCal options to be set in lesson pro configuration so iCal attachment is sent along with pro copies of lesson notifications.
 *        6/24/10  Modified alphaTable calls to pass the new enableAdvAssist parameter which is used for iPad compatability
 *        5/03/10  Expand on the length of lesson times - add more options (North Oaks needed more than 60 minutes).
 *        3/29/10  Virginia CC (virginiacc) - Order pro list by first name alphabetically
 *        2/16/09  No longer factor in lesson groups when determining red/green colorings for lesson book calendar
 *       11/09/09  Only display location column for non-golf activities
 *       11/09/09  Display lesson location on confirmation screen and in email notification
 *       11/08/09  Display location of lesson booking when showing the lesson book for a given day
 *       11/06/09  Change to accomodate booking lesson times that fall completely outside the time-sheets for non-golf activities
 *       11/03/09  Fix for deleting lesson groups
 *       11/02/09  Display location selection checkboxes when configuring lesson types and group lessons to allow for precise court choices
 *       10/29/09  Allow group lessons to be created as Clinic (shared signup list across all recurrences) or Non-Clinic (unique signup list for each recurrence)
 *       10/28/09  Fixes for handling lesson_ids correctly
 *       10/26/09  Updated edit group lesson processing so group lesson name is properly applied to all needed lesson times in the lesson book.
 *       10/23/09  Correct the db updates where activity_id was added so the index values are adjusted accordingly.
 *       10/21/09  Numerous changes to incorporate activity_id functionality into lessonbook features
 *       10/20/09  Added functionality to work with non-golf activities, booking lessons and creating/updating lesson groups will now
 *                 check for open times on the timesheets that it will need to populate, and, if found, will populate said times.
 *       10/09/09  Lesson book will only display lesson pros with the same activity_id as the activity the user is currently logged in as
 *                 when displaying the list of lesson pros (does not apply to the edit pro page)
 *       10/08/09  Added activity selection to Add/Edit lesson pro pages
 *        8/29/09  Change title on page depending on Activity
 *        5/24/09  Use calv40 calendar scripts so we can color the days to indicate if pro has available
 *                 lesson times for each day.  Also, add a pro name selection list so user can switch
 *                 lesson pros quicker (case 1498).
 *        3/02/09  Added dining request processing for group lesson bookings
 *        2/17/09  Belle Meade - display the tFlag after the member name if present in member2b (case 1612).
 *        2/13/09  Tweaks to Dining Request prompt display
 *        2/13/09  Belle Meade - default the suppress email option to yes for all requests (case 1612).
 *       10/18/08  Add member numbers (or usernames for select clubs) to names in the member list when booking lessons
 *       10/13/08  Added "Suppress Email" checkbox when booking a lesson time on proshop side (case 1454).
 *        9/02/08  Modifications to limited access proshop user restrictions
 *        9/02/08  Javascript compatability updates
 *        8/13/08  Always send lesson book emails - even if person signed up is NOT a member
 *        7/18/08  Added limited access proshop users checks
 *        5/20/08  Only display the Cancel Lesson button if we are editing an existing lesson - else hide
 *        4/09/08  Add Announcement/Bio page management area for deleting / restoring automated backups
 *        3/02/08  In reqTime check for lesson group when determining minutes between time requested and next
 *                 occupied time.  Also, reject the request if not enough time for any ltypes (case 1413).
 *        2/27/08  Update getEndTime method to check for more than one hour when calculating end time.
 *        9/06/07  If a lesson is updated and is shorter than the old lesson, then init the unused times to free them.
 *        6/20/07  Add doBlockers method to update all blockers for the pro after a blocker has been updated or removed.
 *        6/20/07  Add entry to tee time history table (teehist) for lessons
 *        6/13/07  Add 'My Blockers' to control panel.
 *        4/10/07  Congressional - add mNum next to member name in lesson book (case #1108).
 *        3/22/07  Add Quick Search feature to member list box
 *       10/27/06  Change range of advance hours for members to book times from 0-8 to 0-24.
 *        5/31/06  RDP - Check for existing lesson time when rebuilding the lesson book after a lesson time changes.
 *        5/09/06  RDP - 2 methods with addLtime name - change 2nd to addLtimeSub.
 *        4/28/06  RDP - do not show lesson type as an option if the length is too long for time selected.
 *        2/20/06  RDP - use the old lesson time name when removing the lesson times from the lesson book (replaceLtime).
 *        9/02/05  RDP - change call to isEmailValid - only pass the email address.
 *        4/28/05  Add Group Lesson - make sure lesson times already exist.
 *        4/28/05  Edit Group Lesson - list group lessons by date.
 *        4/12/05  When adding a lesson time to the lessonbook, do not build the very last time
 *                 if it equals the end time.               
 *        2/18/05  Add 5, 15 & 45 minute interval options for fragment size in lesson times/book.
 *
 ***************************************************************************************
 */                                                                                                             
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import java.lang.Math;

// foretees imports
import com.foretees.common.FeedBack;
import com.foretees.member.Member;
import com.foretees.member.MemberHelper;
import com.foretees.common.verifyLesson;
import com.foretees.common.parmEmail;
import com.foretees.common.sendEmail;
import com.foretees.common.Utilities;
import com.foretees.common.DaysAdv;
import com.foretees.common.dateOpts;
import com.foretees.common.getActivity;
import com.foretees.common.parmSlot;
import com.foretees.common.parmActivity;
import com.foretees.common.verifyActSlot;
import com.foretees.common.Connect;

public class Proshop_lesson extends HttpServlet {
 
                                 
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
 String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
 //****************************************************
 // Process the doGet call (go to doPost)
 //****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   doPost(req, resp);      // call doPost processing

 }   // end of doGet


 //****************************************************
 // Process all requests here (doPost)
 //****************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   String omit = "";

   HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   String club = (String)session.getAttribute("club");
   String user = (String)session.getAttribute("user");               // get username
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int sess_activity_id = (Integer)session.getAttribute("activity_id");
     
   Connection con = Connect.getCon(req);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY>");
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      out.println("<CENTER><BR>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   String proid = "";     // init proid
   int proidInt = 0;
   
     
   //
   //  If request is to 'Add a Pro', then go do it, else check for proid
   //
   if (req.getParameter("addPro") != null) {      // add a Pro?

      // Check Feature Access Rights for current proshop user
      if (!SystemUtils.verifyProAccess(req, "LESS_CONFIG", con, out)) {
          SystemUtils.restrictProshop("LESS_CONFIG", out);
          return;
      }
      
      addPro(req, resp, out, session, con);
      return;
   }
   if (req.getParameter("addPro2") != null) {      // add a Pro (submit)?

      // Check Feature Access Rights for current proshop user
      if (!SystemUtils.verifyProAccess(req, "LESS_CONFIG", con, out)) {
          SystemUtils.restrictProshop("LESS_CONFIG", out);
          return;
      }
      
      addPro2(req, resp, out, session, con);
      return;
   }

   //
   //  If the pro id is not passed, then go prompt for it (required on all except 'Add Pro')
   //
   if (req.getParameter("proid") == null) {     

       proid = promptProId(req, resp, out, session, con);
       
       if (proid.equals( "" )) {       // return if user prompted for pro
           return;
       }
   } else {

      proid = req.getParameter("proid");           //  lesson pro's id

      try {
          proidInt = Integer.parseInt(proid);
      } catch (Exception exc) {
          proidInt = 0;
      }
   }

   //
   //  Process according to the type of request
   //
   if (req.getParameter("addltype") != null) {      // add lesson type?

      // Check Feature Access Rights for current proshop user
      if (!SystemUtils.verifyProAccess(req, "LESS_CONFIG", con, out)) {
          SystemUtils.restrictProshop("LESS_CONFIG", out);
          return;
      }
       
      addLtype(req, resp, out, session, con, proid);       
      return;
   }
   if (req.getParameter("addlgrp") != null) {       // add group lesson?

      // Check Feature Access Rights for current proshop user
      if (!SystemUtils.verifyProAccess(req, "LESS_CONFIG", con, out)) {
          SystemUtils.restrictProshop("LESS_CONFIG", out);
          return;
      }
      
      addLgrp(req, resp, out, session, con, proid);
      return;
   }
   if (req.getParameter("addltime") != null) {      // add lesson time?

      // Check Feature Access Rights for current proshop user
      if (!SystemUtils.verifyProAccess(req, "LESS_CONFIG", con, out)) {
          SystemUtils.restrictProshop("LESS_CONFIG", out);
          return;
      }
      
      addLtime(req, resp, out, session, con, proid);
      return;
   }
   if (req.getParameter("addltype2") != null) {      // add lesson type (submit)?

      // Check Feature Access Rights for current proshop user
      if (!SystemUtils.verifyProAccess(req, "LESS_CONFIG", con, out)) {
          SystemUtils.restrictProshop("LESS_CONFIG", out);
          return;
      }
      
      addLtype2(req, resp, out, session, con, proid);
      return;
   }
   if (req.getParameter("addlgrp2") != null) {       // add group lesson (submit)?

      // Check Feature Access Rights for current proshop user
      if (!SystemUtils.verifyProAccess(req, "LESS_CONFIG", con, out)) {
          SystemUtils.restrictProshop("LESS_CONFIG", out);
          return;
      }
      
      addLgrp2(req, resp, out, session, con, proid);
      return;
   }
   if (req.getParameter("addltime2") != null) {      // add lesson time (submit)?

      // Check Feature Access Rights for current proshop user
      if (!SystemUtils.verifyProAccess(req, "LESS_CONFIG", con, out)) {
          SystemUtils.restrictProshop("LESS_CONFIG", out);
          return;
      }
      
      addLtime2(req, resp, out, session, con, proid);
      return;
   }
   if (req.getParameter("editPro") != null) {      // edit a pro?

      // Check Feature Access Rights for current proshop user
      if (!SystemUtils.verifyProAccess(req, "LESS_CONFIG", con, out)) {
          SystemUtils.restrictProshop("LESS_CONFIG", out);
          return;
      }
      
      editPro(req, resp, out, session, con, proid);
      return;
   }
   if (req.getParameter("editPro2") != null) {      // edit a pro (submit)?

      // Check Feature Access Rights for current proshop user
      if (!SystemUtils.verifyProAccess(req, "LESS_CONFIG", con, out)) {
          SystemUtils.restrictProshop("LESS_CONFIG", out);
          return;
      }
      
      addPro2(req, resp, out, session, con);        // share the addpro2 method
      return;
   }

   if (req.getParameter("editltype") != null || req.getParameter("editLT") != null) {      // edit lesson type?

      // Check Feature Access Rights for current proshop user
      if (!SystemUtils.verifyProAccess(req, "LESS_CONFIG", con, out)) {
          SystemUtils.restrictProshop("LESS_CONFIG", out);
          return;
      }
      
      editLtype(req, resp, out, session, con, proid);
      return;
   }
   if (req.getParameter("editltype2") != null) {      // edit lesson type (submit)?

      // Check Feature Access Rights for current proshop user
      if (!SystemUtils.verifyProAccess(req, "LESS_CONFIG", con, out)) {
          SystemUtils.restrictProshop("LESS_CONFIG", out);
          return;
      }
      
      editLtype2(req, resp, out, session, con, proid);
      return;
   }
   if (req.getParameter("deleteltype") != null) {      // delete lesson type

      // Check Feature Access Rights for current proshop user
      if (!SystemUtils.verifyProAccess(req, "LESS_CONFIG", con, out)) {
          SystemUtils.restrictProshop("LESS_CONFIG", out);
          return;
      }
      
      deleteLtype(req, resp, out, session, con, proid);
      return;
   }
   if (req.getParameter("editlgrp") != null || req.getParameter("editGRP") != null) {     // edit group lesson?

      // Check Feature Access Rights for current proshop user
      if (!SystemUtils.verifyProAccess(req, "LESS_CONFIG", con, out)) {
          SystemUtils.restrictProshop("LESS_CONFIG", out);
          return;
      }
      
      editLgrp(req, resp, out, session, con, proid);
      return;
   }
   if (req.getParameter("editlgrp2") != null) {      // edit group lesson (submit)?

      // Check Feature Access Rights for current proshop user
      if (!SystemUtils.verifyProAccess(req, "LESS_CONFIG", con, out)) {
          SystemUtils.restrictProshop("LESS_CONFIG", out);
          return;
      }
      
      editLgrp2(req, resp, out, session, con, proid);
      return;
   }
   if (req.getParameter("deletelgrp") != null) {      // delete group lesson 

      // Check Feature Access Rights for current proshop user
      if (!SystemUtils.verifyProAccess(req, "LESS_CONFIG", con, out)) {
          SystemUtils.restrictProshop("LESS_CONFIG", out);
          return;
      }
      
      deleteLgrp(req, resp, out, session, con, proid);
      return;
   }
   if (req.getParameter("editltime") != null || req.getParameter("editLTM") != null) {      // edit lesson time?

      // Check Feature Access Rights for current proshop user
      if (!SystemUtils.verifyProAccess(req, "LESS_CONFIG", con, out)) {
          SystemUtils.restrictProshop("LESS_CONFIG", out);
          return;
      }
      
      editLtime(req, resp, out, session, con, proid);
      return;
   }
   if (req.getParameter("editltime2") != null) {      // edit lesson time (submit)?

      // Check Feature Access Rights for current proshop user
      if (!SystemUtils.verifyProAccess(req, "LESS_CONFIG", con, out)) {
          SystemUtils.restrictProshop("LESS_CONFIG", out);
          return;
      }
      
      editLtime2(req, resp, out, session, con, proid);
      return;
   }
   if (req.getParameter("deleteltime") != null) {      // delete lesson time 

      // Check Feature Access Rights for current proshop user
      if (!SystemUtils.verifyProAccess(req, "LESS_CONFIG", con, out)) {
          SystemUtils.restrictProshop("LESS_CONFIG", out);
          return;
      }
      
      deleteLtime(req, resp, out, session, con, proid);
      return;
   }
   if (req.getParameter("deletePro") != null) {      // delete a Pro?

      // Check Feature Access Rights for current proshop user
      if (!SystemUtils.verifyProAccess(req, "LESS_CONFIG", con, out)) {
          SystemUtils.restrictProshop("LESS_CONFIG", out);
          return;
      }
      
      deletePro(req, resp, out, session, con, proid);
      return;
   }
     
   if (req.getParameter("reqtime") != null) {      // request a lesson time from lesson book?

      reqTime(req, resp, out, session, con, proid);
      return;
   }

   if (req.getParameter("reqtime2") != null) {      // update a lesson time from lesson book (submit)?

      reqTime2(req, resp, out, session, con, proid);
      return;
   }

   if (req.getParameter("groupLesson") != null) {      // request a Group Lesson time from lesson book?

      grpLesson(req, resp, out, session, con, proid);
      return;
   }

   if (req.getParameter("groupLesson2") != null) {      // update a Group Lesson from lesson book (submit)?

      grpLesson2(req, resp, out, session, con, proid);
      return;
   }

   if (req.getParameter("myblockers") != null) {       // My Blockers request from control panel?

      myBlockers(req, resp, out, session, con, proid);
      return;
   }

   if (req.getParameter("block") != null) {       // block lesson time request from control panel or book?

      blockTime(req, resp, out, session, con, proid);
      return;
   }

   if (req.getParameter("addblock") != null || req.getParameter("editblock") != null || req.getParameter("deleteblock") != null) {   // block lesson time (submit)?

      blockTime2(req, resp, out, session, con, proid);
      return;
   }

   if (req.getParameter("bio") != null) {         // request to process a pro's bio?

      // Check Feature Access Rights for current proshop user
      if (!SystemUtils.verifyProAccess(req, "LESS_CONFIG", con, out)) {
          SystemUtils.restrictProshop("LESS_CONFIG", out);
          return;
      }
      
      editBio(req, resp, out, session, con, proid);
      return;
   }

   if (req.getParameter("createSet") != null) {     // Display create new lesson set page

      createLessonSet(req, resp, out, session, con, proidInt);
      return;
   }

   if (req.getParameter("deleteSet") != null) {

      deleteLessonSet(req, resp, out, session, con, proidInt);
      return;
   }

   if (req.getParameter("sets") != null) {      // Display list of lesson sets for this pro

      displayLessonSets(req, resp, out, session, con, proidInt);
      return;
   }

   if (req.getParameter("sets2") != null) {     // Display list of lessons contained in the selected lesson set

      displayLessonSetContent(req, resp, out, session, con, proidInt);
      return;
   }

   if (req.getParameter("hist") != null) {

      displayLessonHist(req, resp, out, session, con, proidInt);
      return;
   }

     if (req.getParameter("lessonReport") != null) {      // edit a pro?

         // Check Feature Access Rights for current proshop user
         if (!SystemUtils.verifyProAccess(req, "LESS_VIEW", con, out)) {
             SystemUtils.restrictProshop("LESS_VIEW", out);
             return;
         }

         lessonReport(req, resp, out, session, con, proid);
         return;
     }
     if (req.getParameter("lessonReport2") != null) {      // edit a pro?

         // Check Feature Access Rights for current proshop user
         if (!SystemUtils.verifyProAccess(req, "LESS_VIEW", con, out)) {
             SystemUtils.restrictProshop("LESS_VIEW", out);
             return;
         }

         lessonReport2(req, resp, out, session, con, proid);
         return;
     }
     

   //**************************************************************************************
   // We fall through to the 'Display Lesson Book' Request from the menu (default!!!) 
   //**************************************************************************************
   //
   
   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "LESS_VIEW", con, out)) {
       SystemUtils.restrictProshop("LESS_VIEW", out);
       return;
   }
   
   boolean configAccess = true;
   if (!SystemUtils.verifyProAccess(req, "LESS_CONFIG", con, out)) {
       configAccess = false;
   }
   
   boolean updateAccess = true;
   if (!SystemUtils.verifyProAccess(req, "LESS_UPDATE", con, out)) {
       updateAccess = false;
   }
   
   PreparedStatement pstmt = null;
   ResultSet rs = null;
   PreparedStatement pstmt3 = null;
   ResultSet rs3 = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs2 = null;


   String [] mm_table = { "inv", "January", "February", "March", "April", "May", "June", "July", "August",
                          "September", "October", "November", "December" };

   //
   //  Num of days in each month
   //
   int [] numDays_table = { 0, 31, 0, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

   //
   //  Num of days in Feb indexed by year starting with 2000 - 2040
   //
   int [] feb_table = { 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29,  +
                            28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29 };

   String jumps = "";   // jump values
   String calDate = "";
   String temp = "";
   String dayName = "";
   String proName = "";
   String monthName = "";
   String stime = "";
   String printb = "";
     
   String ltimes1 = "";       // for legend - max 6 lesson times per day
   String lcolor1 = "";
   String ltimes2 = "";     
   String lcolor2 = "";
   String ltimes3 = "";
   String lcolor3 = "";
   String ltimes4 = "";
   String lcolor4 = "";
   String ltimes5 = "";
   String lcolor5 = "";
   String ltimes6 = "";
   String lcolor6 = "";

   String [] lgroupA = new String [10];       // Group Lessons - max 10 group lessons per day
   String [] lgcolorA = new String [10];    
   int [] stimeA = new int [10];             
   int [] etimeA = new int [10];
   int [] lgmaxA = new int [10];
   int [] lgnumA = new int [10];
   int [] lgdoneA = new int [10];
   int [] lgroupidA = new int [10];
   int [] clinicA = new int [10];

   String [] lblockA = new String [20];       // Lesson Time Blockers - max 20 blockers per day
   String [] lbcolorA = new String [20];
   int [] sbtimeA = new int [20];
   int [] ebtimeA = new int [20];
   int [] lbdoneA = new int [20];

   String ltname = "";
   String lgname = "";
   String lbname = "";
   String lbetime = "";
   String ltimename = "";
   String frags = "";
   String color = "";
   String bgcolor = "";
   String lgcolor = "";
   String lbcolor = "";
   String memname = "";
   String memid = "";
   String ltype = "";
   String email1 = "";
   String phone1 = "";
   String phone2 = "";
   String notes = "";
   String ampm = "";
   String submit = "";
   String mNum = "";
   String displayName = "";
   String skipDining = "";
   String tFlag = "";
   String activity_name = "";

   int id = 0;
   int i = 0;
   int skip = 0;
   int mon = 0;
   int tue = 0;
   int wed = 0;
   int thu = 0;
   int fri = 0;
   int sat = 0;
   int sun = 0;
   int fragment = 0;
   int smonth = 0;                // init integer date/time values
   int sday = 0;
   int syear = 0;
   int emonth = 0;
   int eday = 0;
   int eyear = 0;
   int hr = 0;
   int min = 0;
   int s_hr = 0;
   int s_min = 0;
   int s_ampm = 0;
   int e_hr = 0;
   int e_min = 0;
   int e_ampm = 0;
   int lesson_id = 0;
   int sheet_activity_id = 0;

   int jump = 0;
   int j = 0;
   int month = 0;
   int day = 0;
   int day_num = 0;
   int year = 0;
   int time = 0;
   int oldtime = 0;
   int sbtime = 0;
   int ebtime = 0;
   int block = 0;
   int num = 0;
   int length = 0;
   int billed = 0;
   int lgmax = 0;
   int lgnum = 0;
   int in_use = 0;
   int max = 365;                // 365 day calendar (forward from today, goes back to oldest lesson times)
   int count = 0;
   int yy = 0;
   int mm = 0;
   int dd = 0;
   int allID = 999;  
   
   long date = 0;
   long old_date = 0;
   long cur_date = 0;
   long old_mm = 0;               // oldest month
   long old_dd = 0;               // oldest day
   long old_yy = 0;               // oldest year
   
   boolean found = false;
   boolean allPro = false;

   DaysAdv daysArray = new DaysAdv();          // allocate an array object for 'days in adv'

   try {
      id = Integer.parseInt(proid);           // get and convert the proid
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   boolean enableAdvAssist = Utilities.enableAdvAssist(req);

   //
   //  get the jump parm if provided (location on page to jump to)
   //
   if (req.getParameter("jump") != null) {

      jumps = req.getParameter("jump");         //  jump index value for where to jump to on the page

      try {
         jump = Integer.parseInt(jumps);
      }
      catch (NumberFormatException e) {
         jump = 0;
      }
   }

   //
   //   Adjust jump so we jump to the selected line minus 3 so its not on top of page
   //
   if (jump > 3) {

      jump = jump - 3;

   } else {

      jump = 0;         // jump to top of page
   }

   //
   //  init the group lesson arrays
   //
   for (i=0; i<10; i++) {
      lgroupidA[i] = 0;
      lgroupA[i] = "";
      lgcolorA[i] = "";
      clinicA[i] = 0;
   }

   //
   //  init the lesson blocker arrays
   //
   for (i=0; i<20; i++) {
      lblockA[i] = "";
      lbcolorA[i] = "";
   }

   //
   //  Go process 'billed' change if requested
   //
   if (req.getParameter("billed") != null) {      // request to change the 'billed' setting for lesson time?

      reqBilled(req, resp, out, session, con, proid);
   }

   //
   //  If we came from menu, then do today, else we came from the calendar so determine the date
   //
   if (req.getParameter("calDate") != null) {       // if from calendar

      calDate = req.getParameter("calDate");       //  get the date requested (mm/dd/yyyy)

      //
      //  Convert the date from string (mm/dd/yyyy) to ints (month, day, year)
      //
      StringTokenizer tok = new StringTokenizer( calDate, "/" );     // space is the default token - use '/'

      temp = tok.nextToken();                    // get the mm value
      month = Integer.parseInt(temp);
      temp = tok.nextToken();                    // get the dd value
      day = Integer.parseInt(temp);
      temp = tok.nextToken();                    // get the yyyy value
      year = Integer.parseInt(temp);

      //
      //  Set the requested date to get the day name, etc.
      //
      Calendar cal = new GregorianCalendar();       // get todays date

      cal.set(Calendar.YEAR, year);                 // change to requested date
      cal.set(Calendar.MONTH, month-1);
      cal.set(Calendar.DAY_OF_MONTH, day);

      day_num = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)

   } else {

      //
      //  Get today's date and then set the date values
      //
      Calendar cal = new GregorianCalendar();       // get todays date

      year = cal.get(Calendar.YEAR);
      month = cal.get(Calendar.MONTH);
      day = cal.get(Calendar.DAY_OF_MONTH);
      day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07)

      month = month + 1;                            // month starts at zero
       
      calDate = month+ "/" +day+ "/" +year;         // set caldate for returns
   }

   dayName = day_table[day_num];                   // get name for day
   monthName = mm_table[month];                    // get name for month

   date = (year * 10000) + (month * 100) + day;    // create a date field of yyyymmdd


   //  Get today's date
   Calendar cal2 = new GregorianCalendar();       // get todays date

   yy = cal2.get(Calendar.YEAR);
   mm = cal2.get(Calendar.MONTH) + 1;
   dd = cal2.get(Calendar.DAY_OF_MONTH);

   cur_date = (yy * 10000) + (mm * 100) + dd;    // create a date field of yyyymmdd

   //
   //   if call was for Show Notes then get the notes and display a new page
   //
   if (req.getParameter("notes") != null) {

      stime = req.getParameter("time");                     //  time of the slot

      displayNotes(stime, date, id, sess_activity_id, dayName, out, con);           // display the information
      return;
   }

   //
   //   Check if call was to Block or Unblock the time
   //
   if (req.getParameter("blocktime") != null) {

      temp = req.getParameter("blocktime");                 //  current block value
      stime = req.getParameter("time");                     //  time of the slot
        
      int blockvalue = Integer.parseInt(temp);              // get in value of blocktime

      blockOneTime(stime, date, blockvalue, id, sess_activity_id, con);           // block/unblock the time
   }
   
   if (req.getParameter("skipDining") != null) {    // user wish to skip dining prompt
       
       skipDining = req.getParameter("skipDining");
   }

   //
   //  Get the name of this pro
   //
   try {

      pstmt = con.prepareStatement (
              "SELECT * FROM lessonpro5 WHERE id = ? AND activity_id = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1, id);
      pstmt.setInt(2, sess_activity_id);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      if (rs.next()) {

         StringBuffer pro_name = new StringBuffer(rs.getString("fname"));  // get first name

         String mi = rs.getString("mi");                                          // middle initial
         if (!mi.equals( "" )) {
            pro_name.append(" ");
            pro_name.append(mi);
         }
         pro_name.append(" " + rs.getString("lname"));                   // last name

         String suffix = rs.getString("suffix");                                // suffix
         if (!suffix.equals( "" )) {
            pro_name.append(" ");
            pro_name.append(suffix);
         }

         proName = pro_name.toString();                             // convert to one string
      }
      pstmt.close();

   }
   catch (Exception exc) {
   }

   //
   //  Find all lesson times for this date (save for legend)
   //
   try {

      pstmt = con.prepareStatement (
              "SELECT * FROM lessontime5 WHERE proid = ? AND activity_id = ? AND sdate <= ? AND edate >= ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1, id);
      pstmt.setInt(2, sess_activity_id);
      pstmt.setLong(3, date);
      pstmt.setLong(4, date);
      rs = pstmt.executeQuery();      // execute the prepared stmt

      while (rs.next()) {

         ltname = rs.getString("lname");
         mon = rs.getInt("mon");
         tue = rs.getInt("tue");
         wed = rs.getInt("wed");
         thu = rs.getInt("thu");
         fri = rs.getInt("fri");
         sat = rs.getInt("sat");
         sun = rs.getInt("sun");
         color = rs.getString("color");

         if ((mon == 1 && dayName.equals("Monday")) || (tue == 1 && dayName.equals("Tuesday")) ||   
             (wed == 1 && dayName.equals("Wednesday")) || (thu == 1 && dayName.equals("Thursday")) ||
             (fri == 1 && dayName.equals("Friday")) || (sat == 1 && dayName.equals("Saturday")) ||
             (sun == 1 && dayName.equals("Sunday"))) {

            if (!ltname.equals( ltimes1 ) && ltimes1.equals( "" )) {

               ltimes1 = ltname;
               lcolor1 = color;

               if (color.equalsIgnoreCase( "default" )) {
                  lcolor1 = "#F5F5DC";
               }

            } else {

               if (!ltname.equals( ltimes1 ) && !ltname.equals( ltimes2 ) && ltimes2.equals( "" )) {

                  ltimes2 = ltname;
                  lcolor2 = color;

                  if (color.equalsIgnoreCase( "default" )) {
                     lcolor2 = "#F5F5DC";
                  }

               } else {

                  if (!ltname.equals( ltimes1 ) && !ltname.equals( ltimes2 ) && !ltname.equals( ltimes3 ) && 
                      ltimes3.equals( "" )) {

                     ltimes3 = ltname;
                     lcolor3 = color;

                     if (color.equalsIgnoreCase( "default" )) {
                        lcolor3 = "#F5F5DC";
                     }

                  } else {

                     if (!ltname.equals( ltimes1 ) && !ltname.equals( ltimes2 ) && !ltname.equals( ltimes3 ) &&
                         !ltname.equals( ltimes4 ) && ltimes4.equals( "" )) {

                        ltimes4 = ltname;
                        lcolor4 = color;

                        if (color.equalsIgnoreCase( "default" )) {
                           lcolor4 = "#F5F5DC";
                        }

                     } else {

                        if (!ltname.equals( ltimes1 ) && !ltname.equals( ltimes2 ) && !ltname.equals( ltimes3 ) &&
                            !ltname.equals( ltimes4 ) && !ltname.equals( ltimes5 ) && ltimes5.equals( "" )) {

                           ltimes5 = ltname;
                           lcolor5 = color;

                           if (color.equalsIgnoreCase( "default" )) {
                              lcolor5 = "#F5F5DC";
                           }

                        } else {

                           if (!ltname.equals( ltimes1 ) && !ltname.equals( ltimes2 ) && !ltname.equals( ltimes3 ) &&
                               !ltname.equals( ltimes4 ) && !ltname.equals( ltimes5 ) && !ltname.equals( ltimes6 ) && 
                               ltimes6.equals( "" )) {

                              ltimes6 = ltname;
                              lcolor6 = color;

                              if (color.equalsIgnoreCase( "default" )) {
                                 lcolor6 = "#F5F5DC";
                              }
                           }
                        }
                     }
                  }
               }
            }
         }        // end of IF day matches
      }           // end of WHILE
      pstmt.close();
   }
   catch (Exception ignored) {
   }

   //
   //  Find all group lessons for this date (save for legend and book display)
   //
   try {

      i = 0;

      if (sess_activity_id != 0) {
          
          pstmt = con.prepareStatement (
                  "SELECT * FROM lessongrp5 " +
                  "WHERE proid = ? AND activity_id = ? AND date <= ? AND edate >= ? AND " + dayName + " = 1 AND " +
                  "(eo_week = 0 OR (MOD(DATE_FORMAT(date, '%U'), 2) = MOD(DATE_FORMAT(?, '%U'), 2)))");
          pstmt.clearParameters();
          pstmt.setInt(1, id);
          pstmt.setInt(2, sess_activity_id);
          pstmt.setLong(3, date);
          pstmt.setLong(4, date);
          pstmt.setLong(5, date);
          rs = pstmt.executeQuery();
          
      } else {
          pstmt = con.prepareStatement (
                  "SELECT * FROM lessongrp5 WHERE proid = ? AND activity_id = ? AND date = ?");

          pstmt.clearParameters();        // clear the parms
          pstmt.setInt(1, id);
          pstmt.setInt(2, sess_activity_id);
          pstmt.setLong(3, date);
          rs = pstmt.executeQuery();      // execute the prepared stmt
      }

      while (rs.next() && i < 10) {
          
         lgroupidA[i] = rs.getInt("lesson_id");
         lgroupA[i] = rs.getString("lname");
         stimeA[i] = rs.getInt("stime");
         etimeA[i] = rs.getInt("etime");
         lgmaxA[i] = rs.getInt("max");
         lgcolorA[i] = rs.getString("color");
         clinicA[i] = rs.getInt("clinic");
           
         //
         //  determine how many members are currently registered for this group lesson
         //
         if (clinicA[i] == 1 && sess_activity_id != 0) {

             // If this group lesson is a clinic, grab all signups under its lesson_id, regardless of date
             pstmt2 = con.prepareStatement (
                     "SELECT memname FROM lgrpsignup5 WHERE lesson_id = ?");

             pstmt2.clearParameters();        // clear the parms
             pstmt2.setInt(1, lgroupidA[i]);
             rs2 = pstmt2.executeQuery();      // execute the prepared stmt
             
         } else {
             pstmt2 = con.prepareStatement (
                     "SELECT memname FROM lgrpsignup5 WHERE proid = ? AND lname = ? AND date = ?");

             pstmt2.clearParameters();        // clear the parms
             pstmt2.setInt(1, id);
             pstmt2.setString(2, lgroupA[i]);
             pstmt2.setLong(3, date);
             rs2 = pstmt2.executeQuery();      // execute the prepared stmt
         }

         while (rs2.next()) {

            if (!rs2.getString("memname").equals( "" )) {      // if member name exists
              
               lgnumA[i]++;    
            }
         }           // end of WHILE
         pstmt2.close();

         i++;
      }           // end of WHILE
      pstmt.close();
   }
   catch (Exception ignored) {
   }

   //
   //  Find all blockers for this date (save for book display)
   //
   if (date >= cur_date) {
       
       try {

          i = 0;

          pstmt = con.prepareStatement (
                  "SELECT * FROM lessonblock5 WHERE proid = ? AND activity_id = ? AND sdate <= ? AND edate >= ?");

          pstmt.clearParameters();        // clear the parms
          pstmt.setInt(1, id);
          pstmt.setInt(2, sess_activity_id);
          pstmt.setLong(3, date);
          pstmt.setLong(4, date);
          rs = pstmt.executeQuery();      // execute the prepared stmt

          while (rs.next() && i < 20) {

             lbname = rs.getString("lbname");
             sbtime = rs.getInt("stime");
             ebtime = rs.getInt("etime");
             mon = rs.getInt("mon");
             tue = rs.getInt("tue");
             wed = rs.getInt("wed");
             thu = rs.getInt("thu");
             fri = rs.getInt("fri");
             sat = rs.getInt("sat");
             sun = rs.getInt("sun");
             lbcolor = rs.getString("color");

             //
             //  if no days are selected, then that implies it is for one day only (today),
             //  else check for a matching day
             //
             if ((mon == 0 && tue == 0 && wed == 0 && thu == 0 && fri == 0 && sat == 0 && sun == 0) ||
                 (mon == 1 && dayName.equals("Monday")) || (tue == 1 && dayName.equals("Tuesday")) ||
                 (wed == 1 && dayName.equals("Wednesday")) || (thu == 1 && dayName.equals("Thursday")) ||
                 (fri == 1 && dayName.equals("Friday")) || (sat == 1 && dayName.equals("Saturday")) ||
                 (sun == 1 && dayName.equals("Sunday"))) {

                lblockA[i] = lbname;
                sbtimeA[i] = sbtime;
                ebtimeA[i] = ebtime;
                lbcolorA[i] = lbcolor;

                i++;
             }
          }           // end of WHILE
          pstmt.close();
       }
       catch (Exception ignored) {
       }
   }

   //
   //  Build the page to display the lesson book
   //
   if ((req.getParameter("printb") != null)) {        // if request to print or export to excel

      printb = req.getParameter("printb");         //  get the rpint type

      if (printb.equals( "excel" )) {

         resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
         resp.setHeader("Content-Disposition", "attachment;filename=\"Proshop_lesson.xls\"");
      }

      out.println(SystemUtils.HeadTitle("Proshop Print Lesson Book"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");

   } else {       // not a print or export
      
      //
      //  Get today's date
      //
      Calendar cal = new GregorianCalendar();             // get current date & time (Central Time)
      int thisyear = cal.get(Calendar.YEAR);
      int thismonth = cal.get(Calendar.MONTH)+1;
      int thisday = cal.get(Calendar.DAY_OF_MONTH);

      long thisdate = (thisyear * 10000) + (thismonth * 100) + thisday;      // get today

      try {

         //   statement to locate available days
         pstmt = con.prepareStatement (
                 "SELECT time FROM lessonbook5 " +
                 "WHERE proid = ? AND activity_id = ? AND date = ? AND block = 0 AND in_use = 0 AND memname = '' AND ltype = '' " +
                 "LIMIT 1");

         //
         //  Build an array containing indicators of this pro's lesson times availability for the next 100 days.
         //  This is used when building the calendar to indicate if the pro is available on the given days.
         //
         for (i = 0; i <= max; i++) {

           //
           //  Check if any lesson times are available
           //
           pstmt.clearParameters();        // clear the parms
           pstmt.setInt(1, id);
           pstmt.setInt(2, sess_activity_id);
           pstmt.setLong(3, thisdate);
           rs = pstmt.executeQuery();      // execute the prepared stmt

           if (rs.next()) {

              if (club.equals("ballantyne") && sess_activity_id == 1 && i <= 3) {
                  daysArray.days[i] = 3;        // mark the days the members have access to in yellow, if available
              } else {
                  daysArray.days[i] = 1;        // mark this one in lime green (available)
              }

           } else {

              daysArray.days[i] = 2;        // mark this one in red (sienna)
           }

           //
           //  Get next day
           //
           cal.add(Calendar.DATE,1);
           thisyear = cal.get(Calendar.YEAR);
           thismonth = cal.get(Calendar.MONTH) +1;
           thisday = cal.get(Calendar.DAY_OF_MONTH);

           thisdate = (thisyear * 10000) + (thismonth * 100) + thisday;
            
         }         // end of FOR loop for daysarray
      
         pstmt.close();
         
      }
      catch (Exception ignored) {
      }

     
      //
      //  Build the HTML page to prompt user for a specific time slot
      //
      out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
      out.println("<HTML>");
      out.println("<HEAD><link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">");
      out.println("<script language=\"JavaScript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>");
      out.println("<title>Proshop Lesson Book</title>");

      // prevent this from happening on the iPad
      if (enableAdvAssist) {
          out.println("<script type='text/javascript'>");
          out.println("<!--");
          out.println("function jumpToHref(anchorstr) {");
          out.println(" if (location.href.indexOf(anchorstr)<0) {");
          out.println("  location.href = anchorstr;");
          out.println(" }");
          out.println("}");
          out.println("// -->");
          out.println("</script>");                               // End of script
      }

      // include files for dynamic calendars
      out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv40-styles.css\">");
      out.println("<script language=\"javascript\" src=\"/" +rev+ "/calv40-scripts.js\"></script>");      // use new cal script
      
      // include this to override default setting (must include this after including main cal script)
      out.println("<script type='text/javascript'>");
      out.println("select_old_days = true;");
      out.println("</script>");

      out.println("</HEAD>");

      out.println("<body onLoad='jumpToHref(\"#jump" +jump+ "\");' bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\">");
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
         
   }
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"></font><center>");

   if (enableAdvAssist) out.println("<a name=\"jump0\"></a>");     // create a default jump label (start of page)

   out.println("<table border=\"0\" align=\"center\" width=\"100%\">");        // whole page
   out.println("<tr><td align=\"center\">");

   if (printb.equals( "" )) {     // if not a print or export

      //
      //  Find the oldest date with a lesson book entry
      //
      try {

         pstmt = con.prepareStatement (
                 "SELECT MIN(date) FROM lessonbook5");

         pstmt.clearParameters();        // clear the parms
         rs = pstmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            old_date = rs.getLong(1);
         }       
         pstmt.close();
      }
      catch (Exception ignored) {
      }

      //
      //  Determine oldest date values - month, day, year
      //
      old_yy = old_date / 10000;
      old_mm = (old_date - (old_yy * 10000)) / 100;
      old_dd = old_date - ((old_yy * 10000) + (old_mm * 100));


     out.println("<table border=\"0\" align=\"center\">");        // table for cmd tbl & cals
     out.println("<tr align=\"left\"><td align=\"left\" valign=\"top\" width=\"130\">");

      //
      //  Build Control Panel
      //
      out.println("<table border=\"1\" width=\"120\" cellspacing=\"3\" cellpadding=\"3\" bgcolor=\"8B8970\" align=\"left\">");
      out.println("<tr>");
      out.println("<td align=\"center\"><font color=\"#000000\" size=\"3\"><b>Control Panel</b><br>");
      out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
      out.println("<a href=\"Proshop_lesson?proid=" +id+ "&calDate=" +calDate+ "\" title=\"Refresh This Page\" alt=\"Refresh\">");
      out.println("Refresh</a>");

      out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
      out.println("<a href=\"Proshop_lesson?proid=" +id+ "&calDate=" +calDate+ "&printb=print\" target=\"_blank\" title=\"Print This Page\" alt=\"Print\">");
      out.println("Print Lesson Book</a>");

      out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
      out.println("<a href=\"Proshop_lesson?proid=" +id+ "&calDate=" +calDate+ "&printb=excel\" target=\"_blank\" title=\"Print To Excel\" alt=\"Print\">");
      out.println("Export To Excel</a>");
      
      out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
      out.println("<a href=\"Proshop_lesson?proid="+id+"&lessonReport=yes target=\"_blank\">");
      out.println("Lesson Report</a>");
      
//      out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
//      out.println("<a href=\"Proshop_lesson?XlessonReport=yes target=\"_blank\">");
//      out.println("Lesson Report</a>");
      

      if (configAccess && updateAccess) {  // only allow if proshop user has appropriate access
          out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
          out.println("<a href=\"Proshop_lesson?proid=" +id+ "&calDate=" +calDate+ "&date=" +date+ "&block=yes\" target=\"bot\" title=\"Block Times\" alt=\"Block Times\">");
          out.println("Block Times</a>");
      
          out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
          out.println("<a href=\"Proshop_lesson?proid=" +id+ "&calDate=" +calDate+ "&date=" +date+ "&myblockers=yes\" target=\"bot\" title=\"List My Blockers\" alt=\"List My Blockers\">");
          out.println("My Blockers</a>");
      }

      out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
      out.println("<a href=\"Proshop_lesson?sets&proid=" +id+ "\" target=\"bot\" title=\"View My Lesson Sets\" alt=\"View My Lesson Sets\">");
      out.println("My Lesson Sets</a>");
      
      out.println("</font></td></tr></table>");

     out.println("</td>");                             
     out.println("<td align=\"center\" valign=\"top\" width=\"100\">");     // spacer

     out.println("</td>");                                
     out.println("<td align=\"center\" valign=\"top\" width=\"300\">");     // column for calendars

      //
      //  Form for building the Calendars
      //
      out.println("<form action=\"Proshop_lesson\" method=\"post\" target=\"bot\" name=\"frmLoadDay\">");
      out.println("<input type=\"hidden\" name=\"calDate\" value=\"\">");
      out.println("<input type=\"hidden\" name=\"proid\" value=\"" +id+ "\">");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" +jump+ "\">");
      out.println("<input type=\"hidden\" name=\"old_mm\" value=\"" +old_mm+ "\">");   // oldest month (for js)
      out.println("<input type=\"hidden\" name=\"old_dd\" value=\"" +old_dd+ "\">");   // oldest day
      out.println("<input type=\"hidden\" name=\"old_yy\" value=\"" +old_yy+ "\">");   // oldest year

      out.println("</form>");

      // table for calendars built by js

      out.println("<table align=center border=0 height=150>\n<tr valign=top>\n<td>");

      out.println("<div id=cal_elem_0 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>");

      out.println("</td>\n</tr>\n</table>");

      Calendar cal_date = new GregorianCalendar(); //Calendar.getInstance();
      int cal_year = cal_date.get(Calendar.YEAR);
      int cal_month = cal_date.get(Calendar.MONTH) + 1;
      int cal_day = cal_date.get(Calendar.DAY_OF_MONTH);

      out.println("<script type=\"text/javascript\">");
      
      out.println("var g_cal_bg_color = '#F5F5DC';\n");
      out.println("var g_cal_header_color = '#8B8970';\n");
      out.println("var g_cal_border_color = '#8B8970';\n");

      out.println("var g_cal_count = 1;"); // num of calendars
      out.println("var g_cal_year = new Array(g_cal_count - 1);\n");
      out.println("var g_cal_month = new Array(g_cal_count - 1);\n");
      out.println("var g_cal_beginning_month = new Array(g_cal_count - 1);\n");
      out.println("var g_cal_ending_month = new Array(g_cal_count - 1);\n");
      out.println("var g_cal_beginning_day = new Array(g_cal_count - 1);\n");
      out.println("var g_cal_ending_day = new Array(g_cal_count - 1);\n");
      out.println("var g_cal_beginning_year = new Array(g_cal_count - 1);\n");
      out.println("var g_cal_ending_year = new Array(g_cal_count - 1);\n");

      // set calendar date parts
//      out.println("g_cal_month[0] = " + cal_month + ";\n"); // starting month for calendar
//      out.println("g_cal_year[0] = " + cal_year + ";\n"); // starting year for calendar
      out.println("g_cal_month[0] = " + month + ";\n"); // starting month for calendar (from caller or today)
      out.println("g_cal_year[0] = " + year + ";\n"); // starting year for calendar
      out.println("g_cal_beginning_month[0] = " + old_mm + ";\n"); // oldest month calendar can navigate to
      out.println("g_cal_beginning_year[0] = " + old_yy + ";\n"); // oldest year
      out.println("g_cal_beginning_day[0] = " + old_dd + ";\n"); // oldest day
      out.println("g_cal_ending_month[0] = " + cal_month + ";\n"); // future month calendar can navigate to
      out.println("g_cal_ending_day[0] = " + cal_day + ";\n"); // future day
      out.println("g_cal_ending_year[0] = " + (cal_year + 1) + ";\n"); // future year
      
         out.print("var daysArray = new Array(");
         int js_index = 0;
         for (js_index = 0; js_index <= max; js_index++) {
            out.print(daysArray.days[js_index]);
            if (js_index != max) out.print(",");
         }
         out.println(");");
         
         out.println("var max = " + max + ";");
         
      out.println("doCalendar('0');"); // display calendar
      
      out.println("</script>");

     out.println("</td>");
     out.println("<td align=\"center\" valign=\"top\" width=\"250\">");     // spacer

     out.println("</td></tr></table>");   // end of table for control panel and cals

    out.println("</td></tr>");                   // end of row for whole page table
    out.println("<tr><td align=\"center\">");
    
    
      //
      //  Get the existing pro ids - if only one, then skip the selection list
      //
      try {

         pstmt = con.prepareStatement (
                 "SELECT COUNT(*) FROM lessonpro5");

         pstmt.clearParameters();        // clear the parms
         rs = pstmt.executeQuery();      // execute the prepared pstmt

         while (rs.next()) {

            count = rs.getInt(1);          // get number of pros
         }
         pstmt.close();
      }
      catch (Exception exc) {

         count = 0;      // not found
      }
    
      //
      //  Display a selection list of available pros
      //
      if (count > 1) {          // if more than one pro

         out.println("<form action=\"Proshop_lesson\" method=\"post\" name=\"pform\">");

         out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
                  
         out.println("<br><b>Lesson Pro:</b>&nbsp;&nbsp;");
         out.println("<select size=\"1\" name=\"proid\" onChange=\"document.pform.submit()\">");

         try {

            pstmt = con.prepareStatement (
                    "SELECT * FROM lessonpro5 where activity_id = ? ORDER BY sort_by");

            pstmt.clearParameters();        // clear the parms
            pstmt.setInt(1, sess_activity_id);
            rs = pstmt.executeQuery();      // execute the prepared pstmt

            while (rs.next()) {

               StringBuffer pro_name = new StringBuffer(rs.getString("fname"));  // get first name

               String mi = rs.getString("mi");                                   // middle initial
               if (!mi.equals( "" )) {
                  pro_name.append(" ");
                  pro_name.append(mi);
               }
               pro_name.append(" " + rs.getString("lname"));                   // last name

               String suffix = rs.getString("suffix");                         // suffix
               if (!suffix.equals( "" )) {
                  pro_name.append(" ");
                  pro_name.append(suffix);
               }

               String proName2 = pro_name.toString();                             // convert to one string

               int id2 = rs.getInt("id");                   // get the proid

               if (club.equals("pgmunl")) {

                   if ((user.equals("proshopjim") && id2 != 2) || (user.equals("proshopmike") && id2 != 3) ||
                       (user.equals("proshopgreg") && id2 != 4) || (user.equals("proshopchris") && id2 != 5)) {

                       continue;    // If user doesn't match specific pro_id, skip displaying it in the list.
                   }
               }

               if (id == id2 ) {                 // if this is the current pro
                  out.println("<option selected value=\"" +id2+ "\">" +proName2+ "</option>");
               } else {
                  out.println("<option value=\"" +id2+ "\">" +proName2+ "</option>");
               }
            }
            pstmt.close();

         }
         catch (Exception exc) {

            out.println("<BR><BR>Unable to access the Database.");
            out.println("<BR>Please try again later.");
            out.println("<BR><BR>If problem persists, contact customer support.");
            out.println("<BR><BR>" + exc.getMessage());
            out.println("<BR><BR>");
         }

         out.println("</select>");
         out.println("</form>");
      }
      
      
      
      //**********************************************************
      //  Continue with instructions and tee sheet
      //**********************************************************
      out.println("<table cellpadding=\"3\" align=\"center\">");
      out.println("<tr><td bgcolor=\"#336633\"><font color=\"#FFFFFF\" size=\"2\">");
      out.println("&nbsp;&nbsp;<b>Instructions:</b>");
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;To select a lesson time, just click on the button containing the time (1st column).&nbsp;&nbsp; ");
      if (configAccess && updateAccess) {       // only allow if proshop user has appropriate access
          out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ");
          out.println("To block times from members, use the <b>Block Times</b> option in the Control Panel");
          out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ");
          out.println("or just click the <b>B</b> in the time slot.");
      }
      out.println("</font></td></tr></table>");
        
   } else {

      if (printb.equals( "print" )) {
        
         out.println("<table border=\"0\" align=\"center\"><tr>");
         out.println("<form method=\"link\" action=\"javascript:self.print()\">");
         out.println("<td align=\"center\">");
         out.println("<button type=\"submit\" style=\"text-decoration:underline; background:#8B8970\">Print Book</button>");
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;</td></form>");

         out.println("<form><td align=\"left\">&nbsp;&nbsp;&nbsp;&nbsp;");
         out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" Value=\"  Close  \" onClick='self.close()' alt=\"Close\">");
         out.println("</td></form></tr></table>");
      }
   }

   out.println("<font size=\"1\"><br></font><font size=\"3\">");
   out.println("Lesson Book For&nbsp;&nbsp;<b>" +proName+ "</b>&nbsp;&nbsp;&nbsp;&nbsp;Date:&nbsp;&nbsp;<b>" + dayName + "&nbsp;&nbsp;" + month + "/" + day + "/" + year + "</b>");
   out.println("</font><font size=\"2\"><br><br>");

   if (!ltimes1.equals( "" )) {

      out.println("<b>Lesson Book Legend</b><br>");

      out.println("<button type=\"button\" style=\"background:" + lcolor1 + "\">" + ltimes1 + "</button>");

      if (!ltimes2.equals( "" )) {

         out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
         out.println("<button type=\"button\" style=\"background:" + lcolor2 + "\">" + ltimes2 + "</button>");

         if (!ltimes3.equals( "" )) {

            out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<button type=\"button\" style=\"background:" + lcolor3 + "\">" + ltimes3 + "</button>");

            if (!ltimes4.equals( "" )) {

               out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("<button type=\"button\" style=\"background:" + lcolor4 + "\">" + ltimes4 + "</button>");

               if (!ltimes5.equals( "" )) {

                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
                  out.println("<button type=\"button\" style=\"background:" + lcolor5 + "\">" + ltimes5 + "</button>");

                  if (!ltimes6.equals( "" )) {

                     out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
                     out.println("<button type=\"button\" style=\"background:" + lcolor6 + "\">" + ltimes6 + "</button>");
                  }
               }
            }
         }
      }
   }
    
   for (i=0; i<10; i++) {        // add a button for each group lesson
  
      if (!lgroupA[i].equals( "" )) {

         out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
         out.println("<button type=\"button\" style=\"background:" + lgcolorA[i] + "\">" + lgroupA[i] + "</button>");
      }
   }

   out.println("</font>");

   //
   //  Table for the lesson book
   //
   out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\" cellpadding=\"5\">");

   out.println("<tr bgcolor=\"#336633\">");
      out.println("<td align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<b>Time</b>");
         out.println("</font></td>");

      out.println("<td align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<b>Member/Activity</b>");
         out.println("</font></td>");

      out.println("<td align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<b>Lesson Type</b>");
         out.println("</font></td>");

      if (sess_activity_id != 0) {
          out.println("<td align=\"center\">");
             out.println("<font color=\"#FFFFFF\" size=\"2\">");
             out.println("<b>Location</b>");
             out.println("</font></td>");
      }

      out.println("<td align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<b># of Members</b>");
         out.println("</font></td>");
         
     if (club.equals("westchester") && printb.equals("print")) {
         out.println("<td align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<b>Member #</b>");
         out.println("</font></td>");
     }

      out.println("<td align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<b>Phone</b>");
         out.println("</font></td>");
     
     if (club.equalsIgnoreCase("interlachen")) {
         out.println("<td align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<b>Email</b>");
         out.println("</font></td>");
     }

      out.println("<td align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<b>Billed</b>");
         out.println("</font></td>");

      out.println("<td align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<b>Notes</b>");
         out.println("</font></td>");

      out.println("<td align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<b>History</b>");
         out.println("</font></td>");

      if (configAccess && updateAccess) {       // only allow if proshop user has appropriate access
         out.println("<td align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<b>Block</b>");
         out.println("</font></td>");
      }
      
         
   out.println("</tr>");

   oldtime = 0;            // init

   //
   //  Get the lesson times for this date and pro
   //
   try {

      pstmt = con.prepareStatement (
              "SELECT * FROM lessonbook5 " +
              "WHERE proid = ? AND activity_id = ? AND DATE = ? ORDER BY time");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1, id);
      pstmt.setInt(2, sess_activity_id);
      pstmt.setLong(3, date);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      while (rs.next()) {

         lesson_id = rs.getInt("recid");
         lgname = rs.getString("lgname");
         ltimename = rs.getString("ltname");
         time = rs.getInt("time");       
         block = rs.getInt("block");
         memname = rs.getString("memname");
         memid = rs.getString("memid");
         num = rs.getInt("num");
         length = rs.getInt("length");
         billed = rs.getInt("billed");
         in_use = rs.getInt("in_use");
         ltype = rs.getString("ltype");
         phone1 = rs.getString("phone1");
         phone2 = rs.getString("phone2");
         color = rs.getString("color");
         notes = rs.getString("notes");
         sheet_activity_id = rs.getInt("sheet_activity_id");
         
         if ((club.equals("westchester") && printb.equals("print")) || club.equals("interlachen")) {
              mNum = "";
              email1 = "";
              try {
                  pstmt3 = con.prepareStatement("SELECT memNum, email FROM member2b WHERE username = ?");
                  pstmt3.clearParameters();        // clear the parms
                  pstmt3.setString(1, memid);
                  rs3 = pstmt3.executeQuery();      // execute the prepared pstmt3

                  while (rs3.next()) {
                      mNum = rs3.getString("memNum");
                      email1 = rs3.getString("email");

                  }
              } catch (Exception ignore) {
              } finally {
                  Connect.close(rs3, pstmt3);
              }
          }
         //
         //  Determine if we should skip this time
         //
         skip = 0;      // no skip
         lgcolor = "";
         lgmax = 0;
         lgnum = 0;
         
         if (!lgname.equals( "" )) {          // if lesson group
           
            lgloop1:
            for (i=0; i<10; i++) {           // find group lesson info

               if (lgname.equals( lgroupA[i] )) {       

                  lgcolor = lgcolorA[i];                           // save color
                  lgmax = lgmaxA[i];                               // save max # of members
                  lgnum = lgnumA[i];                               // save current # of members signed up

                  if (lgdoneA[i] > 0) {                            // did we already do this group lesson ?

                     skip = 1;                                     // yes, skip this time

                  } else {

                     lgdoneA[i] = 1;                               // no, we have now
                  }
                  break lgloop1;
               }
            }
         }

         lbname = "";   // init blocker name to none
         lbcolor = "";

         for (i=0; i<20; i++) {       // check for blocker

            if (time >= sbtimeA[i] && time <= ebtimeA[i]) {       // if within a blocker

               lbname = lblockA[i];                             // save the name of this blocker
               lbcolor = lbcolorA[i];                           // save color

               if (lbdoneA[i] > 0) {                            // did we already do this blocker ?

                  skip = 2;                                     // yes, skip this time

               } else {

                  lbdoneA[i] = 1;                               // no, we have now

                  hr = ebtimeA[i] / 100;                       // determine end time value
                  min = ebtimeA[i] - (hr * 100);

                  StringBuffer endTime = new StringBuffer("");    // create a string for the end time
                  if (hr > 12) {
                     hr -= 12;               // convert to conventional time
                     endTime.append(hr);
                     hr += 12;               // restore
                  } else {
                     endTime.append(hr);
                  }
                  if (min < 10) {
                     endTime.append(":0");
                  } else {
                     endTime.append(":");
                  }
                  endTime.append(min);

                  if (hr == 12) {
                     endTime.append(" PM");
                  } else {
                     if (hr > 12) {
                        endTime.append(" PM");
                     } else {
                        endTime.append(" AM");
                     }
                  }

                  lbetime = endTime.toString();         // convert to one string
               }
            }
         }

         //
         //  Safety - we have had some problems where duplicate entries are created.  If this occurs, do not display the 2nd copy
         //
         if (time == oldtime && memname.equals( "" )) {      // if same as last and memname is empty 
           
            skip = 3;           // skip this one

            String errorMsg = "Error in Proshop_lesson (display lesson book) - duplicate time entries: club = " +club+ ", date = " +date+ ", proid = " +proid+ ", time = " +time;
            SystemUtils.logError(errorMsg);        // log the error message
              
         } else {
           
            oldtime = time;     // save this time
         }


         if (skip < 2) {         //  on pro side only skip subsequent blocks
           
            //
            //  Display a row for each lesson time
            //
            if (!lgcolor.equals( "" )) {          // if group lesson color
               bgcolor = lgcolor;                 // use it
            } else {
               if (!lbcolor.equals( "" )) {          // if blocker color
                  bgcolor = lbcolor;                 // use it
               } else {
                  bgcolor = color;                   // set table cell color
               }
            }
              
            if (bgcolor.equals("Default")) {
               bgcolor = "#F5F5DC";              //default
            }

            hr = time / 100;           // determine time values
            min = time - (hr * 100);

            ampm = " AM";
            if (hr == 12) {
               ampm = " PM";
            }
            if (hr > 12) {
               ampm = " PM";
               hr = hr - 12;    // convert to conventional time
            }

            j++;                                           // increment the jump label index
            out.println("<a name=\"jump" +j+ "\"></a>");   // create a jump label for returns

            out.println("<tr>");         // new table row

            if (!lgname.equals( "" )) {               // if group lesson time
                
               if (printb.equals( "" ) && skip == 0 && updateAccess) {        // if not printing and not skipping 

                  out.println("<form action=\"Proshop_lesson\" method=\"post\" target=\"bot\">");
                  out.println("<input type=\"hidden\" name=\"lesson_id\" value=\"" +lesson_id+ "\">");
                  out.println("<input type=\"hidden\" name=\"proid\" value=\"" +id+ "\">");
                  out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
                  out.println("<input type=\"hidden\" name=\"date\" value=\"" +date+ "\">");
                  out.println("<input type=\"hidden\" name=\"time\" value=\"" +time+ "\">");
                  out.println("<input type=\"hidden\" name=\"jump\" value=\"" +j+ "\">");
                  out.println("<input type=\"hidden\" name=\"day\" value=\"" +dayName+ "\">");
                  out.println("<input type=\"hidden\" name=\"groupLesson\" value=\"" +lgname+ "\">"); // indicate grp lesson request

                  out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");

                  //
                  //  Column 1 - submit button 
                  //
                  if (min < 10) {                                 // if min value is only 1 digit
                     out.println("<input type=\"submit\" name=\"submit\" value=\"" + hr + ":0" + min + ampm + "\" alt=\"submit\">");
                  } else {                                        // min value is 2 digits
                     out.println("<input type=\"submit\" name=\"submit\" value=\"" + hr + ":" + min + ampm + "\" alt=\"submit\">");
                  }
                  out.println("</font></td></form>");

               } else {

                  out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  if (min < 10) {
                     out.println(hr + ":0" + min + ampm);
                  } else {
                     out.println(hr + ":" + min + ampm);
                  }
                  out.println("</font></td>");
               }

            } else {    // normal lesson time or blocker

               if (!lbname.equals( "" ) || block == 1) {            // if blocker time

                  out.println("<td align=\"center\">");    
                  out.println("<font size=\"2\">");
                  if (min < 10) {
                     out.println(hr + ":0" + min + ampm);
                  } else {
                     out.println(hr + ":" + min + ampm);
                  }
                  if (block == 0) {            
                     out.println("<br> to <br>" +lbetime);         // stime to etime for Blockers
                  }
                  out.println("</font></td>");

               } else {

                  if (!updateAccess) {          // proshop user doesn't have access to modifying enries
                      
                      out.println("<td align=\"center\">");    
                      out.println("<font size=\"2\">");
                      if (min < 10) {
                         out.println(hr + ":0" + min + ampm);
                      } else {
                         out.println(hr + ":" + min + ampm);
                      }
                      out.println("</font></td>");
                      
                  } else {
                   
                      //
                      // if lesson time not currently in use, not a print, and not a subsequent time for lesson
                      //
                      boolean okSub = false;

                      if (in_use == 0 && printb.equals( "" )) {

                         if (!memname.equals( "" )) {       // if member name exists in lesson time

                            if (!ltype.equals( "" )) {      // and lesson type specified (if not, no submit)

                               okSub = true;                // create submit button
                            }
                         } else {                 // no member name

                            okSub = true;                // create submit button
                         }
                      }

                      if (okSub == true) {

                         out.println("<form action=\"Proshop_lesson\" method=\"post\" target=\"_top\">");
                         out.println("<input type=\"hidden\" name=\"lesson_id\" value=\"" +lesson_id+ "\">");
                         out.println("<input type=\"hidden\" name=\"proid\" value=\"" +id+ "\">");
                         out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
                         out.println("<input type=\"hidden\" name=\"date\" value=\"" +date+ "\">");
                         out.println("<input type=\"hidden\" name=\"time\" value=\"" +time+ "\">");
                         out.println("<input type=\"hidden\" name=\"jump\" value=\"" +j+ "\">");
                         out.println("<input type=\"hidden\" name=\"day\" value=\"" +dayName+ "\">");
                         out.println("<input type=\"hidden\" name=\"reqtime\" value=\"yes\">");       // indicate a request

                         out.println("<td align=\"center\">");
                         out.println("<font size=\"2\">");

                         //
                         //  Column 1 - submit button (time)
                         //
                         if (min < 10) {                                 // if min value is only 1 digit
                            out.println("<input type=\"submit\" name=\"submit\" value=\"" + hr + ":0" + min + ampm + "\" alt=\"submit\">");
                         } else {                                        // min value is 2 digits
                            out.println("<input type=\"submit\" name=\"submit\" value=\"" + hr + ":" + min + ampm + "\" alt=\"submit\">");
                         }

                         out.println("</font></td></form>");

                      } else {

                         out.println("<td align=\"center\">");
                         out.println("<font size=\"2\">");
                         if (min < 10) {
                            out.println(hr + ":0" + min + ampm);
                         } else {
                            out.println(hr + ":" + min + ampm);
                         }
                         out.println("</font></td>");
                      }
                  }
               }
            }

            if (!lgname.equals( "" )) {          // if group lesson time
              
               out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
               out.println("<font size=\"2\">");
               out.println(lgname);
                 
            } else {
              
               if (!lbname.equals( "" ) && printb.equals( "" ) && skip == 0) {  // if blocker, first time and not printing

                  out.println("<form action=\"Proshop_lesson\" method=\"post\" target=\"bot\">");
                  out.println("<input type=\"hidden\" name=\"lesson_id\" value=\"" +lesson_id+ "\">");
                  out.println("<input type=\"hidden\" name=\"proid\" value=\"" +id+ "\">");
                  out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
                  out.println("<input type=\"hidden\" name=\"date\" value=\"" +date+ "\">");
                  out.println("<input type=\"hidden\" name=\"jump\" value=\"" +j+ "\">");
                  out.println("<input type=\"hidden\" name=\"day\" value=\"" +dayName+ "\">");
                  out.println("<input type=\"hidden\" name=\"block\" value=\"yes\">");
                  out.println("<input type=\"hidden\" name=\"lbname\" value=\"" +lbname+ "\">");
                    
                  out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\" colspan=\"7\">"); // use rest of row
                  out.println("<font size=\"2\"><br>");
                  
                  // only display link if proshop user has appropriate access
                  if (configAccess && updateAccess) {
                      out.println("<input type=\"submit\" name=\"submit\" value=\"" +lbname+ "\" alt=\"submit\">");
                  } else {
                      out.println(lbname);
                  }
                  
                  out.println("</form>");

               } else {

                  if (block == 0 || !lbname.equals( "" )) {      // if not individually blocked

                     out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                     out.println("<font size=\"2\">");

                     if (!memname.equals( "" )) {

                        if (club.equals( "congressional" ) && !memid.equals( "")) {    // Congressional wants mNums next to names

                           mNum = getmNum(memid, con);                 // get the mnum for this member
                             
                           displayName = memname + " " + mNum;    // combine
                             
                        } else {

                           if (club.equals( "bellemeadecc" ) && !memid.equals( "")) {    // Belle Meade wants tflags next to names

                              tFlag = gettFlag(memid, con);                 // get the tflag for this member
                             
                              if (!tFlag.equals( "" )) {
                                 
                                 displayName = memname + " " + tFlag;          // combine
                                 
                              } else {
                                 
                                 displayName = memname;           // only display the member name
                              }
                              
                           } else {                               // no customs
                             
                              displayName = memname;              // only display the member name
                           }
                        }
                          
                        out.println(displayName);

                     } else {

                        out.println("&nbsp;");
                     }
                  }
               }
            }
            out.println("</font></td>");

            if (lbname.equals( "" )) {         // if no group blocker

               if (block == 1) {              // if individually blocked

                  out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\" colspan=\"7\">"); // use rest of row
                  out.println("<font size=\"3\">");
                  out.println("--Unavailable--");
                  out.println("</font></td>");

               } else {                       // not individually blocked

                  out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                  out.println("<font size=\"2\">");
                  if (skip == 1) {
                     out.println("&nbsp;");
                  } else {
                     if (!lgname.equals( "" )) {
                        out.println("Group Lesson");
                     } else {
                        if (!ltype.equals( "" )) {
                           out.println(ltype);
                        } else {
                           out.println("&nbsp;");
                        }
                     }
                  }
                  out.println("</font></td>");

                  if (sess_activity_id != 0) {
                      out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                      out.println("<font size=\"2\">");
                      activity_name = "";
                      if (skip == 1) {
                         out.println("&nbsp;");
                      } else {
                         if (!lgname.equals( "" ) || memname.equals("") || ltype.equals("")) {
                            out.println("&nbsp;");
                         } else {
                             try {
                                 pstmt2 = con.prepareStatement("SELECT activity_name FROM activities WHERE activity_id = ?");
                                 pstmt2.clearParameters();
                                 pstmt2.setInt(1, sheet_activity_id);

                                 rs2 = pstmt2.executeQuery();

                                 if (rs2.next()) {
                                     activity_name = rs2.getString("activity_name");
                                 }

                                 if (!activity_name.equals("")) {
                                     out.println(activity_name);
                                 } else {
                                     out.println("Unknown");
                                 }

                                 pstmt2.close();

                            } catch (Exception ignore) { }
                         }
                      }
                      out.println("</font></td>");
                  }

                  out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                  out.println("<font size=\"2\">");
                  if (skip == 1) {
                     out.println("&nbsp;");
                  } else {
                     if (!lgname.equals( "" )) {          // if group lesson time
                        out.println(lgnum+ "/" +lgmax);   // display current & max # of mems
                     } else {
                        if (!ltype.equals( "" )) {
                           out.println(num);            // do not display if no member or activity
                        } else {
                           out.println("&nbsp;");
                        }
                     }
                  }
                  out.println("</font></td>");
                   
                  if (club.equals("westchester") && printb.equals("print")) {
                       out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                       out.println("<font size=\"2\">");
                       if (skip == 1) {
                           out.println("&nbsp;");
                       } else {
                           if (!mNum.equals("") && mNum != null) {
                               out.println(mNum);
                           } else {
                               out.println("&nbsp;");
                           }
                       }
                       out.println("</font></td>");
                   }
                  out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                  out.println("<font size=\"2\">");
                  if (skip == 1) {
                     out.println("&nbsp;");
                  } else {
                     if (!phone1.equals( "" ) && phone1 != null) {
                        out.println(phone1);
                        if (!phone2.equals( "" ) && phone2 != null) {
                           out.println("<br>" +phone2);
                        }
                     } else {
                        out.println("&nbsp;");
                     }
                  }
                  out.println("</font></td>");
                  
                   if (club.equalsIgnoreCase("interlachen")) {

                       out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                       out.println("<font size=\"2\">");
                       if (skip == 1) {
                           out.println("&nbsp;");
                       } else {
                           if (!email1.equals("")) {          // if group lesson time
                               out.println(email1);   // display current & max # of mems
                           } else {
                               out.println("&nbsp;");
                           }
                       }
                       out.println("</font></td>");
                   }

                  if (skip == 1) {
                     out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                     out.println("<font size=\"2\">");
                     out.println("&nbsp;");
                     out.println("</font></td>");
                  } else {
                     if (!memname.equals( "" ) && !ltype.equals( "" )) {
                        out.println("<form method=\"post\" action=\"Proshop_lesson\">");
                        out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                        out.println("<font size=\"1\">");
                        out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
                        out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
                        out.println("<input type=\"hidden\" name=\"proid\" value=\"" + id + "\">");
                        out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
                        out.println("<input type=\"hidden\" name=\"jump\" value=\"" +j+ "\">");
                        if (billed == 0) {
                           out.println("<input type=\"hidden\" name=\"billed\" value=\"yes\">");
                           out.println("<input type=\"image\" src=\"/" +rev+ "/images/mtbox.gif\" border=\"1\" name=\"sub\" value=\"billed\" title=\"Click here to set member as billed.\" alt=\"submit\">");
                        } else {
                           out.println("<input type=\"hidden\" name=\"billed\" value=\"no\">");
                           out.println("<input type=\"image\" src=\"/" +rev+ "/images/xbox.gif\" border=\"1\" name=\"sub\" value=\"not billed\" title=\"Click here to set member as not billed.\" alt=\"submit\">");
                        }
                        out.println("</font></td>");
                        out.println("</form>");

                     } else {
                        out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                        out.println("<font size=\"2\">");
                        out.println("&nbsp;");            // do not display anything if no member or activity
                        out.println("</font></td>");
                     }
                  }

                  //
                  //   column for 'Notes' box
                  //
                  out.println("<form method=\"post\" action=\"Proshop_lesson\" target=\"_blank\">");
                  out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                  out.println("<font size=\"2\">");
                  if (skip == 1) {
                     out.println("&nbsp;");
                  } else {
                     if (!notes.equals("")) {
                        
                         if (printb.equals("print")) {
                             out.println(notes);
                         } else {
                             out.println("<input type=\"hidden\" name=\"notes\" value=\"yes\">");
                             out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
                             out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
                             out.println("<input type=\"hidden\" name=\"proid\" value=\"" + id + "\">");
                             out.println("<input type=\"image\" src=\"/" + rev + "/images/notes.jpg\" border=\"0\" name=\"showNotes\" title=\"Click here to view notes.\">");
                         }
                     } else {
                        out.println("&nbsp;");
                     }
                  }
                  out.println("</font></td></form>");

                  //
                  //   column for 'History' box
                  //
                  out.println("<form method=\"post\" action=\"Proshop_lesson\" target=\"_blank\">");
                  out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                  out.println("<font size=\"2\">");

                  if (skip == 1 || !lgname.equals("")) {    // do not display history button for lesson group times
                     out.println("&nbsp;");
                  } else {

                      out.println("<input type=\"hidden\" name=\"hist\" value=\"yes\">");
                      out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
                      out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
                      out.println("<input type=\"hidden\" name=\"proid\" value=\"" + id + "\">");
                      out.println("<input type=\"hidden\" name=\"ltimename\" value=\"" + ltimename + "\">");
                      out.println("<input type=\"image\" src=\"/" +rev+ "/images/history.gif\" border=\"0\" title=\"Click here to view lesson history.\">");
                  }
                  out.println("</font></td></form>");
                    
               }      // end of IF block=
                 
               //
               //  Last column for 'Block' button
               //
               if (configAccess && updateAccess) {      // only allow if proshop user has appropriate access
                   out.println("<form method=\"post\" action=\"Proshop_lesson\">");
                   out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                   out.println("<font size=\"2\">");
                   if (skip == 1 || !memname.equals( "" ) || !lgname.equals( "" )) {
                      out.println("&nbsp;");
                   } else {
                      out.println("<input type=\"hidden\" name=\"blocktime\" value=\"" +block+ "\">");
                      out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
                      out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
                      out.println("<input type=\"hidden\" name=\"proid\" value=\"" + id + "\">");
                      out.println("<input type=\"hidden\" name=\"jump\" value=\"" +j+ "\">");
                      out.println("<input type=\"image\" src=\"/" +rev+ "/images/block.jpg\" border=\"0\" name=\"blockTime\" title=\"Click here to block this time from members.\">");
                   }
                   out.println("</font></td></form>");
               }
            }       // end of IF blocker

            out.println("</tr>");         // end of the row
         }               // end of IF skip

      }                  // end of WHILE times
      pstmt.close();
   }
   catch (Exception exc) {

      dbError(req, out, exc, lottery);
      return;
   }

   //
   //  End of Lesson Book table
   //
   out.println("</table>");                         // end of tee sheet table
   out.println("</td></tr>");
   out.println("</table><br>");                            // end of main page table

   //
   //  End of HTML page
   //
   out.println("</center></body></html>");
   out.close();

 }   // end of doPost   


 // ********************************************************************
 //  Process the 'Prompt Pro Id' request
 // ********************************************************************

 private String promptProId(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session, Connection con)
         throws ServletException, IOException {


   PreparedStatement pstmt = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String user = (String)session.getAttribute("user");               // get name of user
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String proid = "";
   String proName = "";
   String lname = "";
   String fname = "";
   String mi = "";
   String suffix = "";
   String temp = "";
   int id = 0;
   int count = 0;
   boolean found = false;

   //
   //  Get the existing pro ids - if only one, then return it, else prompt for a selection
   //
   try {

      pstmt = con.prepareStatement (
              "SELECT id FROM lessonpro5 WHERE activity_id = ? ORDER BY sort_by");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1, sess_activity_id);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      while (rs.next()) {

         id = rs.getInt("id");          // get the proid
         count++;                       // count how many exist
         found = true;                  // found some
      }
      pstmt.close();
   }
   catch (Exception exc) {
      
      found = false;      // not found
   }

   //
   //  Process according to the number of ids found
   //
   if (found == false) {        // if none yet
      
      // Pros do not exist yet - inform user to add some

      out.println(SystemUtils.HeadTitle("Sequence Error"));
      out.println("<BODY>");
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      out.println("<CENTER>");
      out.println("<BR><BR><H3>Setup Sequence Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to process your request at this time.");
      out.println("<BR>There are no Lesson Pros in the system yet.");
      out.println("<BR>You must add one or more before you can continue.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"Proshop_lesson?addPro=yes\">Add a Lesson Pro</a>");
      out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
  
      proid = "";
      return(proid);            // return none
   }

   if (count == 1 && id != 0) {        // if only one pro
     
      proid = String.valueOf( id );    // convert proid to string 
      return(proid);                   // return it so we can continue using this proid
   }
     

   //
   //  More than one lesson pro found - prompt user to select one
   //
   //  Build the html page to request the lesson info
   //
   out.println(SystemUtils.HeadTitle("Proshop - Select Pro"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);            // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<center>");

   out.println("<br><br><table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("<b>Lesson Pro Selection</b><br>");

      if (sess_activity_id != 0) {
          out.println("<br>Select a professional from the list below.<br>");
      } else {
          out.println("<br>Select a golf professional from the list below.<br>");
      }

   out.println("</font></td></tr></table><br><br><br>");
   out.println("<font size=\"2\">");

      out.println("<form action=\"Proshop_lesson\" method=\"post\" name=\"pform\">");
        
      if (req.getParameter("addltype") != null) {      // add lesson type?
        
         temp = req.getParameter("addltype");       
         out.println("<input type=\"hidden\" name=\"addltype\" value=\"" +temp+ "\">");
      }

      if (req.getParameter("addlgrp") != null) {      // add Group Lesson?

         temp = req.getParameter("addlgrp");
         out.println("<input type=\"hidden\" name=\"addlgrp\" value=\"" +temp+ "\">");
      }

      if (req.getParameter("addltime") != null) {      // add lesson time?

         temp = req.getParameter("addltime");
         out.println("<input type=\"hidden\" name=\"addltime\" value=\"" +temp+ "\">");
      }

      if (req.getParameter("editltype") != null) {      // edit lesson type?

         temp = req.getParameter("editltype");
         out.println("<input type=\"hidden\" name=\"editltype\" value=\"" +temp+ "\">");
      }

      if (req.getParameter("editlgrp") != null) {      // edit Group Lesson?

         temp = req.getParameter("editlgrp");
         out.println("<input type=\"hidden\" name=\"editlgrp\" value=\"" +temp+ "\">");
      }

      if (req.getParameter("editltime") != null) {      // edit lesson time?

         temp = req.getParameter("editltime");
         out.println("<input type=\"hidden\" name=\"editltime\" value=\"" +temp+ "\">");
      }

      if (req.getParameter("editPro") != null) {      // edit a lesson pro?

         temp = req.getParameter("editPro");
         out.println("<input type=\"hidden\" name=\"editPro\" value=\"" +temp+ "\">");
      }
      
      if (req.getParameter("lessonReport") != null) {      

          temp = req.getParameter("lessonReport");
          out.println("<input type=\"hidden\" name=\"lessonReport\" value=\"" + temp + "\">");
      }
//      if (req.getParameter("XlessonReport") != null) {      
//
//          temp = req.getParameter("lessonReport");
//          out.println("<input type=\"hidden\" name=\"lessonReport\" value=\"" + temp + "\">");
//      }

      if (req.getParameter("bio") != null) {      // edit a lesson pro's bio?

         out.println("<input type=\"hidden\" name=\"bio\" value=\"yes\">");
      }

      if (req.getParameter("sets") != null) {

         out.println("<input type=\"hidden\" name=\"sets\" value=\"yes\">");
      }

      out.println("<b>Lesson Pro:</b>&nbsp;&nbsp;");
      out.println("<select size=\"1\" name=\"proid\" onChange=\"document.pform.submit()\">");
         out.println("<option value=\"0\">Select One</option>");

      try {
          
         if (club.equals("virginiacc")) {
             pstmt = con.prepareStatement (
                     "SELECT * FROM lessonpro5 WHERE activity_id = ? ORDER BY fname");
         } else {
             pstmt = con.prepareStatement (
                     "SELECT * FROM lessonpro5 WHERE activity_id = ? ORDER BY sort_by");
         }

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1, sess_activity_id);
         rs = pstmt.executeQuery();      // execute the prepared pstmt

         while (rs.next()) {

            StringBuffer pro_name = new StringBuffer(rs.getString("fname"));  // get first name

            mi = rs.getString("mi");                                          // middle initial
            if (!mi.equals( "" )) {
               pro_name.append(" ");
               pro_name.append(mi);
            }
            pro_name.append(" " + rs.getString("lname"));                   // last name

            suffix = rs.getString("suffix");                                // suffix
            if (!suffix.equals( "" )) {
               pro_name.append(" ");
               pro_name.append(suffix);
            }
              
            proName = pro_name.toString();                             // convert to one string

            id = rs.getInt("id");                   // get the proid


            if (club.equals("pgmunl")) {

                if ((user.equals("proshopjim") && id != 2) || (user.equals("proshopmike") && id != 3) ||
                    (user.equals("proshopgreg") && id != 4) || (user.equals("proshopchris") && id != 5)) {

                    continue;    // If user doesn't match specific pro_id, skip displaying it in the list.
                }
            }

            out.println("<option value=\"" +id+ "\">" +proName+ "</option>");
         }
         pstmt.close();
           
      }
      catch (Exception exc) {

         out.println("<BR><BR>Unable to access the Database.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR>" + exc.getMessage());
         out.println("<BR><BR>");
      }
      if (req.getParameter("lessonReport") != null) { 
      out.println("<option value=\"" +"999"+ "\">" +"-ALL-"+ "</option>");   //proid is 999 which means select all pros
      }
      out.println("</select>");
      out.println("</form>");

   out.println("<font size=\"2\"><br><br>");
   out.println("<form method=\"get\" action=\"Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</center>");
   out.println("</body>");
   out.println("</html>");
   out.close();
     
   proid = "";           // return none so we know user was prompted
     
   return(proid);    
 }


 // ********************************************************************
 //  Process the 'Add Lesson Pro' request
 // ********************************************************************

 private void addPro(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con) throws ServletException, IOException {

   //
   // Define parms - set defaults
   //
   PreparedStatement pstmt = null;
   ResultSet rs = null;

   String club = (String)session.getAttribute("club");               // get name of club
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   boolean error = false;

   int sess_activity_id = (Integer)session.getAttribute("activity_id");


   //
   //  Build the html page to request the lesson pro info
   //
   out.println(SystemUtils.HeadTitle2("Proshop - Add Lesson Pro"));
   out.println("<script type=\"text/javascript\">");
   out.println("<!--");
   out.println("function cursor() { document.forms['f'].fname.focus(); }");
   out.println("// -->");
   out.println("</script>");

   out.println("</head>");

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" onload=cursor()>");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<center>");

   out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\">");
   out.println("<tr><td align=\"center\">");
      out.println("<font color=\"#FFFFFF\" size=\"3\">");
      out.println("<b>Add Lesson Pro</b>");
      out.println("</font><font color=\"#FFFFFF\" size=\"2\">");
      out.println("<br><br>Complete the following information for each Lesson Pro to be added.<br>");
      out.println("The name and cancellation information will be displayed to the members when scheduling a lesson.");
      out.println("<br>The email addresses are optional and are used to inform the pro when lessons are made/changed.");
      out.println("<br><br>Click on 'ADD' to add the Lesson Pro.");
   out.println("</font></td></tr></table><br>");

   out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"6\">");
      out.println("<form action=\"Proshop_lesson\" method=\"post\" target=\"bot\" name=\"f\">");
      out.println("<input type=\"hidden\" name=\"addPro2\" value=\"yes\">");
      out.println("<tr>");
         out.println("<td>");
         out.println("<font size=\"2\">");
         out.println("<p align=\"left\">&nbsp;&nbsp;First Name:&nbsp;");
            out.println("<input type=\"text\" name=\"fname\" size=\"15\" maxlength=\"20\">");
            out.println("&nbsp;&nbsp;MI:&nbsp;");
            out.println("<input type=\"text\" name=\"mi\" size=\"1\" maxlength=\"1\">");
            out.println("&nbsp;&nbsp;Last Name:&nbsp;");
            out.println("<input type=\"text\" name=\"lname\" size=\"15\" maxlength=\"20\">");
            out.println("&nbsp;&nbsp;Suffix (Jr, Sr, etc.):&nbsp;");
            out.println("<input type=\"text\" name=\"suffix\" size=\"3\" maxlength=\"4\">&nbsp;&nbsp;&nbsp;");
         out.println("</p>");

         // If more than one activity configured, print activity selection box, otherwise pass current activity_id as a hidden input
         if (getActivity.getActivityCount(con) > 1) {
             out.println("<p align=\"left\">&nbsp;&nbsp;Activity:&nbsp;&nbsp;");
                error = getActivity.buildActivitySelect(1, sess_activity_id, "", false, con, out);
             out.println("</p>");
                      
             out.println("<p align=\"left\">");
             out.println("&nbsp;&nbsp;How would you prefer to assign locations for lessons for this pro? (FlxRez only)");
             out.println("&nbsp;&nbsp;<select name=\"auto_select_location\" size=\"1\">");
             out.println(" <option value=\"1\" selected>Automatically</option>");
             out.println(" <option value=\"0\">Manually</option>");
             out.println("</select>");
             out.println("<br>&nbsp;&nbsp;(Note:  When a member books a lesson, or a recurring lesson is booked, a location will <span style=\"font-weight:bold;\">always</span> be assigned automatically)");
             out.println("</p>");
         } else {
             out.println("<input type=\"hidden\" name=\"activity_id\" value=\"" + sess_activity_id + "\">");
         }

         out.println("<p align=\"left\">&nbsp;&nbsp;Email Address #1:&nbsp;&nbsp;");
            out.println("<input type=\"text\" name=\"email1\" size=\"20\" maxlength=\"50\">");
            out.println("&nbsp;&nbsp;Receive <a href=\"/" +rev+ "/member_help_icalendar.htm\" target=\"_blank\">iCal attachments</a> at this address?&nbsp;&nbsp;");
            out.println("<select name=\"iCal1\" size=\"1\">");
            out.println("  <option value=\"1\">Yes</option>");
            out.println("  <option value=\"0\" selected>No</option>");
            out.println("</select>");
         out.println("</p>");

         out.println("<p align=\"left\">&nbsp;&nbsp;Email Address #2:&nbsp;&nbsp;");
            out.println("<input type=\"text\" name=\"email2\" size=\"20\" maxlength=\"50\">");
            out.println("&nbsp;&nbsp;Receive <a href=\"/" +rev+ "/member_help_icalendar.htm\" target=\"_blank\">iCal attachments</a> at this address?&nbsp;&nbsp;");
            out.println("<select name=\"iCal2\" size=\"1\">");
            out.println("  <option value=\"1\">Yes</option>");
            out.println("  <option value=\"0\" selected>No</option>");
            out.println("</select>");
         out.println("</p>");

         out.println("<p align=\"left\">&nbsp;&nbsp;Advance Signup Limit (# of hrs prior notice required to signup):&nbsp;&nbsp;");
            out.println("<input type=\"text\" name=\"advlimit\" size=\"2\" maxlength=\"3\">");
         out.println("</p>");

         out.println("<p align=\"left\">&nbsp;&nbsp;Cancellation Limit (# of hrs prior notice required):&nbsp;&nbsp;");
            out.println("<input type=\"text\" name=\"canlimit\" size=\"2\" maxlength=\"3\">");
         out.println("</p>");

         out.println("<p align=\"left\">&nbsp;&nbsp;Cancellation Policy (up to 254 characters):&nbsp;&nbsp;");
         out.println("<textarea name=\"canpolicy\" cols=\"40\" rows=\"3\">");
         out.println("</textarea>");
         out.println("</p>");

         out.println("<p align=\"center\">");
           out.println("<input type=\"submit\" value=\"ADD\">");
           out.println("");
         out.println("</p>");
         out.println("</font>");
         out.println("</td>");
      out.println("</tr>");
      out.println("</form>");
   out.println("</table>");
   out.println("</font>");

   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</center>");
   out.println("</body>");
   out.println("</html>");
   out.close();
 }


 // ********************************************************************
 //  Process the 'Add Lesson Pro' SUBMIT request
 //  and the 'Edit Lesson Pro' SUBMIT request
 // ********************************************************************

 private void addPro2(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con) throws ServletException, IOException {

   PreparedStatement pstmt = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String lname = "";
   String fname = "";
   String mi = "";
   String suffix = "";
   String email1 = "";
   String email2 = "";
   String advlimits = "";
   String canlimits = "";
   String canpolicy = "";
   String proid = "";

   int advlimit = 0;
   int canlimit = 0;
   int id = 0;
   int active = 1;
   int activity_id = 0;
   int iCal1 = 0;
   int iCal2 = 0;
   int auto_select_location = 0;

   Member member = new Member();             // use the member services to verify the email addresses

   if (req.getParameter("editPro2") != null) {      // edit a pro (submit)?

      proid = req.getParameter("proid");            // yes, get the pro id to process
        
      try {
         id = Integer.parseInt(proid);
      }
      catch (NumberFormatException e) {
         id = 0;
      }
   }

   //
   //  Get the parameters
   //
   if (req.getParameter("lname") != null) lname = req.getParameter("lname");  
   if (req.getParameter("fname") != null) fname = req.getParameter("fname");
   if (req.getParameter("mi") != null) mi = req.getParameter("mi");
   if (req.getParameter("suffix") != null) suffix = req.getParameter("suffix");
   if (req.getParameter("email1") != null) email1 = req.getParameter("email1");
   if (req.getParameter("email2") != null) email2 = req.getParameter("email2");
   if (req.getParameter("iCal1") != null) iCal1 = Integer.parseInt(req.getParameter("iCal1"));
   if (req.getParameter("iCal2") != null) iCal2 = Integer.parseInt(req.getParameter("iCal2"));
   if (req.getParameter("activity_id") != null) activity_id = Integer.parseInt(req.getParameter("activity_id"));

   if (req.getParameter("advlimit") != null) {

      advlimits = req.getParameter("advlimit");

      try {
         advlimit = Integer.parseInt(advlimits);
      }
      catch (NumberFormatException e) {
      }
   }
   if (req.getParameter("canlimit") != null) {

      canlimits = req.getParameter("canlimit");

      try {
         canlimit = Integer.parseInt(canlimits);
      }
      catch (NumberFormatException e) {
      }
   }
   if (req.getParameter("canpolicy") != null) {

      canpolicy = req.getParameter("canpolicy");
   }
   
   if (req.getParameter("auto_select_location") != null) {
       try {
           auto_select_location = Integer.parseInt(req.getParameter("auto_select_location"));
       } catch (Exception exc) {
           auto_select_location = 1;
       }
   }

   //
   //  Make sure user specified the minimum parameters
   //
   if (fname.equals( "" ) || fname == null || lname.equals( "" ) || lname == null) {
     
      out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
      out.println("<BODY>");
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      out.println("<CENTER><BR>");
      out.println("<br><br>");
      out.println("<H3>Input Error</H3>");
      out.println("<BR>Sorry, you must specify at least a first and last name.<BR>");
      out.println("<BR>Please specify both names and try again.<BR>");
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
   //  Verify the email addresses
   //
   if (!email1.equals( "" ) && email1 != null) {      // if specified

      FeedBack feedback = (member.isEmailValid(email1));

      if (!feedback.isPositive()) {    // if error

         String emailError = feedback.get(0);             // get error message

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
         out.println("<BR><BR><H2>Data Entry Error</H2>");
         out.println("<BR><BR>The email address you entered is incorrect.");
         out.println("<BR>" +emailError);
         out.println("<BR><BR>Please try again.");
         out.println("<BR>If problem persists, please contact your club manager.");
         out.println("<BR><BR>");
         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
         out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }
   }
   if (!email2.equals( "" ) && email2 != null) {      // if specified

      FeedBack feedback = (member.isEmailValid(email2));

      if (!feedback.isPositive()) {    // if error

         String emailError = feedback.get(0);             // get error message

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
         out.println("<BR><BR><H2>Data Entry Error</H2>");
         out.println("<BR><BR>The email address you entered is incorrect.");
         out.println("<BR>" +emailError);
         out.println("<BR><BR>Please try again.");
         out.println("<BR>If problem persists, please contact your club manager.");
         out.println("<BR><BR>");
         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
         out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }
   }

   if (req.getParameter("editPro2") != null) {      // edit a pro (submit)?

      //
      //  Edit request - update the record
      //
      try {

         //
         //  Udate the record in the pro table
         //
         pstmt = con.prepareStatement (
           "UPDATE lessonpro5 SET lname = ?, fname = ?, mi = ?, suffix = ?, activity_id = ?, " +
           "active = ?, email1 = ?, email2 = ?, " +
           "advlimit = ?, canlimit = ?, canpolicy = ?, iCal1 = ?, iCal2 = ?, auto_select_location = ? " +
           "WHERE id = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setString(1, lname);       // put the parm in pstmt
         pstmt.setString(2, fname);
         pstmt.setString(3, mi);
         pstmt.setString(4, suffix);
         pstmt.setInt(5, activity_id);
         pstmt.setInt(6, active);
         pstmt.setString(7, email1);
         pstmt.setString(8, email2);
         pstmt.setInt(9, advlimit);
         pstmt.setInt(10, canlimit);
         pstmt.setString(11, canpolicy);
         pstmt.setInt(12, iCal1);
         pstmt.setInt(13, iCal2);
         pstmt.setInt(14, auto_select_location);
           
         pstmt.setInt(15, id);

         pstmt.executeUpdate();     // execute the prepared stmt

         pstmt.close();

      }
      catch (Exception exc) {

         dbError(req, out, exc, lottery);
         return;
      }

      //
      // Database updated - inform user
      //
      out.println(SystemUtils.HeadTitle("Proshop Edit Lesson Pro"));
      out.println("<BODY>");
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      out.println("<CENTER><BR>");
      out.println("<BR><BR><H3>The Lesson Pro Has Been Updated</H3>");
      out.println("<BR><BR>Thank you, the lesson pro has been updated in the system database.");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<a href=\"Proshop_lesson?editPro=yes\">Edit Another Lesson Pro</a>");
      out.println("<BR><BR><form method=\"get\" action=\"Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();

   } else {    // add request

      //
      //  Get the highest current proid so we can set a new one for this pro
      //
      try {

         pstmt = con.prepareStatement (
                 "SELECT MAX(id) FROM lessonpro5");

         pstmt.clearParameters();        // clear the parms
         rs = pstmt.executeQuery();      // execute the prepared pstmt

         if (rs.next()) {

            id = rs.getInt(1);          // get the highest current proid

            id++;                       // increment to get new value

         } else {

            id = 1;                     // start with '1'
         }
         pstmt.close();
      }
      catch (Exception exc) {
         id = 1;                        // not found
      }

      //
      //  Make sure the pro doesn't already exist
      //
      try {

         pstmt = con.prepareStatement (
                 "SELECT id FROM lessonpro5 WHERE lname = ? AND fname = ? AND mi = ? AND suffix = ? AND activity_id = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setString(1, lname);       // put the parm in pstmt
         pstmt.setString(2, fname);
         pstmt.setString(3, mi);
         pstmt.setString(4, suffix);
         pstmt.setInt(5, activity_id);
         rs = pstmt.executeQuery();      // execute the prepared pstmt

         if (rs.next()) {

            dupMem(req, out, lottery);
            pstmt.close();
            return;
         }
         pstmt.close();
      }
      catch (Exception exc) {
      }

      //
      //  Add the pro
      //
      try {
         pstmt = con.prepareStatement (
           "INSERT INTO lessonpro5 (lname, fname, mi, suffix, activity_id, id, active, email1, email2, " +
           "advlimit, canlimit, canpolicy, iCal1, iCal2, auto_select_location) " +
           "VALUES (?,?,?,?,?,?,?,?,?,?," +
                   "?,?,?,?,?)");

         pstmt.clearParameters();        // clear the parms
         pstmt.setString(1, lname);       // put the parm in pstmt
         pstmt.setString(2, fname);
         pstmt.setString(3, mi);
         pstmt.setString(4, suffix);
         pstmt.setInt(5, activity_id);
         pstmt.setInt(6, id);
         pstmt.setInt(7, active);
         pstmt.setString(8, email1);
         pstmt.setString(9, email2);
         pstmt.setInt(10, advlimit);
         pstmt.setInt(11, canlimit);
         pstmt.setString(12, canpolicy);
         pstmt.setInt(13, iCal1);
         pstmt.setInt(14, iCal2);
         pstmt.setInt(15, auto_select_location);
         pstmt.executeUpdate();          // execute the prepared stmt

         pstmt.close();   // close the stmt

      }
      catch (Exception exc) {

         dbError(req, out, exc, lottery);
         return;
      }

      //
      // Database updated - inform user
      //
      out.println(SystemUtils.HeadTitle("Proshop Add Lesson Pro"));
      out.println("<BODY>");
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      out.println("<CENTER><BR>");
      out.println("<BR><BR><H3>The Lesson Pro Has Been Added</H3>");
      out.println("<BR><BR>Thank you, the lesson pro has been added to the system database.");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<a href=\"Proshop_lesson?addPro=yes\">Add Another Lesson Pro</a>");
      out.println("<BR><BR><form method=\"get\" action=\"Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
   }
 }
   
  
 // ********************************************************************
 //  Process the 'Edit Lesson Pro' request
 // ********************************************************************

 private void editPro(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException {

   PreparedStatement pstmt = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String lname = "";
   String fname = "";
   String mi = "";
   String suffix = "";
   String email1 = "";
   String email2 = "";
   String canpolicy = "";

   int advlimit = 0;
   int canlimit = 0;
   int active = 1;          // future
   int id = 0;
   int activity_id = 0;
   int iCal1 = 0;
   int iCal2 = 0;
   int auto_select_location = 0;

   boolean error = false;

   try {
      id = Integer.parseInt(proid);
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   //
   //  Get the pro's record
   //
   try {

      pstmt = con.prepareStatement (
              "SELECT * FROM lessonpro5 WHERE id = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1,id);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      if (rs.next()) {

         lname = rs.getString("lname");             // last name 
         fname = rs.getString("fname");             // first name
         mi = rs.getString("mi");                   // middle initial
         suffix = rs.getString("suffix");           // suffix
         activity_id = rs.getInt("activity_id");    // activity id
         email1 = rs.getString("email1");           // email 1
         email2 = rs.getString("email2");           // email 2
         advlimit = rs.getInt("advlimit");          // advance signup limit (hours)
         canlimit = rs.getInt("canlimit");          // cancellation limit (hours)
         canpolicy = rs.getString("canpolicy");     // cancellation policy
         iCal1 = rs.getInt("iCal1");                 // email 1 iCal option
         iCal2 = rs.getInt("iCal2");                 // email 2 iCal option
         auto_select_location = rs.getInt("auto_select_location");
           
      } else {

         nfError(req, out, lottery);
         return;
      }
      pstmt.close();

   }
   catch (Exception exc) {

      dbError(req, out, exc, lottery);
      return;
   }

   //
   //  Build the html page to request the lesson pro info
   //
   out.println(SystemUtils.HeadTitle2("Proshop - Edit Lesson Pro"));
     
   //
   //*******************************************************************
   //  Move cancellation policy into text area
   //*******************************************************************
   //
   out.println("<script language='JavaScript'>");             // Move policy into textarea
   out.println("<!--");
   out.println("function movepolicy() {");
   out.println("var oldpolicy = document.proform.oldpolicy.value;");
   out.println("document.proform.policy.value = oldpolicy;");   // put policy in text area
   out.println("}");                  // end of script function
   out.println("// -->");
   out.println("</script>");          // End of script
  
   out.println("</HEAD>");
   out.println("<body onLoad=\"movepolicy()\" bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<center>");

   out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\">");
   out.println("<tr><td align=\"center\">");
      out.println("<font color=\"#FFFFFF\" size=\"3\">");
      out.println("<b>Edit Lesson Pro</b>");
      out.println("</font><font color=\"#FFFFFF\" size=\"2\">");
      out.println("<br><br>Change the desired information for the Lesson Pro below.<br>");
      out.println("<br>Click on <b>Update</b> to submit the changes.");
      out.println("<br>Click on <b>Remove</b> to delete the Lesson Pro.");
   out.println("</font></td></tr></table><br>");

   out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"6\">");
      out.println("<form action=\"Proshop_lesson\" method=\"post\" name=\"proform\" target=\"bot\">");
      out.println("<input type=\"hidden\" name=\"proid\" value=\"" +id+ "\">");
      out.println("<tr>");
         out.println("<td>");
         out.println("<font size=\"2\">");
         out.println("<p align=\"left\">&nbsp;&nbsp;First Name:&nbsp;");
            out.println("<input type=\"text\" name=\"fname\" value=\"" +fname+ "\" size=\"15\" maxlength=\"20\">");
            out.println("&nbsp;&nbsp;MI:&nbsp;");
            out.println("<input type=\"text\" name=\"mi\" value=\"" +mi+ "\" size=\"1\" maxlength=\"1\">");
            out.println("&nbsp;&nbsp;Last Name:&nbsp;");
            out.println("<input type=\"text\" name=\"lname\" value=\"" +lname+ "\" size=\"15\" maxlength=\"20\">");
            out.println("&nbsp;&nbsp;Suffix (Jr, Sr, etc.):&nbsp;");
            out.println("<input type=\"text\" name=\"suffix\" value=\"" +suffix+ "\" size=\"3\" maxlength=\"4\">&nbsp;&nbsp;&nbsp;");
         out.println("</p>");

         // If more than one activity configured, print activity selection box, otherwise pass current activity_id as a hidden input
         if (getActivity.getActivityCount(con) > 1) {
             out.println("<p align=\"left\">&nbsp;&nbsp;Activity:&nbsp;&nbsp;");
                error = getActivity.buildActivitySelect(1, activity_id, "", false, con, out);
             out.println("</p>");
                      
             out.println("<p align=\"left\">");
             out.println("&nbsp;&nbsp;How would you prefer to assign locations for lessons for this pro? (FlxRez only)");
             out.println("&nbsp;&nbsp;<select name=\"auto_select_location\" size=\"1\">");
             out.println(" <option value=\"1\"" + (auto_select_location == 1 ? " selected" : "") + ">Automatically</option>");
             out.println(" <option value=\"0\"" + (auto_select_location == 0 ? " selected" : "") + ">Manually</option>");
             out.println("</select>");
             out.println("<br>&nbsp;&nbsp;(Note:  When a member books a lesson, or a recurring lesson is booked, a location will <span style=\"font-weight:bold;\">always</span> be assigned automatically)");
             out.println("</p>");
         } else {
             out.println("<input type=\"hidden\" name=\"activity_id\" value=\"" + sess_activity_id + "\">");
         }

         out.println("<p align=\"left\">&nbsp;&nbsp;Email Address #1:&nbsp;&nbsp;");
            out.println("<input type=\"text\" name=\"email1\" value=\"" +email1+ "\" size=\"20\" maxlength=\"50\">");
            out.println("&nbsp;&nbsp;Receive <a href=\"/" +rev+ "/member_help_icalendar.htm\" target=\"_blank\">iCal attachments</a> at this address?&nbsp;&nbsp;");
            out.println("<select name=\"iCal1\" size=\"1\">");
            out.println("  <option value=\"1\"" + (iCal1 == 1 ? " selected" : "") + ">Yes</option>");
            out.println("  <option value=\"0\"" + (iCal1 == 0 ? " selected" : "") + ">No</option>");
            out.println("</select>");
         out.println("</p>");

         out.println("<p align=\"left\">&nbsp;&nbsp;Email Address #2:&nbsp;&nbsp;");
            out.println("<input type=\"text\" name=\"email2\" value=\"" +email2+ "\" size=\"20\" maxlength=\"50\">");
            out.println("&nbsp;&nbsp;Receive <a href=\"/" +rev+ "/member_help_icalendar.htm\" target=\"_blank\">iCal attachments</a> at this address?&nbsp;&nbsp;");
            out.println("<select name=\"iCal2\" size=\"1\">");
            out.println("  <option value=\"1\"" + (iCal2 == 1 ? " selected" : "") + ">Yes</option>");
            out.println("  <option value=\"0\"" + (iCal2 == 0 ? " selected" : "") + ">No</option>");
            out.println("</select>");
         out.println("</p>");

         out.println("<p align=\"left\">&nbsp;&nbsp;Advance Signup Limit (# of hrs prior notice required to signup):&nbsp;&nbsp;");
            out.println("<input type=\"text\" name=\"advlimit\" value=\"" +advlimit+ "\" size=\"2\" maxlength=\"3\">");
         out.println("</p>");

         out.println("<p align=\"left\">&nbsp;&nbsp;Cancellation Limit (# of hrs prior notice required):&nbsp;&nbsp;");
            out.println("<input type=\"text\" name=\"canlimit\" value=\"" +canlimit+ "\" size=\"2\" maxlength=\"3\">");
         out.println("</p>");

         //
         //   Script will put any existing policy info in the textarea (value= doesn't work)
         //
         out.println("<input type=\"hidden\" name=\"oldpolicy\" value=\"" +canpolicy+ "\">"); // hold policy for script

         out.println("<p align=\"left\">&nbsp;&nbsp;Cancellation Policy (up to 254 characters):&nbsp;&nbsp;");
         out.println("<textarea name=\"canpolicy\" value=\"\" id=\"policy\" cols=\"40\" rows=\"3\">");
         out.println("</textarea>");
         out.println("</p>");

         out.println("<p align=\"center\">");
           out.println("<input type=\"submit\" name=\"editPro2\" value=\"Update\">");    // to update record
           out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
           out.println("<input type=\"submit\" name=\"deletePro\" value=\"Remove\">");   // to delete the record
         out.println("</p>");
         out.println("</font>");
         out.println("</td>");
      out.println("</tr>");
      out.println("</form>");
   out.println("</table>");
   out.println("</font>");

   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</center>");
   out.println("</body>");
   out.println("</html>");
   out.close();
 }


 // ********************************************************************
 //  Process the 'Delete Lesson Pro' SUBMIT request
 // ********************************************************************

 private void deletePro(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException {

   PreparedStatement pstmt = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   String name = "";
   String fname = "";
   String lname = "";
   String mi = "";
   String suffix = "";

   int id = 0;

   try {
      id = Integer.parseInt(proid);
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   //
   //  Determine the lesson pro's full name
   //
   try {

      pstmt = con.prepareStatement (
              "SELECT lname, fname, mi, suffix FROM lessonpro5 WHERE id = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1,id);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      if (rs.next()) {

         StringBuffer pro_name = new StringBuffer(rs.getString("fname"));  // get first name

         mi = rs.getString("mi");                                          // middle initial
         if (!mi.equals( "" )) {
            pro_name.append(" ");
            pro_name.append(mi);
         }
         pro_name.append(" " + rs.getString("lname"));                   // last name

         suffix = rs.getString("suffix");                                // suffix
         if (!suffix.equals( "" )) {
            pro_name.append(" ");
            pro_name.append(suffix);
         }

         name = pro_name.toString();                             // convert to one string
      }
      pstmt.close();

   }
   catch (Exception exc) {

      dbError(req, out, exc, lottery);
      return;
   }

   //
   //  Check if this is a confirmation to delete
   //
   if (req.getParameter("conf") != null) {

      //
      //  Delete the rest
      //
      try {
         pstmt = con.prepareStatement (
                  "Delete FROM lessonpro5 WHERE id = ?");

         pstmt.clearParameters();               // clear the parms
         pstmt.setInt(1, id);
         pstmt.executeUpdate();         // execute the prepared pstmt

         pstmt.close();
           
         //
         //  Delete any lesson times for this pro
         //
         pstmt = con.prepareStatement (
                  "Delete FROM lessontime5 WHERE proid = ?");

         pstmt.clearParameters();               // clear the parms
         pstmt.setInt(1, id);
         pstmt.executeUpdate();         // execute the prepared pstmt

         pstmt.close();
           
         //
         //  Delete any lesson blocks for this pro
         //
         pstmt = con.prepareStatement (
                  "Delete FROM lessonblock5 WHERE proid = ?");

         pstmt.clearParameters();               // clear the parms
         pstmt.setInt(1, id);
         pstmt.executeUpdate();         // execute the prepared pstmt

         pstmt.close();

         //
         //  Delete any lesson types for this pro
         //
         pstmt = con.prepareStatement (
                  "Delete FROM lessontype5 WHERE proid = ?");

         pstmt.clearParameters();               // clear the parms
         pstmt.setInt(1, id);
         pstmt.executeUpdate();         // execute the prepared pstmt

         pstmt.close();

      }
      catch (Exception exc) {

         dbError(req, out, exc, lottery);
         return;
      }
        
      //
      //  Pro deleted - inform user
      //
      out.println(SystemUtils.HeadTitle("Delete Lesson Pro Confirmation"));
      out.println("<BODY>");
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      out.println("<CENTER><BR>");
      out.println("<p>&nbsp;</p>");
      out.println("<BR><H3>Lesson Pro Removed</H3><BR>");
      out.println("<BR><b>" + name + "</b> has been removed from the lesson pro database.<br>");
      out.println("<BR>The Lesson Times and Lesson Types associated with this pro have also been removed.");
      out.println("<BR>The Lesson Book associated with this pro has been archived.");

      out.println("<form method=\"get\" action=\"Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();

   } else {   

      //
      //  This is the first request for the delete - request a confirmation
      //
      out.println(SystemUtils.HeadTitle("Delete Lesson Pro Confirmation"));
      out.println("<BODY>");
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      out.println("<CENTER><BR>");
      out.println("<p>&nbsp;</p>");
      out.println("<BR><H3>Remove Lesson Pro Confirmation</H3><BR>");
      out.println("<BR>Please confirm that you wish to remove this Lesson Pro: <b>" + name + "</b><br>");

      out.println("<form action=\"Proshop_lesson\" method=\"post\" target=\"bot\">");
      out.println("<BR>ARE YOU SURE YOU WANT TO DELETE THIS LESSON PRO?");

      out.println("<input type=\"hidden\" name=\"proid\" value =\"" +proid+ "\">");
      out.println("<input type=\"hidden\" name=\"deletePro\" value =\"yes\">");
      out.println("<input type=\"hidden\" name=\"conf\" value =\"yes\">");
      out.println("<br><br><input type=\"submit\" value=\"Yes - Delete\">");
      out.println("</form><font size=\"2\">");

      out.println("<form method=\"get\" action=\"Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"No - Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();        
   }
 }


 // ********************************************************************
 //  Process the 'Add Lesson Type' request
 // ********************************************************************

 private void addLtype(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException {

   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String locations_csv = "";

   //
   //  Build the html page to request the lesson info
   //
   out.println(SystemUtils.HeadTitle2("Proshop - Add Lesson Type"));
   out.println("<script type=\"text/javascript\">");
   out.println("<!--");
   out.println("function cursor() { document.forms['f'].ltype_name.focus(); }");
   out.println("// -->");
   out.println("</script>");

   out.println("</head>");

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" onload=cursor()>");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<center>");

   out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\">");
   out.println("<tr><td align=\"center\">");
      out.println("<font color=\"#FFFFFF\" size=\"3\">");
      out.println("<b>Add Lesson Type</b><br>");
      out.println("</font><font color=\"#FFFFFF\" size=\"2\">");
      out.println("<br>Complete the following information for each Lesson Type to be added.<br>");
      out.println("This information will be displayed to the members when scheduling a lesson.");
      out.println("<br><br>Click on 'ADD' to add the Lesson Type.");
   out.println("</font></td></tr></table><br>");

   out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"6\">");
      out.println("<form action=\"Proshop_lesson\" method=\"post\" target=\"bot\" name=\"f\">");
      out.println("<input type=\"hidden\" name=\"proid\" value=\"" +proid+ "\">");
      out.println("<input type=\"hidden\" name=\"addltype2\" value=\"yes\">");
      out.println("<tr>");
         out.println("<td>");
         out.println("<font size=\"2\">");
         out.println("<p align=\"left\">&nbsp;&nbsp;Lesson Type Name:&nbsp;&nbsp;");
            out.println("<input type=\"text\" name=\"ltype_name\" size=\"30\" maxlength=\"40\">");
            out.println("&nbsp;&nbsp;&nbsp;* Must be unique");
         out.println("</p>");

         out.println("<p align=\"left\">&nbsp;&nbsp;Lesson Length (minutes):&nbsp;&nbsp;");
            out.println("<input type=\"text\" name=\"ltype_length\" size=\"3\" maxlength=\"3\">");
         out.println("</p>");

         out.println("<p align=\"left\">&nbsp;&nbsp;Lesson Price (include $):&nbsp;&nbsp;");
            out.println("<input type=\"text\" name=\"ltype_cost\" size=\"8\" maxlength=\"20\">");
         out.println("</p>");

         if (sess_activity_id != 0 && !getActivity.isChildlessActivity(sess_activity_id, con)) {
             
             if (club.equals("benttreecc") || club.equals("elmcrestcc") || club.equals("ccyork")
                     || (club.equals("gallerygolf") && sess_activity_id == 9)) {
                 out.println("<p align=\"left\">");
                 out.println("&nbsp;&nbsp;Should booking this lesson type automatically block time slots for one of the selected locations?");
                 out.println("&nbsp;&nbsp;<select name=\"autoblock_times\" size=\"1\">");
                 out.println(" <option value=\"1\" selected>Yes</option>");
                 out.println(" <option value=\"0\">No</option>");
                 out.println("</select>");
                 out.println("</p>");
             } else {
                 out.println("<input type=\"hidden\" name=\"autoblock_times\" value=\"1\">");
             }
             
             out.println("<p align=\"left\">&nbsp;&nbsp;Choose the locations this lesson type can take place at:&nbsp;&nbsp;");
             Common_Config.displayActivitySheetSelect(locations_csv, sess_activity_id, true, con, out);
             out.println("</p>");
         }

         out.println("<p align=\"left\">&nbsp;&nbsp;Lesson Description (up to 254 characters):&nbsp;&nbsp;");
         out.println("<textarea name=\"ltype_desc\" cols=\"40\" rows=\"3\">");
         out.println("</textarea>");
         out.println("</p>");

         out.println("<p align=\"center\">");
           out.println("<input type=\"submit\" value=\"ADD\">");
           out.println("");
         out.println("</p>");
         out.println("</font>");
         out.println("</td>");
      out.println("</tr>");
      out.println("</form>");
   out.println("</table>");
   out.println("</font>");

   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</center>");
   out.println("</body>");
   out.println("</html>");
   out.close();
 }


 // ********************************************************************
 //  Process the 'Add Lesson Type' SUBMIT request
 // ********************************************************************

 private void addLtype2(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException {

   PreparedStatement pstmt = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String ltname = "";
   String cost = "";
   String descript = "";
   String tlength = "";
   String locations_csv = "";

   int ilength = 0;
   int id = 0;
   int autoblock_times = 1;
   int auto_select_location = 1;

   try {
      id = Integer.parseInt(proid);          // convert the pro id
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   //
   //  Get the parameters
   //
   if (req.getParameter("ltype_name") != null) ltname = req.getParameter("ltype_name");
   if (req.getParameter("ltype_cost") != null) cost = req.getParameter("ltype_cost");
   if (req.getParameter("ltype_desc") != null) descript = req.getParameter("ltype_desc");
   if (req.getParameter("ltype_length") != null) {

      tlength = req.getParameter("ltype_length");

      try {
         ilength = Integer.parseInt(tlength);
      }
      catch (NumberFormatException e) {
         ilength = 0;
      }
   }
   
   if (req.getParameter("autoblock_times") != null) {
       try {
           autoblock_times = Integer.parseInt(req.getParameter("autoblock_times"));
       } catch (Exception exc) {
           autoblock_times = 1;
       }
   }
   
   //
   //  Make sure user specified the minimum parameters
   //
   if (ltname.equals( "" ) || ltname == null || ilength == 0) {

      out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
      out.println("<BODY>");
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      out.println("<CENTER><BR>");
      out.println("<br><br>");
      out.println("<H3>Input Error</H3>");
      out.println("<BR>Sorry, you must specify at least a name and length value.<BR>");
      out.println("<BR>Please specify both values and try again.<BR>");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   if (sess_activity_id != 0 && !getActivity.isChildlessActivity(sess_activity_id, con)) {
       //  Generate the locations string from the request
       locations_csv = Common_Config.buildLocationsString(req);
       
       if (locations_csv.equals("") && autoblock_times == 1) {

           out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
           out.println("<BODY>");
           SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
           out.println("<CENTER><BR>");
           out.println("<br><br>");
           out.println("<H3>Input Error</H3>");
           out.println("<BR>Sorry, you must specify at least one location where this lesson may take place.<BR>");
           out.println("<BR>Please specify locations and try again.<BR>");
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
   //  Make sure the lesson type doesn't already exist
   //
   try {

      pstmt = con.prepareStatement (
              "SELECT length FROM lessontype5 WHERE proid = ? AND activity_id = ? AND ltname = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1, id);
      pstmt.setInt(2, sess_activity_id);
      pstmt.setString(3, ltname);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      if (rs.next()) {

         dupMem(req, out, lottery);
         pstmt.close();
         return;
      }
      pstmt.close();
   }
   catch (Exception exc) {
   }

   //
   //  Add the lesson type
   //
   try {
      pstmt = con.prepareStatement (
        "INSERT INTO lessontype5 (proid, activity_id, ltname, length, cost, descript, locations, autoblock_times) " +
        "VALUES (?,?,?,?,?,?,?,?)");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1, id);
      pstmt.setInt(2, sess_activity_id);
      pstmt.setString(3, ltname);       // put the parm in pstmt
      pstmt.setInt(4, ilength);
      pstmt.setString(5, cost);
      pstmt.setString(6, descript);
      pstmt.setString(7, locations_csv);
      pstmt.setInt(8, autoblock_times);
      pstmt.executeUpdate();          // execute the prepared stmt

      pstmt.close();   // close the stmt

   }
   catch (Exception exc) {

      dbError(req, out, exc, lottery);
      return;
   }

   //
   // Database updated - inform user
   //
   out.println(SystemUtils.HeadTitle("Proshop Add Lesson Type"));
   out.println("<BODY>");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<CENTER><BR>");
   out.println("<BR><BR><H3>The Lesson Type Has Been Added</H3>");
   out.println("<BR><BR>Thank you, the lesson type has been added for the specified pro.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<a href=\"Proshop_lesson?addltype=yes&proid=" +id+ "\">Add Another Lesson Type For This Pro</a>");
   out.println("<BR><BR>");
   out.println("<a href=\"Proshop_lesson?addltype=yes\">Add Lesson Type For Other Pro</a>");
   out.println("<BR><BR>");
   out.println("<a href=\"Proshop_lesson?editltype=yes&proid=" +id+ "\">View Lesson Types For This Pro</a>");
   out.println("<BR><BR>");
   out.println("<form method=\"get\" action=\"Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 // ********************************************************************
 //  Process the 'Edit Lesson Type' request
 // ********************************************************************

 private void editLtype(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException {

   PreparedStatement pstmt = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String ltname = "";
   String cost = "";
   String descript = "";
   String locations_csv = "";
   String order_by = "";

   int ilength = 0;
   int count = 0;
   int id = 0;
   int autoblock_times = 0;

   boolean b = false;

   try {
      id = Integer.parseInt(proid);
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   if (req.getParameter("editLT") == null) {       // if not a specific edit request (if this is the 1st call)

      //
      //  Build the HTML page to display the existing Lesson Types
      //
      out.println(SystemUtils.HeadTitle("Proshop Lesson Types Page"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr><td align=\"center\">");

         out.println("<table cellpadding=\"5\" bgcolor=\"#336633\" border=\"0\" align=\"center\">");
         out.println("<tr><td align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"3\">");
         out.println("<b>Edit Lesson Types</b><br>");
         out.println("</font>");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<br>To update a Lesson Type, click on the Update button within that lesson type.");
         out.println("<br>To remove a Lesson Type, click on the Delete button within that lesson type.");
         out.println("<br></td></tr></table>");
         out.println("<br>");

         out.println("<table border=\"2\" cellpadding=\"5\" bgcolor=\"#F5F5DC\">");
         out.println("<tr bgcolor=\"#8B8970\">");
         out.println("<td colspan=\"5\" align=\"center\">");
         out.println("<font size=\"2\">");
         out.println("<p align=\"center\"><b>Active Lesson Types</b></p>");
         out.println("</font></td></tr>");
         out.println("<tr bgcolor=\"#8B8970\"><td align=\"center\">");
         out.println("<font size=\"2\"><p><b>Lesson Type Name</b></p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p><b>Length (minutes)</b></p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p><b>Price</b></p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p><b>Description</b></p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>&nbsp;</p>");      // empty for select buttons
         out.println("</font></td></tr>");

      //
      //  Get the lesson type records
      //
      try {
          
         if (club.equals("minikahda")) {
             order_by = " ORDER BY length, descript";
         } else if (club.equals("ccofnc")) {
             order_by = " ORDER BY length, ltname";
         } else {
             order_by = "";
         }

         pstmt = con.prepareStatement (
                 "SELECT * FROM lessontype5 WHERE proid = ? AND activity_id = ?" + order_by);

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1,id);
         pstmt.setInt(2,sess_activity_id);
         rs = pstmt.executeQuery();      // execute the prepared pstmt

         while (rs.next()) {

            b = true;                     // indicate lesson types exist

            ltname = rs.getString("ltname");
            ilength = rs.getInt("length");
            cost = rs.getString("cost");
            descript = rs.getString("descript");

            out.println("<tr>");
            out.println("<form action=\"Proshop_lesson\" method=\"post\" target=\"bot\" name=\"f\">");
            out.println("<input type=\"hidden\" name=\"proid\" value=\"" +proid+ "\">");
            out.println("<input type=\"hidden\" name=\"ltname\" value=\"" +ltname+ "\">");
               out.println("<td align=\"center\">");
                 out.println("<font size=\"2\">");
                 out.println(ltname);
                 out.println("</font>");
               out.println("</td>");

               out.println("<td align=\"center\">");
                 out.println("<font size=\"2\">");
                 out.println(ilength);
                 out.println("</font>");
               out.println("</td>");

               out.println("<td align=\"center\">");
                 out.println("<font size=\"2\">");
                 out.println(cost);
                 out.println("</font>");
               out.println("</td>");

               out.println("<td width=\"240\">");
                 out.println("<font size=\"2\">");
                 out.println(descript);
                 out.println("</textarea>");
                 out.println("</font>");
               out.println("</td>");

               out.println("<td align=\"center\">");
                 out.println("<font size=\"2\">");
                   out.println("<p align=\"center\">");
                   out.println("<input type=\"submit\" name=\"editLT\" value=\"Update\">");    // to update record
                   out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
                   out.println("<input type=\"submit\" name=\"deleteltype\" value=\"Delete\">");   // to delete the record
                   out.println("</p>");
                 out.println("</font>");
               out.println("</td>");

            out.println("</form>");
            out.println("</tr>");

         }
         pstmt.close();

         if (!b) {

            out.println("</font><font size=\"2\"><p>No Lesson Types Currently Exist</p>");
         }
      }
      catch (Exception exc) {

         dbError(req, out, exc, lottery);
         return;
      }

      //
      //  End of HTML page
      //
      out.println("</table></font>");                   // end of lesson type table
      out.println("</td></tr></table>");                // end of main page table
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</center></font></body></html>");
      out.close();

   } else {

      //******************************************************************
      //  Request is to edit a specific Lesson Type
      //******************************************************************
      //
      if (req.getParameter("ltname") != null) {

         ltname = req.getParameter("ltname");
      }
        
      //
      //  Get the lesson type records
      //
      try {

         pstmt = con.prepareStatement (
                 "SELECT * FROM lessontype5 WHERE proid = ? AND activity_id = ? AND ltname = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1,id);
         pstmt.setInt(2,sess_activity_id);
         pstmt.setString(3,ltname);
         rs = pstmt.executeQuery();      // execute the prepared pstmt

         if (rs.next()) {

            ilength = rs.getInt("length");
            cost = rs.getString("cost");
            descript = rs.getString("descript");
            locations_csv = rs.getString("locations");
            autoblock_times = rs.getInt("autoblock_times");

            //
            //  Build the HTML page to display the selected Lesson Type
            //
            out.println(SystemUtils.HeadTitle2("Proshop Lesson Types Page"));
            //
            //*******************************************************************
            //  Move cancellation descript into text area
            //*******************************************************************
            //
            out.println("<script type=\"text/javascript\">");             // Move descript into textarea
            out.println("<!--");
            out.println("function movedescript() {");
            out.println(" var olddescript = document.forms['f'].olddescript.value;");
            out.println(" document.forms['f'].descript.value = olddescript;");   // put descript in text area
            out.println("}");                  // end of script function
            out.println("// -->");
            out.println("</script>");          // End of script

            out.println("</head>");
            out.println("<body onload=\"movedescript()\" bgcolor=\"#FFFFFF\" text=\"#000000\">");
            SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
            out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

            out.println("<table border=\"0\" align=\"center\">");
            out.println("<tr><td align=\"center\">");

            out.println("<table cellpadding=\"5\" bgcolor=\"#336633\" border=\"0\" align=\"center\">");
            out.println("<tr><td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"3\">");
            out.println("<b>Edit Lesson Type</b><br>");
            out.println("</font>");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<br>To update a Lesson Type, make the desired changes and click on the Update button.");
            out.println("<br></td></tr></table>");
            out.println("<br><br>");

            out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"6\">");
               out.println("<form action=\"Proshop_lesson\" method=\"post\" target=\"bot\" name=\"f\">");
               out.println("<input type=\"hidden\" name=\"proid\" value=\"" +proid+ "\">");
               out.println("<input type=\"hidden\" name=\"oldname\" value=\"" +ltname+ "\">");
               out.println("<tr>");
                  out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"left\">&nbsp;&nbsp;Lesson Type Name:&nbsp;&nbsp;");
                     out.println("<input type=\"text\" name=\"ltype_name\" value=\"" +ltname+ "\" size=\"30\" maxlength=\"40\">");
                     out.println("&nbsp;&nbsp;&nbsp;* Must be unique");
                  out.println("</p>");

                  out.println("<p align=\"left\">&nbsp;&nbsp;Lesson Length (minutes):&nbsp;&nbsp;");
                     out.println("<input type=\"text\" name=\"ltype_length\" value=\"" +ilength+ "\" size=\"3\" maxlength=\"3\">");
                  out.println("</p>");

                  out.println("<p align=\"left\">&nbsp;&nbsp;Lesson Price (include $):&nbsp;&nbsp;");
                     out.println("<input type=\"text\" name=\"ltype_cost\" value=\"" +cost+ "\" size=\"8\" maxlength=\"20\">");
                  out.println("</p>");

                  if (sess_activity_id != 0 && !getActivity.isChildlessActivity(sess_activity_id, con)) {
                      
                      if (club.equals("benttreecc") || club.equals("elmcrestcc") || club.equals("ccyork")
                              || (club.equals("gallerygolf") && sess_activity_id == 9)) {
                          out.println("<p align=\"left\">");
                          out.println("&nbsp;&nbsp;Should booking this lesson type automatically block time slots for one of the selected locations?");
                          out.println("&nbsp;&nbsp;<select name=\"autoblock_times\" size=\"1\">");
                          out.println(" <option value=\"1\"" + (autoblock_times == 1 ? " selected" : "") + ">Yes</option>");
                          out.println(" <option value=\"0\"" + (autoblock_times == 0 ? " selected" : "") + ">No</option>");
                          out.println("</select>");
                          out.println("</p>");
                      } else {
                          out.println("<input type=\"hidden\" name=\"autoblock_times\" value=\"1\">");
                      }
                      out.println("<p align=\"left\">&nbsp;&nbsp;Choose the locations this lesson type can take place at:&nbsp;&nbsp;");
                      Common_Config.displayActivitySheetSelect(locations_csv, sess_activity_id, false, con, out);
                      out.println("</p>");
                  }

                  //
                  //   Script will put any existing descript info in the textarea (value= doesn't work)
                  //
                  out.println("<input type=\"hidden\" name=\"olddescript\" value=\"" +descript+ "\">"); // hold descript for script

                  out.println("<p align=\"left\">&nbsp;&nbsp;Lesson Description (up to 254 characters):&nbsp;&nbsp;");
                  out.println("<textarea name=\"ltype_desc\" value=\"\" id=\"descript\" cols=\"40\" rows=\"3\">");
                  out.println("</textarea>");
                  out.println("</p>");

                  out.println("<p align=\"center\">");
                  out.println("<input type=\"submit\" name=\"editltype2\" value=\"Update\">");    // to update record
                  out.println("</p>");
                  out.println("</font>");
                  out.println("</td>");
               out.println("</tr>");
               out.println("</form>");
            out.println("</table>");

            out.println("</td></tr></table>");                // end of main page table
            out.println("<font size=\"2\">");
            out.println("<form method=\"get\" action=\"Proshop_announce\">");
            out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");
            out.println("</center></font></body></html>");
            out.close();

         } else {

            nfError(req, out, lottery);            // LT not found
         }
         pstmt.close();

      }
      catch (Exception exc) {

         dbError(req, out, exc, lottery);
         return;
      }
   }
 }


 // ********************************************************************
 //  Process the 'Edit Lesson Type' SUBMIT request
 // ********************************************************************

 private void editLtype2(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException {

   PreparedStatement pstmt = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String ltname = "";
   String oldname = "";
   String cost = "";
   String descript = "";
   String tlength = "";
   String locations_csv = "";

   int ilength = 0;
   int id = 0;
   int autoblock_times = 0;

   try {
      id = Integer.parseInt(proid);          // convert the pro id
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   //
   //  Get the parameters
   //
   if (req.getParameter("ltype_name") != null) ltname = req.getParameter("ltype_name");
   if (req.getParameter("oldname") != null) oldname = req.getParameter("oldname");
   if (req.getParameter("ltype_cost") != null) cost = req.getParameter("ltype_cost");
   if (req.getParameter("ltype_desc") != null) descript = req.getParameter("ltype_desc");
   if (req.getParameter("ltype_length") != null) {

      tlength = req.getParameter("ltype_length");

      try {
         ilength = Integer.parseInt(tlength);
      }
      catch (NumberFormatException e) {
         ilength = 0;
      }
   }
   if (req.getParameter("autoblock_times") != null) {
       try {
           autoblock_times = Integer.parseInt(req.getParameter("autoblock_times"));
       } catch (Exception exc) {
           autoblock_times = 1;
       }
   }

   //
   //  Make sure user specified the minimum parameters
   //
   if (ltname.equals( "" ) || ltname == null || ilength == 0) {

      out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
      out.println("<BODY>");
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      out.println("<CENTER><BR>");
      out.println("<br><br>");
      out.println("<H3>Input Error</H3>");
      out.println("<BR>Sorry, you must specify at least a name and length value.<BR>");
      out.println("<BR>Please specify both values and try again.<BR>");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   if (sess_activity_id != 0) {
       //  Generate the locations string from the request
       locations_csv = Common_Config.buildLocationsString(req);

       if (locations_csv.equals("") && autoblock_times == 1) {

           out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
           out.println("<BODY>");
           SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
           out.println("<CENTER><BR>");
           out.println("<br><br>");
           out.println("<H3>Input Error</H3>");
           out.println("<BR>Sorry, you must specify at least one location where this lesson may take place.<BR>");
           out.println("<BR>Please specify locations and try again.<BR>");
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
   //  Make sure the lesson type (new name, if changed) doesn't already exist
   //
   if (!ltname.equalsIgnoreCase( oldname )) {      // if name changed
     
      try {

         pstmt = con.prepareStatement (
                 "SELECT length FROM lessontype5 WHERE proid = ? AND activity_id = ? AND ltname = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1, id);
         pstmt.setInt(2, sess_activity_id);
         pstmt.setString(3, ltname);
         rs = pstmt.executeQuery();      // execute the prepared pstmt

         if (rs.next()) {

            dupMem(req, out, lottery);
            pstmt.close();
            return;
         }
         pstmt.close();
      }
      catch (Exception exc) {
      }
   }

   //
   //  Update the lesson type
   //
   try {
      pstmt = con.prepareStatement (
        "UPDATE lessontype5 SET ltname = ?, length = ?, cost = ?, descript = ?, locations = ?, autoblock_times = ? " +
        "WHERE proid = ? AND activity_id = ? AND ltname = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, ltname);
      pstmt.setInt(2, ilength);
      pstmt.setString(3, cost);
      pstmt.setString(4, descript);
      pstmt.setString(5, locations_csv);
      pstmt.setInt(6, autoblock_times);

      pstmt.setInt(7, id);
      pstmt.setInt(8, sess_activity_id);
      pstmt.setString(9, oldname);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

   }
   catch (Exception exc) {

      dbError(req, out, exc, lottery);
      return;
   }

   //
   // Database updated - inform user
   //
   out.println(SystemUtils.HeadTitle("Proshop Add Lesson Type"));
   out.println("<BODY>");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<CENTER><BR>");
   out.println("<BR><BR><H3>The Lesson Type Has Been Updated</H3>");
   out.println("<BR><BR>Thank you, the lesson type has been updated for the specified pro.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<a href=\"Proshop_lesson?editltype=yes&proid=" +id+ "\">Edit Another Lesson Type For This Pro</a>");
   out.println("<BR><BR>");
   out.println("<a href=\"Proshop_lesson?editltype=yes\">Edit Lesson Type For Other Pro</a>");
   out.println("<BR><BR>");
   out.println("<form method=\"get\" action=\"Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 // ********************************************************************
 //  Process the 'Delete Lesson Type' SUBMIT request
 // ********************************************************************

 private void deleteLtype(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException {

   PreparedStatement pstmt = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String ltname = "";

   int id = 0;

   try {
      id = Integer.parseInt(proid);
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   //
   //  Get the parameters
   //
   if (req.getParameter("ltname") != null) {

      ltname = req.getParameter("ltname");
   }

   //
   //  Check if this is a confirmation to delete
   //
   if (req.getParameter("conf") != null) {

      //
      //  Delete the rest
      //
      try {
         pstmt = con.prepareStatement (
                  "Delete FROM lessontype5 WHERE proid = ? AND activity_id = ? AND ltname = ?");

         pstmt.clearParameters();               // clear the parms
         pstmt.setInt(1, id);
         pstmt.setInt(2, sess_activity_id);
         pstmt.setString(3, ltname);
         pstmt.executeUpdate();         // execute the prepared pstmt

         pstmt.close();

      }
      catch (Exception exc) {

         dbError(req, out, exc, lottery);
         return;
      }

      //
      //  Type deleted - inform user
      //
      out.println(SystemUtils.HeadTitle("Delete Lesson Type Confirmation"));
      out.println("<BODY>");
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      out.println("<CENTER><BR>");
      out.println("<p>&nbsp;</p>");
      out.println("<BR><H3>Lesson Type Removed</H3><BR>");
      out.println("<BR><b>" + ltname + "</b> has been removed from the lesson Type database.");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<a href=\"Proshop_lesson?editltype=yes&proid=" +id+ "\">Edit Another Lesson Type For This Pro</a>");
      out.println("<BR><BR>");
      out.println("<a href=\"Proshop_lesson?editltype=yes\">Edit Lesson Type For Other Pro</a>");
      out.println("<BR><BR>");
      out.println("<form method=\"get\" action=\"Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();

   } else {

      //
      //  This is the first request for the delete - request a confirmation
      //
      out.println(SystemUtils.HeadTitle("Delete Lesson Type Confirmation"));
      out.println("<BODY>");
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      out.println("<CENTER><BR>");
      out.println("<p>&nbsp;</p>");
      out.println("<BR><H3>Remove Lesson Type Confirmation</H3><BR>");
      out.println("<BR>Please confirm that you wish to remove this Lesson Type: <b>" + ltname + "</b><br>");

      out.println("<form action=\"Proshop_lesson\" method=\"post\" target=\"bot\">");
      out.println("<BR>ARE YOU SURE YOU WANT TO DELETE THIS LESSON TYPE?");

      out.println("<input type=\"hidden\" name=\"proid\" value =\"" +proid+ "\">");
      out.println("<input type=\"hidden\" name=\"deleteltype\" value =\"yes\">");
      out.println("<input type=\"hidden\" name=\"conf\" value =\"yes\">");
      out.println("<input type=\"hidden\" name=\"ltname\" value =\"" +ltname+ "\">");
      out.println("<br><br><input type=\"submit\" value=\"Yes - Delete\">");
      out.println("</form><font size=\"2\">");

      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"No - Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
   }
 }


 // ********************************************************************
 //  Process the 'Add Group Lesson' request
 // ********************************************************************

 private void addLgrp(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException {

   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String locations_csv = "";

   int[] recurrArr = new int[8];
   int clinic = 0;

   //
   //  Get this year
   //
   Calendar cal = new GregorianCalendar();       // get todays date
   long thisYear = cal.get(Calendar.YEAR);        // get the year
   long thisMonth = cal.get(Calendar.MONTH);
   long thisDay = cal.get(Calendar.DAY_OF_MONTH);
   long year = 0;
   thisMonth++;        // adjust
     
   //
   //  Build the html page to request the lesson info
   //
   out.println(SystemUtils.HeadTitle2("Proshop - Add Group Lesson"));
   out.println("<script type=\"text/javascript\">");
   out.println("<!--");
   out.println("function cursor() { document.forms['f'].lgrp_name.focus(); }");
   out.println("// -->");
   out.println("</script>");

   out.println("</head>");

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" onload=cursor()>");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<center>");

   out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\">");
   out.println("<tr><td align=\"center\">");
      out.println("<font color=\"#FFFFFF\" size=\"3\">");
      out.println("<b>Add Group Lesson</b><br>");
      out.println("</font><font color=\"#FFFFFF\" size=\"2\">");
      out.println("<br>Complete the following information for each Group Lesson to be added.<br>");
      out.println("This information will be displayed to the members when scheduling a lesson.");
      out.println("<br><br>Click on 'ADD' to add the Group Lesson.");
   out.println("</font></td></tr></table><br>");

   out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"6\">");
      out.println("<form action=\"Proshop_lesson\" method=\"post\" target=\"bot\" name=\"f\">");
      out.println("<input type=\"hidden\" name=\"proid\" value=\"" +proid+ "\">");
      out.println("<input type=\"hidden\" name=\"addlgrp2\" value=\"yes\">");
      out.println("<tr>");
         out.println("<td>");
         out.println("<font size=\"2\">");
         out.println("<p align=\"left\">&nbsp;&nbsp;<b>NOTE:</b>&nbsp;&nbsp;");
            out.println("The Lesson Book for this pro must already contain Lesson Times for the date and time<br>");
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("of the Group Lesson you are about to add. Refer to 'Lessons - Configure Lesson Book - <br>");
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("Edit Lesson Times' for a list of the current Lesson Times.");
         out.println("</p>");

         out.println("<p align=\"left\">&nbsp;&nbsp;Group Lesson Name:&nbsp;&nbsp;");
            out.println("<input type=\"text\" name=\"lgrp_name\" size=\"30\" maxlength=\"40\">");
            out.println("&nbsp;&nbsp;&nbsp;* Must be unique");
         out.println("</p>");

         dateOpts.displayStartDate(thisMonth, thisDay, thisYear, out);

         if (sess_activity_id != 0) dateOpts.displayEndDate(thisMonth, thisDay, thisYear, out);

         dateOpts.displayStartTime(1, 0, "AM", out);
         dateOpts.displayEndTime(1, 0, "AM", out);

         // Print out the recurrence options if an activity other than golf
         if (sess_activity_id != 0) {

             // Print radio buttons for every/every-other week option
             out.println("<table border=\"0\" cellpadding=\"2\" style=\"font-size: 10pt; font-weight: normal;\">");
             out.println("<tr>");
             out.println("<td valign=\"top\">&nbsp;Recurrence: </td>");
             out.println("<td valign=\"top\">");

             String checkedE = "";
             String checkedEO = "";

             if (recurrArr[0] != 1) {                  checkedE = "checked";
             } else {
                 checkedEO = "checked";
             }

             out.println("<label><input type=\"radio\" name=\"eo_week\" value=\"every\" " + checkedE + ">Every</label><br>");
             out.println("<label><input type=\"radio\" name=\"eo_week\" value=\"everyother\" " + checkedEO + ">Every other</label>");
             out.println("</td><td>");

             // Print checkboxes for day-of-week selection
             String [] checked = new String[7];
             for (int i=0; i<7; i++) {
                 if (recurrArr[i+1] == 1) {
                     checked[i] = "checked";
                 } else {
                     checked[i] = "";
                 }
             }
             out.println("<label><input type=\"checkbox\" name=\"day1\" value=\"yes\" " + checked[0] + ">Sunday</label><br>");
             out.println("<label><input type=\"checkbox\" name=\"day2\" value=\"yes\" " + checked[1] + ">Monday</label><br>");
             out.println("<label><input type=\"checkbox\" name=\"day3\" value=\"yes\" " + checked[2] + ">Tuesday</label><br>");
             out.println("<label><input type=\"checkbox\" name=\"day4\" value=\"yes\" " + checked[3] + ">Wednesday</label><br>");
             out.println("<label><input type=\"checkbox\" name=\"day5\" value=\"yes\" " + checked[4] + ">Thursday</label><br>");
             out.println("<label><input type=\"checkbox\" name=\"day6\" value=\"yes\" " + checked[5] + ">Friday</label><br>");
             out.println("<label><input type=\"checkbox\" name=\"day7\" value=\"yes\" " + checked[6] + ">Saturday</label><br>");

             out.println("</td><td valign=\"top\">");

             // Print radio buttons for clinic/non-clinic option
             out.println("<label><input type=\"radio\" name=\"clinic\" value=\"1\" checked>Clinic Series - One shared sign-up sheet for all recurrences</label><br>");
             out.println("<label><input type=\"radio\" name=\"clinic\" value=\"0\">Clinic - Separate sign-up sheets for each recurrence</label>");

             out.println("</td></tr></table>");
         }

         if (sess_activity_id != 0 && !getActivity.isChildlessActivity(sess_activity_id, con)) {
             out.println("<p align=\"left\">&nbsp;&nbsp;Choose the locations this group lesson will take place:&nbsp;&nbsp;");
             out.println("<br>&nbsp;&nbsp;(Note: <b>All</b> selected locations will be blocked off on the time sheets.)");
             Common_Config.displayActivitySheetSelect(locations_csv, sess_activity_id, false, con, out);
             out.println("</p>");
         }
         
         out.println("<p align=\"left\">&nbsp;&nbsp;Max Number of Members:&nbsp;&nbsp;");
            out.println("<input type=\"text\" name=\"lgrp_max\" size=\"3\" maxlength=\"3\">");
         out.println("</p>");

         out.println("<p align=\"left\">&nbsp;&nbsp;Lesson Price (include $):&nbsp;&nbsp;");
            out.println("<input type=\"text\" name=\"lgrp_cost\" size=\"8\" maxlength=\"20\">");
         out.println("&nbsp;&nbsp;&nbsp;per member</p>");

         out.println("<p align=\"left\">&nbsp;&nbsp;Color to make these times in the Lesson Book:&nbsp;&nbsp;");
         dateOpts.displayColors(out);

         out.println("<br>");
         out.println("&nbsp;&nbsp;Click here to see the available colors:&nbsp;");
         out.println("<a href=\"/" +rev+ "/proshop_color.htm\" target=\"_blank\">View Colors</a>");
         out.println("</p>");

         out.println("<p align=\"left\">&nbsp;&nbsp;Lesson Description (up to 254 characters):&nbsp;&nbsp;");
         out.println("<textarea name=\"lgrp_desc\" cols=\"40\" rows=\"3\">");
         out.println("</textarea>");
         out.println("</p>");

         out.println("<p align=\"center\">");
           out.println("<input type=\"submit\" value=\"ADD\">");
           out.println("");
         out.println("</p>");
         out.println("</font>");
         out.println("</td>");
      out.println("</tr>");
      out.println("</form>");
   out.println("</table>");
   out.println("</font>");

   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</center>");
   out.println("</body>");
   out.println("</html>");
   out.close();
 }


 // ********************************************************************
 //  Process the 'Add Group Lesson' SUBMIT request
 // ********************************************************************

 private void addLgrp2(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException {

   PreparedStatement pstmt = null;
   Statement stmt = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String lname = "";
   String cost = "";
   String descript = "";
   String smax = "";
   String color = "";
   String error = "";
   String locations_csv = "";

   int max = 0;
   int id = 0;
   int smonth = 0;                // init integer date/time values
   int sday = 0;
   int syear = 0;
   int emonth = 0;
   int eday = 0;
   int eyear = 0;
   int s_hr = 0;
   int s_min = 0;
   int s_ampm = 0;
   int e_hr = 0;
   int e_min = 0;
   int e_ampm = 0;
   int clinic = 0;

   long sdate = 0;
   long edate = 0;

   boolean daysSelected = false;
   boolean isChildlessActivity = false;
   
   if (sess_activity_id != 0) {
       isChildlessActivity = getActivity.isChildlessActivity(sess_activity_id, con);
   }

   int[] recurrArr = new int[8];

   List<Integer> sheet_ids = new ArrayList<Integer>();
   
   Map<Integer, List<Integer>> sheet_id_map = new LinkedHashMap<Integer, List<Integer>>();

   try {
      id = Integer.parseInt(proid);          // convert the pro id
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   //
   //  Get the parameters
   //
   String s_month = req.getParameter("smonth");             //  month (00 - 12)
   String s_day = req.getParameter("sday");                 //  day (01 - 31)
   String s_year = req.getParameter("syear");               //  year
   String start_hr = req.getParameter("start_hr");         //  start hour (01 - 12)
   String start_min = req.getParameter("start_min");       //  start min (00 - 59)
   String start_ampm = req.getParameter("start_ampm");     //  AM/PM (00 or 12)
   String end_hr = req.getParameter("end_hr");
   String end_min = req.getParameter("end_min");
   String end_ampm = req.getParameter("end_ampm");
   if (req.getParameter("lgrp_name") != null) lname = req.getParameter("lgrp_name");
   if (req.getParameter("lgrp_cost") != null) cost = req.getParameter("lgrp_cost");
   if (req.getParameter("color") != null) color = req.getParameter("color");
   if (req.getParameter("lgrp_desc") != null) descript = req.getParameter("lgrp_desc");
   if (req.getParameter("lgrp_max") != null) {

      smax = req.getParameter("lgrp_max");

      try {
         max = Integer.parseInt(smax);
      }
      catch (NumberFormatException e) {
         max = 0;
      }
   }

   if (sess_activity_id != 0) {

       clinic = Integer.parseInt(req.getParameter("clinic"));
       emonth = Integer.parseInt(req.getParameter("emonth"));
       eday = Integer.parseInt(req.getParameter("eday"));
       eyear = Integer.parseInt(req.getParameter("eyear"));

       edate = eyear * 10000;       // create a date field from input values
       edate = edate + (emonth * 100);
       edate = edate + eday;             // date = yyyymmdd (for comparisons)

       if (req.getParameter("eo_week") != null && req.getParameter("eo_week").equalsIgnoreCase("everyother")) {
           recurrArr[0] = 1;
       } else {
           recurrArr[0] = 0;
       }

       for (int i=1; i<8; i++) {
           if (req.getParameter("day" + String.valueOf(i)) != null) {
               recurrArr[i] = 1;
               daysSelected = true;        // so we know if any days were selected
           } else {
               recurrArr[i] = 0;
           }
       }

   } else {
       for (int i=0; i<8; i++) {
           recurrArr[i] = 1;
       }
   }


   //
   // Convert the numeric string parameters to Int's
   //
   try {
      smonth = Integer.parseInt(s_month);
      sday = Integer.parseInt(s_day);
      syear = Integer.parseInt(s_year);
      s_hr = Integer.parseInt(start_hr);
      s_min = Integer.parseInt(start_min);
      s_ampm = Integer.parseInt(start_ampm);
      e_hr = Integer.parseInt(end_hr);
      e_min = Integer.parseInt(end_min);
      e_ampm = Integer.parseInt(end_ampm);
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }

   //
   //  Make sure user specified the minimum parameters
   //
   if (lname.equals( "" ) || lname == null || max == 0) {

      out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
      out.println("<BODY>");
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      out.println("<CENTER><BR>");
      out.println("<br><br>");
      out.println("<H3>Input Error</H3>");
      out.println("<BR>Sorry, you must specify at least a name and max number of members value.<BR>");
      out.println("<BR>Please specify both values and try again.<BR>");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   if (sess_activity_id != 0 && !isChildlessActivity) {
       //  Generate the locations string from the request
       locations_csv = Common_Config.buildLocationsString(req);

       if (locations_csv.equals("")) {

           out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
           out.println("<BODY>");
           SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
           out.println("<CENTER><BR>");
           out.println("<br><br>");
           out.println("<H3>Input Error</H3>");
           out.println("<BR>Sorry, you must specify at least one location where this lesson may take place.<BR>");
           out.println("<BR>Please specify locations and try again.<BR>");
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
   //  adjust some values for the table
   //
   sdate = syear * 10000;       // create a date field from input values
   sdate = sdate + (smonth * 100);
   sdate = sdate + sday;             // date = yyyymmdd (for comparisons)

   if (edate == 0) edate = sdate;

   if (s_hr != 12) {                // _hr specified as 01 - 12 (_ampm = 00 or 12)

      s_hr = s_hr + s_ampm;         // convert to military time (12 is always Noon, or PM)
   }

   if (e_hr != 12) {                // ditto

      e_hr = e_hr + e_ampm;
   }

   int stime = s_hr * 100;
   stime = stime + s_min;
   int etime = e_hr * 100;
   etime = etime + e_min;

   //
   //  Get today's date
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   int year = cal.get(Calendar.YEAR);
   int month = cal.get(Calendar.MONTH);
   int day = cal.get(Calendar.DAY_OF_MONTH);
   month++;                           // month starts at zero

   long today = year * 10000;                        // create a today field of yyyymmdd (for today)
   today = today + (month * 100);
   today = today + day;                         // date = yyyymmdd (for comparisons)

   //
   //  verify the date and time fields
   //
   if (sdate < today) {

      error = "The Start Date cannot be earlier than today.  Please correct the date.";
      invData(req, out, lottery, error);
      return;
   }
   if (stime > etime) {

      error = "The Start Time cannot be later than the End Time.  Please correct the Time range.";
      invData(req, out, lottery, error);
      return;
   }

   //
   //  Make sure there are lesson times already defined for this date and time
   //
   try {

      pstmt = con.prepareStatement (
              "SELECT lname FROM lessontime5 WHERE proid = ? AND activity_id = ? AND sdate <= ? AND edate >= ? AND " +
              "stime <= ? AND etime >= ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1, id);
      pstmt.setLong(2, sdate);
      pstmt.setLong(3, sdate);
      pstmt.setInt(4, stime);
      pstmt.setInt(5, etime);
      rs = pstmt.executeQuery();      // execute the prepared stmt

      if (!rs.next()) {

         error = "There are no Lesson Times defined for this pro that include the requested date and time period." +
                 "<BR><BR>You must first add the Lesson Times or change the date/time fields for this Group Lesson.";
         invData(req, out, lottery, error);
         return;
      }
      pstmt.close();

   }
   catch (Exception exc) {
   }

   //
   //  Make sure the lesson grp doesn't already exist
   //
   try {

      pstmt = con.prepareStatement (
              "SELECT date FROM lessongrp5 WHERE proid = ? AND activity_id = ? AND lname = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1, id);
      pstmt.setInt(2, sess_activity_id);
      pstmt.setString(3, lname);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      if (rs.next()) {

         dupMem(req, out, lottery);
         pstmt.close();
         return;
      }
      pstmt.close();
   }
   catch (Exception exc) {
   }

   //
   //  Add the lesson grp
   //
   try {
       pstmt = con.prepareStatement (
               "INSERT INTO lessongrp5 (proid, activity_id, lname, date, edate, stime, etime, max, cost, color, " +
               "descript, clinic, locations, eo_week, sunday, monday, tuesday, wednesday, thursday, friday, " +
               "saturday) " +
               "VALUES " +
               "(?,?,?,?,?,?,?,?,?,?," +
               " ?,?,?,?,?,?,?,?,?,?," +
               " ?)");

       pstmt.clearParameters();        // clear the parms
       pstmt.setInt(1, id);
       pstmt.setInt(2, sess_activity_id);
       pstmt.setString(3, lname);       // put the parm in pstmt
       pstmt.setLong(4, sdate);
       pstmt.setLong(5, edate);
       pstmt.setInt(6, stime);
       pstmt.setInt(7, etime);
       pstmt.setInt(8, max);
       pstmt.setString(9, cost);
       pstmt.setString(10, color);
       pstmt.setString(11, descript);
       pstmt.setInt(12, clinic);
       pstmt.setString(13, locations_csv);

       int offsetVal = 14;
       for (int i=0; i<8; i++) {
           pstmt.setInt(offsetVal + i, recurrArr[i]);
       }
       pstmt.executeUpdate();          // execute the prepared stmt

       pstmt.close();   // close the stmt

       // If we're currently on an activity other than golf, check to ensure that these times are available on the time sheets
       // If not, reject the change.
       if (sess_activity_id != 0 && !isChildlessActivity) {

          // Create a temp entry in lessongrp5 so we can get an id
          stmt = con.createStatement();
          rs = stmt.executeQuery("SELECT LAST_INSERT_ID() as lesson_id FROM lessongrp5");

          if (rs.next()) {

              int lesson_id = rs.getInt("lesson_id");
              int lesson_id_neg = lesson_id * -1;

              boolean bookAllSheets = true;
              boolean autoSelectLocation = true;

              // If no days were selected, send them back
              if (!daysSelected) {

                  error = "At least one day of the week must be selected for this group lesson. Please correct the recurrence.";
                  invData(req, out, lottery, error);
                  return;
              }

              if (edate < sdate) {
                  error = "The End Date cannot be earlier than the Start Date.  Please correct the date. " + sdate + " " + edate;
                  invData(req, out, lottery, error);
                  return;
              }

              sheet_id_map = verifyLesson.checkActivityTimes(lesson_id_neg, sess_activity_id, locations_csv, sdate, edate, recurrArr, stime, etime, bookAllSheets, autoSelectLocation, con, out);
              
              if (sheet_id_map.get(0) != null) {
                  sheet_ids = sheet_id_map.get(0);
              }

              // If no time slots returned, send the user back to the lesson book
              if (sheet_ids.size() == 0) {

                  // Delete the group lesson from the database
                  pstmt = con.prepareStatement("DELETE FROM lessongrp5 WHERE lesson_id = ?");
                  pstmt.clearParameters();
                  pstmt.setInt(1, lesson_id);

                  pstmt.executeUpdate();

                  pstmt.close();

                  out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
                  out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
                  out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                  out.println("<CENTER><BR><BR><H3>No Times Available</H3>");
                  out.println("<BR><BR>Sorry, no available time slots were found for this group lesson.<BR>");
                  out.println("<BR>Please adjust the date and time values, or remove players from the time sheet occupying conflicting times.");
                  out.println("<BR><BR>");
                  out.println("<font size=\"2\">");
                  out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
                  out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
                  out.println("</form></font>");
                  out.println("</CENTER></BODY></HTML>");
                  out.close();
                  return;
                  
              } else if (sheet_ids.size() > 0 && sheet_ids.get(0) != -99) {

                  // All needed times were found.  Update activity_sheets and place this lesson_id (negative) in all associated sheet_ids
                  // if index 0 was -99, that means no time slots in activity_sheets are needed for this lesson, skip this portion
                  String inString = "";
                  int count = 0;

                  for (int k=0; k<sheet_ids.size(); k++) {
                      inString += sheet_ids.get(k) + ",";
                  }

                  inString = inString.substring(0, inString.length() - 1);

                  try {
                      // First clear out existing lesson_ids for this lesson
                      pstmt = con.prepareStatement("UPDATE activity_sheets SET lesson_id = 0 WHERE lesson_id = ?");
                      pstmt.clearParameters();
                      pstmt.setInt(1, lesson_id_neg);
                      pstmt.executeUpdate();

                      pstmt.close();

                      // Now apply lesson_id to new set of times
                      pstmt = con.prepareStatement("UPDATE activity_sheets SET lesson_id = ? WHERE sheet_id IN (" + inString + ")");
                      pstmt.clearParameters();
                      pstmt.setInt(1, lesson_id_neg);

                      count = pstmt.executeUpdate();

                      pstmt.close();

                      if (count < inString.length()) {
                          out.println("<!-- Not enough slots blocked!!! -->");
                      }


                  } catch (Exception exc) {
                      out.println("<!-- Error encountered: " + exc.getMessage() + " -->");
                  }
              }
          }
          stmt.close();

          //
          //  Set the new lesson group name in all lesson times
          //
          String recurrCheck = "";
          boolean eo_week = false;
          
           if (recurrArr[0] == 1) {    // eo_week check
              recurrCheck += "AND (MOD(DATE_FORMAT(date,'%U'), 2) = MOD(DATE_FORMAT(?,'%U'), 2)) ";
              eo_week = true;
          }

          String dayStr = "";
          String prefix = "AND (";

          if (recurrArr[1] == 1) {    // sunday check
              dayStr += prefix + "DATE_FORMAT(date, '%w') = '0' ";
              prefix = "OR ";
          }
          if (recurrArr[2] == 1) {    // monday check
              dayStr += prefix + "DATE_FORMAT(date, '%w') = '1' ";
              prefix = "OR ";
          }
          if (recurrArr[3] == 1) {    // tuesday check
              dayStr += prefix + "DATE_FORMAT(date, '%w') = '2' ";
              prefix = "OR ";
          }
          if (recurrArr[4] == 1) {    // wednesday check
              dayStr += prefix + "DATE_FORMAT(date, '%w') = '3' ";
              prefix = "OR ";
          }
          if (recurrArr[5] == 1) {    // thursday check
              dayStr += prefix + "DATE_FORMAT(date, '%w') = '4' ";
              prefix = "OR ";
          }
          if (recurrArr[6] == 1) {    // friday check
              dayStr += prefix + "DATE_FORMAT(date, '%w') = '5' ";
              prefix = "OR ";
          }
          if (recurrArr[7] == 1) {    // saturday check
              dayStr += prefix + "DATE_FORMAT(date, '%w') = '6'";
              prefix = "OR ";
          }
          if (!dayStr.equals("")) {
              dayStr += ") ";
          }

          recurrCheck += dayStr;

          // Instead of the normal update, for activities we need to apply to recurrance days as well
          pstmt = con.prepareStatement(
                  "UPDATE lessonbook5 SET lgname = ? " +
                  "WHERE proid = ? AND activity_id = ? AND date >= ? AND date <= ? " +
                  "AND time >= ? AND time <= ? " +
                  recurrCheck);
          pstmt.clearParameters();
          pstmt.setString(1, lname);
          pstmt.setInt(2, id);
          pstmt.setInt(3, sess_activity_id);
          pstmt.setLong(4, sdate);
          pstmt.setLong(5, edate);
          pstmt.setInt(6, stime);
          pstmt.setInt(7, etime);
          if (eo_week) pstmt.setLong(8, sdate);
          pstmt.executeUpdate();

          pstmt.close();

       } else {

           pstmt = con.prepareStatement (
                   "UPDATE lessonbook5 SET lgname = ? " +
                   "WHERE proid = ? AND activity_id = ? AND date = ? AND time >= ? AND time <= ?");

           pstmt.clearParameters();               // clear the parms
           pstmt.setString(1, lname);
           pstmt.setInt(2, id);
           pstmt.setInt(3, sess_activity_id);
           pstmt.setLong(4, sdate);
           pstmt.setInt(5, stime);
           pstmt.setInt(6, etime);
           pstmt.executeUpdate();         // execute the prepared pstmt

           pstmt.close();
       }

   }
   catch (Exception exc) {

       dbError(req, out, exc, lottery);
       return;
   }
   
   //
   // Database updated - inform user
   //
   out.println(SystemUtils.HeadTitle("Proshop Add Group Lesson"));
   out.println("<BODY>");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<CENTER><BR>");
   out.println("<BR><BR><H3>The Group Lesson Has Been Added</H3>");
   out.println("<BR><BR>Thank you, the Group Lesson has been added for the specified pro.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<a href=\"Proshop_lesson?addlgrp=yes&proid=" +id+ "\">Add Another Group Lesson For This Pro</a>");
   out.println("<BR><BR>");
   out.println("<a href=\"Proshop_lesson?addlgrp=yes\">Add Group Lesson For Other Pro</a>");
   out.println("<BR><BR>");
   out.println("<a href=\"Proshop_lesson?editlgrp=yes&proid=" +id+ "\">View Group Lessons For This Pro</a>");
   out.println("<BR><BR>");
   out.println("<form method=\"get\" action=\"Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 // ********************************************************************
 //  Process the 'Edit Group Lesson' request
 // ********************************************************************

 private void editLgrp(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException {

   PreparedStatement pstmt = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String lname = "";
   String cost = "";
   String color = "";
   String descript = "";
   String s_ampm = "AM";
   String e_ampm = "AM";
   String locations_csv = "";

   int count = 0;
   int id = 0;
   int max = 0;
   int s_hr = 0;
   int s_min = 0;
   int e_hr = 0;
   int e_min = 0;
   int stime = 0;
   int etime = 0;
   int clinic = 0;
   int lesson_id = 0;

   long sdate = 0;
   long edate = 0;
   long smonth = 0;                // init longeger date/time values
   long sday = 0;
   long syear = 0;
   long emonth = 0;
   long eday = 0;
   long eyear = 0;
   long mm = 0;
   long dd = 0;
   long yy = 0;

   boolean b = false;

   int [] recurrArr = new int [8];        // index 0 = eo_week value, 1-7 = Sun-Sat values

   try {
      id = Integer.parseInt(proid);
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   //
   //  Get this year
   //
   Calendar cal = new GregorianCalendar();       // get todays date
   int thisYear = cal.get(Calendar.YEAR);        // get the year
   int year = 0;

   if (req.getParameter("editGRP") == null) {       // if not a specific edit request (if this is the 1st call)

      //
      //  Build the HTML page to display the existing Group Lessons
      //
      out.println(SystemUtils.HeadTitle("Proshop Group Lessons Page"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr><td align=\"center\">");

         out.println("<table cellpadding=\"5\" bgcolor=\"#336633\" border=\"0\" align=\"center\">");
         out.println("<tr><td align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"3\">");
         out.println("<b>Edit Group Lessons</b><br>");
         out.println("</font>");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<br>To update a Group Lesson, click on the Update button within that Group Lesson.");
         out.println("<br>To remove a Group Lesson, click on the Delete button within that Group Lesson.");
         out.println("<br></td></tr></table>");
         out.println("<br>");

         out.println("<table border=\"2\" cellpadding=\"5\" bgcolor=\"#F5F5DC\">");
         out.println("<tr bgcolor=\"#8B8970\">");
         out.println("<td colspan=\"9\" align=\"center\">");
         out.println("<font size=\"2\">");
         out.println("<p align=\"center\"><b>Active Group Lessons</b></p>");
         out.println("</font></td></tr>");
         out.println("<tr bgcolor=\"#8B8970\"><td align=\"center\">");
         out.println("<font size=\"2\"><p><b>Group Lesson Name</b></p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p><b>Date</b></p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p><b>Start Time</b></p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p><b>End Time</b></p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p><b>Max Mems</b></p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p><b>Cost Per</b></p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p><b>Color</b></p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p><b>Description</b></p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>&nbsp;</p>");      // empty for select buttons
         out.println("</font></td></tr>");

      //
      //  Get the lesson grp records
      //
      try {

         pstmt = con.prepareStatement (
                 "SELECT * FROM lessongrp5 WHERE proid = ? AND activity_id = ? ORDER BY date");

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1,id);
         pstmt.setInt(2,sess_activity_id);
         rs = pstmt.executeQuery();      // execute the prepared pstmt

         while (rs.next()) {

            b = true;                     // indicate lesson groups exist

            lesson_id = rs.getInt("lesson_id");
            lname = rs.getString("lname");
            sdate = rs.getLong("date");
            stime = rs.getInt("stime");
            etime = rs.getInt("etime");
            max = rs.getInt("max");
            cost = rs.getString("cost");
            color = rs.getString("color");
            descript = rs.getString("descript");

            //
            //  adjust some values for the table
            //
            yy = sdate / 10000;                             // get year
            mm = (sdate - (yy * 10000)) / 100;              // get month
            dd = (sdate - (yy * 10000)) - (mm * 100);       // get day

            smonth = mm;
            sday = dd;
            syear = yy;

            s_ampm = "AM";       // defaults
            e_ampm = "AM";

            s_hr = stime / 100;
            s_min = stime - (s_hr * 100);

            e_hr = etime / 100;
            e_min = etime - (e_hr * 100);

            if (s_hr == 12) {

               s_ampm = "PM";

            } else {

               if (s_hr > 12) {

                  s_hr = s_hr - 12;          // adjust
                  s_ampm = "PM";
               }
            }

            if (e_hr == 12) {

               e_ampm = "PM";

            } else {

               if (e_hr > 12) {

                  e_hr = e_hr - 12;          // adjust
                  e_ampm = "PM";
               }
            }

            if (!color.equalsIgnoreCase( "default" )) {
               out.println("<tr bgcolor=\"" +color+ "\">");
            } else {
               out.println("<tr>");
            }
            out.println("<form action=\"Proshop_lesson\" method=\"post\" target=\"bot\" name=\"f\">");
            out.println("<input type=\"hidden\" name=\"lesson_id\" value=\"" + lesson_id + "\">");
            out.println("<input type=\"hidden\" name=\"proid\" value=\"" +proid+ "\">");
            out.println("<input type=\"hidden\" name=\"lname\" value=\"" +lname+ "\">");
               out.println("<td align=\"center\">");
                 out.println("<font size=\"2\">");
                 out.println(lname);
                 out.println("</font>");
               out.println("</td>");

               out.println("<td align=\"center\">");
                 out.println("<font size=\"2\">");
                 out.println(smonth+ "/" +sday+ "/" +syear);
                 out.println("</font>");
               out.println("</td>");

               out.println("<td align=\"center\">");
                 out.println("<font size=\"2\">");
                 if (s_min < 10) {
                    out.println(s_hr+ ":0" +s_min+ " " +s_ampm);
                 } else {
                    out.println(s_hr+ ":" +s_min+ " " +s_ampm);
                 }
                 out.println("</font>");
               out.println("</td>");

               out.println("<td align=\"center\">");
                 out.println("<font size=\"2\">");
                 if (e_min < 10) {
                    out.println(e_hr+ ":0" +e_min+ " " +e_ampm);
                 } else {
                    out.println(e_hr+ ":" +e_min+ " " +e_ampm);
                 }
                 out.println("</font>");
               out.println("</td>");

               out.println("<td align=\"center\">");
                 out.println("<font size=\"2\">");
                 out.println(max);
                 out.println("</font>");
               out.println("</td>");

               out.println("<td align=\"center\">");
                 out.println("<font size=\"2\">");
                 out.println(cost);
                 out.println("</font>");
               out.println("</td>");

                if (!color.equals( "Default" )) {
                  out.println("<td align=\"center\" bgcolor=\"" +color+ "\">");
                } else {
                  out.println("<td align=\"center\">");
                }
                 out.println("<font size=\"2\">");
                 out.println(color);
                 out.println("</font>");
               out.println("</td>");

               out.println("<td width=\"160\">");
                 out.println("<font size=\"2\">");
                 out.println(descript);
                 out.println("</textarea>");
                 out.println("</font>");
               out.println("</td>");

               out.println("<td align=\"center\">");
                 out.println("<font size=\"2\">");
                   out.println("<p align=\"center\">");
                   out.println("<input type=\"submit\" name=\"editGRP\" value=\"Update\">");    // to update record
                   out.println("&nbsp;&nbsp;");
                   out.println("<input type=\"submit\" name=\"deletelgrp\" value=\"Delete\">");   // to delete the record
                   out.println("</p>");
                 out.println("</font>");
               out.println("</td>");

            out.println("</form>");
            out.println("</tr>");

         }
         pstmt.close();

         if (!b) {

            out.println("</font><font size=\"2\"><p>No Group Lessons Currently Exist</p>");
         }
      }
      catch (Exception exc) {

         dbError(req, out, exc, lottery);
         return;
      }

      //
      //  End of HTML page
      //
      out.println("</table></font>");                   // end of lesson groups table
      out.println("</td></tr></table>");                // end of main page table
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</center></font></body></html>");
      out.close();

   } else {

      //******************************************************************
      //  Request is to edit a specific Lesson grp
      //******************************************************************
      //
      if (req.getParameter("lname") != null) lname = req.getParameter("lname");
      if (req.getParameter("lesson_id") != null) lesson_id = Integer.parseInt(req.getParameter("lesson_id"));

      //
      //  Get the lesson grp records
      //
      try {
          
         pstmt = con.prepareStatement (
                 "SELECT * FROM lessongrp5 WHERE lesson_id = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1,lesson_id);
         rs = pstmt.executeQuery();      // execute the prepared pstmt

         if (rs.next()) {

            sdate = rs.getLong("date");
            stime = rs.getInt("stime");
            etime = rs.getInt("etime");
            max = rs.getInt("max");
            cost = rs.getString("cost");
            color = rs.getString("color");
            descript = rs.getString("descript");
            locations_csv = rs.getString("locations");

            if (sess_activity_id != 0) edate = rs.getLong("edate");

            recurrArr[0] = rs.getInt("eo_week");
            recurrArr[1] = rs.getInt("sunday");
            recurrArr[2] = rs.getInt("monday");
            recurrArr[3] = rs.getInt("tuesday");
            recurrArr[4] = rs.getInt("wednesday");
            recurrArr[5] = rs.getInt("thursday");
            recurrArr[6] = rs.getInt("friday");
            recurrArr[7] = rs.getInt("saturday");
            clinic = rs.getInt("clinic");

            //
            //  adjust some values for the table
            //
            yy = sdate / 10000;                             // get year
            mm = (sdate - (yy * 10000)) / 100;              // get month
            dd = (sdate - (yy * 10000)) - (mm * 100);       // get day

            smonth = (int)mm;
            sday = (int)dd;
            syear = (int)yy;

            yy = edate / 10000;                             // get year
            mm = (edate - (yy * 10000)) / 100;              // get month
            dd = (edate - (yy * 10000)) - (mm * 100);       // get day

            emonth = mm;
            eday = dd;
            eyear = yy;

            s_hr = stime / 100;
            s_min = stime - (s_hr * 100);

            e_hr = etime / 100;
            e_min = etime - (e_hr * 100);

            if (s_hr == 12) {

               s_ampm = "PM";

            } else {

               if (s_hr > 12) {

                  s_hr = s_hr - 12;          // adjust
                  s_ampm = "PM";
               }
            }

            if (e_hr == 12) {

               e_ampm = "PM";

            } else {

               if (e_hr > 12) {

                  e_hr = e_hr - 12;          // adjust
                  e_ampm = "PM";
               }
            }

            //
            //  Build the HTML page to display the selected Lesson grp
            //
            out.println(SystemUtils.HeadTitle2("Proshop Group Lessons Page"));
            //
            //*******************************************************************
            //  Move cancellation descript into text area
            //*******************************************************************
            //
            out.println("<script type\"text/javascript\">");             // Move descript into textarea
            out.println("<!--");
            out.println("function movedescript() {");
            out.println(" var olddescript = document.forms['f'].olddescript.value;");
            out.println(" document.forms['f'].descript.value = olddescript;");   // put descript in text area
            out.println("}");                  // end of script function
            out.println("// -->");
            out.println("</script>");          // End of script

            out.println("</head>");
            out.println("<body onload=\"movedescript()\" bgcolor=\"#FFFFFF\" text=\"#000000\">");
            SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
            out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

            out.println("<table border=\"0\" align=\"center\">");
            out.println("<tr><td align=\"center\">");

            out.println("<table cellpadding=\"5\" bgcolor=\"#336633\" border=\"0\" align=\"center\">");
            out.println("<tr><td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"3\">");
            out.println("<b>Edit Group Lesson</b><br>");
            out.println("</font>");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<br>To update a Group Lesson, make the desired changes and click on the Update button.");
            out.println("<br></td></tr></table>");
            out.println("<br><br>");

            out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"6\">");
               out.println("<form action=\"Proshop_lesson\" method=\"post\" target=\"bot\" name=\"f\">");
               out.println("<input type=\"hidden\" name=\"proid\" value=\"" +proid+ "\">");
               out.println("<input type=\"hidden\" name=\"oldname\" value=\"" +lname+ "\">");
               out.println("<input type=\"hidden\" name=\"lesson_id\" value=\"" +lesson_id+ "\">");
               out.println("<tr>");
                  out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"left\">&nbsp;&nbsp;Group Lesson Name:&nbsp;&nbsp;");
                     out.println("<input type=\"text\" name=\"lgrp_name\" value=\"" +lname+ "\" size=\"30\" maxlength=\"40\">");
                     out.println("&nbsp;&nbsp;&nbsp;* Must be unique");
                  out.println("</p>");

                  dateOpts.displayStartDate(smonth, sday, syear, out);

                  if (sess_activity_id != 0) {
                      dateOpts.displayEndDate(emonth, eday, eyear, out);
                  }

                  out.println("<p align=\"left\">&nbsp;&nbsp;Start Time: &nbsp;&nbsp; hr &nbsp;");
                    out.println("<select size=\"1\" name=\"start_hr\">");
                      if (s_hr == 1) {
                         out.println("<option selected selected value=\"01\">1</option>");
                      } else {
                         out.println("<option value=\"01\">1</option>");
                      }
                      if (s_hr == 2) {
                         out.println("<option selected value=\"02\">2</option>");
                      } else {
                         out.println("<option value=\"02\">2</option>");
                      }
                      if (s_hr == 3) {
                         out.println("<option selected value=\"03\">3</option>");
                      } else {
                         out.println("<option value=\"03\">3</option>");
                      }
                      if (s_hr == 4) {
                         out.println("<option selected value=\"04\">4</option>");
                      } else {
                         out.println("<option value=\"04\">4</option>");
                      }
                      if (s_hr == 5) {
                         out.println("<option selected value=\"05\">5</option>");
                      } else {
                         out.println("<option value=\"05\">5</option>");
                      }
                      if (s_hr == 6) {
                         out.println("<option selected value=\"06\">6</option>");
                      } else {
                         out.println("<option value=\"06\">6</option>");
                      }
                      if (s_hr == 7) {
                         out.println("<option selected value=\"07\">7</option>");
                      } else {
                         out.println("<option value=\"07\">7</option>");
                      }
                      if (s_hr == 8) {
                         out.println("<option selected value=\"08\">8</option>");
                      } else {
                         out.println("<option value=\"08\">8</option>");
                      }
                      if (s_hr == 9) {
                         out.println("<option selected value=\"09\">9</option>");
                      } else {
                         out.println("<option value=\"09\">9</option>");
                      }
                      if (s_hr == 10) {
                         out.println("<option selected value=\"10\">10</option>");
                      } else {
                         out.println("<option value=\"10\">10</option>");
                      }
                      if (s_hr == 11) {
                         out.println("<option selected value=\"11\">11</option>");
                      } else {
                         out.println("<option value=\"11\">11</option>");
                      }
                      if (s_hr == 12) {
                         out.println("<option selected value=\"12\">12</option>");
                      } else {
                         out.println("<option value=\"12\">12</option>");
                      }
                    out.println("</select>");
                    out.println("&nbsp; min &nbsp;");
                    if (s_min < 10) {
                       out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=0" + s_min + " name=\"start_min\">");
                    } else {
                       out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + s_min + " name=\"start_min\">");
                    }
                    out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"start_ampm\">");
                      if (s_ampm.equals( "AM" )) {
                         out.println("<option selected value=\"00\">AM</option>");
                      } else {
                         out.println("<option value=\"00\">AM</option>");
                      }
                      if (s_ampm.equals( "PM" )) {
                         out.println("<option selected value=\"12\">PM</option>");
                      } else {
                         out.println("<option value=\"12\">PM</option>");
                      }
                    out.println("</select>");
                  out.println("</p>");
                  out.println("<p align=\"left\">&nbsp;&nbsp;End Time: &nbsp;&nbsp;&nbsp;&nbsp; hr &nbsp;");
                    out.println("<select size=\"1\" name=\"end_hr\">");
                      if (e_hr == 1) {
                         out.println("<option selected selected value=\"01\">1</option>");
                      } else {
                         out.println("<option value=\"01\">1</option>");
                      }
                      if (e_hr == 2) {
                         out.println("<option selected value=\"02\">2</option>");
                      } else {
                         out.println("<option value=\"02\">2</option>");
                      }
                      if (e_hr == 3) {
                         out.println("<option selected value=\"03\">3</option>");
                      } else {
                         out.println("<option value=\"03\">3</option>");
                      }
                      if (e_hr == 4) {
                         out.println("<option selected value=\"04\">4</option>");
                      } else {
                         out.println("<option value=\"04\">4</option>");
                      }
                      if (e_hr == 5) {
                         out.println("<option selected value=\"05\">5</option>");
                      } else {
                         out.println("<option value=\"05\">5</option>");
                      }
                      if (e_hr == 6) {
                         out.println("<option selected value=\"06\">6</option>");
                      } else {
                         out.println("<option value=\"06\">6</option>");
                      }
                      if (e_hr == 7) {
                         out.println("<option selected value=\"07\">7</option>");
                      } else {
                         out.println("<option value=\"07\">7</option>");
                      }
                      if (e_hr == 8) {
                         out.println("<option selected value=\"08\">8</option>");
                      } else {
                         out.println("<option value=\"08\">8</option>");
                      }
                      if (e_hr == 9) {
                         out.println("<option selected value=\"09\">9</option>");
                      } else {
                         out.println("<option value=\"09\">9</option>");
                      }
                      if (e_hr == 10) {
                         out.println("<option selected value=\"10\">10</option>");
                      } else {
                         out.println("<option value=\"10\">10</option>");
                      }
                      if (e_hr == 11) {
                         out.println("<option selected value=\"11\">11</option>");
                      } else {
                         out.println("<option value=\"11\">11</option>");
                      }
                      if (e_hr == 12) {
                         out.println("<option selected value=\"12\">12</option>");
                      } else {
                         out.println("<option value=\"12\">12</option>");
                      }
                    out.println("</select>");
                    out.println("&nbsp; min &nbsp;");
                    if (e_min < 10) {
                       out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=0" + e_min + " name=\"end_min\">");
                    } else {
                       out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + e_min + " name=\"end_min\">");
                    }
                    out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"end_ampm\">");
                      if (e_ampm.equals( "AM" )) {
                         out.println("<option selected value=\"00\">AM</option>");
                      } else {
                         out.println("<option value=\"00\">AM</option>");
                      }
                      if (e_ampm.equals( "PM" )) {
                         out.println("<option selected value=\"12\">PM</option>");
                      } else {
                         out.println("<option value=\"12\">PM</option>");
                      }
                    out.println("</select>");
                  out.println("</p>");

                  // Print out the recurrence options if an activity other than golf
                  if (sess_activity_id != 0) {
                      out.println("<table border=\"0\" cellpadding=\"2\" style=\"font-size: 10pt; font-weight: normal;\">");
                      out.println("<tr>");
                      out.println("<td valign=\"top\">&nbsp;Recurrence: </td>");
                      out.println("<td valign=\"top\">");

                      String checkedE = "";
                      String checkedEO = "";

                      if (recurrArr[0] != 1) {
                          checkedE = "checked";
                      } else {
                          checkedEO = "checked";
                      }


                      out.println("<label><input type=\"radio\" name=\"eo_week\" value=\"every\" " + checkedE + ">Every</label><br>");
                      out.println("<label><input type=\"radio\" name=\"eo_week\" value=\"everyother\" " + checkedEO + ">Every other</label>");
                      out.println("</td><td>");

                      String [] checked = new String[7];
                      for (int i=0; i<7; i++) {
                          if (recurrArr[i+1] == 1) {
                              checked[i] = "checked";
                          } else {
                              checked[i] = "";
                          }
                      }
                      out.println("<label><input type=\"checkbox\" name=\"day1\" value=\"yes\" " + checked[0] + ">Sunday</label><br>");
                      out.println("<label><input type=\"checkbox\" name=\"day2\" value=\"yes\" " + checked[1] + ">Monday</label><br>");
                      out.println("<label><input type=\"checkbox\" name=\"day3\" value=\"yes\" " + checked[2] + ">Tuesday</label><br>");
                      out.println("<label><input type=\"checkbox\" name=\"day4\" value=\"yes\" " + checked[3] + ">Wednesday</label><br>");
                      out.println("<label><input type=\"checkbox\" name=\"day5\" value=\"yes\" " + checked[4] + ">Thursday</label><br>");
                      out.println("<label><input type=\"checkbox\" name=\"day6\" value=\"yes\" " + checked[5] + ">Friday</label><br>");
                      out.println("<label><input type=\"checkbox\" name=\"day7\" value=\"yes\" " + checked[6] + ">Saturday</label><br>");

                      out.println("</td><td valign=\"top\">");

                      // Print radio buttons for clinic/non-clinic option, but disable (these can only be set at setup
                      String checkedC = "";
                      String checkedNC = "";

                      if (clinic == 1) {
                          checkedC = "checked ";
                      } else {
                          checkedNC = "checked ";
                      }

                      out.println("&nbsp;<b>Note</b>: Clinic option may only be changed during initial setup.<br>");
                      out.println("<input type=\"radio\" name=\"clinic\" value=\"1\" " + checkedC + " disabled>Clinic Series - One shared sign-up sheet for all recurrences<br>");
                      out.println("<input type=\"radio\" name=\"clinic\" value=\"0\" " + checkedNC + " disabled>Clinic - Separate sign-up sheets for each recurrence");

                      out.println("</td></tr></table>");
                  }

                  if (sess_activity_id != 0 && !getActivity.isChildlessActivity(sess_activity_id, con)) {
                      out.println("<p align=\"left\">&nbsp;&nbsp;Choose the locations this group lesson will take place:&nbsp;&nbsp;");
                      out.println("<br>&nbsp;&nbsp;(Note: <b>All</b> selected locations will be blocked off on the time sheets.)");
                      Common_Config.displayActivitySheetSelect(locations_csv, sess_activity_id, false, con, out);
                      out.println("</p>");
                  }
                  
                  out.println("<p align=\"left\">&nbsp;&nbsp;Maximum # of Members:&nbsp;&nbsp;");
                     out.println("<input type=\"text\" name=\"lgrp_max\" value=\"" +max+ "\" size=\"3\" maxlength=\"3\">");
                  out.println("</p>");

                  out.println("<p align=\"left\">&nbsp;&nbsp;Lesson Price (include $):&nbsp;&nbsp;");
                     out.println("<input type=\"text\" name=\"lgrp_cost\" value=\"" +cost+ "\" size=\"8\" maxlength=\"20\">");
                  out.println("</p>");

                  out.println("<p align=\"left\">&nbsp;&nbsp;Color to make these times in the Lesson Book:&nbsp;&nbsp;");
                  dateOpts.displayColorsAll(color, out);
                  
                  out.println("<br>");
                  out.println("&nbsp;&nbsp;Click here to see the available colors:&nbsp;");
                  out.println("<a href=\"/" +rev+ "/proshop_color.htm\" target=\"_blank\">View Colors</a>");
                  out.println("</p>");

                  //
                  //   Script will put any existing descript info in the textarea (value= doesn't work)
                  //
                  out.println("<input type=\"hidden\" name=\"olddescript\" value=\"" +descript+ "\">"); // hold descript for script

                  out.println("<p align=\"left\">&nbsp;&nbsp;Lesson Description (up to 254 characters):&nbsp;&nbsp;");
                  out.println("<textarea name=\"lgrp_desc\" value=\"\" id=\"descript\" cols=\"40\" rows=\"3\">");
                  out.println("</textarea>");
                  out.println("</p>");

                  out.println("<p align=\"center\">");
                  out.println("<input type=\"submit\" name=\"editlgrp2\" value=\"Update\">");    // to update record
                  out.println("</p>");
                  out.println("</font>");
                  out.println("</td>");
               out.println("</tr>");
               out.println("</form>");
            out.println("</table>");

            out.println("</td></tr></table>");                // end of main page table
            out.println("<font size=\"2\">");
            out.println("<form method=\"get\" action=\"Proshop_announce\">");
            out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");
            out.println("</center></font></body></html>");
            out.close();

         } else {

            nfError(req, out, lottery);            // LT not found
         }
         pstmt.close();

      }
      catch (Exception exc) {

         dbError(req, out, exc, lottery);
         return;
      }
   }
 }


 // ********************************************************************
 //  Process the 'Edit Group Lesson' SUBMIT request
 // ********************************************************************

 private void editLgrp2(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException {

   PreparedStatement pstmt = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String lname = "";
   String oldname = "";
   String cost = "";
   String color = "";
   String descript = "";
   String tmax = "";
   String error = "";
   String locations_csv = "";

   int max = 0;
   int id = 0;
   int smonth = 0;                // init integer date/time values
   int sday = 0;
   int syear = 0;
   int emonth = 0;
   int eday = 0;
   int eyear = 0;
   int s_hr = 0;
   int s_min = 0;
   int s_ampm = 0;
   int e_hr = 0;
   int e_min = 0;
   int e_ampm = 0;
   int stime = 0;
   int etime = 0;
   int lesson_id = 0;
   int lesson_id_neg = 0;

   int [] recurrArr = new int [8];        // index 0 = eo_week value, 1-7 = Sun-Sat values

   long sdate = 0;
   long edate = 0;

   boolean daysSelected = false;
   boolean isChildlessActivity = false;
   
   if (sess_activity_id != 0) {
       isChildlessActivity = getActivity.isChildlessActivity(sess_activity_id, con);
   }

   List<Integer> sheet_ids = new ArrayList<Integer>();
   Map<Integer, List<Integer>> sheet_id_map = new LinkedHashMap<Integer, List<Integer>>();

   try {
      id = Integer.parseInt(proid);          // convert the pro id
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   //
   //  Get the parameters
   //
   String s_month = req.getParameter("smonth");             //  month (00 - 12)
   String s_day = req.getParameter("sday");                 //  day (01 - 31)
   String s_year = req.getParameter("syear");               //  year
   String start_hr = req.getParameter("start_hr");         //  start hour (01 - 12)
   String start_min = req.getParameter("start_min");       //  start min (00 - 59)
   String start_ampm = req.getParameter("start_ampm");     //  AM/PM (00 or 12)
   String end_hr = req.getParameter("end_hr");
   String end_min = req.getParameter("end_min");
   String end_ampm = req.getParameter("end_ampm");

   if (sess_activity_id != 0) {

       emonth = Integer.parseInt(req.getParameter("emonth"));
       eday = Integer.parseInt(req.getParameter("eday"));
       eyear = Integer.parseInt(req.getParameter("eyear"));

       edate = eyear * 10000;       // create a date field from input values
       edate = edate + (emonth * 100);
       edate = edate + eday;             // date = yyyymmdd (for comparisons)

       if (req.getParameter("eo_week") != null && req.getParameter("eo_week").equalsIgnoreCase("everyother")) {
           recurrArr[0] = 1;
       } else {
           recurrArr[0] = 0;
       }

       for (int i=1; i<8; i++) {
           if (req.getParameter("day" + String.valueOf(i)) != null) {
               recurrArr[i] = 1;
               daysSelected = true;        // so we know if any days were selected
           } else {
               recurrArr[i] = 0;
           }
       }
   }

   if (req.getParameter("lesson_id") != null) {
       lesson_id = Integer.parseInt(req.getParameter("lesson_id"));
       lesson_id_neg = lesson_id * -1;
   }

   if (req.getParameter("lgrp_name") != null) lname = req.getParameter("lgrp_name");
   if (req.getParameter("oldname") != null) oldname = req.getParameter("oldname");
   if (req.getParameter("lgrp_cost") != null) cost = req.getParameter("lgrp_cost");
   if (req.getParameter("color") != null) color = req.getParameter("color");
   if (req.getParameter("lgrp_desc") != null) descript = req.getParameter("lgrp_desc");
   if (req.getParameter("lgrp_max") != null) {

      tmax = req.getParameter("lgrp_max");

      try {
         max = Integer.parseInt(tmax);
      }
      catch (NumberFormatException e) {
         max = 0;
      }
   }

   //
   // Convert the numeric string parameters to Int's
   //
   try {
      smonth = Integer.parseInt(s_month);
      sday = Integer.parseInt(s_day);
      syear = Integer.parseInt(s_year);
      s_hr = Integer.parseInt(start_hr);
      s_min = Integer.parseInt(start_min);
      s_ampm = Integer.parseInt(start_ampm);
      e_hr = Integer.parseInt(end_hr);
      e_min = Integer.parseInt(end_min);
      e_ampm = Integer.parseInt(end_ampm);
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }

   //
   //  Make sure user specified the minimum parameters
   //
   if (lname.equals( "" ) || lname == null || max == 0) {

      out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
      out.println("<BODY>");
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      out.println("<CENTER><BR>");
      out.println("<br><br>");
      out.println("<H3>Input Error</H3>");
      out.println("<BR>Sorry, you must specify at least a name and max number of members value.<BR>");
      out.println("<BR>Please specify both values and try again.<BR>");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   if (sess_activity_id != 0 && !isChildlessActivity) {
       
       //  Generate the locations string from the request
       locations_csv = Common_Config.buildLocationsString(req);

       if (locations_csv.equals("")) {

           out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
           out.println("<BODY>");
           SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
           out.println("<CENTER><BR>");
           out.println("<br><br>");
           out.println("<H3>Input Error</H3>");
           out.println("<BR>Sorry, you must specify at least one location where this lesson may take place.<BR>");
           out.println("<BR>Please specify locations and try again.<BR>");
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
   //  Check for quotes in name
   //
   boolean err = SystemUtils.scanQuote(lname);           // check for single quote

   if (err == true) {

      error = "Apostrophes (single quotes) cannot be part of the Name.";
      invData(req, out, lottery, error);
      return;
   }

   //
   //  Make sure the lesson grp (new name, if changed) doesn't already exist
   //
   if (!lname.equalsIgnoreCase( oldname )) {      // if name changed

      try {

         pstmt = con.prepareStatement (
                 "SELECT date FROM lessongrp5 WHERE WHERE proid = ? AND lname = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1, id);
         pstmt.setString(2, lname);
         rs = pstmt.executeQuery();      // execute the prepared pstmt

         if (rs.next()) {

            dupMem(req, out, lottery);
            pstmt.close();
            return;
         }
         pstmt.close();
      }
      catch (Exception exc) {
      }
   }

   //
   //  Get today's date
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   int year = cal.get(Calendar.YEAR);
   int month = cal.get(Calendar.MONTH);
   int day = cal.get(Calendar.DAY_OF_MONTH);
   month++;                           // month starts at zero

   long today = year * 10000;                        // create a today field of yyyymmdd (for today)
   today = today + (month * 100);
   today = today + day;                         // date = yyyymmdd (for comparisons)

   //
   //  adjust some values for the table
   //
   sdate = syear * 10000;       // create a date field from input values
   sdate = sdate + (smonth * 100);
   sdate = sdate + sday;             // date = yyyymmdd (for comparisons)

   if (edate == 0) edate = sdate;

   if (s_hr != 12) {                // _hr specified as 01 - 12 (_ampm = 00 or 12)

      s_hr = s_hr + s_ampm;         // convert to military time (12 is always Noon, or PM)
   }

   if (e_hr != 12) {                // ditto

      e_hr = e_hr + e_ampm;
   }

   stime = s_hr * 100;
   stime = stime + s_min;
   etime = e_hr * 100;
   etime = etime + e_min;

   //
   //  verify the date and time fields
   //
   if (sess_activity_id == 0 && sdate < today) {

      error = "The Start Date cannot be earlier than today.  Please correct the date.";
      invData(req, out, lottery, error);
      return;
   }
   if (stime > etime) {

      error = "The Start Time cannot be later than the End Time.  Please correct the Time range.";
      invData(req, out, lottery, error);
      return;
   }

   // If we're currently on an activity other than golf, check to ensure that these times are available on the time sheets
   // If not, reject the change.
   if (sess_activity_id != 0 && !isChildlessActivity) {

      boolean bookAllSheets = true;
      boolean autoSelectLocation = true;
      
      //  If no days were selected, send them back
      if (!daysSelected) {

          error = "At least one day of the week must be selected for this group lesson. Please correct the recurrence.";
          invData(req, out, lottery, error);
          return;
      }

      if (edate < sdate) {
          error = "The End Date cannot be earlier than the Start Date.  Please correct the date.";
          invData(req, out, lottery, error);
          return;
      }

      sheet_id_map = verifyLesson.checkActivityTimes(lesson_id_neg, sess_activity_id, locations_csv, sdate, edate, recurrArr, stime, etime, bookAllSheets, autoSelectLocation, con, out);
      
      if (sheet_id_map.get(0) != null) {
          sheet_ids = sheet_id_map.get(0);
      }

      // If no time slots returned, send the user back to the lesson book
      if (sheet_ids.size() == 0) {

          out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
          out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
          out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
          out.println("<CENTER><BR><BR><H3>No Times Available</H3>");
          out.println("<BR><BR>Sorry, no available time slots were found for this group lesson.<BR>");
          out.println("<BR>Please adjust the date and time values, or remove players from the time sheet occupying conflicting times.");
          out.println("<BR><BR>");
          out.println("<font size=\"2\">");
          out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
          out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
          out.println("</form></font>");
          out.println("</CENTER></BODY></HTML>");
          out.close();
          return;
      } else if (sheet_ids.size() > 0 && sheet_ids.get(0) != -99) {

          // All needed times were found.  Update activity_sheets and place this lesson_id (negative) in all associated sheet_ids
          // if index 0 was -99, that means no time slots in activity_sheets are needed for this lesson, skip this portion
          String inString = "";
          int count = 0;

          for (int k=0; k<sheet_ids.size(); k++) {
              inString += sheet_ids.get(k) + ",";
          }

          inString = inString.substring(0, inString.length() - 1);

          try {
              // First clear out existing lesson_ids for this lesson
              pstmt = con.prepareStatement("UPDATE activity_sheets SET lesson_id = 0 WHERE lesson_id = ?");
              pstmt.clearParameters();
              pstmt.setInt(1, lesson_id_neg);
              pstmt.executeUpdate();

              pstmt.close();

              // Now apply lesson_id to new set of times
              pstmt = con.prepareStatement("UPDATE activity_sheets SET lesson_id = ? WHERE sheet_id IN (" + inString + ")");
              pstmt.clearParameters();
              pstmt.setInt(1, lesson_id_neg);

              count = pstmt.executeUpdate();

              pstmt.close();

              if (count < inString.length()) {
                  out.println("<!-- Not enough slots blocked!!! -->");
              }


          } catch (Exception exc) {
              out.println("<!-- Error encountered: " + exc.getMessage() + " -->");
          }
      }

   }
   

   //
   //  Update the lesson grp
   //
   try {
      if (sess_activity_id != 0) {
          pstmt = con.prepareStatement (
                  "UPDATE lessongrp5 SET lname = ?, date = ?, edate = ?, stime = ?, etime = ?, " +
                  "max = ?, cost = ?, color = ?, descript = ?, locations = ?, " +
                  "eo_week = ?, sunday = ?, monday = ?, tuesday = ?, wednesday = ?, thursday = ?, friday = ?, saturday = ? " +
                  "WHERE lesson_id = ?");

          pstmt.clearParameters();        // clear the parms
          pstmt.setString(1, lname);
          pstmt.setLong(2, sdate);
          pstmt.setLong(3, edate);
          pstmt.setInt(4, stime);
          pstmt.setInt(5, etime);
          pstmt.setInt(6, max);
          pstmt.setString(7, cost);
          pstmt.setString(8, color);
          pstmt.setString(9, descript);
          pstmt.setString(10, locations_csv);

          int offsetVal = 11;
          for (int i=0; i<8; i++) {
              pstmt.setInt(i + offsetVal, recurrArr[i]);
          }

          int offsetVal2 = offsetVal + 8;

          pstmt.setInt(offsetVal2, lesson_id);

          pstmt.executeUpdate();     // execute the prepared stmt

          pstmt.close();

      } else {

          pstmt = con.prepareStatement (
                  "UPDATE lessongrp5 SET lname = ?, date = ?, stime = ?, etime = ?, " +
                  "max = ?, cost = ?, color = ?, descript = ? " +
                  "WHERE lesson_id = ?");

          pstmt.clearParameters();        // clear the parms
          pstmt.setString(1, lname);
          pstmt.setLong(2, sdate);
          pstmt.setInt(3, stime);
          pstmt.setInt(4, etime);
          pstmt.setInt(5, max);
          pstmt.setString(6, cost);
          pstmt.setString(7, color);
          pstmt.setString(8, descript);

          pstmt.setInt(9, lesson_id);

          pstmt.executeUpdate();     // execute the prepared stmt

          pstmt.close();
      }

      //
      //  Remove the lesson group name in all lesson times
      //
      pstmt = con.prepareStatement (
           "UPDATE lessonbook5 SET lgname = '' " +
           "WHERE proid = ? AND activity_id = ? AND lgname = ?");

      pstmt.clearParameters();               // clear the parms
      pstmt.setInt(1, id);
      pstmt.setInt(2, sess_activity_id);
      pstmt.setString(3, oldname);
      pstmt.executeUpdate();         // execute the prepared pstmt

      pstmt.close();

      //
      //  Set the new lesson group name in all lesson times
      //
      if (sess_activity_id != 0) {

          String recurrCheck = "";
          boolean eo_week = false;

           if (recurrArr[0] == 1) {    // eo_week check
              recurrCheck += "AND (MOD(DATE_FORMAT(date,'%U'), 2) = MOD(DATE_FORMAT(?,'%U'), 2)) ";
              eo_week = true;
          }

          String dayStr = "";
          String prefix = "AND (";

          if (recurrArr[1] == 1) {    // sunday check
              dayStr += prefix + "DATE_FORMAT(date, '%w') = '0' ";
              prefix = "OR ";
          }
          if (recurrArr[2] == 1) {    // monday check
              dayStr += prefix + "DATE_FORMAT(date, '%w') = '1' ";
              prefix = "OR ";
          }
          if (recurrArr[3] == 1) {    // tuesday check
              dayStr += prefix + "DATE_FORMAT(date, '%w') = '2' ";
              prefix = "OR ";
          }
          if (recurrArr[4] == 1) {    // wednesday check
              dayStr += prefix + "DATE_FORMAT(date, '%w') = '3' ";
              prefix = "OR ";
          }
          if (recurrArr[5] == 1) {    // thursday check
              dayStr += prefix + "DATE_FORMAT(date, '%w') = '4' ";
              prefix = "OR ";
          }
          if (recurrArr[6] == 1) {    // friday check
              dayStr += prefix + "DATE_FORMAT(date, '%w') = '5' ";
              prefix = "OR ";
          }
          if (recurrArr[7] == 1) {    // saturday check
              dayStr += prefix + "DATE_FORMAT(date, '%w') = '6'";
              prefix = "OR ";
          }
          if (!dayStr.equals("")) {
              dayStr += ") ";
          }

          recurrCheck += dayStr;

          // Instead of the normal update, for activities we need to apply to recurrance days as well
          pstmt = con.prepareStatement(
                  "UPDATE lessonbook5 SET lgname = ? " +
                  "WHERE proid = ? AND activity_id = ? AND date >= ? AND date <= ? " +
                  "AND time >= ? AND time <= ? " +
                  recurrCheck);
          pstmt.clearParameters();
          pstmt.setString(1, lname);
          pstmt.setInt(2, id);
          pstmt.setInt(3, sess_activity_id);
          pstmt.setLong(4, sdate);
          pstmt.setLong(5, edate);
          pstmt.setInt(6, stime);
          pstmt.setInt(7, etime);
          if (eo_week) pstmt.setLong(8, sdate);
          pstmt.executeUpdate();

          pstmt.close();

      } else {

          pstmt = con.prepareStatement (
               "UPDATE lessonbook5 SET lgname = ? " +
               "WHERE proid = ? AND activity_id = ? AND date = ? AND time >= ? AND time <= ?");

          pstmt.clearParameters();               // clear the parms
          pstmt.setString(1, lname);
          pstmt.setInt(2, id);
          pstmt.setInt(3, sess_activity_id);
          pstmt.setLong(4, sdate);
          pstmt.setInt(5, stime);
          pstmt.setInt(6, etime);
          pstmt.executeUpdate();         // execute the prepared pstmt

          pstmt.close();
      }

   }
   catch (Exception exc) {

      dbError(req, out, exc, lottery);
      return;
   }

   //
   // Database updated - inform user
   //
   out.println(SystemUtils.HeadTitle("Proshop Add Group Lesson"));
   out.println("<BODY>");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<CENTER><BR>");
   out.println("<BR><BR><H3>The Group Lesson Has Been Updated</H3>");
   out.println("<BR><BR>Thank you, the Group Lesson has been updated for the specified pro.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<a href=\"Proshop_lesson?editlgrp=yes&proid=" +id+ "\">Edit Another Group Lesson For This Pro</a>");
   out.println("<BR><BR>");
   out.println("<a href=\"Proshop_lesson?editlgrp=yes\">Edit Group Lesson For Other Pro</a>");
   out.println("<BR><BR>");
   out.println("<form method=\"get\" action=\"Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 // ********************************************************************
 //  Process the 'Delete Lesson grp' SUBMIT request
 // ********************************************************************

 private void deleteLgrp(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException {

   PreparedStatement pstmt = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String lname = "";

   int id = 0;
   int lesson_id = 0;

   try {
      id = Integer.parseInt(proid);
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   //
   //  Get the parameters
   //
   if (req.getParameter("lname") != null) lname = req.getParameter("lname");
   if (req.getParameter("lesson_id") != null) lesson_id = Integer.parseInt(req.getParameter("lesson_id"));

   //
   //  Check if this is a confirmation to delete
   //
   if (req.getParameter("conf") != null) {

      //
      //  Delete the rest
      //
      try {
         pstmt = con.prepareStatement (
                  "Delete FROM lessongrp5 WHERE lesson_id = ?");

         pstmt.clearParameters();               // clear the parms
         pstmt.setInt(1, lesson_id);
         pstmt.executeUpdate();         // execute the prepared pstmt

         pstmt.close();

         //
         //  Remove the lesson group name in all lesson times
         //
         pstmt = con.prepareStatement (
              "UPDATE lessonbook5 SET lgname = '' " +
              "WHERE proid = ? AND activity_id = ? AND lgname = ?");

         pstmt.clearParameters();               // clear the parms
         pstmt.setInt(1, id);
         pstmt.setInt(2, sess_activity_id);
         pstmt.setString(3, lname);
         pstmt.executeUpdate();         // execute the prepared pstmt

         pstmt.close();
           
         //
         //  Now remove all entries from the signup list for this group lesson
         //
         pstmt = con.prepareStatement (
                  "Delete FROM lgrpsignup5 WHERE lesson_id = ?");

         pstmt.clearParameters();               // clear the parms
         pstmt.setInt(1, lesson_id);
         pstmt.executeUpdate();         // execute the prepared pstmt

         pstmt.close();

         // If an activity other than golf, go through the activity sheets and remove this lesson group's lesson id from all timesheets
         if (sess_activity_id != 0) {
             int lesson_id_neg = lesson_id * -1;

             pstmt = con.prepareStatement("UPDATE activity_sheets SET lesson_id = 0 WHERE lesson_id = ?");
             pstmt.clearParameters();
             pstmt.setInt(1, lesson_id_neg);
             pstmt.executeUpdate();

             pstmt.close();
         }
      }
      catch (Exception exc) {

         dbError(req, out, exc, lottery);
         return;
      }

      //
      //  grp deleted - inform user
      //
      out.println(SystemUtils.HeadTitle("Delete Group Lesson Confirmation"));
      out.println("<BODY>");
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      out.println("<CENTER><BR>");
      out.println("<p>&nbsp;</p>");
      out.println("<BR><H3>Group Lesson Removed</H3><BR>");
      out.println("<BR><b>" + lname + "</b> has been removed from the Group Lesson database.");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<a href=\"Proshop_lesson?editlgrp=yes&proid=" +id+ "\">Edit Another Group Lesson For This Pro</a>");
      out.println("<BR><BR>");
      out.println("<a href=\"Proshop_lesson?editlgrp=yes\">Edit Group Lesson For Other Pro</a>");
      out.println("<BR><BR>");
      out.println("<form method=\"get\" action=\"Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();

   } else {

      //
      //  This is the first request for the delete - request a confirmation
      //
      out.println(SystemUtils.HeadTitle("Delete Group Lesson Confirmation"));
      out.println("<BODY>");
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      out.println("<CENTER><BR>");
      out.println("<p>&nbsp;</p>");
      out.println("<BR><H3>Remove Group Lesson Confirmation</H3><BR>");
      out.println("<BR>Please confirm that you wish to remove this Group Lesson: <b>" + lname + "</b><br>");

      out.println("<form action=\"Proshop_lesson\" method=\"post\" target=\"bot\">");
      out.println("<BR>ARE YOU SURE YOU WANT TO DELETE THIS Group Lesson?");

      out.println("<input type=\"hidden\" name=\"proid\" value =\"" +proid+ "\">");
      out.println("<input type=\"hidden\" name=\"lesson_id\" value=\"" +lesson_id+ "\">");
      out.println("<input type=\"hidden\" name=\"deletelgrp\" value =\"yes\">");
      out.println("<input type=\"hidden\" name=\"conf\" value =\"yes\">");
      out.println("<input type=\"hidden\" name=\"lname\" value =\"" +lname+ "\">");
      out.println("<br><br><input type=\"submit\" value=\"Yes - Delete\">");
      out.println("</form><font size=\"2\">");

      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"No - Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
   }
 }


 // ********************************************************************
 //  Process the 'Add Lesson Time' request (block of times for lessons)
 // ********************************************************************

 private void addLtime(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException { 


   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   //
   //  Get this year
   //
   Calendar cal = new GregorianCalendar();       // get todays date
   int thisYear = cal.get(Calendar.YEAR);        // get the year
   int thisMonth = cal.get(Calendar.MONTH);
   int thisDay = cal.get(Calendar.DAY_OF_MONTH);
   int year = 0;
   thisMonth++;        // adjust

   //
   //  Build the html page to request the lesson info
   //
   out.println(SystemUtils.HeadTitle2("Proshop - Add Lesson Time"));
   out.println("<script type=\"text/javascript\">");
   out.println("<!--");
   out.println("function cursor() { document.forms['f'].ltime_name.focus(); }");
   out.println("// -->");
   out.println("</script>");

   out.println("</head>");

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" onload=cursor()>");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<center>");

   out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\">");
   out.println("<tr><td align=\"center\">");
      out.println("<font color=\"#FFFFFF\" size=\"3\">");
      out.println("<b>Add Lesson Time</b><br>");
      out.println("</font><font color=\"#FFFFFF\" size=\"2\">");
      out.println("<br>Complete the following information for each Lesson Time to be added.<br>");
      out.println("This information will be used to build your lesson book.");
      out.println("<br><br>Click on 'ADD' to add the Lesson Times to your Lesson Book.");
   out.println("</font></td></tr></table><br>");

   out.println("<table border=\"1\" cols=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"6\">");
      out.println("<form action=\"Proshop_lesson\" method=\"post\" target=\"bot\" name=\"f\">");
      out.println("<input type=\"hidden\" name=\"proid\" value=\"" +proid+ "\">");
      out.println("<input type=\"hidden\" name=\"addltime2\" value=\"yes\">");
      out.println("<tr>");
         out.println("<td width=\"500\">");
         out.println("<font size=\"2\">");
         out.println("<p align=\"left\">&nbsp;&nbsp;Lesson Time Name:&nbsp;&nbsp;");
            out.println("<input type=\"text\" name=\"ltime_name\" size=\"30\" maxlength=\"40\">");
            out.println("&nbsp;&nbsp;&nbsp;* Must be unique");
         out.println("</p>");

         out.println("<p align=\"left\">&nbsp;&nbsp;Start Date:&nbsp;&nbsp;");
           out.println("   Month:&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"smonth\">");
           if (thisMonth == 1) {                                             // start with current month
              out.println("<option selected value=\"01\">JAN</option>");
           } else {
              out.println("<option value=\"01\">JAN</option>");
           }
           if (thisMonth == 2) {
              out.println("<option selected value=\"02\">FEB</option>");
           } else {
              out.println("<option value=\"02\">FEB</option>");
           }
           if (thisMonth == 3) {
              out.println("<option selected value=\"03\">MAR</option>");
           } else {
              out.println("<option value=\"03\">MAR</option>");
           }
           if (thisMonth == 4) {
              out.println("<option selected value=\"04\">APR</option>");
           } else {
              out.println("<option value=\"04\">APR</option>");
           }
           if (thisMonth == 5) {
              out.println("<option selected value=\"05\">MAY</option>");
           } else {
              out.println("<option value=\"05\">MAY</option>");
           }
           if (thisMonth == 6) {
              out.println("<option selected value=\"06\">JUN</option>");
           } else {
              out.println("<option value=\"06\">JUN</option>");
           }
           if (thisMonth == 7) {
              out.println("<option selected value=\"07\">JUL</option>");
           } else {
              out.println("<option value=\"07\">JUL</option>");
           }
           if (thisMonth == 8) {
              out.println("<option selected value=\"08\">AUG</option>");
           } else {
              out.println("<option value=\"08\">AUG</option>");
           }
           if (thisMonth == 9) {
              out.println("<option selected value=\"09\">SEP</option>");
           } else {
              out.println("<option value=\"09\">SEP</option>");
           }
           if (thisMonth == 10) {
              out.println("<option selected value=\"10\">OCT</option>");
           } else {
              out.println("<option value=\"10\">OCT</option>");
           }
           if (thisMonth == 11) {
              out.println("<option selected value=\"11\">NOV</option>");
           } else {
              out.println("<option value=\"11\">NOV</option>");
           }
           if (thisMonth == 12) {
              out.println("<option selected value=\"12\">DEC</option>");
           } else {
              out.println("<option value=\"12\">DEC</option>");
           }
           out.println("</select>");

           out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"sday\">");
           if (thisDay == 1) {                                                     // start with today
              out.println("<option selected selected value=\"01\">1</option>");
           } else {
              out.println("<option value=\"01\">1</option>");
           }
           if (thisDay == 2) {
              out.println("<option selected value=\"02\">2</option>");
           } else {
              out.println("<option value=\"02\">2</option>");
           }
           if (thisDay == 3) {
              out.println("<option selected value=\"03\">3</option>");
           } else {
              out.println("<option value=\"03\">3</option>");
           }
           if (thisDay == 4) {
              out.println("<option selected value=\"04\">4</option>");
           } else {
              out.println("<option value=\"04\">4</option>");
           }
           if (thisDay == 5) {
              out.println("<option selected value=\"05\">5</option>");
           } else {
              out.println("<option value=\"05\">5</option>");
           }
           if (thisDay == 6) {
              out.println("<option selected value=\"06\">6</option>");
           } else {
              out.println("<option value=\"06\">6</option>");
           }
           if (thisDay == 7) {
              out.println("<option selected value=\"07\">7</option>");
           } else {
              out.println("<option value=\"07\">7</option>");
           }
           if (thisDay == 8) {
              out.println("<option selected value=\"08\">8</option>");
           } else {
              out.println("<option value=\"08\">8</option>");
           }
           if (thisDay == 9) {
              out.println("<option selected value=\"09\">9</option>");
           } else {
              out.println("<option value=\"09\">9</option>");
           }
           if (thisDay == 10) {
              out.println("<option selected value=\"10\">10</option>");
           } else {
              out.println("<option value=\"10\">10</option>");
           }
           if (thisDay == 11) {
              out.println("<option selected value=\"11\">11</option>");
           } else {
              out.println("<option value=\"11\">11</option>");
           }
           if (thisDay == 12) {
              out.println("<option selected value=\"12\">12</option>");
           } else {
              out.println("<option value=\"12\">12</option>");
           }
           if (thisDay == 13) {
              out.println("<option selected value=\"13\">13</option>");
           } else {
              out.println("<option value=\"13\">13</option>");
           }
           if (thisDay == 14) {
              out.println("<option selected value=\"14\">14</option>");
           } else {
              out.println("<option value=\"14\">14</option>");
           }
           if (thisDay == 15) {
              out.println("<option selected value=\"15\">15</option>");
           } else {
              out.println("<option value=\"15\">15</option>");
           }
           if (thisDay == 16) {
              out.println("<option selected value=\"16\">16</option>");
           } else {
              out.println("<option value=\"16\">16</option>");
           }
           if (thisDay == 17) {
              out.println("<option selected value=\"17\">17</option>");
           } else {
              out.println("<option value=\"17\">17</option>");
           }
           if (thisDay == 18) {
              out.println("<option selected value=\"18\">18</option>");
           } else {
              out.println("<option value=\"18\">18</option>");
           }
           if (thisDay == 19) {
              out.println("<option selected value=\"19\">19</option>");
           } else {
              out.println("<option value=\"19\">19</option>");
           }
           if (thisDay == 20) {
              out.println("<option selected value=\"20\">20</option>");
           } else {
              out.println("<option value=\"20\">20</option>");
           }
           if (thisDay == 21) {
              out.println("<option selected value=\"21\">21</option>");
           } else {
              out.println("<option value=\"21\">21</option>");
           }
           if (thisDay == 22) {
              out.println("<option selected value=\"22\">22</option>");
           } else {
              out.println("<option value=\"22\">22</option>");
           }
           if (thisDay == 23) {
              out.println("<option selected value=\"23\">23</option>");
           } else {
              out.println("<option value=\"23\">23</option>");
           }
           if (thisDay == 24) {
              out.println("<option selected value=\"24\">24</option>");
           } else {
              out.println("<option value=\"24\">24</option>");
           }
           if (thisDay == 25) {
              out.println("<option selected value=\"25\">25</option>");
           } else {
              out.println("<option value=\"25\">25</option>");
           }
           if (thisDay == 26) {
              out.println("<option selected value=\"26\">26</option>");
           } else {
              out.println("<option value=\"26\">26</option>");
           }
           if (thisDay == 27) {
              out.println("<option selected value=\"27\">27</option>");
           } else {
              out.println("<option value=\"27\">27</option>");
           }
           if (thisDay == 28) {
              out.println("<option selected value=\"28\">28</option>");
           } else {
              out.println("<option value=\"28\">28</option>");
           }
           if (thisDay == 29) {
              out.println("<option selected value=\"29\">29</option>");
           } else {
              out.println("<option value=\"29\">29</option>");
           }
           if (thisDay == 30) {
              out.println("<option selected value=\"30\">30</option>");
           } else {
              out.println("<option value=\"30\">30</option>");
           }
           if (thisDay == 31) {
              out.println("<option selected value=\"31\">31</option>");
           } else {
              out.println("<option value=\"31\">31</option>");
           }
           out.println("</select>");

           year = thisYear; 
             
           out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"syear\">");
             out.println("<option value=\"" +year+ "\">" +year+ "</option>");
             year++;    // next year
             out.println("<option value=\"" +year+ "\">" +year+ "</option>");
             year++;    // next year
             out.println("<option value=\"" +year+ "\">" +year+ "</option>");
             year++;    // next year
             out.println("<option value=\"" +year+ "\">" +year+ "</option>");
             year++;    // next year
             out.println("<option value=\"" +year+ "\">" +year+ "</option>");
           out.println("</select>");
         out.println("</p>");
         out.println("<p align=\"left\">&nbsp;&nbsp;End Date:&nbsp;&nbsp;&nbsp;");
           out.println("Month:&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"emonth\">");
           if (thisMonth == 1) {                                             // start with current month
              out.println("<option selected value=\"01\">JAN</option>");
           } else {
              out.println("<option value=\"01\">JAN</option>");
           }
           if (thisMonth == 2) {
              out.println("<option selected value=\"02\">FEB</option>");
           } else {
              out.println("<option value=\"02\">FEB</option>");
           }
           if (thisMonth == 3) {
              out.println("<option selected value=\"03\">MAR</option>");
           } else {
              out.println("<option value=\"03\">MAR</option>");
           }
           if (thisMonth == 4) {
              out.println("<option selected value=\"04\">APR</option>");
           } else {
              out.println("<option value=\"04\">APR</option>");
           }
           if (thisMonth == 5) {
              out.println("<option selected value=\"05\">MAY</option>");
           } else {
              out.println("<option value=\"05\">MAY</option>");
           }
           if (thisMonth == 6) {
              out.println("<option selected value=\"06\">JUN</option>");
           } else {
              out.println("<option value=\"06\">JUN</option>");
           }
           if (thisMonth == 7) {
              out.println("<option selected value=\"07\">JUL</option>");
           } else {
              out.println("<option value=\"07\">JUL</option>");
           }
           if (thisMonth == 8) {
              out.println("<option selected value=\"08\">AUG</option>");
           } else {
              out.println("<option value=\"08\">AUG</option>");
           }
           if (thisMonth == 9) {
              out.println("<option selected value=\"09\">SEP</option>");
           } else {
              out.println("<option value=\"09\">SEP</option>");
           }
           if (thisMonth == 10) {
              out.println("<option selected value=\"10\">OCT</option>");
           } else {
              out.println("<option value=\"10\">OCT</option>");
           }
           if (thisMonth == 11) {
              out.println("<option selected value=\"11\">NOV</option>");
           } else {
              out.println("<option value=\"11\">NOV</option>");
           }
           if (thisMonth == 12) {
              out.println("<option selected value=\"12\">DEC</option>");
           } else {
              out.println("<option value=\"12\">DEC</option>");
           }
           out.println("</select>");

           out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"eday\">");
           if (thisDay == 1) {                                                     // start with today
              out.println("<option selected selected value=\"01\">1</option>");
           } else {
              out.println("<option value=\"01\">1</option>");
           }
           if (thisDay == 2) {
              out.println("<option selected value=\"02\">2</option>");
           } else {
              out.println("<option value=\"02\">2</option>");
           }
           if (thisDay == 3) {
              out.println("<option selected value=\"03\">3</option>");
           } else {
              out.println("<option value=\"03\">3</option>");
           }
           if (thisDay == 4) {
              out.println("<option selected value=\"04\">4</option>");
           } else {
              out.println("<option value=\"04\">4</option>");
           }
           if (thisDay == 5) {
              out.println("<option selected value=\"05\">5</option>");
           } else {
              out.println("<option value=\"05\">5</option>");
           }
           if (thisDay == 6) {
              out.println("<option selected value=\"06\">6</option>");
           } else {
              out.println("<option value=\"06\">6</option>");
           }
           if (thisDay == 7) {
              out.println("<option selected value=\"07\">7</option>");
           } else {
              out.println("<option value=\"07\">7</option>");
           }
           if (thisDay == 8) {
              out.println("<option selected value=\"08\">8</option>");
           } else {
              out.println("<option value=\"08\">8</option>");
           }
           if (thisDay == 9) {
              out.println("<option selected value=\"09\">9</option>");
           } else {
              out.println("<option value=\"09\">9</option>");
           }
           if (thisDay == 10) {
              out.println("<option selected value=\"10\">10</option>");
           } else {
              out.println("<option value=\"10\">10</option>");
           }
           if (thisDay == 11) {
              out.println("<option selected value=\"11\">11</option>");
           } else {
              out.println("<option value=\"11\">11</option>");
           }
           if (thisDay == 12) {
              out.println("<option selected value=\"12\">12</option>");
           } else {
              out.println("<option value=\"12\">12</option>");
           }
           if (thisDay == 13) {
              out.println("<option selected value=\"13\">13</option>");
           } else {
              out.println("<option value=\"13\">13</option>");
           }
           if (thisDay == 14) {
              out.println("<option selected value=\"14\">14</option>");
           } else {
              out.println("<option value=\"14\">14</option>");
           }
           if (thisDay == 15) {
              out.println("<option selected value=\"15\">15</option>");
           } else {
              out.println("<option value=\"15\">15</option>");
           }
           if (thisDay == 16) {
              out.println("<option selected value=\"16\">16</option>");
           } else {
              out.println("<option value=\"16\">16</option>");
           }
           if (thisDay == 17) {
              out.println("<option selected value=\"17\">17</option>");
           } else {
              out.println("<option value=\"17\">17</option>");
           }
           if (thisDay == 18) {
              out.println("<option selected value=\"18\">18</option>");
           } else {
              out.println("<option value=\"18\">18</option>");
           }
           if (thisDay == 19) {
              out.println("<option selected value=\"19\">19</option>");
           } else {
              out.println("<option value=\"19\">19</option>");
           }
           if (thisDay == 20) {
              out.println("<option selected value=\"20\">20</option>");
           } else {
              out.println("<option value=\"20\">20</option>");
           }
           if (thisDay == 21) {
              out.println("<option selected value=\"21\">21</option>");
           } else {
              out.println("<option value=\"21\">21</option>");
           }
           if (thisDay == 22) {
              out.println("<option selected value=\"22\">22</option>");
           } else {
              out.println("<option value=\"22\">22</option>");
           }
           if (thisDay == 23) {
              out.println("<option selected value=\"23\">23</option>");
           } else {
              out.println("<option value=\"23\">23</option>");
           }
           if (thisDay == 24) {
              out.println("<option selected value=\"24\">24</option>");
           } else {
              out.println("<option value=\"24\">24</option>");
           }
           if (thisDay == 25) {
              out.println("<option selected value=\"25\">25</option>");
           } else {
              out.println("<option value=\"25\">25</option>");
           }
           if (thisDay == 26) {
              out.println("<option selected value=\"26\">26</option>");
           } else {
              out.println("<option value=\"26\">26</option>");
           }
           if (thisDay == 27) {
              out.println("<option selected value=\"27\">27</option>");
           } else {
              out.println("<option value=\"27\">27</option>");
           }
           if (thisDay == 28) {
              out.println("<option selected value=\"28\">28</option>");
           } else {
              out.println("<option value=\"28\">28</option>");
           }
           if (thisDay == 29) {
              out.println("<option selected value=\"29\">29</option>");
           } else {
              out.println("<option value=\"29\">29</option>");
           }
           if (thisDay == 30) {
              out.println("<option selected value=\"30\">30</option>");
           } else {
              out.println("<option value=\"30\">30</option>");
           }
           if (thisDay == 31) {
              out.println("<option selected value=\"31\">31</option>");
           } else {
              out.println("<option value=\"31\">31</option>");
           }
           out.println("</select>");

           year = thisYear;

           out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"eyear\">");
             out.println("<option value=\"" +year+ "\">" +year+ "</option>");
             year++;    // next year
             out.println("<option value=\"" +year+ "\">" +year+ "</option>");
             year++;    // next year
             out.println("<option value=\"" +year+ "\">" +year+ "</option>");
             year++;    // next year
             out.println("<option value=\"" +year+ "\">" +year+ "</option>");
             year++;    // next year
             out.println("<option value=\"" +year+ "\">" +year+ "</option>");
             year++;    // next year
             out.println("<option value=\"" +year+ "\">" +year+ "</option>");
           out.println("</select>");
         out.println("</p>");

         out.println("<p align=\"left\">&nbsp;&nbsp;Start Time: &nbsp;&nbsp; hr &nbsp;");
           out.println("<select size=\"1\" name=\"start_hr\">");
             out.println("<option value=\"01\">01</option>");
             out.println("<option value=\"02\">02</option>");
             out.println("<option value=\"03\">03</option>");
             out.println("<option value=\"04\">04</option>");
             out.println("<option value=\"05\">05</option>");
             out.println("<option value=\"06\">06</option>");
             out.println("<option value=\"07\">07</option>");
             out.println("<option value=\"08\">08</option>");
             out.println("<option value=\"09\">09</option>");
             out.println("<option value=\"10\">10</option>");
             out.println("<option value=\"11\">11</option>");
             out.println("<option value=\"12\">12</option>");
           out.println("</select>");
           out.println("&nbsp; min &nbsp;");
           out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=\"00\" name=\"start_min\">");
           out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"start_ampm\">");
             out.println("<option value=\"00\">AM</option>");
             out.println("<option value=\"12\">PM</option>");
           out.println("</select>");
         out.println("</p>");
         out.println("<p align=\"left\">&nbsp;&nbsp;End Time: &nbsp;&nbsp;&nbsp;&nbsp; hr &nbsp;");
           out.println("<select size=\"1\" name=\"end_hr\">");
             out.println("<option value=\"01\">01</option>");
             out.println("<option value=\"02\">02</option>");
             out.println("<option value=\"03\">03</option>");
             out.println("<option value=\"04\">04</option>");
             out.println("<option value=\"05\">05</option>");
             out.println("<option value=\"06\">06</option>");
             out.println("<option value=\"07\">07</option>");
             out.println("<option value=\"08\">08</option>");
             out.println("<option value=\"09\">09</option>");
             out.println("<option value=\"10\">10</option>");
             out.println("<option value=\"11\">11</option>");
             out.println("<option value=\"12\">12</option>");
           out.println("</select>");
           out.println("&nbsp; min &nbsp;");
           out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=\"00\" name=\"end_min\">");
           out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"end_ampm\">");
             out.println("<option value=\"00\">AM</option>");
             out.println("<option value=\"12\">PM</option>");
           out.println("</select>");
         out.println("</p>");
         out.println("<p align=\"Left\">&nbsp;&nbsp;Recurrence (select all that apply):");
            out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<input type=\"checkbox\" name=\"mon\" value=\"1\">&nbsp;&nbsp;Every Monday");
            out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<input type=\"checkbox\" name=\"tue\" value=\"1\">&nbsp;&nbsp;Every Tuesday");
            out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<input type=\"checkbox\" name=\"wed\" value=\"1\">&nbsp;&nbsp;Every Wednesday");
            out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<input type=\"checkbox\" name=\"thu\" value=\"1\">&nbsp;&nbsp;Every Thursday");
            out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<input type=\"checkbox\" name=\"fri\" value=\"1\">&nbsp;&nbsp;Every Friday");
            out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<input type=\"checkbox\" name=\"sat\" value=\"1\">&nbsp;&nbsp;Every Saturday");
            out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<input type=\"checkbox\" name=\"sun\" value=\"1\">&nbsp;&nbsp;Every Sunday");
         out.println("</p>");

         out.println("<p align=\"left\">&nbsp;&nbsp;Segment Size (blocks of time in minutes):&nbsp;&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"fragment\">");
             out.println("<option value=\"5\">5</option>");
             out.println("<option value=\"10\">10</option>");
             out.println("<option value=\"15\">15</option>");
             out.println("<option value=\"20\">20</option>");
             out.println("<option value=\"25\">25</option>");
             out.println("<option value=\"30\">30</option>");
             out.println("<option value=\"35\">35</option>");
             out.println("<option value=\"40\">40</option>");
             out.println("<option value=\"45\">45</option>");
             out.println("<option value=\"50\">50</option>");
             out.println("<option value=\"55\">55</option>");
             out.println("<option value=\"60\">60</option>");
             out.println("<option value=\"65\">65</option>");
             out.println("<option value=\"70\">70</option>");
             out.println("<option value=\"75\">75</option>");
             out.println("<option value=\"80\">80</option>");
             out.println("<option value=\"85\">85</option>");
             out.println("<option value=\"90\">90</option>");
             out.println("<option value=\"95\">95</option>");
             out.println("<option value=\"100\">100</option>");
             out.println("<option value=\"105\">105</option>");
             out.println("<option value=\"110\">110</option>");
             out.println("<option value=\"115\">115</option>");
             out.println("<option value=\"120\">120</option>");
             out.println("<option value=\"125\">125</option>");
           out.println("</select>");
         out.println("</p>");

         out.println("<p align=\"left\">&nbsp;&nbsp;Color to make these times in the Lesson Book:&nbsp;&nbsp;");
         out.println("<select size=\"1\" name=\"color\">");
          out.println("<option selected value=\"Default\">Default (none)</option>");
             out.println("<option value=\"Antiquewhite\">Antique White</option>");
             out.println("<option value=\"Aqua\">Aqua</option>");
             out.println("<option value=\"Aquamarine\">Aquamarine</option>");
             out.println("<option value=\"Beige\">Beige</option>");
             out.println("<option value=\"Bisque\">Bisque</option>");
             out.println("<option value=\"Blanchedalmond\">Blanched Almond</option>");
             out.println("<option value=\"Blue\">Blue</option>");
             out.println("<option value=\"Bluevoilet\">Blueviolet</option>");
             out.println("<option value=\"Brown\">Brown</option>");
             out.println("<option value=\"Burlywood\">Burlywood</option>");
             out.println("<option value=\"Cadetblue\">Cadetblue</option>");
             out.println("<option value=\"Chartreuse\">Chartreuse</option>");
             out.println("<option value=\"Chocolate\">Chocolate</option>");
             out.println("<option value=\"Coral\">Coral</option>");
             out.println("<option value=\"Cornflowerblue\">Cornflowerblue</option>");
             out.println("<option value=\"Cornsilk\">Cornsilk</option>");
             out.println("<option value=\"Crimson\">Crimson</option>");
             out.println("<option value=\"Cyan\">Cyan</option>");
             out.println("<option value=\"Darkblue\">Darkblue</option>");
             out.println("<option value=\"Darkcyan\">Darkcyan</option>");
             out.println("<option value=\"Darkgoldenrod\">Darkgoldenrod</option>");
             out.println("<option value=\"Darkgray\">Darkgray</option>");
             out.println("<option value=\"Darkgreen\">Darkgreen</option>");
             out.println("<option value=\"Darkkhaki\">Darkkhaki</option>");
             out.println("<option value=\"Darkmagenta\">Darkmagenta</option>");
             out.println("<option value=\"Darkolivegreen\">Darkolivegreen</option>");
             out.println("<option value=\"Darkorange\">Darkorange</option>");
             out.println("<option value=\"Darkorchid\">Darkorchid</option>");
             out.println("<option value=\"Darkred\">Darkred</option>");
             out.println("<option value=\"Darksalmon\">Darksalmon</option>");
             out.println("<option value=\"Darkseagreen\">Darkseagreen</option>");
             out.println("<option value=\"Darkslateblue\">Darkslateblue</option>");
             out.println("<option value=\"Darkslategray\">Darkslategray</option>");
             out.println("<option value=\"Darkturquoise\">Darkturquoise</option>");
             out.println("<option value=\"Darkviolet\">Darkviolet</option>");
             out.println("<option value=\"Deeppink\">Deeppink</option>");
             out.println("<option value=\"Deepskyblue\">Deepskyblue</option>");
             out.println("<option value=\"Default\">Default (none)</option>");
             out.println("<option value=\"Dimgray\">Dimgray</option>");
             out.println("<option value=\"Dodgerblue\">Dodgerblue</option>");
             out.println("<option value=\"Firebrick\">Firebrick</option>");
             out.println("<option value=\"Forestgreen\">Forestgreen</option>");
             out.println("<option value=\"Fuchsia\">Fuchsia</option>");
             out.println("<option value=\"Gainsboro\">Gainsboro</option>");
             out.println("<option value=\"Gold\">Gold</option>");
             out.println("<option value=\"Goldenrod\">Goldenrod</option>");
             out.println("<option value=\"Gray\">Gray</option>");
             out.println("<option value=\"Green\">Green</option>");
             out.println("<option value=\"Greenyellow\">Greenyellow</option>");
             out.println("<option value=\"Hotpink\">Hotpink</option>");
             out.println("<option value=\"Indianred\">Indianred</option>");
             out.println("<option value=\"Indigo\">Indigo</option>");
             out.println("<option value=\"Ivory\">Ivory</option>");
             out.println("<option value=\"Khaki\">Khaki</option>");
             out.println("<option value=\"Lavender\">Lavender</option>");
             out.println("<option value=\"Lavenderblush\">Lavenderblush</option>");
             out.println("<option value=\"Lawngreen\">Lawngreen</option>");
             out.println("<option value=\"Lemonchiffon\">Lemonchiffon</option>");
             out.println("<option value=\"Lightblue\">Lightblue</option>");
             out.println("<option value=\"Lightcoral\">Lightcoral</option>");
             out.println("<option value=\"Lightgoldenrodyellow\">Lightgoldenrodyellow</option>");
             out.println("<option value=\"Lightgreen\">Lightgreen</option>");
             out.println("<option value=\"Lightgrey\">Lightgrey</option>");
             out.println("<option value=\"Lightpink\">Lightpink</option>");
             out.println("<option value=\"Lightsalmon\">Lightsalmon</option>");
             out.println("<option value=\"Lightseagreen\">Lightseagreen</option>");
             out.println("<option value=\"Lightskyblue\">Lightskyblue</option>");
             out.println("<option value=\"Lightslategray\">Lightslategray</option>");
             out.println("<option value=\"Lightsteelblue\">Lightsteelblue</option>");
             out.println("<option value=\"Lime\">Lime</option>");
             out.println("<option value=\"Limegreen\">Limegreen</option>");
             out.println("<option value=\"Linen\">Linen</option>");
             out.println("<option value=\"Magenta\">Magenta</option>");
             out.println("<option value=\"Mediumauqamarine\">Mediumauqamarine</option>");
             out.println("<option value=\"Mediumblue\">Mediumblue</option>");
             out.println("<option value=\"Mediumorchid\">Mediumorchid</option>");
             out.println("<option value=\"Mediumpurple\">Mediumpurple</option>");
             out.println("<option value=\"Mediumseagreen\">Mediumseagreen</option>");
             out.println("<option value=\"Mediumslateblue\">Mediumslateblue</option>");
             out.println("<option value=\"Mediumspringgreen\">Mediumspringgreen</option>");
             out.println("<option value=\"Mediumturquoise\">Mediumturquoise</option>");
             out.println("<option value=\"Mediumvioletred\">Mediumvioletred</option>");
             out.println("<option value=\"Mistyrose\">Mistyrose</option>");
             out.println("<option value=\"Moccasin\">Moccasin</option>");
             out.println("<option value=\"Navajowhite\">Navajowhite</option>");
             out.println("<option value=\"Navy\">Navy</option>");
             out.println("<option value=\"Oldlace\">Oldlace</option>");
             out.println("<option value=\"Olive\">Olive</option>");
             out.println("<option value=\"Olivedrab\">Olivedrab</option>");
             out.println("<option value=\"Orange\">Orange</option>");
             out.println("<option value=\"Orangered\">Orangered</option>");
             out.println("<option value=\"Orchid\">Orchid</option>");
             out.println("<option value=\"Palegoldenrod\">Palegoldenrod</option>");
             out.println("<option value=\"Palegreen\">Palegreen</option>");
             out.println("<option value=\"Paleturquoise\">Paleturquoise</option>");
             out.println("<option value=\"Palevioletred\">Palevioletred</option>");
             out.println("<option value=\"Papayawhip\">Papayawhip</option>");
             out.println("<option value=\"Peachpuff\">Peachpuff</option>");
             out.println("<option value=\"Peru\">Peru</option>");
             out.println("<option value=\"Pink\">Pink</option>");
             out.println("<option value=\"Plum\">Plum</option>");
             out.println("<option value=\"Powderblue\">Powderblue</option>");
             out.println("<option value=\"Purple\">Purple</option>");
             out.println("<option value=\"Red\">Red</option>");
             out.println("<option value=\"Rosybrown\">Rosybrown</option>");
             out.println("<option value=\"Royalblue\">Royalblue</option>");
             out.println("<option value=\"Saddlebrown\">Saddlebrown</option>");
             out.println("<option value=\"Salmon\">Salmon</option>");
             out.println("<option value=\"Sandybrown\">Sandybrown</option>");
             out.println("<option value=\"Seagreen\">Seagreen</option>");
             out.println("<option value=\"Sienna\">Sienna</option>");
             out.println("<option value=\"Silver\">Silver</option>");
             out.println("<option value=\"Skyblue\">Skyblue</option>");
             out.println("<option value=\"Slateblue\">Slateblue</option>");
             out.println("<option value=\"Slategray\">Slategray</option>");
             out.println("<option value=\"Springgreen\">Springgreen</option>");
             out.println("<option value=\"Steelblue\">Steelblue</option>");
             out.println("<option value=\"Tan\">Tan</option>");
             out.println("<option value=\"Teal\">Teal</option>");
             out.println("<option value=\"Thistle\">Thistle</option>");
             out.println("<option value=\"Tomato\">Tomato</option>");
             out.println("<option value=\"Turquoise\">Turquoise</option>");
             out.println("<option value=\"Violet\">Violet</option>");
             out.println("<option value=\"Wheat\">Wheat</option>");
             out.println("<option value=\"Yellow\">Yellow</option>");
             out.println("<option value=\"YellowGreen\">YellowGreen</option>");
         out.println("</select>");
         out.println("<br>");
         out.println("&nbsp;&nbsp;Click here to see the available colors:&nbsp;");
         out.println("<a href=\"/" +rev+ "/proshop_color.htm\" target=\"_blank\">View Colors</a>");
         out.println("</p>");

         out.println("<p align=\"center\">");
           out.println("<input type=\"submit\" value=\"ADD\">");
           out.println("");
         out.println("</p>");
         out.println("</font>");
         out.println("</td>");
      out.println("</tr>");
      out.println("</form>");
   out.println("</table>");
   out.println("</font>");

   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</center>");
   out.println("</body>");
   out.println("</html>");
   out.close();
 }


 // ********************************************************************
 //  Process the 'Add Lesson Time' SUBMIT request
 // ********************************************************************

 private void addLtime2(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException {

   PreparedStatement pstmt = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String ltname = "";
   String frags = "";
   String color = "";
   String error = "";

   int id = 0;
   int mon = 0;
   int tue = 0;
   int wed = 0;
   int thu = 0;
   int fri = 0;
   int sat = 0;
   int sun = 0;
   int fragment = 0;
   int smonth = 0;                // init integer date/time values
   int sday = 0;
   int syear = 0;
   int emonth = 0;
   int eday = 0;
   int eyear = 0;
   int s_hr = 0;
   int s_min = 0;
   int s_ampm = 0;
   int e_hr = 0;
   int e_min = 0;
   int e_ampm = 0;

   try {
      id = Integer.parseInt(proid);          // convert the pro id
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   //
   //  Get the parameters
   //
   String s_month = req.getParameter("smonth");             //  month (00 - 12)
   String s_day = req.getParameter("sday");                 //  day (01 - 31)
   String s_year = req.getParameter("syear");               //  year 
   String start_hr = req.getParameter("start_hr");         //  start hour (01 - 12)
   String start_min = req.getParameter("start_min");       //  start min (00 - 59)
   String start_ampm = req.getParameter("start_ampm");     //  AM/PM (00 or 12)
   String e_month = req.getParameter("emonth");
   String e_day = req.getParameter("eday");
   String e_year = req.getParameter("eyear");
   String end_hr = req.getParameter("end_hr");
   String end_min = req.getParameter("end_min");
   String end_ampm = req.getParameter("end_ampm");
   if (req.getParameter("ltime_name") != null) {

      ltname = req.getParameter("ltime_name");
   }
   if (req.getParameter("fragment") != null) {

      frags = req.getParameter("fragment");

      try {
         fragment = Integer.parseInt(frags);
      }
      catch (NumberFormatException e) {
         fragment = 0;
      }
   }
   if (req.getParameter("color") != null) {

      color = req.getParameter("color");
   }
   if (req.getParameter("mon") != null) {    

      mon = 1;           // recurr check box selected
   }
   if (req.getParameter("tue") != null) {

      tue = 1;           // recurr check box selected
   }
   if (req.getParameter("wed") != null) {

      wed = 1;           // recurr check box selected
   }
   if (req.getParameter("thu") != null) {

      thu = 1;           // recurr check box selected
   }
   if (req.getParameter("fri") != null) {

      fri = 1;           // recurr check box selected
   }
   if (req.getParameter("sat") != null) {

      sat = 1;           // recurr check box selected
   }
   if (req.getParameter("sun") != null) {

      sun = 1;           // recurr check box selected
   }

   //
   // Convert the numeric string parameters to Int's
   //
   try {
      smonth = Integer.parseInt(s_month);
      sday = Integer.parseInt(s_day);
      syear = Integer.parseInt(s_year);
      s_hr = Integer.parseInt(start_hr);
      s_min = Integer.parseInt(start_min);
      s_ampm = Integer.parseInt(start_ampm);
      emonth = Integer.parseInt(e_month);
      eday = Integer.parseInt(e_day);
      eyear = Integer.parseInt(e_year);
      e_hr = Integer.parseInt(end_hr);
      e_min = Integer.parseInt(end_min);
      e_ampm = Integer.parseInt(end_ampm);
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }

   //
   //  Make sure user specified the minimum parameters
   //
   if (ltname.equals( "" ) || ltname == null) {

      error = "You must specify a name for this Lesson Time.";
      invData(req, out, lottery, error);
      return;
   }

   //
   //  Check for quotes in name
   //
   boolean err = SystemUtils.scanQuote(ltname);           // check for single quote

   if (err == true) {

      error = "Apostrophes (single quotes) cannot be part of the Name.";
      invData(req, out, lottery, error);
      return;
   }

   //
   //  Check if lesson time already exists 
   //
   try {

      pstmt = con.prepareStatement (
              "SELECT proid FROM lessontime5 WHERE proid = ? AND activity_id = ? AND lname = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1, id);
      pstmt.setInt(2, sess_activity_id);
      pstmt.setString(3, ltname);
      rs = pstmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         dupMem(req, out, lottery);
         pstmt.close();
         return;
      }
      pstmt.close();
   }
   catch (Exception ignored) {
   }

   //
   //  adjust some values for the table
   //
   long sdate = syear * 10000;       // create a date field from input values
   sdate = sdate + (smonth * 100);
   sdate = sdate + sday;             // date = yyyymmdd (for comparisons)

   long edate = eyear * 10000;       // ditto
   edate = edate + (emonth * 100);
   edate = edate + eday;

   if (s_hr != 12) {                // _hr specified as 01 - 12 (_ampm = 00 or 12)

      s_hr = s_hr + s_ampm;         // convert to military time (12 is always Noon, or PM)
   }

   if (e_hr != 12) {                // ditto

      e_hr = e_hr + e_ampm;
   }

   int stime = (s_hr * 100) + s_min;
   int etime = (e_hr * 100) + e_min;

   //
   //  verify the date and time fields
   //
   if (sdate > edate) {

      error = "The Start Date cannot be later than the End Date.  Please correct the date range.";
      invData(req, out, lottery, error);
      return;
   }
   if (stime > etime) {

      error = "The Start Time cannot be later than the End Time.  Please correct the Time range.";
      invData(req, out, lottery, error);
      return;
   }

   //
   //  Add the lesson time
   //
   try {
      pstmt = con.prepareStatement (
        "INSERT INTO lessontime5 (proid, activity_id, lname, sdate, stime, edate, etime, " +
        "mon, tue, wed, thu, fri, sat, sun, color, fragment) " +
        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1, id);
      pstmt.setInt(2, sess_activity_id);
      pstmt.setString(3, ltname);       // put the parm in pstmt
      pstmt.setLong(4, sdate);
      pstmt.setInt(5, stime);
      pstmt.setLong(6, edate);
      pstmt.setInt(7, etime);
      pstmt.setInt(8, mon);
      pstmt.setInt(9, tue);
      pstmt.setInt(10, wed);
      pstmt.setInt(11, thu);
      pstmt.setInt(12, fri);
      pstmt.setInt(13, sat);
      pstmt.setInt(14, sun);
      pstmt.setString(15, color);
      pstmt.setInt(16, fragment);
      pstmt.executeUpdate();          // execute the prepared stmt

      pstmt.close();   // close the stmt

   }
   catch (Exception exc) {

      dbError(req, out, exc, lottery);
      return;
   }

   //
   // Database updated - inform user
   //
   out.println(SystemUtils.HeadTitle("Proshop Add Lesson Time"));
   out.println("<BODY>");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<CENTER><BR>");
   out.println("<BR><BR><H3>The Lesson Time Has Been Added</H3>");
   out.println("<BR><BR>Thank you, the lesson time has been added for the specified pro.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<a href=\"Proshop_lesson?addltime=yes&proid=" +id+ "\">Add Another Lesson Time For This Pro</a>");
   out.println("<BR><BR>");
   out.println("<a href=\"Proshop_lesson?addltime=yes\">Add Lesson Time For Other Pro</a>");
   out.println("<BR><BR>");
   out.println("<a href=\"Proshop_lesson?editltime=yes&proid=" +id+ "\">View Lesson Times For This Pro</a>");
   out.println("<BR><BR>");
   out.println("<form method=\"get\" action=\"Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
     
   //
   //  Now go build the entries in the Lesson Book
   //
   addLtimeSub(proid, sess_activity_id, ltname, con);
     
 }


 // ********************************************************************
 //  Process the 'Edit Lesson Time' request
 // ********************************************************************

 private void editLtime(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException {

   PreparedStatement pstmt = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String ltname = "";
   String frags = "";
   String color = "";
   String error = "";
   String s_ampm = "AM";
   String e_ampm = "AM";

   int id = 0;
   int mon = 0;
   int tue = 0;
   int wed = 0;
   int thu = 0;
   int fri = 0;
   int sat = 0;
   int sun = 0;
   int fragment = 0;
   int smonth = 0;                // init integer date/time values
   int sday = 0;
   int syear = 0;
   int emonth = 0;
   int eday = 0;
   int eyear = 0;
   int s_hr = 0;
   int s_min = 0;
   int e_hr = 0;
   int e_min = 0;
   int stime = 0;
   int etime = 0;

   long sdate = 0;
   long edate = 0;
   long mm = 0;
   long dd = 0;
   long yy = 0;

   boolean b = false;
   boolean hit = false;

   try {
      id = Integer.parseInt(proid);
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   //
   //  Get this year
   //
   Calendar cal = new GregorianCalendar();       // get todays date
   int thisYear = cal.get(Calendar.YEAR);        // get the year
   int year = 0;

   if (req.getParameter("editLTM") == null) {       // if not a specific edit request (if this is the 1st call)

      //
      //  Build the HTML page to display the existing Lesson Times
      //
      out.println(SystemUtils.HeadTitle("Proshop Lesson Times Page"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr><td align=\"center\">");

         out.println("<table cellpadding=\"5\" bgcolor=\"#336633\" border=\"0\" align=\"center\">");
         out.println("<tr><td align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"3\">");
         out.println("<b>Edit Lesson Times</b><br>");
         out.println("</font>");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<br>To update a Lesson Time, click on the Update button within that lesson time.");
         out.println("<br>To remove a Lesson Time, click on the Delete button within that lesson time.");
         out.println("<br></td></tr></table>");
         out.println("<br>");

         out.println("<table border=\"2\" cellpadding=\"5\" bgcolor=\"#F5F5DC\">");
         out.println("<tr bgcolor=\"#8B8970\">");
         out.println("<td colspan=\"9\" align=\"center\">");
         out.println("<font size=\"2\">");
         out.println("<p align=\"center\"><b>Active Lesson Times</b></p>");
         out.println("</font></td></tr>");
         out.println("<tr bgcolor=\"#8B8970\"><td align=\"center\">");
         out.println("<font size=\"2\"><p><b>Lesson Time Name</b></p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p><b>Start Date</b></p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p><b>End Date</b></p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p><b>Start Time</b></p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p><b>End Time</b></p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p><b>Segment Size</b></p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p><b>Recurrence</b></p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p><b>Color</b></p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>&nbsp;</p>");      // empty for select buttons
         out.println("</font></td></tr>");

      //
      //  Get the lesson time records
      //
      try {

         pstmt = con.prepareStatement (
                 "SELECT * FROM lessontime5 WHERE proid = ? AND activity_id = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1,id);
         pstmt.setInt(2,sess_activity_id);
         rs = pstmt.executeQuery();      // execute the prepared pstmt

         while (rs.next()) {

            b = true;                     // indicate lesson times exist

            ltname = rs.getString("lname");
            sdate = rs.getLong("sdate");
            stime = rs.getInt("stime");
            edate = rs.getLong("edate");
            etime = rs.getInt("etime");
            mon = rs.getInt("mon");
            tue = rs.getInt("tue");
            wed = rs.getInt("wed");
            thu = rs.getInt("thu");
            fri = rs.getInt("fri");
            sat = rs.getInt("sat");
            sun = rs.getInt("sun");
            color = rs.getString("color");
            fragment = rs.getInt("fragment");

            //
            //  adjust some values for the table
            //
            yy = sdate / 10000;                             // get year
            mm = (sdate - (yy * 10000)) / 100;              // get month
            dd = (sdate - (yy * 10000)) - (mm * 100);       // get day
              
            smonth = (int)mm;
            sday = (int)dd;
            syear = (int)yy;

            yy = edate / 10000;                             // get year
            mm = (edate - (yy * 10000)) / 100;              // get month
            dd = (edate - (yy * 10000)) - (mm * 100);       // get day

            emonth = (int)mm;
            eday = (int)dd;
            eyear = (int)yy;

            s_ampm = "AM";         // defaults
            e_ampm = "AM";

            s_hr = stime / 100;                 
            s_min = stime - (s_hr * 100);
              
            e_hr = etime / 100;
            e_min = etime - (e_hr * 100);

            if (s_hr == 12) {
              
               s_ampm = "PM";
                 
            } else { 
              
               if (s_hr > 12) {
                 
                  s_hr = s_hr - 12;          // adjust
                  s_ampm = "PM";
               }
            }
  
            if (e_hr == 12) {

               e_ampm = "PM";

            } else {

               if (e_hr > 12) {

                  e_hr = e_hr - 12;          // adjust
                  e_ampm = "PM";
               }
            }
            
            if (!color.equalsIgnoreCase( "default" )) {
               out.println("<tr bgcolor=\"" +color+ "\">");
            } else {
               out.println("<tr>");
            }
            out.println("<form action=\"Proshop_lesson\" method=\"post\" target=\"bot\" name=\"f\">");
            out.println("<input type=\"hidden\" name=\"proid\" value=\"" +proid+ "\">");
            out.println("<input type=\"hidden\" name=\"ltname\" value=\"" +ltname+ "\">");
               out.println("<td align=\"center\">");
                 out.println("<font size=\"2\">");
                 out.println(ltname);
                 out.println("</font>");
               out.println("</td>");

               out.println("<td align=\"center\">");
                 out.println("<font size=\"2\">");
                 out.println(smonth+ "/" +sday+ "/" +syear);
                 out.println("</font>");
               out.println("</td>");

               out.println("<td align=\"center\">");
                 out.println("<font size=\"2\">");
                 out.println(emonth+ "/" +eday+ "/" +eyear);
                 out.println("</font>");
               out.println("</td>");

               out.println("<td align=\"center\">");
                 out.println("<font size=\"2\">");
                 if (s_min < 10) {
                    out.println(s_hr+ ":0" +s_min+ " " +s_ampm);
                 } else {
                    out.println(s_hr+ ":" +s_min+ " " +s_ampm);
                 }
                 out.println("</font>");
               out.println("</td>");

               out.println("<td align=\"center\">");
                 out.println("<font size=\"2\">");
                 if (e_min < 10) {
                    out.println(e_hr+ ":0" +e_min+ " " +e_ampm);
                 } else {
                    out.println(e_hr+ ":" +e_min+ " " +e_ampm);
                 }
                 out.println("</font>");
               out.println("</td>");

               out.println("<td align=\"center\">");
                 out.println("<font size=\"2\">");
                 out.println(fragment);
                 out.println("</font>");
               out.println("</td>");

               hit = false;         // init

               out.println("<td>");
                 out.println("<font size=\"2\">");
                 if (mon == 1) {
                    out.println("Every Monday");
                    hit = true;                
                 }
                 if (tue == 1) {
                    if (hit == true) {
                       out.println("<br>Every Tuesday");
                    } else {
                       out.println("Every Tuesday");
                       hit = true;
                    }
                 }
                 if (wed == 1) {
                    if (hit == true) {
                       out.println("<br>Every Wednesday");
                    } else {
                       out.println("Every Wednesday");
                       hit = true;
                    }
                 }
                 if (thu == 1) {
                    if (hit == true) {
                       out.println("<br>Every Thursday");
                    } else {
                       out.println("Every Thursday");
                       hit = true;
                    }
                 }
                 if (fri == 1) {
                    if (hit == true) {
                       out.println("<br>Every Friday");
                    } else {
                       out.println("Every Friday");
                       hit = true;
                    }
                 }
                 if (sat == 1) {
                    if (hit == true) {
                       out.println("<br>Every Saturday");
                    } else {
                       out.println("Every Saturday");
                       hit = true;
                    }
                 }
                 if (sun == 1) {
                    if (hit == true) {
                       out.println("<br>Every Sunday");
                    } else {
                       out.println("Every Sunday");
                       hit = true;
                    }
                 }
                 out.println("</font>");
               out.println("</td>");

                if (!color.equals( "Default" )) {
                  out.println("<td align=\"center\" bgcolor=\"" +color+ "\">");
                } else {
                  out.println("<td align=\"center\">");
                }
                 out.println("<font size=\"2\">");
                 out.println(color);
                 out.println("</font>");
               out.println("</td>");

               out.println("<td align=\"center\">");
                 out.println("<font size=\"2\">");
                   out.println("<p align=\"center\">");
                   out.println("<input type=\"submit\" name=\"editLTM\" value=\"Update\">");    // to update record
                   out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
                   out.println("<input type=\"submit\" name=\"deleteltime\" value=\"Delete\">");   // to delete the record
                   out.println("</p>");
                 out.println("</font>");
               out.println("</td>");

            out.println("</form>");
            out.println("</tr>");

         }
         pstmt.close();

         if (!b) {

            out.println("</font><font size=\"2\"><p>No Lesson Times Currently Exist</p>");
         }
      }
      catch (Exception exc) {

         dbError(req, out, exc, lottery);
         return;
      }

      //
      //  End of HTML page
      //
      out.println("</table></font>");                   // end of lesson times table
      out.println("</td></tr></table>");                // end of main page table
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</center></font></body></html>");
      out.close();

   } else {

      //******************************************************************
      //  Request is to edit a specific Lesson Time
      //******************************************************************
      //
      if (req.getParameter("ltname") != null) {

         ltname = req.getParameter("ltname");
      }

      //
      //  Get the lesson time records
      //
      try {

         pstmt = con.prepareStatement (
                 "SELECT * FROM lessontime5 WHERE proid = ? AND activity_id = ? AND lname = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1,id);
         pstmt.setInt(2,sess_activity_id);
         pstmt.setString(3,ltname);
         rs = pstmt.executeQuery();      // execute the prepared pstmt

         if (rs.next()) {

            sdate = rs.getLong("sdate");
            stime = rs.getInt("stime");
            edate = rs.getLong("edate");
            etime = rs.getInt("etime");
            mon = rs.getInt("mon");
            tue = rs.getInt("tue");
            wed = rs.getInt("wed");
            thu = rs.getInt("thu");
            fri = rs.getInt("fri");
            sat = rs.getInt("sat");
            sun = rs.getInt("sun");
            color = rs.getString("color");
            fragment = rs.getInt("fragment");

            //
            //  adjust some values for the table
            //
            yy = sdate / 10000;                             // get year
            mm = (sdate - (yy * 10000)) / 100;              // get month
            dd = (sdate - (yy * 10000)) - (mm * 100);       // get day

            smonth = (int)mm;
            sday = (int)dd;
            syear = (int)yy;

            yy = edate / 10000;                             // get year
            mm = (edate - (yy * 10000)) / 100;              // get month
            dd = (edate - (yy * 10000)) - (mm * 100);       // get day

            emonth = (int)mm;
            eday = (int)dd;
            eyear = (int)yy;

            s_hr = stime / 100;
            s_min = stime - (s_hr * 100);

            e_hr = etime / 100;
            e_min = etime - (e_hr * 100);

            if (s_hr == 12) {

               s_ampm = "PM";

            } else {

               if (s_hr > 12) {

                  s_hr = s_hr - 12;          // adjust
                  s_ampm = "PM";
               }
            }

            if (e_hr == 12) {

               e_ampm = "PM";

            } else {

               if (e_hr > 12) {

                  e_hr = e_hr - 12;          // adjust
                  e_ampm = "PM";
               }
            }

            //
            //  Build the HTML page to display the selected Lesson Time
            //
            out.println(SystemUtils.HeadTitle("Proshop Lesson Times Page"));
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
            SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
            out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

            out.println("<table border=\"0\" align=\"center\">");
            out.println("<tr><td align=\"center\">");

            out.println("<table cellpadding=\"5\" bgcolor=\"#336633\" border=\"0\" align=\"center\">");
            out.println("<tr><td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"3\">");
            out.println("<b>Edit Lesson Time</b><br>");
            out.println("</font>");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<br>To update a Lesson Time, make the desired changes and click on the Update button.");
            out.println("<br></td></tr></table>");
            out.println("<br><br>");

            out.println("<table border=\"1\" cols=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"6\">");
               out.println("<form action=\"Proshop_lesson\" method=\"post\" target=\"bot\" name=\"f\">");
               out.println("<input type=\"hidden\" name=\"proid\" value=\"" +proid+ "\">");
               out.println("<input type=\"hidden\" name=\"oldname\" value=\"" +ltname+ "\">");
               out.println("<input type=\"hidden\" name=\"oldsdate\" value=\"" +sdate+ "\">");
               out.println("<input type=\"hidden\" name=\"oldstime\" value=\"" +stime+ "\">");
               out.println("<input type=\"hidden\" name=\"oldedate\" value=\"" +edate+ "\">");
               out.println("<input type=\"hidden\" name=\"oldetime\" value=\"" +etime+ "\">");
               out.println("<input type=\"hidden\" name=\"oldmon\" value=\"" +mon+ "\">");
               out.println("<input type=\"hidden\" name=\"oldtue\" value=\"" +tue+ "\">");
               out.println("<input type=\"hidden\" name=\"oldwed\" value=\"" +wed+ "\">");
               out.println("<input type=\"hidden\" name=\"oldthu\" value=\"" +thu+ "\">");
               out.println("<input type=\"hidden\" name=\"oldfri\" value=\"" +fri+ "\">");
               out.println("<input type=\"hidden\" name=\"oldsat\" value=\"" +sat+ "\">");
               out.println("<input type=\"hidden\" name=\"oldsun\" value=\"" +sun+ "\">");
               out.println("<input type=\"hidden\" name=\"oldcolor\" value=\"" +color+ "\">");
               out.println("<input type=\"hidden\" name=\"oldfragment\" value=\"" +fragment+ "\">");
               out.println("<tr>");
                  out.println("<td width=\"500\">");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"left\">&nbsp;&nbsp;Lesson Time Name:&nbsp;&nbsp;");
                     out.println("<input type=\"text\" name=\"ltime_name\" value=\"" +ltname+ "\" size=\"30\" maxlength=\"40\">");
                     out.println("&nbsp;&nbsp;&nbsp;* Must be unique");
                  out.println("</p>");

                  out.println("<p align=\"left\">&nbsp;&nbsp;Start Date:&nbsp;&nbsp;");
                    out.println("   Month:&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"smonth\">");
                    if (smonth == 1) {
                       out.println("<option selected value=\"01\">JAN</option>");
                    } else {
                       out.println("<option value=\"01\">JAN</option>");
                    }
                    if (smonth == 2) {
                       out.println("<option selected value=\"02\">FEB</option>");
                    } else {
                       out.println("<option value=\"02\">FEB</option>");
                    }
                    if (smonth == 3) {
                       out.println("<option selected value=\"03\">MAR</option>");
                    } else {
                       out.println("<option value=\"03\">MAR</option>");
                    }
                    if (smonth == 4) {
                       out.println("<option selected value=\"04\">APR</option>");
                    } else {
                       out.println("<option value=\"04\">APR</option>");
                    }
                    if (smonth == 5) {
                       out.println("<option selected value=\"05\">MAY</option>");
                    } else {
                       out.println("<option value=\"05\">MAY</option>");
                    }
                    if (smonth == 6) {
                       out.println("<option selected value=\"06\">JUN</option>");
                    } else {
                       out.println("<option value=\"06\">JUN</option>");
                    }
                    if (smonth == 7) {
                       out.println("<option selected value=\"07\">JUL</option>");
                    } else {
                       out.println("<option value=\"07\">JUL</option>");
                    }
                    if (smonth == 8) {
                       out.println("<option selected value=\"08\">AUG</option>");
                    } else {
                       out.println("<option value=\"08\">AUG</option>");
                    }
                    if (smonth == 9) {
                       out.println("<option selected value=\"09\">SEP</option>");
                    } else {
                       out.println("<option value=\"09\">SEP</option>");
                    }
                    if (smonth == 10) {
                       out.println("<option selected value=\"10\">OCT</option>");
                    } else {
                       out.println("<option value=\"10\">OCT</option>");
                    }
                    if (smonth == 11) {
                       out.println("<option selected value=\"11\">NOV</option>");
                    } else {
                       out.println("<option value=\"11\">NOV</option>");
                    }
                    if (smonth == 12) {
                       out.println("<option selected value=\"12\">DEC</option>");
                    } else {
                       out.println("<option value=\"12\">DEC</option>");
                    }
                    out.println("</select>");

                    out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"sday\">");
                    if (sday == 1) {
                       out.println("<option selected selected value=\"01\">1</option>");
                    } else {
                       out.println("<option value=\"01\">1</option>");
                    }
                    if (sday == 2) {
                       out.println("<option selected value=\"02\">2</option>");
                    } else {
                       out.println("<option value=\"02\">2</option>");
                    }
                    if (sday == 3) {
                       out.println("<option selected value=\"03\">3</option>");
                    } else {
                       out.println("<option value=\"03\">3</option>");
                    }
                    if (sday == 4) {
                       out.println("<option selected value=\"04\">4</option>");
                    } else {
                       out.println("<option value=\"04\">4</option>");
                    }
                    if (sday == 5) {
                       out.println("<option selected value=\"05\">5</option>");
                    } else {
                       out.println("<option value=\"05\">5</option>");
                    }
                    if (sday == 6) {
                       out.println("<option selected value=\"06\">6</option>");
                    } else {
                       out.println("<option value=\"06\">6</option>");
                    }
                    if (sday == 7) {
                       out.println("<option selected value=\"07\">7</option>");
                    } else {
                       out.println("<option value=\"07\">7</option>");
                    }
                    if (sday == 8) {
                       out.println("<option selected value=\"08\">8</option>");
                    } else {
                       out.println("<option value=\"08\">8</option>");
                    }
                    if (sday == 9) {
                       out.println("<option selected value=\"09\">9</option>");
                    } else {
                       out.println("<option value=\"09\">9</option>");
                    }
                    if (sday == 10) {
                       out.println("<option selected value=\"10\">10</option>");
                    } else {
                       out.println("<option value=\"10\">10</option>");
                    }
                    if (sday == 11) {
                       out.println("<option selected value=\"11\">11</option>");
                    } else {
                       out.println("<option value=\"11\">11</option>");
                    }
                    if (sday == 12) {
                       out.println("<option selected value=\"12\">12</option>");
                    } else {
                       out.println("<option value=\"12\">12</option>");
                    }
                    if (sday == 13) {
                       out.println("<option selected value=\"13\">13</option>");
                    } else {
                       out.println("<option value=\"13\">13</option>");
                    }
                    if (sday == 14) {
                       out.println("<option selected value=\"14\">14</option>");
                    } else {
                       out.println("<option value=\"14\">14</option>");
                    }
                    if (sday == 15) {
                       out.println("<option selected value=\"15\">15</option>");
                    } else {
                       out.println("<option value=\"15\">15</option>");
                    }
                    if (sday == 16) {
                       out.println("<option selected value=\"16\">16</option>");
                    } else {
                       out.println("<option value=\"16\">16</option>");
                    }
                    if (sday == 17) {
                       out.println("<option selected value=\"17\">17</option>");
                    } else {
                       out.println("<option value=\"17\">17</option>");
                    }
                    if (sday == 18) {
                       out.println("<option selected value=\"18\">18</option>");
                    } else {
                       out.println("<option value=\"18\">18</option>");
                    }
                    if (sday == 19) {
                       out.println("<option selected value=\"19\">19</option>");
                    } else {
                       out.println("<option value=\"19\">19</option>");
                    }
                    if (sday == 20) {
                       out.println("<option selected value=\"20\">20</option>");
                    } else {
                       out.println("<option value=\"20\">20</option>");
                    }
                    if (sday == 21) {
                       out.println("<option selected value=\"21\">21</option>");
                    } else {
                       out.println("<option value=\"21\">21</option>");
                    }
                    if (sday == 22) {
                       out.println("<option selected value=\"22\">22</option>");
                    } else {
                       out.println("<option value=\"22\">22</option>");
                    }
                    if (sday == 23) {
                       out.println("<option selected value=\"23\">23</option>");
                    } else {
                       out.println("<option value=\"23\">23</option>");
                    }
                    if (sday == 24) {
                       out.println("<option selected value=\"24\">24</option>");
                    } else {
                       out.println("<option value=\"24\">24</option>");
                    }
                    if (sday == 25) {
                       out.println("<option selected value=\"25\">25</option>");
                    } else {
                       out.println("<option value=\"25\">25</option>");
                    }
                    if (sday == 26) {
                       out.println("<option selected value=\"26\">26</option>");
                    } else {
                       out.println("<option value=\"26\">26</option>");
                    }
                    if (sday == 27) {
                       out.println("<option selected value=\"27\">27</option>");
                    } else {
                       out.println("<option value=\"27\">27</option>");
                    }
                    if (sday == 28) {
                       out.println("<option selected value=\"28\">28</option>");
                    } else {
                       out.println("<option value=\"28\">28</option>");
                    }
                    if (sday == 29) {
                       out.println("<option selected value=\"29\">29</option>");
                    } else {
                       out.println("<option value=\"29\">29</option>");
                    }
                    if (sday == 30) {
                       out.println("<option selected value=\"30\">30</option>");
                    } else {
                       out.println("<option value=\"30\">30</option>");
                    }
                    if (sday == 31) {
                       out.println("<option selected value=\"31\">31</option>");
                    } else {
                       out.println("<option value=\"31\">31</option>");
                    }
                    out.println("</select>");

                    year = thisYear;

                    out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"syear\">");
                      if (syear < year) {     // if prior to this year
                         out.println("<option selected value=\"" +syear+ "\">" +syear+ "</option>");
                      }
                      if (syear == year) {    
                         out.println("<option selected value=\"" +year+ "\">" +year+ "</option>");
                      } else {
                         out.println("<option value=\"" +year+ "\">" +year+ "</option>");
                      }
                      year++;    // next year
                      if (syear == year) {    
                         out.println("<option selected value=\"" +year+ "\">" +year+ "</option>");
                      } else {
                         out.println("<option value=\"" +year+ "\">" +year+ "</option>");
                      }
                      year++;    // next year
                      if (syear == year) {    
                         out.println("<option selected value=\"" +year+ "\">" +year+ "</option>");
                      } else {
                         out.println("<option value=\"" +year+ "\">" +year+ "</option>");
                      }
                    out.println("</select>");
                  out.println("</p>");
                  out.println("<p align=\"left\">&nbsp;&nbsp;End Date:&nbsp;&nbsp;&nbsp;");
                    out.println("Month:&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"emonth\">");
                    if (emonth == 1) {
                       out.println("<option selected value=\"01\">JAN</option>");
                    } else {
                       out.println("<option value=\"01\">JAN</option>");
                    }
                    if (emonth == 2) {
                       out.println("<option selected value=\"02\">FEB</option>");
                    } else {
                       out.println("<option value=\"02\">FEB</option>");
                    }
                    if (emonth == 3) {
                       out.println("<option selected value=\"03\">MAR</option>");
                    } else {
                       out.println("<option value=\"03\">MAR</option>");
                    }
                    if (emonth == 4) {
                       out.println("<option selected value=\"04\">APR</option>");
                    } else {
                       out.println("<option value=\"04\">APR</option>");
                    }
                    if (emonth == 5) {
                       out.println("<option selected value=\"05\">MAY</option>");
                    } else {
                       out.println("<option value=\"05\">MAY</option>");
                    }
                    if (emonth == 6) {
                       out.println("<option selected value=\"06\">JUN</option>");
                    } else {
                       out.println("<option value=\"06\">JUN</option>");
                    }
                    if (emonth == 7) {
                       out.println("<option selected value=\"07\">JUL</option>");
                    } else {
                       out.println("<option value=\"07\">JUL</option>");
                    }
                    if (emonth == 8) {
                       out.println("<option selected value=\"08\">AUG</option>");
                    } else {
                       out.println("<option value=\"08\">AUG</option>");
                    }
                    if (emonth == 9) {
                       out.println("<option selected value=\"09\">SEP</option>");
                    } else {
                       out.println("<option value=\"09\">SEP</option>");
                    }
                    if (emonth == 10) {
                       out.println("<option selected value=\"10\">OCT</option>");
                    } else {
                       out.println("<option value=\"10\">OCT</option>");
                    }
                    if (emonth == 11) {
                       out.println("<option selected value=\"11\">NOV</option>");
                    } else {
                       out.println("<option value=\"11\">NOV</option>");
                    }
                    if (emonth == 12) {
                       out.println("<option selected value=\"12\">DEC</option>");
                    } else {
                       out.println("<option value=\"12\">DEC</option>");
                    }
                    out.println("</select>");

                    out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"eday\">");
                    if (eday == 1) {
                       out.println("<option selected selected value=\"01\">1</option>");
                    } else {
                       out.println("<option value=\"01\">1</option>");
                    }
                    if (eday == 2) {
                       out.println("<option selected value=\"02\">2</option>");
                    } else {
                       out.println("<option value=\"02\">2</option>");
                    }
                    if (eday == 3) {
                       out.println("<option selected value=\"03\">3</option>");
                    } else {
                       out.println("<option value=\"03\">3</option>");
                    }
                    if (eday == 4) {
                       out.println("<option selected value=\"04\">4</option>");
                    } else {
                       out.println("<option value=\"04\">4</option>");
                    }
                    if (eday == 5) {
                       out.println("<option selected value=\"05\">5</option>");
                    } else {
                       out.println("<option value=\"05\">5</option>");
                    }
                    if (eday == 6) {
                       out.println("<option selected value=\"06\">6</option>");
                    } else {
                       out.println("<option value=\"06\">6</option>");
                    }
                    if (eday == 7) {
                       out.println("<option selected value=\"07\">7</option>");
                    } else {
                       out.println("<option value=\"07\">7</option>");
                    }
                    if (eday == 8) {
                       out.println("<option selected value=\"08\">8</option>");
                    } else {
                       out.println("<option value=\"08\">8</option>");
                    }
                    if (eday == 9) {
                       out.println("<option selected value=\"09\">9</option>");
                    } else {
                       out.println("<option value=\"09\">9</option>");
                    }
                    if (eday == 10) {
                       out.println("<option selected value=\"10\">10</option>");
                    } else {
                       out.println("<option value=\"10\">10</option>");
                    }
                    if (eday == 11) {
                       out.println("<option selected value=\"11\">11</option>");
                    } else {
                       out.println("<option value=\"11\">11</option>");
                    }
                    if (eday == 12) {
                       out.println("<option selected value=\"12\">12</option>");
                    } else {
                       out.println("<option value=\"12\">12</option>");
                    }
                    if (eday == 13) {
                       out.println("<option selected value=\"13\">13</option>");
                    } else {
                       out.println("<option value=\"13\">13</option>");
                    }
                    if (eday == 14) {
                       out.println("<option selected value=\"14\">14</option>");
                    } else {
                       out.println("<option value=\"14\">14</option>");
                    }
                    if (eday == 15) {
                       out.println("<option selected value=\"15\">15</option>");
                    } else {
                       out.println("<option value=\"15\">15</option>");
                    }
                    if (eday == 16) {
                       out.println("<option selected value=\"16\">16</option>");
                    } else {
                       out.println("<option value=\"16\">16</option>");
                    }
                    if (eday == 17) {
                       out.println("<option selected value=\"17\">17</option>");
                    } else {
                       out.println("<option value=\"17\">17</option>");
                    }
                    if (eday == 18) {
                       out.println("<option selected value=\"18\">18</option>");
                    } else {
                       out.println("<option value=\"18\">18</option>");
                    }
                    if (eday == 19) {
                       out.println("<option selected value=\"19\">19</option>");
                    } else {
                       out.println("<option value=\"19\">19</option>");
                    }
                    if (eday == 20) {
                       out.println("<option selected value=\"20\">20</option>");
                    } else {
                       out.println("<option value=\"20\">20</option>");
                    }
                    if (eday == 21) {
                       out.println("<option selected value=\"21\">21</option>");
                    } else {
                       out.println("<option value=\"21\">21</option>");
                    }
                    if (eday == 22) {
                       out.println("<option selected value=\"22\">22</option>");
                    } else {
                       out.println("<option value=\"22\">22</option>");
                    }
                    if (eday == 23) {
                       out.println("<option selected value=\"23\">23</option>");
                    } else {
                       out.println("<option value=\"23\">23</option>");
                    }
                    if (eday == 24) {
                       out.println("<option selected value=\"24\">24</option>");
                    } else {
                       out.println("<option value=\"24\">24</option>");
                    }
                    if (eday == 25) {
                       out.println("<option selected value=\"25\">25</option>");
                    } else {
                       out.println("<option value=\"25\">25</option>");
                    }
                    if (eday == 26) {
                       out.println("<option selected value=\"26\">26</option>");
                    } else {
                       out.println("<option value=\"26\">26</option>");
                    }
                    if (eday == 27) {
                       out.println("<option selected value=\"27\">27</option>");
                    } else {
                       out.println("<option value=\"27\">27</option>");
                    }
                    if (eday == 28) {
                       out.println("<option selected value=\"28\">28</option>");
                    } else {
                       out.println("<option value=\"28\">28</option>");
                    }
                    if (eday == 29) {
                       out.println("<option selected value=\"29\">29</option>");
                    } else {
                       out.println("<option value=\"29\">29</option>");
                    }
                    if (eday == 30) {
                       out.println("<option selected value=\"30\">30</option>");
                    } else {
                       out.println("<option value=\"30\">30</option>");
                    }
                    if (eday == 31) {
                       out.println("<option selected value=\"31\">31</option>");
                    } else {
                       out.println("<option value=\"31\">31</option>");
                    }
                    out.println("</select>");

                    year = thisYear;

                    out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"eyear\">");
                      if (eyear < year) {     // if prior to this year
                         out.println("<option selected value=\"" +eyear+ "\">" +eyear+ "</option>");
                      }
                      if (eyear == year) {
                         out.println("<option selected value=\"" +year+ "\">" +year+ "</option>");
                      } else {
                         out.println("<option value=\"" +year+ "\">" +year+ "</option>");
                      }
                      year++;    // next year
                      if (eyear == year) {
                         out.println("<option selected value=\"" +year+ "\">" +year+ "</option>");
                      } else {
                         out.println("<option value=\"" +year+ "\">" +year+ "</option>");
                      }
                      year++;    // next year
                      if (eyear == year) {
                         out.println("<option selected value=\"" +year+ "\">" +year+ "</option>");
                      } else {
                         out.println("<option value=\"" +year+ "\">" +year+ "</option>");
                      }
                    out.println("</select>");
                  out.println("</p>");

                  out.println("<p align=\"left\">&nbsp;&nbsp;Start Time: &nbsp;&nbsp; hr &nbsp;");
                    out.println("<select size=\"1\" name=\"start_hr\">");
                      if (s_hr == 1) {
                         out.println("<option selected selected value=\"01\">1</option>");
                      } else {
                         out.println("<option value=\"01\">1</option>");
                      }
                      if (s_hr == 2) {
                         out.println("<option selected value=\"02\">2</option>");
                      } else {
                         out.println("<option value=\"02\">2</option>");
                      }
                      if (s_hr == 3) {
                         out.println("<option selected value=\"03\">3</option>");
                      } else {
                         out.println("<option value=\"03\">3</option>");
                      }
                      if (s_hr == 4) {
                         out.println("<option selected value=\"04\">4</option>");
                      } else {
                         out.println("<option value=\"04\">4</option>");
                      }
                      if (s_hr == 5) {
                         out.println("<option selected value=\"05\">5</option>");
                      } else {
                         out.println("<option value=\"05\">5</option>");
                      }
                      if (s_hr == 6) {
                         out.println("<option selected value=\"06\">6</option>");
                      } else {
                         out.println("<option value=\"06\">6</option>");
                      }
                      if (s_hr == 7) {
                         out.println("<option selected value=\"07\">7</option>");
                      } else {
                         out.println("<option value=\"07\">7</option>");
                      }
                      if (s_hr == 8) {
                         out.println("<option selected value=\"08\">8</option>");
                      } else {
                         out.println("<option value=\"08\">8</option>");
                      }
                      if (s_hr == 9) {
                         out.println("<option selected value=\"09\">9</option>");
                      } else {
                         out.println("<option value=\"09\">9</option>");
                      }
                      if (s_hr == 10) {
                         out.println("<option selected value=\"10\">10</option>");
                      } else {
                         out.println("<option value=\"10\">10</option>");
                      }
                      if (s_hr == 11) {
                         out.println("<option selected value=\"11\">11</option>");
                      } else {
                         out.println("<option value=\"11\">11</option>");
                      }
                      if (s_hr == 12) {
                         out.println("<option selected value=\"12\">12</option>");
                      } else {
                         out.println("<option value=\"12\">12</option>");
                      }
                    out.println("</select>");
                    out.println("&nbsp; min &nbsp;");
                    if (s_min < 10) {
                       out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=0" + s_min + " name=\"start_min\">");
                    } else {
                       out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + s_min + " name=\"start_min\">");
                    }
                    out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"start_ampm\">");
                      if (s_ampm.equals( "AM" )) {
                         out.println("<option selected value=\"00\">AM</option>");
                      } else {
                         out.println("<option value=\"00\">AM</option>");
                      }
                      if (s_ampm.equals( "PM" )) {
                         out.println("<option selected value=\"12\">PM</option>");
                      } else {
                         out.println("<option value=\"12\">PM</option>");
                      }
                    out.println("</select>");
                  out.println("</p>");
                  out.println("<p align=\"left\">&nbsp;&nbsp;End Time: &nbsp;&nbsp;&nbsp;&nbsp; hr &nbsp;");
                    out.println("<select size=\"1\" name=\"end_hr\">");
                      if (e_hr == 1) {
                         out.println("<option selected selected value=\"01\">1</option>");
                      } else {
                         out.println("<option value=\"01\">1</option>");
                      }
                      if (e_hr == 2) {
                         out.println("<option selected value=\"02\">2</option>");
                      } else {
                         out.println("<option value=\"02\">2</option>");
                      }
                      if (e_hr == 3) {
                         out.println("<option selected value=\"03\">3</option>");
                      } else {
                         out.println("<option value=\"03\">3</option>");
                      }
                      if (e_hr == 4) {
                         out.println("<option selected value=\"04\">4</option>");
                      } else {
                         out.println("<option value=\"04\">4</option>");
                      }
                      if (e_hr == 5) {
                         out.println("<option selected value=\"05\">5</option>");
                      } else {
                         out.println("<option value=\"05\">5</option>");
                      }
                      if (e_hr == 6) {
                         out.println("<option selected value=\"06\">6</option>");
                      } else {
                         out.println("<option value=\"06\">6</option>");
                      }
                      if (e_hr == 7) {
                         out.println("<option selected value=\"07\">7</option>");
                      } else {
                         out.println("<option value=\"07\">7</option>");
                      }
                      if (e_hr == 8) {
                         out.println("<option selected value=\"08\">8</option>");
                      } else {
                         out.println("<option value=\"08\">8</option>");
                      }
                      if (e_hr == 9) {
                         out.println("<option selected value=\"09\">9</option>");
                      } else {
                         out.println("<option value=\"09\">9</option>");
                      }
                      if (e_hr == 10) {
                         out.println("<option selected value=\"10\">10</option>");
                      } else {
                         out.println("<option value=\"10\">10</option>");
                      }
                      if (e_hr == 11) {
                         out.println("<option selected value=\"11\">11</option>");
                      } else {
                         out.println("<option value=\"11\">11</option>");
                      }
                      if (e_hr == 12) {
                         out.println("<option selected value=\"12\">12</option>");
                      } else {
                         out.println("<option value=\"12\">12</option>");
                      }
                    out.println("</select>");
                    out.println("&nbsp; min &nbsp;");
                    if (e_min < 10) {
                       out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=0" + e_min + " name=\"end_min\">");
                    } else {
                       out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + e_min + " name=\"end_min\">");
                    }
                    out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"end_ampm\">");
                      if (e_ampm.equals( "AM" )) {
                         out.println("<option selected value=\"00\">AM</option>");
                      } else {
                         out.println("<option value=\"00\">AM</option>");
                      }
                      if (e_ampm.equals( "PM" )) {
                         out.println("<option selected value=\"12\">PM</option>");
                      } else {
                         out.println("<option value=\"12\">PM</option>");
                      }
                    out.println("</select>");
                  out.println("</p>");
                  out.println("<p align=\"Left\">&nbsp;&nbsp;Recurrence (select all that apply):");
                     out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                     if (mon == 1) {
                        out.println("<input type=\"checkbox\" name=\"mon\" checked value=\"1\">&nbsp;&nbsp;Every Monday");
                     } else {
                        out.println("<input type=\"checkbox\" name=\"mon\" value=\"1\">&nbsp;&nbsp;Every Monday");
                     }
                     out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                     if (tue == 1) {
                        out.println("<input type=\"checkbox\" name=\"tue\" checked value=\"1\">&nbsp;&nbsp;Every Tuesday");
                     } else {
                        out.println("<input type=\"checkbox\" name=\"tue\" value=\"1\">&nbsp;&nbsp;Every Tuesday");
                     }
                     out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                     if (wed == 1) {
                        out.println("<input type=\"checkbox\" name=\"wed\" checked value=\"1\">&nbsp;&nbsp;Every Wednesday");
                     } else {
                        out.println("<input type=\"checkbox\" name=\"wed\" value=\"1\">&nbsp;&nbsp;Every Wednesday");
                     }
                     out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                     if (thu == 1) {
                        out.println("<input type=\"checkbox\" name=\"thu\" checked value=\"1\">&nbsp;&nbsp;Every Thursday");
                     } else {
                        out.println("<input type=\"checkbox\" name=\"thu\" value=\"1\">&nbsp;&nbsp;Every Thursday");
                     }
                     out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                     if (fri == 1) {
                        out.println("<input type=\"checkbox\" name=\"fri\" checked value=\"1\">&nbsp;&nbsp;Every Friday");
                     } else {
                        out.println("<input type=\"checkbox\" name=\"fri\" value=\"1\">&nbsp;&nbsp;Every Friday");
                     }
                     out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                     if (sat == 1) {
                        out.println("<input type=\"checkbox\" name=\"sat\" checked value=\"1\">&nbsp;&nbsp;Every Saturday");
                     } else {
                        out.println("<input type=\"checkbox\" name=\"sat\" value=\"1\">&nbsp;&nbsp;Every Saturday");
                     }
                     out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                     if (sun == 1) {
                        out.println("<input type=\"checkbox\" name=\"sun\" checked value=\"1\">&nbsp;&nbsp;Every Sunday");
                     } else {
                        out.println("<input type=\"checkbox\" name=\"sun\" value=\"1\">&nbsp;&nbsp;Every Sunday");
                     }
                  out.println("</p>");

                  out.println("<p align=\"left\">&nbsp;&nbsp;Segment Size (blocks of time in minutes):&nbsp;&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"fragment\">");
                      if (fragment == 5) {
                         out.println("<option selected value=\"5\">5</option>");
                      } else {
                         out.println("<option value=\"5\">5</option>");
                      }
                      if (fragment == 10) {
                         out.println("<option selected value=\"10\">10</option>");
                      } else {
                         out.println("<option value=\"10\">10</option>");
                      }
                      if (fragment == 15) {
                         out.println("<option selected value=\"15\">15</option>");
                      } else {
                         out.println("<option value=\"15\">15</option>");
                      }
                      if (fragment == 20) {
                         out.println("<option selected value=\"20\">20</option>");
                      } else {
                         out.println("<option value=\"20\">20</option>");
                      }
                      if (fragment == 25) {
                         out.println("<option selected value=\"25\">25</option>");
                      } else {
                         out.println("<option value=\"25\">25</option>");
                      }
                      if (fragment == 30) {
                         out.println("<option selected value=\"30\">30</option>");
                      } else {
                         out.println("<option value=\"30\">30</option>");
                      }
                      if (fragment == 35) {
                         out.println("<option selected value=\"35\">35</option>");
                      } else {
                         out.println("<option value=\"35\">35</option>");
                      }
                      if (fragment == 40) {
                         out.println("<option selected value=\"40\">40</option>");
                      } else {
                         out.println("<option value=\"40\">40</option>");
                      }
                      if (fragment == 45) {
                         out.println("<option selected value=\"45\">45</option>");
                      } else {
                         out.println("<option value=\"45\">45</option>");
                      }
                      if (fragment == 50) {
                         out.println("<option selected value=\"50\">50</option>");
                      } else {
                         out.println("<option value=\"50\">50</option>");
                      }
                      if (fragment == 55) {
                         out.println("<option selected value=\"55\">55</option>");
                      } else {
                         out.println("<option value=\"55\">55</option>");
                      }
                      if (fragment == 60) {
                         out.println("<option selected value=\"60\">60</option>");
                      } else {
                         out.println("<option value=\"60\">60</option>");
                      }
                      if (fragment == 65) {
                         out.println("<option selected value=\"65\">65</option>");
                      } else {
                         out.println("<option value=\"65\">65</option>");
                      }
                      if (fragment == 70) {
                         out.println("<option selected value=\"70\">70</option>");
                      } else {
                         out.println("<option value=\"70\">70</option>");
                      }
                      if (fragment == 75) {
                         out.println("<option selected value=\"75\">75</option>");
                      } else {
                         out.println("<option value=\"75\">75</option>");
                      }
                      if (fragment == 80) {
                         out.println("<option selected value=\"80\">80</option>");
                      } else {
                         out.println("<option value=\"80\">80</option>");
                      }
                      if (fragment == 85) {
                         out.println("<option selected value=\"85\">85</option>");
                      } else {
                         out.println("<option value=\"85\">85</option>");
                      }
                      if (fragment == 90) {
                         out.println("<option selected value=\"90\">90</option>");
                      } else {
                         out.println("<option value=\"90\">90</option>");
                      }
                      if (fragment == 95) {
                         out.println("<option selected value=\"95\">95</option>");
                      } else {
                         out.println("<option value=\"95\">95</option>");
                      }
                      if (fragment == 100) {
                         out.println("<option selected value=\"100\">100</option>");
                      } else {
                         out.println("<option value=\"100\">100</option>");
                      }
                      if (fragment == 105) {
                         out.println("<option selected value=\"105\">105</option>");
                      } else {
                         out.println("<option value=\"105\">105</option>");
                      }
                      if (fragment == 110) {
                         out.println("<option selected value=\"110\">110</option>");
                      } else {
                         out.println("<option value=\"110\">110</option>");
                      }
                      if (fragment == 115) {
                         out.println("<option selected value=\"115\">115</option>");
                      } else {
                         out.println("<option value=\"115\">115</option>");
                      }
                      if (fragment == 120) {
                         out.println("<option selected value=\"120\">120</option>");
                      } else {
                         out.println("<option value=\"120\">120</option>");
                      }
                      if (fragment == 125) {
                         out.println("<option selected value=\"125\">125</option>");
                      } else {
                         out.println("<option value=\"125\">125</option>");
                      }
                    out.println("</select>");
                  out.println("</p>");

                  out.println("<p align=\"left\">&nbsp;&nbsp;Color to make these times in the Lesson Book:&nbsp;&nbsp;");
                  out.println("<select size=\"1\" name=\"color\">");
                    
                      if (color.equals( "Default" )) {

                        out.println("<option selected value=" + color + ">Default (none)</option>");
                      } else {
                        out.println("<option selected value=" + color + ">" + color + "</option>");
                      }
                      out.println("<option value=\"Antiquewhite\">Antique White</option>");
                      out.println("<option value=\"Aqua\">Aqua</option>");
                      out.println("<option value=\"Aquamarine\">Aquamarine</option>");
                      out.println("<option value=\"Beige\">Beige</option>");
                      out.println("<option value=\"Bisque\">Bisque</option>");
                      out.println("<option value=\"Blanchedalmond\">Blanched Almond</option>");
                      out.println("<option value=\"Blue\">Blue</option>");
                      out.println("<option value=\"Bluevoilet\">Blueviolet</option>");
                      out.println("<option value=\"Brown\">Brown</option>");
                      out.println("<option value=\"Burlywood\">Burlywood</option>");
                      out.println("<option value=\"Cadetblue\">Cadetblue</option>");
                      out.println("<option value=\"Chartreuse\">Chartreuse</option>");
                      out.println("<option value=\"Chocolate\">Chocolate</option>");
                      out.println("<option value=\"Coral\">Coral</option>");
                      out.println("<option value=\"Cornflowerblue\">Cornflowerblue</option>");
                      out.println("<option value=\"Cornsilk\">Cornsilk</option>");
                      out.println("<option value=\"Crimson\">Crimson</option>");
                      out.println("<option value=\"Cyan\">Cyan</option>");
                      out.println("<option value=\"Darkblue\">Darkblue</option>");
                      out.println("<option value=\"Darkcyan\">Darkcyan</option>");
                      out.println("<option value=\"Darkgoldenrod\">Darkgoldenrod</option>");
                      out.println("<option value=\"Darkgray\">Darkgray</option>");
                      out.println("<option value=\"Darkgreen\">Darkgreen</option>");
                      out.println("<option value=\"Darkkhaki\">Darkkhaki</option>");
                      out.println("<option value=\"Darkmagenta\">Darkmagenta</option>");
                      out.println("<option value=\"Darkolivegreen\">Darkolivegreen</option>");
                      out.println("<option value=\"Darkorange\">Darkorange</option>");
                      out.println("<option value=\"Darkorchid\">Darkorchid</option>");
                      out.println("<option value=\"Darkred\">Darkred</option>");
                      out.println("<option value=\"Darksalmon\">Darksalmon</option>");
                      out.println("<option value=\"Darkseagreen\">Darkseagreen</option>");
                      out.println("<option value=\"Darkslateblue\">Darkslateblue</option>");
                      out.println("<option value=\"Darkslategray\">Darkslategray</option>");
                      out.println("<option value=\"Darkturquoise\">Darkturquoise</option>");
                      out.println("<option value=\"Darkviolet\">Darkviolet</option>");
                      out.println("<option value=\"Deeppink\">Deeppink</option>");
                      out.println("<option value=\"Deepskyblue\">Deepskyblue</option>");
                      out.println("<option value=\"Default\">Default (none)</option>");
                      out.println("<option value=\"Dimgray\">Dimgray</option>");
                      out.println("<option value=\"Dodgerblue\">Dodgerblue</option>");
                      out.println("<option value=\"Firebrick\">Firebrick</option>");
                      out.println("<option value=\"Forestgreen\">Forestgreen</option>");
                      out.println("<option value=\"Fuchsia\">Fuchsia</option>");
                      out.println("<option value=\"Gainsboro\">Gainsboro</option>");
                      out.println("<option value=\"Gold\">Gold</option>");
                      out.println("<option value=\"Goldenrod\">Goldenrod</option>");
                      out.println("<option value=\"Gray\">Gray</option>");
                      out.println("<option value=\"Green\">Green</option>");
                      out.println("<option value=\"Greenyellow\">Greenyellow</option>");
                      out.println("<option value=\"Hotpink\">Hotpink</option>");
                      out.println("<option value=\"Indianred\">Indianred</option>");
                      out.println("<option value=\"Indigo\">Indigo</option>");
                      out.println("<option value=\"Ivory\">Ivory</option>");
                      out.println("<option value=\"Khaki\">Khaki</option>");
                      out.println("<option value=\"Lavender\">Lavender</option>");
                      out.println("<option value=\"Lavenderblush\">Lavenderblush</option>");
                      out.println("<option value=\"Lawngreen\">Lawngreen</option>");
                      out.println("<option value=\"Lemonchiffon\">Lemonchiffon</option>");
                      out.println("<option value=\"Lightblue\">Lightblue</option>");
                      out.println("<option value=\"Lightcoral\">Lightcoral</option>");
                      out.println("<option value=\"Lightgoldenrodyellow\">Lightgoldenrodyellow</option>");
                      out.println("<option value=\"Lightgreen\">Lightgreen</option>");
                      out.println("<option value=\"Lightgrey\">Lightgrey</option>");
                      out.println("<option value=\"Lightpink\">Lightpink</option>");
                      out.println("<option value=\"Lightsalmon\">Lightsalmon</option>");
                      out.println("<option value=\"Lightseagreen\">Lightseagreen</option>");
                      out.println("<option value=\"Lightskyblue\">Lightskyblue</option>");
                      out.println("<option value=\"Lightslategray\">Lightslategray</option>");
                      out.println("<option value=\"Lightsteelblue\">Lightsteelblue</option>");
                      out.println("<option value=\"Lime\">Lime</option>");
                      out.println("<option value=\"Limegreen\">Limegreen</option>");
                      out.println("<option value=\"Linen\">Linen</option>");
                      out.println("<option value=\"Magenta\">Magenta</option>");
                      out.println("<option value=\"Mediumauqamarine\">Mediumauqamarine</option>");
                      out.println("<option value=\"Mediumblue\">Mediumblue</option>");
                      out.println("<option value=\"Mediumorchid\">Mediumorchid</option>");
                      out.println("<option value=\"Mediumpurple\">Mediumpurple</option>");
                      out.println("<option value=\"Mediumseagreen\">Mediumseagreen</option>");
                      out.println("<option value=\"Mediumslateblue\">Mediumslateblue</option>");
                      out.println("<option value=\"Mediumspringgreen\">Mediumspringgreen</option>");
                      out.println("<option value=\"Mediumturquoise\">Mediumturquoise</option>");
                      out.println("<option value=\"Mediumvioletred\">Mediumvioletred</option>");
                      out.println("<option value=\"Mistyrose\">Mistyrose</option>");
                      out.println("<option value=\"Moccasin\">Moccasin</option>");
                      out.println("<option value=\"Navajowhite\">Navajowhite</option>");
                      out.println("<option value=\"Navy\">Navy</option>");
                      out.println("<option value=\"Oldlace\">Oldlace</option>");
                      out.println("<option value=\"Olive\">Olive</option>");
                      out.println("<option value=\"Olivedrab\">Olivedrab</option>");
                      out.println("<option value=\"Orange\">Orange</option>");
                      out.println("<option value=\"Orangered\">Orangered</option>");
                      out.println("<option value=\"Orchid\">Orchid</option>");
                      out.println("<option value=\"Palegoldenrod\">Palegoldenrod</option>");
                      out.println("<option value=\"Palegreen\">Palegreen</option>");
                      out.println("<option value=\"Paleturquoise\">Paleturquoise</option>");
                      out.println("<option value=\"Palevioletred\">Palevioletred</option>");
                      out.println("<option value=\"Papayawhip\">Papayawhip</option>");
                      out.println("<option value=\"Peachpuff\">Peachpuff</option>");
                      out.println("<option value=\"Peru\">Peru</option>");
                      out.println("<option value=\"Pink\">Pink</option>");
                      out.println("<option value=\"Plum\">Plum</option>");
                      out.println("<option value=\"Powderblue\">Powderblue</option>");
                      out.println("<option value=\"Purple\">Purple</option>");
                      out.println("<option value=\"Red\">Red</option>");
                      out.println("<option value=\"Rosybrown\">Rosybrown</option>");
                      out.println("<option value=\"Royalblue\">Royalblue</option>");
                      out.println("<option value=\"Saddlebrown\">Saddlebrown</option>");
                      out.println("<option value=\"Salmon\">Salmon</option>");
                      out.println("<option value=\"Sandybrown\">Sandybrown</option>");
                      out.println("<option value=\"Seagreen\">Seagreen</option>");
                      out.println("<option value=\"Sienna\">Sienna</option>");
                      out.println("<option value=\"Silver\">Silver</option>");
                      out.println("<option value=\"Skyblue\">Skyblue</option>");
                      out.println("<option value=\"Slateblue\">Slateblue</option>");
                      out.println("<option value=\"Slategray\">Slategray</option>");
                      out.println("<option value=\"Springgreen\">Springgreen</option>");
                      out.println("<option value=\"Steelblue\">Steelblue</option>");
                      out.println("<option value=\"Tan\">Tan</option>");
                      out.println("<option value=\"Teal\">Teal</option>");
                      out.println("<option value=\"Thistle\">Thistle</option>");
                      out.println("<option value=\"Tomato\">Tomato</option>");
                      out.println("<option value=\"Turquoise\">Turquoise</option>");
                      out.println("<option value=\"Violet\">Violet</option>");
                      out.println("<option value=\"Wheat\">Wheat</option>");
                      out.println("<option value=\"Yellow\">Yellow</option>");
                      out.println("<option value=\"YellowGreen\">YellowGreen</option>");
                  out.println("</select>");
                  out.println("<br>");
                  out.println("&nbsp;&nbsp;Click here to see the available colors:&nbsp;");
                  out.println("<a href=\"/" +rev+ "/proshop_color.htm\" target=\"_blank\">View Colors</a>");
                  out.println("</p>");

                  out.println("<p align=\"center\">");
                  out.println("<input type=\"submit\" name=\"editltime2\" value=\"Update\">");    // to update record
                  out.println("</p>");
                  out.println("</font>");
                  out.println("</td>");
               out.println("</tr>");
               out.println("</form>");
            out.println("</table>");

            out.println("</td></tr></table>");                // end of main page table
            out.println("<font size=\"2\">");
            out.println("<form method=\"get\" action=\"Proshop_announce\">");
            out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");
            out.println("</center></font></body></html>");
            out.close();

         } else {

            nfError(req, out, lottery);            // LT not found
         }
         pstmt.close();

      }
      catch (Exception exc) {

         dbError(req, out, exc, lottery);
         return;
      }
   }
 }


 // ********************************************************************
 //  Process the 'Edit Lesson Time' SUBMIT request
 // ********************************************************************

 private void editLtime2(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException {

   PreparedStatement pstmt = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String ltname = "";
   String frags = "";
   String color = "";
   String error = "";
     
   String oldname = "";     // original parm values before edit
   String oldsdates = "";
   String oldstimes = "";
   String oldedates = "";
   String oldetimes = "";
   String oldmons = "";
   String oldtues = "";
   String oldweds = "";
   String oldthus = "";
   String oldfris = "";
   String oldsats = "";
   String oldsuns = "";
   String oldcolor = "";
   String oldfrags = "";

   int id = 0;
   int mon = 0;
   int tue = 0;
   int wed = 0;
   int thu = 0;
   int fri = 0;
   int sat = 0;
   int sun = 0;
   int fragment = 0;
   int smonth = 0;                // init integer date/time values
   int sday = 0;
   int syear = 0;
   int emonth = 0;
   int eday = 0;
   int eyear = 0;
   int s_hr = 0;
   int s_min = 0;
   int s_ampm = 0;
   int e_hr = 0;
   int e_min = 0;
   int e_ampm = 0;
   int stime = 0;
   int etime = 0;
   int oldstime = 0;
   int oldetime = 0;
   int oldmon = 0;
   int oldtue = 0;
   int oldwed = 0;
   int oldthu = 0;
   int oldfri = 0;
   int oldsat = 0;
   int oldsun = 0;
   int oldfragment = 0;

   long sdate = 0;
   long edate = 0;
   long oldsdate = 0;
   long oldedate = 0;


   try {
      id = Integer.parseInt(proid);          // convert the pro id
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   //
   //  Get the parameters
   //
   String s_month = req.getParameter("smonth");             //  month (00 - 12)
   String s_day = req.getParameter("sday");                 //  day (01 - 31)
   String s_year = req.getParameter("syear");               //  year
   String start_hr = req.getParameter("start_hr");         //  start hour (01 - 12)
   String start_min = req.getParameter("start_min");       //  start min (00 - 59)
   String start_ampm = req.getParameter("start_ampm");     //  AM/PM (00 or 12)
   String e_month = req.getParameter("emonth");
   String e_day = req.getParameter("eday");
   String e_year = req.getParameter("eyear");
   String end_hr = req.getParameter("end_hr");
   String end_min = req.getParameter("end_min");
   String end_ampm = req.getParameter("end_ampm");
   ltname = req.getParameter("ltime_name");

   if (req.getParameter("fragment") != null) {

      frags = req.getParameter("fragment");

      try {
         fragment = Integer.parseInt(frags);
      }
      catch (NumberFormatException e) {
         fragment = 0;
      }
   }
   if (req.getParameter("color") != null) {

      color = req.getParameter("color");
   }
   if (req.getParameter("mon") != null) {

      mon = 1;           // recurr check box selected
   }
   if (req.getParameter("tue") != null) {

      tue = 1;           // recurr check box selected
   }
   if (req.getParameter("wed") != null) {

      wed = 1;           // recurr check box selected
   }
   if (req.getParameter("thu") != null) {

      thu = 1;           // recurr check box selected
   }
   if (req.getParameter("fri") != null) {

      fri = 1;           // recurr check box selected
   }
   if (req.getParameter("sat") != null) {

      sat = 1;           // recurr check box selected
   }
   if (req.getParameter("sun") != null) {

      sun = 1;           // recurr check box selected
   }

   //
   //  Get the original values before the edit
   //
   oldname = req.getParameter("oldname");
   oldsdates = req.getParameter("oldsdate");
   oldstimes = req.getParameter("oldstime");
   oldedates = req.getParameter("oldedate");
   oldetimes = req.getParameter("oldetime");
   oldmons = req.getParameter("oldmon");
   oldtues = req.getParameter("oldtue");
   oldweds = req.getParameter("oldwed");
   oldthus = req.getParameter("oldthu");
   oldfris = req.getParameter("oldfri");
   oldsats = req.getParameter("oldsat");
   oldsuns = req.getParameter("oldsun");
   oldcolor = req.getParameter("oldcolor");
   oldfrags = req.getParameter("oldfragment");


   //
   // Convert the numeric string parameters to Int's
   //
   try {
      smonth = Integer.parseInt(s_month);
      sday = Integer.parseInt(s_day);
      syear = Integer.parseInt(s_year);
      s_hr = Integer.parseInt(start_hr);
      s_min = Integer.parseInt(start_min);
      s_ampm = Integer.parseInt(start_ampm);
      emonth = Integer.parseInt(e_month);
      eday = Integer.parseInt(e_day);
      eyear = Integer.parseInt(e_year);
      e_hr = Integer.parseInt(end_hr);
      e_min = Integer.parseInt(end_min);
      e_ampm = Integer.parseInt(end_ampm);
      oldmon = Integer.parseInt(oldmons);
      oldtue = Integer.parseInt(oldtues);
      oldwed = Integer.parseInt(oldweds);
      oldthu = Integer.parseInt(oldthus);
      oldfri = Integer.parseInt(oldfris);
      oldsat = Integer.parseInt(oldsats);
      oldsun = Integer.parseInt(oldsuns);
      oldfragment = Integer.parseInt(oldfrags);
      oldstime = Integer.parseInt(oldstimes);
      oldetime = Integer.parseInt(oldetimes);
      oldsdate = Long.parseLong(oldsdates);
      oldedate = Long.parseLong(oldedates);
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }

   //
   //  Make sure user specified the minimum parameters
   //
   if (ltname.equals( "" ) || ltname == null) {

      error = "You must specify a name for this Lesson Time.";
      invData(req, out, lottery, error);
      return;
   }

   //
   //  Check for quotes in name
   //
   boolean err = SystemUtils.scanQuote(ltname);           // check for single quote

   if (err == true) {

      error = "Apostrophes (single quotes) cannot be part of the Name.";
      invData(req, out, lottery, error);
      return;
   }

   //
   //  Check if lesson time already exists
   //
   if (!ltname.equalsIgnoreCase( oldname )) {      // if name changed

      try {

         pstmt = con.prepareStatement (
                 "SELECT proid FROM lessontime5 WHERE proid = ? AND activity_id = ? AND lname = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1, id);
         pstmt.setInt(2, sess_activity_id);
         pstmt.setString(3, ltname);
         rs = pstmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            dupMem(req, out, lottery);
            pstmt.close();
            return;
         }
         pstmt.close();
      }
      catch (Exception ignored) {
      }
   }

   //
   //  adjust some values for the table
   //
   sdate = syear * 10000;       // create a date field from input values
   sdate = sdate + (smonth * 100);
   sdate = sdate + sday;             // date = yyyymmdd (for comparisons)

   edate = eyear * 10000;       // ditto
   edate = edate + (emonth * 100);
   edate = edate + eday;

   if (s_hr != 12) {                // _hr specified as 01 - 12 (_ampm = 00 or 12)

      s_hr = s_hr + s_ampm;         // convert to military time (12 is always Noon, or PM)
   }

   if (e_hr != 12) {                // ditto

      e_hr = e_hr + e_ampm;
   }

   stime = (s_hr * 100) + s_min;
   etime = (e_hr * 100) + e_min;

   //
   //  verify the date and time fields
   //
   if (sdate > edate) {

      error = "The Start Date cannot be later than the End Date.  Please correct the date range.";
      invData(req, out, lottery, error);
      return;
   }
   if (stime > etime) {

      error = "The Start Time cannot be later than the End Time.  Please correct the Time range.";
      invData(req, out, lottery, error);
      return;
   }

   //
   //  Update the lesson time
   //
   try {
      pstmt = con.prepareStatement (
        "UPDATE lessontime5 SET lname = ?, sdate = ?, stime = ?, edate = ?, etime = ?, " +
        "mon = ?, tue = ?, wed = ?, thu = ?, fri = ?, sat = ?, sun = ?, color = ?, fragment = ? " +
        "WHERE proid = ? AND activity_id = ? AND lname = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, ltname);
      pstmt.setLong(2, sdate);
      pstmt.setInt(3, stime);
      pstmt.setLong(4, edate);
      pstmt.setInt(5, etime);
      pstmt.setInt(6, mon);
      pstmt.setInt(7, tue);
      pstmt.setInt(8, wed);
      pstmt.setInt(9, thu);
      pstmt.setInt(10, fri);
      pstmt.setInt(11, sat);
      pstmt.setInt(12, sun);
      pstmt.setString(13, color);
      pstmt.setInt(14, fragment);

      pstmt.setInt(15, id);
      pstmt.setInt(16, sess_activity_id);
      pstmt.setString(17, oldname);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

   }
   catch (Exception exc) {

      dbError(req, out, exc, lottery);
      return;
   }

   //
   // Database updated - inform user
   //
   out.println(SystemUtils.HeadTitle("Proshop Add Lesson Time"));
   out.println("<BODY>");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<CENTER><BR>");
   out.println("<BR><BR><H3>The Lesson Time Has Been Updated</H3>");
   out.println("<BR><BR>Thank you, the lesson time has been updated for the specified pro.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<a href=\"Proshop_lesson?editltime=yes&proid=" +id+ "\">Edit Another Lesson Time For This Pro</a>");
   out.println("<BR><BR>");
   out.println("<a href=\"Proshop_lesson?editltime=yes\">Edit Lesson Time For Other Pro</a>");
   out.println("<BR><BR>");
   out.println("<form method=\"get\" action=\"Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
     
   
   
   //
   //  Now see if we need to update the lesson book
   //
   if (sdate != oldsdate || edate != oldedate || stime != oldstime || etime != oldetime ||  
       mon != oldmon || tue != oldtue || wed != oldwed || thu != oldthu || fri != oldfri || 
       sat != oldsat || sun != oldsun || fragment != oldfragment) {
         
      replaceLtime(proid, sess_activity_id, ltname, oldname, con);    // go replace the lesson times in the lesson book
        
   } else {      // see if name or color has changed
     
      if (!ltname.equalsIgnoreCase( oldname )) {       // if name has changed
        
         changeLTname(proid, sess_activity_id, ltname, oldname, con);    // replace name in lesson book
      }
      if (!color.equalsIgnoreCase( oldcolor )) {       // if color has changed

         changeLTcolor(proid, sess_activity_id, ltname, color, con);      // replace color in lesson book
      }
   }
  
 }


 // ********************************************************************
 //  Process the 'Delete Lesson Time' SUBMIT request
 // ********************************************************************

 private void deleteLtime(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException {

   PreparedStatement pstmt = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String ltname = "";

   long today = 0;
   int id = 0;

   try {
      id = Integer.parseInt(proid);
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   //
   //  Get the parameters
   //
   if (req.getParameter("ltname") != null) {

      ltname = req.getParameter("ltname");
   }

   //
   //  Check if this is a confirmation to delete
   //
   if (req.getParameter("conf") != null) {

      //
      //  Get today's date
      //
      Calendar cal = new GregorianCalendar();       // get todays date

      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH);
      int day = cal.get(Calendar.DAY_OF_MONTH);
      month++;                           // month starts at zero

      today = year * 10000;                        // create a today field of yyyymmdd (for today)
      today = today + (month * 100);
      today = today + day;                         // date = yyyymmdd (for comparisons)

      //
      //  Delete the lesson time
      //
      try {
         pstmt = con.prepareStatement (
                  "Delete FROM lessontime5 WHERE proid = ? AND activity_id = ? AND lname = ?");

         pstmt.clearParameters();               // clear the parms
         pstmt.setInt(1, id);
         pstmt.setInt(2, sess_activity_id);
         pstmt.setString(3, ltname);
         pstmt.executeUpdate();         // execute the prepared pstmt

         pstmt.close();

         //
         //  Remove the old lesson times from the Lesson Book
         //
         pstmt = con.prepareStatement (
                  "Delete FROM lessonbook5 " +
                  "WHERE proid = ? AND activity_id = ? AND ltname = ? AND date >= ? AND memname = ''");

         pstmt.clearParameters();
         pstmt.setInt(1, id);               // this pro
         pstmt.setInt(2, sess_activity_id); // this activity id
         pstmt.setString(3, ltname);        // this lesson time
         pstmt.setLong(4, today);           // today or later
         pstmt.executeUpdate();

         pstmt.close();

      }
      catch (Exception exc) {

         dbError(req, out, exc, lottery);
         return;
      }

      //
      //  Time deleted - inform user
      //
      out.println(SystemUtils.HeadTitle("Delete Lesson Time Confirmation"));
      out.println("<BODY>");
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      out.println("<CENTER><BR>");
      out.println("<p>&nbsp;</p>");
      out.println("<BR><H3>Lesson Time Removed</H3><BR>");
      out.println("<BR><b>" + ltname + "</b> has been removed from the Lesson Time database.");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<a href=\"Proshop_lesson?editltime=yes&proid=" +id+ "\">Edit Another Lesson Time For This Pro</a>");
      out.println("<BR><BR>");
      out.println("<a href=\"Proshop_lesson?editltime=yes\">Edit Lesson Time For Other Pro</a>");
      out.println("<BR><BR>");
      out.println("<form method=\"get\" action=\"Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();

   } else {

      //
      //  This is the first request for the delete - request a confirmation
      //
      out.println(SystemUtils.HeadTitle("Delete Lesson Time Confirmation"));
      out.println("<BODY>");
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      out.println("<CENTER><BR>");
      out.println("<p>&nbsp;</p>");
      out.println("<BR><H3>Remove Lesson Time Confirmation</H3><BR>");
      out.println("<BR>Please confirm that you wish to remove this Lesson Time: <b>" + ltname + "</b><br>");

      out.println("<form action=\"Proshop_lesson\" method=\"post\" target=\"bot\">");
      out.println("<BR>ARE YOU SURE YOU WANT TO DELETE THIS LESSON TIME?");

      out.println("<input type=\"hidden\" name=\"proid\" value =\"" +proid+ "\">");
      out.println("<input type=\"hidden\" name=\"deleteltime\" value =\"yes\">");
      out.println("<input type=\"hidden\" name=\"conf\" value =\"yes\">");
      out.println("<input type=\"hidden\" name=\"ltname\" value =\"" +ltname+ "\">");
      out.println("<br><br><input type=\"submit\" value=\"Yes - Delete\">");
      out.println("</form><font size=\"2\">");

      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"No - Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
   }
 }


 // ****************************************************************************
 //  replaceLtime - replace the lesson times in the lesson book after an edit
 // ****************************************************************************

 private void replaceLtime(String proid, int activity_id, String ltname, String oldname, Connection con) {

   PreparedStatement pstmt = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   String frags = "";
   String color = "";
   String error = "";
   String omit = "";

   int id = 0;
   int mon = 0;
   int tue = 0;
   int wed = 0;
   int thu = 0;
   int fri = 0;
   int sat = 0;
   int sun = 0;
   int fragment = 0;
   int stime = 0;
   int etime = 0;

   long sdate = 0;
   long edate = 0;
   long today = 0;

   try {
      id = Integer.parseInt(proid);
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   //
   //  Get the lesson time records
   //
   try {

      pstmt = con.prepareStatement (
              "SELECT * FROM lessontime5 WHERE proid = ? AND activity_id = ? AND lname = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1,id);
      pstmt.setInt(2,activity_id);
      pstmt.setString(3,ltname);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      if (rs.next()) {

         sdate = rs.getLong("sdate");
         stime = rs.getInt("stime");
         edate = rs.getLong("edate");
         etime = rs.getInt("etime");
         mon = rs.getInt("mon");
         tue = rs.getInt("tue");
         wed = rs.getInt("wed");
         thu = rs.getInt("thu");
         fri = rs.getInt("fri");
         sat = rs.getInt("sat");
         sun = rs.getInt("sun");
         color = rs.getString("color");
         fragment = rs.getInt("fragment");
      }
      pstmt.close();

      //
      //  Get today's date
      //
      Calendar cal = new GregorianCalendar();       // get todays date

      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH);
      int day = cal.get(Calendar.DAY_OF_MONTH);
      month++;                           // month starts at zero

      today = year * 10000;                        // create a today field of yyyymmdd (for today)
      today = today + (month * 100);
      today = today + day;                         // date = yyyymmdd (for comparisons)

      if (oldname.equals( "" )) {                  // if old name not provided
        
         oldname = ltname;                         // use new name
      }
        
      //
      //  Remove the old lesson times from the Lesson Book
      //
      pstmt = con.prepareStatement (
               "Delete FROM lessonbook5 " +
               "WHERE proid = ? AND activity_id = ? AND ltname = ? AND date >= ? AND memname = ?");

      pstmt.clearParameters();            
      pstmt.setInt(1, id);               // this pro
      pstmt.setInt(2, activity_id);      // this activity id
      pstmt.setString(3, oldname);       // this lesson time's old name (if changed)
      pstmt.setLong(4, today);           // today or later
      pstmt.setString(5, omit);          //  and empty
      pstmt.executeUpdate();       

      pstmt.close();

   }
   catch (Exception exc) {

      String errorMsg = "Error in Proshop_lesson (replaceLtime): Exception = " +exc;
      SystemUtils.logError(errorMsg);        // log the error message
      return;
   }

   //
   //  Now go rebuild the lesson book with the new parms
   //
   addLtimeSub(proid, activity_id, ltname, con);

 }


 // ****************************************************************************
 //  addLtimeSub - rebuild the lesson times in the lesson book after an edit or add
 // ****************************************************************************

 private void addLtimeSub(String proid, int activity_id, String ltname, Connection con) {

   PreparedStatement pstmt = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   String frags = "";
   String color = "";
   String error = "";
   String omit = "";

   boolean ltExist = false;

   int id = 0;
   int fragment = 0;
   int stime = 0;
   int etime = 0;
   int time = 0;
   int hr = 0;
   int min = 0;
   int sun = 0;
   int mon = 0;
   int tue = 0;
   int wed = 0;
   int thu = 0;
   int fri = 0;
   int sat = 0;

   long date = 0;
   long sdate = 0;
   long edate = 0;
   long today = 0;

   int [] days = new int [8];                  // array to hold each recurrence day (+1 to start at 0)
     
   days[0] = 0;

   try {
      id = Integer.parseInt(proid);
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   //
   //  Get today's date
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   int year = cal.get(Calendar.YEAR);
   int month = cal.get(Calendar.MONTH) +1;
   int day = cal.get(Calendar.DAY_OF_MONTH);
   int day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07)
     
   today = (year * 10000) + (month * 100) + day;     // create a today field of yyyymmdd (for today)

   
   //
   //  Get the lesson time records
   //
   try {

      pstmt = con.prepareStatement (
              "SELECT * FROM lessontime5 WHERE proid = ? AND activity_id = ? AND lname = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1,id);
      pstmt.setInt(2,activity_id);
      pstmt.setString(3,ltname);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      if (rs.next()) {

         sdate = rs.getLong("sdate");
         stime = rs.getInt("stime");
         edate = rs.getLong("edate");
         etime = rs.getInt("etime");
         days[2] = rs.getInt("mon");
         days[3] = rs.getInt("tue");
         days[4] = rs.getInt("wed");
         days[5] = rs.getInt("thu");
         days[6] = rs.getInt("fri");
         days[7] = rs.getInt("sat");
         days[1] = rs.getInt("sun");           // calendar starts with Sunday !!
         color = rs.getString("color");
         fragment = rs.getInt("fragment");
      }
      pstmt.close();

      //
      //  Add these lesson times to the lesson book according to the parameters defined
      //
      if (sdate > today) {         // if starting after today
        
         //
         //  start with sdate
         //
         long yy = sdate / 10000;                             // get year
         long mm = (sdate - (yy * 10000)) / 100;              // get month
         long dd = (sdate - (yy * 10000)) - (mm * 100);       // get day

         month = (int)mm;
         day = (int)dd;
         year = (int)yy;
           
      } else {
        
         sdate = today;        // we'll start with today
      }

      //
      //  Set the requested date to get the day name, etc.
      //
      cal = new GregorianCalendar();       // get today's date

      cal.set(Calendar.YEAR, year);                 // change to requested date
      cal.set(Calendar.MONTH, month-1);
      cal.set(Calendar.DAY_OF_MONTH, day);

      day_num = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)

      //
      //  Process one day at a time
      //
      while (sdate <= edate) {

         if (days[day_num] == 1) {         // if this day of the week was selected 
           
            //
            //  day selected, add 1 entry per time period requested
            //
            time = stime;     // start with first time period

            while (time < etime) {

               ltExist = false;       // init flag
                 
               pstmt = con.prepareStatement (
                 "SELECT proid FROM lessonbook5 WHERE proid = ? AND activity_id = ? AND date = ? AND time = ?");

               pstmt.clearParameters();        // clear the parms
               pstmt.setInt(1,id);
               pstmt.setInt(2,activity_id);
               pstmt.setLong(3,sdate);
               pstmt.setInt(4,time);
               rs = pstmt.executeQuery();      // execute the prepared pstmt

               if (rs.next()) {

                  ltExist = true;             // indicate lesson time already exists
               }                 

               pstmt.close();   // close the stmt


               if (ltExist == false) {       // if time does not already exist

                  //
                  //  Add the lesson time to the Lesson Book
                  //
                  pstmt = con.prepareStatement (
                    "INSERT INTO lessonbook5 (proid, activity_id, ltname, lgname, date, time, block, memname, " +
                    "memid, num, length, billed, in_use, ltype, phone1, phone2, color, notes, dayNum) " +
                    "VALUES (?,?,?,'',?,?,0,'','',0,?,0,0,'','','',?,'',?)");

                  pstmt.clearParameters();        // clear the parms
                  pstmt.setInt(1, id);
                  pstmt.setInt(2, activity_id);
                  pstmt.setString(3, ltname);
                  pstmt.setLong(4, sdate);
                  pstmt.setInt(5, time);
                  pstmt.setInt(6, fragment);
                  pstmt.setString(7, color);
                  pstmt.setInt(8, day_num);
                  pstmt.executeUpdate();          // execute the prepared stmt

                  pstmt.close();   // close the stmt
               }
                 
               //
               //  increment the time by length specified in 'fragment'
               //
               hr = time / 100;
               min = time - (hr * 100);
                 
               min = min + fragment;     // bump to next segment
                 
               while (min > 59) {
                 
                  hr++;                  // next hour
                  min = min - 60;        // adjust minutes
               }

               time = (hr * 100) + min;      // set new time

            }   // end of WHILE time

         }   // end of IF day selected
            
         //
         //  Do next day
         //
         cal.add(Calendar.DATE,1);            // add one day
         year = cal.get(Calendar.YEAR);
         month = cal.get(Calendar.MONTH) +1;
         day = cal.get(Calendar.DAY_OF_MONTH);
         day_num = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)

         sdate = (year * 10000) + (month * 100) + day;                    // new date

      }   // end of WHILE date range

      //
      //  Now reset any blockers for this pro
      //
      pstmt = con.prepareStatement (
              "SELECT * FROM lessonblock5 WHERE proid = ? AND activity_id = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1, id);
      pstmt.setInt(2, activity_id);
      rs = pstmt.executeQuery();      // execute the prepared stmt

      while (rs.next()) {

         sdate = rs.getInt("sdate");
         stime = rs.getInt("stime");
         edate = rs.getInt("edate");
         etime = rs.getInt("etime");
         mon = rs.getInt("mon");
         tue = rs.getInt("tue");
         wed = rs.getInt("wed");
         thu = rs.getInt("thu");
         fri = rs.getInt("fri");
         sat = rs.getInt("sat");
         sun = rs.getInt("sun");

         //
         //  Make sure the blocker is set in the lesson book
         //
         addBlocker(activity_id, sun, mon, tue, wed, thu, fri, sat, sdate, edate, stime, etime, con, id);

      }           // end of WHILE
      pstmt.close();

   }
   catch (Exception exc) {

      String errorMsg = "Error in Proshop_lesson (addLtimeSub): Exception = " +exc;
      SystemUtils.logError(errorMsg);        // log the error message
      return;
   }

 }


 // ****************************************************************************
 //  changeLTname - Change the name of the lesson times in the lesson book
 // ****************************************************************************

 private void changeLTname(String proid, int activity_id, String ltname, String oldname, Connection con) {

   PreparedStatement pstmt = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   int id = 0;

   long date = 0;
   long sdate = 0;
   long edate = 0;
   long today = 0;

   try {
      id = Integer.parseInt(proid);
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   //
   //  Get today's date
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   int year = cal.get(Calendar.YEAR);
   int month = cal.get(Calendar.MONTH);
   int day = cal.get(Calendar.DAY_OF_MONTH);

   month++;                           // month starts at zero

   today = year * 10000;                        // create a today field of yyyymmdd (for today)
   today = today + (month * 100);
   today = today + day;                         // date = yyyymmdd (for comparisons)

   //
   //  Get the start date from lesson time record
   //
   try {

      pstmt = con.prepareStatement (
              "SELECT sdate FROM lessontime5 WHERE proid = ? AND activity_id = ? AND lname = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1,id);
      pstmt.setInt(2,activity_id);
      pstmt.setString(3,ltname);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      if (rs.next()) {

         sdate = rs.getLong("sdate");
      }
      pstmt.close();

      //
      //  Determine the starting date - when to start changing the name in the lesson book
      //
      if (sdate < today) {         // if starting before today

         sdate = today;        // we'll start with today
      }
        
      //  
      //  Change the name of the lesson time in the lesson book for all entries beyond sdate
      //
      pstmt = con.prepareStatement (
        "UPDATE lessonbook5 SET ltname = ? " +
        "WHERE proid = ? AND activity_id = ? AND ltname = ? AND date >= ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, ltname);
      pstmt.setInt(2, id);
      pstmt.setInt(3, activity_id);
      pstmt.setString(4, oldname);
      pstmt.setLong(5, sdate);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

   }
   catch (Exception exc) {

      String errorMsg = "Error in Proshop_lesson (changeLTname): Exception = " +exc;
      SystemUtils.logError(errorMsg);        // log the error message
      return;
   }

 }


 // ****************************************************************************
 //  changeLTcolor - Change the color of the lesson times in the lesson book
 // ****************************************************************************

 private void changeLTcolor(String proid, int activity_id, String ltname, String color, Connection con) {

   PreparedStatement pstmt = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   int id = 0;

   long date = 0;
   long sdate = 0;
   long today = 0;

   try {
      id = Integer.parseInt(proid);
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   //
   //  Get today's date
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   int year = cal.get(Calendar.YEAR);
   int month = cal.get(Calendar.MONTH);
   int day = cal.get(Calendar.DAY_OF_MONTH);

   month++;                           // month starts at zero

   today = year * 10000;                        // create a today field of yyyymmdd (for today)
   today = today + (month * 100);
   today = today + day;                         // date = yyyymmdd (for comparisons)

   //
   //  Get the start date from lesson time record
   //
   try {

      pstmt = con.prepareStatement (
              "SELECT sdate FROM lessontime5 WHERE proid = ? AND activity_id = ? AND lname = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1,id);
      pstmt.setInt(2,activity_id);
      pstmt.setString(3,ltname);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      if (rs.next()) {

         sdate = rs.getLong("sdate");
      }
      pstmt.close();

      //
      //  Determine the starting date - when to start changing the color in the lesson book
      //
      if (sdate < today) {         // if starting before today

         sdate = today;        // we'll start with today
      }

      //
      //  Change the color of the lesson time in the lesson book for all entries beyond sdate
      //
      pstmt = con.prepareStatement (
        "UPDATE lessonbook5 SET color = ? " +
        "WHERE proid = ? AND activity_id = ? AND ltname = ? AND date >= ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, color);
      pstmt.setInt(2, id);
      pstmt.setInt(3, activity_id);
      pstmt.setString(4, ltname);
      pstmt.setLong(5, sdate);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

   }
   catch (Exception exc) {

      String errorMsg = "Error in Proshop_lesson (changeLTcolor): Exception = " +exc;
      SystemUtils.logError(errorMsg);        // log the error message
      return;
   }

 }


 // ********************************************************************
 //  Process the 'Request Lesson Time' from the Lesson Book
 // ********************************************************************

 private void reqTime(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException {

   PreparedStatement pstmt = null;
   ResultSet rs = null;

   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String user = (String)session.getAttribute("user");               // get name of user
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String memname = "";
   String memid = "";
   String ltype = "";
   String ltype_old = "";
   String phone1 = "";
   String phone2 = "";
   String ph1 = "";
   String ph2 = "";
   String notes = "";
   String color = "";
   String order_by = "";
   String caller = "";
   String ampm = "AM";
   String suppressEmails = "no";
   String skipDining = "no";
   String activity_name = "";

   int id = 0;
   int i = 0;
   int j = 0;
   int block = 0;
   int num = 0;
   int length = 0;
   int length2 = 0;
   int billed = 0;
   int in_use = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int month = 0;
   int day = 0;
   int year = 0;
   int timeNext = 0;
   int minsBetween = 0;
   int set_id = 0;
   int sheet_activity_id = 0;
   int auto_select_location = 1;

   long date = 0;
   long mm = 0;
   long dd = 0;
   long yy = 0;

   ArrayList<Integer> lessonSetIds = new ArrayList<Integer>();
   ArrayList<String> lessonSetNames = new ArrayList<String>();

   boolean b = false;
   //boolean hit = false;

   boolean enableAdvAssist = Utilities.enableAdvAssist(req);

   // Check proshop user feature access for appropriate access rights
   boolean diningAccess = SystemUtils.verifyProAccess(req, "DINING_REQUEST", con, out);

   String [] ltypeA = new String [40];            // save the pro's lesson types

   for (i=0; i<40; i++) {
      ltypeA[i] = "";                             // init the lesson type array
   }

   try {
      id = Integer.parseInt(proid);              // id of pro for this lesson book
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   // 
   //  Get the parms
   //
   String dates = req.getParameter("date");       // date of lesson book
   String calDate = req.getParameter("calDate");  // date of lesson book in String mm/dd/yyyy (for returns)
   String times = req.getParameter("time");       // time requested
   String jump = req.getParameter("jump");        // jump index for return
   String dayName = req.getParameter("day");          // name of the day in this book

   if (req.getParameter("caller") != null) {
       caller = req.getParameter("caller");
   }

   int lesson_id = Integer.parseInt(req.getParameter("lesson_id"));

   //
   //  Convert to ints
   //
   try {
      date = Long.parseLong(dates);
      time = Integer.parseInt(times);
      j = Integer.parseInt(jump);
   }
   catch (NumberFormatException e) {
   }

   //
   //  break down the date and time
   //
   yy = date / 10000;                             // get year
   mm = (date - (yy * 10000)) / 100;              // get month
   dd = (date - (yy * 10000)) - (mm * 100);       // get day

   month = (int)mm;
   day = (int)dd;
   year = (int)yy;

   hr = time / 100;
   min = time - (hr * 100);

   if (hr == 12) {
      ampm = "PM";
   } else {
      if (hr > 12) {
         hr = hr - 12;          // adjust
         ampm = "PM";
      }
   }

   //
   //  Get this year
   //
   Calendar cal = new GregorianCalendar();       // get todays date
   int thisYear = cal.get(Calendar.YEAR);            // get the year

   if (req.getParameter("set_id") != null) {

       try {
           set_id = Integer.parseInt(req.getParameter("set_id"));
       } catch (Exception exc) {
           set_id = 0;
       }
   }
   
   if (sess_activity_id > 0) {
       
       if (req.getParameter("returnToSlot") != null && req.getParameter("auto_select_location") != null) {
           auto_select_location = Integer.parseInt(req.getParameter("auto_select_location"));
       } else {
           try {

               pstmt = con.prepareStatement("SELECT auto_select_location FROM lessonpro5 WHERE id = ? AND activity_id = ?");
               pstmt.clearParameters();
               pstmt.setInt(1, id);
               pstmt.setInt(2, sess_activity_id);

               rs = pstmt.executeQuery();

               if (rs.next()) {
                   auto_select_location = rs.getInt("auto_select_location");
               }

           } catch (Exception e) {
               Utilities.logError("Proshop_lesson.reqTime - Error looking up auto_select_location option for pro (id: " + id + ") - Error: " + e.toString());
           } finally {
               Connect.close(rs, pstmt);
           }
       }
   }

   //
   //  Check if request is a Cancel - return without changes
   //
   if (req.getParameter("cancel") != null) {

      cancel(id, sess_activity_id, date, calDate, j, caller, set_id, out, req, lottery, con);       // process cancel request
      return;
   }
   
   if (req.getParameter("skipDining") != null) {    // user wish to skip dining prompt
       
       skipDining = req.getParameter("skipDining");
   }


   //******************************************************************
   //  Request is to edit a specific Lesson Time
   //******************************************************************
   //
   //   Check if this day is in use, if not, set it (set the whole day busy!!!)
   //
   if (req.getParameter("returnToSlot") == null) {
       
       try {

          in_use = verifyLesson.checkInUse(club, date, time, id, lesson_id, sess_activity_id, ltype, user, con);

       }
       catch (Exception e1) {

          out.println(SystemUtils.HeadTitle("DB Error"));
          out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
          out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
          out.println("<CENTER><BR><BR><H3>Database Access Error</H3>");
          out.println("<BR><BR>Unable to access the Database.");
          out.println("<BR>Please try again later.");
          out.println("<BR><BR>If problem persists, please contact customer support.");
          out.println("<BR><BR>" + e1.getMessage());
          out.println("<BR><BR>");
              out.println("<font size=\"2\">");
              out.println("<form action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
              out.println("<input type=\"hidden\" name=\"lesson\" value=\"yes\">");
              out.println("<input type=\"hidden\" name=\"proid\" value=" +id+ ">");
              out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
              out.println("<input type=\"hidden\" name=\"jump\" value=\"" +j+ "\">");
              if (!caller.equals("")) {
                  out.println("<input type=\"hidden\" name=\"caller\" value=\"" + caller + "\">");
                  out.println("<input type=\"hidden\" name=\"set_id\" value=\"" + set_id + "\">");
              }
              out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
              out.println("</form></font>");
          out.println("</CENTER></BODY></HTML>");
          out.close();
          return;
       }

       if (in_use != 0) {              // if time slot already in use

          out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
          out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
          out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
          out.println("<CENTER><BR><BR><H2>Lesson Time Slot Busy</H2>");
          out.println("<BR><BR>Sorry, but this lesson time slot is currently busy.<BR>");
          out.println("<BR>Please select another time or try again later.");
          out.println("<BR><BR>");
          out.println("<font size=\"2\">");
          out.println("<form action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
          out.println("<input type=\"hidden\" name=\"lesson\" value=\"yes\">");
          out.println("<input type=\"hidden\" name=\"proid\" value=" +id+ ">");
          out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
          out.println("<input type=\"hidden\" name=\"jump\" value=\"" +j+ "\">");
          if (!caller.equals("")) {
              out.println("<input type=\"hidden\" name=\"caller\" value=\"" + caller + "\">");
              out.println("<input type=\"hidden\" name=\"set_id\" value=\"" + set_id + "\">");
          }
          out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
          out.println("</form></font>");
          out.println("</CENTER></BODY></HTML>");
          out.close();
          return;
       }
   }
     
   //
   //  Get the current lesson book info for the time requested
   //
   
   boolean editLesson = false;
   
   try {

      pstmt = con.prepareStatement (
              "SELECT * FROM lessonbook5 WHERE proid = ? AND activity_id = ? AND date = ? AND time = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1,id);
      pstmt.setInt(2,sess_activity_id);
      pstmt.setLong(3,date);
      pstmt.setInt(4,time);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      if (rs.next()) {

         block = rs.getInt("block");
         memname = rs.getString("memname");
         memid = rs.getString("memid");
         num = rs.getInt("num");
         length = rs.getInt("length");
         billed = rs.getInt("billed");
         ltype = rs.getString("ltype");
         phone1 = rs.getString("phone1");
         phone2 = rs.getString("phone2");
         color = rs.getString("color");
         notes = rs.getString("notes");
         set_id = rs.getInt("set_id");
         sheet_activity_id = rs.getInt("sheet_activity_id");

         editLesson = !memname.equals("");
         ltype_old = ltype;
         
      } else {

         nfError(req, out, lottery);            // LT not found
         pstmt.close();
         return;
      }
      pstmt.close();

       //  If this is an empty slot, then check how far away the next occupied slot it
       pstmt = con.prepareStatement(
               "SELECT time FROM lessonbook5 WHERE proid = ? AND activity_id = ? AND date = ? AND time > ? AND ((memname != '' && memname != ?) OR lgname != '' OR block != 0) ORDER BY time");

       pstmt.clearParameters();        // clear the parms
       pstmt.setInt(1, id);
       pstmt.setInt(2, sess_activity_id);
       pstmt.setLong(3, date);
       pstmt.setInt(4, time);
       pstmt.setString(5, memname);

       rs = pstmt.executeQuery();      // execute the prepared pstmt

       if (rs.next()) {
           timeNext = rs.getInt("time");
       }
       pstmt.close();

       i = 0;

       if (timeNext > 0) {       // if we must check for room

           minsBetween = getMinTime(time, timeNext);      // get minutes between times
       }

      //
      //  Get the lesson types for this pro
      //
      if (club.equals("minikahda")) {
          order_by = " ORDER BY length, descript";
      } else if (club.equals("ccofnc")) {
          order_by = " ORDER BY length, ltname";
      } else {
          order_by = "";
      }

      pstmt = con.prepareStatement (
              "SELECT ltname, length FROM lessontype5 WHERE proid = ? AND activity_id = ?" + order_by);

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1,id);
      pstmt.setInt(2,sess_activity_id);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      while (rs.next()) {

         if (minsBetween > 0) {                 // if we must check for room

            length2 = rs.getInt("length");      // get this lesson type's length

            // If Charlotte CC, add a 30 minute buffer to the lesson type length
            if (club.equals("charlottecc") && sess_activity_id == 0) {
                length2 += 30;
            }

            if (length2 <= minsBetween) {       // if it will fit
              
               ltypeA[i] = rs.getString("ltname");   // add this one
               i++;
            }

         } else {

            ltypeA[i] = rs.getString("ltname");   // add this one
            i++;
         }
      }
      pstmt.close();

   }
   catch (Exception exc) {

      dbError(req, out, exc, lottery);
      return;
   }
   
   // if we're returning from another page, pull data from the request object to populate the form
   if (req.getParameter("returnToSlot") != null) {
  
         memname = req.getParameter("memname");
         memid = req.getParameter("memid");
         num = Integer.parseInt(req.getParameter("num"));
         length = Integer.parseInt(req.getParameter("ltlength"));
         billed = Integer.parseInt(req.getParameter("billed"));
         ltype = req.getParameter("ltype");
         phone1 = req.getParameter("phone1");
         phone2 = req.getParameter("phone2");
         notes = req.getParameter("notes");
         set_id = Integer.parseInt(req.getParameter("set_id"));
   }

   if (sheet_activity_id != 0) {
       activity_name = getActivity.getActivityName(sheet_activity_id, con);
   }
   
    // See if any lesson sets have been defined for this pro and activity_id and load them into ArrayLists for output later.
    try {

        lessonSetIds.clear();
        lessonSetNames.clear();

        pstmt = con.prepareStatement("SELECT set_id, name FROM lessonsets WHERE proid = ? AND activity_id = ?");
        pstmt.clearParameters();
        pstmt.setInt(1, id);
        pstmt.setInt(2, sess_activity_id);

        rs = pstmt.executeQuery();

        while (rs.next()) {

            lessonSetIds.add(rs.getInt("set_id"));
            lessonSetNames.add(rs.getString("name"));
        }

    } catch (Exception exc) {
        
        lessonSetIds.clear();
        lessonSetNames.clear();

    } finally {

        try { rs.close(); }
        catch (Exception ignore) { }

        try { pstmt.close(); }
        catch (Exception ignore) { }
    }
   
   //
   //    Make sure there is at least one lesson type - otherwise invalid selection (not enough time for any lesson types).
   //
   if (ltypeA[0].equals( "" )) {     // if no ltypes
      
      //
      //  Clear the in_use flag for the whole day
      //
      clearInUse(id, sess_activity_id, date, con);

      out.println(SystemUtils.HeadTitle("Invalid Lesson Selection"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><BR><BR><H2>Invalid Lesson Selection</H2>");
      out.println("<BR><BR>Sorry, but there is not enough time available for any lesson types.<BR>");
      out.println("<BR>Please select another time or try again later.");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"lesson\" value=\"yes\">");
      out.println("<input type=\"hidden\" name=\"proid\" value=" +id+ ">");
      out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" +j+ "\">");
      if (!caller.equals("")) {
          out.println("<input type=\"hidden\" name=\"caller\" value=\"" + caller + "\">");
          out.println("<input type=\"hidden\" name=\"set_id\" value=\"" + set_id + "\">");
      }
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;     
   }
   
   
   // Custom for Belle Meade - suppress emails always
   //
   if (club.equals("bellemeadecc")) {
      
      suppressEmails = "yes";
   }
   

   
   //
   //  Output a page to prompt for lesson time info
   //
   out.println(SystemUtils.HeadTitle2("Proshop - Edit Lesson Time"));

      //
      //*******************************************************************
      //  Erase player name and phone (erase button selected next to player's name)
      //*******************************************************************
      //
      out.println("<script language='JavaScript'>");            // Erase name script
      out.println("<!--");

      out.println("function erasename(pos1) {");

      out.println("document.playerform[pos1].value = '';");           // clear the player field
      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");          // End of script

      //
      //*******************************************************************
      //  Erase text area - (Notes)
      //*******************************************************************
      //
      out.println("<script language='JavaScript'>");             // Move Notes into textarea
      out.println("<!--");
      
      out.println("function movenotes() {");
      out.println(" var oldnotes = document.playerform.oldnotes.value;");
      out.println(" document.playerform.notes.value = oldnotes;");     // put notes in text area
      out.println("}");                                               // end of script function
      
      out.println("function erasetext(pos1) {");
      out.println(" document.playerform[pos1].value = '';");           // clear the text field
      out.println("}");                                               // end of script function
      
      out.println("function submitForm() {");
      out.println(" f = document.forms['playerform'];");
      
      out.println(" f.btnSubmit.value='Please Wait';");
      out.println(" f.btnSubmit.disabled=true;");
      out.println(" f.submit();");
      out.println("}");
      
      out.println("// -->");
      out.println("</script>");          // End of script

      //
      //*******************************************************************
      //  Move a member name into the tee slot
      //*******************************************************************
      //
      out.println("<script language='JavaScript'>");            // Move name script
      out.println("<!--");

      out.println("function movename(name) {");

      out.println("del = ':';");                                // deliminator is a colon
      out.println("array = name.split(del);");                  // split string into 3 pieces (name, ph1, ph2)
      out.println("var name = array[0];");                      // get the name
      out.println("var ph1 = array[1];");                       // get phone1
      out.println("var ph2 = array[2];");                       // get phone2

      out.println("if (ph1 == null) {");                        // if phone1 is empty
         out.println("ph1 = ''");
      out.println("}");
      out.println("if (ph2 == null) {");                        // if phone2 is empty
         out.println("ph2 = ''");
      out.println("}");
      
      out.println("var player1 = document.playerform.player1.value;");
        
         out.println("if (player1 == '') {");                    // if player1 is empty
            out.println("document.playerform.player1.value = name;");
              
            out.println("if (ph1 != '') {");                    // if phone1 exists
               out.println("document.playerform.phone1.value = ph1;");
            out.println("}");

            out.println("if (ph2 != '') {");                    // if phone2 exists
               out.println("document.playerform.phone2.value = ph2;");
            out.println("}");
         out.println("}");

      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");                               // End of script

   out.println("</HEAD>");
   out.println("<body onLoad=\"movenotes()\" bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\" topmargin=\"0\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" width=\"100%\" align=\"center\" valign=\"top\">");  // large table for whole page
   out.println("<tr><td valign=\"top\" align=\"center\">");

   out.println("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"CCCCAA\" align=\"center\" valign=\"top\">");
     out.println("<tr><td align=\"left\" width=\"300\">");
     out.println("<img src=\"/" +rev+ "/images/foretees.gif\" border=0>");
     out.println("</td>");

     String tmp_title = "Golf Shop Lesson Book";
     int root_id = 0;

     if (sess_activity_id > 0) {

         try { root_id = getActivity.getRootIdFromActivityId(sess_activity_id, con); }
         catch (Exception ignore) {}

         try { tmp_title = getActivity.getActivityName(root_id, con); }
         catch (Exception ignore) {}

         tmp_title += " Lesson Book";

     }

     out.println("<td align=\"center\">");
     out.println("<font size=\"5\">" + tmp_title + "</font>");
     out.println("</font></td>");

     out.println("<td align=\"center\" width=\"300\">");
     out.println("<font size=\"1\" color=\"#000000\">Copyright&nbsp;</font>");
     out.println("<font size=\"2\" color=\"#000000\">&#169;&nbsp;</font>");
     out.println("<font size=\"1\" color=\"#000000\">ForeTees, LLC <br> " +thisYear+ " All rights reserved.");
     out.println("</font><font size=\"3\">");
      out.println("<br><br><a href=\"/" +rev+ "/proshop_help.htm\" target=\"_blank\"><b>Help</b></a>");
     out.println("</font></td>");
   out.println("</tr></table>");

   out.println("<table border=\"0\" align=\"center\">");                           // table for main page
   out.println("<tr><td align=\"center\"><br>");

      out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"3\">");
         out.println("<tr>");
         out.println("<td width=\"630\" align=\"left\">");
         out.println("<font size=\"2\">");
            out.println("<b>Warning</b>:<br>");
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;You have approximately <b>6 minutes</b> to complete this task.");
            out.println("&nbsp;&nbsp;If you want to return without making any");
            out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;changes, <b>do not ");
            out.println("use your browser's BACK</b> button/option. ");
            out.println("Instead select the <b>Go Back</b> option below.");
         out.println("</font></td></tr>");
      out.println("</table>");

      out.println("<font size=\"2\"><br>");
      out.println("Date:&nbsp;&nbsp;<b>" + dayName + "&nbsp;&nbsp;" + month + "/" + day + "/" + year + "</b>");
        if (min < 10) {
           out.println(" &nbsp;&nbsp;&nbsp;&nbsp;Lesson Time:&nbsp;&nbsp;<b>" +hr+ ":0" +min+ " " +ampm+ "</b>");
        } else {
           out.println(" &nbsp;&nbsp;&nbsp;&nbsp;Lesson Time:&nbsp;&nbsp;<b>" +hr+ ":" +min+ " " +ampm+ "</b>");
        }
        out.println("</font>");

      out.println("<table border=\"0\" align=\"center\" cellpadding=\"5\" cellspacing=\"5\">"); // table to contain 3 tables below
         out.println("<tr>");
         out.println("<td valign=\"top\" align=\"center\">");         // col for Instructions and Go Back button

           out.println("<br><br><font size=\"1\">");
           out.println("<a href=\"#\" onClick=\"window.open ('/" +rev+ "/proshop_help_lesson_instruct.htm', 'newwindow', config='Height=460, width=500, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
           out.println("<img src=\"/" +rev+ "/images/instructions.gif\" border=0>");
           out.println("<br>Click for Help</a>");

           out.println("</font><font size=\"2\">");
           out.println("<br><br><br>");
           
           out.println("<form action=\"Proshop_lesson\" method=\"post\" name=\"can\">");
              out.println("<input type=\"hidden\" name=\"reqtime\" value=\"yes\">");
              out.println("<input type=\"hidden\" name=\"caller\" value=\"" + caller + "\">");
              out.println("<input type=\"hidden\" name=\"set_id\" value=\"" + set_id + "\">");
              out.println("<input type=\"hidden\" name=\"lesson_id\" value=" +lesson_id+ ">");
              out.println("<input type=\"hidden\" name=\"proid\" value=" +id+ ">");
              out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
              out.println("<input type=\"hidden\" name=\"date\" value=\"" +date+ "\">");
              out.println("<input type=\"hidden\" name=\"time\" value=\"" +time+ "\">");
              out.println("<input type=\"hidden\" name=\"jump\" value=\"" +j+ "\">");
              out.println("<input type=\"hidden\" name=\"day\" value=\"" +dayName+ "\">");
           out.println("Return<br>w/o Changes:<br>");
           out.println("<input type=\"submit\" value=\"Go Back\" name=\"cancel\"></form>");

         out.println("</font></td>");   // end of 1st col

            out.println("<form action=\"Proshop_lesson\" method=\"post\" name=\"playerform\">");
            out.println("<input type=\"hidden\" name=\"reqtime2\" value=\"yes\">");
            out.println("<input type=\"hidden\" name=\"caller\" value=" +caller+ ">");
            out.println("<input type=\"hidden\" name=\"lesson_id\" value=" +lesson_id+ ">");
            out.println("<input type=\"hidden\" name=\"proid\" value=" +id+ ">");
            out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" +date+ "\">");
            out.println("<input type=\"hidden\" name=\"time\" value=\"" +time+ "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" +j+ "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" +dayName+ "\">");
            out.println("<input type=\"hidden\" name=\"ltype_old\" value=\"" +ltype_old+ "\">");
            if (editLesson) out.println("<input type=\"hidden\" name=\"editLesson\">");

         out.println("<td align=\"center\" valign=\"top\">");    // col for main box

            out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\" cellpadding=\"5\">");  // table for player selection
            out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<b>Add or Change Member Lesson</b>");
            out.println("</font></td></tr>");

            out.println("<tr><td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("<p align=\"left\">&nbsp;&nbsp;Member:&nbsp;&nbsp;");
            out.println("<input type=\"text\" name=\"player1\" value=\"" +memname+ "\" size=\"20\" maxlength=\"30\">");
            out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player1')\" style=\"cursor:hand\">");

            out.println("</p><p align=\"left\">&nbsp;&nbsp;Lesson Type:&nbsp;&nbsp;");
              out.println("<select size=\"1\" name=\"ltype\">");
              for (i=0; i<40; i++) {          // list all lesson types
                if (!ltypeA[i].equals( "" )) {
                   if (ltype.equals( ltypeA[i] )) {
                      out.println("<option selected value=\"" +ltypeA[i]+ "\">" +ltypeA[i]+ "</option>");
                   } else {
                      out.println("<option value=\"" +ltypeA[i]+ "\">" +ltypeA[i]+ "</option>");
                   }
                }
              }
              out.println("</select>");
            out.println("</p>");
              
            if (!activity_name.equals("")) {
                out.println("<p align=\"left\">&nbsp;&nbsp;Current Location:&nbsp;&nbsp;" + activity_name + "</p>");
            }
            
            if (sess_activity_id != 0) {
                out.println("<div style=\"text-align:left;\">");
                out.println("&nbsp;&nbsp;How should a location be selected for this lesson?<br>");
                out.println("</div><div style=\"text-align:center;\">");
                out.println("<label><input type=\"radio\" name=\"auto_select_location\" value=\"1\"" + (auto_select_location == 1 ? " checked" : "") + ">&nbsp;Automatically</label>");
                out.println("&nbsp;<label><input type=\"radio\" name=\"auto_select_location\" value=\"0\"" + (auto_select_location == 0 ? " checked" : "") + ">&nbsp;Manually</label>");
                out.println("</div>");
            }
              
            out.println("<p align=\"left\">&nbsp;&nbsp;Phone 1:&nbsp;&nbsp;");
            out.println("<input type=\"text\" name=\"phone1\" value=\"" +phone1+ "\" size=\"20\" maxlength=\"24\">");
            out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('phone1')\" style=\"cursor:hand\">");
            out.println("</p>");

            out.println("<p align=\"left\">&nbsp;&nbsp;Phone 2:&nbsp;&nbsp;");
            out.println("<input type=\"text\" name=\"phone2\" value=\"" +phone2+ "\" size=\"20\" maxlength=\"24\">");
            out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('phone2')\" style=\"cursor:hand\">");
            out.println("</p>");

            //
            //   Notes
            //
            //   Script will put any existing notes in the textarea (value= doesn't work)
            //
            out.println("<input type=\"hidden\" name=\"oldnotes\" value=\"" + notes + "\">"); // hold notes for script

            out.println("&nbsp;&nbsp;Notes:&nbsp;&nbsp;");
            out.println("<textarea name=\"notes\" value=\"\" id=\"notes\" cols=\"24\" rows=\"3\">");
            out.println("</textarea>");
            out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasetext('notes')\" style=\"cursor:hand\">");

            out.println("<br><br><hr size=\"1\" width=\"75%\">");

            if (!editLesson) {

                // Output checkbox for recurring lesson time option.  If checked, recurr options will appear.
                out.println("<p align=\"left\"><font size=\"2\">&nbsp;&nbsp;Is this a recurring lesson?</font>");
                out.println("<input type=\"checkbox\" name=\"isrecurr\"><font size=\"1\">&nbsp;(checked = yes" + (sess_activity_id != 0 ? ", locations will be auto-selected" : "") + ")</font>");
                out.println("</p>");

                ////////////////////////////////////   Hide behind checkbox script

                out.println("<p align=\"left\">&nbsp;&nbsp;Recurrence:&nbsp;&nbsp;");
                out.println("<select size=\"1\" name=\"eoweek\">");
                out.println("<option value=\"0\">Every Week</option>");
                out.println("<option value=\"1\">Every Other Week</option>");
                out.println("</select></p>");

                Common_Config.displayEndDate(mm, dd, yy, out);

                ////////////////////////////////////   Hide behind checkbox script

                out.println("<hr size=\"1\" width=\"75%\">");
            }
            
            out.println("<p align=\"left\">&nbsp;&nbsp;Include this lesson in a Lesson Set (optional)?<p>");

            if (lessonSetIds.size() > 0) {

                out.println("<p align=\"left\">&nbsp;&nbsp;Select Existing Lesson Set:&nbsp;&nbsp;");
                out.println("<select size=\"1\" name=\"set_id\">");

                out.println("<option " + (set_id == 0 ? "selected " : "") + "value=\"0\">None</option>");
                
                for (int k=0; k<lessonSetIds.size(); k++) {
                    
                    out.println("<option " + (set_id == lessonSetIds.get(k) ? "selected " : "") + " value=\"" + lessonSetIds.get(k) + "\">" + lessonSetNames.get(k) + "</option>");
                }

                out.println("</select></p>");
                out.println("Or");
            }

            out.println("<p align=\"left\">&nbsp;&nbsp;Create New Lesson Set:&nbsp;&nbsp;");
            out.println("<input type=\"text\" name=\"new_lesson_set\" maxlength=\"40\" size=\"20\" value=\"\">");
            out.println("</p>");

            out.println("<hr size=\"1\" width=\"75%\">");

            out.println("<br>");
            out.println("Suppress email notification?:&nbsp;&nbsp; ");
            if (suppressEmails.equalsIgnoreCase( "yes" )) {
               out.println("<input type=\"checkbox\" checked name=\"suppressEmails\" value=\"Yes\">");
            } else {
               out.println("<input type=\"checkbox\" name=\"suppressEmails\" value=\"Yes\">");
            }
            out.println("</font><font size=\"1\">&nbsp;(checked = yes)</font><font size=\"2\">");
            out.println("<br><br>");
            
            if (Utilities.checkDiningLink("pro_lesson", con) && diningAccess) {
                out.println("Skip dining request prompt?:&nbsp;&nbsp; ");
                if (skipDining.equalsIgnoreCase( "yes" )) {
                   out.println("<input type=\"checkbox\" checked name=\"skipDining\" value=\"Yes\">");
                } else {
                   out.println("<input type=\"checkbox\" name=\"skipDining\" value=\"Yes\">");
                }
                out.println("</font><font size=\"1\">&nbsp;(checked = yes)<br><br></font><font size=\"2\">");
            } else {
                out.println("<input type=\"hidden\" name=\"skipDining\" value=\"yes\">");
            }

            //out.println("<input type=submit value=\"Submit\" name=\"submit\">");
            out.println("<input type=button value=\"Submit\" name=\"btnSubmit\" onclick=\"submitForm();\">");
            out.println("</font></td></tr>");
            out.println("</table>");
            if (editLesson) out.println("<p align=\"center\"><input type=submit value=\"Cancel Lesson\" name=\"remove\"></p>");
         out.println("</td>");
         out.println("<td valign=\"top\">");      // col for spacer
         out.println("&nbsp;</td>");

         out.println("<td valign=\"top\">");      // col for name list

         out.println("<table border=\"1\" width=\"140\" bgcolor=\"#F5F5DC\" valign=\"top\">");      // name list
         out.println("<tr><td align=\"center\" bgcolor=\"#336633\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<b>Member List</b>");
               out.println("</font></td>");
         out.println("</tr>");
         
         // include the dynamic search box scripts
         if (enableAdvAssist) {

             //out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/dyn-search.js\"></script>");
             Utilities.proSlotScripts(out);
         
             // output dynamic search box
             out.println("<tr>");
             out.println("<td align=\"center\">");
             out.println("<input type=text name=DYN_search onkeyup=\"DYN_triggerChange()\" onclick=\"this.select()\" value=\"Quick Search Box\">");
             out.println("</td></tr>");

         }

         out.println("<tr><td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("Click on name to add");
            out.println("</font></td></tr>");
         
         // ********************************************************************************
         //    build the name list.
         // ********************************************************************************
         String first = "";
         String mid = "";
         String last = "";
         String name = "";
         String dname = "";
         String mnum = "";
         String username = "";
         
         try {

             PreparedStatement stmt2 = con.prepareStatement(
                     "SELECT name_last, name_first, name_mi, phone1, phone2, memNum, username FROM member2b "
                     + "WHERE inact = 0 "
                     + "ORDER BY name_last, name_first, name_mi");

            stmt2.clearParameters();               // clear the parms
            rs = stmt2.executeQuery();             // execute the prepared stmt

            out.println("<tr><td align=\"left\"><font size=\"2\">");
            //out.println("<select size=\"22\" name=\"bname\" onClick=\"movename(this.form.bname.value)\" style=\"cursor:hand\">");

            out.print("<select size=\"22\" name=\"bname\"");

            if (enableAdvAssist) {

                out.print(" onclick=\"movename(this.form.bname.value)\" onkeypress=\"DYN_moveOnEnterKey(event); return false\">");

            } else {

                out.print(" onchange=\"movename(this.form.bname.value)\"");

            }

            out.println(">");

            while(rs.next()) {

               last = rs.getString(1);
               first = rs.getString(2);
               mid = rs.getString(3);
               ph1 = rs.getString(4);
               ph2 = rs.getString(5);
               mnum = rs.getString("memNum");
               username = rs.getString("username");

               if (mid.equals("")) {

                  name = first + " " + last;
                  dname = last + ", " + first;
               } else {

                  name = first + " " + mid + " " + last;
                  dname = last + ", " + first + " " + mid;
               }

               //
               //  Add Member Number for all other clubs
               //
               if (!mnum.equals("")) {
                   
                   // Add username instead of mnum for Charlotte CC
                   if (club.equals("charlottecc") || club.equals("loxahatchee")) {
                       dname = dname + " " + username;
                   } else {
                       dname = dname + " " + mnum;        // add mnum for display - do not move to slot
                   }
               }
               
               name = name+ ":" +ph1+ ":" +ph2;              // combine name & phones for script

               out.println("<option value=\"" +name+ "\">" + dname + "</option>");
            }
            out.println("</select>");
            out.println("</font></td></tr>");

            stmt2.close();
         }
         catch (Exception ignore) {

         }

         out.println("</table>");                // end of name list table
      out.println("</td>");                                      // end of name list column
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

 }


 // ********************************************************************
 //  Process the 'Update Lesson Time' from reqTime above (submit)
 // ********************************************************************

 private void reqTime2(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException {

   Statement stmt = null;
   PreparedStatement pstmt = null;
   PreparedStatement pstmt2 = null;
   PreparedStatement pstmt3 = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String user = (String)session.getAttribute("user");               // get name of user
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String ltname = "";
   String ltimename = "";
   String memname2 = "";
   String lname = "";
   String fname = "";
   String mi = "";
   String memid = "";
   String color = "";
   String error = "";
   String ampm = "AM";
   String s_ampm = "AM";
   String e_ampm = "AM";
   String caller = "";

   String oldmemname = "";       // old lesson time values
   String oldmemid = "";
   String oldltype = "";
   String oldphone1 = "";
   String oldphone2 = "";
   String oldnotes = "";
   String proName = "";
   String memname = "";
   String ltype = "";
   String ltype_old = "";
   String locations_csv = "";
   String suppressEmails = "no";
   String skipDining = "no";
   String activity_name = "";

   int id = 0;
   int i = 0;
   int j = 0;
   int ltlength = 0;
   int oldlength = 0;
   int num = 0;
   int length = 0;
   int billed = 0;
   int in_use = 0;
   int fragment = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int etime = 0;
   int month = 0;
   int day = 0;
   int year = 0;
   int sendemail = 0;
   int emailNew = 0;
   int emailMod = 0;
   int emailCan = 0;
   int lesson_id = 0;
   int lesson_id2 = 0;
   int sheet_activity_id = 0;
   int dayNum = 0;
   int set_id = 0;
   int old_set_id = 0;
   int recurr_eoweek = 0;
   int recurr_mm = 0;
   int recurr_dd = 0;
   int recurr_yy = 0;
   int recurr_edate = 0;
   int autoblock_times = 1;
   int auto_select_location = 1;

   long date = 0;
   long mm = 0;
   long dd = 0;
   long yy = 0;

   boolean b = false;
   boolean hit = false;
   boolean isRecurr = false;
   boolean editLesson = false;
   boolean lessonBooked = false;
   boolean autoSelectLocation = true;
   boolean returnFromLocSel = false;

   List<Integer> sheet_ids = new ArrayList<Integer>();
   List<Integer> recurr_dates = new ArrayList<Integer>();
   List<Integer> recurr_unavail = new ArrayList<Integer>();
   List<String> recurr_activity_names = new ArrayList<String>();
   List<List<Integer>> recurr_recids = new ArrayList<List<Integer>>();
   List<List<Integer>> recurr_sheet_ids = new ArrayList<List<Integer>>();
   
   Map<Integer, List<Integer>> sheet_id_map = new LinkedHashMap<Integer, List<Integer>>();
   
   // Check proshop user feature access for appropriate access rights
   boolean diningAccess = SystemUtils.verifyProAccess(req, "DINING_REQUEST", con, out);

   try {
      id = Integer.parseInt(proid);              // id of pro for this lesson book
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   //
   //  Get the parms
   //
   String dates = req.getParameter("date");       // date of lesson book
   String calDate = req.getParameter("calDate");  // date of lesson book in String mm/dd/yyyy (for returns)
   String times = req.getParameter("time");       // time requested
   String jump = req.getParameter("jump");        // jump index for return
   String dayName = req.getParameter("day");          // name of the day in this book
   String phone1 = req.getParameter("phone1");
   String phone2 = req.getParameter("phone2");  
   String notes = req.getParameter("notes");
   lesson_id = Integer.parseInt(req.getParameter("lesson_id"));
   ltype_old = req.getParameter("ltype_old");
   
   if (req.getParameter("editLesson") != null) editLesson = true;
   if (req.getParameter("auto_select_location") != null) auto_select_location = Integer.parseInt(req.getParameter("auto_select_location"));

   if (req.getParameter("player1") != null) {         // if player provided

      memname = req.getParameter("player1");         
   }
   
   if (req.getParameter("ltype") != null) {          // if lesson type provided

      ltype = req.getParameter("ltype");
   }

   if (req.getParameter("caller") != null) {
      caller = req.getParameter("caller");
   }

   if (req.getParameter("isrecurr") != null) {       // if selected as a recurring lesson.

       isRecurr = true;

       recurr_eoweek = Integer.parseInt(req.getParameter("eoweek"));

       recurr_mm = Integer.parseInt(req.getParameter("emonth"));
       recurr_dd = Integer.parseInt(req.getParameter("eday"));
       recurr_yy = Integer.parseInt(req.getParameter("eyear"));

       recurr_edate = (recurr_yy * 10000) + (recurr_mm * 100) + recurr_dd;

       // Set the number of the day of the week based on the passed dayName value
       if (dayName.equals("Sunday")) {
           dayNum = 1;
       } else if (dayName.equals("Monday")) {
           dayNum = 2;
       } else if (dayName.equals("Tuesday")) {
           dayNum = 3;
       } else if (dayName.equals("Wednesday")) {
           dayNum = 4;
       } else if (dayName.equals("Thursday")) {
           dayNum = 5;
       } else if (dayName.equals("Friday")) {
           dayNum = 6;
       } else if (dayName.equals("Saturday")) {
           dayNum = 7;
       }
   }

   if (req.getParameter("set_id") != null) {         // if existing lesson set was selected

       try {
           set_id = Integer.parseInt(req.getParameter("set_id"));
       } catch (Exception exc) {
           set_id = 0;
       }
   }
   
   if (req.getParameter("returnFromLocSel") != null) {
       returnFromLocSel = true;
   }
   
   // If we're returning from the manual location selection page, we need to parse out the sheet_ids they picked
   if (returnFromLocSel) {
       
       if (req.getParameter("sheet_id_select") != null && !req.getParameter("sheet_id_select").equals("")) {
                  
           String [] temp_sheet_ids = req.getParameter("sheet_id_select").split("\\|");
           
           int temp_sheet_id = 0;
           
           for (int k = 0; k < temp_sheet_ids.length; k++) {
               
               try {
                   temp_sheet_id = Integer.parseInt(temp_sheet_ids[k]);
               } catch (Exception e) {
                   temp_sheet_id = 0;
               }
               
               if (temp_sheet_id > 0) {
                   sheet_ids.add(temp_sheet_id);
               }
           }
       } else {
           
           // No sheet_id values were returned from the location select page!
           out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
           out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
           out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
           out.println("<CENTER><BR><BR><H3>Error Encountered</H3>");
           out.println("<BR><BR>Sorry, but an error was encountered while trying to reserve the selected lesson location.<BR>");
           out.println("<BR>Please return to the lesson book and try again.");
           out.println("<BR><BR>If the problem persists, please contact customer support.");
           out.println("<BR><BR>");
           out.println("<font size=\"2\">");
           out.println("<form action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
           out.println("<input type=\"hidden\" name=\"lesson\" value=\"yes\">");
           out.println("<input type=\"hidden\" name=\"proid\" value=" + id + ">");
           out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
           out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");
           if (!caller.equals("")) {
               out.println("<input type=\"hidden\" name=\"caller\" value=\"" + caller + "\">");
               out.println("<input type=\"hidden\" name=\"set_id\" value=\"" + set_id + "\">");
           }
           out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
           out.println("</form></font>");
           out.println("</CENTER></BODY></HTML>");
           out.close();
           return;
       }
   }

   if (set_id == 0 && !returnFromLocSel && req.getParameter("new_lesson_set") != null && !req.getParameter("new_lesson_set").equals("")) {      // If a name was entered to create a new lesson set (ignore if existing set was also selected)

       String setName = req.getParameter("new_lesson_set");
       int count = 0;

       if (!SystemUtils.scanName(setName)) {        // if no special characters found
           
           try {

               pstmt = con.prepareStatement("INSERT INTO lessonsets (proid, activity_id, name, description) VALUES (?,?,?,'')");
               pstmt.clearParameters();
               pstmt.setInt(1, id);
               pstmt.setInt(2, sess_activity_id);
               pstmt.setString(3, setName);

               count = pstmt.executeUpdate();

               if (count > 0) {

                   try {

                       stmt = con.createStatement();
                       rs = stmt.executeQuery("SELECT LAST_INSERT_ID() as set_id FROM lessonsets");

                       if (rs.next()) {
                           set_id = rs.getInt("set_id");
                       }

                   } catch (Exception exc) {

                       set_id = 0;
                       Utilities.logError("Proshop_lesson.reqTime2 - " + club + " - Error getting set_id for newly created lesson set - Err: " + exc.toString());

                   } finally {

                       try { rs.close(); }
                       catch (Exception ignore) { }

                       try { stmt.close(); }
                       catch (Exception ignore) { }
                   }
               }

           } catch (Exception exc) {

               set_id = 0;
               Utilities.logError("Proshop_lesson.reqTime2 - " + club + " - Error creating new lesson set - Err: " + exc.toString());

           } finally {

               try { pstmt.close(); }
               catch (Exception ignore) { }
           }
       } else {

           out.println(SystemUtils.HeadTitle("Data Entry Error"));
           out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
           out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
           out.println("<CENTER><BR><BR><H3>Data Entry Error</H3>");
           out.println("<BR><BR>Invalid characters found.<BR>");
           out.println("<BR>Special characters are not allowed in Lesson Set names. Please remove any special characters and try again.");
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
   
   if (req.getParameter("suppressEmails") != null) {             // if email parm exists
       
      suppressEmails = req.getParameter("suppressEmails");
   }
   
   if (req.getParameter("skipDining") != null) {
       
      skipDining = "yes";
   }
   
   //
   //  Convert to ints
   //
   try {
      date = Long.parseLong(dates);
      time = Integer.parseInt(times);
      j = Integer.parseInt(jump);
   }
   catch (NumberFormatException e) {
   }

   //
   //  break down the date and time
   //
   yy = date / 10000;                             // get year
   mm = (date - (yy * 10000)) / 100;              // get month
   dd = (date - (yy * 10000)) - (mm * 100);       // get day

   month = (int)mm;
   day = (int)dd;
   year = (int)yy;

   hr = time / 100;
   min = time - (hr * 100);

   if (hr == 12) {
      ampm = "PM";
   } else {
      if (hr > 12) {
         hr = hr - 12;          // adjust
         ampm = "PM";
      }
   }

   //
   //  Get the length of Notes (max length of 254 chars)
   //
   int notesL = 0;

   if (!notes.equals( "" ) && notes != null) {

      notesL = notes.length();       // get length of notes
   }

   //
   //   Check if still in use (should be)
   //
   try {

      pstmt = con.prepareStatement (
         "SELECT * FROM lessonbook5 WHERE recid = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1, lesson_id);
      rs = pstmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         oldmemname = rs.getString("memname");
         oldmemid = rs.getString("memid");
         oldlength = rs.getInt("length");
         billed = rs.getInt("billed");
         in_use = rs.getInt("in_use");
         oldltype = rs.getString("ltype");
         oldphone1 = rs.getString("phone1");
         oldphone2 = rs.getString("phone2");
         oldnotes = rs.getString("notes");
         ltimename = rs.getString("ltname");
         sheet_activity_id = rs.getInt("sheet_activity_id");
      }
      pstmt.close();

   }
   catch (Exception e1) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, please contact customer support.");
      out.println("<BR><BR>" + e1.getMessage());
      out.println("<BR><BR>");
      if (caller.equals("")) {
          out.println("<font size=\"2\">");
          out.println("<form action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
          out.println("<input type=\"hidden\" name=\"lesson\" value=\"yes\">");
          out.println("<input type=\"hidden\" name=\"proid\" value=" +id+ ">");
          out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
          out.println("<input type=\"hidden\" name=\"jump\" value=\"" +j+ "\">");
      if (!caller.equals("")) {
          out.println("<input type=\"hidden\" name=\"caller\" value=\"" + caller + "\">");
          out.println("<input type=\"hidden\" name=\"set_id\" value=\"" + set_id + "\">");
      }
          out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
          out.println("</form></font>");
      }
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   if (in_use == 0) {              // if slot no longer in use

      out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><BR><BR><H3>Reservation Timer Expired</H3>");
      out.println("<BR><BR>Sorry, but this lesson time slot has been returned to the system.<BR>");
      out.println("<BR>The system timed out and released the lesson time.");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"lesson\" value=\"yes\">");
      out.println("<input type=\"hidden\" name=\"proid\" value=" +id+ ">");
      out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" +j+ "\">");
      if (!caller.equals("")) {
          out.println("<input type=\"hidden\" name=\"caller\" value=\"" + caller + "\">");
          out.println("<input type=\"hidden\" name=\"set_id\" value=\"" + set_id + "\">");
      }
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   //
   //  Get the name of this pro
   //
   try {

      pstmt = con.prepareStatement (
              "SELECT lname, fname, mi, suffix FROM lessonpro5 WHERE id = ? AND activity_id = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1, id);
      pstmt.setInt(2, sess_activity_id);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      if (rs.next()) {

         StringBuffer pro_name = new StringBuffer(rs.getString("fname"));  // get first name

         mi = rs.getString("mi");                                          // middle initial
         if (!mi.equals( "" )) {
            pro_name.append(" ");
            pro_name.append(mi);
         }
         pro_name.append(" " + rs.getString("lname"));                   // last name

         String suffix = rs.getString("suffix");                         // suffix
         if (!suffix.equals( "" )) {
            pro_name.append(" ");
            pro_name.append(suffix);
         }

         proName = pro_name.toString();                             // convert to one string

      }
      pstmt.close();

   }
   catch (Exception exc) {
   }


   //
   //  If request is to 'Cancel This Res', then clear all fields for this slot
   //
   if (req.getParameter("remove") != null) {

      memname = "";
      memid = "";
      ltype = "";
      phone1 = "";
      phone2 = "";
      notes = "";
      num = 0;                
      ltlength = 0;
      billed = 0;
      old_set_id = set_id;  // save for rerouting user at end of process
      set_id = 0;
      sheet_activity_id = 0;

      emailCan = 1;      // send email notification for Cancel Request
      sendemail = 1;

      // If an activity other than golf, remove lesson_id value from time sheets for this lesson
      if (sess_activity_id != 0) {
          verifyLesson.clearLessonFromActSheets(lesson_id, req);
      }

   } else {   // not a cancel request  

      //
      //  Process normal res request
      //
      //  Make sure minimum values provided
      //
      if (memname.equals( "" ) || memname == null || ltype.equals( "" ) || ltype == null) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<CENTER><BR><BR><H3>Data Entry Error</H3>");
         out.println("<BR><BR>Required information not provided.<BR>");
         out.println("<BR>You must provide a member name (or activity) and a lesson type.");
         out.println("<BR><BR>");
         out.println("If you are attempting to cancel an existing lesson, please use the 'Cancel Lesson' button.");
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
      //  Parse the memname value to see if it is a member name
      //
      StringTokenizer tok = new StringTokenizer( memname );     // space is the default token

      if (tok.countTokens() > 1 && tok.countTokens() < 4) {     

         if ( tok.countTokens() == 2 ) {         // first name, last name

            fname = tok.nextToken();
            lname = tok.nextToken();
            mi = "";
         }

         if ( tok.countTokens() == 3 ) {         // first name, mi, last name

            fname = tok.nextToken();
            mi = tok.nextToken();
            lname = tok.nextToken();
         }
           
         //
         //  Get the member's username (id)
         //
         try {

            pstmt = con.prepareStatement (
               "SELECT username " +
               "FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ?");

            pstmt.clearParameters();        // clear the parms
            pstmt.setString(1, lname);
            pstmt.setString(2, fname);
            pstmt.setString(3, mi);
            rs = pstmt.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               memid = rs.getString(1);
            }
            pstmt.close();

         }
         catch (Exception e1) {
         }

      } else {    // wrong number of tokens

         memid = "";         // not a member
      }

      //
      //  Get the lesson type info for the lesson type requested
      //
      try {

         //
         //  Get the lesson types for this pro
         //
         pstmt = con.prepareStatement (
                 "SELECT length, locations, autoblock_times FROM lessontype5 WHERE proid = ? AND activity_id = ? AND ltname = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1,id);
         pstmt.setInt(2,sess_activity_id);
         pstmt.setString(3,ltype);
         rs = pstmt.executeQuery();      // execute the prepared pstmt

         if (rs.next()) {

            ltlength = rs.getInt("length");
            locations_csv = rs.getString("locations");
            autoblock_times = rs.getInt("autoblock_times");
         }
         pstmt.close();

      }
      catch (Exception exc) {

         dbError(req, out, exc, lottery);
         return;
      }

      // If we're returning from manually selecting a lesson location, skip past this portion since we already know what sheet_ids we want to book up.
      if (!returnFromLocSel) {
      
          // If this is a recurring lesson booking, look up all related dates and times for lessonbook availability
          if (isRecurr) {

              String recurr_string = "";

              if (recurr_eoweek == 1) {
                  recurr_string = "AND MOD(DATE_FORMAT(DATE, '%U'), 2) = MOD(DATE_FORMAT(?, '%U'), 2) ";
              }

              etime = getEndTime(time, ltlength);

              // First get list of all applicable dates
              try {

                  pstmt = con.prepareStatement("SELECT date FROM lessonbook5 WHERE proid = ? AND activity_id = ? AND date >= ? AND date <= ? AND dayNum = ? " + recurr_string + "GROUP BY date ORDER BY date");
                  pstmt.clearParameters();
                  pstmt.setInt(1, id);
                  pstmt.setInt(2, sess_activity_id);
                  pstmt.setLong(3, date);
                  pstmt.setInt(4, recurr_edate);
                  pstmt.setInt(5, dayNum);

                  if (recurr_eoweek == 1) {
                      pstmt.setLong(6, date);
                  }

                  rs = pstmt.executeQuery();

                  while (rs.next()) {
                      recurr_dates.add(rs.getInt("date"));
                  }

              } catch (Exception exc) {

                  Utilities.logError("Proshop_lesson.reqTime2 - " + club + " - Error looking up recurr dates - Err: " + exc.toString());

              } finally {

                  try { rs.close(); }
                  catch (Exception ingore) { }

                  try { pstmt.close(); }
                  catch (Exception ignore) { }
              }

              // If one or more dates were returned, loop through dates and see if necessary times are available and flag unavailble days as such.
              if (recurr_dates.size() > 0) {

                  for (int k=0; k<recurr_dates.size(); k++) {

                      recurr_recids.add(new ArrayList<Integer>());
                      recurr_unavail.add(0);

                      if (sess_activity_id != 0) {
                          recurr_sheet_ids.add(new ArrayList<Integer>());
                          recurr_activity_names.add("");
                      }

                      try {

                          pstmt = con.prepareStatement("" +
                                  "SELECT recid, in_use, memname, block " +
                                  "FROM lessonbook5 " +
                                  "WHERE proid = ? AND activity_id = ? AND date = ? AND time >= ? AND time < ? AND dayNum = ? " +
                                  "ORDER BY time");
                          pstmt.clearParameters();
                          pstmt.setInt(1, id);
                          pstmt.setInt(2, sess_activity_id);
                          pstmt.setInt(3, recurr_dates.get(k));
                          pstmt.setInt(4, time);
                          pstmt.setInt(5, etime);
                          pstmt.setInt(6, dayNum);

                          rs = pstmt.executeQuery();

                          while (rs.next()) {

                              // See if this lesson time slot is available to be booked
                              if ((rs.getInt("in_use") > 0 && (long)recurr_dates.get(k) != date) || !rs.getString("memname").equals("") || rs.getInt("block") > 0) {

                                  // Time is not available and lesson can't be booked this day.  Clear collected recids and set day not available in list.
                                  recurr_recids.get(k).clear();
                                  recurr_unavail.set(k, 1);

                              } else {      

                                  // Time is available, add recid to list for this date
                                  recurr_recids.get(k).add(rs.getInt("recid"));
                              }
                          }

                      } catch (Exception exc) {

                          recurr_unavail.set(k, -1);
                          Utilities.logError("Proshop_lesson.reqTime2 - " + club + " - Error checking recurr dates for available times - Err: " + exc.toString());

                      } finally {

                          try { rs.close(); }
                          catch (Exception ignore) { }

                          try { pstmt.close(); }
                          catch (Exception ignore) { }
                      }

                      // Finished gathering recids for this date. If date is available, set collected recids in use.
                      if (recurr_unavail.get(k) == 0) {

                          if (recurr_recids.get(k).size() > 0) {

                              for (int m=0; m<recurr_recids.get(k).size(); m++) {

                                  try {

                                      pstmt = con.prepareStatement("UPDATE lessonbook5 SET in_use = 1 WHERE recid = ?");
                                      pstmt.clearParameters();
                                      pstmt.setInt(1, recurr_recids.get(k).get(m));

                                      pstmt.executeUpdate();

                                  } catch (Exception exc) {

                                      recurr_unavail.set(k, -1);
                                      Utilities.logError("Proshop_lesson.reqTime2 - " + club + " - Error checking available recurr recids in use - Err: " + exc.toString());

                                  } finally {

                                      try { pstmt.close(); }
                                      catch (Exception ignore) { }
                                  }
                              }

                              // If in an activity other than golf, check activity sheets for an empty chunk of slots to cover the lesson time
                              if (sess_activity_id != 0 && !locations_csv.equals("") && autoblock_times == 1) {

                                  int time_start = time;
                                  int time_end = getEndTime(time, ltlength);

                                  boolean bookAllSheets = false;
                                  autoSelectLocation = true;

                                  sheet_id_map = verifyLesson.checkActivityTimes(recurr_recids.get(k).get(0), sess_activity_id, locations_csv, recurr_dates.get(k), recurr_dates.get(k), 
                                          new int[1], time, etime, bookAllSheets, autoSelectLocation, con, out);

                                  if (sheet_id_map.get(0) != null) {
                                      sheet_ids = sheet_id_map.get(0);
                                  }

                                  // If time slots returned, copy the arraylist of ids into the recurr arraylist
                                  if (sheet_ids.size() > 0) {

                                      recurr_sheet_ids.add(k, sheet_ids);

                                  } else {  // If no slots returned, clear the recids arraylist, flag the date as unavailable, and clear any in_use flags for that day

                                      recurr_recids.get(k).clear();
                                      recurr_unavail.set(k, 2);

                                      clearInUse(id, sess_activity_id, recurr_dates.get(k), con);    // clear in use flags for the day
                                  }
                              }

                          } else {

                              // If no recids were found on a given date, set that date unavailable
                              recurr_unavail.set(k, 1);
                          }
                      }
                  }     // End date loop
              }

          } else {    // Non-recurring Lesson
             
              // If in an activity other than golf, check activity sheets for an empty chunk of slots to cover the lesson time
              if (sess_activity_id != 0 && !locations_csv.equals("") && autoblock_times == 1 && req.getParameter("remove") == null 
                      && (!editLesson || !ltype.equals(ltype_old) || auto_select_location == 0)) {

                  int time_start = time;
                  int time_end = getEndTime(time, ltlength);

                  boolean bookAllSheets = false;
                  autoSelectLocation = auto_select_location == 1;

                  if (club.equals("ballantyne") && sess_activity_id == 1) {
                      autoSelectLocation = true;
                  }

                  sheet_id_map = verifyLesson.checkActivityTimes(lesson_id, sess_activity_id, locations_csv, date, date, new int[1], time_start, time_end, bookAllSheets, autoSelectLocation, con, out);

                  if (autoSelectLocation && sheet_id_map.get(0) != null) {
                      sheet_ids = sheet_id_map.get(0);
                  }

                  // If no time slots returned, send the user back to the lesson book
                  if (sheet_id_map.isEmpty()) {

                      clearInUse(id, sess_activity_id, date, con);    // clear in use flags for the day

                      out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
                      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
                      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                      out.println("<CENTER><BR><BR><H3>No Times Available</H3>");
                      if (club.equalsIgnoreCase("pinebrookcc")) {
                      out.println("<BR><BR>Sorry, but unfortunately there are currently no courts available to teach this lesson.<BR>");                      
                      } else {
                      out.println("<BR><BR>Sorry, no available time slots were found for this lesson.<BR>");                      
                      }

                      out.println("<BR>Please select a different time from the lesson book.");
                      out.println("<BR><BR>");
                      out.println("<font size=\"2\">");
                      out.println("<form action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
                      out.println("<input type=\"hidden\" name=\"lesson\" value=\"yes\">");
                      out.println("<input type=\"hidden\" name=\"proid\" value=" +id+ ">");
                      out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
                      out.println("<input type=\"hidden\" name=\"jump\" value=\"" +j+ "\">");
                      if (!caller.equals("")) {
                          out.println("<input type=\"hidden\" name=\"caller\" value=\"" + caller + "\">");
                          out.println("<input type=\"hidden\" name=\"set_id\" value=\"" + set_id + "\">");
                      }
                      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
                      out.println("</form></font>");
                      out.println("</CENTER></BODY></HTML>");
                      out.close();
                      return;

                  } else if (club.equals("ballantyne") && sess_activity_id == 1 && sheet_ids.get(0) != -99) {

                       try {

                           // Populate a slotParms instance to pass to checkSched
                           parmSlot slotParms = new parmSlot();

                           slotParms.club = club;
                           slotParms.user1 = memid;
                           slotParms.user2 = "";
                           slotParms.user3 = "";
                           slotParms.user4 = "";
                           slotParms.player1 = memname;
                           slotParms.player2 = "";
                           slotParms.player3 = "";
                           slotParms.player4 = "";
                           slotParms.oldPlayer1 = "";
                           slotParms.oldPlayer2 = "";
                           slotParms.oldPlayer3 = "";
                           slotParms.oldPlayer4 = "";
                           slotParms.date = date;
                           slotParms.activity_id = getActivity.getActivityIdFromSlotId(sheet_ids.get(0), con);
                           slotParms.root_activity_id = sess_activity_id;

                           slotParms.sheet_ids = (ArrayList) sheet_ids;

                           verifyActSlot.checkSched(slotParms, con);

                           if (slotParms.hit || slotParms.hit2 || slotParms.hit3) {

                               clearInUse(id, sess_activity_id, date, con);    // clear in use flags for the day

                               // Populate our parmAct block
                               parmActivity parmAct = new parmActivity();              // allocate a parm block

                               parmAct.slot_id = sheet_ids.get(0);                              // pass in the slot id so we can determin which activity to load parms for

                               try {

                                   getActivity.getParms(con, parmAct);                  // get the activity config

                               } catch (Exception e1) {
                                   Utilities.logError("Proshop_lesson.reqTime2 - " + club + " - Error getting activity_parameters for custom - " + e1.getMessage());
                               }

                               out.println(SystemUtils.HeadTitle("Member Scheduling Conflict"));
                               out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
                               out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                               out.println("<CENTER><BR><BR><H3>Member Already Playing</H3>");

                               if (slotParms.hit == true) {

                                   if (parmAct.rndsperday > 1) {

                                       out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is already scheduled to play the maximum number of times.<br><br>");
                                       out.println("A player can only be scheduled " +parmAct.rndsperday+ " times per day.<br><br>");

                                   } else {

                                       out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is already scheduled to play on this date at <b>" + SystemUtils.getSimpleTime(slotParms.time2) + "</b>.<br><br>");
                                       out.println("A player can only be scheduled once per day.<br><br>");
                                   }

                               } else if (slotParms.hit2 == true) {

                                   out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is scheduled to play during the time this reservation would cover.<br><br>");
                                   out.println("A player cannot have multiple reservations covering the same times.<br><br>");
                                   out.println("<BR><BR>");

                               } else if (slotParms.hit3 == true) {

                                   out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is scheduled to play another set within " +parmAct.minutesbtwn+ " minutes of the time this reservation would cover.<br><br>");
                                   out.println(slotParms.player + " is already scheduled to play on this date at <b>" + SystemUtils.getSimpleTime(slotParms.time2) + "</b>.<br><br>");
                                   out.println("<BR><BR>");
                               }

                              out.println("Please remove any conflicting reservations and try booking this lesson again.<br><br>");
                              out.println("<font size=\"2\">");
                              out.println("<form action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
                              out.println("<input type=\"hidden\" name=\"lesson\" value=\"yes\">");
                              out.println("<input type=\"hidden\" name=\"proid\" value=" +id+ ">");
                              out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
                              out.println("<input type=\"hidden\" name=\"jump\" value=\"" +j+ "\">");
                              if (!caller.equals("")) {
                                  out.println("<input type=\"hidden\" name=\"caller\" value=\"" + caller + "\">");
                                  out.println("<input type=\"hidden\" name=\"set_id\" value=\"" + set_id + "\">");
                              }
                              out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
                              out.println("</form></font>");
                              out.println("</CENTER></BODY></HTML>");

                              out.close();
                              return;
                           }

                       } catch (Exception exc) {
                           dbError(req, out, exc, lottery);
                           return;
                       }
                   } 
              }
          }
      }    // End of !returnFromLocSel


      num = 1;                // set one member for lesson

      //**************************************************************
      //  Verification Complete !!!!!!!!
      //**************************************************************

      sendemail = 0;         // init email flags
      emailNew = 0;
      emailMod = 0;

      //
      //  Determine if emails should be sent
      //
      //if (!memid.equals( "" )) {       // if member scheduled for lesson (COMMENTED OUT 8-13-08)
        
         if (!memname.equals( oldmemname ) || !ltype.equals( oldltype )) {  // if changed

            sendemail = 1;                // then send email
            emailNew = 1;                 // set new lesson

//            if (!oldmemname.equals( "" )) {     // if changing a lesson 
//               emailMod = 1;       // set modified lesson
//            } else {
//               emailNew = 1;       // set new lesson 
//            }
         }
      //}

   }  // end of IF 'cancel time' request

   if (isRecurr && !returnFromLocSel) {

       // Loop through available days and update the lesson times
       if (recurr_dates.size() > 0) {

           for (int k=0; k<recurr_dates.size(); k++) {
               
               if (recurr_unavail.get(k) == 0) {      // If date is available, we know there's at least one recid to be booked
                   
                   String hist_location = "";

                   try {

                       // Book initial lesson time slot
                       pstmt = con.prepareStatement (
                               "UPDATE lessonbook5 " +
                               "SET memname = ?, memid = ?, num = ?, length = ?, billed = ?, in_use = 0, ltype = ?, " +
                               "phone1 = ?, phone2 = ?, notes = ?, sheet_activity_id = ?, set_id = ? " +
                               "WHERE recid = ?");

                       pstmt.clearParameters();        // clear the parms
                       pstmt.setString(1, memname);
                       pstmt.setString(2, memid);
                       pstmt.setInt(3, num);
                       pstmt.setInt(4, ltlength);
                       pstmt.setInt(5, billed);
                       pstmt.setString(6, ltype);
                       pstmt.setString(7, phone1);
                       pstmt.setString(8, phone2);
                       pstmt.setString(9, notes);
                       pstmt.setInt(10, sheet_activity_id);
                       pstmt.setInt(11, set_id);

                       pstmt.setInt(12, recurr_recids.get(k).get(0));

                       pstmt.executeUpdate();      // execute the prepared stmt

                   } catch (Exception exc) {

                       recurr_unavail.set(k, -1);
                       Utilities.logError("Proshop_lesson.reqTime2 - " + club + " - Error booking intial recurr time for " + recurr_dates.get(k) + " - Err: " + exc.toString());

                   } finally {

                       try { pstmt.close(); }
                       catch (Exception ignore) { }
                   }
                   
                   if (sess_activity_id != 0 && recurr_sheet_ids.get(k).size() > 0) {

                       if (recurr_sheet_ids.get(k).get(0) != -99) {

                           String inString = "";
                           recurr_activity_names.set(k, "Check with Pro");        // Default location in case no specific one has been assigned.

                           for (int m=0; m<recurr_sheet_ids.get(k).size(); m++) {
                               inString += recurr_sheet_ids.get(k).get(m) + ",";
                           }

                           inString = inString.substring(0, inString.length() - 1);

                           try {

                               // Set the lesson_id field to link those activity time slots to this lesson booking
                               pstmt = con.prepareStatement("UPDATE activity_sheets SET lesson_id = ? WHERE sheet_id IN (" + inString + ")");
                               pstmt.clearParameters();
                               pstmt.setInt(1, recurr_recids.get(k).get(0));

                               pstmt.executeUpdate();

                               pstmt.close();

                               // Figure out what activity sheet they're on and store this value.
                               pstmt = con.prepareStatement(
                                       "SELECT acs.activity_id, activity_name FROM activity_sheets acs " +
                                       "LEFT OUTER JOIN activities ac ON acs.activity_id = ac.activity_id " +
                                       "WHERE acs.sheet_id = ?");
                               pstmt.clearParameters();
                               pstmt.setInt(1, recurr_sheet_ids.get(k).get(0));

                               rs = pstmt.executeQuery();

                               if (rs.next()) {

                                   sheet_activity_id = rs.getInt("acs.activity_id");
                                   recurr_activity_names.set(k, rs.getString("activity_name"));

                                   pstmt2 = con.prepareStatement("UPDATE lessonbook5 SET sheet_activity_id = ? WHERE recid = ?");
                                   pstmt2.clearParameters();
                                   pstmt2.setInt(1, sheet_activity_id);
                                   pstmt2.setInt(2, recurr_recids.get(k).get(0));

                                   pstmt2.executeUpdate();

                                   pstmt2.close();
                               }

                               pstmt.close();

                           } catch (Exception exc) {
                               Utilities.logError("Proshop_lesson.reqTime2 - " + club + " - Error booking time sheet slots on " + recurr_dates.get(k) + " - Err: " + exc.toString());
                           }
                       } else {
                           recurr_activity_names.set(k, "Check with Pro");
                       }
                       
                       hist_location = recurr_activity_names.get(k);
                   }

                   //  Make an entry in teehist so we can track these (set fb = 99 so we don't match on a tee time)
                   SystemUtils.updateHist(recurr_dates.get(k), dayName, time, 99, ltimename, proName, memname, ltype, hist_location, "", user, "Proshop User", 0, con);

                   // Book remaining additional times, if any
                   if (recurr_recids.get(k).size() > 1) {

                       // Loop through recid arraylist for this date, starting at 1 (index 0 time slot already booked above)
                       for (int m=1; m<recurr_recids.get(k).size(); m++) {

                           try {

                               //  set this time as a subsequent time for the primary time being processed
                               pstmt = con.prepareStatement (
                                       "UPDATE lessonbook5 " +
                                       "SET memname = ?, memid = ?, num = 0, length = 0, billed = 0, in_use = 0, ltype = '', " +
                                       "phone1 = '', phone2 = '', notes = '' " +
                                       "WHERE recid = ?");

                               pstmt.clearParameters();        // clear the parms
                               pstmt.setString(1, memname);
                               pstmt.setString(2, memid);

                               pstmt.setInt(3, recurr_recids.get(k).get(m));

                               pstmt.executeUpdate();      // execute the prepared stmt

                           } catch (Exception exc) {

                               recurr_unavail.set(k, -1);
                               Utilities.logError("Proshop_lesson.reqTime2 - " + club + " - Error booking subsequent recurr times for " + recurr_dates.get(k) + " - Err: " + exc.toString());

                           } finally {

                               try { pstmt.close(); }
                               catch (Exception ignore) { }
                           }
                       }
                   }
               }
           }

           clearInUse(id, sess_activity_id, date, con);     // Clear the initial day's in_use flags, other days are handled as those times are booked, only initial day is set completely in use.

           //  Completed booking lesson times, return to user
           out.println(SystemUtils.HeadTitle2("Lesson Book - Return"));
           out.println("</HEAD>");
           out.println("<BODY>");
           SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
           out.println("<CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
           out.println("<hr width=\"40%\">");
           out.println("<BR><BR><H3>Lesson Times Booked</H3>");
           out.println("<BR><BR>Thank you, all recurring lesson times have been attempted to be booked. See details below regarding any unavailable dates encountered.");

           out.println("<br>");

           String locationString = "";

           for (int k=0; k<recurr_dates.size(); k++) {

               if (sess_activity_id != 0 && req.getParameter("remove") == null) {
                   if (!recurr_activity_names.get(k).equals("") && !recurr_activity_names.get(k).equals("Check with Pro")) {
                       locationString = "  This lesson will take place on " + recurr_activity_names.get(k) + ".";
                   } else {
                       locationString = "  This lesson could not be assigned a location. Please direct members as needed.";
                   }
               }

               if (recurr_unavail.get(k) == 0) {
                   out.println("<br>" + Utilities.getDateFromYYYYMMDD(recurr_dates.get(k), 2) + " - Booked Successfully!" + locationString);
               } else if (recurr_unavail.get(k) == 1) {
                   out.println("<br><font color=\"red\">" + Utilities.getDateFromYYYYMMDD(recurr_dates.get(k), 2) + " - Times unavailable in lesson book, lesson not booked.</font>");
               } else if (recurr_unavail.get(k) == 2 && sess_activity_id != 0) {
                   out.println("<br><font color=\"red\">" + Utilities.getDateFromYYYYMMDD(recurr_dates.get(k), 2) + " - Times not available on activity time sheets, lesson not booked.</font>");
               } else if (recurr_unavail.get(k) == -1) {
                   out.println("<br><font color=\"red\">" + Utilities.getDateFromYYYYMMDD(recurr_dates.get(k), 2) + " - Error encountered, lesson not booked.</font>");
               }
           }
           out.println("<BR><BR>");

           out.println("<font size=\"2\">");
           out.println("<form action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
           out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
           out.println("<input type=\"hidden\" name=\"lesson\" value=\"yes\">");
           out.println("<input type=\"hidden\" name=\"proid\" value=" +id+ ">");
           out.println("<input type=\"hidden\" name=\"jump\" value=" +jump+ ">");
           out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
           if (!caller.equals("")) {
               out.println("<input type=\"hidden\" name=\"caller\" value=\"" + caller + "\">");
               out.println("<input type=\"hidden\" name=\"set_id\" value=\"" + set_id + "\">");
           }
           out.println("</form></font>");
           out.println("</CENTER></BODY></HTML>");
           out.close();
       }

   } else {    // Non-recurring Lesson
          
       // If we need to allow the user to manually select the location for the lesson to take place on, do so here
       if (!autoSelectLocation && !returnFromLocSel && req.getParameter("remove") == null) {
             
           String location_select_options = "";
           
           int default_sel = 0;
           
           if (sheet_id_map.containsKey(sheet_activity_id) && sheet_id_map.get(sheet_activity_id).size() > 0 && sheet_id_map.get(sheet_activity_id).get(0) != -99) {
               default_sel = sheet_activity_id;
           }
           
           // Loop over time sheets that acceptable blocks of times were found on, and print them out individually
           for (Map.Entry<Integer, List<Integer>> entry : sheet_id_map.entrySet()) {
               
               int key = entry.getKey();
               List<Integer> value = entry.getValue();
  
               if (value.size() > 0 && value.get(0) != -99) {
                   
                   String sheet_id_select = "";

                   for (Integer sheet_id : value) {
                       sheet_id_select += (!sheet_id_select.equals("") ? "|" : "") + String.valueOf(sheet_id);
                   }
                   
                   if (sheet_activity_id != 0) {
                       
                   }

                   location_select_options += "<br><label><input type=\"radio\" name=\"sheet_id_select\"" + ((default_sel == 0 || key == default_sel) ? " checked" : "") 
                           + " value=\"" + sheet_id_select + "\"> " + getActivity.getActivityName(key, con) + "</label>";

                   if (default_sel == 0) default_sel = -1;
               }
           }
           
           if (!location_select_options.equals("")) {
               
               out.println(SystemUtils.HeadTitle2("Lesson Book - Location Select"));
               out.println("</HEAD>");
               out.println("<BODY>");
               SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
               out.println("<CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Select a Location</H3>");
               out.println("<BR><BR>The following possible locations were found for this lesson. Please select a desired location and click 'Continue'.");
               out.println("<BR>");
               out.println("<font size=\"2\">");

               out.println("<form action=\"Proshop_lesson\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"reqtime2\">");
               out.println("<input type=\"hidden\" name=\"caller\" value=" + caller + ">");
               out.println("<input type=\"hidden\" name=\"lesson_id\" value=" + lesson_id + ">");
               out.println("<input type=\"hidden\" name=\"proid\" value=" + id + ">");
               out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
               out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
               out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + dayName + "\">");

               out.println("<input type=\"hidden\" name=\"player1\" value=\"" + memname + "\">");
               out.println("<input type=\"hidden\" name=\"memid\" value=\"" + memid + "\">");
               out.println("<input type=\"hidden\" name=\"num\" value=\"" + num + "\">");
               out.println("<input type=\"hidden\" name=\"ltlength\" value=\"" + ltlength + "\">");
               out.println("<input type=\"hidden\" name=\"billed\" value=\"" + billed + "\">");
               out.println("<input type=\"hidden\" name=\"ltype\" value=\"" + ltype + "\">");
               out.println("<input type=\"hidden\" name=\"phone1\" value=\"" + phone1 + "\">");
               out.println("<input type=\"hidden\" name=\"phone2\" value=\"" + phone2 + "\">");
               out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
               out.println("<input type=\"hidden\" name=\"sheet_activity_id\" value=\"" + sheet_activity_id + "\">");
               out.println("<input type=\"hidden\" name=\"set_id\" value=\"" + set_id + "\">");
               out.println("<input type=\"hidden\" name=\"auto_select_location\" value=\"" + auto_select_location + "\">");
               out.println("<input type=\"hidden\" name=\"returnFromLocSel\">");
               if (editLesson) out.println("<input type=\"hidden\" name=\"editLesson\">");
               
               out.println(location_select_options);

               out.println("<BR><BR>");
               out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</form>");
               out.println("<BR>");

               out.println("<form action=\"Proshop_lesson\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"reqtime\">");
               out.println("<input type=\"hidden\" name=\"caller\" value=" + caller + ">");
               out.println("<input type=\"hidden\" name=\"lesson_id\" value=" + lesson_id + ">");
               out.println("<input type=\"hidden\" name=\"proid\" value=" + id + ">");
               out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
               out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
               out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + dayName + "\">");

               out.println("<input type=\"hidden\" name=\"memname\" value=\"" + memname + "\">");
               out.println("<input type=\"hidden\" name=\"memid\" value=\"" + memid + "\">");
               out.println("<input type=\"hidden\" name=\"num\" value=\"" + num + "\">");
               out.println("<input type=\"hidden\" name=\"ltlength\" value=\"" + ltlength + "\">");
               out.println("<input type=\"hidden\" name=\"billed\" value=\"" + billed + "\">");
               out.println("<input type=\"hidden\" name=\"ltype\" value=\"" + ltype + "\">");
               out.println("<input type=\"hidden\" name=\"phone1\" value=\"" + phone1 + "\">");
               out.println("<input type=\"hidden\" name=\"phone2\" value=\"" + phone2 + "\">");
               out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
               out.println("<input type=\"hidden\" name=\"set_id\" value=\"" + set_id + "\">");
               out.println("<input type=\"hidden\" name=\"auto_select_location\" value=\"" + auto_select_location + "\">");
               out.println("<input type=\"hidden\" name=\"returnToSlot\">");

               out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");

               out.println("</form>");
               out.close();
               return;
               
           } else {
               
               // If all of the locations brought back had a sheet_id of -99, this means the lesson occurs at times completely 
               // before or after the time sheets built for that activity, so that portion of the booking can be skipped.
               sheet_ids.add(-99);
               returnFromLocSel = true;
           }
           
       }
       
       if (autoSelectLocation || returnFromLocSel) {
               
           boolean clear_lesson_times = false;
               
           String inString = "";

           for (int sheet_id : sheet_ids) {
               inString += "?,";
           }

           if (inString.endsWith(",")) {
               inString = inString.substring(0, inString.length() - 1);
           }
           
           if (sess_activity_id != 0 && sheet_ids.size() > 0 && sheet_ids.get(0) != -99 && req.getParameter("remove") == null 
                   && (!editLesson || !ltype.equals(ltype_old) || !autoSelectLocation)) {

               // Lock up all time slots and make sure they're not occupied
               lock_up_loop:
               for (int sheet_id : sheet_ids) {

                   try {

                       pstmt = con.prepareStatement(
                               "UPDATE activity_sheets "
                               + "SET in_use_by = ?, in_use_at = now() "
                               + "WHERE sheet_id = ? AND (SELECT count(*) FROM activity_sheets_players WHERE activity_sheet_id = ?) = 0 AND "
                               + "( in_use_by = '' || (UNIX_TIMESTAMP(in_use_at) + (6 * 60)) < UNIX_TIMESTAMP())");

                       pstmt.clearParameters();
                       pstmt.setString(1, user);
                       pstmt.setInt(2, sheet_id);
                       pstmt.setInt(3, sheet_id);

                       // if we couldn't set the time in use, this means some portion of our court selection has become occupied, and can no longer be used. Free up any times set in use.
                       if (pstmt.executeUpdate() == 0) {
                           clear_lesson_times = true;
                           break lock_up_loop;
                       }

                   } catch (Exception e) {
                       Utilities.logError("Proshop_lesson.reqTime2 - " + club + " - Error locking up activity_sheets - ERR: " + e.toString());
                   } finally {
                       Connect.close(pstmt);
                   }
               }
           }
           
           
           //  Update the lesson time
           if (!clear_lesson_times) {
               
               try {

                  pstmt = con.prepareStatement (
                     "UPDATE lessonbook5 " +
                     "SET memname = ?, memid = ?, num = ?, length = ?, billed = ?, in_use = 0, ltype = ?, " +
                     "phone1 = ?, phone2 = ?, notes = ?, sheet_activity_id = ?, set_id = ? " +
                     "WHERE recid = ?");

                  pstmt.clearParameters();        // clear the parms
                  pstmt.setString(1, memname);
                  pstmt.setString(2, memid);
                  pstmt.setInt(3, num);
                  pstmt.setInt(4, ltlength);
                  pstmt.setInt(5, billed);
                  pstmt.setString(6, ltype);
                  pstmt.setString(7, phone1);
                  pstmt.setString(8, phone2);
                  pstmt.setString(9, notes);
                  pstmt.setInt(10, sheet_activity_id);
                  pstmt.setInt(11, set_id);

                  pstmt.setInt(12, lesson_id);

                  pstmt.executeUpdate();      // execute the prepared stmt

                  lessonBooked = true;           

                  //  Clear the in_use flag for the whole day
                  clearInUse(id, sess_activity_id, date, con);

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
                  out.println("<font size=\"2\">");
                  out.println("<form action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
                  out.println("<input type=\"hidden\" name=\"lesson\" value=\"yes\">");
                  out.println("<input type=\"hidden\" name=\"proid\" value=" +id+ ">");
                  out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
                  out.println("<input type=\"hidden\" name=\"jump\" value=\"" +j+ "\">");
                  if (!caller.equals("")) {
                      out.println("<input type=\"hidden\" name=\"caller\" value=\"" + caller + "\">");
                      out.println("<input type=\"hidden\" name=\"set_id\" value=\"" + set_id + "\">");
                  }
                  out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
                  out.println("</form></font>");
                  out.println("</CENTER></BODY></HTML>");
                  out.close();
                  return;
                  
               } finally {
                   Connect.close(pstmt);
               }
           }

           //  If on an activity other than golf, block any necessary slots (if size > 0 and index 0 = -99, no slots are needed to book this lesson, ignore this section)
           if (sess_activity_id != 0 && sheet_ids.size() > 0 && req.getParameter("remove") == null && (!editLesson || !ltype.equals(ltype_old) || !autoSelectLocation)) {
               
               if (sheet_ids.get(0) != -99) {

                   activity_name = "Check with Pro";        // Default location in case no specific one has been assigned.
                   
                   // If no issue encountered locking up time slots
                   if (!clear_lesson_times) {

                       try {

                           // Set the lesson_id field to link those activity time slots to this lesson booking
                           pstmt = con.prepareStatement(
                                   "UPDATE activity_sheets "
                                   + "SET lesson_id = ? "
                                   + "WHERE sheet_id IN (" + inString + ")");
                           pstmt.clearParameters();
                           pstmt.setInt(1, lesson_id);

                           int m = 2;
                           for (int sheet_id : sheet_ids) {
                               pstmt.setInt(m++, sheet_id);
                           }

                           pstmt.executeUpdate();

                           pstmt.close();

                           // Figure out what activity sheet they're on and store this value.
                           pstmt = con.prepareStatement(
                                   "SELECT acs.activity_id, activity_name FROM activity_sheets acs "
                                   + "LEFT OUTER JOIN activities ac ON acs.activity_id = ac.activity_id "
                                   + "WHERE acs.sheet_id = ?");
                           pstmt.clearParameters();
                           pstmt.setInt(1, sheet_ids.get(0));

                           rs = pstmt.executeQuery();

                           if (rs.next()) {

                               sheet_activity_id = rs.getInt("acs.activity_id");
                               activity_name = rs.getString("activity_name");

                               try {
                                   pstmt2 = con.prepareStatement("UPDATE lessonbook5 SET sheet_activity_id = ? WHERE recid = ?");
                                   pstmt2.clearParameters();
                                   pstmt2.setInt(1, sheet_activity_id);
                                   pstmt2.setInt(2, lesson_id);

                                   pstmt2.executeUpdate();

                               } catch (Exception e) {
                                   clear_lesson_times = true;
                                   Utilities.logError("Proshop_lesson.reqTime2 - " + club + " - Error updating sheet_id in lesson book slot - ERR: " + e.toString());
                               } finally {
                                   Connect.close(pstmt2);
                               }
                           }

                       } catch (Exception e) {
                           clear_lesson_times = true;
                           Utilities.logError("Proshop_lesson.reqTime2 - " + club + " - Error locking up activity_sheets - ERR: " + e.toString());
                       } finally {
                           Connect.close(pstmt);
                       }
                       
                       // Clear out lesson_id from activity_sheet slots that are no longer needed by this lesson booking
                       verifyLesson.clearLessonFromActSheets(lesson_id, sheet_ids, req);
                       
                       // Clear in-use flags on locked up activity times
                       try {
                           
                           pstmt = con.prepareStatement(
                                   "UPDATE activity_sheets "
                                   + "SET in_use_by = '', in_use_at = '0000-00-00 00:00:00' "
                                   + "WHERE in_use_by = ? AND sheet_id IN (" + inString + ")");
                           pstmt.clearParameters();
                           pstmt.setString(1, user);
                           
                           int m = 2;
                           for (int sheet_id : sheet_ids) {
                               pstmt.setInt(m++, sheet_id);
                           }

                           pstmt.executeUpdate();

                       } catch (Exception e) {
                           Utilities.logError("Proshop_lesson.reqTime2 - " + club + " - Error freeing up activity sheets after booking - ERR: " + e.toString());
                       } finally {
                           Connect.close(pstmt);
                       }
                   }
                   
                   // We ran into an issue while trying to finalize the lesson booking. Unbook the lesson and free up any locked up sheet_ids
                   if (clear_lesson_times) {
   
                       if (!editLesson && lessonBooked) {
                           try {
                               pstmt = con.prepareStatement(
                                       "UPDATE lessonbook5 "
                                       + "SET memname = '', memid = '', num = 0, length = 0, billed = 0, ltype = '', "
                                       + "phone1 = '', phone2 = '', notes = '', sheet_activity_id = 0, set_id = 0 "
                                       + "WHERE recid = ?");

                               pstmt.setInt(1, lesson_id);

                               pstmt.executeUpdate();      // execute the prepared stmt

                           } catch (Exception e) {
                               Utilities.logError("Proshop_lesson.reqTime2 - " + club + " - Error un-booking lesson time - ERR: " + e.toString());
                           } finally {
                               Connect.close(pstmt);
                           }
                       }

                       try {
                           
                           pstmt = con.prepareStatement(
                                   "UPDATE activity_sheets "
                                   + "SET in_use_by = '', in_use_at = '0000-00-00 00:00:00' "
                                   + "WHERE in_use_by = ? AND sheet_id IN (" + inString + ")");
                           pstmt.clearParameters();
                           pstmt.setString(1, user);
                           
                           int m = 2;
                           for (int sheet_id : sheet_ids) {
                               pstmt.setInt(m++, sheet_id);
                           }

                           pstmt.executeUpdate();

                       } catch (Exception e) {
                           Utilities.logError("Proshop_lesson.reqTime2 - " + club + " - Error clearing in-use activity sheets - ERR: " + e.toString());
                       } finally {
                           Connect.close(pstmt);
                       }
                        
                       // Route user back to the lesson slot page to try again.
                       out.println(SystemUtils.HeadTitle2("Lesson Book - Location Select"));
                       out.println("</HEAD>");
                       out.println("<BODY>");
                       SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
                       out.println("<CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                       out.println("<hr width=\"40%\">");
                       out.println("<BR><BR><H3>Time Slots No Longer Available</H3>");
                       out.println("<BR><BR>The time slots originally found are unfortunately no longer available. "
                               + "<BR><BR>Please return to the lesson booking and try again.");
                       out.println("<BR><BR>");
                       out.println("<font size=\"2\">");
                      
                       out.println("<form action=\"Proshop_lesson\" method=\"post\" target=\"_top\">");
                       out.println("<input type=\"hidden\" name=\"reqtime\">");
                       out.println("<input type=\"hidden\" name=\"caller\" value=" + caller + ">");
                       out.println("<input type=\"hidden\" name=\"lesson_id\" value=" + lesson_id + ">");
                       out.println("<input type=\"hidden\" name=\"proid\" value=" + id + ">");
                       out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
                       out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
                       out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
                       out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");
                       out.println("<input type=\"hidden\" name=\"day\" value=\"" + dayName + "\">");

                       out.println("<input type=\"hidden\" name=\"memname\" value=\"" + memname + "\">");
                       out.println("<input type=\"hidden\" name=\"memid\" value=\"" + memid + "\">");
                       out.println("<input type=\"hidden\" name=\"num\" value=\"" + num + "\">");
                       out.println("<input type=\"hidden\" name=\"ltlength\" value=\"" + ltlength + "\">");
                       out.println("<input type=\"hidden\" name=\"billed\" value=\"" + billed + "\">");
                       out.println("<input type=\"hidden\" name=\"ltype\" value=\"" + ltype + "\">");
                       out.println("<input type=\"hidden\" name=\"phone1\" value=\"" + phone1 + "\">");
                       out.println("<input type=\"hidden\" name=\"phone2\" value=\"" + phone2 + "\">");
                       out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
                       out.println("<input type=\"hidden\" name=\"set_id\" value=\"" + set_id + "\">");
                       out.println("<input type=\"hidden\" name=\"returnToSlot\">");
                       
                       out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");

                       out.println("</form>");
                       out.println("</font></CENTER></BODY></HTML>");
                       out.close();
                       return;
                   }
                   
               } else {
                   activity_name = "Check with Pro";
               }
           } else if (sess_activity_id != 0 && sheet_activity_id != 0 && editLesson && ltype.equals(ltype_old) && autoSelectLocation) {
               
               // If editing a lesson and the lesson type hasn't changed, look up activity name since we don't already have it
               try {
                   
                   pstmt = con.prepareStatement(
                           "SELECT activity_name FROM activities WHERE activity_id = ?");
                   pstmt.clearParameters();
                   pstmt.setInt(1, sheet_activity_id);

                   rs = pstmt.executeQuery();

                   if (rs.next()) {
                       activity_name = rs.getString("activity_name");
                   }
               } catch (Exception e) {
                   Utilities.logError("Proshop_lesson.reqTime2 - " + club + " - Error looking up activity name - Err: " + e.toString());
               } finally {
                   Connect.close(rs, pstmt);
               }
               
           }

           String hist_location = "";
           
           if (sess_activity_id != 0) {
               hist_location = activity_name;
           }

           //
           //  Make an entry in teehist so we can track these (set fb = 99 so we don't match on a tee time)
           //
           if (oldmemname.equals( "" )) {

              //  new lesson
              SystemUtils.updateHist(date, dayName, time, 99, ltimename, proName, memname, ltype, hist_location, "",
                                     user, "Proshop User", 0, con);

           } else {

              if (req.getParameter("remove") == null) {

                  //  update lesson
                  SystemUtils.updateHist(date, dayName, time, 99, ltimename, proName, memname, ltype, hist_location, "",
                                         user, "Proshop User", 1, con);
              } else {

                  //  cancel lesson
                  SystemUtils.updateHist(date, dayName, time, 99, ltimename, proName, memname, ltype, "", "",
                                         user, "Proshop User", 2, con);
              }
           }


           //
           //  Return to user
           //

           out.println(SystemUtils.HeadTitle2("Lesson Book - Return"));
           if (skipDining.equalsIgnoreCase("yes") || req.getParameter("remove") != null) {
               out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Proshop_jump?lesson=yes&proid=" +id+ "&calDate=" +calDate+ "&jump=" +jump+ (!caller.equals("") ? "&caller=" +caller+ "&set_id=" + (req.getParameter("remove") != null ? old_set_id : set_id) : "") + "\">");
           }
           out.println("</HEAD>");
           out.println("<BODY>");
           SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
           out.println("<CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
           out.println("<hr width=\"40%\">");
           out.println("<BR><BR><H3>Lesson Time Updated</H3>");
           out.println("<BR><BR>Thank you, the lesson slot has been updated.");
           if (sess_activity_id != 0 && req.getParameter("remove") == null) {
               if (!activity_name.equals("") && !activity_name.equals("Check with Pro")) {
                   out.println("  This lesson will take place on " + activity_name + ".");
               } else {
                   out.println("  This lesson could not be assigned a location. Please direct members as needed.");
               }
           }
           out.println("<BR><BR>");

           //
           //  Print dining request prompt if system is active and properly configured
           //
           if (Utilities.checkDiningLink("pro_lesson", con) && req.getParameter("remove") == null && diningAccess) {
               String msgDate = yy + "-" + mm + "-" + dd;
               Utilities.printDiningPrompt(out, con, msgDate, dayName, memid, 1, "lesson", "&proid=" + id + "&jump=" + jump + "&calDate=" + calDate, true);
           }

           out.println("<font size=\"2\">");
           out.println("<form action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
           out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
           out.println("<input type=\"hidden\" name=\"lesson\" value=\"yes\">");
           out.println("<input type=\"hidden\" name=\"proid\" value=" +id+ ">");
           out.println("<input type=\"hidden\" name=\"jump\" value=" +jump+ ">");
           out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
           if (!caller.equals("")) {
               out.println("<input type=\"hidden\" name=\"caller\" value=\"" + caller + "\">");
               out.println("<input type=\"hidden\" name=\"set_id\" value=\"" + (req.getParameter("remove") != null ? old_set_id : set_id) + "\">");
           }
           out.println("</form></font>");
           out.println("</CENTER></BODY></HTML>");
           out.close();

           //
           //************************************************************************************
           //  Now check if more lesson times need to be updated based on the lesson length
           //************************************************************************************
           //
           if (ltlength == 0) {      // if cancel request

              ltlength = oldlength;    // restore old length to update all time slots

           } else {       // if not a cancel request

              if (oldlength > ltlength) {      // if old lesson length was longer than new request, then we must clear unused times

                 etime = getEndTime(time, oldlength);      // get end of old request

                 try {

                    //
                    //  init all the subsequent times from the old request (free them up)
                    //
                    pstmt2 = con.prepareStatement (
                       "UPDATE lessonbook5 " +
                       "SET memname = '', memid = '', num = 0, length = 0, billed = 0, ltype = '', " +
                       "phone1 = '', phone2 = '', notes = '' " +
                       "WHERE proid = ? AND activity_id = ? AND date = ? AND time > ? and time < ?");

                    pstmt2.clearParameters();        // clear the parms
                    pstmt2.setInt(1, id);
                    pstmt2.setInt(2, sess_activity_id);
                    pstmt2.setLong(3, date);
                    pstmt2.setInt(4, time);
                    pstmt2.setInt(5, etime);

                    pstmt2.executeUpdate();      // execute the prepared stmt

                    pstmt2.close();

                 }
                 catch (Exception e1) {
                 }

              }
           }

           etime = getEndTime(time, ltlength);

           //
           //  Get the lesson times that follow this time and within the lesson length
           //
           try {

              pstmt = con.prepareStatement (
                      "SELECT * FROM lessonbook5 " +
                      "WHERE proid = ? AND activity_id = ? AND date = ? AND time > ? AND time < ?");

              pstmt.clearParameters();        // clear the parms
              pstmt.setInt(1, id);
              pstmt.setInt(2, sess_activity_id);
              pstmt.setLong(3, date);
              pstmt.setInt(4, time);
              pstmt.setInt(5, etime);
              rs = pstmt.executeQuery();      // execute the prepared pstmt

              loop1:
              while (rs.next()) {

                 lesson_id2 = rs.getInt("recid");

                 //
                 //  set this time as a subsequent time for the primary time being processed
                 //
                 pstmt2 = con.prepareStatement (
                    "UPDATE lessonbook5 " +
                    "SET memname = ?, memid = ?, num = 0, length = 0, billed = 0, ltype = '', " +
                    "phone1 = '', phone2 = '', notes = '' " +
                    "WHERE recid = ?");

                 pstmt2.clearParameters();        // clear the parms
                 pstmt2.setString(1, memname);
                 pstmt2.setString(2, memid);

                 pstmt2.setInt(3, lesson_id2);

                 pstmt2.executeUpdate();      // execute the prepared stmt

                 pstmt2.close();
              }

              pstmt.close();

              // If Charlotte CC, block any lesson slots that fall within the 30 minutes following the main lesson.
              if (club.equals("charlottecc") && sess_activity_id == 0) {

                  int tempStime = etime;
                  int tempEtime = getEndTime(etime, 30);

                  try {

                      pstmt = con.prepareStatement("UPDATE lessonbook5 SET block = 1, in_use = 0 WHERE proid = ? AND activity_id = ? AND date = ? AND time >= ? AND time < ?");
                      pstmt.clearParameters();
                      pstmt.setInt(1, id);
                      pstmt.setInt(2, sess_activity_id);
                      pstmt.setLong(3, date);
                      pstmt.setInt(4, tempStime);
                      pstmt.setInt(5, tempEtime);

                      pstmt.executeUpdate();

                  } catch (Exception exc) {

                      Utilities.logError("Proshop_lesson.reqTime2 - " + club + " - Error adding custom buffer time after lesson - Err: " + exc.toString());

                  } finally {

                      try { rs.close(); }
                      catch (Exception ignore) { }

                      try { pstmt.close(); }
                      catch (Exception ignore) { }
                  }
              }

           }
           catch (Exception e1) {
           }
       }

       //
       //  Also, see if any phone numbers were entered.  If so, then see if we should update the member's record
       //
       if (!phone1.equals( "" ) && phone1 != null && !memid.equals( "" )) {      // if phone # entered

           String ph1 = "";
           String ph2 = "";

           boolean changePhone = false;

           try {
               pstmt = con.prepareStatement (
                       "SELECT phone1, phone2 FROM member2b " +
                       "WHERE username = ?");

               pstmt.clearParameters();               // clear the parms
               pstmt.setString(1, memid);
               rs = pstmt.executeQuery();             // execute the prepared stmt

               if (rs.next()) {

                   ph1 = rs.getString(1);
                   ph2 = rs.getString(2);
               }
               pstmt.close();

               if (ph1.equals( "" )) {        // if member has no phone #

                   ph1 = phone1;               // he/she does now
                   changePhone = true;         // update the record
               }
               if (ph2.equals( "" ) && !phone2.equals( "" ) && phone2 != null) {   // if member has no phone2, but 1 entered here

                   ph2 = phone2;               // he/she does now
                   changePhone = true;         // update the record
               }

               //
               //  update the member record if phone # to change
               //
               if (changePhone == true) {

                   pstmt = con.prepareStatement (
                           "UPDATE member2b " +
                           "SET phone1 = ?, phone2 = ? " +
                           "WHERE username = ?");

                   pstmt.clearParameters();        // clear the parms
                   pstmt.setString(1, phone1);
                   pstmt.setString(2, phone2);
                   pstmt.setString(3, memid);

                   pstmt.executeUpdate();      // execute the prepared stmt

                   pstmt.close();
               }
           } catch (Exception exc) {
               Utilities.logError("Proshop_lesson.reqTime2 - " + club + " - Error updating member phone numbers - Err: " + exc.toString());
           }
       }

       //
       //***********************************************
       //  Now send any emails if necessary
       //***********************************************
       //
       
       if (sendemail != 0 && !isRecurr) {   //************Remove '!isRecurr' component once proper email handling is added!!************
          //
          //  allocate a parm block to hold the email parms
          //
          parmEmail parme = new parmEmail();          // allocate an Email parm block

          //
          //  Set the values in the email parm block
          //
          parme.activity_id = sess_activity_id;
          parme.club = club;
          parme.guests = 0;
          parme.type = "lesson";                // type = lesson time
          parme.date = date;
          parme.time = time;
          parme.fb = id;                        // pro id
          parme.mm = month;
          parme.dd = day;
          parme.yy = year;
          parme.emailNew = emailNew;
          parme.emailMod = emailMod;  // used for 
          parme.emailCan = emailCan;
          parme.day = dayName;
          parme.user = user;                    // from proshop user
          parme.actual_activity_name = activity_name;
          parme.activity_name = getActivity.getActivityName(sess_activity_id, con);          // root activity name

          if (emailCan == 1) {                     // if Cancel Request

             parme.user1 = oldmemid;               // username of member
             parme.player1 = oldmemname;           // member name
             parme.course = oldltype;              // put lesson type in course field

          } else {

             parme.user1 = memid;                  // username of member
             parme.player1 = memname;              // member name
             parme.course = ltype;                 // put lesson type in course field
          }
          
          if (club.equalsIgnoreCase("colletonriverclub")) {
              parme.notes = notes;
              parme.hideNotes = 0;
          }

          //  if member email suppression desired, set flag in parme
          if (suppressEmails.equalsIgnoreCase( "yes" )) {
              parme.suppressMemberEmails = true;
          }

          //
          //  Send the email
          //
          sendEmail.sendIt(parme, con);      // in common
       }
   }
 }
 
 
 // ********************************************************************
 //  Process the 'Request Group Lesson Time' from the Lesson Book
 // ********************************************************************

 private void grpLesson(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException {

   PreparedStatement pstmt = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String user = (String)session.getAttribute("user");               // get name of user
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String memname = "";
   String memid = "";
   String ltype = "";
   String cost = "";
   String ph1 = "";
   String ph2 = "";
   String descript = "";
   String error = "";
   String ampm = "AM";
   String s_ampm = "AM";
   String e_ampm = "AM";
   String skipDining = "";

   int id = 0;
   int i = 0;
   int i2 = 0;
   int i3 = 0;
   int j = 0;
   int num = 0;
   int max = 0;
   int smonth = 0;
   int sday = 0;
   int syear = 0;
   int emonth = 0;
   int eday = 0;
   int eyear = 0;
   int hr = 0;
   int min = 0;
   int s_hr = 0;
   int s_min = 0;
   int e_hr = 0;
   int e_min = 0;
   int time = 0;
   int stime = 0;
   int etime = 0;
   int month = 0;
   int day = 0;
   int year = 0;
   int lesson_id = 0;
   int clinic = 0;

   long date = 0;
   long sdate = 0;
   long edate = 0;
   long mm = 0;
   long dd = 0;
   long yy = 0;

   boolean b = false;
   boolean hit = false;
   
   // Check proshop user feature access for appropriate access rights
   boolean diningAccess = SystemUtils.verifyProAccess(req, "DINING_REQUEST", con, out);


   try {
      id = Integer.parseInt(proid);              // id of pro for this lesson book
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   //
   //  Get the parms
   //
   String lgname = req.getParameter("groupLesson");   // name of group lesson
   String dates = req.getParameter("date");           // date of lesson book
   String calDate = req.getParameter("calDate");      // date of lesson book in String mm/dd/yyyy (for returns)
   String times = req.getParameter("time");           // time requested
   String jump = req.getParameter("jump");            // jump index for return
   String dayName = req.getParameter("day");          // name of the day in this book

   //
   //  Convert to ints
   //
   try {
      date = Long.parseLong(dates);
      time = Integer.parseInt(times);
      j = Integer.parseInt(jump);
   }
   catch (NumberFormatException e) {
   }

   //
   //  break down the date and time
   //
   yy = date / 10000;                             // get year
   mm = (date - (yy * 10000)) / 100;              // get month
   dd = (date - (yy * 10000)) - (mm * 100);       // get day

   month = (int)mm;
   day = (int)dd;
   year = (int)yy;

   hr = time / 100;
   min = time - (hr * 100);

   if (hr == 12) {
      ampm = "PM";
   } else {
      if (hr > 12) {
         hr = hr - 12;          // adjust
         ampm = "PM";
      }
   }

   //
   //  Get this year
   //
   Calendar cal = new GregorianCalendar();           // get today's date
   int thisYear = cal.get(Calendar.YEAR);            // get the year

   //
   //  Get the current group lesson info
   //
   try {


      if (sess_activity_id != 0) {

          pstmt = con.prepareStatement (
                  "SELECT lesson_id, max, cost, descript, clinic FROM lessongrp5 " +
                  "WHERE proid = ? AND activity_id = ? AND lname = ? AND date <= ? AND edate >= ? AND " + dayName + " = 1 AND " +
                  "(eo_week = 0 OR (MOD(DATE_FORMAT(date, '%U'), 2) = MOD(DATE_FORMAT(?, '%U'), 2)))");
          pstmt.clearParameters();
          pstmt.setInt(1, id);
          pstmt.setInt(2, sess_activity_id);
          pstmt.setString(3, lgname);
          pstmt.setLong(4, date);
          pstmt.setLong(5, date);
          pstmt.setLong(6, date);
          rs = pstmt.executeQuery();

      } else {
          pstmt = con.prepareStatement (
                  "SELECT lesson_id, max, cost, descript, clinic FROM lessongrp5 WHERE proid = ? AND activity_id = ? AND lname = ? AND date = ?");

          pstmt.clearParameters();        // clear the parms
          pstmt.setInt(1,id);
          pstmt.setInt(2,sess_activity_id);
          pstmt.setString(3,lgname);
          pstmt.setLong(4,date);
          rs = pstmt.executeQuery();      // execute the prepared pstmt
      }

      if (rs.next()) {

         lesson_id = rs.getInt("lesson_id");
         max = rs.getInt("max");
         cost = rs.getString("cost");
         descript = rs.getString("descript");
         clinic = rs.getInt("clinic");

      } else {

         nfError(req, out, lottery);            // LT not found
         pstmt.close();
         return;
      }
      pstmt.close();

   }
   catch (Exception exc) {

      dbError(req, out, exc, lottery);
      return;
   }

   //
   //  Output a page to display the group lesson info
   //
   out.println(SystemUtils.HeadTitle2("Proshop - Edit Group Lesson Time"));

      //
      //*******************************************************************
      //  Erase the member info (entire row)
      //*******************************************************************
      //
      out.println("<script language='JavaScript'>");            // Erase name script
      out.println("<!--");

      out.println("function erase(pos1,pos2,pos3,pos4) {");

      out.println("document.playerform[pos1].value = '';");           // clear the player field
      out.println("document.playerform[pos2].value = '';");           // clear the phone1 field
      out.println("document.playerform[pos3].value = '';");           // clear the phone2 field
      out.println("document.playerform[pos4].value = '';");           // clear the notes field
      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");          // End of script

      //
      //*******************************************************************
      //  Move Notes into text area - done at load time (onLoad) 
      //*******************************************************************
      //
      out.println("<script language='JavaScript'>");             // Move Notes into textarea
      out.println("<!--");
      out.println("function movenotes() {");
      for(i=0; i<max; i++) {
         out.println("var oldnotes" +i+ " = document.playerform.oldnotes" +i+ ".value;");
         out.println("document.playerform.notes" +i+ ".value = oldnotes" +i+ ";");   // put notes in text area
      }
      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");          // End of script

      //
      //*******************************************************************
      //  Move a member name into the tee slot
      //*******************************************************************
      //
      out.println("<script language='JavaScript'>");            // Move name script
      out.println("<!--");

      out.println("function movename(name) {");

      out.println("del = ':';");                                // deliminator is a colon
      out.println("array = name.split(del);");                  // split string into 3 pieces (name, ph1, ph2)
      out.println("var name = array[0];");                      // get the name
      out.println("var ph1 = array[1];");                       // get phone1
      out.println("var ph2 = array[2];");                       // get phone2
      out.println("var done = 'no';");

      out.println("if (ph1 == null) {");                        // if phone1 is empty
         out.println("ph1 = ''");
      out.println("}");
      out.println("if (ph2 == null) {");                        // if phone2 is empty
         out.println("ph2 = ''");
      out.println("}");

      for(i=0; i<max; i++) {
        
         out.println("if (done == 'no') {");                    // if empty spot not found yet
            out.println("var player" +i+ " = document.playerform.player" +i+ ".value;");

            out.println("if (player" +i+ " == '') {");                    // if player is empty
               out.println("document.playerform.player" +i+ ".value = name;");
               out.println("document.playerform.phone1" +i+ ".value = ph1;");
               out.println("document.playerform.phone2" +i+ ".value = ph2;");
               out.println("done = 'yes';");
            out.println("}");
         out.println("}");
      }

      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");                               // End of script
      //
      //*******************************************************************
      //  End of Scripts
      //*******************************************************************

   out.println("</HEAD>");
   out.println("<body onLoad=\"movenotes()\" bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\" topmargin=\"0\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" width=\"100%\" align=\"center\" valign=\"top\">");  // large table for whole page
   out.println("<tr><td valign=\"top\" align=\"center\">");

   out.println("<table border=\"0\" align=\"center\">");                           // table for main page
   out.println("<tr><td align=\"center\"><br>");

      out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#336633\" cellpadding=\"3\">");
         out.println("<tr>");
         out.println("<td width=\"630\" align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<b>Group Lesson:</b>&nbsp;&nbsp;&nbsp;&nbsp;" +lgname);
            out.println("<br>Make the desired additions or changes below and click on Submit when done.");
         out.println("</font></td></tr>");
      out.println("</table>");

      out.println("<table border=\"0\">");
         out.println("<tr>");
         out.println("<td width=\"630\" align=\"center\">");
         out.println("<font size=\"2\">");
         out.println("Date:&nbsp;&nbsp;<b>" + dayName + "&nbsp;&nbsp;" + month + "/" + day + "/" + year + "</b>");
        if (min < 10) {
           out.println(" &nbsp;&nbsp;&nbsp;&nbsp;Group Lesson Time:&nbsp;&nbsp;<b>" +hr+ ":0" +min+ " " +ampm+ "</b>");
        } else {
           out.println(" &nbsp;&nbsp;&nbsp;&nbsp;Group Lesson Time:&nbsp;&nbsp;<b>" +hr+ ":" +min+ " " +ampm+ "</b>");
        }
        out.println("<br><b>Description:</b> " +descript);
        out.println("</font></td></tr>");
      out.println("</table>");

      out.println("<table border=\"0\" align=\"center\" cellpadding=\"5\" cellspacing=\"5\">"); 
         out.println("<tr>");

            out.println("<form action=\"Proshop_lesson\" method=\"post\" name=\"playerform\">");
            out.println("<input type=\"hidden\" name=\"groupLesson2\" value=\"" +lgname+ "\">");
            out.println("<input type=\"hidden\" name=\"proid\" value=\"" +id+ "\">");
            out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" +date+ "\">");
            out.println("<input type=\"hidden\" name=\"time\" value=\"" +time+ "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" +j+ "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" +dayName+ "\">");
            out.println("<input type=\"hidden\" name=\"max\" value=\"" +max+ "\">");
            out.println("<input type=\"hidden\" name=\"lesson_id\" value=\"" +lesson_id+ "\">");
            out.println("<input type=\"hidden\" name=\"clinic\" value=\"" +clinic+ "\">");

         out.println("<td align=\"center\" valign=\"top\">");    // col for main box

            out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" align=\"center\" cellpadding=\"3\">");  // table for player selection
            out.println("<tr bgcolor=\"#336633\">");     // header row
            out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("&nbsp;&nbsp;");
            out.println("</font></td>");             
              
            out.println("<td align=\"left\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("&nbsp;&nbsp;<b>Member</b>");
            out.println("</font></td>");
              
            out.println("<td align=\"left\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("&nbsp;&nbsp;<b>Phone 1</b>");
            out.println("</font></td>");

            out.println("<td align=\"left\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("&nbsp;&nbsp;<b>Phone 2</b>");
            out.println("</font></td>");

            out.println("<td align=\"left\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("&nbsp;&nbsp;<b>Billed</b>");
            out.println("</font></td>");

            out.println("<td align=\"left\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("&nbsp;&nbsp;<b>Notes</b>");
            out.println("</font></td>");
            out.println("</tr>");

            String [] mem = new String [max];             // empty arrays for the max # of members
            String [] phone1 = new String [max];  
            String [] phone2 = new String [max];
            String [] notes = new String [max];
            int [] billed = new int [max];

            for (i=0; i<max; i++) {              // init the arrays
              
               mem[i] = "";
               phone1[i] = "";
               phone2[i] = "";
               notes[i] = "";
            }
              
            i = 0;          // init i
              
            // 
            // get any members already signed up for this group lesson
            //
            try {

               // If a clinic, grab all signups under this lesson_id, regardless of date
               if (clinic == 1 && sess_activity_id != 0) {
                   
                   pstmt = con.prepareStatement (
                           "SELECT memname, billed, phone1, phone2, notes " +
                           "FROM lgrpsignup5 WHERE lesson_id = ?");

                   pstmt.clearParameters();        // clear the parms
                   pstmt.setInt(1,lesson_id);
                   rs = pstmt.executeQuery();      // execute the prepared pstmt

               } else {

                   pstmt = con.prepareStatement (
                           "SELECT memname, billed, phone1, phone2, notes " +
                           "FROM lgrpsignup5 WHERE proid = ? AND lname = ? AND date = ?");

                   pstmt.clearParameters();        // clear the parms
                   pstmt.setInt(1,id);
                   pstmt.setString(2,lgname);
                   pstmt.setLong(3,date);
                   rs = pstmt.executeQuery();      // execute the prepared pstmt
               }

               while (rs.next() && i < max) {

                  mem[i] = rs.getString("memname");
                  
                  if (!mem[i].equals( "" )) {            // if member name is there

                     billed[i] = rs.getInt("billed");
                     phone1[i] = rs.getString("phone1");
                     phone2[i] = rs.getString("phone2");
                     notes[i] = rs.getString("notes");

                     i3 = i + 1;

                     out.println("<tr>");
                     out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">" +i3);
                     out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erase('player" +i+ "','phone1" +i+ "','phone2" +i+ "','notes" +i+ "')\" style=\"cursor:hand\">");
                     out.println("</font></td>");

                     out.println("<td align=\"left\">");
                     out.println("<font size=\"2\">");
                     out.println("&nbsp;&nbsp;<input type=\"text\" name=\"player" +i+ "\" value=\"" +mem[i]+ "\" size=\"20\" maxlength=\"30\">");
                     out.println("</font></td>");

                     out.println("<td align=\"left\">");
                     out.println("<font size=\"2\">");
                     out.println("&nbsp;&nbsp;<input type=\"text\" name=\"phone1" +i+ "\" value=\"" +phone1[i]+ "\" size=\"12\" maxlength=\"24\">");
                     out.println("</font></td>");

                     out.println("<td align=\"left\">");
                     out.println("<font size=\"2\">");
                     out.println("&nbsp;&nbsp;<input type=\"text\" name=\"phone2" +i+ "\" value=\"" +phone2[i]+ "\" size=\"12\" maxlength=\"24\">");
                     out.println("</font></td>");

                     out.println("<td align=\"left\">");
                     out.println("<font size=\"2\">");
                     if (billed[i] == 1) {
                        out.println("&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"billed" +i+ "\" value=\"1\">");
                     } else {
                        out.println("&nbsp;&nbsp;<input type=\"checkbox\" name=\"billed" +i+ "\" value=\"1\">");
                     }
                     out.println("</font></td>");

                     out.println("<input type=\"hidden\" name=\"oldnotes" +i+ "\" value=\"" +notes[i]+ "\">"); // hold notes for script
                     out.println("<td align=\"left\">");
                     out.println("<font size=\"2\">");
                     out.println("&nbsp;&nbsp;<textarea name=\"notes" +i+ "\" value=\"\" id=\"notes" +i+ "\" cols=\"20\" rows=\"2\">");
                     out.println("</textarea>");
                     out.println("</font></td>");
                     out.println("</tr>");

                     i++;         // bump member counter
                  }
               }
               pstmt.close();

            }
            catch (Exception exc) {

               dbError(req, out, exc, lottery);
               return;
            }

            //
            //  now add a row for each additional possible member
            //
            for (i2=i; i2<max; i2++) {        // one row for each additional member

               i3 = i2 + 1;

               out.println("<tr>");
               out.println("<td align=\"center\">");
               out.println("<font size=\"2\">" +i3);
               out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erase('player" +i2+ "','phone1" +i2+ "','phone2" +i2+ "','notes" +i2+ "')\" style=\"cursor:hand\">");
               out.println("</font></td>");

               out.println("<td align=\"left\">");
               out.println("<font size=\"2\">");
               out.println("&nbsp;&nbsp;<input type=\"text\" name=\"player" +i2+ "\" value=\"\" size=\"20\" maxlength=\"30\">");
               out.println("</font></td>");

               out.println("<td align=\"left\">");
               out.println("<font size=\"2\">");
               out.println("&nbsp;&nbsp;<input type=\"text\" name=\"phone1" +i2+ "\" value=\"\" size=\"12\" maxlength=\"24\">");
               out.println("</font></td>");

               out.println("<td align=\"left\">");
               out.println("<font size=\"2\">");
               out.println("&nbsp;&nbsp;<input type=\"text\" name=\"phone2" +i2+ "\" value=\"\" size=\"12\" maxlength=\"24\">");
               out.println("</font></td>");

               out.println("<td align=\"left\">");
               out.println("<font size=\"2\">");
               out.println("&nbsp;&nbsp;<input type=\"checkbox\" name=\"billed" +i2+ "\" value=\"1\">");
               out.println("</font></td>");

               out.println("<input type=\"hidden\" name=\"oldnotes" +i2+ "\" value=\"\">"); // hold notes for script
               out.println("<td align=\"left\">");
               out.println("<font size=\"2\">");
               out.println("&nbsp;&nbsp;<textarea name=\"notes" +i2+ "\" value=\"\" id=\"notes" +i2+ "\" cols=\"20\" rows=\"2\">");
               out.println("</textarea>");
               out.println("</font></td>");
               out.println("</tr>");
            }

            if (Utilities.checkDiningLink("pro_lesson", con) && diningAccess) {
                out.println("<tr><td align=\"center\" colspan=\"8\">");
                out.println("Skip dining request prompt?:&nbsp;&nbsp; ");
                if (skipDining.equalsIgnoreCase( "yes" )) {
                   out.println("<input type=\"checkbox\" checked name=\"skipDining\" value=\"Yes\">");
                } else {
                   out.println("<input type=\"checkbox\" name=\"skipDining\" value=\"Yes\">");
                }
                out.println("</font><font size=\"1\">&nbsp;(checked = yes)<br><br></font><font size=\"2\">");
                out.println("</td></tr>");
            } else {
                out.println("<input type=\"hidden\" name=\"skipDining\" value=\"yes\">");
            }
            
            out.println("</table>");
              
         out.println("</td>");
         out.println("<td valign=\"top\">");      // col for spacer
         out.println("&nbsp;</td>");

         out.println("<td valign=\"top\">");      // col for name list

         out.println("<table border=\"1\" width=\"140\" bgcolor=\"#F5F5DC\" valign=\"top\">");      // name list
         out.println("<tr><td align=\"center\" bgcolor=\"#336633\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<b>Member List</b>");
               out.println("</font></td>");
         out.println("</tr><tr>");
         out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("Click on name to add");
            out.println("</font></td></tr>");

         // ********************************************************************************
         //    build the name list.
         // ********************************************************************************
         String first = "";
         String mid = "";
         String last = "";
         String name = "";
         String dname = "";

         try {

             PreparedStatement stmt2 = con.prepareStatement(
                     "SELECT name_last, name_first, name_mi, phone1, phone2 FROM member2b "
                     + "WHERE inact = 0 "
                     + "ORDER BY name_last, name_first, name_mi");

            stmt2.clearParameters();               // clear the parms
            rs = stmt2.executeQuery();             // execute the prepared stmt

            out.println("<tr><td align=\"left\"><font size=\"2\">");
            out.println("<select size=\"22\" name=\"bname\" onClick=\"movename(this.form.bname.value)\" style=\"cursor:hand\">");

            while(rs.next()) {

               last = rs.getString(1);
               first = rs.getString(2);
               mid = rs.getString(3);
               ph1 = rs.getString(4);
               ph2 = rs.getString(5);

               if (mid.equals("")) {

                  name = first + " " + last;
                  dname = last + ", " + first;
               } else {

                  name = first + " " + mid + " " + last;
                  dname = last + ", " + first + " " + mid;
               }

               name = name+ ":" +ph1+ ":" +ph2;              // combine name & phones for script

               out.println("<option value=\"" +name+ "\">" + dname + "</option>");
            }
            out.println("</select>");
            out.println("</font></td></tr>");

            stmt2.close();
         }
         catch (Exception ignore) {

         }

         out.println("</table>");                // end of name list table
      out.println("</td>");                                      // end of name list column
      out.println("</tr>");
    out.println("</table>");      // end of large table containg smaller tables

    out.println("<table border=\"0\" bgcolor=\"#FFFFFF\" valign=\"top\">");      // buttons
      out.println("<tr>");
      out.println("<td align=\"right\">");
      out.println("<input type=submit value=\"Submit\" name=\"submit\">");
      out.println("</td>");
      out.println("</form>");
      out.println("<td align=\"center\">");
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>");
      out.println("<form action=\"Proshop_lesson\" method=\"post\">");
      out.println("<input type=\"hidden\" name=\"proid\" value=" +id+ ">");
      out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" +j+ "\">");
      out.println("<td align=\"left\">");
      out.println("<input type=submit value=\"Return Without Changes\">");
      out.println("</td></form>");
      out.println("</tr>");
    out.println("</table>");

   out.println("</font></td></tr>");
   out.println("</table>");                      // end of main page table

   //
   //  End of HTML page
   //
   out.println("</td></tr>");
   out.println("</table>");                      // end of whole page table
   out.println("</font></center></body></html>");
   out.close();

 }


 // ********************************************************************
 //  Process the 'Update Group Lesson Time' from grpLesson above (submit)
 // ********************************************************************

 private void grpLesson2(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException {

   PreparedStatement pstmt = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String user = (String)session.getAttribute("user");               // get name of user
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String temps = "";
   String ampm = "AM";
   String s_ampm = "AM";
   String e_ampm = "AM";
   String skipDining = "no";

   int id = 0;
   int i = 0;
   int i2 = 0;
   int i3 = 0;
   int j = 0;
   int max = 0;
   int num = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int time2 = 0;
   int etime = 0;
   int month = 0;
   int day = 0;
   int year = 0;
   int sendemail = 0;
   int emailNew = 0;
   int emailMod = 0;
   int emailCan = 0;
   int msgPlayerCount = 0;

   long date = 0;
   long sdate = 0;
   long edate = 0;
   long mm = 0;
   long dd = 0;
   long yy = 0;

   boolean b = false;
   boolean hit = false;
   boolean found = false;

   // Check proshop user feature access for appropriate access rights
   boolean diningAccess = SystemUtils.verifyProAccess(req, "DINING_REQUEST", con, out);

   try {
      id = Integer.parseInt(proid);              // id of pro for this lesson book
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   //
   //  Get the parms
   //
   String lgname = req.getParameter("groupLesson2");    // name of group lesson
   String dates = req.getParameter("date");             // date of lesson book
   String calDate = req.getParameter("calDate");        // date of lesson book in String mm/dd/yyyy (for returns)
   String times = req.getParameter("time");             // time requested
   String jump = req.getParameter("jump");              // jump index for return
   String dayName = req.getParameter("day");            // name of the day in this book
   String maxs = req.getParameter("max");               // max number of members allowed in group lesson
   int lesson_id = Integer.parseInt(req.getParameter("lesson_id"));
   int clinic = Integer.parseInt(req.getParameter("clinic"));
   
   if (req.getParameter("skipDining") != null) {
       
      skipDining = "yes";
   }

   //
   //  Convert to ints
   //
   try {
      date = Long.parseLong(dates);
      max = Integer.parseInt(maxs);
      time = Integer.parseInt(times);
      j = Integer.parseInt(jump);
   }
   catch (NumberFormatException e) {
   }

   String [] mem = new String [max];             // empty arrays for the max # of members
   String [] phone1 = new String [max];
   String [] phone2 = new String [max];
   String [] notes = new String [max];
   String [] memid = new String [max];
   String [] fname = new String [max];
   String [] lname = new String [max];
   String [] mi = new String [max];
   String [] oldmem = new String [max];
   int [] billed = new int [max];
   int [] notesL = new int [max];

   //
   //  Get the rest of the parms (for each member)
   //
   for (i=0; i<max; i++) {        
     
      mem[i] = "";                                     // init fields
      phone1[i] = "";
      phone2[i] = "";
      notes[i] = "";
      memid[i] = "";
      fname[i] = "";
      lname[i] = "";
      mi[i] = "";
      oldmem[i] = "";
      billed[i] = 0;

      if (req.getParameter("player" +i) != null) {
         mem[i] = req.getParameter("player" +i);       // member names 
      }
      if (req.getParameter("phone1" +i) != null) {
         phone1[i] = req.getParameter("phone1" +i);    // phone #
      }
      if (req.getParameter("phone2" +i) != null) {
         phone2[i] = req.getParameter("phone2" +i);    // alt phone #
      }
      if (req.getParameter("billed" +i) != null) {
         temps = req.getParameter("billed" +i);        // billed indicator
         billed[i] = Integer.parseInt(temps);
      }
      if (req.getParameter("notes" +i) != null) {
         notes[i] = req.getParameter("notes" +i);      // notes
         notesL[i] = notes[i].length();                // get length of notes
      }
   }

   //
   //  break down the date and time
   //
   yy = date / 10000;                             // get year
   mm = (date - (yy * 10000)) / 100;              // get month
   dd = (date - (yy * 10000)) - (mm * 100);       // get day

   month = (int)mm;
   day = (int)dd;
   year = (int)yy;

   hr = time / 100;
   min = time - (hr * 100);

   if (hr == 12) {
      ampm = "PM";
   } else {
      if (hr > 12) {
         hr = hr - 12;          // adjust
         ampm = "PM";
      }
   }

   //
   //  Get the current values for this group lesson signup
   //
   try {

      i = 0;

      if (clinic == 1 && sess_activity_id != 0) {

          // If a clinic, grab all signups under this lesson_id, regardless of date
          pstmt = con.prepareStatement (
                  "SELECT memname " +
                  "FROM lgrpsignup5 WHERE lesson_id = ?");

          pstmt.clearParameters();        // clear the parms
          pstmt.setInt(1, lesson_id);
          rs = pstmt.executeQuery();      // execute the prepared stmt

      } else {

          pstmt = con.prepareStatement (
                  "SELECT memname " +
                  "FROM lgrpsignup5 WHERE proid = ? AND lname = ? AND date = ?");

          pstmt.clearParameters();        // clear the parms
          pstmt.setInt(1, id);
          pstmt.setString(2, lgname);
          pstmt.setLong(3, date);
          rs = pstmt.executeQuery();      // execute the prepared stmt
      }

      while (rs.next() && i < max) {

         if (!rs.getString(1).equals( "" )) {

            oldmem[i] = rs.getString(1);
            i++;
         }
      }
      pstmt.close();

   }
   catch (Exception e1) {

      dbError(req, out, e1, lottery);
      return;
   }

   //
   //  Parse the member name values to see if they are members
   //
   for (i=0; i<max; i++) { 

      if (!mem[i].equals( "" )) {

         StringTokenizer tok = new StringTokenizer( mem[i] );     // space is the default token

         if (tok.countTokens() > 1 && tok.countTokens() < 4) {

            if ( tok.countTokens() == 2 ) {         // first name, last name

               fname[i] = tok.nextToken();
               lname[i] = tok.nextToken();
            }

            if ( tok.countTokens() == 3 ) {         // first name, mi, last name

               fname[i] = tok.nextToken();
               mi[i] = tok.nextToken();
               lname[i] = tok.nextToken();
            }

            //
            //  Get the member's username (id)
            //
            try {

               pstmt = con.prepareStatement (
                  "SELECT username " +
                  "FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ?");

               pstmt.clearParameters();        // clear the parms
               pstmt.setString(1, lname[i]);
               pstmt.setString(2, fname[i]);
               pstmt.setString(3, mi[i]);
               rs = pstmt.executeQuery();      // execute the prepared stmt

               if (rs.next()) {

                  memid[i] = rs.getString(1);
               }
               pstmt.close();

            }
            catch (Exception e1) {
            }
         }
      }
   }

   //
   //  Determine if emails should be sent
   //
   sendemail = 1;         // init email flags
   emailNew = 0;
   emailMod = 0;

   if (!oldmem[0].equals( "" )) {        // if group lesson signup already existed

//      emailMod = 1;                   // set modified lesson
      emailNew = 1;                   // set new lesson for now

   } else {

      emailNew = 1;                   // set new lesson
   }

   //
   //  Add or Update the group lesson signup list
   //
   try {

       //
       //  First remove all entries - start over
       //
       if (clinic == 1 && sess_activity_id != 0) {

           // If this group lesson is a clinic, grab all signups under its lesson_id, regardless of date
           pstmt = con.prepareStatement (
                   "Delete FROM lgrpsignup5 WHERE lesson_id = ?");

           pstmt.clearParameters();               // clear the parms
           pstmt.setInt(1, lesson_id);
           pstmt.executeUpdate();         // execute the prepared pstmt

       } else {

           pstmt = con.prepareStatement (
                   "Delete FROM lgrpsignup5 WHERE proid = ? AND lname = ? AND date = ?");

           pstmt.clearParameters();               // clear the parms
           pstmt.setInt(1, id);
           pstmt.setString(2, lgname);
           pstmt.setLong(3, date);
           pstmt.executeUpdate();         // execute the prepared pstmt
       }

      pstmt.close();

      for (i=0; i<max; i++) {

         if (!mem[i].equals( "" )) {                   // if member name exists

            pstmt = con.prepareStatement (
              "INSERT INTO lgrpsignup5 (lesson_id, proid, lname, date, memname, " +
              "memid, billed, phone1, phone2, notes) " +
              "VALUES (?,?,?,?,?,?,?,?,?,?)");

            pstmt.clearParameters();        // clear the parms
            pstmt.setInt(1, lesson_id);
            pstmt.setInt(2, id);
            pstmt.setString(3, lgname);
            pstmt.setLong(4, date);
            pstmt.setString(5, mem[i]);
            pstmt.setString(6, memid[i]);
            pstmt.setInt(7, billed[i]);
            pstmt.setString(8, phone1[i]);
            pstmt.setString(9, phone2[i]);
            pstmt.setString(10, notes[i]);

            pstmt.executeUpdate();      // execute the prepared stmt

            pstmt.close();
         }
      }
   }
   catch (Exception e1) {

      dbError(req, out, e1, lottery);
      return;
   }

   //
   //  Return to user
   //
   for (int k=0; k<max; k++) {
       if (!memid.equals("")) {
           msgPlayerCount++;
       }
   }
   out.println(SystemUtils.HeadTitle2("Lesson Book - Return"));
   if (skipDining.equalsIgnoreCase("yes") || msgPlayerCount == 0) {
       out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Proshop_lesson?proid=" +id+ "&calDate=" +calDate+ "&jump=" +j+ "\">");
   }
   out.println("</HEAD>");
   out.println("<BODY>");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><BR><H3>Group Lesson Signup Updated</H3>");
   out.println("<BR><BR>Thank you, the Group Lesson Member List has been updated.");
   out.println("<BR><BR>");
   
   //
   //  Print dining request prompt if system is active and properly configured
   //
   if (Utilities.checkDiningLink("pro_lesson", con) && req.getParameter("remove") == null && diningAccess) {
       String tempMM = String.valueOf(mm);
       String tempDD = String.valueOf(dd);
       if (tempMM.length() < 2) {
           tempMM = "0" + tempMM;
       }
       if (tempDD.length() < 2) {
           tempDD = "0" + tempDD;
       }
       String msgDate = yy + "-" + tempMM + "-" + tempDD;
       Utilities.printDiningPrompt(out, con, msgDate, dayName, memid[0], msgPlayerCount, "lesson", "&proid=" + id + "&jump=" + j + "&calDate=" + calDate + "&group=yes", true);
   }
   
   out.println("<font size=\"2\">");
   out.println("<form action=\"Proshop_lesson\" method=\"post\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("<input type=\"hidden\" name=\"proid\" value=" +id+ ">");
   out.println("<input type=\"hidden\" name=\"jump\" value=" +j+ ">");
   out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();

   //
   //***********************************************
   //  Now send any emails if necessary
   //***********************************************
   //
   if (sendemail != 0) {

      //
      //  allocate a parm block to hold the email parms
      //
      parmEmail parme = new parmEmail();          // allocate an Email parm block

      for (i=0; i<max; i++) {

         if (!mem[i].equals( "" )) {                   // if member name exists

            found = false;

            eloop1:
            for (i2=0; i2<max; i2++) {

               if (mem[i].equals( oldmem[i2] )) {     // if member was already in the group

                  found = true;
                  break eloop1;
               }
            }

            if (found == false) {

               //
               //  Set the values in the email parm block
               //
               parme.date = date;
               parme.time = time;
               parme.fb = id;                        // pro id
               parme.mm = month;
               parme.dd = day;
               parme.yy = year;
               parme.emailNew = emailNew;
               parme.emailMod = 0;            // not used yet???
               parme.emailCan = 0;
               parme.day = dayName;

               parme.type = "lessongrp";             // type = group lesson time
               parme.user = user;                    // from proshop user
               parme.user1 = memid[i];               // username of member
               parme.player1 = mem[i];               // member name
               parme.course = lgname;                // put group lesson name in course field
               parme.activity_name = getActivity.getActivityName(sess_activity_id, con);          // root activity name

               //
               //  Send the email
               //
               sendEmail.sendIt(parme, con);      // in common
            }
         }
      }
   }

 }


 // *********************************************************
 //  Cancel a request to edit the lesson book
 // *********************************************************

 private void cancel(int id, int activity_id, long date, String calDate, int jump, String caller, int set_id, PrintWriter out, HttpServletRequest req, int lottery, Connection con) {


   //
   //  Clear the 'in_use' flag for all times during the specified day
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
         "UPDATE lessonbook5 SET in_use = 0 WHERE proid = ? AND activity_id = ? AND date = ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setInt(1, id);
      pstmt1.setInt(2, activity_id);
      pstmt1.setLong(3, date);
      pstmt1.executeUpdate();     

      pstmt1.close();

   }
   catch (Exception ignore) {
   }

   //
   //  Return to lesson book
   //
   out.println(SystemUtils.HeadTitle2("Lesson Book - Cancel"));
     
   out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Proshop_jump?lesson=yes&proid=" +id+ "&calDate=" +calDate+ "&jump=" +jump+ (!caller.equals("") ? "&caller=" + caller + "&set_id=" + set_id : "") + "\">");
   out.println("</HEAD>");
   out.println("<BODY>");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><BR><H3>Return/Cancel Requested</H3>");
   out.println("<BR><BR>Thank you, the lesson slot has been returned to the system without changes.");
   out.println("<BR><BR>");

   out.println("<font size=\"2\">");
   out.println("<form action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("<input type=\"hidden\" name=\"lesson\" value=\"yes\">");
   out.println("<input type=\"hidden\" name=\"proid\" value=" +id+ ">");
   out.println("<input type=\"hidden\" name=\"jump\" value=" +jump+ ">");
   out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
   if (!caller.equals("")) {
       out.println("<input type=\"hidden\" name=\"caller\" value=\"" + caller + "\">");
       out.println("<input type=\"hidden\" name=\"set_id\" value=\"" + set_id + "\">");
   }
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 // ********************************************************************
 //  Process the 'Changed Billed Setting' from the Lesson Book
 // ********************************************************************

 private void reqBilled(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException {

   PreparedStatement pstmt = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String user = (String)session.getAttribute("user");               // get name of user
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   int id = 0;
   int j = 0;
   int time = 0;
   int billed = 0;

   long date = 0;

   boolean b = false;

   try {
      id = Integer.parseInt(proid);              // id of pro for this lesson book
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   //
   //  Get the parms
   //
   String billeds = req.getParameter("billed");   // billed? (yes or no)
   String dates = req.getParameter("date");       // date of lesson book
   String calDate = req.getParameter("calDate");  // date of lesson book in String mm/dd/yyyy (for returns)
   String times = req.getParameter("time");       // time requested
   String jump = req.getParameter("jump");        // jump index for return
   String dayName = req.getParameter("day");          // name of the day in this book

   //
   //  Convert to ints
   //
   try {
      date = Long.parseLong(dates);
      time = Integer.parseInt(times);
      j = Integer.parseInt(jump);
   }
   catch (NumberFormatException e) {
   }

   //
   //  Determine if setting 'billed' to yes or no
   //
   if (billeds.equals( "yes" )) {
     
      billed = 1;       // set billed
        
   } else {
     
      billed = 0;      // set not billed
   }

   //******************************************************************
   //  Set the 'billed' flag in the specified lesson time and return
   //******************************************************************
   //
   
   try {

      pstmt = con.prepareStatement (
              "UPDATE lessonbook5 SET billed = ? WHERE proid = ? AND activity_id = ? AND date = ? AND time = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1,billed);
      pstmt.setInt(2,id);
      pstmt.setInt(3,sess_activity_id);
      pstmt.setLong(4,date);
      pstmt.setInt(5,time);
        
      pstmt.executeUpdate();

      pstmt.close();

   }
   catch (Exception exc) {
   }

 }


 // ********************************************************************
 //  Process the 'Block Lesson Time' request from the Control Panel
 // ********************************************************************

 private void blockTime(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException {

   PreparedStatement pstmt = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String user = (String)session.getAttribute("user");               // get name of user
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String lbname = "";
   String color = "";
   String error = "";
   String dates = "";
   String calDate = "";
   String jump = "";
   String ampm = "AM";
   String s_ampm = "AM";
   String e_ampm = "AM";

   int id = 0;
   int i = 0;
   int j = 0;
   int block = 0;
   int today = 0;
   int mon = 0;
   int tue = 0;
   int wed = 0;
   int thu = 0;
   int fri = 0;
   int sat = 0;
   int sun = 0;
   int fragment = 0;
   int smonth = 0;
   int sday = 0;
   int syear = 0;
   int emonth = 0;
   int eday = 0;
   int eyear = 0;
   int hr = 0;
   int min = 0;
   int s_hr = 0;
   int s_min = 0;
   int e_hr = 0;
   int e_min = 0;
   int time = 0;
   int stime = 0;
   int etime = 0;
   int month = 0;
   int day = 0;
   int year = 0;

   long date = 0;
   long sdate = 0;
   long edate = 0;
   long mm = 0;
   long dd = 0;
   long yy = 0;

   boolean newBlock = false;

   try {
      id = Integer.parseInt(proid);              // id of pro for this lesson book
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   //
   //  Get the parms
   //
   if (req.getParameter("calDate") != null) {

      calDate = req.getParameter("calDate");          
   }
   if (req.getParameter("jump") != null) {

      jump = req.getParameter("jump");
   }
   if (req.getParameter("lbname") != null) {        // is this a request to edit an existing blocker ?

      lbname = req.getParameter("lbname");          // name of the blocker to process
   }
   if (req.getParameter("date") != null) {          // is this a request add a new blocker ?

      dates = req.getParameter("date");             // date of the lesson book
      date = Long.parseLong(dates);                 // convert
        
      yy = date / 10000;                             // get year
      mm = (date - (yy * 10000)) / 100;              // get month
      dd = (date - (yy * 10000)) - (mm * 100);       // get day

      month = (int)mm;
      day = (int)dd;
      year = (int)yy;
   }

   //
   //  Get this year
   //
   Calendar cal = new GregorianCalendar();       // get todays date
   int thisYear = cal.get(Calendar.YEAR);            // get the year

   //
   //  if request to edit an existing blocker, get the current info for the blocker requested
   //
   if (!lbname.equals( "" ) && lbname != null) {
     
      try {

         pstmt = con.prepareStatement (
                 "SELECT * FROM lessonblock5 WHERE proid = ? AND activity_id = ? AND lbname = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1,id);
         pstmt.setInt(2,sess_activity_id);
         pstmt.setString(3,lbname);
         rs = pstmt.executeQuery();      // execute the prepared pstmt

         if (rs.next()) {

            sdate = rs.getLong("sdate");
            stime = rs.getInt("stime");
            edate = rs.getLong("edate");
            etime = rs.getInt("etime");
            mon = rs.getInt("mon");
            tue = rs.getInt("tue");
            wed = rs.getInt("wed");
            thu = rs.getInt("thu");
            fri = rs.getInt("fri");
            sat = rs.getInt("sat");
            sun = rs.getInt("sun");
            color = rs.getString("color");
            fragment = rs.getInt("fragment");        // ??

         } else {

            nfError(req, out, lottery);            // LT not found
            pstmt.close();
            return;
         }
         pstmt.close();

      }
      catch (Exception exc) {

         dbError(req, out, exc, lottery);
         return;
      }
        
      //
      //  adjust some values 
      //
      yy = sdate / 10000;                             // get year
      mm = (sdate - (yy * 10000)) / 100;              // get month
      dd = (sdate - (yy * 10000)) - (mm * 100);       // get day

      smonth = (int)mm;
      sday = (int)dd;
      syear = (int)yy;

      yy = edate / 10000;                             // get year
      mm = (edate - (yy * 10000)) / 100;              // get month
      dd = (edate - (yy * 10000)) - (mm * 100);       // get day

      emonth = (int)mm;
      eday = (int)dd;
      eyear = (int)yy;

      s_hr = stime / 100;
      s_min = stime - (s_hr * 100);

      e_hr = etime / 100;
      e_min = etime - (e_hr * 100);

      if (s_hr == 12) {

         s_ampm = "PM";

      } else {

         if (s_hr > 12) {

            s_hr = s_hr - 12;          // adjust
            s_ampm = "PM";
         }
      }

      if (e_hr == 12) {

         e_ampm = "PM";

      } else {

         if (e_hr > 12) {

            e_hr = e_hr - 12;          // adjust
            e_ampm = "PM";
         }
      }
        
//      if (mon == 0 && tue == 0 && wed == 0 && thu == 0 && fri == 0 && sat == 0 && sun ==0) {  // if no recurr
//         today = 1;       // then must be today only
//      }

   } else {      // request is for a new blocker - set default values

      newBlock = true;        // indicate new blocker

      lbname = "";       // empty name
      smonth = month;    // set dates to this date
      sday = day;
      syear = year;
      emonth = month;
      eday = day;
      eyear = year;
   }

   //
   //  Build the HTML page to display the selected Lesson Time
   //
   out.println(SystemUtils.HeadTitle("Proshop Edit Lesson Time Blocker"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");

   out.println("<table cellpadding=\"5\" bgcolor=\"#336633\" border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"3\">");
   out.println("<b>Edit Lesson Time Blocker</b><br>");
   out.println("</font>");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   if (newBlock == true) {
      out.println("<br>To add a Blocker, complete the following and click on the Add button.");
   } else {
      out.println("<br>To update a Blocker, make the desired changes and click on the Update button.");
   }
   out.println("<br><br>Blocked times will not be available to members.  Proshop users will still have full access these times.");
   out.println("<br>You can use this feature to schedule other activities and plan your days, as well as block access.");
   out.println("<br></td></tr></table>");
   out.println("<br><br>");

   out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"6\">");
      out.println("<form action=\"Proshop_lesson\" method=\"post\" target=\"bot\" name=\"f\">");
      out.println("<input type=\"hidden\" name=\"proid\" value=\"" +proid+ "\">");
      out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" +jump+ "\">");
      out.println("<input type=\"hidden\" name=\"oldname\" value=\"" +lbname+ "\">");
      out.println("<input type=\"hidden\" name=\"oldsdate\" value=\"" +sdate+ "\">");
      out.println("<input type=\"hidden\" name=\"oldstime\" value=\"" +stime+ "\">");
      out.println("<input type=\"hidden\" name=\"oldedate\" value=\"" +edate+ "\">");
      out.println("<input type=\"hidden\" name=\"oldetime\" value=\"" +etime+ "\">");
      out.println("<input type=\"hidden\" name=\"oldtoday\" value=\"" +today+ "\">");
      out.println("<input type=\"hidden\" name=\"oldmon\" value=\"" +mon+ "\">");
      out.println("<input type=\"hidden\" name=\"oldtue\" value=\"" +tue+ "\">");
      out.println("<input type=\"hidden\" name=\"oldwed\" value=\"" +wed+ "\">");
      out.println("<input type=\"hidden\" name=\"oldthu\" value=\"" +thu+ "\">");
      out.println("<input type=\"hidden\" name=\"oldfri\" value=\"" +fri+ "\">");
      out.println("<input type=\"hidden\" name=\"oldsat\" value=\"" +sat+ "\">");
      out.println("<input type=\"hidden\" name=\"oldsun\" value=\"" +sun+ "\">");
      out.println("<input type=\"hidden\" name=\"oldcolor\" value=\"" +color+ "\">");
      out.println("<input type=\"hidden\" name=\"oldfragment\" value=\"" +fragment+ "\">");
      out.println("<tr>");
         out.println("<td width=\"560\">");
         out.println("<font size=\"2\">");
         out.println("<p align=\"left\">&nbsp;&nbsp;Name of Blocker or Activity:&nbsp;&nbsp;");
            out.println("<input type=\"text\" name=\"lbname\" value=\"" +lbname+ "\" size=\"30\" maxlength=\"40\">");
            out.println("&nbsp;&nbsp;&nbsp;* Must be unique");
         out.println("</p>");

         out.println("<p align=\"left\">&nbsp;&nbsp;Start Date:&nbsp;&nbsp;");
           out.println("   Month:&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"smonth\">");
           if (smonth == 1) {
              out.println("<option selected value=\"01\">JAN</option>");
           } else {
              out.println("<option value=\"01\">JAN</option>");
           }
           if (smonth == 2) {
              out.println("<option selected value=\"02\">FEB</option>");
           } else {
              out.println("<option value=\"02\">FEB</option>");
           }
           if (smonth == 3) {
              out.println("<option selected value=\"03\">MAR</option>");
           } else {
              out.println("<option value=\"03\">MAR</option>");
           }
           if (smonth == 4) {
              out.println("<option selected value=\"04\">APR</option>");
           } else {
              out.println("<option value=\"04\">APR</option>");
           }
           if (smonth == 5) {
              out.println("<option selected value=\"05\">MAY</option>");
           } else {
              out.println("<option value=\"05\">MAY</option>");
           }
           if (smonth == 6) {
              out.println("<option selected value=\"06\">JUN</option>");
           } else {
              out.println("<option value=\"06\">JUN</option>");
           }
           if (smonth == 7) {
              out.println("<option selected value=\"07\">JUL</option>");
           } else {
              out.println("<option value=\"07\">JUL</option>");
           }
           if (smonth == 8) {
              out.println("<option selected value=\"08\">AUG</option>");
           } else {
              out.println("<option value=\"08\">AUG</option>");
           }
           if (smonth == 9) {
              out.println("<option selected value=\"09\">SEP</option>");
           } else {
              out.println("<option value=\"09\">SEP</option>");
           }
           if (smonth == 10) {
              out.println("<option selected value=\"10\">OCT</option>");
           } else {
              out.println("<option value=\"10\">OCT</option>");
           }
           if (smonth == 11) {
              out.println("<option selected value=\"11\">NOV</option>");
           } else {
              out.println("<option value=\"11\">NOV</option>");
           }
           if (smonth == 12) {
              out.println("<option selected value=\"12\">DEC</option>");
           } else {
              out.println("<option value=\"12\">DEC</option>");
           }
           out.println("</select>");

           out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"sday\">");
           if (sday == 1) {
              out.println("<option selected selected value=\"01\">1</option>");
           } else {
              out.println("<option value=\"01\">1</option>");
           }
           if (sday == 2) {
              out.println("<option selected value=\"02\">2</option>");
           } else {
              out.println("<option value=\"02\">2</option>");
           }
           if (sday == 3) {
              out.println("<option selected value=\"03\">3</option>");
           } else {
              out.println("<option value=\"03\">3</option>");
           }
           if (sday == 4) {
              out.println("<option selected value=\"04\">4</option>");
           } else {
              out.println("<option value=\"04\">4</option>");
           }
           if (sday == 5) {
              out.println("<option selected value=\"05\">5</option>");
           } else {
              out.println("<option value=\"05\">5</option>");
           }
           if (sday == 6) {
              out.println("<option selected value=\"06\">6</option>");
           } else {
              out.println("<option value=\"06\">6</option>");
           }
           if (sday == 7) {
              out.println("<option selected value=\"07\">7</option>");
           } else {
              out.println("<option value=\"07\">7</option>");
           }
           if (sday == 8) {
              out.println("<option selected value=\"08\">8</option>");
           } else {
              out.println("<option value=\"08\">8</option>");
           }
           if (sday == 9) {
              out.println("<option selected value=\"09\">9</option>");
           } else {
              out.println("<option value=\"09\">9</option>");
           }
           if (sday == 10) {
              out.println("<option selected value=\"10\">10</option>");
           } else {
              out.println("<option value=\"10\">10</option>");
           }
           if (sday == 11) {
              out.println("<option selected value=\"11\">11</option>");
           } else {
              out.println("<option value=\"11\">11</option>");
           }
           if (sday == 12) {
              out.println("<option selected value=\"12\">12</option>");
           } else {
              out.println("<option value=\"12\">12</option>");
           }
           if (sday == 13) {
              out.println("<option selected value=\"13\">13</option>");
           } else {
              out.println("<option value=\"13\">13</option>");
           }
           if (sday == 14) {
              out.println("<option selected value=\"14\">14</option>");
           } else {
              out.println("<option value=\"14\">14</option>");
           }
           if (sday == 15) {
              out.println("<option selected value=\"15\">15</option>");
           } else {
              out.println("<option value=\"15\">15</option>");
           }
           if (sday == 16) {
              out.println("<option selected value=\"16\">16</option>");
           } else {
              out.println("<option value=\"16\">16</option>");
           }
           if (sday == 17) {
              out.println("<option selected value=\"17\">17</option>");
           } else {
              out.println("<option value=\"17\">17</option>");
           }
           if (sday == 18) {
              out.println("<option selected value=\"18\">18</option>");
           } else {
              out.println("<option value=\"18\">18</option>");
           }
           if (sday == 19) {
              out.println("<option selected value=\"19\">19</option>");
           } else {
              out.println("<option value=\"19\">19</option>");
           }
           if (sday == 20) {
              out.println("<option selected value=\"20\">20</option>");
           } else {
              out.println("<option value=\"20\">20</option>");
           }
           if (sday == 21) {
              out.println("<option selected value=\"21\">21</option>");
           } else {
              out.println("<option value=\"21\">21</option>");
           }
           if (sday == 22) {
              out.println("<option selected value=\"22\">22</option>");
           } else {
              out.println("<option value=\"22\">22</option>");
           }
           if (sday == 23) {
              out.println("<option selected value=\"23\">23</option>");
           } else {
              out.println("<option value=\"23\">23</option>");
           }
           if (sday == 24) {
              out.println("<option selected value=\"24\">24</option>");
           } else {
              out.println("<option value=\"24\">24</option>");
           }
           if (sday == 25) {
              out.println("<option selected value=\"25\">25</option>");
           } else {
              out.println("<option value=\"25\">25</option>");
           }
           if (sday == 26) {
              out.println("<option selected value=\"26\">26</option>");
           } else {
              out.println("<option value=\"26\">26</option>");
           }
           if (sday == 27) {
              out.println("<option selected value=\"27\">27</option>");
           } else {
              out.println("<option value=\"27\">27</option>");
           }
           if (sday == 28) {
              out.println("<option selected value=\"28\">28</option>");
           } else {
              out.println("<option value=\"28\">28</option>");
           }
           if (sday == 29) {
              out.println("<option selected value=\"29\">29</option>");
           } else {
              out.println("<option value=\"29\">29</option>");
           }
           if (sday == 30) {
              out.println("<option selected value=\"30\">30</option>");
           } else {
              out.println("<option value=\"30\">30</option>");
           }
           if (sday == 31) {
              out.println("<option selected value=\"31\">31</option>");
           } else {
              out.println("<option value=\"31\">31</option>");
           }
           out.println("</select>");

           year = thisYear;

           out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"syear\">");
             if (syear < year) {     // if prior to this year
                out.println("<option selected value=\"" +syear+ "\">" +syear+ "</option>");
             }
             if (syear == year) {
                out.println("<option selected value=\"" +year+ "\">" +year+ "</option>");
             } else {
                out.println("<option value=\"" +year+ "\">" +year+ "</option>");
             }
             year++;    // next year
             if (syear == year) {
                out.println("<option selected value=\"" +year+ "\">" +year+ "</option>");
             } else {
                out.println("<option value=\"" +year+ "\">" +year+ "</option>");
             }
             year++;    // next year
             if (syear == year) {
                out.println("<option selected value=\"" +year+ "\">" +year+ "</option>");
             } else {
                out.println("<option value=\"" +year+ "\">" +year+ "</option>");
             }
           out.println("</select>");
         out.println("</p>");
         out.println("<p align=\"left\">&nbsp;&nbsp;End Date:&nbsp;&nbsp;&nbsp;");
           out.println("Month:&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"emonth\">");
           if (emonth == 1) {
              out.println("<option selected value=\"01\">JAN</option>");
           } else {
              out.println("<option value=\"01\">JAN</option>");
           }
           if (emonth == 2) {
              out.println("<option selected value=\"02\">FEB</option>");
           } else {
              out.println("<option value=\"02\">FEB</option>");
           }
           if (emonth == 3) {
              out.println("<option selected value=\"03\">MAR</option>");
           } else {
              out.println("<option value=\"03\">MAR</option>");
           }
           if (emonth == 4) {
              out.println("<option selected value=\"04\">APR</option>");
           } else {
              out.println("<option value=\"04\">APR</option>");
           }
           if (emonth == 5) {
              out.println("<option selected value=\"05\">MAY</option>");
           } else {
              out.println("<option value=\"05\">MAY</option>");
           }
           if (emonth == 6) {
              out.println("<option selected value=\"06\">JUN</option>");
           } else {
              out.println("<option value=\"06\">JUN</option>");
           }
           if (emonth == 7) {
              out.println("<option selected value=\"07\">JUL</option>");
           } else {
              out.println("<option value=\"07\">JUL</option>");
           }
           if (emonth == 8) {
              out.println("<option selected value=\"08\">AUG</option>");
           } else {
              out.println("<option value=\"08\">AUG</option>");
           }
           if (emonth == 9) {
              out.println("<option selected value=\"09\">SEP</option>");
           } else {
              out.println("<option value=\"09\">SEP</option>");
           }
           if (emonth == 10) {
              out.println("<option selected value=\"10\">OCT</option>");
           } else {
              out.println("<option value=\"10\">OCT</option>");
           }
           if (emonth == 11) {
              out.println("<option selected value=\"11\">NOV</option>");
           } else {
              out.println("<option value=\"11\">NOV</option>");
           }
           if (emonth == 12) {
              out.println("<option selected value=\"12\">DEC</option>");
           } else {
              out.println("<option value=\"12\">DEC</option>");
           }
           out.println("</select>");

           out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"eday\">");
           if (eday == 1) {
              out.println("<option selected selected value=\"01\">1</option>");
           } else {
              out.println("<option value=\"01\">1</option>");
           }
           if (eday == 2) {
              out.println("<option selected value=\"02\">2</option>");
           } else {
              out.println("<option value=\"02\">2</option>");
           }
           if (eday == 3) {
              out.println("<option selected value=\"03\">3</option>");
           } else {
              out.println("<option value=\"03\">3</option>");
           }
           if (eday == 4) {
              out.println("<option selected value=\"04\">4</option>");
           } else {
              out.println("<option value=\"04\">4</option>");
           }
           if (eday == 5) {
              out.println("<option selected value=\"05\">5</option>");
           } else {
              out.println("<option value=\"05\">5</option>");
           }
           if (eday == 6) {
              out.println("<option selected value=\"06\">6</option>");
           } else {
              out.println("<option value=\"06\">6</option>");
           }
           if (eday == 7) {
              out.println("<option selected value=\"07\">7</option>");
           } else {
              out.println("<option value=\"07\">7</option>");
           }
           if (eday == 8) {
              out.println("<option selected value=\"08\">8</option>");
           } else {
              out.println("<option value=\"08\">8</option>");
           }
           if (eday == 9) {
              out.println("<option selected value=\"09\">9</option>");
           } else {
              out.println("<option value=\"09\">9</option>");
           }
           if (eday == 10) {
              out.println("<option selected value=\"10\">10</option>");
           } else {
              out.println("<option value=\"10\">10</option>");
           }
           if (eday == 11) {
              out.println("<option selected value=\"11\">11</option>");
           } else {
              out.println("<option value=\"11\">11</option>");
           }
           if (eday == 12) {
              out.println("<option selected value=\"12\">12</option>");
           } else {
              out.println("<option value=\"12\">12</option>");
           }
           if (eday == 13) {
              out.println("<option selected value=\"13\">13</option>");
           } else {
              out.println("<option value=\"13\">13</option>");
           }
           if (eday == 14) {
              out.println("<option selected value=\"14\">14</option>");
           } else {
              out.println("<option value=\"14\">14</option>");
           }
           if (eday == 15) {
              out.println("<option selected value=\"15\">15</option>");
           } else {
              out.println("<option value=\"15\">15</option>");
           }
           if (eday == 16) {
              out.println("<option selected value=\"16\">16</option>");
           } else {
              out.println("<option value=\"16\">16</option>");
           }
           if (eday == 17) {
              out.println("<option selected value=\"17\">17</option>");
           } else {
              out.println("<option value=\"17\">17</option>");
           }
           if (eday == 18) {
              out.println("<option selected value=\"18\">18</option>");
           } else {
              out.println("<option value=\"18\">18</option>");
           }
           if (eday == 19) {
              out.println("<option selected value=\"19\">19</option>");
           } else {
              out.println("<option value=\"19\">19</option>");
           }
           if (eday == 20) {
              out.println("<option selected value=\"20\">20</option>");
           } else {
              out.println("<option value=\"20\">20</option>");
           }
           if (eday == 21) {
              out.println("<option selected value=\"21\">21</option>");
           } else {
              out.println("<option value=\"21\">21</option>");
           }
           if (eday == 22) {
              out.println("<option selected value=\"22\">22</option>");
           } else {
              out.println("<option value=\"22\">22</option>");
           }
           if (eday == 23) {
              out.println("<option selected value=\"23\">23</option>");
           } else {
              out.println("<option value=\"23\">23</option>");
           }
           if (eday == 24) {
              out.println("<option selected value=\"24\">24</option>");
           } else {
              out.println("<option value=\"24\">24</option>");
           }
           if (eday == 25) {
              out.println("<option selected value=\"25\">25</option>");
           } else {
              out.println("<option value=\"25\">25</option>");
           }
           if (eday == 26) {
              out.println("<option selected value=\"26\">26</option>");
           } else {
              out.println("<option value=\"26\">26</option>");
           }
           if (eday == 27) {
              out.println("<option selected value=\"27\">27</option>");
           } else {
              out.println("<option value=\"27\">27</option>");
           }
           if (eday == 28) {
              out.println("<option selected value=\"28\">28</option>");
           } else {
              out.println("<option value=\"28\">28</option>");
           }
           if (eday == 29) {
              out.println("<option selected value=\"29\">29</option>");
           } else {
              out.println("<option value=\"29\">29</option>");
           }
           if (eday == 30) {
              out.println("<option selected value=\"30\">30</option>");
           } else {
              out.println("<option value=\"30\">30</option>");
           }
           if (eday == 31) {
              out.println("<option selected value=\"31\">31</option>");
           } else {
              out.println("<option value=\"31\">31</option>");
           }
           out.println("</select>");

           year = thisYear;

           out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"eyear\">");
             if (eyear < year) {     // if prior to this year
                out.println("<option selected value=\"" +eyear+ "\">" +eyear+ "</option>");
             }
             if (eyear == year) {
                out.println("<option selected value=\"" +year+ "\">" +year+ "</option>");
             } else {
                out.println("<option value=\"" +year+ "\">" +year+ "</option>");
             }
             year++;    // next year
             if (eyear == year) {
                out.println("<option selected value=\"" +year+ "\">" +year+ "</option>");
             } else {
                out.println("<option value=\"" +year+ "\">" +year+ "</option>");
             }
             year++;    // next year
             if (eyear == year) {
                out.println("<option selected value=\"" +year+ "\">" +year+ "</option>");
             } else {
                out.println("<option value=\"" +year+ "\">" +year+ "</option>");
             }
           out.println("</select>");
         out.println("</p>");

         out.println("<p align=\"left\">&nbsp;&nbsp;Start Time: &nbsp;&nbsp; hr &nbsp;");
           out.println("<select size=\"1\" name=\"start_hr\">");
             if (s_hr == 1) {
                out.println("<option selected selected value=\"01\">1</option>");
             } else {
                out.println("<option value=\"01\">1</option>");
             }
             if (s_hr == 2) {
                out.println("<option selected value=\"02\">2</option>");
             } else {
                out.println("<option value=\"02\">2</option>");
             }
             if (s_hr == 3) {
                out.println("<option selected value=\"03\">3</option>");
             } else {
                out.println("<option value=\"03\">3</option>");
             }
             if (s_hr == 4) {
                out.println("<option selected value=\"04\">4</option>");
             } else {
                out.println("<option value=\"04\">4</option>");
             }
             if (s_hr == 5) {
                out.println("<option selected value=\"05\">5</option>");
             } else {
                out.println("<option value=\"05\">5</option>");
             }
             if (s_hr == 6) {
                out.println("<option selected value=\"06\">6</option>");
             } else {
                out.println("<option value=\"06\">6</option>");
             }
             if (s_hr == 7) {
                out.println("<option selected value=\"07\">7</option>");
             } else {
                out.println("<option value=\"07\">7</option>");
             }
             if (s_hr == 8) {
                out.println("<option selected value=\"08\">8</option>");
             } else {
                out.println("<option value=\"08\">8</option>");
             }
             if (s_hr == 9) {
                out.println("<option selected value=\"09\">9</option>");
             } else {
                out.println("<option value=\"09\">9</option>");
             }
             if (s_hr == 10) {
                out.println("<option selected value=\"10\">10</option>");
             } else {
                out.println("<option value=\"10\">10</option>");
             }
             if (s_hr == 11) {
                out.println("<option selected value=\"11\">11</option>");
             } else {
                out.println("<option value=\"11\">11</option>");
             }
             if (s_hr == 12) {
                out.println("<option selected value=\"12\">12</option>");
             } else {
                out.println("<option value=\"12\">12</option>");
             }
           out.println("</select>");
           out.println("&nbsp; min &nbsp;");
           if (s_min < 10) {
              out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=0" + s_min + " name=\"start_min\">");
           } else {
              out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + s_min + " name=\"start_min\">");
           }
           out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"start_ampm\">");
             if (s_ampm.equals( "AM" )) {
                out.println("<option selected value=\"00\">AM</option>");
             } else {
                out.println("<option value=\"00\">AM</option>");
             }
             if (s_ampm.equals( "PM" )) {
                out.println("<option selected value=\"12\">PM</option>");
             } else {
                out.println("<option value=\"12\">PM</option>");
             }
           out.println("</select>");
         out.println("</p>");
         out.println("<p align=\"left\">&nbsp;&nbsp;End Time: &nbsp;&nbsp;&nbsp;&nbsp; hr &nbsp;");
           out.println("<select size=\"1\" name=\"end_hr\">");
             if (e_hr == 1) {
                out.println("<option selected selected value=\"01\">1</option>");
             } else {
                out.println("<option value=\"01\">1</option>");
             }
             if (e_hr == 2) {
                out.println("<option selected value=\"02\">2</option>");
             } else {
                out.println("<option value=\"02\">2</option>");
             }
             if (e_hr == 3) {
                out.println("<option selected value=\"03\">3</option>");
             } else {
                out.println("<option value=\"03\">3</option>");
             }
             if (e_hr == 4) {
                out.println("<option selected value=\"04\">4</option>");
             } else {
                out.println("<option value=\"04\">4</option>");
             }
             if (e_hr == 5) {
                out.println("<option selected value=\"05\">5</option>");
             } else {
                out.println("<option value=\"05\">5</option>");
             }
             if (e_hr == 6) {
                out.println("<option selected value=\"06\">6</option>");
             } else {
                out.println("<option value=\"06\">6</option>");
             }
             if (e_hr == 7) {
                out.println("<option selected value=\"07\">7</option>");
             } else {
                out.println("<option value=\"07\">7</option>");
             }
             if (e_hr == 8) {
                out.println("<option selected value=\"08\">8</option>");
             } else {
                out.println("<option value=\"08\">8</option>");
             }
             if (e_hr == 9) {
                out.println("<option selected value=\"09\">9</option>");
             } else {
                out.println("<option value=\"09\">9</option>");
             }
             if (e_hr == 10) {
                out.println("<option selected value=\"10\">10</option>");
             } else {
                out.println("<option value=\"10\">10</option>");
             }
             if (e_hr == 11) {
                out.println("<option selected value=\"11\">11</option>");
             } else {
                out.println("<option value=\"11\">11</option>");
             }
             if (e_hr == 12) {
                out.println("<option selected value=\"12\">12</option>");
             } else {
                out.println("<option value=\"12\">12</option>");
             }
           out.println("</select>");
           out.println("&nbsp; min &nbsp;");
           if (e_min < 10) {
              out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=0" + e_min + " name=\"end_min\">");
           } else {
              out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + e_min + " name=\"end_min\">");
           }
           out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"end_ampm\">");
             if (e_ampm.equals( "AM" )) {
                out.println("<option selected value=\"00\">AM</option>");
             } else {
                out.println("<option value=\"00\">AM</option>");
             }
             if (e_ampm.equals( "PM" )) {
                out.println("<option selected value=\"12\">PM</option>");
             } else {
                out.println("<option value=\"12\">PM</option>");
             }
           out.println("</select>");
         out.println("</p>");
         out.println("<p align=\"Left\">&nbsp;&nbsp;Recurrence (select all that apply):");
            out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
              
/*
            if (today == 1) {
               out.println("<input type=\"checkbox\" name=\"today\" checked value=\"1\">&nbsp;&nbsp;Today Only");
               out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; - OR -");
               out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("<input type=\"checkbox\" name=\"mon\" value=\"1\">&nbsp;&nbsp;Every Monday");
               out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("<input type=\"checkbox\" name=\"tue\" value=\"1\">&nbsp;&nbsp;Every Tuesday");
               out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("<input type=\"checkbox\" name=\"wed\" value=\"1\">&nbsp;&nbsp;Every Wednesday");
               out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("<input type=\"checkbox\" name=\"thu\" value=\"1\">&nbsp;&nbsp;Every Thursday");
               out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("<input type=\"checkbox\" name=\"fri\" value=\"1\">&nbsp;&nbsp;Every Friday");
               out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("<input type=\"checkbox\" name=\"sat\" value=\"1\">&nbsp;&nbsp;Every Saturday");
               out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("<input type=\"checkbox\" name=\"sun\" value=\"1\">&nbsp;&nbsp;Every Sunday");
                 
            } else {

               out.println("<input type=\"checkbox\" name=\"today\" value=\"1\">&nbsp;&nbsp;Today Only");
               out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; - OR -");
*/
               out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               if (mon == 1) {
                  out.println("<input type=\"checkbox\" name=\"mon\" checked value=\"1\">&nbsp;&nbsp;Every Monday");
               } else {
                  out.println("<input type=\"checkbox\" name=\"mon\" value=\"1\">&nbsp;&nbsp;Every Monday");
               }
               out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               if (tue == 1) {
                  out.println("<input type=\"checkbox\" name=\"tue\" checked value=\"1\">&nbsp;&nbsp;Every Tuesday");
               } else {
                  out.println("<input type=\"checkbox\" name=\"tue\" value=\"1\">&nbsp;&nbsp;Every Tuesday");
               }
               out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               if (wed == 1) {
                  out.println("<input type=\"checkbox\" name=\"wed\" checked value=\"1\">&nbsp;&nbsp;Every Wednesday");
               } else {
                  out.println("<input type=\"checkbox\" name=\"wed\" value=\"1\">&nbsp;&nbsp;Every Wednesday");
               }
               out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               if (thu == 1) {
                  out.println("<input type=\"checkbox\" name=\"thu\" checked value=\"1\">&nbsp;&nbsp;Every Thursday");
               } else {
                  out.println("<input type=\"checkbox\" name=\"thu\" value=\"1\">&nbsp;&nbsp;Every Thursday");
               }
               out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               if (fri == 1) {
                  out.println("<input type=\"checkbox\" name=\"fri\" checked value=\"1\">&nbsp;&nbsp;Every Friday");
               } else {
                  out.println("<input type=\"checkbox\" name=\"fri\" value=\"1\">&nbsp;&nbsp;Every Friday");
               }
               out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               if (sat == 1) {
                  out.println("<input type=\"checkbox\" name=\"sat\" checked value=\"1\">&nbsp;&nbsp;Every Saturday");
               } else {
                  out.println("<input type=\"checkbox\" name=\"sat\" value=\"1\">&nbsp;&nbsp;Every Saturday");
               }
               out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               if (sun == 1) {
                  out.println("<input type=\"checkbox\" name=\"sun\" checked value=\"1\">&nbsp;&nbsp;Every Sunday");
               } else {
                  out.println("<input type=\"checkbox\" name=\"sun\" value=\"1\">&nbsp;&nbsp;Every Sunday");
               }
//            }
         out.println("</p>");

         out.println("<p align=\"left\">&nbsp;&nbsp;Color to make these times in the Lesson Book:&nbsp;&nbsp;");
         out.println("<select size=\"1\" name=\"color\">");

             if (color.equals( "Default" ) || color.equals( "" )) {

               out.println("<option selected value=" + color + ">Default (none)</option>");
             } else {
               out.println("<option selected value=" + color + ">" + color + "</option>");
             }
             out.println("<option value=\"Antiquewhite\">Antique White</option>");
             out.println("<option value=\"Aqua\">Aqua</option>");
             out.println("<option value=\"Aquamarine\">Aquamarine</option>");
             out.println("<option value=\"Beige\">Beige</option>");
             out.println("<option value=\"Bisque\">Bisque</option>");
             out.println("<option value=\"Blanchedalmond\">Blanched Almond</option>");
             out.println("<option value=\"Blue\">Blue</option>");
             out.println("<option value=\"Bluevoilet\">Blueviolet</option>");
             out.println("<option value=\"Brown\">Brown</option>");
             out.println("<option value=\"Burlywood\">Burlywood</option>");
             out.println("<option value=\"Cadetblue\">Cadetblue</option>");
             out.println("<option value=\"Chartreuse\">Chartreuse</option>");
             out.println("<option value=\"Chocolate\">Chocolate</option>");
             out.println("<option value=\"Coral\">Coral</option>");
             out.println("<option value=\"Cornflowerblue\">Cornflowerblue</option>");
             out.println("<option value=\"Cornsilk\">Cornsilk</option>");
             out.println("<option value=\"Crimson\">Crimson</option>");
             out.println("<option value=\"Cyan\">Cyan</option>");
             out.println("<option value=\"Darkblue\">Darkblue</option>");
             out.println("<option value=\"Darkcyan\">Darkcyan</option>");
             out.println("<option value=\"Darkgoldenrod\">Darkgoldenrod</option>");
             out.println("<option value=\"Darkgray\">Darkgray</option>");
             out.println("<option value=\"Darkgreen\">Darkgreen</option>");
             out.println("<option value=\"Darkkhaki\">Darkkhaki</option>");
             out.println("<option value=\"Darkmagenta\">Darkmagenta</option>");
             out.println("<option value=\"Darkolivegreen\">Darkolivegreen</option>");
             out.println("<option value=\"Darkorange\">Darkorange</option>");
             out.println("<option value=\"Darkorchid\">Darkorchid</option>");
             out.println("<option value=\"Darkred\">Darkred</option>");
             out.println("<option value=\"Darksalmon\">Darksalmon</option>");
             out.println("<option value=\"Darkseagreen\">Darkseagreen</option>");
             out.println("<option value=\"Darkslateblue\">Darkslateblue</option>");
             out.println("<option value=\"Darkslategray\">Darkslategray</option>");
             out.println("<option value=\"Darkturquoise\">Darkturquoise</option>");
             out.println("<option value=\"Darkviolet\">Darkviolet</option>");
             out.println("<option value=\"Deeppink\">Deeppink</option>");
             out.println("<option value=\"Deepskyblue\">Deepskyblue</option>");
             out.println("<option value=\"Default\">Default (none)</option>");
             out.println("<option value=\"Dimgray\">Dimgray</option>");
             out.println("<option value=\"Dodgerblue\">Dodgerblue</option>");
             out.println("<option value=\"Firebrick\">Firebrick</option>");
             out.println("<option value=\"Forestgreen\">Forestgreen</option>");
             out.println("<option value=\"Fuchsia\">Fuchsia</option>");
             out.println("<option value=\"Gainsboro\">Gainsboro</option>");
             out.println("<option value=\"Gold\">Gold</option>");
             out.println("<option value=\"Goldenrod\">Goldenrod</option>");
             out.println("<option value=\"Gray\">Gray</option>");
             out.println("<option value=\"Green\">Green</option>");
             out.println("<option value=\"Greenyellow\">Greenyellow</option>");
             out.println("<option value=\"Hotpink\">Hotpink</option>");
             out.println("<option value=\"Indianred\">Indianred</option>");
             out.println("<option value=\"Indigo\">Indigo</option>");
             out.println("<option value=\"Ivory\">Ivory</option>");
             out.println("<option value=\"Khaki\">Khaki</option>");
             out.println("<option value=\"Lavender\">Lavender</option>");
             out.println("<option value=\"Lavenderblush\">Lavenderblush</option>");
             out.println("<option value=\"Lawngreen\">Lawngreen</option>");
             out.println("<option value=\"Lemonchiffon\">Lemonchiffon</option>");
             out.println("<option value=\"Lightblue\">Lightblue</option>");
             out.println("<option value=\"Lightcoral\">Lightcoral</option>");
             out.println("<option value=\"Lightgoldenrodyellow\">Lightgoldenrodyellow</option>");
             out.println("<option value=\"Lightgreen\">Lightgreen</option>");
             out.println("<option value=\"Lightgrey\">Lightgrey</option>");
             out.println("<option value=\"Lightpink\">Lightpink</option>");
             out.println("<option value=\"Lightsalmon\">Lightsalmon</option>");
             out.println("<option value=\"Lightseagreen\">Lightseagreen</option>");
             out.println("<option value=\"Lightskyblue\">Lightskyblue</option>");
             out.println("<option value=\"Lightslategray\">Lightslategray</option>");
             out.println("<option value=\"Lightsteelblue\">Lightsteelblue</option>");
             out.println("<option value=\"Lime\">Lime</option>");
             out.println("<option value=\"Limegreen\">Limegreen</option>");
             out.println("<option value=\"Linen\">Linen</option>");
             out.println("<option value=\"Magenta\">Magenta</option>");
             out.println("<option value=\"Mediumauqamarine\">Mediumauqamarine</option>");
             out.println("<option value=\"Mediumblue\">Mediumblue</option>");
             out.println("<option value=\"Mediumorchid\">Mediumorchid</option>");
             out.println("<option value=\"Mediumpurple\">Mediumpurple</option>");
             out.println("<option value=\"Mediumseagreen\">Mediumseagreen</option>");
             out.println("<option value=\"Mediumslateblue\">Mediumslateblue</option>");
             out.println("<option value=\"Mediumspringgreen\">Mediumspringgreen</option>");
             out.println("<option value=\"Mediumturquoise\">Mediumturquoise</option>");
             out.println("<option value=\"Mediumvioletred\">Mediumvioletred</option>");
             out.println("<option value=\"Mistyrose\">Mistyrose</option>");
             out.println("<option value=\"Moccasin\">Moccasin</option>");
             out.println("<option value=\"Navajowhite\">Navajowhite</option>");
             out.println("<option value=\"Navy\">Navy</option>");
             out.println("<option value=\"Oldlace\">Oldlace</option>");
             out.println("<option value=\"Olive\">Olive</option>");
             out.println("<option value=\"Olivedrab\">Olivedrab</option>");
             out.println("<option value=\"Orange\">Orange</option>");
             out.println("<option value=\"Orangered\">Orangered</option>");
             out.println("<option value=\"Orchid\">Orchid</option>");
             out.println("<option value=\"Palegoldenrod\">Palegoldenrod</option>");
             out.println("<option value=\"Palegreen\">Palegreen</option>");
             out.println("<option value=\"Paleturquoise\">Paleturquoise</option>");
             out.println("<option value=\"Palevioletred\">Palevioletred</option>");
             out.println("<option value=\"Papayawhip\">Papayawhip</option>");
             out.println("<option value=\"Peachpuff\">Peachpuff</option>");
             out.println("<option value=\"Peru\">Peru</option>");
             out.println("<option value=\"Pink\">Pink</option>");
             out.println("<option value=\"Plum\">Plum</option>");
             out.println("<option value=\"Powderblue\">Powderblue</option>");
             out.println("<option value=\"Purple\">Purple</option>");
             out.println("<option value=\"Red\">Red</option>");
             out.println("<option value=\"Rosybrown\">Rosybrown</option>");
             out.println("<option value=\"Royalblue\">Royalblue</option>");
             out.println("<option value=\"Saddlebrown\">Saddlebrown</option>");
             out.println("<option value=\"Salmon\">Salmon</option>");
             out.println("<option value=\"Sandybrown\">Sandybrown</option>");
             out.println("<option value=\"Seagreen\">Seagreen</option>");
             out.println("<option value=\"Sienna\">Sienna</option>");
             out.println("<option value=\"Silver\">Silver</option>");
             out.println("<option value=\"Skyblue\">Skyblue</option>");
             out.println("<option value=\"Slateblue\">Slateblue</option>");
             out.println("<option value=\"Slategray\">Slategray</option>");
             out.println("<option value=\"Springgreen\">Springgreen</option>");
             out.println("<option value=\"Steelblue\">Steelblue</option>");
             out.println("<option value=\"Tan\">Tan</option>");
             out.println("<option value=\"Teal\">Teal</option>");
             out.println("<option value=\"Thistle\">Thistle</option>");
             out.println("<option value=\"Tomato\">Tomato</option>");
             out.println("<option value=\"Turquoise\">Turquoise</option>");
             out.println("<option value=\"Violet\">Violet</option>");
             out.println("<option value=\"Wheat\">Wheat</option>");
             out.println("<option value=\"Yellow\">Yellow</option>");
             out.println("<option value=\"YellowGreen\">YellowGreen</option>");
         out.println("</select>");
         out.println("<br>");
         out.println("&nbsp;&nbsp;Click here to see the available colors:&nbsp;");
         out.println("<a href=\"/" +rev+ "/proshop_color.htm\" target=\"_blank\">View Colors</a>");
         out.println("</p>");

         out.println("<p align=\"center\">");
         if (newBlock == true) {                    // if add blocker
            out.println("<input type=\"submit\" name=\"addblock\" value=\"Add\">");    // to add record
         } else {
            out.println("<input type=\"submit\" name=\"deleteblock\" value=\"Remove Blocker\">"); // to delete record
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<input type=\"submit\" name=\"editblock\" value=\"Update Blocker\">");   // to update record
         }
         out.println("</p>");
         out.println("</font>");
         out.println("</td>");
      out.println("</tr>");
      out.println("</form>");
   out.println("</table>");

   out.println("</td></tr></table>");                // end of main page table
   out.println("<font size=\"2\">");
   if (newBlock == true) {                    // if add blocker
      out.println("<form action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"lesson\" value=\"yes\">");
      out.println("<input type=\"hidden\" name=\"proid\" value=" +id+ ">");
      out.println("<input type=\"hidden\" name=\"jump\" value=" +jump+ ">");
      out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");
   }
   out.println("<form method=\"get\" action=\"Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</center></font></body></html>");
   out.close();
 }


 // ********************************************************************
 //  Process the 'Edit Block' or 'Add Block' SUBMIT request
 // ********************************************************************

 private void blockTime2(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException {

   PreparedStatement pstmt = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String lbname = "";
   String frags = "";
   String color = "";
   String error = "";
   String calDate = "";
   String jump = "";

   String oldname = "";     // original parm values before edit
   String oldsdates = "";
   String oldstimes = "";
   String oldedates = "";
   String oldetimes = "";
   String oldtodays = "";
   String oldmons = "";
   String oldtues = "";
   String oldweds = "";
   String oldthus = "";
   String oldfris = "";
   String oldsats = "";
   String oldsuns = "";
   String oldcolor = "";
   String oldfrags = "";

   int id = 0;
   int today = 0;
   int sun = 0;
   int mon = 0;
   int tue = 0;
   int wed = 0;
   int thu = 0;
   int fri = 0;
   int sat = 0;
   int fragment = 0;
   int smonth = 0;                // init integer date/time values
   int sday = 0;
   int syear = 0;
   int emonth = 0;
   int eday = 0;
   int eyear = 0;
   int s_hr = 0;
   int s_min = 0;
   int s_ampm = 0;
   int e_hr = 0;
   int e_min = 0;
   int e_ampm = 0;
   int stime = 0;
   int etime = 0;
   int oldstime = 0;
   int oldetime = 0;
   int oldtoday = 0;
   int oldmon = 0;
   int oldtue = 0;
   int oldwed = 0;
   int oldthu = 0;
   int oldfri = 0;
   int oldsat = 0;
   int oldsun = 0;
   int oldfragment = 0;

   long sdate = 0;
   long edate = 0;
   long oldsdate = 0;
   long oldedate = 0;

   boolean addBlock = false;


   try {
      id = Integer.parseInt(proid);          // convert the pro id
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   //
   //  Get the parameters
   //
   String s_month = req.getParameter("smonth");             //  month (00 - 12)
   String s_day = req.getParameter("sday");                 //  day (01 - 31)
   String s_year = req.getParameter("syear");               //  year
   String start_hr = req.getParameter("start_hr");         //  start hour (01 - 12)
   String start_min = req.getParameter("start_min");       //  start min (00 - 59)
   String start_ampm = req.getParameter("start_ampm");     //  AM/PM (00 or 12)
   String e_month = req.getParameter("emonth");
   String e_day = req.getParameter("eday");
   String e_year = req.getParameter("eyear");
   String end_hr = req.getParameter("end_hr");
   String end_min = req.getParameter("end_min");
   String end_ampm = req.getParameter("end_ampm");

   if (req.getParameter("lbname") != null) {

      lbname = req.getParameter("lbname");
   }

   if (req.getParameter("fragment") != null) {

      frags = req.getParameter("fragment");

      try {
         fragment = Integer.parseInt(frags);
      }
      catch (NumberFormatException e) {
         fragment = 0;
      }
   }
   if (req.getParameter("color") != null) {

      color = req.getParameter("color");
   }
   if (req.getParameter("calDate") != null) {

      calDate = req.getParameter("calDate");
   }
   if (req.getParameter("jump") != null) {

      jump = req.getParameter("jump");
   }

   if (req.getParameter("today") != null) {

      today = 1;           // 'today only' check box selected
   }
   if (req.getParameter("mon") != null) {

      mon = 1;           // recurr check box selected
   }
   if (req.getParameter("tue") != null) {

      tue = 1;           // recurr check box selected
   }
   if (req.getParameter("wed") != null) {

      wed = 1;           // recurr check box selected
   }
   if (req.getParameter("thu") != null) {

      thu = 1;           // recurr check box selected
   }
   if (req.getParameter("fri") != null) {

      fri = 1;           // recurr check box selected
   }
   if (req.getParameter("sat") != null) {

      sat = 1;           // recurr check box selected
   }
   if (req.getParameter("sun") != null) {

      sun = 1;           // recurr check box selected
   }

   if (req.getParameter("addblock") != null) {

      addBlock = true;           // this is an add
   }

   //
   //  Get the original values before the edit
   //
   oldname = req.getParameter("oldname");
   oldsdates = req.getParameter("oldsdate");
   oldstimes = req.getParameter("oldstime");
   oldedates = req.getParameter("oldedate");
   oldetimes = req.getParameter("oldetime");
   oldtodays = req.getParameter("oldtoday");
   oldmons = req.getParameter("oldmon");
   oldtues = req.getParameter("oldtue");
   oldweds = req.getParameter("oldwed");
   oldthus = req.getParameter("oldthu");
   oldfris = req.getParameter("oldfri");
   oldsats = req.getParameter("oldsat");
   oldsuns = req.getParameter("oldsun");
   oldcolor = req.getParameter("oldcolor");
   oldfrags = req.getParameter("oldfragment");

   //
   // Convert the numeric string parameters to Int's
   //
   try {
      smonth = Integer.parseInt(s_month);
      sday = Integer.parseInt(s_day);
      syear = Integer.parseInt(s_year);
      s_hr = Integer.parseInt(start_hr);
      s_min = Integer.parseInt(start_min);
      s_ampm = Integer.parseInt(start_ampm);
      emonth = Integer.parseInt(e_month);
      eday = Integer.parseInt(e_day);
      eyear = Integer.parseInt(e_year);
      e_hr = Integer.parseInt(end_hr);
      e_min = Integer.parseInt(end_min);
      e_ampm = Integer.parseInt(end_ampm);
      oldtoday = Integer.parseInt(oldtodays);
      oldmon = Integer.parseInt(oldmons);
      oldtue = Integer.parseInt(oldtues);
      oldwed = Integer.parseInt(oldweds);
      oldthu = Integer.parseInt(oldthus);
      oldfri = Integer.parseInt(oldfris);
      oldsat = Integer.parseInt(oldsats);
      oldsun = Integer.parseInt(oldsuns);
      oldfragment = Integer.parseInt(oldfrags);
      oldstime = Integer.parseInt(oldstimes);
      oldetime = Integer.parseInt(oldetimes);
      oldsdate = Long.parseLong(oldsdates);
      oldedate = Long.parseLong(oldedates);
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }

   //
   //  If request is to delete the blocker, then do it
   //
   if (req.getParameter("deleteblock") != null) {

      try {
         pstmt = con.prepareStatement (
                  "Delete FROM lessonblock5 WHERE proid = ? AND activity_id = ? AND lbname = ?");

         pstmt.clearParameters();               // clear the parms
         pstmt.setInt(1, id);
         pstmt.setInt(2, sess_activity_id);
         pstmt.setString(3, oldname);
         pstmt.executeUpdate();         // execute the prepared pstmt

         pstmt.close();

      }
      catch (Exception exc) {

         dbError(req, out, exc, lottery);
         return;
      }

      //
      //  Now remove the blocker from all lesson times
      //
      try {

         removeBlocker(sess_activity_id, oldsun, oldmon, oldtue, oldwed, oldthu, oldfri, oldsat, oldsdate, oldedate, oldstime, oldetime, con, id);
           
      }
      catch (Exception exc) {

         dbError(req, out, exc, lottery);
         return;
      }

      //
      //  Blocker deleted - inform user
      //
      out.println(SystemUtils.HeadTitle("Delete Blocker Confirmation"));
      out.println("<BODY>");
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      out.println("<CENTER><BR>");
      out.println("<p>&nbsp;</p>");
      out.println("<BR><H3>Blocker Removed</H3><BR>");
      out.println("<BR>Blocker <b>" + oldname + "</b> has been removed.<br>");
      out.println("<BR>The Lesson Times associated with this blocker have also been updated.");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"lesson\" value=\"yes\">");
      out.println("<input type=\"hidden\" name=\"proid\" value=" +id+ ">");
      out.println("<input type=\"hidden\" name=\"jump\" value=" +jump+ ">");
      out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();

   } else {        // not a delete request

      //
      //  Make sure user specified the minimum parameters
      //
      if (lbname.equals( "" ) || lbname == null) {

         error = "You must specify a name for this Blocker.";
         invData(req, out, lottery, error);
         return;
      }

      //
      //  Check for quotes in name
      //
      boolean err = SystemUtils.scanQuote(lbname);           // check for single quote

      if (err == true) {

         error = "Apostrophes (single quotes) cannot be part of the Name.";
         invData(req, out, lottery, error);
         return;
      }

      //
      //  Check the recurrences
      //
      if (mon == 0 && tue == 0 && wed == 0 && thu == 0 && fri == 0 && sat == 0 && sun == 0) {  // if no recurr

         error = "You must select at least one Recurrence.";
         invData(req, out, lottery, error);
         return;
      }
        
      //
      //  Check if blocker already exists
      //
      if (!lbname.equalsIgnoreCase( oldname ) || addBlock == true) {      // if name changed OR this is an add

         try {

            pstmt = con.prepareStatement (
                    "SELECT proid FROM lessonblock5 WHERE proid = ? AND activity_id = ? AND lbname = ?");

            pstmt.clearParameters();        // clear the parms
            pstmt.setInt(1, id);
            pstmt.setInt(2, sess_activity_id);
            pstmt.setString(3, lbname);
            rs = pstmt.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               dupMem(req, out, lottery);
               pstmt.close();
               return;
            }
            pstmt.close();
         }
         catch (Exception ignored) {
         }
      }


      //
      //  adjust some values for the table
      //
      sdate = syear * 10000;       // create a date field from input values
      sdate = sdate + (smonth * 100);
      sdate = sdate + sday;             // date = yyyymmdd (for comparisons)

      edate = eyear * 10000;       // ditto
      edate = edate + (emonth * 100);
      edate = edate + eday;

      if (s_hr != 12) {                // _hr specified as 01 - 12 (_ampm = 00 or 12)

         s_hr = s_hr + s_ampm;         // convert to military time (12 is always Noon, or PM)
      }

      if (e_hr != 12) {                // ditto

         e_hr = e_hr + e_ampm;
      }

      stime = s_hr * 100;
      stime = stime + s_min;
      etime = e_hr * 100;
      etime = etime + e_min;

      //
      //  verify the date and time fields
      //
      if (sdate > edate) {

         error = "The Start Date cannot be later than the End Date.  Please correct the date range.";
         invData(req, out, lottery, error);
         return;
      }
      if (stime > etime) {

         error = "The Start Time cannot be later than the End Time.  Please correct the Time range.";
         invData(req, out, lottery, error);
         return;
      }

      //
      //  Add or Update the blocker
      //
      try {

         if (addBlock == true) {       // if new blocker

            //
            //  Add the lesson time blocker
            //
            pstmt = con.prepareStatement (
              "INSERT INTO lessonblock5 (proid, activity_id, lbname, sdate, stime, edate, etime, mon, tue, " +
              "wed, thu, fri, sat, sun, color, fragment) " +
              "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            pstmt.clearParameters();        // clear the parms
            pstmt.setInt(1, id);
            pstmt.setInt(2, sess_activity_id);
            pstmt.setString(3, lbname);
            pstmt.setLong(4, sdate);
            pstmt.setInt(5, stime);
            pstmt.setLong(6, edate);
            pstmt.setInt(7, etime);
            pstmt.setInt(8, mon);
            pstmt.setInt(9, tue);
            pstmt.setInt(10, wed);
            pstmt.setInt(11, thu);
            pstmt.setInt(12, fri);
            pstmt.setInt(13, sat);
            pstmt.setInt(14, sun);
            pstmt.setString(15, color);
            pstmt.setInt(16, fragment);
            pstmt.executeUpdate();          // execute the prepared stmt

            pstmt.close();   // close the stmt

         } else {       // update the blocker

            pstmt = con.prepareStatement (
              "UPDATE lessonblock5 SET lbname = ?, sdate = ?, stime = ?, edate = ?, etime = ?, " +
              "mon = ?, tue = ?, wed = ?, thu = ?, fri = ?, sat = ?, sun = ?, color = ?, fragment = ? " +
              "WHERE proid = ? AND activity_id = ? AND lbname = ?");

            pstmt.clearParameters();        // clear the parms
            pstmt.setString(1, lbname);
            pstmt.setLong(2, sdate);
            pstmt.setInt(3, stime);
            pstmt.setLong(4, edate);
            pstmt.setInt(5, etime);
            pstmt.setInt(6, mon);
            pstmt.setInt(7, tue);
            pstmt.setInt(8, wed);
            pstmt.setInt(9, thu);
            pstmt.setInt(10, fri);
            pstmt.setInt(11, sat);
            pstmt.setInt(12, sun);
            pstmt.setString(13, color);
            pstmt.setInt(14, fragment);

            pstmt.setInt(15, id);
            pstmt.setInt(16, sess_activity_id);
            pstmt.setString(17, oldname);

            pstmt.executeUpdate();     // execute the prepared stmt

            pstmt.close();
              
            //
            //  Remove the old blocker from the lesson book
            //
            removeBlocker(sess_activity_id, oldsun, oldmon, oldtue, oldwed, oldthu, oldfri, oldsat, oldsdate, oldedate, oldstime, oldetime, con, id);
         }

         //
         //  Add the new or updated blocker to the lesson book
         //
         addBlocker(sess_activity_id, sun, mon, tue, wed, thu, fri, sat, sdate, edate, stime, etime, con, id);

      }
      catch (Exception exc) {

         dbError(req, out, exc, lottery);
         return;
      }

      //
      // Database updated - inform user
      //
      out.println(SystemUtils.HeadTitle("Proshop Add Lesson Time"));
      out.println("<BODY>");
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      out.println("<CENTER><BR>");
      if (addBlock == true) {
         out.println("<BR><BR><H3>The Lesson Time Blocker Has Been Added</H3>");
         out.println("<BR><BR>Thank you, the lesson time blocker has been added for the specified pro.");
      } else {
         out.println("<BR><BR><H3>The Lesson Time Blocker Has Been Updated</H3>");
         out.println("<BR><BR>Thank you, the lesson time blocker has been updated for the specified pro.");
      }
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"lesson\" value=\"yes\">");
      out.println("<input type=\"hidden\" name=\"proid\" value=" +id+ ">");
      out.println("<input type=\"hidden\" name=\"jump\" value=" +jump+ ">");
      out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("<BR><BR>");
      out.println("<form method=\"get\" action=\"Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
   }
     
   //
   //  If we just changed or deleted a blocker, then we must reprocess all blockers to make sure
   //  that all appropriate lesson times are blocked.  This is in case this blocker overlapped
   //  other existing blockers.  Since we clear the 'block' flag when removing a blocker, we may
   //  need to reset it.
   //
   if (addBlock == false) {       // if we updated or removed a blocker

      doBlockers(con, id, sess_activity_id);        // reprocess all existing blockers for this pro
   }

 }


 // ********************************************************************
 //  Remove Blocker from lessonbook
 // ********************************************************************

 private void removeBlocker(int activity_id, int sun, int mon, int tue, int wed, int thu, int fri, int sat,
                       long sdate, long edate, int stime, int etime, Connection con, int id)
                       throws Exception {


   PreparedStatement pstmt = null;

   int [] days = new int [8];    // array to hold each recurrence day (+1 to start at 0)

   days[1] = sun;                // start with 1 to match calendar values
   days[2] = mon;
   days[3] = tue;
   days[4] = wed;
   days[5] = thu;
   days[6] = fri;
   days[7] = sat;

   //
   //  remove the blocker for each day of the week specified
   //
   for (int i=1; i<8; i++) {
     
      if (days[i] > 0) {
        
         try {
           
            pstmt = con.prepareStatement (
                 "UPDATE lessonbook5 SET block = 0 " +
                 "WHERE proid = ? AND activity_id = ? AND date >= ? AND date <= ? AND time >= ? AND time <= ? AND dayNum = ?");

            pstmt.clearParameters();               // clear the parms
            pstmt.setInt(1, id);
            pstmt.setInt(2, activity_id);
            pstmt.setLong(3, sdate);
            pstmt.setLong(4, edate);
            pstmt.setInt(5, stime);
            pstmt.setInt(6, etime);
            pstmt.setInt(7, i);
            pstmt.executeUpdate();         // execute the prepared pstmt

            pstmt.close();
         }
         catch (Exception e1) {

            throw new Exception("Proshop_lesson.removeBlocker error: " + e1.getMessage());
         }

      }
   }
 }


 // ********************************************************************
 //  Add Blocker in lessonbook
 // ********************************************************************

 private void addBlocker(int activity_id, int sun, int mon, int tue, int wed, int thu, int fri, int sat,
                       long sdate, long edate, int stime, int etime, Connection con, int id)
                       throws Exception {


   PreparedStatement pstmt = null;

   int year = 0;
   int month = 0;
   int day = 0;

   long curdate = 0;

   int [] days = new int [8];    // array to hold each recurrence day (+1 to start at 0)

   days[1] = sun;
   days[2] = mon;
   days[3] = tue;
   days[4] = wed;
   days[5] = thu;
   days[6] = fri;
   days[7] = sat;

   Calendar cal = new GregorianCalendar();       // get todays date

   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH) + 1;
   day = cal.get(Calendar.DAY_OF_MONTH);

   curdate = (year * 10000) + (month * 100) + day;    // create a date field of yyyymmdd

   //  If sdate is prior to today's date, adjust the sdate to equal today's date so we aren't blocking lesson times that have already passed
   if (sdate < curdate) {
       sdate = curdate;
   }

   //
   //  Add the blocker for each day of the week specified
   //
   for (int i=1; i<8; i++) {

      if (days[i] > 0) {

         try {

            pstmt = con.prepareStatement (
                 "UPDATE lessonbook5 SET block = 1 " +
                 "WHERE proid = ? AND activity_id = ? AND date >= ? AND date <= ? AND time >= ? AND time <= ? AND dayNum = ?");

            pstmt.clearParameters();               // clear the parms
            pstmt.setInt(1, id);
            pstmt.setInt(2, activity_id);
            pstmt.setLong(3, sdate);
            pstmt.setLong(4, edate);
            pstmt.setInt(5, stime);
            pstmt.setInt(6, etime);
            pstmt.setInt(7, i);
            pstmt.executeUpdate();         // execute the prepared pstmt

            pstmt.close();

         }
         catch (Exception e1) {

            throw new Exception("Proshop_lesson.addBlocker error: " + e1.getMessage());
         }
            
      }
   }
 }


 // ********************************************************************
 //  Do Blockers - reprocess all blockers for pro
 // ********************************************************************

 private void doBlockers(Connection con, int id, int activity_id) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   String error = "";

   int mon = 0;
   int tue = 0;
   int wed = 0;
   int thu = 0;                                                             
   int fri = 0;
   int sat = 0;
   int sun = 0;
   int stime = 0;
   int etime = 0;

   long sdate = 0;
   long edate = 0;


   //
   //*****************************************************
   //  Get all the blockers currently in the block table
   //*****************************************************
   //
   try {

      pstmt = con.prepareStatement (
              "SELECT * FROM lessonblock5 WHERE proid = ? AND activity_id = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1,id);
      pstmt.setInt(2,activity_id);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      while (rs.next()) {

         sdate = rs.getLong("sdate");
         stime = rs.getInt("stime");
         edate = rs.getLong("edate");
         etime = rs.getInt("etime");
         mon = rs.getInt("mon");
         tue = rs.getInt("tue");
         wed = rs.getInt("wed");
         thu = rs.getInt("thu");
         fri = rs.getInt("fri");
         sat = rs.getInt("sat");
         sun = rs.getInt("sun");

         //
         //  go process the single blocker
         //
         addBlocker(activity_id, sun, mon, tue, wed, thu, fri, sat, sdate, edate, stime, etime, con, id);
         
      }   // end of while

      pstmt.close();
        
   }
   catch (Exception e20) {
      String errorMsg20 = "Error in Proshop_lesson.doBlockers: ";
      errorMsg20 = errorMsg20 + e20.getMessage();                                 // build error msg

      SystemUtils.logError(errorMsg20);                                       // log it
   }

 }


 // ********************************************************************
 //  Process the 'My Blockers' request from Control Panel
 // ********************************************************************

 private void myBlockers(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException {

   PreparedStatement pstmt = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String calDate = "";
   String dates = "";
   String lbname = "";
   String color = "";
   String error = "";
   String s_ampm = "AM";
   String e_ampm = "AM";

   int id = 0;
   int mon = 0;
   int tue = 0;
   int wed = 0;
   int thu = 0;
   int fri = 0;
   int sat = 0;
   int sun = 0;
   int smonth = 0;                // init integer date/time values
   int sday = 0;
   int syear = 0;
   int emonth = 0;
   int eday = 0;
   int eyear = 0;
   int s_hr = 0;
   int s_min = 0;
   int e_hr = 0;
   int e_min = 0;
   int stime = 0;
   int etime = 0;
   int jump = 0;

   long sdate = 0;
   long edate = 0;
   long mm = 0;
   long dd = 0;
   long yy = 0;

   boolean b = false;
   boolean hit = false;

   try {
      id = Integer.parseInt(proid);
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   if (req.getParameter("calDate") != null) {

      calDate = req.getParameter("calDate");
   }
     
   if (req.getParameter("date") != null) {      

      dates = req.getParameter("date");             // date of the lesson book
   }
     
  
   //
   //  Get this year
   //
   Calendar cal = new GregorianCalendar();       // get todays date
   int thisYear = cal.get(Calendar.YEAR);        // get the year
   int year = 0;

   //
   //  Build the HTML page to display the existing Blockers
   //
   out.println(SystemUtils.HeadTitle("Proshop My Blockers Page"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");

      out.println("<table cellpadding=\"5\" bgcolor=\"#336633\" border=\"0\" align=\"center\">");
      out.println("<tr><td align=\"center\">");
      out.println("<font color=\"#FFFFFF\" size=\"3\">");
      out.println("<b>Edit Blockers</b><br>");
      out.println("</font>");
      out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("<br>To update a Blocker, click on the Update button within that blocker.");
      out.println("<br></td></tr></table>");
      out.println("<br>");

      out.println("<table border=\"2\" cellpadding=\"5\" bgcolor=\"#F5F5DC\">");
      out.println("<tr bgcolor=\"#8B8970\">");
      out.println("<td colspan=\"9\" align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<p align=\"center\"><b>Active Blockers</b></p>");
      out.println("</font></td></tr>");
      out.println("<tr bgcolor=\"#8B8970\"><td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Blocker Name</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Start Date</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>End Date</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Start Time</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>End Time</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Recurrence</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Color</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p>&nbsp;</p>");      // empty for select buttons
      out.println("</font></td></tr>");

   //
   //  Get the blocker records
   //
   try {

      pstmt = con.prepareStatement (
              "SELECT * FROM lessonblock5 WHERE proid = ? AND activity_id = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1,id);
      pstmt.setInt(2,sess_activity_id);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      while (rs.next()) {

         b = true;                     // indicate blockers exist

         lbname = rs.getString("lbname");
         sdate = rs.getLong("sdate");
         stime = rs.getInt("stime");
         edate = rs.getLong("edate");
         etime = rs.getInt("etime");
         mon = rs.getInt("mon");
         tue = rs.getInt("tue");
         wed = rs.getInt("wed");
         thu = rs.getInt("thu");
         fri = rs.getInt("fri");
         sat = rs.getInt("sat");
         sun = rs.getInt("sun");
         color = rs.getString("color");

         //
         //  adjust some values for the table
         //
         yy = sdate / 10000;                             // get year
         mm = (sdate - (yy * 10000)) / 100;              // get month
         dd = (sdate - (yy * 10000)) - (mm * 100);       // get day

         smonth = (int)mm;
         sday = (int)dd;
         syear = (int)yy;

         yy = edate / 10000;                             // get year
         mm = (edate - (yy * 10000)) / 100;              // get month
         dd = (edate - (yy * 10000)) - (mm * 100);       // get day

         emonth = (int)mm;
         eday = (int)dd;
         eyear = (int)yy;

         s_ampm = "AM";         // defaults
         e_ampm = "AM";

         s_hr = stime / 100;
         s_min = stime - (s_hr * 100);

         e_hr = etime / 100;
         e_min = etime - (e_hr * 100);

         if (s_hr == 12) {

            s_ampm = "PM";

         } else {

            if (s_hr > 12) {

               s_hr = s_hr - 12;          // adjust
               s_ampm = "PM";
            }
         }

         if (e_hr == 12) {

            e_ampm = "PM";

         } else {

            if (e_hr > 12) {

               e_hr = e_hr - 12;          // adjust
               e_ampm = "PM";
            }
         }

         if (!color.equalsIgnoreCase( "default" )) {
            out.println("<tr bgcolor=\"" +color+ "\">");
         } else {
            out.println("<tr>");
         }
         out.println("<form action=\"Proshop_lesson\" method=\"post\" target=\"bot\" name=\"f\">");
         out.println("<input type=\"hidden\" name=\"proid\" value=\"" +proid+ "\">");
         out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
//         out.println("<input type=\"hidden\" name=\"date\" value=\"" +dates+ "\">");        // shouldn't need this
         out.println("<input type=\"hidden\" name=\"block\" value=\"yes\">");
         out.println("<input type=\"hidden\" name=\"lbname\" value=\"" +lbname+ "\">");

            out.println("<td align=\"center\">");
              out.println("<font size=\"2\">");
              out.println(lbname);
              out.println("</font>");
            out.println("</td>");

            out.println("<td align=\"center\">");
              out.println("<font size=\"2\">");
              out.println(smonth+ "/" +sday+ "/" +syear);
              out.println("</font>");
            out.println("</td>");

            out.println("<td align=\"center\">");
              out.println("<font size=\"2\">");
              out.println(emonth+ "/" +eday+ "/" +eyear);
              out.println("</font>");
            out.println("</td>");

            out.println("<td align=\"center\">");
              out.println("<font size=\"2\">");
              if (s_min < 10) {
                 out.println(s_hr+ ":0" +s_min+ " " +s_ampm);
              } else {
                 out.println(s_hr+ ":" +s_min+ " " +s_ampm);
              }
              out.println("</font>");
            out.println("</td>");

            out.println("<td align=\"center\">");
              out.println("<font size=\"2\">");
              if (e_min < 10) {
                 out.println(e_hr+ ":0" +e_min+ " " +e_ampm);
              } else {
                 out.println(e_hr+ ":" +e_min+ " " +e_ampm);
              }
              out.println("</font>");
            out.println("</td>");

            hit = false;         // init

            out.println("<td>");
              out.println("<font size=\"2\">");
              if (mon == 1) {
                 out.println("Every Monday");
                 hit = true;
              }
              if (tue == 1) {
                 if (hit == true) {
                    out.println("<br>Every Tuesday");
                 } else {
                    out.println("Every Tuesday");
                    hit = true;
                 }
              }
              if (wed == 1) {
                 if (hit == true) {
                    out.println("<br>Every Wednesday");
                 } else {
                    out.println("Every Wednesday");
                    hit = true;
                 }
              }
              if (thu == 1) {
                 if (hit == true) {
                    out.println("<br>Every Thursday");
                 } else {
                    out.println("Every Thursday");
                    hit = true;
                 }
              }
              if (fri == 1) {
                 if (hit == true) {
                    out.println("<br>Every Friday");
                 } else {
                    out.println("Every Friday");
                    hit = true;
                 }
              }
              if (sat == 1) {
                 if (hit == true) {
                    out.println("<br>Every Saturday");
                 } else {
                    out.println("Every Saturday");
                    hit = true;
                 }
              }
              if (sun == 1) {
                 if (hit == true) {
                    out.println("<br>Every Sunday");
                 } else {
                    out.println("Every Sunday");
                    hit = true;
                 }
              }
              out.println("</font>");
            out.println("</td>");

             if (!color.equals( "Default" )) {
               out.println("<td align=\"center\" bgcolor=\"" +color+ "\">");
             } else {
               out.println("<td align=\"center\">");
             }
              out.println("<font size=\"2\">");
              out.println(color);
              out.println("</font>");
            out.println("</td>");

            out.println("<td align=\"center\">");
              out.println("<font size=\"2\">");
                out.println("<p align=\"center\">");
                out.println("<input type=\"submit\" name=\"editlb\" value=\"Update\">");    // to update record
                out.println("</p>");
              out.println("</font>");
            out.println("</td>");

         out.println("</form>");
         out.println("</tr>");

      }
      pstmt.close();

      if (!b) {

         out.println("</font><font size=\"2\"><p>No Blockers Currently Exist for this Lesson Pro</p>");
      }
   }
   catch (Exception exc) {

      dbError(req, out, exc, lottery);
      return;
   }

   //
   //  End of HTML page
   //
   out.println("</table></font>");                   // end of lesson times table
   out.println("</td></tr></table>");                // end of main page table
   out.println("<font size=\"2\">");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"lesson\" value=\"yes\">");
      out.println("<input type=\"hidden\" name=\"proid\" value=" +id+ ">");
      out.println("<input type=\"hidden\" name=\"jump\" value=" +jump+ ">");
      out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("<BR><BR>");
   out.println("<form method=\"get\" action=\"Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</center></font></body></html>");
   out.close();

 }


 // ********************************************************************
 //  Process the 'Bio' request from the Main menu
 // ********************************************************************

 private void editBio(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException {


   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   int id = 0;

   try {
      id = Integer.parseInt(proid);          // convert the pro id
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   //
   //  Save the proid in the session for ae2.jsp
   //
   session.setAttribute("proid", proid);         // save proid

   //
   //  Build the HTML page to prompt for next step
   //
   out.println(SystemUtils.HeadTitle2("Proshop - Edit Bio Menu"));
   out.println("<script type=\"text/javascript\" src=\"/" + rev + "/assets/jquery/fancybox/jquery.fancybox-1.3.4.pack.js\"></script>");
   out.println("<script type=\"text/javascript\" src=\"/" + rev + "/assets/jquery/fancybox/jquery.easing-1.3.pack.js\"></script>");
   out.println("<link rel=\"stylesheet\" href=\"/" + rev + "/assets/jquery/fancybox/jquery.fancybox-1.3.4.css\" type=\"text/css\" />");
   
   // Simulate TopUp's modal behavior
   out.println("<script>");
   out.println("$(document).ready(function() {");
   out.println(" $(\"[class^=tu_iframe_]\").click(function(event){");

   out.println(" var tu_obj = $(this);");
   out.println(" var tu_class_arr = tu_obj.attr(\"class\").split(\" \");");
   out.println(" var tu_height = 768;");
   out.println(" var tu_width = 1024;");

   out.println(" tu_obj.fancybox({");
   out.println("     'transitionIn':'elastic',");
   out.println("     'transitionOut':'elastic',");
   out.println("     'height':tu_height,");
   out.println("     'width':tu_width,");
   out.println("     'type':'iframe',");
   out.println("     'centerOnScroll':true,");
   out.println("     'changeSpeed':50,");
   out.println("     'margin':13,");
   out.println("     'padding':0");
   out.println(" });");

   out.println(" event.preventDefault();");
   out.println(" // For some reason, the first click fails. If this is the first try, send another click. ");
   out.println(" if(tu_obj.data('tu_click_track')!=true){");
   out.println("     tu_obj.data('tu_click_track',true)");
   out.println("     tu_obj.click();");
   out.println(" }");

   out.println("});");
    
   out.println("});");

   out.println("</script>");
   out.println("</head>");
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"blue\" alink=\"darkBlue\" vlink=\"blue\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

   out.println("<center><br>");
   out.println("<table border=\"1\" align=\"center\" cellpadding=\"5\" bgcolor=\"F5F5DC\">");

      out.println("<tr><td align=\"center\" valign=\"top\" bgcolor=\"336633\">");
      out.println("<font color=\"#FFFFFF\" size=\"3\">");
      out.println("<b>Lesson Pro Bio Management Menu</b><br>");
      out.println("</font>");
      out.println("</td>");
      out.println("</tr>");

      out.println("<tr>");
      out.println("<td align=\"left\"><font size=\"2\"><br>");
      out.println("<b>Instructions:</b> To create or change a bio page, do one of the following.");
      out.println("<br><br>");
      out.println("&nbsp;&nbsp;&nbsp;1. Use our online editor (allows you to insert your own images).");
      out.println("<br>");
      out.println("&nbsp;&nbsp;&nbsp;2. Create the page on a word processor using your computer, save it as");
      out.println("<br>");
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;an HTML document (<b>MUST be named " +club+ "_bio" +id+ ".htm</b>), then upload it.");
      out.println("<br>");
      out.println("&nbsp;&nbsp;&nbsp;3. Create the page on a word processor using your computer, save it as");
      out.println("<br>");
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;an HTML document (as defined above), upload it and use our editor to add images, etc.");
      out.println("<br><br>");
      out.println("<b>Note:</b>&nbsp;&nbsp;Each Lesson Pro is assigned an 'id'.  Your id is " +id+ ".");
      out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The name of the file must end with this id as described above.");
      out.println("</font></td></tr>");

      out.println("<tr>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<p>");
      out.println("<p><a href=\"Proshop_announce?tinymce&bio=yes&id=" + id + "\" target=\"bot\">Use Online Editor</a></p>");
      out.println("</p>");
      out.println("</font></td>");
      out.println("</tr>");

      out.println("<tr>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<p>");
      out.println("<a href=\"Proshop_upload?proid=" +id+ "\" target=\"bot\">Upload From Your Computer</a>");
      out.println("</p>");
      out.println("</font></td>");
      out.println("</tr>");

      out.println("<tr>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<p>");
    //out.println("<a href=\"/" +rev+ "/announce/" +club+ "/" +club+ "_bio" +id+ ".htm\" class=\"tu_iframe_1024x768 otherclass\">View the Current Bio</a>");  // target=\"_blank\"
      out.println("<a href=\"Common_lesson?bioview&club=" +club+ "&proid=" +id+ "\" class=\"tu_iframe_1024x768\">View the Current Bio</a>");  // target=\"_blank\"
      out.println("</p>");
      out.println("</font></td>");
      out.println("</tr>");
      
      out.println("<tr>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<p>");
      out.println("<a href=\"Proshop_announce?backups&probio=" + id + "\" target=\"bot\">Manage Backup Copies</a>");
      out.println("</p>");
      out.println("</font></td>");
      out.println("</tr>");
         
   out.println("</table>");
      out.println("<BR>");
      out.println("<form method=\"post\" action=\"Proshop_lesson\">");
      out.println("<input type=\"hidden\" name=\"bio\" value=\"yes\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"background:#8B8970; width:75px\">");
      out.println("</form></font>");
   out.println("</center></body></html>");
   out.close();
 }


 private void createLessonSet(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session, Connection con, int proid)
              throws ServletException, IOException {

   //
   // Define parms - set defaults
   //
   PreparedStatement pstmt = null;
   ResultSet rs = null;


   String club = (String)session.getAttribute("club");               // get name of club
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int sess_activity_id = (Integer)session.getAttribute("activity_id");
   int lottery = Integer.parseInt(templott);

   String name = "";
   String description = "";

   int set_id = 0;

   boolean existingSet = false;

   if (req.getParameter("set_id") != null) {

       try {
           set_id = Integer.parseInt(req.getParameter("set_id"));
       } catch (Exception exc) {
           set_id = 0;
           Utilities.logError("Proshop_lesson.createLessonSet - " + club + " - Error getting set_id from request object - Err: " + exc.toString());
       }
   }

   if (set_id > 0) {
       existingSet = true;
   }

   if (req.getParameter("create") != null) {

       name = req.getParameter("name").trim();
       description = req.getParameter("description").trim();

       int count = 0;

       // Check to see if name
       if (!SystemUtils.scanName(name)) {

           try {

               if (!existingSet) {

                   pstmt = con.prepareStatement("INSERT INTO lessonsets (proid, activity_id, name, description) VALUES (?,?,?,?)");
                   pstmt.clearParameters();
                   pstmt.setInt(1, proid);
                   pstmt.setInt(2, sess_activity_id);
                   pstmt.setString(3, name);
                   pstmt.setString(4, description);

                   count = pstmt.executeUpdate();

               } else {

                   pstmt = con.prepareStatement("UPDATE lessonsets SET name = ?, description = ? WHERE set_id = ?");
                   pstmt.clearParameters();
                   pstmt.setString(1, name);
                   pstmt.setString(2, description);

                   pstmt.setInt(3, set_id);

                   count = pstmt.executeUpdate();
               }

           } catch (Exception exc) {

               Utilities.logError("Proshop_lesson.createLessonSet - " + club + " - Error creating lesson set - Err: " + exc.toString());
               count = -1;

           } finally {

               try { pstmt.close(); }
               catch (Exception ignore) { }
           }

           if (count > 0) {

               out.println(SystemUtils.HeadTitle("Proshop - " + (existingSet ? "Edit" : "Create") + " Lesson Set"));

               out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Proshop_lesson?sets&proid=" + proid + "\">");

               out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
               out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
               out.println("<center><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");

               out.println("<br><br>The lesson set was " + (existingSet ? "edited" : "created") + " successfully!");
               out.println("<br><br><button onclick=\"location.href='Proshop_lesson?sets&proid=" + proid + "'\">Continue</button>");

               out.println("</center></body></html>");
           }

       } else {

           out.println(SystemUtils.HeadTitle("Proshop - " + (existingSet ? "Edit" : "Create") + " Lesson Set"));

           out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
           out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
           out.println("<center><img src=\"/" +rev+ "/images/foretees.gif\"><br>");
           out.println("<hr width=\"40%\">");
           out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");

           out.println("<br><br><h3>Invalid Characters Found</h3>");
           out.println("<br>Special characters are not allowed in Lesson Set names.  Please remove any special characters from the name and try again.");
           out.println("<br><br><form method=\"get\" action=\"javascript:history.back(1)\">");
           out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");

           out.println("</center></body></html>");
       }

   } else {
       
       //
       //  Build the html page to request the Group Lesson
       //
       out.println(SystemUtils.HeadTitle("Proshop - " + (existingSet ? "Edit" : "Create") + " Lesson Set"));
       out.println("<style>");
       out.println("table.setsTable {");
       out.println("  margin: 0;");
       out.println("  background: #F5F5DC;");
       out.println("  font: normal 14px \"Trebuchet MS\", Verdana, Arial, Helvetica, sans-serif;");
       out.println("  align: center;");
       out.println("  width: 50%;");
       out.println("}");
       out.println("table.setsTable td {");
       out.println("  font: normal 14px;");
       out.println("}");
       out.println("textarea {");
       out.println("  resize:none;");
       out.println("}");
       out.println("</style>");
       out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
       SystemUtils.getProshopSubMenu(req, out, lottery);            // required to allow submenus on this page
       out.println("<font face=\"Verdana, Arial, Helvetica, Sans-serif\">");

       out.println("<table border=\"0\" bgcolor=\"#FFFFFF\" align=\"center\" width=\"100%\">");        // Main structure table for centering page elements

       out.println("<tr><td align=\"center\"><br>");
       out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\">");
       out.println("<tr><td align=\"center\">");
       out.println("<font color=\"#FFFFFF\" size=\"2\">");
          out.println("<b>Create New Lesson Set</b><br>");
          out.println("<br>" + (existingSet ? "Edit a" : "Create a new") + " Lesson Set by entering details below.<br>" +
                  "A name is required for each lesson set.  A description may also be entered, however this is optional.<br>" +
                  "After a lesson set is created, you will be able to associate lessons with it by selecting the set while booking a lesson.");
       out.println("</font></td></tr></table>");
       out.println("</td></tr>");

       out.println("<tr><td align=\"center\">");
       out.println("<table border=\"0\" bgcolor=\"#FFFFFF\" align=\"center\"><tr>");
       out.println("<form action=\"Proshop_lesson\" method=\"post\">");
       out.println("<td align=\"center\">");
       out.println("<input type=\"hidden\" name=\"proid\" value=\"" + proid + "\">");
       out.println("<input type=\"submit\" name=\"sets\" value=\"Return to Set List\">");
       out.println("</td>");
       out.println("</form>");
       out.println("<form action=\"Proshop_announce\" method=\"get\">");
       out.println("<td align=\"center\">");
       out.println("<input type=\"submit\" value=\"Home\">");
       out.println("</td>");
       out.println("</form>");
       out.println("</tr></table>");
       out.println("</td></tr>");

       // If we're here to edit an existing set, get the name and description from the database.
       if (existingSet) {

           try {

               pstmt = con.prepareStatement("SELECT name, description FROM lessonsets WHERE set_id = ?");
               pstmt.clearParameters();
               pstmt.setInt(1, set_id);

               rs = pstmt.executeQuery();

               if (rs.next()) {
                   name = rs.getString("name").trim();
                   description = rs.getString("description").trim();
               }

           } catch (Exception exc) {
               name = "";
               description = "";
               Utilities.logError("Proshop_lesson.createLessonSet - " + club + " - Error looking up existing set name and description - Err: " + exc.toString());
           } finally {
               
               try { rs.close(); }
               catch (Exception ignore) { }

               try { pstmt.close(); }
               catch (Exception ignore) { }
           }
       }

       out.println("<tr><td align=\"center\">");
       out.println("<table class=\"setsTable\" border=\"1\" cellpadding=\"5\">");   // Outer table
       out.println("<tr>");
       out.println("<form action=\"Proshop_lesson\" method=\"post\">");
       out.println("<input type=\"hidden\" name=\"create\" value=\"1\">");
       if (set_id != 0) out.println("<input type=\"hidden\" name=\"set_id\" value=\"" + set_id + "\">");
       out.println("<input type=\"hidden\" name=\"proid\" value=\"" + proid + "\">");
       out.println("<td align=\"center\">");
       out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" align=\"center\" cellpadding=\"5\">");    // Inner table
       out.println("<tr>");
       out.println("<td align=\"right\">Name:</td>");
       out.println("<td align=\"left\"><input type=\"text\" name=\"name\" size=\"40\" length=\"40\" value=\"" + (!name.equals("") ? name : "") + "\">&nbsp;(Required)</td>");
       out.println("</tr>");
       out.println("<tr>");
       out.println("<td align=\"right\">Description:</td>");
       out.println("<td align=\"left\"><textarea name=\"description\" cols=\"60\" rows=\"5\">" + (!description.equals("") ? description : "") + "</textarea></td>");
       out.println("</tr>");
       out.println("<tr><td align=\"center\" colspan=\"2\"><input type=\"submit\" name=\"createSet\" value=\"" + (existingSet ? "Update" : "Create") + " Set\"></td></tr>");
       out.println("</td></tr>");
       out.println("</table>");     // Close inner table
       out.println("</td>");
       out.println("</form>");
       out.println("</tr>");
       out.println("</table>");     // Close outer table
       out.println("</td></tr>");

       out.println("<tr><td align=\"center\">");
       out.println("<table border=\"0\" bgcolor=\"#FFFFFF\" align=\"center\"><tr>");
       out.println("<form action=\"Proshop_lesson\" method=\"post\">");
       out.println("<td align=\"center\">");
       out.println("<input type=\"hidden\" name=\"proid\" value=\"" + proid + "\">");
       out.println("<input type=\"submit\" name=\"sets\" value=\"Return to Set List\">");
       out.println("</td>");
       out.println("</form>");
       out.println("<form action=\"Proshop_announce\" method=\"get\">");
       out.println("<td align=\"center\">");
       out.println("<input type=\"submit\" value=\"Home\">");
       out.println("</td>");
       out.println("</form>");
       out.println("</tr></table>");
       out.println("</td></tr>");

       out.println("</table>");
   }
 }
 
 private void deleteLessonSet(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session, Connection con, int proid)
              throws ServletException, IOException {

   //
   // Define parms - set defaults
   //
   PreparedStatement pstmt = null;
   PreparedStatement pstmt2 = null;
   PreparedStatement pstmt3 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;


   String club = (String)session.getAttribute("club");               // get name of club
   String user = (String)session.getAttribute("user");               // get name of user
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int sess_activity_id = (Integer)session.getAttribute("activity_id");
   int lottery = Integer.parseInt(templott);

   String name = "";
   String description = "";

   int set_id = 0;

   boolean promptConfirm = false;
   boolean confirmDelete = false;

   try {
       set_id = Integer.parseInt(req.getParameter("set_id"));
   } catch (Exception exc) {
       set_id = 0;
       Utilities.logError("Proshop_lesson.deleteLessonSet - " + club + " - Error getting set_id from request object - Err: " + exc.toString());
   }

   name = req.getParameter("name");

   if (req.getParameter("promptConfirm") != null) promptConfirm = true;
   if (req.getParameter("confirmDelete") != null) confirmDelete = true;

   if (confirmDelete) {

       // First, if selected, need to cancel all remaining upcoming lessons that were part of this lesson set.
       if (req.getParameter("cancelLessons") != null) {

           String dayName = "";
           String ltimename = "";
           String proName = "";
           
           int count = 0;
           int recid = 0;
           int recid2 = 0;
           int ltlength = 0;
           int time = 0;
           int etime = 0;
           
           long date = 0;

           try {

               long cur_date = Utilities.getDate(con);

               pstmt = con.prepareStatement(
                       "SELECT recid, date, DATE_FORMAT(date, '%W') as dayName, time, length, ltname, CONCAT(fname, ' ', lname) as proName " + 
                       "FROM lessonbook5 lb " + 
                       "LEFT OUTER JOIN lessonpro5 lp ON lp.id = lb.proid " +
                       "WHERE set_id = ? AND date >= ? ORDER BY date");
               pstmt.clearParameters();
               pstmt.setInt(1, set_id);
               pstmt.setLong(2, cur_date);

               rs = pstmt.executeQuery();

               while (rs.next()) {

                   recid = rs.getInt("recid");
                   ltlength = rs.getInt("length");
                   time = rs.getInt("time");
                   date = rs.getLong("date");
                   dayName = rs.getString("dayName");
                   ltimename = rs.getString("ltname");
                   proName = rs.getString("proName");

                   try {

                       pstmt2 = con.prepareStatement("" +
                               "UPDATE lessonbook5 " +
                               "SET memname = '', memid = '', num = 0, length = 0, billed = 0, in_use = 0, ltype = '', phone1 = '', phone2 = '', notes = ''  " +
                               "WHERE recid = ?");
                       pstmt2.clearParameters();
                       pstmt2.setInt(1, recid);

                       count = pstmt2.executeUpdate();

                   } catch (Exception exc) {
                       Utilities.logError("Proshop_lesson.deleteLessonSet - " + club + " - Error cancelling remaining lesson recid = " + recid + " - Err: " + exc.toString());
                   } finally {
                       try { pstmt2.close(); }
                       catch (Exception ignore) { }
                   }

                   etime = getEndTime(time, ltlength);
                   
                   //
                   //  Get the lesson times that follow this time and within the lesson length
                   //
                   try {

                      pstmt2 = con.prepareStatement (
                              "SELECT * FROM lessonbook5 " +
                              "WHERE proid = ? AND activity_id = ? AND date = ? AND time > ? AND time < ?");

                      pstmt2.clearParameters();        // clear the parms
                      pstmt2.setInt(1, proid);
                      pstmt2.setInt(2, sess_activity_id);
                      pstmt2.setLong(3, date);
                      pstmt2.setInt(4, time);
                      pstmt2.setInt(5, etime);
                      rs2 = pstmt2.executeQuery();      // execute the prepared pstmt

                      loop1:
                      while (rs2.next()) {

                          try {
                              
                             recid2 = rs2.getInt("recid");

                             //
                             //  set this time as a subsequent time for the primary time being processed
                             //
                             pstmt3 = con.prepareStatement (
                                "UPDATE lessonbook5 " +
                                "SET memname = '', memid = '', num = 0, length = 0, billed = 0, in_use = 0, ltype = '', phone1 = '', phone2 = '', notes = '' " +
                                "WHERE recid = ?");

                             pstmt3.clearParameters();        // clear the parms
                             pstmt3.setInt(1, recid2);

                             pstmt3.executeUpdate();      // execute the prepared stmt

                          } catch (Exception exc) {
                              Utilities.logError("Proshop_lesson.deleteLessonSet - " + club + " - Error cancelling subsequent lesson recid2 = " + recid2 + " - Err: " + exc.toString());
                          } finally {
                              try { pstmt3.close(); }
                              catch (Exception ignore) { }
                          }
                      }

                   } catch (Exception exc) {
                       Utilities.logError("Proshop_lesson.deleteLessonSet - " + club + " - Error looking up subsequent lessons for recid = " + recid + " (followup time) - Err: " + exc.toString());
                   } finally {
                       
                       try { rs2.close(); }
                       catch (Exception ignore) { }
                       
                       try { pstmt2.close(); }
                       catch (Exception ignore) { }
                   }

                   // If not Golf, remove recid from any activity time slots this lesson was occupying.
                   if (sess_activity_id != 0) {

                      try {
                          pstmt2 = con.prepareStatement("UPDATE activity_sheets SET lesson_id = 0 WHERE lesson_id = ?");
                          pstmt2.clearParameters();
                          pstmt2.setInt(1, recid);

                          pstmt2.executeUpdate();

                      } catch (Exception exc) {
                          Utilities.logError("Proshop_lesson.deleteLessonSet - " + club + " - Error removing lesson recid = " + recid + " from time sheets - Err: " + exc.toString());
                      } finally {
                          try { pstmt2.close(); }
                          catch (Exception ignore) { }
                      }
                   }
                   
                   // Insert a history entry for the cancelation
                   SystemUtils.updateHist(date, dayName, time, 99, ltimename, proName, "", "", "", "",
                           user, "Proshop User", 2, con);
               }

           } catch (Exception exc) {
               Utilities.logError("Proshop_lesson.deleteLessonSet - " + club + " - Error looking up remaining lessons for cancelling - Err: " + exc.toString());
           } finally {

               try { rs.close(); }
               catch (Exception ignore) { }

               try { pstmt.close(); }
               catch (Exception ignore) { }
           }
       }

       
       // First remove this set_id from any lesson bookings.
       try {

           pstmt = con.prepareStatement("UPDATE lessonbook5 SET set_id = 0 WHERE set_id = ?");
           pstmt.clearParameters();
           pstmt.setInt(1, set_id);
           
           pstmt.executeUpdate();

       } catch (Exception exc) {

           Utilities.logError("Proshop_lesson.deleteLessonSet - " + club + " - Error removing set_id from lessons - Err: " + exc.toString());

       } finally {

           try { pstmt.close(); }
           catch (Exception ignore) { }
       }
       
       int count = 0;
       
       // Delete the set itself
       try {

           pstmt = con.prepareStatement("DELETE FROM lessonsets WHERE set_id = ?");
           pstmt.clearParameters();
           pstmt.setInt(1, set_id);
           
           count = pstmt.executeUpdate();

       } catch (Exception exc) {

           Utilities.logError("Proshop_lesson.deleteLessonSet - " + club + " - Error deleting lesson set - Err: " + exc.toString());
           count = -1;

       } finally {

           try { pstmt.close(); }
           catch (Exception ignore) { }
       }

       if (count > 0) {

           out.println(SystemUtils.HeadTitle("Proshop - Delete Lesson Set"));

           out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Proshop_lesson?sets&proid=" + proid + "\">");

           out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
           out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
           out.println("<CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
           out.println("<hr width=\"40%\">");
           out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");

           out.println("<br><br>The lesson set was deleted successfully");
           out.println("<br><br><button onclick=\"location.href='Proshop_lesson?sets&proid=" + proid + "'\">Continue</button>");

           out.println("</body></html>");
       }

   } else {     // First time through. If outstanding upcoming lessons found, prompt user to see if they would like to cancel them.  If none found, prompt for confirmation on deleting the lesson set.

       int lesson_count = 0;

       if (!promptConfirm) {

           // See if this set has any remaining upcoming lessons
           try {

               long cur_date = Utilities.getDate(con);

               pstmt = con.prepareStatement("SELECT count(*) FROM lessonbook5 WHERE set_id = ? AND date >= ?");
               pstmt.clearParameters();
               pstmt.setInt(1, set_id);
               pstmt.setLong(2, cur_date);

               rs = pstmt.executeQuery();

               if (rs.next()) {
                   lesson_count = rs.getInt(1);
               }

           } catch (Exception exc) {
               Utilities.logError("Proshop_lesson.deleteLessonSet - " + club + " - Error getting lesson count - Err: " + exc.toString());
           } finally {

               try { rs.close(); }
               catch (Exception ignore) { }

               try { pstmt.close(); }
               catch (Exception ignore) { }
           }

           // If no remaining lessons found, we can skip straight to the final confirmation prompt.
           if (lesson_count == 0) {
               promptConfirm = true;
           }
       }

       //
       //  Build the html page prompt the user for confirmation
       //
       if (!promptConfirm) {    // If remaining upcoming lessons were found, prompt user to see if they would like to cancel these lessons or just remove them from the lesson set

           out.println(SystemUtils.HeadTitle("Proshop - Delete Lesson Set"));
           out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
           SystemUtils.getProshopSubMenu(req, out, lottery);            // required to allow submenus on this page
           out.println("<font face=\"Verdana, Arial, Helvetica, Sans-serif\">");

           out.println("<table border=\"0\" bgcolor=\"#FFFFFF\" align=\"center\" width=\"100%\">");        // Main structure table for centering page elements

           out.println("<tr><td align=\"center\">");

           out.println("<br><br><h3>Delete Lesson Set - " + name + "</h3><br><br>");

           out.println("<form action=\"Proshop_lesson\" method=\"post\">");
           out.println("<input type=\"hidden\" name=\"create\" value=\"1\">");
           if (set_id != 0) out.println("<input type=\"hidden\" name=\"set_id\" value=\"" + set_id + "\">");
           out.println("<input type=\"hidden\" name=\"proid\" value=\"" + proid + "\">");
           out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
           out.println("<input type=\"hidden\" name=\"deleteSet\" value=\"yes\">");

           out.println("<input type=\"hidden\" name=\"promptConfirm\" value=\"yes\">");
           out.println("All lessons included in this lesson set will have their association with the set removed, however, those lessons will <b>not</b> be cancelled unless selected below.");
           out.println("<br><br>Would you like to cancel any remaining upcoming lessons as well (past lessons will not be cancelled)?");
           out.println("<br><br><input type=\"submit\" name=\"leaveLessons\" value=\"No - Don't Cancel Lessons\">");
           out.println("&nbsp;&nbsp;<input type=\"submit\" name=\"cancelLessons\" value=\"Yes - Cancel Remaining Lessons\">");
           out.println("</form>");
           out.println("<button onclick=\"location.href='Proshop_lesson?sets&proid=" + proid + "'\">Return to Set List</button>");

           out.println("</td></tr>");

           out.println("</table>");

       } else {     // No remaining upcoming lessons found.  Prompt user to confirm deletion of set

           out.println("<script language='JavaScript'>");             // Move Notes into textarea
           out.println("<!--");                                              // end of script function
           
           out.println("function submitForm() {");
           out.println(" f = document.forms['frmConfirmDelete'];");
           out.println(" f.btnConfirmDelete.value='Please Wait';");
           out.println(" f.btnConfirmDelete.disabled=true;");
           out.println(" f.btnReturn.disabled=true;");
           out.println(" f.submit();");
           out.println("}");
           
           out.println("// -->");
           out.println("</script>");          // End of script
      
           out.println(SystemUtils.HeadTitle("Proshop - Delete Lesson Set"));
           out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
           SystemUtils.getProshopSubMenu(req, out, lottery);            // required to allow submenus on this page
           out.println("<font face=\"Verdana, Arial, Helvetica, Sans-serif\">");

           out.println("<table border=\"0\" bgcolor=\"#FFFFFF\" align=\"center\" width=\"100%\">");        // Main structure table for centering page elements

           out.println("<tr><td align=\"center\">");

           out.println("<br><br><h3>Delete Lesson Set - " + name + "</h3><br><br>");

           out.println("<form action=\"Proshop_lesson\" method=\"post\" name=\"frmConfirmDelete\">");
           out.println("<input type=\"hidden\" name=\"create\" value=\"1\">");
           if (set_id != 0) out.println("<input type=\"hidden\" name=\"set_id\" value=\"" + set_id + "\">");
           out.println("<input type=\"hidden\" name=\"proid\" value=\"" + proid + "\">");
           out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
           out.println("<input type=\"hidden\" name=\"deleteSet\" value=\"yes\">");

           out.println("The lesson set <b>" + name + "</b> will be removed from the system.  ");
           if (req.getParameter("cancelLessons") != null) {
               out.println("Remaining upcoming lessons <b>will</b> be cancelled.");
               out.println("<input type=\"hidden\" name=\"cancelLessons\" value=\"yes\">");
           } else if (req.getParameter("leaveLessons") != null) {
               out.println("Remaining upcoming lessons will <b>not</b> be cancelled.");
           }
           out.println("<br><br>Are you sure you would like to procede and remove this lesson set?");

           out.println("<br><br><input type=\"button\" name=\"btnReturn\" value=\"No - Return to Set List\" onclick=\"location.href='Proshop_lesson?sets&proid=" + proid + "'\">");
           out.println("<input type=\"hidden\" name=\"confirmDelete\" value=\"1\">");
           out.println("&nbsp;&nbsp;<input type=\"button\" name=\"btnConfirmDelete\" value=\"Yes - Delete Set\" onclick=\"submitForm();\">");

           out.println("</form>");
           out.println("</td></tr>");

           out.println("</table>");
       }
   }
 }
 
 private void displayLessonSets(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session, Connection con, int proid)
              throws ServletException, IOException {

   PreparedStatement pstmt = null;
   ResultSet rs = null;

   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String user = (String)session.getAttribute("user");               // get username
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int sess_activity_id = (Integer)session.getAttribute("activity_id");
   int lottery = Integer.parseInt(templott);

   String name = "";

   int set_id = 0;

   boolean found = false;

   String [] day_table = { "inv", "Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat" };

   //
   //  Build the html page to display lesson sets
   //
   out.println(SystemUtils.HeadTitle("Proshop - Select Lesson Set"));
   out.println("<style>");
   out.println("table.setsTable {");
   out.println("  margin: 0;");
   out.println("  background: #F5F5DC;");
   out.println("  font: normal 14px \"Trebuchet MS\", Verdana, Arial, Helvetica, sans-serif;");
   out.println("  align: center;");
   out.println("}");
   out.println("table.setsTable td {");
   out.println("  font: normal 14px;");
   out.println("}");
   out.println("table.setsTable td.headerRow {");
   out.println("  font: normal 18px;");
   out.println("  font-weight: bold;");
   out.println("  text-decoration: underline;");
   out.println("  text-align: left;");
   out.println("}");
   out.println("</style>");
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);            // required to allow submenus on this page
   out.println("<font face=\"Verdana, Arial, Helvetica, Sans-serif\">");

   out.println("<table border=\"0\" bgcolor=\"#FFFFFF\" align=\"center\" width=\"100%\">");        // Main structure table for centering page elements

   out.println("<tr><td align=\"center\"><br>");
   out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("<b>Lesson Set Selection</b><br>");
      out.println("<br>Select an existing Lesson Set from the list below or select \"Create New Set\" to create a new set.<br>");
   out.println("</font></td></tr></table>");
   out.println("</td></tr>");

   out.println("<tr><td align=\"center\">");
   printLessonProSelect(club, user, sess_activity_id, proid, "sets", "yes", out, con);
   out.println("</td></tr>");

   out.println("<tr><td align=\"center\">");
   out.println("<table border=\"0\" bgcolor=\"#FFFFFF\" align=\"center\"><tr>");
   out.println("<form action=\"Proshop_lesson\" method=\"post\">");
   out.println("<td align=\"center\">");
   out.println("<input type=\"hidden\" name=\"proid\" value=\"" + proid + "\">");
   out.println("<input type=\"submit\" name=\"createSet\" value=\"Create New Set\">");
   out.println("</td>");
   out.println("</form>");
   out.println("<form action=\"Proshop_lesson\" method=\"post\">");
   out.println("<td align=\"center\">");
   out.println("<input type=\"hidden\" name=\"proid\" value=\"" + proid + "\">");
   out.println("<input type=\"submit\" value=\"My Lesson Book\">");
   out.println("</td>");
   out.println("</form>");
   out.println("<form action=\"Proshop_announce\" method=\"get\">");
   out.println("<td align=\"center\">");
   out.println("<input type=\"submit\" name=\"sets\" value=\"Home\">");
   out.println("</td>");
   out.println("</form>");
   out.println("</tr></table>");
   out.println("</td></tr>");

   out.println("<tr><td align=\"center\">");
   out.println("</td></tr>");

   out.println("<tr><td align=\"center\">");
   out.println("<table class=\"setsTable\" border=\"1\" cellpadding=\"5\">");
   out.println("<tr>");
   out.println("<td>&nbsp;</td>");
   out.println("<td class=\"headerRow\">");
   out.println("Lesson Set Name");
   out.println("</td>");
   /*
   out.println("<td class=\"headerRow\">");
   out.println("First Lesson");
   out.println("</td>");
   out.println("<td class=\"headerRow\">");
   out.println("Last Lesson");
   out.println("</td>");
   out.println("<td class=\"headerRow\">");
   out.println("# of Lessons");
   out.println("</td>");
   */
   out.println("<td class=\"headerRow\" style=\"width:240px;\">");
   out.println("Description");
   out.println("</td>");
   out.println("</tr>");

   // Get all lesson sets for the selected pro
   try {

       int lesson_set_count = 0;

       pstmt = con.prepareStatement("" +
               "SELECT name, set_id, description " +
               //"    (SELECT MIN(DATE) FROM lessonbook5 WHERE set_id = lr.set_id) AS date_min, " +
               //"    (SELECT MAX(DATE) FROM lessonbook5 WHERE set_id = lr.set_id) AS date_max, " +
               //"    (SELECT count(*) FROM lessonbook5 WHERE set_id = lr.set_id) as lesson_count " +
               "FROM lessonsets lr " +
               "WHERE proid = ? AND activity_id = ? " +
               "ORDER BY name");
       pstmt.clearParameters();
       pstmt.setInt(1, proid);
       pstmt.setInt(2, sess_activity_id);

       rs = pstmt.executeQuery();

       while (rs.next()) {

           name = rs.getString("name");
           set_id = rs.getInt("set_id");

           out.println("<tr>");
           out.println("<td>");
           out.println("<table border=\"0\" align=\"center\"><tr>");
           out.println("<form action=\"Proshop_lesson\" method=\"post\" name=\"lessonSetSelect" + set_id + "\">");
           out.println("<td>");
           out.println("<input type=\"hidden\" name=\"set_id\" value=\"" + set_id + "\">");
           out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
           out.println("<input type=\"hidden\" name=\"proid\" value=\"" + proid + "\">");
           out.println("<input type=\"submit\" name=\"sets2\" value=\"Select\">");
           out.println("</td>");
           out.println("</form>");
           out.println("<form action=\"Proshop_lesson\" method=\"post\">");
           out.println("<td align=\"center\">");
           out.println("<input type=\"hidden\" name=\"proid\" value=\"" + proid + "\">");
           out.println("<input type=\"hidden\" name=\"set_id\" value=\"" + set_id + "\">");
           out.println("<input type=\"submit\" name=\"createSet\" value=\"Edit\">");
           out.println("</td>");
           out.println("</form>");
           out.println("<form action=\"Proshop_lesson\" method=\"post\">");
           out.println("<td align=\"center\">");
           out.println("<input type=\"hidden\" name=\"proid\" value=\"" + proid + "\">");
           out.println("<input type=\"hidden\" name=\"set_id\" value=\"" + set_id + "\">");
           out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
           out.println("<input type=\"submit\" name=\"deleteSet\" value=\"Delete\">");
           out.println("</td>");
           out.println("</form>");
           out.println("</tr></table>");
           out.println("</td>");
           out.println("<td>" + name + "</td>");
           //out.println("<td>&nbsp;</td>");
           //out.println("<td>&nbsp;</td>");
           //out.println("<td>&nbsp;</td>");
           //out.println("<td>" + (rs.getInt("date_min") == 0 ? "N/A" : Utilities.getDateFromYYYYMMDD(rs.getInt("date_min"), 2)) + "</td>");
           //out.println("<td>" + (rs.getInt("date_max") == 0 ? "N/A" : Utilities.getDateFromYYYYMMDD(rs.getInt("date_max"), 2)) + "</td>");
           //out.println("<td>" + rs.getInt("lesson_count") + "</td>");
           out.println("<td>" + (rs.getString("description").equals("") ? "&nbsp;" : rs.getString("description")) + "</td>");
           out.println("</tr>");
           lesson_set_count++;
       }

       if (lesson_set_count == 0) {
           out.println("<tr><td colspan=\"3\" style=\"text-align:center;\">No lesson sets found</td></tr>");
       }

   } catch (Exception exc) {
       out.println("Error looking up lesson sets.  Error = " + exc.toString());
   } finally {

       try { rs.close(); }
       catch (Exception ignore) { }

       try { pstmt.close(); }
       catch (Exception ignore) { }
   }

   out.println("</td></tr>");
   out.println("</table>");    // End main structure table

   out.println("</td></tr>");

   out.println("<tr><td align=\"center\"><br>");
   out.println("<table border=\"0\" bgcolor=\"#FFFFFF\" align=\"center\">");
   out.println("<form action=\"Proshop_lesson\" method=\"post\">");
   out.println("<td align=\"center\">");
   out.println("<input type=\"hidden\" name=\"proid\" value=\"" + proid + "\">");
   out.println("<input type=\"submit\" name=\"createSet\" value=\"Create New Set\">");
   out.println("</td>");
   out.println("</form>");
   out.println("<form action=\"Proshop_lesson\" method=\"post\">");
   out.println("<td align=\"center\">");
   out.println("<input type=\"hidden\" name=\"proid\" value=\"" + proid + "\">");
   out.println("<input type=\"submit\" value=\"My Lesson Book\">");
   out.println("</td>");
   out.println("</form>");
   out.println("<form action=\"Proshop_announce\" method=\"get\">");
   out.println("<td align=\"center\">");
   out.println("<input type=\"submit\" name=\"sets\" value=\"Home\">");
   out.println("</td>");
   out.println("</form>");
   out.println("</tr></table>");
   out.println("</td></tr>");

   out.println("</table>");
 }

 private void displayLessonSetContent(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session, Connection con, int proid)
              throws ServletException, IOException {

   PreparedStatement pstmt = null;
   ResultSet rs = null;

   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String user = (String)session.getAttribute("user");               // get username
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int sess_activity_id = (Integer)session.getAttribute("activity_id");
   int lottery = Integer.parseInt(templott);

   String name = "";

   int date = 0;
   int time = 0;
   int set_id = 0;
   int recid = 0;
   int dayNum = 0;
   int cur_date = 0;

   boolean normColor = false;

   try {
       set_id = Integer.parseInt(req.getParameter("set_id"));
   } catch (Exception exc) {
       set_id = 0;
   }

   // look up the set name based on the id
   if (req.getParameter("name") != null) {

       name = req.getParameter("name");
       
   } else {  // name not passed, look it up from the db

       try {

           pstmt = con.prepareStatement("SELECT name FROM lessonsets WHERE set_id = ?");
           pstmt.clearParameters();
           pstmt.setInt(1, set_id);

           rs = pstmt.executeQuery();

           if (rs.next()) {
               name = rs.getString("name");
           }

       } catch (Exception exc) {
           Utilities.logError("Proshop_lesson.displayLessonSetContent - " + club + " - Error looking up set name - Err: " + exc.toString());
       } finally {

           try { rs.close(); }
           catch (Exception ignore) { }

           try { pstmt.close(); }
           catch (Exception ignore) { }
       }
   }

   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

   //
   //  Build the html page to display lessons in this set
   //
   if (set_id != 0) {
       
       out.println(SystemUtils.HeadTitle("Proshop - Display Lesson Set Content"));
       out.println("<style>");
       out.println("table.setsTable {");
       out.println("  margin: 0;");
       out.println("  background: #F5F5DC;");
       out.println("  font: normal 14px \"Trebuchet MS\", Verdana, Arial, Helvetica, sans-serif;");
       out.println("  align: center;");
       out.println("  width: 70%;");
       out.println("}");
       out.println("table.setsTable td {");
       out.println("  font: normal 14px;");
       out.println("}");
       out.println("table.setsTable td.titleRow {");
       out.println("  background-color: #336633;");
       out.println("  color: #FFFFFF;");
       out.println("  font: normal 18px;");
       out.println("  font-weight: bold;");
       out.println("  text-align: center;");
       out.println("}");
       out.println("table.setsTable td.headerRow {");
       out.println("  font: normal 18px;");
       out.println("  font-weight: bold;");
       out.println("  text-decoration: underline;");
       out.println("  text-align: left;");
       out.println("}");
       out.println("table.setsTable td.norm {");
       out.println("  background-color: #F5F5DC;");
       out.println("}");
       out.println("table.setsTable td.alt {");
       out.println("  background-color: #CCCCAA;");
       out.println("}");
       out.println("</style>");
       out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
       SystemUtils.getProshopSubMenu(req, out, lottery);            // required to allow submenus on this page
       out.println("<font face=\"Verdana, Arial, Helvetica, Sans-serif\">");

       out.println("<table border=\"0\" bgcolor=\"#FFFFFF\" align=\"center\" width=\"100%\">");        // Main structure table for centering page elements

       out.println("<tr><td align=\"center\"><br>");
       out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\">");
       out.println("<tr><td align=\"center\">");
       out.println("<font color=\"#FFFFFF\" size=\"2\">");
          out.println("<b>Display Lesson Set Content</b><br>");
          out.println("<br>All lessons that are currently contained in this lesson set are displayed below." +
                  "<br>To make changes or view details on a specific lesson, click \"Select\" to the right of that lesson.<br>");
       out.println("</font></td></tr></table>");
       out.println("</td></tr>");

       out.println("<tr><td align=\"center\">");
       printLessonProSelect(club, user, sess_activity_id, proid, "sets", "yes", out, con);
       out.println("</td></tr>");


       out.println("<tr><td align=\"center\">");
       out.println("<b>Lesson Set:&nbsp;&nbsp;" + name + "<b>");
       out.println("</td></tr>");

       out.println("<tr><td align=\"center\">");
       out.println("<br><table border=\"0\" bgcolor=\"#FFFFFF\" align=\"center\"><tr>");
       out.println("<form action=\"Proshop_lesson\" method=\"post\">");
       out.println("<td align=\"center\">");
       out.println("<input type=\"hidden\" name=\"proid\" value=\"" + proid + "\">");
       out.println("<input type=\"submit\" name=\"sets\" value=\"Return To Set List\">");
       out.println("</td>");
       out.println("</form>");
       out.println("<form action=\"Proshop_lesson\" method=\"post\">");
       out.println("<td align=\"center\">");
       out.println("<input type=\"hidden\" name=\"proid\" value=\"" + proid + "\">");
       out.println("<input type=\"hidden\" name=\"set_id\" value=\"" + set_id + "\">");
       out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
       out.println("<input type=\"submit\" name=\"deleteSet\" value=\"Delete This Set\">");
       out.println("</td>");
       out.println("</form>");
       out.println("<form action=\"Proshop_lesson\" method=\"post\">");
       out.println("<td align=\"center\">");
       out.println("<input type=\"hidden\" name=\"proid\" value=\"" + proid + "\">");
       out.println("<input type=\"submit\" value=\"My Lesson Book\">");
       out.println("</td>");
       out.println("</form>");
       out.println("<form action=\"Proshop_announce\" method=\"get\">");
       out.println("<td align=\"center\">");
       out.println("<input type=\"submit\" name=\"sets\" value=\"Home\">");
       out.println("</td>");
       out.println("</form>");
       out.println("</tr></table>");
       out.println("</td></tr>");

       out.println("<tr><td align=\"center\">");
       out.println("</td></tr>");

       // Print table of upcoming lessons
       out.println("<tr><td align=\"center\">");
       out.println("<table class=\"setsTable\" border=\"1\" cellpadding=\"5\">");
       out.println("<tr><td class=\"titleRow\" colspan=\"9\">Upcoming Lessons</td></tr>");
       out.println("<tr>");
       out.println("<td>&nbsp;</td>");
       out.println("<td class=\"headerRow\">");
       out.println("Date");
       out.println("</td>");
       out.println("<td class=\"headerRow\">");
       out.println("Day");
       out.println("</td>");
       out.println("<td class=\"headerRow\">");
       out.println("Time");
       out.println("</td>");
       out.println("<td class=\"headerRow\">");
       out.println("Member Name");
       out.println("</td>");
       out.println("<td class=\"headerRow\">");
       out.println("Lesson Type");
       out.println("</td>");
       out.println("<td class=\"headerRow\">");
       out.println("Length (mins)");
       out.println("</td>");
       out.println("<td class=\"headerRow\" style=\"width:240px;\">");
       out.println("Notes");
       out.println("</td>");
       out.println("<td class=\"headerRow\">");
       out.println("History");
       out.println("</td>");
       out.println("</tr>");

       // Get all upcoming lessons for the selected lesson set
       try {

           int lesson_count = 0;

           cur_date = (int)Utilities.getDate(con);       // Reset starting date to today's date

           pstmt = con.prepareStatement("" +
                   "SELECT *, (SELECT length FROM lessontype5 lt WHERE lt.activity_id = lb.activity_id AND lt.proid = lb.proid AND lt.ltname = lb.ltype) AS length " +
                   "FROM lessonbook5 lb " +
                   "WHERE set_id = ? AND date >= ? ORDER BY date");
           pstmt.clearParameters();
           pstmt.setInt(1, set_id);
           pstmt.setLong(2, cur_date);

           rs = pstmt.executeQuery();

           while (rs.next()) {

               recid = rs.getInt("recid");
               date = rs.getInt("date");
               time = rs.getInt("time");
               dayNum = rs.getInt("dayNum");

               if (cur_date != date) {
                   
                   cur_date = date;
                   normColor = !normColor;
               }

               out.println("<tr>");
               out.println("<form action=\"Proshop_lesson\" method=\"post\" name=\"lessonSelect" + recid + "\" target=\"_top\">");
               out.println("<td class=\"" + (normColor ? "norm" : "alt") + "\">");
               out.println("<input type=\"hidden\" name=\"lesson_id\" value=\"" + recid + "\">");
               out.println("<input type=\"hidden\" name=\"proid\" value=\"" + rs.getInt("proid") + "\">");
               out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + Utilities.getDateFromYYYYMMDD(date, 2) + "\">");
               out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
               out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"0\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_table[dayNum] + "\">");
               out.println("<input type=\"hidden\" name=\"caller\" value=\"lessonset\">");
               out.println("<input type=\"hidden\" name=\"reqtime\" value=\"yes\">");
               out.println("<input type=\"submit\" value=\"Select\">");
               out.println("</td>");
               out.println("</form>");
               out.println("<td class=\"" + (normColor ? "norm" : "alt") + "\">" + Utilities.getDateFromYYYYMMDD(date, 2) + "</td>");
               out.println("<td class=\"" + (normColor ? "norm" : "alt") + "\">" + day_table[dayNum] + "</td>");
               out.println("<td class=\"" + (normColor ? "norm" : "alt") + "\">" + Utilities.getSimpleTime(time) + "</td>");
               out.println("<td class=\"" + (normColor ? "norm" : "alt") + "\">" + rs.getString("memname") + "</td>");
               out.println("<td class=\"" + (normColor ? "norm" : "alt") + "\">" + rs.getString("ltype") + "</td>");
               out.println("<td class=\"" + (normColor ? "norm" : "alt") + "\">" + rs.getInt("length") + "</td>");
               out.println("<td class=\"" + (normColor ? "norm" : "alt") + "\">" + (rs.getString("notes").equals("") ? "&nbsp;" : rs.getString("notes")) + "</td>");
               out.println("<form method=\"post\" action=\"Proshop_lesson\" target=\"_blank\">");
               out.println("<td class=\"" + (normColor ? "norm" : "alt") + "\" align=\"center\">");
               out.println("<input type=\"hidden\" name=\"hist\" value=\"yes\">");
               out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
               out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
               out.println("<input type=\"hidden\" name=\"proid\" value=\"" + proid + "\">");
               out.println("<input type=\"hidden\" name=\"ltimename\" value=\"" + rs.getString("ltname") + "\">");
               out.println("<input type=\"image\" src=\"/" +rev+ "/images/history.gif\" border=\"0\" title=\"Click here to view lesson history.\">");
               out.println("</td></form>");
               out.println("</tr>");
               lesson_count++;
           }

           if (lesson_count == 0) {
               out.println("<tr><td colspan=\"9\" style=\"text-align:center;\">No upcoming lessons found</td></tr>");
           }

       } catch (Exception exc) {
           out.println("Error looking up lesson sets.  Error = " + exc.toString());
       } finally {

           try { rs.close(); }
           catch (Exception ignore) { }

           try { pstmt.close(); }
           catch (Exception ignore) { }
       }

       out.println("</table>");
       out.println("</td></tr>");
       
       // Print table of past lessons
       out.println("<tr><td align=\"center\">");
       out.println("<br><table class=\"setsTable\" border=\"1\" cellpadding=\"5\">");
       out.println("<tr><td class=\"titleRow\" colspan=\"9\">Past Lessons</td></tr>");
       out.println("<tr>");
       out.println("<td>&nbsp;</td>");
       out.println("<td class=\"headerRow\">");
       out.println("Date");
       out.println("</td>");
       out.println("<td class=\"headerRow\">");
       out.println("Day");
       out.println("</td>");
       out.println("<td class=\"headerRow\">");
       out.println("Time");
       out.println("</td>");
       out.println("<td class=\"headerRow\">");
       out.println("Member Name");
       out.println("</td>");
       out.println("<td class=\"headerRow\">");
       out.println("Lesson Type");
       out.println("</td>");
       out.println("<td class=\"headerRow\">");
       out.println("Length (mins)");
       out.println("</td>");
       out.println("<td class=\"headerRow\" style=\"width:240px;\">");
       out.println("Notes");
       out.println("</td>");
       out.println("<td class=\"headerRow\">");
       out.println("History");
       out.println("</td>");
       out.println("</tr>");

       // Get all past lessons for the selected lesson set
       try {

           int lesson_count = 0;

           cur_date = (int)Utilities.getDate(con);       // Reset starting date to today's date

           pstmt = con.prepareStatement("" +
                   "SELECT *, (SELECT length FROM lessontype5 lt WHERE lt.activity_id = lb.activity_id AND lt.proid = lb.proid AND lt.ltname = lb.ltype) AS length " +
                   "FROM lessonbook5 lb " +
                   "WHERE set_id = ? AND date < ? ORDER BY date DESC");
           pstmt.clearParameters();
           pstmt.setInt(1, set_id);
           pstmt.setLong(2, cur_date);

           rs = pstmt.executeQuery();

           while (rs.next()) {

               recid = rs.getInt("recid");
               date = rs.getInt("date");
               time = rs.getInt("time");
               dayNum = rs.getInt("dayNum");

               if (cur_date != date) {

                   cur_date = date;
                   normColor = !normColor;
               }

               out.println("<tr>");
               out.println("<form action=\"Proshop_lesson\" method=\"post\" name=\"lessonSelect" + recid + "\" target=\"_top\">");
               out.println("<td class=\"" + (normColor ? "norm" : "alt") + "\">");
               out.println("<input type=\"hidden\" name=\"lesson_id\" value=\"" + recid + "\">");
               out.println("<input type=\"hidden\" name=\"proid\" value=\"" + rs.getInt("proid") + "\">");
               out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + Utilities.getDateFromYYYYMMDD(date, 2) + "\">");
               out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
               out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"0\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_table[dayNum] + "\">");
               out.println("<input type=\"hidden\" name=\"caller\" value=\"lessonset\">");
               out.println("<input type=\"hidden\" name=\"reqtime\" value=\"yes\">");
               out.println("<input type=\"submit\" value=\"Select\">");
               out.println("</td>");
               out.println("</form>");
               out.println("<td class=\"" + (normColor ? "norm" : "alt") + "\">" + Utilities.getDateFromYYYYMMDD(date, 2) + "</td>");
               out.println("<td class=\"" + (normColor ? "norm" : "alt") + "\">" + day_table[dayNum] + "</td>");
               out.println("<td class=\"" + (normColor ? "norm" : "alt") + "\">" + Utilities.getSimpleTime(time) + "</td>");
               out.println("<td class=\"" + (normColor ? "norm" : "alt") + "\">" + rs.getString("memname") + "</td>");
               out.println("<td class=\"" + (normColor ? "norm" : "alt") + "\">" + rs.getString("ltype") + "</td>");
               out.println("<td class=\"" + (normColor ? "norm" : "alt") + "\">" + rs.getInt("length") + "</td>");
               out.println("<td class=\"" + (normColor ? "norm" : "alt") + "\">" + (rs.getString("notes").equals("") ? "&nbsp;" : rs.getString("notes")) + "</td>");
               out.println("<form method=\"post\" action=\"Proshop_lesson\" target=\"_blank\">");
               out.println("<td class=\"" + (normColor ? "norm" : "alt") + "\" align=\"center\">");
               out.println("<input type=\"hidden\" name=\"hist\" value=\"yes\">");
               out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
               out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
               out.println("<input type=\"hidden\" name=\"proid\" value=\"" + proid + "\">");
               out.println("<input type=\"hidden\" name=\"ltimename\" value=\"" + rs.getString("ltname") + "\">");
               out.println("<input type=\"image\" src=\"/" +rev+ "/images/history.gif\" border=\"0\" title=\"Click here to view lesson history.\">");
               out.println("</td></form>");
               out.println("</tr>");
               lesson_count++;
           }

           if (lesson_count == 0) {
               out.println("<tr><td colspan=\"9\" style=\"text-align:center;\">No past lessons found</td></tr>");
           }

       } catch (Exception exc) {
           out.println("Error looking up lesson sets.  Error = " + exc.toString());
       } finally {

           try { rs.close(); }
           catch (Exception ignore) { }

           try { pstmt.close(); }
           catch (Exception ignore) { }
       }

       out.println("</table>");
       out.println("</td></tr>");


       out.println("<tr><td align=\"center\">");
       out.println("<br><table border=\"0\" bgcolor=\"#FFFFFF\" align=\"center\"><tr>");
       out.println("<form action=\"Proshop_lesson\" method=\"post\">");
       out.println("<td align=\"center\">");
       out.println("<input type=\"hidden\" name=\"proid\" value=\"" + proid + "\">");
       out.println("<input type=\"submit\" name=\"sets\" value=\"Return To Set List\">");
       out.println("</td>");
       out.println("</form>");
       out.println("<form action=\"Proshop_lesson\" method=\"post\">");
       out.println("<td align=\"center\">");
       out.println("<input type=\"hidden\" name=\"proid\" value=\"" + proid + "\">");
       out.println("<input type=\"hidden\" name=\"set_id\" value=\"" + set_id + "\">");
       out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
       out.println("<input type=\"submit\" name=\"deleteSet\" value=\"Delete This Set\">");
       out.println("</td>");
       out.println("</form>");
       out.println("<form action=\"Proshop_lesson\" method=\"post\">");
       out.println("<td align=\"center\">");
       out.println("<input type=\"hidden\" name=\"proid\" value=\"" + proid + "\">");
       out.println("<input type=\"submit\" value=\"My Lesson Book\">");
       out.println("</td>");
       out.println("</form>");
       out.println("<form action=\"Proshop_announce\" method=\"get\">");
       out.println("<td align=\"center\">");
       out.println("<input type=\"submit\" name=\"sets\" value=\"Home\">");
       out.println("</td>");
       out.println("</form>");
       out.println("</tr></table>");
       out.println("</td></tr>");

       out.println("</td></tr>");
       out.println("</table>");    // End main structure table
   }
 }

 private void displayLessonHist (HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session, Connection con, int proid)
              throws ServletException, IOException {

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    int hr = 0;
    int min = 0;
    int count = 0;
    int date = 0;
    int time = 0;

    String stime = "";
    String ampm = "";
    String pro_name = "";
    String bgcolor = "";
    String modType = "";
    String ltimename = "";

    boolean flipColor = false;
    
    int sess_activity_id = (Integer)session.getAttribute("activity_id");

    // Get passed parameters
    date = Integer.parseInt(req.getParameter("date"));
    time = Integer.parseInt(req.getParameter("time"));
    ltimename = req.getParameter("ltimename");

    out.println(SystemUtils.HeadTitle("Lesson History"));
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    out.println("");

    //
    //  create a time string for display
    //
    hr = time / 100;
    min = time - (hr * 100);

    if (hr > 11) {
        ampm = " PM";
        if (hr > 12) hr = hr - 12;
    }

    stime = hr + ":" + ((min < 10) ? "0" : "") + min + ampm;

    try {

        // Look up pro name based on passed proid
        pstmt = con.prepareStatement("SELECT CONCAT(fname, ' ', lname) AS pro_name FROM lessonpro5 WHERE id = ?");
        pstmt.clearParameters();
        pstmt.setInt(1, proid);

        rs = pstmt.executeQuery();

        if (rs.next()) {
            pro_name = rs.getString("pro_name");
        }

    } catch (Exception exc) {
        Utilities.logError("Proshop_lesson.displayLessonHist - Error looking up pro name - Err: " + exc.toString());
    } finally {

        try { rs.close(); }
        catch (Exception ignore) { }

        try { pstmt.close(); }
        catch (Exception ignore) { }
    }

    out.println("<center><p><font size=5>Lesson History</font></p>");
    out.println("<b>Lesson Pro:</b>&nbsp;&nbsp;" + pro_name);
    out.println("&nbsp;&nbsp;&nbsp;<b>Lesson Date:</b>&nbsp;&nbsp;" + Utilities.getDateFromYYYYMMDD(date, 2));
    out.println("&nbsp;&nbsp;&nbsp;<b>Time:</b>&nbsp;&nbsp;" + stime);
    out.println("&nbsp;&nbsp;&nbsp;<b>Lesson Time Name:</b>&nbsp;&nbsp;" + ltimename);
    out.println("</center><br>");

    out.println("<center><form>");
    out.println("<input type=image src=\"/" +rev+ "/images/print.gif\" width=\"80\" height=\"18\" border=\"0\" onclick=\"window.print(); return false;\" alt=\"Click here to print this history.\">");
    out.println("&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;");
    out.println("<input type=button value=\"  Close  \" onclick=\"window.close();\">");
    out.println("</form></center>");

    try {

        // Look up history entries for this slot
        pstmt = con.prepareStatement (
            "SELECT type, sdate, mname, user, player2, player3, player4 " +
            "FROM teehist " +
            "WHERE date = ? AND time = ? AND courseName = ? AND fb = 99 AND player1 = ? " +
            "ORDER BY mdate DESC, sdate DESC");

        pstmt.clearParameters();        // clear the parms
        pstmt.setLong(1, date);
        pstmt.setInt(2, time);
        pstmt.setString(3, ltimename);
        pstmt.setString(4, pro_name);
        
        rs = pstmt.executeQuery();      // execute the prepared stmt

        out.println("<table align=center border=1 cellspacing=5 cellpadding=3>");

        while (rs.next()) {

           modType = "";

           if (count == 0) {
              out.println("<tr bgcolor=\"#336633\" style=\"color: white\">");
              out.println("<td nowrap><b>Date & Time</b></td>");
              out.println("<td><b>User</b></td>");
              out.println("<td><b>Lesson Type</b></td>");
              out.println("<td nowrap><b>Member Name</b></td>");
              if (sess_activity_id != 0) {
                  out.println("<td nowrap><b>Location</b></td>");
              }
              out.println("<td nowrap><b>Modification Type</b></td>");
              out.println("</tr>");
           }

           if (rs.getInt("type") == 0) {
               modType = "New";
           } else if (rs.getInt("type") == 1) {
               modType = "Modified";
           } else if (rs.getInt("type") == 2) {
               modType = "Cancelled";
           }

           flipColor = !flipColor;

           bgcolor = (flipColor) ? "#F9F9F9" : "#F2F2F2";

           out.println("<tr bgcolor=\"" + bgcolor + "\">");
           out.print("<td nowrap>" + rs.getString("sdate") + "</td>"); // date & time stamp
           out.print("<td nowrap>" + rs.getString("user") + "</td>"); // username
           out.print("<td nowrap>" + (!rs.getString("player3").equals("") ? rs.getString("player3") : "&nbsp;") + "</td>"); // Lesson Type
           out.print("<td nowrap>" + (!rs.getString("player2").equals("") ? rs.getString("player2") : "&nbsp;") + "</td>"); // Member Name
           if (sess_activity_id != 0) {
               out.print("<td nowrap>" + (!rs.getString("player4").equals("") ? rs.getString("player4") : "N/A") + "</td>"); // Member Name
           }
           out.print("<td nowrap>" + modType + "</td>"); // Modification type (New/Modified/Canceled)
           out.println("</tr>");

           count++;
        }

        out.println("</table>");
        
    } catch (Exception exc) {

        Utilities.logError("Proshop_lesson.displayLessonHist - Error looking up pro name - Err: " + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) { }

        try { pstmt.close(); }
        catch (Exception ignore) { }
    }

    if (count == 0) {
        out.println("<center><br><p><i><b><font color=red>No history found for this lesson time.</font></b></i></p>");
        //out.println("<br><form><input type=button value=\"  Close  \" onclick=\"window.close();\"></form><br>");
        out.println("<font size=-1>(this window will auto close in 3 seconds)</font></center>");
        out.println("<script>setTimeout('window.close()', 3000)</script>");
    } else {
        out.println("<p align=center><b>NOTE:</b> Time is in military format as hours:minutes:seconds.milli-seconds (in your local time).</p>");
        out.println("<p align=center><i><b>"+ count +" historical " + ((count > 1) ? "entries" : "entry") + " found for this lesson time.</b></i></p>");
        //out.println("<br><form><input type=button value=\"  Close  \" onclick=\"window.close();\"></form>");
    }

    out.println("<br>");

 //displayDatabaseErrMsg("Error looking up lesson time history.", exp.getMessage(), out);
 
 }


 // *********************************************************
 //  Clear the in-use flag for entire day
 // *********************************************************

 private void clearInUse(int id, int activity_id, long date, Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   try {
    
      pstmt = con.prepareStatement (
         "UPDATE lessonbook5 " +
         "SET in_use = 0 " +
         "WHERE proid = ? AND activity_id = ? AND date = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1, id);
      pstmt.setInt(2, activity_id);
      pstmt.setLong(3, date);

      pstmt.executeUpdate();      // execute the prepared stmt

      pstmt.close();

   }
   catch (Exception ignore) {
   }
 }
   

 // *********************************************************
 //  Display Notes in new pop-up window
 // *********************************************************

 private void displayNotes(String stime, long date, int id, int activity_id, String dayName, PrintWriter out, Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   String memname = "";
   String memid = "";
   String notes = "";
   String ampm = "";

   int month = 0;
   int day = 0;
   int year = 0;
   int hr = 0;
   int min = 0;

   long mm = 0;
   long dd = 0;
   long yy = 0;

   //
   //  Convert the common string values to int's
   //
   int time = Integer.parseInt(stime);

   //
   //  break down the date and time
   //
   yy = date / 10000;                             // get year
   mm = (date - (yy * 10000)) / 100;              // get month
   dd = (date - (yy * 10000)) - (mm * 100);       // get day

   month = (int)mm;
   day = (int)dd;
   year = (int)yy;

   hr = time / 100;
   min = time - (hr * 100);

   if (hr == 12) {
      ampm = "PM";
   } else {
      if (hr > 12) {
         hr = hr - 12;          // adjust
         ampm = "PM";
      }
   }

   try {

      pstmt = con.prepareStatement (
         "SELECT memname, memid, notes " +
         "FROM lessonbook5 WHERE proid = ? AND activity_id = ? AND date = ? AND time = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1, id);
      pstmt.setInt(2, activity_id);
      pstmt.setLong(3, date);
      pstmt.setInt(4, time);

      rs = pstmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         memname = rs.getString(1);
         memid = rs.getString(2);
         notes = rs.getString(3);
      }

      pstmt.close();

   }
   catch (Exception ignore) {
   }

   out.println(SystemUtils.HeadTitle("Show Notes"));
   out.println("<BODY><CENTER><BR>");
   out.println("<font size=\"3\"><b>Lesson Time Notes</b></font><br><BR>");
   out.println("<font size=\"2\"><b>For " + dayName + " " +month+ "/" +day+ "/" +year);
   if (min < 10) {
      out.println("at " +hr+ ":0" +min+ " " +ampm);
   } else {
      out.println("at " +hr+ ":" +min+ " " +ampm);
   }
   out.println("</b>");
   out.println("<br><br><br>");
   out.println("<b>Member/Activity:</b> &nbsp;&nbsp;" +memname);
   if (!notes.equals( "" ) && notes != null) {
      out.println("<br><br>");
      out.println("<b>Notes:</b> &nbsp;&nbsp;" + notes );
   }
   out.println("<BR><BR>");
   out.println("<form>");
   out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" Value=\"  Close  \" onClick='self.close()' alt=\"Close\">");
   out.println("</form>");
   out.println("</font></CENTER></BODY></HTML>");
   out.close();

 }                   // end of displayNotes

 /**
  * printLessonProSelect - Prints "Lesson Pro: " followed by a drop-down menu containing the names of all lesson pros found for the specified activity_id
  *
  * @param club Name of club.
  * @param user Current user logged in.
  * @param activity_id Specified activity_id to look for lesson pros in (should be a root_activity_id).
  * @param curr_proid Currently selected proid, if applicable.  Pass 0 if no pro selected at this point.
  * @param redirect Optional name of a sub-area of the Lesson system, for redirection purposes (e.g. 'sets' would tell the page to redirect to the Lesson Sets page upon changing the selected pro). Pass a blank String if not needed.
  * @param redirectValue Optional value for the redirect parameter, as some redirect parameters require a passed value.  Pass a blank String if not needed.
  * @param out Output stream.
  * @param con Connection to club database.
  */
 private void printLessonProSelect(String club, String user, int activity_id, int curr_proid, String redirect, String redirectValue, PrintWriter out, Connection con) {

     PreparedStatement pstmt = null;
     ResultSet rs = null;

     out.println("<form action=\"Proshop_lesson\" method=\"post\" name=\"pform\">");

     if (!redirect.equals("")) {
         out.println("<input type=\"hidden\" name=\"" + redirect + "\" value=\"" + redirectValue + "\">");
     }

     out.println("<br><b>Lesson Pro:</b>&nbsp;&nbsp;");
     out.println("<select size=\"1\" name=\"proid\" onChange=\"document.pform.submit()\">");

     try {

        pstmt = con.prepareStatement (
                "SELECT * FROM lessonpro5 where activity_id = ? ORDER BY sort_by");

        pstmt.clearParameters();        // clear the parms
        pstmt.setInt(1, activity_id);
        rs = pstmt.executeQuery();      // execute the prepared pstmt

        while (rs.next()) {

           StringBuffer pro_name = new StringBuffer(rs.getString("fname"));  // get first name

           String mi = rs.getString("mi");                                   // middle initial
           if (!mi.equals( "" )) {
              pro_name.append(" ");
              pro_name.append(mi);
           }
           pro_name.append(" " + rs.getString("lname"));                   // last name

           String suffix = rs.getString("suffix");                         // suffix
           if (!suffix.equals( "" )) {
              pro_name.append(" ");
              pro_name.append(suffix);
           }

           String proName2 = pro_name.toString();                             // convert to one string

           int id2 = rs.getInt("id");                   // get the proid

           if (club.equals("pgmunl")) {

               if ((user.equals("proshopjim") && id2 != 2) || (user.equals("proshopmike") && id2 != 3) ||
                   (user.equals("proshopgreg") && id2 != 4) || (user.equals("proshopchris") && id2 != 5)) {

                   continue;    // If user doesn't match specific pro_id, skip displaying it in the list.
               }
           }

           if (curr_proid == id2 ) {                 // if this is the current pro
              out.println("<option selected value=\"" +id2+ "\">" +proName2+ "</option>");
           } else {
              out.println("<option value=\"" +id2+ "\">" +proName2+ "</option>");
           }
        }
        pstmt.close();

     }
     catch (Exception exc) {

        out.println("<BR><BR>Unable to access the Database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<BR><BR>" + exc.getMessage());
        out.println("<BR><BR>");
     }

     out.println("</select>");
     out.println("</form>");
 }


 // *********************************************************
 //  Block or Unblock the lesson time
 // *********************************************************

 private void blockOneTime(String stime, long date, int block, int id, int activity_id, Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;


   //
   //  Convert the common string values to int's
   //
   int time = Integer.parseInt(stime);

   //
   //  Reverset the block value (if blocked - unblock, and vice versa)
   //
   if (block == 0) {
     
      block = 1;
        
   } else {
     
      block = 0;
   }

   try {

      pstmt = con.prepareStatement (
              "UPDATE lessonbook5 SET block = ? WHERE proid = ? AND activity_id = ? AND date = ? AND time = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1,block);
      pstmt.setInt(2,id);
      pstmt.setInt(3,activity_id);
      pstmt.setLong(4,date);
      pstmt.setInt(5,time);

      pstmt.executeUpdate();

      pstmt.close();

   }
   catch (Exception ignore) {
   }

 }                   // end of blockOneTime

 // ********************************************************************
 //  Process the 'Lesson Report' request
 // ********************************************************************

 private void lessonReport(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException {
   
   PreparedStatement pstmt = null;
   PreparedStatement pstmtx = null;
   Statement stmt = null;
   ResultSet rs = null;
   
   int pro_id = 0;
   int day_Num = 0;
   int id = 0;
   int i2 = 0;
   int i3 = 0;
   int j = 0;
   int num = 0;
   int max = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int month = 0;
   int day = 0;
   int year = 0;
      
   long old_date = 0;
   int oldest_date = 0;
   int oldest_mm = 0;
   int oldest_dd = 0;
   int oldest_yy = 0;
   
   String ltName = "";
   String notes_Lesson = "";
   String color_Lesson = "";
   String memname = "";
   String memid = "";
   String ltype = "";
 
   String error = "";
   String calDate = "";
   String cur_Date = "";
   
   Calendar cal_date = new GregorianCalendar();
   int cal_year = cal_date.get(Calendar.YEAR);
   int cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based
   int cal_day = cal_date.get(Calendar.DAY_OF_MONTH);
   int start_year = cal_year;
   int start_month = cal_month;
   
   Calendar cal = new GregorianCalendar();       // get todays date
   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH) +1;  
   day = cal.get(Calendar.DAY_OF_MONTH) +1;   //Adjust the date to tomorrow's date
   hr = cal.get(Calendar.HOUR_OF_DAY);           // get 24 hr clock value
   min = cal.get(Calendar.MINUTE);
   //
   // Define parms - set defaults
   //   
   String club = (String)session.getAttribute("club");               // get name of club
   String user = (String)session.getAttribute("user");               // get name of user
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int sess_activity_id = (Integer)session.getAttribute("activity_id");
   
   if (req.getParameter("calDate") != null) {

       calDate = req.getParameter("calDate");
   } 
   
   out.println(SystemUtils.HeadTitle("Lesson Book Search"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<HEAD>");
      out.println("<script language='JavaScript'>");            // Erase name script
      out.println("<!--");

      out.println("function erase(pos1) {");
      out.println("document.playerform[pos1].value = '';");           // clear the player field
      out.println("}");                  // end of script function
      
      out.println("// -->");
      out.println("</script>");          // End of script      
      //
      //*******************************************************************
      //  Move a member name into the tee slot
      //*******************************************************************
      //
      out.println("<script language='JavaScript'>");            // Move name script
      out.println("<!--");

      out.println("function movename(name) {");
      out.println("del = ':';");                                // deliminator is a colon
      out.println("array = name.split(del);");
      out.println("var name = array[0];");
      out.println("var username = array[1];");
      out.println("var done = 'no';");
      out.println("if (done == 'no') {");                    // if empty spot not found yet
        out.println("var player1 = document.playerform.player1.value;");
        out.println("if (player1 == '') {");                    // if player is empty
            out.println("document.playerform.player1.value = name;");
            out.println("document.playerform.username.value = username;");
            out.println("done = 'yes';");
        out.println("}");
      out.println("}");
      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");                               // End of script
   out.println("</HEAD>"); 
     
      out.println("<br><table cellpadding=\"3\" align=\"center\" bgcolor=\"#336633\">");       
        out.println("<tr>");
            
            out.println("<th><font color=\"#ffffff\" size=\"2\"><b>Instructions:</b></font></th>");
            out.println("<th><font color=\"#ffffff\" size=\"2\">Select a member from the list or check ALL MEMBERS if you would like to search the lesson book for all members.");
      
      out.println("</font></th></tr></table><br>");
           
      out.println("<table align=\"center\" cellpadding=\"3\" bgcolor=\"#F5F5DC\" style=\"border: 1px solid black\">");     
        out.println("<tr bgcolor=\"#336633\" style=\"font-family:verdana;color:white;font-size:22\">");
            out.println("<th colspan=2>Lesson Book Search</th>");
        out.println("</tr>");
     
        out.println("<tr valign=\"top\" align=\"center\">");
            out.println("<td height=\"50%\">");
            out.println("<font size=\"2\">");
            out.println("<form action=\"Proshop_lesson\" method=\"post\" name=\"playerform\">");
            out.println("Member:");
            out.println("<input type=\"text\" name=\"player1\" size=\"20\" maxlength=\"30\">");
            out.println("<input type=\"hidden\" name=\"username\"size=\"20\" maxlength=\"30\">");
            out.println("&nbsp;&nbsp;<img src=\"/" + rev + "/images/erase.gif\" onClick=\"erase('player1')\" style=\"cursor:hand\">");////MEMBER NAME TABLE AND DELETE IMAGE
            out.println("<input type=\"checkbox\" name=\"All\" id=\"all\" value=\"yes\"><label for=\"all\">All Members");
                        
            out.println("</td>");
            
            out.println("<td rowspan=\"6\">");
                out.println("<table border=\"2\" width=\"140\" bgcolor=\"#F5F5DC\" valign=\"right\"style=\"border: 1px solid black\">");      // name list
                    out.println("<tr>");
                        out.println("<td align=\"center\" bgcolor=\"#336633\">");
                            out.println("<font color=\"#FFFFFF\" size=\"2\">");
                            out.println("<b>Member List</b>");
                            out.println("</font>");
                        out.println("</td>");
                    out.println("</tr>");
                    out.println("<tr>");
                        out.println("<td align=\"center\">");// include the dynamic search box scripts
                            out.println("<font size=\"2\">");
                            out.println("Click on name to add");
                            out.println("</font>");
                        out.println("</td>");
                    out.println("</tr>");

     // ********************************************************************************
     //    build the name list.
     // ********************************************************************************
     String first = "";
     String mid = "";
     String last = "";
     String name = "";
     String dname = "";
     String username = "";
     String prepState = "";
     
     if (proid.equals("999")){       //if all pros
         prepState = "SELECT name_last, name_first, name_mi, username FROM lessonbook5 lb5 INNER JOIN member2b m2b ON m2b.username = lb5.memid WHERE lb5.activity_id = ? AND lb5.length > 0 GROUP BY name_last, name_first";
     } else {
         prepState = "SELECT name_last, name_first, name_mi, username FROM member2b m2b LEFT OUTER JOIN lessonbook5 lb5 ON lb5.memid = m2b.username WHERE lb5.proid = ? AND lb5.activity_id = ? AND lb5.length > 0 GROUP BY memid ORDER BY name_last, name_first";
     }
     
     try {
         
         PreparedStatement pstmty = con.prepareStatement(prepState);

         pstmty.clearParameters();               // clear the parms

         
         if (!proid.equals("999")) {   //if all pros
             pstmty.setString(1, proid);
             pstmty.setInt(2, sess_activity_id);
         } else {
             pstmty.setInt(1, sess_activity_id);
         }
         
         rs = pstmty.executeQuery();             // execute the prepared stmt

         out.println("<tr>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         out.println("<select size=\"22\" style=\"width: 130px\" name=\"bname\" onClick=\"movename(this.form.bname.value)\" style=\"cursor:hand\">");

         while (rs.next()) {

             last = rs.getString("name_last");
             first = rs.getString("name_first");
             mid = rs.getString("name_mi");
             username = rs.getString("username");

             if (mid.equals("")) {

                 name = first + " " + last;
                 dname = last + ", " + first;
             } else if (!last.equals("")) {

                 name = first + " " + mid + " " + last;
                 dname = last + ", " + first + " " + mid;
             }
             name = name + ":" + username;

             out.println("<option value=\"" + name + "\">" + dname + "</option>");
         }
         out.println("</select>");
         out.println("</font>");
         out.println("</td>");
         out.println("</tr>");

         pstmty.close();
     } catch (Exception ignore) {
     }

      out.println("</table>");                // end of name list table
      out.println("</td>");                                      // end of name list column
      out.println("</tr>");
       // lookup oldest date in lesosnbook5 that has a pace status
       try {
          stmt = con.createStatement();
          rs = stmt.executeQuery("SELECT MIN(date) FROM lessonbook5");

          if (rs.next()) {

               old_date = rs.getLong(1);
          }

       } catch (Exception e) {
          SystemUtils.buildDatabaseErrMsg("Proshop_report_custom - Error looking up oldest tee time.", e.getMessage(), out, false);
          return;
       }
       //
       //  Determine oldest date values - month, day, year
       //
       oldest_date = (int)old_date;
       oldest_yy = oldest_date / 10000;
       oldest_mm = (oldest_date - (oldest_yy * 10000)) / 100;
       oldest_dd = oldest_date - ((oldest_yy * 10000) + (oldest_mm * 100));
    
    out.println("<tr valign=top>");
        out.println("<td valign=top height=\"40%\">");
             out.println("<table align=center style=\"border: 1px solid black\">");
             out.println("<th colspan=3 bgcolor=\"#336633\"><font color=\"#FFFFFF\" size=\"2\">Select the date range below</font></th>");
             out.println("<tr valign=top>\n<td align=center>");
   
                out.println(" <div id=cal_elem_0 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>");////CALANDER START DATE FIELD
                out.println(" <br><input type=text name=cal_box_0 id=cal_box_0>");
                out.println("</td>\n<td>&nbsp; &nbsp;</td>\n<td align=center>");
                out.println(" <div id=cal_elem_1 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>");////CALANDER END DATE FIELD
                out.println(" <br><input type=text name=cal_box_1 id=cal_box_1>");
            out.println("</td>\n</tr></table>\n"); 
            out.println("</td>");
        out.println("</tr>");

        out.println("<tr>");
            out.println("<td valign=top align = center>");
            out.println("<input type=\"hidden\" name=\"old_date\" value=\"" + oldest_date + "\">");
            out.println("<input type=\"hidden\" name=\"proid\" value=\"" + proid + "\">");
            out.println("<input type=\"hidden\" name=\"lessonReport2\" value=\"\">");
            out.println("<input type=submit value=\"SUBMIT\" name=\"sub_mit\"></form>");    //SUBMIT button that returns results
            out.println("</td>");
        out.println("</tr>");
   
        out.println("<tr>");
            out.println("<td>");
            out.println("</td>");
        out.println("</tr>");
   
     out.println("</table>");// end of whole page table
     
    // include files for dynamic calendars
    out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv30-styles.css\">");
    out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/calv30-scripts.js\"></script>");

    // output back button form
    out.println("<form method=\"get\" action=\"Proshop_announce\">");
     out.println("<p align=\"center\"><input type=\"submit\" value=\" Home \" style=\"background:#8B8970\"></p>");  //Home button returns you to proshop announce
    out.println("</form>");

    // start calendar javascript setup code
    out.println("<script type=\"text/javascript\">");

     out.println("var g_cal_bg_color = '#F5F5DC';");
     out.println("var g_cal_header_color = '#8B8970';");
     out.println("var g_cal_border_color = '#8B8970';");

     out.println("var g_cal_count = 2;"); // number of calendars on this page
     out.println("var g_cal_year = new Array(g_cal_count - 1);");
     out.println("var g_cal_month = new Array(g_cal_count - 1);");
     out.println("var g_cal_beginning_month = new Array(g_cal_count - 1);");
     out.println("var g_cal_ending_month = new Array(g_cal_count - 1);");
     out.println("var g_cal_beginning_day = new Array(g_cal_count - 1);");
     out.println("var g_cal_ending_day = new Array(g_cal_count - 1);");
     out.println("var g_cal_beginning_year = new Array(g_cal_count - 1);");
     out.println("var g_cal_ending_year = new Array(g_cal_count - 1);");

     // set calendar date parts in js
     out.println("g_cal_month[0] = " + start_month + ";");
     out.println("g_cal_year[0] = " + start_year + ";");
     out.println("g_cal_beginning_month[0] = " + oldest_mm + ";");
     out.println("g_cal_beginning_year[0] = " + oldest_yy + ";");
     out.println("g_cal_beginning_day[0] = " + oldest_dd + ";");
     out.println("g_cal_ending_month[0] = " + cal_month + ";");
     out.println("g_cal_ending_day[0] = " + cal_day + ";");
     out.println("g_cal_ending_year[0] = " + (cal_year+1) + ";");

     out.println("g_cal_month[1] = " + start_month + ";");
     out.println("g_cal_year[1] = " + start_year + ";");
     out.println("g_cal_beginning_month[1] = " + oldest_mm + ";");
     out.println("g_cal_beginning_year[1] = " + oldest_yy + ";");
     out.println("g_cal_beginning_day[1] = " + oldest_dd + ";");
     out.println("g_cal_ending_month[1] = " + cal_month + ";");
     out.println("g_cal_ending_day[1] = " + cal_day + ";");
     out.println("g_cal_ending_year[1] = " + (cal_year+1) + ";");//Set date range out one year

     out.println("function sd(pCal, pMonth, pDay, pYear) {");
     out.println(" f = document.getElementById(\"cal_box_\"+pCal);");
     out.println(" f.value = pYear + \"-\" + pMonth + \"-\" + pDay;");
     out.println("}");

    out.println("</script>");
    out.println("<script type=\"text/javascript\">\n doCalendar('0');\n doCalendar('1');\n</script>");

 }
 // *********************************************************
 //  determine time value for lesson length
// *********************************************************

private void lessonReport2(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
        Connection con, String proid) throws ServletException, IOException {

    PreparedStatement pstmt = null;
    PreparedStatement pstmt2 = null;
    PreparedStatement pst = null;
    PreparedStatement pstmtz = null;
    PreparedStatement pstmt2z = null;
    ResultSet rs = null;
    ResultSet rsz = null;
    ResultSet rsz2 = null;
    
    int id = 0;
    int date = 0;
    int date_temp = 0;
    int jump = 0;
    int billed = 0;
    int time = 0;
    int length = 0; 
    int sDate = 0;
    int eDate = 0;

    String mem_name = "";
    String ltype = "";
    String jumps = "";
    String start_Date = "";
    String start_YY = "";
    String start_mm = "";
    String start_dd = "";
    String end_Date = "";
    String end_YY = "";
    String end_mm = "";
    String end_dd = "";
    String calDate = "";
    String oldDate = "";
    String preparedstmt = "";
    String preparedstmt2 = "";
    String userName = "";
    String name = "";
    String prep = "";
    String prep2 = "";
    String m_name  = "";
    String sname = "";
    String pro_first = "";
    String pro_last = "";
    String pro_full = "";
    String search_info = "";
    String allMem = "";
    
    boolean all_Pros = false;       //search all pros
    boolean all_Members = false;    //search all members
    boolean mem_Search = false;     //search based on user input string    
    boolean results = false;
    boolean results2 = false;

    Calendar cal = new GregorianCalendar();       // get todays date
    int year_yyyy = cal.get(Calendar.YEAR);
    int month_mm = cal.get(Calendar.MONTH) + 1;
    int day_dd = cal.get(Calendar.DAY_OF_MONTH);
    int future_date = ((year_yyyy * 10000)+10000) + (month_mm * 100) + day_dd;   // get date
    int activity_id = (Integer) session.getAttribute("activity_id");
    String templott = (String) session.getAttribute("lottery");        // get lottery support indicator
    int lottery = Integer.parseInt(templott);
    
    
    if (req.getParameter("All") != null) {        
        
        all_Members = true;
    }
    
    if (req.getParameter("allmembers") != null) {
        if (req.getParameter("allmembers").equals("false")){
            all_Members = false;

        } else {
            all_Members = true;

        } 
    }
    
    if (req.getParameter("allpros") != null) {

        all_Pros = true;
    }

    if (req.getParameter("date") != null) {

        String dates = req.getParameter("date");

        try {
            date = (int) Long.parseLong(dates);

        } catch (NumberFormatException e) {
        }
    }
    
    if (req.getParameter("jump") != null) {

        jumps = req.getParameter("jump");         //  jump index value for where to jump to on the page

        try {
            jump = Integer.parseInt(jumps);
        } catch (NumberFormatException e) {
            jump = 0;
        }
    }

    if (req.getParameter("proid") != null) {
        
        proid = req.getParameter("proid");
        
        if (proid.equals("999")) {                      //if proid 999 search all pros
            all_Pros = true;
        }
    }
    
    if (req.getParameter("lessonReport2") != null) {

        proid = req.getParameter("proid");

        try {
            id = Integer.parseInt(proid);
        } catch (NumberFormatException e) {
            id = 0;
        }
    }

    if (req.getParameter("old_date") != null) {
        
        oldDate = req.getParameter("old_date");
    }

    if (req.getParameter("username") != null) {
        
        userName = req.getParameter("username");
    }
    
    if (req.getParameter("sname") != null) {
        
        sname = req.getParameter("sname");
        if (!sname.equals("")) {
        mem_Search = true;        
        }

         
    }
    
    if (req.getParameter("allMem") != null) {
        
        allMem = req.getParameter("allMem");
        if (allMem.equalsIgnoreCase("yes")) {
            all_Members = true;
            mem_Search = false;
        }


    } 
        
    if (req.getParameter("time") != null) {

        String times = req.getParameter("time");

        try {
            time = (int) Long.parseLong(times);
        } catch (NumberFormatException e) {
        }
    }

    String excel = (req.getParameter("excel") != null) ? req.getParameter("excel") : "";
       
    if (!excel.equals("yes")) {
        
        out.println(SystemUtils.HeadTitle("Lesson Book Search"));
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
        SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
        out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");
    }
    
    try {
        if (excel.equals("yes")) {                // if user requested Excel Spreadsheet Format
            
            resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
            resp.setHeader("Content-Disposition", "attachment;filename=\"Proshop_lesson.xls\"");
        }
    } catch (Exception exc) {
    }

    if (req.getParameter("player1") != null) {
        
        try {
            pst = con.prepareStatement("SELECT memname FROM lessonbook5 where memid = ?");
            pst.clearParameters();
            pst.setString(1, userName);
            rs = pst.executeQuery();
            
            while (rs.next()) {
                m_name = rs.getString("memname");
            }
        } catch (Exception ignore) {
        } finally {
            Connect.close(rs, pst);
        }
        
        if (req.getParameter("player1").equals("")) {
            all_Members = true;
            mem_Search = false;
            allMem = "yes";

        }        
        
        if (!req.getParameter("player1").equalsIgnoreCase(m_name) && !all_Members) {
            mem_Search = true;
            name = req.getParameter("player1");
            //
            //  verify the required fields
            //
            length = name.length();                    // get length of name requested
            if ((name.equals("")) || (length > 43)) {

                invData(out);    // inform the user and return
                return;
            }
            //
            //   Add a % to the name provided so search will match anything close
            //
            sname = name;
            StringBuffer buf = new StringBuffer("%");
            buf.append(name);
            buf.append("%");
            sname = buf.toString();
        }
    }

    if (req.getParameter("cal_box_0") != null && !req.getParameter("cal_box_0").equals("")) {

        start_Date = req.getParameter("cal_box_0");
        StringTokenizer tok = new StringTokenizer(start_Date, "-");     // delimiters are hyphen
        
        if (tok.countTokens() > 0) {
            start_YY = tok.nextToken();
        }
        
        if (tok.countTokens() > 0) {
            start_mm = tok.nextToken();
        }
        
        if (tok.countTokens() > 0) {
            start_dd = tok.nextToken();
        }
        
        if (!start_mm.equalsIgnoreCase("10") && !start_mm.equalsIgnoreCase("11") && !start_mm.equalsIgnoreCase("12")) {
            start_mm = "0" + start_mm;
        }
        
        if (start_dd.equalsIgnoreCase("1") || start_dd.equalsIgnoreCase("2") || start_dd.equalsIgnoreCase("3") || start_dd.equalsIgnoreCase("4") || start_dd.equalsIgnoreCase("5")
                || start_dd.equalsIgnoreCase("6") || start_dd.equalsIgnoreCase("7") || start_dd.equalsIgnoreCase("8") || start_dd.equalsIgnoreCase("9")) {

            start_dd = "0" + start_dd;
        }
        start_Date = start_YY + start_mm + start_dd;

    } else {
        start_Date = oldDate;
    }


    if (req.getParameter("cal_box_1") != null && !req.getParameter("cal_box_1").equals("")) {

        end_Date = req.getParameter("cal_box_1");
        StringTokenizer tok2 = new StringTokenizer(end_Date, "-");     // delimiters are hyphen
        
        if (tok2.countTokens() > 0) {
            end_YY = tok2.nextToken();
        }
        
        if (tok2.countTokens() > 0) {
            end_mm = tok2.nextToken();
        }
        
        if (tok2.countTokens() > 0) {
            end_dd = tok2.nextToken();
        }
        
        if (!end_mm.equalsIgnoreCase("10") && !end_mm.equalsIgnoreCase("11") && !end_mm.equalsIgnoreCase("12")) {
            end_mm = "0" + end_mm;
        }
        
        if (end_dd.equalsIgnoreCase("1") || end_dd.equalsIgnoreCase("2") || end_dd.equalsIgnoreCase("3") || end_dd.equalsIgnoreCase("4") || end_dd.equalsIgnoreCase("5")
                || end_dd.equalsIgnoreCase("6") || end_dd.equalsIgnoreCase("7") || end_dd.equalsIgnoreCase("8") || end_dd.equalsIgnoreCase("9")) {
            
            end_dd = "0" + end_dd;
        }
        end_Date = end_YY + end_mm + end_dd;
    } else {
        end_Date = "" + future_date;
    }
    
    if (req.getParameter("startd") != null) {
        
        start_Date = req.getParameter("startd");
        
        if (req.getParameter("endd") != null) {
            end_Date = req.getParameter("endd");
        }                       
    }
    
        
    if (mem_Search) {        //if search by user imput string
        
        if (all_Pros) {
            prep = "memname like ? ORDER BY DATE, TIME";
            prep2 = "ls.memname like ? ORDER BY ls.date";
        } else {
            prep = " proid = ? AND memname like ? ORDER BY DATE, TIME";
            prep2 = "ls.proid = ? AND ls.memname like ? ORDER BY ls.date";
        }
        
        search_info = sname;
        
        if (search_info.startsWith("%")) {
            search_info = search_info.substring(1);
        }
        
        if (search_info.endsWith("%")) {
            search_info = search_info.substring(0, search_info.length() - 1);
        }
    
    } else if (all_Members) {   //if search for all members
        
        search_info = "All Members";
        
        if (all_Pros) {         //if search for all member and all pros
            prep = "memname !='' ORDER BY DATE, TIME";
            prep2 = "ls.memname!='' ORDER BY ls.date";
        } else {                //if search for all members and a specific pro
            prep = "proid=? AND memname!='' ORDER BY DATE, TIME";
            prep2 = "ls.proid=? AND ls.memname !='' ORDER BY ls.date";
        }
    } else {
        
        search_info = req.getParameter("player1");     

        if (all_Pros) { //if search for all pros and specific member
            prep = "memid=? ORDER BY DATE, TIME";
            prep2 = "ls.memid=? ORDER by ls.date";
        } else {        //if search for individual member & pro
            prep = "proid=? AND memid=? ORDER BY DATE, TIME";
            prep2 = "ls.proid=? AND ls.memid=? ORDER BY ls.date";
        }
    }    

    try{
        sDate = Integer.parseInt(start_Date); //convert start and end dates to integer form
        eDate = Integer.parseInt(end_Date);
    }catch (Exception ignore){
    
    }
        
    if (!excel.equals("yes")) {                // if not Excel Spreadsheet Format
        
        out.println("<br><table border=\"0\" align=\"center\">");
        out.println("<tr><td>");
        out.println("<input type=\"button\" value=\"Print\" onclick=\"window.print();\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp;");
        out.println("</td>");
        out.println("<td>");
        out.println("<form method=\"post\" action=\"Proshop_lesson\" name=frmRequestExcel id=frmRequestExcel target=\"_blank\">");
        out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"lessonReport2\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"proid\" value=\"" + proid + "\">");
        out.println("<input type=\"hidden\" name=\"searchinfo\" value=\"" + search_info + "\">");
        out.println("<input type=\"hidden\" name=\"allmembers\" value=\"" + all_Members + "\">");
        out.println("<input type=\"hidden\" name=\"username\" value=\"" + userName + "\">");
        out.println("<input type=\"hidden\" name=\"startd\" value=\"" + start_Date + "\">");
        out.println("<input type=\"hidden\" name=\"endd\" value=\"" + end_Date + "\">");
        out.println("<input type=\"hidden\" name=\"allMem\" value=\"" + allMem + "\">");
        out.println("<input type=\"hidden\" name=\"sname\" value=\"" + sname + "\">");
        out.println("<input type=\"submit\" value=\"Excel\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp;");
        out.println("</td>");
        out.println("</form>");
        
        out.println("<td>");
        out.println("<form action=\"Proshop_lesson\" method=\"post\">");
        out.println("<input type=\"hidden\" name=\"lessonReport\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"proid\" value=" +id+ ">");
        out.println("<input type=\"hidden\" name=\"jump\" value=" +jump+ ">");
        out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp;");
        out.println("</form>");
        out.println("</td>");
        out.println("<td>");
        out.println("<form method=\"get\" action=\"Proshop_announce\">");
        out.println("<p align=\"center\"><input type=\"submit\" value=\" Home \" style=\"background:#8B8970\"></p>");
        out.println("</form>");
        out.println("</td></tr></table>");
    }
        
    if (sDate > eDate) {    
        
        out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
        out.println("<BODY><CENTER>");
        out.println("<p>&nbsp;</p>");
        out.println("<BR><H3>Input Error</H3><BR>");
        out.println("<BR><BR>Sorry, but your start date is greater than your end date.<BR>");
        out.println("<BR>Please try again.<BR>");
        out.println("<BR><BR>");
        out.println("<font size=\"2\">");
        out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></font>");
        out.println("</CENTER></BODY></HTML>");
        out.close();
    
    } else {
                               
        out.println("<table cellpadding=\"3\" align=\"center\" bgcolor=\"#336633\">");
        out.println("<tr>");
        
        if (excel.equals("yes")) {
            out.println("<th><font color=\"#ffffff\" face=\"font-family:verdana;color:white;font-size:.9em\" size=\"2\">"+Utilities.getDateFromYYYYMMDD(sDate, 4)+" to "+Utilities.getDateFromYYYYMMDD(eDate, 4));
        } else {
            out.println("<th><font color=\"#ffffff\" face=\"font-family:verdana;color:white;font-size:.9em\" size=\"2\">Searched "+search_info+" FROM "+Utilities.getDateFromYYYYMMDD(sDate, 4)+" to "+Utilities.getDateFromYYYYMMDD(eDate, 4));
        }
        
        out.println("</font></th></tr></table><br>");
        
        out.println("<table cellpadding=\"3\" align=\"center\" bgcolor=\"#336633\">");
        out.println("<tr>");
            out.println("<th><font color=\"#ffffff\" face=\"font-family:verdana;color:white;font-size:.9em\" size=\"2\">Individual Lessons</font>");
        out.println("</th></tr></table><br>");
       
        out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\" cellpadding=\"5\">");             //start table with lesson info
        out.println("<tr bgcolor=\"#336633\" style=\"font-family:verdana;color:white;font-size:.9em\">");
        out.println("<td>Date</td><td>Day</td><td>Time</td><td>Pro Name</td>");
       
        if(!excel.equals("yes")){
           out.println("<td>Name</td>");
        } else {                                         //for excel seperate last name and first name for easy sorting
           out.println("<td>Last Name</td><td>First Name</td>");
        }
        
        out.println("<td>Memid</td><td>Lesson Name</td><td>Lesson Type</td><td>Length</td><td>Billed</td><td>Phone1</td><td>Phone2</td><td>Notes</td>");
        out.println("</tr>");
        
        preparedstmt = "SELECT proid, date, dayNum, time, memid, memname, ltype, length, billed, ltname, phone1, phone2, notes From lessonbook5 WHERE length > 0 AND DATE >= ? AND DATE <= ? AND activity_id = ? AND " + prep;
           
        try {
            
            pstmt = con.prepareStatement("" + preparedstmt);
            pstmt.clearParameters();        // clear the parms
            pstmt.setString(1, start_Date);
            pstmt.setString(2, end_Date);
            pstmt.setInt(3, activity_id);
            if (mem_Search  && all_Pros) {
                
                pstmt.setString(4, sname);                             
            
            } else if (mem_Search  && !all_Pros) {
               
                pstmt.setString(4, proid);
                pstmt.setString(5, sname);
            
            } else if (all_Members && !all_Pros) {
            
                pstmt.setString(4, proid);
            
            } else if (!all_Members && all_Pros) {
            
                pstmt.setString(4, userName);
            
            } else if (!all_Members && !all_Pros) {
            
                pstmt.setString(4, proid);
                pstmt.setString(5, userName);
            }
            
            rs = pstmt.executeQuery();

            while (rs.next()) {
                
                results = true;
                
                out.println("<tr style=\"border: 1px solid black\">");
                proid = rs.getString("proid");

                try {                //Search for pro name in lessonpro5
                    
                    pstmtz = con.prepareStatement("SELECT fname, lname FROM lessonpro5 WHERE id = ?");
                    pstmtz.clearParameters();
                    pstmtz.setString(1, proid);
                    rsz = pstmtz.executeQuery();

                    while (rsz.next()) {
                        
                        pro_first = rsz.getString("fname");
                        pro_last = rsz.getString("lname");
                        pro_full = pro_first + " " + pro_last;
                    }
                } catch (Exception exx) {
                } finally {
                    Connect.close(rsz, pstmtz);
                }

                date = rs.getInt("date");
                mem_name = rs.getString("memname");
                ltype = rs.getString("ltype");
                billed = rs.getInt("billed");
                time = rs.getInt("time");

                String temp[] = mem_name.split(" ");

                out.println("<td>" + Utilities.getDateFromYYYYMMDD(date, 4) + "</td>");
                out.println("<td>" + day_table[rs.getInt("dayNum")] + "</td>");
                out.println("<td>" + time + "</td>");
                out.println("<td>" + pro_full + "</td>");
                
                if (!excel.equals("yes")) {
                    out.println("<td>" + mem_name + "</td>");
                } else {
                    String mm_name[] = mem_name.split(" ");
                    if (!mem_name.contains(" ")) {
                        out.println("<td>" + mm_name[0] + "</td>");
                        out.println("<td>" + mm_name[0] + "</td>");
                    } else {
                        if (mm_name[1].length() <= 1) {
                            out.println("<td>" + mm_name[2] + "</td>");
                            out.println("<td>" + mm_name[0] + "</td>");
                        } else {
                            out.println("<td>" + mm_name[1] + "</td>");
                            out.println("<td>" + mm_name[0] + "</td>");
                        }
                    }
                }

                out.println("<td>" + rs.getString("memid") + "</td>");
                out.println("<td>" + rs.getString("ltname") + "</td>");
                out.println("<td>" + ltype + "</td>");
                out.println("<td>" + rs.getInt("length") + "</td>");
                out.println("<td>" + billed + "</td>");
                out.println("<td>" + rs.getString("phone1") + "</td>");
                out.println("<td>" + rs.getString("phone2") + "</td>");
                out.println("<td>" + rs.getString("notes") + "</td>");
                out.println("</tr>");

            }
            if (!results){
                out.println("<td colspan=\"13\" align=\"center\">Your search has returned no results.</td>");
            }
        } catch (Exception ignore) {
        } finally {
            Connect.close(rs, pstmt);
        }
        

    out.println("</tr></table>");       //END TABLE LESSON INFO
    
    out.println("<br><table cellpadding=\"3\" align=\"center\" bgcolor=\"#336633\">");
    out.println("<tr>");
        out.println("<th><font color=\"#ffffff\" face=\"font-family:verdana;color:white;font-size:.9em\" size=\"2\">Group Lessons</font>");
    if (!excel.equals("yes")) {
        out.println("</th></tr></table>");
    } else {
        out.println("</th></tr></table><br>");
    }
    out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\" cellpadding=\"5\">");             //start table with lesson info
    out.println("<tr bgcolor=\"#336633\" style=\"font-family:verdana;color:white;font-size:.9em\">");
    out.println("<td>Date</td><td>Pro Name</td>");
       
    if(!excel.equals("yes")){
       out.println("<td>Name</td>");
    } else {                                         //for excel seperate last name and first name for easy sorting
       out.println("<td>Last Name</td><td>First Name</td>");
    }
    
    out.println("<td>Memid</td><td>Lesson Name</td><td>Lesson Description</td><td>Billed</td><td>Phone1</td><td>Phone2</td><td>Notes</td>");
    out.println("</tr>");
    
    preparedstmt2 = "SELECT lg.lesson_id, lg.descript, ls.lesson_id, ls.proid, ls.lname, ls.memname, ls.memid, ls.billed, ls.phone1, ls.phone2, ls.notes, ls.date FROM lgrpsignup5 as ls, lessongrp5 as lg WHERE ls.date >=? AND ls.date <= ? AND lg.activity_id = ? AND ls.lesson_id = lg.lesson_id AND " + prep2;    

        try {
            
            pstmt2 = con.prepareStatement("" + preparedstmt2);
            pstmt2.clearParameters();        // clear the parms
            pstmt2.setString(1, start_Date);
            pstmt2.setString(2, end_Date);
            pstmt2.setInt(3, activity_id);
            
            if (mem_Search && all_Pros) {
                
                pstmt2.setString(4, sname);
                           
            } else if (mem_Search && !all_Pros) {
                
                pstmt2.setString(4, proid);
                pstmt2.setString(5, sname);
            
            } else if (all_Members && !all_Pros) {
                
                pstmt2.setString(4, proid);
            
            } else if (!all_Members && all_Pros) {
                
                pstmt2.setString(4, userName);
            
            } else if (!all_Members && !all_Pros) {
                
                pstmt2.setString(4, proid);
                pstmt2.setString(5, userName);
            }
            
            rs = pstmt2.executeQuery();
            
            while (rs.next()) {
                
                results2 = true;
                
                out.println("<tr style=\"border: 1px solid black\">");
                
                date = rs.getInt("ls.date");
                mem_name = rs.getString("ls.memname");
                String temp[] = mem_name.split(" ");

                out.println("<td>" + Utilities.getDateFromYYYYMMDD(date, 4) + "</td>");
                out.println("<td>" + pro_full + "</td>");
                if (!excel.equals("yes")) {
                    out.println("<td>" + mem_name + "</td>");
                } else {
                    String mm_name[] = mem_name.split(" ");
                    if (mm_name[1].length() <= 1) {
                        out.println("<td>" + mm_name[2] + "</td>");
                        out.println("<td>" + mm_name[0] + "</td>");
                    } else {
                        out.println("<td>" + mm_name[1] + "</td>");
                        out.println("<td>" + mm_name[0] + "</td>");
                    }
                }

                out.println("<td>" + rs.getString("ls.memid") + "</td>");
                out.println("<td>" + rs.getString("ls.lname") + "</td>");
                out.println("<td>" + rs.getString("lg.descript") + "</td>");
                out.println("<td>" + rs.getString("ls.billed") + "</td>");
                out.println("<td>" + rs.getString("ls.phone1") + "</td>");
                out.println("<td>" + rs.getString("ls.phone2") + "</td>");
                out.println("<td>" + rs.getString("ls.notes") + "</td>");
                out.println("</tr>");

            }
            
            if (!results2) {
                out.println("<td colspan=\"13\" align=\"center\">Your search has returned no results.</td>");
            }
        
        } catch (Exception ignore) {
        } finally {
            Connect.close(rs, pstmt2);
        }       
    
        if (!excel.equals("yes")) {                // if not Excel Spreadsheet Format
            
            out.println("<br><table border=\"0\" align=\"center\">");
            out.println("<tr><td>");
            out.println("<input type=\"button\" value=\"Print\" onclick=\"window.print();\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp;");
            out.println("</td>");
            out.println("<td>");
            out.println("<form method=\"post\" action=\"Proshop_lesson\" name=frmRequestExcel id=frmRequestExcel target=\"_blank\">");
            out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
            out.println("<input type=\"hidden\" name=\"lessonReport2\" value=\"yes\">");
            out.println("<input type=\"hidden\" name=\"proid\" value=\"" + proid + "\">");
            out.println("<input type=\"hidden\" name=\"allmembers\" value=\"" + all_Members + "\">");
            out.println("<input type=\"hidden\" name=\"username\" value=\"" + userName + "\">");
            out.println("<input type=\"hidden\" name=\"startd\" value=\"" + start_Date + "\">");
            out.println("<input type=\"hidden\" name=\"endd\" value=\"" + end_Date + "\">");
            out.println("<input type=\"hidden\" name=\"sname\" value=\"" + sname + "\">");
            out.println("<input type=\"hidden\" name=\"player1\">");
            out.println("<input type=\"submit\" value=\"Excel\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp;");
            out.println("</td>");
            out.println("</form>");
            
            out.println("<td>");
            out.println("<form action=\"Proshop_lesson\" method=\"post\">");
            out.println("<input type=\"hidden\" name=\"lessonReport\" value=\"yes\">");
            out.println("<input type=\"hidden\" name=\"proid\" value=" + id + ">");
            out.println("<input type=\"hidden\" name=\"jump\" value=" + jump + ">");
            out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp;");//return to search
            out.println("</form>");
            out.println("</td>");
            
            out.println("<td>");
            out.println("<form method=\"get\" action=\"Proshop_announce\">");
            out.println("<p align=\"center\"><input type=\"submit\" value=\" Home \" style=\"background:#8B8970\"></p>");//return to announcement page
            out.println("</form>");
            out.println("</td></tr></table><br>");
        }
    
        if (!excel.equals("yes")) {
            out.println("</body>");
            out.println("</html>");
            out.close();
        }
    }

}
 private int getEndTime(int time, int ltlength) {


   int hr = 0;
   int min = 0;
     
   //
   //  add the length field to the time field
   //
   hr = time / 100;            // get hr
   min = time - (hr * 100);    // get minute

   min += ltlength;            // add length to minutes
     
   while (min > 59 && hr < 23) {     // if exceeded next hour
     
      hr++;                    // get next hour 
      min -= 60;               // adjust minute
   }

   time = (hr * 100) + min;    // get new time

   return(time);
     
 }   // end of getEndTime


 // *********************************************************
 //  determine # of minutes between two time values
 // *********************************************************

 private int getMinTime(int time1, int time2) {


   int hr1 = 0;
   int hr2 = 0;
   int min1 = 0;
   int min2 = 0;
   int mins = 0;

   //
   //  Calculate the number of minutes between time1 and time2
   //
   hr1 = time1 / 100;             // get hr
   min1 = time1 - (hr1 * 100);    // get minute

   hr2 = time2 / 100;             // get hr
   min2 = time2 - (hr2 * 100);    // get minute

   hr1 = hr2 - hr1;            
     
   if (hr1 > 0) {
     
      mins = hr1 * 60;               // get minutes
   }
   
   if (min2 > min1) {        

      mins = mins + (min2 - min1);   // get total minutes
        
   } else {

      min1 = min1 - min2;

      mins = mins - min1;           
   }

   return(mins);

 }   // end of getEndTime


 // ******************************************************************************
 //  Get a member's mNum
 // ******************************************************************************

 private String getmNum(String user, Connection con) {


   ResultSet rs = null;

   String mnum = "";


   try {

      if (!user.equals( "" )) {

         PreparedStatement pstmte1 = con.prepareStatement (
                  "SELECT memNum FROM member2b WHERE username = ?");

         pstmte1.clearParameters();        // clear the parms
         pstmte1.setString(1, user);
         rs = pstmte1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            mnum = rs.getString(1);        // user's mNum
         }

         pstmte1.close();                  // close the stmt
      }

   }
   catch (Exception ignore) {
   }

   return(mnum);
 }                   // end of getmNum


 // ******************************************************************************
 //  Get a member's tFlag from member2b
 // ******************************************************************************

 private String gettFlag(String user, Connection con) {


   ResultSet rs = null;

   String tFlag = "";


   try {

      if (!user.equals( "" )) {

         PreparedStatement pstmte1 = con.prepareStatement (
                  "SELECT tflag FROM member2b WHERE username = ?");

         pstmte1.clearParameters();        // clear the parms
         pstmte1.setString(1, user);
         rs = pstmte1.executeQuery();     

         if (rs.next()) {

            tFlag = rs.getString(1);        // user's tflag
         }

         pstmte1.close();                  // close the stmt
      }

   }
   catch (Exception ignore) {
   }

   return(tFlag);
 }                   // end of gettFlag


 // *********************************************************
 // name already exists
 // *********************************************************

 private void invData(HttpServletRequest req, PrintWriter out, int lottery, String error) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY>");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>" +error+ "<BR>");
   out.println("<BR>Please complete the form and try again.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 // *********************************************************
 // name already exists
 // *********************************************************

 private void dupMem(HttpServletRequest req, PrintWriter out, int lottery) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY>");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, the <b>name</b> you specified already exists in the database.<BR>");
   out.println("<BR>Please use the edit feature to change an existing record.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 // *********************************************************
 // Record Not Found Error
 // *********************************************************

 private void nfError(HttpServletRequest req, PrintWriter out, int lottery) {

   out.println(SystemUtils.HeadTitle("Database Error"));
   out.println("<BODY>");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<CENTER><BR>");
   out.println("<BR><BR><H3>Record Not Found Error</H3>");
   out.println("<BR><BR>Sorry, we are unable to locate the database record at this time.");
   out.println("<BR>Please try again later.");
   out.println("<BR><BR>If problem persists, contact customer support.");
   out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 // *********************************************************
 // Database Error
 // *********************************************************

 private void dbError(HttpServletRequest req, PrintWriter out, Exception exc, int lottery) {

   out.println(SystemUtils.HeadTitle("Database Error"));
   out.println("<BODY>");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<CENTER><BR>");
   out.println("<BR><BR><H3>Database Access Error</H3>");
   out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
   out.println("<BR>Please try again later.");
   out.println("<BR><BR>If problem persists, contact customer support.");
   out.println("<BR><BR>" + exc.getMessage());
   out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }
 // *********************************************************
 // Missing or invalid data entered...
 // *********************************************************

 private void invData(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, some data you entered is missing or invalid.<BR>");
   out.println("<BR><BR>You can search for name or guest type up to 43 characters in length.<BR>");
   out.println("<BR>Please try again.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }

}
