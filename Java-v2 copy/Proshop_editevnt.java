/***************************************************************************************
 *   Proshop_editevnt:  This servlet will process the 'Edit Event' request from
 *                      the Proshop's Edit Events page.
 *
 *
 *   called by:  Proshop_events (via doPost in HTML built by Proshop_events)
 *
 *   created: 12/18/2001   Bob P.
 *
 *   last updated:
 *
 *        1/08/14   Add Golf Genius export type.
 *        9/27/13   User will now be returned to the 'Show Previous' page if they entered an event configuration from there.
 *        6/05/13   Add Event POS charge fields for TAI (Bald Peak to start with).
 *        1/06/13   Add TinyMCE support to the edit event form, cleaned up some html for more consistant rendering
 *       11/08/12   Add option (memedit) to allow members to edit event times after moved to tee sheet. 
 *        2/06/12   For season long events, if the year of the cut-off date has been changed to the next calendar year, inactivate any old signups, like we do based on the event date year for reg events.
 *       12/20/11   Added support for selecting event categories when adding/editing events (case 2076).
 *        9/28/11   Added sanity checking to mempos and gstpos values when adding an event to prevent these from being set to NULL for FlxRez events.
 *        9/27/11   Removed suggestion of 0-48 hrs for xhr settings since no limit is actually forced in the code.
 *       11/01/10   Fixed issue with players not getting moved from the wait list when the # of teams increased.
 *       10/20/10   Populate new parmEmail fields
 *       10/19/10   When the date on an event is changed, though the year stays the same, reset the 'moved' field on all active signups for that event
 *        1/19/10   Set max=0 when editing an event that is not a signup type.  This is in case the event was copied from a signup event.
 *       12/08/09   Do not delete events or event signups - mark them inactive instead so we can easily restore them.
 *       11/05/09   Fixed bug where trimmed event name was being passed to the delete method which caused the event to not be removed from tee sheets
 *       11/08/09   Cleaned up removeEvent method - now uses event_id 
 *       10/12/09   Changes to prevent doubles spaces in event names
 *       10/06/09   Fixed for loops for # of Xs allowed to run from 0-4 instead of 1-5
 *        9/28/09   Added support for Activities
 *        6/25/09   Add Tournament Expert (TourEx) option to export types
 *        5/01/09   Allow season to arrive null (the select for it may be disabled)
 *       11/10/08   Added changes for additional signup information
 *        9/02/08   Javascript compatability updates
 *        8/15/08   Add more years to the start and end dates until we come up with a better method.
 *        8/12/08   Added limited access proshop users checks
 *        7/10/08   Remove holes from update statement in addEvent to prevent problem with holes getting cleared on Copy Event.
 *        5/05/08   Remove Event man option until ready for implementation.
 *        4/07/08   Fixes for season long events
 *        3/24/08   Add gender, season, email1/2, export_type to event config
 *        2/27/08   Removed message of 254 char restriction on Itinerary - removed actual limitation last year
 *        9/25/07   Add minimum sign-up size to configurable options
 *        6/26/07   Only update the required fields when signUp = No in case pro wants to temporarily change it.
 *        6/22/06   Remove POS charge codes until the POS interfaces are added for events.
 *        3/29/06   Do not specify the course name when accessing evntsup2b so the matching entries will be found (in case ALL).
 *        5/10/05   Add mtype and mship restrictions to the events.
 *        1/24/05   Ver 5 - change club2 to club5.
 *        1/08/05   Add 'copy event' processing to allow pro to copy both pages of another event.
 *        5/12/04   Add sheet= parm for call from Proshop_sheet.
 *        5/05/04   Add 2nd set of block times for events.
 *        2/17/04   Version 4 - add processing for POS Charges and Dynamic Modes of Trans.
 *        7/18/03   Enhancements for Version 3 of the software.
 *        2/10/03   Add signUp processing for members to sing up online.
 *        1/14/03   Add signUp option for members to sing up online.
 *        1/08/03   Enhancements for Version 2 of the software.
 *                  Add multiple course support.
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
import com.foretees.common.parmCourse;
import com.foretees.common.getParms;
import com.foretees.common.parmEmail;
import com.foretees.common.sendEmail;
import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.common.Labels;
import com.foretees.common.getActivity;
import com.foretees.common.parmActivity;
import com.foretees.common.Utilities;

import org.apache.commons.lang.*;


public class Proshop_editevnt extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 //*****************************************************
 // doGet - go to doPost
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   doPost(req, resp);      // call doPost processing

 }

 //****************************************************************
 // Process the form request from Proshop_editevnt page.
 //****************************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();


   HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   //
   // Process request according to which 'submit' button was selected
   //
   //      Continue - check if 2nd page of options should be output, else go to updateConf
   //      page2 - all options selected - verify and prompt user for conf
   //      Remove - delete requested - verify and prompt user for conf
   //      Yes (value = update) - process the update confirmation
   //      Delete (value = remove) - process the delete confirmation
   //
   if (req.getParameter("page2") != null) {

      updateConf(req, out, session);

   } else {

      if (req.getParameter("Continue") != null) {
          
         // updating event from Proshop_events - Continue is from the submit button 
         update1(req, out, session);

      } else {

         if (req.getParameter("Remove") != null) {

            removeConf(req, out);

         } else {

            if (req.getParameter("Delete") != null) {

               removeEvent(req, out, session);

            } else {

               if (req.getParameter("addevent") != null) {

                  addEvent(req, out, session);

               } else {

                  if (req.getParameter("Yes") != null) {

                     updateEvent(req, out, session, resp);

                  }
               }
            }
         }
      }
   }
 }   // end of doPost


   //*************************************************
   //  update1 - Check if 2nd page of options required
   //            for Member Signup
   //*************************************************

 private void update1(HttpServletRequest req, PrintWriter out, HttpSession sess) {


   String signUp = req.getParameter("signUp");

   if (signUp.equals( "No" )) {

      updateConf(req, out, sess);         // go process the event now

   } else {

      PreparedStatement pstmt1 = null;
      Statement stmtc = null;
      ResultSet rs = null;
        
      Connection con = SystemUtils.getCon(sess);            // get DB connection

      if (con == null) {

         out.println(SystemUtils.HeadTitle("DB Connection Error"));
         out.println("<BODY><CENTER><BR>");
         out.println("<BR><BR><H3>Database Connection Error</H3>");
         out.println("<BR><BR>Unable to connect to the Database.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR>");
         out.println("<a href=\"Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      // Check Feature Access Rights for current proshop user
      if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_EVENT", con, out)) {
          SystemUtils.restrictProshop("SYSCONFIG_EVENT", out);
      }

      int sess_activity_id = (Integer)sess.getAttribute("activity_id");

      String format = "";
      String pairings = "";
      //String ssize = "";
      //String sminsize = "";
      //String smax = "";
      //String sguests = "";
      String memcost = "";
      String gstcost = "";
      //String ssu_month = "";
      //String ssu_day = "";
      //String ssu_year = "";
      //String ssu_hr = "";
      //String ssu_min = "";
      String su_ampm = "";
      //String sc_month = "";
      //String sc_day = "";
      //String sc_year = "";
      //String sc_hr = "";
      //String sc_min = "";
      String c_ampm = "";
      String posType = "";
      //String sx = "";
      //String sxhrs = "";
      String mempos = "";
      String gstpos = "";

      String [] tmode = new String [16];   // Modes of trans

      int [] tmodei = new int [16];   // Modes of trans indicators

      int i = 0;
      int i2 = 0;
      int s_min = 0;
      int e_min = 0;
      int s_min2 = 0;
      int e_min2 = 0;
      int a_min = 0;
      int month = 0;
      int day = 0;
      int year = 0;
      int size = 0;
      int minsize = 0;
      int max = 0;
      int guests = 0;
      int x = 0;
      int xhrs = 0;
      int c_month = 0;
      int c_day = 0;
      int c_year = 0;
      int c_hr = 0;
      int c_min = 0;
      int c_time = 0;
      int su_month = 0;
      int su_day = 0;
      int su_year = 0;
      int su_hr = 0;
      int su_min = 0;
      int su_time = 0;
      //int itinL = 0;
      int export_type = 0;
      int season = 0;
      long event_date = 0;
      long c_date = 0;
      long su_date = 0;
      int activity_id = 0;

      String copyname = "";
      String name = "";
      String oldName = "";
      String s_month = "";
      String s_day = "";
      String s_year = "";
      String shr = "";
      String smin = "";
      String sampm = "";
      String ehr = "";
      String emin = "";
      String eampm = "";
      String ahr = "";
      String amin = "";
      String aampm = "";
      String type = "";
      String holes = "";
      String color = "";
      String course = "";
      String oldCourse = "";
      String oldDate = "";
      String oldMax = "";
      String gstOnly = "";
      String gender = "";
      String fb = "";
      String itin = "";
      String shr2 = "";
      String smin2 = "";
      String sampm2 = "";
      String ehr2 = "";
      String emin2 = "";
      String eampm2 = "";
      String fb2 = "";
      String sheet = "";
      String sseason = "";
      String sexport_type = "";
      String email1 = "";
      String email2 = "";
      String memedit = "";

      int ask_hdcp = 0;
      int ask_homeclub = 0;
      int ask_gender = 0;
      int ask_phone = 0;
      int ask_email = 0;
      int ask_address = 0;
      int ask_shirtsize = 0;
      int ask_shoesize = 0;
      int ask_otherQ1 = 0;
      int ask_otherQ2 = 0;
      int ask_otherQ3 = 0;

      int req_guestname = 0;
      int req_hdcp = 0;
      int req_homeclub = 0;
      int req_gender = 0;
      int req_phone = 0;
      int req_email = 0;
      int req_address = 0;
      int req_shirtsize = 0;
      int req_shoesize = 0;
      int req_otherQ1 = 0;
      int req_otherQ2 = 0;
      int req_otherQ3 = 0;
 
      String otherQ1 = "";
      String otherQ2 = "";
      String otherQ3 = "";

      // sanity check some of the options
      if (otherQ1 == null) otherQ1 = "";
      if (otherQ2 == null) otherQ2 = "";
      if (otherQ3 == null) otherQ3 = "";
   
      String sactivity_id = "";
      String locations_csv = "";
      String slocation_count = "";
      int location_count = 0;
      
      boolean showOld = false;
      
      ArrayList<Integer> category_ids = new ArrayList<Integer>();  // ArrayList to hold all category_ids for this event
   
      //
      //  parm block to hold the club parameters
      //
      parmClub parm = new parmClub(sess_activity_id, con);

      String [] mship = new String [parm.MAX_Mships+1];        // Mship Types
      String [] mtype = new String [parm.MAX_Mems+1];          // Mem Types


      //
      //  If we came here from Proshop_addevnt for a copy 2nd page - go directly to 2nd page
      //
      if (req.getParameter("copyname") != null) {

         copyname = req.getParameter("copyname");        // name of event being copied
         name = req.getParameter("name");                // name of new event being added
         course = req.getParameter("course");
         oldName = copyname;

         sseason = req.getParameter("season");
         if (sseason != null && sseason.equals("1")) season = 1;
         
         try {
            //
            //  Get the date for this new event
            //
            pstmt1 = con.prepareStatement (
                     "SELECT date FROM events2b WHERE name = ?");

            pstmt1.clearParameters();
            pstmt1.setString(1, name);
            rs = pstmt1.executeQuery();

            if (rs.next()) {

               //
               //  Found the event record - get it
               //
               event_date = rs.getLong("date");
            }

            pstmt1.close();              // close the stmt

         }
         catch (Exception e) {
         }

      } else {

         //
         //  User wants to allow members to signup - prompt for member options now
         //
         name = req.getParameter("event_name").trim();
         oldName = req.getParameter("oldName");
         s_month = req.getParameter("month");
         s_day = req.getParameter("day");
         s_year = req.getParameter("year");
         shr = req.getParameter("start_hr");
         smin = req.getParameter("start_min");
         sampm = req.getParameter("start_ampm");
         ehr = req.getParameter("end_hr");
         emin = req.getParameter("end_min");
         eampm = req.getParameter("end_ampm");
         ahr = req.getParameter("act_hr");
         amin = req.getParameter("act_min");
         aampm = req.getParameter("act_ampm");
         type = req.getParameter("type");
         holes = req.getParameter("holes");
         color = req.getParameter("color");
         course = req.getParameter("course");
         oldCourse = req.getParameter("oldCourse");
         oldDate = req.getParameter("oldDate");
         oldMax = req.getParameter("oldMax");
         gstOnly = req.getParameter("gstOnly");
         gender = req.getParameter("gender");
         itin = req.getParameter("itin").trim();
         shr2 = req.getParameter("start_hr2");
         smin2 = req.getParameter("start_min2");
         sampm2 = req.getParameter("start_ampm2");
         ehr2 = req.getParameter("end_hr2");
         emin2 = req.getParameter("end_min2");
         eampm2 = req.getParameter("end_ampm2");
         fb2 = req.getParameter("fb2");
         sheet = req.getParameter("sheet");
         sexport_type = req.getParameter("export_type");
         email1 = req.getParameter("email1").trim();
         email2 = req.getParameter("email2").trim();
         slocation_count = req.getParameter("location_count");

         memedit = req.getParameter("memedit");    // can member edit event times once on tee sheet ?
   
         fb = (req.getParameter("fb") != null) ? req.getParameter("fb") : "";
         season = (req.getParameter("season") != null && req.getParameter("season").equals("1")) ? 1 : 0;

         ask_hdcp = (req.getParameter("ask_hdcp") != null && req.getParameter("ask_hdcp").equals("1")) ? 1 : 0;
         ask_homeclub = (req.getParameter("ask_homeclub") != null && req.getParameter("ask_homeclub").equals("1")) ? 1 : 0;
         ask_gender = (req.getParameter("ask_gender") != null && req.getParameter("ask_gender").equals("1")) ? 1 : 0;
         ask_phone = (req.getParameter("ask_phone") != null && req.getParameter("ask_phone").equals("1")) ? 1 : 0;
         ask_email = (req.getParameter("ask_email") != null && req.getParameter("ask_email").equals("1")) ? 1 : 0;
         ask_address = (req.getParameter("ask_address") != null && req.getParameter("ask_address").equals("1")) ? 1 : 0;
         ask_shirtsize = (req.getParameter("ask_shirtsize") != null && req.getParameter("ask_shirtsize").equals("1")) ? 1 : 0;
         ask_shoesize = (req.getParameter("ask_shoesize") != null && req.getParameter("ask_shoesize").equals("1")) ? 1 : 0;
         ask_otherQ1 = (req.getParameter("ask_otherQ1") != null && req.getParameter("ask_otherQ1").equals("1")) ? 1 : 0;
         ask_otherQ2 = (req.getParameter("ask_otherQ2") != null && req.getParameter("ask_otherQ2").equals("1")) ? 1 : 0;
         ask_otherQ3 = (req.getParameter("ask_otherQ3") != null && req.getParameter("ask_otherQ3").equals("1")) ? 1 : 0;

         req_guestname = (req.getParameter("req_guestname") != null && req.getParameter("req_guestname").equals("1")) ? 1 : 0;
         req_hdcp = (req.getParameter("req_hdcp") != null && req.getParameter("req_hdcp").equals("1")) ? 1 : 0;
         req_homeclub = (req.getParameter("req_homeclub") != null && req.getParameter("req_homeclub").equals("1")) ? 1 : 0;
         req_gender = (req.getParameter("req_gender") != null && req.getParameter("req_gender").equals("1")) ? 1 : 0;
         req_phone = (req.getParameter("req_phone") != null && req.getParameter("req_phone").equals("1")) ? 1 : 0;
         req_email = (req.getParameter("req_email") != null && req.getParameter("req_email").equals("1")) ? 1 : 0;
         req_address = (req.getParameter("req_address") != null && req.getParameter("req_address").equals("1")) ? 1 : 0;
         req_shirtsize = (req.getParameter("req_shirtsize") != null && req.getParameter("req_shirtsize").equals("1")) ? 1 : 0;
         req_shoesize = (req.getParameter("req_shoesize") != null && req.getParameter("req_shoesize").equals("1")) ? 1 : 0;
         req_otherQ1 = (req.getParameter("req_otherQ1") != null && req.getParameter("req_otherQ1").equals("1")) ? 1 : 0;
         req_otherQ2 = (req.getParameter("req_otherQ2") != null && req.getParameter("req_otherQ2").equals("1")) ? 1 : 0;
         req_otherQ3 = (req.getParameter("req_otherQ3") != null && req.getParameter("req_otherQ3").equals("1")) ? 1 : 0;

         otherQ1 = (ask_otherQ1 == 0) ? "" : req.getParameter("otherQ1");
         otherQ2 = (ask_otherQ2 == 0) ? "" : req.getParameter("otherQ2");
         otherQ3 = (ask_otherQ3 == 0) ? "" : req.getParameter("otherQ3");
         
         showOld = (req.getParameter("showOld") != null) ? true : false;

         // sanity check some of the options
         if (otherQ1 == null) otherQ1 = "";
         if (otherQ2 == null) otherQ2 = "";
         if (otherQ3 == null) otherQ3 = "";
        
         sactivity_id = (req.getParameter("activity_id") == null) ? "0" : req.getParameter("activity_id");
         
         category_ids = Utilities.buildEventCategoryListFromReq(req, sess_activity_id, con);
   
         //
         // Convert the numeric string parameters to Int's
         //
         try {
            month = Integer.parseInt(s_month);
            day = Integer.parseInt(s_day);
            year = Integer.parseInt(s_year);
            s_min = Integer.parseInt(smin);
            e_min = Integer.parseInt(emin);
            a_min = Integer.parseInt(amin);
            s_min2 = Integer.parseInt(smin2);
            e_min2 = Integer.parseInt(emin2);
            export_type = Integer.parseInt(sexport_type);
            activity_id = Integer.parseInt(sactivity_id);
            location_count = Integer.parseInt(slocation_count);
         }
         catch (NumberFormatException e) {
            // ignore error - let verify catch it
         }
         

         if (activity_id != 0) {
            out.println("<!-- activity_id=" + activity_id + " -->"); 
           //
           // Get all the checked activities (courts or locations) from the form
           // this is where the event will take place
           //
           ArrayList<Integer> locations = new ArrayList<Integer>();
           try {

               for (i = 0; i <= location_count; i++) {

                   if (req.getParameter("actChkBox_" + i) != null) {

                       try {

                           out.println("<!-- i=" + req.getParameter("actChkBox_" + i) + " -->");
                           locations.add(Integer.parseInt(req.getParameter("actChkBox_" + i)));

                       } catch (Exception ignore) { }

                   }

               }

               for (i = 0; i < locations.size(); i++) {

                   locations_csv += locations.get(i) + ",";
               }

               if (!locations_csv.equals("")) {

                   locations_csv = locations_csv.substring(0, locations_csv.length() - 1);

               } else {

                   out.println(SystemUtils.HeadTitle("Data Entry Error"));
                   out.println("<BODY><CENTER>");
                   out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
                   out.println("<BR><BR>You must specify at least one location for this event.");
                   out.println("<BR>Please try again.");
                   out.println("<BR><BR>");
                   out.println("<font size=\"2\">");
                   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
                   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
                   out.println("</form></font>");
                   out.println("</CENTER></BODY></HTML>");
                   return;

               }

           } catch (Exception exc) {

               out.println("<br>locations.size()=" + locations.size());
               out.println("<br>location_count=" + location_count);
               out.println("<br>locations_csv=" + locations_csv);
               out.println("<br>Err=" + exc.toString());
               return;

           }
         }


         //
         //  adjust some values for tests
         //
         long date = year * 10000;      // create event date
         date = date + (month * 100);
         date = date + day;             // date = yyyymmdd (for comparisons)

         //
         //  Validate parms
         //
         //  Event names cannot include special chars
         //
         boolean error = SystemUtils.scanName(name);           // check for special characters

         if (error == true) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<BODY><CENTER>");
            out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
            out.println("<BR><BR>Special characters cannot be part of the Event Name.");
            out.println("<BR>You can only use the characters A-Z, a-z, 0-9 and space.");
            out.println("<BR>You entered:" + name);
            out.println("<BR>Please try again.");
            out.println("<br><br><font size=\"2\">");
            out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");
            out.println("</CENTER></BODY></HTML>");
            return;
         }

         if (gstOnly.equals( "Yes" ) && signUp.equals( "Yes" )) {

            out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
            out.println("<BODY><CENTER><BR>");
            out.println("<p>&nbsp;</p>");
            out.println("<BR><H3>Input Error</H3><BR>");
            out.println("<BR><BR>You have specified 'Guest Only' and 'Allow Members to Sign Up'.<BR>");
            out.println("You cannot specify both of these options for the same event.<BR>");
            out.println("<BR>Please try again.<BR>");
            out.println("<BR><BR>");
            out.println("<font size=\"2\">");
            out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");
            out.println("</CENTER></BODY></HTML>");
            return;
         }

         if ((e_min < 0) || (e_min > 59)) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<BODY><CENTER>");
            out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
            out.println("<BR><BR>End Minute parameter must be in the range of 00 - 59.");
            out.println("<BR>You entered:" + e_min);
            out.println("<BR>Please try again.");
            out.println("<br><br><font size=\"2\">");
            out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");
            out.println("</CENTER></BODY></HTML>");
            return;
         }

         if ((a_min < 0) || (a_min > 59)) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<BODY><CENTER>");
            out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
            out.println("<BR><BR>Actual Minute parameter must be in the range of 00 - 59.");
            out.println("<BR>You entered:" + a_min);
            out.println("<BR>Please try again.");
            out.println("<br><br><font size=\"2\">");
            out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");
            out.println("</CENTER></BODY></HTML>");
            return;
         }

         if ((s_min2 < 0) || (s_min2 > 59)) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<BODY><CENTER>");
            out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
            out.println("<BR><BR>2nd Start Minute parameter must be in the range of 00 - 59.");
            out.println("<BR>You entered:" + s_min2);
            out.println("<BR>Please try again.");
            out.println("<br><br><font size=\"2\">");
            out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");
            out.println("</CENTER></BODY></HTML>");
            return;
         }

         if ((e_min2 < 0) || (e_min2 > 59)) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<BODY><CENTER>");
            out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
            out.println("<BR><BR>2nd End Minute parameter must be in the range of 00 - 59.");
            out.println("<BR>You entered:" + e_min2);
            out.println("<BR>Please try again.");
            out.println("<br><br><font size=\"2\">");
            out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");
            out.println("</CENTER></BODY></HTML>");
            return;
         }

         try {

            if (!oldName.equals( name )) {    // if name has changed

               //
               //  Check if event already exists in database
               //
               pstmt1 = con.prepareStatement (
                       "SELECT date FROM events2b WHERE name = ?"); // AND courseName = ?

               pstmt1.clearParameters();        // clear the parms
               pstmt1.setString(1, name);       // put the parm in stmt
               //pstmt1.setString(2, course);
               rs = pstmt1.executeQuery();      // execute the prepared stmt

               if (rs.next()) {

                  dupEvent(out);    // event exists - inform the user and return
                  pstmt1.close();
                  return;
               }
               pstmt1.close();
            }

         }
         catch (Exception ignore) {
         }
           
      }          // end of IF copy

      
      //
      // Populate a parm block for this activity
      //
      parmActivity parmAct = new parmActivity();              // allocate a parm block
      parmAct.activity_id = sess_activity_id;                      // pass in the activity_id so we can determin which activity to load parms for

      // get the activity config
      try { getActivity.getParms(con, parmAct); }
      catch (Exception e1) { out.println("<BR><BR>" + e1.getMessage()); }


      try {

         //
         // Get the Mem Types and Mship Types from the club db
         //
         getClub.getParms(con, parm, sess_activity_id);        // get the club parms

         //
         //  Get the existing values for this event
         //
         pstmt1 = con.prepareStatement (
                  "SELECT * FROM events2b WHERE name = ?");

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, oldName);
         rs = pstmt1.executeQuery();      // execute the prepared pstmt1

         if (rs.next()) {

            //
            //  Found the event record - get it
            //
            //season = rs.getInt("season");
            format = rs.getString("format");
            pairings = rs.getString("pairings");
            size = rs.getInt("size");
            minsize = rs.getInt("minsize");
            max = rs.getInt("max");
            guests = rs.getInt("guests");
            memcost = rs.getString("memcost");
            gstcost = rs.getString("gstcost");
            c_month = rs.getInt("c_month");
            c_day = rs.getInt("c_day");
            c_year = rs.getInt("c_year");
            c_date = rs.getLong("c_date");
            c_time = rs.getInt("c_time");
            x = rs.getInt("x");
            xhrs = rs.getInt("xhrs");
            su_month = rs.getInt("su_month");
            su_day = rs.getInt("su_day");
            su_year = rs.getInt("su_year");
            su_date = rs.getLong("su_date");
            su_time = rs.getInt("su_time");
            mempos = rs.getString("mempos");
            gstpos = rs.getString("gstpos");
            tmodei[0] = rs.getInt("tmode1");
            tmodei[1] = rs.getInt("tmode2");
            tmodei[2] = rs.getInt("tmode3");
            tmodei[3] = rs.getInt("tmode4");
            tmodei[4] = rs.getInt("tmode5");
            tmodei[5] = rs.getInt("tmode6");
            tmodei[6] = rs.getInt("tmode7");
            tmodei[7] = rs.getInt("tmode8");
            tmodei[8] = rs.getInt("tmode9");
            tmodei[9] = rs.getInt("tmode10");
            tmodei[10] = rs.getInt("tmode11");
            tmodei[11] = rs.getInt("tmode12");
            tmodei[12] = rs.getInt("tmode13");
            tmodei[13] = rs.getInt("tmode14");
            tmodei[14] = rs.getInt("tmode15");
            tmodei[15] = rs.getInt("tmode16");
            for (i=1; i<parm.MAX_Mems+1; i++) {
               mtype[i] = rs.getString("mem" +i);
            }
            for (i=1; i<parm.MAX_Mships+1; i++) {
               mship[i] = rs.getString("mship" +i);
            }
         }

         pstmt1.close();              // close the stmt

         if (sess_activity_id == 0) {

             //
             //  Get the Modes of Trans for the course (new) specified
             //
             PreparedStatement pstmtc = null;

             if (course.equals( "-ALL-" )) {

                pstmtc = con.prepareStatement (
                   "SELECT * FROM clubparm2");        // get the first course's parms

                pstmtc.clearParameters();

             } else {

                pstmtc = con.prepareStatement (
                   "SELECT * " +
                   "FROM clubparm2 WHERE courseName = ?");

                pstmtc.clearParameters();        // clear the parms
                pstmtc.setString(1, course);
             }

             rs = pstmtc.executeQuery();      // execute the prepared stmt

             if (rs.next()) {

                tmode[0] = rs.getString("tmode1");
                tmode[1] = rs.getString("tmode2");
                tmode[2] = rs.getString("tmode3");
                tmode[3] = rs.getString("tmode4");
                tmode[4] = rs.getString("tmode5");
                tmode[5] = rs.getString("tmode6");
                tmode[6] = rs.getString("tmode7");
                tmode[7] = rs.getString("tmode8");
                tmode[8] = rs.getString("tmode9");
                tmode[9] = rs.getString("tmode10");
                tmode[10] = rs.getString("tmode11");
                tmode[11] = rs.getString("tmode12");
                tmode[12] = rs.getString("tmode13");
                tmode[13] = rs.getString("tmode14");
                tmode[14] = rs.getString("tmode15");
                tmode[15] = rs.getString("tmode16");
             }
             pstmtc.close();

         }

         //
         //  Get the POS System Type, if sepcified
         //
         stmtc = con.createStatement();        // create a statement

         rs = stmtc.executeQuery("SELECT posType " +
                                "FROM club5 WHERE clubName != ''");

         if (rs.next()) {

            posType = rs.getString(1);

         }
         stmtc.close();

      }
      catch (Exception exc) {

         dbError(out, exc);
         return;
      }

      //
      //  Calculate time values if they exist
      //
      if (c_time != 0) {

         c_hr = c_time / 100;
         c_hr = c_hr * 100;     // get rid of minute value
         c_min = c_time - c_hr;
         c_hr = c_hr / 100;     // restore hr value

         c_ampm = "AM";

         if (c_hr > 11) {

            c_ampm = "PM";         // indicate PM

            if (c_hr > 12) {

               c_hr = c_hr - 12;  // convert back to normal time
            }
         }
      }

      if (su_time != 0) {

         su_hr = su_time / 100;
         su_hr = su_hr * 100;     // get rid of minute value
         su_min = su_time - su_hr;
         su_hr = su_hr / 100;     // restore hr value

         su_ampm = "AM";

         if (su_hr > 11) {

            su_ampm = "PM";         // indicate PM

            if (su_hr > 12) {

               su_hr = su_hr - 12;  // convert back to normal time
            }
         }
      }

      //
      //  Output a prompt for the Member Signup options
      //
      out.println("<HTML>");
      out.println("<HEAD><link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">");
      out.println("<Title>Proshop Edit Events Page</Title>");
      out.println("</HEAD>");

      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" topmargin=\"0\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<table border=\"0\" align=\"center\">");
         out.println("<tr><td align=\"center\">");

         out.println("<table border=\"1\" bgcolor=\"#336633\" cellpadding=\"8\" cellspacing=\"5\">");
         out.println("<tr><td align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<b>Edit Event - Member Signup</b><br>");
         out.println("<br>Change the desired information for the event below.<br>");
         out.println("<br>Click on <b>Update</b> to submit the changes.");
         out.println("</font>");
         out.println("</td></tr></table><br>");

         out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"8\" cellspacing=\"5\">");
         if (sheet.equals( "yes" )) {
            out.println("<form action=\"Proshop_editevnt\" method=\"post\" name=\"eventform\">");
         } else {
            out.println("<form action=\"Proshop_editevnt\" method=\"post\" target=\"bot\" name=\"eventform\">");
         }

         out.println("<tr>");
         out.println("<td width=\"600\">");
            out.println("<font size=\"2\">");
                 out.println("<b>Complete the following ONLY if you selected Yes for members to sign up online:</b>");
                 out.println("<br> (This information will be used to define the event for member sign up.)");
                 out.println("<br><br>");
               out.println("Event Format:&nbsp;&nbsp;");
                  out.println("<input type=\"text\" name=\"format\" value=\"" + format + "\" size=\"40\" maxlength=\"60\">");
                  out.println("");
               out.println("<br><br>");
                 out.println("Teams Selected By:&nbsp;&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"pairings\">");
                 if (pairings.equals( "ProShop" )) {
                   out.println("<option selected value=\"ProShop\">" + ((sess_activity_id == 0) ? "Golf Shop" : "Staff") + "</option>");
                 } else {
                   out.println("<option value=\"ProShop\">" + ((sess_activity_id == 0) ? "Golf Shop" : "Staff") + "</option>");
                 }
                 if (pairings.equals( "Member" )) {
                   out.println("<option selected value=\"Member\">Member</option>");
                 } else {
                   out.println("<option value=\"Member\">Member</option>");
                 }
                 out.println("</select>");
                 out.println("<br><br>");
                 out.println("Team Size (if Member Selects):&nbsp;&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"size\">");
                 if (sess_activity_id == 0) {
                     for (i = 1; i <= 5; i++) {
                         Common_Config.buildOption(i, i, size, out);
                     }
                 } else {
                     for (i = 1; i <= parmAct.max_players; i++) {
                         Common_Config.buildOption(i, i, size, out);
                     }
                 }
                 out.println("</select>");
/*
                 if (size == 1) {
                   out.println("<option selected value=\"1\">1</option>");
                 } else {
                   out.println("<option value=\"1\">1</option>");
                 }
                 if (size == 2) {
                   out.println("<option selected value=\"2\">2</option>");
                 } else {
                   out.println("<option value=\"2\">2</option>");
                 }
                 if (size == 3) {
                   out.println("<option selected value=\"3\">3</option>");
                 } else {
                   out.println("<option value=\"3\">3</option>");
                 }
                 if (size == 4) {
                   out.println("<option selected value=\"4\">4</option>");
                 } else {
                   out.println("<option value=\"4\">4</option>");
                 }
                 if (size == 5) {
                   out.println("<option selected value=\"5\">5</option>");
                 } else {
                   out.println("<option value=\"5\">5</option>");
                 }
*/ 
                 out.println("&nbsp;&nbsp;(1 if " + ((sess_activity_id == 0) ? "Golf Shop" : "Staff") + " Selects)");
                 out.println("<br><br>");
                 out.println("Min. Sign-up Size (if Member Selects):&nbsp;&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"minsize\">");
                 if (sess_activity_id == 0) {
                     for (i = 1; i <= 5; i++) {
                         if (i==1) {
                            Common_Config.buildOption(i, "No Min", minsize, out);
                         } else {
                            Common_Config.buildOption(i, i, minsize, out);
                         }
                     }
                 } else {
                     for (i = 1; i <= parmAct.max_players; i++) {
                         Common_Config.buildOption(i, i, minsize, out);
                     }
                 }
                 out.println("</select>");
/*
                 if (minsize < 2) {
                   out.println("<option selected value=\"1\">No Min</option>");
                 } else {
                   out.println("<option value=\"1\">No Min</option>");
                 }
                 if (minsize == 2) {
                   out.println("<option selected value=\"2\">2</option>");
                 } else {
                   out.println("<option value=\"2\">2</option>");
                 }
                 if (minsize == 3) {
                   out.println("<option selected value=\"3\">3</option>");
                 } else {
                   out.println("<option value=\"3\">3</option>");
                 }
                 if (minsize == 4) {
                   out.println("<option selected value=\"4\">4</option>");
                 } else {
                   out.println("<option value=\"4\">4</option>");
                 }
                 if (minsize == 5) {
                   out.println("<option selected value=\"5\">5</option>");
                 } else {
                   out.println("<option value=\"5\">5</option>");
                 }
*/
                 out.println("&nbsp;&nbsp;(must not be greater than team size)");
                 out.println("<br><br>");
               out.println("Max # of Teams:&nbsp;&nbsp;&nbsp;");
                  out.println("<input type=\"text\" name=\"max\" value=\"" + max + "\" size=\"3\" maxlength=\"3\">");
                  out.println("<br> (If " + ((sess_activity_id == 0) ? "Golf Shop" : "Staff") + " Selects Teams, this will be # of members allowed to sign up.)");
                  out.println("");
                 out.println("<br><br>");
                 out.println("Guests Allowed per Member (if Member Selects Teams):&nbsp;&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"guests\">");
                 if (sess_activity_id == 0) {
                     for (i = 0; i <= 4; i++) {
                         Common_Config.buildOption(i, i, guests, out);
                     }
                 } else {
                     for (i = 0; i <= parmAct.max_players - 1; i++) {
                         Common_Config.buildOption(i, i, guests, out);
                     }
                 }
                 out.println("</select>");
/*
                 if (guests == 0) {
                   out.println("<option selected value=\"0\">0</option>");
                 } else {
                   out.println("<option value=\"0\">0</option>");
                 }
                 if (guests == 1) {
                   out.println("<option selected value=\"1\">1</option>");
                 } else {
                   out.println("<option value=\"1\">1</option>");
                 }
                 if (guests == 2) {
                   out.println("<option selected value=\"2\">2</option>");
                 } else {
                   out.println("<option value=\"2\">2</option>");
                 }
                 if (guests == 3) {
                   out.println("<option selected value=\"3\">3</option>");
                 } else {
                   out.println("<option value=\"3\">3</option>");
                 }
                 if (guests == 4) {
                   out.println("<option selected value=\"4\">4</option>");
                 } else {
                   out.println("<option value=\"4\">4</option>");
                 }
*/
              out.println("<br><br>");
                out.println("Allow members to reserve player positions using an <b>'X'</b>?<br>");
                  out.println("If yes, how many X's can members specify per team? (0 [zero] = NO): &nbsp;&nbsp;");
                  out.println("<select size=\"1\" name=\"x\">");
                 if (sess_activity_id == 0) {
                     for (i = 0; i <= 4; i++) {
                         Common_Config.buildOption(i, i, x, out);
                     }
                 } else {
                     for (i = 0; i <= parmAct.max_players - 1; i++) {
                         Common_Config.buildOption(i, i, x, out);
                     }
                 }
                 out.println("</select>");
/*
                  if (x == 0) {
                    out.println("<option selected value=\"0\">0</option>");
                 } else {
                    out.println("<option value=\"0\">0</option>");
                 }
                  if (x == 1) {
                    out.println("<option selected value=\"1\">1</option>");
                 } else {
                    out.println("<option value=\"1\">1</option>");
                 }
                  if (x == 2) {
                    out.println("<option selected value=\"2\">2</option>");
                 } else {
                    out.println("<option value=\"2\">2</option>");
                 }
                  if (x == 3) {
                    out.println("<option selected value=\"3\">3</option>");
                 } else {
                    out.println("<option value=\"3\">3</option>");
                 }
                  if (x == 4) {
                    out.println("<option selected value=\"4\">4</option>");
                 } else {
                    out.println("<option value=\"4\">4</option>");
                 }
*/
              out.println("<br>");
                out.println("How many hours in advance of cut-off date/time should we remove X's?: &nbsp;&nbsp;");
                  out.println("<input type=\"text\" name=\"xhrs\" value=\"" + xhrs + "\" size=\"2\" maxlength=\"2\">");

              if (sess_activity_id == 0) {
                out.println("<br><br>");
                out.println("Player Transportation Options allowed (select all that apply):");
                i2 = 1;
                for (i=0; i<16; i++) {
                   if (!tmode[i].equals( "" ) && !tmode[i].equals( null )) {
                      out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                      if (tmodei[i] == 1) {   // if already selected
                         out.println("<input type=\"checkbox\" checked name=\"tmode" +i2+ "\" value=\"yes\">&nbsp;&nbsp;" +tmode[i]+ "");
                      } else {
                         out.println("<input type=\"checkbox\" name=\"tmode" +i2+ "\" value=\"yes\">&nbsp;&nbsp;" +tmode[i]+ "");
                      }
                   }
                   i2++;
                }
              }
              
              out.println("<br><br>");
               out.println("Cost:&nbsp;&nbsp;per Member&nbsp;");
                  out.println("<input type=\"text\" name=\"memcost\" value=\"" + memcost + "\" size=\"10\" maxlength=\"10\">");

                     if (posType.equals("TAI Club Management" ) && sess_activity_id == 0) {   // if POS charges needed
                        out.println("&nbsp;&nbsp;&nbsp;&nbsp;POS Charge Code&nbsp;");
                        out.println("<input type=\"text\" name=\"mempos\" value=\"" + mempos + "\" size=\"15\" maxlength=\"30\">");
                     }

                  out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                  out.println("&nbsp;&nbsp;per Guest&nbsp;");
                     out.println("<input type=\"text\" name=\"gstcost\" value=\"" + gstcost + "\" size=\"10\" maxlength=\"10\">");

                     if (posType.equals("TAI Club Management" ) && sess_activity_id == 0) {   // if POS charges needed
                        out.println("&nbsp;&nbsp;&nbsp;&nbsp;POS Charge Code&nbsp;");
                        out.println("<input type=\"text\" name=\"gstpos\" value=\"" + gstpos + "\" size=\"15\" maxlength=\"30\">");
                     }

  
                 out.println("<br><br>");
               out.println("Sign-up Date and Time (when members can begin to sign up):<br><br>");
                 out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Month:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"su_month\">");
                      if (su_month == 1) {
                         out.println("<option selected value=\"01\">JAN</option>");
                      } else {
                         out.println("<option value=\"01\">JAN</option>");
                      }
                      if (su_month == 2) {
                         out.println("<option selected value=\"02\">FEB</option>");
                      } else {
                         out.println("<option value=\"02\">FEB</option>");
                      }
                      if (su_month == 3) {
                         out.println("<option selected value=\"03\">MAR</option>");
                      } else {
                         out.println("<option value=\"03\">MAR</option>");
                      }
                      if (su_month == 4) {
                         out.println("<option selected value=\"04\">APR</option>");
                      } else {
                         out.println("<option value=\"04\">APR</option>");
                      }
                      if (su_month == 5) {
                         out.println("<option selected value=\"05\">MAY</option>");
                      } else {
                         out.println("<option value=\"05\">MAY</option>");
                      }
                      if (su_month == 6) {
                         out.println("<option selected value=\"06\">JUN</option>");
                      } else {
                         out.println("<option value=\"06\">JUN</option>");
                      }
                      if (su_month == 7) {
                         out.println("<option selected value=\"07\">JUL</option>");
                      } else {
                         out.println("<option value=\"07\">JUL</option>");
                      }
                      if (su_month == 8) {
                         out.println("<option selected value=\"08\">AUG</option>");
                      } else {
                         out.println("<option value=\"08\">AUG</option>");
                      }
                      if (su_month == 9) {
                         out.println("<option selected value=\"09\">SEP</option>");
                      } else {
                         out.println("<option value=\"09\">SEP</option>");
                      }
                      if (su_month == 10) {
                         out.println("<option selected value=\"10\">OCT</option>");
                      } else {
                         out.println("<option value=\"10\">OCT</option>");
                      }
                      if (su_month == 11) {
                         out.println("<option selected value=\"11\">NOV</option>");
                      } else {
                         out.println("<option value=\"11\">NOV</option>");
                      }
                      if (su_month == 12) {
                         out.println("<option selected value=\"12\">DEC</option>");
                      } else {
                         out.println("<option value=\"12\">DEC</option>");
                      }
                 out.println("</select>");
                 out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"su_day\">");
                      if (su_day == 1) {
                         out.println("<option selected selected value=\"01\">1</option>");
                      } else {
                         out.println("<option value=\"01\">1</option>");
                      }
                      if (su_day == 2) {
                         out.println("<option selected value=\"02\">2</option>");
                      } else {
                         out.println("<option value=\"02\">2</option>");
                      }
                      if (su_day == 3) {
                         out.println("<option selected value=\"03\">3</option>");
                      } else {
                         out.println("<option value=\"03\">3</option>");
                      }
                      if (su_day == 4) {
                         out.println("<option selected value=\"04\">4</option>");
                      } else {
                         out.println("<option value=\"04\">4</option>");
                      }
                      if (su_day == 5) {
                         out.println("<option selected value=\"05\">5</option>");
                      } else {
                         out.println("<option value=\"05\">5</option>");
                      }
                      if (su_day == 6) {
                         out.println("<option selected value=\"06\">6</option>");
                      } else {
                         out.println("<option value=\"06\">6</option>");
                      }
                      if (su_day == 7) {
                         out.println("<option selected value=\"07\">7</option>");
                      } else {
                         out.println("<option value=\"07\">7</option>");
                      }
                      if (su_day == 8) {
                         out.println("<option selected value=\"08\">8</option>");
                      } else {
                         out.println("<option value=\"08\">8</option>");
                      }
                      if (su_day == 9) {
                         out.println("<option selected value=\"09\">9</option>");
                      } else {
                         out.println("<option value=\"09\">9</option>");
                      }
                      if (su_day == 10) {
                         out.println("<option selected value=\"10\">10</option>");
                      } else {
                         out.println("<option value=\"10\">10</option>");
                      }
                      if (su_day == 11) {
                         out.println("<option selected value=\"11\">11</option>");
                      } else {
                         out.println("<option value=\"11\">11</option>");
                      }
                      if (su_day == 12) {
                         out.println("<option selected value=\"12\">12</option>");
                      } else {
                         out.println("<option value=\"12\">12</option>");
                      }
                      if (su_day == 13) {
                         out.println("<option selected value=\"13\">13</option>");
                      } else {
                         out.println("<option value=\"13\">13</option>");
                      }
                      if (su_day == 14) {
                         out.println("<option selected value=\"14\">14</option>");
                      } else {
                         out.println("<option value=\"14\">14</option>");
                      }
                      if (su_day == 15) {
                         out.println("<option selected value=\"15\">15</option>");
                      } else {
                         out.println("<option value=\"15\">15</option>");
                      }
                      if (su_day == 16) {
                         out.println("<option selected value=\"16\">16</option>");
                      } else {
                         out.println("<option value=\"16\">16</option>");
                      }
                      if (su_day == 17) {
                         out.println("<option selected value=\"17\">17</option>");
                      } else {
                         out.println("<option value=\"17\">17</option>");
                      }
                      if (su_day == 18) {
                         out.println("<option selected value=\"18\">18</option>");
                      } else {
                         out.println("<option value=\"18\">18</option>");
                      }
                      if (su_day == 19) {
                         out.println("<option selected value=\"19\">19</option>");
                      } else {
                         out.println("<option value=\"19\">19</option>");
                      }
                      if (su_day == 20) {
                         out.println("<option selected value=\"20\">20</option>");
                      } else {
                         out.println("<option value=\"20\">20</option>");
                      }
                      if (su_day == 21) {
                         out.println("<option selected value=\"21\">21</option>");
                      } else {
                         out.println("<option value=\"21\">21</option>");
                      }
                      if (su_day == 22) {
                         out.println("<option selected value=\"22\">22</option>");
                      } else {
                         out.println("<option value=\"22\">22</option>");
                      }
                      if (su_day == 23) {
                         out.println("<option selected value=\"23\">23</option>");
                      } else {
                         out.println("<option value=\"23\">23</option>");
                      }
                      if (su_day == 24) {
                         out.println("<option selected value=\"24\">24</option>");
                      } else {
                         out.println("<option value=\"24\">24</option>");
                      }
                      if (su_day == 25) {
                         out.println("<option selected value=\"25\">25</option>");
                      } else {
                         out.println("<option value=\"25\">25</option>");
                      }
                      if (su_day == 26) {
                         out.println("<option selected value=\"26\">26</option>");
                      } else {
                         out.println("<option value=\"26\">26</option>");
                      }
                      if (su_day == 27) {
                         out.println("<option selected value=\"27\">27</option>");
                      } else {
                         out.println("<option value=\"27\">27</option>");
                      }
                      if (su_day == 28) {
                         out.println("<option selected value=\"28\">28</option>");
                      } else {
                         out.println("<option value=\"28\">28</option>");
                      }
                      if (su_day == 29) {
                         out.println("<option selected value=\"29\">29</option>");
                      } else {
                         out.println("<option value=\"29\">29</option>");
                      }
                      if (su_day == 30) {
                         out.println("<option selected value=\"30\">30</option>");
                      } else {
                         out.println("<option value=\"30\">30</option>");
                      }
                      if (su_day == 31) {
                         out.println("<option selected value=\"31\">31</option>");
                      } else {
                         out.println("<option value=\"31\">31</option>");
                      }
                 out.println("</select>");
                 out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"su_year\">");
                      if (su_year == 2003) {
                         out.println("<option selected value=\"2003\">2003</option>");
                      } else {
                         out.println("<option value=\"2003\">2003</option>");
                      }
                      if (su_year == 2004) {
                         out.println("<option selected value=\"2004\">2004</option>");
                      } else {
                         out.println("<option value=\"2004\">2004</option>");
                      }
                      if (su_year == 2005) {
                         out.println("<option selected value=\"2005\">2005</option>");
                      } else {
                         out.println("<option value=\"2005\">2005</option>");
                      }
                      if (su_year == 2006) {
                         out.println("<option selected value=\"2006\">2006</option>");
                      } else {
                         out.println("<option value=\"2006\">2006</option>");
                      }
                      if (su_year == 2007) {
                         out.println("<option selected value=\"2007\">2007</option>");
                      } else {
                         out.println("<option value=\"2007\">2007</option>");
                      }
                      if (su_year == 2008) {
                         out.println("<option selected value=\"2008\">2008</option>");
                      } else {
                         out.println("<option value=\"2008\">2008</option>");
                      }
                      if (su_year == 2009) {
                         out.println("<option selected value=\"2009\">2009</option>");
                      } else {
                         out.println("<option value=\"2009\">2009</option>");
                      }
                      if (su_year == 2010) {
                         out.println("<option selected value=\"2010\">2010</option>");
                      } else {
                         out.println("<option value=\"2010\">2010</option>");
                      }
                      if (su_year == 2011) {
                         out.println("<option selected value=\"2011\">2011</option>");
                      } else {
                         out.println("<option value=\"2011\">2011</option>");
                      }
                      if (su_year == 2012) {
                         out.println("<option selected value=\"2012\">2012</option>");
                      } else {
                         out.println("<option value=\"2012\">2012</option>");
                      }
                      if (su_year == 2013) {
                         out.println("<option selected value=\"2013\">2013</option>");
                      } else {
                         out.println("<option value=\"2013\">2013</option>");
                      }
                      if (su_year == 2014) {
                         out.println("<option selected value=\"2014\">2014</option>");
                      } else {
                         out.println("<option value=\"2014\">2014</option>");
                      }
                 out.println("</select>");
               out.println("<br><br>");
               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Hour &nbsp;");
                 out.println("<select size=\"1\" name=\"su_hr\">");
                      if (su_hr == 1) {
                         out.println("<option selected selected value=\"01\">1</option>");
                      } else {
                         out.println("<option value=\"01\">1</option>");
                      }
                      if (su_hr == 2) {
                         out.println("<option selected value=\"02\">2</option>");
                      } else {
                         out.println("<option value=\"02\">2</option>");
                      }
                      if (su_hr == 3) {
                         out.println("<option selected value=\"03\">3</option>");
                      } else {
                         out.println("<option value=\"03\">3</option>");
                      }
                      if (su_hr == 4) {
                         out.println("<option selected value=\"04\">4</option>");
                      } else {
                         out.println("<option value=\"04\">4</option>");
                      }
                      if (su_hr == 5) {
                         out.println("<option selected value=\"05\">5</option>");
                      } else {
                         out.println("<option value=\"05\">5</option>");
                      }
                      if (su_hr == 6) {
                         out.println("<option selected value=\"06\">6</option>");
                      } else {
                         out.println("<option value=\"06\">6</option>");
                      }
                      if (su_hr == 7) {
                         out.println("<option selected value=\"07\">7</option>");
                      } else {
                         out.println("<option value=\"07\">7</option>");
                      }
                      if (su_hr == 8) {
                         out.println("<option selected value=\"08\">8</option>");
                      } else {
                         out.println("<option value=\"08\">8</option>");
                      }
                      if (su_hr == 9) {
                         out.println("<option selected value=\"09\">9</option>");
                      } else {
                         out.println("<option value=\"09\">9</option>");
                      }
                      if (su_hr == 10) {
                         out.println("<option selected value=\"10\">10</option>");
                      } else {
                         out.println("<option value=\"10\">10</option>");
                      }
                      if (su_hr == 11) {
                         out.println("<option selected value=\"11\">11</option>");
                      } else {
                         out.println("<option value=\"11\">11</option>");
                      }
                      if (su_hr == 12) {
                         out.println("<option selected value=\"12\">12</option>");
                      } else {
                         out.println("<option value=\"12\">12</option>");
                      }
                 out.println("</select>");
                 out.println("&nbsp; Min &nbsp;");
                 out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + Utilities.ensureDoubleDigit(su_min) + " name=\"su_min\">");
                 out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"su_ampm\">");
                if (su_ampm.equals( "AM" )) {
                   out.println("<option selected value=\"AM\">AM</option>");
                } else {
                   out.println("<option value=\"AM\">AM</option>");
                }
                if (su_ampm.equals( "PM" )) {
                   out.println("<option selected value=\"PM\">PM</option>");
                } else {
                   out.println("<option value=\"PM\">PM</option>");
                }
                 out.println("</select>");
                 out.println("<br><br>");
               out.println("Cut-off Date and Time (when members can no longer sign up):<br><br>");
                 out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Month:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"c_month\">");
                      if (c_month == 1) {
                         out.println("<option selected value=\"01\">JAN</option>");
                      } else {
                         out.println("<option value=\"01\">JAN</option>");
                      }
                      if (c_month == 2) {
                         out.println("<option selected value=\"02\">FEB</option>");
                      } else {
                         out.println("<option value=\"02\">FEB</option>");
                      }
                      if (c_month == 3) {
                         out.println("<option selected value=\"03\">MAR</option>");
                      } else {
                         out.println("<option value=\"03\">MAR</option>");
                      }
                      if (c_month == 4) {
                         out.println("<option selected value=\"04\">APR</option>");
                      } else {
                         out.println("<option value=\"04\">APR</option>");
                      }
                      if (c_month == 5) {
                         out.println("<option selected value=\"05\">MAY</option>");
                      } else {
                         out.println("<option value=\"05\">MAY</option>");
                      }
                      if (c_month == 6) {
                         out.println("<option selected value=\"06\">JUN</option>");
                      } else {
                         out.println("<option value=\"06\">JUN</option>");
                      }
                      if (c_month == 7) {
                         out.println("<option selected value=\"07\">JUL</option>");
                      } else {
                         out.println("<option value=\"07\">JUL</option>");
                      }
                      if (c_month == 8) {
                         out.println("<option selected value=\"08\">AUG</option>");
                      } else {
                         out.println("<option value=\"08\">AUG</option>");
                      }
                      if (c_month == 9) {
                         out.println("<option selected value=\"09\">SEP</option>");
                      } else {
                         out.println("<option value=\"09\">SEP</option>");
                      }
                      if (c_month == 10) {
                         out.println("<option selected value=\"10\">OCT</option>");
                      } else {
                         out.println("<option value=\"10\">OCT</option>");
                      }
                      if (c_month == 11) {
                         out.println("<option selected value=\"11\">NOV</option>");
                      } else {
                         out.println("<option value=\"11\">NOV</option>");
                      }
                      if (c_month == 12) {
                         out.println("<option selected value=\"12\">DEC</option>");
                      } else {
                         out.println("<option value=\"12\">DEC</option>");
                      }
                 out.println("</select>");
                 out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"c_day\">");
                      if (c_day == 1) {
                         out.println("<option selected selected value=\"01\">1</option>");
                      } else {
                         out.println("<option value=\"01\">1</option>");
                      }
                      if (c_day == 2) {
                         out.println("<option selected value=\"02\">2</option>");
                      } else {
                         out.println("<option value=\"02\">2</option>");
                      }
                      if (c_day == 3) {
                         out.println("<option selected value=\"03\">3</option>");
                      } else {
                         out.println("<option value=\"03\">3</option>");
                      }
                      if (c_day == 4) {
                         out.println("<option selected value=\"04\">4</option>");
                      } else {
                         out.println("<option value=\"04\">4</option>");
                      }
                      if (c_day == 5) {
                         out.println("<option selected value=\"05\">5</option>");
                      } else {
                         out.println("<option value=\"05\">5</option>");
                      }
                      if (c_day == 6) {
                         out.println("<option selected value=\"06\">6</option>");
                      } else {
                         out.println("<option value=\"06\">6</option>");
                      }
                      if (c_day == 7) {
                         out.println("<option selected value=\"07\">7</option>");
                      } else {
                         out.println("<option value=\"07\">7</option>");
                      }
                      if (c_day == 8) {
                         out.println("<option selected value=\"08\">8</option>");
                      } else {
                         out.println("<option value=\"08\">8</option>");
                      }
                      if (c_day == 9) {
                         out.println("<option selected value=\"09\">9</option>");
                      } else {
                         out.println("<option value=\"09\">9</option>");
                      }
                      if (c_day == 10) {
                         out.println("<option selected value=\"10\">10</option>");
                      } else {
                         out.println("<option value=\"10\">10</option>");
                      }
                      if (c_day == 11) {
                         out.println("<option selected value=\"11\">11</option>");
                      } else {
                         out.println("<option value=\"11\">11</option>");
                      }
                      if (c_day == 12) {
                         out.println("<option selected value=\"12\">12</option>");
                      } else {
                         out.println("<option value=\"12\">12</option>");
                      }
                      if (c_day == 13) {
                         out.println("<option selected value=\"13\">13</option>");
                      } else {
                         out.println("<option value=\"13\">13</option>");
                      }
                      if (c_day == 14) {
                         out.println("<option selected value=\"14\">14</option>");
                      } else {
                         out.println("<option value=\"14\">14</option>");
                      }
                      if (c_day == 15) {
                         out.println("<option selected value=\"15\">15</option>");
                      } else {
                         out.println("<option value=\"15\">15</option>");
                      }
                      if (c_day == 16) {
                         out.println("<option selected value=\"16\">16</option>");
                      } else {
                         out.println("<option value=\"16\">16</option>");
                      }
                      if (c_day == 17) {
                         out.println("<option selected value=\"17\">17</option>");
                      } else {
                         out.println("<option value=\"17\">17</option>");
                      }
                      if (c_day == 18) {
                         out.println("<option selected value=\"18\">18</option>");
                      } else {
                         out.println("<option value=\"18\">18</option>");
                      }
                      if (c_day == 19) {
                         out.println("<option selected value=\"19\">19</option>");
                      } else {
                         out.println("<option value=\"19\">19</option>");
                      }
                      if (c_day == 20) {
                         out.println("<option selected value=\"20\">20</option>");
                      } else {
                         out.println("<option value=\"20\">20</option>");
                      }
                      if (c_day == 21) {
                         out.println("<option selected value=\"21\">21</option>");
                      } else {
                         out.println("<option value=\"21\">21</option>");
                      }
                      if (c_day == 22) {
                         out.println("<option selected value=\"22\">22</option>");
                      } else {
                         out.println("<option value=\"22\">22</option>");
                      }
                      if (c_day == 23) {
                         out.println("<option selected value=\"23\">23</option>");
                      } else {
                         out.println("<option value=\"23\">23</option>");
                      }
                      if (c_day == 24) {
                         out.println("<option selected value=\"24\">24</option>");
                      } else {
                         out.println("<option value=\"24\">24</option>");
                      }
                      if (c_day == 25) {
                         out.println("<option selected value=\"25\">25</option>");
                      } else {
                         out.println("<option value=\"25\">25</option>");
                      }
                      if (c_day == 26) {
                         out.println("<option selected value=\"26\">26</option>");
                      } else {
                         out.println("<option value=\"26\">26</option>");
                      }
                      if (c_day == 27) {
                         out.println("<option selected value=\"27\">27</option>");
                      } else {
                         out.println("<option value=\"27\">27</option>");
                      }
                      if (c_day == 28) {
                         out.println("<option selected value=\"28\">28</option>");
                      } else {
                         out.println("<option value=\"28\">28</option>");
                      }
                      if (c_day == 29) {
                         out.println("<option selected value=\"29\">29</option>");
                      } else {
                         out.println("<option value=\"29\">29</option>");
                      }
                      if (c_day == 30) {
                         out.println("<option selected value=\"30\">30</option>");
                      } else {
                         out.println("<option value=\"30\">30</option>");
                      }
                      if (c_day == 31) {
                         out.println("<option selected value=\"31\">31</option>");
                      } else {
                         out.println("<option value=\"31\">31</option>");
                      }
                 out.println("</select>");
                 out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"c_year\">");
                      if (c_year == 2003) {
                         out.println("<option selected value=\"2003\">2003</option>");
                      } else {
                         out.println("<option value=\"2003\">2003</option>");
                      }
                      if (c_year == 2004) {
                         out.println("<option selected value=\"2004\">2004</option>");
                      } else {
                         out.println("<option value=\"2004\">2004</option>");
                      }
                      if (c_year == 2005) {
                         out.println("<option selected value=\"2005\">2005</option>");
                      } else {
                         out.println("<option value=\"2005\">2005</option>");
                      }
                      if (c_year == 2006) {
                         out.println("<option selected value=\"2006\">2006</option>");
                      } else {
                         out.println("<option value=\"2006\">2006</option>");
                      }
                      if (c_year == 2007) {
                         out.println("<option selected value=\"2007\">2007</option>");
                      } else {
                         out.println("<option value=\"2007\">2007</option>");
                      }
                      if (c_year == 2008) {
                         out.println("<option selected value=\"2008\">2008</option>");
                      } else {
                         out.println("<option value=\"2008\">2008</option>");
                      }
                      if (c_year == 2009) {
                         out.println("<option selected value=\"2009\">2009</option>");
                      } else {
                         out.println("<option value=\"2009\">2009</option>");
                      }
                      if (c_year == 2010) {
                         out.println("<option selected value=\"2010\">2010</option>");
                      } else {
                         out.println("<option value=\"2010\">2010</option>");
                      }
                      if (c_year == 2011) {
                         out.println("<option selected value=\"2011\">2011</option>");
                      } else {
                         out.println("<option value=\"2011\">2011</option>");
                      }
                      if (c_year == 2012) {
                         out.println("<option selected value=\"2012\">2012</option>");
                      } else {
                         out.println("<option value=\"2012\">2012</option>");
                      }
                      if (c_year == 2013) {
                         out.println("<option selected value=\"2013\">2013</option>");
                      } else {
                         out.println("<option value=\"2013\">2013</option>");
                      }
                      if (c_year == 2014) {
                         out.println("<option selected value=\"2014\">2014</option>");
                      } else {
                         out.println("<option value=\"2014\">2014</option>");
                      }
                 out.println("</select>");
               out.println("<br><br>");
               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Hour &nbsp;");
                 out.println("<select size=\"1\" name=\"c_hr\">");
                      if (c_hr == 1) {
                         out.println("<option selected selected value=\"01\">1</option>");
                      } else {
                         out.println("<option value=\"01\">1</option>");
                      }
                      if (c_hr == 2) {
                         out.println("<option selected value=\"02\">2</option>");
                      } else {
                         out.println("<option value=\"02\">2</option>");
                      }
                      if (c_hr == 3) {
                         out.println("<option selected value=\"03\">3</option>");
                      } else {
                         out.println("<option value=\"03\">3</option>");
                      }
                      if (c_hr == 4) {
                         out.println("<option selected value=\"04\">4</option>");
                      } else {
                         out.println("<option value=\"04\">4</option>");
                      }
                      if (c_hr == 5) {
                         out.println("<option selected value=\"05\">5</option>");
                      } else {
                         out.println("<option value=\"05\">5</option>");
                      }
                      if (c_hr == 6) {
                         out.println("<option selected value=\"06\">6</option>");
                      } else {
                         out.println("<option value=\"06\">6</option>");
                      }
                      if (c_hr == 7) {
                         out.println("<option selected value=\"07\">7</option>");
                      } else {
                         out.println("<option value=\"07\">7</option>");
                      }
                      if (c_hr == 8) {
                         out.println("<option selected value=\"08\">8</option>");
                      } else {
                         out.println("<option value=\"08\">8</option>");
                      }
                      if (c_hr == 9) {
                         out.println("<option selected value=\"09\">9</option>");
                      } else {
                         out.println("<option value=\"09\">9</option>");
                      }
                      if (c_hr == 10) {
                         out.println("<option selected value=\"10\">10</option>");
                      } else {
                         out.println("<option value=\"10\">10</option>");
                      }
                      if (c_hr == 11) {
                         out.println("<option selected value=\"11\">11</option>");
                      } else {
                         out.println("<option value=\"11\">11</option>");
                      }
                      if (c_hr == 12) {
                         out.println("<option selected value=\"12\">12</option>");
                      } else {
                         out.println("<option value=\"12\">12</option>");
                      }
                 out.println("</select>");
                 out.println("&nbsp; Min &nbsp;");
                 out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=\"" + Utilities.ensureDoubleDigit(c_min) + "\" name=\"c_min\">");
                 out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"c_ampm\">");
                 if (c_ampm.equals( "AM" )) {
                   out.println("<option selected value=\"AM\">AM</option>");
                 } else {
                   out.println("<option value=\"AM\">AM</option>");
                 }
                 if (c_ampm.equals( "PM" )) {
                   out.println("<option selected value=\"PM\">PM</option>");
                 } else {
                   out.println("<option value=\"PM\">PM</option>");
                 }
                 out.println("</select>");

                 out.println("<p align=\"left\">&nbsp;&nbsp;<b>Groups to be Restricted</b><br>");
                 out.println("&nbsp;&nbsp;Specify Member Types to restrict and/or Membership Types to restrict.<br>");
                 out.println("&nbsp;&nbsp;(the types selected will NOT be allowed to register for this event)</p>");

                 out.println("<p align=\"left\">&nbsp;&nbsp;Members to be Restricted (select all that apply, or none):");

                 for (i=0; i<parm.MAX_Mems; i++) {

                    if (!parm.mem[i].equals( "" )) {          // if supported and

                       i2 = i + 1;
                       out.println("<br>");
                       out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");

                       if (!mtype[i2].equals( "" )) {        //    already checked

                          out.println("<input type=\"checkbox\" checked name=\"mem" +i2+ "\" value=\"" +parm.mem[i]+ "\">&nbsp;&nbsp;" +parm.mem[i]);
                       } else {
                          out.println("<input type=\"checkbox\" name=\"mem" +i2+ "\" value=\"" +parm.mem[i]+ "\">&nbsp;&nbsp;" +parm.mem[i]);
                       }
                    }
                 }

                 out.println("<p align=\"left\">&nbsp;&nbsp;Membership Types to be Restricted (select all that apply, or none):");

                 for (i=0; i<parm.MAX_Mships; i++) {

                    if (!parm.mship[i].equals( "" )) {          // if supported and

                       i2 = i + 1;
                       out.println("<br>");
                       out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");

                       if (!mship[i2].equals( "" )) {        //    already checked

                          out.println("<input type=\"checkbox\" checked name=\"mship" +i2+ "\" value=\"" +parm.mship[i]+ "\">&nbsp;&nbsp;" +parm.mship[i]);
                       } else {
                          out.println("<input type=\"checkbox\" name=\"mship" +i2+ "\" value=\"" +parm.mship[i]+ "\">&nbsp;&nbsp;" +parm.mship[i]);
                       }
                    }
                 }

                 if (!copyname.equals( "" )) {         // if a copy request

                    out.println("<input type=\"hidden\" name=\"event_name\" value=\"" + name + "\">");
                    out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                    out.println("<input type=\"hidden\" name=\"date\" value=\"" + event_date + "\">");
                    out.println("<input type=\"hidden\" name=\"season\" value=\"" + season + "\">");
         
                    if (showOld) {
                        out.println("<input type=\"hidden\" name=\"showOld\" value=\"Yes\">");
                    }

                    out.println("<p align=\"center\">");
                    out.println("<input type=\"submit\" name=\"addevent\" value=\"Add Event\">");
                    out.println("</p>");


                 } else {                 // normal edit request

                     out.println("<input type=\"hidden\" name=\"event_name\" value=\"" + name + "\">");
                     out.println("<input type=\"hidden\" name=\"activity_id\" value=\"" + sess_activity_id + "\">");
                     out.println("<input type=\"hidden\" name=\"locations_csv\" value=\"" +locations_csv+ "\">");
                     out.println("<input type=\"hidden\" name=\"oldName\" value=\"" + oldName + "\">");
                     out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                     out.println("<input type=\"hidden\" name=\"oldCourse\" value=\"" + oldCourse + "\">");
                     out.println("<input type=\"hidden\" name=\"oldDate\" value=\"" + oldDate + "\">");
                     out.println("<input type=\"hidden\" name=\"oldMax\" value=\"" + oldMax + "\">");
                     out.println("<input type=\"hidden\" name=\"month\" value=" + month + ">");
                     out.println("<input type=\"hidden\" name=\"day\" value=" + day + ">");
                     out.println("<input type=\"hidden\" name=\"year\" value=" + year + ">");
                     out.println("<input type=\"hidden\" name=\"start_hr\" value=" + shr + ">");
                     out.println("<input type=\"hidden\" name=\"start_min\" value=" + smin + ">");
                     out.println("<input type=\"hidden\" name=\"start_ampm\" value=" + sampm + ">");
                     out.println("<input type=\"hidden\" name=\"end_hr\" value=" + ehr + ">");
                     out.println("<input type=\"hidden\" name=\"end_min\" value=" + emin + ">");
                     out.println("<input type=\"hidden\" name=\"end_ampm\" value=" + eampm + ">");
                     out.println("<input type=\"hidden\" name=\"act_hr\" value=" + ahr + ">");
                     out.println("<input type=\"hidden\" name=\"act_min\" value=" + amin + ">");
                     out.println("<input type=\"hidden\" name=\"act_ampm\" value=" + aampm + ">");
                     out.println("<input type=\"hidden\" name=\"type\" value=" + type + ">");
                     out.println("<input type=\"hidden\" name=\"holes\" value=" + holes + ">");
                     out.println("<input type=\"hidden\" name=\"color\" value=\"" + color + "\">");
                     out.println("<input type=\"hidden\" name=\"signUp\" value=" + signUp + ">");
                     out.println("<input type=\"hidden\" name=\"gstOnly\" value=" + gstOnly + ">");
                     out.println("<input type=\"hidden\" name=\"gender\" value=\"" + gender + "\">");
                     out.println("<input type=\"hidden\" name=\"fb\" value=" + fb + ">");
                     out.println("<input type=\"hidden\" name=\"itin\" value=\"" + StringEscapeUtils.escapeHtml(itin) + "\">");
                     out.println("<input type=\"hidden\" name=\"fb2\" value=\"" + fb2 + "\">");
                     out.println("<input type=\"hidden\" name=\"start_hr2\" value=" + shr2 + ">");
                     out.println("<input type=\"hidden\" name=\"start_min2\" value=" + smin2 + ">");
                     out.println("<input type=\"hidden\" name=\"start_ampm2\" value=" + sampm2 + ">");
                     out.println("<input type=\"hidden\" name=\"end_hr2\" value=" + ehr2 + ">");
                     out.println("<input type=\"hidden\" name=\"end_min2\" value=" + emin2 + ">");
                     out.println("<input type=\"hidden\" name=\"end_ampm2\" value=" + eampm2 + ">");
                     out.println("<input type=\"hidden\" name=\"sheet\" value=\"" +sheet+ "\">");
                     out.println("<input type=\"hidden\" name=\"season\" value=" + season + ">");
                     out.println("<input type=\"hidden\" name=\"export_type\" value=" + export_type + ">");
                     out.println("<input type=\"hidden\" name=\"email1\" value=\"" + email1 + "\">");
                     out.println("<input type=\"hidden\" name=\"email2\" value=\"" + email2 + "\">");
                     out.println("<input type=\"hidden\" name=\"memedit\" value=\"" + memedit + "\">");
                     out.println("<input type=\"hidden\" name=\"page2\" value=\"yes\">");
                     
                     Common_Config.printEventCategoryHiddenInputs(category_ids, out);

                     out.println("<input type=\"hidden\" name=\"ask_homeclub\" value=\"" + ask_homeclub + "\">");
                     out.println("<input type=\"hidden\" name=\"ask_phone\" value=\"" + ask_phone + "\">");
                     out.println("<input type=\"hidden\" name=\"ask_address\" value=\"" + ask_address + "\">");
                     out.println("<input type=\"hidden\" name=\"ask_hdcp\" value=\"" + ask_hdcp + "\">");
                     out.println("<input type=\"hidden\" name=\"ask_email\" value=\"" + ask_email + "\">");
                     out.println("<input type=\"hidden\" name=\"ask_gender\" value=\"" + ask_gender + "\">");
                     out.println("<input type=\"hidden\" name=\"ask_shirtsize\" value=\"" + ask_shirtsize + "\">");
                     out.println("<input type=\"hidden\" name=\"ask_shoesize\" value=\"" + ask_shoesize + "\">");
                     out.println("<input type=\"hidden\" name=\"ask_otherQ1\" value=\"" + ask_otherQ1 + "\">");
                     out.println("<input type=\"hidden\" name=\"ask_otherQ2\" value=\"" + ask_otherQ2 + "\">");
                     out.println("<input type=\"hidden\" name=\"ask_otherQ3\" value=\"" + ask_otherQ3 + "\">");
                     
                     out.println("<input type=\"hidden\" name=\"req_guestname\" value=\"" + req_guestname + "\">");
                     out.println("<input type=\"hidden\" name=\"req_homeclub\" value=\"" + req_homeclub + "\">");
                     out.println("<input type=\"hidden\" name=\"req_phone\" value=\"" + req_phone + "\">");
                     out.println("<input type=\"hidden\" name=\"req_address\" value=\"" + req_address + "\">");
                     out.println("<input type=\"hidden\" name=\"req_hdcp\" value=\"" + req_hdcp + "\">");
                     out.println("<input type=\"hidden\" name=\"req_email\" value=\"" + req_email + "\">");
                     out.println("<input type=\"hidden\" name=\"req_gender\" value=\"" + req_gender + "\">");
                     out.println("<input type=\"hidden\" name=\"req_shirtsize\" value=\"" + req_shirtsize + "\">");
                     out.println("<input type=\"hidden\" name=\"req_shoesize\" value=\"" + req_shoesize + "\">");
                     out.println("<input type=\"hidden\" name=\"req_otherQ1\" value=\"" + req_otherQ1 + "\">");
                     out.println("<input type=\"hidden\" name=\"req_otherQ2\" value=\"" + req_otherQ2 + "\">");
                     out.println("<input type=\"hidden\" name=\"req_otherQ3\" value=\"" + req_otherQ3 + "\">");
                     
                     out.println("<input type=\"hidden\" name=\"otherQ1\" value=\"" + otherQ1 + "\">");
                     out.println("<input type=\"hidden\" name=\"otherQ2\" value=\"" + otherQ2 + "\">");
                     out.println("<input type=\"hidden\" name=\"otherQ3\" value=\"" + otherQ3 + "\">");
         
                     if (showOld) {
                         out.println("<input type=\"hidden\" name=\"showOld\" value=\"Yes\">");
                     }
                     
                    out.println("<p align=\"center\">");
                    out.println("<input type=\"submit\" name=\"Update\" value=\"Update\">");
                    out.println("</p>");
                 }
                    
               out.println("</font></td></tr></form></table>");
               out.println("</td></tr></table>");                                     // end of main page table

      out.println("<font size=\"2\">");
      if (!sheet.equals( "yes" )) {                      // if not call from Proshop_sheet
         out.println("<p><form method=\"get\" action=\"Proshop_events\">");
         out.println("<div style=\"margin:auto; text-align:center\"><input type=\"submit\" value=\"Cancel\" style=\"text-decoration:underline; background:#8B8970; width:75px\"></div>");
         
         if (showOld) {
             out.println("<input type=\"hidden\" name=\"showOld\" value=\"Yes\">");
         }
         
         out.println("</form></p>");
      } else {
         out.println("<p><form><div style=\"margin:auto; text-align:center\"><input type=\"button\" value=\"Cancel\" onClick='self.close();'></div>");
         out.println("</form></p>");
      }
      out.println("</font>");

      out.println("</center></font></body></html>");
      out.close();
   }

 }   // end of update1


   //*************************************************
   //  updateConf - Request a confirmation from user
   //*************************************************

 private void updateConf(HttpServletRequest req, PrintWriter out, HttpSession sess) {


   ResultSet rs = null;

   Connection con = SystemUtils.getCon(sess);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_EVENT", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_EVENT", out);
   }

   int sess_activity_id = (Integer)sess.getAttribute("activity_id");

   String format = "";
   String pairings = "";
   String ssize = "";
   String sminsize = "";
   String max = "";
   String guests = "";
   String x = "";
   String xhrs = "";
   String memcost = "";
   String gstcost = "";
   String ssu_month = "";
   String ssu_day = "";
   String ssu_year = "";
   String su_hr = "";
   String ssu_min = "";
   String su_ampm = "";
   String sc_month = "";
   String sc_day = "";
   String sc_year = "";
   String c_hr = "";
   String sc_min = "";
   String c_ampm = "";
   String mempos = "";
   String gstpos = "";

   int s_min = 0;
   int e_min = 0;
   int s_min2 = 0;
   int e_min2 = 0;
   int a_min = 0;
   int su_min = 0;
   int c_min = 0;
   int month = 0;
   int day = 0;
   int year = 0;
   int su_month = 0;
   int su_day = 0;
   int su_year = 0;
   int c_month = 0;
   int c_day = 0;
   int c_year = 0;
   int holes = 0;
   int i = 0;
   int size = 0;
   int minsize = 0;
   int gender = 0;
   int export_type = 0;
   int activity_id = sess_activity_id;

   int [] tmodei = new int [16];   // Modes of trans indicators

   long su_date = 0;
   long c_date = 0;

   String ssampm = "AM";
   String seampm = "AM";
   String ssampm2 = "AM";
   String seampm2 = "AM";
   String saampm = "AM";

   boolean found = false;
   boolean showOld = false;
      
   ArrayList<Integer> category_ids = new ArrayList<Integer>();  // ArrayList to hold all category_ids for this event

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(sess_activity_id, con);

   String [] mship = new String [parm.MAX_Mems+1];            // Mem Types
   String [] mtype = new String [parm.MAX_Mships+1];          // Mship Types

   //
   // Get the parameters entered
   //
   String name = req.getParameter("event_name").trim();
   String oldName = req.getParameter("oldName");
   String s_month = req.getParameter("month");
   String s_day = req.getParameter("day");
   String s_year = req.getParameter("year");
   String shr = req.getParameter("start_hr");
   String smin = req.getParameter("start_min");
   String sampm = req.getParameter("start_ampm");
   String ehr = req.getParameter("end_hr");
   String emin = req.getParameter("end_min");
   String eampm = req.getParameter("end_ampm");
   String ahr = req.getParameter("act_hr");
   String amin = req.getParameter("act_min");
   String aampm = req.getParameter("act_ampm");
   String type = req.getParameter("type");
   String sholes = req.getParameter("holes");
   String color = req.getParameter("color");
   String course = req.getParameter("course");
   String oldCourse = req.getParameter("oldCourse");
   String oldDate = req.getParameter("oldDate");
   String oldMax = req.getParameter("oldMax");
   String signUp = req.getParameter("signUp");
   String gstOnly = req.getParameter("gstOnly");
   String sgender = req.getParameter("gender");
   String fb = (req.getParameter("fb") != null) ? req.getParameter("fb") : "";
   String itin = req.getParameter("itin").trim();
   String shr2 = req.getParameter("start_hr2");
   String smin2 = req.getParameter("start_min2");
   String sampm2 = req.getParameter("start_ampm2");
   String ehr2 = req.getParameter("end_hr2");
   String emin2 = req.getParameter("end_min2");
   String eampm2 = req.getParameter("end_ampm2");
   String fb2 = req.getParameter("fb2");
   String sheet = req.getParameter("sheet");
   String sexport_type = req.getParameter("export_type");
   //String sactivity_id = req.getParameter("activity_id");
   String email1 = req.getParameter("email1");
   String email2 = req.getParameter("email2");
   String locations_csv = req.getParameter("locations_csv");         
   String memedit = req.getParameter("memedit");    // can member edit event times once on tee sheet ?
   
   category_ids = Utilities.buildEventCategoryListFromReq(req, sess_activity_id, con);
         
   int season = (req.getParameter("season") != null && req.getParameter("season").equals("1")) ? 1 : 0;

   int ask_hdcp = (req.getParameter("ask_hdcp") != null && req.getParameter("ask_hdcp").equals("1")) ? 1 : 0;
   int ask_homeclub = (req.getParameter("ask_homeclub") != null && req.getParameter("ask_homeclub").equals("1")) ? 1 : 0;
   int ask_gender = (req.getParameter("ask_gender") != null && req.getParameter("ask_gender").equals("1")) ? 1 : 0;
   int ask_phone = (req.getParameter("ask_phone") != null && req.getParameter("ask_phone").equals("1")) ? 1 : 0;
   int ask_email = (req.getParameter("ask_email") != null && req.getParameter("ask_email").equals("1")) ? 1 : 0;
   int ask_address = (req.getParameter("ask_address") != null && req.getParameter("ask_address").equals("1")) ? 1 : 0;
   int ask_shirtsize = (req.getParameter("ask_shirtsize") != null && req.getParameter("ask_shirtsize").equals("1")) ? 1 : 0;
   int ask_shoesize = (req.getParameter("ask_shoesize") != null && req.getParameter("ask_shoesize").equals("1")) ? 1 : 0;
   int ask_otherQ1 = (req.getParameter("ask_otherQ1") != null && req.getParameter("ask_otherQ1").equals("1")) ? 1 : 0;
   int ask_otherQ2 = (req.getParameter("ask_otherQ2") != null && req.getParameter("ask_otherQ2").equals("1")) ? 1 : 0;
   int ask_otherQ3 = (req.getParameter("ask_otherQ3") != null && req.getParameter("ask_otherQ3").equals("1")) ? 1 : 0;

   int req_guestname = (req.getParameter("req_guestname") != null && req.getParameter("req_guestname").equals("1")) ? 1 : 0;
   int req_hdcp = (req.getParameter("req_hdcp") != null && req.getParameter("req_hdcp").equals("1")) ? 1 : 0;
   int req_homeclub = (req.getParameter("req_homeclub") != null && req.getParameter("req_homeclub").equals("1")) ? 1 : 0;
   int req_gender = (req.getParameter("req_gender") != null && req.getParameter("req_gender").equals("1")) ? 1 : 0;
   int req_phone = (req.getParameter("req_phone") != null && req.getParameter("req_phone").equals("1")) ? 1 : 0;
   int req_email = (req.getParameter("req_email") != null && req.getParameter("req_email").equals("1")) ? 1 : 0;
   int req_address = (req.getParameter("req_address") != null && req.getParameter("req_address").equals("1")) ? 1 : 0;
   int req_shirtsize = (req.getParameter("req_shirtsize") != null && req.getParameter("req_shirtsize").equals("1")) ? 1 : 0;
   int req_shoesize = (req.getParameter("req_shoesize") != null && req.getParameter("req_shoesize").equals("1")) ? 1 : 0;
   int req_otherQ1 = (req.getParameter("req_otherQ1") != null && req.getParameter("req_otherQ1").equals("1")) ? 1 : 0;
   int req_otherQ2 = (req.getParameter("req_otherQ2") != null && req.getParameter("req_otherQ2").equals("1")) ? 1 : 0;
   int req_otherQ3 = (req.getParameter("req_otherQ3") != null && req.getParameter("req_otherQ3").equals("1")) ? 1 : 0;
   
   String otherQ1 = (ask_otherQ1 == 0) ? "" : req.getParameter("otherQ1");
   String otherQ2 = (ask_otherQ2 == 0) ? "" : req.getParameter("otherQ2");
   String otherQ3 = (ask_otherQ3 == 0) ? "" : req.getParameter("otherQ3");
   
   showOld = (req.getParameter("showOld")) != null ? true : false;
   
   // sanity check some of the options
   if (otherQ1 == null) otherQ1 = "";
   if (otherQ2 == null) otherQ2 = "";
   if (otherQ3 == null) otherQ3 = "";


   ArrayList<Integer> locations = new ArrayList<Integer>();

   if (activity_id != 0 && locations_csv == null) {

       locations_csv = Common_Config.buildLocationsString(req);
   }
/*
      locations_csv = "";
      int location_count = 0;
      String slocation_count = (req.getParameter("location_count") == null) ? "0" : req.getParameter("location_count");
      location_count = Integer.parseInt(slocation_count);

       //
       // Get all the checked activities (courts or locations) from the form
       // this is where the event will take place
       //
       try {

           for (i = 0; i <= location_count; i++) {

               if (req.getParameter("actChkBox_" + i) != null) {

                   try {

                       //out.println("<!-- " + i + "=" + req.getParameter("actChkBox_" + i) + " -->");
                       locations.add(Integer.parseInt(req.getParameter("actChkBox_" + i)));

                   } catch (Exception ignore) { }

               }

           }

           for (i = 0; i < locations.size(); i++) {

               locations_csv += locations.get(i) + ",";
           }

           if (!locations_csv.equals("")) {

               locations_csv = locations_csv.substring(0, locations_csv.length() - 1);

           } else {

               out.println(SystemUtils.HeadTitle("Data Entry Error"));
               out.println("<BODY><CENTER>");
               out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
               out.println("<BR><BR>You must specify at least one location for this event.");
               out.println("<BR>Please try again.");
               out.println("<BR><BR>");
               out.println("<font size=\"2\">");
               out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
               out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</form></font>");
               out.println("</CENTER></BODY></HTML>");
               return;

           }

       } catch (Exception exc) {

           out.println("<br>locations.size()=" + locations.size());
           out.println("<br>location_count=" + location_count);
           out.println("<br>locations_csv=" + locations_csv);
           out.println("<br>Err=" + exc.toString());
           return;

       }

   }
*/
       
   if (req.getParameter("page2") != null) {        // if member signup parms provided

      format = req.getParameter("format");
      pairings = req.getParameter("pairings");
      ssize = req.getParameter("size");
      sminsize = req.getParameter("minsize");
      max = req.getParameter("max");
      guests = req.getParameter("guests");
      x = req.getParameter("x");
      xhrs = req.getParameter("xhrs");
      memcost = req.getParameter("memcost");
      gstcost = req.getParameter("gstcost");
      ssu_month = req.getParameter("su_month");
      ssu_day = req.getParameter("su_day");
      ssu_year = req.getParameter("su_year");
      su_hr = req.getParameter("su_hr");
      ssu_min = req.getParameter("su_min");
      su_ampm = req.getParameter("su_ampm");
      sc_month = req.getParameter("c_month");
      sc_day = req.getParameter("c_day");
      sc_year = req.getParameter("c_year");
      c_hr = req.getParameter("c_hr");
      sc_min = req.getParameter("c_min");
      c_ampm = req.getParameter("c_ampm");

      if (req.getParameter("mempos") != null) {        // if member pos charge code specified

         mempos = req.getParameter("mempos");
      }
      if (req.getParameter("gstpos") != null) {        // if guest pos charge code specified

         gstpos = req.getParameter("gstpos");
      }

      for (i=1; i<parm.MAX_Mems+1; i++) {
         mtype[i] = "";
         if (req.getParameter("mem" +i) != null) {

            mtype[i] = req.getParameter("mem" +i);
         }
      }

      for (i=1; i<parm.MAX_Mships+1; i++) {
         mship[i] = "";
         if (req.getParameter("mship" +i) != null) {

            mship[i] = req.getParameter("mship" +i);
         }
      }

      //
      //  Set the Modes of Trans according to which ones were selected
      //
      if (req.getParameter("tmode1") != null) {

         tmodei[0] = 1;
         found = true;
      } else {
         tmodei[0] = 0;
      }
      if (req.getParameter("tmode2") != null) {

         tmodei[1] = 1;
         found = true;
      } else {
         tmodei[1] = 0;
      }
      if (req.getParameter("tmode3") != null) {

         tmodei[2] = 1;
         found = true;
      } else {
         tmodei[2] = 0;
      }
      if (req.getParameter("tmode4") != null) {

         tmodei[3] = 1;
         found = true;
      } else {
         tmodei[3] = 0;
      }
      if (req.getParameter("tmode5") != null) {

         tmodei[4] = 1;
         found = true;
      } else {
         tmodei[4] = 0;
      }
      if (req.getParameter("tmode6") != null) {

         tmodei[5] = 1;
         found = true;
      } else {
         tmodei[5] = 0;
      }
      if (req.getParameter("tmode7") != null) {

         tmodei[6] = 1;
         found = true;
      } else {
         tmodei[6] = 0;
      }
      if (req.getParameter("tmode8") != null) {

         tmodei[7] = 1;
         found = true;
      } else {
         tmodei[7] = 0;
      }
      if (req.getParameter("tmode9") != null) {

         tmodei[8] = 1;
         found = true;
      } else {
         tmodei[8] = 0;
      }
      if (req.getParameter("tmode10") != null) {

         tmodei[9] = 1;
         found = true;
      } else {
         tmodei[9] = 0;
      }
      if (req.getParameter("tmode11") != null) {

         tmodei[10] = 1;
         found = true;
      } else {
         tmodei[10] = 0;
      }
      if (req.getParameter("tmode12") != null) {

         tmodei[11] = 1;
         found = true;
      } else {
         tmodei[11] = 0;
      }
      if (req.getParameter("tmode13") != null) {

         tmodei[12] = 1;
         found = true;
      } else {
         tmodei[12] = 0;
      }
      if (req.getParameter("tmode14") != null) {

         tmodei[13] = 1;
         found = true;
      } else {
         tmodei[13] = 0;
      }
      if (req.getParameter("tmode15") != null) {

         tmodei[14] = 1;
         found = true;
      } else {
         tmodei[14] = 0;
      }
      if (req.getParameter("tmode16") != null) {

         tmodei[15] = 1;
         found = true;
      } else {
         tmodei[15] = 0;
      }

      su_min = Integer.parseInt(ssu_min);
      c_min = Integer.parseInt(sc_min);

      if (!ssize.equals( "" )) {

         size = Integer.parseInt(ssize);
      }
      if (!sminsize.equals( "" )) {

         minsize = Integer.parseInt(sminsize);
      }
      if (!ssu_month.equals( "" )) {

         su_month = Integer.parseInt(ssu_month);
      }
      if (!ssu_day.equals( "" )) {

         su_day = Integer.parseInt(ssu_day);
      }
      if (!ssu_year.equals( "" )) {

         su_year = Integer.parseInt(ssu_year);
      }

      if (!sc_month.equals( "" )) {

         c_month = Integer.parseInt(sc_month);
      }
      if (!sc_day.equals( "" )) {

         c_day = Integer.parseInt(sc_day);
      }
      if (!sc_year.equals( "" )) {

         c_year = Integer.parseInt(sc_year);
      }

      su_date = su_year * 10000;             // create a date field from input values
      su_date = su_date + (su_month * 100);
      su_date = su_date + su_day;             // date = yyyymmdd (for comparisons)

      c_date = c_year * 10000;             // create a date field from input values
      c_date = c_date + (c_month * 100);
      c_date = c_date + c_day;             // date = yyyymmdd (for comparisons)


   }

   if (sampm.equals ( "12" )) {      // sampm & eampm are either '00' or '12'
      ssampm = "PM";
   }
   if (eampm.equals ( "12" )) {
      seampm = "PM";
   }
   if (aampm.equals ( "12" )) {
      saampm = "PM";
   }
   if (sampm2.equals ( "12" )) {     
      ssampm2 = "PM";
   }
   if (eampm2.equals ( "12" )) {
      seampm2 = "PM";
   }

   //
   // Convert the numeric string parameters to Int's
   //
   try {
      month = Integer.parseInt(s_month);
      day = Integer.parseInt(s_day);
      year = Integer.parseInt(s_year);
      s_min = Integer.parseInt(smin);
      e_min = Integer.parseInt(emin);
      a_min = Integer.parseInt(amin);
      holes = Integer.parseInt(sholes);
      s_min2 = Integer.parseInt(smin2);
      e_min2 = Integer.parseInt(emin2);
      gender = Integer.parseInt(sgender);
      export_type = Integer.parseInt(sexport_type);
   }
   catch (NumberFormatException e) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Data Input Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to process one or more of the parameters entered.");
      out.println("<BR>Please go back and make sure all values are correct.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR>Exception:   " + e.getMessage());
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
   //  adjust some values for tests
   //
   long date = year * 10000;      // create event date
   date = date + (month * 100);
   date = date + day;             // date = yyyymmdd (for comparisons)

   //
   //  Validate parms
   //
   if (!oldName.equals( name )) {    // if name has changed

      try {
         //
         //  Check if event already exists in database
         //
         PreparedStatement pstmt1 = con.prepareStatement (
                 "SELECT date FROM events2b WHERE name = ? AND courseName = ?");

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, name);       // put the parm in stmt
         pstmt1.setString(2, course);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            dupEvent(out);    // event exists - inform the user and return
            pstmt1.close();
            return;
         }
         pstmt1.close();
      }
      catch (Exception exc) {

         dbError(out, exc);
         return;
      }
   }


   //  Course names cannot include special chars because of _jump - uri requirements
   if ( SystemUtils.scanName(name) ) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
      out.println("<BR><BR>Special characters cannot be part of the Event Name.");
      out.println("<BR>You can only use the characters A-Z, a-z, 0-9 and space.");
      out.println("<BR>You entered:" + name);
      out.println("<BR>Please try again.");
      out.println("<BR><BR><font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   if (gender == -1) {    // if gender was not specified

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Error - Gender Not Specified</H3>");
      out.println("<BR>You must specify the gender for this event as either ");
      out.println(Labels.gender_opts[1] + ", " + Labels.gender_opts[2] + " or " + Labels.gender_opts[3] + ".");
      out.println("<BR><BR>Please correct and try again.");
      out.println("<BR><BR><font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(2)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   if (req.getParameter("page2") != null && activity_id == 0 && found == false) {    // if no Modes of Trans were specified

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Error - Mode of Transportation Not Specified</H3>");
      out.println("<BR><BR>If members are allowed to signup for the event online,");
      out.println("<BR>you must specify the modes of transportation that will be allowed for the event.");
      out.println("<BR><BR>Please correct and try again.");
      out.println("<BR><BR><font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   if (gstOnly.equals( "Yes" ) && signUp.equals( "Yes" )) {

      out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
      out.println("<BODY><CENTER><BR>");
      out.println("<p>&nbsp;</p>");
      out.println("<BR><H3>Input Error</H3><BR>");
      out.println("<BR><BR>You have specified 'Guest Only' and 'Allow Members to Sign Up'.<BR>");
      out.println("You cannot specify both of these options for the same event.<BR>");
      out.println("<BR>Please try again.<BR>");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

    //
    //  Validate minimum team size
    //
    if (minsize > size) {

        out.println(SystemUtils.HeadTitle("Data Entry Error"));
        out.println("<BODY><CENTER>");
        out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
        out.println("<BR><BR>The minimum team size is larger then the team size.");
        out.println("<BR>You entered a team size of " + size + " and a minimum sign-up size of " + minsize + ".");
        out.println("<BR>Please try again.");
        out.println("<br><br><font size=\"2\">");
        out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></font>");
        out.println("</CENTER></BODY></HTML>");
        return;
    }

   //
   //  Validate minute values
   //
   if ((s_min < 0) || (s_min > 59)) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
      out.println("<BR><BR>Start Minute parameter must be in the range of 00 - 59.");
      out.println("<BR>You entered:" + s_min);
      out.println("<BR>Please try again.");
      out.println("<br><br><font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   if ((e_min < 0) || (e_min > 59)) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
      out.println("<BR><BR>End Minute parameter must be in the range of 00 - 59.");
      out.println("<BR>You entered:" + e_min);
      out.println("<BR>Please try again.");
      out.println("<br><br><font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   if ((s_min2 < 0) || (s_min2 > 59)) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
      out.println("<BR><BR>2nd Start Minute parameter must be in the range of 00 - 59.");
      out.println("<BR>You entered:" + s_min2);
      out.println("<BR>Please try again.");
      out.println("<br><br><font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   if ((e_min2 < 0) || (e_min2 > 59)) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
      out.println("<BR><BR>2nd End Minute parameter must be in the range of 00 - 59.");
      out.println("<BR>You entered:" + e_min2);
      out.println("<BR>Please try again.");
      out.println("<br><br><font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   if ((a_min < 0) || (a_min > 59)) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
      out.println("<BR><BR>Actual Minute parameter must be in the range of 00 - 59.");
      out.println("<BR>You entered:" + a_min);
      out.println("<BR>Please try again.");
      out.println("<br><br><font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   if ((su_min < 0) || (su_min > 59)) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
      out.println("<BR><BR>Sign Up Start Minute parameter must be in the range of 00 - 59.");
      out.println("<BR>You entered:" + su_min);
      out.println("<BR>Please try again.");
      out.println("<br><br><font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   if ((c_min < 0) || (c_min > 59)) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
      out.println("<BR><BR>Sign Up Cut-off Minute parameter must be in the range of 00 - 59.");
      out.println("<BR>You entered:" + c_min);
      out.println("<BR>Please try again.");
      out.println("<br><br><font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   if (activity_id == 0 && holes < 9) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
      out.println("<BR><BR>The Number of Holes for the event must be at least 9.");
      out.println("<BR>You entered:" + holes);
      out.println("<BR>Please try again.");
      out.println("<br><br><font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   if (signUp.equals( "Yes" ) && req.getParameter("page2") != null) {

      //
      //  verify that the signup cut-off date is earlier than the events date, skip check for season long events
      //
      if (c_date > date && season == 0) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
         out.println("<BR><BR>The Sign Up Cut-off date cannot be later than the Event date.");
         out.println("<BR>Please try again.");
         out.println("<BR><BR>");
         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      //
      //  verify that the signup cut-off date is later than the start sign-up date
      //
      if (c_date < su_date) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
         out.println("<BR><BR>The Cut-Off date cannot be earlier than the Sign-Up date.");
         out.println("<BR>Please try again.");
         out.println("<BR><BR>");
         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }
   }

   //
   // Make sure if this event is for an activity then it has locations assigned to it.
   //
   if (activity_id != 0 && (locations_csv == null || locations_csv.equals(""))) {

       out.println(SystemUtils.HeadTitle("Data Entry Error"));
       out.println("<BODY><CENTER>");
       out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
       out.println("<BR><BR>You musy specify at least one location for this event.");
       out.println("<BR>Please try again.");
       out.println("<BR><BR>");
       out.println("<font size=\"2\">");
       out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
       out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
       out.println("</form></font>");
       out.println("</CENTER></BODY></HTML>");
       return;

   }


   //
   //  Prompt user for confirmation
   //
   out.println(SystemUtils.HeadTitle("Update Event Confirmation"));
   out.println("<BODY><CENTER><BR>");
   out.println("<br>");
   out.println("<H3>Update Event Confirmation</H3><BR>");
   out.println("<BR>Please confirm the following updated parameters for event:<br><b>" + name + "</b><br><br>");

   out.println("<table border=\"1\" cols=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"7\">");

   if (activity_id == 0) {

       if (!course.equals( "" )) {
          out.println("<tr><td width=\"300\" align=\"right\">");
          out.println("Course Name:");
          out.println("</td><td width=\"300\" align=\"left\">");
          out.println(course);
          out.println("</td></tr>");
       }

       if (season == 0) {
          out.println("<tr><td width=\"300\" align=\"right\">");
          out.println("Tees:");
          out.println("</td><td width=\"300\" align=\"left\">");
          out.println(fb);
          out.println("</td></tr>");
       }

       out.println("<tr><td width=\"300\" align=\"right\">");
       out.println("Season Long:");
       out.println("</td><td width=\"300\" align=\"left\">");
       out.println((season == 1) ? "Yes" : "No");
       out.println("</td></tr>");
   
   } else {

      String tmp = "";
      
      //ArrayList<Integer> locations = new ArrayList<Integer>();
      locations.clear();
      StringTokenizer tok = new StringTokenizer( locations_csv, "," );
      while ( tok.hasMoreTokens() ) {
          tmp = tok.nextToken();
          try {
              locations.add(Integer.parseInt(tmp));
          } catch (Exception exc) {
              out.println("<!-- locations_csv=" + locations_csv + ", tmp=" + tmp  + ", size=" + locations.size() + ", err=" + exc.toString() + " -->");
              return;
          }
      }
      tmp = "";
      int i2 = 0;
      for (i = 0; i < locations.size(); i++) {

          tmp += getActivity.getActivityName(locations.get(i), con) + ", ";
          i2++;
          if (i2 == 4) { tmp += "<br>"; i2 = 0; }
      }

      if (tmp.endsWith("<br>")) {
          tmp = tmp.substring(0, tmp.length() - 6);
      } else if (!tmp.equals("")) {
          tmp = tmp.substring(0, tmp.length() - 2);
      }
      
      out.println("<tr><td width=\"300\" align=\"right\">");
      out.println("Locations:");
      out.println("</td><td width=\"300\" align=\"left\">");
      out.println(tmp);
      out.println("</td></tr>");
       
   }

// if it's a season long event, suppress the un-nessesary information (similar to form)   
if (season == 0) {
   
   out.println("<tr><td width=\"300\" align=\"right\">");
   out.println("Date:");
   out.println("</td><td width=\"300\" align=\"left\">");
   out.println(month + "/" + day + "/" + year);
   out.println("</td></tr>");

   out.println("<tr><td width=\"300\" align=\"right\">");
   out.println("Start Time to Block " + ((activity_id == 0) ? "Tee Times" : "Time Sheets") + ":");
   out.println("</td><td width=\"300\" align=\"left\">");
   out.println(shr + ":" + Utilities.ensureDoubleDigit(s_min) + "  " + ssampm);
   out.println("</td></tr>");

   out.println("<tr><td width=\"300\" align=\"right\">");
   out.println("End Time:");
   out.println("</td><td width=\"300\" align=\"left\">");
   out.println(ehr + ":" + Utilities.ensureDoubleDigit(e_min) + "  " + seampm);
   out.println("</td></tr>");

   out.println("<tr><td width=\"300\" align=\"right\">");
   out.println("Actual Time of Event:");
   out.println("</td><td width=\"300\" align=\"left\">");
   out.println(ahr + ":" + Utilities.ensureDoubleDigit(a_min) + "  " + saampm);
   out.println("</td></tr>");

   if (activity_id == 0 && (!shr2.equals(ehr2) || s_min2 != e_min2)) {
      out.println("<tr><td width=\"300\" align=\"right\">");
      out.println("Tees for 2nd Blocker:");
      out.println("</td><td width=\"300\" align=\"left\">");
      out.println(fb2);
      out.println("</td></tr>");

      out.println("<tr><td width=\"300\" align=\"right\">");
      out.println("Start Time for 2nd Blocker:");
      out.println("</td><td width=\"300\" align=\"left\">");
      out.println(shr2 + ":" + Utilities.ensureDoubleDigit(s_min2) + "  " + ssampm2);
      out.println("</td></tr>");

      out.println("<tr><td width=\"300\" align=\"right\">");
      out.println("End Time for 2nd Blocker:");
      out.println("</td><td width=\"300\" align=\"left\">");
      out.println(ehr2 + ":" + Utilities.ensureDoubleDigit(e_min2) + "  " + seampm2);
      out.println("</td></tr>");
   }

   if (activity_id == 0) {
      out.println("<tr><td width=\"300\" align=\"right\">");
      out.println("Shotgun Event:");
      out.println("</td><td width=\"300\" align=\"left\">");
      out.println((type.equals ( "00" )) ? "No" : "Yes");
      out.println("</td></tr>");
   }

} // end if not season long event

   if (activity_id == 0) {
      out.println("<tr><td width=\"300\" align=\"right\">");
      out.println("Number of holes to be played:");
      out.println("</td><td width=\"300\" align=\"left\">");
      out.println(holes);
      out.println("</td></tr>");
   }

   out.println("<tr><td width=\"300\" align=\"right\">");
   out.println("Guest Only Event:");
   out.println("</td><td width=\"300\" align=\"left\">");
   out.println((gstOnly.equals ( "Yes" ) ? "Yes" : "No"));
   out.println("</td></tr>");

   out.println("<tr><td width=\"300\" align=\"right\">");
   out.println("Members can edit times on tee sheet:");
   out.println("</td><td width=\"300\" align=\"left\">");
   out.println(memedit);
   out.println("</td></tr>");

   out.println("<tr><td width=\"300\" align=\"right\">");
   out.println("Gender Based Event:");
   out.println("</td><td width=\"300\" align=\"left\">");
   out.println(Labels.gender_opts[gender]);
   out.println("</td></tr>");

   out.println("<tr><td width=\"300\" align=\"right\">");
   out.println("Event Categories:");
   out.println("</td><td width=\"300\" align=\"left\">");
   if (category_ids.size() > 0) {
       for (int j=0; j<category_ids.size(); j++) {

           if (j>0) out.println("<br>");

           out.println(Utilities.getEventCategoryNameFromId(category_ids.get(j), con));
       }
   } else {
       out.println("&nbsp;");
   }
   out.println("</td></tr>");
   
   out.println("<tr><td width=\"300\" align=\"right\">");
   out.println("Color:");
   out.println("</td><td width=\"300\" align=\"left\">");
   out.println(color);
   out.println("</td></tr>");

   out.println("<tr><td width=\"300\" align=\"right\">");
   out.println("Itinerary:");
   out.println("</td><td width=\"300\" align=\"left\">");
   out.println(itin);
   out.println("&nbsp;</td></tr>");

   out.println("<tr><td width=\"300\" align=\"right\">");
   out.println("Allow Members to Sign-up Online:");
   out.println("</td><td width=\"300\" align=\"left\">");
   out.println((signUp.equals ( "No" )) ? "No" : "Yes");
   out.println("</td></tr>");

   if (signUp.equals ( "Yes" )) {
       
       out.println("<tr><td width=\"300\" align=\"right\">");
       out.println("Require Guest Names:");
       out.println("</td><td width=\"300\" align=\"left\">");
       out.println((req_guestname == 1) ? "Yes" : "No");
       out.println("</td></tr>");
       
       out.println("<tr><td width=\"300\" align=\"right\">");
       out.println("Handicap Numbers:");
       out.println("</td><td width=\"300\" align=\"left\">");
       out.println((ask_hdcp == 0) ? "No" : (req_hdcp == 1) ? "Required" : "Ask Only");
       out.println("</td></tr>");
       
       out.println("<tr><td width=\"300\" align=\"right\">");
       out.println("Player Gender:");
       out.println("</td><td width=\"300\" align=\"left\">");
       out.println((ask_gender == 0) ? "No" : (req_gender == 1) ? "Required" : "Ask Only");
       out.println("</td></tr>");
       
       out.println("<tr><td width=\"300\" align=\"right\">");
       out.println("Guest Home Club:");
       out.println("</td><td width=\"300\" align=\"left\">");
       out.println((ask_homeclub == 0) ? "No" : (req_homeclub == 1) ? "Required" : "Ask Only");
       out.println("</td></tr>");
       
       out.println("<tr><td width=\"300\" align=\"right\">");
       out.println("Guest Phone Numbers:");
       out.println("</td><td width=\"300\" align=\"left\">");
       out.println((ask_phone == 0) ? "No" : (req_phone == 1) ? "Required" : "Ask Only");
       out.println("</td></tr>");
       
       out.println("<tr><td width=\"300\" align=\"right\">");
       out.println("Guest Address:");
       out.println("</td><td width=\"300\" align=\"left\">");
       out.println((ask_address == 0) ? "No" : (req_address == 1) ? "Required" : "Ask Only");
       out.println("</td></tr>");
       
       out.println("<tr><td width=\"300\" align=\"right\">");
       out.println("Guest Email Address:");
       out.println("</td><td width=\"300\" align=\"left\">");
       out.println((ask_email == 0) ? "No" : (req_email == 1) ? "Required" : "Ask Only");
       out.println("</td></tr>");
       
       out.println("<tr><td width=\"300\" align=\"right\">");
       out.println("Shirt Size:");
       out.println("</td><td width=\"300\" align=\"left\">");
       out.println((ask_shirtsize == 0) ? "No" : (req_shirtsize == 1) ? "Required" : "Ask Only");
       out.println("</td></tr>");
       
       out.println("<tr><td width=\"300\" align=\"right\">");
       out.println("Shoe Size:");
       out.println("</td><td width=\"300\" align=\"left\">");
       out.println((ask_shoesize == 0) ? "No" : (req_shoesize == 1) ? "Required" : "Ask Only");
       out.println("</td></tr>");
       
       out.println("<tr><td width=\"300\" align=\"right\">");
       out.println("Shoe Size:");
       out.println("</td><td width=\"300\" align=\"left\">");
       out.println((ask_shoesize == 0) ? "No" : (req_shoesize == 1) ? "Required" : "Ask Only");
       out.println("</td></tr>");
       
   }
   
   out.println("<tr><td width=\"300\" align=\"right\">");
   out.println("Email #1:");
   out.println("</td><td width=\"300\" align=\"left\">");
   out.println(email1);
   out.println("&nbsp;</td></tr>");
   
   out.println("<tr><td width=\"300\" align=\"right\">");
   out.println("Email #2:");
   out.println("</td><td width=\"300\" align=\"left\">");
   out.println(email2);
   out.println("&nbsp;</td></tr>");
   
   out.println("<tr><td width=\"300\" align=\"right\">");
   out.println("Export Type:");
   out.println("</td><td width=\"300\" align=\"left\">");

   if (export_type == 1) {
      out.println("TPP");
   } else if (export_type == 2) {
      out.println("Event-Man");
   } else if (export_type == 3) {
      out.println("GolfNet TMS");
   } else if (export_type == 4) {
      out.println("TourEx");
   } else if (export_type == 5) {
      out.println("Golf Genius");
   } else {
      out.println("None");       // if 0
   }
   out.println("</td></tr>");
   
   if (signUp.equals ( "Yes" )) {

      out.println("<tr><td width=\"300\" align=\"right\">");
      out.println("Format:");
      out.println("</td><td width=\"300\" align=\"left\">");
      out.println(format);
      out.println("&nbsp;</td></tr>");

      out.println("<tr><td width=\"300\" align=\"right\">");
      out.println("Teams Made By:");
      out.println("</td><td width=\"300\" align=\"left\">");
      out.println(pairings);
      out.println("</td></tr>");

      out.println("<tr><td width=\"300\" align=\"right\">");
      out.println("Team Size:");
      out.println("</td><td width=\"300\" align=\"left\">");
      out.println(ssize);
      out.println("</td></tr>");

      out.println("<tr><td width=\"300\" align=\"right\">");
      out.println("Min. Sign-up Size:");
      out.println("</td><td width=\"300\" align=\"left\">");
      out.println(sminsize);
      out.println("</td></tr>");
      
      out.println("<tr><td width=\"300\" align=\"right\">");
      out.println("Max # of Teams:");
      out.println("</td><td width=\"300\" align=\"left\">");
      out.println(max);
      out.println("</td></tr>");

      out.println("<tr><td width=\"300\" align=\"right\">");
      out.println("Guests Allowed:");
      out.println("</td><td width=\"300\" align=\"left\">");
      out.println(guests);
      out.println("</td></tr>");

      out.println("<tr><td width=\"300\" align=\"right\">");
      out.println("X's Allowed:<br>");
      out.println("Hours To Replace X's:");
      out.println("</td><td width=\"300\" align=\"left\">");
      out.println(x);
      out.println("<br>" + xhrs);
      out.println("</td></tr>");

      out.println("<tr><td width=\"300\" align=\"right\">");
      out.println("Cost per Member:");
      out.println("</td><td width=\"300\" align=\"left\">");
      out.println(memcost);
      out.println("&nbsp;</td></tr>");

      out.println("<tr><td width=\"300\" align=\"right\">");
      out.println("Cost per Guest:");
      out.println("</td><td width=\"300\" align=\"left\">");
      out.println(gstcost);
      out.println("&nbsp;</td></tr>");

      out.println("<tr><td width=\"300\" align=\"right\">");
      out.println("Sign-up Start Date:");
      out.println("</td><td width=\"300\" align=\"left\">");
      out.println(su_month + "/" + su_day + "/" + su_year);
      out.println("</td></tr>");

      out.println("<tr><td width=\"300\" align=\"right\">");
      out.println("Sign-up Start Time:");
      out.println("</td><td width=\"300\" align=\"left\">");
      out.println(su_hr + ":" + Utilities.ensureDoubleDigit(su_min) + " " + su_ampm);
      out.println("</td></tr>");

      out.println("<tr><td width=\"300\" align=\"right\">");
      out.println("Cut-off Date:");
      out.println("</td><td width=\"300\" align=\"left\">");
      out.println(c_month + "/" + c_day + "/" + c_year);
      out.println("</td></tr>");

      out.println("<tr><td width=\"300\" align=\"right\">");
      out.println("Cut-off Time:");
      out.println("</td><td width=\"300\" align=\"left\">");
      out.println(c_hr + ":" + Utilities.ensureDoubleDigit(c_min) + " " + c_ampm);
      out.println("</td></tr>");

      out.println("<tr><td width=\"300\" align=\"right\">");
      out.println("Member Restrictions:");
      out.println("</td><td width=\"300\" align=\"left\">");
      for (i=1; i<parm.MAX_Mems+1; i++) {
         if (!mtype[i].equals( "" )) {
            out.println(mtype[i] + "<br>");
         }
      }
      out.println("&nbsp;</td></tr>");
      out.println("<tr><td width=\"300\" align=\"right\">");
      out.println("Membership Restrictions:");
      out.println("</td><td width=\"300\" align=\"left\">");
      for (i=1; i<parm.MAX_Mships+1; i++) {
         if (!mship[i].equals( "" )) {
            out.println(mship[i] + "<br>");
         }
      }
      out.println("&nbsp;</td></tr>");
   }
   out.println("</table>");

   if (sheet.equals( "yes" )) {
      out.println("<form action=\"Proshop_editevnt\" method=\"post\">");
   } else {
      out.println("<form action=\"Proshop_editevnt\" method=\"post\" target=\"bot\">");
   }
   out.println("<BR>ARE YOU SURE YOU WANT TO UPDATE THIS RECORD?");
    out.println("<input type=\"hidden\" name=\"event_name\" value=\"" + name + "\">");
    out.println("<input type=\"hidden\" name=\"activity_id\" value=\"" + activity_id + "\">");
    out.println("<input type=\"hidden\" name=\"locations_csv\" value=\"" +locations_csv+ "\">");
    out.println("<input type=\"hidden\" name=\"oldName\" value=\"" + oldName + "\">");
    out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
    out.println("<input type=\"hidden\" name=\"oldCourse\" value=\"" + oldCourse + "\">");
    out.println("<input type=\"hidden\" name=\"oldDate\" value=\"" + oldDate + "\">");
    out.println("<input type=\"hidden\" name=\"oldMax\" value=\"" + oldMax + "\">");
    out.println("<input type=\"hidden\" name=\"month\" value=" + month + ">");
    out.println("<input type=\"hidden\" name=\"day\" value=" + day + ">");
    out.println("<input type=\"hidden\" name=\"year\" value=" + year + ">");
    out.println("<input type=\"hidden\" name=\"start_hr\" value=" + shr + ">");
    out.println("<input type=\"hidden\" name=\"start_min\" value=" + smin + ">");
    out.println("<input type=\"hidden\" name=\"start_ampm\" value=" + sampm + ">");
    out.println("<input type=\"hidden\" name=\"end_hr\" value=" + ehr + ">");
    out.println("<input type=\"hidden\" name=\"end_min\" value=" + emin + ">");
    out.println("<input type=\"hidden\" name=\"end_ampm\" value=" + eampm + ">");
    out.println("<input type=\"hidden\" name=\"act_hr\" value=" + ahr + ">");
    out.println("<input type=\"hidden\" name=\"act_min\" value=" + amin + ">");
    out.println("<input type=\"hidden\" name=\"act_ampm\" value=" + aampm + ">");
    out.println("<input type=\"hidden\" name=\"type\" value=" + type + ">");
    out.println("<input type=\"hidden\" name=\"holes\" value=" + holes + ">");
    out.println("<input type=\"hidden\" name=\"color\" value=" + color + ">");
    out.println("<input type=\"hidden\" name=\"signUp\" value=" + signUp + ">");
    out.println("<input type=\"hidden\" name=\"format\" value=\"" + format + "\">");
    out.println("<input type=\"hidden\" name=\"pairings\" value=" + pairings + ">");
    out.println("<input type=\"hidden\" name=\"size\" value=" + ssize + ">");
    out.println("<input type=\"hidden\" name=\"minsize\" value=" + sminsize + ">");
    out.println("<input type=\"hidden\" name=\"max\" value=" + max + ">");
    out.println("<input type=\"hidden\" name=\"guests\" value=" + guests + ">");
    out.println("<input type=\"hidden\" name=\"x\" value=" + x + ">");
    out.println("<input type=\"hidden\" name=\"xhrs\" value=" + xhrs + ">");
    out.println("<input type=\"hidden\" name=\"memcost\" value=\"" + memcost + "\">");
    out.println("<input type=\"hidden\" name=\"gstcost\" value=\"" + gstcost + "\">");
    out.println("<input type=\"hidden\" name=\"su_month\" value=" + su_month + ">");
    out.println("<input type=\"hidden\" name=\"su_day\" value=" + su_day + ">");
    out.println("<input type=\"hidden\" name=\"su_year\" value=" + su_year + ">");
    out.println("<input type=\"hidden\" name=\"su_hr\" value=" + su_hr + ">");
    out.println("<input type=\"hidden\" name=\"su_min\" value=" + su_min + ">");
    out.println("<input type=\"hidden\" name=\"su_ampm\" value=" + su_ampm + ">");
    out.println("<input type=\"hidden\" name=\"c_month\" value=" + c_month + ">");
    out.println("<input type=\"hidden\" name=\"c_day\" value=" + c_day + ">");
    out.println("<input type=\"hidden\" name=\"c_year\" value=" + c_year + ">");
    out.println("<input type=\"hidden\" name=\"c_hr\" value=" + c_hr + ">");
    out.println("<input type=\"hidden\" name=\"c_min\" value=" + c_min + ">");
    out.println("<input type=\"hidden\" name=\"c_ampm\" value=" + c_ampm + ">");
    out.println("<input type=\"hidden\" name=\"itin\" value=\"" + StringEscapeUtils.escapeHtml(itin) + "\">");
    out.println("<input type=\"hidden\" name=\"gstOnly\" value=\"" + gstOnly + "\">");
    out.println("<input type=\"hidden\" name=\"gender\" value=\"" + gender + "\">");
    out.println("<input type=\"hidden\" name=\"season\" value=\"" + season + "\">");
    out.println("<input type=\"hidden\" name=\"export_type\" value=\"" + export_type + "\">");
    out.println("<input type=\"hidden\" name=\"email1\" value=\"" + email1 + "\">");
    out.println("<input type=\"hidden\" name=\"email2\" value=\"" + email2 + "\">");
    out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
    out.println("<input type=\"hidden\" name=\"fb2\" value=\"" + fb2 + "\">");
    out.println("<input type=\"hidden\" name=\"start_hr2\" value=" + shr2 + ">");
    out.println("<input type=\"hidden\" name=\"start_min2\" value=" + smin2 + ">");
    out.println("<input type=\"hidden\" name=\"start_ampm2\" value=" + sampm2 + ">");
    out.println("<input type=\"hidden\" name=\"end_hr2\" value=" + ehr2 + ">");
    out.println("<input type=\"hidden\" name=\"end_min2\" value=" + emin2 + ">");
    out.println("<input type=\"hidden\" name=\"end_ampm2\" value=" + eampm2 + ">");
    out.println("<input type=\"hidden\" name=\"mempos\" value=\"" + mempos + "\">");
    out.println("<input type=\"hidden\" name=\"gstpos\" value=\"" + gstpos + "\">");
    out.println("<input type=\"hidden\" name=\"sheet\" value=\"" + sheet + "\">");
    out.println("<input type=\"hidden\" name=\"memedit\" value=\"" + memedit + "\">");
    
    Common_Config.printEventCategoryHiddenInputs(category_ids, out);

    out.println("<input type=\"hidden\" name=\"ask_homeclub\" value=\"" + ask_homeclub + "\">");
    out.println("<input type=\"hidden\" name=\"ask_phone\" value=\"" + ask_phone + "\">");
    out.println("<input type=\"hidden\" name=\"ask_address\" value=\"" + ask_address + "\">");
    out.println("<input type=\"hidden\" name=\"ask_hdcp\" value=\"" + ask_hdcp + "\">");
    out.println("<input type=\"hidden\" name=\"ask_email\" value=\"" + ask_email + "\">");
    out.println("<input type=\"hidden\" name=\"ask_gender\" value=\"" + ask_gender + "\">");
    out.println("<input type=\"hidden\" name=\"ask_shirtsize\" value=\"" + ask_shirtsize + "\">");
    out.println("<input type=\"hidden\" name=\"ask_shoesize\" value=\"" + ask_shoesize + "\">");
    out.println("<input type=\"hidden\" name=\"ask_otherQ1\" value=\"" + ask_otherQ1 + "\">");
    out.println("<input type=\"hidden\" name=\"ask_otherQ2\" value=\"" + ask_otherQ2 + "\">");
    out.println("<input type=\"hidden\" name=\"ask_otherQ3\" value=\"" + ask_otherQ3 + "\">");

    out.println("<input type=\"hidden\" name=\"req_guestname\" value=\"" + req_guestname + "\">");
    out.println("<input type=\"hidden\" name=\"req_homeclub\" value=\"" + req_homeclub + "\">");
    out.println("<input type=\"hidden\" name=\"req_phone\" value=\"" + req_phone + "\">");
    out.println("<input type=\"hidden\" name=\"req_address\" value=\"" + req_address + "\">");
    out.println("<input type=\"hidden\" name=\"req_hdcp\" value=\"" + req_hdcp + "\">");
    out.println("<input type=\"hidden\" name=\"req_email\" value=\"" + req_email + "\">");
    out.println("<input type=\"hidden\" name=\"req_gender\" value=\"" + req_gender + "\">");
    out.println("<input type=\"hidden\" name=\"req_shirtsize\" value=\"" + req_shirtsize + "\">");
    out.println("<input type=\"hidden\" name=\"req_shoesize\" value=\"" + req_shoesize + "\">");
    out.println("<input type=\"hidden\" name=\"req_otherQ1\" value=\"" + req_otherQ1 + "\">");
    out.println("<input type=\"hidden\" name=\"req_otherQ2\" value=\"" + req_otherQ2 + "\">");
    out.println("<input type=\"hidden\" name=\"req_otherQ3\" value=\"" + req_otherQ3 + "\">");

    out.println("<input type=\"hidden\" name=\"otherQ1\" value=\"" + otherQ1 + "\">");
    out.println("<input type=\"hidden\" name=\"otherQ2\" value=\"" + otherQ2 + "\">");
    out.println("<input type=\"hidden\" name=\"otherQ3\" value=\"" + otherQ3 + "\">");
    
    if (showOld) {
        out.println("<input type=\"hidden\" name=\"showOld\" value=\"Yes\">");
    }
                     
   if (req.getParameter("tmode1") != null) {
      out.println("<input type=\"hidden\" name=\"tmode1\" value=\"yes\">");
   }
   if (req.getParameter("tmode2") != null) {
      out.println("<input type=\"hidden\" name=\"tmode2\" value=\"yes\">");
   }
   if (req.getParameter("tmode3") != null) {
      out.println("<input type=\"hidden\" name=\"tmode3\" value=\"yes\">");
   }
   if (req.getParameter("tmode4") != null) {
      out.println("<input type=\"hidden\" name=\"tmode4\" value=\"yes\">");
   }
   if (req.getParameter("tmode5") != null) {
      out.println("<input type=\"hidden\" name=\"tmode5\" value=\"yes\">");
   }
   if (req.getParameter("tmode6") != null) {
      out.println("<input type=\"hidden\" name=\"tmode6\" value=\"yes\">");
   }
   if (req.getParameter("tmode7") != null) {
      out.println("<input type=\"hidden\" name=\"tmode7\" value=\"yes\">");
   }
   if (req.getParameter("tmode8") != null) {
      out.println("<input type=\"hidden\" name=\"tmode8\" value=\"yes\">");
   }
   if (req.getParameter("tmode9") != null) {
      out.println("<input type=\"hidden\" name=\"tmode9\" value=\"yes\">");
   }
   if (req.getParameter("tmode10") != null) {
      out.println("<input type=\"hidden\" name=\"tmode10\" value=\"yes\">");
   }
   if (req.getParameter("tmode11") != null) {
      out.println("<input type=\"hidden\" name=\"tmode11\" value=\"yes\">");
   }
   if (req.getParameter("tmode12") != null) {
      out.println("<input type=\"hidden\" name=\"tmode12\" value=\"yes\">");
   }
   if (req.getParameter("tmode13") != null) {
      out.println("<input type=\"hidden\" name=\"tmode13\" value=\"yes\">");
   }
   if (req.getParameter("tmode14") != null) {
      out.println("<input type=\"hidden\" name=\"tmode14\" value=\"yes\">");
   }
   if (req.getParameter("tmode15") != null) {
      out.println("<input type=\"hidden\" name=\"tmode15\" value=\"yes\">");
   }
   if (req.getParameter("tmode16") != null) {
      out.println("<input type=\"hidden\" name=\"tmode16\" value=\"yes\">");
   }

   for (i=1; i<parm.MAX_Mems+1; i++) {
      if (req.getParameter("mem" +i) != null) {
         out.println("<input type=\"hidden\" name=\"mem" +i+ "\" value=\"" +mtype[i]+ "\">");
      }
   }
   for (i=1; i<parm.MAX_Mships+1; i++) {
      if (req.getParameter("mship" +i) != null) {
         out.println("<input type=\"hidden\" name=\"mship" +i+ "\" value=\"" +mship[i]+ "\">");
      }
   }

   out.println("<br><input type=\"submit\" value=\"Yes\" name=\"Yes\">");
   out.println("</form><font size=\"2\">");

   if (!sheet.equals( "yes" )) {                      // if not call from Proshop_sheet
      out.println("<p><form method=\"get\" action=\"Proshop_events\">");
      out.println("<input type=\"submit\" value=\"No - Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
      
         if (showOld) {
             out.println("<input type=\"hidden\" name=\"showOld\" value=\"Yes\">");
         }
         
      out.println("</form></p>");
   } else {
      out.println("<form><input type=\"button\" value=\"No - Cancel\" onClick='self.close();'>");
      out.println("</form>");
   }
   out.println("</font><br>");
   out.println("</CENTER></BODY></HTML>");

 }  // wait for confirmation (Yes)


   //*************************************************
   //  removeConf - Request a confirmation for delete
   //*************************************************

 private void removeConf(HttpServletRequest req, PrintWriter out) {


   //
   // Get the name parameter from the hidden input field
   //
   String name = req.getParameter("event_name");
   String course = req.getParameter("course");
   String sheet = req.getParameter("sheet");
   
   boolean showOld = (req.getParameter("showOld") != null) ? true : false;

   //
   //  Prompt user for confirmation
   //
   out.println(SystemUtils.HeadTitle("Delete Event Confirmation"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Remove Event Confirmation</H3><BR>");
   out.println("<BR>Please confirm that you wish to remove event: <b>" + name + "</b><br>");
   out.println("<BR>This will also remove all players from the event sign up database for this event.<br>");

   if (sheet.equals( "yes" )) {
      out.println("<form action=\"Proshop_editevnt\" method=\"post\">");
   } else {
      out.println("<form action=\"Proshop_editevnt\" method=\"post\" target=\"bot\">");
   }
   out.println("<BR>ARE YOU SURE YOU WANT TO DELETE THIS RECORD?");
   out.println("<input type=\"hidden\" name=\"event_name\" value=\"" + name + "\">");
   out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
   out.println("<input type=\"hidden\" name=\"sheet\" value=\"" + sheet + "\">");
   
   if (showOld) {
       out.println("<input type=\"hidden\" name=\"showOld\" value=\"Yes\">");
   }

   out.println("<BR><BR>");
   out.println("<input type=\"submit\" value=\"Delete\" name=\"Delete\">");
   out.println("</form><font size=\"2\">");

   if (!sheet.equals( "yes" )) {                      // if not call from Proshop_sheet
      out.println("<form method=\"get\" action=\"Proshop_events\">");
         
      if (showOld) {
          out.println("<input type=\"hidden\" name=\"showOld\" value=\"Yes\">");
      }
      
      out.println("<input type=\"submit\" value=\"No - Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");
   } else {
      out.println("<form><input type=\"button\" value=\"No - Cancel\" onclick='self.close();'>");
      out.println("</form>");
   }
   out.println("</font>");
   out.println("</CENTER></BODY></HTML>");

 }  // wait for confirmation (Yes)


   //*************************************************************************
   //  Remove an event from events table - received confirmation
   //*************************************************************************

 private void removeEvent(HttpServletRequest req, PrintWriter out, HttpSession sess) {
     
    
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    Connection con = SystemUtils.getCon(sess);            // get DB connection

    if (con == null) {

        out.println(SystemUtils.HeadTitle("DB Connection Error"));
        out.println("<BODY><CENTER><BR>");
        out.println("<BR><BR><H3>Database Connection Error</H3>");
        out.println("<BR><BR>Unable to connect to the Database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<BR><BR>");
        out.println("<a href=\"Proshop_announce\">Home</a>");
        out.println("</CENTER></BODY></HTML>");
        return;
    }

    // Check Feature Access Rights for current proshop user
    if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_EVENT", con, out)) {
         SystemUtils.restrictProshop("SYSCONFIG_EVENT", out);
    }

    int event_id = 0;

    String sid = req.getParameter("event_id");
    try {
    event_id = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}

    int sess_activity_id = (Integer)sess.getAttribute("activity_id");
    String club = (String)sess.getAttribute("club");

    //
    // Get the name parameter from the hidden input field
    //
    String name = req.getParameter("oldName");
    String course = req.getParameter("course");
    String sheet = req.getParameter("sheet");
    
    boolean showOld = (req.getParameter("showOld") != null) ? true : false;

    //
    //  Try to update the event to determine if the name needs to be filtered
    //
    int count = 0;

    try {

        pstmt = con.prepareStatement (
                        "UPDATE events2b SET mship24 = ? " +
                        "WHERE name = ? AND courseName = ?");

        pstmt.clearParameters();
        pstmt.setString(1, "never see this");   // put anything in here   
        pstmt.setString(2, name);
        pstmt.setString(3, course);

        count = pstmt.executeUpdate();

    } catch (Exception exc) { 

        Utilities.logError("Error verifying event name when deleting event_id=" + event_id + ", club=" + club + ", err=" + exc.toString());

    } finally {

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    //
    // IF NO RECORD WAS UPDATED THEN THE NAME MUST BE FILTERED
    //
    if (count == 0) name = SystemUtils.filter(name);          // change any special chars

   
    //
    // DELETE THE EVENT FORM THE EVENT TABLE
    //
    //   NOTE:  We now mark them inactive so we don't lose them (pros often delete events accidently).
    //
    String new_name = "*** " + name;        // change the name in case pro adds a new event with this name
    new_name = truncate(new_name, 30);      // make sure its not too long
    
    try {

      //  pstmt = con.prepareStatement (
      //                  "DELETE FROM events2b WHERE event_id = ?");

        pstmt = con.prepareStatement (
                        "UPDATE events2b SET name = ?, inactive = 1 WHERE event_id = ?");

        pstmt.clearParameters();
        pstmt.setString(1, new_name);
        pstmt.setInt(2, event_id);
        pstmt.executeUpdate();

    } catch (Exception exc) { 

        Utilities.logError("Error deleting event. event_id=" + event_id + ", club=" + club + ", err=" + exc.toString());

    } finally {

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }


    if (sess_activity_id == 0) {
    
        //
        // REMOVE THE EVENT FROM THE TEE SHEETS
        //
        try {

            pstmt = con.prepareStatement (
                            "UPDATE teecurr2 SET event = ?, event_color = ?, event_type = 0 WHERE event = ?");

            pstmt.clearParameters();
            pstmt.setString(1, "");
            pstmt.setString(2, "");
            pstmt.setString(3, name);
            pstmt.executeUpdate();

        } catch (Exception exc) { 

            Utilities.logError("Error removing event from teecurr2, event_id=" + event_id + ", club=" + club + ", err=" + exc.toString());

        } finally {

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }

    } else {
    
        //
        // REMOVE THE EVENT FROM THE TIME SHEETS
        //
        try {

            pstmt = con.prepareStatement (
                            "UPDATE activity_sheets SET event_id = 0 WHERE event_id = ?");

            pstmt.clearParameters();
            pstmt.setInt(1, event_id);
            pstmt.executeUpdate();

        } catch (Exception exc) { 

            Utilities.logError("Error removing event from activity_sheets. event_id=" + event_id + ", club=" + club + ", err=" + exc.toString());

        } finally {

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }

    }

    //
    //  DELETE ALL THE SIGNUPS FOR THIS EVENT
    //
    //   NOTE:  We now mark them inactive so we don't lose them (pros often delete events accidently).
    //
    try {

       // pstmt = con.prepareStatement (
       //                 "DELETE FROM evntsup2b WHERE name = ?");

        pstmt = con.prepareStatement (
                        "UPDATE evntsup2b SET name = ?, inactive = 1 WHERE name = ?");

        pstmt.clearParameters();
        pstmt.setString(1, new_name);     // use new name so we can delete these later (in SystemUtils)
        pstmt.setString(2, name);
        pstmt.executeUpdate();

    } catch (Exception exc) { 

        Utilities.logError("Error deleting signups for event " + name + " with id " + event_id + " for " + club + ".  Err=" + exc.toString());

    } finally {

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    
    //
    // EVENT SUCCESSFULLY DELETED
    //
    out.println(SystemUtils.HeadTitle("Delete Event Confirmation"));
    out.println("<BODY><CENTER><BR>");
    out.println("<p>&nbsp;</p>");
    out.println("<BR><H3>Event Record Has Been Removed</H3><BR>");
    out.println("<BR><BR>Thank you, the event has been removed from the database.");
    out.println("<br><br><font size=\"2\">");
    if (!sheet.equals( "yes" )) {                      // if not call from Proshop_sheet
        out.println("<form method=\"get\" action=\"Proshop_events\">");
        
        if (showOld) {
            out.println("<input type=\"hidden\" name=\"showOld\" value=\"Yes\">");
        }
        
        out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form>");
    } else {
        out.println("<form><input type=\"button\" value=\"Done\" onClick='self.close();'>");
        out.println("</form>");
    }
    out.println("</font>");
    out.println("</CENTER></BODY></HTML>");

 }  // done with delete function


   //*******************************************************************
   //  updateEvent - Update an event from events table - received a conf
   //*******************************************************************

 private void updateEvent(HttpServletRequest req, PrintWriter out, HttpSession sess, HttpServletResponse resp) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   Connection con = SystemUtils.getCon(sess);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_EVENT", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_EVENT", out);
   }

   int sess_activity_id = (Integer)sess.getAttribute("activity_id");

   String user = (String)sess.getAttribute("user");
   String club = (String)sess.getAttribute("club");

   String wplayer1 = "";
   String wplayer2 = "";
   String wplayer3 = "";
   String wplayer4 = "";
   String wplayer5 = "";
   String wuser1 = "";
   String wuser2 = "";
   String wuser3 = "";
   String wuser4 = "";
   String wuser5 = "";

   int month = 0;
   int day = 0;
   int s_hr = 0;
   int s_min = 0;
   int s_ampm = 0;
   int e_hr = 0;
   int e_min = 0;
   int e_ampm = 0;
   int s_hr2 = 0;
   int s_min2 = 0;
   int s_ampm2 = 0;
   int e_hr2 = 0;
   int e_min2 = 0;
   int e_ampm2 = 0;
   int a_hr = 0;
   int a_min = 0;
   int a_ampm = 0;
   int year = 0;
   int type = 0;
   int holes = 0;
   int signUp = 0;
   int size = 0;
   int minsize = 0;
   int max = 0;
   int guests = 0;
   int x = 0;
   int t = 0;
   int i = 0;
   int xhrs = 0;
   int gstOnly = 0;
   int su_month = 0;
   int su_day = 0;
   int su_year = 0;
   int su_hr = 0;
   int su_min = 0;
   int su_time = 0;
   int c_month = 0;
   int c_day = 0;
   int c_year = 0;
   int c_yearOld = 0;
   int c_hr = 0;
   int c_min = 0;
   int c_time = 0;
   //int itinL = 0;
   int oldMax = 0;
   int id = 0;
   //int emailOpt = 0;                        // user's email option parm
   //int send = 0;
   int mc = 0;
   int pc = 0;
   int wa = 0;
   int ca = 0;
   int gender = 0;
   int export_type = 0;
   int activity_id = 0;
   int event_id = 0;

   int [] tmodei = new int [16];   // Modes of trans indicators

   long su_date = 0;
   long c_date = 0;
   long oldDate = 0;
   long oldDateYear = 0;
   
   boolean showOld = false;
      
   ArrayList<Integer> category_ids = new ArrayList<Integer>();  // ArrayList to hold all category_ids for this event

   String omit = "";
   //String to = "";                          // to address
   //String userFirst = "";
   //String userMi = "";
   //String userLast = "";
   //String f_b = "";
   //String f_b2 = "";
   String act_time = "";

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(sess_activity_id, con);

   String [] mship = new String [parm.MAX_Mems+1];            // Mem Types
   String [] mtype = new String [parm.MAX_Mships+1];          // Mship Types

   //
   // Get the parameters entered
   //
   String name = req.getParameter("event_name");
   String oldName = req.getParameter("oldName");
   String s_month = req.getParameter("month");             //  month (00 - 12)
   String s_day = req.getParameter("day");                 //  day (01 - 31)
   String s_year = req.getParameter("year");               //  year (2002 - 20xx)
   String start_hr = req.getParameter("start_hr");         //  start hour (01 - 12)
   String start_min = req.getParameter("start_min");       //  start min (00 - 59)
   String start_ampm = req.getParameter("start_ampm");     //  AM/PM (00 or 12)
   String end_hr = req.getParameter("end_hr");             //   .
   String end_min = req.getParameter("end_min");           //   .
   String end_ampm = req.getParameter("end_ampm");         //   .
   String act_hr = req.getParameter("act_hr");             //   .
   String act_min = req.getParameter("act_min");           //   .
   String act_ampm = req.getParameter("act_ampm");         //   .
   String stype = req.getParameter("type");                //  type of event (shotgun = 1)
   String sholes = req.getParameter("holes");              //  number of holes to be played
   String color = req.getParameter("color");               //  color for the event display
   String course = req.getParameter("course");
   String oldCourse = req.getParameter("oldCourse");
   String soldDate = req.getParameter("oldDate");
   String soldMax = req.getParameter("oldMax");
   String ssignUp = req.getParameter("signUp");
   String format = req.getParameter("format");
   String pairings = req.getParameter("pairings");
   String ssize = req.getParameter("size");
   String sminsize = req.getParameter("minsize");
   String smax = req.getParameter("max");
   String sguests = req.getParameter("guests");
   String sx = req.getParameter("x");
   String sxhrs = req.getParameter("xhrs");
   String memcost = req.getParameter("memcost");
   String gstcost = req.getParameter("gstcost");
   String ssu_month = req.getParameter("su_month");
   String ssu_day = req.getParameter("su_day");
   String ssu_year = req.getParameter("su_year");
   String ssu_hr = req.getParameter("su_hr");
   String ssu_min = req.getParameter("su_min");
   String su_ampm = req.getParameter("su_ampm");
   String sc_month = req.getParameter("c_month");
   String sc_day = req.getParameter("c_day");
   String sc_year = req.getParameter("c_year");
   String sc_hr = req.getParameter("c_hr");
   String sc_min = req.getParameter("c_min");
   String c_ampm = req.getParameter("c_ampm");
   String itin = req.getParameter("itin").trim();
   String guestOnly = req.getParameter("gstOnly");
   String sgender = req.getParameter("gender");
   String fb = (req.getParameter("fb") != null) ? req.getParameter("fb") : "";  //  Front/Back indicator
   String mempos = req.getParameter("mempos");
   String gstpos = req.getParameter("gstpos");
   String start_hr2 = req.getParameter("start_hr2");         //  start hour (01 - 12)
   String start_min2 = req.getParameter("start_min2");       //  start min (00 - 59)
   String start_ampm2 = req.getParameter("start_ampm2");     //  AM/PM (00 or 12)
   String end_hr2 = req.getParameter("end_hr2");             //   .
   String end_min2 = req.getParameter("end_min2");           //   .
   String end_ampm2 = req.getParameter("end_ampm2");         //   .
   String fb2 = req.getParameter("fb2");                     //  Front/Back indicator
   String sheet = req.getParameter("sheet");                 //  From Proshop_sheet?
   String sexport_type = req.getParameter("export_type");
   String email1 = req.getParameter("email1");
   String email2 = req.getParameter("email2");
   String sactivity_id = req.getParameter("activity_id");
   String locations_csv = req.getParameter("locations_csv");
         
   category_ids = Utilities.buildEventCategoryListFromReq(req, sess_activity_id, con);

   int season = (req.getParameter("season") != null && req.getParameter("season").equals("1")) ? 1 : 0;

   int ask_hdcp = (req.getParameter("ask_hdcp") != null && req.getParameter("ask_hdcp").equals("1")) ? 1 : 0;
   int ask_homeclub = (req.getParameter("ask_homeclub") != null && req.getParameter("ask_homeclub").equals("1")) ? 1 : 0;
   int ask_gender = (req.getParameter("ask_gender") != null && req.getParameter("ask_gender").equals("1")) ? 1 : 0;
   int ask_phone = (req.getParameter("ask_phone") != null && req.getParameter("ask_phone").equals("1")) ? 1 : 0;
   int ask_email = (req.getParameter("ask_email") != null && req.getParameter("ask_email").equals("1")) ? 1 : 0;
   int ask_address = (req.getParameter("ask_address") != null && req.getParameter("ask_address").equals("1")) ? 1 : 0;
   int ask_shirtsize = (req.getParameter("ask_shirtsize") != null && req.getParameter("ask_shirtsize").equals("1")) ? 1 : 0;
   int ask_shoesize = (req.getParameter("ask_shoesize") != null && req.getParameter("ask_shoesize").equals("1")) ? 1 : 0;
   int ask_otherQ1 = (req.getParameter("ask_otherQ1") != null && req.getParameter("ask_otherQ1").equals("1")) ? 1 : 0;
   int ask_otherQ2 = (req.getParameter("ask_otherQ2") != null && req.getParameter("ask_otherQ2").equals("1")) ? 1 : 0;
   int ask_otherQ3 = (req.getParameter("ask_otherQ3") != null && req.getParameter("ask_otherQ3").equals("1")) ? 1 : 0;

   int req_guestname = (req.getParameter("req_guestname") != null && req.getParameter("req_guestname").equals("1")) ? 1 : 0;
   int req_hdcp = (req.getParameter("req_hdcp") != null && req.getParameter("req_hdcp").equals("1")) ? 1 : 0;
   int req_homeclub = (req.getParameter("req_homeclub") != null && req.getParameter("req_homeclub").equals("1")) ? 1 : 0;
   int req_gender = (req.getParameter("req_gender") != null && req.getParameter("req_gender").equals("1")) ? 1 : 0;
   int req_phone = (req.getParameter("req_phone") != null && req.getParameter("req_phone").equals("1")) ? 1 : 0;
   int req_email = (req.getParameter("req_email") != null && req.getParameter("req_email").equals("1")) ? 1 : 0;
   int req_address = (req.getParameter("req_address") != null && req.getParameter("req_address").equals("1")) ? 1 : 0;
   int req_shirtsize = (req.getParameter("req_shirtsize") != null && req.getParameter("req_shirtsize").equals("1")) ? 1 : 0;
   int req_shoesize = (req.getParameter("req_shoesize") != null && req.getParameter("req_shoesize").equals("1")) ? 1 : 0;
   int req_otherQ1 = (req.getParameter("req_otherQ1") != null && req.getParameter("req_otherQ1").equals("1")) ? 1 : 0;
   int req_otherQ2 = (req.getParameter("req_otherQ2") != null && req.getParameter("req_otherQ2").equals("1")) ? 1 : 0;
   int req_otherQ3 = (req.getParameter("req_otherQ3") != null && req.getParameter("req_otherQ3").equals("1")) ? 1 : 0;
   
   int memedit = (req.getParameter("memedit") != null && req.getParameter("memedit").equals("Yes")) ? 1 : 0;    // can member edit event times once on tee sheet ?
   
   String otherQ1 = (ask_otherQ1 == 0) ? "" : req.getParameter("otherQ1");
   String otherQ2 = (ask_otherQ2 == 0) ? "" : req.getParameter("otherQ2");
   String otherQ3 = (ask_otherQ3 == 0) ? "" : req.getParameter("otherQ3");
   
   showOld = (req.getParameter("showOld") != null) ? true : false;
   
   // sanity check some of the options
   if (otherQ1 == null) otherQ1 = "";
   if (otherQ2 == null) otherQ2 = "";
   if (otherQ3 == null) otherQ3 = "";
   
   //
   //  indicate parm specified for those that were (walk/cart options)
   //
   if (req.getParameter("tmode1") != null) {

      tmodei[0] = 1;
   } else {
      tmodei[0] = 0;
   }
   if (req.getParameter("tmode2") != null) {

      tmodei[1] = 1;
   } else {
      tmodei[1] = 0;
   }
   if (req.getParameter("tmode3") != null) {

      tmodei[2] = 1;
   } else {
      tmodei[2] = 0;
   }
   if (req.getParameter("tmode4") != null) {

      tmodei[3] = 1;
   } else {
      tmodei[3] = 0;
   }
   if (req.getParameter("tmode5") != null) {

      tmodei[4] = 1;
   } else {
      tmodei[4] = 0;
   }
   if (req.getParameter("tmode6") != null) {

      tmodei[5] = 1;
   } else {
      tmodei[5] = 0;
   }
   if (req.getParameter("tmode7") != null) {

      tmodei[6] = 1;
   } else {
      tmodei[6] = 0;
   }
   if (req.getParameter("tmode8") != null) {

      tmodei[7] = 1;
   } else {
      tmodei[7] = 0;
   }
   if (req.getParameter("tmode9") != null) {

      tmodei[8] = 1;
   } else {
      tmodei[8] = 0;
   }
   if (req.getParameter("tmode10") != null) {

      tmodei[9] = 1;
   } else {
      tmodei[9] = 0;
   }
   if (req.getParameter("tmode11") != null) {

      tmodei[10] = 1;
   } else {
      tmodei[10] = 0;
   }
   if (req.getParameter("tmode12") != null) {

      tmodei[11] = 1;
   } else {
      tmodei[11] = 0;
   }
   if (req.getParameter("tmode13") != null) {

      tmodei[12] = 1;
   } else {
      tmodei[12] = 0;
   }
   if (req.getParameter("tmode14") != null) {

      tmodei[13] = 1;
   } else {
      tmodei[13] = 0;
   }
   if (req.getParameter("tmode15") != null) {

      tmodei[14] = 1;
   } else {
      tmodei[14] = 0;
   }
   if (req.getParameter("tmode16") != null) {

      tmodei[15] = 1;
   } else {
      tmodei[15] = 0;
   }

   for (i=1; i<parm.MAX_Mems+1; i++) {
      mtype[i] = "";
      if (req.getParameter("mem" +i) != null) {

         mtype[i] = req.getParameter("mem" +i);
      }
   }

   for (i=1; i<parm.MAX_Mships+1; i++) {
      mship[i] = "";
      if (req.getParameter("mship" +i) != null) {

         mship[i] = req.getParameter("mship" +i);
      }
   }

   //
   // Convert the numeric string parameters to Int's
   //
   try {
      month = Integer.parseInt(s_month);
      day = Integer.parseInt(s_day);
      year = Integer.parseInt(s_year);
      s_hr = Integer.parseInt(start_hr);
      s_min = Integer.parseInt(start_min);
      s_ampm = Integer.parseInt(start_ampm);
      e_hr = Integer.parseInt(end_hr);
      e_min = Integer.parseInt(end_min);
      e_ampm = Integer.parseInt(end_ampm);
      a_hr = Integer.parseInt(act_hr);
      a_min = Integer.parseInt(act_min);
      a_ampm = Integer.parseInt(act_ampm);
      type = Integer.parseInt(stype);
      holes = Integer.parseInt(sholes);
      oldDate = Integer.parseInt(soldDate);
      oldMax = Integer.parseInt(soldMax);
      s_hr2 = Integer.parseInt(start_hr2);
      s_min2 = Integer.parseInt(start_min2);
      s_ampm2 = Integer.parseInt(start_ampm2);
      e_hr2 = Integer.parseInt(end_hr2);
      e_min2 = Integer.parseInt(end_min2);
      e_ampm2 = Integer.parseInt(end_ampm2);
      gender = Integer.parseInt(sgender);
      export_type = Integer.parseInt(sexport_type);
      activity_id = Integer.parseInt(sactivity_id);

      if (!ssize.equals( "" )) {

         size = Integer.parseInt(ssize);
      }
      if (!sminsize.equals( "" )) {

         minsize = Integer.parseInt(sminsize);
      }
      if (!smax.equals( "" )) {

         max = Integer.parseInt(smax);
      }
      if (!sguests.equals( "" )) {

         guests = Integer.parseInt(sguests);
      }
      if (!sx.equals( "" )) {

         x = Integer.parseInt(sx);
      }
      if (!sxhrs.equals( "" )) {

         xhrs = Integer.parseInt(sxhrs);
      }
      if (!ssu_month.equals( "" )) {

         su_month = Integer.parseInt(ssu_month);
      }
      if (!ssu_day.equals( "" )) {

         su_day = Integer.parseInt(ssu_day);
      }
      if (!ssu_year.equals( "" )) {

         su_year = Integer.parseInt(ssu_year);
      }
      if (!ssu_hr.equals( "" )) {

         su_hr = Integer.parseInt(ssu_hr);
      }
      if (!ssu_min.equals( "" )) {

         su_min = Integer.parseInt(ssu_min);
      }

      if (!sc_month.equals( "" )) {

         c_month = Integer.parseInt(sc_month);
      }
      if (!sc_day.equals( "" )) {

         c_day = Integer.parseInt(sc_day);
      }
      if (!sc_year.equals( "" )) {

         c_year = Integer.parseInt(sc_year);
      }
      if (!sc_hr.equals( "" )) {

         c_hr = Integer.parseInt(sc_hr);
      }
      if (!sc_min.equals( "" )) {

         c_min = Integer.parseInt(sc_min);
      }
   }
   catch (NumberFormatException e) {
   }

   if (guestOnly.equals( "Yes" )) {

      gstOnly = 1;
   }

   if (ssignUp.equals( "Yes" )) {

      signUp = 1;
   }
   
/*
   //
   //  Get the length of Itin (max length of 255 chars)
   //
   if (!itin.equals( "" )) {

      itinL = itin.length();       // get length of itin
   }
*/
   
   //
   //  get the old year for testing
   //
   oldDateYear = oldDate / 10000;      // isolate the old year value

   //
   //  adjust some values for the table
   //
   long date = year * 10000;      // create event date
   date = date + (month * 100);
   date = date + day;             // date = yyyymmdd (for comparisons)

   if (s_hr != 12) {              // _hr specified as 01 - 12 (_ampm = 00 or 12)
      s_hr = s_hr + s_ampm;       // convert to military time (12 is always Noon or PM)
   }
   if (e_hr != 12) {              // ditto
      e_hr = e_hr + e_ampm;
   }
   if (s_hr2 != 12) {              // _hr specified as 01 - 12 (_ampm = 00 or 12)
      s_hr2 = s_hr2 + s_ampm2;       // convert to military time (12 is always Noon or PM)
   }
   if (e_hr2 != 12) {              // ditto
      e_hr2 = e_hr2 + e_ampm2;
   }
   if (a_hr != 12) {              // ditto
      a_hr = a_hr + a_ampm;
   }

   int stime = s_hr * 100;
   stime = stime + s_min;
   int etime = e_hr * 100;
   etime = etime + e_min;
   int stime2 = s_hr2 * 100;
   stime2 = stime2 + s_min2;
   int etime2 = e_hr2 * 100;
   etime2 = etime2 + e_min2;
   int atime = a_hr * 100;
   atime = atime + a_min;

   su_date = su_year * 10000;             // create a date field from input values
   su_date = su_date + (su_month * 100);
   su_date = su_date + su_day;             // date = yyyymmdd (for comparisons)

   if ((su_hr != 12) && (su_ampm.equals( "PM" ))) {   // _hr specified as 01 - 12

      su_hr = su_hr + 12;                             // convert to military time (12 is always Noon or PM)
   }

   if ((su_hr == 12) && (su_ampm.equals( "AM" ))) {   // _hr specified as 01 - 12

      su_hr = 0;                                     // midnight is 00:00
   }

   su_time = su_hr * 100;
   su_time = su_time + su_min;

   c_date = c_year * 10000;             // create a date field from input values
   c_date = c_date + (c_month * 100);
   c_date = c_date + c_day;             // date = yyyymmdd (for comparisons)

   if ((c_hr != 12) && (c_ampm.equals( "PM" ))) {   // _hr specified as 01 - 12

      c_hr = c_hr + 12;                             // convert to military time (12 is always Noon or PM)
   }

   if ((c_hr == 12) && (c_ampm.equals( "AM" ))) {   // _hr specified as 01 - 12

      c_hr = 0;                                     // midnight is 00:00
   }

   c_time = c_hr * 100;
   c_time = c_time + c_min;


   //
   //  Trim double spaces from name.
   //
   name = Utilities.trimDoubleSpaces(name);


   //
   //  Make sure the oldName value is correct
   //
   int count = 0;
   try {

      pstmt = con.prepareStatement (
        "UPDATE events2b SET date = ? " +
        "WHERE name = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setString(2, oldName);

      count = pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR><br>Exception: " + exc.getMessage());
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //  If oldName not found, then filter it and try again
   //
   if (count == 0) {

      oldName = SystemUtils.filter(oldName);          // change any special chars
   }
   
   // Look up the event_id for this event
   try {
       
       pstmt = con.prepareStatement (
               "SELECT event_id " +
               "FROM events2b WHERE name = ?");
       
       pstmt.clearParameters();        // clear the parms
       pstmt.setString(1, oldName);       // put the parm in stmt
       rs = pstmt.executeQuery();      // execute the prepared stmt
       
       if (rs.next()) {
           
           event_id = rs.getInt("event_id");
       }
       
   } catch (Exception exc) {
       Utilities.logError("Proshop_editevnt.updateEvent - " + club + " - Error looking up event_id - ERR: " + exc.toString());
   } finally {
       
       try { rs.close(); }
       catch (Exception ignore) { }
       
       try { pstmt.close(); }
       catch (Exception ignroe) { }
   }
   
   //  If season long event, look up the old cut-off date's year value for comparison later
   if (season == 1) {
       
       try {
           
           pstmt = con.prepareStatement("SELECT c_year FROM events2b WHERE event_id = ?");
           pstmt.clearParameters();
           pstmt.setInt(1, event_id);
           
           rs = pstmt.executeQuery();
           
           if (rs.next()) {
               
               c_yearOld = rs.getInt(1);
           }
           
       } catch (Exception exc) {
           Utilities.logError("Proshop_editevnt.updateEvent - " + club + " - Error looking up old cut-off date for season long event_id (" + event_id + ") - ERR: " + exc.toString());
       } finally {
           
           try { rs.close(); }
           catch (Exception ignore) { }
           
           try { pstmt.close(); }
           catch (Exception ignroe) { }
       }
   }

   //
   //  Udate the record in the events table
   //
   try {
     
      if (signUp == 0) {     // only change the required parms in case they changed signup and want to come back later!!
                             //  except - set max to zero in case this event was copied from a signup event (so it won't show in Pro's event signup list)
        
         pstmt = con.prepareStatement (
           "UPDATE events2b SET date = ?, year = ?, month = ?, day = ?, start_hr = ?, start_min = ?, " +
           "stime = ?, end_hr = ?, end_min = ?, etime = ?, color = ?, type = ?, act_hr = ?, act_min = ?, " +
           "courseName = ?, signUp = ?, max = 0, " +
           "itin = ?, mc = ?, pc = ?, wa = ?, ca = ?, gstOnly = ?, holes = ?, fb = ?, " +
           "mempos = ?, gstpos = ?, " +
           "name = ?, stime2 = ?, etime2 = ?, fb2 = ?, " +
           "gender = ?, export_type = ?, season = ?, email1 = ?, email2 = ?, activity_id = ?, locations = ?, memedit = ? " +
           "WHERE name = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setLong(1, date);
         pstmt.setInt(2, year);
         pstmt.setInt(3, month);
         pstmt.setInt(4, day);
         pstmt.setInt(5, s_hr);
         pstmt.setInt(6, s_min);
         pstmt.setInt(7, stime);
         pstmt.setInt(8, e_hr);
         pstmt.setInt(9, e_min);
         pstmt.setInt(10, etime);
         pstmt.setString(11, color);
         pstmt.setInt(12, type);
         pstmt.setInt(13, a_hr);
         pstmt.setInt(14, a_min);
         pstmt.setString(15, course);
         pstmt.setInt(16, signUp);
         pstmt.setString(17, itin);
         pstmt.setInt(18, mc);
         pstmt.setInt(19, pc);
         pstmt.setInt(20, wa);
         pstmt.setInt(21, ca);
         pstmt.setInt(22, gstOnly);
         pstmt.setInt(23, holes);
         pstmt.setString(24, fb);
         pstmt.setString(25, mempos);
         pstmt.setString(26, gstpos);
         pstmt.setString(27, name);
         pstmt.setInt(28, stime2);
         pstmt.setInt(29, etime2);
         pstmt.setString(30, fb2);
         pstmt.setInt(31, gender);
         pstmt.setInt(32, export_type);
         pstmt.setInt(33, season);
         pstmt.setString(34, email1);
         pstmt.setString(35, email2);
         pstmt.setInt(36, activity_id);
         pstmt.setString(37, locations_csv);
         pstmt.setInt(38, memedit);

         pstmt.setString(39, oldName);

      } else {

         pstmt = con.prepareStatement (
           "UPDATE events2b SET date = ?, year = ?, month = ?, day = ?, start_hr = ?, start_min = ?, " +
           "stime = ?, end_hr = ?, end_min = ?, etime = ?, color = ?, type = ?, act_hr = ?, act_min = ?, " +
           "courseName = ?, signUp = ?, format = ?, pairings = ?, size = ?, max = ?, guests = ?, memcost = ?, " +
           "gstcost = ?, c_month = ?, c_day = ?, c_year = ?, c_hr = ?, c_min = ?, c_date = ?, " +
           "c_time = ?, itin = ?, mc = ?, pc = ?, wa = ?, ca = ?, gstOnly = ?, x = ?, xhrs = ?, holes = ?, " +
           "su_month = ?, su_day = ?, su_year = ?, su_hr = ?, su_min = ?, su_date = ?, su_time = ?, fb = ?, " +
           "mempos = ?, gstpos = ?, tmode1 = ?, tmode2 = ?, tmode3 = ?, tmode4 = ?, tmode5 = ?, tmode6 = ?, " +
           "tmode7 = ?, tmode8 = ?, tmode9 = ?, tmode10 = ?, tmode11 = ?, tmode12 = ?, tmode13 = ?, tmode14 = ?, " +
           "tmode15 = ?, tmode16 = ?, name = ?, stime2 = ?, etime2 = ?, fb2 = ?, " +
           "mem1 = ?, mem2 = ?, mem3 = ?, mem4 = ?, mem5 = ?, mem6 = ?, mem7 = ?, mem8 = ?, " +
           "mem9 = ?, mem10 = ?, mem11 = ?, mem12 = ?, mem13 = ?, mem14 = ?, mem15 = ?, mem16 = ?, " +
           "mem17 = ?, mem18 = ?, mem19 = ?, mem20 = ?, mem21 = ?, mem22 = ?, mem23 = ?, mem24 = ?, " +
           "mship1 = ?, mship2 = ?, mship3 = ?, mship4 = ?, mship5 = ?, mship6 = ?, mship7 = ?, mship8 = ?, " +
           "mship9 = ?, mship10 = ?, mship11 = ?, mship12 = ?, mship13 = ?, mship14 = ?, mship15 = ?, mship16 = ?, " +
           "mship17 = ?, mship18 = ?, mship19 = ?, mship20 = ?, mship21 = ?, mship22 = ?, mship23 = ?, mship24 = ?, " +
           "minsize = ?, gender = ?, export_type = ?, season = ?, email1 = ?, email2 = ?, " +
           "ask_homeclub = ?, ask_phone = ?, ask_address = ?, ask_hdcp = ?, ask_email = ?, ask_gender = ?, ask_shirtsize = ?, ask_shoesize = ?, ask_otherA1 = ?, ask_otherA2 = ?, ask_otherA3 = ?, req_guestname = ?, " +
           "req_homeclub = ?, req_phone = ?, req_address = ?, req_hdcp = ?, req_email = ?, req_gender = ?, req_shirtsize = ?, req_shoesize = ?, req_otherA1 = ?, req_otherA2 = ?, req_otherA3 = ?, otherQ1 = ?, otherQ2 = ?, otherQ3 = ?, " +
           "activity_id = ?, locations = ?, memedit = ? " +
           "WHERE name = ?");
         
         pstmt.clearParameters();        // clear the parms
         pstmt.setLong(1, date);
         pstmt.setInt(2, year);
         pstmt.setInt(3, month);
         pstmt.setInt(4, day);
         pstmt.setInt(5, s_hr);
         pstmt.setInt(6, s_min);
         pstmt.setInt(7, stime);
         pstmt.setInt(8, e_hr);
         pstmt.setInt(9, e_min);
         pstmt.setInt(10, etime);
         pstmt.setString(11, color);
         pstmt.setInt(12, type);
         pstmt.setInt(13, a_hr);
         pstmt.setInt(14, a_min);
         pstmt.setString(15, course);
         pstmt.setInt(16, signUp);
         pstmt.setString(17, format);
         pstmt.setString(18, pairings);
         pstmt.setInt(19, size);
         pstmt.setInt(20, max);
         pstmt.setInt(21, guests);
         pstmt.setString(22, memcost);
         pstmt.setString(23, gstcost);
         pstmt.setInt(24, c_month);
         pstmt.setInt(25, c_day);
         pstmt.setInt(26, c_year);
         pstmt.setInt(27, c_hr);
         pstmt.setInt(28, c_min);
         pstmt.setLong(29, c_date);
         pstmt.setInt(30, c_time);
         pstmt.setString(31, itin);
         pstmt.setInt(32, mc);
         pstmt.setInt(33, pc);
         pstmt.setInt(34, wa);
         pstmt.setInt(35, ca);
         pstmt.setInt(36, gstOnly);
         pstmt.setInt(37, x);
         pstmt.setInt(38, xhrs);
         pstmt.setInt(39, holes);
         pstmt.setInt(40, su_month);
         pstmt.setInt(41, su_day);
         pstmt.setInt(42, su_year);
         pstmt.setInt(43, su_hr);
         pstmt.setInt(44, su_min);
         pstmt.setLong(45, su_date);
         pstmt.setInt(46, su_time);
         pstmt.setString(47, fb);
         pstmt.setString(48, mempos);
         pstmt.setString(49, gstpos);
         pstmt.setInt(50, tmodei[0]);
         pstmt.setInt(51, tmodei[1]);
         pstmt.setInt(52, tmodei[2]);
         pstmt.setInt(53, tmodei[3]);
         pstmt.setInt(54, tmodei[4]);
         pstmt.setInt(55, tmodei[5]);
         pstmt.setInt(56, tmodei[6]);
         pstmt.setInt(57, tmodei[7]);
         pstmt.setInt(58, tmodei[8]);
         pstmt.setInt(59, tmodei[9]);
         pstmt.setInt(60, tmodei[10]);
         pstmt.setInt(61, tmodei[11]);
         pstmt.setInt(62, tmodei[12]);
         pstmt.setInt(63, tmodei[13]);
         pstmt.setInt(64, tmodei[14]);
         pstmt.setInt(65, tmodei[15]);
         pstmt.setString(66, name);
         pstmt.setInt(67, stime2);
         pstmt.setInt(68, etime2);
         pstmt.setString(69, fb2);
         for (i=1; i<parm.MAX_Mems+1; i++) {
            pstmt.setString(69+i, mtype[i]);
         }
         for (i=1; i<parm.MAX_Mships+1; i++) {
            pstmt.setString(93+i, mship[i]);
         }

         pstmt.setInt(118, minsize);
         pstmt.setInt(119, gender);
         pstmt.setInt(120, export_type);
         pstmt.setInt(121, season);
         pstmt.setString(122, email1);
         pstmt.setString(123, email2);
         
         pstmt.setInt(124, ask_homeclub);
         pstmt.setInt(125, ask_phone);
         pstmt.setInt(126, ask_address);
         pstmt.setInt(127, ask_hdcp);
         pstmt.setInt(128, ask_email);
         pstmt.setInt(129, ask_gender);
         pstmt.setInt(130, ask_shirtsize);
         pstmt.setInt(131, ask_shoesize);
         pstmt.setInt(132, ask_otherQ1);
         pstmt.setInt(133, ask_otherQ2);
         pstmt.setInt(134, ask_otherQ3);
         pstmt.setInt(135, req_guestname);
         pstmt.setInt(136, req_homeclub);
         pstmt.setInt(137, req_phone);
         pstmt.setInt(138, req_address);
         pstmt.setInt(139, req_hdcp);
         pstmt.setInt(140, req_email);
         pstmt.setInt(141, req_gender);
         pstmt.setInt(142, req_shirtsize);
         pstmt.setInt(143, req_shoesize);
         pstmt.setInt(144, req_otherQ1);
         pstmt.setInt(145, req_otherQ2);
         pstmt.setInt(146, req_otherQ3);
         pstmt.setString(147, otherQ1);
         pstmt.setString(148, otherQ2);
         pstmt.setString(149, otherQ3);
         pstmt.setInt(150, activity_id);
         pstmt.setString(151, locations_csv);
         pstmt.setInt(152, memedit);
         
         pstmt.setString(153, oldName);
      }

      count = pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();
      
      // Go out and apply the selected event categories for this event
      Utilities.updateEventCategoryBindings(event_id, category_ids, con);

   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR><br>Exception: " + exc.getMessage());
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }


   //
   // Database updated - inform user
   //
   out.println(SystemUtils.HeadTitle("Proshop Edit Event"));
   out.println("<BODY><CENTER><BR>");
   out.println("<BR><BR><H3>Event Has Been Updated</H3>");
   out.println("<BR><BR>Thank you, the event has been updated in the system database.");
/*   
   if (itinL > 255) {

      out.println("<br><br><b>Notice:</b>&nbsp;&nbsp;The Itinerary you entered exceeded 254 characters in length.  All characters beyond 254 will be truncated.");
   }
*/
   if (x > max) {      // if max changed and now there are too many teams signed up

      out.println("<br><br><b>Warning:</b>&nbsp;&nbsp;You changed the max number of teams allowed for this event and ");
      out.println("<br>there are currently more teams registered than are allowed.");
      out.println("<br>Go to Event Sign Up to see the member list for this event.");
   }
   out.println("<br><br><font size=\"2\">");
   if (!sheet.equals( "yes" )) {                      // if not call from Proshop_sheet
      out.println("<form method=\"get\" action=\"Proshop_events\">");
      if (showOld) {
          out.println("<input type=\"hidden\" name=\"showOld\" value=\"Yes\">");
      }
      out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");
   } else {
      out.println("<form><input type=\"button\" value=\"Done\" onClick='self.close();'>");
      out.println("</form>");
   }
   out.println("</font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();

   try {

      resp.flushBuffer();      // force the repsonse to complete

   }
   catch (Exception ignore) {
   }

   // now finish processing
   try {
      //
      //  Create time values for email msg below
      //
      act_ampm = " AM";

      if (a_hr == 0) {

         a_hr = 12;                 // change to 12 AM (midnight)

      } else {

         if (a_hr == 12) act_ampm = " PM";         // change to Noon
         
      }
      if (a_hr > 12) {

         a_hr = a_hr - 12;
         act_ampm = " PM";             // change to 12 hr clock
      }

      //
      //  convert time to hour and minutes for email msg
      //
      act_time = a_hr + ":" + Utilities.ensureDoubleDigit(a_min) + act_ampm;

      //
      //   If the year has changed, then delete all the old entries in the Event Sign Up table. If season long event, base this on the cut-off date's year instead.
      //
      //    NOTE:  We do not delete events or signups - we now just mark them inactive so we can easily recover them
      //
      if (year > oldDateYear || (season == 1 && c_year > c_yearOld)) {

        // pstmt = con.prepareStatement (
        //   "DELETE FROM evntsup2b " +
        //   "WHERE name = ?");

         pstmt = con.prepareStatement (
           "UPDATE evntsup2b SET inactive = 1 " +
           "WHERE name = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setString(1, oldName);

         count = pstmt.executeUpdate();     // execute the prepared stmt

         pstmt.close();

      } else if (date != oldDate) {     // If date has changed within the same year, reset all the active signups to not-moved

          pstmt = con.prepareStatement("UPDATE evntsup2b SET moved = 0 WHERE inactive = 0 AND name = ?");
          pstmt.clearParameters();
          pstmt.setString(1, oldName);

          count = pstmt.executeUpdate();

          pstmt.close();
      }
      //
      //  If the Event Name has changed, then we must change it in the Event Sign Up table (teecurr done below)
      //
      if (!name.equals( oldName )) {

         pstmt = con.prepareStatement (
           "UPDATE evntsup2b SET name = ? " +
           "WHERE name = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setString(1, name);

         pstmt.setString(2, oldName);

         count = pstmt.executeUpdate();     // execute the prepared stmt

         pstmt.close();
      }

      if (activity_id == 0) {

          //
          //  If the Course Name has changed, then we must change it in the Event Sign Up table
          //
          if (!course.equals( oldCourse )) {

             pstmt = con.prepareStatement (
               "UPDATE evntsup2b SET courseName = ? " +
               "WHERE name = ?");

             pstmt.clearParameters();        // clear the parms
             pstmt.setString(1, course);

             pstmt.setString(2, name);

             count = pstmt.executeUpdate();     // execute the prepared stmt

             pstmt.close();
          }
      }

      x = 0;  // init counter
      t = 0;  // init counter

      if (max != oldMax) {     // if max # of teams has changed

         //
         //   Count number of teams currently signed up for this event (not on wait list)
         //
         pstmt = con.prepareStatement (
            "SELECT player1, player2, player3, player4, player5 FROM evntsup2b " +
            "WHERE name = ? AND wait = 0 AND inactive = 0");

         pstmt.clearParameters();        // clear the parms
         pstmt.setString(1, name);
         rs = pstmt.executeQuery();      // execute the prepared pstmt

         while (rs.next()) {

            wplayer1 = rs.getString("player1");
            wplayer2 = rs.getString("player2");
            wplayer3 = rs.getString("player3");
            wplayer4 = rs.getString("player4");
            wplayer5 = rs.getString("player5");

            t = 0;

            if (!wplayer1.equals( "" )) {
               t = 1;
            }
            if (!wplayer2.equals( "" )) {
               t = 1;
            }
            if (!wplayer3.equals( "" )) {
               t = 1;
            }
            if (!wplayer4.equals( "" )) {
               t = 1;
            }
            if (!wplayer5.equals( "" )) {
               t = 1;
            }
            x = x + t;                     // count number of teams
         }
         pstmt.close();
      }

      //
      //  If the max number of teams has changed, then we must check the event sign up list
      //
      int x2 = 0;
      long wdate = 0;
      int wtime = 0;

      if (max > oldMax && x < max) {  // if new max is > old max AND # of teams is < max
         //
         //  the new max is greater than the old - if entries on the wait list, add the earliest ones
         //
         pstmt = con.prepareStatement (
            "SELECT id FROM evntsup2b " +
            "WHERE name = ? AND wait != 0 AND inactive = 0");

         pstmt.clearParameters();        // clear the parms
         pstmt.setString(1, name);
         rs = pstmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            x2 = max - oldMax;        // determine how many we can move (x2 = # of slots now open)
            if ((max - x) < x2) {     // x = # of teams registered (not on wait list)
               x2 = max - x;
            }
         }
         pstmt.close();

         //
         //  allocate a parm block to hold the email parms
         //
         parmEmail parme = new parmEmail();          // allocate an Email parm block

         //
         //  Set the values in the email parm block
         //
         parme.activity_id = sess_activity_id;
         parme.club = club;
         parme.type = "event";         // type = event
         parme.date = date;            // date of event
         parme.time = 0;
         parme.fb = 0;
         parme.mm = month;
         parme.dd = day;
         parme.yy = year;
         parme.name = name;
         parme.etype = 0;
         parme.act_time = act_time;
         parme.wait = 0;
         parme.checkWait = 1;         // call is for Wait List Notification

         parme.season = season;
         
         parme.user = user;
         parme.emailNew = 0;
         parme.emailMod = 0;
         parme.emailCan = 0;

         parme.p91 = 0;                // doesn't matter for event
         parme.p92 = 0;
         parme.p93 = 0;
         parme.p94 = 0;
         parme.p95 = 0;

         parme.course = course;
         parme.day = "";

         parme.player1 = "";
         parme.player2 = "";
         parme.player3 = "";
         parme.player4 = "";
         parme.player5 = "";

         parme.oldplayer1 = "";
         parme.oldplayer2 = "";
         parme.oldplayer3 = "";
         parme.oldplayer4 = "";
         parme.oldplayer5 = "";

         parme.user1 = "";
         parme.user2 = "";
         parme.user3 = "";
         parme.user4 = "";
         parme.user5 = "";

         parme.olduser1 = "";
         parme.olduser2 = "";
         parme.olduser3 = "";
         parme.olduser4 = "";
         parme.olduser5 = "";

         loop1:
         while (x2 > 0) {          // take entries off the waiting list if there are any

            //
            //   get the earliest registration date on wait list
            //
            PreparedStatement stmtw1 = con.prepareStatement (
               "SELECT MIN(r_date) FROM evntsup2b " +
               "WHERE name = ? AND wait != 0 AND inactive = 0");

            stmtw1.clearParameters();        // clear the parms
            stmtw1.setString(1, name);
            rs = stmtw1.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               wdate = rs.getLong(1);
            }
            stmtw1.close();

            if (wdate != 0) {

               //
               //   get the earliest time on this reg date on wait list
               //
               PreparedStatement stmtw2 = con.prepareStatement (
                  "SELECT MIN(r_time) FROM evntsup2b " +
                  "WHERE name = ? AND r_date = ? AND wait != 0 AND inactive = 0");

               stmtw2.clearParameters();        // clear the parms
               stmtw2.setString(1, name);
               stmtw2.setLong(2, wdate);
               rs = stmtw2.executeQuery();      // execute the prepared stmt

               if (rs.next()) {

                  wtime = rs.getInt(1);
               }
               stmtw2.close();

               if (wtime != 0) {

                  guests = 0; // reset & resuse

                  //
                  //   get the id with the earliest time and date on wait list
                  //
                  PreparedStatement stmtw4 = con.prepareStatement (
                     "SELECT username1, username2, username3, username4, username5, id, player1, player2, player3, player4, player5 " +
                     "FROM evntsup2b " +
                     "WHERE name = ? AND r_date = ? AND r_time = ? AND wait != 0 AND inactive = 0");

                  stmtw4.clearParameters();        // clear the parms
                  stmtw4.setString(1, name);
                  stmtw4.setLong(2, wdate);
                  stmtw4.setInt(3, wtime);
                  rs = stmtw4.executeQuery();      // execute the prepared stmt

                  if (rs.next()) {

                     wuser1 = rs.getString("username1");
                     wuser2 = rs.getString("username2");
                     wuser3 = rs.getString("username3");
                     wuser4 = rs.getString("username4");
                     wuser5 = rs.getString("username5");
                     id = rs.getInt("id");

                     // check to see if any are guests - might as well count them since all the parmEmail.guests are set to the # of guests
                     if (wuser1.equals("") && !rs.getString("player1").equals("") && !rs.getString("player1").equalsIgnoreCase("X")) guests++;
                     if (wuser2.equals("") && !rs.getString("player2").equals("") && !rs.getString("player2").equalsIgnoreCase("X")) guests++;
                     if (wuser3.equals("") && !rs.getString("player3").equals("") && !rs.getString("player3").equalsIgnoreCase("X")) guests++;
                     if (wuser4.equals("") && !rs.getString("player4").equals("") && !rs.getString("player4").equalsIgnoreCase("X")) guests++;
                     if (wuser5.equals("") && !rs.getString("player5").equals("") && !rs.getString("player5").equalsIgnoreCase("X")) guests++;

                  }
                  stmtw4.close();

                  PreparedStatement pstmtw3 = con.prepareStatement (
                     "UPDATE evntsup2b SET wait = 0 " +
                     "WHERE name = ? AND id = ?");

                  pstmtw3.clearParameters();        // clear the parms
                  pstmtw3.setString(1, name);
                  pstmtw3.setInt(2, id);

                  count = pstmtw3.executeUpdate();      // execute the prepared stmt

                  pstmtw3.close();

                  //
                  //  send an email to all members on team to inform them of new status
                  //
                  parme.guests = guests;
                  parme.wuser1 = wuser1;     // set Event-only fields
                  parme.wuser2 = wuser2;
                  parme.wuser3 = wuser3;
                  parme.wuser4 = wuser4;
                  parme.wuser5 = wuser5;

                  //
                  //  Send the email
                  //
                  sendEmail.sendIt(parme, con);

               } else {

                  break loop1;                   // done checking - exit while loop
               }

            } else {

               break loop1;                   // done checking - exit while loop
            }

            x2--;        // decrement number to move
            wdate = 0;  // init date
            wtime = 0;  // init date

         }     // end of WHILE x
      }        // end of IF max
   }
   catch (Exception exc) {
   }

   if (activity_id == 0) {
       //
       // Remove the Event from any tee sheets in teecurr table (in case they now need changing)
       //
       try {

          pstmt = con.prepareStatement (
            "UPDATE teecurr2 SET event = ?, event_color = ?, event_type = 0 WHERE event = ?");

          pstmt.clearParameters();               // clear the parms
          pstmt.setString(1,omit);
          pstmt.setString(2,omit);
          pstmt.setString(3, oldName);
          count = pstmt.executeUpdate();         // execute the prepared stmt

          pstmt.close();
       }
       catch (Exception ignore) {
       }
       
   } else {
      
       //
       //  Event is for an Activity - remove the event from any of these activity sheets
       //
       if (event_id > 0) {
           
           try {
               
               //     remove the event from sheets         
               pstmt = con.prepareStatement (
                       "UPDATE activity_sheets " +
                       "SET event_id = 0 " +
                       "WHERE event_id = ?");
               
               pstmt.clearParameters();
               pstmt.setInt(1, event_id);
               pstmt.executeUpdate();
               
               pstmt.close();
               
           }
           catch (Exception ignore) {
           }           
       }
   }

   //
   //  Now, call utility to update tee sheets or time sheets accordingly for this event
   //
   SystemUtils.do1Event(con, name);

 }


   //*******************************************************************
   //  addEvent - Update an event that is being added by a Copy Event
   //*******************************************************************

 private void addEvent(HttpServletRequest req, PrintWriter out, HttpSession sess) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   Connection con = SystemUtils.getCon(sess);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"Proshop_mainleft\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_EVENT", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_EVENT", out);
   }

   String user = (String)sess.getAttribute("user");

   int sess_activity_id = (Integer)sess.getAttribute("activity_id");
/*
   int month = 0;
   int day = 0;
   int s_hr = 0;
   int s_min = 0;
   int s_ampm = 0;
   int e_hr = 0;
   int e_min = 0;
   int e_ampm = 0;
   int s_hr2 = 0;
   int s_min2 = 0;
   int s_ampm2 = 0;
   int e_hr2 = 0;
   int e_min2 = 0;
   int e_ampm2 = 0;
   int a_hr = 0;
   int a_min = 0;
   int a_ampm = 0;
   int year = 0;
   int type = 0;
   int holes = 0;
   int signUp = 0;*/
   int size = 0;
   int minsize = 0;
   int max = 0;
   int guests = 0;
   int x = 0;
   //int t = 0;
   int i = 0;
   int xhrs = 0;
   //int gstOnly = 0;
   int su_month = 0;
   int su_day = 0;
   int su_year = 0;
   int su_hr = 0;
   int su_min = 0;
   int su_time = 0;
   int c_month = 0;
   int c_day = 0;
   int c_year = 0;
   int c_hr = 0;
   int c_min = 0;
   int c_time = 0;
   //int itinL = 0;
   //int oldMax = 0;
   //int id = 0;
   //int emailOpt = 0;                        // user's email option parm
   //int send = 0;
   //int mc = 0;
   //int pc = 0;
   //int wa = 0;
   //int ca = 0;
   int season = 0; // passing this forward because we need to know if we must skip some date validation

   int [] tmodei = new int [16];   // Modes of trans indicators

   long date = 0;
   long su_date = 0;
   long c_date = 0;
   //long oldDate = 0;

   //String omit = "";
   //String to = "";                          // to address
   //String userFirst = "";
   //String userMi = "";
   //String userLast = "";
   //String f_b = "";
   //String f_b2 = "";
   //String act_time = "";

   boolean found = false;

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(sess_activity_id, con);

   String [] mship = new String [parm.MAX_Mems+1];            // Mem Types
   String [] mtype = new String [parm.MAX_Mships+1];          // Mship Types

   //
   // Get the parameters entered
   //
   String name = req.getParameter("event_name");
   String sdate = req.getParameter("date");
   //String course = req.getParameter("course");
   String format = req.getParameter("format");
   String pairings = req.getParameter("pairings");
   String ssize = req.getParameter("size");
   String sminsize = req.getParameter("minsize");
   String smax = req.getParameter("max");
   String sguests = req.getParameter("guests");
   String sx = req.getParameter("x");
   String sxhrs = req.getParameter("xhrs");
   String memcost = req.getParameter("memcost");
   String gstcost = req.getParameter("gstcost");
   String ssu_month = req.getParameter("su_month");
   String ssu_day = req.getParameter("su_day");
   String ssu_year = req.getParameter("su_year");
   String ssu_hr = req.getParameter("su_hr");
   String ssu_min = req.getParameter("su_min");
   String su_ampm = req.getParameter("su_ampm");
   String sc_month = req.getParameter("c_month");
   String sc_day = req.getParameter("c_day");
   String sc_year = req.getParameter("c_year");
   String sc_hr = req.getParameter("c_hr");
   String sc_min = req.getParameter("c_min");
   String c_ampm = req.getParameter("c_ampm");
   String mempos = req.getParameter("mempos");
   String gstpos = req.getParameter("gstpos");
   String sseason = req.getParameter("season");

   // Do some sanity checking on certain values
   if (mempos == null) mempos = "";
   if (gstpos == null) gstpos = "";

   //String sactivity_id = req.getParameter("activity_id");

   //int activity_id = Integer.parseInt(sactivity_id);

   date = Long.parseLong(sdate);            // get date of this event

   //
   //  indicate parm specified for those that were (walk/cart options)
   //
   if (req.getParameter("tmode1") != null) {

      tmodei[0] = 1;
      found = true;
   } else {
      tmodei[0] = 0;
   }
   if (req.getParameter("tmode2") != null) {

      tmodei[1] = 1;
      found = true;
   } else {
      tmodei[1] = 0;
   }
   if (req.getParameter("tmode3") != null) {

      tmodei[2] = 1;
      found = true;
   } else {
      tmodei[2] = 0;
   }
   if (req.getParameter("tmode4") != null) {

      tmodei[3] = 1;
      found = true;
   } else {
      tmodei[3] = 0;
   }
   if (req.getParameter("tmode5") != null) {

      tmodei[4] = 1;
      found = true;
   } else {
      tmodei[4] = 0;
   }
   if (req.getParameter("tmode6") != null) {

      tmodei[5] = 1;
      found = true;
   } else {
      tmodei[5] = 0;
   }
   if (req.getParameter("tmode7") != null) {

      tmodei[6] = 1;
      found = true;
   } else {
      tmodei[6] = 0;
   }
   if (req.getParameter("tmode8") != null) {

      tmodei[7] = 1;
      found = true;
   } else {
      tmodei[7] = 0;
   }
   if (req.getParameter("tmode9") != null) {

      tmodei[8] = 1;
      found = true;
   } else {
      tmodei[8] = 0;
   }
   if (req.getParameter("tmode10") != null) {

      tmodei[9] = 1;
      found = true;
   } else {
      tmodei[9] = 0;
   }
   if (req.getParameter("tmode11") != null) {

      tmodei[10] = 1;
      found = true;
   } else {
      tmodei[10] = 0;
   }
   if (req.getParameter("tmode12") != null) {

      tmodei[11] = 1;
      found = true;
   } else {
      tmodei[11] = 0;
   }
   if (req.getParameter("tmode13") != null) {

      tmodei[12] = 1;
      found = true;
   } else {
      tmodei[12] = 0;
   }
   if (req.getParameter("tmode14") != null) {

      tmodei[13] = 1;
      found = true;
   } else {
      tmodei[13] = 0;
   }
   if (req.getParameter("tmode15") != null) {

      tmodei[14] = 1;
      found = true;
   } else {
      tmodei[14] = 0;
   }
   if (req.getParameter("tmode16") != null) {

      tmodei[15] = 1;
      found = true;
   } else {
      tmodei[15] = 0;
   }

   for (i=1; i<parm.MAX_Mems+1; i++) {
      mtype[i] = "";
      if (req.getParameter("mem" +i) != null) {

         mtype[i] = req.getParameter("mem" +i);
      }
   }

   for (i=1; i<parm.MAX_Mships+1; i++) {
      mship[i] = "";
      if (req.getParameter("mship" +i) != null) {

         mship[i] = req.getParameter("mship" +i);
      }
   }

   //
   // Convert the numeric string parameters to Int's
   //
   try {

      if (!ssize.equals( "" )) {

         size = Integer.parseInt(ssize);
      }
      if (!sminsize.equals( "" )) {

         minsize = Integer.parseInt(sminsize);
      }
      if (!smax.equals( "" )) {

         max = Integer.parseInt(smax);
      }
      if (!sguests.equals( "" )) {

         guests = Integer.parseInt(sguests);
      }
      if (!sx.equals( "" )) {

         x = Integer.parseInt(sx);
      }
      if (!sxhrs.equals( "" )) {

         xhrs = Integer.parseInt(sxhrs);
      }
      if (!ssu_month.equals( "" )) {

         su_month = Integer.parseInt(ssu_month);
      }
      if (!ssu_day.equals( "" )) {

         su_day = Integer.parseInt(ssu_day);
      }
      if (!ssu_year.equals( "" )) {

         su_year = Integer.parseInt(ssu_year);
      }
      if (!ssu_hr.equals( "" )) {

         su_hr = Integer.parseInt(ssu_hr);
      }
      if (!ssu_min.equals( "" )) {

         su_min = Integer.parseInt(ssu_min);
      }

      if (!sc_month.equals( "" )) {

         c_month = Integer.parseInt(sc_month);
      }
      if (!sc_day.equals( "" )) {

         c_day = Integer.parseInt(sc_day);
      }
      if (!sc_year.equals( "" )) {

         c_year = Integer.parseInt(sc_year);
      }
      if (!sc_hr.equals( "" )) {

         c_hr = Integer.parseInt(sc_hr);
      }
      if (!sc_min.equals( "" )) {

         c_min = Integer.parseInt(sc_min);
      }
   }
   catch (NumberFormatException e) {
   }

   if (sseason.equals("1")) season = 1;
   
   //
   //  adjust some values for the table
   //
   su_date = su_year * 10000;             // create a date field from input values
   su_date = su_date + (su_month * 100);
   su_date = su_date + su_day;             // date = yyyymmdd (for comparisons)

   if ((su_hr != 12) && (su_ampm.equals( "PM" ))) {   // _hr specified as 01 - 12

      su_hr = su_hr + 12;                             // convert to military time (12 is always Noon or PM)
   }

   if ((su_hr == 12) && (su_ampm.equals( "AM" ))) {   // _hr specified as 01 - 12

      su_hr = 0;                                     // midnight is 00:00
   }

   su_time = su_hr * 100;
   su_time = su_time + su_min;

   c_date = c_year * 10000;             // create a date field from input values
   c_date = c_date + (c_month * 100);
   c_date = c_date + c_day;             // date = yyyymmdd (for comparisons)

   if ((c_hr != 12) && (c_ampm.equals( "PM" ))) {   // _hr specified as 01 - 12

      c_hr = c_hr + 12;                             // convert to military time (12 is always Noon or PM)
   }

   if ((c_hr == 12) && (c_ampm.equals( "AM" ))) {   // _hr specified as 01 - 12

      c_hr = 0;                                     // midnight is 00:00
   }

   c_time = c_hr * 100;
   c_time = c_time + c_min;

   //
   //   Validate the parms received
   //
   if (sess_activity_id == 0 && found == false) {    // if no Modes of Trans were specified

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Error - Mode of Transportation Not Specified</H3>");
      out.println("<BR><BR>If members are allowed to signup for the event online,");
      out.println("<BR>you must specify the modes of transportation that will be allowed for the event.");
      out.println("<BR><br>Please correct and try again.");
      out.println("<br><br><font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

    //
    //  Validate minimum team size
    //
    if (minsize > size) {

        out.println(SystemUtils.HeadTitle("Data Entry Error"));
        out.println("<BODY><CENTER>");
        out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
        out.println("<BR><BR>The minimum team size is larger then the team size.");
        out.println("<BR>You entered a team size of " + size + " and a minimum sign-up size of " + minsize + ".");
        out.println("<BR>Please try again.");
        out.println("<br><br><font size=\"2\">");
        out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></font>");
        out.println("</CENTER></BODY></HTML>");
        return;
    }
   
   if ((su_min < 0) || (su_min > 59)) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
      out.println("<BR><BR>Sign Up Start Minute parameter must be in the range of 00 - 59.");
      out.println("<BR>You entered:" + su_min);
      out.println("<BR>Please try again.");
      out.println("<br><br><font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   if ((c_min < 0) || (c_min > 59)) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
      out.println("<BR><BR>Sign Up Cut-off Minute parameter must be in the range of 00 - 59.");
      out.println("<BR>You entered:" + c_min);
      out.println("<BR>Please try again.");
      out.println("<br><br><font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //  verify that the signup cut-off date is earlier than the events date, skip this check for season long events
   //
   if (c_date > date && season == 0) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
      out.println("<BR><BR>The Sign Up Cut-off date cannot be later than the Event date.");
      out.println("<BR>Please try again.");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //  verify that the signup cut-off date is later than the start sign-up date
   //
   if (c_date < su_date) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
      out.println("<BR><BR>The Cut-Off date cannot be earlier than the Sign-Up date.");
      out.println("<BR>Please try again.");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //  Udate the record in the events table
   //
   int count = 0;
   try {

      pstmt = con.prepareStatement (
        "UPDATE events2b SET " +
        "format = ?, pairings = ?, size = ?, max = ?, guests = ?, memcost = ?, " +
        "gstcost = ?, c_month = ?, c_day = ?, c_year = ?, c_hr = ?, c_min = ?, c_date = ?, " +
        "c_time = ?, x = ?, xhrs = ?, " +
        "su_month = ?, su_day = ?, su_year = ?, su_hr = ?, su_min = ?, su_date = ?, su_time = ?, " +
        "mempos = ?, gstpos = ?, tmode1 = ?, tmode2 = ?, tmode3 = ?, tmode4 = ?, tmode5 = ?, tmode6 = ?, " +
        "tmode7 = ?, tmode8 = ?, tmode9 = ?, tmode10 = ?, tmode11 = ?, tmode12 = ?, tmode13 = ?, tmode14 = ?, " +
        "tmode15 = ?, tmode16 = ?, " +
        "mem1 = ?, mem2 = ?, mem3 = ?, mem4 = ?, mem5 = ?, mem6 = ?, mem7 = ?, mem8 = ?, " +
        "mem9 = ?, mem10 = ?, mem11 = ?, mem12 = ?, mem13 = ?, mem14 = ?, mem15 = ?, mem16 = ?, " +
        "mem17 = ?, mem18 = ?, mem19 = ?, mem20 = ?, mem21 = ?, mem22 = ?, mem23 = ?, mem24 = ?, " +
        "mship1 = ?, mship2 = ?, mship3 = ?, mship4 = ?, mship5 = ?, mship6 = ?, mship7 = ?, mship8 = ?, " +
        "mship9 = ?, mship10 = ?, mship11 = ?, mship12 = ?, mship13 = ?, mship14 = ?, mship15 = ?, mship16 = ?, " +
        "mship17 = ?, mship18 = ?, mship19 = ?, mship20 = ?, mship21 = ?, mship22 = ?, mship23 = ?, mship24 = ?, minsize = ? " +
        "WHERE name = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, format);
      pstmt.setString(2, pairings);
      pstmt.setInt(3, size);
      pstmt.setInt(4, max);
      pstmt.setInt(5, guests);
      pstmt.setString(6, memcost);
      pstmt.setString(7, gstcost);
      pstmt.setInt(8, c_month);
      pstmt.setInt(9, c_day);
      pstmt.setInt(10, c_year);
      pstmt.setInt(11, c_hr);
      pstmt.setInt(12, c_min);
      pstmt.setLong(13, c_date);
      pstmt.setInt(14, c_time);
      pstmt.setInt(15, x);
      pstmt.setInt(16, xhrs);
      pstmt.setInt(17, su_month);
      pstmt.setInt(18, su_day);
      pstmt.setInt(19, su_year);
      pstmt.setInt(20, su_hr);
      pstmt.setInt(21, su_min);
      pstmt.setLong(22, su_date);
      pstmt.setInt(23, su_time);
      pstmt.setString(24, mempos);
      pstmt.setString(25, gstpos);
      pstmt.setInt(26, tmodei[0]);
      pstmt.setInt(27, tmodei[1]);
      pstmt.setInt(28, tmodei[2]);
      pstmt.setInt(29, tmodei[3]);
      pstmt.setInt(30, tmodei[4]);
      pstmt.setInt(31, tmodei[5]);
      pstmt.setInt(32, tmodei[6]);
      pstmt.setInt(33, tmodei[7]);
      pstmt.setInt(34, tmodei[8]);
      pstmt.setInt(35, tmodei[9]);
      pstmt.setInt(36, tmodei[10]);
      pstmt.setInt(37, tmodei[11]);
      pstmt.setInt(38, tmodei[12]);
      pstmt.setInt(39, tmodei[13]);
      pstmt.setInt(40, tmodei[14]);
      pstmt.setInt(41, tmodei[15]);
      for (i=1; i<parm.MAX_Mems+1; i++) {
         pstmt.setString(41+i, mtype[i]);
      }
      for (i=1; i<parm.MAX_Mships+1; i++) {
         pstmt.setString(65+i, mship[i]);
      }

      pstmt.setInt(90, minsize);
      pstmt.setString(91, name);

      count = pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR><br>Exception: " + exc.getMessage());
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"Proshop_mainleft\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   // Database updated - inform user
   //
   out.println(SystemUtils.HeadTitle("Proshop Edit Event"));
   out.println("<BODY><CENTER><BR>");
   out.println("<BR><BR><H3>Event Has Been Added</H3>");
   out.println("<BR><BR>Thank you, the event has been added to the system database.");
   out.println("<br><br><font size=\"2\">");
   out.println("<form method=\"get\" action=\"Proshop_events\">");
   out.println("<input type=\"hidden\" name=\"copy\" value=\"yes\">");
   out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form>");
   out.println("</font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();

   //
   //  Now, call utility to scan the event table and update tee slots in teecurr accordingly
   //
   SystemUtils.do1Event(con, name);

 }


 // *********************************************************
 //  Return a string with the specified length from a possibly longer field
 // *********************************************************

 private final static String truncate( String s, int slength ) {

      char[] ca = s.toCharArray();
      char[] ca2 = new char [slength];


      if (slength < ca.length) {       // if string is longer than allowed

         for ( int i=0; i<slength; i++ ) {
            ca2[i] = ca[i];
         } // end for

      } else {

         return (s);
      }

      return new String (ca2);

 } // end truncate

 
 // *********************************************************
 // Event already exists
 // *********************************************************

 private void dupEvent(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, the <b>event name</b> you specified already exists in the database.<BR>");
   out.println("<BR>The name must be unique.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
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
   out.println("<BR><BR>Error: " +exc.getMessage());
   out.println("<BR>Please try again later.");
   out.println("<BR><BR>If problem persists, contact customer support.");
   out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }

}
