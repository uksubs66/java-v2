/***************************************************************************************
 *   SYSTEMUTILS:  This servlet will provide some common utilites for other servlets.
 *
 *   utilities:  HeadTitle (creates a HTML header and title)
 *               Connect (gets a connection to the DB)
 *               getCon (gets a db connection)
 *               verifyMem (verify the user - prevent unauthorized access)
 *               verifyPro (verify the user - prevent unauthorized access)
 *               verifyAdmin (verify the user - prevent unauthorized access)
 *               verifyHotel (verify the user - prevent unauthorized access)
 *               buildTee (builds a tee sheet for one day into teecurr2 table)
 *               buildHTee (builds a custom tee sheet for Hazeltine National)
 *               moveTee (moves oldest teesheet from teecurr2 to teepast2)
 *               scanTee & teeTimer (scans teecurr2 for missing days and builds them if needed)
 *               buildDblTee (builds double tees into the tee sheets when a Double Tee has been added or changed)
 *               removeDblTee (removes double tees from the tee sheets when a Double Tee has been changed)
 *               filter (filters strings to replace special characters for HTML display)
 *               unfilter (changes special characters back for jump code)
 *               scanName (scans a name for special characters)
 *               updateTeecurr (scans block, event & restriction tables and updates teecurr2 if necessary)
 *               inactTimer (processes 2 minute system timer expiration - check teecurr2 for in-use entries)
 *               inactTimer2 (processes 2 minute system timer expiration - check customs, etc)
 *               xTimer (processes 60 minute system timer expiration - check teecurr2 for X's)
 *               tsheetTimer (sends current tee sheet to all Pro Shops 2 times per day)
 *               getLottId (get the next sequential lottery id from club table)
 *               processLott (process a lottery when inactTimer detects its time)
 *               moveReqs (move lottery requests from lreqs to teecurr after times have been assigned)
 *               adjustTime (adjust the time for club's time zone)
 *               adjustTimeBack (adjust the time Back for club's time zone - opposite of adjustTime)
 *               newGuest (correct the stats table after Guest Types are changed)
 *               newMem (correct the stats table after Member Types are changed)
 *               newMship (correct the stats table after Membership Types are changed)
 *               newTmode (correct the stats table after any Modes of Transportation are changed)
 *               insertTee (insert a new tee time - Proshop_dsheet)
 *               insertTee2 (insert a new tee time - Proshop_evntChkAll)
 *               getDate (get today's date)
 *               getTime (get current time - adjusted for time zone)
 *               logError (log system errors to a text file)
 *               logErrorToFile (logs roster sync errors to rsync-log.txt file)
 *               getStackAsString (get the stack trace from an exception and return it in String)
 *               getClubParmIdFromTeeCurrID(returns the uid for a course from a given tee time uid)
 *               getClubParmIdFromCourseName(returns the uid for a course from a given course name)
 *               scrubString(returns trimmed and ? version of the passed string)
 *               getIndexFromToday(accepts long date and returns index from today)
 *               getClubName(accepts con object and returns the full club name from club5 table)
 *               buildCustomTees(builds custom tee sheets for a specific day)
 *               isWaitListTime(returns the uid of a wait list covering a specific date/time/course)
 *               getIntTime (accepts time parts from user input and returns 24hr time as an int)
 *               getLongDateTime (accepts date, time and seperator and returns (Thu Jul 10th, 2008 at 10:23 AM)
 *
 *
 *   created: 11/20/2001   Bob P.
 *
 *   last updated:
 *
 *        2/14/14   Sea Cliff CC (seacliffcc) - Removed the custom to set pre-checkin values at midnight (case 1758).
 *        1/29/14   Castle Pines CC (castlepines) - Updated xCustomTimer to now release X's 2 days in advance at 12:00pm for all days (case 2003).
 *        1/23/14   Manor CC (manorcc) - Include all proshop menus on mobile devices (iPads).
 *        1/16/14   Updated restriction release custom to also release the "Practice Hole Policy" restriction along with their WALK IN TIMES restrictions.
 *        1/13/14   Added support for the 'supportpro' limited user on the support side.
 *       11/21/13   Add counters to errlog entry when scanTee completes.
 *       10/22/13   Updated updateHist method to accommodate a value for the Make Tee Time Private option being passed.
 *       10/03/13   Oak Hill CC (oakhillcc) - Added custom to send a reminder email to members when they have a tee time 7 days out (case 2308).
 *        9/24/13   Macgregor Downs CC (macgregordowns) - Added custom to release the "Friday Member Only 12 to 150PM" guest restriction 2 days in advance at 12:00 PM ET (case 2248).
 *        9/20/13   The CC of Brookline (tcclub) - include all the proshop menus on mobile devices (iPads).
 *        9/09/13   Add Plantation G & CC to list of clubs using the new Common_Lott processing.
 *        9/06/13   The Concession - send out an extra backup teesheet at 7pm est - added hook to xtimer
 *        8/29/13   Add date limit on call to buildOlyPOS for the Olympic Club.  They are switching from CSG to NS and will not need this timer function.
 *        7/23/13   Add Estero CC to list of clubs using the new Common_Lott processing.
 *        7/12/13   Added overloaded method call for updateHist so an "email_suppressed" parameter can be included (defaults to 0 if not included). This parameter indicates whether emails were suppressed.
 *        6/26/13   Atlanta CC (atlatnacountryclub) - Fixed issue with auto-generate charge custom that was causing it to not run for them.
 *        6/21/13   Atlanta CC (atlantacountryclub) - Added to custom to generate and send POS charges for Northstar at 9PM Central (redundant run at 9:25pm).
 *        5/29/13   Dove Canyon Club (dovecanyonclub) - Updated custom to release restrictions starting with "TPC" as well as "PLI" 3 days in advance at 6am CT (case 2258).
 *        5/23/13   Dataw Island Club (dataw) - Added to new lottery processing (time priority instead of course priority).
 *        5/13/13   Add the Gallery (gallerygolf) to the list of clubs that use the new lottery process in Common_Lott.
 *        5/10/13   Added releaseRestCommonCustom() method to allow easy releasing of all member type restrictions that start with a common string of characters. (see previous update for actual code changes).
 *        5/10/13   Dove Canyon Club (dovecanyonclub) - Added custom to release all restrictions starting with "PLI" 3 days in advance at 6am CT (case 2258).
 *        5/02/13   Add initial support for CDN utilization (proshop menus)
 *        4/23/13   Hazeltine - remove custom tee sheets - use normal config parms and normal custom tee sheets (case 2257).
 *        4/17/13   The CC - add custom to getProshopMainMenu and getProshopSubMenu to ignore mobile settings so they can use
 *                           iPads for all proshop functions (they have been informed that not all features will function properly) - case 2256.
 *        4/03/13   Rogue Valley CC (roguevalley) - Brought back "Phone Only" custom, but they will always be released at 8:30am.
 *        4/02/13   Rogue Valley CC (roguevalley) - Commented out custom to release "Phone Only" restrictions 3 days in advance.
 *        3/20/13   Brooklawn CC (brooklawn) - move restriction custom to verifyCustom.checkRestLift.  Commented out some additional custom tee sheet code.
 *        3/19/13   Brooklawn CC (brooklawn) - Commented out custom tee sheet code.
 *        3/06/13   Stanwich CC (stanwichclub) - Commented out custom tee sheet code, since it will be handled manually from now on.
 *        3/04/13   Removed meta-refresh from verifyMem Access Error page, was redirecting members to a broken link.
 *        2/26/13   Updated moveReqs to pass and be passed a username value.
 *        2/21/13   Updated moveTee to move event_type and hole fields from teecurr to teepast.
 *        2/14/13   Change the FLEXWEBFT message slightly in verifyMem.
 *        1/17/13   Add custom message to verifyMem if caller is FLEXWEBFT and session is expired
 *       12/21/12   Desert Mountain - release a guest restriction 48 hrs in advance (case 2211).
 *       12/10/12   Updated xTees, xEvents, xCustomTimer to fix an issue where the date/time of X removal message was getting duplicated with each email sent.
 *       12/05/12   Existing iPad menus are Golf specific so only use them if current activity is golf
 *       11/21/12   New doctype decleration
 *       11/07/12   Move Lottery processing methods to Common_Lott_Orig so we can easily remove them once all clubs are using the methods in Common_Lot.
 *       10/04/12   Estero CC (esterocc) - Added custom to release a restriction covering their lottery times, two days after (case 2191).
 *       10/02/12   Oahu CC (oahucc) - Fixed issue with releaseRestOahuCC that was causing the restriciton name and color to not be removed after the restrictions had been released (case 1893).
 *        9/11/12   Oahu CC (oahucc) - Updated restriction release to be 96 hrs instead of 72 (there was a miscommunication) (case 1893).
 *        9/13/12   The Country Club (tcclub) - Updated day-of 3 min interval custom to start 3 min intervals at 7:03 during both seasonal date ranges.
 *        9/11/12   Oahu CC (oahucc) - Fixed issue with one of their restriction releases releasing one day late (case 1893).
 *        9/10/12   Add courseID to the CDGA output file
 *        8/23/12   West Shore CC (westshorecc) - Added custom to release the 'Weekend Mornings' restriction Friday around 12:00PM ET (case 2182).
 *        8/13/12   Oahu CC - Updated restriction release custom to release non-Saturday restrictions 3 days in advance instead of 5 (case 1893).
 *        8/01/12   Westchester - Lottery Weighted By Rounds processing - reverse the weighting so it gives priority to those members that play the most.
 *        7/31/12   Move Lottery processing methods to Common_Lott.  Also, prevent duplicate lassigns5 entries in logAss
 *        7/31/12   Oahu CC (oahucc) - Updated restriction release custom to avoid releasing two Sunday spouse restrictions (case 1893).
 *        7/23/12   Brooklawn CC (brooklawn) - Removed restriction release custom for Labor Day.
 *        7/18/12   Fixed a bug within xTees that was duplicating a portion of the X warning email message.
 *        7/17/12   Updated moveTee() to include the nopost1-5 fields for tee times moved to teepast2.
 *        6/24/12   Reworked checkTime(), moved 2min timer customs to verifySlot.getSlotHoldTime and also added 1 additional loop to the 2min timer value (max) for all clubs.
 *        6/12/12   Changed server id for both timer servers to use the new 101 & 102 which are the dedicated timer VMs.
 *        5/31/12   Updated updateActHist() so that it properly adjusts the timestamp on the history entries to match the club's time zone.
 *        5/24/12   Brooklawn CC (brooklawn) - Updated restriction release custom to use the proper date variable.
 *        5/16/12   Brooklawn CC (brooklawn) - Added two custom restriction releases for Memorial Day and Labor Day.
 *        5/02/12   Valley CC (valleycc) - Updated custom restriction release custom to release 4 specific customs instead of all customs blindly (case 1945).
 *        4/25/12   Scioto CC (sciotocc) - Updated custom processing for spouse access with different days in advance.
 *        4/24/12   Willow Ridge CC (willowridgecc) - Tweaked custom to address a couple issues with the original settings.
 *        4/19/12   Updated logAssign() method with a check to prevent duplicate log entries.
 *        4/18/12   Willow Ridge CC (willowridgecc) - Added custom to use a different start and end time when building time sheets on weekends.
 *        3/14/12   Modified verifyMem to redirect new skin sites that don't use seamless to their login page if their session is null
 *        3/08/12   Remove Congressional custom tee sheets.
 *        2/02/12   Mesa Verde CC - only get weights for the lottery being processed (same as case 1860 for Leawood).
 *        1/23/12   Discovery Bay CC (discoverybay) - Added a custom using xCustomTimer to release all X's for a day 1 day in advance at 12:00pm PT (case 2108).
 *       12/01/11   Ironwood CC (ironwood) - Added a custom to release all X's for a day 3 days in advance at 6:30am PT.
 *       12/01/11   Updated xCastlePinesTees method to be xCustomTimer instead, and set it up to handle more than just one club. Method is used to release all X's for a day at a specific time.
 *       11/22/11   Updated moveTee so the custom_disp fields are moved for all clubs, instead of just for Mid Pacific.
 *       11/08/11   Ocean Reef Club (oceanreef) - Added custom to release restrictions "Advanced Time 1/2/3/4" 3 days in advance at 7am ET (roll them ahead 4 days every morning) (case 2063).
 *       11/08/11   Tonto Verde GC (tontoverde) - Increased the inactivity timer from 6 minutes to 8 minutes (case ?).
 *       11/07/11   Oahu CC (oahucc) - Updated restriction release custom to release the "Saturday Working Spouses" restriction 4 days in advance instead of 2 (case 1893).
 *       10/25/11   Indian Ridge CC (indianridgecc) - Updated custom to release the "Guest Policy" guest restriction 72 hrs in advance, instead of 24 (case 2040).
 *       10/21/11   Indian Ridge CC (indianridgecc) - Decrease the timeout to 4 minutes for Indian Ridge CC (members are used to online tee times and don't need 6 minutes, according to club) (case 2058).
 *       10/13/11   Ironwood - add custom to build POS charge files at 11 PM so they can pull them via FTP.
 *       10/18/11   Monterey Peninsula CC (mpccpb) - Increase inactivity timer from 6 mins to 10 mins (case 2054).
 *        9/27/11   Indian Ridge CC (indianridgecc) - Release "Guest Policy" guest restriction 24 hrs in advance around 7am Pacific time (case 2040).
 *        9/26/11   Time Sheet Backups will now display lesson bookings that occupy time sheet slots.
 *        9/23/11   Quechee Club Tennis (quecheeclubtennis) - Removed custom to build custom time sheets for weekends since this now applies to all days.
 *        9/22/11   Add support for nightly generation of CDGA files for clubs configured to use it
 *        6/28/11   The Country Club - Brookline (tcclub) - Updated 3 min interval custom to start at 7:48 on Tue/Wed/Thurs between 9/1 and 10/31.
 *        7/25/11   CC at Castle Pines (castlepines) - Added xCastlePinesTees() custom to run at ~5pm MT (6 CT) each day and remove X's 2-3 days in advance, depending on the day (case 2003).
 *        7/20/11   Philly Cricket (philcricket) - add custom to build POS charge files at 9 PM so NorthStar can pull them via FTP (case 2006).
 *        7/15/11   Olympic Club (olyclub) - add custom to build POS charge files at 11 PM so they can pull them via FTP.
 *        6/28/11   The Country Club - Brookline (tcclub) - Updated 3 min interval custom to start at 7:03am instead of 8:03am.
 *        6/24/11   Run custom to remove blockers twice each morning to ensure it runs - The CC.
 *        6/16/11   moveTee - Added tflag1-5 fields to be moved from teecurr2 to teepast2.
 *        6/15/11   teeTimer - further split the load by doing the db optimizations over 4 days rather than all in one night.
 *        6/14/11   xTees - Interlachen - do not remove X from player5 if no other X's in the tee time.
 *        6/14/11   teeTimer - split the load between 2 servers by calling scanTee for half the clubs on server 1 and half on server 2.  This process
 *                  has been taking too long at night and some clubs were not getting done.
 *        6/10/11   Quechee Club Tennis (quecheeclubtennis) - Updated custom time sheets to build for quecheclubtennis site instead of quecheeclub.
 *        6/01/11   Updated tsheetTimerAct to run off the staff list, as well as to send separate emails for each root activity, instead of all in one email.
 *        5/26/11   Updated moveReqs to not call logassign if called from Proshop_dsheet as _dsheet will log the assignments when sending the emails.
 *        5/26/11   Updated tsheetTimerGolf to only search the staff list for entries with an activity_id of 0.  Was previously sending backup tee sheets to non-golf staff members.
 *        5/24/11   Scioto CC (sciotocc) - Adjusted days in advance that spouses have access to Friday tee times.
 *        5/17/11   Updated xTees() to clear out orig2-5 values if an 'x' is removed from that player slot.
 *        5/12/11   Valley CC (valleycc) - Release all restrictions 48 hours in advance (case 1945).
 *        5/11/11   Royal Montreal GC (rmgc) - Added custom tee sheet processing.
 *        4/26/11   Updated removeLessonBlockers to remove old lesson blockers when they are a year old instead of older than today's date.
 *        4/19/11   TPC Boston (tpcboston) - Increase inactivity timer from 6 mins to 10 mins (case 1974).
 *        4/12/11   Allow for X's in lottery requests.
 *        4/08/11   Rogue Valley CC (roguevalley) - Adjusted date range for custom to release 'Phone Only' restrictions.
 *        4/05/11   Birmingham CC (bhamcc) - Added custom to release Sat/Sun mnum restrictions if associated lotteries have been approved and there are still open times available (case 1946).
 *        3/28/11   The Country Club (tcclub) - Adjusted twosome times custom to build 7:45-8:15 10min intervals Tues/Wed/Thurs in Sept/Oct.
 *        3/24/11   Talbot CC (talbotcc) - Increase inactivity timer from 6 mins to 10 mins (case 1953).
 *        3/21/11   Changed applicable sql update calls to teecurr2 to set the last_mod_date to now, for insert statements we set create_date
 *        3/07/11   Updated verifyProAccess so that it just returns false instead of bombin out and displaying an error message if a proshop user was not found
 *        3/02/11   Wee Burn - Updated custom tee sheet code for 2-some times with new date/times for Tuesdays.
 *        2/17/11   Converted all references to disallow_joins over to the new force_singles field.
 *        2/04/11   Change to prevent lottery_email flag from getting set when a lottery request is converted into a time that is not a lottery time.
 *        1/05/11   moveReqs - save the teecurr_id of each tee time in lreqs for the sendEmail process in Proshop_dsheet.
 *       12/15/10   Mirasol CC (mirasolcc) - Release 2 restrictions and roll them forward every morning around 4am (case 1920).
 *       11/18/10   Dataw Island Club (dataw) - Guest type "Blank" will now be ignored during group weight processing in lotteries (case 1913).
 *       11/16/10   Added common methods for lottery assignment calculations and added processing to log every assigned time for members in lassigns5.
 *        9/28/10   Pecan Plantation - only get weights for the lottery being processed (case 1894 - same as 1860 for Leawood).
 *        9/27/10   Oahu CC (oahucc) - Update restriction release custom to release 'Spouses Juniors Front 9 PM' and 'Spouses Juniors Back 9 PM' restriction at 6am Tuesday morning (case 1893).
 *        9/02/10   Oahu CC (oahucc) - Update restriction release custom to release 'Saturday Working Spouses' restriction at 6am Thursday morning.
 *        8/25/10   updateHist - change the sdate values to include milli-seconds and use the club's time zone value, also remove the year and CDT.
 *        8/24/10   Updated moveTee to move empty tee times that have history entries associated with them to the teepast2 table instead of teepast_empty
 *        8/20/10   Update queries in rebuildTFlags to only match on single mship5 record to fix the error: Subquery returns more than 1 row
 *        8/17/10   assign1Time (lottery processing) - if the first matching time is available, make sure we check for restrictions.
 *        8/12/10   Fix array sizing problem with guest types in moveTee
 *        8/12/10   assignTime2 - set the new course name in the lottery request parm so assign1Time will check for restrictions on the new course.
 *        8/10/10   do1Rest - Fixed activity sheet processing.  Fixed syntax errors in database queries for 'All Weekday' and 'All Weekends' options.
 *        8/04/10   tsheetTimerGolf - get the pro emails from the new staff_list table instead of the backup_emails table (case 1605).
 *        7/22/10   Commented out the bcc'ing of the support email address on all backup teesheet emails.
 *        7/07/10   Fixed do1Blocker.  "All Weekdays" option was set up improperly and was including Sundays as a result.
 *        6/30/10   Leawood South CC - only get weights for the lottery being processed (case 1860).
 *        6/24/10   Oahu CC (oahucc) - Created custom releaseRestOahuCC method.  Do not roll forward restrictions recurring on Saturdays
 *        6/24/10   Force mail.smtp.sendpartial to true to ensure we send emails even if there are bad address - send the ones we can
 *        5/24/10   Allow for an unlimited number of courses.
 *        5/20/10   Quechee Club (quecheeclub) - Added custom to buildTimeSheet to apply different start/end times on Fri-Sun timesheets
 *        4/15/10   Inverness Club - update daysInAdv to allow access to all lotteries.
 *        4/11/10   Updated moveTee to include guest_id fields.
 *        3/10/10   Add 2010 exceptions to custom tee sheets for Congressional (case 1060).
 *        1/27/10   Update buildTimeSheet to prevent a loop if the last time is near midnight.
 *        1/20/10   Updated do1Rest to add locations_csv support for Activities & it now used id instead of name
 *       12/15/09   Sea Cliff CC - add custom to set precheckin in todays tee time at midnight (case 1758).
 *       12/10/09   Oahu CC - add custom to roll ahead the start date of all member restrictions (case 1756).
 *       12/08/09   Do not delete events or event signups - mark them inactive instead so we can easily restore them.
 *                  Delete inactive events and event signups after 90 days.
 *       12/07/09   Make do1Event process old events if it's an activity event
 *       12/03/09   Updated optimize method from buddy to partner table.  refreshBuddyList method removed (commented out for now)
 *       12/01/09   Add Saudi Arabia time zone processing (no DST).
 *       11/16/09   Added methods to build the time sheets for activities
 *       11/10/09   Update lottery processing to check if a tee time is already busy when assigning times.
 *                  This is necessary because a pro can pre-book a tee time that is during a lottery when a member(s)
 *                  have already requested that time.  Assign1Time needs to find a matching tee time.
 *       11/10/09   Medinah - add custom to roll ahead the start date of 2 member restrictions (case 1692).
 *       11/03/09   Change the end date used to calculate the member's weight in a weighted by prox lottery from today
 *                  to the date the request is for (date of tee sheet).  This ensures that all recent weights are included.
 *       11/03/09   Add processing to tsheetTimer to email Activity sheets to appropriate pro or administrator.
 *       11/02/09   Updated do1Blocker to add locations_csv support for Activities
 *       10/26/09   Correct the references to pstmt55ab in do1Five (statements were referencing pstm55a).
 *       10/25/09   Change meta tag in HeadTitileMobile to prevent screen size problem.
 *       10/22/09   Added updateActHist method for updating history on activity times
 *       10/16/09   Add a failure code to lreqs3 and save it when we cannot assign a lottery request in assignTime.
 *       10/08/09   Updated verifyProAccess to allow ForeTees unrestricted proshop users from being restricted from any limited access area
 *       10/06/09   Updated verifyProAccess to check permissions for a specific activity_id only.
 *       10/05/09   Fixed error in xMerionTees where the message was not getting initialized for each group.
 *       10/04/09
 *        9/25/09   Updated doBlockers/do1Bocker & do1Event to add support for Activities
 *        9/24/09   Oakmont - use xMerionTees to send email reminders to members 7 days in adv of tee times (case 1725).
 *        9/15/09   Add TS_PAST_UPDATE to list of featureIDs for processing in proshopRestrict() method
 *        9/12/09   Add BannerMobileSlot for mobile users.
 *        9/08/09   Remove stats5 db table processing as we don't use this any longer.
 *        9/03/09   Desert Forest - add custom to set precheckin in todays tee time at midnight (case 1693).
 *        9/02/09   Add getLotteryState method for Member_teelist and Member_teelist_list.
 *        8/24/09   Add custom_disp fields to moveTee for midpacific, will later switch to apply to all clubs
 *        7/28/09   Add method to display error message for Mobile users (displayMobileError).
 *        7/27/09   Fix for isWaitListTim
 *        7/16/09   Add headtitles for Mobile devices.
 *        7/02/09   Add clearing of in_use fields in activity_sheets to checkTime2
 *        7/01/09   Wedgewood G&CC (wedgewood) - Increase inactivity timer from 6 mins to 8 mins
 *        6/25/09   Medinah - Increase inactivity timer from 6 mins to 10 mins
 *        6/23/09   Sankaty Head GC (sankatyheadgc) - Increase inactivity timer from 6 mins to 10 mins
 *        6/14/09   Add new refreshBuddyList method - called by Common_sync.rosterSync()
 *        6/11/09   Updated insertTee2 to populate the new mship, mtype, gtype, and grev fields in teepast2
 *        6/02/09   Ravenna - remove custom to bump start date on guest restrictions.
 *        5/13/09   Do not reset timers if they have not yet fired (watch dog processing).
 *        5/11/09   Add and correct the watch dog functions in xTimer, inactTimer, and inactTimer2.
 *        5/08/09   Add new 2 minute timer method (inactTimer2) to off-load original 2 min timer.
 *                  Each 2 minute timer will watch dog the other.
 *                  Reset each 2 minute timer immediately upon entry rather than waiting until processing complete.
 *        5/07/09   GC of Georgia - Increase inactivity timer from 6 mins to 10 mins due to low staffing levels of their golf shop
 *        5/05/09   Medinah - remove custom days in advance (case 1673).
 *        5/04/09   If logerror fails then write error message to text file in V5 folder.
 *        5/04/09   The Lakes - change custom to move the start date of a guest restriction each day (change back from 6 days to 4 days).
 *        4/09/09   Remove custom Sunday tee sheet for Green Hills CC (commented out)
 *        4/02/09   Fix in moveReqs for lottery emails (lottery (name) field was getting cleared when being moved to teesheet - then emails would not go out
 *        3/26/09   moveTee changes - populate new teepast2 fields mtype, mship, gtype, grev values with current values from member2b & guest5 to allow for
 *                  changes to rounds reports.
 *        3/03/09   Oahu CC - change custom to move the start date of a member restriction (case 1610).
 *        3/03/09   The Lakes - change custom to move the start date of a guest restriction each day (case 1621).
 *        2/26/09   Manito CC - change their custom restriction updater to move the start date at 8:05 AM
 *                              instead of 1:10 PM.
 *        2/13/09   Oahu CC - custom to roll member restriction ahead 7 days (case 1610).
 *        2/11/09   Increase the inactivity timer for event signups from 6 ins to 12 mins to allow for addition
 *                  of questions and handicap info (requested by Colleton River - no case).
 *       12/12/08   Remove custom tee sheets for CC of St Albans (now uses custom tee sheet feature).
 *       11/26/08   Lottery Processing - buildArrays - do not include tee times that have a player already.
 *                  This will be necessary in case we do a custom for ClubCorp to allow them to book times for
 *                  Signature Gold members w/o using the lottery.
 *       11/24/08   Don't build daily tee sheets for TPC Sugarloaf (tpcsugarloaf) (case 1588).
 *       10/15/08   Fixed bug in checkWaitListSignup so it doesn't prompt on converted signups
 *       10/15/08   Ravenna - Change end date to roll member type restriction ahead 7 days.
 *       10/05/08   Black Diamond Ranch - custom to remove event signups for specifed year long events each day of week (case 1543).
 *       10/02/08   Make sure the con is closed in inactTimer method if exception received.
 *       10/01/08   Only print 15 minute intervals on the oakhillscc backup tee sheets
 *        8/29/08   Mayfield Sand Ridge - put custom on hold while the club decides (case 1539).
 *        8/28/08   Add more try-catches to xMerionTees to identify an error.  Also, exclude inactive clubs from
 *                  timer processing.
 *        8/25/08   Mayfield Sand Ridge - send email reminder of tee times 48 hrs in advance (case 1539).
 *        8/22/08   Adjusted feature-specific redirection processing in restrictProshop()
 *        8/20/08   Aliso Viejo - increase tee timer value from 10 mins to 16 mins until they get new clubhouse.
 *        8/14/08   Adjusted feature-specific redirection processing in restrictProshop()
 *        8/12/08   Added some additional debug info to dayInAdv method and logError now includes node# in msg
 *        7/31/08   Updated assign1Time to init the fail status to true in case course has no times avail.
 *                  This was returning an incorrect status if all times on a course were blocked.
 *        7/31/08   Altered tSheetTimer to get additional email addresses from database, commented out related customs.
 *        7/18/08   Added verifyProAccess and restrictProshop methods for use with limited access proshop users
 *        7/17/08   Bishops Bay - change default lottery weight in get1WeightP so a member with no weight gets zero (case 1521).
 *        7/16/08   rebuildTFlags - delete any member records with an empty username to prevent tflag problems in teecurr.
 *        7/14/08   Brookhaven - add emails for backup tee sheets. Orchid Island - change backup tee sheet email.
 *        7/09/08   Lottery - set the lottery type in lreqs3 at the start of processing rather than after assigned.
 *        7/02/08   Update checkTime to release wait list signups
 *        7/01/08   Seville - add emails for backup tee sheets.
 *        7/01/08   Added getIntTime method
 *        6/24/08   Hop Meadow - custom to roll guest restriction ahead 7 days (case 1402).
 *        6/24/08   Hop Meadow - custom to roll member number restriction ahead 7 days (case 1401).
 *        6/20/08   Copy the pos fields from tecurr to teepast so we can send POS charges from old tee sheets.
 *        6/12/08   do1Blocker - change so processing only applies blockers to times with no players
 *        6/07/08   Aliso Viejo - increase tee timer value from 6 mins to 10 mins until they get new clubhouse.
 *        6/07/08   The Club at Ravenna - custom to roll guest restrictions ahead 2 days (case 1485).
 *        6/05/08   Slight change to how logErrorToFile outputs files (different filename scheme)
 *        6/04/08   Added logErrorToFile method to output errors to rsync-log.txt in a clubs webapps directory
 *        6/02/08   Create new public member timeout value SystemUtils.MEMBER_TIMEOUT (set to 15 min from previous 10)
 *        5/19/08   Fixed tsheetTimer() so that if one club fails it continues on with remaining clubs
 *        5/19/08   Martin Downs - Remove email address from tee sheet backup
 *        5/15/08   Tamarack - change default lottery weight in get1WeightP so a member with no weight gets zero (case 1479).
 *        5/06/08   Forest Highlands GC - add email addresses for backup tee sheets.
 *        4/28/08   Update menu get prohop/member methods
 *        4/25/08   Add processing for tflag to update teecurr2 each night after we move the tee times to old sheets (case 1357).
 *        4/24/08   Add tflag process in insertTee2 for member flags to be displayed on pro tee sheet (case 1357).
 *        4/17/08   Add isWaitListTime() method
 *        4/16/08   Castle Pines - add/change some email addresses for backup tee sheets.
 *        4/11/08   Muirfield - change custom to roll restrictions ahead 8 days instead of 7 to get them past
 *                              the day of the following week.  Do this around 1:00 AM.
 *        4/10/08   Mayfield Sand Ridge - add email to backup tee sheets
 *        4/02/08   Oak Hill CC - custom tee sheets for day of - 5 minute intervals (case #1422).
 *        3/28/08   Cherry Hills - add email addresses for backup tee sheets.
 *        3/26/08   Blackstone - add two email addresses for backup tee sheets.
 *        3/24/08   Woodway - remove custom tee sheets for 2-some times (now uses standard feature).
 *        3/11/08   Colleton River - add some email addresses forbackup tee sheets.
 *        3/07/08   Muirfield - move the start date of some member restrictions each week (case 1366).
 *        3/07/08   Robert Trent Jones - add email address for backup tee sheets.
 *        3/05/08   Member menus - remove check for web site callers so all members get email tab.  Email was disabled
 *                  for CE and MF sites originally because the web site companies requested that we do it.
 *        2/20/08   Change scanTee to add support for 'every other week' parameter for custom tee sheets
 *        2/15/08   Mirasol - comment out custom to cancel tee time if less then 2 players until club decides (case #1244).
 *        2/10/08   Robert Trent Jones - add email address for backup tee sheets
 *        2/07/08   Add hook to scanTee for custom tee sheet processesing - new method buildCustomTees
 *        1/31/08   Blackstone - add email address for backup tee sheets
 *        1/07/08   Jonathans Landing - Removed days in advance custom #1328
 *        1/03/08   Blackstone - change email addresses for backup tee sheets.
 *       12/17/07   Blackstone - add another email address for backup tee sheets.
 *       12/13/07   The CC at Brookline - custom tee sheets for day of (case #1346).
 *       12/05/07   Jonathan's Landing - Custom days in advance for certain member types - Case# 1328
 *       12/05/07   Eagle Creek - Add email address to backup tee sheets
 *       11/23/07   Congressional - change custom tee sheets - only do 8min intervals Tues. from 4/1 -> 10/31 on Club course
 *       10/31/07   Add new tsheetTimer(club) method to send backup tee sheets to specific clubs via the Proshop side
 *       10/25/07   Change Sales and Support passwords.
 *       10/16/07   Imperial CC - custom tee sheets (case #1293).
 *       10/14/07   Eugene CC - custom tee sheets (case #1273).
 *       10/14/07   Eugene CC - add email address for backup tee sheets (case #1274).
 *       10/14/07   Claremont CC - custom tee sheets (case #1281).
 *       10/07/07   xTees - Custom for Gallery Golf Club - X's removed 96 hours in advance (case #1272). *** REMOVED 1-30-08
 *       10/01/07   Rogue Valley - change the time of day the restriction is updated based on date.
 *        9/21/07   Shadow Ridge CC - add additional email address for backup tee sheets.
 *        9/21/07   Mirasol - custom to cancel tee time if less then 2 players after removing X's (case #1244)
 *        9/19/07   Ravenna - only move restrictions during summer season.
 *        9/12/07   Congressional - change custom tee sheets (skip some days).
 *        9/12/07   CC of Castle Pines - add additional email address for backup tee sheets.
 *        9/12/07   Added getClubName method
 *        9/11/07   Mediterra & Orchid Island - add additional email addresses for backup tee sheets.
 *        8/21/07   getUser - pull handicap from g_hancap instead of c_hancap in member2b
 *        8/03/07   St. Albans - Updated the custom restriction updating for their Member only play restrictions
 *        7/27/07   Ravenna - move the start date of a member restriction each week.
 *        7/24/07   Medinah CC - Custom days in advance (case #1225).
 *        7/17/07   Oahu CC - Custom tee sheets (case #1214).
 *        7/17/07   Sewickley Heights - Custom tee sheets (case #1212).
 *        7/17/07   Add Arizona time zone processing (case #1218).
 *        7/16/07   Add Hawaiian time zone processing (no DST).
 *        7/10/07   scanTee - if running on server #4 (dev server) then scan all days, don't break when found
 *        6/27/07   teeTimer move stmt.close() from after catch to before.
 *                  Also, scanTee - quit looking for tee sheets once tee times are found for a date.
 *        6/21/07   checkTime (2 min timer) - bump some time spans for customs to make sure we don't miss the time to run custom.
 *        6/19/07   Brooklawn - change the time of day that we move the start date of a member restriction each week.
 *        6/15/07   scanTee - do not adjust date based on time.  This prevents west coast clubs from updating tee sheets.
 *        6/13/07   Moved the purging of old teehist entries from updateHist() to teeTimer() for nightly processing
 *        6/13/07   Modified updateHist to enforce a max length for the player1-5 fields
 *        6/13/07   Modified insertTee2 to enforce a max length for the player1-5 fields for teepast too
 *        6/07/07   Updated moveReqs and added new checkBlockers method
 *        6/06/07   Modified insertTee2 to enforce a max length for the player1-5 fields
 *        6/04/07   Changed event_type default value in buildHTee method (Data truncation error w/ latest connector J v3)
 *        5/31/07   Fixed bug in PurgeBouncedEmails method
 *        5/30/07   Remove custom tee sheets for Skokie.
 *        5/25/07   Catamount/Cordillera - set course name to Catamount for file entries from Catamount (custom reporting).
 *        5/24/07   The CC - remove blockers for the current day each morning (case #1119).
 *        5/24/07   Tamarack - Custom tee sheets (case #1125).
 *        5/22/07   Bonnie Briar - Custom tee sheets (case #1173).
 *        5/21/07   Added getIndexFromToday method
 *        5/09/07   Moved daysInAdv method from Login to here
 *        5/07/07   Blackstone - add GM's email address for backup tee sheets.
 *        4/30/07   Bellevue CC - Add custom tee sheets (case #1147).
 *        4/30/07   Rogue Valley - change the time that the restricitons are updated.
 *        4/27/07   Valley CC - remove X's 36 hours in advance on weekends.
 *        4/27/07   Make changes to Westchester's custom tee sheets.
 *        4/27/07   Add 2 custom fields and datetime fields when moving tee times from teecurr to teepast.
 *        4/26/07   Updated SystemUtils.moveReqs
 *        4/24/07   Greenwich CC - Add custom tee sheets for 2-some times (case #1121).
 *        4/24/07   Congressional - Change custom tee sheets for 2-some times (case #1060).
 *        4/24/07   Add custom tee sheets for Green Hills CC.
 *        4/20/07   Muirfield - add email addresses for backup tee sheets (case #1139).
 *        4/13/07   Updated teehist table definition - mname varchar(50)
 *        4/11/07   Changed calcProxTime, checkInUse, getAuthenticator, getUser to have a public interface (for use in Proshop_dsheet)
 *        4/10/07   Congressional - Add custom tee sheets for 2-some times (case #1060).
 *        4/06/07   Hazeltine - move the start date of a member restriction each week (case #1109).
 *        4/04/07   The CC - Add custom tee sheets for 2-some times.
 *        4/02/07   Los Coyotes - add email addresses for backup tee sheets.
 *        3/28/07   CC of Jackson - delete any Sat/Sun tee times with less than 3 players on Friday at 9:00 AM (case 1066).
 *        3/28/07   Process Lottery - process restrictions for all clubs when assigning a time for lottery request (case 1078, 1084).
 *        3/14/07   Change Woodway custom tee sheets to reflect new dates for 2-some times.
 *        3/25/07   Add email address to backup tee sheets for Columbine
 *        3/20/07   Added getFullNameFromUsername method to return a members full name from their username
 *        3/11/07   Add custom tee sheets for The Pinery CC.
 *        3/06/07   Add custom tee sheets for CC of St Albans.
 *        2/20/07   Merion - add xMerionTees to send email reminders to members 7 days in adv of tee times.
 *        2/19/07   Added getUsernameFromFullName method to return a members username name from their full name
 *        2/05/07   Blackstone - add email addresses for backup tee sheets.
 *        1/29/07   Add verifyProAdm method to verify user of Common servlets.
 *        1/17/07   CC of Lincoln - add custom to set precheckin in todays tee time at midnight.
 *        1/11/07   Royal Oaks Houston - add email addresses for backup tee sheets.
 *        1/11/07   Update SQL statements in do1Blocker so that blockers are not set in tee times for Shotgun Events.
 *        1/10/07   Transit Valley - move the start date of a member restriction each week.
 *                  Also, make this a common method and change others to use it (releaseRestCommon).
 *        1/10/07   Peninsula - add email addresses for backup tee sheets.
 *       12/27/06   Modified checkTime to release notifications that are in use too long
 *       12/11/06   Lottery processing - change to check front and back times in sequence with before and after times.
 *       11/14/06   xTees - Custom for Lakewood Ranch - X's removed 6 days in advance *** REMOVED 1-30-08
 *       11/14/06   Meadow Springs - remove the date limitation when moving the start date of a member restriction each week.
 *       10/17/06   Added additional email for Merion to the Backup Tee Sheet routine
 *       10/13/06   Add new public scrubString method for preping incoming form values
 *       10/13/06   Brooklawn - move the start date of a member restriction each week.
 *       10/11/06   Blackstone - add email addresses for backup tee sheets.
 *       10/10/06   Backup Tee Sheets - if shotgun event, then put "Shotgun" in time column.
 *       10/10/06   Lottery - allow for a unique f/b indicator in lreqs3 for all times when the times have been assigned.
 *        9/30/06   Meadow Springs - move the start date of a member restriction each week.
 *        9/28/06   CC of St. Albans - move the start date of a member restriction each week.
 *        9/20/06   Bellerive - use custom tee sheets for fall of 2006 while their new grass grows.
 *        9/19/06   Check restrictions when assigning times for Cherry Hills lotteries (enable this feature now).
 *        9/06/06   Change the custom tee sheets for Piedmont from 8 mins to 6 mins.
 *        9/05/06   Martin Downs - add email addresses for backup tee sheets.
 *        8/22/06   Changes to getMemberMainMenu & getMemberSubMenu / getProshopMainMenu & getProshopSubMenufor TLT System
 *        8/18/06   Add special reporting for Cordillera and Catamount.  Build files containing the day's tee time
 *                  information and send to their consultant.
 *        8/03/06   xTees - Custom for Mission Viejo - X's removed 7 days in advance
 *        7/29/06   Modified moveTee so that it copies teecurr_id & pace_status_id from teecurr2 -> teepast2
 *        7/28/06   xTees & xEvents - correct the date in the email message for when X's will be removed.
 *        7/28/06   xTimer - seperate the processing for each club so that if one fails it does not stop the others.
 *        7/28/06   Lottery - get1WeightP - correct the weight calculation.
 *        7/26/06   Always reset the 2 min timer and 60 min timer to ensure that they are running - if exception.
 *        7/14/06   Add some safety checks for the 2 min timer and 60 min timer to ensure that they are running.
 *        7/12/06   Lottery processing - if assigning a time other than that requested, check member access restrictions.
 *        7/11/06   Add custom tee sheets for North Oaks.
 *        6/28/06   Modified moveReqs to use the 'assigned' fb indicator from lreqs instead of the original fb.
 *        6/26/06   Modified do1Blocker so that tee times unblocked via auto-blocker feature will not become re-blocked
 *        6/23/06   Added buildDatabaseErrMsg global method for database errors
 *        6/22/06   Add custom tee sheets for El Niguel.
 *        6/15/06   Save the date when scanTee was last run so we don't have to call it every time a pro logs in.
 *        6/05/06   Changed the moveTee function to access db field names instead of #s
 *        6/01/06   Changed the dates for Westchester's custom tee sheets.
 *        5/30/06   Added getSimpleTime(hr,min) method for returning "3:05 PM" from 15 5
 *        5/30/06   Add ensureDoubleDigit() method for returning '08' from an '8'
 *        5/22/06   Changes to support the added primary key column in teecurr2 (teecurr_id)
 *        5/09/06   Skaneateles - add custom to automatically move the start date of some member rests.
 *        5/02/06   Remove synchronized statements.
 *        4/24/06   Add custom tee sheets for Brooklawn.
 *        4/24/06   Use the MYSQL 'ORDER BY RAND()' option in the Random Lottery processing.
 *        4/19/06   Add custom tee sheets for Wee Burn.
 *        4/17/06   Add custom tee sheets for TPC-TC.
 *        4/17/06   Add custom tee sheets for Apawamis CC.
 *        4/11/06   CC at Castle Pines - add more email addresses for backup tee sheets.
 *        4/03/06   Add custom tee sheets for New Canaan CC.
 *        3/29/06   CC at Castle Pines - add a 2nd email address for backup tee sheets.
 *        3/29/06   Mission Viejo - add a 2nd email address for backup tee sheets.
 *        3/08/06   Cherry Hills - move the start date of a member restriction each week.
 *        3/07/06   Change the errorlog method to save msg in db table.
 *        3/06/06   Change sessionLog method from using a text file to log data to using a db table.
 *        3/01/06   Add Common_Server class file processing where we check the id of the server we are running in
 *                  when timer processes are triggered.  If not server #1, do not process the timer (let it run though).
 *        3/01/06   Remove the 'Order By' command on the Lottery queries so that the groups are processed
 *                  in random order versus by the largest groups first.
 *        2/27/06   Add updateHist method to track all changes to tee times.
 *        2/08/06   Inverness - add a 2nd email address for backup tee sheets.
 *       12/30/05   When moving tee times from teecurr to teepast, move empty tee times to new table (teepastempty).
 *       12/21/05   Add custom tee sheets for Skaneateles CC.
 *       12/08/05   The Lakes - move the start date of a member restriction each day.
 *       12/06/05   Add doTableUpdate_tmodes method for new rounds report feature.
 *       11/09/05   Add custom tee sheets for Tripoli CC.
 *       11/06/05   Set the mNums in teecurr2 when building tee times for Proshop_evntChkAll so reports will work on tee sheet.
 *       10/16/05   Add custom tee sheets for Meadow Springs CC.
 *       10/11/05   Add custom tee sheets for Nakoma CC.
 *       10/07/05   Add custom tee sheets for The Stanwich Club.
 *        9/24/05   Correct the exception displays in the logerror calls.
 *        9/22/05   Add a login log to track all logins.
 *       09/13/05   processLott - reset i2 when a spot is taken - before checking before & after times.
 *                        This corrects problem where the last group in a multi group is unassigned.
 *       09/01/05   Add a 2nd email address for the back-up tee sheets for CC of St. Albans.
 *       08/21/05   Add displayOldNotes method - copied from Proshop_oldsheet so others can access it.
 *       08/19/05   Add displayNotes method - copied from Proshop_sheet so others can access it.
 *       08/07/05   Add custom tee sheets for Mission Viejo.
 *       06/07/05   Add new weighted lottery type processing.
 *       05/04/05   Old Warson - add custom to set precheckin in todays tee time at midnight.
 *       04/18/05   Move login credentials from Login to here for security purposes.
 *       04/15/05   Add custom tee sheets for Skokie.
 *       04/04/05   Add custom tee sheets for Hudson National.
 *       03/25/05   Add custom tee sheets for Woodway, Big Springs & Westchester.
 *       03/20/05   Remove DOCTYPE statement from page headers.
 *       03/09/05   Added getDate and getTime methods.
 *       03/03/05   Ver 5 - changed some expressions (show_) for precheckin feature
 *       02/14/05   Move Connect method to its own class file (dbConn) so we can maintain multiple copies.
 *       02/11/05   Add custom tee sheets for Piedmont Driving Club.
 *       01/24/05   Ver 5 - change club2 to club5 and stats2 to stats5.
 *       01/18/05   Add custom processing for North Ridge and Rogue Valley - deact member restrictions daily.
 *       12/07/04   Ver 5 - Check lesson books for busy time slots in inactTimer processing.
 *       11/11/04   Ver 5 - Add removeLessonBlockers method to clean up lessonblock table.
 *       10/17/04   Ver 5 - Add HeadTitle2 for servlets that require javascript.
 *       10/05/04   Ver 5 - Add getMemberMainMenu & getMemberSubMenu for navigation bar drop-down menus.
 *       09/17/04   Ver 5 - Change stats2 table processing to allow for more mem and mship types.
 *       09/16/04   Ver 5 - Remove getDaysInAdv - use com/foretees/common/verifySlot.getDaysInAdv.
 *       09/16/04   Ver 5 - Remove getClub - use com/foretees/common/getClub.
 *       08/02/04   Only process one lottery per club per timer expiration (wait for next timer).
 *       07/08/04   Add optimize method to optimize the db tables every Wed morning.
 *       04/20/04   Add buildHTee for Hazeltine National - custom tee sheets.
 *       03/04/04   Add adjustTimeBack method to make time adjustment.
 *       02/13/04   Add insertTee2 method to insert the tee times for Events.
 *       02/10/04   Add newTmode method to process changes in Modes of Trans..
 *       02/06/04   Add support for configurable modes of transportation in MoveStats.
 *       01/21/04   Add getDaysInAdv method.
 *       01/15/04   Add newGuest, newMem and newMship methods.
 *       01/14/04   Change tee sheets from 30 days to 365 days.
 *                  Add methods to process double tees - buildDblTee and removeDblTee
 *       01/07/03   Added stylesheet and js file link includes for HeadTitle method
 *       12/20/03   Upgrade to Version 4.
 *       12/15/03   Do not add member restriction to teecurr if show=no was specified.
 *       11/25/03   Enhancements for Version 3 - add RevLevel.
 *       11/19/03   Enhancements for Version 3 - add hotel changes to teecurr2 and teepast.
 *        7/18/03   Enhancements for Version 3 of the software.
 *                    - add doLotteries
 *                    - add getLottId
 *                    - add processLott
 *        4/30/03   Add support for F/B tees on events.
 *        2/13/03   Version 2 - add event sign up processing (scan in_use entries, and X's).
 *        1/04/03   Enhancements for Version 2 of the software.
 *                  inactTimer - change processing for removing X's from tee sheets (use hours specified).
 *                  Add support for multiple courses.
 *                  Support new fields in database tables.
 *                  Add support for Front/Back option in Block and Restrictions.
 *                  Add tsheetTimer to email pro shops the current tee sheet.
 *                  Add xTimer to process X's on tee sheet.
 *
 *        8/15/02   Added 'alternate minutes between tee times' processing to buildTee
 *
 *
 *
 ***************************************************************************************
 */


import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.http.*;
import javax.mail.internet.*;
import javax.mail.*;
//import javax.servlet.*;
//import javax.activation.*;

// foretees imports
import com.foretees.common.parmTee;
import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.common.ProcessConstants;
import com.foretees.common.DaysAdv;
import com.foretees.common.verifySlot;
import com.foretees.common.verifyCustom;
import com.foretees.common.verifyLesson;
import com.foretees.common.parmSlot;
import com.foretees.common.Utilities;
import com.foretees.common.getActivity;
import com.foretees.common.Connect;
//import com.foretees.common.parmCourse;
//import com.foretees.common.getParms;

public class SystemUtils {


   //********************************************************
   // Some constants for use by other servlets
   //********************************************************
   //
   // NOTE:  see also com\foretees\common\ProcessConstants !!!!!!!
   //
   public static final String REVLEVEL = "v5";          // change for new version of software!!!!!!!!!!!!!!!!!

   public static final int COURSEL = 30;                // length of courseName field in clubparm2 table
   public static final int EVENTL = 30;                 // length of name field in events2b table
   public static final int EVNTSUPL = 30;               // length of name field in evntsup2 table
   public static final int RESTL = 30;                  // length of name field in restriction2 table
   public static final int FIVEL = 30;                  // length of name field in fives2 table
   public static final int BLOCKL = 30;                 // length of name field in block2 table
   public static final int GRESL = 30;                  // length of name field in guestres2 table
   public static final int GQTAL = 30;                  // length of name field in guestqta4 table
   public static final int MNUML = 30;                  // length of name field in mnumres table
   public static final int DBLTL = 30;                  // length of name field in dbltee table
   public static final int LOTTL = 30;                  // length of name field in lottery table

   //
   //  Database Connection Credentials (keep here to protect)
   //
   public static String db_user = "bobp";
   public static String db_password = "rdp0805";

   //
   //  Login credentials - for Login.java
   //
   public static String support = "support";            // class variables that never change
   public static String supportpro = "supportpro";
   public static String sales = "sales";
   public static String admin = "admin";
   public static String proshop = "proshop";
   public static String id = "foretees";
   public static String passwordSup = "f0ret33s";       // password for support (dev) login...
   public static String passwordSupPro = "foresupport"; // password for support (prosupport) login...
   public static String passwordSales = "foresales";    // password for sales login...

   //
   //   Date that scanTee last ran
   //
   public static long scanDate = 0;

   //
   //   Login and Tee Sheet Disply Statistics - Login, Member_sheet, Proshop_sheet (dsiplayed in Support)
   //
   public static String startDate = "";                 // date and time of first login

   public static int [] loginCountsMem = new int [24];  // one per hour of day
   public static int [] loginCountsPro = new int [24];

   public static int [] sheetCountsMem = new int [24];
   public static int [] sheetCountsPro = new int [24];

   //
   //   Email server info - ******** see also .../com/foretees/common/ProcessConstants **********
   //
   public static String HOST = "10.0.0.25";                 // mail.foretees.com - ForeTees mail server
   // public static String HOST = "216.243.184.88";                 // for dev server testing !!!

   public static String PORT = "20025";

   //public static String PORT = "587";                       // use 587 so they don't get blocked (server can use both 25 & 587)
   //public static String PORT = "25";                      // default port for emails

   public static String EFROM = "auto-send@foretees.com";   // fake return address

   public static String HEADER = "******************************************************************************************" +
                    "\nNOTICE: Please DO NOT reply to this email. " +
                    "\nThis message has been generated by the ForeTees system which cannot process a reply. " +
                    "\n**************************************************************************************** \n\n";

   public static String TRAILER = "\n\n\n*****************************************************************************************" +
                    "\nShould you prefer not to receive these email notifications, then login to the " +
                    "ForeTees Reservation System and select 'Settings'.  From there, select 'No' next to 'Do " +
                    "you wish to receive email notices of your tee times?' and click on 'Submit'.  If you " +
                    "require further assistance, contact your golf professionals. " +
                    "Thank you for using the ForeTees Reservation System." +
                    "\n********************************************************************************************";


   //********************************************************
   // Some constants for within this class
   //********************************************************
   //
   static int TIMER_SERVER = 101;                              // Timer Server ID value (timer server)
   static int TIMER_SERVER2 = 102;                             // additional Timer Server ID value (node 1, instance 1)

   static String rev = REVLEVEL;

   static int errorCount = 0;

   static String host = HOST;

   static String port = PORT;

   static String efrom = EFROM;

   static String header = HEADER;

   static String trailer = TRAILER;

   //
   //  Holiday dates
   //
   //    also, see com/foretees/common/ProcessConstants (Hdate_ labels) !!!!!!!!!!!!
   //
   static long memDay1 = ProcessConstants.memDay;        // Memorial Day (Monday)
   static long memDay2 = 20080526;
   static long july4th1 = ProcessConstants.july4b;       // Actual 4th of July
   static long july4th2 = 20090703;                      // change if needed
   static long july3rd1 = ProcessConstants.july4;        // Monday (IF necessary)
   static long july3rd2 = 20090704;                      // change if needed
   static long laborDay1 = ProcessConstants.laborDay;    // Labor Day (Monday)
   static long laborDay2 = 20080901;

   //
   //  Time value save area for Timer verification
   //
   static int inactTime = 0;
   static long min2Time = 0;      // date and time when 2 min timer should expire by (next expiration)
   static long min2Time2 = 0;     // date and time when 2 min timer should expire by (next expiration)
   static long min60Time = 0;     // date and time when 60 min timer should expire by (next expiration)

   //
   // Session length for member logins
   //
   public static final int MEMBER_TIMEOUT = 15 * 60;

   //
   // Get this year for copyright use
   //
   public static final int CURRENT_YEAR = new GregorianCalendar().get(Calendar.YEAR);      // get the year


   //**********************************************************
   // HeadTitle - Build HTML Header with a Title provided by caller.....
   //**********************************************************

   public static final String DOCTYPE = "<!DOCTYPE html>";
  //"<!DOCTYPE html>";
  //"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">";


 public static String HeadTitle(String title) {

     return HeadTitle2(title) + "\n</HEAD>\n";
 }

//  same as above except no end HEAD (/head) - allows unique js to be cleanly added in servlet
 
 public static String HeadTitle2(String title) {

     return getHeadTitle(title, ProcessConstants.SCRIPT_MODE_PROSHOP_HEADTITLE2);
    //String CDN = Common_Server.getContentServer();
/*
    return(DOCTYPE + "\n" +"<HTML>\n<!--Copyright notice:  This software (including any images, servlets, applets, photographs, animations, video, music and text incorporated into the software) " +
           "is the proprietary property of ForeTees, LLC and its use, modification and distribution are protected " +
           "and limited by United States copyright laws and international treaty provisions and all other applicable national laws. " +
           "\nReproduction is strictly prohibited.-->\n" +
           "<HEAD>" + //getBaseTag()  +
         //"<meta http-equiv=\"X-UA-Compatible\" content=\"IE=9\">" +
         //  "<script type=\"text/javascript\" src=\"" + ((CDN.equals("")) ? "" : CDN) + "/" +rev+ "/assets/jquery/jquery-1.10.2.min.js\"></script>" +  // jquery-1.7.1.min.js
         //  "<script type=\"text/javascript\" src=\"" + ((CDN.equals("")) ? "" : CDN) + "/" +rev+ "/assets/jquery/jquery-1.10.2.min.js\"></script>" + 
         //  "<script type=\"text/javascript\" src=\"" + ((CDN.equals("")) ? "" : CDN) + "/" +rev+ "/web%20utilities/foretees.js\"></script>" +
         //"<script type=\"text/javascript\" src=\"/" +rev+ "/web%20utilities/ftms.js\"></script>" +
         //  "<link rel=\"stylesheet\" href=\"" + ((CDN.equals("")) ? "" : CDN) + "/" +rev+ "/web%20utilities/foretees2.css\" type=\"text/css\">" +
         //"<link rel=\"stylesheet\" href=\"/" +rev+ "/web%20utilities/ftms.css\" type=\"text/css\">" +
            // Get proshop scripts via common skin for versioning, etc.
            Common_skin.getProshopScripts(ProcessConstants.SCRIPT_MODE_PROSHOP_HEADTITLE2) +
           "\n\n<!-- ************* Server Id = " +Common_Server.SERVER_ID+ " ************* -->\n\n" +
           "<TITLE>" + title + "</TITLE>\n");
      * 
      */
 }
 
 // Include scripts for editor
  public static String HeadTitleEditor(String title) {

      return getHeadTitle(title, ProcessConstants.SCRIPT_MODE_PROSHOP_HEADTITLEEDITOR);
 }
  
 public static String HeadTitleAdmin(String title) {

     return getHeadTitle(title, ProcessConstants.SCRIPT_MODE_PROSHOP_HEADTITLEADMIN);
    //String CDN = Common_Server.getContentServer();
/*
    return(DOCTYPE + "\n" +"<HTML><!--Copyright notice:  This software (including any images, servlets, applets, photographs, animations, video, music and text incorporated into the software) " +
           "is the proprietary property of ForeTees, LLC and its use, modification and distribution are protected " +
           "and limited by United States copyright laws and international treaty provisions and all other applicable national laws. " +
           "\nReproduction is strictly prohibited.-->\n" +
           "<HEAD>" +
            Common_skin.getProshopScripts(ProcessConstants.SCRIPT_MODE_PROSHOP_HEADTITLEADMIN) +
            //"<link rel=\"stylesheet\" href=\"" + ((CDN.equals("")) ? "" : CDN) + "/" +rev+ "/web%20utilities/foretees.css\" type=\"text/css\">" +
           //"<script type=\"text/javascript\" src=\"" + ((CDN.equals("")) ? "" : CDN) + "/" +rev+ "/web%20utilities/foretees.js\"></script>" +
           "\n\n<!-- ************* Server Id = " +Common_Server.SERVER_ID+ " ************* -->\n\n" +
           "<TITLE>" + title + "</TITLE></HEAD>\n");
      * 
      */
 }
 
   // Create common foretees header, with selected script mode
  public static String getHeadTitle(String title, int scriptMode) {

    //String CDN = Common_Server.getContentServer();

    return(DOCTYPE + "\n" +"<HTML>\n<!--Copyright notice:  This software (including any images, servlets, applets, photographs, animations, video, music and text incorporated into the software) " +
           "is the proprietary property of ForeTees, LLC and its use, modification and distribution are protected " +
           "and limited by United States copyright laws and international treaty provisions and all other applicable national laws. " +
           "\nReproduction is strictly prohibited.-->\n" +
           "<HEAD>" + //getBaseTag()  +
         //"<meta http-equiv=\"X-UA-Compatible\" content=\"IE=9\">" +
         //  "<script type=\"text/javascript\" src=\"" + ((CDN.equals("")) ? "" : CDN) + "/" +rev+ "/assets/jquery/jquery-1.10.2.min.js\"></script>" +  // jquery-1.7.1.min.js
         //  "<script type=\"text/javascript\" src=\"" + ((CDN.equals("")) ? "" : CDN) + "/" +rev+ "/assets/jquery/jquery-1.10.2.min.js\"></script>" + 
         //  "<script type=\"text/javascript\" src=\"" + ((CDN.equals("")) ? "" : CDN) + "/" +rev+ "/web%20utilities/foretees.js\"></script>" +
         //"<script type=\"text/javascript\" src=\"/" +rev+ "/web%20utilities/ftms.js\"></script>" +
         //  "<link rel=\"stylesheet\" href=\"" + ((CDN.equals("")) ? "" : CDN) + "/" +rev+ "/web%20utilities/foretees2.css\" type=\"text/css\">" +
         //"<link rel=\"stylesheet\" href=\"/" +rev+ "/web%20utilities/ftms.css\" type=\"text/css\">" +
            // Get proshop scripts via common skin for versioning, etc.
            Common_skin.getProshopScripts(scriptMode) +
           "\n\n<!-- ************* Server Id = " +Common_Server.SERVER_ID+ " ************* -->\n\n" +
           "<TITLE>" + title + "</TITLE>\n");
 }



 public static String HeadTitleMobile(String title) {

    return("" +
           "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" +
           "<html xmlns=\"http://www.w3.org/1999/xhtml\">" +
           "<head>" +
           "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" />" +
           "<meta name=\"viewport\" id=\"viewport\" content=\"width=device-width, user-scalable=no\" />" +
           "<title>" +title+ "</title>" +
           "<LINK REL=StyleSheet HREF=\"/" +rev+ "/mobile/style.css\" TYPE=\"text/css\" MEDIA=screen>" +
           "</head><body>\n");
 }

      //      "<meta name=\"viewport\" id=\"viewport\" content=\"width=device-width, user-scalable=yes\" />" +
      //  Changed this meta tag to correct a problem where the iPhone screen was showing as zoomed in
      //  after the user selected a drop-down list.
      //     "<meta name=\"viewport\" id=\"viewport\" content=\"initial-scale=1.0, user-scalable=yes\" />" +  // tried this too, but only worked if scalable=no - but landscape mode was bad


 public static String BannerMobile() {

    return("" +
           "<div id=\"header\">" +
           "<div id=\"logo\">" +
           "<div id=\"logout\"><a href=\"Logout\">Log Out</a></div>" +
           "<div id=\"home\"><a href=\"/" +rev+ "/mobile/member_mobile_home.html\">Home </a></div>" +
           "</div>" +
           "</div>\n");
 }


 public static String BannerMobileSlot() {

    return("" +
           "<div id=\"header\">" +
           "<div id=\"logo\">" +
           "</div>" +
           "</div>\n");         // Banner without links - for Common_mobile.doSlot
 }


 // *****************************************************************************
 // getMemberMainMenu - build the Member drop-down menu for navigation bar
 // *****************************************************************************

 public static void getMemberMainMenu(HttpServletRequest req, PrintWriter out, String caller)
 {

    //The following AllWebMenus code must always be placed right AFTER the BODY tag
    //This is needed for the Member tabs

    //These are the default values
    String mnuId = "xawmMenuPathImg-foreteesMember";
    String mnuImgNameAndId = "awmMenuPathImg-foreteesMember";
    String mnuName = "foreteesMember";

    HttpSession session = null;
    session = req.getSession(false);  // Get user's session object (no new one)

    if (session == null) {

        logError("getMemberMainMenu: Error: No Session Found.");
        return;
    }

    int sess_activity_id = (Integer)session.getAttribute("activity_id");

    int tmp_tlt = 0;

    try { tmp_tlt = (Integer)session.getAttribute("tlt"); }
    catch (Exception ignore) { }

    boolean IS_TLT = (tmp_tlt == 1) ? true : false;

    if (IS_TLT) {

        mnuId = "xawmMenuPathImg-foreteesMemberTLT";
        mnuImgNameAndId = "awmMenuPathImg-foreteesMemberTLT";
        mnuName = "foreteesMemberTLT";

    /*           // remove and see if anyone complains!  3/05/08
    } else {

        // if called by other web site (MFirst)
        if (caller.equals( "MEMFIRST" ) || caller.equals( "AMO" ) || caller.equals( "CLUBESSENTIAL" )) {

            //request came from an external site, remove the email tab
            mnuId = "xawmMenuPathImg-foreteesMemberCond1";
            mnuImgNameAndId = "awmMenuPathImg-foreteesMemberCond1";
            mnuJsFileName = "foreteesMemberCond1.js";
        }
     */
     // end if IS_TLT

    } else if ( sess_activity_id != 0 ) {

       mnuId = "xawmMenuPathImg-foreteesMemberGenRez";
       mnuImgNameAndId = "awmMenuPathImg-foreteesMemberGenRez";
       mnuName = "foreteesMemberGenRez";

    }

    out.println("<span id='" + mnuId + "' style='position:absolute;top:-50px'><img ");
    out.println("name='" + mnuImgNameAndId + "' id='" + mnuImgNameAndId + "' src='/" +rev+ "/web%20utilities/member/awmmenupath.gif' alt=''></span>");
    out.println("<script type='text/javascript'>var MenuLinkedBy='AllWebMenus [4]',awmMenuName='" + mnuName + "',awmBN='854';awmAltUrl='';</script>");
    out.println("<script type='text/javascript' charset='UTF-8' src='/" +rev+ "/web%20utilities/member/" + mnuName + ".js'></script>");
    out.println("<script type='text/javascript'>awmBuildMenu();</script>");
    //out.println("<hr class=\"gblHdr\">");
    out.println("<span style=\"position:absolute;top:-100px\"><form name=\"mnuHlp\" target=\"bot\">");
    out.println("<input type=\"hidden\" name=\"revLevel\" value=\"" + rev + "\"></form></span>");
 }


 // *****************************************************************************
 // getMemberSubMenu - build the Member drop-down sub-menus for navigation bar
 // *****************************************************************************

 public static void getMemberSubMenu(HttpServletRequest req, PrintWriter out, String caller)
 {

    //The following AllWebMenus code must always be placed right AFTER the BODY tag
    //within the "bot" Frame.  This is needed for the Member sub menus

    //These are the default values
    String mnuId = "xawmMenuPathImg-foreteesMember_sub";
    String mnuImgNameAndId = "awmMenuPathImg-foreteesMember_sub";
    String mnuName = "foreteesMember_sub";

    HttpSession session = null;
    session = req.getSession(false);  // Get user's session object (no new one)

    if (session == null) {

        logError("getMemberSubMenu: Error: No Session Found.");
        return;
    }

    int sess_activity_id = (Integer)session.getAttribute("activity_id");

    int tmp_tlt = 0;
    String macOpt = "";

    try {

       tmp_tlt = (Integer)session.getAttribute("tlt");
       macOpt = (String)session.getAttribute("mac");

    }
    catch (Exception ignore) {      // default to zero if error
    }

    boolean IS_TLT = (tmp_tlt == 1) ? true : false;

    if (IS_TLT) {

       mnuId = "xawmMenuPathImg-foreteesMemberTLT_sub";
       mnuImgNameAndId = "awmMenuPathImg-foreteesMemberTLT_sub";
       mnuName = "foreteesMemberTLT_sub";

    /*           // remove and see if anyone complains!  3/05/08
    } else {

       // if called by other web site (MFirst)
       if (caller.equals( "MEMFIRST" ) || caller.equals( "AMO" ) || caller.equals( "CLUBESSENTIAL" )) {

         //request came from an external site
          mnuId = "xawmMenuPathImg-foreteesMemberCond1_sub";
          mnuImgNameAndId = "awmMenuPathImg-foreteesMemberCond1_sub";
          mnuJsFileName = "foreteesMemberCond1_sub.js";
       }
    */
        // end if IS_TLT

    } else if ( sess_activity_id != 0 ) {

       mnuId = "xawmMenuPathImg-foreteesMemberGenRez_sub";
       mnuImgNameAndId = "awmMenuPathImg-foreteesMemberGenRez_sub";
       mnuName = "foreteesMemberGenRez_sub";

    }

    //<!-- DO NOT MOVE! The following AllWebMenus code must always be placed right AFTER the BODY tag-->
    out.println("<span id='" + mnuId + "' style='position:absolute;top:-50px'><img ");
    out.println("name='" + mnuImgNameAndId + "' id='" + mnuImgNameAndId + "' src='/" +rev+ "/web%20utilities/member/awmmenupath.gif' alt=''></span>");
    out.println("<script type='text/javascript'>var MenuLinkedBy='AllWebMenus [4]',awmMenuName='" + mnuName + "',awmBN='900';awmAltUrl='';</script>");
    out.println("<script type='text/javascript' charset='UTF-8' src='/" + rev + "/web%20utilities/member/" + mnuName + ".js'></script>");
    out.println("<script type='text/javascript'>awmBuildMenu();</script>");
    out.println("<span id='" + mnuId + "' style='position:absolute;top:-100px'><form name=\"mnuHlp\" target=\"bot\">");
    out.println("<input type=\"hidden\" name=\"revLevel\" value=\"" + rev + "\"></form></span>");

 }


 // *****************************************************************************
 // getProshopMainMenu - build the Proshop drop-down menu for navigation bar
 // *****************************************************************************

 public static void getProshopMainMenu(HttpServletRequest req, PrintWriter out, int lottery) {


    String CDN = Common_Server.getContentServer();

    //The following AllWebMenus code must always be placed right AFTER the BODY tag
    //This is needed for the Proshop tabs

    //These are the default values
    String mnuId = "xawmMenuPathImg-foreteesProshop";
    String mnuImgNameAndId = "awmMenuPathImg-foreteesProshop";
    String mnuName = "foreteesProshop";

    HttpSession session = null;
    session = req.getSession(false);  // Get user's session object (no new one)

    if (session == null) {

        logError("getMemberSubMenu: Error: No Session Found.");
        return;
    }

        
    boolean enableAdvAssist = Utilities.enableAdvAssist(req);   // Now passed by Proshop_maintop !!

    int sess_activity_id = (Integer)session.getAttribute("activity_id");
    String club = (String)session.getAttribute("club");

    int tmp_tlt = (Integer)session.getAttribute("tlt");
    boolean IS_TLT = (tmp_tlt == 1) ? true : false;
    
    
    //
    //  Custom to allow all menu tabs for The CC of Brookline for Mobile devices (iPads)
    //
    if (sess_activity_id == 0 && (club.equals("tcclub") || club.equals("manorcc") || club.equals("demov4"))) {
       
       enableAdvAssist = true;     // make sure we don't treat as mobile (true = NOT mobile)
    }
    

    if (!enableAdvAssist && !IS_TLT && sess_activity_id == 0) {

        // golf iPad menus
        mnuId = "xawmMenuPathImg-foreteesProshop_iPad";
        mnuImgNameAndId = "awmMenuPathImg-foreteesProshop_iPad";
        mnuName = "foreteesProshop_iPad";

    } else if ( sess_activity_id != 0 ) {

        //  we are in activity mode - load the generic activity menus
        mnuId = "xawmMenuPathImg-foreteesProshopGenRez";
        mnuImgNameAndId = "awmMenuPathImg-foreteesProshopGenRez";
        mnuName = "foreteesProshopGenRez";

    } else if (IS_TLT) {

        mnuId = "xawmMenuPathImg-foreteesProshopTLT";
        mnuImgNameAndId = "awmMenuPathImg-foreteesProshopTLT";
        mnuName = "foreteesProshopTLT";

    } else if (lottery == 0) {

        //  lottery not supported, remove the lottery tab
        mnuId = "xawmMenuPathImg-foreteesProshopNoLot";
        mnuImgNameAndId = "awmMenuPathImg-foreteesProshopNoLot";
        mnuName = "foreteesProshopNoLot";

    }

    out.print("<span id='" + mnuId + "' style='position:absolute;top:-50px'><img ");
    out.println("name='" + mnuImgNameAndId + "' id='" + mnuImgNameAndId + "' src='" + ((CDN.equals("")) ? "" : CDN) + "/" +rev+ "/web%20utilities/proshop/awmmenupath.gif' alt=''></span>");


    //
    // FOR TESTING THE SERVER SIDE MENUS
    //

/*
    if (Common_Server.SERVER_ID == 4) {
            out.println("<script type=\"text/javascript\">var MenuLinkedBy=\"AllWebMenus [4]\",awmMenuName=\"foreteesProshop\",awmBN=\"828\";awmAltUrl=\"\";</script>");
            out.println("<script charset=\"UTF-8\" src=\"/v5/assets/menus/proshop/foreteesProshop.js\" type=\"text/javascript\"></script>");
            out.println("<script type=\"text/javascript\">");
            out.println("awmBuildMenu();");

            //------- Add your Server-Side code right after this comment ----------



            out.println("if (typeof(foreteesProshop)!=\"undefined\") ProduceMenu(foreteesProshop);");
            out.println("</script>");

    } else {
*/

    
    
    out.println("<script type='text/javascript'>var MenuLinkedBy='AllWebMenus [4]',awmMenuName='" + mnuName + "',awmBN='922';awmAltUrl='';</script>");
    out.println("<script type='text/javascript' charset='UTF-8' src='" + ((CDN.equals("")) ? "" : CDN) + "/" +rev+ "/web%20utilities/proshop/" + mnuName + ".js'></script>");
    out.println("<script type='text/javascript'>awmBuildMenu();</script>");

    // if (req.getHeader("User-Agent").indexOf("MSIE 10") > 0 && req.getHeader("User-Agent").indexOf("Touch") > 0) { }

    // fix for awm menus and ie10 w/ touch
    // ideally we only do this for ie10 & touch but detecting touch is not working
    out.println("" +
            "<script type='text/javascript'>" +
                "$(document).ready(function(){ " +
                    //"if ($.browser.msie && $.browser.version == 10) {" + // $.browser.msie && $.browser.version == 10 && window.navigator.msMaxTouchPoints
                        "$(\"[id^='AWMEL']\").attr('aria-haspopup','true'); " +
                    //"}" +
                "});" +
            "</script>");


    //out.println("<hr class=\"gblHdr\">");
    out.println("<span id='" + mnuId + "' style='position:absolute;top:-100px'><form name=\"mnuHlp\" action=\"\">");                          // to be filled by exeMenuAction js
    out.println("<input type=\"hidden\" name=\"revLevel\" value=\"" + rev + "\"></form></span>");
 }


 // *****************************************************************************
 // getProshopSubMenu - build the Proshop drop-down sub-menus for navigation bar
 // *****************************************************************************

 public static void getProshopSubMenu(HttpServletRequest req, PrintWriter out, int lottery) {


    String CDN = Common_Server.getContentServer();
    
    // The following AllWebMenus code must always be placed right AFTER the BODY tag
    // within the "bot" Frame.  This is needed for the sub menus

    // these are the default values
    String mnuId = "xawmMenuPathImg-foreteesProshop_sub";
    String mnuImgNameAndId = "awmMenuPathImg-foreteesProshop_sub";
    String mnuName = "foreteesProshop_sub";

    HttpSession session = null;
    session = req.getSession(false);  // Get user's session object (no new one)

    if (session == null) {

        logError("getMemberSubMenu: Error: No Session Found.");
        return;
    }

    boolean enableAdvAssist = Utilities.enableAdvAssist(req);

    int sess_activity_id = (Integer)session.getAttribute("activity_id");
    String club = (String)session.getAttribute("club");
    

    int tmp_tlt = (Integer)session.getAttribute("tlt");
    boolean IS_TLT = (tmp_tlt == 1) ? true : false;


    //
    //  Custom to allow all menu tabs for The CC of Brookline for Mobile devices (iPads)
    //
    if (sess_activity_id == 0 && (club.equals("tcclub") || club.equals("manorcc") || club.equals("demov4"))) {
       
       enableAdvAssist = true;     // make sure we don't treat as mobile (true = NOT mobile)
    }
    

    if (!enableAdvAssist && !IS_TLT && sess_activity_id == 0) {

        mnuId = "xawmMenuPathImg-foreteesProshop_iPad_sub";
        mnuImgNameAndId = "awmMenuPathImg-foreteesProshop_iPad_sub";
        mnuName = "foreteesProshop_iPad_sub";

    } else if ( sess_activity_id != 0 ) {

        //  we are in activity mode - load the generic activity menus
        mnuId = "xawmMenuPathImg-foreteesProshopGenRez_sub";
        mnuImgNameAndId = "awmMenuPathImg-foreteesProshopGenRez_sub";
        mnuName = "foreteesProshopGenRez_sub";

    } else if (IS_TLT) {

        mnuId = "xawmMenuPathImg-foreteesProshopTLT_sub";
        mnuImgNameAndId = "awmMenuPathImg-foreteesProshopTLT_sub";
        mnuName = "foreteesProshopTLT_sub";

    } else if (lottery == 0) {

        //  lottery not supported
        mnuId = "xawmMenuPathImg-foreteesProshopNoLot_sub";
        mnuImgNameAndId = "awmMenuPathImg-foreteesProshopNoLot_sub";
        mnuName = "foreteesProshopNoLot_sub";

    } // end if IS_TLT

    //<!-- DO NOT MOVE! The following AllWebMenus code must always be placed right AFTER the BODY tag-->
    out.println("<span id='" + mnuId + "' style='position:absolute;top:-50px'><img ");
    out.println("name='" + mnuImgNameAndId + "' id='" + mnuImgNameAndId + "' src='" + ((CDN.equals("")) ? "" : CDN) + "/" +rev+ "/web%20utilities/proshop/awmmenupath.gif' alt=''></span>");
    out.println("<script type='text/javascript'>var MenuLinkedBy='AllWebMenus [4]',awmMenuName='" + mnuName + "',awmBN='922';awmAltUrl='';</script>");
    out.println("<script type='text/javascript' charset='UTF-8' src='" + ((CDN.equals("")) ? "" : CDN) + "/" + rev + "/web%20utilities/proshop/" + mnuName + ".js'></script>");
    out.println("<script type='text/javascript'>awmBuildMenu();</script>");

    out.println("<script type='text/javascript'>$(document).ready(function(){ $(\"[id^='AWMEL']\").attr('aria-haspopup','true'); });</script>");
/*
    // fix for awm menus and ie10 w/ touch
    out.println("" +
            "<script type='text/javascript'>" +
                "$(document).ready(function(){ " +
                    //"if ($.browser.msie && document.documentMode == 10) {" + // $.browser.msie && $.browser.version == 10 && window.navigator.msMaxTouchPoints
                        //"alert('document.documentMode='+document.documentMode+', navigator.msMaxTouchPoints='+navigator.msMaxTouchPoints+', window.ontouchstart='+window.ontouchstart);" +
                        "alert('ie>10');" + 
                        "$(\"[id^='AWMEL']\").attr('aria-haspopup','true'); " +
                    //"}" +
                "});" +
            "</script>");
*/
    out.println("<span id='" + mnuId + "' style='position:absolute;top:-100px'><form name=\"mnuHlp\">");               // to be filled by exeMenuAction js
    out.println("<input type=\"hidden\" name=\"revLevel\" value=\"" + rev + "\"></form></span>");

 }


 // *********************************************************
 // Get a DB connection from the session or new
 // *********************************************************
 //
// public synchronized static Connection getCon(HttpSession sess) {
 public static Connection getCon(HttpSession sess) {

   Connection con = null;

   //
   // Get the connection holder saved in the session object
   //
   ConnHolder holder = (ConnHolder) sess.getAttribute("connect");

   if (holder == null) {

      //
      // Lost the connection - get and save a new one
      //
      String club = (String)sess.getAttribute("club");   // get club name

      if (!club.equals( "" )) {

         try {
            //con = dbConn.Connect(club);
            con = Connect.getCon(club);
            holder = new ConnHolder(con);            // create a new holder from ConnHolder class
            sess.setAttribute("connect", holder);    // save DB connection holder

         }
         catch (Exception exc) {

            logError("Fatal Error: Lost db connection and could not reconnect! club=" + club);
            return null;
         }

      } else {

          logError("Fatal Error: Lost db connection and no valid session!");
          return null;
      }

   } else {

      con = holder.getConn();      // get the connection and return it

   }

   //
   // Let check to make sure the con is valid before returning it
   //
   try {

       if ( con.isValid(1) == false ) {

          //
          // Lost the connection - get and save a new one
          //
          String club = (String)sess.getAttribute("club");   // get club name

          if (!club.equals( "" )) {

             try {

                //con = dbConn.Connect(club);
                con = Connect.getCon(club);
                holder = new ConnHolder(con);            // create a new holder from ConnHolder class
                sess.setAttribute("connect", holder);    // save DB connection holder

             } catch (Exception exc) {

                logError("Fatal Error: Invalid db connection and could not reconnect! club=" + club);
                return null;
             }

          } else {

              logError("Fatal Error: Invalid db connection and no valid session!");
              return null;
          }

       }

   } catch (Exception ignore) {

   }

   return con;

 }


 // *********************************************************
 // verifyMem - Check for illegal access by user
 // *********************************************************
 
 // Only verify the session, do not send output
 
public static HttpSession verifyMem(HttpServletRequest req, PrintWriter out) {
    
    Map<String, Object> verifyMap = verifyMem(req);
    HttpSession session = (HttpSession) verifyMap.get("session");
    String htmlResponse = (String) verifyMap.get("htmlResponse");
    if(htmlResponse != null && htmlResponse.length() > 0){
        out.print(htmlResponse);
        out.close();
    } else if(session != null) {
       String tpa_check = req.getParameter("sso_tpa");
       String tpa_id = (String)session.getAttribute("session_uuid");
       if( tpa_check != null && tpa_id != null && tpa_check.length() > 0 && !tpa_check.equals(tpa_id)){
           // This was an SSO, with a "tpa" id to detect if the session was properly established
           // when in an iframe
           // If it wasn't, we need to deal with it by outputing a page that will deal with it, and
           // cancel this session
           session.setAttribute("sso_tpa_mode", "auth");
           Common_skin.outputHeader((String)verifyMap.get("club"), (Integer)verifyMap.get("activity_id"), "", true, out, req);
           out.println("</html>");
           out.close();
           session.invalidate();
           
           session = null;
           
       } else {
           session.removeAttribute("sso_tpa_mode");
       }
           /*else if(tpa_check != null && tpa_id != null) {
           session.setAttribute("sso_tpa_mode",tpa_check+"|"+tpa_id);
       } else if(tpa_check != null){
           session.setAttribute("sso_tpa_mode","tpa_check|"+tpa_id);
       } else if(tpa_id != null){
           session.setAttribute("sso_tpa_mode","tpa_id|"+tpa_id);
       }*/
    }
    
    return session;
      
}
 
 // Verify session and return map coontaining session and/or error
 public static Map<String, Object> verifyMem(HttpServletRequest req) {

   String id = "foretees";                   // session id

   String agent = req.getHeader("User-Agent");  // Get user's browser info for error msg

   String errorMsg = "Verify Member: User Verification Error (cookies). Agent: " + agent + ", ";

   HttpSession session = null;
   
   StringBuilder resp = new StringBuilder();
   Map<String,Object> respMap = new LinkedHashMap<String,Object>();
   
   // Check if this is a SSO request
     if (req.getParameter("sso_uid") != null) {
         final long benchmark_start = System.currentTimeMillis();
         Map<String, Object> userMap = Common_webapi.loadUser(req, "SSO", "", "");
         if (!(Boolean) userMap.get("is_error")) {
             // Looks like a valid SSO request -- establish the session using SSO
             Connection con = (Connection) userMap.get("db_connection");

             String mship = (String) userMap.get("mship");
             String mtype = (String) userMap.get("mtype");
             String wc = (String) userMap.get("wc");
             String club = (String) userMap.get("club");
             String clubid = (String) userMap.get("clubid");
             String name = (String) userMap.get("name");
             String username = (String) userMap.get("username");
             String user = (String) userMap.get("user");
             String remote_ip = (String) userMap.get("remote_ip");
             String caller = ProcessConstants.FT_PREMIER_CALLER;
             if (req.getParameter("sso_caller") != null) {
                 caller = req.getParameter("sso_caller");
             }
             //out.print("<!-- " + user+"/"+clubid + "-->");
             // get the activity id
            int activity_id = 0;
            String tmp = req.getParameter("s_a") == null ? "" : req.getParameter("s_a");
            try {
                activity_id = Integer.parseInt(tmp);
            } catch (Exception ignore) {}
            try {
                Map<String, Object> result = Common_webapi.performSSO(req, null, activity_id, user, username, club, caller, mship, mtype, name, wc, remote_ip, benchmark_start, con);
                respMap.put("ssoMap",result);
            } catch (Exception ignore) {}
             
         }
     }
   session = req.getSession(false);  // Get user's session object (no new one)
   
   String sess_id = "";
   String user = "";
   String name = "";
   String club = "";
   String caller = "";
   Integer app_mode = 0;
   Boolean rwd = false; // Responsive Web Design mode

   if (session == null) {

      //logError(errorMsg + " No Session Found.");

   } else {

      // trap an error if one occurs and fail (caller was null on an expired session - not sure how/why this happens)
      try {

          sess_id = (String)session.getAttribute("sess_id");   // get session id
          user = (String)session.getAttribute("user");         // get username
          name = (String)session.getAttribute("name");         // get user's full name
          club = (String)session.getAttribute("club");         // get club's name
          caller = (String)session.getAttribute("caller");     // get caller (none, or AMO or MFirst)
          rwd = (Boolean)session.getAttribute("rwd");     // get Responsive Web Design mode
          
          if (rwd == null) { 
              rwd = false;
          }

      } catch (Exception ignore) {

          session = null;

      }

      if (session == null || sess_id == null || user == null) {

         logError(errorMsg + " Invalid (null) session info: Session id = " +sess_id+ ", User = " +user+ ", Name = " +name+ ", Club = " +club+ ", Caller = " +caller);

         session = null;

      } else if (!sess_id.equals( id ) || user.equals( "" )) {

         logError(errorMsg + " Invalid session info: Session id = " +sess_id+ ", User = " +user+ ", Name = " +name+ ", Club = " +club+ ", Caller = " +caller);

         session = null;
      }
   }
   
   if(req.getParameter("s_c") != null){
       String club_test = req.getParameter("s_c");
       if(club == null || club.length() < 1 || club_test.equals(club)){
           club = club_test;
       } else if(session != null) {
           // club mismatch -- error out
           session.invalidate();
           session = null;
           club = club_test;
       }
       
   }

   if(req.getParameter("s_rwdon") != null){
       rwd = true;
   }
   
   if(req.getParameter("s_rwdoff") != null){
       rwd = false;
   }
   
   if(rwd){
       // If responsive is on, we should probably turn off old mobile here -- but
       // need to think through the ramifications of doing so.
   }
  
   if (session != null) {
       
       respMap.put("session", session);
       Integer set_activity_id = 0;
       if(req.getParameter("s_a") != null){
           try {
               set_activity_id = Integer.parseInt(req.getParameter("s_a"));
               session.setAttribute("activity_id", set_activity_id);
               
           } catch (NumberFormatException nfe) {
               
           }
           
       }
       // App mode is a 32 bit binary map
       //
       // See ProcessContants APPMODE_* for definition
       // 
       if(req.getParameter("s_m") != null){
           try {
               app_mode = Integer.parseInt(req.getParameter("s_m"));

           } catch (NumberFormatException nfe) {
               
           }
           
       }
       
       // Set script debug mode
       if(req.getParameter("s_sdbg") != null){
           String scriptDebug = req.getParameter("s_sdbg");
           if(scriptDebug.equals("on")){
               session.setAttribute("script_debug", true);
           } else {
               session.setAttribute("script_debug", false);
           }
       }
       
       session.setAttribute("app_mode", app_mode);
       
       respMap.put("activity_id", set_activity_id);
       respMap.put("club", club);
       respMap.put("user", user);
       // Stamp our last activity
       java.util.Date serverTime = new java.util.Date();
       Long timeStamp = serverTime.getTime();
       session.setAttribute("last_activity_time", timeStamp);
       // Store in session
       session.setAttribute("rwd", rwd);

       
   } else {

      if (!club.equals("")) caller = Utilities.getClubCaller(club);

      if (!club.equals("") && !caller.equals("") && !caller.equals("FLEXWEBFT")) { // club was found in url and club does not use seamless
          
          resp.append("<!doctype html>");
          resp.append("<html>");
          resp.append("<head lang=\"en-US\">");
          //resp.append("<meta http-equiv=\"Refresh\" content=\"5; url=" + club + "\">");
          resp.append("<title>Access Error</title>");
          resp.append("</head>");
          resp.append("<BODY><CENTER>");
          resp.append("<H2>Access Error - Please Read</H2>");
          resp.append("Sorry, your session either timed out, you didn't login, or your computer does not allow the use of Cookies.");
          resp.append("<BR><BR>This site requires the use of Cookies for security purposes.");
          resp.append("<BR>We use them to verify your session and prevent unauthorized access.");
          resp.append("<BR><BR><HR width=\"500\">");
          resp.append("<b>NOTE:</b> You must be logged in to access the ForeTees system. You cannot bookmark a page");
          resp.append("<BR>within ForeTees and then return to it later without logging in.  If you access ForeTees");
          resp.append("<BR>from your club web site, then you must login to the web site.  If you access ForeTees");
          resp.append("<BR>directly, then you must do so through the ForeTees Login page.");
          resp.append("<BR><HR width=\"500\"><BR>");
          resp.append("If you have tried all of the above and still receive this message,");
          resp.append("<BR>please email us at <a href=\"mailto:support@foretees.com\">support@foretees.com</a>.");
          resp.append("<BR><b>Provide your name or member number, the name of your club and a detailed description of your problem.</b>");
          resp.append("<BR>Thank you.");
          resp.append("<BR><BR>");
          resp.append("<h3><a href=\"/" + club + "\" target=\"_top\">Return to Login Page</a></h3>");
          resp.append("<BR><BR>");
          resp.append("<CENTER>Server: " + Common_Server.SERVER_ID + "</CENTER>");
          resp.append("</CENTER></BODY></HTML>");
  

      } else if (caller.equals("FLEXWEBFT")) {

          resp.append("<!DOCTYPE html>");
          resp.append("<html>");
          resp.append("<head>");
          resp.append("<meta name=\"application-name\" content=\"ForeTees\">");
          resp.append("<meta name=\"ft-server-id\" content=\"" + ProcessConstants.SERVER_ID + "\">");
          resp.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">");
          resp.append("</head>");
          resp.append("<body>");
          resp.append("<br><center>");
          resp.append("<h2>Sorry, your session has either expired or was not established correctly.<br><br>Please use the main menu to restart the reservations session.</h2>");
          resp.append("<h3>If you are seeing this message immediately or continually, you may need to adjust your browser's cookie settings.<br>Please <a href=\"/v5/premier_cookies.htm\">click here for further information</a>.</h3>");

          if (club.equals("parkmeadowscc")) {
            //resp.append("<h3>You can also access ForeTees using the previous method by <a href=\"http://www.parkmeadowscc.com/Members/GOLF/Online-Tee-Times-154.html\" target=\"_top\">clicking here</a>.</h3>");
            resp.append("<h3>If you are uncomfortable changing your browser settings to always allow, <a href=\"http://www.parkmeadowscc.com/Members/GOLF/Online-Tee-Times-154.html\" target=\"_top\">please click here</a> to access ForeTees.</h3>");
          }
          
          resp.append("</center>");
          resp.append("</body></html>");
 

      } else {
          
          resp.append("<HTML>");
          resp.append("<HEAD>");
          resp.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
          resp.append("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
          resp.append("<meta http-equiv=\"Content-Style-Type\" content=\"text/css\">");
          resp.append("<TITLE>Access Error</TITLE></HEAD>");
          resp.append("<BODY><CENTER>");
          resp.append("<H2>Access Error - Please Read</H2>");
          resp.append("Sorry, your session either timed out, you didn't login, or your computer does not allow the use of Cookies.");
          resp.append("<BR><BR>This site requires the use of Cookies for security purposes.");
          resp.append("<BR>We use them to verify your session and prevent unauthorized access.");
          resp.append("<BR><BR>Please check your 'Privacy' settings, under 'Tools', 'Internet Options'");
          resp.append("<BR>(for MS Internet Explorer).  This must be set to 'Medium High' or lower.");
          resp.append("<BR><BR>If you have a firewall, please check its settings as well.");
          resp.append("<BR><BR><HR width=\"500\">");
          resp.append("<b>NOTE:</b> You must be logged in to access the ForeTees system. You cannot bookmark a page");
          resp.append("<BR>within ForeTees and then return to it later without logging in.  If you access ForeTees");
          resp.append("<BR>from your club web site, then you must login to the web site.  If you access ForeTees");
          resp.append("<BR>directly, then you must do so through the ForeTees Login page.");
          resp.append("<BR><HR width=\"500\"><BR>");
          resp.append("If you have tried all of the above and still receive this message,");
          resp.append("<BR>please email us at <a href=\"mailto:support@foretees.com\">support@foretees.com</a>.");
          resp.append("<BR><b>Provide your name or member number, the name of your club and a detailed description of your problem.</b>");
          resp.append("<BR>Thank you.");
          resp.append("<BR><BR>");
          resp.append("<a href=\"Logout\" target=\"_top\">Return</a>");
          resp.append("<BR><BR>");
          resp.append("<CENTER>Server: " + Common_Server.SERVER_ID + "</CENTER>");
          resp.append("</CENTER></BODY></HTML>");


      }

   }
   
   respMap.put("htmlResponse", resp.toString());
   return respMap;
 }

 //
 //  same as above wihtout the error msg - called by Member_maintop (to prevent double error msg)
 //
 public static HttpSession verifyMem2(HttpServletRequest req, PrintWriter out) {

   String id = "foretees";                   // session id

   HttpSession session = null;

   session = req.getSession(false);  // Get user's session object (no new one)

   if (session != null) {

      String sess_id = (String)session.getAttribute("sess_id");   // get session id
      String user = (String)session.getAttribute("user");         // get username
      String name = (String)session.getAttribute("name");         // get user's full name
      String club = (String)session.getAttribute("club");         // get club's name
      String caller = (String)session.getAttribute("caller");     // get caller (none, or AMO or MFirst)

      if (!sess_id.equals( id ) || user.equals( "" )) {

         session = null;
      }
   }
   return session;
 }


 // *********************************************************
 // verifyHotel - Check for illegal access by user
 // *********************************************************

 public static HttpSession verifyHotel(HttpServletRequest req, PrintWriter out) {


   String id = "foretees";                   // session id

   String agent = req.getHeader("User-Agent");  // Get user's browser info for error msg

   String errorMsg = "Member_select: User Verification Error (cookies). Agent: " + agent + ", ";


   HttpSession session = null;

   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      //
      //  save error message in /v2/error.txt
      //
      errorMsg = errorMsg + " Cannot access session info - cookies blocked??";    // build error msg

//      logError(errorMsg);                           // log it

   } else {

      String sess_id = (String)session.getAttribute("sess_id");   // get session id
      String user = (String)session.getAttribute("user");         // get username
      String name = (String)session.getAttribute("name");         // get user's full name
      String club = (String)session.getAttribute("club");         // get club's name
      String caller = (String)session.getAttribute("caller");     // get caller (none, or AMO or MFirst)

      if (!sess_id.equals( id ) || user.equals( "" )) {

         //
         //  save error message in /v2/error.txt
         //
         errorMsg = errorMsg + " Invalid session info: Session id = " +sess_id+ ", User = " +user+ ", Name = " +name+ ", Club = " +club+ ", Caller = " +caller;

         logError(errorMsg);                           // log it

         session = null;
      }
   }

   if (session == null) {

//      out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
      out.println("<HTML>");
      out.println("<HEAD>");
      out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
      out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
      out.println("<meta http-equiv=\"Content-Style-Type\" content=\"text/css\">");
      out.println("<TITLE>Access Error - Redirect</TITLE></HEAD>");
      out.println("<BODY><CENTER>");
      out.println("<BR><H2>Access Error</H2><BR>");
      out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
      out.println("<BR>This site requires the use of Cookies for security purposes.");
      out.println("<BR>We use them to verify your session and prevent unauthorized access.");
      out.println("<BR><BR>Please check your 'Privacy' settings, under 'Tools', 'Internet Options'");
      out.println("<BR>(for MS Internet Explorer).  This must be set to 'Medium High' or lower.");
      out.println("<BR><BR>");
      out.println("<BR>If you have changed or verified the setting above and still receive this message,");
      out.println("<BR>please email us at <a href=\"mailto:support@foretees.com\">support@foretees.com</a>.");
      out.println("<BR>Provide your name or member number and the name of your club.  Thank you.");
      out.println("<BR><BR>");
      out.println("<a href=\"Logout\" target=\"_top\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
   }

   return session;
 }

 // ****************************************************************************
 // verifyProAccess - Check if proshop user has access to a specific feature
 //                   Returns true if access is allowed
 // ****************************************************************************
 public static boolean verifyProAccess(HttpServletRequest req, String featureID, Connection con, PrintWriter out) {

    boolean allow = false; // default to not allow access

    HttpSession session = null;

    String user = "";

    int sess_activity_id = 0;

    PreparedStatement stmt = null;
    ResultSet rs = null;

    session = req.getSession(false);  // Get user's session object (no new one)

    if (session != null) {

        user = (String)session.getAttribute("user");   // get username

        if (Utilities.isFTProshopUser(user)) {

            allow = true;

        } else {

            //
            // See what activity mode we are in
            //
            try { sess_activity_id = (Integer)session.getAttribute("activity_id"); }
            catch (Exception ignore) { }

            try {

                stmt = con.prepareStatement("" +
                        "SELECT * FROM login2 WHERE username = ? AND activity_id = ?");
                stmt.clearParameters();
                stmt.setString(1, user);
                stmt.setInt(2, sess_activity_id);
                rs = stmt.executeQuery();

                if (rs.next()) {

                     allow = (rs.getInt(featureID) == 1);

                } /* else {     // Just return false and let the calling method handle the failure instead of overriding it with this error message.

                    // *should* this ever occur - maybe if they delete a proshop user?

                    out.println(SystemUtils.HeadTitle("User Not Found"));
                    out.println("<BODY><CENTER><BR>");
                    out.println("<BR><BR><H3>User Not Found</H3>");
                    out.println("<BR><BR>Unable to find the specified user in the database.");
                    out.println("<BR><BR>If problem persists, contact customer support.");
                    out.println("<BR><BR>");
                    out.println("<a href=\"Proshop_announce\" target=\"bot\">Return</a>");
                    out.println("</CENTER></BODY></HTML>");
                    out.close();

                    // return false; NOT NEEDED SINCE METHOD DEFAULTS TO FALSE
                }
 */

            } catch (Exception exc) {

                out.println(SystemUtils.HeadTitle("DB Connection Error"));
                out.println("<BODY><CENTER><BR>");
                out.println("<BR><BR><H3>Database Connection Error</H3>");
                out.println("<BR><BR>Unable to connect to the Database.");
                out.println("<BR>Please try again later.");
                out.println("<BR><BR>If problem persists, contact customer support.");
                out.println("<BR><BR>");
                out.println("<a href=\"Proshop_announce\" target=\"bot\">Return</a>");
                out.println("</CENTER></BODY></HTML>");
                out.close();

                // return false; NOT NEEDED SINCE METHOD DEFAULTS TO FALSE

            } finally {

                if (rs != null) {

                    try {
                        rs.close();
                    } catch (SQLException sqlEx) {}

                    rs = null;
                }

                if (stmt != null) {

                    try {
                        stmt.close();
                    } catch (SQLException sqlEx) {}

                    stmt = null;
                }

            } // end try/catch/finally
        } // end if not FT proshop user
    } // end if session not null

    return allow;
 }

 // *********************************************************
 // verifyPro - Check for illegal access by user
 // *********************************************************

 public static HttpSession verifyPro(HttpServletRequest req, PrintWriter out) {

   HttpSession session = null;

   String proshop = "proshop";
   String user = "";

   String agent = req.getHeader("User-Agent");  // Get user's browser info for error msg

   String errorMsg = "Proshop User Rejected: User Verification Error (cookies). Agent: " + agent + ", ";

   //
   // Make sure user didn't enter illegally
   //
   session = req.getSession(false);  // Get user's session object (no new one)

   if (session != null) {

      user = (String)session.getAttribute("user");   // get username

      if (!user.startsWith( proshop )) {

         session = null;
      }
   }

   if (session == null) {

//      out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
      out.println("<HTML>");
      out.println("<HEAD>");
      out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
      out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
      out.println("<meta http-equiv=\"Content-Style-Type\" content=\"text/css\">");
      out.println("<TITLE>Access Error - Redirect</TITLE></HEAD>");
      out.println("<BODY><CENTER>");
      out.println("<BR><H2>Access Error</H2><BR>");
      out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
      out.println("<BR>Your session has most likely timed out.");
      out.println("<BR><BR>");
      out.println("<BR>If you feel you have received this message in error,");
      out.println("<BR>please email us at <a href=\"mailto:support@foretees.com\">support@foretees.com</a>.");
      out.println("<BR>Provide your name and the name of your club.  Thank you.");
      out.println("<BR><BR>");
      out.println("<a href=\"Logout\" target=\"_top\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      //
      //  save error message in /v2/error.txt
      //
      errorMsg = errorMsg + " User=" +user;    // build error msg - display user if present
//      logError(errorMsg);                           // log it
   }

   return session;
 }


 // *********************************************************
 // verifyProAdm - Check for illegal access by user (proshop or admin) for Common servlets
 // *********************************************************

 public static HttpSession verifyProAdm(HttpServletRequest req, PrintWriter out) {

   HttpSession session = null;

   String proshop = "proshop";
   String admin = "admin";
   String user = "";

   String agent = req.getHeader("User-Agent");  // Get user's browser info for error msg

   String errorMsg = "Proshop/Admin User Rejected: User Verification Error (cookies). Agent: " + agent + ", ";

   //
   // Make sure user didn't enter illegally
   //
   session = req.getSession(false);  // Get user's session object (no new one)

   if (session != null) {

      user = (String)session.getAttribute("user");   // get username

      if (!user.startsWith( proshop ) && !user.startsWith( admin )) {

         session = null;
      }
   }

   if (session == null) {

      out.println("<HTML>");
      out.println("<HEAD>");
      out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
      out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
      out.println("<meta http-equiv=\"Content-Style-Type\" content=\"text/css\">");
      out.println("<TITLE>Access Error - Redirect</TITLE></HEAD>");
      out.println("<BODY><CENTER>");
      out.println("<BR><H2>Access Error</H2><BR>");
      out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
      out.println("<BR>Your session has most likely timed out.");
      out.println("<BR><BR>");
      out.println("<BR>If you feel you have received this message in error,");
      out.println("<BR>please email us at <a href=\"mailto:support@foretees.com\">support@foretees.com</a>.");
      out.println("<BR>Provide your name and the name of your club.  Thank you.");
      out.println("<BR><BR>");
      out.println("<a href=\"Logout\" target=\"_top\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      //
      //  save error message in /v2/error.txt
      //
      errorMsg = errorMsg + " User=" +user;    // build error msg - display user if present
//      logError(errorMsg);                           // log it
   }

   return session;
 }


 // *********************************************************
 // verifyAdmin - Check for illegal access by user
 // *********************************************************

 public static HttpSession verifyAdmin(HttpServletRequest req, PrintWriter out) {

   HttpSession session = null;

   String admin = "admin";
   String admin2 = "Admin";

   //
   // Make sure user didn't enter illegally
   //
   session = req.getSession(false);  // Get user's session object (no new one)

   if (session != null) {

      String user = (String)session.getAttribute("user");   // get username

      if (!user.startsWith( admin ) && !user.startsWith( admin2 )) {

         session = null;
      }
   }

   if (session == null) {

//      out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
      out.println("<HTML>");
      out.println("<HEAD>");
      out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
      out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
      out.println("<meta http-equiv=\"Content-Style-Type\" content=\"text/css\">");
      out.println("<TITLE>Access Error - Redirect</TITLE></HEAD>");
      out.println("<BODY><CENTER>");
      out.println("<BR><H2>Access Error</H2><BR>");
      out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
      out.println("<BR>This site requires the use of Cookies for security purposes.");
      out.println("<BR>We use them to verify your session and prevent unauthorized access.");
      out.println("<BR><BR>Please check your 'Privacy' settings, under 'Tools', 'Internet Options'");
      out.println("<BR>(for MS Internet Explorer).  This must be set to 'Medium High' or lower.");
      out.println("<BR><BR>");
      out.println("<BR>If you have changed or verified the setting above and still receive this message,");
      out.println("<BR>please email us at <a href=\"mailto:support@foretees.com\">support@foretees.com</a>.");
      out.println("<BR>Provide your name and the name of your club.  Thank you.");
      out.println("<BR><BR>");
      out.println("<a href=\"Logout\" target=\"_top\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
   }

   return session;
 }

 //***********************************************************************************************************
 // restrictProshop - performs actions taken when restricting a proshop user from specific feature access
 //***********************************************************************************************************
 public static void restrictProshop(String featureID, PrintWriter out) {

     // General action
     out.println(HeadTitle("Feature Access Error"));
     out.println("<BODY><CENTER><BR>");
     out.println("<BR><BR><H3>Feature Access Error</H3>");
     out.println("<BR><BR>Sorry, you do not have access to this feature.");
     out.println("<BR><BR>");

     if (featureID.equals("TS_CTRL_TSEDIT") || featureID.equals("TS_UPDATE") || featureID.equals("LOTT_UPDATE") ||
             featureID.equals("WAITLIST_MANAGE") || featureID.equals("LOTT_APPROVE") || featureID.equals("EVNTSUP_MANAGE") ||
             featureID.equals("REPORTS") || featureID.equals("TS_PAST_UPDATE")) {
         out.println("<a href=\"javascript:history.back(1)\">Return</a>");
     } else if (featureID.equals("TS_PACE_VIEW") || featureID.equals("TS_PACE_UPDATE")) {
         out.println("<input type=button value=\" Close Window \" onclick=\"window.close()\" style=\"background-color: #8B8970\">");
     } else {
         out.println("<a href=\"Proshop_announce\" target=\"bot\">Return</a>");
     }

     out.println("</CENTER></BODY></HTML>");
     out.close();
 }

 //************************************************************************
 // buildTee - Builds a tee sheet for the date requested into teecurr2 table
 //
 //   called by: scanTee (below) to build missing tee sheets
 //
 //************************************************************************

 public static void buildTee(int year, int month, int day, int day_name, String course, String club, Connection con)
         throws Exception {


   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

   Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs1 = null;
   ResultSet rs2 = null;
   ResultSet rs3 = null;

   String error = "None";

   int count = 0;
   int f_hr = 0;
   int f_min = 0;
   int l_hr = 0;
   int l_min = 0;
   int betwn = 0;
   int betwn2 = 0;
   int alt_betwn = 0;
   int alt = 0;
   int first = 0;
   short xx = 0;

   boolean WCfiveMinDay = false;
   boolean WWsixMinDay = false;
   boolean BSsaturday = false;
   boolean BSholiday = false;
   boolean SkokieDay1 = false;
   boolean SkokieDay2 = false;
   boolean NCspec1 = false;
   boolean NCspec2 = false;
   boolean NCspec3 = false;
   boolean APspec1 = false;
   boolean APspec2 = false;
   boolean TCC3min = false;
   boolean oakCC5min = false;
   boolean customTees = false;

   //
   //  Gather all the info we need to build the table
   //
   long date = (year * 10000) + (month * 100) + day;       // Date requested

   long shortDate = date - ((date / 10000) * 10000);       // get mmdd (i.e.  20060512 - 20060000)

   //
   //   Get Today's date
   //
   Calendar cal = new GregorianCalendar();                // get todays date
   int yy = cal.get(Calendar.YEAR);
   int mm = cal.get(Calendar.MONTH) +1;
   int dd = cal.get(Calendar.DAY_OF_MONTH);

   long today = (yy * 10000) + (mm * 100) + dd;              // Today

   //
   //  day_name is an index representing the day of the week (01 = sun, 02 = mon, etc.)
   //
   if (day_name > 7) {

      throw new Exception("Invalid day_name value received - buildTee");
   }

   String name = day_table[day_name];  // get name for day

   //
   //  Get the club parameters from clubparm table for the requested course
   //
   try {
      PreparedStatement pstmt1 = con.prepareStatement (
         "SELECT * FROM clubparm2 WHERE courseName = ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setString(1, course);
      rs2 = pstmt1.executeQuery();      // execute the prepared stmt

      if (rs2.next()) {

         f_hr = rs2.getInt("first_hr");
         f_min = rs2.getInt("first_min");
         l_hr = rs2.getInt("last_hr");
         l_min = rs2.getInt("last_min");
         betwn = rs2.getInt("betwn");
         xx = rs2.getShort("xx");
         alt_betwn = rs2.getInt("alt");

      } else {

         xx = 0;
      }                // end of if clubparms (for this course)
      pstmt1.close();

      //
      //  if we are to wait for initial setup, then skip
      //
      //  xx is set to zero in Proshop_parms when we set the club parms for the first time
      //  zero indicates we should wait for config completion before building the tees
      //
      if (xx != 0) {

         //
         //  init some variables to be used in building the tee slots
         //
         int hour = f_hr;                       // hour value for tee time slot
         int min = f_min;                       // minute value for slot
         int thistime = (f_hr * 100) + f_min;   // start time
         int lasttime = (l_hr * 100) + l_min;   // end time

         short fb = 0;
         short fb0 = 0;
         short fb1 = 1;
         String dbl_recurr = "";
         String sfb = "";
         boolean check_dbl = false;

         error = "Tee Times Exist?";

         //
         //  Check if any tee time slots already exist for this date on this course
         //
         if (club.equals("tcclub") && date == today && !course.equals("Primrose Course")) {

            TCC3min = true;                // do custom tees for The CC if today (add to existing times)

            betwn = 3;                     // do 3 min intervals for today
            alt_betwn = 0;
            hour = 6;                      // start these days at 6:48 AM (6:45 is already built))
            min = 48;
            thistime = 648;

         } else {

            if (club.equals("oakhillcc") && date == today) {

               oakCC5min = true;                // do custom tees for Oak Hill CC if today (add to existing times)

               betwn = 5;                     // do 5 min intervals for today
               alt_betwn = 0;
               hour = 7;                      // start these days at 7:05 AM (7:00 is already built))
               min = 5;
               thistime = 705;

            } else {      // all other clubs

               PreparedStatement pstmt = con.prepareStatement (
                  "SELECT fb FROM teecurr2 WHERE date = ? AND courseName = ?");

               pstmt.clearParameters();        // clear the parms
               pstmt.setLong(1, date);         // put the parm in pstmt
               pstmt.setString(2, course);
               rs = pstmt.executeQuery();      // execute the prepared stmt

               if (rs.next()) {               // if tee times exist

                  pstmt.close();
                  return;                     // return - don't do again
               }

               pstmt.close();
            }
         }

         //
         // Prepared statement to put this days tee times in the teecurr2 table
         //
         PreparedStatement pstmt3 = con.prepareStatement (
            "INSERT INTO teecurr2 (date, mm, dd, yy, day, hr, min, time, event, event_color, " +
            "restriction, rest_color, player1, player2, player3, player4, username1, " +
            "username2, username3, username4, p1cw, p2cw, p3cw, p4cw, first, in_use, in_use_by, " +
            "event_type, hndcp1, hndcp2, hndcp3, hndcp4, show1, show2, show3, show4, fb, " +
            "player5, username5, p5cw, hndcp5, show5, notes, hidenotes, lottery, courseName, blocker, " +
            "proNew, proMod, memNew, memMod, rest5, rest5_color, mNum1, mNum2, mNum3, mNum4, mNum5, " +
            "lottery_color, userg1, userg2, userg3, userg4, userg5, hotelNew, hotelMod, orig_by, " +
            "conf, p91, p92, p93, p94, p95, pos1, pos2, pos3, pos4, pos5, hole, create_date) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, '', '', '', '', '', '', '', '', '', '', " +
            "'', '', '', '', '', '', ?, 0, '', 0, 0, 0, 0, 0, 0, 0, 0, 0, ?, '', '', '', 0, 0, '', 0, " +
            "'', ?, '', 0, 0, 0, 0, '', '', '', '', '', '', '', '', '', '', '', '', '', 0, 0, '', '', " +
            "0, 0, 0, 0, 0, 0, 0, 0, 0, 0, '', now())");

            //"'', '', '', '', '', '', ?, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 0, ?, '', '', '', 0, 0, '', 0, " +

         error = "Check Dbl Tees?";

         //
         // See if we need to check the dbltee table each time
         //
         PreparedStatement pstmt8 = con.prepareStatement (
                "SELECT name FROM dbltee2 WHERE sdate <= ? AND edate >= ? AND (courseName = ? OR courseName = '-ALL-')");

         pstmt8.clearParameters();        // clear the parms
         pstmt8.setLong(1, date);         // put the parm in stmt
         pstmt8.setLong(2, date);
         pstmt8.setString(3, course);
         rs1 = pstmt8.executeQuery();      // execute the prepared stmt

         if (rs1.next()) {

            check_dbl = true;    // we have to check dbltee table
         }

         pstmt8.close();


         //
         //******************************************************************************************
         //   Custom sheets for Santa Ana CC
         //
         //   Sat & Sun      8 minute intervals
         //   Week Days      7 & 8 minute intervals (set as norm in config)
         //******************************************************************************************
         //
         if (club.equals( "santaana" )) {       // if Santa Ana

            if (name.equalsIgnoreCase( "saturday" ) || name.equalsIgnoreCase( "sunday" )) {

               betwn = 8;
               alt_betwn = 0;
            }
         }


         //
         //******************************************************************************************
         //   Custom sheets for Westchester CC - South Course ONLY
         //
         //      ************* See also Member_slot ********************
         //
         //   5 minute intervals starting at 7:40 AM to 10:30 AM for the following dates:
         //
         //         Every Wed from 5/09 - 10/19, except 7/25 & 9/05
         //
         //         Every Thurs from 5/10 - 10/19, except 7/12 & 9/06 (2007)
         //
         //
         //******************************************************************************************
         //
         if (club.equals( "westchester" ) && course.equalsIgnoreCase( "south" )) {

            if (shortDate > 503 && shortDate < 1020 && (name.equals( "Wednesday" ) || name.equals( "Thursday" ))) {

               WCfiveMinDay = true;

               if (name.equals( "Wednesday" ) && ((shortDate > 718 && shortDate < 726) || (shortDate > 900 && shortDate < 906))) {  // skip these Wed's

                  WCfiveMinDay = false;
               }

               if (name.equals( "Thursday" ) && ((shortDate > 705 && shortDate < 713) || (shortDate > 900 && shortDate < 907))) {  // skip these Thur's

                  WCfiveMinDay = false;
               }
            }

            if (WCfiveMinDay == true) {

               betwn2 = betwn;        // save config'd between value
               betwn = 5;
               alt_betwn = 0;
               hour = 7;              // start these days at 7:40 AM
               min = 40;
               thistime = 740;
            }
         }

        /*       // replaced on 12/12/08 (new custom tee sheet feature)
         //
         //******************************************************************************************
         //   Custom sheets for CC of St Albans - both courses
         //
         //    Weekends only
         //
         //******************************************************************************************
         //
         if (club.equals( "stalbans" ) && (name.equals( "Saturday" ) || name.equals( "Sunday" ))) {

            betwn = 9;
            alt_betwn = 8;
            hour = 6;              // start these days at 6:35 AM
            min = 35;
            thistime = 635;
         }
         */

         //
         //******************************************************************************************
         //   Custom sheets for The Pinery - all courses
         //
         //******************************************************************************************
         //
         if (club.equals( "pinery" )) {

            betwn = 9;
            alt_betwn = 8;
            hour = 6;              // start these days at 6:51 AM
            min = 51;
            thistime = 651;
         }


         //
         //******************************************************************************************
         //   Custom sheets for Big Spring CC
         //
         //     Special times for Saturdays and Holidays
         //
         //      April 16, 23, 30
         //      May 7, 14, 21, 28, (30 = holiday)
         //      June 4, 11, 18, 25
         //      July 2, 9, 16, 23, 30, (4 = holiday)
         //      August 6, 13, 20, 27
         //      Sept  3, 10, 17, 24, (5 = holiday)
         //      Oct   1
         //
         //******************************************************************************************
         //
         if (club.equals( "bigsprings" )) {

            if (shortDate > 400 && shortDate < 1001) {         // April - Sept

               if (name.equalsIgnoreCase( "saturday" )) {      // if Saturday

                  BSsaturday = true;
               }

               if (date == memDay1 || date == july4th1 || date == laborDay1) {   // if a Holiday

                  BSholiday = true;
               }
            }

            if (BSsaturday == true) {   // if SATURDAY

               betwn2 = betwn;        // save config'd between value
               betwn = 8;
               alt_betwn = 0;
               hour = 8;              // start these days at 8:00 AM
               min = 0;
               thistime = 800;
               check_dbl = false;     // do custom dbl tees
            }

            if (BSholiday == true) {   // if HOLIDAY

               betwn2 = betwn;        // save config'd between value
               betwn = 8;
               alt_betwn = 0;
               hour = 8;              // start these days at 8:00 AM
               min = 0;
               thistime = 800;
               check_dbl = false;     // do custom dbl tees
            }
         }

         //
         //******************************************************************************************
         //   Custom sheets for New Canaan CC
         //
         //     Special 2-some times based on time of year and day of week
         //
         //******************************************************************************************
         //
         if (club.equals( "newcanaan" )) {

            //
            //  Off-Peak Season (401 - 5/26 OR 9/05 - 11/01)
            //
            if ((shortDate > 331 && shortDate < 527) || (shortDate > 904 && shortDate < 1102)) {

               if (name.equalsIgnoreCase( "saturday" ) || name.equalsIgnoreCase( "sunday" )) {

                  betwn2 = betwn;        // save config'd between value
                  betwn = 5;
                  alt_betwn = 0;
                  hour = 7;              // start these days at 7:30 AM
                  min = 30;
                  thistime = 730;

                  NCspec1 = true;        // indicate special times #1 for below
               }
            }

            //
            //  Peak Season (5/27 - 9/04)
            //
            if (shortDate > 526 && shortDate < 905) {

               if (name.equalsIgnoreCase( "saturday" ) || name.equalsIgnoreCase( "sunday" ) ||
                   date == memDay1 || date == july4th1 || date == july3rd1 || date == laborDay1) {

                  betwn2 = betwn;        // save config'd between value
                  betwn = 5;
                  alt_betwn = 0;
                  hour = 7;              // start these days at 7:00 AM
                  min = 0;
                  thistime = 700;

                  NCspec2 = true;        // indicate special times #2 for below
               }
            }

            //
            //  Weekday Season (4/11 - 10/30)
            //
            if (shortDate > 410 && shortDate < 1031) {

               if ((name.equalsIgnoreCase( "tuesday" ) || name.equalsIgnoreCase( "wednesday" ) || name.equalsIgnoreCase( "thursday" ) ||
                   name.equalsIgnoreCase( "friday" )) && shortDate != 704) {

                  betwn2 = betwn;        // save config'd between value
                  betwn = 5;
                  alt_betwn = 0;
                  hour = 8;              // start these days at 8:00 AM
                  min = 0;
                  thistime = 800;

                  NCspec3 = true;        // indicate special times #3 for below
               }
            }
         }

         //
         //******************************************************************************************
         //   Custom sheets for The CC (Brookline) - Main Course Only!!
         //
         //     Special 2-some times based on time of year and day of week
         //
         //******************************************************************************************
         //
         if (club.equals( "tcclub" ) && course.equals( "Main Course" )) {

            //
            //  Season (4/01 - 8/31)
            //
            if ((shortDate >= 401 && shortDate <= 831)) {

               if (name.equals( "Tuesday" ) || name.equals( "Wednesday" ) || name.equals( "Thursday" )) {

                  if (TCC3min == true) {       // if this is today and we are inserting 3 min intervals

                     hour = 7;                 // start these days at 7:03 AM (others already built)
                     min = 3;
                     thistime = 703;

                  } else {

                     betwn2 = betwn;        // save config'd between value
                     betwn = 10;
                     alt_betwn = 0;
                     hour = 7;              // start these days at 7:30 AM
                     min = 30;
                     thistime = 730;

                     customTees = true;       // indicate custom tee times
                  }
               }
            }

            if ((shortDate >= 901 && shortDate <= 1031)) {

               if (name.equals( "Tuesday" ) || name.equals( "Wednesday" ) || name.equals( "Thursday" )) {

                  if (TCC3min == true) {       // if this is today and we are inserting 3 min intervals

                     hour = 7;                 // start these days at 7:03 (was 7:48) AM (others already built)
                     min = 03;
                     thistime = 703;

                  } else {

                     betwn2 = betwn;        // save config'd between value
                     betwn = 10;
                     alt_betwn = 0;
                     hour = 7;              // start these days at 7:45 AM
                     min = 45;
                     thistime = 745;

                     customTees = true;       // indicate custom tee times
                  }
               }
            }
         }

         //
         //******************************************************************************************
         //   Custom sheets for Greenwich CC
         //
         //     Special 2-some times based on dates
         //
         //******************************************************************************************
         //
         if (club.equals( "greenwich" )) {

            customTees = false;

            //
            //  Only on specified dates
            //
            if (shortDate == 526 || shortDate == 527 || shortDate == 528 || shortDate == 602 || shortDate == 603 ||
                shortDate == 609 || shortDate == 610 || shortDate == 616 || shortDate == 617 || shortDate == 623 ||
                shortDate == 624 || shortDate == 701 || shortDate == 707 || shortDate == 708 || shortDate == 721 ||
                shortDate == 804 || shortDate == 811 || shortDate == 812 || shortDate == 818 || shortDate == 819 ||
                shortDate == 826) {

               betwn2 = betwn;        // save config'd between value
               betwn = 6;
               alt_betwn = 0;
               hour = 7;              // start these days at 7:00 AM
               min = 0;
               thistime = 700;

               customTees = true;       // indicate custom tee times
            }
         }
/*              // Club does not wish to use this anymore
         //
         //******************************************************************************************
         //   Custom sheets for Green Hills
         //
         //     Special 8 minute times on Sundays
         //
         //******************************************************************************************
         //
         if (club.equals( "greenhills" )) {

            customTees = false;

            //
            //  Sundays only
            //
            if (name.equals( "Sunday" )) {

               customTees = true;       // indicate custom tee times
            }
         }
*/
         //
         //******************************************************************************************
         //   Custom sheets for Bellevue
         //
         //     Special 10 minute times on Tuesdays
         //
         //******************************************************************************************
         //
         if (club.equals( "bellevuecc" )) {

            customTees = false;

            if (shortDate > 514 && shortDate < 829) {      // between these dates

               //
               //  Tuesdays only
               //
               if (name.equals( "Tuesday" )) {

                  customTees = true;       // indicate custom tee times

                  betwn2 = betwn;          // save config'd between value
               }
            }
         }

         //
         //******************************************************************************************
         //   Custom sheets for The Congressional - Club Course Only!!
         //
         //     Special 2-some times and double tees on Tuesdays
         //
         //******************************************************************************************
         //
         /*
         if (club.equals( "congressional" ) && course.equals( "Club Course" ) && name.equals( "Tuesday" )) {

            // Tuesdays between Apr 1 & Oct 31 (some exceptions every year)
            if ( shortDate != 622 && shortDate != 727 && shortDate != 1012 && (shortDate >= 401 && shortDate <= 1031) ) {

               betwn2 = betwn;         // save config'd between value
               betwn = 8;
               alt_betwn = 0;
               hour = 8;                    // start these days at 8:00 AM
               min = 0;
               thistime = 800;

               check_dbl = false;      // do custom dbl tees

               customTees = true;    // indicate custom tee times
            }
         }
          */

         //
         //******************************************************************************************
         //   Custom sheets for Eugene CC
         //
         //     They start with 8 min, then 9, then 8 again, then 10 min the rest of the day.
         //
         //******************************************************************************************
         //
         if (club.equals( "eugenecc" )) {

            betwn2 = betwn;        // save config'd between value
            betwn = 8;
            alt_betwn = 0;
            hour = 7;              // start at 7:28 AM
            min = 28;
            thistime = 728;

            customTees = true;       // indicate custom tee times
         }

         //
         //******************************************************************************************
         //   Custom sheets for Imperial GC
         //
         //     They start at 7:45 with 7 & 8 min, then 8 min at 8:22 for the rest of the day.
         //
         //******************************************************************************************
         //
         if (club.equals( "imperialgc" )) {

            betwn2 = betwn;        // save config'd between value (8 min)
            betwn = 7;
            alt_betwn = 8;
            hour = 7;              // start at 7:45 AM
            min = 45;
            thistime = 745;

            customTees = true;       // indicate custom tee times
         }

         //
         //******************************************************************************************
         //   Custom sheets for Oahu CC
         //
         //     Special alternating times on Saturdays
         //
         //******************************************************************************************
         //
         if (club.equals( "oahucc" ) && name.equals( "Saturday" )) {

            betwn = 7;
            alt_betwn = 8;
            hour = 7;              // start these days at 7:00 AM
            min = 0;
            thistime = 700;
            l_hr = 17;              // end these days at 5:45 PM
            l_min = 45;
            lasttime = 1745;
         }

         //
         //******************************************************************************************
         //   Custom sheets for Bonnie Briar
         //
         //     Special double tees on weekends and holidays
         //
         //******************************************************************************************
         //
         if (club.equals( "bonniebriar" ) && shortDate > 414 && shortDate < 1031) {

            if (name.equals( "Saturday" ) || name.equals( "Sunday" ) ||
                date == memDay1 || date == laborDay1) {

               customTees = true;       // indicate custom tee times
            }
         }

         //
         //******************************************************************************************
         //   Custom sheets for The Apawamis Club
         //
         //     Special 2-some times on Sundays based on time of year
         //
         //******************************************************************************************
         //
         if (club.equals( "apawamis" )) {

            //
            //   Sundays only
            //
            if (name.equals( "Sunday" )) {

               //
               //  Peak Season (5/28 - 9/03)
               //
               if (shortDate > 527 && shortDate < 904) {

                  betwn2 = betwn;        // save config'd between value
                  betwn = 6;
                  alt_betwn = 0;
                  hour = 7;              // start these days at 7:00 AM
                  min = 0;
                  thistime = 700;

                  APspec1 = true;        // indicate special times for below
               }

               //
               //  Off-Peak Season (9/04 - 10/30)
               //
               if (shortDate > 903 && shortDate < 1031) {

                  betwn2 = betwn;        // save config'd between value
                  betwn = 6;
                  alt_betwn = 0;
                  hour = 7;              // start these days at 7:30 AM
                  min = 30;
                  thistime = 730;

                  APspec1 = true;        // indicate special times for below
               }
            }
         }

         //
         //******************************************************************************************
         //   Custom sheets for North Oaks
         //
         //     Tuesdays after Aug 1st have diferent times.
         //
         //******************************************************************************************
         //
         if (club.equals( "noaks" )) {

            //
            //   Tuesdays on & after 8/01 only
            //
            if (name.equals( "Tuesday" ) && shortDate > 800) {

               betwn2 = betwn;        // save config'd between value
               betwn = 8;
               alt_betwn = 0;
               hour = 7;              // start these days at 7:30 AM
               min = 30;
               thistime = 730;
            }
         }

         //
         //******************************************************************************************
         //   Custom sheets for Brooklawn CC
         //******************************************************************************************
         //
         /*
         if (club.equals( "brooklawn" )) {

            if (name.equals( "Tuesday" )) {

               betwn2 = betwn;        // save config'd between value
               betwn = 8;
               alt_betwn = 0;
               hour = 8;              // start these days at 8:00 AM
               min = 0;
               thistime = 800;
            }

            if (name.equals( "Wednesday" ) || name.equals( "Thursday" ) || name.equals( "Friday" )) {

               betwn2 = betwn;        // save config'd between value
               betwn = 9;
               alt_betwn = 0;
               hour = 8;              // start these days at 8:00 AM
               min = 0;
               thistime = 800;
            }

            if (name.equals( "Saturday" ) || name.equals( "Sunday" )) {

               betwn2 = betwn;        // save config'd between value
               betwn = 8;
               alt_betwn = 0;
               hour = 7;              // start these days at 7:00 AM
               min = 0;
               thistime = 700;
            }
         }
          */

         //
         //******************************************************************************************
         //   Custom sheets for El Niguel CC
         //******************************************************************************************
         //
         if (club.equals( "elniguelcc" )) {

            if (name.equals( "Tuesday" )) {

               betwn2 = betwn;        // save config'd between value
               betwn = 8;
               alt_betwn = 0;
               hour = 7;              // start these days at 7:00 AM
               min = 0;
               thistime = 700;
            }

            if (name.equals( "Thursday" )) {

               betwn2 = betwn;        // save config'd between value
               betwn = 8;
               alt_betwn = 0;
               hour = 7;              // start these days at 7:28 AM
               min = 28;
               thistime = 728;
            }
         }

         //
         //******************************************************************************************
         //   Custom sheets for The TPC-TC
         //******************************************************************************************
         //
         if (club.equals( "tpctc" )) {

            betwn2 = betwn;        // save config'd between value
            betwn = 8;             // start with 8 min intervals
            alt_betwn = 0;
            hour = 7;              // start these days at 7:00 AM
            min = 0;
            thistime = 700;

         }

         //
         //******************************************************************************************
         //   Custom sheets for Wee Burn
         //
         //     Special 2-some times on Wednesdays (see below)
         //
         //******************************************************************************************
         //
         if (club.equals( "weeburn" )) {

            betwn2 = betwn;        // save config'd between value
         }

         //
         //******************************************************************************************
         //   Custom sheets for Hudson National CC
         //
         //     Special times for Weekends and Holidays
         //
         //******************************************************************************************
         //
         if (club.equals( "hudsonnatl" )) {

            if (name.equalsIgnoreCase( "saturday" ) || name.equalsIgnoreCase( "sunday" )) {

               BSsaturday = true;
            }

            if (date == memDay1 || date == july4th1 || date == laborDay1 || date == memDay2 || date == july4th2 || date == laborDay2) {

               BSsaturday = true;
            }

            if (BSsaturday == true) {   // if SATURDAY

               betwn2 = betwn;        // save config'd between value
               betwn = 10;
               alt_betwn = 0;
               hour = 7;              // start these days at 7:30 AM
               min = 30;
               thistime = 730;
            }
         }

         //
         //******************************************************************************************
         //   Custom sheets for Mission Viejo CC
         //
         //     8 minute intervals for Sundays (normally 10 minute)
         //
         //******************************************************************************************
         //
         if (club.equals( "missionviejo" )) {

            if (name.equalsIgnoreCase( "sunday" )) {

               betwn = 8;             // 8 minute intervals on Sundays
               alt_betwn = 0;
               hour = 6;              // start these days at 6:32 AM
               min = 32;
               thistime = 632;
            }
         }

         //
         //******************************************************************************************
         //   Custom sheets for The Stanwich Club
         //
         //     6 minute intervals early each day for 2-some-only times (normally 8 minute)
         //
         //******************************************************************************************
         //
         /*
         if (club.equals( "stanwichclub" )) {

            if (name.equalsIgnoreCase( "saturday" )  || name.equalsIgnoreCase( "sunday" )  || name.equalsIgnoreCase( "monday" )) {

               betwn2 = betwn;        // save config'd between value
               betwn = 6;             // start with 6 minute intervals
               alt_betwn = 0;
               hour = 7;              // start these days at 7:00 AM
               min = 0;
               thistime = 700;
            }

            if (name.equalsIgnoreCase( "tuesday" ) || name.equalsIgnoreCase( "wednesday" )) {

               betwn2 = betwn;        // save config'd between value
               betwn = 6;             // start with 6 minute intervals
               alt_betwn = 0;
               hour = 8;              // start these days at 8:00 AM
               min = 0;
               thistime = 800;
            }

            if (name.equalsIgnoreCase( "thursday" ) || name.equalsIgnoreCase( "friday" )) {

               alt_betwn = 0;
               hour = 8;              // start these days at 8:00 AM (8 min intervals all day)
               min = 0;
               thistime = 800;
            }
         }
         */
         
         //
         //******************************************************************************************
         //   Custom sheets for Fairwood CC
         //
         //     8 minute intervals for Saturdays and Sundays (normally 10 minute)
         //
         //******************************************************************************************
         //
         if (club.equals( "fairwood" )) {

            if (name.equalsIgnoreCase( "saturday" ) || name.equalsIgnoreCase( "sunday" )) {

               betwn = 8;             // 8 minute intervals on Sundays
               alt_betwn = 0;
               hour = 6;              // start these days at 6:30 AM
               min = 30;
               thistime = 630;
               lasttime = 2100;           // end of tee sheet (9:00 PM)
            }
         }

         //
         //******************************************************************************************
         //   Custom sheets for Piedmont Driving Club in Atlanta
         //
         //   Start Times:
         //              4/01 - 10/31     Nov & Mar       12/01 - 2/29
         //              ------------     ---------       ------------
         //   Sat & Sun      8:00            8:30             9:00
         //   (& Mem & Labor Days)
         //   Week Days      8:30            9:00             9:30
         //  -----------------------------------------------------------
         //
         //  Intervals:
         //
         //     Sat & Sun -  All year long the 1st 6 tee times are 6 minute intervals, then
         //     (& holidays) the rest of the day its 10 minute intervals, though the 10 minute
         //                  interval times must start on an even 10's minute (i.e. 8:40).
         //
         //     Weekdays -   Always 10 minute intervals (as specified in course parms)
         //******************************************************************************************
         //
         if (club.equals( "piedmont" )) {       // if Piedmont Driving Club

            //
            //  Reset parms based on time of year and day of week
            //
            hour = 8;              // hour value for tee time slot
            min = 0;               // minute value for slot

            if (month == 3 || month == 11) {       // March or Nov

               if (name.equalsIgnoreCase( "saturday" ) || name.equalsIgnoreCase( "sunday" ) ||
                   date == memDay1 || date == laborDay1) {

                  min = 30;               // minute value to start (8:30)

               } else {

                  hour = 9;              // hour value to start (9:00)
               }
            } else {

               if (month == 1 || month == 2 || month == 12) {       // Jan, Feb or Dec

                  if (name.equalsIgnoreCase( "saturday" ) || name.equalsIgnoreCase( "sunday" ) ||
                      date == memDay1 || date == laborDay1) {

                     hour = 9;              // hour value to start (9:00)

                  } else {

                     hour = 9;              // hour value to start
                     min = 30;              // minute value to start (9:30)
                  }
               } else {             // rest of the year (8:00 or 8:30)

                  if (!name.equalsIgnoreCase( "saturday" ) && !name.equalsIgnoreCase( "sunday" ) &&
                      date != memDay1 && date != laborDay1) {

                     min = 30;              // minute value to start (8:30)
                  }
               }
            }

            thistime = (hour * 100) + min;  // hr & min in one value (start time)
         }                                  // end of IF piedmont


         if (club.equals("rmgc")) {

             // Set the interval to 7 or 9 based on course, even/odd date, and weekend/weekday
             if (verifyCustom.checkRMGC4Ball(course, date, name)) {
                 betwn = 9;
             } else {
                 betwn = 7;
             }

             alt_betwn = 0;
             hour = 7;              // start these days at 6:30 AM
             min = 0;
             thistime = 700;
             lasttime = 2000;           // end of tee sheet (9:00 PM)
         }

         //*****************************************************************************************
         //  End of Custom Tee Sheet Checks (might be more to finish below)
         //*****************************************************************************************

         first = 1;       // first slot indicator

         //
         //  Now we can build the tee time slots
         //
         while (thistime <= lasttime) {

            pstmt3.clearParameters();        // clear the parms
            pstmt3.setLong(1, date);         // put the parms in stmt for tee slot
            pstmt3.setInt(2, month);
            pstmt3.setInt(3, day);
            pstmt3.setInt(4, year);
            pstmt3.setString(5, name);
            pstmt3.setInt(6, hour);
            pstmt3.setInt(7, min);
            pstmt3.setInt(8, thistime);

            //
            // check for double tees if there are any for this day
            //
            fb = 0;

            if (check_dbl) {             // must we check double tees?

               //
               // Prepared statement to search the double tees table for 'double tee' times
               //
               PreparedStatement pstmt6 = con.prepareStatement (
                  "SELECT recurr FROM dbltee2 WHERE sdate <= ? AND edate >= ? " +
                  "AND stime1 <= ? AND etime1 >= ? AND (courseName = ? OR courseName = '-ALL-')");

               error = "Check for Dbl Tees";

               pstmt6.clearParameters();        // clear the parms
               pstmt6.setLong(1, date);         // put the parms in stmt
               pstmt6.setLong(2, date);
               pstmt6.setInt(3, thistime);
               pstmt6.setInt(4, thistime);
               pstmt6.setString(5, course);
               rs3 = pstmt6.executeQuery();      // see if Front and Back to be built

               loop2:
               while (rs3.next()) {

                  dbl_recurr = rs3.getString(1);   // dbl tee found for this time

                  //
                  //  We must check the recurrence for this day (Monday, etc.)
                  //
                  if (dbl_recurr.equalsIgnoreCase( "every " + name )) {

                     fb = 2;                        // set double tees for this time
                     break loop2;                   // done checking - exit while loop
                  }

                  if (dbl_recurr.equalsIgnoreCase( "every day" )) {   // if everyday

                     fb = 2;                        // set double tees for this time
                     break loop2;                   // done checking - exit while loop
                  }

                  if ((dbl_recurr.equalsIgnoreCase( "all weekdays" )) &&
                      (!name.equalsIgnoreCase( "saturday" )) &&
                      (!name.equalsIgnoreCase( "sunday" ))) {

                     fb = 2;                        // set double tees for this time
                     break loop2;                   // done checking - exit while loop
                  }

                  if ((dbl_recurr.equalsIgnoreCase( "all weekends" )) &&
                      (name.equalsIgnoreCase( "saturday" ))) {

                     fb = 2;                        // set double tees for this time
                     break loop2;                   // done checking - exit while loop
                  }

                  if ((dbl_recurr.equalsIgnoreCase( "all weekends" )) &&
                      (name.equalsIgnoreCase( "sunday" ))) {

                     fb = 2;                        // set double tees for this time
                     break loop2;                   // done checking - exit while loop
                  }

               }   // end of loop2 while loop

               pstmt6.close();

               if (fb == 0) {                        // if no hit, check 'no tees' times (cross-over)

                  //
                  // Prepared statement to search the double tees table for 'no tee' times (cross-overs)
                  //
                  PreparedStatement pstmt7 = con.prepareStatement (
                     "SELECT recurr FROM dbltee2 WHERE sdate <= ? AND edate >= ? " +
                     "AND stime2 <= ? AND etime2 >= ? AND (courseName = ? OR courseName = '-ALL-')");

                  error = "Check for Cross-Over Tees";

                  pstmt7.clearParameters();        // clear the parms
                  pstmt7.setLong(1, date);         // put the parms in stmt
                  pstmt7.setLong(2, date);
                  pstmt7.setInt(3, thistime);
                  pstmt7.setInt(4, thistime);
                  pstmt7.setString(5, course);
                  rs1 = pstmt7.executeQuery();      // check for cross-over time

                  loop3:
                  while (rs1.next()) {

                     dbl_recurr = rs1.getString(1);   // dbl tee found for this time

                     //
                     //  We must check the recurrence for this day (Monday, etc.)
                     //
                     if (dbl_recurr.equalsIgnoreCase( "every " + name )) {

                        fb = 9;                        // set double tees for this time
                        break loop3;                   // done checking - exit while loop
                     }

                     if (dbl_recurr.equalsIgnoreCase( "every day" )) {   // if everyday

                        fb = 9;                        // set double tees for this time
                        break loop3;                   // done checking - exit while loop
                     }

                     if ((dbl_recurr.equalsIgnoreCase( "all weekdays" )) &&
                         (!name.equalsIgnoreCase( "saturday" )) &&
                         (!name.equalsIgnoreCase( "sunday" ))) {

                        fb = 9;                        // set double tees for this time
                        break loop3;                   // done checking - exit while loop
                     }

                     if ((dbl_recurr.equalsIgnoreCase( "all weekends" )) &&
                         (name.equalsIgnoreCase( "saturday" ))) {

                        fb = 9;                        // set double tees for this time
                        break loop3;                   // done checking - exit while loop
                     }

                     if ((dbl_recurr.equalsIgnoreCase( "all weekends" )) &&
                         (name.equalsIgnoreCase( "sunday" ))) {

                        fb = 9;                        // set double tees for this time
                        break loop3;                   // done checking - exit while loop
                     }
                  }      // end of loop3 while

                  pstmt7.close();

               }     // end of 2nd double tee if

            }        // end of 'check dbl' if

            if (club.equals( "bigsprings" ) && (BSsaturday == true || BSholiday == true)) {

               fb = 2;                // do both dbl tees (no cross-overs needed)
            }

            /*
            if (club.equals( "congressional" ) && (customTees == true)) {

               fb = 2;                // do both dbl tees (no cross-overs needed)
            }
             */

        //    if (club.equals( "skokie" ) && (SkokieDay1 == true || SkokieDay2 == true)) {

        //       fb = 2;                // do both dbl tees (no cross-overs needed)
        //    }

            if (club.equals( "nakoma" ) && name.equalsIgnoreCase( "thursday" )) {

               if (thistime > 1156 && thistime < 1357) {

                  fb = 2;                           // do dbl tees (no cross-overs needed)

                  if (thistime == 1157) {           // if this is 11:57 AM

                     alt_betwn = 8;                 // then alternate between 9 and 8 minute intervals
                  }

                  if (thistime == 1356) {           // if this is 1:56 PM

                     alt_betwn = 0;                 // then stop alternate intervals
                  }
               }
            }

            //
            //  Done checking for double tees
            //
            //     fb:  0 = no dbl tees (build all front tees - 0)
            //          2 = dbl tees found (build both - 0 & 1)
            //          9 = cross over time (build cross-over tee time - 9)
            //
            pstmt3.setInt(9, first);          // set first indicator

            if (fb == 2) {                    // double tee (do both front and back for this time) ?

               pstmt3.setShort(10, fb0);      // do front 9 first

            } else {

               pstmt3.setShort(10, fb);        // 0 = front, 9 = cross-over

            }
            pstmt3.setString(11, course);              // course name

            error = "Add Entry To Teecurr ";

            //
            //  Put the entry in the teecurr2 table and do the next one
            //
            pstmt3.executeUpdate();        // execute the prepared stmt

            first = 0;             // no longer first slot

            //
            // if 'double tee' was found for this time, build a 2nd tee time for the back 9
            //
            if (fb == 2) {

               error = "Add Entry To Teecurr 2";

               pstmt3.setInt(9, first);       // set first indicator
               pstmt3.setShort(10, fb1);        // do back 9 (1)

               pstmt3.executeUpdate();        // execute the prepared stmt

            }

            //
            //  Manually set interval if Piedmont Driving Club
            //
            if (club.equals( "piedmont" )) {       // if Piedmont Driving Club

               if ((name.equalsIgnoreCase( "saturday" ) || name.equalsIgnoreCase( "sunday" ) ||
                    date == memDay1 || date == laborDay1) && count < 5) {

                  min = min + 6;            // 1st 6 intervals on Sat & Sun are 6 minutes

               } else {

                  min = min + betwn;         // use normal interval (10)
               }

            } else {                  // NOT Piedmont

               //
               //******************************************************************************************
               //   Custom sheets for Wee Burn
               //
               //     Special 2-some times on Tuesdays & Wednesdays based on time of year (July 6th for 2006 only!)
               //
               //     NOTE:  this checks a range of dates that allows for Wednesdays changing each year!!!
               //            (i.e. instead of checking for 5/31, it checks the whole week)
               //
               //******************************************************************************************
               //
               if (club.equals( "weeburn" )) {

                  if (name.equals( "Wednesday" ) || date == 20060706) {       // only check 7/06 in 2006

                     if (shortDate > 419 && shortDate < 927) {       // April - Sept. (every Wed)

                        if ((shortDate > 524 && shortDate < 608) || date == 20060706) {

                           if (thistime == 800) {            // if we just did 8:00 AM

                              betwn = 6;                     // then switch to 6 min intervals

                           } else {

                              if (thistime == 1100) {        // if we just did 11:00 AM

                                 betwn = betwn2;             // then return to normal intervals
                              }
                           }

                        } else {

                           if ((shortDate > 823 && shortDate < 831) || (shortDate > 831 && shortDate < 907)) {

                              if (thistime == 800) {            // if we just did 8:00 AM

                                 betwn = 6;                     // then switch to 6 min intervals

                              } else {

                                 if (thistime == 1030) {        // if we just did 10:30 AM

                                    betwn = betwn2;             // then return to normal intervals
                                 }
                              }

                           } else {                             // all other Wednesdays in date range

                              if (thistime == 900) {            // if we just did 9:00 AM

                                 betwn = 6;                     // then switch to 6 min intervals

                              } else {

                                 if (thistime == 1030) {        // if we just did 10:30 AM

                                    betwn = betwn2;             // then return to normal intervals
                                 }
                              }
                           }
                        }
                     }
                  }

                  if (name.equals( "Tuesday" )) {         // Tuesdays

                     if ((shortDate >= 501 && shortDate <= 930)) {

                        if (thistime == 800) {            // if we just did 8:00 AM

                           betwn = 6;                     // then switch to 6 min intervals

                        } else {

                           if (thistime == 1000) {        // if we just did 10:30 AM

                              betwn = betwn2;             // then return to normal intervals
                           }
                        }
                     }
                  }

               }         // end of IF Weeburn

               if (club.equals( "claremontcc" ) && name.equals("Sunday")) {

                    if (thistime == 1030) {            // if we just did 10:30 AM

                       betwn2 = betwn;
                       betwn = 5;                     // then switch to 5 min intervals

                    } else {

                       if (thistime == 1120) {        // if we just did 11:20 AM

                          betwn = betwn2;             // then return to normal intervals (10 min)
                       }
                    }
               }

               if (club.equals( "westchester" ) && WCfiveMinDay == true) {

                  if (thistime == 1030) {           // if we just did 10:30 AM

                     betwn = betwn2;                // then switch to normal intervals
                  }
               }

               if (club.equals( "newcanaan" ) && NCspec1 == true) {

                  if (thistime == 800) {            // if we just did 8:00 AM

                     betwn = betwn2;                // then switch to normal intervals
                  }
               }
               if (club.equals( "newcanaan" ) && NCspec2 == true) {

                  if (thistime == 730) {            // if we just did 7:30 AM

                     betwn = betwn2;                // then switch to normal intervals
                  }
               }
               if (club.equals( "newcanaan" ) && NCspec3 == true) {

                  if (thistime == 830) {           // if we just did 8:30 AM

                     betwn = betwn2;                // then switch to normal intervals
                  }
               }

               if (club.equals( "tcclub" ) && TCC3min == true) {  // if we are inserting 3 min intervals for The CC

                  if (min == 12) {           // if we just did x:12

                     min = 15;               // make sure we skip past x:15 (already done)
                     thistime = hour + min;

                  } else {

                     if (min == 27) {           // if we just did x:27

                        min = 30;               // make sure we skip past x:30 (already done)
                        thistime = hour + min;

                     } else {

                        if (min == 42) {           // if we just did x:42

                           min = 45;               // make sure we skip past x:45 (already done)
                           thistime = hour + min;

                        } else {

                           if (min == 57) {           // if we just did x:57

                              min = 0;               // make sure we skip past x:00 (already done)
                              hour += 1;
                              thistime = hour + min;
                           }
                        }
                     }
                  }
               }

               if (club.equals( "tcclub" ) && course.equals( "Main Course" ) && customTees == true) {

                  if ((shortDate >= 401 && shortDate <= 831 && thistime == 800) ||
                      (shortDate >= 901 && shortDate <= 1031 && thistime == 815)) {            // if we just did 8:00 AM or 8:15 AM (depends on date-range)

                     betwn = betwn2;                // then switch to normal intervals

                     customTees = false;            // no more custom tees for this day
                  }
               }

               if (club.equals( "oakhillcc" ) && oakCC5min == true) {  // if we are inserting 5 min intervals for Oak Hill CC

                  if (min == 10) {           // if we just did x:10

                     min = 15;               // make sure we skip past x:15 (already done)
                     thistime = hour + min;

                  } else {

                     if (min == 25) {           // if we just did x:25

                        min = 30;               // make sure we skip past x:30 (already done)
                        thistime = hour + min;

                     } else {

                        if (min == 40) {           // if we just did x:40

                           min = 45;               // make sure we skip past x:45 (already done)
                           thistime = hour + min;

                        } else {

                           if (min == 55) {           // if we just did x:55

                              min = 0;               // make sure we skip past x:00 (already done)
                              hour += 1;
                              thistime = hour + min;
                           }
                        }
                     }
                  }
               }

               if (club.equals( "greenwich" ) && customTees == true) {   // Greenwich and 2-some date

                  if (thistime == 748) {            // if we just did 7:48 AM

                     betwn = betwn2;                // then switch to normal intervals

                     customTees = false;            // no more custom tees for this day
                  }
               }


/*             // Club does not wish to use this custom anymore
               if (club.equals( "greenhills" ) && customTees == true) {           // Green Hills and Sunday

                  if (thistime == 1000) {            // if we just did 10:00 AM

                     betwn = 8;                // then switch to 8 min intervals

                  } else {

                     if (thistime == 1400) {            // if we just did 2:00 PM

                        betwn = 10;                     // then switch back to 10 min intervals

                        customTees = false;             // no more custom tees for this day
                     }
                  }
               }
*/
               if (club.equals( "bellevuecc" ) && customTees == true) {       // Bellevue and Tuesday

                  if (thistime == 1550) {            // if we just did 3:50 PM

                     betwn = 10;                     // then switch to 10 min intervals

                  } else {

                     if (thistime == 1800) {            // if we just did 6:00 PM

                        betwn = betwn2;                 // then switch back to 8 min intervals

                        customTees = false;             // no more custom tees for this day
                     }
                  }
               }

               /*
               if (club.equals( "congressional" ) && (customTees == true)) {

                  if (thistime == 1048) {            // if we just did 10:48 AM

                     betwn = betwn2;                // then switch to normal intervals (10 mins)

                     customTees = false;            // no more dbl tees

                     thistime = 1050;              // change time so we skip to 11:00
                     hour = 10;
                     min = 50;
                  }
               }
                */

               if (club.equals( "imperialgc" ) && (customTees == true)) {

                  if (thistime == 822) {            // if we just did 8:22 AM

                     betwn = betwn2;                // then switch to normal intervals (8 mins)

                     alt_betwn = 0;                 // stop alternate intervals

                     customTees = false;            // no more dbl tees
                  }
               }

               if (club.equals( "eugenecc" ) && (customTees == true)) {

                  if (thistime == 808) {           // if we just did 8:08 AM

                     betwn = 9;                     // then switch to 9 min intervals
                  }

                  if (thistime == 902) {           // if we just did 9:02 AM

                     betwn = 8;                     // then switch back to 8 min intervals
                  }

                  if (thistime == 950) {           // if we just did 9:50 AM

                     betwn = 10;                   // then switch to normal intervals (10 mins)

                     customTees = false;           // no more changes
                  }
               }

               if (club.equals( "apawamis" ) && APspec1 == true) {

                  if (thistime == 830) {           // if we just did 8:30 AM

                     betwn = betwn2;                // then switch to normal intervals
                  }
               }

               if (club.equals( "tpctc" )) {

                  if (thistime == 900) {           // if we just did 9:00 AM

                     betwn = 9;                    // switch to 9 min intervals

                  } else {

                     if (thistime == 1200) {          // if we just did 12:00 Noon

                        betwn = 10;                    // switch to 10 min intervals

                     } else {

                        if (thistime == 1400) {          // if we just did 2:00 PM

                           betwn = 9;                    // switch back to 9 min intervals
                        }
                     }
                  }
               }

               /*
               if (club.equals( "brooklawn" )) {

                  if (name.equals( "Tuesday" ) && thistime == 1104) {   // if we just did 11:04 AM

                     betwn = 9;                    // switch to 9 min intervals

                  } else {

                     if ((name.equals( "Saturday" ) || name.equals( "Sunday" )) && thistime == 1028) {   // if we just did 10:28 AM

                        betwn = 9;                    // switch to 9 min intervals
                     }
                  }
               }
                */

               if (club.equals( "noaks" )) {

                  if (name.equals( "Tuesday" ) && thistime == 1154) {   // if we just did 11:54 AM

                     betwn = betwn2;                // then switch to normal intervals
                  }
               }


               if (club.equals( "elniguelcc" )) {

                  if (name.equals( "Tuesday" ) && thistime == 956) {   // if we just did 9:56 AM

                     thistime = 952;              // change time so we skip to 10:00
                     hour = 9;
                     min = 52;
                  }

                  if (name.equals( "Tuesday" ) && thistime == 1000) {   // if we just did 10:00 AM

                     betwn = 7;                    // switch to 7 & 8 min intervals
                     alt_betwn = 8;
                     alt = 0;                      // init the alt switch
                  }

                  if (name.equals( "Thursday" ) && thistime == 1048) {   // if we just did 10:48 AM

                     thistime = 1052;              // change time so we skip to 11:00
                     hour = 10;
                     min = 52;
                  }

                  if (name.equals( "Thursday" ) && thistime == 1100) {   // if we just did 11:00 AM

                     betwn = 7;                    // switch to 7 & 8 min intervals
                     alt_betwn = 8;
                     alt = 0;                      // init the alt switch
                  }
               }

               if (club.equals( "bigsprings" ) && (BSsaturday == true || BSholiday == true)) {

                  if (thistime == 848) {           // if we just did 8:48 AM

                     thistime = 852;                // change time so we skip to 9:00
                     hour = 8;
                     min = 52;

                  } else {

                     if (thistime == 948) {           // if we just did 9:48 AM

                        betwn = betwn2;                // then switch to normal intervals (10 mins)

                        if (BSsaturday == true) {

                           thistime = 1210;                // change time so we skip to 12:20
                           hour = 12;
                           min = 10;
                           BSsaturday = false;             // quit doing dbl tees and custom

                        } else {

                           if (BSholiday == true) {

                              thistime = 1220;                // change time so we skip to 12:30 (keep doing dbl tees)
                              hour = 12;
                              min = 20;
                           }
                        }

                     } else {

                        if (BSholiday == true && thistime == 1430) {   // if we just did 2:30 PM

                           thistime = 1650;                // change time so we skip to 5:00
                           hour = 16;
                           min = 50;
                           BSholiday = false;              // quit doing dbl tees and custom
                        }
                     }
                  }
               }

               if (club.equals( "hudsonnatl" ) && BSsaturday == true) {

                  if (thistime == 830) {           // if we just did 8:30 AM

                     betwn = betwn2;                // then switch to normal intervals (15 mins)
                  }
               }

               //
               //  The Stanwich Club - switch from 6 min ints to 8 min ints based on day
               //
               if (club.equals( "stanwichclub" )) {

                  if (name.equalsIgnoreCase( "saturday" )  || name.equalsIgnoreCase( "sunday" )  || name.equalsIgnoreCase( "monday" )) {

                     if (thistime == 824) {           // if we just did 8:24 AM

                        betwn = betwn2;                // then switch to normal intervals (8 mins)
                     }
                  }

                  if (name.equalsIgnoreCase( "tuesday" ) || name.equalsIgnoreCase( "wednesday" )) {

                     if (thistime == 1006) {           // if we just did 10:06 AM

                        betwn = betwn2;                // then switch to normal intervals (8 mins)
                     }
                  }
               }

               //
               //  Meadow Springs - 8 min intervals except last interval of each hour is 12 minutes
               //                   (i.e.  7:00 - 7:48 then skip to 8:00, etc.
               //
               if (club.equals( "meadowsprings" )) {        // Meadow Srings custom

                  if (min == 48) {                    // if xx:48

                     thistime = thistime + 4;         // change time so we skip to xy:00
                     min = 52;
                  }
               }

               //
               //  Skaneateles - 10 min intervals except the following are 8 minutes
               //
               //       Mondays starting at Noon (1200), except for Memorial Day, 7/03 and Labor Day
               //       W/Es and Holidays at 11:00 AM
               //
               if (club.equals( "skaneateles" )) {        // Skaneateles custom

                  if (name.equalsIgnoreCase( "monday" )) {                           // Monday ?

                     if (date != memDay1 && date != july3rd1 && date != laborDay1 && date != memDay2 && date != july3rd2 && date != laborDay2) {

                        if (thistime == 1200) {           // if we just did 12:00 PM (noon)

                           betwn = 8;                     // then switch to 8 min intervals
                        }
                     }
                  }

                  if (name.equalsIgnoreCase( "saturday" ) || name.equalsIgnoreCase( "sunday" ) || date == july3rd1 || date == july3rd2 ||
                      date == memDay1 || date == july4th1 || date == laborDay1 || date == memDay2 || date == july4th2 || date == laborDay2) {

                     if (thistime == 1100) {           // if we just did 11:00 AM

                        betwn = 8;                     // then switch to 8 min intervals
                     }
                  }
               }

               /*
               //
               //  St Albans weekends - 6:35, 6:44, 6:52, 7:00, 7:09, 7:17, .... (on 9 & 8 min intervals)
               //
               if (club.equals( "stalbans" ) && (name.equals( "Saturday" ) || name.equals( "Sunday" ))) {

                  if (thistime == 652) {           // if we just did 6:52 AM

                     alt = 1;                      // then use alt_betwn again (8 mins)

                  } else {

                     if (thistime == 700) {           // if we just did 7:00 AM

                        alt = 0;                      // then return to normal alternating
                     }
                  }
               }
                */

               //
               //  Tamarack - custom tees each afternoon based on day of week
               //
               if (club.equals( "tamarack" )) {

                  if (name.equals( "Saturday" ) || name.equals( "Sunday" ) ||
                      date == memDay1 || date == july4th1 || date == laborDay1) {    // if w/e or holiday

                     if (thistime == 1053) {           // if we just did 10:53 AM

                        betwn = 9;                     // then switch to 9 min intervals

                     } else {

                        if (thistime == 1259) {           // if we just did 12:59 PM

                           betwn = 10;                     // then switch to 10 min intervals for rest of day
                        }
                     }

                  } else {

                     if (name.equals( "Friday" )) {

                        if (thistime == 1157) {           // if we just did 11:57 AM

                           betwn = 10;                     // then switch to 10 min intervals
                        }

                     } else {                             // other weekdays

                        if (thistime == 1357) {           // if we just did 1:57 PM

                           betwn = 10;                     // then switch to 10 min intervals
                        }
                     }
                  }
               }


               //
               //  Sewickley Heights - custom tees each Thursday from 4/15 - 10/15
               //
               if (club.equals( "sewickley" )) {

                  if (name.equals( "Thursday" ) &&
                      shortDate > 414 && shortDate < 1016) {

                     if (thistime == 830) {           // if we just did 8:30 AM

                        betwn = 5;                     // then switch to 5 min intervals

                     } else {

                        if (thistime == 930) {           // if we just did 9:30 AM

                           betwn = 10;                     // then switch back to 10 min intervals for rest of day
                        }
                     }

                  } else {

                     if (name.equals( "Friday" )) {

                        if (thistime == 1157) {           // if we just did 11:57 AM

                           betwn = 10;                     // then switch to 10 min intervals
                        }

                     } else {                             // other weekdays

                        if (thistime == 1357) {           // if we just did 1:57 PM

                           betwn = 10;                     // then switch to 10 min intervals
                        }
                     }
                  }
               }


               //
               //  The Pinery - 6:51, 7:00, 7:09, 7:17, .... 7:51, 8:00, 8:09, 8:17... (on 9 & 8 min intervals)
               //
               if (club.equals( "pinery" )) {

                  int pineMins = thistime / 100;             // isolate hr value

                  pineMins = thistime - (pineMins * 100);    // get min value

                  if (pineMins == 00) {           // if we just did x:00

                     alt = 0;                     // then use betwn again (9 mins) to get to x:09
                  }
               }

               //
               //  Tripoli - every hour has these times:  :00, :08, :17, :25, :34, :42, :51
               //
               if (club.equals( "tripoli" )) {        // Tripoli custom

                  if (min == 0) {

                     min = 8;        // next time

                  } else {

                     if (min == 8) {

                        min = 17;

                     } else {

                        if (min == 17) {

                           min = 25;

                        } else {

                           if (min == 25) {

                              min = 34;

                           } else {

                              if (min == 34) {

                                 min = 42;

                              } else {

                                 if (min == 42) {

                                    min = 51;

                                 } else {        // must have just done :51 - now start next hour (xx:00)

                                    min = 0;
                                    hour++;        // next hour
                                 }
                              }
                           }
                        }
                     }
                  }

               } else {       // not Tripoli (all other clubs)

                  //
                  //   Bump time to next interval
                  //
                  if (alt_betwn > 0) {        // if alternate minutes specified in parms

                     if (alt == 1) {          //  and this is an alternate time (every other)

                        min = min + alt_betwn;     // bump minutes by alt minutes between times
                        alt = 0;                   // toggle the alt switch

                     } else {

                        min = min + betwn;         // bump minutes by minutes between times
                        alt = 1;                   // toggle the alt switch
                     }
                  } else {

                     min = min + betwn;     // no alt minutes - bump minutes by minutes between times
                  }

               }        // end of IF tripoli
            }           // end of IF piedmont

            if (min > 59) {

               min = min - 60;     // adjust past hour count
               hour = hour + 1;
            }

            count++;                     // bump tee time counter

            thistime = hour * 100;       // recalc thistime (hhmm)
            thistime = thistime + min;

         }   // end of while loop - build all slots for this day


         //
         //  Done with tee sheet for this day and club
         //
         //   If Bonnie Briar and a custom day, build custom double tees
         //
         if (club.equals( "bonniebriar" ) && customTees == true) {

            error = "Build custom double tees for Bonnie Briar ";

            //
            //  First add the back tees (7:15 - 9:15)
            //
            betwn = 12;                   // 12 minute intervals
            thistime = 715;               // setup first back time
            hour = 7;
            min = 15;
            first = 0;

            while (thistime < 916) {            // do up to 9:15

               pstmt3.clearParameters();        // clear the parms
               pstmt3.setLong(1, date);         // put the parms in stmt for tee slot
               pstmt3.setInt(2, month);
               pstmt3.setInt(3, day);
               pstmt3.setInt(4, year);
               pstmt3.setString(5, name);
               pstmt3.setInt(6, hour);
               pstmt3.setInt(7, min);
               pstmt3.setInt(8, thistime);
               pstmt3.setInt(9, first);
               pstmt3.setShort(10, fb1);           // do back 9 (1)
               pstmt3.setString(11, course);       // course name

               pstmt3.executeUpdate();        // execute the prepared stmt

               min = min + betwn;             // bump minutes by minutes between times

               if (min > 59) {

                  min = min - 60;           // adjust past hour count
                  hour = hour + 1;
               }

               thistime = hour * 100;       // recalc thistime (hhmm)
               thistime = thistime + min;
            }

            //
            //  Now change the front tees to cross-overs (9:16 - 11:29)
            //
            PreparedStatement pstmtbb = con.prepareStatement (
                "UPDATE teecurr2 SET fb = 9 " +
                        "WHERE date = ? AND time > 915 AND time < 1130 AND fb = 0 AND courseName = ?");

            //
            //  execute the prepared statement to update the tee time slot
            //
            pstmtbb.clearParameters();        // clear the parms
            pstmtbb.setLong(1, date);
            pstmtbb.setString(2, course);
            pstmtbb.executeUpdate();

            pstmtbb.close();

         }          // end of Bonnie Briar custom

         error = "Done - Close Stmts ";

         pstmt3.close();

      }             // end of IF xx = 0 (ready to build sheets)

   }
   catch (Exception e) {

      throw new Exception("Error Building New Tee Sheets - buildTee " + error + "  " + e.getMessage());
   }

 }


 //************************************************************************
 // buildHTee - Builds a custom tee sheet for Hazeltine Natl. for the date requested.
 //
 //   called by: scanTee (below) to build missing tee sheets
 //
 //************************************************************************
 //
 //   NOTE:  no longer used as of 4/23/13 BP
 //
 /*
 public static void buildHTee(int year, int month, int day, int day_name, String course, Connection con)
         throws Exception {


   Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs1 = null;
   ResultSet rs2 = null;
   ResultSet rs3 = null;

   String error = "None";

   //
   //  init some variables to be used in building the tee slots
   //
   int hour = 0;              // hour value for tee time slot
   int min = 0;              // minute value for slot
   int time = 0;
   int first = 0;
   int wday = 0;
   int length = 0;

   short xx = 0;
   short fb = 0;
   short fb0 = 0;
   short fb1 = 1;
   String dbl_recurr = "";
   String sfb = "";
   boolean check_dbl = false;

   //
   //   Array for day names
   //
   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

   //
   //   Array for Weekday Times
   //
   int wdLength = 68;
   int [] weekdays = { 724, 733, 742, 751, 800, 809, 818, 827, 836, 850, 900, 910, 920, 930, 940, 950, 1000,
                       1010, 1020, 1030, 1040, 1100, 1110, 1120, 1130, 1140, 1150, 1200, 1210, 1220, 1230,
                       1240, 1250, 1300, 1310, 1320, 1330, 1340, 1350, 1400, 1410, 1420, 1438, 1447, 1456,
                       1505, 1514, 1523, 1532, 1541, 1556, 1605, 1614, 1623, 1632, 1641, 1650, 1659, 1708,
                       1717, 1726, 1735, 1744, 1753, 1802, 1811, 1820, 1829 };

   //
   //   Array for Weekend Front Times
   //
   int weLength = 72;
   int [] weekends = { 710, 719, 728, 737, 746, 755, 804, 813, 822, 831, 840, 849, 858, 907, 916, 925, 934, 943, 952,
                       1010, 1019, 1028, 1037, 1046, 1055, 1110, 1119, 1128, 1137, 1146, 1155, 1204, 1213, 1222,
                       1231, 1240, 1249, 1307, 1316, 1325, 1334, 1343, 1352, 1401, 1410, 1419, 1428, 1446, 1455,
                       1504, 1513, 1522, 1531, 1540, 1549, 1558, 1607, 1625, 1634, 1643, 1652, 1701, 1710,
                       1719, 1728, 1737, 1746, 1755, 1804, 1813, 1822, 1831 };

   //
   //   Array for Weekend Back Times
   //
   int weLength2 = 6;
   int [] weekend2 = { 800, 809, 818, 827, 836, 845 };

   //
   //  Gather all the info we need to build the table
   //
   long date = year * 10000;          // create a date field from input values
   date = date + (month * 100);
   date = date + day;                 // date = yyyymmdd (for comparisons)

   //
   //  day_name is an index representing the day of the week (01 = sun, 02 = mon, etc.)
   //
   if (day_name > 7) {

      throw new Exception("Invalid day_name value received - buildHTee");
   }

   String name = day_table[day_name];  // get name for day

   //
   //  Determine which tee sheet to build:  0 = weekday, 1 = weekend
   //
   //    weekends = Sun, Sat, Memorial Day, July 4th Monday, Labor Day
   //
   wday = 0;              // default to weekday values
   length = wdLength;

   if (day_name == 1 || day_name == 7 || date == memDay1 || date == july4th1 || date == laborDay1 ||
       date == memDay2 || date == july4th2 || date == laborDay2) {

      wday = 1;            // weekend tee sheet
      length = weLength;
   }

   //
   //
   try {
      PreparedStatement pstmt1 = con.prepareStatement (
         "SELECT xx FROM clubparm2 WHERE courseName = ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setString(1, course);
      rs2 = pstmt1.executeQuery();      // execute the prepared stmt

      if (rs2.next()) {

         xx = rs2.getShort(1);

      } else {

         xx = 0;
      }                // end of if clubparms (for this course)
      pstmt1.close();

      //
      //  if we are to wait for initial setup, then skip
      //
      //  xx is set to zero in Proshop_parms when we set the club parms for the first time
      //  zero indicates we should wait for config completion before building the tees
      //
      if (xx != 0) {

         error = "Tee Times Exist?";

         //
         //  Check if any tee time slots already exist for this date on this course
         //
         PreparedStatement pstmt = con.prepareStatement (
            "SELECT fb FROM teecurr2 WHERE date = ? AND courseName = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setLong(1, date);         // put the parm in pstmt
         pstmt.setString(2, course);
         rs = pstmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {               // if no tee times exist

            pstmt.close();
            return;                     // not ready yet
         }
         pstmt.close();

         //
         // Prepared statement to put this days tee times in the teecurr2 table
         //
         PreparedStatement pstmt3 = con.prepareStatement (
            "INSERT INTO teecurr2 (date, mm, dd, yy, day, hr, min, time, event, event_color, " +
            "restriction, rest_color, player1, player2, player3, player4, username1, " +
            "username2, username3, username4, p1cw, p2cw, p3cw, p4cw, first, in_use, in_use_by, " +
            "event_type, hndcp1, hndcp2, hndcp3, hndcp4, show1, show2, show3, show4, fb, " +
            "player5, username5, p5cw, hndcp5, show5, notes, hidenotes, lottery, courseName, blocker, " +
            "proNew, proMod, memNew, memMod, rest5, rest5_color, mNum1, mNum2, mNum3, mNum4, mNum5, " +
            "lottery_color, userg1, userg2, userg3, userg4, userg5, hotelNew, hotelMod, orig_by, " +
            "conf, p91, p92, p93, p94, p95, pos1, pos2, pos3, pos4, pos5, hole, create_date) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, '', '', '', '', '', '', '', '', '', '', " +
            "'', '', '', '', '', '', ?, 0, '', 0, 0, 0, 0, 0, 0, 0, 0, 0, ?, '', '', '', 0, 0, '', 0, " +
            "'', ?, '', 0, 0, 0, 0, '', '', '', '', '', '', '', '', '', '', '', '', '', 0, 0, '', '', " +
            "0, 0, 0, 0, 0, 0, 0, 0, 0, 0, '', now())");

         error = "Check Dbl Tees?";

         //
         // See if we need to check the dbltee table each time
         //
         PreparedStatement pstmt8 = con.prepareStatement (
                "SELECT name FROM dbltee2 WHERE sdate <= ? AND edate >= ? AND (courseName = ? OR courseName = '-ALL-')");

         pstmt8.clearParameters();        // clear the parms
         pstmt8.setLong(1, date);         // put the parm in stmt
         pstmt8.setLong(2, date);
         pstmt8.setString(3, course);
         rs1 = pstmt8.executeQuery();      // execute the prepared stmt

         if (rs1.next()) {

            check_dbl = true;    // we have to check dbltee table
         }

         pstmt8.close();


         first = 1;       // first slot indicator

         //
         //  Now we can build the tee time slots
         //
         for (int i = 0; i < length; i++) {           // length = # of times in appropriate array

            //
            //  get the time for this slot
            //
            if (wday == 0) {       // if weekday

               time = weekdays[i];     // get weekday time

            } else {

               time = weekends[i];     // get weekend time
            }

            hour = time / 100;
            min = time - (hour * 100);

            pstmt3.clearParameters();        // clear the parms
            pstmt3.setLong(1, date);         // put the parms in stmt for tee slot
            pstmt3.setInt(2, month);
            pstmt3.setInt(3, day);
            pstmt3.setInt(4, year);
            pstmt3.setString(5, name);
            pstmt3.setInt(6, hour);
            pstmt3.setInt(7, min);
            pstmt3.setInt(8, time);

            //
            // check for double tees if there are any for this day
            //
            fb = 0;

            if (check_dbl) {             // must we check double tees?

               //
               // Prepared statement to search the double tees table for 'double tee' times
               //
               PreparedStatement pstmt6 = con.prepareStatement (
                  "SELECT recurr FROM dbltee2 WHERE sdate <= ? AND edate >= ? " +
                  "AND stime1 <= ? AND etime1 >= ? AND (courseName = ? OR courseName = '-ALL-')");

               error = "Check for Dbl Tees";

               pstmt6.clearParameters();        // clear the parms
               pstmt6.setLong(1, date);         // put the parms in stmt
               pstmt6.setLong(2, date);
               pstmt6.setInt(3, time);
               pstmt6.setInt(4, time);
               pstmt6.setString(5, course);
               rs3 = pstmt6.executeQuery();      // see if Front and Back to be built

               loop2:
               while (rs3.next()) {

                  dbl_recurr = rs3.getString(1);   // dbl tee found for this time

                  //
                  //  We must check the recurrence for this day (Monday, etc.)
                  //
                  if (dbl_recurr.equalsIgnoreCase( "every " + name )) {

                     fb = 2;                        // set double tees for this time
                     break loop2;                   // done checking - exit while loop
                  }

                  if (dbl_recurr.equalsIgnoreCase( "every day" )) {   // if everyday

                     fb = 2;                        // set double tees for this time
                     break loop2;                   // done checking - exit while loop
                  }

                  if ((dbl_recurr.equalsIgnoreCase( "all weekdays" )) &&
                      (!name.equalsIgnoreCase( "saturday" )) &&
                      (!name.equalsIgnoreCase( "sunday" ))) {

                     fb = 2;                        // set double tees for this time
                     break loop2;                   // done checking - exit while loop
                  }

                  if ((dbl_recurr.equalsIgnoreCase( "all weekends" )) &&
                      (name.equalsIgnoreCase( "saturday" ))) {

                     fb = 2;                        // set double tees for this time
                     break loop2;                   // done checking - exit while loop
                  }

                  if ((dbl_recurr.equalsIgnoreCase( "all weekends" )) &&
                      (name.equalsIgnoreCase( "sunday" ))) {

                     fb = 2;                        // set double tees for this time
                     break loop2;                   // done checking - exit while loop
                  }

               }   // end of loop2 while loop

               pstmt6.close();

               if (fb == 0) {                        // if no hit, check 'no tees' times (cross-over)

                  //
                  // Prepared statement to search the double tees table for 'no tee' times (cross-overs)
                  //
                  PreparedStatement pstmt7 = con.prepareStatement (
                     "SELECT recurr FROM dbltee2 WHERE sdate <= ? AND edate >= ? " +
                     "AND stime2 <= ? AND etime2 >= ? AND (courseName = ? OR courseName = '-ALL-')");

                  error = "Check for Cross-Over Tees";

                  pstmt7.clearParameters();        // clear the parms
                  pstmt7.setLong(1, date);         // put the parms in stmt
                  pstmt7.setLong(2, date);
                  pstmt7.setInt(3, time);
                  pstmt7.setInt(4, time);
                  pstmt7.setString(5, course);
                  rs1 = pstmt7.executeQuery();      // check for cross-over time

                  loop3:
                  while (rs1.next()) {

                     dbl_recurr = rs1.getString(1);   // dbl tee found for this time

                     //
                     //  We must check the recurrence for this day (Monday, etc.)
                     //
                     if (dbl_recurr.equalsIgnoreCase( "every " + name )) {

                        fb = 9;                        // set double tees for this time
                        break loop3;                   // done checking - exit while loop
                     }

                     if (dbl_recurr.equalsIgnoreCase( "every day" )) {   // if everyday

                        fb = 9;                        // set double tees for this time
                        break loop3;                   // done checking - exit while loop
                     }

                     if ((dbl_recurr.equalsIgnoreCase( "all weekdays" )) &&
                         (!name.equalsIgnoreCase( "saturday" )) &&
                         (!name.equalsIgnoreCase( "sunday" ))) {

                        fb = 9;                        // set double tees for this time
                        break loop3;                   // done checking - exit while loop
                     }

                     if ((dbl_recurr.equalsIgnoreCase( "all weekends" )) &&
                         (name.equalsIgnoreCase( "saturday" ))) {

                        fb = 9;                        // set double tees for this time
                        break loop3;                   // done checking - exit while loop
                     }

                     if ((dbl_recurr.equalsIgnoreCase( "all weekends" )) &&
                         (name.equalsIgnoreCase( "sunday" ))) {

                        fb = 9;                        // set double tees for this time
                        break loop3;                   // done checking - exit while loop
                     }
                  }      // end of loop3 while

                  pstmt7.close();

               }     // end of 2nd double tee if

            }        // end of 'check dbl' if

            //
            //  Done checking for double tees
            //
            //     fb:  0 = no dbl tees (build all front tees - 0)
            //          2 = dbl tees found (build both - 0 & 1)
            //          9 = cross over time (build cross-over tee time - 9)
            //
            pstmt3.setInt(9, first);          // set first indicator

            if (fb == 2) {                    // double tee (do both front and back for this time) ?

               pstmt3.setShort(10, fb0);      // do front 9 first

            } else {

               pstmt3.setShort(10, fb);        // 0 = front, 9 = cross-over

            }
            pstmt3.setString(11, course);              // course name

            error = "Add Entry To Teecurr ";

            //
            //  Put the entry in the teecurr2 table and do the next one
            //
            pstmt3.executeUpdate();        // execute the prepared stmt

            first = 0;             // no longer first slot

            //
            // if 'double tee' was found for this time, build a 2nd tee time for the back 9
            //
            if (fb == 2) {

               error = "Add Entry To Teecurr 2";

               pstmt3.setInt(9, first);       // set first indicator
               pstmt3.setShort(10, fb1);        // do back 9 (1)

               pstmt3.executeUpdate();        // execute the prepared stmt

            }

         }   // end of FOR loop - build all slots for this day

         //
         //  Now, if weekend tee sheet, build the back tees (6 of them)
         //
         if (wday == 1) {          // if weekend

            first = 0;             // no longer first slot
            fb = 1;                // back tees

            for (int i = 0; i < weLength2; i++) {

               time = weekend2[i];              // get weekend back tee time

               hour = time / 100;
               min = time - (hour * 100);

               pstmt3.clearParameters();        // clear the parms
               pstmt3.setLong(1, date);         // put the parms in stmt for tee slot
               pstmt3.setInt(2, month);
               pstmt3.setInt(3, day);
               pstmt3.setInt(4, year);
               pstmt3.setString(5, name);
               pstmt3.setInt(6, hour);
               pstmt3.setInt(7, min);
               pstmt3.setInt(8, time);
               pstmt3.setInt(9, first);        // set first indicator
               pstmt3.setShort(10, fb);        // do back 9 (1)
               pstmt3.setString(11, course);   // course name

               pstmt3.executeUpdate();        // execute the prepared stmt
            }
         }
         pstmt3.close();

      }             // end of IF xx = 0 (ready to build sheets)

   }
   catch (Exception e) {

      throw new Exception("Error Building New Tee Sheets - buildHTee " + error + "  " + e.getMessage());
   }

 }
 * 
 */

 

 //************************************************************************
 //  moveTee - moves the oldest tee sheet from teecurr2 to teepast2
 //
 //  called by:  scanTee if too many sheets on teecurr2
 //              Support_init
 //
 //************************************************************************

 public static void moveTee(Connection con, long today_date, String club)
           throws Exception {


   ResultSet rs = null;
   ResultSet rs2 = null;

   long date = 0;
   int mm = 0;
   int dd = 0;
   int yy = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int proNew = 0;
   int proMod = 0;
   int memNew = 0;
   int memMod = 0;
   int hotelNew = 0;
   int hotelMod = 0;
   int gotsome = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int pos1 = 0;
   int pos2 = 0;
   int pos3 = 0;
   int pos4 = 0;
   int pos5 = 0;
   int guest_id1 = 0;
   int guest_id2 = 0;
   int guest_id3 = 0;
   int guest_id4 = 0;
   int guest_id5 = 0;
   int nopost1 = 0;
   int nopost2 = 0;
   int nopost3 = 0;
   int nopost4 = 0;
   int nopost5 = 0;
   int teecurr_id = 0;
   int pace_status_id = 0;
   int custom_int = 0;
   int event_type = 0;


   String day = "";
   String event = "";
   String ecolor = "";
   String rest = "";
   String rcolor = "";
   String notes = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
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
   String mNum1 = "";
   String mNum2 = "";
   String mNum3 = "";
   String mNum4 = "";
   String mNum5 = "";
   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";
   String mship1 = "";
   String mship2 = "";
   String mship3 = "";
   String mship4 = "";
   String mship5 = "";
   String mtype1 = "";
   String mtype2 = "";
   String mtype3 = "";
   String mtype4 = "";
   String mtype5 = "";
   String gtype1 = "";
   String gtype2 = "";
   String gtype3 = "";
   String gtype4 = "";
   String gtype5 = "";
   String course = "";
   String orig_by = "";
   String conf = "";
   String blocker = "";
   String custom_string = "";
   String create_date = "";
   String last_mod_date = "";
   String errorMsg = "";
   String custom_disp1 = "";
   String custom_disp2 = "";
   String custom_disp3 = "";
   String custom_disp4 = "";
   String custom_disp5 = "";
   String tflag1 = "";
   String tflag2 = "";
   String tflag3 = "";
   String tflag4 = "";
   String tflag5 = "";
   String hole = "";

   short show1 = 0;
   short show2 = 0;
   short show3 = 0;
   short show4 = 0;
   short show5 = 0;
   short fb = 0;

   int grev1 = 0;
   int grev2 = 0;
   int grev3 = 0;
   int grev4 = 0;
   int grev5 = 0;

   boolean hasHistory = false;

   PreparedStatement pstmt;
   PreparedStatement pstmt1;
   PreparedStatement pstmt2;


/*
   //
   //  Create table to hold empty tee times in case it has not already been added for this club
   //
   try {
      Statement stmt = con.createStatement();        // create a statement

      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS teepastempty(" +
                         "date bigint, mm smallint, dd smallint, " +
                         "yy integer, day varchar(10), hr smallint, min smallint, time integer, " +
                         "event varchar(30), event_color varchar(24), restriction varchar(30), " +
                         "rest_color varchar(24), fb smallint, courseName varchar(30), " +
                         "proNew integer, proMod integer, memNew integer, memMod integer, " +
                         "hotelNew integer, hotelMod integer, orig_by varchar(15), conf varchar(15), " +
                         "index ind1 (date, time, fb, courseName), " +
                         "index ind2 (date, courseName))");

      stmt.close();

   }
   catch (SQLException e) {

      logError("Error creating teepastempty table in SystemUtils moveTee for club: " +club+ ". Exception= " + e.getMessage());                                       // log it and continue
   }


   // load up and store all the guest types in an array
   String [] gtypes = new String[36];
   int [] grevs = new int[36];
   int total_guests = 0;
   int x = 0;

   Statement stmt = null;
   stmt = con.createStatement();
   rs = stmt.executeQuery("SELECT guest, revenue FROM guest5");

   while (rs.next()) {

       gtypes[x] = rs.getString(1);
       grevs[x] = rs.getInt(2);
       x++;
   }

   if (x > 0) total_guests = x - 1; // make sure we found at least one guest type

*/

   int x = 0;

   parmClub parm = new parmClub(0, con);

   getClub.getParms(con, parm, 0);

   int total_guests = parm.MAX_Guests;

   String [] gtypes = new String [parm.MAX_Guests];
   int [] grevs = new int [parm.MAX_Guests];

   gtypes = parm.guest;
   grevs = parm.gRev;

   //
   //  Get all tee sheets in teecurr2 older than today (one sheet per day)
   //
   //  ************* today_date has been adjusted for time zone by scanTee *********
   //
   try {

      pstmt1 = con.prepareStatement (
                "SELECT * FROM teecurr2 WHERE date < ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setLong(1, today_date);   // put the parms in stmt for tee slot
      rs = pstmt1.executeQuery();      // get all tee slots older than today

      while (rs.next()) {

         mtype1 = "";        // init member2b values in case player is not a member
         mtype2 = "";
         mtype3 = "";
         mtype4 = "";
         mtype5 = "";
         mship1 = "";
         mship2 = "";
         mship3 = "";
         mship4 = "";
         mship5 = "";
         gtype1 = "";
         gtype2 = "";
         gtype3 = "";
         gtype4 = "";
         gtype5 = "";
         tflag1 = "";
         tflag2 = "";
         tflag3 = "";
         tflag4 = "";
         tflag5 = "";
         grev1 = 0;
         grev2 = 0;
         grev3 = 0;
         grev4 = 0;
         grev5 = 0;
         hasHistory = false;

         teecurr_id = rs.getInt("teecurr_id");
         date = rs.getLong("date");
         mm = rs.getInt("mm");
         dd = rs.getInt("dd");
         yy = rs.getInt("yy");
         day = rs.getString("day");
         hr = rs.getInt("hr");
         min = rs.getInt("min");
         time = rs.getInt("time");
         event = rs.getString("event");
         ecolor = rs.getString("event_color");
         rest = rs.getString("restriction");
         rcolor = rs.getString("rest_color");
         player1 = rs.getString("player1");
         player2 = rs.getString("player2");
         player3 = rs.getString("player3");
         player4 = rs.getString("player4");
         user1 = rs.getString("username1");
         user2 = rs.getString("username2");
         user3 = rs.getString("username3");
         user4 = rs.getString("username4");
         p1cw = rs.getString("p1cw");
         p2cw = rs.getString("p2cw");
         p3cw = rs.getString("p3cw");
         p4cw = rs.getString("p4cw");
         show1 = rs.getShort("show1");
         show2 = rs.getShort("show2");
         show3 = rs.getShort("show3");
         show4 = rs.getShort("show4");
         fb = rs.getShort("fb");
         player5 = rs.getString("player5");
         user5 = rs.getString("username5");
         p5cw = rs.getString("p5cw");
         show5 = rs.getShort("show5");
         notes = rs.getString("notes");
         course = rs.getString("courseName");
         blocker = rs.getString("blocker");
         proNew = rs.getInt("proNew");
         proMod = rs.getInt("proMod");
         memNew = rs.getInt("memNew");
         memMod = rs.getInt("memMod");
         mNum1 = rs.getString("mNum1");
         mNum2 = rs.getString("mNum2");
         mNum3 = rs.getString("mNum3");
         mNum4 = rs.getString("mNum4");
         mNum5 = rs.getString("mNum5");
         userg1 = rs.getString("userg1");
         userg2 = rs.getString("userg2");
         userg3 = rs.getString("userg3");
         userg4 = rs.getString("userg4");
         userg5 = rs.getString("userg5");
         hotelNew = rs.getInt("hotelNew");
         hotelMod = rs.getInt("hotelMod");
         orig_by = rs.getString("orig_by");
         conf = rs.getString("conf");
         p91 = rs.getInt("p91");
         p92 = rs.getInt("p92");
         p93 = rs.getInt("p93");
         p94 = rs.getInt("p94");
         p95 = rs.getInt("p95");
         pos1 = rs.getInt("pos1");
         pos2 = rs.getInt("pos2");
         pos3 = rs.getInt("pos3");
         pos4 = rs.getInt("pos4");
         pos5 = rs.getInt("pos5");
         pace_status_id = rs.getInt("pace_status_id");
         custom_string = rs.getString("custom_string");
         custom_int = rs.getInt("custom_int");
         custom_disp1 = rs.getString("custom_disp1");
         custom_disp2 = rs.getString("custom_disp2");
         custom_disp3 = rs.getString("custom_disp3");
         custom_disp4 = rs.getString("custom_disp4");
         custom_disp5 = rs.getString("custom_disp5");
         guest_id1 = rs.getInt("guest_id1");
         guest_id2 = rs.getInt("guest_id2");
         guest_id3 = rs.getInt("guest_id3");
         guest_id4 = rs.getInt("guest_id4");
         guest_id5 = rs.getInt("guest_id5");
         tflag1 = rs.getString("tflag1");
         tflag2 = rs.getString("tflag2");
         tflag3 = rs.getString("tflag3");
         tflag4 = rs.getString("tflag4");
         tflag5 = rs.getString("tflag5");
         nopost1 = rs.getInt("nopost1");
         nopost2 = rs.getInt("nopost2");
         nopost3 = rs.getInt("nopost3");
         nopost4 = rs.getInt("nopost4");
         nopost5 = rs.getInt("nopost5");
         event_type = rs.getInt("event_type");
         hole = rs.getString("hole");
//         create_date = rs.getString("create_date");
//         last_mod_date = rs.getString("last_mod_date");   // replace these after fixing the date fields ***************


         // If tee time is empty but DOES have history entries associated with it, save it to teepast2 instead of teepast_empty
         if (player1.equals("") && player2.equals("") && player3.equals("") && player4.equals("") && player5.equals("")) {

             try {

                 pstmt2 = con.prepareStatement("SELECT date FROM teehist WHERE date = ? AND time = ? AND courseName = ? AND fb = ? LIMIT 1");
                 pstmt2.clearParameters();
                 pstmt2.setLong(1, date);
                 pstmt2.setInt(2, time);
                 pstmt2.setString(3, course);
                 pstmt2.setShort(4, fb);

                 rs2 = pstmt2.executeQuery();

                 if (rs2.next()) {
                     hasHistory = true;
                 }

                 pstmt2.close();

             } catch (Exception exc) {
                 hasHistory = false;
                 logError(exc.getMessage());        // Remove later
             }
         }

         //
         //  If the tee time is not empty, then save it in teepast
         //
         if (!player1.equals("") || !player2.equals("") || !player3.equals("") || !player4.equals("") || !player5.equals("") || hasHistory) {

            gotsome = 1;         // indicate at least one tee time

            // get mship1, mtype1, gtype1, grev1
            if (!user1.equals("")) {

                try {

                    pstmt2 = con.prepareStatement (
                            "SELECT m_ship, m_type FROM member2b WHERE username = ?");

                    pstmt2.clearParameters();
                    pstmt2.setString(1, user1);
                    rs2 = pstmt2.executeQuery();

                    if (rs2.next()) {

                        mship1 = rs2.getString(1);
                        mtype1 = rs2.getString(2);
                    }

                    pstmt2.close();

                } catch (Exception ignore) { }

            } else if (!player1.equals("")) {

                // check to see which type of guest this player is
                loop1:
                for (x=0; x <= total_guests; x++) {

                    try {
                    if (player1.substring(0, gtypes[x].length()).equalsIgnoreCase(gtypes[x])) {

                        gtype1 = gtypes[x];
                        grev1 = grevs[x];
                        break;
                    }
                    } catch (IndexOutOfBoundsException ignore) {}
                }

            }

            // get mship2, mtype2, gtype2, grev2
            if (!user2.equals("")) {

                try {

                    pstmt2 = con.prepareStatement (
                            "SELECT m_ship, m_type FROM member2b WHERE username = ?");

                    pstmt2.clearParameters();
                    pstmt2.setString(1, user2);
                    rs2 = pstmt2.executeQuery();

                    if (rs2.next()) {

                        mship2 = rs2.getString(1);
                        mtype2 = rs2.getString(2);
                    }

                    pstmt2.close();

                } catch (Exception ignore) { }

            } else if (!player2.equals("")) {

                // check to see which type of guest this player is
                loop12:
                for (x=0; x <= total_guests; x++) {

                    try {
                    if (player2.substring(0, gtypes[x].length()).equalsIgnoreCase(gtypes[x])) {

                        gtype2 = gtypes[x];
                        grev2 = grevs[x];
                        break;
                    }
                    } catch (IndexOutOfBoundsException ignore) {}
                }

            }

            // get mship3, mtype3, gtype3, grev3
            if (!user3.equals("")) {

                try {

                    pstmt2 = con.prepareStatement (
                            "SELECT m_ship, m_type FROM member2b WHERE username = ?");

                    pstmt2.clearParameters();
                    pstmt2.setString(1, user3);
                    rs2 = pstmt2.executeQuery();

                    if (rs2.next()) {

                        mship3 = rs2.getString(1);
                        mtype3 = rs2.getString(2);
                    }

                    pstmt2.close();

                } catch (Exception ignore) { }

            } else if (!player3.equals("")) {

                // check to see which type of guest this player is
                loop3:
                for (x=0; x <= total_guests; x++) {

                    try {
                    if (player3.substring(0, gtypes[x].length()).equalsIgnoreCase(gtypes[x])) {

                        gtype3 = gtypes[x];
                        grev3 = grevs[x];
                        break;
                    }
                    } catch (IndexOutOfBoundsException ignore) {}
                }

            }

            // get mship4, mtype4, gtype4, grev4
            if (!user4.equals("")) {

                try {

                    pstmt2 = con.prepareStatement (
                            "SELECT m_ship, m_type FROM member2b WHERE username = ?");

                    pstmt2.clearParameters();
                    pstmt2.setString(1, user4);
                    rs2 = pstmt2.executeQuery();

                    if (rs2.next()) {

                        mship4 = rs2.getString(1);
                        mtype4 = rs2.getString(2);
                    }

                    pstmt2.close();

                } catch (Exception ignore) { }

            } else if (!player4.equals("")) {

                // check to see which type of guest this player is
                loop4:
                for (x=0; x <= total_guests; x++) {

                    try {
                    if (player4.substring(0, gtypes[x].length()).equalsIgnoreCase(gtypes[x])) {

                        gtype4 = gtypes[x];
                        grev4 = grevs[x];
                        break;
                    }
                    } catch (IndexOutOfBoundsException ignore) {}
                }

            }

            // get mship5, mtype5, gtype5, grev5
            if (!user5.equals("")) {

                try {

                    pstmt2 = con.prepareStatement (
                            "SELECT m_ship, m_type FROM member2b WHERE username = ?");

                    pstmt2.clearParameters();
                    pstmt2.setString(1, user5);
                    rs2 = pstmt2.executeQuery();

                    if (rs2.next()) {

                        mship5 = rs2.getString(1);
                        mtype5 = rs2.getString(2);
                    }

                    pstmt2.close();

                } catch (Exception ignore) { }

            } else if (!player5.equals("")) {

                // check to see which type of guest this player is
                loop5:
                for (x=0; x <= total_guests; x++) {

                    try {
                    if (player5.substring(0, gtypes[x].length()).equalsIgnoreCase(gtypes[x])) {

                        gtype5 = gtypes[x];
                        grev5 = grevs[x];
                        break;
                    }
                    } catch (IndexOutOfBoundsException ignore) {}
                }

            }

            try {

               //
               //  Setup the prepared stmt for teepast
               //
               pstmt = con.prepareStatement (
                  "INSERT INTO teepast2 (date, mm, dd, yy, day, hr, min, time, event, event_color, " +
                  "restriction, rest_color, player1, player2, player3, player4, username1, " +
                  "username2, username3, username4, p1cw, p2cw, p3cw, p4cw, show1, show2, show3, show4, fb, " +
                  "player5, username5, p5cw, show5, courseName, proNew, proMod, memNew, memMod, " +
                  "mNum1, mNum2, mNum3, mNum4, mNum5, userg1, userg2, userg3, userg4, userg5, hotelNew, " +
                  "hotelMod, orig_by, conf, notes, p91, p92, p93, p94, p95, teecurr_id, pace_status_id, " +
//                  "create_date, last_mod_date, custom_string, custom_int, pos1, pos2, pos3, pos4, pos5) " +
                  "custom_string, custom_int, pos1, pos2, pos3, pos4, pos5," +
                  "mship1, mship2, mship3, mship4, mship5, mtype1, mtype2, mtype3, mtype4, mtype5, " +
                  "gtype1, gtype2, gtype3, gtype4, gtype5, " +
                  "grev1, grev2, grev3, grev4, grev5, custom_disp1, custom_disp2, custom_disp3, custom_disp4, custom_disp5, " +
                  "guest_id1, guest_id2, guest_id3, guest_id4, guest_id5, tflag1, tflag2, tflag3, tflag4, tflag5, " +
                  "nopost1, nopost2, nopost3, nopost4, nopost5, event_type, hole) " +
                  "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                  "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
//                  "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                  "?, ?, ?, ?, ?, ?, ?, " +
                  "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                  "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                  "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                  "?, ?, ?, ?, ?, ?, ?)");      // replace these with above after fixing the date fields ********************

               pstmt.clearParameters();        // clear the parms
               pstmt.setLong(1, date);         // put the parms in pstmt for tee slot
               pstmt.setInt(2, mm);
               pstmt.setInt(3, dd);
               pstmt.setInt(4, yy);
               pstmt.setString(5, day);
               pstmt.setInt(6, hr);
               pstmt.setInt(7, min);
               pstmt.setInt(8, time);
               pstmt.setString(9, event);
               pstmt.setString(10, ecolor);
               pstmt.setString(11, rest);
               pstmt.setString(12, rcolor);
               pstmt.setString(13, player1);
               pstmt.setString(14, player2);
               pstmt.setString(15, player3);
               pstmt.setString(16, player4);
               pstmt.setString(17, user1);
               pstmt.setString(18, user2);
               pstmt.setString(19, user3);
               pstmt.setString(20, user4);
               pstmt.setString(21, p1cw);
               pstmt.setString(22, p2cw);
               pstmt.setString(23, p3cw);
               pstmt.setString(24, p4cw);
               pstmt.setShort(25, show1);
               pstmt.setShort(26, show2);
               pstmt.setShort(27, show3);
               pstmt.setShort(28, show4);
               pstmt.setShort(29, fb);
               pstmt.setString(30, player5);
               pstmt.setString(31, user5);
               pstmt.setString(32, p5cw);
               pstmt.setShort(33, show5);
               pstmt.setString(34, course);
               pstmt.setInt(35, proNew);
               pstmt.setInt(36, proMod);
               pstmt.setInt(37, memNew);
               pstmt.setInt(38, memMod);
               pstmt.setString(39, mNum1);
               pstmt.setString(40, mNum2);
               pstmt.setString(41, mNum3);
               pstmt.setString(42, mNum4);
               pstmt.setString(43, mNum5);
               pstmt.setString(44, userg1);
               pstmt.setString(45, userg2);
               pstmt.setString(46, userg3);
               pstmt.setString(47, userg4);
               pstmt.setString(48, userg5);
               pstmt.setInt(49, hotelNew);
               pstmt.setInt(50, hotelMod);
               pstmt.setString(51, orig_by);
               pstmt.setString(52, conf);
               pstmt.setString(53, notes);
               pstmt.setInt(54, p91);
               pstmt.setInt(55, p92);
               pstmt.setInt(56, p93);
               pstmt.setInt(57, p94);
               pstmt.setInt(58, p95);
               pstmt.setInt(59, teecurr_id);
               pstmt.setInt(60, pace_status_id);
//               pstmt.setString(61, create_date);
//               pstmt.setString(62, last_mod_date);
//               pstmt.setString(63, custom_string);
//               pstmt.setInt(64, custom_int);
               pstmt.setString(61, custom_string);         // replace these with above after fixing the date fields ***************
               pstmt.setInt(62, custom_int);
               pstmt.setInt(63, pos1);
               pstmt.setInt(64, pos2);
               pstmt.setInt(65, pos3);
               pstmt.setInt(66, pos4);
               pstmt.setInt(67, pos5);
               pstmt.setString(68, mship1);
               pstmt.setString(69, mship2);
               pstmt.setString(70, mship3);
               pstmt.setString(71, mship4);
               pstmt.setString(72, mship5);
               pstmt.setString(73, mtype1);
               pstmt.setString(74, mtype2);
               pstmt.setString(75, mtype3);
               pstmt.setString(76, mtype4);
               pstmt.setString(77, mtype5);
               pstmt.setString(78, gtype1);
               pstmt.setString(79, gtype2);
               pstmt.setString(80, gtype3);
               pstmt.setString(81, gtype4);
               pstmt.setString(82, gtype5);
               pstmt.setInt(83, grev1);
               pstmt.setInt(84, grev2);
               pstmt.setInt(85, grev3);
               pstmt.setInt(86, grev4);
               pstmt.setInt(87, grev5);
               pstmt.setString(88, custom_disp1);
               pstmt.setString(89, custom_disp2);
               pstmt.setString(90, custom_disp3);
               pstmt.setString(91, custom_disp4);
               pstmt.setString(92, custom_disp5);
               pstmt.setInt(93, guest_id1);
               pstmt.setInt(94, guest_id2);
               pstmt.setInt(95, guest_id3);
               pstmt.setInt(96, guest_id4);
               pstmt.setInt(97, guest_id5);
               pstmt.setString(98, tflag1);
               pstmt.setString(99, tflag2);
               pstmt.setString(100, tflag3);
               pstmt.setString(101, tflag4);
               pstmt.setString(102, tflag5);
               pstmt.setInt(103, nopost1);
               pstmt.setInt(104, nopost2);
               pstmt.setInt(105, nopost3);
               pstmt.setInt(106, nopost4);
               pstmt.setInt(107, nopost5);
               pstmt.setInt(108, event_type);
               pstmt.setString(109, hole);

               pstmt.executeUpdate();        // move the tee slot to teepast

               pstmt.close();

            }
            catch (Exception exc2) {

               logError("Error1 in SystemUtils moveTee for club: " +club+ ". date=" +date+ " time=" +time+ " Exception= " + exc2.getMessage());                                       // log it

               //throw new Exception("Error reading Oldest Tee Sheet - moveTee: " + exc2.getMessage());
            }

         } else {

            //
            //  tee time is empty - move it to the old empty tee time table (teepastempty)
            //
            if (blocker.equals( "" )) {         // if tee time not blocked

               try {

                  //
                  //  Setup the prepared stmt for teepast
                  //
                  pstmt = con.prepareStatement (
                     "INSERT INTO teepastempty (date, mm, dd, yy, day, hr, min, time, event, event_color, " +
                     "restriction, rest_color, fb, courseName, " +
                     "proNew, proMod, memNew, memMod, " +
                     "hotelNew, hotelMod, orig_by, conf) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

                  pstmt.clearParameters();        // clear the parms
                  pstmt.setLong(1, date);         // put the parms in pstmt for tee slot
                  pstmt.setInt(2, mm);
                  pstmt.setInt(3, dd);
                  pstmt.setInt(4, yy);
                  pstmt.setString(5, day);
                  pstmt.setInt(6, hr);
                  pstmt.setInt(7, min);
                  pstmt.setInt(8, time);
                  pstmt.setString(9, event);
                  pstmt.setString(10, ecolor);
                  pstmt.setString(11, rest);
                  pstmt.setString(12, rcolor);
                  pstmt.setShort(13, fb);
                  pstmt.setString(14, course);
                  pstmt.setInt(15, proNew);
                  pstmt.setInt(16, proMod);
                  pstmt.setInt(17, memNew);
                  pstmt.setInt(18, memMod);
                  pstmt.setInt(19, hotelNew);
                  pstmt.setInt(20, hotelMod);
                  pstmt.setString(21, orig_by);
                  pstmt.setString(22, conf);

                  pstmt.executeUpdate();        // move the tee slot to teepast

                  pstmt.close();

               }
               catch (Exception exc2) {

                  logError("Error1b in SystemUtils moveTee for club: " +club+ ". Exception= " + exc2.getMessage());                                       // log it

                  throw new Exception("Error moving empty tee time - moveTee: " + exc2.getMessage());
               }
            }

         }     // end of if empty test

      }   // end of while loop

      pstmt1.close();

      //
      //   Gather the stats from this tee sheet and place in the stats table
      //
      if (gotsome != 0) {       // if any tee times are present

         // moveStats(con, club);         // go build stats

         //
         // If club is Cordillera or Catamount, go build a report file for them for the past day's tee sheet
         //
         if (club.equals( "cordillera" ) || club.equals( "catamount" )) {

            buildCordilleraReport(con, club, today_date);         // go build report file
         }
      }

      //
      //  Remove the oldest day's tee slots (rows) from teecurr2 table
      //
      try {

         pstmt2 = con.prepareStatement (
                      "DELETE FROM teecurr2 WHERE date < ?");

         pstmt2.clearParameters();        // clear the parms
         pstmt2.setLong(1, today_date);   // put the parms in stmt for tee slot
         pstmt2.executeUpdate();          // delete all tee slots for oldest date

         pstmt2.close();

      }
      catch (SQLException exc1) {

         logError("Error2 in SystemUtils moveTee.  Couldn't remove old tee times for club: " +club+ ", date=" + today_date + ". Exception= " + exc1.getMessage());                                       // log it

         throw new Exception("Error removing Oldest Tee Sheet - moveTee: " + exc1.getMessage());
      }

      //
      //  Remove any old lottery requests (general clean up)
      //
      try {

         pstmt2 = con.prepareStatement (
                      "DELETE FROM lreqs3 WHERE date < ?");

         pstmt2.clearParameters();        // clear the parms
         pstmt2.setLong(1, today_date);   // put the parms in stmt for tee slot
         pstmt2.executeUpdate();          // delete all tee slots for oldest date

         pstmt2.close();

         pstmt2 = con.prepareStatement (
                      "DELETE FROM actlott3 WHERE pdate < ?");

         pstmt2.clearParameters();        // clear the parms
         pstmt2.setLong(1, today_date);   // put the parms in stmt for tee slot
         pstmt2.executeUpdate();          // delete all tee slots for oldest date

         pstmt2.close();

      }
      catch (SQLException exc1) {

         logError("Error3 in SystemUtils moveTee for club: " +club+ ". Exception= " + exc1.getMessage());                                       // log it

         throw new Exception("Error removing Old Lottery Requests - moveTee: " + exc1.getMessage());
      }

   }
   catch (SQLException e) {

      logError("Error4 in SystemUtils moveTee for club: " +club+ ". Exception= " + e.getMessage());                                       // log it

      throw new Exception("Error Getting Old Tee Sheets in teecurr2 - moveTee: " + e.getMessage());
   }

 }  // end of moveTee


 // *********************************************************
 //  buildCordilleraReport - create a custom report file
 //                          for Coridllera and Catamount.
 //                          These files will be ftp'd nightly
 //                          to their server.
 // *********************************************************

   private static void buildCordilleraReport(Connection con, String club, long today_date) {


   PreparedStatement pstmt1;
   ResultSet rs = null;

   short show1 = 0;
   short show2 = 0;
   short show3 = 0;
   short show4 = 0;
   short show5 = 0;

   int mm = 0;
   int dd = 0;
   int yy = 0;
   int hr = 0;
   int min = 0;
   int mCount = 0;
   int i = 0;

   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";
   String gtype = "";
   String course = "";
   String sdate = "";
   String stime = "";
   String ampm = "";
   String record = "";
   String fname = "";

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con);          // allocate a parm block

   //
   //   Get guest types for this club
   //
   try {
      getClub.getParms(con, parm);
   }
   catch (Exception e1) {
   }

   //
   //  Arrays for guests
   //
   int [] gCounts = new int [parm.MAX_Guests];     // # of each guest type in tee time


   //
   //   Remove any guest types that are null - for tests below
   //
   for (i = 0; i < parm.MAX_Guests; i++) {

      if (parm.guest[i].equals( "" )) {

         parm.guest[i] = "$@#!^&*";      // make so it won't match player name
      }
   }


   //
   //  Get all tee times in teecurr2 older than today
   //
   try {

      pstmt1 = con.prepareStatement (
                "SELECT mm, dd, yy, hr, min, player1, player2, player3, player4, username1, username2, username3, username4, " +
                "show1, show2, show3, show4, player5, username5, show5, courseName " +
                "FROM teecurr2 WHERE date < ? ORDER BY date, time");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setLong(1, today_date);   // put the parms in stmt for tee slot
      rs = pstmt1.executeQuery();      // get all tee slots older than today

      while (rs.next()) {

         mm = rs.getInt("mm");
         dd = rs.getInt("dd");
         yy = rs.getInt("yy");
         hr = rs.getInt("hr");
         min = rs.getInt("min");
         player1 = rs.getString("player1");
         player2 = rs.getString("player2");
         player3 = rs.getString("player3");
         player4 = rs.getString("player4");
         user1 = rs.getString("username1");
         user2 = rs.getString("username2");
         user3 = rs.getString("username3");
         user4 = rs.getString("username4");
         show1 = rs.getShort("show1");
         show2 = rs.getShort("show2");
         show3 = rs.getShort("show3");
         show4 = rs.getShort("show4");
         player5 = rs.getString("player5");
         user5 = rs.getString("username5");
         show5 = rs.getShort("show5");
         course = rs.getString("courseName");

         //
         //  If the tee time is not empty, then process it
         //
         if (!player1.equals("") || !player2.equals("") || !player3.equals("") || !player4.equals("") || !player5.equals("")) {

            //
            //  init counters
            //
            mCount = 0;

            for (i = 0; i < parm.MAX_Guests; i++) {

               gCounts[i] = 0;
            }

            //
            //  Process each player
            //
            if (!player1.equals("") && show1 == 1) {        // if player exists and played

               if (!user1.equals("")) {       // if member

                  mCount++;                   // bump # of members in tee time

               } else {

                  i = 0;
                  ploop1:
                  while (i < parm.MAX_Guests) {

                     if (player1.startsWith( parm.guest[i] )) {

                        gCounts[i]++;          // count # of this guest type
                        break ploop1;
                     }
                     i++;
                  }
               }
            }

            if (!player2.equals("") && show2 == 1) {        // if player exists

               if (!user2.equals("")) {       // if member

                  mCount++;                   // bump # of members in tee time

               } else {

                  i = 0;
                  ploop2:
                  while (i < parm.MAX_Guests) {

                     if (player2.startsWith( parm.guest[i] )) {

                        gCounts[i]++;          // count # of this guest type
                        break ploop2;
                     }
                     i++;
                  }
               }
            }

            if (!player3.equals("") && show3 == 1) {        // if player exists

               if (!user3.equals("")) {       // if member

                  mCount++;                   // bump # of members in tee time

               } else {

                  i = 0;
                  ploop3:
                  while (i < parm.MAX_Guests) {

                     if (player3.startsWith( parm.guest[i] )) {

                        gCounts[i]++;          // count # of this guest type
                        break ploop3;
                     }
                     i++;
                  }
               }
            }

            if (!player4.equals("") && show4 == 1) {        // if player exists

               if (!user4.equals("")) {       // if member

                  mCount++;                   // bump # of members in tee time

               } else {

                  i = 0;
                  ploop4:
                  while (i < parm.MAX_Guests) {

                     if (player4.startsWith( parm.guest[i] )) {

                        gCounts[i]++;          // count # of this guest type
                        break ploop4;
                     }
                     i++;
                  }
               }
            }

            if (!player5.equals("") && show5 == 1) {        // if player exists

               if (!user5.equals("")) {       // if member

                  mCount++;                   // bump # of members in tee time

               } else {

                  i = 0;
                  ploop5:
                  while (i < parm.MAX_Guests) {

                     if (player5.startsWith( parm.guest[i] )) {

                        gCounts[i]++;          // count # of this guest type
                        break ploop5;
                     }
                     i++;
                  }
               }
            }

            //
            //  Now build the record(s):
            //            date, time, course, # of players, player type
            //
            fname = club + "_" + mm + "-" + dd + "-" + yy;     // i.e.  cordillera_8-17-2006.csv (.csv added below)

            if (dd < 10) {

               sdate = mm + "/0" + dd + "/" + yy;         // mm/dd/yyyy

            } else {

               sdate = mm + "/" + dd + "/" + yy;         // mm/dd/yyyy
            }

            ampm = " AM";
            if (hr > 12) {

               ampm = " PM";
               hr = hr - 12;       // convert from military time
            }
            if (hr == 12) {

               ampm = " PM";
            }
            if (hr == 0) {

               hr = 12;
               ampm = " AM";
            }

            if (min < 10) {

               stime = hr + ":0" + min + ampm;

            } else {

               stime = hr + ":" + min + ampm;
            }

            //
            //   If no course name, then set to "Catamount" so they know which club this record belongs to (Cordillera has course names)
            //
            if (course.equals( "" )) {

               course = "Catamount";
            }


            //
            //  Build the member record if any members
            //
            if (mCount > 0) {

               record = sdate + "," + stime + "," + course + "," + mCount + ",Member";

               addCordilleraRecord(record, fname);       // add the record to the file
            }

            //
            //  Build the guest records if any guests
            //
            i = 0;
            while (i < parm.MAX_Guests) {

               if (gCounts[i] > 0) {        // if any of this guest type

                  gtype = parm.guest[i];    // get the guest type

                  record = sdate + "," + stime + "," + course + "," + gCounts[i] + ",Guest: " + gtype;

                  addCordilleraRecord(record, fname);       // add the record to the file
               }
               i++;
            }

         }     // end of if empty test

      }   // end of while loop

      pstmt1.close();

   }
   catch (SQLException e) {

      String errorMsg = "Error in SystemUtils.buildCordilleraReport for club: " +club+ ". Exception= ";
      errorMsg = errorMsg + e.getMessage();                                // build error msg

      logError(errorMsg);                                       // log it
   }

 }  // end of buildCordilleraReport


 // *********************************************************
 //  addCordilleraRecord - build a report file and add a record
 // *********************************************************

   private static void addCordilleraRecord(String record, String fname) {


   int fail = 0;

   String dirname = "//home//reports//cordillera//";   // create directory name
   String filename = fname + ".csv";                   // create full file name
   String fileDest = dirname + filename;               // destination (temp file)


   try {
      //
      //  Dir path for the real server
      //
      PrintWriter fout1 = new PrintWriter(new FileWriter(fileDest, true));

      //
      //  Put record in text file
      //
      fout1.print(record);
      fout1.println();                            // output the line

      fout1.close();

   }
   catch (Exception e2) {

      fail = 1;
   }

   //
   //  if above failed, try local pc
   //
   if (fail != 0) {

      dirname = "c:\\java\\tomcat\\webapps\\" +rev+ "\\reports\\cordillera\\";     // create directory name
      fileDest = dirname + filename;

      try {
         //
         //  dir path for test pc
         //
         PrintWriter fout = new PrintWriter(new FileWriter(fileDest, true));

         //
         //  Put record in text file
         //
         fout.print(record);
         fout.println();                              // output the line

         fout.close();

      }
      catch (Exception ignore) {
      }
   }

 }  // end of addCordilleraRecord


 // *********************************************************
 //  moveStats - gather stats from teecurr before moving them and put in stats table
 // *********************************************************

   private static void moveStats(Connection con, String club) {


   //
   //  This method is no longer used - log error if it is called
   //
   logError("Error in SystemUtils.moveStats - method no longer used.  Caller should be updated.");

 }


 // *****************************************************************************
 //  moveStatsCom - gather stats from teecurr or teepast and put in stats table
 //
 //  called by:  moveStats (above)
 //              Proshop_oldsheets
 // *****************************************************************************

   public static void moveStatsCom(long date, String ind, String courseName, Connection con, String club) {


   //
   //  This method is no longer used - log error if it is called
   //
   logError("Error in SystemUtils.moveStatsCom - method no longer used.  Caller should be updated.");

 }


 //************************************************************************
 //  teeTimer - Calls scanTee and then resets timer.
 //
 //     called by: TeeTimer (when timer expires)
 //
 //************************************************************************

 public static void teeTimer()
                          throws Exception {


   Connection con = null;
   Connection con2 = null;
   Statement stmt = null;
   Statement stmt2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;
   PreparedStatement pstmt = null;

   boolean b = false;
   boolean doThis = false;
   
   int count1 = 0;         // count the # of clubs processed (scanTee)
   int count2 = 0;         
   int optCount1 = 0;      // count # of clubs optimized
   int optCount2 = 0;      
   int flexCount1 = 0;     // count # of FlxRez clubs processed
   int flexCount2 = 0;     

   //
   //  This must be the master server!!!  If not, let the timer run in case master goes down.
   //
   if (Common_Server.SERVER_ID == TIMER_SERVER || Common_Server.SERVER_ID == TIMER_SERVER2) {   // use 2 servers for this method (takes too long for one)

      long startTime = System.currentTimeMillis();
      logError("Starting SystemUtils.teeTimer() on server " +Common_Server.SERVER_ID);

      //
      //  Perform timer function for each club in the system database 'clubs' table
      //
      String club = rev;                       // get db name to use for 'clubs' table

      try {

         con = dbConn.Connect(club);                       // get a connection
      }
      catch (Exception e1) {

         logError("Error1 in SystemUtils teeTimer: " + e1.getMessage());                                       // log it
      }

      //
      //  Get the current day
      //
      Calendar cal = new GregorianCalendar();        // get todays date
      int day_name = cal.get(Calendar.DAY_OF_WEEK);

      //
      //  Spread out the database optimizations as they take a considerable amount of time
      //
      //boolean doOptimize = (day_name == 4);     // optimize the database tables every Wednesday
      boolean doOptimize = false;

      //
      //  Get the current date from 1 year ago
      //
      int year = cal.get(Calendar.YEAR) - 1;
      int month = cal.get(Calendar.MONTH) + 1;
      int daynum = cal.get(Calendar.DAY_OF_MONTH);
      long odate = (year * 10000) + (month * 100) + daynum;

      if (con != null) {

         //
         // Get the club names from the 'clubs' table
         //
         //  Process each club in the table
         //
         try {

            stmt = con.createStatement();              // create a statement

            rs = stmt.executeQuery("SELECT clubname FROM clubs WHERE inactive = 0 ORDER BY clubname");

            while (rs.next()) {

               club = rs.getString(1);                 // get a club name

               doThis = false;
               doOptimize = false;

               //
               //  Split the load over 2 servers and optimize on different days so we can get done in time for the db backups
               //
               //  NOTE: At this time we have a separate timer server (Node 1) and timer_server2 is Node 7 (a web server).  For some reason, Node 7
               //        seems to crunch through this much faster so we are assigning more clubs to that server.
               //
               if (Common_Server.SERVER_ID == TIMER_SERVER &&
                   (club.startsWith("a") || club.startsWith("b") || club.startsWith("c") || club.startsWith("d") || club.startsWith("e") ||
                    club.startsWith("f") || club.startsWith("g") || club.startsWith("h") || club.startsWith("i"))) {

                  doThis = true;

                  if (day_name == 5 && (club.startsWith("a") || club.startsWith("b") || club.startsWith("c") ||
                                        club.startsWith("d") || club.startsWith("e"))) {

                     doOptimize = true;      // optimize these dbs on Thursday

                  } else if (day_name == 4 && (club.startsWith("f") || club.startsWith("g") || club.startsWith("h") || club.startsWith("i"))) {

                     doOptimize = true;      // optimize these dbs on Wednesday
                  }

               } else if (Common_Server.SERVER_ID == TIMER_SERVER2 &&
                         (club.startsWith("j") || club.startsWith("k") || club.startsWith("l") || club.startsWith("m") || club.startsWith("n") ||
                          club.startsWith("o") || club.startsWith("p") || club.startsWith("q") || club.startsWith("r") || club.startsWith("s") ||
                          club.startsWith("t") || club.startsWith("u") || club.startsWith("v") || club.startsWith("w") || club.startsWith("x") ||
                          club.startsWith("y") || club.startsWith("z"))) {

                  doThis = true;

                  if (day_name == 2 && (club.startsWith("j") || club.startsWith("k") || club.startsWith("l") ||
                                        club.startsWith("m") || club.startsWith("n") || club.startsWith("o") ||
                                        club.startsWith("p") || club.startsWith("q") || club.startsWith("r"))) {

                     doOptimize = true;      // optimize these dbs on Monday

                  } else if (day_name == 3 && ( club.startsWith("s") || club.startsWith("t") || club.startsWith("u") || club.startsWith("v") ||
                                               club.startsWith("w") || club.startsWith("x") || club.startsWith("y") || club.startsWith("z"))) {

                     doOptimize = true;      // optimize these dbs on Tuesday
                  }
               }

               if (doThis == true) {      // if we should do this club on this server

                  try {

                      con2 = dbConn.Connect(club);            // get a connection to this club's db

                      if (con2 != null) {

                         b = scanTee(con2, club);                // build new tee sheets for each club
                         
                         if (Common_Server.SERVER_ID == TIMER_SERVER) {   // if server 1

                            count1++;                                // count clubs processed

                         } else {

                            count2++;                                // count clubs processed
                         }

                         stmt2 = con2.createStatement();
                         rs2 = stmt2.executeQuery("SELECT COUNT(activity_id) FROM activities WHERE parent_id = 0 AND enabled = 1");

                         if (rs2.next() && rs2.getInt(1) > 0) {

                            scanSheets(con2, club);          // do the activity sheets if any exist

                            if (Common_Server.SERVER_ID == TIMER_SERVER) {   // if server 1

                                flexCount1++;                                // count clubs processed

                            } else {

                                flexCount2++;                                // count clubs processed
                            }
                         }

                         if (doOptimize) {

                            optimize(con2);              // Optimize this club's db tables (every Wed)
                            
                            if (Common_Server.SERVER_ID == TIMER_SERVER) {   // if server 1

                                optCount1++;                                // count clubs processed

                            } else {

                                optCount2++;                                // count clubs processed
                            }
                         }

                         // purge old teehist entries
                         pstmt = con2.prepareStatement ("DELETE FROM teehist WHERE mdate < ?");
                         pstmt.clearParameters();
                         pstmt.setLong(1, odate);

                         pstmt.executeUpdate();          // execute the prepared stmt

                      } else {

                         logError("Error3 in SystemUtils teeTimer: Connection failed to " +club);

                      }

                  } catch (Exception e1) {

                      Utilities.logError("Error in SystemUtils teeTimer: club=" + club + " Exception:" + e1.getMessage(), con);

                  } finally {

                      try { rs2.close(); }
                      catch (Exception ignore) {}

                      try { stmt2.close(); }
                      catch (Exception ignore) {}

                      try { pstmt.close(); }
                      catch (Exception ignore) {}

                      try { con2.close(); }
                      catch (Exception ignore) {}
                  }
               }

            } // end while all clubs loop - do next club

            //
            //   Get today's date
            //
            //Calendar cal = new GregorianCalendar(); // reuse cal from above
            //long yy = cal.get(Calendar.YEAR);
            //long mm = cal.get(Calendar.MONTH) + 1;
            //long dd = cal.get(Calendar.DAY_OF_MONTH);

            // save it in public variable defined above
            scanDate = (cal.get(Calendar.YEAR) * 10000) + ((cal.get(Calendar.MONTH) + 1) * 100) + cal.get(Calendar.DAY_OF_MONTH);

         } catch (Exception e2) {

             logError("Error2 in SystemUtils teeTimer: " + e2.getMessage());                                       // log it

         } finally {

             try { rs.close(); }
             catch (Exception ignore) {}

             try { stmt.close(); }
             catch (Exception ignore) {}

             try { con.close(); }
             catch (Exception ignore) {}

         }

      } // end if con != null

      if (Common_Server.SERVER_ID == TIMER_SERVER) {   // if server 1

         logError("Finished SystemUtils.teeTimer() on server " +Common_Server.SERVER_ID+ ". Run time:" + (System.currentTimeMillis() - startTime) + 
                  "ms (" + ((System.currentTimeMillis() - startTime) / 1000 / 60) + "min). FT Clubs = " +count1+ ", FlxRez Clubs = " +flexCount1+ ", Clubs Optimized = " +optCount1);

      } else {

         logError("Finished SystemUtils.teeTimer() on server " +Common_Server.SERVER_ID+ ". Run time:" + (System.currentTimeMillis() - startTime) + 
                  "ms (" + ((System.currentTimeMillis() - startTime) / 1000 / 60) + "min). FT Clubs = " +count2+ ", FlxRez Clubs = " +flexCount2+ ", Clubs Optimized = " +optCount2);
      }
      
   } // end if master server

   TeeTimer t_timer = new TeeTimer();            // reset timer to keep building tee sheets daily

 }


 //
 // This method will find any configured root activites for a club and call
 // another method to build time sheets for each one found
 //
 //   NOTE:  this can be called from Support also
 //
 public static void scanSheets(Connection con, String club) {


    Statement stmt = null;
    ResultSet rs = null;

    try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT activity_id FROM activities WHERE parent_id = 0 AND enabled = 1");

        while ( rs.next() ) {

            buildTimeSheets(rs.getInt("activity_id"), club, con);

        }

    } catch (Exception exc) {

        logError("Error in SystemUtils.scanSheets: club=" + club + ", err=" + exc.getMessage());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { stmt.close(); }
        catch (Exception ignore) {}

    }

 }


 //
 // This method will accept a root activity passed in and use it to find all of its children
 // then we check the activity_sheets table to see if we need to build any time sheets for
 // each child activity.  If none were found then we call another method to build them for that day.
 // Typically this runs off a timer on the timer server so we just check 366 days out, but if this
 // is run on the dev server then we will check every day for the next year to see if sheets exist.
 //
 private static void buildTimeSheets(int activity_id, String club, Connection con) {


    PreparedStatement pstmt = null;
    ResultSet rs = null;

    int date = 0;
    int year = 0;
    int month = 0;
    int day = 0;
    int skip = 0;
    int i = 0;

    //String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

    String in = getActivity.buildInString(activity_id, 1, con); // get all the children of this activity id

    ArrayList<Integer> array = new ArrayList<Integer>();

    Calendar cal = null;

    try { array = getActivity.getAllActivitiesWithSheets(in, con); }
    catch (Exception exc) {}

    for (int x = 0; x < array.size(); x++) {

        cal = new GregorianCalendar();          // get todays date
        cal.add(Calendar.DATE, 365);            // roll ahead one year

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        day = cal.get(Calendar.DAY_OF_MONTH);

        date = (year * 10000) + (month * 100) + day;

        //
        //  check all 365 days
        //
        i = 0;
        loopb1:
        while (i < 366) {        // go 1 extra for today

            skip = 0;

            //
            //  See if tee sheet already exists for this activity and day
            //
            try {

                pstmt = con.prepareStatement (
                        "SELECT sheet_id FROM activity_sheets WHERE activity_id = ? AND DATE_FORMAT(date_time, '%Y%m%d') = ?");

                pstmt.clearParameters();
                pstmt.setInt(1, array.get(x));       // activity_id
                pstmt.setInt(2, date);
                rs = pstmt.executeQuery();

                if ( rs.next() ) {                   // if times exist

                    skip = 1;                        // found one - we're done checking
                }

            } catch (Exception exc) {

                Utilities.logError("buildTimeSheets: Error=" + exc.getMessage());

            } finally {

                i++; // bump the counter here so errors will not cause infinte loops

                try { rs.close(); }
                catch (Exception ignore) {}

                try { pstmt.close(); }
                catch (Exception ignore) {}

            }

            if (skip == 0) {

                // we didn't find any - let's build this day's time sheet
                buildTimeSheet(array.get(x), date, club, con);

            } else {

                // if running on dev server then don't break - scan full year
                if (Common_Server.SERVER_ID != 4) break loopb1;

            }

            cal.add(Calendar.DATE, -1);                 // roll back one day
            day = cal.get(Calendar.DAY_OF_MONTH);
            month = cal.get(Calendar.MONTH) +1;
            year = cal.get(Calendar.YEAR);

            date = (year * 10000) + (month * 100) + day;

        } // end year while loop

    } // end for loop of avtivities are are building time sheets for

 }


 //
 // This method will build one time sheet for the specified activity
 // Make this method accept the local ints as parms (interval, start time, etc.)
 //
 private static void buildTimeSheet(int activity_id, int date, String club, Connection con) {


    PreparedStatement pstmt = null;
    ResultSet rs = null;

    int interval = 0;      // minutes between times
    int alt_interval = 0;
    int first_time = 0;
    int first_hr = 0;
    int last_time = 0;
    int this_time = 0;
    int hr = 0;
    int min = 0;

    boolean alt = false;
    boolean applyCustom = false;

    Calendar cal = null;

    // get the parameters we need to build the time sheets for this activity
    try {

        pstmt = con.prepareStatement("SELECT * FROM activities WHERE activity_id = ? AND enabled = 1");
        pstmt.clearParameters();
        pstmt.setInt(1, activity_id);
        rs = pstmt.executeQuery();

        if ( rs.next() ) {

            interval = rs.getInt("interval");
            alt_interval = rs.getInt("alt_interval");
            first_time = rs.getInt("first_time");
            last_time = rs.getInt("last_time");

        }

    } catch (Exception exc) {

        logError("Error in SystemUtils.buildTimeSheet: activity_id=" + activity_id + ", club=" + club + ", err=" + exc.getMessage());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }
/*      Removed at request of club
    // Custom sheets for weekends (fri-sun) at Quechee Club
    if (club.equals("quecheeclubtennis")) {

        int year = date / 10000;
        int month = (date - (year * 10000)) / 100;
        int day = (date - (year * 10000)) - (month * 100);

        Calendar cal2 = new GregorianCalendar();
        cal2.set(year, month - 1, day);

        int day_num = cal2.get(Calendar.DAY_OF_WEEK);

        // If day is Sunday, Friday, or Saturday, and is for court 4-11 (activity_ids 5-12) apply special first and last time values
        if ((day_num == 1 || day_num == 6 || day_num == 7) && activity_id >= 5 && activity_id <=12) {

            first_time = 830;
            last_time = 2030;
        }
    }
 */
    
    if (club.equals("willowridgecc")) {
        
        int year = date / 10000;
        int month = (date - (year * 10000)) / 100;
        int day = (date - (year * 10000)) - (month * 100);

        Calendar cal2 = new GregorianCalendar();
        cal2.set(year, month - 1, day);

        int day_num = cal2.get(Calendar.DAY_OF_WEEK);

        // If day is Saturday or Sunday between 4/22/2012 and 10/31/2012, apply special first and last time values
        if (date >= 20120422 && date <= 20121031 && (day_num == 1 || day_num == 7)) {

            applyCustom = true;
            last_time = 1730;
        }
    }


    // only proceed if properly configured
    if (last_time > first_time && interval != 0) {


        this_time = first_time;
        hr = this_time / 100;
        min = this_time - (hr * 100);
        first_hr = first_time / 100;        // get first hour to prevent wrapping below

        // an alt_interval of zero means they're not using alternating times
        // so we'll just set it to the same value as interval
        if (alt_interval == 0) alt_interval = interval;


        // START CUSTOMS


        //
        // We can make any changes to the parameters based upon things like day of week
        // here before we get to the loop that builds the actual time slots for this sheet
        //

        /*
        if (club.equals("demopaul") && activity_id == 2) {

            PreparedStatement pstmt = null;
            ResultSet rs = null;

            try {

                pstmt = con.prepareStatement (
                        "SELECT DATE_FORMAT(?, '%W') AS dayName");

                pstmt.clearParameters();
                pstmt.setInt(1, date);
                rs = pstmt.executeQuery();

                is_wednesday = ( rs.next() && rs.getString(1).equals("Wednesday") );

            } catch (Exception exc) {

                Utilities.logError("buildTimeSheets: is_wednesday error=" + exc.getMessage());

            } finally {

                try { rs.close(); }
                catch (Exception ignore) {}

                try { pstmt.close(); }
                catch (Exception ignore) {}

            }

            last_time = (is_wednesday) ? 1900 : 1730;

        } else {

            last_time = 1730;

        }
        */


        // END CUSTOMS



        //
        // Loop thru each time we need to build a time slot for
        //
        loop1:
        while (this_time <= last_time) {
            
            if (club.equals("willowridgecc") && applyCustom && this_time == 1300) {
                hr = 13;
                min = 30;
                this_time = 1330;
            }

            buildTimeSlot(date, this_time, activity_id, con);

            cal = new GregorianCalendar();
            cal.set(Calendar.HOUR_OF_DAY, hr);
            cal.set(Calendar.MINUTE, min);

            cal.add(Calendar.MINUTE, (!alt) ? interval : alt_interval);

            hr = cal.get(Calendar.HOUR_OF_DAY);
            min = cal.get(Calendar.MINUTE);

            if (hr < first_hr) break loop1;     // exit loop if we wrapped to the next day

            this_time = hr * 100 + min;

            alt = alt == false;

        }

    } // end if properly configured

 }


 private static void buildTimeSlot(int date, int time, int activity_id, Connection con) {

    PreparedStatement pstmt = null;

    try {

        pstmt = con.prepareStatement( "" +
                "INSERT INTO activity_sheets " +
                    "(activity_id, date_time)" +
                "VALUES " +
                    "(?, ?)" );

        pstmt.clearParameters();
        pstmt.setInt(1, activity_id);
        pstmt.setString(2, Utilities.get_mysql_timestamp(date, time));

        pstmt.executeUpdate();

    } catch (Exception exc) {

        Utilities.logError("SystemUtils.buildTimeSlot: activity_id=" + activity_id + ", date=" + date + ", time=" + time + ", Error=" + exc.getMessage());

    } finally {

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

 }


 //************************************************************************
 //  scanTee - Scans teecurr2 to make sure there are 365 days of tee sheets.
 //            This is done in case the server goes down and our timer
 //            does not expire.
 //
 //    called by:
 //                Proshop_buildTees
 //                teeTimer (above when timer expires)
 //
 //    calls:   buildTee (above)
 //             moveTee
 //             updateTeecurr
 //
 //************************************************************************

 public static boolean scanTee(Connection con, String club)
                          throws Exception {


   Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   String course = "";
   String hdcp_system = "";
   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

   long date = 0;

   int year = 0;
   int month = 0;
   int day = 0;
   int day_name = 0;
   int time = 0;
   int hr = 0;
   int min = 0;
   int i = 0;
   int sheet_id = 0;
   int this_week = 0;
   int eo_week = 0;

   short xx = 0;
   short skip = 0;

   boolean doneSome = false;
   boolean doCustom = false;

   Calendar cal = null;


   //
   //  Get the 'build tees' parm from clubparm table - check if we should wait to build tee times
   //
   try {
      stmt = con.createStatement();        // create a statement

      rs2 = stmt.executeQuery(
              "SELECT cp.courseName, cp.xx, c.hdcpSystem " +
              "FROM clubparm2 cp, club5 c " +
              "WHERE cp.first_hr != 0");

      while (rs2.next()) {

         course = rs2.getString(1);
         xx = rs2.getShort(2);
         hdcp_system = rs2.getString(3);

         if (xx != 0) {                                    // if course ready for tee sheets

            //
            //  start at the end of period and work backwards (faster)!!!!!!!!!!!
            //
            cal = new GregorianCalendar();        // get todays date
            cal.add(Calendar.DATE,365);                    // roll ahead one year !!!!!!

            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH) +1;
            day = cal.get(Calendar.DAY_OF_MONTH);
            day_name = cal.get(Calendar.DAY_OF_WEEK);
            hr = cal.get(Calendar.HOUR_OF_DAY);
            min = cal.get(Calendar.MINUTE);

            /*        // don't do this - we now process this at 1:05 AM CT and changing the date prevents West Coast clubs from updating.
            //
            //  Build the 'time' string for display
            //
            //    Adjust the time based on the club's time zone (we are Central)
            //
            time = (hr * 100) + min;

            time = adjustTime(con, time);       // adjust for time zone

            if (time < 0) {                // if negative, then we went back or ahead one day

               time = 0 - time;          // convert back to positive value

               if (time < 100) {           // if hour is zero, then we rolled ahead 1 day
                  //
                  // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
                  //
                  cal.add(Calendar.DATE,1);                     // get next day's date

                  year = cal.get(Calendar.YEAR);
                  month = cal.get(Calendar.MONTH) +1;
                  day = cal.get(Calendar.DAY_OF_MONTH);
                  day_name = cal.get(Calendar.DAY_OF_WEEK);

               } else {                        // we rolled back 1 day

                  //
                  // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
                  //
                  cal.add(Calendar.DATE,-1);                     // get yesterday's date

                  year = cal.get(Calendar.YEAR);
                  month = cal.get(Calendar.MONTH) +1;
                  day = cal.get(Calendar.DAY_OF_MONTH);
                  day_name = cal.get(Calendar.DAY_OF_WEEK);
               }
            }
            */

            //
            //  Create numeric date for movetee (today's date)
            //
            date = (year * 10000) + (month * 100) + day;     // date = yyyymmdd (for comparisons)

            //
            //  check all 365 days
            //
            i = 0;
            loopb1:
            while (i < 366) {        // go 1 extra for today

               skip = 0;
               sheet_id = 0;
               this_week = 0;
               eo_week = 0;
               doCustom = false;

               //
               //  See if tee sheet already exists for this course and day
               //
               PreparedStatement pstmt = con.prepareStatement (
                  "SELECT fb FROM teecurr2 WHERE date = ? AND courseName = ?");

               pstmt.clearParameters();        // clear the parms
               pstmt.setLong(1, date);         // put the parm in pstmt
               pstmt.setString(2, course);
               rs = pstmt.executeQuery();      // execute the prepared stmt

               if (rs.next()) {                // if tee times exist

                  skip = 1;                    // found one - we're done checking this course
               }

               pstmt.close();
               i++;


               if (skip == 0) {             // check all days !!!!! 6/14/2006

                  //
                  //  If Hazeltine, build custom tee sheets
                  //
                  /*
                  if (club.equals( "hazeltine" )) {

                     buildHTee(year, month, day, day_name, course, con);   // go build tee sheet for this day

                  } else {
                  * 
                  */

                     // look for a custom tee sheet for this day
                     pstmt = con.prepareStatement ("" +
                        "SELECT custom_sheet_id, eo_week, " +
                          "IF(MOD(DATE_FORMAT(start_date, '%U'), 2) = MOD(DATE_FORMAT(?, '%U'), 2), 1, 0) AS thisweek " +
                        "FROM custom_sheets " +
                        "WHERE course = ? AND " + day_table[day_name] + " = 1 AND start_date <= ? AND end_date >= ?");


                     /*

                     // old query
                     "SELECT custom_sheet_id " +
                     "FROM custom_sheets " +
                     "WHERE course = ? AND " + day_table[day_name] + " = 1 AND " +
                     "start_date <= ? AND end_date >= ?"

                     // new query
                     SELECT custom_sheet_id,
                             DATE_FORMAT(start_date, "%U") AS start_week, DATE_FORMAT("20080701", "%U") AS curr_week,
                             IF(MOD(DATE_FORMAT(start_date, "%U"), 2) = MOD(DATE_FORMAT("20080701", "%U"), 2), 'yes', 'no') AS thisweek
                     FROM custom_sheets
                     WHERE course = "Members Course" AND monday = 1 AND start_date <= 20080701 AND end_date >= 20080701

                     */

                     pstmt.clearParameters();
                     pstmt.setLong(1, date);
                     pstmt.setString(2, course);
                     pstmt.setLong(3, date);
                     pstmt.setLong(4, date);
                     rs = pstmt.executeQuery();

                     if (rs.next()) {

                         sheet_id = rs.getInt(1);
                         eo_week = rs.getInt(2);
                         this_week = rs.getInt(3);
                     }

                     pstmt.close();

                     // see if we found a sheet for this day
                     if (sheet_id > 0 && (eo_week == 0 || (eo_week == this_week))) {

                         // build this days sheet using the custom tee sheet we found
                         buildCustomTees(sheet_id, date, con);

                     } else if (!club.equals("tpcsugarloaf")) {

                         // no custom sheet, build using default configuration
                         buildTee(year, month, day, day_name, course, club, con);  // go build tee sheet for this day

                     }
                //  }       // end of Hazeltine check

                  doneSome = true;

               } else {             // tee times found

                  // if running on dev server then don't break - scan full year
                  if (Common_Server.SERVER_ID != 4) break loopb1;     // quit looking for this course
               }

               cal.add(Calendar.DATE,-1);                 // roll back one day
               day = cal.get(Calendar.DAY_OF_MONTH);      // get new day
               month = cal.get(Calendar.MONTH) +1;
               year = cal.get(Calendar.YEAR);             // get year
               day_name = cal.get(Calendar.DAY_OF_WEEK);  // get name of new day (01 - 07)

               date = (year * 10000) + (month * 100) + day;     // date = yyyymmdd (for comparisons)

            }   // end of while loop (check all 365 days until some found)

         }      // end of if ready

      }         // end of while more courses

      stmt.close();

   } catch (Exception e2) {

      logError("Error1 in SystemUtils scanTee for club: " +club+ ". Exception= " + e2.getMessage());

      throw new Exception("Error reading Club Parameters - scanTee Exception: " + e2.getMessage());

   }

   //
   //  Get today's date and time
   //
   cal = new GregorianCalendar();
   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH);
   day = cal.get(Calendar.DAY_OF_MONTH);
   day_name = cal.get(Calendar.DAY_OF_WEEK);
   hr = cal.get(Calendar.HOUR_OF_DAY);
   min = cal.get(Calendar.MINUTE);

   month += 1;                                      // month starts at zero

   date = (year * 10000) + (month * 100) + day;     // date = yyyymmdd (for comparisons)


   //
   //  Now move any old tee sheets from teecurr to teepast
   //
   try {

      //
      //   Move old tee sheets if more than 365 on teecurr2
      //
      moveTee(con, date, club);

   }
   catch (Exception e3) {

      logError("Error2 in SystemUtils scanTee for " +club+ ". Exception= " + e3.getMessage());

      throw new Exception("scanTee - Error from moveTee -  " + e3.toString());
   }

   if (doneSome == true) {             // if we built any new tee times

      try {

         //
         //  call updateTeecurr to add any restrictions, etc. to the new tee times.
         //
         updateTeecurr(con);

         //
         //   Remove any old blockers from the lesson books for this club
         //
         removeLessonBlockers(con, date, club);

         //
         //   Remove any old inactive events and event signups for this club
         //
         removeOldEvents(con, date, club);

      }
      catch (Exception e3) {

         String errorMsg3 = "Error3 in SystemUtils scanTee for " +club+ ". Exception= ";
         errorMsg3 = errorMsg3 + e3.getMessage();                                // build error msg

         logError(errorMsg3);                                       // log it

         throw new Exception("scanTee - Error from updateTeecurr -  " + e3.getMessage());
      }

      try {

         //
         //  if The CC, then build custom sheet for today
         //
         if (club.equals( "tcclub" )) {

            course = "Championship Course";

            buildTee(year, month, day, day_name, course, club, con);  // go build tee sheet for this day

            course = "Main Course";

            buildTee(year, month, day, day_name, course, club, con);  // go build tee sheet for this day
         }


         //
         //  if Oak Hill CC, then build custom sheet for today
         //
         if (club.equals( "oakhillcc" )) {

            course = "East Course";

            buildTee(year, month, day, day_name, course, club, con);  // go build tee sheet for this day

            course = "West Course";

            buildTee(year, month, day, day_name, course, club, con);  // go build tee sheet for this day
         }

      }
      catch (Exception e4) {

         String errorMsg4 = "Error4 in SystemUtils scanTee for " +club+ ". Exception= ";
         errorMsg4 = errorMsg4 + e4.getMessage();                                // build error msg

         logError(errorMsg4);                                       // log it

         throw new Exception("scanTee - Error from updateTeecurr (tcclub or oakhillcc) -  " + e4.getMessage());
      }
   }


   //
   //  Now cycle through teecurr and make sure any tflags are updated.  These flags are used on the pro tee sheet
   //  for display purposes only to indicate certain mship classes or other indicators for selected members.
   //
   rebuildTFlags(club, con);



   //
   //  Delete any old session log entries
   //
   try {

      cal = new GregorianCalendar();                 // get todays date
      cal.add(Calendar.DATE,-7);                     // get last week's date

      year = cal.get(Calendar.YEAR);
      month = cal.get(Calendar.MONTH) +1;
      day = cal.get(Calendar.DAY_OF_MONTH);

      date = (year * 10000) + (month * 100) + day;     // date = yyyymmdd (for comparisons)

      PreparedStatement pstmt3 = con.prepareStatement (
          "DELETE FROM sessionlog " +
          "WHERE date < ?");

      //
      //  execute the prepared statement to update the tee time slot
      //
      pstmt3.clearParameters();        // clear the parms
      pstmt3.setLong(1, date);
      pstmt3.executeUpdate();

      pstmt3.close();

   }
   catch (Exception ignore) {
   }

   //
   // CUSTOM for tahoedonner - dump their tee sheet each night
   //
   if (club.equals("tahoedonner")) dumpTeeSheet(club, con);


   //
   // If CDGA club then call the dump sheet method
   //
   if (hdcp_system.equalsIgnoreCase("CDGA")) dumpCDGA_sheets(club, con, null);

   
   return(doneSome);

 }  // end of scanTee


 //************************************************************************
 //  buildDblTee - Builds double tees into the tee sheets.
 //
 //    called by:  Proshop_adddbltee
 //                Proshop_editdbltee
 //
 //
 //    returns status:  0 = no tee times exist
 //                     1 = double tees built
 //                     2 = double tees built, some had problems
 //                     9 = double tee not found - nothing done
 //
 //************************************************************************

 public static int buildDblTee(String name, Connection con)
                          throws Exception {


   Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   String course = "";
   String courseName = "";
   String recurr = "";
   String day = "";
   String day_name = "";
   String statement = "";
   String statement2 = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";

   long date = 0;
   long sdate = 0;
   long edate = 0;

   int status = 0;
   int time = 0;
   int stime1 = 0;
   int etime1 = 0;
   int stime2 = 0;
   int etime2 = 0;

   short fb = 0;
   short ok = 0;
   short skip = 0;


   //
   //  Get the 'double tee' parms for the name passed
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
               "SELECT * FROM dbltee2 WHERE name = ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setString(1, name);       // put the parm in pstmt1
      rs = pstmt1.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         sdate = rs.getLong("sdate");
         stime1 = rs.getInt("stime1");
         edate = rs.getLong("edate");
         etime1 = rs.getInt("etime1");
         stime2 = rs.getInt("stime2");
         etime2 = rs.getInt("etime2");
         recurr = rs.getString("recurr");
         course = rs.getString("courseName");

      } else {

         ok = 1;        // not ok to proceed
         status = 9;    // error - dbl tee not found
      }

      pstmt1.close();              // close the stmt

      if (ok == 0) {             // if ok to proceed

         //
         //  Use this info to scan the tee times and build the double tees whenever possible
         //
         if ( recurr.equalsIgnoreCase( "every sunday" ) ) {   // if every Sunday

            day = "Sunday";
         }
         if ( recurr.equalsIgnoreCase( "every monday" ) ) {   // if every Monday

            day = "Monday";
         }
         if ( recurr.equalsIgnoreCase( "every tuesday" ) ) {   // if every Tuesday

            day = "Tuesday";
         }
         if ( recurr.equalsIgnoreCase( "every wednesday" ) ) {   // if every Wednesday

            day = "Wednesday";
         }
         if ( recurr.equalsIgnoreCase( "every thursday" ) ) {   // if every Thursday

            day = "Thursday";
         }
         if ( recurr.equalsIgnoreCase( "every friday" ) ) {   // if every Friday

            day = "Friday";
         }
         if ( recurr.equalsIgnoreCase( "every saturday" ) ) {   // if every Satruday

            day = "Saturday";
         }

         //
         //   Statements to use based on the double tee config - for adding 'Back 9' tee time
         //
         String ps1 = "SELECT * FROM teecurr2 " +
                      "WHERE date >= " +sdate+ " AND date <= " +edate+ " AND day = '" +day+ "' AND " +
                      "time >= " +stime1+ " AND time <= " +etime1+ " AND " +
                      "courseName = '" +course+ "'";                                           // day & course

         String ps2 = "SELECT * FROM teecurr2 " +
                      "WHERE date >= " +sdate+ " AND date <= " +edate+ " AND day = '" +day+ "' AND " +
                      "time >= " +stime1+ " AND time <= " +etime1;                             // day & any course

         String ps3 = "SELECT * FROM teecurr2 " +
                      "WHERE date >= " +sdate+ " AND date <= " +edate+ " AND time >= " +stime1+ " AND " +
                      "time <= " +etime1+ " AND courseName = '" +course+ "'";                 // every day & course

         String ps4 = "SELECT * FROM teecurr2 " +
                      "WHERE date >= " +sdate+ " AND date <= " +edate+ " AND " +
                      "time >= " +stime1+ " AND time <= " +etime1;                            // every day & any course

         String ps5 = "SELECT * FROM teecurr2 " +
                      "WHERE date >= " +sdate+ " AND date <= " +edate+ " AND " +
                      "day != 'Saturday' AND day != 'Sunday' AND time >= " +stime1+ " AND " +
                      "time <= " +etime1+ " AND courseName = '" +course+ "'";                   // weekdays & course

         String ps6 = "SELECT * FROM teecurr2 " +
                      "WHERE date >= " +sdate+ " AND date <= " +edate+ " AND " +
                      "day != 'Saturday' AND day != 'Sunday' AND " +
                      "time >= " +stime1+ " AND time <= " +etime1;                           // weekdays & any course

         String ps7 = "SELECT * FROM teecurr2 " +
                      "WHERE date >= " +sdate+ " AND date <= " +edate+ " AND " +
                      "day != 'Monday' AND day != 'Tuesday' AND day != 'Wednesday' AND " +
                      "day != 'Thursday' AND day != 'Friday' AND time >= " +stime1+ " AND " +
                      "time <= " +etime1+ " AND courseName = '" +course+ "'";                   // weekends & course

         String ps8 = "SELECT * FROM teecurr2 " +
                      "WHERE date >= " +sdate+ " AND date <= " +edate+ " AND " +
                      "day != 'Monday' AND day != 'Tuesday' AND day != 'Wednesday' AND " +
                      "day != 'Thursday' AND day != 'Friday' AND " +
                      "time >= " +stime1+ " AND time <= " +etime1;                           // weekends & any course

         //
         //   Statements to use based on the double tee config - for adding 'Crossover' tee time
         //
         String cs1 = "SELECT * FROM teecurr2 " +
                      "WHERE date >= " +sdate+ " AND date <= " +edate+ " AND day = '" +day+ "' AND " +
                      "time >= " +stime2+ " AND time <= " +etime2+ " AND " +
                      "courseName = '" +course+ "'";                                           // day & course

         String cs2 = "SELECT * FROM teecurr2 " +
                      "WHERE date >= " +sdate+ " AND date <= " +edate+ " AND day = '" +day+ "' AND " +
                      "time >= " +stime2+ " AND time <= " +etime2;                             // day & any course

         String cs3 = "SELECT * FROM teecurr2 " +
                      "WHERE date >= " +sdate+ " AND date <= " +edate+ " AND time >= " +stime2+ " AND " +
                      "time <= " +etime2+ " AND courseName = '" +course+ "'";                 // every day & course

         String cs4 = "SELECT * FROM teecurr2 " +
                      "WHERE date >= " +sdate+ " AND date <= " +edate+ " AND " +
                      "time >= " +stime2+ " AND time <= " +etime2;                            // every day & any course

         String cs5 = "SELECT * FROM teecurr2 " +
                      "WHERE date >= " +sdate+ " AND date <= " +edate+ " AND " +
                      "day != 'Saturday' AND day != 'Sunday' AND time >= " +stime2+ " AND " +
                      "time <= " +etime2+ " AND courseName = '" +course+ "'";                   // weekdays & course

         String cs6 = "SELECT * FROM teecurr2 " +
                      "WHERE date >= " +sdate+ " AND date <= " +edate+ " AND " +
                      "day != 'Saturday' AND day != 'Sunday' AND " +
                      "time >= " +stime2+ " AND time <= " +etime2;                           // weekdays & any course

         String cs7 = "SELECT * FROM teecurr2 " +
                      "WHERE date >= " +sdate+ " AND date <= " +edate+ " AND " +
                      "day != 'Monday' AND day != 'Tuesday' AND day != 'Wednesday' AND " +
                      "day != 'Thursday' AND day != 'Friday' AND time >= " +stime2+ " AND " +
                      "time <= " +etime2+ " AND courseName = '" +course+ "'";                   // weekends & course

         String cs8 = "SELECT * FROM teecurr2 " +
                      "WHERE date >= " +sdate+ " AND date <= " +edate+ " AND " +
                      "day != 'Monday' AND day != 'Tuesday' AND day != 'Wednesday' AND " +
                      "day != 'Thursday' AND day != 'Friday' AND " +
                      "time >= " +stime2+ " AND time <= " +etime2;                           // weekends & any course

         //
         //  Determine which statement to use and then build the 'Back 9' tee times
         //
         if (course.equals( "-ALL-" ) || course.equals( "" )) {    // if ALL Courses or none

            if (!day.equals( "" )) {    // if an individual day

               statement = ps2;                                               // days specified & any course
               statement2 = cs2;

            } else {

               if ( recurr.equalsIgnoreCase( "every day" )) {   // if everyday

                  statement = ps4;                                          // every day specified & any course
                  statement2 = cs4;

               } else {

                  if ( recurr.equalsIgnoreCase( "all weekdays" )) {   // if Monday - Friday

                     statement = ps6;                                       // all week days & any course
                     statement2 = cs6;

                  } else {

                     if ( recurr.equalsIgnoreCase( "all weekends" ) ) {   // if Saturday & Sunday

                        statement = ps8;                                       // weekends & any course
                        statement2 = cs8;

                     }
                  }
               }
            }

         } else {      // course name was specified

            if (!day.equals( "" )) {    // if an individual day

               statement = ps1;                                               // days specified & course
               statement2 = cs1;

            } else {

               if ( recurr.equalsIgnoreCase( "every day" )) {   // if everyday

                  statement = ps3;                                          // every day specified & course
                  statement2 = cs3;

               } else {

                  if ( recurr.equalsIgnoreCase( "all weekdays" )) {   // if Monday - Friday

                     statement = ps5;                                       // all week days & course
                     statement2 = cs5;

                  } else {

                     if ( recurr.equalsIgnoreCase( "all weekends" ) ) {   // if Saturday & Sunday

                        statement = ps7;                                       // weekends & course
                        statement2 = cs7;
                     }
                  }
               }
            }
         }        // end of statement tests

         //
         //  Now execute the selected statement and build the double tees
         //
         PreparedStatement pstmt5 = con.prepareStatement (statement);

         pstmt5.clearParameters();
         rs = pstmt5.executeQuery();      // execute the prepared stmt

         while (rs.next()) {                // if tee times exist

            date = rs.getInt("date");
            day_name = rs.getString("day");
            time = rs.getInt("time");
            fb = rs.getShort("fb");
            courseName = rs.getString("courseName");

            if (fb != 1) {       // if not already a 'Back 9' tee time (else skip)

               //
               //  Must be either a 'Front 9' or a 'Crossover'
               //  If Crossover, conver to 'Front 9'
               //
               if (fb == 9) {

                  PreparedStatement pstmt3 = con.prepareStatement (
                      "UPDATE teecurr2 SET fb = 0 " +
                              "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                  //
                  //  execute the prepared statement to update the tee time slot
                  //
                  pstmt3.clearParameters();        // clear the parms
                  pstmt3.setLong(1, date);
                  pstmt3.setInt(2, time);
                  pstmt3.setInt(3, fb);
                  pstmt3.setString(4, courseName);
                  pstmt3.executeUpdate();

                  pstmt3.close();
               }

               skip = 0;

               //
               //  Now check for a 'Back 9' tee time with matching date/time/course
               //
               PreparedStatement pstmt = con.prepareStatement (
                  "SELECT mm FROM teecurr2 WHERE date = ? AND time = ? AND fb = 1 AND courseName = ?");

               pstmt.clearParameters();        // clear the parms
               pstmt.setLong(1, date);         // put the parm in pstmt
               pstmt.setInt(2, time);
               pstmt.setString(3, courseName);
               rs2 = pstmt.executeQuery();      // execute the prepared stmt

               if (rs2.next()) {                // if tee time already exists

                  skip = 1;
                  status = 2;                    // tee times changed (may require attention from pro)
               }
               pstmt.close();

               //
               //  If none found, then we can add one
               //
               if (skip == 0) {

                  fb = 1;

                  insertTee(date, time, fb, courseName, day_name, con);     // insert new tee time (see directly below)

                  if (status == 0) {
                     status = 1;                    // built at least one
                  }
               }

            }  // end of IF NOT 'back 9' tee time

         }  // end of WHILE tee times

         pstmt5.close();

         //
         //  Now change the existing tee times to cross-overs
         //
         pstmt5 = con.prepareStatement (statement2);

         pstmt5.clearParameters();
         rs = pstmt5.executeQuery();      // execute the prepared stmt

         while (rs.next()) {                // if tee times exist

            date = rs.getInt("date");
            time = rs.getInt("time");
            player1 = rs.getString("player1");
            player2 = rs.getString("player2");
            player3 = rs.getString("player3");
            player4 = rs.getString("player4");
            player5 = rs.getString("player5");
            fb = rs.getShort("fb");
            courseName = rs.getString("courseName");

            if (fb != 9) {       // if not already a 'Crossover' tee time (else skip)

               if (!player1.equals( "" ) || !player2.equals( "" ) || !player3.equals( "" ) || !player4.equals( "" ) ||
                   !player5.equals( "" )) {

                  status = 2;       // inform user there may be a problem
               }

               //
               // change tee time to a crossover
               //
               PreparedStatement pstmt3 = con.prepareStatement (
                   "UPDATE teecurr2 SET fb = 9 " +
                           "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

               //
               //  execute the prepared statement to update the tee time slot
               //
               pstmt3.clearParameters();        // clear the parms
               pstmt3.setLong(1, date);
               pstmt3.setInt(2, time);
               pstmt3.setInt(3, fb);
               pstmt3.setString(4, courseName);
               pstmt3.executeUpdate();

               pstmt3.close();

            }

         }  // end of WHILE tee times

         pstmt5.close();

      }                    // end of IF ok

   }
   catch (Exception e2) {

      String errorMsg1 = "Error1 in SystemUtils buildDblTee: ";
      errorMsg1 = errorMsg1 + e2.getMessage();                                // build error msg

      logError(errorMsg1);                                       // log it

      throw new Exception("Error adding double tee - buildDblTee Exception: " + e2.getMessage());
   }

   return(status);

 }  // end of buildDblTee


 //************************************************************************
 //  removeDblTee - Removes double tees from the tee sheets.
 //
 //    called by:  Proshop_editdbltee
 //
 //
 //    returns status:  0 = no double tees exist
 //                     1 = double tees (back tees) found
 //                     2 = double tees (back tees) removed
 //                     3 = double tees removed, some had problems
 //
 //************************************************************************

 public static int removeDblTee(String course, String recurr, long sdate, long edate, int stime1, int etime1,
                                int stime2, int etime2, Connection con) throws Exception {


   Statement stmt = null;
   PreparedStatement pstmt = null;
   PreparedStatement pstmt2 = null;
   PreparedStatement pstmt3 = null;

   ResultSet rs = null;
   ResultSet rs2 = null;

   String courseName = "";
   String day = "";
   String day_name = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String errorMsg1 = "";

   long date = 0;

   int status = 0;
   int time = 0;

   short fb = 0;
   short skip = 0;


   try {

      if (sdate > 0) {          // if ok to continue

         //
         //  Use this info to scan the tee times and build the double tees whenever possible
         //
         if ( recurr.equalsIgnoreCase( "every sunday" ) ) {   // if every Sunday

            day = "Sunday";
         }
         if ( recurr.equalsIgnoreCase( "every monday" ) ) {   // if every Monday

            day = "Monday";
         }
         if ( recurr.equalsIgnoreCase( "every tuesday" ) ) {   // if every Tuesday

            day = "Tuesday";
         }
         if ( recurr.equalsIgnoreCase( "every wednesday" ) ) {   // if every Wednesday

            day = "Wednesday";
         }
         if ( recurr.equalsIgnoreCase( "every thursday" ) ) {   // if every Thursday

            day = "Thursday";
         }
         if ( recurr.equalsIgnoreCase( "every friday" ) ) {   // if every Friday

            day = "Friday";
         }
         if ( recurr.equalsIgnoreCase( "every saturday" ) ) {   // if every Satruday

            day = "Saturday";
         }

         //
         //  Determine which statement to use and then remove the 'Back 9' tee times
         //
         if (course.equals( "-ALL-" ) || course.equals( "" )) {    // if ALL Courses or none

            if (!day.equals( "" )) {    // if an individual day

               pstmt = con.prepareStatement (
                  "SELECT date, time, player1, player2, player3, player4, player5, fb, courseName FROM teecurr2 " +
                  "WHERE date >= ? AND date <= ? AND day = ? AND " +
                  "time >= ? AND time <= ? AND fb = 1");                               // day & any course

               pstmt.clearParameters();
               pstmt.setLong(1, sdate);
               pstmt.setLong(2, edate);
               pstmt.setString(3, day);
               pstmt.setInt(4, stime1);
               pstmt.setInt(5, etime1);

               pstmt2 = con.prepareStatement (
                  "SELECT date, time, fb, courseName FROM teecurr2 " +
                  "WHERE date >= ? AND date <= ? AND day = ? AND " +
                  "time >= ? AND time <= ? AND fb = 9");                                  // day & any course

               pstmt2.clearParameters();
               pstmt2.setLong(1, sdate);
               pstmt2.setLong(2, edate);
               pstmt2.setString(3, day);
               pstmt2.setInt(4, stime2);
               pstmt2.setInt(5, etime2);

            } else {

               if ( recurr.equalsIgnoreCase( "every day" )) {                // if everyday

                  pstmt = con.prepareStatement (
                     "SELECT date, time, player1, player2, player3, player4, player5, fb, courseName FROM teecurr2 " +
                      "WHERE date >= ? AND date <= ? AND " +
                      "time >= ? AND time <= ? AND fb = 1");                           // every day & any course

                  pstmt.clearParameters();
                  pstmt.setLong(1, sdate);
                  pstmt.setLong(2, edate);
                  pstmt.setInt(3, stime1);
                  pstmt.setInt(4, etime1);

                  pstmt2 = con.prepareStatement (
                     "SELECT date, time, fb, courseName FROM teecurr2 " +
                     "WHERE date >= ? AND date <= ? AND " +
                     "time >= ? AND time <= ? AND fb = 9");                           // every day & any course

                  pstmt2.clearParameters();
                  pstmt2.setLong(1, sdate);
                  pstmt2.setLong(2, edate);
                  pstmt2.setInt(3, stime2);
                  pstmt2.setInt(4, etime2);

               } else {

                  if ( recurr.equals( "All Weekdays" )) {                    // if Monday - Friday

                     pstmt = con.prepareStatement (
                        "SELECT date, time, player1, player2, player3, player4, player5, fb, courseName FROM teecurr2 " +
                        "WHERE date >= ? AND date <= ? AND " +
                        "day != 'Saturday' AND day != 'Sunday' AND " +
                        "time >= ? AND time <= ? AND fb = 1");                         // weekdays & any course

                     pstmt.clearParameters();
                     pstmt.setLong(1, sdate);
                     pstmt.setLong(2, edate);
                     pstmt.setInt(3, stime1);
                     pstmt.setInt(4, etime1);

                     pstmt2 = con.prepareStatement (
                         "SELECT date, time, fb, courseName FROM teecurr2 " +
                         "WHERE date >= ? AND date <= ? AND " +
                         "day != 'Saturday' AND day != 'Sunday' AND " +
                         "time >= ? AND time <= ? AND fb = 9");                   // weekdays & any course

                     pstmt2.clearParameters();
                     pstmt2.setLong(1, sdate);
                     pstmt2.setLong(2, edate);
                     pstmt2.setInt(3, stime2);
                     pstmt2.setInt(4, etime2);

                  } else {

                     if ( recurr.equals( "All Weekends" ) ) {                   // if Saturday & Sunday

                        pstmt = con.prepareStatement (
                           "SELECT date, time, player1, player2, player3, player4, player5, fb, courseName FROM teecurr2 " +
                           "WHERE date >= ? AND date <= ? AND " +
                           "(day = 'Saturday' OR day = 'Sunday') AND " +
                           "time >= ? AND time <= ? AND fb = 1");                       // weekends & any course

                        pstmt.clearParameters();
                        pstmt.setLong(1, sdate);
                        pstmt.setLong(2, edate);
                        pstmt.setInt(3, stime1);
                        pstmt.setInt(4, etime1);

                        pstmt2 = con.prepareStatement (
                           "SELECT date, time, fb, courseName FROM teecurr2 " +
                           "WHERE date >= ? AND date <= ? AND " +
                           "(day = 'Saturday' OR day = 'Sunday') AND " +
                           "time >= ? AND time <= ? AND fb = 9");                       // weekends & any course

                        pstmt2.clearParameters();
                        pstmt2.setLong(1, sdate);
                        pstmt2.setLong(2, edate);
                        pstmt2.setInt(3, stime2);
                        pstmt2.setInt(4, etime2);
                     }
                  }
               }
            }

         } else {      // course name was specified

            if (!day.equals( "" )) {                      // if an individual day

               pstmt = con.prepareStatement (
                  "SELECT date, time, player1, player2, player3, player4, player5, fb, courseName FROM teecurr2 " +
                  "WHERE date >= ? AND date <= ? AND day = ? AND " +
                  "time >= ? AND time <= ? AND fb = 1 AND " +
                  "courseName = ?");                                           // day & course

               pstmt.clearParameters();
               pstmt.setLong(1, sdate);
               pstmt.setLong(2, edate);
               pstmt.setString(3, day);
               pstmt.setInt(4, stime1);
               pstmt.setInt(5, etime1);
               pstmt.setString(6, course);

               pstmt2 = con.prepareStatement (
                  "SELECT date, time, fb, courseName FROM teecurr2 " +
                  "WHERE date >= ? AND date <= ? AND day = ? AND " +
                  "time >= ? AND time <= ? AND fb = 9 AND " +
                  "courseName = ?");                                               // day & course

               pstmt2.clearParameters();
               pstmt2.setLong(1, sdate);
               pstmt2.setLong(2, edate);
               pstmt2.setString(3, day);
               pstmt2.setInt(4, stime2);
               pstmt2.setInt(5, etime2);
               pstmt2.setString(6, course);

            } else {

               if ( recurr.equalsIgnoreCase( "every day" )) {   // if everyday

                  pstmt = con.prepareStatement (
                      "SELECT date, time, player1, player2, player3, player4, player5, fb, courseName FROM teecurr2 " +
                      "WHERE date >= ? AND date <= ? AND time >= ? AND " +
                      "time <= ? AND fb = 1 AND courseName = ?");                   // every day & course

                  pstmt.clearParameters();
                  pstmt.setLong(1, sdate);
                  pstmt.setLong(2, edate);
                  pstmt.setInt(3, stime1);
                  pstmt.setInt(4, etime1);
                  pstmt.setString(5, course);

                  pstmt2 = con.prepareStatement (
                      "SELECT date, time, fb, courseName FROM teecurr2 " +
                      "WHERE date >= ? AND date <= ? AND time >= ? AND " +
                      "time <= ? AND fb = 9 AND courseName = ?");                    // every day & course

                  pstmt2.clearParameters();
                  pstmt2.setLong(1, sdate);
                  pstmt2.setLong(2, edate);
                  pstmt2.setInt(3, stime2);
                  pstmt2.setInt(4, etime2);
                  pstmt2.setString(5, course);

               } else {

                  if ( recurr.equals( "All Weekdays" )) {   // if Monday - Friday

                     pstmt = con.prepareStatement (
                        "SELECT date, time, player1, player2, player3, player4, player5, fb, courseName FROM teecurr2 " +
                        "WHERE date >= ? AND date <= ? AND " +
                        "day != 'Saturday' AND day != 'Sunday' AND time >= ? AND " +
                        "time <= ? AND fb = 1 AND courseName = ?");                    // weekdays & course

                     pstmt.clearParameters();
                     pstmt.setLong(1, sdate);
                     pstmt.setLong(2, edate);
                     pstmt.setInt(3, stime1);
                     pstmt.setInt(4, etime1);
                     pstmt.setString(5, course);

                     pstmt2 = con.prepareStatement (
                        "SELECT date, time, fb, courseName FROM teecurr2 " +
                        "WHERE date >= ? AND date <= ? AND " +
                        "day != 'Saturday' AND day != 'Sunday' AND time >= ? AND " +
                        "time <= ? AND fb = 9 AND courseName = ?");                   // weekdays & course

                     pstmt2.clearParameters();
                     pstmt2.setLong(1, sdate);
                     pstmt2.setLong(2, edate);
                     pstmt2.setInt(3, stime2);
                     pstmt2.setInt(4, etime2);
                     pstmt2.setString(5, course);

                  } else {

                     if ( recurr.equals( "All Weekends" ) ) {   // if Saturday & Sunday

                        pstmt = con.prepareStatement (
                           "SELECT date, time, player1, player2, player3, player4, player5, fb, courseName FROM teecurr2 " +
                           "WHERE date >= ? AND date <= ? AND " +
                           "(day = 'Saturday' OR day = 'Sunday') AND time >= ? AND " +
                           "time <= ? AND fb = 1 AND courseName = ?");                  // weekends & course

                        pstmt.clearParameters();
                        pstmt.setLong(1, sdate);
                        pstmt.setLong(2, edate);
                        pstmt.setInt(3, stime1);
                        pstmt.setInt(4, etime1);
                        pstmt.setString(5, course);

                        pstmt2 = con.prepareStatement (
                           "SELECT date, time, fb, courseName FROM teecurr2 " +
                           "WHERE date >= ? AND date <= ? AND " +
                           "(day = 'Saturday' OR day = 'Sunday') AND time >= ? AND " +
                           "time <= ? AND fb = 9 AND courseName = ?");                   // weekends & course

                        pstmt2.clearParameters();
                        pstmt2.setLong(1, sdate);
                        pstmt2.setLong(2, edate);
                        pstmt2.setInt(3, stime2);
                        pstmt2.setInt(4, etime2);
                        pstmt2.setString(5, course);
                     }
                  }
               }
            }
         }        // end of statement tests

         //
         //  Now execute the selected statement and remove the double tees (Back 9 tees)
         //
         rs = pstmt.executeQuery();         // execute the prepared stmt

         while (rs.next()) {                // if tee times exist

            date = rs.getInt("date");
            time = rs.getInt("time");
            player1 = rs.getString("player1");
            player2 = rs.getString("player2");
            player3 = rs.getString("player3");
            player4 = rs.getString("player4");
            player5 = rs.getString("player5");
            fb = rs.getShort("fb");
            courseName = rs.getString("courseName");

            if (status == 0) {
               status = 1;                    // found at least one
            }

            //
            //  remove all double tee (Back) slots that aren't occupied !!!!!!!!!!
            //
            if (player1.equals( "" ) && player2.equals( "" ) && player3.equals( "" ) && player4.equals( "" ) &&
                player5.equals( "" )) {

               pstmt3 = con.prepareStatement (
                   "DELETE FROM teecurr2 " +
                           "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

               //
               //  execute the prepared statement to update the tee time slot
               //
               pstmt3.clearParameters();        // clear the parms
               pstmt3.setLong(1, date);
               pstmt3.setInt(2, time);
               pstmt3.setInt(3, fb);
               pstmt3.setString(4, courseName);
               pstmt3.executeUpdate();

               pstmt3.close();

               if (status < 2) {
                  status = 2;                    // removed at least one
               }

            } else {

               status = 3;            // unable to remove all Back 9 tee times
            }

         }  // end of WHILE tee times

         pstmt.close();

         //
         //  Now change the cross-overs back to Front tees
         //
         rs = pstmt2.executeQuery();      // execute the prepared stmt

         while (rs.next()) {                // if tee times exist

            date = rs.getInt("date");
            time = rs.getInt("time");
            fb = rs.getShort("fb");
            courseName = rs.getString("courseName");

            //
            //  remove all cross-over tee slots that aren't occupied
            //
            pstmt3 = con.prepareStatement (
                "UPDATE teecurr2 SET fb = 0 " +
                "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

            //
            //  execute the prepared statement to update the tee time slot
            //
            pstmt3.clearParameters();        // clear the parms
            pstmt3.setLong(1, date);
            pstmt3.setInt(2, time);
            pstmt3.setInt(3, fb);
            pstmt3.setString(4, courseName);
            pstmt3.executeUpdate();

            pstmt3.close();

         }  // end of WHILE tee times

         pstmt2.close();
      }                           // end of IF ok

   }
   catch (Exception e2) {

      errorMsg1 = "Error1 in SystemUtils removeDblTee: ";
      errorMsg1 = errorMsg1 + e2.getMessage();                                // build error msg

      logError(errorMsg1);                                       // log it

      throw new Exception("Error adding double tee - removeDblTee Exception: " + e2.getMessage());
   }

   return(status);

 }  // end of removeDblTee


 //************************************************************************
 //  insertTee - Inserts a tee time into teecurr
 //
 //    called by:  buildDbltee above
 //                Proshop_dsheet
 //
 //
 //************************************************************************

 public static void insertTee(long date, int time, int fb, String course, String day_name, Connection con)
                          throws Exception {


   ResultSet rs = null;

   //
   //  variables for this method
   //
   int year = 0;
   int month = 0;
   int day = 0;
   int day_num = 0;
   int hr = 0;
   int min = 0;
   int ampm = 0;
   int index = 0;
   int event_type = 0;
   int stime = 0;
   int etime = 0;
   int stime2 = 0;
   int etime2 = 0;

   long month2 = 0;
   long day2 = 0;
   long year2 = 0;

   String sfb = "";
   String sfb2 = "";
   String sfb3 = "";
   String event = "";
   String event_color = "";
   String rest = "";
   String rest2 = "";
   String rest_color = "";
   String rest_color2 = "";
   String rest_recurr = "";
   String rest5 = "";                      // default values
   String rest52 = "";
   String rest5_color = "";
   String rest5_color2 = "";
   String rest5_recurr = "";
   String lott = "";                      // lottery name
   String lott2 = "";                      // lottery name
   String lott_color = "";
   String lott_color2 = "";
   String lott_recurr = "";

   //
   //  Gather parms for tee time
   //
   year2 = date / 10000;
   month2 = (date - (year2 * 10000)) / 100;
   day2 = (date - (year2 * 10000)) - (month2 * 100);

   month = (int)month2;
   day = (int)day2;
   year = (int)year2;

   hr = time / 100;
   min = time - (hr * 100);

   try {
      //
      // Prepared statement to search the events table
      //
      PreparedStatement pstmt2 = con.prepareStatement (
         "SELECT name, stime, etime, color, type, fb, stime2, etime2, fb2 FROM events2b " +
         "WHERE date = ? AND (courseName = ? OR courseName = '-ALL-') AND inactive = 0");

      //
      // Prepared statement to search the restrictions tables
      //
      PreparedStatement pstmt3 = con.prepareStatement (
         "SELECT name, recurr, color, fb FROM restriction2 WHERE sdate <= ? AND edate >= ? " +
         "AND stime <= ? AND etime >= ? AND (courseName = ? OR courseName = '-ALL-')");

      PreparedStatement pstmt3a = con.prepareStatement (
         "SELECT name, recurr, color, fb FROM fives2 WHERE sdate <= ? AND edate >= ? " +
         "AND stime <= ? AND etime >= ? AND (courseName = ? OR courseName = '-ALL-')");

      PreparedStatement pstmt3b = con.prepareStatement (
         "SELECT name, recurr, color, fb FROM lottery3 WHERE sdate <= ? AND edate >= ? " +
         "AND stime <= ? AND etime >= ? AND (courseName = ? OR courseName = '-ALL-')");

      //
      //  Prep'd statement to insert tee time in teecurr
      //
      PreparedStatement pstmt4 = con.prepareStatement (
         "INSERT INTO teecurr2 (date, mm, dd, yy, day, hr, min, time, event, event_color, " +
         "restriction, rest_color, player1, player2, player3, player4, username1, " +
         "username2, username3, username4, p1cw, p2cw, p3cw, p4cw, first, in_use, in_use_by, " +
         "event_type, hndcp1, hndcp2, hndcp3, hndcp4, show1, show2, show3, show4, fb, " +
         "player5, username5, p5cw, hndcp5, show5, notes, hideNotes, lottery, courseName, blocker, " +
         "proNew, proMod, memNew, memMod, rest5, rest5_color, " +
         "mNum1, mNum2, mNum3, mNum4, mNum5, lottery_color, userg1, userg2, " +
         "userg3, userg4, userg5, hotelNew, hotelMod, orig_by, conf, p91, p92, p93, p94, p95, " +
         "pos1, pos2, pos3, pos4, pos5, hole, create_date) " +
         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, '', '', '', '', '', '', " +
         "'', '', '', '', '', '', 0, '0', '', ?, 0, 0, 0, 0, 0, 0, 0, 0, ?, '', " +
         "'', '', 0, 0, '', 0, ?, ?, '', 0, 0, 0, 0, ?, ?, '', '', '', '', '', " +
         "?, '', '', '', '', '', 0, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, '', now())");

      //
      //   Check for any events during this time
      //
      sfb = "Front";
      if (fb == 1) {       // if back requested

         sfb = "Back";
      }

      event = "";                      // default values
      event_color = "";
      event_type = 0;

      pstmt2.clearParameters();              // clear the parms
      pstmt2.setLong(1, date);               // put the parm in stmt
      pstmt2.setString(2, course);
      rs = pstmt2.executeQuery();            // execute the prepared stmt

      if (rs.next()) {

         stime = rs.getInt(2);
         etime = rs.getInt(3);
         sfb2 = rs.getString(6);
         stime2 = rs.getInt(7);
         etime2 = rs.getInt(8);
         sfb3 = rs.getString(9);

         if ((time >= stime && time <= etime) && (sfb2.equals( "Both" ) || sfb2.equals( sfb ))) {

            event = rs.getString(1);            // event exists for this slot
            event_color = rs.getString(4);
            event_type = rs.getInt(5);

         } else {           // check 2nd set of blockers for event

            if ((time >= stime2 && time <= etime2) && (sfb3.equals( "Both" ) || sfb3.equals( sfb ))) {

               event = rs.getString(1);            // event exists for this slot
               event_color = rs.getString(4);
               event_type = rs.getInt(5);
            }
         }
      }
      pstmt2.close();   // close the stmt

      //
      //   Check for any restrictions during this time
      //
      rest = "";                      // default values
      rest2 = "";
      rest_color = "";
      rest_color2 = "";
      rest_recurr = "";

      pstmt3.clearParameters();        // clear the parms
      pstmt3.setLong(1, date);         // put the parms in stmt
      pstmt3.setLong(2, date);
      pstmt3.setInt(3, time);
      pstmt3.setInt(4, time);
      pstmt3.setString(5, course);
      rs = pstmt3.executeQuery();      // execute the prepared stmt

      loop1:
      while (rs.next()) {

         rest2 = rs.getString(1);          // rest exists for this slot time
         rest_recurr = rs.getString(2);
         rest_color2 = rs.getString(3);
         sfb2 = rs.getString(4);

         if (sfb2.equals( "Both" ) || sfb2.equals( sfb )) {

            //
            //  We must check the recurrence for this day (Monday, etc.)
            //
            if (rest_recurr.equalsIgnoreCase( "every " + day_name )) {

               rest = rest2;
               rest_color = rest_color2;
               break loop1;                   // done checking - exit while loop
            }

            if (rest_recurr.equalsIgnoreCase( "every day" )) {   // if everyday

               rest = rest2;
               rest_color = rest_color2;
               break loop1;                   // done checking - exit while loop
            }

            if ((rest_recurr.equalsIgnoreCase( "all weekdays" )) &&
                (!day_name.equalsIgnoreCase( "saturday" )) &&
                (!day_name.equalsIgnoreCase( "sunday" ))) {

               rest = rest2;
               rest_color = rest_color2;
               break loop1;                   // done checking - exit while loop
            }

            if ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&
                (day_name.equalsIgnoreCase( "saturday" ))) {

               rest = rest2;
               rest_color = rest_color2;
               break loop1;                   // done checking - exit while loop
            }

            if ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&
                (day_name.equalsIgnoreCase( "sunday" ))) {

               rest = rest2;
               rest_color = rest_color2;
               break loop1;                   // done checking - exit while loop
            }
         }

      }   // end of loop1 while loop

      pstmt3.close();   // close the stmt

      //
      //   Check for any 5-some restrictions during this time
      //
      rest5 = "";                      // default values
      rest52 = "";
      rest5_color = "";
      rest5_color2 = "";
      rest5_recurr = "";

      pstmt3a.clearParameters();        // clear the parms
      pstmt3a.setLong(1, date);         // put the parms in stmt
      pstmt3a.setLong(2, date);
      pstmt3a.setInt(3, time);
      pstmt3a.setInt(4, time);
      pstmt3a.setString(5, course);
      rs = pstmt3a.executeQuery();      // execute the prepared stmt

      loop2:
      while (rs.next()) {

         rest52 = rs.getString(1);          // 5-some rest exists for this slot time
         rest5_recurr = rs.getString(2);
         rest5_color2 = rs.getString(3);
         sfb2 = rs.getString(4);

         if (sfb2.equals( "Both" ) || sfb2.equals( sfb )) {

            //
            //  We must check the recurrence for this day (Monday, etc.)
            //
            if (rest5_recurr.equalsIgnoreCase( "every " + day_name )) {

               rest5 = rest52;
               rest5_color = rest5_color2;
               break loop2;                   // done checking - exit while loop
            }

            if (rest5_recurr.equalsIgnoreCase( "every day" )) {   // if everyday

               rest5 = rest52;
               rest5_color = rest5_color2;
               break loop2;                   // done checking - exit while loop
            }

            if ((rest5_recurr.equalsIgnoreCase( "all weekdays" )) &&
                (!day_name.equalsIgnoreCase( "saturday" )) &&
                (!day_name.equalsIgnoreCase( "sunday" ))) {

               rest5 = rest52;
               rest5_color = rest5_color2;
               break loop2;                   // done checking - exit while loop
            }

            if ((rest5_recurr.equalsIgnoreCase( "all weekends" )) &&
                (day_name.equalsIgnoreCase( "saturday" ))) {

               rest5 = rest52;
               rest5_color = rest5_color2;
               break loop2;                   // done checking - exit while loop
            }

            if ((rest5_recurr.equalsIgnoreCase( "all weekends" )) &&
                (day_name.equalsIgnoreCase( "sunday" ))) {

               rest5 = rest52;
               rest5_color = rest5_color2;
               break loop2;                   // done checking - exit while loop
            }
         }

      }   // end of loop2 while loop

      pstmt3a.close();   // close the stmt

      //
      //   Check for any lotteries during this time
      //
      lott = "";                      // default values
      lott2 = "";
      lott_color = "";
      lott_color2 = "";
      lott_recurr = "";

      pstmt3b.clearParameters();        // clear the parms
      pstmt3b.setLong(1, date);         // put the parms in stmt
      pstmt3b.setLong(2, date);
      pstmt3b.setInt(3, time);
      pstmt3b.setInt(4, time);
      pstmt3b.setString(5, course);
      rs = pstmt3b.executeQuery();      // execute the prepared stmt

      loop3:
      while (rs.next()) {

         lott2 = rs.getString(1);          // lottery exists for this slot time
         lott_recurr = rs.getString(2);
         lott_color2 = rs.getString(3);
         sfb2 = rs.getString(4);

         if (sfb2.equals( "Both" ) || sfb2.equals( sfb )) {

            //
            //  We must check the recurrence for this day (Monday, etc.)
            //
            if (lott_recurr.equalsIgnoreCase( "every " + day_name )) {

               lott = lott2;
               lott_color = lott_color2;
               break loop3;                   // done checking - exit while loop
            }

            if (lott_recurr.equalsIgnoreCase( "every day" )) {   // if everyday

               lott = lott2;
               lott_color = lott_color2;
               break loop3;                   // done checking - exit while loop
            }

            if ((lott_recurr.equalsIgnoreCase( "all weekdays" )) &&
                (!day_name.equalsIgnoreCase( "saturday" )) &&
                (!day_name.equalsIgnoreCase( "sunday" ))) {

               lott = lott2;
               lott_color = lott_color2;
               break loop3;                   // done checking - exit while loop
            }

            if ((lott_recurr.equalsIgnoreCase( "all weekends" )) &&
                (day_name.equalsIgnoreCase( "saturday" ))) {

               lott = lott2;
               lott_color = lott_color2;
               break loop3;                   // done checking - exit while loop
            }

            if ((lott_recurr.equalsIgnoreCase( "all weekends" )) &&
                (day_name.equalsIgnoreCase( "sunday" ))) {

               lott = lott2;
               lott_color = lott_color2;
               break loop3;                   // done checking - exit while loop
            }
         }

      }   // end of loop2 while loop

      pstmt3b.close();   // close the stmt

      //
      //   Add this time slot to teecurr
      //
      pstmt4.clearParameters();        // clear the parms
      pstmt4.setLong(1, date);
      pstmt4.setInt(2, month);
      pstmt4.setInt(3, day);
      pstmt4.setInt(4, year);
      pstmt4.setString(5, day_name);
      pstmt4.setInt(6, hr);
      pstmt4.setInt(7, min);
      pstmt4.setInt(8, time);
      pstmt4.setString(9, event);
      pstmt4.setString(10, event_color);
      pstmt4.setString(11, rest);
      pstmt4.setString(12, rest_color);
      pstmt4.setInt(13, event_type);
      pstmt4.setInt(14, fb);
      pstmt4.setString(15, lott);
      pstmt4.setString(16, course);
      pstmt4.setString(17, rest5);
      pstmt4.setString(18, rest5_color);
      pstmt4.setString(19, lott_color);

      pstmt4.executeUpdate();        // execute the prepared stmt - insert the tee time slot

      pstmt4.close();   // close the stmt

   }
   catch (Exception e) {

      throw new Exception("Error inserting tee time - SystemUtils.insertTee " + e.getMessage());
   }

 }  // end of insertTee


 //************************************************************************
 //  insertTee2 - Inserts a tee time into teecurr for events
 //
 //    called by:  Proshop_evntChkAll
 //
 //
 //************************************************************************

 public static void insertTee2(parmTee parm, Connection con)
                          throws Exception {


   PreparedStatement pstmt2 = null;
   PreparedStatement pstmt3 = null;
   Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   String [] mnumA = new String [5];             // array to hold the member numbers
   String [] userA = new String [5];             // array to hold the member usernames
   String [] mshipA = new String [5];            // array to hold the member membership types
   String [] mtypeA = new String [5];            // array to hold the member member types
   String [] gtypeA = new String [5];            // array to hold the player guest types
   String [] gtypes = new String[35];            // array to hold all guest types

   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

   int i = 0;
   int day = 0;
   int month = parm.mm - 1;

   int [] grevA = new int [5];                   // array to hold whether or not a player's gtype is revenue generating
   int [] grevs = new int[35];                   // array to hold revenue generating values for all guest types

   //
   //  Caller does not set the day name, so get it now for teecurr and teepast
   //
   Calendar cal = new GregorianCalendar();            // get todays date

   cal.set(Calendar.YEAR,parm.yy);                    // set year in cal
   cal.set(Calendar.MONTH,month);                     // set month in cal
   cal.set(Calendar.DAY_OF_MONTH,parm.dd);            // set day in cal

   day = cal.get(Calendar.DAY_OF_WEEK);               // day of week

   parm.day = day_table[day];                         // get name for day

   for (int k=0; k<5; k++) {
       mshipA[k] = "";
       mtypeA[k] = "";
       gtypeA[k] = "";
       grevA[k] = 0;
   }

   try {

      if (parm.edate >= parm.date) {       // if today or later then use teecurr

         userA[0] = parm.username1;
         userA[1] = parm.username2;
         userA[2] = parm.username3;
         userA[3] = parm.username4;
         userA[4] = parm.username5;
         mnumA[0] = "";
         mnumA[1] = "";
         mnumA[2] = "";
         mnumA[3] = "";
         mnumA[4] = "";

         //
         //  Proshop_evntChkAll does not set the member number - do this now
         //
         for (i=0; i<5; i++) {

            if (!userA[i].equals( "" )) {       // if username provided

               pstmt3 = con.prepareStatement (
                  "SELECT memNum FROM member2b WHERE username= ?");

               pstmt3.clearParameters();        // clear the parms
               pstmt3.setString(1, userA[i]);

               rs = pstmt3.executeQuery();

               if (rs.next()) {

                  mnumA[i] = rs.getString(1);
               }
               pstmt3.close();
            }
         }

         //
         //  Check members in this tee time for any configured flags for display on proshop tee sheet
         //
         parmSlot parm1 = new parmSlot();          // allocate a parm block for a single tee time

         //
         //  set parms for this group - only need usernames
         //
         parm1.user1 = parm.username1;
         parm1.user2 = parm.username2;
         parm1.user3 = parm.username3;
         parm1.user4 = parm.username4;
         parm1.user5 = parm.username5;

         verifySlot.checkTFlag(parm1, con);         // check for tflags - returned in parm1


         //
         // Prepared statement to put this days tee times in the teecurr2 table
         //
         pstmt3 = con.prepareStatement (
            "INSERT INTO teecurr2 (date, mm, dd, yy, day, hr, min, time, event, event_color, " +
                "restriction, rest_color, player1, player2, player3, player4, username1, " +
                "username2, username3, username4, p1cw, p2cw, p3cw, p4cw, first, in_use, in_use_by, " +
                "event_type, hndcp1, hndcp2, hndcp3, hndcp4, show1, show2, show3, show4, fb, " +
                "player5, username5, p5cw, hndcp5, show5, notes, hidenotes, lottery, courseName, blocker, " +
                "proNew, proMod, memNew, memMod, rest5, rest5_color, mNum1, mNum2, mNum3, mNum4, mNum5, " +
                "lottery_color, userg1, userg2, userg3, userg4, userg5, hotelNew, hotelMod, orig_by, " +
                "conf, p91, p92, p93, p94, p95, pos1, pos2, pos3, pos4, pos5, hole, " +
                "tflag1, tflag2, tflag3, tflag4, tflag5, create_date) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                "'', '', LEFT(?, 43), LEFT(?, 43), LEFT(?, 43), LEFT(?, 43), ?, " +
                "?, ?, ?, ?, ?, ?, ?, 0, 0, '', " +
                "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                "LEFT(?, 43), ?, ?, ?, ?, '', 0, '', ?, '', " +
                "0, 0, 0, 0, '', '', ?, ?, ?, ?, ?, " +
                "'', ?, ?, ?, ?, ?, 0, 0, '', " +
                "'', ?, ?, ?, ?, ?, 0, 0, 0, 0, 0, '', " +
                "?, ?, ?, ?, ?, now())");

         pstmt3.clearParameters();         // clear the parms
         pstmt3.setLong(1, parm.edate);         // put the parms in stmt for tee slot
         pstmt3.setInt(2, parm.mm);
         pstmt3.setInt(3, parm.dd);
         pstmt3.setInt(4, parm.yy);
         pstmt3.setString(5, parm.day);
         pstmt3.setInt(6, parm.hr);
         pstmt3.setInt(7, parm.min);
         pstmt3.setInt(8, parm.time);
         pstmt3.setString(9, parm.event);            // event name
         pstmt3.setString(10, parm.event_color);           // event color
         pstmt3.setString(11, parm.player1);
         pstmt3.setString(12, parm.player2);
         pstmt3.setString(13, parm.player3);
         pstmt3.setString(14, parm.player4);
         pstmt3.setString(15, parm.username1);
         pstmt3.setString(16, parm.username2);
         pstmt3.setString(17, parm.username3);
         pstmt3.setString(18, parm.username4);
         pstmt3.setString(19, parm.p1cw);
         pstmt3.setString(20, parm.p2cw);
         pstmt3.setString(21, parm.p3cw);
         pstmt3.setString(22, parm.p4cw);
         pstmt3.setInt(23, parm.event_type);
         pstmt3.setFloat(24, parm.hndcp1);
         pstmt3.setFloat(25, parm.hndcp2);
         pstmt3.setFloat(26, parm.hndcp3);
         pstmt3.setFloat(27, parm.hndcp4);
         pstmt3.setInt(28, parm.show1);
         pstmt3.setInt(29, parm.show2);
         pstmt3.setInt(30, parm.show3);
         pstmt3.setInt(31, parm.show4);
         pstmt3.setInt(32, parm.fb);
         pstmt3.setString(33, parm.player5);
         pstmt3.setString(34, parm.username5);
         pstmt3.setString(35, parm.p5cw);
         pstmt3.setFloat(36, parm.hndcp5);
         pstmt3.setInt(37, parm.show5);
         pstmt3.setString(38, parm.courseName);
         pstmt3.setString(39, mnumA[0]);
         pstmt3.setString(40, mnumA[1]);
         pstmt3.setString(41, mnumA[2]);
         pstmt3.setString(42, mnumA[3]);
         pstmt3.setString(43, mnumA[4]);
         pstmt3.setString(44, parm.userg1);
         pstmt3.setString(45, parm.userg2);
         pstmt3.setString(46, parm.userg3);
         pstmt3.setString(47, parm.userg4);
         pstmt3.setString(48, parm.userg5);
         pstmt3.setInt(49, parm.p91);
         pstmt3.setInt(50, parm.p92);
         pstmt3.setInt(51, parm.p93);
         pstmt3.setInt(52, parm.p94);
         pstmt3.setInt(53, parm.p95);
         pstmt3.setString(54, parm1.tflag1);
         pstmt3.setString(55, parm1.tflag2);
         pstmt3.setString(56, parm1.tflag3);
         pstmt3.setString(57, parm1.tflag4);
         pstmt3.setString(58, parm1.tflag5);

         pstmt3.executeUpdate();          // execute the prepared stmt

         pstmt3.close();

      } else {          // not today - use teepast

        //
        //  Gather information regarding guest types, membership/member types, and revenue generating guest types
        //

        int total_guests = 0;
        int j = 0;

        try {


            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT guest, revenue FROM guest5");

            while (rs.next()) {

                gtypes[j] = rs.getString(1);
                grevs[j] = rs.getInt(2);

                j++;
            }

            stmt.close();

        } catch (Exception exc) {
        }

        if (j > 0) total_guests = j - 1;

        // get mship1, mtype1, gtype1, grev1
        if (!parm.username1.equals("")) {

            try {

                pstmt2 = con.prepareStatement (
                        "SELECT m_ship, m_type FROM member2b WHERE username = ?");

                pstmt2.clearParameters();
                pstmt2.setString(1, parm.username1);
                rs2 = pstmt2.executeQuery();

                if (rs2.next()) {

                    mshipA[0] = rs2.getString(1);
                    mtypeA[0] = rs2.getString(2);
                }

                pstmt2.close();

            } catch (Exception ignore) { }

        } else if (!parm.player1.equals("")) {

            // check to see which type of guest this player is
            loop1:
            for (j=0; j <= total_guests; j++) {

                try {
                if (parm.player1.substring(0, gtypes[j].length()).equalsIgnoreCase(gtypes[j])) {

                    gtypeA[0] = gtypes[j];
                    grevA[0] = grevs[j];
                    break;
                }
                } catch (IndexOutOfBoundsException ignore) {}
            }

        }

        // get mship2, mtype2, gtype2, grev2
        if (!parm.username2.equals("")) {

            try {

                pstmt2 = con.prepareStatement (
                        "SELECT m_ship, m_type FROM member2b WHERE username = ?");

                pstmt2.clearParameters();
                pstmt2.setString(1, parm.username2);
                rs2 = pstmt2.executeQuery();

                if (rs2.next()) {

                    mshipA[1] = rs2.getString(1);
                    mtypeA[1] = rs2.getString(2);
                }

                pstmt2.close();

            } catch (Exception ignore) { }

        } else if (!parm.player2.equals("")) {

            // check to see which type of guest this player is
            loop12:
            for (j=0; j <= total_guests; j++) {

                try {
                if (parm.player2.substring(0, gtypes[j].length()).equalsIgnoreCase(gtypes[j])) {

                    gtypeA[1] = gtypes[j];
                    grevA[1] = grevs[j];
                    break;
                }
                } catch (IndexOutOfBoundsException exc) {}
            }

        }

        // get mship3, mtype3, gtype3, grev3
        if (!parm.username3.equals("") && mshipA[2].equals("") && mtypeA[2].equals("")) {

            try {

                pstmt2 = con.prepareStatement (
                        "SELECT m_ship, m_type FROM member2b WHERE username = ?");

                pstmt2.clearParameters();
                pstmt2.setString(1, parm.username3);
                rs2 = pstmt2.executeQuery();

                if (rs2.next()) {

                    mshipA[2] = rs2.getString(1);
                    mtypeA[2] = rs2.getString(2);
                }

                pstmt2.close();

            } catch (Exception ignore) { }

        } else if (!parm.player3.equals("")) {

            // check to see which type of guest this player is
            loop3:
            for (j=0; j <= total_guests; j++) {

                try {
                if (parm.player3.substring(0, gtypes[j].length()).equalsIgnoreCase(gtypes[j])) {

                    gtypeA[2] = gtypes[j];
                    grevA[2] = grevs[j];
                    break;
                }
                } catch (IndexOutOfBoundsException ignore) {}
            }

        }

        // get mship4, mtype4, gtype4, grev4
        if (!parm.username4.equals("")) {

            try {

                pstmt2 = con.prepareStatement (
                        "SELECT m_ship, m_type FROM member2b WHERE username = ?");

                pstmt2.clearParameters();
                pstmt2.setString(1, parm.username4);
                rs2 = pstmt2.executeQuery();

                if (rs2.next()) {

                    mshipA[3] = rs2.getString(1);
                    mtypeA[3] = rs2.getString(2);
                }

                pstmt2.close();

            } catch (Exception ignore) { }

        } else if (!parm.player4.equals("")) {

            // check to see which type of guest this player is
            loop4:
            for (j=0; j <= total_guests; j++) {

                try {
                if (parm.player4.substring(0, gtypes[j].length()).equalsIgnoreCase(gtypes[j])) {

                    gtypeA[3] = gtypes[j];
                    grevA[3] = grevs[j];
                    break;
                }
                } catch (IndexOutOfBoundsException ignore) {}
            }

        }

        // get mship5, mtype5, gtype5, grev5
        if (!parm.username5.equals("")) {

            try {

                pstmt2 = con.prepareStatement (
                        "SELECT m_ship, m_type FROM member2b WHERE username = ?");

                pstmt2.clearParameters();
                pstmt2.setString(1, parm.username5);
                rs2 = pstmt2.executeQuery();

                if (rs2.next()) {

                    mshipA[4] = rs2.getString(1);
                    mtypeA[4] = rs2.getString(2);
                }

                pstmt2.close();

            } catch (Exception ignore) { }

        } else if (!parm.player5.equals("")) {

            // check to see which type of guest this player is
            loop5:
            for (j=0; j <= total_guests; j++) {

                try {
                if (parm.player5.substring(0, gtypes[j].length()).equalsIgnoreCase(gtypes[j])) {

                    gtypeA[4] = gtypes[j];
                    grevA[4] = grevs[j];
                    break;
                }
                } catch (IndexOutOfBoundsException ignore) {}
            }

        }

         //
         // Prepared statement to put this days tee times in the teepast table
         //
         pstmt3 = con.prepareStatement (
            "INSERT INTO teepast2 (" +
            "date, mm, dd, yy, day, hr, min, time, event, event_color, " +
            "restriction, rest_color, player1, player2, player3, player4, username1, username2, username3, username4, " +
            "p1cw, p2cw, p3cw, p4cw, show1, show2, show3, show4, fb, player5, " +
            "username5, p5cw, show5, courseName, proNew, proMod, memNew, memMod, mNum1, mNum2, " +
            "mNum3, mNum4, mNum5, userg1, userg2, userg3, userg4, userg5, hotelNew, hotelMod, " +
            "orig_by, conf, notes, p91, p92, p93, p94, p95, mship1, mship2, " +
            "mship3, mship4, mship5, mtype1, mtype2, mtype3, mtype4, mtype5, gtype1, gtype2, " +
            "gtype3, gtype4, gtype5, grev1, grev2, grev3, grev4, grev5) " +
            "VALUES (" +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "'', '', LEFT(?, 43), LEFT(?, 43), LEFT(?, 43), LEFT(?, 43), ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, LEFT(?, 43), " +
            "?, ?, ?, ?, 0, 0, 0, 0, '', '', " +
            "'', '', '', ?, ?, ?, ?, ?, 0, 0, " +
            "'', '', '', ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?)");

         pstmt3.clearParameters();         // clear the parms
         pstmt3.setLong(1, parm.edate);         // put the parms in stmt for tee slot
         pstmt3.setInt(2, parm.mm);
         pstmt3.setInt(3, parm.dd);
         pstmt3.setInt(4, parm.yy);
         pstmt3.setString(5, parm.day);
         pstmt3.setInt(6, parm.hr);
         pstmt3.setInt(7, parm.min);
         pstmt3.setInt(8, parm.time);
         pstmt3.setString(9, parm.event);            // event name
         pstmt3.setString(10, parm.event_color);           // event color
         pstmt3.setString(11, parm.player1);
         pstmt3.setString(12, parm.player2);
         pstmt3.setString(13, parm.player3);
         pstmt3.setString(14, parm.player4);
         pstmt3.setString(15, parm.username1);
         pstmt3.setString(16, parm.username2);
         pstmt3.setString(17, parm.username3);
         pstmt3.setString(18, parm.username4);
         pstmt3.setString(19, parm.p1cw);
         pstmt3.setString(20, parm.p2cw);
         pstmt3.setString(21, parm.p3cw);
         pstmt3.setString(22, parm.p4cw);
         pstmt3.setInt(23, parm.show1);
         pstmt3.setInt(24, parm.show2);
         pstmt3.setInt(25, parm.show3);
         pstmt3.setInt(26, parm.show4);
         pstmt3.setInt(27, parm.fb);
         pstmt3.setString(28, parm.player5);
         pstmt3.setString(29, parm.username5);
         pstmt3.setString(30, parm.p5cw);
         pstmt3.setInt(31, parm.show5);
         pstmt3.setString(32, parm.courseName);
         pstmt3.setString(33, parm.userg1);
         pstmt3.setString(34, parm.userg2);
         pstmt3.setString(35, parm.userg3);
         pstmt3.setString(36, parm.userg4);
         pstmt3.setString(37, parm.userg5);
         pstmt3.setInt(38, parm.p91);
         pstmt3.setInt(39, parm.p92);
         pstmt3.setInt(40, parm.p93);
         pstmt3.setInt(41, parm.p94);
         pstmt3.setInt(42, parm.p95);
         pstmt3.setString(43, mshipA[0]);
         pstmt3.setString(44, mshipA[1]);
         pstmt3.setString(45, mshipA[2]);
         pstmt3.setString(46, mshipA[3]);
         pstmt3.setString(47, mshipA[4]);
         pstmt3.setString(48, mtypeA[0]);
         pstmt3.setString(49, mtypeA[1]);
         pstmt3.setString(50, mtypeA[2]);
         pstmt3.setString(51, mtypeA[3]);
         pstmt3.setString(52, mtypeA[4]);
         pstmt3.setString(53, gtypeA[0]);
         pstmt3.setString(54, gtypeA[1]);
         pstmt3.setString(55, gtypeA[2]);
         pstmt3.setString(56, gtypeA[3]);
         pstmt3.setString(57, gtypeA[4]);
         pstmt3.setInt(58, grevA[0]);
         pstmt3.setInt(59, grevA[1]);
         pstmt3.setInt(60, grevA[2]);
         pstmt3.setInt(61, grevA[3]);
         pstmt3.setInt(62, grevA[4]);

         pstmt3.executeUpdate();          // execute the prepared stmt

         pstmt3.close();

      }  // end of IF Date

      //
      //  Track the history of this tee time - make entry in 'teehist' table (check if new or update)
      //
      String user = "Proshop";
      String fullName = "Proshop Event";

      updateHist(parm.edate, parm.day, parm.time, parm.fb, parm.courseName, parm.player1, parm.player2, parm.player3,
                             parm.player4, parm.player5, user, fullName, 0, con);

   }
   catch (Exception e) {

      throw new Exception("Error inserting tee time - SystemUtils.insertTee2 " + e.getMessage());
   }

 }  // end of insertTee2


 //************************************************************************
 //  filter - Searches the string provided for special characters and
 //           replaces them with HTML friendly characters.
 //
 //           < becomes &lt
 //           > becomes &gt
 //           " becomes &quot
 //           & becomes &amp
 //           # becomes &#35 (hash - not pound)
 //
 //
 //    called by:  Proshop_addevnt
 //                Proshop_events
 //                Proshop_editevnt
 //                Proshop_mrest
 //                Proshop_addmrest
 //                Proshop_editmrest
 //                Proshop_grest
 //                Proshop_addgrest
 //                Proshop_editgrest
 //                ...and others
 //
 //************************************************************************

 public static String filter(String input) {


    StringBuffer filtered = new StringBuffer(input.length());

    char c;

    for(int i=0; i<input.length(); i++) {

       c = input.charAt(i);
       if (c == '<') {                  // if special char
          filtered.append("&lt;");      // change to html char
       } else if (c == '>') {
          filtered.append("&gt;");
       } else if (c == '"') {
          filtered.append("&quot;");
       } else if (c == '&') {
          filtered.append("&amp;");
       } else if (c == '#') {
          filtered.append("&#35;");
       } else if (c == '!') {
          filtered.append("&#33;");
       } else if (c == '$') {
          filtered.append("&#36;");
       } else if (c == '%') {
          filtered.append("&#37;");
       } else if (c == '(') {
          filtered.append("&#40;");
       } else if (c == ')') {
          filtered.append("&#41;");
       } else if (c == '*') {
          filtered.append("&#42;");
       } else if (c == '+') {
          filtered.append("&#43;");
       } else if (c == ',') {
          filtered.append("&#44;");
       } else if (c == '-') {
          filtered.append("&#45;");
       } else if (c == '.') {
          filtered.append("&#46;");
       } else if (c == '/') {
          filtered.append("&#47;");
       } else if (c == '=') {
          filtered.append("&#61;");
       } else if (c == '?') {
          filtered.append("&#63;");
       } else if (c == '@') {
          filtered.append("&#64;");
       } else if (c == '[') {
          filtered.append("&#91;");
       } else if (c == ']') {
          filtered.append("&#93;");
       } else if (c == '^') {
          filtered.append("&#94;");
       } else if (c == '_') {
          filtered.append("&#95;");
       } else if (c == '{') {
          filtered.append("&#123;");
       } else if (c == '|') {
          filtered.append("&#124;");
       } else if (c == '}') {
          filtered.append("&#125;");
       } else if (c == '~') {
          filtered.append("&#126;");
       } else {
          filtered.append(c);
       }
    }

    return(filtered.toString());

 }  // end of filter


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


 //************************************************************************
 //  scanName - Searches the string provided for special characters and
 //             returns an error if found.
 //
 //
 //    called by:  Proshop_parms
 //
 //
 //************************************************************************

 public static boolean scanName(String name) {


    boolean error = false;

    char [] c = { ' ' };
    String cs = "";

    String space = " ";    // space char               x020
    String n1 = "0";       // low numeric              x030 -
    String n2 = "9";       // high numeric             x039
    String a1 = "A";       // first alpha char         x041 -
    String a2 = "Z";       // upper alpha end          x05A
    String a3 = "a";       // 2nd alpha lower limit    x061 -
    String a4 = "z";       // upper limit              x07A

    for(int i=0; i<name.length(); i++) {

       c[0] = name.charAt(i);
       cs = new String ( c );

       if (cs.compareTo( space ) < 0) {                             // if less than a space char
          error = true;
       } else {
          if ((cs.compareTo( space ) > 0) && (cs.compareTo( n1 ) < 0)) {   // if > space char & < 0
             error = true;
          } else {
             if ((cs.compareTo( n2 ) > 0) && (cs.compareTo( a1 ) < 0)) {   // if > 9 & < A
                error = true;
             } else {
                if ((cs.compareTo( a2 ) > 0) && (cs.compareTo( a3 ) < 0)) {   // if > Z & < a
                   error = true;
                } else {
                   if (cs.compareTo( a4 ) > 0) {                         // if > z
                      error = true;
                   }
                }
             }
          }
       }
    }         // check all characters in name

    return(error);

 }  // end of scanName


 //************************************************************************
 //  scanQuote - Searches the string provided for a single quote and
 //              returns an error if found.
 //
 //
 //    called by:  Proshop_mrest, etc. (configs)
 //
 //
 //************************************************************************

 public static boolean scanQuote(String name) {


    boolean error = false;

    char [] c = { ' ' };
    String cs = "";

    String quote = "'";    // single quote char

    for(int i=0; i<name.length(); i++) {

       c[0] = name.charAt(i);
       cs = new String ( c );

       if (cs.equals( quote )) {                             // if quote
          error = true;
       }
    }         // check all characters in name
    return(error);

 }  // end of scanName


 //************************************************************************
 // updateTeecurr - Scans the block, event, restriction & lottery tables for
 //                 new/modified entries and updates teecurr2 if necessary.
 //
 //
 //   called by:  buildTee above after new tee times have been built
 //
 //************************************************************************

 public static void updateTeecurr(Connection con) {


   //
   //  check the restriction tables
   //
   doRests(con);

   //
   //  check the 5-Some tables
   //
   doFives(con);

   //
   //  check the Event tables
   //
   doEvents(con);

   //
   //  check the Blocker tables
   //
   doBlockers(con);

   //
   //  check the Lottery tables
   //
   doLotteries(con);

 }  // end of updateTecurr


 //************************************************************************
 // doRests - Scans the restriction tables for
 //           new/modified entries and updates teecurr2 if necessary.
 //
 //
 //   called by:  Proshop_updateTeecurr
 //
 //************************************************************************

 public static void doRests(Connection con) {


   Statement stmt = null;
   ResultSet rs = null;

   int rest_id = 0;

   //
   //****************************************************************
   //  Get all the restrictions currently in the restriction table
   //****************************************************************
   //
   try {

      stmt = con.createStatement();
      rs = stmt.executeQuery("" +
              "SELECT id " +
              "FROM restriction2 " +
              "WHERE edate >= DATE_FORMAT(now(), '%Y%m%d')");

      while (rs.next()) {

         rest_id = rs.getInt(1);
         do1Rest(con, rest_id);

      }

   } catch (Exception exc) {

      logError("Error in SystemUtils.doRests(): rest_id=" + rest_id + ", Err=" + exc.getMessage());

   } finally {

      try { rs.close(); }
      catch (Exception ignore) {}

      try { stmt.close(); }
      catch (Exception ignore) {}

   }  // end of try

 }  // end of doRests


 //************************************************************************
 // do1Rest - Update teecurr for the restriction
 //
 //
 //   called by:  Proshop_addmrest
 //               Proshop_editmrest
 //               doEvents (above)
 //
 //************************************************************************

 public static void do1Rest(Connection con, int rest_id) {


   PreparedStatement pstmt1 = null;
   ResultSet rs = null;


   long sdate = 0;
   long edate = 0;

   int stime = 0;
   int etime = 0;
   int count = 0;
   int fb = 0;
   int activity_id = 0;

   String name = "";
   String recurr = "";
   String day = "";
   String def = "default";
   String courseName = "";
   String sfb = "";
   String omit = "";
   String color = "";
   String locations_csv = "";

   //
   //****************************************************************
   //  Get the restriction named and process teecurr
   //****************************************************************
   //
   try {

      pstmt1 = con.prepareStatement (
              "SELECT name, activity_id, locations, sdate, stime, edate, etime, recurr, color, courseName, fb FROM restriction2 WHERE id = ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setInt(1, rest_id);       // put the parm in stmt
      rs = pstmt1.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         activity_id = rs.getInt("activity_id");
         locations_csv = rs.getString("locations");
         name = rs.getString("name");
         sdate = rs.getLong("sdate");
         stime = rs.getInt("stime");
         edate = rs.getLong("edate");
         etime = rs.getInt("etime");
         recurr = rs.getString("recurr");
         color = rs.getString("color");
         courseName = rs.getString("courseName");
         sfb = rs.getString("fb");

         if (activity_id == 0) {        // if GOLF

            day = "";    // init day value

            //
            //  Put restriction info in teecurr2 slots if matching date/time and restriction not already there
            //  (this is done based on recurrence sepcified in restriction)
            //
            if ( recurr.equalsIgnoreCase( "every sunday" ) ) {   // if every Sunday

               day = "Sunday";
            }

            if ( recurr.equalsIgnoreCase( "every monday" ) ) {   // if every Monday

               day = "Monday";
            }

            if ( recurr.equalsIgnoreCase( "every tuesday" ) ) {   // if every Tuesday

               day = "Tuesday";
            }

            if ( recurr.equalsIgnoreCase( "every wednesday" ) ) {   // if every Wednesday

               day = "Wednesday";
            }

            if ( recurr.equalsIgnoreCase( "every thursday" ) ) {   // if every Thursday

               day = "Thursday";
            }

            if ( recurr.equalsIgnoreCase( "every friday" ) ) {   // if every Friday

               day = "Friday";
            }

            if ( recurr.equalsIgnoreCase( "every saturday" ) ) {   // if every Satruday

               day = "Saturday";
            }

            //
            //  prepare F/B indicator
            //
            fb = 0;                       // init to Front

            if (sfb.equals( "Back" )) {

               fb = 1;     // back
            }

            //
            //   courseName = '-ALL-', or name of course, or null
            //
            //   sfb = 'Front', 'Back', or 'Both'
            //
            if (courseName.equals( "-ALL-" ) || courseName.equals( "" )) {    // if ALL Courses or none

               if (!day.equals( "" )) {    // if an individual day

                  try {

                     PreparedStatement pstmt2 = con.prepareStatement (
                       "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                       "date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                       "time <= ? AND restriction = ? AND rest_color = ? ");

                     PreparedStatement pstmt5 = con.prepareStatement (
                       "UPDATE teecurr2 SET restriction = ?, rest_color = ? " +
                       "WHERE ((date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                       "time <= ?) AND (rest_color = ? OR rest_color = ?))");

                     PreparedStatement pstmt2b = con.prepareStatement (
                       "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                       "date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                       "time <= ? AND restriction = ? AND rest_color = ? AND fb = ?");

                     PreparedStatement pstmt5b = con.prepareStatement (
                       "UPDATE teecurr2 SET restriction = ?, rest_color = ? " +
                       "WHERE ((date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                       "time <= ? AND fb = ?) AND (rest_color = ? OR rest_color = ?))");

                     //
                     //   If restriction color is not 'default' and a rest exists in teecurr2 that is default, override
                     //
                     if (color.equalsIgnoreCase("default")) {    // if default color - only add if no other rest

                        if (sfb.equals( "Both" )) {

                           pstmt2.clearParameters();               // clear the parms
                           pstmt2.setString(1, name);              // put the parms in stmt
                           pstmt2.setString(2, color);
                           pstmt2.setLong(3, sdate);
                           pstmt2.setLong(4, edate);
                           pstmt2.setString(5, day);
                           pstmt2.setInt(6, stime);
                           pstmt2.setInt(7, etime);
                           pstmt2.setString(8, omit);
                           pstmt2.setString(9, omit);
                           count = pstmt2.executeUpdate();         // execute the prepared stmt

                        } else {

                           pstmt2b.clearParameters();               // clear the parms
                           pstmt2b.setString(1, name);              // put the parms in stmt
                           pstmt2b.setString(2, color);
                           pstmt2b.setLong(3, sdate);
                           pstmt2b.setLong(4, edate);
                           pstmt2b.setString(5, day);
                           pstmt2b.setInt(6, stime);
                           pstmt2b.setInt(7, etime);
                           pstmt2b.setString(8, omit);
                           pstmt2b.setString(9, omit);
                           pstmt2b.setInt(10, fb);
                           count = pstmt2b.executeUpdate();         // execute the prepared stmt
                        }

                     } else {   // color not default

                        if (sfb.equals( "Both" )) {

                           pstmt5.clearParameters();               // clear the parms
                           pstmt5.setString(1, name);              // put the parms in stmt
                           pstmt5.setString(2, color);
                           pstmt5.setLong(3, sdate);
                           pstmt5.setLong(4, edate);
                           pstmt5.setString(5, day);
                           pstmt5.setInt(6, stime);
                           pstmt5.setInt(7, etime);
                           pstmt5.setString(8, def);               // replace if existing color is default
                           pstmt5.setString(9, omit);              // or null
                           count = pstmt5.executeUpdate();         // execute the prepared stmt

                        } else {

                           pstmt5b.clearParameters();               // clear the parms
                           pstmt5b.setString(1, name);              // put the parms in stmt
                           pstmt5b.setString(2, color);
                           pstmt5b.setLong(3, sdate);
                           pstmt5b.setLong(4, edate);
                           pstmt5b.setString(5, day);
                           pstmt5b.setInt(6, stime);
                           pstmt5b.setInt(7, etime);
                           pstmt5b.setInt(8, fb);
                           pstmt5b.setString(9, def);               // replace if existing color is defualt
                           pstmt5b.setString(10, omit);             // or null
                           count = pstmt5b.executeUpdate();         // execute the prepared stmt
                        }
                     }

                     pstmt2.close();
                     pstmt5.close();
                     pstmt2b.close();
                     pstmt5b.close();

                  } catch (Exception e2) {

                      logError("Error1 in SystemUtils do1Rest: " + e2.getMessage());

                  }

               }  // end of IF individual day

               if ( recurr.equalsIgnoreCase( "every day" )) {   // if everyday

                  try {

                     //
                     //   If restriction color is not 'default' and a rest exists in teecurr2 that is default, override
                     //
                     if (color.equalsIgnoreCase("default")) {    // if default color - only add if no other rest

                        if (sfb.equals( "Both" )) {

                           PreparedStatement pstmt1a = con.prepareStatement (
                             "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                             "date >= ? AND date <= ? AND time >= ? AND " +
                             "time <= ? AND restriction = ? AND rest_color = ? ");

                           pstmt1a.clearParameters();               // clear the parms
                           pstmt1a.setString(1, name);              // put the parms in stmt
                           pstmt1a.setString(2, color);
                           pstmt1a.setLong(3, sdate);
                           pstmt1a.setLong(4, edate);
                           pstmt1a.setInt(5, stime);
                           pstmt1a.setInt(6, etime);
                           pstmt1a.setString(7, omit);
                           pstmt1a.setString(8, omit);
                           count = pstmt1a.executeUpdate();         // execute the prepared stmt

                           pstmt1a.close();

                        } else {

                           PreparedStatement pstmt1b = con.prepareStatement (
                             "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                             "date >= ? AND date <= ? AND time >= ? AND " +
                             "time <= ? AND restriction = ? AND rest_color = ? AND fb = ?");

                           pstmt1b.clearParameters();               // clear the parms
                           pstmt1b.setString(1, name);              // put the parms in stmt
                           pstmt1b.setString(2, color);
                           pstmt1b.setLong(3, sdate);
                           pstmt1b.setLong(4, edate);
                           pstmt1b.setInt(5, stime);
                           pstmt1b.setInt(6, etime);
                           pstmt1b.setString(7, omit);
                           pstmt1b.setString(8, omit);
                           pstmt1b.setInt(9, fb);
                           count = pstmt1b.executeUpdate();         // execute the prepared stmt

                           pstmt1b.close();
                        }

                     } else {   // rest color not default

                        if (sfb.equals( "Both" )) {

                           PreparedStatement pstmt1a = con.prepareStatement (
                             "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                             "((date >= ? AND date <= ? AND time >= ? AND " +
                             "time <= ?) AND (rest_color = ? OR rest_color = ?))");

                           pstmt1a.clearParameters();               // clear the parms
                           pstmt1a.setString(1, name);              // put the parms in stmt
                           pstmt1a.setString(2, color);
                           pstmt1a.setLong(3, sdate);
                           pstmt1a.setLong(4, edate);
                           pstmt1a.setInt(5, stime);
                           pstmt1a.setInt(6, etime);
                           pstmt1a.setString(7, def);
                           pstmt1a.setString(8, omit);
                           count = pstmt1a.executeUpdate();         // execute the prepared stmt

                           pstmt1a.close();

                        } else {

                           PreparedStatement pstmt1b = con.prepareStatement (
                             "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                             "((date >= ? AND date <= ? AND time >= ? AND " +
                             "time <= ? AND fb = ?) AND (rest_color = ? OR rest_color = ?))");

                           pstmt1b.clearParameters();               // clear the parms
                           pstmt1b.setString(1, name);              // put the parms in stmt
                           pstmt1b.setString(2, color);
                           pstmt1b.setLong(3, sdate);
                           pstmt1b.setLong(4, edate);
                           pstmt1b.setInt(5, stime);
                           pstmt1b.setInt(6, etime);
                           pstmt1b.setInt(7, fb);
                           pstmt1b.setString(8, def);
                           pstmt1b.setString(9, omit);
                           count = pstmt1b.executeUpdate();         // execute the prepared stmt

                           pstmt1b.close();
                        }
                     }

                  } catch (Exception e2) {

                      logError("Error2 in SystemUtils do1Rest: " + e2.getMessage());

                  }

               }  // end of IF every day

               if ( recurr.equalsIgnoreCase( "all weekdays" ) ) {   // if Monday through Friday

                  try {

                     //
                     //   If restriction color is not 'default' and a rest exists in teecurr2 that is default, override
                     //
                     if (color.equalsIgnoreCase("default")) {    // if default color - only add if no other rest

                        if (sfb.equals( "Both" )) {

                           PreparedStatement pstmt3 = con.prepareStatement (
                             "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                             "date >= ? AND date <= ? AND day != 'Saturday'  " +
                             "AND day != 'Sunday' AND time >= ? AND " +
                             "time <= ? AND restriction = ? AND rest_color = ? ");

                           pstmt3.clearParameters();               // clear the parms
                           pstmt3.setString(1, name);              // put the parms in stmt
                           pstmt3.setString(2, color);
                           pstmt3.setLong(3, sdate);
                           pstmt3.setLong(4, edate);
                           pstmt3.setInt(5, stime);
                           pstmt3.setInt(6, etime);
                           pstmt3.setString(7, omit);
                           pstmt3.setString(8, omit);
                           count = pstmt3.executeUpdate();         // execute the prepared stmt

                           pstmt3.close();

                        } else {

                           PreparedStatement pstmt3b = con.prepareStatement (
                             "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                             "date >= ? AND date <= ? AND day != 'Saturday'  " +
                             "AND day != 'Sunday' AND time >= ? AND " +
                             "time <= ? AND restriction = ? AND rest_color = ? AND fb = ?");

                           pstmt3b.clearParameters();               // clear the parms
                           pstmt3b.setString(1, name);              // put the parms in stmt
                           pstmt3b.setString(2, color);
                           pstmt3b.setLong(3, sdate);
                           pstmt3b.setLong(4, edate);
                           pstmt3b.setInt(5, stime);
                           pstmt3b.setInt(6, etime);
                           pstmt3b.setString(7, omit);
                           pstmt3b.setString(8, omit);
                           pstmt3b.setInt(9, fb);
                           count = pstmt3b.executeUpdate();         // execute the prepared stmt

                           pstmt3b.close();
                        }

                     } else {    // rest color is not default

                        if (sfb.equals( "Both" )) {

                           PreparedStatement pstmt3 = con.prepareStatement (
                             "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                             "((date >= ? AND date <= ? AND day != 'Saturday'  " +
                             "AND day != 'Sunday' AND time >= ? AND " +
                             "time <= ?) AND (rest_color = ? OR rest_color = ?))");

                           pstmt3.clearParameters();               // clear the parms
                           pstmt3.setString(1, name);              // put the parms in stmt
                           pstmt3.setString(2, color);
                           pstmt3.setLong(3, sdate);
                           pstmt3.setLong(4, edate);
                           pstmt3.setInt(5, stime);
                           pstmt3.setInt(6, etime);
                           pstmt3.setString(7, def);
                           pstmt3.setString(8, omit);
                           count = pstmt3.executeUpdate();         // execute the prepared stmt

                           pstmt3.close();

                        } else {

                           PreparedStatement pstmt3b = con.prepareStatement (
                             "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                             "((date >= ? AND date <= ? AND day != 'Saturday'  " +
                             "AND day != 'Sunday' AND time >= ? AND " +
                             "time <= ? AND fb = ?) AND (rest_color = ? OR rest_color = ?))");

                           pstmt3b.clearParameters();               // clear the parms
                           pstmt3b.setString(1, name);              // put the parms in stmt
                           pstmt3b.setString(2, color);
                           pstmt3b.setLong(3, sdate);
                           pstmt3b.setLong(4, edate);
                           pstmt3b.setInt(5, stime);
                           pstmt3b.setInt(6, etime);
                           pstmt3b.setInt(7, fb);
                           pstmt3b.setString(8, def);
                           pstmt3b.setString(9, omit);
                           count = pstmt3b.executeUpdate();         // execute the prepared stmt

                           pstmt3b.close();
                        }
                     }

                  } catch (Exception e2) {

                      logError("Error3 in SystemUtils do1Rest: " + e2.getMessage());

                  }

               }  // end of IF all weekdays

               if ( recurr.equalsIgnoreCase( "all weekends" ) ) {   // if Saturday & Sunday

                  try {

                     //
                     //   If restriction color is not 'default' and a rest exists in teecurr2 that is default, override
                     //
                     if (color.equalsIgnoreCase("default")) {    // if default color - only add if no other rest

                        if (sfb.equals( "Both" )) {

                           PreparedStatement pstmt4 = con.prepareStatement (
                             "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                             "date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                             "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                             "AND time >= ? AND time <= ? AND " +
                             "restriction = ? AND rest_color = ? ");

                           pstmt4.clearParameters();               // clear the parms
                           pstmt4.setString(1, name);              // put the parms in stmt
                           pstmt4.setString(2, color);
                           pstmt4.setLong(3, sdate);
                           pstmt4.setLong(4, edate);
                           pstmt4.setInt(5, stime);
                           pstmt4.setInt(6, etime);
                           pstmt4.setString(7, omit);
                           pstmt4.setString(8, omit);
                           count = pstmt4.executeUpdate();         // execute the prepared stmt

                           pstmt4.close();

                        } else {

                           PreparedStatement pstmt4b = con.prepareStatement (
                             "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                             "date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                             "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                             "AND time >= ? AND time <= ? AND " +
                             "restriction = ? AND rest_color = ? AND fb = ?");

                           pstmt4b.clearParameters();               // clear the parms
                           pstmt4b.setString(1, name);              // put the parms in stmt
                           pstmt4b.setString(2, color);
                           pstmt4b.setLong(3, sdate);
                           pstmt4b.setLong(4, edate);
                           pstmt4b.setInt(5, stime);
                           pstmt4b.setInt(6, etime);
                           pstmt4b.setString(7, omit);
                           pstmt4b.setString(8, omit);
                           pstmt4b.setInt(9, fb);
                           count = pstmt4b.executeUpdate();         // execute the prepared stmt

                           pstmt4b.close();
                        }

                     } else {     // rest color is not default

                        if (sfb.equals( "Both" )) {

                           PreparedStatement pstmt4 = con.prepareStatement (
                             "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                             "((date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                             "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                             "AND time >= ? AND time <= ?) AND " +
                             "(rest_color = ? OR rest_color = ?))");

                           pstmt4.clearParameters();               // clear the parms
                           pstmt4.setString(1, name);              // put the parms in stmt
                           pstmt4.setString(2, color);
                           pstmt4.setLong(3, sdate);
                           pstmt4.setLong(4, edate);
                           pstmt4.setInt(5, stime);
                           pstmt4.setInt(6, etime);
                           pstmt4.setString(7, def);
                           pstmt4.setString(8, omit);
                           count = pstmt4.executeUpdate();         // execute the prepared stmt

                           pstmt4.close();

                        } else {

                           PreparedStatement pstmt4b = con.prepareStatement (
                             "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                             "((date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                             "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                             "AND time >= ? AND time <= ? AND fb = ?) AND " +
                             "(rest_color = ? OR rest_color = ?))");

                           pstmt4b.clearParameters();               // clear the parms
                           pstmt4b.setString(1, name);              // put the parms in stmt
                           pstmt4b.setString(2, color);
                           pstmt4b.setLong(3, sdate);
                           pstmt4b.setLong(4, edate);
                           pstmt4b.setInt(5, stime);
                           pstmt4b.setInt(6, etime);
                           pstmt4b.setInt(7, fb);
                           pstmt4b.setString(8, def);
                           pstmt4b.setString(9, omit);
                           count = pstmt4b.executeUpdate();         // execute the prepared stmt

                           pstmt4b.close();
                        }
                     }

                  } catch (Exception e2) {

                      logError("Error4 in SystemUtils do1Rest: " + e2.getMessage());

                  }

               }  // end of IF all weekends

            } else {                       // a specific courseName

               if (!day.equals( "" )) {    // if an individual day

                  try {

                     PreparedStatement pstmt2 = con.prepareStatement (
                       "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                       "date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                       "time <= ? AND restriction = ? AND rest_color = ? AND courseName = ?");

                     PreparedStatement pstmt5 = con.prepareStatement (
                       "UPDATE teecurr2 SET restriction = ?, rest_color = ? " +
                       "WHERE ((date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                       "time <= ? AND courseName = ?) AND (rest_color = ? OR rest_color = ?))");

                     PreparedStatement pstmt2b = con.prepareStatement (
                       "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                       "date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                       "time <= ? AND restriction = ? AND rest_color = ? AND courseName = ? AND fb = ?");

                     PreparedStatement pstmt5b = con.prepareStatement (
                       "UPDATE teecurr2 SET restriction = ?, rest_color = ? " +
                       "WHERE ((date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                       "time <= ? AND courseName = ? AND fb = ?) AND (rest_color = ? OR rest_color = ?))");

                     //
                     //   If restriction color is not 'default' and a rest exists in teecurr2 that is default, override
                     //
                     if (color.equalsIgnoreCase("default")) {    // if default color - only add if no other rest

                        if (sfb.equals( "Both" )) {

                           pstmt2.clearParameters();               // clear the parms
                           pstmt2.setString(1, name);              // put the parms in stmt
                           pstmt2.setString(2, color);
                           pstmt2.setLong(3, sdate);
                           pstmt2.setLong(4, edate);
                           pstmt2.setString(5, day);
                           pstmt2.setInt(6, stime);
                           pstmt2.setInt(7, etime);
                           pstmt2.setString(8, omit);
                           pstmt2.setString(9, omit);
                           pstmt2.setString(10, courseName);
                           count = pstmt2.executeUpdate();         // execute the prepared stmt

                        } else {

                           pstmt2b.clearParameters();               // clear the parms
                           pstmt2b.setString(1, name);              // put the parms in stmt
                           pstmt2b.setString(2, color);
                           pstmt2b.setLong(3, sdate);
                           pstmt2b.setLong(4, edate);
                           pstmt2b.setString(5, day);
                           pstmt2b.setInt(6, stime);
                           pstmt2b.setInt(7, etime);
                           pstmt2b.setString(8, omit);
                           pstmt2b.setString(9, omit);
                           pstmt2b.setString(10, courseName);
                           pstmt2b.setInt(11, fb);
                           count = pstmt2b.executeUpdate();         // execute the prepared stmt
                        }

                     } else {   // color not default

                        if (sfb.equals( "Both" )) {

                           pstmt5.clearParameters();               // clear the parms
                           pstmt5.setString(1, name);              // put the parms in stmt
                           pstmt5.setString(2, color);
                           pstmt5.setLong(3, sdate);
                           pstmt5.setLong(4, edate);
                           pstmt5.setString(5, day);
                           pstmt5.setInt(6, stime);
                           pstmt5.setInt(7, etime);
                           pstmt5.setString(8, courseName);
                           pstmt5.setString(9, def);               // replace if existing color is default
                           pstmt5.setString(10, omit);              // or null
                           count = pstmt5.executeUpdate();         // execute the prepared stmt

                        } else {

                           pstmt5b.clearParameters();               // clear the parms
                           pstmt5b.setString(1, name);              // put the parms in stmt
                           pstmt5b.setString(2, color);
                           pstmt5b.setLong(3, sdate);
                           pstmt5b.setLong(4, edate);
                           pstmt5b.setString(5, day);
                           pstmt5b.setInt(6, stime);
                           pstmt5b.setInt(7, etime);
                           pstmt5b.setString(8, courseName);
                           pstmt5b.setInt(9, fb);
                           pstmt5b.setString(10, def);               // replace if existing color is defualt
                           pstmt5b.setString(11, omit);              // or null
                           count = pstmt5b.executeUpdate();          // execute the prepared stmt
                        }
                     }

                     pstmt2.close();
                     pstmt5.close();
                     pstmt2b.close();
                     pstmt5b.close();

                  } catch (Exception e2) {

                      logError("Error5 in SystemUtils do1Rest: " + e2.getMessage());                                       // log it

                  }

               }  // end of IF individual day

               if ( recurr.equalsIgnoreCase( "every day" )) {   // if everyday

                  try {

                     //
                     //   If restriction color is not 'default' and a rest exists in teecurr2 that is default, override
                     //
                     if (color.equalsIgnoreCase("default")) {    // if default color - only add if no other rest

                        if (sfb.equals( "Both" )) {

                           PreparedStatement pstmt1a = con.prepareStatement (
                             "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                             "date >= ? AND date <= ? AND time >= ? AND " +
                             "time <= ? AND restriction = ? AND rest_color = ? AND courseName = ? ");

                           pstmt1a.clearParameters();               // clear the parms
                           pstmt1a.setString(1, name);              // put the parms in stmt
                           pstmt1a.setString(2, color);
                           pstmt1a.setLong(3, sdate);
                           pstmt1a.setLong(4, edate);
                           pstmt1a.setInt(5, stime);
                           pstmt1a.setInt(6, etime);
                           pstmt1a.setString(7, omit);
                           pstmt1a.setString(8, omit);
                           pstmt1a.setString(9, courseName);
                           count = pstmt1a.executeUpdate();         // execute the prepared stmt

                           pstmt1a.close();

                        } else {

                           PreparedStatement pstmt1b = con.prepareStatement (
                             "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                             "date >= ? AND date <= ? AND time >= ? AND " +
                             "time <= ? AND restriction = ? AND rest_color = ? AND courseName = ? AND fb = ?");

                           pstmt1b.clearParameters();               // clear the parms
                           pstmt1b.setString(1, name);              // put the parms in stmt
                           pstmt1b.setString(2, color);
                           pstmt1b.setLong(3, sdate);
                           pstmt1b.setLong(4, edate);
                           pstmt1b.setInt(5, stime);
                           pstmt1b.setInt(6, etime);
                           pstmt1b.setString(7, omit);
                           pstmt1b.setString(8, omit);
                           pstmt1b.setString(9, courseName);
                           pstmt1b.setInt(10, fb);
                           count = pstmt1b.executeUpdate();         // execute the prepared stmt

                           pstmt1b.close();
                        }

                     } else {   // rest color not default

                        if (sfb.equals( "Both" )) {

                           PreparedStatement pstmt1a = con.prepareStatement (
                             "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                             "((date >= ? AND date <= ? AND time >= ? AND " +
                             "time <= ? AND courseName = ?) AND (rest_color = ? OR rest_color = ?))");

                           pstmt1a.clearParameters();               // clear the parms
                           pstmt1a.setString(1, name);              // put the parms in stmt
                           pstmt1a.setString(2, color);
                           pstmt1a.setLong(3, sdate);
                           pstmt1a.setLong(4, edate);
                           pstmt1a.setInt(5, stime);
                           pstmt1a.setInt(6, etime);
                           pstmt1a.setString(7, courseName);
                           pstmt1a.setString(8, def);
                           pstmt1a.setString(9, omit);
                           count = pstmt1a.executeUpdate();         // execute the prepared stmt

                           pstmt1a.close();

                        } else {

                           PreparedStatement pstmt1b = con.prepareStatement (
                             "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                             "((date >= ? AND date <= ? AND time >= ? AND " +
                             "time <= ? AND courseName = ? AND fb = ?) AND (rest_color = ? OR rest_color = ?))");

                           pstmt1b.clearParameters();               // clear the parms
                           pstmt1b.setString(1, name);              // put the parms in stmt
                           pstmt1b.setString(2, color);
                           pstmt1b.setLong(3, sdate);
                           pstmt1b.setLong(4, edate);
                           pstmt1b.setInt(5, stime);
                           pstmt1b.setInt(6, etime);
                           pstmt1b.setString(7, courseName);
                           pstmt1b.setInt(8, fb);
                           pstmt1b.setString(9, def);
                           pstmt1b.setString(10, omit);
                           count = pstmt1b.executeUpdate();         // execute the prepared stmt

                           pstmt1b.close();
                        }
                     }

                  } catch (Exception e2) {

                     logError("Error6 in SystemUtils do1Rest: " + e2.getMessage());
                  }

               }  // end of IF every day

               if ( recurr.equalsIgnoreCase( "all weekdays" ) ) {   // if Monday through Friday

                  try {

                     //
                     //   If restriction color is not 'default' and a rest exists in teecurr2 that is default, override
                     //
                     if (color.equalsIgnoreCase("default")) {    // if default color - only add if no other rest

                        if (sfb.equals( "Both" )) {

                           PreparedStatement pstmt3 = con.prepareStatement (
                             "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                             "date >= ? AND date <= ? AND day != 'Saturday' " +
                             "AND day != 'Sunday' AND time >= ? AND " +
                             "time <= ? AND restriction = ? AND rest_color = ? AND courseName = ? ");

                           pstmt3.clearParameters();               // clear the parms
                           pstmt3.setString(1, name);              // put the parms in stmt
                           pstmt3.setString(2, color);
                           pstmt3.setLong(3, sdate);
                           pstmt3.setLong(4, edate);
                           pstmt3.setInt(5, stime);
                           pstmt3.setInt(6, etime);
                           pstmt3.setString(7, omit);
                           pstmt3.setString(8, omit);
                           pstmt3.setString(9, courseName);
                           count = pstmt3.executeUpdate();         // execute the prepared stmt

                           pstmt3.close();

                        } else {

                           PreparedStatement pstmt3b = con.prepareStatement (
                             "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                             "date >= ? AND date <= ? AND day != 'Saturday' " +
                             "AND day != 'Sunday' AND time >= ? AND " +
                             "time <= ? AND restriction = ? AND rest_color = ? AND courseName = ? AND fb = ?");

                           pstmt3b.clearParameters();               // clear the parms
                           pstmt3b.setString(1, name);              // put the parms in stmt
                           pstmt3b.setString(2, color);
                           pstmt3b.setLong(3, sdate);
                           pstmt3b.setLong(4, edate);
                           pstmt3b.setInt(5, stime);
                           pstmt3b.setInt(6, etime);
                           pstmt3b.setString(7, omit);
                           pstmt3b.setString(8, omit);
                           pstmt3b.setString(9, courseName);
                           pstmt3b.setInt(10, fb);
                           count = pstmt3b.executeUpdate();         // execute the prepared stmt

                           pstmt3b.close();
                        }

                     } else {    // rest color is not default

                        if (sfb.equals( "Both" )) {

                           PreparedStatement pstmt3 = con.prepareStatement (
                             "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                             "((date >= ? AND date <= ? AND day != 'Saturday' " +
                             "AND day != 'Sunday' AND time >= ? AND " +
                             "time <= ? AND courseName = ?) AND (rest_color = ? OR rest_color = ?))");

                           pstmt3.clearParameters();               // clear the parms
                           pstmt3.setString(1, name);              // put the parms in stmt
                           pstmt3.setString(2, color);
                           pstmt3.setLong(3, sdate);
                           pstmt3.setLong(4, edate);
                           pstmt3.setInt(5, stime);
                           pstmt3.setInt(6, etime);
                           pstmt3.setString(7, courseName);
                           pstmt3.setString(8, def);
                           pstmt3.setString(9, omit);
                           count = pstmt3.executeUpdate();         // execute the prepared stmt

                           pstmt3.close();

                        } else {

                           PreparedStatement pstmt3b = con.prepareStatement (
                             "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                             "((date >= ? AND date <= ? AND day != 'Saturday' " +
                             "AND day != 'Sunday' AND time >= ? AND " +
                             "time <= ? AND courseName = ? AND fb = ?) AND (rest_color = ? OR rest_color = ?))");

                           pstmt3b.clearParameters();               // clear the parms
                           pstmt3b.setString(1, name);              // put the parms in stmt
                           pstmt3b.setString(2, color);
                           pstmt3b.setLong(3, sdate);
                           pstmt3b.setLong(4, edate);
                           pstmt3b.setInt(5, stime);
                           pstmt3b.setInt(6, etime);
                           pstmt3b.setString(7, courseName);
                           pstmt3b.setInt(8, fb);
                           pstmt3b.setString(9, def);
                           pstmt3b.setString(10, omit);
                           count = pstmt3b.executeUpdate();         // execute the prepared stmt

                           pstmt3b.close();
                        }
                     }

                  } catch (Exception e2) {

                     logError("Error7 in SystemUtils do1Rest: " + e2.getMessage());

                  }

               }  // end of IF all weekdays

               if ( recurr.equalsIgnoreCase( "all weekends" ) ) {   // if Saturday & Sunday

                  try {

                     //
                     //   If restriction color is not 'default' and a rest exists in teecurr2 that is default, override
                     //
                     if (color.equalsIgnoreCase("default")) {    // if default color - only add if no other rest

                        if (sfb.equals( "Both" )) {

                           PreparedStatement pstmt4 = con.prepareStatement (
                             "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                             "date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                             "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                             "AND time >= ? AND time <= ? AND " +
                             "restriction = ? AND rest_color = ? AND courseName = ? ");

                           pstmt4.clearParameters();               // clear the parms
                           pstmt4.setString(1, name);              // put the parms in stmt
                           pstmt4.setString(2, color);
                           pstmt4.setLong(3, sdate);
                           pstmt4.setLong(4, edate);
                           pstmt4.setInt(5, stime);
                           pstmt4.setInt(6, etime);
                           pstmt4.setString(7, omit);
                           pstmt4.setString(8, omit);
                           pstmt4.setString(9, courseName);
                           count = pstmt4.executeUpdate();         // execute the prepared stmt

                           pstmt4.close();

                        } else {

                           PreparedStatement pstmt4b = con.prepareStatement (
                             "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                             "date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                             "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                             "AND time >= ? AND time <= ? AND " +
                             "restriction = ? AND rest_color = ? AND courseName = ? AND fb = ?");

                           pstmt4b.clearParameters();               // clear the parms
                           pstmt4b.setString(1, name);              // put the parms in stmt
                           pstmt4b.setString(2, color);
                           pstmt4b.setLong(3, sdate);
                           pstmt4b.setLong(4, edate);
                           pstmt4b.setInt(5, stime);
                           pstmt4b.setInt(6, etime);
                           pstmt4b.setString(7, omit);
                           pstmt4b.setString(8, omit);
                           pstmt4b.setString(9, courseName);
                           pstmt4b.setInt(10, fb);
                           count = pstmt4b.executeUpdate();         // execute the prepared stmt

                           pstmt4b.close();
                        }

                     } else {     // rest color is not default

                        if (sfb.equals( "Both" )) {

                           PreparedStatement pstmt4 = con.prepareStatement (
                             "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                             "((date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                             "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                             "AND time >= ? AND time <= ? AND courseName = ?) AND " +
                             "(rest_color = ? OR rest_color = ?))");

                           pstmt4.clearParameters();               // clear the parms
                           pstmt4.setString(1, name);              // put the parms in stmt
                           pstmt4.setString(2, color);
                           pstmt4.setLong(3, sdate);
                           pstmt4.setLong(4, edate);
                           pstmt4.setInt(5, stime);
                           pstmt4.setInt(6, etime);
                           pstmt4.setString(7, courseName);
                           pstmt4.setString(8, def);
                           pstmt4.setString(9, omit);
                           count = pstmt4.executeUpdate();         // execute the prepared stmt

                           pstmt4.close();

                        } else {

                           PreparedStatement pstmt4b = con.prepareStatement (
                             "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                             "((date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                             "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                             "AND time >= ? AND time <= ? AND courseName = ? AND fb = ?) AND " +
                             "(rest_color = ? OR rest_color = ?))");

                           pstmt4b.clearParameters();               // clear the parms
                           pstmt4b.setString(1, name);              // put the parms in stmt
                           pstmt4b.setString(2, color);
                           pstmt4b.setLong(3, sdate);
                           pstmt4b.setLong(4, edate);
                           pstmt4b.setInt(5, stime);
                           pstmt4b.setInt(6, etime);
                           pstmt4b.setString(7, courseName);
                           pstmt4b.setInt(8, fb);
                           pstmt4b.setString(9, def);
                           pstmt4b.setString(10, omit);
                           count = pstmt4b.executeUpdate();         // execute the prepared stmt

                           pstmt4b.close();
                        }
                     }

                  } catch (Exception e2) {

                      logError("Error8 in SystemUtils do1Rest: " + e2.getMessage());                                       // log it
                  }

               }  // end of IF all weekends

            }  // end of IF courseName=ALL

         } else {


            //
            //************************************************
            //  For an ACTIVITY
            //************************************************
            //
            //  set rest_id (int) in all appropriate activity sheets
            //
            String day_clause = "";
            //String in = getActivity.buildInString(activity_id, 1, con);

            String sql = "UPDATE activity_sheets SET rest_id = ? WHERE activity_id IN (" + locations_csv + ") AND rest_id = 0 AND " +
                         "DATE_FORMAT(date_time, '%Y%m%d') >= ? AND DATE_FORMAT(date_time, '%Y%m%d') <= ? AND " +
                         "DATE_FORMAT(date_time, '%H%i') >= ? AND DATE_FORMAT(date_time, '%H%i') <= ? ";


             // setup the day name part of the where clause based upon the recurr for this blocker
             if ( recurr.equalsIgnoreCase( "every sunday" ) ) {

                 day_clause = "AND DATE_FORMAT(date_time, '%W') = 'Sunday'";

             } else if ( recurr.equalsIgnoreCase( "every monday" ) ) {

                 day_clause = "AND DATE_FORMAT(date_time, '%W') = 'Monday'";

             } else if ( recurr.equalsIgnoreCase( "every tuesday" ) ) {

                 day_clause = "AND DATE_FORMAT(date_time, '%W') = 'Tuesday'";

             } else if ( recurr.equalsIgnoreCase( "every wednesday" ) ) {

                 day_clause = "AND DATE_FORMAT(date_time, '%W') = 'Wednesday'";

             } else if ( recurr.equalsIgnoreCase( "every thursday" ) ) {

                 day_clause = "AND DATE_FORMAT(date_time, '%W') = 'Thursday'";

             } else if ( recurr.equalsIgnoreCase( "every friday" ) ) {

                 day_clause = "AND DATE_FORMAT(date_time, '%W') = 'Friday'";

             } else if ( recurr.equalsIgnoreCase( "every saturday" ) ) {

                 day_clause = "AND DATE_FORMAT(date_time, '%W') = 'Saturday'";

             } else if ( recurr.equalsIgnoreCase( "all weekdays" ) ) {

                 day_clause = "AND (DATE_FORMAT(date_time, '%W') <> 'Saturday' AND DATE_FORMAT(date_time, '%W') <> 'Sunday')";

             } else if ( recurr.equalsIgnoreCase( "all weekends" ) ) {

                 day_clause = "AND (DATE_FORMAT(date_time, '%W') = 'Saturday' OR DATE_FORMAT(date_time, '%W') = 'Sunday')";

             }

             // append the day clause
             sql = sql + day_clause;

             try {

                 pstmt1 = con.prepareStatement ( sql );

                 pstmt1.clearParameters();
                 pstmt1.setInt(1, rest_id);
                 pstmt1.setLong(2, sdate);
                 pstmt1.setLong(3, edate);
                 pstmt1.setInt(4, stime);
                 pstmt1.setInt(5, etime);
                 pstmt1.executeUpdate();

             } catch (Exception exc) {

                 logError("Error in SystemUtils.do1Rest: rest_id=" + rest_id + ", sql="+sql+", err=" + exc.toString());

             } finally {

                 try { pstmt1.close(); }
                 catch (Exception ignore) {}

             }

         }     // end of IF Golf or Activity

      }    // end of IF restriction found

      pstmt1.close();

   } catch (Exception e9) {

      logError("Error9 in SystemUtils do1Rest: " + e9.getMessage());

   }

 }  // end of do1Rest


 //************************************************************************
 // doFives - Scans the 5-some restriction tables for
 //           new/modified entries and updates teecurr2 if necessary.
 //
 //
 //   called by:  Proshop_updateTeecurr
 //
 //************************************************************************

 public static void doFives(Connection con) {


   Statement stmt = null;
   ResultSet rs = null;

   //
   //****************************************************************
   //  Get all the 5-some restrictions currently in the fives2 table
   //****************************************************************
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT name " +
                              "FROM fives2 " +
                              "WHERE edate >= DATE_FORMAT(now(), '%Y%m%d')");

      while ( rs.next() ) {

         //
         //  go process the single rest
         //
         do1Five(con, rs.getString(1));

      }

   } catch (Exception exc) {

       logError("Error18 in SystemUtils doFives: " + exc.getMessage());

   } finally {

       try { rs.close(); }
       catch (Exception ignore) {}

       try { stmt.close(); }
       catch (Exception ignore) {}
   }

 }  // end of doFives


 //************************************************************************
 // do1Five - Update teecurr for the 5-some restriction passed
 //
 //   called by:  Proshop_addfives
 //                      _editfives
 //               doFives (above)
 //
 //************************************************************************

 public static void do1Five(Connection con, String name) {


   Statement stmt = null;
   Statement stmt2 = null;
   ResultSet rs = null;


   long date = 0;                 // restriction table variables
   int stime = 0;
   int etime = 0;
   int type = 0;

   long sdate = 0;                 // restriction table variables
   long edate = 0;
   String recurr = "";
   String day = "";

   int count = 0;
   int fb = 0;

   String def = "default";

   String course = "";           // common variables
   String courseName = "";
   String sfb = "";
   String omit = "";
   String color = "";

   //
   //****************************************************************
   //  Get the 5-some restriction and process teecurr
   //****************************************************************
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
              "SELECT sdate, stime, edate, etime, recurr, color, courseName, fb FROM fives2 WHERE name = ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setString(1, name);       // put the parm in stmt
      rs = pstmt1.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         sdate = rs.getLong(1);
         stime = rs.getInt(2);
         edate = rs.getLong(3);
         etime = rs.getInt(4);
         recurr = rs.getString(5);
         color = rs.getString(6);
         courseName = rs.getString(7);
         sfb = rs.getString(8);

         day = "";    // init day value

         //
         //  Put restriction info in teecurr2 slots if matching date/time and restriction not already there
         //  (this is done based on recurrence sepcified in restriction)
         //
         if ( recurr.equalsIgnoreCase( "every sunday" ) ) {   // if every Sunday

            day = "Sunday";
         }

         if ( recurr.equalsIgnoreCase( "every monday" ) ) {   // if every Monday

            day = "Monday";
         }

         if ( recurr.equalsIgnoreCase( "every tuesday" ) ) {   // if every Tuesday

            day = "Tuesday";
         }

         if ( recurr.equalsIgnoreCase( "every wednesday" ) ) {   // if every Wednesday

            day = "Wednesday";
         }

         if ( recurr.equalsIgnoreCase( "every thursday" ) ) {   // if every Thursday

            day = "Thursday";
         }

         if ( recurr.equalsIgnoreCase( "every friday" ) ) {   // if every Friday

            day = "Friday";
         }

         if ( recurr.equalsIgnoreCase( "every saturday" ) ) {   // if every Satruday

            day = "Saturday";
         }

         //
         //  prepare F/B indicator
         //
         fb = 0;                       // init to Front

         if (sfb.equals( "Back" )) {

            fb = 1;     // back
         }

         //
         //   courseName = '-ALL-', or name of course, or null
         //
         //   sfb = 'Front', 'Back', or 'Both'
         //
         if (courseName.equals( "-ALL-" )) {    // if ALL Courses

            if (!day.equals( "" )) {    // if an individual day

               try {

                  PreparedStatement pstmt52 = con.prepareStatement (
                    "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND rest5 = ? AND rest5_color = ? ");

                  PreparedStatement pstmt55 = con.prepareStatement (
                    "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? " +
                    "WHERE ((date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ?) AND (rest5_color = ? OR rest5_color = ?))");

                  PreparedStatement pstmt52b = con.prepareStatement (
                    "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND rest5 = ? AND rest5_color = ? AND fb = ?");

                  PreparedStatement pstmt55b = con.prepareStatement (
                    "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? " +
                    "WHERE ((date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND fb = ?) AND (rest5_color = ? OR rest5_color = ?))");

                  //
                  //   If restriction color is not 'default' and a rest exists in teecurr2 that is default, override
                  //
                  if (color.equalsIgnoreCase("default")) {    // if default color - only add if no other rest

                     if (sfb.equals( "Both" )) {

                        pstmt52.clearParameters();               // clear the parms
                        pstmt52.setString(1, name);              // put the parms in stmt
                        pstmt52.setString(2, color);
                        pstmt52.setLong(3, sdate);
                        pstmt52.setLong(4, edate);
                        pstmt52.setString(5, day);
                        pstmt52.setInt(6, stime);
                        pstmt52.setInt(7, etime);
                        pstmt52.setString(8, omit);
                        pstmt52.setString(9, omit);
                        count = pstmt52.executeUpdate();         // execute the prepared stmt

                     } else {

                        pstmt52b.clearParameters();               // clear the parms
                        pstmt52b.setString(1, name);              // put the parms in stmt
                        pstmt52b.setString(2, color);
                        pstmt52b.setLong(3, sdate);
                        pstmt52b.setLong(4, edate);
                        pstmt52b.setString(5, day);
                        pstmt52b.setInt(6, stime);
                        pstmt52b.setInt(7, etime);
                        pstmt52b.setString(8, omit);
                        pstmt52b.setString(9, omit);
                        pstmt52b.setInt(10, fb);
                        count = pstmt52b.executeUpdate();         // execute the prepared stmt
                     }

                  } else {

                     if (sfb.equals( "Both" )) {

                        pstmt55.clearParameters();               // clear the parms
                        pstmt55.setString(1, name);              // put the parms in stmt
                        pstmt55.setString(2, color);
                        pstmt55.setLong(3, sdate);
                        pstmt55.setLong(4, edate);
                        pstmt55.setString(5, day);
                        pstmt55.setInt(6, stime);
                        pstmt55.setInt(7, etime);
                        pstmt55.setString(8, def);
                        pstmt55.setString(9, omit);
                        count = pstmt55.executeUpdate();         // execute the prepared stmt

                     } else {

                        pstmt55b.clearParameters();               // clear the parms
                        pstmt55b.setString(1, name);              // put the parms in stmt
                        pstmt55b.setString(2, color);
                        pstmt55b.setLong(3, sdate);
                        pstmt55b.setLong(4, edate);
                        pstmt55b.setString(5, day);
                        pstmt55b.setInt(6, stime);
                        pstmt55b.setInt(7, etime);
                        pstmt55b.setInt(8, fb);
                        pstmt55b.setString(9, def);
                        pstmt55b.setString(10, omit);
                        count = pstmt55b.executeUpdate();         // execute the prepared stmt
                     }
                  }

                  pstmt52.close();
                  pstmt55.close();
                  pstmt52b.close();
                  pstmt55b.close();
               }
               catch (Exception e10) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg10 = "Error10 in SystemUtils do1Five: ";
                  errorMsg10 = errorMsg10 + e10.getMessage();                                 // build error msg

                  logError(errorMsg10);                                       // log it
               }

            }  // end of IF individual day

            if ( recurr.equalsIgnoreCase( "every day" )) {   // if everyday

               try {

                  if (sfb.equals( "Both" )) {

                     PreparedStatement pstmt51 = con.prepareStatement (
                       "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? WHERE " +
                       "date >= ? AND date <= ? AND time >= ? AND " +
                       "time <= ? AND rest5 = ? AND rest5_color = ? ");

                     pstmt51.clearParameters();               // clear the parms
                     pstmt51.setString(1, name);              // put the parms in stmt
                     pstmt51.setString(2, color);
                     pstmt51.setLong(3, sdate);
                     pstmt51.setLong(4, edate);
                     pstmt51.setInt(5, stime);
                     pstmt51.setInt(6, etime);
                     pstmt51.setString(7, omit);
                     pstmt51.setString(8, omit);
                     count = pstmt51.executeUpdate();         // execute the prepared stmt

                     pstmt51.close();

                  } else {

                     PreparedStatement pstmt51b = con.prepareStatement (
                       "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? WHERE " +
                       "date >= ? AND date <= ? AND time >= ? AND " +
                       "time <= ? AND rest5 = ? AND rest5_color = ? AND fb = ?");

                     pstmt51b.clearParameters();               // clear the parms
                     pstmt51b.setString(1, name);              // put the parms in stmt
                     pstmt51b.setString(2, color);
                     pstmt51b.setLong(3, sdate);
                     pstmt51b.setLong(4, edate);
                     pstmt51b.setInt(5, stime);
                     pstmt51b.setInt(6, etime);
                     pstmt51b.setString(7, omit);
                     pstmt51b.setString(8, omit);
                     pstmt51b.setInt(9, fb);
                     count = pstmt51b.executeUpdate();         // execute the prepared stmt

                     pstmt51b.close();
                  }
               }
               catch (Exception e11) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg11 = "Error11 in SystemUtils do1Five: ";
                  errorMsg11 = errorMsg11 + e11.getMessage();                                 // build error msg

                  logError(errorMsg11);                                       // log it
               }

            }  // end of IF every day

            if ( recurr.equalsIgnoreCase( "all weekdays" ) ) {   // if Monday through Friday

               try {

                  if (sfb.equals( "Both" )) {

                     PreparedStatement pstmt53 = con.prepareStatement (
                       "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? WHERE " +
                       "date >= ? AND date <= ? AND day != 'Saturday'  " +
                       "AND day != 'Sunday' AND time >= ? AND " +
                       "time <= ? AND rest5 = ? AND rest5_color = ? ");

                     pstmt53.clearParameters();               // clear the parms
                     pstmt53.setString(1, name);              // put the parms in stmt
                     pstmt53.setString(2, color);
                     pstmt53.setLong(3, sdate);
                     pstmt53.setLong(4, edate);
                     pstmt53.setInt(5, stime);
                     pstmt53.setInt(6, etime);
                     pstmt53.setString(7, omit);
                     pstmt53.setString(8, omit);
                     count = pstmt53.executeUpdate();         // execute the prepared stmt

                     pstmt53.close();

                  } else {

                     PreparedStatement pstmt53b = con.prepareStatement (
                       "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? WHERE " +
                       "date >= ? AND date <= ? AND day != 'Saturday'  " +
                       "AND day != 'Sunday' AND time >= ? AND " +
                       "time <= ? AND rest5 = ? AND rest5_color = ? AND fb = ?");

                     pstmt53b.clearParameters();               // clear the parms
                     pstmt53b.setString(1, name);              // put the parms in stmt
                     pstmt53b.setString(2, color);
                     pstmt53b.setLong(3, sdate);
                     pstmt53b.setLong(4, edate);
                     pstmt53b.setInt(5, stime);
                     pstmt53b.setInt(6, etime);
                     pstmt53b.setString(7, omit);
                     pstmt53b.setString(8, omit);
                     pstmt53b.setInt(9, fb);
                     count = pstmt53b.executeUpdate();         // execute the prepared stmt

                     pstmt53b.close();
                  }
               }
               catch (Exception e12) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg12 = "Error12 in SystemUtils do1Five: ";
                  errorMsg12 = errorMsg12 + e12.getMessage();                                 // build error msg

                  logError(errorMsg12);                                       // log it
               }

            }  // end of IF all weekdays

            if ( recurr.equalsIgnoreCase( "all weekends" ) ) {   // if Saturday & Sunday

               try {

                  if (sfb.equals( "Both" )) {

                     PreparedStatement pstmt54 = con.prepareStatement (
                       "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? WHERE " +
                       "date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                       "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                       "AND time >= ? AND time <= ? AND " +
                       "rest5 = ? AND rest5_color = ? ");

                     pstmt54.clearParameters();               // clear the parms
                     pstmt54.setString(1, name);              // put the parms in stmt
                     pstmt54.setString(2, color);
                     pstmt54.setLong(3, sdate);
                     pstmt54.setLong(4, edate);
                     pstmt54.setInt(5, stime);
                     pstmt54.setInt(6, etime);
                     pstmt54.setString(7, omit);
                     pstmt54.setString(8, omit);
                     count = pstmt54.executeUpdate();         // execute the prepared stmt

                     pstmt54.close();

                  } else {

                     PreparedStatement pstmt54b = con.prepareStatement (
                       "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? WHERE " +
                       "date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                       "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                       "AND time >= ? AND time <= ? AND " +
                       "rest5 = ? AND rest5_color = ? AND fb = ?");

                     pstmt54b.clearParameters();               // clear the parms
                     pstmt54b.setString(1, name);              // put the parms in stmt
                     pstmt54b.setString(2, color);
                     pstmt54b.setLong(3, sdate);
                     pstmt54b.setLong(4, edate);
                     pstmt54b.setInt(5, stime);
                     pstmt54b.setInt(6, etime);
                     pstmt54b.setString(7, omit);
                     pstmt54b.setString(8, omit);
                     pstmt54b.setInt(9, fb);
                     count = pstmt54b.executeUpdate();         // execute the prepared stmt

                     pstmt54b.close();
                  }
               }
               catch (Exception e13) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg13 = "Error13 in SystemUtils do1Five: ";
                  errorMsg13 = errorMsg13 + e13.getMessage();                                 // build error msg

                  logError(errorMsg13);                                       // log it
               }

            }  // end of IF all weekends

         } else {     // courseName != ALL

            if (!day.equals( "" )) {    // if an individual day

               try {

                  PreparedStatement pstmt52a = con.prepareStatement (
                    "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND rest5 = ? AND rest5_color = ? AND courseName = ? ");

                  PreparedStatement pstmt55a = con.prepareStatement (
                    "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? " +
                    "WHERE ((date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND courseName = ?) AND (rest5_color = ? OR rest5_color = ?))");

                  PreparedStatement pstmt52ab = con.prepareStatement (
                    "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND rest5 = ? AND rest5_color = ? AND courseName = ? AND fb = ?");

                  PreparedStatement pstmt55ab = con.prepareStatement (
                    "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? " +
                    "WHERE ((date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND courseName = ? AND fb = ?) AND (rest5_color = ? OR rest5_color = ?))");

                  //
                  //   If restriction color is not 'default' and a rest exists in teecurr2 that is default, override
                  //
                  if (color.equalsIgnoreCase("default")) {    // if default color - only add if no other rest

                     if (sfb.equals( "Both" )) {

                        pstmt52a.clearParameters();               // clear the parms
                        pstmt52a.setString(1, name);              // put the parms in stmt
                        pstmt52a.setString(2, color);
                        pstmt52a.setLong(3, sdate);
                        pstmt52a.setLong(4, edate);
                        pstmt52a.setString(5, day);
                        pstmt52a.setInt(6, stime);
                        pstmt52a.setInt(7, etime);
                        pstmt52a.setString(8, omit);
                        pstmt52a.setString(9, omit);
                        pstmt52a.setString(10, courseName);
                        count = pstmt52a.executeUpdate();         // execute the prepared stmt

                     } else {

                        pstmt52ab.clearParameters();               // clear the parms
                        pstmt52ab.setString(1, name);              // put the parms in stmt
                        pstmt52ab.setString(2, color);
                        pstmt52ab.setLong(3, sdate);
                        pstmt52ab.setLong(4, edate);
                        pstmt52ab.setString(5, day);
                        pstmt52ab.setInt(6, stime);
                        pstmt52ab.setInt(7, etime);
                        pstmt52ab.setString(8, omit);
                        pstmt52ab.setString(9, omit);
                        pstmt52ab.setString(10, courseName);
                        pstmt52ab.setInt(11, fb);
                        count = pstmt52ab.executeUpdate();         // execute the prepared stmt
                     }
                  } else {

                     if (sfb.equals( "Both" )) {

                        pstmt55a.clearParameters();               // clear the parms
                        pstmt55a.setString(1, name);              // put the parms in stmt
                        pstmt55a.setString(2, color);
                        pstmt55a.setLong(3, sdate);
                        pstmt55a.setLong(4, edate);
                        pstmt55a.setString(5, day);
                        pstmt55a.setInt(6, stime);
                        pstmt55a.setInt(7, etime);
                        pstmt55a.setString(8, courseName);
                        pstmt55a.setString(9, def);
                        pstmt55a.setString(10, omit);
                        count = pstmt55a.executeUpdate();         // execute the prepared stmt

                     } else {

                        pstmt55ab.clearParameters();               // clear the parms
                        pstmt55ab.setString(1, name);              // put the parms in stmt
                        pstmt55ab.setString(2, color);
                        pstmt55ab.setLong(3, sdate);
                        pstmt55ab.setLong(4, edate);
                        pstmt55ab.setString(5, day);
                        pstmt55ab.setInt(6, stime);
                        pstmt55ab.setInt(7, etime);
                        pstmt55ab.setString(8, courseName);
                        pstmt55ab.setInt(9, fb);
                        pstmt55ab.setString(10, def);
                        pstmt55ab.setString(11, omit);
                        count = pstmt55ab.executeUpdate();         // execute the prepared stmt
                     }
                  }

                  pstmt52a.close();
                  pstmt55a.close();
                  pstmt52ab.close();
                  pstmt55ab.close();
               }
               catch (Exception e14) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg14 = "Error14 in SystemUtils do1Five: ";
                  errorMsg14 = errorMsg14 + e14.getMessage();                                 // build error msg

                  logError(errorMsg14);                                       // log it
               }

            }  // end of IF individual day

            if ( recurr.equalsIgnoreCase( "every day" ) ) {   // if everyday

               try {
                  PreparedStatement pstmt51a = con.prepareStatement (
                    "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? WHERE " +
                    "date >= ? AND date <= ? AND time >= ? AND " +
                    "time <= ? AND rest5 = ? AND rest5_color = ? AND courseName = ?");

                  PreparedStatement pstmt51ab = con.prepareStatement (
                    "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? WHERE " +
                    "date >= ? AND date <= ? AND time >= ? AND " +
                    "time <= ? AND rest5 = ? AND rest5_color = ? AND courseName = ? AND fb = ?");

                  if (sfb.equals( "Both" )) {

                     pstmt51a.clearParameters();               // clear the parms
                     pstmt51a.setString(1, name);              // put the parms in stmt
                     pstmt51a.setString(2, color);
                     pstmt51a.setLong(3, sdate);
                     pstmt51a.setLong(4, edate);
                     pstmt51a.setInt(5, stime);
                     pstmt51a.setInt(6, etime);
                     pstmt51a.setString(7, omit);
                     pstmt51a.setString(8, omit);
                     pstmt51a.setString(9, courseName);
                     count = pstmt51a.executeUpdate();         // execute the prepared stmt

                  } else {

                     pstmt51ab.clearParameters();               // clear the parms
                     pstmt51ab.setString(1, name);              // put the parms in stmt
                     pstmt51ab.setString(2, color);
                     pstmt51ab.setLong(3, sdate);
                     pstmt51ab.setLong(4, edate);
                     pstmt51ab.setInt(5, stime);
                     pstmt51ab.setInt(6, etime);
                     pstmt51ab.setString(7, omit);
                     pstmt51ab.setString(8, omit);
                     pstmt51ab.setString(9, courseName);
                     pstmt51ab.setInt(10, fb);
                     count = pstmt51ab.executeUpdate();         // execute the prepared stmt
                  }
                  pstmt51a.close();
                  pstmt51ab.close();
               }
               catch (Exception e15) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg15 = "Error15 in SystemUtils do1Five: ";
                  errorMsg15 = errorMsg15 + e15.getMessage();                                 // build error msg

                  logError(errorMsg15);                                       // log it
               }

            }  // end of IF every day

            if ( recurr.equalsIgnoreCase( "all weekdays" ) ) {   // if Monday through Friday

               try {
                  PreparedStatement pstmt53a = con.prepareStatement (
                    "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day != 'Saturday'  " +
                    "AND day != 'Sunday' AND time >= ? AND " +
                    "time <= ? AND rest5 = ? AND rest5_color = ? AND courseName = ?");

                  PreparedStatement pstmt53ab = con.prepareStatement (
                    "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day != 'Saturday'  " +
                    "AND day != 'Sunday' AND time >= ? AND " +
                    "time <= ? AND rest5 = ? AND rest5_color = ? AND courseName = ? AND fb = ?");

                  if (sfb.equals( "Both" )) {

                     pstmt53a.clearParameters();               // clear the parms
                     pstmt53a.setString(1, name);              // put the parms in stmt
                     pstmt53a.setString(2, color);
                     pstmt53a.setLong(3, sdate);
                     pstmt53a.setLong(4, edate);
                     pstmt53a.setInt(5, stime);
                     pstmt53a.setInt(6, etime);
                     pstmt53a.setString(7, omit);
                     pstmt53a.setString(8, omit);
                     pstmt53a.setString(9, courseName);
                     count = pstmt53a.executeUpdate();         // execute the prepared stmt

                  } else {

                     pstmt53ab.clearParameters();               // clear the parms
                     pstmt53ab.setString(1, name);              // put the parms in stmt
                     pstmt53ab.setString(2, color);
                     pstmt53ab.setLong(3, sdate);
                     pstmt53ab.setLong(4, edate);
                     pstmt53ab.setInt(5, stime);
                     pstmt53ab.setInt(6, etime);
                     pstmt53ab.setString(7, omit);
                     pstmt53ab.setString(8, omit);
                     pstmt53ab.setString(9, courseName);
                     pstmt53ab.setInt(10, fb);
                     count = pstmt53ab.executeUpdate();         // execute the prepared stmt
                  }
                  pstmt53a.close();
                  pstmt53ab.close();
               }
               catch (Exception e16) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg16 = "Error16 in SystemUtils do1Five: ";
                  errorMsg16 = errorMsg16 + e16.getMessage();                                 // build error msg

                  logError(errorMsg16);                                       // log it
               }

            }  // end of IF all weekdays

            if ( recurr.equalsIgnoreCase( "all weekends" ) ) {   // if Saturday & Sunday

               try {
                  PreparedStatement pstmt54a = con.prepareStatement (
                    "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                    "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                    "AND time >= ? AND time <= ? AND " +
                    "rest5 = ? AND rest5_color = ? AND courseName = ?");

                  PreparedStatement pstmt54ab = con.prepareStatement (
                    "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                    "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                    "AND time >= ? AND time <= ? AND " +
                    "rest5 = ? AND rest5_color = ? AND courseName = ? AND fb = ?");

                  if (sfb.equals( "Both" )) {

                     pstmt54a.clearParameters();               // clear the parms
                     pstmt54a.setString(1, name);              // put the parms in stmt
                     pstmt54a.setString(2, color);
                     pstmt54a.setLong(3, sdate);
                     pstmt54a.setLong(4, edate);
                     pstmt54a.setInt(5, stime);
                     pstmt54a.setInt(6, etime);
                     pstmt54a.setString(7, omit);
                     pstmt54a.setString(8, omit);
                     pstmt54a.setString(9, courseName);
                     count = pstmt54a.executeUpdate();         // execute the prepared stmt

                  } else {

                     pstmt54ab.clearParameters();               // clear the parms
                     pstmt54ab.setString(1, name);              // put the parms in stmt
                     pstmt54ab.setString(2, color);
                     pstmt54ab.setLong(3, sdate);
                     pstmt54ab.setLong(4, edate);
                     pstmt54ab.setInt(5, stime);
                     pstmt54ab.setInt(6, etime);
                     pstmt54ab.setString(7, omit);
                     pstmt54ab.setString(8, omit);
                     pstmt54ab.setString(9, courseName);
                     pstmt54ab.setInt(10, fb);
                     count = pstmt54ab.executeUpdate();         // execute the prepared stmt
                  }

                  pstmt54a.close();
                  pstmt54ab.close();
               }
               catch (Exception e17) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg17 = "Error17 in SystemUtils do1Five: ";
                  errorMsg17 = errorMsg17 + e17.getMessage();                                 // build error msg

                  logError(errorMsg17);                                       // log it
               }

            }  // end of IF all weekends

         }  // end of IF courseName=ALL

      }               // end of while for restriction table
      pstmt1.close();
   }
   catch (Exception e18) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg18 = "Error18 in SystemUtils do1Five: ";
      errorMsg18 = errorMsg18 + e18.getMessage();                                 // build error msg

      logError(errorMsg18);                                       // log it
   }

 }  // end of do1Five


 //************************************************************************
 // doEvents - Scans the event tables for
 //            new/modified entries and updates teecurr2 if necessary.
 //
 //
 //   called by:  Proshop_updateTeecurr
 //
 //************************************************************************

 public static void doEvents(Connection con) {


   Statement stmt = null;
   ResultSet rs = null;

   //
   //*****************************************************
   //  Get all the upcoming events from the event table
   //*****************************************************
   //
   try {

      stmt = con.createStatement();

      rs = stmt.executeQuery("" +
              "SELECT name FROM events2b " +
              "WHERE inactive = 0 AND " +
              "date >= DATE_FORMAT(now(), '%Y%m%d')");

      while ( rs.next() ) {

         //
         //  go process the single event
         //
         do1Event(con, rs.getString(1));

      }

   } catch (Exception exc) {

       logError("Error20 in SystemUtils doEvents: " + exc.getMessage());

   } finally {

      try { rs.close(); }
      catch (Exception ignore) {}

      try { stmt.close(); }
      catch (Exception ignore) {}

   }

 }  // end of doEvents


 //************************************************************************
 // do1Event - Update teecurr for the single event.
 //
 //
 //   called by:  Proshop_editevnt
 //               doEvents (above)
 //
 //************************************************************************

 public static void do1Event(Connection con, String name) {

   Statement stmt = null;
   Statement stmt2 = null;
   ResultSet rs = null;


   long date = 0;                 // event table variables
   int stime = 0;
   int etime = 0;
   int stime2 = 0;
   int etime2 = 0;
   int type = 0;

   long sdate = 0;                 // restriction table variables
   long edate = 0;
   String recurr = "";
   String day = "";

   int event_id = 0;
   int activity_id = 0;
   int count = 0;
   int fb = 0;
   int fb2 = 0;

   String event_name = "";        // teecurr2 table variables
   String event_color = "";
   String def = "default";

   String course = "";           // common variables
   String courseName = "";
   String sfb = "";
   String sfb2 = "";
   String omit = "";
   String color = "";
   String locations_csv = "";

   //
   //  Get today's date and the current time for event processing
   //
   Calendar cal = new GregorianCalendar();            // get todays date

   int yy = cal.get(Calendar.YEAR);
   int mm = cal.get(Calendar.MONTH);
   int dd = cal.get(Calendar.DAY_OF_MONTH);
   mm++;                                                  // month starts at zero
   long today_date = (yy * 10000) + (mm * 100) + dd;      // create a date field of yyyymmdd

   //
   //*****************************************************
   //  Get the event and use it to update teecurr
   //*****************************************************
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
              "SELECT event_id, date, activity_id, locations, stime, etime, color, type, courseName, fb, stime2, etime2, fb2 " +
              "FROM events2b WHERE name = ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setString(1, name);       // put the parm in stmt
      rs = pstmt1.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         event_id = rs.getInt("event_id");
         activity_id = rs.getInt("activity_id");
         locations_csv = rs.getString("locations");
         date = rs.getLong("date");
         stime = rs.getInt("stime");
         etime = rs.getInt("etime");
         color = rs.getString("color");
         type = rs.getInt("type");
         courseName = rs.getString("courseName");
         sfb = rs.getString("fb");
         stime2 = rs.getInt("stime2");
         etime2 = rs.getInt("etime2");
         sfb2 = rs.getString("fb2");

         //if (date >= today_date) {           // if event hasn't passed

            if (date >= today_date && activity_id == 0) {

               //
               //  prepare F/B indicators
               //
               //     sfb = Front, Back or Both
               //
               fb = 0;                       // init to Front
               fb2 = 0;

               if (sfb.equals( "Back" )) {
                  fb = 1;     // back
               }
               if (sfb2.equals( "Back" )) {
                  fb2 = 1;     // back
               }

               //
               //  Put event info in teecurr slots if matching date/time and event not already there
               //
               try {

                  if (courseName.equals( "-ALL-" )) {    // if ALL Courses

                     if (sfb.equalsIgnoreCase( "both" )) {

                        PreparedStatement pstmte1 = con.prepareStatement (
                          "UPDATE teecurr2 SET event = ?, event_color = ?, event_type = ? WHERE date = ? AND " +
                          "time >= ? AND time <= ? AND event = ? AND event_color = ? ");

                        pstmte1.clearParameters();               // clear the parms
                        pstmte1.setString(1, name);              // put the parms in stmt
                        pstmte1.setString(2, color);
                        pstmte1.setInt(3, type);
                        pstmte1.setLong(4, date);
                        pstmte1.setInt(5, stime);
                        pstmte1.setInt(6, etime);
                        pstmte1.setString(7, omit);
                        pstmte1.setString(8, omit);
                        count = pstmte1.executeUpdate();         // execute the prepared stmt

                        pstmte1.close();

                     } else {

                        PreparedStatement pstmte2 = con.prepareStatement (
                          "UPDATE teecurr2 SET event = ?, event_color = ?, event_type = ? WHERE date = ? AND " +
                          "time >= ? AND time <= ? AND event = ? AND event_color = ? AND fb = ?");

                        pstmte2.clearParameters();               // clear the parms
                        pstmte2.setString(1, name);              // put the parms in stmt
                        pstmte2.setString(2, color);
                        pstmte2.setInt(3, type);
                        pstmte2.setLong(4, date);
                        pstmte2.setInt(5, stime);
                        pstmte2.setInt(6, etime);
                        pstmte2.setString(7, omit);
                        pstmte2.setString(8, omit);
                        pstmte2.setInt(9, fb);
                        count = pstmte2.executeUpdate();         // execute the prepared stmt

                        pstmte2.close();
                     }
                     //
                     //  now do second set of blockers if necessary
                     //
                     if (stime2 != etime2) {      // if 2nd blockers requested

                        if (sfb2.equalsIgnoreCase( "both" )) {

                           PreparedStatement pstmte1 = con.prepareStatement (
                             "UPDATE teecurr2 SET event = ?, event_color = ?, event_type = ? WHERE date = ? AND " +
                             "time >= ? AND time <= ? AND event = ? AND event_color = ? ");

                           pstmte1.clearParameters();               // clear the parms
                           pstmte1.setString(1, name);              // put the parms in stmt
                           pstmte1.setString(2, color);
                           pstmte1.setInt(3, type);
                           pstmte1.setLong(4, date);
                           pstmte1.setInt(5, stime2);
                           pstmte1.setInt(6, etime2);
                           pstmte1.setString(7, omit);
                           pstmte1.setString(8, omit);
                           count = pstmte1.executeUpdate();         // execute the prepared stmt

                           pstmte1.close();

                        } else {

                           PreparedStatement pstmte2 = con.prepareStatement (
                             "UPDATE teecurr2 SET event = ?, event_color = ?, event_type = ? WHERE date = ? AND " +
                             "time >= ? AND time <= ? AND event = ? AND event_color = ? AND fb = ?");

                           pstmte2.clearParameters();               // clear the parms
                           pstmte2.setString(1, name);              // put the parms in stmt
                           pstmte2.setString(2, color);
                           pstmte2.setInt(3, type);
                           pstmte2.setLong(4, date);
                           pstmte2.setInt(5, stime2);
                           pstmte2.setInt(6, etime2);
                           pstmte2.setString(7, omit);
                           pstmte2.setString(8, omit);
                           pstmte2.setInt(9, fb2);
                           count = pstmte2.executeUpdate();         // execute the prepared stmt

                           pstmte2.close();
                        }
                     }

                  } else {     // courseName != ALL

                     if (sfb.equalsIgnoreCase( "both" )) {

                        PreparedStatement pstmta = con.prepareStatement (
                          "UPDATE teecurr2 SET event = ?, event_color = ?, event_type = ? WHERE date = ? AND " +
                          "time >= ? AND time <= ? AND event = ? AND event_color = ? AND courseName = ?");

                        pstmta.clearParameters();               // clear the parms
                        pstmta.setString(1, name);              // put the parms in stmt
                        pstmta.setString(2, color);
                        pstmta.setInt(3, type);
                        pstmta.setLong(4, date);
                        pstmta.setInt(5, stime);
                        pstmta.setInt(6, etime);
                        pstmta.setString(7, omit);
                        pstmta.setString(8, omit);
                        pstmta.setString(9, courseName);
                        count = pstmta.executeUpdate();         // execute the prepared stmt

                        pstmta.close();

                     } else {

                        PreparedStatement pstmta2 = con.prepareStatement (
                          "UPDATE teecurr2 SET event = ?, event_color = ?, event_type = ? WHERE date = ? AND " +
                          "time >= ? AND time <= ? AND event = ? AND event_color = ? AND courseName = ? AND fb = ?");

                        pstmta2.clearParameters();               // clear the parms
                        pstmta2.setString(1, name);              // put the parms in stmt
                        pstmta2.setString(2, color);
                        pstmta2.setInt(3, type);
                        pstmta2.setLong(4, date);
                        pstmta2.setInt(5, stime);
                        pstmta2.setInt(6, etime);
                        pstmta2.setString(7, omit);
                        pstmta2.setString(8, omit);
                        pstmta2.setString(9, courseName);
                        pstmta2.setInt(10, fb);
                        count = pstmta2.executeUpdate();         // execute the prepared stmt

                        pstmta2.close();
                     }
                     //
                     //  now do second set of blockers if necessary
                     //
                     if (stime2 != etime2) {      // if 2nd blockers requested

                        if (sfb2.equalsIgnoreCase( "both" )) {

                           PreparedStatement pstmta = con.prepareStatement (
                             "UPDATE teecurr2 SET event = ?, event_color = ?, event_type = ? WHERE date = ? AND " +
                             "time >= ? AND time <= ? AND event = ? AND event_color = ? AND courseName = ?");

                           pstmta.clearParameters();               // clear the parms
                           pstmta.setString(1, name);              // put the parms in stmt
                           pstmta.setString(2, color);
                           pstmta.setInt(3, type);
                           pstmta.setLong(4, date);
                           pstmta.setInt(5, stime2);
                           pstmta.setInt(6, etime2);
                           pstmta.setString(7, omit);
                           pstmta.setString(8, omit);
                           pstmta.setString(9, courseName);
                           count = pstmta.executeUpdate();         // execute the prepared stmt

                           pstmta.close();

                        } else {

                           PreparedStatement pstmta2 = con.prepareStatement (
                             "UPDATE teecurr2 SET event = ?, event_color = ?, event_type = ? WHERE date = ? AND " +
                             "time >= ? AND time <= ? AND event = ? AND event_color = ? AND courseName = ? AND fb = ?");

                           pstmta2.clearParameters();               // clear the parms
                           pstmta2.setString(1, name);              // put the parms in stmt
                           pstmta2.setString(2, color);
                           pstmta2.setInt(3, type);
                           pstmta2.setLong(4, date);
                           pstmta2.setInt(5, stime2);
                           pstmta2.setInt(6, etime2);
                           pstmta2.setString(7, omit);
                           pstmta2.setString(8, omit);
                           pstmta2.setString(9, courseName);
                           pstmta2.setInt(10, fb2);
                           count = pstmta2.executeUpdate();         // execute the prepared stmt

                           pstmta2.close();
                        }
                     }
                  }
               }
               catch (Exception e19) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg19 = "Error19 in SystemUtils do1Event: ";
                  errorMsg19 = errorMsg19 + e19.getMessage();                                 // build error msg

                  logError(errorMsg19);                                       // log it
               }

            } else if (activity_id != 0) {

                // update the activity sheets to reflect this event
                PreparedStatement pstmt = con.prepareStatement (
                   "UPDATE activity_sheets " +
                   "SET event_id = ? " +
                   "WHERE DATE_FORMAT(date_time, '%Y%m%d') = ? AND " +
                   "DATE_FORMAT(date_time, '%H%i') >= ? AND DATE_FORMAT(date_time, '%H%i') <= ? AND " +
                   "activity_id IN (" + locations_csv + ")");

                pstmt.clearParameters();
                pstmt.setInt(1, event_id);
                pstmt.setLong(2, date);
                pstmt.setInt(3, stime);
                pstmt.setInt(4, etime);
                count = pstmt.executeUpdate();

                pstmt.close();

            }

         //}     // end of IF date ok

      }  // end of IF event

      pstmt1.close();
   }
   catch (Exception e20) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg20 = "Error20 in SystemUtils do1Event: ";
      errorMsg20 = errorMsg20 + e20.getMessage();                                 // build error msg

      logError(errorMsg20);                                       // log it
   }

 }  // end of do1Event


 //************************************************************************
 // doBlockers - Scans the block tables for
 //              new/modified entries and updates teecurr2 if necessary.
 //
 //
 //   called by:  updateTeecurr
 //
 //************************************************************************

 public static void doBlockers(Connection con) {


    Statement stmt = null;
    ResultSet rs = null;

    //
    //*****************************************************
    //  Get all the active blockers from the block table
    //*****************************************************
    //
    try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("" +
                "SELECT id, name, activity_id " +
                "FROM block2 " +
                "WHERE edate >= DATE_FORMAT(now(), '%Y%m%d')");

        while (rs.next()) {

            //
            //  go process each blocker seperately
            //
            if (rs.getInt("activity_id") == 0) {

                // tee time blocker
                do1Blocker(con, rs.getString("name"));

            } else {

                // activity blocker
                do1Blocker(con, rs.getInt("id"));

            }

        }

    } catch (Exception exc) {

        logError("Error in SystemUtils doBlockers: " + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { stmt.close(); }
        catch (Exception ignore) {}

    }

 }  // end of doBlockers



 public static void do1Blocker(Connection con, int id) {

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    int activity_id = 0;
    int sdate = 0;
    int edate = 0;
    int stime = 0;
    int etime = 0;

    String locations_csv = "";
    String day_clause = "";
    String recurr = "";

    try {

        pstmt = con.prepareStatement ("SELECT locations, sdate, stime, edate, etime, recurr FROM block2 WHERE id = ?");

        pstmt.clearParameters();
        pstmt.setInt(1, id);
        rs = pstmt.executeQuery();

        if ( rs.next() ) {

            //activity_id = rs.getInt("activity_id");
            sdate = rs.getInt("sdate");
            stime = rs.getInt("stime");
            edate = rs.getInt("edate");
            etime = rs.getInt("etime");
            recurr = rs.getString("recurr");
            locations_csv = rs.getString("locations");

        }

    } catch (Exception exc) {

        logError("Error1 in SystemUtils.do1Blocker: id=" + id + ", err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    //String in = getActivity.buildInString(activity_id, 1, con);

    String sql = "UPDATE activity_sheets SET blocker_id = ? WHERE activity_id IN (" + locations_csv + ") AND auto_blocked = 0 AND " +
                    "DATE_FORMAT(date_time, '%Y%m%d') >= ? AND DATE_FORMAT(date_time, '%Y%m%d') <= ? AND " +
                    "DATE_FORMAT(date_time, '%H%i') >= ? AND DATE_FORMAT(date_time, '%H%i') <= ? ";


    // setup the day name part of the where clause based upon the recurr for this blocker
    if ( recurr.equalsIgnoreCase( "every sunday" ) ) {

        day_clause = "AND DATE_FORMAT(date_time, '%W') = 'Sunday'";

    } else if ( recurr.equalsIgnoreCase( "every monday" ) ) {

        day_clause = "AND DATE_FORMAT(date_time, '%W') = 'Monday'";

    } else if ( recurr.equalsIgnoreCase( "every tuesday" ) ) {

        day_clause = "AND DATE_FORMAT(date_time, '%W') = 'Tuesday'";

    } else if ( recurr.equalsIgnoreCase( "every wednesday" ) ) {

        day_clause = "AND DATE_FORMAT(date_time, '%W') = 'Wednesday'";

    } else if ( recurr.equalsIgnoreCase( "every thursday" ) ) {

        day_clause = "AND DATE_FORMAT(date_time, '%W') = 'Thursday'";

    } else if ( recurr.equalsIgnoreCase( "every friday" ) ) {

        day_clause = "AND DATE_FORMAT(date_time, '%W') = 'Friday'";

    } else if ( recurr.equalsIgnoreCase( "every saturday" ) ) {

        day_clause = "AND DATE_FORMAT(date_time, '%W') = 'Saturday'";

    } else if ( recurr.equalsIgnoreCase( "all weekdays" ) ) {

        day_clause = "AND (DATE_FORMAT(date_time, '%W') <> 'Saturday' AND DATE_FORMAT(date_time, '%W') <> 'Sunday')";

    } else if ( recurr.equalsIgnoreCase( "all weekends" ) ) {

        day_clause = "AND (DATE_FORMAT(date_time, '%W') = 'Saturday' OR DATE_FORMAT(date_time, '%W') = 'Sunday')";

    }

    // append the day clause
    sql = sql + day_clause;

    try {

        pstmt = con.prepareStatement ( sql );

        pstmt.clearParameters();
        pstmt.setInt(1, id);
        pstmt.setInt(2, sdate);
        pstmt.setInt(3, edate);
        pstmt.setInt(4, stime);
        pstmt.setInt(5, etime);

        pstmt.executeUpdate();

    } catch (Exception exc) {

        logError("Error2 in SystemUtils.do1Blocker: id=" + id + ", sql="+sql+", err=" + exc.toString());

    } finally {

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

 }


 //************************************************************************
 // do1Blocker - Update teecurr for the named blocker
 //
 //   called by:  Proshop_addblock
 //                  "   _editblock
 //               doBlcokers (above)
 //
 //************************************************************************

 public static void do1Blocker(Connection con, String name) {


   ResultSet rs = null;


   long date = 0;                 // event table variables
   int stime = 0;
   int etime = 0;
   int type = 0;

   long sdate = 0;                 // restriction table variables
   long edate = 0;
   String recurr = "";
   String day = "";

   int count = 0;
   int fb = 0;

   String event_name = "";        // teecurr2 table variables
   String event_color = "";
   String def = "default";

   String course = "";           // common variables
   String courseName = "";
   String sfb = "";
   String omit = "";
   String color = "";

   //
   //*****************************************************
   //  Get the blocker and process teecurr
   //*****************************************************
   //
   try {

      PreparedStatement pstmtx = con.prepareStatement (
              "SELECT sdate, stime, edate, etime, recurr, courseName, fb FROM block2 WHERE name = ?");

      pstmtx.clearParameters();        // clear the parms
      pstmtx.setString(1, name);       // put the parm in stmt
      rs = pstmtx.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         sdate = rs.getLong(1);
         stime = rs.getInt(2);
         edate = rs.getLong(3);
         etime = rs.getInt(4);
         recurr = rs.getString(5);
         courseName = rs.getString(6);
         sfb = rs.getString(7);

         day = "";    // init day value

         //
         //  Put blocker info in teecurr2 slots if matching date/time and restriction not already there
         //  (this is done based on recurrence sepcified in restriction)
         //
         if ( recurr.equalsIgnoreCase( "every sunday" ) ) {   // if every Sunday

            day = "Sunday";
         }

         if ( recurr.equalsIgnoreCase( "every monday" ) ) {   // if every Monday

            day = "Monday";
         }

         if ( recurr.equalsIgnoreCase( "every tuesday" ) ) {   // if every Tuesday

            day = "Tuesday";
         }

         if ( recurr.equalsIgnoreCase( "every wednesday" ) ) {   // if every Wednesday

            day = "Wednesday";
         }

         if ( recurr.equalsIgnoreCase( "every thursday" ) ) {   // if every Thursday

            day = "Thursday";
         }

         if ( recurr.equalsIgnoreCase( "every friday" ) ) {   // if every Friday

            day = "Friday";
         }

         if ( recurr.equalsIgnoreCase( "every saturday" ) ) {   // if every Satruday

            day = "Saturday";
         }

         //
         //  prepare F/B indicator
         //
         fb = 0;                       // init to Front

         if (sfb.equals( "Back" )) {

            fb = 1;     // back
         }

         //
         //   courseName = '-ALL-', or name of course, or null
         //
         //   sfb = 'Front', 'Back', or 'Both'
         //
         if (courseName.equals( "-ALL-" )) {    // if ALL Courses

            if (!day.equals( "" )) {    // if an individual day

               PreparedStatement pstmt5 = con.prepareStatement (
                 "UPDATE teecurr2 SET blocker = ? " +
                 "WHERE auto_blocked = 0 AND date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                 "time <= ? AND event_type != 1 AND player1 = ''");

               PreparedStatement pstmt5b = con.prepareStatement (
                 "UPDATE teecurr2 SET blocker = ? " +
                 "WHERE auto_blocked = 0 AND date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                 "time <= ? AND event_type != 1 AND fb = ? AND player1 = ''");

                  if (sfb.equals( "Both" )) {

                     pstmt5.clearParameters();               // clear the parms
                     pstmt5.setString(1, name);              // put the parms in stmt
                     pstmt5.setLong(2, sdate);
                     pstmt5.setLong(3, edate);
                     pstmt5.setString(4, day);
                     pstmt5.setInt(5, stime);
                     pstmt5.setInt(6, etime);
                     count = pstmt5.executeUpdate();         // execute the prepared stmt

                  } else {

                     pstmt5b.clearParameters();               // clear the parms
                     pstmt5b.setString(1, name);              // put the parms in stmt
                     pstmt5b.setLong(2, sdate);
                     pstmt5b.setLong(3, edate);
                     pstmt5b.setString(4, day);
                     pstmt5b.setInt(5, stime);
                     pstmt5b.setInt(6, etime);
                     pstmt5b.setInt(7, fb);
                     count = pstmt5b.executeUpdate();         // execute the prepared stmt
                  }
               pstmt5.close();
               pstmt5b.close();

            }  // end of IF individual day

            if ( recurr.equalsIgnoreCase( "every day" ) ) {   // if everyday

               PreparedStatement pstmt1 = con.prepareStatement (
                 "UPDATE teecurr2 SET blocker = ? WHERE auto_blocked = 0 AND " +
                 "date >= ? AND date <= ? AND time >= ? AND " +
                 "time <= ? AND event_type != 1 AND player1 = ''");

               PreparedStatement pstmt1b = con.prepareStatement (
                 "UPDATE teecurr2 SET blocker = ? WHERE auto_blocked = 0 AND " +
                 "date >= ? AND date <= ? AND time >= ? AND " +
                 "time <= ? AND event_type != 1 AND fb = ? AND player1 = ''");

               if (sfb.equals( "Both" )) {

                  pstmt1.clearParameters();               // clear the parms
                  pstmt1.setString(1, name);              // put the parms in stmt
                  pstmt1.setLong(2, sdate);
                  pstmt1.setLong(3, edate);
                  pstmt1.setInt(4, stime);
                  pstmt1.setInt(5, etime);
                  count = pstmt1.executeUpdate();         // execute the prepared stmt

               } else {

                  pstmt1b.clearParameters();               // clear the parms
                  pstmt1b.setString(1, name);              // put the parms in stmt
                  pstmt1b.setLong(2, sdate);
                  pstmt1b.setLong(3, edate);
                  pstmt1b.setInt(4, stime);
                  pstmt1b.setInt(5, etime);
                  pstmt1b.setInt(6, fb);
                  count = pstmt1b.executeUpdate();         // execute the prepared stmt
               }
               pstmt1.close();
               pstmt1b.close();

            }  // end of IF every day

            if ( recurr.equalsIgnoreCase( "all weekdays" ) ) {   // if Monday through Friday

               PreparedStatement pstmt3 = con.prepareStatement (
                 "UPDATE teecurr2 SET blocker = ? WHERE auto_blocked = 0 AND " +
                 "date >= ? AND date <= ? AND day != 'Saturday'  " +
                 "AND day != 'Sunday' AND time >= ? AND " +
                 "time <= ? AND event_type != 1 AND player1 = ''");

               PreparedStatement pstmt3b = con.prepareStatement (
                 "UPDATE teecurr2 SET blocker = ? WHERE auto_blocked = 0 AND " +
                 "date >= ? AND date <= ? AND day != 'Saturday'  " +
                 "AND day != 'Sunday' AND time >= ? AND " +
                 "time <= ? AND event_type != 1 AND fb = ? AND player1 = ''");

               if (sfb.equals( "Both" )) {

                  pstmt3.clearParameters();               // clear the parms
                  pstmt3.setString(1, name);              // put the parms in stmt
                  pstmt3.setLong(2, sdate);
                  pstmt3.setLong(3, edate);
                  pstmt3.setInt(4, stime);
                  pstmt3.setInt(5, etime);
                  count = pstmt3.executeUpdate();         // execute the prepared stmt

               } else {

                  pstmt3b.clearParameters();               // clear the parms
                  pstmt3b.setString(1, name);              // put the parms in stmt
                  pstmt3b.setLong(2, sdate);
                  pstmt3b.setLong(3, edate);
                  pstmt3b.setInt(4, stime);
                  pstmt3b.setInt(5, etime);
                  pstmt3b.setInt(6, fb);
                  count = pstmt3b.executeUpdate();         // execute the prepared stmt
               }
               pstmt3.close();
               pstmt3b.close();

            }  // end of IF all weekdays

            if ( recurr.equalsIgnoreCase( "all weekends" ) ) {   // if Saturday & Sunday

               PreparedStatement pstmt4 = con.prepareStatement (
                 "UPDATE teecurr2 SET blocker = ? WHERE auto_blocked = 0 AND " +
                 "date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                 "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                 "AND time >= ? AND time <= ? AND event_type != 1 AND player1 = ''");

               PreparedStatement pstmt4b = con.prepareStatement (
                 "UPDATE teecurr2 SET blocker = ? WHERE auto_blocked = 0 AND " +
                 "date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                 "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                 "AND time >= ? AND time <= ? AND event_type != 1 AND fb = ? AND player1 = ''");

               if (sfb.equals( "Both" )) {

                  pstmt4.clearParameters();               // clear the parms
                  pstmt4.setString(1, name);              // put the parms in stmt
                  pstmt4.setLong(2, sdate);
                  pstmt4.setLong(3, edate);
                  pstmt4.setInt(4, stime);
                  pstmt4.setInt(5, etime);
                  count = pstmt4.executeUpdate();         // execute the prepared stmt

               } else {

                  pstmt4b.clearParameters();               // clear the parms
                  pstmt4b.setString(1, name);              // put the parms in stmt
                  pstmt4b.setLong(2, sdate);
                  pstmt4b.setLong(3, edate);
                  pstmt4b.setInt(4, stime);
                  pstmt4b.setInt(5, etime);
                  pstmt4b.setInt(6, fb);
                  count = pstmt4b.executeUpdate();         // execute the prepared stmt
               }
               pstmt4b.close();
               pstmt4.close();

            }  // end of IF all weekends

         } else {     // courseName != ALL

            if (!day.equals( "" )) {    // if an individual day

               PreparedStatement pstmt5a = con.prepareStatement (
                 "UPDATE teecurr2 SET blocker = ? " +
                 "WHERE date >= ? AND date <= ? AND day = ? AND time >= ? AND auto_blocked = 0 AND " +
                 "time <= ? AND event_type != 1 AND courseName = ? AND player1 = ''");

               PreparedStatement pstmt5ab = con.prepareStatement (
                 "UPDATE teecurr2 SET blocker = ? " +
                 "WHERE date >= ? AND date <= ? AND day = ? AND time >= ? AND auto_blocked = 0 AND " +
                 "time <= ? AND event_type != 1 AND courseName = ? AND fb = ? AND player1 = ''");

               if (sfb.equals( "Both" )) {

                  pstmt5a.clearParameters();               // clear the parms
                  pstmt5a.setString(1, name);              // put the parms in stmt
                  pstmt5a.setLong(2, sdate);
                  pstmt5a.setLong(3, edate);
                  pstmt5a.setString(4, day);
                  pstmt5a.setInt(5, stime);
                  pstmt5a.setInt(6, etime);
                  pstmt5a.setString(7, courseName);
                  count = pstmt5a.executeUpdate();         // execute the prepared stmt

               } else {

                  pstmt5ab.clearParameters();               // clear the parms
                  pstmt5ab.setString(1, name);              // put the parms in stmt
                  pstmt5ab.setLong(2, sdate);
                  pstmt5ab.setLong(3, edate);
                  pstmt5ab.setString(4, day);
                  pstmt5ab.setInt(5, stime);
                  pstmt5ab.setInt(6, etime);
                  pstmt5ab.setString(7, courseName);
                  pstmt5ab.setInt(8, fb);
                  count = pstmt5ab.executeUpdate();         // execute the prepared stmt
               }
               pstmt5ab.close();
               pstmt5a.close();

            }  // end of IF individual day

            if ( recurr.equalsIgnoreCase( "every day" ) ) {   // if everyday

               PreparedStatement pstmt1a = con.prepareStatement (
                 "UPDATE teecurr2 SET blocker = ? WHERE auto_blocked = 0 AND " +
                 "date >= ? AND date <= ? AND time >= ? AND " +
                 "time <= ? AND event_type != 1 AND courseName = ? AND player1 = ''");

               PreparedStatement pstmt1ab = con.prepareStatement (
                 "UPDATE teecurr2 SET blocker = ? WHERE auto_blocked = 0 AND " +
                 "date >= ? AND date <= ? AND time >= ? AND " +
                 "time <= ? AND event_type != 1 AND courseName = ? AND fb = ? AND player1 = ''");

               if (sfb.equals( "Both" )) {

                  pstmt1a.clearParameters();               // clear the parms
                  pstmt1a.setString(1, name);              // put the parms in stmt
                  pstmt1a.setLong(2, sdate);
                  pstmt1a.setLong(3, edate);
                  pstmt1a.setInt(4, stime);
                  pstmt1a.setInt(5, etime);
                  pstmt1a.setString(6, courseName);
                  count = pstmt1a.executeUpdate();         // execute the prepared stmt

               } else {

                  pstmt1ab.clearParameters();               // clear the parms
                  pstmt1ab.setString(1, name);              // put the parms in stmt
                  pstmt1ab.setLong(2, sdate);
                  pstmt1ab.setLong(3, edate);
                  pstmt1ab.setInt(4, stime);
                  pstmt1ab.setInt(5, etime);
                  pstmt1ab.setString(6, courseName);
                  pstmt1ab.setInt(7, fb);
                  count = pstmt1ab.executeUpdate();         // execute the prepared stmt
               }
               pstmt1ab.close();
               pstmt1a.close();

            }  // end of IF every day

            if ( recurr.equalsIgnoreCase( "all weekdays" ) ) {   // if Monday through Friday

               PreparedStatement pstmt3a = con.prepareStatement (
                 "UPDATE teecurr2 SET blocker = ? WHERE auto_blocked = 0 AND " +
                 "date >= ? AND date <= ? AND day != 'Saturday'  " +
                 "AND day != 'Sunday' AND time >= ? AND " +
                 "time <= ? AND event_type != 1 AND courseName = ? AND player1 = ''");

               PreparedStatement pstmt3ab = con.prepareStatement (
                 "UPDATE teecurr2 SET blocker = ? WHERE auto_blocked = 0 AND " +
                 "date >= ? AND date <= ? AND day != 'Saturday'  " +
                 "AND day != 'Sunday' AND time >= ? AND " +
                 "time <= ? AND event_type != 1 AND courseName = ? AND fb = ? AND player1 = ''");

               if (sfb.equals( "Both" )) {

                  pstmt3a.clearParameters();               // clear the parms
                  pstmt3a.setString(1, name);              // put the parms in stmt
                  pstmt3a.setLong(2, sdate);
                  pstmt3a.setLong(3, edate);
                  pstmt3a.setInt(4, stime);
                  pstmt3a.setInt(5, etime);
                  pstmt3a.setString(6, courseName);
                  count = pstmt3a.executeUpdate();         // execute the prepared stmt

               } else {

                  pstmt3ab.clearParameters();               // clear the parms
                  pstmt3ab.setString(1, name);              // put the parms in stmt
                  pstmt3ab.setLong(2, sdate);
                  pstmt3ab.setLong(3, edate);
                  pstmt3ab.setInt(4, stime);
                  pstmt3ab.setInt(5, etime);
                  pstmt3ab.setString(6, courseName);
                  pstmt3ab.setInt(7, fb);
                  count = pstmt3ab.executeUpdate();         // execute the prepared stmt
               }
               pstmt3ab.close();
               pstmt3a.close();

            }  // end of IF all weekdays

            if ( recurr.equalsIgnoreCase( "all weekends" ) ) {   // if Saturday & Sunday

               PreparedStatement pstmt4a = con.prepareStatement (
                 "UPDATE teecurr2 SET blocker = ? WHERE auto_blocked = 0 AND " +
                 "date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                 "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                 "AND time >= ? AND time <= ? AND event_type != 1 AND courseName = ? AND player1 = ''");

               PreparedStatement pstmt4ab = con.prepareStatement (
                 "UPDATE teecurr2 SET blocker = ? WHERE auto_blocked = 0 AND " +
                 "date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                 "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                 "AND time >= ? AND time <= ? AND event_type != 1 AND courseName = ? AND fb = ? AND player1 = ''");

               if (sfb.equals( "Both" )) {

                  pstmt4a.clearParameters();               // clear the parms
                  pstmt4a.setString(1, name);              // put the parms in stmt
                  pstmt4a.setLong(2, sdate);
                  pstmt4a.setLong(3, edate);
                  pstmt4a.setInt(4, stime);
                  pstmt4a.setInt(5, etime);
                  pstmt4a.setString(6, courseName);
                  count = pstmt4a.executeUpdate();         // execute the prepared stmt

               } else {

                  pstmt4ab.clearParameters();               // clear the parms
                  pstmt4ab.setString(1, name);              // put the parms in stmt
                  pstmt4ab.setLong(2, sdate);
                  pstmt4ab.setLong(3, edate);
                  pstmt4ab.setInt(4, stime);
                  pstmt4ab.setInt(5, etime);
                  pstmt4ab.setString(6, courseName);
                  pstmt4ab.setInt(7, fb);
                  count = pstmt4ab.executeUpdate();         // execute the prepared stmt
               }
               pstmt4ab.close();
               pstmt4a.close();

            }  // end of IF all weekends

         }  // end of IF courseName=ALL

      }               // end of while for restriction table
      pstmtx.close();
   }
   catch (Exception e20) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg20 = "Error20 in SystemUtils do1Blocker: ";
      errorMsg20 = errorMsg20 + e20.getMessage();                                 // build error msg

      logError(errorMsg20);                                       // log it
   }

 }  // end of do1Blocker


 //************************************************************************
 // doLotteries - Scans the Lottery tables for
 //               new/modified entries and updates teecurr2 if necessary.
 //
 //
 //   called by:  Proshop_updateTeecurr
 //
 //************************************************************************

 public static void doLotteries(Connection con) {


   Statement stmt = null;
   ResultSet rs = null;

   //
   //****************************************************************
   //  Get the active lotteries and process them
   //****************************************************************
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("" +
              "SELECT name " +
              "FROM lottery3 " +
              "WHERE edate >= DATE_FORMAT(now(), '%Y%m%d')");

      while (rs.next()) {

         //
         //  go process the single lottery
         //
         do1Lottery(con, rs.getString(1));

      }

   } catch (Exception exc) {

      logError("Error18 in SystemUtils doLottery: "+ exc.getMessage());

   } finally {

      try { rs.close(); }
      catch (Exception ignore) {}

      try { stmt.close(); }
      catch (Exception ignore) {}

   }

 }  // end of doLotteries


 //************************************************************************
 // do1Lottery - Update teecurr for this lottery
 //
 //   called by:  Proshop_addlottery
 //                 "    _editlott
 //               doLotteries (above)
 //
 //************************************************************************

 public static void do1Lottery(Connection con, String name) {


   Statement stmt = null;
   Statement stmt2 = null;
   ResultSet rs = null;


   long date = 0;                 // table variables
   int stime = 0;
   int etime = 0;
   int type = 0;

   long sdate = 0;
   long edate = 0;
   String recurr = "";
   String day = "";

   int count = 0;
   int fb = 0;

   String event_name = "";        // teecurr2 table variables
   String event_color = "";
   String def = "default";

   String course = "";           // common variables
   String courseName = "";
   String sfb = "";
   String omit = "";
   String color = "";

   //
   //****************************************************************
   //  Get the lottery and process teecurr
   //****************************************************************
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
              "SELECT sdate, stime, edate, etime, recurr, color, courseName, fb FROM lottery3 WHERE name = ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setString(1, name);       // put the parm in stmt
      rs = pstmt1.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         sdate = rs.getLong(1);
         stime = rs.getInt(2);
         edate = rs.getLong(3);
         etime = rs.getInt(4);
         recurr = rs.getString(5);
         color = rs.getString(6);
         courseName = rs.getString(7);
         sfb = rs.getString(8);

         day = "";    // init day value

         //
         //  Put lottery info in teecurr2 slots if matching date/time and lottery not already there
         //  (this is done based on recurrence specified in lottery)
         //
         if ( recurr.equalsIgnoreCase( "every sunday" ) ) {   // if every Sunday

            day = "Sunday";
         }

         if ( recurr.equalsIgnoreCase( "every monday" ) ) {   // if every Monday

            day = "Monday";
         }

         if ( recurr.equalsIgnoreCase( "every tuesday" ) ) {   // if every Tuesday

            day = "Tuesday";
         }

         if ( recurr.equalsIgnoreCase( "every wednesday" ) ) {   // if every Wednesday

            day = "Wednesday";
         }

         if ( recurr.equalsIgnoreCase( "every thursday" ) ) {   // if every Thursday

            day = "Thursday";
         }

         if ( recurr.equalsIgnoreCase( "every friday" ) ) {   // if every Friday

            day = "Friday";
         }

         if ( recurr.equalsIgnoreCase( "every saturday" ) ) {   // if every Satruday

            day = "Saturday";
         }

         //
         //  prepare F/B indicator
         //
         fb = 0;                       // init to Front

         if (sfb.equals( "Back" )) {

            fb = 1;     // back
         }

         //
         //   courseName = '-ALL-', or name of course, or null
         //
         //   sfb = 'Front', 'Back', or 'Both'
         //
         if (courseName.equals( "-ALL-" )) {    // if ALL Courses

            if (!day.equals( "" )) {    // if an individual day

               try {

                  PreparedStatement pstmt52 = con.prepareStatement (
                    "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND lottery = ? AND lottery_color = ? ");

                  PreparedStatement pstmt55 = con.prepareStatement (
                    "UPDATE teecurr2 SET lottery = ?, lottery_color = ? " +
                    "WHERE ((date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ?) AND (lottery_color = ? OR lottery_color = ?))");

                  PreparedStatement pstmt52b = con.prepareStatement (
                    "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND lottery = ? AND lottery_color = ? AND fb = ?");

                  PreparedStatement pstmt55b = con.prepareStatement (
                    "UPDATE teecurr2 SET lottery = ?, lottery_color = ? " +
                    "WHERE ((date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND fb = ?) AND (lottery_color = ? OR lottery_color = ?))");

                  //
                  //   If lottery color is not 'default' and a rest exists in teecurr2 that is default, override
                  //
                  if (color.equalsIgnoreCase("default")) {    // if default color - only add if no other rest

                     if (sfb.equals( "Both" )) {

                        pstmt52.clearParameters();               // clear the parms
                        pstmt52.setString(1, name);              // put the parms in stmt
                        pstmt52.setString(2, color);
                        pstmt52.setLong(3, sdate);
                        pstmt52.setLong(4, edate);
                        pstmt52.setString(5, day);
                        pstmt52.setInt(6, stime);
                        pstmt52.setInt(7, etime);
                        pstmt52.setString(8, omit);
                        pstmt52.setString(9, omit);
                        count = pstmt52.executeUpdate();         // execute the prepared stmt

                     } else {

                        pstmt52b.clearParameters();               // clear the parms
                        pstmt52b.setString(1, name);              // put the parms in stmt
                        pstmt52b.setString(2, color);
                        pstmt52b.setLong(3, sdate);
                        pstmt52b.setLong(4, edate);
                        pstmt52b.setString(5, day);
                        pstmt52b.setInt(6, stime);
                        pstmt52b.setInt(7, etime);
                        pstmt52b.setString(8, omit);
                        pstmt52b.setString(9, omit);
                        pstmt52b.setInt(10, fb);
                        count = pstmt52b.executeUpdate();         // execute the prepared stmt
                     }

                  } else {

                     if (sfb.equals( "Both" )) {

                        pstmt55.clearParameters();               // clear the parms
                        pstmt55.setString(1, name);              // put the parms in stmt
                        pstmt55.setString(2, color);
                        pstmt55.setLong(3, sdate);
                        pstmt55.setLong(4, edate);
                        pstmt55.setString(5, day);
                        pstmt55.setInt(6, stime);
                        pstmt55.setInt(7, etime);
                        pstmt55.setString(8, def);
                        pstmt55.setString(9, omit);
                        count = pstmt55.executeUpdate();         // execute the prepared stmt

                     } else {

                        pstmt55b.clearParameters();               // clear the parms
                        pstmt55b.setString(1, name);              // put the parms in stmt
                        pstmt55b.setString(2, color);
                        pstmt55b.setLong(3, sdate);
                        pstmt55b.setLong(4, edate);
                        pstmt55b.setString(5, day);
                        pstmt55b.setInt(6, stime);
                        pstmt55b.setInt(7, etime);
                        pstmt55b.setInt(8, fb);
                        pstmt55b.setString(9, def);
                        pstmt55b.setString(10, omit);
                        count = pstmt55b.executeUpdate();         // execute the prepared stmt
                     }
                  }

                  pstmt52.close();
                  pstmt55.close();
                  pstmt52b.close();
                  pstmt55b.close();
               }
               catch (Exception e10) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg10 = "Error10 in SystemUtils do1Lottery: ";
                  errorMsg10 = errorMsg10 + e10.getMessage();                                 // build error msg

                  logError(errorMsg10);                                       // log it
               }

            }  // end of IF individual day

            if ( recurr.equalsIgnoreCase( "every day" )) {   // if everyday

               try {

                  if (sfb.equals( "Both" )) {

                     PreparedStatement pstmt51 = con.prepareStatement (
                       "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE " +
                       "date >= ? AND date <= ? AND time >= ? AND " +
                       "time <= ? AND lottery = ? AND lottery_color = ? ");

                     pstmt51.clearParameters();               // clear the parms
                     pstmt51.setString(1, name);              // put the parms in stmt
                     pstmt51.setString(2, color);
                     pstmt51.setLong(3, sdate);
                     pstmt51.setLong(4, edate);
                     pstmt51.setInt(5, stime);
                     pstmt51.setInt(6, etime);
                     pstmt51.setString(7, omit);
                     pstmt51.setString(8, omit);
                     count = pstmt51.executeUpdate();         // execute the prepared stmt

                     pstmt51.close();

                  } else {

                     PreparedStatement pstmt51b = con.prepareStatement (
                       "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE " +
                       "date >= ? AND date <= ? AND time >= ? AND " +
                       "time <= ? AND lottery = ? AND lottery_color = ? AND fb = ?");

                     pstmt51b.clearParameters();               // clear the parms
                     pstmt51b.setString(1, name);              // put the parms in stmt
                     pstmt51b.setString(2, color);
                     pstmt51b.setLong(3, sdate);
                     pstmt51b.setLong(4, edate);
                     pstmt51b.setInt(5, stime);
                     pstmt51b.setInt(6, etime);
                     pstmt51b.setString(7, omit);
                     pstmt51b.setString(8, omit);
                     pstmt51b.setInt(9, fb);
                     count = pstmt51b.executeUpdate();         // execute the prepared stmt

                     pstmt51b.close();
                  }
               }
               catch (Exception e11) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg11 = "Error11 in SystemUtils do1Lottery: ";
                  errorMsg11 = errorMsg11 + e11.getMessage();                                 // build error msg

                  logError(errorMsg11);                                       // log it
               }
            }  // end of IF every day

            if ( recurr.equalsIgnoreCase( "all weekdays" ) ) {   // if Monday through Friday

               try {

                  if (sfb.equals( "Both" )) {

                     PreparedStatement pstmt53 = con.prepareStatement (
                       "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE " +
                       "date >= ? AND date <= ? AND day != 'Saturday'  " +
                       "AND day != 'Sunday' AND time >= ? AND " +
                       "time <= ? AND lottery = ? AND lottery_color = ? ");

                     pstmt53.clearParameters();               // clear the parms
                     pstmt53.setString(1, name);              // put the parms in stmt
                     pstmt53.setString(2, color);
                     pstmt53.setLong(3, sdate);
                     pstmt53.setLong(4, edate);
                     pstmt53.setInt(5, stime);
                     pstmt53.setInt(6, etime);
                     pstmt53.setString(7, omit);
                     pstmt53.setString(8, omit);
                     count = pstmt53.executeUpdate();         // execute the prepared stmt

                     pstmt53.close();

                  } else {

                     PreparedStatement pstmt53b = con.prepareStatement (
                       "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE " +
                       "date >= ? AND date <= ? AND day != 'Saturday'  " +
                       "AND day != 'Sunday' AND time >= ? AND " +
                       "time <= ? AND lottery = ? AND lottery_color = ? AND fb = ?");

                     pstmt53b.clearParameters();               // clear the parms
                     pstmt53b.setString(1, name);              // put the parms in stmt
                     pstmt53b.setString(2, color);
                     pstmt53b.setLong(3, sdate);
                     pstmt53b.setLong(4, edate);
                     pstmt53b.setInt(5, stime);
                     pstmt53b.setInt(6, etime);
                     pstmt53b.setString(7, omit);
                     pstmt53b.setString(8, omit);
                     pstmt53b.setInt(9, fb);
                     count = pstmt53b.executeUpdate();         // execute the prepared stmt

                     pstmt53b.close();
                  }
               }
               catch (Exception e12) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg12 = "Error12 in SystemUtils do1Lottery: ";
                  errorMsg12 = errorMsg12 + e12.getMessage();                                 // build error msg

                  logError(errorMsg12);                                       // log it
               }

            }  // end of IF all weekdays

            if ( recurr.equalsIgnoreCase( "all weekends" ) ) {   // if Saturday & Sunday

               try {

                  if (sfb.equals( "Both" )) {

                     PreparedStatement pstmt54 = con.prepareStatement (
                       "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE " +
                       "date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                       "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                       "AND time >= ? AND time <= ? AND " +
                       "lottery = ? AND lottery_color = ? ");

                     pstmt54.clearParameters();               // clear the parms
                     pstmt54.setString(1, name);              // put the parms in stmt
                     pstmt54.setString(2, color);
                     pstmt54.setLong(3, sdate);
                     pstmt54.setLong(4, edate);
                     pstmt54.setInt(5, stime);
                     pstmt54.setInt(6, etime);
                     pstmt54.setString(7, omit);
                     pstmt54.setString(8, omit);
                     count = pstmt54.executeUpdate();         // execute the prepared stmt

                     pstmt54.close();

                  } else {

                     PreparedStatement pstmt54b = con.prepareStatement (
                       "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE " +
                       "date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                       "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                       "AND time >= ? AND time <= ? AND " +
                       "lottery = ? AND lottery_color = ? AND fb = ?");

                     pstmt54b.clearParameters();               // clear the parms
                     pstmt54b.setString(1, name);              // put the parms in stmt
                     pstmt54b.setString(2, color);
                     pstmt54b.setLong(3, sdate);
                     pstmt54b.setLong(4, edate);
                     pstmt54b.setInt(5, stime);
                     pstmt54b.setInt(6, etime);
                     pstmt54b.setString(7, omit);
                     pstmt54b.setString(8, omit);
                     pstmt54b.setInt(9, fb);
                     count = pstmt54b.executeUpdate();         // execute the prepared stmt

                     pstmt54b.close();
                  }
               }
               catch (Exception e13) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg13 = "Error13 in SystemUtils do1Lottery: ";
                  errorMsg13 = errorMsg13 + e13.getMessage();                                 // build error msg

                  logError(errorMsg13);                                       // log it
               }

            }  // end of IF all weekends

         } else {     // courseName != ALL

            if (!day.equals( "" )) {    // if an individual day

               try {

                  PreparedStatement pstmt52a = con.prepareStatement (
                    "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND lottery = ? AND lottery_color = ? AND courseName = ? ");

                  PreparedStatement pstmt55a = con.prepareStatement (
                    "UPDATE teecurr2 SET lottery = ?, lottery_color = ? " +
                    "WHERE ((date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND courseName = ?) AND (lottery_color = ? OR lottery_color = ?))");

                  PreparedStatement pstmt52ab = con.prepareStatement (
                    "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND lottery = ? AND lottery_color = ? AND courseName = ? AND fb = ?");

                  PreparedStatement pstmt55ab = con.prepareStatement (
                    "UPDATE teecurr2 SET lottery = ?, lottery_color = ? " +
                    "WHERE ((date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND courseName = ? AND fb = ?) AND (lottery_color = ? OR lottery_color = ?))");

                  //
                  //   If lottery color is not 'default' and a rest exists in teecurr2 that is default, override
                  //
                  if (color.equalsIgnoreCase("default")) {    // if default color - only add if no other rest

                     if (sfb.equals( "Both" )) {

                        pstmt52a.clearParameters();               // clear the parms
                        pstmt52a.setString(1, name);              // put the parms in stmt
                        pstmt52a.setString(2, color);
                        pstmt52a.setLong(3, sdate);
                        pstmt52a.setLong(4, edate);
                        pstmt52a.setString(5, day);
                        pstmt52a.setInt(6, stime);
                        pstmt52a.setInt(7, etime);
                        pstmt52a.setString(8, omit);
                        pstmt52a.setString(9, omit);
                        pstmt52a.setString(10, courseName);
                        count = pstmt52a.executeUpdate();         // execute the prepared stmt

                     } else {

                        pstmt52ab.clearParameters();               // clear the parms
                        pstmt52ab.setString(1, name);              // put the parms in stmt
                        pstmt52ab.setString(2, color);
                        pstmt52ab.setLong(3, sdate);
                        pstmt52ab.setLong(4, edate);
                        pstmt52ab.setString(5, day);
                        pstmt52ab.setInt(6, stime);
                        pstmt52ab.setInt(7, etime);
                        pstmt52ab.setString(8, omit);
                        pstmt52ab.setString(9, omit);
                        pstmt52ab.setString(10, courseName);
                        pstmt52ab.setInt(11, fb);
                        count = pstmt52ab.executeUpdate();         // execute the prepared stmt
                     }
                  } else {

                     if (sfb.equals( "Both" )) {

                        pstmt55a.clearParameters();               // clear the parms
                        pstmt55a.setString(1, name);              // put the parms in stmt
                        pstmt55a.setString(2, color);
                        pstmt55a.setLong(3, sdate);
                        pstmt55a.setLong(4, edate);
                        pstmt55a.setString(5, day);
                        pstmt55a.setInt(6, stime);
                        pstmt55a.setInt(7, etime);
                        pstmt55a.setString(8, courseName);
                        pstmt55a.setString(9, def);
                        pstmt55a.setString(10, omit);
                        count = pstmt55a.executeUpdate();         // execute the prepared stmt

                     } else {

                        pstmt55ab.clearParameters();               // clear the parms
                        pstmt55ab.setString(1, name);              // put the parms in stmt
                        pstmt55ab.setString(2, color);
                        pstmt55ab.setLong(3, sdate);
                        pstmt55ab.setLong(4, edate);
                        pstmt55ab.setString(5, day);
                        pstmt55ab.setInt(6, stime);
                        pstmt55ab.setInt(7, etime);
                        pstmt55ab.setString(8, courseName);
                        pstmt55ab.setInt(9, fb);
                        pstmt55ab.setString(10, def);
                        pstmt55ab.setString(11, omit);
                        count = pstmt55ab.executeUpdate();         // execute the prepared stmt
                     }
                  }

                  pstmt52a.close();
                  pstmt55a.close();
                  pstmt52ab.close();
                  pstmt55ab.close();
               }
               catch (Exception e14) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg14 = "Error14 in SystemUtils do1Lottery: ";
                  errorMsg14 = errorMsg14 + e14.getMessage();                                 // build error msg

                  logError(errorMsg14);                                       // log it
               }

            }  // end of IF individual day

            if ( recurr.equalsIgnoreCase( "every day" ) ) {   // if everyday

               try {
                  PreparedStatement pstmt51a = con.prepareStatement (
                    "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE " +
                    "date >= ? AND date <= ? AND time >= ? AND " +
                    "time <= ? AND lottery = ? AND lottery_color = ? AND courseName = ?");

                  PreparedStatement pstmt51ab = con.prepareStatement (
                    "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE " +
                    "date >= ? AND date <= ? AND time >= ? AND " +
                    "time <= ? AND lottery = ? AND lottery_color = ? AND courseName = ? AND fb = ?");

                  if (sfb.equals( "Both" )) {

                     pstmt51a.clearParameters();               // clear the parms
                     pstmt51a.setString(1, name);              // put the parms in stmt
                     pstmt51a.setString(2, color);
                     pstmt51a.setLong(3, sdate);
                     pstmt51a.setLong(4, edate);
                     pstmt51a.setInt(5, stime);
                     pstmt51a.setInt(6, etime);
                     pstmt51a.setString(7, omit);
                     pstmt51a.setString(8, omit);
                     pstmt51a.setString(9, courseName);
                     count = pstmt51a.executeUpdate();         // execute the prepared stmt

                  } else {

                     pstmt51ab.clearParameters();               // clear the parms
                     pstmt51ab.setString(1, name);              // put the parms in stmt
                     pstmt51ab.setString(2, color);
                     pstmt51ab.setLong(3, sdate);
                     pstmt51ab.setLong(4, edate);
                     pstmt51ab.setInt(5, stime);
                     pstmt51ab.setInt(6, etime);
                     pstmt51ab.setString(7, omit);
                     pstmt51ab.setString(8, omit);
                     pstmt51ab.setString(9, courseName);
                     pstmt51ab.setInt(10, fb);
                     count = pstmt51ab.executeUpdate();         // execute the prepared stmt
                  }
                  pstmt51a.close();
                  pstmt51ab.close();
               }
               catch (Exception e15) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg15 = "Error15 in SystemUtils do1Lottery: ";
                  errorMsg15 = errorMsg15 + e15.getMessage();                                 // build error msg

                  logError(errorMsg15);                                       // log it
               }

            }  // end of IF every day

            if ( recurr.equalsIgnoreCase( "all weekdays" ) ) {   // if Monday through Friday

               try {
                  PreparedStatement pstmt53a = con.prepareStatement (
                    "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day != 'Saturday'  " +
                    "AND day != 'Sunday' AND time >= ? AND " +
                    "time <= ? AND lottery = ? AND lottery_color = ? AND courseName = ?");

                  PreparedStatement pstmt53ab = con.prepareStatement (
                    "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day != 'Saturday'  " +
                    "AND day != 'Sunday' AND time >= ? AND " +
                    "time <= ? AND lottery = ? AND lottery_color = ? AND courseName = ? AND fb = ?");

                  if (sfb.equals( "Both" )) {

                     pstmt53a.clearParameters();               // clear the parms
                     pstmt53a.setString(1, name);              // put the parms in stmt
                     pstmt53a.setString(2, color);
                     pstmt53a.setLong(3, sdate);
                     pstmt53a.setLong(4, edate);
                     pstmt53a.setInt(5, stime);
                     pstmt53a.setInt(6, etime);
                     pstmt53a.setString(7, omit);
                     pstmt53a.setString(8, omit);
                     pstmt53a.setString(9, courseName);
                     count = pstmt53a.executeUpdate();         // execute the prepared stmt

                  } else {

                     pstmt53ab.clearParameters();               // clear the parms
                     pstmt53ab.setString(1, name);              // put the parms in stmt
                     pstmt53ab.setString(2, color);
                     pstmt53ab.setLong(3, sdate);
                     pstmt53ab.setLong(4, edate);
                     pstmt53ab.setInt(5, stime);
                     pstmt53ab.setInt(6, etime);
                     pstmt53ab.setString(7, omit);
                     pstmt53ab.setString(8, omit);
                     pstmt53ab.setString(9, courseName);
                     pstmt53ab.setInt(10, fb);
                     count = pstmt53ab.executeUpdate();         // execute the prepared stmt
                  }
                  pstmt53a.close();
                  pstmt53ab.close();
               }
               catch (Exception e16) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg16 = "Error16 in SystemUtils do1Lottery: ";
                  errorMsg16 = errorMsg16 + e16.getMessage();                                 // build error msg

                  logError(errorMsg16);                                       // log it
               }

            }  // end of IF all weekdays

            if ( recurr.equalsIgnoreCase( "all weekends" ) ) {   // if Saturday & Sunday

               try {
                  PreparedStatement pstmt54a = con.prepareStatement (
                    "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                    "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                    "AND time >= ? AND time <= ? AND " +
                    "lottery = ? AND lottery_color = ? AND courseName = ?");

                  PreparedStatement pstmt54ab = con.prepareStatement (
                    "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                    "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                    "AND time >= ? AND time <= ? AND " +
                    "lottery = ? AND lottery_color = ? AND courseName = ? AND fb = ?");

                  if (sfb.equals( "Both" )) {

                     pstmt54a.clearParameters();               // clear the parms
                     pstmt54a.setString(1, name);              // put the parms in stmt
                     pstmt54a.setString(2, color);
                     pstmt54a.setLong(3, sdate);
                     pstmt54a.setLong(4, edate);
                     pstmt54a.setInt(5, stime);
                     pstmt54a.setInt(6, etime);
                     pstmt54a.setString(7, omit);
                     pstmt54a.setString(8, omit);
                     pstmt54a.setString(9, courseName);
                     count = pstmt54a.executeUpdate();         // execute the prepared stmt

                  } else {

                     pstmt54ab.clearParameters();               // clear the parms
                     pstmt54ab.setString(1, name);              // put the parms in stmt
                     pstmt54ab.setString(2, color);
                     pstmt54ab.setLong(3, sdate);
                     pstmt54ab.setLong(4, edate);
                     pstmt54ab.setInt(5, stime);
                     pstmt54ab.setInt(6, etime);
                     pstmt54ab.setString(7, omit);
                     pstmt54ab.setString(8, omit);
                     pstmt54ab.setString(9, courseName);
                     pstmt54ab.setInt(10, fb);
                     count = pstmt54ab.executeUpdate();         // execute the prepared stmt
                  }

                  pstmt54a.close();
                  pstmt54ab.close();
               }
               catch (Exception e17) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg17 = "Error17 in SystemUtils do1Lottery: ";
                  errorMsg17 = errorMsg17 + e17.getMessage();                                 // build error msg

                  logError(errorMsg17);                                       // log it
               }

            }  // end of IF all weekends

         }  // end of IF courseName=ALL

      }               // end of while for lottery table
      pstmt1.close();
   }
   catch (Exception e18) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg18 = "Error18 in SystemUtils do1Lottery: ";
      errorMsg18 = errorMsg18 + e18.getMessage();                                 // build error msg

      logError(errorMsg18);                                       // log it
   }

 }  // end of do1Lottery


 //************************************************************************
 // getLottId - get the next sequential lottery id from club table
 //
 //   called by:  Proshop_lott
 //               Member_lott
 //
 //************************************************************************

 public static long getLottId(Connection con) {


   Statement stmtm = null;
   ResultSet rs = null;

   long id = 0;

   //
   //   Get last lottery id used for this club
   //
   try {

      stmtm = con.createStatement();        // create a statement

      rs = stmtm.executeQuery("SELECT lottid " +
                             "FROM club5 WHERE clubName != ''");

      if (rs.next()) {

         id = rs.getLong(1);

      }
      stmtm.close();

      id++;                  // determine next value

      if (id <= 0) {

         id = 1;             // start over if we wrapped
      }

      //
      //  save the new value
      //
      PreparedStatement pstmt = con.prepareStatement (
         "UPDATE club5 SET lottid = ?");

      pstmt.clearParameters();            // clear the parms
      pstmt.setLong(1, id);

      pstmt.executeUpdate();  // execute the prepared stmt

      pstmt.close();
   }
   catch (Exception ignore) {

      id = 0;   // indicate failed
   }
   return(id);
 }


 //************************************************************************
 // inactTimer - Process 2 minute system timer expiration.  This timer is set
 //              at init time by Login and reset every 2 minutes so we
 //              can check for tee time slots that have been held on to for
 //              too long (more than 6 minutes).  Members' sessions timeout
 //              after 15 minutes and they could leave a tee slot hanging.
 //
 //              Also, check evntsup entries (event sign up table) and
 //              the lottery requests.
 //
 //   called by:  minTimer on timer expiration
 //
 //************************************************************************

 public static void inactTimer() {


   Connection con2 = null;
   Statement stmt2 = null;
   ResultSet rs2 = null;

   int count = 0;
   int use = 0;
   int fb = 0;
   int time = 0;
   long date = 0;

   String club = "";
   String course = "";
   String name = "";
   String errorMsg = "";

   boolean tooShort = false;

   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };


   //
   //  always reset the 2 minute timer immediately
   //
   minTimer t_timer = new minTimer();


   //
   //  This must be the master server!!!  If not, let the timer run in case master goes down.
   //
   if (Common_Server.SERVER_ID == TIMER_SERVER) {

      //
      //  Set the date/time when this timer should expire by next (safety check to ensure timers run)
      //
      Calendar cal = new GregorianCalendar();   // get todays date

      cal.add(Calendar.MINUTE,4);               // roll ahead 4 minutes to give plenty of time

      long yy = cal.get(Calendar.YEAR);
      long mm = cal.get(Calendar.MONTH) + 1;
      long dd = cal.get(Calendar.DAY_OF_MONTH);
      long hh = cal.get(Calendar.HOUR_OF_DAY);  // get 24 hr clock value
      long mn = cal.get(Calendar.MINUTE);

      //
      //  Set next expiration safety net (yyyymmddhhmm) - timer should expire BEFORE this time!!
      //
      min2Time = (yy * 100000000) + (mm * 1000000) + (dd * 10000) + (hh * 100) + mn;   // save date/time stamp


      //
      //  Now verify that the 60 minute timer is running
      //
      cal = new GregorianCalendar();            // get current date & time

      yy = cal.get(Calendar.YEAR);
      mm = cal.get(Calendar.MONTH) + 1;
      dd = cal.get(Calendar.DAY_OF_MONTH);
      hh = cal.get(Calendar.HOUR_OF_DAY);
      mn = cal.get(Calendar.MINUTE);

      //
      //  create date & time stamp value (yyyymmddhhmm) for compares
      //
      long currTime = (yy * 100000000) + (mm * 1000000) + (dd * 10000) + (hh * 100) + mn;   // form date/time stamp

      if (currTime > min60Time && min60Time > 0) {       // if 60 min timer did not fire when expected

         min60Timer t4_timer = new min60Timer();         // reset the 60 min timer - t2_timer

         //
         //  log this so we can track how often it must be reset
         //
         errorMsg = "inactTimer: Had to reset the 60 min timer. currTime=" +currTime+ ", min60Time=" +min60Time;                                       // log it

         //
         //  set new 'expected timeout' value so we don't do this again in 2 minutes
         //
         cal = new GregorianCalendar();         // get current date & time

         cal.add(Calendar.MINUTE,90);           // roll ahead 90 minutes to give plenty of time

         yy = cal.get(Calendar.YEAR);
         mm = cal.get(Calendar.MONTH) +1;
         dd = cal.get(Calendar.DAY_OF_MONTH);
         hh = cal.get(Calendar.HOUR_OF_DAY);    // get 24 hr clock value
         mn = cal.get(Calendar.MINUTE);

         min60Time = (yy * 100000000) + (mm * 1000000) + (dd * 10000) + (hh * 100) + mn;   // set new date/time for 60 min timer

         logError(errorMsg + ", new min60Time="+min60Time);

         // DEBUG
         if (min60Time < 0) logError("ERROR: min60Time="+min60Time+" (inactTimer)");
      }


      //
      //  Now verify that the other 2 minute timer is running
      //
      if (currTime > min2Time2 && min2Time2 > 0) {            // if 2 min timer did not fire when expected

         // reset the 2 min timer & log this so we can track how often it's being reset
         minTimer2 t_timer2 = new minTimer2();

         //
         //  log this so we can track how often it must be reset
         //
         errorMsg = "inactTimer: Had to reset the other 2 min timer. currTime=" +currTime+ ", min2Time2=" +min2Time2;                                       // log it

         //
         //  set new 'expected timeout' value so we don't do this again in 2 minutes
         //
         cal = new GregorianCalendar();         // get current date & time

         cal.add(Calendar.MINUTE,4);           // roll ahead 4 minutes to give plenty of time

         yy = cal.get(Calendar.YEAR);
         mm = cal.get(Calendar.MONTH) +1;
         dd = cal.get(Calendar.DAY_OF_MONTH);
         hh = cal.get(Calendar.HOUR_OF_DAY);    // get 24 hr clock value
         mn = cal.get(Calendar.MINUTE);

         min2Time2 = (yy * 100000000) + (mm * 1000000) + (dd * 10000) + (hh * 100) + mn;   // set new date/time for 60 min timer

         logError(errorMsg + ", new min2Time2="+min2Time2);
      }


      //
      // Get the club names from the 'clubs' table
      //
      //  Process each club in the table
      //
      try {

         //
         //  Get today's date and the current time for lottery processing
         //
         cal = new GregorianCalendar();         // get todays date

         int year = cal.get(Calendar.YEAR);
         int month = cal.get(Calendar.MONTH);
         int day = cal.get(Calendar.DAY_OF_MONTH);
         int hr = cal.get(Calendar.HOUR_OF_DAY);        // get 24 hr clock value
         int min = cal.get(Calendar.MINUTE);
         int sec = cal.get(Calendar.SECOND);
         int day_num = cal.get(Calendar.DAY_OF_WEEK);   // day of week (01 - 07)

         String day_name = day_table[day_num];          // get name for day

         month = month + 1;                             // month starts at zero

         date = (year * 10000) + (month * 100) + day;   // create a date field of yyyymmdd

         //
         //  do not adjust the time!!!  pdate/ptime in actlott3 have already been adjusted to central time!!!
         //
         time = (hr * 100) + min;                       // create time field of hhmm

         //
         //  Verify that at least 2 minutes passed since the last expiration
         //
         // tooShort = verifyTimer(hr, min, sec);

         if (tooShort == false) {        // if timer lenght was ok

            //
            //  Process each club
            //
            con2 = dbConn.Connect( rev );

            if (con2 != null) {

               stmt2 = con2.createStatement();          // create a statement

               rs2 = stmt2.executeQuery("SELECT clubname FROM clubs WHERE clubname != '' AND inactive = 0 ORDER BY clubname DESC");

               while (rs2.next()) {                     // process all clubs

                  club = rs2.getString(1);              // get a club name

                  //
                  //  call checkTime to process each club
                  //
                  checkTime(club, date, time, day_name);

               } // end of while for all clubs

               stmt2.close();
               con2.close();

            } else {

               errorMsg = "Error in SystemUtils inactTimer - DB Connection failed.";
               logError(errorMsg);
            }

         } // end if tooShort

      } catch (Exception e2) {

         logError("Error in SystemUtils inactTimer: " + e2.getMessage());                                       // log it

      } finally {        // clean up!!

          try {
             if (rs2 != null) rs2.close();
          } catch (Exception ignore) {}

          rs2 = null;

          try {
             if (stmt2 != null) stmt2.close();
          } catch (Exception ignore) {}

          stmt2 = null;

          try {
             if (con2 != null) con2.close();
          } catch (Exception ignore) {}

          con2 = null;
      }

   } // end if timer server

 }  // end of inactTimer


 //************************************************************************
 // inactTimer2 - Process 2 minute system timer expiration.  This timer is set
 //              at init time by Login and reset every 2 minutes so we
 //              can check for customs and do other processing to off-load
 //              the other 2 minute timer.
 //
 //              This timer is initially set to 3 seconds so it will run
 //              opposite the other 2 min timer.
 //
 //   called by:  minTimer2 on timer expiration
 //
 //************************************************************************

 public static void inactTimer2() {


   Connection con2 = null;
   Statement stmt2 = null;
   ResultSet rs2 = null;

   int count = 0;
   int use = 0;
   int fb = 0;
   int time = 0;
   long date = 0;

   String club = "";
   String course = "";
   String name = "";
   String errorMsg = "";

   boolean tooShort = false;

   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

   //
   //  always reset the 2 minute timer immediately
   //
   minTimer2 t_timer2 = new minTimer2();

   //
   //  This must be the master server!!!  If not, let the timer run in case master goes down.
   //
   if (Common_Server.SERVER_ID == TIMER_SERVER) {

      //
      //  Set the date/time when this timer should expire by next (safety check to ensure timers run)
      //
      Calendar cal = new GregorianCalendar();   // get todays date

      cal.add(Calendar.MINUTE,4);               // roll ahead 4 minutes to give plenty of time

      long yy = cal.get(Calendar.YEAR);
      long mm = cal.get(Calendar.MONTH) + 1;
      long dd = cal.get(Calendar.DAY_OF_MONTH);
      long hh = cal.get(Calendar.HOUR_OF_DAY);  // get 24 hr clock value
      long mn = cal.get(Calendar.MINUTE);

      //
      //  create date & time stamp value (yyyymmddhhmm) for compares - we should expire by this time!!!
      //
      min2Time2 = (yy * 100000000) + (mm * 1000000) + (dd * 10000) + (hh * 100) + mn;   // save date/time stamp

      //
      //  Now verify that the other 2 minute timer is running
      //
      cal = new GregorianCalendar();            // get current date & time

      yy = cal.get(Calendar.YEAR);
      mm = cal.get(Calendar.MONTH) + 1;
      dd = cal.get(Calendar.DAY_OF_MONTH);
      hh = cal.get(Calendar.HOUR_OF_DAY);       // get 24 hr clock value
      mn = cal.get(Calendar.MINUTE);

      //
      //  create date & time stamp value (yyyymmddhhmm) for compares
      //
      long currTime = (yy * 100000000) + (mm * 1000000) + (dd * 10000) + (hh * 100) + mn;   // form date/time stamp

      if (currTime > min2Time && min2Time > 0) {          // if 2 min timer did not fire when expected

         // reset the 2 min timer & log this so we can track how often it's being reset
         minTimer t_timer = new minTimer();

         //
         //  log this so we can track how often it must be reset
         //
         errorMsg = "inactTimer2: Had to reset the 2 min timer. currTime=" +currTime+ ", min2Time=" +min2Time;                                       // log it

         //
         //  set new 'expected timeout' value so we don't do this again in 2 minutes
         //
         cal = new GregorianCalendar();         // get current date & time

         cal.add(Calendar.MINUTE,4);           // roll ahead 4 minutes to give plenty of time

         yy = cal.get(Calendar.YEAR);
         mm = cal.get(Calendar.MONTH) +1;
         dd = cal.get(Calendar.DAY_OF_MONTH);
         hh = cal.get(Calendar.HOUR_OF_DAY);    // get 24 hr clock value
         mn = cal.get(Calendar.MINUTE);

         min2Time = (yy * 100000000) + (mm * 1000000) + (dd * 10000) + (hh * 100) + mn;   // set new date/time for 2 min timer

         logError(errorMsg + ", new min2Time="+min2Time);
      }


      //
      // Get the club names from the 'clubs' table
      //
      //  Process each club in the table
      //
      try {

         //
         //  Get today's date and the current time for lottery processing
         //
         cal = new GregorianCalendar();         // get todays date

         int year = cal.get(Calendar.YEAR);
         int month = cal.get(Calendar.MONTH);
         int day = cal.get(Calendar.DAY_OF_MONTH);
         int hr = cal.get(Calendar.HOUR_OF_DAY);        // get 24 hr clock value
         int min = cal.get(Calendar.MINUTE);
         //int sec = cal.get(Calendar.SECOND);
         int day_num = cal.get(Calendar.DAY_OF_WEEK);   // day of week (01 - 07)

         String day_name = day_table[day_num];          // get name for day

         month = month + 1;                             // month starts at zero

         date = (year * 10000) + (month * 100) + day;   // create a date field of yyyymmdd

         //
         //  do not adjust the time!!!  pdate/ptime in actlott3 have already been adjusted to central time!!!
         //
         time = (hr * 100) + min;                       // create time field of hhmm

         //
         //  Verify that at least 2 minutes passed since the last expiration
         //
         // tooShort = verifyTimer(hr, min, sec);

         if (tooShort == false) {        // if timer length was ok

            //
            //  Process each club
            //
            con2 = dbConn.Connect( rev );

            if (con2 != null) {

               stmt2 = con2.createStatement();          // create a statement

               rs2 = stmt2.executeQuery("SELECT clubname FROM clubs WHERE clubname != '' AND inactive = 0 ORDER BY clubname ASC");

               while (rs2.next()) {                     // process all clubs

                  club = rs2.getString(1);              // get a club name

                  //
                  //  call checkTime to process each club
                  //
                  checkTime2(club, date, time, day_name);

               } // end of while for all clubs

               stmt2.close();
               con2.close();

            } else {

               errorMsg = "Error in SystemUtils inactTimer2 - DB Connection failed.";
               logError(errorMsg);
            }

         } // end if tooShort

      } catch (Exception e2) {

         logError("Error in SystemUtils inactTimer2: " + e2.getMessage());                                       // log it

      } finally {        // clean up!!

          try {
             if (rs2 != null) rs2.close();
          } catch (Exception ignore) {}

          rs2 = null;

          try {
             if (stmt2 != null) stmt2.close();
          } catch (Exception ignore) {}

          stmt2 = null;

          try {
             if (con2 != null) con2.close();
          } catch (Exception ignore) {}

          con2 = null;
      }

   } // end if timer server

 }  // end of inactTimer2


 //************************************************************************
 // checkTime - Process 2 minute system timer expiration from inactTimer.
 //
 //
 //   called by:  inactTimer above
 //
 //         pdate & ptime = today's date & time
 //
 //************************************************************************

 public static void checkTime(String club, long pdate, int ptime, String day_name) {


   Connection con = null;
   Statement stmt = null;
   PreparedStatement pstmt1 = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;

   //int count = 0;
   int use = 0;
   int not_in_use = 0;
   int fb = 0;
   int time = 0;
   int id = 0;
   int max = 3;
   int maxEvent = 6;

   long date = 0;

   int teecurr_id = 0;

   String course = "";
   String name = "";
   //String errorMsg = "";

   //
   //  Process the club passed
   //
   try {

      con = dbConn.Connect(club);                           // get a connection to this club's db

   } catch (Exception e1) {
      //
      //  save error message in /v_x/error.txt
      //
      //errorMsg = "Error in SystemUtils checkTime - cannot get con for " +club+ ": ";
      //errorMsg = errorMsg + e1.getMessage();                                 // build error msg

//      logError(errorMsg);                                       // log it
      return;
   }
/*

   //
   //  Custom (temp) to increase timeout for Aliso Viejo - they have a very slow Internet Connection
   //
   //  THESE ARE NOW ALL IN verifySlot.getSlotHoldTime
   //
   if (club.equals( "alisoviejo" )) {

      max = 8;       // temp until they get new clubhouse (2009??)
   }

   //
   //  Custom to increase timeout for Golf Club of Georgia to 10 mins due to low staffing in golf shop
   //
   if (club.equals("gcgeorgia") || club.equals("sankatyheadgc") || club.equals("medinahcc") || club.equals("talbotcc") || club.equals("tpcboston") || club.equals("mpccpb")) {

      max = 5;
   }

   //  Increase the timeout to 8 minutes
   if (club.equals("wedgewood") || club.equals("tontoverde")) {

      max = 4;
   }

   // Decrease the timeout to 4 minutes for Indian Ridge CC (members are used to online tee times and don't need 6 minutes, according to club).
   if (club.equals("indianridgecc")) {
       
      max = 2;
   }
*/
   
   max = verifySlot.getSlotHoldTime(club);  // returns milliseconds
   max = max / 60 / 1000 / 2;               // convert to inactTimer loops

   max++;                                   // trial fix to eliminate the problem of reservation timer expiring prematurely


   //
   //  Make sure we have a connection (club may not be setup yet)
   //
   if (con != null) {
       
      //
      //  Increment the in_use counter in all tee slots where in_use <> 0
      //
      try {

         pstmt1 = con.prepareStatement (
                "UPDATE teecurr2 SET in_use = ? WHERE teecurr_id = ?");

         pstmt2 = con.prepareStatement (
                "SELECT teecurr_id, in_use FROM teecurr2 WHERE in_use != ?");

         pstmt2.clearParameters();
         pstmt2.setInt(1, not_in_use);

         rs = pstmt2.executeQuery();

         while ( rs.next() ) {

            teecurr_id = rs.getInt("teecurr_id");
            use = rs.getInt("in_use");

            //
            //  The in_use parm indicates if the slot is currently in use and if so,
            //  the value indicates the number of 2 minute increments (it starts with 1).
            //
            use++;                          // increment in_use value (# of 2 minute ticks)

            if (use > max) {                // this actually means 'at least 4 mins' !!!!!

               use = 0;                     // too much time - reset in_use (timeout - return slot to system)
            }

            //
            //  execute the prepared statement to update the tee time slot
            //
            pstmt1.clearParameters();
            pstmt1.setInt(1, use);
            pstmt1.setInt(2, teecurr_id);

            pstmt1.executeUpdate();

         } // end of while

      } catch (Exception e2) {

         logError("Error in SystemUtils checkTime (check teecurr2) for  " +club+ ": err=" + e2.getMessage());                                       // log it
      
      } finally {

         try { rs.close(); }
         catch (Exception ignore) {}

         try { pstmt1.close(); }
         catch (Exception ignore) {}

         try { pstmt2.close(); }
         catch (Exception ignore) {}

      }

      try {
          
         //****************************************************************************
         //  Increment the in_use counter in all Event Entries where in_use <> 0
         //****************************************************************************
         //
         pstmt1 = con.prepareStatement (
                "UPDATE evntsup2b SET in_use = ? WHERE id = ?");

         stmt = con.createStatement();

         rs = stmt.executeQuery("SELECT id, in_use FROM evntsup2b WHERE in_use != 0 AND inactive = 0");

         while ( rs.next() ) {

            id = rs.getInt("id");
            use = rs.getInt("in_use");

            //
            //  The in_use parm indicates if the slot is currently in use and if so,
            //  the value indicates the number of 2 minute increments.
            //
            //   Timer increased to 12 mins for Events due to the addition of the questions and handicap info (2/11/09)!!!!!
            //
            use++;                             // increment in_use value (# of 2 minute ticks)

            if (use > maxEvent) {              // this actually means 'at least 10 mins for Events !!!!!

               use = 0;                        // too much time - reset in_use (timeout - return slot to system)
            }

            //
            //  execute the prepared statement to update the in_use counter
            //
            pstmt1.clearParameters();
            pstmt1.setInt(1, use);
            pstmt1.setInt(2, id);

            pstmt1.executeUpdate();

         } // end of while

      } catch (Exception e3) {

         logError("Error in SystemUtils checkTime (check events) for " +club+ ": " + e3.getMessage());
      
      } finally {

         try { rs.close(); }
         catch (Exception ignore) {}

         try { stmt.close(); }
         catch (Exception ignore) {}

         try { pstmt1.close(); }
         catch (Exception ignore) {}

      }

      try {

         //****************************************************************************
         //  Increment the in_use counter in all Lesson Book Entries where in_use <> 0
         //****************************************************************************
         //
         pstmt1 = con.prepareStatement (
                "UPDATE lessonbook5 SET in_use = ? WHERE proid = ? AND date = ? AND time = ?");

         stmt = con.createStatement();

         rs = stmt.executeQuery("SELECT proid, date, time, in_use FROM lessonbook5 WHERE in_use != 0");

         int proid = 0;

         while ( rs.next() ) {

            proid = rs.getInt(1);
            date = rs.getLong(2);
            time = rs.getInt(3);
            use = rs.getInt(4);

            //
            //  The in_use parm indicates if the slot is currently in use and if so,
            //  the value indicates the number of 2 minute increments.
            //
            use++;                          // increment in_use value (# of 2 minute ticks)

            if (use > max) {                // this actually means 'at least 4 mins' !!!!!

               use = 0;                     // too much time - reset in_use (timeout - return slot to system)
            }

            //
            //  execute the prepared statement to update the in_use counter
            //
            pstmt1.clearParameters();
            pstmt1.setInt(1, use);
            pstmt1.setInt(2, proid);
            pstmt1.setLong(3, date);
            pstmt1.setInt(4, time);

            pstmt1.executeUpdate();

         } // end of while

      } catch (Exception e3) {

         logError("Error in SystemUtils checkTime (check lesson books) for  " +club+ ": " + e3.getMessage());
      
      } finally {

         try { rs.close(); }
         catch (Exception ignore) {}

         try { stmt.close(); }
         catch (Exception ignore) {}

         try { pstmt1.close(); }
         catch (Exception ignore) {}

      }

      try {

         //****************************************************************************
         //  Increment the in_use counter in all Lottery Requests where in_use <> 0
         //****************************************************************************
         //
         long lid = 0;

         pstmt1 = con.prepareStatement (
                "UPDATE lreqs3 SET in_use = ? WHERE id = ?");

         stmt = con.createStatement();

         rs = stmt.executeQuery("SELECT in_use, id FROM lreqs3 WHERE in_use != 0");

         while (rs.next()) {

            use = rs.getInt(1);
            lid = rs.getLong(2);

            //
            //  The in_use parm indicates if the slot is currently in use and if so,
            //  the value indicates the number of 2 minute increments.
            //
            use++;                          // increment in_use value (# of 2 minute ticks)

            if (use > 5) {                  // allow 10 minutes for lottery reqs

               use = 0;                     // too much time - reset in_use (timeout - return slot to system)
            }

            //
            //  execute the prepared statement to update the in_use counter
            //
            pstmt1.clearParameters();
            pstmt1.setInt(1, use);
            pstmt1.setLong(2, lid);

            pstmt1.executeUpdate();

         } // end of while

      } catch (Exception e4) {

         logError("Error in SystemUtils checkTime (check lottery) for  " +club+ ": " + e4.getMessage());

      } finally {

         try { rs.close(); }
         catch (Exception ignore) {}

         try { stmt.close(); }
         catch (Exception ignore) {}

         try { pstmt1.close(); }
         catch (Exception ignore) {}

      }


      // close the connection to this club
      try {
         con.close();
      } catch (Exception ignore) {}

   } // end of IF con

 } // end of checkTime


 //************************************************************************
 // checkTime2 - Process 2 minute system timer #2 expiration from inactTimer2.
 //
 //
 //   called by:  inactTimer2 above
 //
 //         pdate & ptime = today's date & time
 //
 //************************************************************************

 public static void checkTime2(String club, long pdate, int ptime, String day_name) {


   Connection con = null;
   Statement stmt = null;
   PreparedStatement pstmt1 = null;
   ResultSet rs = null;


   //int id = 0;

   long date = 0;

   //String course = "";
   String name = "";
   String errorMsg = "";

   //
   //  Process the club passed
   //
   try {

      con = dbConn.Connect(club);   // get a connection to this club's db

   } catch (Exception e1) {

      logError("Error in SystemUtils checkTime2 - cannot get con for " +club+ ": " + e1.getMessage());
      return;
   }

   //
   //  Make sure we have a connection (club may not be setup yet)
   //
   if (con != null) {

      //
      //   Get current year and create a date of mmdd
      //
      Calendar cal = new GregorianCalendar();   // get todays date
      long yy = cal.get(Calendar.YEAR);

      long mmdd = pdate - (yy * 10000);        // mmdd for compares


      try {
         //****************************************************************************
         //  Check for any Lotteries that are ready to be processed.
         //****************************************************************************
         //
         //  Is there a lottery to process?  (entry added in _lott when lottery time requested - only once)
         //
         pstmt1 = con.prepareStatement (
                "SELECT name, date FROM actlott3 WHERE pdate < ? OR (pdate = ? AND ptime <= ?)");

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setLong(1, pdate);        // current date
         pstmt1.setLong(2, pdate);        // current date
         pstmt1.setInt(3, ptime);         // current time
         rs = pstmt1.executeQuery();

         if (rs.next()) {                 // only one per timer expiration (if more, wait for next timer exp)

            name = rs.getString(1);
            date = rs.getLong(2);              // actual date of lottery
            
           // if (club.equals("mirasolcc") || club.equals("pelicansnest") || club.equals("gallerygolf") || club.equals("dataw") || 
           //     club.equals("esterocc") || club.equals("demov4")) {                         // TEST OTHER CLUBS TOO ??????????????????????

            Common_Lott.processLott(name, date, club, con);      // process this lottery
               
           // } else {
                
           //    Common_Lott_Orig.processLott(name, date, club, con);  // process this lottery (REMOVE THIS ONCE WE KNOW THE NEW CODE IS WORKING OK!!!!!!!!!!!!!!!)
           // }
         }

         pstmt1.close();

      }
      catch (Exception e5) {
         //
         //  save error message in /v_x/error.txt
         //
         errorMsg = "Error in SystemUtils checkTime2 (process lottery) for  " +club+ ": ";
         errorMsg = errorMsg + e5.getMessage();                                 // build error msg

         logError(errorMsg);                                       // log it
      }

      //
      // **********************************************************
      //  Custom processing for Lincoln & Desert Forest
      //      set noshows to '2' so they can use the pre-checkin feature to mark players on premise.
      // **********************************************************
      //
      if (club.equals( "lincoln" ) || club.equals( "desertforestgolfclub" )) {

         if (ptime > 01 && ptime < 30) {       // do after midnight and make sure it gets done (more than once)

            setNoShowOW(pdate, con);           // go change the noshow values to 'pre-checkin'
         }
      }

      //
      // *************************************************************************************************
      //  Custom processing to automatically adjust restrictions.
      // *************************************************************************************************
      //
      if (club.equals( "cherryhills" ) && mmdd > 415 && mmdd < 931) {

         if (day_name.equals( "Monday" ) && ptime > 826 && ptime < 831) {      // release restrictions by 7:30 AM MT

            releaseRestCommon("SPOUSE PRIORITY Open to 11 am", 2, con);        // go change the restriction start date
         }

         if (day_name.equals( "Wednesday" ) && ptime > 826 && ptime < 831) {     // release restrictions by 7:30 AM MT

            releaseRestCommon("SPOUSE PRIORITY Open to 10 am", 2, con);          // go change the restriction start date
         }
      }

      if (club.equals( "stalbans" )) {

         if (day_name.equals( "Thursday" ) && ptime > 757 && ptime < 803) {        // release restrictions around 8:00 AM CT

            releaseRestCommon("Member only play Saturdays", 7, con);                      // go change the restriction start date
         }

         if (day_name.equals( "Thursday" ) && ptime > 757 && ptime < 803) {        // release restrictions around 8:00 AM CT

            releaseRestCommon("Member only play Sundays", 7, con);                      // go change the restriction start date
         }
      }

      if (club.equals( "ccjackson" )) {        // if CC of Jackson

         if (day_name.equals( "Friday" ) && ptime > 858 && ptime < 903) {        // remove unfilled w/e tee times at 9:00 AM CT

            checkTeeTimesCCJ(con);
         }
      }

/*    // removed per club's instructions 3/07/07
      if (club.equals( "meadowsprings" )) {

         if (day_name.equals( "Friday" ) && ptime > 957 && ptime < 1003) {        // release restrictions around 8:00 AM PT

            releaseRestCommon("Saturday Gangsome", 7, con);                    // go change the Gangsome restrictions' start date

            releaseRestCommon("Sunday Gangsome", 7, con);                      // go change the Gangsome restrictions' start date
         }
      }
*/

        // moved to verifyCustom.checkRest Lift - Leave this on for the time being to make sure the new custom is working properly.
      if (club.equals( "brooklawn" )) {

         if (day_name.equals( "Thursday" ) && ptime > 759 && ptime < 805) {        // release restrictions around 9:00 AM ET (no earlier than 9:00!)

            releaseRestCommon("Times open at 9am Thurs", 7, con);                     // go change the restriction start date
         }
/*
         if (day_name.equals( "Thursday" ) && pdate == 20120524 && ptime > 759 && ptime < 805) {        // release restrictions around 9:00 AM ET (no earlier than 9:00!)

            releaseRestCommon("Times open at 9am on Thursday", 7, con);                     // go change the restriction start date
         }
          
         if (day_name.equals( "Thursday" ) && pdate == 20120830 && ptime > 759 && ptime < 805) {        // release restrictions around 9:00 AM ET (no earlier than 9:00!)

            releaseRestCommon("Times open 9am Thursday", 7, con);                     // go change the restriction start date
         }
      */
      }


      if (club.equals("bhamcc")) {

         if (day_name.equals("Thursday") && ptime > 1158 && ptime < 1203) {        // release Sat/Sun restrictions around 1:00 PM ET (only done if open times lott is approved and times available - checked in custom method)

            releaseRestBirminghamCC(con);
         }
      }

      if (club.equals( "hopmeadowcc" ) && mmdd > 414 && mmdd < 1010) {

         if (day_name.equals( "Friday" ) && ptime > 1058 && ptime < 1104) {        // release restrictions around Noon ET

            releaseRestCommonMN("2nd Adult Member", 7, con);                     // go change the Member Number restriction start date

            releaseRestCommonGR("Weekend", 7, con);                              // go change the Guest restriction start date
         }
      }


      //  If Valley CC, release all restrictions 48 hrs in advance around 6am Mountain time so they are ready to go when the tee sheets open at 6:30am
      if (club.equals("valleycc")) {

          if (ptime > 659 && ptime < 703) {

              releaseRestCommon("Ladies Friday Play", 3, con);
              releaseRestCommon("Friday Member Play", 3, con);
              releaseRestCommon("Saturday Member Play", 3, con);
              releaseRestCommon("Sunday Member Play", 3, con);
          }
      }


      // If Indian Ridge CC, release "Guest Policy" guest restriction 72 hrs in advance around 7am Pacific time.
      if (club.equals("indianridgecc")) {

          if (ptime > 855 && ptime < 859) {

              releaseRestCommonGR("Guest Policy", 4, con);
          }
      }

      
      
      int adjtime = 0;
      
      

      // If Desert Mountain, release "Guest Policy" guest restriction 48 hrs in advance around 7 am Arizona time.
      if (club.equals("desertmountain") && ptime > 750 && ptime < 910) {   // between 7:50 and 9:10 CT (1 or 2 hrs different)

          adjtime = adjustTime(con, ptime);                // get adjusted time (necessary for Arizona)

          if (adjtime > 657 && adjtime < 705) {

              releaseRestCommonGR("Guests Restricted 9 to 10 am", 2, con);    // shift the start date 2 days ahead
          }
      }


      if (club.equals( "oahucc" )) {                          // Oahu CC

         adjtime = adjustTime(con, ptime);                // get adjusted time (necessary for Hawaii)

         // if (pdate > 20091231) {                   // if 2010 or beyond

            if (adjtime > 558 && adjtime < 604) {                 // release ALL restrictions around 6:00 AM HT

               releaseRestOahuCC("", 5, con);                     // go change the Member restriction start date (96 hrs in advance)

               releaseRestCommon("Saturday Working Spouses", 5, con);
               releaseRestCommon("Spouses Juniors Front 9 PM", 5, con);
               releaseRestCommon("Spouses Juniors Back 9 PM", 5, con);

            }

         /*
         } else {                                  // 2009 custom - can remove after 1/1/10

            if (day_name.equals( "Wednesday" ) && adjtime > 558 && adjtime < 604) {        // release restrictions around 6:00 AM HT

               releaseRestCommon("Saturday Working Spouses", 7, con);                     // go change the Member restriction start date
            }

            if (day_name.equals( "Tuesday" ) && adjtime > 558 && adjtime < 604) {        // release restrictions around 6:00 AM HT

               releaseRestCommon("Friday Spouse Play II", 7, con);                     // go change the Member restriction start date
            }

            if (day_name.equals( "Saturday" ) && adjtime > 558 && adjtime < 604) {        // release restrictions around 6:00 AM HT

               releaseRestCommon("Tuesday Spouse Play II", 7, con);                     // go change the Member restriction start date
            }
         }
          */
      }



      if (club.equals( "muirfield" ) && mmdd > 331 && mmdd < 1126) {               // Muirfield Village (4/01 - 12/01)

         if (ptime > 100 && ptime < 108) {                                     // release restrictions around 1:00 AM

            if (day_name.equals( "Tuesday" )) {

               releaseRestCommon("Tue 12PM Time Opens 7 Days Out", 8, con);          // go change the restriction start date
               releaseRestCommon("Tue 2PM Time Opens 7 Days Out", 8, con);
               releaseRestCommon("Tue 9AM Time Opens 7 Days Out", 8, con);
            }

            if (day_name.equals( "Wednesday" )) {

               releaseRestCommon("WED 12PM Time Opens 7 Days Out", 8, con);
               releaseRestCommon("Wed 2PM Time Opens 7 Days Out", 8, con);
               releaseRestCommon("Wed 9AM Time Opens 7 Days Out", 8, con);
            }

            if (day_name.equals( "Thursday" )) {

               releaseRestCommon("Thu 12PM Time Opens 7 Days Out", 8, con);
               releaseRestCommon("Thur 2PM Time Opens 7 Days Out", 8, con);
               releaseRestCommon("Thur 9AM Time Opens 7 Days Out", 8, con);
            }

            if (day_name.equals( "Friday" )) {

               releaseRestCommon("Fri 12PM Time Opens 7 Days Out", 8, con);
               releaseRestCommon("Fri 2PM Time Opens 7 Days Out", 8, con);
               releaseRestCommon("Fri 9AM Time Opens 7 Days Out", 8, con);
            }
         }
      }

      if (club.equals( "ravennagolf" ) && mmdd > 407 && mmdd < 1031) {

         if (day_name.equals( "Wednesday" ) && ptime > 759 && ptime < 805) {     // release restrictions around 7:00 AM MT (no earlier than 7:00!)

            releaseRestCommon("Spouse Priority before 10 am F", 7, con);         // go change the restriction start date for Friday Rest
         }

         if (day_name.equals( "Sunday" ) && ptime > 759 && ptime < 805) {        // release restrictions around 7:00 AM MT (no earlier than 7:00!)

            releaseRestCommon("Spouse Priority before 10 am T", 7, con);         // go change the restriction start date for Tuesday Rest
         }

         if (day_name.equals( "Friday" ) && ptime > 759 && ptime < 805) {        // release restrictions around 7:00 AM MT (no earlier than 7:00!)

            releaseRestCommon("Primary Member Saturday", 7, con);                // go change the restriction start date for Saturday Rest

            // releaseRestCommonGR("Saturday Guest Custom Restrict", 2, con);       // go change the restriction start date (bump 2 days ahead of today)
         }

         if (day_name.equals( "Saturday" ) && ptime > 759 && ptime < 805) {      // release restrictions around 7:00 AM MT (no earlier than 7:00!)

            releaseRestCommon("Primary Member Sunday", 7, con);                  // go change the restriction start date for Sunday Rest

            // releaseRestCommonGR("Sunday Guest Custom Restrict", 2, con);         // go change the restriction start date (bump 2 days ahead of today)
         }
      }
      
      if (club.equals("westshorecc") && mmdd >= 401 && mmdd <= 1031) {
          
          if (day_name.equals("Friday") && ptime >= 1100 && ptime <= 1104) {      // release restrictions around 12:00 PM ET
              
              releaseRestCommon("Weekend Mornings", 3, con);
          }
      }


      if (club.equals( "transitvalley" ) && mmdd > 425 && mmdd < 931) {          // Transit Valley May - Sept

         if (day_name.equals( "Monday" ) && ptime > 757 && ptime < 803) {        // release restrictions around 9:00 AM ET

            releaseRestCommon("Ladies Tournament Morning", 7, con);                 // go change the restriction start date
         }
      }


      if (club.equals( "medinah" ) && ptime > 557 && ptime < 603) {          // Medinah

         releaseRestCommon("Soc Reg and Prob on no 1 and 2", 4, con);        // change start date to today + 4 days (so mems can access 3 days in adv)

         releaseRestCommon("Soc Reg and Prob on no 3", 4, con);              // ditto with this one
      }


      if (club.equals( "hazeltine" )) {

         if (day_name.equals( "Tuesday" ) && ptime > 1159 && ptime < 1204) {        // release restrictions at Noon CT

            releaseRestCommonGR("Wed AM Guest", 7, con);       // go change the restriction start date
         }

         if (day_name.equals( "Wednesday" ) && ptime > 1159 && ptime < 1204) {        // release restrictions at Noon CT

            releaseRestCommonGR("Thurs PM Guest", 7, con);       // go change the restriction start date
         }
      }

      if (club.equals("mirasolcc") && ptime > 400 && ptime < 410) {          // Mirasol - release restrictions around 4am CT

         releaseRestCommon("Sports Member 5days in advance", 6, con);        // change start date to today + 6 days (so mems can access 5 days in adv)

         releaseRestCommon("Tenant Member 5days in advance", 6, con);              // ditto with this one
      }

      if (club.equals( "northridge" )) {

         if (ptime > 859 && ptime < 904) {        // release restrictions at 7:00 AM PT

            releaseRestNR(con);                   // go change the restrictions' start date
         }
      }

      if (club.equals( "roguevalley" )) {

          if (ptime > 1029 && ptime < 1034) {      // release restrictions at 8:30 AM PT (Off-Season)

              releaseRestRV(con);                   // go change the restrictions' start date
          }
      }

      if (club.equals( "manitocc" )) {

         // if (ptime > 1509 && ptime < 1514) {      // release restrictions at 1:10 PM PT
         if (ptime > 1002 && ptime < 1007) {      // release restrictions at 8:05 AM PT

            releaseRestMAN(con);                  // go change the restrictions' start date
         }
      }

      if (club.equals( "lakes" )) {

         if (ptime > 927 && ptime < 932) {                          // release restrictions by 7:30 AM PT

       //     releaseRestCommonGR("5 Day Guest Restriction", 6, con);   // go change the restriction start date
            releaseRestCommonGR("5 Day Guest Restriction", 4, con);   // go change the restriction start date
         }
      }
      
      if (club.equals("macgregordowns")) {
          
         if (ptime > 1059 && ptime < 1104) {    // release restriction at 12:00 PM ET, 2 days in advance
             releaseRestCommonGR("Friday Member Only 12 to 150PM", 4, con);
         }
      }
      
      if (club.equals("oceanreef")) {
          
          if (ptime > 559 && ptime < 604) {         // release restrictions at 7:00 AM ET, 3 days in advance
              
              releaseRestCommon("Advanced Time 1", 4, con);         // change start date to today + 4 days (so mems can access 3 days in adv)
              releaseRestCommon("Advanced Time 2", 4, con);
              releaseRestCommon("Advanced Time 3", 4, con);
              releaseRestCommon("Advanced Time 4", 4, con);
          }
      }

      if (club.equals( "skaneateles" )) {

         if (ptime > 627 && ptime < 632) {              // release restrictions by 7:30 AM ET

            String skanRest_name = "";

            if (day_name.equals( "Monday" )) {

               skanRest_name = "White Times Up To 3 Days Thur";    // Thurs rest - adv on Monday
            }
            if (day_name.equals( "Thursday" )) {

               skanRest_name = "Red Times Up to 3 Days";           // Sunday Rest - adv on Thurs
            }
            if (day_name.equals( "Saturday" )) {

               skanRest_name = "White Times Up To 3 Days";         // Tuesday Rest - adv on Sat
            }

            if (!skanRest_name.equals( "" )) {

               releaseRestCommon(skanRest_name, 7, con);         // go change the restriction's start date
            }
         }
      }
      
      if (club.equals("esterocc")) {
          
          if (ptime > 557 && ptime < 602) {
              
              releaseRestCommon("Request Policy Membership Type", 4, con);              // release restrictions by 7:00 AM ET
          }
      }
      
      if (club.equals("dovecanyonclub")) {
          
          if (ptime > 559 && ptime < 604) {
              
              // release all restrictions starting with "TPC" or "PLI" 3 days in advance at 6:00 AM CT
              releaseRestCommonCustom("TPC", 4, club, con);
              releaseRestCommonCustom("PLI", 4, club, con);
          }
      }

      /*
      if (club.equals( "congressional" )) {

         if (ptime > 1357 && ptime < 1402) {              // release restrictions by 3:00 PM ET

            if (day_name.equals( "Tuesday" )) {

              releaseRestCommon("9 Hole Ladies Only", 9, con);     
            }
            if (day_name.equals( "Wednesday" )) {

              releaseRestCommon("18 Hole Ladies Only", 9, con);      
            }
         }
      }
       */

      // Castle Pines - Remove X's 2 days in advance, depending on day, at 12pm MT
      if (club.equals("castlepines") && ptime > 1258 && ptime < 1303) {

            xCustomTimer(club, day_name, con);
      }
      
      if (club.equals("discoverybay") && ptime > 1358 && ptime < 1403) {
          
            xCustomTimer(club, day_name, con);
      }
      
      
      //
      //  Remove Blockers for today (run twice to ensure it runs)
      //
      if (club.equals( "tcclub" )) {

         if ((ptime > 426 && ptime < 431) || (ptime > 526 && ptime < 531)) {   // remove blockers at approximately 4:30 AM CT and 5:30 AM CT (insurance)

            releaseBlockerTCC(con);   // go change the restriction start date
         }
      }


      //
      //  Olympic Club - process POS charges nightly and save the files so they can FTP them to their system
      //
      if (club.equals( "olyclub" ) && pdate < 20130901) {       // STOP on 9/01 as they are switching to NS from CSG and won't need this timer

         if ((ptime > 2257 && ptime < 2301) || (ptime > 2327 && ptime < 2331)) {   // run twice to ensure we do it (11:00 PM & 11:30 PM CT)

            buildOlyPOS(club, con);   // go build the POS files
         }
      }


      //
      //  Ironwood - process POS charges nightly and save the files so they can FTP them to their system
      //
      if (club.equals( "ironwood" )) {

         if (ptime > 828 && ptime < 833) {      // Clear the X's at approximately 6:30 AM PT
             
            xCustomTimer(club, day_name, con);
         }
          
         if ((ptime > 2257 && ptime < 2301) || (ptime > 2327 && ptime < 2331)) {   // run twice to ensure we do it (11:00 PM & 11:30 PM CT)

            buildOlyPOS(club, con);   // go build the POS files
         }
      }


      //
      //  Philly Cricket Club / Atlanta CC - process POS charges nightly and save the files so NorthStar can FTP them to their system
      //
      if (club.equals( "philcricket" ) || club.equals("atlantacountryclub")) {

         if ((ptime > 2057 && ptime < 2101) || (ptime > 2123 && ptime < 2137)) {   // run twice to ensure we do it (9:00 PM & 9:25 PM CT)

            buildOlyPOS(club, con);   // go build the POS files
         }
      }


      //
      // Release any notifications that are being held up
      //
      try {

         stmt = con.createStatement();        // create a statement
         stmt.executeUpdate("" +
                 "UPDATE notifications " +
                 "SET in_use_by = '', in_use_at = '0000-00-00 00:00:00' " +
                 "WHERE DATE_ADD(in_use_at, INTERVAL 6 MINUTE) < now();");

         stmt.close();

      }
      catch (Exception e) {

         errorMsg = "Error in SystemUtils checkTime2 (release notifications) for " +club+ ": ";
         errorMsg = errorMsg + e.getMessage();                                 // build error msg

         logError(errorMsg);
      }

      //
      // Release any wait list signups that are being held up
      //
      try {

         stmt = con.createStatement();        // create a statement
         stmt.executeUpdate("" +
                 "UPDATE wait_list_signups " +
                 "SET in_use_by = '', in_use_at = '0000-00-00 00:00:00' " +
                 "WHERE DATE_ADD(in_use_at, INTERVAL 6 MINUTE) < now();");

         stmt.close();

      }
      catch (Exception e) {

         errorMsg = "Error in SystemUtils checkTime2 (release wait list signups) for " +club+ ": ";
         errorMsg = errorMsg + e.getMessage();                                 // build error msg

         logError(errorMsg);
      }

      //
      // Release any activity slots that are being held up
      //
      try {

         stmt = con.createStatement();        // create a statement
         stmt.executeUpdate("" +
                 "UPDATE activity_sheets " +
                 "SET in_use_by = '', in_use_at = '0000-00-00 00:00:00' " +
                 "WHERE DATE_ADD(in_use_at, INTERVAL 6 MINUTE) < now();");

         stmt.close();

      } catch (Exception e) {

         logError("Error in SystemUtils checkTime2 (release activity slots) for " + club + ": " + e.getMessage());

      }


      try { pstmt1.close(); }
      catch (Exception ignore) {}

      try { stmt.close(); }
      catch (Exception ignore) {}

      try { con.close(); }
      catch (Exception ignore) {}

   } else {

       logError("Error in SystemUtils checkTime2 - con was null for " +club+ "!");

   } // end of IF con

 }  // end of checkTime2




 //************************************************************************
 // moveReqs - Move lottery requests from lreqs to teecurr.
 //
 //   called by:  Proshop_dsheet
 //
 //       parms:  name   = name of the lottery
 //               date   = date of the lottery requests
 //               course = name of course
 //               con    = db connection
 //************************************************************************

 public static void moveReqs(String name, long date, String course, String user, boolean sendEmailsNow, Connection con) {
     
     //  MOVED to Common_Lott 8/22/12
     
     Common_Lott.moveReqs(name, date, course, user, sendEmailsNow, con);
 }


 //************************************************************************
 //  checkBlockers - check tee times to see if any of them are blocked
 //
 //   called by:  Proshop_dsheet
 //
 //       parms:  con = db connection
 //               id  = id of the lottery request
 //
 //************************************************************************

 public static boolean checkBlockers(Connection con, long id) {

       // MOVED to Common_Lott 8/22/12
     
     boolean ok = Common_Lott.checkBlockers(con, id);
     
     return(ok);
 }
 
 
 //************************************************************************
 //  checkInUse - check tee times to see if they are in use
 //
 //   called by:  Proshop_dsheet
 //
 //       parms:  con = db connection
 //               id  = id of the lottery request
 //
 //************************************************************************

 public static boolean checkInUse(Connection con, long id) {

       // MOVED to Common_Lott 8/22/12
     
     boolean ok = Common_Lott.checkInUse(con, id);
     
     return(ok);
 }


 
 
 
 
 // *********************************************************
 //  get the tee time interval for course
 // *********************************************************

 public static int getCourseInterval(String course, Connection con) {

   int interval = 0;

   try {

      PreparedStatement pstmt = con.prepareStatement ("" +
              "SELECT betwn FROM clubparm2 WHERE courseName = ?;");

      pstmt.clearParameters();
      pstmt.setString(1, course);

      ResultSet rs = pstmt.executeQuery();

      if ( rs.next() ) {

          interval = rs.getInt(1);
      }

      pstmt.close();

   }
   catch (Exception e1) {

      String errorMsg = "Error in SystemUtils getCourseInterval: " + e1.getMessage();
      logError(errorMsg);
   }

   return(interval);

 }    // end of getCourseInterval


 // *********************************************************
 //  calculate the requested time based on first req time and tee time interval
 // *********************************************************

 public static int calcRtime(int rtime, int group, int interval) {

   group--;                // adjust for multiplier (2nd group should be requested time + 1 interval)

   rtime = rtime + (group * interval);

   int tmp_hr = rtime / 100;
   int tmp_min = rtime - (tmp_hr * 100);

   if (tmp_min > 59) {

       tmp_hr++;                               // next hour
       tmp_min = tmp_min - 60;                 // adjust minutes
   }

   rtime = (tmp_hr * 100) + tmp_min;           // set new time

   return(rtime);

 }    // end of getCourseInterval


 // *********************************************************
 //  determine minutes between 2 time values
 // *********************************************************

 public static int calcProxTime(int rtime, int atime) {


   int time1 = 0;
   int time2 = 0;
   int hr1 = 0;
   int min1 = 0;
   int hr2 = 0;
   int min2 = 0;
   int mins = 0;

   //
   //  determine the number of minutes between the 2 time values passed
   //
   if (rtime != atime) {          // if different, else return zero

      if (rtime > atime) {          // determine which is larger

         time1 = atime;             // put smaller value in time1
         time2 = rtime;

      } else {

         time1 = rtime;             // put smaller value in time1
         time2 = atime;
      }

      hr1 = time1 / 100;             // get hr
      min1 = time1 - (hr1 * 100);    // get minute

      hr2 = time2 / 100;
      min2 = time2 - (hr2 * 100);

      while (hr2 > hr1) {

         mins += 60;        // add 60 minutes
         hr2--;             // reduce hour
      }

      if (min2 > min1) {

         mins = mins + (min2 - min1);      // add the difference

      } else {

         mins = mins - (min1 - min2);      // subtract the difference
      }
   }

   return(mins);

 }   // end of getEndTime



 // *********************************************************
 //  log a lottery assignment in lassigns5
 // *********************************************************

 public static void logAssign(String user, String name, long date, int mins, int timer, String courseReq, int time, String course, int mem_weight,
                              int grp_weight, long id, Connection con) {

   PreparedStatement pstmtd2 = null;
   ResultSet rs = null;

   boolean skip = false;  
     
   String errorMsg = "Error in SystemUtils logAssign: ";
   
   try {
       
       //  First, see if this entry already exists
       
       pstmtd2 = con.prepareStatement ("SELECT mins " +
                   "FROM lassigns5 WHERE username = ? AND date = ? AND time_assign = ? AND course_assign = ?");

       pstmtd2.clearParameters();
       pstmtd2.setString(1, user);
       pstmtd2.setLong(2, date);
       pstmtd2.setInt(3, time);
       pstmtd2.setString(4, course);

       rs = pstmtd2.executeQuery();

       if (rs.next()) {

           skip = true;
       }
             
       if (skip == false) {

          pstmtd2 = con.prepareStatement (
                  "INSERT INTO lassigns5 (username, lname, date, mins, time_req, course_req, time_assign, course_assign, " +
                  "weight, grp_weight, lreq_id) " +
                  "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

          pstmtd2.clearParameters();
          pstmtd2.setString(1, user);             // username
          pstmtd2.setString(2, name);             // name of lottery
          pstmtd2.setLong(3, date);               // date of request
          pstmtd2.setInt(4, mins);                // minutes between time req'd and time assgnd
          pstmtd2.setInt(5, timer);               // time req'd
          pstmtd2.setString(6, courseReq);        // course req'd
          pstmtd2.setInt(7, time);                // time assgnd
          pstmtd2.setString(8, course);           // course assgnd
          pstmtd2.setInt(9, mem_weight);
          pstmtd2.setInt(10, grp_weight);
          pstmtd2.setLong(11, id);                 // use the lreq id to tie the members of a group together

          pstmtd2.executeUpdate();
      }

      pstmtd2.close();

   }
   catch (Exception e1) {

      errorMsg = errorMsg + e1.getMessage();
      logError(errorMsg);
   }

 }   // end of logAssign




 // **********************************************************************************
 //  log some lottery stats - number of requests and number of available tee times
 // **********************************************************************************

 public static void logLottStats(parmLott parm, Connection con) {


   PreparedStatement pstmtd2 = null;

   int reqs = 0;
   int teetimes = 0;

   String errorMsg = "Error in SystemUtils logLottStats (getting request count): ";


   try {

      //
      // calculate the number of requests (total tee times requested)
      //
      pstmtd2 = con.prepareStatement ("" +
              "SELECT SUM(groups) FROM lreqs3 WHERE name = ? AND date = ?;");

      pstmtd2.clearParameters();
      pstmtd2.setString(1, parm.lottName);
      pstmtd2.setLong(2, parm.date);

      ResultSet rs = pstmtd2.executeQuery();

      if ( rs.next() ) {

          reqs = rs.getInt(1);  // get total number of tee times requested for this lottery and date
      }

      pstmtd2.close();

      errorMsg = "Error in SystemUtils logLottStats (getting tee time count): ";

      //
      //  Now determine the total number of tee times available for this lottery
      //
      if (parm.sfb.equals( "Both" )) {

         if (parm.course.equals( "" ) || parm.course.equals( "-ALL-" )) {

            pstmtd2 = con.prepareStatement (
               "SELECT COUNT(*) " +
               "FROM teecurr2 WHERE date = ? AND time >= ? AND time <= ? AND fb < 2 AND " +
               "event = '' AND blocker = ''");

         } else {

            pstmtd2 = con.prepareStatement (
               "SELECT COUNT(*) " +
               "FROM teecurr2 WHERE date = ? AND time >= ? AND time <= ? AND fb < 2 AND " +
               "event = '' AND blocker = '' AND courseName = ?");
         }

         pstmtd2.clearParameters();        // clear the parms
         pstmtd2.setLong(1, parm.date);
         pstmtd2.setInt(2, parm.stime);
         pstmtd2.setInt(3, parm.etime);

         if (!parm.course.equals( "" ) && !parm.course.equals( "-ALL-" )) {

            pstmtd2.setString(4, parm.course);
         }

         rs = pstmtd2.executeQuery();

         if (rs.next()) {

            teetimes = rs.getInt(1);        // get the number of available tee times
         }
         pstmtd2.close();

      } else {        // single tees

         int tfb = 0;         // default = Front

         if (parm.sfb.equals( "Back" )) {

            tfb = 1;         // back tee
         }

         if (parm.course.equals( "" ) || parm.course.equals( "-ALL-" )) {

            pstmtd2 = con.prepareStatement (
               "SELECT COUNT(*) " +
               "FROM teecurr2 WHERE date = ? AND fb = ? AND time >= ? AND time <= ? AND " +
               "event = '' AND blocker = ''");

         } else {

            pstmtd2 = con.prepareStatement (
               "SELECT COUNT(*) " +
               "FROM teecurr2 WHERE date = ? AND fb = ? AND time >= ? AND time <= ? AND " +
               "event = '' AND blocker = '' AND courseName = ?");
         }

         pstmtd2.clearParameters();        // clear the parms
         pstmtd2.setLong(1, parm.date);
         pstmtd2.setInt(2, tfb);
         pstmtd2.setInt(3, parm.stime);
         pstmtd2.setInt(4, parm.etime);

         if (!parm.course.equals( "" ) && !parm.course.equals( "-ALL-" )) {

            pstmtd2.setString(5, parm.course);
         }

         rs = pstmtd2.executeQuery();

         if (rs.next()) {

            teetimes = rs.getInt(1);        // get the number of available tee times
         }
         pstmtd2.close();
      }


      errorMsg = "Error in SystemUtils logLottStats (logging stats): ";

      //
      //  log this info
      //
      pstmtd2 = con.prepareStatement (
              "INSERT INTO lott_stats (lott_name, date, courseName, requests, teetimes) " +
              "VALUES (?, ?, ?, ?, ?)");

      pstmtd2.clearParameters();
      pstmtd2.setString(1, parm.lottName);
      pstmtd2.setLong(2, parm.date);
      pstmtd2.setString(3, parm.course);
      pstmtd2.setInt(4, reqs);
      pstmtd2.setInt(5, teetimes);

      pstmtd2.executeUpdate();

      pstmtd2.close();

   }
   catch (Exception e1) {

      errorMsg = errorMsg + e1.getMessage();
      logError(errorMsg);
   }

 }   // end of logLottStats



 //************************************************************************
 // getUser - get User information - get user's handicap and member number.
 //
 //   called by:  moveRegs above
 //
 //       parms:  con   = db connection
 //               user  = username of the member
 //
 //************************************************************************

 public static String getUser(Connection con, String user) {


   ResultSet rs = null;

   String parm = "";
   String mNum = "";
   String hndcps = "";

   float hndcp = 99;

   try {

      PreparedStatement pstmt = con.prepareStatement (
               "SELECT g_hancap, memNum FROM member2b WHERE username = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, user);
      rs = pstmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         hndcp = rs.getFloat(1);
         mNum = rs.getString(2);
      }
      pstmt.close();

   }
   catch (Exception e1) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils getUser: ";
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg
      logError(errorMsg);                                       // log it
   }

   if (mNum.equals( "" )) {

      mNum = "*@&";         // so return parm will work
   }

   hndcps = String.valueOf( hndcp );     // create string value of hndcp
   parm = mNum + "," + hndcps;           // create one parm to return

   return(parm);
 }  // end of getUser



 //************************************************************************
 //
 //  getTeeCurrId - get the tecurr_id for the tee time provided
 //
 //************************************************************************

 public static int getTeeCurrId(long date, int time, int fb, String course, Connection con) {


   ResultSet rs = null;

   int teecurr_id = 0;

   try {

      PreparedStatement pstmt = con.prepareStatement (
               "SELECT teecurr_id FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setInt(2, time);
      pstmt.setInt(3, fb);
      pstmt.setString(4, course);
      rs = pstmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         teecurr_id = rs.getInt(1);
      }
      pstmt.close();

   }
   catch (Exception e1) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils getTeeCurrId: ";
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg
      logError(errorMsg);                                       // log it
   }

   return(teecurr_id);

 }  // end of getTeeCurrId





 //************************************************************************
 // xTimer - Process 60 minute system timer expiration.  This timer is set
 //              at init time by Login and reset every 60 minutes so we
 //              can check for X's on the tee sheets and remove them when
 //              requested (indicated by club parms).
 //
 //          The same is done for the Event Sign Up table (remove X's).
 //
 //   called by:  min60Timer on timer expiration
 //
 //************************************************************************

 public static void xTimer() {


   Connection con = null;
   Statement stmt = null;
   ResultSet rs = null;

   String club = "";

   //int hr = 0;
   int c = 0;

   try {

      //
      //  This must be the master server!!!  If not, let the timer run in case master goes down.
      //
      if (Common_Server.SERVER_ID == TIMER_SERVER) {

         long startTime = System.currentTimeMillis();

         //
         //  Set the date/time when this timer should expire by next (safety check to ensure timers keep running)
         //
         Calendar cal = new GregorianCalendar();   // get todays date

         cal.add(Calendar.MINUTE,90);              // roll ahead 90 minutes to give plenty of time

         long year = cal.get(Calendar.YEAR);
         long month = cal.get(Calendar.MONTH) + 1;
         long day = cal.get(Calendar.DAY_OF_MONTH);
         long hr = cal.get(Calendar.HOUR_OF_DAY);       // get 24 hr clock value
         long min = cal.get(Calendar.MINUTE);

         //
         //  create date & time stamp value (yyyymmddhhmm) for compares - timer should expire PRIOR to this time!!!
         //
         long tmpTime = (year * 100000000) + (month * 1000000) + (day * 10000) + (hr * 100) + min;   // save date/time stamp

         min60Time = tmpTime;               // save in global are

         // DEBUG
         if (min60Time < 0) logError("ERROR: min60Time="+min60Time+" (xTimer) year="+year+", month="+month+", day="+day+", hr="+hr+", min="+min);

         //
         //  Now verify that the 2 minute timer is running
         //
         cal = new GregorianCalendar();              // get current date & time

         year = cal.get(Calendar.YEAR);
         month = cal.get(Calendar.MONTH) + 1;
         day = cal.get(Calendar.DAY_OF_MONTH);
         hr = cal.get(Calendar.HOUR_OF_DAY);       // get 24 hr clock value
         min = cal.get(Calendar.MINUTE);

         //
         //  create date & time stamp value (yyyymmddhhmm) for compares
         //
         long currTime = (year * 100000000) + (month * 1000000) + (day * 10000) + (hr * 100) + min;   // form date/time stamp

         if (currTime > min2Time) {    // if 2 min timer not yet set OR did not fire when expected

             // reset the 2 min timer & log this so we can track how often it's being reset
             minTimer t_timer = new minTimer();

             //
             //  log this so we can track how often it must be reset
             //
             String errorMsg = "xTimer: Had to reset the 2 min timer. currTime=" +currTime+ ", min2Time=" +min2Time;                                       // log it

             cal.add(Calendar.MINUTE,4);              // roll ahead 4 minutes to give plenty of time
             year = cal.get(Calendar.YEAR);
             month = cal.get(Calendar.MONTH) + 1;
             day = cal.get(Calendar.DAY_OF_MONTH);
             hr = cal.get(Calendar.HOUR_OF_DAY);
             min = cal.get(Calendar.MINUTE);

             tmpTime = (year * 100000000) + (month * 1000000) + (day * 10000) + (hr * 100) + min;   // create date/time stamp

             min2Time = tmpTime;               // save in global area (should expire PRIOR to this time!!)


             logError(errorMsg + ", new min2Time="+min2Time);
         }

         //
         //  Perform timer function for each club in the system database 'clubs' table
         //
         con = dbConn.Connect(rev);

         //
         // Get the club names from the 'clubs' table and process each club
         //
         stmt = con.createStatement();
         rs = stmt.executeQuery("SELECT clubname FROM clubs WHERE inactive = 0 ORDER BY clubname");

         while (rs.next()) {                // process each club individually

            club = rs.getString(1);         // get a club name

            xTimerClubs(club, hr);          // go process it

            c++;
            
            // custom for The Concession - send out an extra backup teesheet at 7pm est
            if (club.equals("theconcession") && hr == 18) {    // 6pm central

                con = dbConn.Connect("theconcession");
                tsheetTimerGolf("theconcession", null, con);
            }
         
         }

         stmt.close();
         con.close();

         logError("xTimer(" + hr + "): run time " + (System.currentTimeMillis() - startTime) + "ms for " + c + " clubs. ");

      } // end of IF timer server

   } catch (Exception e2) {

       logError("Error in SystemUtils xTimer: club="+ club + ", Err=" + e2.getMessage()); //hr=" + hr + ",

   } finally {

       try {
           if (rs != null) rs.close();
       } catch (Exception ignore) {}

       try {
           if (stmt != null) stmt.close();
       } catch (Exception ignore) {}

       try {
           if (con != null) con.close();
       } catch (Exception ignore) {}

   }

   // make sure the timer is always reset
   min60Timer t4_timer = new min60Timer();

 }  // end of xTimer


 //************************************************************************
 //  xTimerClubs - subr of xTimer to process each club individually
 //************************************************************************

 public static void xTimerClubs(String club, long hour) {

   Connection con = null;

   try {

      //
      //  Process the club that was passed
      //
      con = dbConn.Connect(club);     // get a connection to this club's db

      if (con != null) {

         xTees(club, con);            // check this club's tee sheets for X's

         xEvents(club, con);          // check this club's event signup sheets for X's

         //
         //   Custom processing -
         //
         if ((club.equals( "merion" ) || club.equals( "oakmont" ) || club.equals("oakhillcc")) && hour == 5) {    //  Merion or Oakmont and around 5:00 AM CT

            xMerionTees(club, con);     //  send email reminders to all members scheduled to play 7 days from now
         }

         /*   on hold until club decides
         if (club.equals( "mayfieldsr" ) && hour == 6) {      //  Mayfield Sand Ridge and around 6:00 AM CT

            xMerionTees(club, con);     //  send email reminders to all members scheduled to play 2 days from now
         }
         */

         if (club.equals( "blackdiamondranch" ) && hour == 21) {    //  Black Diamond Ranch and around 9:00 PM CT

            xBDRevents(club, con);                                 //  remove the event signups for each day of the week
         }

         con.close();                 // close the connection to this club

      } else {

         logError("Error in SystemUtils xTimerClubs - unable to get con for club: " +club);
      }

   }
   catch (Exception e2) {

      logError("Error in SystemUtils xTimerClubs: club="+club+", hour="+hour+", Err="+e2.getMessage());

   } finally {

       // ensure connection is closed
       try {
           if (con != null) con.close();
       } catch (Exception ignore) {}

   }

 }  // end of xTimerClubs


 //************************************************************************
 //  xTees - subr of xTimer to remove X's from Tee Sheets
 //************************************************************************

 public static void xTees(String club, Connection con) {


   Statement stmt = null;
   Statement stmt2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;
   ResultSet rs3 = null;

   PreparedStatement pstmt2 = null;
   PreparedStatement pstmt3 = null;
   PreparedStatement pstmte1 = null;

   int hr = 0;
   int min = 0;
   int ehr = 0;
   int emin = 0;
   int eyear = 0;          // date for email msg
   int emonth = 0;
   int eday = 0;
   int e_year = 0;         // date for event email msg
   int e_month = 0;
   int e_day = 0;
   int year = 0;
   int thisYear = 0;
   int month = 0;
   int day = 0;
   int mm = 0;
   int dd = 0;
   int yy = 0;
   int change = 0;
   int x = 0;
   int xhrs = 0;
   int count = 0;
   int fb = 0;
   int id = 0;
   int time = 0;
   int adv_time = 0;
   int adv_time2 = 0;
   int act_hr = 0;
   int act_min = 0;

   long date = 0;
   long adv_date = 0;

   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String username1 = "";
   String username2 = "";
   String username3 = "";
   String username4 = "";
   String username5 = "";
   String orig2 = "";
   String orig3 = "";
   String orig4 = "";
   String orig5 = "";
   String course = "";
   String name = "";
   String dayName = "";
   String clubName = "";
   String subject = "";

   String errorMsg = "SystemUtils.xTees Error for club " +club+ " - Exception = ";

   String to = "";                          // to address
   String f_b = "";
   String eampm = "";
   String act_ampm = "";
   String act_time = "";
   String etime = "";
   int emailOpt = 0;                        // user's email option parm
   int send = 0;

   int teecurr_id = 0;

   String enew = "WARNING:  The following tee time contains one or more player positions reserved with an 'X'.\n" +
                 "The X's will be removed by the system on ";

   //
   //   Get x parms and check tee times for any x's
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT clubName, x, xhrs FROM club5 WHERE clubName != ''");

      if (rs.next()) {                     // one club5 table per club

         clubName = rs.getString(1);
         x = rs.getInt(2);
         xhrs = rs.getInt(3);

         //if (club.equals("missionviejo")) xhrs = (24 * 7);    // 7 days in advance

         //if (club.equals("lakewoodranch")) xhrs = (24 * 6);   // 6 days in advance

         //if (club.equals("gallerygolf")) xhrs = 96;               // 96 hours in advance

         if ((x != 0) && (xhrs != 0)) {     // if club wants to remove X's

            //
            //  Set date/time values to be used to check for X's in tee sheet
            //
            //  Get today's date and then go up by 'xhrs' hours
            //
            Calendar cal = new GregorianCalendar();       // get todays date

            cal.add(Calendar.HOUR_OF_DAY,xhrs);           // roll ahead 'xhrs' hours (rest should adjust)

            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH) +1;
            day = cal.get(Calendar.DAY_OF_MONTH);
            hr = cal.get(Calendar.HOUR_OF_DAY);           // get 24 hr clock value
            min = cal.get(Calendar.MINUTE);

            //
            //  adjust hours for the time zone
            //
            adv_time = (hr * 100) + min;                        // get time in hhmm format

            adv_time = adjustTime(con, adv_time);   // adjust the time

            if (adv_time < 0) {          // if negative, then we went back or ahead one day

               adv_time = 0 - adv_time;        // convert back to positive value

               if (adv_time < 100) {           // if hour is zero, then we rolled ahead 1 day

                  //
                  // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
                  //
                  cal.add(Calendar.DATE,1);                     // get next day's date

                  year = cal.get(Calendar.YEAR);
                  month = cal.get(Calendar.MONTH) +1;
                  day = cal.get(Calendar.DAY_OF_MONTH);

               } else {                        // we rolled back 1 day

                  //
                  // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
                  //
                  cal.add(Calendar.DATE,-1);                     // get yesterday's date

                  year = cal.get(Calendar.YEAR);
                  month = cal.get(Calendar.MONTH) +1;
                  day = cal.get(Calendar.DAY_OF_MONTH);
               }
            }

            //
            //  make sure we have the new time
            //
            hr = adv_time / 100;
            min = adv_time - (hr * 100);

            adv_date = year * 10000;                      // create a date field of yyyymmdd
            adv_date = adv_date + (month * 100);
            adv_date = adv_date + day;                    // date = yyyymmdd (for comparisons)

            //
            //  Remove all 'X' players within 'xhrs' hours from now
            //
            pstmt2 = con.prepareStatement (
                      "SELECT teecurr_id, date, time, player1, player2, player3, player4, fb, player5, courseName, orig2, orig3, orig4, orig5 " +
                      "FROM teecurr2 WHERE date <= ? AND time <= ?");

            //
            //  find tee slots for the specified date and time periods
            //
            pstmt2.clearParameters();        // clear the parms
            pstmt2.setLong(1, adv_date);
            pstmt2.setInt(2, adv_time);
            rs2 = pstmt2.executeQuery();

            while (rs2.next()) {

               teecurr_id = rs2.getInt(1);
               date = rs2.getLong(2);
               time = rs2.getInt(3);
               player1 = rs2.getString(4);
               player2 = rs2.getString(5);
               player3 = rs2.getString(6);
               player4 = rs2.getString(7);
               fb = rs2.getInt(8);
               player5 = rs2.getString(9);
               course = rs2.getString(10);
               orig2 = rs2.getString("orig2");
               orig3 = rs2.getString("orig3");
               orig4 = rs2.getString("orig4");
               orig5 = rs2.getString("orig5");

               change = 0;                  // init change slot indicator

               //
               //  If player = 'X' then clear it (make available to other players)
               //
               //   If player1 = 'x', then ignore rest as only proshop can put an x in slot 1
               //
               if ((!player1.equalsIgnoreCase("x")) && (!player1.equals( "" ))) {

                  if (player2.equalsIgnoreCase("x")) {

                     player2 = "";
                     change = 1;
                     orig2 = "";
                  }

                  if (player3.equalsIgnoreCase("x")) {

                     player3 = "";
                     change = 1;
                     orig3 = "";
                  }

                  if (player4.equalsIgnoreCase("x")) {

                     player4 = "";
                     change = 1;
                     orig4 = "";
                  }

                  if (player5.equalsIgnoreCase("x")) {

                     if (club.equals("interlachen") && change == 0) {

                        change = 0;    // leave X in player5 if this is the only X

                     } else {

                        player5 = "";
                        change = 1;
                        orig5 = "";
                     }
                  }
               }


            /*     // remove until they decide!! (2/15/08)

               //***************************************************************
               //  Mirasol - custom to cancel tee time if less then 2 players
               //            after removing X's   Case (#1244)
               //***************************************************************
               //
               if (club.equals("mirasolcc")) {

                   int tmp_players = 0;
                   if (!player1.equals("")) tmp_players++;
                   if (!player2.equals("")) tmp_players++;
                   if (!player3.equals("")) tmp_players++;
                   if (!player4.equals("")) tmp_players++;
                   if (!player5.equals("")) tmp_players++;

                   if (tmp_players < 2) {

                      pstmt3 = con.prepareStatement (
                          "UPDATE teecurr2 SET " +
                              "player1 = '', player2 = '', player3 = '', player4 = '', player5 = '', " +
                              "username1 = '', username2 = '', username3 = '', username4 = '', username5 = '', " +
                              "userg1 = '', userg2 = '', userg3 = '', userg4 = '', userg5 = '', " +
                              "mNum1 = '', mNum2 = '', mNum3 = '', mNum4 = '', mNum5 = '', " +
                              "p91 = 0, p92 = 0, p93 = 0, p94 = 0, p95 = 0 " +
                          "WHERE teecurr_id = ?");
                      pstmt3.setInt(1, teecurr_id);
                      pstmt3.executeUpdate();
                      pstmt3.close();

                      change = 0; // reset update flag
                   }
               } // end if mirasolcc

             */




               if (change == 1) {

                  pstmt3 = con.prepareStatement (
                      "UPDATE teecurr2 SET player2 = ?, player3 = ?, player4 = ?, player5 = ?, orig2 = ?, orig3 = ?, orig4 = ?, orig5 = ? " +
                              "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                  //
                  //  execute the prepared statement to update the tee time slot
                  //
                  pstmt3.clearParameters();        // clear the parms
                  pstmt3.setString(1, player2);
                  pstmt3.setString(2, player3);
                  pstmt3.setString(3, player4);
                  pstmt3.setString(4, player5);
                  pstmt3.setString(5, orig2);
                  pstmt3.setString(6, orig3);
                  pstmt3.setString(7, orig4);
                  pstmt3.setString(8, orig5);
                  pstmt3.setLong(9, date);
                  pstmt3.setInt(10, time);
                  pstmt3.setInt(11, fb);
                  pstmt3.setString(12, course);
                  count = pstmt3.executeUpdate();

                  pstmt3.close();

               }
            }      // end of while to remove X's

            pstmt2.close();



            //***************************************************************
            //  Valley CC custom
            //***************************************************************
            //
            if (club.equals("valleycc")) {    // Valley CC

               //
               //  Valley CC (CO) wants to remove X's 12 hours in adv normally, but 36 hrs for weekends
               //
               //  We just did the normal days, now do the weekends
               //
               xhrs = 36;                           // do 36 hrs now

               cal = new GregorianCalendar();       // get todays date

               cal.add(Calendar.HOUR_OF_DAY,xhrs);           // roll ahead 'xhrs' hours (rest should adjust)

               year = cal.get(Calendar.YEAR);
               month = cal.get(Calendar.MONTH) +1;
               day = cal.get(Calendar.DAY_OF_MONTH);
               hr = cal.get(Calendar.HOUR_OF_DAY);           // get 24 hr clock value
               min = cal.get(Calendar.MINUTE);

               //
               //  adjust hours for the time zone
               //
               adv_time = (hr * 100) + min;                        // get time in hhmm format

               adv_time = adjustTime(con, adv_time);   // adjust the time

               if (adv_time < 0) {          // if negative, then we went back or ahead one day

                  adv_time = 0 - adv_time;        // convert back to positive value

                  if (adv_time < 100) {           // if hour is zero, then we rolled ahead 1 day

                     //
                     // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
                     //
                     cal.add(Calendar.DATE,1);                     // get next day's date

                     year = cal.get(Calendar.YEAR);
                     month = cal.get(Calendar.MONTH) +1;
                     day = cal.get(Calendar.DAY_OF_MONTH);

                  } else {                        // we rolled back 1 day

                     //
                     // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
                     //
                     cal.add(Calendar.DATE,-1);                     // get yesterday's date

                     year = cal.get(Calendar.YEAR);
                     month = cal.get(Calendar.MONTH) +1;
                     day = cal.get(Calendar.DAY_OF_MONTH);
                  }
               }

               //
               //  make sure we have the new time
               //
               hr = adv_time / 100;
               min = adv_time - (hr * 100);

               adv_date = year * 10000;                      // create a date field of yyyymmdd
               adv_date = adv_date + (month * 100);
               adv_date = adv_date + day;                    // date = yyyymmdd (for comparisons)

               //
               //  Remove all 'X' players within 'xhrs' hours from now on Sat or Sun
               //
               pstmt2 = con.prepareStatement (
                         "SELECT date, time, player1, player2, player3, player4, fb, player5, courseName, orig2, orig3, orig4, orig5 " +
                         "FROM teecurr2 WHERE date <= ? AND time <= ? AND (day = 'Saturday' OR day = 'Sunday')");

               //
               //  find tee slots for the specified date and time periods
               //
               pstmt2.clearParameters();        // clear the parms
               pstmt2.setLong(1, adv_date);
               pstmt2.setInt(2, adv_time);
               rs2 = pstmt2.executeQuery();

               while (rs2.next()) {

                  date = rs2.getLong(1);
                  time = rs2.getInt(2);
                  player1 = rs2.getString(3);
                  player2 = rs2.getString(4);
                  player3 = rs2.getString(5);
                  player4 = rs2.getString(6);
                  fb = rs2.getInt(7);
                  player5 = rs2.getString(8);
                  course = rs2.getString(9);
                  orig2 = rs2.getString("orig2");
                  orig3 = rs2.getString("orig3");
                  orig4 = rs2.getString("orig4");
                  orig5 = rs2.getString("orig5");

                  change = 0;                  // init change slot indicator

                  //
                  //  If player = 'X' then clear it (make available to other players)
                  //
                  //   If player1 = 'x', then ignore rest as only proshop can put an x in slot 1
                  //
                  if (!player1.equalsIgnoreCase("x") && !player1.equals( "" )) {

                     if (player2.equalsIgnoreCase("x")) {

                        player2 = "";
                        change = 1;
                        orig2 = "";
                     }

                     if (player3.equalsIgnoreCase("x")) {

                        player3 = "";
                        change = 1;
                        orig3 = "";
                     }

                     if (player4.equalsIgnoreCase("x")) {

                        player4 = "";
                        change = 1;
                        orig4 = "";
                     }

                     if (player5.equalsIgnoreCase("x")) {

                        player5 = "";
                        change = 1;
                        orig5 = "";
                     }
                  }

                  if (change == 1) {

                     pstmt3 = con.prepareStatement (
                         "UPDATE teecurr2 SET player2 = ?, player3 = ?, player4 = ?, player5 = ?, orig2 = ?, orig3 = ?, orig4 = ?, orig5 = ? " +
                                 "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                     //
                     //  execute the prepared statement to update the tee time slot
                     //
                     pstmt3.clearParameters();        // clear the parms
                     pstmt3.setString(1, player2);
                     pstmt3.setString(2, player3);
                     pstmt3.setString(3, player4);
                     pstmt3.setString(4, player5);
                     pstmt3.setString(5, orig2);
                     pstmt3.setString(6, orig3);
                     pstmt3.setString(7, orig4);
                     pstmt3.setString(8, orig5);
                     pstmt3.setLong(9, date);
                     pstmt3.setInt(10, time);
                     pstmt3.setInt(11, fb);
                     pstmt3.setString(12, course);
                     count = pstmt3.executeUpdate();

                     pstmt3.close();

                  }
               }      // end of while to remove X's

               pstmt2.close();

            }          // end of IF Valley CC


            //***************************************************************
            //
            //  Send email warnings 24 hrs in advance (X's will be removed)
            //
            cal.add(Calendar.HOUR_OF_DAY,24);             // roll ahead another 24 hours (rest should adjust)

            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH) +1;
            hr = cal.get(Calendar.HOUR_OF_DAY);           // get 24 hr clock value
            min = cal.get(Calendar.MINUTE);

            adv_date = year * 10000;                      // create a date field of yyyymmdd
            adv_date = adv_date + (month * 100);
            adv_date = adv_date + day;                    // date = yyyymmdd (for comparisons)

            adv_time = hr * 100;                          // create time field of hhmm
            adv_time2 = adv_time + 59;

            //
            //  Get today's date and then go up by 24 hours (when x's will be removed)
            //
            cal = new GregorianCalendar();       // get todays date

            cal.add(Calendar.HOUR_OF_DAY, 24);    // roll ahead 24 hours (rest should adjust)

            eyear = cal.get(Calendar.YEAR);
            emonth = cal.get(Calendar.MONTH) +1;
            eday = cal.get(Calendar.DAY_OF_MONTH);

            //
            //  Find all 'X' players within the next 24 hour period (do one hour at a time)
            //
            pstmt2 = con.prepareStatement (
                      "SELECT mm, dd, yy, day, hr, min, player1, player2, player3, player4,  " +
                      "username1, username2, username3, username4, fb, player5, username5, courseName " +
                      "FROM teecurr2 WHERE date = ? AND time >= ? AND time <= ?");

            //
            //  find tee slots for the specified date and time periods
            //
            pstmt2.clearParameters();        // clear the parms
            pstmt2.setLong(1, adv_date);
            pstmt2.setInt(2, adv_time);
            pstmt2.setInt(3, adv_time2);
            rs2 = pstmt2.executeQuery();

            while (rs2.next()) {

               mm = rs2.getInt(1);
               dd = rs2.getInt(2);
               yy = rs2.getInt(3);
               dayName = rs2.getString(4);
               ehr = rs2.getInt(5);
               emin = rs2.getInt(6);
               player1 = rs2.getString(7);
               player2 = rs2.getString(8);
               player3 = rs2.getString(9);
               player4 = rs2.getString(10);
               username1 = rs2.getString(11);
               username2 = rs2.getString(12);
               username3 = rs2.getString(13);
               username4 = rs2.getString(14);
               fb = rs2.getInt(15);
               player5 = rs2.getString(16);
               username5 = rs2.getString(17);
               course = rs2.getString(18);
               
               // Reset the email message to avoid compounding 'X' removal date/time warning messages.
               enew = "WARNING:  The following tee time contains one or more player positions reserved with an 'X'.\n" +
                 "The X's will be removed by the system on ";

               //
               //  if any X's in the tee time, then send email to all players with email enabled
               //
               if (player1.equalsIgnoreCase( "x" ) || player2.equalsIgnoreCase( "x" ) ||
                   player3.equalsIgnoreCase( "x" ) || player4.equalsIgnoreCase( "x" ) ||
                   player5.equalsIgnoreCase( "x" )) {

                  Properties properties = new Properties();
                  properties.put("mail.smtp.host", host);                // set outbound host address
                  properties.put("mail.smtp.port", port);                // set outbound port
                  properties.put("mail.smtp.auth", "true");              // set 'use authentication'
                  properties.put("mail.smtp.sendpartial", "true");       // a message has some valid and some invalid addresses, send the message anyway

                  Session mailSess = Session.getInstance(properties, getAuthenticator()); // get session properties

                  MimeMessage message = new MimeMessage(mailSess);
                  message.setFrom(new InternetAddress(efrom));                  // set from addr
                  message.setSentDate(new java.util.Date());                             // set date/time sent

                  subject = "Tee Time Warning";

                  if (!clubName.equals( "" )) {

                     subject = subject + " - " + clubName;
                  }

                  message.setSubject( subject );                      // set subject line

                  enew = enew + emonth + "/" + eday + "/" + eyear + " at approximately " + hr + ":00.\n" +
                                "If you do not fill these positions before that time, the positions will be " +
                                "made available to other members.\n\n";

                  send = 0;                                                    // init

                  if (!username1.equals( "" )) {

                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();
                     pstmte1.setString(1, username1);
                     rs3 = pstmte1.executeQuery();       // get member's email info

                     if (rs3.next()) {

                        to = rs3.getString(1);           // user's email address
                        emailOpt = rs3.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

                           send = 1;                     // send email
                        }
                     }
                     pstmte1.close();              // close the stmt

                  }

                  if (!username2.equals( "" )) {

                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();
                     pstmte1.setString(1, username2);
                     rs3 = pstmte1.executeQuery();       // get member's email info

                     if (rs3.next()) {

                        to = rs3.getString(1);           // user's email address
                        emailOpt = rs3.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

                           send = 1;                     // send email
                        }
                     }
                     pstmte1.close();              // close the stmt

                  }

                  if (!username3.equals( "" )) {

                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();
                     pstmte1.setString(1, username3);
                     rs3 = pstmte1.executeQuery();       // get member's email info

                     if (rs3.next()) {

                        to = rs3.getString(1);           // user's email address
                        emailOpt = rs3.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

                           send = 1;                     // send email
                        }
                     }
                     pstmte1.close();              // close the stmt

                  }

                  if (!username4.equals( "" )) {

                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();
                     pstmte1.setString(1, username4);
                     rs3 = pstmte1.executeQuery();       // get member's email info

                     if (rs3.next()) {

                        to = rs3.getString(1);           // user's email address
                        emailOpt = rs3.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

                           send = 1;                     // send email
                        }
                     }
                     pstmte1.close();              // close the stmt

                  }

                  if (!username5.equals( "" )) {

                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();
                     pstmte1.setString(1, username5);
                     rs3 = pstmte1.executeQuery();       // get member's email info

                     if (rs3.next()) {

                        to = rs3.getString(1);           // user's email address
                        emailOpt = rs3.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

                           send = 1;                     // send email
                        }
                     }
                     pstmte1.close();              // close the stmt

                  }

                  if (send != 0) {                 // shuld we send an email ?

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

                     if (emin > 9) {

                        etime = ehr + ":" + emin + eampm;

                     } else {

                        etime = ehr + ":0" + emin + eampm;
                     }

                     //
                     //  set the front/back value
                     //
                     f_b = "Front";

                     if (fb == 1) {

                        f_b = "Back";
                     }

                     //
                     //  Create the message content
                     //
                     String enewMsg = header + enew + "Tee Time: " + dayName +
                                      " " + mm + "/" + dd + "/" + yy + " at " + etime + " " +
                                      "on the " + f_b + " tee ";

                     if (!course.equals( "" )) {

                        enewMsg = enewMsg + "of Course: " + course;
                     }

                     enewMsg = enewMsg + "\n";

                     if (!player1.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 1: " + player1;
                     }
                     if (!player2.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 2: " + player2;
                     }
                     if (!player3.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 3: " + player3;
                     }
                     if (!player4.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 4: " + player4;
                     }
                     if (!player5.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 5: " + player5;
                     }

                     enewMsg = enewMsg + trailer;

                     message.setText( enewMsg );  // put msg in email text area

                     Transport.send(message);     // send it!!

                  }    // end of IF send

               }  // end of IF any X's

            }      // end of while to send email warnings

            pstmt2.close();

         }               // end of IF X option
      }                  // end of IF rs.next
      stmt.close();

   }
   catch (Exception e2) {
      //
      //  save error message in /v_x/error.txt
      //
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg
      logError(errorMsg);                                       // log it
   }

 }  // end of xTees


 //************************************************************************
 //  xEvents - subr of xTimer to remove X's from Event Signups
 //************************************************************************

 public static void xEvents(String club, Connection con) {


   Statement stmt = null;
   Statement stmt2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;
   ResultSet rs3 = null;

   PreparedStatement pstmte = null;
   PreparedStatement pstmt2 = null;
   PreparedStatement pstmt3 = null;
   PreparedStatement pstmte1 = null;

   int hr = 0;
   int min = 0;
   int ehr = 0;
   int emin = 0;
   int eyear = 0;          // date for email msg
   int emonth = 0;
   int eday = 0;
   int e_year = 0;         // date for event email msg
   int e_month = 0;
   int e_day = 0;
   int year = 0;
   int thisYear = 0;
   int month = 0;
   int day = 0;
   int mm = 0;
   int dd = 0;
   int yy = 0;
   int change = 0;
   int x = 0;
   int xhrs = 0;
   int count = 0;
   int fb = 0;
   int id = 0;
   int time = 0;
   int adv_time = 0;
   int adv_time2 = 0;
   int act_hr = 0;
   int act_min = 0;

   long date = 0;
   long adv_date = 0;

   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String username1 = "";
   String username2 = "";
   String username3 = "";
   String username4 = "";
   String username5 = "";
   String course = "";
   String name = "";
   String dayName = "";
   String clubName = "";
   String subject = "";

   String errorMsg = "SystemUtils.xEvents Error for club " +club+ " - Exception = ";

   String to = "";                          // to address
   String f_b = "";
   String eampm = "";
   String act_ampm = "";
   String act_time = "";
   String etime = "";
   int emailOpt = 0;                        // user's email option parm
   int send = 0;

   String emsg = "WARNING:  The following Event Registration contains one or more player positions reserved with an 'X'.\n" +
                 "The X's will be removed by the system on ";

   //
   //  Get this year for event processing
   //
   Calendar cal = new GregorianCalendar();       // get todays date
   thisYear = cal.get(Calendar.YEAR);

   try {

      //**************************************************************
      //  Process the Event Sign Up tables
      //
      //  Get the parms from the events db for this club.
      //**************************************************************
      //
      pstmte = con.prepareStatement (
                "SELECT name, year, month, day, act_hr, act_min, courseName, x, xhrs " +
                "FROM events2b WHERE name != '' AND year >= ? AND inactive = 0");

      pstmte.clearParameters();        // clear the parms
      pstmte.setInt(1, thisYear);
      rs = pstmte.executeQuery();

      while (rs.next()) {                     // process each event individually

         name = rs.getString(1);
         e_year = rs.getInt(2);
         e_month = rs.getInt(3);
         e_day = rs.getInt(4);
         act_hr = rs.getInt(5);
         act_min = rs.getInt(6);
         course = rs.getString(7);
         x = rs.getInt(8);
         xhrs = rs.getInt(9);

         // Reset the email message to avoid compounding 'X' removal date/time warning messages.         
         emsg = "WARNING:  The following Event Registration contains one or more player positions reserved with an 'X'.\n" +
                 "The X's will be removed by the system on ";

         //
         //  Create time values
         //
         act_ampm = "AM";

         if (act_hr == 0) {

            act_hr = 12;                 // change to 12 AM (midnight)

         } else {

            if (act_hr == 12) {

               act_ampm = "PM";         // change to Noon
            }
         }
         if (act_hr > 12) {

            act_hr = act_hr - 12;
            act_ampm = "PM";             // change to 12 hr clock
         }


         if ((x != 0) && (xhrs != 0)) {     // if club wants to remove X's

            //
            //  Set date/time values to be used to check for X's in Event Sign Up table
            //
            //  Get today's date and then go up by 'xhrs' hours
            //
            cal = new GregorianCalendar();                // get todays date

            cal.add(Calendar.HOUR_OF_DAY,xhrs);           // roll ahead 'xhrs' hours (rest should adjust)

            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH) +1;
            day = cal.get(Calendar.DAY_OF_MONTH);
            hr = cal.get(Calendar.HOUR_OF_DAY);           // get 24 hr clock value
            min = cal.get(Calendar.MINUTE);

            //
            //  adjust hours for the time zone
            //
            adv_time = (hr * 100) + min;                        // get time in hhmm format

            adv_time = adjustTime(con, adv_time);   // adjust the time

            if (adv_time < 0) {          // if negative, then we went back or ahead one day

               adv_time = 0 - adv_time;        // convert back to positive value

               if (adv_time < 100) {           // if hour is zero, then we rolled ahead 1 day

                  //
                  // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
                  //
                  cal.add(Calendar.DATE,1);                     // get next day's date

                  year = cal.get(Calendar.YEAR);
                  month = cal.get(Calendar.MONTH) +1;
                  day = cal.get(Calendar.DAY_OF_MONTH);

               } else {                        // we rolled back 1 day

                  //
                  // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
                  //
                  cal.add(Calendar.DATE,-1);                     // get yesterday's date

                  year = cal.get(Calendar.YEAR);
                  month = cal.get(Calendar.MONTH) +1;
                  day = cal.get(Calendar.DAY_OF_MONTH);
               }
            }

            //
            //  make sure we have the new time
            //
            hr = adv_time / 100;
            min = adv_time - (hr * 100);

            adv_date = year * 10000;                      // create a date field of yyyymmdd
            adv_date = adv_date + (month * 100);
            adv_date = adv_date + day;                    // date = yyyymmdd (for comparisons)

            //
            //  Remove all 'X' players within 'xhrs' hours from now
            //
            pstmt2 = con.prepareStatement (
                      "SELECT player1, player2, player3, player4, player5, id " +
                      "FROM evntsup2b WHERE name = ? AND courseName = ? AND c_date <= ? AND c_time <= ? AND inactive = 0");

            //
            //  find event entries for the specified date and time periods
            //
            pstmt2.clearParameters();        // clear the parms
            pstmt2.setString(1, name);
            pstmt2.setString(2, course);
            pstmt2.setLong(3, adv_date);
            pstmt2.setInt(4, adv_time);
            rs2 = pstmt2.executeQuery();

            while (rs2.next()) {

               player1 = rs2.getString(1);
               player2 = rs2.getString(2);
               player3 = rs2.getString(3);
               player4 = rs2.getString(4);
               player5 = rs2.getString(5);
               id = rs2.getInt(6);

               change = 0;                  // init change slot indicator

               //
               //  If player = 'X' then clear it (make available to other players)
               //
               //   If player1 = 'x', then ignore rest as only proshop can put an x in slot 1
               //
               if ((!player1.equalsIgnoreCase("x")) && (!player1.equals( "" ))) {

                  if (player2.equalsIgnoreCase("x")) {

                     player2 = "";
                     change = 1;
                  }

                  if (player3.equalsIgnoreCase("x")) {

                     player3 = "";
                     change = 1;
                  }

                  if (player4.equalsIgnoreCase("x")) {

                     player4 = "";
                     change = 1;
                  }

                  if (player5.equalsIgnoreCase("x")) {

                     player5 = "";
                     change = 1;
                  }
               }

               if (change == 1) {

                  pstmt3 = con.prepareStatement (
                      "UPDATE evntsup2b SET player2 = ?, player3 = ?, player4 = ?, player5 = ? " +
                      "WHERE name = ? AND courseName = ? AND id = ?");

                  //
                  //  execute the prepared statement to update the tee time slot
                  //
                  pstmt3.clearParameters();        // clear the parms
                  pstmt3.setString(1, player2);
                  pstmt3.setString(2, player3);
                  pstmt3.setString(3, player4);
                  pstmt3.setString(4, player5);
                  pstmt3.setString(5, name);
                  pstmt3.setString(6, course);
                  pstmt3.setInt(7, id);
                  count = pstmt3.executeUpdate();

                  pstmt3.close();
               }
            }      // end of while to remove X's

            pstmt2.close();

            //
            //  Send email warnings 24 hrs in advance (X's will be removed)
            //
            cal.add(Calendar.HOUR_OF_DAY,24);             // roll ahead another 24 hours (rest should adjust)

            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH);
            hr = cal.get(Calendar.HOUR_OF_DAY);           // get 24 hr clock value
            min = cal.get(Calendar.MINUTE);

            month = month + 1;                            // month starts at zero

            adv_date = year * 10000;                      // create a date field of yyyymmdd
            adv_date = adv_date + (month * 100);
            adv_date = adv_date + day;                    // date = yyyymmdd (for comparisons)

            adv_time = hr * 100;                          // create time field of hhmm
            adv_time2 = adv_time + 59;

            //
            //  Get today's date and then go up by 24 hours (when x's will be removed)
            //
            cal = new GregorianCalendar();       // get todays date

            cal.add(Calendar.HOUR_OF_DAY,24);    // roll ahead 24 hours (rest should adjust)

            eyear = cal.get(Calendar.YEAR);
            emonth = cal.get(Calendar.MONTH) +1;
            eday = cal.get(Calendar.DAY_OF_MONTH);
            
            emsg = emsg + emonth + "/" + eday + "/" + eyear + " at approximately " + hr + ":00.\n"
                    + "If you do not fill these positions before that time, the positions will be "
                    + "made available to other members.\n\n";

            //
            //  Check for 'X' players within 'xhrs' + 24 hours from now for this event
            //
            pstmt2 = con.prepareStatement (
                "SELECT player1, player2, player3, player4, player5, " +
                "username1, username2, username3, username4, username5, id " +
                "FROM evntsup2b WHERE name = ? AND courseName = ? AND c_date = ? AND c_time >= ? AND c_time <= ? AND inactive = 0 AND player1<>''");

            //
            //  find tee slots for the specified date and time periods
            //
            pstmt2.clearParameters();        // clear the parms
            pstmt2.setString(1, name);
            pstmt2.setString(2, course);
            pstmt2.setLong(3, adv_date);
            pstmt2.setInt(4, adv_time);
            pstmt2.setInt(5, adv_time2);
            rs2 = pstmt2.executeQuery();

            while (rs2.next()) {

               player1 = rs2.getString(1);
               player2 = rs2.getString(2);
               player3 = rs2.getString(3);
               player4 = rs2.getString(4);
               player5 = rs2.getString(5);
               username1 = rs2.getString(6);
               username2 = rs2.getString(7);
               username3 = rs2.getString(8);
               username4 = rs2.getString(9);
               username5 = rs2.getString(10);
               id = rs2.getInt(11);

               //
               //  if any X's in the tee time, then send email to all players with email enabled
               //
               if (player1.equalsIgnoreCase( "x" ) || player2.equalsIgnoreCase( "x" ) ||
                   player3.equalsIgnoreCase( "x" ) || player4.equalsIgnoreCase( "x" ) ||
                   player5.equalsIgnoreCase( "x" )) {

                  Properties properties = new Properties();
                  properties.put("mail.smtp.host", host);                       // set outbound host address
                  properties.put("mail.smtp.port", port);                       // set outbound port
                  properties.put("mail.smtp.auth", "true");                     // set 'use authentication'
                  properties.put("mail.smtp.sendpartial", "true");              // a message has some valid and some invalid addresses, send the message anyway

                  Session mailSess = Session.getInstance(properties, getAuthenticator()); // get session properties

                  MimeMessage message = new MimeMessage(mailSess);
                  message.setFrom(new InternetAddress(efrom));                  // set from addr
                  message.setSentDate(new java.util.Date());                    // set date/time sent

                  subject = "Event Registration Warning";

                  if (!clubName.equals( "" )) {

                     subject = subject + " - " + clubName;
                  }

                  message.setSubject( subject );                      // set subject line

                  send = 0;                                                    // init

                  if (!username1.equals( "" )) {

                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();
                     pstmte1.setString(1, username1);
                     rs3 = pstmte1.executeQuery();       // get member's email info

                     if (rs3.next()) {

                        to = rs3.getString(1);           // user's email address
                        emailOpt = rs3.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

                           send = 1;                     // send email
                        }
                     }
                     pstmte1.close();              // close the stmt

                  }

                  if (!username2.equals( "" )) {

                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();
                     pstmte1.setString(1, username2);
                     rs3 = pstmte1.executeQuery();       // get member's email info

                     if (rs3.next()) {

                        to = rs3.getString(1);           // user's email address
                        emailOpt = rs3.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

                           send = 1;                     // send email
                        }
                     }
                     pstmte1.close();              // close the stmt

                  }

                  if (!username3.equals( "" )) {

                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();
                     pstmte1.setString(1, username3);
                     rs3 = pstmte1.executeQuery();       // get member's email info

                     if (rs3.next()) {

                        to = rs3.getString(1);           // user's email address
                        emailOpt = rs3.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

                           send = 1;                     // send email
                        }
                     }
                     pstmte1.close();              // close the stmt

                  }

                  if (!username4.equals( "" )) {

                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();
                     pstmte1.setString(1, username4);
                     rs3 = pstmte1.executeQuery();       // get member's email info

                     if (rs3.next()) {

                        to = rs3.getString(1);           // user's email address
                        emailOpt = rs3.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

                           send = 1;                     // send email
                        }
                     }
                     pstmte1.close();              // close the stmt

                  }

                  if (!username5.equals( "" )) {

                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();
                     pstmte1.setString(1, username5);
                     rs3 = pstmte1.executeQuery();       // get member's email info

                     if (rs3.next()) {

                        to = rs3.getString(1);           // user's email address
                        emailOpt = rs3.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

                           send = 1;                     // send email
                        }
                     }
                     pstmte1.close();              // close the stmt

                  }

                  if (send != 0) {                 // shuld we send an email ?

                     //
                     //  Create the message content
                     //
                     if (act_min < 10) {

                        act_time = act_hr + ":0" + act_min + " " + act_ampm;

                     } else {

                        act_time = act_hr + ":" + act_min + " " + act_ampm;
                     }

                     String enewMsg = header + emsg + " Event: " + name + " " +
                                   " Date: " + e_month + "/" + e_day + "/" + e_year + " at " +
                                   " " + act_time;

                     if (!course.equals( "" )) {

                        enewMsg = enewMsg + "on Course: " + course;
                     }

                     enewMsg = enewMsg + "\n";

                     if (!player1.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 1: " + player1;
                     }
                     if (!player2.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 2: " + player2;
                     }
                     if (!player3.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 3: " + player3;
                     }
                     if (!player4.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 4: " + player4;
                     }
                     if (!player5.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 5: " + player5;
                     }

                     enewMsg = enewMsg + trailer;

                     message.setText( enewMsg );  // put msg in email text area

                     Transport.send(message);     // send it!!

                  }    // end of IF send

               }  // end of IF any X's

            }      // end of while to send email warnings

            pstmt2.close();

         }               // end of IF X option
      }                  // end of WHILE rs.next (events)
      pstmte.close();

   }
   catch (Exception e2) {
      //
      //  save error message in /v_x/error.txt
      //
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it

   }

 }  // end of xEvents



 //************************************************************************
 //  xBDRevents - subr of xTimer to remove event signups
 //************************************************************************

 public static void xBDRevents(String club, Connection con) {


   PreparedStatement stmt = null;
   PreparedStatement pstmt1 = null;
   ResultSet rs = null;

   int day = 0;

   String dayname = "";
   String ename = "";


   //
   //  Get current date and day
   //
   Calendar cal = new GregorianCalendar();          // get todays date
   day = cal.get(Calendar.DAY_OF_WEEK);             // day - 1 = Sunday, 7 = Saturday

   //
   //  current time is 8:xx PM CT - process the next day's season long event(s)
   //
   if (day > 0 && day < 8) {         // valid day number ?

       switch (day) {

           case 1:           // today is Sunday

              dayname = "Monday";  // day to process
              break;

           case 2:           // today is Monday

              dayname = "Tuesday";  // day to process
              break;

           case 3:           // today is Tuesday

              dayname = "Wednesday";  // day to process
              break;

           case 4:           // today is Wednesday

              dayname = "Thursday";  // day to process
              break;

           case 5:           // today is Thursday

              dayname = "Friday";  // day to process
              break;

           case 6:           // today is Friday

              dayname = "Saturday";  // day to process
              break;

           case 7:           // today is Saturday

              dayname = "Sunday";  // day to process
              break;

       }    // end of switch

       try {

          //
          //  Now remove the signups for the season long event(s) for the specific day of the week
          //
          pstmt1 = con.prepareStatement (
              "DELETE FROM evntsup2b " +
              "WHERE name = ? AND inactive = 0");

          stmt = con.prepareStatement (
              "SELECT name " +
              "FROM events2b " +
              "WHERE season = ? AND inactive = 0");

         stmt.clearParameters();
         stmt.setInt(1, 1);              // get all season long events
         rs = stmt.executeQuery();

         while (rs.next()) {

            ename = rs.getString("name");

            //
            //  if this event's name starts with the day specified, then remove the singups
            //
            if (ename.startsWith( dayname )) {

               pstmt1.clearParameters();
               pstmt1.setString(1, ename);

               pstmt1.executeUpdate();
            }
         }   // end of WHILE event

         pstmt1.close();
         stmt.close();

      }
      catch (Exception e1) {

         logError("Error in SystemUtils.xBDRevents - Exception for day = " +day+ ": " + e1.getMessage());      // log the error
      }

   } else {

      logError("Error in SystemUtils.xBDRevents - invalid day number - day = " +day);      // log the error
   }

 }  // end of xBDRevents



 //************************************************************************
 //  xMerionTees - subr of xTimer to send reminders to members x days in adv
 //************************************************************************

 public static void xMerionTees(String club, Connection con) {


   Statement stmt = null;
   ResultSet rs2 = null;
   ResultSet rs3 = null;

   PreparedStatement pstmt2 = null;
   PreparedStatement pstmte1 = null;

   int hr = 0;
   int min = 0;
   int ehr = 0;
   int emin = 0;
   int eyear = 0;          // date for email msg
   int emonth = 0;
   int eday = 0;
   int e_year = 0;         // date for event email msg
   int e_month = 0;
   int e_day = 0;
   int year = 0;
   int thisYear = 0;
   int month = 0;
   int day = 0;
   int mm = 0;
   int dd = 0;
   int yy = 0;
   int change = 0;
   int x = 0;
   int xhrs = 0;
   int count = 0;
   int fb = 0;
   int id = 0;
   int time = 0;
   int adv_time = 0;
   int adv_time2 = 0;
   int act_hr = 0;
   int act_min = 0;
   int emailOpt = 0;                        // user's email option parm
   int email1_bounced = 0;
   int email2_bounced = 0;
   int send = 0;
   int days = 0;

   long date = 0;
   long adv_date = 0;

   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String username1 = "";
   String username2 = "";
   String username3 = "";
   String username4 = "";
   String username5 = "";
   String course = "";
   String name = "";
   String email1 = "";
   String email2 = "";
   String dayName = "";
   String clubName = "";

   String errorMsg = "SystemUtils.xMerionTees Error for club " +club+ " - ";

   String to = "";                          // to address
   String to2 = "";
   String f_b = "";
   String eampm = "";
   String act_ampm = "";
   String act_time = "";
   String etime = "";
   String edate = "";
   String subject = "";
   String enew = "";
   String proEmail1 = "";

   boolean error = false;

   Properties properties = null;
   Session mailSess = null;
   MimeMessage message = null;


   // String tmp_sql = "SELECT (SELECT email FROM member2b WHERE username = ? AND email_bounced = 0) AS email1, (SELECT emailOpt FROM member2b WHERE username = ?) AS emailOpt, (SELECT email2 FROM member2b WHERE username = ? AND email2_bounced = 0) AS email2";
   String tmp_sql = "SELECT email, emailOpt, email2, email_bounced, email2_bounced FROM member2b WHERE username = ?";


   if (club.equals("mayfieldsr")) {

       subject = "Reminder from The Mayfield Sand Ridge Club";
       
       proEmail1 = "";    // Pro email address
       
       days = 2;        // days in advance to send emails

   } else if (club.equals("oakmont")) {

       subject = "Reminder from Oakmont Country Club";
       
       proEmail1 = "";       // Pro email address
       
       days = 7;         // days in advance to send emails
       
   } else if (club.equals("oakhillcc") || club.equals("demobrad")) {

       subject = "Reminder from Oak Hill Country Club";
       
       proEmail1 = "";       // Pro email address
       
       days = 7;         // days in advance to send emails
       
   } else {          // Merion GC
       
       subject = "Reminder from Merion Golf Club";
       
       proEmail1 = ""; // bad address, keeps bouncing "teetimes@meriongolfclub.com";          // Pro email address
       
       days = 7;         // days in advance to send emails
   }


   //
   //  Get the date x days from today
   //
   Calendar cal = new GregorianCalendar();          // get todays date

   cal.add(Calendar.DATE,days);                     // roll ahead x days

   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH) +1;
   day = cal.get(Calendar.DAY_OF_MONTH);

   adv_date = year * 10000;                      // create a date field of yyyymmdd
   adv_date = adv_date + (month * 100);
   adv_date = adv_date + day;                    // date = yyyymmdd (for comparisons)

   edate = month + "/" + day + "/" + year;       // date for email message


    //  logError("TEST - email method entered. Club=" +club+ ".");   // TEMP !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


   //
   //  Get all tee times for this date
   //
   try {

      pstmt2 = con.prepareStatement (
               "SELECT date, time, player1, player2, player3, player4, " +
               "username1, username2, username3, username4, fb, player5, username5, courseName " +
               "FROM teecurr2 WHERE date = ? AND player1 != '' AND player1 != 'x'");

      //
      //  find tee slots for the specified date and time periods
      //
      pstmt2.clearParameters();        // clear the parms
      pstmt2.setLong(1, adv_date);
      rs2 = pstmt2.executeQuery();

      while (rs2.next()) {

         date = rs2.getLong(1);
         time = rs2.getInt(2);
         player1 = rs2.getString(3);
         player2 = rs2.getString(4);
         player3 = rs2.getString(5);
         player4 = rs2.getString(6);
         username1 = rs2.getString(7);
         username2 = rs2.getString(8);
         username3 = rs2.getString(9);
         username4 = rs2.getString(10);
         fb = rs2.getInt(11);
         player5 = rs2.getString(12);
         username5 = rs2.getString(13);
         course = rs2.getString(14);

         //
         //  if any members in the tee time, then send email to all players with email enabled
         //
         if (!username1.equalsIgnoreCase( "" ) || !username2.equalsIgnoreCase( "" ) ||
             !username3.equalsIgnoreCase( "" ) || !username4.equalsIgnoreCase( "" ) ||
             !username5.equalsIgnoreCase( "" )) {

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

            if (emin < 10) {

               etime = ehr + ":0" + emin + eampm;

            } else {

               etime = ehr + ":" + emin + eampm;
            }

            error = false;

            try {

               //
               //  Setup for emails
               //
               properties = new Properties();
               properties.put("mail.smtp.host", host);                // set outbound host address
               properties.put("mail.smtp.port", port);                // set outbound port
               properties.put("mail.smtp.auth", "true");              // set 'use authentication'
               properties.put("mail.smtp.sendpartial", "true");       // a message has some valid and some invalid addresses, send the message anyway

               mailSess = Session.getInstance(properties, getAuthenticator()); // get session properties

               message = new MimeMessage(mailSess);
               message.setFrom(new InternetAddress(efrom));                  // set from addr
               message.setSentDate(new java.util.Date());                    // set date/time sent

               message.setSubject( subject );                      // set subject line

            }
            catch (Exception e2) {
               //
               //  save error message in /v_x/error.txt
               //
               errorMsg = errorMsg + "Exception #2 = " + e2.getMessage();        // build error msg
               logError(errorMsg);                                               // log it
               error = true;
            }


            if (error == false) {

               send = 0;                                                    // init

               if (!username1.equals( "" )) {

                  try {

                     pstmte1 = con.prepareStatement ( tmp_sql );

                     pstmte1.clearParameters();
                     pstmte1.setString(1, username1);
                   //  pstmte1.setString(2, username1);
                   //  pstmte1.setString(3, username1);
                     rs3 = pstmte1.executeQuery();       // get member's email info

                     if (rs3.next()) {

                        to = rs3.getString(1);           // user's email address
                        emailOpt = rs3.getInt(2);        // email option
                        to2 = rs3.getString(3);           // user's email address
                        email1_bounced = rs3.getInt(4);
                        email2_bounced = rs3.getInt(5);

                        if ((emailOpt != 0) && (email1_bounced == 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           try {
                              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           }
                           catch (Exception ignore) {
                           }

                           send = 1;                     // send email

                           if (!to2.equals( "" ) && (email2_bounced == 0)) {     // if 2nd email address

                              try {
                                 message.addRecipient(Message.RecipientType.TO, new InternetAddress(to2));
                              }
                              catch (Exception ignore) {
                              }
                           }
                        }
                     }
                     pstmte1.close();              // close the stmt

                  }
                  catch (SQLException e1) {
                     //
                     //  save error message in /v_x/error.txt
                     //
                     errorMsg = errorMsg + "Exception #3 = " + e1.getMessage();                                 // build error msg
                     logError(errorMsg);                                       // log it
                  }
               }

               if (!username2.equals( "" )) {

                  try {

                     pstmte1 = con.prepareStatement ( tmp_sql );

                     pstmte1.clearParameters();
                     pstmte1.setString(1, username2);
                   //  pstmte1.setString(2, username2);
                   //  pstmte1.setString(3, username2);
                     rs3 = pstmte1.executeQuery();       // get member's email info

                     if (rs3.next()) {

                        to = rs3.getString(1);           // user's email address
                        emailOpt = rs3.getInt(2);        // email option
                        to2 = rs3.getString(3);           // user's email address
                        email1_bounced = rs3.getInt(4);
                        email2_bounced = rs3.getInt(5);

                        if ((emailOpt != 0) && (email1_bounced == 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           try {
                              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           }
                           catch (Exception ignore) {
                           }

                           send = 1;                     // send email

                           if (!to2.equals( "" ) && (email2_bounced == 0)) {     // if 2nd email address

                              try {
                                 message.addRecipient(Message.RecipientType.TO, new InternetAddress(to2));
                              }
                              catch (Exception ignore) {
                              }
                           }
                        }
                     }
                     pstmte1.close();              // close the stmt

                  }
                  catch (SQLException e1) {
                     //
                     //  save error message in /v_x/error.txt
                     //
                     errorMsg = errorMsg + "Exception #4 = " + e1.getMessage();                                 // build error msg
                     logError(errorMsg);                                       // log it
                  }
               }

               if (!username3.equals( "" )) {

                  try {

                     pstmte1 = con.prepareStatement ( tmp_sql );

                     pstmte1.clearParameters();
                     pstmte1.setString(1, username3);
                   //  pstmte1.setString(2, username3);
                   //  pstmte1.setString(3, username3);
                     rs3 = pstmte1.executeQuery();       // get member's email info

                     if (rs3.next()) {

                        to = rs3.getString(1);           // user's email address
                        emailOpt = rs3.getInt(2);        // email option
                        to2 = rs3.getString(3);           // user's email address
                        email1_bounced = rs3.getInt(4);
                        email2_bounced = rs3.getInt(5);

                        if ((emailOpt != 0) && (email1_bounced == 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           try {
                              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           }
                           catch (Exception ignore) {
                           }

                           send = 1;                     // send email

                           if (!to2.equals( "" ) && (email2_bounced == 0)) {     // if 2nd email address

                              try {
                                 message.addRecipient(Message.RecipientType.TO, new InternetAddress(to2));
                              }
                              catch (Exception ignore) {
                              }
                           }
                        }
                     }
                     pstmte1.close();              // close the stmt

                  }
                  catch (SQLException e1) {
                     //
                     //  save error message in /v_x/error.txt
                     //
                     errorMsg = errorMsg + "Exception #5 = " + e1.getMessage();                                 // build error msg
                     logError(errorMsg);                                       // log it
                  }
               }

               if (!username4.equals( "" )) {

                  try {

                     pstmte1 = con.prepareStatement ( tmp_sql );

                     pstmte1.clearParameters();
                     pstmte1.setString(1, username4);
                  //   pstmte1.setString(2, username4);
                  //   pstmte1.setString(3, username4);
                     rs3 = pstmte1.executeQuery();       // get member's email info

                     if (rs3.next()) {

                        to = rs3.getString(1);           // user's email address
                        emailOpt = rs3.getInt(2);        // email option
                        to2 = rs3.getString(3);           // user's email address
                        email1_bounced = rs3.getInt(4);
                        email2_bounced = rs3.getInt(5);

                        if ((emailOpt != 0) && (email1_bounced == 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           try {
                              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           }
                           catch (Exception ignore) {
                           }

                           send = 1;                     // send email

                           if (!to2.equals( "" ) && (email2_bounced == 0)) {     // if 2nd email address

                              try {
                                 message.addRecipient(Message.RecipientType.TO, new InternetAddress(to2));
                              }
                              catch (Exception ignore) {
                              }
                           }
                        }
                     }
                     pstmte1.close();              // close the stmt

                  }
                  catch (SQLException e1) {
                     //
                     //  save error message in /v_x/error.txt
                     //
                     errorMsg = errorMsg + "Exception #6 = " + e1.getMessage();                                 // build error msg
                     logError(errorMsg);                                       // log it
                  }
               }

               if (!username5.equals( "" )) {

                  try {

                     pstmte1 = con.prepareStatement ( tmp_sql );

                     pstmte1.clearParameters();
                     pstmte1.setString(1, username5);
                  //   pstmte1.setString(2, username5);
                  //   pstmte1.setString(3, username5);
                     rs3 = pstmte1.executeQuery();       // get member's email info

                     if (rs3.next()) {

                        to = rs3.getString(1);           // user's email address
                        emailOpt = rs3.getInt(2);        // email option
                        to2 = rs3.getString(3);           // user's email address
                        email1_bounced = rs3.getInt(4);
                        email2_bounced = rs3.getInt(5);

                        if ((emailOpt != 0) && (email1_bounced == 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           try {
                              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           }
                           catch (Exception ignore) {
                           }

                           send = 1;                     // send email

                           if (!to2.equals( "" ) && (email2_bounced == 0)) {     // if 2nd email address

                              try {
                                 message.addRecipient(Message.RecipientType.TO, new InternetAddress(to2));
                              }
                              catch (Exception ignore) {
                              }
                           }
                        }
                     }
                     pstmte1.close();              // close the stmt

                  }
                  catch (SQLException e1) {
                     //
                     //  save error message in /v_x/error.txt
                     //
                     errorMsg = errorMsg + "Exception #7 = " + e1.getMessage();                                 // build error msg
                     logError(errorMsg);                                       // log it
                  }
               }




      // logError("TEST - email to be sent. Club=" +club+ " send=" +send+ ".");   // TEMP !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!




               if (send != 0) {                 // shuld we send an email ?

                  //
                  //  Build the message body
                  //
                  enew = "REMINDER:  You are scheduled for the following tee time in " + days + " days.\n\n";
                  
                  if (club.equals("oakhillcc")) {
                      enew += "If you do not intend to use this tee time, please cancel immediately as a courtesy to other members looking to bring guests, and to avoid a cancellation fee.\n\n";
                  }

                  enew = enew + "On " + edate + " at " + etime + " on the " + course +  " course.\n\n" +
                         "Players:  " + player1;

                  if (!player2.equals( "" )) {

                     enew = enew + ", " + player2;
                  }
                  if (!player3.equals( "" )) {

                     enew = enew + ", " + player3;
                  }
                  if (!player4.equals( "" )) {

                     enew = enew + ", " + player4;
                  }
                  if (!player5.equals( "" )) {

                     enew = enew + ", " + player5;
                  }

                  //
                  //  Add pro's email address if provided - copy him/her
                  //
                  if (!proEmail1.equals( "" )) {

                     try {
                        message.addRecipient(Message.RecipientType.BCC, new InternetAddress(proEmail1));   // add pro to BCC
                     }
                     catch (Exception ignore) {
                     }
                  }

                  enew = enew + trailer;

                  try {
                     message.setText( enew );  // put msg in email text area

                     Transport.send(message);     // send it!!
                  }
                  catch (Exception e12) {

                     errorMsg = errorMsg + "Sending Email Exception #12 = " + e12.getMessage();
                     logError(errorMsg);                                       // log it
                  }

               }    // end of IF send

            }       // end of IF error

         }  // end of IF any members

      }      // end of while tee times

      pstmt2.close();

   }
   catch (SQLException e1) {
      //
      //  save error message in /v_x/error.txt
      //
      errorMsg = errorMsg + "Exception #10 = " + e1.getMessage();                                 // build error msg
      logError(errorMsg);                                       // log it
   }
   
   return;
 }  // end of xMerionTees

 public static void xCustomTimer (String club, String day_name, Connection con) {


   Statement stmt = null;
   Statement stmt2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;
   ResultSet rs3 = null;

   PreparedStatement pstmt2 = null;
   PreparedStatement pstmt3 = null;
   PreparedStatement pstmte1 = null;

   int hr = 0;
   int min = 0;
   int ehr = 0;
   int emin = 0;
   int eyear = 0;          // date for email msg
   int emonth = 0;
   int eday = 0;
   int e_year = 0;         // date for event email msg
   int e_month = 0;
   int e_day = 0;
   int year = 0;
   int thisYear = 0;
   int month = 0;
   int day = 0;
   int days = 0;
   int mm = 0;
   int dd = 0;
   int yy = 0;
   int change = 0;
   int x = 0;
   int xhrs = 0;
   int count = 0;
   int fb = 0;
   int id = 0;
   int time = 0;
   int act_hr = 0;
   int act_min = 0;

   long date = 0;
   long adv_date = 0;

   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String username1 = "";
   String username2 = "";
   String username3 = "";
   String username4 = "";
   String username5 = "";
   String orig2 = "";
   String orig3 = "";
   String orig4 = "";
   String orig5 = "";
   String course = "";
   String name = "";
   String dayName = "";
   String clubName = "";
   String subject = "";

   String errorMsg = "SystemUtils.xCustomTimer Error for " + club + " - Exception = ";

   String to = "";                          // to address
   String f_b = "";
   String eampm = "";
   String act_ampm = "";
   String act_time = "";
   String etime = "";
   String timeString = "";
   int emailOpt = 0;                        // user's email option parm
   int send = 0;

   int teecurr_id = 0;

   String enew = "WARNING:  The following tee time contains one or more player positions reserved with an 'X'.\n" +
                 "The X's will be removed by the system on ";


   try {


        if (club.equals("castlepines")) {
            
            days = 2;
            
        } else if (club.equals("ironwood")) {
            
            days = 3;
            
        } else if (club.equals("discoverybay")) {
            
            days = 1;
            
        }

        //
        //  Set date/time values to be used to check for X's in tee sheet
        //
        //  Get today's date and then go up by 'xhrs' hours
        //
        Calendar cal = new GregorianCalendar();       // get todays date

        cal.add(Calendar.DATE,days);           // roll ahead 'xhrs' hours (rest should adjust)

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) +1;
        day = cal.get(Calendar.DAY_OF_MONTH);

        adv_date = year * 10000;                      // create a date field of yyyymmdd
        adv_date = adv_date + (month * 100);
        adv_date = adv_date + day;                    // date = yyyymmdd (for comparisons)

       //if (!club.equals("castlepines") || !day_name.equals("Monday")) {     // Don't run on Monday for Castle Pines

            //
            //  Remove all 'X' players within 'xhrs' hours from now
            //
            pstmt2 = con.prepareStatement (
                      "SELECT teecurr_id, date, time, player1, player2, player3, player4, fb, player5, courseName, orig2, orig3, orig4, orig5 " +
                      "FROM teecurr2 WHERE date <= ?");

            //
            //  find tee slots for the specified date and time periods
            //
            pstmt2.clearParameters();        // clear the parms
            pstmt2.setLong(1, adv_date);
            rs2 = pstmt2.executeQuery();

            while (rs2.next()) {

               teecurr_id = rs2.getInt(1);
               date = rs2.getLong(2);
               time = rs2.getInt(3);
               player1 = rs2.getString(4);
               player2 = rs2.getString(5);
               player3 = rs2.getString(6);
               player4 = rs2.getString(7);
               fb = rs2.getInt(8);
               player5 = rs2.getString(9);
               course = rs2.getString(10);
               orig2 = rs2.getString("orig2");
               orig3 = rs2.getString("orig3");
               orig4 = rs2.getString("orig4");
               orig5 = rs2.getString("orig5");

               change = 0;                  // init change slot indicator

               //
               //  If player = 'X' then clear it (make available to other players)
               //
               //   If player1 = 'x', then ignore rest as only proshop can put an x in slot 1
               //
               if ((!player1.equalsIgnoreCase("x")) && (!player1.equals( "" ))) {

                  if (player2.equalsIgnoreCase("x")) {

                     player2 = "";
                     change = 1;
                     orig2 = "";
                  }

                  if (player3.equalsIgnoreCase("x")) {

                     player3 = "";
                     change = 1;
                     orig3 = "";
                  }

                  if (player4.equalsIgnoreCase("x")) {

                     player4 = "";
                     change = 1;
                     orig4 = "";
                  }

                  if (player5.equalsIgnoreCase("x")) {

                     player5 = "";
                     change = 1;
                     orig5 = "";
                  }
               }

               if (change == 1) {

                  pstmt3 = con.prepareStatement (
                      "UPDATE teecurr2 SET player2 = ?, player3 = ?, player4 = ?, player5 = ?, orig2 = ?, orig3 = ?, orig4 = ?, orig5 = ? " +
                              "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                  //
                  //  execute the prepared statement to update the tee time slot
                  //
                  pstmt3.clearParameters();        // clear the parms
                  pstmt3.setString(1, player2);
                  pstmt3.setString(2, player3);
                  pstmt3.setString(3, player4);
                  pstmt3.setString(4, player5);
                  pstmt3.setString(5, orig2);
                  pstmt3.setString(6, orig3);
                  pstmt3.setString(7, orig4);
                  pstmt3.setString(8, orig5);
                  pstmt3.setLong(9, date);
                  pstmt3.setInt(10, time);
                  pstmt3.setInt(11, fb);
                  pstmt3.setString(12, course);
                  count = pstmt3.executeUpdate();

                  pstmt3.close();

               }
            }      // end of while to remove X's

            pstmt2.close();
       //}

        //***************************************************************
        //
        //  Send email warnings 2-3 days in advance (X's will be removed)
        //
        //if (!club.equals("castlepines") || !day_name.equals("Sunday")) {       // Don't send on Monday reminder emails on Sunday for Castle Pines since we don't remove X's for them on Mondays
            
            cal.add(Calendar.DATE,1);             // roll ahead another 1 day (rest should adjust)

            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH) +1;

            adv_date = year * 10000;                      // create a date field of yyyymmdd
            adv_date = adv_date + (month * 100);
            adv_date = adv_date + day;                    // date = yyyymmdd (for comparisons)

            //
            //  Get today's date and then go up by 1 day (when x's will be removed)
            //
            cal = new GregorianCalendar();       // get todays date

            cal.add(Calendar.DATE, 1);    // roll ahead 1 day (rest should adjust)

            eyear = cal.get(Calendar.YEAR);
            emonth = cal.get(Calendar.MONTH) +1;
            eday = cal.get(Calendar.DAY_OF_MONTH);

            //
            //  Find all 'X' players within the next 24 hour period (do one hour at a time)
            //
            pstmt2 = con.prepareStatement (
                      "SELECT mm, dd, yy, day, hr, min, player1, player2, player3, player4,  " +
                      "username1, username2, username3, username4, fb, player5, username5, courseName " +
                      "FROM teecurr2 WHERE date = ?");

            //
            //  find tee slots for the specified date and time periods
            //
            pstmt2.clearParameters();        // clear the parms
            pstmt2.setLong(1, adv_date);
            rs2 = pstmt2.executeQuery();

            while (rs2.next()) {

               mm = rs2.getInt(1);
               dd = rs2.getInt(2);
               yy = rs2.getInt(3);
               dayName = rs2.getString(4);
               ehr = rs2.getInt(5);
               emin = rs2.getInt(6);
               player1 = rs2.getString(7);
               player2 = rs2.getString(8);
               player3 = rs2.getString(9);
               player4 = rs2.getString(10);
               username1 = rs2.getString(11);
               username2 = rs2.getString(12);
               username3 = rs2.getString(13);
               username4 = rs2.getString(14);
               fb = rs2.getInt(15);
               player5 = rs2.getString(16);
               username5 = rs2.getString(17);
               course = rs2.getString(18);
               

               // Reset the email message to avoid compounding 'X' removal date/time warning messages.                
               enew = "WARNING:  The following tee time contains one or more player positions reserved with an 'X'.\n" +
                 "The X's will be removed by the system on ";

               //
               //  if any X's in the tee time, then send email to all players with email enabled
               //
               if (player1.equalsIgnoreCase( "x" ) || player2.equalsIgnoreCase( "x" ) ||
                   player3.equalsIgnoreCase( "x" ) || player4.equalsIgnoreCase( "x" ) ||
                   player5.equalsIgnoreCase( "x" )) {

                  Properties properties = new Properties();
                  properties.put("mail.smtp.host", host);                // set outbound host address
                  properties.put("mail.smtp.port", port);                // set outbound port
                  properties.put("mail.smtp.auth", "true");              // set 'use authentication'
                  properties.put("mail.smtp.sendpartial", "true");       // a message has some valid and some invalid addresses, send the message anyway

                  Session mailSess = Session.getInstance(properties, getAuthenticator()); // get session properties

                  MimeMessage message = new MimeMessage(mailSess);
                  message.setFrom(new InternetAddress(efrom));                  // set from addr
                  message.setSentDate(new java.util.Date());                             // set date/time sent

                  subject = "Tee Time Warning";

                  if (!clubName.equals( "" )) {

                     subject = subject + " - " + clubName;
                  }

                  message.setSubject( subject );                      // set subject line

                  if (club.equals("castlepines")) {
                      
                      timeString = "12:00pm";
                      
                  } else if (club.equals("ironwood")) {
                      
                      timeString = "6:30am";
                      
                  } else if (club.equals("discoverybay")) {
                      
                      timeString = "12:00pm";
                      
                  }
                      
                  enew = enew + emonth + "/" + eday + "/" + eyear + " at approximately " + timeString + ".\n" +
                          "If you do not fill these positions before that time, the positions will be " +
                          "made available to other members.\n\n";
                      
                  send = 0;                                                    // init

                  if (!username1.equals( "" )) {

                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();
                     pstmte1.setString(1, username1);
                     rs3 = pstmte1.executeQuery();       // get member's email info

                     if (rs3.next()) {

                        to = rs3.getString(1);           // user's email address
                        emailOpt = rs3.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

                           send = 1;                     // send email
                        }
                     }
                     pstmte1.close();              // close the stmt

                  }

                  if (!username2.equals( "" )) {

                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();
                     pstmte1.setString(1, username2);
                     rs3 = pstmte1.executeQuery();       // get member's email info

                     if (rs3.next()) {

                        to = rs3.getString(1);           // user's email address
                        emailOpt = rs3.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

                           send = 1;                     // send email
                        }
                     }
                     pstmte1.close();              // close the stmt

                  }

                  if (!username3.equals( "" )) {

                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();
                     pstmte1.setString(1, username3);
                     rs3 = pstmte1.executeQuery();       // get member's email info

                     if (rs3.next()) {

                        to = rs3.getString(1);           // user's email address
                        emailOpt = rs3.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

                           send = 1;                     // send email
                        }
                     }
                     pstmte1.close();              // close the stmt

                  }

                  if (!username4.equals( "" )) {

                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();
                     pstmte1.setString(1, username4);
                     rs3 = pstmte1.executeQuery();       // get member's email info

                     if (rs3.next()) {

                        to = rs3.getString(1);           // user's email address
                        emailOpt = rs3.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

                           send = 1;                     // send email
                        }
                     }
                     pstmte1.close();              // close the stmt

                  }

                  if (!username5.equals( "" )) {

                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();
                     pstmte1.setString(1, username5);
                     rs3 = pstmte1.executeQuery();       // get member's email info

                     if (rs3.next()) {

                        to = rs3.getString(1);           // user's email address
                        emailOpt = rs3.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

                           send = 1;                     // send email
                        }
                     }
                     pstmte1.close();              // close the stmt

                  }

                  if (send != 0) {                 // shuld we send an email ?

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

                     if (emin > 9) {

                        etime = ehr + ":" + emin + eampm;

                     } else {

                        etime = ehr + ":0" + emin + eampm;
                     }

                     //
                     //  set the front/back value
                     //
                     f_b = "Front";

                     if (fb == 1) {

                        f_b = "Back";
                     }

                     //
                     //  Create the message content
                     //
                     String enewMsg = header + enew + "Tee Time: " + dayName +
                                      " " + mm + "/" + dd + "/" + yy + " at " + etime + " " +
                                      "on the " + f_b + " tee ";

                     if (!course.equals( "" )) {

                        enewMsg = enewMsg + "of Course: " + course;
                     }

                     enewMsg = enewMsg + "\n";

                     if (!player1.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 1: " + player1;
                     }
                     if (!player2.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 2: " + player2;
                     }
                     if (!player3.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 3: " + player3;
                     }
                     if (!player4.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 4: " + player4;
                     }
                     if (!player5.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 5: " + player5;
                     }

                     enewMsg = enewMsg + trailer;

                     message.setText( enewMsg );  // put msg in email text area

                     Transport.send(message);     // send it!!

                  }    // end of IF send

               }  // end of IF any X's

            }      // end of while to send email warnings

            pstmt2.close();
        //}
   }
   catch (Exception e2) {
      //
      //  save error message in /v_x/error.txt
      //
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg
      logError(errorMsg);                                       // log it
   }

 }


 // ************************************************************************
 //  Process getAuthenticator for email authentication
 // ************************************************************************

 public static Authenticator getAuthenticator() {

    Authenticator auth = new Authenticator() {

       public PasswordAuthentication getPasswordAuthentication() {

         return new PasswordAuthentication("support@foretees.com", "fikd18"); // credentials
       }
    };

    return auth;
 }


 //************************************************************************
 // tsheetTimer - Process system timer expiration.  This timer is set
 //               to periodically send an email to each proshop with the
 //               current tee sheets.  For back-up purposes.
 //
 //               Sends sheets at 10:00 PM and 4:00 AM each day.
 //
 //
 //   called by:  TsheetTimer on timer expiration
 //
 //************************************************************************

 public static void tsheetTimer() {

     tsheetTimer("", null);
 }

 public static void tsheetTimer(String club, PrintWriter out) {

   //logError("Starting tsheetTimer() on node " + Common_Server.SERVER_ID + (club.equals("")) ? "." : " for " + club + "."));

   Connection con1 = null;
   Connection con2 = null;
   Statement stmt1 = null;
   Statement stmt2 = null;
   ResultSet rs1 = null;
   ResultSet rs2 = null;


   int foretees_mode = 0;
   int genrez_mode = 0;


   if ( !club.equals("") ) {   // if this is for an individual club

      try {

          //
          //   Process each club - get guest names and email address
          //
          con2 = dbConn.Connect(club);                                 // get a connection to this club's db

          //
          //  Determine which activities this club has defined
          //
          stmt2 = con2.createStatement();

          rs2 = stmt2.executeQuery("SELECT foretees_mode, genrez_mode FROM club5 WHERE clubName <> '';");

          if (rs2.next()) {

             foretees_mode = rs2.getInt(1);
             genrez_mode = rs2.getInt(2);
          }

          stmt2.close();

          if (foretees_mode > 0) {          // if Golf defined in system

             tsheetTimerGolf(club, out, con2);         // go send tee sheets
          }

          if (genrez_mode > 0) {          // if any Activities defined in system

             tsheetTimerAct(club, out, con2);         // go send activity sheets
          }

          con2.close();         // close the connection to this club

          con2 = null;

       } catch (Exception e1) {

           logError("Error1 in SystemUtils.tsheetTimer for club " +club+ ", error: " + e1.getMessage());
       }


   } else {

      //
      //  All clubs - This must be the master server!!!  If not, let the timer run in case master goes down.
      //
      if (Common_Server.SERVER_ID == TIMER_SERVER) {

         //if (singleClub.equals("")) logError("Starting tsheetTimer()");

         //
         //  Perform timer function for each club in the system database 'clubs' table
         //
         club = rev;          // get db name for 'clubs' table (v5)

         try {
            con1 = dbConn.Connect(club);                           // get a connection
         }
         catch (Exception e1) {

            return;
         }

         //
         // Get the club names from the 'clubs' table
         //
         //  Process each club in the table
         //
         try {

            stmt1 = con1.createStatement();        // create a statement

            rs1 = stmt1.executeQuery("SELECT clubname FROM clubs WHERE inactive = 0 ORDER BY clubname");

            while (rs1.next()) {

               club = rs1.getString(1);        // get a club name

               try {

                   //
                   //   Process each club - get guest names and email address
                   //
                   con2 = dbConn.Connect(club);                                 // get a connection to this club's db

                   //
                   //  Determine which activities this club has defined
                   //
                   stmt2 = con2.createStatement();

                   rs2 = stmt2.executeQuery("SELECT foretees_mode, genrez_mode FROM club5 WHERE clubName <> '';");

                   if (rs2.next()) {

                      foretees_mode = rs2.getInt(1);
                      genrez_mode = rs2.getInt(2);
                   }

                   stmt2.close();

                   if (foretees_mode > 0) {          // if Golf defined in system

                      tsheetTimerGolf(club, null, con2);         // go send tee sheets
                   }

                   if (genrez_mode > 0) {          // if any Activities defined in system

                      tsheetTimerAct(club, null, con2);         // go send activity sheets
                   }

                   con2.close();         // close the connection to this club

                   con2 = null;

                } catch (Exception e1) {

                    logError("Error2 in SystemUtils.tsheetTimer for club " +club+ ", error: " + e1.getMessage());
                } // end try/catch for each individual club

            }      // end of while for all clubs

            stmt1.close();

            con1.close();             // close the connection to the system db

            con1 = null;

         }
         catch (Exception e2) {
            //
            //  save error message in /v_x/error.txt
            //
            String errorMsg = "Error3 in SystemUtils.tsheetTimer for club " +club+ ", error: ";
            errorMsg = errorMsg + e2.getMessage();                                 // build error msg

            logError(errorMsg);                                       // log it
         }


         try {
            if (con2 != null) con2.close();
         } catch (Exception ignore) {}

         try {
            if (con1 != null) con1.close();
         } catch (Exception ignore) {}


      } // end if master server check

      //logError("Finished tsheetTimer()");

      //
      //  reset the Tee Sheet timer for next period
      //
      TsheetTimer t_timer = new TsheetTimer();

   }           // end of IF individual club or all clubs

 }  // end of tsheetTimer


 //************************************************************************
 // tsheetTimerGolf - Email backup tee sheets to pro
 //
 //   called by:  tsheetTimer above on timer expiration
 //
 //************************************************************************

 public static void tsheetTimerGolf(String club, PrintWriter out, Connection con2) {

   Statement stmt3 = null;
   Statement stmt4 = null;
   ResultSet rs = null;
   ResultSet rs3 = null;
   ResultSet rs4 = null;

   int hr = 0;
   int min = 0;
   int year = 0;
   int month = 0;
   int day = 0;
   int fives = 0;
   int hndcp = 0;

   int players = 0;
   int fb = 0;
   int day_num = 0;
   int type = 0;
   // int emailOpt = 0;            // email option - send tee sheets via email?
   int shotgun = 1;             // event type = shotgun
   int emailCount = 0;

   int g1 = 0;                  // guest indicators (1 per player)
   int g2 = 0;
   int g3 = 0;
   int g4 = 0;
   int g5 = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;

   float hndcp1 = 0;
   float hndcp2 = 0;
   float hndcp3 = 0;
   float hndcp4 = 0;
   float hndcp5 = 0;

   short show = 0;
   short show1 = 0;
   short show2 = 0;
   short show3 = 0;
   short show4 = 0;
   short show5 = 0;
   String event = "";
   String ecolor = "";
   String rest = "";
   String rcolor = "";
   //String player = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String p1 = "";
   String p2 = "";
   String p3 = "";
   String p4 = "";
   String p5 = "";
   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";
   String ampm = "";
   String bgcolor = "";
   String sfb = "";
   String blocker = "";

   String day_name = "";

   String eMsg = "";        // email message text (html page)
   //String email = "";
   //String email2 = "";
   String support = "backup@foretees.com";                      // email account for backup tee sheets
   String course = "";

   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con2);        // allocate a parm block


   //
   //  Get today's date
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   hr = cal.get(Calendar.HOUR_OF_DAY);           // get 24 hr clock value

   if ( (hr > 21) || (club.equals("theconcession") && hr == 18) ) {             // if current server time is 10:00 PM

      cal.add(Calendar.DATE,1);                  // roll ahead one day
   }

   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH);
   day = cal.get(Calendar.DAY_OF_MONTH);
   day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07)
   month++;                                      // month starts at zero

   day_name = day_table[day_num];                // get name for day

   long date = year * 10000;                     // create a date field of yyyymmdd
   date = date + (month * 100);
   date = date + day;                            // date = yyyymmdd (for comparisons)


   try {

       //
       //  Get number of pro emails to send backups to
       //
       stmt4 = con2.createStatement();
       rs4 = stmt4.executeQuery("SELECT COUNT(*) FROM ( " +
                                "SELECT address1 AS email FROM staff_list WHERE activity_id = 0 AND address1 != '' AND receive_backups1 > 0 AND email_bounced1 = 0 " +
                                "UNION ALL " +
                                "SELECT address2 AS email FROM staff_list WHERE activity_id = 0 AND address2 != '' AND receive_backups2 > 0 AND email_bounced2 = 0 " +
                                ") AS t1");

       if (rs4.next()) {

          emailCount = rs4.getInt(1);       // get number of email addresses to send to
       }

       stmt4.close();

   }
   catch (Exception ex) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error getting email count in SystemUtils.tsheetTimerGolf for club " +club+ ", error: ";
      errorMsg = errorMsg + ex.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
   }


   try {

       //
       //   Get the club parms for this club
       //
       getClub.getParms(con2, parm);      // Activity defaults to Golf

       //email = parm.email;
       //emailOpt = parm.emailOpt;

       //
       //   Remove any guest types that are null - for tests below
       //
       int i = 0;
       while (i < parm.MAX_Guests) {

          if (parm.guest[i].equals( "" )) {

             parm.guest[i] = "$@#!^&*";      // make so it won't match player name
          }
          i++;
       }         // end of while loop

       //
       //  if email address was specified and pro wants emails,
       //  AND (either singleClub is empty [running via timer so do all clubs] or
       //  if singleClub is not empty AND it matchs the current club from rs1)
       //  build today's tee sheet and email it to the club
       //
       // if ( !email.equals("") && emailOpt != 0 ) {
       if ( emailCount > 0 ) {

          Properties properties = new Properties();
          properties.put("mail.smtp.host", host);                // set outbound host address
          properties.put("mail.smtp.port", port);                // set outbound port
          properties.put("mail.smtp.auth", "true");              // attempt to authenticate the user using the AUTH command
          properties.put("mail.smtp.sendpartial", "true");       // a message has some valid and some invalid addresses, send the message anyway

          Session mailSess = Session.getInstance(properties, getAuthenticator()); // get session properties

          MimeMessage message = new MimeMessage(mailSess);
          message.setHeader("Content-Type", "text/html");                              // set html type content
          message.setFrom(new InternetAddress("teesheets@foretees.com"));              // set 'from' addr
          message.setSentDate(new java.util.Date());                                   // set date/time sent
          // message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));  // set 'to' address (do below now)

          // Retrieve additional email address from database and add them
          try {
              stmt4 = con2.createStatement();
              // rs4 = stmt4.executeQuery("SELECT * FROM backup_emails");

              rs4 = stmt4.executeQuery("SELECT address1 FROM staff_list " +
                                       "WHERE activity_id = 0 AND address1 != '' AND receive_backups1 > 0 AND email_bounced1 = 0");

              while (rs4.next()) {
                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(rs4.getString("address1")));
              }

              rs4 = stmt4.executeQuery("SELECT address2 FROM staff_list " +
                                       "WHERE activity_id = 0 AND address2 != '' AND receive_backups2 > 0 AND email_bounced2 = 0");

              while (rs4.next()) {
                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(rs4.getString("address2")));
              }

          } catch (Exception exc) {
              logError("Error in SystemUtils.tsheetTimerGolf for club " +club+ ", error getting emails: " + exc.getMessage());
          }

          stmt4.close();


          //
          //  Copy Support - comment this out to disable
          //
          //message.addRecipient(Message.RecipientType.BCC, new InternetAddress(support));  // 'bcc' address

          //
          //  Set the message subject
          //
          message.setSubject("ForeTees Backup Tee Sheet");

          //
          //  Get parms for each course
          //
          stmt3 = con2.createStatement();

          rs3 = stmt3.executeQuery("SELECT courseName, fives FROM clubparm2");

          eMsg = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=us-ascii\">" +
             "</head><body><center>";

          while (rs3.next()) {

             course = rs3.getString(1);
             fives = rs3.getInt(2);

             eMsg = eMsg + "<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">" +
             "Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + month + "/" + day + "/" + year + "</b></font>";
             if (!course.equals( "" )) {
                eMsg = eMsg + "&nbsp;&nbsp;&nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + course + "</b>";
             }
             eMsg = eMsg + "<table border=\"1\" bgcolor=\"#FFFFCC\" width=\"85%\">" +
             "<tr bgcolor=\"#CC9966\"><td align=\"center\">" +
                   "<font size=\"1\">" +
                   "<u><b>Time</b></u>" +
                   "</font></td>" +

                "<td align=\"center\">" +
                   "<font size=\"1\">" +
                   "<u><b>F/B</b></u>" +
                   "</font></td>" +

                "<td align=\"center\">" +
                   "<font size=\"1\">" +
                   "<u><b>Player 1</b></u> " +
                   "</font><font size=\"1\"><u>hndcp</u>" +
                   "</font></td>" +

                "<td align=\"center\">" +
                   "<font size=\"1\">" +
                   "<u><b>C/W</b></u>" +
                   "</font></td>" +

                "<td align=\"center\">" +
                   "<font size=\"1\">" +
                   "<u><b>Player 2</b></u> " +
                   "</font><font size=\"1\"><u>hndcp</u>" +
                   "</font></td>" +

                "<td align=\"center\">" +
                   "<font size=\"1\">" +
                   "<u><b>C/W</b></u>" +
                   "</font></td>" +

                "<td align=\"center\">" +
                   "<font size=\"1\">" +
                   "<u><b>Player 3</b></u> " +
                   "</font><font size=\"1\"><u>hndcp</u>" +
                   "</font></td>" +

                "<td align=\"center\">" +
                   "<font size=\"1\">" +
                   "<u><b>C/W</b></u>" +
                   "</font></td>" +

                "<td align=\"center\">" +
                   "<font size=\"1\">" +
                   "<u><b>Player 4</b></u> " +
                   "</font><font size=\"1\"><u>hndcp</u>" +
                   "</font></td>" +

                "<td align=\"center\">" +
                   "<font size=\"1\">" +
                   "<u><b>C/W</b></u>" +
                   "</font></td>";

                if (fives != 0 ) {

                   eMsg = eMsg + "<td align=\"center\">" +
                      "<font size=\"1\">" +
                      "<u><b>Player 5</b></u> " +
                      "</font><font size=\"1\"><u>hndcp</u>" +
                      "</font></td>" +

                   "<td align=\"center\">" +
                      "<font size=\"1\">" +
                      "<u><b>C/W</b></u>" +
                      "</font></td>";
                }
                eMsg = eMsg + "</tr>";

             //
             //  Get all tee sheet entries for this club
             //
             PreparedStatement pstmt = con2.prepareStatement (
                "SELECT hr, min, time, event, event_color, restriction, rest_color, player1, player2, " +
                "player3, player4, p1cw, p2cw, p3cw, p4cw, in_use, event_type, hndcp1, hndcp2, hndcp3, hndcp4, " +
                "show1, show2, show3, show4, fb, player5, p5cw, hndcp5, show5, blocker, " +
                "rest5, rest5_color, p91, p92, p93, p94, p95 " +
                "FROM teecurr2 WHERE date = ? AND courseName = ? ORDER BY time, fb");

             pstmt.clearParameters();        // clear the parms
             pstmt.setLong(1, date);         // put the parm in pstmt
             pstmt.setString(2, course);
             rs = pstmt.executeQuery();      // execute the prepared stmt

             while (rs.next()) {

                hr = rs.getInt(1);
                min = rs.getInt(2);
                event = rs.getString(4);
                ecolor = rs.getString(5);
                rest = rs.getString(6);
                rcolor = rs.getString(7);
                player1 = rs.getString(8);
                player2 = rs.getString(9);
                player3 = rs.getString(10);
                player4 = rs.getString(11);
                p1cw = rs.getString(12);
                p2cw = rs.getString(13);
                p3cw = rs.getString(14);
                p4cw = rs.getString(15);
                type = rs.getInt(17);
                hndcp1 = rs.getFloat(18);
                hndcp2 = rs.getFloat(19);
                hndcp3 = rs.getFloat(20);
                hndcp4 = rs.getFloat(21);
                show1 = rs.getShort(22);
                show2 = rs.getShort(23);
                show3 = rs.getShort(24);
                show4 = rs.getShort(25);
                fb = rs.getShort(26);
                player5 = rs.getString(27);
                p5cw = rs.getString(28);
                hndcp5 = rs.getFloat(29);
                show5 = rs.getShort(30);
                blocker = rs.getString("blocker");
                p91 = rs.getInt("p91");
                p92 = rs.getInt("p92");
                p93 = rs.getInt("p93");
                p94 = rs.getInt("p94");
                p95 = rs.getInt("p95");

                if (blocker.equals( "" )) {    // continue if tee time not blocked - else skip

                   if (!club.equals("oakhillcc") || min == 0 || min == 15 || min == 30 || min == 45) {  // Show only 15 minute intervals for oakhillcc

                       ampm = " AM";
                       if (hr == 12) {
                          ampm = " PM";
                       }
                       if (hr > 12) {
                          ampm = " PM";
                          hr = hr - 12;    // convert to conventional time
                       }

                       bgcolor = "#FFFFCC";               //default

                       if (!event.equals("")) {
                          bgcolor = ecolor;
                       } else {

                          if (!rest.equals("")) {
                             bgcolor = rcolor;
                          }
                       }

                       if (bgcolor.equals("Default")) {
                          bgcolor = "#FFFFCC";              //default
                       }

                       if (p91 == 1) {              // if 9 hole round
                          p1cw = p1cw + "9";
                       }
                       if (p92 == 1) {
                          p2cw = p2cw + "9";
                       }
                       if (p93 == 1) {
                          p3cw = p3cw + "9";
                       }
                       if (p94 == 1) {
                          p4cw = p4cw + "9";
                       }
                       if (p95 == 1) {
                          p5cw = p5cw + "9";
                       }

                       if (player1.equals("")) {
                          p1cw = "";
                       }
                       if (player2.equals("")) {
                          p2cw = "";
                       }
                       if (player3.equals("")) {
                          p3cw = "";
                       }
                       if (player4.equals("")) {
                          p4cw = "";
                       }
                       if (player5.equals("")) {
                          p5cw = "";
                       }

                       g1 = 0;     // init guest indicators
                       g2 = 0;
                       g3 = 0;
                       g4 = 0;
                       g5 = 0;

                       //
                       //  Check if any player names are guest names
                       //
                       if (!player1.equals( "" )) {

                          i = 0;
                          ploop1:
                          while (i < parm.MAX_Guests) {

                             if (player1.startsWith( parm.guest[i] )) {
                                g1 = 1;
                                break ploop1;
                             }
                             i++;
                          }
                       }
                       if (!player2.equals( "" )) {

                          i = 0;
                          ploop2:
                          while (i < parm.MAX_Guests) {

                             if (player2.startsWith( parm.guest[i] )) {
                                g2 = 1;
                                break ploop2;
                             }
                             i++;
                          }
                       }
                       if (!player3.equals( "" )) {

                          i = 0;
                          ploop3:
                          while (i < parm.MAX_Guests) {

                             if (player3.startsWith( parm.guest[i] )) {
                                g3 = 1;
                                break ploop3;
                             }
                             i++;
                          }
                       }
                       if (!player4.equals( "" )) {

                          i = 0;
                          ploop4:
                          while (i < parm.MAX_Guests) {

                             if (player4.startsWith( parm.guest[i] )) {
                                g4 = 1;
                                break ploop4;
                             }
                             i++;
                          }
                       }
                       if (!player5.equals( "" )) {

                          i = 0;
                          ploop5:
                          while (i < parm.MAX_Guests) {

                             if (player5.startsWith( parm.guest[i] )) {
                                g5 = 1;
                                break ploop5;
                             }
                             i++;
                          }
                       }

                       //
                       //  Process the F/B parm    0 = Front 9, 1 = Back 9, 9 = none (open for cross-over)
                       //
                       sfb = "F";       // default Front 9

                       if (fb == 1) {

                          sfb = "B";
                       }

                       if (fb == 9) {

                          sfb = "O";
                       }

                       if (type == shotgun) {

                          sfb = "S";            // there's an event and its type is 'shotgun'
                       }

                       eMsg = eMsg + "<tr>" +
                       "<td align=\"center\">" +
                       "<font size=\"1\">";

                       if (type == shotgun) {                             // if Shotgun Event
                          eMsg = eMsg + "Shotgun";
                       } else {
                          if (min < 10) {                                 // if min value is only 1 digit
                             eMsg = eMsg + hr + ":0" + min + ampm;
                          } else {                                        // min value is 2 digits
                             eMsg = eMsg + hr + ":" + min + ampm;
                          }
                       }
                       eMsg = eMsg + "</font></td>" +

                       "<td bgcolor=\"white\" align=\"center\">" +
                          "<font size=\"1\">" +
                          sfb +
                          "</font></td>";

                       if (!player1.equals("")) {

                          players++;                 // bump number of players on tee sheets

                          if (player1.equalsIgnoreCase("x")) {

                             eMsg = eMsg + "<td bgcolor=\"" + bgcolor + "\" align=\"center\">" +
                             "<font size=\"1\">" +
                             player1 +
                             "</font></td>";

                          } else {
                             eMsg = eMsg + "<td bgcolor=\"" + bgcolor + "\" align=\"center\">" +
                             "<font size=\"1\">";

                             p1 = player1;                                       // copy name only

                             if (g1 == 0) {        // if not guest then add hndcp

                                if ((hndcp1 == 99) || (hndcp1 == -99)) {
                                   p1 = p1 + "  NH";
                                } else {
                                   if (hndcp1 <= 0) {
                                      hndcp1 = 0 - hndcp1;                          // convert to non-negative
                                      hndcp = Math.round(hndcp1);                   // round it off
                                      p1 = (p1 + "  " + hndcp);
                                   } else {
                                      hndcp = Math.round(hndcp1);                   // round it off
                                      p1 = (p1 + "  +" + hndcp);
                                   }
                                }
                             }
                             if (show1 == 1) {                         // if player has checked in
                                eMsg = eMsg + "X   ";
                             }
                             eMsg = eMsg + p1 +"</font></td>";
                          }

                       } else {
                          eMsg = eMsg + "<td bgcolor=\"" + bgcolor + "\" align=\"center\">" +
                          "<font size=\"1\">" +
                          "&nbsp;" +
                          "</font></td>";
                       }

                       if ((!player1.equals("")) && (!player1.equalsIgnoreCase( "x" ))) {
                          eMsg = eMsg + "<td bgcolor=\"white\" align=\"center\">" +
                          "<font size=\"1\">" +
                          p1cw;
                       } else {
                          eMsg = eMsg + "<td bgcolor=\"white\" align=\"center\">" +
                          "<font size=\"1\">" +
                          "&nbsp;";
                       }
                          eMsg = eMsg + "</font></td>";

                       if (!player2.equals("")) {

                          players++;                 // bump number of players on tee sheets

                          if (player2.equalsIgnoreCase("x")) {

                             eMsg = eMsg + "<td bgcolor=\"" + bgcolor + "\" align=\"center\">" +
                             "<font size=\"1\">" +
                             player2 +
                             "</font></td>";

                          } else {

                             eMsg = eMsg + "<td bgcolor=\"" + bgcolor + "\" align=\"center\">" +
                             "<font size=\"1\">";

                             p2 = player2;                                       // copy name only

                             if (g2 == 0) {         // if not guest then add ndcp

                                if ((hndcp2 == 99) || (hndcp2 == -99)) {
                                   p2 = p2 + "  NH";
                                } else {
                                   if (hndcp2 <= 0) {
                                      hndcp2 = 0 - hndcp2;                          // convert to non-negative
                                      hndcp = Math.round(hndcp2);                   // round it off
                                      p2 = (p2 + "  " + hndcp);
                                   } else {
                                      hndcp = Math.round(hndcp2);                   // round it off
                                      p2 = (p2 + "  +" + hndcp);
                                   }
                                }
                             }
                             if (show2 == 1) {                         // if player has checked in
                                eMsg = eMsg + "X   ";
                             }
                             eMsg = eMsg + p2 +"</font></td>";
                          }
                       } else {
                          eMsg = eMsg + "<td bgcolor=\"" + bgcolor + "\" align=\"center\">" +
                          "<font size=\"1\">" +
                          "&nbsp;" +
                          "</font></td>" ;
                       }

                       if ((!player2.equals("")) && (!player2.equalsIgnoreCase( "x" ))) {
                          eMsg = eMsg + "<td bgcolor=\"white\" align=\"center\">" +
                          "<font size=\"1\">" +
                          p2cw;
                       } else {
                          eMsg = eMsg + "<td bgcolor=\"white\">" +
                          "<font size=\"1\">" +
                          "&nbsp;";
                       }
                       eMsg = eMsg + "</font></td>";

                       if (!player3.equals("")) {

                          players++;                 // bump number of players on tee sheets

                          if (player3.equalsIgnoreCase("x")) {

                             eMsg = eMsg + "<td bgcolor=\"" + bgcolor + "\" align=\"center\">" +
                             "<font size=\"1\">" +
                             player3 +
                             "</font></td>";

                          } else {

                             eMsg = eMsg + "<td bgcolor=\"" + bgcolor + "\" align=\"center\">" +
                             "<font size=\"1\">";

                             p3 = player3;                                       // copy name only

                             if (g3 == 0) {         // if not guest then add hndcp

                                if ((hndcp3 == 99) || (hndcp3 == -99)) {
                                   p3 = p3 + "  NH";
                                } else {
                                   if (hndcp3 <= 0) {
                                      hndcp3 = 0 - hndcp3;                          // convert to non-negative
                                      hndcp = Math.round(hndcp3);                   // round it off
                                      p3 = (p3 + "  " + hndcp);
                                   } else {
                                      hndcp = Math.round(hndcp3);                   // round it off
                                      p3 = (p3 + "  +" + hndcp);
                                   }
                                }
                             }
                             if (show3 == 1) {                         // if player has checked in
                                eMsg = eMsg + "X   ";
                             }
                             eMsg = eMsg + p3 +"</font></td>";
                          }

                       } else {
                          eMsg = eMsg + "<td bgcolor=\"" + bgcolor + "\">" +
                          "<font size=\"1\">" +
                          "&nbsp;" +
                          "</font></td>";
                       }

                       if ((!player3.equals("")) && (!player3.equalsIgnoreCase( "x" ))) {
                          eMsg = eMsg + "<td bgcolor=\"white\" align=\"center\">" +
                          "<font size=\"1\">" +
                          p3cw;
                       } else {
                          eMsg = eMsg + "<td bgcolor=\"white\">" +
                          "<font size=\"1\">" +
                          "&nbsp;";
                       }
                          eMsg = eMsg + "</font></td>";

                       if (!player4.equals("")) {

                          players++;                 // bump number of players on tee sheets

                          if (player4.equalsIgnoreCase("x")) {

                             eMsg = eMsg + "<td bgcolor=\"" + bgcolor + "\" align=\"center\">" +
                             "<font size=\"1\">" +
                             player4 +
                             "</font></td>";

                          } else {

                             eMsg = eMsg + "<td bgcolor=\"" + bgcolor + "\" align=\"center\">" +
                             "<font size=\"1\">";

                             p4 = player4;                                       // copy name only

                             if (g4 == 0) {         // if not guest then add hndcp

                                if ((hndcp4 == 99) || (hndcp4 == -99)) {
                                   p4 = p4 + "  NH";
                                } else {
                                   if (hndcp4 <= 0) {
                                      hndcp4 = 0 - hndcp4;                          // convert to non-negative
                                      hndcp = Math.round(hndcp4);                   // round it off
                                      p4 = (p4 + "  " + hndcp);
                                   } else {
                                      hndcp = Math.round(hndcp4);                   // round it off
                                      p4 = (p4 + "  +" + hndcp);
                                   }
                                }
                             }
                             if (show4 == 1) {                         // if player has checked in
                                eMsg = eMsg + "X   ";
                             }
                             eMsg = eMsg + p4 +"</font></td>";
                          }
                       } else {
                          eMsg = eMsg + "<td bgcolor=\"" + bgcolor + "\">" +
                          "<font size=\"1\">" +
                          "&nbsp;" +
                          "</font></td>";
                       }

                       if ((!player4.equals("")) && (!player4.equalsIgnoreCase( "x" ))) {
                          eMsg = eMsg + "<td bgcolor=\"white\" align=\"center\">" +
                          "<font size=\"1\">" +
                          p4cw;
                       } else {
                          eMsg = eMsg + "<td bgcolor=\"white\" align=\"center\">" +
                          "<font size=\"1\">" +
                          "&nbsp;";
                       }
                       eMsg = eMsg + "</font></td>";

                       if (fives != 0) {

                          if (!player5.equals("")) {

                             players++;                 // bump number of players on tee sheets

                             if (player5.equalsIgnoreCase("x")) {

                                eMsg = eMsg + "<td bgcolor=\"" + bgcolor + "\" align=\"center\">" +
                                "<font size=\"1\">" +
                                player5 +
                                "</font></td>";

                             } else {

                                eMsg = eMsg + "<td bgcolor=\"" + bgcolor + "\" align=\"center\">" +
                                "<font size=\"1\">";

                                p5 = player5;                                       // copy name only

                                if (g5 == 0) {

                                   if ((hndcp5 == 99) || (hndcp5 == -99)) {
                                      p5 = p5 + "  NH";
                                   } else {
                                      if (hndcp5 <= 0) {
                                         hndcp5 = 0 - hndcp5;                          // convert to non-negative
                                         hndcp = Math.round(hndcp5);                   // round it off
                                         p5 = (p5 + "  " + hndcp);
                                      } else {
                                         hndcp = Math.round(hndcp5);                   // round it off
                                         p5 = (p5 + "  +" + hndcp);
                                      }
                                   }
                                }
                                if (show5 == 1) {                         // if player has checked in
                                   eMsg = eMsg + "X   ";
                                }
                                eMsg = eMsg + p5 +"</font></td>";
                             }
                          } else {
                             eMsg = eMsg + "<td bgcolor=\"" + bgcolor + "\">" +
                             "<font size=\"1\">" +
                             "&nbsp;" +
                             "</font></td>";
                          }

                          if ((!player5.equals("")) && (!player5.equalsIgnoreCase( "x" ))) {
                             eMsg = eMsg + "<td bgcolor=\"white\" align=\"center\">" +
                             "<font size=\"1\">" +
                             p5cw;
                          } else {
                             eMsg = eMsg + "<td bgcolor=\"white\" align=\"center\">" +
                             "<font size=\"1\">" +
                             "&nbsp;";
                          }
                          eMsg = eMsg + "</font></td>";
                       }
                       eMsg = eMsg + "</tr>";

                    }  // end of if not oakhillcc or is oakhillcc and is 15 min interval

                }  // end of IF Blocker

             }  // end of while

             pstmt.close();


             eMsg = eMsg + "</td></tr></table></font><br><br><br>";  // end of course table

          }   // end of while more courses

          stmt3.close();


          if (players != 0) {           // do not send sheets if no players on any of them

             eMsg = eMsg + "</center></body></html>";           // end of html page

             message.setContent(eMsg, "text/html");             // put msg in email text area

             try {

                Transport.send(message);                        // send it!!

             } catch (Exception exp) {

                if (out != null) {
                    out.println("<p>Error sending email:");
                    out.println("<br>Error Message: " + exp.getMessage());
                    out.println("<br>Error String: " + exp.toString());
                    //out.println("<br>From Address: " + message.getFrom());
                    //String tmp_from = ""; //message.getFrom();
                    //tmp_from = new Address[]{new InternetAddress(message.getFrom())}.toString();
                    //tmp_from = new Address[]{new InternetAddress("me@mr.com")}.toString();
                    //tmp_from = new InternetAddress(message.getFrom()).getAddress();
                    //out.println("<br>From Address: " + tmp_from);
                    out.println("<br>Message:<br><br><pre>" + eMsg + "</pre>");
                    out.println("</p>");
                }
                logError("Error in SystemUtils.tsheetTimerGolf for club " +club+ ", error sending email: " + exp.getMessage());

             }

          } // end if players found

       } // end of IF email address and emailOpt check


   }
   catch (Exception e2) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error1 in SystemUtils.tsheetTimerGolf for club " +club+ ", error: ";
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
   }

 }  // end of tsheetTimerGolf


 //************************************************************************
 // tsheetTimerAct - Email backup Activity sheets to pro
 //
 //   called by:  tsheetTimer above on timer expiration
 //
 //************************************************************************

 public static void tsheetTimerAct(String club, PrintWriter out, Connection con) {

   Statement stmt = null;
   Statement stmt2 = null;
   PreparedStatement pstmt = null;
   PreparedStatement pstmt2 = null;
   PreparedStatement pstmt3 = null;
   ResultSet rs = null;
   ResultSet rs1 = null;
   ResultSet rs2 = null;
   ResultSet rs3 = null;

   int players = 0;
   int group_id = 0;
   int email_count = 0;
   int i = 0;
   int cols = 0;
   int activity_id = 0;

   String day_name = "";
   String act_name = "";
   String fullName = "";
   String lastName = "";
   String actName = "";
   String eMsg = "";            // email message text (html page)
   String lname = "";
   String result = "";
   String support = "backup@foretees.com";                      // BCC email account for OUR copy of their backup sheets

   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

   //
   //  Get today's date
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   int hr = cal.get(Calendar.HOUR_OF_DAY);           // get 24 hr clock value

   if (hr > 21) {                                // if current server time is 10:00 PM

      cal.add(Calendar.DATE,1);                  // roll ahead one day
   }

   int year = cal.get(Calendar.YEAR);
   int month = cal.get(Calendar.MONTH) +1;
   int day = cal.get(Calendar.DAY_OF_MONTH);
   int day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07)

   day_name = day_table[day_num];                // get name for day

   int date = year * 10000;                     // create a date field of yyyymmdd
   date = date + (month * 100);
   date = date + day;                            // date = yyyymmdd (for comparisons)

   //
   //    For activities we will send all backup sheets to each owner that has an email address entered (could be one for every activity, group and sheet)
   //
   try {

       stmt = con.createStatement();
       rs = stmt.executeQuery("" +
               "SELECT sl.activity_id, a.activity_name FROM staff_list sl " +
               "LEFT OUTER JOIN activities a ON sl.activity_id = a.activity_id " +
               "WHERE sl.activity_id != 0 AND ((address1 != '' AND receive_backups1 = 1) OR (address2 != '' AND receive_backups2 = 1)) GROUP BY sl.activity_id ORDER BY sl.activity_id");

       while (rs.next()) {

           //
           //  if email address was specified and pro wants emails,
           //  AND (either singleClub is empty [running via timer so do all clubs] or
           //  if singleClub is not empty AND it matchs the current club from rs1)
           //  build today's tee sheet and email it to the club
           //
          //String [] emailA = new String [email_count];        // array to hold the email addresses
          ArrayList<String> emailA = new ArrayList<String>();

          activity_id = rs.getInt("sl.activity_id");
          act_name = rs.getString("a.activity_name");

          i = 0;

          try {
              pstmt = con.prepareStatement("SELECT address1, receive_backups1, address2, receive_backups2 FROM staff_list WHERE activity_id = ? AND ((address1 != '' AND receive_backups1 = 1) OR (address2 != '' AND receive_backups2 = 1))");
              pstmt.clearParameters();
              pstmt.setInt(1, activity_id);

              rs2 = pstmt.executeQuery();  // get unique eamil addresses

              while (rs2.next()) {

                 if (!rs2.getString("address1").equals("") && rs2.getInt("receive_backups1") == 1) {
                     emailA.add(rs2.getString("address1"));
                 }
                 if (!rs2.getString("address2").equals("") && rs2.getInt("receive_backups2") == 1) {
                     emailA.add(rs2.getString("address2"));
                 }
                 //emailA.add(rs2.getString("email"));    // get email of pro
                 //i++;
              }
          } catch (Exception exc) {

              logError("Error in SystemUtils.tsheetTimerAct gathering staff addresses for club " +club+ ", error: " + exc.getMessage());

          } finally {

              try { rs2.close(); }
              catch (Exception ignore) { }

              try { pstmt.close(); }
              catch (Exception ignore) { }
          }

          //
          //   Build the email message
          //
          Properties properties = new Properties();
          properties.put("mail.smtp.host", host);                // set outbound host address
          properties.put("mail.smtp.port", port);                // set outbound port
          properties.put("mail.smtp.auth", "true");              // set 'use authentication'
          properties.put("mail.smtp.sendpartial", "true");       // a message has some valid and some invalid addresses, send the message anyway

          Session mailSess = Session.getInstance(properties, getAuthenticator()); // get session properties

          MimeMessage message = new MimeMessage(mailSess);
          message.setHeader("Content-Type", "text/html");                              // set html type content
          message.setFrom(new InternetAddress("timesheets@foretees.com"));             // set 'from' addr
          message.setSentDate(new java.util.Date());                                   // set date/time sent

          for (i=0; i<emailA.size(); i++) {         // add each pro's email address

              message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailA.get(i)));  // set 'to' addresses
          }

          //
          //  Copy Support - comment this out to disable
          //
          //message.addRecipient(Message.RecipientType.BCC, new InternetAddress(support));  // 'bcc' address

          //
          //  Set the message subject
          //
          message.setSubject("FlxRez Backup " +act_name+ " Sheets");

          eMsg = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=us-ascii\">" +
                  "</head><body><center>";

          players = 0;                    // init total number of players on all sheets

          try {

              //
              //  Get the activity sheets and group them by parent activity - to get the lowest level group id
              //
              String childString = getActivity.buildInString(activity_id, 1, con);

              stmt2 = con.createStatement();
              rs2 = stmt2.executeQuery("" +
                      "SELECT parent_id FROM activities " +
                      "WHERE activity_id IN (" + childString + ") AND activity_id NOT IN (SELECT parent_id FROM activities) " +
                      "GROUP BY parent_id");

              while (rs2.next()) {       // get each activity sheet (lowest level activities)

                 group_id = rs2.getInt("parent_id");             // get group id of this activity (parent)
                 //
                 //  Add each sheet to the email message
                 //
                 fullName = getActivity.getFullActivityName(group_id, con);  // get the full name of this group activity (i.e. Racquet Sports -> Tennis)


                 // Use LAYOUT MODE = 3 - Individual Sheets in Detail Mode

                 eMsg += "<BR><BR><b>" + fullName + "</b><BR>";     // group name

                 eMsg += "<table align=center border=0><tr valign=top><td>";     // one big table for whole sheet to align in center of page

                 try {

                     // Get Activlity Sheets in order by name, then time
                     pstmt2 = con.prepareStatement("" +
                               "SELECT *, activity_name, max_players, " +
                                   "DATE_FORMAT(date_time, \"%l:%i %p\") AS time, " +
                                   "DATE_FORMAT(date_time, \"%Y%m%d\") AS date " +
                               "FROM activity_sheets t1 " +
                               "LEFT OUTER JOIN activities t2 ON t2.activity_id = t1.activity_id " +
                               "WHERE " +
                                   "t1.activity_id IN (SELECT activity_id FROM activities WHERE parent_id = ?) AND " +
                                   "DATE_FORMAT(date_time, \"%Y%m%d\") = ? " +
                               "ORDER BY activity_name, date_time");
                       pstmt2.clearParameters();
                       pstmt2.setInt(1, group_id); // activity_id
                       pstmt2.setInt(2, date);
                       rs1 = pstmt2.executeQuery();

                       int players_found = 0;
                       boolean alt = true;
                       String last_time = "";

                       while (rs1.next()) {

                          actName = rs1.getString("activity_name");       // get the name of this activity
                          cols = rs1.getInt("max_players") +1;            // # of cols in table

                          if (!actName.equals( lastName )) {           // if new Sheet

                             if (!lastName.equals("")) {               // if not the first sheet

                                eMsg += "</table><BR><BR>";
                             }

                             lastName = actName;

                             eMsg += "<table border=1 class=\"timesheet\" align=center style=\"align:center;\">";

                             // header row
                             eMsg += "<tr class=timesheetTH><td class=headerTD colspan=" +cols+ " align=center><strong>" +actName+ "</strong></td></tr>";    // name of Sheet
                             eMsg += "<tr class=timesheetTH><td class=headerTD>Time</td>";
                             for (i = 1; i < cols; i++) {

                                eMsg += "<td class=headerTD>Player " + i + "</td>";
                             }
                             eMsg += "</tr>";
                          }

                          // output each time

                           if ( !last_time.equals(rs1.getString("time")) ) {

                               last_time = rs1.getString("time");
                               alt = (alt == false); // toggle row shading
                           }

                           // hide blocked times
                           if (rs1.getInt("blocker_id") == 0 && rs1.getInt("auto_blocked") == 0) {

                               players_found = 0; // reset
                               eMsg += "<tr class=\"timesheetTR\"><td align=center><font size=2>" + rs1.getString("time") + "</font></td>";

                               if (rs1.getInt("lesson_id") != 0) {

                                   result = verifyLesson.getLessonInfo(rs1.getInt("lesson_id"), con);

                                   StringTokenizer tok = new StringTokenizer( result, "|" );

                                   lname = tok.nextToken();       // get the name of this lesson

                                   eMsg += "<td class=timesheetTD align=\"center\" nowrap colspan=\"" + rs1.getInt("max_players") + "\">" + lname + "</td>";

                               } else {

                                   try {

                                       pstmt3 = con.prepareStatement("" +
                                               "SELECT * " +
                                               "FROM activity_sheets_players " +
                                               "WHERE activity_sheet_id = ? " +
                                               "ORDER BY pos");
                                       pstmt3.clearParameters();
                                       pstmt3.setInt(1, rs1.getInt("sheet_id"));
                                       rs3 = pstmt3.executeQuery();

                                       while ( rs3.next() ) {

                                           players_found++;
                                           players++;

                                           eMsg += "<td class=timesheetTD nowrap>&nbsp;<img src=\"/" +rev+ "/images/";

                                            switch (rs3.getInt("show")) {
                                            case 1:
                                                eMsg += "xbox.gif";
                                                break;
                                            case 2:
                                                eMsg += "rmtbox.gif";
                                                break;
                                            default:
                                                eMsg += "mtbox.gif";
                                                break;
                                            }

                                            // check-in image and player name
                                            eMsg += "\" border=\"1\" name=\"noShow\">&nbsp;" + rs3.getString("player_name") + "</td>";

                                       } // end player loop

                                       pstmt3.close();

                                   } catch (Exception exc) {

                                       eMsg += "<p>ERROR LOADING PLAYER:" + exc.toString() + "</p>";

                                   } finally {

                                       try { rs3.close(); }
                                       catch (Exception ignore) { }

                                       try { pstmt3.close(); }
                                       catch (Exception ignore) { }
                                   }

                                   // see if we need to fill in any remaining player positions for this time slot
                                   while (players_found < rs1.getInt("max_players")) {

                                       if (rs1.getInt("force_singles") > 0) {                // if force_singles selected
                                          eMsg += "<td class=timesheetTD>&nbsp;N/A</td>";       // Indicate slot not available
                                       } else {
                                          eMsg += "<td class=timesheetTD>&nbsp;</td>";
                                       }
                                       players_found++;
                                   }
                               }
                               
                               eMsg += "</tr>";

                           } // end if blocked

                     } // end time slot rs loop

                 } catch (Exception exc) {

                     logError("Error2 in SystemUtils.tsheetTimerAct for club " +club+ ", error sending email: " + exc.getMessage());

                 } finally {

                     try { rs1.close(); }
                     catch (Exception ignore) { }

                     try { pstmt2.close(); }
                     catch (Exception ignore) { }
                 }

                 eMsg += "</table></div></div></td><tr></table>";
               }        // end of WHILE activities to process

          } catch (Exception exc) {

              logError("Error2 in SystemUtils.tsheetTimerAct for club " +club+ ", error sending email: " + exc.getMessage());

          } finally {

              try { rs2.close(); }
              catch (Exception ignore) { }

              try { stmt2.close(); }
              catch (Exception ignore) { }
          }
           //
           //  Send email if any players were found on any sheets
           //
           if (players != 0) {           // do not send sheets if no players on any of them

              eMsg = eMsg + "</center></body></html>";           // end of html page

              message.setContent(eMsg, "text/html");             // put msg in email text area

              try {

                 Transport.send(message);                        // send it!!

              } catch (Exception exp) {

                 if (out != null) {
                    out.println("<p>Error sending email:");
                    out.println("<br>Error Message: " + exp.getMessage());
                    out.println("<br>Error String: " + exp.toString());
                    //out.println("<br>From Address: " + message.getFrom());
                    //String tmp_from = ""; //message.getFrom();
                    //tmp_from = new Address[]{new InternetAddress(message.getFrom())}.toString();
                    //tmp_from = new Address[]{new InternetAddress("me@mr.com")}.toString();
                    //tmp_from = new InternetAddress(message.getFrom()).getAddress();
                    //out.println("<br>From Address: " + tmp_from);
                    out.println("<br>Message:<br><br><pre>" + eMsg + "</pre>");
                    out.println("</p>");
                 }
                 logError("Error in SystemUtils.tsheetTimerAct for club " +club+ ", error sending email: " + exp.getMessage());
              }

           }      // end if players found

       }    // End of activity_id loop

   } catch (Exception e2) {

       logError("Error1 in SystemUtils.tsheetTimerAct for club " +club+ ", email_count = " + email_count + ", error: " + e2.getMessage());                                       // log it

   } finally {

       try { rs.close(); }
       catch (Exception ignore) { }

       try { stmt.close(); }
       catch (Exception ignore) { }
   }

 }  // end of tsheetTimerAct




 //************************************************************************
 //  adjustTime - receives a time value (hhmm) and adjusts it for the club's
 //               specified time zone.
 //
 //   **************** See also adjustTime2 below ****************
 //
 //  Called by:
 //              Proshop_select
 //              Member_select
 //              moveReqs (and others above)
 //
 //
 //   returns: time (hhmm) - negative value if it rolled back or ahead a day
 //
 //************************************************************************

 public static int adjustTime(Connection con, int time) {

   //
   //  Call common for this so we don't have to maintain 2 methods
   //
   time = Utilities.adjustTime(con, time);   // get adjusted time value

   return( time );

 }  // end of adjustTime



 //************************************************************************
 //  adjustTime2 - receives a time value (hhmm) and adjusts it for the club's
 //               specified time zone.
 //
 //  ******* Same as adjustTime above except that it uses 'long' values - for Reports ***********
 //
 //
 //  Called by:
 //              Proshop_reports
 //
 //
 //   returns: time (hhmm) - negative value if it rolled back or ahead a day
 //
 //************************************************************************

 public static long adjustTime2(Connection con, long time) {


   Statement stmt = null;
   ResultSet rs = null;

   long hour = 0;
   long min = 0;
   boolean roll = false;
   boolean DST = false;           // Day Light Savings

   String adv_zone = "";

   //
   //  separate hour and min from time
   //
   hour = time / 100;                    // 00 - 23
   min = time - (hour * 100);            // 00 - 59

   //
   //  get the club's time zone
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT adv_zone " +
                              "FROM club5");

      if (rs.next()) {

         adv_zone = rs.getString(1);
      }
      stmt.close();

   }
   catch (Exception e2) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils adjustTime: ";
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

//      logError(errorMsg);                                       // log it
   }

   if (adv_zone.equals( "Eastern" )) {      // Eastern Time = +1 hr

      hour++;                // adjust the hour value

      if (hour == 24) {

         hour = 0;           // keep from 0 to 23
         roll = true;        // rolled ahead a day
      }
   }

   if (adv_zone.equals( "Mountain" )) {      // Mountain Time = -1 hr

      if (hour == 0) {

         hour = 24;          // change so it can be adjusted below
         roll = true;        // rolled back a day
      }

      hour--;                // adjust the hour value
   }

   if (adv_zone.equals( "Pacific" )) {      // Pacific Time = -2 hrs

      if (hour == 0) {

         hour = 22;          // adjust it
         roll = true;        // rolled back a day

      } else {

         if (hour == 1) {

            hour = 23;          // adjust it
            roll = true;        // rolled back a day

         } else {

            hour = hour - 2;             // adjust the hour value
         }
      }
   }


   //
   //  Arizona - no DST
   //
   if (adv_zone.equals( "Arizona" )) {      // Mountain or Pacific Time (no DST)

      DST = checkDST(0);                     // check if DST here in CT zone

      if (hour == 0) {

         if (DST == true) {
            hour = 22;          // adjust it -2 in summer
         } else {
            hour = 23;          // adjust it -1
         }
         roll = true;           // rolled back a day

      } else {

         if (hour == 1) {

            if (DST == true) {
               hour = 23;           // adjust it -2 in summer
               roll = true;         // rolled back a day
            } else {
               hour = 0;            // adjust it -1 (no roll back)
            }

         } else {                    // its 2:00 AM or later in CT zone

            if (DST == true) {
               hour = hour - 2;             // -2 in summer
            } else {
               hour = hour - 1;             // -1 in winter
            }
         }
      }
   }


   //
   //  Hawaii time - never goes to DST.  If our DST, then subtract 5 hrs, else -4.
   //
   if (adv_zone.equals( "Hawaiian" )) {      // Hawaiian Time = -4 or -5 hrs (no DST in Hawaii)

      DST = checkDST(0);                  // check if DST here

      if (hour == 0) {

         if (DST == true) {
            hour = 19;          // adjust it -5
         } else {
            hour = 20;          // adjust it -4
         }
         roll = true;        // rolled back a day

      } else {

         if (hour == 1) {

            if (DST == true) {
               hour = 20;          // adjust it -5
            } else {
               hour = 21;          // adjust it -4
            }
            roll = true;        // rolled back a day

         } else {

            if (hour == 2) {

               if (DST == true) {
                  hour = 21;          // adjust it -5
               } else {
                  hour = 22;          // adjust it -4
               }
               roll = true;        // rolled back a day

            } else {

               if (hour == 3) {

                  if (DST == true) {
                     hour = 22;          // adjust it -5
                  } else {
                     hour = 23;          // adjust it -4
                  }
                  roll = true;        // rolled back a day

               } else {

                  if (hour == 4) {

                     if (DST == true) {
                        hour = 23;          // adjust it -5
                        roll = true;        // rolled back a day
                     } else {
                        hour = 0;          // adjust it -4 (no roll back)
                     }

                  } else {

                     if (DST == true) {
                        hour = hour - 5;             // adjust the hour value
                     } else {
                        hour = hour - 4;             // adjust the hour value
                     }
                  }
               }
            }
         }
      }
   }


   //
   //  Saudi Arabia time (GMT + 3) - never goes to DST.  If our DST, then add 8 hrs, else add 9.
   //
   if (adv_zone.equals( "Saudi" )) {      // Saudi Time = +8 or +9 hrs (no DST)

      DST = checkDST(0);                  // check if DST here

      if (DST == true) {

         hour += 8;                   // DST - roll ahead 8 hours

      } else {

         hour += 9;                   // NOT DST - roll ahead 9 hours
      }

      if (hour == 24) {

         hour = 0;
         roll = true;                 // midnight the next day

      } else if (hour > 24) {

         hour = hour - 24;
         roll = true;                 // some time the next morning
      }

   }      // end of Saudi Arabia


   time = (hour * 100) + min;

   if (roll == true) {

      time = (0 - time);        // create negative value to indicate we rolled ahead or back one day
   }

   return( time );

 }  // end of adjustTime2


 //************************************************************************
 //  adjustTimeBack - receives a time value (hhmm) and adjusts it for the club's
 //                   specified time zone.  This adjusts in the opposite
 //                   direction as adjustTime above.
 //
 //                   This is used to adjust a time value to be used to process
 //                   an event at the correct time in the future.
 //
 //
 //  Called by:  Proshop_lott
 //              Member_lott
 //              Proshop_editlottery
 //
 //
 //   returns: time (hhmm) - negative value if it rolled back or ahead a day
 //
 //   NOTE:  refer to adjustTime in common/Utilities !!!!!!
 //
 //************************************************************************

 public static int adjustTimeBack(Connection con, int time, long date) {


   Statement stmt = null;
   ResultSet rs = null;

   int hour = 0;
   int min = 0;
   boolean roll = false;
   boolean DST = false;           // Day Light Savings

   String adv_zone = "";

   //
   //  separate hour and min from time
   //
   hour = time / 100;                    // 00 - 23
   min = time - (hour * 100);            // 00 - 59

   //
   //  get the club's time zone
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT adv_zone " +
                              "FROM club5 WHERE clubName != ''");

      if (rs.next()) {

         adv_zone = rs.getString(1);
      }
      stmt.close();

   }
   catch (Exception e2) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils adjustTimeBack: ";
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
   }

   if (adv_zone.equals( "Eastern" )) {      // Eastern Time = -1 hr

      if (hour == 0) {

         hour = 24;          // change so it can be adjusted below
         roll = true;        // rolled back a day
      }

      hour--;                // adjust the hour value
   }

   if (adv_zone.equals( "Mountain" )) {      // Mountain Time = +1 hr

      hour++;                // adjust the hour value

      if (hour == 24) {

         hour = 0;           // keep from 0 to 23
         roll = true;        // rolled ahead a day
      }
   }

   if (adv_zone.equals( "Pacific" )) {      // Pacific Time = +2 hrs

      hour++;                // adjust the hour value

      if (hour == 24) {

         hour = 0;           // keep from 0 to 23
         roll = true;        // rolled back a day
      }
      hour++;                // adjust the hour value

      if (hour == 24) {

         hour = 0;           // keep from 0 to 23
         roll = true;        // rolled back a day
      }

   }

   //
   //  Arizona - no DST
   //
   if (adv_zone.equals( "Arizona" )) {      // Mountain or Pacific Time (no DST)

      DST = checkDST(date);                     // check if DST here in CT zone

      hour++;                // adjust the hour value

      if (hour == 24) {

         hour = 0;           // keep from 0 to 23
         roll = true;        // rolled back a day
      }

      if (DST == true) {

         hour++;                // adjust the hour value

         if (hour == 24) {

            hour = 0;           // keep from 0 to 23
            roll = true;        // rolled back a day
         }
      }
   }


   //
   //  Hawaii time - never goes to DST.  If our DST, then add 5 hrs, else +4.
   //
   if (adv_zone.equals( "Hawaiian" )) {      // Hawaiian Time = +4 or +5 hrs (no DST in Hawaii)

      DST = checkDST(date);                  // check if DST here

      hour++;                // adjust the hour value

      if (hour == 24) {

         hour = 0;
         roll = true;
      }

      hour++;                // add 1

      if (hour == 24) {

         hour = 0;
         roll = true;
      }

      hour++;                // add 1

      if (hour == 24) {

         hour = 0;
         roll = true;
      }

      hour++;                // add 1

      if (hour == 24) {

         hour = 0;
         roll = true;
      }

      if (DST == true) {        // if Daylight Savings Time here (5 hrs)

         hour++;                // add 1

         if (hour == 24) {

            hour = 0;
            roll = true;
         }
      }
   }


   //
   //  Saudi Arabia time (GMT + 3) - never goes to DST.  If our DST, then subtract 8 hrs, else subtract 9.
   //
   if (adv_zone.equals( "Saudi" )) {      // Saudi Time = +8 or +9 hrs (no DST)

      DST = checkDST(date);                  // check if DST here

      int diff = 9;                   // hours difference for non-DST

      if (DST == true) {

         diff = 8;                    // DST - roll back 8 hours
      }

      if (hour >= diff) {

         hour = hour - diff;

      } else {

         while (diff > 0) {

            if (hour == 0) hour = 24;

            hour--;
            diff--;
         }

         roll = true;
      }

   }      // end of Saudi Arabia


   time = (hour * 100) + min;

   if (roll == true) {

      time = (0 - time);        // create negative value to indicate we rolled back one day
   }

   return( time );

 }  // end of adjustTimeBack


 //************************************************************************
 //
 //  checkDST - Check if we are now in Daylight Savings Time
 //
 //************************************************************************

 public static boolean checkDST(long ldate) {


   boolean DST = true;

   int sdate = 0;
   int edate = 0;
   int yy = 0;
   int date = (int)ldate;

   if (date == 0) {

      //
      //   Get current date
      //
      Calendar cal = new GregorianCalendar();                      // get todays date
      yy = cal.get(Calendar.YEAR);
      int mm = cal.get(Calendar.MONTH) +1;
      int dd = cal.get(Calendar.DAY_OF_MONTH);

      date = (yy * 10000) + (mm * 100) + dd;      // get today

   } else {

      yy = date / 10000;                            // get year
   }

   //
   //  Determine start and end of Daylight Saving Time
   //
   if (yy == 2009) {

      sdate = 20090308;
      edate = 20091031;

   } else if (yy == 2010) {

      sdate = 20100314;
      edate = 20101106;

   } else if (yy == 2011) {

      sdate = 20110313;
      edate = 20111105;

   } else if (yy == 2012) {

      sdate = 20120311;
      edate = 20121103;

   } else if (yy == 2013) {

      sdate = 20130310;
      edate = 20131102;

   } else if (yy == 2014) {

      sdate = 20140309;
      edate = 20141101;

   } else if (yy == 2015) {

      sdate = 20150308;
      edate = 20151031;

   }

   if (date < sdate || date > edate) {      // if not DST today

      DST = false;
   }

   return( DST );

 }  // end of checkDST


 //************************************************************************
 //  newGuest - Correct the stats after the Guest Types were changed.
 //             This is done when the names or the order has changed.
 //
 //  Called by:  Proshop_club
 //
 //
 //   receives:  a club parm block
 //
 //   returns: void
 //
 //************************************************************************

 public static void newGuest(Connection con, parmClub parm)
                          throws Exception {


   //
   //  This method is no longer used - log error if it is called
   //
   logError("Error in SystemUtils.newGuest - method no longer used.  Caller should be updated.");

 }  // end of newGuest


 //************************************************************************
 //  newMem - Correct the stats after the Member Types were changed.
 //
 //  Called by:  Proshop_club
 //
 //
 //   receives:  a club parm block
 //
 //   returns: void
 //
 //************************************************************************

 public static void newMem(Connection con, parmClub parm)
                          throws Exception {


   //
   //  This method is no longer used - log error if it is called
   //
   logError("Error in SystemUtils.newMem - method no longer used.  Caller should be updated.");

 }  // end of newMem


 //************************************************************************
 //  newTmode - Correct the stats after a Mode of Transportation was changed.
 //
 //  Called by:  Proshop_parm
 //
 //
 //   receives:  a club parm block
 //
 //   returns: void
 //
 //************************************************************************

 public static void newTmode(Connection con, parmClub parm, String course) {


   //
   //  This method is no longer used - log error if it is called
   //
   logError("Error in SystemUtils.newTmode - method no longer used.  Caller should be updated.");

 }  // end of newTmode


 //************************************************************************
 //  newMship - Correct the stats after the Membership Types were changed.
 //
 //  Called by:  Proshop_club
 //
 //
 //   receives:  a club parm block
 //
 //   returns: void
 //
 //************************************************************************

 public static void newMship(Connection con, parmClub parm)
                          throws Exception {


   //
   //  This method is no longer used - log error if it is called
   //
   logError("Error in SystemUtils.newMship - method no longer used.  Caller should be updated.");

 }  // end of newMship


 //************************************************************************
 //  removeLessonBlockers - removes any old blockers from the Lesson Books
 //
 //       This is done to keep the db table clean and to eliminate some
 //       duplicate name errors when pro is creating new blockers.
 //
 //  called by:  scanTee
 //
 //************************************************************************

 public static void removeLessonBlockers(Connection con, long today_date, String club)
           throws Exception {


   PreparedStatement pstmt2;

   //
   //  ************* today_date has been adjusted for time zone by scanTee *********
   //
   //  Remove all blockers that are more than a year old
   //
   long lastYear = today_date - 10000;     //   Get date of 1 year ago

   try {

      pstmt2 = con.prepareStatement (
                   "DELETE FROM lessonblock5 WHERE edate < ?");

      pstmt2.clearParameters();        // clear the parms
      pstmt2.setLong(1, lastYear);     // put the parms in stmt for tee slot
      pstmt2.executeUpdate();          // delete all tee slots for oldest date

      pstmt2.close();

   }
   catch (SQLException exc1) {

      throw new Exception("Error removing Old Lesson Blockers - removeLessonBlocker");
   }

 }  // end of removeLessonBlocker


 //************************************************************************
 //  removeOldEvents - removes any old inactive events and event signups
 //
 //     Events and event signups are not deleted when the pro thinks they are deleting them.
 //     This is because they so often do this without thinking about the consequences. We
 //     now save them so we can easily restore them.  We will delete the old ones here.
 //
 //  called by:  scanTee
 //
 //************************************************************************

 public static void removeOldEvents(Connection con, long today_date, String club)
           throws Exception {


   PreparedStatement pstmt1 = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;

   String name = "";


   //
   //  ************* today_date has been adjusted for time zone by scanTee *********
   //
   //  Remove all Inactive events and event signups that are more than 1 year old
   //
   long lastYear = today_date - 10000;     //   Get date of 1 year ago

   try {

      //
      //  Get any Inactive events that are more than 1 year old (one year from date of the event)
      //
      pstmt2 = con.prepareStatement (
         "SELECT name FROM events2b " +
         "WHERE date < ? AND inactive = 1");

      pstmt2.clearParameters();              // clear the parms
      pstmt2.setLong(1, lastYear);               // put the parm in stmt
      rs = pstmt2.executeQuery();            // execute the prepared stmt

      while (rs.next()) {

         name = rs.getString(1);

         //
         //  Delete any inactive signups for this event
         //
         pstmt1 = con.prepareStatement (
                      "DELETE FROM evntsup2b WHERE name = ? AND inactive = 1");

         pstmt1.clearParameters();
         pstmt1.setString(1, name);
         pstmt1.executeUpdate();

         pstmt1.close();

      }
      pstmt2.close();   // close the stmt

      //
      //   Now remove the old inactive events
      //
      pstmt2 = con.prepareStatement (
                   "DELETE FROM events2b WHERE date < ? AND inactive = 1");

      pstmt2.clearParameters();
      pstmt2.setLong(1, lastYear);
      pstmt2.executeUpdate();

      pstmt2.close();

   }
   catch (SQLException exc1) {

      throw new Exception("Error removing Old Events - removeOldEvents");
   }

 }  // end of removeOldEvents


 //************************************************************************
 //  optimize - optimize the club's db tables every Wednesday morning
 //
 //  Called by:  teeTimer (above) when timer expires at 2:30 AM
 //
 //
 //************************************************************************

 public static void optimize(Connection con) {


   Statement stmt = null;
   ResultSet rs = null;

   boolean b = false;

   //
   //  Get the current day
   //
   Calendar cal = new GregorianCalendar();        // get todays date
   int day_name = cal.get(Calendar.DAY_OF_WEEK);

   if (day_name == 4) {             // if Wednesday

      try {
         //
         //  Optimize the tables
         //
         stmt = con.createStatement();           // create a statement

         b = stmt.execute("OPTIMIZE TABLE member2b");          // member2b

         stmt.close();

         stmt = con.createStatement();           // create a statement

         b = stmt.execute("OPTIMIZE TABLE teecurr2");          // teecurr2

         stmt.close();

         stmt = con.createStatement();           // create a statement

         b = stmt.execute("OPTIMIZE TABLE events2b");          // events2b

         stmt.close();

         stmt = con.createStatement();           // create a statement

         b = stmt.execute("OPTIMIZE TABLE evntsup2b");          // evntsup2b

         stmt.close();

         stmt = con.createStatement();           // create a statement

         b = stmt.execute("OPTIMIZE TABLE restriction2");          // restriction2

         stmt.close();

         stmt = con.createStatement();           // create a statement

         b = stmt.execute("OPTIMIZE TABLE fives2");          // fives2

         stmt.close();

         stmt = con.createStatement();           // create a statement

         b = stmt.execute("OPTIMIZE TABLE block2");          // block2

         stmt.close();

         stmt = con.createStatement();           // create a statement

         b = stmt.execute("OPTIMIZE TABLE guestres2");          // guestres2

         stmt.close();

         stmt = con.createStatement();           // create a statement

         b = stmt.execute("OPTIMIZE TABLE dbltee2");          // dbltee2

         stmt.close();

         stmt = con.createStatement();           // create a statement

         b = stmt.execute("OPTIMIZE TABLE partner");          // partner

         stmt.close();

         stmt = con.createStatement();           // create a statement

         b = stmt.execute("OPTIMIZE TABLE lottery3");          // lottery3

         stmt.close();

         stmt = con.createStatement();           // create a statement

         b = stmt.execute("OPTIMIZE TABLE lreqs3");          // lreqs3

         stmt.close();

         stmt = con.createStatement();           // create a statement

         b = stmt.execute("OPTIMIZE TABLE actlott3");          // actlott3

         stmt.close();

      }
      catch (Exception ignore) {
      }
   }
 }  // end of optimize


 //************************************************************************
 // setNoShowOW - Custom Processing for Old Warson CC & CC of Lincoln
 //
 //   If the 'Pre-CheckIn' feature is enabled then set all players in today's
 //   tee sheet to 'new' ('2').  They use this for the bag room to know when
 //   bags have been prepared.  the bag room staff will check-in the players
 //   the first time after the player's bag is put out.
 //
 //   called by:  inactTimer
 //
 //************************************************************************

 public static void setNoShowOW(long date, Connection con) {


   PreparedStatement pstmt = null;
   PreparedStatement pstmt1 = null;
   ResultSet rs = null;


   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";

   int show1 = 0;
   int show2 = 0;
   int show3 = 0;
   int show4 = 0;
   int show5 = 0;
   int fb = 0;
   int time = 0;
   int precheckin = 0;

   try {
      //
      //  Check if 'Pre-Checkin' feature is enabled
      //
      pstmt = con.prepareStatement (
         "SELECT precheckin FROM club5");

      pstmt.clearParameters();        // clear the parms
      rs = pstmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         precheckin = rs.getInt(1);
      }
      pstmt.close();

      if (precheckin > 0) {

         //
         //  Check each tee time for today
         //
         pstmt = con.prepareStatement (
            "SELECT time, player1, player2, player3, player4, fb, player5 FROM teecurr2 " +
            "WHERE date = ? AND (player1 != '' OR player2 != '' OR player3 != '' OR player4 != '' OR player5 != '')");

         pstmt.clearParameters();        // clear the parms
         pstmt.setLong(1, date);
         rs = pstmt.executeQuery();      // execute the prepared stmt

         while (rs.next()) {

            time = rs.getInt(1);
            player1 = rs.getString(2);
            player2 = rs.getString(3);
            player3 = rs.getString(4);
            player4 = rs.getString(5);
            fb = rs.getInt(6);
            player5 = rs.getString(7);

            show1 = 0;      // init
            show2 = 0;
            show3 = 0;
            show4 = 0;
            show5 = 0;

            //
            //  Check for member or guest
            //
            if (!player1.equals( "" ) && !player1.equalsIgnoreCase( "x" )) {

               show1 = 2;         // set pre-checkin value
            }
            if (!player2.equals( "" ) && !player2.equalsIgnoreCase( "x" )) {

               show2 = 2;         // set pre-checkin value
            }
            if (!player3.equals( "" ) && !player3.equalsIgnoreCase( "x" )) {

               show3 = 2;         // set pre-checkin value
            }
            if (!player4.equals( "" ) && !player4.equalsIgnoreCase( "x" )) {

               show4 = 2;         // set pre-checkin value
            }
            if (!player5.equals( "" ) && !player5.equalsIgnoreCase( "x" )) {

               show5 = 2;         // set pre-checkin value
            }

            if (show1 == 2 || show2 == 2 || show3 == 2 || show4 == 2 || show5 == 2) {

               //
               //  Check each tee time for today
               //
               pstmt1 = con.prepareStatement (
                 "UPDATE teecurr2 SET show1 = ?, show2 = ?, show3 = ?, show4 = ?, show5 = ? " +
                 "WHERE date = ? AND time = ? AND fb = ?");

               pstmt1.clearParameters();        // clear the parms
               pstmt1.setInt(1, show1);
               pstmt1.setInt(2, show2);
               pstmt1.setInt(3, show3);
               pstmt1.setInt(4, show4);
               pstmt1.setInt(5, show5);
               pstmt1.setLong(6, date);
               pstmt1.setInt(7, time);
               pstmt1.setInt(8, fb);

               pstmt1.executeUpdate();     // execute the prepared stmt

               pstmt1.close();
            }
         }
         pstmt.close();
      }

   }
   catch (Exception e2) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils setNoShowOW: ";
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
   }

 }  // end of setNoShowOW


 //************************************************************************
 // checkTeeTimesCCJ - Custom Processing for CC of Jackson
 //
 //       Delete any Sat or Sun tee times with less than 3 players.
 //
 //   called by:  inactTimer
 //
 //************************************************************************

 public static void checkTeeTimesCCJ(Connection con) {


   PreparedStatement pstmt = null;
   PreparedStatement pstmt2 = null;

   ResultSet rs = null;


   String day = "";
   String course = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";

   int time = 0;
   int fb = 0;
   int id = 0;
   int count = 0;

   long date = 0;


   try {
      //
      //  Get today's date
      //
      Calendar cal = new GregorianCalendar();            // get todays date (Friday)

      int yy = cal.get(Calendar.YEAR);
      int mm = cal.get(Calendar.MONTH) +1;
      int dd = cal.get(Calendar.DAY_OF_MONTH);

      long sdate = (yy * 10000) + (mm * 100) + dd;      // create a date field of yyyymmdd


      cal.add(Calendar.DATE,3);                             // get Monday's date
      yy = cal.get(Calendar.YEAR);
      mm = cal.get(Calendar.MONTH) +1;
      dd = cal.get(Calendar.DAY_OF_MONTH);

      long edate = (yy * 10000) + (mm * 100) + dd;      // create a date field of yyyymmdd

      //
      //  Get any tee times with less than 3 players (do not allow X's)
      //
      pstmt2 = con.prepareStatement (
         "SELECT teecurr_id, date, day, time, player1, player2, player3, player4, fb, player5, courseName " +
         "FROM teecurr2 WHERE date > ? AND date < ? AND player1 != ''");

      pstmt2.clearParameters();        // clear the parms
      pstmt2.setLong(1, sdate);
      pstmt2.setLong(2, edate);
      rs = pstmt2.executeQuery();      // execute the prepared stmt

      while (rs.next()) {

         id = rs.getInt( "teecurr_id" );
         date = rs.getLong( "date" );
         day = rs.getString( "day" );
         time = rs.getInt( "time" );
         player1 = rs.getString( "player1" );
         player2 = rs.getString( "player2" );
         player3 = rs.getString( "player3" );
         player4 = rs.getString( "player4" );
         fb = rs.getInt( "fb" );
         player5 = rs.getString( "player5" );
         course = rs.getString( "courseName" );

         count = 0;

         if (!player1.equals( "" ) && !player1.equalsIgnoreCase( "x" )) {    // if a player

            count++;
         }
         if (!player2.equals( "" ) && !player2.equalsIgnoreCase( "x" )) {    // if a player

            count++;
         }
         if (!player3.equals( "" ) && !player3.equalsIgnoreCase( "x" )) {    // if a player

            count++;
         }
         if (!player4.equals( "" ) && !player4.equalsIgnoreCase( "x" )) {    // if a player

            count++;
         }
         if (!player5.equals( "" ) && !player5.equalsIgnoreCase( "x" )) {    // if a !player

            count++;
         }

         if (count < 3) {            // if less than 3 players - delete it

            //
            //  Remove the tee time
            //
            pstmt = con.prepareStatement (
               "UPDATE teecurr2 SET player1 = '', player2 = '', player3 = '', player4 = '', " +
               "username1 = '', username2 = '', username3 = '', username4 = '', " +
               "in_use = 0, show1 = 0, show2 = 0, show3 = 0, show4 = 0, " +
               "player5 = '', username5 = '', show5 = 0, " +
               "notes = '', " +
               "mNum1 = '', mNum2 = '', mNum3 = '', mNum4 = '', mNum5 = '', " +
               "userg1 = '', userg2 = '', userg3 = '', userg4 = '', userg5 = '', orig_by = '', conf = '', " +
               "pos1 = 0, pos2 = 0, pos3 = 0, pos4 = 0, pos5 = 0, last_mod_date = now() " +
               "WHERE teecurr_id = ?");

            pstmt.clearParameters();        // clear the parms
            pstmt.setInt(1, id);

            pstmt.executeUpdate();     // execute the prepared stmt

            pstmt.close();

            //
            //  Track the history of this tee time - make entry in 'teehist' table
            //
            String empty = "";

            updateHist(date, day, time, fb, course, empty, empty, empty,
                                   empty, empty, "Custom", "Custom", 1, con);

         }
      }

      pstmt2.close();

   }
   catch (Exception e2) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils checkTeeTimesCCJ: ";
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
   }

 }  // end of checkTeeTimesCCJ


 //************************************************************************
 // releaseRestNR - Custom Processing for North Ridge CC
 //
 //      Change the start date of all member restrictions whose names start
 //      with 'WALK IN TIME' so they are not enforced for the current day.
 //
 //   called by:  inactTimer
 //
 //************************************************************************

 public static void releaseRestNR(Connection con) {


   PreparedStatement pstmt = null;


   String name = "WALK IN TIME%";            // beginning of name of restrictions
   String name2 = "Practice Hole Policy";

   try {
      //
      //  Get today's date
      //
      Calendar cal = new GregorianCalendar();            // get todays date

      cal.add(Calendar.DATE,1);                          // get tomorrow's date
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH);
      int day = cal.get(Calendar.DAY_OF_MONTH);

      month = month + 1;                                 // month starts at zero

      long date = (year * 10000) + (month * 100) + day;      // create a date field of yyyymmdd

      //
      //  Change the start dates
      //
      pstmt = con.prepareStatement (
        "UPDATE restriction2 SET sdate = ?, start_mm = ?, start_dd = ?, start_yy = ? " +
        "WHERE name LIKE ? OR name = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setInt(2, month);
      pstmt.setInt(3, day);
      pstmt.setInt(4, year);
      pstmt.setString(5, name);
      pstmt.setString(6, name2);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

      //
      //  Remove the restrictions from teecurr
      //
      pstmt = con.prepareStatement (
        "UPDATE teecurr2 SET restriction = '', rest_color = '' " +
        "WHERE date < ? AND restriction LIKE ? OR restriction = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setString(2, name);
      pstmt.setString(3, name2);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

      //
      //  Now, check the restriction tables and rebuild teecurr entries if needed
      //
      doRests(con);

   }
   catch (Exception e2) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils releaseRestNR: ";
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
   }

 }  // end of releaseRestNR


 //************************************************************************
 // releaseRestCommon - Custom Processing for Member Restrictions
 //
 //      Change the start date of a member restrictions whose name is
 //      passed so it is not enforced until next week.
 //
 //   called by:  inactTimer
 //
 //
 //       restName = name of the restriction to update (empty indicates ALL restrictions)
 //
 //       days     = # of days after today to change the start date to
 //
 //************************************************************************

 public static void releaseRestCommon(String restName, int days, Connection con) {


   PreparedStatement pstmt = null;


   try {

      //
      //  Get date 'days' days from now
      //
      Calendar cal = new GregorianCalendar();            // get todays date

      cal.add(Calendar.DATE,days);                          // advance the requested days
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH) + 1;
      int day = cal.get(Calendar.DAY_OF_MONTH);

      long date = (year * 10000) + (month * 100) + day;      // create a date field of yyyymmdd


      //
      //  Change the start dates
      //
      if (!restName.equals("")) {                     // if restriction specified

         pstmt = con.prepareStatement (
           "UPDATE restriction2 SET sdate = ?, start_mm = ?, start_dd = ?, start_yy = ? " +
           "WHERE name = ?");

      } else {                                        // change start date of ALL restrictions

         pstmt = con.prepareStatement (
           "UPDATE restriction2 SET sdate = ?, start_mm = ?, start_dd = ?, start_yy = ?");
      }

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setInt(2, month);
      pstmt.setInt(3, day);
      pstmt.setInt(4, year);
      if (!restName.equals("")) pstmt.setString(5, restName);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();


      //
      //  Remove the restrictions from teecurr
      //
      if (!restName.equals("")) {                     // if restriction specified

         pstmt = con.prepareStatement (
           "UPDATE teecurr2 SET restriction = '', rest_color = '' " +
           "WHERE date < ? AND restriction = ?");

      } else {                                        // remove ALL restrictions

         pstmt = con.prepareStatement (
           "UPDATE teecurr2 SET restriction = '', rest_color = '' " +
           "WHERE date < ?");
      }

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      if (!restName.equals("")) pstmt.setString(2, restName);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

      //
      //  Now, check the restriction tables and rebuild teecurr entries if needed
      //
      doRests(con);

   }
   catch (Exception e2) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils releaseRestCommon: ";
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
   }

 }  // end of releaseRestCommon


 //************************************************************************
 // releaseRestCommonMN - Custom Processing for Member Number Restrictions
 //
 //      Change the start date of a member number restrictions whose name is
 //      passed so it is not enforced until next week.
 //
 //   called by:  inactTimer
 //
 //
 //       restName = name of the restriction to update
 //
 //       days     = # of days after today to change the start date to
 //
 //************************************************************************

 public static void releaseRestCommonMN(String restName, int days, Connection con) {


   PreparedStatement pstmt = null;


   try {

      //
      //  Get date 'days' days from now
      //
      Calendar cal = new GregorianCalendar();            // get todays date

      cal.add(Calendar.DATE,days);                          // get next week
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH) + 1;
      int day = cal.get(Calendar.DAY_OF_MONTH);

      long date = (year * 10000) + (month * 100) + day;      // create a date field of yyyymmdd


      //
      //  Change the start dates
      //
      pstmt = con.prepareStatement (
        "UPDATE mnumres2 SET sdate = ?, start_mm = ?, start_dd = ?, start_yy = ? " +
        "WHERE name = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setInt(2, month);
      pstmt.setInt(3, day);
      pstmt.setInt(4, year);
      pstmt.setString(5, restName);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

   }
   catch (Exception e2) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils releaseRestCommonMN: ";
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
   }

 }  // end of releaseRestCommonMN


 //************************************************************************
 // releaseRestCommonGR - Custom Processing for GUEST Restrictions
 //
 //      Change the start date of a guest restrictions whose name is
 //      passed so it is not enforced until next week.
 //
 //   called by:  inactTimer
 //
 //
 //       name = name of the GUEST restriction to update
 //
 //       days     = # of days after today to change the start date to
 //
 //************************************************************************

 public static void releaseRestCommonGR(String name, int days, Connection con) {


   PreparedStatement pstmt = null;


   try {

      //
      //  Get date 'days' days from now
      //
      Calendar cal = new GregorianCalendar();            // get todays date

      cal.add(Calendar.DATE,days);                          // get x days from today
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH) + 1;
      int day = cal.get(Calendar.DAY_OF_MONTH);

      long date = (year * 10000) + (month * 100) + day;      // create a date field of yyyymmdd

      //
      //  Change the start dates
      //
      pstmt = con.prepareStatement (
        "UPDATE guestres2 SET sdate = ?, start_mm = ?, start_dd = ?, start_yy = ? " +
        "WHERE name = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setInt(2, month);
      pstmt.setInt(3, day);
      pstmt.setInt(4, year);
      pstmt.setString(5, name);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

   }
   catch (Exception e2) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils releaseRestCommonGR: ";
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
   }

 }  // end of releaseRestCommonGR
 
 /**
  * releaseRestCommonCustom - Change the start date for all restrictions that start with the string passed, 
  *                so that they no will no longer apply to the day that matches 'days' - 1 days in advance.
  * 
  * @param startsWith String that the names of all restrictions to be released start with.
  * @param days Number of days in advance the restriction should be released to (1 day more than the number of days in advance requested. (e.g. club asks to release restriction 3 days in advance, days should = 4)
  * @param con Connection to club database
  */
 public static void releaseRestCommonCustom(String startsWith, int days, String club, Connection con) {


   PreparedStatement pstmt = null;


   String name = startsWith + "%";            // beginning of name of restrictions

   try {
      //  Get today's date
      Calendar cal = new GregorianCalendar();            // get todays date

      cal.add(Calendar.DATE, days);                          // get the new start date
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH);
      int day = cal.get(Calendar.DAY_OF_MONTH);

      month = month + 1;                                 // month starts at zero

      long date = (year * 10000) + (month * 100) + day;      // create a date field of yyyymmdd

      //  Change the start dates
      pstmt = con.prepareStatement (
        "UPDATE restriction2 SET sdate = ?, start_mm = ?, start_dd = ?, start_yy = ? " +
        "WHERE name LIKE ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setInt(2, month);
      pstmt.setInt(3, day);
      pstmt.setInt(4, year);
      pstmt.setString(5, name);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

      //  Remove the restrictions from teecurr
      pstmt = con.prepareStatement (
        "UPDATE teecurr2 SET restriction = '', rest_color = '' " +
        "WHERE date < ? AND restriction LIKE ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setString(2, name);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

      //  Now, check the restriction tables and rebuild teecurr entries if needed
      doRests(con);

   }
   catch (Exception e2) {
       
      //  save error message to v5\errorlog database table
      Utilities.logError("SystemUtils.releaseRestCommonCustom - " + club + " - Error releasing restriction starting with '" + startsWith + "' to '" + days + "' days out - ERR: " + e2.toString());
   }

 }  // end of releaseRestCommonCustom


 //************************************************************************
 // releaseRestMAN - Custom Processing for Manito CC
 //
 //      Change the start date of all member restrictions whose names start
 //      with 'Alternate Time' so they are not enforced for the current day
 //      and the next day (selected tee times open up 1 day in advance).
 //
 //   called by:  inactTimer
 //
 //************************************************************************

 public static void releaseRestMAN(Connection con) {


   PreparedStatement pstmt = null;


   String name = "Alternate Time%";            // beginning of name of restrictions

   try {
      //
      //  Get today's date
      //
      Calendar cal = new GregorianCalendar();            // get todays date

      cal.add(Calendar.DATE,2);                          // get the day after tomorrow
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH) + 1;
      int day = cal.get(Calendar.DAY_OF_MONTH);

      long date = (year * 10000) + (month * 100) + day;      // create a date field of yyyymmdd

      //
      //  Change the start dates
      //
      pstmt = con.prepareStatement (
        "UPDATE restriction2 SET sdate = ?, start_mm = ?, start_dd = ?, start_yy = ? " +
        "WHERE name LIKE ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setInt(2, month);
      pstmt.setInt(3, day);
      pstmt.setInt(4, year);
      pstmt.setString(5, name);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

      //
      //  Remove the restrictions from teecurr
      //
      pstmt = con.prepareStatement (
        "UPDATE teecurr2 SET restriction = '', rest_color = '' " +
        "WHERE date < ? AND restriction LIKE ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setString(2, name);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

      //
      //  Now, check the restriction tables and rebuild teecurr entries if needed
      //
      doRests(con);

   }
   catch (Exception e2) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils releaseRestMAN: ";
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
   }

 }  // end of releaseRestMAN


 //************************************************************************
 // releaseRestRV - Custom Processing for Rogue Valley CC
 //
 //      Change the start date of all member restrictions whose names start
 //      with 'Phone Only' so they are not enforced for the day 2 days from today.
 //
 //   called by:  inactTimer
 //
 //************************************************************************

 public static void releaseRestRV(Connection con) {


   PreparedStatement pstmt = null;


   String name = "Phone Only%";            // beginning of name of restrictions

   try {
      //
      //  Get today's date
      //
      Calendar cal = new GregorianCalendar();            // get todays date

      cal.add(Calendar.DATE,3);                          // get the new start date
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH);
      int day = cal.get(Calendar.DAY_OF_MONTH);

      month = month + 1;                                 // month starts at zero

      long date = (year * 10000) + (month * 100) + day;      // create a date field of yyyymmdd

      //
      //  Change the start dates
      //
      pstmt = con.prepareStatement (
        "UPDATE restriction2 SET sdate = ?, start_mm = ?, start_dd = ?, start_yy = ? " +
        "WHERE name LIKE ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setInt(2, month);
      pstmt.setInt(3, day);
      pstmt.setInt(4, year);
      pstmt.setString(5, name);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

      //
      //  Remove the restrictions from teecurr
      //
      pstmt = con.prepareStatement (
        "UPDATE teecurr2 SET restriction = '', rest_color = '' " +
        "WHERE date < ? AND restriction LIKE ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setString(2, name);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

      //
      //  Now, check the restriction tables and rebuild teecurr entries if needed
      //
      doRests(con);

   }
   catch (Exception e2) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils releaseRestRV: ";
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
   }

 }  // end of releaseRestRV


 //************************************************************************
 // releaseBlockerTCC - Custom Processing for The CC
 //
 //   Remove all blockers for today (from teecurr).
 //
 //   called by:  inactTimer
 //
 //************************************************************************

 public static void releaseBlockerTCC(Connection con) {


   PreparedStatement pstmt = null;


   try {
      //
      //  Get today's date
      //
      Calendar cal = new GregorianCalendar();            // get todays date

      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH) +1;
      int day = cal.get(Calendar.DAY_OF_MONTH);

      long date = (year * 10000) + (month * 100) + day;      // create a date field of yyyymmdd

      //
      //   Remove blockers
      //
      pstmt = con.prepareStatement (
        "UPDATE teecurr2 SET blocker = '' " +
        "WHERE date = ? AND blocker <> ''");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);

      int count = pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

      logError("** TEST: Blockers removed for The CC. # of tee times updated=" +count);    // log it

   }
   catch (Exception e2) {

      logError("Error in SystemUtils releaseBlockerTCC: " + e2.getMessage());                                       // log it
   }

 }  // end of releaseBlockerTCC

 //************************************************************************
 // releaseRestOahuCC - Custom Processing for Member Restrictions
 //
 //      Change the start date of member restrictions (except ones
 //      on Saturday) so they are not enforced until next week.
 //
 //************************************************************************

 public static void releaseRestOahuCC(String restName, int days, Connection con) {


   PreparedStatement pstmt = null;

   try {

      //
      //  Get date 'days' days from now
      //
      Calendar cal = new GregorianCalendar();            // get todays date

      cal.add(Calendar.DATE,days);                          // advance the requested days
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH) + 1;
      int day = cal.get(Calendar.DAY_OF_MONTH);

      long date = (year * 10000) + (month * 100) + day;      // create a date field of yyyymmdd


      //
      //  Change the start dates
      //                                     // change start date of ALL restrictions except ones on Saturday
      pstmt = con.prepareStatement (
              "UPDATE restriction2 SET sdate = ?, start_mm = ?, start_dd = ?, start_yy = ? WHERE recurr <> 'Every Saturday' AND name <> 'Sunday Spouse restriction' AND name <> 'Spouses Juniors Front 9 AM sun'");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setInt(2, month);
      pstmt.setInt(3, day);
      pstmt.setInt(4, year);
      //if (!restName.equals("")) pstmt.setString(5, restName);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();


      //
      //  Remove the restrictions from teecurr
      //
      pstmt = con.prepareStatement (
              "UPDATE teecurr2 SET restriction = '', rest_color = '' " +
              "WHERE date < ? AND day <> 'Saturday' AND restriction <> 'Sunday Spouse restriction' AND restriction <> 'Spouses Juniors Front 9 AM sun'");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      //if (!restName.equals("")) pstmt.setString(2, restName);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

      //
      //  Now, check the restriction tables and rebuild teecurr entries if needed
      //
      doRests(con);

   }
   catch (Exception e2) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils releaseRestOahuCC: ";
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
   }

 }  // end of releaseRestOahuCC


 //************************************************************************
 // releaseRestBirminghamCC - Custom Processing for Member Number Restrictions
 //
 //      If the Saturday and Sunday lotteries have already been processed and there
 //      are still open time slots during that time, release the appropriate member number restrictions.
 //
 //************************************************************************

 public static void releaseRestBirminghamCC(Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   Calendar cal = new GregorianCalendar();            // get todays date

   cal.add(Calendar.DATE, 2);                          // advance to Sat (this method will only run on Thurs, so 2 days)
   int year = cal.get(Calendar.YEAR);
   int month = cal.get(Calendar.MONTH) + 1;
   int day = cal.get(Calendar.DAY_OF_MONTH);

   long date = (year * 10000) + (month * 100) + day;      // create a date field of yyyymmdd

   int lstate = 0;

   // Check if Saturday lottery has already been approved
   lstate = getLotteryState(date, month, day, year, "Saturday Lottery", "", con);

   if (lstate == 5) {

       // Check if there are still any open times
       try {

           pstmt = con.prepareStatement("SELECT * FROM teecurr2 WHERE date = ? AND time >= 700 AND time <= 1028 AND player1='' AND player2='' AND player3='' AND player4='' AND player5='' LIMIT 1");
           pstmt.clearParameters();
           pstmt.setLong(1, date);

           rs = pstmt.executeQuery();

           if (rs.next()) {

               releaseRestCommonMN("Saturday Member Policy", 3, con);   // Move restriction forward so it no longer applies to this week
           }

       } catch (Exception exc) {

          //  save error message in /v_x/error.txt
          String errorMsg = "Error in SystemUtils releaseRestBirminghamCC: ";
          errorMsg = errorMsg + exc.getMessage();                                 // build error msg

          logError(errorMsg);                                       // log it

       } finally {

           try { rs.close(); }
           catch (Exception ignore) { }

           try { pstmt.close(); }
           catch (Exception ignore) { }
       }
   }

   lstate = 0;

   cal.add(Calendar.DATE, 1);                          // advance to Sat (calendar is set to Sat, so 1 day)
   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH) + 1;
   day = cal.get(Calendar.DAY_OF_MONTH);

   date = (year * 10000) + (month * 100) + day;      // create a date field of yyyymmdd

   // Check if Sunday lottery has already been approved
   lstate = getLotteryState(date, month, day, year, "Sunday Lottery", "", con);

   if (lstate == 5) {

       // Check if there are still any open times
       try {

           pstmt = con.prepareStatement("SELECT * FROM teecurr2 WHERE date = ? AND time >= 700 AND time <= 928 AND player1='' AND player2='' AND player3='' AND player4='' AND player5='' LIMIT 1");
           pstmt.clearParameters();
           pstmt.setLong(1, date);

           rs = pstmt.executeQuery();

           if (rs.next()) {

               releaseRestCommonMN("Sunday Member Policy", 4, con);   // Move restriction forward so it no longer applies to this week
           }

       } catch (Exception exc) {

          //  save error message in /v_x/error.txt
          String errorMsg = "Error in SystemUtils releaseRestBirminghamCC: ";
          errorMsg = errorMsg + exc.getMessage();                                 // build error msg

          logError(errorMsg);                                       // log it

       } finally {

           try { rs.close(); }
           catch (Exception ignore) { }

           try { pstmt.close(); }
           catch (Exception ignore) { }
       }
   }

 }  // end of releaseRestCommonMN




 //************************************************************************
 // buildOlyPOS - builds POS files for each of the Olympic Club's 3 courses.
 //
 //   The files are saved on our server and they pull them nightly via FTP.
 //
 //   called by:  2 minute timer above (at 11:00 and 11:30 PM CT)
 //
 //************************************************************************

 private static void buildOlyPOS(String club, Connection con) {


   HttpServletResponse resp = null;    // required for Proshop_sheet_pos but not needed for this
   PrintWriter out = null;

   //
   //  Get today's date
   //
   Calendar cal = new GregorianCalendar();       // get todays date
   int year = cal.get(Calendar.YEAR);
   int month = cal.get(Calendar.MONTH) +1;
   int daynum = cal.get(Calendar.DAY_OF_MONTH);

   long date = (year * 10000) + (month * 100) + daynum;          // create a date field of yyyymmdd

   if (club.equals("olyclub")) {     // Olympic Club

      //
      //  Build charges for each of the 3 courses at the Olympic Club
      //
      Proshop_sheet_pos.sendCharges("timer", club, "Ocean", "autotimer", date, resp, out, con);      // build charges for each course

      Proshop_sheet_pos.sendCharges("timer", club, "Lake", "autotimer", date, resp, out, con);

      Proshop_sheet_pos.sendCharges("timer", club, "Cliffs", "autotimer", date, resp, out, con);
      

   } else if (club.equals( "philcricket" )) {    // Philly Cricket

      //
      //  Build charges for each of the 3 courses at Philly Cricket
      //
      Proshop_sheet_pos.sendCharges("timer", club, "Wissahickon", "autotimer", date, resp, out, con);      // build charges for each course

      Proshop_sheet_pos.sendCharges("timer", club, "Militia Hill", "autotimer", date, resp, out, con);

      Proshop_sheet_pos.sendCharges("timer", club, "St Martins", "autotimer", date, resp, out, con);
      

   } else if (club.equals( "ironwood" )) {    // Ironwood CC

      //
      //  Build charges for each of the 2 courses at Ironwood CC
      //
      Proshop_sheet_pos.sendCharges("timer", club, "North", "autotimer", date, resp, out, con);      // build charges for each course

      Proshop_sheet_pos.sendCharges("timer", club, "South", "autotimer", date, resp, out, con);

   } else if (club.equals("atlantacountryclub")) {
       
      Proshop_sheet_pos.sendCharges("timer", club, "", "autotimer", date, resp, out, con);
   }

 }  // end of buildOlyPOS







 //************************************************************************
 // rebuildTFlags - rebuilt tflags in teecurr
 //
 //   Remove all tflags in teecurr for this club, then put them back in with new settings.
 //
 //   called by:  scanTee after tee times moved to old sheets
 //
 //************************************************************************

 public static void rebuildTFlags(String club, Connection con) {


   Statement stmt = null;

   try {

      //
      //  First make sure there are no member records with an empty username field (not allowed) - causes problems with the following statements
      //
      stmt = con.createStatement();

      stmt.executeUpdate("DELETE FROM member2b WHERE username = ''");          // delete all records with an empty username

   } catch (Exception e1) {

      logError("Error in SystemUtils rebuildTFlags (username): club=" + club + ", err=" + e1.getMessage());

   } finally {

       try { stmt.close(); }
       catch (Exception ignore) {}

   }

   //
   //  Now use the tflags from member2b and mship5 to create the necessary tflags in teecurr
   //
   try {

       stmt = con.createStatement();
       stmt.executeUpdate(
            "UPDATE teecurr2 t " +
            "SET " +
                "tflag1 = TRIM(CONCAT((SELECT tflag FROM member2b WHERE username = t.username1), ' ', (SELECT tflag FROM mship5 WHERE activity_id = 0 AND mship = (SELECT m_ship FROM member2b WHERE username = t.username1)))), " +
                "tflag2 = TRIM(CONCAT((SELECT tflag FROM member2b WHERE username = t.username2), ' ', (SELECT tflag FROM mship5 WHERE activity_id = 0 AND mship = (SELECT m_ship FROM member2b WHERE username = t.username2)))), " +
                "tflag3 = TRIM(CONCAT((SELECT tflag FROM member2b WHERE username = t.username3), ' ', (SELECT tflag FROM mship5 WHERE activity_id = 0 AND mship = (SELECT m_ship FROM member2b WHERE username = t.username3)))), " +
                "tflag4 = TRIM(CONCAT((SELECT tflag FROM member2b WHERE username = t.username4), ' ', (SELECT tflag FROM mship5 WHERE activity_id = 0 AND mship = (SELECT m_ship FROM member2b WHERE username = t.username4)))), " +
                "tflag5 = TRIM(CONCAT((SELECT tflag FROM member2b WHERE username = t.username5), ' ', (SELECT tflag FROM mship5 WHERE activity_id = 0 AND mship = (SELECT m_ship FROM member2b WHERE username = t.username5))))");

   } catch (Exception e2) {

      logError("Error in SystemUtils rebuildTFlags: club=" + club + ", err=" + e2.getMessage());

   } finally {

       try { stmt.close(); }
       catch (Exception ignore) {}

   }

 }  // end of rebuildTFlags


 //************************************************************************
 // getDate
 //
 //    Common method to get the current date (adjusted for time zone)
 //************************************************************************

 public static long getDate(Connection con) {

   return Utilities.getDate(con);

   /*
   long date = 0;

   //
   //   Get current date and time
   //
   Calendar cal = new GregorianCalendar();        // get todays date
   int year = cal.get(Calendar.YEAR);
   int month = cal.get(Calendar.MONTH);
   int day = cal.get(Calendar.DAY_OF_MONTH);
   int cal_hourDay = cal.get(Calendar.HOUR_OF_DAY);
   int cal_min = cal.get(Calendar.MINUTE);

   //
   //  Build the 'time' string for display
   //
   //    Adjust the time based on the club's time zone (we are Central)
   //
   int time = (cal_hourDay * 100) + cal_min;

   time = adjustTime(con, time);       // adjust for time zone

   if (time < 0) {                // if negative, then we went back or ahead one day

      time = 0 - time;          // convert back to positive value

      if (time < 100) {           // if hour is zero, then we rolled ahead 1 day

         //
         // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
         //
         cal.add(Calendar.DATE,1);                     // get next day's date

         year = cal.get(Calendar.YEAR);
         month = cal.get(Calendar.MONTH);
         day = cal.get(Calendar.DAY_OF_MONTH);

      } else {                        // we rolled back 1 day

         //
         // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
         //
         cal.add(Calendar.DATE,-1);                     // get yesterday's date

         year = cal.get(Calendar.YEAR);
         month = cal.get(Calendar.MONTH);
         day = cal.get(Calendar.DAY_OF_MONTH);
      }
   }

   month++;                            // month starts at zero
   date = (year * 10000) + (month * 100) + day;

   return(date);
   */
 }  // end of getDate


 //************************************************************************
 // getTime
 //
 //    Common method to get the current time (adjusted for time zone)
 //************************************************************************

 public static int getTime(Connection con) {

   return Utilities.getTime(con);

   /*
   int time = 0;

   //
   //   Get current date and time
   //
   Calendar cal = new GregorianCalendar();        // get todays date
   int cal_hourDay = cal.get(Calendar.HOUR_OF_DAY);
   int cal_min = cal.get(Calendar.MINUTE);

   //
   //  Build the 'time' string for display
   //
   //    Adjust the time based on the club's time zone (we are Central)
   //
   time = (cal_hourDay * 100) + cal_min;

   time = adjustTime(con, time);       // adjust for time zone

   if (time < 0) {                // if negative, then we went back or ahead one day

      time = 0 - time;          // convert back to positive value (getDate above should have adjusted the date)
   }

   return(time);
   */
 }  // end of getTime


 // *********************************************************
 //  Display Notes in new pop-up window
 //
 //
 //   called by:  Proshop_sheet
 //               Proshop_searchmem
 //
 // *********************************************************

 public static void displayNotes(String stime, String sfb, long date, String courseName1, PrintWriter out, Connection con) {


   PreparedStatement pstmtc = null;
   ResultSet rs = null;

   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
   String notes = "";
   String orig_by = "";
   String orig_name = "";
   String assoc_name = "";
   String conf = "";
   String day_name = "";

   int month = 0;
   int day = 0;
   int year = 0;

   boolean unaccomp = false;

   //
   //  Convert the common string values to int's
   //
   int time = Integer.parseInt(stime);
   short fb = Short.parseShort(sfb);

   try {

      PreparedStatement pstmt2s = con.prepareStatement (
         "SELECT mm, dd, yy, day, player1, player2, player3, player4, player5, notes, " +
         "userg1, userg2, userg3, userg4, userg5, orig_by, conf " +
         "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? and courseName = ?");

      pstmt2s.clearParameters();        // clear the parms
      pstmt2s.setLong(1, date);
      pstmt2s.setInt(2, time);
      pstmt2s.setShort(3, fb);
      pstmt2s.setString(4, courseName1);

      rs = pstmt2s.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         month = rs.getInt(1);
         day = rs.getInt(2);
         year = rs.getInt(3);
         day_name = rs.getString(4);
         player1 = rs.getString(5);
         player2 = rs.getString(6);
         player3 = rs.getString(7);
         player4 = rs.getString(8);
         player5 = rs.getString(9);
         notes = rs.getString(10);
         userg1 = rs.getString(11);
         userg2 = rs.getString(12);
         userg3 = rs.getString(13);
         userg4 = rs.getString(14);
         userg5 = rs.getString(15);
         orig_by = rs.getString(16);
         conf = rs.getString(17);
      }

      pstmt2s.close();

      if (!orig_by.equals( "" )) {         // if originator exists (username of person originating tee time)

         if (orig_by.startsWith( "proshop" )) {  // if originator exists (username of person originating tee time)

            orig_name = orig_by;         // use proshop username

         } else {

            //
            //  Check member table and hotel table for match
            //
            orig_name = "";           // init

            pstmtc = con.prepareStatement (
               "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

            pstmtc.clearParameters();        // clear the parms
            pstmtc.setString(1, orig_by);

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

               orig_name = mem_name.toString();                          // convert to one string
            }
            pstmtc.close();

            if (orig_name.equals( "" )) {       // if match not found - check hotel user table

               pstmtc = con.prepareStatement (
                  "SELECT name_last, name_first, name_mi FROM hotel3 WHERE username= ?");

               pstmtc.clearParameters();        // clear the parms
               pstmtc.setString(1, orig_by);

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

                  orig_name = mem_name.toString();                          // convert to one string
               }
               pstmtc.close();
            }
         }
      }

      //
      //  Check for Unaccompanied Guests
      //
      unaccomp = false;

      if (!userg1.equals( "" ) || !userg2.equals( "" ) || !userg3.equals( "" ) || !userg4.equals( "" ) || !userg5.equals( "" )) {    // if any guests

         unaccomp = true;     // default = unaccompanied guests

         if (!player1.equals("") && !player1.equalsIgnoreCase( "x" ) && userg1.equals( "" )) { // if player and NOT a guest
            unaccomp = false;
         }
         if (!player2.equals("") && !player2.equalsIgnoreCase( "x" ) && userg2.equals( "" )) { // if player and NOT a guest
            unaccomp = false;
         }
         if (!player3.equals("") && !player3.equalsIgnoreCase( "x" ) && userg3.equals( "" )) { // if player and NOT a guest
            unaccomp = false;
         }
         if (!player4.equals("") && !player4.equalsIgnoreCase( "x" ) && userg4.equals( "" )) { // if player and NOT a guest
            unaccomp = false;
         }
         if (!player5.equals("") && !player5.equalsIgnoreCase( "x" ) && userg5.equals( "" )) { // if player and NOT a guest
            unaccomp = false;
         }
      }

      if (unaccomp == true) {

         if (!player1.equals("") && !userg1.equals( "" )) {   // if player is a guest

            assoc_name = "";           // init

            pstmtc = con.prepareStatement (
               "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

            pstmtc.clearParameters();        // clear the parms
            pstmtc.setString(1, userg1);

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

               assoc_name = mem_name.toString();                          // convert to one string
            }
            pstmtc.close();

            player1 = player1 + " is associated with member " + assoc_name;  // create string
         } else {
            player1 = "";
         }
         if (!player2.equals("") && !userg2.equals( "" )) {   // if player is a guest

            assoc_name = "";           // init

            pstmtc = con.prepareStatement (
               "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

            pstmtc.clearParameters();        // clear the parms
            pstmtc.setString(1, userg2);

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

               assoc_name = mem_name.toString();                          // convert to one string
            }
            pstmtc.close();

            player2 = player2 + " is associated with member " + assoc_name;  // create string
         } else {
            player2 = "";
         }
         if (!player3.equals("") && !userg3.equals( "" )) {   // if player is a guest

            assoc_name = "";           // init

            pstmtc = con.prepareStatement (
               "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

            pstmtc.clearParameters();        // clear the parms
            pstmtc.setString(1, userg3);

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

               assoc_name = mem_name.toString();                          // convert to one string
            }
            pstmtc.close();

            player3 = player3 + " is associated with member " + assoc_name;  // create string
         } else {
            player3 = "";
         }
         if (!player4.equals("") && !userg4.equals( "" )) {   // if player is a guest

            assoc_name = "";           // init

            pstmtc = con.prepareStatement (
               "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

            pstmtc.clearParameters();        // clear the parms
            pstmtc.setString(1, userg4);

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

               assoc_name = mem_name.toString();                          // convert to one string
            }
            pstmtc.close();

            player4 = player4 + " is associated with member " + assoc_name;  // create string
         } else {
            player4 = "";
         }
         if (!player5.equals("") && !userg5.equals( "" )) {   // if player is a guest

            assoc_name = "";           // init

            pstmtc = con.prepareStatement (
               "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

            pstmtc.clearParameters();        // clear the parms
            pstmtc.setString(1, userg5);

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

               assoc_name = mem_name.toString();                          // convert to one string
            }
            pstmtc.close();

            player5 = player5 + " is associated with member " + assoc_name;  // create string
         } else {
            player5 = "";
         }
      }
   }
   catch (Exception ignore) {
      return;
   }

   out.println(SystemUtils.HeadTitle("Show Notes"));
   out.println("<BODY><CENTER><BR>");
   out.println("<font size=\"3\"><b>Tee Time Notes</b></font><br><BR>");
   out.println("<font size=\"2\"><b>For " + day_name + " " +month+ "/" +day+ "/" +year+ " on the ");
   if (fb == 0) {
      out.println("Front Tee");
   } else {
      out.println("Back Tee");
   }
   if (!courseName1.equals( "" )) {
      out.println(" of the " + courseName1);
   }
   out.println("</b>");
   if (!orig_name.equals( "" )) {
      out.println("<br><br>");
      out.println("Tee Time Originated by: &nbsp;&nbsp;<b>" + orig_name + "</b>");
   }
   if (!conf.equals( "" )) {
      out.println("<br><br>");
      out.println("Tee Time Confirmation # or Id: &nbsp;&nbsp;<b>" + conf + "</b>");
   }

   if (unaccomp == true) {

      out.println("<br><br>Unaccompanied Guests");
      if (!player1.equals( "" )) {
         out.println("<br>");
         out.println("<b>Player 1&nbsp;&nbsp;</b>" + player1);
      }
      if (!player2.equals( "" )) {
         out.println("<br>");
         out.println("<b>Player 2&nbsp;&nbsp;</b>" + player2);
      }
      if (!player3.equals( "" )) {
         out.println("<br>");
         out.println("<b>Player 3&nbsp;&nbsp;</b>" + player3);
      }
      if (!player4.equals( "" )) {
         out.println("<br>");
         out.println("<b>Player 4&nbsp;&nbsp;</b>" + player4);
      }
      if (!player5.equals( "" )) {
         out.println("<br>");
         out.println("<b>Player 5&nbsp;&nbsp;</b>" + player5);
      }
   }
   if (!notes.equals( "" )) {
      out.println("<br><br>");
      out.println("<b>Notes:</b> &nbsp;&nbsp;" + notes );
   }
   out.println("<BR>");
   out.println("<form>");
   out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970; width:80px\" value=\"Close\" onClick='self.close()' alt=\"Close\">");
   out.println("</form>");
   out.println("</font></CENTER></BODY></HTML>");
   out.close();

 }                   // end of displayNotes


 // *********************************************************
 //  Display Notes in new pop-up window
 //
 //
 //   called by:  Proshop_sheet
 //               Proshop_searchmem
 //
 // *********************************************************

 public static void displayOldNotes(String stime, String sfb, long date, String courseName1, PrintWriter out, Connection con) {


   PreparedStatement pstmtc = null;
   ResultSet rs = null;

   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
   String notes = "";
   String orig_by = "";
   String orig_name = "";
   String conf = "";
   String day_name = "";

   int month = 0;
   int day = 0;
   int year = 0;

   boolean unaccomp = false;

   //
   //  Convert the common string values to int's
   //
   int time = Integer.parseInt(stime);
   short fb = Short.parseShort(sfb);

   try {

      PreparedStatement pstmt2s = con.prepareStatement (
         "SELECT mm, dd, yy, day, player1, player2, player3, player4, player5, notes, " +
         "userg1, userg2, userg3, userg4, userg5, orig_by, conf " +
         "FROM teepast2 WHERE date = ? AND time = ? AND fb = ? and courseName = ?");

      pstmt2s.clearParameters();        // clear the parms
      pstmt2s.setLong(1, date);
      pstmt2s.setInt(2, time);
      pstmt2s.setShort(3, fb);
      pstmt2s.setString(4, courseName1);

      rs = pstmt2s.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         month = rs.getInt(1);
         day = rs.getInt(2);
         year = rs.getInt(3);
         day_name = rs.getString(4);
         player1 = rs.getString(5);
         player2 = rs.getString(6);
         player3 = rs.getString(7);
         player4 = rs.getString(8);
         player5 = rs.getString(9);
         notes = rs.getString(10);
         userg1 = rs.getString(11);
         userg2 = rs.getString(12);
         userg3 = rs.getString(13);
         userg4 = rs.getString(14);
         userg5 = rs.getString(15);
         orig_by = rs.getString(16);
         conf = rs.getString(17);
      }

      pstmt2s.close();

      if (!orig_by.equals( "" )) {         // if originator exists (username of person originating tee time)

         if (orig_by.startsWith( "proshop" )) {  // if originator exists (username of person originating tee time)

            orig_name = orig_by;         // use proshop username

         } else {

            //
            //  Check member table and hotel table for match
            //
            orig_name = "";           // init

            pstmtc = con.prepareStatement (
               "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

            pstmtc.clearParameters();        // clear the parms
            pstmtc.setString(1, orig_by);

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

               orig_name = mem_name.toString();                          // convert to one string
            }
            pstmtc.close();

            if (orig_name.equals( "" )) {       // if match not found - check hotel user table

               pstmtc = con.prepareStatement (
                  "SELECT name_last, name_first, name_mi FROM hotel3 WHERE username= ?");

               pstmtc.clearParameters();        // clear the parms
               pstmtc.setString(1, orig_by);

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

                  orig_name = mem_name.toString();                          // convert to one string
               }
               pstmtc.close();
            }
         }
      }

      //
      //  Check for Unaccompanied Guests
      //
      unaccomp = false;

      if (!userg1.equals( "" ) || !userg2.equals( "" ) || !userg3.equals( "" ) || !userg4.equals( "" ) || !userg5.equals( "" )) {    // if any guests

         unaccomp = true;     // default = unaccompanied guests

         if (!player1.equals("") && !player1.equalsIgnoreCase( "x" ) && userg1.equals( "" )) { // if player and NOT a guest
            unaccomp = false;
         }
         if (!player2.equals("") && !player2.equalsIgnoreCase( "x" ) && userg2.equals( "" )) { // if player and NOT a guest
            unaccomp = false;
         }
         if (!player3.equals("") && !player3.equalsIgnoreCase( "x" ) && userg3.equals( "" )) { // if player and NOT a guest
            unaccomp = false;
         }
         if (!player4.equals("") && !player4.equalsIgnoreCase( "x" ) && userg4.equals( "" )) { // if player and NOT a guest
            unaccomp = false;
         }
         if (!player5.equals("") && !player5.equalsIgnoreCase( "x" ) && userg5.equals( "" )) { // if player and NOT a guest
            unaccomp = false;
         }
      }

      if (unaccomp == true) {

         if (!player1.equals("") && !userg1.equals( "" )) {   // if player is a guest

            orig_name = "";           // init

            pstmtc = con.prepareStatement (
               "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

            pstmtc.clearParameters();        // clear the parms
            pstmtc.setString(1, userg1);

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

               orig_name = mem_name.toString();                          // convert to one string
            }
            pstmtc.close();

            player1 = player1 + " is associated with member " + orig_name;  // create string
         } else {
            player1 = "";
         }
         if (!player2.equals("") && !userg2.equals( "" )) {   // if player is a guest

            orig_name = "";           // init

            pstmtc = con.prepareStatement (
               "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

            pstmtc.clearParameters();        // clear the parms
            pstmtc.setString(1, userg2);

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

               orig_name = mem_name.toString();                          // convert to one string
            }
            pstmtc.close();

            player2 = player2 + " is associated with member " + orig_name;  // create string
         } else {
            player2 = "";
         }
         if (!player3.equals("") && !userg3.equals( "" )) {   // if player is a guest

            orig_name = "";           // init

            pstmtc = con.prepareStatement (
               "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

            pstmtc.clearParameters();        // clear the parms
            pstmtc.setString(1, userg3);

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

               orig_name = mem_name.toString();                          // convert to one string
            }
            pstmtc.close();

            player3 = player3 + " is associated with member " + orig_name;  // create string
         } else {
            player3 = "";
         }
         if (!player4.equals("") && !userg4.equals( "" )) {   // if player is a guest

            orig_name = "";           // init

            pstmtc = con.prepareStatement (
               "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

            pstmtc.clearParameters();        // clear the parms
            pstmtc.setString(1, userg4);

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

               orig_name = mem_name.toString();                          // convert to one string
            }
            pstmtc.close();

            player4 = player4 + " is associated with member " + orig_name;  // create string
         } else {
            player4 = "";
         }
         if (!player5.equals("") && !userg5.equals( "" )) {   // if player is a guest

            orig_name = "";           // init

            pstmtc = con.prepareStatement (
               "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

            pstmtc.clearParameters();        // clear the parms
            pstmtc.setString(1, userg5);

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

               orig_name = mem_name.toString();                          // convert to one string
            }
            pstmtc.close();

            player5 = player5 + " is associated with member " + orig_name;  // create string
         } else {
            player5 = "";
         }
      }
   }
   catch (Exception ignore) {
      return;
   }

   out.println(SystemUtils.HeadTitle("Show Notes"));
   out.println("<BODY><CENTER><BR>");
   out.println("<font size=\"3\"><b>Tee Time Notes</b></font><br><BR>");
   out.println("<font size=\"2\"><b>For " + day_name + " " +month+ "/" +day+ "/" +year+ " on the ");
   if (fb == 0) {
      out.println("Front Tee");
   } else {
      out.println("Back Tee");
   }
   if (!courseName1.equals( "" )) {
      out.println(" of the " + courseName1);
   }
   out.println("</b>");
   if (!orig_name.equals( "" )) {
      out.println("<br><br>");
      out.println("Tee Time Originated by: &nbsp;&nbsp;<b>" + orig_name + "</b>");
   }
   if (!conf.equals( "" )) {
      out.println("<br><br>");
      out.println("Tee Time Confirmation # or Id: &nbsp;&nbsp;<b>" + conf + "</b>");
   }

   if (unaccomp == true) {

      out.println("<br><br>Unaccompanied Guests");
      if (!player1.equals( "" )) {
         out.println("<br>");
         out.println("<b>Player 1&nbsp;&nbsp;</b>" + player1);
      }
      if (!player2.equals( "" )) {
         out.println("<br>");
         out.println("<b>Player 2&nbsp;&nbsp;</b>" + player2);
      }
      if (!player3.equals( "" )) {
         out.println("<br>");
         out.println("<b>Player 3&nbsp;&nbsp;</b>" + player3);
      }
      if (!player4.equals( "" )) {
         out.println("<br>");
         out.println("<b>Player 4&nbsp;&nbsp;</b>" + player4);
      }
      if (!player5.equals( "" )) {
         out.println("<br>");
         out.println("<b>Player 5&nbsp;&nbsp;</b>" + player5);
      }
   }
   if (!notes.equals( "" )) {
      out.println("<br><br>");
      out.println("<b>Notes:</b> &nbsp;&nbsp;" + notes );
   }
   out.println("<BR>");
   out.println("<form>");
   out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" Value=\"  Close  \" onClick='self.close()' alt=\"Close\">");
   out.println("</form>");
   out.println("</font></CENTER></BODY></HTML>");
   out.close();

 }                   // end of displayOldNotes


 //************************************************************************
 //  doTableUpdate_tmodes - creates the tmodes db table if it does not already exist
 //                       - also populates it from tmode info in clubparm2 table.
 //
 //   called by:  Proshop_reports_course_rounds
 //               Proshop_parms
 //
 //************************************************************************

 public static void doTableUpdate_tmodes(Connection con) {


     Statement stmt = null;

//   String tmp_error = "";

     try {

        stmt = con.createStatement();        // create a statement
/*
        tmp_error = "SystemUtils.doTableUpdate_tmodes: Create tmodes ";

        // if needed create tmodes table
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS tmodes(" +
            "courseName CHAR(30) NOT NULL, " +
            "tmodea CHAR(3) NOT NULL, " +
            "tmode CHAR(20) NOT NULL, " +
            "PRIMARY KEY (courseName, tmodea));");
 *
        tmp_error = "SystemUtils.doTableUpdate_tmodes: Update tmodes ";
*/

        // populate table
        stmt.executeUpdate("REPLACE INTO tmodes " +
            "SELECT courseName, tmodea, tmode FROM (" +
            "SELECT tmode1 AS tmode, tmodea1 AS tmodea, courseName FROM clubparm2 " +
            "UNION ALL " +
            "SELECT tmode2 AS tmode, tmodea2 AS tmodea, courseName FROM clubparm2 " +
            "UNION ALL " +
            "SELECT tmode3 AS tmode, tmodea3 AS tmodea, courseName FROM clubparm2 " +
            "UNION ALL " +
            "SELECT tmode4 AS tmode, tmodea4 AS tmodea, courseName FROM clubparm2 " +
            "UNION ALL " +
            "SELECT tmode5 AS tmode, tmodea5 AS tmodea, courseName FROM clubparm2 " +
            "UNION ALL " +
            "SELECT tmode6 AS tmode, tmodea6 AS tmodea, courseName FROM clubparm2 " +
            "UNION ALL " +
            "SELECT tmode7 AS tmode, tmodea7 AS tmodea, courseName FROM clubparm2 " +
            "UNION ALL " +
            "SELECT tmode8 AS tmode, tmodea8 AS tmodea, courseName FROM clubparm2 " +
            "UNION ALL " +
            "SELECT tmode9 AS tmode, tmodea9 AS tmodea, courseName FROM clubparm2 " +
            "UNION ALL " +
            "SELECT tmode10 AS tmode, tmodea10 AS tmodea, courseName FROM clubparm2 " +
            "UNION ALL " +
            "SELECT tmode12 AS tmode, tmodea12 AS tmodea, courseName FROM clubparm2 " +
            "UNION ALL " +
            "SELECT tmode13 AS tmode, tmodea13 AS tmodea, courseName FROM clubparm2 " +
            "UNION ALL " +
            "SELECT tmode14 AS tmode, tmodea14 AS tmodea, courseName FROM clubparm2 " +
            "UNION ALL " +
            "SELECT tmode15 AS tmode, tmodea15 AS tmodea, courseName FROM clubparm2 " +
            "UNION ALL " +
            "SELECT tmode16 AS tmode, tmodea16 AS tmodea, courseName FROM clubparm2 " +
            ") AS tmodes WHERE courseName IS NOT NULL AND tmodea IS NOT NULL AND tmode IS NOT NULL AND tmodea <> '';");

     } catch (Exception exc) {

         logError("SystemUtils.doTableUpdate_tmodes(): err=" + exc.getMessage());

     } finally {

         try { stmt.close(); }
         catch (Exception ignore) {}

     }

  }


 //************************************************************************
 //
 // Add an entry to the activity_sheet_history table
 //
 //************************************************************************
 public static void updateActHist(int sheet_id, String user, String players, Connection con) {

    PreparedStatement pstmt = null;
    
    Calendar cal = new GregorianCalendar();
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH);
    int day_wk = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)
    int daynum = cal.get(Calendar.DAY_OF_MONTH);
    int cal_hourDay = cal.get(Calendar.HOUR_OF_DAY);    // 24 hr clock (0 - 23)
    int cal_min = cal.get(Calendar.MINUTE);
    int cal_sec = cal.get(Calendar.SECOND);
    int cal_mil = cal.get(Calendar.MILLISECOND);
    //
    //    Adjust the time based on the club's time zone (we are Central)
    //
    int cal_time = (cal_hourDay * 100) + cal_min;     // get time in hhmm format
    
    cal_time = adjustTime(con, cal_time);             // adjust the time to club's time zone
    
    if (cal_time < 0) {          // if negative, then we went back or ahead one day
        
        cal_time = 0 - cal_time;        // convert back to positive value
        
        if (cal_time < 1200) {           // if AM, then we rolled ahead 1 day (allow for Saudi time)
            
            //
            // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
            //
            cal.add(Calendar.DATE,1);                     // get next day's date
            
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            daynum = cal.get(Calendar.DAY_OF_MONTH);
            day_wk = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)
            
        } else {                        // we rolled back 1 day
            
            //
            // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
            //
            cal.add(Calendar.DATE,-1);                     // get yesterday's date
            
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            daynum = cal.get(Calendar.DAY_OF_MONTH);
            day_wk = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)
        }
    }
    
    month++;                                      // month starts at zero
    
    int cal_hour = cal_time / 100;                // get adjusted hour
    cal_min = cal_time - (cal_hour * 100);        // get minute value
    
    long mdate = (year * 10000) + (month * 100) + daynum;         // date value for today (for sorting the records)
    
    String mdateS = year + "-" + month + "-" + daynum + " " + cal_hour + ":" + cal_min + ":" + cal_sec;

    try {

        pstmt = con.prepareStatement ("" +
                "INSERT INTO activity_sheet_history " +
                "(sheet_id, date_time, username, players) VALUES (?, ?, ?, ?)");
        pstmt.clearParameters();
        pstmt.setInt(1, sheet_id);
        pstmt.setString(2, mdateS);
        pstmt.setString(3, user);
        pstmt.setString(4, players);

        pstmt.executeUpdate();

    } catch (Exception e1) {

        SystemUtils.logError("Error in SystemUtils.updateActHist(): sheet_id=" + sheet_id + ", user=" + user + ", players=" + players + ", Exception: " +e1.getMessage() );

    } finally {

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

 }


 //************************************************************************
 //  updateHist - track all changes to tee times - maintain a history log
 //
 //   called by:  Proshop_slot
 //               Proshop_slotm
 //               Proshop_dsheet
 //               Proshop_evntChkAll
 //               Member_slot
 //               Member_slotm
 //
 //************************************************************************
 public static void updateHist(long date, String day, int time, int fb, String course, String p1, String p2, String p3, String p4, String p5,
                               String user, String mName, int type, Connection con) {

     updateHist(date, day, time, fb, course, p1, p2, p3, p4, p5, user, mName, type, 0, 0, con);
 }
 
 
 public static void updateHist(long date, String day, int time, int fb, String course, String p1, String p2, String p3, String p4, String p5,
                               String user, String mName, int type, int email_suppressed, int make_private, Connection con) {


     //String tmp_error = "";
     //String ampm = " AM";
     String day_name = "";
     String mm_name = "";
     String sdate = "";

     String [] mm_table = { "inv", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
                          "Sep", "Oct", "Nov", "Dec" };

     String [] day_table = { "inv", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

     //
     //  Get the current date/time
     //
     Calendar cal = new GregorianCalendar();        // get todays date
     int year = cal.get(Calendar.YEAR);
     int month = cal.get(Calendar.MONTH);
     int day_wk = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)
     int daynum = cal.get(Calendar.DAY_OF_MONTH);
     int cal_hourDay = cal.get(Calendar.HOUR_OF_DAY);    // 24 hr clock (0 - 23)
     int cal_min = cal.get(Calendar.MINUTE);
     int cal_sec = cal.get(Calendar.SECOND);
     int cal_mil = cal.get(Calendar.MILLISECOND);

      //
      //    Adjust the time based on the club's time zone (we are Central)
      //
      int cal_time = (cal_hourDay * 100) + cal_min;     // get time in hhmm format

      cal_time = adjustTime(con, cal_time);             // adjust the time to club's time zone

      if (cal_time < 0) {          // if negative, then we went back or ahead one day

         cal_time = 0 - cal_time;        // convert back to positive value

         if (cal_time < 1200) {           // if AM, then we rolled ahead 1 day (allow for Saudi time)

            //
            // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
            //
            cal.add(Calendar.DATE,1);                     // get next day's date

            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            daynum = cal.get(Calendar.DAY_OF_MONTH);
            day_wk = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)

         } else {                        // we rolled back 1 day

            //
            // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
            //
            cal.add(Calendar.DATE,-1);                     // get yesterday's date

            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            daynum = cal.get(Calendar.DAY_OF_MONTH);
            day_wk = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)
         }
      }

      month++;                                      // month starts at zero

      int cal_hour = cal_time / 100;                // get adjusted hour
      cal_min = cal_time - (cal_hour * 100);        // get minute value

      /*                  // use military time so entries will list in order
      if (cal_hour > 11) {

         cal_hour = cal_hour - 12;         // set to 12 hr clock
         ampm = " PM";
      }

      if (cal_hour == 0) cal_hour = 12;
       */


      day_name = day_table[day_wk];         // get name for day

      mm_name = mm_table[month];             // get name for month


     long mdate = (year * 10000) + (month * 100) + daynum;         // date value for today (for sorting the records)

     //year--;
     //long odate = (year * 10000) + (month * 100) + daynum;         // create a date value of one year ago

     //String sdate = String.valueOf(new java.util.Date());

     //
     //  Build a date and time string for the history entry (i.e.  Wed Aug 12 10:04:52:123 AM - 123 is milli-seconds)
     //
     if (cal_hour < 10) {

        sdate = day_name + " " + mm_name + " " + daynum + " 0" + cal_hour;

     } else {

        sdate = day_name + " " + mm_name + " " + daynum + " " + cal_hour;
     }

     if (cal_min < 10) {

        sdate += ":0" + cal_min;

     } else {

        sdate += ":" + cal_min;
     }

     if (cal_sec < 10) {

        sdate += ":0" + cal_sec;

     } else {

        sdate += ":" + cal_sec;
     }


     if (cal_mil < 10) {

        sdate += ".00" + cal_mil;

     } else {

        if (cal_mil < 100) {

           sdate += ".0" + cal_mil;

        } else {

           sdate += "." + cal_mil;
        }
     }


/*
     //
     //   Create the teehist table if it does not already exist
     //
     try {

        Statement stmt = con.createStatement();        // create a statement

        // if needed create teehist table
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS teehist (" +
                         "date bigint, " +
                         "day varchar(10), time integer, " +
                         "fb smallint, courseName varchar(30), " +
                         "player1 varchar(43), player2 varchar(43), " +
                         "player3 varchar(43), player4 varchar(43), player5 varchar(43), " +
                         "user varchar(15), mname varchar(50), " +
                         "mdate bigint, sdate varchar(36), type smallint, " +
                         "INDEX ind1 (date, time, fb, courseName))");

        stmt.close();

     } catch (Exception exc) {

         logError("SystemUtils.updateHist: Create teehist (" + getClubName(con) + ") " + exc.getMessage());
     }
*/


     //
     // Insert an entry in the history table.  Save the new tee time info and some tracking data.
     //
     //   user = username of person making the change
     //   mname = their name
     //   mdate = current date in yyyymmdd format for searches
     //   sdate = use current date/time stamp in Club's time zone
     //   type = 0 is new tee time, 1 = modified tee time
     //
     if (date > 0 && time > 0 && !user.equals( "" )) {

        try {

           //
           //  Insert a history entry
           //
           PreparedStatement pstmt = con.prepareStatement (
                      "INSERT INTO teehist (date, day, time, fb, courseName, " +
                      "player1, player2, player3, player4, player5, " +
                      "user, mname, mdate, sdate, type, email_suppressed, make_private) " +
                      "VALUES (?, ?, ?, ?, ?, " +
                      "LEFT(?, 43), LEFT(?, 43), LEFT(?, 43), LEFT(?, 43), LEFT(?, 43), " +
                      "?, ?, ?, ?, ?, ?, ?)");

           pstmt.clearParameters();
           pstmt.setLong(1, date);
           pstmt.setString(2, day);
           pstmt.setInt(3, time);
           pstmt.setInt(4, fb);
           pstmt.setString(5, course);
           pstmt.setString(6, p1);
           pstmt.setString(7, p2);
           pstmt.setString(8, p3);
           pstmt.setString(9, p4);
           pstmt.setString(10, p5);
           pstmt.setString(11, user);
           pstmt.setString(12, mName);
           pstmt.setLong(13, mdate);
           pstmt.setString(14, sdate);
           pstmt.setInt(15, type);
           pstmt.setInt(16, email_suppressed);
           pstmt.setInt(17, make_private);

           pstmt.executeUpdate();          // execute the prepared stmt

           pstmt.close();

        } catch (Exception exc) {

            logError("SystemUtils.updateHist: Update teehist (" + getClubName(con) + ")" + exc.getMessage());
        }
     }

  }       // end of updateHist


/*

 //************************************************************************
 //  verifyTimer - verify that the 2 minute inactTimer was actually 2 minutes
 //
 //   called by:  inactTimer
 //
 //************************************************************************

 public static boolean verifyTimer(int hr, int min, int sec) {


   boolean error = false;

   String tmp_error = "SystemUtils.verifyTimer: 2 minute tee time timer expired too early.";

   int newTime = (hr * 10000) + (min * 100) + sec;     // create time stamp from current time

   if (inactTime == 0) {                               // if first time here

      inactTime - newTime;                             // set this time

   } else {

              // Do not need this method right now - finish this later if needed
   }

   if ( ) {

         error = true;                        // indicate error

         logError(tmp_error);                 // log it
   }

   return(error);
  }

*/


 //************************************************************************
 //
 //  sessionLog - logs each time a user logs in
 //
 //   called by:  Login (see Logout also)
 //
 //************************************************************************

 public static void sessionLog(String msg, String user, String pw, String club, String caller, Connection con) {

    Statement stmt = null;
    PreparedStatement pstmt = null;

    try {

      //
      //   Get current date
      //
      Calendar cal = new GregorianCalendar();                      // get todays date
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH) +1;
      int daynum = cal.get(Calendar.DAY_OF_MONTH);

      long date = (year * 10000) + (month * 100) + daynum;         // date value for today

      String sdate = String.valueOf(new java.util.Date());         // get date and time string

      //
      //  Build the message
      //
      msg = msg + " User=" +user+ ", PW=" +pw+ ", Club=" +club+ ", Caller=" +caller;

      //
      //  Create table to hold log in case it has not already been added for this club
      //
      if (con != null) {
/*
         stmt = con.createStatement();        // create a statement
         stmt.executeUpdate("CREATE TABLE IF NOT EXISTS sessionlog(" +
                            "date bigint, sdate varchar(36), msg text, " +
                            "index ind1 (date))");
         stmt.close();
*/
         //
         //  Save the session message in the db table
         //
         pstmt = con.prepareStatement (
              "INSERT INTO sessionlog (date, sdate, msg) VALUES (?,?,?)");

         pstmt.clearParameters();
         pstmt.setLong(1, date);
         pstmt.setString(2, sdate);
         pstmt.setString(3, msg);
         pstmt.executeUpdate();
      }

    }
    catch (Exception e) {

        logError("Error in SystemUtils.sessionLog for club: " +club+ ". Exception= " + e.getMessage());

    } finally {

        try {
            if (stmt != null) stmt.close();
        } catch (Exception ignore) {}

        try {
            if (pstmt != null) pstmt.close();
        } catch (Exception ignore) {}

    }

 }  // end of sessionLog


 //************************************************************************
 //  logError - logs system messages to a db table (errorlog) in Vx db
 //
 //
 //   called by:  Nearly all servelts
 //
 //************************************************************************

 public static void logError(String msg) {

    Utilities.logError( msg );

 }  // end of logError


 //************************************************************************
 //  logErrorTxt - logs error messages to a text file in the V5 folder
 //************************************************************************

 public final static void logErrorTxt(String msg, String club) {

    Utilities.logErrorTxt( msg, club );

 }  // end of logErrorTxt


 //************************************************************************
 //  logError2 - logs system error messages to a text file
 //
 //       *** NOT USED ***
 //
 //************************************************************************

 public static void logError2(String msg, parmLott parm, parmLottC parmc) {

   String space = "  ";

   int time = 0;
   int fb = 0;
   int wght = 0;
   long id = 0;


   try {
      //
      //  dir path for test pc
      //
      PrintWriter fout = new PrintWriter(new FileWriter("c:\\java\\tomcat\\webapps\\" +rev+ "\\error.txt", true));

      //
      //  Put header line in text file
      //
      fout.print(new java.util.Date() + space + msg);
      fout.println();      // output the line

      fout.print(parm.sfb+ ", " +parm.stime+ ", " +parm.etime+ ", " +parm.ltype);
      fout.println();      // output the line

      for (int i=0; i<100; i++) {

         time = parmc.timeA[i];
         fb = parmc.fbA[i];
         id = parmc.idA[i];
         wght = parmc.wghtA[i];

         if (time > 0 || id > 0) {

            fout.print(time+ ", " +fb+ ", " +id+ ", " +wght);
            fout.println();      // output the line

         } else {

            break;
         }
      }

      fout.close();
   }
   catch (Exception ignore) {
   }

 }  // end of logError2

 //************************************************************************
 //  logError - logs error messages to a text file in the club's folder
 //************************************************************************

 public final static void logErrorToFile(String msg, String club, boolean append) {

   int fail = 0;

   try {
      //
      //  Dir path for the real server
      //
      PrintWriter fout1 = new PrintWriter(new FileWriter("//usr//local//tomcat//webapps//" + club + "//" + club + "-rsync-log.txt", append));

      //
      //  Put header line in text file
      //
      fout1.print(msg);
      fout1.println();      // output the line

      fout1.close();

   }
   catch (Exception e2) {

      fail = 1;
   }

 }  // end of logError


 //************************************************************************
 //  getStackAsString - convert the stack trace to a string given the exception
 //
 //         (taken from oreilly - see page 152 in Java Servlet Programming)
 //
 //
 //************************************************************************

 public static String getStackAsString(Throwable t) {


   ByteArrayOutputStream bytes = new ByteArrayOutputStream();

   PrintWriter writer = new PrintWriter(bytes, true);

   t.printStackTrace(writer);

   return bytes.toString();

 }  // end of getStackAsString


 //************************************************************************
 //
 // Return a string with a leading zero is nessesary
 //
 //************************************************************************
 public static String ensureDoubleDigit(int value) {

    return Utilities.ensureDoubleDigit(value);

    //return ((value < 10) ? "0" + value : "" + value);

 }


 //************************************************************************
 //
 // Returns a formated string from passed in military time
 //
 //************************************************************************
 public static String getSimpleTime(int time) {

    return Utilities.getSimpleTime(time / 100, time - ((time / 100) * 100));

    //return getSimpleTime(time / 100, time - ((time / 100) * 100));

 }


 //************************************************************************
 //
 // Returns a formated string from passed in parts
 //
 //************************************************************************
 public static String getSimpleTime(int hr, int min) {

    return Utilities.getSimpleTime(hr, min);

    /*
    String ampm = " AM";

    if (hr == 12) ampm = " PM";
    if (hr > 12) { ampm = " PM"; hr = hr - 12; }    // convert to conventional time
    if (hr == 0) hr = 12;

    return hr + ":" + ensureDoubleDigit(min) + ampm;
    */
 }


 //************************************************************************
 //
 // Returns a string containing a formal date and time
 //
 //************************************************************************
 public static String getLongDateTime(int date, int time, String seperator, Connection con) {

    return Utilities.getLongDateTime(date, time, seperator, con);

    /*
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    String sdate = "";
    String sql = "SELECT DATE_FORMAT(?, '%a %b %D, %Y') AS d";

    if (date == 0) sql = "SELECT DATE_FORMAT(now(), '%a %b %D, %Y') AS d";

    try {

        pstmt = con.prepareStatement ( sql );
        pstmt.clearParameters();
        if (date != 0) pstmt.setInt(1, date);
        rs = pstmt.executeQuery();
        if (rs.next()) sdate = rs.getString(1);

    } catch (Exception ignore) {

    } finally {

        try {
            if (rs != null) rs.close();
        } catch (Exception ignore) {}

        try {
            if (pstmt != null) pstmt.close();
        } catch (Exception ignore) {}

    }

    return sdate + seperator + getSimpleTime(time);
    */
 }


 //************************************************************************
 //
 // Accepts time parts from user input (12hr with AM/PM indicator)
 // and returns our common time integer in 24hr format
 //
 //************************************************************************
 public static int getIntTime(int hr, int min, String ampm) {

    return Utilities.getIntTime(hr, min, ampm);

    /*
    int time = 0;

    if ((ampm.equals("PM") || ampm.equals("12")) && hr != 12) hr = hr + 12;
    if ((ampm.equals("AM") || ampm.equals("00")) && hr == 12) hr = 0;
    time = hr * 100;
    time = time + min;

    return time;
    */
 }


 //************************************************************************
 //
 // Display a standard database error message
 //
 //************************************************************************
 public static void buildDatabaseErrMsg(String pMessage, String pException, PrintWriter out, boolean pNewPage) {

    if (pMessage == null) pMessage = "";
    if (pException == null) pException = "";

    if (pNewPage) {
        out.println(SystemUtils.HeadTitle("Database Error"));
        out.println("<BODY BGCOLOR=\"white\">");
    }

    out.println("<BR><BR><H2>Database Access Error</H2>");
    out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
    out.println("<BR>Please try again later.");

    if (!pMessage.equals("")) out.println("<BR>Fatal Error: " + pMessage);
    if (!pException.equals("")) out.println("<BR>Exception: " + pException);

    out.println("<BR>If problem persists, contact customer support.");
    out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
    out.println("</BODY></HTML>");
    out.close();

 }


 //************************************************************************
 //
 // Returns the UID clubparm_id for the specified tee time.  Returns
 // if method fails or not found.
 //
 //************************************************************************
 public static int getClubParmIdFromTeeCurrID(int pTeeCurrID, Connection con) {

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    int clubparm_id = 0;

    try {

        pstmt = con.prepareStatement("SELECT courseName FROM teecurr2 WHERE teecurr_id = ?");
        pstmt.clearParameters();
        pstmt.setInt(1, pTeeCurrID);
        rs = pstmt.executeQuery();

        if (rs.next()) {

            // we found a course name, now lets look up its uid
            clubparm_id = getClubParmIdFromCourseName(rs.getString("courseName"), con);
        }

    } catch (Exception ignore) {

    } finally {

        try {
            if (rs != null) rs.close();
        } catch (Exception ignore) {}

        try {
            if (pstmt != null) pstmt.close();
        } catch (Exception ignore) {}

    }

    return clubparm_id;
 }


 //************************************************************************
 //
 // Returns the UID clubparm_id for the specified course name.  Returns
 // if method fails or not found.
 //
 //************************************************************************
 public static int getClubParmIdFromCourseName(String pCourseName, Connection con) {

    ResultSet rs = null;
    PreparedStatement pstmt = null;
    int clubparm_id = 0;

    try {

        pstmt = con.prepareStatement("SELECT clubparm_id FROM clubparm2 WHERE courseName = ?");
        pstmt.clearParameters();
        pstmt.setString(1, pCourseName);
        rs = pstmt.executeQuery();
        if (rs.next()) clubparm_id = rs.getInt(1);

    } catch (Exception ignore) {

    } finally {

        try {
            if (rs != null) rs.close();
        } catch (Exception ignore) {}

        try {
            if (pstmt != null) pstmt.close();
        } catch (Exception ignore) {}

    }

    return clubparm_id;
 }


 //************************************************************************
 //
 // Connect to mail server database and retrieved rs of all bounced emails
 // and purge them from the club databases. Called by TeeTimer.java
 //
 //************************************************************************
 public static void purgeBouncedEmails() {

    Connection con = null; // initally remote db
    Connection con2 = null; // local
    Statement stmt = null;
    Statement stmt2 = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    int count = 0;
    int total = 0;
    int club_count = 0;
    String club = "";
    String email = "";

    //
    //  This must be the master server!!!  If not, let the timer run in case master goes down.
    //
    if (Common_Server.SERVER_ID == TIMER_SERVER) {

        long startTime = System.currentTimeMillis();
        logError("Starting SystemUtils.purgeBouncedEmails()");

        // move records from mail server db to the foretees db
        try {

            // get con to mail servers db
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection("jdbc:mysql://216.243.184.88/xmail_bounces", "xmail_filters", "xmfilmail");
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM bounced WHERE email <> ''");

            // con to main db
            con2 = dbConn.Connect("xmail_bounces");

            while (rs.next()) {

                email = rs.getString("email").replace("\"", "");
                stmt2 = con2.createStatement();
                stmt2.executeUpdate("INSERT INTO bounced (email) VALUES (\"" + email + "\") ON DUPLICATE KEY UPDATE email = \"" + email + "\"");
                count++;
                stmt2.close();
            }

            stmt.close();

            logError("XMail Bouncer: Retrieved " + count + " email addresses from bounce backs.");

            // delete any empty email address on main db server (this shouldn't happen now, but it causes problems if present)
            stmt = con2.createStatement();
            stmt.executeUpdate("DELETE FROM bounced WHERE email = ''");
            stmt.close();

            // empty addresses stored on mail server
            stmt = con.createStatement();
            stmt.executeUpdate("DELETE FROM bounced");
            stmt.close();
        }
        catch (Exception e0) {

            logError("XMail Bouncer: Error moving records. (" + count + ") " + e0.getMessage());
        }

        try {
            if (con != null) con.close();
        } catch (Exception ignore) {}

        try {
            if (con2 != null) con2.close();
        } catch (Exception ignore) {}

        con = null;
        con2 = null;


        //
        //  Perform timer function for each club in the system database 'clubs' table
        //
        try {
            con = dbConn.Connect(rev);
        }
        catch (Exception e1) {

            logError("Error connecting to " + rev + " in SystemUtils.purgeBouncedEmails: " + e1.getMessage());
        }

        if (con != null) {

            //
            // Get the club names from the 'clubs' table
            //
            //  Process each club in the table
            //
            try {

                stmt = con.createStatement();
                rs = stmt.executeQuery("SELECT clubname FROM clubs WHERE inactive = 0 ORDER BY clubname");

                while (rs.next()) {

                    count = 0;

                    club = rs.getString(1);                 // get a club name
                    con2 = dbConn.Connect(club);            // get a connection to this club's db

                    stmt2 = con2.createStatement();
                    count = stmt2.executeUpdate("" +
                            "UPDATE member2b m, xmail_bounces.bounced b " +
                            "SET " +
                                "m.email_bounced = IF(m.email=b.email, 1, 0), " +
                                "m.email2_bounced = if(m.email2=b.email, 1, 0) " +
                            "WHERE " +
                                "m.email = b.email " +
                                "OR " +
                                "m.email2 = b.email");

                    stmt2.close();

                    con2.close();
                    con2 = null;

                    if (count > 0) {
                        total += count;
                        club_count++;
                        // we removed an address from this club - log it (removed until we improve the log)
                        //logError("XMail Bouncer: Disabled " + count + " from " + club);
                    }

                } // end while loop of all clubs

                stmt.close();

                con.close();
                con = null;


                // delete bounced records on foretees db server
                con2 = dbConn.Connect("xmail_bounces");
                stmt = con2.createStatement();
                stmt.executeUpdate("DELETE FROM bounced");

                stmt.close();

                con2.close();
                con2 = null;

                logError("XMail Bouncer: Disabled " + total + " emails from " + club_count + " clubs.");

            } catch (Exception e2) {

                logError("Error2 in SystemUtils.purgeBouncedEmails: club=" + club + ", " + e2.getMessage());
            } // end try/catch

            try {
               if (con != null) con.close();
            } catch (Exception ignore) {}

            try {
               if (con2 != null) con2.close();
            } catch (Exception ignore) {}

        } // end con check

        logError("Finished SystemUtils.purgeBouncedEmails() run time:" + (System.currentTimeMillis() - startTime) + "ms");

    } // end if TIMER_SERVER

 }


 public static String scrubString(String pValue) {

     if (pValue != null) pValue = pValue.trim();
     return pValue;
 }


 public static String getUsernameFromFullName(String fullMemberName, Connection con) {

    // FIRST GET THE USERNAME FOR THIS MEMBER FROM THEIR FULL NAME
    StringTokenizer tok = new StringTokenizer( fullMemberName );     // space is the default token
    String fname = "";
    String mi = "";
    String lname = "";
    String user = "";

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    if ( tok.countTokens() == 2 ) {         // first name, last name

        fname = tok.nextToken();
        lname = tok.nextToken();
    }

    if ( tok.countTokens() == 3 ) {         // first name, mi, last name

        fname = tok.nextToken();
        mi = tok.nextToken();
        lname = tok.nextToken();
    }

    try {

        pstmt = con.prepareStatement (
            "SELECT username " +
            "FROM member2b " +
            "WHERE name_last = ? AND name_first = ? AND name_mi = ?");

        if (!fname.equals("") && !lname.equals("")) {

            pstmt.clearParameters();
            pstmt.setString(1, lname);
            pstmt.setString(2, fname);
            pstmt.setString(3, mi);
            rs = pstmt.executeQuery();

            if (rs.next()) user = rs.getString("username");

        }

    } catch (Exception ignore) {

    } finally {

        try {
            if (rs != null) rs.close();
        } catch (Exception ignore) {}

        try {
            if (pstmt != null) pstmt.close();
        } catch (Exception ignore) {}

    }

    return user;

 }


 public static String getFullNameFromUsername(String username, Connection con) {

    String ret = "";
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {

        pstmt = con.prepareStatement("" +
                "SELECT CONCAT(name_first, ' ', name_last) AS fullName " +
                "FROM member2b " +
                "WHERE username = ?;");
        pstmt.clearParameters();
        pstmt.setString(1, username);
        rs = pstmt.executeQuery();

        if (rs.next()) ret = rs.getString("fullName");

    } catch (Exception ignore) {

    } finally {

        try {
            if (rs != null) rs.close();
        } catch (Exception ignore) {}

        try {
            if (pstmt != null) pstmt.close();
        } catch (Exception ignore) {}

    }

    return ret;

 }



 // ***************************************************************
 // Process request to build the 'Days in Adv' array for members
 //
 //   This method will calculate the days in advance that the member
 //   is allowed to edit tee sheets.  Each day is represented in the
 //   daysArray (int array) where the value indicates:
 //          0 = No (View Only)
 //          1 = Yes, normal tee sheet access (green)
 //          2 = Yes, Lottery access only (red)
 //   This array is used each time a calendar is to be
 //   built for the member.
 //
 // ***************************************************************

 public static DaysAdv daysInAdv(DaysAdv daysArray, String club, String mship, String mtype, String user, Connection con) {

    return daysInAdv(daysArray, club, mship, mtype, user, 0, con);

 }


 public static DaysAdv daysInAdv(DaysAdv daysArray, String club, String mship, String mtype, String user, int activity_id, Connection con) {


   Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;
   ResultSet rs3 = null;
   ResultSet rs4 = null;

   int root_activity_id = 0;

   try { root_activity_id = getActivity.getRootIdFromActivityId(activity_id, con); }
   catch (Exception ignore) { }

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(root_activity_id, con);          // allocate a parm block

   String lottery1 = "";
   String lottery2 = "";
   String msubtype = "";

   int index = 0;
   int lott = 0;
   int days = 0;
   int days1 = 0;               // days in advance that members can make tee times
   int days2 = 0;               //         one per day of week (Sun - Sat)
   int days3 = 0;
   int days4 = 0;
   int days5 = 0;
   int days6 = 0;
   int days7 = 0;
   int sdays = 0;
   int cal_time = 0;            // calendar time for compares
   //int count = 0;               // init day counter
   //int col = 0;                 // init column counter
   //int d = 0;                   // 'days in advance' value for current day of week

   long date = 0;

   //
   //  Array to hold the 'Days in Advance' value for each day of the week
   //
   int [] advdays = new int [7];                        // 0=Sun, 6=Sat
   int [] advtime = new int [7];                        // adv time for each

   //
   //  init the array (1 entry per day, relative to today)
   //
   int max = daysArray.MAXDAYS;           // get max days in advance (length of array)

   for (index = 0; index < max; index++) {

      daysArray.days[index] = 0;
   }

   boolean reconnect = false;
   try {
       stmt = con.createStatement();
   } catch (Exception exp) {
       reconnect = true;
   }

   if (reconnect) {
       try {
           con = dbConn.Connect(club);
           //SystemUtils.logError("SystemUtils.daysInAdv for club: " + club + ". Reconnected to db.");
       } catch (Exception exp) {
           //SystemUtils.logError("SystemUtils.daysInAdv for club: " + club + ". Failed to reconnect to db.");
       }
   }

   //
   // Get the Lottery Option, days in advance and time for advance from the club db
   //
   try {

      getClub.getParms(con, parm, activity_id);        // get the club parms

   }
   catch (Exception e1) {

      SystemUtils.logError("Error1 in SystemUtils.daysInAdv for club: " + club + ". Exception: " +e1.getMessage());                           // log it
      return (null);
   }

   //
   //  use the member's mship type to determine which 'days in advance' parms to use
   //
   verifySlot.getDaysInAdv(con, parm, mship, activity_id);        // get the days in adv data for this member

   days1 = parm.advdays1;           // get days in adv for this type
   days2 = parm.advdays2;           // Monday
   days3 = parm.advdays3;
   days4 = parm.advdays4;
   days5 = parm.advdays5;
   days6 = parm.advdays6;
   days7 = parm.advdays7;           // Saturday

   advtime[0] = parm.advtime1;      // get time values
   advtime[1] = parm.advtime2;
   advtime[2] = parm.advtime3;
   advtime[3] = parm.advtime4;
   advtime[4] = parm.advtime5;
   advtime[5] = parm.advtime6;
   advtime[6] = parm.advtime7;


   lott = parm.lottery;

   daysArray.maxview = parm.memviewdays;    // days this member can view tee sheets


/*
   //
   //  If Jonathan's Landing and an certain member types - change days in advance to 6 - Case# 1328
   //
   if (club.equals( "jonathanslanding" ) &&
           (mship.equals( "Golf" ) || mship.equals( "Golf Asc" ) || mship.equals( "Golf Sr" )) ) {

      days1 = 6;                    // change to 6 days
      days2 = 6;
      days3 = 6;
      days4 = 6;
      days5 = 6;
      days6 = 6;
      days7 = 6;
   }
*/
   //
   //  If El Niguel and an Adult Female - change the Tuesday Days and Time
   //
   if (club.equals( "elniguelcc" ) && mtype.equals( "Adult Female" ) && activity_id == 0) {

      days3 = 4;                    // Tues = 4 (normally is 2)
      advtime[2] = 1300;            // at 1:00 PM
   }

   //
   //   Scioto Custom - change the days in adv for Spouses - Sun, Mon, Thur, Fri, Sat = 2, Tue, Wed = 3
   //
   if (club.equals( "sciotocc" ) && mtype.startsWith( "Spouse" ) && activity_id == 0) {

      days1 = 3;          // Sun = 3 days in advance (starting at 7:30 AM)
      days2 = 3;          // Mon = 3 days in advance (starting at 7:30 AM)
      days3 = 4;          // Tue = 4 days in advance (starting at 7:30 AM)
      days4 = 4;          // Wed = 4 days in advance (starting at 7:30 AM)
      days5 = 4;          // Thu = 4 days in advance (starting at 7:30 AM)
      days6 = 3;          // Fri = 3 days in advance (starting at 7:30 AM)
      days7 = 3;          // Sat = 3 days in advance (starting at 7:30 AM)

      //advtime[5] = 1200;  // Changed back to 7:30
   }

   //
   //  If Hazeltine, check if days in adv should change
   //
   if ( club.equals( "hazeltine" ) && activity_id == 0 ) {

      //
      //  Get the member's sub-type to determine if change is needed
      //
      try {
         PreparedStatement pstmt1 = con.prepareStatement (
            "SELECT msub_type " +
            "FROM member2b WHERE username = ?");

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, user);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            msubtype = rs.getString("msub_type");
         }
         pstmt1.close();
      }
      catch (Exception e) {

          SystemUtils.logError("Error in SystemUtils.daysInAdv (msub_type) for club: " + club + ", user (" + user + "). Exception: " +e.getMessage());                           // log it
      }

      //
      //  If a Female and sub-type is 'After Hours', '9 holer', or combo, then set Tuesdays to 14 days adv.
      //
      //    Time Limits are enforced in Member_sheet !!!!!!!!!!!
      //
      if ((mtype.equals("Adult Female")) && (msubtype.equals("After Hours") || msubtype.equals("9 Holer") ||
          msubtype.startsWith("AH-") || msubtype.equals("9/18 Holer"))) {

         days3 = 14;      // set 14 days in advance for Tuesdays (all 'After Hours' and 9-Holers)
      }

      if ((mtype.equals("Adult Female")) && (msubtype.equals("18 Holer") || msubtype.startsWith("AH-9/18") ||
          msubtype.startsWith("AH-18") || msubtype.equals("9/18 Holer"))) {

         days5 = 14;      // set 14 days in advance for Thursdays (all 18-Holers)
      }
   }

   //
   //  Get today's date and setup parms
   //
   Calendar cal = new GregorianCalendar();             // get todays date
   int year = cal.get(Calendar.YEAR);
   int month = cal.get(Calendar.MONTH);
   int day = cal.get(Calendar.DAY_OF_MONTH);
   int cal_am_pm = cal.get(Calendar.AM_PM);            // current time
   int cal_hourDay = cal.get(Calendar.HOUR_OF_DAY);
   int cal_min = cal.get(Calendar.MINUTE);
   int day_num = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07, Sun - Sat)

   cal_time = (cal_hourDay * 100) + cal_min;

   cal_time = adjustTime(con, cal_time);   // adjust the time

   if (cal_time < 0) {          // if negative, then we went back or ahead one day

      cal_time = 0 - cal_time;        // convert back to positive value - ok for compare below

      if (cal_time < 100) {           // if hour is zero, then we rolled ahead 1 day

         //
         // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
         //
         cal.add(Calendar.DATE,1);                       // get next day's date
         year = cal.get(Calendar.YEAR);
         month = cal.get(Calendar.MONTH);
         day = cal.get(Calendar.DAY_OF_MONTH);
         day_num = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)

      } else {                        // we rolled back 1 day

         //
         // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
         //
         cal.add(Calendar.DATE,-1);                      // get yesterday's date
         year = cal.get(Calendar.YEAR);
         month = cal.get(Calendar.MONTH);
         day = cal.get(Calendar.DAY_OF_MONTH);
         day_num = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)
      }
   }

   month++;                            //  adjust month

   //
   //  if its earlier than the time specified for days in advance, do not allow the last day_in_advance
   //
   //  Must check this again when building the calendars!!!!!!!!!
   //
   if (advtime[0] > cal_time) {

      days1--;
   }
   if (advtime[1] > cal_time) {

      days2--;
   }
   if (advtime[2] > cal_time) {

      days3--;
   }
   if (advtime[3] > cal_time) {

      days4--;
   }
   if (advtime[4] > cal_time) {

      days5--;
   }
   if (advtime[5] > cal_time) {

      days6--;
   }
   if (advtime[6] > cal_time) {

      days7--;
   }


     /*    // removed per Mike Scully's request 5/05/09
   //
   //   Medinah Custom - if Monday (today) and days in adv = 30, change to 29 (proshop closed on Mondays)  Case #1225
   //
   if (club.equals( "medinahcc" )) {     // if Medinah and today is Monday

       if ( day_num == 2 || (day_num == 3 && cal_time < 600) ) {

          if (days1 == 30) {
             days1 = 29;
          }
          if (days2 == 30) {
             days2 = 29;
          }
          if (days3 == 30) {
             days3 = 29;
          }
          if (days4 == 30) {
             days4 = 29;
          }
          if (days5 == 30) {
             days5 = 29;
          }
          if (days6 == 30) {
             days6 = 29;
          }
          if (days7 == 30) {
             days7 = 29;
          }

       }
   }
      */


/*
   //
   //   Los Coyotes Custom - change the days in adv for Secondary Members from to 3 days
   //
   if (club.equals( "loscoyotes" ) && mtype.startsWith( "Secondary" )) {

      days1 = 3;
      days2 = 3;
      days3 = 3;
      days4 = 3;
      days5 = 3;
      days6 = 3;
      days7 = 3;
   }
*/

   //
   //  put the 'days in advance' values in an array to be used below
   //
   advdays[0] = days1;
   advdays[1] = days2;
   advdays[2] = days3;
   advdays[3] = days4;
   advdays[4] = days5;
   advdays[5] = days6;
   advdays[6] = days7;

   //
   //  Set value in daysArray for each day up to max
   //
   day_num--;                           // convert today's day_num to index (0 - 6)

   for (index = 0; index < max; index++) {

      days = advdays[day_num];             // get days in advance for day of the week

      day_num++;                           // bump to next day of week

      if (day_num > 6) {                   // if wrapped past end of week

         day_num = 0;
      }

      date = (year * 10000) + (month * 100) + day;     // create date (yyyymmdd) for this day

      //
      // roll cal ahead 1 day for next time thru here
      //
      cal.add(Calendar.DATE,1);                       // get next day's date
      year = cal.get(Calendar.YEAR);
      month = cal.get(Calendar.MONTH) +1;
      day = cal.get(Calendar.DAY_OF_MONTH);

      //
      // check if this day can be accessed by members
      //
      //    0 = No, 1 = Yes, 2 = Yes for Lottery only
      //
      if (days >= index) {               // if ok for this day (use index since today is automatic)

         daysArray.days[index] = 1;        // set ok in array

      } else {

         // default to no access, then check for lottery and set to 2 if needed
         daysArray.days[index] = 0;        // set to no access in array

         //
         //  determine if a lottery is setup for this day, and if the signup is longer than 'd' days
         //
         if (activity_id == 0 && lott != 0) {                 // if lottery supported by this club

            int found = 0;      // init skip switch
            int tmp_err = 0;    // failure location check

            //
            //  Look for any lotteries on this date (any course) - up to 3 of them for one day
            //
            try {
               PreparedStatement pstmt1 = con.prepareStatement (
                  "SELECT lottery " +
                  "FROM teecurr2 WHERE date = ? AND lottery != ''");

               pstmt1.clearParameters();        // clear the parms
               pstmt1.setLong(1, date);
               rs = pstmt1.executeQuery();      // execute the prepared stmt
               tmp_err = 1;

               if (rs.next()) {

                  lottery1 = rs.getString(1);

                  PreparedStatement pstmt1b = con.prepareStatement (
                     "SELECT sdays " +
                     "FROM lottery3 WHERE name = ?");

                  pstmt1b.clearParameters();        // clear the parms
                  pstmt1b.setString(1, lottery1);
                  rs2 = pstmt1b.executeQuery();      // execute the prepared stmt
                  tmp_err = 2;

                  if (rs2.next()) {

                     sdays = rs2.getInt(1);    // get days in advance to start taking requests

                  }  // end of IF lottery days 1
                  pstmt1b.close();

                  if (sdays >= index || club.equals("invernessclub")) {       // if ok for this day

                     found = 1;                // indicate found

                  } else {         // check for another (different) lottery on this date

                     PreparedStatement pstmt2 = con.prepareStatement (
                        "SELECT lottery " +
                        "FROM teecurr2 WHERE date = ? AND lottery != ? AND lottery != ''");

                     pstmt2.clearParameters();        // clear the parms
                     pstmt2.setLong(1, date);
                     pstmt2.setString(2, lottery1);
                     rs3 = pstmt2.executeQuery();      // execute the prepared stmt
                     tmp_err = 3;

                     if (rs3.next()) {

                        lottery2 = rs3.getString(1);

                        PreparedStatement pstmt2b = con.prepareStatement (
                           "SELECT sdays " +
                           "FROM lottery3 WHERE name = ?");

                        pstmt2b.clearParameters();        // clear the parms
                        pstmt2b.setString(1, lottery2);
                        rs2 = pstmt2b.executeQuery();      // execute the prepared stmt
                        tmp_err = 4;

                        if (rs2.next()) {

                           sdays = rs2.getInt(1);    // get days in advance to start taking requests

                        }  // end of IF lottery days 1
                        pstmt2b.close();

                        if (sdays >= index || club.equals("invernessclub")) {       // if ok for this day

                           found = 1;                // indicate found

                        } else {         // check for another (different) lottery on this date

                           PreparedStatement pstmt3 = con.prepareStatement (
                              "SELECT lottery " +
                              "FROM teecurr2 WHERE date = ? AND lottery != ? AND lottery != ? AND lottery != ''");

                           pstmt3.clearParameters();        // clear the parms
                           pstmt3.setLong(1, date);
                           pstmt3.setString(2, lottery1);
                           pstmt3.setString(3, lottery2);
                           rs4 = pstmt3.executeQuery();      // execute the prepared stmt
                           tmp_err = 5;

                           if (rs4.next()) {

                              lottery2 = rs4.getString(1);

                              PreparedStatement pstmt3b = con.prepareStatement (
                                 "SELECT sdays " +
                                 "FROM lottery3 WHERE name = ?");

                              pstmt3b.clearParameters();        // clear the parms
                              pstmt3b.setString(1, lottery2);
                              rs2 = pstmt3b.executeQuery();      // execute the prepared stmt
                              tmp_err = 6;

                              if (rs2.next()) {

                                 sdays = rs2.getInt(1);    // get days in advance to start taking requests

                              }  // end of IF lottery days 1
                              pstmt3b.close();

                              if (sdays >= index || club.equals("invernessclub")) {       // if ok for this day

                                 found = 1;                // indicate found
                              }
                           }  // end of IF lottery 3
                           pstmt3.close();

                        }  // end of IF found

                     }  // end of IF lottery 2
                     pstmt2.close();

                  }  // end of IF found

               }  // end of IF lottery 1
               pstmt1.close();
            }
            catch (Exception e1) {

               SystemUtils.logError("Error2 (lott check " + tmp_err + ") in SystemUtils.daysInAdv for club: " + club + ". Exception: " +e1.getMessage());
               return (null);
            }

            if (found != 0) {                   // if a lottery was found for this day

               daysArray.days[index] = 2;       // set ok for lottery in array
            }

         }        // end of IF lottery supported

      }          // end of IF days check

   }  // end of FOR max

   return (daysArray);
 }


 public static int getIndexFromToday(long date, Connection con) {


    PreparedStatement pstmt = null;
    ResultSet rs = null;

    long today = getDate(con);
    int index = 0;

    try {

        pstmt = con.prepareStatement ("SELECT DATEDIFF(?, ?) AS i");
        pstmt.clearParameters();
        pstmt.setLong(1, date);
        pstmt.setLong(2, today);

        rs = pstmt.executeQuery();

        if ( rs.next() ) index = rs.getInt(1);

    } catch (Exception e1) {

        SystemUtils.logError("Error in SystemUtils.getIndexFromToday(): today=" + today + ", date=" + date + ", Exception: " +e1.getMessage() );

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return index;
 }


 //
 //  Return the name of the club
 //
 public static String getClubName(Connection con) {

    String clubName = "";

    try {

       clubName = Utilities.getClubName(con);

    } catch (Exception e1) {

        SystemUtils.logError("Error in SystemUtils.getClubName, Exception: " +e1.getMessage() );
    }

    return clubName;

 } // end getClubName


 //
 //  Return the multi option for club
 //
 public static int getMulti(Connection con) {

    return Utilities.getMulti(con);

 } // end getMulti


 //
 // Build custom tee sheets - called by scanTee and Proshop_customsheets
 //
 public static void buildCustomTees(int sheet_id, long date, Connection con) {

    // called from scanTee above so we can assume that there will be NO existing tee times found for this date

    PreparedStatement pstmt = null;
    PreparedStatement pstmt2 = null;
    ResultSet rs = null;

    int i = 0;
    int first = 1;
    int time = 0;
    int fb = 0;
    int hr = 0;
    int min = 0;
    int yy = (int)date / 10000;
    int mm = ((int)date - (yy * 10000)) / 100;
    int dd = ((int)date - (yy * 10000)) - (mm * 100);

    String course = "";
    String day = "";

    try {

        pstmt2 = con.prepareStatement (
            "INSERT INTO teecurr2 (date, mm, dd, yy, day, hr, min, time, event, event_color, " +
            "restriction, rest_color, player1, player2, player3, player4, username1, " +
            "username2, username3, username4, p1cw, p2cw, p3cw, p4cw, first, in_use, in_use_by, " +
            "event_type, hndcp1, hndcp2, hndcp3, hndcp4, show1, show2, show3, show4, fb, " +
            "player5, username5, p5cw, hndcp5, show5, notes, hidenotes, lottery, courseName, blocker, " +
            "proNew, proMod, memNew, memMod, rest5, rest5_color, mNum1, mNum2, mNum3, mNum4, mNum5, " +
            "lottery_color, userg1, userg2, userg3, userg4, userg5, hotelNew, hotelMod, orig_by, " +
            "conf, p91, p92, p93, p94, p95, pos1, pos2, pos3, pos4, pos5, hole, create_date) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, '', '', '', '', '', '', '', '', '', '', " +
            "'', '', '', '', '', '', ?, 0, '', 0, 0, 0, 0, 0, 0, 0, 0, 0, ?, '', '', '', 0, 0, '', 0, " +
            "'', ?, '', 0, 0, 0, 0, '', '', '', '', '', '', '', '', '', '', '', '', '', 0, 0, '', '', " +
            "0, 0, 0, 0, 0, 0, 0, 0, 0, 0, '', now())");

        pstmt = con.prepareStatement (
            "SELECT ct.*, DATE_FORMAT(?, '%W') AS day_name, cs.course " +
            "FROM custom_sheets cs LEFT OUTER JOIN custom_tee_times ct ON cs.custom_sheet_id = ct.custom_sheet_id " +
            "WHERE cs.custom_sheet_id = ? ORDER BY ct.time, ct.fb");

        pstmt.clearParameters();
        pstmt.setLong(1, date);
        pstmt.setInt(2, sheet_id);
        rs = pstmt.executeQuery();

        while (rs.next()) {

            fb = rs.getInt("fb");
            day = rs.getString("day_name");
            course = rs.getString("course");
            time = rs.getInt("time");

            hr = time / 100;
            min = time - (hr * 100);

            pstmt2.clearParameters();
            pstmt2.setLong(1, date);
            pstmt2.setInt(2, mm);
            pstmt2.setInt(3, dd);
            pstmt2.setInt(4, yy);
            pstmt2.setString(5, day);
            pstmt2.setInt(6, hr);
            pstmt2.setInt(7, min);
            pstmt2.setInt(8, time);
            pstmt2.setInt(9, first);
            pstmt2.setInt(10, fb);
            pstmt2.setString(11, course);

            pstmt2.executeUpdate();

            i++;
            first = 0;

        } // end while loop

        pstmt.close();
        pstmt2.close();

    } catch (Exception e1) {

        SystemUtils.logError("Error in SystemUtils.buildCustomTees(): sheet_id=" + sheet_id + ", date=" + date + ", Exception: " +e1.getMessage() );

    } finally {

       try {
           if (rs != null) rs.close();
       } catch (Exception ignore) {}

       try {
           if (pstmt2 != null) pstmt2.close();
       } catch (Exception ignore) {}

       try {
           if (pstmt != null) pstmt.close();
       } catch (Exception ignore) {}

    }

 }


 //
 // If the time provided is covered by an active wait list then return the uid
 // parms: date, tee time, local time, course name, day name, con
 // Update: curr_time no longer used - doing check in member_sheet
 //
 public static int isWaitListTime(long date, int time, int curr_time, String course, String day_name, Connection con) {

    PreparedStatement pstmt = null;
    ResultSet rs = null;
    int id = 0;

    try {

        pstmt = con.prepareStatement (
            "SELECT wait_list_id " +
            "FROM wait_list " +
            "WHERE " +
                "DATE_FORMAT(sdatetime, '%Y%m%d') <= ? AND DATE_FORMAT(edatetime, '%Y%m%d') >= ? AND " +
                "DATE_FORMAT(sdatetime, '%H%i') <= ? AND DATE_FORMAT(edatetime, '%H%i') >= ? AND " +
                "( member_view_teesheet = 2 OR " + // cutoff_days = 0 OR " +
                 "(DATEDIFF(?, now()) < cutoff_days OR (DATEDIFF(?, now()) = cutoff_days AND DATE_FORMAT(now(), '%k%i') >= cutoff_time) AND member_view_teesheet != 2) ) AND " + // = 0
                "(course = ? OR course = '-ALL-') AND " + day_name + " = 1 AND enabled = 1");

        pstmt.clearParameters();
        pstmt.setLong(1, date);
        pstmt.setLong(2, date);
        pstmt.setLong(3, time);
        pstmt.setLong(4, time);
        //pstmt.setLong(5, curr_time);
        pstmt.setLong(5, date);
        pstmt.setLong(6, date);
        pstmt.setString(7, course);
        rs = pstmt.executeQuery();

        if (rs.next()) id = rs.getInt(1);

        pstmt.close();

    } catch (Exception ignore) {

        // do nothing, if this fails it will return zero which indicates no wait list available

    } finally {

       try {
           if (rs != null) rs.close();
       } catch (Exception ignore) {}

       try {
           if (pstmt != null) pstmt.close();
       } catch (Exception ignore) {}

    }

    return id;

 } // end isWaitListTime


 //
 // Checkes the date/time/course for any wait list signups that have been submitted for given tee time.
 // Called by Proshop_slot to see if we should alert user that there is a signup waiting for this tee time.
 //
 public static int checkWaitListSignup(long date, int time, String course, Connection con) {

    PreparedStatement pstmt = null;
    ResultSet rs = null;
    int count = -1;

    try {

        pstmt = con.prepareStatement (
            "SELECT COUNT(*) " +
            "FROM wait_list_signups wls " +
            "LEFT OUTER JOIN wait_list wl ON wl.wait_list_id = wls.wait_list_id " +
            "WHERE " +
                "DATE_FORMAT(wls.date, '%Y%m%d') = ? AND " +
                "wls.ok_stime <= ? AND wls.ok_etime >= ? AND " +
                "(course = ? OR course = '-ALL-') AND enabled = 1 AND converted = 0");

        pstmt.clearParameters();
        pstmt.setLong(1, date);
        pstmt.setInt(2, time);
        pstmt.setInt(3, time);
        pstmt.setString(4, course);
        rs = pstmt.executeQuery();

        if (rs.next()) count = rs.getInt(1);

        pstmt.close();

    } catch (Exception ignore) {

        // do nothing, if this fails it will return -1 which indicates an error

    } finally {

       try {
           if (rs != null) rs.close();
       } catch (Exception ignore) {}

       try {
           if (pstmt != null) pstmt.close();
       } catch (Exception ignore) {}

    }

    return count;

 } // end isWaitListTime


 //
 //*****************************************************************************
 //  Call this to force a lottery to be processed (FOR TESTING ONLY!!!!!)
 //
 //      name = name of Lottery
 //      date = date of Lottery
 //*****************************************************************************
 //
 public static void testLott(String name, long date, String club, Connection con) {

   // Common_Lott_Orig.processLott(name, date, club, con);    // go process the lottery (original method)
    Common_Lott.processLott(name, date, club, con);    // go process the lottery (new method)

 }   // end of testLott


 //
 //   Get current state of lottery (also called from Member_teelist_list)
 //
 public static int getLotteryState(long date, int mm, int dd, int yy, String lottery, String course, Connection con) {

   PreparedStatement pstmt1 = null;
   ResultSet rs2 = null;

   int lstate = 0;
   int sdays = 0;
   int sdtime = 0;
   int edays = 0;
   int edtime = 0;
   int pdays = 0;
   int ptime = 0;


   //
   //  determine the current state for this lottery
   //
   try {
         pstmt1 = con.prepareStatement (
            "SELECT sdays, sdtime, edays, edtime, pdays, ptime " +
            "FROM lottery3 WHERE name = ?");

         pstmt1.clearParameters();
         pstmt1.setString(1, lottery);

         rs2 = pstmt1.executeQuery();      // find all matching lotteries, if any

         if (rs2.next()) {

            sdays = rs2.getInt(1);         // days in advance to start taking requests
            sdtime = rs2.getInt(2);
            edays = rs2.getInt(3);         // ...stop taking reqs
            edtime = rs2.getInt(4);
            pdays = rs2.getInt(5);         // ....to process reqs
            ptime = rs2.getInt(6);
         }

         //
         //    Determine which state we are in (before req's, during req's, before process, after process)
         //
         //  Get the current time
         //
         Calendar cal = new GregorianCalendar();    // get the current time of the day
         int cal_hour = cal.get(Calendar.HOUR_OF_DAY);  // 00 - 23 (military time - adjusted for time zone)
         int cal_min = cal.get(Calendar.MINUTE);

         //
         //    Adjust the time based on the club's time zone (we are Central)
         //
         int cal_time = (cal_hour * 100) + cal_min;     // get time in hhmm format

         cal_time = SystemUtils.adjustTime(con, cal_time);   // adjust the time

         if (cal_time < 0) {          // if negative, then we went back or ahead one day

            cal_time = 0 - cal_time;        // convert back to positive value

            if (cal_time < 100) {           // if hour is zero, then we rolled ahead 1 day

               //
               // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
               //
               cal.add(Calendar.DATE,1);                     // get next day's date

            } else {                        // we rolled back 1 day

               //
               // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
               //
               cal.add(Calendar.DATE,-1);                     // get yesterday's date
            }
         }

         //
         //  determine the number of days in advance of the req'd tee time we currently are
         //
         int cal_yy = cal.get(Calendar.YEAR);
         int cal_mm = cal.get(Calendar.MONTH) +1;
         int cal_dd = cal.get(Calendar.DAY_OF_MONTH);

         int advance_days = 0;

         while (cal_mm != mm || cal_dd != dd || cal_yy != yy) {

            cal.add(Calendar.DATE,1);                // roll ahead 1 day until a match found

            cal_yy = cal.get(Calendar.YEAR);
            cal_mm = cal.get(Calendar.MONTH);
            cal_dd = cal.get(Calendar.DAY_OF_MONTH);

            cal_mm++;                            // month starts at zero
            advance_days++;
         }

         //
         //  now check the day and time values
         //
         if (advance_days > sdays) {       // if we haven't reached the start day yet

            lstate = 1;                    // before time to take requests

         } else {

            if (advance_days == sdays) {   // if this is the start day

               if (cal_time >= sdtime) {   // have we reached the start time?

                  lstate = 2;              // after start time, before stop time to take requests

               } else {

                  lstate = 1;              // before time to take requests
               }
            } else {                        // we are past the start day

               lstate = 2;                 // after start time, before stop time to take requests
            }

            if (advance_days == edays) {   // if this is the stop day

               if (cal_time >= edtime) {   // have we reached the stop time?

                  lstate = 3;              // after start time, before stop time to take requests
               }
            }

            if (advance_days < edays) {   // if we are past the stop day

               lstate = 3;                // after start time, before stop time to take requests
            }
         }

         if (lstate == 3) {                // if we are now in state 3, check for state 4

            if (advance_days == pdays) {   // if this is the process day

               if (cal_time >= ptime) {    // have we reached the process time?

                  lstate = 4;              // after process time
               }
            }

            if (advance_days < pdays) {   // if we are past the process day

               lstate = 4;                // after process time
            }
         }

         if (lstate == 4) {                // if we are now in state 4, check for state 5

            pstmt1 = con.prepareStatement (
                   "SELECT mm FROM lreqs3 " +
                   "WHERE name = ? AND date = ? AND courseName = ? AND state = 2");

            pstmt1.clearParameters();        // clear the parms
            pstmt1.setString(1, lottery);
            pstmt1.setLong(2, date);
            pstmt1.setString(3, course);
            rs2 = pstmt1.executeQuery();

            if (!rs2.next()) {             // if none waiting approval

               lstate = 5;                // state 5 - after process & approval time
            }
         }

    } catch (Exception exc) {

        Utilities.logError("SystemUtils.getLotteryState() Error: " + exc.toString());
    
    } finally {

        try { rs2.close(); }
        catch (Exception ignore) {}

        try { pstmt1.close(); }
        catch (Exception ignore) {}

    }

    return lstate;
 }


  /**
  ***************************************************************************************
  *
  * This common method will display an error message for Mobile users.
  *
  * @param out the printwriter to write out the html
  *
  ***************************************************************************************
  **/

  public static void displayMobileError(String msg, String url, PrintWriter out)
  {

   //
   //  output a response
   //
   out.println(SystemUtils.HeadTitleMobile("Member Access Error"));
   // out.println(SystemUtils.BannerMobile());

   out.println("<div class=\"headertext\">ForeTees Error Report</div>");
   out.println("<div class=\"smheadertext\">" +msg+ "</div>");
   out.println("<div class=\"content\"><ul>");
   if (!url.equals( "" )) {
      out.println("<li><a href=\"/" + rev + "/" + url + "\">Return</a></li>");
   }
   out.println("<li><a href=\"/" +rev+ "/mobile/member_mobile_home.html\">Home</a></li></ul>");
   out.println("</body></html>");
   out.flush();
  }


  /**
  ***************************************************************************************
  *
  * This method creates a text csv file in the clubs root directory containing the current days tee sheet
  *
  * @param club the club we are running this for
  * @param con the existing database connection object for this club
  *
  ***************************************************************************************
  **/

  public static void dumpTeeSheet(String club, Connection con) {


    PrintWriter fout = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {

        fout = new PrintWriter(new FileWriter("//usr//local//tomcat//webapps//" + club + "//teesheet.csv", false));

        pstmt = con.prepareStatement (
           "SELECT * " +
           "FROM teecurr2 " +
           "WHERE date = DATE_FORMAT(now(), '%Y%m%d') AND player1 <> '' " +
           "ORDER BY time");

        pstmt.clearParameters();
        rs = pstmt.executeQuery();

        while (rs.next()) {

            fout.print("\"" + rs.getInt("yy") + "-" + rs.getInt("mm") + "-" + rs.getInt("dd") + " " + rs.getInt("hr") + ":" + Utilities.ensureDoubleDigit(rs.getInt("min")) + ":00\",");
            fout.print("\"" + rs.getString("event") + "\",");
            fout.print("\"" + rs.getString("player1") + "\",");
            fout.print("\"" + rs.getString("player2") + "\",");
            fout.print("\"" + rs.getString("player3") + "\",");
            fout.print("\"" + rs.getString("player4") + "\",");
            fout.print("\"" + rs.getString("player5") + "\",");
            fout.print("\"" + rs.getString("p1cw") + "\",");
            fout.print("\"" + rs.getString("p2cw") + "\",");
            fout.print("\"" + rs.getString("p3cw") + "\",");
            fout.print("\"" + rs.getString("p4cw") + "\",");
            fout.print("\"" + rs.getString("p5cw") + "\",");
            fout.print("\"" + rs.getString("mNum1") + "\",");
            fout.print("\"" + rs.getString("mNum2") + "\",");
            fout.print("\"" + rs.getString("mNum3") + "\",");
            fout.print("\"" + rs.getString("mNum4") + "\",");
            fout.print("\"" + rs.getString("mNum5") + "\",");
            fout.print("\"" + rs.getInt("p91") + "\",");
            fout.print("\"" + rs.getInt("p92") + "\",");
            fout.print("\"" + rs.getInt("p93") + "\",");
            fout.print("\"" + rs.getInt("p94") + "\",");
            fout.print("\"" + rs.getInt("p95") + "\",");
            fout.print("\"" + rs.getString("notes").replace("\"", "'") + "\","); // need to esacpe double-quotes and ???
            fout.print("\"" + rs.getInt("hideNotes") + "\",");
            fout.println("\"" + rs.getString("orig_by") + "\"");
        }

    } catch (Exception exc) {

        Utilities.logError("SystemUtils.dumpTeeSheet(): club=" + club + ", err=" + exc.getMessage());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

        try { fout.close(); }
        catch (Exception ignore) {}
    }

    return;
  }


  /**
  ***************************************************************************************
  *
  * This method will output the old tee sheets for the CDGA interface.
  *
  * @param club the club we are running this for
  * @param con the existing database connection object for this club
  * @param out pass null in to the PrintWriter to trigger output to file instead of browser
  *
  ***************************************************************************************
  **/

 public static void dumpCDGA_sheets(String club, Connection con, PrintWriter out) {


    PreparedStatement pstmt = null;
    ResultSet rs = null;


    int days_to_go_back = 3;
    String siteID = "";
    String courseID = "";
    String filename = "";
    
    boolean output_to_file = false;

    try {
        pstmt = con.prepareStatement (
           "SELECT club_num " +
           "FROM hdcp_club_num");

        pstmt.clearParameters();
        rs = pstmt.executeQuery();

        if (rs.next()) siteID = rs.getString(1);

    } catch (Exception exc) {

        Utilities.logError("dumpCDGA_sheets(): Error looking up Site ID. Error=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}
    }



    /*
        The Site ID's for the clubs are as follows:
        (we are storing these in hdcp_club_num table.club_num - but need to create a UI to edit/view the value - for now we manually put it in db)

        --Clubs
        --1.	Briarwood CC - IL       siteid 2171
        --2.	Crystal Lake CC - IL    siteid 1900
        --3.	Green Acres CC - IL     siteid 1940
        --4.	Inverness GC - IL       siteid 2174
        --5.	Sunset Ridge CC - IL    siteid 2113
        --6.	Skokie CC - IL         	siteid 2092
        --7.	Point 'o Woods - MI    	siteid 2299 // not currently a customer
   */

    if (club.equals("briarwoodcountryclub")) {

        siteID = "2171";

    } else if (club.equals("greenacrescountryclub")) {

        siteID = "1940";

    } else if (club.equals("invernessgc")) {

        siteID = "2174";

    } else if (club.equals("sunsetridge")) {

        siteID = "2113";

    } else if (club.equals("skokie")) {

        siteID = "2092";

    } else if (club.equals("clcountryclub")) {

        siteID = "1900";

    } else if (club.equals("napervillecc")) {

        siteID = "2016";

    } else if (club.equals("olympiafieldscc")) {

        siteID = "1198";
    }

    if (!siteID.equals("")) {

        if (out == null) {

            output_to_file = true;
            
            //
            //  Setup a FileWriter as the out object
            //

            // first see if we need to delete an existing file

            filename = "//home//cdga//" + siteID + "-" + getDate(con) + ".csv";

            try {

                File f = new File(filename);
                if (f.isFile()) f.delete();

            } catch(Exception exc) {

                Utilities.logError("dumpCDGA_sheets(): Error trying to delete existing file. Error=" + exc.toString());

            }

            try {

                out = new PrintWriter(new FileWriter(filename, true));

            } catch(Exception exc) {

                Utilities.logError("dumpCDGA_sheets(): Error setting up new FileWriter. Error=" + exc.toString());

            }

        } // end if out is null

        boolean tee_times_found = false;

        try {

            String hdcpNum = "";

            pstmt = con.prepareStatement (
               "SELECT * " +
               "FROM teepast2 " +
               "WHERE date >= DATE_FORMAT(DATE_ADD(now(), INTERVAL -" + days_to_go_back + " DAY), '%Y%m%d') AND " +
               "!(player1 = '' AND player2 = '' AND player3 = '' AND player4 = '' AND player5 = '') " + // skip any empty tee times add by the pro via old sheets
               "ORDER BY date, time");

            pstmt.clearParameters();
            rs = pstmt.executeQuery();

            while (rs.next()) {

                // set the courseId
                if (club.equals("briarwoodcountryclub")) {

                    courseID = "315";

                } else if (club.equals("greenacrescountryclub")) {

                    courseID = "132";

                } else if (club.equals("invernessgc")) {

                    courseID = "316";

                } else if (club.equals("sunsetridge")) {

                    courseID = "278";

                } else if (club.equals("skokie")) {

                    courseID = "262";

                } else if (club.equals("clcountryclub")) {

                    courseID = "96";

                } else if (club.equals("napervillecc")) {

                    courseID = "194";

                } else if (club.equals("olympiafieldscc")) {

                    if (rs.getString("courseName").equals("North Course")) {

                        courseID = "40";

                    } else if (rs.getString("courseName").equals("South Course")) {

                        courseID = "426";

                    }
                
                } else {

                    // set to empty if we didn't match
                    courseID = "";
                }


                if (!rs.getString("player1").equals("")) {

                    hdcpNum = ((!rs.getString("username1").equals("")) ? Utilities.getHdcpNum(rs.getString("username1"), con) : "");
                    
                    if (!hdcpNum.equals("")) {
                        
                        printCDGArow(siteID, courseID, rs.getInt("teepast_id"), 1, rs.getString("player1"), rs.getString("p1cw"), rs.getString("mNum1"), rs.getInt("p91"), rs.getInt("show1"), hdcpNum, rs, out);
                        tee_times_found = true;
                    }

                }

                if (!rs.getString("player2").equals("")) {

                    hdcpNum = ((!rs.getString("username2").equals("")) ? Utilities.getHdcpNum(rs.getString("username2"), con) : "");
                    
                    if (!hdcpNum.equals("")) {
                    
                        printCDGArow(siteID, courseID, rs.getInt("teepast_id"), 2, rs.getString("player2"), rs.getString("p2cw"), rs.getString("mNum2"), rs.getInt("p92"), rs.getInt("show2"), hdcpNum, rs, out);
                        tee_times_found = true;
                    }

                }

                if (!rs.getString("player3").equals("")) {

                    hdcpNum = ((!rs.getString("username3").equals("")) ? Utilities.getHdcpNum(rs.getString("username3"), con) : "");
                    
                    if (!hdcpNum.equals("")) {
                        
                        printCDGArow(siteID, courseID, rs.getInt("teepast_id"), 3, rs.getString("player3"), rs.getString("p3cw"), rs.getString("mNum3"), rs.getInt("p93"), rs.getInt("show3"), hdcpNum, rs, out);
                        tee_times_found = true;
                    }

                }

                if (!rs.getString("player4").equals("")) {

                    hdcpNum = ((!rs.getString("username4").equals("")) ? Utilities.getHdcpNum(rs.getString("username4"), con) : "");
                    
                    if (!hdcpNum.equals("")) {
                        
                        printCDGArow(siteID, courseID, rs.getInt("teepast_id"), 4, rs.getString("player4"), rs.getString("p4cw"), rs.getString("mNum4"), rs.getInt("p94"), rs.getInt("show4"), hdcpNum, rs, out);
                        tee_times_found = true;
                    }

                }

                if (!rs.getString("player5").equals("")) {

                    hdcpNum = ((!rs.getString("username5").equals("")) ? Utilities.getHdcpNum(rs.getString("username5"), con) : "");
                    
                    if (!hdcpNum.equals("")) {
                        
                        printCDGArow(siteID, courseID, rs.getInt("teepast_id"), 5, rs.getString("player5"), rs.getString("p5cw"), rs.getString("mNum5"), rs.getInt("p95"), rs.getInt("show5"), hdcpNum, rs, out);
                        tee_times_found = true;
                    }

                }

            } // rs loop

        } catch (Exception exc) {

            Utilities.logError("dumpCDGA_sheets(): Error=" + exc.toString());

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}
        }

        // if we were set to output to file but did not output any results
        if (output_to_file) {

            out.close();

            if (!tee_times_found) {

                Utilities.logError("dumpCDGA_sheets(): " + club + " - no tee times dumped - delete empty file");

                try {

                    File f = new File(filename);
                    if (f.isFile()) f.delete();

                } catch(Exception exc) {

                    Utilities.logError("dumpCDGA_sheets(): Error trying to delete empty file. Error=" + exc.toString());

                }

            } else {
                
                Utilities.logError("dumpCDGA_sheets(): " + club + " - tee times dumped");
                
            }

        }

    } // end if siteID poopulated

    return;

 }


 private static void printCDGArow(String siteID, String courseID, int teepast_id, int player_pos, String player, String tmode, String mNum, int p9, int show, String hdcpNum, ResultSet rs, PrintWriter out) {


    try {

        out.print("\"" + siteID + "\",");
        out.print("\"" + teepast_id + "\",");
        out.print("\"" + player_pos + "\",");
        out.print("\"" + rs.getInt("yy") + "-" + rs.getInt("mm") + "-" + rs.getInt("dd") + " " + rs.getInt("hr") + ":" + Utilities.ensureDoubleDigit(rs.getInt("min")) + ":00\",");
        out.print("\"" + courseID + "\",");
        out.print("\"" + rs.getString("courseName") + "\",");
        out.print("\"" + rs.getString("event") + "\",");
        out.print("\"" + player + "\",");
        out.print("\"" + hdcpNum + "\",");
        out.print("\"" + tmode + "\",");
        out.print("\"" + mNum + "\",");
        out.print("\"" + p9 + "\",");
        out.print("\"" + show + "\",");
        out.print("\"\","); // " + rs.getString("notes") + " need to esacpe double-quotes and ???
        out.print("\"1\","); // " + rs.getInt("hideNotes") + "
        out.print("\"" + rs.getString("orig_by") + "\"");
        out.print("\r\n");

    } catch (Exception exc) {

        Utilities.logError("dumpCDGA_sheets(): Failed to output row. Error=" + exc.toString());

    }

 }

} // end of utility class
