/***************************************************************************************
 *   Member_activity_slot:  This servlet will display and process an Activity
 *                           registration request form from the Member
 *
 *
 *   Called by:     Member_gensheets
 *                  self on cancel request
 *
 *
 *   Created:       12/19/2008
 *
 *   Last Updated:
 *
 *         7/12/10  If players present in the slot that was clicked and 'slots' > 0, default 'slots' to 0 to so original time is
 *                  loaded instead of trying to look for block of open times.
 *         6/24/10  Modified alphaTable calls to pass the new enableAdvAssist parameter which is used for iPad compatability
 *         4/19/10  Updated moveguest Javascript function to handle the new use_guestdb value being passed to it
 *         4/09/10  Make sure that the time passed to sendEmail is for the starting time if reservation is for consecutive times.
 *         3/11/10  Added custom for pattersonclub - set max_players to 2 if consec is 2 and here for paddle (REMOVED ON 3-27-10)
 *        12/28/09  Added support for processing consecutive times
 *        12/21/09  Updated return to sheet code in Cancel & Verify - no longer passing back parent_id or group_id
 *        12/06/09  Force Singles Match checkbox only appears now if enabled in config
 *        12/02/09  Call to alphaTable.displayPartnerList added to print the partner list, outdated code removed
 *        10/16/09  Return the layout option to sheet and return automatically on the Go Back (1 sec). 
 *        10/08/09  Enforce the Singles option.
 *        10/04/09  Added activity isolation to the buddy list
 *         9/07/09  Removed references to Golf and Tee Times.
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
import com.foretees.common.parmSlot;
import com.foretees.common.verifySlot;
import com.foretees.common.parmEmail;
import com.foretees.common.sendEmail;
import com.foretees.common.alphaTable;
import com.foretees.common.parmActivity;
import com.foretees.common.getActivity;
import com.foretees.common.verifyActSlot;
import com.foretees.common.Utilities;


public class Member_activity_slot extends HttpServlet {

 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 
 //*****************************************************
 // Process the request from Member_sheet
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


    doPost(req, resp);

 }     // done


 //*****************************************************
 // Process the request from doGet above and processing below
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
        
   ResultSet rs = null;
   PreparedStatement pstmt = null;

   HttpSession session = SystemUtils.verifyMem(req, out);             // check for intruder

   if (session == null) return;
       
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

   //
   //  Get this session's username (to be saved in teecurr)
   //
   String name = (String)session.getAttribute("name");          // get users full name
   //String userMship = (String)session.getAttribute("mship");    // get users mship type
   //String mtype = (String)session.getAttribute("mtype");        // get users mtype
   String club = (String)session.getAttribute("club");
   String user = (String)session.getAttribute("user");
   int sess_activity_id = (Integer)session.getAttribute("activity_id");
   
   //
   // Process request according to which 'submit' button was selected
   //
   //      'cancel' - a cancel request from user via Member_activity_slot
   //      'submitForm' - a reservation request from Member_activity_slot
   //      'remove' - a 'cancel reservation' request from Member_activity_slot (remove all names)
   //      'letter' - a request to list member names from Member_activity_slot
   //      'return' - a return to Member_activity_slot from verify (from a skip)
   //
   
   // process cancel request
   if (req.getParameter("cancel") != null) {

      cancel(req, out, club, user, con);
      return;
   }
   
   // process reservation requests
   if ((req.getParameter("submitForm") != null) || (req.getParameter("remove") != null)) {

      verify(req, out, con, session, resp);
      return;
   }


   boolean enableAdvAssist = Utilities.enableAdvAssist(req);

   //
   //  parm block to hold the activity time parms
   //
   parmSlot slotParms = new parmSlot();

   slotParms.club = club;                        // save club name

   //
   //  Request from Member_gensheet, Member_activity_slot or Member_searchmem
   //
   int slot_id = 0;
   int group_id = 0;
   int in_use = 0;
   int time = 0;
   int hide = 0;
   int disallow_joins = 0;
   int layout_mode = 0;
   int consec = 0;

   long mm = 0;
   long dd = 0;
   long yy = 0;
   long temp = 0;
   long date = 0;

   String sdate = "";
   String stime = "";
   String notes = "";
   String hides = "";
   String jump = "0";                     // jump index - default to zero (for _sheet)
   String orig_by = "";
   String last_user = "";
   String custom1 = "";
   String custom2 = "";
   String custom3 = "";
   String custom4 = "";
   String custom5 = "";

   boolean first_call = true;                   // default to first time thru   
   boolean new_reservation = false;
   
   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(sess_activity_id, con);
   
   //
   // Get all the parameters entered
   //
    String day_name = "";
    String index = "";

    if (req.getParameter("index") != null) {
      index = req.getParameter("index");
    }

    if (req.getParameter("jump") != null) {             // if jump index provided

        jump = req.getParameter("jump");
    }

    if (req.getParameter("custom1") != null) {          // custom parms added for Interlachen, but can be used by others too
      custom1 = req.getParameter("custom1");
    }
    if (req.getParameter("custom2") != null) {
      custom2 = req.getParameter("custom2");
    }
    if (req.getParameter("custom3") != null) {
      custom3 = req.getParameter("custom3");
    }
    if (req.getParameter("custom4") != null) {
      custom4 = req.getParameter("custom4");
    }
    if (req.getParameter("custom5") != null) {
      custom5 = req.getParameter("custom5");
    }

    String sid = req.getParameter("slot_id");           // sheet_id in activities_sheet (id of this slot on the sheet) 
    if (sid == null) sid = "0";
    try { slot_id = Integer.parseInt(sid); }
    catch (NumberFormatException e) {}

    sid = req.getParameter("group_id");                 // Group that the sheet belongs to
    if (sid == null) sid = "0";
    try { group_id = Integer.parseInt(sid); }
    catch (NumberFormatException e) {}

    if (req.getParameter("layout_mode") != null) {      // if layout_mode passed from sheet

        sid = req.getParameter("layout_mode");
        try { layout_mode = Integer.parseInt(sid); }
        catch (NumberFormatException e) {}
    }
    if (req.getParameter("disallow_joins") != null) {   // user wish to disallow other from joining their reservation

        sid = req.getParameter("disallow_joins");
        try { disallow_joins = Integer.parseInt(sid); }
        catch (NumberFormatException e) {}
    }
    if (req.getParameter("consec") != null) {           // if consecutive times requested from sheet

        sid = req.getParameter("consec");
        try { consec = Integer.parseInt(sid); }
        catch (NumberFormatException e) {}
    }

    // store the consecutive value in slotParms - 0 or 1 = 0, > 1 then assign it
    slotParms.slots = (consec > 1) ? consec : 0;

    slotParms.layout_mode = layout_mode;

    parmActivity parmAct = new parmActivity();              // allocate a parm block
    parmAct.slot_id = slot_id;                              // pass in the slot id so we can determin which activity to load parms for

    // get the activity config
    try { getActivity.getParms(con, parmAct); }
    catch (Exception e1) { out.println("<BR><BR>" + e1.getMessage()); }

    //
    // CUSTOM: if pattersonclub and root activity is paddle courts then overide the max_players depending on the consec being passed in
    //
    //if (club.equals("pattersonclub") && sess_activity_id == 9 && consec == 2)  parmAct.max_players = 2;

    // See if there are existing players for this slot and override the slots value if so
    if (slotParms.slots > 0 && verifyActSlot.checkSlotHasPlayers(parmAct.slot_id, con)) {
        slotParms.slots = 0;
    }
    
    String [] player = new String[parmAct.max_players + 1];
    String [] mem = new String[parmAct.max_players + 1];          // Name of Member associated with a guest player

    //
    //   Save club info in club parm table
    //
    parm.club = club;
    //parm.course = course;

    //
    //  Convert the values from string to int
    //
    try { date = Long.parseLong(sdate); }
    catch (NumberFormatException ignore) {}

    //
    //  Get this year
    //
    Calendar cal = new GregorianCalendar();       // get todays date
    int thisYear = cal.get(Calendar.YEAR);            // get the year


    //
    //  if this is a call from self - user clicked on a letter or a return from verify
    //
    if ((req.getParameter("letter") != null) || (req.getParameter("return") != null)) {

        first_call = false;       // indicate NOT first call so we don't plug user's name into empty slot
    }


    //
    // Populate our local vars depending if we are coming in for the first time or reloading page
    //
    if ((req.getParameter("letter") != null) || (req.getParameter("return") != null) ||
       (req.getParameter("mtypeopt") != null) || (req.getParameter("memNotice") != null) ||
       (req.getParameter("promptOtherTime") != null)) {

      // we've been here before so get vars from request object
      for (int x = 1; x <= parmAct.max_players; x++) {
          player[x] = req.getParameter("player"+x);
          if (player[x] == null) player[x] = "";    // sanity
      }
      
      notes = req.getParameter("notes");
      orig_by = req.getParameter("orig_by");

      if (req.getParameter("mem1") != null) {
         mem[1] = req.getParameter("mem1");
      }
      if (req.getParameter("mem2") != null) {
         mem[2] = req.getParameter("mem2");
      }
      if (req.getParameter("mem3") != null) {
         mem[3] = req.getParameter("mem3");
      }
      if (req.getParameter("mem4") != null) {
         mem[4] = req.getParameter("mem4");
      }
      if (req.getParameter("mem5") != null) {
         mem[5] = req.getParameter("mem5");
      }

      if (req.getParameter("date") != null) {
         sdate = req.getParameter("date");
      }
      try {
         date = Integer.parseInt(sdate);
      } catch (NumberFormatException ignore) {}

      if (req.getParameter("time") != null) {
         stime = req.getParameter("time");
      }
      try {
         time = Integer.parseInt(stime);
      } catch (NumberFormatException ignore) {}

      if (req.getParameter("activity_id") != null) {

         slotParms.activity_id = Integer.parseInt(req.getParameter("activity_id"));     // sheet id
      }
      
      // set stime to human readable time
      stime = SystemUtils.getSimpleTime(time);

      day_name = req.getParameter("day");         //  name of the day

      if (req.getParameter("index") != null) {
         index = req.getParameter("index"); // still use index to control where we return to
      }

      //
      //  Convert hide from string to int
      //
      hide = 0;                       // init to No
      if (hides.equals( "Yes" )) hide = 1;

      if (req.getParameter("in_slots") != null) {

          //slotParms.slots = Integer.parseInt(req.getParameter("consec"));
          slotParms.in_slots = req.getParameter("in_slots");

          // populate slotParms.sheet_ids array
          StringTokenizer tok = new StringTokenizer( slotParms.in_slots, "," );
          while ( tok.hasMoreTokens() ) {
              slotParms.sheet_ids.add(Integer.parseInt(tok.nextToken()));
              slotParms.slots++;
              //out.println("<!-- adding " + slotParms.sheet_ids.get(slotParms.slots - 1) + " to slotParms.sheet_id (" + slotParms.slots + ") -->");
          }
          //out.println("<!-- slotParms.sheet_ids.size()=" + slotParms.sheet_ids.size() + " -->");
      }

      // see if we need to adjust slot_id
      if (req.getParameter("promptOtherTime") != null) {

          // the user has requested times that were not available but they have
          // decided to accept the times the system was able to find for them
          // so we need to adjust slot_id - slotParms.slot_id will be set later
          slot_id = slotParms.sheet_ids.get(0);
          out.println("<!-- updating slot_id to newly accepting time. new slot_id=" + slot_id + " -->");
      }

   } else {
     
       // First time here!
       // Load up slotParm with details from this slot_id

      
/*      THESE ARE NOT PASSED IN - INSTEAD THEY ARE FOUND WHEN WE CHECK TO SEE IF SLOT IS IN USE
      slotParms.day = day_name;
      slotParms.index = index;          
      slotParms.course = course;
      slotParms.returnCourse = returnCourse;
      slotParms.jump = jump;
      slotParms.date = date;
      slotParms.fb = fb;
      slotParms.time = time;
*/
       
      //
      //  Verify the required parms exist
      //
      //String in_slots = "";
      if (slot_id == 0) {

         //SystemUtils.logError("Error in Member_activity_slot: Missing slot_id parameter!");                                   // log it
         out.println("<h2 align=center>Error in Member_activity_slot: Missing slot_id parameter!</h2>");
         in_use = 1;          // make like the time is busy

      } else {               // continue if parms ok

         if (slotParms.slots < 2) {

             try {

                 in_use = verifyActSlot.checkInUse(slot_id, user, slotParms, con, out);

             } catch (Exception e1) {

                 //SystemUtils.logError("Error in Member_activity_slot: Check in use flag failed - Exception: " + e1.getMessage());
                 out.println("<h2 align=center>Error in Member_activity_slot: Check in use flag failed - Exception: " + e1.getMessage() + "</h2>");
                 in_use = 1;          // make like the time is busy

             }

         } else {

             try {

                 in_use = verifyActSlot.checkInUseM(slot_id, user, slotParms, con, out);

             } catch (Exception e1) {

                 //SystemUtils.logError("Error in Member_activity_slot: Check in use flag failed - Exception: " + e1.getMessage());
                 out.println("<h2 align=center>Error2 in Member_activity_slot: Check in use flag failed - Exception: " + e1.getMessage() + "</h2>");
                 in_use = 1;          // make like the time is busy

             }

             //
             //  If we did not get the exact tee time requested, then ask the user if they want to proceed or go back.
             //
             if (in_use == 9) {

                 slotParms.group_id = group_id;
                 promptOtherTime(out, slotParms, con);
                 return;

             } else if (in_use == 1) {

                out.println(SystemUtils.HeadTitle("Data Entry Error"));
                out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                out.println("<center>");
                out.println("<BR><BR><H3>Times Unavailable</H3>");
                out.println("<BR><BR>Sorry but we were unable to find enough consecutive times to fulfill your request.");
                out.println("<BR><BR>Please return to the time sheet and select another time.");
                out.println("<BR><BR>");

                out.println("<form action=\"/" +rev+ "/servlet/Member_jump\" method=\"post\">");
                out.println("<input type=\"hidden\" name=\"activity\" value=\"\">");
                out.println("<input type=\"hidden\" name=\"activity_id\" value=\"" + slotParms.group_id + "\">"); // pass the group_id to _jump as the activit_id
                out.println("<input type=\"hidden\" name=\"layout_mode\" value=\"" + slotParms.layout_mode + "\">");
                out.println("<input type=\"hidden\" name=\"date\" value=\"" + slotParms.date + "\">");
                out.println("<input type=\"submit\" value=\"Return to Time Sheet\"></form>");

                return;

             }
         }
         
      }

      if (in_use != 0) {              // if time slot already in use

         out.println(SystemUtils.HeadTitle("Time Slot In Use Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<CENTER><BR><BR><H2>Time Slot Busy</H2>");
         out.println("<BR><BR>Sorry, but this time slot is currently busy.<BR>");
         out.println("<BR>Please select another time or try again later.");
         out.println("<BR><BR>");
         out.println("<font size=\"2\">");
         
         if (index.equals( "888" ) || index.equals( "998" ) || index.equals( "999" )) {       // if originated from Member_searcmem or teelist
            out.println("<form action=\"/" +rev+ "/servlet/Member_jump\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"activity\" value=\"\">");
            out.println("<input type=\"hidden\" name=\"activity_id\" value=\"" + slotParms.group_id + "\">");
            out.println("<input type=\"hidden\" name=\"layout_mode\" value=\"" + slotParms.layout_mode + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + slotParms.date + "\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\"></form>");
         } else {
            out.println("<button onclick=\"window.history.go(-1);\">Return</button>");
         }
         out.println("</font></CENTER></BODY></HTML>");
         out.close();
         return;
      }

      if ( parmAct.max_players >= 1  ) player[1]  = slotParms.player1;
      if ( parmAct.max_players >= 2  ) player[2]  = slotParms.player2;
      if ( parmAct.max_players >= 3  ) player[3]  = slotParms.player3;
      if ( parmAct.max_players >= 4  ) player[4]  = slotParms.player4;
      if ( parmAct.max_players >= 5  ) player[5]  = slotParms.player5;
      if ( parmAct.max_players >= 6  ) player[6]  = slotParms.player6;
      if ( parmAct.max_players >= 7  ) player[7]  = slotParms.player7;
      if ( parmAct.max_players >= 8  ) player[8]  = slotParms.player8;
      if ( parmAct.max_players >= 9  ) player[9]  = slotParms.player9;
      if ( parmAct.max_players >= 10 ) player[10] = slotParms.player10;
      if ( parmAct.max_players >= 11 ) player[11] = slotParms.player11;
      if ( parmAct.max_players >= 12 ) player[12] = slotParms.player12;
      if ( parmAct.max_players >= 13 ) player[13] = slotParms.player13;
      if ( parmAct.max_players >= 14 ) player[14] = slotParms.player14;
      if ( parmAct.max_players >= 15 ) player[15] = slotParms.player15;
      if ( parmAct.max_players >= 16 ) player[16] = slotParms.player16;
      if ( parmAct.max_players >= 17 ) player[17] = slotParms.player17;
      if ( parmAct.max_players >= 18 ) player[18] = slotParms.player18;
      if ( parmAct.max_players >= 19 ) player[19] = slotParms.player19;
      if ( parmAct.max_players >= 20 ) player[20] = slotParms.player20;
      if ( parmAct.max_players >= 21 ) player[21] = slotParms.player21;
      if ( parmAct.max_players >= 22 ) player[22] = slotParms.player22;
      if ( parmAct.max_players >= 23 ) player[23] = slotParms.player23;
      if ( parmAct.max_players >= 24 ) player[24] = slotParms.player24;
      if ( parmAct.max_players >= 25 ) player[25] = slotParms.player25;

      last_user = slotParms.last_user;
      notes = slotParms.notes;
      hide = slotParms.hide;
      orig_by = slotParms.orig_by;
      
      custom1 = slotParms.custom_disp1;      // added for Interlachen but can be used by others
      custom2 = slotParms.custom_disp2;
      custom3 = slotParms.custom_disp3;
      custom4 = slotParms.custom_disp4;
      custom5 = slotParms.custom_disp5;

      day_name = slotParms.day;
      stime = SystemUtils.getSimpleTime(slotParms.time);
      time = slotParms.time;
      date = slotParms.date;

      hides = "No";            // make sure hides is set correctly
      
      if (hide == 1) {
         
         hides = "Yes";
      }

      //
      //**********************************************
      //   Check for Member Notice from Pro
      //**********************************************
      //
      //  NEED TO MAKE NEW METHOD TO ACCEPT SLOT ID OR ?
      String memNotice = ""; // verifySlot.checkMemNotice(date, time, fb, course, day_name, "teetime", true, con);

      if (!memNotice.equals( "" )) {      // if message to display

         //
         //  Display the Pro's Message and then prompt the user to either accept or return to the tee sheet
         //
         out.println("<HTML><HEAD>");
         out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
         out.println("<title>Member Notice For Activity Request</Title>");
         out.println("</HEAD>");

         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

            out.println("<table border=\"0\" width=\"100%\" align=\"center\" valign=\"top\">");  // large table for whole page
            out.println("<tr><td valign=\"top\" align=\"center\">");
               out.println("<p>&nbsp;&nbsp;</p>");
               out.println("<p>&nbsp;&nbsp;</p>");
               out.println("<font size=\"3\">");
               out.println("<b>NOTICE</b><br><br><br></font>");

            out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"3\">");
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
               out.println("<form action=\"/" +rev+ "/servlet/Member_activity_slot\" method=\"post\" name=\"can\">");
//             out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
               out.println("<input type=\"hidden\" name=\"slot_id\" value=\"" + slot_id + "\">");
               out.println("<input type=\"hidden\" name=\"consec\" value=\"" + consec + "\">");
               out.println("<input type=\"hidden\" name=\"in_slots\" value=\"" + slotParms.in_slots + "\">");
               out.println("<input type=\"hidden\" name=\"group_id\" value=\"" + group_id + "\">");
               out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
               out.println("<input type=\"hidden\" name=\"day\" value=" + day_name + ">");
               out.println("<input type=\"hidden\" name=\"time\" value=" + time + ">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
               out.println("<input type=\"hidden\" name=\"layout_mode\" value=" + layout_mode + ">");
               out.println("<input type=\"submit\" value=\"NO - Return\" name=\"cancel\"></form>");

               out.println("</font></td>");

               out.println("<td align=\"center\">");
               out.println("<font size=\"2\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("</font></td>");

               out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
                  out.println("<form action=\"/" +rev+ "/servlet/Member_activity_slot\" method=\"post\">");
                  out.println("<input type=\"hidden\" name=\"slot_id\" value=\"" + slot_id + "\">");
                  out.println("<input type=\"hidden\" name=\"group_id\" value=\"" + group_id + "\">");
                  out.println("<input type=\"hidden\" name=\"in_slots\" value=\"" + slotParms.in_slots + "\">");
                  out.println("<input type=\"hidden\" name=\"consec\" value=\"" + consec + "\">");
                  out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
//                out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                  out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
                  out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
                  out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
                  for (int x = 1; x <= parmAct.max_players; x++) {
                     out.println("<input type=\"hidden\" name=\"player1\" value=\"" + player[x] + "\">");
                  }
                  out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
                  out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hides + "\">");
                  out.println("<input type=\"hidden\" name=\"custom1\" value=\"" + custom1 + "\">");
                  out.println("<input type=\"hidden\" name=\"custom2\" value=\"" + custom2 + "\">");
                  out.println("<input type=\"hidden\" name=\"custom3\" value=\"" + custom3 + "\">");
                  out.println("<input type=\"hidden\" name=\"custom4\" value=\"" + custom4 + "\">");
                  out.println("<input type=\"hidden\" name=\"custom5\" value=\"" + custom5 + "\">");
                  out.println("<input type=\"hidden\" name=\"layout_mode\" value=" + layout_mode + ">");
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
      } // end Notice display
                
   }              // end of 'letter' or 'return' if


    // Determin if we are here to modify an existing time or if we are creating a new one
    // to do this let's see if there are any existing players in the db for this slot
    try {

        pstmt = con.prepareStatement (
           "SELECT COUNT(*) " +
           "FROM activity_sheets_players " +
           "WHERE activity_sheet_id = ?");

        pstmt.clearParameters();
        pstmt.setInt(1, slot_id);
        rs = pstmt.executeQuery();

        if (rs.next()) new_reservation = (rs.getInt(1) == 0);

    } catch (Exception exc) {

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    slotParms.slot_id = slot_id;
    slotParms.group_id = group_id;
    

    // debug
    out.println("<!-- parmAct.max_players=" + parmAct.max_players + " -->");
    out.println("<!-- slotParms.activity_id=" + slotParms.activity_id + " -->");   // sheet id
    out.println("<!-- slotParms.slot_id=" + slotParms.slot_id + " -->");           // slot on the sheet
    out.println("<!-- slotParms.time=" + slotParms.time + " -->");
    out.println("<!-- slotParms.group_id=" + slotParms.group_id + " -->");         // group that sheet belongs to
    out.println("<!-- slotParms.slots=" + slotParms.slots + " -->");
    out.println("<!-- slotParms.in_slots=" + slotParms.in_slots + " -->");
    out.println("<!-- new_reservation=" + new_reservation + " -->");


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
   //  Ensure that there are no null fields
   //
   for (int x = 1; x>=5; x++) {
       if (player[x] == null ) player[x] = "";
   }
   if (last_user == null ) {
      last_user = "";
   }
   if (notes == null ) {
      notes = "";
   }
   if (orig_by == null ) {
      orig_by = "";
   }
   
   
   //
   //  Set user's name as first open player to be placed in name slot for them
   //
   //  First, check if first time here and user is already included in this slot.
   //  Member_sheet already checked if slot is full and user not one of them!!
   //
   if ( first_call == true ) {

      boolean tmp = false;

      // check to see if the name is already present
      for (int x = 1; x <= parmAct.max_players; x++) {
          if (player[x].equals(name)) {
              tmp = true;
              break;
          }
      }

      // if the name wasn't found then add it to the first avail player spot
      if (tmp == false) {
          for (int x = 1; x <= parmAct.max_players; x++) {
              if (player[x].equals("")) {
                  player[x] = name;
                  break;
              }
          }
      }
   }
      
   //
   //  Set user's name as first open player to be placed in name slot for them
   //
   //  First, check if first time here and user is already included in this slot.
   //
   if (first_call == true) {
      
      boolean nameFound = false;

      nloop1:
      for (int x = 1; x <= parmAct.max_players; x++) {

         if (player[x].equals(name)) {

            nameFound = true;
            break nloop1;
         }
      }
      
      if (nameFound == false) {
         
         nloop2:
         for (int x = 1; x <= parmAct.max_players; x++) {

            if (player[x].equals("")) {

               player[x] = name;      // put name in empty spot
               break nloop2;
            }
         }
         
      }
   }

   //
   //  Get the name of this activity (i.e. Court Name)
   //
   String activity_name = "";
   try { activity_name = getActivity.getActivityName(slotParms.activity_id, con); }
   catch (Exception ignore) {}

   
   //
   //  Build the HTML page to prompt user for names
   //
   out.println("<HTML>");
   out.println("<HEAD><link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
   out.println("<Title>Member Activity Booking</Title>");

      //
      //*******************************************************************
      //  User clicked on a letter - submit the form for the letter
      //*******************************************************************
      //
      out.println("<script type='text/javascript'>");            // Submit the form when clicking on a letter
      out.println("<!--");
      out.println("function subletter(x) {");
      out.println(" document.playerform.letter.value = x;");         // put the letter in the parm
      out.println(" playerform.submit();");        // submit the form
      out.println("}");
      out.println("// -->");
      out.println("</script>");

      //
      //*******************************************************************
      //  Erase player name (erase button selected next to player's name)
      //
      //    Remove the player's name and shift any other names up starting at player1
      //*******************************************************************
      //
      out.println("<script type='text/javascript'>");            // Erase name script
      out.println("<!--");
      out.println("function erasename(elem) {");
      out.println(" document.playerform[elem].value = '';");           // clear the player field
      out.println("}");
      out.println("// -->");
      out.println("</script>");

      //
      //*******************************************************************
      //  Erase text area - (Notes)
      //*******************************************************************
      //
      out.println("<script type='text/javascript'>");            // Erase text area script
      out.println("<!--");
      out.println("function erasetext(elem) {");
      out.println(" document.playerform[elem].value = '';");           // clear the text field
      out.println("}");
      out.println("// -->");
      out.println("</script>");

      //
      //*******************************************************************
      //  Move a member name into the tee slot
      //*******************************************************************
      //
      out.println("<script type='text/javascript'>");            // Move name script
      out.println("<!--");

      out.println("function movename(namewc) {");

      out.println("del = ':';");                               // deliminator is a colon
      out.println("array = namewc.split(del);");                 // split string into 2 pieces (name, wc)
      out.println("var name = array[0];");
      out.println("var wc = array[1];");
      out.println("skip = 0;");

      for (int x = 1; x <= parmAct.max_players; x++) {
          out.println("var player"+x+" = document.playerform.player"+x+".value;");
      }

      out.println("if (( name != 'x') && ( name != 'X')) {");

      // see if this player is already part of this time slot - if so skip
      for (int x = 1; x <= parmAct.max_players; x++) {
          out.println(" if ( player"+x+".value == name ) skip = 1;");
      }

      out.println("}");  // end of IF not x

      out.println("if (skip == 0) {");

      for (int x = 1; x <= parmAct.max_players; x++) {
         out.println("if (player"+x+" == '') {");
            out.println(" document.playerform.player"+x+".value = name;");
         if (x==parmAct.max_players) {
             out.println("}");
         } else {
             out.print("} else ");
         }
      }

      out.println("}");                  // end of dup name chack

      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");                               // End of script


      //
      //*******************************************************************
      //  Move a Guest Name or 'X' into the tee slot
      //*******************************************************************
      //
      out.println("<script type='text/javascript'>");            // Move Guest Name script
      out.println("<!--");

      out.println("function moveguest(namewc) {");

      //out.println("var name = namewc;");
      out.println("array = namewc.split('|');"); // split string (partner_name, partner_id)
      out.println("var name = array[0];");
/*
      if (enableAdvAssist) {
          out.println("var use_guestdb = array[1];");
      } else {
          out.println("var use_guestdb = 0; // force to off on iPad");
      }
*/
      for (int x = 1; x <= parmAct.max_players; x++) {
          out.println("var player"+x+" = document.playerform.player"+x+".value;");
      }
      
      //  set spc to ' ' if name to move isn't an 'X'
      out.println("var spc = '';");
      out.println("if (name != 'X' && name != 'x') {");
      out.println("   spc = ' ';");
      out.println("}");

      for (int x = 1; x <= parmAct.max_players; x++) {
         out.println("if (player"+x+" == '') {");
            out.println(" document.playerform.player"+x+".value = name + spc;");
            out.println(" document.playerform.player"+x+".focus();");
         if (x==parmAct.max_players) {
             out.println("}");
         } else {
             out.print("} else ");
         }
      }

      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");                               // End of script


   out.println("</HEAD>");

   int root_id = 0;
   String tmp_title = "Activity";
   
   try { root_id = getActivity.getRootIdFromActivityId(slotParms.activity_id, con); }
   catch (Exception ignore) {}

   try { tmp_title = getActivity.getActivityName(root_id, con); }
   catch (Exception ignore) {}
   
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\" topmargin=\"0\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" width=\"100%\" align=\"center\" valign=\"top\">");  // large table for whole page
   out.println("<tr><td valign=\"top\" align=\"center\">");

   out.println("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#CCCCAA\" align=\"center\" valign=\"top\">");
     out.println("<tr><td align=\"left\" width=\"300\">");
     out.println("&nbsp;&nbsp;&nbsp;<b><i>FlxRez</i></b>");
     out.println("</td>");
       
     out.println("<td align=\"center\">");
     out.println("<font size=\"5\">" + tmp_title + " Reservation</font>");
     out.println("</font></td>");
       
     out.println("<td align=\"center\" width=\"300\">");
     out.println("<font size=\"1\" color=\"#000000\">Copyright&nbsp;</font>");
     out.println("<font size=\"2\" color=\"#000000\">&#169;&nbsp;</font>");
     out.println("<font size=\"1\" color=\"#000000\">ForeTees, LLC <br> " +thisYear+ " All rights reserved.");
     out.println("</font></td>");
   out.println("</tr></table>");

   out.println("<table border=\"0\" align=\"center\">");                           // table for main page
   out.println("<tr><td align=\"center\"><br>");

      out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"3\">");
         out.println("<tr>");
         out.println("<td width=\"620\" align=\"center\">");
         out.println("<font size=\"2\">");
         out.println("<b>Warning</b>:&nbsp;&nbsp;You have <b>6 minutes</b> to complete this reservation.");
         out.println("&nbsp; If you want to return without completing a reservation, <b>do not ");
         out.println("use your browser's BACK</b> button/option.&nbsp; Instead select the <b>Go Back</b> ");
         out.println("option below.");
         out.println("</font></td></tr>");
      out.println("</table>");

      out.println("<table border=\"0\" align=\"center\" cellpadding=\"5\" cellspacing=\"5\">"); // table to contain 4 tables below
      out.println("<tr>");
      out.println("<td align=\"center\">");         // col for Instructions and Go Back button

      out.println("<font size=\"1\">");
      out.println("<a href=\"#\" onClick=\"window.open ('/" +rev+ "/member_help_slot_instruct.htm', 'newwindow', config='Height=540, width=520, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
      
      out.println("<img src=\"/" +rev+ "/images/instructions.gif\" border=0>");
      out.println("<br>Click for Help</a>");

      out.println("</font><font size=\"2\">");
      out.println("<br><br><br>");

      out.println("<form action=\"/" +rev+ "/servlet/Member_activity_slot\" method=\"post\" name=\"can\">");
      out.println("<input type=\"hidden\" name=\"slot_id\" value=\"" + slot_id + "\">");
      out.println("<input type=\"hidden\" name=\"group_id\" value=\"" + group_id + "\">");
      out.println("<input type=\"hidden\" name=\"in_slots\" value=\"" + slotParms.in_slots + "\">");
//    out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
      out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
      out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
      out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
      out.println("<input type=\"hidden\" name=\"layout_mode\" value=\"" + layout_mode + "\">");
      out.println("Return<br>w/o Changes:<br>");
      out.println("<input type=\"submit\" value=\"Go Back\" name=\"cancel\"></form>");
      out.println("</font></td>");

      out.println("<form action=\"/" +rev+ "/servlet/Member_activity_slot\" method=\"post\" name=\"playerform\">");
      out.println("<input type=\"hidden\" name=\"slot_id\" value=\"" + slot_id + "\">");
      out.println("<input type=\"hidden\" name=\"group_id\" value=\"" + group_id + "\">");
      out.println("<td align=\"center\" valign=\"top\">");

         out.println("<font size=\"2\">");
         out.println("Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy + "</b>");

         String times = "";

         try {

             for (int i = 0; slotParms.sheet_ids.size() > i; i++) {

                pstmt = con.prepareStatement (
                   "SELECT DATE_FORMAT(date_time, '%k%i') AS time " +
                   "FROM activity_sheets " +
                   "WHERE sheet_id = ?");

                pstmt.clearParameters();
                pstmt.setInt(1, slotParms.sheet_ids.get(i));
                rs = pstmt.executeQuery();

                if (rs.next()) {

                    times += SystemUtils.getSimpleTime(rs.getInt(1)) + ", ";

                }

             } // end for loop

             times = times.substring(0, times.length() - 2);

         } catch (Exception exc) {

         } finally {

             try { rs.close(); }
             catch (Exception ignore) {}

             try { pstmt.close(); }
             catch (Exception ignore) {}

         }

         if (times.equals("")) times = stime;


           out.println("&nbsp;&nbsp;Time:&nbsp;&nbsp;<b>" + times + "</b> ");
           if (!activity_name.equals("")) {
              out.println("<br>Where: <b>" +activity_name+ "</b>");
           }
           if (slotParms.slots > 1 && new_reservation) {
               out.println("<p>NOTE: You have requested " + slotParms.slots + " consecutive times.  The player information<br>you enter will be copied to the other times automatically.</p>");
           } else if (slotParms.slots > 1 && !new_reservation) {
               out.println("<p>NOTE: The time you have requested is part of block of consecutive times.  The changes<br>you make to this time will be copied to the other times automatically.</p>");
           } else {
               out.println("<br><br>");
           }
           out.println("</font>");

            out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\" width=\"400\">");  // table for player selection
            out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<b>Add or Remove Players</b><br>");
            out.println("</font></td></tr>");
            out.println("<tr><td align=\"center\"><font size=\"2\"><br>");

            out.println("<table cellpadding=0 cellspacing=0 border=0>");

            out.println("<tr style=\"font-size:10pt;font-weight:bold\">" +
                            "<td></td>" +
                            "<td>&nbsp;&nbsp;&nbsp;&nbsp; Players</td>" +
                            "<td>&nbsp;&nbsp;</td>");
            out.println("</tr>");


            for (int x = 1; x <= parmAct.max_players; x++) {

                out.println("<tr style=\"height:30px\"><td><img src=\"/" +rev+ "/images/erase.gif\" onclick=\"erasename('player"+x+"')\" style=\"cursor:hand\"></td>");
                out.println("<td nowrap style=\"font-size:10pt;font-weight:bold\">"+x+":&nbsp;<input type=\"text\" id=\"player"+x+"\" name=\"player"+x+"\" value=\"" + player[x] + "\" size=\"32\" maxlength=\"60\"></td>");
                out.println("<td></td>");
                out.println("</tr>");

            }

            out.println("</table>");


            //
            // ONLY DISPLAY THIS CHECKBOX IF IT'S ENABLED IN THE CONFIG
            //
            if (parmAct.disallow_joins == 1) {

                out.println("<font size=\"2\"><br><br>Force Singles Match?:&nbsp;&nbsp; ");
                if ( disallow_joins == 1 ) {
                   out.println("<input type=\"checkbox\" checked name=\"disallow_joins\" value=\"1\">");
                } else {
                   out.println("<input type=\"checkbox\" name=\"disallow_joins\" value=\"1\">");
                }
                out.println("</font><font size=\"1\">&nbsp;(checked = yes)</font><font size=\"2\">");

            }

            //
            //   Notes
            //
            out.println("<br><br><img src=\"/" +rev+ "/images/erase.gif\" onclick=\"erasetext('notes')\" style=\"cursor:hand\">");
            out.println("Notes:&nbsp;<textarea name=\"notes\" id=\"notes\" cols=\"28\" rows=\"3\">" + notes + "</textarea>");


         out.println("<input type=\"hidden\" name=\"slot_id\" value=\"" + slot_id + "\">");
         out.println("<input type=\"hidden\" name=\"in_slots\" value=\"" + slotParms.in_slots + "\">");
         out.println("<input type=\"hidden\" name=\"activity_id\" value=\"" + slotParms.activity_id + "\">");
         out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
         out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
         out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
         out.println("<input type=\"hidden\" name=\"stime\" value=\"" + stime + "\">");
//       out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
         out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
         out.println("<input type=\"hidden\" name=\"layout_mode\" value=\"" + layout_mode + "\">");
         out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + orig_by + "\">");
         out.println("<input type=\"hidden\" name=\"custom1\" value=\"" + custom1 + "\">");
         out.println("<input type=\"hidden\" name=\"custom2\" value=\"" + custom2 + "\">");
         out.println("<input type=\"hidden\" name=\"custom3\" value=\"" + custom3 + "\">");
         out.println("<input type=\"hidden\" name=\"custom4\" value=\"" + custom4 + "\">");
         out.println("<input type=\"hidden\" name=\"custom5\" value=\"" + custom5 + "\">");
         out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
         
         out.println("<br><br><input type=submit value=\"Submit\" name=\"submitForm\"><br>");
         out.println("</font></td></tr>");
         out.println("</table>");
         out.println("<br><input type=submit value=\"Cancel Reservation\" name=\"remove\" onclick=\"return confirm('Are you sure you want to remove ALL players from this reservation?')\">");
         
      out.println("</td>");
      out.println("<td valign=\"top\"><BR><BR>");
      
      
       
      
   // ********************************************************************************
   //   If we got control from user clicking on a letter in the Member List,
   //   then we must build the name list.
   // ********************************************************************************
   String letter = "";
     
   if (req.getParameter("letter") != null) {     // if user clicked on a name letter

      letter = req.getParameter("letter");
        
      if (!letter.equals( "Partner List" )) {      // if not Partner List request
        
         letter = letter + "%";

         String first = "";
         String mid = "";
         String last = "";
         String bname = "";
         String wname = "";
         String dname = "";
         String mship = "";
         String wc = "";

         out.println("<table border=\"1\" width=\"140\" bgcolor=\"#f5f5dc\" valign=\"top\">");      // name list
         out.println("<tr><td align=\"center\" bgcolor=\"#336633\">");
               out.println("<font color=\"#ffffff\" size=\"2\">");
               out.println("<b>Name List</b>");
               out.println("</font></td>");
         out.println("</tr><tr>");
         out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("Click on name to add");
            out.println("</font></td></tr>");

         try {

            pstmt = con.prepareStatement (
                     "SELECT name_last, name_first, name_mi, m_ship " +
                     "FROM member2b " +
                     "WHERE name_last LIKE ? AND inact = 0 ORDER BY name_last, name_first, name_mi");

            pstmt.clearParameters();
            pstmt.setString(1, letter);
            rs = pstmt.executeQuery();

            out.println("<tr><td align=\"left\"><font size=\"2\">");
            out.println("<select size=\"20\" name=\"bname\" onClick=\"movename(this.value)\">"); // movename(this.form.bname.value)

            while( rs.next() ) {

               last = rs.getString(1);
               first = rs.getString(2);
               mid = rs.getString(3);
               mship = rs.getString(4);

               if (mid.equals("")) {

                  bname = first + " " + last;
                  dname = last + ", " + first;

               } else {

                  bname = first + " " + mid + " " + last;
                  dname = last + ", " + first + " " + mid;
                  
               }

               wname = bname;              // combine name:wc for script

               if (club.equals( "cordillera" )) {
                 
                  if (!mship.startsWith( "Employee" )) {       // if not an Employee (skip employees)
                    
                     out.println("<option value=\"" + wname + "\">" + dname + "</option>");
                  }
               } else {
                  out.println("<option value=\"" + wname + "\">" + dname + "</option>");
               }
            }

            out.println("</select>");
            out.println("</font></td></tr>");

         } catch (Exception ignore) {

         } finally {

             try { rs.close(); }
             catch (Exception ignore) {}

             try { pstmt.close(); }
             catch (Exception ignore) {}

         } 

         out.println("</table>");

      }        // end of IF Partner List or letter

   }           // not letter display

   if (letter.equals( "" ) || letter.equals( "Partner List" )) {  // if no letter or Partner List request

       alphaTable.displayPartnerList(user, sess_activity_id, 0, con, out);
       
   }        // end of if letter display

   out.println("</td>");                                      // end of this column
   out.println("<td width=\"200\" valign=\"top\"><BR><BR>");

     
   //
   //   Output the Alphabit Table for Members' Last Names 
   //
   alphaTable.getTable(out, user);
     

   //
   //   Output the List of Guests
   //
   alphaTable.guestList(club, "", day_name, time, parm, false, true, slotParms.activity_id, enableAdvAssist, out, con);


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
 //  Process reservation request from Member_activity_slot (HTML)
 // *********************************************************

 private void verify(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session, HttpServletResponse resp) {


    ResultSet rs = null;

    //
    //  Get this session's attributes
    //
    String user = "";
    String club = "";
    String posType = "";
    user = (String)session.getAttribute("user");
    club = (String)session.getAttribute("club");
    posType = (String)session.getAttribute("posType");

    int sess_activity_id = (Integer)session.getAttribute("activity_id");

    int activity_id = 0;
    int slot_id = 0;
    int group_id = 0;
    //int consec = 0;
    int hide = 0;
    int mm = 0;
    int yy = 0;
    int dd = 0;
    int calYear = 0;
    int calMonth = 0;
    int thisMonth = 0;
    int calDay = 0;
    int skip = 0;
    int time = 0;
//  int ind = 0;
    int temp = 0;
    int sendemail = 0;
    int emailNew = 0;
    int emailMod = 0;
    int emailCan = 0;
    int gi = 0;
    int memNew = 0;
    int memMod = 0;
    int custom_int = 0;
    int date = 0;
    
    long todayDate = 0;

    String player = "";
    String err_name = "";
    String memberName = "";

    String custom_string = "";
    String custom_disp1 = "";
    String custom_disp2 = "";
    String custom_disp3 = "";
    String custom_disp4 = "";
    String custom_disp5 = "";
/*
    String customS1 = "";
    String customS2 = "";
    String customS3 = "";
    String customS4 = "";
    String customS5 = "";
*/
    boolean error = false;

    if (req.getParameter("skip") != null) {
        skip = Integer.parseInt(req.getParameter("skip"));
    }

    if (req.getParameter("slot_id") != null) {

        slot_id = Integer.parseInt(req.getParameter("slot_id"));
    }

    if (req.getParameter("group_id") != null) {

        group_id = Integer.parseInt(req.getParameter("group_id"));
    }

    if (req.getParameter("activity_id") != null) {

        activity_id = Integer.parseInt(req.getParameter("activity_id"));
    }

    //
    //  parm block to hold the club parameters
    //
    parmClub parm = new parmClub(sess_activity_id, con);

    //
    //  parm block to hold the time slot parms
    //
    parmSlot slotParms = new parmSlot();          // allocate a parm block

    //
    // Populate our parmAct block
    //
    parmActivity parmAct = new parmActivity();              // allocate a parm block
    parmAct.slot_id = slot_id;                              // pass in the slot id so we can determin which activity to load parms for

    try {

        getActivity.getParms(con, parmAct);                  // get the activity config

    } catch (Exception e1) {
        out.println("<BR><BR>" + e1.getMessage());
    }

    //
    //  Arrays to hold member & guest names to tie guests to members
    //
    //String [] memA = new String [5];     // members
    String [] usergA = new String [parmAct.max_players];   // guests' associated member (username)

    //
    // Get all the parameters entered
    //
    String sdate = req.getParameter("date");           //  date of time slot requested (yyyymmdd)
    String stime = req.getParameter("time");           //  time of time slot requested (hhmm)
//  String index = req.getParameter("index");          //  day index value (needed by _sheet on return)

    slotParms.player1 = (req.getParameter("player1") == null) ? "" : req.getParameter("player1").trim();
    slotParms.player2 = (req.getParameter("player2") == null) ? "" : req.getParameter("player2").trim();
    slotParms.player3 = (req.getParameter("player3") == null) ? "" : req.getParameter("player3").trim();
    slotParms.player4 = (req.getParameter("player4") == null) ? "" : req.getParameter("player4").trim();
    slotParms.player5 = (req.getParameter("player5") == null) ? "" : req.getParameter("player5").trim();
    slotParms.notes = (req.getParameter("notes") == null) ? "" : req.getParameter("notes").trim();
    slotParms.day = req.getParameter("day");                      // name of day
    slotParms.jump = req.getParameter("jump");                    // jump index for _sheet
    slotParms.disallow_joins = (req.getParameter("disallow_joins") == null) ? 0 : 1;

    slotParms.hndcp1 = 99;     // init handicaps
    slotParms.hndcp2 = 99;
    slotParms.hndcp3 = 99;
    slotParms.hndcp4 = 99;
    slotParms.hndcp5 = 99;

    if (req.getParameter("layout_mode") != null) {

        slotParms.layout_mode = Integer.parseInt(req.getParameter("layout_mode"));   // save for return to sheet
    }

    if (req.getParameter("in_slots") != null && !req.getParameter("in_slots").equals("")) {

        //slotParms.slots = Integer.parseInt(req.getParameter("consec"));
        slotParms.in_slots = req.getParameter("in_slots");

        // populate slotParms.sheet_ids array
        StringTokenizer tok = new StringTokenizer( slotParms.in_slots, "," );
        while ( tok.hasMoreTokens() ) {
            slotParms.sheet_ids.add(Integer.parseInt(tok.nextToken()));
            slotParms.slots++;
            out.println("<!-- adding " + slotParms.sheet_ids.get(slotParms.slots - 1) + " to slotParms.sheet_id (" + (slotParms.slots - 1) + ") -->");
        }

    } else {

        // we're here to process a single request - lets setup slotParms.sheet_ids & .slots
        // to help with our loops later on
        slotParms.sheet_ids.add(slot_id);
        slotParms.slots = 1;
        out.println("<!-- single time slot request - adding " + slot_id + " to slotParms.sheet_id array -->");

    }

    out.println("<!-- slotParms.sheet_ids.size()=" + slotParms.sheet_ids.size() + " -->");

    /*
    //
    //  Get member names for Unaccompanied Guests, if provided
    //
    if (req.getParameter("mem1") != null) {
      slotParms.mem1 = req.getParameter("mem1");
    }
    if (req.getParameter("mem2") != null) {
      slotParms.mem2 = req.getParameter("mem2");
    }
    if (req.getParameter("mem3") != null) {
      slotParms.mem3 = req.getParameter("mem3");
    }
    if (req.getParameter("mem4") != null) {
      slotParms.mem4 = req.getParameter("mem4");
    }
    if (req.getParameter("mem5") != null) {
      slotParms.mem5 = req.getParameter("mem5");
    }
    */

    //
    //  Convert date & time from string to int
    //
    try {
        date = Integer.parseInt(sdate);
        time = Integer.parseInt(stime);
//      ind = Integer.parseInt(index);       // get numeric value of index
    }
    catch (NumberFormatException e) {
        out.println("<!-- ERROR PARSING NUMBERS IN VERIFY! -->");
    }

    long shortDate = date - ((date / 10000) * 10000);       // get mmdd (i.e.  20060512 - 20060000 = 512)


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
    //  put parms in Parameter Object for portability
    //
    slotParms.slot_id = slot_id;
    slotParms.group_id = group_id;
    slotParms.activity_id = activity_id;
    try { slotParms.root_activity_id = getActivity.getRootIdFromActivityId(activity_id, con); }
    catch (Exception ignore) {}
    slotParms.date = date;
    slotParms.time = time;
    slotParms.mm = mm;
    slotParms.yy = yy;
    slotParms.dd = dd;
//  slotParms.ind = ind;                      // index value
    slotParms.club = club;                    // name of club

    //
    //  Get today's date
    //
    Calendar cal = new GregorianCalendar();       // get todays date
    calYear = cal.get(Calendar.YEAR);
    calMonth = cal.get(Calendar.MONTH) +1;
    calDay = cal.get(Calendar.DAY_OF_MONTH);

    thisMonth = calMonth;                          // save this month

    todayDate = calYear * 10000;                      // create a date field of yyyymmdd
    todayDate = todayDate + (calMonth * 100);
    todayDate = todayDate + calDay;                    // date = yyyymmdd (for comparisons)


    // debug
    out.println("<!-- parmAct.max_players=" + parmAct.max_players + " -->");
    out.println("<!-- slotParms.activity_id=" + slotParms.activity_id + " -->");
    out.println("<!-- slotParms.slot_id=" + slotParms.slot_id + " -->");
    out.println("<!-- slotParms.time=" + slotParms.time + " -->");
    out.println("<!-- slotParms.group_id=" + slotParms.group_id + " -->");
    out.println("<!-- slotParms.slots=" + slotParms.slots + " -->");
    out.println("<!-- slotParms.in_slots=" + slotParms.in_slots + " -->");
   
    //
    //  Check if this tee slot is still 'in use' and still in use by this user??
    //
    //  This is necessary because the user may have gone away while holding this slot.  If the
    //  slot timed out (system timer), the slot would be marked 'not in use' and another
    //  user could pick it up.  The original holder could be trying to use it now.
    //
    //  TODO: Need to adjust this so that it can check consecutive times
    //

    try {

        PreparedStatement pstmt = con.prepareStatement(
            "SELECT * " +
            "FROM activity_sheets " +
            "WHERE sheet_id = ? AND (in_use_by = '' || in_use_by = ?)");

        pstmt.clearParameters();
        pstmt.setInt(1, slot_id);
        pstmt.setString(2, user);
        rs = pstmt.executeQuery();

        if (rs.next()) {

            slotParms.activity_id = rs.getInt( "activity_id" );
            slotParms.last_user = rs.getString( "last_mod_by" );
            slotParms.hideNotes = rs.getInt( "hideNotes" );
            slotParms.in_use_by = user;
            memNew = rs.getInt("memNew");
            memMod = rs.getInt("memMod");

        } else {

            // this time slot is in-use by someone else - abort!

        }
        pstmt.close();

        //
        // Load up all the old players for comparison later on
        //
        pstmt = con.prepareStatement (
            "SELECT * " +
            "FROM activity_sheets_players " +
            "WHERE activity_sheet_id = ? " +
            "ORDER BY pos");

        pstmt.clearParameters();
        pstmt.setInt(1, slot_id);
        rs = pstmt.executeQuery();

        if (rs.next()) {

            slotParms.oldPlayer1 = rs.getString( "player_name" );
            slotParms.oldUser1 = rs.getString( "username" );
            //slotParms.players = 1;
        }

        if (rs.next()) {

            slotParms.oldPlayer2 = rs.getString( "player_name" );
            slotParms.oldUser2 = rs.getString( "username" );
            //slotParms.players = 2;
        }

        if (rs.next()) {

            slotParms.oldPlayer3 = rs.getString( "player_name" );
            slotParms.oldUser3 = rs.getString( "username" );
            //slotParms.players = 3;
        }

        if (rs.next()) {

            slotParms.oldPlayer4 = rs.getString( "player_name" );
            slotParms.oldUser4 = rs.getString( "username" );
            //slotParms.players = 4;
        }

        if (rs.next()) {

            slotParms.oldPlayer5 = rs.getString( "player_name" );
            slotParms.oldUser5 = rs.getString( "username" );
            //slotParms.players = 5;
        }

        pstmt.close();

        if (slotParms.orig_by.equals( "" )) {    // if originator field still empty

            slotParms.orig_by = user;             // set this user as the originator
        }

    }
    catch (Exception e) {

        out.println("<!-- ERROR LOADING OLD PLAYERS -->");
        dbError(out, e);
        return;
    }


   //
   //  Save the custom fields in slotParms in case they are needed elsewhere
   //
   slotParms.custom_string = custom_string;
   slotParms.custom_int = custom_int;
   slotParms.custom_disp1 = custom_disp1;
   slotParms.custom_disp2 = custom_disp2;
   slotParms.custom_disp3 = custom_disp3;
   slotParms.custom_disp4 = custom_disp4;
   slotParms.custom_disp5 = custom_disp5;



   //
   //  If request is to 'Cancel This Res', then clear all fields for this slot
   //
   if (req.getParameter("remove") != null) {
      
      slotParms.player1 = "";
      slotParms.player2 = "";
      slotParms.player3 = "";
      slotParms.player4 = "";
      slotParms.player5 = "";
      slotParms.user1 = "";
      slotParms.user2 = "";
      slotParms.user3 = "";
      slotParms.user4 = "";
      slotParms.user5 = "";
      slotParms.userg1 = "";
      slotParms.userg2 = "";
      slotParms.userg3 = "";
      slotParms.userg4 = "";
      slotParms.userg5 = "";
      slotParms.show1 = 0;
      slotParms.show2 = 0;
      slotParms.show3 = 0;
      slotParms.show4 = 0;
      slotParms.show5 = 0;
      slotParms.notes = "";
      hide = 0;
      slotParms.mNum1 = "";
      slotParms.mNum2 = "";
      slotParms.mNum3 = "";
      slotParms.mNum4 = "";
      slotParms.mNum5 = "";
      slotParms.orig_by = "";
      slotParms.conf = "";
      slotParms.pos1 = 0;
      slotParms.pos2 = 0;
      slotParms.pos3 = 0;
      slotParms.pos4 = 0;
      slotParms.pos5 = 0;
      slotParms.custom_disp1 = "";
      slotParms.custom_disp2 = "";
      slotParms.custom_disp3 = "";
      slotParms.custom_disp4 = "";
      slotParms.custom_disp5 = "";
      slotParms.tflag1 = "";
      slotParms.tflag2 = "";
      slotParms.tflag3 = "";
      slotParms.tflag4 = "";
      slotParms.tflag5 = "";

      slotParms.in_slots = "";
      slotParms.report_ignore = 0;
      slotParms.disallow_joins = 0;
      
      emailCan = 1;      // send email notification for Cancel Request
      sendemail = 1;

      memMod++;      // increment number of mods for report

   } else {

      //
      //  Process normal res request
      //
      //   Get the parms specified for this club
      //
      try {

         parm.club = club;                              // set club name
         //parm.course = slotParms.course;                // and course name

         getClub.getParms(con, parm, activity_id);      // get the club parms

         //slotParms.rnds = parm.rnds;
         //slotParms.hrsbtwn = parm.hrsbtwn;
      
      } catch (Exception ignore) {}

      //
      //  Make sure at least 1 player contains a name
      //
      if ((slotParms.player1.equals( "" )) && (slotParms.player2.equals( "" )) && (slotParms.player3.equals( "" )) && (slotParms.player4.equals( "" )) && (slotParms.player5.equals( "" ))) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<center>");
         out.println("<BR><BR><H3>Data Entry Error</H3>");
         out.println("<BR><BR>Required field has not been completed or is invalid.");
         out.println("<BR><BR>At least 1 Player field must contain a valid entry.");
         out.println("<BR>If you wish to remove all names from this slot, use the 'Cancel Reservation' button.");
         out.println("<BR><BR>");
         //
         //  Return to _slot to change the player order
         //
         returnToSlot(out, slotParms);
         return;

      }

      //
      //  Shift players up if any empty spots (start with Player1 position)
      //
      verifySlot.shiftUp(slotParms); // OK
      
      //
      //  Check if any player names are guest names (set userg1-5 if necessary)
      //
      try {
        
         verifySlot.parseGuests(slotParms, con); // OK

      } catch (Exception ignore) { }


        //
        //  Reject if any player was a guest type that is not allowed for members
        //
        if (!slotParms.gplayer.equals( "" )) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><H3>Data Entry Error</H3>");
            if (slotParms.hit3 == true) {                      // if error was name not specified
                out.println("<BR><BR>You must specify the name of your guest(s).");
                out.println("<BR><b>" + slotParms.gplayer + "</b> does not include a valid name (must be at least first & last names).");
                out.println("<BR><BR>To specify the name, click in the player box where the guest is specified, ");
                out.println("<BR>move the cursor (use the arrow keys or mouse) to the end of the guest type value, ");
                out.println("<BR>use the space bar to enter a space and then type the guest's name.");
            } else {
                out.println("<BR><BR><b>" + slotParms.gplayer + "</b> specifies a Guest Type that is not allowed for member use.");
            }
            out.println("<BR><BR>If the Golf Shop had originally entered this guest, then it <b>must not</b> be changed.");
            out.println("<BR><BR>Please correct this and try again.");
            out.println("<BR><BR>");

            returnToSlot(out, slotParms);
            return;
        }

        error = false;


        if (parm.unacompGuest == 0) {      // if unaccompanied guests not supported

            //
            //  Make sure at least 1 player contains a member
            //
            if (((slotParms.player1.equals( "" )) || (slotParms.player1.equalsIgnoreCase( "x" )) || (!slotParms.g1.equals( "" ))) &&
                ((slotParms.player2.equals( "" )) || (slotParms.player2.equalsIgnoreCase( "x" )) || (!slotParms.g2.equals( "" ))) &&
                ((slotParms.player3.equals( "" )) || (slotParms.player3.equalsIgnoreCase( "x" )) || (!slotParms.g3.equals( "" ))) &&
                ((slotParms.player4.equals( "" )) || (slotParms.player4.equalsIgnoreCase( "x" )) || (!slotParms.g4.equals( "" ))) &&
                ((slotParms.player5.equals( "" )) || (slotParms.player5.equalsIgnoreCase( "x" )) || (!slotParms.g5.equals( "" )))) {

                error = true;
            }

        } else {           // guests are ok

            //
            //  Make sure at least 1 player contains a player
            //
            if (((slotParms.player1.equals( "" )) || (slotParms.player1.equalsIgnoreCase( "x" ))) &&
                ((slotParms.player2.equals( "" )) || (slotParms.player2.equalsIgnoreCase( "x" ))) &&
                ((slotParms.player3.equals( "" )) || (slotParms.player3.equalsIgnoreCase( "x" ))) &&
                ((slotParms.player4.equals( "" )) || (slotParms.player4.equalsIgnoreCase( "x" ))) &&
                ((slotParms.player5.equals( "" )) || (slotParms.player5.equalsIgnoreCase( "x" )))) {

                error = true;
            }
        }

        if (error == true) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><H3>Data Entry Error</H3>");
            out.println("<BR><BR>Required field has not been completed or is invalid.");
            out.println("<BR><BR>At least one player field must contain a name.");
            out.println("<BR>If you want to cancel this reservation, use the 'Cancel Sign-up' button under the player fields.");
            out.println("<BR><BR>");

            returnToSlot(out, slotParms);
            out.close();
            return;
        }

        //
        //  Check the number of X's against max specified by proshop
        //
        int xcount = 0;

        if (slotParms.player1.equalsIgnoreCase( "x" )) xcount++;
        if (slotParms.player2.equalsIgnoreCase( "x" )) xcount++;
        if (slotParms.player3.equalsIgnoreCase( "x" )) xcount++;
        if (slotParms.player4.equalsIgnoreCase( "x" )) xcount++;
        if (slotParms.player5.equalsIgnoreCase( "x" )) xcount++;

        if (xcount > parmAct.allow_x) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><H3>Data Entry Error</H3>");
            out.println("<BR><BR>The number of X's requested (" + xcount + ") exceeds the number allowed (" + parmAct.allow_x + ") for this reservation.");
            out.println("<BR>Please try again.");
            out.println("<BR><BR>");

            returnToSlot(out, slotParms);
            out.close();
            return;
        }



      //
      //  Make sure there are no duplicate names
      //
      player = "";

      if ((!slotParms.player1.equals( "" )) && (!slotParms.player1.equalsIgnoreCase( "x" )) && (slotParms.g1.equals( "" ))) {

        if ((slotParms.player1.equalsIgnoreCase( slotParms.player2 )) || (slotParms.player1.equalsIgnoreCase( slotParms.player3 )) ||
            (slotParms.player1.equalsIgnoreCase( slotParms.player4 )) || (slotParms.player1.equalsIgnoreCase( slotParms.player5 ))) {

           player = slotParms.player1;
        }
      }

      if ((!slotParms.player2.equals( "" )) && (!slotParms.player2.equalsIgnoreCase( "x" )) && (slotParms.g2.equals( "" ))) {

        if ((slotParms.player2.equalsIgnoreCase( slotParms.player3 )) || (slotParms.player2.equalsIgnoreCase( slotParms.player4 )) ||
            (slotParms.player2.equalsIgnoreCase( slotParms.player5 ))) {

           player = slotParms.player2;
        }
      }

      if ((!slotParms.player3.equals( "" )) && (!slotParms.player3.equalsIgnoreCase( "x" )) && (slotParms.g3.equals( "" ))) {

        if ((slotParms.player3.equalsIgnoreCase( slotParms.player4 )) ||
            (slotParms.player3.equalsIgnoreCase( slotParms.player5 ))) {

           player = slotParms.player3;
        }
      }

      if ((!slotParms.player4.equals( "" )) && (!slotParms.player4.equalsIgnoreCase( "x" )) && (slotParms.g4.equals( "" ))) {

        if (slotParms.player4.equalsIgnoreCase( slotParms.player5 )) {

           player = slotParms.player4;
        }
      }

      if (!player.equals( "" )) {          // if dup name found

          out.println(SystemUtils.HeadTitle("Data Entry Error"));
          out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
          out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
          out.println("<center>");
          out.println("<BR><BR><H3>Data Entry Error</H3>");
          out.println("<BR><BR><b>" + player + "</b> was specified more than once.");
          out.println("<BR><BR>Please correct this and try again.");
          out.println("<BR><BR>");

          returnToSlot(out, slotParms);
          out.close();
          return;
      }
      
      //
      //  Parse the names to separate first, last & mi
      //
      try {

         error = verifySlot.parseNames(slotParms, "pro");  // OK

      } catch (Exception exc) {
         verifySlot.logError("verifySlot.parseNames error: " + exc.getMessage());
      }

        
      if ( error == true && skip == 0) {          // if problem

         //  Return to _slot to change the player order
         out.println(SystemUtils.HeadTitle("Invalid Data - Reject"));
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><H3>Invalid Data Received</H3><BR>");
         out.println("<BR><BR>Sorry, a name you entered is not valid.<BR>");
         out.println("The player <b>" +slotParms.player+ "</b> is not a guest type, an X, or a valid member name.");

         returnToSlot(out, slotParms);
         out.close();
         return;
      }


      //
      //  Get the usernames, membership types and hndcp's for players if matching name found
      //
      try {

         verifySlot.getUsers(slotParms, con); // OK

      } catch (Exception exc) {
         verifySlot.logError("verifySlot.getUsers error: " + exc.getMessage());
      }
/*
      //
      //  Save the members' usernames for guest association 
      //
      memA[0] = slotParms.user1;
      memA[1] = slotParms.user2;
      memA[2] = slotParms.user3;
      memA[3] = slotParms.user4;
      memA[4] = slotParms.user5;
*/
      //
      //  Check if proshop user requested that we skip the following name test.
      //
      //  If any skips are set, then we've already been through here.
      //

      int invalNum = 0;
      err_name = "";

      //
      //  Check if any of the names are invalid.  If so, ask proshop if they want to ignore the error.
      //
      if (slotParms.inval5 != 0) {

        err_name = slotParms.player5;
        invalNum = slotParms.inval5;
      }

      if (slotParms.inval4 != 0) {

        err_name = slotParms.player4;
        invalNum = slotParms.inval4;
      }

      if (slotParms.inval3 != 0) {

        err_name = slotParms.player3;
        invalNum = slotParms.inval3;
      }

      if (slotParms.inval2 != 0) {

        err_name = slotParms.player2;
        invalNum = slotParms.inval2;
      }

      if (slotParms.inval1 != 0) {

        err_name = slotParms.player1;
        invalNum = slotParms.inval1;
      }

      if (!err_name.equals( "" )) {      // invalid name received

        out.println(SystemUtils.HeadTitle("Player Not Found - Prompt"));
        out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");

        if (invalNum == 2) {        // if incomplete member record

           out.println("<BR><H3>Incomplete Member Record</H3><BR>");
           out.println("<BR><BR>Sorry, a member you entered has an imcomplete member record and cannot be included at this time.<BR>");
           out.println("<BR>Member Name:&nbsp;&nbsp;&nbsp;'" +err_name+ "'");
           out.println("<BR><BR>Please update this member's record via Admin and complete the required fields.");
           out.println("<BR><BR>You will have to remove this name from your request.");
           out.println("<BR><BR>");

        } else {

           out.println("<BR><H3>Player's Name Not Found in System</H3><BR>");
           out.println("<BR><BR>Warning:  " + err_name + " does not exist in the system database.");
           out.println("<BR><BR>");
        }

        // do we need to handle incomplete member records?
        returnToSlot(out, slotParms);
        out.close();
        return;

      } // end if err_name not empty



     //
     //************************************************************************
     //  Check any membership types for max rounds per week, month or year
     //************************************************************************
     //
     if ((!slotParms.mship1.equals( "" )) ||
         (!slotParms.mship2.equals( "" )) ||
         (!slotParms.mship3.equals( "" )) ||
         (!slotParms.mship4.equals( "" )) ||
         (!slotParms.mship5.equals( "" ))) {   // if at least one member exists then check number of rounds

        error = false;                             // init error indicator

        try {

           error = verifyActSlot.checkMaxRounds(slotParms, con); // OK

        } catch (Exception e2) {

           SystemUtils.logError("Check for Max Rounds (Member_activity_slot): exception="  + e2);        // log the error message

        }

        if (error == true) {      // a member exceed the max allowed time slots per week, month or year

           out.println(SystemUtils.HeadTitle("Max Limit Exceeded - Reject"));
           out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
           out.println("<hr width=\"40%\">");
           out.println("<BR><H3>Member Exceeded Limit</H3><BR>");
           out.println("<BR><BR>Warning:  " + slotParms.player + " is a " + slotParms.mship + " member and has exceeded the<BR>");
           out.println("maximum number of reservations allowed for this " + slotParms.period + ".");
           out.println("<BR><BR>");

           returnToSlot(out, slotParms);
           out.close();
           return;
        }
     }      // end of mship if
         
         

     //
     //*******************************************************************************************************
     //  DISALLOW_JOINS - do not allow more than 2 players if this option was selected (Singles Match Only)
     //
     //   NOTE:  we may have to chane this for non-tennis activities!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     //*******************************************************************************************************
     //
     if (!slotParms.player3.equals("") && slotParms.disallow_joins != 0) {      // if singles match and more than 2 players

           out.println(SystemUtils.HeadTitle("Max Limit Exceeded - Reject"));
           out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
           out.println("<hr width=\"40%\">");
           out.println("<BR><H3>Invalid Number of Players</H3><BR>");
           out.println("<BR><BR>Sorry, you cannot include more than 2 players when you select<BR>");
           out.println("the Force Singles Match option. Please limit your group to 2 players.");
           out.println("<BR><BR>");

           returnToSlot(out, slotParms);
           out.close();
           return;
     }                  // end of IF disallow_joins
         
         
     //
     //***************************************************************************************************
     //
     //  CUSTOMS - check all possible customs here - those that are not dependent on guest info!!!!!!!!
     //
     //     verifyCustom.checkCustoms1 will process the individual custom and return any error message.
     //
     //    *** USE THIS FOR ALL FUTURE CUSTOMS WHEN APPROPRIATE !!!!!!!!!!!!!  ***
     //
     //***************************************************************************************************
     //

     // NEED TO IMPLEMENT EQUIVALENT ONCE WE ARE DOING CUSTOMS
     String errorMsg = ""; // verifyCustom.checkCustoms1(slotParms, con);     // go check for customs

     if (!errorMsg.equals( "" )) {         // if error encountered - reject

        out.println(SystemUtils.HeadTitle("Data Entry Error"));
        out.println("<BODY><font face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR><BR><BR>");
        out.println("<hr width=\"40%\"><BR>");
        out.println( errorMsg );           // add custom error msg
        out.println("<BR><BR>");

        returnToSlot(out, slotParms);
        out.close();
        return;
     }

     //
     //  MOVE ANY APPROPRIATE CUSTOMS THAT FOLLOW THIS SO THEY USE ABOVE PROCESS !!!!!!!!!!!!!!
     //
         



     //
     //************************************************************************
     //  Check for max # of guests exceeded (per Member or per time slot)
     //************************************************************************
     //
     if (slotParms.guests != 0) {      // if any guests were included

        error = false;                             // init error indicator

        try {

           error = verifySlot.checkMaxGuests(slotParms, con); // OK

        }
        catch (Exception e5) {

           out.println("<!-- ERROR RETURNED FROM verifySlot.checkMaxGuests -->");
           dbError(out, e5);
           return;
        }

        if (error == true) {      // a member exceed the max allowed guests

            out.println(SystemUtils.HeadTitle("Max Num Guests Exceeded - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR><BR><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><H3>Number of Guests Exceeded Limit</H3>");
            out.println("<BR>Sorry, the maximum number of guests allowed for the<BR>");
            out.println("time you are requesting is " +slotParms.grest_num+ " per " +slotParms.grest_per+ ".");
            out.println("<BR>You have requested " +slotParms.guests+ " guests and " +slotParms.members+ " members.");
            out.println("<BR><BR>Restriction Name = " +slotParms.rest_name);
            out.println("<BR><BR>");

            returnToSlot(out, slotParms);
            out.close();
            return;
        }

     }



     //
     // *******************************************************************************
     //  Check member restrictions
     //
     //     First, find all restrictions within date & time constraints on this course.
     //     Then, find the ones for this day.
     //     Then, find any for this member type or membership type (all 5 players).
     //
     // *******************************************************************************
     //
     error = false;                             // init error indicator

     parmSlot parm1 = new parmSlot();

     for (int i = 0; i < slotParms.sheet_ids.size(); i++) {

         // populate parm1 with slotParms w/ different time
         if (i > 0) {
             
             parm1 = getConsecParms(slotParms, i, con);

         } else {

             parm1 = slotParms;

         }

         try {

            error = verifySlot.checkMemRests(parm1, con); // OK

         } catch (Exception e7) {

            out.println("<!-- ERROR RETURNED FROM verifySlot.checkMemRests (" + i + ") -->");
            dbError(out, e7);
            return;
         }

         if (error) break;

     } // end consec loop

     if (error == true) {           // if we hit on a restriction

        out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
        out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<BR><BR><H3>Member Restricted</H3><BR>");
        out.println("<BR>Sorry, <b>" + parm1.player + "</b> is restricted from playing during this time.<br><br>");
        out.println("This time slot has the following restriction:  <b>" + parm1.rest_name + "</b><br><br>");

        returnToSlot(out, parm1);
        out.close();
        return;
     }

     
     //
     // *******************************************************************************
     //  Check Member Number restrictions
     //
     //     First, find all restrictions within date & time constraints
     //     Then, find the ones for this day
     //     Then, check all players' member numbers against all others in the time period
     //
     // *******************************************************************************
     //
     error = false;                             // init error indicator

     try {

        // NEED TO IMPLEMENT
        //error = verifySlot.checkMemNum(slotParms, con);

     }
     catch (Exception e7) {

        out.println("<!-- ERROR RETURNED FROM verifySlot.checkMemNum -->");
        dbError(out, e7);
        return;
     }                             // end of member restriction tests

     if (error == true) {          // if we hit on a restriction

        out.println(SystemUtils.HeadTitle("Member Number Restricted - Reject"));
        out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<BR><BR><H3>Member Restricted by Member Number</H3><BR>");
        out.println("<BR>Sorry, ");
        if (!slotParms.pnum1.equals( "" )) {
           out.println("<b>" + slotParms.pnum1 + "</b> ");
        }
        if (!slotParms.pnum2.equals( "" )) {
           out.println("<b>" + slotParms.pnum2 + "</b> ");
        }
        if (!slotParms.pnum3.equals( "" )) {
           out.println("<b>" + slotParms.pnum3 + "</b> ");
        }
        if (!slotParms.pnum4.equals( "" )) {
           out.println("<b>" + slotParms.pnum4 + "</b> ");
        }
        if (!slotParms.pnum5.equals( "" )) {
           out.println("<b>" + slotParms.pnum5 + "</b> ");
        }
        out.println("is/are restricted from playing during this time because the");
        out.println("<BR> number of members with the same member number has exceeded the maximum allowed.");
        out.println("<br><br>This time slot has the following restriction:  <b>" + slotParms.rest_name + "</b>");
        out.println("<BR><BR>");
        //
        //  Return to _slot to change the player order
        //
        returnToSlot(out, slotParms);
        out.close();
        return;
     }



     //
     //***********************************************************************************************
     //
     //    Now check if any of the players are already scheduled today
     //
     //***********************************************************************************************
     //
     slotParms.hit = false;                             // init error indicator
     slotParms.hit2 = false;                            // init error indicator

     try {

        // skip this check if the rounds-per-day paremters are not set
        if (parmAct.rndsperday != 0) {

            verifyActSlot.checkSched(slotParms, con); // OK
        }

     } catch (Exception e21) {

        out.println("<!-- ERROR RETURNED FROM verifyActSlot.checkSched -->");
        dbError(out, e21);
        return;
     }

     if (slotParms.hit == true || slotParms.hit2 == true || slotParms.hit3 == true) { // if we hit on a duplicate res

        out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
        out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<BR><BR><H3>Member Already Playing</H3><BR>");

        if (parmAct.rndsperday > 1) {           // if multiple rounds per day supported

           if (slotParms.hit3 == true) {        // if rounds too close together

               out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is scheduled to play another set within " +parmAct.minutesbtwn+ " minutes.<br><br>");
               out.println(slotParms.player + " is already scheduled to play on this date at <b>" + SystemUtils.getSimpleTime(slotParms.time2) + "</b>.<br><br>");

           } else {

               out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is already scheduled to play the maximum number of times.<br><br>");
               out.println("A player can only be scheduled " +parmAct.rndsperday+ " times per day.<br><br>");

           }

        } else {

           if (slotParms.hit2 == true) {
              out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is part of a lottery request for this date.<br><br>");
           } else {
              out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is already scheduled to play on this date at <b>" + SystemUtils.getSimpleTime(slotParms.time2) + "</b>.<br><br>");
           }
           out.println("A player can only be scheduled once per day.<br><br>");

        }

        returnToSlot(out, slotParms);
        out.close();
        return;
     }



     //
     //***********************************************************************************************
     //
     //    Now check all players for 'days in advance' - based on membership types
     //
     //***********************************************************************************************
     //
     if (!slotParms.mship1.equals( "" ) || !slotParms.mship2.equals( "" ) || !slotParms.mship3.equals( "" ) ||
         !slotParms.mship4.equals( "" ) || !slotParms.mship5.equals( "" )) {


       try {

          error = verifySlot.checkDaysAdv(slotParms, con); // OK

       }
       catch (Exception e21) {

          out.println("<!-- ERROR RETURNED FROM verifySlot.checkDaysAdv -->");
          dbError(out, e21);
          return;
       }

       if (error == true) {          // if we hit on a violation

          out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
          out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
          out.println("<hr width=\"40%\">");
          out.println("<BR><BR><H3>Days in Advance Exceeded for Member</H3><BR>");
          out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is not allowed to be part of a reservation this far in advance.<br><br>");
          out.println("This restriction is based on the 'Days In Advance' setting for each Membership Type.<br><br>");

          //
          //  Return to _slot
          //
          returnToSlot(out, slotParms);
          out.close();
          return;
       }

     } // end if at least one member



     if ( skip < 8 ) {

     //
     //***********************************************************************************************
     //
     //    Now check the order of guests and members (guests must follow a member) - prompt to verify order
     //
     //***********************************************************************************************
     //
     if (slotParms.guests != 0 && slotParms.members != 0) {      // if both guests and members were included

        if (slotParms.g1.equals( "" )) {              // if slot 1 is not a guest

           //
           //  Both guests and members specified - determine guest owners by order
           //
           gi = 0;
           memberName = "";
           while (gi < 4) {                  // cycle thru arrays and find guests/members !!!!!!!!!!!!!! Change this to allow for max players!!!!!!!!!!!!!!!!

              if (!slotParms.gstA[gi].equals( "" )) {

                 usergA[gi] = memberName;       // get last players username
              } else {
                 usergA[gi] = "";               // init field
              }
              //if (!memA[gi].equals( "" )) {

                 //memberName = memA[gi];        // get players username
              //}
              gi++;
           }
           slotParms.userg1 = usergA[0];        // max of 4 guests since 1 player must be a member to get here
           slotParms.userg2 = usergA[1];
           slotParms.userg3 = usergA[2];
           slotParms.userg4 = usergA[3];
           // slotParms.userg5 = usergA[4];
        }

        if (!slotParms.g1.equals( "" ) || slotParms.members > 1) {  // if slot 1 is a guest OR more than 1 member

           //
           //  At least one guest and one member have been specified.
           //  Prompt user to verify the order.
           //
           //  Only require positioning if a POS system was specified for this club (saved in Login)
           //
           out.println(SystemUtils.HeadTitle("Guests Specified - Prompt"));
           out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
           out.println("<hr width=\"40%\">");
           out.println("<BR><BR><H3>Player/Guest Association Prompt</H3><BR>");

           //
           // if slot 1 is a guest & POS & not already assigned
           //
           if (!slotParms.g1.equals( "" ) && !posType.equals( "" ) && !slotParms.oldPlayer1.equals( slotParms.player1 )) {

              out.println("Guests must be specified <b>immediately after</b> the member they belong to.<br><br>");
              out.println("You cannot have a guest in the first player position when one or more members are also specified.");
              out.println("<BR><BR>");
           } else {
              out.println("Guests should be specified <b>immediately after</b> the member they belong to.<br><br>");
              out.println("Please verify that the following order is correct:");
              out.println("<BR><BR>");
              out.println(slotParms.player1 + " <BR>");
              out.println(slotParms.player2 + " <BR>");
              if (!slotParms.player3.equals( "" )) {
                 out.println(slotParms.player3 + " <BR>");
              }
              if (!slotParms.player4.equals( "" )) {
                 out.println(slotParms.player4 + " <BR>");
              }
              if (!slotParms.player5.equals( "" )) {
                 out.println(slotParms.player5 + " <BR>");
              }
              out.println("<BR>Would you like to process the request as is?");
           }

           //
           //  Return to _slot
           //
           returnToSlot(out, slotParms, 8);
           out.close();
           return;

        }

     } else {

        //
        //  Either all members or all guests - check for all guests (Unaccompanied Guests)
        //
        if (slotParms.guests != 0) {      // if all guests

           //
           //  At least one guest and no member has been specified.
           //  Get associated member names if already assigned.
           //
           try {

              if (!slotParms.userg1.equals( "" )) {

                 PreparedStatement pstmtc = con.prepareStatement (
                    "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

                 pstmtc.clearParameters();        // clear the parms
                 pstmtc.setString(1, slotParms.userg1);

                 rs = pstmtc.executeQuery();

                 if (rs.next()) {

                    // Get the member's full name.......

                    StringBuffer mem_name = new StringBuffer(rs.getString(2));  // get first name

                    String mi = rs.getString(3);                                // middle initial
                    if (!mi.equals( "" )) {
                       mem_name.append(" ");
                       mem_name.append(mi);
                    }
                    mem_name.append(" " + rs.getString(1));                     // last name

                    slotParms.mem1 = mem_name.toString();                      // convert to one string
                 }
                 pstmtc.close();
              }
              if (!slotParms.userg2.equals( "" )) {

                 PreparedStatement pstmtc = con.prepareStatement (
                    "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

                 pstmtc.clearParameters();        // clear the parms
                 pstmtc.setString(1, slotParms.userg2);

                 rs = pstmtc.executeQuery();

                 if (rs.next()) {

                    // Get the member's full name.......

                    StringBuffer mem_name = new StringBuffer(rs.getString(2));  // get first name

                    String mi = rs.getString(3);                                // middle initial
                    if (!mi.equals( "" )) {
                       mem_name.append(" ");
                       mem_name.append(mi);
                    }
                    mem_name.append(" " + rs.getString(1));                     // last name

                    slotParms.mem2 = mem_name.toString();                          // convert to one string
                 }
                 pstmtc.close();
              }
              if (!slotParms.userg3.equals( "" )) {

                 PreparedStatement pstmtc = con.prepareStatement (
                    "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

                 pstmtc.clearParameters();        // clear the parms
                 pstmtc.setString(1, slotParms.userg3);

                 rs = pstmtc.executeQuery();

                 if (rs.next()) {

                    // Get the member's full name.......

                    StringBuffer mem_name = new StringBuffer(rs.getString(2));  // get first name

                    String mi = rs.getString(3);                                // middle initial
                    if (!mi.equals( "" )) {
                       mem_name.append(" ");
                       mem_name.append(mi);
                    }
                    mem_name.append(" " + rs.getString(1));                     // last name

                    slotParms.mem3 = mem_name.toString();                          // convert to one string
                 }
                 pstmtc.close();
              }
              if (!slotParms.userg4.equals( "" )) {

                 PreparedStatement pstmtc = con.prepareStatement (
                    "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

                 pstmtc.clearParameters();        // clear the parms
                 pstmtc.setString(1, slotParms.userg4);

                 rs = pstmtc.executeQuery();

                 if (rs.next()) {

                    // Get the member's full name.......

                    StringBuffer mem_name = new StringBuffer(rs.getString(2));  // get first name

                    String mi = rs.getString(3);                                // middle initial
                    if (!mi.equals( "" )) {
                       mem_name.append(" ");
                       mem_name.append(mi);
                    }
                    mem_name.append(" " + rs.getString(1));                     // last name

                    slotParms.mem4 = mem_name.toString();                          // convert to one string
                 }
                 pstmtc.close();
              }
              if (!slotParms.userg5.equals( "" )) {

                 PreparedStatement pstmtc = con.prepareStatement (
                    "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

                 pstmtc.clearParameters();        // clear the parms
                 pstmtc.setString(1, slotParms.userg5);

                 rs = pstmtc.executeQuery();

                 if (rs.next()) {

                    // Get the member's full name.......

                    StringBuffer mem_name = new StringBuffer(rs.getString(2));  // get first name

                    String mi = rs.getString(3);                                // middle initial
                    if (!mi.equals( "" )) {
                       mem_name.append(" ");
                       mem_name.append(mi);
                    }
                    mem_name.append(" " + rs.getString(1));                     // last name

                    slotParms.mem5 = mem_name.toString();                          // convert to one string
                 }
                 pstmtc.close();
              }
           }
           catch (Exception ignore) {
           }

           //
           //  Prompt user to specify associated member(s) or skip.
           //
           out.println(SystemUtils.HeadTitle("Guests Specified - Prompt"));
           out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
           out.println("<hr width=\"40%\">");
           out.println("<BR><BR><H3>Player/Guest Association Prompt</H3><BR>");


           if (slotParms.guests == 1) {      // if one guest
             out.println("You are requesting a reservation for an unaccompanied guest.<br>");
             out.println("The guest should be associated with a member.<br><br>");
             out.println("<BR>Would you like to assign a member to the guest, or change the assignment?");
           } else {
             out.println("You are requesting a reservation for unaccompanied guests.<br>");
             out.println("Guests should be associated with a member.<br><br>");
             out.println("<BR>Would you like to assign a member to the guests, or change the assignments?");
           }

           //
           //  Return to _slot
           //
           returnToSlot(out, slotParms);
           out.close();
           return;

        }
     }      // end of IF any guests specified

     } // end skip guest ordering


     //
     //***********************************************************************************************
     //
     //  Now that the guests are assigned, check for any Guest Quotas - if any guests requested
     //
     //***********************************************************************************************
     //
     if (!slotParms.userg1.equals( "" ) || !slotParms.userg2.equals( "" ) || !slotParms.userg3.equals( "" ) ||
         !slotParms.userg4.equals( "" ) || !slotParms.userg5.equals( "" )) {

        try {

           error = verifySlot.checkGuestQuota(slotParms, con); // OK

        }
        catch (Exception e22) {
        }

        if (error == true) {          // if we hit on a violation

           out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
           out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
           out.println("<hr width=\"40%\">");
           out.println("<BR><BR><H3>Guest Quota Exceeded for Member</H3><BR>");
           out.println("<BR>Sorry, requesting <b>" + slotParms.player + "</b> exceeds the guest quota established for this guest type.");
           out.println("<br><br>You will have to remove the guest in order to complete this request.");
           out.println("<BR><BR>");
           //
           //  Return to _slot (doPost) to assign members
           //
           returnToSlot(out, slotParms);
           out.close();
           return;
        }

     }   // end of IF guests
     

      //
      //  We must restore the guest usernames
      //
      slotParms.userg1 = (req.getParameter("userg1") == null) ? "" : req.getParameter("userg1");
      slotParms.userg2 = (req.getParameter("userg2") == null) ? "" : req.getParameter("userg2");
      slotParms.userg3 = (req.getParameter("userg3") == null) ? "" : req.getParameter("userg3");
      slotParms.userg4 = (req.getParameter("userg4") == null) ? "" : req.getParameter("userg4");
      slotParms.userg5 = (req.getParameter("userg5") == null) ? "" : req.getParameter("userg5");

      //
      //  Before we update the time slot, go check for any flags to be added to members' names for the pro tee sheet
      //
      verifySlot.checkTFlag(slotParms, con);



      //**************************************************************
      //  Verification Complete !!!!!!!!
      //**************************************************************

      sendemail = 0;         // init email flags
      emailNew = 0;
      emailMod = 0;

      //
      //  Make sure there is a member in the time slot slot
      //    If not, no email and no statistic counted
      //
      if (((!slotParms.player1.equals( "" )) && (!slotParms.player1.equalsIgnoreCase( "x" )) && (slotParms.g1.equals( "" ))) ||
          ((!slotParms.player2.equals( "" )) && (!slotParms.player2.equalsIgnoreCase( "x" )) && (slotParms.g2.equals( "" ))) ||
          ((!slotParms.player3.equals( "" )) && (!slotParms.player3.equalsIgnoreCase( "x" )) && (slotParms.g3.equals( "" ))) ||
          ((!slotParms.player4.equals( "" )) && (!slotParms.player4.equalsIgnoreCase( "x" )) && (slotParms.g4.equals( "" ))) ||
          ((!slotParms.player5.equals( "" )) && (!slotParms.player5.equalsIgnoreCase( "x" )) && (slotParms.g5.equals( "" )))) {

         //
         //  If players changed, then set email flag
         //
         // see if the player has changed - send email notification to all if true
         // if new time slot oldPlayer1 will be empty 
         //
         if (!slotParms.player1.equals( slotParms.oldPlayer1 )) {
            sendemail = 1;
         }

         if (!slotParms.player2.equals( slotParms.oldPlayer2 )) {
            sendemail = 1;
         }

         if (!slotParms.player3.equals( slotParms.oldPlayer3 )) {
            sendemail = 1;
         }

         if (!slotParms.player4.equals( slotParms.oldPlayer4 )) {
            sendemail = 1;
         }

         if (!slotParms.player5.equals( slotParms.oldPlayer5 )) {
            sendemail = 1;
         }

         //
         //  Verification complete -
         //   Set email type based on new or update request (cancel set above)
         //   Also, bump stats counters for reports
         //
         if ((!slotParms.oldPlayer1.equals( "" )) || (!slotParms.oldPlayer2.equals( "" )) || (!slotParms.oldPlayer3.equals( "" )) ||
             (!slotParms.oldPlayer4.equals( "" )) || (!slotParms.oldPlayer5.equals( "" ))) {

            memMod++;      // increment number of mods
            emailMod = 1;  // time slot was modified

         } else {

            memNew++;      // increment number of new time slots
            emailNew = 1;  // time slot is new
         }
      }

      //
      //  Set show values
      //
      if (slotParms.player1.equals( "" ) || slotParms.player1.equalsIgnoreCase( "x" )) {

         slotParms.show1 = 0;       // reset show parm if no player
      }
        
      if (slotParms.player2.equals( "" ) || slotParms.player2.equalsIgnoreCase( "x" )) {

         slotParms.show2 = 0;       // reset show parm if no player
      }

      if (slotParms.player3.equals( "" ) || slotParms.player3.equalsIgnoreCase( "x" )) {

         slotParms.show3 = 0;       // reset show parm if no player
      }

      if (slotParms.player4.equals( "" ) || slotParms.player4.equalsIgnoreCase( "x" )) {

         slotParms.show4 = 0;       // reset show parm if no player
      }

      if (slotParms.player5.equals( "" ) || slotParms.player5.equalsIgnoreCase( "x" )) {

         slotParms.show5 = 0;       // reset show parm if no player
      }

      //
      //   set show value if double check-in feature supported
      //
      if ((!slotParms.player1.equals( "" ) && !slotParms.player1.equalsIgnoreCase( "x" )) ||
          (!slotParms.player2.equals( "" ) && !slotParms.player2.equalsIgnoreCase( "x" )) ||
          (!slotParms.player3.equals( "" ) && !slotParms.player3.equalsIgnoreCase( "x" )) ||
          (!slotParms.player4.equals( "" ) && !slotParms.player4.equalsIgnoreCase( "x" )) ||
          (!slotParms.player5.equals( "" ) && !slotParms.player5.equalsIgnoreCase( "x" ))) {

         // set show values to 2 if feature is supported and teetime is today
         GregorianCalendar cal_pci = new GregorianCalendar();
         short tmp_pci = (
            parm.precheckin == 1 &&
            mm == (cal_pci.get(cal_pci.MONTH) + 1) &&
            dd == cal_pci.get(cal_pci.DAY_OF_MONTH) &&
            yy == cal_pci.get(cal_pci.YEAR)
         ) ? (short)2 : (short)0;

         //
         //  If players changed and have not already been check in, then set the new no-show value
         //
         if (!slotParms.player1.equals( slotParms.oldPlayer1 ) && slotParms.show1 == 0) {
            slotParms.show1 = tmp_pci;
         }

         if (!slotParms.player2.equals( slotParms.oldPlayer2 ) && slotParms.show2 == 0) {
            slotParms.show2 = tmp_pci;
         }

         if (!slotParms.player3.equals( slotParms.oldPlayer3 ) && slotParms.show3 == 0) {
            slotParms.show3 = tmp_pci;
         }

         if (!slotParms.player4.equals( slotParms.oldPlayer4 ) && slotParms.show4 == 0) {
            slotParms.show4 = tmp_pci;
         }

         if (!slotParms.player5.equals( slotParms.oldPlayer5 ) && slotParms.show5 == 0) {
            slotParms.show5 = tmp_pci;
         }
      }     // end set show values

      //
      //  Adjust POS values if necessary
      //
      if ((!slotParms.player1.equals( "" ) && !slotParms.player1.equalsIgnoreCase( "x" )) ||
          (!slotParms.player2.equals( "" ) && !slotParms.player2.equalsIgnoreCase( "x" )) ||
          (!slotParms.player3.equals( "" ) && !slotParms.player3.equalsIgnoreCase( "x" )) ||
          (!slotParms.player4.equals( "" ) && !slotParms.player4.equalsIgnoreCase( "x" )) ||
          (!slotParms.player5.equals( "" ) && !slotParms.player5.equalsIgnoreCase( "x" ))) {

         //
         //  If player has changed and pos already sent, then reset the pos value
         //
         if (!slotParms.player1.equals( slotParms.oldPlayer1 ) && slotParms.pos1 == 1) {
            slotParms.pos1 = 0;
         }

         if (!slotParms.player2.equals( slotParms.oldPlayer2 ) && slotParms.pos2 == 1) {
            slotParms.pos2 = 0;
         }

         if (!slotParms.player3.equals( slotParms.oldPlayer3 ) && slotParms.pos3 == 1) {
            slotParms.pos3 = 0;
         }

         if (!slotParms.player4.equals( slotParms.oldPlayer4 ) && slotParms.pos4 == 1) {
            slotParms.pos4 = 0;
         }

         if (!slotParms.player5.equals( slotParms.oldPlayer5 ) && slotParms.pos5 == 1) {
            slotParms.pos5 = 0;
         }
      }        // end pos tests

      
      
   }  // end of IF 'Cancel time slot' ELSE 'Process normal res request'

   
   
    //
    //  Verification complete -
    //  Update the activity slot record in the activity sheet tables
    //

    for (int i = 0; i < slotParms.sheet_ids.size(); i++) {

        // if we're here for consecutive times
        if (slotParms.sheet_ids.size() > 0) {
            
            // set the slot_id for the time we are updating
            slot_id = slotParms.sheet_ids.get(i);

            // if not the first time and not canceling then set report_ignore to 1
            if (i > 0 && req.getParameter("remove") == null) {

                slotParms.report_ignore = 1;
            }
        }

        try {

            PreparedStatement pstmt6 = con.prepareStatement (
                "UPDATE activity_sheets " +
                "SET notes = ?, disallow_joins = ?, " +
                "last_mod_by = ?, last_mod_date = now(), " +
                "memNew = ?, memMod = ?, related_ids = ? , report_ignore = ? " +
                "WHERE sheet_id = ?");

            pstmt6.setString(1, slotParms.notes);
            pstmt6.setInt(2, slotParms.disallow_joins);
            pstmt6.setString(3, user);
            pstmt6.setInt(4, memNew);
            pstmt6.setInt(5, memMod);
            pstmt6.setString(6, slotParms.in_slots);
            pstmt6.setInt(7, slotParms.report_ignore);
            pstmt6.setInt(8, slot_id);
            pstmt6.executeUpdate();
            
            // add/update players
            updateActivityPlayer(slot_id, slotParms.player1, slotParms.oldPlayer1, slotParms.user1, slotParms.userg1, 1, con, out);
            updateActivityPlayer(slot_id, slotParms.player2, slotParms.oldPlayer2, slotParms.user2, slotParms.userg2, 2, con, out);
            updateActivityPlayer(slot_id, slotParms.player3, slotParms.oldPlayer3, slotParms.user3, slotParms.userg3, 3, con, out);
            updateActivityPlayer(slot_id, slotParms.player4, slotParms.oldPlayer4, slotParms.user4, slotParms.userg4, 4, con, out);
            updateActivityPlayer(slot_id, slotParms.player5, slotParms.oldPlayer5, slotParms.user5, slotParms.userg5, 5, con, out);

            // clear in_use fields
            pstmt6 = con.prepareStatement (
                "UPDATE activity_sheets SET in_use_by = '', in_use_at = '0000-00-00 00:00:00' WHERE sheet_id = ?");

            pstmt6.clearParameters();
            pstmt6.setInt(1, slot_id);
            pstmt6.executeUpdate();
            pstmt6.close();

       } catch (Exception e1) {

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

          out.close();
          return;

       }

   } // end consec loop


   //Utilities.logError("DEBUG-MEM: slotParms.in_slots=" + slotParms.in_slots + ", slot_id=" + slot_id + ", time=" + slotParms.time);


   // if the array was populated then we know we played with the slot_id directly above
   if (slotParms.sheet_ids.size() != 0) {

       slotParms.time = time;   // reset the time too (not sure where it's getting changed - maybe in one of the calls to verifySlot)
       slot_id = slotParms.sheet_ids.get(0);
   }


   //Utilities.logError("DEBUG-MEM: slot_id=" + slot_id + ", new time=" + slotParms.time);


    String players = "";

    if (slotParms.player1.equals("")) {

        players = "None";

    } else {

        // use player array when we need more than 5

        players = "1: " + slotParms.player1;

        if (!slotParms.player2.equals("")) {
            players += ", 2: " + slotParms.player2;
        }
        if (!slotParms.player3.equals("")) {
            players += ", 3: " + slotParms.player3;
        }
        if (!slotParms.player4.equals("")) {
            players += ", 4: " + slotParms.player4;
        }
        if (!slotParms.player5.equals("")) {
            players += ", 5: " + slotParms.player5;
        }

    }
    
    for (int i = 0; i < slotParms.sheet_ids.size(); i++) {

        SystemUtils.updateActHist(slotParms.sheet_ids.get(i), user, players, con);

    }

    
   //
   //  Build the HTML page to confirm reservation for user
   //
   //  These returns will pause for 1 second, then return automatically if meta supported, else user clicks on 'return'
   //   
   //
   out.println("<HTML>");
   out.println("<HEAD>");
   out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
   out.println("<title>Member Slot Page</title>");
//bad out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Member_jump?index=" + index + "&course=" + slotParms.course + "&jump=" + slotParms.jump + "\">");

   out.println("<meta http-equiv=\"Refresh\" content=\"2; url=/" +rev+ "/servlet/Member_jump?activity=yes&last_tab=" +activity_id+ "&activity_id=" +group_id+ "&date=" +date+ "&layout_mode=" + slotParms.layout_mode + "\">");

   out.println("</HEAD>");
   out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");

   if (req.getParameter("remove") != null) {

      out.println("<p>&nbsp;</p><p>&nbsp;<b>Thank you!</b>&nbsp;&nbsp;The reservation has been cancelled.</p>");

   } else {

      out.println("<p>&nbsp;</p><p>&nbsp;<b>Thank you!</b>&nbsp;&nbsp;Your reservation " +
              ((slotParms.sheet_ids.size() > 0) ? "for " + slotParms.sheet_ids.size() + " times " : "") +
              "has been accepted and processed.</p>");

   }

   out.println("<p>&nbsp;</p></font>");

   out.println("<table border=\"0\" cols=\"1\" bgcolor=\"#8B8970\" cellpadding=\"8\">");
   out.println("<form action=\"/" +rev+ "/servlet/Member_jump\" method=\"post\" target=\"_top\">");
   out.println("<input type=\"hidden\" name=\"activity\" value=\"\">"); // flag for Member_jump
   out.println("<input type=\"hidden\" name=\"last_tab\" value=\"" + activity_id + "\">"); // used to default to the correct tab
   //out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
   out.println("<input type=\"hidden\" name=\"activity_id\" value=\"" + group_id + "\">");
   out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
   out.println("<input type=\"hidden\" name=\"jump\" value=" + slotParms.jump + ">");
   out.println("<input type=\"hidden\" name=\"layout_mode\" value=" + slotParms.layout_mode + ">");
   out.println("<tr><td><font size=\"2\">");
   out.println("<input type=\"submit\" value=\"Return\">");
   out.println("</font></td></tr></form></table>");
   
   //
   //  End of HTML page
   //
   out.println("</center></font></body></html>");
   out.close();

   try {

      resp.flushBuffer();      // force the repsonse to complete

   } catch (Exception ignore) { }


   //
   //***********************************************
   //  Send email notification if necessary
   //***********************************************
   //
   if ( sendemail != 0 ) {

      int root_id = 0;
      try { root_id = getActivity.getRootIdFromActivityId(activity_id, con); }
      catch (Exception ignore) {}

      String activity_name = "";
      try { activity_name = getActivity.getActivityName(root_id, con); }
      catch (Exception ignore) {}

      int slot_activity_id = 0;
      try { slot_activity_id = getActivity.getActivityIdFromSlotId(slot_id, con); }
      catch (Exception exc) { out.println("<!-- ERROR GETTING ACTIVITY ID: " + exc.toString() + " -->"); }

      String actual_activity_name = "";
      try { actual_activity_name = getActivity.getActivityName(slot_activity_id, con); }
      catch (Exception ignore) {}

      //
      //  allocate a parm block to hold the email parms
      //
      parmEmail parme = new parmEmail();          // allocate an Email parm block

      //
      //  Set the values in the email parm block
      //
      parme.type = "activity";         // type = time slot
      parme.activity_name = activity_name;
      parme.actual_activity_name = actual_activity_name;

      parme.date = slotParms.date;
      parme.time = slotParms.time;
      parme.mm = slotParms.mm;
      parme.dd = slotParms.dd;
      parme.yy = slotParms.yy;
      parme.day = slotParms.day;
      parme.notes = slotParms.notes;
      parme.hideNotes = hide; //Integer.parseInt(slotParms.hides);

      parme.user = user;
      parme.emailNew = emailNew;
      parme.emailMod = emailMod;
      parme.emailCan = emailCan;

      parme.player1 = slotParms.player1;
      parme.player2 = slotParms.player2;
      parme.player3 = slotParms.player3;
      parme.player4 = slotParms.player4;
      parme.player5 = slotParms.player5;

      parme.oldplayer1 = slotParms.oldPlayer1;
      parme.oldplayer2 = slotParms.oldPlayer2;
      parme.oldplayer3 = slotParms.oldPlayer3;
      parme.oldplayer4 = slotParms.oldPlayer4;
      parme.oldplayer5 = slotParms.oldPlayer5;

      parme.user1 = slotParms.user1;
      parme.user2 = slotParms.user2;
      parme.user3 = slotParms.user3;
      parme.user4 = slotParms.user4;
      parme.user5 = slotParms.user5;

      parme.olduser1 = slotParms.oldUser1;
      parme.olduser2 = slotParms.oldUser2;
      parme.olduser3 = slotParms.oldUser3;
      parme.olduser4 = slotParms.oldUser4;
      parme.olduser5 = slotParms.oldUser5;

      
      //
      //  Send the email
      //
      sendEmail.sendIt(parme, con);      // in common

   }     // end of IF sendemail
     
 }       // end of Verify


 // *********************************************************
 //  Process cancel request from Member_activity_slot (HTML) - 'Go Back'
 // *********************************************************

 private void cancel(HttpServletRequest req, PrintWriter out, String club, String user, Connection con) {

     
    PreparedStatement pstmt = null;

    int slot_id = 0;
    int group_id = 0;
    int activity_id = 0;

    //
    // Get all the parameters entered
    //
    String date = req.getParameter("date");           //  date of activity time requested (yyyymmdd)
    String index = req.getParameter("index");
    String sid = req.getParameter("slot_id");
    String gid = req.getParameter("group_id");
    String aid = req.getParameter("activity_id");
    String layout = req.getParameter("layout_mode");
    String in_slots = req.getParameter("in_slots");
    
    //
    //  Convert the values from string to int
    //
    try { slot_id = Integer.parseInt(sid); }
    catch (NumberFormatException ignore) { out.println("<!-- ERROR PARSING SLOT ID (" + sid + ") -->"); }

    try { group_id = Integer.parseInt(gid); }
    catch (NumberFormatException ignore) { out.println("<!-- ERROR PARSING GROUP ID (" + gid + ") -->"); }

    try { activity_id = Integer.parseInt(aid); }
    catch (NumberFormatException ignore) { out.println("<!-- ERROR PARSING ACTIVITY ID (" + aid + ") -->"); }

    //out.println("<!-- slot_id=" + slot_id + " | user=" + user + " | in_slots=" + in_slots + " -->");

    try { activity_id = getActivity.getActivityIdFromSlotId(slot_id, con); }
    catch (Exception exc) { out.println("<!-- ERROR GETTING ACTIVITY ID: " + exc.toString() + " -->"); }

    //
    //  Clear the 'in_use' flag for this time slot in activity_sheets
    //
    try {

        String sql = "" +
                "UPDATE activity_sheets " +
                "SET in_use_by = '', in_use_at = '0000-00-00 00:00:00' " +
                "WHERE in_use_by = ? AND sheet_id " + ((in_slots.equals("")) ? "= ?" : "IN (" + in_slots + ")");

        //out.println("<!-- sql=" + sql + " -->");

        pstmt = con.prepareStatement ( sql );

        pstmt.clearParameters();
        pstmt.setString(1, user);
        if (in_slots.equals("")) pstmt.setInt(2, slot_id);

        pstmt.executeUpdate();

    } catch (Exception ignore) { 
        
    } finally {

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    //
    //  Prompt user to return to Member_gensheet or Member_searchmem (index = 888) - handled in Member_jump
    //
    //  These returns will pause for 1 second, then return automatically if meta supported, else user clicks on 'return'
    //
     out.println("<HTML>");
     out.println("<HEAD>");
     out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
     out.println("<Title>Member Slot Page</Title>");
     //old out.println("<meta http-equiv=\"Refresh\" content=\"2; url=/" +rev+ "/servlet/Member_jump?activity=&date=" + date + "&index=" + index + "&layout_mode=" + layout + "&sheet_id=" + slot_id + "&group_id=" + group_id + "&parent_id=" + parent_id + "\">");
     out.println("<meta http-equiv=\"Refresh\" content=\"2; url=/" +rev+ "/servlet/Member_jump?activity=&date=" + date + "&index=" + index + "&layout_mode=" + layout + "&activity_id=" + group_id + "&last_tab=" + activity_id + "\">"); // &sheet_id=" + slot_id + "
     out.println("</HEAD>");
     out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
     out.println("<hr width=\"40%\">");
     out.println("<BR><BR><H3>Return/Cancel Requested</H3>");
     out.println("<BR><BR>Thank you, the time slot has been returned to the system without changes.");
     out.println("<BR><BR>");

     out.println("<font size=\"2\">");
     out.println("<form action=\"/" +rev+ "/servlet/Member_jump\" method=\"post\" target=\"_top\">");
     out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
     out.println("<input type=\"hidden\" name=\"activity\" value=\"\">");   // flag for Member_jump
     //out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
     out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
     out.println("<input type=\"hidden\" name=\"layout_mode\" value=" + layout + ">");
     out.println("<input type=\"hidden\" name=\"last_tab\" value=\"" + activity_id + "\">"); // used to default to the correct tab
     out.println("<input type=\"hidden\" name=\"activity_id\" value=\"" + group_id + "\">"); // pass the group_id in for sheets so it get detected 
     out.println("</form></font>");
    out.println("</CENTER></BODY></HTML>");
    out.close();
 }


 private void returnToSlot(PrintWriter out, parmSlot slotParms) {

     // call other method and pass default options so the user can not override
     returnToSlot(out, slotParms, false, "", 0);

 }


 private void returnToSlot(PrintWriter out, parmSlot slotParms, int skip) {

     // call other method and pass default options so the user can override
     returnToSlot(out, slotParms, true, "", skip);

 }


 // *********************************************************
 //  Return to Member_waitlist_slot
 //  TODO: Pass max team size and build player#, show#, userg# to scale
 // *********************************************************
 private void returnToSlot(PrintWriter out, parmSlot slotParms, boolean allowOverride, String user, int skip) {

   //
   //  Prompt user for return
   //
   out.println("<font size=\"2\">");
   out.println("<form action=\"/" +rev+ "/servlet/Member_activity_slot\" method=\"post\" target=\"_top\">");
   out.println("<input type=\"hidden\" name=\"slot_id\" value=\"" + slotParms.slot_id + "\">");
   out.println("<input type=\"hidden\" name=\"group_id\" value=\"" + slotParms.group_id + "\">");
   out.println("<input type=\"hidden\" name=\"activity_id\" value=\"" + slotParms.activity_id + "\">");
   out.println("<input type=\"hidden\" name=\"in_slots\" value=\"" + slotParms.in_slots + "\">");
   out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
   out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
   out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
   out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
   out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
   out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotParms.show1 + "\">");
   out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotParms.show2 + "\">");
   out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotParms.show3 + "\">");
   out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotParms.show4 + "\">");
   out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotParms.show5 + "\">");
   out.println("<input type=\"hidden\" name=\"date\" value=\"" + slotParms.date + "\">");
   out.println("<input type=\"hidden\" name=\"time\" value=\"" + slotParms.time + "\">");
   out.println("<input type=\"hidden\" name=\"mm\" value=\"" + slotParms.mm + "\">");
   out.println("<input type=\"hidden\" name=\"yy\" value=\"" + slotParms.yy + "\">");
   out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
   out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
   out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
   out.println("<input type=\"hidden\" name=\"layout_mode\" value=\"" + slotParms.layout_mode + "\">");
   if (slotParms.disallow_joins == 1) out.println("<input type=\"hidden\" name=\"disallow_joins\" value=\"1\">");
   out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
   out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
   
   out.println("<input type=\"hidden\" name=\"userg1\" value=\"" + slotParms.userg1 + "\">");
   out.println("<input type=\"hidden\" name=\"userg2\" value=\"" + slotParms.userg2 + "\">");
   out.println("<input type=\"hidden\" name=\"userg3\" value=\"" + slotParms.userg3 + "\">");
   out.println("<input type=\"hidden\" name=\"userg4\" value=\"" + slotParms.userg4 + "\">");
   out.println("<input type=\"hidden\" name=\"userg5\" value=\"" + slotParms.userg5 + "\">");
   
   out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");

   if (allowOverride) { // add check here to see if user has permission to override restrictions

      out.println("<form action=\"/" +rev+ "/servlet/Member_activity_slot\" method=\"post\" target=\"_top\">");

      out.println("<input type=\"hidden\" name=\"skip\" value=\"" +skip+ "\">");

      out.println("<input type=\"hidden\" name=\"slot_id\" value=\"" + slotParms.slot_id + "\">");
      out.println("<input type=\"hidden\" name=\"group_id\" value=\"" + slotParms.group_id + "\">");
      out.println("<input type=\"hidden\" name=\"activity_id\" value=\"" + slotParms.activity_id + "\">");
      out.println("<input type=\"hidden\" name=\"in_slots\" value=\"" + slotParms.in_slots + "\">");
      out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
      out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
      out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
      out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
      out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
      out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotParms.show1 + "\">");
      out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotParms.show2 + "\">");
      out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotParms.show3 + "\">");
      out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotParms.show4 + "\">");
      out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotParms.show5 + "\">");
      out.println("<input type=\"hidden\" name=\"date\" value=\"" + slotParms.date + "\">");
      out.println("<input type=\"hidden\" name=\"time\" value=\"" + slotParms.time + "\">");
      out.println("<input type=\"hidden\" name=\"mm\" value=\"" + slotParms.mm + "\">");
      out.println("<input type=\"hidden\" name=\"yy\" value=\"" + slotParms.yy + "\">");
      out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
      out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
      out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
      out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
      out.println("<input type=\"hidden\" name=\"layout_mode\" value=\"" + slotParms.layout_mode + "\">");
      if (slotParms.disallow_joins == 1) out.println("<input type=\"hidden\" name=\"disallow_joins\" value=\"1\">");

      out.println("<input type=\"hidden\" name=\"userg1\" value=\"" + slotParms.userg1 + "\">");
      out.println("<input type=\"hidden\" name=\"userg2\" value=\"" + slotParms.userg2 + "\">");
      out.println("<input type=\"hidden\" name=\"userg3\" value=\"" + slotParms.userg3 + "\">");
      out.println("<input type=\"hidden\" name=\"userg4\" value=\"" + slotParms.userg4 + "\">");
      out.println("<input type=\"hidden\" name=\"userg5\" value=\"" + slotParms.userg5 + "\">");

      out.println("<input type=\"submit\" value=\"YES\" name=\"submitForm\"></form>");
   }

   out.println("</CENTER></BODY></HTML>");
   out.close();
 }

/*
 // *********************************************************
 //  Prompt user when a different time slot is available.
 // *********************************************************

 private void promptOtherTime(PrintWriter out, parmSlot parm) {


   String stime = "";
   String ampm = "";
   String omit = "";

   String sfb = "Front";
   
   if (parm.fb == 1) {
      sfb = "Back";
   }

   int time = parm.time;
   int hr = 0;
   int min = 0;


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

   //
   //  Prompt the user to either accept the times available or return to the tee sheet
   //
   out.println("<HTML><HEAD>");
   out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
   out.println("<Title>Member Prompt - Alternate Time Request</Title>");
   out.println("</HEAD>");

   out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

      out.println("<table border=\"0\" width=\"100%\" align=\"center\" valign=\"top\">");  // large table for whole page
      out.println("<tr><td valign=\"top\" align=\"center\">");
         out.println("<p>&nbsp;&nbsp;</p>");
         out.println("<p>&nbsp;&nbsp;</p>");

      out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"3\">");
         out.println("<tr>");
         out.println("<td width=\"580\" align=\"center\">");
         out.println("<font size=\"3\">");
         out.println("<b>NOTICE</b><br></font>");
         out.println("<font size=\"2\">");
         out.println("<br>The time you requested is currently busy.<br>");
         out.println("The following time is the next available:<br><br>");
         out.println("&nbsp;&nbsp;&nbsp;" +stime+ "<br>");
         out.println("<br>Would you like to accept this time?<br>");
         out.println("</font><font size=\"3\">");
         out.println("<br><b>Please select your choice below. DO NOT use you browser's BACK button!</b><br>");
         out.println("</font></td></tr>");
         out.println("</table><br>");

         out.println("<table border=\"0\" cols=\"1\" cellpadding=\"3\">");
         out.println("<tr><td align=\"center\">");
         out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Member_activity_slot\" method=\"post\" name=\"can\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + parm.index + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + parm.course + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + parm.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + parm.day + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + parm.date + "\">");
            out.println("<input type=\"hidden\" name=\"time\" value=\"" + parm.time + "\">");
            out.println("<input type=\"hidden\" name=\"fb\" value=\"" + parm.fb + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + parm.jump + "\">");
            out.println("<input type=\"hidden\" name=\"layout_mode\" value=\"" + parm.layout_mode + "\">");
            out.println("<input type=\"hidden\" name=\"cancel\" value=\"yes\">");
            out.println("<input type=\"submit\" value=\"NO - Return to Time Sheet\"></form>");
         out.println("</font></td></tr>");

         out.println("<tr><td align=\"center\">");
         out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Member_activity_slot\" method=\"post\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + parm.index + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + parm.course + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + parm.jump + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + parm.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + parm.date + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + parm.day + "\">");
            out.println("<input type=\"hidden\" name=\"stime\" value=\"" + parm.time + "\">");
            out.println("<input type=\"hidden\" name=\"fb\" value=\"" + parm.fb + "\">");
            out.println("<input type=\"hidden\" name=\"p5\" value=\"" + parm.p5 + "\">");
            out.println("<input type=\"hidden\" name=\"player1\" value=\"" +omit+ "\">");
            out.println("<input type=\"hidden\" name=\"player2\" value=\"" +omit+ "\">");    // new time slot requested
            out.println("<input type=\"hidden\" name=\"player3\" value=\"" +omit+ "\">");
            out.println("<input type=\"hidden\" name=\"player4\" value=\"" +omit+ "\">");
            out.println("<input type=\"hidden\" name=\"player5\" value=\"" +omit+ "\">");
            out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" +omit+ "\">");
            out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" +omit+ "\">");
            out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" +omit+ "\">");
            out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" +omit+ "\">");
            out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" +omit+ "\">");
            out.println("<input type=\"hidden\" name=\"notes\" value=\"" +omit+ "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"0\">");
            out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + parm.p5rest + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + parm.jump + "\">");
            out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + parm.orig_by + "\">");
            out.println("<input type=\"hidden\" name=\"layout_mode\" value=\"" + parm.layout_mode + "\">");
            out.println("<input type=\"hidden\" name=\"promptOtherTime\" value=\"yes\">");
            out.println("<input type=\"submit\" value=\"YES - Continue\"></form>");
         out.println("</font></td></tr>");
         out.println("</table>");

         out.println("</td>");
         out.println("</tr>");
      out.println("</table>");
      out.println("</font></center></body></html>");
 }
*/

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
      out.println("<BR><BR>" + e1.toString());
      out.println("<BR><BR>" + e1.getMessage());
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;

 }


 private void updateActivityPlayer(int slot_id, String player_name, String old_player, String username, String userg, int pos, Connection con, PrintWriter out) {

    
    PreparedStatement pstmt = null;
     
    try {

        if ( player_name.equals("") && !old_player.equals("") ) {

            //out.println("<!-- DELETING player"+pos+" | player_name=" + player_name + " | old_player=" + old_player + " -->");

            pstmt = con.prepareStatement ("DELETE FROM activity_sheets_players WHERE activity_sheet_id = ? AND pos = ?");
            pstmt.setInt(1, slot_id);
            pstmt.setInt(2, pos);
            pstmt.executeUpdate();

        } else if ( !player_name.equals(old_player) ) {

            //out.println("<!-- UPDATING player"+pos+" [" + username + " | " + player_name + " | " + userg + "] -->");

            pstmt = con.prepareStatement (
                "INSERT INTO activity_sheets_players " +
                    "(activity_sheet_id, username, player_name, userg, pos) VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                    "activity_sheet_id = VALUES(activity_sheet_id), " +
                    "username = VALUES(username), " +
                    "userg = VALUES(userg), " +
                    "player_name = VALUES(player_name)");

            pstmt.clearParameters();
            pstmt.setInt(1, slot_id);
            pstmt.setString(2, username);
            pstmt.setString(3, player_name);
            pstmt.setString(4, userg);
            pstmt.setInt(5, pos);
            pstmt.executeUpdate();

        } else {

            //out.println("<!-- UNCHANGED player"+pos+" [" + username +  " | " + player_name + "] -->");
        }

    } catch (Exception e) {

        dbError(out, e);
        
    } finally {
        
        try { pstmt.close(); }
        catch (Exception ignore) {}
        
    }

 }


 private void promptOtherTime(PrintWriter out, parmSlot slotParms, Connection con) {


    PreparedStatement pstmt = null;
    ResultSet rs = null;

    ArrayList<String> times = new ArrayList<String>();

    int time = 0;   // this will get set to the first time we found

    try {

        for (int i = 0; i < slotParms.sheet_ids.size(); i++) {

            pstmt = con.prepareStatement("" +
                    "SELECT " +
                        "DATE_FORMAT(date_time, '%l:%i %p') AS stime," +
                        "DATE_FORMAT(date_time, '%k%i') AS mtime " +
                    "FROM activity_sheets " +
                    "WHERE sheet_id = ?");

            pstmt.clearParameters();
            pstmt.setInt(1, slotParms.sheet_ids.get(i));

            rs = pstmt.executeQuery();

            if (rs.next()) times.add(rs.getString(1));

            if (i == 0) time = rs.getInt(2);

        }

    } catch (Exception exc) {

        out.println("Error looking up times for user approval: Error=" + exc.getMessage());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }


    //
    //  Prompt the user to either accept the times available or return to the tee sheet
    //
    out.println("<HTML><HEAD>");
    out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
    out.println("<Title>Member Prompt - Consecutive Time Request</Title>");
    out.println("</HEAD>");

    out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\">");
    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

    out.println("<table border=\"0\" width=\"100%\" align=\"center\" valign=\"top\">");  // large table for whole page
    out.println("<tr><td valign=\"top\" align=\"center\">");
    out.println("<p>&nbsp;&nbsp;</p>");
    out.println("<p>&nbsp;&nbsp;</p>");

    out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"3\">");
    out.println("<tr>");
    out.println("<td width=\"580\" align=\"center\">");
    out.println("<font size=\"3\">");
    out.println("<b>NOTICE</b><br></font>");
    out.println("<font size=\"2\">");
    out.println("<br>One or more of the time slots you requested is currently busy or otherwise unavailable..<br>");
        //out.println("There are " +parm.slots+ " tee times available, as follows:<br><br>");
    out.println("The time we did find for you are as follows:<br><br>");

    for (int i = 0; i < times.size(); i++) {
        out.println("&nbsp;&nbsp;&nbsp;" +times.get(i)+ "<br>");
    }

    out.println("<br>Would you like to accept these times?<br>");
    out.println("</font><font size=\"3\">");
    out.println("<br><b>Please select your choice below. DO NOT use you browser's BACK button!</b><br>");
    out.println("</font></td></tr>");
    out.println("</table><br>");

    out.println("<table border=\"0\" cols=\"1\" cellpadding=\"3\">");
    out.println("<tr><td align=\"center\">");
    out.println("<font size=\"2\">");
        out.println("<form action=\"/" +rev+ "/servlet/Member_activity_slot\" method=\"post\">");
        out.println("<input type=\"hidden\" name=\"slot_id\" value=\"" + slotParms.slot_id + "\">");
        out.println("<input type=\"hidden\" name=\"group_id\" value=\"" + slotParms.group_id + "\">");
        out.println("<input type=\"hidden\" name=\"activity_id\" value=\"" + slotParms.activity_id + "\">");
        out.println("<input type=\"hidden\" name=\"in_slots\" value=\"" + slotParms.in_slots + "\">");
        out.println("<input type=\"hidden\" name=\"layout_mode\" value=\"" + slotParms.layout_mode + "\">");
        out.println("<input type=\"hidden\" name=\"date\" value=\"" + slotParms.date + "\">");
        out.println("<input type=\"hidden\" name=\"cancel\" value=\"yes\">");
        out.println("<input type=\"submit\" value=\"NO - Return to Time Sheet\"></form>");
    out.println("</font></td></tr>");

    out.println("<tr><td align=\"center\">");
    out.println("<font size=\"2\">");
        out.println("<form action=\"/" +rev+ "/servlet/Member_activity_slot\" method=\"post\">");
        out.println("<input type=\"hidden\" name=\"slot_id\" value=\"" + slotParms.sheet_ids.get(0) + "\">");
        out.println("<input type=\"hidden\" name=\"group_id\" value=\"" + slotParms.group_id + "\">");
        out.println("<input type=\"hidden\" name=\"activity_id\" value=\"" + slotParms.activity_id + "\">");
        out.println("<input type=\"hidden\" name=\"in_slots\" value=\"" + slotParms.in_slots + "\">");
        out.println("<input type=\"hidden\" name=\"layout_mode\" value=\"" + slotParms.layout_mode + "\">");
        out.println("<input type=\"hidden\" name=\"date\" value=\"" + slotParms.date + "\">");
        out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
        out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
        out.println("<input type=\"hidden\" name=\"promptOtherTime\" value=\"yes\">");
        out.println("<input type=\"submit\" value=\"YES - Continue\"></form>");
    out.println("</font></td></tr>");
    out.println("</table>");

    out.println("</td>");
    out.println("</tr>");
    out.println("</table>");
    out.println("</font></center></body></html>");

    out.close();

 }


 private parmSlot getConsecParms(parmSlot slotParms, int i, Connection con) {


    PreparedStatement pstmt = null;
    ResultSet rs = null;

    parmSlot parm = new parmSlot();

    parm = slotParms; // copy all the data to the new parm

    try {

        pstmt = con.prepareStatement (
           "SELECT DATE_FORMAT(date_time, '%k%i') AS time " +
           "FROM activity_sheets " +
           "WHERE sheet_id = ?");

        pstmt.clearParameters();
        pstmt.setInt(1, slotParms.sheet_ids.get(i));
        rs = pstmt.executeQuery();

        if (rs.next()) {

            parm.time = rs.getInt(1);

        }

    } catch (Exception exc) {

        verifySlot.logError("getConsecParms: Error=" + exc.getMessage());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}
    }

    return parm;

 }

}
