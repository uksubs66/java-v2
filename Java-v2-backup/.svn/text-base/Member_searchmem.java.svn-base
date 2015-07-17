/***************************************************************************************
 *   Member_searchmem:  This servlet will process the 'search for member' request from
 *                      Member's searchmain page.  It will use the name provided to search
 *                      teecurr for any matches (current tee times).
 *
 *                      Caller only provides the last name, or a portion of it.
 *
 *
 *   called by:  Member_maintop
 *
 *   created: 1/10/2002   Bob P.
 *
 *   last updated:
 *
 *
 *       11/11/13   When displaying a member's tee times, if any are part of a shotgun event, look up the start time for that shotgun and display that time instead.
 *       10/22/13   Updated search to accommodate the Make Tee Time Private option set by proshop users, and to hide private tee times if the current member isn't a part of them (or originator).
 *        4/08/13   Adjusted search so that it will now pull back tee times even if the lottery_email flag has not been set to 0 for a tee time.
 *        1/17/13   Add the Request object to outputBanner, outputSubNav, and outputPageEnd so we can get the session object to test caller.
 *       11/19/12   Fixed issue with member search not working for FlxRez activities.
 *       10/04/12   Update sql statement for mysql 5 compatability
 *        9/06/12   Updated outputTopNav calls to also pass the HttpServletRequest object.
 *        1/22/12   New skin updates.
 *        7/05/11   Forest Highlands GC (foresthighlands) - Added custom so tee times entered from this page hide the 5th player position during the appropriate date range (case 1613).
 *        2/17/11   Converted all references to disallow_joins over to the new force_singles field.
 *        9/10/10   Do not include event and lottery times that have been drug to the tee sheet but not approved in member search
 *        8/10/10   Changes to support passing encrypted tee time info to _slot page
 *       11/20/09   Black Diamond Ranch - they changed their minds after members complained.
 *       11/11/09   Black Diamond Ranch - disable this feature - they don't want members to see pre-booked times.
 *       11/11/09   Limit the search to member times - do not include guests (so members cannot search for guest times).
 *       10/29/09   Add checks and processing for activities.
 *        9/03/09   Do not show a tee time that was pre-booked while part of a lottery - hide from members (case 1703).
 *       10/03/08   Check for replacement text for the word "Lottery" when email is for a lottery request.
 *        9/02/08   Javascript compatability updates
 *        9/25/06   Enhancements for TLT version - Add SystemLingo support
 *        9/25/06   commented out noMem method - unused
 *        3/30/06   Forest Highlands - allow this feature even though club does not wish to display member names.
 *        2/28/06   Merion - do not show member and guest names whenever there is a guest in the tee time.
 *        1/30/06   Do not allow this feature if club does not wish to display member names.
 *        1/24/05   Ver 5 - change club2 to club5.
 *       10/06/04   Ver 5 - add sub-menu support.  Add doGet processing for call from menu.
 *        1/13/04   JAG Modifications to match new color scheme.
 *        7/18/03   Enhancements for Version 3 of the software.
 *                  Add lottery processing.
 *        9/18/02   Enhancements for Version 2 of the software.
 *
 *
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
import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.client.SystemLingo;
import com.foretees.common.getActivity;
import com.foretees.common.Utilities;
import com.foretees.common.nameLists;
import com.foretees.common.ProcessConstants;
import com.foretees.common.htmlTags;

import org.apache.commons.lang.*;

import com.google.gson.*; // for json
import com.foretees.common.Connect;

public class Member_searchmem extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
 
 //  Holidays
 private static long Hdate1 = ProcessConstants.memDay;     // Memorial Day
 private static long Hdate2 = ProcessConstants.july4;      // 4th of July - Monday
 private static long Hdate2b = ProcessConstants.july4b;    // 4th of July - other
 private static long Hdate3 = ProcessConstants.laborDay;   // Labor Day
 private static long Hdate7 = ProcessConstants.tgDay;   // Thanksgiving

 //
 //****************************************************
 // Process the call from Member_maintop (menu call)
 //****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   //Statement stmt = null;
   //ResultSet rs = null;

   HttpSession session = SystemUtils.verifyMem(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   Connection con = Connect.getCon(req);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><BR>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact your club manager.");
      out.println("<BR><BR>");
      out.println("<a href=\"Member_announce\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   String club = (String)session.getAttribute("club");              // get club name
   String caller = (String)session.getAttribute("caller");        
   String user = (String)session.getAttribute("user");        
   String fullname = (String)session.getAttribute("name");          // member's full name   
   
   boolean rwd = Utilities.getRequestBoolean(req, ProcessConstants.RQA_RWD, false);
   
   htmlTags tags = new htmlTags(rwd);
   
   //
   // See what activity mode we are in
   //
   int sess_activity_id = 0;

   try { sess_activity_id = (Integer)session.getAttribute("activity_id"); }
   catch (Exception ignore) { }

   String clubName = Utilities.getClubName(con, true);        // get the full name of this club
      
   int hideNames = 0;


   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(sess_activity_id, con);

   try {

      //
      // Get the Multiple Course Option, guest types, days in advance and time for advance from the club db
      //
      getClub.getParms(con, parm, sess_activity_id);        // get the club parms

      hideNames = parm.hiden;

   }
   catch (Exception exc) {
   }
   
   boolean IS_TLT = Utilities.getSessionInteger(req, "tlt", 0) == 1;
   
   
   //
   //  Output the member Name Select Page
   //
   
   //
   //  Build the top of the page
   //
   Common_skin.outputHeader(club, sess_activity_id, "Member Search", true, out, req);
   Common_skin.outputBody(club, sess_activity_id, out, req);
   Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
   Common_skin.outputBanner(club, sess_activity_id, clubName, (String) session.getAttribute("zipcode"), out, req);  
   Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
   Common_skin.outputPageStart(club, sess_activity_id, out, req);
   Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Search", req);
   Common_skin.outputLogo(club, sess_activity_id, out, req);

   String searchType = sess_activity_id == 0?(IS_TLT?"Notifications":"Tee Times"):"Reservations";

   if(!rwd){
    out.println("<div class=\"preContentFix\"></div>"); // clear the float
   }


   if (hideNames == 0 || club.equals( "foresthighlands" )) {     // if its ok to allow members to search for others

       if(rwd){
           // Responsive search
           // Instructions for this page
           out.print("<div class=\"main_instructions pageHelp\"  data-fthelptitle=\"Instructions\">");
           out.print("<h2 class=\"altTitle\">Instructions:</h2>");
           out.print("<p>To locate the current "+searchType.toLowerCase()+" for an individual or group, select a member using the tool below.</p>");
           out.print("</div>"); // End instructions 

           out.print("<div class=\"memberReservationSearch\"></div>");
           Common_skin.outputPageEnd(club, sess_activity_id, out, req);    // finish the page      

       } else {
           // Desktop search
           // Instructions for this page
           out.println("<div class=\"main_instructions\">");          

           if (sess_activity_id == 0) {    // if this user is under Golf

              out.println("<p><strong>Instructions</strong>: To locate the current tee times for an individual or group, enter the name, ");
              out.println("or any portion of the name, as it may exist on the tee sheets.");

           } else {      // user under an activity other than golf

              out.println("<p><strong>Instructions</strong>: To locate the current reservations for an individual or group, enter the name, ");
              out.println("or any portion of the name, as it may exist in the reservations.");
           }

           out.println(" This will search for all names that contain the value you enter.</p></div>");

           out.println("<br><br><form action=\"Member_searchmem\" id=\"member_search_form\" name=\"member_search_form\" method=\"post\">");
           out.println("<input type=\"hidden\" name=\"source\" value=\"main\">");

           out.println("<div class=\"res_left\">");
                out.println("<div class=\"sub_main\">");
                out.println("<strong>Please Select a Member</strong>  &nbsp; &nbsp; Note: Click on Names &nbsp;&gt;&gt;");

                out.println("<br><br><a href=\"javascript:void(0);\" class=\"tip_text\" onclick=\"erasename()\">Erase</a>&nbsp;&nbsp;");          // added 8/25/11

                out.println(" <input type=\"text\" name=\"search_name\" class=\"res_name\" value=\"\" " + "></td>"); // onfocus=\"this.blur()\"
                out.println(" <input type=\"hidden\" name=\"search_data\" value=\"\">");
                out.println("</div>"); // end sub_main

                // display the go back and submit buttons
                out.println("<br><div style=\"text-align:center;\">");
                out.println("<br><br><input id=\"back\" name=\"back\" type=\"submit\" value=\"Search\" />");
                out.println("<br><br><br><input id=\"back\" name=\"back\" type=\"button\" value=\"Home\" onclick=\"window.location.href='Member_announce'\" />");
                out.println("</div>");

           out.println("</div>"); // end res_left

           //
           // output partner list
           //
           out.println("<div class=\"res_mid\" id=\"name_select\">");

                out.println(" <div class=\"name_list\" id=\"name_list_content\">");
                    nameLists.displayPartnerList(user, sess_activity_id, 0, con, out);       
                out.println("</div>"); // end name_list_content div

           out.println("</div>"); // end res_mid

           //
           // output letter box list
           //
           out.println("<div class=\"res_right\">");
           nameLists.getTable(out, user);
           out.println("</div>"); // end res_right div

           out.println("</form>");
           out.println("<div class=\"clearFix\"></div>"); // clear the float
           out.println("</div>"); // end reservations
           out.println("</center></div></div>");

           Common_skin.outputPageEnd(club, sess_activity_id, out, req);    // finish the page       

           out.println("<script type=\"text/javascript\">");
           out.println("function goBack() {");
           out.println(" window.history.back(-1);");
           out.println("}");

           out.println("function erasename(elem) {");
           out.println(" eval(\"document.member_search_form.search_name.value = '';\")");
           out.println("}");

           out.println("function goSearch() {");
           out.println(" var f = document.member_search_form;");
           out.println(" if(f.search_name.value='') {");
           out.println("  alert('Please select a name first.');");
           out.println(" } else {");
           out.println("  f.submit();");
           out.println(" }");
           out.println("}");

           out.println("function doSelf(fullname,person_id) {");
           out.println(" var f = document.member_search_form;");
           out.println(" f.search_name_self.value = fullname;");
           out.println(" f.search_data.value = person_id;");
           out.println(" f.submit();");
           out.println("}");

           out.println("function move_name(pName) {");
           out.println(" var f = document.member_search_form;");
           out.println(" var names = new Array();");
           out.println(" array = pName.split(':'); ");
           out.println(" var name = array[0];");
           out.println(" var data = array[1];"); // person_id - if absent it's 'undefined' so test and set to zero too
           out.println(" if (isNaN(data)) data = 0;");
           out.println(" f.search_name.value = name;");
           out.println(" f.search_data.value = data;");
           out.println("}");

           out.println("function subletter(x) {");
           out.println(" $(\"#name_list_content\").load(\"data_loader?name_list&letter=\" + x);");
           out.println("}");

           //out.println("$(document).ready(function() {");
           //out.println(" $.loading({onAjax:true, text: 'Loading Names...', effect: 'ellipsis'});");
           //out.println("});");
           out.println("</script>");
       }

   } else {

      //
      //  Club does not want member names displayed.  Therefore we cannot allow members to search
      //  for other members' tee times.
      //
      out.println("<div class=\"main_instructions\">");          
      out.println("<p>Sorry, but your club does not support this feature.</p><br></div>");
      if(!rwd){
            out.println("<br><div style=\"text-align:center;\">");
            out.println("<input id=\"back\" name=\"back\" type=\"button\" value=\"Home\" onclick=\"window.location.href='Member_announce'\" />");
            out.println("<input id=\"commit_sub\" name=\"commit_sub\" type=\"submit\" value=\"Search\" />");
            out.println("</div>");
           out.println("</div>"); // end reservations
           out.println("</center></div></div>");
      }

       Common_skin.outputPageEnd(club, sess_activity_id, out, req);    // finish the page                  
   }
  
   out.close();
          
 }


 //
 //****************************************************
 // Process the form request from member_searchmain page
 //****************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   PreparedStatement pstmt = null;
   Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;
   ResultSet rs3 = null;

   HttpSession session = SystemUtils.verifyMem(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   Connection con = Connect.getCon(req);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><BR>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact your club manager.");
      out.println("<BR><BR>");
      out.println("<a href=\"Member_announce\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   String club = (String)session.getAttribute("club");              // get club name
   String user = (String)session.getAttribute("user");
   String caller = (String)session.getAttribute("caller");
   String fullname = (String)session.getAttribute("name");          // member's full name    
   
   boolean IS_TLT = Utilities.getSessionInteger(req, "tlt", 0) == 1;

   String clubName = Utilities.getClubName(con, true);        // get the full name of this club
      
   //
   // See what activity mode we are in
   //
   int sess_activity_id = 0;

   try { sess_activity_id = (Integer)session.getAttribute("activity_id"); }
   catch (Exception ignore) { }

   
   String omit = "";
   String time_zone = "";
   String ampm = "";
   String day = "";
   String lname = "";
   String submit = "";
   String sfb = "";
   String course = "";
   String player1 = "";
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
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
   String lotteryText = "";
   String lotteryName = "";
   String orig_by = "";
   String event = "";
   String player_color = "white";

   String rest = "";

   long date = 0;
   long lottid = 0;

   int mm  = 0;
   int dd = 0;
   int yy = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int count = 0;
   int fb = 0;
   int multi = 0;
   int lottery = 0;
   int fives = 0;
   int fivesomes = 0;
   int slots = 0;
   int lstate = 0;
   int groups = 0;
   int playerCount = 0;
   int maxPlayers = 0;
   int make_private = 0;
   int event_type = 0;
   int custom_int = 0;

   boolean available = false;
   boolean rwd = Utilities.getRequestBoolean(req, ProcessConstants.RQA_RWD, false);
   
   htmlTags tags = new htmlTags(rwd);
   
   Gson gson_obj = new Gson();
   
   String searchType = sess_activity_id == 0?(IS_TLT?"Notifications":"Tee Times"):"Reservations";

   //
   // Process request according to which 'submit' button was selected
   //
   //      'search' - a search request
   //      'letter' - a request to list member names (= A - Z)
   //      'name'  -  the name, or partial name, to search for
   //
   // Get the parameters entered
   //

      //
      //  Called to search for a name - get the name and search for it
      //
      String name = "";        //  name or portion of name

      name = req.getParameter("search_name");     
          
      int length = name.length();                    // get length of name requested

      //
      //  verify the required fields
      //
      if ((name.equals( omit )) || (length > 20)) { // why is this capd at 20?

          String errMsg = "<BR>Sorry, some data you entered is missing or invalid.<BR>"
                  + "<BR>You must enter the player's name, or some portion of it.<BR>"
                  + "<BR>Please try again.<BR>";

          Common_skin.outputError(club, clubName, sess_activity_id, "Invalid Entry", errMsg, "javascript:history.back(1)", out, req);
          return;
      }

      //
      // see if 5-somes are supported on any course at this club
      //
      try {

         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT fives FROM clubparm2");

         while (rs.next()) {

            fives = rs.getInt(1);

            if (fives != 0) {

               fivesomes = 1;          // set 5-somes supported for this club
            }
         }

         stmt.close();

         //
         //  get the multiple course parm for this club
         //
         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT multi, lottery, adv_zone FROM club5 WHERE clubName != ''");

         if (rs.next()) {

            multi = rs.getInt(1);
            lottery = rs.getInt(2);
            time_zone = rs.getString(3);

         }

         stmt.close();


         //
         //   Add a % to the name provided so search will match anything close
         //
         StringBuffer buf = new StringBuffer("%");
         buf.append( name );
         buf.append("%");
         String sname = buf.toString();
         PreparedStatement pstmt1 = null;
         
         //
         //   build the HTML page for the display
         //
          Common_skin.outputHeader(club, sess_activity_id, "Member Search", true, out, req);
          Common_skin.outputBody(club, sess_activity_id, out, req);
          Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
          Common_skin.outputBanner(club, sess_activity_id, clubName, (String) session.getAttribute("zipcode"), out, req);
          Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
          Common_skin.outputPageStart(club, sess_activity_id, out, req);
          Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Search", req);
          Common_skin.outputLogo(club, sess_activity_id, out, req);

          if (!rwd) {
              out.println("<div class=\"preContentFix\"></div>"); // clear the float
          }

         if(!rwd){
             out.println("<table border=\"0\" align=\"center\">");
             out.println("<tr><td align=\"center\">");
         }
         //
         //  Process according to the activity
         //
         if (sess_activity_id == 0 && IS_TLT) {    // is Notification
             
         } else if (sess_activity_id == 0) {    // if Golf  
         
            //
            // use the name to search table
            //
            pstmt1 = con.prepareStatement (
               "SELECT * " +
               "FROM teecurr2 " +
               "WHERE ((player1 LIKE ? AND username1 != '') OR (player2 LIKE ? AND username2 != '') OR " +
               "(player3 LIKE ? AND username3 != '') OR (player4 LIKE ? AND username4 != '') OR (player5 LIKE ? AND username5 != '')) " +
               "ORDER BY date, time");

            pstmt1.clearParameters();        // clear the parms
            pstmt1.setString(1, sname);
            pstmt1.setString(2, sname);
            pstmt1.setString(3, sname);
            pstmt1.setString(4, sname);
            pstmt1.setString(5, sname);

            rs = pstmt1.executeQuery();      // execute the prepared stmt

            out.println("<H2>Search Results for " + name + "</H2>");
            if(rwd){
                out.println("<"+tags.table+" class=\"rwdTable standard_list_table rwdCompactible\">");
                out.println("<"+tags.caption+" class=\"rwdCaption\">");
            } else {
                out.print("<br>");
                out.println("<div class=\"main_instructions\">");
            }
            // Instructions for this page
            out.println("<H2>Tee Times</H2>");          
            out.println("To join an open tee time simply click on the button containing the time.");          
            out.println("<br><br>F/B Legend:&nbsp;&nbsp;&nbsp;&nbsp;F = Front 9, &nbsp;&nbsp;B = Back 9, &nbsp;&nbsp;O = Other");
            if(rwd){
                out.println("</"+tags.caption+">");
            } else {
                out.print("</div>");
                out.println("<table class=\"standard_list_table rwdCompactible\">");
            }

            out.println("  <"+tags.thead+" class=\"rwdThead\"><"+tags.tr+" class=\"rwdTr\">");
            out.println("  <"+tags.th+" class=\"rwdTh\">Date</"+tags.th+">");
            out.println("  <"+tags.th+" class=\"rwdTh\">Time</"+tags.th+">");
            if (multi != 0) out.println("  <"+tags.th+" class=\"rwdTh\">Course</"+tags.th+">");
            out.println("  <"+tags.th+" class=\"rwdTh\">F/B</"+tags.th+">");
            if(rwd){
                out.println("  <"+tags.th+" class=\"rwdTh\">Players</"+tags.th+">");
            } else {
                out.println("  <th>Player 1</th>");
                out.println("  <th>Player 2</th>");
                out.println("  <th>Player 3</th>");
                out.println("  <th>Player 4</th>");
                if (fivesomes != 0) out.println("  <th>Player 5</th>");
            }
            out.println(" </"+tags.tr+"></"+tags.thead+">");
            out.println(" <"+tags.tbody+" class=\"rwdTbody\">");
                
            //
            //  Get each record and display it
            //
            count = 0;             // number of records found

            while (rs.next()) {

               date = rs.getLong("date");
               mm = rs.getInt("mm");
               dd = rs.getInt("dd");
               yy = rs.getInt("yy");
               day = rs.getString("day");
               hr = rs.getInt("hr");
               min = rs.getInt("min");
               time = rs.getInt("time");
               player1 = rs.getString("player1");
               player2 = rs.getString("player2");
               player3 = rs.getString("player3");
               player4 = rs.getString("player4");
               user1 = rs.getString("username1");
               user2 = rs.getString("username2");
               user3 = rs.getString("username3");
               user4 = rs.getString("username4");
               fb = rs.getInt("fb");
               player5 = rs.getString("player5");
               user5 = rs.getString("username5");
               lotteryName = rs.getString("lottery");
               course = rs.getString("courseName");
               rest = rs.getString("rest5");
               userg1 = rs.getString("userg1");
               userg2 = rs.getString("userg2");
               userg3 = rs.getString("userg3");
               userg4 = rs.getString("userg4");
               userg5 = rs.getString("userg5");
               orig_by = rs.getString("orig_by");
               make_private = rs.getInt("make_private");
               event = rs.getString("event");
               event_type = rs.getInt("event_type");
               custom_int = rs.getInt("custom_int");
               
               // If this time is during a shotgun event, look up the actual start time of the event, and display that instead.
               if (!event.equals("") && event_type == 1) {
               
                   try {
                       
                       pstmt = con.prepareStatement("SELECT act_hr, act_min FROM events2b WHERE activity_id = 0 AND name = ?");
                       pstmt.clearParameters();
                       pstmt.setString(1, event);
                       
                       rs3 = pstmt.executeQuery();
                       
                       if (rs3.next()) {
                           hr = rs3.getInt("act_hr");
                           min = rs3.getInt("act_min");
                       }
                       
                   } catch (Exception exc) {
                       Utilities.logError("Member_searchmem.doPost - " + club + " - Error looking up shotgun event details - ERR: " + exc.toString());
                   } finally {
                       
                       try { rs3.close(); }
                       catch (Exception ignore) {}
                       
                       try { pstmt.close(); }
                       catch (Exception ignore) {}
                   }                   
               }

               boolean skip = false;

               if (club.equals( "merion" )) {

                  //
                  //  Merion - do not show tee time whenever there is a guest in the tee time (and its not this member's time)
                  //
                  if (!userg1.equals( "" ) || !userg2.equals( "" ) || !userg3.equals( "" ) || !userg4.equals( "" ) || !userg5.equals( "" )) {

                     skip = true;        // skip this guest time

                     if (user.equalsIgnoreCase(user1) || user.equalsIgnoreCase(user2) || user.equalsIgnoreCase(user3) ||
                         user.equalsIgnoreCase(user4) || user.equalsIgnoreCase(user5)) {    // if user is in this group

                        skip = false;     // do not skip when member is included
                     }
                  }
               } 

               //
               //  Check for lottery time
               //
               if (!lotteryName.equals("")) {

                  //
                  //  Get the current state of this lottery on the day of this tee time
                  //
                  lstate = SystemUtils.getLotteryState(date, mm, dd, yy, lotteryName, course, con);

                  if (lstate < 5) {

                     skip = true;        // do not allow access to this tee time (pre-booked lottery time)
                  }
               }
            
               if (make_private > 0 && !user1.equalsIgnoreCase(user) && !user2.equalsIgnoreCase(user) && !user3.equalsIgnoreCase(user) 
                       && !user4.equalsIgnoreCase(user) && !user5.equalsIgnoreCase(user) && !orig_by.equalsIgnoreCase(user)) {
                   skip = true;
               }

               if (skip == false) {

                  count++;

                  //
                  //  check if 5-somes allowed on this course
                  //
                  PreparedStatement pstmt3 = con.prepareStatement (
                     "SELECT fives FROM clubparm2 WHERE courseName = ?");

                  pstmt3.clearParameters();        // clear the parms
                  pstmt3.setString(1, course);
                  rs2 = pstmt3.executeQuery();      // execute the prepared pstmt3

                  if (rs2.next()) {

                     fives = rs2.getInt(1);
                  }

                  pstmt3.close();

                  //
                  // Forest Highlands - don't allow 5-somes or show 5th player column during specified date range
                  //
//                  if (club.equals("foresthighlands")) {
//
//                      long month_day = (mm * 100) + dd;     // get adjusted date
//
//                      if (month_day > 424 && month_day < 1001) {
//                          fives = 0;
//                      }
//                  }
            
                  if (club.equals("dmgcc")) {
                      
                      long month_day = (mm * 100) + dd;     // get adjusted date
                      
                      if (month_day >= 401 && date <= Hdate3) {
                          fives = 0;
                      }
                  }
                  
                  ampm = " AM";
                  if (hr == 12) {
                     ampm = " PM";
                  }
                  if (hr > 12) {
                     ampm = " PM";
                     hr = hr - 12;    // convert to conventional time
                  }

                  String[] players = {player1,player2,player3,player4,player5};
                  available = false;

                  if (player1.equals( "" )) {

                     player1 = "&nbsp;";       // make it a space for table display
                     available = true;         // tee slot is available for others
                  }

                  if (player2.equals( "" )) {

                     player2 = "&nbsp;";       // make it a space for table display
                     available = true;         // tee slot is available for others
                  }

                  if (player3.equals( "" )) {

                     player3 = "&nbsp;";       // make it a space for table display
                     available = true;         // tee slot is available for others
                  }

                  if (player4.equals( "" )) {

                     player4 = "&nbsp;";       // make it a space for table display
                     available = true;         // tee slot is available for others
                  }

                  if (fives != 0) {            // 5-somes supported on this course

                     if (player5.equals( "" )) {

                        if (rest.equals( "" )) {          // if 5-somes are not restricted

                           available = true;         // tee slot is available for others
                           player5 = "&nbsp;";       // make it a space for table display
                        } else {
                           player5 = "N/A";          // player 5 N/A
                        }
                     }
                  }
                 

                  //
                  //  Process the F/B parm    0 = Front 9, 1 = Back 9, 9 = none (open for cross-over)
                  //
                  sfb = "O";       // default Other

                  if (fb == 1) {

                     sfb = "B";
                  }

                  if (fb == 0) {

                     sfb = "F";
                  }

                  submit = "time:" + fb;       // create a name for the submit button


                  //
                  //  Build the HTML for each record found
                  //
                  if(rwd){
                        //slotMap.put("time:0", (hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm));
                        //String[] pcw = {p1cw,p2cw,p3cw,p4cw,p5cw};
                      
                        if (club.equals("kiawahislandclub") && custom_int > 0) {
                            out.print("<"+tags.tr+" class=\"rwdTr\" style=\"background:orange;\">");
                        } else {
                            out.print("<"+tags.tr+" class=\"rwdTr\">");
                        }
                        out.print("<"+tags.td+" class=\"rwdTd sDd\">"+day + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy +"</"+tags.td+">");
                        if (available) {
                            
                            Map<String, Object> slotMap = new LinkedHashMap<String, Object>(); // Create hashmap response for later use

                            slotMap.put("ttdata", Utilities.encryptTTdata(hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "|" + fb + "|" + user));
                            slotMap.put("date", date);
                            slotMap.put("day", day);
                            slotMap.put("course", course);
                            slotMap.put("index", 888);
                            slotMap.put("p5", (fives != 0 && rest.equals( "" )?"Yes":"No"));
                            out.print("<"+tags.td+" class=\"rwdTd sT\"><a href=\"#\" data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(slotMap)) + "\" class=\"standard_button teetime_button\">"+hr + (min<10?":0":":") + min + ampm+"</a></"+tags.td+">");
                        } else {
                            out.print("<"+tags.td+" class=\"rwdTd sT\"><div class=\"time_slot\">"+hr + (min<10?":0":":") + min + ampm+"</div></"+tags.td+">");
                        }
                        if (multi != 0) {
                            out.print("<"+tags.td+" class=\"rwdTd sN\">"+course+"</"+tags.td+">");
                        }
                        out.print("<"+tags.td+" class=\"rwdTd sF\">"+sfb+"</"+tags.td+">");
                        out.print("<"+tags.td+" class=\"rwdTd sP\">");
                        for(int i = 0; i < players.length; i++){
                            if(!players[i].equals("")){
                                out.print("<div");
                                if(players[i].equals( name )){
                                    out.print(" class=\"me\"");
                                }
                                out.print(">");
                                out.print("<span>"+players[i]+"</span>");
                                //out.print("<span>"+pcw[i]+"</span>");
                                out.print("</div>");
                            }
                        }
                        out.print("</"+tags.td+">");
                        out.println("</"+tags.tr+">");
                  } else {
                      
                      if (club.equals("kiawahislandclub") && custom_int > 0) {
                          out.println("<tr style=\"background:orange;\">");
                          player_color = "orange";
                          available = false;
                      } else {
                          out.println("<tr>");
                          player_color = "white";
                      }
                      out.println("<form action=\"Member_slot\" method=\"post\" target=\"_top\">");

                      out.println("<input type=\"hidden\" name=\"ttdata\" value=\"" + Utilities.encryptTTdata(hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "|" + fb + "|" + user) + "\">");

                      out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
                      out.println("<input type=\"hidden\" name=\"day\" value=" + day + ">");
                      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                      out.println("<input type=\"hidden\" name=\"index\" value=\"888\">");  // from here
                         if (fives != 0 && rest.equals( "" )) {
                            out.println("<input type=\"hidden\" name=\"p5\" value=\"Yes\">");
                         } else {
                            out.println("<input type=\"hidden\" name=\"p5\" value=\"No\">");
                         }

                      out.println("<td align=\"center\">");
                         out.println("<font size=\"2\">");
                         out.println( day + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy );
                         out.println("</font></td>");

                      out.println("<td align=\"center\">");
                         out.println("<font size=\"2\">");

                      if (available) {                      // if there is an empty slot available

                         out.println("<input type=\"submit\" value=\"" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "\">"); // name=\"" + submit + "\"
    /*
                         out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "\">");
    */
                      } else {

                         out.println(hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm);

                      }
                         out.println("</font></td>");

                      if (multi != 0) {

                         out.println("<td align=\"center\">");
                         out.println("<font size=\"2\">");
                         out.println(course);
                         out.println("</font></td>");
                      }

                         out.println("<td align=\"center\">");
                         out.println("<font size=\"2\">");
                         out.println(sfb);
                         out.println("</font></td>");

                         out.println("<td align=\"center\" style=\"background:" + player_color + ";\">");
                         out.println("<font size=\"2\">");
                         out.println( player1 );
                         out.println("</font></td>");

                         out.println("<td align=\"center\" style=\"background:" + player_color + ";\">");
                         out.println("<font size=\"2\">");
                         out.println( player2 );
                         out.println("</font></td>");

                         out.println("<td align=\"center\" style=\"background:" + player_color + ";\">");
                         out.println("<font size=\"2\">");
                         out.println( player3 );
                         out.println("</font></td>");

                         out.println("<td align=\"center\" style=\"background:" + player_color + ";\">");
                         out.println("<font size=\"2\">");
                         out.println( player4 );
                         out.println("</font></td>");

                      if (fivesomes != 0) {

                         out.println("<td align=\"center\" style=\"background:" + player_color + ";\">");
                         out.println("<font size=\"2\">");
                         out.println( player5 );
                         out.println("</font></td>");
                      }
                         out.println("</form></tr>");
                  }

               }   // end of IF skip

            }    // end of while

            pstmt1.close();

            out.println(" </"+tags.tbody+"></"+tags.table+">");

            if (count == 0) {

               out.println("<p align=\"center\">No records found for " + name + ".</p>");

            }

            //
            // ****************************************************************
            // use the name to search for lottery requests (if supported)
            // ****************************************************************
            //
            if (lottery > 0) {

               //
               //  Get the replacement text for the word "Lottery" if club requested one - added to club5 by Support Club Options
               //
               lotteryText = getClub.getLotteryText(con);         // get replacement text for "Lottery" if provided  

               if (club.equals( "oldoaks" )) {
                    lotteryText = "Tee Time Request";
               } else if (lotteryText.equals("")){
                    lotteryText = "Lottery Request";
               }
               if(!rwd){
                    out.println("</td></tr>");       // terminate previous col/row
               }
               pstmt1 = con.prepareStatement (
                  "SELECT name, date, mm, dd, yy, day, hr, min, time, player1, player2, player3, player4, " +
                  "player5, player6, player7, player8, player9, player10, player11, player12, player13, player14, " +
                  "player15, player16, player17, player18, player19, player20, player21, player22, player23, " +
                  "player24, player25, fb, courseName, id, groups " +
                  "FROM lreqs3 " +
                  "WHERE player1 LIKE ? OR player2 LIKE ? OR player3 LIKE ? OR player4 LIKE ? OR player5 LIKE ? OR " +
                  "player6 LIKE ? OR player7 LIKE ? OR player8 LIKE ? OR player9 LIKE ? OR player10 LIKE ? OR " +
                  "player11 LIKE ? OR player12 LIKE ? OR player13 LIKE ? OR player14 LIKE ? OR player15 LIKE ? OR " +
                  "player16 LIKE ? OR player17 LIKE ? OR player18 LIKE ? OR player19 LIKE ? OR player20 LIKE ? OR " +
                  "player21 LIKE ? OR player22 LIKE ? OR player23 LIKE ? OR player24 LIKE ? OR player25 LIKE ? " +
                  "ORDER BY date, time");

               pstmt1.clearParameters();        // clear the parms
               pstmt1.setString(1, sname);
               pstmt1.setString(2, sname);
               pstmt1.setString(3, sname);
               pstmt1.setString(4, sname);
               pstmt1.setString(5, sname);
               pstmt1.setString(6, sname);
               pstmt1.setString(7, sname);
               pstmt1.setString(8, sname);
               pstmt1.setString(9, sname);
               pstmt1.setString(10, sname);
               pstmt1.setString(11, sname);
               pstmt1.setString(12, sname);
               pstmt1.setString(13, sname);
               pstmt1.setString(14, sname);
               pstmt1.setString(15, sname);
               pstmt1.setString(16, sname);
               pstmt1.setString(17, sname);
               pstmt1.setString(18, sname);
               pstmt1.setString(19, sname);
               pstmt1.setString(20, sname);
               pstmt1.setString(21, sname);
               pstmt1.setString(22, sname);
               pstmt1.setString(23, sname);
               pstmt1.setString(24, sname);
               pstmt1.setString(25, sname);
               rs = pstmt1.executeQuery();      // execute the prepared stmt

               //
               //   build the table for the display
               //
               if(!rwd){
                    out.println("<tr><td align=\"center\" valign=\"top\">");
               }

               if(rwd){
                    out.println("<"+tags.table+" class=\"rwdTable standard_list_table rwdCompactible\">");
                    out.println("<"+tags.caption+" class=\"rwdCaption\">");
                } else {
                    out.print("<br><br>");
                    out.println("<div class=\"main_instructions\">");
                }
                // Instructions for this page
                out.println("<H2>Current " +lotteryText+ "s</H2>");
                out.println("If the " +lotteryText+ " is eligible for change, you will be able to click on the button containing the time.");          
                out.println("<br><br>F/B Legend:&nbsp;&nbsp;&nbsp;&nbsp;F = Front 9, &nbsp;&nbsp;B = Back 9, &nbsp;&nbsp;O = Other");
                if(rwd){
                    out.println("</"+tags.caption+">");
                } else {
                    out.println("</div><"+tags.table+" class=\"rwdTable standard_list_table rwdCompactible\">");
                }

                out.println("  <"+tags.thead+" class=\"rwdThead\"><"+tags.tr+" class=\"rwdTr\">");
                out.println("  <"+tags.th+" class=\"rwdTh\">Date</"+tags.th+">");
                out.println("  <"+tags.th+" class=\"rwdTh\">Time</"+tags.th+">");
                if (multi != 0) out.println("  <"+tags.th+" class=\"rwdTh\">Course</"+tags.th+">");
                out.println("  <"+tags.th+" class=\"rwdTh\">F/B</"+tags.th+">");
                if(rwd){
                    out.println("  <"+tags.th+" class=\"rwdTh\">Players</"+tags.th+">");
                }else{
                    out.println("  <th>Player 1</th>");
                    out.println("  <th>Player 2</th>");
                    out.println("  <th>Player 3</th>");
                    out.println("  <th>Player 4</th>");
                    if (fivesomes != 0) out.println("  <th>Player 5</th>");
                }
                out.println(" </"+tags.tr+"></"+tags.thead+">");
                out.println(" <"+tags.tbody+" class=\"rwdTbody\">");
                   
               //
               //  Get each record and display it
               //
               count = 0;             // number of records found

               while (rs.next()) {

                  count++;

                  lname = rs.getString(1);
                  date = rs.getLong(2);
                  mm = rs.getInt(3);
                  dd = rs.getInt(4);
                  yy = rs.getInt(5);
                  day = rs.getString(6);
                  hr = rs.getInt(7);
                  min = rs.getInt(8);
                  time = rs.getInt(9);
                  player1 = rs.getString(10);
                  player2 = rs.getString(11);
                  player3 = rs.getString(12);
                  player4 = rs.getString(13);
                  player5 = rs.getString(14);
                  player6 = rs.getString(15);
                  player7 = rs.getString(16);
                  player8 = rs.getString(17);
                  player9 = rs.getString(18);
                  player10 = rs.getString(19);
                  player11 = rs.getString(20);
                  player12 = rs.getString(21);
                  player13 = rs.getString(22);
                  player14 = rs.getString(23);
                  player15 = rs.getString(24);
                  player16 = rs.getString(25);
                  player17 = rs.getString(26);
                  player18 = rs.getString(27);
                  player19 = rs.getString(28);
                  player20 = rs.getString(29);
                  player21 = rs.getString(30);
                  player22 = rs.getString(31);
                  player23 = rs.getString(32);
                  player24 = rs.getString(33);
                  player25 = rs.getString(34);
                  fb = rs.getInt(35);
                  course = rs.getString(36);
                  lottid = rs.getLong(37);
                  groups = rs.getInt(38);

                  ampm = " AM";
                  if (hr == 12) {
                     ampm = " PM";
                  }
                  if (hr > 12) {
                     ampm = " PM";
                     hr = hr - 12;    // convert to conventional time
                  }

                  playerCount = 0;         // init # of players in request
                  
                  String[] players = {
                      player1,player2,player3,player4,player5,
                      player6,player7,player8,player9,player10,
                      player11,player12,player13,player14,player15,
                      player16,player17,player18,player19,player20,
                      player21,player22,player23,player24,player25
                  };

                  if (player1.equals( "" )) {

                     player1 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player2.equals( "" )) {

                     player2 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player3.equals( "" )) {

                     player3 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player4.equals( "" )) {

                     player4 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player5.equals( "" )) {

                     player5 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player6.equals( "" )) {

                     player6 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player7.equals( "" )) {

                     player7 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player8.equals( "" )) {

                     player8 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player9.equals( "" )) {

                     player9 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player10.equals( "" )) {

                     player10 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player11.equals( "" )) {

                     player11 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player12.equals( "" )) {

                     player12 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player13.equals( "" )) {

                     player13 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player14.equals( "" )) {

                     player14 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player15.equals( "" )) {

                     player15 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player16.equals( "" )) {

                     player16 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player17.equals( "" )) {

                     player17 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player18.equals( "" )) {

                     player18 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player19.equals( "" )) {

                     player19 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player20.equals( "" )) {

                     player20 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player21.equals( "" )) {

                     player21 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player22.equals( "" )) {

                     player22 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player23.equals( "" )) {

                     player23 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player24.equals( "" )) {

                     player24 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player25.equals( "" )) {

                     player25 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }

                  //
                  //  Process the F/B parm    0 = Front 9, 1 = Back 9, 9 = none (open for cross-over)
                  //
                  sfb = "O";       // default Other

                  if (fb == 1) {

                     sfb = "B";
                  }

                  if (fb == 0) {

                     sfb = "F";
                  }

                  //
                  //  Check if 5-somes supported for this course
                  //
                  fives = 0;        // init

                  PreparedStatement pstmtc = con.prepareStatement (
                     "SELECT fives " +
                     "FROM clubparm2 WHERE first_hr != 0 AND courseName = ?");

                  pstmtc.clearParameters();        // clear the parms
                  pstmtc.setString(1, course);
                  rs2 = pstmtc.executeQuery();      // execute the prepared stmt

                  if (rs2.next()) {

                     fives = rs2.getInt(1);          // 5-somes
                  }
                  pstmtc.close();

                  //
                  //  check if 5-somes restricted for this time
                  //
                  if (fives != 0) {

                     PreparedStatement pstmtr = con.prepareStatement (
                        "SELECT rest5 " +
                        "FROM teecurr2 WHERE date = ? AND time =? AND fb = ? AND courseName = ?");

                     pstmtr.clearParameters();        // clear the parms
                     pstmtr.setLong(1, date);
                     pstmtr.setInt(2, time);
                     pstmtr.setInt(3, fb);
                     pstmtr.setString(4, course);
                     rs2 = pstmtr.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        rest = rs2.getString(1);
                     }
                     pstmtr.close();
                  }

                  //
                  //  Determine the max # of players allowed in this request
                  //
                  if (fives > 0 && rest.equals( "" )) {       // if 5-somes allowed

                     maxPlayers = (groups * 5);

                  } else {

                     maxPlayers = (groups * 4);
                  }

                  //
                  //  get the slots value and determine the current state for this lottery
                  //
                  PreparedStatement pstmt7d = con.prepareStatement (
                     "SELECT slots " +
                     "FROM lottery3 WHERE name = ?");

                  pstmt7d.clearParameters();          // clear the parms
                  pstmt7d.setString(1, lname);

                  rs2 = pstmt7d.executeQuery();      // find all matching lotteries, if any

                  if (rs2.next()) {

                     slots = rs2.getInt(1);

                  }                  // end of while
                  pstmt7d.close();


                  //
                  //  Get the current state of this lottery on the day of this tee time
                  //
                  lstate = SystemUtils.getLotteryState(date, mm, dd, yy, lname, course, con);


                  submit = "time:" + fb;       // create a name for the submit button
                  
                  //
                  //  Build the HTML for each record found
                  //
                  
                  if(rwd){

                        //slotMap.put("time:0", (hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm));
                        //String[] pcw = {p1cw,p2cw,p3cw,p4cw,p5cw};
                        out.print("<"+tags.tr+" class=\"rwdTr\">");
                        out.print("<"+tags.td+" class=\"rwdTd sDd\">"+day + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy +"</"+tags.td+">");
                        if (lstate == 2 && playerCount < maxPlayers) { 
                            Map<String, Object> slotMap = new LinkedHashMap<String, Object>(); // Create hashmap response for later use

                            //slotMap.put("ttdata", Utilities.encryptTTdata(hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "|" + fb + "|" + user));
                            slotMap.put("date", date);
                            slotMap.put("day", day);
                            slotMap.put("course", course);
                            slotMap.put("lname", lname);
                            slotMap.put("lottid", lottid);
                            slotMap.put("slots", slots);
                            slotMap.put("lstate", lstate);
                            slotMap.put("index", 888);
                            slotMap.put("stime", hr + (min<10?":0":":") + min + ampm);
                            slotMap.put("p5", (fives != 0 && rest.equals( "" )?"Yes":"No"));
                            out.print("<"+tags.td+" class=\"rwdTd sT\"><a href=\"#\" data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(slotMap)) + "\" class=\"standard_button lottery_button\">"+hr + (min<10?":0":":") + min + ampm+"</a></"+tags.td+">");
                        } else {
                            out.print("<"+tags.td+" class=\"rwdTd sT\"><div class=\"time_slot\">"+hr + (min<10?":0":":") + min + ampm+"</div></"+tags.td+">");
                        }
                        if (multi != 0) {
                            out.print("<"+tags.td+" class=\"rwdTd sN\">"+course+"</"+tags.td+">");
                        }
                        out.print("<"+tags.td+" class=\"rwdTd sF\">"+sfb+"</"+tags.td+">");
                        out.print("<"+tags.td+" class=\"rwdTd sP\">");
                        for(int i = 0; i < players.length; i++){
                            if(!players[i].equals("")){
                                out.print("<div");
                                if(players[i].equals( name )){
                                    out.print(" class=\"me\"");
                                }
                                out.print(">");
                                out.print("<span>"+players[i]+"</span>");
                                //out.print("<span>"+pcw[i]+"</span>");
                                out.print("</div>");
                            }
                        }
                        out.print("</"+tags.td+">");
                        out.println("</"+tags.tr+">");
                  } else {
                  
                      out.println("<tr>");
                      out.println("<form action=\"Member_lott\" method=\"post\" target=\"_top\">");
                      out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
                      out.println("<input type=\"hidden\" name=\"day\" value=" + day + ">");
                      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                      out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lname + "\">");
                      out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + lottid + "\">");
                      out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots + "\">");
                      out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate + "\">");

                      if (fives != 0) {                     // if 5-somes on this course
                         if (!rest.equals( "" )) {          // if 5-somes are restricted
                            out.println("<input type=\"hidden\" name=\"p5\" value=\"No\">");
                         } else {
                            out.println("<input type=\"hidden\" name=\"p5\" value=\"Yes\">");
                         }
                      } else {
                         out.println("<input type=\"hidden\" name=\"p5\" value=\"No\">");
                      }

                      out.println("<input type=\"hidden\" name=\"index\" value=888>");  // indicate from searchmain.htm
                      out.println("<td align=\"center\">");
                         out.println("<font size=\"2\">");
                         out.println( day + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy );
                         out.println("</font></td>");

                      out.println("<td align=\"center\">");
                         out.println("<font size=\"2\">");
                         if (lstate == 2 && playerCount < maxPlayers) {    // if state allows change & room for players
                            if (min < 10) {
                               out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":0" + min + ampm + "\">");
                            } else {
                               out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":" + min + ampm + "\">");
                            }
                         } else {
                            if (min < 10) {
                               out.println(hr + ":0" + min + ampm);
                            } else {
                               out.println(hr + ":" + min + ampm);
                            }
                         }
                      out.println("</font></td>");

                      if (multi != 0) {
                         out.println("<td align=\"center\">");
                         out.println("<font size=\"2\">");
                         out.println(course);
                         out.println("</font></td>");
                      }

                         out.println("<td align=\"center\">");
                         out.println("<font size=\"2\">");
                         out.println(sfb);
                         out.println("</font></td>");

                         out.println("<td align=\"center\" bgcolor=\"white\">");
                         out.println("<font size=\"2\">");
                         out.println( player1 );
                         out.println("</font></td>");

                         out.println("<td align=\"center\" bgcolor=\"white\">");
                         out.println("<font size=\"2\">");
                         out.println( player2 );
                         out.println("</font></td>");

                         out.println("<td align=\"center\" bgcolor=\"white\">");
                         out.println("<font size=\"2\">");
                         out.println( player3 );
                         out.println("</font></td>");

                         out.println("<td align=\"center\" bgcolor=\"white\">");
                         out.println("<font size=\"2\">");
                         out.println( player4 );
                         out.println("</font></td>");

                      if (fives != 0) {
                         out.println("<td align=\"center\" bgcolor=\"white\">");
                         out.println("<font size=\"2\">");
                         out.println( player5 );
                         out.println("</font></td>");

                         //
                         //  check if there are more than 5 players registered
                         //
                         if (!player6.equals( " " ) || !player7.equals( " " ) || !player8.equals( " " ) || !player9.equals( " " ) || !player10.equals( " " )) {

                            out.println("</tr><tr>");
                            out.println("<td align=\"center\">");      // date col
                            out.println("&nbsp;</td>");

                            out.println("<td align=\"center\">");      // time col
                            out.println("&nbsp;</td>");

                            if (multi != 0) {
                               out.println("<td align=\"center\">");   // course
                               out.println("&nbsp;</td>");
                            }

                            out.println("<td align=\"center\">");       // f/b
                            out.println("&nbsp;</td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player6 );
                            out.println("</font></td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player7 );
                            out.println("</font></td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player8 );
                            out.println("</font></td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player9 );
                            out.println("</font></td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player10 );
                            out.println("</font></td>");
                         }

                         if (!player11.equals( " " ) || !player12.equals( " " ) || !player13.equals( " " ) || !player14.equals( " " ) || !player15.equals( " " )) {

                            out.println("</tr><tr>");
                            out.println("<td align=\"center\">");      // date col
                            out.println("&nbsp;</td>");

                            out.println("<td align=\"center\">");      // time col
                            out.println("&nbsp;</td>");

                            if (multi != 0) {
                               out.println("<td align=\"center\">");   // course
                               out.println("&nbsp;</td>");
                            }

                            out.println("<td align=\"center\">");       // f/b
                            out.println("&nbsp;</td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player11 );
                            out.println("</font></td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player12 );
                            out.println("</font></td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player13 );
                            out.println("</font></td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player14 );
                            out.println("</font></td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player15 );
                            out.println("</font></td>");
                         }

                         if (!player16.equals( " " ) || !player17.equals( " " ) || !player18.equals( " " ) || !player19.equals( " " ) || !player20.equals( " " )) {

                            out.println("</tr><tr>");
                            out.println("<td align=\"center\">");      // date col
                            out.println("&nbsp;</td>");

                            out.println("<td align=\"center\">");      // time col
                            out.println("&nbsp;</td>");

                            if (multi != 0) {
                               out.println("<td align=\"center\">");   // course
                               out.println("&nbsp;</td>");
                            }

                            out.println("<td align=\"center\">");       // f/b
                            out.println("&nbsp;</td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player16 );
                            out.println("</font></td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player17 );
                            out.println("</font></td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player18 );
                            out.println("</font></td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player19 );
                            out.println("</font></td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player20 );
                            out.println("</font></td>");
                         }

                         if (!player21.equals( " " ) || !player22.equals( " " ) || !player23.equals( " " ) || !player24.equals( " " ) || !player25.equals( " " )) {

                            out.println("</tr><tr>");
                            out.println("<td align=\"center\">");      // date col
                            out.println("&nbsp;</td>");

                            out.println("<td align=\"center\">");      // time col
                            out.println("&nbsp;</td>");

                            if (multi != 0) {
                               out.println("<td align=\"center\">");   // course
                               out.println("&nbsp;</td>");
                            }

                            out.println("<td align=\"center\">");       // f/b
                            out.println("&nbsp;</td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player21 );
                            out.println("</font></td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player22 );
                            out.println("</font></td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player23 );
                            out.println("</font></td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player24 );
                            out.println("</font></td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player25 );
                            out.println("</font></td>");
                         }

                      } else {   // no 5-somes on this course

                         if (fivesomes != 0) {        // if 5-somes listed - empty slot

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println("nbsp;");
                            out.println("</font></td>");
                         }
                         //
                         //  check if there are more than 4 players registered
                         //
                         if (!player5.equals( " " ) || !player6.equals( " " ) || !player7.equals( " " ) || !player8.equals( " " )) {

                            out.println("</tr><tr>");
                            out.println("<td align=\"center\">");      // date col
                            out.println("&nbsp;</td>");

                            out.println("<td align=\"center\">");      // time col
                            out.println("&nbsp;</td>");

                            if (multi != 0) {
                               out.println("<td align=\"center\">");   // course
                               out.println("&nbsp;</td>");
                            }

                            out.println("<td align=\"center\">");       // f/b
                            out.println("&nbsp;</td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player5 );
                            out.println("</font></td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player6 );
                            out.println("</font></td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player7 );
                            out.println("</font></td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player8 );
                            out.println("</font></td>");

                            if (fivesomes != 0) {        // if 5-somes listed - empty slot

                               out.println("<td align=\"center\" bgcolor=\"white\">");
                               out.println("<font size=\"2\">");
                               out.println("nbsp;");
                               out.println("</font></td>");
                            }
                         }

                         if (!player9.equals( " " ) || !player10.equals( " " ) || !player11.equals( " " ) || !player12.equals( " " )) {

                            out.println("</tr><tr>");
                            out.println("<td align=\"center\">");      // date col
                            out.println("&nbsp;</td>");

                            out.println("<td align=\"center\">");      // time col
                            out.println("&nbsp;</td>");

                            if (multi != 0) {
                               out.println("<td align=\"center\">");   // course
                               out.println("&nbsp;</td>");
                            }

                            out.println("<td align=\"center\">");       // f/b
                            out.println("&nbsp;</td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player9 );
                            out.println("</font></td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player10 );
                            out.println("</font></td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player11 );
                            out.println("</font></td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player12 );
                            out.println("</font></td>");

                            if (fivesomes != 0) {        // if 5-somes listed - empty slot

                               out.println("<td align=\"center\" bgcolor=\"white\">");
                               out.println("<font size=\"2\">");
                               out.println("nbsp;");
                               out.println("</font></td>");
                            }
                         }

                         if (!player13.equals( " " ) || !player14.equals( " " ) || !player15.equals( " " ) || !player16.equals( " " )) {

                            out.println("</tr><tr>");
                            out.println("<td align=\"center\">");      // date col
                            out.println("&nbsp;</td>");

                            out.println("<td align=\"center\">");      // time col
                            out.println("&nbsp;</td>");

                            if (multi != 0) {
                               out.println("<td align=\"center\">");   // course
                               out.println("&nbsp;</td>");
                            }

                            out.println("<td align=\"center\">");       // f/b
                            out.println("&nbsp;</td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player13 );
                            out.println("</font></td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player14 );
                            out.println("</font></td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player15 );
                            out.println("</font></td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player16 );
                            out.println("</font></td>");

                            if (fivesomes != 0) {        // if 5-somes listed - empty slot

                               out.println("<td align=\"center\" bgcolor=\"white\">");
                               out.println("<font size=\"2\">");
                               out.println("nbsp;");
                               out.println("</font></td>");
                            }
                         }

                         if (!player17.equals( " " ) || !player18.equals( " " ) || !player19.equals( " " ) || !player20.equals( " " )) {

                            out.println("</tr><tr>");
                            out.println("<td align=\"center\">");      // date col
                            out.println("&nbsp;</td>");

                            out.println("<td align=\"center\">");      // time col
                            out.println("&nbsp;</td>");

                            if (multi != 0) {
                               out.println("<td align=\"center\">");   // course
                               out.println("&nbsp;</td>");
                            }

                            out.println("<td align=\"center\">");       // f/b
                            out.println("&nbsp;</td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player17 );
                            out.println("</font></td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player18 );
                            out.println("</font></td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player19 );
                            out.println("</font></td>");

                            out.println("<td align=\"center\" bgcolor=\"white\">");
                            out.println("<font size=\"2\">");
                            out.println( player20 );
                            out.println("</font></td>");

                            if (fivesomes != 0) {        // if 5-somes listed - empty slot

                               out.println("<td align=\"center\" bgcolor=\"white\">");
                               out.println("<font size=\"2\">");
                               out.println("nbsp;");
                               out.println("</font></td>");
                            }
                         }
                      }     // end of IF 5-somes
                      out.println("</form></tr>");
                  }

               }    // end of while

               pstmt1.close();

               out.println(" </"+tags.tbody+"></"+tags.table+">");

               if (count == 0) {
                  
                  if (club.equals( "oldoaks" )) {
                     out.println("<p align=\"center\">No tee time requests found for " + name + ".</p>");
                  } else if (!lotteryText.equals("")) {
                     out.println("<p align=\"center\">No " +lotteryText+ "s found for " + name + ".</p>");
                  } else {
                     out.println("<p align=\"center\">No lottery requests found for " + name + ".</p>");
                  }
               }

            } // end if lottery search

            
         } else {   // NOT Golf
         
            //***********************************************************
            //  ACTIVITY Processing
            //***********************************************************
            //
            PreparedStatement pstmt2 = null;
            
            //
            // non-Golf Activity - find any reservations for the selected name
            //
            pstmt1 = con.prepareStatement (
                "SELECT *, " +
                   "DATE_FORMAT(ash.date_time, '%W, %b. %D') AS pretty_date, " +
                   "DATE_FORMAT(ash.date_time, '%Y%m%d') AS dateymd, " +
                   "DATE_FORMAT(ash.date_time, '%l:%i %p') AS pretty_time " +
                "FROM activity_sheets_players asp " +
                "LEFT OUTER JOIN activity_sheets ash ON ash.sheet_id = asp.activity_sheet_id " +
                "LEFT OUTER JOIN activities a ON a.activity_id = ash.activity_id " +
                "WHERE asp.player_name LIKE ? " +
                   "AND DATE(ash.date_time) >= DATE(now()) " +
                "ORDER BY ash.date_time");

            pstmt1.clearParameters();        // clear the parms
            pstmt1.setString(1, sname);

            rs = pstmt1.executeQuery();      // execute the prepared stmt

            // if we found any then output the header row.
            //rs.last();
            //if (rs.getRow() > 0) {

            out.println("<H2>Search Results for " + name + "</H2><br>");
            out.println("");
            // Instructions for this page
            out.println("<div class=\"main_instructions\"><H2>Reservations</H2>");          
            out.println("To join an open reservation simply click on the date and time (if green & underlined).</div>");          

            out.println("<"+tags.table+" class=\"rwdTable standard_list_table rwdCompactible\">");
            out.println("  <"+tags.thead+" class=\"rwdThead\"><"+tags.tr+" class=\"rwdTr\">");
            out.println("  <"+tags.th+" class=\"rwdTh\">Date/Time</"+tags.th+">");
            out.println("  <"+tags.th+" class=\"rwdTh\">Location</"+tags.th+">");
            out.println("  <"+tags.th+" class=\"rwdTh\">Players</"+tags.th+">");
            out.println(" </"+tags.tr+"></"+tags.thead+">");
            out.println(" <"+tags.tbody+" class=\"rwdTbody\">");
            //}
 
            //rs.beforeFirst();       // back up

            //
            //  Get each record and display it
            //
            count = 0;             // number of records found

            while (rs.next()) {
               
               StringBuilder playerList = new StringBuilder();
               String player = null;
               count++;
               int pcount = 0;

               //
               //   Get the players 
               //
               pstmt2 = con.prepareStatement("" +
                      "SELECT *, (" +
                          "SELECT COUNT(*) AS players " +
                          "FROM activity_sheets_players " +
                          "WHERE activity_sheet_id = ?) AS part_of " +
                      "FROM activity_sheets_players " +
                      "WHERE activity_sheet_id = ? " +
                      "ORDER BY pos");
               pstmt2.clearParameters();
               pstmt2.setInt(1, rs.getInt("sheet_id"));
               pstmt2.setInt(2, rs.getInt("sheet_id"));
               rs2 = pstmt2.executeQuery();

               while ( rs2.next() ) {
                   player = rs2.getString("player_name");
                  pcount++;          // count number of players
                  if(rwd){
                    if(!player.equals("")){
                        playerList.append("<div");
                        if(player.equals( name )){
                            playerList.append(" class=\"me\"");
                        }
                        playerList.append(">");
                        playerList.append("<span>");
                        playerList.append(player);
                        playerList.append("</span>");
                        //out.print("<span>"+pcw[i]+"</span>");
                        playerList.append("</div>");
                    }
                  } else {
                      playerList.append(player);
                      playerList.append("<BR>");     // add player to playerlist
                  }
               }

               //
               //  Build the HTML for each record found
               //
               out.println("<"+tags.tr+" class=\"rwdTr\">");
               
               if(rwd){
                   out.print("<"+tags.td+" class=\"rwdTd sT\">");
                   if (rs.getInt("force_singles") == 0 && pcount < rs.getInt("max_players")) {      // if member can join
                      out.print("<a class=\"standard_button\" href=\"javascript:void(0)\" onclick=\"top.location.href='Member_activity_slot?slot_id=" + rs.getInt("sheet_id") + "&date=" + rs.getInt("dateymd") + "&index=888'\">" + rs.getString("pretty_date") + " at " + rs.getString("pretty_time") + "</a>"); 
                   } else {
                      out.print("<div class=\"time_slot\">" + rs.getString("pretty_date") + " at " + rs.getString("pretty_time") + "</div>"); 
                   }
                   out.print("</"+tags.td+">");
                   
                   out.print("<"+tags.td+" class=\"rwdTd sN\">");
                   out.println(getActivity.getFullActivityName(rs.getInt("activity_id"), con));
                   out.print("</"+tags.td+">");

                   out.print("<"+tags.td+" class=\"rwdTd sP\">");
                   out.println( playerList );
                   out.print("</"+tags.td+">");
               } else {
                   out.println("<td align=\"center\">");
                   if (rs.getInt("force_singles") == 0 && pcount < rs.getInt("max_players")) {      // if member can join
                      out.print("<a href=\"javascript:void(0)\" onclick=\"top.location.href='Member_activity_slot?slot_id=" + rs.getInt("sheet_id") + "&date=" + rs.getInt("dateymd") + "&index=888'\"><font size=2 color=darkGreen>" + rs.getString("pretty_date") + " at " + rs.getString("pretty_time") + "</font></a>"); 
                   } else {
                      out.print("<font size=2>" + rs.getString("pretty_date") + " at " + rs.getString("pretty_time") + "</font>"); 
                   }
                   out.println("</td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println(getActivity.getFullActivityName(rs.getInt("activity_id"), con));
                   out.println("</font></td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( playerList );
                   out.println("</font></td>");
               }
               out.println("</"+tags.tr+">");          

               pstmt2.close();

            }    // end of while

            pstmt1.close();

            out.println(" </"+tags.tbody+"></"+tags.table+">");

            if (count == 0) {

               out.println("<p align=\"center\">No records found for " + name + ".</p>");
            }               
                 
         }    // end of IF GOLF
            
            

         //
         //  End of HTML page
         //
         if(!rwd){
            out.println("</td></tr></table><br>");                // end of main page table & column
         }
         out.println("<div style=\"text-align:center;\">");
         out.println("<input id=\"back\" name=\"back\" type=\"button\" value=\"Go Back\" onclick=\"window.location.href='Member_searchmem'\" />");
         out.println("</div>");
         if(!rwd){
             out.println("</div>"); // end reservations
             out.println("</center></div></div>");
         }


         Common_skin.outputPageEnd(club, sess_activity_id, out, req);    // finish the page       
             
         out.close();


      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><BR>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Error:" + exc.getMessage());
         out.println("<BR><BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact your club manager.");
         out.println("<br><br><a href=\"Member_searchmem\">Return</a>");  // return to searchmain.htm
         out.println("</CENTER></BODY></HTML>");
         out.close();

      }     // end of search function

//   }        // end of call type if

 }   // end of doPost


 // *********************************************************
 // Missing or invalid data entered...
 // *********************************************************

 private void invData(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><BR>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR>Sorry, some data you entered is missing or invalid.<BR>");
   out.println("<BR>You must enter the player's name, or some portion of it.<BR>");
   out.println("<BR>Please try again.<BR>");
   out.println("<BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }

}
