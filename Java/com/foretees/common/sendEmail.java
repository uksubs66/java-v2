/***************************************************************************************
 *   sendEmail:  This servlet will send an email notification based on info provided in the parm table.
 *
 *       called by:  Proshop_slot
 *                   Proshop_lott
 *                   Proshop_evntSignUp
 *                   Proshop_dsheet
 *                   Proshop_waitlist_slot
 *                   Member_slot
 *                   Member_lott
 *                   Member_evntSignUp
 *                   Member_waitlist_slot
 *
 *
 *   created:  2/18/2004   Bob P.
 *   
 *   iCal reference: http://tools.ietf.org/html/rfc2445#section-2
 *
 *   last updated:
 *
 *      7/20/10  Oak Hill CC (oakhillcc) - Added custom email message for when at least one guest is present.
 *      7/13/10  Cherry Hill CC (cherryhills) - Apply custom to send custom guest message to lottery assignment emails as well.
 *      7/13/10  The Country Club (tcclub) - Adjustments to custom email message (bolding and small additions)
 *      6/24/10  Force mail.smtp.sendpartial to true to ensure we send emails even if there are bad address - send the ones we can
 *      6/17/10  Include tracked guests when sending email notifications if they have an email address in the system
 *      5/28/10  Congressional CC - add/modify a custom message to tee time notifications (case 1560).
 *      5/12/10  Baltimore CC (baltimore) - Remove custom to send an email to a pro address on event signups (case 1228).
 *      5/05/10  Winged Foot - add custom link to pdf on tee time confs with guests (case 1833).
 *      4/26/10  Willamette Valley - add custom link to pdf on tee time confs for auto detailing service.
 *      4/19/10  Mid Pacific - add a custom message to tee time notifications.
 *      4/13/10  Fix to make sure FlxRez event emails use the FlxRez logo and not the ForeTees logo
 *      4/13/10  Cherry Hills - add custom link to pdf when guests included in tee time - guest policies.
 *      3/02/10  Added a confirmation email when the staff uses the Email Tool - this email goes to the pro to tell them we've queued all the outgoing emails
 *      2/19/10  Add support for including attachments, iCal and html emails
 *      2/02/10  Tee Time Events - show start time of event, but make it more obvious that the member's actual time may vary.
 *      1/29/10  MountainGate CC (mtngatecc) - Remove custom associated with case 1707 from processing.
 *      1/19/10  Add the course name to the iCal summary if exists and a tee time notification.
 *      1/14/10  Changes for Activity emails
 *     12/09/09  When looking for events only check those that are active.
 *     12/04/09  Mountaingate - send email to pro whenever a tee time is cancelled (case 1707).
 *     12/02/09  Move adjustTime method to Utilities.
 *     11/24/09  Baltusrol - add a custom message to tee time notifications (case 1741).
 *     11/17/09  Columbia Edgewater CC (cecc) - Updated pro email address to BCC when "Potential Member" guests are playing
 *     11/16/09  Santa Ana CC - add a custom message to tee time notifications (case 1742).
 *     11/08/09  Added lesson location to email notifications for lesson bookings on non-golf activities
 *     10/27/09  Jonathans Landing - do not include the time of the event in message if email is for shotgun event where there are
 *                                   more than one shotguns on this date and course (case 1604).
 *      9/15/09  Added support for activity based emails
 *      9/02/09  Fox Den CC (foxdencountryclub) - Add Fox Den CC to custom processing for sending out an email whenever a member changes his/her email address(es)
 *      8/28/09  Wellesley - add custom message to tee time notifications when guests are included (case 1706).
 *      7/28/09  Include an iCal attachment in modified tee time and event signup emails if the member was
 *               not part of the tee time previous to the modification (new players).
 *      7/13/09  Do not include the standard trailer in emails that contain the unsubscribe trailer.
 *      7/02/09  The Country Club (tcclub) - Remove 'Limousines are discouraged' line from custom email text.
 *      6/03/09  Do not send iCal attachments for lottery requests
 *      5/29/09  Only add dining prompt and link if notification is for a tee time or lesson booking and the corresponding options are selected in the dining system config
 *      5/18/09  Added support for iCal1/2 preferences in member records and lesson pro records
 *      4/28/09  Columbia Edgewater CC - Add specific pro email address as BCC on tee time notifcations involving the "Potential Member" guest type
 *      4/28/09  Added oldplayersA array to hold all old players for this tee time
 *      4/02/09  Congressional CC - modify dress code email message
 *      3/31/09  Baltusrol GC - Hide Front or Back Nine References.  Change this to use a boolean - skipFrontBack. (Case #1639)
 *      3/27/09  Congressional CC - add dress code message to custom tee time email message.
 *      3/03/09  Rework email sending - add html multipart (unsubscribe link) & vcal attachments
 *      2/18/09  Added dining request processing.
 *     11/12/08  Make further changes to frost delay message - change 'Pushed Back' to modified.
 *     11/04/08  Change "frost" text in frost delay messages to "weather" so it is more global.
 *     10/15/08  Added processing to handle only suppressing member emails for the lesson book email suppression (not pro emails) (case 1454).
 *     10/09/08  Congressional CC - add a custom message to tee time notifications (case 1560).
 *     10/03/08  Check for replacement text for the word "Lottery" when email is for a lottery request.
 *      8/26/08  Rhode Island CC - add a custom message (teetimeMsg) to tee time notifications.
 *      8/26/08  Brooklawn CC - add a custom message to tee time notifications without guests (case 1540).
 *      8/14/08  Remove unnecessary strings and use the parm values instead to increase efficiency.
 *      8/13/08  The CC (Brookline) - add some custom text to tee time notifications (case 1520).
 *      7/23/08  Brooklawn CC - add some text to tee time notifications with guests.
 *      6/10/08  Make checks for duplicate email addresses NOT case sensitive.
 *      5/08/08  Oakmont - Add custom trailer message (case 1474).
 *               Also, user string buffers when building message instead of strings.
 *      3/27/08  The CC - Add unhidden notes to tee time emails (Case# 1406)
 *      3/27/08  Tweak date output for season long events
 *      3/10/08  Added support for external mail servers
 *      3/07/08  Piedmont & Gallery - add pro emails to all event signup notifications (cases 1395 & 1398).
 *               This is for these customs, but will be used for all when config added to events.
 *      2/29/08  Add the individual tee times to the groups of multiple tee time requests (case 1231). 
 *     12/31/07  MN Valley CC - send email to pro on mods or cancels of event registrations (Case #1353)
 *     12/14/07  Fixed lesson book emails so they are sent to pro even if member doesn't have email in system
 *      9/24/07  Pinery CC - Hide Front or Back Nine References (Case #1045)
 *      9/24/07  Hallbrook CC - send email to caddie master when a caddie is requested (case #1037) - use Oakmont method.
 *      9/21/07  Mediterra - Added sendMediterraEmail to send email to pro when Sports mship uses up their guest quota
 *      9/21/07  Baltimore CC - send email to pro on all event registration activity (Case #1228)
 *      8/14/07  Changed logic to allow sending to email2 even if email is empty
 *      6/26/07  Blackhawk - blind copy Mark Caufield whenever an email goes to specific members (case #1201).
 *      6/26/07  The CC - do not include the '9' (front/back) in emails - it confuses the members (case #1197).
 *      6/19/07  Winged Foot - copy all emails to pro (case #1146).
 *      6/19/07  TLT - change messages for TLT clubs.
 *      5/07/07  The CC - add a note to all emails for members to contact pro with questions.
 *      5/07/07  Correct special characters in the club name (i.e. & instead of &amp).
 *      4/25/07  Congressional - pass the date for the ourse Name Labeling.
 *      4/10/07  Congressional - abstract the course name depending on the day (Course Name Labeling, case #1046).
 *      3/27/07  Tweaked debug info and changed the word Lottery to Tee Time Request for Pecan Plantation (for spam testing)
 *      3/12/07  Add additional debug information to error log for failed send calls.
 *      2/06/07  Add checks for duplicate email addresses.
 *      1/29/07  Add the time of the event to emails for event signups (for non-shotgun events).
 *     10/07/06  Change queries to only pull back email addresses that don't have the bounced flag on
 *      3/07/06  change logerror to go to verifySlot.
 *     11/02/05  Bellerive - send emails to other clubs from their temp site.
 *     10/14/05  Add spam message for AOL users.
 *      6/16/05  Westchester - custom subject strings.
 *      5/12/05  Send email to lesson pro whenever a lesson is added or cancelled.
 *      3/25/05  Add custom method for Oakmont - send email to caddie master.
 *      1/24/05  RDP Ver 5 - change club2 to club5.
 *      1/05/05  RDP Ver 5 - add emails for frost delay feature in proshop.
 *      8/25/04  RDP Do not send email if today and tee time has past or is within 1 hr of current time.
 *
 *
 ***************************************************************************************
 */

/*
 *  Notes: 
 *          Some mail servers will reject mail if there is no To listed.
 *          Make arraylist for To, Cc, Bcc containing all receipts - if afer processing there is no To, then we can add 1 from cc or bcc or send to dummy account to ensure To is listed.
 *          
 *
 */

package com.foretees.common;

import java.io.*;
import java.util.*;
import java.sql.*;
import java.text.DateFormatSymbols;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.mail.internet.*;
import javax.mail.*;
import javax.activation.*;


public class sendEmail {


   //********************************************************
   // Some constants for emails sent within this class
   //********************************************************
   //
   static String host = ProcessConstants.HOST;

   static String port = ProcessConstants.PORT;

   static String efrom = ProcessConstants.EFROM;

   static String header = ProcessConstants.HEADER;

   static String trailer = ProcessConstants.TRAILER;

   static String trailerAOL = ProcessConstants.TRAILERAOL;

   private static String rev = ProcessConstants.REV;

   //static String unsubcribe = ProcessConstants.UNSUBSCRIBE;


/**
 //************************************************************************
 //
 //  sendIt - build and send the email notification
 //
 //************************************************************************
 **/

 public static void sendIt(parmEmail parms, Connection con) {


   Statement stmtN = null;
   ResultSet rs = null;

   //
   //  Get the parms passed in the parm block
   //
   long date = parms.date;
   int time = parms.time;
   int time2 = parms.time2;
   int time3 = parms.time3;
   int time4 = parms.time4;
   int time5 = parms.time5;
   int to_time = parms.to_time;
   int from_time = parms.from_time;
   int fb = parms.fb;
   int to_fb = parms.to_fb;
   int from_fb = parms.from_fb;
   int mm = parms.mm;
   int dd = parms.dd;
   int yy = parms.yy;

   int emailNew = parms.emailNew;
   int emailMod = parms.emailMod;
   int emailCan = parms.emailCan;

   int p91 = parms.p91;
   int p92 = parms.p92;
   int p93 = parms.p93;
   int p94 = parms.p94;
   int p95 = parms.p95;
   int p96 = parms.p96;
   int p97 = parms.p97;
   int p98 = parms.p98;
   int p99 = parms.p99;
   int p910 = parms.p910;
   int p911 = parms.p911;
   int p912 = parms.p912;
   int p913 = parms.p913;
   int p914 = parms.p914;
   int p915 = parms.p915;
   int p916 = parms.p916;
   int p917 = parms.p917;
   int p918 = parms.p918;
   int p919 = parms.p919;
   int p920 = parms.p920;
   int p921 = parms.p921;
   int p922 = parms.p922;
   int p923 = parms.p923;
   int p924 = parms.p924;
   int p925 = parms.p925;
     
   int i = 0;
   int i2 = 0;
   int TLT = 0;
   
   String club = getClub.getClubName(con); // get our internal club name for this club
   
   ArrayList<ArrayList<String>> eaddrTo  = new ArrayList<ArrayList<String>>();
   ArrayList<String> eaddrProCopy = new ArrayList<String>();    // email addresses that we normally bcc
   
   String replyTo = "";
   
   String [] eaddrA = new String [100];            // arrays to process email addresses (max possible = 100)

   String [] playerA = new String [25];            // player names 
   String [] oldplayerA = new String[25];          // old player names
   String [] userA = new String [25];              // usernames 
   String [] olduserA = new String [25];           // old usernames
   int [] guest_idA = new int[25];
   int [] oldguest_idA = new int[25];

   playerA[0] = parms.player1;
   playerA[1] = parms.player2;
   playerA[2] = parms.player3;
   playerA[3] = parms.player4;
   playerA[4] = parms.player5;
   playerA[5] = parms.player6;
   playerA[6] = parms.player7;
   playerA[7] = parms.player8;
   playerA[8] = parms.player9;
   playerA[9] = parms.player10;
   playerA[10] = parms.player11;
   playerA[11] = parms.player12;
   playerA[12] = parms.player13;
   playerA[13] = parms.player14;
   playerA[14] = parms.player15;
   playerA[15] = parms.player16;
   playerA[16] = parms.player17;
   playerA[17] = parms.player18;
   playerA[18] = parms.player19;
   playerA[19] = parms.player20;
   playerA[20] = parms.player21;
   playerA[21] = parms.player22;
   playerA[22] = parms.player23;
   playerA[23] = parms.player24;
   playerA[24] = parms.player25;

   String user = parms.user;
   
   userA[0] = parms.user1;
   userA[1] = parms.user2;
   userA[2] = parms.user3;
   userA[3] = parms.user4;
   userA[4] = parms.user5;
   userA[5] = parms.user6;
   userA[6] = parms.user7;
   userA[7] = parms.user8;
   userA[8] = parms.user9;
   userA[9] = parms.user10;
   userA[10] = parms.user11;
   userA[11] = parms.user12;
   userA[12] = parms.user13;
   userA[13] = parms.user14;
   userA[14] = parms.user15;
   userA[15] = parms.user16;
   userA[16] = parms.user17;
   userA[17] = parms.user18;
   userA[18] = parms.user19;
   userA[19] = parms.user20;
   userA[20] = parms.user21;
   userA[21] = parms.user22;
   userA[22] = parms.user23;
   userA[23] = parms.user24;
   userA[24] = parms.user25;

   olduserA[0] = parms.olduser1;
   olduserA[1] = parms.olduser2;
   olduserA[2] = parms.olduser3;
   olduserA[3] = parms.olduser4;
   olduserA[4] = parms.olduser5;
   olduserA[5] = parms.olduser6;
   olduserA[6] = parms.olduser7;
   olduserA[7] = parms.olduser8;
   olduserA[8] = parms.olduser9;
   olduserA[9] = parms.olduser10;
   olduserA[10] = parms.olduser11;
   olduserA[11] = parms.olduser12;
   olduserA[12] = parms.olduser13;
   olduserA[13] = parms.olduser14;
   olduserA[14] = parms.olduser15;
   olduserA[15] = parms.olduser16;
   olduserA[16] = parms.olduser17;
   olduserA[17] = parms.olduser18;
   olduserA[18] = parms.olduser19;
   olduserA[19] = parms.olduser20;
   olduserA[20] = parms.olduser21;
   olduserA[21] = parms.olduser22;
   olduserA[22] = parms.olduser23;
   olduserA[23] = parms.olduser24;
   olduserA[24] = parms.olduser25;

   oldplayerA[0] = parms.oldplayer1;
   oldplayerA[1] = parms.oldplayer2;
   oldplayerA[2] = parms.oldplayer3;
   oldplayerA[3] = parms.oldplayer4;
   oldplayerA[4] = parms.oldplayer5;
   oldplayerA[5] = parms.oldplayer6;
   oldplayerA[6] = parms.oldplayer7;
   oldplayerA[7] = parms.oldplayer8;
   oldplayerA[8] = parms.oldplayer9;
   oldplayerA[9] = parms.oldplayer10;
   oldplayerA[10] = parms.oldplayer11;
   oldplayerA[11] = parms.oldplayer12;
   oldplayerA[12] = parms.oldplayer13;
   oldplayerA[13] = parms.oldplayer14;
   oldplayerA[14] = parms.oldplayer15;
   oldplayerA[15] = parms.oldplayer16;
   oldplayerA[16] = parms.oldplayer17;
   oldplayerA[17] = parms.oldplayer18;
   oldplayerA[18] = parms.oldplayer19;
   oldplayerA[19] = parms.oldplayer20;
   oldplayerA[20] = parms.oldplayer21;
   oldplayerA[21] = parms.oldplayer22;
   oldplayerA[22] = parms.oldplayer23;
   oldplayerA[23] = parms.oldplayer24;
   oldplayerA[24] = parms.oldplayer25;

   guest_idA[0] = parms.guest_id1;
   guest_idA[1] = parms.guest_id2;
   guest_idA[2] = parms.guest_id3;
   guest_idA[3] = parms.guest_id4;
   guest_idA[4] = parms.guest_id5;
   guest_idA[5] = parms.guest_id6;
   guest_idA[6] = parms.guest_id7;
   guest_idA[7] = parms.guest_id8;
   guest_idA[8] = parms.guest_id9;
   guest_idA[9] = parms.guest_id10;
   guest_idA[10] = parms.guest_id11;
   guest_idA[11] = parms.guest_id12;
   guest_idA[12] = parms.guest_id13;
   guest_idA[13] = parms.guest_id14;
   guest_idA[14] = parms.guest_id15;
   guest_idA[15] = parms.guest_id16;
   guest_idA[16] = parms.guest_id17;
   guest_idA[17] = parms.guest_id18;
   guest_idA[18] = parms.guest_id19;
   guest_idA[19] = parms.guest_id20;
   guest_idA[20] = parms.guest_id21;
   guest_idA[21] = parms.guest_id22;
   guest_idA[22] = parms.guest_id23;
   guest_idA[23] = parms.guest_id24;
   guest_idA[24] = parms.guest_id25;

   oldguest_idA[0] = parms.oldguest_id1;
   oldguest_idA[1] = parms.oldguest_id2;
   oldguest_idA[2] = parms.oldguest_id3;
   oldguest_idA[3] = parms.oldguest_id4;
   oldguest_idA[4] = parms.oldguest_id5;
   oldguest_idA[5] = parms.oldguest_id6;
   oldguest_idA[6] = parms.oldguest_id7;
   oldguest_idA[7] = parms.oldguest_id8;
   oldguest_idA[8] = parms.oldguest_id9;
   oldguest_idA[9] = parms.oldguest_id10;
   oldguest_idA[10] = parms.oldguest_id11;
   oldguest_idA[11] = parms.oldguest_id12;
   oldguest_idA[12] = parms.oldguest_id13;
   oldguest_idA[13] = parms.oldguest_id14;
   oldguest_idA[14] = parms.oldguest_id15;
   oldguest_idA[15] = parms.oldguest_id16;
   oldguest_idA[16] = parms.oldguest_id17;
   oldguest_idA[17] = parms.oldguest_id18;
   oldguest_idA[18] = parms.oldguest_id19;
   oldguest_idA[19] = parms.oldguest_id20;
   oldguest_idA[20] = parms.oldguest_id21;
   oldguest_idA[21] = parms.oldguest_id22;
   oldguest_idA[22] = parms.oldguest_id23;
   oldguest_idA[23] = parms.oldguest_id24;
   oldguest_idA[24] = parms.oldguest_id25;
   
   String tmp_sql = "SELECT (SELECT email FROM member2b WHERE username = ? AND email_bounced = 0) AS email1, (SELECT emailOpt FROM member2b WHERE username = ?) AS emailOpt, (SELECT email2 FROM member2b WHERE username = ? AND email2_bounced = 0) AS email2";
   String tmp_sql_gst = "SELECT (SELECT email1 FROM guestdb_data WHERE guest_id = ? AND email_bounced1 = 0) AS email1, (SELECT emailOpt FROM guestdb_data WHERE guest_id = ?) AS emailOpt, (SELECT email2 FROM guestdb_data WHERE guest_id = ? AND email_bounced2 = 0) AS email2";
   
   int etype = parms.etype;
   int wait = parms.wait;
   int checkWait = parms.checkWait;
   
   String oakmontTrlr = "";
   String brookMsg = "";
           
   StringBuffer strbfr = new StringBuffer("");        // build string buffer for email messages

           
   //
   //  Setup for email
   //
   String author = "unknown";
   String userFirst = "";
   String userMi = "";
   String userLast = "";
   String proName = "";
   String proEmail1 = "";
   String proEmail2 = "";
   String clubProEmail = "";    // used in blackstone custom
   String teetimeMsg = "";      // custom extension for tee time notifications
   String lotteryText = "";

   //String TEXT_BR = "\n";
   //String HTML_BR = "<br>";
   //String TEXT_2BR = "\n\n";
   //String HTML_2BR = "<br><br>";

   boolean aol = false;
   boolean dup = false;
   boolean BHproCCed = false;
   boolean CECCproCCed = false;
   boolean guestsIncluded = false;
   boolean skipFrontBack = false;
     
   //
   //  Custom to skip the f/b indicator in tee time emails
   //
   if (club.equals( "tcclub" ) || club.equals("pinery") || club.equals("baltusrolgc")) {     

      skipFrontBack = true;              // do not use the f/b indicator in messages
   }
   
   for (i2=0; i2<100; i2++) {            // init the email address array

      eaddrA[i2] = "";
   }
   
   if (!user.startsWith( "proshop" )) {  // if not proshop

      try {

         //
         //  Get this user's name (for id in email msg)
         //
         PreparedStatement pstmte = con.prepareStatement (
                  "SELECT name_last, name_first, name_mi FROM member2b WHERE username = ?");

         pstmte.clearParameters();        // clear the parms
         pstmte.setString(1, user);
         rs = pstmte.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            userLast = rs.getString(1);        // user's name
            userFirst = rs.getString(2);
            userMi = rs.getString(3);

            if (userMi.equals( "" )) {

               author = userFirst + " " + userLast;

            } else {

               author = userFirst + " " + userMi + " " + userLast;
            }
         }
         pstmte.close();              // close the stmt

      } catch (Exception ignore) {}
        
   } else {

      author = user;
   }

   if (parms.type.equals( "password" )) {       // if from Login (password request)

      user = "";                         //  don't need now
   }
   
   //
   //  Get today's date and time for email processing
   //
   Calendar ecal = new GregorianCalendar();               // get todays date

   int eyear = ecal.get(Calendar.YEAR);
   int emonth = ecal.get(Calendar.MONTH);
   int eday = ecal.get(Calendar.DAY_OF_MONTH);
   int e_hourDay = ecal.get(Calendar.HOUR_OF_DAY);
   int e_min = ecal.get(Calendar.MINUTE);
   int e_sec = ecal.get(Calendar.SECOND);
   int emailOk = 1;          // default to 'send it'
   int e_time = 0;

   long e_date = 0;

   //
   //  Build the 'time' string for display
   //
   //    Adjust the time based on the club's time zone (we are Central)
   //
   e_time = (e_hourDay * 100) + e_min;

   e_time = adjustTime(con, e_time);       // adjust for time zone

   if (e_time < 0) {                // if negative, then we went back or ahead one day

      e_time = 0 - e_time;          // convert back to positive value

      if (e_time < 1200) {           // if AM, then we rolled ahead 1 day

         //
         // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
         //
         ecal.add(Calendar.DATE,1);                     // get next day's date

         eyear = ecal.get(Calendar.YEAR);
         emonth = ecal.get(Calendar.MONTH);
         eday = ecal.get(Calendar.DAY_OF_MONTH);

      } else {                        // we rolled back 1 day

         //
         // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
         //
         ecal.add(Calendar.DATE,-1);                     // get yesterday's date

         eyear = ecal.get(Calendar.YEAR);
         emonth = ecal.get(Calendar.MONTH);
         eday = ecal.get(Calendar.DAY_OF_MONTH);
      }
   }

   int e_hour = e_time / 100;               // get adjusted hour
   e_min = e_time - (e_hour * 100);         // get minute value

   if (e_hour > 11) e_hour = e_hour - 12;                 // set to 12 hr clock
   
   if (e_hour == 0) e_hour = 12;

   emonth++;                                // month starts at zero
   e_date = (eyear * 10000) + (emonth * 100) + eday;

   // set the date/time string for email message
   //String email_time = emonth + "/" + eday + "/" + eyear + " at " + e_hour + ":" + ensureDoubleDigit(e_min) + ((e_am_pm == 0) ? " AM" : " PM");

   String DTSTAMP = e_date + "T" + ensureDoubleDigit(e_hour) + ensureDoubleDigit(e_min) + ensureDoubleDigit(e_sec);
   
   // if from edit tee sheet (move whole tee time) then set tee time to the time moved to
   if (parms.type.equals( "moveWhole" )) time = to_time;
   
   
   //
   // DONE SETTING UP INITIAL DATE/TIME VARIABLES
   

   //
   // Do not send email if it is for an activity within the next hour
   //
   if (date == e_date && time <= e_time + 100) emailOk = 0;
   


   //
   //****************************************************************************
   //  Send email notification if it is not for an activity within the next hour
   //****************************************************************************
   //
   if (emailOk != 0) { // this if block span the entire method!

      String to = "";                           // to address
      String to2 = "";                          // to address
      String f_b = "";
      String eampm = "";
      String etime = "";
      String etime2 = "";
      String etime3 = "";
      String etime4 = "";
      String etime5 = "";
      int emailOpt = 0;                         // user's email option parm
      int ehr = 0;
      int emin = 0;
      int send = 0;
      String clubName = "";
      String errorMsg = "";
      
      boolean hideTime = false;               // true = do not include the time of event in event emails
        
      PreparedStatement pstmte1 = null;

      //
      //  Get the full name of the club
      //
      try {

         stmtN = con.createStatement();
         rs = stmtN.executeQuery("SELECT clubName, contact, email, no_reservations FROM club5 WHERE clubName != ''");

         if (rs.next()) {

            clubName = rs.getString(1);
            //clubProName = rs.getString(2);        // no longer used
            clubProEmail = rs.getString(3);         // used in blackstone custom
            TLT = rs.getInt(4);                     // TLT Club ?
            parms.IS_TLT = (TLT == 1);
         }
         stmtN.close();

      } catch (Exception ignore) {}

      //
      //  Clean up the club name - convert any html special characters to normal
      //
      clubName = unfilter(clubName);
        
      
      //
      //  Check if Jonathans Landing and Shotgun Event - then check if more than one shotgun for this day
      //
      if (club.equals( "jonathanslanding" ) && parms.type.equals("event") && etype == 1) {
         
         try {

            pstmte1 = con.prepareStatement (
                    "SELECT COUNT(*) FROM events2b " +
                    "WHERE date = ? AND type = 1 AND courseName = ? AND inactive = 0");

            pstmte1.clearParameters();      
            pstmte1.setLong(1, date);
            pstmte1.setString(2, parms.course);
            rs = pstmte1.executeQuery();    

            if (rs.next()) {

               if (rs.getInt(1) > 1) hideTime = true;      // hide the time value if more than one shotgun
            }
            pstmte1.close();

         } catch (Exception ignore) {}      
      }
      

      //
      //  convert time to hour and minutes for email msg
      //
      ehr = time / 100;
      emin = time - (ehr * 100);
      eampm = " AM";
      if (ehr > 12) {

         eampm = " PM";
         ehr = ehr - 12;       // convert from military time
      }
      if (ehr == 12) {

         eampm = " PM";
      }
      if (ehr == 0) {

         ehr = 12;
         eampm = " AM";
      }

      etime = ehr + ":" + ensureDoubleDigit(emin) + eampm;
         

      if (time2 > 0) {            // if another time specified (multiple groups)
         
         ehr = time2 / 100;
         emin = time2 - (ehr * 100);
         eampm = " AM";
         if (ehr > 12) {

            eampm = " PM";
            ehr = ehr - 12;       // convert from military time
         }
         if (ehr == 12) {

            eampm = " PM";
         }
         if (ehr == 0) {

            ehr = 12;
            eampm = " AM";
         }

         etime2 = ehr + ":" + ensureDoubleDigit(emin) + eampm;
      }

      if (time3 > 0) {            // if another time specified (multiple groups)
         
         ehr = time3 / 100;
         emin = time3 - (ehr * 100);
         eampm = " AM";
         if (ehr > 12) {

            eampm = " PM";
            ehr = ehr - 12;       // convert from military time
         }
         if (ehr == 12) {

            eampm = " PM";
         }
         if (ehr == 0) {

            ehr = 12;
            eampm = " AM";
         }

         etime3 = ehr + ":" + ensureDoubleDigit(emin) + eampm;
      }

      if (time4 > 0) {            // if another time specified (multiple groups)
         
         ehr = time4 / 100;
         emin = time4 - (ehr * 100);
         eampm = " AM";
         if (ehr > 12) {

            eampm = " PM";
            ehr = ehr - 12;       // convert from military time
         }
         if (ehr == 12) {

            eampm = " PM";
         }
         if (ehr == 0) {

            ehr = 12;
            eampm = " AM";
         }

         etime4 = ehr + ":" + ensureDoubleDigit(emin) + eampm;
      }

      if (time5 > 0) {            // if another time specified (multiple groups)
         
         ehr = time5 / 100;
         emin = time5 - (ehr * 100);
         eampm = " AM";
         if (ehr > 12) {

            eampm = " PM";
            ehr = ehr - 12;       // convert from military time
         }
         if (ehr == 12) {

            eampm = " PM";
         }
         if (ehr == 0) {

            ehr = 12;
            eampm = " AM";
         }

         etime5 = ehr + ":" + ensureDoubleDigit(emin) + eampm;
      }
      
      
   
      //
      // PERFORM PRE-PROCESSING DEPENDING ON "parms.type" OF MESSAGE
      
      //
      // Set the front/back value for non-lesson emails
      //
      if (!parms.type.startsWith( "lesson" )) {       // if not a lesson email (fb = proid for lessons)

         f_b = "Front";
         if (fb == 1) f_b = "Back";
      }
      
      //
      //   Does this club wish to hide the f/b?
      //
      if (skipFrontBack == true) f_b = "";


      String ext = "";
      String mod = "";
      String enew = "";
      String can = "";
      String ewait = "";
      String subject = "";
      String dayShort = "";

      //
      //  Get short version of day name
      //
      if (parms.day.equalsIgnoreCase( "Sunday" )) {
        
         dayShort = "Sun ";
           
      } else if (parms.day.equalsIgnoreCase( "Monday" )) {

         dayShort = "Mon ";

      } else if (parms.day.equalsIgnoreCase( "Tuesday" )) {

         dayShort = "Tue ";

      } else if (parms.day.equalsIgnoreCase( "Wednesday" )) {

         dayShort = "Wed ";

      } else if (parms.day.equalsIgnoreCase( "Thursday" )) {

         dayShort = "Thu ";

      } else if (parms.day.equalsIgnoreCase( "Friday" )) {

         dayShort = "Fri ";

      } else {

         dayShort = "Sat ";
      }


      //
      //  Congressional - change the course names to include Gold or Blue indications so members will know
      //
      if (club.equals( "congressional" )) {

         if (!parms.course.equals( "" )) {     // if course specified
           
            parms.course = congressionalCustom.getFullCourseName(date, dd, parms.course);
         }  
         
         if (!parms.to_course.equals( "" )) {     // if course specified

            parms.to_course = congressionalCustom.getFullCourseName(date, dd, parms.to_course);
         }

         if (!parms.from_course.equals( "" )) {     // if course specified

            parms.from_course = congressionalCustom.getFullCourseName(date, dd, parms.from_course);
         }
      }


      if (parms.type.equals( "lottery" )) {
         
         lotteryText = getClub.getLotteryText(con);         // get replacement text for "Lottery" if provided  
        
         if (lotteryText.equals( "" )) lotteryText = "Lottery Request";     // default

         ext = "NOTICE:  This is a Request for a Tee Time, NOT an actual Tee Time.\n\n";

         if (club.equals( "oldoaks" )) {

            mod = "The following Tee Time Request has been MODIFIED by " + author + ".\n\n";
            enew = "The following Tee Time Request has been ENTERED by " + author + ".\n\n";
            subject = "ForeTees Tee Time Request Notification";

         } else if (club.equals( "westchester" )) {

            mod = "The following Draw Request has been MODIFIED by " + author + ".\n\n";
            enew = "The following Draw Request has been ENTERED by " + author + ".\n\n";
            subject = "WCC - Your Lottery Request Has Been Received - This is NOT a Tee Time";

         } else if (club.equals( "pecanplantation" )) {
            
            // TEST to see if using the word Lottery is causing emails to get blocked by Charter.net
            mod = "The following Tee Time Request has been MODIFIED by " + author + ".\n\n";
            enew = "The following Tee Time Request has been ENTERED by " + author + ".\n\n";
            subject = "ForeTees Tee Time Request Notification";
            
         } else {
            
            mod = "The following " +lotteryText+ " has been MODIFIED by " + author + ".\n\n" + ext;
            enew = "The following " +lotteryText+ " has been ENTERED by " + author + ".\n\n" + ext;
            subject = "ForeTees " +lotteryText+ " Notification";
            
         }
         
      } else if (parms.type.equals( "tee" )) {

         if (TLT == 0) {     // if normal tee time system

            can = "The following tee time has been CANCELLED by " + author + ".\n\n";
            mod = "The following tee time has been MODIFIED by " + author + ".\n\n";
            enew = "The following tee time has been RESERVED by " + author + ".\n\n";

         } else {        // TLT system

            can = "The following Notification has been CANCELLED by " + author + ".\n\n";
            mod = "The following Notification has been MODIFIED by " + author + ".\n\n";
            enew = "The following Notification has been SUBMITTED by " + author + ".\n\n";
         }

         if (club.equals( "westchester" )) {

            subject = "WCC - Your Tee Time";

         } else {

            subject = "ForeTees Tee Time Notification (" + dayShort + mm + "/" + dd + "/" + yy + ")";
         }

         
         if (club.equals( "oakmont" )) {       // if Oakmont - custom trailer message for all tee times

            oakmontTrlr = "\n\n\nDear Member," +
                                 "\n\nThe following information is a reminder for you and your guests:" +
                                 "\n\n* The use of all cell phones and electronic communication devices are permitted ONLY in the locker rooms and inside vehicles in the parking lot." +
                                 "\n\n* Members and Guests are expected to play a round of golf in four hours or less." +
                                 "\n\nThank you for following these customs and traditions of Oakmont Country Club, we hope to enhance everyone's Oakmont golf experience." +
                                 "\n\nThe Board of Governors";
         }
         
         if (club.equals( "brooklawn" )) {       // if Brooklawn - custom trailer message for all guest tee times

            brookMsg = "\n\n\nDear Member," +
                        "\n\nPlease remind your guest(s) of the following." +
                        "\n\nDress code for the Golf Course and the Driving Range:" +
                        "\n\n    The Following apply to adults and children of any age on the golf course, the practice putting " +
                        "green and the practice range: Blue jeans of any style, short-shorts or mini-skirts, cut-offs, tank tops, " +
                        "bathing suits, cargo pants and shorts, tennis apparel, or men�s and boys� shirts without collars are not permitted." +
                        "\n\n    Shirttails must be tucked in. Bermuda length shorts are acceptable. Only spike less golf shoes and " +
                        "rubber soled shoes are permitted. Gentlemen are not permitted to wear hats in the clubhouse except in the pro shop " +
                        "and locker room areas. Caps must be worn bill forward." +
                        "\n\nPlease repair your divots, fix your ball marks on the greens, throw your tees in the rough and rake your bunkers." +
                        "\n\nYou are expected to play in less than four hours.  Failure to do so will result in the sponsoring member(s) " +
                        "receiving a pace of play letter which may lead to a suspension of their golfing privileges." +
                        "\n\nThank you for reminding your guests and fellow members." +
                        "\n\nThe Golf Committee";
         
            teetimeMsg = "\n\n\nPlease remember to always fill-in your divots and repair your ball marks on the greens." +
                          "\n\nThank you,  " +
                          "\nThe Golf Committee";
         }

         if (club.equals( "tcclub" )) {       // if The CC - custom trailer message for all tee times

            teetimeMsg = "\n\n\n *  <b>The use of cell phones is prohibited on property.</b>  Your cell phone can be used in your car and in the " +
                          "phone booth located in the men�s locker room. The member will be fined $100 if a cell phone is used." +
                          "\n\n *  For men and boys, collared shirts (tucked in) and shorts are permissible (no more than 3 to 4 inches above the knee). " +
                          "Short sleeve mock shirts and cargo shorts are prohibited." +
                          "\n\n *  For women and girls, conventional blouses and tee shirts (not sloganned) and either pants, shorts or skirts " +
                          "(no more than 3 to 4 inches above the knee) are permissible." +
                          "\n\n *  Bag Drop is behind the Brick Bldg at the end of the circle." +
                          "\n\n *  The Country Club is a walking facility." +
                          "\n\n *  We ask that all participants have carry bags that are suitable for caddies." +
                          "\n\n *  Groups of four are expected to play in four hours or less. If your group falls behind, the golf staff will " +
                          "assist you back into position by giving rides in a golf cart." +
                          "\n\n Please feel free to pass this along to your guests.";
         }


         if (club.equals( "rhodeisland" )) {       // if Rhode Island CC - custom trailer message for all tee times

            teetimeMsg = "\n\n\nPlease advise your guests of our dress code and cell phone policies.  No cargo shorts " +
                         "or tee-shirts and all men's shirts must be completely tucked in.";
         }         
      
         
         if (club.equals( "santaana" )) {       // if Santa Ana CC - custom trailer message for all tee times

            teetimeMsg = "\n\n\nAttention Members:\n\nAs a courtesy to the other Members, if you will NOT be using your Tee Time, " +
                         "please CANCEL the Tee TIME in a timely fashion.\nYou May cancel on ForeTees or contact the Starter.\n " +
                         "THANK YOU!";
         }         
      
         
         if (club.equals( "baltusrolgc" )) {       // if Baltusrol GC - custom trailer message for all tee times

            teetimeMsg = "\n\n\nFor guest regulations, dress code information and directions to Baltusrol Golf Club, please visit www.baltusrol.org ";
         }         
      
         
         if (club.equals( "midpacific" )) {       // if Mid Pacific - custom trailer message for all tee times

            teetimeMsg = "\n\n\nIf you can't play, please don't forget to cancel your reservations. Your fellow members will appreciate your thoughtfulness. Thank you.";
         }         
      
         
         if (club.equals( "congressional" )) {       // if Congressional CC - custom trailer message for all tee times

            teetimeMsg = "\n\n\nWelcome to Congressional and we hope you enjoy your round of golf. Playing golf at " +
                         "Congressional at a reasonable pace is consistent with the traditions of the game, the traditions of " +
                         "Congressional and demonstrates the proper respect for the others on the course." +
                         "\n\nAll groups playing at Congressional are expected to meet our Pace of Play standard of:" +
                         "\n    o  <b>4 hours and 15 minutes or less for a foursome</b>" +
                         "\n    o  <b>3 hours or less for a twosome</b>" +
                         "\n\nThe following Pace of Play Recommendations are provided to help everyone meet this standard:" +
                         "\n\n<b>Be aware of where you are at all times relative to the group in front of you and the " +
                         "\nclock. In a four hour and fifteen minutes round you should achieve the following checkpoints:</b>" +
                         "\n\n    o  Reach 5th tee in one hour." +
                         "\n    o  Reach 11th tee in two hours and 10 minutes." +
                         "\n    o  Reach 14th tee in two hours and 45 minutes." +
                         "\n\n  * <b>1st Tee Practices:</b>" +
                         "\n\n    o  Meet your caddie and encourage him to help you maintain the proper pace of play." +
                         "\n    o  Do not commence your round until the group in front reaches the first green." +
                         "\n    o  Mulligans are not permitted after the first tee shot." +
                         "\n    o  Play from the appropriate tees - guests with a handicap of 10 or higher may not play from the back tees." +
                         "\n\n  * <b>Be prepared to play at all times.</b>" +
                         "\n\n    o  Be prepared to tee off." +
                         "\n    o  Repair ball marks and prepare to putt while others are playing their ball." +
                         "\n    o  Do not linger on the green; mark your scorecard on the next tee." +
                         "\n\n  * <b>Play smart golf.</b>" +
                         "\n\n    o  Invite faster groups to play through." +
                         "\n    o  Watch other players' shots in order to minimize the need to search for lost balls." +
                         "\n    o  Be ready to hit when it is your turn and feel free to play ready golf." +
                         "\n    o  Always rake your own fairway bunker and replace your own divot." +
                         "\n    o  <b>If you have reached your maximum allowable score, pickup.</b> " +
                         "\n\n<b>For everyone's convenience please park your cart behind the green, ideally between " +
                         "your green and the next tee box." +
                         "\nWhen you arrive at a hole with the Pace of Play clock, the clock should read your starting time. " +
                         "If the clock is behind your starting time, the amount of time the clock is behind your starting time " +
                         "is the amount of time your group is behind pace.  For instance if your group's tee time was 8:00am and your " +
                         "group arrives at number 3 Gold when the clock reads 8:10, then your group is 10 minutes behind.  Please " +
                         "note, the clocks are set at a 4 hour pace.</b>" +
                         "\n\n<b>While the perfect round has yet to be played, playing golf slowly can lead to a " +
                         "disappointing experience for everyone on the course. Please pay attention to your " +
                         "pace of play and enjoy your round on our national treasure.</b>" +
                         "\n\nDRESS CODE" +
                         "\n\nProper attire is required at all times in the common areas, the golf areas and around the clubhouse. The Board " +
                         "of Governors and club management has made general adherence to the dress code a priority for the golf staff. The golf " +
                         "staff is asked to withhold playing privileges for members or their guests who are not properly attired." +
                         "\n\nThe dress code applies to all golfers of all ages. The dress code at Congressional CC pertains to all golfing areas " +
                         "at all times. This includes the golf course, practice range, practice pitching and chipping areas, and putting greens. " +
                         "This code is as follows: All men must have a collared shirt that is in good taste and designed for golf. All shirts must " +
                         "be tucked into slacks or shorts. ***Jeans or cargo pants or shorts or tennis attire including designer pants and shorts " +
                         "with side pockets are not permitted.*** Conforming shorts designed for golf which stop at the knee and are no shorter than " +
                         "5 inches above the knee are acceptable. All hats must be worn forward at all times. Congressional CC is a non-metal spike " +
                         "facility. Juniors must abide by the adult dress code at all times." +
                         "\n\nLadies may wear collarless shirts as long as they are conservatively tailored. String, halter-tops and tank tops are " +
                         "not allowed. Tennis dresses or shorts below the knee or shorter than 5 inches above the knee will not be permitted. Jeans " +
                         "or cargo pants or shorts or tennis attire including designer pants and shorts with side pockets are not permitted. Our " +
                         "policy has always been to encourage our members and guests, if in doubt, to err on the side of conservatism. Please check " +
                         "before arrival to avoid embarrassment to yourself or your guests." +
                         "\n\nThe Board of Governors " +
                         "\nCongressional Country Club";
         }
      
            
      
      } else if (parms.type.equals( "frost" )) {      // if Frost Delay

         if (TLT == 0) {     // if normal tee time system

            mod = "The following tee time has been Modified by the Golf Shop Staff because of a Weather Related Delay (please check new time below).\n\n";

         } else {        // TLT system

            mod = "There is a weather related delay today.  You may want to check for changes.\n\n";
         }

         if (club.equals( "westchester" )) {

            subject = "WCC - Frost Delay Notification";

         } else {

            subject = "ForeTees - Weather Delay Notification";
         }
         
      } else if (parms.type.equals( "event" )) {

         can = "The following Event Registration has been CANCELLED by " + author + ".\n\n";
         mod = "The following Event Registration has been MODIFIED by " + author + ".\n\n";
         enew = "The following Event Registration has been RESERVED by " + author + ".\n\n";
         ewait = "You, or your team, have been taken off the WAIT LIST and are now registered for the following event:\n\n";
           
         if (club.equals( "westchester" )) {

            subject = "WCC - Event Registration Notification";

         } else {

            subject = "ForeTees Event Registration Notification";
         }
         
      } else if (parms.type.equals( "lesson" )) {
         
         if (!parms.activity_name.equals("")) {

            subject = "FlxRez " + parms.activity_name + " Lesson Notification";
            enew = parms.player1 + " has been scheduled for the following " + parms.activity_name + " Lesson (by " + author + ").\n\n";
            can = parms.player1 + "'s " + parms.activity_name + " Lesson has been cancelled by " + author + ".\n\n";

         } else {
             
            subject = "ForeTees Golf Lesson Notification";
            enew = parms.player1 + " has been scheduled for the following Golf Lesson (by " + author + ").\n\n";
            can = parms.player1 + "'s Golf Lesson has been cancelled by " + author + ".\n\n";

         }

         if (club.equals( "westchester" )) {

            subject = "WCC - Golf Lesson Notification";
         }
         
      } else if (parms.type.equals( "lessongrp" )) {

         enew = parms.player1 + " has been added to the following Group Lesson (by " + author + ").\n\n";

         if (club.equals( "westchester" )) {

            subject = "WCC - Group Lesson Notification";

         } else {

            subject = "ForeTees Group Lesson Notification";
         }
         
      } else if (parms.type.equals( "moveWhole" )) {       // if from edit tee sheet (move whole tee time)

         if (club.equals( "westchester" )) {

            subject = "WCC - Tee Time Has Changed";

         } else {

            if (TLT == 0) {     // if normal tee time system

               subject = "ForeTees - Notification Has Changed";

            } else {        // TLT system

               subject = "ForeTees - Tee Time Has Changed";
            }
         }
         
      } else if (parms.type.equals( "waitlist" )) {
          
         can = "The following Wait List Sign-up has been CANCELLED by " + author + ".\n\n";
         mod = "The following Wait List Sign-up has been MODIFIED by " + author + ".\n\n";
         enew = "The following Wait List Sign-up has been CREATED by " + author + ".\n\n";
         //ewait = "You, or your team, have been taken off the WAIT LIST and are now registered for the following event:\n\n";
           
         subject = "ForeTees - Wait List Sign-up Notification";
          
      } else if (parms.type.equals( "password" )) {       // if from Login (password request)

         subject = "ForeTees - Credentials";
         
      } else if (parms.type.equals( "drequest" )) {       // if from Dining Request
          
         can = "The following Dining Request has been cancelled by " + author + ".\n\n";
         mod = "The following Dining Request has been modified by " + author + ".\n\n";
         enew = "The following Dining Request has been created by " + author + ".\n\n";
         
         subject = "ForeTees - Dining Request";
         
      } else if (parms.type.equals( "activity" )) {
          
         can = "The following " + parms.activity_name + " reservation has been CANCELLED by " + author + ".\n\n";
         mod = "The following " + parms.activity_name + " reservation has been MODIFIED by " + author + ".\n\n";
         enew = "The following " + parms.activity_name + " reservation has been CREATED by " + author + ".\n\n";

         subject = "FlxRez - " + parms.activity_name + " Reservation";

      }
      
      
      if (!clubName.equals( "" ) && !parms.type.equals("drequest")) {

         subject = subject + " - " + clubName;
      }


      if (club.equals( "tcclub" )) {     // The Country Club of Brookline
      
         can = can + "*** NOTE: If you have any questions please email Kim Hall at khall@tcclub.org\n\n";
         mod = mod + "*** NOTE: If you have any questions please email Kim Hall at khall@tcclub.org\n\n";
         enew = enew + "*** NOTE: If you have any questions please email Kim Hall at khall@tcclub.org\n\n";
      }

      //
      // Message subject and new,mod,can are set
      
        
      
      
      
      //
      // ADD RECIPIENTS TO THE ARRAY LISTS DEPENDING ON MESSAGE TYPE
      //
      if (parms.type.equals("drequest")) {


          // Gather dining request emails
          Statement stmtdreq = null;
          ResultSet rsdreq = null;

          try {
              
             stmtdreq = con.createStatement();
             rsdreq = stmtdreq.executeQuery("SELECT address FROM dining_emails");

             while (rsdreq.next()) {
                 eaddrTo.add(new ArrayList<String>());
                 eaddrTo.get(eaddrTo.size() - 1).add(rsdreq.getString("address"));
                 eaddrTo.get(eaddrTo.size() - 1).add("");                  // no username to include
                 //message.addRecipient(Message.RecipientType.TO, new InternetAddress(rsdreq.getString("address")));
             }

             stmtdreq.close();
             
             replyTo = parms.email;

             StringBuffer vCalMsg = new StringBuffer();  // no vCal for password emails

             doSending(eaddrTo, eaddrProCopy, replyTo, subject, parms.message, vCalMsg, parms, con);

             send = 0;
             
             /*
              * 
              * SET THE REPLY TO VAR AND WE'LL PASS IT TO THE 
              
             InternetAddress dreqreplyto[] = new InternetAddress[1];               // create replyto array
             dreqreplyto[0] = new InternetAddress(parms.email);
             message.setReplyTo(dreqreplyto);

             message.setText(enew + parms.message);
             
             Transport.send(message);
             */

          } catch (Exception e1) {
              
              Utilities.logError("DinReq Error in sendEmail.sendIt() for " + club + ": " + e1.getMessage() + ", " + e1.toString());

          } finally {

              try { rsdreq.close(); }
              catch (SQLException ignored) {}

              try { stmtdreq.close(); }
              catch (SQLException ignored) {}
          }


      } else if (parms.type.startsWith( "lesson" )) {       // if a lesson type


         //
         //  Get the name of the pro for this lesson
         //
         try {

            pstmte1 = con.prepareStatement (
                    "SELECT lname, fname, mi, suffix, email1, email2, ical1, ical2 FROM lessonpro5 WHERE id = ?");

            pstmte1.clearParameters();        // clear the parms
            pstmte1.setInt(1, fb);
            rs = pstmte1.executeQuery();      // execute the prepared pstmt

            if (rs.next()) {

               StringBuffer pro_name = new StringBuffer(rs.getString("fname"));  // get first name

               String mi = rs.getString("mi");                   // middle initial
               if (!mi.equals( "" )) {
                  pro_name.append(" ");
                  pro_name.append(mi);
               }
               pro_name.append(" " + rs.getString("lname"));     // last name

               String suffix = rs.getString("suffix");           // suffix
               if (!suffix.equals( "" )) {
                  pro_name.append(" ");
                  pro_name.append(suffix);
               }

               proName = pro_name.toString();                    // convert to one string

               proEmail1 = rs.getString("email1");               // get email addresses                           
               proEmail2 = rs.getString("email2");

               // if they pro wants ical attachments let's append an astrik to the end (TEMP)
               if (rs.getInt("ical1") == 1 && !proEmail1.equals("")) proEmail1 += "*";
               if (rs.getInt("ical2") == 1 && !proEmail2.equals("")) proEmail2 += "*";

               if (!proEmail1.equals( "" )) {
                   eaddrProCopy.add(proEmail1);
                   send = 1;
               }
               
               if (!proEmail2.equals( "" )) {

                   eaddrProCopy.add(proEmail2);
                   send = 1;
               }
               
            }
            pstmte1.close();

         } catch (Exception exc) { }
         
         //
         // Add the member to this lesson email
         //
         if (!parms.suppressMemberEmails && !userA[0].equals( "" )) {       // if user exists

            try {
               pstmte1 = con.prepareStatement ( tmp_sql );

               pstmte1.clearParameters();        // clear the parms
               pstmte1.setString(1, userA[0]);
               pstmte1.setString(2, userA[0]);
               pstmte1.setString(3, userA[0]);
               rs = pstmte1.executeQuery();      // execute the prepared stmt

               if (rs.next()) {

                  to = rs.getString(1);         // user's email address
                  emailOpt = rs.getInt(2);      // email option
                  to2 = rs.getString(3);        // user's 2nd email address

                  if (emailOpt != 0) {    // if user wants email notifications
                    
                     if (!to.equals( "" )) {
                         
                         eaddrTo.add(new ArrayList<String>());
                         eaddrTo.get(eaddrTo.size() - 1).add(to);
                         eaddrTo.get(eaddrTo.size() - 1).add(userA[0]);
                         send = 1;
                     }
                     if (!to2.equals( "" )) {     // if 2nd email address
                         
                         eaddrTo.add(new ArrayList<String>());
                         eaddrTo.get(eaddrTo.size() - 1).add(to2);
                         eaddrTo.get(eaddrTo.size() - 1).add(userA[0]);
                         send = 1;
                     }
                  }
               }
               pstmte1.close();              // close the stmt
            } catch (Exception ignore) { }
         }


      }  else if (parms.type.equals( "password" )) {       // if from Login (password request)


         //
         //  Create the message content
         //
         String pwMsg = header + "The following password is for " + author + ".\n\n";

         pwMsg = pwMsg + "Password = " +parms.password+ "\n\n";
         pwMsg = pwMsg + "Thank you for using ForeTees!\n\n";

         eaddrTo.add(new ArrayList<String>());
         eaddrTo.get(eaddrTo.size() - 1).add(parms.email);   // add the members email address here
         eaddrTo.get(eaddrTo.size() - 1).add("");            // no username will surpress the subscribe link in email)

         try {

            /*
            message.addRecipient(Message.RecipientType.TO, new InternetAddress( parms.email ));
            message.setText( pwMsg );  // put msg in email text area
            Transport.send(message);     // send it!!
            */

            StringBuffer vCalMsg = new StringBuffer();  // no vCal for password emails

            doSending(eaddrTo, eaddrProCopy, replyTo, subject, pwMsg, vCalMsg, parms, con);

            send = 0; // should already be zero, but just incase

         } catch (Exception e1) {

            Utilities.logError("Error in sendEmail (password) for " + club + ": " + e1);                                       // log it
         }

         // end of IF password request from Login


      } else {                      // NOT a dinning request, lesson type or password request.


         // THIS REQUEST IS FOR A TEE TIME, EVENT SIGNUP, LOTTERY, WAITLIST OR ACTIVITY


         //
         //  If event - check for any pro emails to copy
         //
         if (parms.type.equals( "event" )) {

              if (!parms.emailpro1.equals( "" )) {      // if pro email address provided

                eaddrProCopy.add(parms.emailpro1);
                send = 1;
              }

              if (!parms.emailpro2.equals( "" )) {      // if pro email address provided

                eaddrProCopy.add(parms.emailpro2);
                send = 1;
              }

              //
              // Custom for MN Valley CC - send email to pro on mods or cancels of event registrations (Case #1353)
              //
              if ((emailMod == 1 || emailCan == 1) && club.equals( "mnvalleycc" )) {

                 eaddrProCopy.add("rhary@mvccgolf.com");
                 send = 1;
              }

         } // end if event
         
         
         //
         //   Check all users from the old request (prior to this change)
         //
         for (i=0; i<25; i++) {
  
            if (!olduserA[i].equals( "" )) {      // if old user exists

               try {
                  pstmte1 = con.prepareStatement ( tmp_sql );

                  pstmte1.clearParameters();        // clear the parms
                  pstmte1.setString(1, olduserA[i]);
                  pstmte1.setString(2, olduserA[i]);
                  pstmte1.setString(3, olduserA[i]);
                  rs = pstmte1.executeQuery();      // execute the prepared stmt

                  if (rs.next()) {

                     to = rs.getString(1);              // user's email address
                     emailOpt = rs.getInt(2);           // email option
                     to2 = rs.getString(3);             // user's alternate email address

                     if ( emailOpt != 0 ) {             // if user wants email notifications
                         
                        if ( !to.equals( "" ) ) {
                            
                            eaddrTo.add(new ArrayList<String>());
                            eaddrTo.get(eaddrTo.size() - 1).add(to);
                            eaddrTo.get(eaddrTo.size() - 1).add(olduserA[i]);
                            send = 1;
                        }
                        
                        if (!to2.equals( "" )) {
                            
                            eaddrTo.add(new ArrayList<String>());
                            eaddrTo.get(eaddrTo.size() - 1).add(to2);
                            eaddrTo.get(eaddrTo.size() - 1).add(olduserA[i]);
                            send = 1;
                        }
                     } // end if emailOpt != 0
                  }
                  pstmte1.close();              // close the stmt
                    
                  //
                  //  Custom for Blackhawk CC - BCC the pro
                  //
                  if (club.equals( "blackhawk" )) {   // if Blackhawk CC (Mark Caufield)

                     if (olduserA[i].equals( "3503" ) || olduserA[i].equals( "2853" ) || olduserA[i].equals( "1539" )) {   // if one of specified members 

                        if (send == 1 && BHproCCed == false) {         // if members to send to

                           eaddrProCopy.add(clubProEmail);
                           BHproCCed = true;           // indicate pro CC'ed already
                        }
                     }
                     
                  } // end if blackhawk CC
                  
               } catch (Exception ignore) {}
               
            } else if (oldguest_idA[i] != 0) { // end if old user found

               try {
                  pstmte1 = con.prepareStatement ( tmp_sql_gst );

                  pstmte1.clearParameters();        // clear the parms
                  pstmte1.setInt(1, oldguest_idA[i]);
                  pstmte1.setInt(2, oldguest_idA[i]);
                  pstmte1.setInt(3, oldguest_idA[i]);
                  rs = pstmte1.executeQuery();      // execute the prepared stmt

                  if (rs.next()) {

                     to = rs.getString(1);              // user's email address
                     emailOpt = rs.getInt(2);           // email option
                     to2 = rs.getString(3);             // user's alternate email address

                     if ( emailOpt != 0 ) {             // if user wants email notifications

                        if ( !to.equals( "" ) ) {

                            eaddrTo.add(new ArrayList<String>());
                            eaddrTo.get(eaddrTo.size() - 1).add(to);
                            eaddrTo.get(eaddrTo.size() - 1).add("");    // Do not include any sort of ID at this point
                            send = 1;
                        }

                        if (!to2.equals( "" )) {

                            eaddrTo.add(new ArrayList<String>());
                            eaddrTo.get(eaddrTo.size() - 1).add(to2);
                            eaddrTo.get(eaddrTo.size() - 1).add("");    // Do not include any sort of ID at this point
                            send = 1;
                        }
                     } // end if emailOpt != 0
                  }
                  pstmte1.close();              // close the stmt

               } catch (Exception ignore) {}
            }
            

            //
            //  Custom for Columbia Edgewater CC - add the pro's email if a "Potential Member" guest is in the tee time
            //
            try {
                if (club.equals("cecc")) {

                    if (oldplayerA[i].startsWith("Potential Member") && !CECCproCCed) {

                        eaddrProCopy.add("bryan.tunstill@cecc.com");
                        
                        CECCproCCed = true;       // flag so we know he's already added
                        send = 1;
                    }
                }
            } catch (Exception ignore) { }
            
         } // end i for next loop 
         

         String err = "";
         //
         //  check new players
         //
         for (i=0; i<25; i++) {

            if (!userA[i].equals( "" )) {      // if new user exists

               try {
                   
                  err = "SQL user="+userA[i];

                  pstmte1 = con.prepareStatement ( tmp_sql );

                  pstmte1.clearParameters();        // clear the parms
                  pstmte1.setString(1, userA[i]);
                  pstmte1.setString(2, userA[i]);
                  pstmte1.setString(3, userA[i]);
                  rs = pstmte1.executeQuery();      // execute the prepared stmt

                  if (rs.next()) {

                     to = rs.getString(1);           // user's email address
                     emailOpt = rs.getInt(2);        // email option
                     to2 = rs.getString(3);          // user's alternate email address

                     if (emailOpt != 0) {    // if user wants email notifications

                        err = "to="+to;
                        if (to != null && !to.equals( "" ) ) {
                            
                            eaddrTo.add(new ArrayList<String>());
                            eaddrTo.get(eaddrTo.size() - 1).add(to);
                            eaddrTo.get(eaddrTo.size() - 1).add(userA[i]);
                            send = 1;
                        }

                        err = "to2="+to2;
                        if (to2 != null && !to2.equals( "" )) {
                            
                            eaddrTo.add(new ArrayList<String>());
                            eaddrTo.get(eaddrTo.size() - 1).add(to2);
                            eaddrTo.get(eaddrTo.size() - 1).add(userA[i]);
                            send = 1;
                        }

                     } // end if emailOpt != 0
                  }
                  pstmte1.close();              // close the stmt

               } catch (Exception exc) {
                   
                   Utilities.logError("sendEmail: i="+i+", err=" + err + ", msg="+exc.getMessage()+", str="+exc.toString());
               }
                    
               //
               //  Custom for Blackhawk CC - BCC the pro
               //
               if (club.equals( "blackhawk" )) {   // if Blackhawk CC (Mark Caufield)

                  if (userA[i].equals( "3503" ) || userA[i].equals( "2853" ) || userA[i].equals( "1539" )) {   // if one of specified members

                     if (send == 1 && BHproCCed == false) {         // if members to send to

                        eaddrProCopy.add(clubProEmail);
                        BHproCCed = true;           // indicate pro CC'ed already
                     }
                  }
               } // end if blackhawk cc
               
            } else if (guest_idA[i] != 0) {

                guestsIncluded = true;

                try {

                  err = "SQL user="+userA[i];

                  pstmte1 = con.prepareStatement ( tmp_sql_gst );

                  pstmte1.clearParameters();        // clear the parms
                  pstmte1.setInt(1, guest_idA[i]);
                  pstmte1.setInt(2, guest_idA[i]);
                  pstmte1.setInt(3, guest_idA[i]);
                  rs = pstmte1.executeQuery();      // execute the prepared stmt

                  if (rs.next()) {

                     to = rs.getString(1);           // user's email address
                     emailOpt = rs.getInt(2);        // email option
                     to2 = rs.getString(3);          // user's alternate email address

                     if (emailOpt != 0) {    // if user wants email notifications

                        err = "to="+to;
                        if (to != null && !to.equals( "" ) ) {

                            eaddrTo.add(new ArrayList<String>());
                            eaddrTo.get(eaddrTo.size() - 1).add(to);
                            eaddrTo.get(eaddrTo.size() - 1).add("");    // Do not include any sort of ID at this point
                            send = 1;
                        }

                        err = "to2="+to2;
                        if (to2 != null && !to2.equals( "" )) {

                            eaddrTo.add(new ArrayList<String>());
                            eaddrTo.get(eaddrTo.size() - 1).add(to2);
                            eaddrTo.get(eaddrTo.size() - 1).add("");    // Do not include any sort of ID at this point
                            send = 1;
                        }

                     } // end if emailOpt != 0
                  }
                  pstmte1.close();              // close the stmt

               } catch (Exception exc) {

                   Utilities.logError("sendEmail: i="+i+", err=" + err + ", msg="+exc.getMessage()+", str="+exc.toString());
               }


            } else {    // user not present - check for guest
               
               if (guestsIncluded == false) {      // don't bother checking if guest already found
                  
                  if (!playerA[i].equals( "" ) && !playerA[i].equalsIgnoreCase( "X" )) {  // if player present & NOT X     

                     guestsIncluded = true;
                  }
               }
               
               //
               //  Custom for Columbia Edgewater CC - add the pro's email if a "Potential Member" guest is in the tee time
               //
               try {
                   if (club.equals("cecc")) {

                       if (playerA[i].startsWith("Potential Member") && !CECCproCCed) {

                           eaddrProCopy.add("bryan.tunstill@cecc.com");
                           
                           CECCproCCed = true;       // flag so we know he's already added
                           send = 1;
                       }
                   }
               } catch (Exception ignore) { }

            }

         } // end of FOR loop

         // end else portion of if type = lesson, dinreq, password, event


         //
         //  Wellesley CC - if guests included then add a custom message
         //
         if (club.equals( "wellesley" ) && guestsIncluded == true) {
            
            teetimeMsg = "\n\n\nPlease make sure your guests are familiar with the club�s dress code, cell phone and practice range policy. " +
                    " Enjoy your round of golf with your guests! ";
         }
         

      } // end if parm.type.equals


      //
      // If Winged Foot force send to 1 (incase it wasn't) and at least send it to the pro
      //
      if (club.equals( "wingedfoot" )) {

         eaddrTo.add(new ArrayList<String>());
         eaddrTo.get(eaddrTo.size() - 1).add("dzona@wfgc.org");         // pro's email address
         eaddrTo.get(eaddrTo.size() - 1).add("");
         send = 1;
      }

      
      //
      // DONE ADDING RECIPIENTS 
      




      //
      // IF REQUEST FOR A TEE TIME OR LESSON TIME AND THE CORRESPONDING PROMPT DISPLAY OPTION
      // IS SELECTED IN DINING SYSTEM CONFIGURATION, THEN ADD THE DINING PROMPT/LINK
      //
      if ((parms.type.equals("tee") && Utilities.checkDiningLink("email_teetime", con)) ||
          (parms.type.startsWith("lesson") && Utilities.checkDiningLink("email_lesson", con)) || 
          (parms.type.equals("activity") && Utilities.checkDiningLink("email_activity", con))) {

        Statement stmt = null;
        PreparedStatement pstmt = null;
        String day_name = "0";
        
        if (parms.date != 0) {

            Calendar cal_date = new GregorianCalendar(parms.yy, parms.mm - 1, parms.dd);
            DateFormatSymbols dfs = DateFormatSymbols.getInstance(Locale.US);
            String weekdaysArray[] = dfs.getWeekdays();
            day_name = weekdaysArray[cal_date.get(Calendar.DAY_OF_WEEK)];

        }

        // look up the dining request prompt message and link text
        try {

            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT prompt_text, link_text FROM dining_config");

            if (rs.next()) {

                parms.diningPrompt = rs.getString("prompt_text");
                parms.diningLink = rs.getString("link_text");
            }

            stmt.close();

            pstmt = con.prepareStatement("SELECT id, prompt_text, link_text FROM dining_messages " +
                    "WHERE active=1 AND sdate <= ? AND edate >= ? AND " + day_name + "=1 AND " +
                    "(eo_week = 0 OR (MOD(DATE_FORMAT(sdate, '%U'), 2) = MOD(DATE_FORMAT(?, '%U'), 2))) " +
                    "ORDER BY priority DESC");
            pstmt.clearParameters();
            pstmt.setLong(1, parms.date);
            pstmt.setLong(2, parms.date);
            pstmt.setLong(3, parms.date);
            rs = pstmt.executeQuery();

            if (rs.next()) {

                parms.diningMsgId = rs.getInt("id");

                String temp = rs.getString("prompt_text");
                if (!temp.equals("")) parms.diningPrompt = temp;

                temp = rs.getString("link_text");
                if (!temp.equals("")) parms.diningLink = temp;
            }

        } catch (Exception exc) {

            // do nothing and no dining prompt will be included in email if we error out here
            Utilities.logError("sendIt: Error looking up dining prompt, message & link.  err="+exc.toString());

        } finally {

            try { rs.close(); }
            catch (SQLException ignored) {}

            try { stmt.close(); }
            catch (SQLException ignored) {}

            try { pstmt.close(); }
            catch (SQLException ignored) {}
        }

      } // end if not dining request

              

      //
      //  send email if anyone to send it to
      //
      if (send != 0) {        // this if block spans almost this entire method

         //
         //  set trans mode based on 9-hole options
         //
         if (p91 == 1) parms.pcw1 = parms.pcw1 + "9";
         if (p92 == 1) parms.pcw2 = parms.pcw2 + "9";
         if (p93 == 1) parms.pcw3 = parms.pcw3 + "9";
         if (p94 == 1) parms.pcw4 = parms.pcw4 + "9";
         if (p95 == 1) parms.pcw5 = parms.pcw5 + "9";
         if (p96 == 1) parms.pcw6 = parms.pcw6 + "9";
         if (p97 == 1) parms.pcw7 = parms.pcw7 + "9";
         if (p98 == 1) parms.pcw8 = parms.pcw8 + "9";
         if (p99 == 1) parms.pcw9 = parms.pcw9 + "9";
         if (p910 == 1) parms.pcw10 = parms.pcw10 + "9";
         if (p911 == 1) parms.pcw11 = parms.pcw11 + "9";
         if (p912 == 1) parms.pcw12 = parms.pcw12 + "9";
         if (p913 == 1) parms.pcw13 = parms.pcw13 + "9";
         if (p914 == 1) parms.pcw14 = parms.pcw14 + "9";
         if (p915 == 1) parms.pcw15 = parms.pcw15 + "9";
         if (p916 == 1) parms.pcw16 = parms.pcw16 + "9";
         if (p917 == 1) parms.pcw17 = parms.pcw17 + "9";
         if (p918 == 1) parms.pcw18 = parms.pcw18 + "9";
         if (p919 == 1) parms.pcw19 = parms.pcw19 + "9";
         if (p920 == 1) parms.pcw20 = parms.pcw20 + "9";
         if (p921 == 1) parms.pcw21 = parms.pcw21 + "9";
         if (p922 == 1) parms.pcw22 = parms.pcw22 + "9";
         if (p923 == 1) parms.pcw23 = parms.pcw23 + "9";
         if (p924 == 1) parms.pcw24 = parms.pcw24 + "9";
         if (p925 == 1) parms.pcw25 = parms.pcw25 + "9";
           
         //
         //  if player is 'x' then change c/w option to space
         //
         if (parms.player1.equalsIgnoreCase( "x" )) parms.pcw1 = " ";
         if (parms.player2.equalsIgnoreCase( "x" )) parms.pcw2 = " ";
         if (parms.player3.equalsIgnoreCase( "x" )) parms.pcw3 = " ";
         if (parms.player4.equalsIgnoreCase( "x" )) parms.pcw4 = " ";
         if (parms.player5.equalsIgnoreCase( "x" )) parms.pcw5 = " ";
         if (parms.player6.equalsIgnoreCase( "x" )) parms.pcw6 = " ";
         if (parms.player7.equalsIgnoreCase( "x" )) parms.pcw7 = " ";
         if (parms.player8.equalsIgnoreCase( "x" )) parms.pcw8 = " ";
         if (parms.player9.equalsIgnoreCase( "x" )) parms.pcw9 = " ";
         if (parms.player10.equalsIgnoreCase( "x" )) parms.pcw10 = " ";
         if (parms.player11.equalsIgnoreCase( "x" )) parms.pcw11 = " ";
         if (parms.player12.equalsIgnoreCase( "x" )) parms.pcw12 = " ";
         if (parms.player13.equalsIgnoreCase( "x" )) parms.pcw13 = " ";
         if (parms.player14.equalsIgnoreCase( "x" )) parms.pcw14 = " ";
         if (parms.player15.equalsIgnoreCase( "x" )) parms.pcw15 = " ";
         if (parms.player16.equalsIgnoreCase( "x" )) parms.pcw16 = " ";
         if (parms.player17.equalsIgnoreCase( "x" )) parms.pcw17 = " ";
         if (parms.player18.equalsIgnoreCase( "x" )) parms.pcw18 = " ";
         if (parms.player19.equalsIgnoreCase( "x" )) parms.pcw19 = " ";
         if (parms.player20.equalsIgnoreCase( "x" )) parms.pcw20 = " ";
         if (parms.player21.equalsIgnoreCase( "x" )) parms.pcw21 = " ";
         if (parms.player22.equalsIgnoreCase( "x" )) parms.pcw22 = " ";
         if (parms.player23.equalsIgnoreCase( "x" )) parms.pcw23 = " ";
         if (parms.player24.equalsIgnoreCase( "x" )) parms.pcw24 = " ";
         if (parms.player25.equalsIgnoreCase( "x" )) parms.pcw25 = " ";
           
         String players = "";
               
         //
         //  Process according to the request type
         //
         if (emailNew != 0) {        // if new request
             
             
            //
            //  Create the message content
            //
            String enewMsg = "";

            if (parms.type.startsWith( "lesson" )) {         // if lesson type

                String act_name = "";

                if (!parms.actual_activity_name.equals("")) {
                    act_name = "Loaction: " + parms.actual_activity_name + "  ";
                }

               if (parms.type.equals( "lessongrp" )) {         // if group lesson 

                 enewMsg = header + enew + " Group Lesson: " + parms.course + "    Date: " +parms.day+ ", " +mm+ "/" +dd+ "/" +yy+ 
                           " at " + etime + " With " +proName+ ".";

               } else {              // normal lesson

                 enewMsg = header + enew + " Lesson Type: " + parms.course + "  " + act_name + "  Date: " +parms.day+ ", " +mm+ "/" +dd+ "/" +yy+
                           " at " + etime + " With " +proName+ ".";
               }
                 
            } else {              // tee time or event

               if (parms.type.equals( "event" )) {
                  
                  if (parms.season == 0) {
                      
                      // NOT a season long event
                      enewMsg = header + enew + " Event: " + parms.name + "    Date: " + mm + "/" + dd + "/" + yy + " ";

                      if (!parms.course.equals( "" )) {

                         enewMsg = enewMsg + "on Course: " + parms.course + " ";
                      }

                      if (etype == 1) {                                  // if Shotgun event

                         if (hideTime == false) {        // if ok to include the time of the event

                            enewMsg = enewMsg + "at " + parms.act_time + ".";        // add the event time
                         }
                         
                      } else {          // Tee Time Event

                         enewMsg = enewMsg + "\n\nEvent starts at " + parms.act_time + ".  YOUR ACTUAL START TIME MAY VARY.";
                      }
                  } else {
                      
                      // season long event
                      enewMsg = header + enew + " Event: " + parms.name + "    Date: Season Long ";

                      if (!parms.course.equals( "" )) {

                         enewMsg = enewMsg + "\nCourse: " + parms.course + " ";
                      }
                  }
                  if (wait > 0) {            // if on wait list

                     enewMsg = enewMsg + "\n\nNote:  This team is currently on the WAIT LIST.";
                  }

               } else if (parms.type.equals("waitlist")) {         // wait list signup

                  enewMsg = header + enew + parms.day + " " + mm + "/" + dd + "/" + yy + " from " + etime + " to " + etime2 + " ";
                                    
                  if (!parms.course.equals( "" )) {

                     enewMsg = enewMsg + "on Course: " + parms.course;
                  }
                  
               } else if (parms.type.equals("activity")) {         // activity reservation

                   enewMsg = header + enew + parms.day + " " + mm + "/" + dd + "/" + yy + " at " + etime + " for " + parms.actual_activity_name + " ";

               } else {
                   
                  enewMsg = header + enew + parms.day + " " + mm + "/" + dd + "/" + yy + " at " + etime + " ";

                  if (!f_b.equals ( "" )) {
                    
                     enewMsg = enewMsg + "on the " + f_b + " 9 of ";
                       
                  } else {

                     enewMsg = enewMsg + "on ";
                  }

                  if (!parms.course.equals( "" )) {

                     enewMsg = enewMsg + "Course: " + parms.course;
                  }
               }

               enewMsg = enewMsg + "\n";
               

               //
               //  build message text
               //
               if (!parms.player1.equals( "" )) {

                  players = players + "\nPlayer 1: " + parms.player1 + "  " + parms.pcw1;
               }
               if (!parms.player2.equals( "" )) {

                  players = players + "\nPlayer 2: " + parms.player2 + "  " + parms.pcw2;
               }
               if (!parms.player3.equals( "" )) {

                  players = players + "\nPlayer 3: " + parms.player3 + "  " + parms.pcw3;
               }
               if (!parms.player4.equals( "" )) {

                  players = players + "\nPlayer 4: " + parms.player4 + "  " + parms.pcw4;
               }
               if (!parms.player5.equals( "" )) {

                  players = players + "\nPlayer 5: " + parms.player5 + "  " + parms.pcw5;
               }
                              
               if (!etime2.equals("") && !parms.type.equals("waitlist")) { // (etime2 is also used for waitlist)
                  
                  players = players + "\n\n" + etime2;
               }
               
               if (!parms.player6.equals( "" )) {

                  players = players + "\nPlayer 6: " + parms.player6 + "  " + parms.pcw6;
               }
               if (!parms.player7.equals( "" )) {

                  players = players + "\nPlayer 7: " + parms.player7 + "  " + parms.pcw7;
               }
               if (!parms.player8.equals( "" )) {

                  players = players + "\nPlayer 8: " + parms.player8 + "  " + parms.pcw8;
               }
               if (!parms.player9.equals( "" )) {

                  players = players + "\nPlayer 9: " + parms.player9 + "  " + parms.pcw9;
               }
               if (!parms.player10.equals( "" )) {

                  players = players + "\nPlayer 10: " + parms.player10 + "  " + parms.pcw10;
               }
               
               if (!etime3.equals("")) {
                  
                  players = players + "\n\n" + etime3;
               }
               
               if (!parms.player11.equals( "" )) {

                  players = players + "\nPlayer 11: " + parms.player11 + "  " + parms.pcw11;
               }
               if (!parms.player12.equals( "" )) {

                  players = players + "\nPlayer 12: " + parms.player12 + "  " + parms.pcw12;
               }
               if (!parms.player13.equals( "" )) {

                  players = players + "\nPlayer 13: " + parms.player13 + "  " + parms.pcw13;
               }
               if (!parms.player14.equals( "" )) {

                  players = players + "\nPlayer 14: " + parms.player14 + "  " + parms.pcw14;
               }
               if (!parms.player15.equals( "" )) {

                  players = players + "\nPlayer 15: " + parms.player15 + "  " + parms.pcw15;
               }
               
               if (!etime4.equals("")) {
                  
                  players = players + "\n\n" + etime4;
               }
               
               if (!parms.player16.equals( "" )) {

                  players = players + "\nPlayer 16: " + parms.player16 + "  " + parms.pcw16;
               }
               if (!parms.player17.equals( "" )) {

                  players = players + "\nPlayer 17: " + parms.player17 + "  " + parms.pcw17;
               }
               if (!parms.player18.equals( "" )) {

                  players = players + "\nPlayer 18: " + parms.player18 + "  " + parms.pcw18;
               }
               if (!parms.player19.equals( "" )) {

                  players = players + "\nPlayer 19: " + parms.player19 + "  " + parms.pcw19;
               }
               if (!parms.player20.equals( "" )) {

                  players = players + "\nPlayer 20: " + parms.player20 + "  " + parms.pcw20;
               }
               
               if (!etime5.equals("")) {
                  
                  players = players + "\n\n" + etime5;
               }
               
               if (!parms.player21.equals( "" )) {

                  players = players + "\nPlayer 21: " + parms.player21 + "  " + parms.pcw21;
               }
               if (!parms.player22.equals( "" )) {

                  players = players + "\nPlayer 22: " + parms.player22 + "  " + parms.pcw22;
               }
               if (!parms.player23.equals( "" )) {

                  players = players + "\nPlayer 23: " + parms.player23 + "  " + parms.pcw23;
               }
               if (!parms.player24.equals( "" )) {

                  players = players + "\nPlayer 24: " + parms.player24 + "  " + parms.pcw24;
               }
               if (!parms.player25.equals( "" )) {

                  players = players + "\nPlayer 25: " + parms.player25 + "  " + parms.pcw25;
               }
            }

            enewMsg = enewMsg + players;
            
            if (parms.type.equals("tee") && club.equals("tcclub")) {
                
              if (!parms.notes.equals( "" ) && parms.hideNotes == 0) {

                 enewMsg = enewMsg + "\n\nNotes: " + parms.notes;        // add notes (case# 1406)
              }
            }
        
            
            
            
            
            String tmp_course = "";
            String tmp_time = date + "T" + ((time < 1000) ? "0" + time : time) + "00";
            
            if (!parms.course.equals("")) tmp_course = "Course: " + parms.course + "\\n";
            
            StringBuffer vCalMsg = new StringBuffer();       // use string buffer to build file

            // do not send iCal attachments for season long events or for wait list signups
            if (parms.season == 0 && !parms.type.equals( "waitlist" ) && !parms.type.equals( "lottery" )) {
                
                String tmp_summary = etime + " Tee Time"; // default to tee time and change accordingly
                
                if (!parms.course.equals("")) tmp_summary += " on " +parms.course;    // add course name if exists
                
                String tmp_description = tmp_course + players.replace("\n", "\\n");
                
                if (parms.type.equals( "event" )) {
                    
                    StringTokenizer tok = new StringTokenizer( parms.act_time, ": " );     // space is the default token

                    String shr = tok.nextToken();
                    String smin = tok.nextToken();
                    String ampm = tok.nextToken();
                    int hr = 0;
                    int min = 0;
                    
                    try {
                       hr = Integer.parseInt(shr);
                       min = Integer.parseInt(smin);
                    } catch (Exception ignore) { }
                    
                    if (ampm.equalsIgnoreCase ( "PM" ) && hr != 12) hr += 12;
                    
                    int event_time = (hr * 100) + min;
                    
                    tmp_time = yy + ensureDoubleDigit(mm) + ensureDoubleDigit(dd) + "T" + ((event_time < 1000) ? "0" + event_time : event_time) + "00";
                    
                    tmp_summary = parms.act_time + " Event";
                    
                    tmp_description = parms.name + "\n"; // + tmp_course;
                    tmp_description += "You are " + ((wait > 0) ? "on the wait list" : "registered") + " for this event.";
                    
                } else if (parms.type.equals( "lesson" )) {
                    
                    tmp_description = etime + " Lesson with " +proName+ ".";
                    
                    tmp_summary = parms.act_time + " Lesson";
                    
                } else if (parms.type.equals( "lessongrp" )) {
                    
                    tmp_description = etime + " Group Lesson with " +proName+ ".";
                    
                    tmp_summary = parms.act_time + " Lesson Group";

                } else if (parms.type.equals( "activity" )) {

                    tmp_description = players.replace("\n", "\\n");

                    tmp_summary = etime + " " + parms.activity_name + " Reservation";

                }
                
                
                // TODO: wrap descriptions at 75 bytes
                vCalMsg.append("" +
                    "BEGIN:VCALENDAR\n" +
                    "PRODID:-//ForeTees//NONSGML v1.0//EN\n" +
                    "METHOD:PUBLISH\n" +
                    "BEGIN:VEVENT\n" +
                    "DTSTAMP:" + DTSTAMP + "\n" +
                    "DTSTART:" + tmp_time + "\n" +
                    "SUMMARY:" + tmp_summary + "\n" +
                    "LOCATION:" + clubName + "\n" + 
                    "DESCRIPTION:" + tmp_description + "\n" +
                    "URL:http://www1.foretees.com/" + club + "\n" +
                    "END:VEVENT\n" +
                    "END:VCALENDAR");
            }
            
            
            
            
            
            
            strbfr = new StringBuffer(enewMsg);       // get email message

            if (club.equals( "oakmont" )) {           // if Oakmont

               if (!oakmontTrlr.equals("")) {
                  
                  strbfr.append(oakmontTrlr);         // appeand Oakmont's custom mesage
               }
            }

            if (parms.type.equals("tee")) {          // if Tee Time notification
                
               if (club.equals("brooklawn")) {
                
                 if (guestsIncluded == true) {           // if any guests in tee time

                    strbfr.append(brookMsg);             // add custom guest message

                 } else {

                    strbfr.append(teetimeMsg);             // add custom guest message
                 }
                 
               } else {     // all other clubs
                  
                  //
                  //   If custom tee time message exist, appeand it now
                  //
                  if (!teetimeMsg.equals("")) {
                     
                     strbfr.append(teetimeMsg);             // add custom guest message
                  }
               }
            }

            strbfr.append("\n\n");                    // appeand blank lines

            //enewMsg = strbfr.toString();              // convert to one string                  

            doSending(eaddrTo, eaddrProCopy, replyTo, subject, strbfr.toString(), vCalMsg, parms, con);
            
            

         } else if (emailCan != 0) {        // if Cancelled reservation


            //
            //  Create the message content
            //
            String ecanMsg = "";
  
            if (parms.type.startsWith( "lesson" )) {         // if lesson type

               ecanMsg = header + can + " Lesson Type: " + parms.course + "    Date: " +parms.day+ ", " +mm+ "/" +dd+ "/" +yy+
                           " at " + etime + " With " +proName+ ".";

            } else {              // tee time or event

               if (parms.type.equals( "event" )) {

                  if (etype == 1 && hideTime == false) {             // if shotgun event, show time of event

                      ecanMsg = header + can + " Event: " + parms.name + "    Date: " + mm + "/" + dd + "/" + yy +
                                    " at " + parms.act_time + " ";

                  } else {

                     ecanMsg = header + can + " Event: " + parms.name + "    Date: " + mm + "/" + dd + "/" + yy + " ";
                  }

                  if (!parms.course.equals( "" )) {

                     ecanMsg = ecanMsg + "on Course: " + parms.course;
                  }

               }  else if (parms.type.equals("waitlist")) {         // wait list signup

                   ecanMsg = header + can + parms.day + " " + mm + "/" + dd + "/" + yy + " from " + etime + " to " + etime2 + " ";

                   if (!parms.course.equals( "" )) {

                      ecanMsg = ecanMsg + "on Course: " + parms.course;
                   }

               } else if (parms.type.equals("activity")) {          // activity reservation

                   ecanMsg = header + can + parms.day + " " + mm + "/" + dd + "/" + yy + " at " + etime + " for " + parms.actual_activity_name + " ";

               } else {                                             // tee time

                  ecanMsg = header + can + parms.day + " " + mm + "/" + dd + "/" + yy + " at " + etime + " ";

                  if (!f_b.equals ( "" )) {

                     ecanMsg = ecanMsg + "on the " + f_b + " 9 of ";

                  } else {

                     ecanMsg = ecanMsg + "on ";
                  }

                  if (!parms.course.equals( "" )) {

                     ecanMsg = ecanMsg + "Course: " + parms.course;
                  }
               }

               ecanMsg = ecanMsg + "\n";

               //
               //  build message text
               //
               if (!parms.oldplayer1.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 1: " + parms.oldplayer1 + "  " + parms.oldpcw1;
               }
               if (!parms.oldplayer2.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 2: " + parms.oldplayer2 + "  " + parms.oldpcw2;
               }
               if (!parms.oldplayer3.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 3: " + parms.oldplayer3 + "  " + parms.oldpcw3;
               }
               if (!parms.oldplayer4.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 4: " + parms.oldplayer4 + "  " + parms.oldpcw4;
               }
               if (!parms.oldplayer5.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 5: " + parms.oldplayer5 + "  " + parms.oldpcw5;
               }
               if (!parms.oldplayer6.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 6: " + parms.oldplayer6 + "  " + parms.oldpcw6;
               }
               if (!parms.oldplayer7.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 7: " + parms.oldplayer7 + "  " + parms.oldpcw7;
               }
               if (!parms.oldplayer8.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 8: " + parms.oldplayer8 + "  " + parms.oldpcw8;
               }
               if (!parms.oldplayer9.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 9: " + parms.oldplayer9 + "  " + parms.oldpcw9;
               }
               if (!parms.oldplayer10.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 10: " + parms.oldplayer10 + "  " + parms.oldpcw10;
               }
               if (!parms.oldplayer11.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 11: " + parms.oldplayer11 + "  " + parms.oldpcw11;
               }
               if (!parms.oldplayer12.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 12: " + parms.oldplayer12 + "  " + parms.oldpcw12;
               }
               if (!parms.oldplayer13.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 13: " + parms.oldplayer13 + "  " + parms.oldpcw13;
               }
               if (!parms.oldplayer14.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 14: " + parms.oldplayer14 + "  " + parms.oldpcw14;
               }
               if (!parms.oldplayer15.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 15: " + parms.oldplayer15 + "  " + parms.oldpcw15;
               }
               if (!parms.oldplayer16.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 16: " + parms.oldplayer16 + "  " + parms.oldpcw16;
               }
               if (!parms.oldplayer17.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 17: " + parms.oldplayer17 + "  " + parms.oldpcw17;
               }
               if (!parms.oldplayer18.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 18: " + parms.oldplayer18 + "  " + parms.oldpcw18;
               }
               if (!parms.oldplayer19.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 19: " + parms.oldplayer19 + "  " + parms.oldpcw19;
               }
               if (!parms.oldplayer20.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 20: " + parms.oldplayer20 + "  " + parms.oldpcw20;
               }
               if (!parms.oldplayer21.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 21: " + parms.oldplayer21 + "  " + parms.oldpcw21;
               }
               if (!parms.oldplayer22.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 22: " + parms.oldplayer22 + "  " + parms.oldpcw22;
               }
               if (!parms.oldplayer23.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 23: " + parms.oldplayer23 + "  " + parms.oldpcw23;
               }
               if (!parms.oldplayer24.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 24: " + parms.oldplayer24 + "  " + parms.oldpcw24;
               }
               if (!parms.oldplayer25.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 25: " + parms.oldplayer25 + "  " + parms.oldpcw25;
               }
            }

            //strbfr = new StringBuffer(ecanMsg);        // get email message
            
            StringBuffer vCalMsg = new StringBuffer();  // we'll add vCal here once we can reliably replace existing entires
            
            doSending(eaddrTo, eaddrProCopy, replyTo, subject, ecanMsg, vCalMsg, parms, con);
            
            //
            // END IF CANCEL
            
            
         } else if (emailMod != 0) {        // /if tee time modified
             
             
            //
            //  Create the message content
            //
            String emodMsg = "";

            if (parms.type.equals( "event" )) {

               emodMsg = header + mod + " Event: " + parms.name + "    Date: " + mm + "/" + dd + "/" + yy + " ";

               if (!parms.course.equals( "" )) {

                  emodMsg = emodMsg + "on Course: " + parms.course + " ";
               }

               if (etype == 1) {                                  // if Shotgun event

                  if (hideTime == false) {                 // if ok to include the time

                     emodMsg = emodMsg + "at " + parms.act_time + ".";        // add the event time
                  }
                  
               } else {          // Tee Time Event

                   emodMsg = emodMsg + "\n\nEvent starts at " + parms.act_time + ".  YOUR ACTUAL START TIME MAY VARY.";
               }

               if (wait > 0) {            // if on wait list

                  emodMsg = emodMsg + "\n\nNote:  This team is currently on the WAIT LIST.";
               }

            } else if (parms.type.equals("waitlist")) {         // wait list signup

               emodMsg = header + mod + parms.day + " " + mm + "/" + dd + "/" + yy + " from " + etime + " to " + etime2 + " ";

               if (!parms.course.equals( "" )) {

                  emodMsg = emodMsg + "on Course: " + parms.course;
               }

            } else if (parms.type.equals("activity")) {          // activity reservation

               emodMsg = header + mod + parms.day + " " + mm + "/" + dd + "/" + yy + " at " + etime + " for " + parms.actual_activity_name + " ";

            } else {
  
               emodMsg = header + mod + parms.day + " " + mm + "/" + dd + "/" + yy + " at " + etime + " ";

               if (!f_b.equals ( "" )) {

                  emodMsg = emodMsg + "on the " + f_b + " 9 of ";

               } else {

                  emodMsg = emodMsg + "on ";
               }

               if (!parms.from_course.equals( "" )) {

                  emodMsg = emodMsg + "Course: " + parms.to_course;
                 
               } else {

                  if (!parms.course.equals( "" )) {

                     emodMsg = emodMsg + "Course: " + parms.course;
                  }
               }
            }

            if (parms.type.equals( "frost" )) {
               emodMsg = emodMsg + "\n\nGroup:\n";
            } else {
               emodMsg = emodMsg + "\n\nNew Group:\n";
            }

            if (!parms.player1.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 1: " + parms.player1 + "  " + parms.pcw1;
            }
            if (!parms.player2.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 2: " + parms.player2 + "  " + parms.pcw2;
            }
            if (!parms.player3.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 3: " + parms.player3 + "  " + parms.pcw3;
            }
            if (!parms.player4.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 4: " + parms.player4 + "  " + parms.pcw4;
            }
            if (!parms.player5.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 5: " + parms.player5 + "  " + parms.pcw5;
            }
            if (!parms.player6.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 6: " + parms.player6 + "  " + parms.pcw6;
            }
            if (!parms.player7.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 7: " + parms.player7 + "  " + parms.pcw7;
            }
            if (!parms.player8.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 8: " + parms.player8 + "  " + parms.pcw8;
            }
            if (!parms.player9.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 9: " + parms.player9 + "  " + parms.pcw9;
            }
            if (!parms.player10.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 10: " + parms.player10 + "  " + parms.pcw10;
            }
            if (!parms.player11.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 11: " + parms.player11 + "  " + parms.pcw11;
            }
            if (!parms.player12.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 12: " + parms.player12 + "  " + parms.pcw12;
            }
            if (!parms.player13.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 13: " + parms.player13 + "  " + parms.pcw13;
            }
            if (!parms.player14.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 14: " + parms.player14 + "  " + parms.pcw14;
            }
            if (!parms.player15.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 15: " + parms.player15 + "  " + parms.pcw15;
            }
            if (!parms.player16.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 16: " + parms.player16 + "  " + parms.pcw16;
            }
            if (!parms.player17.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 17: " + parms.player17 + "  " + parms.pcw17;
            }
            if (!parms.player18.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 18: " + parms.player18 + "  " + parms.pcw18;
            }
            if (!parms.player19.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 19: " + parms.player19 + "  " + parms.pcw19;
            }
            if (!parms.player20.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 20: " + parms.player20 + "  " + parms.pcw20;
            }
            if (!parms.player21.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 21: " + parms.player21 + "  " + parms.pcw21;
            }
            if (!parms.player22.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 22: " + parms.player22 + "  " + parms.pcw22;
            }
            if (!parms.player23.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 23: " + parms.player23 + "  " + parms.pcw23;
            }
            if (!parms.player24.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 24: " + parms.player24 + "  " + parms.pcw24;
            }
            if (!parms.player25.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 25: " + parms.player25 + "  " + parms.pcw25;
            }

            if (!parms.type.equals( "frost" )) {

               emodMsg = emodMsg + "\n\nPrevious Group (before change):\n";

               if (!parms.oldplayer1.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 1: " + parms.oldplayer1 + "  " + parms.oldpcw1;
               }
               if (!parms.oldplayer2.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 2: " + parms.oldplayer2 + "  " + parms.oldpcw2;
               }
               if (!parms.oldplayer3.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 3: " + parms.oldplayer3 + "  " + parms.oldpcw3;
               }
               if (!parms.oldplayer4.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 4: " + parms.oldplayer4 + "  " + parms.oldpcw4;
               }
               if (!parms.oldplayer5.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 5: " + parms.oldplayer5 + "  " + parms.oldpcw5;
               }
               if (!parms.oldplayer6.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 6: " + parms.oldplayer6 + "  " + parms.oldpcw6;
               }
               if (!parms.oldplayer7.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 7: " + parms.oldplayer7 + "  " + parms.oldpcw7;
               }
               if (!parms.oldplayer8.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 8: " + parms.oldplayer8 + "  " + parms.oldpcw8;
               }
               if (!parms.oldplayer9.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 9: " + parms.oldplayer9 + "  " + parms.oldpcw9;
               }
               if (!parms.oldplayer10.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 10: " + parms.oldplayer10 + "  " + parms.oldpcw10;
               }
               if (!parms.oldplayer11.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 11: " + parms.oldplayer11 + "  " + parms.oldpcw11;
               }
               if (!parms.oldplayer12.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 12: " + parms.oldplayer12 + "  " + parms.oldpcw12;
               }
               if (!parms.oldplayer13.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 13: " + parms.oldplayer13 + "  " + parms.oldpcw13;
               }
               if (!parms.oldplayer14.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 14: " + parms.oldplayer14 + "  " + parms.oldpcw14;
               }
               if (!parms.oldplayer15.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 15: " + parms.oldplayer15 + "  " + parms.oldpcw15;
               }
               if (!parms.oldplayer16.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 16: " + parms.oldplayer16 + "  " + parms.oldpcw16;
               }
               if (!parms.oldplayer17.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 17: " + parms.oldplayer17 + "  " + parms.oldpcw17;
               }
               if (!parms.oldplayer18.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 18: " + parms.oldplayer18 + "  " + parms.oldpcw18;
               }
               if (!parms.oldplayer19.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 19: " + parms.oldplayer19 + "  " + parms.oldpcw19;
               }
               if (!parms.oldplayer20.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 20: " + parms.oldplayer20 + "  " + parms.oldpcw20;
               }
               if (!parms.oldplayer21.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 21: " + parms.oldplayer21 + "  " + parms.oldpcw21;
               }
               if (!parms.oldplayer22.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 22: " + parms.oldplayer22 + "  " + parms.oldpcw22;
               }
               if (!parms.oldplayer23.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 23: " + parms.oldplayer23 + "  " + parms.oldpcw23;
               }
               if (!parms.oldplayer24.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 24: " + parms.oldplayer24 + "  " + parms.oldpcw24;
               }
               if (!parms.oldplayer25.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 25: " + parms.oldplayer25 + "  " + parms.oldpcw25;
               }
            }

            if (parms.type.equals("tee") && club.equals("tcclub")) {
                
              if (!parms.notes.equals( "" ) && parms.hideNotes == 0) {

                 emodMsg = emodMsg + "\n\nNotes: " + parms.notes;        // add notes (case# 1406)
              }
            }
            
            strbfr = new StringBuffer(emodMsg);        // get email message

            
            if (parms.type.equals("tee")) {        // if Tee Time notification
                
               //
               //  check for Custom messages and add to notification
               //
               if (club.equals("brooklawn")) {
                
                  if (guestsIncluded == true) {     // if any guests in tee time

                     strbfr.append(brookMsg);             // add custom guest message
                     
                  } else {
                     
                     strbfr.append(teetimeMsg);             // add custom tee time message
                  }
                  
               } else {                                   // all other clubs - check for msg
                  
                  if (!teetimeMsg.equals("")) {           // if Custom Tee Time msg exists
                     
                     strbfr.append(teetimeMsg);             // add custom tee time message
                  }
               }
            }  
            
            
            
            //
            //   Setup iCal attachment for any new players added to the tee time or event signup
            //
            String tmp_course = "";
            String tmp_time = date + "T" + ((time < 1000) ? "0" + time : time) + "00";
            
            if (!parms.course.equals("")) tmp_course = "Course: " + parms.course + "\\n";
            
            StringBuffer vCalMsg = new StringBuffer();       // use string buffer to build file

            // only send iCal attachments to newly added members and for tee times and events
            if (parms.type.equals("tee") || (parms.season == 0 && parms.type.equals( "event" ))) {
                
                String tmp_summary = etime + " Tee Time"; // default to tee time and change accordingly
                String tmp_description = tmp_course + players.replace("\n", "\\n");
                
                if (parms.type.equals( "event" )) {
                    
                    StringTokenizer tok = new StringTokenizer( parms.act_time, ": " );     // space is the default token

                    String shr = tok.nextToken();
                    String smin = tok.nextToken();
                    String ampm = tok.nextToken();
                    int hr = 0;
                    int min = 0;
                    
                    try {
                       hr = Integer.parseInt(shr);
                       min = Integer.parseInt(smin);
                    } catch (Exception ignore) { }
                    
                    if (ampm.equalsIgnoreCase ( "PM" ) && hr != 12) hr += 12;
                    
                    int event_time = (hr * 100) + min;
                    
                    tmp_time = yy + ensureDoubleDigit(mm) + ensureDoubleDigit(dd) + "T" + ((event_time < 1000) ? "0" + event_time : event_time) + "00";
                    
                    tmp_summary = parms.act_time + " Event";
                    
                    tmp_description = parms.name + "\n"; // + tmp_course;
                    tmp_description += "You are " + ((wait > 0) ? "on the wait list" : "registered") + " for this event.";
                }
                
                
                // TODO: wrap descriptions at 75 bytes
                vCalMsg.append("" +
                    "BEGIN:VCALENDAR\n" +
                    "PRODID:-//ForeTees//NONSGML v1.0//EN\n" +
                    "METHOD:PUBLISH\n" +
                    "BEGIN:VEVENT\n" +
                    "DTSTAMP:" + DTSTAMP + "\n" +
                    "DTSTART:" + tmp_time + "\n" +
                    "SUMMARY:" + tmp_summary + "\n" +
                    "LOCATION:" + clubName + "\n" + 
                    "DESCRIPTION:" + tmp_description + "\n" +
                    "URL:http://www1.foretees.com/" + club + "\n" +
                    "END:VEVENT\n" +
                    "END:VCALENDAR");
            }
            
            
            
            
            //StringBuffer vCalMsg = new StringBuffer();  // we'll add vCal here once we can reliably replace existing entires

            doSending(eaddrTo, eaddrProCopy, replyTo, subject, strbfr.toString(), vCalMsg, parms, con, "modify");

            //
            // END IF MODIFY

            
         } else if (parms.type.equals( "moveWhole" )) {        // if whole tee time moved
             
             
            //
            //  Check for Tee Time moved (from edit tee sheet - Proshop_dsheet)
            //
            String moveMsg = "";

            if (TLT == 0) {          // if Tee Time system
              
               moveMsg = header+ "The following tee time has been MOVED by the Golf Shop (" + author + ").\n\n";

            } else {                 // TLT System

               moveMsg = header+ "The following Notification has been MOVED by the Golf Shop (" + author + ").\n\n";
            }

            moveMsg = moveMsg + parms.day + " " + mm + "/" + dd + "/" + yy;

            //
            //  convert from_time to hour and minutes for email msg
            //
            ehr = from_time / 100;
            emin = from_time - (ehr * 100);
            eampm = " AM";
            if (ehr > 12) {

               eampm = " PM";
               ehr = ehr - 12;       // convert from military time
            }
            if (ehr == 12) {

               eampm = " PM";
            }
            if (ehr == 0) {

               ehr = 12;
               eampm = " AM";
            }

            etime = ehr + ":" + ensureDoubleDigit(emin) + eampm;
               

            f_b = "Front";
            if (from_fb == 1) {
              
               f_b = "Back";
            }

            if (skipFrontBack == true) {     

               moveMsg = moveMsg + "\n\nOriginal time: " + etime + " ";

               if (!parms.from_course.equals( "" )) {

                  moveMsg = moveMsg + "on Course: " + parms.from_course;
               }

            } else {
              
               moveMsg = moveMsg + "\n\nOriginal time: " + etime + " " +
                                "on the " + f_b + " 9 ";

               if (!parms.from_course.equals( "" )) {

                  moveMsg = moveMsg + " of Course: " + parms.from_course;
               }
            }

            //
            //  convert to_time to hour and minutes for email msg
            //
            ehr = to_time / 100;
            emin = to_time - (ehr * 100);
            eampm = " AM";
            if (ehr > 12) {

               eampm = " PM";
               ehr = ehr - 12;       // convert from military time
            }
            if (ehr == 12) {

               eampm = " PM";
            }
            if (ehr == 0) {

               ehr = 12;
               eampm = " AM";
            }

            etime = ehr + ":" + ensureDoubleDigit(emin) + eampm;
               

            f_b = "Front";
            if (to_fb == 1) {

               f_b = "Back";
            }

            if (skipFrontBack == true) {     

               moveMsg = moveMsg + "\n\nNew time: " + etime + " ";

               if (!parms.to_course.equals( "" )) {

                  moveMsg = moveMsg + "on Course: " + parms.to_course;
               }

            } else {

               moveMsg = moveMsg + "\n\nNew time: " + etime + " " +
                                "on the " + f_b + " 9 ";

               if (!parms.to_course.equals( "" )) {

                  moveMsg = moveMsg + " of Course: " + parms.to_course;
               }
            }

            moveMsg = moveMsg + "\n\n";

            //
            //  build message text
            //
            if (!parms.player1.equals( "" )) {

               moveMsg = moveMsg + "\nPlayer 1: " + parms.player1 + "  " + parms.pcw1;
            }
            if (!parms.player2.equals( "" )) {

               moveMsg = moveMsg + "\nPlayer 2: " + parms.player2 + "  " + parms.pcw2;
            }
            if (!parms.player3.equals( "" )) {

               moveMsg = moveMsg + "\nPlayer 3: " + parms.player3 + "  " + parms.pcw3;
            }
            if (!parms.player4.equals( "" )) {

               moveMsg = moveMsg + "\nPlayer 4: " + parms.player4 + "  " + parms.pcw4;
            }
            if (!parms.player5.equals( "" )) {

               moveMsg = moveMsg + "\nPlayer 5: " + parms.player5 + "  " + parms.pcw5;
            }

            //strbfr = new StringBuffer(moveMsg);        // get email message
            
            StringBuffer vCalMsg = new StringBuffer();  // we'll add vCal here once we can reliably replace existing entires
            
            doSending(eaddrTo, eaddrProCopy, replyTo, subject, moveMsg, vCalMsg, parms, con);

         } // end if moveWhole
         
         
         

         //
         //  Now check if any team from the wait list was bumped up to the normal event sign-up list
         //
         if (parms.type.equals( "event" ) && checkWait != 0) {

            try {
              
               if (!parms.wuser1.equals( "" ) || !parms.wuser2.equals( "" ) || !parms.wuser3.equals( "" ) || !parms.wuser4.equals( "" ) || !parms.wuser5.equals( "" )) {

                  subject = "ForeTees Event Registration Notification";

                  if (!clubName.equals( "" )) subject = subject + " - " + clubName;

                  // empty the member email address array (leave the pro one since it will contain the pro's event email (or from customs) if it was found earlier)
                  eaddrTo.clear();

                  // reset init send flag
                  send = 0;

                  //
                  //  Set the recipient addresses
                  //
                  if (!parms.wuser1.equals( "" )) {       // if user exist

                     pstmte1 = con.prepareStatement ( tmp_sql );

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, parms.wuser1);
                     pstmte1.setString(2, parms.wuser1);
                     pstmte1.setString(3, parms.wuser1);
                     rs = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs.next()) {

                        to = rs.getString(1);        // user's email address
                        emailOpt = rs.getInt(2);        // email option
                        to2 = rs.getString(3);        // user's 2nd email address

                        if (emailOpt != 0) {    // if user wants email notifications

                           if (!to.equals( "" )) { 

                               eaddrTo.add(new ArrayList<String>());
                               eaddrTo.get(eaddrTo.size() - 1).add(to);
                               eaddrTo.get(eaddrTo.size() - 1).add(parms.wuser1);
                               send = 1;
                           }
                           if (!to2.equals( "" )) {     // if 2nd email address

                               eaddrTo.add(new ArrayList<String>());
                               eaddrTo.get(eaddrTo.size() - 1).add(to2);
                               eaddrTo.get(eaddrTo.size() - 1).add(parms.wuser1);
                               send = 1;
                           }
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  if (!parms.wuser2.equals( "" )) {       // if user exist

                     PreparedStatement pstmte2 = con.prepareStatement ( tmp_sql );

                     pstmte2.clearParameters();        // clear the parms
                     pstmte2.setString(1, parms.wuser2);
                     pstmte2.setString(2, parms.wuser2);
                     pstmte2.setString(3, parms.wuser2);
                     rs = pstmte2.executeQuery();      // execute the prepared stmt

                     if (rs.next()) {

                        to = rs.getString(1);        // user's email address
                        emailOpt = rs.getInt(2);        // email option
                        to2 = rs.getString(3);        // user's 2nd email address

                        if (emailOpt != 0) {    // if user wants email notifications

                           if (!to.equals( "" )) {

                               eaddrTo.add(new ArrayList<String>());
                               eaddrTo.get(eaddrTo.size() - 1).add(to);
                               eaddrTo.get(eaddrTo.size() - 1).add(parms.wuser2);
                               send = 1;
                           }
                           if (!to2.equals( "" )) {     // if 2nd email address

                               eaddrTo.add(new ArrayList<String>());
                               eaddrTo.get(eaddrTo.size() - 1).add(to2);
                               eaddrTo.get(eaddrTo.size() - 1).add(parms.wuser2);
                               send = 1;
                           }
                        }
                     }
                     pstmte2.close();              // close the stmt
                  }
                  if (!parms.wuser3.equals( "" )) {       // if user exist

                     PreparedStatement pstmte3 = con.prepareStatement ( tmp_sql );

                     pstmte3.clearParameters();        // clear the parms
                     pstmte3.setString(1, parms.wuser3);
                     pstmte3.setString(2, parms.wuser3);
                     pstmte3.setString(3, parms.wuser3);
                     rs = pstmte3.executeQuery();      // execute the prepared stmt

                     if (rs.next()) {

                        to = rs.getString(1);        // user's email address
                        emailOpt = rs.getInt(2);        // email option
                        to2 = rs.getString(3);        // user's 2nd email address

                        if (emailOpt != 0) {    // if user wants email notifications

                           if (!to.equals( "" )) {

                               eaddrTo.add(new ArrayList<String>());
                               eaddrTo.get(eaddrTo.size() - 1).add(to);
                               eaddrTo.get(eaddrTo.size() - 1).add(parms.wuser3);
                               send = 1;
                           }
                           if (!to2.equals( "" )) {     // if 2nd email address

                               eaddrTo.add(new ArrayList<String>());
                               eaddrTo.get(eaddrTo.size() - 1).add(to2);
                               eaddrTo.get(eaddrTo.size() - 1).add(parms.wuser3);
                               send = 1;
                           }
                        }
                     }
                     pstmte3.close();              // close the stmt
                  }
                  if (!parms.wuser4.equals( "" )) {       // if user exist

                     PreparedStatement pstmte4 = con.prepareStatement ( tmp_sql );

                     pstmte4.clearParameters();        // clear the parms
                     pstmte4.setString(1, parms.wuser4);
                     pstmte4.setString(2, parms.wuser4);
                     pstmte4.setString(3, parms.wuser4);
                     rs = pstmte4.executeQuery();      // execute the prepared stmt

                     if (rs.next()) {

                        to = rs.getString(1);        // user's email address
                        emailOpt = rs.getInt(2);        // email option
                        to2 = rs.getString(3);        // user's 2nd email address

                        if (emailOpt != 0) {    // if user wants email notifications

                           if (!to.equals( "" )) {

                               eaddrTo.add(new ArrayList<String>());
                               eaddrTo.get(eaddrTo.size() - 1).add(to);
                               eaddrTo.get(eaddrTo.size() - 1).add(parms.wuser4);
                               send = 1;
                           }
                           if (!to2.equals( "" )) {     // if 2nd email address

                               eaddrTo.add(new ArrayList<String>());
                               eaddrTo.get(eaddrTo.size() - 1).add(to2);
                               eaddrTo.get(eaddrTo.size() - 1).add(parms.wuser4);
                               send = 1;
                           }
                        }
                     }
                     pstmte4.close();              // close the stmt
                  }
                  if (!parms.wuser5.equals( "" )) {       // if user exist

                     PreparedStatement pstmte5 = con.prepareStatement ( tmp_sql );

                     pstmte5.clearParameters();        // clear the parms
                     pstmte5.setString(1, parms.wuser5);
                     pstmte5.setString(2, parms.wuser5);
                     pstmte5.setString(3, parms.wuser5);
                     rs = pstmte5.executeQuery();      // execute the prepared stmt

                     if (rs.next()) {

                        to = rs.getString(1);        // user's email address
                        emailOpt = rs.getInt(2);        // email option
                        to2 = rs.getString(3);        // user's 2nd email address

                        if (emailOpt != 0) {    // if user wants email notifications

                           if (!to.equals( "" )) {

                               eaddrTo.add(new ArrayList<String>());
                               eaddrTo.get(eaddrTo.size() - 1).add(to);
                               eaddrTo.get(eaddrTo.size() - 1).add(parms.wuser5);
                               send = 1;
                           }
                           if (!to2.equals( "" )) {     // if 2nd email address

                               eaddrTo.add(new ArrayList<String>());
                               eaddrTo.get(eaddrTo.size() - 1).add(to2);
                               eaddrTo.get(eaddrTo.size() - 1).add(parms.wuser5);
                               send = 1;
                           }
                        }
                     }
                     pstmte5.close();              // close the stmt
                  }

                  if (send != 0) {      // if recipient found

                     //
                     //  Create the message content
                     //
                     String ewaitMsg = header + ewait + "Event: " + parms.name + "    Date: " + mm + "/" + dd + "/" + yy;
                     
                     if (hideTime == false) {        // if ok to include the time
                        
                        ewaitMsg += " at " + parms.act_time + " ";
                     }

                     if (!parms.course.equals( "" )) {

                        ewaitMsg = ewaitMsg + "on Course: " + parms.course;
                     }

                     ewaitMsg = ewaitMsg + "\n";

                     StringBuffer vCalMsg = new StringBuffer();  // we'll add vCal here once we can reliably replace existing entires

                     doSending(eaddrTo, eaddrProCopy, replyTo, subject, ewaitMsg, vCalMsg, parms, con);

                  } // end if send != 0

               } // end of if one of wuser1-5 not empty

            } catch (Exception e1) {                             // build error msg
               
                Utilities.logError("Error6 in sendEmail (Event CheckWait) for " + club + ": " + e1);                                       // log it
            }

         } // end of IF checkwait
         
      } // end of if send != 0 (if anyone to send it to)
      
   } // end of IF emailOk != 0 (this elseless if block spans most of this method)
   
 } // end of sendIt method
 
 
 //
 // This method will send individual emails to each recipient 
 //
 private static boolean doSending(ArrayList<ArrayList<String>> eaddrTo, ArrayList<String> eaddrProCopy, String replyTo,
                                  String msgSubject, String msgBody, StringBuffer vCalMsg, parmEmail emailParm, Connection con) {

     boolean result = false;         // default to fail
     
     result = doSending(eaddrTo, eaddrProCopy, replyTo, msgSubject, msgBody, vCalMsg, emailParm, con, "none", null);   // callType = "none" when not supplied
     
     return result;
 }

 //
 // This method will send individual emails to each recipient
 //
 private static boolean doSending(ArrayList<ArrayList<String>> eaddrTo, ArrayList<String> eaddrProCopy, String replyTo,
                                  String msgSubject, String msgBody, StringBuffer vCalMsg, parmEmail emailParm, Connection con,
                                  String callType) {

     boolean result = false;         // default to fail

     result = doSending(eaddrTo, eaddrProCopy, replyTo, msgSubject, msgBody, vCalMsg, emailParm, con, callType, null);   // callType = "none" when not supplied

     return result;
 }

 //
 // This method will send individual emails to each recipient 
 //
 // fields must contain a HashTable that contains each BodyPart ready to be attached to the outgoing email
 public static boolean doSending(ArrayList<ArrayList<String>> eaddrTo, ArrayList<String> eaddrProCopy, String replyTo,
                                  String msgSubject, String msgBody, StringBuffer vCalMsg, parmEmail emailParm, Connection con,
                                  String callType, Dictionary fields) {


    //boolean result = false;         // default to fail
    boolean attach_iCal = false;    // default to not include the iCal attachment

    String email_address = "";
    String tmpMsgBody = "";
    String htmlBody = "";
    String diningLink = "";
    String diningLinkHTML = "";
    String unsubscribeLink = "";
    String unsubscribeLinkHTML = "";
    String club = getClub.getClubName(con);

    //
    // Get the SMTP parmaters this club is using
    //
    parmSMTP parm = new parmSMTP();

    try {

        getSMTP.getParms(con, parm);       // get the SMTP parms

    } catch (Exception ignore) {}


/*
    // if this is from the Email Tool then send the pro/member the same copy that the recipients get
    if (emailParm.type.startsWith("EmailTool")) {

        eaddrTo.add(new ArrayList<String>());
        eaddrTo.get(eaddrTo.size() - 1).add(replyTo);
        eaddrTo.get(eaddrTo.size() - 1).add("");

    }
*/

    //
    // Scrub the email address to make sure there are no duplicates
    //
    eaddrProCopy = scrubProCopyList(eaddrProCopy);
    eaddrTo = scrubToList(eaddrTo);

    //
    // Define connection to the mail server
    //
    Properties properties = new Properties();
    Session mailSess;
    
    try {
        
        properties.put("mail.smtp.host", parm.SMTP_ADDR);                           // set outbound host address
        properties.put("mail.smtp.port", parm.SMTP_PORT);                           // set port address
        properties.put("mail.smtp.auth", (parm.SMTP_AUTH) ? "true" : "false");      // attempt to authenticate the user using the AUTH command
        properties.put("mail.smtp.sendpartial", "true");                            // a message has some valid and some invalid addresses, send the message anyway

        //
        // Create a connection to the mail server
        //
        if (parm.SMTP_AUTH) {

            mailSess = Session.getInstance(properties, getAuthenticator(parm.SMTP_USER, parm.SMTP_PASS));

        } else {

            mailSess = Session.getInstance(properties, null);

        }
        
    } catch (Exception exc) {

        Utilities.logError("sendMail.doSending() - Error establishing connection to mail server " + parm.SMTP_ADDR + ":" + parm.SMTP_PORT + " using auth="+parm.SMTP_AUTH);
        return false;
    }


    String title = ((emailParm.IS_TLT) ? "Notification" : "Reservation") + " Confirmation";

    if (emailParm.type.equals("EmailToolPro")) {
        
        title = "Club Communication";
        
    } else if (emailParm.type.equals("EmailToolMem")) {
        
        title = "Member Communication";
        
    }

    // build the basic parts of the html email here since these will be the same for all recipients
    String html_start = "" +
        "<html>" +
        "<head></head>" +
        "<body bgcolor=\"#FFFFFF\"><!-- F5F5DC -->" +
        "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border: 2px solid #225522\" align=\"center\"><tr><td>" +
        "<table width=\"100%\" cellpadding=\"5\" cellspacing=\"0\" border=\"0\">" +
        " <!-- HEADER -->" +
        " <tr>" +
        "  <td bgcolor=\"#CCCCAA\" align=\"left\"><img src=\"http://www1.foretees.com/" + club + "/images/logo.jpg\" height=\"76\" border=\"0\" alt=\"Club Logo\"></td>" +
        "  <td bgcolor=\"#CCCCAA\" align=\"center\"><p style=\"font-family:verdana,arial,helvetica,sans-serif; font-size: 24px; color: #000000; font-weight: bold\">" + title + "</p></td>" +
        "  <td bgcolor=\"#CCCCAA\" align=\"right\" valign=\"middle\"><img src=\"http://www1.foretees.com/v5/images/" + ((emailParm.type.equals( "activity" ) || (emailParm.type.equals( "event" ) && !emailParm.activity_name.equals(""))) ? "FlxRez_nav.gif" : "foretees_nav.jpg") + "\" height=\"34\" border=\"0\" alt=\"" + ((emailParm.type.equals( "activity" )) ? "FlxRez" : "ForeTees") + " Logo\">&nbsp;</td>" +
        "   <!--<br><span style=\"font-size:.5em; color:#000000\">Copyright&nbsp;</span><span style=\"font-size:.7em; color:#000000\">&#169;&nbsp;</span><span style=\"font-size:.5em; color:#000000\">ForeTees, LLC<br>2010 All rights reserved.</span>-->" +
        " </tr>" +
        " <!-- BODY -->" +
        " <tr>" +
        "  <td colspan=\"3\" style=\"padding: 16px\" bgcolor=\"#FFFFFF\">" +
        "   <p style=\"font-family:verdana,arial,helvetica,sans-serif; font-size: 12px; color: #000000\">";

        // " + ((emailParm.type.equals( "activity" )) ? "FlxRez" : "ForeTees") + "   #225522

    String html_footer = "" +
        " <tr>" +
        "  <td bgcolor=\"#CCCCAA\" align=\"center\" colspan=\"3\">" +
        "  <p style=\"font-family:verdana,arial,helvetica,sans-serif; font-size: 10px; color: #000000\">" +
        "   To stop receiving these email notifications, " +
        "   please <a href=\"http://www1.foretees.com/v5/servlet/Login?extlogin&#ELS#=els&caller=email\" style=\"color:#000000\">click here to unsubscribe</a>.<br>" +
        "   Please do not reply to this email. It is an automated notification email from the " + ((emailParm.type.equals( "activity" )) ? "FlxRez" : "ForeTees") + " System.<br>" +
        "   If you require further assistance, contact your golf professionals.</p>" +
        "  </td>" +
        " </tr>";

    String html_end = "" +
       "</table>" +
       "</td></tr></table>" +
       "<p align=\"center\" style=\"font-family:verdana,arial,helvetica,sans-serif; font-size: 9px; color: #666666\">" +
       "Copyright &copy; 2010 ForeTees, LLC. &nbsp; All Rights Reserved" +
       "</p>" +
       "</body></html>";


    String efrom = parm.EMAIL_FROM;

    if (emailParm.type.startsWith("EmailTool")) {

        efrom = emailParm.from;

    }

    String proConfirm = "\nEmail Tool Message Queing Results:\n\n";
    int count = 0;
    int count_attach = 0;
    boolean error = false;
/*
    if (emailParm.type.equals("EmailToolPro")) {

        Utilities.logError("EmailTool: (start) club=" + club + ", subject=" + msgSubject + ", recipients=" + eaddrTo.size());
    }
*/
    
    //**************************************************************************
    //   CUSTOM MESSAGES
    //**************************************************************************
    //
    String custom_message = "";          // For Custom HTML Messages
    
    if (club.equals("cherryhills") && (emailParm.type.equals("tee") || emailParm.type.equals("lottery"))) {      // if Cherry Hills Tee Time
            
       custom_message = checkCherryGuests(emailParm, club);                // get custom message if guest(s) included in tee time            
    }
    
    if (club.equals("wingedfoot") && emailParm.type.equals("tee")) {      // if Winged Foot Tee Time or Notification (both use type=tee)
            
       custom_message = checkCherryGuests(emailParm, club);                // get custom message if guest(s) included in tee time            
    }

    if (club.equals("oakhillcc") && emailParm.type.equals("tee")) {       // if Oak Hill CC Tee Time

        custom_message = checkCherryGuests(emailParm, club);               // get custom message if guest(s) included in tee time
    }
    
    if (club.equals("willamette") && emailParm.type.equals("tee")) {      // if Willamette Valley Tee Time
            
       custom_message = checkWillamette(emailParm, con);                // check if custom message to be included in tee time            
    }
    
    

    //
    // Loop over the recipient array lists and send each one their own email
    //
    for (int i = 0; i < eaddrTo.size(); i++) {
        
        tmpMsgBody = msgBody; // msgBody is passed in from the caller and contains the main information for this email

        //test = true;
        
        MimetypesFileTypeMap mimetypes = (MimetypesFileTypeMap)MimetypesFileTypeMap.getDefaultFileTypeMap();
        mimetypes.addMimeTypes("text/calendar ics ICS");

        MailcapCommandMap mailcap = (MailcapCommandMap) MailcapCommandMap.getDefaultCommandMap();
        mailcap.addMailcap("text/calendar;; x-java-content-handler=com.sun.mail.handlers.text_plain");

        MimeMessage message = new MimeMessage(mailSess);

        try {

            message.setFrom(new InternetAddress(efrom));                  // set from addr
            if (emailParm.type.startsWith("EmailTool")) {
                InternetAddress iareply[] = new InternetAddress[1];               // create replyto array
                iareply[0] = new InternetAddress(emailParm.replyTo);
                message.setReplyTo(iareply);
            }                  // set from addr
            message.setSubject( msgSubject );                                       // set subject line
            message.setSentDate(new java.util.Date());                              // set date/time sent
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(eaddrTo.get(i).get(0)));

        } catch (Exception exc) {
            
            Utilities.logError("Error1 in sendEmail.doSending: " + exc.getMessage());
            return false;
        }

        
        // if AOL address then append trailer
        if (eaddrTo.get(i).get(0).endsWith( "aol.com" ) || eaddrTo.get(i).get(0).endsWith( "mnwebhost.net" )) {

            tmpMsgBody = tmpMsgBody + trailerAOL;                  // appeand AOL trailer
        }
        

        try {

            error = false;
            attach_iCal = false;

            // if there is a username here then add iCal attachment and dining and unsubscribe links
            if (!eaddrTo.get(i).get(1).equals( "" )) {

                String els = Utilities.getELS(club, eaddrTo.get(i).get(1));

                if (vCalMsg.length() > 0) {           // if vCalMsg has been built
                   
                   if (callType.equals( "modify" )) {     // if this is a modified tee time or event signup 
               
                      //
                      //  Only send iCal attachments to members that have been added to the tee time or event signup
                      //
                      attach_iCal = checkNewMem(eaddrTo.get(i).get(1), emailParm);      // see if this member has been added
                      
                      if (attach_iCal == true) {    
                      
                         attach_iCal = checkCalPref(eaddrTo.get(i).get(1), eaddrTo.get(i).get(0), con);
                      }
                      
                   } else {

                      attach_iCal = checkCalPref(eaddrTo.get(i).get(1), eaddrTo.get(i).get(0), con);
                   }
                }

                //
                // If not a dining request then add a dining link
                //
                if ((emailParm.type.equals("tee") && Utilities.checkDiningLink("email_teetime", con)) ||
                    (emailParm.type.startsWith("lesson") && Utilities.checkDiningLink("email_lesson", con))) {

                    if (emailParm.diningPrompt != null && !emailParm.diningPrompt.equals("")) {

                        String customId = "";
                        if (emailParm.diningMsgId != 0) {
                            customId = "&customId=" + emailParm.diningMsgId;
                        }
                        
                        diningLink = "" +
                            "\n\n" + emailParm.diningPrompt + " (click the link below)" +
                            "\nhttp://www1.foretees.com/"+rev+"/servlet/Login?extlogin&els=" + els + "&caller=dining&date=" + emailParm.date + customId;

                        diningLinkHTML = "" +
                            "<!-- DINING LINK --><br><p style=\"font-family:verdana,arial,helvetica,sans-serif; font-size: 12px; color: #000000\">" +
                            "" + emailParm.diningPrompt + "<br>" + 
                            "<a href=\"http://www1.foretees.com/"+rev+"/servlet/Login?extlogin&els=" + els + "&caller=dining&date=" + emailParm.date + customId + "\">" + emailParm.diningLink + "</a></p>";
                    }
                }
                    
                unsubscribeLink = "\n\n" +
                         "Thank you for using ForeTees. Should you prefer not to receive these email notifications, " +
                         "please visit http://www1.foretees.com/" + rev + "/servlet/Login?extlogin&els=" + els + "&caller=email to update your email preferences. " +
                         "Please contact your club for further assistance.";

                unsubscribeLinkHTML = "" +
                        "<p style=\"font-family:verdana,arial,helvetica,sans-serif; font-size: 10px; color: #000000\">" +
                        "To stop receiving these email notifications, " +
                        "please <a href=\"http://www1.foretees.com/" + rev + "/servlet/Login?extlogin&els=" + els + "&caller=email\" style=\"color:#000000\">click here to unsubscribe</a>.<br>" +
                        "If you require further assistance, contact your golf professionals.</p>";
                    
                tmpMsgBody += diningLink + unsubscribeLink + "\n\n";        // no need for standard trailer
               

            } else {          // NO username (so no links)

               tmpMsgBody += trailer + "\n\n";                              // no need for standard trailer

            }

            //
            // ADD PLAIN TEXT BODY
            //
            BodyPart txtBodyPart = new MimeBodyPart();
            txtBodyPart.setContent(tmpMsgBody, "text/plain");



            //
            // ADD HTML BODY
            //
            BodyPart htmlBodyPart = new MimeBodyPart();
            //htmlBody = html_start.replace("#CLUB#", club).replace("#TITLE#", ((!parmEmail.activity_name.equals("")) ? "FlxRez" : "ForeTees")); // plug in the club name
            //html_start.replace("#TITLE#", ((!parmEmail.activity_name.equals("")) ? "FlxRez" : "ForeTees"));
            htmlBody = html_start;

            // convert text message to HTML format

            htmlBody += msgBody.replace("\n", "<br>") + "</p>";
            htmlBody = htmlBody.replace("New Group", "<u>New Group:</u>");
            htmlBody = htmlBody.replace("Previous Group (before change):", "<u>Previous Group:</u>");
            if (!diningLinkHTML.equals("") && !emailParm.type.startsWith("EmailTool")) htmlBody += diningLinkHTML;
            
            //  add custom message here, if any
            
            if (!custom_message.equals("")) {
               
               htmlBody += custom_message;    // add custom message
            }
            
            htmlBody += "</td></tr>"; // end main body area
            
            //  Unsubscribe info
            if (!unsubscribeLinkHTML.equals("") && !emailParm.type.startsWith("EmailTool")) {
                htmlBody += "<tr><td bgcolor=\"#CCCCAA\" align=\"center\" colspan=\"3\">";
                htmlBody += unsubscribeLinkHTML;
                htmlBody += "</td></tr>";
            }
            htmlBody += html_end;

            htmlBodyPart.setContent(htmlBody, "text/html");


            //
            // ATTACH CALENDAR FILE (currently vCalMsg is only getting set for new not mod or can)
            //
            BodyPart icsBodyPart = null;
            if (attach_iCal && vCalMsg.length() > 0) {

                icsBodyPart = new MimeBodyPart();
              //msgBodyPart.setFileName("foretees.ics"); // OUTLOOK recommends not setting filename
              //icsBodyPart.addHeader("Content-Class", "urn:content-classes:calendarmessage");
                icsBodyPart.addHeader("Content-Disposition", "attachment"); // ;filename=foretees.ics
                icsBodyPart.addHeader("Content-Class", "urn:content-classes:calendarmessage");

                icsBodyPart.setContent(vCalMsg.toString(), "text/calendar;method=REQUEST"); // this way the email renders properly on desktops & iphone but ical is not attachment on iPhone
                
            }


            Multipart mpRoot = new MimeMultipart("mixed");
            Multipart mpContent = new MimeMultipart("alternative");

            // Create a body part to house the multipart/alternative Part
            MimeBodyPart contentPartRoot = new MimeBodyPart();
            contentPartRoot.setContent(mpContent);

            // Add the root body part to the root multipart
            mpRoot.addBodyPart(contentPartRoot);

            mpContent.addBodyPart(txtBodyPart);
            mpContent.addBodyPart(htmlBodyPart);
            if (attach_iCal && vCalMsg.length() > 0) mpRoot.addBodyPart(icsBodyPart);

            // if this from the Email Tool - handle attachments
            if (emailParm.type.startsWith("EmailTool") && fields != null) {
                count_attach = 0;
                // check for attachments
                if(fields.get("attachment1") != null) {

                    BodyPart body = new MimeBodyPart(), attachment = (BodyPart)fields.get("attachment1");
                    mpRoot.addBodyPart(attachment);
                    count_attach++;
                }

                if(fields.get("attachment2") != null) {

                    BodyPart body = new MimeBodyPart(), attachment = (BodyPart)fields.get("attachment2");
                    mpRoot.addBodyPart(attachment);
                    count_attach++;
                }

                if(fields.get("attachment3") != null) {

                    BodyPart body = new MimeBodyPart(), attachment = (BodyPart)fields.get("attachment3");
                    mpRoot.addBodyPart(attachment);
                    count_attach++;
                }
            }

            message.setContent(mpRoot);
            message.saveChanges();

            //Transport transport = mailSess.getTransport("smtp");
            //transport.connect();
            //transport.sendMessage(message, message.getAllRecipients());
            Transport.send(message);
            //transport.close();

            error = false;

        } catch (Exception exc) {
            
            Utilities.logError("sendEmail.doSending: Error sending. club=" + club + ", type=" + emailParm.type + ", err = " + exc.getMessage());
            error = true;
            //return false;  // don't return false here because then we won't try to send this email to any other recipients

        } finally {

            if (!error) count++; // if no error then bump the count

        }

    } // end loop of 'to' recipients
    
                
    
    // if here from the Email Tool then skip the proCopy loop since it's not used
    if (!emailParm.type.startsWith("EmailTool")) {

        //
        // Loop over the pro copy recipient list and send each one their own email
        //
        for (int i=0; i<eaddrProCopy.size(); i++) {

            attach_iCal = false;
            email_address = eaddrProCopy.get(i);

            if (email_address.endsWith("*")) {

                attach_iCal = true;
                email_address = email_address.substring(0, email_address.length() - 2); // trim off the last char
            }

            MimeMessage message = new MimeMessage(mailSess);

            try {

                message.setFrom(new InternetAddress(parm.EMAIL_FROM));                  // set from addr
                message.setSubject( msgSubject );                                       // set subject line
                message.setSentDate(new java.util.Date());                              // set date/time sent

                message.addRecipient(Message.RecipientType.TO, new InternetAddress(email_address));

            } catch (Exception exc) {

                Utilities.logError("Error1b in sendEmail.doSending: " + exc.getMessage());
            }


            tmpMsgBody = "\nYou are being copied on this email to one of your members.\n\n" + msgBody + "\n\n"; // emailParm.proCopyNotice


            Multipart multipart = new MimeMultipart();
            BodyPart msgBodyPart = new MimeBodyPart();


            //
            // ADD BODY AND ATTACH CALENDAR FILE
            //
            try {

                msgBodyPart.setText(tmpMsgBody + "\n\n");

                multipart.addBodyPart(msgBodyPart);

            } catch (Exception exc) {

                Utilities.logError("Error2b in sendEmail.doSending: " + exc.getMessage());
            }

            // for the pro email copies, only send the ical attachments for lessons
            if (attach_iCal && emailParm.type.equals("lesson") && vCalMsg.length() > 0) {

                // add the iCal attachment
                try {

                    BodyPart calBodyPart = new MimeBodyPart();
                    calBodyPart.setFileName("lesson.ics");
                    calBodyPart.setContent(vCalMsg.toString(), "text/calendar");

                    multipart.addBodyPart(calBodyPart);
                    message.setContent(multipart);

                } catch (Exception exc) {

                    Utilities.logError("Error3b in sendEmail.doSending: " + exc.getMessage());
                }

            } // end if lesson


            // send the email
            try {

                message.setContent(multipart);

                Transport.send(message);

            } catch (Exception exc) {

                Utilities.logError("Error4b in sendEmail.doSending: club=" + club + ", type=" + emailParm.type + ", err = " + exc.getMessage());
                return false;
            }

        } // end loop of proCopy recipients

    } else if (emailParm.type.equals("EmailToolPro")) {

        // we were here sending an email from the pro via the Email Tool
        // let's send a confirmation email the pro so that they know the
        // emails are queued

        proConfirm += "Message subject: " + msgSubject + "\n\n";
        proConfirm += "Message attachments included: " + count_attach + "\n\n";
        proConfirm += "Messages attempted: " + eaddrTo.size() + "\n\n";
        proConfirm += "Messages successfully queued: " + count + "\n\n";
        proConfirm += "Errors encountered: " + (eaddrTo.size() - count) + "\n\n";
        
        proConfirm += "\nThe mail server will now attempt to deliver the messages.  " +
                      "You should be receiving a copy of the message yourself at this address.  " +
                      "Watch for it to arrive.\n";

        try {

            MimeMessage message = new MimeMessage(mailSess);

            message.setFrom(new InternetAddress(parm.EMAIL_FROM));
            message.setSubject( "ForeTees Email Tool Confirmation" );
            message.setSentDate(new java.util.Date());
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailParm.replyTo));
            message.setText(proConfirm + "\n\n");

            Transport.send(message);

        } catch (Exception exc) {

            Utilities.logError("Error5 in sendEmail.doSending: proConfirm for club=" + club + ", proConfirm=" + proConfirm + ", err = " + exc.getMessage());
        
        } finally {

            //Utilities.logError("EmailTool: (finished) club=" + club + ", subject=" + msgSubject + ", attempted=" + eaddrTo.size() + ", queued=" + count + ", attachments=" + count_attach);

        }
        
    }

    return true;
    
 }
 
 
 //
 // Remove any dups from the arraylist
 //
 private static ArrayList<String> scrubProCopyList(ArrayList<String> eaddrTo) {
     
    ArrayList<String> tmpArray = new ArrayList<String>();

    boolean found = false;

    for (int i=0; i<eaddrTo.size(); i++){
        
        loop1:
        for (int i2=0; i2<tmpArray.size(); i2++){

            if (eaddrTo.get(i).equals(tmpArray.get(i2))) {
                
                found = true;
                break;
            }
        }
        
        if (!found) {
            
            tmpArray.add(eaddrTo.get(i));
            
        } else { found = false; }

    }

    return tmpArray;
    
 }
 
 
 //
 // Remove any dups from the arraylist
 //
 private static ArrayList<ArrayList<String>> scrubToList(ArrayList<ArrayList<String>> eaddrTo) {
     
    ArrayList<ArrayList<String>> tmpArray = new ArrayList<ArrayList<String>>();

    boolean found = false;

    for (int i=0; i<eaddrTo.size(); i++) {

        loop1:
        for (int i2=0; i2<tmpArray.size(); i2++){

            if (eaddrTo.get(i).get(0).equals(tmpArray.get(i2).get(0))) {

                found = true;
                break;
            }
        }
        
        if (!found) {

            tmpArray.add(new ArrayList<String>());
            tmpArray.get(tmpArray.size() - 1).add(eaddrTo.get(i).get(0));     // copy the email address
            tmpArray.get(tmpArray.size() - 1).add(eaddrTo.get(i).get(1));     // copy the username
            
        } else { found = false; }

    }
    
    return tmpArray;
    
 }
 
 
 // ************************************************************************
 //  Process 'send email to Caddie Master' request for Oalmont CC and Hallbrook CC
 // ************************************************************************

 public static void sendOakmontEmail(parmEmail parms, Connection con, String club) {


   //Statement estmt = null;
   Statement stmtN = null;
   ResultSet rs = null;

   //
   //  Get the parms passed in the parm block
   //
   long date = parms.date;
   int time = parms.time;
   int to_time = parms.to_time;
   int from_time = parms.from_time;
   int fb = parms.fb;
   int to_fb = parms.to_fb;
   int from_fb = parms.from_fb;
   int mm = parms.mm;
   int dd = parms.dd;
   int yy = parms.yy;

   int emailNew = parms.emailNew;
   int emailMod = parms.emailMod;
   int emailCan = parms.emailCan;

   int p91 = parms.p91;
   int p92 = parms.p92;
   int p93 = parms.p93;
   int p94 = parms.p94;
   int p95 = parms.p95;

   String day = parms.day;
   String course = parms.course;
   String to_course = parms.to_course;
   String from_course = parms.from_course;
   String notes = parms.notes;

   String player1 = parms.player1;
   String player2 = parms.player2;
   String player3 = parms.player3;
   String player4 = parms.player4;
   String player5 = parms.player5;

   String oldplayer1 = parms.oldplayer1;
   String oldplayer2 = parms.oldplayer2;
   String oldplayer3 = parms.oldplayer3;
   String oldplayer4 = parms.oldplayer4;
   String oldplayer5 = parms.oldplayer5;

   String user = parms.user;

   String pcw1 = parms.pcw1;
   String pcw2 = parms.pcw2;
   String pcw3 = parms.pcw3;
   String pcw4 = parms.pcw4;
   String pcw5 = parms.pcw5;

   String oldpcw1 = parms.oldpcw1;
   String oldpcw2 = parms.oldpcw2;
   String oldpcw3 = parms.oldpcw3;
   String oldpcw4 = parms.oldpcw4;
   String oldpcw5 = parms.oldpcw5;

   //
   //  Setup for email
   //
   String author = "unknown";
   String userFirst = "";
   String userMi = "";
   String userLast = "";
   String proName = "";

   //
   // Get the SMTP parmaters this club is using
   //
   parmSMTP parm = new parmSMTP();
   
   try {

      getSMTP.getParms(con, parm);        // get the SMTP parms

   } catch (Exception ignore) {}
   
   
   if (!user.startsWith( "proshop" )) {        // if not proshop

      try {

         //
         //  Get this user's name (for id in email msg)
         //
         PreparedStatement pstmte = con.prepareStatement (
                  "SELECT name_last, name_first, name_mi FROM member2b WHERE username = ?");

         pstmte.clearParameters();        // clear the parms
         pstmte.setString(1, user);
         rs = pstmte.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            userLast = rs.getString(1);        // user's name
            userFirst = rs.getString(2);
            userMi = rs.getString(3);

            if (userMi.equals( "" )) {

               author = userFirst + " " + userLast;

            } else {

               author = userFirst + " " + userMi + " " + userLast;
            }
         }
         pstmte.close();              // close the stmt

      }
      catch (Exception ignore) {
      }

   } else {

      author = user;
   }

   //
   //  Get today's date and time for email processing
   //
   Calendar ecal = new GregorianCalendar();               // get todays date
   int eyear = ecal.get(Calendar.YEAR);
   int emonth = ecal.get(Calendar.MONTH);
   int eday = ecal.get(Calendar.DAY_OF_MONTH);
   int e_hourDay = ecal.get(Calendar.HOUR_OF_DAY);
   int e_min = ecal.get(Calendar.MINUTE);

   int e_time = 0;
   int e_time2 = 0;
   long e_date = 0;

   //String email_time = "";

   //
   //  Build the 'time' string for display
   //
   //    Adjust the time based on the club's time zone (we are Central)
   //
   e_time = (e_hourDay * 100) + e_min;

   e_time = adjustTime(con, e_time);       // adjust for time zone

   if (e_time < 0) {                // if negative, then we went back or ahead one day

      e_time = 0 - e_time;          // convert back to positive value

      if (e_time < 1200) {           // if AM, then we rolled ahead 1 day

         //
         // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
         //
         ecal.add(Calendar.DATE,1);                     // get next day's date

         eyear = ecal.get(Calendar.YEAR);
         emonth = ecal.get(Calendar.MONTH);
         eday = ecal.get(Calendar.DAY_OF_MONTH);

      } else {                        // we rolled back 1 day

         //
         // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
         //
         ecal.add(Calendar.DATE,-1);                     // get yesterday's date

         eyear = ecal.get(Calendar.YEAR);
         emonth = ecal.get(Calendar.MONTH);
         eday = ecal.get(Calendar.DAY_OF_MONTH);
      }
   }

   int e_hour = e_time / 100;           // get adjusted hour
   e_min = e_time - (e_hour * 100);     // get minute value
   int e_am_pm = 0;                     // preset to AM

   if (e_hour > 11) {

      e_am_pm = 1;                      // PM
      e_hour = e_hour - 12;             // set to 12 hr clock
   }
   
   if (e_hour == 0) e_hour = 12;

   emonth++;                            // month starts at zero
   e_date = (eyear * 10000) + (emonth * 100) + eday;

   
   // set the date/time string for email message
   //email_time = emonth + "/" + eday + "/" + eyear + " at " + e_hour + ":" + ensureDoubleDigit(e_min) + ((e_am_pm == 0) ? " AM" : " PM");


   //
   //***********************************************
   //  Send email notification if necessary
   //***********************************************
   //
   String to = "caddiemaster@oakmont-countryclub.org";            // to address
   if (club.equals( "hallbrookcc" )) {
      to = "golf@hallbrookcc.org";                                // to address for Hallbrook CC
   }
   String f_b = "";
   String eampm = "";
   String etime = "";
   int ehr = 0;
   int emin = 0;
   int send = 0;
   String clubName = "";
   String errorMsg = "";

   PreparedStatement pstmte1 = null;

   //
   //  Get the name of the club
   //
   try {

      stmtN = con.createStatement();

      rs = stmtN.executeQuery("SELECT clubName FROM club5 WHERE clubName != ''");

      if (rs.next()) {

         clubName = rs.getString(1);
      }
      stmtN.close();

   }
   catch (Exception ignore) {
   }

   //
   //  convert time to hour and minutes for email msg
   //
   ehr = time / 100;
   emin = time - (ehr * 100);
   eampm = " AM";
   if (ehr > 12) {

      eampm = " PM";
      ehr = ehr - 12;       // convert from military time
   }
   if (ehr == 12) {

      eampm = " PM";
   }
   if (ehr == 0) {

      ehr = 12;
      eampm = " AM";
   }

   etime = ehr + ":" + ensureDoubleDigit(emin) + eampm;
   

   //
   //  set the front/back value
   //
   f_b = "Front";

   if (fb == 1) {

      f_b = "Back";
   }

   String mod = "";
   String enew = "";
   String can = "";
   String enewMsg = "";
   String emodMsg = "";
   String ecanMsg = "";

   mod = "The following Tee Time Request has been MODIFIED by " + author + ".\n\n";
   enew = "The following Tee Time Request has been ENTERED by " + author + ".\n\n";
   can = "The following tee time has been CANCELLED by " + author + ".\n\n";
   String subjectNew = "ADD from ForeTees - A Guest Tee Time has been added.";
   String subjectMod = "CHANGE from ForeTees - A Guest Tee Time has been changed.";
   String subjectCan = "DELETED from ForeTees - A Guest Tee Time has been cancelled.";

   if (club.equals( "hallbrookcc" )) {

      subjectNew = "ADD from ForeTees - A Caddie Request has been added.";
      subjectMod = "CHANGE from ForeTees - A Caddie Request has been changed.";
      subjectCan = "DELETED from ForeTees - A Caddie has been cancelled.";
   }
        
      
   Properties properties = new Properties();
   properties.put("mail.smtp.host", parm.SMTP_ADDR);                        // set outbound host address
   properties.put("mail.smtp.port", parm.SMTP_PORT);                        // set port address
   properties.put("mail.smtp.auth", (parm.SMTP_AUTH) ? "true" : "false");   // set 'use authentication'
   properties.put("mail.smtp.sendpartial", "true");                            // a message has some valid and some invalid addresses, send the message anyway

   //Session mailSess = Session.getInstance(properties, getAuthenticator()); // get session properties
   
   Session mailSess;
      
   if (parm.SMTP_AUTH) {

       mailSess = Session.getInstance(properties, getAuthenticator(parm.SMTP_USER, parm.SMTP_PASS));

   } else {

       mailSess = Session.getInstance(properties, null);

   } 
   
   MimeMessage message = new MimeMessage(mailSess);

   try {

      message.setFrom(new InternetAddress(parm.EMAIL_FROM));                              // set from addr
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
      message.setSentDate(new java.util.Date());                                // set date/time sent

      if (emailNew != 0) {        // if new tee time

         message.setSubject( subjectNew );                                            // set subject line
      }

      if (emailMod != 0) {        // if modified tee time

         message.setSubject( subjectMod );                                            // set subject line
      }

      if (emailCan != 0) {        // if Cancel tee time

         message.setSubject( subjectCan );                                            // set subject line
      }

   }
   catch (Exception e1) {
      //
      //  save error message in /" +rev+ "/error.txt
      //
      errorMsg = "Error1 in Oakmont sendEmail (get Properties) from Member_slot: ";
      errorMsg = errorMsg + e1;                              // build error msg

      Utilities.logError(errorMsg);                                       // log it
   }

   //
   //  set trans mode based on 9-hole options
   //
   if (p91 == 1) {

      pcw1 = pcw1 + "9";
   }
   if (p92 == 1) {

      pcw2 = pcw2 + "9";
   }
   if (p93 == 1) {

      pcw3 = pcw3 + "9";
   }
   if (p94 == 1) {

      pcw4 = pcw4 + "9";
   }
   if (p95 == 1) {

      pcw5 = pcw5 + "9";
   }

   //
   //  if player is 'x' then change c/w option to space
   //
   if (player1.equalsIgnoreCase( "x" )) {

      pcw1 = " ";
   }
   if (player2.equalsIgnoreCase( "x" )) {

      pcw2 = " ";
   }
   if (player3.equalsIgnoreCase( "x" )) {

      pcw3 = " ";
   }
   if (player4.equalsIgnoreCase( "x" )) {

      pcw4 = " ";
   }
   if (player5.equalsIgnoreCase( "x" )) {

      pcw5 = " ";
   }

   //
   //  Process according to the request type
   //
   if (emailNew != 0) {        // if new tee time

      enewMsg = header + enew + day + " " + mm + "/" + dd + "/" + yy + " at " + etime + " " +
                       "on the " + f_b + " 9 ";

      if (!course.equals( "" )) {

         enewMsg = enewMsg + "of Course: " + course;
      }

      enewMsg = enewMsg + "\n";

      //
      //  build message text
      //
      if (!player1.equals( "" )) {

         enewMsg = enewMsg + "\nPlayer 1: " + player1 + "  " + pcw1;
      }
      if (!player2.equals( "" )) {

         enewMsg = enewMsg + "\nPlayer 2: " + player2 + "  " + pcw2;
      }
      if (!player3.equals( "" )) {

         enewMsg = enewMsg + "\nPlayer 3: " + player3 + "  " + pcw3;
      }
      if (!player4.equals( "" )) {

         enewMsg = enewMsg + "\nPlayer 4: " + player4 + "  " + pcw4;
      }
      if (!player5.equals( "" )) {

         enewMsg = enewMsg + "\nPlayer 5: " + player5 + "  " + pcw5;
      }

      if (!notes.equals( "" )) {
      
         enewMsg = enewMsg + "\nNotes: " + notes;        // add notes
      }

      enewMsg = enewMsg + trailer;

      try {
         message.setText( enewMsg );  // put msg in email text area

         Transport.send(message);     // send it!!
      }
      catch (Exception e1) {
         //
         //  save error message in /" +rev+ "/error.txt
         //
         errorMsg = "Error2 in sendOakmontEmail (Transport.send) from Member_slot: ";
         errorMsg = errorMsg + e1;                              // build error msg

         Utilities.logError(errorMsg);                                       // log it
      }
   }

   if (emailCan != 0) {        // if Cancelled tee time

      //
      //  Create the message content
      //
      ecanMsg = header + can + day + " " + mm + "/" + dd + "/" + yy + " at " + etime + " " +
                             "on the " + f_b + " 9 ";

      if (!course.equals( "" )) {

          ecanMsg = ecanMsg + "of Course: " + course;
      }

      ecanMsg = ecanMsg + "\n";

      //
      //  build message text
      //
      if (!oldplayer1.equals( "" )) {

         ecanMsg = ecanMsg + "\nPlayer 1: " + oldplayer1 + "  " + oldpcw1;
      }
      if (!oldplayer2.equals( "" )) {

         ecanMsg = ecanMsg + "\nPlayer 2: " + oldplayer2 + "  " + oldpcw2;
      }
      if (!oldplayer3.equals( "" )) {

         ecanMsg = ecanMsg + "\nPlayer 3: " + oldplayer3 + "  " + oldpcw3;
      }
      if (!oldplayer4.equals( "" )) {

         ecanMsg = ecanMsg + "\nPlayer 4: " + oldplayer4 + "  " + oldpcw4;
      }
      if (!oldplayer5.equals( "" )) {

         ecanMsg = ecanMsg + "\nPlayer 5: " + oldplayer5 + "  " + oldpcw5;
      }

      ecanMsg = ecanMsg + trailer;

      try {
         message.setText( ecanMsg );  // put msg in email text area

         Transport.send(message);     // send it!!
      }
      catch (Exception e1) {
         //
         //  save error message in /" +rev+ "/error.txt
         //
         errorMsg = "Error3 in sendOakmontEmail (Transport.send) from Member_slot: ";
         errorMsg = errorMsg + e1;                              // build error msg

         Utilities.logError(errorMsg);                                       // log it
      }
   }

   if (emailMod != 0) {        // if tee time modified

      //
      //  Create the message content
      //
      emodMsg = header + mod + day + " " + mm + "/" + dd + "/" + yy + " at " + etime + " " +
                  "on the " + f_b + " 9 ";

      if (!from_course.equals( "" )) {

         course = to_course;
      }

      if (!course.equals( "" )) {

         emodMsg = emodMsg + "of Course: " + course;
      }

      emodMsg = emodMsg + "\n\nNew Group:\n";

      if (!player1.equals( "" )) {

         emodMsg = emodMsg + "\nPlayer 1: " + player1 + "  " + pcw1;
      }
      if (!player2.equals( "" )) {

         emodMsg = emodMsg + "\nPlayer 2: " + player2 + "  " + pcw2;
      }
      if (!player3.equals( "" )) {

         emodMsg = emodMsg + "\nPlayer 3: " + player3 + "  " + pcw3;
      }
      if (!player4.equals( "" )) {

         emodMsg = emodMsg + "\nPlayer 4: " + player4 + "  " + pcw4;
      }
      if (!player5.equals( "" )) {

         emodMsg = emodMsg + "\nPlayer 5: " + player5 + "  " + pcw5;
      }

      if (!notes.equals( "" )) {

         emodMsg = emodMsg + "\nNotes: " + notes;        // add notes
      }

      emodMsg = emodMsg + "\n\nPrevious Group (before change):\n";

      if (!oldplayer1.equals( "" )) {

         emodMsg = emodMsg + "\nPlayer 1: " + oldplayer1 + "  " + oldpcw1;
      }
      if (!oldplayer2.equals( "" )) {

         emodMsg = emodMsg + "\nPlayer 2: " + oldplayer2 + "  " + oldpcw2;
      }
      if (!oldplayer3.equals( "" )) {

         emodMsg = emodMsg + "\nPlayer 3: " + oldplayer3 + "  " + oldpcw3;
      }
      if (!oldplayer4.equals( "" )) {

         emodMsg = emodMsg + "\nPlayer 4: " + oldplayer4 + "  " + oldpcw4;
      }
      if (!oldplayer5.equals( "" )) {

         emodMsg = emodMsg + "\nPlayer 5: " + oldplayer5 + "  " + oldpcw5;
      }

      emodMsg = emodMsg + trailer;

      try {
         message.setText( emodMsg );  // put msg in email text area

         Transport.send(message);     // send it!!

      }
      catch (Exception e1) {
         //
         //  save error message in /" +rev+ "/error.txt
         //
         errorMsg = "Error4 in sendOakmontEmail (Transport.send) from Member_slot: ";
         errorMsg = errorMsg + e1;                              // build error msg

         Utilities.logError(errorMsg);                                       // log it
      }
   }

 }  // end of sendOakmontEmail


 // ************************************************************************
 //  Process 'send email to Head Pro' request for Congressional CC
 // ************************************************************************

 public static void sendCongressEmail(parmEmail parms, Connection con) {


   //Statement estmt = null;
   //Statement stmtN = null;
   ResultSet rs = null;

   //
   //  Get the parms passed in the parm block
   //
   long date = parms.date;
   int time = parms.time;
   int to_time = parms.to_time;
   int from_time = parms.from_time;
   int fb = parms.fb;
   int to_fb = parms.to_fb;
   int from_fb = parms.from_fb;
   int mm = parms.mm;
   int dd = parms.dd;
   int yy = parms.yy;

   int emailNew = parms.emailNew;
   int emailMod = parms.emailMod;
   int emailCan = parms.emailCan;

   int p91 = parms.p91;
   int p92 = parms.p92;
   int p93 = parms.p93;
   int p94 = parms.p94;
   int p95 = parms.p95;

   String day = parms.day;
   String course = parms.course;
   String to_course = parms.to_course;
   String from_course = parms.from_course;
   String notes = parms.notes;

   String player1 = parms.player1;
   String player2 = parms.player2;
   String player3 = parms.player3;
   String player4 = parms.player4;
   String player5 = parms.player5;

   String oldplayer1 = parms.oldplayer1;
   String oldplayer2 = parms.oldplayer2;
   String oldplayer3 = parms.oldplayer3;
   String oldplayer4 = parms.oldplayer4;
   String oldplayer5 = parms.oldplayer5;

   String user = parms.user;

   String pcw1 = parms.pcw1;
   String pcw2 = parms.pcw2;
   String pcw3 = parms.pcw3;
   String pcw4 = parms.pcw4;
   String pcw5 = parms.pcw5;

   String oldpcw1 = parms.oldpcw1;
   String oldpcw2 = parms.oldpcw2;
   String oldpcw3 = parms.oldpcw3;
   String oldpcw4 = parms.oldpcw4;
   String oldpcw5 = parms.oldpcw5;

   //
   //  Setup for email
   //
   String author = "unknown";
   String userFirst = "";
   String userMi = "";
   String userLast = "";
   String proName = "";

   //
   // Get the SMTP parmaters this club is using
   //
   parmSMTP parm = new parmSMTP();
   
   try {

      getSMTP.getParms(con, parm);        // get the SMTP parms

   } catch (Exception ignore) {}
   
   
   if (!user.startsWith( "proshop" )) {        // if not proshop

      try {

         //
         //  Get this user's name (for id in email msg)
         //
         PreparedStatement pstmte = con.prepareStatement (
                  "SELECT name_last, name_first, name_mi FROM member2b WHERE username = ?");

         pstmte.clearParameters();        // clear the parms
         pstmte.setString(1, user);
         rs = pstmte.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            userLast = rs.getString(1);        // user's name
            userFirst = rs.getString(2);
            userMi = rs.getString(3);

            if (userMi.equals( "" )) {

               author = userFirst + " " + userLast;

            } else {

               author = userFirst + " " + userMi + " " + userLast;
            }
         }
         pstmte.close();              // close the stmt

      }
      catch (Exception ignore) {
      }

   } else {

      author = user;
   }

   //
   //  Get today's date and time for email processing
   //
   Calendar ecal = new GregorianCalendar();               // get todays date
   int eyear = ecal.get(Calendar.YEAR);
   int emonth = ecal.get(Calendar.MONTH);
   int eday = ecal.get(Calendar.DAY_OF_MONTH);
   int e_hourDay = ecal.get(Calendar.HOUR_OF_DAY);
   int e_min = ecal.get(Calendar.MINUTE);

   int e_time = 0;
   int e_time2 = 0;
   long e_date = 0;

   //String email_time = "";

   //
   //  Build the 'time' string for display
   //
   //    Adjust the time based on the club's time zone (we are Central)
   //
   e_time = (e_hourDay * 100) + e_min;

   e_time = adjustTime(con, e_time);       // adjust for time zone

   if (e_time < 0) {                // if negative, then we went back or ahead one day

      e_time = 0 - e_time;          // convert back to positive value

      if (e_time < 1200) {           // if AM, then we rolled ahead 1 day

         //
         // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
         //
         ecal.add(Calendar.DATE,1);                     // get next day's date

         eyear = ecal.get(Calendar.YEAR);
         emonth = ecal.get(Calendar.MONTH);
         eday = ecal.get(Calendar.DAY_OF_MONTH);

      } else {                        // we rolled back 1 day

         //
         // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
         //
         ecal.add(Calendar.DATE,-1);                     // get yesterday's date

         eyear = ecal.get(Calendar.YEAR);
         emonth = ecal.get(Calendar.MONTH);
         eday = ecal.get(Calendar.DAY_OF_MONTH);
      }
   }

   int e_hour = e_time / 100;                // get adjusted hour
   e_min = e_time - (e_hour * 100);          // get minute value
   int e_am_pm = 0;                         // preset to AM

   if (e_hour > 11) {

      e_am_pm = 1;                // PM
      e_hour = e_hour - 12;       // set to 12 hr clock
   }
   if (e_hour == 0) {

      e_hour = 12;
   }

   emonth = emonth + 1;                            // month starts at zero
   e_date = (eyear * 10000) + (emonth * 100) + eday;

   
   // set the date/time string for email message
   //email_time = emonth + "/" + eday + "/" + eyear + " at " + e_hour + ":" + ensureDoubleDigit(e_min) + ((e_am_pm == 0) ? " AM" : " PM");


   //
   //***********************************************
   //  Send email notification if necessary
   //***********************************************
   //
   String to = "golfpro@ccclub.org";                   // to address
   String f_b = "";
   String eampm = "";
   String etime = "";
   int ehr = 0;
   int emin = 0;
   int send = 0;
   String clubName = "Congressional CC";
   String errorMsg = "";

   PreparedStatement pstmte1 = null;

   //
   //  convert time to hour and minutes for email msg
   //
   ehr = time / 100;
   emin = time - (ehr * 100);
   eampm = " AM";
   if (ehr > 12) {

      eampm = " PM";
      ehr = ehr - 12;       // convert from military time
   }
   if (ehr == 12) {

      eampm = " PM";
   }
   if (ehr == 0) {

      ehr = 12;
      eampm = " AM";
   }

   etime = ehr + ":" + ensureDoubleDigit(emin) + eampm;

   //
   //  set the front/back value
   //
   f_b = "Front";

   if (fb == 1) {

      f_b = "Back";
   }

   String can = "";
   String ecanMsg = "";

   can = "The following 'Non Local Guest' tee time has been CANCELLED by " + author + ".\n\n";
   String subjectCan = "ForeTees - Non Local Guest Tee Time has been cancelled.";


   Properties properties = new Properties();
   properties.put("mail.smtp.host", parm.SMTP_ADDR);                        // set outbound host address
   properties.put("mail.smtp.port", parm.SMTP_PORT);                        // set port address
   properties.put("mail.smtp.auth", (parm.SMTP_AUTH) ? "true" : "false");   // set 'use authentication'
   properties.put("mail.smtp.sendpartial", "true");                            // a message has some valid and some invalid addresses, send the message anyway

   //Session mailSess = Session.getInstance(properties, getAuthenticator());  // get session properties

   Session mailSess;
      
   if (parm.SMTP_AUTH) {

       mailSess = Session.getInstance(properties, getAuthenticator(parm.SMTP_USER, parm.SMTP_PASS));

   } else {

       mailSess = Session.getInstance(properties, null);

   }
   
   MimeMessage message = new MimeMessage(mailSess);

   try {

      message.setFrom(new InternetAddress(parm.EMAIL_FROM));                          // set from addr
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
      message.setSentDate(new java.util.Date());                            // set date/time sent

      message.setSubject( subjectCan );                                     // set subject line

   }
   catch (Exception e1) {
      //
      //  save error message in /" +rev+ "/error.txt
      //
      errorMsg = "Error1 in Congressional sendEmail (get Properties) from Member_slot: ";
      errorMsg = errorMsg + e1;                              // build error msg

      Utilities.logError(errorMsg);                                       // log it
   }

   //
   //  set trans mode based on 9-hole options
   //
   if (p91 == 1) {

      pcw1 = pcw1 + "9";
   }
   if (p92 == 1) {

      pcw2 = pcw2 + "9";
   }
   if (p93 == 1) {

      pcw3 = pcw3 + "9";
   }
   if (p94 == 1) {

      pcw4 = pcw4 + "9";
   }
   if (p95 == 1) {

      pcw5 = pcw5 + "9";
   }

   //
   //  if player is 'x' then change c/w option to space
   //
   if (player1.equalsIgnoreCase( "x" )) {

      pcw1 = " ";
   }
   if (player2.equalsIgnoreCase( "x" )) {

      pcw2 = " ";
   }
   if (player3.equalsIgnoreCase( "x" )) {

      pcw3 = " ";
   }
   if (player4.equalsIgnoreCase( "x" )) {

      pcw4 = " ";
   }
   if (player5.equalsIgnoreCase( "x" )) {

      pcw5 = " ";
   }

   //
   //  Process Cancel email
   //
   ecanMsg = header + can + day + " " + mm + "/" + dd + "/" + yy + " at " + etime + " " +
                          "on the " + f_b + " 9 ";

   if (!course.equals( "" )) {

       ecanMsg = ecanMsg + "of Course: " + course;
   }

   ecanMsg = ecanMsg + "\n";

   //
   //  build message text
   //
   if (!oldplayer1.equals( "" )) {

      ecanMsg = ecanMsg + "\nPlayer 1: " + oldplayer1 + "  " + oldpcw1;
   }
   if (!oldplayer2.equals( "" )) {

      ecanMsg = ecanMsg + "\nPlayer 2: " + oldplayer2 + "  " + oldpcw2;
   }
   if (!oldplayer3.equals( "" )) {

      ecanMsg = ecanMsg + "\nPlayer 3: " + oldplayer3 + "  " + oldpcw3;
   }
   if (!oldplayer4.equals( "" )) {

      ecanMsg = ecanMsg + "\nPlayer 4: " + oldplayer4 + "  " + oldpcw4;
   }
   if (!oldplayer5.equals( "" )) {

      ecanMsg = ecanMsg + "\nPlayer 5: " + oldplayer5 + "  " + oldpcw5;
   }

   ecanMsg = ecanMsg + trailer;

   try {
      message.setText( ecanMsg );  // put msg in email text area

      Transport.send(message);     // send it!!
   }
   catch (Exception e1) {
      //
      //  save error message in /" +rev+ "/error.txt
      //
      errorMsg = "Error3 in sendCongressEmail (Transport.send) from Member_slot: ";
      errorMsg = errorMsg + e1;                              // build error msg

      Utilities.logError(errorMsg);                                       // log it
   }

 }  // end of sendCongressEmail


 // **************************************************************************************************************
 //  Process send email to Westchester CC or Pelicans Nest GC - 
 //       - a member has changed his/her email address (from Member_services)
 // **************************************************************************************************************

 public static void sendWestEmail(parmEmail parms, Connection con) {


   //Statement estmt = null;
   //Statement stmtN = null;
   ResultSet rs = null;

   //
   //  Get the parms passed in the parm block
   //
   String player1 = parms.player1;              // get email address
   String player2 = parms.player2;              // 2nd email address
   String user = parms.user;                    // member
   String errorMsg = "";


   //
   //  Setup for email
   //
   String author = "unknown";
   String userFirst = "";
   String userMi = "";
   String userLast = "";


   //
   // Get the SMTP parmaters this club is using
   //
   parmSMTP parm = new parmSMTP();
   
   try {

      getSMTP.getParms(con, parm);        // get the SMTP parms

   } catch (Exception ignore) {}
   
   
   try {

      //
      //  Get this user's name (for id in email msg)
      //
      PreparedStatement pstmte = con.prepareStatement (
               "SELECT name_last, name_first, name_mi FROM member2b WHERE username = ?");

      pstmte.clearParameters();        // clear the parms
      pstmte.setString(1, user);
      rs = pstmte.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         userLast = rs.getString(1);        // user's name
         userFirst = rs.getString(2);
         userMi = rs.getString(3);

         if (userMi.equals( "" )) {

            author = userFirst + " " + userLast;

         } else {

            author = userFirst + " " + userMi + " " + userLast;
         }
      }
      pstmte.close();              // close the stmt

   }
   catch (Exception ignore) {
   }


   //
   //***********************************************
   //  Send email notification if necessary
   //***********************************************
   //
   String to = "";                   // to addresses
   String to2 = "";    

   if (parms.type.equals( "Westchester" )) {

      to = "cisolini@wccclub.org";            // to address
      to2 = "aspiconardi@wccclub.org";        // to address

   } else if (parms.type.equals("FoxDen")) {    // if FoxDen

      to = "pattit@foxdencountryclub.com";

   } else {       // else Pelicans Nest
    
      to = "cpera@pelicansnest.org";            // to address
   }

   String msg = "\nMember " + author + " has added or changed an email address on the ForeTees system.\n\n";
   String subject = "Member Email Address Change Notification from ForeTees";

   String trailerWest = "\n\n\n*****************************************************************************************" +
                    "\nThis message is sent by the ForeTees system whenever a member of your club changes one or both " +
                    "of his/her email addresses.  This allows you the opportunity to make the same change in any of " +
                    "your other member databases.  Please contact ForeTees at support@foretees.com to request any " +
                    "changes. " +
                    "Thank you for using the ForeTees Reservation System." +
                    "\n********************************************************************************************";


   Properties properties = new Properties();
   properties.put("mail.smtp.host", parm.SMTP_ADDR);                        // set outbound host address
   properties.put("mail.smtp.port", parm.SMTP_PORT);                        // set port address
   properties.put("mail.smtp.auth", (parm.SMTP_AUTH) ? "true" : "false");   // set 'use authentication'
   properties.put("mail.smtp.sendpartial", "true");                            // a message has some valid and some invalid addresses, send the message anyway

   //Session mailSess = Session.getInstance(properties, getAuthenticator());  // get session properties

   Session mailSess;
      
   if (parm.SMTP_AUTH) {

       mailSess = Session.getInstance(properties, getAuthenticator(parm.SMTP_USER, parm.SMTP_PASS));

   } else {

       mailSess = Session.getInstance(properties, null);

   }
   
   MimeMessage message = new MimeMessage(mailSess);

   try {

      message.setFrom(new InternetAddress(parm.SMTP_ADDR));                 // set from addr
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
      if (!to2.equals( "" )) {
         message.addRecipient(Message.RecipientType.TO, new InternetAddress(to2));
      }
      message.setSentDate(new java.util.Date());                            // set date/time sent
      message.setSubject( subject );                                        // set subject line

   }
   catch (Exception e1) {
      //
      //  save error message in /" +rev+ "/error.txt
      //
      errorMsg = "Error1 in sendEmail.sendWestEmail (get Properties) from Member_services: ";
      errorMsg = errorMsg + e1;                                             // build error msg

      Utilities.logError(errorMsg);                                        // log it
   }

   if (player1 == null) {       // make sure email #1 is not null

      player1 = " ";
   }
   if (player2 == null) {

      player2 = " ";
   }
     
   msg = msg + "New primary email address: " + player1;

   msg = msg + "\n\nNew alternate email address: " + player2;

   msg = msg + trailerWest;            // add trailer

   try {
      message.setText( msg );      // put msg in email text area

      Transport.send(message);     // send it!!
        
   }
   catch (Exception e1) {
      //
      //  save error message in /" +rev+ "/error.txt
      //
      errorMsg = "Error2 in sendEmail.sendWestEmail (Transport.send) from Member_services: ";
      errorMsg = errorMsg + e1;                              // build error msg

      Utilities.logError(errorMsg);                                       // log it
   }

 }  // end of sendWestEmail


 // **************************************************************************************************************
 //  Process send email to Winged Foot Pro -
 //       - a member has gotten close to his/her guest quota (within 3)
 // **************************************************************************************************************

 public static void sendWFemail(int remaining, int quota, String player, String msgType) {


   //
   //  Send email notification if necessary
   //
   String errorMsg = "";      
   String to = "dzona@wfgc.org";              // to address


   String msg = "\nMember " +player+ " has " +remaining+ " guest(s) remaing in his/her guest quota of " +quota+ " for the " +msgType+ ".\n\n";
   String subject = "Member Guest Quota Notification from ForeTees";

   String trailerWF = "\n\n\n*****************************************************************************************" +
                    "\nThis message is sent by the ForeTees system whenever a member of your club comes " +
                    "within 6 guests of their family quota for the season or the year. " +
                    "Please contact ForeTees at support@foretees.com to request any changes. " +
                    "Thank you for using the ForeTees Reservation System." +
                    "\n********************************************************************************************";


   Properties properties = new Properties();
   properties.put("mail.smtp.host", host);                      // set outbound host address
   properties.put("mail.smtp.port", port);                      // set port address
   properties.put("mail.smtp.auth", "true");                    // set 'use authentication'
   properties.put("mail.smtp.sendpartial", "true");                            // a message has some valid and some invalid addresses, send the message anyway

   Session mailSess = Session.getInstance(properties, getAuthenticator("support@foretees.com", "fikd18")); // get session properties
   
   MimeMessage message = new MimeMessage(mailSess);

   try {

      message.setFrom(new InternetAddress(efrom));                               // set from addr
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
      message.setSentDate(new java.util.Date());                                // set date/time sent
      message.setSubject( subject );                                            // set subject line

   }
   catch (Exception e1) {
      errorMsg = "Error1 in sendEmail.sendWFemail (get Properties) from Member_services: ";
      errorMsg = errorMsg + e1;                              // build error msg

      Utilities.logError(errorMsg);                                       // log it
   }


   msg = msg + trailerWF;            // add trailer

   try {
      message.setText( msg );      // put msg in email text area

      Transport.send(message);     // send it!!

   }
   catch (Exception e1) {
      errorMsg = "Error2 in sendEmail.sendWFemail (Transport.send) from Member_services: ";
      errorMsg = errorMsg + e1;                              // build error msg

      Utilities.logError(errorMsg);                                       // log it
   }

 }  // end of sendWFemail

 
 
 // **************************************************************************************************************
 //  Process send email to Mediterra Pro -  (Case 1262)
 //       - a sports member has booked their final tee time allowed in time period
 // **************************************************************************************************************

 public static void sendMediterraEmail(String player, String mnum) {


   //
   //  Send email notification if necessary
   //
   String errorMsg = "";      
   String to = "KrisinC@bonitabaygroup.com";              // to address


   String msg = "\nMember " +player+ " (member number " + mnum + ") has used their 4 allowed rounds.\n\n";
   String subject = "Sports member has reached their quota.";

   String trailerWF = "\n\n\n*****************************************************************************************" +
                    "\nThis message is sent by the ForeTees system whenever a Sports member of your club " +
                    "uses all their allowed rounds of golf for the season. " +
                    "Please contact ForeTees at support@foretees.com to request any changes. " +
                    "Thank you for using the ForeTees Reservation System." +
                    "\n********************************************************************************************";


   Properties properties = new Properties();
   properties.put("mail.smtp.host", host);                      // set outbound host address
   properties.put("mail.smtp.port", port);                      // set port address
   properties.put("mail.smtp.auth", "true");                    // set 'use authentication'
   properties.put("mail.smtp.sendpartial", "true");                            // a message has some valid and some invalid addresses, send the message anyway

   Session mailSess = Session.getInstance(properties, getAuthenticator("support@foretees.com", "fikd18")); // get session properties

   MimeMessage message = new MimeMessage(mailSess);

   try {

      message.setFrom(new InternetAddress(efrom));                               // set from addr
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
      message.setSentDate(new java.util.Date());                                // set date/time sent
      message.setSubject( subject );                                            // set subject line

   }
   catch (Exception e1) {
      errorMsg = "Error1 in sendEmail.sendMediterraEmail (get Properties) from Member_services: ";
      errorMsg = errorMsg + e1;                              // build error msg

      Utilities.logError(errorMsg);                                       // log it
   }


   msg = msg + trailerWF;            // add trailer

   try {
      message.setText( msg );      // put msg in email text area

      Transport.send(message);     // send it!!

   }
   catch (Exception e1) {
      errorMsg = "Error2 in sendEmail.sendMediterraEmail (Transport.send) from Member_services: ";
      errorMsg = errorMsg + e1;                              // build error msg

      Utilities.logError(errorMsg);                                       // log it
   }

 }  // end of sendMediterraEmail
 
 
 
 

 // **************************************************************************************************************
 //  Cherry Hills Tee Time or Winged Foot tee time or notification
 //       - if any player is a guest, then create a custom message that includes a link to a pdf for guest guidelines
 // **************************************************************************************************************
 public static String checkCherryGuests(parmEmail parms, String club) {

   String message = "";
         
   String [] playerA = new String [25];            // player names 
   String [] userA = new String [25];              // usernames 

   playerA[0] = parms.player1;
   playerA[1] = parms.player2;
   playerA[2] = parms.player3;
   playerA[3] = parms.player4;
   playerA[4] = parms.player5;
   playerA[5] = parms.player6;
   playerA[6] = parms.player7;
   playerA[7] = parms.player8;
   playerA[8] = parms.player9;
   playerA[9] = parms.player10;
   playerA[10] = parms.player11;
   playerA[11] = parms.player12;
   playerA[12] = parms.player13;
   playerA[13] = parms.player14;
   playerA[14] = parms.player15;
   playerA[15] = parms.player16;
   playerA[16] = parms.player17;
   playerA[17] = parms.player18;
   playerA[18] = parms.player19;
   playerA[19] = parms.player20;
   playerA[20] = parms.player21;
   playerA[21] = parms.player22;
   playerA[22] = parms.player23;
   playerA[23] = parms.player24;
   playerA[24] = parms.player25;

   userA[0] = parms.user1;
   userA[1] = parms.user2;
   userA[2] = parms.user3;
   userA[3] = parms.user4;
   userA[4] = parms.user5;
   userA[5] = parms.user6;
   userA[6] = parms.user7;
   userA[7] = parms.user8;
   userA[8] = parms.user9;
   userA[9] = parms.user10;
   userA[10] = parms.user11;
   userA[11] = parms.user12;
   userA[12] = parms.user13;
   userA[13] = parms.user14;
   userA[14] = parms.user15;
   userA[15] = parms.user16;
   userA[16] = parms.user17;
   userA[17] = parms.user18;
   userA[18] = parms.user19;
   userA[19] = parms.user20;
   userA[20] = parms.user21;
   userA[21] = parms.user22;
   userA[22] = parms.user23;
   userA[23] = parms.user24;
   userA[24] = parms.user25;
   
   boolean found = false;
   
   //
   //  Check for any guests
   //
   for (int i=0; i<25; i++) {

      if (userA[i].equals("") && !playerA[i].equals("") && !playerA[i].equalsIgnoreCase("x")) {   // if NOT member and NOT X, must be a guest
         
         found = true;
         break;
      }
   }
   
   if (found == true) {       // if guest found
    
      if (club.equals("cherryhills")) {      // if Cherry Hills Tee Time
            
         message = "<p align=\"center\"><a href=\"http://web.foretees.com/"+rev+"/AEimages/cherryhills/CHCC_Guest_Guidelines.pdf\">" +
                   "Click here to view the current CHCC Guest Guidelines.</a> Please share this information with your guests.  Thank you.</p>";     
         
      } else if (club.equals("oakhillcc")) {

          if (parms.course.equals("East Course")) {
              message = "\n\n\nThank you for reserving a guest time on the East course. The expected pace of play is 4 hours and 15 minutes, " +
                      "please remind your guest of this time on the first tee. The Golf Committee suggests you play ready golf and that " +
                      "your proper position on the golf course is directly behind the group in front of you. Enjoy your round!";
          } else if (parms.course.equals("West Course")) {
              message = "\n\n\nThank you for reserving a guest time on the West course. The expected pace of play is 3 hours and 45 minutes, " +
                      "please remind your guest of this time on the first tee. The Golf Committee suggests you play ready golf and that " +
                      "your proper position on the golf course is directly behind the group in front of you. Enjoy your round!";
          }

      } else {     // Winged Foot
         
         message = "<p align=\"center\">Please forward this information to your guests prior to their arrival at the Club. " +
                   "<a href=\"http://web.foretees.com/"+rev+"/AEimages/wingedfoot/WF_Guest_Information.pdf\">Guest Information</a></p>";     
      }
   }   
         
   return(message);

 }  // end of checkCherryGuests

         

 
 // **************************************************************************************************************
 //  Willamette Valley Tee Time
 //       - create a custom message for the auto detailing service - include a pdf
 // **************************************************************************************************************

 public static String checkWillamette(parmEmail parms, Connection con) {

   String message = "";
         
   boolean send = false;
   
   //
   //  Get today's date and time
   //
   Calendar ecal = new GregorianCalendar();               // get todays date

   int eyear = ecal.get(Calendar.YEAR);
   int emonth = ecal.get(Calendar.MONTH);
   int eday = ecal.get(Calendar.DAY_OF_MONTH);
   int e_hourDay = ecal.get(Calendar.HOUR_OF_DAY);
   int e_min = ecal.get(Calendar.MINUTE);
   
   //
   //  Build the 'time' string for display
   //
   //    Adjust the time based on the club's time zone (we are Central)
   //
   int e_time = (e_hourDay * 100) + e_min;

   e_time = adjustTime(con, e_time);       // adjust for time zone

   if (e_time < 0) {                // if negative, then we went back or ahead one day

      e_time = 0 - e_time;          // convert back to positive value

      if (e_time < 1200) {           // if AM, then we rolled ahead 1 day

         //
         // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
         //
         ecal.add(Calendar.DATE,1);                     // get next day's date

         eyear = ecal.get(Calendar.YEAR);
         emonth = ecal.get(Calendar.MONTH);
         eday = ecal.get(Calendar.DAY_OF_MONTH);

      } else {                        // we rolled back 1 day

         //
         // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
         //
         ecal.add(Calendar.DATE,-1);                     // get yesterday's date

         eyear = ecal.get(Calendar.YEAR);
         emonth = ecal.get(Calendar.MONTH);
         eday = ecal.get(Calendar.DAY_OF_MONTH);
      }
   }

   emonth++;                                // month starts at zero
   long e_date = (eyear * 10000) + (emonth * 100) + eday;

   //
   //  Do not add message before 5/01/2010
   //
   if (e_date > 20100430) {
    
      //
      //  Now check if the tee time is at least 3 days from now - do not add message if not
      //
      ecal.add(Calendar.DATE,3);                     // get 3 days from now
      eyear = ecal.get(Calendar.YEAR);
      emonth = ecal.get(Calendar.MONTH) +1;
      eday = ecal.get(Calendar.DAY_OF_MONTH);

      e_date = (eyear * 10000) + (emonth * 100) + eday;

      if (parms.date > e_date || (parms.date == e_date && parms.time >= e_time)) {
         
         send = true;         // send the message
      }
   }
   
   
   if (send == true) {       // if ok to include message
    
      message = "<p align=\"center\"><b>NEW! Auto Detailing available for members and guests while you golf.</b><BR><a href=\"http://web.foretees.com/"+rev+"/AEimages/willamette/auto_detail_flier.pdf\">Click here for details and contact information.</a></p>";     
   }   
         
   return(message);

 }  // end of checkWillamette

         

 
 //************************************************************************
 //  adjustTime - receives a time value (hhmm) and adjusts it for the club's
 //               specified time zone.
 //
 //  Called by:  Proshop_lott
 //              Member_lott
 //              Proshop_select
 //              Member_select
 //              moveReqs (and others above)
 //
 //
 //   returns: time (hhmm) - negative value if it rolled back or ahead a day
 //
 //************************************************************************

 public static int adjustTime(Connection con, int time) {

    
    //  use common/Utilities so we don't have to maintain this in multiple files
    
   time = Utilities.adjustTime(con, time);     // get adjusted time
    
   return( time );

 } 


 //************************************************************************
 //  unfilter - Searches the string provided for special HTML characters and
 //             replaces them with normal special characters.
 //
 //           &lt becomes <
 //           &gt becomes >
 //           &quot becomes "
 //           &amp becomes &
 //
 //
 //    called by:  Send_email
 //
 //************************************************************************

 public static String unfilter(String input) {


    StringBuffer unfiltered = new StringBuffer(input.length());
    StringBuffer spec = new StringBuffer(5);

    int length = input.length();
    char c;
    String specs = "";

    for(int i=0; i<length; i++) {

       c = input.charAt(i);
       if (c == '&') {                  // if special html char

          spec = new StringBuffer(5);   // init string

          while (c != ';' && i<length) {   // go till end of spec char - semi-colon

             spec.append(c);            // put in buffer
             i++;
             c = input.charAt(i);       // get next
          }                             // end of WHILE

          specs = spec.toString();      // put in string form

          if (specs.equals("&lt")) {                  // if special char
             unfiltered.append('<');                 // change back to normal spec char
          } else if (specs.equals("&gt")) {
             unfiltered.append('>');
          } else if (specs.equals("&quot")) {
             unfiltered.append('"');
          } else if (specs.equals("&amp")) {
             unfiltered.append('&');
          } else if (specs.equals("&#35")) {
             unfiltered.append('#');
          } else if (specs.equals("&#33")) {
             unfiltered.append('!');
          } else if (specs.equals("&#36")) {
             unfiltered.append("$");
          } else if (specs.equals("&#37")) {
             unfiltered.append('%');
          } else if (specs.equals("&#40")) {
             unfiltered.append('(');
          } else if (specs.equals("&#41")) {
             unfiltered.append(')');
          } else if (specs.equals("&#42")) {
             unfiltered.append('*');
          } else if (specs.equals("&#43")) {
             unfiltered.append('+');
          } else if (specs.equals("&#44")) {
             unfiltered.append(',');
          } else if (specs.equals("&#45")) {
             unfiltered.append('-');
          } else if (specs.equals("&#46")) {
             unfiltered.append('.');
          } else if (specs.equals("&#47")) {
             unfiltered.append('/');
          } else if (specs.equals("&#61")) {
             unfiltered.append('=');
          } else if (specs.equals("&#63")) {
             unfiltered.append('?');
          } else if (specs.equals("&#64")) {
             unfiltered.append('@');
          } else if (specs.equals("&#91")) {
             unfiltered.append('[');
          } else if (specs.equals("&#93")) {
             unfiltered.append(']');
          } else if (specs.equals("&#94")) {
             unfiltered.append('^');
          } else if (specs.equals("&#95")) {
             unfiltered.append('_');
          } else if (specs.equals("&#123")) {
             unfiltered.append('{');
          } else if (specs.equals("&#124")) {
             unfiltered.append('|');
          } else if (specs.equals("&#125")) {
             unfiltered.append('}');
          } else if (specs.equals("&#126")) {
             unfiltered.append('~');
          }
       } else {
          unfiltered.append(c);

       }          // end of IF spec char
    }             // end of DO loop

    return(unfiltered.toString());

 }  // end of unfilter


 // ************************************************************************
 //  Process getAuthenticator for email authentication
 // ************************************************************************

 private static Authenticator getAuthenticator(final String user, final String pass) {

    Authenticator auth = new Authenticator() {

       public PasswordAuthentication getPasswordAuthentication() {

         return new PasswordAuthentication(user, pass); // credentials
         //return new PasswordAuthentication("support@foretees.com", "fikd18"); // credentials
       }
    };

    return auth;
 }
 
 
 //************************************************************************
 //
 // Return a string with a leading zero is nessesary
 //
 //************************************************************************
 private static String ensureDoubleDigit(int value) {

    return ((value < 10) ? "0" + value : "" + value);
     
 }


 private static boolean checkCalPref(String user, String email, Connection con) {


    ResultSet rs = null;
    PreparedStatement pstmt = null;
    boolean result = false;

    try {

        pstmt = con.prepareStatement (
                    "SELECT email, email2, ical1, ical2 FROM member2b WHERE username = ?");

        pstmt.clearParameters();
        pstmt.setString(1, user);
        rs = pstmt.executeQuery();

        if (rs.next()) {

            if (email.equalsIgnoreCase(rs.getString("email"))) {

                result = (rs.getInt("ical1") == 1);

            } else if (email.equalsIgnoreCase(rs.getString("email2"))) {

                result = (rs.getInt("ical2") == 1);

            }
        }

        pstmt.close();

    } catch (Exception exc) {

        Utilities.logError("sendEmail.checkCalPref: user="+user+", email="+email+", error=" + exc.getMessage());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return(result);

 }

 
 private static boolean checkNewMem(String user, parmEmail parms) {

    boolean result = false;   
   
    //
    //  Check to see if the member was already part of the tee time or event signup
    //
    if (!user.equals( "" )) {       // if user provided
       
       if (user.equals(parms.olduser1) || user.equals(parms.olduser2) || user.equals(parms.olduser3) || user.equals(parms.olduser4) || user.equals(parms.olduser5) || 
           user.equals(parms.olduser6) || user.equals(parms.olduser7) || user.equals(parms.olduser8) || user.equals(parms.olduser9) || user.equals(parms.olduser10) || 
           user.equals(parms.olduser11) || user.equals(parms.olduser12) || user.equals(parms.olduser13) || user.equals(parms.olduser14) || user.equals(parms.olduser15) || 
           user.equals(parms.olduser16) || user.equals(parms.olduser17) || user.equals(parms.olduser18) || user.equals(parms.olduser19) || user.equals(parms.olduser20) || 
           user.equals(parms.olduser21) || user.equals(parms.olduser22) || user.equals(parms.olduser23) || user.equals(parms.olduser24) || user.equals(parms.olduser25) ) {
       
          result = false;       // user was already part of tee time or event - do not send iCal again
          
       } else {
          
          result = true;        // user is new - send iCal attachment
       }
    }
    
    return(result);

 }


/*
 private static Dictionary getUpload(HttpServletRequest req)
    throws IOException, MessagingException {


      String boundary = req.getHeader("Content-Type");
      int pos = boundary.indexOf('=');
      boundary = boundary.substring(pos + 1);
      boundary = "--" + boundary;
      ServletInputStream in =
         req.getInputStream();
      byte[] bytes = new byte[512];
      int state = 0;
      ByteArrayOutputStream buffer =
         new ByteArrayOutputStream();
      String name = null,
             value = null,
             filename = null,
             contentType = null;
      Dictionary fields = new Hashtable();

      int i = in.readLine(bytes,0,512);
      while(-1 != i)
      {
         String st = new String(bytes,0,i);
         if(st.startsWith(boundary))
         {
            state = 0;
            if(null != name)
            {
               if(value != null)
                  fields.put(name,
                     value.substring(0,
                           // -2 to remove CR/LF
                           value.length() - 2));
               else if(buffer.size() > 2)
               {
                  InternetHeaders headers =
                     new InternetHeaders();
                  MimeBodyPart bodyPart =
                     new MimeBodyPart();
                  DataSource ds =
                     new ByteArrayDataSource(
                        buffer.toByteArray(),
                        contentType,filename);
                  bodyPart.setDataHandler(
                     new DataHandler(ds));
                  bodyPart.setDisposition(
                     "attachment; filename=\"" +
                     filename + "\"");
                  bodyPart.setFileName(filename);
                  fields.put(name,bodyPart);
               }
               name = null;
               value = null;
               filename = null;
               contentType = null;
               buffer = new ByteArrayOutputStream();
            }
         }
         else if(st.startsWith(
            "Content-Disposition: form-data") &&
            state == 0)
         {
            StringTokenizer tokenizer =
               new StringTokenizer(st,";=\"");
            while(tokenizer.hasMoreTokens())
            {
               String token = tokenizer.nextToken();
               if(token.startsWith(" name"))
               {
                  name = tokenizer.nextToken();
                  state = 2;
               }
               else if(token.startsWith(" filename"))
               {
                  filename = tokenizer.nextToken();
                  StringTokenizer ftokenizer =
                     new StringTokenizer(filename,"\\/:");
                  filename = ftokenizer.nextToken();
                  while(ftokenizer.hasMoreTokens())
                     filename = ftokenizer.nextToken();
                  state = 1;
                  break;
               }
            }
         }
         else if(st.startsWith("Content-Type") &&
                 state == 1)
         {
            pos = st.indexOf(":");
            // + 2 to remove the space
            // - 2 to remove CR/LF
            contentType =
               st.substring(pos + 2,st.length() - 2);
         }
         else if(st.equals("\r\n") && state == 1)
            state = 3;
         else if(st.equals("\r\n") && state == 2)
            state = 4;
         else if(state == 4)
            value = value == null ? st : value + st;
         else if(state == 3)
            buffer.write(bytes,0,i);
         i = in.readLine(bytes,0,512);

      } // end while loop

      return fields;

  }
*/

static class ByteArrayDataSource
   implements DataSource
{
   byte[] bytes;
   String contentType,
          name;

   ByteArrayDataSource(byte[] bytes,
                       String contentType,
                       String name)
   {
      this.bytes = bytes;
      if(contentType == null)
         this.contentType = "application/octet-stream";
      else
         this.contentType = contentType;
      this.name = name;
   }

   public String getContentType()
   {
      return contentType;
   }

   public InputStream getInputStream()
   {
      // remove the final CR/LF
      return new ByteArrayInputStream(
         bytes,0,bytes.length - 2);
   }

   public String getName()
   {
      return name;
   }

   public OutputStream getOutputStream()
      throws IOException
   {
      throw new FileNotFoundException();
   }
}

}  // end of sendEmail class