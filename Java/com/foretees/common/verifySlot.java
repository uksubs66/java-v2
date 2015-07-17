/***************************************************************************************
 *   verifySlot:  This servlet will provide some common tee time request processing methods.
 *
 *       called by:  Proshop_slot
 *                   Proshop_dsheet
 *
 *
 *   created:  1/12/2004   Bob P.
 *
 *
 *   last updated:
 *
 *      ****NOTE***  add all customs to verifyCustom (as of 6/09/2006)
 *
 *      7/21/10  Rolling Hills CC - CO (rhillscc) - added support for 'Ladies' msubtype access to Tuesday and Thursday times 14 days in advance (case 1866).
 *      7/19/10  Meadow Club (meadowclub) - Added custom to checkMemRests to skip displaying the rest prompt if Proshop user and 'Member Tee Time Restriction' rest (case 1802).
 *      7/08/10  Added override method for checkMemNotice to allow ranges of times to be checked.  Traditional method call will call the override
 *               method and pass the same time value for both stime and etime.
 *      6/29/10  Cherry Hills CC (cherryhills) - Update custom to apply to Mondays and 4th of July observance day instead of actual 4th
 *      6/15/10  North Hills - do not force user to specify guest name for the "No Fivesome" guest type (case 1858).
 *      6/10/10  Brooklawn CC (brooklawn) - Allow access to tee times on 7/17/09 for Male members from 7:00 to 14:00 for tee time event
 *      6/07/10  Added code to guest tracking verification to allow for the guest "TBA" option
 *      5/11/10  Royal Oaks WA - allow 9-Hole Women to book more days in adv for Friday mornings. (case 1843).
 *      5/07/10  Updated addMTeeTime to apply the current user's username for all orig values if member, or blank if proshop user
 *      5/07/10  Updated shiftUp to shift orig1-5 values
 *      5/07/10  Added orig1-5 to checkInUse and checkInUseN so they get populated
 *      5/07/10  Philly Cricket (philcricket) - Change memberhip type round restriction to ignore rounds played on the 'St Martins' course
 *      4/19/10  Pinehurst CC (pinehurstcountryclub) - minor tweak to Ladies Days times so all women can access (case 1766).
 *      4/17/10  Updated checkMaxGuests & checkGuestQuota to work with unlimited guests & use new gtypes tabels
 *      4/15/10  Woodway CC (woodway) - Updated checkWoodway custom with new tee time range for 2010 (case 1053).
 *      4/13/10  Updated checkEventRests to utilize event id instead of the event name
 *      4/13/10  Changes to checkMaxGuests/checkGuestQuote/checkMemNums/countGuests so guest types can be unlimited
 *      3/30/10  Palo Alto Hills - checkMemRests - do not restrict if member subtype is 18 Holer and restriction is !8 Holes Ladies (case 1785).
 *      3/30/10  Long Cove Club (longcove) - Re-enable custom for April to allow members to originate only 1 time per day 4/14-4/18.
 *      3/25/10  Interlachen - ignore guest restrictions on Fridays from 10:00 to 11:30 - custom will handle it (case 1791).
 *      3/25/10  Woodway CC (woodway) - Updated checkWoodway custom with new dates for 2010 (case 1053).
 *      2/15/10  Update to shiftUp method to shift guest_id values.
 *      2/12/10  Updates to parseNames and parseGuests to accomodate guest_ids and guests that are a associated with the guest tracking feature
 *      2/04/10  Philly Cricket - change custom in checkMaxRounds to check mship types instead of member types.
 *      2/02/10  Pinehurst CC - Proprietary members can only access the Plfuger 9 course on the day of at 6:00 AM (case 1766).
 *      1/26/10  Long Cove - checkinusemn - bump the max number of tee times we scan looking for available times (case 1783).
 *      1/05/10  Cherry Hills - correct the guest type when checking to force guest name (now check for 'X TBA Lott') - case 1675.
 *     12/09/09  When looking for events only check those that are active.
 *     12/04/09  Seacliff CC - add women ladies-day - more days in adv for Tues. (case 1750).
 *     12/01/09  Use the adjustTime method in Utilities.
 *     11/08/09  Update checkInUseN to only look for times that are not in use and if alternate time found,
 *               allocate it at that time so others can be checked if that time is no longer available.
 *      9/28/09  Hudson National (hudsonnatl) - Force "per member" guest restrictions to be enforced on member associations instead of
 *               the ratio of guests:members (e.g. each member allowed ONE guest each) (case 1728).
 *      9/04/09  Cleaned up code (currently on line 11595 working my way up)
 *      9/02/09  Elmcrest CC (elmcrestcc) - When a name is manually typed in, if their default MoT is pro-only, apply their default
 *      8/31/09  Changed mship proceessing to grab mships from mship5 instead of club5
 *      8/07/09  Brooklawn CC (brooklawn) - Change round limit processing to ignore past rounds not played under the current membership type
 *      7/21/09  Portland GC - when checking for an available tee time, check if it is a walk-up time.
 *      7/01/09  Wee Burn (weeburn) - Updated checkWeeburn() method with new time values
 *      6/25/09  Medinah - only force members to enter one name (word) after the guest type (case 1695).
 *      6/15/09  Brooklawn - allow Male members to book tee times in advance on 7/18/09.
 *      6/02/09  Royal Oaks WA - allow women to book more days in adv for Tues mornings. (case 1684).
 *      5/26/09  Added custom_disp fields and p9 fields to shiftUp method
 *      5/05/09  Medinah CC - remove days in advance custom (case 1673).
 *      4/27/09  Merion - allow access to Memorial Day and Labor Day 365 days in advance.
 *      4/22/09  Brooklawn - add women 9 holers - more days in adv for Wed. (case 1637).
 *      4/21/09  Chartwell GCC - Allow booking of consecutive tee times for shotgun event tee times (case 1556).
 *      3/11/09  Update temp custom for Long Cove for thier Heritage Classic event (Case# 1038).
 *      2/05/09  Change checkMerionGres to allow a max of 5 guest/weekday times instead of 4.
 *     12/31/08  Green Hills CC - add women ladies-day - more days in adv for Thurs morning (case 1574).
 *     12/08/08  Modified checkRestSuspend to take course name into consideration
 *     11/20/08  Update getUsers to rebuild the players' names if members to make sure we get the proper titlecase (case 1576).
 *     11/18/08  Additional changes to restriction suspension processing: checkInUseN/checkInUseMn
 *     11/13/08  Fix for checkMemNotice
 *     11/04/08  checkInUseN & checkInUseMn - check if all player slots are empty since edit tee sheet will allow pro to move
 *               players and leave player1 empty.
 *      9/31/08  Added processing for checking member/guest restriction suspensions during verification process.
 *      9/08/08  Added check for displaying memNotices on the teesheet to checkMemNotice()
 *      9/08/08  Sharon Heights - add women 18 holers - more days in adv for Tues. (case 1533).
 *      6/26/08  Save the teecurr_id in parmSlot when checking a tee time.
 *      6/17/08  Updates to checkProOnlyMOT
 *      6/16/08  checkProOnlyMOT added to verify that Pro Only modes of trans are not selected by members
 *      6/03/08  St. Clair - only check the Championship course in checkMaxRounds (case 1437).
 *      6/02/08  Brooklawn - add women 18 holers - more days in adv for Tues. (case 14??).
 *      5/16/08  Move checInUseMN from verifyCustom to here since it is not a custom any longer.
 *               Also, checkInUseM is no longer used.
 *      4/24/08  Add tflags to addMteeTime for multiple tee time request (case 1357).
 *      4/23/08  Add checkTFlag method to check for member tags based on mship and member to be appended to player's
 *               name in tee time and displayed on proshop tee sheet (case 1357).
 *      4/18/08  Claremont CC - change days in adv parms for Adult Females on Tues (case 1361).
 *      4/16/08  MN Valley - checkGstName - do not check for name if guest type is 5th Player Block (case 1432).
 *      4/10/08  Updated checkHudson method for 2-some times per Case# 1447
 *      3/27/08  The CC - Enforce a default guest type if player name not member name for tcclub (case #1370)
 *      3/24/08  Change checkWoodway method to reflect new dates for 2-some times.
 *      3/14/08  Added checkNewReq method to check if tee time is a new req or an existing tee time (has players).
 *      3/03/08  Update temp custom for Long Cove for thier Heritage Classic event (Case# 1038)
 *      2/27/08  In checkInUse and checkInUseM get the event and blocker data from teecurr for verification in _slot.
 *      2/18/08  Copy custom_disp fields from teecurr to slotParms when allocating a tee time (for customs).
 *      2/18/08  Change the query in checkInUseN to include both front and back times in case there are double tees.
 *               Also, do not include event times, restricted times or blocked times in the count of max times to check (case 1396).
 *      1/24/08  Add custom_int to addMteeTime for Oakmont custom.
 *     10/24/07  Add new checkMaxOrigBy for enforcing a max number of rounds that can be originated by a member
 *     10/14/07  checkInUseN - check for member restrictions when checking alternative tee tims.
 *      8/28/07  Add checkInUseN for new feature to get next available tee time if requested time is busy (case #1241).
 *      8/28/07  Updated checkGstName for Mirasol - one name after guest type is ok (case #1226).
 *      8/20/07  Updated checkMemNotice to make add configurable option for all clubs to display on proshop side (per notice basis)
 *      7/19/07  getUsers - Do not include members that have been excluded (new billable flag in member2b).
 *      6/29/07  Catamount Ranch - change max number of advance tee times from 5 to 10 (case #1205).
 *      6/26/07  Brantofrd - do not force names for Lottery TBA guest types.
 *      6/26/07  When pulling a handicap from member2b get it from the g_hancap field instead of c_hancap
 *      6/18/07  Colorado GC - custom to allow Juniors in tee time when restricted if accompanined by an adult (case #1144).
 *      6/18/07  Catamount Ranch - change max number of advance tee times back to 5, except for Founder members - they are unlimited (case #1124).
 *      6/18/07  Sonnenalp - use checkRockies to check for more than 12 advance times per member (case #1089).
 *      5/31/07  Westchester 2-some times - skip 6/07/2007.
 *      5/24/07  checkRockies - change max allowed times for Catamount Ranch (was 5, now 14).
 *      5/08/07  getDaysInAdv - add 'viewdays' to the parm block - get # of days mships can view tee sheets.
 *      5/07/07  checkDaysAdv - adjust the # of days between today and day of tee time based on time zone in case
 *                              we are near midnight.
 *      4/06/07  getUsers - Do not include members that are inactive (new inact flag in member2b).
 *      3/28/07  Philadelphia Cricket Club - custom to check member types for max rounds per year (case 1032).
 *      3/28/07  Oakmont CC - change Wed & Fri guest times to allow 2 guests and 2 members.
 *      3/25/07  Add temp custom for Long Cove for thier Heritage Classic event (Case# 00001038)
 *      3/14/07  Change checkWoodway method to reflect new dates for 2-some times.
 *      2/27/07  Merion - add checkMemNotice method to check for Member Notices.
 *      2/20/07  Merion - add checkMerionWE and checkMerionWEm methods to check advance w/e tee times.
 *      2/08/07  Piedmont - change the dates for Daylight Saving Time (expanded starting in 2007).
 *      1/11/07  El Niguel - change days in adv for Adult Females on Tues.
 *     12/28/06  Oakmont CC - change some of the Wed & Fri guest times for 2007.
 *     11/13/06  Disabled Santa Ana Womens retriction - per Larry
 *     11/08/06  Columbia-Edgewater - custom days in adv for Spouse member types.
 *      9/06/06  Piedmont - change the 2-some times (now first 6 times on w/e's).
 *      6/28/06  Cherry Hills - change custom to check holidays before days of the week. Also, check 7/04 not 7/03.
 *      6/22/06  Scioto - custom days in adv for all member types.
 *      6/14/06  Scioto - custom days in adv for Spouse member types.
 *      5/25/06  Oakland Hills - when counting advance times, check all days and times that were adv times.
 *      5/17/06  Hazeltine - checkSponsGrp - remove time constraints and change end date for Jr Fridays.
 *      5/11/06  Oakland Hills - change weekday days in advance if before 10 AM.
 *      5/04/06  Oakland Hills - change custom restrictions to allow for guest time days in advance rules.
 *      4/27/06  Added showX properties (for 'check in') and precheckin handlers to addMteeTime (called by Proshop_slotm)
 *      4/24/06  Add checkCastleDependents method to check for Juniors w/o and adult.
 *      4/20/06  Add checkWeeburn method for custom 2-some times.
 *      4/18/06  Cherry Hills - do not force guest name for guest type of 'X TBA lottery use'.
 *      4/17/06  Add checkApawamis method for custom 2-some times.
 *      4/17/06  Add checkHazGrps method for custom singles and 2-some times.
 *      4/05/06  Add checkNewCanaan method for custom 2-some times.
 *      3/15/06  CC of the Rockies - skip the 'days in advance' tests.  Also, add method
 *               checkRockies to check for more than 5 advance times per member.
 *      3/15/06  Bearpath - add a method to check for guests on certain times and days.
 *      3/14/06  Oakland Hills - add a method to check if a tee time is allowed (checkOaklandAdvTime).
 *      3/14/06  Oakland Hills - add a custom restriction for Dependents (checkOaklandKids).
 *      3/07/06  Change the errorlog method to save msg in db table.
 *      3/01/06  Change the checkInUse methods as follows:
 *                    - remove the synchronized tag to hopefully prevent the tee time hang problem
 *                    - change the order of the db calls from query - update to update - query so
 *                      we now set the tee time as busy IF it was not already busy, then we
 *                      get the info we need from the tee time.  This replaces the functionality
 *                      of the synchronized method.
 *      2/27/06  Add checkMerionSchedm (multiple) method for custom member restrictions.
 *      2/27/06  Add checkMerionSched method for custom member restrictions.
 *      2/24/06  Add checkMerionGres method for custom guest restrictions.
 *      2/24/06  Merion - checkDaysAdv - do not check if weekday.
 *      2/23/06  Add checkMerionG method for custom guest restrictions.
 *     12/21/05  Add checkSkaneateles method for custom member (dependent) restrictions.
 *     12/05/05  Add checkRitz method for custom member restrictions.
 *     11/08/05  Allows throw 'Exception' when throwing a new exception back to caller.  The same type of
 *               exception must be caught by caller.
 *     11/04/05  Add checkCherryHills method for custom member restrictions.
 *     10/07/05  Add checkStanwich method for custom tee sheets & 2-some times.
 *      6/24/05  North Shore CC - checkNSGuestTimes - times that require at least 2 guests per group.
 *      6/23/05  Cordillera - remove check for days in advance - ok for home member to add non-home members.
 *      6/08/05  Cordillera - check member number and course to determine days in advance.
 *      6/02/05  Green Bay - add custom check for max of 9 guests per hour (checkGBguests).
 *      5/13/05  Add checkEventRests to check member restrictions for an event.
 *      5/05/05  Reject request if a member has an empty mship, mtype, or username (getUsers).
 *      4/26/05  Custom for Santa Ana Women - Increase days in advance for Tues and Fri.
 *      4/13/05  Add checkPortage method for Portage custom for Associate memberships (6 rounds per year).
 *      4/11/05  Add checkPineDependents method for Pine Hills custom - dependents w/o adult.
 *      4/11/05  Add checkWestDependents method for Westchester custom - dependents w/o adult.
 *      4/11/05  Add checkWestPlayers method for Westchester custom - 2-some times.
 *      4/04/05  Add checkHudson method for custom tee sheets & 2-some times.
 *      3/24/05  Add checkWestchester and checkWoodway methods for custom tee sheets & 2-some times.
 *      3/03/05  Ver 5 - changed some expressions with show_ vars to support new precheckin feature
 *      2/17/05  Ver 5 - add support for option to force members to specify a guest's name.
 *      2/16/05  Save the name of the guest restriction for error message (checkMaxGuests).
 *      1/08/05  Rogue Valley & Northridge - if multiple tee times on different courses, do not reject.
 *               They each have a practice course so we don't want to refuse times on both courses.
 *      1/24/05  RDP  Ver 5 - change club2 to club5.
 *      1/21/05  RDP  Add checkInUseM to check multiple tee times, if busy.
 *      1/10/05  Oakmont CC - change some of the Wed guest times for April, Sept & Oct.
 *     11/30/04  RDP  Add checkBelleHaven for special membership processing for Belle Haven CC.
 *     11/16/04  RDP  Add support for multiple rounds per day for members.
 *      9/22/04  RDP  Add oakmontGuests for special guest processing for Oakmont.
 *      9/21/04  RDP  Add checks for 'checked-in' (show) to checkMaxRounds.
 *      9/21/04  RDP  Add getLottery to get lottery info.
 *      9/08/04  RDP  Add checks in parseGuests for guests already approved by pro.
 *      8/26/04  RDP  Add checks in checkMaxGuests for guests already approved by pro.
 *      5/13/04  RDP  Add custom checks for guest names for Hazeltine Natl. (checkGuestNames).
 *      4/27/04  RDP  Add custom checks in checkInUse for Double Tees at Hazeltine Natl.
 *      4/25/04  RDP  Add custom checks in checkMemRests for Junior Restrictions at Hazeltine Natl.
 *      4/25/04  RDP  Add checkNational for max number of tee times for National members at Hazeltine Natl.
 *      4/24/04  RDP  Add custom checks in checkDaysAdv for Women at Hazeltine Natl.
 *      4/21/04  RDP  Add checkSponsGrp to verify Sponsored Group Requests for Hazeltine Natl.
 *
 ***************************************************************************************
 */


package com.foretees.common;

//import java.io.*;
import java.util.*;
import java.sql.*;
//import javax.servlet.*;
//import javax.servlet.http.*;
//import javax.mail.internet.*;
//import javax.mail.*;
//import javax.activation.*;


public class verifySlot {

   //private static String rev = ProcessConstants.REV;

   //
   //  Holidays for Hazeltine, Oakmont and other custom codes that may require them
   //
   //   Must change them in ProcessConstants...
   //     also, refer to SystemUtils !!!!!!!!!
   //
   private static long Hdate1 = ProcessConstants.memDay;     // Memorial Day
   private static long Hdate2 = ProcessConstants.july4;      // 3rd of July - Monday
   private static long Hdate2b = ProcessConstants.july4b;    // 4th of July
   private static long Hdate3 = ProcessConstants.laborDay;   // Labor Day
   private static long Hdate7 = ProcessConstants.tgDay;      // Thanksgiving Day
   private static long Hdate8 = ProcessConstants.colDay;     // Columbus Day
   private static long Hdate9 = ProcessConstants.colDayObsrvd;     // Columbus Day Observed

   private static long Hdate4 = ProcessConstants.Hdate4;     // October 1st
   private static long Hdate5 = ProcessConstants.Hdate5;     // Junior Fridays Start (start on Thurs.)
   private static long Hdate6 = ProcessConstants.Hdate6;     // Junior Fridays End  (end on Sat.)



/**
 //************************************************************************
 //
 //  checkInUse - check if slot is in use and if not, set it
 //
 //************************************************************************
 **/

 public static int checkInUse(long date, int time, int fb, String course, String user, parmSlot slotParms, Connection con)
         throws Exception {


   PreparedStatement pstmt = null;
   //Statement stmt = null;
   ResultSet rs = null;

   int in_use = 1;                // init to busy in case tee time not found
   int found = 0;
   int count = 0;

   //
   //  Verify the input parms
   //
   if (date > 0 && time > 0 && course != null && user != null && !user.equals( "" )) {

      try {

         //
         //   Set the tee time as busy, IF it is not already (count will be zero if its already busy)
         //
         pstmt = con.prepareStatement (
            "UPDATE teecurr2 SET in_use = 1, in_use_by = ? WHERE date = ? AND time = ? AND in_use = 0 AND fb = ? AND courseName = ?");

         pstmt.clearParameters();          // clear the parms
         pstmt.setString(1, user);         // put the parm in pstmt
         pstmt.setLong(2, date);
         pstmt.setInt(3, time);
         pstmt.setInt(4, fb);
         pstmt.setString(5, course);

         count = pstmt.executeUpdate();            // execute the prepared stmt

         pstmt.close();

         //
         //  If the above was successful, then we now own the tee time
         //
         if (count > 0) {

            pstmt = con.prepareStatement (
               "SELECT * " +
               "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

            pstmt.clearParameters();        // clear the parms
            pstmt.setLong(1, date);         // put the parm in pstmt
            pstmt.setInt(2, time);
            pstmt.setInt(3, fb);
            pstmt.setString(4, course);
            rs = pstmt.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               in_use = 0;                    // set return code as successful ****

               slotParms.teecurr_id = rs.getInt( "teecurr_id" );
               slotParms.event = rs.getString( "event" );
               slotParms.player1 = rs.getString( "player1" );
               slotParms.player2 = rs.getString( "player2" );
               slotParms.player3 = rs.getString( "player3" );
               slotParms.player4 = rs.getString( "player4" );
               slotParms.user1 = rs.getString( "username1" );
               slotParms.user2 = rs.getString( "username2" );
               slotParms.user3 = rs.getString( "username3" );
               slotParms.user4 = rs.getString( "username4" );
               slotParms.p1cw = rs.getString( "p1cw" );
               slotParms.p2cw = rs.getString( "p2cw" );
               slotParms.p3cw = rs.getString( "p3cw" );
               slotParms.p4cw = rs.getString( "p4cw" );
               slotParms.last_user = rs.getString( "in_use_by" );
               slotParms.hndcp1 = rs.getFloat( "hndcp1" );
               slotParms.hndcp2 = rs.getFloat( "hndcp2" );
               slotParms.hndcp3 = rs.getFloat( "hndcp3" );
               slotParms.hndcp4 = rs.getFloat( "hndcp4" );
               slotParms.show1 = rs.getShort( "show1" );
               slotParms.show2 = rs.getShort( "show2" );
               slotParms.show3 = rs.getShort( "show3" );
               slotParms.show4 = rs.getShort( "show4" );
               slotParms.player5 = rs.getString( "player5" );
               slotParms.user5 = rs.getString( "username5" );
               slotParms.p5cw = rs.getString( "p5cw" );
               slotParms.hndcp5 = rs.getFloat( "hndcp5" );
               slotParms.show5 = rs.getShort( "show5" );
               slotParms.notes = rs.getString( "notes" );
               slotParms.hide = rs.getInt( "hideNotes" );
               slotParms.blocker = rs.getString( "blocker" );
               slotParms.rest5 = rs.getString( "rest5" );
               slotParms.mNum1 = rs.getString( "mNum1" );
               slotParms.mNum2 = rs.getString( "mNum2" );
               slotParms.mNum3 = rs.getString( "mNum3" );
               slotParms.mNum4 = rs.getString( "mNum4" );
               slotParms.mNum5 = rs.getString( "mNum5" );
               slotParms.userg1 = rs.getString( "userg1" );
               slotParms.userg2 = rs.getString( "userg2" );
               slotParms.userg3 = rs.getString( "userg3" );
               slotParms.userg4 = rs.getString( "userg4" );
               slotParms.userg5 = rs.getString( "userg5" );
               slotParms.guest_id1 = rs.getInt( "guest_id1" );
               slotParms.guest_id2 = rs.getInt( "guest_id2" );
               slotParms.guest_id3 = rs.getInt( "guest_id3" );
               slotParms.guest_id4 = rs.getInt( "guest_id4" );
               slotParms.guest_id5 = rs.getInt( "guest_id5" );
               slotParms.orig_by = rs.getString( "orig_by" );
               slotParms.conf = rs.getString( "conf" );
               slotParms.p91 = rs.getInt( "p91" );
               slotParms.p92 = rs.getInt( "p92" );
               slotParms.p93 = rs.getInt( "p93" );
               slotParms.p94 = rs.getInt( "p94" );
               slotParms.p95 = rs.getInt( "p95" );
               slotParms.pos1 = rs.getShort( "pos1" );
               slotParms.pos2 = rs.getShort( "pos2" );
               slotParms.pos3 = rs.getShort( "pos3" );
               slotParms.pos4 = rs.getShort( "pos4" );
               slotParms.pos5 = rs.getShort( "pos5" );
               slotParms.custom_disp1 = rs.getString( "custom_disp1" );
               slotParms.custom_disp2 = rs.getString( "custom_disp2" );
               slotParms.custom_disp3 = rs.getString( "custom_disp3" );
               slotParms.custom_disp4 = rs.getString( "custom_disp4" );
               slotParms.custom_disp5 = rs.getString( "custom_disp5" );
               slotParms.tflag1 = rs.getString( "tflag1" );
               slotParms.tflag2 = rs.getString( "tflag2" );
               slotParms.tflag3 = rs.getString( "tflag3" );
               slotParms.tflag4 = rs.getString( "tflag4" );
               slotParms.tflag5 = rs.getString( "tflag5" );
               slotParms.orig1 = rs.getString( "orig1" );
               slotParms.orig2 = rs.getString( "orig2" );
               slotParms.orig3 = rs.getString( "orig3" );
               slotParms.orig4 = rs.getString( "orig4" );
               slotParms.orig5 = rs.getString( "orig5" );
            }

            pstmt.close();


            if (in_use == 0) {       // if ok

               //
               //  Add custom processing for Hazeltine National
               //
               //    If this is a double tee, then we must also lock out its associated time on the opposite tee.
               //
               if (slotParms.club.equals( "hazeltine" )) {

                  // if weekend or holiday
                  if (slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday") ||
                      date == Hdate1 || date == Hdate2 || date == Hdate3) {

                     found = 0;     // default to not double tee

                     //
                     // Related Times:   8:00 B <--> 10:10 F
                     //                  8:09 B <--> 10:19 F
                     //                  8:18 B <--> 10:28 F
                     //                  8:27 B <--> 10:37 F
                     //                  8:36 B <--> 10:46 F
                     //                  8:45 B <--> 10:55 F
                     //
                     if (time == 800 && fb == 1) {    // if 8:00 AM Back Tee
                        found = 1;                    // found one
                        time = 1010;                  // associated time
                        fb = 0;                       // asscoiated tee
                     } else {
                       if (time == 809 && fb == 1) {    // if 8:09 AM Back Tee
                          found = 1;
                          time = 1019;
                          fb = 0;
                       } else {
                         if (time == 818 && fb == 1) {    // if 8:18 AM Back Tee
                            found = 1;
                            time = 1028;
                            fb = 0;
                         } else {
                           if (time == 827 && fb == 1) {    // if 8:27 AM Back Tee
                              found = 1;
                              time = 1037;
                              fb = 0;
                           } else {
                             if (time == 836 && fb == 1) {    // if 8:36 AM Back Tee
                                found = 1;
                                time = 1046;
                                fb = 0;
                             } else {
                               if (time == 845 && fb == 1) {    // if 8:45 AM Back Tee
                                  found = 1;
                                  time = 1055;
                                  fb = 0;
                               } else {

                                 if (time == 1010 && fb == 0) {    // if 10:10 AM Front Tee
                                    found = 1;
                                    time = 800;
                                    fb = 1;
                                 } else {
                                   if (time == 1019 && fb == 0) {    // if 10:19 AM Front Tee
                                      found = 1;
                                      time = 809;
                                      fb = 1;
                                   } else {
                                     if (time == 1028 && fb == 0) {    // if 10:28 AM Front Tee
                                        found = 1;
                                        time = 818;
                                        fb = 1;
                                     } else {
                                       if (time == 1037 && fb == 0) {    // if 10:37 AM Front Tee
                                          found = 1;
                                          time = 827;
                                          fb = 1;
                                       } else {
                                         if (time == 1046 && fb == 0) {    // if 10:46 AM Front Tee
                                            found = 1;
                                            time = 836;
                                            fb = 1;
                                         } else {
                                           if (time == 1055 && fb == 0) {    // if 10:55 AM Front Tee
                                              found = 1;
                                              time = 845;
                                              fb = 1;
                                           }
                                         }
                                       }
                                     }
                                   }
                                 }
                               }
                             }
                           }
                         }
                       }
                     }
                     //
                     //  if we hit on a time, then we must lock out the associated time (should be free if assoc is)
                     //
                     if (found == 1) {

                        pstmt = con.prepareStatement (
                           "UPDATE teecurr2 SET in_use = 1, in_use_by = ? WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                        pstmt.clearParameters();          // clear the parms
                        pstmt.setString(1, user);         // put the parm in pstmt
                        pstmt.setLong(2, date);
                        pstmt.setInt(3, time);
                        pstmt.setInt(4, fb);
                        pstmt.setString(5, course);
                        pstmt.executeUpdate();            // execute the prepared stmt

                        pstmt.close();
                     }
                  }
               } // end if hazeltine
            }
         }

      }
      catch (SQLException e) {

         throw new Exception("Error checking in-use - verifySlot.checkInUse - SQL Exception: " + e.getMessage());
      }
      catch (Exception e) {

         throw new Exception("Error checking in-use - verifySlot.checkInUse - Exception: " + e.getMessage());
      }
   }

   return(in_use);
 }


/**
 //************************************************************************
 //
 //  checkInUseN - check if slot is in use and if not, set it
 //
 //             If busy, then locate the next available and return that.
 //
 //             NOTE:  This MUST be a new tee time request!!
 //
 //               Potential Problem:  We don't check for lotteries, or their state!!!
 //
 //************************************************************************
 **/

 public static int checkInUseN(long date, int time, int fb, String course, String user, parmSlot slotParms, Connection con)
         throws Exception {


   PreparedStatement pstmt = null;
   //Statement stmt = null;
   ResultSet rs = null;

   int max = 30;                  // max number of tee times to check (could change in future)

   int i = 0;
   //int i2 = 0;
   int in_use = 1;                // init to busy in case tee time not found
   int found = 0;
   int count = 0;

   int [] timeA = new int [max];
   //int [] in_useA = new int [max];
   int [] fbA = new int [max];

   String [] rests = new String [max];
   String [] day_name = new String [max];

   String sfb = "Front";

   if (fb == 1) {
      sfb = "Back";
   }


   //
   //  parm block to hold the member restrictions for this date and member
   //
   parmRest parmr = new parmRest();          // allocate a parm block

   //
   //  Get all restrictions for this day and user (for use when checking each tee time below)
   //
   parmr.user = user;
   parmr.mship = slotParms.mship;
   parmr.mtype = slotParms.mtype;
   parmr.date = date;
   parmr.day = slotParms.day;
   parmr.course = course;

   if (!user.startsWith( "proshop" )) {   // if member

      getRests.getAll(con, parmr);       // get the restrictions for this member, day and course
   }


   for (i=0; i<max; i++) {         // init player fields

      rests[i] = "";
      day_name[i] = "";
     // in_useA[i] = 1;            // init to busy in case tee time not found
   }

   i = 0;        // reset index

   //
   //  Verify the input parms
   //
   if (date > 0 && time > 0 && course != null && user != null && !user.equals( "" )) {

      try {

         //
         //   Set the tee time as busy, IF it is not already (count will be zero if its already busy)
         //
         pstmt = con.prepareStatement (
            "UPDATE teecurr2 SET in_use = 1, in_use_by = ? WHERE date = ? AND time = ? AND in_use = 0 AND fb = ? AND courseName = ?");

         pstmt.clearParameters();          // clear the parms
         pstmt.setString(1, user);         // put the parm in pstmt
         pstmt.setLong(2, date);
         pstmt.setInt(3, time);
         pstmt.setInt(4, fb);
         pstmt.setString(5, course);

         count = pstmt.executeUpdate();            // execute the prepared stmt

         pstmt.close();

         //
         //  If the above was successful, the requested tee time is available
         //
         if (count > 0) {

            in_use = 0;                    // set return code as successful (requested time allocated) ****

         } else {

            //
            //  Requested time is busy - search for the next available time
            //
            pstmt = con.prepareStatement (
               "SELECT day, time, restriction, in_use, fb " +
               "FROM teecurr2 " +
               "WHERE date = ? AND time > ? AND event = '' AND " +
               "player1 = '' AND player2 = '' AND player3 = '' AND player4 = '' AND player5 = '' AND " +
               "(fb = 0 OR fb = 1) AND courseName = ? AND blocker = '' AND in_use = 0 " +
               "ORDER BY time");

            pstmt.clearParameters();        // clear the parms
            pstmt.setLong(1, date);         // put the parm in pstmt
            pstmt.setInt(2, time);
            pstmt.setString(3, course);
            rs = pstmt.executeQuery();      // execute the prepared stmt

            while (rs.next() && i < max) {

               day_name[i] = rs.getString("day");
               timeA[i] = rs.getInt("time");
               rests[i] = rs.getString("restriction");
             //  in_useA[i] = rs.getInt("in_use");
               fbA[i] = rs.getInt("fb");

               i++;
            }

            pstmt.close();

            //
            //  Use the arrays just built to locate an available time
            //
            boolean skip = false;
            boolean suspend = false;

            i = 0;
            loop1:
            while ( i < max && count == 0) {

               skip = false;

              // if (in_useA[i] == 0) {        // if available

                  // ok so far - now check if restricted for this user (if member)

                  if (!user.startsWith( "proshop" ) && !rests[i].equals( "" )) {   // if not proshop user & time restricted

                     sfb = "Front";

                     if (fbA[i] == 1) {
                        sfb = "Back";
                     }

                     //
                     //  Check if this member is restricted from this time
                     //
                     int ind = 0;                                            // init index

                     while (ind < parmr.MAX && skip == false && !parmr.restName[ind].equals("")) {     // check all possible restrictions

                        if (parmr.applies[ind] == 1 && parmr.stime[ind] <= timeA[i] && parmr.etime[ind] >= timeA[i]) {    // matching time ?

                           // Check to make sure no suspensions apply
                           suspend = false;
                           for (int k=0; k<parmr.MAX; k++) {

                               if (parmr.susp[ind][k][0] == 0 && parmr.susp[ind][k][1] == 0) {
                                   k = parmr.MAX;   // don't bother checking any more
                               } else if (parmr.susp[ind][k][0] <= timeA[i] && parmr.susp[ind][k][1] >= timeA[i]) {    //tee_time falls within a suspension
                                   suspend = true;
                                   k = parmr.MAX;     // don't bother checking any more
                               }
                           }

                           if (!suspend) {

                               if ((parmr.courseName[ind].equals( "-ALL-" )) || (parmr.courseName[ind].equals( course ))) {  // course ?

                                  if ((parmr.fb[ind].equals( "Both" )) || (parmr.fb[ind].equals( sfb ))) {    // matching f/b ?

                                     //
                                     //  Found a restriction that matches date, time, day, F/B, mtype & mship of this member
                                     //
                                     skip = true;                    // match found - skip this one
                                  }
                               }
                           }
                        }
                        ind++;
                     }               // end of while parmr.MAX
                  }                  // end of if rest exists in teecurr

                  //
                  //  Make sure tee time is ok for member access
                  //
                  if (skip == false && !user.startsWith( "proshop" ) && slotParms.club.equals( "portlandgc" )) {

                     skip = verifyCustom.checkPGCwalkup(date, timeA[i], day_name[i]);   // skip this one if Walk-Up time
                  }


                  if (skip == false) {              // if still ok

                     time = timeA[i];               // get the new time

                     fb = fbA[i];                   // get fb

                     //
                     //  Found one - make sure it is still available
                     //
                     pstmt = con.prepareStatement (
                        "UPDATE teecurr2 SET in_use = 1, in_use_by = ? WHERE date = ? AND time = ? AND in_use = 0 AND fb = ? AND courseName = ?");

                     pstmt.clearParameters();
                     pstmt.setString(1, user);
                     pstmt.setLong(2, date);
                     pstmt.setInt(3, time);
                     pstmt.setInt(4, fb);
                     pstmt.setString(5, course);

                     count = pstmt.executeUpdate();

                     pstmt.close();

                     if (count > 0) {        // if this time was successfully allocated

                        in_use = 9;         // set return code as successful (next available time allocated) ****
                        count = 1;
                        break loop1;        // exit loop
                     }
                  }                   // end of IF not restricted
              // }                    // end of IF available

               i++;
            }           // end of while max (loop1)

            /*   moved to above loop
            //
            //  If we got one, set it busy
            //
            if (count > 0) {

               pstmt = con.prepareStatement (
                  "UPDATE teecurr2 SET in_use = 1, in_use_by = ? WHERE date = ? AND time = ? AND in_use = 0 AND fb = ? AND courseName = ?");

               pstmt.clearParameters();
               pstmt.setString(1, user);
               pstmt.setLong(2, date);
               pstmt.setInt(3, time);
               pstmt.setInt(4, fb);
               pstmt.setString(5, course);

               count = pstmt.executeUpdate();

               pstmt.close();
            }
             */

         }  // end of 1st IF count (if original tee time available)


         //
         //  If we now own the requested tee time OR the next available
         //
         if (count > 0) {

            pstmt = con.prepareStatement (
               "SELECT * " +
               "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

            pstmt.clearParameters();        // clear the parms
            pstmt.setLong(1, date);         // put the parm in pstmt
            pstmt.setInt(2, time);
            pstmt.setInt(3, fb);
            pstmt.setString(4, course);
            rs = pstmt.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               slotParms.teecurr_id = rs.getInt( "teecurr_id" );
               slotParms.event = rs.getString( "event" );
               slotParms.player1 = rs.getString( "player1" );
               slotParms.player2 = rs.getString( "player2" );
               slotParms.player3 = rs.getString( "player3" );
               slotParms.player4 = rs.getString( "player4" );
               slotParms.user1 = rs.getString( "username1" );
               slotParms.user2 = rs.getString( "username2" );
               slotParms.user3 = rs.getString( "username3" );
               slotParms.user4 = rs.getString( "username4" );
               slotParms.p1cw = rs.getString( "p1cw" );
               slotParms.p2cw = rs.getString( "p2cw" );
               slotParms.p3cw = rs.getString( "p3cw" );
               slotParms.p4cw = rs.getString( "p4cw" );
               slotParms.last_user = rs.getString( "in_use_by" );
               slotParms.hndcp1 = rs.getFloat( "hndcp1" );
               slotParms.hndcp2 = rs.getFloat( "hndcp2" );
               slotParms.hndcp3 = rs.getFloat( "hndcp3" );
               slotParms.hndcp4 = rs.getFloat( "hndcp4" );
               slotParms.show1 = rs.getShort( "show1" );
               slotParms.show2 = rs.getShort( "show2" );
               slotParms.show3 = rs.getShort( "show3" );
               slotParms.show4 = rs.getShort( "show4" );
               slotParms.player5 = rs.getString( "player5" );
               slotParms.user5 = rs.getString( "username5" );
               slotParms.p5cw = rs.getString( "p5cw" );
               slotParms.hndcp5 = rs.getFloat( "hndcp5" );
               slotParms.show5 = rs.getShort( "show5" );
               slotParms.notes = rs.getString( "notes" );
               slotParms.hide = rs.getInt( "hideNotes" );
               slotParms.blocker = rs.getString( "blocker" );
               slotParms.rest5 = rs.getString( "rest5" );
               slotParms.mNum1 = rs.getString( "mNum1" );
               slotParms.mNum2 = rs.getString( "mNum2" );
               slotParms.mNum3 = rs.getString( "mNum3" );
               slotParms.mNum4 = rs.getString( "mNum4" );
               slotParms.mNum5 = rs.getString( "mNum5" );
               slotParms.userg1 = rs.getString( "userg1" );
               slotParms.userg2 = rs.getString( "userg2" );
               slotParms.userg3 = rs.getString( "userg3" );
               slotParms.userg4 = rs.getString( "userg4" );
               slotParms.userg5 = rs.getString( "userg5" );
               slotParms.guest_id1 = rs.getInt( "guest_id1" );
               slotParms.guest_id2 = rs.getInt( "guest_id2" );
               slotParms.guest_id3 = rs.getInt( "guest_id3" );
               slotParms.guest_id4 = rs.getInt( "guest_id4" );
               slotParms.guest_id5 = rs.getInt( "guest_id5" );
               slotParms.orig_by = rs.getString( "orig_by" );
               slotParms.conf = rs.getString( "conf" );
               slotParms.p91 = rs.getInt( "p91" );
               slotParms.p92 = rs.getInt( "p92" );
               slotParms.p93 = rs.getInt( "p93" );
               slotParms.p94 = rs.getInt( "p94" );
               slotParms.p95 = rs.getInt( "p95" );
               slotParms.pos1 = rs.getShort( "pos1" );
               slotParms.pos2 = rs.getShort( "pos2" );
               slotParms.pos3 = rs.getShort( "pos3" );
               slotParms.pos4 = rs.getShort( "pos4" );
               slotParms.pos5 = rs.getShort( "pos5" );
               slotParms.custom_disp1 = rs.getString( "custom_disp1" );
               slotParms.custom_disp2 = rs.getString( "custom_disp2" );
               slotParms.custom_disp3 = rs.getString( "custom_disp3" );
               slotParms.custom_disp4 = rs.getString( "custom_disp4" );
               slotParms.custom_disp5 = rs.getString( "custom_disp5" );
               slotParms.tflag1 = rs.getString( "tflag1" );
               slotParms.tflag2 = rs.getString( "tflag2" );
               slotParms.tflag3 = rs.getString( "tflag3" );
               slotParms.tflag4 = rs.getString( "tflag4" );
               slotParms.tflag5 = rs.getString( "tflag5" );
               slotParms.orig1 = rs.getString( "orig1" );
               slotParms.orig2 = rs.getString( "orig2" );
               slotParms.orig3 = rs.getString( "orig3" );
               slotParms.orig4 = rs.getString( "orig4" );
               slotParms.orig5 = rs.getString( "orig5" );

               slotParms.time = time;                    // save new time if it changed
               slotParms.fb = fb;                        // and fb
            }

            pstmt.close();

         } else {                // All times are busy or unavailable

            in_use = 1;          // set return code as unsuccessful (no time allocated) ****

         }          // end of IF count (if busy)

         //
         //  See if we got one
         //
         if (in_use != 1) {       // if ok (time and fb must be set)

            //
            //
            //  NOTE: ***** Hazeltine does not use this method yet (see checkInUse above) ******************
            //
            //
            //  Add custom processing for Hazeltine National
            //
            //    If this is a double tee, then we must also lock out its associated time on the opposite tee.
            //
            if (slotParms.club.equals( "hazeltine" )) {

               // if weekend or holiday
               if (slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday") ||
                   date == Hdate1 || date == Hdate2 || date == Hdate3) {

                  found = 0;     // default to not double tee

                  //
                  // Related Times:   8:00 B <--> 10:10 F
                  //                  8:09 B <--> 10:19 F
                  //                  8:18 B <--> 10:28 F
                  //                  8:27 B <--> 10:37 F
                  //                  8:36 B <--> 10:46 F
                  //                  8:45 B <--> 10:55 F
                  //
                  if (time == 800 && fb == 1) {    // if 8:00 AM Back Tee
                     found = 1;                    // found one
                     time = 1010;                  // associated time
                     fb = 0;                       // asscoiated tee
                  } else {
                    if (time == 809 && fb == 1) {    // if 8:09 AM Back Tee
                       found = 1;
                       time = 1019;
                       fb = 0;
                    } else {
                      if (time == 818 && fb == 1) {    // if 8:18 AM Back Tee
                         found = 1;
                         time = 1028;
                         fb = 0;
                      } else {
                        if (time == 827 && fb == 1) {    // if 8:27 AM Back Tee
                           found = 1;
                           time = 1037;
                           fb = 0;
                        } else {
                          if (time == 836 && fb == 1) {    // if 8:36 AM Back Tee
                             found = 1;
                             time = 1046;
                             fb = 0;
                          } else {
                            if (time == 845 && fb == 1) {    // if 8:45 AM Back Tee
                               found = 1;
                               time = 1055;
                               fb = 0;
                            } else {

                              if (time == 1010 && fb == 0) {    // if 10:10 AM Front Tee
                                 found = 1;
                                 time = 800;
                                 fb = 1;
                              } else {
                                if (time == 1019 && fb == 0) {    // if 10:19 AM Front Tee
                                   found = 1;
                                   time = 809;
                                   fb = 1;
                                } else {
                                  if (time == 1028 && fb == 0) {    // if 10:28 AM Front Tee
                                     found = 1;
                                     time = 818;
                                     fb = 1;
                                  } else {
                                    if (time == 1037 && fb == 0) {    // if 10:37 AM Front Tee
                                       found = 1;
                                       time = 827;
                                       fb = 1;
                                    } else {
                                      if (time == 1046 && fb == 0) {    // if 10:46 AM Front Tee
                                         found = 1;
                                         time = 836;
                                         fb = 1;
                                      } else {
                                        if (time == 1055 && fb == 0) {    // if 10:55 AM Front Tee
                                           found = 1;
                                           time = 845;
                                           fb = 1;
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                  //
                  //  if we hit on a time, then we must lock out the associated time (should be free if assoc is)
                  //
                  if (found == 1) {

                     pstmt = con.prepareStatement (
                        "UPDATE teecurr2 SET in_use = 1, in_use_by = ? WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                     pstmt.clearParameters();          // clear the parms
                     pstmt.setString(1, user);         // put the parm in pstmt
                     pstmt.setLong(2, date);
                     pstmt.setInt(3, time);
                     pstmt.setInt(4, fb);
                     pstmt.setString(5, course);
                     pstmt.executeUpdate();            // execute the prepared stmt

                     pstmt.close();
                  }
               }
            }
         }

      }
      catch (SQLException e) {

         throw new Exception("Error checking in-use - verifySlot.checkInUseN - SQL Exception: " + e.getMessage());
      }
      catch (Exception e) {

         throw new Exception("Error checking in-use - verifySlot.checkInUseN - Exception: " + e.getMessage());
      }
   }

   return(in_use);
 }


/**
 //************************************************************************
 //
 //  checkInUseMn - check if multiple slots are in use and if not, set them.
 //
 //
 //      Custom - this version will attempt to satisfy the request the best it can
 //               by searching for the next available group of times if one or more
 //               of the requested times are not available.
 //
 //
 //************************************************************************
 **/

 public static int checkInUseMn(boolean consecutive, long date, int time, int fb, String course, String user, parmSlotm slotParms, Connection con)
         throws Exception {


   PreparedStatement pstmt = null;
   Statement stmt = null;
   ResultSet rs = null;


   boolean skip = false;
   boolean suspend = false;

   //int max = 30;               // max number of tee times to check (could change in future)
   int max = 40;                 // new max to prevent unsuccessful requests

   //  custom for Long Cove to scan even further
   if (slotParms.club.equals("longcove")) {
      max = 60;
   }


   int i = 0;
   int i2 = 0;
   int first = 0;
   int status = 0;
   int found = 0;
   int updateCount = 0;
   int count = slotParms.slots;          // # of tee times requested

   int [] in_use = new int [max];
   int [] timeA = new int [max];
   int [] timeR = new int [5];

   String [] player1 = new String [max];
   String [] player2 = new String [max];
   String [] player3 = new String [max];
   String [] player4 = new String [max];
   String [] player5 = new String [max];
   String [] events = new String [max];
   String [] blockers = new String [max];
   String [] rests = new String [max];

   String sfb = "Front";

   if (fb == 1) {
      sfb = "Back";
   }


   //
   //  parm block to hold the member restrictions for this date and member
   //
   parmRest parmr = new parmRest();          // allocate a parm block

   //
   //  Get all restrictions for this day and user (for use when checking each tee time below)
   //
   parmr.user = user;
   parmr.mship = slotParms.mship;
   parmr.mtype = slotParms.mtype;
   parmr.date = date;
   parmr.day = slotParms.day;
   parmr.course = course;

   if (!user.startsWith( "proshop" )) {   // if member

      getRests.getAll(con, parmr);       // get the restrictions for this member, day and course
   }


   if (count > 5) {           // make sure no problems

      count = 5;
   }

   for (i=0; i<max; i++) {         // init player fields

      player1[i] = "";
      player2[i] = "";
      player3[i] = "";
      player4[i] = "";
      player5[i] = "";
      events[i] = "";
      blockers[i] = "";
      rests[i] = "";
      in_use[i] = 1;            // init to busy in case tee time not found
   }

   //
   //  Verify the input parms
   //
   if (date > 0 && time > 0 && course != null && user != null && !user.equals( "" )) {

      try {

         //
         //  Get the next 40 (max) tee times starting with the 1st time that was requested
         //
         i = 0;

         pstmt = con.prepareStatement (
            "SELECT time, event, restriction, player1, player2, player3, player4, in_use, player5, blocker " +
            "FROM teecurr2 WHERE date = ? AND time >= ? AND fb = ? AND courseName = ? ORDER BY time");

         pstmt.clearParameters();        // clear the parms
         pstmt.setLong(1, date);         // put the parm in pstmt
         pstmt.setInt(2, time);
         pstmt.setInt(3, fb);
         pstmt.setString(4, course);
         rs = pstmt.executeQuery();      // execute the prepared stmt

         while (rs.next() && i < max) {

            timeA[i] = rs.getInt("time");
            events[i] = rs.getString("event");
            rests[i] = rs.getString("restriction");
            player1[i] = rs.getString("player1");
            player2[i] = rs.getString("player2");
            player3[i] = rs.getString("player3");
            player4[i] = rs.getString("player4");
            in_use[i] = rs.getInt("in_use");
            player5[i] = rs.getString("player5");
            blockers[i] = rs.getString("blocker");

            i++;
         }

         pstmt.close();

         status = 0;

         //
         //  Find the requested number of available tee times.
         //
         //   We must check for restrictions, events and blockers since the user could select a time just prior to these
         //   and then the subsequent times are affected by the event or blocker (we don't know until here).
         //
         i = 0;
         i2 = 0;
         while ( i < max && found < count ) {

            skip = false;                          // init skip flag

            if (in_use[i] > 0 || !player1[i].equals( "" ) || !player2[i].equals( "" ) ||
                !player3[i].equals( "" ) || !player4[i].equals( "" ) || !player5[i].equals( "" ) ||
                ((!events[i].equals( "" ) && !slotParms.club.equals("chartwellgcc")) || (slotParms.club.equals("chartwellgcc") && !slotParms.custom_string1.equals("sgcons"))) ||
                !blockers[i].equals( "" )) {

               skip = true;

            } else {

               // ok so far - now check if restricted for this user (if member)

               if (!user.startsWith( "proshop" ) && !rests[i].equals( "" )) {   // if not proshop user & time restricted

                  //
                  //  Check if this member is restricted from this time
                  //
                  int ind = 0;                                            // init index

                  while (ind < parmr.MAX && skip == false && !parmr.restName[ind].equals("")) {                 // check all possible restrictions

                     if (parmr.applies[ind] == 1 && parmr.stime[ind] <= timeA[i] && parmr.etime[ind] >= timeA[i]) {         // matching time ?

                         // Check to make sure no suspensions apply
                         suspend = false;
                         for (int k=0; k<parmr.MAX; k++) {

                             if (parmr.susp[ind][k][0] == 0 && parmr.susp[ind][k][1] == 0) {
                                 k = parmr.MAX;   // don't bother checking any more
                             } else if (parmr.susp[ind][k][0] <= timeA[i] && parmr.susp[ind][k][1] >= timeA[i]) {    //tee_time falls within a suspension
                                 suspend = true;
                                 k = parmr.MAX;     // don't bother checking any more
                             }
                         }

                         if (!suspend) {

                             if ((parmr.courseName[ind].equals( "-ALL-" )) || (parmr.courseName[ind].equals( course ))) {  // course ?

                                 if ((parmr.fb[ind].equals( "Both" )) || (parmr.fb[ind].equals( sfb ))) {    // matching f/b ?
                                                                   //
                                     //  Found a restriction that matches date, time, day, F/B, mtype & mship of this member
                                     //
                                     skip = true;                    // match found - skip this one
                                 }
                             }
                         }
                     }
                     ind++;
                  }               // end of while (loop2)
               }                  // end of if rest exists in teecurr
            }

            if (skip == true) {

               //
               //  busy, event, restriction or blocker - skip this one
               //
               if (consecutive == true) {            // if consecutive times requested

                  found = 0;                         // start over

               } else {

                  first = 1;                         // indicate at least one time was not available
               }

               if (status == 0) {          // if no status yet (always use the first one found)

                  if (!events[i].equals( "" )) {

                     status = 2;       // tee time during an event

                  } else {

                     if (!blockers[i].equals( "" )) {

                        status = 3;       // tee time is blocked

                     } else {

                        status = 1;              // busy
                     }
                  }
               }

            } else {              // not busy

               if (consecutive == true) {            // if consecutive times requested

                  if (found == 0) {          // if this is the first time found

                     first = i;             // save the index
                  }

               } else {                // NOT consecutive times

                  timeR[i2] = timeA[i];    // save this time

                  i2++;
               }

               found++;                // add to total found
            }

            i++;                      // check the next time
         }       // end of while max


         //
         //  If ok, set them all busy
         //
         if (found == count) {                // if we found them all

            status = 0;                       // make sure the status is good (could have been bad earlier)

            //
            //  save the time values for each tee time
            //
            if (consecutive == true) {            // if consecutive times requested

               i2 = first;                        // restore the index value of the first good time

               for (i=0; i<count; i++) {

                  timeR[i] = timeA[i2];           // copy the allocated times into an array

                  i2++;
               }
            }

            slotParms.time1 = timeR[0];        // save time values
            slotParms.time2 = timeR[1];
            slotParms.time3 = timeR[2];
            slotParms.time4 = timeR[3];
            slotParms.time5 = timeR[4];


            loop1:
            for (i=0; i<count; i++) {             // process each time

               if (timeR[i] > 0) {                // make sure we have a time

                  updateCount = 0;

                  //
                  //  Update the tee time if it is still available (in_use = 0)
                  //
                  pstmt = con.prepareStatement (
                     "UPDATE teecurr2 SET in_use = 1, in_use_by = ? " +
                     "WHERE date = ? AND time = ? AND in_use = 0 AND fb = ? AND courseName = ?");

                  pstmt.clearParameters();          // clear the parms
                  pstmt.setString(1, user);         // put the parm in pstmt
                  pstmt.setLong(2, date);
                  pstmt.setInt(3, timeR[i]);
                  pstmt.setInt(4, fb);
                  pstmt.setString(5, course);

                  updateCount = pstmt.executeUpdate();            // execute the prepared stmt

                  pstmt.close();

                  //
                  //  Check if the tee time was actually updated (if still available)
                  //
                  if (updateCount == 0) {        // if now busy

                     //
                     //  Now set the previous times back to not busy
                     //
                     if (i > 0) {            // if we did any others

                        for (i2=0; i2<i; i2++) {

                           pstmt = con.prepareStatement (
                              "UPDATE teecurr2 SET in_use = 0 " +
                              "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                           pstmt.clearParameters();
                           pstmt.setLong(1, date);
                           pstmt.setInt(2, timeA[i2]);
                           pstmt.setInt(3, fb);
                           pstmt.setString(4, course);

                           pstmt.executeUpdate();            // execute the prepared stmt

                           pstmt.close();

                        }
                     }

                     status = 1;                 // set as busy
                     break loop1;                // quit trying
                  }

               } else {

                  status = 2;                        // error - more times requested than exist
                  break loop1;
               }

            }   // end of loop1

            if (status == 0 && first > 0) {             // if still ok and we found times different than those requested

               status = 9;                             // inform caller
            }
         }

      }
      catch (SQLException e) {
         status = 4;           // db error - could be not enough tee times at end of day
      }
      catch (Exception e) {
         status = 4;           // db error - could be not enough tee times at end of day
      }

   } else {

      status = 1;       // busy (error)
   }

   return(status);
 }


/**
 //************************************************************************
 //
 //  checkInUseM - check if multiple slots are in use and if not, set them.
 //
 //      The tee times must be empty when first requested!!
 //
 //************************************************************************
 **/

 /*    // no longer used - 5/16/08
  *
 public static int checkInUseM(long date, int time, int fb, String course, String user, parmSlotm slotParms, Connection con)
         throws Exception {


   PreparedStatement pstmt = null;
   Statement stmt = null;
   ResultSet rs = null;

   int i = 0;
   int i2 = 0;
   int status = 0;
   int found = 0;
   int updateCount = 0;
   int count = slotParms.slots;          // # of tee times requested

   int [] in_use = new int [5];
   int [] timeA = new int [5];

   String [] player1 = new String [5];
   String [] player2 = new String [5];
   String [] player3 = new String [5];
   String [] player4 = new String [5];
   String [] player5 = new String [5];
   String [] events = new String [5];
   String [] blockers = new String [5];

   if (count > 5) {           // make sure no problems

      count = 5;
   }

   for (i=0; i<5; i++) {         // init player fields

      player1[i] = "";
      player2[i] = "";
      player3[i] = "";
      player4[i] = "";
      player5[i] = "";
      in_use[i] = 1;            // init to busy in case tee time not found
   }

   //
   //  Verify the input parms
   //
   if (date > 0 && time > 0 && course != null && user != null && !user.equals( "" )) {

      try {

         //
         //  Get the tee times that were requested
         //
         i = 0;

         pstmt = con.prepareStatement (
            "SELECT time, event, player1,  player2,  player3,  player4, in_use,  player5, blocker " +
            "FROM teecurr2 WHERE date = ? AND time >= ? AND fb = ? AND courseName = ? ORDER BY time");

         pstmt.clearParameters();        // clear the parms
         pstmt.setLong(1, date);         // put the parm in pstmt
         pstmt.setInt(2, time);
         pstmt.setInt(3, fb);
         pstmt.setString(4, course);
         rs = pstmt.executeQuery();      // execute the prepared stmt

         while (rs.next() && i < count) {

            timeA[i] = rs.getInt(1);
            events[i] = rs.getString(2);
            player1[i] = rs.getString(3);
            player2[i] = rs.getString(4);
            player3[i] = rs.getString(5);
            player4[i] = rs.getString(6);
            in_use[i] = rs.getInt(7);
            player5[i] = rs.getString(8);
            blockers[i] = rs.getString(9);

            i++;
         }

         pstmt.close();

         status = 0;

         //
         //  Make sure they are available
         //
         //   We must check for events and blockers since the user could select a time just prior to these
         //   and then the subsequent times are affected by the event or blocker (we don't know until here).
         //
         for (i=0; i<count; i++) {

            if (in_use[i] != 0 || !player1[i].equals( "" ) || !player2[i].equals( "" ) || !player3[i].equals( "" ) ||
                !player4[i].equals( "" ) || !player5[i].equals( "" ) ) {

               status = 1;       // busy !!
            }
            if (!events[i].equals( "" )) {

               status = 2;       // tee time during an event
            }
            if (!blockers[i].equals( "" )) {

               status = 3;       // tee time is blocked
            }
         }

         //
         //  If ok, set them all busy
         //
         if (status == 0) {

            loop1:
            for (i=0; i<count; i++) {

               if (timeA[i] > 0) {            // make sure we have a time

                  updateCount = 0;

                  //
                  //  Update the tee time if it is still available (in_use = 0)
                  //
                  pstmt = con.prepareStatement (
                     "UPDATE teecurr2 SET in_use = 1, in_use_by = ? " +
                     "WHERE date = ? AND time = ? AND in_use = 0 AND fb = ? AND courseName = ?");

                  pstmt.clearParameters();          // clear the parms
                  pstmt.setString(1, user);         // put the parm in pstmt
                  pstmt.setLong(2, date);
                  pstmt.setInt(3, timeA[i]);
                  pstmt.setInt(4, fb);
                  pstmt.setString(5, course);

                  updateCount = pstmt.executeUpdate();            // execute the prepared stmt

                  pstmt.close();

                  //
                  //  Check if the tee time was actually updated (if still available)
                  //
                  if (updateCount == 0) {        // if now busy

                     //
                     //  Now set the previous times back to not busy
                     //
                     if (i > 0) {            // if we did any others

                        for (i2=0; i2<i; i2++) {

                           pstmt = con.prepareStatement (
                              "UPDATE teecurr2 SET in_use = 0 " +
                              "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                           pstmt.clearParameters();
                           pstmt.setLong(1, date);
                           pstmt.setInt(2, timeA[i2]);
                           pstmt.setInt(3, fb);
                           pstmt.setString(4, course);

                           pstmt.executeUpdate();            // execute the prepared stmt

                           pstmt.close();

                        }
                     }

                     status = 1;                 // set as busy
                     break loop1;                // quit trying
                  }

               } else {

                  status = 2;                        // error - more times requested than exist
                  break loop1;
               }

            }   // end of loop1

         }

         //
         //  save the time values for each tee time
         //
         slotParms.time1 = timeA[0];
         slotParms.time2 = timeA[1];
         slotParms.time3 = timeA[2];
         slotParms.time4 = timeA[3];
         slotParms.time5 = timeA[4];

      }
      catch (SQLException e) {
         status = 4;           // db error - could be not enough tee times at end of day
      }
      catch (Exception e) {
         status = 4;           // db error - could be not enough tee times at end of day
      }

   } else {

      status = 1;       // busy (error)
   }

   return(status);
 }
  */




/**
 //************************************************************************
 //
 //  clearInUseM - clear the in use flag for multiple tee time slots.
 //
 //************************************************************************
 **/

 public static void clearInUseM(long date, int time, int time2, int time3, int time4, int time5,
                                             int fb, String course, Connection con)
                                             throws Exception {


   //Statement stmt = null;
   //ResultSet rs = null;

   int i = 0;

   int [] timeA = new int [5];

   timeA[0] = time;
   timeA[1] = time2;
   timeA[2] = time3;
   timeA[3] = time4;
   timeA[4] = time5;

   try {

      //
      //  Clear the in use flags
      //
      for (i=0; i<5; i++) {

         if (timeA[i] > 0) {            // make sure we have a time

            PreparedStatement pstmt1 = con.prepareStatement (
               "UPDATE teecurr2 SET in_use = 0 " +
               "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

            pstmt1.clearParameters();          // clear the parms
            pstmt1.setLong(1, date);
            pstmt1.setInt(2, timeA[i]);
            pstmt1.setInt(3, fb);
            pstmt1.setString(4, course);
            pstmt1.executeUpdate();            // execute the prepared stmt

            pstmt1.close();
         }
      }

   }
   catch (Exception e) {

      throw new Exception("Error clearing in-use - verifySlot.clearInUseM " + e.getMessage());
   }

 }


/**
 //************************************************************************
 //
 //  addMteeTime - Add multiple tee time slots.
 //
 //************************************************************************
 **/

 public static int addMteeTime(parmSlotm parm, Connection con)
         throws Exception {


   PreparedStatement pstmt6 = null;
   //Statement stmt = null;
   //ResultSet rs = null;

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
   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";
   String mNum1 = "";
   String mNum2 = "";
   String mNum3 = "";
   String mNum4 = "";
   String mNum5 = "";
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
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
   String orig1 = "";
   String orig2 = "";
   String orig3 = "";
   String orig4 = "";
   String orig5 = "";
   String user = "";
   String omit = "";

   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int guest_id1 = 0;
   int guest_id2 = 0;
   int guest_id3 = 0;
   int guest_id4 = 0;
   int guest_id5 = 0;
   int proNew = 0;
   int memNew = 0;
   int newTimes = 0;

   short show1 = 0;
   short show2 = 0;
   short show3 = 0;
   short show4 = 0;
   short show5 = 0;

   float hndcp1 = 0;
   float hndcp2 = 0;
   float hndcp3 = 0;
   float hndcp4 = 0;
   float hndcp5 = 0;

   int count = parm.slots;    // # of tee times to be added

   //
   //  Determine if Proshop or Member
   //
   if (parm.orig_by.startsWith( "proshop" )) {

      proNew = 1;
      user = "";

   } else {

      memNew = 1;
      user = parm.orig_by;
   }


   //
   //  Add each new tee time
   //
   player1 = parm.player1;
   player2 = parm.player2;
   player3 = parm.player3;
   player4 = parm.player4;
   user1 = parm.user1;
   user2 = parm.user2;
   user3 = parm.user3;
   user4 = parm.user4;
   p1cw = parm.pcw1;
   p2cw = parm.pcw2;
   p3cw = parm.pcw3;
   p4cw = parm.pcw4;
   mNum1 = parm.mNum1;
   mNum2 = parm.mNum2;
   mNum3 = parm.mNum3;
   mNum4 = parm.mNum4;
   userg1 = parm.userg[0];
   userg2 = parm.userg[1];
   userg3 = parm.userg[2];
   userg4 = parm.userg[3];
   hndcp1 = parm.hndcp1;
   hndcp2 = parm.hndcp2;
   hndcp3 = parm.hndcp3;
   hndcp4 = parm.hndcp4;
   p91 = parm.p91;
   p92 = parm.p92;
   p93 = parm.p93;
   p94 = parm.p94;
   show1 = (parm.precheckin == 1 && parm.show1 == 0) ? 2 : parm.show1;
   show2 = (parm.precheckin == 1 && parm.show2 == 0) ? 2 : parm.show2;
   show3 = (parm.precheckin == 1 && parm.show3 == 0) ? 2 : parm.show3;
   show4 = (parm.precheckin == 1 && parm.show4 == 0) ? 2 : parm.show4;
   custom_disp1 = parm.custom_disp1;
   custom_disp2 = parm.custom_disp2;
   custom_disp3 = parm.custom_disp3;
   custom_disp4 = parm.custom_disp4;
   tflag1 = parm.tflag1;
   tflag2 = parm.tflag2;
   tflag3 = parm.tflag3;
   tflag4 = parm.tflag4;
   guest_id1 = parm.guest_id1;
   guest_id2 = parm.guest_id2;
   guest_id3 = parm.guest_id3;
   guest_id4 = parm.guest_id4;
   orig1 = (!player1.equals("") ? user : "");
   orig2 = (!player2.equals("") ? user : "");
   orig3 = (!player3.equals("") ? user : "");
   orig4 = (!player4.equals("") ? user : "");

   if (parm.p5.equals( "Yes" )) {

      player5 = parm.player5;
      user5 = parm.user5;
      p5cw = parm.pcw5;
      mNum5 = parm.mNum5;
      userg5 = parm.userg[4];
      hndcp5 = parm.hndcp5;
      p95 = parm.p95;
      show5 = (parm.precheckin == 1 && parm.show5 == 0) ? 2 : parm.show5;
      custom_disp5 = parm.custom_disp5;
      tflag5 = parm.tflag5;
      guest_id5 = parm.guest_id5;
      orig5 = (!player5.equals("") ? user : "");

   } else {

      player5 = "";
      user5 = "";
      p5cw = "";
      mNum5 = "";
      userg5 = "";
      hndcp5 = 0;
      p95 = 0;
      show5 = 0;
      custom_disp5 = "";
      tflag5 = "";
      guest_id5 = 0;
      orig5 = "";
   }

   try {

      pstmt6 = con.prepareStatement (
         "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
         "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
         "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
         "hndcp4 = ?, show1 = ?, show2 = ?, show3 = ?, show4 = ?, player5 = ?, username5 = ?, " +
         "p5cw = ?, hndcp5 = ?, show5 = ?, notes = ?, proNew = ?, memNew = ?, " +
         "mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
         "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, " +
         "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, " +
         "custom_disp1 = ?, custom_disp2 = ?, custom_disp3 = ?, custom_disp4 = ?, custom_disp5 = ?, custom_int = ?, " +
         "tflag1 = ?, tflag2 = ?, tflag3 = ?, tflag4 = ?, tflag5 = ?, " +
         "guest_id1 = ?, guest_id2 = ?, guest_id3 = ?, guest_id4 = ?, guest_id5 = ?, " +
         "orig1 = ?, orig2 = ?, orig3 = ?, orig4 = ?, orig5 = ? " +
         "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

      pstmt6.clearParameters();        // clear the parms
      pstmt6.setString(1, player1);
      pstmt6.setString(2, player2);
      pstmt6.setString(3, player3);
      pstmt6.setString(4, player4);
      pstmt6.setString(5, user1);
      pstmt6.setString(6, user2);
      pstmt6.setString(7, user3);
      pstmt6.setString(8, user4);
      pstmt6.setString(9, p1cw);
      pstmt6.setString(10, p2cw);
      pstmt6.setString(11, p3cw);
      pstmt6.setString(12, p4cw);
      pstmt6.setFloat(13, hndcp1);
      pstmt6.setFloat(14, hndcp2);
      pstmt6.setFloat(15, hndcp3);
      pstmt6.setFloat(16, hndcp4);
      pstmt6.setShort(17, show1);
      pstmt6.setShort(18, show2);
      pstmt6.setShort(19, show3);
      pstmt6.setShort(20, show4);
      pstmt6.setString(21, player5);
      pstmt6.setString(22, user5);
      pstmt6.setString(23, p5cw);
      pstmt6.setFloat(24, hndcp5);
      pstmt6.setShort(25, show5);
      pstmt6.setString(26, parm.notes);
      pstmt6.setInt(27, proNew);
      pstmt6.setInt(28, memNew);
      pstmt6.setString(29, mNum1);
      pstmt6.setString(30, mNum2);
      pstmt6.setString(31, mNum3);
      pstmt6.setString(32, mNum4);
      pstmt6.setString(33, mNum5);
      pstmt6.setString(34, userg1);
      pstmt6.setString(35, userg2);
      pstmt6.setString(36, userg3);
      pstmt6.setString(37, userg4);
      pstmt6.setString(38, userg5);
      pstmt6.setString(39, parm.orig_by);
      pstmt6.setInt(40, p91);
      pstmt6.setInt(41, p92);
      pstmt6.setInt(42, p93);
      pstmt6.setInt(43, p94);
      pstmt6.setInt(44, p95);
      pstmt6.setString(45, custom_disp1);
      pstmt6.setString(46, custom_disp2);
      pstmt6.setString(47, custom_disp3);
      pstmt6.setString(48, custom_disp4);
      pstmt6.setString(49, custom_disp5);
      pstmt6.setInt(50, parm.custom_int1);
      pstmt6.setString(51, tflag1);
      pstmt6.setString(52, tflag2);
      pstmt6.setString(53, tflag3);
      pstmt6.setString(54, tflag4);
      pstmt6.setString(55, tflag5);
      pstmt6.setInt(56, guest_id1);
      pstmt6.setInt(57, guest_id2);
      pstmt6.setInt(58, guest_id3);
      pstmt6.setInt(59, guest_id4);
      pstmt6.setInt(60, guest_id5);
      pstmt6.setString(61, orig1);
      pstmt6.setString(62, orig2);
      pstmt6.setString(63, orig3);
      pstmt6.setString(64, orig4);
      pstmt6.setString(65, orig5);

      pstmt6.setLong(66, parm.date);
      pstmt6.setInt(67, parm.time1);
      pstmt6.setInt(68, parm.fb);
      pstmt6.setString(69, parm.course);
      pstmt6.executeUpdate();      // execute the prepared stmt

      pstmt6.close();

      if (!player1.equals( "" ) || !player2.equals( "" ) || !player3.equals( "" ) || !player4.equals( "" ) ||
          !player5.equals( "" )) {

         newTimes++;               // count number of tee times added
      }

      //
      //  Add next tee time, if present
      //
      if (count > 1) {

         if (parm.p5.equals( "Yes" )) {

            player1 = parm.player6;
            player2 = parm.player7;
            player3 = parm.player8;
            player4 = parm.player9;
            player5 = parm.player10;
            user1 = parm.user6;
            user2 = parm.user7;
            user3 = parm.user8;
            user4 = parm.user9;
            user5 = parm.user10;
            p1cw = parm.pcw6;
            p2cw = parm.pcw7;
            p3cw = parm.pcw8;
            p4cw = parm.pcw9;
            p5cw = parm.pcw10;
            mNum1 = parm.mNum6;
            mNum2 = parm.mNum7;
            mNum3 = parm.mNum8;
            mNum4 = parm.mNum9;
            mNum5 = parm.mNum10;
            userg1 = parm.userg[5];
            userg2 = parm.userg[6];
            userg3 = parm.userg[7];
            userg4 = parm.userg[8];
            userg5 = parm.userg[9];
            hndcp1 = parm.hndcp6;
            hndcp2 = parm.hndcp7;
            hndcp3 = parm.hndcp8;
            hndcp4 = parm.hndcp9;
            hndcp5 = parm.hndcp10;
            p91 = parm.p96;
            p92 = parm.p97;
            p93 = parm.p98;
            p94 = parm.p99;
            p95 = parm.p910;
            show1 = (parm.precheckin == 1 && parm.show6 == 0) ? 2 : parm.show6;
            show2 = (parm.precheckin == 1 && parm.show7 == 0) ? 2 : parm.show7;
            show3 = (parm.precheckin == 1 && parm.show8 == 0) ? 2 : parm.show8;
            show4 = (parm.precheckin == 1 && parm.show9 == 0) ? 2 : parm.show9;
            show5 = (parm.precheckin == 1 && parm.show10 == 0) ? 2 : parm.show10;
            custom_disp1 = parm.custom_disp6;
            custom_disp2 = parm.custom_disp7;
            custom_disp3 = parm.custom_disp8;
            custom_disp4 = parm.custom_disp9;
            custom_disp5 = parm.custom_disp10;
            tflag1 = parm.tflag6;
            tflag2 = parm.tflag7;
            tflag3 = parm.tflag8;
            tflag4 = parm.tflag9;
            tflag5 = parm.tflag10;
            guest_id1 = parm.guest_id6;
            guest_id2 = parm.guest_id7;
            guest_id3 = parm.guest_id8;
            guest_id4 = parm.guest_id9;
            guest_id5 = parm.guest_id10;
            orig1 = (!player1.equals("") ? user : "");
            orig2 = (!player2.equals("") ? user : "");
            orig3 = (!player3.equals("") ? user : "");
            orig4 = (!player4.equals("") ? user : "");
            orig5 = (!player5.equals("") ? user : "");

         } else {

            player1 = parm.player5;
            player2 = parm.player6;
            player3 = parm.player7;
            player4 = parm.player8;
            user1 = parm.user5;
            user2 = parm.user6;
            user3 = parm.user7;
            user4 = parm.user8;
            p1cw = parm.pcw5;
            p2cw = parm.pcw6;
            p3cw = parm.pcw7;
            p4cw = parm.pcw8;
            mNum1 = parm.mNum5;
            mNum2 = parm.mNum6;
            mNum3 = parm.mNum7;
            mNum4 = parm.mNum8;
            userg1 = parm.userg[4];
            userg2 = parm.userg[5];
            userg3 = parm.userg[6];
            userg4 = parm.userg[7];
            hndcp1 = parm.hndcp5;
            hndcp2 = parm.hndcp6;
            hndcp3 = parm.hndcp7;
            hndcp4 = parm.hndcp8;
            p91 = parm.p95;
            p92 = parm.p96;
            p93 = parm.p97;
            p94 = parm.p98;
            show1 = (parm.precheckin == 1 && parm.show5 == 0) ? 2 : parm.show5;
            show2 = (parm.precheckin == 1 && parm.show6 == 0) ? 2 : parm.show6;
            show3 = (parm.precheckin == 1 && parm.show7 == 0) ? 2 : parm.show7;
            show4 = (parm.precheckin == 1 && parm.show8 == 0) ? 2 : parm.show8;
            custom_disp1 = parm.custom_disp5;
            custom_disp2 = parm.custom_disp6;
            custom_disp3 = parm.custom_disp7;
            custom_disp4 = parm.custom_disp8;
            tflag1 = parm.tflag5;
            tflag2 = parm.tflag6;
            tflag3 = parm.tflag7;
            tflag4 = parm.tflag8;
            guest_id1 = parm.guest_id5;
            guest_id2 = parm.guest_id6;
            guest_id3 = parm.guest_id7;
            guest_id4 = parm.guest_id8;
            orig1 = (!player1.equals("") ? user : "");
            orig2 = (!player2.equals("") ? user : "");
            orig3 = (!player3.equals("") ? user : "");
            orig4 = (!player4.equals("") ? user : "");

            player5 = "";
            user5 = "";
            p5cw = "";
            mNum5 = "";
            userg5 = "";
            hndcp5 = 0;
            p95 = 0;
            show5 = 0;
            custom_disp5 = "";
            tflag5 = "";
            guest_id5 = 0;
            orig5 = "";
         }

         pstmt6 = con.prepareStatement (
            "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
            "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
            "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
            "hndcp4 = ?, show1 = ?, show2 = ?, show3 = ?, show4 = ?, player5 = ?, username5 = ?, " +
            "p5cw = ?, hndcp5 = ?, show5 = ?, notes = ?, proNew = ?, memNew = ?, " +
            "mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
            "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, " +
            "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, " +
            "custom_disp1 = ?, custom_disp2 = ?, custom_disp3 = ?, custom_disp4 = ?, custom_disp5 = ?, custom_int = ?, " +
            "tflag1 = ?, tflag2 = ?, tflag3 = ?, tflag4 = ?, tflag5 = ?, " +
            "guest_id1 = ?, guest_id2 = ?, guest_id3 = ?, guest_id4 = ?, guest_id5 = ?, " +
            "orig1 = ?, orig2 = ?, orig3 = ?, orig4 = ?, orig5 = ? " +
            "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt6.clearParameters();        // clear the parms
         pstmt6.setString(1, player1);
         pstmt6.setString(2, player2);
         pstmt6.setString(3, player3);
         pstmt6.setString(4, player4);
         pstmt6.setString(5, user1);
         pstmt6.setString(6, user2);
         pstmt6.setString(7, user3);
         pstmt6.setString(8, user4);
         pstmt6.setString(9, p1cw);
         pstmt6.setString(10, p2cw);
         pstmt6.setString(11, p3cw);
         pstmt6.setString(12, p4cw);
         pstmt6.setFloat(13, hndcp1);
         pstmt6.setFloat(14, hndcp2);
         pstmt6.setFloat(15, hndcp3);
         pstmt6.setFloat(16, hndcp4);
         pstmt6.setShort(17, show1);
         pstmt6.setShort(18, show2);
         pstmt6.setShort(19, show3);
         pstmt6.setShort(20, show4);
         pstmt6.setString(21, player5);
         pstmt6.setString(22, user5);
         pstmt6.setString(23, p5cw);
         pstmt6.setFloat(24, hndcp5);
         pstmt6.setShort(25, show5);
         pstmt6.setString(26, omit);
         pstmt6.setInt(27, proNew);
         pstmt6.setInt(28, memNew);
         pstmt6.setString(29, mNum1);
         pstmt6.setString(30, mNum2);
         pstmt6.setString(31, mNum3);
         pstmt6.setString(32, mNum4);
         pstmt6.setString(33, mNum5);
         pstmt6.setString(34, userg1);
         pstmt6.setString(35, userg2);
         pstmt6.setString(36, userg3);
         pstmt6.setString(37, userg4);
         pstmt6.setString(38, userg5);
         pstmt6.setString(39, parm.orig_by);
         pstmt6.setInt(40, p91);
         pstmt6.setInt(41, p92);
         pstmt6.setInt(42, p93);
         pstmt6.setInt(43, p94);
         pstmt6.setInt(44, p95);
         pstmt6.setString(45, custom_disp1);
         pstmt6.setString(46, custom_disp2);
         pstmt6.setString(47, custom_disp3);
         pstmt6.setString(48, custom_disp4);
         pstmt6.setString(49, custom_disp5);
         pstmt6.setInt(50, parm.custom_int2);
         pstmt6.setString(51, tflag1);
         pstmt6.setString(52, tflag2);
         pstmt6.setString(53, tflag3);
         pstmt6.setString(54, tflag4);
         pstmt6.setString(55, tflag5);
         pstmt6.setInt(56, guest_id1);
         pstmt6.setInt(57, guest_id2);
         pstmt6.setInt(58, guest_id3);
         pstmt6.setInt(59, guest_id4);
         pstmt6.setInt(60, guest_id5);
         pstmt6.setString(61, orig1);
         pstmt6.setString(62, orig2);
         pstmt6.setString(63, orig3);
         pstmt6.setString(64, orig4);
         pstmt6.setString(65, orig5);

         pstmt6.setLong(66, parm.date);
         pstmt6.setInt(67, parm.time2);
         pstmt6.setInt(68, parm.fb);
         pstmt6.setString(69, parm.course);
         pstmt6.executeUpdate();      // execute the prepared stmt

         pstmt6.close();

         if (!player1.equals( "" ) || !player2.equals( "" ) || !player3.equals( "" ) || !player4.equals( "" ) ||
             !player5.equals( "" )) {

            newTimes++;               // count number of tee times added
         }
      }

      //
      //  Add 3rd tee time, if present
      //
      if (count > 2) {

         if (parm.p5.equals( "Yes" )) {

            player1 = parm.player11;
            player2 = parm.player12;
            player3 = parm.player13;
            player4 = parm.player14;
            player5 = parm.player15;
            user1 = parm.user11;
            user2 = parm.user12;
            user3 = parm.user13;
            user4 = parm.user14;
            user5 = parm.user15;
            p1cw = parm.pcw11;
            p2cw = parm.pcw12;
            p3cw = parm.pcw13;
            p4cw = parm.pcw14;
            p5cw = parm.pcw15;
            mNum1 = parm.mNum11;
            mNum2 = parm.mNum12;
            mNum3 = parm.mNum13;
            mNum4 = parm.mNum14;
            mNum5 = parm.mNum15;
            userg1 = parm.userg[10];
            userg2 = parm.userg[11];
            userg3 = parm.userg[12];
            userg4 = parm.userg[13];
            userg5 = parm.userg[14];
            hndcp1 = parm.hndcp11;
            hndcp2 = parm.hndcp12;
            hndcp3 = parm.hndcp13;
            hndcp4 = parm.hndcp14;
            hndcp5 = parm.hndcp15;
            p91 = parm.p911;
            p92 = parm.p912;
            p93 = parm.p913;
            p94 = parm.p914;
            p95 = parm.p915;
            show1 = (parm.precheckin == 1 && parm.show11 == 0) ? 2 : parm.show11;
            show2 = (parm.precheckin == 1 && parm.show12 == 0) ? 2 : parm.show12;
            show3 = (parm.precheckin == 1 && parm.show13 == 0) ? 2 : parm.show13;
            show4 = (parm.precheckin == 1 && parm.show14 == 0) ? 2 : parm.show14;
            show5 = (parm.precheckin == 1 && parm.show15 == 0) ? 2 : parm.show15;
            custom_disp1 = parm.custom_disp11;
            custom_disp2 = parm.custom_disp12;
            custom_disp3 = parm.custom_disp13;
            custom_disp4 = parm.custom_disp14;
            custom_disp5 = parm.custom_disp15;
            tflag1 = parm.tflag11;
            tflag2 = parm.tflag12;
            tflag3 = parm.tflag13;
            tflag4 = parm.tflag14;
            tflag5 = parm.tflag15;
            guest_id1 = parm.guest_id11;
            guest_id2 = parm.guest_id12;
            guest_id3 = parm.guest_id13;
            guest_id4 = parm.guest_id14;
            guest_id5 = parm.guest_id15;
            orig1 = (!player1.equals("") ? user : "");
            orig2 = (!player2.equals("") ? user : "");
            orig3 = (!player3.equals("") ? user : "");
            orig4 = (!player4.equals("") ? user : "");
            orig5 = (!player5.equals("") ? user : "");

         } else {

            player1 = parm.player9;
            player2 = parm.player10;
            player3 = parm.player11;
            player4 = parm.player12;
            user1 = parm.user9;
            user2 = parm.user10;
            user3 = parm.user11;
            user4 = parm.user12;
            p1cw = parm.pcw9;
            p2cw = parm.pcw10;
            p3cw = parm.pcw11;
            p4cw = parm.pcw12;
            mNum1 = parm.mNum9;
            mNum2 = parm.mNum10;
            mNum3 = parm.mNum11;
            mNum4 = parm.mNum12;
            userg1 = parm.userg[8];
            userg2 = parm.userg[9];
            userg3 = parm.userg[10];
            userg4 = parm.userg[11];
            hndcp1 = parm.hndcp9;
            hndcp2 = parm.hndcp10;
            hndcp3 = parm.hndcp11;
            hndcp4 = parm.hndcp12;
            p91 = parm.p99;
            p92 = parm.p910;
            p93 = parm.p911;
            p94 = parm.p912;
            show1 = (parm.precheckin == 1 && parm.show9 == 0) ? 2 : parm.show9;
            show2 = (parm.precheckin == 1 && parm.show10 == 0) ? 2 : parm.show10;
            show3 = (parm.precheckin == 1 && parm.show11 == 0) ? 2 : parm.show11;
            show4 = (parm.precheckin == 1 && parm.show12 == 0) ? 2 : parm.show12;
            custom_disp1 = parm.custom_disp9;
            custom_disp2 = parm.custom_disp10;
            custom_disp3 = parm.custom_disp11;
            custom_disp4 = parm.custom_disp12;
            tflag1 = parm.tflag9;
            tflag2 = parm.tflag10;
            tflag3 = parm.tflag11;
            tflag4 = parm.tflag12;
            guest_id1 = parm.guest_id9;
            guest_id2 = parm.guest_id10;
            guest_id3 = parm.guest_id11;
            guest_id4 = parm.guest_id12;
            orig1 = (!player1.equals("") ? user : "");
            orig2 = (!player2.equals("") ? user : "");
            orig3 = (!player3.equals("") ? user : "");
            orig4 = (!player4.equals("") ? user : "");

            player5 = "";
            user5 = "";
            p5cw = "";
            mNum5 = "";
            userg5 = "";
            hndcp5 = 0;
            p95 = 0;
            show5 = 0;
            custom_disp5 = "";
            tflag5 = "";
            guest_id5 = 0;
            orig5 = "";
         }

         pstmt6 = con.prepareStatement (
            "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
            "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
            "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
            "hndcp4 = ?, show1 = ?, show2 = ?, show3 = ?, show4 = ?, player5 = ?, username5 = ?, " +
            "p5cw = ?, hndcp5 = ?, show5 = ?, notes = ?, proNew = ?, memNew = ?, " +
            "mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
            "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, " +
            "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, " +
            "custom_disp1 = ?, custom_disp2 = ?, custom_disp3 = ?, custom_disp4 = ?, custom_disp5 = ?, custom_int = ?, " +
            "tflag1 = ?, tflag2 = ?, tflag3 = ?, tflag4 = ?, tflag5 = ?, " +
            "guest_id1 = ?, guest_id2 = ?, guest_id3 = ?, guest_id4 = ?, guest_id5 = ?, " +
            "orig1 = ?, orig2 = ?, orig3 = ?, orig4 = ?, orig5 = ? " +
            "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt6.clearParameters();        // clear the parms
         pstmt6.setString(1, player1);
         pstmt6.setString(2, player2);
         pstmt6.setString(3, player3);
         pstmt6.setString(4, player4);
         pstmt6.setString(5, user1);
         pstmt6.setString(6, user2);
         pstmt6.setString(7, user3);
         pstmt6.setString(8, user4);
         pstmt6.setString(9, p1cw);
         pstmt6.setString(10, p2cw);
         pstmt6.setString(11, p3cw);
         pstmt6.setString(12, p4cw);
         pstmt6.setFloat(13, hndcp1);
         pstmt6.setFloat(14, hndcp2);
         pstmt6.setFloat(15, hndcp3);
         pstmt6.setFloat(16, hndcp4);
         pstmt6.setShort(17, show1);
         pstmt6.setShort(18, show2);
         pstmt6.setShort(19, show3);
         pstmt6.setShort(20, show4);
         pstmt6.setString(21, player5);
         pstmt6.setString(22, user5);
         pstmt6.setString(23, p5cw);
         pstmt6.setFloat(24, hndcp5);
         pstmt6.setShort(25, show5);
         pstmt6.setString(26, omit);
         pstmt6.setInt(27, proNew);
         pstmt6.setInt(28, memNew);
         pstmt6.setString(29, mNum1);
         pstmt6.setString(30, mNum2);
         pstmt6.setString(31, mNum3);
         pstmt6.setString(32, mNum4);
         pstmt6.setString(33, mNum5);
         pstmt6.setString(34, userg1);
         pstmt6.setString(35, userg2);
         pstmt6.setString(36, userg3);
         pstmt6.setString(37, userg4);
         pstmt6.setString(38, userg5);
         pstmt6.setString(39, parm.orig_by);
         pstmt6.setInt(40, p91);
         pstmt6.setInt(41, p92);
         pstmt6.setInt(42, p93);
         pstmt6.setInt(43, p94);
         pstmt6.setInt(44, p95);
         pstmt6.setString(45, custom_disp1);
         pstmt6.setString(46, custom_disp2);
         pstmt6.setString(47, custom_disp3);
         pstmt6.setString(48, custom_disp4);
         pstmt6.setString(49, custom_disp5);
         pstmt6.setInt(50, parm.custom_int3);
         pstmt6.setString(51, tflag1);
         pstmt6.setString(52, tflag2);
         pstmt6.setString(53, tflag3);
         pstmt6.setString(54, tflag4);
         pstmt6.setString(55, tflag5);
         pstmt6.setInt(56, guest_id1);
         pstmt6.setInt(57, guest_id2);
         pstmt6.setInt(58, guest_id3);
         pstmt6.setInt(59, guest_id4);
         pstmt6.setInt(60, guest_id5);
         pstmt6.setString(61, orig1);
         pstmt6.setString(62, orig2);
         pstmt6.setString(63, orig3);
         pstmt6.setString(64, orig4);
         pstmt6.setString(65, orig5);

         pstmt6.setLong(66, parm.date);
         pstmt6.setInt(67, parm.time3);
         pstmt6.setInt(68, parm.fb);
         pstmt6.setString(69, parm.course);
         pstmt6.executeUpdate();      // execute the prepared stmt

         pstmt6.close();

         if (!player1.equals( "" ) || !player2.equals( "" ) || !player3.equals( "" ) || !player4.equals( "" ) ||
             !player5.equals( "" )) {

            newTimes++;               // count number of tee times added
         }
      }

      //
      //  Add 4th tee time, if present
      //
      if (count > 3) {

         if (parm.p5.equals( "Yes" )) {

            player1 = parm.player16;
            player2 = parm.player17;
            player3 = parm.player18;
            player4 = parm.player19;
            player5 = parm.player20;
            user1 = parm.user16;
            user2 = parm.user17;
            user3 = parm.user18;
            user4 = parm.user19;
            user5 = parm.user20;
            p1cw = parm.pcw16;
            p2cw = parm.pcw17;
            p3cw = parm.pcw18;
            p4cw = parm.pcw19;
            p5cw = parm.pcw20;
            mNum1 = parm.mNum16;
            mNum2 = parm.mNum17;
            mNum3 = parm.mNum18;
            mNum4 = parm.mNum19;
            mNum5 = parm.mNum20;
            userg1 = parm.userg[15];
            userg2 = parm.userg[16];
            userg3 = parm.userg[17];
            userg4 = parm.userg[18];
            userg5 = parm.userg[19];
            hndcp1 = parm.hndcp16;
            hndcp2 = parm.hndcp17;
            hndcp3 = parm.hndcp18;
            hndcp4 = parm.hndcp19;
            hndcp5 = parm.hndcp20;
            p91 = parm.p916;
            p92 = parm.p917;
            p93 = parm.p918;
            p94 = parm.p919;
            p95 = parm.p920;
            show1 = (parm.precheckin == 1 && parm.show16 == 0) ? 2 : parm.show16;
            show2 = (parm.precheckin == 1 && parm.show17 == 0) ? 2 : parm.show17;
            show3 = (parm.precheckin == 1 && parm.show18 == 0) ? 2 : parm.show18;
            show4 = (parm.precheckin == 1 && parm.show19 == 0) ? 2 : parm.show19;
            show5 = (parm.precheckin == 1 && parm.show20 == 0) ? 2 : parm.show20;
            custom_disp1 = parm.custom_disp16;
            custom_disp2 = parm.custom_disp17;
            custom_disp3 = parm.custom_disp18;
            custom_disp4 = parm.custom_disp19;
            custom_disp5 = parm.custom_disp20;
            tflag1 = parm.tflag16;
            tflag2 = parm.tflag17;
            tflag3 = parm.tflag18;
            tflag4 = parm.tflag19;
            tflag5 = parm.tflag20;
            guest_id1 = parm.guest_id16;
            guest_id2 = parm.guest_id17;
            guest_id3 = parm.guest_id18;
            guest_id4 = parm.guest_id19;
            guest_id5 = parm.guest_id20;
            orig1 = (!player1.equals("") ? user : "");
            orig2 = (!player2.equals("") ? user : "");
            orig3 = (!player3.equals("") ? user : "");
            orig4 = (!player4.equals("") ? user : "");
            orig5 = (!player5.equals("") ? user : "");

         } else {

            player1 = parm.player13;
            player2 = parm.player14;
            player3 = parm.player15;
            player4 = parm.player16;
            user1 = parm.user13;
            user2 = parm.user14;
            user3 = parm.user15;
            user4 = parm.user16;
            p1cw = parm.pcw13;
            p2cw = parm.pcw14;
            p3cw = parm.pcw15;
            p4cw = parm.pcw16;
            mNum1 = parm.mNum13;
            mNum2 = parm.mNum14;
            mNum3 = parm.mNum15;
            mNum4 = parm.mNum16;
            userg1 = parm.userg[12];
            userg2 = parm.userg[13];
            userg3 = parm.userg[14];
            userg4 = parm.userg[15];
            hndcp1 = parm.hndcp13;
            hndcp2 = parm.hndcp14;
            hndcp3 = parm.hndcp15;
            hndcp4 = parm.hndcp16;
            p91 = parm.p913;
            p92 = parm.p914;
            p93 = parm.p915;
            p94 = parm.p916;
            show1 = (parm.precheckin == 1 && parm.show13 == 0) ? 2 : parm.show13;
            show2 = (parm.precheckin == 1 && parm.show14 == 0) ? 2 : parm.show14;
            show3 = (parm.precheckin == 1 && parm.show15 == 0) ? 2 : parm.show15;
            show4 = (parm.precheckin == 1 && parm.show16 == 0) ? 2 : parm.show16;
            custom_disp1 = parm.custom_disp13;
            custom_disp2 = parm.custom_disp14;
            custom_disp3 = parm.custom_disp15;
            custom_disp4 = parm.custom_disp16;
            tflag1 = parm.tflag13;
            tflag2 = parm.tflag14;
            tflag3 = parm.tflag15;
            tflag4 = parm.tflag16;
            guest_id1 = parm.guest_id13;
            guest_id2 = parm.guest_id14;
            guest_id3 = parm.guest_id15;
            guest_id4 = parm.guest_id16;
            orig1 = (!player1.equals("") ? user : "");
            orig2 = (!player2.equals("") ? user : "");
            orig3 = (!player3.equals("") ? user : "");
            orig4 = (!player4.equals("") ? user : "");

            player5 = "";
            user5 = "";
            p5cw = "";
            mNum5 = "";
            userg5 = "";
            hndcp5 = 0;
            p95 = 0;
            show5 = 0;
            custom_disp5 = "";
            tflag5 = "";
            guest_id5 = 0;
            orig5 = "";
         }

         pstmt6 = con.prepareStatement (
            "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
            "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
            "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
            "hndcp4 = ?, show1 = ?, show2 = ?, show3 = ?, show4 = ?, player5 = ?, username5 = ?, " +
            "p5cw = ?, hndcp5 = ?, show5 = ?, notes = ?, proNew = ?, memNew = ?, " +
            "mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
            "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, " +
            "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, " +
            "custom_disp1 = ?, custom_disp2 = ?, custom_disp3 = ?, custom_disp4 = ?, custom_disp5 = ?, custom_int = ?, " +
            "tflag1 = ?, tflag2 = ?, tflag3 = ?, tflag4 = ?, tflag5 = ?, " +
            "guest_id1 = ?, guest_id2 = ?, guest_id3 = ?, guest_id4 = ?, guest_id5 = ?, " +
            "orig1 = ?, orig2 = ?, orig3 = ?, orig4 = ?, orig5 = ? " +
            "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt6.clearParameters();        // clear the parms
         pstmt6.setString(1, player1);
         pstmt6.setString(2, player2);
         pstmt6.setString(3, player3);
         pstmt6.setString(4, player4);
         pstmt6.setString(5, user1);
         pstmt6.setString(6, user2);
         pstmt6.setString(7, user3);
         pstmt6.setString(8, user4);
         pstmt6.setString(9, p1cw);
         pstmt6.setString(10, p2cw);
         pstmt6.setString(11, p3cw);
         pstmt6.setString(12, p4cw);
         pstmt6.setFloat(13, hndcp1);
         pstmt6.setFloat(14, hndcp2);
         pstmt6.setFloat(15, hndcp3);
         pstmt6.setFloat(16, hndcp4);
         pstmt6.setShort(17, show1);
         pstmt6.setShort(18, show2);
         pstmt6.setShort(19, show3);
         pstmt6.setShort(20, show4);
         pstmt6.setString(21, player5);
         pstmt6.setString(22, user5);
         pstmt6.setString(23, p5cw);
         pstmt6.setFloat(24, hndcp5);
         pstmt6.setShort(25, show5);
         pstmt6.setString(26, omit);
         pstmt6.setInt(27, proNew);
         pstmt6.setInt(28, memNew);
         pstmt6.setString(29, mNum1);
         pstmt6.setString(30, mNum2);
         pstmt6.setString(31, mNum3);
         pstmt6.setString(32, mNum4);
         pstmt6.setString(33, mNum5);
         pstmt6.setString(34, userg1);
         pstmt6.setString(35, userg2);
         pstmt6.setString(36, userg3);
         pstmt6.setString(37, userg4);
         pstmt6.setString(38, userg5);
         pstmt6.setString(39, parm.orig_by);
         pstmt6.setInt(40, p91);
         pstmt6.setInt(41, p92);
         pstmt6.setInt(42, p93);
         pstmt6.setInt(43, p94);
         pstmt6.setInt(44, p95);
         pstmt6.setString(45, custom_disp1);
         pstmt6.setString(46, custom_disp2);
         pstmt6.setString(47, custom_disp3);
         pstmt6.setString(48, custom_disp4);
         pstmt6.setString(49, custom_disp5);
         pstmt6.setInt(50, parm.custom_int4);
         pstmt6.setString(51, tflag1);
         pstmt6.setString(52, tflag2);
         pstmt6.setString(53, tflag3);
         pstmt6.setString(54, tflag4);
         pstmt6.setString(55, tflag5);
         pstmt6.setInt(56, guest_id1);
         pstmt6.setInt(57, guest_id2);
         pstmt6.setInt(58, guest_id3);
         pstmt6.setInt(59, guest_id4);
         pstmt6.setInt(60, guest_id5);
         pstmt6.setString(61, orig1);
         pstmt6.setString(62, orig2);
         pstmt6.setString(63, orig3);
         pstmt6.setString(64, orig4);
         pstmt6.setString(65, orig5);

         pstmt6.setLong(66, parm.date);
         pstmt6.setInt(67, parm.time4);
         pstmt6.setInt(68, parm.fb);
         pstmt6.setString(69, parm.course);
         pstmt6.executeUpdate();      // execute the prepared stmt

         pstmt6.close();

         if (!player1.equals( "" ) || !player2.equals( "" ) || !player3.equals( "" ) || !player4.equals( "" ) ||
             !player5.equals( "" )) {

            newTimes++;               // count number of tee times added
         }
      }

      //
      //  Add 5th tee time, if present
      //
      if (count > 4) {

         if (parm.p5.equals( "Yes" )) {

            player1 = parm.player21;
            player2 = parm.player22;
            player3 = parm.player23;
            player4 = parm.player24;
            player5 = parm.player25;
            user1 = parm.user21;
            user2 = parm.user22;
            user3 = parm.user23;
            user4 = parm.user24;
            user5 = parm.user25;
            p1cw = parm.pcw21;
            p2cw = parm.pcw22;
            p3cw = parm.pcw23;
            p4cw = parm.pcw24;
            p5cw = parm.pcw25;
            mNum1 = parm.mNum21;
            mNum2 = parm.mNum22;
            mNum3 = parm.mNum23;
            mNum4 = parm.mNum24;
            mNum5 = parm.mNum25;
            userg1 = parm.userg[20];
            userg2 = parm.userg[21];
            userg3 = parm.userg[22];
            userg4 = parm.userg[23];
            userg5 = parm.userg[24];
            hndcp1 = parm.hndcp21;
            hndcp2 = parm.hndcp22;
            hndcp3 = parm.hndcp23;
            hndcp4 = parm.hndcp24;
            hndcp5 = parm.hndcp25;
            p91 = parm.p921;
            p92 = parm.p922;
            p93 = parm.p923;
            p94 = parm.p924;
            p95 = parm.p925;
            show1 = (parm.precheckin == 1 && parm.show21 == 0) ? 2 : parm.show21;
            show2 = (parm.precheckin == 1 && parm.show22 == 0) ? 2 : parm.show22;
            show3 = (parm.precheckin == 1 && parm.show23 == 0) ? 2 : parm.show23;
            show4 = (parm.precheckin == 1 && parm.show24 == 0) ? 2 : parm.show24;
            show5 = (parm.precheckin == 1 && parm.show25 == 0) ? 2 : parm.show25;
            custom_disp1 = parm.custom_disp21;
            custom_disp2 = parm.custom_disp22;
            custom_disp3 = parm.custom_disp23;
            custom_disp4 = parm.custom_disp24;
            custom_disp5 = parm.custom_disp25;
            tflag1 = parm.tflag21;
            tflag2 = parm.tflag22;
            tflag3 = parm.tflag23;
            tflag4 = parm.tflag24;
            tflag5 = parm.tflag25;
            guest_id1 = parm.guest_id21;
            guest_id2 = parm.guest_id22;
            guest_id3 = parm.guest_id23;
            guest_id4 = parm.guest_id24;
            guest_id5 = parm.guest_id25;
            orig1 = (!player1.equals("") ? user : "");
            orig2 = (!player2.equals("") ? user : "");
            orig3 = (!player3.equals("") ? user : "");
            orig4 = (!player4.equals("") ? user : "");
            orig5 = (!player5.equals("") ? user : "");

         } else {

            player1 = parm.player17;
            player2 = parm.player18;
            player3 = parm.player19;
            player4 = parm.player20;
            user1 = parm.user17;
            user2 = parm.user18;
            user3 = parm.user19;
            user4 = parm.user20;
            p1cw = parm.pcw17;
            p2cw = parm.pcw18;
            p3cw = parm.pcw19;
            p4cw = parm.pcw20;
            mNum1 = parm.mNum17;
            mNum2 = parm.mNum18;
            mNum3 = parm.mNum19;
            mNum4 = parm.mNum20;
            userg1 = parm.userg[16];
            userg2 = parm.userg[17];
            userg3 = parm.userg[18];
            userg4 = parm.userg[19];
            hndcp1 = parm.hndcp17;
            hndcp2 = parm.hndcp18;
            hndcp3 = parm.hndcp19;
            hndcp4 = parm.hndcp20;
            p91 = parm.p917;
            p92 = parm.p918;
            p93 = parm.p919;
            p94 = parm.p920;
            show1 = (parm.precheckin == 1 && parm.show17 == 0) ? 2 : parm.show17;
            show2 = (parm.precheckin == 1 && parm.show18 == 0) ? 2 : parm.show18;
            show3 = (parm.precheckin == 1 && parm.show19 == 0) ? 2 : parm.show19;
            show4 = (parm.precheckin == 1 && parm.show20 == 0) ? 2 : parm.show20;
            custom_disp1 = parm.custom_disp17;
            custom_disp2 = parm.custom_disp18;
            custom_disp3 = parm.custom_disp19;
            custom_disp4 = parm.custom_disp20;
            tflag1 = parm.tflag17;
            tflag2 = parm.tflag18;
            tflag3 = parm.tflag19;
            tflag4 = parm.tflag20;
            guest_id1 = parm.guest_id17;
            guest_id2 = parm.guest_id18;
            guest_id3 = parm.guest_id19;
            guest_id4 = parm.guest_id20;
            orig1 = (!player1.equals("") ? user : "");
            orig2 = (!player2.equals("") ? user : "");
            orig3 = (!player3.equals("") ? user : "");
            orig4 = (!player4.equals("") ? user : "");

            player5 = "";
            user5 = "";
            p5cw = "";
            mNum5 = "";
            userg5 = "";
            hndcp5 = 0;
            p95 = 0;
            show5 = 0;
            custom_disp5 = "";
            tflag5 = "";
            guest_id5 = 0;
            orig5 = "";
         }

         pstmt6 = con.prepareStatement (
            "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
            "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
            "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
            "hndcp4 = ?, show1 = ?, show2 = ?, show3 = ?, show4 = ?, player5 = ?, username5 = ?, " +
            "p5cw = ?, hndcp5 = ?, show5 = ?, notes = ?, proNew = ?, memNew = ?, " +
            "mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
            "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, " +
            "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, " +
            "custom_disp1 = ?, custom_disp2 = ?, custom_disp3 = ?, custom_disp4 = ?, custom_disp5 = ?, custom_int = ?, " +
            "tflag1 = ?, tflag2 = ?, tflag3 = ?, tflag4 = ?, tflag5 = ?, " +
            "guest_id1 = ?, guest_id2 = ?, guest_id3 = ?, guest_id4 = ?, guest_id5 = ?, " +
            "orig1 = ?, orig2 = ?, orig3 = ?, orig4 = ?, orig5 = ? " +
            "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt6.clearParameters();        // clear the parms
         pstmt6.setString(1, player1);
         pstmt6.setString(2, player2);
         pstmt6.setString(3, player3);
         pstmt6.setString(4, player4);
         pstmt6.setString(5, user1);
         pstmt6.setString(6, user2);
         pstmt6.setString(7, user3);
         pstmt6.setString(8, user4);
         pstmt6.setString(9, p1cw);
         pstmt6.setString(10, p2cw);
         pstmt6.setString(11, p3cw);
         pstmt6.setString(12, p4cw);
         pstmt6.setFloat(13, hndcp1);
         pstmt6.setFloat(14, hndcp2);
         pstmt6.setFloat(15, hndcp3);
         pstmt6.setFloat(16, hndcp4);
         pstmt6.setShort(17, show1);
         pstmt6.setShort(18, show2);
         pstmt6.setShort(19, show3);
         pstmt6.setShort(20, show4);
         pstmt6.setString(21, player5);
         pstmt6.setString(22, user5);
         pstmt6.setString(23, p5cw);
         pstmt6.setFloat(24, hndcp5);
         pstmt6.setShort(25, show5);
         pstmt6.setString(26, omit);
         pstmt6.setInt(27, proNew);
         pstmt6.setInt(28, memNew);
         pstmt6.setString(29, mNum1);
         pstmt6.setString(30, mNum2);
         pstmt6.setString(31, mNum3);
         pstmt6.setString(32, mNum4);
         pstmt6.setString(33, mNum5);
         pstmt6.setString(34, userg1);
         pstmt6.setString(35, userg2);
         pstmt6.setString(36, userg3);
         pstmt6.setString(37, userg4);
         pstmt6.setString(38, userg5);
         pstmt6.setString(39, parm.orig_by);
         pstmt6.setInt(40, p91);
         pstmt6.setInt(41, p92);
         pstmt6.setInt(42, p93);
         pstmt6.setInt(43, p94);
         pstmt6.setInt(44, p95);
         pstmt6.setString(45, custom_disp1);
         pstmt6.setString(46, custom_disp2);
         pstmt6.setString(47, custom_disp3);
         pstmt6.setString(48, custom_disp4);
         pstmt6.setString(49, custom_disp5);
         pstmt6.setInt(50, parm.custom_int5);
         pstmt6.setString(51, tflag1);
         pstmt6.setString(52, tflag2);
         pstmt6.setString(53, tflag3);
         pstmt6.setString(54, tflag4);
         pstmt6.setString(55, tflag5);
         pstmt6.setInt(56, guest_id1);
         pstmt6.setInt(57, guest_id2);
         pstmt6.setInt(58, guest_id3);
         pstmt6.setInt(59, guest_id4);
         pstmt6.setInt(60, guest_id5);
         pstmt6.setString(61, orig1);
         pstmt6.setString(62, orig2);
         pstmt6.setString(63, orig3);
         pstmt6.setString(64, orig4);
         pstmt6.setString(65, orig5);

         pstmt6.setLong(66, parm.date);
         pstmt6.setInt(67, parm.time5);
         pstmt6.setInt(68, parm.fb);
         pstmt6.setString(69, parm.course);
         pstmt6.executeUpdate();      // execute the prepared stmt

         pstmt6.close();

         if (!player1.equals( "" ) || !player2.equals( "" ) || !player3.equals( "" ) || !player4.equals( "" ) ||
             !player5.equals( "" )) {

            newTimes++;               // count number of tee times added
         }
      }

   }
   catch (Exception e) {

      throw new Exception("Error adding new tee times - verifySlot.addMteeTime " + e.getMessage());
   }

   return(newTimes);

 }


/**
 //************************************************************************
 //
 //  Hazeltine Custom Processing
 //
 //  Htoggle - clear the in_use flag of the associated tee time, if it exists
 //            Also, if not a cancel, set the assoc tee time to a cross-over.
 //            If cancel, then return the assoc tee time to its original tee.
 //
 //     This is done when a tee time is added, changed or cancelled.
 //
 //************************************************************************
 **/

 public static void Htoggle(long date, int time, int fb, parmSlot slotParms, Connection con) {


   int found = 0;
   int cancel = 0;

   //
   //  Add custom processing for Hazeltine National
   //
   //    If this is a double tee, then we must also free its associated time and returns its original tee.
   //
   // if weekend or holiday
   if (slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday") ||
       date == Hdate1 || date == Hdate2 || date == Hdate3) {

      if (slotParms.player1.equals( "" ) && slotParms.player2.equals( "" ) && slotParms.player3.equals( "" ) &&
          slotParms.player4.equals( "" ) && slotParms.player5.equals( "" )) {

         cancel = 1;      // no players, must be cancel
      }

      //
      // Related Times:   8:00 B <--> 10:10 F
      //                  8:09 B <--> 10:19 F
      //                  8:18 B <--> 10:28 F
      //                  8:27 B <--> 10:37 F
      //                  8:36 B <--> 10:46 F
      //                  8:45 B <--> 10:55 F
      //
      if (time == 800 && fb == 1) {    // if 8:00 AM Back Tee
         found = 1;                    // found one
         time = 1010;                  // associated time
         fb = 0;                       // asscoiated tee
         if (cancel == 0) {
            fb = 9;                       // make it a cross-over if not a cancel request
         }
      } else {
        if (time == 809 && fb == 1) {    // if 8:09 AM Back Tee
           found = 1;
           time = 1019;
           fb = 0;
           if (cancel == 0) {
              fb = 9;                       // make it a cross-over if not a cancel request
           }
        } else {
          if (time == 818 && fb == 1) {    // if 8:18 AM Back Tee
             found = 1;
             time = 1028;
             fb = 0;
             if (cancel == 0) {
                fb = 9;                       // make it a cross-over if not a cancel request
             }
          } else {
            if (time == 827 && fb == 1) {    // if 8:27 AM Back Tee
               found = 1;
               time = 1037;
               fb = 0;
               if (cancel == 0) {
                  fb = 9;                       // make it a cross-over if not a cancel request
               }
            } else {
              if (time == 836 && fb == 1) {    // if 8:36 AM Back Tee
                 found = 1;
                 time = 1046;
                 fb = 0;
                 if (cancel == 0) {
                    fb = 9;                       // make it a cross-over if not a cancel request
                 }
              } else {
                if (time == 845 && fb == 1) {    // if 8:45 AM Back Tee
                   found = 1;
                   time = 1055;
                   fb = 0;
                   if (cancel == 0) {
                      fb = 9;                       // make it a cross-over if not a cancel request
                   }
                } else {

                  if (time == 1010 && fb == 0) {    // if 10:10 AM Front Tee
                     found = 1;
                     time = 800;
                     fb = 1;
                     if (cancel == 0) {
                        fb = 9;                       // make it a cross-over if not a cancel request
                     }
                  } else {
                    if (time == 1019 && fb == 0) {    // if 10:19 AM Front Tee
                       found = 1;
                       time = 809;
                       fb = 1;
                       if (cancel == 0) {
                          fb = 9;                       // make it a cross-over if not a cancel request
                       }
                    } else {
                      if (time == 1028 && fb == 0) {    // if 10:28 AM Front Tee
                         found = 1;
                         time = 818;
                         fb = 1;
                         if (cancel == 0) {
                            fb = 9;                       // make it a cross-over if not a cancel request
                         }
                      } else {
                        if (time == 1037 && fb == 0) {    // if 10:37 AM Front Tee
                           found = 1;
                           time = 827;
                           fb = 1;
                           if (cancel == 0) {
                              fb = 9;                       // make it a cross-over if not a cancel request
                           }
                        } else {
                          if (time == 1046 && fb == 0) {    // if 10:46 AM Front Tee
                             found = 1;
                             time = 836;
                             fb = 1;
                             if (cancel == 0) {
                                fb = 9;                       // make it a cross-over if not a cancel request
                             }
                          } else {
                            if (time == 1055 && fb == 0) {    // if 10:55 AM Front Tee
                               found = 1;
                               time = 845;
                               fb = 1;
                               if (cancel == 0) {
                                  fb = 9;                       // make it a cross-over if not a cancel request
                               }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
      //
      //  if we hit on a time, then we must free the associated time and change its tee
      //
      if (found == 1) {

         try {

            PreparedStatement pstmt1 = con.prepareStatement (
               "UPDATE teecurr2 SET in_use = 0, fb = ? WHERE date = ? AND time = ? AND courseName = ?");

            pstmt1.clearParameters();          // clear the parms
            pstmt1.setInt(1, fb);
            pstmt1.setLong(2, date);
            pstmt1.setInt(3, time);
            pstmt1.setString(4, slotParms.course);
            pstmt1.executeUpdate();            // execute the prepared stmt

            pstmt1.close();

         }
         catch (Exception e) {
         }
      }
   }
   return;
 }


/**
 //************************************************************************
 //
 //  Hazeltine Custom Processing
 //
 //  HclearInUse - clear the in_use flag of the associated tee time, if it exists
 //
 //     This is done when a tee time request is cancelled. (Return w/o Changes)
 //
 //************************************************************************
 **/

 public static void HclearInUse(long date, int time, int fb, String course, String day, Connection con) {


   int found = 0;

   //
   //  Add custom processing for Hazeltine National
   //
   //    If this is a double tee, then we must also free its associated time on the opposite tee.
   //
   // if weekend or holiday
   if (day.equals("Saturday") || day.equals("Sunday") ||
       date == Hdate1 || date == Hdate2 || date == Hdate3) {

      found = 0;     // default to not double tee

      //
      // Related Times:   8:00 B <--> 10:10 F
      //                  8:09 B <--> 10:19 F
      //                  8:18 B <--> 10:28 F
      //                  8:27 B <--> 10:37 F
      //                  8:36 B <--> 10:46 F
      //                  8:45 B <--> 10:55 F
      //
      if (time == 800 && fb == 1) {    // if 8:00 AM Back Tee
         found = 1;                    // found one
         time = 1010;                  // associated time
         fb = 0;                       // asscoiated tee
      } else {
        if (time == 809 && fb == 1) {    // if 8:09 AM Back Tee
           found = 1;
           time = 1019;
           fb = 0;
        } else {
          if (time == 818 && fb == 1) {    // if 8:18 AM Back Tee
             found = 1;
             time = 1028;
             fb = 0;
          } else {
            if (time == 827 && fb == 1) {    // if 8:27 AM Back Tee
               found = 1;
               time = 1037;
               fb = 0;
            } else {
              if (time == 836 && fb == 1) {    // if 8:36 AM Back Tee
                 found = 1;
                 time = 1046;
                 fb = 0;
              } else {
                if (time == 845 && fb == 1) {    // if 8:45 AM Back Tee
                   found = 1;
                   time = 1055;
                   fb = 0;
                } else {

                  if (time == 1010 && fb == 0) {    // if 10:10 AM Front Tee
                     found = 1;
                     time = 800;
                     fb = 1;
                  } else {
                    if (time == 1019 && fb == 0) {    // if 10:19 AM Front Tee
                       found = 1;
                       time = 809;
                       fb = 1;
                    } else {
                      if (time == 1028 && fb == 0) {    // if 10:28 AM Front Tee
                         found = 1;
                         time = 818;
                         fb = 1;
                      } else {
                        if (time == 1037 && fb == 0) {    // if 10:37 AM Front Tee
                           found = 1;
                           time = 827;
                           fb = 1;
                        } else {
                          if (time == 1046 && fb == 0) {    // if 10:46 AM Front Tee
                             found = 1;
                             time = 836;
                             fb = 1;
                          } else {
                            if (time == 1055 && fb == 0) {    // if 10:55 AM Front Tee
                               found = 1;
                               time = 845;
                               fb = 1;
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
      //
      //  if we hit on a time, then we must lock out the associated time (should be free if assoc is)
      //
      if (found == 1) {

         try {

            PreparedStatement pstmt1 = con.prepareStatement (
               "UPDATE teecurr2 SET in_use = 0 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

            pstmt1.clearParameters();          // clear the parms
            pstmt1.setLong(1, date);
            pstmt1.setInt(2, time);
            pstmt1.setInt(3, fb);
            pstmt1.setString(4, course);
            pstmt1.executeUpdate();            // execute the prepared stmt

            pstmt1.close();

         }
         catch (Exception e) {
         }
      }
   }
   return;
 }


/**
 //************************************************************************
 //
 //  shiftUp - shifts players so they start in position 1
 //
 //************************************************************************
 **/

 public static void shiftUp(parmSlot slotParms) {


   if (slotParms.player1.equals( "" )) {    // if empty

      if (!slotParms.player2.equals( "" )) {    // if not empty

         slotParms.player1 = slotParms.player2;
         slotParms.p1cw = slotParms.p2cw;
         slotParms.show1 = slotParms.show2;
         slotParms.pos1 = slotParms.pos2;
         slotParms.p91 = slotParms.p92;
         slotParms.custom_disp1 = slotParms.custom_disp2;
         slotParms.guest_id1 = slotParms.guest_id2;
         slotParms.orig1 = slotParms.orig2;
         slotParms.player2 = "";
         slotParms.show2 = 0;
         slotParms.pos2 = 0;
         slotParms.p92 = 0;
         slotParms.custom_disp2 = "";
         slotParms.guest_id2 = 0;
         slotParms.orig2 = "";

      } else {

         if (!slotParms.player3.equals( "" )) {    // if not empty

            slotParms.player1 = slotParms.player3;
            slotParms.p1cw = slotParms.p3cw;
            slotParms.show1 = slotParms.show3;
            slotParms.pos1 = slotParms.pos3;
            slotParms.p91 = slotParms.p93;
            slotParms.custom_disp1 = slotParms.custom_disp3;
            slotParms.guest_id1 = slotParms.guest_id3;
            slotParms.orig1 = slotParms.orig3;
            slotParms.player3 = "";
            slotParms.show3 = 0;
            slotParms.pos3 = 0;
            slotParms.p93 = 0;
            slotParms.custom_disp3 = "";
            slotParms.guest_id3 = 0;
            slotParms.orig3 = "";

         } else {

            if (!slotParms.player4.equals( "" )) {    // if not empty

               slotParms.player1 = slotParms.player4;
               slotParms.p1cw = slotParms.p4cw;
               slotParms.show1 = slotParms.show4;
               slotParms.pos1 = slotParms.pos4;
               slotParms.p91 = slotParms.p94;
               slotParms.custom_disp1 = slotParms.custom_disp4;
               slotParms.guest_id1 = slotParms.guest_id4;
               slotParms.orig1 = slotParms.orig4;
               slotParms.player4 = "";
               slotParms.show4 = 0;
               slotParms.pos4 = 0;
               slotParms.p94 = 0;
               slotParms.custom_disp4 = "";
               slotParms.guest_id4 = 0;
               slotParms.orig4 = "";

            } else {

               if (!slotParms.player5.equals( "" )) {    // if not empty

                  slotParms.player1 = slotParms.player5;
                  slotParms.p1cw = slotParms.p5cw;
                  slotParms.show1 = slotParms.show5;
                  slotParms.pos1 = slotParms.pos5;
                  slotParms.p91 = slotParms.p95;
                  slotParms.custom_disp1 = slotParms.custom_disp5;
                  slotParms.guest_id1 = slotParms.guest_id5;
                  slotParms.orig1 = slotParms.orig5;
                  slotParms.player5 = "";
                  slotParms.show5 = 0;
                  slotParms.pos5 = 0;
                  slotParms.p95 = 0;
                  slotParms.custom_disp5 = "";
                  slotParms.guest_id5 = 0;
                  slotParms.orig5 = "";
               }
            }
         }
      }
   }
   if (slotParms.player2.equals( "" )) {    // if empty

      if (!slotParms.player3.equals( "" )) {    // if not empty

         slotParms.player2 = slotParms.player3;
         slotParms.p2cw = slotParms.p3cw;
         slotParms.show2 = slotParms.show3;
         slotParms.pos2 = slotParms.pos3;
         slotParms.p92 = slotParms.p93;
         slotParms.custom_disp2 = slotParms.custom_disp3;
         slotParms.guest_id2 = slotParms.guest_id3;
         slotParms.orig2 = slotParms.orig3;
         slotParms.player3 = "";
         slotParms.show3 = 0;
         slotParms.pos3 = 0;
         slotParms.p93 = 0;
         slotParms.custom_disp3 = "";
         slotParms.guest_id3 = 0;
         slotParms.orig3 = "";

      } else {

         if (!slotParms.player4.equals( "" )) {    // if not empty

            slotParms.player2 = slotParms.player4;
            slotParms.p2cw = slotParms.p4cw;
            slotParms.show2 = slotParms.show4;
            slotParms.pos2 = slotParms.pos4;
            slotParms.p92 = slotParms.p94;
            slotParms.custom_disp2 = slotParms.custom_disp4;
            slotParms.guest_id2 = slotParms.guest_id4;
            slotParms.orig2 = slotParms.orig4;
            slotParms.player4 = "";
            slotParms.show4 = 0;
            slotParms.pos4 = 0;
            slotParms.p94 = 0;
            slotParms.custom_disp4 = "";
            slotParms.guest_id4 = 0;
            slotParms.orig4 = "";

         } else {

            if (!slotParms.player5.equals( "" )) {    // if not empty

               slotParms.player2 = slotParms.player5;
               slotParms.p2cw = slotParms.p5cw;
               slotParms.show2 = slotParms.show5;
               slotParms.pos2 = slotParms.pos5;
               slotParms.p92 = slotParms.p95;
               slotParms.custom_disp2 = slotParms.custom_disp5;
               slotParms.guest_id2 = slotParms.guest_id5;
               slotParms.orig2 = slotParms.orig5;
               slotParms.player5 = "";
               slotParms.show5 = 0;
               slotParms.pos5 = 0;
               slotParms.p95 = 0;
               slotParms.custom_disp5 = "";
               slotParms.guest_id5 = 0;
               slotParms.orig5 = "";
            }
         }
      }
   }
   if (slotParms.player3.equals( "" )) {    // if empty

      if (!slotParms.player4.equals( "" )) {    // if not empty

         slotParms.player3 = slotParms.player4;
         slotParms.p3cw = slotParms.p4cw;
         slotParms.show3 = slotParms.show4;
         slotParms.pos3 = slotParms.pos4;
         slotParms.p93 = slotParms.p94;
         slotParms.custom_disp3 = slotParms.custom_disp4;
         slotParms.guest_id3 = slotParms.guest_id4;
         slotParms.orig3 = slotParms.orig4;
         slotParms.player4 = "";
         slotParms.show4 = 0;
         slotParms.pos4 = 0;
         slotParms.p94 = 0;
         slotParms.custom_disp4 = "";
         slotParms.guest_id4 = 0;
         slotParms.orig4 = "";

      } else {

         if (!slotParms.player5.equals( "" )) {    // if not empty

            slotParms.player3 = slotParms.player5;
            slotParms.p3cw = slotParms.p5cw;
            slotParms.show3 = slotParms.show5;
            slotParms.pos3 = slotParms.pos5;
            slotParms.p93 = slotParms.p95;
            slotParms.custom_disp3 = slotParms.custom_disp5;
            slotParms.guest_id3 = slotParms.guest_id5;
            slotParms.orig3 = slotParms.orig5;
            slotParms.player5 = "";
            slotParms.show5 = 0;
            slotParms.pos5 = 0;
            slotParms.p95 = 0;
            slotParms.custom_disp5 = "";
            slotParms.guest_id5 = 0;
            slotParms.orig5 = "";
         }
      }
   }
   if (slotParms.player4.equals( "" )) {    // if empty

      if (!slotParms.player5.equals( "" )) {    // if not empty

         slotParms.player4 = slotParms.player5;
         slotParms.p4cw = slotParms.p5cw;
         slotParms.show4 = slotParms.show5;
         slotParms.pos4 = slotParms.pos5;
         slotParms.p94 = slotParms.p95;
         slotParms.custom_disp4 = slotParms.custom_disp5;
         slotParms.guest_id4 = slotParms.guest_id5;
         slotParms.orig4 = slotParms.orig5;
         slotParms.player5 = "";
         slotParms.show5 = 0;
         slotParms.pos5 = 0;
         slotParms.p95 = 0;
         slotParms.custom_disp5 = "";
         slotParms.guest_id5 = 0;
         slotParms.orig5 = "";
      }
   }

 }


/**
 //************************************************************************
 //
 //  Parse the names to separate first, last & mi
 //
 //      sets:  last name
 //             first name
 //             middle initial
 //
 //************************************************************************
 **/

 public static boolean parseNames(parmSlot slotParms, String caller)
         throws Exception {


   boolean error = false;


   if ((!slotParms.player1.equals( "" )) && (slotParms.g1.equals( "" ))) {   // specified and not a guest

      slotParms.player = slotParms.player1;                               // save player name in case of an error

      StringTokenizer tok = new StringTokenizer( slotParms.player1 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         error = true;
         return(error);
      }

      if (caller.equalsIgnoreCase( "mem" )) {

         if (tok.countTokens() == 1 ) {    //  X or single names not allowed in player1

            error = true;
            return(error);
         }
      }

      if (caller.equalsIgnoreCase( "pro" )) {

         if ((tok.countTokens() == 1 ) && (!slotParms.player2.equalsIgnoreCase( "X"))) {    // if not X

            error = true;
            return(error);
         }
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         slotParms.fname1 = tok.nextToken();
         slotParms.lname1 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         slotParms.fname1 = tok.nextToken();
         slotParms.mi1 = tok.nextToken();
         slotParms.lname1 = tok.nextToken();
      }

      slotParms.guest_id1 = 0;
   }


   if ((!slotParms.player2.equals( "" )) && (slotParms.g2.equals( "" ))) {                  // specified but not guest

      slotParms.player = slotParms.player2;                               // save player name in case of an error

      StringTokenizer tok = new StringTokenizer( slotParms.player2 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         error = true;
         return(error);
      }

      if ((tok.countTokens() == 1 ) && (!slotParms.player2.equalsIgnoreCase( "X"))) {    // if not X

         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         slotParms.fname2 = tok.nextToken();
         slotParms.lname2 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         slotParms.fname2 = tok.nextToken();
         slotParms.mi2 = tok.nextToken();
         slotParms.lname2 = tok.nextToken();
      }

      slotParms.guest_id2 = 0;
   }

   if ((!slotParms.player3.equals( "" )) && (slotParms.g3.equals( "" ))) {                  // specified but not guest

      slotParms.player = slotParms.player3;                               // save player name in case of an error

      StringTokenizer tok = new StringTokenizer( slotParms.player3 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         error = true;
         return(error);
      }

      if ((tok.countTokens() == 1 ) && (!slotParms.player3.equalsIgnoreCase( "X"))) {    // if not X

         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         slotParms.fname3 = tok.nextToken();
         slotParms.lname3 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         slotParms.fname3 = tok.nextToken();
         slotParms.mi3 = tok.nextToken();
         slotParms.lname3 = tok.nextToken();
      }

      slotParms.guest_id3 = 0;
   }

   if ((!slotParms.player4.equals( "" )) && (slotParms.g4.equals( "" ))) {                  // specified but not guest

      slotParms.player = slotParms.player4;                               // save player name in case of an error

      StringTokenizer tok = new StringTokenizer( slotParms.player4 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         error = true;
         return(error);
      }

      if ((tok.countTokens() == 1 ) && (!slotParms.player4.equalsIgnoreCase( "X"))) {    // if not X

         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         slotParms.fname4 = tok.nextToken();
         slotParms.lname4 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         slotParms.fname4 = tok.nextToken();
         slotParms.mi4 = tok.nextToken();
         slotParms.lname4 = tok.nextToken();
      }

      slotParms.guest_id4 = 0;
   }

   if ((!slotParms.player5.equals( "" )) && (slotParms.g5.equals( "" ))) {                  // specified but not guest

      slotParms.player = slotParms.player5;                               // save player name in case of an error

      StringTokenizer tok = new StringTokenizer( slotParms.player5 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         error = true;
         return(error);
      }

      if ((tok.countTokens() == 1 ) && (!slotParms.player5.equalsIgnoreCase( "X"))) {    // if not X

         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         slotParms.fname5 = tok.nextToken();
         slotParms.lname5 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         slotParms.fname5 = tok.nextToken();
         slotParms.mi5 = tok.nextToken();
         slotParms.lname5 = tok.nextToken();
      }

      slotParms.guest_id5 = 0;
   }

   if (error == false) {

      slotParms.player = "";                   // clear if ok
   }

   return(error);

 }


/**
 //************************************************************************
 //
 //  Get Lottery Info
 //
 //      sets:  slotParms.lottery_type
 //
 //************************************************************************
 **/

 public static void getLottery(parmSlot slotParms, Connection con)
         throws Exception {


   PreparedStatement pstmt1 = null;
   ResultSet rs = null;


   //
   //   Get the lottery info for the lottery in slotParms
   //
   try {

      pstmt1 = con.prepareStatement (
         "SELECT type FROM lottery3 WHERE name = ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setString(1, slotParms.lottery);
      rs = pstmt1.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         slotParms.lottery_type = rs.getString(1);
      }
      pstmt1.close();

   }
   catch (Exception e) {

      throw new Exception("Error getting Lottery info - verifySlot.getLottery " + e.getMessage());
   }

   return;
 }


/**
 //************************************************************************
 //2
 //  Determine if player names are guests
 //
 //      sets:  slotParms.gX (X = 1 - 5) - guest type for player, if guest
 //             slotParms.guests (# of guests)
 //             slotParms.userg(1-5) - member username assoc'd with guest (Proshop only)
 //
 //************************************************************************
 **/

 public static void parseGuests(parmSlot slotParms, Connection con)
         throws Exception {


   PreparedStatement pstmt1 = null;
   //Statement stmt = null;
   ResultSet rs = null;

   int i = 0;
   int i2 = 0;

   boolean invalid = false;
   boolean isProshop = ProcessConstants.isProshopUser(slotParms.user);
   boolean guestdbTbaAllowed = false;

   StringTokenizer tok = null;
   String fname = "";
   String lname = "";
   String mi = "";
   String user = "";

   String club = slotParms.club;

   String [] oldPlayers = new String [5];    // array to hold the old player values

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(slotParms.root_activity_id, con);

   //
   //   Get the guest names specified for this club
   //
   try {

      //  If guest tracking is in use, determine whether names are optional or required
      if (Utilities.isGuestTrackingConfigured(slotParms.root_activity_id, con) && Utilities.isGuestTrackingTbaAllowed(slotParms.root_activity_id, isProshop, con)) {
          guestdbTbaAllowed = true;
      }

      //getClub.getParms(con, parm, getActivity.getParentIdFromActivityId(slotParms.activity_id, con));        // get the club parms
      getClub.getParms(con, parm, slotParms.root_activity_id);        // get the club parms
/*
      //
      //   Remove any guest types that are null - for tests below
      //
      i = 0;
      while (i < parm.MAX_Guests) {

         if (parm.guest[i].equals( "" )) {

            parm.guest[i] = "$@#!^&*";      // make so it won't match player name
         }
         i++;
      }         // end of while loop
*/
      //
      //  init guest parms
      //
      slotParms.g1 = "";
      slotParms.g2 = "";
      slotParms.g3 = "";
      slotParms.g4 = "";
      slotParms.g5 = "";
      slotParms.gstA[0] = "";
      slotParms.gstA[1] = "";
      slotParms.gstA[2] = "";
      slotParms.gstA[3] = "";
      slotParms.gstA[4] = "";
      slotParms.gplayer = "";
      slotParms.hit3 = false;

      oldPlayers[0] = slotParms.oldPlayer1;       // put original player values in array
      oldPlayers[1] = slotParms.oldPlayer2;
      oldPlayers[2] = slotParms.oldPlayer3;
      oldPlayers[3] = slotParms.oldPlayer4;
      oldPlayers[4] = slotParms.oldPlayer5;

      //
      //    Check if player is a guest
      //
      if (!slotParms.player1.equals( "" )) {

         i = 0;
         loop1:
         while (i < parm.MAX_Guests) {

            if (slotParms.player1.startsWith( parm.guest[i] )) {

               slotParms.g1 = parm.guest[i];             // indicate player is a guest name and save name
               slotParms.gstA[0] = slotParms.player1;    // save guest value
               slotParms.guests++;                       // increment number of guests this slot

               if (parm.gOpt[i] > 0 && !slotParms.user.startsWith( "proshop" )) {  // if Proshop-only guest type & member user

                  invalid = true;                        // default to invalid guest type
                  i2 = 0;
                  loopg1:
                  while (i2 < 5) {

                     if (slotParms.player1.equals( oldPlayers[i2] )) {   // if already approved

                        invalid = false;          // ok to skip this guest
                        break loopg1;             // exit loop
                     }
                     i2++;
                  }

                  if (invalid == true) {                       // if new or changed player name

                     slotParms.gplayer = slotParms.player1;    // indicate error (ok if it was already entered by pro)
                  }
               }

               //
               //  Check for guest name if requested for this club
               //
               if (invalid == false) {

                  if (parm.gDb[i] == 1) {

                      if (!guestdbTbaAllowed || slotParms.guest_id1 != 0 || !slotParms.player1.equals(parm.guest[i] + " TBA")) {

                          if (slotParms.guest_id1 == 0) {
                              invalid = true;
                          } else {
                              invalid = checkTrackedGuestName(slotParms.player1, slotParms.guest_id1, parm.guest[i], club, con);
                          }

                          if (invalid) {
                                slotParms.gplayer = slotParms.player1;    // indicate error
                                slotParms.hit4 = true;                    // error is guest name not specified
                          }
                      }

                  } else if (parm.forceg > 0) {

                      invalid = checkGstName(slotParms.player1, parm.guest[i], club);      // go check for a name

                      if (invalid == true) {         // if name not specified

                         if (slotParms.player1.equals( oldPlayers[0] )) {   // if already approved

                            invalid = false;          // ok to skip this guest

                         } else {

                            slotParms.gplayer = slotParms.player1;    // indicate error
                            slotParms.hit3 = true;                    // error is guest name not specified
                         }
                      }
                  }
               }

               break loop1;     // matching guest type - exit loop
            }
            i++;
         }         // end of while loop
      }
      if (!slotParms.player2.equals( "" )) {

         i = 0;
         loop2:
         while (i < parm.MAX_Guests) {

            if (slotParms.player2.startsWith( parm.guest[i] )) {

               slotParms.g2 = parm.guest[i];       // indicate player is a guest name and save name
               slotParms.gstA[1] = slotParms.player2;    // save guest value
               slotParms.guests++;             // increment number of guests this slot

               if (parm.gOpt[i] > 0 && !slotParms.user.startsWith( "proshop" )) {     // if Proshop-only guest type

                  invalid = true;                 // default to invalid guest type
                  i2 = 0;
                  loopg2:
                  while (i2 < 5) {

                     if (slotParms.player2.equals( oldPlayers[i2] )) {   // if already approved

                        invalid = false;          // ok to skip this guest
                        break loopg2;             // exit loop
                     }
                     i2++;
                  }

                  if (invalid == true) {                       // if new or changed player name

                     slotParms.gplayer = slotParms.player2;    // indicate error (ok if it was already entered by pro)
                  }
               }

               //
               //  Check for guest name if requested for this club
               //
               if (invalid == false) {

                   if (parm.gDb[i] == 1) {

                      if (!guestdbTbaAllowed || slotParms.guest_id2 != 0 || !slotParms.player2.equals(parm.guest[i] + " TBA")) {

                          if (slotParms.guest_id2 == 0) {
                              invalid = true;
                          } else {
                              invalid = checkTrackedGuestName(slotParms.player2, slotParms.guest_id2, parm.guest[i], club, con);
                          }

                          if (invalid) {
                                slotParms.gplayer = slotParms.player2;    // indicate error
                                slotParms.hit4 = true;                    // error is guest name not specified
                          }
                      }

                   } else if (parm.forceg > 0) {

                       invalid = checkGstName(slotParms.player2, parm.guest[i], club);      // go check for a name

                       if (invalid == true) {         // if name not specified

                           if (slotParms.player2.equals( oldPlayers[1] )) {   // if already approved

                               invalid = false;          // ok to skip this guest

                           } else {

                               slotParms.gplayer = slotParms.player2;    // indicate error
                               slotParms.hit3 = true;                    // error is guest name not specified
                           }
                       }
                   }
               }

               break loop2;
            }
            i++;
         }         // end of while loop
      }
      if (!slotParms.player3.equals( "" )) {

         i = 0;
         loop3:
         while (i < parm.MAX_Guests) {

            if (slotParms.player3.startsWith( parm.guest[i] )) {

               slotParms.g3 = parm.guest[i];       // indicate player is a guest name and save name
               slotParms.gstA[2] = slotParms.player3;    // save guest value
               slotParms.guests++;             // increment number of guests this slot

               if (parm.gOpt[i] > 0 && !slotParms.user.startsWith( "proshop" )) {    // if Proshop-only guest type

                  invalid = true;                 // default to invalid guest type
                  i2 = 0;
                  loopg3:
                  while (i2 < 5) {

                     if (slotParms.player3.equals( oldPlayers[i2] )) {   // if already approved

                        invalid = false;          // ok to skip this guest
                        break loopg3;             // exit loop
                     }
                     i2++;
                  }

                  if (invalid == true) {                       // if new or changed player name

                     slotParms.gplayer = slotParms.player3;                   // indicate error (ok if it was already entered by pro)
                  }
               }

               //
               //  Check for guest name if requested for this club
               //
               if (invalid == false) {

                   if (parm.gDb[i] == 1) {

                      if (!guestdbTbaAllowed || slotParms.guest_id3 != 0 || !slotParms.player3.equals(parm.guest[i] + " TBA")) {

                          if (slotParms.guest_id3 == 0) {
                              invalid = true;
                          } else {
                              invalid = checkTrackedGuestName(slotParms.player3, slotParms.guest_id3, parm.guest[i], club, con);
                          }

                          if (invalid) {
                                slotParms.gplayer = slotParms.player3;    // indicate error
                                slotParms.hit4 = true;                    // error is guest name not specified
                          }
                      }

                   } else if (parm.forceg > 0) {

                       invalid = checkGstName(slotParms.player3, parm.guest[i], club);      // go check for a name

                       if (invalid == true) {         // if name not specified

                           if (slotParms.player3.equals( oldPlayers[2] )) {   // if already approved

                               invalid = false;          // ok to skip this guest

                           } else {

                               slotParms.gplayer = slotParms.player3;    // indicate error
                               slotParms.hit3 = true;                    // error is guest name not specified
                           }
                       }
                   }
               }

               break loop3;
            }
            i++;
         }         // end of while loop
      }
      if (!slotParms.player4.equals( "" )) {

         i = 0;
         loop4:
         while (i < parm.MAX_Guests) {

            if (slotParms.player4.startsWith( parm.guest[i] )) {

               slotParms.g4 = parm.guest[i];       // indicate player is a guest name and save name
               slotParms.gstA[3] = slotParms.player4;    // save guest value
               slotParms.guests++;             // increment number of guests this slot

               if (parm.gOpt[i] > 0 && !slotParms.user.startsWith( "proshop" )) {   // if Proshop-only guest type

                  invalid = true;                 // default to invalid guest type
                  i2 = 0;
                  loopg4:
                  while (i2 < 5) {

                     if (slotParms.player4.equals( oldPlayers[i2] )) {   // if already approved

                        invalid = false;          // ok to skip this guest
                        break loopg4;             // exit loop
                     }
                     i2++;
                  }

                  if (invalid == true) {                       // if new or changed player name

                     slotParms.gplayer = slotParms.player4;                   // indicate error (ok if it was already entered by pro)
                  }
               }

               //
               //  Check for guest name if requested for this club
               //
               if (invalid == false) {

                   if (parm.gDb[i] == 1) {

                      if (!guestdbTbaAllowed || slotParms.guest_id4 != 0 || !slotParms.player4.equals(parm.guest[i] + " TBA")) {

                          if (slotParms.guest_id4 == 0) {
                              invalid = true;
                          } else {
                              invalid = checkTrackedGuestName(slotParms.player4, slotParms.guest_id4, parm.guest[i], club, con);
                          }

                          if (invalid) {
                                slotParms.gplayer = slotParms.player4 + " : " + slotParms.guest_id4;    // indicate error
                                slotParms.hit4 = true;                    // error is guest name not specified
                          }
                      }

                   } else if (parm.forceg > 0) {

                       invalid = checkGstName(slotParms.player4, parm.guest[i], club);      // go check for a name

                       if (invalid == true) {         // if name not specified

                           if (slotParms.player4.equals( oldPlayers[3] )) {   // if already approved

                               invalid = false;          // ok to skip this guest

                           } else {

                               slotParms.gplayer = slotParms.player4;    // indicate error
                               slotParms.hit3 = true;                    // error is guest name not specified
                           }
                       }
                   }
               }

               break loop4;
            }
            i++;
         }         // end of while loop
      }
      if (!slotParms.player5.equals( "" )) {

         i = 0;
         loop5:
         while (i < parm.MAX_Guests) {

            if (slotParms.player5.startsWith( parm.guest[i] )) {

               slotParms.g5 = parm.guest[i];       // indicate player is a guest name and save name
               slotParms.gstA[4] = slotParms.player5;    // save guest value
               slotParms.guests++;             // increment number of guests this slot

               if (parm.gOpt[i] > 0 && !slotParms.user.startsWith( "proshop" )) {    // if Proshop-only guest type

                  invalid = true;                 // default to invalid guest type
                  i2 = 0;
                  loopg5:
                  while (i2 < 5) {

                     if (slotParms.player5.equals( oldPlayers[i2] )) {   // if already approved

                        invalid = false;          // ok to skip this guest
                        break loopg5;             // exit loop
                     }
                     i2++;
                  }

                  if (invalid == true) {                       // if new or changed player name

                     slotParms.gplayer = slotParms.player5;                   // indicate error (ok if it was already entered by pro)
                  }
               }

               //
               //  Check for guest name if requested for this club
               //
               if (invalid == false) {

                   if (parm.gDb[i] == 1) {

                      if (!guestdbTbaAllowed || slotParms.guest_id5 != 0 || !slotParms.player5.equals(parm.guest[i] + " TBA")) {

                          if (slotParms.guest_id5 == 0) {
                              invalid = true;
                          } else {
                              invalid = checkTrackedGuestName(slotParms.player5, slotParms.guest_id5, parm.guest[i], club, con);
                          }

                          if (invalid) {
                                slotParms.gplayer = slotParms.player5;    // indicate error
                                slotParms.hit4 = true;                    // error is guest name not specified
                          }
                      }

                   } else if (parm.forceg > 0) {

                       invalid = checkGstName(slotParms.player5, parm.guest[i], club);      // go check for a name

                       if (invalid == true) {         // if name not specified

                           if (slotParms.player5.equals( oldPlayers[4] )) {   // if already approved

                               invalid = false;          // ok to skip this guest

                           } else {

                               slotParms.gplayer = slotParms.player5;    // indicate error
                               slotParms.hit3 = true;                    // error is guest name not specified
                           }
                       }
                   }
               }

               break loop5;
            }
            i++;
         }         // end of while loop
      }

      //
      //  Check for Unaccompanied Guests - see if Associated Member Name provided
      //
      if (!slotParms.mem1.equals( "" )) {     // if name provided, then find username

         tok = new StringTokenizer( slotParms.mem1 );     // space is the default token

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

         if (!lname.equals( "" )) {

            pstmt1 = con.prepareStatement (
               "SELECT username FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ?");

            pstmt1.clearParameters();        // clear the parms
            pstmt1.setString(1, lname);
            pstmt1.setString(2, fname);
            pstmt1.setString(3, mi);
            rs = pstmt1.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               slotParms.userg1 = rs.getString(1);
               user = slotParms.userg1;               // save
            }
            pstmt1.close();
         }
      }
      if (!slotParms.mem2.equals( "" )) {     // if name provided, then find username

         tok = new StringTokenizer( slotParms.mem2 );     // space is the default token

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

         if (!lname.equals( "" )) {

            pstmt1 = con.prepareStatement (
               "SELECT username FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ?");

            pstmt1.clearParameters();        // clear the parms
            pstmt1.setString(1, lname);
            pstmt1.setString(2, fname);
            pstmt1.setString(3, mi);
            rs = pstmt1.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               slotParms.userg2 = rs.getString(1);
               user = slotParms.userg2;               // save
            }
            pstmt1.close();
         }
      } else {

         if (!slotParms.g2.equals( "" ) && slotParms.userg2.equals( "" )) {  // if guest but not assigned

            slotParms.userg2 = user;               // assign to last assigned, if any
         }
      }

      if (!slotParms.mem3.equals( "" )) {     // if name provided, then find username

         tok = new StringTokenizer( slotParms.mem3 );     // space is the default token

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

         if (!lname.equals( "" )) {

            pstmt1 = con.prepareStatement (
               "SELECT username FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ?");

            pstmt1.clearParameters();        // clear the parms
            pstmt1.setString(1, lname);
            pstmt1.setString(2, fname);
            pstmt1.setString(3, mi);
            rs = pstmt1.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               slotParms.userg3 = rs.getString(1);
               user = slotParms.userg3;               // save
            }
            pstmt1.close();
         }
      } else {

         if (!slotParms.g3.equals( "" ) && slotParms.userg3.equals( "" )) {  // if guest but not assigned

            slotParms.userg3 = user;               // assign to last assigned, if any
         }
      }

      if (!slotParms.mem4.equals( "" )) {     // if name provided, then find username

         tok = new StringTokenizer( slotParms.mem4 );     // space is the default token

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

         if (!lname.equals( "" )) {

            pstmt1 = con.prepareStatement (
               "SELECT username FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ?");

            pstmt1.clearParameters();        // clear the parms
            pstmt1.setString(1, lname);
            pstmt1.setString(2, fname);
            pstmt1.setString(3, mi);
            rs = pstmt1.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               slotParms.userg4 = rs.getString(1);
               user = slotParms.userg4;               // save
            }
            pstmt1.close();
         }
      } else {

         if (!slotParms.g4.equals( "" ) && slotParms.userg4.equals( "" )) {  // if guest but not assigned

            slotParms.userg4 = user;               // assign to last assigned, if any
         }
      }

      if (!slotParms.mem5.equals( "" )) {     // if name provided, then find username

         tok = new StringTokenizer( slotParms.mem5 );     // space is the default token

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

         if (!lname.equals( "" )) {

            pstmt1 = con.prepareStatement (
               "SELECT username FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ?");

            pstmt1.clearParameters();        // clear the parms
            pstmt1.setString(1, lname);
            pstmt1.setString(2, fname);
            pstmt1.setString(3, mi);
            rs = pstmt1.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               slotParms.userg5 = rs.getString(1);
            }
            pstmt1.close();
         }
      } else {

         if (!slotParms.g5.equals( "" ) && slotParms.userg5.equals( "" )) {   // if guest but not assigned

            slotParms.userg5 = user;               // assign to last assigned, if any
         }
      }

   }
   catch (Exception e) {

      throw new Exception("Error getting guest info - verifySlot.parseGuests " + e.getMessage());
   }

 }


/**
 //************************************************************************
 //
 //   getUsers - Get the usernames, membership types and hndcp's for players if matching name found
 //
 //     sets:  username
 //            membership type
 //            member type
 //            member number
 //            handicap
 //            number of members
 //
 //************************************************************************
 **/

 public static void getUsers(parmSlot slotParms, Connection con)
         throws Exception {


   //Statement stmt = null;
   ResultSet rs = null;

   String club = con.getCatalog(); // get db (club) name

   //
   //  Init the values in slotParm
   //
   slotParms.user1 = "";
   slotParms.user2 = "";
   slotParms.user3 = "";
   slotParms.user4 = "";
   slotParms.user5 = "";
   slotParms.mship1 = "";
   slotParms.mship2 = "";
   slotParms.mship3 = "";
   slotParms.mship4 = "";
   slotParms.mship5 = "";
   slotParms.mtype1 = "";
   slotParms.mtype2 = "";
   slotParms.mtype3 = "";
   slotParms.mtype4 = "";
   slotParms.mtype5 = "";
   slotParms.mNum1 = "";
   slotParms.mNum2 = "";
   slotParms.mNum3 = "";
   slotParms.mNum4 = "";
   slotParms.mNum5 = "";
   slotParms.mstype1 = "";
   slotParms.mstype2 = "";
   slotParms.mstype3 = "";
   slotParms.mstype4 = "";
   slotParms.mstype5 = "";

   slotParms.inval1 = 0;    // init invalid indicators
   slotParms.inval2 = 0;
   slotParms.inval3 = 0;
   slotParms.inval4 = 0;
   slotParms.inval5 = 0;
   slotParms.members = 0;   // init member counter

   StringBuffer mem_name = new StringBuffer();      // for name


   try {

      PreparedStatement pstmt1 = con.prepareStatement (
         "SELECT username, m_ship, m_type, g_hancap, memNum, msub_type, name_first, name_last, name_mi, wc " +
         "FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ? AND inact = 0 AND billable = 1");

      if ((!slotParms.fname1.equals( "" )) && (!slotParms.lname1.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, slotParms.lname1);
         pstmt1.setString(2, slotParms.fname1);
         pstmt1.setString(3, slotParms.mi1);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            slotParms.user1 = rs.getString(1);
            slotParms.mship1 = rs.getString(2);
            slotParms.mtype1 = rs.getString(3);
            slotParms.hndcp1 = rs.getFloat(4);
            slotParms.mNum1 = rs.getString(5);
            slotParms.mstype1 = rs.getString(6);
            slotParms.fname1 = rs.getString("name_first");
            slotParms.lname1 = rs.getString("name_last");
            slotParms.mi1 = rs.getString("name_mi");

            slotParms.members++;         // increment number of members this res.

            //
            //  Rebuild the player name using the name as it is in member2b - to get proper case, etc.
            //
            mem_name = new StringBuffer(slotParms.fname1);             // get first name

            if (!slotParms.mi1.equals( "" )) {
               mem_name.append(" ");
               mem_name.append(slotParms.mi1);                          // add mi if present
            }
            mem_name.append(" " + slotParms.lname1);                    // add last name

            slotParms.player1 = mem_name.toString();                    // set as player name


            //
            //  Make sure member has required fields
            //
            if (slotParms.user1 == null || slotParms.user1.equals( "" ) ||
                slotParms.mship1 == null || slotParms.mship1.equals( "" ) ||
                slotParms.mtype1 == null || slotParms.mtype1.equals( "" )) {

               slotParms.inval1 = 2;        // indicate incomplete member record
            }

            if (slotParms.mNum1 == null) {

               slotParms.mNum1 = "";
            }

            if (club.equals("elmcrestcc")) {

                // Check player's default mode of transportation.  If they have a default and it's pro-only, apply it
                if (Utilities.isProOnlyTmode(rs.getString("wc"), con)) {
                    slotParms.p1cw = rs.getString("wc");
                }
            }

         } else {       // not a member name

            if (!slotParms.player1.equals( slotParms.oldPlayer1 ) &&
                !slotParms.player1.equals( slotParms.oldPlayer2 ) &&
                !slotParms.player1.equals( slotParms.oldPlayer3 ) &&
                !slotParms.player1.equals( slotParms.oldPlayer4 ) &&
                !slotParms.player1.equals( slotParms.oldPlayer5 )) {    // if name not already accepted by pro

                // enforce a default guest type for tcclub - this is repeated for each position (case #1370)
                if (club.equals("tcclub") || club.equals("demov4")) {

                    slotParms.player1 = "Guest " + slotParms.player1;   // append 'Guest' to player name
                    slotParms.g1 = "Guest";                             // save name of guest type
                    slotParms.gstA[0] = slotParms.player1;              // save full player name
                    slotParms.guests++;                                 // increment number of guests in this slot

                } else {
                    slotParms.inval1 = 1;                               // indicate invalid name entered
                }
            }
         }
      }

      if ((!slotParms.fname2.equals( "" )) && (!slotParms.lname2.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, slotParms.lname2);
         pstmt1.setString(2, slotParms.fname2);
         pstmt1.setString(3, slotParms.mi2);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            slotParms.user2 = rs.getString(1);
            slotParms.mship2 = rs.getString(2);
            slotParms.mtype2 = rs.getString(3);
            slotParms.hndcp2 = rs.getFloat(4);
            slotParms.mNum2 = rs.getString(5);
            slotParms.mstype2 = rs.getString(6);
            slotParms.fname2 = rs.getString("name_first");
            slotParms.lname2 = rs.getString("name_last");
            slotParms.mi2 = rs.getString("name_mi");

            slotParms.members++;         // increment number of members this res.

            //
            //  Rebuild the player name using the name as it is in member2b - to get proper case, etc.
            //
            mem_name = new StringBuffer(slotParms.fname2);             // get first name

            if (!slotParms.mi2.equals( "" )) {
               mem_name.append(" ");
               mem_name.append(slotParms.mi2);                          // add mi if present
            }
            mem_name.append(" " + slotParms.lname2);                    // add last name

            slotParms.player2 = mem_name.toString();                    // set as player name


            //
            //  Make sure member has required fields
            //
            if (slotParms.user2 == null || slotParms.user2.equals( "" ) ||
                slotParms.mship2 == null || slotParms.mship2.equals( "" ) ||
                slotParms.mtype2 == null || slotParms.mtype2.equals( "" )) {

               slotParms.inval2 = 2;        // indicate incomplete member record
            }

            if (slotParms.mNum2 == null) {

               slotParms.mNum2 = "";
            }

            if (club.equals("elmcrestcc")) {

                // Check player's default mode of transportation.  If they have a default and it's pro-only, apply it
                if (Utilities.isProOnlyTmode(rs.getString("wc"), con)) {
                    slotParms.p2cw = rs.getString("wc");
                }
            }

         } else {

            if (!slotParms.player2.equals( slotParms.oldPlayer1 ) &&
                !slotParms.player2.equals( slotParms.oldPlayer2 ) &&
                !slotParms.player2.equals( slotParms.oldPlayer3 ) &&
                !slotParms.player2.equals( slotParms.oldPlayer4 ) &&
                !slotParms.player2.equals( slotParms.oldPlayer5 )) {    // if name not already accepted by pro

                if (club.equals("tcclub") || club.equals("demov4")) {

                    slotParms.player2 = "Guest " + slotParms.player2;   // append 'Guest' to player name
                    slotParms.g2 = "Guest";                             // save name of guest type
                    slotParms.gstA[1] = slotParms.player2;              // save full player name
                    slotParms.guests++;                                 // increment number of guests in this slot

                } else {
                    slotParms.inval2 = 1;                               // indicate invalid name entered
                }
            }
         }
      }

      if ((!slotParms.fname3.equals( "" )) && (!slotParms.lname3.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, slotParms.lname3);
         pstmt1.setString(2, slotParms.fname3);
         pstmt1.setString(3, slotParms.mi3);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            slotParms.user3 = rs.getString(1);
            slotParms.mship3 = rs.getString(2);
            slotParms.mtype3 = rs.getString(3);
            slotParms.hndcp3 = rs.getFloat(4);
            slotParms.mNum3 = rs.getString(5);
            slotParms.mstype3 = rs.getString(6);
            slotParms.fname3 = rs.getString("name_first");
            slotParms.lname3 = rs.getString("name_last");
            slotParms.mi3 = rs.getString("name_mi");

            slotParms.members++;         // increment number of members this res.

            //
            //  Rebuild the player name using the name as it is in member2b - to get proper case, etc.
            //
            mem_name = new StringBuffer(slotParms.fname3);             // get first name

            if (!slotParms.mi3.equals( "" )) {
               mem_name.append(" ");
               mem_name.append(slotParms.mi3);                          // add mi if present
            }
            mem_name.append(" " + slotParms.lname3);                    // add last name

            slotParms.player3 = mem_name.toString();                    // set as player name


            //
            //  Make sure member has required fields
            //
            if (slotParms.user3 == null || slotParms.user3.equals( "" ) ||
                slotParms.mship3 == null || slotParms.mship3.equals( "" ) ||
                slotParms.mtype3 == null || slotParms.mtype3.equals( "" )) {

               slotParms.inval3 = 2;        // indicate incomplete member record
            }

            if (slotParms.mNum3 == null) {

               slotParms.mNum3 = "";
            }

            if (club.equals("elmcrestcc")) {

                // Check player's default mode of transportation.  If they have a default and it's pro-only, apply it
                if (Utilities.isProOnlyTmode(rs.getString("wc"), con)) {
                    slotParms.p3cw = rs.getString("wc");
                }
            }

         } else {

            if (!slotParms.player3.equals( slotParms.oldPlayer1 ) &&
                !slotParms.player3.equals( slotParms.oldPlayer2 ) &&
                !slotParms.player3.equals( slotParms.oldPlayer3 ) &&
                !slotParms.player3.equals( slotParms.oldPlayer4 ) &&
                !slotParms.player3.equals( slotParms.oldPlayer5 )) {    // if name not already accepted by pro

                if (club.equals("tcclub") || club.equals("demov4")) {

                    slotParms.player3 = "Guest " + slotParms.player3;   // append 'Guest' to player name
                    slotParms.g3 = "Guest";                             // save name of guest type
                    slotParms.gstA[2] = slotParms.player3;              // save full player name
                    slotParms.guests++;                                 // increment number of guests in this slot

                } else {
                    slotParms.inval3 = 1;                               // indicate invalid name entered
                }
            }
         }
      }

      if ((!slotParms.fname4.equals( "" )) && (!slotParms.lname4.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, slotParms.lname4);
         pstmt1.setString(2, slotParms.fname4);
         pstmt1.setString(3, slotParms.mi4);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            slotParms.user4 = rs.getString(1);
            slotParms.mship4 = rs.getString(2);
            slotParms.mtype4 = rs.getString(3);
            slotParms.hndcp4 = rs.getFloat(4);
            slotParms.mNum4 = rs.getString(5);
            slotParms.mstype4 = rs.getString(6);
            slotParms.fname4 = rs.getString("name_first");
            slotParms.lname4 = rs.getString("name_last");
            slotParms.mi4 = rs.getString("name_mi");

            slotParms.members++;         // increment number of members this res.

            //
            //  Rebuild the player name using the name as it is in member2b - to get proper case, etc.
            //
            mem_name = new StringBuffer(slotParms.fname4);             // get first name

            if (!slotParms.mi4.equals( "" )) {
               mem_name.append(" ");
               mem_name.append(slotParms.mi4);                          // add mi if present
            }
            mem_name.append(" " + slotParms.lname4);                    // add last name

            slotParms.player4 = mem_name.toString();                    // set as player name


            //
            //  Make sure member has required fields
            //
            if (slotParms.user4 == null || slotParms.user4.equals( "" ) ||
                slotParms.mship4 == null || slotParms.mship4.equals( "" ) ||
                slotParms.mtype4 == null || slotParms.mtype4.equals( "" )) {

               slotParms.inval4 = 2;        // indicate incomplete member record
            }

            if (slotParms.mNum4 == null) {

               slotParms.mNum4 = "";
            }

            if (club.equals("elmcrestcc")) {

                // Check player's default mode of transportation.  If they have a default and it's pro-only, apply it
                if (Utilities.isProOnlyTmode(rs.getString("wc"), con)) {
                    slotParms.p4cw = rs.getString("wc");
                }
            }

         } else {

            if (!slotParms.player4.equals( slotParms.oldPlayer1 ) &&
                !slotParms.player4.equals( slotParms.oldPlayer2 ) &&
                !slotParms.player4.equals( slotParms.oldPlayer3 ) &&
                !slotParms.player4.equals( slotParms.oldPlayer4 ) &&
                !slotParms.player4.equals( slotParms.oldPlayer5 )) {    // if name not already accepted by pro

                if (club.equals("tcclub") || club.equals("demov4")) {

                    slotParms.player4 = "Guest " + slotParms.player4;   // append 'Guest' to player name
                    slotParms.g4 = "Guest";                             // save name of guest type
                    slotParms.gstA[3] = slotParms.player4;              // save full player name
                    slotParms.guests++;                                 // increment number of guests in this slot

                } else {
                    slotParms.inval4 = 1;                               // indicate invalid name entered
                }
            }
         }
      }

      if ((!slotParms.fname5.equals( "" )) && (!slotParms.lname5.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, slotParms.lname5);
         pstmt1.setString(2, slotParms.fname5);
         pstmt1.setString(3, slotParms.mi5);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            slotParms.user5 = rs.getString(1);
            slotParms.mship5 = rs.getString(2);
            slotParms.mtype5 = rs.getString(3);
            slotParms.hndcp5 = rs.getFloat(4);
            slotParms.mNum5 = rs.getString(5);
            slotParms.mstype5 = rs.getString(6);
            slotParms.fname5 = rs.getString("name_first");
            slotParms.lname5 = rs.getString("name_last");
            slotParms.mi5 = rs.getString("name_mi");

            slotParms.members++;         // increment number of members this res.

            //
            //  Rebuild the player name using the name as it is in member2b - to get proper case, etc.
            //
            mem_name = new StringBuffer(slotParms.fname5);             // get first name

            if (!slotParms.mi5.equals( "" )) {
               mem_name.append(" ");
               mem_name.append(slotParms.mi5);                          // add mi if present
            }
            mem_name.append(" " + slotParms.lname5);                    // add last name

            slotParms.player5 = mem_name.toString();                    // set as player name


            //
            //  Make sure member has required fields
            //
            if (slotParms.user5 == null || slotParms.user5.equals( "" ) ||
                slotParms.mship5 == null || slotParms.mship5.equals( "" ) ||
                slotParms.mtype5 == null || slotParms.mtype5.equals( "" )) {

               slotParms.inval5 = 2;        // indicate incomplete member record
            }

            if (slotParms.mNum5 == null) {

               slotParms.mNum5 = "";
            }

            if (club.equals("elmcrestcc")) {

                // Check player's default mode of transportation.  If they have a default and it's pro-only, apply it
                if (Utilities.isProOnlyTmode(rs.getString("wc"), con)) {
                    slotParms.p5cw = rs.getString("wc");
                }
            }

         } else {

            if (!slotParms.player5.equals( slotParms.oldPlayer1 ) &&
                !slotParms.player5.equals( slotParms.oldPlayer2 ) &&
                !slotParms.player5.equals( slotParms.oldPlayer3 ) &&
                !slotParms.player5.equals( slotParms.oldPlayer4 ) &&
                !slotParms.player5.equals( slotParms.oldPlayer5 )) {    // if name not already accepted by pro

                if (club.equals("tcclub") || club.equals("demov4")) {

                    slotParms.player5 = "Guest " + slotParms.player5;   // append 'Guest' to player name
                    slotParms.g5 = "Guest";                             // save name of guest type
                    slotParms.gstA[4] = slotParms.player5;              // save full player name
                    slotParms.guests++;                                 // increment number of guests in this slot

                } else {
                    slotParms.inval5 = 1;                               // indicate invalid name entered
                }
            }
         }
      }
      pstmt1.close();

   }
   catch (Exception e) {

      throw new Exception("Error getting user info - verifySlot.getUsers " + e.getMessage());
   }

   return;

 }


/**
 //************************************************************************
 //
 //  checkDaysAdv - checks members for exceeding 'Days in Advance'
 //                   based on their membership type.
 //
 //************************************************************************
 **/

 public static boolean checkDaysAdv(parmSlot slotParms, Connection con)
         throws Exception {


   //Statement stmt = null;
   //ResultSet rs = null;

   int year = 0;
   int month = 0;
   int day = 0;
   int days = 0;
   int dayNum = 0;
   //int count = 0;
   int i = 0;
   int ind = 0;

   String player = "";
   String mship = "";
   String mtype = "";
   //String user = "";
   String courseName = "East";            // Custom for this course only (Merion)
   String mshipM = "Non-Resident";        // Custom for this mship only (Merion)

   String [] userA = new String [5];      // array to hold the players' usernames
   String [] mshipA = new String [5];     // array to hold the players' membership types
   String [] mtypeA = new String [5];     // array to hold the players' member types
   String [] mstypeA = new String [5];    // array to hold the players' member sub-types
   String [] mNumA = new String [5];      // array to hold the players' member numbers
   String [] playerA = new String [5];    // array to hold the players' names
   String [] oldplayerA = new String [5];    // array to hold the old players' names
   int [] daysA = new int [7];            // array to hold the days in adv for each day of the week

   boolean error = false;
   boolean skip = false;


   //
   //   Skip this if club is Oakland Hills or CC of Rockies - they have their own custom processing
   //
   if ((slotParms.club.equals( "oaklandhills" ) && slotParms.course.equals( "South Course" )) || slotParms.club.equals( "ccrockies" )) {

      skip = true;        // skip this test

   } else {

      //
      //  If Merion, East course and a weekday - skip this test
      //
      if (slotParms.club.equals( "merion" ) && slotParms.course.equals( courseName )) {

         skip = true;        // default to skip this test

      //   if (slotParms.date == Hdate1 || slotParms.date == Hdate2 || slotParms.date == Hdate2b || slotParms.date == Hdate3 ||
      //       slotParms.day.equals( "Saturday" ) || slotParms.day.equals( "Sunday" )) {
         if (slotParms.date == Hdate2 || slotParms.date == Hdate2b ||
             slotParms.day.equals( "Saturday" ) || slotParms.day.equals( "Sunday" )) {

            skip = false;        // weekend or holiday - do NOT skip this test

            //
            //  If a w/e and mship = Non-Resident, then allow one per family
            //
            if ((slotParms.day.equals( "Saturday" ) && slotParms.time > 1030) ||
                (slotParms.day.equals( "Sunday" ) && slotParms.time > 900)) {

               if ((slotParms.mship1.equals( "" ) || slotParms.mship1.equals( mshipM )) &&
                   (slotParms.mship2.equals( "" ) || slotParms.mship2.equals( mshipM )) &&
                   (slotParms.mship3.equals( "" ) || slotParms.mship3.equals( mshipM )) &&
                   (slotParms.mship4.equals( "" ) || slotParms.mship4.equals( mshipM )) &&
                   (slotParms.mship5.equals( "" ) || slotParms.mship5.equals( mshipM ))) {     // if Non_Resident members

                  skip = checkMerionNonRes(slotParms, con);     // check if we can allow Non_Resident to book on w/e's
               }
            }
         }
      }
   }


   if (skip == false) {

      //
      //  parm block to hold the club parameters
      //
      parmClub parm = new parmClub(slotParms.root_activity_id, con);          // allocate a parm block

      try {
         //
         //  put player info in arrays for processing below
         //
         userA[0] = slotParms.user1;
         userA[1] = slotParms.user2;
         userA[2] = slotParms.user3;
         userA[3] = slotParms.user4;
         userA[4] = slotParms.user5;
         mshipA[0] = slotParms.mship1;
         mshipA[1] = slotParms.mship2;
         mshipA[2] = slotParms.mship3;
         mshipA[3] = slotParms.mship4;
         mshipA[4] = slotParms.mship5;
         mtypeA[0] = slotParms.mtype1;
         mtypeA[1] = slotParms.mtype2;
         mtypeA[2] = slotParms.mtype3;
         mtypeA[3] = slotParms.mtype4;
         mtypeA[4] = slotParms.mtype5;
         mstypeA[0] = slotParms.mstype1;
         mstypeA[1] = slotParms.mstype2;
         mstypeA[2] = slotParms.mstype3;
         mstypeA[3] = slotParms.mstype4;
         mstypeA[4] = slotParms.mstype5;
         mNumA[0] = slotParms.mNum1;
         mNumA[1] = slotParms.mNum2;
         mNumA[2] = slotParms.mNum3;
         mNumA[3] = slotParms.mNum4;
         mNumA[4] = slotParms.mNum5;
         playerA[0] = slotParms.player1;
         playerA[1] = slotParms.player2;
         playerA[2] = slotParms.player3;
         playerA[3] = slotParms.player4;
         playerA[4] = slotParms.player5;
         oldplayerA[0] = slotParms.oldPlayer1;
         oldplayerA[1] = slotParms.oldPlayer2;
         oldplayerA[2] = slotParms.oldPlayer3;
         oldplayerA[3] = slotParms.oldPlayer4;
         oldplayerA[4] = slotParms.oldPlayer5;

         //
         //  Get the tee time's date values
         //
         month = slotParms.mm;                    // get month
         day = slotParms.dd;                      // get day
         year = slotParms.yy;                     // get year

         //
         // Calculate the number of days between today and the date requested (=> ind)
         // and get the day of the week (for the requested tee time)
         //
         BigDate today = BigDate.localToday();                 // get today's date
         BigDate thisdate = new BigDate(year, month, day);     // get requested date

         dayNum = thisdate.getDayOfWeek();                     // get req'd date's day of week (0 - 6)

         ind = (thisdate.getOrdinal() - today.getOrdinal());   // number of days between


         //
         //  Get current time and see if we need to adjust ind
         //
         Calendar cal = new GregorianCalendar();             // get current date & time (Central Time)
         int cal_hourDay = cal.get(Calendar.HOUR_OF_DAY);    // 24 hr clock (0 - 23)
         int cal_min = cal.get(Calendar.MINUTE);

         //
         //    Adjust the time based on the club's time zone (we are Central)
         //
         int cal_time = (cal_hourDay * 100) + cal_min;     // get time in hhmm format

         cal_time = adjustTime(con, cal_time);   // adjust the time

         if (cal_time < 0) {                // if negative, then we went back or ahead one day

            cal_time = 0 - cal_time;        // convert back to positive value

            if (cal_time < 1200) {           // if AM, then we rolled ahead 1 day (allow for Saudi Arabia and others east of us)

               //
               // roll ahead 1 day (its now just after midnight, the next day Eastern Time)
               //
               if (ind > 0) {

                  ind--;                    // one less day between today and the day of the tee time
               }

            } else {                        // we rolled back 1 day

               //
               // roll back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
               //
               ind++;                    // one more day between today and the day of the tee time
            }
         }


         //
         //   Get the 'days in adv' parms specified for this club
         //
         getClub.getParms(con, parm, slotParms.activity_id);        // get the club parms


         //
         //  Check each player that is a member for 'days in adv' violation
         //
         i = 0;
         loop1:
         while (i < 5) {           // do each player

            if (!mshipA[i].equals( "" )) {

               mship = mshipA[i];
               mtype = mtypeA[i];
               player = playerA[i];

               //
               //  use the member's mship type to determine which 'days in advance' parms to use
               //
               getDaysInAdv(con, parm, mship);        // get the days in adv data for this member

               daysA[0] = parm.advdays1;     // get days in adv for this type
               daysA[1] = parm.advdays2;     // Monday
               daysA[2] = parm.advdays3;
               daysA[3] = parm.advdays4;
               daysA[4] = parm.advdays5;
               daysA[5] = parm.advdays6;
               daysA[6] = parm.advdays7;     // Saturday


             /*               // removed per Mike Scully's request 5/05/09
               //
               //  If Medinah CC - override days in adv parms
               //
               if (slotParms.club.equals( "medinahcc" )) {

                  if (slotParms.course.equals( "No 2" )) {

                     daysA[0] = 7;     // Course 2 is always 7 days for ALL
                     daysA[1] = 7;
                     daysA[2] = 7;
                     daysA[3] = 7;
                     daysA[4] = 7;
                     daysA[5] = 7;
                     daysA[6] = 7;
                  }
               }         // end of Medinah
              */


               //
               //   scioto Custom - change the days in adv for Spouses - Sun, Mon, Thur, Fri, Sat = 2, Tue, Wed = 3
               //
               if (slotParms.club.equals( "sciotocc" ) && mtype.startsWith( "Spouse" )) {

                  daysA[0] = 2;         // Sun = 2
                  daysA[1] = 2;         // Mon = 2
                  daysA[2] = 3;         // Tue = 3
                  daysA[3] = 3;         // Wed = 3
                  daysA[4] = 2;         // Thu = 2
                  daysA[5] = 2;         // Fri = 2
                  daysA[6] = 2;         // Sat = 2
               }

               //
               //   Pinehurst CC - if Proprietary member only allow access the day of, starting at 6:00 AM MT.
               //
               if (slotParms.club.equals( "pinehurstcountryclub" ) && slotParms.course.equals("Pfluger 9") && mship.equals("Proprietary")) {

                  if ((slotParms.day.equals("Tuesday") || slotParms.day.equals("Thursday")) && slotParms.time < 1100 && mtype.endsWith("Female")) {  // let women access their special times

                     error = false;

                  } else {

                     daysA[0] = 0;         // Sun = 0
                     daysA[1] = 0;         // Mon = 0
                     daysA[2] = 0;         // Tue = 0
                     daysA[3] = 0;         // Wed = 0
                     daysA[4] = 0;         // Thu = 0
                     daysA[5] = 0;         // Fri = 0
                     daysA[6] = 0;         // Sat = 0
                  }
               }

               //
               //   Columbia Edgewater Custom - change the days in adv for Spouses to 7
               //
               if (slotParms.club.equals( "cecc" ) && mtype.startsWith( "Spouse" )) {

                  daysA[2] = 7;         // 7 days for Tues and Fri
                  daysA[5] = 7;
               }

               //
               //  If El Niguel and an Adult Female - change the Tuesday Days and Time
               //
               if (slotParms.club.equals( "elniguelcc" ) && mtype.equals( "Adult Female" )) {

                  daysA[2] = 4;               // Tues = 4 (normally is 2)
               }


               //
               //  If Claremont CC and an Adult Female - change the Tuesday Days to 30
               //
               if (slotParms.club.equals( "claremontcc" ) && mtype.equals( "Adult Female" )) {

                  daysA[2] = 30;               // Tues = 30 (normally is 3)
               }


               //
               //  If Royal Oaks WA and an Adult Female - change the Tuesday Days to 7
               //
               if (slotParms.club.equals( "royaloaks" ) && mtype.equals( "Adult Female" )) {

                  daysA[2] = 7;               // Tues = 7 (normally is 2)
                  daysA[3] = 7;               // Wed = 7 (normally is 2)
                  daysA[5] = 7;               // Fri = 7 (normally is 2)
               }


               //
               //  If Brooklawn CC and an Adult Female 18 Holer - change the Tuesday Days to 7
               //
               if (slotParms.club.equals( "brooklawn" ) && (mstypeA[i].equals( "18 Holer" ) || mstypeA[i].equals( "9/18 Holer" ))) {

                  daysA[2] = 7;               // Tues = 7 (normally is 2)
               }

               if (slotParms.club.equals( "brooklawn" ) && (mstypeA[i].equals( "9 Holer" ) || mstypeA[i].equals( "9/18 Holer" ))) {

                  daysA[3] = 7;               // Wed = 7 (normally is 2)
               }

               if (slotParms.club.equals( "sharonheights" ) && mstypeA[i].equals( "18 Holer" )) {

                  daysA[2] = 14;               // Tues = 14 (normally is 2)
               }

               if (slotParms.club.equals( "seacliffcc" ) && mstypeA[i].equals( "18 Holer" )) {

                  daysA[2] = 30;               // Tues = 30 (normally is 7)
               }

               if (slotParms.club.equals( "greenhills" ) && mstypeA[i].equals("Ladies")) {

                   daysA[4] = 7;
               }

               if (slotParms.club.equals( "paloaltohills" ) && mstypeA[i].equals("18 Holer")) {

                   daysA[4] = 30;       // Thurs = 30 days
               }

               if (slotParms.club.equals( "rhillscc" )  && mstypeA[i].equals("Ladies")) {

                   daysA[2] = 14;
                   daysA[4] = 14;
               }

           /*
               //
               //   Los Coyotes Custom - override the days in adv for Secondary Members from to 3 days  (Case #1191)
               //
               if (slotParms.club.equals( "loscoyotes" ) && mtype.startsWith( "Secondary" )) {

                  daysA[0] = 3;
                  daysA[1] = 3;
                  daysA[2] = 3;
                  daysA[3] = 3;
                  daysA[4] = 3;
                  daysA[5] = 3;
                  daysA[6] = 3;
               }
*/
               days = daysA[dayNum];            // get the value based on the day of the week

               if (ind > days) {                // if tee time's days in adv is more than allowed for player

                  //
                  //  ok if player was already on tee time (already approved by pro)
                  //
                  if (!player.equals( oldplayerA[0] ) && !player.equals( oldplayerA[1] ) &&
                      !player.equals( oldplayerA[2] ) && !player.equals( oldplayerA[3] ) &&
                      !player.equals( oldplayerA[4] )) {

                     //
                     //  Add custom checks for Hazeltine
                     //
                     //    Certain women are allowed to book times at specific times on Tuesdays and
                     //    Thursdays 14 days in advance (vs. 7 days normally).
                     //    We must limit access to the specified times for that day.
                     //
                     if (slotParms.club.equals( "hazeltine" ) && ind < 15) {  // if within 14 day period

                        if (mtype.endsWith( "Female" ) && (dayNum == 2 || dayNum == 4)) {  // if female & Tues or Thurs

                           // if Thursday & 18 Holer
                           if (dayNum == 4 && (mstypeA[i].equals("18 Holer") || mstypeA[i].startsWith("AH-18") ||
                               mstypeA[i].startsWith("AH-9/18") || mstypeA[i].startsWith("9/18"))) {

                              if (slotParms.time < 724 || slotParms.time > 930) {  // must be between 7:24 and 9:30

                                 error = true;
                              }
                           } else {

                              // if Tuesday
                              if (dayNum == 2) {

                                 error = true;     // default to error so we can check both subtype options

                                 // and 'After Hours' member
                                 if (mstypeA[i].equals("After Hours") || mstypeA[i].startsWith("AH-")) {

                                    if (slotParms.time > 1631 && slotParms.time < 1831) {  // must be between 4:32 and 6:30

                                       error = false;
                                    }
                                 }
                                 // or a 9 holer
                                 if (mstypeA[i].equals("9 Holer") || mstypeA[i].startsWith("AH-9") ||
                                     mstypeA[i].equals("9/18 Holer")) {                           // if '9 Holer' type

                                    if (slotParms.time > 723 && slotParms.time < 921) {  // must be between 7:24 and 9:20

                                       error = false;
                                    }
                                 }
                              } else {
                                 error = true;       // error if wrong member and/or wrong day
                              }
                           }

                        } else {
                           error = true;                // error if not female or not Tuesday or Thursday
                        }
                        if (error == true) {
                           slotParms.player = player;
                           break loop1;                  // exit loop
                        }

                     } else {           // not Hazeltine or not within 14 days

                        //
                        //  Special processing for Scioto CC - Ok if certain member is included
                        //
                        if (slotParms.club.equals( "sciotocc" )) {      // if Scioto

                           boolean sciotob = false;

                           if ((slotParms.day.equals( "Saturday" ) || slotParms.day.equals( "Sunday" ) || slotParms.day.equals( "Monday" )) &&
                               ind < 5) {

                              sciotob = true;            // check for full member included in request

                           } else {

                              if ((slotParms.day.equals( "Tuesday" ) || slotParms.day.equals( "Wednesday" ) || slotParms.day.equals( "Thursday" ) ||
                                   slotParms.day.equals( "Friday" )) && ind < 6) {

                                 sciotob = true;            // check for full member included in request
                              }
                           }

                           if (sciotob == true) {

                              //
                              //  Within full member's allowed days - check if any full access members are included
                              //
                              if ((mtypeA[0].startsWith( "Member" ) && (mshipA[0].startsWith( "Full" ) || mshipA[0].startsWith( "Life" ) ||
                                  mshipA[0].equals( "Honorary" ) || mshipA[0].equals( "Assoc Golf" ) || mshipA[0].equals( "Assoc Golf - Half Dues" ) ||
                                  mshipA[0].equals( "Assoc Golf Daughter" ))) ||
                                 (mtypeA[1].startsWith( "Member" ) && (mshipA[1].startsWith( "Full" ) || mshipA[1].startsWith( "Life" ) ||
                                  mshipA[1].equals( "Honorary" ) || mshipA[1].equals( "Assoc Golf" ) || mshipA[1].equals( "Assoc Golf - Half Dues" ) ||
                                  mshipA[1].equals( "Assoc Golf Daughter" ))) ||
                                 (mtypeA[2].startsWith( "Member" ) && (mshipA[2].startsWith( "Full" ) || mshipA[2].startsWith( "Life" ) ||
                                  mshipA[2].equals( "Honorary" ) || mshipA[2].equals( "Assoc Golf" ) || mshipA[2].equals( "Assoc Golf - Half Dues" ) ||
                                  mshipA[2].equals( "Assoc Golf Daughter" ))) ||
                                 (mtypeA[3].startsWith( "Member" ) && (mshipA[3].startsWith( "Full" ) || mshipA[3].startsWith( "Life" ) ||
                                  mshipA[3].equals( "Honorary" ) || mshipA[3].equals( "Assoc Golf" ) || mshipA[3].equals( "Assoc Golf - Half Dues" ) ||
                                  mshipA[3].equals( "Assoc Golf Daughter" ))) ||
                                 (mtypeA[4].startsWith( "Member" ) && (mshipA[4].startsWith( "Full" ) || mshipA[4].startsWith( "Life" ) ||
                                  mshipA[4].equals( "Honorary" ) || mshipA[4].equals( "Assoc Golf" ) || mshipA[4].equals( "Assoc Golf - Half Dues" ) ||
                                  mshipA[4].equals( "Assoc Golf Daughter" )))) {

                                 sciotob = true;            // just set this so the if statement is easier (use an else)

                              } else {

                                 error = true;                 // error
                                 slotParms.player = player;
                                 break loop1;                  // exit loop
                              }

                           } else {

                              error = true;                 // error
                              slotParms.player = player;
                              break loop1;                  // exit loop
                           }

                        } else {           // not Scioto CC

                           if (slotParms.club.equals( "brooklawn" ) && mtype.endsWith( "Male" ) && slotParms.date == 20100717 && slotParms.time < 1400) {

                              error = false;               // ok

                           } else {

                              error = true;                 // error
                              slotParms.player = player;
                              break loop1;                  // exit loop
                           }
                        }
                     }
                  }
               }

            }   // end of IF mship not null

            i++;
         }       // end of WHILE

      }
      catch (Exception e) {

         throw new Exception("Error checking days in advance - verifySlot.checkDaysAdv " + e.getMessage());
      }

   }

   return(error);

 }


/**
  //************************************************************************
  //  getDaysInAdv - get the 'days in adv' data for the specified membership type.
  //
  //
  //   receives:  a club parm block and mship type
  //
  //   returns: void (parms are saved in the parm block)
  //
  //************************************************************************
 **/
 public static void getDaysInAdv(Connection con, parmClub parm, String mship) {

     getDaysInAdv(con, parm, mship, 0);

 }

 public static void getDaysInAdv(Connection con, parmClub parm, String mship, int activity_id) {


   int hour = 0;

   loop1:
   for (int i = 0; i < parm.MAX_Mships; i++) {   // check all mship types for a match

      if (mship.equals( parm.mship[i] )) {

         parm.advdays1 = parm.days1[i];     // get days in adv for this type
         parm.advdays2 = parm.days2[i];
         parm.advdays3 = parm.days3[i];
         parm.advdays4 = parm.days4[i];
         parm.advdays5 = parm.days5[i];
         parm.advdays6 = parm.days6[i];
         parm.advdays7 = parm.days7[i];

         parm.advhr1 = parm.advhrd1[i];
         parm.advhr2 = parm.advhrd2[i];
         parm.advhr3 = parm.advhrd3[i];
         parm.advhr4 = parm.advhrd4[i];
         parm.advhr5 = parm.advhrd5[i];
         parm.advhr6 = parm.advhrd6[i];
         parm.advhr7 = parm.advhrd7[i];

         parm.advmin1 = parm.advmind1[i];
         parm.advmin2 = parm.advmind2[i];
         parm.advmin3 = parm.advmind3[i];
         parm.advmin4 = parm.advmind4[i];
         parm.advmin5 = parm.advmind5[i];
         parm.advmin6 = parm.advmind6[i];
         parm.advmin7 = parm.advmind7[i];

         parm.advam1 = parm.advamd1[i];
         parm.advam2 = parm.advamd2[i];
         parm.advam3 = parm.advamd3[i];
         parm.advam4 = parm.advamd4[i];
         parm.advam5 = parm.advamd5[i];
         parm.advam6 = parm.advamd6[i];
         parm.advam7 = parm.advamd7[i];

         parm.memviewdays = parm.viewdays[i];

         break loop1;        // exit loop
      }
   }

   //
   //  Calculate the time value for the advance parms
   //
   hour = parm.advhr1;

   if (hour == 12) {

      hour = 0;                      // if midnight or noon, change to 0 hours
   }

   parm.advtime1 = (hour * 100) + parm.advmin1;     // create military time

   if (parm.advam1.equals( "PM" )) {

      parm.advtime1 += 1200;                // if PM, adjust for military (noon becomes 1200)
   }

   hour = parm.advhr2;        // do next day

   if (hour == 12) {

      hour = 0;
   }

   parm.advtime2 = (hour * 100) + parm.advmin2;

   if (parm.advam2.equals( "PM" )) {

      parm.advtime2 += 1200;
   }

   hour = parm.advhr3;        // do next day

   if (hour == 12) {

      hour = 0;
   }

   parm.advtime3 = (hour * 100) + parm.advmin3;

   if (parm.advam3.equals( "PM" )) {

      parm.advtime3 += 1200;
   }

   hour = parm.advhr4;        // do next day

   if (hour == 12) {

      hour = 0;
   }

   parm.advtime4 = (hour * 100) + parm.advmin4;

   if (parm.advam4.equals( "PM" )) {

      parm.advtime4 += 1200;
   }

   hour = parm.advhr5;        // do next day

   if (hour == 12) {

      hour = 0;
   }

   parm.advtime5 = (hour * 100) + parm.advmin5;

   if (parm.advam5.equals( "PM" )) {

      parm.advtime5 += 1200;
   }

   hour = parm.advhr6;        // do next day

   if (hour == 12) {

      hour = 0;
   }

   parm.advtime6 = (hour * 100) + parm.advmin6;

   if (parm.advam6.equals( "PM" )) {

      parm.advtime6 += 1200;
   }

   hour = parm.advhr7;        // do next day

   if (hour == 12) {

      hour = 0;
   }

   parm.advtime7 = (hour * 100) + parm.advmin7;

   if (parm.advam7.equals( "PM" )) {

      parm.advtime7 += 1200;
   }

 }  // end of getDaysInAdv


 public static boolean checkMaxOrigBy(String user, long date, int max_originations, Connection con) {

    int count = 0;

    try {

        PreparedStatement pstmt = con.prepareStatement("SELECT COUNT(*) FROM teecurr2 WHERE orig_by = ? AND date = ?;");

        pstmt.clearParameters();
        pstmt.setString(1, user);
        pstmt.setLong(2, date);

        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) count = rs.getInt(1);

        pstmt.close();

    } catch (Exception ignore) {

        return false;
    }

    return (count >= max_originations);
 }


/**
 //************************************************************************
 //
 //  checkMaxRounds - checks members for max rounds per week, month or year
 //                   based on their membership type.
 //
 //************************************************************************
 **/

 public static boolean checkMaxRounds(parmSlot slotParms, Connection con)
         throws Exception {

   PreparedStatement pstmt1 = null;
   //Statement stmt = null;
   ResultSet rs = null;

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(slotParms.root_activity_id, con);          // allocate a parm block

   int year = 0;
   int month = 0;
   int dayNum = 0;
   int count = 0;
   int mtimes = 0;
   int ind = 0;
   int i = 0;
   int show1 = 0;
   int show2 = 0;
   int show3 = 0;
   int show4 = 0;
   int show5 = 0;

   long dateStart = 0;
   long dateEnd = 0;

   String mperiod = "";
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";

   String [] mshipA = new String [parm.MAX_Mships+1];     // array to hold the membership names
   int [] mtimesA = new int [parm.MAX_Mships+1];          // array to hold the mship max # of rounds value
   String [] periodA = new String [parm.MAX_Mships+1];    // array to hold the mship periods (week, month, year)

   boolean error = false;
   boolean skipCheck = false;


   //
   //  if St. Clair CC and the Terrace course, skip these checks - this course has unlimited play
   //
   if (slotParms.club.equals( "stclaircc" ) && slotParms.course.equals( "Terrace" )) {

      skipCheck = true;       // skip these tests
   }


   if (skipCheck == false) {      // if ok to proceed

      //
      //  init the string arrays
      //
      for (i=0; i<parm.MAX_Mships+1; i++) {
         mshipA[i] = "";
         mtimesA[i] = 0;
         periodA[i] = "";
      }

      try {

         //
         //  Get this date's calendar and then determine start and end of week.
         //
         int calmm = slotParms.mm -1;                    // adjust month value for cal

         Calendar cal = new GregorianCalendar();         // get todays date

         //
         //  set cal to tee time's date
         //
         cal.set(Calendar.YEAR,slotParms.yy);                    // set year in cal
         cal.set(Calendar.MONTH,calmm);                          // set month in cal
         cal.set(Calendar.DAY_OF_MONTH,slotParms.dd);            // set day in cal

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

         month = month + 1;                                // month starts at zero

         dateEnd = year * 10000;                           // create a date field of yyyymmdd
         dateEnd = dateEnd + (month * 100);
         dateEnd = dateEnd + dayNum;                       // date = yyyymmdd (for comparisons)

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

         pstmt1 = con.prepareStatement("SELECT mship, mtimes, period FROM mship5 WHERE activity_id = ? LIMIT " + slotParms.MAX_Mships);
         pstmt1.clearParameters();
         pstmt1.setInt(1, slotParms.root_activity_id);
         rs = pstmt1.executeQuery();

         i = 1; // force reset

         while ( rs.next() ) {

             mshipA[i] = rs.getString("mship");
             mtimesA[i] = rs.getInt("mtimes");
             periodA[i] = rs.getString("period");

             i++;
         }

         pstmt1.close();

         //
         // statements for week
         //
         PreparedStatement pstmt2 = con.prepareStatement (
            "SELECT dd FROM teecurr2 WHERE date != ? AND date >= ? AND date <= ? AND " +
                       "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)");

         PreparedStatement pstmt3 = con.prepareStatement (
            "SELECT username1, username2, username3, username4, show1, show2, show3, show4, username5, show5 " +
            "FROM teepast2 WHERE date != ? AND date >= ? AND date <= ? AND " +
                       "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)");
         //
         // statements for month
         //
         PreparedStatement pstmt2m = con.prepareStatement (
            "SELECT dd FROM teecurr2 WHERE date != ? AND mm = ? AND yy = ? AND " +
                       "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)");

         PreparedStatement pstmt3m = con.prepareStatement (
            "SELECT username1, username2, username3, username4, show1, show2, show3, show4, username5, show5 " +
            "FROM teepast2 WHERE date != ? AND mm = ? AND yy = ? AND " +
                       "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)");
         //
         // statements for year
         //
         PreparedStatement pstmt2y = con.prepareStatement (
            "SELECT dd FROM teecurr2 WHERE date != ? AND yy = ? AND " +
                       "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)");

         PreparedStatement pstmt3y = con.prepareStatement (
            "SELECT username1, username2, username3, username4, show1, show2, show3, show4, username5, show5 " +
            "FROM teepast2 WHERE date != ? AND yy = ? AND " +
                       "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)");

         if (slotParms.club.equals("brooklawn")) {

             pstmt3y = con.prepareStatement (
                     "SELECT username1, username2, username3, username4, show1, show2, show3, show4, username5, show5 " +
                     "FROM teepast2 WHERE date != ? AND yy = ? AND " +
                     "((username1 = ? AND mship1 = ?) OR (username2 = ? AND mship2 = ?) OR " +
                     "(username3 = ? AND mship3 = ?) OR (username4 = ? AND mship4 = ?) OR " +
                     "(username5 = ? AND mship5 = ?))");
         }

         //
         //  if St. Clair CC then only check the Championship course
         //
         if (slotParms.club.equals( "stclaircc" )) {

            //
            // statements for week
            //
            pstmt2 = con.prepareStatement (
               "SELECT dd FROM teecurr2 WHERE date != ? AND date >= ? AND date <= ? AND " +
                          "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?) AND courseName = 'Championship'");

            pstmt3 = con.prepareStatement (
               "SELECT username1, username2, username3, username4, show1, show2, show3, show4, username5, show5 " +
               "FROM teepast2 WHERE date != ? AND date >= ? AND date <= ? AND " +
                          "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?) AND courseName = 'Championship'");
            //
            // statements for month
            //
            pstmt2m = con.prepareStatement (
               "SELECT dd FROM teecurr2 WHERE date != ? AND mm = ? AND yy = ? AND " +
                          "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?) AND courseName = 'Championship'");

            pstmt3m = con.prepareStatement (
               "SELECT username1, username2, username3, username4, show1, show2, show3, show4, username5, show5 " +
               "FROM teepast2 WHERE date != ? AND mm = ? AND yy = ? AND " +
                          "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?) AND courseName = 'Championship'");
            //
            // statements for year
            //
            pstmt2y = con.prepareStatement (
               "SELECT dd FROM teecurr2 WHERE date != ? AND yy = ? AND " +
                          "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?) AND courseName = 'Championship'");

            pstmt3y = con.prepareStatement (
               "SELECT username1, username2, username3, username4, show1, show2, show3, show4, username5, show5 " +
               "FROM teepast2 WHERE date != ? AND yy = ? AND " +
                          "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?) AND courseName = 'Championship'");
         }


         if (!slotParms.mship1.equals( "" )) {          // check if player 1 name specified

            ind = 1;             // init fields
            count = 0;
            mtimes = 0;
            mperiod = "";

            loop1:
            while (ind < parm.MAX_Mships+1) {

               if (slotParms.mship1.equals( mshipA[ind] )) {

                  mtimes = mtimesA[ind];            // match found - get number of rounds
                  mperiod = periodA[ind];           //               and period (week, month, year)
                  break loop1;
               }
               ind++;
            }

            if (mtimes != 0) {             // if match found for this player and there is a limit

               if (mperiod.equals( "Week" )) {       // if WEEK

                  pstmt2.clearParameters();        // get count from teecurr
                  pstmt2.setLong(1, slotParms.date);
                  pstmt2.setLong(2, dateStart);
                  pstmt2.setLong(3, dateEnd);
                  pstmt2.setString(4, slotParms.user1);
                  pstmt2.setString(5, slotParms.user1);
                  pstmt2.setString(6, slotParms.user1);
                  pstmt2.setString(7, slotParms.user1);
                  pstmt2.setString(8, slotParms.user1);
                  rs = pstmt2.executeQuery();

                  count = 0;

                  while (rs.next()) {

                     count++;                      // count number or tee times in this week
                  }

                  pstmt3.clearParameters();        // get count from teepast
                  pstmt3.setLong(1, slotParms.date);
                  pstmt3.setLong(2, dateStart);
                  pstmt3.setLong(3, dateEnd);
                  pstmt3.setString(4, slotParms.user1);
                  pstmt3.setString(5, slotParms.user1);
                  pstmt3.setString(6, slotParms.user1);
                  pstmt3.setString(7, slotParms.user1);
                  pstmt3.setString(8, slotParms.user1);
                  rs = pstmt3.executeQuery();

                  while (rs.next()) {

                     user1 = rs.getString("username1");
                     user2 = rs.getString("username2");
                     user3 = rs.getString("username3");
                     user4 = rs.getString("username4");
                     show1 = rs.getInt("show1");
                     show2 = rs.getInt("show2");
                     show3 = rs.getInt("show3");
                     show4 = rs.getInt("show4");
                     user5 = rs.getString("username5");
                     show5 = rs.getInt("show5");

                     if (user1.equalsIgnoreCase( slotParms.user1 ) && show1 == 1) {

                        count++;                      // count number or tee times in this week

                     } else {

                        if (user2.equalsIgnoreCase( slotParms.user1 ) && show2 == 1) {

                           count++;                      // count number or tee times in this week

                        } else {

                           if (user3.equalsIgnoreCase( slotParms.user1 ) && show3 == 1) {

                              count++;                      // count number or tee times in this week

                           } else {

                              if (user4.equalsIgnoreCase( slotParms.user1 ) && show4 == 1) {

                                 count++;                      // count number or tee times in this week

                              } else {

                                 if (user5.equalsIgnoreCase( slotParms.user1 ) && show5 == 1) {

                                    count++;                      // count number or tee times in this week
                                 }
                              }
                           }
                        }
                     }
                  }
               }       // end of IF mperiod = week

               if (mperiod.equals( "Month" )) {      // if MONTH

                  pstmt2m.clearParameters();        // get count from teecurr
                  pstmt2m.setLong(1, slotParms.date);
                  pstmt2m.setInt(2, slotParms.mm);
                  pstmt2m.setInt(3, slotParms.yy);
                  pstmt2m.setString(4, slotParms.user1);
                  pstmt2m.setString(5, slotParms.user1);
                  pstmt2m.setString(6, slotParms.user1);
                  pstmt2m.setString(7, slotParms.user1);
                  pstmt2m.setString(8, slotParms.user1);
                  rs = pstmt2m.executeQuery();

                  count = 0;

                  while (rs.next()) {

                     count++;                      // count number or tee times in this month
                  }

                  pstmt3m.clearParameters();        // get count from teepast
                  pstmt3m.setLong(1, slotParms.date);
                  pstmt3m.setInt(2, slotParms.mm);
                  pstmt3m.setInt(3, slotParms.yy);
                  pstmt3m.setString(4, slotParms.user1);
                  pstmt3m.setString(5, slotParms.user1);
                  pstmt3m.setString(6, slotParms.user1);
                  pstmt3m.setString(7, slotParms.user1);
                  pstmt3m.setString(8, slotParms.user1);
                  rs = pstmt3m.executeQuery();

                  while (rs.next()) {

                     user1 = rs.getString("username1");
                     user2 = rs.getString("username2");
                     user3 = rs.getString("username3");
                     user4 = rs.getString("username4");
                     show1 = rs.getInt("show1");
                     show2 = rs.getInt("show2");
                     show3 = rs.getInt("show3");
                     show4 = rs.getInt("show4");
                     user5 = rs.getString("username5");
                     show5 = rs.getInt("show5");

                     if (user1.equalsIgnoreCase( slotParms.user1 ) && show1 == 1) {

                        count++;                      // count number or tee times in this week

                     } else {

                        if (user2.equalsIgnoreCase( slotParms.user1 ) && show2 == 1) {

                           count++;                      // count number or tee times in this week

                        } else {

                           if (user3.equalsIgnoreCase( slotParms.user1 ) && show3 == 1) {

                              count++;                      // count number or tee times in this week

                           } else {

                              if (user4.equalsIgnoreCase( slotParms.user1 ) && show4 == 1) {

                                 count++;                      // count number or tee times in this week

                              } else {

                                 if (user5.equalsIgnoreCase( slotParms.user1 ) && show5 == 1) {

                                    count++;                      // count number or tee times in this week
                                 }
                              }
                           }
                        }
                     }
                  }
               }       // end of IF mperiod = Month

               if (mperiod.equals( "Year" )) {            // if Year

                  pstmt2y.clearParameters();             // get count from teecurr
                  pstmt2y.setLong(1, slotParms.date);
                  pstmt2y.setInt(2, slotParms.yy);
                  pstmt2y.setString(3, slotParms.user1);
                  pstmt2y.setString(4, slotParms.user1);
                  pstmt2y.setString(5, slotParms.user1);
                  pstmt2y.setString(6, slotParms.user1);
                  pstmt2y.setString(7, slotParms.user1);
                  rs = pstmt2y.executeQuery();

                  count = 0;

                  while (rs.next()) {

                     count++;                      // count number or tee times in this year
                  }

                  if (slotParms.club.equals("brooklawn")) {
                      pstmt3y.clearParameters();        // get count from teepast
                      pstmt3y.setLong(1, slotParms.date);
                      pstmt3y.setInt(2, slotParms.yy);
                      pstmt3y.setString(3, slotParms.user1);
                      pstmt3y.setString(4, slotParms.mship1);
                      pstmt3y.setString(5, slotParms.user1);
                      pstmt3y.setString(6, slotParms.mship1);
                      pstmt3y.setString(7, slotParms.user1);
                      pstmt3y.setString(8, slotParms.mship1);
                      pstmt3y.setString(9, slotParms.user1);
                      pstmt3y.setString(10, slotParms.mship1);
                      pstmt3y.setString(11, slotParms.user1);
                      pstmt3y.setString(12, slotParms.mship1);
                      rs = pstmt3y.executeQuery();
                  } else {
                      pstmt3y.clearParameters();        // get count from teepast
                      pstmt3y.setLong(1, slotParms.date);
                      pstmt3y.setInt(2, slotParms.yy);
                      pstmt3y.setString(3, slotParms.user1);
                      pstmt3y.setString(4, slotParms.user1);
                      pstmt3y.setString(5, slotParms.user1);
                      pstmt3y.setString(6, slotParms.user1);
                      pstmt3y.setString(7, slotParms.user1);
                      rs = pstmt3y.executeQuery();
                  }

                  while (rs.next()) {

                     user1 = rs.getString("username1");
                     user2 = rs.getString("username2");
                     user3 = rs.getString("username3");
                     user4 = rs.getString("username4");
                     show1 = rs.getInt("show1");
                     show2 = rs.getInt("show2");
                     show3 = rs.getInt("show3");
                     show4 = rs.getInt("show4");
                     user5 = rs.getString("username5");
                     show5 = rs.getInt("show5");

                     if (user1.equalsIgnoreCase( slotParms.user1 ) && show1 == 1) {

                        count++;                      // count number or tee times in this week

                     } else {

                        if (user2.equalsIgnoreCase( slotParms.user1 ) && show2 == 1) {

                           count++;                      // count number or tee times in this week

                        } else {

                           if (user3.equalsIgnoreCase( slotParms.user1 ) && show3 == 1) {

                              count++;                      // count number or tee times in this week

                           } else {

                              if (user4.equalsIgnoreCase( slotParms.user1 ) && show4 == 1) {

                                 count++;                      // count number or tee times in this week

                              } else {

                                 if (user5.equalsIgnoreCase( slotParms.user1 ) && show5 == 1) {

                                    count++;                      // count number or tee times in this week
                                 }
                              }
                           }
                        }
                     }
                  }
               }       // end of IF mperiod = Year

               //
               //  Compare # of tee times in this period with max allowed for membership type
               //
               if (count >= mtimes)  {

                  if (!slotParms.player1.equals( slotParms.oldPlayer1 ) &&
                      !slotParms.player1.equals( slotParms.oldPlayer2 ) &&
                      !slotParms.player1.equals( slotParms.oldPlayer3 ) &&
                      !slotParms.player1.equals( slotParms.oldPlayer4 ) &&
                      !slotParms.player1.equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

                     error = true;                // reject this member
                     slotParms.mship = slotParms.mship1;
                     slotParms.player = slotParms.player1;
                     slotParms.period = mperiod;
                  }
               }
            }          // end of IF match found for player
         }          // end of player 1 if

         if (!slotParms.mship2.equals( "" )) {          // check if player 2 name specified

            ind = 1;             // init fields
            count = 0;
            mtimes = 0;
            mperiod = "";

            loop2:
            while (ind < parm.MAX_Mships+1) {

               if (slotParms.mship2.equals( mshipA[ind] )) {

                  mtimes = mtimesA[ind];            // match found - get number of rounds
                  mperiod = periodA[ind];           //               and period (week, month, year)
                  break loop2;
               }
               ind++;
            }

            if (mtimes != 0) {             // if match found for this player and there is a limit

               if (mperiod.equals( "Week" )) {       // if WEEK

                  pstmt2.clearParameters();        // get count from teecurr
                  pstmt2.setLong(1, slotParms.date);
                  pstmt2.setLong(2, dateStart);
                  pstmt2.setLong(3, dateEnd);
                  pstmt2.setString(4, slotParms.user2);
                  pstmt2.setString(5, slotParms.user2);
                  pstmt2.setString(6, slotParms.user2);
                  pstmt2.setString(7, slotParms.user2);
                  pstmt2.setString(8, slotParms.user2);
                  rs = pstmt2.executeQuery();

                  count = 0;

                  while (rs.next()) {

                     count++;                      // count number or tee times in this week
                  }

                  pstmt3.clearParameters();        // get count from teepast
                  pstmt3.setLong(1, slotParms.date);
                  pstmt3.setLong(2, dateStart);
                  pstmt3.setLong(3, dateEnd);
                  pstmt3.setString(4, slotParms.user2);
                  pstmt3.setString(5, slotParms.user2);
                  pstmt3.setString(6, slotParms.user2);
                  pstmt3.setString(7, slotParms.user2);
                  pstmt3.setString(8, slotParms.user2);
                  rs = pstmt3.executeQuery();

                  while (rs.next()) {

                     user1 = rs.getString("username1");
                     user2 = rs.getString("username2");
                     user3 = rs.getString("username3");
                     user4 = rs.getString("username4");
                     show1 = rs.getInt("show1");
                     show2 = rs.getInt("show2");
                     show3 = rs.getInt("show3");
                     show4 = rs.getInt("show4");
                     user5 = rs.getString("username5");
                     show5 = rs.getInt("show5");

                     if (user1.equalsIgnoreCase( slotParms.user2 ) && show1 == 1) {

                        count++;                      // count number or tee times in this week

                     } else {

                        if (user2.equalsIgnoreCase( slotParms.user2 ) && show2 == 1) {

                           count++;                      // count number or tee times in this week

                        } else {

                           if (user3.equalsIgnoreCase( slotParms.user2 ) && show3 == 1) {

                              count++;                      // count number or tee times in this week

                           } else {

                              if (user4.equalsIgnoreCase( slotParms.user2 ) && show4 == 1) {

                                 count++;                      // count number or tee times in this week

                              } else {

                                 if (user5.equalsIgnoreCase( slotParms.user2 ) && show5 == 1) {

                                    count++;                      // count number or tee times in this week
                                 }
                              }
                           }
                        }
                     }
                  }
               }       // end of IF mperiod = week

               if (mperiod.equals( "Month" )) {      // if MONTH

                  pstmt2m.clearParameters();        // get count from teecurr
                  pstmt2m.setLong(1, slotParms.date);
                  pstmt2m.setInt(2, slotParms.mm);
                  pstmt2m.setInt(3, slotParms.yy);
                  pstmt2m.setString(4, slotParms.user2);
                  pstmt2m.setString(5, slotParms.user2);
                  pstmt2m.setString(6, slotParms.user2);
                  pstmt2m.setString(7, slotParms.user2);
                  pstmt2m.setString(8, slotParms.user2);
                  rs = pstmt2m.executeQuery();

                  count = 0;

                  while (rs.next()) {

                     count++;                      // count number or tee times in this month
                  }

                  pstmt3m.clearParameters();        // get count from teepast
                  pstmt3m.setLong(1, slotParms.date);
                  pstmt3m.setInt(2, slotParms.mm);
                  pstmt3m.setInt(3, slotParms.yy);
                  pstmt3m.setString(4, slotParms.user2);
                  pstmt3m.setString(5, slotParms.user2);
                  pstmt3m.setString(6, slotParms.user2);
                  pstmt3m.setString(7, slotParms.user2);
                  pstmt3m.setString(8, slotParms.user2);
                  rs = pstmt3m.executeQuery();

                  while (rs.next()) {

                     user1 = rs.getString("username1");
                     user2 = rs.getString("username2");
                     user3 = rs.getString("username3");
                     user4 = rs.getString("username4");
                     show1 = rs.getInt("show1");
                     show2 = rs.getInt("show2");
                     show3 = rs.getInt("show3");
                     show4 = rs.getInt("show4");
                     user5 = rs.getString("username5");
                     show5 = rs.getInt("show5");

                     if (user1.equalsIgnoreCase( slotParms.user2 ) && show1 == 1) {

                        count++;                      // count number or tee times in this week

                     } else {

                        if (user2.equalsIgnoreCase( slotParms.user2 ) && show2 == 1) {

                           count++;                      // count number or tee times in this week

                        } else {

                           if (user3.equalsIgnoreCase( slotParms.user2 ) && show3 == 1) {

                              count++;                      // count number or tee times in this week

                           } else {

                              if (user4.equalsIgnoreCase( slotParms.user2 ) && show4 == 1) {

                                 count++;                      // count number or tee times in this week

                              } else {

                                 if (user5.equalsIgnoreCase( slotParms.user2 ) && show5 == 1) {

                                    count++;                      // count number or tee times in this week
                                 }
                              }
                           }
                        }
                     }
                  }
               }       // end of IF mperiod = Month

               if (mperiod.equals( "Year" )) {            // if Year

                  pstmt2y.clearParameters();             // get count from teecurr
                  pstmt2y.setLong(1, slotParms.date);
                  pstmt2y.setInt(2, slotParms.yy);
                  pstmt2y.setString(3, slotParms.user2);
                  pstmt2y.setString(4, slotParms.user2);
                  pstmt2y.setString(5, slotParms.user2);
                  pstmt2y.setString(6, slotParms.user2);
                  pstmt2y.setString(7, slotParms.user2);
                  rs = pstmt2y.executeQuery();

                  count = 0;

                  while (rs.next()) {

                     count++;                      // count number or tee times in this year
                  }

                  if (slotParms.club.equals("brooklawn")) {
                      pstmt3y.clearParameters();        // get count from teepast
                      pstmt3y.setLong(1, slotParms.date);
                      pstmt3y.setInt(2, slotParms.yy);
                      pstmt3y.setString(3, slotParms.user2);
                      pstmt3y.setString(4, slotParms.mship2);
                      pstmt3y.setString(5, slotParms.user2);
                      pstmt3y.setString(6, slotParms.mship2);
                      pstmt3y.setString(7, slotParms.user2);
                      pstmt3y.setString(8, slotParms.mship2);
                      pstmt3y.setString(9, slotParms.user2);
                      pstmt3y.setString(10, slotParms.mship2);
                      pstmt3y.setString(11, slotParms.user2);
                      pstmt3y.setString(12, slotParms.mship2);
                      rs = pstmt3y.executeQuery();
                  } else {
                      pstmt3y.clearParameters();        // get count from teepast
                      pstmt3y.setLong(1, slotParms.date);
                      pstmt3y.setInt(2, slotParms.yy);
                      pstmt3y.setString(3, slotParms.user2);
                      pstmt3y.setString(4, slotParms.user2);
                      pstmt3y.setString(5, slotParms.user2);
                      pstmt3y.setString(6, slotParms.user2);
                      pstmt3y.setString(7, slotParms.user2);
                      rs = pstmt3y.executeQuery();
                  }

                  while (rs.next()) {

                     user1 = rs.getString("username1");
                     user2 = rs.getString("username2");
                     user3 = rs.getString("username3");
                     user4 = rs.getString("username4");
                     show1 = rs.getInt("show1");
                     show2 = rs.getInt("show2");
                     show3 = rs.getInt("show3");
                     show4 = rs.getInt("show4");
                     user5 = rs.getString("username5");
                     show5 = rs.getInt("show5");

                     if (user1.equalsIgnoreCase( slotParms.user2 ) && show1 == 1) {

                        count++;                      // count number or tee times in this week

                     } else {

                        if (user2.equalsIgnoreCase( slotParms.user2 ) && show2 == 1) {

                           count++;                      // count number or tee times in this week

                        } else {

                           if (user3.equalsIgnoreCase( slotParms.user2 ) && show3 == 1) {

                              count++;                      // count number or tee times in this week

                           } else {

                              if (user4.equalsIgnoreCase( slotParms.user2 ) && show4 == 1) {

                                 count++;                      // count number or tee times in this week

                              } else {

                                 if (user5.equalsIgnoreCase( slotParms.user2 ) && show5 == 1) {

                                    count++;                      // count number or tee times in this week
                                 }
                              }
                           }
                        }
                     }
                  }
               }       // end of IF mperiod = Year

               //
               //  Compare # of tee times in this period with max allowed for membership type
               //
               if (count >= mtimes)  {

                  if (!slotParms.player2.equals( slotParms.oldPlayer1 ) &&
                      !slotParms.player2.equals( slotParms.oldPlayer2 ) &&
                      !slotParms.player2.equals( slotParms.oldPlayer3 ) &&
                      !slotParms.player2.equals( slotParms.oldPlayer4 ) &&
                      !slotParms.player2.equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

                     error = true;                // reject this member
                     slotParms.mship = slotParms.mship2;
                     slotParms.player = slotParms.player2;
                     slotParms.period = mperiod;
                  }
               }
            }          // end of IF match found for player
         }          // end of player 2 if

         if (!slotParms.mship3.equals( "" )) {          // check if player 3 name specified

            ind = 1;             // init fields
            count = 0;
            mtimes = 0;
            mperiod = "";

            loop3:
            while (ind < parm.MAX_Mships+1) {

               if (slotParms.mship3.equals( mshipA[ind] )) {

                  mtimes = mtimesA[ind];            // match found - get number of rounds
                  mperiod = periodA[ind];           //               and period (week, month, year)
                  break loop3;
               }
               ind++;
            }

            if (mtimes != 0) {             // if match found for this player and there is no limit

               if (mperiod.equals( "Week" )) {       // if WEEK

                  pstmt2.clearParameters();        // get count from teecurr
                  pstmt2.setLong(1, slotParms.date);
                  pstmt2.setLong(2, dateStart);
                  pstmt2.setLong(3, dateEnd);
                  pstmt2.setString(4, slotParms.user3);
                  pstmt2.setString(5, slotParms.user3);
                  pstmt2.setString(6, slotParms.user3);
                  pstmt2.setString(7, slotParms.user3);
                  pstmt2.setString(8, slotParms.user3);
                  rs = pstmt2.executeQuery();

                  count = 0;

                  while (rs.next()) {

                     count++;                      // count number or tee times in this week
                  }

                  pstmt3.clearParameters();        // get count from teepast
                  pstmt3.setLong(1, slotParms.date);
                  pstmt3.setLong(2, dateStart);
                  pstmt3.setLong(3, dateEnd);
                  pstmt3.setString(4, slotParms.user3);
                  pstmt3.setString(5, slotParms.user3);
                  pstmt3.setString(6, slotParms.user3);
                  pstmt3.setString(7, slotParms.user3);
                  pstmt3.setString(8, slotParms.user3);
                  rs = pstmt3.executeQuery();

                  while (rs.next()) {

                     user1 = rs.getString("username1");
                     user2 = rs.getString("username2");
                     user3 = rs.getString("username3");
                     user4 = rs.getString("username4");
                     show1 = rs.getInt("show1");
                     show2 = rs.getInt("show2");
                     show3 = rs.getInt("show3");
                     show4 = rs.getInt("show4");
                     user5 = rs.getString("username5");
                     show5 = rs.getInt("show5");

                     if (user1.equalsIgnoreCase( slotParms.user3 ) && show1 == 1) {

                        count++;                      // count number or tee times in this week

                     } else {

                        if (user2.equalsIgnoreCase( slotParms.user3 ) && show2 == 1) {

                           count++;                      // count number or tee times in this week

                        } else {

                           if (user3.equalsIgnoreCase( slotParms.user3 ) && show3 == 1) {

                              count++;                      // count number or tee times in this week

                           } else {

                              if (user4.equalsIgnoreCase( slotParms.user3 ) && show4 == 1) {

                                 count++;                      // count number or tee times in this week

                              } else {

                                 if (user5.equalsIgnoreCase( slotParms.user3 ) && show5 == 1) {

                                    count++;                      // count number or tee times in this week
                                 }
                              }
                           }
                        }
                     }
                  }
               }       // end of IF mperiod = week

               if (mperiod.equals( "Month" )) {      // if MONTH

                  pstmt2m.clearParameters();        // get count from teecurr
                  pstmt2m.setLong(1, slotParms.date);
                  pstmt2m.setInt(2, slotParms.mm);
                  pstmt2m.setInt(3, slotParms.yy);
                  pstmt2m.setString(4, slotParms.user3);
                  pstmt2m.setString(5, slotParms.user3);
                  pstmt2m.setString(6, slotParms.user3);
                  pstmt2m.setString(7, slotParms.user3);
                  pstmt2m.setString(8, slotParms.user3);
                  rs = pstmt2m.executeQuery();

                  count = 0;

                  while (rs.next()) {

                     count++;                      // count number or tee times in this month
                  }

                  pstmt3m.clearParameters();        // get count from teepast
                  pstmt3m.setLong(1, slotParms.date);
                  pstmt3m.setInt(2, slotParms.mm);
                  pstmt3m.setInt(3, slotParms.yy);
                  pstmt3m.setString(4, slotParms.user3);
                  pstmt3m.setString(5, slotParms.user3);
                  pstmt3m.setString(6, slotParms.user3);
                  pstmt3m.setString(7, slotParms.user3);
                  pstmt3m.setString(8, slotParms.user3);
                  rs = pstmt3m.executeQuery();

                  while (rs.next()) {

                     user1 = rs.getString("username1");
                     user2 = rs.getString("username2");
                     user3 = rs.getString("username3");
                     user4 = rs.getString("username4");
                     show1 = rs.getInt("show1");
                     show2 = rs.getInt("show2");
                     show3 = rs.getInt("show3");
                     show4 = rs.getInt("show4");
                     user5 = rs.getString("username5");
                     show5 = rs.getInt("show5");

                     if (user1.equalsIgnoreCase( slotParms.user3 ) && show1 == 1) {

                        count++;                      // count number or tee times in this week

                     } else {

                        if (user2.equalsIgnoreCase( slotParms.user3 ) && show2 == 1) {

                           count++;                      // count number or tee times in this week

                        } else {

                           if (user3.equalsIgnoreCase( slotParms.user3 ) && show3 == 1) {

                              count++;                      // count number or tee times in this week

                           } else {

                              if (user4.equalsIgnoreCase( slotParms.user3 ) && show4 == 1) {

                                 count++;                      // count number or tee times in this week

                              } else {

                                 if (user5.equalsIgnoreCase( slotParms.user3 ) && show5 == 1) {

                                    count++;                      // count number or tee times in this week
                                 }
                              }
                           }
                        }
                     }
                  }
               }       // end of IF mperiod = Month

               if (mperiod.equals( "Year" )) {            // if Year

                  pstmt2y.clearParameters();             // get count from teecurr
                  pstmt2y.setLong(1, slotParms.date);
                  pstmt2y.setInt(2, slotParms.yy);
                  pstmt2y.setString(3, slotParms.user3);
                  pstmt2y.setString(4, slotParms.user3);
                  pstmt2y.setString(5, slotParms.user3);
                  pstmt2y.setString(6, slotParms.user3);
                  pstmt2y.setString(7, slotParms.user3);
                  rs = pstmt2y.executeQuery();

                  count = 0;

                  while (rs.next()) {

                     count++;                      // count number or tee times in this year
                  }

                  if (slotParms.club.equals("brooklawn")) {
                      pstmt3y.clearParameters();        // get count from teepast
                      pstmt3y.setLong(1, slotParms.date);
                      pstmt3y.setInt(2, slotParms.yy);
                      pstmt3y.setString(3, slotParms.user3);
                      pstmt3y.setString(4, slotParms.mship3);
                      pstmt3y.setString(5, slotParms.user3);
                      pstmt3y.setString(6, slotParms.mship3);
                      pstmt3y.setString(7, slotParms.user3);
                      pstmt3y.setString(8, slotParms.mship3);
                      pstmt3y.setString(9, slotParms.user3);
                      pstmt3y.setString(10, slotParms.mship3);
                      pstmt3y.setString(11, slotParms.user3);
                      pstmt3y.setString(12, slotParms.mship3);
                      rs = pstmt3y.executeQuery();
                  } else {
                      pstmt3y.clearParameters();        // get count from teepast
                      pstmt3y.setLong(1, slotParms.date);
                      pstmt3y.setInt(2, slotParms.yy);
                      pstmt3y.setString(3, slotParms.user3);
                      pstmt3y.setString(4, slotParms.user3);
                      pstmt3y.setString(5, slotParms.user3);
                      pstmt3y.setString(6, slotParms.user3);
                      pstmt3y.setString(7, slotParms.user3);
                      rs = pstmt3y.executeQuery();
                  }

                  while (rs.next()) {

                     user1 = rs.getString("username1");
                     user2 = rs.getString("username2");
                     user3 = rs.getString("username3");
                     user4 = rs.getString("username4");
                     show1 = rs.getInt("show1");
                     show2 = rs.getInt("show2");
                     show3 = rs.getInt("show3");
                     show4 = rs.getInt("show4");
                     user5 = rs.getString("username5");
                     show5 = rs.getInt("show5");

                     if (user1.equalsIgnoreCase( slotParms.user3 ) && show1 == 1) {

                        count++;                      // count number or tee times in this week

                     } else {

                        if (user2.equalsIgnoreCase( slotParms.user3 ) && show2 == 1) {

                           count++;                      // count number or tee times in this week

                        } else {

                           if (user3.equalsIgnoreCase( slotParms.user3 ) && show3 == 1) {

                              count++;                      // count number or tee times in this week

                           } else {

                              if (user4.equalsIgnoreCase( slotParms.user3 ) && show4 == 1) {

                                 count++;                      // count number or tee times in this week

                              } else {

                                 if (user5.equalsIgnoreCase( slotParms.user3 ) && show5 == 1) {

                                    count++;                      // count number or tee times in this week
                                 }
                              }
                           }
                        }
                     }
                  }
               }       // end of IF mperiod = Year

               //
               //  Compare # of tee times in this period with max allowed for membership type
               //
               if (count >= mtimes)  {

                  if (!slotParms.player3.equals( slotParms.oldPlayer1 ) &&
                      !slotParms.player3.equals( slotParms.oldPlayer2 ) &&
                      !slotParms.player3.equals( slotParms.oldPlayer3 ) &&
                      !slotParms.player3.equals( slotParms.oldPlayer4 ) &&
                      !slotParms.player3.equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

                     error = true;                // reject this member
                     slotParms.mship = slotParms.mship3;
                     slotParms.player = slotParms.player3;
                     slotParms.period = mperiod;
                  }
               }
            }          // end of IF match found for player
         }          // end of player 3 if

         if (!slotParms.mship4.equals( "" )) {          // check if player 4 name specified

            ind = 1;             // init fields
            count = 0;
            mtimes = 0;
            mperiod = "";

            loop4:
            while (ind < parm.MAX_Mships+1) {

               if (slotParms.mship4.equals( mshipA[ind] )) {

                  mtimes = mtimesA[ind];            // match found - get number of rounds
                  mperiod = periodA[ind];           //               and period (week, month, year)
                  break loop4;
               }
               ind++;
            }

            if (mtimes != 0) {             // if match found for this player and there is no limit

               if (mperiod.equals( "Week" )) {       // if WEEK

                  pstmt2.clearParameters();        // get count from teecurr
                  pstmt2.setLong(1, slotParms.date);
                  pstmt2.setLong(2, dateStart);
                  pstmt2.setLong(3, dateEnd);
                  pstmt2.setString(4, slotParms.user4);
                  pstmt2.setString(5, slotParms.user4);
                  pstmt2.setString(6, slotParms.user4);
                  pstmt2.setString(7, slotParms.user4);
                  pstmt2.setString(8, slotParms.user4);
                  rs = pstmt2.executeQuery();

                  count = 0;

                  while (rs.next()) {

                     count++;                      // count number or tee times in this week
                  }

                  pstmt3.clearParameters();        // get count from teepast
                  pstmt3.setLong(1, slotParms.date);
                  pstmt3.setLong(2, dateStart);
                  pstmt3.setLong(3, dateEnd);
                  pstmt3.setString(4, slotParms.user4);
                  pstmt3.setString(5, slotParms.user4);
                  pstmt3.setString(6, slotParms.user4);
                  pstmt3.setString(7, slotParms.user4);
                  pstmt3.setString(8, slotParms.user4);
                  rs = pstmt3.executeQuery();

                  while (rs.next()) {

                     user1 = rs.getString("username1");
                     user2 = rs.getString("username2");
                     user3 = rs.getString("username3");
                     user4 = rs.getString("username4");
                     show1 = rs.getInt("show1");
                     show2 = rs.getInt("show2");
                     show3 = rs.getInt("show3");
                     show4 = rs.getInt("show4");
                     user5 = rs.getString("username5");
                     show5 = rs.getInt("show5");

                     if (user1.equalsIgnoreCase( slotParms.user4 ) && show1 == 1) {

                        count++;                      // count number or tee times in this week

                     } else {

                        if (user2.equalsIgnoreCase( slotParms.user4 ) && show2 == 1) {

                           count++;                      // count number or tee times in this week

                        } else {

                           if (user3.equalsIgnoreCase( slotParms.user4 ) && show3 == 1) {

                              count++;                      // count number or tee times in this week

                           } else {

                              if (user4.equalsIgnoreCase( slotParms.user4 ) && show4 == 1) {

                                 count++;                      // count number or tee times in this week

                              } else {

                                 if (user5.equalsIgnoreCase( slotParms.user4 ) && show5 == 1) {

                                    count++;                      // count number or tee times in this week
                                 }
                              }
                           }
                        }
                     }
                  }
               }       // end of IF mperiod = week

               if (mperiod.equals( "Month" )) {      // if MONTH

                  pstmt2m.clearParameters();        // get count from teecurr
                  pstmt2m.setLong(1, slotParms.date);
                  pstmt2m.setInt(2, slotParms.mm);
                  pstmt2m.setInt(3, slotParms.yy);
                  pstmt2m.setString(4, slotParms.user4);
                  pstmt2m.setString(5, slotParms.user4);
                  pstmt2m.setString(6, slotParms.user4);
                  pstmt2m.setString(7, slotParms.user4);
                  pstmt2m.setString(8, slotParms.user4);
                  rs = pstmt2m.executeQuery();

                  count = 0;

                  while (rs.next()) {

                     count++;                      // count number or tee times in this month
                  }

                  pstmt3m.clearParameters();        // get count from teepast
                  pstmt3m.setLong(1, slotParms.date);
                  pstmt3m.setInt(2, slotParms.mm);
                  pstmt3m.setInt(3, slotParms.yy);
                  pstmt3m.setString(4, slotParms.user4);
                  pstmt3m.setString(5, slotParms.user4);
                  pstmt3m.setString(6, slotParms.user4);
                  pstmt3m.setString(7, slotParms.user4);
                  pstmt3m.setString(8, slotParms.user4);
                  rs = pstmt3m.executeQuery();

                  while (rs.next()) {

                     user1 = rs.getString("username1");
                     user2 = rs.getString("username2");
                     user3 = rs.getString("username3");
                     user4 = rs.getString("username4");
                     show1 = rs.getInt("show1");
                     show2 = rs.getInt("show2");
                     show3 = rs.getInt("show3");
                     show4 = rs.getInt("show4");
                     user5 = rs.getString("username5");
                     show5 = rs.getInt("show5");

                     if (user1.equalsIgnoreCase( slotParms.user4 ) && show1 == 1) {

                        count++;                      // count number or tee times in this week

                     } else {

                        if (user2.equalsIgnoreCase( slotParms.user4 ) && show2 == 1) {

                           count++;                      // count number or tee times in this week

                        } else {

                           if (user3.equalsIgnoreCase( slotParms.user4 ) && show3 == 1) {

                              count++;                      // count number or tee times in this week

                           } else {

                              if (user4.equalsIgnoreCase( slotParms.user4 ) && show4 == 1) {

                                 count++;                      // count number or tee times in this week

                              } else {

                                 if (user5.equalsIgnoreCase( slotParms.user4 ) && show5 == 1) {

                                    count++;                      // count number or tee times in this week
                                 }
                              }
                           }
                        }
                     }
                  }
               }       // end of IF mperiod = Month

               if (mperiod.equals( "Year" )) {            // if Year

                  pstmt2y.clearParameters();             // get count from teecurr
                  pstmt2y.setLong(1, slotParms.date);
                  pstmt2y.setInt(2, slotParms.yy);
                  pstmt2y.setString(3, slotParms.user4);
                  pstmt2y.setString(4, slotParms.user4);
                  pstmt2y.setString(5, slotParms.user4);
                  pstmt2y.setString(6, slotParms.user4);
                  pstmt2y.setString(7, slotParms.user4);
                  rs = pstmt2y.executeQuery();

                  count = 0;

                  while (rs.next()) {

                     count++;                      // count number or tee times in this year
                  }

                  if (slotParms.club.equals("brooklawn")) {
                      pstmt3y.clearParameters();        // get count from teepast
                      pstmt3y.setLong(1, slotParms.date);
                      pstmt3y.setInt(2, slotParms.yy);
                      pstmt3y.setString(3, slotParms.user4);
                      pstmt3y.setString(4, slotParms.mship4);
                      pstmt3y.setString(5, slotParms.user4);
                      pstmt3y.setString(6, slotParms.mship4);
                      pstmt3y.setString(7, slotParms.user4);
                      pstmt3y.setString(8, slotParms.mship4);
                      pstmt3y.setString(9, slotParms.user4);
                      pstmt3y.setString(10, slotParms.mship4);
                      pstmt3y.setString(11, slotParms.user4);
                      pstmt3y.setString(12, slotParms.mship4);
                      rs = pstmt3y.executeQuery();
                  } else {
                      pstmt3y.clearParameters();        // get count from teepast
                      pstmt3y.setLong(1, slotParms.date);
                      pstmt3y.setInt(2, slotParms.yy);
                      pstmt3y.setString(3, slotParms.user4);
                      pstmt3y.setString(4, slotParms.user4);
                      pstmt3y.setString(5, slotParms.user4);
                      pstmt3y.setString(6, slotParms.user4);
                      pstmt3y.setString(7, slotParms.user4);
                      rs = pstmt3y.executeQuery();
                  }

                  while (rs.next()) {

                     user1 = rs.getString("username1");
                     user2 = rs.getString("username2");
                     user3 = rs.getString("username3");
                     user4 = rs.getString("username4");
                     show1 = rs.getInt("show1");
                     show2 = rs.getInt("show2");
                     show3 = rs.getInt("show3");
                     show4 = rs.getInt("show4");
                     user5 = rs.getString("username5");
                     show5 = rs.getInt("show5");

                     if (user1.equalsIgnoreCase( slotParms.user4 ) && show1 == 1) {

                        count++;                      // count number or tee times in this week

                     } else {

                        if (user2.equalsIgnoreCase( slotParms.user4 ) && show2 == 1) {

                           count++;                      // count number or tee times in this week

                        } else {

                           if (user3.equalsIgnoreCase( slotParms.user4 ) && show3 == 1) {

                              count++;                      // count number or tee times in this week

                           } else {

                              if (user4.equalsIgnoreCase( slotParms.user4 ) && show4 == 1) {

                                 count++;                      // count number or tee times in this week

                              } else {

                                 if (user5.equalsIgnoreCase( slotParms.user4 ) && show5 == 1) {

                                    count++;                      // count number or tee times in this week
                                 }
                              }
                           }
                        }
                     }
                  }
               }       // end of IF mperiod = Year

               //
               //  Compare # of tee times in this period with max allowed for membership type
               //
               if (count >= mtimes)  {

                  if (!slotParms.player4.equals( slotParms.oldPlayer1 ) &&
                      !slotParms.player4.equals( slotParms.oldPlayer2 ) &&
                      !slotParms.player4.equals( slotParms.oldPlayer3 ) &&
                      !slotParms.player4.equals( slotParms.oldPlayer4 ) &&
                      !slotParms.player4.equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

                     error = true;                // reject this member
                     slotParms.mship = slotParms.mship4;
                     slotParms.player = slotParms.player4;
                     slotParms.period = mperiod;
                  }
               }
            }          // end of IF match found for player
         }          // end of player 4 if

         if (!slotParms.mship5.equals( "" )) {          // check if player 5 name specified

            ind = 1;             // init fields
            count = 0;
            mtimes = 0;
            mperiod = "";

            loop5:
            while (ind < parm.MAX_Mships+1) {

               if (slotParms.mship5.equals( mshipA[ind] )) {

                  mtimes = mtimesA[ind];            // match found - get number of rounds
                  mperiod = periodA[ind];           //               and period (week, month, year)
                  break loop5;
               }
               ind++;
            }

            if (mtimes != 0) {             // if match found for this player and there is no limit

               if (mperiod.equals( "Week" )) {       // if WEEK

                  pstmt2.clearParameters();        // get count from teecurr
                  pstmt2.setLong(1, slotParms.date);
                  pstmt2.setLong(2, dateStart);
                  pstmt2.setLong(3, dateEnd);
                  pstmt2.setString(4, slotParms.user5);
                  pstmt2.setString(5, slotParms.user5);
                  pstmt2.setString(6, slotParms.user5);
                  pstmt2.setString(7, slotParms.user5);
                  pstmt2.setString(8, slotParms.user5);
                  rs = pstmt2.executeQuery();

                  count = 0;

                  while (rs.next()) {

                     count++;                      // count number or tee times in this week
                  }

                  pstmt3.clearParameters();        // get count from teepast
                  pstmt3.setLong(1, slotParms.date);
                  pstmt3.setLong(2, dateStart);
                  pstmt3.setLong(3, dateEnd);
                  pstmt3.setString(4, slotParms.user5);
                  pstmt3.setString(5, slotParms.user5);
                  pstmt3.setString(6, slotParms.user5);
                  pstmt3.setString(7, slotParms.user5);
                  pstmt3.setString(8, slotParms.user5);
                  rs = pstmt3.executeQuery();

                  while (rs.next()) {

                     user1 = rs.getString("username1");
                     user2 = rs.getString("username2");
                     user3 = rs.getString("username3");
                     user4 = rs.getString("username4");
                     show1 = rs.getInt("show1");
                     show2 = rs.getInt("show2");
                     show3 = rs.getInt("show3");
                     show4 = rs.getInt("show4");
                     user5 = rs.getString("username5");
                     show5 = rs.getInt("show5");

                     if (user1.equalsIgnoreCase( slotParms.user5 ) && show1 == 1) {

                        count++;                      // count number or tee times in this week

                     } else {

                        if (user2.equalsIgnoreCase( slotParms.user5 ) && show2 == 1) {

                           count++;                      // count number or tee times in this week

                        } else {

                           if (user3.equalsIgnoreCase( slotParms.user5 ) && show3 == 1) {

                              count++;                      // count number or tee times in this week

                           } else {

                              if (user4.equalsIgnoreCase( slotParms.user5 ) && show4 == 1) {

                                 count++;                      // count number or tee times in this week

                              } else {

                                 if (user5.equalsIgnoreCase( slotParms.user5 ) && show5 == 1) {

                                    count++;                      // count number or tee times in this week
                                 }
                              }
                           }
                        }
                     }
                  }
               }       // end of IF mperiod = week

               if (mperiod.equals( "Month" )) {      // if MONTH

                  pstmt2m.clearParameters();        // get count from teecurr
                  pstmt2m.setLong(1, slotParms.date);
                  pstmt2m.setInt(2, slotParms.mm);
                  pstmt2m.setInt(3, slotParms.yy);
                  pstmt2m.setString(4, slotParms.user5);
                  pstmt2m.setString(5, slotParms.user5);
                  pstmt2m.setString(6, slotParms.user5);
                  pstmt2m.setString(7, slotParms.user5);
                  pstmt2m.setString(8, slotParms.user5);
                  rs = pstmt2m.executeQuery();

                  count = 0;

                  while (rs.next()) {

                     count++;                      // count number or tee times in this month
                  }

                  pstmt3m.clearParameters();        // get count from teepast
                  pstmt3m.setLong(1, slotParms.date);
                  pstmt3m.setInt(2, slotParms.mm);
                  pstmt3m.setInt(3, slotParms.yy);
                  pstmt3m.setString(4, slotParms.user5);
                  pstmt3m.setString(5, slotParms.user5);
                  pstmt3m.setString(6, slotParms.user5);
                  pstmt3m.setString(7, slotParms.user5);
                  pstmt3m.setString(8, slotParms.user5);
                  rs = pstmt3m.executeQuery();

                  while (rs.next()) {

                     user1 = rs.getString("username1");
                     user2 = rs.getString("username2");
                     user3 = rs.getString("username3");
                     user4 = rs.getString("username4");
                     show1 = rs.getInt("show1");
                     show2 = rs.getInt("show2");
                     show3 = rs.getInt("show3");
                     show4 = rs.getInt("show4");
                     user5 = rs.getString("username5");
                     show5 = rs.getInt("show5");

                     if (user1.equalsIgnoreCase( slotParms.user5 ) && show1 == 1) {

                        count++;                      // count number or tee times in this week

                     } else {

                        if (user2.equalsIgnoreCase( slotParms.user5 ) && show2 == 1) {

                           count++;                      // count number or tee times in this week

                        } else {

                           if (user3.equalsIgnoreCase( slotParms.user5 ) && show3 == 1) {

                              count++;                      // count number or tee times in this week

                           } else {

                              if (user4.equalsIgnoreCase( slotParms.user5 ) && show4 == 1) {

                                 count++;                      // count number or tee times in this week

                              } else {

                                 if (user5.equalsIgnoreCase( slotParms.user5 ) && show5 == 1) {

                                    count++;                      // count number or tee times in this week
                                 }
                              }
                           }
                        }
                     }
                  }
               }       // end of IF mperiod = Month

               if (mperiod.equals( "Year" )) {            // if Year

                  pstmt2y.clearParameters();             // get count from teecurr
                  pstmt2y.setLong(1, slotParms.date);
                  pstmt2y.setInt(2, slotParms.yy);
                  pstmt2y.setString(3, slotParms.user5);
                  pstmt2y.setString(4, slotParms.user5);
                  pstmt2y.setString(5, slotParms.user5);
                  pstmt2y.setString(6, slotParms.user5);
                  pstmt2y.setString(7, slotParms.user5);
                  rs = pstmt2y.executeQuery();

                  count = 0;

                  while (rs.next()) {

                     count++;                      // count number or tee times in this year
                  }

                  if (slotParms.club.equals("brooklawn")) {
                      pstmt3y.clearParameters();        // get count from teepast
                      pstmt3y.setLong(1, slotParms.date);
                      pstmt3y.setInt(2, slotParms.yy);
                      pstmt3y.setString(3, slotParms.user5);
                      pstmt3y.setString(4, slotParms.mship5);
                      pstmt3y.setString(5, slotParms.user5);
                      pstmt3y.setString(6, slotParms.mship5);
                      pstmt3y.setString(7, slotParms.user5);
                      pstmt3y.setString(8, slotParms.mship5);
                      pstmt3y.setString(9, slotParms.user5);
                      pstmt3y.setString(10, slotParms.mship5);
                      pstmt3y.setString(11, slotParms.user5);
                      pstmt3y.setString(12, slotParms.mship5);
                      rs = pstmt3y.executeQuery();
                  } else {
                      pstmt3y.clearParameters();        // get count from teepast
                      pstmt3y.setLong(1, slotParms.date);
                      pstmt3y.setInt(2, slotParms.yy);
                      pstmt3y.setString(3, slotParms.user5);
                      pstmt3y.setString(4, slotParms.user5);
                      pstmt3y.setString(5, slotParms.user5);
                      pstmt3y.setString(6, slotParms.user5);
                      pstmt3y.setString(7, slotParms.user5);
                      rs = pstmt3y.executeQuery();
                  }

                  while (rs.next()) {

                     user1 = rs.getString("username1");
                     user2 = rs.getString("username2");
                     user3 = rs.getString("username3");
                     user4 = rs.getString("username4");
                     show1 = rs.getInt("show1");
                     show2 = rs.getInt("show2");
                     show3 = rs.getInt("show3");
                     show4 = rs.getInt("show4");
                     user5 = rs.getString("username5");
                     show5 = rs.getInt("show5");

                     if (user1.equalsIgnoreCase( slotParms.user5 ) && show1 == 1) {

                        count++;                      // count number or tee times in this week

                     } else {

                        if (user2.equalsIgnoreCase( slotParms.user5 ) && show2 == 1) {

                           count++;                      // count number or tee times in this week

                        } else {

                           if (user3.equalsIgnoreCase( slotParms.user5 ) && show3 == 1) {

                              count++;                      // count number or tee times in this week

                           } else {

                              if (user4.equalsIgnoreCase( slotParms.user5 ) && show4 == 1) {

                                 count++;                      // count number or tee times in this week

                              } else {

                                 if (user5.equalsIgnoreCase( slotParms.user5 ) && show5 == 1) {

                                    count++;                      // count number or tee times in this week
                                 }
                              }
                           }
                        }
                     }
                  }
               }       // end of IF mperiod = Year

               //
               //  Compare # of tee times in this period with max allowed for membership type
               //
               if (count >= mtimes)  {

                  if (!slotParms.player5.equals( slotParms.oldPlayer1 ) &&
                      !slotParms.player5.equals( slotParms.oldPlayer2 ) &&
                      !slotParms.player5.equals( slotParms.oldPlayer3 ) &&
                      !slotParms.player5.equals( slotParms.oldPlayer4 ) &&
                      !slotParms.player5.equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

                     error = true;                // reject this member
                     slotParms.mship = slotParms.mship5;
                     slotParms.player = slotParms.player5;
                     slotParms.period = mperiod;
                  }
               }
            }          // end of IF match found for player
         }          // end of player 5 if


         //
         //  Custom for Philly Cricket Club - check membership types for max of 4 rounds per year
         //
         if (slotParms.club.equals( "philcricket" ) && error == false) {

            //
            // statements for year
            //
            pstmt2y = con.prepareStatement (
               "SELECT dd FROM teecurr2 WHERE date != ? AND yy = ? AND " +
                          "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?) AND courseName != 'St Martins'");

            pstmt3y = con.prepareStatement (
               "SELECT username1, username2, username3, username4, show1, show2, show3, show4, username5, show5 " +
               "FROM teepast2 WHERE date != ? AND yy = ? AND " +
                          "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?) AND courseName != 'St Martins'");

            String mtype1 = slotParms.mship1;    // NOW WE CHECK THE MSHIP TYPE!!!!!!!!!!!
            String mtype2 = slotParms.mship2;
            String mtype3 = slotParms.mship3;
            String mtype4 = slotParms.mship4;
            String mtype5 = slotParms.mship5;

            //
            //  Check each player for the specified member types
            //
            if (mtype1.equals( "Golf Stm Family" ) || mtype1.equals( "Golf Stm Ind." ) || mtype1.equals( "No Golf" )) {

               count = 0;

               pstmt2y.clearParameters();             // get count from teecurr
               pstmt2y.setLong(1, slotParms.date);
               pstmt2y.setInt(2, slotParms.yy);
               pstmt2y.setString(3, slotParms.user1);
               pstmt2y.setString(4, slotParms.user1);
               pstmt2y.setString(5, slotParms.user1);
               pstmt2y.setString(6, slotParms.user1);
               pstmt2y.setString(7, slotParms.user1);
               rs = pstmt2y.executeQuery();

               count = 0;

               while (rs.next()) {

                  count++;                      // count number or tee times in this year
               }

               pstmt3y.clearParameters();        // get count from teepast
               pstmt3y.setLong(1, slotParms.date);
               pstmt3y.setInt(2, slotParms.yy);
               pstmt3y.setString(3, slotParms.user1);
               pstmt3y.setString(4, slotParms.user1);
               pstmt3y.setString(5, slotParms.user1);
               pstmt3y.setString(6, slotParms.user1);
               pstmt3y.setString(7, slotParms.user1);
               rs = pstmt3y.executeQuery();

               while (rs.next()) {

                  user1 = rs.getString("username1");
                  user2 = rs.getString("username2");
                  user3 = rs.getString("username3");
                  user4 = rs.getString("username4");
                  show1 = rs.getInt("show1");
                  show2 = rs.getInt("show2");
                  show3 = rs.getInt("show3");
                  show4 = rs.getInt("show4");
                  user5 = rs.getString("username5");
                  show5 = rs.getInt("show5");

                  if (user1.equalsIgnoreCase( slotParms.user1 ) && show1 == 1) {

                     count++;

                  } else {

                     if (user2.equalsIgnoreCase( slotParms.user1 ) && show2 == 1) {

                        count++;

                     } else {

                        if (user3.equalsIgnoreCase( slotParms.user1 ) && show3 == 1) {

                           count++;

                        } else {

                           if (user4.equalsIgnoreCase( slotParms.user1 ) && show4 == 1) {

                              count++;

                           } else {

                              if (user5.equalsIgnoreCase( slotParms.user1 ) && show5 == 1) {

                                 count++;
                              }
                           }
                        }
                     }
                  }
               }       // end of WHILE

               //
               //  Compare # of tee times in this year with max allowed for member type
               //
               if (count >= 4)  {

                  if (!slotParms.player1.equals( slotParms.oldPlayer1 ) &&
                      !slotParms.player1.equals( slotParms.oldPlayer2 ) &&
                      !slotParms.player1.equals( slotParms.oldPlayer3 ) &&
                      !slotParms.player1.equals( slotParms.oldPlayer4 ) &&
                      !slotParms.player1.equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

                     error = true;                // reject this member
                     slotParms.mship = mtype1;
                     slotParms.player = slotParms.player1;
                     slotParms.period = "year";
                  }
               }
            }

            if (mtype2.equals( "Golf Stm Family" ) || mtype2.equals( "Golf Stm Ind." ) || mtype2.equals( "No Golf" )) {

               count = 0;

               pstmt2y.clearParameters();             // get count from teecurr
               pstmt2y.setLong(1, slotParms.date);
               pstmt2y.setInt(2, slotParms.yy);
               pstmt2y.setString(3, slotParms.user2);
               pstmt2y.setString(4, slotParms.user2);
               pstmt2y.setString(5, slotParms.user2);
               pstmt2y.setString(6, slotParms.user2);
               pstmt2y.setString(7, slotParms.user2);
               rs = pstmt2y.executeQuery();

               count = 0;

               while (rs.next()) {

                  count++;                      // count number or tee times in this year
               }

               pstmt3y.clearParameters();        // get count from teepast
               pstmt3y.setLong(1, slotParms.date);
               pstmt3y.setInt(2, slotParms.yy);
               pstmt3y.setString(3, slotParms.user2);
               pstmt3y.setString(4, slotParms.user2);
               pstmt3y.setString(5, slotParms.user2);
               pstmt3y.setString(6, slotParms.user2);
               pstmt3y.setString(7, slotParms.user2);
               rs = pstmt3y.executeQuery();

               while (rs.next()) {

                  user1 = rs.getString("username1");
                  user2 = rs.getString("username2");
                  user3 = rs.getString("username3");
                  user4 = rs.getString("username4");
                  show1 = rs.getInt("show1");
                  show2 = rs.getInt("show2");
                  show3 = rs.getInt("show3");
                  show4 = rs.getInt("show4");
                  user5 = rs.getString("username5");
                  show5 = rs.getInt("show5");

                  if (user1.equalsIgnoreCase( slotParms.user2 ) && show1 == 1) {

                     count++;

                  } else {

                     if (user2.equalsIgnoreCase( slotParms.user2 ) && show2 == 1) {

                        count++;

                     } else {

                        if (user3.equalsIgnoreCase( slotParms.user2 ) && show3 == 1) {

                           count++;

                        } else {

                           if (user4.equalsIgnoreCase( slotParms.user2 ) && show4 == 1) {

                              count++;

                           } else {

                              if (user5.equalsIgnoreCase( slotParms.user2 ) && show5 == 1) {

                                 count++;
                              }
                           }
                        }
                     }
                  }
               }       // end of WHILE

               //
               //  Compare # of tee times in this year with max allowed for member type
               //
               if (count >= 4)  {

                  if (!slotParms.player2.equals( slotParms.oldPlayer1 ) &&
                      !slotParms.player2.equals( slotParms.oldPlayer2 ) &&
                      !slotParms.player2.equals( slotParms.oldPlayer3 ) &&
                      !slotParms.player2.equals( slotParms.oldPlayer4 ) &&
                      !slotParms.player2.equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

                     error = true;                // reject this member
                     slotParms.mship = mtype2;
                     slotParms.player = slotParms.player2;
                     slotParms.period = "year";
                  }
               }
            }

            if (mtype3.equals( "Golf Stm Family" ) || mtype3.equals( "Golf Stm Ind." ) || mtype3.equals( "No Golf" )) {

               count = 0;

               pstmt2y.clearParameters();             // get count from teecurr
               pstmt2y.setLong(1, slotParms.date);
               pstmt2y.setInt(2, slotParms.yy);
               pstmt2y.setString(3, slotParms.user3);
               pstmt2y.setString(4, slotParms.user3);
               pstmt2y.setString(5, slotParms.user3);
               pstmt2y.setString(6, slotParms.user3);
               pstmt2y.setString(7, slotParms.user3);
               rs = pstmt2y.executeQuery();

               count = 0;

               while (rs.next()) {

                  count++;                      // count number or tee times in this year
               }

               pstmt3y.clearParameters();        // get count from teepast
               pstmt3y.setLong(1, slotParms.date);
               pstmt3y.setInt(2, slotParms.yy);
               pstmt3y.setString(3, slotParms.user3);
               pstmt3y.setString(4, slotParms.user3);
               pstmt3y.setString(5, slotParms.user3);
               pstmt3y.setString(6, slotParms.user3);
               pstmt3y.setString(7, slotParms.user3);
               rs = pstmt3y.executeQuery();

               while (rs.next()) {

                  user1 = rs.getString("username1");
                  user2 = rs.getString("username2");
                  user3 = rs.getString("username3");
                  user4 = rs.getString("username4");
                  show1 = rs.getInt("show1");
                  show2 = rs.getInt("show2");
                  show3 = rs.getInt("show3");
                  show4 = rs.getInt("show4");
                  user5 = rs.getString("username5");
                  show5 = rs.getInt("show5");

                  if (user1.equalsIgnoreCase( slotParms.user3 ) && show1 == 1) {

                     count++;

                  } else {

                     if (user2.equalsIgnoreCase( slotParms.user3 ) && show2 == 1) {

                        count++;

                     } else {

                        if (user3.equalsIgnoreCase( slotParms.user3 ) && show3 == 1) {

                           count++;

                        } else {

                           if (user4.equalsIgnoreCase( slotParms.user3 ) && show4 == 1) {

                              count++;

                           } else {

                              if (user5.equalsIgnoreCase( slotParms.user3 ) && show5 == 1) {

                                 count++;
                              }
                           }
                        }
                     }
                  }
               }       // end of WHILE

               //
               //  Compare # of tee times in this year with max allowed for member type
               //
               if (count >= 4)  {

                  if (!slotParms.player3.equals( slotParms.oldPlayer1 ) &&
                      !slotParms.player3.equals( slotParms.oldPlayer2 ) &&
                      !slotParms.player3.equals( slotParms.oldPlayer3 ) &&
                      !slotParms.player3.equals( slotParms.oldPlayer4 ) &&
                      !slotParms.player3.equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

                     error = true;                // reject this member
                     slotParms.mship = mtype3;
                     slotParms.player = slotParms.player3;
                     slotParms.period = "year";
                  }
               }
            }

            if (mtype4.equals( "Golf Stm Family" ) || mtype4.equals( "Golf Stm Ind." ) || mtype4.equals( "No Golf" )) {

               count = 0;

               pstmt2y.clearParameters();             // get count from teecurr
               pstmt2y.setLong(1, slotParms.date);
               pstmt2y.setInt(2, slotParms.yy);
               pstmt2y.setString(3, slotParms.user4);
               pstmt2y.setString(4, slotParms.user4);
               pstmt2y.setString(5, slotParms.user4);
               pstmt2y.setString(6, slotParms.user4);
               pstmt2y.setString(7, slotParms.user4);
               rs = pstmt2y.executeQuery();

               count = 0;

               while (rs.next()) {

                  count++;                      // count number or tee times in this year
               }

               pstmt3y.clearParameters();        // get count from teepast
               pstmt3y.setLong(1, slotParms.date);
               pstmt3y.setInt(2, slotParms.yy);
               pstmt3y.setString(3, slotParms.user4);
               pstmt3y.setString(4, slotParms.user4);
               pstmt3y.setString(5, slotParms.user4);
               pstmt3y.setString(6, slotParms.user4);
               pstmt3y.setString(7, slotParms.user4);
               rs = pstmt3y.executeQuery();

               while (rs.next()) {

                  user1 = rs.getString("username1");
                  user2 = rs.getString("username2");
                  user3 = rs.getString("username3");
                  user4 = rs.getString("username4");
                  show1 = rs.getInt("show1");
                  show2 = rs.getInt("show2");
                  show3 = rs.getInt("show3");
                  show4 = rs.getInt("show4");
                  user5 = rs.getString("username5");
                  show5 = rs.getInt("show5");

                  if (user1.equalsIgnoreCase( slotParms.user4 ) && show1 == 1) {

                     count++;

                  } else {

                     if (user2.equalsIgnoreCase( slotParms.user4 ) && show2 == 1) {

                        count++;

                     } else {

                        if (user3.equalsIgnoreCase( slotParms.user4 ) && show3 == 1) {

                           count++;

                        } else {

                           if (user4.equalsIgnoreCase( slotParms.user4 ) && show4 == 1) {

                              count++;

                           } else {

                              if (user5.equalsIgnoreCase( slotParms.user4 ) && show5 == 1) {

                                 count++;
                              }
                           }
                        }
                     }
                  }
               }       // end of WHILE

               //
               //  Compare # of tee times in this year with max allowed for member type
               //
               if (count >= 4)  {

                  if (!slotParms.player4.equals( slotParms.oldPlayer1 ) &&
                      !slotParms.player4.equals( slotParms.oldPlayer2 ) &&
                      !slotParms.player4.equals( slotParms.oldPlayer3 ) &&
                      !slotParms.player4.equals( slotParms.oldPlayer4 ) &&
                      !slotParms.player4.equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

                     error = true;                // reject this member
                     slotParms.mship = mtype4;
                     slotParms.player = slotParms.player4;
                     slotParms.period = "year";
                  }
               }
            }

            if (mtype5.equals( "Golf Stm Family" ) || mtype5.equals( "Golf Stm Ind." ) || mtype5.equals( "No Golf" )) {

               count = 0;

               pstmt2y.clearParameters();             // get count from teecurr
               pstmt2y.setLong(1, slotParms.date);
               pstmt2y.setInt(2, slotParms.yy);
               pstmt2y.setString(3, slotParms.user5);
               pstmt2y.setString(4, slotParms.user5);
               pstmt2y.setString(5, slotParms.user5);
               pstmt2y.setString(6, slotParms.user5);
               pstmt2y.setString(7, slotParms.user5);
               rs = pstmt2y.executeQuery();

               count = 0;

               while (rs.next()) {

                  count++;                      // count number or tee times in this year
               }

               pstmt3y.clearParameters();        // get count from teepast
               pstmt3y.setLong(1, slotParms.date);
               pstmt3y.setInt(2, slotParms.yy);
               pstmt3y.setString(3, slotParms.user5);
               pstmt3y.setString(4, slotParms.user5);
               pstmt3y.setString(5, slotParms.user5);
               pstmt3y.setString(6, slotParms.user5);
               pstmt3y.setString(7, slotParms.user5);
               rs = pstmt3y.executeQuery();

               while (rs.next()) {

                  user1 = rs.getString("username1");
                  user2 = rs.getString("username2");
                  user3 = rs.getString("username3");
                  user4 = rs.getString("username4");
                  show1 = rs.getInt("show1");
                  show2 = rs.getInt("show2");
                  show3 = rs.getInt("show3");
                  show4 = rs.getInt("show4");
                  user5 = rs.getString("username5");
                  show5 = rs.getInt("show5");

                  if (user1.equalsIgnoreCase( slotParms.user5 ) && show1 == 1) {

                     count++;

                  } else {

                     if (user2.equalsIgnoreCase( slotParms.user5 ) && show2 == 1) {

                        count++;

                     } else {

                        if (user3.equalsIgnoreCase( slotParms.user5 ) && show3 == 1) {

                           count++;

                        } else {

                           if (user4.equalsIgnoreCase( slotParms.user5 ) && show4 == 1) {

                              count++;

                           } else {

                              if (user5.equalsIgnoreCase( slotParms.user5 ) && show5 == 1) {

                                 count++;
                              }
                           }
                        }
                     }
                  }
               }       // end of WHILE

               //
               //  Compare # of tee times in this year with max allowed for member type
               //
               if (count >= 4)  {

                  if (!slotParms.player5.equals( slotParms.oldPlayer1 ) &&
                      !slotParms.player5.equals( slotParms.oldPlayer2 ) &&
                      !slotParms.player5.equals( slotParms.oldPlayer3 ) &&
                      !slotParms.player5.equals( slotParms.oldPlayer4 ) &&
                      !slotParms.player5.equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

                     error = true;                // reject this member
                     slotParms.mship = mtype5;
                     slotParms.player = slotParms.player5;
                     slotParms.period = "year";
                  }
               }
            }
         }       // end of IF Philly Cricket


         //
         //  Close out the sql statements
         //
         pstmt2.close();
         pstmt3.close();
         pstmt2m.close();
         pstmt3m.close();
         pstmt2y.close();
         pstmt3y.close();

      }
      catch (SQLException e1) {

         throw new Exception("SQL Error Checking Max Rounds - verifySlot.checkMaxRounds " + e1.getMessage());
      }

      catch (Exception e) {

         throw new Exception("Exception Checking Max Rounds - verifySlot.checkMaxRounds " + e.getMessage());
      }

   }        // end of IF skipCheck

   return(error);

 }


/**
 //************************************************************************
 //
 //  checkPortage - checks Associate members for max rounds per month & year.
 //
 //************************************************************************
 **/

 public static boolean checkPortage(parmSlot slotParms, Connection con)
         throws Exception {


   //Statement stmt = null;
   ResultSet rs = null;

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(slotParms.root_activity_id, con);          // allocate a parm block

   //int year = 0;
   //int month = 0;
   //int dayNum = 0;
   int countm = 0;
   int county = 0;
   //int ind = 0;
   //int i = 0;
   int show1 = 0;
   int show2 = 0;
   int show3 = 0;
   int show4 = 0;
   int show5 = 0;
   int maxM = 0;
   int maxY = 0;
   int mtimes = 2;                     // max rounds per month per mship
   int ytimes = 6;                     // max rounds per year per mship

   String mship = "Associate";         // Membership type to check
   String mNum1 = "";
   String mNum2 = "";
   String mNum3 = "";
   String mNum4 = "";
   String mNum5 = "";
   String mship1 = slotParms.mship1;
   String mship2 = slotParms.mship2;
   String mship3 = slotParms.mship3;
   String mship4 = slotParms.mship4;
   String mship5 = slotParms.mship5;

   boolean error = false;


   try {

      //
      // statements for month
      //
      PreparedStatement pstmt2m = con.prepareStatement (
         "SELECT mNum1, mNum2, mNum3, mNum4, mNum5 " +
         "FROM teecurr2 WHERE mm = ? AND yy = ? AND " +
                    "(mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?)");

      PreparedStatement pstmt3m = con.prepareStatement (
         "SELECT mNum1, mNum2, mNum3, mNum4, show1, show2, show3, show4, mNum5, show5 " +
         "FROM teepast2 WHERE mm = ? AND yy = ? AND " +
                    "(mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?)");
      //
      // statements for year
      //
      PreparedStatement pstmt2y = con.prepareStatement (
         "SELECT mNum1, mNum2, mNum3, mNum4, mNum5 " +
         "FROM teecurr2 WHERE yy = ? AND " +
                    "(mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?)");

      PreparedStatement pstmt3y = con.prepareStatement (
         "SELECT mNum1, mNum2, mNum3, mNum4, show1, show2, show3, show4, mNum5, show5 " +
         "FROM teepast2 WHERE yy = ? AND " +
                    "(mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?)");


      //
      //  Player 1 - Do not bother checking player if player was already in the tee time
      //
      if (!slotParms.player1.equals( slotParms.oldPlayer1 ) &&
          !slotParms.player1.equals( slotParms.oldPlayer2 ) &&
          !slotParms.player1.equals( slotParms.oldPlayer3 ) &&
          !slotParms.player1.equals( slotParms.oldPlayer4 ) &&
          !slotParms.player1.equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

         if (mship1.equals( mship )) {          // check if player 1 is an Associate member

            countm = 0;                  // init counters
            county = 0;

            //
            //  Check for 2 rounds per Month
            //
            pstmt2m.clearParameters();                    // get count from teecurr
            pstmt2m.setInt(1, slotParms.mm);
            pstmt2m.setInt(2, slotParms.yy);
            pstmt2m.setString(3, slotParms.mNum1);
            pstmt2m.setString(4, slotParms.mNum1);
            pstmt2m.setString(5, slotParms.mNum1);
            pstmt2m.setString(6, slotParms.mNum1);
            pstmt2m.setString(7, slotParms.mNum1);
            rs = pstmt2m.executeQuery();

            while (rs.next()) {

               mNum1 = rs.getString("mNum1");
               mNum2 = rs.getString("mNum2");
               mNum3 = rs.getString("mNum3");
               mNum4 = rs.getString("mNum4");
               mNum5 = rs.getString("mNum5");

               if (mNum1.equals( slotParms.mNum1 )) {    // if same family

                  countm++;                              // bump month counter
               }
               if (mNum2.equals( slotParms.mNum1 )) {    // if same family

                  countm++;                              // bump month counter
               }
               if (mNum3.equals( slotParms.mNum1 )) {    // if same family

                  countm++;                              // bump month counter
               }
               if (mNum4.equals( slotParms.mNum1 )) {    // if same family

                  countm++;                              // bump month counter
               }
               if (mNum5.equals( slotParms.mNum1 )) {    // if same family

                  countm++;                              // bump month counter
               }
            }

            pstmt3m.clearParameters();        // get count from teepast
            pstmt3m.setInt(1, slotParms.mm);
            pstmt3m.setInt(2, slotParms.yy);
            pstmt3m.setString(3, slotParms.mNum1);
            pstmt3m.setString(4, slotParms.mNum1);
            pstmt3m.setString(5, slotParms.mNum1);
            pstmt3m.setString(6, slotParms.mNum1);
            pstmt3m.setString(7, slotParms.mNum1);
            rs = pstmt3m.executeQuery();

            while (rs.next()) {

               mNum1 = rs.getString("mNum1");
               mNum2 = rs.getString("mNum2");
               mNum3 = rs.getString("mNum3");
               mNum4 = rs.getString("mNum4");
               show1 = rs.getInt("show1");
               show2 = rs.getInt("show2");
               show3 = rs.getInt("show3");
               show4 = rs.getInt("show4");
               mNum5 = rs.getString("mNum5");
               show5 = rs.getInt("show5");

               if (mNum1.equals( slotParms.mNum1 ) && show1 == 1) {

                  countm++;                      // bump month counter
               }
               if (mNum2.equals( slotParms.mNum1 ) && show2 == 1) {

                  countm++;                      // bump month counter
               }
               if (mNum3.equals( slotParms.mNum1 ) && show3 == 1) {

                  countm++;                      // bump month counter
               }
               if (mNum4.equals( slotParms.mNum1 ) && show4 == 1) {

                  countm++;                      // bump month counter
               }
               if (mNum5.equals( slotParms.mNum1 ) && show5 == 1) {

                  countm++;                      // bump month counter
               }
            }

            //
            //  Gather the Year count (6 per year)
            //
            pstmt2y.clearParameters();             // get count from teecurr
            pstmt2y.setInt(1, slotParms.yy);
            pstmt2y.setString(2, slotParms.mNum1);
            pstmt2y.setString(3, slotParms.mNum1);
            pstmt2y.setString(4, slotParms.mNum1);
            pstmt2y.setString(5, slotParms.mNum1);
            pstmt2y.setString(6, slotParms.mNum1);
            rs = pstmt2y.executeQuery();

            while (rs.next()) {

               mNum1 = rs.getString("mNum1");
               mNum2 = rs.getString("mNum2");
               mNum3 = rs.getString("mNum3");
               mNum4 = rs.getString("mNum4");
               mNum5 = rs.getString("mNum5");

               if (mNum1.equals( slotParms.mNum1 )) {    // if same family

                  county++;                              // bump year counter
               }
               if (mNum2.equals( slotParms.mNum1 )) {    // if same family

                  county++;                              // bump year counter
               }
               if (mNum3.equals( slotParms.mNum1 )) {    // if same family

                  county++;                              // bump year counter
               }
               if (mNum4.equals( slotParms.mNum1 )) {    // if same family

                  county++;                              // bump year counter
               }
               if (mNum5.equals( slotParms.mNum1 )) {    // if same family

                  county++;                              // bump year counter
               }
            }

            pstmt3y.clearParameters();        // get count from teepast
            pstmt3y.setInt(1, slotParms.yy);
            pstmt3y.setString(2, slotParms.mNum1);
            pstmt3y.setString(3, slotParms.mNum1);
            pstmt3y.setString(4, slotParms.mNum1);
            pstmt3y.setString(5, slotParms.mNum1);
            pstmt3y.setString(6, slotParms.mNum1);
            rs = pstmt3y.executeQuery();

            while (rs.next()) {

               mNum1 = rs.getString("mNum1");
               mNum2 = rs.getString("mNum2");
               mNum3 = rs.getString("mNum3");
               mNum4 = rs.getString("mNum4");
               show1 = rs.getInt("show1");
               show2 = rs.getInt("show2");
               show3 = rs.getInt("show3");
               show4 = rs.getInt("show4");
               mNum5 = rs.getString("mNum5");
               show5 = rs.getInt("show5");

               if (mNum1.equals( slotParms.mNum1 ) && show1 == 1) {

                  county++;                      // bump year counter
               }
               if (mNum2.equals( slotParms.mNum1 ) && show2 == 1) {

                  county++;                      // bump year counter
               }
               if (mNum3.equals( slotParms.mNum1 ) && show3 == 1) {

                  county++;                      // bump year counter
               }
               if (mNum4.equals( slotParms.mNum1 ) && show4 == 1) {

                  county++;                      // bump year counter
               }
               if (mNum5.equals( slotParms.mNum1 ) && show5 == 1) {

                  county++;                      // bump year counter
               }
            }

            //
            //  Compare # of tee times in these periods with max allowed
            //
            maxM = mtimes;      // default max allowed times
            maxY = ytimes;

            if (!slotParms.oldPlayer1.equals( "" )) {     // if this tee time already existed

               maxM++;                                    // add one more since this player has been counted
               maxY++;
            }

            if (countm >= maxM)  {     // if month limit reached

               error = true;                // reject this member
               slotParms.mship = mship;
               slotParms.player = slotParms.player1;
               slotParms.period = "Month";

            } else {

               if (county >= maxY)  {     // if year limit reached

                  error = true;                // reject this member
                  slotParms.mship = mship;
                  slotParms.player = slotParms.player1;
                  slotParms.period = "Year";
               }
            }
         }
      }          // end of player 1 checks

      if (error == false) {      // if no error yet

         //
         //  Player 2 - Do not bother checking player if player was already in the tee time
         //
         if (!slotParms.player2.equals( slotParms.oldPlayer1 ) &&
             !slotParms.player2.equals( slotParms.oldPlayer2 ) &&
             !slotParms.player2.equals( slotParms.oldPlayer3 ) &&
             !slotParms.player2.equals( slotParms.oldPlayer4 ) &&
             !slotParms.player2.equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

            //
            //  Count rounds for player if Associate mship and not already counted
            //
            if (mship2.equals( mship ) && !slotParms.mNum2.equals( slotParms.mNum1 )) {

               countm = 0;                  // init counters
               county = 0;

               //
               //  Check for 2 rounds per Month
               //
               pstmt2m.clearParameters();                    // get count from teecurr
               pstmt2m.setInt(1, slotParms.mm);
               pstmt2m.setInt(2, slotParms.yy);
               pstmt2m.setString(3, slotParms.mNum2);
               pstmt2m.setString(4, slotParms.mNum2);
               pstmt2m.setString(5, slotParms.mNum2);
               pstmt2m.setString(6, slotParms.mNum2);
               pstmt2m.setString(7, slotParms.mNum2);
               rs = pstmt2m.executeQuery();

               while (rs.next()) {

                  mNum1 = rs.getString("mNum1");
                  mNum2 = rs.getString("mNum2");
                  mNum3 = rs.getString("mNum3");
                  mNum4 = rs.getString("mNum4");
                  mNum5 = rs.getString("mNum5");

                  if (mNum1.equals( slotParms.mNum2 )) {    // if same family

                     countm++;                              // bump month counter
                  }
                  if (mNum2.equals( slotParms.mNum2 )) {    // if same family

                     countm++;                              // bump month counter
                  }
                  if (mNum3.equals( slotParms.mNum2 )) {    // if same family

                     countm++;                              // bump month counter
                  }
                  if (mNum4.equals( slotParms.mNum2 )) {    // if same family

                     countm++;                              // bump month counter
                  }
                  if (mNum5.equals( slotParms.mNum2 )) {    // if same family

                     countm++;                              // bump month counter
                  }
               }

               pstmt3m.clearParameters();        // get count from teepast
               pstmt3m.setInt(1, slotParms.mm);
               pstmt3m.setInt(2, slotParms.yy);
               pstmt3m.setString(3, slotParms.mNum2);
               pstmt3m.setString(4, slotParms.mNum2);
               pstmt3m.setString(5, slotParms.mNum2);
               pstmt3m.setString(6, slotParms.mNum2);
               pstmt3m.setString(7, slotParms.mNum2);
               rs = pstmt3m.executeQuery();

               while (rs.next()) {

                  mNum1 = rs.getString("mNum1");
                  mNum2 = rs.getString("mNum2");
                  mNum3 = rs.getString("mNum3");
                  mNum4 = rs.getString("mNum4");
                  show1 = rs.getInt("show1");
                  show2 = rs.getInt("show2");
                  show3 = rs.getInt("show3");
                  show4 = rs.getInt("show4");
                  mNum5 = rs.getString("mNum5");
                  show5 = rs.getInt("show5");

                  if (mNum1.equals( slotParms.mNum2 ) && show1 == 1) {

                     countm++;                      // bump month counter
                  }
                  if (mNum2.equals( slotParms.mNum2 ) && show2 == 1) {

                     countm++;                      // bump month counter
                  }
                  if (mNum3.equals( slotParms.mNum2 ) && show3 == 1) {

                     countm++;                      // bump month counter
                  }
                  if (mNum4.equals( slotParms.mNum2 ) && show4 == 1) {

                     countm++;                      // bump month counter
                  }
                  if (mNum5.equals( slotParms.mNum2 ) && show5 == 1) {

                     countm++;                      // bump month counter
                  }
               }

               //
               //  Gather the Year count (6 per year)
               //
               pstmt2y.clearParameters();             // get count from teecurr
               pstmt2y.setInt(1, slotParms.yy);
               pstmt2y.setString(2, slotParms.mNum2);
               pstmt2y.setString(3, slotParms.mNum2);
               pstmt2y.setString(4, slotParms.mNum2);
               pstmt2y.setString(5, slotParms.mNum2);
               pstmt2y.setString(6, slotParms.mNum2);
               rs = pstmt2y.executeQuery();

               while (rs.next()) {

                  mNum1 = rs.getString("mNum1");
                  mNum2 = rs.getString("mNum2");
                  mNum3 = rs.getString("mNum3");
                  mNum4 = rs.getString("mNum4");
                  mNum5 = rs.getString("mNum5");

                  if (mNum1.equals( slotParms.mNum2 )) {    // if same family

                     county++;                              // bump year counter
                  }
                  if (mNum2.equals( slotParms.mNum2 )) {    // if same family

                     county++;                              // bump year counter
                  }
                  if (mNum3.equals( slotParms.mNum2 )) {    // if same family

                     county++;                              // bump year counter
                  }
                  if (mNum4.equals( slotParms.mNum2 )) {    // if same family

                     county++;                              // bump year counter
                  }
                  if (mNum5.equals( slotParms.mNum2 )) {    // if same family

                     county++;                              // bump year counter
                  }
               }

               pstmt3y.clearParameters();        // get count from teepast
               pstmt3y.setInt(1, slotParms.yy);
               pstmt3y.setString(2, slotParms.mNum2);
               pstmt3y.setString(3, slotParms.mNum2);
               pstmt3y.setString(4, slotParms.mNum2);
               pstmt3y.setString(5, slotParms.mNum2);
               pstmt3y.setString(6, slotParms.mNum2);
               rs = pstmt3y.executeQuery();

               while (rs.next()) {

                  mNum1 = rs.getString("mNum1");
                  mNum2 = rs.getString("mNum2");
                  mNum3 = rs.getString("mNum3");
                  mNum4 = rs.getString("mNum4");
                  show1 = rs.getInt("show1");
                  show2 = rs.getInt("show2");
                  show3 = rs.getInt("show3");
                  show4 = rs.getInt("show4");
                  mNum5 = rs.getString("mNum5");
                  show5 = rs.getInt("show5");

                  if (mNum1.equals( slotParms.mNum2 ) && show1 == 1) {

                     county++;                      // bump year counter
                  }
                  if (mNum2.equals( slotParms.mNum2 ) && show2 == 1) {

                     county++;                      // bump year counter
                  }
                  if (mNum3.equals( slotParms.mNum2 ) && show3 == 1) {

                     county++;                      // bump year counter
                  }
                  if (mNum4.equals( slotParms.mNum2 ) && show4 == 1) {

                     county++;                      // bump year counter
                  }
                  if (mNum5.equals( slotParms.mNum2 ) && show5 == 1) {

                     county++;                      // bump year counter
                  }
               }

               //
               //  Compare # of tee times in these periods with max allowed
               //
               maxM = mtimes;      // default max allowed times
               maxY = ytimes;

               if (!slotParms.oldPlayer1.equals( "" )) {     // if this tee time already existed

                  maxM++;                                 // add one more since this player has been counted
                  maxY++;
               }

               if (countm >= maxM)  {     // if month limit reached

                  error = true;                // reject this member
                  slotParms.mship = mship;
                  slotParms.player = slotParms.player2;
                  slotParms.period = "Month";

               } else {

                  if (county >= maxY)  {     // if year limit reached

                     error = true;                // reject this member
                     slotParms.mship = mship;
                     slotParms.player = slotParms.player2;
                     slotParms.period = "Year";
                  }
               }
            }
         }          // end of player 2 checks
      }

      if (error == false) {      // if no error yet

         //
         //  Player 3 - Do not bother checking player if player was already in the tee time
         //
         if (!slotParms.player3.equals( slotParms.oldPlayer1 ) &&
             !slotParms.player3.equals( slotParms.oldPlayer2 ) &&
             !slotParms.player3.equals( slotParms.oldPlayer3 ) &&
             !slotParms.player3.equals( slotParms.oldPlayer4 ) &&
             !slotParms.player3.equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

            //
            //  Count rounds for player if Associate mship and not already counted
            //
            if (mship3.equals( mship ) && !slotParms.mNum3.equals( slotParms.mNum1 ) &&
                !slotParms.mNum3.equals( slotParms.mNum2 )) {

               countm = 0;                  // init counters
               county = 0;

               //
               //  Check for 2 rounds per Month
               //
               pstmt2m.clearParameters();                    // get count from teecurr
               pstmt2m.setInt(1, slotParms.mm);
               pstmt2m.setInt(2, slotParms.yy);
               pstmt2m.setString(3, slotParms.mNum3);
               pstmt2m.setString(4, slotParms.mNum3);
               pstmt2m.setString(5, slotParms.mNum3);
               pstmt2m.setString(6, slotParms.mNum3);
               pstmt2m.setString(7, slotParms.mNum3);
               rs = pstmt2m.executeQuery();

               while (rs.next()) {

                  mNum1 = rs.getString("mNum1");
                  mNum2 = rs.getString("mNum2");
                  mNum3 = rs.getString("mNum3");
                  mNum4 = rs.getString("mNum4");
                  mNum5 = rs.getString("mNum5");

                  if (mNum1.equals( slotParms.mNum3 )) {    // if same family

                     countm++;                              // bump month counter
                  }
                  if (mNum2.equals( slotParms.mNum3 )) {    // if same family

                     countm++;                              // bump month counter
                  }
                  if (mNum3.equals( slotParms.mNum3 )) {    // if same family

                     countm++;                              // bump month counter
                  }
                  if (mNum4.equals( slotParms.mNum3 )) {    // if same family

                     countm++;                              // bump month counter
                  }
                  if (mNum5.equals( slotParms.mNum3 )) {    // if same family

                     countm++;                              // bump month counter
                  }
               }

               pstmt3m.clearParameters();        // get count from teepast
               pstmt3m.setInt(1, slotParms.mm);
               pstmt3m.setInt(2, slotParms.yy);
               pstmt3m.setString(3, slotParms.mNum3);
               pstmt3m.setString(4, slotParms.mNum3);
               pstmt3m.setString(5, slotParms.mNum3);
               pstmt3m.setString(6, slotParms.mNum3);
               pstmt3m.setString(7, slotParms.mNum3);
               rs = pstmt3m.executeQuery();

               while (rs.next()) {

                  mNum1 = rs.getString("mNum1");
                  mNum2 = rs.getString("mNum2");
                  mNum3 = rs.getString("mNum3");
                  mNum4 = rs.getString("mNum4");
                  show1 = rs.getInt("show1");
                  show2 = rs.getInt("show2");
                  show3 = rs.getInt("show3");
                  show4 = rs.getInt("show4");
                  mNum5 = rs.getString("mNum5");
                  show5 = rs.getInt("show5");

                  if (mNum1.equals( slotParms.mNum3 ) && show1 == 1) {

                     countm++;                      // bump month counter
                  }
                  if (mNum2.equals( slotParms.mNum3 ) && show2 == 1) {

                     countm++;                      // bump month counter
                  }
                  if (mNum3.equals( slotParms.mNum3 ) && show3 == 1) {

                     countm++;                      // bump month counter
                  }
                  if (mNum4.equals( slotParms.mNum3 ) && show4 == 1) {

                     countm++;                      // bump month counter
                  }
                  if (mNum5.equals( slotParms.mNum3 ) && show5 == 1) {

                     countm++;                      // bump month counter
                  }
               }

               //
               //  Gather the Year count (6 per year)
               //
               pstmt2y.clearParameters();             // get count from teecurr
               pstmt2y.setInt(1, slotParms.yy);
               pstmt2y.setString(2, slotParms.mNum3);
               pstmt2y.setString(3, slotParms.mNum3);
               pstmt2y.setString(4, slotParms.mNum3);
               pstmt2y.setString(5, slotParms.mNum3);
               pstmt2y.setString(6, slotParms.mNum3);
               rs = pstmt2y.executeQuery();

               while (rs.next()) {

                  mNum1 = rs.getString("mNum1");
                  mNum2 = rs.getString("mNum2");
                  mNum3 = rs.getString("mNum3");
                  mNum4 = rs.getString("mNum4");
                  mNum5 = rs.getString("mNum5");

                  if (mNum1.equals( slotParms.mNum3 )) {    // if same family

                     county++;                              // bump year counter
                  }
                  if (mNum2.equals( slotParms.mNum3 )) {    // if same family

                     county++;                              // bump year counter
                  }
                  if (mNum3.equals( slotParms.mNum3 )) {    // if same family

                     county++;                              // bump year counter
                  }
                  if (mNum4.equals( slotParms.mNum3 )) {    // if same family

                     county++;                              // bump year counter
                  }
                  if (mNum5.equals( slotParms.mNum3 )) {    // if same family

                     county++;                              // bump year counter
                  }
               }

               pstmt3y.clearParameters();        // get count from teepast
               pstmt3y.setInt(1, slotParms.yy);
               pstmt3y.setString(2, slotParms.mNum3);
               pstmt3y.setString(3, slotParms.mNum3);
               pstmt3y.setString(4, slotParms.mNum3);
               pstmt3y.setString(5, slotParms.mNum3);
               pstmt3y.setString(6, slotParms.mNum3);
               rs = pstmt3y.executeQuery();

               while (rs.next()) {

                  mNum1 = rs.getString("mNum1");
                  mNum2 = rs.getString("mNum2");
                  mNum3 = rs.getString("mNum3");
                  mNum4 = rs.getString("mNum4");
                  show1 = rs.getInt("show1");
                  show2 = rs.getInt("show2");
                  show3 = rs.getInt("show3");
                  show4 = rs.getInt("show4");
                  mNum5 = rs.getString("mNum5");
                  show5 = rs.getInt("show5");

                  if (mNum1.equals( slotParms.mNum3 ) && show1 == 1) {

                     county++;                      // bump year counter
                  }
                  if (mNum2.equals( slotParms.mNum3 ) && show2 == 1) {

                     county++;                      // bump year counter
                  }
                  if (mNum3.equals( slotParms.mNum3 ) && show3 == 1) {

                     county++;                      // bump year counter
                  }
                  if (mNum4.equals( slotParms.mNum3 ) && show4 == 1) {

                     county++;                      // bump year counter
                  }
                  if (mNum5.equals( slotParms.mNum3 ) && show5 == 1) {

                     county++;                      // bump year counter
                  }
               }

               //
               //  Compare # of tee times in these periods with max allowed
               //
               maxM = mtimes;      // default max allowed times
               maxY = ytimes;

               if (!slotParms.oldPlayer1.equals( "" )) {     // if this tee time already existed

                  maxM++;                                 // add one more since this player has been counted
                  maxY++;
               }

               if (countm >= maxM)  {     // if month limit reached

                  error = true;                // reject this member
                  slotParms.mship = mship;
                  slotParms.player = slotParms.player3;
                  slotParms.period = "Month";

               } else {

                  if (county >= maxY)  {     // if year limit reached

                     error = true;                // reject this member
                     slotParms.mship = mship;
                     slotParms.player = slotParms.player3;
                     slotParms.period = "Year";
                  }
               }
            }
         }          // end of player 3 checks
      }

      if (error == false) {      // if no error yet

         //
         //  Player 4 - Do not bother checking player if player was already in the tee time
         //
         if (!slotParms.player4.equals( slotParms.oldPlayer1 ) &&
             !slotParms.player4.equals( slotParms.oldPlayer2 ) &&
             !slotParms.player4.equals( slotParms.oldPlayer3 ) &&
             !slotParms.player4.equals( slotParms.oldPlayer4 ) &&
             !slotParms.player4.equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

            //
            //  Count rounds for player if Associate mship and not already counted
            //
            if (mship4.equals( mship ) && !slotParms.mNum4.equals( slotParms.mNum1 ) &&
                !slotParms.mNum4.equals( slotParms.mNum2 ) && !slotParms.mNum4.equals( slotParms.mNum3 )) {

               countm = 0;                  // init counters
               county = 0;

               //
               //  Check for 2 rounds per Month
               //
               pstmt2m.clearParameters();                    // get count from teecurr
               pstmt2m.setInt(1, slotParms.mm);
               pstmt2m.setInt(2, slotParms.yy);
               pstmt2m.setString(3, slotParms.mNum4);
               pstmt2m.setString(4, slotParms.mNum4);
               pstmt2m.setString(5, slotParms.mNum4);
               pstmt2m.setString(6, slotParms.mNum4);
               pstmt2m.setString(7, slotParms.mNum4);
               rs = pstmt2m.executeQuery();

               while (rs.next()) {

                  mNum1 = rs.getString("mNum1");
                  mNum2 = rs.getString("mNum2");
                  mNum3 = rs.getString("mNum3");
                  mNum4 = rs.getString("mNum4");
                  mNum5 = rs.getString("mNum5");

                  if (mNum1.equals( slotParms.mNum4 )) {    // if same family

                     countm++;                              // bump month counter
                  }
                  if (mNum2.equals( slotParms.mNum4 )) {    // if same family

                     countm++;                              // bump month counter
                  }
                  if (mNum3.equals( slotParms.mNum4 )) {    // if same family

                     countm++;                              // bump month counter
                  }
                  if (mNum4.equals( slotParms.mNum4 )) {    // if same family

                     countm++;                              // bump month counter
                  }
                  if (mNum5.equals( slotParms.mNum4 )) {    // if same family

                     countm++;                              // bump month counter
                  }
               }

               pstmt3m.clearParameters();        // get count from teepast
               pstmt3m.setInt(1, slotParms.mm);
               pstmt3m.setInt(2, slotParms.yy);
               pstmt3m.setString(3, slotParms.mNum4);
               pstmt3m.setString(4, slotParms.mNum4);
               pstmt3m.setString(5, slotParms.mNum4);
               pstmt3m.setString(6, slotParms.mNum4);
               pstmt3m.setString(7, slotParms.mNum4);
               rs = pstmt3m.executeQuery();

               while (rs.next()) {

                  mNum1 = rs.getString("mNum1");
                  mNum2 = rs.getString("mNum2");
                  mNum3 = rs.getString("mNum3");
                  mNum4 = rs.getString("mNum4");
                  show1 = rs.getInt("show1");
                  show2 = rs.getInt("show2");
                  show3 = rs.getInt("show3");
                  show4 = rs.getInt("show4");
                  mNum5 = rs.getString("mNum5");
                  show5 = rs.getInt("show5");

                  if (mNum1.equals( slotParms.mNum4 ) && show1 == 1) {

                     countm++;                      // bump month counter
                  }
                  if (mNum2.equals( slotParms.mNum4 ) && show2 == 1) {

                     countm++;                      // bump month counter
                  }
                  if (mNum3.equals( slotParms.mNum4 ) && show3 == 1) {

                     countm++;                      // bump month counter
                  }
                  if (mNum4.equals( slotParms.mNum4 ) && show4 == 1) {

                     countm++;                      // bump month counter
                  }
                  if (mNum5.equals( slotParms.mNum4 ) && show5 == 1) {

                     countm++;                      // bump month counter
                  }
               }

               //
               //  Gather the Year count (6 per year)
               //
               pstmt2y.clearParameters();             // get count from teecurr
               pstmt2y.setInt(1, slotParms.yy);
               pstmt2y.setString(2, slotParms.mNum4);
               pstmt2y.setString(3, slotParms.mNum4);
               pstmt2y.setString(4, slotParms.mNum4);
               pstmt2y.setString(5, slotParms.mNum4);
               pstmt2y.setString(6, slotParms.mNum4);
               rs = pstmt2y.executeQuery();

               while (rs.next()) {

                  mNum1 = rs.getString("mNum1");
                  mNum2 = rs.getString("mNum2");
                  mNum3 = rs.getString("mNum3");
                  mNum4 = rs.getString("mNum4");
                  mNum5 = rs.getString("mNum5");

                  if (mNum1.equals( slotParms.mNum4 )) {    // if same family

                     county++;                              // bump year counter
                  }
                  if (mNum2.equals( slotParms.mNum4 )) {    // if same family

                     county++;                              // bump year counter
                  }
                  if (mNum3.equals( slotParms.mNum4 )) {    // if same family

                     county++;                              // bump year counter
                  }
                  if (mNum4.equals( slotParms.mNum4 )) {    // if same family

                     county++;                              // bump year counter
                  }
                  if (mNum5.equals( slotParms.mNum4 )) {    // if same family

                     county++;                              // bump year counter
                  }
               }

               pstmt3y.clearParameters();        // get count from teepast
               pstmt3y.setInt(1, slotParms.yy);
               pstmt3y.setString(2, slotParms.mNum4);
               pstmt3y.setString(3, slotParms.mNum4);
               pstmt3y.setString(4, slotParms.mNum4);
               pstmt3y.setString(5, slotParms.mNum4);
               pstmt3y.setString(6, slotParms.mNum4);
               rs = pstmt3y.executeQuery();

               while (rs.next()) {

                  mNum1 = rs.getString("mNum1");
                  mNum2 = rs.getString("mNum2");
                  mNum3 = rs.getString("mNum3");
                  mNum4 = rs.getString("mNum4");
                  show1 = rs.getInt("show1");
                  show2 = rs.getInt("show2");
                  show3 = rs.getInt("show3");
                  show4 = rs.getInt("show4");
                  mNum5 = rs.getString("mNum5");
                  show5 = rs.getInt("show5");

                  if (mNum1.equals( slotParms.mNum4 ) && show1 == 1) {

                     county++;                      // bump year counter
                  }
                  if (mNum2.equals( slotParms.mNum4 ) && show2 == 1) {

                     county++;                      // bump year counter
                  }
                  if (mNum3.equals( slotParms.mNum4 ) && show3 == 1) {

                     county++;                      // bump year counter
                  }
                  if (mNum4.equals( slotParms.mNum4 ) && show4 == 1) {

                     county++;                      // bump year counter
                  }
                  if (mNum5.equals( slotParms.mNum4 ) && show5 == 1) {

                     county++;                      // bump year counter
                  }
               }

               //
               //  Compare # of tee times in these periods with max allowed
               //
               maxM = mtimes;      // default max allowed times
               maxY = ytimes;

               if (!slotParms.oldPlayer1.equals( "" )) {     // if this tee time already existed

                  maxM++;                                 // add one more since this player has been counted
                  maxY++;
               }

               if (countm >= maxM)  {     // if month limit reached

                  error = true;                // reject this member
                  slotParms.mship = mship;
                  slotParms.player = slotParms.player4;
                  slotParms.period = "Month";

               } else {

                  if (county >= maxY)  {     // if year limit reached

                     error = true;                // reject this member
                     slotParms.mship = mship;
                     slotParms.player = slotParms.player4;
                     slotParms.period = "Year";
                  }
               }
            }
         }          // end of player 4 checks
      }

      if (error == false) {      // if no error yet

         //
         //  Player 5 - Do not bother checking player if player was already in the tee time
         //
         if (!slotParms.player5.equals( slotParms.oldPlayer1 ) &&
             !slotParms.player5.equals( slotParms.oldPlayer2 ) &&
             !slotParms.player5.equals( slotParms.oldPlayer3 ) &&
             !slotParms.player5.equals( slotParms.oldPlayer4 ) &&
             !slotParms.player5.equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

            //
            //  Count rounds for player if Associate mship and not already counted
            //
            if (mship5.equals( mship ) && !slotParms.mNum5.equals( slotParms.mNum1 ) &&
                !slotParms.mNum5.equals( slotParms.mNum2 ) && !slotParms.mNum5.equals( slotParms.mNum3 ) &&
                !slotParms.mNum5.equals( slotParms.mNum4 )) {

               countm = 0;                  // init counters
               county = 0;

               //
               //  Check for 2 rounds per Month
               //
               pstmt2m.clearParameters();                    // get count from teecurr
               pstmt2m.setInt(1, slotParms.mm);
               pstmt2m.setInt(2, slotParms.yy);
               pstmt2m.setString(3, slotParms.mNum5);
               pstmt2m.setString(4, slotParms.mNum5);
               pstmt2m.setString(5, slotParms.mNum5);
               pstmt2m.setString(6, slotParms.mNum5);
               pstmt2m.setString(7, slotParms.mNum5);
               rs = pstmt2m.executeQuery();

               while (rs.next()) {

                  mNum1 = rs.getString("mNum1");
                  mNum2 = rs.getString("mNum2");
                  mNum3 = rs.getString("mNum3");
                  mNum4 = rs.getString("mNum4");
                  mNum5 = rs.getString("mNum5");

                  if (mNum1.equals( slotParms.mNum5 )) {    // if same family

                     countm++;                              // bump month counter
                  }
                  if (mNum2.equals( slotParms.mNum5 )) {    // if same family

                     countm++;                              // bump month counter
                  }
                  if (mNum3.equals( slotParms.mNum5 )) {    // if same family

                     countm++;                              // bump month counter
                  }
                  if (mNum4.equals( slotParms.mNum5 )) {    // if same family

                     countm++;                              // bump month counter
                  }
                  if (mNum5.equals( slotParms.mNum5 )) {    // if same family

                     countm++;                              // bump month counter
                  }
               }

               pstmt3m.clearParameters();        // get count from teepast
               pstmt3m.setInt(1, slotParms.mm);
               pstmt3m.setInt(2, slotParms.yy);
               pstmt3m.setString(3, slotParms.mNum5);
               pstmt3m.setString(4, slotParms.mNum5);
               pstmt3m.setString(5, slotParms.mNum5);
               pstmt3m.setString(6, slotParms.mNum5);
               pstmt3m.setString(7, slotParms.mNum5);
               rs = pstmt3m.executeQuery();

               while (rs.next()) {

                  mNum1 = rs.getString("mNum1");
                  mNum2 = rs.getString("mNum2");
                  mNum3 = rs.getString("mNum3");
                  mNum4 = rs.getString("mNum4");
                  show1 = rs.getInt("show1");
                  show2 = rs.getInt("show2");
                  show3 = rs.getInt("show3");
                  show4 = rs.getInt("show4");
                  mNum5 = rs.getString("mNum5");
                  show5 = rs.getInt("show5");

                  if (mNum1.equals( slotParms.mNum5 ) && show1 == 1) {

                     countm++;                      // bump month counter
                  }
                  if (mNum2.equals( slotParms.mNum5 ) && show2 == 1) {

                     countm++;                      // bump month counter
                  }
                  if (mNum3.equals( slotParms.mNum5 ) && show3 == 1) {

                     countm++;                      // bump month counter
                  }
                  if (mNum4.equals( slotParms.mNum5 ) && show4 == 1) {

                     countm++;                      // bump month counter
                  }
                  if (mNum5.equals( slotParms.mNum5 ) && show5 == 1) {

                     countm++;                      // bump month counter
                  }
               }

               //
               //  Gather the Year count (6 per year)
               //
               pstmt2y.clearParameters();             // get count from teecurr
               pstmt2y.setInt(1, slotParms.yy);
               pstmt2y.setString(2, slotParms.mNum5);
               pstmt2y.setString(3, slotParms.mNum5);
               pstmt2y.setString(4, slotParms.mNum5);
               pstmt2y.setString(5, slotParms.mNum5);
               pstmt2y.setString(6, slotParms.mNum5);
               rs = pstmt2y.executeQuery();

               while (rs.next()) {

                  mNum1 = rs.getString("mNum1");
                  mNum2 = rs.getString("mNum2");
                  mNum3 = rs.getString("mNum3");
                  mNum4 = rs.getString("mNum4");
                  mNum5 = rs.getString("mNum5");

                  if (mNum1.equals( slotParms.mNum5 )) {    // if same family

                     county++;                              // bump year counter
                  }
                  if (mNum2.equals( slotParms.mNum5 )) {    // if same family

                     county++;                              // bump year counter
                  }
                  if (mNum3.equals( slotParms.mNum5 )) {    // if same family

                     county++;                              // bump year counter
                  }
                  if (mNum4.equals( slotParms.mNum5 )) {    // if same family

                     county++;                              // bump year counter
                  }
                  if (mNum5.equals( slotParms.mNum5 )) {    // if same family

                     county++;                              // bump year counter
                  }
               }

               pstmt3y.clearParameters();        // get count from teepast
               pstmt3y.setInt(1, slotParms.yy);
               pstmt3y.setString(2, slotParms.mNum5);
               pstmt3y.setString(3, slotParms.mNum5);
               pstmt3y.setString(4, slotParms.mNum5);
               pstmt3y.setString(5, slotParms.mNum5);
               pstmt3y.setString(6, slotParms.mNum5);
               rs = pstmt3y.executeQuery();

               while (rs.next()) {

                  mNum1 = rs.getString("mNum1");
                  mNum2 = rs.getString("mNum2");
                  mNum3 = rs.getString("mNum3");
                  mNum4 = rs.getString("mNum4");
                  show1 = rs.getInt("show1");
                  show2 = rs.getInt("show2");
                  show3 = rs.getInt("show3");
                  show4 = rs.getInt("show4");
                  mNum5 = rs.getString("mNum5");
                  show5 = rs.getInt("show5");

                  if (mNum1.equals( slotParms.mNum5 ) && show1 == 1) {

                     county++;                      // bump year counter
                  }
                  if (mNum2.equals( slotParms.mNum5 ) && show2 == 1) {

                     county++;                      // bump year counter
                  }
                  if (mNum3.equals( slotParms.mNum5 ) && show3 == 1) {

                     county++;                      // bump year counter
                  }
                  if (mNum4.equals( slotParms.mNum5 ) && show4 == 1) {

                     county++;                      // bump year counter
                  }
                  if (mNum5.equals( slotParms.mNum5 ) && show5 == 1) {

                     county++;                      // bump year counter
                  }
               }

               //
               //  Compare # of tee times in these periods with max allowed
               //
               maxM = mtimes;      // default max allowed times
               maxY = ytimes;

               if (!slotParms.oldPlayer1.equals( "" )) {     // if this tee time already existed

                  maxM++;                                 // add one more since this player has been counted
                  maxY++;
               }

               if (countm >= maxM)  {     // if month limit reached

                  error = true;                // reject this member
                  slotParms.mship = mship;
                  slotParms.player = slotParms.player5;
                  slotParms.period = "Month";

               } else {

                  if (county >= maxY)  {     // if year limit reached

                     error = true;                // reject this member
                     slotParms.mship = mship;
                     slotParms.player = slotParms.player5;
                     slotParms.period = "Year";
                  }
               }
            }
         }          // end of player 5 checks
      }

      pstmt2m.close();
      pstmt3m.close();
      pstmt2y.close();
      pstmt3y.close();

   }
   catch (SQLException e1) {

      throw new Exception("SQL Error Checking Portage Max Rounds - verifySlot.checkPortage " + e1.getMessage());
   }

   catch (Exception e) {

      throw new Exception("Exception Checking Portage Max Rounds - verifySlot.checkPortage " + e.getMessage());
   }

   return(error);
 }


/**
 //************************************************************************
 //
 //  checkGuestNames - checks for guest names - required for Hazeltine (name must be provided)
 //
 //    HAZELTINE CUSTOM PROCESSING
 //
 //************************************************************************
 **/

 public static boolean checkGuestNames(parmSlot slotParms, Connection con) {


   boolean error = false;

   //int count = 0;
   //int count2 = 0;

   String name = "";
   String [] gName = new String [5];    // array to hold the Guest Names specified
   String [] gType = new String [5];    // array to hold the Guest Types specified

   String club = "hazeltine";

   int i = 0;

   //
   //  init string arrays
   //
   for (i=0; i < 5; i++) {

      gName[i] = "";
      gType[i] = "";
   }

   //
   //  Determine which player positions need to be tested.
   //  Do not check players that have already been checked.
   //
   i = 0;

   if (!slotParms.player1.equals( slotParms.oldPlayer1 )) {     // if player name is new or has changed

      if (!slotParms.g1.equals( "" )) {

         gType[i] = slotParms.g1;        // get guest type
         gName[i] = slotParms.player1;   // get name entered
         i++;
      }
   }
   if (!slotParms.player2.equals( slotParms.oldPlayer2 )) {     // if player name is new or has changed

      if (!slotParms.g2.equals( "" )) {

         gType[i] = slotParms.g2;        // get guest type
         gName[i] = slotParms.player2;   // get name entered
         i++;
      }
   }
   if (!slotParms.player3.equals( slotParms.oldPlayer3 )) {     // if player name is new or has changed

      if (!slotParms.g3.equals( "" )) {

         gType[i] = slotParms.g3;        // get guest type
         gName[i] = slotParms.player3;   // get name entered
         i++;
      }
   }
   if (!slotParms.player4.equals( slotParms.oldPlayer4 )) {     // if player name is new or has changed

      if (!slotParms.g4.equals( "" )) {

         gType[i] = slotParms.g4;        // get guest type
         gName[i] = slotParms.player4;   // get name entered
         i++;
      }
   }
   if (!slotParms.player5.equals( slotParms.oldPlayer5 )) {     // if player name is new or has changed

      if (!slotParms.g5.equals( "" )) {

         gType[i] = slotParms.g5;        // get guest type
         gName[i] = slotParms.player5;   // get name entered
         i++;
      }
   }

   //
   //  Verify that a name was provided
   //
   i = 0;
   loop1:
   while (i < 5) {

      if (!gType[i].equals( "" )) {       // if guest type specified

         name = gName[i];                 // get player name specified

         error = checkGstName(name, gType[i], club);      // go check for a name

         if (error == true) {                       // if name not specified

            break loop1;
         }

      } else {        // done when no guest type
         break loop1;
      }
      i++;
   }                  // end of while
   return(error);
 }


/**
 //************************************************************************
 //
 //  checkMaxGuests - checks for maximum number of guests exceeded per member or tee time
 //
 //************************************************************************
 **/

 public static boolean checkMaxGuests(parmSlot slotParms, Connection con)
         throws Exception {


   ResultSet rs = null;
   ResultSet rs2 = null;
   PreparedStatement pstmt = null;
   PreparedStatement pstmt2 = null;


   boolean error = false;
   boolean check = false;
   boolean check1 = false;
   boolean check2 = false;
   boolean check3 = false;
   boolean check4 = false;
   boolean check5 = false;

   int i = 0;
   int grest_id = 0;
   int guests = 0;        // number of restricted guests in tee time

   String rcourse = "";
   String rest_fb = "";
   String grest_recurr = "";
   String per = "";                           // per = 'Member' or 'Tee Time'

   //String [] rguest = new String [slotParms.MAX_Guests];       // array to hold the Guest Restriction's guest types
   ArrayList<String> rguest = new ArrayList<String>();

   String [] oldPlayers = new String [5];    // array to hold the old player values


   if (slotParms.fb == 0) {                   // is Tee time for Front 9?

      slotParms.sfb = "Front";
   }

   if (slotParms.fb == 1) {                   // is it Back 9?

      slotParms.sfb = "Back";
   }

   try {

      pstmt = con.prepareStatement (
         "SELECT * " +
         "FROM guestres2 WHERE sdate <= ? AND edate >= ? AND " +
         "stime <= ? AND etime >= ? AND " +
         ((slotParms.activity_id == 0) ? "activity_id = 0" : "? IN (locations)"));

      pstmt.clearParameters();
      pstmt.setLong(1, slotParms.date);
      pstmt.setLong(2, slotParms.date);
      pstmt.setInt(3, slotParms.time);
      pstmt.setInt(4, slotParms.time);
      if (slotParms.activity_id > 0) pstmt.setInt(5, slotParms.activity_id);

      rs = pstmt.executeQuery();

      loop1:
      while (rs.next()) {

         slotParms.rest_name = rs.getString("name");    // get name for error message
         grest_recurr = rs.getString("recurr");
         grest_id = rs.getInt("id");
         slotParms.grest_num = rs.getInt("num_guests");
         rcourse = rs.getString("courseName");
         rest_fb = rs.getString("fb");
         per = rs.getString("per");

         // now look up the guest types for this restriction
         pstmt2 = con.prepareStatement (
                 "SELECT guest_type FROM guestres2_gtypes WHERE guestres_id = ?");

         pstmt2.clearParameters();
         pstmt2.setInt(1, grest_id);

         rs2 = pstmt2.executeQuery();

         rguest.clear(); // reset

         while ( rs2.next() ) {

            rguest.add(rs2.getString("guest_type"));

         }

         pstmt2.close();

         check = false;       // init 'check guests' flag
         check1 = false;
         check2 = false;
         check3 = false;
         check4 = false;
         check5 = false;

         guests = 0;          // reset # of guests in tee time

/*
         //
         //   Change any guest types that are null - for tests below
         //
         i = 0;
         while (i < slotParms.MAX_Guests) {

            if (rguest[i].equals( "" )) {

               rguest[i] = "$@#!^&*";      // make so it won't match player name
            }
            i++;
         }         // end of while loop
*/

         //
         //  Check if for activity or if golf make sure course & fb matches that specified in restriction
         //
         if ( (int)slotParms.activity_id > 0 ||
             (
              (rcourse.equals( "-ALL-" ) || rcourse.equals( slotParms.course )) &&
              (rest_fb.equals( "Both" ) || rest_fb.equals( slotParms.sfb ))
             )

            ) {

            if (!checkRestSuspend(-99, grest_id, (int)slotParms.date, slotParms.time, slotParms.day, slotParms.course, con)) {        // check if this guest restriction is suspended for this time

                //
                //  We must check the recurrence for this day (Monday, etc.) and guest types
                //
                //     gx = guest types specified in player name fields
                //     rguest[x] = guest types from restriction gotten above
                //
                if ( grest_recurr.equalsIgnoreCase( "every " + slotParms.day ) ||
                     grest_recurr.equalsIgnoreCase( "every day" ) ||
                     (grest_recurr.equalsIgnoreCase( "all weekdays" ) &&
                      !slotParms.day.equalsIgnoreCase( "saturday" ) &&
                      !slotParms.day.equalsIgnoreCase( "sunday" )
                     ) ||
                     (grest_recurr.equalsIgnoreCase( "all weekends" ) &&
                      ( slotParms.day.equalsIgnoreCase( "saturday" ) || slotParms.day.equalsIgnoreCase( "sunday" ) )
                     )
                   ) {

                      i = 0;
                      ploop1:
                      while (i < rguest.size()) {

                         if (!slotParms.g1.equals( "" )) {
                            if (slotParms.g1.equals( rguest.get(i) )) {
                               check1 = true;                 // indicate player1 is guest
                               guests++;                      // bump number of restricted guests in tee time
                            }
                         }
                         if (!slotParms.g2.equals( "" )) {
                            if (slotParms.g2.equals( rguest.get(i) )) {
                               check2 = true;                 // indicate player2 is guest
                               guests++;                      // bump number of restricted guests in tee time
                            }
                         }
                         if (!slotParms.g3.equals( "" )) {
                            if (slotParms.g3.equals( rguest.get(i) )) {
                               check3 = true;                 // indicate player3 is guest
                               guests++;                      // bump number of restricted guests in tee time
                            }
                         }
                         if (!slotParms.g4.equals( "" )) {
                            if (slotParms.g4.equals( rguest.get(i) )) {
                               check4 = true;                 // indicate player4 is guest
                               guests++;                      // bump number of restricted guests in tee time
                            }
                         }
                         if (!slotParms.g5.equals( "" )) {
                            if (slotParms.g5.equals( rguest.get(i) )) {
                               check5 = true;                 // indicate player5 is guest
                               guests++;                      // bump number of restricted guests in tee time
                            }
                         }
                         i++;
                      }

                }

            } // end IF recurr matches

         } // end of IF course & fb matches

         //
         //  If any player position contains a restricted guest, then check if it already was in the
         //  tee time.  If so, then we do not have to check them as they might have been approved by
         //  the pro.  Allow for the old players being shifted up if a player was removed.  The order
         //  of the original guests cannot be changed in order for this to work.
         //
         oldPlayers[0] = slotParms.oldPlayer1;
         oldPlayers[1] = slotParms.oldPlayer2;
         oldPlayers[2] = slotParms.oldPlayer3;
         oldPlayers[3] = slotParms.oldPlayer4;
         oldPlayers[4] = slotParms.oldPlayer5;

         i = 0;

         if (check1 == true) {   // if player contains guest that is restricted

            check = true;       // init as 'check the guests'

            loopg1:
            while (i < 5) {

               if (slotParms.player1.equals( oldPlayers[i] )) {   // if already approved

                  check = false;            // ok to skip this guest
                  i++;                      // bump to next position
                  break loopg1;             // exit loop
               }
               i++;
            }
         }
         if (check2 == true && check == false) {   // if player contains guest that is restricted

            check = true;       // init as 'check the guests'

            loopg2:
            while (i < 5) {    // DO NOT reset i (so we start we we left off above)

               if (slotParms.player2.equals( oldPlayers[i] )) {   // if already approved

                  check = false;            // ok to skip this guest
                  i++;                      // bump to next position
                  break loopg2;             // exit loop
               }
               i++;
            }
         }
         if (check3 == true && check == false) {   // if player contains guest that is restricted

            check = true;       // init as 'check the guests'

            loopg3:
            while (i < 5) {    // DO NOT reset i (so we start we we left off above)

               if (slotParms.player3.equals( oldPlayers[i] )) {   // if already approved

                  check = false;            // ok to skip this guest
                  i++;                      // bump to next position
                  break loopg3;             // exit loop
               }
               i++;
            }
         }
         if (check4 == true && check == false) {   // if player contains guest that is restricted

            check = true;       // init as 'check the guests'

            loopg4:
            while (i < 5) {    // DO NOT reset i (so we start we we left off above)

               if (slotParms.player4.equals( oldPlayers[i] )) {   // if already approved

                  check = false;            // ok to skip this guest
                  i++;                      // bump to next position
                  break loopg4;             // exit loop
               }
               i++;
            }
         }
         if (check5 == true && check == false) {   // if player contains guest that is restricted

            check = true;       // init as 'check the guests'

            loopg5:
            while (i < 5) {    // DO NOT reset i (so we start we we left off above)

               if (slotParms.player5.equals( oldPlayers[i] )) {   // if already approved

                  check = false;            // ok to skip this guest
                  i++;                      // bump to next position
                  break loopg5;             // exit loop
               }
               i++;
            }
         }

         //
         //  Now see if we must check for max number of guests exceeded
         //
         if (check == true) {   // if restriction exists for this day and time

            // if num of guests req'd (guests) > num allowed (grest_num) per member
            //
            //       to get here guests is > 0
            //       grest_num is 0 - 3
            //       members is 0 - 5
            //       per is 'Member' or 'Tee Time'
            //
            if (slotParms.grest_num == 0) {         // no guests allowed

               error = true;                        // set error flag
               slotParms.grest_per = per;           // save this rest's per value
               break loop1;                         // done checking - exit while loop
            }

            if (per.equals( "Member" )) {           // if restriction is 'per member'

               if (slotParms.members < 2) {

                  if (guests > slotParms.grest_num) {     // if 1 member (or none) and more guests than allowed

                     error = true;                  // set error flag
                     slotParms.grest_per = per;     // save this rest's per value
                     break loop1;                   // done checking - exit while loop
                  }
               }
               if (slotParms.members == 2) {        // if > 2, then must be < 1 guest per mem

                  if (slotParms.grest_num == 1) {

                     if (guests > 2) {              // if 1 allowed and more than 1 each

                        error = true;               // set error flag
                        slotParms.grest_per = per;  // save this rest's per value
                        break loop1;                // done checking - exit while loop
                     }
                  }
               }

               if (slotParms.club.equals("hudsonnatl")) {  // if Hudson Nat'l only 1 guest allowed per member (by assignment)

                   if (slotParms.grest_num == 1 &&
                      ((!slotParms.user1.equals("") && !slotParms.g2.equals("") && !slotParms.g3.equals("")) ||
                       (!slotParms.user2.equals("") && !slotParms.g3.equals("") && !slotParms.g4.equals("")))) {

                        error = true;               // set error flag
                        slotParms.grest_per = per;  // save this rest's per value
                        break loop1;                // done checking - exit while loop
                   }
               }

            } else {      // per Tee Time

               if (guests > slotParms.grest_num) {  // if more guests than allowed per tee time

                  error = true;                     // set error flag
                  slotParms.grest_per = per;        // save this rest's per value
                  break loop1;                      // done checking - exit while loop
               }
            }
         }
      }   // end of rs (loop1) while loop

      if (error == true && slotParms.club.equals("interlachen")) {  // if error and Interlachen - check for exceptions on Tues - Friday

         //
         //  Interlachen - if a weekday between 10:00 and 11:30, allow up to 3 groups to have up to 3 guests (more than one)
         //
         if (!slotParms.day.equals( "Saturday" ) && !slotParms.day.equals( "Sunday" ) && !slotParms.day.equals( "Monday" ) &&
              slotParms.time > 959 && slotParms.time < 1131) {

            error = false;          // ignore this restriction, custom will catch the '3 guest' rule (see verifyCustom)
         }
      }

   } catch (Exception e) {

      throw new Exception("Error Checking Guest Rest - verifySlot: " + e.getMessage());

   } finally {

      try { rs.close(); }
      catch (Exception ignore) {}

      try { pstmt.close(); }
      catch (Exception ignore) {}

      try { rs2.close(); }
      catch (Exception ignore) {}

      try { pstmt2.close(); }
      catch (Exception ignore) {}

   }

   return(error);

 }


/**
 //************************************************************************
 //
 //  checkGuestQuota - checks for maximum number of guests exceeded per member
 //                    or per membership during a specified period.
 //
 //************************************************************************
 **/

 public static boolean checkGuestQuota(parmSlot slotParms, Connection con)
         throws Exception {

   ResultSet rs = null;
   ResultSet rs2 = null;
   PreparedStatement pstmt = null;
   PreparedStatement pstmt2 = null;

   boolean error = false;
   boolean check = false;

   int i = 0;
   int guests1 = 0;             // number of restricted guests in tee time
   int guests2 = 0;
   int guests3 = 0;
   int guests4 = 0;
   int guests5 = 0;
   int guests = 0;

   int stime = 0;
   int etime = 0;

   long sdate = 0;
   long edate = 0;

   String rcourse = "";
   String rest_fb = "";
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
   String per = "";                        // per = 'Member' or 'Tee Time'
   //String errorMsg = "";

   //String [] rguest = new String [slotParms.MAX_Guests];    // array to hold the Guest Restriction's guest types
   ArrayList<String> rguest = new ArrayList<String>();


   if (slotParms.fb == 0) {                   // is Tee time for Front 9?

      slotParms.sfb = "Front";
   }

   if (slotParms.fb == 1) {                   // is it Back 9?

      slotParms.sfb = "Back";
   }

   try {

      pstmt = con.prepareStatement (
         "SELECT * " +
         "FROM guestqta4 WHERE sdate <= ? AND edate >= ? AND " +
         "stime <= ? AND etime >= ? AND " +
         ((slotParms.activity_id == 0) ? "activity_id = 0" : "? IN (locations)"));

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, slotParms.date);
      pstmt.setLong(2, slotParms.date);
      pstmt.setInt(3, slotParms.time);
      pstmt.setInt(4, slotParms.time);
      if (slotParms.activity_id > 0) pstmt.setInt(5, slotParms.activity_id);

      rs = pstmt.executeQuery();      // execute the prepared stmt

      loop1:
      while ( rs.next() ) {

         sdate = rs.getLong("sdate");
         stime = rs.getInt("stime");
         edate = rs.getLong("edate");
         etime = rs.getInt("etime");
         slotParms.grest_num = rs.getInt("num_guests");
         rcourse = rs.getString("courseName");
         rest_fb = rs.getString("fb");
         per = rs.getString("per");

         // now look up the guest types for this guest quota
         pstmt2 = con.prepareStatement (
                 "SELECT guest_type FROM guestqta4_gtypes WHERE guestqta_id = ?");

         pstmt2.clearParameters();
         pstmt2.setInt(1, rs.getInt("id"));

         rs2 = pstmt2.executeQuery();

         rguest.clear(); // reset

         while ( rs2.next() ) {

            rguest.add(rs2.getString("guest_type"));

         }
         pstmt2.close();

         check = false;       // init 'check guests' flag
         userg1 = "";         // init usernames
         userg2 = "";
         userg3 = "";
         userg4 = "";
         userg5 = "";
/*
         //
         //   Change any guest types that are null - for tests below
         //
         i = 0;
         while (i < slotParms.MAX_Guests) {

            if (rguest[i].equals( "" )) {

               rguest[i] = "$@#!^&*";      // make so it won't match player name
            }
            i++;
         }         // end of while loop
*/
         //
         //  Check golf then make sure course and f/b match that specified in restriction
         //
         if ( slotParms.activity_id != 0 ||
                 ( slotParms.activity_id == 0 &&
                    ( rcourse.equals( "-ALL-" ) || rcourse.equals( slotParms.course ) ) &&
                    ( rest_fb.equals( "Both" ) || rest_fb.equals( slotParms.sfb ) )) ) {

            //  compare guest types in tee time against those specified in restriction
            i = 0;
            ploop1:
            while (i < rguest.size()) {

              //
              //     slotParms.gx = guest types specified in player name fields
              //     rguest[x] = guest types from restriction gotten above
              //
              if (!slotParms.g1.equals( "" )) {
                 if (slotParms.g1.equals( rguest.get(i) )) {
                    check = true;                  // indicate check num of guests
                    userg1 = slotParms.userg1;       // save member associated with this guest
                 }
              }
              if (!slotParms.g2.equals( "" )) {
                 if (slotParms.g2.equals( rguest.get(i) )) {
                    check = true;                  // indicate check num of guests
                    userg2 = slotParms.userg2;       // save member associated with this guest
                 }
              }
              if (!slotParms.g3.equals( "" )) {
                 if (slotParms.g3.equals( rguest.get(i) )) {
                    check = true;                  // indicate check num of guests
                    userg3 = slotParms.userg3;       // save member associated with this guest
                 }
              }
              if (!slotParms.g4.equals( "" )) {
                 if (slotParms.g4.equals( rguest.get(i) )) {
                    check = true;                  // indicate check num of guests
                    userg4 = slotParms.userg4;       // save member associated with this guest
                 }
              }
              if (!slotParms.g5.equals( "" )) {
                 if (slotParms.g5.equals( rguest.get(i) )) {
                    check = true;                  // indicate check num of guests
                    userg5 = slotParms.userg5;       // save member associated with this guest
                 }
              }
              i++;
            }
         }      // end of IF course matches

         if (check == true) {   // if restriction exists for this day and time and there are guests in tee time

            //
            //  Determine the member assigned to the guest and calculate their quota count
            //
            guests1 = 0;          // init # of guests for each member
            guests2 = 0;
            guests3 = 0;
            guests4 = 0;
            guests5 = 0;

            //
            //  Check each member for duplicates and count these guests first
            //
            if (!userg1.equals( "" )) {

               guests1++;               // count the guest

               if (userg1.equals( userg2 )) {

                  guests1++;            // count the guest
                  userg2 = "";          // remove dup
               }
               if (userg1.equals( userg3 )) {

                  guests1++;            // count the guest
                  userg3 = "";          // remove dup
               }
               if (userg1.equals( userg4 )) {

                  guests1++;            // count the guest
                  userg4 = "";          // remove dup
               }
               if (userg1.equals( userg5 )) {

                  guests1++;            // count the guest
                  userg5 = "";          // remove dup
               }

               // go count the number of guests for this member
               guests = countGuests(con, userg1, slotParms, sdate, edate, stime, etime, rest_fb, rcourse, rguest);

               guests1 += guests;         // add to total
            }

            if (!userg2.equals( "" )) {

               guests2++;               // count the guest

               if (userg2.equals( userg3 )) {

                  guests2++;            // count the guest
                  userg3 = "";          // remove dup
               }
               if (userg2.equals( userg4 )) {

                  guests2++;            // count the guest
                  userg4 = "";          // remove dup
               }
               if (userg2.equals( userg5 )) {

                  guests2++;            // count the guest
                  userg5 = "";          // remove dup
               }

               // go count the number of guests for this member
               guests = countGuests(con, userg2, slotParms, sdate, edate, stime, etime, rest_fb, rcourse, rguest);

               guests2 += guests;         // add to total
            }

            if (!userg3.equals( "" )) {

               guests3++;               // count the guest

               if (userg3.equals( userg4 )) {

                  guests3++;            // count the guest
                  userg4 = "";          // remove dup
               }
               if (userg3.equals( userg5 )) {

                  guests3++;            // count the guest
                  userg5 = "";          // remove dup
               }

               // go count the number of guests for this member
               guests = countGuests(con, userg3, slotParms, sdate, edate, stime, etime, rest_fb, rcourse, rguest);

               guests3 += guests;         // add to total
            }

            if (!userg4.equals( "" )) {

               guests4++;               // count the guest

               if (userg4.equals( userg5 )) {

                  guests4++;            // count the guest
                  userg5 = "";          // remove dup
               }

               // go count the number of guests for this member
               guests = countGuests(con, userg4, slotParms, sdate, edate, stime, etime, rest_fb, rcourse, rguest);

               guests4 += guests;         // add to total
            }

            if (!userg5.equals( "" )) {

               guests5++;               // count the guest

               // go count the number of guests for this member
               guests = countGuests(con, userg5, slotParms, sdate, edate, stime, etime, rest_fb, rcourse, rguest);

               guests5 += guests;         // add to total
            }

            //
            //  Process according to the 'per' value; member or member number
            //
            if (per.startsWith( "Membership" )) {

               if (!userg1.equals( "" )) {

                  // go count the number of guests for this member number
                  guests = checkMnums(con, userg1, slotParms, sdate, edate, stime, etime, rest_fb, rcourse, rguest);

                  guests1 += guests;         // add to total
               }
               if (!userg2.equals( "" )) {

                  // go count the number of guests for this member number
                  guests = checkMnums(con, userg2, slotParms, sdate, edate, stime, etime, rest_fb, rcourse, rguest);

                  guests2 += guests;         // add to total
               }
               if (!userg3.equals( "" )) {

                  // go count the number of guests for this member number
                  guests = checkMnums(con, userg3, slotParms, sdate, edate, stime, etime, rest_fb, rcourse, rguest);

                  guests3 += guests;         // add to total
               }
               if (!userg4.equals( "" )) {

                  // go count the number of guests for this member number
                  guests = checkMnums(con, userg4, slotParms, sdate, edate, stime, etime, rest_fb, rcourse, rguest);

                  guests4 += guests;         // add to total
               }
               if (!userg5.equals( "" )) {

                  // go count the number of guests for this member number
                  guests = checkMnums(con, userg5, slotParms, sdate, edate, stime, etime, rest_fb, rcourse, rguest);

                  guests5 += guests;         // add to total
               }
            }

            // if num of guests in quota count (guests_) > num allowed (grest_num) per member
            //
            //       to get here guests_ is = # of guests accumulated for member
            //       grest_num is 0 - 999 (restriction quota)
            //       per is 'Member' or 'Membership Number'
            //
            if (guests1 > slotParms.grest_num || guests2 > slotParms.grest_num || guests3 > slotParms.grest_num ||
                guests4 > slotParms.grest_num || guests5 > slotParms.grest_num) {

               error = true;                 // set error flag
               slotParms.grest_per = per;    // save this rest's per value

               if (guests1 > slotParms.grest_num) {

                  slotParms.player = slotParms.player1;   // save member name
               }
               if (guests2 > slotParms.grest_num) {

                  slotParms.player = slotParms.player2;   // save member name
               }
               if (guests3 > slotParms.grest_num) {

                  slotParms.player = slotParms.player3;   // save member name
               }
               if (guests4 > slotParms.grest_num) {

                  slotParms.player = slotParms.player4;   // save member name
               }
               if (guests5 > slotParms.grest_num) {

                  slotParms.player = slotParms.player5;   // save member name
               }
               break loop1;                  // done checking - exit while loop
            }

         }    // end of IF true
      }       // end of loop1 while loop

   } catch (Exception e) {

      throw new Exception("Error Checking Guest Rest - verifySlot: " + e.getMessage());

   } finally {

       try { rs.close(); }
       catch (Exception ignore) {}

       try { pstmt.close(); }
       catch (Exception ignore) {}

       try { rs2.close(); }
       catch (Exception ignore) {}

       try { pstmt2.close(); }
       catch (Exception ignore) {}

   }

   return(error);

 }


/**
 //************************************************************************
 //
 //  Check if any members are restricted from the event.
 //
 //    Called by:  Member_evntSignUp
 //                Proshop_evntSignUp
 //
 //************************************************************************
 **/

 public static String checkEventRests(String user1, String user2, String user3, String user4, String user5, int event_id, Connection con)
        throws Exception {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   int i = 0;
   int memLimit = Labels.MAX_MEMS;
   int mshipLimit = Labels.MAX_MSHIPS;

   String mship = "";
   String mtype = "";
   String restPlayer = "";

   String [] mshipA = new String [mshipLimit+1];        // Mship Types
   String [] mtypeA = new String [memLimit+1];          // Mem Types


   //if (!name.equals( "" ) && name != null) {        // make sure event name was passed

      try {

         //
         //  First, get the member types and mship types that are restricted for this event
         //
         pstmt = con.prepareStatement (
                 "SELECT * FROM events2b WHERE event_id = ? AND inactive = 0");

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1, event_id);
         rs = pstmt.executeQuery();      // execute the prepared pstmt1

         if (rs.next()) {

            for (i=1; i<memLimit+1; i++) {
               mtypeA[i] = rs.getString("mem" +i);
            }
            for (i=1; i<mshipLimit+1; i++) {
               mshipA[i] = rs.getString("mship" +i);
            }
         }
         pstmt.close();

         //
         //  Now check each user to see if they are restricted from the event
         //
         if (!user1.equals( "" ) && user1 != null) {

            pstmt = con.prepareStatement (
               "SELECT m_ship, m_type FROM member2b WHERE username = ?");

            pstmt.clearParameters();        // clear the parms
            pstmt.setString(1, user1);
            rs = pstmt.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               mship = rs.getString(1);
               mtype = rs.getString(2);
            }
            pstmt.close();

            loop1a:
            for (i=1; i<memLimit+1; i++) {

               if (mtype.equals( mtypeA[i] )) {

                  restPlayer = user1;                 // return player in error
                  break loop1a;
               }
            }

            if (restPlayer.equals( "" )) {       // if still ok

               loop1b:
               for (i=1; i<mshipLimit+1; i++) {

                  if (mship.equals( mshipA[i] )) {

                     restPlayer = user1;                 // return player in error
                     break loop1b;
                  }
               }
            }
         }

         if (!user2.equals( "" ) && user2 != null && restPlayer.equals( "" )) {

            pstmt = con.prepareStatement (
               "SELECT m_ship, m_type FROM member2b WHERE username = ?");

            pstmt.clearParameters();        // clear the parms
            pstmt.setString(1, user2);
            rs = pstmt.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               mship = rs.getString(1);
               mtype = rs.getString(2);
            }
            pstmt.close();

            loop2a:
            for (i=1; i<memLimit+1; i++) {

               if (mtype.equals( mtypeA[i] )) {

                  restPlayer = user2;                 // return player in error
                  break loop2a;
               }
            }

            if (restPlayer.equals( "" )) {       // if still ok

               loop2b:
               for (i=1; i<mshipLimit+1; i++) {

                  if (mship.equals( mshipA[i] )) {

                     restPlayer = user2;                 // return player in error
                     break loop2b;
                  }
               }
            }
         }

         if (!user3.equals( "" ) && user3 != null && restPlayer.equals( "" )) {

            pstmt = con.prepareStatement (
               "SELECT m_ship, m_type FROM member2b WHERE username = ?");

            pstmt.clearParameters();        // clear the parms
            pstmt.setString(1, user3);
            rs = pstmt.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               mship = rs.getString(1);
               mtype = rs.getString(2);
            }
            pstmt.close();

            loop3a:
            for (i=1; i<memLimit+1; i++) {

               if (mtype.equals( mtypeA[i] )) {

                  restPlayer = user3;                 // return player in error
                  break loop3a;
               }
            }

            if (restPlayer.equals( "" )) {       // if still ok

               loop3b:
               for (i=1; i<mshipLimit+1; i++) {

                  if (mship.equals( mshipA[i] )) {

                     restPlayer = user3;                 // return player in error
                     break loop3b;
                  }
               }
            }
         }

         if (!user4.equals( "" ) && user4 != null && restPlayer.equals( "" )) {

            pstmt = con.prepareStatement (
               "SELECT m_ship, m_type FROM member2b WHERE username = ?");

            pstmt.clearParameters();        // clear the parms
            pstmt.setString(1, user4);
            rs = pstmt.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               mship = rs.getString(1);
               mtype = rs.getString(2);
            }
            pstmt.close();

            loop4a:
            for (i=1; i<memLimit+1; i++) {

               if (mtype.equals( mtypeA[i] )) {

                  restPlayer = user4;                 // return player in error
                  break loop4a;
               }
            }

            if (restPlayer.equals( "" )) {       // if still ok

               loop4b:
               for (i=1; i<mshipLimit+1; i++) {

                  if (mship.equals( mshipA[i] )) {

                     restPlayer = user4;                 // return player in error
                     break loop4b;
                  }
               }
            }
         }

         if (!user5.equals( "" ) && user5 != null && restPlayer.equals( "" )) {

            pstmt = con.prepareStatement (
               "SELECT m_ship, m_type FROM member2b WHERE username = ?");

            pstmt.clearParameters();        // clear the parms
            pstmt.setString(1, user5);
            rs = pstmt.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               mship = rs.getString(1);
               mtype = rs.getString(2);
            }
            pstmt.close();

            loop5a:
            for (i=1; i<memLimit+1; i++) {

               if (mtype.equals( mtypeA[i] )) {

                  restPlayer = user5;                 // return player in error
                  break loop5a;
               }
            }

            if (restPlayer.equals( "" )) {       // if still ok

               loop5b:
               for (i=1; i<mshipLimit+1; i++) {

                  if (mship.equals( mshipA[i] )) {

                     restPlayer = user5;                 // return player in error
                     break loop5b;
                  }
               }
            }
         }

      } catch (Exception e) {

         throw new Exception("Error Checking Member Restrictions for Event ID: " + event_id + " - verifySlot: " + e.getMessage());

      } finally {

          try { rs.close(); }
          catch (SQLException ignored) {}

          try { pstmt.close(); }
          catch (SQLException ignored) {}

      }
   //}

   return(restPlayer);
 }


/**
 //************************************************************************
 //
 //  Find any other members with the same Member Number and count their guests.
 //
 //    Called by:  checkGuestQuota above
 //
 //************************************************************************
 **/

 private static int checkMnums(Connection con, String user, parmSlot slotParms, long sdate, long edate, int stime, int etime,
                               String rfb, String rcourse, ArrayList<String> rguest)
                           throws Exception {

   ResultSet rs = null;
   PreparedStatement pstmt = null;

   int guests = 0;

   String mNum = "";
   String tuser = "";


   //****************************************************************************
   //  per = Membership Number  -  check all members with the same Member Number
   //****************************************************************************

   try {

      //  get this user's mNum
      pstmt = con.prepareStatement (
         "SELECT memNum FROM member2b WHERE username = ?");

      pstmt.clearParameters();
      pstmt.setString(1, user);
      rs = pstmt.executeQuery();

      if (rs.next()) {

         mNum = rs.getString(1);            // get this user's member number
      }

      if (!mNum.equals( "" )) {             // if there is one specified

         //  get all users with matching mNum and put in userm array
         pstmt = con.prepareStatement (
            "SELECT username FROM member2b WHERE memNum = ?");

         pstmt.clearParameters();
         pstmt.setString(1, mNum);
         rs = pstmt.executeQuery();

         while ( rs.next() ) {

            tuser = rs.getString(1);        // get the username

            if (!tuser.equals( "" ) && !tuser.equalsIgnoreCase( user )) {   // if exists and not this user

               // go count the number of guests for this member
               guests += countGuests(con, tuser, slotParms, sdate, edate, stime, etime, rfb, rcourse, rguest);

            }

         } // end while loop

      }

   } catch (Exception e) {

      throw new Exception("Error Checking Mnums for Guest Rest - verifySlot: " + e.getMessage());

   } finally {

      try { rs.close(); }
      catch (Exception ignore) {}

      try { pstmt.close(); }
      catch (Exception ignore) {}
   }

   return(guests);
 }


/**
 //************************************************************************
 //
 //  Count the number of guests that a member has scheduled in the specified time.
 //
 //    Called by:  checkGuestQuota and checkMnums above
 //
 //    Check teecurr and tee past for all specified guest types that are
 //    associated with this member or member number.
 //
 //************************************************************************
 **/

 private static int countGuests(Connection con, String user, parmSlot slotParms, long sdate, long edate, int stime, int etime,
                               String rfb, String rcourse, ArrayList<String> rguest)
                           throws Exception {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   int guests = 0;
   int time = 0;
   int fb = 0;
   int rest_fb = 0;
   int i = 0;

   long date = 0;

   //String sfb = "";
   String course = "";
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
   //String errorMsg = "";

   //  convert restriction's f/b
   if (rfb.equalsIgnoreCase( "Front" )) {
      rest_fb = 0;
   } else {
      rest_fb = 1;
   }

   if ( slotParms.activity_id == 0 ) {

       //
       //  Count all guests with matching guest types that are associated with this member (teecurr)
       //
       try {

          pstmt = con.prepareStatement (
             "SELECT date, time, courseName, player1, player2, player3, player4, fb, player5, " +
             "userg1, userg2, userg3, userg4, userg5 " +
             "FROM teecurr2 " +
             "WHERE date <= ? AND date >= ? AND time <= ? AND time >= ? AND " +
             "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?)");

          pstmt.clearParameters();        // clear the parms and check player 1
          pstmt.setLong(1, edate);
          pstmt.setLong(2, sdate);
          pstmt.setInt(3, etime);
          pstmt.setInt(4, stime);
          pstmt.setString(5, user);
          pstmt.setString(6, user);
          pstmt.setString(7, user);
          pstmt.setString(8, user);
          pstmt.setString(9, user);
          rs = pstmt.executeQuery();      // execute the prepared stmt

          while ( rs.next() ) {

             date = rs.getLong("date");
             time = rs.getInt("time");
             course = rs.getString("courseName");
             player1 = rs.getString("player1");
             player2 = rs.getString("player2");
             player3 = rs.getString("player3");
             player4 = rs.getString("player4");
             fb = rs.getInt("fb");
             player5 = rs.getString("player5");
             userg1 = rs.getString("userg1");
             userg2 = rs.getString("userg2");
             userg3 = rs.getString("userg3");
             userg4 = rs.getString("userg4");
             userg5 = rs.getString("userg5");

             //
             //  matching tee time found in teecurr - check if course, date and time match
             //  Make sure this is not this tee time before changes,
             //
             if ((date != slotParms.date) || (time != slotParms.time) || (!course.equals( slotParms.course ))) {

                //
                //  Check if course and f/b match that specified in restriction
                //
                if ((rcourse.equals( "-ALL-" )) || (rcourse.equals( course ))) {

                   if ((rfb.equals( "Both" )) || (rest_fb == fb )) {  // if f/b matches

                      // check if any players from the tee time are a restricted guest
                      if (user.equalsIgnoreCase( userg1 )) {
                         i = 0;
                         loop1:
                         while (i < rguest.size()) {
                            if (player1.startsWith( rguest.get(i) )) {    // if matching guest type & associated with this user
                               guests++;             // bump count of guests for quota check
                               break loop1;          // exit loop
                            }
                            i++;
                         }
                      }
                      if (user.equalsIgnoreCase( userg2 )) {
                         i = 0;
                         loop2:
                         while (i < rguest.size()) {
                            if (player2.startsWith( rguest.get(i) )) {    // if matching guest type & associated with this user
                               guests++;             // bump count of guests for quota check
                               break loop2;          // exit loop
                            }
                            i++;
                         }
                      }
                      if (user.equalsIgnoreCase( userg3 )) {
                         i = 0;
                         loop3:
                         while (i < rguest.size()) {
                            if (player3.startsWith( rguest.get(i) )) {    // if matching guest type & associated with this user
                               guests++;             // bump count of guests for quota check
                               break loop3;          // exit loop
                            }
                            i++;
                         }
                      }
                      if (user.equalsIgnoreCase( userg4 )) {
                         i = 0;
                         loop4:
                         while (i < rguest.size()) {
                            if (player4.startsWith( rguest.get(i) )) {    // if matching guest type & associated with this user
                               guests++;             // bump count of guests for quota check
                               break loop4;          // exit loop
                            }
                            i++;
                         }
                      }
                      if (user.equalsIgnoreCase( userg5 )) {
                         i = 0;
                         loop5:
                         while (i < rguest.size()) {
                            if (player5.startsWith( rguest.get(i) )) {    // if matching guest type & associated with this user
                               guests++;             // bump count of guests for quota check
                               break loop5;          // exit loop
                            }
                            i++;
                         }
                      }
                   }
                }
             }   // end of IF tee time not 'this tee time'
          }   // end of WHILE


          //
          //  Count all guests with matching guest types that are associated with this member (teepast)
          //
          pstmt = con.prepareStatement (
             "SELECT courseName, player1, player2, player3, player4, fb, player5, " +
             "userg1, userg2, userg3, userg4, userg5 " +
             "FROM teepast2 " +
             "WHERE date <= ? AND date >= ? AND time <= ? AND time >= ? AND " +
             "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?)");

          pstmt.clearParameters();        // clear the parms and check player 1
          pstmt.setLong(1, edate);
          pstmt.setLong(2, sdate);
          pstmt.setInt(3, etime);
          pstmt.setInt(4, stime);
          pstmt.setString(5, user);
          pstmt.setString(6, user);
          pstmt.setString(7, user);
          pstmt.setString(8, user);
          pstmt.setString(9, user);
          rs = pstmt.executeQuery();      // execute the prepared stmt

          while ( rs.next() ) {

             course = rs.getString("courseName");
             player1 = rs.getString("player1");
             player2 = rs.getString("player2");
             player3 = rs.getString("player3");
             player4 = rs.getString("player4");
             fb = rs.getInt("fb");
             player5 = rs.getString("player5");
             userg1 = rs.getString("userg1");
             userg2 = rs.getString("userg2");
             userg3 = rs.getString("userg3");
             userg4 = rs.getString("userg4");
             userg5 = rs.getString("userg5");

             //
             //  Check if course and f/b match that specified in restriction
             //
             if ((rcourse.equals( "-ALL-" )) || (rcourse.equals( course ))) {

                if ((rfb.equals( "Both" )) || (rest_fb == fb )) {  // if f/b matches

                   //
                   //  matching tee time found in teecurr
                   //  check if any players from the tee time are a restricted guest
                   //
                   if (user.equalsIgnoreCase( userg1 )) {
                      i = 0;
                      loop11:
                      while (i < rguest.size()) {
                         if (player1.startsWith( rguest.get(i) )) {    // if matching guest type & associated with this user
                            guests++;             // bump count of guests for quota check
                            break loop11;          // exit loop
                         }
                         i++;
                      }
                   }
                   if (user.equalsIgnoreCase( userg2 )) {
                      i = 0;
                      loop12:
                      while (i < rguest.size()) {
                         if (player2.startsWith( rguest.get(i) )) {    // if matching guest type & associated with this user
                            guests++;             // bump count of guests for quota check
                            break loop12;          // exit loop
                         }
                         i++;
                      }
                   }
                   if (user.equalsIgnoreCase( userg3 )) {
                      i = 0;
                      loop13:
                      while (i < rguest.size()) {
                         if (player3.startsWith( rguest.get(i) )) {    // if matching guest type & associated with this user
                            guests++;             // bump count of guests for quota check
                            break loop13;          // exit loop
                         }
                         i++;
                      }
                   }
                   if (user.equalsIgnoreCase( userg4 )) {
                      i = 0;
                      loop14:
                      while (i < rguest.size()) {
                         if (player4.startsWith( rguest.get(i) )) {    // if matching guest type & associated with this user
                            guests++;             // bump count of guests for quota check
                            break loop14;          // exit loop
                         }
                         i++;
                      }
                   }
                   if (user.equalsIgnoreCase( userg5 )) {
                      i = 0;
                      loop15:
                      while (i < rguest.size()) {
                         if (player5.startsWith( rguest.get(i) )) {    // if matching guest type & associated with this user
                            guests++;             // bump count of guests for quota check
                            break loop15;          // exit loop
                         }
                         i++;
                      }
                   }
                }
             }
          }   // end of WHILE

       }
       catch (Exception e) {

          throw new Exception("Error Counting Guests for Guest Rest - verifySlot: " + e.getMessage());

       } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

       }

   } else {

       Utilities.logError("counting guests for " + user + ", sdate=" + sdate + ", edate=" + edate + ", stime=" + stime + ", etime=" + etime);

       try {

           pstmt = con.prepareStatement ("" +
                   "SELECT COUNT(*) " +
                   "FROM activity_sheets s " +
                   "LEFT OUTER JOIN activity_sheets_players p ON s.sheet_id = p.activity_sheet_id " +
                   "WHERE p.userg = ? AND " +
                       "DATE_FORMAT(date_time, '%Y%m%d') >= ? AND " +
                       "DATE_FORMAT(date_time, '%Y%m%d') <= ? AND " +
                       "DATE_FORMAT(date_time, '%k%i') >= ? AND " +
                       "DATE_FORMAT(date_time, '%k%i') <= ?");

           pstmt.clearParameters();
           pstmt.setString(1, user);
           pstmt.setLong(2, sdate);
           pstmt.setLong(3, edate);
           pstmt.setInt(4, stime);
           pstmt.setInt(5, etime);

           rs = pstmt.executeQuery();

           if ( rs.next() ) guests = rs.getInt(1);

       } catch (Exception e) {

          throw new Exception("Error Counting Guests for activity - verifySlot: " + e.getMessage());

       } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

       }

   }

   return(guests);

 }

/**
 //************************************************************************
 //
 //  Check restriction suspensions
 //
 //     Check to see if the found restriction is suspended for this date/day/time.
 //     -If suspension applies, return true;
 //     -If no suspension, return false;
 //
 //************************************************************************
 **/
 public static boolean checkRestSuspend(int mrest_id, int grest_id, int rest_date, int rest_time, String rest_day, String rest_course, Connection con) {

     boolean result = false;

     PreparedStatement pstmt = null;
     ResultSet rs = null;

     int rest_id = 0;

     String rest_type = "";

     if (grest_id == -99) {
         rest_type = "mrest";
         rest_id = mrest_id;
     } else {
         rest_type = "grest";
         rest_id = grest_id;
     }

     try {
         pstmt = con.prepareStatement("SELECT * FROM rest_suspend WHERE " +
                 rest_type + "_id = ? AND " + rest_day.toLowerCase() + " = 1 AND sdate <= ? AND edate >= ? AND stime <= ? AND etime >= ? " +
                 "AND (courseName = '-ALL-' OR courseName = ?) AND (eo_week = 0 OR (MOD(DATE_FORMAT(sdate, '%U'), 2) = MOD(DATE_FORMAT(?, '%U'), 2)))");

         pstmt.clearParameters();
         pstmt.setInt(1, rest_id);
         pstmt.setInt(2, rest_date);
         pstmt.setInt(3, rest_date);
         pstmt.setInt(4, rest_time);
         pstmt.setInt(5, rest_time);
         pstmt.setString(6, rest_course);
         pstmt.setInt(7, rest_date);

         rs = pstmt.executeQuery();

         if (rs.next()) {       // suspension was found
             result = true;
         }

         pstmt.close();

     } catch (Exception exc) { }

     return result;
 }

/**
 //************************************************************************
 //
 //  Check member restrictions
 //
 //     First, find all restrictions within date & time constraints on this course.
 //     Then, find the ones for this day.
 //     Then, find any for this member type or membership type (all 5 players).
 //
 //
 //    **** Hazeltine Custom Processing *********
 //    Hazeltine will setup member restrictions for junior members.  However,
 //    if the junior(s) are playing with one or more parent, its ok.
 //
 //************************************************************************
 **/

 public static boolean checkMemRests(parmSlot slotParms, Connection con)
         throws Exception {


   //Statement stmt = null;
   ResultSet rs = null;

   boolean error = false;
   //boolean ok = false;
   boolean jrOK = false;
   boolean isProshop = ProcessConstants.isProshopUser(slotParms.user);

   //int ind = 0;
   int i = 0;
   int mrest_id = 0;
   int memLimit = Labels.MAX_MEMS;
   int mshipLimit = Labels.MAX_MSHIPS;

   String rest_fb = "";
   String rest_recurr = "";

   String [] mtypeA = new String [Labels.MAX_MEMS];     // array to hold the member type names

   String [] mshipA = new String [Labels.MAX_MSHIPS];   // array to hold the membership names
/*
   String in = "0";

   try { in = getActivity.buildInString(slotParms.activity_id, 0, con); }
   catch (Exception exc) { Utilities.logError("Error in verifySlot.checkMemRests getting parent activities. ERR=" + exc.toString()); }
*/
   try {

      PreparedStatement pstmt7 = con.prepareStatement (
         "SELECT * FROM restriction2 WHERE sdate <= ? AND edate >= ? AND " +
         "stime <= ? AND etime >= ? AND " +
         ((slotParms.activity_id == 0) ? "(courseName = ? OR courseName = '-ALL-') AND activity_id = 0" : "? IN (locations)"));


      pstmt7.clearParameters();          // clear the parms
      pstmt7.setLong(1, slotParms.date);
      pstmt7.setLong(2, slotParms.date);
      pstmt7.setInt(3, slotParms.time);
      pstmt7.setInt(4, slotParms.time);
      if (slotParms.activity_id == 0) {
          pstmt7.setString(5, slotParms.course);
      } else {
          pstmt7.setInt(5, slotParms.activity_id);
      }

      rs = pstmt7.executeQuery();      // find all matching restrictions, if any

      error = false;                     // init 'hit' flag

      if (slotParms.fb == 0) {                   // is Tee time for Front 9?

         slotParms.sfb = "Front";

      } else if (slotParms.fb == 1) {                   // is it Back 9?

         slotParms.sfb = "Back";
      }

      loop2:
      while (rs.next()) {              // check all matching restrictions for this day, mship, mtype & F/B

         slotParms.rest_name = rs.getString("name");
         mrest_id = rs.getInt("id");
         rest_recurr = rs.getString("recurr");
         for (i=0; i<memLimit; i++) {
            mtypeA[i] = rs.getString("mem" +(i+1));
         }
         for (i=0; i<mshipLimit; i++) {
            mshipA[i] = rs.getString("mship" +(i+1));
         }
         rest_fb = rs.getString("fb");

         jrOK = false;

         if (!slotParms.club.equals("meadowclub") || !isProshop || !slotParms.rest_name.equals("Member Tee Time Restriction")) {
             //
             //  We must check the recurrence for this day (Monday, etc.)
             //
             if ((rest_recurr.equals( "Every " + slotParms.day )) ||               // if this day
                 (rest_recurr.equalsIgnoreCase( "every day" )) ||        // or everyday
                 ((rest_recurr.equalsIgnoreCase( "all weekdays" )) &&    // or all weekdays (and this is one)
                   (!slotParms.day.equalsIgnoreCase( "saturday" )) &&
                   (!slotParms.day.equalsIgnoreCase( "sunday" ))) ||
                 ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&    // or all weekends (and this is one)
                  (slotParms.day.equalsIgnoreCase( "saturday" ))) ||
                 ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&
                  (slotParms.day.equalsIgnoreCase( "sunday" )))) {

                //
                //  Now check if activity or if golf that the F/B matches
                //
                if (slotParms.activity_id != 0 || (rest_fb.equals( "Both" ) || rest_fb.equals( slotParms.sfb ))) {

                   error = false;

                   if (!checkRestSuspend(mrest_id, -99, (int)slotParms.date, slotParms.time, slotParms.day, slotParms.course, con)) {

                       //
                       //  Found a restriction that matches date, time, day & F/B - check mtype & mship of each member player
                       //
                       if (!slotParms.mship1.equals( "" )) {           // if this player is a member

                          slotParms.player = slotParms.player1;                  // save current player name

                          error = checkPlayerRest(slotParms.mship1, slotParms.mtype1, slotParms.user1, slotParms.player1, slotParms, mshipA, mtypeA, con); // check if player is restricted

                          if (error == true) break loop2;         // stop checking if member restricted

                       }  // end of member 1 restrictions if

                       if (!slotParms.mship2.equals( "" )) {           // if this player is a member

                          slotParms.player = slotParms.player2;                  // save current player name

                          error = checkPlayerRest(slotParms.mship2, slotParms.mtype2, slotParms.user2, slotParms.player2, slotParms, mshipA, mtypeA, con); // check if player is restricted

                          if (error == true) break loop2;         // stop checking if member restricted

                       }  // end of member 2 restrictions if

                       if (!slotParms.mship3.equals( "" )) {           // if this player is a member

                          slotParms.player = slotParms.player3;                  // save current player name

                          error = checkPlayerRest(slotParms.mship3, slotParms.mtype3, slotParms.user3, slotParms.player3, slotParms, mshipA, mtypeA, con); // check if player is restricted

                          if (error == true) break loop2;         // stop checking if member restricted

                       }  // end of member 3 restrictions if

                       if (!slotParms.mship4.equals( "" )) {           // if this player is a member

                          slotParms.player = slotParms.player4;                  // save current player name

                          error = checkPlayerRest(slotParms.mship4, slotParms.mtype4, slotParms.user4, slotParms.player4, slotParms, mshipA, mtypeA, con); // check if player is restricted

                          if (error == true) break loop2;         // stop checking if member restricted

                       }  // end of member 4 restrictions if

                       if (!slotParms.mship5.equals( "" )) {           // if this player is a member

                          slotParms.player = slotParms.player5;                  // save current player name

                          error = checkPlayerRest(slotParms.mship5, slotParms.mtype5, slotParms.user5, slotParms.player5, slotParms, mshipA, mtypeA, con); // check if player is restricted

                          if (error == true) break loop2;         // stop checking if member restricted


                          /*         the following code was moved to checkPlayerRest - before that it was here under each player
                           *
                          ind = 0;                           // init fields
                          slotParms.player = slotParms.player5;                  // save current player name

                          while (ind < memLimit) {

                             if ((slotParms.mship5.equalsIgnoreCase( mshipA[ind] )) || (slotParms.mtype5.equalsIgnoreCase( mtypeA[ind] ))) {

                                if (!slotParms.player5.equals( slotParms.oldPlayer1 ) &&
                                    !slotParms.player5.equals( slotParms.oldPlayer2 ) &&
                                    !slotParms.player5.equals( slotParms.oldPlayer3 ) &&
                                    !slotParms.player5.equals( slotParms.oldPlayer4 ) &&
                                    !slotParms.player5.equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

                                   //  Custom processing for Hazeltine National
                                   if (slotParms.club.equals( "hazeltine" ) && slotParms.activity_id == 0) {    // if Hazeltine Natl. - check for juniors

                                      ok = false;         // default to error

                                      if ((slotParms.mtype5.equals("Juniors Golfer") || slotParms.mtype5.equals("Certified Junior")) &&
                                          (mtypeA[ind].equals("Juniors Golfer") || mtypeA[ind].equals("Certified Junior"))) {

                                         //
                                         //  Junior golfer and Junior restriction - ok if with parent
                                         //
                                         if (jrOK == false) {      // if not already ok'ed

                                            ok = true;

                                         } else {

                                            if (slotParms.mNum5.equals(slotParms.mNum1) && slotParms.mtype1.startsWith("Adult")) {
                                               ok = true;      // ok if parent
                                               jrOK = true;    // all juniors ok
                                            } else {
                                               if (slotParms.mNum5.equals(slotParms.mNum2) && slotParms.mtype2.startsWith("Adult")) {
                                                  ok = true;      // ok if parent
                                                  jrOK = true;    // all juniors ok
                                               } else {
                                                  if (slotParms.mNum5.equals(slotParms.mNum3) && slotParms.mtype3.startsWith("Adult")) {
                                                     ok = true;      // ok if parent
                                                     jrOK = true;    // all juniors ok
                                                  } else {
                                                     if (slotParms.mNum5.equals(slotParms.mNum4) && slotParms.mtype4.startsWith("Adult")) {
                                                        ok = true;      // ok if parent
                                                        jrOK = true;    // all juniors ok
                                                     }
                                                  }
                                               }
                                            }
                                         }
                                      }
                                      if (ok == false) {
                                         error = true;        // member is restricted
                                         break loop2;
                                      }

                                   } else {

                                      //  Custom processing for Colorado Golf Club
                                      if (slotParms.club.equals( "coloradogc" ) && slotParms.activity_id == 0) {    // if Colorado GC. - check for juniors

                                         ok = false;         // default to error

                                         if (slotParms.mtype5.startsWith( "Junior" ) && slotParms.rest_name.startsWith( "Junior Restriction" )) {

                                            if (jrOK == true) {      // if already ok'ed

                                               ok = true;

                                            } else {

                                               //
                                               //  Junior golfer and Junior restriction - ok if with an adult
                                               //
                                               if (slotParms.mtype1.startsWith("Primary") || slotParms.mtype1.startsWith("Spouse") ||
                                                   slotParms.mtype2.startsWith("Primary") || slotParms.mtype2.startsWith("Spouse") ||
                                                   slotParms.mtype3.startsWith("Primary") || slotParms.mtype3.startsWith("Spouse") ||
                                                   slotParms.mtype4.startsWith("Primary") || slotParms.mtype4.startsWith("Spouse")) {

                                                  ok = true;      // ok if parent
                                               }
                                            }
                                         }
                                         if (ok == false) {
                                            error = true;        // member is restricted
                                            break loop2;
                                         }

                                      } else {                  // all other clubs
                                         error = true;          // match found - member is restricted
                                         break loop2;
                                      }
                                   }
                                }
                             }
                             ind++;
                          }
                           */

                       }  // end of member 5 restrictions if

                   }   // end of restriction suspension check
                }     // end of IF F/B matches
             }     // end of 'day' if
         }
      }       // end of while (no more restrictions)

      pstmt7.close();
   }
   catch (Exception e) {
      throw new Exception("Error Checking Member Restrictions - verifySlot.checkMemRests " + e.getMessage());
   }

   return(error);
 }


/**
 //************************************************************************
 //
 //  Check player restrictions
 //
 //       Check each player for restrictions (from directly above)
 //
 //************************************************************************
 **/

 public static boolean checkPlayerRest(String mship, String mtype, String user, String player, parmSlot slotParms,
                                       String [] mshipA, String [] mtypeA, Connection con) {


   PreparedStatement pstmt1 = null;
   ResultSet rs = null;

   boolean error = false;
   boolean ok = false;

   int ind = 0;
   int memLimit = Labels.MAX_MEMS;
   //int mshipLimit = Labels.MAX_MSHIPS;

   String msubtype = "";


    loop1:
    while (ind < memLimit) {          // check all mtype or mship types specified in the restriction

       if ((mship.equalsIgnoreCase( mshipA[ind] )) || (mtype.equalsIgnoreCase( mtypeA[ind] ))) {     // if this player hits on the restriction

          if (!player.equals( slotParms.oldPlayer1 ) &&
              !player.equals( slotParms.oldPlayer2 ) &&
              !player.equals( slotParms.oldPlayer3 ) &&
              !player.equals( slotParms.oldPlayer4 ) &&
              !player.equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

             //  Custom processing for Hazeltine National
             if (slotParms.club.equals( "hazeltine" ) && slotParms.activity_id == 0) {    // if Hazeltine Natl. - check for juniors

                ok = false;         // default to error

                if ((mtype.equals("Juniors Golfer") || mtype.equals("Certified Junior")) &&
                    (mtypeA[ind].equals("Juniors Golfer") || mtypeA[ind].equals("Certified Junior"))) {    // if player is a junior and this is a junior restriction

                   //
                   //  Junior golfer and Junior restriction - ok if with parent (adult)
                   //
                   if (slotParms.mtype1.startsWith("Adult") || slotParms.mtype2.startsWith("Adult") || slotParms.mtype3.startsWith("Adult") ||
                       slotParms.mtype4.startsWith("Adult") || slotParms.mtype5.startsWith("Adult")) {

                      ok = true;      // ok if with an adult
                   }

                }
                if (ok == false) {
                   error = true;        // member is restricted
                   break loop1;
                }

             } else if (slotParms.club.equals( "coloradogc" ) && slotParms.activity_id == 0) {    // if Colorado GC. - check for juniors

                //  Custom processing for Colorado Golf Club

                ok = false;         // default to error

                if (mtype.startsWith( "Junior" ) && slotParms.rest_name.startsWith( "Junior Restriction" )) {

                   //
                   //  Junior golfer and Junior restriction - ok if with an adult
                   //
                   if (slotParms.mtype1.startsWith("Primary") || slotParms.mtype1.startsWith("Spouse") ||
                       slotParms.mtype2.startsWith("Primary") || slotParms.mtype2.startsWith("Spouse") ||
                       slotParms.mtype3.startsWith("Primary") || slotParms.mtype3.startsWith("Spouse") ||
                       slotParms.mtype4.startsWith("Primary") || slotParms.mtype4.startsWith("Spouse") ||
                       slotParms.mtype5.startsWith("Primary") || slotParms.mtype5.startsWith("Spouse")) {

                      ok = true;      // ok if with an adult
                   }
                }
                if (ok == false) {
                   error = true;        // member is restricted
                   break loop1;
                }

             } else if (slotParms.club.equals( "paloaltohills" ) && slotParms.activity_id == 0) {    // if Palo Alto Hills - check for Lady 18 Holers

                //  Custom processing for Palo Alto Hills

                ok = false;         // default to error

                if (slotParms.rest_name.equals( "18 Hole Ladies" )) {

                   if (mtype.endsWith("Female")) {          // 18 Hole Ladies Restriction - check if adult female

                      //
                      //    18 Hole Ladies rest and player is a female member - if subtype is 18 Holer, then ok
                      //
                      try {

                        pstmt1 = con.prepareStatement (
                           "SELECT msub_type FROM member2b WHERE username = ?");

                           pstmt1.clearParameters();        // clear the parms
                           pstmt1.setString(1, user);
                           rs = pstmt1.executeQuery();      // execute the prepared stmt

                           if (rs.next()) {

                              msubtype = rs.getString(1);
                           }

                        pstmt1.close();

                     } catch (Exception e) {

                     } finally {

                          try { rs.close(); }
                          catch (Exception ignore) {}

                          try { pstmt1.close(); }
                          catch (Exception ignore) {}
                      }


                      if (msubtype.equals("18 Holer")) {

                         ok = true;      // ok if 18 Holer
                      }
                   }
                }
                if (ok == false) {
                   error = true;        // member is restricted
                   break loop1;
                }

             } else {                  // all other clubs
                error = true;          // match found - member is restricted
                break loop1;
             }
          }
       }
       ind++;
    }

   return(error);
 }




/**
 //************************************************************************
 //
 //  Check Member Number restrictions
 //
 //     First, find all restrictions within date & time constraints
 //     Then, find the ones for this day
 //     Then, check all players' member numbers against all others in the time period
 //
 //************************************************************************
 **/

 public static boolean checkMemNum(parmSlot slotParms, Connection con)
         throws Exception {


   PreparedStatement pstmt1 = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs1 = null;
   ResultSet rs2 = null;

   boolean error = false;

   int ind = 0;
   int rest_stime = 0;
   int rest_etime = 0;
   int mems = 0;
   int time2 = 0;
   int t_fb = 0;

   String course2 = "";
   String sfb2 = "";
   String rest_fb = "";
   String rest_course = "";
   String rest_recurr = "";
   String rmNum1 = "";
   String rmNum2 = "";
   String rmNum3 = "";
   String rmNum4 = "";
   String rmNum5 = "";
/*
   String in = "";

   try { in = getActivity.buildInString(slotParms.root_activity_id, 1, con); }
   catch (Exception exc) { verifySlot.logError("Error in verifySlot.checkMemNum getting root activities. ERR=" + exc.toString()); }
*/
   try {

      pstmt1 = con.prepareStatement (
         "SELECT name, stime, etime, recurr, courseName, fb, num_mems " +
         "FROM mnumres2 WHERE sdate <= ? AND edate >= ? AND " +
         "stime <= ? AND etime >= ? AND (courseName = ? OR courseName = '-ALL-')");
/*
         "stime <= ? AND etime >= ? AND " +
         ((slotParms.activity_id == 0) ? "(courseName = ? OR courseName = '-ALL-') AND activity_id = 0" : "activity_id IN (" + in + ")"));
*/

      pstmt1.clearParameters();          // clear the parms
      pstmt1.setLong(1, slotParms.date);
      pstmt1.setLong(2, slotParms.date);
      pstmt1.setInt(3, slotParms.time);
      pstmt1.setInt(4, slotParms.time);
      if (slotParms.activity_id == 0) pstmt1.setString(5, slotParms.course);

      rs1 = pstmt1.executeQuery();      // find all matching restrictions, if any

      error = false;                    // init 'hit' flag
      ind = 0;                          // init matching member count

      if (slotParms.fb == 0) {                    // is Tee time for Front 9?

         slotParms.sfb = "Front";
      }

      if (slotParms.fb == 1) {                    // is it Back 9?

         slotParms.sfb = "Back";
      }

      loop3:
      while (rs1.next()) {              // check all matching restrictions for this day & F/B

         slotParms.rest_name = rs1.getString("name");    // get name for error message
         rest_stime = rs1.getInt("stime");
         rest_etime = rs1.getInt("etime");
         rest_recurr = rs1.getString("recurr");
         rest_course = rs1.getString("courseName");
         rest_fb = rs1.getString("fb");
         mems = rs1.getInt("num_mems");

         //
         //  We must check the recurrence for this day (Monday, etc.)
         //
         if ((rest_recurr.equals( "Every " + slotParms.day )) ||               // if this day
             (rest_recurr.equalsIgnoreCase( "every day" )) ||        // or everyday
             ((rest_recurr.equalsIgnoreCase( "all weekdays" )) &&    // or all weekdays (and this is one)
               (!slotParms.day.equalsIgnoreCase( "saturday" )) &&
               (!slotParms.day.equalsIgnoreCase( "sunday" ))) ||
             ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&    // or all weekends (and this is one)
              (slotParms.day.equalsIgnoreCase( "saturday" ))) ||
             ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&
              (slotParms.day.equalsIgnoreCase( "sunday" )))) {

            //
            //  Now check if F/B matches this tee time
            //
            if (rest_fb.equals( "Both" ) || rest_fb.equals( slotParms.sfb )) {

               //
               //  Found a restriction that matches date, time, day, course & F/B - check each member player
               //
               //   Check Player 1
               //
               if (!slotParms.mNum1.equals( "" )) {           // if this player is a member and member number exists

                  pstmt2 = con.prepareStatement (
                     "SELECT time, fb, courseName, mNum1, mNum2, mNum3, mNum4, mNum5 FROM teecurr2 " +
                     "WHERE (mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?) AND date = ? " +
                     "AND time <= ? AND time >= ?");

                  ind = 0;                           // init fields

                  pstmt2.clearParameters();        // clear the parms and check player 1
                  pstmt2.setString(1, slotParms.mNum1);
                  pstmt2.setString(2, slotParms.mNum1);
                  pstmt2.setString(3, slotParms.mNum1);
                  pstmt2.setString(4, slotParms.mNum1);
                  pstmt2.setString(5, slotParms.mNum1);
                  pstmt2.setLong(6, slotParms.date);
                  pstmt2.setInt(7, rest_etime);
                  pstmt2.setInt(8, rest_stime);
                  rs2 = pstmt2.executeQuery();      // execute the prepared stmt

                  while (rs2.next()) {

                     time2 = rs2.getInt("time");
                     t_fb = rs2.getInt("fb");
                     course2 = rs2.getString("courseName");
                     rmNum1 = rs2.getString("mNum1");
                     rmNum2 = rs2.getString("mNum2");
                     rmNum3 = rs2.getString("mNum3");
                     rmNum4 = rs2.getString("mNum4");
                     rmNum5 = rs2.getString("mNum5");

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
                     if ((time2 != slotParms.time) || (!course2.equals( slotParms.course ))) {  // either time or course is diff

                        if ((rest_fb.equals( "Both" ) || rest_fb.equals( sfb2 )) &&
                            (rest_course.equals( "-ALL-" ) || rest_course.equals( course2 ))) {

                           if (slotParms.mNum1.equals( rmNum1 )) {  // count number of players with this member number already scheduled
                              ind++;
                           }
                           if (slotParms.mNum1.equals( rmNum2 )) {
                              ind++;
                           }
                           if (slotParms.mNum1.equals( rmNum3 )) {
                              ind++;
                           }
                           if (slotParms.mNum1.equals( rmNum4 )) {
                              ind++;
                           }
                           if (slotParms.mNum1.equals( rmNum5 )) {
                              ind++;
                           }
                        }
                     }

                  } // end of while members

                  slotParms.pnum1 = "";
                  slotParms.pnum2 = "";
                  slotParms.pnum3 = "";
                  slotParms.pnum4 = "";
                  slotParms.pnum5 = "";

                  //
                  //  Now check if any other members in this tee time match
                  //
                  slotParms.pnum1 = slotParms.player1;  // save this player name for error msg

                  if (slotParms.mNum1.equals( slotParms.mNum2 )) {

                     ind++;
                     slotParms.pnum2 = slotParms.player2;  // match found for player - save for error msg
                  }
                  if (slotParms.mNum1.equals( slotParms.mNum3 )) {

                     ind++;
                     slotParms.pnum3 = slotParms.player3;  // match found for player - save for error msg
                  }
                  if (slotParms.mNum1.equals( slotParms.mNum4 )) {

                     ind++;
                     slotParms.pnum4 = slotParms.player4;  // match found for player - save for error msg
                  }
                  if (slotParms.mNum1.equals( slotParms.mNum5 )) {

                     ind++;
                     slotParms.pnum5 = slotParms.player5;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     if (!slotParms.player1.equals( slotParms.oldPlayer1 ) &&
                         !slotParms.player1.equals( slotParms.oldPlayer2 ) &&
                         !slotParms.player1.equals( slotParms.oldPlayer3 ) &&
                         !slotParms.player1.equals( slotParms.oldPlayer4 ) &&
                         !slotParms.player1.equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

                        error = true;      // max # exceeded - reject
                     }
                  }

                  pstmt2.close();

               }  // end of member 1 restrictions if

               //
               //   Check Player 2
               //
               if ((error == false) && (!slotParms.mNum2.equals( "" ))) {   // if this player is a member

                  pstmt2 = con.prepareStatement (
                     "SELECT time, fb, courseName, mNum1, mNum2, mNum3, mNum4, mNum5 FROM teecurr2 " +
                     "WHERE (mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?) AND date = ? " +
                     "AND time <= ? AND time >= ?");

                  ind = 0;                           // init fields
                  slotParms.pnum1 = "";
                  slotParms.pnum2 = "";
                  slotParms.pnum3 = "";
                  slotParms.pnum4 = "";
                  slotParms.pnum5 = "";

                  pstmt2.clearParameters();        // clear the parms and check player 2
                  pstmt2.setString(1, slotParms.mNum2);
                  pstmt2.setString(2, slotParms.mNum2);
                  pstmt2.setString(3, slotParms.mNum2);
                  pstmt2.setString(4, slotParms.mNum2);
                  pstmt2.setString(5, slotParms.mNum2);
                  pstmt2.setLong(6, slotParms.date);
                  pstmt2.setInt(7, rest_etime);
                  pstmt2.setInt(8, rest_stime);
                  rs2 = pstmt2.executeQuery();      // execute the prepared stmt

                  while (rs2.next()) {

                     time2 = rs2.getInt("time");
                     t_fb = rs2.getInt("fb");
                     course2 = rs2.getString("courseName");
                     rmNum1 = rs2.getString("mNum1");
                     rmNum2 = rs2.getString("mNum2");
                     rmNum3 = rs2.getString("mNum3");
                     rmNum4 = rs2.getString("mNum4");
                     rmNum5 = rs2.getString("mNum5");

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
                     if ((time2 != slotParms.time) || (!course2.equals( slotParms.course ))) {  // either time or course is diff

                        if ((rest_fb.equals( "Both" ) || rest_fb.equals( sfb2 )) &&
                            (rest_course.equals( "-ALL-" ) || rest_course.equals( course2 ))) {

                           if (slotParms.mNum2.equals( rmNum1 )) {  // count number of players with this member number already scheduled
                              ind++;
                           }
                           if (slotParms.mNum2.equals( rmNum2 )) {
                              ind++;
                           }
                           if (slotParms.mNum2.equals( rmNum3 )) {
                              ind++;
                           }
                           if (slotParms.mNum2.equals( rmNum4 )) {
                              ind++;
                           }
                           if (slotParms.mNum2.equals( rmNum5 )) {
                              ind++;
                           }
                        }
                     }

                  } // end of while members

                  //
                  //  Now check if any other members in this tee time match
                  //
                  slotParms.pnum2 = slotParms.player2;  // save this player name for error msg

                  if (slotParms.mNum2.equals( slotParms.mNum1 )) {

                     ind++;
                     slotParms.pnum1 = slotParms.player1;  // match found for player - save for error msg
                  }
                  if (slotParms.mNum2.equals( slotParms.mNum3 )) {

                     ind++;
                     slotParms.pnum3 = slotParms.player3;  // match found for player - save for error msg
                  }
                  if (slotParms.mNum2.equals( slotParms.mNum4 )) {

                     ind++;
                     slotParms.pnum4 = slotParms.player4;  // match found for player - save for error msg
                  }
                  if (slotParms.mNum2.equals( slotParms.mNum5 )) {

                     ind++;
                     slotParms.pnum5 = slotParms.player5;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     if (!slotParms.player2.equals( slotParms.oldPlayer1 ) &&
                         !slotParms.player2.equals( slotParms.oldPlayer2 ) &&
                         !slotParms.player2.equals( slotParms.oldPlayer3 ) &&
                         !slotParms.player2.equals( slotParms.oldPlayer4 ) &&
                         !slotParms.player2.equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

                        error = true;      // max # exceeded - reject
                     }
                  }

                  pstmt2.close();

               }  // end of member 2 restrictions if

               //
               //   Check Player 3
               //
               if ((error == false) && (!slotParms.mNum3.equals( "" ))) {           // if this player is a member

                  pstmt2 = con.prepareStatement (
                     "SELECT time, fb, courseName, mNum1, mNum2, mNum3, mNum4, mNum5 FROM teecurr2 " +
                     "WHERE (mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?) AND date = ? " +
                     "AND time <= ? AND time >= ?");

                  ind = 0;                           // init fields
                  slotParms.pnum1 = "";
                  slotParms.pnum2 = "";
                  slotParms.pnum3 = "";
                  slotParms.pnum4 = "";
                  slotParms.pnum5 = "";

                  pstmt2.clearParameters();        // clear the parms and check player 2
                  pstmt2.setString(1, slotParms.mNum3);
                  pstmt2.setString(2, slotParms.mNum3);
                  pstmt2.setString(3, slotParms.mNum3);
                  pstmt2.setString(4, slotParms.mNum3);
                  pstmt2.setString(5, slotParms.mNum3);
                  pstmt2.setLong(6, slotParms.date);
                  pstmt2.setInt(7, rest_etime);
                  pstmt2.setInt(8, rest_stime);
                  rs2 = pstmt2.executeQuery();      // execute the prepared stmt

                  while (rs2.next()) {

                     time2 = rs2.getInt("time");
                     t_fb = rs2.getInt("fb");
                     course2 = rs2.getString("courseName");
                     rmNum1 = rs2.getString("mNum1");
                     rmNum2 = rs2.getString("mNum2");
                     rmNum3 = rs2.getString("mNum3");
                     rmNum4 = rs2.getString("mNum4");
                     rmNum5 = rs2.getString("mNum5");

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
                     if ((time2 != slotParms.time) || (!course2.equals( slotParms.course ))) {  // either time or course is diff

                        if ((rest_fb.equals( "Both" ) || rest_fb.equals( sfb2 )) &&
                            (rest_course.equals( "-ALL-" ) || rest_course.equals( course2 ))) {

                           if (slotParms.mNum3.equals( rmNum1 )) {  // count number of players with this member number already scheduled
                              ind++;
                           }
                           if (slotParms.mNum3.equals( rmNum2 )) {
                              ind++;
                           }
                           if (slotParms.mNum3.equals( rmNum3 )) {
                              ind++;
                           }
                           if (slotParms.mNum3.equals( rmNum4 )) {
                              ind++;
                           }
                           if (slotParms.mNum3.equals( rmNum5 )) {
                              ind++;
                           }
                        }
                     }

                  } // end of while members

                  //
                  //  Now check if any other members in this tee time match
                  //
                  slotParms.pnum3 = slotParms.player3;  // save this player name for error msg

                  if (slotParms.mNum3.equals( slotParms.mNum1 )) {

                     ind++;
                     slotParms.pnum1 = slotParms.player1;  // match found for player - save for error msg
                  }
                  if (slotParms.mNum3.equals( slotParms.mNum2 )) {

                     ind++;
                     slotParms.pnum2 = slotParms.player2;  // match found for player - save for error msg
                  }
                  if (slotParms.mNum3.equals( slotParms.mNum4 )) {

                     ind++;
                     slotParms.pnum4 = slotParms.player4;  // match found for player - save for error msg
                  }
                  if (slotParms.mNum3.equals( slotParms.mNum5 )) {

                     ind++;
                     slotParms.pnum5 = slotParms.player5;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     if (!slotParms.player3.equals( slotParms.oldPlayer1 ) &&
                         !slotParms.player3.equals( slotParms.oldPlayer2 ) &&
                         !slotParms.player3.equals( slotParms.oldPlayer3 ) &&
                         !slotParms.player3.equals( slotParms.oldPlayer4 ) &&
                         !slotParms.player3.equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

                        error = true;      // max # exceeded - reject
                     }
                  }

                  pstmt2.close();

               }  // end of member 3 restrictions if

               //
               //   Check Player 4
               //
               if ((error == false) && (!slotParms.mNum4.equals( "" ))) {           // if this player is a member

                  pstmt2 = con.prepareStatement (
                     "SELECT time, fb, courseName, mNum1, mNum2, mNum3, mNum4, mNum5 FROM teecurr2 " +
                     "WHERE (mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?) AND date = ? " +
                     "AND time <= ? AND time >= ?");

                  ind = 0;                           // init fields
                  slotParms.pnum1 = "";
                  slotParms.pnum2 = "";
                  slotParms.pnum3 = "";
                  slotParms.pnum4 = "";
                  slotParms.pnum5 = "";

                  pstmt2.clearParameters();        // clear the parms and check player 2
                  pstmt2.setString(1, slotParms.mNum4);
                  pstmt2.setString(2, slotParms.mNum4);
                  pstmt2.setString(3, slotParms.mNum4);
                  pstmt2.setString(4, slotParms.mNum4);
                  pstmt2.setString(5, slotParms.mNum4);
                  pstmt2.setLong(6, slotParms.date);
                  pstmt2.setInt(7, rest_etime);
                  pstmt2.setInt(8, rest_stime);
                  rs2 = pstmt2.executeQuery();      // execute the prepared stmt

                  while (rs2.next()) {

                     time2 = rs2.getInt("time");
                     t_fb = rs2.getInt("fb");
                     course2 = rs2.getString("courseName");
                     rmNum1 = rs2.getString("mNum1");
                     rmNum2 = rs2.getString("mNum2");
                     rmNum3 = rs2.getString("mNum3");
                     rmNum4 = rs2.getString("mNum4");
                     rmNum5 = rs2.getString("mNum5");

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
                     if ((time2 != slotParms.time) || (!course2.equals( slotParms.course ))) {  // either time or course is diff

                        if ((rest_fb.equals( "Both" ) || rest_fb.equals( sfb2 )) &&
                            (rest_course.equals( "-ALL-" ) || rest_course.equals( course2 ))) {

                           if (slotParms.mNum4.equals( rmNum1 )) {  // count number of players with this member number already scheduled
                              ind++;
                           }
                           if (slotParms.mNum4.equals( rmNum2 )) {
                              ind++;
                           }
                           if (slotParms.mNum4.equals( rmNum3 )) {
                              ind++;
                           }
                           if (slotParms.mNum4.equals( rmNum4 )) {
                              ind++;
                           }
                           if (slotParms.mNum4.equals( rmNum5 )) {
                              ind++;
                           }
                        }
                     }

                  } // end of while members

                  //
                  //  Now check if any other members in this tee time match
                  //
                  slotParms.pnum4 = slotParms.player4;  // save this player name for error msg

                  if (slotParms.mNum4.equals( slotParms.mNum1 )) {

                     ind++;
                     slotParms.pnum1 = slotParms.player1;  // match found for player - save for error msg
                  }
                  if (slotParms.mNum4.equals( slotParms.mNum2 )) {

                     ind++;
                     slotParms.pnum2 = slotParms.player2;  // match found for player - save for error msg
                  }
                  if (slotParms.mNum4.equals( slotParms.mNum3 )) {

                     ind++;
                     slotParms.pnum3 = slotParms.player3;  // match found for player - save for error msg
                  }
                  if (slotParms.mNum4.equals( slotParms.mNum5 )) {

                     ind++;
                     slotParms.pnum5 = slotParms.player5;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     if (!slotParms.player4.equals( slotParms.oldPlayer1 ) &&
                         !slotParms.player4.equals( slotParms.oldPlayer2 ) &&
                         !slotParms.player4.equals( slotParms.oldPlayer3 ) &&
                         !slotParms.player4.equals( slotParms.oldPlayer4 ) &&
                         !slotParms.player4.equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

                        error = true;      // max # exceeded - reject
                     }
                  }

                  pstmt2.close();

               }  // end of member 4 restrictions if

               //
               //   Check Player 5
               //
               if ((error == false) && (!slotParms.mNum5.equals( "" ))) {           // if this player is a member

                  pstmt2 = con.prepareStatement (
                     "SELECT time, fb, courseName, mNum1, mNum2, mNum3, mNum4, mNum5 FROM teecurr2 " +
                     "WHERE (mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?) AND date = ? " +
                     "AND time <= ? AND time >= ?");

                  ind = 0;                           // init fields
                  slotParms.pnum1 = "";
                  slotParms.pnum2 = "";
                  slotParms.pnum3 = "";
                  slotParms.pnum4 = "";
                  slotParms.pnum5 = "";

                  pstmt2.clearParameters();        // clear the parms and check player 2
                  pstmt2.setString(1, slotParms.mNum5);
                  pstmt2.setString(2, slotParms.mNum5);
                  pstmt2.setString(3, slotParms.mNum5);
                  pstmt2.setString(4, slotParms.mNum5);
                  pstmt2.setString(5, slotParms.mNum5);
                  pstmt2.setLong(6, slotParms.date);
                  pstmt2.setInt(7, rest_etime);
                  pstmt2.setInt(8, rest_stime);
                  rs2 = pstmt2.executeQuery();      // execute the prepared stmt

                  while (rs2.next()) {

                     time2 = rs2.getInt("time");
                     t_fb = rs2.getInt("fb");
                     course2 = rs2.getString("courseName");
                     rmNum1 = rs2.getString("mNum1");
                     rmNum2 = rs2.getString("mNum2");
                     rmNum3 = rs2.getString("mNum3");
                     rmNum4 = rs2.getString("mNum4");
                     rmNum5 = rs2.getString("mNum5");

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
                     if ((time2 != slotParms.time) || (!course2.equals( slotParms.course ))) {  // either time or course is diff

                        if ((rest_fb.equals( "Both" ) || rest_fb.equals( sfb2 )) &&
                            (rest_course.equals( "-ALL-" ) || rest_course.equals( course2 ))) {

                           if (slotParms.mNum5.equals( rmNum1 )) {  // count number of players with this member number already scheduled
                              ind++;
                           }
                           if (slotParms.mNum5.equals( rmNum2 )) {
                              ind++;
                           }
                           if (slotParms.mNum5.equals( rmNum3 )) {
                              ind++;
                           }
                           if (slotParms.mNum5.equals( rmNum4 )) {
                              ind++;
                           }
                           if (slotParms.mNum5.equals( rmNum5 )) {
                              ind++;
                           }
                        }
                     }

                  } // end of while members

                  //
                  //  Now check if any other members in this tee time match
                  //
                  slotParms.pnum5 = slotParms.player5;  // save this player name for error msg

                  if (slotParms.mNum5.equals( slotParms.mNum1 )) {

                     ind++;
                     slotParms.pnum1 = slotParms.player1;  // match found for player - save for error msg
                  }
                  if (slotParms.mNum5.equals( slotParms.mNum2 )) {

                     ind++;
                     slotParms.pnum2 = slotParms.player2;  // match found for player - save for error msg
                  }
                  if (slotParms.mNum5.equals( slotParms.mNum3 )) {

                     ind++;
                     slotParms.pnum3 = slotParms.player3;  // match found for player - save for error msg
                  }
                  if (slotParms.mNum5.equals( slotParms.mNum4 )) {

                     ind++;
                     slotParms.pnum4 = slotParms.player4;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     if (!slotParms.player5.equals( slotParms.oldPlayer1 ) &&
                         !slotParms.player5.equals( slotParms.oldPlayer2 ) &&
                         !slotParms.player5.equals( slotParms.oldPlayer3 ) &&
                         !slotParms.player5.equals( slotParms.oldPlayer4 ) &&
                         !slotParms.player5.equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

                        error = true;      // max # exceeded - reject
                     }
                  }

                  pstmt2.close();

               }  // end of member 5 restrictions if

               if (error == true ) {          // if restriction hit

                  break loop3;
               }
            }     // end of IF F/B matches
         }     // end of 'day' if
      }       // end of while (no more restrictions)

      pstmt1.close();

   } catch (Exception e) {

      throw new Exception("Error checking Member Number Restrictions - verifySlot.checkMemNum " + e.getMessage());

   } finally {

        try { rs1.close(); }
        catch (Exception ignore) {}

        try { rs2.close(); }
        catch (Exception ignore) {}

        try { pstmt1.close(); }
        catch (Exception ignore) {}

        try { pstmt2.close(); }
        catch (Exception ignore) {}

    }

   return(error);

 }


/**
 //************************************************************************
 //
 //  Check for a Member Notice defined for the specified date, time, etc.
 //
 //  Override method in place to allow for looking over a span of times (e.g. waitlist).  Original method
 //  will call overriden method and pass the same time for the start and end time.
 //
 //    Called by:  Member_lott
 //                Member_slot
 //                Member_slotm
 //                Member_evntSignUp
 //
 //    Retrun:  Message to display (if found)
 //
 //************************************************************************
 **/

 public static String checkMemNotice(long date, int time, int fb, String course, String day, String type, boolean pro, Connection con) {

     String message = "";

     message = checkMemNotice(date, time, time, fb, course, day, type, pro, con);

     return message;
 }

 public static String checkMemNotice(long date, int stime, int etime, int fb, String course, String day, String type, boolean pro, Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   String message = "";
   String sfb = (fb == 0) ? "Front" : "Back";
   String temp = "";
   String both = "Both";
   String all = "-ALL-";

   int mon = 0;
   int tue = 0;
   int wed = 0;
   int thu = 0;
   int fri = 0;
   int sat = 0;
   int sun = 0;
   int teetime = 0;
   int event = 0;
   int proside = 0;
   int teesheet = 0;

   //
   //  Check for Member Notice message
   //
   try {

       if (!type.equals("teesheet")) {

           pstmt = con.prepareStatement (
                   "SELECT mon, tue, wed, thu, fri, sat, sun, teetime, event, message, proside, teesheet " +
                   "FROM mem_notice " +
                   "WHERE sdate <= ? AND edate >= ? AND stime <= ? AND etime >= ? AND " +
                   "(courseName = ? OR courseName = ?) AND (fb = ? OR fb = ?)");

           pstmt.clearParameters();        // clear the parms and check player 1
           pstmt.setLong(1, date);
           pstmt.setLong(2, date);
           pstmt.setInt(3, etime);
           pstmt.setInt(4, stime);
           pstmt.setString(5, course);
           pstmt.setString(6, all);
           pstmt.setString(7, sfb);
           pstmt.setString(8, both);
           rs = pstmt.executeQuery();      // execute the prepared stmt

           while (rs.next()) {

             mon = rs.getInt("mon");
             tue = rs.getInt("tue");
             wed = rs.getInt("wed");
             thu = rs.getInt("thu");
             fri = rs.getInt("fri");
             sat = rs.getInt("sat");
             sun = rs.getInt("sun");
             teetime = rs.getInt("teetime");
             event = rs.getInt("event");
             temp = rs.getString("message");
             proside = rs.getInt("proside");
             teesheet = rs.getInt("teesheet");

             if ((mon == 1 && day.equals( "Monday")) || (tue == 1 && day.equals( "Tuesday")) || (wed == 1 && day.equals( "Wednesday")) ||
                 (thu == 1 && day.equals( "Thursday")) || (fri == 1 && day.equals( "Friday")) || (sat == 1 && day.equals( "Saturday")) ||
                 (sun == 1 && day.equals( "Sunday"))) {

                if ((teetime == 1 && type.equals( "teetime")) || (event == 1 && type.equals( "event")) || teesheet == 1 && type.equals( "teesheet")) {
                    if ((pro && proside == 1) || (!pro)) {
                        message = message + temp + "<BR><BR>";         // join and space in case more than one message
                    }
                }
             }

          }   // end of WHILE

      } else {      // to be displayed on teesheet, simply want to know if we have any notices to worry about

          pstmt = con.prepareStatement(
             "SELECT mon, tue, wed, thu, fri, sat, sun, proside " +
             "FROM mem_notice " +
             "WHERE sdate <= ? AND edate >= ? AND " +
             "(courseName = ? OR courseName = ?) AND teesheet=1");

          pstmt.clearParameters();        // clear the parms and check player 1
          pstmt.setLong(1, date);
          pstmt.setLong(2, date);
          pstmt.setString(3, course);
          pstmt.setString(4, all);
          rs = pstmt.executeQuery();      // execute the prepared stmt

          while (rs.next()) {        // there's a mem_notice to display, return successful

             mon = rs.getInt("mon");
             tue = rs.getInt("tue");
             wed = rs.getInt("wed");
             thu = rs.getInt("thu");
             fri = rs.getInt("fri");
             sat = rs.getInt("sat");
             sun = rs.getInt("sun");
             proside = rs.getInt("proside");

             if ((mon == 1 && day.equals( "Monday")) || (tue == 1 && day.equals( "Tuesday")) || (wed == 1 && day.equals( "Wednesday")) ||
                 (thu == 1 && day.equals( "Thursday")) || (fri == 1 && day.equals( "Friday")) || (sat == 1 && day.equals( "Saturday")) ||
                 (sun == 1 && day.equals( "Sunday"))) {

                 if ((pro && proside == 1) || (!pro)) {
                     message = "found";
                 }
             }
          }

      }

   } catch (Exception e) {

      Utilities.logError("Error checking for Member Notices - verifySlot.checkMemNotices: " + e.getMessage());        // log the error message

   } finally {

      try { rs.close(); }
      catch (Exception ignore) {}

      try { pstmt.close(); }
      catch (Exception ignore) {}

   }

   return(message);

 }


/**
 //************************************************************************
 //
 //  checkSched
 //
 //    Check if any of the players are already scheduled today
 //
 //************************************************************************
 **/

 public static void checkSched(parmSlot slotParms, Connection con)
         throws Exception {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   int time2 = 0;
   int fb2 = 0;
   int count = 0;

   int max = slotParms.rnds;           // max allowed rounds per day for members (club option)
   int hrsbtwn = slotParms.hrsbtwn;    // minumum hours between tee times (club option when rnds > 1)

   String course2 = "";

   boolean hit1 = false;
   boolean hit2 = false;

   String sql_lreqs3 =
       "SELECT courseName FROM lreqs3 " +
       "WHERE (player1 = ? OR player2 = ? OR player3 = ? OR player4 = ? OR player5 = ? OR " +
              "player6 = ? OR player7 = ? OR player8 = ? OR player9 = ? OR player10 = ? OR " +
              "player11 = ? OR player12 = ? OR player13 = ? OR player14 = ? OR player15 = ? OR " +
              "player16 = ? OR player17 = ? OR player18 = ? OR player19 = ? OR player20 = ? OR " +
              "player21 = ? OR player22 = ? OR player23 = ? OR player24 = ? OR player25 = ?) AND date = ? " +
       "ORDER BY time";

   String sql_teecurr2 =
       "SELECT time, fb, courseName FROM teecurr2 " +
       "WHERE date = ? AND (player1 = ? OR player2 = ? OR player3 = ? OR player4 = ? OR player5 = ?) " +
       "ORDER BY time";


   // temporary custom for Long Cove, Week of The Heritage Classic
   // Case #: 1038
   if ( slotParms.club.equals( "longcove" ) &&
        slotParms.date >= 20100414 &&
        slotParms.date <= 20100418 ) {

       max = 1; // max of one round per day
   }
   // end temporary Long Cove custom


   try {

      slotParms.hit = false;
      slotParms.hit2 = false;        // lottery time scheduled for this date
      slotParms.hit3 = false;        // multiple tee times too close together
      slotParms.player = "";
      slotParms.time2 = 0;
      count = 0;

      if ((!slotParms.player1.equals( "" )) && (!slotParms.player1.equalsIgnoreCase( "x" )) && (slotParms.g1.equals( "" ))) {

         pstmt = con.prepareStatement ( sql_teecurr2 );

         pstmt.clearParameters();        // clear the parms and check player 1
         pstmt.setLong(1, slotParms.date);
         pstmt.setString(2, slotParms.player1);
         pstmt.setString(3, slotParms.player1);
         pstmt.setString(4, slotParms.player1);
         pstmt.setString(5, slotParms.player1);
         pstmt.setString(6, slotParms.player1);
         rs = pstmt.executeQuery();      // execute the prepared stmt

         while (rs.next()) {

            time2 = rs.getInt("time");
            fb2 = rs.getInt("fb");
            course2 = rs.getString("courseName");

            if ((time2 != slotParms.time) || (fb2 != slotParms.fb) || (!course2.equals( slotParms.course ))) {      // if not this tee time

               if (!slotParms.player1.equals( slotParms.oldPlayer1 ) &&
                   !slotParms.player1.equals( slotParms.oldPlayer2 ) &&
                   !slotParms.player1.equals( slotParms.oldPlayer3 ) &&
                   !slotParms.player1.equals( slotParms.oldPlayer4 ) &&
                   !slotParms.player1.equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

                  hit1 = false;                               // init hit indicator

                  //
                  //  If club is North Ridge or Rogue Valley, skip if different courses (1 is practice)
                  //
                  if (slotParms.club.equals( "northridge" ) || slotParms.club.equals( "roguevalley" )) {

                     if (course2.equals( slotParms.course )) {       // error if same course

                        hit1 = true;                       // player already scheduled on this date
                     }

                  } else {        // not North Ridge or Rogue Valley

                     hit1 = true;                       // player already scheduled on this date
                  }

                  if (hit1 == true) {                 // if player already scheduled

                     count++;                                    // add to tee time counter for member
                     slotParms.player = slotParms.player1;       // get player for message
                     slotParms.time2 = time2;                    // save time for message
                     slotParms.course2 = course2;                // save course for message

                     //
                     //  check if requested tee time is too close to this one
                     //
                     if (max > 1 && hrsbtwn > 0) {

                        if (time2 < slotParms.time) {            // if this tee time is before the time requested

                           if (slotParms.time < (time2 + (hrsbtwn * 100))) {     // if this tee time is within range

                              slotParms.hit3 = true;                       // tee times not far enough apart
                           }

                        } else {                                 // this time is after the requested time

                           if (time2 < (slotParms.time + (hrsbtwn * 100))) {     // if this tee time is within range

                              slotParms.hit3 = true;                       // tee times not far enough apart
                           }
                        }
                     }
                  }
               }
            }
         }
         pstmt.close();

         //
         //  check if player already on a lottery request
         //
         pstmt = con.prepareStatement ( sql_lreqs3 );

         pstmt.clearParameters();        // clear the parms and check player1
         pstmt.setString(1, slotParms.player1);
         pstmt.setString(2, slotParms.player1);
         pstmt.setString(3, slotParms.player1);
         pstmt.setString(4, slotParms.player1);
         pstmt.setString(5, slotParms.player1);
         pstmt.setString(6, slotParms.player1);
         pstmt.setString(7, slotParms.player1);
         pstmt.setString(8, slotParms.player1);
         pstmt.setString(9, slotParms.player1);
         pstmt.setString(10, slotParms.player1);
         pstmt.setString(11, slotParms.player1);
         pstmt.setString(12, slotParms.player1);
         pstmt.setString(13, slotParms.player1);
         pstmt.setString(14, slotParms.player1);
         pstmt.setString(15, slotParms.player1);
         pstmt.setString(16, slotParms.player1);
         pstmt.setString(17, slotParms.player1);
         pstmt.setString(18, slotParms.player1);
         pstmt.setString(19, slotParms.player1);
         pstmt.setString(20, slotParms.player1);
         pstmt.setString(21, slotParms.player1);
         pstmt.setString(22, slotParms.player1);
         pstmt.setString(23, slotParms.player1);
         pstmt.setString(24, slotParms.player1);
         pstmt.setString(25, slotParms.player1);
         pstmt.setLong(26, slotParms.date);
         rs = pstmt.executeQuery();      // execute the prepared stmt

         while (rs.next()) {

            course2 = rs.getString("courseName");

            if (!slotParms.player1.equals( slotParms.oldPlayer1 ) &&
                !slotParms.player1.equals( slotParms.oldPlayer2 ) &&
                !slotParms.player1.equals( slotParms.oldPlayer3 ) &&
                !slotParms.player1.equals( slotParms.oldPlayer4 ) &&
                !slotParms.player1.equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

               hit1 = false;                               // init hit indicator

               //
               //  If club is North Ridge or Rogue Valley, skip if different courses (1 is practice)
               //
               if (slotParms.club.equals( "northridge" ) || slotParms.club.equals( "roguevalley" )) {

                  if (course2.equals( slotParms.course )) {       // error if same course

                     hit1 = true;                       // player already scheduled on this date
                  }

               } else {        // not North Ridge or Rogue Valley

                  hit1 = true;                       // player already scheduled on this date
               }

               if (hit1 == true) {                 // if player already scheduled

                  count++;                                           // add to tee time counter for member
                  hit2 = true;
                  slotParms.player = slotParms.player1;              // get player for message
               }
            }
         }
         pstmt.close();
      }

      //
      //  See if we exceeded max allowed for day - if so, set indicator
      //
      if (count >= max) {

         slotParms.hit = true;                       // player already scheduled on this date (max times allowed)

         if (hit2 == true) {     // if we hit on lottery

            slotParms.hit2 = true;                    // player has a lottery request scheduled on this date
         }
      }

      if (slotParms.hit == false && slotParms.hit3 == false) {   // if we haven't already hit an error

         count = 0;             // init counter
         hit2 = false;

         //
         // check player 2
         //
         if ((!slotParms.player2.equals( "" )) && (!slotParms.player2.equalsIgnoreCase( "x" )) && (slotParms.g2.equals( "" )) && (slotParms.hit == false)) {

            pstmt = con.prepareStatement ( sql_teecurr2 );

            pstmt.clearParameters();        // clear the parms
            pstmt.setLong(1, slotParms.date);
            pstmt.setString(2, slotParms.player2);
            pstmt.setString(3, slotParms.player2);
            pstmt.setString(4, slotParms.player2);
            pstmt.setString(5, slotParms.player2);
            pstmt.setString(6, slotParms.player2);
            rs = pstmt.executeQuery();      // execute the prepared stmt

            while (rs.next()) {

               time2 = rs.getInt("time");
               fb2 = rs.getInt("fb");
               course2 = rs.getString("courseName");

               if ((time2 != slotParms.time) || (fb2 != slotParms.fb) || (!course2.equals( slotParms.course ))) {      // if not this tee time

                  if (!slotParms.player2.equals( slotParms.oldPlayer1 ) &&
                      !slotParms.player2.equals( slotParms.oldPlayer2 ) &&
                      !slotParms.player2.equals( slotParms.oldPlayer3 ) &&
                      !slotParms.player2.equals( slotParms.oldPlayer4 ) &&
                      !slotParms.player2.equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

                     hit1 = false;                               // init hit indicator

                     //
                     //  If club is North Ridge or Rogue Valley, skip if different courses (1 is practice)
                     //
                     if (slotParms.club.equals( "northridge" ) || slotParms.club.equals( "roguevalley" )) {

                        if (course2.equals( slotParms.course )) {       // error if same course

                           hit1 = true;                       // player already scheduled on this date
                        }

                     } else {        // not North Ridge or Rogue Valley

                        hit1 = true;                       // player already scheduled on this date
                     }

                     if (hit1 == true) {                 // if player already scheduled

                        count++;                                    // add to tee time counter for member
                        slotParms.player = slotParms.player2;              // get player for message
                        slotParms.time2 = time2;                    // save time for message
                        slotParms.course2 = course2;                // save course for message

                        //
                        //  check if requested tee time is too close to this one
                        //
                        if (max > 1 && hrsbtwn > 0) {

                           if (time2 < slotParms.time) {            // if this tee time is before the time requested

                              if (slotParms.time < (time2 + (hrsbtwn * 100))) {     // if this tee time is within range

                                 slotParms.hit3 = true;                       // tee times not far enough apart
                              }

                           } else {                                 // this time is after the requested time

                              if (time2 < (slotParms.time + (hrsbtwn * 100))) {     // if this tee time is within range

                                 slotParms.hit3 = true;                       // tee times not far enough apart
                              }
                           }
                        }
                     }
                  }
               }
            }
            pstmt.close();

            //
            //  check if player already on a lottery request
            //
            pstmt = con.prepareStatement ( sql_lreqs3 );

            pstmt.clearParameters();        // clear the parms and check player2
            pstmt.setString(1, slotParms.player2);
            pstmt.setString(2, slotParms.player2);
            pstmt.setString(3, slotParms.player2);
            pstmt.setString(4, slotParms.player2);
            pstmt.setString(5, slotParms.player2);
            pstmt.setString(6, slotParms.player2);
            pstmt.setString(7, slotParms.player2);
            pstmt.setString(8, slotParms.player2);
            pstmt.setString(9, slotParms.player2);
            pstmt.setString(10, slotParms.player2);
            pstmt.setString(11, slotParms.player2);
            pstmt.setString(12, slotParms.player2);
            pstmt.setString(13, slotParms.player2);
            pstmt.setString(14, slotParms.player2);
            pstmt.setString(15, slotParms.player2);
            pstmt.setString(16, slotParms.player2);
            pstmt.setString(17, slotParms.player2);
            pstmt.setString(18, slotParms.player2);
            pstmt.setString(19, slotParms.player2);
            pstmt.setString(20, slotParms.player2);
            pstmt.setString(21, slotParms.player2);
            pstmt.setString(22, slotParms.player2);
            pstmt.setString(23, slotParms.player2);
            pstmt.setString(24, slotParms.player2);
            pstmt.setString(25, slotParms.player2);
            pstmt.setLong(26, slotParms.date);
            rs = pstmt.executeQuery();      // execute the prepared stmt

            while (rs.next()) {

               course2 = rs.getString("courseName");

               if (!slotParms.player2.equals( slotParms.oldPlayer1 ) &&
                   !slotParms.player2.equals( slotParms.oldPlayer2 ) &&
                   !slotParms.player2.equals( slotParms.oldPlayer3 ) &&
                   !slotParms.player2.equals( slotParms.oldPlayer4 ) &&
                   !slotParms.player2.equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

                  hit1 = false;                               // init hit indicator

                  //
                  //  If club is North Ridge or Rogue Valley, skip if different courses (1 is practice)
                  //
                  if (slotParms.club.equals( "northridge" ) || slotParms.club.equals( "roguevalley" )) {

                     if (course2.equals( slotParms.course )) {       // error if same course

                        hit1 = true;                       // player already scheduled on this date
                     }

                  } else {        // not North Ridge or Rogue Valley

                     hit1 = true;                       // player already scheduled on this date
                  }

                  if (hit1 == true) {                 // if player already scheduled

                     count++;                                           // add to tee time counter for member
                     hit2 = true;
                     slotParms.player = slotParms.player2;              // get player for message
                  }
               }
            }
            pstmt.close();
         }

         //
         //  See if we exceeded max allowed for day - if so, set indicator
         //
         if (count >= max) {

            slotParms.hit = true;                       // player already scheduled on this date (max times allowed)

            if (hit2 == true) {     // if we hit on lottery

               slotParms.hit2 = true;                    // player has a lottery request scheduled on this date
            }
         }
      }

      if (slotParms.hit == false && slotParms.hit3 == false) {   // if we haven't already hit an error

         count = 0;             // init counter
         hit2 = false;

         //
         // check player 3
         //
         if ((!slotParms.player3.equals( "" )) && (!slotParms.player3.equalsIgnoreCase( "x" )) && (slotParms.g3.equals( "" )) && (slotParms.hit == false)) {

            pstmt = con.prepareStatement ( sql_teecurr2 );

            pstmt.clearParameters();        // clear the parms
            pstmt.setLong(1, slotParms.date);
            pstmt.setString(2, slotParms.player3);
            pstmt.setString(3, slotParms.player3);
            pstmt.setString(4, slotParms.player3);
            pstmt.setString(5, slotParms.player3);
            pstmt.setString(6, slotParms.player3);
            rs = pstmt.executeQuery();      // execute the prepared stmt

            while (rs.next()) {

               time2 = rs.getInt("time");
               fb2 = rs.getInt("fb");
               course2 = rs.getString("courseName");

               if ((time2 != slotParms.time) || (fb2 != slotParms.fb) || (!course2.equals( slotParms.course ))) {      // if not this tee time

                  if (!slotParms.player3.equals( slotParms.oldPlayer1 ) &&
                      !slotParms.player3.equals( slotParms.oldPlayer2 ) &&
                      !slotParms.player3.equals( slotParms.oldPlayer3 ) &&
                      !slotParms.player3.equals( slotParms.oldPlayer4 ) &&
                      !slotParms.player3.equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

                     hit1 = false;                               // init hit indicator

                     //
                     //  If club is North Ridge or Rogue Valley, skip if different courses (1 is practice)
                     //
                     if (slotParms.club.equals( "northridge" ) || slotParms.club.equals( "roguevalley" )) {

                        if (course2.equals( slotParms.course )) {       // error if same course

                           hit1 = true;                       // player already scheduled on this date
                        }

                     } else {        // not North Ridge or Rogue Valley

                        hit1 = true;                       // player already scheduled on this date
                     }

                     if (hit1 == true) {                 // if player already scheduled

                        count++;                                    // add to tee time counter for member
                        slotParms.player = slotParms.player3;              // get player for message
                        slotParms.time2 = time2;                    // save time for message
                        slotParms.course2 = course2;                // save course for message

                        //
                        //  check if requested tee time is too close to this one
                        //
                        if (max > 1 && hrsbtwn > 0) {

                           if (time2 < slotParms.time) {            // if this tee time is before the time requested

                              if (slotParms.time < (time2 + (hrsbtwn * 100))) {     // if this tee time is within range

                                 slotParms.hit3 = true;                       // tee times not far enough apart
                              }

                           } else {                                 // this time is after the requested time

                              if (time2 < (slotParms.time + (hrsbtwn * 100))) {     // if this tee time is within range

                                 slotParms.hit3 = true;                       // tee times not far enough apart
                              }
                           }
                        }
                     }
                  }
               }
            }
            pstmt.close();

            //
            //  check if player already on a lottery request
            //
            pstmt = con.prepareStatement ( sql_lreqs3 );

            pstmt.clearParameters();        // clear the parms and check player3
            pstmt.setString(1, slotParms.player3);
            pstmt.setString(2, slotParms.player3);
            pstmt.setString(3, slotParms.player3);
            pstmt.setString(4, slotParms.player3);
            pstmt.setString(5, slotParms.player3);
            pstmt.setString(6, slotParms.player3);
            pstmt.setString(7, slotParms.player3);
            pstmt.setString(8, slotParms.player3);
            pstmt.setString(9, slotParms.player3);
            pstmt.setString(10, slotParms.player3);
            pstmt.setString(11, slotParms.player3);
            pstmt.setString(12, slotParms.player3);
            pstmt.setString(13, slotParms.player3);
            pstmt.setString(14, slotParms.player3);
            pstmt.setString(15, slotParms.player3);
            pstmt.setString(16, slotParms.player3);
            pstmt.setString(17, slotParms.player3);
            pstmt.setString(18, slotParms.player3);
            pstmt.setString(19, slotParms.player3);
            pstmt.setString(20, slotParms.player3);
            pstmt.setString(21, slotParms.player3);
            pstmt.setString(22, slotParms.player3);
            pstmt.setString(23, slotParms.player3);
            pstmt.setString(24, slotParms.player3);
            pstmt.setString(25, slotParms.player3);
            pstmt.setLong(26, slotParms.date);
            rs = pstmt.executeQuery();      // execute the prepared stmt

            while (rs.next()) {

               course2 = rs.getString("courseName");

               if (!slotParms.player3.equals( slotParms.oldPlayer1 ) &&
                   !slotParms.player3.equals( slotParms.oldPlayer2 ) &&
                   !slotParms.player3.equals( slotParms.oldPlayer3 ) &&
                   !slotParms.player3.equals( slotParms.oldPlayer4 ) &&
                   !slotParms.player3.equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

                  hit1 = false;                               // init hit indicator

                  //
                  //  If club is North Ridge or Rogue Valley, skip if different courses (1 is practice)
                  //
                  if (slotParms.club.equals( "northridge" ) || slotParms.club.equals( "roguevalley" )) {

                     if (course2.equals( slotParms.course )) {       // error if same course

                        hit1 = true;                       // player already scheduled on this date
                     }

                  } else {        // not North Ridge or Rogue Valley

                     hit1 = true;                       // player already scheduled on this date
                  }

                  if (hit1 == true) {                 // if player already scheduled

                     count++;                                    // add to tee time counter for member
                     hit2 = true;
                     slotParms.player = slotParms.player3;              // get player for message
                  }
               }
            }
            pstmt.close();
         }

         //
         //  See if we exceeded max allowed for day - if so, set indicator
         //
         if (count >= max) {

            slotParms.hit = true;                       // player already scheduled on this date (max times allowed)

            if (hit2 == true) {     // if we hit on lottery

               slotParms.hit2 = true;                    // player has a lottery request scheduled on this date
            }
         }
      }
      if (slotParms.hit == false && slotParms.hit3 == false) {   // if we haven't already hit an error

         count = 0;             // init counter
         hit2 = false;

         //
         // check player 4
         //
         if ((!slotParms.player4.equals( "" )) && (!slotParms.player4.equalsIgnoreCase( "x" )) && (slotParms.g4.equals( "" )) && (slotParms.hit == false)) {

            pstmt = con.prepareStatement ( sql_teecurr2 );

            pstmt.clearParameters();        // clear the parms
            pstmt.setLong(1, slotParms.date);
            pstmt.setString(2, slotParms.player4);
            pstmt.setString(3, slotParms.player4);
            pstmt.setString(4, slotParms.player4);
            pstmt.setString(5, slotParms.player4);
            pstmt.setString(6, slotParms.player4);
            rs = pstmt.executeQuery();      // execute the prepared stmt

            while (rs.next()) {

               time2 = rs.getInt("time");
               fb2 = rs.getInt("fb");
               course2 = rs.getString("courseName");

               if ((time2 != slotParms.time) || (fb2 != slotParms.fb) || (!course2.equals( slotParms.course ))) {      // if not this tee time

                  if (!slotParms.player4.equals( slotParms.oldPlayer1 ) &&
                      !slotParms.player4.equals( slotParms.oldPlayer2 ) &&
                      !slotParms.player4.equals( slotParms.oldPlayer3 ) &&
                      !slotParms.player4.equals( slotParms.oldPlayer4 ) &&
                      !slotParms.player4.equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

                     hit1 = false;                               // init hit indicator

                     //
                     //  If club is North Ridge or Rogue Valley, skip if different courses (1 is practice)
                     //
                     if (slotParms.club.equals( "northridge" ) || slotParms.club.equals( "roguevalley" )) {

                        if (course2.equals( slotParms.course )) {       // error if same course

                           hit1 = true;                       // player already scheduled on this date
                        }

                     } else {        // not North Ridge or Rogue Valley

                        hit1 = true;                       // player already scheduled on this date
                     }

                     if (hit1 == true) {                 // if player already scheduled

                        count++;                                    // add to tee time counter for member
                        slotParms.player = slotParms.player4;              // get player for message
                        slotParms.time2 = time2;                    // save time for message
                        slotParms.course2 = course2;                // save course for message

                        //
                        //  check if requested tee time is too close to this one
                        //
                        if (max > 1 && hrsbtwn > 0) {

                           if (time2 < slotParms.time) {            // if this tee time is before the time requested

                              if (slotParms.time < (time2 + (hrsbtwn * 100))) {     // if this tee time is within range

                                 slotParms.hit3 = true;                       // tee times not far enough apart
                              }

                           } else {                                 // this time is after the requested time

                              if (time2 < (slotParms.time + (hrsbtwn * 100))) {     // if this tee time is within range

                                 slotParms.hit3 = true;                       // tee times not far enough apart
                              }
                           }
                        }
                     }
                  }
               }
            }
            pstmt.close();

            //
            //  check if player already on a lottery request
            //
            pstmt = con.prepareStatement ( sql_lreqs3 );

            pstmt.clearParameters();        // clear the parms and check player4
            pstmt.setString(1, slotParms.player4);
            pstmt.setString(2, slotParms.player4);
            pstmt.setString(3, slotParms.player4);
            pstmt.setString(4, slotParms.player4);
            pstmt.setString(5, slotParms.player4);
            pstmt.setString(6, slotParms.player4);
            pstmt.setString(7, slotParms.player4);
            pstmt.setString(8, slotParms.player4);
            pstmt.setString(9, slotParms.player4);
            pstmt.setString(10, slotParms.player4);
            pstmt.setString(11, slotParms.player4);
            pstmt.setString(12, slotParms.player4);
            pstmt.setString(13, slotParms.player4);
            pstmt.setString(14, slotParms.player4);
            pstmt.setString(15, slotParms.player4);
            pstmt.setString(16, slotParms.player4);
            pstmt.setString(17, slotParms.player4);
            pstmt.setString(18, slotParms.player4);
            pstmt.setString(19, slotParms.player4);
            pstmt.setString(20, slotParms.player4);
            pstmt.setString(21, slotParms.player4);
            pstmt.setString(22, slotParms.player4);
            pstmt.setString(23, slotParms.player4);
            pstmt.setString(24, slotParms.player4);
            pstmt.setString(25, slotParms.player4);
            pstmt.setLong(26, slotParms.date);
            rs = pstmt.executeQuery();      // execute the prepared stmt

            while (rs.next()) {

               course2 = rs.getString("courseName");

               if (!slotParms.player4.equals( slotParms.oldPlayer1 ) &&
                   !slotParms.player4.equals( slotParms.oldPlayer2 ) &&
                   !slotParms.player4.equals( slotParms.oldPlayer3 ) &&
                   !slotParms.player4.equals( slotParms.oldPlayer4 ) &&
                   !slotParms.player4.equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

                  hit1 = false;                               // init hit indicator

                  //
                  //  If club is North Ridge or Rogue Valley, skip if different courses (1 is practice)
                  //
                  if (slotParms.club.equals( "northridge" ) || slotParms.club.equals( "roguevalley" )) {

                     if (course2.equals( slotParms.course )) {       // error if same course

                        hit1 = true;                       // player already scheduled on this date
                     }

                  } else {        // not North Ridge or Rogue Valley

                     hit1 = true;                       // player already scheduled on this date
                  }

                  if (hit1 == true) {                 // if player already scheduled

                     count++;                                    // add to tee time counter for member
                     hit2 = true;
                     slotParms.player = slotParms.player4;              // get player for message
                  }
               }
            }
            pstmt.close();
         }

         //
         //  See if we exceeded max allowed for day - if so, set indicator
         //
         if (count >= max) {

            slotParms.hit = true;                       // player already scheduled on this date (max times allowed)

            if (hit2 == true) {     // if we hit on lottery

               slotParms.hit2 = true;                    // player has a lottery request scheduled on this date
            }
         }
      }
      if (slotParms.hit == false && slotParms.hit3 == false) {   // if we haven't already hit an error

         count = 0;             // init counter
         hit2 = false;

         //
         // check player 5
         //
         if ((!slotParms.player5.equals( "" )) && (!slotParms.player5.equalsIgnoreCase( "x" )) && (slotParms.g5.equals( "" )) && (slotParms.hit == false)) {

            pstmt = con.prepareStatement ( sql_teecurr2 );

            pstmt.clearParameters();        // clear the parms
            pstmt.setLong(1, slotParms.date);
            pstmt.setString(2, slotParms.player5);
            pstmt.setString(3, slotParms.player5);
            pstmt.setString(4, slotParms.player5);
            pstmt.setString(5, slotParms.player5);
            pstmt.setString(6, slotParms.player5);
            rs = pstmt.executeQuery();      // execute the prepared stmt

            while (rs.next()) {

               time2 = rs.getInt("time");
               fb2 = rs.getInt("fb");
               course2 = rs.getString("courseName");

               if ((time2 != slotParms.time) || (fb2 != slotParms.fb) || (!course2.equals( slotParms.course ))) {      // if not this tee time

                  if (!slotParms.player5.equals( slotParms.oldPlayer1 ) &&
                      !slotParms.player5.equals( slotParms.oldPlayer2 ) &&
                      !slotParms.player5.equals( slotParms.oldPlayer3 ) &&
                      !slotParms.player5.equals( slotParms.oldPlayer4 ) &&
                      !slotParms.player5.equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

                     hit1 = false;                               // init hit indicator

                     //
                     //  If club is North Ridge or Rogue Valley, skip if different courses (1 is practice)
                     //
                     if (slotParms.club.equals( "northridge" ) || slotParms.club.equals( "roguevalley" )) {

                        if (course2.equals( slotParms.course )) {       // error if same course

                           hit1 = true;                       // player already scheduled on this date
                        }

                     } else {        // not North Ridge or Rogue Valley

                        hit1 = true;                       // player already scheduled on this date
                     }

                     if (hit1 == true) {                 // if player already scheduled

                        count++;                                    // add to tee time counter for member
                        slotParms.player = slotParms.player5;              // get player for message
                        slotParms.time2 = time2;                    // save time for message
                        slotParms.course2 = course2;                // save course for message

                        //
                        //  check if requested tee time is too close to this one
                        //
                        if (max > 1 && hrsbtwn > 0) {

                           if (time2 < slotParms.time) {            // if this tee time is before the time requested

                              if (slotParms.time < (time2 + (hrsbtwn * 100))) {     // if this tee time is within range

                                 slotParms.hit3 = true;                       // tee times not far enough apart
                              }

                           } else {                                 // this time is after the requested time

                              if (time2 < (slotParms.time + (hrsbtwn * 100))) {     // if this tee time is within range

                                 slotParms.hit3 = true;                       // tee times not far enough apart
                              }
                           }
                        }
                     }
                  }
               }
            }
            pstmt.close();

            //
            //  check if player already on a lottery request
            //
            pstmt = con.prepareStatement ( sql_lreqs3 );

            pstmt.clearParameters();        // clear the parms and check player5
            pstmt.setString(1, slotParms.player5);
            pstmt.setString(2, slotParms.player5);
            pstmt.setString(3, slotParms.player5);
            pstmt.setString(4, slotParms.player5);
            pstmt.setString(5, slotParms.player5);
            pstmt.setString(6, slotParms.player5);
            pstmt.setString(7, slotParms.player5);
            pstmt.setString(8, slotParms.player5);
            pstmt.setString(9, slotParms.player5);
            pstmt.setString(10, slotParms.player5);
            pstmt.setString(11, slotParms.player5);
            pstmt.setString(12, slotParms.player5);
            pstmt.setString(13, slotParms.player5);
            pstmt.setString(14, slotParms.player5);
            pstmt.setString(15, slotParms.player5);
            pstmt.setString(16, slotParms.player5);
            pstmt.setString(17, slotParms.player5);
            pstmt.setString(18, slotParms.player5);
            pstmt.setString(19, slotParms.player5);
            pstmt.setString(20, slotParms.player5);
            pstmt.setString(21, slotParms.player5);
            pstmt.setString(22, slotParms.player5);
            pstmt.setString(23, slotParms.player5);
            pstmt.setString(24, slotParms.player5);
            pstmt.setString(25, slotParms.player5);
            pstmt.setLong(26, slotParms.date);
            rs = pstmt.executeQuery();      // execute the prepared stmt

            while (rs.next()) {

               course2 = rs.getString("courseName");

               if (!slotParms.player5.equals( slotParms.oldPlayer1 ) &&
                   !slotParms.player5.equals( slotParms.oldPlayer2 ) &&
                   !slotParms.player5.equals( slotParms.oldPlayer3 ) &&
                   !slotParms.player5.equals( slotParms.oldPlayer4 ) &&
                   !slotParms.player5.equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

                  hit1 = false;                               // init hit indicator

                  //
                  //  If club is North Ridge or Rogue Valley, skip if different courses (1 is practice)
                  //
                  if (slotParms.club.equals( "northridge" ) || slotParms.club.equals( "roguevalley" )) {

                     if (course2.equals( slotParms.course )) {       // error if same course

                        hit1 = true;                       // player already scheduled on this date
                     }

                  } else {        // not North Ridge or Rogue Valley

                     hit1 = true;                       // player already scheduled on this date
                  }

                  if (hit1 == true) {                 // if player already scheduled

                     count++;                                    // add to tee time counter for member
                     hit2 = true;
                     slotParms.player = slotParms.player5;              // get player for message
                  }
               }
            }
            pstmt.close();
         }

         //
         //  See if we exceeded max allowed for day - if so, set indicator
         //
         if (count >= max) {

            slotParms.hit = true;                       // player already scheduled on this date (max times allowed)

            if (hit2 == true) {     // if we hit on lottery

               slotParms.hit2 = true;                    // player has a lottery request scheduled on this date
            }
         }
      }

   } catch (Exception e) {

      throw new Exception("Error checking if Player Already Scheduled - verifySlot.checkSched " + e.getMessage());

   } finally {

          try { rs.close(); }
          catch (Exception ignore) {}

          try { pstmt.close(); }
          catch (Exception ignore) {}

   }

   return;

 }


/**
 //************************************************************************
 //
 //  checkHazGrps  (for Hazeltine National only)
 //
 //     Check singles and 2-somes for restricted times (can't have 2 consecutive groups)
 //
 //************************************************************************
 **/

 public static boolean checkHazGrps(parmSlot slotParms, Connection con) {


   boolean error = false;
   boolean restricted = false;

   //
   //  First see if this is a restricted time
   //
   if (slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday") || slotParms.date == Hdate1 || slotParms.date == Hdate2 || slotParms.date == Hdate3) {   // w/e or holiday?

      if (slotParms.time > 1059 && slotParms.time < 1501) {       // 11:00 - 3:00 ?

         restricted = true;                   // yes, check it
      }

   } else {

      if (slotParms.day.equals("Tuesday") || slotParms.day.equals("Wednesday")) {

         if ((slotParms.time > 959 && slotParms.time < 1201) || (slotParms.time > 1329 && slotParms.time < 1601)) {       // 10:00 - 12:00 OR 1:30 - 4:00 ?

            restricted = true;                   // yes, check it
         }

      } else {

         if (slotParms.day.equals("Thursday")) {

            if ((slotParms.time > 959 && slotParms.time < 1201) || (slotParms.time > 1259 && slotParms.time < 1601)) {       // 10:00 - 12:00 OR 1:00 - 4:00 ?

               restricted = true;                   // yes, check it
            }

         } else {

            if (slotParms.day.equals("Friday")) {

               if ((slotParms.time > 959 && slotParms.time < 1131) || (slotParms.time > 1459 && slotParms.time < 1631)) {       // 10:00 - 11:30 OR 3:00 - 2:30 ?

                  restricted = true;                   // yes, check it
               }
            }
         }
      }
   }

   //
   //   If a restricted time, then check if there is another small group (1 or 2 players)
   //   immediately in front of or behind this group.
   //
   if (restricted == true) {        // should we check for back to back small groups ?

      PreparedStatement pstmt = null;
      ResultSet rs = null;

      boolean found = false;

      String p1 = "";
      String p2 = "";
      String p3 = "";
      String p4 = "";
      String p5 = "";

      try {

         pstmt = con.prepareStatement (
            "SELECT time, player1, player2, player3, player4, player5 " +
            "FROM teecurr2 " +
            "WHERE date = ? AND time > ? AND time < ? AND fb = ?");

         pstmt.clearParameters();
         pstmt.setLong(1, slotParms.date);
         pstmt.setInt(2, slotParms.time - 100);     // start back one hour
         pstmt.setInt(3, slotParms.time + 100);     // end ahead one hour
         pstmt.setInt(4, slotParms.fb);

         rs = pstmt.executeQuery();

         loop1:
         while (rs.next()) {

            if ( rs.getInt("time") == slotParms.time ) {      // is this the tee time we are checking?

               found = true;          // yes, found it

               if (!p1.equals( "" ) && p3.equals( "" ) && p4.equals( "" ) && p5.equals( "" )) {    // if last group was single or 2-some

                  error = true;       // set error
                  break loop1;        // exit
               }

            } else {

               p1 = rs.getString("player1");
               p2 = rs.getString("player2");
               p3 = rs.getString("player3");
               p4 = rs.getString("player4");
               p5 = rs.getString("player5");

               if (found == true) {           // if this is the tee time after the one we are checking

                  if (!p1.equals( "" ) && p3.equals( "" ) && p4.equals( "" ) && p5.equals( "" )) {    // if this group is single or 2-some

                     error = true;       // set error
                  }
                  break loop1;        // exit - we're done
               }
            }

         }        // end if while (loop1)

         pstmt.close();

      } catch (Exception e) {

         Utilities.logError("Error checking for Hazeltine Groups - verifySlot.checkHazGrps: " + e.getMessage());        // log the error message

      } finally {

          try { rs.close(); }
          catch (Exception ignore) {}

          try { pstmt.close(); }
          catch (Exception ignore) {}

      }

   } // end if restricted

   return(error);

 } // end of checkHazGrps


/**
 //************************************************************************
 //
 //  checkNational  (for Hazeltine National only)
 //
 //     Check 'National' members - allowed 20 rounds per year (per membership)
 //
 //************************************************************************
 **/

 public static boolean checkNational(parmSlot slotParms, Connection con)
         throws Exception {


   int count1 = 0;      // number of rounds this year for each possible member number (family)
   int count2 = 0;
   int count3 = 0;
   int count4 = 0;
   int count5 = 0;
   int max = 20;         // max rounds per year

   String mship = "National";                // membership type to process

   String mNum1 = slotParms.mNum1;           // Member #'s for this tee time
   String mNum2 = slotParms.mNum2;
   String mNum3 = slotParms.mNum3;
   String mNum4 = slotParms.mNum4;
   String mNum5 = slotParms.mNum5;

   boolean error = false;

   //
   //   Check each player for mship = National
   //
   try {

      //
      //   Check Player 1
      //
      if (!mNum1.equals( "" ) && slotParms.mship1.equals( mship )) {   // if member and a National member

         //
         //  count the number of rounds this year for all members with this member number
         //
         count1 = getNatCounts(slotParms, mNum1, con);

         count1++;         // include this tee time request
      }

      //
      //   Check Player 2
      //     if member and a National member and not same as previous player
      //
      if (!mNum2.equals( "" ) && slotParms.mship2.equals( mship ) && !mNum2.equals( mNum1 )) {

         //
         //  count the number of rounds this year for all members with this member number
         //
         count2 = getNatCounts(slotParms, mNum2, con);

         count2++;         // include this tee time request
      }

      //
      //   Check Player 3
      //     if member and a National member and not same as previous player
      //
      if (!mNum3.equals( "" ) && slotParms.mship3.equals( mship ) && !mNum3.equals( mNum1 ) && !mNum3.equals( mNum2 )) {

         //
         //  Count the number of rounds this year for all members with this member number
         //
         count3 = getNatCounts(slotParms, mNum3, con);

         count3++;         // include this tee time request
      }

      //
      //   Check Player 4
      //     if member and a National member and not same as previous player
      //
      if (!mNum4.equals( "" ) && slotParms.mship4.equals( mship ) && !mNum4.equals( mNum1 ) &&
          !mNum4.equals( mNum2 ) && !mNum4.equals( mNum3 )) {

         //
         //  Count the number of rounds this year for all members with this member number
         //
         count4 = getNatCounts(slotParms, mNum4, con);

         count4++;         // include this tee time request
      }

      //
      //   Check Player 5
      //     if member and a National member and not same as previous player
      //
      if (!mNum5.equals( "" ) && slotParms.mship5.equals( mship ) && !mNum5.equals( mNum1 ) &&
          !mNum5.equals( mNum2 ) && !mNum5.equals( mNum3 ) && !mNum5.equals( mNum4 )) {

         //
         //  Count the number of rounds this year for all members with this member number
         //
         count5 = getNatCounts(slotParms, mNum5, con);

         count5++;         // add 1 for this member
      }
   }
   catch (Exception e) {

      String errorMsg = "Error checking for National Member - verifySlot.checkNational: " + e.getMessage();

      Utilities.logError(errorMsg);        // log the error message

      throw new Exception(errorMsg);
   }

   //
   //  Check counts to see if any member exceeds the max allowed rounds per year
   //
   if (count1 > max) {
      error = true;                            // indicate error
      slotParms.player = slotParms.player1;    // save player in error for reply
      slotParms.period = "year";               // set period for reply
      slotParms.mship = slotParms.mship1;
   } else {
      if (count2 > max) {
         error = true;                            // indicate error
         slotParms.player = slotParms.player2;    // save player in error for reply
         slotParms.period = "year";               // set period for reply
         slotParms.mship = slotParms.mship2;
      } else {
         if (count3 > max) {
            error = true;                            // indicate error
            slotParms.player = slotParms.player3;    // save player in error for reply
            slotParms.period = "year";               // set period for reply
            slotParms.mship = slotParms.mship3;
         } else {
            if (count4 > max) {
               error = true;                            // indicate error
               slotParms.player = slotParms.player4;    // save player in error for reply
               slotParms.period = "year";               // set period for reply
               slotParms.mship = slotParms.mship4;
            } else {
               if (count5 > max) {
                  error = true;                            // indicate error
                  slotParms.player = slotParms.player5;    // save player in error for reply
                  slotParms.period = "year";               // set period for reply
                  slotParms.mship = slotParms.mship5;
               }
            }
         }
      }
   }
   return(error);
 }              // end of checkNational


/**
 //************************************************************************
 //
 //  getNatCounts  (for Hazeltine National only)
 //
 //     Check 'National' members - allowed 20 tee times per year (per membership)
 //     Count each tee time that contains at least one of the membership.
 //
 //    called by:  checkNational (above)
 //************************************************************************
 **/

 public static int getNatCounts(parmSlot slotParms, String mNum1, Connection con)
         throws Exception {


    int count = 0;      // number of rounds this year for each possible member number (family)

    //
    //   Check Player
    //
    if (!mNum1.equals( "" )) {   // if member

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        int show1 = 0;
        int show2 = 0;
        int show3 = 0;
        int show4 = 0;
        int show5 = 0;
        int time = 0;
        int year = 0;

        long date1 = 0;
        long date2 = 0;
        long tdate = 0;

        String tmNum1 = "";
        String tmNum2 = "";
        String tmNum3 = "";
        String tmNum4 = "";
        String tmNum5 = "";

        Calendar cal = new GregorianCalendar();         // get todays date
        year = cal.get(Calendar.YEAR);                  // get the current year
        date1 = year * 10000;                           // create date of yyyy0000 for compares
        date2 = date1 + 1232;                            // end of year

        try {

             //
             //  count the number of tee times this year for all members with this member number
             //
             pstmt = con.prepareStatement (
                "SELECT date, time FROM teecurr2 " +
                "WHERE date > ? AND date < ? AND " +
                "(mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?)");

             pstmt.clearParameters();        // clear the parms and check player 1
             pstmt.setLong(1, date1);
             pstmt.setLong(2, date2);
             pstmt.setString(3, mNum1);
             pstmt.setString(4, mNum1);
             pstmt.setString(5, mNum1);
             pstmt.setString(6, mNum1);
             pstmt.setString(7, mNum1);
             rs = pstmt.executeQuery();      // execute the prepared stmt

             while (rs.next()) {

                tdate = rs.getLong("date");
                time = rs.getInt("time");

                //
                //  Make sure this is not this tee time before changes, if so do not count
                //
                if ((time != slotParms.time) || (tdate != slotParms.date )) {  // either time or date is diff

                   count++;        // count tee times for this membership
                }
             }
             pstmt.close();

             //
             //  Check teepast
             //
             pstmt = con.prepareStatement (
                "SELECT show1, show2, show3, show4, show5, mNum1, mNum2, mNum3, mNum4, mNum5 " +
                "FROM teepast2 " +
                "WHERE date > ? AND date < ? AND " +
                "(mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?)");

             pstmt.clearParameters();        // clear the parms and check player 1
             pstmt.setLong(1, date1);
             pstmt.setLong(2, date2);
             pstmt.setString(3, mNum1);
             pstmt.setString(4, mNum1);
             pstmt.setString(5, mNum1);
             pstmt.setString(6, mNum1);
             pstmt.setString(7, mNum1);
             rs = pstmt.executeQuery();      // execute the prepared stmt

             while (rs.next()) {

                show1 = rs.getInt("show1");
                show2 = rs.getInt("show2");
                show3 = rs.getInt("show3");
                show4 = rs.getInt("show4");
                show5 = rs.getInt("show5");
                tmNum1 = rs.getString("mNum1");
                tmNum2 = rs.getString("mNum2");
                tmNum3 = rs.getString("mNum3");
                tmNum4 = rs.getString("mNum4");
                tmNum5 = rs.getString("mNum5");

                if (tmNum1.equals( mNum1 ) && show1 == 1) {

                   count++;        // count rounds for this membership

                } else {

                   if (tmNum2.equals( mNum1 ) && show2 == 1) {

                      count++;        // count rounds for this membership

                   } else {

                      if (tmNum3.equals( mNum1 ) && show3 == 1) {

                         count++;        // count rounds for this membership

                      } else {

                         if (tmNum4.equals( mNum1 ) && show4 == 1) {

                            count++;        // count rounds for this membership

                         } else {

                            if (tmNum5.equals( mNum1 ) && show5 == 1) {

                               count++;        // count rounds for this membership
                            }
                         }
                      }
                   }
                }
             }

             pstmt.close();

        } catch (Exception e) {

            String errorMsg = "Error checking for National Member - verifySlot.getNatCounts: " + e.getMessage();

            Utilities.logError(errorMsg);        // log the error message

            throw new Exception(errorMsg);

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }

    } // end if mNum1 not empty

    return( count );

 } // end of getNatCounts


/**
 //************************************************************************
 //
 //  checkSponsGrp
 //
 //      Verify Sponsored Group Requests for Hazeltine National (Unaccompanied Guests)
 //
 //
 //      1. Max of 4 groups (total) allowed weekdays
 //
 //      2. Except on Junior Fridays (6/08/06 thru 7/29/06), only 2 groups allowed
 //
 //      3. Max of 2 groups (total) allowed weekend days and holidays (Mem Day, 5th of July, Labor Day)
 //
 //      4. No more than 2 groups per day per individual member
 //
 //
 //  ********** NOTE **********
 //
 //    Some dates, etc will have to be changed below annually to keep this functioning properly.
 //
 //
 //   Returns:  error = 0  all ok
 //                     1  max groups already scheduled for today
 //                     2  sponspored groups not allowed during this time of day (no longer used)
 //                     3  requesting member already has 2 sponsored groups scheduled
 //
 //************************************************************************
 **/

 public static int checkSponsGrp(parmSlot slotParms, Connection con)
         throws Exception {


   int error = 0;

   String sponsored = "Spons";          // guest type is for sponsored guest


   //
   //  Check if this is a sponsored group
   //
   if (slotParms.player1.startsWith(sponsored) || slotParms.player2.startsWith(sponsored) ||
       slotParms.player3.startsWith(sponsored) || slotParms.player4.startsWith(sponsored) ||
       slotParms.player5.startsWith(sponsored)) {        // if any guest type is sponsored type


       PreparedStatement pstmt = null;
       ResultSet rs = null;

       int fb2 = 0;

       //
       //  error codes
       //
       int one = 1;             // max groups already scheduled for the day
       //int two = 2;           // sponsored groups not allowed during this time of day
       int three = 3;           // Requesting member already has 2 sponsored groups sscheduled

       String empty = "";
       String course2 = "";
       String user = "";

       long date1 = Hdate1;     // Memorial Day                ****** Change these each Year (see above) *************
       long date2 = Hdate2;     // 4th of July - Monday
       long date3 = Hdate3;     // Labor Day
       //long date4 = Hdate4;   // October 1st
       //long date5 = Hdate5;   // Junior Fridays Start
       long date6 = Hdate6;     // Junior Fridays End

       //
       //  Holding area for existing Sponsored Groups - max of 4 per day
       //
       int count = 0;
       int timeG = 0;
       int timeG1 = 0;
       int timeG2 = 0;
       int timeG3 = 0;
       int timeG4 = 0;

       String p1 = "";
       String p2 = "";
       String p3 = "";
       String p4 = "";
       String p5 = "";

       String userg1G1 = "";
       String userg2G1 = "";
       String userg3G1 = "";
       String userg4G1 = "";
       String userg5G1 = "";
       String userg1G2 = "";
       String userg2G2 = "";
       String userg3G2 = "";
       String userg4G2 = "";
       String userg5G2 = "";
       String userg1G3 = "";
       String userg2G3 = "";
       String userg3G3 = "";
       String userg4G3 = "";
       String userg5G3 = "";
       String userg1G4 = "";
       String userg2G4 = "";
       String userg3G4 = "";
       String userg4G4 = "";
       String userg5G4 = "";

       //
       //  Get the date, time and day_name of this tee time
       //
       long date = slotParms.date;
       int time = slotParms.time;
       int fb = slotParms.fb;
       String day = slotParms.day;
       String course = slotParms.course;


      //
      //  Locate and count all Sponsored groups already scheduled for today
      //
      try {

         pstmt = con.prepareStatement (
            "SELECT time, player1, player2, player3, player4, fb, player5, courseName, userg1, userg2, userg3, userg4, userg5 " +
            "FROM teecurr2 " +
            "WHERE date = ? AND player1 != ? AND username1 = ? AND username2 = ? AND username3 = ? AND " +
            "username4 = ? AND username5 = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setLong(1, date);
         pstmt.setString(2, empty);
         pstmt.setString(3, empty);
         pstmt.setString(4, empty);
         pstmt.setString(5, empty);
         pstmt.setString(6, empty);
         pstmt.setString(7, empty);
         rs = pstmt.executeQuery();      // execute the prepared stmt

         while (rs.next() && count < 4) {

            timeG = rs.getInt("time");
            p1 = rs.getString("player1");
            p2 = rs.getString("player2");
            p3 = rs.getString("player3");
            p4 = rs.getString("player4");
            fb2 = rs.getInt("fb");
            p5 = rs.getString("player5");
            course2 = rs.getString("courseName");

            if ((timeG != time) || (fb2 != fb) || (!course2.equals( course ))) {  // if not this tee time

               if (p1.startsWith(sponsored) || p2.startsWith(sponsored) || p3.startsWith(sponsored) ||
                   p4.startsWith(sponsored) || p5.startsWith(sponsored)) {        // if guest type is sponsored type

                  count++;

                  if (count == 1) {

                     timeG1 = timeG;       // save values
                     userg1G1 = rs.getString("userg1");
                     userg2G1 = rs.getString("userg2");
                     userg3G1 = rs.getString("userg3");
                     userg4G1 = rs.getString("userg4");
                     userg5G1 = rs.getString("userg5");
                  }
                  if (count == 2) {

                     timeG2 = timeG;       // save values
                     userg1G2 = rs.getString("userg1");
                     userg2G2 = rs.getString("userg2");
                     userg3G2 = rs.getString("userg3");
                     userg4G2 = rs.getString("userg4");
                     userg5G2 = rs.getString("userg5");
                  }
                  if (count == 3) {

                     timeG3 = timeG;       // save values
                     userg1G3 = rs.getString("userg1");
                     userg2G3 = rs.getString("userg2");
                     userg3G3 = rs.getString("userg3");
                     userg4G3 = rs.getString("userg4");
                     userg5G3 = rs.getString("userg5");
                  }
                  if (count == 4) {

                     timeG4 = timeG;       // save values
                     userg1G4 = rs.getString("userg1");
                     userg2G4 = rs.getString("userg2");
                     userg3G4 = rs.getString("userg3");
                     userg4G4 = rs.getString("userg4");
                     userg5G4 = rs.getString("userg5");
                  }
               }
            }
         }
         pstmt.close();

         error = 0;

         //
         //  Now check for max allowed already scheduled and check the time
         //
         if (day.equals("Saturday") || day.equals("Sunday") || date == date1 || date == date2 || date == date3) {

            //
            //  Weekend or Holiday - no more than 2 groups allowed per day
            //
            if (count > 1) {

               error = one;        // reject request
            }

         } else {

            //
            //   Its a weekday - 4 groups allowed, except junior Fridays
            //
            if (count > 3) {

               error = one;     // reject request - already reached max

            } else {   // check for Junior Fridays

               if (day.equals( "Friday" ) && date < date6) {   // if before last Friday in Sept

                  if (count > 1) {      // max of 2

                     error = one;       // max reched
                  }
               }
            }
         }
         //
         //  See if it survived all that
         //
         if (error == 0) {

            //
            //  Now check if the member sponsoring this group has already sponsored 2 groups for this day
            //
            count = 0;     // init counter

            if (!slotParms.userg1.equals( "" )) {

               user = slotParms.userg1;         // get user requesting this group

            } else {

               if (!slotParms.userg2.equals( "" )) {

                  user = slotParms.userg2;         // get user requesting this group

               } else {

                  if (!slotParms.userg3.equals( "" )) {

                     user = slotParms.userg3;         // get user requesting this group

                  } else {

                     if (!slotParms.userg4.equals( "" )) {

                        user = slotParms.userg4;         // get user requesting this group

                     } else {

                        if (!slotParms.userg5.equals( "" )) {

                           user = slotParms.userg5;         // get user requesting this group
                        }
                     }
                  }
               }
            }
            if (!user.equals( "" )) {

               //
               //  Got a member - check for other groups sponsored by this member
               //
               if (user.equalsIgnoreCase(userg1G1) || user.equalsIgnoreCase(userg2G1) || user.equalsIgnoreCase(userg3G1) ||
                   user.equalsIgnoreCase(userg4G1) || user.equalsIgnoreCase(userg5G1)) {

                  count++;
               }
               if (user.equalsIgnoreCase(userg1G2) || user.equalsIgnoreCase(userg2G2) || user.equalsIgnoreCase(userg3G2) ||
                   user.equalsIgnoreCase(userg4G2) || user.equalsIgnoreCase(userg5G2)) {

                  count++;
               }
               if (user.equalsIgnoreCase(userg1G3) || user.equalsIgnoreCase(userg2G3) || user.equalsIgnoreCase(userg3G3) ||
                   user.equalsIgnoreCase(userg4G3) || user.equalsIgnoreCase(userg5G3)) {

                  count++;
               }
               if (user.equalsIgnoreCase(userg1G4) || user.equalsIgnoreCase(userg2G4) || user.equalsIgnoreCase(userg3G4) ||
                   user.equalsIgnoreCase(userg4G4) || user.equalsIgnoreCase(userg5G4)) {

                  count++;
               }
               if (count > 1) {

                  error = three;      // reject request - member laready has 2 sponsored groups scheduled
               }
            }
         }

      } catch (Exception e) {

         throw new Exception("Error checking for Sponsored Groups - verifySlot.checkSponsGrp " + e.getMessage());

      } finally {

          try { rs.close(); }
          catch (Exception ignore) {}

          try { pstmt.close(); }
          catch (Exception ignore) {}

      }

   } // end if sponsored guest

   return(error);

 } // end of checkSponsGrp


/**
 //************************************************************************
 //
 //  oakmontGuests - special processing for Oakmont CC.
 //
 //     At this point we know there is more than one guest
 //     in this tee time and it is Oakmont.
 //
 //     If Sat, Sun or a holiday, and after 1 PM, members can have no more
 //     than two groups per day with more than one family guest (total per day).
 //
 //************************************************************************
 **/

 public static boolean oakmontGuests(parmSlot slotParms, Connection con)
         throws Exception {


    boolean error = false;

    int holiday = 0;
    int guests = 0;

    long memDay = Hdate1;        // Memorial Day     !!!!!!!!!! Must keep current !!!!!!!!!!!!!!!!!!
    long july4th = Hdate2;       // 4th of July
    long laborDay = Hdate3;      // Labor Day


    if (slotParms.date == memDay || slotParms.date == july4th || slotParms.date == laborDay) {

        holiday = 1;
    }

    //
    //  Check if Sat, Sun or Holiday and after 1 PM
    //
    if (slotParms.time > 1300 && (slotParms.day.equals( "Saturday" ) || slotParms.day.equals( "Sunday" ) || holiday == 1)) {

     //
     //  Sat, Sun or Holiday - max of two tee times after 1 PM with 2 or more family guests
     //
     //  'Member, Guest, Member, Guest' is ok - if player 1 or player3 is any guest, then check for more
     //
     if (!slotParms.g1.equals( "" ) || !slotParms.g3.equals( "" )) {

        if (slotParms.g1.equalsIgnoreCase( "family guest" )) {

           guests++;
        }
        if (slotParms.g2.equalsIgnoreCase( "family guest" )) {

           guests++;
        }
        if (slotParms.g3.equalsIgnoreCase( "family guest" )) {

           guests++;
        }
        if (slotParms.g4.equalsIgnoreCase( "family guest" )) {

           guests++;
        }
        if (slotParms.g5.equalsIgnoreCase( "family guest" )) {

           guests++;
        }
     }

     error = false;

     if (guests > 1) {        // if more than one family guest in this tee time

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        int time = 0;
        int fb = 0;
        int gtimes = 0;              // number of tee times for this date with 2 or more guests
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

        try {

            time = 1300;          // 1 PM
            gtimes = 0;

            //
            //   Check all other tee times for this date for more than 1 family guest
            //
            pstmt = con.prepareStatement (
               "SELECT time, fb, player1, player2, player3, player4, player5, " +
               "userg1, userg2, userg3, userg4, userg5 " +
               "FROM teecurr2 " +
               "WHERE date = ? AND time > ?");

            pstmt.clearParameters();
            pstmt.setLong(1, slotParms.date);
            pstmt.setInt(2, time);
            rs = pstmt.executeQuery();

            while (rs.next()) {

               time = rs.getInt("time");
               fb = rs.getInt("fb");
               player1 = rs.getString("player1");
               player2 = rs.getString("player2");
               player3 = rs.getString("player3");
               player4 = rs.getString("player4");
               player5 = rs.getString("player5");
               userg1 = rs.getString("userg1");
               userg2 = rs.getString("userg2");
               userg3 = rs.getString("userg3");
               userg4 = rs.getString("userg4");
               userg5 = rs.getString("userg5");

               guests = 0;     // # of guests in this tee time

               if (time != slotParms.time || fb != slotParms.fb) {  // if not this tee time

                  //
                  //  'Member, Guest, Member, Guest' is ok - if player 1 or player3 is any guest, then check for more
                  //
                  if (!userg1.equals( "" ) || !userg3.equals( "" )) {

                     if (player1.startsWith( "Family Guest" )) {

                        guests++;     // bump # of guests
                     }
                     if (player2.startsWith( "Family Guest" )) {

                        guests++;     // bump # of guests
                     }
                     if (player3.startsWith( "Family Guest" )) {

                        guests++;     // bump # of guests
                     }
                     if (player4.startsWith( "Family Guest" )) {

                        guests++;     // bump # of guests
                     }
                     if (player5.startsWith( "Family Guest" )) {

                        guests++;     // bump # of guests
                     }

                     if (guests > 1) {

                        gtimes++;        // bump # of tee times with more than one family guest
                     }
                  }
               }

            } // end while

            pstmt.close();

            if (gtimes > 1) {          // if 2 tee times already on this date

               error = true;
            }

       } catch (Exception e) {

          throw new Exception("Error checking for Oakmont guests - verifySlot.oakmontGuests " + e.getMessage());

       } finally {

          try { rs.close(); }
          catch (Exception ignore) {}

          try { pstmt.close(); }
          catch (Exception ignore) {}

       }

     } // end more than one guests

   } // end if Sat, Sun or Holiday and after 1 PM


   return(error);
 }


/**
 //************************************************************************
 //
 //  oakmontGuestsWF - special processing for Oakmont CC.
 //
 //     If Wed or Fri, allow only 3 guests amd 1 member OR
 //                    2 guests and 2 members during the specified time.
 //     Exception:  On Fridays if there is a shotgun event, no restriction.
 //
 //************************************************************************
 **/

 public static boolean oakmontGuestsWF(parmSlot slotParms, Connection con)
         throws Exception {


  boolean error = false;

  if (slotParms.day.equals( "Wednesday" ) || slotParms.day.equals( "Friday" )) {  // if Wednesday or Friday

     //
     //  Must be 1 member and 3 guests, or 2 members and 2 guest, during specified times (see above)
     //
     if (slotParms.members > 2 || slotParms.members == 0 || slotParms.guests < 2 || (slotParms.guests == 2 && slotParms.members < 2)) {

       PreparedStatement pstmt = null;
       ResultSet rs = null;

       boolean check = true;

       int shotgun = 1;        // event_type = shotgun

       //
       //   Tee time arrays for Wed & Fri (times when multiple guests are allowed) - see also _sheet.
       //
       int wedcount = 19;    // 19 tee times on Wednesday
       int [] wedtimes = { 820, 830, 840, 850, 1010, 1020, 1030, 1040, 1050, 1100, 1110, 1120, 1130, 1140, 1150, 1430, 1440, 1450, 1500 };

       int fricount = 21;    // 21 tee times
       int [] fritimes = { 830, 840, 850, 900, 910, 920, 930, 940, 950, 1000, 1010, 1020, 1030, 1040, 1050, 1100, 1110, 1120, 1130, 1140, 1150 };

       try {

            if (slotParms.day.equals( "Friday" )) {       // if Friday

               check = true;

               //
               //  Check for shotgun event on this day
               //
               pstmt = con.prepareStatement (
                  "SELECT dd " +
                  "FROM teecurr2 " +
                  "WHERE date = ? AND event_type = ?");

               pstmt.clearParameters();        // clear the parms
               pstmt.setLong(1, slotParms.date);
               pstmt.setInt(2, shotgun);
               rs = pstmt.executeQuery();      // execute the prepared stmt

               if (rs.next()) {

                  check = false;      // skip the following checks
               }

               pstmt.close();

               if (check == true) {

                  //
                  //  Check the time of this tee time against the special times for this day
                  //
                  error = false;         // default to no error

                  for (int i = 0; i < fricount; i++) {

                     if (slotParms.time == fritimes[i]) {

                        error = true;       // tee time in error
                     }
                  }
               }

            } else {    // Wednesday - check for special dates

               //
               //  Check the time of this tee time against those allowed for this day
               //
               error = false;       // default to no error

               for (int i = 0; i < wedcount; i++) {

                  if (slotParms.time == wedtimes[i]) {

                     error = true;       // tee time is in error
                  }
               }
            }

       } catch (Exception e) {

          throw new Exception("Error checking for Oakmont guests - verifySlot.oakmontGuestsWF " + e.getMessage());

       } finally {

          try { rs.close(); }
          catch (Exception ignore) {}

          try { pstmt.close(); }
          catch (Exception ignore) {}

       }

     } // end slotParm member & guest checks

   } // end if Wednesday or Friday

   return(error);

 }


/**
 //************************************************************************
 //
 //  checkNSGuestTimes - special processing for North Shore CC.
 //
 //    On certain days, groups must include at least 2 guests.
 //
 //************************************************************************
 **/

 public static boolean checkNSGuestTimes(parmSlot slotParms, Connection con)
         throws Exception {


   boolean error = false;

   long excd1 = 20050624;       // Exception Dates     !!!!!!!!!! Must keep current !!!!!!!!!!!!!!!!!!
   long excd2 = 20050701;
   long excd3 = 20050715;
   long excd4 = 20050722;
   long excd5 = 20050729;

   //
   //   Tee time arrays for Fridays (times when multiple guests are required)
   //
   int [] fritimes = { 800, 830, 900, 930, 1000, 1030, 1100, 1130, 1200, 1230, 1300, 1330, 1400, 1430 };
   int friCount = 14;

   //
   //  Exception Days
   //
   int [] exctimes = { 1130, 1200, 1300, 1330, 1400, 1430 };
   int excCount = 6;

   long date = slotParms.date;

   int time = slotParms.time;

   int guests = 0;
   int i = 0;

   String day = slotParms.day;

   try {

      if (day.equals( "Wednesday" ) || day.equals( "Thursday" )) {  // if Wednesday or Thursday

         //
         //  Check the time
         //
         if (time == 1230 || time == 1300 || time == 1330 || time == 1400) {

            //
            //  Make sure there at least 2 guests
            //
            if (!slotParms.g1.equals( "" )) {

               guests++;           // count guests
            }
            if (!slotParms.g2.equals( "" )) {

               guests++;           // count guests
            }
            if (!slotParms.g3.equals( "" )) {

               guests++;           // count guests
            }
            if (!slotParms.g4.equals( "" )) {

               guests++;           // count guests
            }
            if (!slotParms.g5.equals( "" )) {

               guests++;           // count guests
            }

            if (guests < 2) {       // if not at least 2 guests

               error = true;
            }
         }

      } else {              // not Wed or Thurs

         if (day.equals( "Friday" )) {               // if Friday

            if (date != excd1 && date != excd2 && date != excd3 && date != excd4 && date != excd5) {

               //
               //  Check the time
               //
               for (i=0; i<friCount; i++) {

                  if (time == fritimes[i]) {

                     //
                     //  Make sure there at least 2 guests
                     //
                     if (!slotParms.g1.equals( "" )) {

                        guests++;           // count guests
                     }
                     if (!slotParms.g2.equals( "" )) {

                        guests++;           // count guests
                     }
                     if (!slotParms.g3.equals( "" )) {

                        guests++;           // count guests
                     }
                     if (!slotParms.g4.equals( "" )) {

                        guests++;           // count guests
                     }
                     if (!slotParms.g5.equals( "" )) {

                        guests++;           // count guests
                     }

                     if (guests < 2) {       // if not at least 2 guests

                        error = true;
                     }
                  }
               }

            } else {          // check times for Exception dates

               //
               //  Check the time
               //
               for (i=0; i<excCount; i++) {

                  if (time == exctimes[i]) {

                     //
                     //  Make sure there at least 2 guests
                     //
                     if (!slotParms.g1.equals( "" )) {

                        guests++;           // count guests
                     }
                     if (!slotParms.g2.equals( "" )) {

                        guests++;           // count guests
                     }
                     if (!slotParms.g3.equals( "" )) {

                        guests++;           // count guests
                     }
                     if (!slotParms.g4.equals( "" )) {

                        guests++;           // count guests
                     }
                     if (!slotParms.g5.equals( "" )) {

                        guests++;           // count guests
                     }

                     if (guests < 2) {       // if not at least 2 guests

                        error = true;
                     }
                  }
               }
            }

         }      // end of IF Friday
      }

   } catch (Exception e) {

      throw new Exception("Error checking for North Shore guest times - verifySlot.checkNSGuestTimes " + e.getMessage());

   }

   return(error);

 }


/**
 //************************************************************************
 //
 //  checkBelleHaven - checks members for exceeding the max number of tee
 //                    times on w/e's or holidays per year.
 //
 //************************************************************************
 **/

 public static boolean checkBelleHaven(parmSlot slotParms, Connection con)
         throws Exception {


   boolean error = false;

   long date = slotParms.date;    // get date of this request
   long date1 = Hdate1;           // Memorial Day         (must change each year!!)
   long date2 = Hdate2;           // Monday near July 4th
   long date3 = Hdate3;           // Labor Day
   long date4 = Hdate7;           // Thanksgiving Day

   //
   //   Only check if this is a w/e or holiday
   //
   if (slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday") ||
       date == date1 || date == date2 || date == date3 || date == date4) {

     PreparedStatement pstmt1 = null;
     ResultSet rs = null;

     try {

         int year = 0;
         int count = 0;
         int i = 0;
         long datet = 0;

         String player = "";
         String mship = "";
         String user = "";

         String [] mshipA = new String [5];     // array to hold the players' membership types
         String [] playerA = new String [5];    // array to hold the players' names
         String [] userA = new String [5];      // array to hold the user names
         String [] oldplayerA = new String [5];    // array to hold the old players' names

         //
         //  put player info in arrays for processing below
         //
         mshipA[0] = slotParms.mship1;
         mshipA[1] = slotParms.mship2;
         mshipA[2] = slotParms.mship3;
         mshipA[3] = slotParms.mship4;
         mshipA[4] = slotParms.mship5;
         playerA[0] = slotParms.player1;
         playerA[1] = slotParms.player2;
         playerA[2] = slotParms.player3;
         playerA[3] = slotParms.player4;
         playerA[4] = slotParms.player5;
         userA[0] = slotParms.user1;
         userA[1] = slotParms.user2;
         userA[2] = slotParms.user3;
         userA[3] = slotParms.user4;
         userA[4] = slotParms.user5;
         oldplayerA[0] = slotParms.oldPlayer1;
         oldplayerA[1] = slotParms.oldPlayer2;
         oldplayerA[2] = slotParms.oldPlayer3;
         oldplayerA[3] = slotParms.oldPlayer4;
         oldplayerA[4] = slotParms.oldPlayer5;

         //
         //  Get the tee time's date values
         //
         year = slotParms.yy;                     // get year

         //
         //  Check each player that is an 'Elective' member
         //
         loop1:
         for (i = 0; i < 5; i++) {           // do each player

            mship = mshipA[i];
            player = playerA[i];
            user = userA[i];

            if (mship.equals( "Elective" ) && !user.equals( "" )) {

               //
               //  ok if player was already on tee time (already approved by pro)
               //
               if (!player.equals( oldplayerA[0] ) && !player.equals( oldplayerA[1] ) &&
                   !player.equals( oldplayerA[2] ) && !player.equals( oldplayerA[3] ) &&
                   !player.equals( oldplayerA[4] )) {

                  count = 0;

                  //
                  //  Add custom checks for Belle Haven
                  //
                  //     Count # of w/e and holiday tee times during the calendar year
                  //
                  pstmt1 = con.prepareStatement (
                     "SELECT dd " +
                     "FROM teepast2 WHERE (date = ? OR date = ? OR date = ? OR date = ? OR " +
                     "day = 'Saturday' OR day = 'Sunday') AND yy = ? AND " +
                     "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)");

                  pstmt1.clearParameters();        // clear the parms and check player 1
                  pstmt1.setLong(1, date1);
                  pstmt1.setLong(2, date2);
                  pstmt1.setLong(3, date3);
                  pstmt1.setLong(4, date4);
                  pstmt1.setInt(5, year);          // this year
                  pstmt1.setString(6, user);
                  pstmt1.setString(7, user);
                  pstmt1.setString(8, user);
                  pstmt1.setString(9, user);
                  pstmt1.setString(10, user);
                  rs = pstmt1.executeQuery();      // execute the prepared stmt

                  while (rs.next()) {

                     count++;
                  }
                  pstmt1.close();

                  pstmt1 = con.prepareStatement (
                     "SELECT date " +
                     "FROM teecurr2 WHERE (date = ? OR date = ? OR date = ? OR date = ? OR " +
                     "day = 'Saturday' OR day = 'Sunday') AND yy = ? AND " +
                     "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)");

                  pstmt1.clearParameters();        // clear the parms and check player 1
                  pstmt1.setLong(1, date1);
                  pstmt1.setLong(2, date2);
                  pstmt1.setLong(3, date3);
                  pstmt1.setLong(4, date4);
                  pstmt1.setInt(5, year);          // this year
                  pstmt1.setString(6, user);
                  pstmt1.setString(7, user);
                  pstmt1.setString(8, user);
                  pstmt1.setString(9, user);
                  pstmt1.setString(10, user);
                  rs = pstmt1.executeQuery();      // execute the prepared stmt

                  while (rs.next()) {

                     datet = rs.getLong(1);

                     if (datet != date) {        // if not this date
                        count++;
                     }
                  }
                  pstmt1.close();

                  if (count > 9) {        // if already had 10 tee times

                     error = true;
                  }
               }

               if (error == true) {
                  slotParms.player = player;
                  break loop1;                  // exit loop
               }

            } // end of IF mship

         } // end of FOR loop

       } catch (Exception e) {

          throw new Exception("Error checking Belle Haven - verifySlot.checkBelleHaven " + e.getMessage());

       } finally {

          try { rs.close(); }
          catch (Exception ignore) {}

          try { pstmt1.close(); }
          catch (Exception ignore) {}

       }

   } // end of IF w/e or holiday

   return( error );

 }


/**
 //************************************************************************
 //
 //  checkGuestNames - checks for guest names - if required for this club
 //
 //  called by:  above and Member_slotm and Member_lott
 //
 //************************************************************************
 **/

 public static boolean checkGstName(String name, String gType, String club) {


   boolean error = false;

   int count = 0;
   int count2 = 0;
   int minimum = 2;        // # of words in name

   if (club.equals("medinahcc")) {

      minimum = 1;          // they only need one name
   }

   /*
   if ( (club.equals( "cherryhills" ) && name.startsWith( "X TBA lottery use" )) ||
        (club.equals( "denvercc" ) && name.startsWith( "Lottery TBD" )) ||
        (club.equals( "mnvalleycc" ) && name.startsWith( "5th Player" )) ||
        (club.equals( "brantford" ) && name.startsWith( "Lottery TBA" )) ) {
    */

   if ( name.startsWith( "X TBA Lott" ) ||
        name.startsWith( "Lottery TBD" ) ||
        name.startsWith( "Lottery TBA" ) ||
        (club.equals( "northhills" ) && name.startsWith( "NO FIVESOME" )) ||
        (club.equals( "mnvalleycc" ) && name.startsWith( "5th Player" )) ) {

      error = false;             // skip this one

   } else {

      if (name.equals( gType )) {   // if matches then can't be name

         error = true;

      } else {

         //
         //  Use tokens to determine the number of words in each string.
         //  There must be at least 2 extra words in the player name.
         //
         StringTokenizer tok = new StringTokenizer( name, " " );          // delimiter is a space
         count = tok.countTokens();                                       // number of words in player name

         StringTokenizer tok2 = new StringTokenizer( gType, " " );        // guest type
         count2 = tok2.countTokens();                                     // number of words in guest type

         if (count > count2) {

            if (!club.equals( "mirasolcc" )) {           // Mirasol only needs one name field (anything is ok as long is there is something)

               count = count - count2;          // how many more words in player name than guest type

               if (count < minimum) {                 // must be at least 2

                  error = true;
               }
            }

         } else {           // error

            error = true;
         }
      }
   }

   return(error);
 }


 public static boolean checkTrackedGuestName(String name, int guest_id, String gType, String club, Connection con) {

     PreparedStatement pstmt = null;
     ResultSet rs = null;

     boolean error = false;

     name = name.trim();

     if (name.equals(gType)) {
         error = true;
     } else {

         try {
             pstmt = con.prepareStatement(
                     "SELECT CONCAT(name_first, ' ', IF(name_mi != '', CONCAT(name_mi, ' '), ''), name_last) as guest_name " +
                     "FROM guestdb_data " +
                     "WHERE guest_id = ?");
             pstmt.clearParameters();
             pstmt.setInt(1, guest_id);

             rs = pstmt.executeQuery();

             if (rs.next()) {
                 if (!name.equals(gType + " " + rs.getString("guest_name"))) {
                     error = true;
                 }
             }

             pstmt.close();

         } catch (Exception exc) {
             error = true;
         }
     }

     return error;
 }


/**
 //************************************************************************
 //
 //  checkTFlag - Check for tflag in member2b and mship5 for players passed.
 //
 //    Called by:  Member_slot(m) & Proshop_slot(m) and others.
 //
 //************************************************************************
 **/

 public static void checkTFlag(parmSlot slotParms, Connection con) {

    //
    //  init the flags in case player has changed
    //
    slotParms.tflag1 = "";
    slotParms.tflag2 = "";
    slotParms.tflag3 = "";
    slotParms.tflag4 = "";
    slotParms.tflag5 = "";


    //
    //  If player is a member, then check for any associated tags (tflags) to be shown on pro tee sheet
    //
    if (!slotParms.user1.equals("")) {

       slotParms.tflag1 = getTflag(slotParms.user1, con);
    }

    if (!slotParms.user2.equals("")) {

       slotParms.tflag2 = getTflag(slotParms.user2, con);
    }

    if (!slotParms.user3.equals("")) {

       slotParms.tflag3 = getTflag(slotParms.user3, con);
    }

    if (!slotParms.user4.equals("")) {

       slotParms.tflag4 = getTflag(slotParms.user4, con);
    }

    if (!slotParms.user5.equals("")) {

       slotParms.tflag5 = getTflag(slotParms.user5, con);
    }

 }              // end of checkTFlag


   //   used by checkTFlag above
 private static String getTflag(String user, Connection con) {


    String tflag = "";

    if (!user.equals("")) {

       ResultSet rs = null;
       PreparedStatement pstmt1 = null;

       String tflag2 = "";
       String mship = "";

       try {

             //
             //  Get this users tflag if provided
             //
             pstmt1 = con.prepareStatement (
                "SELECT m_ship, tflag " +
                "FROM member2b " +
                "WHERE username = ?");

             pstmt1.clearParameters();
             pstmt1.setString(1, user);
             rs = pstmt1.executeQuery();

             if (rs.next()) {

                mship = rs.getString("m_ship");
                tflag = rs.getString("tflag");
             }
             pstmt1.close();

             if (!mship.equals( "" )) {               // if mship provided

                //
                //  Get the tflag for this mship type if specified
                //
                pstmt1 = con.prepareStatement (
                   "SELECT tflag " +
                   "FROM mship5 " +
                   "WHERE mship = ?");

                pstmt1.clearParameters();
                pstmt1.setString(1, mship);

                rs = pstmt1.executeQuery();

                if (rs.next()) {

                   tflag2 = rs.getString("tflag");
                }
                pstmt1.close();
             }

             //
             //  Put the 2 possible tags into one field for the tee sheet
             //
             if (!tflag.equals("")) {

                if (!tflag2.equals("")) {

                   tflag = tflag + " " + tflag2;    // combine with space seperator
                }

             } else {

                tflag = tflag2;          // just use mship flag if present
             }


       } catch (Exception e) {

          Utilities.logError("Error getting tflag data for member - verifySlot.getTflag: " + e.getMessage());

       } finally {

          try { rs.close(); }
          catch (Exception ignore) {}

          try { pstmt1.close(); }
          catch (Exception ignore) {}

       }

   }        // end of IF user

   return(tflag);

 }



 // *********************************************************
 //  Westchester CC - check for 2-some restricted times
 // *********************************************************

 public static int checkWestPlayers(parmSlot slotParms) {


   int westPlayers = 0;
   long date = slotParms.date;

   boolean check = false;


   //
   //  Custom check for Westchester - Sat & Sun & Holidays before 2 PM - no singles or 2-somes!!
   //
   if ((slotParms.day.equalsIgnoreCase( "saturday" ) || slotParms.day.equalsIgnoreCase( "sunday" )) &&
        slotParms.time < 1400) {

      check = true;

   } else {       // if MemDay, 4th of July, Labor Day or Columbus Day

      if ((date == Hdate1 || date == Hdate2 || date == Hdate3 || date == Hdate8) && slotParms.time < 1400) {

         check = true;
      }
   }

   if (check == true) {

      //
      //  Make sure at least 3 players
      //
      if (!slotParms.player1.equals( "" ) && !slotParms.player1.equalsIgnoreCase( "x" )) {  // if member or guest

         westPlayers++;
      }
      if (!slotParms.player2.equals( "" ) && !slotParms.player2.equalsIgnoreCase( "x" )) {  // if member or guest

         westPlayers++;
      }
      if (!slotParms.player3.equals( "" ) && !slotParms.player3.equalsIgnoreCase( "x" )) {  // if member or guest

         westPlayers++;
      }
      if (!slotParms.player4.equals( "" ) && !slotParms.player4.equalsIgnoreCase( "x" )) {  // if member or guest

         westPlayers++;
      }
      if (!slotParms.player5.equals( "" ) && !slotParms.player5.equalsIgnoreCase( "x" )) {  // if member or guest

         westPlayers++;
      }
   }

   return(westPlayers);         // return number of players or zero
 }


 // *********************************************************
 //  The Stanwich Club - check for dependents w/o an adult
 //
 //    Dependents = mtypes of 'Dependent' and 'Certified Dependent'
 //
 //    Restrictions:
 //
 //          Tues, Wed & Thur - all day - must be accompanied by an adult
 //          Fri, Sat, & Sun - after 1:30 - Dependent must be accompanied by adult
 //          Fri, Sat, & Sun - before 1:30 - Certified Dependent must be accompanied by adult
 //
 // *********************************************************

 public static boolean checkStanwichDependents(parmSlot slotParms) {


   int time = slotParms.time;

   boolean error = false;


   //
   //  Custom check for Stanwich - Tues, Wed, Thur - all dependents must be with an adult
   //
   if (slotParms.day.equals( "Tuesday" ) || slotParms.day.equals( "Wednesday" ) || slotParms.day.equals( "Thursday" )) {

      //
      //  Check for any dependents (Dependent and Certified Dependent)
      //
      if (slotParms.mtype1.endsWith( "ependent" ) || slotParms.mtype2.endsWith( "ependent" ) || slotParms.mtype3.endsWith( "ependent" ) ||
          slotParms.mtype4.endsWith( "ependent" ) || slotParms.mtype5.endsWith( "ependent" )) {

         error = true;     // default to error
      }
   }

   if (error == false) {

      //
      //  Check Friday, Saturday or Sunday
      //
      if (slotParms.day.equals( "Friday" ) || slotParms.day.equals( "Saturday" ) || slotParms.day.equals( "Sunday" )) {

         if (time < 1330) {          // if before 1:30 PM (there is no 1:30 time)

            //
            //  Check for any Certified Dependents
            //
            if (slotParms.mtype1.equals( "Certified Dependent" ) || slotParms.mtype2.equals( "Certified Dependent" ) ||
                slotParms.mtype3.equals( "Certified Dependent" ) || slotParms.mtype4.equals( "Certified Dependent" ) ||
                slotParms.mtype5.equals( "Certified Dependent" )) {

               error = true;     // default to error
            }

         } else {      // After 1:30 Dependents must be accompanied

            //
            //  Check for any Dependents
            //
            if (slotParms.mtype1.equals( "Dependent" ) || slotParms.mtype2.equals( "Dependent" ) ||
                slotParms.mtype3.equals( "Dependent" ) || slotParms.mtype4.equals( "Dependent" ) ||
                slotParms.mtype5.equals( "Dependent" )) {

               error = true;     // default to error
            }
         }
      }
   }

   if (error == true) {      // if dependent is in request

      //
      //  Make sure at least 1 adult
      //
      if ((!slotParms.mtype1.equals( "" ) && !slotParms.mtype1.endsWith( "ependent" )) ||
          (!slotParms.mtype2.equals( "" ) && !slotParms.mtype2.endsWith( "ependent" )) ||
          (!slotParms.mtype3.equals( "" ) && !slotParms.mtype3.endsWith( "ependent" )) ||
          (!slotParms.mtype4.equals( "" ) && !slotParms.mtype4.endsWith( "ependent" )) ||
          (!slotParms.mtype5.equals( "" ) && !slotParms.mtype5.endsWith( "ependent" ))) {   // if any adults

         error = false;     // with an adult - ok
      }
   }

   return(error);
 }


 // *********************************************************
 //  CC at Castle Pines - check for Juniors w/o an adult
 //
 //    Dependents = mtypes of 'Junior Male' and 'Junior Female'
 //
 //    Restrictions:  all day, every day
 //
 // *********************************************************

 public static boolean checkCastleDependents(parmSlot slotParms) {


   boolean error = false;


   //
   //  Check for any dependents
   //
   if (slotParms.mtype1.startsWith( "Junior" ) || slotParms.mtype2.startsWith( "Junior" ) || slotParms.mtype3.startsWith( "Junior" ) ||
       slotParms.mtype4.startsWith( "Junior" ) || slotParms.mtype5.startsWith( "Junior" )) {

      //
      //  Make sure at least 1 adult
      //
      if (!slotParms.mtype1.startsWith( "Adult" ) &&
          !slotParms.mtype2.startsWith( "Adult" ) &&
          !slotParms.mtype3.startsWith( "Adult" ) &&
          !slotParms.mtype4.startsWith( "Adult" ) &&
          !slotParms.mtype5.startsWith( "Adult" )) {       // if no adults

         error = true;           // no adult - error
      }
   }

   return(error);
 }


 // *********************************************************
 //  Westchester CC - check for dependents w/o an adult
 // *********************************************************

 public static boolean checkWestDependents(parmSlot slotParms) {


   long date = slotParms.date;

   boolean error = false;
   boolean check = false;


   //
   //  Custom check for Westchester - Sat & Sun & Holidays between 10:30 and 1:45 - kids must be with an adult
   //
   if ((slotParms.day.equalsIgnoreCase( "saturday" ) || slotParms.day.equalsIgnoreCase( "sunday" )) &&
       (slotParms.time > 1030 && slotParms.time < 1345)) {

      check = true;

   } else {       // if MemDay, 4th of July, Labor Day or Columbus Day

      if ((date == Hdate1 || date == Hdate2 || date == Hdate3 || date == Hdate8) &&
          (slotParms.time > 1030 && slotParms.time < 1345)) {

         check = true;
      }
   }

   if (check == true) {       // if restricted day and time

      //
      //  Check for any dependents
      //
      if (slotParms.mtype1.equals( "Dependent" ) || slotParms.mtype2.equals( "Dependent" ) || slotParms.mtype3.equals( "Dependent" ) ||
          slotParms.mtype4.equals( "Dependent" ) || slotParms.mtype5.equals( "Dependent" )) {

         error = true;     // default to error
      }
   }

   if (error == true) {      // if dependent is in request

      //
      //  Make sure at least 1 adult
      //
      if (!slotParms.mtype1.equals( "" ) && !slotParms.mtype1.equals( "Dependent" )) {

         error = false;       // change to OK
      }
      if (!slotParms.mtype2.equals( "" ) && !slotParms.mtype2.equals( "Dependent" )) {

         error = false;       // change to OK
      }
      if (!slotParms.mtype3.equals( "" ) && !slotParms.mtype3.equals( "Dependent" )) {

         error = false;       // change to OK
      }
      if (!slotParms.mtype4.equals( "" ) && !slotParms.mtype4.equals( "Dependent" )) {

         error = false;       // change to OK
      }
      if (!slotParms.mtype5.equals( "" ) && !slotParms.mtype5.equals( "Dependent" )) {

         error = false;       // change to OK
      }
   }

   return(error);
 }


 // *************************************************************************************
 // Westchester CC - Custom Processing (check date and time of day for 2-some only time)
 // *************************************************************************************

 public static boolean checkWestchester(long date, int time, String day_name) {


   boolean status = false;
   boolean WCfiveMinDay = false;

   //
   //  Determine date values - month, day, year
   //
   long year = date / 10000;
   long month = (date - (year * 10000)) / 100;
   long day = date - ((year * 10000) + (month * 100));
   long mmdd = (month * 100) + day;                        // create mmdd value

   //
   //      ************* See also SystemUtils ********************
   //
   //   2-some ONLY times from 7:40 AM to 10:30 AM for the following dates:
   //
   //      Every Wed from 5/09 - 10/19, except 7/25 & 9/05
   //
   //      Every Thurs from 5/10 - 10/19, except 7/12 & 9/06 (2007)
   //
   if (date != 20070607) {       // skip this day

     if (mmdd > 503 && mmdd < 1020 && (day_name.equals( "Wednesday" ) || day_name.equals( "Thursday" ))) {

        WCfiveMinDay = true;

        if (day_name.equals( "Wednesday" ) && ((mmdd > 718 && mmdd < 726) || (mmdd > 900 && mmdd < 906))) {  // skip these Wed's

           WCfiveMinDay = false;
        }

        if (day_name.equals( "Thursday" ) && ((mmdd > 705 && mmdd < 713) || (mmdd > 900 && mmdd < 907))) {  // skip these Thur's

           WCfiveMinDay = false;
        }

        if (WCfiveMinDay == true) {

           if (time > 739 && time < 1031) {         // if tee time is between 7:40 and 10:30 AM

              status = true;                        // 2-some only time
           }
        }
     }
  }

   return(status);         // true = 2-somes only time
 }


 // *********************************************************
 //  Pine Hills CC - check for Juniors w/o an adult
 // *********************************************************

 public static boolean checkPineDependents(parmSlot slotParms) {


   String jru14 = "Junior under 14";
   String jro14 = "Junior over 14";

   boolean error = false;
   boolean under = false;
   boolean over = false;


   //
   //  Check for any Juniors Under 14
   //
   if (slotParms.mtype1.equals( jru14 ) || slotParms.mtype2.equals( jru14 ) || slotParms.mtype3.equals( jru14 ) ||
       slotParms.mtype4.equals( jru14 ) || slotParms.mtype5.equals( jru14 )) {

      under = true;     // found

   } else {     // check for Juniors Over 14

      if (slotParms.mtype1.equals( jro14 ) || slotParms.mtype2.equals( jro14 ) || slotParms.mtype3.equals( jro14 ) ||
          slotParms.mtype4.equals( jro14 ) || slotParms.mtype5.equals( jro14 )) {

         over = true;     // found
      }
   }

   if (under == true) {      // if Juniors Under 14 found

      error = true;          // default to error

      //
      //  Make sure at least 1 adult - all days and times
      //
      if (!slotParms.mtype1.equals( "" ) && !slotParms.mtype1.startsWith( "Junior" )) {

         error = false;       // change to OK
      }
      if (!slotParms.mtype2.equals( "" ) && !slotParms.mtype2.startsWith( "Junior" )) {

         error = false;       // change to OK
      }
      if (!slotParms.mtype3.equals( "" ) && !slotParms.mtype3.startsWith( "Junior" )) {

         error = false;       // change to OK
      }
      if (!slotParms.mtype4.equals( "" ) && !slotParms.mtype4.startsWith( "Junior" )) {

         error = false;       // change to OK
      }
      if (!slotParms.mtype5.equals( "" ) && !slotParms.mtype5.startsWith( "Junior" )) {

         error = false;       // change to OK
      }

   } else {

      if (over == true) {      // if Juniors Over 14 found

         over = false;          // reset flag and check day and time

         //
         //  Restricted times are dependent on the day of the week
         //
         if (slotParms.day.equalsIgnoreCase( "saturday" ) &&
             (slotParms.time > 959 && slotParms.time < 1330)) {    // if Saturday and 10:00 AM - 1:29 PM

            over = true;

         } else {

            if (slotParms.day.equalsIgnoreCase( "sunday" ) &&
                (slotParms.time < 1329)) {                          // if Sunday and before 1:29 PM

               over = true;

            } else {

               if (slotParms.day.equalsIgnoreCase( "tuesday" ) &&
                   (slotParms.time > 1059 && slotParms.time < 1330)) {    // if Tues and 11:00 AM - 1:29 PM

                  over = true;

               } else {

                  if (slotParms.day.equalsIgnoreCase( "wednesday" ) &&
                      (slotParms.time > 1159 && slotParms.time < 1330)) {  // if Wed and 12:00 PM - 1:29 PM

                     over = true;
                  }
               }
            }
         }
      }

      if (over == true) {      // if Junior is in request and restricted time found

         error = true;          // default to error

         //
         //  Make sure at least 1 adult
         //
         if (!slotParms.mtype1.equals( "" ) && !slotParms.mtype1.startsWith( "Junior" )) {

            error = false;       // change to OK
         }
         if (!slotParms.mtype2.equals( "" ) && !slotParms.mtype2.startsWith( "Junior" )) {

            error = false;       // change to OK
         }
         if (!slotParms.mtype3.equals( "" ) && !slotParms.mtype3.startsWith( "Junior" )) {

            error = false;       // change to OK
         }
         if (!slotParms.mtype4.equals( "" ) && !slotParms.mtype4.startsWith( "Junior" )) {

            error = false;       // change to OK
         }
         if (!slotParms.mtype5.equals( "" ) && !slotParms.mtype5.startsWith( "Junior" )) {

            error = false;       // change to OK
         }
      }
   }

   return(error);
 }


 // *********************************************************
 //  Cherry Hills CC - custom member restrictions
 // *********************************************************

 public static boolean checkCherryHills(parmSlot slotParms) {


   long sdate = 414;
   long edate = 931;

   long date = (slotParms.mm * 100) + slotParms.dd;       // isolate month and day (mmdd)

   boolean error = false;

   boolean isSpouse = false;

   if (slotParms.mtype1.endsWith("pouse") || slotParms.mtype2.endsWith("pouse") || slotParms.mtype3.endsWith("pouse") ||
       slotParms.mtype4.endsWith("pouse") || slotParms.mtype5.endsWith("pouse")) {

       isSpouse = true;
   }

   Calendar cal = new GregorianCalendar();                  // get todays date
   int day = cal.get(Calendar.DAY_OF_WEEK);
   int hr = cal.get(Calendar.HOUR_OF_DAY);
   int min = cal.get(Calendar.MINUTE);

   int curtime = (hr * 100) + min;

//         String errorMsg = "TEST verifySlot.checkCherryHills: date=" +date+ ", day=" +slotParms.day+ ", time=" +slotParms.time+ ", ind=" +slotParms.ind;
//         logError(errorMsg);        // log the error message


   //
   //  Process according to the day of the week or holiday
   //
   //   'Members' are the primary members - can be male or female
   //   'Spouses' are the spouses - can be male or female
   //
   if (isSpouse && slotParms.day.equals("Monday") && ((day != 1 && day != 2) || (day == 1 && curtime < 830))) {

       error = true;

   } else if (slotParms.day.equals( "Saturday" ) || slotParms.date == Hdate1 || slotParms.date == Hdate2 ||
       slotParms.date == Hdate3) {

      //
      //  Saturday or Holiday (Memorial Day, 4th of july, Labor Day)
      //
      //  if < 11:00 AM - Members only (Exception:  Special Resident mships - 1 day in adv only from 4/15 - 9/30)
      //
      //  if after 11 AM and more than 1 day in adv, at least one member must be in the request
      //
      if ((slotParms.time < 1100 && date > sdate && date < edate)) {

         if (isSpouse) {

            error = true;          // error if Spouse included

         } else {

            //
            //  Check for more than 1 day in adv and Special Resident members
            //
            //    If Special Resident mship type, then must be accompanied by a Regular Member
            //
            if (slotParms.ind > 1) {

               if (slotParms.mship1.startsWith( "Special Resident" ) || slotParms.mship2.startsWith( "Special Resident" ) ||
                   slotParms.mship3.startsWith( "Special Resident" ) || slotParms.mship4.startsWith( "Special Resident" ) ||
                   slotParms.mship5.startsWith( "Special Resident" )) {

                  error = true;          // default to error

                  if (slotParms.mship1.equals( "Resident" ) || slotParms.mship1.equals( "Senior" ) ||
                      slotParms.mship1.equals( "Honorary Life" ) || slotParms.mship1.equals( "Clergy" ) ||
                      slotParms.mship1.equals( "Life Member" ) ||
                      slotParms.mship2.equals( "Resident" ) || slotParms.mship2.equals( "Senior" ) ||
                      slotParms.mship2.equals( "Honorary Life" ) || slotParms.mship2.equals( "Clergy" ) ||
                      slotParms.mship2.equals( "Life Member" ) ||
                      slotParms.mship3.equals( "Resident" ) || slotParms.mship3.equals( "Senior" ) ||
                      slotParms.mship3.equals( "Honorary Life" ) || slotParms.mship3.equals( "Clergy" ) ||
                      slotParms.mship3.equals( "Life Member" ) ||
                      slotParms.mship4.equals( "Resident" ) || slotParms.mship4.equals( "Senior" ) ||
                      slotParms.mship4.equals( "Honorary Life" ) || slotParms.mship4.equals( "Clergy" ) ||
                      slotParms.mship4.equals( "Life Member" ) ||
                      slotParms.mship5.equals( "Resident" ) || slotParms.mship5.equals( "Senior" ) ||
                      slotParms.mship5.equals( "Honorary Life" ) || slotParms.mship5.equals( "Clergy" ) ||
                      slotParms.mship5.equals( "Life Member" )) {

                     error = false;          // ok - no error
                  }
               }

               if (error == false) {         // if still ok, check for Non-Residents

                  if (slotParms.mship1.startsWith( "Non-Resident" ) || slotParms.mship2.startsWith( "Non-Resident" ) ||
                      slotParms.mship3.startsWith( "Non-Resident" ) || slotParms.mship4.startsWith( "Non-Resident" ) ||
                      slotParms.mship5.startsWith( "Non-Resident" )) {

                     error = true;          // default to error

                     if (slotParms.mship1.equals( "Resident" ) ||
                         slotParms.mship2.equals( "Resident" ) ||
                         slotParms.mship3.equals( "Resident" ) ||
                         slotParms.mship4.equals( "Resident" ) ||
                         slotParms.mship5.equals( "Resident" )) {

                        error = false;          // ok - no error
                     }
                  }
               }
            }
         }

      } else {

         if (slotParms.ind > 1) {

            error = true;             // error if no Members included

            if (slotParms.mtype1.endsWith( "ember" ) || slotParms.mtype2.endsWith( "ember" ) ||
                slotParms.mtype3.endsWith( "ember" ) || slotParms.mtype4.endsWith( "ember" ) ||
                slotParms.mtype5.endsWith( "ember" )) {

               error = false;          // OK if Member included
            }
         }
      }

   } else {

      if (slotParms.day.equals( "Monday" )) {   //  Monday

         //
         //  if after noon and more than 1 day in adv, at least one member must be in the request
         //
         if (slotParms.time > 1200 && slotParms.ind > 1) {

            error = true;             // error if no Members included

            if (slotParms.mtype1.endsWith( "ember" ) || slotParms.mtype2.endsWith( "ember" ) ||
                slotParms.mtype3.endsWith( "ember" ) || slotParms.mtype4.endsWith( "ember" ) ||
                slotParms.mtype5.endsWith( "ember" )) {

               error = false;          // OK if Member included
            }
         }
      }

      if (slotParms.day.equals( "Tuesday" )) {   //  Tuesday

         //
         //  if < 11:00 AM and more than 1 day in adv, MUST be at least one spouse (4/15 - 9/30)
         //
         //  if after 11 AM and more than 1 day in adv, at least one member must be in the request
         //
         if (slotParms.time < 1100 && slotParms.ind > 1 && date > sdate && date < edate) {

            error = true;          // default to error in case Member included (must be at least 1 spouse)

            if (slotParms.mtype1.endsWith( "pouse" ) || slotParms.mtype2.endsWith( "pouse" ) ||
                slotParms.mtype3.endsWith( "pouse" ) || slotParms.mtype4.endsWith( "pouse" ) ||
                slotParms.mtype5.endsWith( "pouse" )) {

               error = false;          // ok if Spouse included
            }

         } else {

            if (slotParms.ind > 1) {      // more than 1 day in advance

               error = true;             // error if no Members included

               if (slotParms.mtype1.endsWith( "ember" ) || slotParms.mtype2.endsWith( "ember" ) ||
                   slotParms.mtype3.endsWith( "ember" ) || slotParms.mtype4.endsWith( "ember" ) ||
                   slotParms.mtype5.endsWith( "ember" )) {

                  error = false;          // OK if Member included
               }
            }
         }
      }

      if (slotParms.day.equals( "Wednesday" ) && date > sdate && date < edate) {   //  Wednesday & regular season

         //
         //  if more than 1 day in adv, at least one member must be in the request (all day)
         //
         if (slotParms.ind > 1) {

            error = true;             // error if no Members included

            if (slotParms.mtype1.endsWith( "ember" ) || slotParms.mtype2.endsWith( "ember" ) ||
                slotParms.mtype3.endsWith( "ember" ) || slotParms.mtype4.endsWith( "ember" ) ||
                slotParms.mtype5.endsWith( "ember" )) {

               error = false;          // OK if Member included
            }
         }
      }

      if (slotParms.day.equals( "Thursday" )) {        //  Thursday

         //
         //  if < 10:00 AM and more than 1 day in adv, MUST be at least one spouse (4/15 - 9/30)
         //
         //  if after 10 AM and more than 1 day in adv, at least one member must be in the request
         //
         if (slotParms.time < 1000 && slotParms.ind > 1 && date > sdate && date < edate) {

            error = true;          // default to error in case Member included (must be at least 1 spouse)

            if (slotParms.mtype1.endsWith( "pouse" ) || slotParms.mtype2.endsWith( "pouse" ) ||
                slotParms.mtype3.endsWith( "pouse" ) || slotParms.mtype4.endsWith( "pouse" ) ||
                slotParms.mtype5.endsWith( "pouse" )) {

               error = false;          // ok if Spouse included
            }

         } else {

            if (slotParms.ind > 1) {

               error = true;             // error if no Members included

               if (slotParms.mtype1.endsWith( "ember" ) || slotParms.mtype2.endsWith( "ember" ) ||
                   slotParms.mtype3.endsWith( "ember" ) || slotParms.mtype4.endsWith( "ember" ) ||
                   slotParms.mtype5.endsWith( "ember" )) {

                  error = false;          // OK if Member included
               }
            }
         }
      }

      if (slotParms.day.equals( "Friday" )) {          //  Friday

         //
         //  if more than 1 day in adv, at least one member must be in the request (all day)
         //
         if (slotParms.ind > 1) {

            error = true;             // error if no Members included

            if (slotParms.mtype1.endsWith( "ember" ) || slotParms.mtype2.endsWith( "ember" ) ||
                slotParms.mtype3.endsWith( "ember" ) || slotParms.mtype4.endsWith( "ember" ) ||
                slotParms.mtype5.endsWith( "ember" )) {

               error = false;          // OK if Member included
            }
         }
      }

      if (slotParms.day.equals( "Sunday" )) {          //  Sunday

         //
         //  if < 10:00 AM - Members only
         //
         //  if 10 AM or later and more than 1 day in adv, at least one member must be in the request
         //
         if (slotParms.time < 1000 && date > sdate && date < edate) {

            if (slotParms.mtype1.endsWith( "pouse" ) || slotParms.mtype2.endsWith( "pouse" ) ||
                slotParms.mtype3.endsWith( "pouse" ) || slotParms.mtype4.endsWith( "pouse" ) ||
                slotParms.mtype5.endsWith( "pouse" )) {

               error = true;          // error if Spouse included
            }

         } else {

            if (slotParms.ind > 1) {

               error = true;             // error if no Members included

               if (slotParms.mtype1.endsWith( "ember" ) || slotParms.mtype2.endsWith( "ember" ) ||
                   slotParms.mtype3.endsWith( "ember" ) || slotParms.mtype4.endsWith( "ember" ) ||
                   slotParms.mtype5.endsWith( "ember" )) {

                  error = false;          // OK if Member included
               }
            }
         }
      }
   }

   return(error);
 }


 // *********************************************************
 //  Skaneateles CC - custom member restrictions - dependent restriction
 // *********************************************************

 public static boolean checkSkaneateles(parmSlot slotParms) {


   boolean error = false;

   //
   //  Every day from 4:00 PM on, all dependents must be accompanied by an adult.
   //
   if (slotParms.time > 1559) {

      if (slotParms.mtype1.equals( "Dependent" ) || slotParms.mtype2.equals( "Dependent" ) ||
          slotParms.mtype3.equals( "Dependent" ) || slotParms.mtype4.equals( "Dependent" ) ||
          slotParms.mtype5.equals( "Dependent" )) {

         error = true;          // assume error

         if (slotParms.mtype1.endsWith( "ale" ) || slotParms.mtype2.endsWith( "ale" ) ||
             slotParms.mtype3.endsWith( "ale" ) || slotParms.mtype4.endsWith( "ale" ) ||
             slotParms.mtype5.endsWith( "ale" )) {                                         // ends with Male or Female

            error = false;          // OK if Member included
         }
      }
   }

   return(error);
 }


 // *********************************************************
 //  Oakland Hills CC - custom member restrictions - dependent restriction
 // *********************************************************

 public static boolean checkOaklandKids(parmSlot slotParms) {


   boolean error = false;

   //
   //  Every day on both courses - all dependents must be accompanied by an adult.
   //
   if (slotParms.mtype1.equals( "Child" ) || slotParms.mtype2.equals( "Child" ) ||
       slotParms.mtype3.equals( "Child" ) || slotParms.mtype4.equals( "Child" ) ||
       slotParms.mtype5.equals( "Child" )) {

      error = true;          // assume error

      if (slotParms.mtype1.startsWith( "Primary" ) || slotParms.mtype2.startsWith( "Primary" ) ||
          slotParms.mtype3.startsWith( "Primary" ) || slotParms.mtype4.startsWith( "Primary" ) ||
          slotParms.mtype5.startsWith( "Primary" ) ||
          slotParms.mtype1.startsWith( "Spouse" ) || slotParms.mtype2.startsWith( "Spouse" ) ||
          slotParms.mtype3.startsWith( "Spouse" ) || slotParms.mtype4.startsWith( "Spouse" ) ||
          slotParms.mtype5.startsWith( "Spouse" )) {

         error = false;          // OK if Adult included
      }
   }

   return(error);
 }


 // *********************************************************
 //  Oakland Hills CC - check if the tee time is allowed
 // *********************************************************

 public static boolean checkOaklandAdvTime(long date, int time, String day_name) {


   boolean allow = false;


   //
   //  Tee time is more than 8 days in advance -  Ok if:
   //
   //     Tues - Fri and before 11:00 AM or after 2:30 PM
   //     Weekends or Holidays and after 12:30 PM
   //
   if (day_name.equals( "Saturday" ) || day_name.equals( "Sunday" ) || date == Hdate1 ||
       date == Hdate2 || date == Hdate2b || date == Hdate3) {

      if (time > 1230) {        // if after 12:30

         allow = true;          // ok
      }

   } else {

      if (day_name.equals( "Tuesday" ) || day_name.equals( "Wednesday" ) || day_name.equals( "Thursday" ) || day_name.equals( "Friday" )) {

         if (time < 1100 || time > 1430) {      // if before 11:00 AM or after 2:30 PM

            allow = true;                      // ok
         }
      }
   }

   return(allow);
 }


 // *********************************************************
 //  Oakland Hills CC - check if member has already booked an
 //                     advance time this year.
 // *********************************************************

 public static boolean checkOaklandAdvTime1(parmSlot slotParms, Connection con) {


   String course = "South Course";        // only enforce on this course

   boolean error = false;


   if ( slotParms.course.equals( course ) ) {

       long date = 0;
       int time = 0;
       int fb = 0;
       int count = 0;
       int days = 0;

       Calendar cal = new GregorianCalendar();         // get current time
       int hr = cal.get(Calendar.HOUR_OF_DAY);
       int min = cal.get(Calendar.MINUTE);

       int curtime = (hr * 100) + min;

       curtime = adjustTime(con, curtime);       // adjust for time zone

       if (curtime < 0) {                // if negative, then we went back or ahead one day

          curtime = 0 - curtime;          // convert back to positive value
       }

       //
       //  Determine the allowed days in advance for this request
       //
       if (slotParms.guests > 0) {          // if any guests in request

          if (slotParms.day.equals( "Tuesday" ) || slotParms.day.equals( "Wednesday" ) || slotParms.day.equals( "Thursday" ) ||
              slotParms.day.equals( "Friday" )) {

             days = 7;                         // guest times are allowed up to 7 days in advance

             if (curtime < 1000) {             // if before 10 AM

                days = 6;                      // one less day allowed
             }
          }

          if (slotParms.day.equals( "Saturday" )) {

             days = 5;                         // guest times are allowed up to 5 days in advance starting at noon

             if (curtime < 1200) {             // if before noon

                days = 4;                      // one less day allowed
             }
          }

          if (slotParms.day.equals( "Sunday" )) {

             days = 6;                         // guest times are allowed up to 6 days in advance starting at noon

             if (curtime < 1200) {             // if before noon

                days = 5;                      // one less day allowed
             }
          }

       } else {            // members only in this request

          days = 8;                         // member only times are allowed up to 8 days in advance

          if (curtime < 700) {              // if before 7 AM

             days = 7;                      // one less day allowed
          }
       }

       //
       //  Determine dates for tests below
       //
       cal = new GregorianCalendar();                  // get todays date
       cal.add(Calendar.DATE,days);                    // roll ahead x days (max allowed days in adv)
       int year = cal.get(Calendar.YEAR);
       int month = cal.get(Calendar.MONTH) +1;
       int day = cal.get(Calendar.DAY_OF_MONTH);

       long adate = (year * 10000) + (month * 100) + day;     // create a date field of yyyymmdd

       cal = new GregorianCalendar();                  // get todays date
       cal.add(Calendar.DATE,8);                       // roll ahead 8 days (max days for all requests)
       year = cal.get(Calendar.YEAR);
       month = cal.get(Calendar.MONTH) +1;
       day = cal.get(Calendar.DAY_OF_MONTH);

       long date8 = (year * 10000) + (month * 100) + day;     // create a date field of yyyymmdd


       //
       //  Check for adv time if South course and more than allowed days in adv
       //
       if ( slotParms.ind > days ) {    // only do if South Course

          PreparedStatement pstmt = null;
          ResultSet rs = null;

          String [] mNumA = new String [5];        // array to hold the player's mnums
          String [] userA = new String [5];        // array to hold the player's user names
          String [] playerA = new String [5];      // array to hold the player's names

          String userg1 = "";
          String userg2 = "";
          String userg3 = "";
          String userg4 = "";
          String userg5 = "";

          //
          //  Put mNums in array for loop
          //
          mNumA[0] = slotParms.mNum1;
          mNumA[1] = slotParms.mNum2;
          mNumA[2] = slotParms.mNum3;
          mNumA[3] = slotParms.mNum4;
          mNumA[4] = slotParms.mNum5;
          userA[0] = slotParms.user1;
          userA[1] = slotParms.user2;
          userA[2] = slotParms.user3;
          userA[3] = slotParms.user4;
          userA[4] = slotParms.user5;
          playerA[0] = slotParms.player1;
          playerA[1] = slotParms.player2;
          playerA[2] = slotParms.player3;
          playerA[3] = slotParms.player4;
          playerA[4] = slotParms.player5;

          //
          //  Check if any members included in tee time have already scheduled an advanced tee time request (anyone in family).
          //
          try {

             loop1:
             for (int i = 0; i < 5; i++) {

                if (!mNumA[i].equals( "" )) {           // if this player is a member and member number exists

                   count = 0;

                   pstmt = con.prepareStatement (
                      "SELECT date, time, fb, userg1, userg2, userg3, userg4, userg5 FROM teecurr2 " +
                      "WHERE (mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?) AND date > ? AND courseName = ?");

                   pstmt.clearParameters();
                   pstmt.setString(1, mNumA[i]);
                   pstmt.setString(2, mNumA[i]);
                   pstmt.setString(3, mNumA[i]);
                   pstmt.setString(4, mNumA[i]);
                   pstmt.setString(5, mNumA[i]);
                   pstmt.setLong(6, adate);          // check all times after max allowed days in adv
                   pstmt.setString(7, course);
                   rs = pstmt.executeQuery();

                   while (rs.next()) {

                      date = rs.getLong("date");
                      time = rs.getInt("time");
                      fb = rs.getInt("fb");
                      userg1 = rs.getString("userg1");
                      userg2 = rs.getString("userg2");
                      userg3 = rs.getString("userg3");
                      userg4 = rs.getString("userg4");
                      userg5 = rs.getString("userg5");

                      if (date != slotParms.date || time != slotParms.time || fb != slotParms.fb) {   // if not this tee time

                         if (date > date8) {         // if more than 8 days from today

                            count++;          // count it regardless

                         } else {             // check if member-guest time

                            if (userg1.equals( userA[i] ) || userg2.equals( userA[i] ) || userg3.equals( userA[i] ) ||
                                userg4.equals( userA[i] ) || userg5.equals( userA[i] )) {       // if member already has a guest time

                               count++;          // count it
                            }
                         }
                      }
                   }

                   pstmt.close();

                   if (count > 0) {                     // if any times found

                      error = true;
                      slotParms.player = playerA[i];      // save player's name for error message
                      break loop1;
                   }
                }
             }

          } catch (Exception e) {

             Utilities.logError("Error checking for Advance Time - verifySlot.checkOaklandAdvTime1: " + e.getMessage());

          } finally {

             try { rs.close(); }
             catch (Exception ignore) {}

             try { pstmt.close(); }
             catch (Exception ignore) {}

          }
       }
   }

   return(error);

 }


 // *********************************************************
 //  Oakland Hills CC - check if there are already 2 advance
 //                     times on this date.
 // *********************************************************

 public static boolean checkOaklandAdvTime2(parmSlot slotParms, Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   boolean error = false;

   int time = 0;
   int fb = 0;
   int count = 0;
   int days = 0;
   int days2 = 0;

   String player1 = "";
   String course = "South Course";        // only enforce on this course
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";

   Calendar cal = new GregorianCalendar();         // get current time
   int hr = cal.get(Calendar.HOUR_OF_DAY);
   int min = cal.get(Calendar.MINUTE);

   int curtime = (hr * 100) + min;

   curtime = adjustTime(con, curtime);       // adjust for time zone

   if (curtime < 0) {                // if negative, then we went back or ahead one day

      curtime = 0 - curtime;          // convert back to positive value
   }

   //
   //  Determine the allowed days in advance for any member times
   //
   days2 = 8;                         // member only times are allowed up to 8 days in advance

   if (curtime < 700) {              // if before 7 AM

      days2 = 7;                      // one less day allowed
   }

   //
   //  Determine the allowed days in advance for any guest times
   //
   if (slotParms.guests > 0) {          // if any guests in request

      if (slotParms.day.equals( "Tuesday" ) || slotParms.day.equals( "Wednesday" ) || slotParms.day.equals( "Thursday" ) ||
          slotParms.day.equals( "Friday" )) {

         days = 7;                         // guest times are allowed up to 7 days in advance

         if (curtime < 1000) {             // if before 10 AM

            days = 6;                      // one less day allowed
         }
      }

      if (slotParms.day.equals( "Saturday" )) {

         days = 5;                         // guest times are allowed up to 5 days in advance starting at noon

         if (curtime < 1200) {             // if before noon

            days = 4;                      // one less day allowed
         }
      }

      if (slotParms.day.equals( "Sunday" )) {

         days = 6;                         // guest times are allowed up to 6 days in advance starting at noon

         if (curtime < 1200) {             // if before noon

            days = 5;                      // one less day allowed
         }
      }

   } else {

      days = days2;
   }


   //
   //  Check for adv time if South course and more than allowed days in adv
   //
   if (slotParms.course.equals( course ) && slotParms.ind > days) {

      //
      //  Check if there are already 2 advanced tee times scheduled for this date
      //
      try {

         pstmt = con.prepareStatement (
            "SELECT time, fb, player1, userg1, userg2, userg3, userg4, userg5 FROM teecurr2 " +
            "WHERE date = ? AND courseName = ?");

         pstmt.clearParameters();
         pstmt.setLong(1, slotParms.date);
         pstmt.setString(2, course);
         rs = pstmt.executeQuery();            // see if member has another adv tee time already scheduled

         while (rs.next()) {

            time = rs.getInt("time");
            fb = rs.getInt("fb");
            player1 = rs.getString("player1");
            userg1 = rs.getString("userg1");
            userg2 = rs.getString("userg2");
            userg3 = rs.getString("userg3");
            userg4 = rs.getString("userg4");
            userg5 = rs.getString("userg5");

            if ((time != slotParms.time || fb != slotParms.fb) && !player1.equals( "" )) {   // if not this tee time and occupied

               if (slotParms.ind > days2) {       // if more than allowed for member times, then they all count

                  count++;

               } else {                        // check for guest times only

                  if (!userg1.equals( "" ) || !userg2.equals( "" ) || !userg3.equals( "" ) || !userg4.equals( "" ) || !userg5.equals( "" )) {

                     count++;             // count it
                  }
               }
            }
         }

         pstmt.close();

         if (count > 1) {

            error = true;        // error if already 2 adv times
         }

      } catch (Exception e) {

         Utilities.logError("Error checking for Advance Times - verifySlot.checkOaklandAdvTime2: " + e.getMessage());

      } finally {

       try { rs.close(); }
       catch (Exception ignore) {}

       try { pstmt.close(); }
       catch (Exception ignore) {}

      }
   }

   return(error);
 }


 // *********************************************************
 //  Merion CC - check if Non-Resident member already has an
 //              advance tee time (one per fmaily).
 //
 //    called from above
 // *********************************************************

 public static boolean checkMerionNonRes(parmSlot slotParms, Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   boolean skip = true;           // default = ok to skip

   int count = 0;
   int time = 0;
   int fb = 0;

   long date = 0;

   String mNum = "";
   String course = "";
   String mshipM = "Non-Resident";        // Custom for this mship only (Merion)


   //
   //  Determine date for tests below
   //
   Calendar cal = new GregorianCalendar();                  // get todays date
   cal.add(Calendar.DATE,7);                       // roll ahead 7 days (max allowed days in adv)
   int year = cal.get(Calendar.YEAR);
   int month = cal.get(Calendar.MONTH) +1;
   int day = cal.get(Calendar.DAY_OF_MONTH);

   long sdate = (year * 10000) + (month * 100) + day;     // create a date field of yyyymmdd

   //
   //   Find the member number to search for
   //
   if (slotParms.mship1.equals( mshipM )) {          // if this is a Non_Resident member

      mNum = slotParms.mNum1;                        // get the mNum

   } else {

      if (slotParms.mship2.equals( mshipM )) {          // if this is a Non_Resident member

         mNum = slotParms.mNum2;                        // get the mNum

      } else {

         if (slotParms.mship3.equals( mshipM )) {          // if this is a Non_Resident member

            mNum = slotParms.mNum3;                        // get the mNum

         } else {

            if (slotParms.mship4.equals( mshipM )) {          // if this is a Non_Resident member

               mNum = slotParms.mNum4;                        // get the mNum

            } else {

               mNum = slotParms.mNum5;                        // get the mNum
            }
         }
      }
   }


   //
   //  See if any family member already has an advanced tee time
   //
   try {

      pstmt = con.prepareStatement (
         "SELECT date, time, fb, courseName FROM teecurr2 " +
         "WHERE date > ? AND " +
         "(mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?)");

      pstmt.clearParameters();
      pstmt.setLong(1, sdate);
      pstmt.setString(2, mNum);
      pstmt.setString(3, mNum);
      pstmt.setString(4, mNum);
      pstmt.setString(5, mNum);
      pstmt.setString(6, mNum);
      rs = pstmt.executeQuery();            // see if member has another adv tee time already scheduled

      while (rs.next()) {

         date = rs.getLong("date");
         time = rs.getInt("time");
         fb = rs.getInt("fb");
         course = rs.getString("courseName");

         if (date != slotParms.date || time != slotParms.time || fb != slotParms.fb || !course.equals( slotParms.course )) {

            count++;           // count this one
         }
      }

      pstmt.close();

      if (count > 0) {               // if aother advance times found

         skip = false;                   // other tee time found - do not skip this member
      }

   } catch (Exception e) {

       Utilities.logError("Error checking for Advance Times - verifySlot.checkMerionNonRes: " + e.getMessage());

   } finally {

       try { rs.close(); }
       catch (Exception ignore) {}

       try { pstmt.close(); }
       catch (Exception ignore) {}

   }

   return(skip);

 }


 // *********************************************************
 //  Merion CC - check if there are already 4 advance
 //              times on this weekend day.
 // *********************************************************

 public static boolean checkMerionWE(parmSlot slotParms, Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   boolean error = false;

   int time = 0;
   int time2 = 0;
   int fb = 0;
   int count = 0;

   String player1 = "";
   String course = "East";        // only enforce on this course


   //
   //  Check if there are already 4 advanced tee times scheduled for this date
   //
   if (slotParms.day.equals( "Saturday" )) {

      time2 = 1030;       // after 10:30
   }

   if (slotParms.day.equals( "Sunday" )) {

      time2 = 900;        // after 9:00
   }


   try {

      pstmt = con.prepareStatement (
         "SELECT time, fb, player1 FROM teecurr2 " +
         "WHERE date = ? AND time > ? AND courseName = ?");

      pstmt.clearParameters();
      pstmt.setLong(1, slotParms.date);
      pstmt.setInt(2, time2);
      pstmt.setString(3, course);
      rs = pstmt.executeQuery();            // see if member has another adv tee time already scheduled

      while (rs.next()) {

         time = rs.getInt("time");
         fb = rs.getInt("fb");
         player1 = rs.getString("player1");

         if ((time != slotParms.time || fb != slotParms.fb) && !player1.equals( "" )) {   // if not this tee time and occupied

            count++;
         }
      }

      pstmt.close();

      if (count > 3) {

         error = true;        // error if already 4 adv times
      }

   } catch (Exception e) {

      Utilities.logError("Error checking for Advance Times - verifySlot.checkMerionWE: " + e.getMessage());

   } finally {

       try { rs.close(); }
       catch (Exception ignore) {}

       try { pstmt.close(); }
       catch (Exception ignore) {}

   }

   return(error);
 }


 // *********************************************************
 //  Merion CC - check if there are already 4 advance
 //              times on this weekend day.
 // *********************************************************

 public static boolean checkMerionWEm(parmSlotm slotParms, Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   boolean error = false;

   int time = 0;
   int time2 = 0;
   int fb = 0;
   int count = slotParms.slots;       // # of tee times being requested

   String player1 = "";
   String course = "East";        // only enforce on this course


   //
   //  Check if there are already 4 advanced tee times scheduled for this date
   //
   if (slotParms.day.equals( "Saturday" )) {

      time2 = 1030;       // after 10:30
   }

   if (slotParms.day.equals( "Sunday" )) {

      time2 = 900;        // after 9:00
   }


   try {

      pstmt = con.prepareStatement (
         "SELECT time, fb, player1 FROM teecurr2 " +
         "WHERE date = ? AND time > ? AND courseName = ?");

      pstmt.clearParameters();
      pstmt.setLong(1, slotParms.date);
      pstmt.setInt(2, time2);
      pstmt.setString(3, course);
      rs = pstmt.executeQuery();            // see if member has another adv tee time already scheduled

      while (rs.next()) {

         time = rs.getInt("time");
         fb = rs.getInt("fb");
         player1 = rs.getString("player1");

         if ((time != slotParms.time1 || fb != slotParms.fb) && !player1.equals( "" )) {   // if not this tee time and occupied

            count++;
         }
      }

      pstmt.close();

      if (count > 4) {

         error = true;        // error if this request would exceed 4 adv times
      }

   } catch (Exception e) {

      Utilities.logError("Error checking for Advance Times - verifySlot.checkMerionWEm: " + e.getMessage());

   }

   return(error);

 }


 // *********************************************************
 //  CC of the Rockies & Catamount Ranch & Sonnenalp -
 //                      Check if member has already booked max
 //                      advance times this year.
 // *********************************************************

 public static boolean checkRockies(parmSlot slotParms, Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   boolean error = false;

   String [] userA = new String [5];        // array to hold the usernames
   String [] playerA = new String [5];      // array to hold the player's names

   int max = 0;

   //
   //  Put users in array for loop
   //
   playerA[0] = slotParms.player1;
   playerA[1] = slotParms.player2;
   playerA[2] = slotParms.player3;
   playerA[3] = slotParms.player4;
   playerA[4] = slotParms.player5;

   if (slotParms.club.equals( "ccrockies" ) || slotParms.club.equals( "sonnenalp" )) {   // if CC of the Rockies or Sonnenalp

      userA[0] = slotParms.user1;
      userA[1] = slotParms.user2;
      userA[2] = slotParms.user3;
      userA[3] = slotParms.user4;
      userA[4] = slotParms.user5;

      max = 4;                       // max allowed is 5

      if (slotParms.club.equals( "sonnenalp" )) {   // if Sonnenalp

         max = 11;                  // max allowed is 12
      }

   } else {                         // Catamount Ranch - only non-Founder members

      userA[0] = "";
      userA[1] = "";
      userA[2] = "";
      userA[3] = "";
      userA[4] = "";

      if (!slotParms.user1.equals( "" ) && !slotParms.mship1.equals( "Founder" )) {  // if this player is NOT a Founder member

         userA[0] = slotParms.user1;
      }
      if (!slotParms.user2.equals( "" ) && !slotParms.mship2.equals( "Founder" )) {  // if this player is NOT a Founder member

         userA[1] = slotParms.user2;
      }
      if (!slotParms.user3.equals( "" ) && !slotParms.mship3.equals( "Founder" )) {  // if this player is NOT a Founder member

         userA[2] = slotParms.user3;
      }
      if (!slotParms.user4.equals( "" ) && !slotParms.mship4.equals( "Founder" )) {  // if this player is NOT a Founder member

         userA[3] = slotParms.user4;
      }
      if (!slotParms.user5.equals( "" ) && !slotParms.mship5.equals( "Founder" )) {  // if this player is NOT a Founder member

         userA[4] = slotParms.user5;
      }

      max = 9;                       // max allowed is 10 (changed on 6/29/07)
//    max = 4;                       // max allowed is 5
   }


   //
   //  get the date of today
   //
   Calendar cal = new GregorianCalendar();         // get todays date
   int year = cal.get(Calendar.YEAR);
   int month = cal.get(Calendar.MONTH) +1;
   int day = cal.get(Calendar.DAY_OF_MONTH);

   long adate = (year * 10000) + (month * 100) + day;     // create a date field of yyyymmdd

   long date = 0;
   int time = 0;
   int fb = 0;
   int count = 0;


   //
   //  Check if any members included in tee time have already scheduled 'max' advanced tee time requests
   //
   try {

      loop1:
      for (int i = 0; i < 5; i++) {

         if (!userA[i].equals( "" )) {           // if this player is a member

            count = 0;

            pstmt = con.prepareStatement (
               "SELECT date, time, fb FROM teecurr2 " +
               "WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?) AND date > ?");

            pstmt.clearParameters();
            pstmt.setString(1, userA[i]);
            pstmt.setString(2, userA[i]);
            pstmt.setString(3, userA[i]);
            pstmt.setString(4, userA[i]);
            pstmt.setString(5, userA[i]);
            pstmt.setLong(6, adate);

            rs = pstmt.executeQuery();            // see if member has another adv tee time already scheduled

            while (rs.next()) {

               date = rs.getLong("date");
               time = rs.getInt("time");
               fb = rs.getInt("fb");

               if (date != slotParms.date || time != slotParms.time || fb != slotParms.fb) {   // if not this tee time

                  count++;                           // count number of adv times
               }
            }

            pstmt.close();

            if (count > max) {                      // if member already has max allowed adv times

               error = true;
               slotParms.player = playerA[i];      // save player's name for error message
               break loop1;
            }
         }
      }

   } catch (Exception e) {

       Utilities.logError("Error checking for Advance Times - verifySlot.checkRockies: " + e.getMessage());

   } finally {

       try { rs.close(); }
       catch (Exception ignore) {}

       try { pstmt.close(); }
       catch (Exception ignore) {}

   }

   return(error);
 }


 // *********************************************************
 //  Bearpath CC - check if guests are allowed
 // *********************************************************

 public static boolean checkBearpathGuests(String day, long date, int time, int ind) {


   boolean error = false;


   //
   //  Get the current server time
   //
   Calendar cal = new GregorianCalendar();        // get todays date and time
   int hr = cal.get(Calendar.HOUR_OF_DAY);
   int min = cal.get(Calendar.MINUTE);

   int currtime = (hr * 100) + min;

   //
   //  Guests were specified in the tee time - check if ok
   //
   //     Tues - Fri (except holidays), these times are for members only:
   //
   //        12:06, 12:24, 12:42, 1:00, 1:18, 1:36, 1:54
   //
   if (!day.equals( "Saturday" ) && !day.equals( "Sunday" ) && !day.equals( "Monday" ) &&
       date != Hdate1 && date != Hdate2 && date != Hdate2b && date != Hdate3) {

      if (time == 1206 || time == 1224 || time == 1242 || time == 1300 ||
          time == 1318 || time == 1336 || time == 1354) {

         //
         //  if the day before (1 day in adv) and after 8:00 AM, then its ok
         //
         if (ind == 0 || (ind == 1 && currtime >= 800)) {

            error = false;          // guests are ok now

         } else {

            error = true;          // No guests allowed
         }
      }

   }

   return(error);
 }


 // *********************************************************
 //  Ritz Carlton CC - custom member restrictions
 // *********************************************************

 public static boolean checkRitz(parmSlot slotParms, Connection con) {


   PreparedStatement pstmt = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   int hour = 0;
   int count = 0;
   int stime = 0;
   int etime = 0;

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

   boolean error = false;


   hour = slotParms.time / 100;       // isolate the tee time hour

   stime = hour * 100;                // create time range (xx00 - xx59)
   etime = (hour * 100) + 59;

   //
   //  There can only be 2 tee times per hour with either a 'Club Golf' mship type or a 'Recip' guest.
   //
   if (slotParms.mship1.equals( "Club Golf" ) || slotParms.mship2.equals( "Club Golf" ) ||
       slotParms.mship3.equals( "Club Golf" ) || slotParms.mship4.equals( "Club Golf" ) ||
       slotParms.mship5.equals( "Club Golf" ) ||
       slotParms.player1.startsWith( "Recip" ) || slotParms.player2.startsWith( "Recip" ) ||
       slotParms.player3.startsWith( "Recip" ) || slotParms.player4.startsWith( "Recip" ) ||
       slotParms.player5.startsWith( "Recip" )) {

      //
      //  Check for others already scheduled this hour
      //
      try {

         pstmt = con.prepareStatement (
            "SELECT player1, player2, player3, player4, player5, " +
            "username1, username2, username3, username4, player5, username5 " +
            "FROM teecurr2 " +
            "WHERE date = ? AND time >= ? AND time <= ? AND player1 != ''");

         pstmt.clearParameters();        // clear the parms
         pstmt.setLong(1, slotParms.date);
         pstmt.setInt(2, stime);
         pstmt.setInt(3, etime);
         rs = pstmt.executeQuery();      // execute the prepared stmt

         while (rs.next()) {

            player1 = rs.getString("player1");
            player2 = rs.getString("player2");
            player3 = rs.getString("player3");
            player4 = rs.getString("player4");
            user1 = rs.getString("username1");
            user2 = rs.getString("username2");
            user3 = rs.getString("username3");
            user4 = rs.getString("username4");
            player5 = rs.getString("player5");
            user5 = rs.getString("username5");

            if (player1.startsWith( "Recip" ) || player2.startsWith( "Recip" ) || player3.startsWith( "Recip" ) ||
                player4.startsWith( "Recip" ) || player5.startsWith( "Recip" )) {

               count++;

            } else {

               boolean found = false;

               //
               //  No Recip guest types, check for 'Club Golf' members
               //
               if (!user1.equals( "" )) {

                  pstmt2 = con.prepareStatement (
                     "SELECT name_mi " +
                     "FROM member2b " +
                     "WHERE username = ? AND m_ship = ?");

                  pstmt2.clearParameters();        // clear the parms
                  pstmt2.setString(1, user1);
                  pstmt2.setString(2, "Club Golf");
                  rs2 = pstmt2.executeQuery();      // execute the prepared stmt

                  if (rs2.next()) {

                     found = true;
                     count++;
                  }
                  pstmt2.close();
               }

               if (!user2.equals( "" ) && found == false) {

                  pstmt2 = con.prepareStatement (
                     "SELECT name_mi " +
                     "FROM member2b " +
                     "WHERE username = ? AND m_ship = ?");

                  pstmt2.clearParameters();        // clear the parms
                  pstmt2.setString(1, user2);
                  pstmt2.setString(2, "Club Golf");
                  rs2 = pstmt2.executeQuery();      // execute the prepared stmt

                  if (rs2.next()) {

                     found = true;
                     count++;
                  }
                  pstmt2.close();
               }

               if (!user3.equals( "" ) && found == false) {

                  pstmt2 = con.prepareStatement (
                     "SELECT name_mi " +
                     "FROM member2b " +
                     "WHERE username = ? AND m_ship = ?");

                  pstmt2.clearParameters();        // clear the parms
                  pstmt2.setString(1, user3);
                  pstmt2.setString(2, "Club Golf");
                  rs2 = pstmt2.executeQuery();      // execute the prepared stmt

                  if (rs2.next()) {

                     found = true;
                     count++;
                  }
                  pstmt2.close();
               }

               if (!user4.equals( "" ) && found == false) {

                  pstmt2 = con.prepareStatement (
                     "SELECT name_mi " +
                     "FROM member2b " +
                     "WHERE username = ? AND m_ship = ?");

                  pstmt2.clearParameters();        // clear the parms
                  pstmt2.setString(1, user4);
                  pstmt2.setString(2, "Club Golf");
                  rs2 = pstmt2.executeQuery();      // execute the prepared stmt

                  if (rs2.next()) {

                     found = true;
                     count++;
                  }
                  pstmt2.close();
               }

               if (!user5.equals( "" ) && found == false) {

                  pstmt2 = con.prepareStatement (
                     "SELECT name_mi " +
                     "FROM member2b " +
                     "WHERE username = ? AND m_ship = ?");

                  pstmt2.clearParameters();        // clear the parms
                  pstmt2.setString(1, user5);
                  pstmt2.setString(2, "Club Golf");
                  rs2 = pstmt2.executeQuery();      // execute the prepared stmt

                  if (rs2.next()) {

                     found = true;
                     count++;
                  }
                  pstmt2.close();
               }
            }
         }
         pstmt.close();

      } catch (Exception e1) {

         Utilities.logError("Error checking for RitzCarlton restrictions - verifySlot.checkRitz: " + e1.getMessage());        // log the error message

      } finally {

         try { rs.close(); }
         catch (Exception ignore) {}

         try { rs2.close(); }
         catch (Exception ignore) {}

         try { pstmt.close(); }
         catch (Exception ignore) {}

         try { pstmt2.close(); }
         catch (Exception ignore) {}

      }

      //
      //  Now see if there are already 2 tee times with restricted players/guests
      //
      if (count > 1) {

         error = true;
      }

   }

   return(error);
 }


 // *********************************************************
 // Woodway CC - Custom Processing (check date and time of day for 2-some only time)
 // *********************************************************

 public static boolean checkWoodway(long date, int time) {

   //
   //   2-some ONLY times from 9:00 AM - 10:30 AM for the following dates:
   //
   //      April 27
   //
   //
   //   2-some ONLY times from 8:00 AM to 10:00 AM for the following dates:
   //
   //      April 20
   //      May 11, 25
   //      July 13, 20, 27
   //      August 3, 10, 24, 31
   //      Sept  28
   //
   //
   //   2-some ONLY times from 8:45 AM to 10:15 AM for the following dates:
   //
   //      April 28
   //      May 12
   //      June 30
   //      July 14, 21, 28
   //      August 11, 18, 25
   //      Sept  29
   //      Oct  6
   //

   boolean status = false;

   //  Determine date values - month, day, year
   long year = date / 10000;
   long month = (date - (year * 10000)) / 100;
   long day = date - ((year * 10000) + (month * 100));

   if (month == 4 && day == 27) {       // 9:00 - 10:30 date

       if (time >= 900 && time <= 1030) status = true;

   } else if (
           (month == 4 && day == 20) ||
           (month == 5 && (day == 11 || day == 25)) ||
           (month == 7 && (day == 13 || day == 20 || day == 27)) ||
           (month == 8 && (day == 3 || day == 10 || day == 24 || day == 31)) ||
           (month == 9 && day == 28)) {     // 8:00 - 10:30 dates

       if (time >= 800 && time <= 1000) status = true;

   } else if (
           (month == 4 && day == 28) ||
           (month == 5 && day == 12) ||
           (month == 6 && day == 30) ||
           (month == 7 && (day == 14 || day == 21 || day == 28)) ||
           (month == 8 && (day == 11 || day == 18 || day == 25)) ||
           (month == 9 && day == 29) ||
           (month == 10 && day == 6)) {     // 8;45 - 10:15 dates

       if (time >= 845 && time <= 1015) status = true;
      }

   return(status);         // true = 2-somes only time
 }


 // *********************************************************
 // Hudson National CC - Custom Processing (check date and time of day for 2-some only time)
 // *********************************************************

 public static boolean checkHudson(long date, int time, String dayName) {


   boolean status = false;

   //
   //  Determine date values - month, day, year
   //
   //long year = date / 10000;
   //long month = (date - (year * 10000)) / 100;
   //long day = date - ((year * 10000) + (month * 100));

   //
   //        ************* See also SystemUtils ********************
   //
   //   2-some ONLY times from 7:30 AM through 8:20 AM for the following dates:
   //
   //      All Weekends
   //      Memorial Day
   //      July 4th
   //      Labor Day
   //
   // if weekend or holiday and between 7:30 & 8:50AM
   if ( (dayName.equals( "Saturday" ) || dayName.equals( "Sunday" ) ||
         date == Hdate1 || date == Hdate2 || date == Hdate3 ) &&
        (time > 729 && time < 821)) {   // was 851 changed per Brock on 9/28/09 (Case 1447)

       status = true;                   // 2-some only time
   }

   return(status);
 }


 // *********************************************************
 // The Stanwich Club - Custom Processing (check date and time of day for 2-some only time)
 // *********************************************************

 public static boolean checkStanwich(long date, int time, String dayName) {


   boolean status = false;

   //
   //        ************* See also SystemUtils ********************
   //
   //   2-some ONLY times as follows:
   //
   //      Sat, Sun & Mon = 7:00 - 8:18 inclusive
   //      Tues           = 8:00 - 10:00 inclusive
   //      Wed            = 8:12 - 10:00 inclusive
   //
   if (dayName.equals( "Saturday" ) || dayName.equals( "Sunday" ) || dayName.equals( "Monday" )) {   // Weekend ?

      if (time > 659 && time < 819) {         // if tee time is between 7:00 and 8:18 AM

         status = true;                        // 2-some only time
      }
   }

   if (dayName.equals( "Tuesday" )) {         // Tuesday ?

      if (time > 759 && time < 1001) {         // if tee time is between 8:00 and 10:00 AM

         status = true;                        // 2-some only time
      }
   }

   if (dayName.equals( "Wednesday" )) {         // Wednesday ?

      if (time > 811 && time < 1001) {         // if tee time is between 8:12 and 10:00 AM

         status = true;                        // 2-some only time
      }
   }

   return(status);         // true = 2-somes only time
 }


 // *********************************************************
 //  New Canaan - Custom Processing (check date and time of day for 2-some only time)
 // *********************************************************

 public static boolean checkNewCanaan(long date, int time, String name) {


   boolean status = false;

   long shortDate = date - ((date / 10000) * 10000);       // get mmdd (i.e.  20060512 - 20060000)

   //
   //        ************* See also SystemUtils ********************
   //
   //   2-some ONLY times as follows:
   //
   //        Off-Peak Season (401 - 5/26 OR 9/05 - 11/01) Saturdays and Sundays
   //
   //              7:30 - 7:55
   //
   //        Peak Season (5/27 - 9/04) Saturdays, Sundays and Holidays
   //
   //              7:00 - 7:25
   //
   //        Weekday Season (4/11 - 10/30) Tues, Wed, Thu, Fri, except for 7/04
   //
   //              8:00 - 8:25
   //
   if ((shortDate > 331 && shortDate < 527) || (shortDate > 904 && shortDate < 1102)) {  // if Off Peak

      if ((name.equalsIgnoreCase( "saturday" ) || name.equalsIgnoreCase( "sunday" )) &&
           time > 729 && time < 756) {

         status = true;                        // 2-some only time
      }
   }

   if (shortDate > 526 && shortDate < 905) {       // Peak Season

      if (name.equalsIgnoreCase( "saturday" ) || name.equalsIgnoreCase( "sunday" ) ||
          date == Hdate1 || date == Hdate2 || date == Hdate2b || date == Hdate3) {

         if (time > 659 && time < 726) {

            status = true;                        // 2-some only time
         }
      }
   }

   if (shortDate > 410 && shortDate < 1031) {     // Weekday Season

      if ((name.equalsIgnoreCase( "tuesday" ) || name.equalsIgnoreCase( "wednesday" ) || name.equalsIgnoreCase( "thursday" ) ||
           name.equalsIgnoreCase( "friday" )) && shortDate != 704) {

         if (time > 759 && time < 826) {

            status = true;                        // 2-some only time
         }
      }
   }

   return(status);         // true = 2-somes only time
 }


 // *********************************************************
 //  Apawamis - Custom Processing (check date and time of day for 2-some only time)
 // *********************************************************

 public static boolean checkApawamis(long date, int time, String name) {


   boolean status = false;

   long shortDate = date - ((date / 10000) * 10000);       // get mmdd (i.e.  20060512 - 20060000)

   //
   //        ************* See also SystemUtils ********************
   //
   //   2-some ONLY times as follows:
   //
   //        Off-Peak Season (4/30 - 5/21 AND 9/04 - 10/30) Sundays
   //
   //              7:30 - 8:30
   //
   //        Peak Season (5/28 - 9/03) Sundays
   //
   //              7:00 - 8:30
   //
   if (name.equalsIgnoreCase( "sunday" )) {

      if (shortDate > 427 && shortDate < 525 && time > 729 && time < 831) {    // if Off Peak

         status = true;                        // 2-some only time
      }

      if (shortDate > 903 && shortDate < 1031 && time > 729 && time < 831) {    // if Off Peak

         status = true;                        // 2-some only time
      }

      if (shortDate > 525 && shortDate < 904 && time > 659 && time < 831) {    // if Peak

         status = true;                        // 2-some only time
      }

   }

   return(status);         // true = 2-somes only time
 }


 // *********************************************************
 //  Wee Burn - Custom Processing (check date and time of day for 2-some only time)
 // *********************************************************

 public static boolean checkWeeburn(long date, int time, String name) {


   boolean status = false;

   long shortDate = date - ((date / 10000) * 10000);       // get mmdd (i.e.  20060512 - 20060000)

   //
   //        ************* See also SystemUtils ********************
   //
   //     Special 2-some times on Tues & Wed based on time of year (July 6th for 2006 only!)
   //
   //     NOTE:  this checks a range of dates that allows for Wednesdays changing each year!!!
   //            (i.e. instead of checking for 5/31, it checks the whole week)
   //
   if (name.equals( "Wednesday" )) {        // 7/06/2006 is a Thurs

      if (shortDate > 419 && shortDate < 931) {                 // April - Sept. (every Wed)

         if ((shortDate > 524 && shortDate < 532) || (shortDate > 600 && shortDate < 608)) {

            if (time > 759 && time < 1030) {  // if 8:00 - 10:29

               status = true;                        // 2-some only time
            }

         } else {

            if ((shortDate > 823 && shortDate < 831) || (shortDate > 831 && shortDate < 907)) {

               if (time > 759 && time < 1030) {  // if 8:00 - 9:59

                  status = true;                        // 2-some only time
               }

            } else {                             // all other Wednesdays in date range

               if (time > 859 && time < 1030) {  // if 9:00 - 9:59

                  status = true;                        // 2-some only time
               }
            }
         }
      }

   } else {

      if (name.equals( "Tuesday" )) {        // if Tuesday

         if ((shortDate > 500 && shortDate < 627) || (shortDate > 704 && shortDate < 931)) {

            if (time > 759 && time < 1000) {  // if 8:00 - 9:59

               status = true;                        // 2-some only time
            }
         }
      }
   }

   return(status);         // true = 2-somes only time
 }


 // *********************************************************
 // Piedmont Driving Club - Custom Processing (check date and time of day)
 // *********************************************************

 public static int checkPiedmont(long date, int time, String day_name) {


   int status = 0;
   //int endtime = 0;

   //
   //  Determine date values - month, day, year
   //
   long yy = date / 10000;
   long mm = (date - (yy * 10000)) / 100;
   long dd = date - ((yy * 10000) + (mm * 100));

   long sdate = 0;
   long edate = 0;

   //
   //  Determine start and end of Daylight Saving Time
   //
   if (yy == 2007) {

      sdate = 20070311;
      edate = 20071103;

   } else {

      if (yy == 2008) {

         sdate = 20080309;
         edate = 20081101;

      } else {

         if (yy == 2009) {

            sdate = 20090308;
            edate = 20091031;

         } else {

            sdate = 20100314;      // 2010
            edate = 20101106;
         }
      }
   }

   //
   //  Check the date, day, and time of day - return status indicator
   //
   //      0 = tee time not affected
   //      1 = Sat or Sun between 1st 4-some group and noon (issue notice regarding caddies)
   //      2 = Mon - Fri, before 2:30 PM (or 1:30 DST) OR Sat - Sun, noon to 2:30 (or 1:30 DST)
   //      3 = Sat or Sun (or Mem Day or Labor Day) 1st 6 tee times are 2-somes only
   //
   if (day_name.equalsIgnoreCase( "Saturday" ) || day_name.equalsIgnoreCase( "Sunday" ) ||
                      date == Hdate1 || date == Hdate3) {


      if (time < 1210) {        // if before noon (actually 12:10)

         if (mm == 03 || mm == 11) {     // if March or Nov

            if (time > 900) {            // if before noon and after the 2-some times

               status = 1;               // return status to indicate this

            } else {

               status = 3;               // return status to indicate 2-some only time
            }

         } else {

            if (mm == 12 || mm == 1 || mm == 2) {     // if Dec, Jan or Feb

               if (time > 930) {            // if before noon and after the 2-some times

                  status = 1;               // return status to indicate this

               } else {

                  status = 3;               // return status to indicate 2-some only time
               }

            } else {

               if (time > 830) {            // if before noon and after the 2-some times

                  status = 1;               // return status to indicate this

               } else {

                  status = 3;               // return status to indicate 2-some only time
               }
            }
         }

      } else {      // Sat or Sun and 12:10 or later

         if (date < sdate || date > edate) {      // if not DST

            if (time < 1331) {                // if between 12:10 and 1:30 (inclusive)

               status = 2;
            }

         } else {           // it is DST

            if (time < 1431) {                // if between 12:10 and 2:30 (inclusive)

               status = 2;
            }
         }
      }

   } else {        // it is a weekday (Monday - Friday)

      if (date < sdate || date > edate) {      // if not DST

         if (time < 1331) {                // if before 1:31

            status = 2;
         }

      } else {           // it is DST (April xx - October xx)

         if (time < 1431) {                // if before 2:31

            status = 2;
         }
      }
   }

   return(status);
 }


/**
 //************************************************************************
 //
 //  Green Bay CC - Check for more than 9 guests in one hour.
 //
 //    Called by:  Member_slot & Proshop_slot
 //
 //    Check teecurr for the number of guests requested during the hour.
 //
 //************************************************************************
 **/

 public static boolean checkGBguests(parmSlot slotParms, Connection con) {


   PreparedStatement pstmt1 = null;
   ResultSet rs = null;

   boolean error = false;

   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";

   int guests = slotParms.guests;    // # of guests in this request

   //
   //  Count all guests already scheduled during the hour specified for this tee time (exclude this time)
   //
   try {

      pstmt1 = con.prepareStatement (
         "SELECT " +
         "userg1, userg2, userg3, userg4, userg5 " +
         "FROM teecurr2 " +
         "WHERE date = ? AND hr = ? AND time != ? AND " +
         "(userg1 != ? OR userg2 != ? OR userg3 != ? OR userg4 != ? OR userg5 != ?)");

      pstmt1.clearParameters();        // clear the parms and check player 1
      pstmt1.setLong(1, slotParms.date);
      pstmt1.setInt(2, slotParms.time / 100);
      pstmt1.setInt(3, slotParms.time);
      pstmt1.setString(4, "");
      pstmt1.setString(5, "");
      pstmt1.setString(6, "");
      pstmt1.setString(7, "");
      pstmt1.setString(8, "");

      rs = pstmt1.executeQuery();      // execute the prepared stmt

      while (rs.next()) {

         userg1 = rs.getString("userg1");
         userg2 = rs.getString("userg2");
         userg3 = rs.getString("userg3");
         userg4 = rs.getString("userg4");
         userg5 = rs.getString("userg5");

         //
         //  Count the number of guests already scheduled
         //
         if (!userg1.equals( "" )) {

            guests++;
         }
         if (!userg2.equals( "" )) {

            guests++;
         }
         if (!userg3.equals( "" )) {

            guests++;
         }
         if (!userg4.equals( "" )) {

            guests++;
         }
         if (!userg5.equals( "" )) {

            guests++;
         }
      }   // end of WHILE

      pstmt1.close();

      //
      //  If more then 9 guests scheduled (counting this request), then set error
      //
      if (guests > 9) {

         error = true;
      }

   } catch (Exception e) {

      Utilities.logError("Error checking for Green Bay Guests - verifySlot.checkGBguests: " + e.getMessage());        // log the error message

   } finally {

      try { rs.close(); }
      catch (Exception ignore) {}

      try { pstmt1.close(); }
      catch (Exception ignore) {}

   }

   return(error);

 }


/**
 //************************************************************************
 //
 //  checkMerionSched
 //
 //    Check if any of the players have a family member already scheduled today
 //    (only one player per membership, or family, each day)
 //
 //************************************************************************
 **/

 public static void checkMerionSched(parmSlot slotParms, Connection con)
         throws Exception {


   String courseName = "East";            // name of course for Merion

   try {

      slotParms.hit = false;
      slotParms.player = "";

      if (!slotParms.mNum1.equals( "" )) {                    // if player is a member and has a mNum

         slotParms.hit = checkMnum(slotParms.date, slotParms.time, courseName, slotParms.mNum1, con);

         if (slotParms.hit == true) {

            slotParms.player = slotParms.player1;              // get player for message
         }
      }

      if (slotParms.hit == false && !slotParms.mNum2.equals( "" )) {                    // if player is a member and has a mNum

         slotParms.hit = checkMnum(slotParms.date, slotParms.time, courseName, slotParms.mNum2, con);

         if (slotParms.hit == true) {

            slotParms.player = slotParms.player2;              // get player for message
         }
      }

      if (slotParms.hit == false && !slotParms.mNum3.equals( "" )) {                    // if player is a member and has a mNum

         slotParms.hit = checkMnum(slotParms.date, slotParms.time, courseName, slotParms.mNum3, con);

         if (slotParms.hit == true) {

            slotParms.player = slotParms.player3;              // get player for message
         }
      }

      if (slotParms.hit == false && !slotParms.mNum4.equals( "" )) {                    // if player is a member and has a mNum

         slotParms.hit = checkMnum(slotParms.date, slotParms.time, courseName, slotParms.mNum4, con);

         if (slotParms.hit == true) {

            slotParms.player = slotParms.player4;              // get player for message
         }
      }

      if (slotParms.hit == false && !slotParms.mNum5.equals( "" )) {                    // if player is a member and has a mNum

         slotParms.hit = checkMnum(slotParms.date, slotParms.time, courseName, slotParms.mNum5, con);

         if (slotParms.hit == true) {

            slotParms.player = slotParms.player5;              // get player for message
         }
      }

   }
   catch (Exception e) {

      throw new Exception("Error checking if Merion Player Already Scheduled - verifySlot.checkMerionSched " + e.getMessage());
   }

   return;
 }


/**
 //************************************************************************
 //
 //  checkMerionSchedm (for multiple tee time requests)
 //
 //    Check if any of the players have a family member already scheduled today
 //    (only one player per membership, or family, each day)
 //
 //************************************************************************
 **/

 public static boolean checkMerionSchedm(parmSlotm slotParms, Connection con) {


   boolean hit = false;

   String courseName = "East";            // name of course for Merion


   try {

      slotParms.player = "";

      if (!slotParms.mNum1.equals( "" )) {                    // if player is a member and has a mNum

         hit = checkMnum5(slotParms, courseName, slotParms.mNum1, con);

         if (hit == true) {

            slotParms.player = slotParms.player1;              // get player for message
         }
      }

      if (hit == false && !slotParms.mNum2.equals( "" )) {                    // if player is a member and has a mNum

         hit = checkMnum5(slotParms, courseName, slotParms.mNum2, con);

         if (hit == true) {

            slotParms.player = slotParms.player2;              // get player for message
         }
      }

      if (hit == false && !slotParms.mNum3.equals( "" )) {                    // if player is a member and has a mNum

         hit = checkMnum5(slotParms, courseName, slotParms.mNum3, con);

         if (hit == true) {

            slotParms.player = slotParms.player3;              // get player for message
         }
      }

      if (hit == false && !slotParms.mNum4.equals( "" )) {                    // if player is a member and has a mNum

         hit = checkMnum5(slotParms, courseName, slotParms.mNum4, con);

         if (hit == true) {

            slotParms.player = slotParms.player4;              // get player for message
         }
      }

      if (hit == false && !slotParms.mNum5.equals( "" )) {                    // if player is a member and has a mNum

         hit = checkMnum5(slotParms, courseName, slotParms.mNum5, con);

         if (hit == true) {

            slotParms.player = slotParms.player5;              // get player for message
         }
      }

      if (hit == false && !slotParms.mNum6.equals( "" )) {                    // if player is a member and has a mNum

         hit = checkMnum5(slotParms, courseName, slotParms.mNum6, con);

         if (hit == true) {

            slotParms.player = slotParms.player6;              // get player for message
         }
      }

      if (hit == false && !slotParms.mNum7.equals( "" )) {                    // if player is a member and has a mNum

         hit = checkMnum5(slotParms, courseName, slotParms.mNum7, con);

         if (hit == true) {

            slotParms.player = slotParms.player7;              // get player for message
         }
      }

      if (hit == false && !slotParms.mNum8.equals( "" )) {                    // if player is a member and has a mNum

         hit = checkMnum5(slotParms, courseName, slotParms.mNum8, con);

         if (hit == true) {

            slotParms.player = slotParms.player8;              // get player for message
         }
      }

      if (hit == false && !slotParms.mNum9.equals( "" )) {                    // if player is a member and has a mNum

         hit = checkMnum5(slotParms, courseName, slotParms.mNum9, con);

         if (hit == true) {

            slotParms.player = slotParms.player9;              // get player for message
         }
      }

      if (hit == false && !slotParms.mNum10.equals( "" )) {                    // if player is a member and has a mNum

         hit = checkMnum5(slotParms, courseName, slotParms.mNum10, con);

         if (hit == true) {

            slotParms.player = slotParms.player10;              // get player for message
         }
      }

      if (hit == false && !slotParms.mNum11.equals( "" )) {                    // if player is a member and has a mNum

         hit = checkMnum5(slotParms, courseName, slotParms.mNum11, con);

         if (hit == true) {

            slotParms.player = slotParms.player11;              // get player for message
         }
      }

      if (hit == false && !slotParms.mNum12.equals( "" )) {                    // if player is a member and has a mNum

         hit = checkMnum5(slotParms, courseName, slotParms.mNum12, con);

         if (hit == true) {

            slotParms.player = slotParms.player12;              // get player for message
         }
      }

      if (hit == false && !slotParms.mNum13.equals( "" )) {                    // if player is a member and has a mNum

         hit = checkMnum5(slotParms, courseName, slotParms.mNum13, con);

         if (hit == true) {

            slotParms.player = slotParms.player13;              // get player for message
         }
      }

      if (hit == false && !slotParms.mNum14.equals( "" )) {                    // if player is a member and has a mNum

         hit = checkMnum5(slotParms, courseName, slotParms.mNum14, con);

         if (hit == true) {

            slotParms.player = slotParms.player14;              // get player for message
         }
      }

      if (hit == false && !slotParms.mNum15.equals( "" )) {                    // if player is a member and has a mNum

         hit = checkMnum5(slotParms, courseName, slotParms.mNum15, con);

         if (hit == true) {

            slotParms.player = slotParms.player15;              // get player for message
         }
      }

      if (hit == false && !slotParms.mNum16.equals( "" )) {                    // if player is a member and has a mNum

         hit = checkMnum5(slotParms, courseName, slotParms.mNum16, con);

         if (hit == true) {

            slotParms.player = slotParms.player16;              // get player for message
         }
      }

      if (hit == false && !slotParms.mNum17.equals( "" )) {                    // if player is a member and has a mNum

         hit = checkMnum5(slotParms, courseName, slotParms.mNum17, con);

         if (hit == true) {

            slotParms.player = slotParms.player17;              // get player for message
         }
      }

      if (hit == false && !slotParms.mNum18.equals( "" )) {                    // if player is a member and has a mNum

         hit = checkMnum5(slotParms, courseName, slotParms.mNum18, con);

         if (hit == true) {

            slotParms.player = slotParms.player18;              // get player for message
         }
      }

      if (hit == false && !slotParms.mNum19.equals( "" )) {                    // if player is a member and has a mNum

         hit = checkMnum5(slotParms, courseName, slotParms.mNum19, con);

         if (hit == true) {

            slotParms.player = slotParms.player19;              // get player for message
         }
      }

      if (hit == false && !slotParms.mNum20.equals( "" )) {                    // if player is a member and has a mNum

         hit = checkMnum5(slotParms, courseName, slotParms.mNum20, con);

         if (hit == true) {

            slotParms.player = slotParms.player20;              // get player for message
         }
      }

      if (hit == false && !slotParms.mNum21.equals( "" )) {                    // if player is a member and has a mNum

         hit = checkMnum5(slotParms, courseName, slotParms.mNum21, con);

         if (hit == true) {

            slotParms.player = slotParms.player21;              // get player for message
         }
      }

      if (hit == false && !slotParms.mNum22.equals( "" )) {                    // if player is a member and has a mNum

         hit = checkMnum5(slotParms, courseName, slotParms.mNum22, con);

         if (hit == true) {

            slotParms.player = slotParms.player22;              // get player for message
         }
      }

      if (hit == false && !slotParms.mNum23.equals( "" )) {                    // if player is a member and has a mNum

         hit = checkMnum5(slotParms, courseName, slotParms.mNum23, con);

         if (hit == true) {

            slotParms.player = slotParms.player23;              // get player for message
         }
      }

      if (hit == false && !slotParms.mNum24.equals( "" )) {                    // if player is a member and has a mNum

         hit = checkMnum5(slotParms, courseName, slotParms.mNum24, con);

         if (hit == true) {

            slotParms.player = slotParms.player24;              // get player for message
         }
      }

      if (hit == false && !slotParms.mNum25.equals( "" )) {                    // if player is a member and has a mNum

         hit = checkMnum5(slotParms, courseName, slotParms.mNum25, con);

         if (hit == true) {

            slotParms.player = slotParms.player25;              // get player for message
         }
      }

   } catch (Exception e) {

      Utilities.logError("Error checking if Family Member Already Scheduled - verifySlot.checkMnum " + e.getMessage());

   }

   return(hit);

 }


/**
 //************************************************************************
 //
 //  checkMnum
 //
 //    Check if any of the players have a family member already scheduled today
 //
 //************************************************************************
 **/

 public static boolean checkMnum(long date, int time, String course, String mNum, Connection con)
         throws Exception {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   boolean hit = false;

   try {

      if (!mNum.equals( "" )) {

         pstmt = con.prepareStatement (
            "SELECT fb FROM teecurr2 " +
            "WHERE date = ? AND time != ? AND courseName = ? AND (mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?) " +
            "LIMIT 1");

         pstmt.clearParameters();        // clear the parms and check player 1
         pstmt.setLong(1, date);
         pstmt.setInt(2, time);
         pstmt.setString(3, course);
         pstmt.setString(4, mNum);
         pstmt.setString(5, mNum);
         pstmt.setString(6, mNum);
         pstmt.setString(7, mNum);
         pstmt.setString(8, mNum);

         rs = pstmt.executeQuery();

         if (rs.next()) hit = true;              // found one

         pstmt.close();
      }

   } catch (Exception e) {

       throw new Exception("Error checking if Family Member Already Scheduled - verifySlot.checkMnum " + e.getMessage());

   } finally {

       try { rs.close(); }
       catch (Exception ignore) {}

       try { pstmt.close(); }
       catch (Exception ignore) {}

   }

   return(hit);
 }


/**
 //************************************************************************
 //
 //  checkMnum5
 //
 //    Check if any of the players have a family member already scheduled today
 //
 //************************************************************************
 **/

 public static boolean checkMnum5(parmSlotm parm, String course, String mNum, Connection con)
         throws Exception {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   boolean hit = false;

   try {

      if (!mNum.equals( "" )) {

         pstmt = con.prepareStatement (
            "SELECT fb FROM teecurr2 " +
            "WHERE date = ? AND time != ? AND time != ? AND time != ? AND time != ? AND time != ? AND courseName = ? AND " +
            "(mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?) " +
            "LIMIT 1");

         pstmt.clearParameters();        // clear the parms and check player 1
         pstmt.setLong(1, parm.date);
         pstmt.setInt(2, parm.time1);
         pstmt.setInt(3, parm.time2);
         pstmt.setInt(4, parm.time3);
         pstmt.setInt(5, parm.time4);
         pstmt.setInt(6, parm.time5);
         pstmt.setString(7, course);
         pstmt.setString(8, mNum);
         pstmt.setString(9, mNum);
         pstmt.setString(10, mNum);
         pstmt.setString(11, mNum);
         pstmt.setString(12, mNum);

         rs = pstmt.executeQuery();

         if (rs.next()) hit = true;              // found one

         pstmt.close();

      }

   } catch (Exception e) {

      throw new Exception("Error checking if Family Member Already Scheduled - verifySlot.checkMnum " + e.getMessage());

   } finally {

         try { rs.close(); }
         catch (Exception ignore) {}

         try { pstmt.close(); }
         catch (Exception ignore) {}

   }

   return(hit);
 }


/**
 //************************************************************************
 //
 //  checkNewReq
 //
 //    Check if the tee time passed is new or already scheduled
 //
 //************************************************************************
 **/

 public static boolean checkNewReq(long date, int time, int fb, String course, Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";

   boolean newreq = false;

   try {

      pstmt = con.prepareStatement (
         "SELECT player1, player2, player3, player4, player5 FROM teecurr2 " +
         "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

      pstmt.clearParameters();
      pstmt.setLong(1, date);
      pstmt.setInt(2, time);
      pstmt.setInt(3, fb);
      pstmt.setString(4, course);

      rs = pstmt.executeQuery();

      if ( rs.next() ) {

         player1 = rs.getString( "player1" );
         player2 = rs.getString( "player2" );
         player3 = rs.getString( "player3" );
         player4 = rs.getString( "player4" );
         player5 = rs.getString( "player5" );

         if (player1.equals("") && player2.equals("") && player3.equals("") && player4.equals("") && player5.equals("")) {

            newreq = true;              // no players - new request
         }
      }

      pstmt.close();

   } catch (Exception ignore) {

   } finally {

      try { rs.close(); }
      catch (Exception ignore) {}

      try { pstmt.close(); }
      catch (Exception ignore) {}

   }

   return(newreq);

 }


/**
 //************************************************************************
 //
 //  Merion - Check for max allowed tee times per mship (East course only).
 //
 //    Called by:  Member_slot & Proshop_slot(m)
 //
 //    Check teecurr for the number of times already scheduled for this family.
 //
 //      This will count the following for all members of the family:
 //
 //         - all guest times
 //         - all member times during weekdays (weekend member times not counted)
 //
 //************************************************************************
 **/

 public static boolean checkMerionGres(parmSlot slotParms, Connection con) {


   boolean error = false;

   String courseName = "East";        // Only check this Merion course

   if (slotParms.course.equals( courseName )) {      // only check if East course

      PreparedStatement pstmt1 = null;
      PreparedStatement pstmt2 = null;
      ResultSet rs = null;
      ResultSet rs2 = null;

      String player = "";
      String mNum = "";
      String user = "";

      int max = 5;                       // max allowed guest times per family for Merion
      int i = 0;
      int count = 0;

      String [] usergA = new String [5];     // array to hold the members' usernames

      usergA[0] = slotParms.userg1;                  // copy userg values into array
      usergA[1] = slotParms.userg2;
      usergA[2] = slotParms.userg3;
      usergA[3] = slotParms.userg4;
      usergA[4] = slotParms.userg5;


      //
      //  Get each member with a guest in this tee time
      //
      try {

         loop1:
         for (i=0; i<5; i++) {       // check all 5 possible guests

            if (!usergA[i].equals( "" )) {       // if guest assigned to a member

               count = 0;                        // init counter

               //
               //  Get member name and member number
               //
               pstmt1 = con.prepareStatement (
                  "SELECT name_last, name_first, name_mi, memNum " +
                  "FROM member2b " +
                  "WHERE username = ?");

               pstmt1.clearParameters();
               pstmt1.setString(1, usergA[i]);
               rs = pstmt1.executeQuery();

               if (rs.next()) {

                  StringBuffer mem_name = new StringBuffer(rs.getString("name_first"));  // get first name

                  String mi = rs.getString("name_mi");                                   // middle initial
                  if (!mi.equals( "" )) {
                     mem_name.append(" ");
                     mem_name.append(mi);
                  }
                  mem_name.append(" " + rs.getString("name_last"));                     // last name

                  player = mem_name.toString();                                         // convert to one string

                  mNum = rs.getString("memNum");       // Get mNum
               }
               pstmt1.close();

               if (!mNum.equals( "" )) {               // if mNum provided

                  //
                  //  Count how many other WEEKDAY tee times this family (mNum) has scheduled (member or guest times)
                  //
                  pstmt2 = con.prepareStatement (
                     "SELECT mNum1 " +
                     "FROM teecurr2 " +
                     "WHERE date != ? AND courseName = ? AND day != 'Saturday' AND day != 'Sunday' AND " +
                     "(mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?)");

                  pstmt2.clearParameters();
                  pstmt2.setLong(1, slotParms.date);
                  pstmt2.setString(2, courseName);
                  pstmt2.setString(3, mNum);
                  pstmt2.setString(4, mNum);
                  pstmt2.setString(5, mNum);
                  pstmt2.setString(6, mNum);
                  pstmt2.setString(7, mNum);

                  rs2 = pstmt2.executeQuery();

                  while (rs2.next()) {

                     count++;
                  }

                  pstmt2.close();

                  //
                  //  Now count how many WEEKEND guest times this family (mNum) has scheduled
                  //
                  pstmt1 = con.prepareStatement (
                     "SELECT username " +
                     "FROM member2b " +
                     "WHERE memNum = ?");

                  pstmt1.clearParameters();
                  pstmt1.setString(1, mNum);
                  rs = pstmt1.executeQuery();

                  while (rs.next()) {                        // get each family member

                     user = rs.getString(1);

                     //
                     //  Find all other guest times for this user
                     //
                     pstmt2 = con.prepareStatement (
                        "SELECT userg1 " +
                        "FROM teecurr2 " +
                        "WHERE date != ? AND courseName = ? AND (day = 'Saturday' OR day = 'Sunday') AND " +
                        "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?)");

                     pstmt2.clearParameters();
                     pstmt2.setLong(1, slotParms.date);
                     pstmt2.setString(2, courseName);
                     pstmt2.setString(3, user);
                     pstmt2.setString(4, user);
                     pstmt2.setString(5, user);
                     pstmt2.setString(6, user);
                     pstmt2.setString(7, user);

                     rs2 = pstmt2.executeQuery();

                     while (rs2.next()) {

                        count++;
                     }

                     pstmt2.close();

                  }
                  pstmt1.close();
               }

               //
               //   Has this family already used the max guest times?
               //
               if (count >= max) {

                  error = true;                // yes

                  slotParms.player = player;   // save player name for message

                  break loop1;                 // exit loop
               }
            }
         }        // end of loop1

      } catch (Exception e) {

          Utilities.logError("Error checking for Merion Guest Times - verifySlot.checkMerionGres: " + e.getMessage());

      } finally {

          try { rs.close(); }
          catch (Exception ignore) {}

          try { rs2.close(); }
          catch (Exception ignore) {}

          try { pstmt1.close(); }
          catch (Exception ignore) {}

          try { pstmt2.close(); }
          catch (Exception ignore) {}

      }

   } // end if East course

   return(error);

 }


/**
 //************************************************************************
 //
 //  Merion - Check for more than x guest times in one hour (East course only).
 //
 //    Called by:  Member_slot & Proshop_slot(m)
 //
 //    Check teecurr for the number of guest times requested during the hour.
 //
 //
 //************************************************************************
 **/

 public static boolean checkMerionG(parmSlot slotParms, Connection con) {


   PreparedStatement pstmt1 = null;
   ResultSet rs = null;

   boolean error = false;

   String courseName = "East";        // Only check this course

   int guests = 0;                   // # of guest times
   int max = 4;                      // max # of guest times allowed on W/E days !!!

   if (slotParms.course.equals( courseName )) {      // only check if East course

      //
      //  Count all guest times already scheduled during the hour specified for this tee time (exclude this time)
      //
      try {

         pstmt1 = con.prepareStatement (
            "SELECT userg1 " +
            "FROM teecurr2 " +
            "WHERE date = ? AND hr = ? AND time != ? AND courseName = ? AND " +
            "(userg1 != '' OR userg2 != '' OR userg3 != '' OR userg4 != '' OR userg5 != '' OR " +
            "(player1 != '' AND username1 = ''))");

         pstmt1.clearParameters();        // clear the parms and check player 1
         pstmt1.setLong(1, slotParms.date);
         pstmt1.setInt(2, slotParms.time / 100);
         pstmt1.setInt(3, slotParms.time);
         pstmt1.setString(4, courseName);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         while (rs.next()) {

            guests++;

         }   // end of WHILE

         pstmt1.close();

         //
         //  If this time would exceed the max, then set error
         //
         if (guests >= max) {

            error = true;
         }

      } catch (Exception e) {

         Utilities.logError("Error checking for Merion Guest Times - verifySlot.checkMerionG: " + e.getMessage());

      } finally {

         try { rs.close(); }
         catch (Exception ignore) {}

         try { pstmt1.close(); }
         catch (Exception ignore) {}

      }

   }

   return(error);
 }


 public static boolean checkGreenwichMinPlayers(parmSlot slotParms, int xcount) {

    boolean error = false;
    if ((slotParms.day.equals( "Saturday" ) || slotParms.day.equals( "Sunday" ) ||
        slotParms.date == Hdate1 || slotParms.date == Hdate2 || slotParms.date == Hdate3 || slotParms.date == Hdate9) &&
        ( (slotParms.time > 755 && slotParms.time < 1125) || (slotParms.time > 1235 && slotParms.time < 1357) )) {

        if (xcount == -1) {
            error = slotParms.player3.equals("");
        } else {
            error = ((slotParms.members + slotParms.guests + xcount) < 3);
        }
    }

    return(error);

 }


 //************************************************************************
 //  adjustTime - receives a time value (hhmm) and adjusts it for the club's
 //               specified time zone.
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
 //
 //  logError - logs system error messages to a text file
 //
 //************************************************************************

 public static void logError(String msg) {

    // now passing this call to Utilities
    Utilities.logError( msg );

 }  // end of logError


 //************************************************************************
 //
 //  checkProOnlyMOT - verifys that players don't have a Pro-Only mode of transportation selected
 //
 //************************************************************************
 public static boolean checkProOnlyMOT(parmSlot slotParms, parmCourse courseParms, Connection con) {

     try {

         // if player is not empty and has changed from old player, make sure they are not using a Pro Only mode of trans unless it's their default
         // guests may not have Pro Only tmodes unless set by proshop
         if (!slotParms.player1.equals("") && !slotParms.player1.equalsIgnoreCase("x") && (!slotParms.player1.equalsIgnoreCase(slotParms.oldPlayer1) || !slotParms.p1cw.equalsIgnoreCase(slotParms.oldp1cw))) {

             for (int i=0; i < courseParms.tmode_limit; i++) {

                if (courseParms.tOpt[i] == 1 && slotParms.p1cw.equals(courseParms.tmodea[i])) {

                    if (slotParms.user1.equals("")) {       // no db connection required

                        slotParms.player = slotParms.player1;
                        return false;
                    } else if (!checkCurMOT(slotParms.user1, slotParms.p1cw, con)) {    // only want to make db connection if neccessary

                        slotParms.player = slotParms.player1;
                        return false;
                    }
                }
             }
         }
         if (!slotParms.player2.equals("") && !slotParms.player2.equalsIgnoreCase("x") && (!slotParms.player2.equalsIgnoreCase(slotParms.oldPlayer2) || !slotParms.p2cw.equalsIgnoreCase(slotParms.oldp2cw))) {

             for (int i=0; i < courseParms.tmode_limit; i++) {

                if (courseParms.tOpt[i] == 1 && slotParms.p2cw.equals(courseParms.tmodea[i])) {

                    if (slotParms.user2.equals("")) {       // no db connection required

                        slotParms.player = slotParms.player2;
                        return false;
                    } else if (!checkCurMOT(slotParms.user2, slotParms.p2cw, con)) {    // only want to make db connection if neccessary

                        slotParms.player = slotParms.player2;
                        return false;
                    }
                }
             }
         }
         if (!slotParms.player3.equals("") && !slotParms.player3.equalsIgnoreCase("x") && (!slotParms.player3.equalsIgnoreCase(slotParms.oldPlayer3) || !slotParms.p3cw.equalsIgnoreCase(slotParms.oldp3cw))) {

             for (int i=0; i < courseParms.tmode_limit; i++) {

                if (courseParms.tOpt[i] == 1 && slotParms.p3cw.equals(courseParms.tmodea[i])) {

                    if (slotParms.user3.equals("")) {       // no db connection required

                        slotParms.player = slotParms.player3;
                        return false;
                    } else if (!checkCurMOT(slotParms.user3, slotParms.p3cw, con)) {    // only want to make db connection if neccessary

                        slotParms.player = slotParms.player3;
                        return false;
                    }
                }
             }
         }
         if (!slotParms.player4.equals("") && !slotParms.player4.equalsIgnoreCase("x") && (!slotParms.player4.equalsIgnoreCase(slotParms.oldPlayer4) || !slotParms.p4cw.equalsIgnoreCase(slotParms.oldp4cw))) {

             for (int i=0; i < courseParms.tmode_limit; i++) {

                if (courseParms.tOpt[i] == 1 && slotParms.p4cw.equals(courseParms.tmodea[i])) {

                    if (slotParms.user4.equals("")) {       // no db connection required

                        slotParms.player = slotParms.player4;
                        return false;
                    } else if (!checkCurMOT(slotParms.user4, slotParms.p4cw, con)) {    // only want to make db connection if neccessary

                        slotParms.player = slotParms.player4;
                        return false;
                    }
                }
             }
         }
         if (!slotParms.player5.equals("") && !slotParms.player5.equalsIgnoreCase("x") && (!slotParms.player5.equalsIgnoreCase(slotParms.oldPlayer5) || !slotParms.p5cw.equalsIgnoreCase(slotParms.oldp5cw))) {

             for (int i=0; i < courseParms.tmode_limit; i++) {

                if (courseParms.tOpt[i] == 1 && slotParms.p5cw.equals(courseParms.tmodea[i])) {

                    if (slotParms.user5.equals("")) {       // no db connection required

                        slotParms.player = slotParms.player5;
                        return false;
                    } else if (!checkCurMOT(slotParms.user5, slotParms.p5cw, con)) {    // only want to make db connection if neccessary

                        slotParms.player = slotParms.player5;
                        return false;
                    }
                }
             }
         }

     } catch (Exception ignore) {

     }

     return true;
 }

 //************************************************************************
 //
 //  checkCurMOT - returns true if tmodea matches the database wc entry for passed username, false otherwise
 //
 //************************************************************************
 public static boolean checkCurMOT(String username, String tmodea, Connection con) {

     Statement stmt = null;
     ResultSet rs = null;

     try {

         stmt = con.createStatement();
         rs = stmt.executeQuery("SELECT wc FROM member2b WHERE username='" + username + "'");

         if (rs.next()) {

             if (rs.getString("wc").equalsIgnoreCase(tmodea)) {
                 return true;
             } else {
                 return false;
             }
         }

     } catch (Exception ignore) {

     } finally {

         try { rs.close(); }
         catch (Exception ignore) {}

         try { stmt.close(); }
         catch (Exception ignore) {}

     }

     return false;
 }

/*
 public static parmSlot get1SlotFromMslot(parmSlotm parm, int group) {


    parmSlot parm1 = new parmSlot();          // allocate a parm block for returning a single tee time

    //
    //  Setup the new single parm block
    //
    parm1.date = parm.date;
    parm1.mm = parm.mm;
    parm1.yy = parm.yy;
    parm1.dd = parm.dd;
    parm1.course = parm.course;
    parm1.p5 = parm.p5;
    parm1.day = parm.day;
    parm1.fb = parm.fb;
    parm1.ind = parm.ind;
    parm1.sfb = parm.sfb;
    parm1.club = parm.club;


    if (group == 1) {

        //
        // Use the values for first group
        //

        parm1.time = parm.time1;
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
        parm1.mstype1 = parm.mstype1;
        parm1.mstype2 = parm.mstype2;
        parm1.mstype3 = parm.mstype3;
        parm1.mstype4 = parm.mstype4;
        parm1.mstype5 = parm.mstype5;
        parm1.mNum1 = parm.mNum1;
        parm1.mNum2 = parm.mNum2;
        parm1.mNum3 = parm.mNum3;
        parm1.mNum4 = parm.mNum4;
        parm1.mNum5 = parm.mNum5;
        parm1.oldPlayer1 = parm.oldPlayer1;
        parm1.oldPlayer2 = parm.oldPlayer2;
        parm1.oldPlayer3 = parm.oldPlayer3;
        parm1.oldPlayer4 = parm.oldPlayer4;
        parm1.oldPlayer5 = parm.oldPlayer5;

        if (!parm.p5.equals( "Yes" )) {

                parm1.player5 = "";
                parm1.user5 = "";
                parm1.mship5 = "";
                parm1.mtype5 = "";
                parm1.mstype5 = "";
                parm1.mNum5 = "";

        }

    } else if (group == 2) {

        //
        // Use the values for second group
        //

        parm1.time = parm.time2;

        if (parm.p5.equals( "Yes" )) {

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
            parm1.mstype1 = parm.mstype6;
            parm1.mstype2 = parm.mstype7;
            parm1.mstype3 = parm.mstype8;
            parm1.mstype4 = parm.mstype9;
            parm1.mstype5 = parm.mstype10;
            parm1.mNum1 = parm.mNum6;
            parm1.mNum2 = parm.mNum7;
            parm1.mNum3 = parm.mNum8;
            parm1.mNum4 = parm.mNum9;
            parm1.mNum5 = parm.mNum10;

        } else {

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
            parm1.mstype1 = parm.mstype5;
            parm1.mstype2 = parm.mstype6;
            parm1.mstype3 = parm.mstype7;
            parm1.mstype4 = parm.mstype8;
            parm1.mNum1 = parm.mNum5;
            parm1.mNum2 = parm.mNum6;
            parm1.mNum3 = parm.mNum7;
            parm1.mNum4 = parm.mNum8;
            parm1.player5 = "";
            parm1.user5 = "";
            parm1.mship5 = "";
            parm1.mtype5 = "";
            parm1.mstype5 = "";
            parm1.mNum5 = "";

        }

    } else if (group == 3) {

        //
        // Use the values for third group
        //

        parm1.time = parm.time3;

        if (parm.p5.equals( "Yes" )) {

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
            parm1.mstype1 = parm.mstype11;
            parm1.mstype2 = parm.mstype12;
            parm1.mstype3 = parm.mstype13;
            parm1.mstype4 = parm.mstype14;
            parm1.mstype5 = parm.mstype15;
            parm1.mNum1 = parm.mNum11;
            parm1.mNum2 = parm.mNum12;
            parm1.mNum3 = parm.mNum13;
            parm1.mNum4 = parm.mNum14;
            parm1.mNum5 = parm.mNum15;

        } else {

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
            parm1.mstype1 = parm.mstype9;
            parm1.mstype2 = parm.mstype10;
            parm1.mstype3 = parm.mstype11;
            parm1.mstype4 = parm.mstype12;
            parm1.mNum1 = parm.mNum9;
            parm1.mNum2 = parm.mNum10;
            parm1.mNum3 = parm.mNum11;
            parm1.mNum4 = parm.mNum12;
            parm1.player5 = "";
            parm1.user5 = "";
            parm1.mship5 = "";
            parm1.mtype5 = "";
            parm1.mstype5 = "";
            parm1.mNum5 = "";

        }

    } else if (group == 4) {

        //
        // Use the values for fourth group
        //

        parm1.time = parm.time4;

        if (parm.p5.equals( "Yes" )) {

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
            parm1.mstype1 = parm.mstype16;
            parm1.mstype2 = parm.mstype17;
            parm1.mstype3 = parm.mstype18;
            parm1.mstype4 = parm.mstype19;
            parm1.mstype5 = parm.mstype20;
            parm1.mNum1 = parm.mNum16;
            parm1.mNum2 = parm.mNum17;
            parm1.mNum3 = parm.mNum18;
            parm1.mNum4 = parm.mNum19;
            parm1.mNum5 = parm.mNum20;

        } else {

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
            parm1.mstype1 = parm.mstype13;
            parm1.mstype2 = parm.mstype14;
            parm1.mstype3 = parm.mstype15;
            parm1.mstype4 = parm.mstype16;
            parm1.mNum1 = parm.mNum13;
            parm1.mNum2 = parm.mNum14;
            parm1.mNum3 = parm.mNum15;
            parm1.mNum4 = parm.mNum16;
            parm1.player5 = "";
            parm1.user5 = "";
            parm1.mship5 = "";
            parm1.mtype5 = "";
            parm1.mstype5 = "";
            parm1.mNum5 = "";
        }

    } else if (group == 5) {

        //
        // Use the values for fifth group
        //

        parm1.time = parm.time5;

        if (parm.p5.equals( "Yes" )) {

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
            parm1.mstype1 = parm.mstype21;
            parm1.mstype2 = parm.mstype22;
            parm1.mstype3 = parm.mstype23;
            parm1.mstype4 = parm.mstype24;
            parm1.mstype5 = parm.mstype25;
            parm1.mNum1 = parm.mNum21;
            parm1.mNum2 = parm.mNum22;
            parm1.mNum3 = parm.mNum23;
            parm1.mNum4 = parm.mNum24;
            parm1.mNum5 = parm.mNum25;

        } else {

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
            parm1.mstype1 = parm.mstype17;
            parm1.mstype2 = parm.mstype18;
            parm1.mstype3 = parm.mstype19;
            parm1.mstype4 = parm.mstype20;
            parm1.mNum1 = parm.mNum17;
            parm1.mNum2 = parm.mNum18;
            parm1.mNum3 = parm.mNum19;
            parm1.mNum4 = parm.mNum20;
            parm1.player5 = "";
            parm1.user5 = "";
            parm1.mship5 = "";
            parm1.mtype5 = "";
            parm1.mstype5 = "";
            parm1.mNum5 = "";
        }

    } // end which group

 } // end get1SlotFromMslot
*/

} // end of verifySlot class

