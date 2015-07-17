/***************************************************************************************
 *   parmEmail:  This class will define a paramter block object to be used for 
 *               sending email notifications of tee times, lottery requests, etc.
 *
 *               This is setup to handle lotteries and normal tee times.
 *
 *
 *   created: 2/20/2004   Bob P.
 *
 *   last updated:
 *
 *         2/10/14   Added orig_by to allow the originator of a reservation to be stored and used.
 *        10/30/12   Added fb2-fb5 to allow additional fb values to be passed when sending out lottery assignment emails.
 *         9/17/12   Added wplayer1-5 and wpcw1-5 fields for additional information when moving a team off the event wait list.
 *         4/07/11   Added activity_email_name field.
 *        12/19/10   Added preview boolean, preview_address string and out printWriter
 *        11/30/10   Added userg1-25 values for sending emails to sponsors of unaccompanied guests (case 1410).
 *         2/18/09   Added activity_id, club and guests fields to parm
 *         2/18/09   Added new fields (replyTo, from, subject htmlBody, txtBody) for sendEmail & Send_email changes.
 *         2/11/09   Added IS_TLT to indicate this is a notification club
 *         2/11/09   Add guest_id1-25 and oldguest_id1-25 for holding guest tracking ids
 *         9/15/09   Added activity_name and actual_activity_name srings for genrez
 *         2/19/09   Added 'message' String for use with the dining request system
 *        10/15/08   Added suppressMemberEmails boolean for use with lesson book email suppression feature (case 1454).
 *         3/27/08   Add hideNotes int for tcclub (case #1406)
 *         3/27/08   Add season int for season long events
 *         3/07/08   Add emailpro1 & 2 strings for event signup to pass extra email addresses
 *                   so we can copy a pro or group leader. 
 *         2/29/08   Add time values to ahold all 5 possible tee times.
 *
 ***************************************************************************************
 */


package com.foretees.common;

import java.io.PrintWriter;

public class parmEmail {

   public boolean error = false;
   public boolean suppressMemberEmails = false;
     
   public long date = 0;

   public int emailNew = 0;
   public int emailMod = 0;
   public int emailCan = 0;
   public int time = 0;
   public int time2 = 0;
   public int time3 = 0;
   public int time4 = 0;
   public int time5 = 0;
   public int to_time = 0;
   public int from_time = 0;
   public int fb = 0;
   public int fb2 = 0;
   public int fb3 = 0;
   public int fb4 = 0;
   public int fb5 = 0;
   public int to_fb = 0;
   public int from_fb = 0;
   public int mm = 0;
   public int dd = 0;
   public int yy = 0;
   public int groups = 0;
   public int p91 = 0;
   public int p92 = 0;
   public int p93 = 0;
   public int p94 = 0;
   public int p95 = 0;
   public int p96 = 0;
   public int p97 = 0;
   public int p98 = 0;
   public int p99 = 0;
   public int p910 = 0;
   public int p911 = 0;
   public int p912 = 0;
   public int p913 = 0;
   public int p914 = 0;
   public int p915 = 0;
   public int p916 = 0;
   public int p917 = 0;
   public int p918 = 0;
   public int p919 = 0;
   public int p920 = 0;
   public int p921 = 0;
   public int p922 = 0;
   public int p923 = 0;
   public int p924 = 0;
   public int p925 = 0;
   public int hideNotes = 0;

   public int etype = 0;                // Event-only fields
   public int wait = 0;
   public int checkWait = 0;
   public int season = 0;

   public boolean IS_TLT = false;

   public String club = "";
   public int activity_id = 0;
   public int guests = 0;
   public int force_content_id = 0;
   public int force_content_location_id = 0;
   public int force_now_date = 0;
   public int force_now_time = 0;

   public String activity_name = "";
   public String actual_activity_name = "";
   public String activity_email_name = "";
   public String act_time = "";
   public String name = "";
   public String password = "";
   public String email = "";
   public String emailpro1 = "";        // extra email addresses for pro or other (used for event notifications)
   public String emailpro2 = "";
   public String wuser1 = "";
   public String wuser2 = "";
   public String wuser3 = "";
   public String wuser4 = "";
   public String wuser5 = "";           // end of Event-only fields
   public String wplayer1 = "";
   public String wplayer2 = "";
   public String wplayer3 = "";
   public String wplayer4 = "";
   public String wplayer5 = "";
   public String wp1cw = "";
   public String wp2cw = "";
   public String wp3cw = "";
   public String wp4cw = "";
   public String wp5cw = "";
   public String message = "";          // Used for dining requests since no dynamic processing needs to be done

   public String diningPrompt = "";
   public String diningLink = "";
   public int diningMsgId = 0;

   public String from = "";
   public String replyTo = "";
   public String subject = "";
   public String txtBody = "";
   public String htmlBody = "";
   public String type = "";
   public String course = "";
   public String to_course = "";        // for Proshop_dsheet
   public String from_course = "";      //       "
   public String day = "";
   public String notes = "";
   public String orig_by = "";
   public String player1 = "";
   public String player2 = "";
   public String player3 = "";
   public String player4 = "";
   public String player5 = "";
   public String player6 = "";
   public String player7 = "";
   public String player8 = "";
   public String player9 = "";
   public String player10 = "";
   public String player11 = "";
   public String player12 = "";
   public String player13 = "";
   public String player14 = "";
   public String player15 = "";
   public String player16 = "";
   public String player17 = "";
   public String player18 = "";
   public String player19 = "";
   public String player20 = "";
   public String player21 = "";
   public String player22 = "";
   public String player23 = "";
   public String player24 = "";
   public String player25 = "";

   public String oldplayer1 = "";
   public String oldplayer2 = "";
   public String oldplayer3 = "";
   public String oldplayer4 = "";
   public String oldplayer5 = "";
   public String oldplayer6 = "";
   public String oldplayer7 = "";
   public String oldplayer8 = "";
   public String oldplayer9 = "";
   public String oldplayer10 = "";
   public String oldplayer11 = "";
   public String oldplayer12 = "";
   public String oldplayer13 = "";
   public String oldplayer14 = "";
   public String oldplayer15 = "";
   public String oldplayer16 = "";
   public String oldplayer17 = "";
   public String oldplayer18 = "";
   public String oldplayer19 = "";
   public String oldplayer20 = "";
   public String oldplayer21 = "";
   public String oldplayer22 = "";
   public String oldplayer23 = "";
   public String oldplayer24 = "";
   public String oldplayer25 = "";

   public String user = "";
   public String user1 = "";
   public String user2 = "";
   public String user3 = "";
   public String user4 = "";
   public String user5 = "";
   public String user6 = "";
   public String user7 = "";
   public String user8 = "";
   public String user9 = "";
   public String user10 = "";
   public String user11 = "";
   public String user12 = "";
   public String user13 = "";
   public String user14 = "";
   public String user15 = "";
   public String user16 = "";
   public String user17 = "";
   public String user18 = "";
   public String user19 = "";
   public String user20 = "";
   public String user21 = "";
   public String user22 = "";
   public String user23 = "";
   public String user24 = "";
   public String user25 = "";

   public String olduser1 = "";
   public String olduser2 = "";
   public String olduser3 = "";
   public String olduser4 = "";
   public String olduser5 = "";
   public String olduser6 = "";
   public String olduser7 = "";
   public String olduser8 = "";
   public String olduser9 = "";
   public String olduser10 = "";
   public String olduser11 = "";
   public String olduser12 = "";
   public String olduser13 = "";
   public String olduser14 = "";
   public String olduser15 = "";
   public String olduser16 = "";
   public String olduser17 = "";
   public String olduser18 = "";
   public String olduser19 = "";
   public String olduser20 = "";
   public String olduser21 = "";
   public String olduser22 = "";
   public String olduser23 = "";
   public String olduser24 = "";
   public String olduser25 = "";

   public String pcw1 = "";
   public String pcw2 = "";
   public String pcw3 = "";
   public String pcw4 = "";
   public String pcw5 = "";
   public String pcw6 = "";
   public String pcw7 = "";
   public String pcw8 = "";
   public String pcw9 = "";
   public String pcw10 = "";
   public String pcw11 = "";
   public String pcw12 = "";
   public String pcw13 = "";
   public String pcw14 = "";
   public String pcw15 = "";
   public String pcw16 = "";
   public String pcw17 = "";
   public String pcw18 = "";
   public String pcw19 = "";
   public String pcw20 = "";
   public String pcw21 = "";
   public String pcw22 = "";
   public String pcw23 = "";
   public String pcw24 = "";
   public String pcw25 = "";

   public String oldpcw1 = "";
   public String oldpcw2 = "";
   public String oldpcw3 = "";
   public String oldpcw4 = "";
   public String oldpcw5 = "";
   public String oldpcw6 = "";
   public String oldpcw7 = "";
   public String oldpcw8 = "";
   public String oldpcw9 = "";
   public String oldpcw10 = "";
   public String oldpcw11 = "";
   public String oldpcw12 = "";
   public String oldpcw13 = "";
   public String oldpcw14 = "";
   public String oldpcw15 = "";
   public String oldpcw16 = "";
   public String oldpcw17 = "";
   public String oldpcw18 = "";
   public String oldpcw19 = "";
   public String oldpcw20 = "";
   public String oldpcw21 = "";
   public String oldpcw22 = "";
   public String oldpcw23 = "";
   public String oldpcw24 = "";
   public String oldpcw25 = "";

   public int guest_id1 = 0;
   public int guest_id2 = 0;
   public int guest_id3 = 0;
   public int guest_id4 = 0;
   public int guest_id5 = 0;
   public int guest_id6 = 0;
   public int guest_id7 = 0;
   public int guest_id8 = 0;
   public int guest_id9 = 0;
   public int guest_id10 = 0;
   public int guest_id11 = 0;
   public int guest_id12 = 0;
   public int guest_id13 = 0;
   public int guest_id14 = 0;
   public int guest_id15 = 0;
   public int guest_id16 = 0;
   public int guest_id17 = 0;
   public int guest_id18 = 0;
   public int guest_id19 = 0;
   public int guest_id20 = 0;
   public int guest_id21 = 0;
   public int guest_id22 = 0;
   public int guest_id23 = 0;
   public int guest_id24 = 0;
   public int guest_id25 = 0;

   public int oldguest_id1 = 0;
   public int oldguest_id2 = 0;
   public int oldguest_id3 = 0;
   public int oldguest_id4 = 0;
   public int oldguest_id5 = 0;
   public int oldguest_id6 = 0;
   public int oldguest_id7 = 0;
   public int oldguest_id8 = 0;
   public int oldguest_id9 = 0;
   public int oldguest_id10 = 0;
   public int oldguest_id11 = 0;
   public int oldguest_id12 = 0;
   public int oldguest_id13 = 0;
   public int oldguest_id14 = 0;
   public int oldguest_id15 = 0;
   public int oldguest_id16 = 0;
   public int oldguest_id17 = 0;
   public int oldguest_id18 = 0;
   public int oldguest_id19 = 0;
   public int oldguest_id20 = 0;
   public int oldguest_id21 = 0;
   public int oldguest_id22 = 0;
   public int oldguest_id23 = 0;
   public int oldguest_id24 = 0;
   public int oldguest_id25 = 0;

   public String userg1 = "";
   public String userg2 = "";
   public String userg3 = "";
   public String userg4 = "";
   public String userg5 = "";
   public String userg6 = "";
   public String userg7 = "";
   public String userg8 = "";
   public String userg9 = "";
   public String userg10 = "";
   public String userg11 = "";
   public String userg12 = "";
   public String userg13 = "";
   public String userg14 = "";
   public String userg15 = "";
   public String userg16 = "";
   public String userg17 = "";
   public String userg18 = "";
   public String userg19 = "";
   public String userg20 = "";
   public String userg21 = "";
   public String userg22 = "";
   public String userg23 = "";
   public String userg24 = "";
   public String userg25 = "";

   public boolean preview = false;
   public String preview_address = "";
   public PrintWriter out = null;

}  // end of class
