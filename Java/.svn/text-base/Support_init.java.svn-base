/***************************************************************************************     
 *   Support_init:  This servlet will process the init request from Support's Init page.
 *                  It will perform the processing necessary to initialize a club's web site.
 *
 *
 *   called by:  support_init.htm
 *
 *   created: 11/30/2001   Bob P.
 *
 *
 *   last updated:
 *
 *        7/15/10   Added consec_mem_csv and consec_pro_csv fields to activities table
 *        7/07/10   Added stripalpha and stripdash fields to club5 table
 *        5/25/10   Added sort_by field to clubparm2 table
 *        5/20/10   Added orig1-5 values to teecurr2 table
 *        4/19/10   Added unique key to hotel3 and removed its guest1-36 fields.  Added hotel3_gtypes table to hold the guest types instead.
 *        4/16/10   Changes to club5, guestres2 and guestqta4 tables. Added guestres2_gtypes & guestqta4_gtypes tables.
 *        4/15/10   Added guest_id field to notifications_players table
 *        4/06/10   Added sort_by field to activities table
 *        3/11/10   Moved guest_id fields to the ends of their respective tables
 *        2/19/10   Added activity_sheet_notes table
 *        2/05/10   Added allow_lesson field to restriction2 table.
 *        2/04/10   Added website_url field to club5 to hold a club's website url
 *        1/25/10   Add seamless_caller & allow_mobile to club5 table
 *                  Removed unique key (guest) guest5 and added a new one (key1) for guest & activity_id
 *        1/21/10   Changed guestdb_bindings table to guestdb_hosts, added inact field to guestdb_data table
 *        1/14/10   Added unique index to member2b for mobile_user field
 *        1/14/10   Added report_ignore & related_ids to activity_sheets table
 *        1/06/10   Added Guest Database tables and fields (*guestdb, *guestdb_data, *guestdb_bindings, teecurr2, teepast2,
 *                  evntsup2b, lreqs3, activity_sheets_players, wait_list_signups_players, activities, club5, guest5) * = new
 *        1/03/10   Added consec_mem & consec_pro to activities table and
 *                  also added mobile mobile_user, _pass, _count, _iphone to member2b table
 *       12/15/09   Added inact field to demo_clubs table
 *       12/03/09   Added activity_id field to buddy table
 *       12/02/09   Add partner table for use with the updated Partner List feature
 *       12/01/09   Add ntrp_rating and usta_num to member2b table
 *       11/17/09   Add remaining Activity tables - activity_sheet_history, activity_sheets & activity_sheets_players
 *       11/16/09   Add additional fields to the activities table
 *       11/08/09   Add type_id in demo_clubs table and sheet_activity_id in lessonbook5 table
 *       11/03/09   Drop redundent 'name' key on restriction2 table and add a unique key to guestres2 (name & activity_id)
 *       11/03/09   Add locations text field to restriction2, guestres2, block2 tables
 *       11/03/09   Add demo_clubs_types table and type_id int to demo_clubs table
 *       11/02/09   Add clinic tinyint to lessongrp5 table
 *       10/30/09   Add activity_id to demo_clubs table
 *       10/16/09   Add fail_code to lreqs3 to track why a request was not assigned
 *       10/16/09   Added numorus fields to the lesson book tables for adding activity support
 *       10/06/09   Add activity_id to lessonpro5 and inactive tinyint to events2b and evntsup2b tables
 *        9/30/09   Add primary key id, and activity_id to block2 table and event_id & activity_id to events2b
 *        9/14/09   Added default_activity_id to member2b table
 *        9/08/09   Remove stats5 db table processing as we don't use this any longer.
 *        8/22/09   Add activity_id field to guestres2, guest5, mship5 tables
 *                  Add activity_id & default_entry fields to login2 table
 *                  Add foretees_mode & genrez_mode fields to club5 table
 *                  Add new activities table
 *        5/18/09   Add new iCal field to member2b and lessonpro5 table
 *        5/18/09   Increase field size for homeclub1-5 & address1-5 in evntsup2b table
 *        4/03/09   Default proshop4tea to have DINING_REQUEST and DINING_CONFIG flags set to 1
 *        3/28/09   Add new grev1-5 fieids to teepast2
 *        3/27/09   Add userg field to notifications_players table
 *        3/26/09   Add insert for proshopfb default user login
 *        3/18/09   Updated with new tables for Dining and Demo Club features
 *        2/18/09   Add new fieids to teepast2 (mship1-5, mtype1-5, gtype1-5)
 *        2/03/09   Add DEMOCLUBS_CHECKIN, DEMOCLUBS_MANAGE, DINING_REQUEST, and DINING_CONFIG fields to login2 init
 *       12/23/08   Add unique id to restriction2 and guestres2 tables, add rest_suspend table for restriction suspensions
 *       11/14/08   updated the events2b & evntsup2b tables
 *       10/30/08   Changed wait_list.auto_assign to member_view_teesheet
 *       10/11/08   Added lottery_text to club5 and courseReq to lreqs3
 *        9/12/08   Fixed syntax errors
 *        9/11/08   Modified evntsup2b table - removed unsed fields and made 'id' an auto-increment field
 *        9/10/08   Removed creation of proshop2/3/4/5 users with implementation of limited access proshop user system
 *        9/10/08   Add teesheet and bgColor fields to mem_notice table
 *        8/11/08   Modifications to login2 for further limited access features
 *        7/30/08   Add new backup_emails table
 *        7/17/08   Add pos_paynow flag to club5 for 'Pay Now' option (case 1429).
 *        7/16/08   Add pos_hist table for POS reports (case 1429).
 *        7/11/08   Add new permission fields to login2 table for limitied access users
 *        7/07/08   Add default course fields to club5 (case 1513).
 *        6/30/08   Add the three wait list tables
 *        5/05/08   Add NoDup unique index to teecurr2 table
 *        5/05/08   Add five gender and ghin fields to the evntsup2b table
 *        3/05/08   Add numerous fields to club5, member2b, mship5, teecurr2, teepast2, guest5, events2b, lottery3 tables
 *        2/21/08   Add new eo_week fieldl to custom_sheets table
 *        2/06/08   Add new custom_sheets & custom_tee_times tables
 *        2/05/08   Add new cutoffDays & cutoffTime fields to club5 table
 *       12/31/07   Change type field in score_postings table definition to two chars
 *       11/26/07   Add fb column to teereport4 table
 *       11/17/07   Change the ForeTees passwords for admin4tea and proshop4tea.
 *       10/31/07   Correct score field in score_postings table definition
 *       10/26/07   Add new max_originations field to club5 table
 *        9/28/07   Add minsize to events2b table
 *        9/11/07   Add gender and primary indicator to member2b table
 *        9/07/07   Add club5.emailPass field
 *        8/20/07   Add proside field to mem_notice table
 *        7/24/07   Change hndcp fields from real to double(3,1) to better control values in member2b, teecurr2 and evntsup2b
 *        7/24/07   Add unique index to username field in member2b table
 *        7/18/07   Add moved field to evntsup2b table
 *        6/18/07   Updated indexs on lessonbook5, lottery3, lreqs3, mship5, guest5, clubparm2, restriction2
 *        6/05/07   Add club options to club5 for web site interface and roster sync options.
 *        5/03/07   Add tee_id to score_postings table
 *        5/01/07   Add 'viewdays' to mship5 table
 *        4/27/07   Add custom fields to teepast2 table
 *        4/26/07   Add lottery_email to teecurr2 table
 *        4/12/07   Change teehist.mname from varchar(30) to varchar(50)
 *                  Add create_date & last_mod_date to teepast2 table
 *                  Add custom_disp1-5, custom_string, custom_int, create_date & last_mod_date to teecurr2 table
 *        4/06/07   Add inact, billable, last_sync_date to member2b table
 *        3/27/07   Add unique index (id field) to lreqs3 table
 *        3/25/07   Changes for hdcp - club_num & assoc_num are now strings not integers
 *        3/21/07   Made club5.init a text field instead of a varchar
 *        3/15/07   More changes TLT and ghin integration, modified club5, member2b and 
 *                  added hdcp_club_num & hdcp_assoc_num tables
 *        2/27/07   Added various fields to club5 table for TLT and ghin integration, 
 *                  also added new score_postings and tees tables, and the two notification tables
 *        2/26/07   Add mem_notice table for Member Notices feature.
 *       12/13/06   Increase the size of 'bag' in member2b from 6 to 12.
 *       10/10/06   Add afb2 - afb5 fields to lreqs table.
 *        9/30/06   Add salestax for IBS POS to club5 table
 *        8/26/06   Add fields for IBS cost values to clubparm2 table
 *        8/22/06   Added no_reservations field to club5 table
 *        7/27/06   Added Pace of Play tables and fields
 *        7/26/06   Increaed the length of the POS related fields in guest5 and mship5 tables 
 *                   - Added clubparm_id uid field to clubparm5
 *        6/26/06   Add auto_blocked field to teecurr2 for the blockers feature
 *        5/22/06   Add recid col to lessonbook5 so each record has an id.
 *        2/27/06   Add teehist to track the tee time history of changes.
 *        2/09/06   Increase the length of password in member2b from varchar(10) to varchar(15).
 *        2/07/06   Add webid field to member2b table for member mapping in web site interface.
 *       12/30/05   Add new teepastempty table.
 *       12/30/05   Add new tmodes table.
 *        6/23/05   Add new lassigns5 table, add checkothers to lreqs3 for new lottery type.
 *        6/16/05   Add course and time fields to teereport4 table.
 *        5/02/05   Add member and membership types to event table.
 *        3/08/05   Ver 5 - add precheckin to club5 and add diary and diarycc tables.
 *        1/24/05   Ver 5 - change club2 to club5 and stats2 to stats5.
 *        1/05/05   Ver 5 - Add a constimesm & constimesp to club2 for Consecutive Tee Times feature.
 *        1/03/05   Ver 5 - Add a proshop user (proshop4tea) for our use only.
 *        1/02/05   Add a random password generator for the admin and proshop passwords.
 *       10/19/04   Version 5 changes:
 *                    - Add 'lessonpro5', 'lessonbook5', 'lessontype5', 'lessontime5' & 'lessonblock' tables.
 *       10/11/04   Add 'courseid' field to clubparm2 for Jonas POS I/F.
 *       10/01/04   Remove Sales Tax fields in mship5, guest5 & clubparm2 as they are no longer used by Jonas I/F.
 *                  Also, remove POSChit from club2.
 *        9/01/04   Add new table teereport4 for alphabetical list of members on the tee sheet.
 *        8/26/04   Version 5 changes:
 *                    - Add 'mship5' table and move info from club2 into this table (allow for 24 mship types).
 *                    - Add 'guest5' table and move info from club2 into this table (allow for 24 guest types).
 *        7/07/04   Add new table eventa4 for alphabetical list of members in event.
 *        4/22/04   Add msub_type (member type - subtype) for Hazeltine.
 *        2/25/04   Add POS fields to club2 table for Jonas and Pro-ShopKeeper.
 *        2/16/04   Add POS Charge Code fields to the event table and the 9-hole indicators to
 *                  the event signup table.
 *        2/09/04   Add items to teecurr to indicate the players have been processed by POS.
 *        2/09/04   Add the dist4p table for proshop email distribution lists.
 *        2/09/04   Add p9x (9 hole indicators) to teecurr, teepast and lreqs tables.
 *        2/06/04   Add customizable modes of transportation (16) to Stats table.
 *        2/05/04   Add customizable modes of transportation (16) and POS charge codes to Course table.
 *        2/04/04   Add POS fields to club and course tables.
 *        2/04/04   Add 'display handicaps' and 'support unacompanied guests' options to club table.
 *        1/24/04   Add table for email distribution lists, add username fields to buddy.
 *        1/21/04   Enhancements for Version 4 - add 'days in adv' values for each mship type (club). 
 *       11/19/03   Enhancements for Version 3 - add hotel tables and info to other tables (teecurr).
 *        7/18/03   Enhancements for Version 3 of the software.
 *                  Add Lottery tables.
 *                  Increase buddy table to 12 entries.
 *                  Add usergx to teecurr, teepast & evntsup to track which member the guests belong to.
 *  
 *        3/10/03   Add new fields to events2 and evntsup2 for waiting list.
 *
 *       12/02/02   Add new fields to tables for Version 2 - indicated by the ending '2'
 *                  value on the table name.
 *
 *        9/18/02   Enhancements for Version 2 of the software.
 *
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import java.lang.Math;


public class Support_init extends HttpServlet {
 
       
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 // Process the form request from support_init.htm.....

 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
        
   Connection con = null;                  // init DB objects
   Connection con2 = null;                  // init DB objects
   Statement stmt = null;
   ResultSet rs = null;
     
   String support = "support";             // valid username

   HttpSession session = null; 


   // Make sure user didn't enter illegally.........

   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   String user = (String)session.getAttribute("user");   // get username

   if (!user.equals( support )) {

      invalidUser(out);            // Intruder - reject
      return;
   }


   // Load the JDBC Driver and connect to DB.........

   String club = (String)session.getAttribute("club");   // get club name

   try {
      con = dbConn.Connect(club);

   }
   catch (Exception exc) {

      // Error connecting to db....

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_init.htm\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   // 'support' is defined in login...
   // refer to SystemUtils for connect info...

   int count = 0;
   
   // Check if the v5 DB Tables already exist.........

   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT * FROM login2");   // check for login2 table...

      stmt.close();

      // v5 DB Table already exists - do not continue....

      out.println("<HTML><HEAD><TITLE>Database Already Exists Warning</TITLE></HEAD>");
      out.println("<BODY><CENTER><H1>V5 Database Tables Already Exist</H1>");
      out.println("<BR><BR><b>Warning,</b> the v5 database tables already exist.");
      out.println("<BR><BR>To start from scratch you will have to first drop the database via MySQL.");
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");

      if (con != null) {
         try {
            con.close();       // Close the db connection........
         }
         catch (SQLException ignored) {
         }
      }
      return;
              
   }
   catch (Exception exc) {

      // This is good - v5 tables do not exist - create them now
   }

   try {

      stmt.executeUpdate("CREATE TABLE sessionlog (" +
                            "date bigint, sdate varchar(36), msg text, " +
                            "index ind1 (date))");

      //
      //  Proshop and Admin Login Table
      //
      stmt.executeUpdate("CREATE TABLE login2 (" + 
                            "username varchar(15) NOT NULL default '', " +
                            "activity_id int(11) NOT NULL default '0', " +
                            "default_entry tinyint(4) NOT NULL default '0', " +
                            "password varchar(10) NOT NULL default '', " + 
                            "message varchar(10) NOT NULL default '', " + 
                            "name_first varchar(20) NOT NULL default '', " + 
                            "name_last varchar(20) NOT NULL default '', " + 
                            "name_mi char(1) NOT NULL default '', " + 
                            "inact tinyint(4) NOT NULL default '0', " + 
                            "start_time int(11) NOT NULL default '0', " + 
                            "end_time int(11) NOT NULL default '0', " + 
                            "display_bag tinyint(4) NOT NULL default '0', " + 
                            "display_mnum tinyint(4) NOT NULL default '0', " + 
                            "display_hdcp tinyint(4) NOT NULL default '0', " + 
                            "SYS_CONFIG tinyint(4) NOT NULL default '1', " + 
                            "TOOLS_ANNOUNCE tinyint(4) NOT NULL default '1', " + 
                            "TOOLS_EMAIL tinyint(4) NOT NULL default '1', " + 
                            "TOOLS_SEARCHTS tinyint(4) NOT NULL default '1', " + 
                            "TOOLS_HDCP tinyint(4) NOT NULL default '1', " + 
                            "REPORTS tinyint(4) NOT NULL default '1', " +
                            "REST_OVERRIDE tinyint(4) NOT NULL default '1', " +
                            "LOTT_UPDATE tinyint(4) NOT NULL default '1', " + 
                            "LOTT_APPROVE tinyint(4) NOT NULL default '1', " + 
                            "LESS_CONFIG tinyint(4) NOT NULL default '1', " + 
                            "LESS_VIEW tinyint(4) NOT NULL default '1', " + 
                            "LESS_UPDATE tinyint(4) NOT NULL default '1', " + 
                            "EVNTSUP_UPDATE tinyint(4) NOT NULL default '1', " + 
                            "EVNTSUP_VIEW tinyint(4) NOT NULL default '1', " + 
                            "EVNTSUP_MANAGE tinyint(4) NOT NULL default '1', " + 
                            "WAITLIST_UPDATE tinyint(4) NOT NULL default '1', " + 
                            "WAITLIST_VIEW tinyint(4) NOT NULL default '1', " + 
                            "WAITLIST_MANAGE tinyint(4) NOT NULL default '1', " + 
                            "TS_VIEW tinyint(4) NOT NULL default '1', " + 
                            "TS_UPDATE tinyint(4) NOT NULL default '1', " + 
                            "TS_CHECKIN tinyint(4) NOT NULL default '1', " + 
                            "TS_PRINT tinyint(4) NOT NULL default '1', " + 
                            "TS_POS tinyint(4) NOT NULL default '1', " + 
                            "TS_CTRL_FROST tinyint(4) NOT NULL default '1', " + 
                            "TS_CTRL_TSEDIT tinyint(4) NOT NULL default '1', " + 
                            "TS_CTRL_EMAIL tinyint(4) NOT NULL default '1', " + 
                            "TS_PACE_VIEW tinyint(4) NOT NULL default '1', " + 
                            "TS_PACE_UPDATE tinyint(4) NOT NULL default '1', " +
                            "TS_PAST_VIEW tinyint(4) NOT NULL default '1', " +
                            "TS_PAST_UPDATE tinyint(4) NOT NULL default '1', " +
                            "TS_NOTES_VIEW tinyint(4) NOT NULL default '1', " +
                            "TS_NOTES_UPDATE tinyint(4) NOT NULL default '1', " +
                            "SYSCONFIG_CLUBCONFIG tinyint(4) NOT NULL default '1', " + 
                            "SYSCONFIG_TEESHEETS tinyint(4) NOT NULL default '1', " + 
                            "SYSCONFIG_EVENT tinyint(4) NOT NULL default '1', " + 
                            "SYSCONFIG_LOTTERY tinyint(4) NOT NULL default '1', " + 
                            "SYSCONFIG_WAITLIST tinyint(4) NOT NULL default '1', " + 
                            "SYSCONFIG_RESTRICTIONS tinyint(4) NOT NULL default '1', " + 
                            "SYSCONFIG_MEMBERNOTICES tinyint(4) NOT NULL default '1', " +
                            "DEMOCLUBS_CHECKIN tinyint(4) NOT NULL default '1', " +
                            "DEMOCLUBS_MANAGE tinyint(4) NOT NULL default '1', " +
                            "DINING_REQUEST tinyint(4) NOT NULL default '1', " +
                            "DINING_CONFIG tinyint(4) NOT NULL default '0'," +
                            "UNIQUE KEY key1 (username,activity_id) " +
                        ") ENGINE=MyISAM;");
              
      //
      //  Member Table
      //
      stmt.executeUpdate("CREATE TABLE member2b (username varchar(15), password varchar(15), " +
                         "name_last varchar(20), name_first varchar(20), name_mi char, " +
                         "m_ship varchar(30), m_type varchar(30), email varchar(50), " +
                         "count integer, c_hancap double(3,1), g_hancap double(3,1), wc varchar(4), " +
                         "message varchar(10), emailOpt smallint, memNum varchar(10), " +
                         "ghin varchar(16), locker varchar(6), bag varchar(12), birth integer, " +
                         "posid varchar(15), msub_type varchar(30), email2 varchar(50), " +
                         "phone1 varchar(24), phone2 varchar(24), name_pre varchar(4), name_suf varchar(4), " +
                         "webid varchar(15), " +
                         "email_bounced tinyint(4) NOT NULL default '0', " +
                         "email2_bounced tinyint(4) NOT NULL default '0', " +
                         "hdcp_club_num_id int NOT NULL default '0', " +
                         "hdcp_assoc_num_id int NOT NULL default '0', " +
                         "default_tee_id int NOT NULL default '0', " +
                         "default_holes tinyint NOT NULL default '0', " +
                         "displayHdcp tinyint NOT NULL default '0', " +
                         "inact tinyint NOT NULL default '0', " +
                         "billable tinyint NOT NULL default '1', " +
                         "last_sync_date date NOT NULL default '0000-00-00', " +
                         "gender enum('','M','F') NOT NULL default '', " +
                         "pri_indicator tinyint NOT NULL default '0', " +
                         "tflag varchar(4) NOT NULL default '', " +
                         "ical1 tinyint NOT NULL default '-1', " +
                         "ical2 tinyint NOT NULL default '-1', " +
                         "default_activity_id int NOT NULL default '0', " +
                         "ntrp_rating double(2,1) NOT NULL default '0.0', " +
                         "usta_num varchar(16) NOT NULL default '', " +
                         "mobile_user varchar(15) DEFAULT NULL, " +
                         "mobile_pass varchar(15) DEFAULT NULL, " +
                         "mobile_count tinyint NOT NULL default '0', " +
                         "mobile_iphone tinyint NOT NULL default '0', " +
                         "unique index ind1 (username), " +
                         "index ind2 (name_last, name_first, name_mi), " +
                         "index ind3 (memNum), " +
                         "unique index ind4 (mobile_user))");
                     // leave index at end (for searches)
     
      //
      //  Club Parameters Table
      //
      stmt.executeUpdate("CREATE TABLE club5 (clubName varchar(30), multi smallint, " +
                         "lottery smallint, contact varchar(40), email varchar(40), " +
                         "website_url varchar(255) NOT NULL default '', " + /*
                         "guest1 varchar(20), guest2 varchar(20), guest3 varchar(20), guest4 varchar(20), " +
                         "guest5 varchar(20), guest6 varchar(20), guest7 varchar(20), guest8 varchar(20), " +
                         "guest9 varchar(20), guest10 varchar(20), guest11 varchar(20), guest12 varchar(20), " +
                         "guest13 varchar(20), guest14 varchar(20), guest15 varchar(20), guest16 varchar(20), " +
                         "guest17 varchar(20), guest18 varchar(20), guest19 varchar(20), guest20 varchar(20), " +
                         "guest21 varchar(20), guest22 varchar(20), guest23 varchar(20), guest24 varchar(20), " +
                         "guest25 varchar(20), guest26 varchar(20), guest27 varchar(20), guest28 varchar(20), " +
                         "guest29 varchar(20), guest30 varchar(20), guest31 varchar(20), guest32 varchar(20), " +
                         "guest33 varchar(20), guest34 varchar(20), guest35 varchar(20), guest36 varchar(20), " + */
                         "mem1 varchar(30), mem2 varchar(30), mem3 varchar(30), mem4 varchar(30), " +
                         "mem5 varchar(30), mem6 varchar(30), mem7 varchar(30), mem8 varchar(30), " +
                         "mem9 varchar(30), mem10 varchar(30), mem11 varchar(30), mem12 varchar(30), " +
                         "mem13 varchar(30), mem14 varchar(30), mem15 varchar(30), mem16 varchar(30), " +
                         "mem17 varchar(30), mem18 varchar(30), mem19 varchar(30), mem20 varchar(30), " +
                         "mem21 varchar(30), mem22 varchar(30), mem23 varchar(30), mem24 varchar(30), " +
                         "mship1 varchar(30), mship2 varchar(30), mship3 varchar(30), mship4 varchar(30), " +
                         "mship5 varchar(30), mship6 varchar(30), mship7 varchar(30), mship8 varchar(30), " +
                         "mship9 varchar(30), mship10 varchar(30), mship11 varchar(30), mship12 varchar(30), " +
                         "mship13 varchar(30), mship14 varchar(30), mship15 varchar(30), mship16 varchar(30), " +
                         "mship17 varchar(30), mship18 varchar(30), mship19 varchar(30), mship20 varchar(30), " +
                         "mship21 varchar(30), mship22 varchar(30), mship23 varchar(30), mship24 varchar(30), " +
                         "x smallint, xhrs smallint, adv_zone varchar(8), emailOpt smallint, " +
                         "lottid bigint, hotel smallint, userlock smallint, " +
                         "unacompGuest smallint, hndcpProSheet smallint, hndcpProEvent smallint, " +
                         "hndcpMemSheet smallint, hndcpMemEvent smallint, posType varchar(20), " +
                         "logins integer, rndsperday smallint, hrsbtwn smallint, forcegnames smallint, " +
                         "hidenames smallint, constimesm smallint, constimesp smallint, precheckin smallint, " +
                         "paceofplay tinyint NOT NULL default '0', no_reservations tinyint NOT NULL default '0', " +
                         "salestax real NOT NULL default '0', nwindow_starttime time default NULL, " +
                         "nwindow_endtime time default NULL, notify_interval tinyint(4) NOT NULL, " +
                         "hdcpSystem varchar(8) NOT NULL, allowMemPost tinyint(4) NOT NULL, " +
                         "lastHdcpSync datetime NOT NULL, hdcpStartDate date NOT NULL, " +
                         "hdcpEndDate date NOT NULL, " +
                         "rsync tinyint NOT NULL default '0', seamless tinyint NOT NULL default '0', " +
                         "zipcode varchar(5) NOT NULL, primaryif tinyint NOT NULL default '0', " +
                         "mnum tinyint NOT NULL default '0', mapping tinyint NOT NULL default '0', " +
                         "stripzero tinyint(4) NOT NULL default '0', " +
                         "stripalpha tinyint(4) NOT NULL default '0', " +
                         "stripdash tinyint(4) NOT NULL default '0', " +
                         "emailPass varchar(16) NOT NULL default '', " +
                         "max_originations tinyint NOT NULL default '0', " +
                         "cutoffDays tinyint NOT NULL default '99', " + 
                         "cutoffTime smallint NOT NULL default '0', " +
                         "smtp_addr varchar(50) NOT NULL default '', " +
                         "smtp_port smallint NOT NULL default '25', " +
                         "smtp_auth tinyint NOT NULL default '0', " +
                         "smtp_user varchar(50) NOT NULL default '', " +
                         "smtp_pass varchar(32) NOT NULL default '', " +
                         "email_from varchar(50) NOT NULL default '', " +
                         "email_from_pro varchar(50) NOT NULL default '', " +
                         "email_from_mem varchar(50) NOT NULL default '', " + 
                         "default_course_mem varchar(30) NOT NULL default '', " + 
                         "default_course_pro varchar(30) NOT NULL default '', " + 
                         "pos_paynow smallint NOT NULL DEFAULT '0', " +
                         "lottery_text varchar(30) NOT NULL, " + 
                         "democlub_days smallint(6) NOT NULL default '0'," +
                         "dining tinyint(4) NOT NULL default '1', " +
                         "guestdb tinyint(4) NOT NULL default '0', " +
                         "foretees_mode tinyint(4) NOT NULL default '1'," +
                         "genrez_mode tinyint(4) NOT NULL default '0', " +
                         "seamless_caller varchar(24) NOT NULL default '', " +
                         "allow_mobile tinyint(4) NOT NULL default '0' " +
                         ") ENGINE=MyISAM;");
                         

      //
      //  Membership Type Table (one entry per mship type)
      //
      stmt.executeUpdate("CREATE TABLE mship5 (mship varchar(30), " +
                         "activity_id int NOT NULL default '0', " +
                         "mtimes integer, period varchar(5), days1 smallint, " +
                         "days2 smallint, days3 smallint, days4 smallint, " +
                         "days5 smallint, days6 smallint, days7 smallint, " +
                         "advhrd1 smallint, advmind1 smallint, advamd1 varchar(2), " +
                         "advhrd2 smallint, advmind2 smallint, advamd2 varchar(2), " +
                         "advhrd3 smallint, advmind3 smallint, advamd3 varchar(2), " +
                         "advhrd4 smallint, advmind4 smallint, advamd4 varchar(2), " +
                         "advhrd5 smallint, advmind5 smallint, advamd5 varchar(2), " +
                         "advhrd6 smallint, advmind6 smallint, advamd6 varchar(2), " +
                         "advhrd7 smallint, advmind7 smallint, advamd7 varchar(2), " +
                         "mpos varchar(20), mposc varchar(20), m9posc varchar(20), " +
                         "mshipItem varchar(20), mship9Item varchar(20), " +
                         "viewdays smallint NOT NULL default '0', " +
                         "tflag varchar(4) NOT NULL default '', " +
                         "UNIQUE key1 (activity_id,mship))");


      //
      //  Guest Type Table (one entry per guest type)
      //
      stmt.executeUpdate("CREATE TABLE guest5 (guest varchar(20), " +
                         "activity_id int NOT NULL default '0', " +
                         "gOpt smallint, gpos varchar(30), g9pos varchar(30), " +
                         "gstItem varchar(20), gst9Item varchar(20), " +
                         "revenue tinyint NOT NULL default '0', " +
                         "use_guestdb tinyint(4) NOT NULL default '0', " +
                         "UNIQUE key1 (activity_id,guest))");


      //
      //  Course Parameters Table
      //
      stmt.executeUpdate("CREATE TABLE clubparm2 (clubparm_id int(11) auto_increment, " +
                         "courseName varchar(30), first_hr smallint, " +
                         "first_min smallint, last_hr smallint, last_min smallint, " +
                         "betwn smallint, xx smallint, alt smallint, fives smallint, " +
                         "tmode1 varchar(20), tmodea1 varchar(3), tmode2 varchar(20), tmodea2 varchar(3), " +
                         "tmode3 varchar(20), tmodea3 varchar(3), tmode4 varchar(20), tmodea4 varchar(3), " +
                         "tmode5 varchar(20), tmodea5 varchar(3), tmode6 varchar(20), tmodea6 varchar(3), " +
                         "tmode7 varchar(20), tmodea7 varchar(3), tmode8 varchar(20), tmodea8 varchar(3), " +
                         "tmode9 varchar(20), tmodea9 varchar(3), tmode10 varchar(20), tmodea10 varchar(3), " +
                         "tmode11 varchar(20), tmodea11 varchar(3), tmode12 varchar(20), tmodea12 varchar(3), " +
                         "tmode13 varchar(20), tmodea13 varchar(3), tmode14 varchar(20), tmodea14 varchar(3), " +
                         "tmode15 varchar(20), tmodea15 varchar(3), tmode16 varchar(20), tmodea16 varchar(3), " +
                         "t9pos1 varchar(30), tpos1 varchar(30), t9pos2 varchar(30), tpos2 varchar(30), " +
                         "t9pos3 varchar(30), tpos3 varchar(30), t9pos4 varchar(30), tpos4 varchar(30), " +
                         "t9pos5 varchar(30), tpos5 varchar(30), t9pos6 varchar(30), tpos6 varchar(30), " +
                         "t9pos7 varchar(30), tpos7 varchar(30), t9pos8 varchar(30), tpos8 varchar(30), " +
                         "t9pos9 varchar(30), tpos9 varchar(30), t9pos10 varchar(30), tpos10 varchar(30), " +
                         "t9pos11 varchar(30), tpos11 varchar(30), t9pos12 varchar(30), tpos12 varchar(30), " +
                         "t9pos13 varchar(30), tpos13 varchar(30), t9pos14 varchar(30), tpos14 varchar(30), " +
                         "t9pos15 varchar(30), tpos15 varchar(30), t9pos16 varchar(30), tpos16 varchar(30), " +
                         "courseid varchar(2), tOpt1 smallint, tOpt2 smallint, tOpt3 smallint, tOpt4 smallint, " +
                         "tOpt5 smallint, tOpt6 smallint, tOpt7 smallint, tOpt8 smallint, tOpt9 smallint," +
                         "tOpt10 smallint, tOpt11 smallint, tOpt12 smallint, tOpt13 smallint, tOpt14 smallint," +
                         "tOpt15 smallint, tOpt16 smallint, " +
                         "t9posc1 varchar(30), tposc1 varchar(30), t9posc2 varchar(30), tposc2 varchar(30), " +
                         "t9posc3 varchar(30), tposc3 varchar(30), t9posc4 varchar(30), tposc4 varchar(30), " +
                         "t9posc5 varchar(30), tposc5 varchar(30), t9posc6 varchar(30), tposc6 varchar(30), " +
                         "t9posc7 varchar(30), tposc7 varchar(30), t9posc8 varchar(30), tposc8 varchar(30), " +
                         "t9posc9 varchar(30), tposc9 varchar(30), t9posc10 varchar(30), tposc10 varchar(30), " +
                         "t9posc11 varchar(30), tposc11 varchar(30), t9posc12 varchar(30), tposc12 varchar(30), " +
                         "t9posc13 varchar(30), tposc13 varchar(30), t9posc14 varchar(30), tposc14 varchar(30), " +
                         "t9posc15 varchar(30), tposc15 varchar(30), t9posc16 varchar(30), tposc16 varchar(30), " +
                         "sort_by tinyint(4) NOT NULL default '0', " +
                         "PRIMARY KEY (clubparm_id), " +
                         "UNIQUE KEY courseName (courseName))");

      //
      //  Event Configuration Table
      //
      stmt.executeUpdate("CREATE TABLE events2b (event_id int(11) auto_increment, name varchar(30), " +
                         "activity_id int NOT NULL default '0', date bigint, year integer, " +
                         "month smallint, day smallint, start_hr smallint, " +
                         "start_min smallint, stime integer, end_hr smallint, " +
                         "end_min smallint, etime integer, color varchar(24), type integer, " +
                         "act_hr smallint, act_min smallint, courseName varchar(30), signUp smallint, " +
                         "format varchar(60), pairings varchar(7), size smallint, max smallint, " +
                         "guests smallint, memcost varchar(10), gstcost varchar(10), c_month smallint, " +
                         "c_day smallint, c_year integer, c_hr smallint, c_min smallint, " +
                         "c_date bigint, c_time integer, itin text, mc smallint, pc smallint, " +
                         "wa smallint, ca smallint, gstOnly smallint, x smallint, xhrs smallint, " +
                         "holes smallint, su_month smallint, su_day smallint, su_year integer, su_hr smallint, " +
                         "su_min smallint, su_date bigint, su_time integer, fb varchar(5), " +
                         "mempos varchar(30), gstpos varchar(30), " +
                         "tmode1 smallint, tmode2 smallint, tmode3 smallint, tmode4 smallint, " +
                         "tmode5 smallint, tmode6 smallint, tmode7 smallint, tmode8 smallint, " +
                         "tmode9 smallint, tmode10 smallint, tmode11 smallint, tmode12 smallint, " +
                         "tmode13 smallint, tmode14 smallint, tmode15 smallint, tmode16 smallint, " +
                         "stime2 integer, etime2 integer, fb2 varchar(5), " +
                         "mem1 varchar(30), mem2 varchar(30), mem3 varchar(30), mem4 varchar(30), " +
                         "mem5 varchar(30), mem6 varchar(30), mem7 varchar(30), mem8 varchar(30), " +
                         "mem9 varchar(30), mem10 varchar(30), mem11 varchar(30), mem12 varchar(30), " +
                         "mem13 varchar(30), mem14 varchar(30), mem15 varchar(30), mem16 varchar(30), " +
                         "mem17 varchar(30), mem18 varchar(30), mem19 varchar(30), mem20 varchar(30), " +
                         "mem21 varchar(30), mem22 varchar(30), mem23 varchar(30), mem24 varchar(30), " +
                         "mship1 varchar(30), mship2 varchar(30), mship3 varchar(30), mship4 varchar(30), " +
                         "mship5 varchar(30), mship6 varchar(30), mship7 varchar(30), mship8 varchar(30), " +
                         "mship9 varchar(30), mship10 varchar(30), mship11 varchar(30), mship12 varchar(30), " +
                         "mship13 varchar(30), mship14 varchar(30), mship15 varchar(30), mship16 varchar(30), " +
                         "mship17 varchar(30), mship18 varchar(30), mship19 varchar(30), mship20 varchar(30), " +
                         "mship21 varchar(30), mship22 varchar(30), mship23 varchar(30), mship24 varchar(30), " +
                         "minsize tinyint NOT NULL DEFAULT '0', " +
                         "gender tinyint NOT NULL default '0', " +
                         "email1 varchar(50) NOT NULL default '', " +
                         "email2 varchar(50) NOT NULL default '', " +
                         "season tinyint NOT NULL default '0', " +
                         "export_type tinyint NOT NULL default '0', " +
                         "member_item_code varchar(30) NOT NULL, " +
                         "guest_item_code varchar(30) NOT NULL, " + 
                         "ask_homeclub tinyint(1) NOT NULL default '0'," + 
                         "ask_phone tinyint(1) NOT NULL default '0'," + 
                         "ask_address tinyint(1) NOT NULL default '0', " + 
                         "ask_hdcp tinyint(1) NOT NULL default '0', " + 
                         "ask_email tinyint(1) NOT NULL default '0', " + 
                         "ask_gender tinyint(1) NOT NULL default '0', " + 
                         "ask_shirtsize tinyint(1) NOT NULL default '0', " + 
                         "ask_shoesize tinyint(1) NOT NULL default '0', " + 
                         "ask_otherA1 tinyint(1) NOT NULL default '0', " + 
                         "ask_otherA2 tinyint(1) NOT NULL default '0', " + 
                         "ask_otherA3 tinyint(1) NOT NULL default '0', " + 
                         "req_guestname tinyint(1) NOT NULL default '0', " + 
                         "req_homeclub tinyint(1) NOT NULL default '0', " + 
                         "req_phone tinyint(1) NOT NULL default '0', " + 
                         "req_address tinyint(1) NOT NULL default '0', " + 
                         "req_hdcp tinyint(1) NOT NULL default '0', " + 
                         "req_email tinyint(1) NOT NULL default '0', " + 
                         "req_gender tinyint(1) NOT NULL default '0', " + 
                         "req_shirtsize tinyint(1) NOT NULL default '0', " + 
                         "req_shoesize tinyint(1) NOT NULL default '0', " + 
                         "req_otherA1 tinyint(1) NOT NULL default '0', " + 
                         "req_otherA2 tinyint(1) NOT NULL default '0', " + 
                         "req_otherA3 tinyint(1) NOT NULL default '0', " + 
                         "otherQ1 varchar(32) NOT NULL default '', " + 
                         "otherQ2 varchar(32) NOT NULL default '', " + 
                         "otherQ3 varchar(32) NOT NULL default '', " +
                         "locations text NOT NULL default '', " +
                         "inactive tinyint NOT NULL default '0', " +
                         "PRIMARY KEY (event_id), " +
                         "index ind1 (name), index ind2 (date, courseName))");
                     // leave index at end (for searches)

      //
      //  Event Signup Table
      //
      stmt.executeUpdate("CREATE TABLE evntsup2b (id int(11) NOT NULL auto_increment, " +
                         "name varchar(30), courseName varchar(30), " +
                         "player1 varchar(60), player2 varchar(60), player3 varchar(60), player4 varchar(60), " +
                         "player5 varchar(60), username1 varchar(15), username2 varchar(15), username3 varchar(15), " +
                         "username4 varchar(15), username5 varchar(15), p1cw varchar(4), " +
                         "p2cw varchar(4), p3cw varchar(4), p4cw varchar(4), p5cw varchar(4), " +
                         "in_use smallint, in_use_by varchar(15), hndcp1 double(3,1), hndcp2 double(3,1), hndcp3 double(3,1), " +
                         "hndcp4 double(3,1), hndcp5 double(3,1), notes text, hideNotes smallint, " +
                         "c_date bigint, c_time integer, r_date bigint, r_time integer, wait smallint, " +
                         "userg1 varchar(15), userg2 varchar(15), userg3 varchar(15), userg4 varchar(15), userg5 varchar(15), " +
                         "hole varchar(4), moved tinyint(4) NOT NULL default '0', " +
                         "gender1 enum('','M','F') NOT NULL default '', " +
                         "gender2 enum('','M','F') NOT NULL default '', " +
                         "gender3 enum('','M','F') NOT NULL default '', " +
                         "gender4 enum('','M','F') NOT NULL default '', " +
                         "gender5 enum('','M','F') NOT NULL default '', " +
                         "ghin1 varchar(16) NOT NULL default '', " +
                         "ghin2 varchar(16) NOT NULL default '', " +
                         "ghin3 varchar(16) NOT NULL default '', " +
                         "ghin4 varchar(16) NOT NULL default '', " +
                         "ghin5 varchar(16) NOT NULL default '', " +
                         "homeclub1 varchar(50) NOT NULL default '', " +
                         "homeclub2 varchar(50) NOT NULL default '', " +
                         "homeclub3 varchar(50) NOT NULL default '', " +
                         "homeclub4 varchar(50) NOT NULL default '', " +
                         "homeclub5 varchar(50) NOT NULL default '', " +
                         "phone1 varchar(20) NOT NULL default '', " +  
                         "phone2 varchar(20) NOT NULL default '', " +  
                         "phone3 varchar(20) NOT NULL default '', " +  
                         "phone4 varchar(20) NOT NULL default '', " +  
                         "phone5 varchar(20) NOT NULL default '', " +  
                         "address1 varchar(64) NOT NULL default '', " +
                         "address2 varchar(64) NOT NULL default '', " +
                         "address3 varchar(64) NOT NULL default '', " +
                         "address4 varchar(64) NOT NULL default '', " +
                         "address5 varchar(64) NOT NULL default '', " +
                         "email1 varchar(50) NOT NULL default '', " +  
                         "email2 varchar(50) NOT NULL default '', " +  
                         "email3 varchar(50) NOT NULL default '', " +  
                         "email4 varchar(50) NOT NULL default '', " +  
                         "email5 varchar(50) NOT NULL default '', " +  
                         "shirtsize1 varchar(8) NOT NULL default '', " +  
                         "shirtsize2 varchar(8) NOT NULL default '', " + 
                         "shirtsize3 varchar(8) NOT NULL default '', " + 
                         "shirtsize4 varchar(8) NOT NULL default '', " + 
                         "shirtsize5 varchar(8) NOT NULL default '', " + 
                         "shoesize1 varchar(8) NOT NULL default '', " +  
                         "shoesize2 varchar(8) NOT NULL default '', " +  
                         "shoesize3 varchar(8) NOT NULL default '', " +  
                         "shoesize4 varchar(8) NOT NULL default '', " +  
                         "shoesize5 varchar(8) NOT NULL default '', " +  
                         "other1A1 varchar(24) NOT NULL default '', " + 
                         "other1A2 varchar(24) NOT NULL default '', " + 
                         "other1A3 varchar(24) NOT NULL default '', " + 
                         "other2A1 varchar(24) NOT NULL default '', " + 
                         "other2A2 varchar(24) NOT NULL default '', " + 
                         "other2A3 varchar(24) NOT NULL default '', " + 
                         "other3A1 varchar(24) NOT NULL default '', " + 
                         "other3A2 varchar(24) NOT NULL default '', " + 
                         "other3A3 varchar(24) NOT NULL default '', " + 
                         "other4A1 varchar(24) NOT NULL default '', " + 
                         "other4A2 varchar(24) NOT NULL default '', " + 
                         "other4A3 varchar(24) NOT NULL default '', " + 
                         "other5A1 varchar(24) NOT NULL default '', " + 
                         "other5A2 varchar(24) NOT NULL default '', " + 
                         "other5A3 varchar(24) NOT NULL default '', " +
                         "inactive tinyint NOT NULL default '0', " +
                         "guest_id1 int(11) NOT NULL default '0', " +
                         "guest_id2 int(11) NOT NULL default '0', " +
                         "guest_id3 int(11) NOT NULL default '0', " +
                         "guest_id4 int(11) NOT NULL default '0', " +
                         "guest_id5 int(11) NOT NULL default '0', " +
                         "PRIMARY KEY (id), " +
                         "KEY ind1 (name, courseName), KEY ind2 (in_use))");
                     // leave index at end (for searches)

      //
      //  Member Restrictions Configuration Table
      //
      stmt.executeUpdate("CREATE TABLE restriction2 (" +
                         "id int auto_increment, activity_id int NOT NULL default '0', " +
                         "name varchar(30), sdate bigint, start_mm smallint, " +
                         "start_dd smallint, start_yy integer, start_hr smallint, " +
                         "start_min smallint, stime integer, edate bigint, end_mm smallint, " +
                         "end_dd smallint, end_yy integer, end_hr smallint, " +
                         "end_min smallint, etime integer, recurr varchar(20), " +
                         "mem1 varchar(30), mem2 varchar(30), mem3 varchar(30), mem4 varchar(30), " +
                         "mem5 varchar(30), mem6 varchar(30), mem7 varchar(30), mem8 varchar(30), " +
                         "mem9 varchar(30), mem10 varchar(30), mem11 varchar(30), mem12 varchar(30), " +
                         "mem13 varchar(30), mem14 varchar(30), mem15 varchar(30), mem16 varchar(30), " +
                         "mem17 varchar(30), mem18 varchar(30), mem19 varchar(30), mem20 varchar(30), " +
                         "mem21 varchar(30), mem22 varchar(30), mem23 varchar(30), mem24 varchar(30), " +
                         "mship1 varchar(30), mship2 varchar(30), mship3 varchar(30), mship4 varchar(30), " +
                         "mship5 varchar(30), mship6 varchar(30), mship7 varchar(30), mship8 varchar(30), " +
                         "mship9 varchar(30), mship10 varchar(30), mship11 varchar(30), mship12 varchar(30), " +
                         "mship13 varchar(30), mship14 varchar(30), mship15 varchar(30), mship16 varchar(30), " +
                         "mship17 varchar(30), mship18 varchar(30), mship19 varchar(30), mship20 varchar(30), " +
                         "mship21 varchar(30), mship22 varchar(30), mship23 varchar(30), mship24 varchar(30), " +
                         "color varchar(24), courseName varchar(30), fb varchar(5), showit varchar(3), " +
                         "locations text NOT NULL default '', allow_lesson tinyint(4) NOT NULL DEFAULT '0', " +
                         "PRIMARY KEY (id), " +
                         "UNIQUE KEY key1 (name,activity_id), " +
                         "KEY dates (sdate,edate), " +
                         "KEY courseName (courseName(10)))");
      
                         //"index ind1 (name), index ind2 (sdate, edate, courseName), " +
                         //"index ind3 (sdate, edate, stime, etime, courseName))");
                     // leave index at end (for searches)

      //
      //  5-Some Restrictions Configuration Table
      //
      stmt.executeUpdate("CREATE TABLE fives2 (name varchar(30), sdate bigint, start_mm smallint, " +
                         "start_dd smallint, start_yy integer, start_hr smallint, " +
                         "start_min smallint, stime integer, edate bigint, end_mm smallint, " +
                         "end_dd smallint, end_yy integer, end_hr smallint, " +
                         "end_min smallint, etime integer, recurr varchar(20), " +
                         "color varchar(24), courseName varchar(30), fb varchar(5))");

      //
      //  Block Tee Times Configuration Table
      //
      stmt.executeUpdate("CREATE TABLE block2 (id int auto_increment, name varchar(30), " +
                         "activity_id int NOT NULL default '0', sdate bigint, start_mm smallint, " +
                         "start_dd smallint, start_yy integer, start_hr smallint, " +
                         "start_min smallint, stime integer, edate bigint, end_mm smallint, " +
                         "end_dd smallint, end_yy integer, end_hr smallint, " +
                         "end_min smallint, etime integer, recurr varchar(20), " +
                         "courseName varchar(30), fb varchar(5), locations text NOT NULL default '', " +
                         "PRIMARY KEY (id)," +
                         "UNIQUE KEY key1 (name,activity_id))");

      //
      //  Guest Restrictions Configuration Table
      //
      stmt.executeUpdate("CREATE TABLE guestres2 (id int auto_increment, " +
                         "activity_id int NOT NULL default '0', " +
                         "name varchar(30), sdate bigint, start_mm smallint, " +
                         "start_dd smallint, start_yy integer, start_hr smallint, " +
                         "start_min smallint, stime integer, edate bigint, end_mm smallint, " +
                         "end_dd smallint, end_yy integer, end_hr smallint, end_min smallint, " +
                         "etime integer, recurr varchar(20), num_guests smallint, " +
                         "courseName varchar(30), fb varchar(5), color varchar(24), " +
                         "per varchar(8), locations text NOT NULL default '', " +
                         "PRIMARY KEY (id), " +
                         "UNIQUE KEY key1 (name, activity_id))");
/*
                         "guest1 varchar(20), guest2 varchar(20), guest3 varchar(20), guest4 varchar(20),  " +
                         "guest5 varchar(20), guest6 varchar(20), guest7 varchar(20), guest8 varchar(20),  " +
                         "guest9 varchar(20), guest10 varchar(20), guest11 varchar(20), guest12 varchar(20), " +
                         "guest13 varchar(20), guest14 varchar(20), guest15 varchar(20), guest16 varchar(20), " +
                         "guest17 varchar(20), guest18 varchar(20), guest19 varchar(20), guest20 varchar(20), " +
                         "guest21 varchar(20), guest22 varchar(20), guest23 varchar(20), guest24 varchar(20), " +
                         "guest25 varchar(20), guest26 varchar(20), guest27 varchar(20), guest28 varchar(20), " +
                         "guest29 varchar(20), guest30 varchar(20), guest31 varchar(20), guest32 varchar(20), " +
                         "guest33 varchar(20), guest34 varchar(20), guest35 varchar(20), guest36 varchar(20), " +
*/

      //
      //  Guest Restrictions Guest Types Table
      //
      stmt.executeUpdate("CREATE TABLE guestres2_gtypes (" +
                         "id int(11) NOT NULL auto_increment, " +
                         "guestres_id int(11) default NULL, " +
                         "guest_type varchar(20) NOT NULL default '', " +
                         "PRIMARY KEY  (id), " +
                         "UNIQUE KEY key1 (guestres_id,guest_type) " +
                         ") ENGINE=MyISAM");


      //
      //  Restriction Suspension Table
      //
      stmt.executeUpdate("CREATE TABLE rest_suspend (" +
                         "id int(11) NOT NULL auto_increment," +
                         "mrest_id int(11) default NULL," +
                         "grest_id int(11) default NULL," +
                         "courseName varchar(30) NOT NULL default ''," +
                         "sdate int(11) NOT NULL default '0'," +
                         "edate int(11) NOT NULL default '0'," +
                         "sunday tinyint(4) NOT NULL default '0'," +
                         "monday tinyint(4) NOT NULL default '0'," +
                         "tuesday tinyint(4) NOT NULL default '0'," +
                         "wednesday tinyint(4) NOT NULL default '0'," +
                         "thursday tinyint(4) NOT NULL default '0'," +
                         "friday tinyint(4) NOT NULL default '0'," +
                         "saturday tinyint(4) NOT NULL default '0'," +
                         "stime smallint(6) NOT NULL default '0'," +
                         "etime smallint(6) NOT NULL default '0'," +
                         "eo_week tinyint(4) NOT NULL default '0'," +
                         "UNIQUE KEY id (id)" +
                         ") ENGINE=MyISAM");
      
      //
      //  Guest Quota Restrictions Configuration Table
      //
      stmt.executeUpdate("CREATE TABLE guestqta4 (" +
                         "id int(11) NOT NULL AUTO_INCREMENT, " +
                         "name varchar(30), activity_id int NOT NULL DEFAULT '0', " +
                         "sdate bigint, start_mm smallint, " +
                         "start_dd smallint, start_yy integer, start_hr smallint, " +
                         "start_min smallint, stime integer, edate bigint, end_mm smallint, " +
                         "end_dd smallint, end_yy integer, end_hr smallint, end_min smallint, " +
                         "etime integer, recurr varchar(20), num_guests integer, " +
                         "courseName varchar(30), fb varchar(5), color varchar(24), " +
                         "per varchar(20), locations text NOT NULL default '', " +
                         "PRIMARY KEY (id), UNIQUE KEY key1 (activity_id, name) )");
/*
                         "guest1 varchar(20), guest2 varchar(20), guest3 varchar(20), guest4 varchar(20),  " +
                         "guest5 varchar(20), guest6 varchar(20), guest7 varchar(20), guest8 varchar(20),  " +
                         "guest9 varchar(20), guest10 varchar(20), guest11 varchar(20), guest12 varchar(20), " +
                         "guest13 varchar(20), guest14 varchar(20), guest15 varchar(20), guest16 varchar(20), " +
                         "guest17 varchar(20), guest18 varchar(20), guest19 varchar(20), guest20 varchar(20), " +
                         "guest21 varchar(20), guest22 varchar(20), guest23 varchar(20), guest24 varchar(20), " +
                         "guest25 varchar(20), guest26 varchar(20), guest27 varchar(20), guest28 varchar(20), " +
                         "guest29 varchar(20), guest30 varchar(20), guest31 varchar(20), guest32 varchar(20), " +
                         "guest33 varchar(20), guest34 varchar(20), guest35 varchar(20), guest36 varchar(20), " +
*/

      //
      //  Guest Quota Restrictions Guest Types Table
      //
      stmt.executeUpdate("CREATE TABLE guestqta4_gtypes (" +
                         "id int(11) NOT NULL auto_increment, " +
                         "guestqta_id int(11) default NULL, " +
                         "guest_type varchar(20) NOT NULL default '', " +
                         "PRIMARY KEY  (id), " +
                         "UNIQUE KEY key1 (guestqta_id,guest_type) " +
                         ") ENGINE=MyISAM");


      //
      //  Member Number Restrictions Configuration Table
      //
      stmt.executeUpdate("CREATE TABLE mnumres2 (id INT AUTO_INCREMENT, name varchar(30), " +
                         "activity_id int(11) NOT NULL DEFAULT '0', sdate bigint, start_mm smallint, " +
                         "start_dd smallint, start_yy integer, start_hr smallint, " +
                         "start_min smallint, stime integer, edate bigint, end_mm smallint, " +
                         "end_dd smallint, end_yy integer, end_hr smallint, end_min smallint, " +
                         "etime integer, recurr varchar(20), num_mems smallint, " +
                         "courseName varchar(30), fb varchar(5), " +
                         "PRIMARY KEY (id), UNIQUE KEY key1 (activity_id, name))");

      //
      //  Double Tee Configuration Table
      //
      stmt.executeUpdate("CREATE TABLE dbltee2 (name varchar(30), sdate bigint, start_mm smallint, " +
                         "start_dd smallint, start_yy integer, start_hr1 smallint, " +
                         "start_min1 smallint, stime1 integer, edate bigint, end_mm smallint, " +
                         "end_dd smallint, end_yy integer, end_hr1 smallint, end_min1 smallint, " +
                         "etime1 integer, start_hr2 smallint, start_min2 smallint, stime2 integer, " +
                         "end_hr2 smallint, end_min2 smallint, etime2 integer, recurr varchar(20), " +
                         "courseName varchar(30))");

      //
      //  Current tee times
      //
      
      stmt.executeUpdate("CREATE TABLE teecurr2 (teecurr_id int auto_increment, date bigint, mm smallint, dd smallint, " +
                         "yy integer, day varchar(10), hr smallint, min smallint, time integer, " +
                         "event varchar(30), event_color varchar(24), restriction varchar(30), " +
                         "rest_color varchar(24), player1 varchar(43), player2 varchar(43), " + 
                         "player3 varchar(43), player4 varchar(43), username1 varchar(15), " +
                         "username2 varchar(15), username3 varchar(15), username4 varchar(15), " +
                         "p1cw varchar(4), p2cw varchar(4), p3cw varchar(4), p4cw varchar(4), " +
                         "first smallint, in_use smallint, in_use_by varchar(15), event_type integer, " +
                         "hndcp1 double(3,1), hndcp2 double(3,1), hndcp3 double(3,1), hndcp4 double(3,1), " +
                         "show1 smallint, show2 smallint, show3 smallint, show4 smallint, " +
                         "fb smallint, player5 varchar(43), username5 varchar(15), p5cw varchar(4), " +
                         "hndcp5 double(3,1), show5 smallint, notes varchar(254), hideNotes smallint, " +
                         "lottery varchar(30), courseName varchar(30), blocker varchar(30), " +
                         "proNew integer, proMod integer, memNew integer, memMod integer, rest5 varchar(30), " +
                         "rest5_color varchar(24), mNum1 varchar(10), mNum2 varchar(10), mNum3 varchar(10), " +
                         "mNum4 varchar(10), mNum5 varchar(10), lottery_color varchar(24), userg1 varchar(15), " +
                         "userg2 varchar(15), userg3 varchar(15), userg4 varchar(15), userg5 varchar(15), " +
                         "hotelNew integer, hotelMod integer, orig_by varchar(15), conf varchar(15), " +
                         "p91 smallint, p92 smallint, p93 smallint, p94 smallint, p95 smallint, " +
                         "pos1 smallint, pos2 smallint, pos3 smallint, pos4 smallint, pos5 smallint, " +
                         "hole varchar(4), " +
                         "auto_blocked tinyint NOT NULL default '0', " +
                         "pace_status_id int NOT NULL default '0', " +
                         "custom_disp1 varchar(10) NOT NULL default '', " +
                         "custom_disp2 varchar(10) NOT NULL default '', " +
                         "custom_disp3 varchar(10) NOT NULL default '', " +
                         "custom_disp4 varchar(10) NOT NULL default '', " +
                         "custom_disp5 varchar(10) NOT NULL default '', " +
                         "custom_string varchar(10) NOT NULL default '', " +
                         "custom_int int NOT NULL default '0', " +
                         "create_date datetime NOT NULL default '0000-00-00 00:00:00', " + 
                         "last_mod_date datetime NOT NULL default '0000-00-00 00:00:00', " +
                         "lottery_email int NOT NULL default '0', " +
                         "tflag1 varchar(9) NOT NULL default '', " +
                         "tflag2 varchar(9) NOT NULL default '', " +
                         "tflag3 varchar(9) NOT NULL default '', " +
                         "tflag4 varchar(9) NOT NULL default '', " +
                         "tflag5 varchar(9) NOT NULL default '', " +
                         "hideit tinyint NOT NULL default 0, " +
                         "guest_id1 int(11) NOT NULL default '0', " +
                         "guest_id2 int(11) NOT NULL default '0', " +
                         "guest_id3 int(11) NOT NULL default '0', " +
                         "guest_id4 int(11) NOT NULL default '0', " +
                         "guest_id5 int(11) NOT NULL default '0', " +
                         "orig1 varchar(15) NOT NULL default '', " +
                         "orig2 varchar(15) NOT NULL default '', " +
                         "orig3 varchar(15) NOT NULL default '', " +
                         "orig4 varchar(15) NOT NULL default '', " +
                         "orig5 varchar(15) NOT NULL default '', " +
                         "PRIMARY KEY (teecurr_id), " +
                         "UNIQUE KEY NoDup (date,time,fb,courseName), " +
                         "KEY date (date), " +
                         "KEY time (time), " +
                         "KEY fb (fb), " +
                         "KEY courseName (courseName(6)), " +
                         "KEY in_use (in_use), " +
                         "KEY blocker (blocker(8)), " +
                         "KEY lottery (lottery(8)), " +
                         "KEY mm (mm), " +
                         "KEY dd (dd), " +
                         "KEY yy (yy), " +
                         "KEY player1 (player1(10)), " +
                         "KEY player2 (player2(10)), " +
                         "KEY player3 (player3(10)), " +
                         "KEY player4 (player4(10)), " +
                         "KEY player5 (player5(10)), " +
                         "KEY username1 (username1(8)), " +
                         "KEY username2 (username2(8)), " +
                         "KEY username3 (username3(8)), " +
                         "KEY username4 (username4(8)), " +
                         "KEY username5 (username5(8)), " +
                         "KEY pace_status_id (pace_status_id) " +
                         ") ENGINE=MyISAM");
      
           
                         /*"index ind1 (date, time, fb, courseName), index ind2 (in_use), " +
                         "index ind3 (date, courseName), " +
                         "index ind4 (date, username1, username2, username3, username4, username5), " +
                         "index ind5 (date, yy, username1, username2, username3, username4, username5), " +
                         "index ind6 (date, mm, yy, username1, username2, username3, username4, username5))");
                          **/
                     // leave index at end (for searches)

      //
      //  Past tee times - occupied
      //
      stmt.executeUpdate("CREATE TABLE teepast2 (teepast_id int auto_increment, teecurr_id int, " +
                         "date bigint, mm smallint, dd smallint, " +
                         "yy integer, day varchar(10), hr smallint, min smallint, time integer, " +
                         "event varchar(30), event_color varchar(24), restriction varchar(30), " +
                         "rest_color varchar(24), player1 varchar(43), player2 varchar(43), " +
                         "player3 varchar(43), player4 varchar(43), username1 varchar(15), " +
                         "username2 varchar(15), username3 varchar(15), username4 varchar(15), " +
                         "p1cw varchar(4), p2cw varchar(4), p3cw varchar(4), p4cw varchar(4), " +
                         "show1 smallint, show2 smallint, show3 smallint, show4 smallint, " +
                         "fb smallint, player5 varchar(43), username5 varchar(15), p5cw varchar(4), " +
                         "show5 smallint, courseName varchar(30), " +
                         "proNew integer, proMod integer, memNew integer, memMod integer, " +
                         "mNum1 varchar(10), mNum2 varchar(10), mNum3 varchar(10), " +
                         "mNum4 varchar(10), mNum5 varchar(10), userg1 varchar(15), userg2 varchar(15), " +
                         "userg3 varchar(15), userg4 varchar(15), userg5 varchar(15), " +
                         "hotelNew integer, hotelMod integer, orig_by varchar(15), conf varchar(15), " +
                         "notes varchar(254), " +
                         "p91 smallint, p92 smallint, p93 smallint, p94 smallint, p95 smallint, pace_status_id int, " +
                         "create_date datetime NOT NULL default '0000-00-00 00:00:00', " + 
                         "last_mod_date datetime NOT NULL default '0000-00-00 00:00:00', " +
                         "custom_string varchar(10) NOT NULL default '', " +
                         "custom_int int NOT NULL default '0', " +
                         "pos1 tinyint NOT NULL default '0', " +
                         "pos2 tinyint NOT NULL default '0', " +
                         "pos3 tinyint NOT NULL default '0', " +
                         "pos4 tinyint NOT NULL default '0', " +
                         "pos5 tinyint NOT NULL default '0', " +
                         "mship1 varchar(30) NOT NULL, " +
                         "mship2 varchar(30) NOT NULL, " +
                         "mship3 varchar(30) NOT NULL, " +
                         "mship4 varchar(30) NOT NULL, " +
                         "mship5 varchar(30) NOT NULL, " +
                         "mtype1 varchar(30) NOT NULL, " +
                         "mtype2 varchar(30) NOT NULL, " +
                         "mtype3 varchar(30) NOT NULL, " +
                         "mtype4 varchar(30) NOT NULL, " +
                         "mtype5 varchar(30) NOT NULL, " +
                         "gtype1 varchar(20) NOT NULL, " +
                         "gtype2 varchar(20) NOT NULL, " +
                         "gtype3 varchar(20) NOT NULL, " +
                         "gtype4 varchar(20) NOT NULL, " +
                         "gtype5 varchar(20) NOT NULL, " +
                         "grev1 tinyint(4) NOT NULL default '0', " +
                         "grev2 tinyint(4) NOT NULL default '0', " +
                         "grev3 tinyint(4) NOT NULL default '0', " +
                         "grev4 tinyint(4) NOT NULL default '0', " +
                         "grev5 tinyint(4) NOT NULL default '0', " +
                         "guest_id1 int(11) NOT NULL default '0', " +
                         "guest_id2 int(11) NOT NULL default '0', " +
                         "guest_id3 int(11) NOT NULL default '0', " +
                         "guest_id4 int(11) NOT NULL default '0', " +
                         "guest_id5 int(11) NOT NULL default '0', " +
                         "PRIMARY KEY (teepast_id), " +
                         "UNIQUE INDEX teecurr_id (teecurr_id), " +
                         "INDEX pace_status_id (pace_status_id), " +
                         "INDEX date (date), " +
                         "INDEX time (time), " +
                         "INDEX fb (fb), " +
                         "INDEX courseName (courseName), " +
                         "INDEX mm (mm), " +
                         "INDEX yy (yy), " +
                         "INDEX player1 (player1), " +
                         "INDEX player2 (player2), " +
                         "INDEX player3 (player3), " +
                         "INDEX player4 (player4), " +
                         "INDEX player5 (player5), " +
                         "INDEX username1 (username1), " +
                         "INDEX username2 (username2), " +
                         "INDEX username3 (username3), " +
                         "INDEX username4 (username4), " +
                         "INDEX username5 (username5))");
              
                        // "index ind1 (date, time, fb, courseName), " +
                         //"index ind2 (date, courseName), " +
                         //"index ind3 (date, username1, username2, username3, username4, username5), " +
                        // "index ind4 (date, yy, username1, username2, username3, username4, username5), " +
                         //"index ind5 (date, mm, yy, username1, username2, username3, username4, username5))");
                     // leave index at end (for searches)
                            
      //
      //  Past tee times - empty (used for reports and old tee sheets)    !!!!!!! see also SystemUtils.moveTee !!!!!!!!!!!!!
      //
      stmt.executeUpdate("CREATE TABLE teepastempty (date bigint, mm smallint, dd smallint, " +
                         "yy integer, day varchar(10), hr smallint, min smallint, time integer, " +
                         "event varchar(30), event_color varchar(24), restriction varchar(30), " +
                         "rest_color varchar(24), fb smallint, courseName varchar(30), " +
                         "proNew integer, proMod integer, memNew integer, memMod integer, " +
                         "hotelNew integer, hotelMod integer, orig_by varchar(15), conf varchar(15), " +
                         "index ind1 (date, time, fb, courseName), " +
                         "index ind2 (date, courseName))");
                     // leave index at end (for searches)

      //
      //    Partner List
      //
      stmt.executeUpdate("CREATE TABLE buddy (username varchar(15), " +
                         "activity_id int(11) NOT NULL default '0', buddy1 varchar(43), " +
                         "buddy2 varchar(43), buddy3 varchar(43), buddy4 varchar(43), " +
                         "buddy5 varchar(43), buddy6 varchar(43), buddy7 varchar(43), " +
                         "buddy8 varchar(43), buddy9 varchar(43), buddy10 varchar(43), " +
                         "buddy11 varchar(43), buddy12 varchar(43), buddy13 varchar(43), " +
                         "buddy14 varchar(43), buddy15 varchar(43), buddy16 varchar(43), " +
                         "buddy17 varchar(43), buddy18 varchar(43), buddy19 varchar(43), " +
                         "buddy20 varchar(43), buddy21 varchar(43), buddy22 varchar(43), " +
                         "buddy23 varchar(43), buddy24 varchar(43), buddy25 varchar(43), " +
                         "b1cw varchar(4), b2cw varchar(4), b3cw varchar(4), b4cw varchar(4), " +
                         "b5cw varchar(4), b6cw varchar(4), b7cw varchar(4), b8cw varchar(4), " +
                         "b9cw varchar(4), b10cw varchar(4), b11cw varchar(4), b12cw varchar(4), " +
                         "b13cw varchar(4), b14cw varchar(4), b15cw varchar(4), b16cw varchar(4), " +
                         "b17cw varchar(4), b18cw varchar(4), b19cw varchar(4), b20cw varchar(4), " +
                         "b21cw varchar(4), b22cw varchar(4), b23cw varchar(4), b24cw varchar(4), " +
                         "b25cw varchar(4), user1 varchar(15), " +
                         "user2 varchar(15), user3 varchar(15), user4 varchar(15), " +
                         "user5 varchar(15), user6 varchar(15), user7 varchar(15), " +
                         "user8 varchar(15), user9 varchar(15), user10 varchar(15), " +
                         "user11 varchar(15), user12 varchar(15), user13 varchar(15), " +
                         "user14 varchar(15), user15 varchar(15), user16 varchar(15), " +
                         "user17 varchar(15), user18 varchar(15), user19 varchar(15), " +
                         "user20 varchar(15), user21 varchar(15), user22 varchar(15), " +
                         "user23 varchar(15), user24 varchar(15), user25 varchar(15), " +
                         "index ind1 (username))");
                     // leave index at end (for searches)

      //
      //  Partner List
      //
      stmt.executeUpdate("CREATE TABLE partner (" +
              "id int(11) NOT NULL auto_increment, " +
              "user_id varchar(15) NOT NULL default '', " +
              "activity_id int(11) NOT NULL default '0', " +
              "partner_id varchar(15) NOT NULL default '', " +
              "priority int(11) NOT NULL default '1', " +
              "PRIMARY KEY  (id), " +
              "UNIQUE KEY user_id (user_id,activity_id,partner_id)" +
              ") ENGINE=MyISAM DEFAULT CHARSET=latin1");

      //
      //  Lottery Configuration Table
      //
      stmt.executeUpdate("CREATE TABLE lottery3 (name varchar(40), sdate bigint, start_mm smallint, " +
                         "start_dd smallint, start_yy integer, start_hr smallint, " +
                         "start_min smallint, stime integer, edate bigint, end_mm smallint, " +
                         "end_dd smallint, end_yy integer, end_hr smallint, " +
                         "end_min smallint, etime integer, recurr varchar(20), " +
                         "color varchar(24), courseName varchar(30), fb varchar(5), " +
                         "sdays integer, sdtime integer, sd_hr smallint, sd_min smallint, " +
                         "edays integer, edtime integer, ed_hr smallint, ed_min smallint, " +
                         "pdays integer, ptime integer, p_hr smallint, p_min smallint, " +
                         "type varchar(10), adays integer, wdpts integer, wepts integer, evpts integer, gpts integer, " +
                         "nopts integer, selection integer, guest integer, slots integer, pref varchar(3), " +
                         "approve varchar(3), members integer, players integer, " +
                         "minsbefore smallint NOT NULL default '120', " +
                         "minsafter smallint NOT NULL default '120', " +
                         "allowmins tinyint NOT NULL default '0', " +
                         "UNIQUE KEY name (name), " +
                         "KEY dates (sdate,edate))");

      //
      //  Individual Lottery Requests
      //
      stmt.executeUpdate("CREATE TABLE lreqs3 (name varchar(40), date bigint, mm smallint, dd smallint, " +
                         "yy integer, day varchar(10), hr smallint, min smallint, time integer, " +
                         "minsbefore integer, minsafter integer, " +
                         "player1 varchar(43), player2 varchar(43), player3 varchar(43), player4 varchar(43), " +
                         "player5 varchar(43), player6 varchar(43), player7 varchar(43), player8 varchar(43), " +
                         "player9 varchar(43), player10 varchar(43), player11 varchar(43), player12 varchar(43), " +
                         "player13 varchar(43), player14 varchar(43), player15 varchar(43), player16 varchar(43), " +
                         "player17 varchar(43), player18 varchar(43), player19 varchar(43), player20 varchar(43), " +
                         "player21 varchar(43), player22 varchar(43), player23 varchar(43), player24 varchar(43), " +
                         "player25 varchar(43), user1 varchar(15), user2 varchar(15), user3 varchar(15), user4 varchar(15), " +
                         "user5 varchar(15), user6 varchar(15), user7 varchar(15), user8 varchar(15), " +
                         "user9 varchar(15), user10 varchar(15), user11 varchar(15), user12 varchar(15), " +
                         "user13 varchar(15), user14 varchar(15), user15 varchar(15), user16 varchar(15), " +
                         "user17 varchar(15), user18 varchar(15), user19 varchar(15), user20 varchar(15), " +
                         "user21 varchar(15), user22 varchar(15), user23 varchar(15), user24 varchar(15), " +
                         "user25 varchar(15), p1cw varchar(4), p2cw varchar(4), p3cw varchar(4), p4cw varchar(4), " +
                         "p5cw varchar(4), p6cw varchar(4), p7cw varchar(4), p8cw varchar(4), " +
                         "p9cw varchar(4), p10cw varchar(4), p11cw varchar(4), p12cw varchar(4), " +
                         "p13cw varchar(4), p14cw varchar(4), p15cw varchar(4), p16cw varchar(4), " +
                         "p17cw varchar(4), p18cw varchar(4), p19cw varchar(4), p20cw varchar(4), " +
                         "p21cw varchar(4), p22cw varchar(4), p23cw varchar(4), p24cw varchar(4), " +
                         "p25cw varchar(4), notes varchar(254), hideNotes smallint, fb smallint, " +
                         "courseName varchar(30), proNew integer, proMod integer, memNew integer, memMod integer, " +
                         "id bigint, in_use smallint, in_use_by varchar(15), " +
                         "groups integer, type varchar(10), state integer, atime1 integer, atime2 integer, " +
                         "atime3 integer, atime4 integer, atime5 integer, afb smallint, p5 varchar(3), " +
                         "players integer, userg1 varchar(15), userg2 varchar(15), userg3 varchar(15), " +
                         "userg4 varchar(15), userg5 varchar(15), userg6 varchar(15), userg7 varchar(15), " +
                         "userg8 varchar(15), userg9 varchar(15), userg10 varchar(15), userg11 varchar(15), " +
                         "userg12 varchar(15), userg13 varchar(15), userg14 varchar(15), userg15 varchar(15), " +
                         "userg16 varchar(15), userg17 varchar(15), userg18 varchar(15), userg19 varchar(15), " +
                         "userg20 varchar(15), userg21 varchar(15), userg22 varchar(15), userg23 varchar(15), " +
                         "userg24 varchar(15), userg25 varchar(15), weight integer, orig_by varchar(15), " +
                         "p91 smallint, p92 smallint, p93 smallint, p94 smallint, p95 smallint, " +
                         "p96 smallint, p97 smallint, p98 smallint, p99 smallint, p910 smallint, " +
                         "p911 smallint, p912 smallint, p913 smallint, p914 smallint, p915 smallint, " +
                         "p916 smallint, p917 smallint, p918 smallint, p919 smallint, p920 smallint, " +
                         "p921 smallint, p922 smallint, p923 smallint, p924 smallint, p925 smallint, " +
                         "checkothers smallint, afb2 smallint, afb3 smallint, afb4 smallint, afb5 smallint, " +
                         "courseReq varchar(30) NOT NULL, fail_code tinyint NOT NULL default '0', " +
                         "guest_id1 int(11) NOT NULL default '0', guest_id2 int(11) NOT NULL default '0', " +
                         "guest_id3 int(11) NOT NULL default '0', guest_id4 int(11) NOT NULL default '0', " +
                         "guest_id5 int(11) NOT NULL default '0', guest_id6 int(11) NOT NULL default '0', " +
                         "guest_id7 int(11) NOT NULL default '0', guest_id8 int(11) NOT NULL default '0', " +
                         "guest_id9 int(11) NOT NULL default '0', guest_id10 int(11) NOT NULL default '0', " +
                         "guest_id11 int(11) NOT NULL default '0', guest_id12 int(11) NOT NULL default '0', " +
                         "guest_id13 int(11) NOT NULL default '0', guest_id14 int(11) NOT NULL default '0', " +
                         "guest_id15 int(11) NOT NULL default '0', guest_id16 int(11) NOT NULL default '0', " +
                         "guest_id17 int(11) NOT NULL default '0', guest_id18 int(11) NOT NULL default '0', " +
                         "guest_id19 int(11) NOT NULL default '0', guest_id20 int(11) NOT NULL default '0', " +
                         "guest_id21 int(11) NOT NULL default '0', guest_id22 int(11) NOT NULL default '0', " +
                         "guest_id23 int(11) NOT NULL default '0', guest_id24 int(11) NOT NULL default '0', " +
                         "guest_id25 int(11) NOT NULL default '0', " +
                         "UNIQUE KEY id (id), " +
                         "KEY name (name), " +
                         "KEY date (date), " +
                         "KEY in_use (in_use) " +
                         ")");

      //
      //  Active Lotteries - waiting to be processed/approved
      //
      stmt.executeUpdate("CREATE TABLE actlott3 (name varchar(40), date bigint, pdate bigint, ptime integer, " +
                         "courseName varchar(30))");

      //
      //  Lottery assinged table for determining weights 
      //
      stmt.executeUpdate("CREATE TABLE lassigns5 (username varchar(15), lname varchar(40), date bigint, mins integer, " +
                         "index ind1 (username), index ind2 (date))");

      //
      //  Hotel User Table
      //
      stmt.executeUpdate("CREATE TABLE hotel3 (username varchar(15), password varchar(10), " +
                         "name_last varchar(20), name_first varchar(20), name_mi char, " +
                         "days1 smallint, days2 smallint, days3 smallint, days4 smallint, " +  
                         "days5 smallint, days6 smallint, days7 smallint, " + /*
                         "guest1 varchar(20), guest2 varchar(20), guest3 varchar(20), guest4 varchar(20), " +
                         "guest5 varchar(20), guest6 varchar(20), guest7 varchar(20), guest8 varchar(20), " +
                         "guest9 varchar(20), guest10 varchar(20), guest11 varchar(20), guest12 varchar(20), " +
                         "guest13 varchar(20), guest14 varchar(20), guest15 varchar(20), guest16 varchar(20), " +
                         "guest17 varchar(20), guest18 varchar(20), guest19 varchar(20), guest20 varchar(20), " +
                         "guest21 varchar(20), guest22 varchar(20), guest23 varchar(20), guest24 varchar(20), " +
                         "guest25 varchar(20), guest26 varchar(20), guest27 varchar(20), guest28 varchar(20), " +
                         "guest29 varchar(20), guest30 varchar(20), guest31 varchar(20), guest32 varchar(20), " +
                         "guest33 varchar(20), guest34 varchar(20), guest35 varchar(20), guest36 varchar(20), " + */
                         "message varchar(10), " +
                         "UNIQUE KEY ind1 (username))");

      //
      // Table for binding hotel users to the guest types they have permission to access
      //
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS hotel3_gtypes (" +
                         "id int(11) NOT NULL auto_increment," +
                         "username varchar(15) NOT NULL default ''," +
                         "guest_type varchar(20) NOT NULL default ''," +
                         "PRIMARY KEY (id)," +
                         "UNIQUE KEY key1 (username,guest_type))");

      //
      //  Email Distribution Lists for Members
      //
      stmt.executeUpdate("CREATE TABLE dist4 (name varchar(40), owner varchar(15), " +
                         "user1 varchar(15), user2 varchar(15), user3 varchar(15), user4 varchar(15), " +
                         "user5 varchar(15), user6 varchar(15), user7 varchar(15), " +
                         "user8 varchar(15), user9 varchar(15), user10 varchar(15), " +
                         "user11 varchar(15), user12 varchar(15), user13 varchar(15), " +
                         "user14 varchar(15), user15 varchar(15), user16 varchar(15), " +
                         "user17 varchar(15), user18 varchar(15), user19 varchar(15), " +
                         "user20 varchar(15), user21 varchar(15), user22 varchar(15), " +
                         "user23 varchar(15), user24 varchar(15), user25 varchar(15), " +
                         "user26 varchar(15), user27 varchar(15), user28 varchar(15), " +
                         "user29 varchar(15), user30 varchar(15))");

      //
      //  Email Distribution Lists for Proshop
      //
      stmt.executeUpdate("CREATE TABLE dist4p (name varchar(40), owner varchar(15), " +
                         "user1 varchar(15), user2 varchar(15), user3 varchar(15), user4 varchar(15), " +
                         "user5 varchar(15), user6 varchar(15), user7 varchar(15), " +
                         "user8 varchar(15), user9 varchar(15), user10 varchar(15), " +
                         "user11 varchar(15), user12 varchar(15), user13 varchar(15), " +
                         "user14 varchar(15), user15 varchar(15), user16 varchar(15), " +
                         "user17 varchar(15), user18 varchar(15), user19 varchar(15), " +
                         "user20 varchar(15), user21 varchar(15), user22 varchar(15), " +
                         "user23 varchar(15), user24 varchar(15), user25 varchar(15), " +
                         "user26 varchar(15), user27 varchar(15), user28 varchar(15), " +
                         "user29 varchar(15), user30 varchar(15), user31 varchar(15), " +
                         "user32 varchar(15), user33 varchar(15), user34 varchar(15), " +
                         "user35 varchar(15), user36 varchar(15), user37 varchar(15), " +
                         "user38 varchar(15), user39 varchar(15), user40 varchar(15), " +
                         "user41 varchar(15), user42 varchar(15), user43 varchar(15), " +
                         "user44 varchar(15), user45 varchar(15), user46 varchar(15), " +
                         "user47 varchar(15), user48 varchar(15), user49 varchar(15), " +
                         "user50 varchar(15), user51 varchar(15), user52 varchar(15), " +
                         "user53 varchar(15), user54 varchar(15), user55 varchar(15), " +
                         "user56 varchar(15), user57 varchar(15), user58 varchar(15), " +
                         "user59 varchar(15), user60 varchar(15), user61 varchar(15), " +
                         "user62 varchar(15), user63 varchar(15), user64 varchar(15), " +
                         "user65 varchar(15), user66 varchar(15), user67 varchar(15), " +
                         "user68 varchar(15), user69 varchar(15), user70 varchar(15), " +
                         "user71 varchar(15), user72 varchar(15), user73 varchar(15), " +
                         "user74 varchar(15), user75 varchar(15), user76 varchar(15), " +
                         "user77 varchar(15), user78 varchar(15), user79 varchar(15), " +
                         "user80 varchar(15), user81 varchar(15), user82 varchar(15), " +
                         "user83 varchar(15), user84 varchar(15), user85 varchar(15), " +
                         "user86 varchar(15), user87 varchar(15), user88 varchar(15), " +
                         "user89 varchar(15), user90 varchar(15), user91 varchar(15), " +
                         "user92 varchar(15), user93 varchar(15), user94 varchar(15), " +
                         "user95 varchar(15), user96 varchar(15), user97 varchar(15), " +
                         "user98 varchar(15), user99 varchar(15), user100 varchar(15))");

      //
      //  List of Members and their guests in event signup (for alphabeticla listing - held here temporarily)
      //
      stmt.executeUpdate("CREATE TABLE eventa4 (name varchar(30), p1 char(60), " +
                         "p2 char(60), p3 char(60), p4 char(60), p5 char(60))");

      //
      //  List of Members and their guests on a tee sheet (for alphabeticla listing - held here temporarily)
      //
      stmt.executeUpdate("CREATE TABLE teereport4 (lname varchar(20), fname varchar(20), mi char, " +
                         "username varchar(15), tmode varchar(4), mNum varchar(10), checkIn smallint, " +
                         "course varchar(30), time integer, fb char(1))");

      //
      //  Lesson Book tables (Ver 5)
      //
      stmt.executeUpdate("CREATE TABLE lessonpro5 (lname varchar(20), fname varchar(20), mi char, " +
                         "suffix varchar(4), id integer, active smallint, email1 varchar(50), email2 varchar(50), " +
                         "advlimit integer, canlimit integer, canpolicy varchar(254)," +
                         "ical1 tinyint NOT NULL default '0', " +
                         "ical2 tinyint NOT NULL default '0', " +
                         "activity_id int NOT NULL default '0', " +
                         "index ind1 (id))");
                     // leave index at end (for searches)

      stmt.executeUpdate("CREATE TABLE lessonbook5 (proid integer, " +
                         "activity_id int NOT NULL default '0', " +
                         "ltname varchar(40), lgname varchar(40), " +
                         "date bigint, time integer, block smallint, memname varchar(43), " +
                         "memid varchar(15), num integer, length integer, billed smallint, in_use smallint, " +
                         "ltype varchar(40), phone1 varchar(24), phone2 varchar(24), color varchar(24), " +
                         "notes varchar(254), dayNum smallint, " +
                         "recid integer NOT NULL AUTO_INCREMENT, " +
                         "sheet_activity_id int(11) NOT NULL default 0, " +
                         "UNIQUE KEY recid (recid), " +
                         "KEY date (date), " +
                         "KEY in_use (in_use))");
                    
      stmt.executeUpdate("CREATE TABLE lessontype5 (proid integer, " +
                         "activity_id int NOT NULL default '0', " +
                         "ltname varchar(40), length integer, " +
                         "cost varchar(20), descript varchar(254), " +
                         "locations TEXT NOT NULL, " +
                         "index ind1 (proid), index ind2 (proid, ltname))");
                     // leave index at end (for searches)

      stmt.executeUpdate("CREATE TABLE lessongrp5 (lesson_id int NOT NULL auto_increment, " +
                         "proid integer, activity_id int NOT NULL default '0', " +
                         "lname varchar(40), date bigint, edate bigint NOT NULL default '0', " +
                         "stime integer, etime integer, max integer, cost varchar(20), " +
                         "color varchar(24), descript varchar(254), " +
                         "sunday tinyint NOT NULL default '0', " +
                         "monday tinyint NOT NULL default '0', " +
                         "tuesday tinyint NOT NULL default '0', " +
                         "wednesday tinyint NOT NULL default '0', " +
                         "thursday tinyint NOT NULL default '0', " +
                         "friday tinyint NOT NULL default '0', " +
                         "saturday tinyint NOT NULL default '0', " +
                         "eo_week tinyint NOT NULL default '0', " +
                         "locations TEXT NOT NULL, " +
                         "clinic tinyint NOT NULL default '0', " +
                         "PRIMARY KEY (lesson_id), " +
                         "index ind1 (proid), index ind2 (proid, date), index ind3 (proid, lname))");
                     // leave index at end (for searches)

      stmt.executeUpdate("CREATE TABLE lgrpsignup5 (" +
                         "lesson_id int NOT NULL default '0', " +
                         "proid integer, lname varchar(40), date bigint, " +
                         "memname varchar(43), memid varchar(15), billed smallint, " +
                         "phone1 varchar(24), phone2 varchar(24), notes varchar(254), " +
                         "index ind1 (proid), index ind2 (proid, lname, date))");
                     // leave index at end (for searches)

      stmt.executeUpdate("CREATE TABLE lessontime5 (proid integer, " +
                         "activity_id int NOT NULL default '0', " +
                         "lname varchar(40), sdate bigint, " +
                         "stime integer, edate bigint, etime integer, mon smallint, " +
                         "tue smallint, wed smallint, thu smallint, fri smallint, sat smallint, " +
                         "sun smallint, color varchar(24), fragment integer, " +
                         "index ind1 (proid), index ind2 (proid, lname), index ind3 (proid, sdate, edate))");
                     // leave index at end (for searches)

      stmt.executeUpdate("CREATE TABLE lessonblock5 (proid integer, " +
                         "activity_id int NOT NULL default '0', " +
                         "lbname varchar(40), sdate bigint, " +
                         "stime integer, edate bigint, etime integer, mon smallint, " +
                         "tue smallint, wed smallint, thu smallint, fri smallint, sat smallint, " +
                         "sun smallint, color varchar(24), fragment integer, " +
                         "index ind1 (proid), index ind2 (proid, lbname), index ind3 (proid, sdate, edate))");
                     // leave index at end (for searches)

      stmt.executeUpdate("CREATE TABLE diary (diary_entry_id integer auto_increment, " +
                         "diary_date date, " +
                         "weather_am tinyint, " +
                         "weather_mid tinyint, " +
                         "weather_pm tinyint, " +
                         "notes text, " +
                         "PRIMARY KEY (diary_entry_id))");

      stmt.executeUpdate("CREATE TABLE diarycc (diary_course_cond_id integer auto_increment, " +
                         "diary_date date, " +
                         "course_name varchar(30), " +
                         "am_condition tinyint, " +
                         "mid_condition tinyint, " +
                         "pm_condition tinyint, " +
                         "PRIMARY KEY (diary_course_cond_id))");

//
//   !!!!!!! see also SystemUtils.doTableUpdate for tmodes definition !!!!!!!!!!!!!!
//
      stmt.executeUpdate("CREATE TABLE tmodes (courseName CHAR(30) NOT NULL, " +
                         "tmodea CHAR(3) NOT NULL, " +
                         "tmode CHAR(20) NOT NULL, " +
                         "PRIMARY KEY (courseName, tmodea));");

      //
      //  Tee Time History 
      //
      //   !!!!!!! see also SystemUtils.updateHist for definition !!!!!!!!!!!!!!
      //
      stmt.executeUpdate("CREATE TABLE teehist (date bigint, " +
                         "day varchar(10), time integer, " +
                         "fb smallint, courseName varchar(30), " +
                         "player1 varchar(43), player2 varchar(43), " +
                         "player3 varchar(43), player4 varchar(43), player5 varchar(43), " +
                         "user varchar(15), mname varchar(50), " +
                         "mdate bigint, sdate varchar(36), type smallint, " +
                         "INDEX ind1 (date, time, fb, courseName))");
                     // leave index at end (for searches)

        //
        // Pace of Play tables
        //
      
        stmt.executeUpdate("CREATE TABLE pace_status (" +
                            "pace_status_id int NOT NULL auto_increment, " +
                            "pace_status_sort int default NULL, " +
                            "pace_status_name varchar(16) default NULL, " +
                            "pace_status_color varchar(16) default NULL, " +
                            "pace_leeway double default NULL, " +
                            "PRIMARY KEY  (pace_status_id) " +
                            ") ENGINE=MyISAM");


        stmt.executeUpdate("INSERT INTO pace_status (pace_status_sort, pace_status_name, pace_status_color, pace_leeway) VALUES " +
                            "(\"1\", \"On Pace\", \"green\", \"0.045\"), " +
                            "(\"2\", \"Falling Behind\", \"yellow\", \"0.09\"), " + 
                            "(\"3\", \"Behind\", \"pink\", \"0.12\")");
        

        stmt.executeUpdate("CREATE TABLE pace_entries (" +
                            "pace_entry_id int(11) NOT NULL auto_increment," +
                            "teecurr_id int(11) default NULL," +
                            "hole_number tinyint(4) default NULL," +
                            "invert tinyint(4) default NULL," +
                            "hole_timestamp time default NULL," +
                            "PRIMARY KEY (pace_entry_id), " +
                            "UNIQUE KEY teecurr_id (teecurr_id, hole_number) " +
                            ") ENGINE=MyISAM"); 

        
        stmt.executeUpdate("CREATE TABLE pace_benchmarks (" +
                            "pace_benchmark_id int(11) NOT NULL auto_increment," +
                            "clubparm_id int(11) default NULL," +
                            "hole_number tinyint(4) default NULL," +
                            "hole_pace int(11) default NULL," +
                            "invert tinyint(4) default NULL," +
                            "PRIMARY KEY (pace_benchmark_id), " +
                            "UNIQUE KEY clubparm_id (clubparm_id, hole_number)" +
                            ") ENGINE=MyISAM");
        

      stmt.executeUpdate("CREATE TABLE mem_notice(mem_notice_id int NOT NULL auto_increment, " +
                         "name varchar(30) NOT NULL, " +
                         "sdate bigint NOT NULL, " +
                         "stime int NOT NULL, " +
                         "edate bigint NOT NULL, " +
                         "etime int NOT NULL, " +
                         "mon smallint NOT NULL, " +
                         "tue smallint NOT NULL, " +
                         "wed smallint NOT NULL, " +
                         "thu smallint NOT NULL, " +
                         "fri smallint NOT NULL, " +
                         "sat smallint NOT NULL, " +
                         "sun smallint NOT NULL, " +
                         "teetime smallint NOT NULL, " +
                         "event smallint NOT NULL, " +
                         "courseName varchar(30) NOT NULL, " +
                         "fb varchar(5) NOT NULL, " +
                         "message text NOT NULL, " +
                         "proside tinyint NOT NULL DEFAULT '0', " +
                         "teesheet tinyint NOT NULL DEFAULT '0', " +
                         "bgColor varchar(24) NOT NULL, " +
                         "PRIMARY KEY (mem_notice_id))");

      
      stmt.executeUpdate("" +
            "CREATE TABLE score_postings (" + 
                "posting_id int(11) NOT NULL auto_increment, " +
                "hdcpNum varchar(15) NOT NULL, " +
                "date date NOT NULL, " +
                "score smallint(4) NOT NULL, " +
                "type varchar(2) NOT NULL, " +
                "hdcpIndex double(3,1) NOT NULL, " +
                "tee_id int NOT NULL default '0', " +
                "PRIMARY KEY (posting_id), " +
                "UNIQUE KEY entry (hdcpNum, date, score, type), " +
                "KEY hdcpNum (hdcpNum) " +
            ") ENGINE=MyISAM;");
            
            
      stmt.executeUpdate("" +
            "CREATE TABLE tees (" +
                "tee_id int(11) NOT NULL auto_increment, " + 
                "course_id int(11) NOT NULL, " +
                "tee_name varchar(24) NOT NULL, " +
                "tee_rating18 double(3,1) NOT NULL, " +
                "tee_slope18 int(11) NOT NULL, " +
                "tee_ratingF9 double(3,1) NOT NULL, " +
                "tee_slopeF9 int(11) NOT NULL, " +
                "tee_ratingB9 double(3,1) NOT NULL, " +
                "tee_slopeB9 int(11) NOT NULL, " +
                "PRIMARY KEY (tee_id) " +
            ") ENGINE=MyISAM;");
            
      
      stmt.executeUpdate("" +
            "CREATE TABLE notifications ( " +                                    
                "notification_id int(11) NOT NULL auto_increment, " +              
                "created_by varchar(32) NOT NULL, " +                              
                "created_datetime datetime NOT NULL, " +                           
                "req_datetime datetime NOT NULL default '0000-00-00 00:00:00', " + 
                "course_id int(11) NOT NULL default '0', " +                       
                "in_use_by varchar(15) NOT NULL default '', " +                    
                "in_use_at datetime NOT NULL default '0000-00-00 00:00:00', " +    
                "hideNotes tinyint(4) NOT NULL default '0', " +                    
                "notes text, " +                                                   
                "converted tinyint(4) NOT NULL default '0', " +                    
                "converted_at datetime NOT NULL default '0000-00-00 00:00:00', " + 
                "converted_by varchar(15) NOT NULL default '', " +                 
                "teecurr_id int(11) NOT NULL default '0', " +                      
                "teepast_id int(11) NOT NULL default '0', " +                      
                "PRIMARY KEY (notification_id) " +                           
            ") ENGINE=MyISAM;");


      stmt.executeUpdate("" +
            "CREATE TABLE notifications_players ( " + 
                 "notification_player_id int(11) NOT NULL auto_increment, " + 
                 "notification_id int(11) NOT NULL default '0', " + 
                 "username varchar(12) NOT NULL default '', " + 
                 "cw varchar(4) NOT NULL default '', " + 
                 "player_name varchar(64) NOT NULL default '', " + 
                 "9hole tinyint(4) NOT NULL default '0', " + 
                 "pos tinyint(4) NOT NULL default '0', " +
                 "userg varchar(12) NOT NULL default '', " +
                 "guest_id int(11) NOT NULL default '0', " +
                 "PRIMARY KEY (notification_player_id), " + 
                 "UNIQUE KEY pos (notification_id, pos) " + 
            ") ENGINE=MyISAM;");
            
      
      stmt.executeUpdate("" +
            "CREATE TABLE hdcp_assoc_num ( " + 
              "hdcp_assoc_num_id int(11) NOT NULL auto_increment, " +
              "assoc_num varchar(8) NOT NULL, " +
              "assoc_name varchar(16) NOT NULL, " +
              "PRIMARY KEY (hdcp_assoc_num_id) " +
            ") ENGINE=MyISAM;");

      
      stmt.executeUpdate("" +
            "CREATE TABLE hdcp_club_num ( " +
             "hdcp_club_num_id int(11) NOT NULL auto_increment, " +
             "club_num varchar(8) NOT NULL, " +
             "club_name varchar(16) NOT NULL, " +
             "PRIMARY KEY (hdcp_club_num_id) " +         
           ") ENGINE=MyISAM;");
            

      stmt.executeUpdate("" +
            "CREATE TABLE custom_sheets (" + 
                "custom_sheet_id int(11) NOT NULL auto_increment," + 
                "name varchar(32) NOT NULL default ''," + 
                "course varchar(30) NOT NULL default ''," + 
                "start_date int(11) NOT NULL default '0'," + 
                "end_date int(11) NOT NULL default '0'," + 
                "sunday tinyint(4) NOT NULL default '0'," + 
                "monday tinyint(4) NOT NULL default '0'," + 
                "tuesday tinyint(4) NOT NULL default '0'," + 
                "wednesday tinyint(4) NOT NULL default '0'," + 
                "thursday tinyint(4) NOT NULL default '0'," + 
                "friday tinyint(4) NOT NULL default '0'," + 
                "saturday tinyint(4) NOT NULL default '0'," + 
                "stime smallint(6) NOT NULL default '0'," + 
                "etime smallint(6) NOT NULL default '0'," + 
                "alt tinyint(4) NOT NULL default '0'," + 
                "betwn tinyint(4) NOT NULL default '0'," + 
                "eo_week tinyint(4) NOT NULL default '0'," + 
                "PRIMARY KEY  (custom_sheet_id)," + 
                "UNIQUE KEY name (name)" + 
            ") ENGINE=MyISAM");


      stmt.executeUpdate("" +
            "CREATE TABLE custom_tee_times (" + 
                "custom_tee_time_id int(11) NOT NULL auto_increment," + 
                "custom_sheet_id int(11) NOT NULL default '0'," + 
                "time int(11) NOT NULL default '0'," + 
                "fb smallint(6) NOT NULL default '0'," + 
                "PRIMARY KEY  (custom_tee_time_id)," + 
                "UNIQUE KEY ind1 (custom_sheet_id,time,fb)" + 
            ") ENGINE=MyISAM");
      

      stmt.executeUpdate("CREATE TABLE wait_list (" + 
                            "wait_list_id int(11) NOT NULL auto_increment, " +
                            "name varchar(32) NOT NULL default '', " + 
                            "sdatetime datetime default '0000-00-00 00:00:00', " + 
                            "edatetime datetime default '0000-00-00 00:00:00', " + 
                            "cutoff_days tinyint(4) NOT NULL default '0', " + 
                            "cutoff_time smallint(6) NOT NULL default '0', " + 
                            "sunday tinyint(4) NOT NULL default '0', " + 
                            "monday tinyint(4) NOT NULL default '0', " + 
                            "tuesday tinyint(4) NOT NULL default '0', " + 
                            "wednesday tinyint(4) NOT NULL default '0', " + 
                            "thursday tinyint(4) NOT NULL default '0', " + 
                            "friday tinyint(4) NOT NULL default '0', " + 
                            "saturday tinyint(4) NOT NULL default '0', " + 
                            "course varchar(32) NOT NULL default '0', " + 
                            "member_view_teesheet tinyint(4) NOT NULL default '0', " + 
                            "member_access tinyint(4) NOT NULL default '0', " + 
                            "member_view tinyint(4) NOT NULL default '0', " + 
                            "max_team_size tinyint(4) NOT NULL default '0', " + 
                            "max_list_size tinyint(4) NOT NULL default '0', " + 
                            "allow_guests tinyint(4) NOT NULL default '0', " + 
                            "allow_x tinyint(4) NOT NULL default '0', " + 
                            "enabled tinyint(4) NOT NULL default '0', " + 
                            "notice text NOT NULL, " + 
                            "color varchar(24) NOT NULL default '', " + 
                            "PRIMARY KEY  (wait_list_id), " + 
                            "UNIQUE KEY name (name), " + 
                            "KEY course_id (course) " + 
                         ") ENGINE=MyISAM DEFAULT CHARSET=latin1");
            

      stmt.executeUpdate("CREATE TABLE wait_list_signups ( " + 
                            "wait_list_signup_id int(11) NOT NULL auto_increment, " + 
                            "wait_list_id int(11) default NULL, " + 
                            "created_by varchar(32) NOT NULL default '', " + 
                            "created_datetime datetime NOT NULL default '0000-00-00 00:00:00', " + 
                            "date date NOT NULL default '0000-00-00', " + 
                            "ok_stime smallint(6) NOT NULL default '0', " + 
                            "ok_etime smallint(6) NOT NULL default '0', " + 
                            "in_use_by varchar(15) NOT NULL default '', " + 
                            "in_use_at datetime NOT NULL default '0000-00-00 00:00:00', " + 
                            "hideNotes tinyint(4) NOT NULL default '0', " + 
                            "notes text, " + 
                            "converted tinyint(4) NOT NULL default '0', " + 
                            "converted_at datetime NOT NULL default '0000-00-00 00:00:00', " + 
                            "converted_by varchar(15) NOT NULL default '', " + 
                            "teecurr_id int(11) NOT NULL default '0', " + 
                            "teepast_id int(11) NOT NULL default '0', " + 
                            "PRIMARY KEY  (wait_list_signup_id) " + 
                         ") ENGINE=MyISAM DEFAULT CHARSET=latin1");
        
        
      stmt.executeUpdate("CREATE TABLE wait_list_signups_players ( " + 
                            "wait_list_signup_player_id int(11) NOT NULL auto_increment, " + 
                            "wait_list_signup_id int(11) NOT NULL default '0', " + 
                            "username varchar(12) NOT NULL default '', " +
                            "cw varchar(4) NOT NULL default '', " + 
                            "player_name varchar(64) NOT NULL default '', " + 
                            "9hole tinyint(4) NOT NULL default '0', " + 
                            "pos tinyint(4) NOT NULL default '0', " +
                            "guest_id int(11) NOT NULL default '0', " +
                            "PRIMARY KEY  (wait_list_signup_player_id), " + 
                            "UNIQUE KEY pos (wait_list_signup_id,pos) " + 
                         ") ENGINE=MyISAM DEFAULT CHARSET=latin1");
            
      
      
      //
      //   POS History Table - for POS Reports
      //
      stmt.executeUpdate("CREATE TABLE pos_hist ( " +
                          "pos_hist_id int(11) NOT NULL auto_increment, " +
                          "date bigint NOT NULL DEFAULT '0', " +
                          "time int NOT NULL DEFAULT '0', " +
                          "course varchar(30) NOT NULL default ''," + 
                          "fb smallint NOT NULL DEFAULT '0', " +
                          "member_id varchar(15) NOT NULL default '', " +
                          "player varchar(45) NOT NULL default '', " +
                          "item_num varchar(30) NOT NULL default '', " +
                          "item_name varchar(45) NOT NULL default '', " +
                          "price varchar(8) NOT NULL default '', " +
                          "p9 smallint NOT NULL DEFAULT '0', " +
                          "PRIMARY KEY (pos_hist_id), " +
                          "KEY date (date) " +
                        ") ENGINE=MyISAM;");

      //
      //   Backup Tee Sheet Emails
      //
      stmt.executeUpdate("CREATE TABLE backup_emails ( " +
                         "address varchar(50) NOT NULL default '' " +
                         ") ENGINE=MyISAM");
      
        // create demo_clubs
        stmt.executeUpdate("CREATE TABLE demo_clubs (" +
                "id int(11) NOT NULL auto_increment," +
                "activity_id int(11) NOT NULL default '0', " +
                "mfr_id int(11) NOT NULL default '0'," +
                "name varchar(255) NOT NULL default ''," +
                "icn varchar(16) default ''," +
                "type enum('','Driver','Wood','Hybrid','Iron','Wedge','Putter','Specialty','Other','Rental') NOT NULL default ''," +
                "type_id int(11) NOT NULL default 0," +
                "notes text NOT NULL default ''," +
                "for_sale tinyint(4) NOT NULL default '0', " +
                "enabled tinyint(4) NOT NULL default '0', " +
                "inact tinyint(4) NOT NULL default '0', " +
                "PRIMARY KEY (id)," +
                "UNIQUE KEY name (name)," +
                "UNIQUE KEY icn (icn)" +
                ") ENGINE=MyISAM DEFAULT CHARSET=latin1");

        // create demo_clubs_types
        stmt.executeUpdate("CREATE TABLE demo_clubs_types (" +
                "id int(11) NOT NULL auto_increment, " +
                "activity_id int(11) NOT NULL default '0', " +
                "type varchar(32) NOT NULL default '', " +
                "PRIMARY KEY  (id), " +
                "UNIQUE KEY type (activity_id, type) " +
                ") ENGINE=MyISAM DEFAULT CHARSET=latin1");

        // create demo_clubs_mfr
        stmt.executeUpdate("CREATE TABLE demo_clubs_mfr (" +
                "id int(11) NOT NULL auto_increment," +
                "mfr varchar(32) default NULL," +
                "PRIMARY KEY (id)," +
                "UNIQUE KEY mfr (mfr)" +
                ") ENGINE=MyISAM DEFAULT CHARSET=latin1");

        // populate demo_club_mfr table with defaults
        stmt.executeUpdate("INSERT INTO demo_clubs_mfr (id,mfr) VALUES ('1','None/Unknown');");
        stmt.executeUpdate("INSERT INTO demo_clubs_mfr (id,mfr) VALUES ('2','Other');");
        stmt.executeUpdate("INSERT INTO demo_clubs_mfr (id,mfr) VALUES ('3','Nike');");
        stmt.executeUpdate("INSERT INTO demo_clubs_mfr (id,mfr) VALUES ('4','Callaway');");
        stmt.executeUpdate("INSERT INTO demo_clubs_mfr (id,mfr) VALUES ('5','Cleveland');");
        stmt.executeUpdate("INSERT INTO demo_clubs_mfr (id,mfr) VALUES ('6','Cobra');");
        stmt.executeUpdate("INSERT INTO demo_clubs_mfr (id,mfr) VALUES ('7','Adams');");
        stmt.executeUpdate("INSERT INTO demo_clubs_mfr (id,mfr) VALUES ('8','Mizuno');");
        stmt.executeUpdate("INSERT INTO demo_clubs_mfr (id,mfr) VALUES ('9','Odyssey');");
        stmt.executeUpdate("INSERT INTO demo_clubs_mfr (id,mfr) VALUES ('10','Ping');");
        stmt.executeUpdate("INSERT INTO demo_clubs_mfr (id,mfr) VALUES ('11','Titleist');");
        stmt.executeUpdate("INSERT INTO demo_clubs_mfr (id,mfr) VALUES ('12','TaylorMade');");
        stmt.executeUpdate("INSERT INTO demo_clubs_mfr (id,mfr) VALUES ('13','Tour Edge');");

        // create demo_clubs_usage
        stmt.executeUpdate("CREATE TABLE demo_clubs_usage (" +
                "id int(11) NOT NULL auto_increment," +
                "club_id int(11) NOT NULL default '0'," +
                "username varchar(15) NOT NULL default ''," +
                "out_by varchar(15) NOT NULL default ''," +
                "datetime_out datetime NOT NULL default '0000-00-00 00:00:00'," +
                "in_by varchar(15) NOT NULL default ''," +
                "datetime_in datetime NOT NULL default '0000-00-00 00:00:00'," +
                "notes text NOT NULL," +
                "PRIMARY KEY (id)" +
                ") ENGINE=MyISAM DEFAULT CHARSET=latin1");


        // DINING CLUB TABLES

        // create dining_config
        stmt.executeUpdate("CREATE TABLE dining_config (" +
                "form_text text NOT NULL," +
                "link_text varchar(80) NOT NULL default 'Make a Dining Request'," +
                "prompt_text text NOT NULL," +
                "pro_main tinyint(4) NOT NULL default '0'," +
                "pro_teetime tinyint(4) NOT NULL default '0'," +
                "pro_lesson tinyint(4) NOT NULL default '0'," +
                "mem_main tinyint(4) NOT NULL default '0'," +
                "mem_teetime tinyint(4) NOT NULL default '0'," +
                "mem_lesson tinyint(4) NOT NULL default '0'," +
                "email_teetime tinyint(4) NOT NULL default '0'," +
                "email_lesson tinyint(4) NOT NULL default '0'," +
                "custom_url varchar(50) NOT NULL default ''" +
                ") ENGINE=MyISAM DEFAULT CHARSET=utf8");


        // create dining_emails
        stmt.executeUpdate("CREATE TABLE dining_emails (" +
                "id tinyint(4) NOT NULL auto_increment," +
                "address varchar(50) default NULL," +
                "PRIMARY KEY (id)" +
                ") ENGINE=MyISAM DEFAULT CHARSET=utf8");


        // create dining_messages
        stmt.executeUpdate("CREATE TABLE dining_messages (" +
                "id int(11) NOT NULL auto_increment," +
                "active tinyint(4) default '1'," +
                "name varchar(30) default NULL," +
                "sdate date default NULL," +
                "edate date default NULL," +
                "form_text text," +
                "prompt_text text," +
                "link_text varchar(80) default NULL," +
                "priority int(11) default '0'," +
                "eo_week tinyint(4) default '0'," +
                "sunday tinyint(4) default '0'," +
                "monday tinyint(4) default '0'," +
                "tuesday tinyint(4) default '0'," +
                "wednesday tinyint(4) default '0'," +
                "thursday tinyint(4) default '0'," +
                "friday tinyint(4) default '0'," +
                "saturday tinyint(4) default '0'," +
                "PRIMARY KEY (id)" +
                ") ENGINE=MyISAM DEFAULT CHARSET=utf8");


        // create dining_rooms
        stmt.executeUpdate("CREATE TABLE dining_rooms (" +
                "id int(11) NOT NULL auto_increment," +
                "name varchar(30) default NULL," +
                "description text," +
                "PRIMARY KEY (id)" +
                ") ENGINE=MyISAM DEFAULT CHARSET=utf8");


        // create dining_rooms
        stmt.executeUpdate("CREATE TABLE dining_stats (" +
                "id int(11) NOT NULL auto_increment," +
                "date date default NULL," +
                "date_time datetime default NULL," +
                "type enum('','view','submit') default NULL," +
                "user_group enum('','proshop','member','email') default NULL," +
                "caller enum('','main','teetime','lesson','email') default NULL," +
                "custom_id int(11) default NULL," +
                "count int(11) NOT NULL default '1'," +
                "PRIMARY KEY (id)," +
                "UNIQUE KEY date (date,date_time,type,user_group,caller,custom_id)" +
                ") ENGINE=MyISAM DEFAULT CHARSET=utf8");

        // create activities table
        stmt.executeUpdate("CREATE TABLE activities (" +
                "activity_id int(11) NOT NULL auto_increment," +
                "parent_id int(11) NOT NULL default '0'," +
                "activity_name varchar(32) NOT NULL default ''," +
                "common_name varchar(16) NOT NULL default ''," +
                "max_players tinyint(4) NOT NULL default '0'," +
                "allow_guests tinyint(4) NOT NULL default '0'," +
                "allow_x tinyint(4) NOT NULL default '0'," +
                "xhrs tinyint(4) NOT NULL default '0'," +
                "contact varchar(40) NOT NULL default ''," +
                "email varchar(64) NOT NULL default ''," +
                "emailOpt tinyint(4) NOT NULL default '0'," +
                "unacompGuest tinyint(4) NOT NULL default '0'," +
                "forceg tinyint(4) NOT NULL default '0'," +
                "hndcpProSheet tinyint(4) NOT NULL default '0'," +
                "hndcpMemSheet tinyint(4) NOT NULL default '0'," +
                "rndsperday tinyint(4) NOT NULL default '0'," +
                "minutesbtwn smallint(8) NOT NULL default '0'," +
                "first_time int(11) NOT NULL default '0', " + 
                "last_time int(11) NOT NULL default '0', " + 
                "`interval` int(11) NOT NULL default '0', " + 
                "alt_interval int(11) NOT NULL default '0', " + 
                "disallow_joins tinyint(4) NOT NULL default '0', " + 
                "use_hdcp_equiv tinyint(4) NOT NULL default '0', " + 
                "hdcp_equiv_name varchar(16) NOT NULL default '', " + 
                "hdcp_joining_range double(3,1) NOT NULL default '0.0', " + 
                "allowable_views varchar(8) NOT NULL default '1,2,3', " +
                "consec_mem tinyint(4) NOT NULL default '0', " +
                "consec_mem_csv text NOT NULL default '', " +
                "consec_pro tinyint(4) NOT NULL default '0', " +
                "consec_pro_csv text NOT NULL default '', " +
                "guestdb tinyint(4) NOT NULL default '0', " +
                "enabled tinyint(4) NOT NULL default '1', " +
                "sort_by tinyint(4) NOT NULL default '0', " +
                "PRIMARY KEY  (activity_id)" +
                ") ENGINE=MyISAM DEFAULT CHARSET=latin1");



        stmt.executeUpdate("CREATE TABLE activity_sheet_history ( " +
                "history_id int(11) NOT NULL auto_increment, " +
                "sheet_id int(11) NOT NULL default '0', " +
                "date_time datetime NOT NULL default '0000-00-00 00:00:00', " +
                "username varchar(15) NOT NULL default '', " +
                "players text NOT NULL, " +
                "PRIMARY KEY  (history_id) " +
                ") ENGINE=MyISAM DEFAULT CHARSET=latin1");


        stmt.executeUpdate("CREATE TABLE activity_sheets ( " +
                "sheet_id int(11) NOT NULL auto_increment, " +
                "activity_id int(11) NOT NULL default '0', " +
                "date_time datetime NOT NULL default '0000-00-00 00:00:00', " +
                "event_id int(11) NOT NULL default '0', " +
                "lesson_id int(11) NOT NULL default '0', " +
                "rest_id int(11) NOT NULL default '0', " +
                "blocker_id int(11) NOT NULL default '0', " +
                "auto_blocked tinyint(4) NOT NULL default '0', " +
                "in_use_by varchar(15) NOT NULL default '', " +
                "in_use_at datetime NOT NULL default '0000-00-00 00:00:00', " +
                "notes text NOT NULL, " +
                "hideNotes tinyint(4) NOT NULL default '0', " +
                "last_mod_by varchar(15) NOT NULL default '', " +
                "last_mod_date datetime NOT NULL default '0000-00-00 00:00:00', " +
                "proNew tinyint(4) NOT NULL default '0', " +
                "proMod tinyint(4) NOT NULL default '0', " +
                "memNew tinyint(4) NOT NULL default '0', " +
                "memMod tinyint(4) NOT NULL default '0', " +
                "disallow_joins tinyint(4) NOT NULL default '0', " +
                "related_ids text NOT NULL, " +
                "report_ignore tinyint(4) NOT NULL default '0', " +
                "PRIMARY KEY  (sheet_id) " +
                ") ENGINE=MyISAM DEFAULT CHARSET=latin1");

        stmt.executeUpdate("CREATE TABLE activity_sheets_players ( " +
                "activity_sheets_player_id int(11) NOT NULL auto_increment, " +
                "activity_sheet_id int(11) NOT NULL default '0', " +
                "username varchar(15) NOT NULL default '', " +
                "userg varchar(12) NOT NULL default '', " +
                "player_name varchar(64) NOT NULL default '', " +
                "pos tinyint(4) NOT NULL default '0', " +
                "`show` tinyint(4) NOT NULL default '0', " +
                "guest_id int(11) NOT NULL default '0', " +
                "PRIMARY KEY  (activity_sheets_player_id), " +
                "UNIQUE KEY pos (activity_sheet_id,pos) " +
                ") ENGINE=MyISAM DEFAULT CHARSET=latin1");

        // Create guestdb table
        stmt.executeUpdate("CREATE TABLE guestdb (" +
                "activity_id int(11) NOT NULL default '0', " +
                "ask_name tinyint(1) NOT NULL default '0', " +
                "ask_email tinyint(1) NOT NULL default '0', " +
                "ask_phone tinyint(1) NOT NULL default '0', " +
                "ask_address tinyint(1) NOT NULL default '0', " +
                "ask_gender tinyint(1) NOT NULL default '0', " +
                "ask_hdcp_num tinyint(1) NOT NULL default '0', " +
                "ask_hdcp_index tinyint(1) NOT NULL default '0', " +
                "ask_home_club tinyint(1) NOT NULL default '0', " +
                "req_uid tinyint(1) NOT NULL default '0', " +
                "req_name tinyint(1) NOT NULL default '0', " +
                "req_email tinyint(1) NOT NULL default '0', " +
                "req_phone tinyint(1) NOT NULL default '0', " +
                "req_address tinyint(1) NOT NULL default '0', " +
                "req_gender tinyint(1) NOT NULL default '0', " +
                "req_hdcp_num tinyint(1) NOT NULL default '0', " +
                "req_hdcp_index tinyint(1) NOT NULL default '0', " +
                "req_home_club tinyint(1) NOT NULL default '0', " +
                "force_uid tinyint(1) NOT NULL default '0', " +
                "display_uid tinyint(1) NOT NULL default '0'" +
                ") ENGINE=MyISAM DEFAULT CHARSET=latin1");


        // Create guestdb_hosts table
        stmt.executeUpdate("CREATE TABLE guestdb_hosts (" +
                "host_id int(11) NOT NULL auto_increment, " +
                "guest_Id int(11) NOT NULL default '0', " +
                "username varchar(15) NOT NULL default '', " +
                "PRIMARY KEY (host_id), " +
                "UNIQUE KEY username (guest_id, username)" +
                ") ENGINE=MyISAM DEFAULT CHARSET=latin1");


        // Create guestdb_data table
        stmt.executeUpdate("CREATE TABLE guestdb_data (" +
                "guest_id int(11) NOT NULL auto_increment, " +
                "unique_id varchar(24) default NULL, " +
                "uid_type varchar(24) NOT NULL default '', " +
                "name_pre varchar(4) NOT NULL default '', " +
                "name_first varchar(20) NOT NULL default '', " +
                "name_mi char(1) NOT NULL default '', " +
                "name_last varchar(20) NOT NULL default '', " +
                "name_suf varchar(4) NOT NULL default '', " +
                "email1 varchar(50) NOT NULL default '', " +
                "email2 varchar(50) NOT NULL default '', " +
                "email_bounced1 tinyint(4) NOT NULL default '0', " +
                "email_bounced2 tinyint(4) NOT NULL default '0', " +
                "emailOpt tinyint(4) NOT NULL default '0', " +
                "phone1 varchar(24) NOT NULL default '', " +
                "phone2 varchar(24) NOT NULL default '', " +
                "address1 varchar(64) NOT NULL default '', " +
                "address2 varchar(64) NOT NULL default '', " +
                "city varchar(30) NOT NULL default '', " +
                "state varchar(24) NOT NULL default '', " +
                "zip varchar(10) NOT NULL default '', " +
                "gender enum('','M','F') NOT NULL default '', " +
                "hdcp_num varchar(16) NOT NULL default '', " +
                "hdcp_index double NOT NULL default '-99', " +
                "home_club varchar(30) NOT NULL default '', " +
                "count int(11) NOT NULL default '0', " +
                "inact tinyint(4) NOT NULL default '0', " +
                "PRIMARY KEY (guest_id), " +
                "UNIQUE KEY unique_id (unique_id)" +
                ") ENGINE=MyISAM DEFAULT CHARSET=latin1");

        
        stmt.executeUpdate("CREATE TABLE activity_sheet_notes ( " +
                "note_id int(11) NOT NULL auto_increment, " +
                "activity_id int(11) NOT NULL default '0', " +
                "`date` date NOT NULL default '0000-00-00', " +
                "notes text NOT NULL, " +
                "PRIMARY KEY (note_id), " +
                "UNIQUE KEY ind1 (activity_id,`date`) " +
                ") ENGINE=MyISAM DEFAULT CHARSET=latin1");


      stmt.close();

   }
   catch (Exception exc) {

      // SQL Error ....

      out.println("<HTML><HEAD><TITLE>SQL Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H1>SQL Type Error on V5 Tables</H1>");
      out.println("<BR>Exception: "+ exc.getMessage());
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_init.htm\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");

      if (con != null) {
         try {
            con.close();       // Close the db connection........
         }
         catch (SQLException ignored) {
         }
      }
      return;
   }

   //
   //  New v5 Club - v5 DB Tables are built - init the v5 tables
   //
   String message = "";     // init message field
   String pw = "";          // init password
   String pwChar = "";

   String [] pw_table = { "q", "w", "e", "6", "9", "2", "3", "5", "7", "a", "s", "d", "f", "g", "h",
                          "j", "k", "8", "z", "x", "c", "v", "b", "n", "m", "r", "t", "y", "u", "p" };

   //
   //  Build a randomly generated password to use for the admin and proshop users (6 chars long)
   //
   for (int i=0; i<6; i++) {
      double x = Math.random() * 30;       // get a number between 0 and 29
      int y = (int) x;                     // create an int
      pwChar = pw_table[y];                // get a character
      pw = pw + pwChar;                    // biuld pw
   }


   try {
      PreparedStatement pstmt = con.prepareStatement (
         "INSERT INTO login2 (username, password, message) VALUES ('admin', ?, ?)");     // admin user

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, pw);       // put the parm in pstmt
      pstmt.setString(2, message);
      count = pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

      pstmt = con.prepareStatement (
         "INSERT INTO login2 (username, password, message) VALUES ('admin4tea', 'amonkey', ?)");   // admin user

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, message);
      count = pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

      pstmt = con.prepareStatement (
         "INSERT INTO login2 (username, password, message) VALUES ('proshop1', ?, ?)");     // proshop1 user

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, pw);       // put the parm in pstmt
      pstmt.setString(2, message);
      count = pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();
      
      pstmt = con.prepareStatement (
         "INSERT INTO login2 (username, password, message, " +
         "TOOLS_ANNOUNCE, TOOLS_EMAIL, TOOLS_SEARCHTS, TOOLS_HDCP, REPORTS, LOTT_UPDATE, LOTT_APPROVE, LESS_CONFIG, LESS_VIEW, LESS_UPDATE, " +
         "EVNTSUP_UPDATE, EVNTSUP_VIEW, EVNTSUP_MANAGE, WAITLIST_UPDATE, WAITLIST_VIEW, WAITLIST_MANAGE, TS_VIEW, TS_UPDATE, TS_CHECKIN, TS_PRINT, " +
         "TS_POS, TS_CTRL_FROST, TS_CTRL_TSEDIT, TS_CTRL_EMAIL, TS_PACE_VIEW, TS_PACE_UPDATE, SYSCONFIG_CLUBCONFIG, SYSCONFIG_TEESHEETS, SYSCONFIG_EVENT, SYSCONFIG_LOTTERY, " +
         "SYSCONFIG_RESTRICTIONS, SYSCONFIG_MEMBERNOTICES, SYS_CONFIG, DEMOCLUBS_CHECKIN, DEMOCLUBS_MANAGE, DINING_REQUEST, DINING_CONFIG) " +
         "VALUES ('proshopfb', ?, ?, " +
         "0,0,0,0,0,0,0,0,0,0," +
         "0,0,0,0,0,0,0,0,0,0," +
         "0,0,0,0,0,0,0,0,0,0," +
         "0,0,0,0,0,1,1)");
      
      pstmt.clearParameters();
      pstmt.setString(1, pw);
      pstmt.setString(2, message);
      count = pstmt.executeUpdate();
      
      pstmt.close();
      
      //
      //   Setup a proshop user just for our use (proshop4tea)
      //
      pw = "amonkey";                 // our pw
        
      pstmt = con.prepareStatement (
         "INSERT INTO login2 (username, password, message, DINING_REQUEST, DINING_CONFIG) VALUES ('proshop4tea', ?, ?,1,1)");   // proshop4tea user

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, pw);       // put the parm in pstmt
      pstmt.setString(2, message);
      count = pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

   }
   catch (Exception exc) {

      // SQL Error ....

      out.println("<HTML><HEAD><TITLE>SQL Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H1>SQL Type Error While Updating v5 Tables</H1>");
      out.println("<BR><br>An error was received while adding the initial users to Login2.");
      out.println("<BR><br>Exception: "+ exc.getMessage());
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_init.htm\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");

      if (con != null) {
         try {
            con.close();       // Close the db connection........
         }
         catch (SQLException ignored) {
         }
      }
      return;
   }

   // DB Table setup complete - inform support....

   out.println("<HTML><HEAD><TITLE>Database Creation Complete</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>v5 Database Tables Successfully Built</H3>");
   out.println("<BR><BR>This is a new v5 Club.");
   out.println("<BR><BR>Please continue.");
   out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return</A>.");
   out.println("</CENTER></BODY></HTML>");
   out.close();

   // 
   // done
   //
   if (con != null) {
      try {
         con.close();       // Close the db connection........
      }
      catch (SQLException ignored) {
      }
   }
   if (con2 != null) {
      try {
         con2.close();       // Close the db connection........
      }
      catch (SQLException ignored) {
      }
   }
 }   
    
 // *********************************************************
 // Illegal access by user - force user to login....
 // *********************************************************

 private void invalidUser(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H2>Access Error</H2><BR>");
   out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
   out.println("<BR><BR>Please <A HREF=\"/" +rev+ "/servlet/Logout\">login</A>");
   out.println("</CENTER></BODY></HTML>");
 }
}
