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
 *      5/22/14  kansascitycc-Added custom title &  changed the footer    
 *      5/06/14  Switch up the banner ads for St Cloud.
 *      4/04/14  Philly Cricket Recip Site (philcricketrecip) - Updated email address for French Creek.
 *      3/05/14  Added some sysLingo for Kopplin & Kuebler (Greg DeRosa) for their Meeting Scheduling site.
 *      2/10/14  Tartan Fields GC (tartanfields) - Added to sendOakmontEmail for receiving staff email notificaitons for guest times.
 *      2/10/14  TPC Snoqualmie Ridge (snoqualmieridge) - Added to sendOakmontEmail for receiving staff email notificaitons for guest times.
 *      2/10/14  Pinery CC (pinery) - Added to sendOakmontEmail for receiving staff email notificaitons for guest times.
 *      2/10/14  The Club at Pradera (pradera) - Added to sendOakmontEmail for receiving staff email notificaitons for guest times.
 *      2/10/14  sendIt will now add the orig_by for a reservation to the recipients list, if provided.
 *      2/07/14  Philly Cricket Recip Site (philcricketrecip) - Added additional email address.
 *      1/10/14  Pelican Marsh GC (pmarshgc) - Added custom to sendOakmontEmail to send the proshop an email whenever a member books a new tee time on the day of (case 2336).
 *      1/10/14  Bear Creek GC (bearcreekgc) - Added custom to sendOakmontEmail to send the pro an email whenever a notification is booked/modified/canceled (case 2344).
 *      1/02/14  Add logging to the email_audit_log tables when sending emails
 *      1/02/14  Change the background color of the emails to match the custom style sheet color if club uses a custom style sheet.
 *     12/20/13  Commented out AOL trailer message since we should no longer get blacklisted even if people do flag our emails as spam.
 *     12/10/13  Silverleaf Club (silverleaf) - Updated sendOakmontEmail to include details for sending the Silverleaf Club caddie master emails when caddie times are booked (case 2329).
 *     12/05/13  Add the Unsubscribe message to all emails and add checks for the new email option flags.
 *     10/25/13  Fixed issue that was causing tracked guest emails to not be sent out when part of an unaccompanied guest time where no members were assigned.
 *     10/03/13  Belfair CC (belfair) - Added custom to sendOakmontEmail to send an email to 4 staff members whenever a tee time containing guests is canceled (case 2307).
 *      9/25/13  Philly Cricket Recip Site (philcricketrecip) - Updated an email address.
 *      9/19/13  Philly Cricket Recip Site (philcricketrecip) - Added an email address.
 *      9/17/13  Fixed issue with aol trailer that was causing the message body to get duplicated in the resulting email.
 *      9/17/13  Philly Cricket Recip Site (philcricketrecip) - Updated an email address.
 *      9/16/13  Player # in tee time notification emails will now be based on the count of players instead of the number of the player slot to avoid disjointed numbering.
 *      8/15/13  Pine Brook CC (pinebrookcc) - Added custom to use a title of "Pine Brook CC Tennis Communication" instead of "Tennis Club Communication" for Tennis pro email tool emails.
 *      8/05/13  Philly Cricket Recip Site (philcricketrecip) - Added additional email addresses.
 *      8/01/13  Mirabel (mirabel) - Added custom template components to use for their proshopcom user.
 *      7/18/13  Brooklawn CC (brooklawn) - Removed custom email messages, since they are going to start using the custom email content feature instead (case 1540).
 *      7/18/13  Philly Cricket Recip Site (philcricketrecip) - Added additional email address.
 *      7/16/13  Potowomut GC (potowomut) - Added custom to use "potowomutreservations@foretees.com" in place of "auto-send@foretees.com" for emails (case 2284).
 *      7/16/13  Royal Oaks CC - Dallas (roccdallas) - Added custom to use an alternate club logo image for emails.
 *      7/08/13  Oak Hill CC (oakhillcc) - Removed custom to send golf chair an email whenever someone cancels a time.
 *      7/03/13  Philly Cricket Recip Site (philcricketrecip) - Added additional email addresses.
 *      7/01/13  Philly Cricket Recip Site (philcricketrecip) - Added additional email address.
 *      6/26/13  Philly Cricket Recip Site (philcricketrecip) - Added additional email addresses, and replaced some email addresses.
 *      6/20/13  Philly Cricket Recip Site (philcricketrecip) - Added an additional email address for Plymouth CC in email custom.
 *      6/06/13  Update some email addresses in the custom for Philly Cricket.
 *      5/30/13  Philadelphia Cricket Club Recip Site (philcricketrecip) - add email processing to sendOakmontEmail to send all emails to Pro at selected course.
 *               Also, changed all references to 'Cancelled' to 'Canceled'.
 *      5/16/13  Oak Hill CC (oakhillcc) - Add email custom to sendOakmontEmail to notify Golf Chair when anyone cancels a time.
 *      5/03/13  St Cloud CC (stcloudcc) - enable the custom that adds banner ads to all emails that go out (the club is selling ads).
 *      4/23/13  Added trap for AddressException when building the message.  Use transport instance instead of static transport.send method.
 *      3/08/13  Added sendFTNotification() method, which will allow canned email notifications to be sent off on-demand, such as in debug or error reporting.
 *      2/28/13  Denver CC (denvercc) - Custom email header banner images will now be used in place of the standard logos and header text.
 *      2/28/13  When building the iCal file for tee time emails set the end time value to tee time plus 4 hrs or 2 hrs based on player1's 9-hole setting. 
 *      2/27/13  Denver CC (denvercc) - Changed the verbiage in the new event signup message to say it was "RECEIVED" instead of "RESERVED" (case 2233).
 *      2/22/13  Check for courseName in email_content if golf email - only send for selected course(s).
 *      2/19/13  St Cloud CC - add custom banners to all emails.  They are selling ad space on our emails.
 *      2/15/13  Mirabel (mirabel) - Custom email header banner images will now be used in place of the standard logos and header text. Altered borders and bg colors to fit with this.
 *      2/13/13  St Cloud CC (stcloudcc) - Updated custom so that it will only check for their custom headers if the proshop user matches one of the custom users so default processing will still apply otherwise.
 *      1/21/13  Rehoboth Beach CC (rehobothbeachcc) - Added custom to send the caddie master an email if tee times containing "CAD" or "CFC" MoTs are booked/modified/canceled (case 2217).
 *      1/15/13  Baltusrol GC (baltusrolgc) - Commented out custom email message regarding guest dress code (case 1751).
 *      1/11/13  Updated processing to accomodate the new "masstee" email type, for when booking 6+ tee times.
 *      1/04/13  St Cloud CC (stcloudcc) - Added a custom title for emails sent by the "proshoppres" proshop user.
 *      1/03/13  St Cloud CC (stcloudcc) - Added a custom title for emails sent by the "proshopmarketin" proshop user.
 *     12/03/12  Add club name and email address to logerror messages in doSending and checkCalPref.
 *     11/08/12  Check for a shotgun event for modified tee time (tee sheet shotgun).
 *     10/30/12  1st or 10th tee indicators will now be included for groups 2-5 on lottery assignment emails.
 *     10/10/12  Blackhawk CC (blackhawk) - Added custom to email pro Tim Burr any time member Neal Mitchell is part of a tee time notification (case 1201).
 *      9/18/12  Added the list of players for the notification email that gets sent when an event team gets moved off of the wait list.
 *      7/27/12  Misquamicut Club (misquamicut) - Added custom to copy in 'kristen.majeika@gmail.com' on all FlxRez reservation notification emails (case 2170).
 *      7/18/12  Removed the line that explained the '9' appended to the MOT as it was confusing too many members.
 *      7/17/12  Change f_b text from Front or Back to '1st tee' or '10th tee' to avoid confusion (tee time related notifications).  Also, add a line that explains the 9 on the
 *               end of the MOT.
 *      7/17/12  doSending - add checks for Invoice emails (from Support_invoicing).
 *      7/17/12  Baltimore CC (baltimore) - Updated sendOakmontEmail to include details for sending the Baltimore CC caddie master emails when caddie times are booked (case 2158).
 *      7/11/12  Fixed issue where MoT defaults were being included after a player's name for FlxRez event emails. This will now only be done for golf.
 *      7/05/12  Edina CC (edina) - Added custom to display 'Tennis and Swim Communication' instead of the default 'Tennis Club Communication' in the email header for Tennis emails.
 *      6/27/12  Updated member/userg/guest email lookup so the tracked guest email lookup is bundled with the userg email lookup code instead of being an either-or situation.
 *      6/13/12  The Patterson Club (pattersonclub) - The email header will now use a custom font of "Copperplate Gothic Light".
 *      5/31/12  The Patterson Club (pattersonclub) - Added custom to display "The Patterson Club" in email notifications instead of "Golf Club Communication" (case 2164).
 *      5/16/12  Fort Collins CC (fortcollins) - Added custom so "Ball Machine" lingo will be used in place of Lesson verbiage.
 *      5/16/12  Sierra View CC (sierraviewcc) - Added custom so "Ball Machine" lingo will be used in place of Lesson verbiage.
 *      5/15/12  Use the dining logo when dining admin user is sending emails (Send Email feature).
 *      4/03/12  Ballantyne CC (ballantyne) - Added custom so "Ball Machine" lingo will be used in place of Lesson verbiage (case ?).
 *      4/03/12  Updated lesson emails and iCals to use SystemLingo entries for Lesson verbiage (for custom verbiage such as "Ball Machine").
 *      3/27/12  Add new option for Unaccompanied Guests Only for email content in reservations (case 2124).
 *      3/14/12  Birmingham CC (bhamcc) - Added custom to display "Golf Department Communication" in golf emails in stead of "Golf Club Communication" (case 2136).
 *      2/29/12  Fixed issue with event emails in the email tool so that all email links don't log members in as the same person.
 *      2/17/12  Updated email processing to allow event links to be used in the email tool, instead of requiring them to be a part of custom email content.
 *      1/27/12  Moved the sendEmail check for the notification sent to an event group that's getting moved off the wait list so that it doesn't rely on the cancellation email being sent to go out. If no one
 *               in the canceling group had email notifications turned on, no one in the group getting moved off the wait list would get sent a notification!
 *     12/23/11  Green Bay CC (greenbay) - Added custom to display "Country Club Communication" in golf emails instead of "Golf Club Communication" (case 2095).
 *     11/30/11  Olympic Club (olyclub) - Added a custom footer message and changed the verbiage in the new event signup message to say it was "RECEIVED" instead of "RESERVED" (case 2084).
 *     11/28/11  Olympic Club (olyclub) - If the author of an event notification email is a proshop user, display "the Tournament Coordinator" instead of the proshop username (case 2071).
 *     10/03/11  Fixed an issue with consecutive tee times where the start time values for groups 3 and 4 were using the incorrect variable and were not displaying as a result.
 *      9/15/11  The time of the reservation will now be displayed in the subject line of tee time and reservation notification emails (date as well for FlxRez, as it wasn't included either).
 *      9/01/11  Willamette Valley (willamette) - Removed custom emaill message regarding Auto Detailing service.
 *      8/31/11  Implement new iCalendar class
 *      7/30/11  Minor email template fix
 *      7/27/11  Patterson Club (pattersonclub) - Added custom to prevent guests from being sent new/mod/can email notifications. Set up easy use booleans for if other clubs want this (case 2007).
 *      7/20/11  Added iCalFoldContentLine() method to automatically truncate lines in iCals beyond a certain length and move the additional text to another line.
 *      7/05/11  Club at Mediterra (mediterra) - Updated sendMediterraEmail with a new email address, as well as the updated round limit (case 1262).
 *      5/18/11  Oak Hill CC (oakhillcc) - Removed custom email message when guest included in tee time (case 1868).
 *      4/27/11  Added override to getContentID method to help catch errors by retrying after we ensure the connection
 *      4/22/11  Changed doSeinding method so that it does not swap \n for br tags if here from the email tool on the pro side.
 *      4/21/11  Do not display "YOUR ACTUAL START TIME MAY VARY" message for event signup notificatione mails for FlxRez.
 *      4/14/11  Added BCC to our internal email address (emailprotool@) for emails sent from the proshop staff
 *      4/07/11  For FlxRez emails, if the court a reservation for has a email_name specified in the activities table, that name will be used in place of the root activity name in the email.
 *      4/05/11  The Country Club (tcclub) - Updated custom email message to remove an unwanted portion, as well as to no longer use a custom course name.
 *      4/05/11  Omaha CC (omahacc) - Added processing to send an email to the club if a member's email address is updated in ForeTees (case 1963).
 *      3/18/11  Updated processing to now include iCal attachments with lottery assignment emails.
 *      2/15/11  Add checks for embedded links in custom content so we insert the club name and username into the link for each member.
 *      2/14/11  Golf Academy of America Campuses (gaa*, non-classroom sites) - Do not include F/B information in tee time notifications (case 1931).
 *      1/14/11  Emails sent via the email tool will now have a header of "Golf Club Communication" or "<activity name> Club Communication" instead of simply "Club Communication"
 *      1/07/11  Add processing for the Lottery assign notifications from Proshop_dsheet.
 *     11/30/10  Email notifications will now be sent to sponsors of unaccompanied guests (case 1410).
 *     11/05/10  Added the date to the subject line for lottery request emails (new and modified)
 *     11/04/10  Changed how it's determined whether to use the ForeTees or FlxRez logo.  Now based solely on activity_id, regardless of email type.
 *     10/08/10  The Country Club (tcclub) - Updated custom email message, changed "Main Front 9/Primrose" to "Main Course Back 9/Primrose" and a couple other tweaks
 *      9/16/10  Fix for iCal attachments on pro copy emails for lesson bookings.
 *      8/27/10  Shady Canyon GC (shadycanyongolfclub) - Updated sendOakmontEmail to include custom for Shady Canyon (case 1881).
 *      8/25/10  Fixed a couple instances where Golf jargon was still appearing on FlxRez emails.
 *      8/24/10  The Country Club (tcclub) - Temporarily change "Main Course" to "Main Front 9/Primrose" for tee time notification emails during maint. period.
 *               Also added extra message regarding the maintenance period to existing custom message.
 *      8/19/10  Add current user's (non-proshop) email address to recipient list, so they receive a notification even if they are not part of the reservation
 *      8/10/10  Fort Collins/Greeley/Fox Hill (fortcollins) - Do not include the " - Fort Collins CC" at the end of email notifications
 *      7/20/10  Blackhawk - remove custom to blind copy the pro whenever an email goes to specific members (case #1201).
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
 *          We could add a default flag and add unique index to it and the location and activity.  Then it would be the default so that if
 *          nothing else was defined or found for that type of email/datetime/? it gets displayed
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


// foretees imports
import com.foretees.common.ProcessConstants;
import com.foretees.common.getClub;
import com.foretees.client.SystemLingo;


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

   public static int CONTENT_AREA_1 = 1;
   public static int LOWER_SPAN = 2;

   static int dining_activity_id = ProcessConstants.DINING_ACTIVITY_ID;    // Global activity_id for Dining System  
 
   
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
   int fb2 = parms.fb2;
   int fb3 = parms.fb3;
   int fb4 = parms.fb4;
   int fb5 = parms.fb5;
   int to_fb = parms.to_fb;
   int from_fb = parms.from_fb;
   int mm = parms.mm;
   int dd = parms.dd;
   int yy = parms.yy;
   int playerCount = 0;

   int emailNew = parms.emailNew;
   int emailMod = parms.emailMod;
   int emailCan = parms.emailCan;
   int emailRem = parms.emailRem;

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

   String user = parms.user;
   String orig_by = parms.orig_by;
   
   String [] eaddrA = new String [100];            // arrays to process email addresses (max possible = 100)

   String [] playerA = new String [25];            // player names 
   String [] oldplayerA = new String[25];          // old player names
   String [] userA = new String [25];              // usernames 
   String [] olduserA = new String [25];           // old usernames
   String [] usergA = new String [25];
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

   usergA[0] = parms.userg1;
   usergA[1] = parms.userg2;
   usergA[2] = parms.userg3;
   usergA[3] = parms.userg4;
   usergA[4] = parms.userg5;
   usergA[5] = parms.userg6;
   usergA[6] = parms.userg7;
   usergA[7] = parms.userg8;
   usergA[8] = parms.userg9;
   usergA[9] = parms.userg10;
   usergA[10] = parms.userg11;
   usergA[11] = parms.userg12;
   usergA[12] = parms.userg13;
   usergA[13] = parms.userg14;
   usergA[14] = parms.userg15;
   usergA[15] = parms.userg16;
   usergA[16] = parms.userg17;
   usergA[17] = parms.userg18;
   usergA[18] = parms.userg19;
   usergA[19] = parms.userg20;
   usergA[20] = parms.userg21;
   usergA[21] = parms.userg22;
   usergA[22] = parms.userg23;
   usergA[23] = parms.userg24;
   usergA[24] = parms.userg25;
   
   String tmp_sql = "SELECT (SELECT email FROM member2b WHERE username = ? AND email_bounced = 0) AS email1, "
                  + "(SELECT emailOpt FROM member2b WHERE username = ?) AS emailOpt, "
                  + "(SELECT email2 FROM member2b WHERE username = ? AND email2_bounced = 0) AS email2, "
                  + "(SELECT emailOpt2 FROM member2b WHERE username = ?) AS emailOpt2";
   
   String tmp_sql_gst = "SELECT (SELECT email1 FROM guestdb_data WHERE guest_id = ? AND email_bounced1 = 0) AS email1, "
                      + "(SELECT emailOpt FROM guestdb_data WHERE guest_id = ?) AS emailOpt, "
                      + "(SELECT email2 FROM guestdb_data WHERE guest_id = ? AND email_bounced2 = 0) AS email2";
   
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
   //String clubProEmail = "";    // used in blackhawk custom
   String teetimeMsg = "";      // custom extension for tee time notifications
   String lotteryText = "";
   String activityDisplayName = "";

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
   boolean skipGuestEmails_new = false;
   boolean skipGuestEmails_mod = false;
   boolean skipGuestEmails_can = false;
   boolean skipGuestEmails_rem = false;
   boolean skipOrigBy = false;              
     
   //
   //  Custom to skip the f/b indicator in tee time emails
   //
   if (club.equals( "tcclub" ) || club.equals("pinery") || club.equals("baltusrolgc") || (club.startsWith("gaa") && !club.endsWith("class"))) {

      skipFrontBack = true;              // do not use the f/b indicator in messages
   }

   // Option to skip guest email confirmations for new bookings
   if (emailNew != 0 && club.equals("pattersonclub")) {
       skipGuestEmails_new = true;
   }

   // Option to skip guest email confirmations for modified bookings
   if (emailMod != 0 && club.equals("pattersonclub")) {
       skipGuestEmails_mod = true;
   }

   // Option to skip guest email confirmations for canceled bookings
   if (emailCan != 0 && club.equals("pattersonclub")) {
       skipGuestEmails_can = true;
   }

   // Option to skip guest email confirmations for canceled bookings
   if (emailRem != 0 && club.equals("pattersonclub")) {
       skipGuestEmails_rem = true;
   } 

      /////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\
     //skip email for originator when they book/modifiy/cancel\\
    //           times that they are not a part of             \\      
   ////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
   if (club.equals("westmoor")) {
       skipOrigBy = true;
   }

   SystemLingo sysLingo = new SystemLingo();
   
   if (parms.type.equals("lesson") || parms.type.equals("lessongrp")) {
       
       sysLingo.setLingo("Lesson Book", club, parms.activity_id);
   }

   /*
   if (club.equals("tcclub") && parms.type.equals("tee") && parms.course.equals("Main Course")) {
       parms.course = "Main Course Back 9/Primrose";
   }
   */
   
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

   if (!parms.activity_email_name.equals("")) {
       activityDisplayName = parms.activity_email_name;
   } else if (!parms.activity_name.equals("")) {
       activityDisplayName = parms.activity_name;
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
   if (emailOk != 0 || parms.preview == true) { // this if block span the entire method!

      String to = "";                           // to address
      String to2 = "";                          // to address
      String f_b = "";
      String f_b2 = "";
      String f_b3 = "";
      String f_b4 = "";
      String f_b5 = "";
      String eampm = "";
      String etime = "";
      String etime2 = "";
      String etime3 = "";
      String etime4 = "";
      String etime5 = "";
      int emailOpt = 0;                         // user's email option parm
      int emailOpt2 = 0;
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
         rs = stmtN.executeQuery("SELECT clubName, no_reservations FROM club5 WHERE clubName != ''");

         if (rs.next()) {

            clubName = rs.getString("clubName");
            //clubProName = rs.getString(2);        // no longer used
            //clubProEmail = rs.getString(3);         // used in blackhawk custom
            TLT = rs.getInt("no_reservations");    // TLT Club ?
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
      
      if (parms.type.equals("lesson")) {
          time2 = parms.to_time;
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

        // f_b = "Front";
        // if (fb == 1) f_b = "Back";
          
         f_b = "1st";
         f_b2 = "1st";
         f_b3 = "1st";
         f_b4 = "1st";
         f_b5 = "1st";
         if (fb == 1) f_b = "10th";
         if (fb2 == 1) f_b2 = "10th";
         if (fb3 == 1) f_b3 = "10th";
         if (fb4 == 1) f_b4 = "10th";
         if (fb5 == 1) f_b5 = "10th";
      }
      
      //
      //   Does this club wish to hide the f/b?
      //
      if (skipFrontBack == true) {
          f_b = "";
          f_b2 = "";
          f_b3 = "";
          f_b4 = "";
          f_b5 = "";
      }


      String ext = "";
      String mod = "";
      String enew = "";
      String erem = "";
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
            subject = "ForeTees " +lotteryText+ " Notification (" + dayShort + " " + mm + "/" + dd + "/" + yy + ")";
            
         }
         
      } else if (parms.type.equals( "lassign" )) {            // Lottery Assigns from Proshop_dsheet

            if (clubName.startsWith( "Old Oaks" )) {

               enew = "The following Tee Time has been ASSIGNED.\n\n";
               subject = "ForeTees Tee Time Assignment Notification";
               
            } else if (clubName.startsWith( "Westchester" )) {

               enew = "The following Draw Tee Time has been ASSIGNED.\n\n";
               subject = "Your Tee Time for Weekend Draw";

            } else if (clubName.startsWith( "Pecan Plantation" )) {

               enew = "The following Tee Time Request has been ASSIGNED.\n\n";
               subject = "ForeTees Tee Time Assignment Notification";

            } else if (!lotteryText.equals( "" )) {      // if replacement text provided
            
               enew = "The following " +lotteryText+ " has been ASSIGNED.\n\n";
               subject = "ForeTees " +lotteryText+ " Assignment Notification (" + dayShort + " " + mm + "/" + dd + "/" + yy + ")";
            
            } else {

               enew = "The following Lottery Tee Time has been ASSIGNED.\n\n";
               subject = "ForeTees Lottery Assignment Notification (" + dayShort + " " + mm + "/" + dd + "/" + yy + ")";
            }
            
            //
            //  Override the message if the group was NOT assigned a time
            //
            if (parms.wuser1.equals("Unassigned")) {            // if players NOT assigned a time

                enew = "Sorry, the following group was NOT assigned a time.\n\n";
            }


           
      } else if (parms.type.equals( "tee" )) {

         if (TLT == 0) {     // if normal tee time system

            can = "The following tee time has been CANCELED by " + author + ".\n\n";
            mod = "The following tee time has been MODIFIED by " + author + ".\n\n";
            enew = "The following tee time has been RESERVED by " + author + ".\n\n";
            erem = "This is an automated reminder regarding your upcoming tee time.\n\n";

         } else {        // TLT system

            can = "The following Notification has been CANCELED by " + author + ".\n\n";
            mod = "The following Notification has been MODIFIED by " + author + ".\n\n";
            enew = "The following Notification has been SUBMITTED by " + author + ".\n\n";
            erem = "This is an automated reminder regarding your upcoming notification.\n\n";
         }
         
         if (club.equals("thereserveclub")) {
             enew = "We are in receipt of your Notification to play on the below date and time. Thank you for letting us know of your intention to play.";
         }

         if (club.equals( "westchester" )) {

            subject = "WCC - Your Tee Time" + (emailRem != 0 ? " Reminder" : "");

         } else {

            subject = "ForeTees Tee Time " + (emailRem != 0 ? "REMINDER" : "Notification") + " (" + dayShort + mm + "/" + dd + "/" + yy + " " + etime + ")";
         }

         
         if (club.equals( "oakmont" )) {       // if Oakmont - custom trailer message for all tee times

            oakmontTrlr = "\n\n\nDear Member," +
                                 "\n\nThe following information is a reminder for you and your guests:" +
                                 "\n\n* The use of all cell phones and electronic communication devices are permitted ONLY in the locker rooms and inside vehicles in the parking lot." +
                                 "\n\n* Members and Guests are expected to play a round of golf in four hours or less." +
                                 "\n\nThank you for following these customs and traditions of Oakmont Country Club, we hope to enhance everyone's Oakmont golf experience." +
                                 "\n\nThe Board of Governors";
         }
         
             /*
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
            */


         if (club.equals( "rhodeisland" )) {       // if Rhode Island CC - custom trailer message for all tee times

            teetimeMsg = "\n\n\nPlease advise your guests of our dress code and cell phone policies.  No cargo shorts " +
                         "or tee-shirts and all men's shirts must be completely tucked in.";
         }         
      
         
         if (club.equals( "santaana" )) {       // if Santa Ana CC - custom trailer message for all tee times

            teetimeMsg = "\n\n\nAttention Members:\n\nAs a courtesy to the other Members, if you will NOT be using your Tee Time, " +
                         "please CANCEL the Tee TIME in a timely fashion.\nYou May cancel on ForeTees or contact the Starter.\n " +
                         "THANK YOU!";
         }         
      
         /*
         if (club.equals( "baltusrolgc" )) {       // if Baltusrol GC - custom trailer message for all tee times

            teetimeMsg = "\n\n\nFor guest regulations, dress code information and directions to Baltusrol Golf Club, please visit www.baltusrol.org ";
         }  
          */
      
         
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
         
         if (club.equals("bloomfieldhillscc")) {
             subject = "BHCC Golf - Notification Confirmation ";
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
          
         if (club.equals("olyclub") && author.startsWith("proshop")) {
             author = "the Tournament Coordinator";
         }

         String waitlist_header = "";
         
         if (wait > 0) {
             waitlist_header = "Wait List ";
         }
         
         can = "The following Event " + waitlist_header + "Registration has been CANCELED by " + author + ".\n\n";
         mod = "The following Event " + waitlist_header + "Registration has been MODIFIED by " + author + ".\n\n";
         enew = "The following Event " + waitlist_header + "Registration has been RESERVED by " + author + ".\n\n";
         erem = "This is an automated reminder regarding your upcoming Event Registration.\n\n";
         ewait = "You, or your team, have been taken off the WAIT LIST and are now registered for the following event:\n\n";
         
         if (club.equals("olyclub") || (club.equals("denvercc") && parms.activity_id == 2)) {
             enew = "The following Event Registration has been RECEIVED, and was submitted by " + author + ".\n\n";
         }
           
         if (club.equals( "westchester" )) {

            subject = "WCC - Event Registration " + (emailRem != 0 ? "Reminder" : "Notification");

         } else {

             if (!activityDisplayName.equals("") && !activityDisplayName.equals("Golf")) {
                 subject = "FlxRez Event " + (wait > 0 ? "Waitlist " : "") + "Registration " + (emailRem != 0 ? "Reminder" : "Notification");
             } else {
                 subject = "ForeTees Event " + (wait > 0 ? "Waitlist " : "") + "Registration " + (emailRem != 0 ? "Reminder" : "Notification");
             }
         }
         
         if (club.equals("bloomfieldhillscc")) {
             subject = "BHCC Golf - Event Registration";
         }
         
      } else if (parms.type.equals( "lesson" )) {
         
         if (!activityDisplayName.equals("") && !activityDisplayName.equals("Golf")) {

            subject = "FlxRez - " + (emailRem != 0 ? "REMINDER - " : "") + activityDisplayName + " " + sysLingo.TEXT_Lesson_Reservation + " Notification (" + dayShort + mm + "/" + dd + "/" + yy + " " + etime + ")";
            enew = parms.player1 + " has been scheduled for the following " + activityDisplayName + " " + sysLingo.TEXT_Lesson + " (by " + author + "):\n\n";
            can = parms.player1 + "'s " + activityDisplayName + " " + sysLingo.TEXT_Lesson_Reservation + " has been canceled by " + author + ".\n\n";
            erem = "This is an automated reminder regarding your upcoming " + activityDisplayName + " " + sysLingo.TEXT_Lesson + ".\n\n";

         } else {
             
            if (club.equals("kopplinandkuebler")) {      // if Meeting Scheduling site for Kopplin & Kuebler (Greg DeRosa)
            
               subject = "Kopplin & Kuebler Interview " + (emailRem != 0 ? "Reminder" : "Notification");
               enew = parms.player1 + " has been scheduled for the following Interview by " + author + ".\n\n";
               can = parms.player1 + "'s Interview has been canceled by " + author + ".\n\n";
               erem = "This is an automated reminder regarding your upcoming Interview.\n\n";
               
               
            } else {
            
               subject = "ForeTees - " + (emailRem != 0 ? "REMINDER - " : "") + "Golf Lesson Notification (" + dayShort + mm + "/" + dd + "/" + yy + " " + etime + ")";
               enew = parms.player1 + " has been scheduled for the following Golf Lesson (by " + author + ").\n\n";
               can = parms.player1 + "'s Golf Lesson has been canceled by " + author + ".\n\n";
               erem = "This is an automated reminder regarding your upcoming Golf Lesson.\n\n";
            }
         }

         if (club.equals( "westchester" )) {

            subject = "WCC - Golf Lesson " + (emailRem != 0 ? "Reminder" : "Notification");
         }
         
         if (club.equals("bloomfieldhillscc")) {
             subject = "BHCC Golf-Golf Lesson Notification";
         }
         
      } else if (parms.type.equals( "lessongrp" )) {

         enew = parms.player1 + " has been added to the following Group Lesson (by " + author + "):\n\n";
         can = parms.player1 + " has been removed from the following Group Lesson (by " + author + "):\n\n";
         erem = "This is an automated reminder regarding your upcoming Group Lesson.\n\n";

         if (club.equals( "westchester" )) {

            subject = "WCC - Group Lesson " + (emailRem != 0 ? "Reminder" : "Notification");

         } else {

            if (!activityDisplayName.equals("") && !activityDisplayName.equals("Golf")) {
                subject = "FlxRez - " + (emailRem != 0 ? "REMINDER - " : "") + activityDisplayName + " Group Lesson Notification (" + dayShort + mm + "/" + dd + "/" + yy + " " + etime + ")";
            } else {
                subject = "ForeTees - " + (emailRem != 0 ? "REMINDER - " : "") + "Golf Group Lesson Notification (" + dayShort + mm + "/" + dd + "/" + yy + " " + etime + ")";
            }
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
          
         can = "The following Wait List Sign-up has been CANCELED by " + author + ".\n\n";
         mod = "The following Wait List Sign-up has been MODIFIED by " + author + ".\n\n";
         enew = "The following Wait List Sign-up has been CREATED by " + author + ".\n\n";
         //ewait = "You, or your team, have been taken off the WAIT LIST and are now registered for the following event:\n\n";
           
         subject = "ForeTees - Wait List Sign-up Notification";
          
      } else if (parms.type.equals( "password" )) {       // if from Login (password request)

         subject = "ForeTees - Credentials";
         
      } else if (parms.type.equals( "drequest" )) {       // if from Dining Request
          
         can = "The following Dining Request has been canceled by " + author + ".\n\n";
         mod = "The following Dining Request has been modified by " + author + ".\n\n";
         enew = "The following Dining Request has been created by " + author + ".\n\n";
         erem = "This is an automated reminder regarding your upcoming Dining Request.\n\n";
         
         subject = "ForeTees - " + (emailRem != 0 ? "REMINDER - " : "") + "Dining Request";
         
      } else if (parms.type.equals( "activity" )) {
          
         can = "The following " + activityDisplayName + " reservation has been CANCELED by " + author + ".\n\n";
         mod = "The following " + activityDisplayName + " reservation has been MODIFIED by " + author + ".\n\n";
         enew = "The following " + activityDisplayName + " reservation has been CREATED by " + author + ".\n\n";
         erem = "This is an automated reminder regarding your upcoming " + activityDisplayName + " reservation.\n\n";

         subject = "FlxRez - " + (emailRem != 0 ? "REMINDER - " : "") + activityDisplayName + " Reservation (" + dayShort + mm + "/" + dd + "/" + yy + " " + etime + ")";

      } else if (parms.type.equals( "masstee" )) {
          
         enew = "A total of " + parms.groups + " tee times have been RESERVED by " + author + ".\n\n";
         
         subject = "ForeTees Tee Time Notification (" + dayShort + mm + "/" + dd + "/" + yy + " starting at " + etime + ")";
          
      } else if (parms.type.equals("diningrem")) {    // Dining reminder notification
          
          subject = "ForeTees Dining - REMINDER - " + parms.dining_disp_date + " at " + parms.dining_disp_time;
          
      } else if (parms.type.equals("diningeventrem")) {
          
          subject = "ForeTees Dining - REMINDER - " + parms.name + " - " + parms.dining_disp_date + " at " + parms.dining_disp_time;
          
      }
      
      
      if (!clubName.equals( "" ) && !parms.type.equals("drequest") && !club.equals("fortcollins")) {

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
               pstmte1.setString(4, userA[0]);
               rs = pstmte1.executeQuery();      // execute the prepared stmt

               if (rs.next()) {

                  to = rs.getString("email1");           // user's email address
                  emailOpt = rs.getInt("emailOpt");      // email option
                  emailOpt2 = rs.getInt("emailOpt2");    // email option for 2nd address
                  to2 = rs.getString("email2");          // user's 2nd email address

                  if (!to.equals( "" ) && emailOpt != 0) {

                        eaddrTo.add(new ArrayList<String>());
                        eaddrTo.get(eaddrTo.size() - 1).add(to);
                        eaddrTo.get(eaddrTo.size() - 1).add(userA[0]);
                        send = 1;
                  }
                  if (!to2.equals( "" ) && emailOpt2 != 0) {     // if 2nd email address

                        eaddrTo.add(new ArrayList<String>());
                        eaddrTo.get(eaddrTo.size() - 1).add(to2);
                        eaddrTo.get(eaddrTo.size() - 1).add(userA[0]);
                        send = 1;
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


      } else if (parms.type.equals("diningrem") || parms.type.equals("diningeventrem")) {

          if (parms.dining_users.size() > 0) {
              
              String in_string = "";
              String dinMsgHtml = "";
              String dinMsgText = "";
              StringBuffer vCalMsg = new StringBuffer();  // no vCal for dining reminder emails
              
              replyTo = "";

              // Gather email addresses for reservees
              try {

                  for (String dining_user : parms.dining_users) {
                      in_string += "?,";
                  }

                  in_string = in_string.substring(0, in_string.length() - 1);    // Remove extra comma from end

                  pstmte1 = con.prepareStatement("SELECT username, email, email2, email_bounced, email2_bounced, emailOpt, emailOpt2 FROM member2b WHERE username IN (" + in_string + ")");
                  pstmte1.clearParameters();

                  int ind = 1;
                  
                  for (String dining_user : parms.dining_users) {
                      pstmte1.setString(ind++, dining_user);
                  }

                  rs = pstmte1.executeQuery();
                  
                  while (rs.next()) {
                      
                      if (rs.getInt("emailOpt") > 0 && !rs.getString("email").equals("") && rs.getInt("email_bounced") == 0) {
                          eaddrTo.add(new ArrayList<String>());
                          eaddrTo.get(eaddrTo.size() - 1).add(rs.getString("email"));
                          eaddrTo.get(eaddrTo.size() - 1).add(rs.getString("username"));
                          send = 1;
                      }
                      
                      if (rs.getInt("emailOpt2") > 0 && !rs.getString("email2").equals("") && rs.getInt("email2_bounced") == 0) {
                          eaddrTo.add(new ArrayList<String>());
                          eaddrTo.get(eaddrTo.size() - 1).add(rs.getString("email2"));
                          eaddrTo.get(eaddrTo.size() - 1).add(rs.getString("username"));
                          send = 1;
                      }
                  }
                  
              } catch (Exception e) {
                  Utilities.logError("sendEmail.sendIt - " + club + " - Error gathering email addresses for dining reminder emails - Error=" + e.toString());
              } finally {
                  Connect.close(rs, pstmte1);
              }
              
              // We want to match the dining email formatting, which uses full HTML, so define both a text and HTML version of the email content.
              
              // Define text email content
              dinMsgText = "This is a reminder of your upcoming reservation.\n\n"
                      + "Reservation #: " + parms.dining_resNum + "\n";
              
              if (parms.type.equals("diningeventrem")) {
                  dinMsgText += "Event: " + parms.name + "\n";
              }
              
              dinMsgText += "Date: " + parms.dining_disp_date + "\n"
                      + "Time: " + parms.dining_disp_time + "\n"
                      + "Location: " + parms.course + "\n"
                      + "Dining Party:\n  " + parms.dining_names.get(0) + "\n";
              
              for (int j = 1; j < parms.dining_names.size(); j++) {
                  dinMsgText += "  " + parms.dining_names.get(j) + "\n";
              }
              
              if (parms.type.equals("diningeventrem")) {
                  dinMsgText += parms.guests + " total\n"
                          + "Dress Code: " + parms.notes + "\n"
                          + "Cancellation Policy: " + parms.to_time + " hours prior to the event\n";
              } else {
                  dinMsgText += parms.guests + " total\n";
              }
              
              parms.txtBody = dinMsgText;
              
              
              
              // Define HTML email content
              dinMsgHtml = "This is a reminder of your upcoming reservation."
                      + "<br><br><table>"
                      + "<tr><td>Reservation #: </td><td><span style=\"font-weight: bold;\">" + parms.dining_resNum + "</span></td></tr>";
              
              if (parms.type.equals("diningeventrem")) {
                  dinMsgHtml += "<tr><td>Event: </td><td><span style=\"font-weight: bold;\">" + parms.name + "</span></td></tr>";
              }
              
              dinMsgHtml += "<tr><td>Date: </td><td><span style=\"font-weight: bold;\">" + parms.dining_disp_date + "</span></td></tr>"
                      + "<tr><td>Time: </td><td><span style=\"font-weight: bold;\">" + parms.dining_disp_time + "</span></td></tr>"
                      + "<tr><td>Location: </td><td><span style=\"font-weight: bold;\">" + parms.course + "</span></td></tr>"
                      + "<tr><td>Dining Party: </td><td><span style=\"font-weight: bold;\">" + parms.dining_names.get(0) + "</span></td></tr>";
              
              for (int j = 1; j < parms.dining_names.size(); j++) {
                  dinMsgHtml += "<tr><td>&nbsp;</td><td>" + parms.dining_names.get(j) + "</td></tr>";
              }
              
              if (parms.type.equals("diningeventrem")) {
                  dinMsgHtml += "<tr><td>&nbsp;</td><td><span style=\"font-weight: bold;\">" + parms.guests + " total</span></td></tr>"
                          + "<tr><td>Dress Code: </td><td><span style=\"font-weight: bold;\">" + parms.notes + "</span></td></tr>"
                          + "<tr><td>Cancellation Policy: </td><td><span style=\"font-weight: bold;\">" + parms.to_time + " hours prior to the event</span></td></tr>";
              } else {
                  dinMsgHtml += "<tr><td>&nbsp;</td><td><span style=\"font-weight: bold;\">" + parms.guests + " total</span></td></tr>";
              }
              
              dinMsgHtml += "</table>";
              
              parms.htmlBody = dinMsgHtml;
              
              doSending(eaddrTo, eaddrProCopy, replyTo, subject, "", vCalMsg, parms, con);
          }
          
      } else {                      // NOT a dining request, lesson type or password request.


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
          for (i = 0; i < 25; i++) {

              if (!olduserA[i].equals("")) {      // if old user exists

                      try {
                          pstmte1 = con.prepareStatement(tmp_sql);

                          pstmte1.clearParameters();        // clear the parms
                          pstmte1.setString(1, olduserA[i]);
                          pstmte1.setString(2, olduserA[i]);
                          pstmte1.setString(3, olduserA[i]);
                          pstmte1.setString(4, olduserA[i]);
                          rs = pstmte1.executeQuery();      // execute the prepared stmt

                          if (rs.next()) {

                              to = rs.getString("email1");           // user's email address
                              emailOpt = rs.getInt("emailOpt");      // email option
                              emailOpt2 = rs.getInt("emailOpt2");    // email option for 2nd address
                              to2 = rs.getString("email2");          // user's 2nd email address

                              if (emailOpt != 0 && emailOpt > 0) {             // if user wants email notifications

                                  if (!to.equals("")) {

                                      eaddrTo.add(new ArrayList<String>());
                                      eaddrTo.get(eaddrTo.size() - 1).add(to);
                                      eaddrTo.get(eaddrTo.size() - 1).add(olduserA[i]);
                                      send = 1;
                                  }

                                  if (!to2.equals("") && emailOpt2 > 0) {

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
                          if (club.equals("blackhawk")) {   // if Blackhawk CC (Mark Caufield)

                              if (olduserA[i].equals("1237")) {   // if one of specified members 

                                  if (send == 1 && BHproCCed == false) {         // if members to send to

                                      eaddrProCopy.add("tburr@blackhawkcc.org");
                                      BHproCCed = true;           // indicate pro CC'ed already
                                  }
                              }

                          } // end if blackhawk CC

                      } catch (Exception ignore) {
                      }

              } else if (oldguest_idA[i] != 0) { // end if old user found

                if ((emailNew != 0 && !skipGuestEmails_new) || (emailMod != 0 && !skipGuestEmails_mod) || (emailCan != 0 && !skipGuestEmails_can)) {

                    try {
                      pstmte1 = con.prepareStatement ( tmp_sql_gst );

                      pstmte1.clearParameters();        // clear the parms
                      pstmte1.setInt(1, oldguest_idA[i]);
                      pstmte1.setInt(2, oldguest_idA[i]);
                      pstmte1.setInt(3, oldguest_idA[i]);
                      rs = pstmte1.executeQuery();      // execute the prepared stmt

                      if (rs.next()) {

                         to = rs.getString("email1");           // user's email address
                         emailOpt = rs.getInt("emailOpt");      // email option
                         to2 = rs.getString("email2");          // user's 2nd email address
 
                         if ( !to.equals( "" ) && emailOpt != 0 ) {

                            eaddrTo.add(new ArrayList<String>());
                            eaddrTo.get(eaddrTo.size() - 1).add(to);
                            eaddrTo.get(eaddrTo.size() - 1).add("");    // Do not include any sort of ID at this point
                            send = 1;
                         }

                         if ( !to2.equals( "" ) && emailOpt != 0 ) {

                            eaddrTo.add(new ArrayList<String>());
                            eaddrTo.get(eaddrTo.size() - 1).add(to2);
                            eaddrTo.get(eaddrTo.size() - 1).add("");    // Do not include any sort of ID at this point
                            send = 1;
                         }
                       
                      }
                      pstmte1.close();              // close the stmt

                   } catch (Exception ignore) {}
                }
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
         for (i = 0; i < 25; i++) {

              if (!userA[i].equals("")) {      // if new user exists
                  
                      try {

                          err = "SQL user=" + userA[i];

                          pstmte1 = con.prepareStatement(tmp_sql);

                          pstmte1.clearParameters();        // clear the parms
                          pstmte1.setString(1, userA[i]);
                          pstmte1.setString(2, userA[i]);
                          pstmte1.setString(3, userA[i]);
                          pstmte1.setString(4, userA[i]);
                          rs = pstmte1.executeQuery();      // execute the prepared stmt

                          if (rs.next()) {

                              to = rs.getString("email1");           // user's email address
                              emailOpt = rs.getInt("emailOpt");      // email option
                              emailOpt2 = rs.getInt("emailOpt2");    // email option for 2nd address
                              to2 = rs.getString("email2");          // user's 2nd email address

                              err = "to=" + to;
                              if (to != null && !to.equals("") && emailOpt != 0) {

                                  eaddrTo.add(new ArrayList<String>());
                                  eaddrTo.get(eaddrTo.size() - 1).add(to);
                                  eaddrTo.get(eaddrTo.size() - 1).add(userA[i]);
                                  send = 1;
                              }

                              err = "to2=" + to2;
                              if (to2 != null && !to2.equals("") && emailOpt2 != 0) {

                                  eaddrTo.add(new ArrayList<String>());
                                  eaddrTo.get(eaddrTo.size() - 1).add(to2);
                                  eaddrTo.get(eaddrTo.size() - 1).add(userA[i]);
                                  send = 1;
                              }

                          }
                          pstmte1.close();              // close the stmt

                      } catch (Exception exc) {

                          Utilities.logError("sendEmail: i=" + i + ", err=" + err + ", msg=" + exc.getMessage() + ", str=" + exc.toString());
                      }

               //
                      //  Custom for Blackhawk CC - BCC the pro
                      //
                      if (club.equals("blackhawk")) {   // if Blackhawk CC (Mark Caufield)

                          if (userA[i].equals("1237")) {   // if one of specified members

                              if (send == 1 && BHproCCed == false) {         // if members to send to

                                  eaddrProCopy.add("tburr@blackhawkcc.org");
                                  BHproCCed = true;           // indicate pro CC'ed already
                              }
                          }
                      } // end if blackhawk cc

              } else {    // user not present - check for guest
               
               if (guestsIncluded == false) {      // don't bother checking if guest already found
                  
                  if (!playerA[i].equals( "" ) && !playerA[i].equalsIgnoreCase( "X" )) {  // if player present & NOT X     

                     guestsIncluded = true;
                  }
               }

               // If userg value is populated for this guest, see if that username is already a part of this reservation.  If so, skip them.
               if (!usergA[i].equals("")) {
                   
                   boolean existingUser = false;

                   for (int j=0; j < i; j++) {

                       if (usergA[i].equals(userA[j]) || usergA[i].equals(usergA[j])) {
                           
                           existingUser = true;
                           break;
                       }
                   }

                   if (!existingUser) {

                       try {

                           err = "SQL user="+usergA[i];

                           pstmte1 = con.prepareStatement ( tmp_sql );

                           pstmte1.clearParameters();        // clear the parms
                           pstmte1.setString(1, usergA[i]);
                           pstmte1.setString(2, usergA[i]);
                           pstmte1.setString(3, usergA[i]);
                           pstmte1.setString(4, usergA[i]);
                           rs = pstmte1.executeQuery();      // execute the prepared stmt

                           if (rs.next()) {

                               to = rs.getString("email1");           // user's email address
                               emailOpt = rs.getInt("emailOpt");      // email option
                               emailOpt2 = rs.getInt("emailOpt2");    // email option for 2nd address
                               to2 = rs.getString("email2");          // user's 2nd email address
 
                                err = "to="+to;
                                if (to != null && !to.equals( "" ) && emailOpt != 0 ) {

                                    eaddrTo.add(new ArrayList<String>());
                                    eaddrTo.get(eaddrTo.size() - 1).add(to);
                                    eaddrTo.get(eaddrTo.size() - 1).add("");    // Do not include any sort of ID at this point
                                    send = 1;
                                }

                                err = "to2="+to2;
                                if ( to2 != null && !to2.equals( "" ) && emailOpt2 != 0 ) {

                                    eaddrTo.add(new ArrayList<String>());
                                    eaddrTo.get(eaddrTo.size() - 1).add(to2);
                                    eaddrTo.get(eaddrTo.size() - 1).add("");    // Do not include any sort of ID at this point
                                    send = 1;
                                }

                           }
                           pstmte1.close();              // close the stmt

                       } catch (Exception exc) {

                           Utilities.logError("sendEmail: error adding unaccompanied guest email, club=" + club + ", msg="+exc.getMessage());
                       }
                   }
               }
                   
                // Check if they're a Tracked Guest and add their email address(es) if they have one
                if (guest_idA[i] != 0) {

                    if ((emailNew != 0 && !skipGuestEmails_new) || (emailMod != 0 && !skipGuestEmails_mod) 
                            || (emailCan != 0 && !skipGuestEmails_can) || (emailRem != 0 && !skipGuestEmails_rem)) {

                        guestsIncluded = true;

                        try {

                            err = "SQL user=" + userA[i];

                            pstmte1 = con.prepareStatement(tmp_sql_gst);

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

                                    err = "to=" + to;
                                    if (to != null && !to.equals("")) {

                                        eaddrTo.add(new ArrayList<String>());
                                        eaddrTo.get(eaddrTo.size() - 1).add(to);
                                        eaddrTo.get(eaddrTo.size() - 1).add("");    // Do not include any sort of ID at this point
                                        send = 1;
                                    }

                                    err = "to2=" + to2;
                                    if (to2 != null && !to2.equals("")) {

                                        eaddrTo.add(new ArrayList<String>());
                                        eaddrTo.get(eaddrTo.size() - 1).add(to2);
                                        eaddrTo.get(eaddrTo.size() - 1).add("");    // Do not include any sort of ID at this point
                                        send = 1;
                                    }

                                } // end if emailOpt != 0
                            }
                            pstmte1.close();              // close the stmt

                        } catch (Exception exc) {

                            Utilities.logError("sendEmail: i=" + i + ", err=" + err + ", msg=" + exc.getMessage() + ", str=" + exc.toString());
                        }
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
             
         // If Misquamicut Club, copy a pro in for all FlxRez reservation emails (new/mod/cancel).
         if (club.equals("misquamicut") && parms.type.equals("activity")) {
             
             eaddrProCopy.add("kristen.majeika@gmail.com");

             send = 1;
             
         } else if (club.equals("marbellacc") && parms.type.equals("activity") && !ProcessConstants.isProshopUser(user)) {
             
             eaddrProCopy.add("tennis@marbellacc.net");
             
             send = 1;
         } else if (club.equals("pinebrookcc") && parms.type.equals("activity") && emailCan == 1) { //send email noticifations to pro when someone cancels court reservations

             eaddrProCopy.add("JSharton@pbccma.com");
             
             send = 1;
         }


          if (!ProcessConstants.isProshopUser(user)) {
              if ((skipOrigBy && !user.equals(orig_by) && !orig_by.equals("")) || !skipOrigBy) {
                  try {

                      err = "SQL user=" + user;

                      pstmte1 = con.prepareStatement(tmp_sql);

                      pstmte1.clearParameters();        // clear the parms
                      pstmte1.setString(1, user);
                      pstmte1.setString(2, user);
                      pstmte1.setString(3, user);
                      pstmte1.setString(4, user);
                      rs = pstmte1.executeQuery();      // execute the prepared stmt

                      if (rs.next()) {

                          to = rs.getString("email1");           // user's email address
                          emailOpt = rs.getInt("emailOpt");      // email option
                          emailOpt2 = rs.getInt("emailOpt2");    // email option for 2nd address
                          to2 = rs.getString("email2");          // user's 2nd email address

                          err = "to=" + to;
                          if (to != null && !to.equals("") && emailOpt != 0) {

                              eaddrTo.add(new ArrayList<String>());
                              eaddrTo.get(eaddrTo.size() - 1).add(to);
                              eaddrTo.get(eaddrTo.size() - 1).add(user);
                              send = 1;
                          }

                          err = "to2=" + to2;
                          if (to2 != null && !to2.equals("") && emailOpt2 != 0) {

                              eaddrTo.add(new ArrayList<String>());
                              eaddrTo.get(eaddrTo.size() - 1).add(to2);
                              eaddrTo.get(eaddrTo.size() - 1).add(user);
                              send = 1;
                          }

                      }
                      pstmte1.close();              // close the stmt

                  } catch (Exception exc) {

                      Utilities.logError("sendEmail: i=" + i + ", err=" + err + ", msg=" + exc.getMessage() + ", str=" + exc.toString());
                  }
              }
          }
         
          // Add the originator of the reservation to the email, if included, not a proshop user, and not a reminder email
          if (!orig_by.equals("") && !ProcessConstants.isProshopUser(orig_by) && emailRem == 0) {
              if (!skipOrigBy) {

                  try {

                      err = "SQL user=" + orig_by;

                      pstmte1 = con.prepareStatement(tmp_sql);

                      pstmte1.clearParameters();        // clear the parms
                      pstmte1.setString(1, orig_by);
                      pstmte1.setString(2, orig_by);
                      pstmte1.setString(3, orig_by);
                      pstmte1.setString(4, orig_by);
                      rs = pstmte1.executeQuery();      // execute the prepared stmt

                      if (rs.next()) {

                          to = rs.getString("email1");           // originator's email address
                          emailOpt = rs.getInt("emailOpt");      // email option
                          emailOpt2 = rs.getInt("emailOpt2");    // email option for 2nd address
                          to2 = rs.getString("email2");          // originator's 2nd email address

                          err = "to=" + to;
                          if (to != null && !to.equals("") && emailOpt != 0) {

                              eaddrTo.add(new ArrayList<String>());
                              eaddrTo.get(eaddrTo.size() - 1).add(to);
                              eaddrTo.get(eaddrTo.size() - 1).add(orig_by);
                              send = 1;
                          }

                          err = "to2=" + to2;
                          if (to2 != null && !to2.equals("") && emailOpt2 != 0) {

                              eaddrTo.add(new ArrayList<String>());
                              eaddrTo.get(eaddrTo.size() - 1).add(to2);
                              eaddrTo.get(eaddrTo.size() - 1).add(orig_by);
                              send = 1;
                          }

                      }
                      pstmte1.close();              // close the stmt

                  } catch (Exception exc) {

                      Utilities.logError("sendEmail: i=" + i + ", err=" + err + ", msg=" + exc.getMessage() + ", str=" + exc.toString());
                  }
              }
          }


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
      if (send != 0 || parms.preview == true) {        // this IF block spans almost this entire method

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
           
         if (club.equals("oakhillcc") && (parms.type.equals("tee") || parms.type.equals("masstee"))) {
             for (int j = 0; j < 25; j++) {
                 if (!parms.getPlayer(j).equals("") && parms.getUser(j).equals("") && !parms.getPlayer(j).equalsIgnoreCase("X")) {
                     parms.setPlayer("Guest", j);
                 }
                 if (!parms.getOldPlayer(j).equals("") && parms.getOldUser(j).equals("") && !parms.getOldPlayer(j).equalsIgnoreCase("X")) {
                     parms.setOldPlayer("Guest", j);
                 }
             }
         }
         
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
                    act_name = "\n\nLocation: " + parms.actual_activity_name + "  ";
                }

                if (parms.type.equals("lessongrp")) {         // if group lesson 
                    
                    enewMsg = header + enew + "Group Lesson: " + parms.course + " "
                            + "\n\nDate: " + (!parms.day.trim().equals("") ? parms.day + ", " : "") + mm + "/" + dd + "/" + yy
                            + "\n\nTime: " + etime + " "
                            + "\n\n" + sysLingo.TEXT_Lesson_Pro + ": " + proName;

                } else {              // normal lesson

                    enewMsg = header + enew + sysLingo.TEXT_Lesson + " Type: " + parms.course + "  "
                            + act_name + "  "
                            + "\n\nDate: " + parms.day + ", " + mm + "/" + dd + "/" + yy
                            + "\n\nTime: " + etime + " "
                            + "\n\n" + sysLingo.TEXT_Lesson_Pro + ": " + proName;
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

                          enewMsg = enewMsg + "\n\nEvent starts at " + parms.act_time + "." + (parms.activity_id == 0 ? "  YOUR ACTUAL START TIME MAY VARY." : "");
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
                   
                  if (parms.type.equals("lassign") && parms.wuser1.equals("Unassigned")) {    // if lottery and players NOT assigned a time

                        enewMsg = header + enew + "On " + parms.day + " " + mm + "/" + dd + "/" + yy;
                      
                        if (!parms.course.equals( "" )) {

                            enewMsg = enewMsg + " on Course: " + parms.course;
                        }
                            
                  } else {

                        if (parms.type.equals("lassign")) etime = parms.wuser1;        // Proshop_dsheet uses wuser for time string

                        enewMsg = header + enew + parms.day + " " + mm + "/" + dd + "/" + yy + (parms.type.equals("masstee") ? " starting" : "") + " at " + etime;

                        if (etype == 1) {           // if tee time during a Shotgun event

                            enewMsg = enewMsg + " (Shotgun)";

                            if (!parms.course.equals( "" )) {

                                enewMsg = enewMsg + " on Course: " + parms.course;
                            }
                            
                        } else { 

                          if (!f_b.equals ( "" )) {

                              // enewMsg = enewMsg + "on the " + f_b + " 9";
                              enewMsg = enewMsg + (!parms.type.equals("masstee") ? " starting" : "") + " on the " + f_b + " tee";

                              if (!parms.course.equals( "" )) {

                                  enewMsg = enewMsg + " of Course: " + parms.course;
                              }

                          } else {

                              if (!parms.course.equals( "" )) {

                                  enewMsg = enewMsg + " on Course: " + parms.course;
                              }
                          }
                      }

                      if (parms.type.equals("masstee")) {

                          enewMsg += "\n\nThese tee times can be accessed individually from the tee sheet to add players.";
                      }
                  }
               }

               enewMsg = enewMsg + "\n";
               //enewMsg = enewMsg + "\n\nNote: a '9' appended to the end of your mode of tranpsoration option indicates that you are scheduled to play 9 holes.\n";
               
               //
               //  build message text
               //
               if (!parms.player1.equals( "" )) {
                   
                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player1 + (parms.activity_id == 0 ? "  " + parms.pcw1 : "");
               }
               if (!parms.player2.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player2 + (parms.activity_id == 0 ? "  " + parms.pcw2 : "");
               }
               if (!parms.player3.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player3 + (parms.activity_id == 0 ? "  " + parms.pcw3 : "");
               }
               if (!parms.player4.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player4 + (parms.activity_id == 0 ? "  " + parms.pcw4 : "");
               }
               if (!parms.player5.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player5 + (parms.activity_id == 0 ? "  " + parms.pcw5 : "");
               }

               
               if (!etime2.equals("") && !parms.type.equals("waitlist") && !parms.type.equals("lassign")) {   // (etime2 is also used for waitlist)
                  
                  players = players + "\n\n" + etime2;
               }
               
               if (!etime2.equals("") && parms.type.equals("lassign")) {     // if lottery assign - break into groups 
                  
                  players = players + "\n\nGroup 2: At " + parms.wuser2;
                  
                  if (!f_b2.equals ( "" )) {
                  
                      players = players + " starting on the " + f_b2 + " tee";

                       if (!parms.oldplayer2.equals("")) {

                           players = players + " of Course: " + parms.oldplayer2;
                       }

                  } else {

                       if (!parms.oldplayer2.equals("")) {                      // course name saved in oldplayer by Proshop_dsheet

                           players = players + " on Course: " + parms.oldplayer2;
                       }
                  }
                  
                  players = players + "\n";
               }
               
               if (!parms.player6.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player6 + (parms.activity_id == 0 ? "  " + parms.pcw6 : "");
               }
               if (!parms.player7.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player7 + (parms.activity_id == 0 ? "  " + parms.pcw7 : "");
               }
               if (!parms.player8.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player8 + (parms.activity_id == 0 ? "  " + parms.pcw8 : "");
               }
               if (!parms.player9.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player9 + (parms.activity_id == 0 ? "  " + parms.pcw9 : "");
               }
               if (!parms.player10.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player10 + (parms.activity_id == 0 ? "  " + parms.pcw10 : "");
               }
               
               
               if (!etime3.equals("") && !parms.type.equals("lassign")) {
                  
                  players = players + "\n\n" + etime3;
               }
               
               if (!etime3.equals("") && parms.type.equals("lassign")) {     // if lottery assign - break into groups 
                  
                  players = players + "\n\nGroup 3: At " + parms.wuser3;
                  
                  if (!f_b3.equals ( "" )) {
                  
                      players = players + " starting on the " + f_b3 + " tee";
                      
                      if (!parms.oldplayer3.equals("")) {
                          
                          players = players + " of Course: " + parms.oldplayer3;
                      }

                  } else {
                      
                      if (!parms.oldplayer3.equals( "" )) {                      // course name saved in oldplayer by Proshop_dsheet

                         players = players + " on Course: " + parms.oldplayer3;
                      }
                  }
                  
                  players = players + "\n";
               }
               
               if (!parms.player11.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player11 + (parms.activity_id == 0 ? "  " + parms.pcw11 : "");
               }
               if (!parms.player12.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player12 + (parms.activity_id == 0 ? "  " + parms.pcw12 : "");
               }
               if (!parms.player13.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player13 + (parms.activity_id == 0 ? "  " + parms.pcw13 : "");
               }
               if (!parms.player14.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player14 + (parms.activity_id == 0 ? "  " + parms.pcw14 : "");
               }
               if (!parms.player15.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player15 + (parms.activity_id == 0 ? "  " + parms.pcw15 : "");
               }
               
               
               if (!etime4.equals("") && !parms.type.equals("lassign")) {
                  
                  players = players + "\n\n" + etime4;
               }
               
               if (!etime4.equals("") && parms.type.equals("lassign")) {     // if lottery assign - break into groups 
                  
                  players = players + "\n\nGroup 4: At " + parms.wuser4;
                  
                  if (!f_b4.equals ( "" )) {
                  
                      players = players + " starting on the " + f_b4 + " tee";

                      if (!parms.oldplayer4.equals("")) {
                          
                          players = players + " of Course: " + parms.oldplayer4;
                      }

                  } else {
                      
                      if (!parms.oldplayer4.equals( "" )) {                      // course name saved in oldplayer by Proshop_dsheet

                          players = players + " on Course: " + parms.oldplayer4;
                      }
                  }
                  
                  players = players + "\n";
               }
               
               if (!parms.player16.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player16 + (parms.activity_id == 0 ? "  " + parms.pcw16 : "");
               }
               if (!parms.player17.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player17 + (parms.activity_id == 0 ? "  " + parms.pcw17 : "");
               }
               if (!parms.player18.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player18 + (parms.activity_id == 0 ? "  " + parms.pcw18 : "");
               }
               if (!parms.player19.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player19 + (parms.activity_id == 0 ? "  " + parms.pcw19 : "");
               }
               if (!parms.player20.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player20 + (parms.activity_id == 0 ? "  " + parms.pcw20 : "");
               }
               
               
               if (!etime5.equals("") && !parms.type.equals("lassign")) {
                  
                  players = players + "\n\n" + etime5;
               }
               
               if (!etime5.equals("") && parms.type.equals("lassign")) {     // if lottery assign - break into groups 
                  
                  players = players + "\n\nGroup 5: At " + parms.wuser5;
                  
                  if (!f_b5.equals ( "" )) {
                  
                      players = players + " starting on the " + f_b5 + " tee";
                      
                      if (!parms.oldplayer5.equals("")) {
                          
                          players = players + " of Course: " + parms.oldplayer5;
                      }
                      
                  } else {
                      
                      if (!parms.oldplayer5.equals( "" )) {                      // course name saved in oldplayer by Proshop_dsheet
                          
                          players = players + " on Course: " + parms.oldplayer5;
                      }
                  }
                  
                  players = players + "\n";
               }
               
               if (!parms.player21.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player21 + (parms.activity_id == 0 ? "  " + parms.pcw21 : "");
               }
               if (!parms.player22.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player22 + (parms.activity_id == 0 ? "  " + parms.pcw22 : "");
               }
               if (!parms.player23.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player23 + (parms.activity_id == 0 ? "  " + parms.pcw23 : "");
               }
               if (!parms.player24.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player24 + (parms.activity_id == 0 ? "  " + parms.pcw24 : "");
               }
               if (!parms.player25.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player25 + (parms.activity_id == 0 ? "  " + parms.pcw25 : "");
               }
            }

            enewMsg = enewMsg + players;
            
//            if (parms.type.equals("tee") && club.equals("tcclub")) {
//                
//              if (!parms.notes.equals( "" ) && parms.hideNotes == 0) {
//
//                 enewMsg = enewMsg + "\n\nNotes: " + parms.notes;        // add notes (case# 1406)
//              }
//            }

             if ((parms.type.equals("tee") && club.equals("tcclub")) || (club.equalsIgnoreCase("colletonriverclub") && parms.type.equals("lesson"))) {

                 if (!parms.notes.equals("") && parms.hideNotes == 0) {

                     enewMsg = enewMsg + "\n\nNotes: " + parms.notes;        // add notes (case# 1406)
                 }
             }
        
            
            
            
            
            
            StringBuffer vCalMsg = new StringBuffer();       // use string buffer to build file

            // do not send iCal attachments for season long events or for wait list signups or lotteries
            if (parms.season == 0 && !parms.type.equals( "waitlist" ) && !parms.type.equals( "lottery" )) {

                iCalendar iCal = new iCalendar();

                String tmp_course = "";
                String tmp_end_time = "";  
                String tmp_time = date + "T" + ((time < 1000) ? "0" + time : time) + "00";

                if (!parms.course.equals("")) tmp_course = "Course: " + parms.course + iCal.LINE_SERPARATOR; // "\\n";
                
                String tmp_summary = etime + " Tee Time"; // default to tee time and change accordingly
                
                if (!parms.course.equals("")) tmp_summary += " on " +parms.course;    // add course name if exists
                
                String tmp_description = tmp_course + players.replace("\n", iCal.LINE_SERPARATOR); // "\\n"
                
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
                    
                    tmp_description = parms.name + iCal.LINE_SERPARATOR; // "\n"; // + tmp_course;
                    tmp_description += "You are " + ((wait > 0) ? "on the wait list" : "registered") + " for this event.";
                    
                } else if (parms.type.equals( "lesson" )) {
                    
                    tmp_description = etime + " to " + etime2 + " " + sysLingo.TEXT_Lesson + " with " +proName+ ".";
                    
                    tmp_summary = etime + " " + sysLingo.TEXT_Lesson + " for " + parms.player1;
                    
                    tmp_end_time = date + "T" + ((time2 < 1000) ? "0" + time2 : time2) + "00";
                    
                    if (sysLingo.TEXT_Lesson.equals("Ball Machine")) {
                        tmp_description =  etime + " to " + etime2 + " " + sysLingo.TEXT_Lesson;  // Use a different description for Ball Machine, since pro name doesn't make sense.
                    }
                    
                } else if (parms.type.equals( "lessongrp" )) {
                    
                    tmp_description = etime + " Group Lesson with " +proName+ ".";
                    
                    tmp_summary = parms.act_time + " Lesson Group";

                } else if (parms.type.equals( "activity" )) {

                    tmp_description = players.replace("\n", iCal.LINE_SERPARATOR); // "\\n"

                    tmp_summary = etime + " " + activityDisplayName + " Reservation";

                } else {       // assume tee time - set end time for cal
                    
                    int end_time = time + ((p91 == 0) ? 400 : 200 );      // add 4 hrs or 2 hrs (based on player1)
                    
                    tmp_end_time = date + "T" + ((end_time < 1000) ? "0" + end_time : end_time) + "00";
                }


                iCal.club_name = club;

                iCal.DTSTART = tmp_time;
                iCal.LOCATION = clubName;
                iCal.SUMMARY = tmp_summary;
                iCal.DESCRIPTION = tmp_description;
                iCal.DTSTAMP = DTSTAMP;
                
                if (!tmp_end_time.equals("")) {        // if end time provided
                    
                    iCal.DTEND = tmp_end_time;
                }

                iCal.buildICS(con);

                vCalMsg = new StringBuffer(iCal.ICS_FILE);

                /*
                // TODO: wrap descriptions at 75 bytes
                vCalMsg.append("" +
                    "BEGIN:VCALENDAR\n" +
                    "PRODID:-//ForeTees//NONSGML v1.0//EN\n" +
                    "METHOD:PUBLISH\n" +
                    "BEGIN:VEVENT\n" +
                    "DTSTAMP:" + DTSTAMP + "\n" +
                    "DTSTART:" + tmp_time + "\n" +
                    iCalFoldContentLine("SUMMARY:" + tmp_summary + "\n") +
                    "LOCATION:" + clubName + "\n" + 
                    iCalFoldContentLine("DESCRIPTION:" + tmp_description + "\n") +
                    "URL:http://www1.foretees.com/" + club + "\n" +
                    "END:VEVENT\n" +
                    "END:VCALENDAR");
                 */
            }
            
            
            strbfr = new StringBuffer(enewMsg);       // get email message

            if (club.equals( "oakmont" )) {           // if Oakmont

               if (!oakmontTrlr.equals("")) {
                  
                  strbfr.append(oakmontTrlr);         // appeand Oakmont's custom mesage
               }
            }

            if (parms.type.equals("tee") || parms.type.equals("masstee")) {          // if Tee Time notification
                
                /*
               if (club.equals("brooklawn")) {
                 if (guestsIncluded == true) {           // if any guests in tee time

                    strbfr.append(brookMsg);             // add custom guest message

                 } else {

                    strbfr.append(teetimeMsg);             // add custom guest message
                 }
               } else {     // all other clubs
                  
                 */
                  //
                  //   If custom tee time message exist, appeand it now
                  //
                  if (!teetimeMsg.equals("")) {
                     
                     strbfr.append(teetimeMsg);             // add custom guest message
                  }
               //}
            }

            strbfr.append("\n\n");                    // appeand blank lines

            //enewMsg = strbfr.toString();              // convert to one string                  

            doSending(eaddrTo, eaddrProCopy, replyTo, subject, strbfr.toString(), vCalMsg, parms, con);
            
            

         } else if (emailCan != 0) {        // if Canceled reservation


            //
            //  Create the message content
            //
            String ecanMsg = "";
  
            if (parms.type.startsWith( "lesson" )) {         // if lesson type

                if (parms.type.equals("lessongrp")) {         // if group lesson 
                                 
                    ecanMsg = header + can + "Group Lesson: " + parms.course + " "
                            + "\n\nDate: " + (!parms.day.trim().equals("") ? parms.day + ", " : "") + mm + "/" + dd + "/" + yy
                            + "\n\nTime: " + etime + " "
                            + "\n\n" + sysLingo.TEXT_Lesson_Pro + ": " + proName;

                } else {              // normal lesson
                    ecanMsg = header + can + " "
                            + sysLingo.TEXT_Lesson + " Type: " + parms.course
                            + "\n\nDate: " + parms.day + ", " + mm + "/" + dd + "/" + yy
                            + "\n\nTime: " + etime 
                            + "\n\n" + sysLingo.TEXT_Lesson_Pro + ": "+ proName;
                }

            } else {              // tee time or event

               if (parms.type.equals( "event" )) {

                  if (parms.season == 0) {
                      
                      if (etype == 1 && hideTime == false) {             // if shotgun event, show time of event

                          ecanMsg = header + can + " Event: " + parms.name + "    Date: " + mm + "/" + dd + "/" + yy +
                                        " at " + parms.act_time + " ";

                      } else {

                         ecanMsg = header + can + " Event: " + parms.name + "    Date: " + mm + "/" + dd + "/" + yy + " ";
                      }

                      if (!parms.course.equals( "" )) {

                         ecanMsg = ecanMsg + "on Course: " + parms.course;
                      }
                      
                  } else {
                      
                      // season long event
                      ecanMsg = header + can + " Event: " + parms.name + "    Date: Season Long ";

                      if (!parms.course.equals( "" )) {

                         ecanMsg = ecanMsg + "\nCourse: " + parms.course + " ";
                      }
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

                  if (etype == 1) {           // if tee time during a Shotgun event

                       ecanMsg = ecanMsg + "(Shotgun) ";

                  } else {         

                      if (!f_b.equals ( "" )) {

                           // ecanMsg = ecanMsg + "on the " + f_b + " 9 of ";
                           ecanMsg = ecanMsg + "starting on the " + f_b + " tee of ";

                      } else {

                           ecanMsg = ecanMsg + "on ";
                      }
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

                  ecanMsg = ecanMsg + "\nPlayer 1: " + parms.oldplayer1 + (parms.activity_id == 0 ? "  " + parms.oldpcw1 : "");
               }
               if (!parms.oldplayer2.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 2: " + parms.oldplayer2 + (parms.activity_id == 0 ? "  " + parms.oldpcw2 : "");
               }
               if (!parms.oldplayer3.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 3: " + parms.oldplayer3 + (parms.activity_id == 0 ? "  " + parms.oldpcw3 : "");
               }
               if (!parms.oldplayer4.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 4: " + parms.oldplayer4 + (parms.activity_id == 0 ? "  " + parms.oldpcw4 : "");
               }
               if (!parms.oldplayer5.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 5: " + parms.oldplayer5 + (parms.activity_id == 0 ? "  " + parms.oldpcw5 : "");
               }
               if (!parms.oldplayer6.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 6: " + parms.oldplayer6 + (parms.activity_id == 0 ? "  " + parms.oldpcw6 : "");
               }
               if (!parms.oldplayer7.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 7: " + parms.oldplayer7 + (parms.activity_id == 0 ? "  " + parms.oldpcw7 : "");
               }
               if (!parms.oldplayer8.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 8: " + parms.oldplayer8 + (parms.activity_id == 0 ? "  " + parms.oldpcw8 : "");
               }
               if (!parms.oldplayer9.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 9: " + parms.oldplayer9 + (parms.activity_id == 0 ? "  " + parms.oldpcw9 : "");
               }
               if (!parms.oldplayer10.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 10: " + parms.oldplayer10 + (parms.activity_id == 0 ? "  " + parms.oldpcw10 : "");
               }
               if (!parms.oldplayer11.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 11: " + parms.oldplayer11 + (parms.activity_id == 0 ? "  " + parms.oldpcw11 : "");
               }
               if (!parms.oldplayer12.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 12: " + parms.oldplayer12 + (parms.activity_id == 0 ? "  " + parms.oldpcw12 : "");
               }
               if (!parms.oldplayer13.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 13: " + parms.oldplayer13 + (parms.activity_id == 0 ? "  " + parms.oldpcw13 : "");
               }
               if (!parms.oldplayer14.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 14: " + parms.oldplayer14 + (parms.activity_id == 0 ? "  " + parms.oldpcw14 : "");
               }
               if (!parms.oldplayer15.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 15: " + parms.oldplayer15 + (parms.activity_id == 0 ? "  " + parms.oldpcw15 : "");
               }
               if (!parms.oldplayer16.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 16: " + parms.oldplayer16 + (parms.activity_id == 0 ? "  " + parms.oldpcw16 : "");
               }
               if (!parms.oldplayer17.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 17: " + parms.oldplayer17 + (parms.activity_id == 0 ? "  " + parms.oldpcw17 : "");
               }
               if (!parms.oldplayer18.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 18: " + parms.oldplayer18 + (parms.activity_id == 0 ? "  " + parms.oldpcw18 : "");
               }
               if (!parms.oldplayer19.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 19: " + parms.oldplayer19 + (parms.activity_id == 0 ? "  " + parms.oldpcw19 : "");
               }
               if (!parms.oldplayer20.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 20: " + parms.oldplayer20 + (parms.activity_id == 0 ? "  " + parms.oldpcw20 : "");
               }
               if (!parms.oldplayer21.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 21: " + parms.oldplayer21 + (parms.activity_id == 0 ? "  " + parms.oldpcw21 : "");
               }
               if (!parms.oldplayer22.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 22: " + parms.oldplayer22 + (parms.activity_id == 0 ? "  " + parms.oldpcw22 : "");
               }
               if (!parms.oldplayer23.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 23: " + parms.oldplayer23 + (parms.activity_id == 0 ? "  " + parms.oldpcw23 : "");
               }
               if (!parms.oldplayer24.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 24: " + parms.oldplayer24 + (parms.activity_id == 0 ? "  " + parms.oldpcw24 : "");
               }
               if (!parms.oldplayer25.equals( "" )) {

                  ecanMsg = ecanMsg + "\nPlayer 25: " + parms.oldplayer25 + (parms.activity_id == 0 ? "  " + parms.oldpcw25 : "");
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

               if (parms.season == 0) {
                
                   emodMsg = header + mod + " Event: " + parms.name + "    Date: " + mm + "/" + dd + "/" + yy + " ";

                   if (!parms.course.equals( "" )) {

                      emodMsg = emodMsg + "on Course: " + parms.course + " ";
                   }

                   if (etype == 1) {                                  // if Shotgun event

                      if (hideTime == false) {                 // if ok to include the time

                         emodMsg = emodMsg + "at " + parms.act_time + ".";        // add the event time
                      }

                   } else {          // Tee Time Event

                       emodMsg = emodMsg + "\n\nEvent starts at " + parms.act_time + "." + (parms.activity_id == 0 ? "  YOUR ACTUAL START TIME MAY VARY." : "");
                   }
                   
               } else {
                      
                   // season long event
                   emodMsg = header + mod + " Event: " + parms.name + "    Date: Season Long ";

                   if (!parms.course.equals("")) {

                       emodMsg = emodMsg + "\nCourse: " + parms.course + " ";
                   }
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
               
               
               if (etype == 1) {           // if tee time during a Shotgun event

                  emodMsg = emodMsg + "(Shotgun) ";
                  
               } else {         

                   if (!f_b.equals ( "" )) {

                        // emodMsg = emodMsg + "on the " + f_b + " 9 of ";
                        emodMsg = emodMsg + "starting on the " + f_b + " tee of ";

                   } else {

                        emodMsg = emodMsg + "on ";
                   }
               }

               if (!parms.from_course.equals( "" )) {

                  emodMsg = emodMsg + "Course: " + parms.to_course;
                 
               } else {

                  if (!parms.course.equals( "" )) {

                     emodMsg = emodMsg + "Course: " + parms.course;
                  }
               }
            }
            
            //emodMsg = emodMsg + "\n\nNote: a '9' appended to the end of your mode of tranpsoration option indicates that you are scheduled to play 9 holes.";

            if (parms.type.equals( "frost" )) {
               emodMsg = emodMsg + "\n\nGroup:\n";
            } else {
               emodMsg = emodMsg + "\n\nNew Group:\n";
            }

            if (!parms.player1.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 1: " + parms.player1 + (parms.activity_id == 0 ? "  " + parms.pcw1 : "");
            }
            if (!parms.player2.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 2: " + parms.player2 + (parms.activity_id == 0 ? "  " + parms.pcw2 : "");
            }
            if (!parms.player3.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 3: " + parms.player3 + (parms.activity_id == 0 ? "  " + parms.pcw3 : "");
            }
            if (!parms.player4.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 4: " + parms.player4 + (parms.activity_id == 0 ? "  " + parms.pcw4 : "");
            }
            if (!parms.player5.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 5: " + parms.player5 + (parms.activity_id == 0 ? "  " + parms.pcw5 : "");
            }
            if (!parms.player6.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 6: " + parms.player6 + (parms.activity_id == 0 ? "  " + parms.pcw6 : "");
            }
            if (!parms.player7.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 7: " + parms.player7 + (parms.activity_id == 0 ? "  " + parms.pcw7 : "");
            }
            if (!parms.player8.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 8: " + parms.player8 + (parms.activity_id == 0 ? "  " + parms.pcw8 : "");
            }
            if (!parms.player9.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 9: " + parms.player9 + (parms.activity_id == 0 ? "  " + parms.pcw9 : "");
            }
            if (!parms.player10.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 10: " + parms.player10 + (parms.activity_id == 0 ? "  " + parms.pcw10 : "");
            }
            if (!parms.player11.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 11: " + parms.player11 + (parms.activity_id == 0 ? "  " + parms.pcw11 : "");
            }
            if (!parms.player12.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 12: " + parms.player12 + (parms.activity_id == 0 ? "  " + parms.pcw12 : "");
            }
            if (!parms.player13.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 13: " + parms.player13 + (parms.activity_id == 0 ? "  " + parms.pcw13 : "");
            }
            if (!parms.player14.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 14: " + parms.player14 + (parms.activity_id == 0 ? "  " + parms.pcw14 : "");
            }
            if (!parms.player15.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 15: " + parms.player15 + (parms.activity_id == 0 ? "  " + parms.pcw15 : "");
            }
            if (!parms.player16.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 16: " + parms.player16 + (parms.activity_id == 0 ? "  " + parms.pcw16 : "");
            }
            if (!parms.player17.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 17: " + parms.player17 + (parms.activity_id == 0 ? "  " + parms.pcw17 : "");
            }
            if (!parms.player18.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 18: " + parms.player18 + (parms.activity_id == 0 ? "  " + parms.pcw18 : "");
            }
            if (!parms.player19.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 19: " + parms.player19 + (parms.activity_id == 0 ? "  " + parms.pcw19 : "");
            }
            if (!parms.player20.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 20: " + parms.player20 + (parms.activity_id == 0 ? "  " + parms.pcw20 : "");
            }
            if (!parms.player21.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 21: " + parms.player21 + (parms.activity_id == 0 ? "  " + parms.pcw21 : "");
            }
            if (!parms.player22.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 22: " + parms.player22 + (parms.activity_id == 0 ? "  " + parms.pcw22 : "");
            }
            if (!parms.player23.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 23: " + parms.player23 + (parms.activity_id == 0 ? "  " + parms.pcw23 : "");
            }
            if (!parms.player24.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 24: " + parms.player24 + (parms.activity_id == 0 ? "  " + parms.pcw24 : "");
            }
            if (!parms.player25.equals( "" )) {

               emodMsg = emodMsg + "\nPlayer 25: " + parms.player25 + (parms.activity_id == 0 ? "  " + parms.pcw25 : "");
            }

            if (!parms.type.equals( "frost" )) {

               emodMsg = emodMsg + "\n\nPrevious Group (before change):\n";

               if (!parms.oldplayer1.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 1: " + parms.oldplayer1 + (parms.activity_id == 0 ? "  " + parms.oldpcw1 : "");
               }
               if (!parms.oldplayer2.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 2: " + parms.oldplayer2 + (parms.activity_id == 0 ? "  " + parms.oldpcw2 : "");
               }
               if (!parms.oldplayer3.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 3: " + parms.oldplayer3 + (parms.activity_id == 0 ? "  " + parms.oldpcw3 : "");
               }
               if (!parms.oldplayer4.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 4: " + parms.oldplayer4 + (parms.activity_id == 0 ? "  " + parms.oldpcw4 : "");
               }
               if (!parms.oldplayer5.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 5: " + parms.oldplayer5 + (parms.activity_id == 0 ? "  " + parms.oldpcw5 : "");
               }
               if (!parms.oldplayer6.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 6: " + parms.oldplayer6 + (parms.activity_id == 0 ? "  " + parms.oldpcw6 : "");
               }
               if (!parms.oldplayer7.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 7: " + parms.oldplayer7 + (parms.activity_id == 0 ? "  " + parms.oldpcw7 : "");
               }
               if (!parms.oldplayer8.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 8: " + parms.oldplayer8 + (parms.activity_id == 0 ? "  " + parms.oldpcw8 : "");
               }
               if (!parms.oldplayer9.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 9: " + parms.oldplayer9 + (parms.activity_id == 0 ? "  " + parms.oldpcw9 : "");
               }
               if (!parms.oldplayer10.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 10: " + parms.oldplayer10 + (parms.activity_id == 0 ? "  " + parms.oldpcw10 : "");
               }
               if (!parms.oldplayer11.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 11: " + parms.oldplayer11 + (parms.activity_id == 0 ? "  " + parms.oldpcw11 : "");
               }
               if (!parms.oldplayer12.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 12: " + parms.oldplayer12 + (parms.activity_id == 0 ? "  " + parms.oldpcw12 : "");
               }
               if (!parms.oldplayer13.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 13: " + parms.oldplayer13 + (parms.activity_id == 0 ? "  " + parms.oldpcw13 : "");
               }
               if (!parms.oldplayer14.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 14: " + parms.oldplayer14 + (parms.activity_id == 0 ? "  " + parms.oldpcw14 : "");
               }
               if (!parms.oldplayer15.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 15: " + parms.oldplayer15 + (parms.activity_id == 0 ? "  " + parms.oldpcw15 : "");
               }
               if (!parms.oldplayer16.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 16: " + parms.oldplayer16 + (parms.activity_id == 0 ? "  " + parms.oldpcw16 : "");
               }
               if (!parms.oldplayer17.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 17: " + parms.oldplayer17 + (parms.activity_id == 0 ? "  " + parms.oldpcw17 : "");
               }
               if (!parms.oldplayer18.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 18: " + parms.oldplayer18 + (parms.activity_id == 0 ? "  " + parms.oldpcw18 : "");
               }
               if (!parms.oldplayer19.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 19: " + parms.oldplayer19 + (parms.activity_id == 0 ? "  " + parms.oldpcw19 : "");
               }
               if (!parms.oldplayer20.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 20: " + parms.oldplayer20 + (parms.activity_id == 0 ? "  " + parms.oldpcw20 : "");
               }
               if (!parms.oldplayer21.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 21: " + parms.oldplayer21 + (parms.activity_id == 0 ? "  " + parms.oldpcw21 : "");
               }
               if (!parms.oldplayer22.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 22: " + parms.oldplayer22 + (parms.activity_id == 0 ? "  " + parms.oldpcw22 : "");
               }
               if (!parms.oldplayer23.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 23: " + parms.oldplayer23 + (parms.activity_id == 0 ? "  " + parms.oldpcw23 : "");
               }
               if (!parms.oldplayer24.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 24: " + parms.oldplayer24 + (parms.activity_id == 0 ? "  " + parms.oldpcw24 : "");
               }
               if (!parms.oldplayer25.equals( "" )) {

                  emodMsg = emodMsg + "\nPlayer 25: " + parms.oldplayer25 + (parms.activity_id == 0 ? "  " + parms.oldpcw25 : "");
               }
            }

            if ((parms.type.equals("tee") && club.equals("tcclub")) || (club.equalsIgnoreCase("colletonriverclub") && parms.type.equalsIgnoreCase("lesson"))) {
                
              if (!parms.notes.equals( "" ) && parms.hideNotes == 0) {

                 emodMsg = emodMsg + "\n\nNotes: " + parms.notes;        // add notes (case# 1406)
              }
            }
            
            strbfr = new StringBuffer(emodMsg);        // get email message

            
            if (parms.type.equals("tee") || parms.type.equals("masstee")) {        // if Tee Time notification
                
               //
               //  check for Custom messages and add to notification
               //
               /*
               if (club.equals("brooklawn")) {
                
                  if (guestsIncluded == true) {     // if any guests in tee time

                     strbfr.append(brookMsg);             // add custom guest message
                     
                  } else {
                     
                     strbfr.append(teetimeMsg);             // add custom tee time message
                  }
               } else {    
                  */                               // all other clubs - check for msg
                  
                  if (!teetimeMsg.equals("")) {           // if Custom Tee Time msg exists
                     
                     strbfr.append(teetimeMsg);             // add custom tee time message
                  }
               //}
            }  
            
            
            
            //
            //   Setup iCal attachment for any new players added to the tee time or event signup
            //
            StringBuffer vCalMsg = new StringBuffer();       // use string buffer to build file

            // only send iCal attachments to newly added members and for tee times and events
            if (parms.type.equals("tee") || (parms.season == 0 && parms.type.equals( "event" ))) {
                
                iCalendar iCal = new iCalendar();
                
                String tmp_course = "";
                String tmp_time = date + "T" + ((time < 1000) ? "0" + time : time) + "00";

                if (!parms.course.equals("")) tmp_course = "Course: " + parms.course + iCal.LINE_SERPARATOR; // "\\n";
            
                String tmp_summary = etime + " Tee Time"; // default to tee time and change accordingly
                String tmp_description = tmp_course + players.replace("\n", iCal.LINE_SERPARATOR); // "\\n"
                
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
                    
                    tmp_description = parms.name + iCal.LINE_SERPARATOR; // "\n"; // + tmp_course;
                    tmp_description += "You are " + ((wait > 0) ? "on the wait list" : "registered") + " for this event.";
                }
                
                
                
                iCal.club_name = club;
                
                iCal.DTSTART = tmp_time;
                iCal.LOCATION = clubName;
                iCal.SUMMARY = tmp_summary;
                iCal.DESCRIPTION = tmp_description;
                iCal.DTSTAMP = DTSTAMP;

                iCal.buildICS(con);
                
                vCalMsg = new StringBuffer(iCal.ICS_FILE);

                /*
                // TODO: wrap descriptions at 75 bytes
                vCalMsg.append("" +
                    "BEGIN:VCALENDAR\n" +
                    "PRODID:-//ForeTees//NONSGML v1.0//EN\n" +
                    "METHOD:PUBLISH\n" +
                    "BEGIN:VEVENT\n" +
                    "DTSTAMP:" + DTSTAMP + "\n" +
                    "DTSTART:" + tmp_time + "\n" +
                    iCalFoldContentLine("SUMMARY:" + tmp_summary + "\n") +
                    "LOCATION:" + clubName + "\n" + 
                    iCalFoldContentLine("DESCRIPTION:" + tmp_description + "\n") +
                    "URL:http://www1.foretees.com/" + club + "\n" +
                    "END:VEVENT\n" +
                    "END:VCALENDAR");
                */
            }
            
            
            
            
            //StringBuffer vCalMsg = new StringBuffer();  // we'll add vCal here once we can reliably replace existing entires

            doSending(eaddrTo, eaddrProCopy, replyTo, subject, strbfr.toString(), vCalMsg, parms, con, "modify");

            //
            // END IF MODIFY

            
         } else if (emailRem != 0) {        // if sending an automated reminder email
             
            //  Create the message content
            String eremMsg = "";

            if (parms.type.startsWith( "lesson" )) {         // if lesson type

                String act_name = "";

                if (!parms.actual_activity_name.equals("")) {
                    act_name = "\n\nLocation: " + parms.actual_activity_name + "  ";
                }

                if (parms.type.equals("lessongrp")) {         // if group lesson 
                    
                    eremMsg = header + erem + "Group Lesson: " + parms.course + " "
                            + "\n\nDate: " + (!parms.day.trim().equals("") ? parms.day + ", " : "") + mm + "/" + dd + "/" + yy
                            + "\n\nTime: " + etime + " "
                            + "\n\n" + sysLingo.TEXT_Lesson_Pro + ": " + proName;

                } else {              // normal lesson

                    eremMsg = header + erem + sysLingo.TEXT_Lesson + " Type: " + parms.course + "  "
                            + act_name + "  "
                            + "\n\nDate: " + parms.day + ", " + mm + "/" + dd + "/" + yy
                            + "\n\nTime: " + etime + " "
                            + "\n\n" + sysLingo.TEXT_Lesson_Pro + ": " + proName;
                }
                 
            } else {              // tee time or event

               if (parms.type.equals( "event" )) {
                  
                  if (parms.season == 0) {
                      
                      // NOT a season long event
                      eremMsg = header + erem + " Event: " + parms.name + "    Date: " + mm + "/" + dd + "/" + yy + " ";

                      if (!parms.course.equals( "" )) {

                         eremMsg = eremMsg + "on Course: " + parms.course + " ";
                      }

                      if (etype == 1) {                                  // if Shotgun event

                         if (hideTime == false) {        // if ok to include the time of the event

                            eremMsg = eremMsg + "at " + parms.act_time + ".";        // add the event time
                         }
                         
                      } else {          // Tee Time Event

                         eremMsg = eremMsg + "\n\nEvent starts at " + parms.act_time + "." + (parms.activity_id == 0 ? "  YOUR ACTUAL START TIME MAY VARY." : "");
                      }
                  } else {
                      
                      // season long event
                      eremMsg = header + erem + " Event: " + parms.name + "    Date: Season Long ";

                      if (!parms.course.equals( "" )) {

                         eremMsg = eremMsg + "\nCourse: " + parms.course + " ";
                      }
                  }
                  if (wait > 0) {            // if on wait list

                     eremMsg = eremMsg + "\n\nNote:  This team is currently on the WAIT LIST.";
                  }

               } else if (parms.type.equals("activity")) {         // activity reservation

                   eremMsg = header + erem + parms.day + " " + mm + "/" + dd + "/" + yy + " at " + etime + " for " + parms.actual_activity_name + " ";

               } else {
                   
                    eremMsg = header + erem + parms.day + " " + mm + "/" + dd + "/" + yy + (parms.type.equals("masstee") ? " starting" : "") + " at " + etime;

                    if (!f_b.equals ( "" )) {

                        // eremMsg = eremMsg + "on the " + f_b + " 9";
                        eremMsg = eremMsg + (!parms.type.equals("masstee") ? " starting" : "") + " on the " + f_b + " tee";

                        if (!parms.course.equals( "" )) {

                            eremMsg = eremMsg + " of Course: " + parms.course;
                        }

                    } else {

                        if (!parms.course.equals( "" )) {

                            eremMsg = eremMsg + " on Course: " + parms.course;
                        }
                    }
               }

               eremMsg = eremMsg + "\n";
               //eremMsg = eremMsg + "\n\nNote: a '9' appended to the end of your mode of tranpsoration option indicates that you are scheduled to play 9 holes.\n";
               
               //
               //  build message text
               //
               if (!parms.player1.equals( "" )) {
                   
                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player1 + (parms.activity_id == 0 ? "  " + parms.pcw1 : "");
               }
               if (!parms.player2.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player2 + (parms.activity_id == 0 ? "  " + parms.pcw2 : "");
               }
               if (!parms.player3.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player3 + (parms.activity_id == 0 ? "  " + parms.pcw3 : "");
               }
               if (!parms.player4.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player4 + (parms.activity_id == 0 ? "  " + parms.pcw4 : "");
               }
               if (!parms.player5.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player5 + (parms.activity_id == 0 ? "  " + parms.pcw5 : "");
               }

               
               if (!etime2.equals("") && !parms.type.equals("waitlist") && !parms.type.equals("lassign")) {   // (etime2 is also used for waitlist)
                  
                  players = players + "\n\n" + etime2;
               }
               
               if (!etime2.equals("") && parms.type.equals("lassign")) {     // if lottery assign - break into groups 
                  
                  players = players + "\n\nGroup 2: At " + parms.wuser2;
                  
                  if (!f_b2.equals ( "" )) {
                  
                      players = players + " starting on the " + f_b2 + " tee";

                       if (!parms.oldplayer2.equals("")) {

                           players = players + " of Course: " + parms.oldplayer2;
                       }

                  } else {

                       if (!parms.oldplayer2.equals("")) {                      // course name saved in oldplayer by Proshop_dsheet

                           players = players + " on Course: " + parms.oldplayer2;
                       }
                  }
                  
                  players = players + "\n";
               }
               
               if (!parms.player6.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player6 + (parms.activity_id == 0 ? "  " + parms.pcw6 : "");
               }
               if (!parms.player7.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player7 + (parms.activity_id == 0 ? "  " + parms.pcw7 : "");
               }
               if (!parms.player8.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player8 + (parms.activity_id == 0 ? "  " + parms.pcw8 : "");
               }
               if (!parms.player9.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player9 + (parms.activity_id == 0 ? "  " + parms.pcw9 : "");
               }
               if (!parms.player10.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player10 + (parms.activity_id == 0 ? "  " + parms.pcw10 : "");
               }
               
               
               if (!etime3.equals("") && !parms.type.equals("lassign")) {
                  
                  players = players + "\n\n" + etime3;
               }
               
               if (!etime3.equals("") && parms.type.equals("lassign")) {     // if lottery assign - break into groups 
                  
                  players = players + "\n\nGroup 3: At " + parms.wuser3;
                  
                  if (!f_b3.equals ( "" )) {
                  
                      players = players + " starting on the " + f_b3 + " tee";
                      
                      if (!parms.oldplayer3.equals("")) {
                          
                          players = players + " of Course: " + parms.oldplayer3;
                      }

                  } else {
                      
                      if (!parms.oldplayer3.equals( "" )) {                      // course name saved in oldplayer by Proshop_dsheet

                         players = players + " on Course: " + parms.oldplayer3;
                      }
                  }
                  
                  players = players + "\n";
               }
               
               if (!parms.player11.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player11 + (parms.activity_id == 0 ? "  " + parms.pcw11 : "");
               }
               if (!parms.player12.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player12 + (parms.activity_id == 0 ? "  " + parms.pcw12 : "");
               }
               if (!parms.player13.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player13 + (parms.activity_id == 0 ? "  " + parms.pcw13 : "");
               }
               if (!parms.player14.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player14 + (parms.activity_id == 0 ? "  " + parms.pcw14 : "");
               }
               if (!parms.player15.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player15 + (parms.activity_id == 0 ? "  " + parms.pcw15 : "");
               }
               
               
               if (!etime4.equals("") && !parms.type.equals("lassign")) {
                  
                  players = players + "\n\n" + etime4;
               }
               
               if (!etime4.equals("") && parms.type.equals("lassign")) {     // if lottery assign - break into groups 
                  
                  players = players + "\n\nGroup 4: At " + parms.wuser4;
                  
                  if (!f_b4.equals ( "" )) {
                  
                      players = players + " starting on the " + f_b4 + " tee";

                      if (!parms.oldplayer4.equals("")) {
                          
                          players = players + " of Course: " + parms.oldplayer4;
                      }

                  } else {
                      
                      if (!parms.oldplayer4.equals( "" )) {                      // course name saved in oldplayer by Proshop_dsheet

                          players = players + " on Course: " + parms.oldplayer4;
                      }
                  }
                  
                  players = players + "\n";
               }
               
               if (!parms.player16.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player16 + (parms.activity_id == 0 ? "  " + parms.pcw16 : "");
               }
               if (!parms.player17.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player17 + (parms.activity_id == 0 ? "  " + parms.pcw17 : "");
               }
               if (!parms.player18.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player18 + (parms.activity_id == 0 ? "  " + parms.pcw18 : "");
               }
               if (!parms.player19.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player19 + (parms.activity_id == 0 ? "  " + parms.pcw19 : "");
               }
               if (!parms.player20.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player20 + (parms.activity_id == 0 ? "  " + parms.pcw20 : "");
               }
               
               
               if (!etime5.equals("") && !parms.type.equals("lassign")) {
                  
                  players = players + "\n\n" + etime5;
               }
               
               if (!etime5.equals("") && parms.type.equals("lassign")) {     // if lottery assign - break into groups 
                  
                  players = players + "\n\nGroup 5: At " + parms.wuser5;
                  
                  if (!f_b5.equals ( "" )) {
                  
                      players = players + " starting on the " + f_b5 + " tee";
                      
                      if (!parms.oldplayer5.equals("")) {
                          
                          players = players + " of Course: " + parms.oldplayer5;
                      }
                      
                  } else {
                      
                      if (!parms.oldplayer5.equals( "" )) {                      // course name saved in oldplayer by Proshop_dsheet
                          
                          players = players + " on Course: " + parms.oldplayer5;
                      }
                  }
                  
                  players = players + "\n";
               }
               
               if (!parms.player21.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player21 + (parms.activity_id == 0 ? "  " + parms.pcw21 : "");
               }
               if (!parms.player22.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player22 + (parms.activity_id == 0 ? "  " + parms.pcw22 : "");
               }
               if (!parms.player23.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player23 + (parms.activity_id == 0 ? "  " + parms.pcw23 : "");
               }
               if (!parms.player24.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player24 + (parms.activity_id == 0 ? "  " + parms.pcw24 : "");
               }
               if (!parms.player25.equals( "" )) {

                  playerCount++;
                  players = players + "\nPlayer " + playerCount + ": " + parms.player25 + (parms.activity_id == 0 ? "  " + parms.pcw25 : "");
               }
            }

            eremMsg = eremMsg + players;
            
             if ((parms.type.equals("tee") && club.equals("tcclub")) || (club.equalsIgnoreCase("colletonriverclub") && parms.type.equals("lesson"))) {

                 if (!parms.notes.equals("") && parms.hideNotes == 0) {

                     eremMsg = eremMsg + "\n\nNotes: " + parms.notes;        // add notes (case# 1406)
                 }
             }
        
             
            StringBuffer vCalMsg = new StringBuffer();       // use string buffer to build file

            // do not send iCal attachments for season long events or for wait list signups or lotteries
            if (parms.season == 0 && !parms.type.equals( "waitlist" ) && !parms.type.equals( "lottery" )) {

                iCalendar iCal = new iCalendar();

                String tmp_course = "";
                String tmp_end_time = "";  
                String tmp_time = date + "T" + ((time < 1000) ? "0" + time : time) + "00";

                if (!parms.course.equals("")) tmp_course = "Course: " + parms.course + iCal.LINE_SERPARATOR; // "\\n";
                
                String tmp_summary = etime + " Tee Time"; // default to tee time and change accordingly
                
                if (!parms.course.equals("")) tmp_summary += " on " +parms.course;    // add course name if exists
                
                String tmp_description = tmp_course + players.replace("\n", iCal.LINE_SERPARATOR); // "\\n"
                
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
                    
                    tmp_description = parms.name + iCal.LINE_SERPARATOR; // "\n"; // + tmp_course;
                    tmp_description += "You are " + ((wait > 0) ? "on the wait list" : "registered") + " for this event.";
                    
                } else if (parms.type.equals( "lesson" )) {
                    
                    tmp_description = etime + " " + sysLingo.TEXT_Lesson + " with " +proName+ ".";
                    
                    tmp_summary = parms.act_time + " " + sysLingo.TEXT_Lesson;
                    
                    if (sysLingo.TEXT_Lesson.equals("Ball Machine")) {
                        tmp_description =  etime + " " + sysLingo.TEXT_Lesson;  // Use a different description for Ball Machine, since pro name doesn't make sense.
                    }
                    
                } else if (parms.type.equals( "lessongrp" )) {
                    
                    tmp_description = etime + " Group Lesson with " +proName+ ".";
                    
                    tmp_summary = parms.act_time + " Lesson Group";

                } else if (parms.type.equals( "activity" )) {

                    tmp_description = players.replace("\n", iCal.LINE_SERPARATOR); // "\\n"

                    tmp_summary = etime + " " + activityDisplayName + " Reservation";

                } else {       // assume tee time - set end time for cal
                    
                    int end_time = time + ((p91 == 0) ? 400 : 200 );      // add 4 hrs or 2 hrs (based on player1)
                    
                    tmp_end_time = date + "T" + ((end_time < 1000) ? "0" + end_time : end_time) + "00";
                }


                iCal.club_name = club;

                iCal.DTSTART = tmp_time;
                iCal.LOCATION = clubName;
                iCal.SUMMARY = tmp_summary;
                iCal.DESCRIPTION = tmp_description;
                iCal.DTSTAMP = DTSTAMP;
                
                if (!tmp_end_time.equals("")) {        // if end time provided
                    
                    iCal.DTEND = tmp_end_time;
                }

                iCal.buildICS(con);

                vCalMsg = new StringBuffer(iCal.ICS_FILE);

                /*
                // TODO: wrap descriptions at 75 bytes
                vCalMsg.append("" +
                    "BEGIN:VCALENDAR\n" +
                    "PRODID:-//ForeTees//NONSGML v1.0//EN\n" +
                    "METHOD:PUBLISH\n" +
                    "BEGIN:VEVENT\n" +
                    "DTSTAMP:" + DTSTAMP + "\n" +
                    "DTSTART:" + tmp_time + "\n" +
                    iCalFoldContentLine("SUMMARY:" + tmp_summary + "\n") +
                    "LOCATION:" + clubName + "\n" + 
                    iCalFoldContentLine("DESCRIPTION:" + tmp_description + "\n") +
                    "URL:http://www1.foretees.com/" + club + "\n" +
                    "END:VEVENT\n" +
                    "END:VCALENDAR");
                 */
            }
            
            
            strbfr = new StringBuffer(eremMsg);       // get email message

            if (club.equals( "oakmont" )) {           // if Oakmont

               if (!oakmontTrlr.equals("")) {
                  
                  strbfr.append(oakmontTrlr);         // appeand Oakmont's custom mesage
               }
            }

            if (parms.type.equals("tee") || parms.type.equals("masstee")) {          // if Tee Time notification
                
                /*
               if (club.equals("brooklawn")) {
                 if (guestsIncluded == true) {           // if any guests in tee time

                    strbfr.append(brookMsg);             // add custom guest message

                 } else {

                    strbfr.append(teetimeMsg);             // add custom guest message
                 }
               } else {     // all other clubs
                  
                 */
                  //
                  //   If custom tee time message exist, appeand it now
                  //
                  if (!teetimeMsg.equals("")) {
                     
                     strbfr.append(teetimeMsg);             // add custom guest message
                  }
               //}
            }

            strbfr.append("\n\n");                    // appeand blank lines

            //eremMsg = strbfr.toString();              // convert to one string   
            
            doSending(eaddrTo, eaddrProCopy, replyTo, subject, strbfr.toString(), vCalMsg, parms, con);
            
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
               

            /*
            f_b = "Front";
            if (from_fb == 1) {
              
               f_b = "Back";
            }
            */
            f_b = "1st";
            if (from_fb == 1) {
              
               f_b = "10th";
            }

            if (skipFrontBack == true) {     

               moveMsg = moveMsg + "\n\nOriginal time: " + etime + " ";

               if (!parms.from_course.equals( "" )) {

                  moveMsg = moveMsg + "on Course: " + parms.from_course;
               }

            } else {
              
               //moveMsg = moveMsg + "\n\nOriginal time: " + etime + " " +
                 //               "on the " + f_b + " 9 ";
               moveMsg = moveMsg + "\n\nOriginal time: " + etime + " " +
                                "starting on the " + f_b + " tee ";

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
               

            /*
            f_b = "Front";
            if (to_fb == 1) {

               f_b = "Back";
            }
            */
            f_b = "1st";
            if (to_fb == 1) {

               f_b = "10th";
            }

            if (skipFrontBack == true) {     

               moveMsg = moveMsg + "\n\nNew time: " + etime + " ";

               if (!parms.to_course.equals( "" )) {

                  moveMsg = moveMsg + "on Course: " + parms.to_course;
               }

            } else {

              // moveMsg = moveMsg + "\n\nNew time: " + etime + " " +
              //                  "on the " + f_b + " 9 ";
               moveMsg = moveMsg + "\n\nNew time: " + etime + " " +
                                "starting on the " + f_b + " tee ";

               if (!parms.to_course.equals( "" )) {

                  moveMsg = moveMsg + " of Course: " + parms.to_course;
               }
            }

            moveMsg = moveMsg + "\n\n";
            //moveMsg = moveMsg + "\n\nNote: a '9' appended to the end of your mode of tranpsoration option indicates that you are scheduled to play 9 holes.\n";

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
         
      } // end of if send != 0 (if anyone to send it to)
         
     //
     //  Now check if any team from the wait list was bumped up to the normal event sign-up list
     //
     if (parms.type.equals( "event" ) && checkWait != 0) {

        try {

           if (!parms.wuser1.equals( "" ) || !parms.wuser2.equals( "" ) || !parms.wuser3.equals( "" ) || !parms.wuser4.equals( "" ) || !parms.wuser5.equals( "" )) {

              subject = "ForeTees Event Registration Notification";

              if (!clubName.equals( "" ) && !club.equals("fortcollins")) subject = subject + " - " + clubName;

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
                 pstmte1.setString(4, parms.wuser1);
                 rs = pstmte1.executeQuery();      // execute the prepared stmt

                 if (rs.next()) {

                    to = rs.getString("email1");           // user's email address
                    emailOpt = rs.getInt("emailOpt");      // email option
                    emailOpt2 = rs.getInt("emailOpt2");    // email option for 2nd address
                    to2 = rs.getString("email2");          // user's 2nd email address
 
                    if (!to.equals( "" ) && emailOpt != 0) { 

                        eaddrTo.add(new ArrayList<String>());
                        eaddrTo.get(eaddrTo.size() - 1).add(to);
                        eaddrTo.get(eaddrTo.size() - 1).add(parms.wuser1);
                        send = 1;
                    }
                    if (!to2.equals( "" ) && emailOpt2 != 0) {     // if 2nd email address

                        eaddrTo.add(new ArrayList<String>());
                        eaddrTo.get(eaddrTo.size() - 1).add(to2);
                        eaddrTo.get(eaddrTo.size() - 1).add(parms.wuser1);
                        send = 1;
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
                 pstmte2.setString(4, parms.wuser2);
                 rs = pstmte2.executeQuery();      // execute the prepared stmt

                 if (rs.next()) {

                    to = rs.getString("email1");           // user's email address
                    emailOpt = rs.getInt("emailOpt");      // email option
                    emailOpt2 = rs.getInt("emailOpt2");    // email option for 2nd address
                    to2 = rs.getString("email2");          // user's 2nd email address
 
                    if (!to.equals( "" ) && emailOpt != 0) {

                        eaddrTo.add(new ArrayList<String>());
                        eaddrTo.get(eaddrTo.size() - 1).add(to);
                        eaddrTo.get(eaddrTo.size() - 1).add(parms.wuser2);
                        send = 1;
                    }
                    if (!to2.equals( "" ) && emailOpt2 != 0) {     // if 2nd email address

                        eaddrTo.add(new ArrayList<String>());
                        eaddrTo.get(eaddrTo.size() - 1).add(to2);
                        eaddrTo.get(eaddrTo.size() - 1).add(parms.wuser2);
                        send = 1;
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
                 pstmte3.setString(4, parms.wuser3);
                 rs = pstmte3.executeQuery();      // execute the prepared stmt

                 if (rs.next()) {

                    to = rs.getString("email1");           // user's email address
                    emailOpt = rs.getInt("emailOpt");      // email option
                    emailOpt2 = rs.getInt("emailOpt2");    // email option for 2nd address
                    to2 = rs.getString("email2");          // user's 2nd email address
 
                    if (!to.equals( "" ) && emailOpt != 0) {

                        eaddrTo.add(new ArrayList<String>());
                        eaddrTo.get(eaddrTo.size() - 1).add(to);
                        eaddrTo.get(eaddrTo.size() - 1).add(parms.wuser3);
                        send = 1;
                    }
                    if (!to2.equals( "" ) && emailOpt2 != 0) {     // if 2nd email address

                        eaddrTo.add(new ArrayList<String>());
                        eaddrTo.get(eaddrTo.size() - 1).add(to2);
                        eaddrTo.get(eaddrTo.size() - 1).add(parms.wuser3);
                        send = 1;
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
                 pstmte4.setString(4, parms.wuser4);
                 rs = pstmte4.executeQuery();      // execute the prepared stmt

                 if (rs.next()) {

                    to = rs.getString("email1");           // user's email address
                    emailOpt = rs.getInt("emailOpt");      // email option
                    emailOpt2 = rs.getInt("emailOpt2");    // email option for 2nd address
                    to2 = rs.getString("email2");          // user's 2nd email address
 
                    if (!to.equals( "" ) && emailOpt != 0) {

                        eaddrTo.add(new ArrayList<String>());
                        eaddrTo.get(eaddrTo.size() - 1).add(to);
                        eaddrTo.get(eaddrTo.size() - 1).add(parms.wuser4);
                        send = 1;
                    }
                    if (!to2.equals( "" ) && emailOpt2 != 0) {     // if 2nd email address

                        eaddrTo.add(new ArrayList<String>());
                        eaddrTo.get(eaddrTo.size() - 1).add(to2);
                        eaddrTo.get(eaddrTo.size() - 1).add(parms.wuser4);
                        send = 1;
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
                 pstmte5.setString(4, parms.wuser5);
                 rs = pstmte5.executeQuery();      // execute the prepared stmt

                 if (rs.next()) {

                    to = rs.getString("email1");           // user's email address
                    emailOpt = rs.getInt("emailOpt");      // email option
                    emailOpt2 = rs.getInt("emailOpt2");    // email option for 2nd address
                    to2 = rs.getString("email2");          // user's 2nd email address
 
                    if (!to.equals( "" ) && emailOpt != 0) {

                        eaddrTo.add(new ArrayList<String>());
                        eaddrTo.get(eaddrTo.size() - 1).add(to);
                        eaddrTo.get(eaddrTo.size() - 1).add(parms.wuser5);
                        send = 1;
                    }
                    if (!to2.equals( "" ) && emailOpt2 != 0) {     // if 2nd email address

                        eaddrTo.add(new ArrayList<String>());
                        eaddrTo.get(eaddrTo.size() - 1).add(to2);
                        eaddrTo.get(eaddrTo.size() - 1).add(parms.wuser5);
                        send = 1;
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

                 ewaitMsg = ewaitMsg + "\n\n";
                 
                 //
                 //  build message text
                 //
                 if (!parms.wplayer1.equals("")) {
                      ewaitMsg = ewaitMsg + "\nPlayer 1: " + parms.wplayer1 + "  " + parms.wp1cw;
                 }
                 if (!parms.wplayer2.equals("")) {
                      ewaitMsg = ewaitMsg + "\nPlayer 2: " + parms.wplayer2 + "  " + parms.wp2cw;
                 }
                 if (!parms.wplayer3.equals("")) {
                      ewaitMsg = ewaitMsg + "\nPlayer 3: " + parms.wplayer3 + "  " + parms.wp3cw;
                 }
                 if (!parms.wplayer4.equals("")) {
                      ewaitMsg = ewaitMsg + "\nPlayer 4: " + parms.wplayer4 + "  " + parms.wp4cw;
                 }
                 if (!parms.wplayer5.equals("")) {
                      ewaitMsg = ewaitMsg + "\nPlayer 5: " + parms.wplayer5 + "  " + parms.wp5cw;
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
      
   } // end of IF emailOk != 0 (this elseless if block spans most of this method)
   
 } // end of sendIt method
 
 
 //
 // This method will send individual emails to each recipient 
 //
 private static boolean doSending(ArrayList<ArrayList<String>> eaddrTo, ArrayList<String> eaddrProCopy, String replyTo,
                                  String msgSubject, String msgBody, StringBuffer vCalMsg, parmEmail emailParm, Connection con) {

     boolean result = false;         // default to fail
     
     result = doSending(eaddrTo, eaddrProCopy, replyTo, msgSubject, msgBody, vCalMsg, emailParm, con, "none", null, null);   // callType = "none" when not supplied
     
     return result;
 }

 //
 // This method will send individual emails to each recipient
 //
 public static boolean doSending(ArrayList<ArrayList<String>> eaddrTo, ArrayList<String> eaddrProCopy, String replyTo,
                                  String msgSubject, String msgBody, StringBuffer vCalMsg, parmEmail emailParm, Connection con,
                                  String callType) {

     boolean result = false;         // default to fail

     result = doSending(eaddrTo, eaddrProCopy, replyTo, msgSubject, msgBody, vCalMsg, emailParm, con, callType, null, null);   // callType = "none" when not supplied

     return result;
 }

 //
 // This method will send individual emails to each recipient 
 //
 // fields must contain a HashTable that contains each BodyPart ready to be attached to the outgoing email
 public static boolean doSending(ArrayList<ArrayList<String>> eaddrTo, ArrayList<String> eaddrProCopy, String replyTo,
                                  String msgSubject, String msgBody, StringBuffer vCalMsg, parmEmail emailParm, Connection con,
                                  String callType, Dictionary fields) {
     return doSending(eaddrTo, eaddrProCopy, replyTo, msgSubject, msgBody, vCalMsg, emailParm, con, callType, fields, null);
 }

  //
 // This method will send individual emails to each recipient 
 //
 public static boolean doSending(ArrayList<ArrayList<String>> eaddrTo, ArrayList<String> eaddrProCopy, String replyTo,
                                  String msgSubject, String msgBody, StringBuffer vCalMsg, parmEmail emailParm, String club) {
     Connection club_con = Connect.getCon(club);
     boolean result = doSending(eaddrTo, eaddrProCopy, replyTo, msgSubject, msgBody, vCalMsg, emailParm, club_con, "", null, null);
     Connect.close(club_con);
     return result;
 }

 //
 // This method will send individual emails to each recipient 
 //
 public static boolean doSending(ArrayList<ArrayList<String>> eaddrTo, ArrayList<String> eaddrProCopy, String replyTo,
                                  String msgSubject, String msgBody, StringBuffer vCalMsg, parmEmail emailParm, String club, List<BodyPart> attachments) {
     Connection club_con = Connect.getCon(club);
     boolean result = doSending(eaddrTo, eaddrProCopy, replyTo, msgSubject, msgBody, vCalMsg, emailParm, club_con, "", null, attachments);
     Connect.close(club_con);
     return result;
 }

 //
 // This method will send individual emails to each recipient 
 //
 public static boolean doSending(ArrayList<ArrayList<String>> eaddrTo, ArrayList<String> eaddrProCopy, String replyTo,
                                  String msgSubject, String msgBody, StringBuffer vCalMsg, parmEmail emailParm, Connection con, List<BodyPart> attachments) {
     return doSending(eaddrTo, eaddrProCopy, replyTo, msgSubject, msgBody, vCalMsg, emailParm, con, "", null, attachments);
 }
 
 //
 // This method will send individual emails to each recipient 
 //
 public static boolean doSending(ArrayList<ArrayList<String>> eaddrTo, ArrayList<String> eaddrProCopy, String replyTo,
                                  String msgSubject, String msgBody, StringBuffer vCalMsg, parmEmail emailParm, Connection con,
                                  String callType, List<BodyPart> attachments) {
     return doSending(eaddrTo, eaddrProCopy, replyTo, msgSubject, msgBody, vCalMsg, emailParm, con, callType, null, attachments);
 }

 //
 // This method will send individual emails to each recipient 
 //
 // fields must contain a HashTable that contains each BodyPart ready to be attached to the outgoing email
 public static boolean doSending(ArrayList<ArrayList<String>> eaddrTo, ArrayList<String> eaddrProCopy, String replyTo,
                                  String msgSubject, String msgBody, StringBuffer vCalMsg, parmEmail emailParm, Connection con,
                                  String callType, Dictionary fields, List<BodyPart> attachments) {


    //boolean result = false;         // default to fail
    boolean attach_iCal = false;    // default to not include the iCal attachment

    boolean custom_template = false;
    boolean email_content_exists = false;      // indicates if we found email content defined

    int template_id = 0;
    int email_content_id = 0;

    String email_address = "";
    String tmpMsgBody = "";
    String htmlBody = "";
    String diningLink = "";
    String diningLinkHTML = "";
    String unsubscribeLink = "";
    String unsubscribeLinkHTML = "";
    String club = getClub.getClubName(con);     
    
    // for sanity purposes make sure the email parm has the club name set
    if (emailParm.club.equals("")) emailParm.club = club;


    String type_field = getTypeField(emailParm.type);

    // other types: password, frost, drequest

    if (!emailParm.type.equals("password") && !emailParm.type.equals("frost") && !type_field.equals("")) {

        // this type of email could  have content defined - lets see if there is any
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        try {

            pstmt = con.prepareStatement (
                        "SELECT COUNT(*) " +
                        "FROM email_content " +
                        "WHERE " + type_field + " = 1 AND activity_id = ? AND enabled = 1");

            pstmt.clearParameters();
            pstmt.setInt(1, emailParm.activity_id);
            rs = pstmt.executeQuery();

            if (rs.next()) email_content_exists = (rs.getInt(1) > 0);

        } catch (Exception exc) {

            Utilities.logError("sendEmail.doSending: Checking if content exists. email.type=" + emailParm.type + ", club=" + emailParm.club + ", error=" + exc.getMessage());

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }

    }


    if (email_content_exists) email_content_id = getContentID(emailParm, CONTENT_AREA_1, con); // 1


    //
    // Get the SMTP parmaters this club is using
    //
    parmSMTP parm = new parmSMTP();

    try {

        getSMTP.getParms(con, parm);       // get the SMTP parms

    } catch (Exception ignore) {}


    Calendar ecal = new GregorianCalendar();               // get current year
    int year = ecal.get(Calendar.YEAR);

   
    //
    // Determine which template to use for this email message
    //
    


    //
    // If here for a "preview" this override the eaddrTo ArrayList
    //
    if (emailParm.preview == true) {

        eaddrTo.clear();
        eaddrTo.add(new ArrayList<String>());
        eaddrTo.get(eaddrTo.size() - 1).add(emailParm.preview_address);
        eaddrTo.get(eaddrTo.size() - 1).add("6700");
    }

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
      //properties.put("mail.smtp.quitwait", "false");                              // if set to false, the QUIT command is sent and the connection is immediately closed. if set to true (the default), causes the transport to wait for the response to the QUIT command.

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


    //
    // Create our transport object
    //
    Transport transport = null;

    try {

        transport = mailSess.getTransport("smtp");
        transport.connect();

    } catch (Exception exc) {

        Utilities.logError("sendMail.doSending() - Error creating transport " + parm.SMTP_ADDR + ":" + parm.SMTP_PORT + " using auth="+parm.SMTP_AUTH + ", err=" + exc.toString());
        return false;
    }

    String res_type = "Reservation ";
    String notice_type = "Confirmation";

    
    if (club.equals("rollinghillscccarecip")) {
        notice_type = "Request Received";
    }
    
    if (emailParm.IS_TLT) {
        res_type = "Notification ";
    } else if (emailParm.type.equals("event") && emailParm.wait > 0) {
        res_type = "Wait List ";
    }
    
    if (emailParm.emailRem == 1) {
        notice_type = "Reminder";
    }
    
    String title = res_type + notice_type;
    String title_font_family = "verdana,arial,helvetica,sans-serif";
    
    if (emailParm.type.equals("password")) {
        title = "Password Request";
    }

    if (emailParm.type.equals("EmailToolPro")) {
        
        if (club.equals("greenbay")) {
            title = "Country Club Communication";
        }else if (club.equals("kansascitycc")) {
            title = "The Kansas City Country Club Golf Staff";
        } else if (club.equals("pattersonclub")) {
            title = "The Patterson Club";  
            title_font_family = "Copperplate Gothic Light,verdana,arial,helvetica,sans-serif";
        } else if (club.equals("edina") && emailParm.activity_id == 1) {
            title = "Tennis and Swim Communication";
        } else if (club.equals("stcloudcc") && (emailParm.user.equalsIgnoreCase("proshopmarketin") || emailParm.user.equalsIgnoreCase("proshoppres") 
                || emailParm.user.equalsIgnoreCase("proshopnewslet"))) {
            if (emailParm.user.equalsIgnoreCase("proshopmarketin")) {
                title = "Membership Central";
            } else if (emailParm.user.equalsIgnoreCase("proshoppres")) {
                title = "Club Communication";
            } else if (emailParm.user.equalsIgnoreCase("proshopnewslet")) {
                title = "SCCC Newsletter";
            }
        } else if (club.equals("pinebrookcc") && emailParm.activity_id == 1) {
            title = "Pine Brook CC Tennis Communication";
        } else if (club.equals("rhillscc") && emailParm.activity_id == 0 && emailParm.user.equalsIgnoreCase("proshopgrounds")) {
            title = "Golf Course Weekly Update";
        } else if (club.equals("winchestercc") && emailParm.activity_id == 1) {
            title = "WCC Lifestyle Communication";
        } else if (club.equals("fortwayne")) {
            title = "From The FWCC Golf Shop";
        } else if (club.equals("esterocc") && (emailParm.dining_user_id.equals("155288") || emailParm.user.equalsIgnoreCase("proshopcomm"))) {
            if (emailParm.dining_user_id.equals("155288")) {
                title = "House & Entertainment Communication";
            } else if (emailParm.user.equalsIgnoreCase("proshopcomm")) {
                title = "Club Communication";
            }
        } else {
            title = (emailParm.activity_id != 0 ? emailParm.activity_name : "Golf") + " Club Communication";
        }
        
    } else if (emailParm.type.equals("EmailToolMem")) {
        
        title = "Member Communication";
        
    } else if (emailParm.type.equals("Invoice")) {     // if from Support_invoicing
        
        title = "Invoice Notification";
        
    } else if (emailParm.type.equals("diningrem") || emailParm.type.equals("diningeventrem")) {
       
        title = "Dining Reminder";
    }
    
    
    
    //
    //  Look for any custom banner ads (top and bottom) if club wishes to sell ads to members
    //
    //  NOTE:  Must be a pre-built image with a width of no more than 640 pixels !!!!!!!!!!!!!!!
    //
    //   Just put the image(s) in v5/AEimages/clubname/Custom_Email_Images and rename the one(s) you want to use to top_banner1.jpg and/or bottom_banner1.jpg
    //
    String topBannerAd = "";
    String bottomBannerAd = "";
    
   
    if (club.equals("stcloudcc") && !emailParm.type.equals("Invoice")) {       // St Cloud CC and not an invoice
        
        topBannerAd = "top_banner1.jpg";    // get the top banner ad
        
        bottomBannerAd = "bottom_banner1.jpg";    // get the bottom banner ad
        
    }
    
    
    //
    //  Add banner ads if present (custom ads)
    //
    String html_top_banner_ad = "";
    String html_bottom_banner_ad = "";
    
    if (!topBannerAd.equals("")) {   // if custom ad found (see above custom check)
                
        html_top_banner_ad = 
                " <!-- Top Banner Ad -->" +
                " <tr>" +
                "  <td colspan=\"3\" bgcolor=\"#FFFFFF\" align=\"center\">"
                + "<p align=\"center\" style=\"font-family:verdana,arial,helvetica,sans-serif; font-size: 10px; color: #666666\">"
                + "<img src=\"http://web.foretees.com/" +rev+ "/AEimages/" +club+ "/Custom_Email_Images/" +topBannerAd+ "\" border=\"0\" alt=\"Banner Ad\">"
                + "<br><i>The above ad is brought to you on behalf of your club.</i></p>"
                + " </td></tr>";
    }
    
    if (!bottomBannerAd.equals("")) {   // if custom ad found (see above custom check)
                
        html_bottom_banner_ad = 
                " <!-- Bottom Banner Ad -->" +
                " <tr>" +
                "  <td colspan=\"3\" bgcolor=\"#FFFFFF\" align=\"center\">"
                + "<p align=\"center\" style=\"font-family:verdana,arial,helvetica,sans-serif; font-size: 10px; color: #666666\"><i>The following ad is brought to you on behalf of your club.</i><br>"
                + "<img src=\"http://web.foretees.com/" +rev+ "/AEimages/" +club+ "/Custom_Email_Images/" +bottomBannerAd+ "\" border=\"0\" alt=\"Banner Ad\"></p>"
                + " </td></tr>";
    }
    
        
    

    // Set defaults to be overridden by customs
    String email_border_style = "border: 2px solid #225522";
    String unsubscribe_bgcolor = "#CCCCAA";
    String header_bgcolor = "#CCCCAA";
    String footer_bgcolor = "#CCCCAA";
    String header_fontcolor = "#000000";
    String footer_fontcolor = "#000000";
    String logo_name = "";
    String club_logo_name = "logo.jpg";
    
    if (emailParm.activity_id == 0) {                     // if Golf
        
        logo_name = "foretees_nav.jpg";                   // ForeTees Logo
        
    } else if (emailParm.activity_id == dining_activity_id) {
        
        logo_name = "ft_dininglogo_hdr_blue.png";         // Dining Logo (blue)
        
    } else {
        
        logo_name = "FlxRez_nav.gif";                     // FlxRez Logo
    }       
    
    //
    //  Check if club is using a custom style sheet.  If so, set logo, background and font colors accordingly.
    //
    String custom_styles = Utilities.getCustomStyles(con);
    
    if (!custom_styles.equals("")) {
       
        if (emailParm.activity_id == 0) {                     // if Golf

            logo_name = "foretees_nav.png";                   // ForeTees Transparent Logo

        } else if (emailParm.activity_id == dining_activity_id) {

            logo_name = "ft_dininglogo_hdr_blue.png";         // Dining Transparent Logo (blue)

        } else {

            logo_name = "FlxRez_nav.png";                     // FlxRez Transparent Logo
        }       
    
        if (custom_styles.startsWith("original")) {
                     
            header_bgcolor = "#3A5248";
            footer_bgcolor = "#3A5248";
            unsubscribe_bgcolor = "#3A5248";
            header_fontcolor = "#FFFFFF";   // use white fonts for dark colors
            footer_fontcolor = "#FFFFFF";
    
        } else if (custom_styles.startsWith("blue")) {
                     
            header_bgcolor = "#002861";
            footer_bgcolor = "#002861";
            unsubscribe_bgcolor = "#002861";
            header_fontcolor = "#FFFFFF";        // use white fonts for dark colors
            footer_fontcolor = "#FFFFFF";
    
            if (emailParm.activity_id == 0) {                    // if Golf

               logo_name = "foretees_nav_white.jpg";             // White ForeTees Logo

            } else if (emailParm.activity_id == dining_activity_id) {

               logo_name = "ft_dininglogo_hdr.png";             // White Dining Logo (blue)

            } else {

               logo_name = "FlxRez_nav_white.jpg";                     // White FlxRez Logo
            }       
            
        } else if (custom_styles.startsWith("brown")) {
                     
            header_bgcolor = "#8C6448";
            footer_bgcolor = "#8C6448";
            unsubscribe_bgcolor = "#8C6448";
            
        } else if (custom_styles.startsWith("light-brown")) {
                     
            header_bgcolor = "#8C7C62";
            footer_bgcolor = "#8C7C62";
            unsubscribe_bgcolor = "#8C7C62";
            
        } else if (custom_styles.startsWith("green")) {
                     
            header_bgcolor = "#676E4C";
            footer_bgcolor = "#676E4C";
            unsubscribe_bgcolor = "#676E4C";
            
        } else if (custom_styles.startsWith("contemporary")) {
                     
            header_bgcolor = "#4C646B";
            footer_bgcolor = "#4C646B";
            unsubscribe_bgcolor = "#4C646B";
            
        } else if (custom_styles.startsWith("dark")) {
                     
            header_bgcolor = "#4C4C4C";
            footer_bgcolor = "#4C4C4C";
            unsubscribe_bgcolor = "#4C4C4C";
            
        } else if (custom_styles.startsWith("faded")) {
                     
            header_bgcolor = "#A3A691";
            footer_bgcolor = "#A3A691";
            unsubscribe_bgcolor = "#A3A691";
            
        } else if (custom_styles.startsWith("neon")) {
                     
            header_bgcolor = "#75C46B";
            footer_bgcolor = "#75C46B";
            unsubscribe_bgcolor = "#75C46B";
            
        } else if (custom_styles.startsWith("niners")) {
                     
            header_bgcolor = "#6A241C";
            footer_bgcolor = "#6A241C";
            unsubscribe_bgcolor = "#6A241C";
            header_fontcolor = "#FFFFFF";   // use white fonts for dark colors
            footer_fontcolor = "#FFFFFF";
    
            if (emailParm.activity_id == 0) {                    // if Golf

               logo_name = "foretees_nav_white.jpg";             // White ForeTees Logo

            } else if (emailParm.activity_id == dining_activity_id) {

               logo_name = "ft_dininglogo_hdr.png";             // White Dining Logo (blue)

            } else {

               logo_name = "FlxRez_nav_white.jpg";                     // White FlxRez Logo
            }       
            
        } else if (custom_styles.startsWith("huskers")) {
                     
            header_bgcolor = "#C60202";
            footer_bgcolor = "#C60202";
            unsubscribe_bgcolor = "#C60202";
            header_fontcolor = "#FFFFFF";   // use white fonts for dark colors
            footer_fontcolor = "#FFFFFF";
    
            if (emailParm.activity_id == 0) {                    // if Golf

               logo_name = "foretees_nav_white.jpg";             // White ForeTees Logo

            } else if (emailParm.activity_id == dining_activity_id) {

               logo_name = "ft_dininglogo_hdr.png";             // White Dining Logo (blue)

            } else {

               logo_name = "FlxRez_nav_white.jpg";                     // White FlxRez Logo
            }       
        }
    }        // end of IF custom styles
    
    if (club.equalsIgnoreCase("fortwayne")) {
            header_bgcolor = "#007d00";
            footer_bgcolor = "#007d00";
            unsubscribe_bgcolor = "#007d00";
            logo_name = "foretees_nav.png";

        
    }
    
    if (club.equals("mirabel")) {
        
        if (!emailParm.user.equalsIgnoreCase("proshopcom")) {
            email_border_style = "border: 2px solid #405664";
        } else {
            email_border_style = "border: 0px";
        }
        
        unsubscribe_bgcolor = "#FFFFFF";
        
    } else if (club.equals("fortcollins")) {
        
        if (emailParm.user.equalsIgnoreCase("proshop1") || emailParm.user.equalsIgnoreCase("proshop2") || emailParm.user.equalsIgnoreCase("proshop3") 
                || emailParm.user.equalsIgnoreCase("proshopkristi")) {
            email_border_style = "border: 0px";
            unsubscribe_bgcolor = "#FFFFFF";
        } else {
            email_border_style = "border: 2px solid #405664";
        }
    }
    
    if (club.equals("roccdallas")) {
        club_logo_name = "logo2.jpg";
    } else if (club.equals("lacumbrecc") || club.equalsIgnoreCase("fortwayne") || club.equalsIgnoreCase("bluehillscc")) {
        club_logo_name = "logo.png";
    }
    
    String html_start = "" +
        "<html>" +
        "<head></head>" +
        "<body bgcolor=\"#FFFFFF\">" +
        "<table width=\"640\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"" + email_border_style + "\" align=\"center\"><tr><td>" +
        "<table width=\"100%\" cellpadding=\"5\" cellspacing=\"0\" border=\"0\">";
    
    
    if (club.equals("mirabel") || club.equals("denvercc") || club.equals("tahoedonner") 
            || (club.equals("fortcollins") && emailParm.type.equals("EmailToolPro")
                && (emailParm.user.equalsIgnoreCase("proshop1") || emailParm.user.equalsIgnoreCase("proshop2") 
                 || emailParm.user.equalsIgnoreCase("proshop3") || emailParm.user.equalsIgnoreCase("proshopkristi")))) {
        
        String banner_filename = "";
        
        if (club.equals("mirabel")) {
            if (emailParm.type.equals("EmailToolPro")) {
                banner_filename = "banner_pro.jpg";
            } else if (emailParm.type.equals("EmailToolMem")) {
                banner_filename = "banner_mem.jpg";
            } else {
                banner_filename = "banner_res.jpg";
            }
        } else if (club.equals("denvercc")) {
            if (emailParm.activity_id == 0) {
                banner_filename = "banner_golf.jpg";
            } else if (emailParm.activity_id == 1) {
                banner_filename = "banner_tennis.jpg";
            } else if (emailParm.activity_id == 2) {
                banner_filename = "banner_juniors.jpg";
            } else if (emailParm.activity_id == 3) {
                banner_filename = "banner_fitness.jpg";
            } else if (emailParm.activity_id == 9999) {
                banner_filename = "banner_dining.jpg";
            }
        } else if (club.equals("tahoedonner")) {
            banner_filename = "banner.jpg";
        } else if (club.equals("fortcollins")) {
            banner_filename = "banner_fccc_news_2.jpg";
        }
        
        if (!club.equals("mirabel") || !emailParm.user.equalsIgnoreCase("proshopcom")) {    // Don't display the banner and logos for this Mirabel proshop user

            html_start += 
                " <!-- HEADER -->" +
                " <tr>" +
                "  <td bgcolor=\"#FFFFFF\" align=\"center\" colspan=\"3\" cellpadding=\"0\"><img src=\"http://www1.foretees.com/" + club + "/images/" + banner_filename + "\" border=\"0\" alt=\"Club Banner\"></td>" +
                " </tr>";
        }
        
    } else {

        html_start += 
            " <!-- HEADER -->" +
            " <tr>" +
            "  <td bgcolor=\"" +header_bgcolor+ "\" align=\"left\"><img src=\"http://www1.foretees.com/" + club + "/images/" + club_logo_name + "\" height=\"76\" border=\"0\" alt=\"Club Logo\"></td>" +
            "  <td bgcolor=\"" +header_bgcolor+ "\" align=\"center\"><p style=\"font-family:" + title_font_family + "; font-size: 24px; color: " +header_fontcolor+ "; font-weight: bold\">" + title + "</p></td>" +
            "  <td bgcolor=\"" +header_bgcolor+ "\" align=\"right\" valign=\"middle\"><img src=\"http://www1.foretees.com/v5/images/" + logo_name + "\" height=\"34\" border=\"0\" alt=\"Company Logo\">&nbsp;</td>" +
            " </tr>";

    }
    
    
    //  
    //  Add custom banner advertising, if present (see above customs)
    //
    if (!html_top_banner_ad.equals("")) { 
        
        html_start += html_top_banner_ad;      // insert the row with the ad
    }
                
    
    
    html_start +=
        " <!-- BODY -->" +
        " <tr>" +
        "  <td colspan=\"3\" style=\"padding: 16px\" bgcolor=\"#FFFFFF\">" +
        "   <p style=\"font-family:verdana,arial,helvetica,sans-serif; font-size: 12px; color: #000000\">";

        //  "  <td bgcolor=\"#CCCCAA\" align=\"right\" valign=\"middle\"><img src=\"http://www1.foretees.com/v5/images/" + (emailParm.activity_id != 0 ? "FlxRez_nav.gif" : "foretees_nav.jpg") + "\" height=\"34\" border=\"0\" alt=\"" + (emailParm.activity_id != 0 ? "FlxRez" : "ForeTees") + " Logo\">&nbsp;</td>" +
        // " + ((emailParm.type.equals( "activity" )) ? "FlxRez" : "ForeTees") + "   #225522

    
    //
    //  This footer is no longer used - refer to unsubscribe messages below
    //
    String html_footer = "" +
        " <tr>" +
        "  <td bgcolor=\"" +footer_bgcolor+ "\" align=\"center\" colspan=\"3\">" +
        "  <p style=\"font-family:verdana,arial,helvetica,sans-serif; font-size: 10px; color: " +footer_fontcolor+ "\">" +
        "   To stop receiving these email notifications, " +
        "   please <a href=\"http://www1.foretees.com/"+rev+"/servlet/Login?extlogin&#ELS#=els&caller=email\" style=\"color:#000000\">click here to unsubscribe</a>.<br>" +
        "   Please do not reply to this email. It is an automated notification email from the " + ((emailParm.type.equals( "activity" )) ? "FlxRez" : "ForeTees") + " System.<br>" +
        "   If you require further assistance, contact your " + (emailParm.activity_id != 0 ? emailParm.activity_name : "golf") + " professionals.</p>" +
        "  </td>" +
        " </tr>";
    

    //  
    //  Add custom banner advertising, if present (see above customs)
    //
    String html_end = "";
    
    if (!html_bottom_banner_ad.equals("")) { 
        
        html_end = html_bottom_banner_ad;      // insert the row with the ad
    }
                
    
    html_end += "" +
       "</table>" +
       "</td></tr></table>" +
       "<p align=\"center\" style=\"font-family:verdana,arial,helvetica,sans-serif; font-size: 9px; color: #666666\">" +
       "Copyright &copy; " +year+ " ForeTees, LLC. &nbsp; All Rights Reserved" +
       "</p>" +
       "</body></html>";
    
    
    String efrom = parm.EMAIL_FROM;

    if (emailParm.type.startsWith("EmailTool") || emailParm.type.equals("Invoice")) {

        efrom = emailParm.from;

    } else if (club.equals("potowomut")) {
        
        efrom = "potowomutreservations@foretees.com";
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
    String legacy_message = "";
    
    if (club.equals("cherryhills") && (emailParm.type.equals("tee") || emailParm.type.equals("lottery"))) {      // if Cherry Hills Tee Time
            
       legacy_message = checkCherryGuests(emailParm, club);                // get custom message if guest(s) included in tee time            
    }
    
    if (club.equals("wingedfoot") && emailParm.type.equals("tee")) {      // if Winged Foot Tee Time or Notification (both use type=tee)
            
       legacy_message = checkCherryGuests(emailParm, club);                // get custom message if guest(s) included in tee time            
    }

    if (club.equals("oakhillcc") && emailParm.type.equals("tee")) {       // if Oak Hill CC Tee Time

        legacy_message = checkCherryGuests(emailParm, club);               // get custom message if guest(s) included in tee time
    }

    /*
    if (club.equals("willamette") && emailParm.type.equals("tee")) {      // if Willamette Valley Tee Time
            
       legacy_message = checkWillamette(emailParm, con);                // check if custom message to be included in tee time            
    }
     */

    //if (club.equals("demov4") && emailParm.type.equals("tee")) {      // if Willamette Valley Tee Time

       //legacy_message = "This is our <b>custom message</b>. <img src=\"http://www1.foretees.com/v5/images/foretees.gif\"><center>And we can have <a href=\"www.foretees.com\">links</a> in here too.</center>";
    //}
    
    // If we're sending a dining reminder email, the HTML msgBody in in parmEmail.htmlBody
    if (emailParm.type.equals("diningrem") || emailParm.type.equals("diningeventrem")) {
        msgBody = emailParm.htmlBody;
    }

    //
    // Loop over the recipient array lists and send each one their own email
    //
    String origMsgBody = msgBody;
    
    recipientLoop:
    for (int i = 0; i < eaddrTo.size(); i++) {

       msgBody = origMsgBody;       // Reset to original state, incase an ##ELS## string was replaced.

       if (legacy_message.equals("")) {

           if (email_content_exists) {

               int test_id = getContentID(emailParm, LOWER_SPAN, con);
               custom_message = getContent(test_id, con, club, eaddrTo.get(i).get(1));  // check for a custom content message
               
           }

       } else {
          
           custom_message = legacy_message;      // use old custom message if defined above
       }

       if (!msgBody.equals("") && !club.equals("") && !eaddrTo.get(i).get(1).equals("")) {
           //
           //  content found and email destined for a member, look for an embedded link that
           //  needs the club's site name and the username so the member can link directly back to ForeTees
           //
           if (msgBody.indexOf("##ELS##") > -1) {          // if link found that needs the club:username

              String els = Utilities.getELS(club, eaddrTo.get(i).get(1));   // get the ELS code (encrypetd) for this club and user

              msgBody = msgBody.replace("##ELS##", els);       
           }
       }

       /*
        // if AOL address then append trailer
        if (eaddrTo.get(i).get(0).endsWith( "aol.com" ) || eaddrTo.get(i).get(0).endsWith( "mnwebhost.net" )) {

            //msgBody = "\nSent on " + Utilities.getDateString(con, 0, "-") + " at " + Utilities.getSimpleTime(Utilities.getTime(con)) + "\n\n" + msgBody + trailerAOL;
            msgBody += trailerAOL;                 // appeand AOL trailer
        }
        */

        tmpMsgBody = msgBody; // msgBody is passed in from the caller and contains the main information for this email

        // If we're sending a dining reminder email, the Text msgBody in in parmEmail.textBody
        if (emailParm.type.equals("diningrem") || emailParm.type.equals("diningeventrem")) {
            tmpMsgBody = emailParm.txtBody;
        }
        
        //test = true;
        
        MimetypesFileTypeMap mimetypes = (MimetypesFileTypeMap)MimetypesFileTypeMap.getDefaultFileTypeMap();
        mimetypes.addMimeTypes("text/calendar ics ICS");

        MailcapCommandMap mailcap = (MailcapCommandMap) MailcapCommandMap.getDefaultCommandMap();
        mailcap.addMailcap("text/calendar;; x-java-content-handler=com.sun.mail.handlers.text_plain");

        MimeMessage message = new MimeMessage(mailSess);

        try {

            message.setFrom(new InternetAddress(efrom));                  // set from addr
            if (emailParm.type.startsWith("EmailTool") || emailParm.type.equals("Invoice")) {
                InternetAddress iareply[] = new InternetAddress[1];               // create replyto array
                iareply[0] = new InternetAddress(emailParm.replyTo);
                message.setReplyTo(iareply);
            }                  // set from addr
            message.setSubject( msgSubject );                                       // set subject line
            message.setSentDate(new java.util.Date());                              // set date/time sent
            if (emailParm.preview == false || (emailParm.preview == true && !emailParm.preview_address.equals("")))
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(eaddrTo.get(i).get(0)));


            // if here for EmailToolPro then add BCC for our internal debugging (only do once!)
            if (i == 0 && emailParm.type.equals("EmailToolPro"))
                message.addRecipient(Message.RecipientType.BCC, new InternetAddress("proemailtool@foretees.com"));

        } catch (javax.mail.internet.AddressException e) {

            // these are common and likely just a malformed address (how are these getting into the system?! roster sync?
            if (!eaddrTo.get(i).get(1).equals( "" )) {

                // it's a member - lets log who for the heck of it but we should 
                // probably take some action like flagging it or just erasing it
                String err_msg = "sendEmail() - BAD EMAIL ADDRESS: club=" + emailParm.club + ", user=" + eaddrTo.get(i).get(1) + ", email=" + eaddrTo.get(i).get(0);
                
                if (emailParm.type.startsWith("EmailTool") || emailParm.type.equals("Invoice")) {    // Include the replyTo address as well, if relevant.
                    err_msg += ", replyTo=" + emailParm.replyTo;
                }
                
                // If a reference string is present in the exception object, include it, as well as the position where the exception was found in the ref string
                if (e.getRef() != null) {
                    err_msg += ", errorRef=" + e.getRef() + " (pos=" + e.getPos() + ")";
                }
                
                Utilities.logError(err_msg);
            }

            continue recipientLoop;
            
        } catch (Exception exc) {
            
            Utilities.logError("Fatal Error1 in sendEmail.doSending: err=" + exc.getMessage() + " " + exc.toString() + ", strace=" + Utilities.getStackTraceAsString(exc));
            return false;
        }

        /*
        // if AOL address then append trailer
        if (eaddrTo.get(i).get(0).endsWith( "aol.com" ) || eaddrTo.get(i).get(0).endsWith( "mnwebhost.net" )) {

            tmpMsgBody = "\nSent on " + Utilities.getDate(con) + " at " + Utilities.getTime(con) + "\n\n" + tmpMsgBody + trailerAOL;                  // appeand AOL trailer
        }
        */

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
                      
                   } else if (emailParm.emailRem == 1) {    // Do not include iCal attachments for email reminders!
                      attach_iCal = false;
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
                         "please visit http://www1.foretees.com/"+rev+"/servlet/Login?extlogin&els=" + els + "&caller=email to update your email preferences. " +
                         "Please contact your club for further assistance.";

                if (club.equals("olyclub") && emailParm.type.equals("event")) {
                    unsubscribeLinkHTML = "" +
                            "<p style=\"font-family:verdana,arial,helvetica,sans-serif; font-size: 10px; color: #000000\">" +
                            "To stop receiving these email notifications, " +
                            "please <a href=\"http://www1.foretees.com/"+rev+"/servlet/Login?extlogin&els=" + els + "&caller=email\" style=\"color: " +footer_fontcolor+ "\">click here to unsubscribe</a>.<br>";
//                            "If you require further assistance, please contact your Tournament Coordinator at jdugan@olyclub.com.</p>";
                } else if (club.equals("stcloudcc") && emailParm.type.equals("EmailToolPro") && emailParm.user.equalsIgnoreCase("proshopnewslet")) {
                    unsubscribeLinkHTML = "" +
                            "<p style=\"font-family:verdana,arial,helvetica,sans-serif; font-size: 10px; color: " +footer_fontcolor+ "\">" +
                            "To stop receiving these email notifications, " +
                            "please <a href=\"http://www1.foretees.com/"+rev+"/servlet/Login?extlogin&els=" + els + "&caller=email\" style=\"color:" +footer_fontcolor+ "\">click here to unsubscribe</a>.<br>" +
                            "If you require further assistance, contact your Club.</p>";
                } else if (club.equals("kansascitycc")){
                    unsubscribeLinkHTML = "<p style=\"font-family:verdana,arial,helvetica,sans-serif; font-size: 10px; color: " +footer_fontcolor+ "\">" +
                            "To stop receiving these email notifications, " +
                            "please <a href=\"http://www1.foretees.com/"+rev+"/servlet/Login?extlogin&els=" + els + "&caller=email\" style=\"color:" +footer_fontcolor+ "\">click here to unsubscribe</a>.<br>";
                    
                } else if (club.equals("esterocc") && emailParm.activity_id == 9999) {

                    unsubscribeLinkHTML = "" +
                            "<p style=\"font-family:verdana,arial,helvetica,sans-serif; font-size: 10px; color: " +footer_fontcolor+ "\">" +
                            "To stop receiving these email notifications, " +
                            "please <a href=\"http://www1.foretees.com/"+rev+"/servlet/Login?extlogin&els=" + els + "&caller=email\" style=\"color:" +footer_fontcolor+ "\">click here to unsubscribe</a>.<br>" +
                            "If you require further assistance, contact your Club Staff.</p>";
                    
                }else if (club.equals("fortwayne")) {

                     unsubscribeLinkHTML = "" +
                            "<p style=\"font-family:verdana,arial,helvetica,sans-serif; font-size: 10px; color: " +footer_fontcolor+ "\">" +
                            "To stop receiving these email notifications, " +
                            "please <a href=\"http://www1.foretees.com/"+rev+"/servlet/Login?extlogin&els=" + els + "&caller=email\" style=\"color:" +footer_fontcolor+ "\">click here to unsubscribe</a>.<br>" +
                            "5221 Covington Road, Fort Wayne, IN 46804  (260) 432-2581</p>";
                
                } else {
                    unsubscribeLinkHTML = "" +
                            "<p style=\"font-family:verdana,arial,helvetica,sans-serif; font-size: 10px; color: " +footer_fontcolor+ "\">" +
                            "To stop receiving these email notifications, " +
                            "please <a href=\"http://www1.foretees.com/"+rev+"/servlet/Login?extlogin&els=" + els + "&caller=email\" style=\"color:" +footer_fontcolor+ "\">click here to unsubscribe</a>.<br>" +
                            "If you require further assistance, contact your " + (emailParm.activity_name.equals("") ? "golf" : emailParm.activity_name) + " professionals.</p>";
                }
                    
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

            if (email_content_id == 0) {

                // default
                if (!emailParm.type.equals("EmailToolPro") && !emailParm.type.equals("diningrem") && !emailParm.type.equals("diningeventrem")) {
                    htmlBody += msgBody.replace("\n", "<br>") + "</p>";
                } else {
                    htmlBody += msgBody;
                }

            } else {

                // implement the default template
                htmlBody += "<table width=\"100%\" border=\"0\"><tr valign=top><td width=\"48%\">" +
                        "<p style=\"font-family:verdana,arial,helvetica,sans-serif; font-size: 12px; color: #000000\">";
                if (!emailParm.type.equals("EmailToolPro") && !emailParm.type.equals("diningrem") && !emailParm.type.equals("diningeventrem")) {
                    htmlBody += msgBody.replace("\n", "<br>");
                } else {
                    htmlBody += msgBody;
                }
                htmlBody += "</p></td><td width=\"4%\">&nbsp;</td><td width=\"48%\">" + getContent(email_content_id, con, club, eaddrTo.get(i).get(1)) + "</td></tr></table>";

                
                // implement the default template - this will place the custom message below the main message content
               /*
                htmlBody += "<table width=\"100%\" border=\"0\" cellpadding=\"7\"><tr valign=top><td>" +
                        "<p style=\"font-family:verdana,arial,helvetica,sans-serif; font-size: 12px; color: #000000\">";
                htmlBody += msgBody.replace("\n", "<br>");
                htmlBody += "<br></p></td></tr>";
                htmlBody += "<tr bgcolor=\"#F5F5DC\"><td>" + getContent(email_content_id, con) + "<br><br></td></tr></table>";
                */

            }

            htmlBody = htmlBody.replace("New Group:", "<u>New Group:</u>");
            htmlBody = htmlBody.replace("Previous Group (before change):", "<u>Previous Group:</u>");
            if (!diningLinkHTML.equals("") && !emailParm.type.startsWith("EmailTool")) htmlBody += diningLinkHTML;
            
            //  add custom message here, if any
            
            if (!custom_message.equals("")) {

               htmlBody += "</td></tr>";
               htmlBody += "<tr><td bgcolor=\"white\" align=\"center\" colspan=\"3\">";
               htmlBody += custom_message + "<br>";    // add custom footer message and a spacer
            }

            if (emailParm.type.startsWith("EmailTool") || emailParm.type.equals("Invoice")) {

                htmlBody += emailParm.message.replace("\n", "<br>");  // contains the email tool trailer
            }

            htmlBody += "</td></tr>"; // end main body area
            
            //  Unsubscribe info
            // if (!unsubscribeLinkHTML.equals("") && !emailParm.type.startsWith("EmailTool")) {
            if (!unsubscribeLinkHTML.equals("")) {    // include Unsubscribe in all emails to members
                htmlBody += "<tr><td bgcolor=\"" + unsubscribe_bgcolor + "\" align=\"center\" colspan=\"3\">";
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
                //icsBodyPart.addHeader("Content-Encoding", "UTF-8");
                //icsBodyPart.addHeader("Charset", "UTF-8");
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
            if (fields != null && (emailParm.type.equals("Invoice") || emailParm.type.startsWith("EmailTool"))) {
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

            // Another way to check for attachments
            if(attachments != null){
                for(BodyPart attachment : attachments){
                    mpRoot.addBodyPart(attachment);
                    count_attach++;
                }
            }

            message.setContent(mpRoot);
            message.saveChanges();

            if (emailParm.out != null && emailParm.preview == true) {

                emailParm.out.println(htmlBody);
                
            }

            if (emailParm.preview == false || (emailParm.preview == true && !emailParm.preview_address.equals(""))) {

                //Transport transport = mailSess.getTransport("smtp");
                //transport.connect();
                
                // make sure the transport object is still open
                if (!transport.isConnected()) transport.connect();

                transport.sendMessage(message, message.getAllRecipients());
                //transport.close();

                // Transport.send() is a static method - DO NOT USE!
                //Transport.send(message);

            }

            error = false;

        } catch (Exception exc) {
            
            Utilities.logError("sendEmail.doSending: Error sending. club=" + club + ", type=" + emailParm.type + ", # of recipients = " +eaddrTo.size()+ ", err = " + exc.getMessage() + ", errString = " + exc.toString());
            error = true;
            //if (emailParm.out != null) emailParm.out.println("<br><br><p>Error sending test email. Error: " + exc.toString() + "</p>");
            //return false;  // don't return false here because then we won't try to send this email to any other recipients

        } finally {

            if (error == false) count++;      // if no error then bump the count

        }

    } // end loop of 'to' recipients


    // close our transport obect - we can leave it open and resuse below if all is well
    try {

        transport.close();

    } catch (Exception exc) {

        Utilities.logError("sendEmail.doSending: Error closing transport. err = " + exc.getMessage() + ", errString = " + exc.toString());
        
    }


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
                email_address = email_address.substring(0, email_address.length() - 1); // trim off the last char
            }

            MimeMessage message = new MimeMessage(mailSess);

            try {

                message.setFrom(new InternetAddress(parm.EMAIL_FROM));                  // set from addr
                message.setSubject( msgSubject );                                       // set subject line
                message.setSentDate(new java.util.Date());                              // set date/time sent

                message.addRecipient(Message.RecipientType.TO, new InternetAddress(email_address));

            } catch (Exception exc) {

                Utilities.logError("Error1b in sendEmail.doSending - club=" + club + ", email=" + email_address + ", Error: " + exc.getMessage());
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

                Utilities.logError("Error2b in sendEmail.doSending - club=" + club + ", email=" + email_address + ", Error: : " + exc.getMessage());
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

                    Utilities.logError("Error3b in sendEmail.doSending - club=" + club + ", email=" + email_address + ", Error: : " + exc.getMessage());
                }

            } // end if lesson


            // send the email
            try {

                message.setContent(multipart);

                Transport.send(message);

            } catch (Exception exc) {

                Utilities.logError("Error4b in sendEmail.doSending: club=" + club + ", email=" + email_address + ", type=" + emailParm.type + ", err = " + exc.getMessage());
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
            message.addRecipient(Message.RecipientType.BCC, new InternetAddress("proemailtool@foretees.com")); // add ourselves for debugging
            message.setText(proConfirm + "\n\n");

            Transport.send(message);

        } catch (Exception exc) {

            Utilities.logError("Error5 in sendEmail.doSending: proConfirm for club=" + club + ", proConfirm=" + proConfirm + ", err = " + exc.getMessage());
        
        } finally {

            //Utilities.logError("EmailTool: (finished) club=" + club + ", subject=" + msgSubject + ", attempted=" + eaddrTo.size() + ", queued=" + count + ", attachments=" + count_attach);

        }
        
    }

    
    //
    //
    //
    if (emailParm.type.equals("EmailToolPro") || (emailParm.type.equals("EmailToolMem") && emailParm.handicapChair)) {
        
        //
        
        int email_type = 0;
        int last_id = 0;
        
        if (emailParm.type.equals("EmailToolPro")) {
            
            email_type = 1;
            
        } else if (emailParm.type.equals("EmailToolMem") && emailParm.handicapChair) {
            
            email_type = 2;
            
        } else if (emailParm.type.equals("define more here")) {
            
            email_type = 3;
            
        }
        
        Statement stmt = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        StringBuilder insertStatement = new StringBuilder();
        
        try {

            // add the main email log entry
            pstmt = con.prepareStatement(
                "INSERT INTO email_audit_log (id, email_type, sent_date, recipients, attachments, from_address, subject, message) VALUES (null, ?, now(), ?, ?, ?, ?, ?)");

            // parm.SMTP_ADDR
            String log_subject = "";
            
            try {
                log_subject = emailParm.subject.substring(0, emailParm.subject.lastIndexOf("(") - 1);
            } catch (Exception e) {
                log_subject = emailParm.subject;
            }
            
            pstmt.clearParameters();
            pstmt.setInt(1, email_type);                    // email_type
            pstmt.setInt(2, eaddrTo.size());                // recipients
            pstmt.setInt(3, count_attach);                  // attachments
            pstmt.setString(4, emailParm.replyTo);          // from_address
            pstmt.setString(5, log_subject);          // subject  msgSubject
            pstmt.setString(6, origMsgBody);                // message body
            pstmt.executeUpdate();
            pstmt.close();

            // find the id of the row we just inserted
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT LAST_INSERT_ID()");
            if (rs.next()) last_id = rs.getInt(1);
            
            // insert all the recipients
            insertStatement.append("INSERT INTO email_audit_log_details (audit_log_id, username, email) VALUES ");

            for (int i = 0; i < eaddrTo.size(); i++) {

                insertStatement.append("(");
                insertStatement.append(last_id);
                insertStatement.append(",\"");
                insertStatement.append(eaddrTo.get(i).get(1));
                insertStatement.append("\",\"");
                insertStatement.append(eaddrTo.get(i).get(0));
                insertStatement.append("\"),");
            }

            insertStatement.deleteCharAt(insertStatement.length() - 1);

            pstmt = con.prepareStatement(insertStatement.toString());
            pstmt.clearParameters();
            pstmt.executeUpdate();

        } catch (Exception exc) {
            
            Utilities.logError("sendEmail - auditLog error - " + club + " - " + exc.toString() + ", subject=" + emailParm.subject + ", insertStatement=" + insertStatement.toString() + ", trace=" + Utilities.getStackTraceAsString(exc));
            
        } finally {
            
            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}
            
        }
        
    } // end adding of audit trail for EmailToolPro
    
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

   PreparedStatement pstmte1 = null;
   
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
   String mNum = "";
   
   String customTrailer = trailer;       // default to standard email trailer
   
   String f_b = "";
   String eampm = "";
   String etime = "";
   int ehr = 0;
   int emin = 0;
   int send = 0;
   String errorMsg = "";


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
                  "SELECT name_last, name_first, name_mi, memNum FROM member2b WHERE username = ?");

         pstmte.clearParameters();        // clear the parms
         pstmte.setString(1, user);
         rs = pstmte.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            userLast = rs.getString("name_last");        // user's name
            userFirst = rs.getString("name_first");
            userMi = rs.getString("name_mi");
            mNum = rs.getString("memNum");

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
   String to2 = "";
   String to3 = "";
   String to4 = "";
   
   if (club.equals( "hallbrookcc" )) {
      to = "golf@hallbrookcc.org";                                // to address for Hallbrook CC
   } else if (club.equals("shadycanyongolfclub")) {
      to = "";
   } else if (club.equals("baltimore")) {
      to = "snicholls@bcc1898.com";
   } else if (club.equals("riverside")) {
      to = "nward@riversidegcc.com";
      to2 = "psutton@riversidegcc.com";
   } else if (club.equals("rehobothbeachcc")) {
      to = "caddie.rbcc@gmail.com";
//   } else if (club.equals("belfair")) {
//      to = "rleitman@belfair1811.com";  
//      to2 = "kyarrison@belfair1811.com";  
//      to3 = "jswift@belfair1811.com";  
//      to4 = "jmattox@belfair1811.com";  
//   } else if (club.equals("oakhillcc")) {                     // Oak Hill CC
//      to = "bmcareavy@therada.org";
   } else if (club.equals("silverleaf")) {
       to = "wschlaebitz@silverleafclub.com";
   } else if (club.equals("demowill")) {
       to = "whawkins@foretees.com";
       to2 = "support@foretees.com";
   } else if (club.equals("rollinghillscccarecip")) {
       to = "blairh@rollinghillscc.com";
       to2 = "jasons@rollinghillscc.com";
   } else if (club.equals("bearcreekgc")) {
       to = "golfshop@bcgc.org";
   } else if (club.equals("calclub")) {
       to = "chris@calclub.org";
   } else if (club.equals("roxiticus")) {
       to = "proshop@roxiticus.com";
   }else if (club.equals("pmarshgc")) {
       to = "philpellerito@pmarshgc.com"; 
       to2 = "nickbaumhart@pmarshgc.com";
   } else if (club.equals("pradera")) {
       to = "bfreyschlag@theclubatpradera.com";
   } else if (club.equals("pinery")) {
       to = "llangton@thepinerycc.com";
   }/* else if (club.equals("snoqualmieridge")) {
       to = "mbrown@tpcsr.com";
   }*/ else if (club.equals("tartanfields")) {
       to = "jrussell@tartanfields.com";
   } else if (club.equals("philcricketrecip")) {              //  Philly Cricket Recip Site
      
      if (course.equals("Ace Club")) {
         to = "linda.nevatt@acegroup.com";
      } else if (course.equals("Applebrook")) {
         to = "dmcnabb@applebrookgolfclub.com";
         to2 = "dmcfalls@applebrookgolfclub.com";
      } else if (course.equals("Aronimink")) {
         to = "tfoley@aronimink.org";
      } else if (course.equals("Bellewood")) {
         to = "pgasr@aol.com";
      } else if (course.equals("Bidermann")) {
         to = "amalizia@vicmead.com";
         to2 = "golfshop@vicmead.com";
      } else if (course.equals("Blue Bell")) {
         to = "swahaljr@hansen-properties.com";
      } else if (course.equals("Burlington")) {
         to = "bcc.golfshop@comcast.net";
      } else if (course.equals("Cedarbrook")) {
         to = "drlrggkm@aol.com";
      } else if (course.equals("Chester Valley")) {
         to = "jdoctor@chestervalleygc.org";
      } else if (course.equals("Commonwealth")) {
         to = "pshine@commonwealthgolf.com";
      } else if (course.equals("Concord")) {
         to = "Mmoses1231@comcast.net";
      } else if (course.equals("Doylestown")) {
         to = "gsskp@aol.com";
      } else if (course.equals("Fieldstone")) {
         to = "jlarkin@troongolf.com";
         to2 = "jplarks@gmail.com";
      } else if (course.equals("French Creek")) {
         to = "jhaas@frenchcreekgolf.com";
      } else if (course.equals("Galloway")) {
         to = "mkill90953@comcast.net";
      } else if (course.equals("Greate Bay")) {
         to = "mparson@greatebay.com";
      } else if (course.equals("Green Valley")) {
         to = "john.cooper@greenvalleycc.org";
         to2 = "jackie.coll@greenvalleycc.org";
         to3 = "brian.way@greenvalleycc.org";
      } else if (course.equals("Gulph Mills")) {
         to = "golfshop@gulphmillsgc.com";
      } else if (course.equals("Hersheys Mill")) {
         to = "sqrl72@comcast.net";
      } else if (course.equals("Hidden Creek")) {
         to = "amadsen@hiddencreekclub.com";
      } else if (course.equals("Huntingdon Valley")) {
         to = "idalzell@hvccpa.org";
      } else if (course.equals("Huntsville")) {
         to = "mattocchiato@pga.com";
      } else if (course.equals("Indian Valley")) {
         to = "joeypohle@gmail.com";
      } else if (course.equals("Jericho National")) {
         to = "jfarese@pga.com";
      } else if (course.equals("Lancaster")) {
         to = "rgibson@lancastercc.com";
      } else if (course.equals("Laurel Creek")) {
         to = "jdimarco@laurelcreek.org";
      } else if (course.equals("Ledgerock")) {
         to = "jarod@ledgerockgolf.com";
         to2 = "ben@ledgerockgolf.com";
      } else if (course.equals("Lehigh")) {
         to = "proshop@lehighcc.com";
      } else if (course.equals("Little Mill")) {
         to = "george@littlemill.com";
      } else if (course.equals("Llanerch")) {
         to = "cwilkinson@llanerchcc.org";
      } else if (course.equals("Lookaway")) {
         to = "lookawaygolfshop@aol.com";
      } else if (course.equals("Lulu")) {
         to = "jonrusk215@yahoo.com";
      } else if (course.equals("Manufacturers")) {
         to = "sikina23@pga.com";
      } else if (course.equals("Meadowlands")) {
         to = "johnshap88@gmail.com";
      } else if (course.equals("North Hills")) {
         to = "pronh@verizon.net";
      } else if (course.equals("Old York Road")) {
         to = "dave.mckenzie@oyrcc.com";
      } else if (course.equals("Overbrook")) {
         to = "ekennedy@overbrookgolfclub.com";
         to2 = "tournaments@overbrookgolfclub.com";
      } else if (course.equals("Philadelphia")) {
         to = "sreilly@philadelphiacc.net";
      } else if (course.equals("Plymouth")) {
         to = "chris@plymouthcc.com";
         to2 = "adamrstafford@gmail.com";
      } else if (course.equals("Radley Run")) {
         to = "jkellogg@radleyruncc.com";
      } else if (course.equals("Radnor Valley")) {
         to = "rvccpro@aol.com";
      } else if (course.equals("Rivercrest")) {
         to = "golfshop@rivercrestgolfclub.com";
      } else if (course.equals("Riverton")) {
         to = "kevinduffy@therivertoncountryclub.com";
      } else if (course.equals("Running Deer")) {
         to = "johntyrell13@gmail.com";
      } else if (course.equals("Sandy Run")) {
         to = "srproshop@comcast.net";
         to2 = "loopergolf@gmail.com";
      } else if (course.equals("Spring Mill")) {
         to = "breedy@springmillcountryclub.com";
      } else if (course.equals("SpringFord")) {
         to = "resteinmetz@comcast.net";
         to2 = "rich@springfordcc.org";
      } else if (course.equals("Springhaven")) {
         to = "jnewlon@springhavengolf.com";
         to2 = "bspeakman@springhavengolf.com";
         to3 = "rspeakman1554@comast.net";
      } else if (course.equals("St Davids")) {
         to = "dean@stdavidsgc.com";
      } else if (course.equals("Stonewall")) {
         to = "ryan@stonewalllinks.com";
      } else if (course.equals("Sunnybrook")) {
         to = "eschultz@sunnybrook.org";
      } else if (course.equals("Talamore")) {
         to = "tyler.johnsonpga@comcast.net";
      } else if (course.equals("Tavistock")) {
         to = "rhgolfshop@tavistockcc.com";
         to2 = "golfshop@tavistockcc.com";
      } else if (course.equals("Torresdale Frankford")) {
         to = "bunkerleft@aol.com";
      } else if (course.equals("Trenton")) {
         to = "gdendler@trentoncc.com";
      } else if (course.equals("Trump National")) {
         to = "dcartwright@trumpnational.com";
      } else if (course.equals("Waynesborough")) {
         to = "tmichaels@wcc1965.org";
      } else if (course.equals("White Manor")) {
         to = "mlevine@whitemanorcc.com";
      } else if (course.equals("Whitemarsh Valley")) {
         to = "proshop@whitemarshvalleycc.com";
      } else if (course.equals("Whitford")) {
         to = "mladden@whitfordcc.com";
      } else if (course.equals("Wilmington")) {
         to = "golfshop@wilmingtoncc.com";
      }
   } else if (club.equalsIgnoreCase("ccstalbans")) {
          to = "jschwent@ccstalbans.com";
          to2 = "zpotts@ccstalbans.com";
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
   can = "The following tee time has been CANCELED by " + author + ".\n\n";
   String subjectNew = "ADD from ForeTees - A Guest Tee Time has been added.";
   String subjectMod = "CHANGE from ForeTees - A Guest Tee Time has been changed.";
   String subjectCan = "DELETED from ForeTees - A Guest Tee Time has been canceled.";

   if (club.equals( "hallbrookcc" )) {

      subjectNew = "ADD from ForeTees - A Caddie Request has been added.";
      subjectMod = "CHANGE from ForeTees - A Caddie Request has been changed.";
      subjectCan = "DELETED from ForeTees - A Caddie has been canceled.";
      
   } else if (club.equals("shadycanyongolfclub")) {

      enew = "The following Tee Time Request has been ADDED by " + author + ".\n\n";
      subjectNew = "ADD from ForeTees - A note was included in a new Tee Time.";
      subjectMod = "CHANGE from ForeTees - A note has been modified in a Tee Time.";
      subjectCan = "DELETED from ForeTees - A Tee Time containing a note has been canceled.";
      
   } else if (club.equals("baltimore") || club.equals("rehobothbeachcc") || club.equals("silverleaf") || club.equals("riverside")) {
       
       mod = "The following Tee Time has been MODIFIED by " + author + ".\n\n";
       enew = "The following Tee Time has been ENTERED by " + author + ".\n\n";
       can = "The following Tee Time has been CANCELED by " + author + ".\n\n";
       subjectNew = "ADD from ForeTees - A tee time including a Caddie has been added.";
       subjectMod = "CHANGE from ForeTees - A tee time including a Caddie has been changed.";
       subjectCan = "DELETED from ForeTees - A tee time including a Caddie has been canceled.";
       
   }/* else if (club.equals("oakhillcc")) {         // Oak Hill CC
       
       if (!user.startsWith( "proshop" )) {        // if not proshop

           can = "The following tee time has been CANCELED by " + author + ", member number " +mNum+ ".\n\n";
       }
       
       subjectCan = "ForeTees - A tee time has been canceled.";
       
       customTrailer = "\n\nNOTICE: This message is being sent to you from the ForeTees system to inform you that a member or golf shop staff has canceled a tee time.";   
       
   } */else if (club.equals("philcricketrecip")) {              //  Philly Cricket Recip Site
      
       mod = "The following Tee Time has been MODIFIED by " + author + ".\n\n";
       enew = "The following Tee Time has been ENTERED by " + author + ".\n\n";
       can = "The following Tee Time has been CANCELED by " + author + ".\n\n";
       subjectNew = "New Tee Time from The Philadelphia Cricket Club";
       subjectMod = "Modified Tee Time from The Philadelphia Cricket Club";
       subjectCan = "Canceled Tee Time from The Philadelphia Cricket Club";
       
   } else if (club.equals("bearcreekgc") || club.equals("roxiticus")) {
       
       mod = "The following Notification has been MODIFIED by " + author + ".\n\n";
       enew = "The following Notification has been ENTERED by " + author + ".\n\n";
       can = "The following Notification has been CANCELED by " + author + ".\n\n";
       subjectNew = "ADD from ForeTees - A notification has been added.";
       subjectMod = "CHANGE from ForeTees - A notification has been changed.";
       subjectCan = "DELETED from ForeTees - A notification has been canceled.";
       
   } else if (club.equals("pmarshgc")) {              //  Philly Cricket Recip Site
      
       mod = "The following Tee Time has been MODIFIED by " + author + ".\n\n";
       enew = "The following Tee Time has been ENTERED by " + author + ".\n\n";
       can = "The following Tee Time has been CANCELED by " + author + ".\n\n";
       subjectNew = "ADD from ForeTees - A Tee Time has been added on the day of.";
       subjectMod = "CHANGE from ForeTees - A Tee Time has been changed on the day of.";
       subjectCan = "DELETED from ForeTees - A Tee Time has been canceled on the day of.";
       
   } else if (club.equalsIgnoreCase("ccstalbans")) {
       mod = "The following Tee Time has been MODIFIED by " + author + ".\n\n";
       enew = "The following Tee Time has been ENTERED by " + author + ".\n\n";
       can = "The following Tee Time has been CANCELED by " + author + ".\n\n";
       subjectNew = "ADD from ForeTees - A Tee Time has been added on the day of or after 6pm the day prior.";
       subjectMod = "CHANGE from ForeTees - A Tee Time has been changed on the day of or after 6pm the day prior.";
       subjectCan = "DELETED from ForeTees - A Tee Time has been canceled on the day of or after 6pm the day prior.";  
   
   } else if (club.equalsIgnoreCase("carolinacc")) {  //changed spelling of calceled to cancelled

       can = "The following tee time has been CANCELLED by " + author + ".\n\n";
       subjectCan = "DELETED from ForeTees - A Guest Tee Time has been cancelled.";

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

      if (club.equals("shadycanyongolfclub")) {     // Shady Canyon has 7 different addresses they would like their note update notifications to go to.

          message.addRecipient(Message.RecipientType.TO, new InternetAddress("kmanley@shadycanyongolfclub.com"));
          message.addRecipient(Message.RecipientType.TO, new InternetAddress("bgunson@shadycanyongolfclub.com"));
          message.addRecipient(Message.RecipientType.TO, new InternetAddress("mbrewer@shadycanyongolfclub.com"));
          message.addRecipient(Message.RecipientType.TO, new InternetAddress("klifshin@shadycanyongolfclub.com"));
          message.addRecipient(Message.RecipientType.TO, new InternetAddress("kswoish@shadycanyongolfclub.com"));
          message.addRecipient(Message.RecipientType.TO, new InternetAddress("lstone@shadycanyongolfclub.com"));
          message.addRecipient(Message.RecipientType.TO, new InternetAddress("cdeans@shadycanyongolfclub.com"));

      } else {
          message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
          
          if (!to2.equals("")) {
              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to2));
          }
          if (!to3.equals("")) {
              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to3));
          }
          if (!to4.equals("")) {
              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to4));
          }
      }
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

      if (!notes.equals( "" ) || club.equals("shadycanyongolfclub")) {
      
         enewMsg = enewMsg + "\n\nNotes: " + notes;        // add notes
      }

      enewMsg = enewMsg + customTrailer;

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

   if (emailCan != 0) {        // if Canceled tee time

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

      if (club.equals("shadycanyongolfclub")) {

         ecanMsg = ecanMsg + "\n\nNotes: " + notes;        // add notes
      }

      ecanMsg = ecanMsg + customTrailer;

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

      if (!notes.equals( "" ) || club.equals("shadycanyongolfclub")) {

         emodMsg = emodMsg + "\n\nNotes: " + notes;        // add notes
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

      emodMsg = emodMsg + customTrailer;

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

   can = "The following 'Non Local Guest' tee time has been CANCELED by " + author + ".\n\n";
   String subjectCan = "ForeTees - Non Local Guest Tee Time has been canceled.";


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

   } else if (parms.type.equals("Omaha")) {

      to = "omahacc@omahacc.org";

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
   String to = "ChrisH@clubmediterra.com";              // to address


   String msg = "\nMember " +player+ " (member number " + mnum + ") has used their 6 allowed rounds.\n\n";
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

 /**
  * Overloaded method call to allow calling sendAlertEmail with a single address and no ArrayList. Funnels into main method call.
  * @param toAddress Address to send alert to
  * @param subject Email subject
  * @param msg Email message body
  */
/* 
 public static void sendAlertEmail(String toAddress, String subject, String msg) {
     
     ArrayList<String> toAddresses = new ArrayList<String>();
     
     toAddresses.add(toAddress);
     
     sendAlertEmail(toAddresses, subject, msg);
 }
  */
 
 /**
  * Sends an alert email to the specified addresses using the passed subject and message for content. Intended to be used for automatic error and debug in-house notifications.
  * @param toAddresses ArrayList of addresses to send the email to.
  * @param subject Email subject
  * @param msg Email message body
  */
/* 
 public static void sendAlertEmail(ArrayList<String> toAddresses, String subject, String msg) {

   // Get the SMTP parmaters this club is using
   parmSMTP parm = new parmSMTP();

   Properties properties = new Properties();
   properties.put("mail.smtp.host", parm.SMTP_ADDR);                        // set outbound host address
   properties.put("mail.smtp.port", parm.SMTP_PORT);                        // set port address
   properties.put("mail.smtp.auth", (parm.SMTP_AUTH) ? "true" : "false");   // set 'use authentication'
   properties.put("mail.smtp.sendpartial", "true");                            // a message has some valid and some invalid addresses, send the message anyway

   //Session mailSess = Session.getInstance(properties, getAuthenticator());  // get session properties

   Session mailSess = Session.getInstance(properties, getAuthenticator(parm.SMTP_USER, parm.SMTP_PASS));

   MimeMessage message = new MimeMessage(mailSess);

   try {

      message.setFrom(new InternetAddress(efrom));                               // set from addr
      for (int i = 0; i < toAddresses.size(); i++) {
          message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddresses.get(i)));
      }
      message.setSentDate(new java.util.Date());                                // set date/time sent
      message.setSubject( subject );                                            // set subject line

   }
   catch (Exception e1) {
      errorMsg = "Error1 in sendEmail.sendAlertEmail setting properties - ERR: " + e1;
      Utilities.logError(errorMsg);                                       // log it
   }

   try {
      message.setText( msg );      // put msg in email text area

      Transport.send(message);     // send it!!

   }
   catch (Exception e2) {
      errorMsg = "Error2 in sendEmail.sendAlertEmail sending email - ERR: " + e2;
      Utilities.logError(errorMsg);                                       // log it
   }

 }  // end of sendMediterraEmail
 */
 

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
         
         message = ""; 

         /*              ***Custom removed at request of club 5/18/11***
          if (parms.course.equals("East Course")) {
              message = "\n\n\nThank you for reserving a guest time on the East course. The expected pace of play is 4 hours and 15 minutes, " +
                      "please remind your guest of this time on the first tee. The Golf Committee suggests you play ready golf and that " +
                      "your proper position on the golf course is directly behind the group in front of you. Enjoy your round!";
          } else if (parms.course.equals("West Course")) {
              message = "\n\n\nThank you for reserving a guest time on the West course. The expected pace of play is 3 hours and 45 minutes, " +
                      "please remind your guest of this time on the first tee. The Golf Committee suggests you play ready golf and that " +
                      "your proper position on the golf course is directly behind the group in front of you. Enjoy your round!";
          }
          */

      } else {     // Winged Foot
         
         message = "<p align=\"center\">Please forward this information to your guests prior to their arrival at the Club. " +
                   "<a href=\"http://web.foretees.com/"+rev+"/AEimages/wingedfoot/WF_Guest_Information.pdf\">Guest Information</a></p>";     
      }
   }   
         
   return(message);

 }  // end of checkCherryGuests

         

 /* // Removed at request of club. Vendor providing the service has moved.
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
*/
         
 

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

    String club = getClub.getClubName(con);         // get the club name
    
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

        Utilities.logError("sendEmail.checkCalPref: club="+club+", user="+user+", email="+email+", error=" + exc.getMessage());

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


 public static int hasGuests(parmEmail parms, String club) {
     
   int players = 0;  

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

   //
   //  Check for any guests - club name is here if we need to custom checks
   //
   for (int i=0; i<25; i++) {

      if (userA[i].equals("") && !playerA[i].equals("") && !playerA[i].equalsIgnoreCase("x")) {   // if NOT member and NOT X, must be a guest

         players = 1;      // at least one guest was found
         break;
      }
   }
   
   if (players > 0) {      // if any guests were found, check for members
       
       for (int i=0; i<25; i++) {

          if (!userA[i].equals("")) {   // if member

             players = 2;      // at least one guest and one member were found
             break;
          }
       }
   }

   return(players);

 }  // end of hasGuests


 public static int getContentID(parmEmail email, int location_id, Connection con) {

     int content_id = getContentID(email, location_id, con, false);

     if (content_id == -1) {

         con = Connect.ensureConnection(email.club, con);

         content_id = getContentID(email, location_id, con, false);

         // if still unable to perform lookup, log error
         if (content_id == -1) Utilities.logError("sendEmail.getContentID() email.type=" + email.type + ", club=" + email.club + ", CON DIED AND UNABLE TO RECONNECT, content_id=" + content_id);

     }

     return content_id;
 }

 /* never call this method directly */
 private static int getContentID(parmEmail email, int location_id, Connection con, boolean indirect_call) {

    int id = 0;

    String type_field = "";

    if (email.preview && email.force_content_id != 0 && email.force_content_location_id == location_id) {

        id = email.force_content_id;

    } else {

        type_field = getTypeField(email.type);

        // other types: password, frost, drequest

        if (!email.type.equals("password") && !email.type.equals("frost") && !type_field.equals("")) {

            ResultSet rs = null;
            PreparedStatement pstmt = null;

            //int has_guests = (hasGuests(email, email.club)) ? 1 : 0;
            //if (email.preview) has_guests = email.guests;
            
            int unaccomp_gsts = 0;
            int has_guests = hasGuests(email, email.club);    // determine who is in the res (0 = members only, 1 = guests only, 2 = members & guests)            
            
            if (has_guests == 1) {             // if only guests (unaccompanied)
                
                unaccomp_gsts = 1; 
                has_guests = 0;               // not members with guests
                
            } else if (has_guests == 2) {     // if guests and members
                
                has_guests = 1;
            }
            
            int date = (int)Utilities.getDate(con);
            int time = Utilities.getTime(con);

            if (email.preview && email.force_now_date != 0) date = email.force_now_date;
            if (email.preview && email.force_now_time != 0) time= email.force_now_time;

            String sql = "" +
                    "SELECT id " +
                            "FROM email_content " +
                            "WHERE " +
                                type_field + " = 1 AND ";

            if (email.type.startsWith("EmailTool")) {

                sql += "DATE_FORMAT(start_datetime, '%Y%m%d') <= ? AND DATE_FORMAT(end_datetime, '%Y%m%d') >= ? AND " +
                       "DATE_FORMAT(start_datetime,   '%k%i') <= ? AND DATE_FORMAT(end_datetime,   '%k%i') >= ? AND ";

            } else {

                sql += "((only_if_guests = 0 && only_if_unaccomp = 0) OR (only_if_guests = 1 AND " + has_guests + " = 1) OR (only_if_unaccomp = 1 AND " + unaccomp_gsts + " = 1)) AND " +
                        "(" +
                            "(" +
                            "time_mode = 0 AND " + // check against the reservation time
                            "DATE_FORMAT(start_datetime, '%Y%m%d') <= ? AND DATE_FORMAT(end_datetime, '%Y%m%d') >= ? AND " +
                            "DATE_FORMAT(start_datetime,   '%k%i') <= ? AND DATE_FORMAT(end_datetime,   '%k%i') >= ?" +
                            ") OR (" +
                            "time_mode = 1 AND " + // check against the club adjust current time
                            "DATE_FORMAT(start_datetime, '%Y%m%d') <= ? AND DATE_FORMAT(end_datetime, '%Y%m%d') >= ? AND " +
                            "DATE_FORMAT(start_datetime,   '%k%i') <= ? AND DATE_FORMAT(end_datetime,   '%k%i') >= ?" +
                            ")" +
                        ") AND ";

            }

            sql += "activity_id = ? AND location_id = ? AND enabled = 1 ";

            if (email.activity_id == 0 && !email.course.equals("")) {
                
                // find content for this course
                sql += "AND (courseName = '-ALL-' OR courseName = '' OR courseName = ?) ";
            }

            sql += "ORDER BY DATEDIFF(start_datetime, end_datetime) DESC";

            try {

                pstmt = con.prepareStatement ( sql );

                pstmt.clearParameters();

                if (email.type.startsWith("EmailTool")) {

                    pstmt.setInt(1, date);                      // club date right now
                    pstmt.setInt(2, date);
                    pstmt.setInt(3, time);                      // club time right now
                    pstmt.setInt(4, time);
                    pstmt.setInt(5, email.activity_id);
                    pstmt.setInt(6, location_id);
                    if (email.activity_id == 0 && !email.course.equals("")) pstmt.setString(7, email.course);

                } else {

                    pstmt.setInt(1, (int)email.date);           // date of reservation/signup
                    pstmt.setInt(2, (int)email.date);
                    pstmt.setInt(3, email.time);                // time of reservation/signup
                    pstmt.setInt(4, email.time);
                    pstmt.setInt(5, date);                      // club date right now
                    pstmt.setInt(6, date);
                    pstmt.setInt(7, time);                      // club time right now
                    pstmt.setInt(8, time);
                    pstmt.setInt(9, email.activity_id);
                    pstmt.setInt(10, location_id);
                    if (email.activity_id == 0 && !email.course.equals("")) pstmt.setString(11, email.course);
                }
                
                rs = pstmt.executeQuery();

                if (rs.next()) id = rs.getInt("id");

            } catch (Exception exc) {
/*
                if (exc.getMessage().equals("No operations allowed after connection closed.")) {



                }
  */
                //Utilities.logError("sendEmail.getContentID() email.type=" + email.type + ", club=" + email.club + ", error=" + exc.getMessage());

                id = -1; // indicate error

            } finally {

                try { rs.close(); }
                catch (Exception ignore) {}

                try { pstmt.close(); }
                catch (Exception ignore) {}

            }

        } // end skip frost & password
    
    }

    return id;

 }


 private static String getTypeField (String type) {


    String type_field = "";

    if (type.equals("tee") || type.equals("masstee") || type.equals("moveWhole") || type.equals("activity")) {
        type_field = "reservation";
    } else if (type.equals("lottery") || type.equals("lassign")) {
        type_field = "lottery_signup";
    } else if (type.equals("event")) {
        type_field = "event_signup";
    } else if (type.equals("waitlist")) {
        type_field = "event_signup";
    } else if (type.equals("lesson") || type.equals("lessongrp")) {
        type_field = "lesson_signup";
    } else if (type.equals("EmailToolMem")) {
        type_field = "email_tool_mem";
    } else if (type.equals("EmailToolPro")) {
        type_field = "email_tool_pro";
    } else if (type.equals("drequest")) {
        type_field = "";
    }

    return type_field;

 }


 public static String getContent(int email_content_id, Connection con, String club, String user) {


    // jump out right away if there is nothing to lookup (zero would indicate nothing was found and -1 indicates an error looking for content)
    if (email_content_id < 1) return "";


    ResultSet rs = null;
    PreparedStatement pstmt = null;

    String content = "";
    int no_guests = 0;

    try {

        pstmt = con.prepareStatement (
                    "SELECT content, no_guests " +
                    "FROM email_content " +
                    "WHERE id = ?");

        pstmt.clearParameters();
        pstmt.setInt(1, email_content_id);
        rs = pstmt.executeQuery();

        if (rs.next()) {

            content = rs.getString("content");
            no_guests = rs.getInt("no_guests");   
        }

    } catch (Exception exc) {

        Utilities.logError("sendEmail.getContent() id=" + email_content_id + ", error=" + exc.getMessage());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}
    }
    
    //
    //  if email is destined for a guest and the content is not intended for guests, then no content
    //
    if (!content.equals("") && no_guests > 0 && user.equals("")) {
       
        content = "";
    }
    
    
    if (!content.equals("") && !club.equals("") && !user.equals("")) {
       
       //
       //  content found and email destined for a member, look for an embedded link that
       //  needs the club's site name and the username so the member can link directly back to ForeTees
       //
       if (content.indexOf("##ELS##") > -1) {          // if link found that needs the club:username
          
          //String els = Utilities.getELS(club, user);   // get the ELS code (encrypetd) for this club and user
          
          content = content.replace("##ELS##", Utilities.getELS(club, user));
       }
    }

    return content;

 }

 public static String iCalFoldContentLine(String description) {

    String result = "";

    try {

        int len = 75; // MAX

        // return empty description for null text (this shouldn't happen)
        if (description == null) return result;

        // return description if less than length
        if (description.length() <= len) return description;

        Vector<String> lines = new Vector<String>();
        StringBuilder line = new StringBuilder();
        StringBuilder word = new StringBuilder();

        char [] chars = description.toCharArray();

        for (int i = 0; i < chars.length; i++) {

            word.append(chars[i]);

            if (chars[i] == ' ') {

                // determine if we should append to the current line or add new
                if ( ( line.length() + word.length() ) > len ) {

                    lines.add(line.toString());
                    line.delete(0, line.length());
                }

                line.append(word);
                word.delete(0, word.length());
            }
        }

        // handle any extra chars in current word
        if (word.length() > 0) {

            if ((line.length() + word.length()) > len) {

                lines.add(line.toString());
                line.delete(0, line.length());
            }

            line.append(word);
        }

        // handle extra line
        if (line.length() > 0) {

            lines.add(line.toString());
        }

        //String [] ret = new String[lines.size()];

        int c = 0; // counter
        for (Enumeration e = lines.elements(); e.hasMoreElements(); c++) {

            if (c == 0) {

                result += (String) e.nextElement() + "\\r\\n ";

            } else {

                // start each additional line with a space
                result += " " + (String) e.nextElement() + "\\r\\n";

            }
        }

    } catch (Exception exc) {

        Utilities.logError("sendEmail.iCalFold Err=" + exc.toString());

    }

    if (result.endsWith("\\r\\n")) result.substring(0, result.length() - 4);

    return result;

 }
 
    public static void sendFTNotification(ArrayList<String> toAddresses, String fromAddress, String replyTo, String subject, String msgBody, String club, Connection con) {

        String errorMsg = "";

        // Get the SMTP parmaters this club is using
        parmSMTP parm = new parmSMTP();

        try {
            getSMTP.getParms(con, parm);        // get the SMTP parms
        } catch (Exception ignore) {}

        Properties properties = new Properties();
        properties.put("mail.smtp.host", parm.SMTP_ADDR);                        // set outbound host address
        properties.put("mail.smtp.port", parm.SMTP_PORT);                        // set port address
        properties.put("mail.smtp.auth", (parm.SMTP_AUTH) ? "true" : "false");   // set 'use authentication'
        properties.put("mail.smtp.sendpartial", "true");                            // a message has some valid and some invalid addresses, send the message anyway

        Session mailSess;

        if (parm.SMTP_AUTH) {
            mailSess = Session.getInstance(properties, getAuthenticator(parm.SMTP_USER, parm.SMTP_PASS));
        } else {
            mailSess = Session.getInstance(properties, null);
        }
        
        MimetypesFileTypeMap mimetypes = (MimetypesFileTypeMap)MimetypesFileTypeMap.getDefaultFileTypeMap();
        mimetypes.addMimeTypes("text/calendar ics ICS");

        MailcapCommandMap mailcap = (MailcapCommandMap) MailcapCommandMap.getDefaultCommandMap();
        mailcap.addMailcap("text/calendar;; x-java-content-handler=com.sun.mail.handlers.text_plain");

        MimeMessage message = new MimeMessage(mailSess);

        try {

            if (!fromAddress.equals("")) {
                message.setFrom(new InternetAddress(fromAddress));
            } else {
                message.setFrom(new InternetAddress(parm.EMAIL_FROM));    // set from addr
            }
            
            for (int i = 0; i < toAddresses.size(); i++) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddresses.get(i)));
            }
            
            if (!replyTo.equals("")) {
                
                InternetAddress iareply[] = new InternetAddress[1];    // create replyto array
                iareply[0] = new InternetAddress(replyTo);
                message.setReplyTo(iareply);
            }
            
            message.setSentDate(new java.util.Date());    // set date/time sent

            message.setSubject(subject);    // set subject line

            // ADD PLAIN TEXT BODY
            BodyPart txtBodyPart = new MimeBodyPart();
            txtBodyPart.setContent("Plain Text Version", "text/plain");

            // ADD HTML BODY
            BodyPart htmlBodyPart = new MimeBodyPart();
            htmlBodyPart.setContent(msgBody, "text/html");


            Multipart mpRoot = new MimeMultipart("mixed");
            Multipart mpContent = new MimeMultipart("alternative");


            // Create a body part to house the multipart/alternative Part
            MimeBodyPart contentPartRoot = new MimeBodyPart();
            contentPartRoot.setContent(mpContent);

            // Add the root body part to the root multipart
            mpRoot.addBodyPart(contentPartRoot);

            mpContent.addBodyPart(txtBodyPart);
            mpContent.addBodyPart(htmlBodyPart);

            message.setContent(mpRoot);
            message.saveChanges();

        } catch (Exception e1) {
            Utilities.logError("sendEmail.sendFTNotification - " + club + " - Error setting email properties - ERR: " + e1.toString());
        }
        
        try {

            Transport.send(message);     // send it!!
            
        } catch (Exception e1) {
            Utilities.logError("sendEmail.sendFTNotification - " + club + " - Error sending email (Transport.send()) - ERR: " + e1.toString()); 
        }

    }
    
    public static void sendReminderNotifications(String club, Connection con) {

        Statement stmt = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;

        long cur_date = Utilities.getDate(con);
        long remind_sent = -1;
        int cur_time = Utilities.getTime(con);
        int organization_id = 0;

        boolean reservations = false;    // Global flags to be set so we know if at least one activity needs a certain type of reminder email
        boolean events = false;
        boolean lessons = false;

        boolean flxrez_res = false;    // Flags to be set so we know if at least one FlxRez activity needs a certain type of reminder email
        boolean flxrez_events = false;
        boolean flxrez_lessons = false;

        Map<Integer, String> activity_names = new HashMap<Integer, String>();    // Stores FlxRez root activity names so we only have to look them up once, as necessary.
        Map<Integer, Long> adv_res_email = new LinkedHashMap<Integer, Long>();    // Advance Dates for Reservations
        Map<Integer, Long> adv_events_email = new LinkedHashMap<Integer, Long>();    // Advance Dates for Events
        Map<Integer, Long> adv_lessons_email = new LinkedHashMap<Integer, Long>();    // Advance Dates for Lessons

        
        // Check the remind_sent value to see if reminders have already been sent today.
        try {
            
            stmt = con.createStatement();
            
            rs = stmt.executeQuery("SELECT remind_sent FROM club5");
            
            if (rs.next()) {
                remind_sent = rs.getLong("remind_sent");
            }
            
        } catch (Exception e) {
            Utilities.logError("SystemUtils.sendReminderNotifications - " + club + " - Error while setting club5.remind_sent - Error = " + e.toString());
            remind_sent = -1;    // We don't want to risk sending duplicate emails, so return if we failed to detect if they've already been sent today
        } finally {
            Connect.close(stmt);
        }
        

        if (remind_sent >= cur_date || remind_sent == -1) {       
            return;    // We've already sent reminders for this club today, or encountered a problem when looking up their most recent send date. Exit the processing right away.
        }
        
        // Look up adv_dates for all notification types and activities
        try {
            
            int activity_id = 0;
            
            boolean isFlxRezActId = false;
            boolean diningUsed = false;
            
            String type = "";
            
            stmt = con.createStatement();
            
            rs = stmt.executeQuery("SELECT activity_id, type, reservations, events, lessons FROM reminder_table ORDER BY type");
            
            while (rs.next()) {
                
                activity_id = rs.getInt("activity_id");
                type = rs.getString("type");    // Notification type
                
                isFlxRezActId = (activity_id != 0 && activity_id != dining_activity_id);
                
                if (!diningUsed && activity_id == dining_activity_id) {
                    diningUsed = true;
                }
                
                // Plug the adv_dates for this activity into the corresponding Maps, if not set to -1.
                if (rs.getInt("reservations") >= 0) {

                    if (type.equals("email")) {
                        adv_res_email.put(activity_id, Utilities.getDate(con, rs.getInt("reservations")));
                    }

                    reservations = true;    // Set the overall reservations flag, so we know that processing needs to be run

                    if (!flxrez_res && isFlxRezActId) {
                        flxrez_res = true;    // If FlxRez activity, set shortcut flag so we know at a glance if any FlxRez activities need notifications
                    }
                }

                if (rs.getInt("events") >= 0) {

                    if (type.equals("email")) {
                        adv_events_email.put(activity_id, Utilities.getDate(con, rs.getInt("events")));
                    }
                    
                    events = true;    // Set overall 'events' flag

                    if (!flxrez_events && isFlxRezActId) {
                        flxrez_events = true;
                    }
                }

                if (rs.getInt("lessons") >= 0) {

                    if (type.equals("email")) {
                        adv_lessons_email.put(activity_id, Utilities.getDate(con, rs.getInt("lessons")));
                    }
                    
                    lessons = true;    // Set overall 'lessons' flag

                    if (!flxrez_lessons && isFlxRezActId) {
                        flxrez_lessons = true;
                    }
                }
            }
            
            if (diningUsed) {
                organization_id = Utilities.getOrganizationId(con);
            }
            
        } catch (Exception e) {
            Utilities.logError("SystemUtils.sendReminderNotifications - " + club + " - Failed looking up reminder config data - Error = " + e.toString());
        } finally {
            Connect.close(rs, stmt);
        }
        
        // Set flag in club5 so we know the last day reminders were sent for this club.  Set this before we send reminders, in order to minimize the chance of sending duplicate reminders
        if (reservations || events || lessons) {
            try {

                stmt = con.createStatement();

                stmt.executeUpdate("UPDATE club5 SET remind_sent = DATE_FORMAT(now(), '%Y%m%d')");

            } catch (Exception e) {
                Utilities.logError("SystemUtils.sendReminderNotifications - " + club + " - Error while setting club5.remind_sent - Error = " + e.toString());
            } finally {
                Connect.close(stmt);
            }
        }
        

        // Send Reminder Emails for Standard Reservations (Tee Times/Court Bookings/Ala Carte Dining)
        if (reservations) {

            // Tee Times (teecurr2)
            if (adv_res_email.containsKey(0)) {

                try {

                    int guests = 0;

                    pstmt = con.prepareStatement("SELECT date, time, courseName, day, fb, mm, dd, yy, notes, event, event_type, "
                            + "player1, player2, player3, player4, player5, "
                            + "username1, username2, username3, username4, username5, "
                            + "p1cw, p2cw, p3cw, p4cw, p5cw, "
                            + "p91, p92, p93, p94, p95, "
                            + "userg1, userg2, userg3, userg4, userg5, "
                            + "guest_id1, guest_id2, guest_id3, guest_id4, guest_id5 "
                            + "FROM teecurr2 WHERE date = ? && player1 <> ''");
                    pstmt.clearParameters();
                    pstmt.setLong(1, adv_res_email.get(0));

                    rs = pstmt.executeQuery();

                    while (rs.next()) {

                        // If reminders are set to go out on the day of, don't bother sending if the reservation time has already passed.
                        if (adv_res_email.get(0) == 0 && rs.getInt("time") <= cur_time) {
                            continue;
                        }
                        
                        parmEmail parme = new parmEmail();          // allocate an Email parm block

                        parme.type = "tee";         // type = tee time
                        parme.activity_id = 0;
                        parme.club = club;
                        parme.user = "ftautoreminder";

                        parme.orig_by = "";    // We don't want to include the originator in the reminder emails, so don't bother passing it
                        parme.emailNew = 0;
                        parme.emailMod = 0;
                        parme.emailCan = 0;
                        parme.emailRem = 1;

                        parme.date = rs.getLong("date");
                        parme.time = rs.getInt("time");
                        parme.course = rs.getString("courseName");
                        parme.day = rs.getString("day");
                        parme.fb = rs.getInt("fb");
                        parme.mm = rs.getInt("mm");
                        parme.dd = rs.getInt("dd");
                        parme.yy = rs.getInt("yy");
                        parme.notes = rs.getString("notes");
                        parme.etype = 0;

                        //  If tee time is part of a shotgun event, then change the time and indicate its a shotgun
                        if (!rs.getString("event").equals("") && rs.getInt("event_type") == 1) {

                            parme.time = Utilities.getEventTime(rs.getString("event"), con);   // get the actual time of the shotgun
                            parme.etype = 1;       // indicate shotgun tee time
                        }

                        for (int i = 0; i < 5; i++) {
                            parme.setPlayer(rs.getString("player" + (i + 1)), i);
                            parme.setUser(rs.getString("username" + (i + 1)), i);
                            parme.setCw(rs.getString("p" + (i + 1) + "cw"), i);
                            parme.setP9(rs.getInt("p9" + (i + 1)), i);
                            parme.setUserg(rs.getString("userg" + (i + 1)), i);
                            parme.setGuestId(rs.getInt("guest_id" + (i + 1)), i);

                            parme.setOldPlayer("", i);
                            parme.setOldUser("", i);
                            parme.setOldCw("", i);
                            parme.setOldGuestId(0, i);
                        }

                        //  Send the email
                        sendIt(parme, con);      // in common
                    }

                } catch (Exception e) {
                    Utilities.logError("SystemUtils.sendReminderNotifications - " + club + " - Failed sending Golf Tee Time reminder emails - Error = " + e.toString());
                } finally {
                    Connect.close(rs, pstmt);
                }
            }

            if (flxrez_res) {

                try {
                    
                    String act_query = "";
                    String in_string = "";
                    
                    int activity_id = 0;
                    
                    Map<Integer, List<Integer>> activity_child_ids = new HashMap<Integer, List<Integer>>();
                    
                    for (Map.Entry<Integer, Long> entry : adv_res_email.entrySet()) {
                        
                        if (entry.getKey() != 0 && entry.getKey() != dining_activity_id) {
                            
                            activity_child_ids.put(entry.getKey(), getActivity.getAllChildrenForActivity(entry.getKey(), con));
                            
                            if (act_query.equals("")) {
                                act_query += "AND (";
                            } else {
                                act_query += " OR ";
                            }
                            
                            for (int child_id : activity_child_ids.get(entry.getKey())) {
                                in_string += "?,";
                        }
                            
                            if (!in_string.equals("")) {    // Only add this activity if child activities were found
                                
                                in_string = in_string.substring(0, in_string.length() - 1);    // Trim off excess comma
                                
                                act_query += "(ash.activity_id IN (" + in_string + ") AND date_time >= ? AND date_time <= ?)";
                            }
                        }
                    }
                    
                    // Only continue if at least one activity was specified
                    if (!act_query.equals("")) {
                        
                        act_query += ") ";    // Close off the end of the dynamic query component

                        pstmt = con.prepareStatement(
                                "SELECT sheet_id, ash.activity_id, activity_name, email_name, orig_by, notes, hideNotes, "
                                + "(count(sheet_id) * a.interval) AS 'length', "
                                + "DATE_FORMAT(date_time, '%Y%m%d') AS 'date', "
                                + "DATE_FORMAT(date_time, '%H%i') AS 'time', "
                                + "DATE_FORMAT(date_time, '%c') AS 'mm', "
                                + "DATE_FORMAT(date_time, '%e') AS 'dd', "
                                + "DATE_FORMAT(date_time, '%Y') AS 'yy', "
                                + "DATE_FORMAT(date_time, '%W') AS 'day' "
                                + "FROM activity_sheets ash "
                                + "INNER JOIN activities a ON a.activity_id = ash.activity_id "
                                + "WHERE related_ids <> '' "
                                + act_query 
                                + "GROUP BY related_ids "
                                + "ORDER BY ash.activity_id, date_time");

                        int ind = 1;
                        
                        pstmt.clearParameters();

                        for (Map.Entry<Integer, Long> entry : adv_res_email.entrySet()) {
                            
                            if (entry.getKey() != 0 && entry.getKey() != dining_activity_id) {
                                
                                for (int child_id : activity_child_ids.get(entry.getKey())) {
                                    pstmt.setInt(ind++, child_id);
                                }
                                
                                pstmt.setString(ind++, Utilities.get_mysql_timestamp(entry.getValue().intValue(), 1));
                                pstmt.setString(ind++, Utilities.get_mysql_timestamp(entry.getValue().intValue(), 2359));
                            }
                        }

                        rs = pstmt.executeQuery();

                        while (rs.next()) {

                            activity_id = getActivity.getRootIdFromActivityId(rs.getInt("ash.activity_id"), con);
                            
                            // If reminders are set to go out on the day of, don't bother sending if the reservation time has already passed.
                            if (adv_res_email.get(activity_id) == 0 && rs.getInt("time") <= cur_time) {
                                continue;
                            }
                            
                            parmEmail parme = new parmEmail();          // allocate an Email parm block

                            parme.oldplayer1 = "";    // Not needed since this is occurring outside of a reservation booking.
                            parme.oldplayer2 = "";
                            parme.oldplayer3 = "";
                            parme.oldplayer4 = "";
                            parme.oldplayer5 = "";

                            parme.olduser1 = "";    // Not needed since this is occurring outside of a reservation booking.
                            parme.olduser2 = "";
                            parme.olduser3 = "";
                            parme.olduser4 = "";
                            parme.olduser5 = "";

                            parme.player1 = "";    // Initialize these in case we don't fill all 5 slots when looking up players.
                            parme.player2 = "";
                            parme.player3 = "";
                            parme.player4 = "";
                            parme.player5 = "";

                            parme.user1 = "";    // Initialize these in case we don't fill all 5 slots when looking up players.
                            parme.user2 = "";
                            parme.user3 = "";
                            parme.user4 = "";
                            parme.user5 = "";

                            if (!activity_names.containsKey(activity_id)) {
                                activity_names.put(activity_id, getActivity.getRootNameFromActivityId(activity_id, con));
                            }

                            try {

                                pstmt2 = con.prepareStatement(
                                        "SELECT * "
                                        + "FROM activity_sheets_players asp "
                                        + "LEFT JOIN member2b m ON m.username = asp.username "
                                        + "WHERE activity_sheet_id = ? "
                                        + "ORDER BY pos");

                                pstmt2.clearParameters();
                                pstmt2.setInt(1, rs.getInt("sheet_id"));

                                rs2 = pstmt2.executeQuery();

                                while (rs2.next()) {

                                    switch (rs2.getInt("pos")) {

                                        case 1:
                                            parme.player1 = rs2.getString("player_name");
                                            parme.user1 = rs2.getString("username");
                                            break;
                                        case 2:
                                            parme.player2 = rs2.getString("player_name");
                                            parme.user2 = rs2.getString("username");
                                            break;
                                        case 3:
                                            parme.player3 = rs2.getString("player_name");
                                            parme.user3 = rs2.getString("username");
                                            break;
                                        case 4:
                                            parme.player4 = rs2.getString("player_name");
                                            parme.user4 = rs2.getString("username");
                                            break;
                                        case 5:
                                            parme.player5 = rs2.getString("player_name");
                                            parme.user5 = rs2.getString("username");
                                            break;
                                    }
                                }

                            } catch (Exception e) {

                            } finally {
                                Connect.close(rs2, pstmt2);
                            }

                            //  Set the values in the email parm block
                            parme.activity_id = getActivity.getRootIdFromActivityId(activity_id, con);
                            parme.club = club;
    //                     parme.guests = slotParms.guests;    // Doesn't appear to be used anywhere in email code
                            parme.type = "activity";         // type = time slot
                            parme.activity_name = activity_names.get(activity_id);
                            parme.actual_activity_name = rs.getString("activity_name");
                            parme.activity_email_name = rs.getString("email_name");

                            parme.date = rs.getLong("date");
                            parme.time = rs.getInt("time");
                            parme.mm = rs.getInt("mm");
                            parme.dd = rs.getInt("dd");
                            parme.yy = rs.getInt("yy");
                            parme.day = rs.getString("day");
                            parme.notes = rs.getString("notes");
                            parme.hideNotes = rs.getInt("hideNotes"); //Integer.parseInt(slotParms.hides);

                            parme.user = "ftautoreminder";
                            parme.emailNew = 0;
                            parme.emailMod = 0;
                            parme.emailCan = 0;
                            parme.emailRem = 1;

                            //  Send the email
                            sendIt(parme, con);      // in common
                        }
                    }
                } catch (Exception e) {
                    Utilities.logError("SystemUtils.sendReminderNotifications - " + club + " - Failed sending FlxRez Reservation reminder emails - Error = " + e.toString());
                } finally {
                    Connect.close(rs, pstmt);
                }
            }

            // Dining Reservations (dining system)
            if (adv_res_email.containsKey(dining_activity_id) && organization_id > 0) {
                
                Connection con_d = null;
                
                try {
                    
                    con_d = Connect.getDiningCon();
                    
                    if (con_d != null) {

                        // Get dining ala carte reservation data and send emails
                        pstmt = con_d.prepareStatement(
                                  "SELECT reservation_number, to_char(date, 'FMDay, FMMonth FMDD, YYYY') display_date, to_char(time, 'FMHH:MI AM') display_time, l.name AS loc_name, count(*) reservee_count, "
                                + " array_to_string(array_agg(reservee_name), ';') reservee_names, "
                                + " array_to_string(array_agg(ltrim(split_part(p.user_identity, ':', 1), ' ')), ';') reservee_usernames "
                                + "FROM (SELECT * FROM reservations ORDER BY id DESC) r "
                                + "INNER JOIN locations l ON r.location_id = l.id "
                                + "LEFT JOIN people p ON r.person_id = p.id "
                                + "WHERE category = 'dining' AND state NOT IN ('cancelled', 'waitlisted') AND r.organization_id = ? AND to_char(date, 'YYYYMMDD')::int = ? "
                                + "GROUP BY reservation_number, display_date, display_time, l.name;");
                        
                        pstmt.clearParameters();
                        pstmt.setInt(1, organization_id);
                        pstmt.setInt(2, adv_res_email.get(dining_activity_id).intValue());
                        
                        rs = pstmt.executeQuery();
                        
                        while (rs.next()) {
                            
                            parmEmail parme = new parmEmail();          // allocate an Email parm block
                            
                            parme.type = "diningrem";
                            parme.activity_id = 9999;
                            parme.dining_resNum = rs.getInt("reservation_number");    // Reservation Number
                            parme.dining_disp_date = rs.getString("display_date");    // Date - Pretty format
                            parme.dining_disp_time = rs.getString("display_time");    // Time - Pretty format
                            parme.course = rs.getString("loc_name");                  // Location name
                            parme.guests = rs.getInt("reservee_count");               // Number of reservees in reservation
                            
                            String[] reservee_names = rs.getString("reservee_names").split(";");
                            String[] reservee_usernames = rs.getString("reservee_usernames").split(";");
                            
                            parme.dining_names = Arrays.asList(reservee_names);
                            parme.dining_users = Arrays.asList(reservee_usernames);
                            
                            sendIt(parme, con);
                        }
                    }
                 
                } catch (Exception e) {
                    Utilities.logError("SystemUtils.sendReminderNotifications - " + club + " - Failed sending Dining a la carte reservation reminder emails - Error = " + e.toString());
                } finally {
                    Connect.close(rs, pstmt, con_d);
                }
            }
        }

        // Send Reminder Emails for Event Signups
        if (events) {

            // ForeTees & FlxRez Events (evntsup2b)
            if (adv_events_email.containsKey(0) || flxrez_events) {

                String activity_name = "";
                String act_query = "";
                String act_ampm = "";
                String act_time = "";
                
                int act_hr = 0;
                int act_min = 0;
                int act_time_int = 0;
                int activity_id = 0;

                try {
                    
                    for (Map.Entry<Integer, Long> entry : adv_events_email.entrySet()) {
                        
                        if (entry.getKey() != dining_activity_id) {
                            
                            if (act_query.equals("")) {
                                act_query += "AND (";
                            } else {
                                act_query += " OR ";
                            }
                            
                            act_query += "(activity_id = ? AND date = ?)";
                        }
                    }

                    if (!act_query.equals("")) {
                        
                        act_query += ")";    // Add closing paren for query component
                        
                        pstmt = con.prepareStatement(
                                "SELECT e.name, activity_id, e.date, month, day, year, act_hr, act_min, e.courseName, type, "
                                + "player1, player2, player3, player4, player5, "
                                + "username1, username2, username3, username4, username5, "
                                + "p1cw, p2cw, p3cw, p4cw, p5cw, "
                                + "userg1, userg2, userg3, userg4, userg5, "
                                + "guest_id1, guest_id2, guest_id3, guest_id4, guest_id5 "
                                + "FROM evntsup2b esup "
                                + "INNER JOIN events2b e ON esup.event_id = e.event_id "
                                + "WHERE season = 0 AND wait = 0 AND e.inactive = 0 AND esup.inactive = 0 AND player1 <> '' "
                                + act_query);
                        
                        int ind = 1;
                        
                        pstmt.clearParameters();

                        for (Map.Entry<Integer, Long> entry : adv_events_email.entrySet()) {
                            
                            if (entry.getKey() != dining_activity_id) {
                                pstmt.setInt(ind++, entry.getKey());
                                pstmt.setLong(ind++, entry.getValue());
                            }
                        }

                        rs = pstmt.executeQuery();

                        while (rs.next()) {

                            activity_name = "";
                            activity_id = rs.getInt("activity_id");

                            try {
                                activity_name = getActivity.getActivityName(rs.getInt("activity_id"), con);
                            } catch (Exception ignore) {
                            }

                            act_hr = rs.getInt("act_hr");
                            act_min = rs.getInt("act_min");
                            act_time = "";
                            act_ampm = " AM";
                            
                            act_time_int = (act_hr * 100) + act_min;

                            if (act_hr == 0) {
                                act_hr = 12;    // change to 12 AM (midnight)
                            } else if (act_hr == 12) {
                                act_ampm = " PM";    // change to Noon
                            }

                            if (act_hr > 12) {
                                act_hr = act_hr - 12;
                                act_ampm = " PM";    // change to 12 hr clock
                            }

                            //  convert time to hour and minutes for email msg
                            act_time = act_hr + ":" + Utilities.ensureDoubleDigit(act_min) + act_ampm;
                            
                            // If reminders are set to go out on the day of, don't bother sending if the event start time has already passed.
                            if (adv_events_email.get(activity_id) == 0 && act_time_int <= cur_time) {
                                continue;
                            }

                            parmEmail parme = new parmEmail();          // allocate an Email parm block

                            //  Set the values in the email parm block
                            parme.activity_id = rs.getInt("activity_id");
                            parme.club = club;
                            parme.activity_name = activity_name;
                            parme.actual_activity_name = activity_name;

                            parme.type = "event";         // type = event
                            parme.date = rs.getLong("e.date");
                            parme.time = 0;
                            parme.fb = 0;
                            parme.mm = rs.getInt("month");
                            parme.dd = rs.getInt("day");
                            parme.yy = rs.getInt("year");
                            parme.season = 0;    // No reminder emails for season long events, since they don't have a specific date associated with them

                            parme.name = rs.getString("e.name");
                            parme.etype = rs.getInt("type");
                            parme.act_time = act_time;
                            parme.wait = 0;
                            parme.checkWait = 0;

                            parme.user = "ftautoreminder";
                            parme.emailNew = 0;
                            parme.emailMod = 0;
                            parme.emailCan = 0;
                            parme.emailRem = 1;

                            parme.course = rs.getString("e.courseName");
                            parme.day = "";

                            for (int i = 0; i < 5; i++) {
                                parme.setPlayer(rs.getString("player" + (i + 1)), i);
                                parme.setUser(rs.getString("username" + (i + 1)), i);
                                parme.setCw(rs.getString("p" + (i + 1) + "cw"), i);
                                parme.setUserg(rs.getString("userg" + (i + 1)), i);
                                parme.setGuestId(rs.getInt("guest_id" + (i + 1)), i);

                                parme.setOldPlayer("", i);
                                parme.setOldUser("", i);
                                parme.setOldCw("", i);
                                parme.setOldGuestId(0, i);
                                parme.setP9(0, i);    // Doesn't matter for event
                            }

                            //  Send the email
                            sendIt(parme, con);      // in common
                        }
                    }
                } catch (Exception e) {
                    Utilities.logError("SystemUtils.sendReminderNotifications - " + club + " - Failed sending ForeTees/FlxRez event reminder emails - Error = " + e.toString() + ", pstmt: " + pstmt.toString());
                } finally {
                    Connect.close(rs, pstmt);
                }
            }


            // Dining Events (dining system)
            if (adv_events_email.containsKey(dining_activity_id)) {
                
                Connection con_d = null;
                
                try {
                
                    con_d = Connect.getDiningCon();
                    
                    if (con_d != null) {

                        // Get dining ala carte reservation data and send emails
                        pstmt = con_d.prepareStatement(
                                "SELECT reservation_number, to_char(r.date, 'FMDay, FMMonth FMDD, YYYY') display_date, to_char(r.time, 'FMHH:MI AM') display_time, l.name AS loc_name, count(*) reservee_count, "
                                + " e.name as event_name, d.name AS dress_code, e.cancellation_hours as can_hrs, "
                                + " array_to_string(array_agg(reservee_name), ';') reservee_names, "
                                + " array_to_string(array_agg(ltrim(split_part(p.user_identity, ':', 1), ' ')), ';') reservee_usernames "
                                + "FROM (SELECT * FROM reservations ORDER BY id) r "
                                + "INNER JOIN locations l ON r.location_id = l.id "
                                + "INNER JOIN events e ON e.id = r.event_id "
                                + "INNER JOIN dress_codes d ON d.id = e.dress_code_id "
                                + "LEFT JOIN people p ON r.person_id = p.id "
                                + "WHERE category = 'event' AND r.state NOT IN ('cancelled', 'waitlisted') AND r.organization_id = ? AND to_char(r.date, 'YYYYMMDD')::int = ? "
                                + "GROUP BY reservation_number, display_date, display_time, l.name, e.name, d.name, e.cancellation_hours;");
                        
                        pstmt.clearParameters();
                        pstmt.setInt(1, organization_id);
                        pstmt.setInt(2, adv_events_email.get(dining_activity_id).intValue());
                        
                        rs = pstmt.executeQuery();
                        
                        while (rs.next()) {
                            
                            parmEmail parme = new parmEmail();          // allocate an Email parm block
                            
                            parme.type = "diningeventrem";
                            parme.activity_id = 9999;
                            parme.name = rs.getString("event_name");                  // Name of event
                            parme.dining_resNum = rs.getInt("reservation_number");    // Reservation Number
                            parme.dining_disp_date = rs.getString("display_date");    // Date - Pretty format
                            parme.dining_disp_time = rs.getString("display_time");    // Time - Pretty format
                            parme.course = rs.getString("loc_name");                  // Location name
                            parme.guests = rs.getInt("reservee_count");               // Number of reservees in reservation
                            parme.notes = rs.getString("dress_code");                     // Dress Code policy description
                            parme.to_time = rs.getInt("can_hrs");        // Number hours in advance they can cancel
                            
                            String[] reservee_names = rs.getString("reservee_names").split(";");
                            String[] reservee_usernames = rs.getString("reservee_usernames").split(";");
                            
                            parme.dining_names = Arrays.asList(reservee_names);
                            parme.dining_users = Arrays.asList(reservee_usernames);
                            
                            sendIt(parme, con);
                        }
                    }
                 
                } catch (Exception e) {
                    Utilities.logError("SystemUtils.sendReminderNotifications - " + club + " - Failed sending Dining event reminder emails - Error = " + e.toString());
                } finally {
                    Connect.close(rs, pstmt, con_d);
                }
            }
        }

        // Send Reminder Emails for Lesson Bookings (ForeTees & FlxRez Only!)
        if (lessons) {

            if (adv_lessons_email.containsKey(0) || flxrez_lessons) {

                // Individual Lessons (lessonbook5)
                try {
                    
                    String act_query = "";

                    int mm = 0;
                    int dd = 0;
                    int yy = 0;
                    int temp = 0;
                    int activity_id = 0;
                    
                    for (Map.Entry<Integer, Long> entry : adv_lessons_email.entrySet()) {
                        
                        if (entry.getKey() != dining_activity_id) {
                            
                            if (act_query.equals("")) {
                                act_query += "AND (";
                            } else {
                                act_query += " OR ";
                            }
                            
                            act_query += "(l.activity_id = ? AND date = ?)";
                        }
                    }

                    if (!act_query.equals("")) {
                        
                        act_query += ")";    // Add closing paren for query component
                        
                        pstmt = con.prepareStatement(
                                "SELECT l.activity_id, proid, date, time, ltype, memname, memid, notes, "
                                + "DATE_FORMAT(DATE, '%W') AS dayName, IF(ISNULL(activity_name), '', activity_name) AS 'activity_name' "
                                + "FROM lessonbook5 l "
                                + "LEFT JOIN activities a ON l.sheet_activity_id = a.activity_id "
                                + "INNER JOIN lessonpro5 lp ON lp.id = l.proid AND lp.active = 1 "
                                + "WHERE memname <> '' AND num = 1 "
                                + act_query);

                        int ind = 1;

                        pstmt.clearParameters();

                        for (Map.Entry<Integer, Long> entry : adv_lessons_email.entrySet()) {

                            if (entry.getKey() != dining_activity_id) {
                                pstmt.setInt(ind++, entry.getKey());
                                pstmt.setLong(ind++, entry.getValue());
                            }
                        }

                        rs = pstmt.executeQuery();

                        while (rs.next()) {

                            mm = 0;
                            dd = 0;
                            yy = 0;
                            temp = 0;
                            activity_id = rs.getInt("l.activity_id");

                            if (!activity_names.containsKey(activity_id)) {
                                activity_names.put(activity_id, getActivity.getRootNameFromActivityId(activity_id, con));
                            }

                            // If reminders are set to go out on the day of, don't bother sending if the lesson time has already passed.
                            if (adv_lessons_email.get(activity_id) == 0 && rs.getInt("time") <= cur_time) {
                                continue;
                            }

                            parmEmail parme = new parmEmail();    // allocate an Email parm block

                            parme.date = rs.getLong("date");

                            yy = (int)parme.date / 10000;
                            temp = yy * 10000;
                            mm = (int)parme.date - temp;
                            temp = mm / 100;
                            temp = temp * 100;
                            dd = mm - temp;
                            mm = mm / 100;

                            // Set the values in the email parm block
                            parme.activity_id = activity_id;
                            parme.club = club;
                            parme.guests = 0;
                            parme.type = "lesson";    // type = lesson time
                            parme.time = rs.getInt("time");
                            parme.fb = rs.getInt("proid");    // pro id
                            parme.mm = mm;
                            parme.dd = dd;
                            parme.yy = yy;
                            parme.emailNew = 0;
                            parme.emailMod = 0;
                            parme.emailCan = 0;
                            parme.emailRem = 1;
                            parme.day = rs.getString("dayName");
                            parme.user = "ftautoreminder";    // from this user
                            parme.user1 = rs.getString("memid");    // username of member
                            parme.player1 = rs.getString("memname");    // name of member
                            parme.actual_activity_name = rs.getString("activity_name");    // this is the 'court' level name

                            if (club.equalsIgnoreCase("colletonriverclub")) {

                                parme.notes = rs.getString("notes");    // lesson notes
                                parme.hideNotes = 0;    //don't hide notes
                            }

                            parme.activity_name = activity_names.get(activity_id);    // root activity name

                            parme.course = rs.getString("ltype");                      // put lesson type in course field

                            //  Send the email
                            sendEmail.sendIt(parme, con);                 // in common
                        }
                    }

                } catch (Exception e) {
                    Utilities.logError("SystemUtils.sendReminderNotifications - " + club + " - Failed sending ForeTees/FlxRez Individual Lesson reminder emails - Error = " + e.toString() + ", pstmt: " + pstmt.toString());
                } finally {
                    Connect.close(rs, pstmt);
                }
            }

            if (adv_lessons_email.containsKey(0) || flxrez_lessons) {

                // Group Lessons / Clinics (lgrpsignup5)
                try {
                    
                    String act_query = "";

                    int activity_id = 0;

                    String day_name = "";

                    for (Map.Entry<Integer, Long> entry : adv_lessons_email.entrySet()) {
                        
                        day_name = Utilities.getDayNameFromDate(entry.getValue().intValue());
                        
                        if (entry.getKey() != dining_activity_id) {
                            
                            if (!act_query.equals("")) {
                                act_query += " OR ";
                            } else {
                                act_query += "(";
                            }
                            
                            act_query += "(lg.activity_id = ? AND ((clinic = 0 AND lgs.date = ?) OR (clinic = 1 AND ? >= lg.date AND ? <= lg.edate AND " + day_name.toLowerCase() + " = 1)))";
                        }
                    }
                    
                    if (!act_query.equals("")) {
                    
                        act_query += ")";    // Add closing paren for query component
                        
                        pstmt = con.prepareStatement(
                                "SELECT lg.activity_id, lg.proid, stime, memname, memid, lg.lname "
                                + "FROM lgrpsignup5 lgs "
                                + "INNER JOIN lessongrp5 lg ON lgs.lesson_id = lg.lesson_id "
                                + "INNER JOIN lessonpro5 lp ON lp.id = lg.proid AND lp.active = 1 "
                                + "WHERE " + act_query);

                        int ind = 1;

                        pstmt.clearParameters();

                        for (Map.Entry<Integer, Long> entry : adv_lessons_email.entrySet()) {
                            
                            if (entry.getKey() != dining_activity_id) {
                                pstmt.setInt(ind++, entry.getKey());
                                pstmt.setLong(ind++, entry.getValue());
                                pstmt.setLong(ind++, entry.getValue());
                                pstmt.setLong(ind++, entry.getValue());
                            }
                        }

                        rs = pstmt.executeQuery();

                        while (rs.next()) {

                            activity_id = rs.getInt("lg.activity_id");

                            if (!activity_names.containsKey(activity_id)) {
                                activity_names.put(activity_id, getActivity.getRootNameFromActivityId(activity_id, con));
                            }

                            // If reminders are set to go out on the day of, don't bother sending if the clinic start time has already passed.
                            if (adv_lessons_email.get(activity_id) == 0 && rs.getInt("stime") <= cur_time) {
                                continue;
                            }

                            int yy = adv_lessons_email.get(activity_id).intValue() / 10000;
                            int temp = yy * 10000;
                            int mm = adv_lessons_email.get(activity_id).intValue() - temp;
                            temp = mm / 100;
                            temp = temp * 100;
                            int dd = mm - temp;
                            mm = mm / 100;

                            //  allocate a parm block to hold the email parms
                            parmEmail parme = new parmEmail();    // allocate an Email parm block

                            //  Set the values in the email parm block
                            parme.date = adv_lessons_email.get(activity_id);
                            parme.time = rs.getInt("stime");
                            parme.fb = rs.getInt("lg.proid");    // pro id
                            parme.mm = mm;
                            parme.dd = dd;
                            parme.yy = yy;
                            parme.emailNew = 0;
                            parme.emailMod = 0;
                            parme.emailCan = 0;
                            parme.emailRem = 1;
                            parme.day = day_name;

                            parme.type = "lessongrp";    // type = group lesson time
                            parme.user = "ftautoreminder";    // from user
                            parme.user1 = rs.getString("memid");    // username of member
                            parme.player1 = rs.getString("memname");    // name of member
                            parme.course = rs.getString("lg.lname");    // put group lesson name in course field
                            parme.activity_name = activity_names.get(activity_id);    // root activity name

                            //  Send the email
                            sendEmail.sendIt(parme, con);      // in common
                        }
                    }
                } catch (Exception e) {
                    Utilities.logError("SystemUtils.sendReminderNotifications - " + club + " - Failed sending ForeTees/FlxRez Group Lesson/Clinic reminder emails - Error = " + e.toString());
                } finally {
                    Connect.close(rs, pstmt);
                }
            }
        }
    }
}  // end of sendEmail class
