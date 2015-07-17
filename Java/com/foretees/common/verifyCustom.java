/***************************************************************************************
 *   verifyCustom:  This servlet will provide some Custom tee time request processing methods.
 *
 *       called by:  Proshop_slot
 *                   Proshop_slotm
 *                   Proshop_lott
 *                   Member_slot
 *                   Member_slotm
 *                   Member_lott
 *
 *
 *   created:  6/09/2006   Bob P.
 *
 *
 *   last updated:
 *
 *      7/29/10  Southern Hills CC (southernhillscc) - Tee times must have at least 1 member and 2 guests during specific times on Friday during the year (case 1689).
 *      7/23/10  Rolling Hills CO (rhillscc) - changes to days in advance custom
 *      7/22/10  TPC Rivers Bend (tpcriversbend) - Added "CHARTER CORPORATE" mship to filter in checkTPCmship.
 *      7/20/10  Perry Park CC (perryparkcc) - Added checkPerryParkMship() method to check whether or not a given member has an mship of 'Annual Pass Member'
 *      7/19/10  Wee Burn CC (weeburn) - Added checkWeeburnWFGGuests method for use with checkCustomsGst custom guest restriction (case 1681).
 *      6/24/10  Oahu CC (oahucc) - no x's allowed weekends before 11am.
 *      6/16/10  Rolling Hills CO - custom restriction for Secondary members on Sunday morning (case 1853).
 *      6/16/10  Rolling Hills CO - custom days in advance checks (case 1852).
 *      6/07/10  Hazeltine2010 - add checkHazeltine2010 to process member restriction of 2 tee times per month per course. 
 *      5/21/10  Cape Cod National - Updated custom to check individual hours during the off-season (case 1828).
 *      5/20/10  Cape Cod National - add checks for the number of Hotel guest times per day/hour (case 1828).
 *      5/14/10  Edison Club - added min. player check to checkCustoms1 (case 1834).
 *      5/12/10  Tamarisk (tamarisk) - Update checkTamariskAdvTime custom to run from 11/1 to 4/30 each year (case 1657).
 *      5/05/10  MN Valley - add custom to check for an adult in any group with 1 or more juniors (case 1676).
 *      5/05/10  Oakley CC - must be at least 2 players in every tee time (case 1835).
 *      4/26/10  Morris Country GC (morriscgc) - Added custom to check yearly weekday/weekend round limits (case 1794).
 *      4/21/10  Sonnenalp - change some guest rates.
 *      4/20/10  Ramsey - add custom for 3-some times (case 1816).
 *      4/19/10  Portland GC - add an exception in checkPGCwalkup for 9-Hole Ladies (Wed mornings) - added the 7:30 - 8:22 times.
 *      4/16/10  Wollaston GC - add a method (checkWollastonMon) to check for Monday so we know to block member access (case 1819).
 *      4/08/10  Portland GC - add an exception in checkPGCwalkup for 9-Hole Ladies (Wed mornings).
 *      4/01/10  Yankee Hill & Druid Hills - check for at least 2 players in every tee time (cases 1803, 1814).
 *      4/01/10  Longue Vue - add check2LongueVue to check for 2-some times (case 1798).
 *      3/26/10  Canterbury - check for guests in any request beyond the normal 7 days in advance (case 1800).
 *      3/25/10  Interlachen - check for max of 3 guest times on Fridays from 10:00 to 11:30 (case 1791).
 *      3/24/09  Los Coyotes CC (loscoyotes) - Updated times for custom days in advance (case 1740).
 *      1/29/10  Include event signups for CC of Naples custom membership type quota restriction (case 1704).
 *      1/08/10  Meadow Club - check for 3-some times (case 1761).
 *      1/06/10  Oakmont - change the max # of guests per month in checkOakmontGuestQuota from 10 to 8 (case 1364).
 *     12/28/09  Ocean Reef - add "Multi-Game Card" mship type to their custom for days in advance (case 1731).
 *     12/14/09  Oahu CC - add custom to check for at least 3 players on weekend mornings (case 1757).
 *     12/04/09  Add custom checks for Dependents for Cherokee CC (case 1690).
 *     11/09/09  Update checkTamariskAdvTime to check the days in advance and time of day.
 *     11/08/09  CC of Naples - Associate B mships can only access times after 12:30 in season (case 1704).
 *     11/05/09  Add custom for Los Coyotes to check specific tee times and limit them to Primary members only (case 1740).
 *     11/04/09  Make some mship changes to checkOceanReefMship (case 1731).
 *     10/27/09  Update checkPGCwalkup for Portland GC to update the Friday walk-up times (case 1738).
 *     10/21/09  Ocean Reef - add checkOceanReefMship to check days in advance for user (case 1731).
 *     10/14/09  Changes to checkDesertForest (case 1694).
 *      9/04/09  General code clean up - fixed wellesleyGuests
 *      9/03/09  Beverly - add custom guest quota (case 1449).
 *      9/01/09  Added check for Desert Forest 2-some times (case 1694).
 *      8/27/09  Fox Den CC - only allow members to specify one X on weekends (case 1710).
 *      8/19/09  Mid Pacific CC (midpacific) - Added custom restriction checking to checkCustoms1 and checkCustomsGst
 *      7/02/09  Tamarisk CC (tamariskcc) - Added custom to check if the current tee time is before 8:00am or after 11:07am on a given day (case 1657).
 *      6/23/09  Added checkPMarshMNum() custom to restrict any given family number from booking more than 2 times in a given day (case 1620).
 *      6/23/09  Tweaks for checkBonnieBriarMships() method SQL queries
 *      6/23/09  Applied changes for 2009 season (Tue/Wed/Thu 3 guest rounds/hr except 12-2pm, change mem-labor day 1 guest round/hr to 12-2 from 11-1) for Minikahda (case 1027).
 *      6/19/09  Added checkBonnieBriarMships() to handle checking that Associate/Sports members only play 1 weekend round per month (case 1655).
 *      6/18/09  Added checkKinsaleGuests() to handle a customized guest restriction for them (case 1628).
 *      6/08/09  Update checkPGCwalkup for Portland GC to update the Wednesday Two Ball times (case 1527).
 *      6/05/09  Interlachen - only allow members with a subtype of "Member guest Pass" to use the guest type
 *                              of Guest-Centennial (case 1686).
 *      6/01/09  Update checkBaltusrolGuestQuota for Baltusrol - increase max guests from 3 to 5 (case 1455).
 *      5/15/09  Update checkPGCwalkup for Portland GC to update the Friday Ladies times (case 1527).
 *      5/07/09  Forest Highlands - add checkFHBlueCarts to check limits on cart usage (case 1670).
 *      4/27/09  Add checkCustomsGst and checkTPCmems for custom guest restrictions.
 *      4/23/09  North Oaks - add custom junior restrictions (case 1662).
 *      4/15/09  Tweaks to checkMayfieldSR for 2009 dates
 *      3/27/09  Woodway CC - Add checkWoodwayGuests - mship Restricted Golf may not have guests on Fri/Sat/Sun/Holidays (case 1510).
 *      3/17/09  Tweaks to checkMayfieldSR for 2009 dates
 *      2/11/09  Add checkHazeltineInvite to check member sub-type for Mens Invitational Event signup (case 1585).
 *      1/26/09  Update checkPGCwalkup for Portland GC to update the Tues Ladies times (case 1527).
 *     01/09/09  Add checkElmcrestJrs method to restrict dependents w/o an adult (case 1601).
 *     01/09/09  Palm Valley CC - change checkPVCCmships so it returns the name of the player being restricted instead of always player1
 *     11/21/08  Remove checkRivercrest and checkRivercrestDay method for custom 3-some times.
 *     10/21/08  Jonathans Landing - add checkJLGCmships to process Membership restrictions (case 1329). 
 *     10/20/08  Palm Valley CC - add checkPVCCmships to process Membership restrictions (case 1242). 
 *     10/15/08  Tualatin - add checkTualatinJr to process Junior restrictions (case 1473). 
 *     10/15/08  Add checkCustoms1 method to provide a common method to check for individual customs.  This can be called by both
 *               Member and Proshop servlets (slot and slotm) to process custom restrictions, etc. 
 *     10/10/08  Add checkPattersonGuests for Patterson Club (case 1470).
 *      9/17/08  Remove Wednesday conditional in checkPGCwalkupeckPGCwalkup
 *      8/25/08  Add checkPGCwalkup for Portland GC to check for Walk-Up Only times (case 1527).
 *      8/13/08  Update checkTCCguests to NOT include Tournament guests for guest counts.
 *      6/26/08  Add checkBaltusrolGuestQuota for Baltusrol (case 1455).
 *      6/11/08  Add checkBelleMeadeFems for Belle Meade female restriction on Sundays (case 1496).
 *      6/07/08  Add getLCGender for Los Coyotes (case 1482).
 *      6/06/08  Add checkRivercrest and checkRivercrestDay method for custom 3-some times.
 *      5/16/08  Move checkInUseMN to verifySlot as this is now standard code.
 *      5/16/08  Tamarack - add removeHist method to delete a lottery request history entry when member removed from tee time (case 1479).
 *      5/05/08  Beverly GC - add beverlyGuests for custom guest quota (case 1449).
 *      5/04/08  Minikahda - change number of guests per hour between Mem Day and Labor Day (case 1027).
 *      5/03/08  Sonnenalp - change some guest rates (case 1461).
 *      4/15/08  Wellesley - change the mship checks in wellesleyGuests (add Limited).
 *      4/10/08  Added checkDorsetFC for 2-some check for Case# 1440
 *      4/10/08  Added checkMayfieldSR for 2-some check for Case# 1424
 *      4/10/08  TheCC (Brookline) - updated checkTheCC to include new 2-some times Case# 1436
 *      3/27/08  Sonnenalp addGuestRates - change the date range for the high season.
 *      3/14/08  Los Coyotes - add checkLCSpouses to check if spouses together more than 3 days in advance (case 1397).
 *      1/24/08  Oakmont - add checkOakmontGuestQuota to check max number of guest times per member (case #1364).
 *     11/26/07  Eagle Creek - Add checkEagleCreekSocial method for Case #1284
 *     10/12/07  checkInUseMn - check if user is restricted from tee times before using as alternative tee time.
 *      9/25/07  Add checkMediterraSports for checking Sports membership quotas (fixed 10/3/07)
 *      8/29/07  Update checkTCCguests to NOT include event times for guest counts.
 *      8/21/07  Update checkTheCC to include 2-some times for 9/04 - 10/31.
 *      7/23/07  Update checkTheCC to include 8:00 for 2-some time.
 *      7/17/07  Add checkWilmington - check for special mship subtypes so they can be marked on pro tee sheet (case #1204).
 *      6/29/07  Add checkMerrill - check for special mships so they can be marked on pro tee sheet (case #1183).
 *      6/21/07  Add checkNewCan for New Canaan - check for 2-some time.
 *      5/29/07  Add addGuestRates for Sonnenalp - add guest rate info to tee time for all guests for tee sheet (case #1070).
 *      4/25/07  Add checkMiniGuestTimes for Minikahda CC - check for 2 guest times per hour (case #1027).
 *      4/24/07  Add checkGreenwich for Greenwich CC - check for 2-some time.
 *      4/12/07  Add checkTCCguests for The CC (Brookline) - check guest quotas (case #1087).
 *      4/04/07  Add checkTheCC for The CC (Brookline) - check for 2-some time.
 *      3/29/07  Add checkInUseMn for CC of Jackson - if one time of a multi request is busy,
 *                  check for other available times (case 1074).
 *      2/15/07  Add checkAwbreyDependents method to check for Juniors w/o and adult.
 *      2/09/07  Add checkWilmingtonGuests for Wilmington.
 *     12/20/06  Add checkElNiguelDependents for El Niguel.
 *     11/28/06  Add checkInUseMc for Long Cove - if one time of a multi request is busy, skip it
 *                    and return the others.
 *     11/17/06  Riverside - add custom guest restriction (no more than 12 on Sunday mornings).
 *      9/05/06  Wellesley - verify the parms before processing to prevent exception.
 *      7/26/06  Bearpath - add custom restriction to check member types.
 *
 ***************************************************************************************
 */


package com.foretees.common;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.mail.internet.*;
import javax.mail.*;
import javax.activation.*;


public class verifyCustom {

   private static String rev = ProcessConstants.REV;

   //
   //  Holidays for custom codes that may require them
   //
   //   Must change them in ProcessConstants...
   //     also, refer to SystemUtils !!!!!!!!!
   //
   private static long Hdate1 = ProcessConstants.memDay;     // Memorial Day
   private static long Hdate2 = ProcessConstants.july4;      // 4th of July - Monday
   private static long Hdate2b = ProcessConstants.july4b;    // 4th of July - other
   private static long Hdate3 = ProcessConstants.laborDay;   // Labor Day
   private static long Hdate7 = ProcessConstants.tgDay;      // Thanksgiving Day
   private static long Hdate8 = ProcessConstants.colDay;     // Columbus Day
     
   private static long Hdate4 = ProcessConstants.Hdate4;     // October 1st
   private static long Hdate5 = ProcessConstants.Hdate5;     // Junior Fridays Start (start on Thurs.)
   private static long Hdate6 = ProcessConstants.Hdate6;     // Junior Fridays End  (end on Sat.)
     
     
 
/**
 //************************************************************************
 //
 //  checkCustoms1 
 //
 //      This method provides a common mechanism to process individual custom 
 //      restrictions, etc. Each Member and Proshop verify method can call this one
 //      method to check ALL custom restrictions that do not involve Guests.
 //      This is called early in the verification process - before guests are assigned.
 //
 //      This method will process each custom based on the club.  A String is
 //      returned to indicate if it hit on an error condition.  The string will
 //      contain the specific error message for the response. 
 //
 //      Any other pertinent information is returned in slotParms.
 //
 //************************************************************************
 **/

 public static String checkCustoms1(parmSlot slotParms, Connection con) {


   String returnMsg = "";
   
   boolean error = false;
    
   //
   //  Process according to the club
   //

   if (slotParms.club.equals("tualatincc")) {     // TUALATIN CC
      
      //
      //  Tualatin - Check for any Juniors without an Adult
      //
      error = checkTualatinJr(slotParms, con);
      
      if (error == true) {         // if we hit an error
         
         returnMsg = "<BR><BR><H3>Juniors Not Allowed Without An Adult</H3>" +
                     "<BR>Sorry, but there must be at least one adult member in the group" +
                     "<BR>when one or more juniors are included between 11:00 AM and 2:00 PM." +
                     "<BR><BR>Please include an adult in the group, or try another time of the day.";
      }

   } else if (slotParms.club.equals("mnvalleycc")) {     // MN VALLEY CC
      
      //
      //  MN Valley - Check for any Juniors without an Adult
      //
      error = checkMNValleyJrs(slotParms);
      
      if (error == true) {         // if we hit an error
         
         returnMsg = "<BR><BR><H3>Juniors Not Allowed Without An Adult</H3>" +
                     "<BR>Sorry, but there must be at least one adult member in the group when" +
                     "<BR>one or more juniors 11 - 14 are included on Saturday or Sunday afternoons." +
                     "<BR><BR>Please include an adult in the group, or try another time of the day.";
      }

   } else if (slotParms.club.equals("cherokeecountryclub")) {     // CHEROKEE CC
      
      error = checkCherokeeJr(slotParms, con);
      
      if (error == true) {         // if we hit an error
         
         returnMsg = "<BR><BR><H3>Dependents Not Allowed Without An Adult</H3>" +
                     "<BR>Dependents that aren't accompanied by an adult member,<BR>" +
                     "must call the golf shop for tee time approval.";
      }

   } else if (slotParms.club.equals("elmcrestcc")) {     // ELMCREST CC
      
      //
      //  Elmcrest - Check for any Dependents without an Adult
      //
      error = checkElmcrestJr(slotParms, con);
      
      if (error == true) {         // if we hit an error
         
         returnMsg = "<BR><BR><H3>Dependents Not Allowed Without An Adult</H3>" +
                     "<BR>Sorry, but there must be at least one adult member in the group" +
                     "<BR>when one or more dependents are included." +
                     "<BR><BR>Please include an adult in the group.";
      }

   } else if (slotParms.club.equals("palmvalley-cc")) {     // PALM VALLEY CC
      
      //
      //  Palm Valley CC - Check for Membership Quotas
      //
      error = checkPVCCmships(slotParms, con);
      
      if (error == true) {         // if we hit an error
         
         returnMsg = "<BR><BR><H3>Quota Exceeded for Membership</H3>" +
                     "<BR>Sorry, but " +slotParms.player+ " has already played (or scheduled to play) the maximum allowed rounds for the season." +
                     "<BR><BR>Please remove this player or return to the tee sheet.";
      }

   } else if (slotParms.club.equals("jonathanslanding")) {     // JONATHANS LANDING GC
      
      //
      //  Jonathans Landing - Check for Membership Quotas
      //
      error = checkJLGCmships(slotParms, con);
      
      if (error == true) {         // if we hit an error
         
         returnMsg = "<BR><BR><H3>Quota Exceeded for Membership</H3>" +
                     "<BR>Sorry, but " +slotParms.player+ " (and family) have already played, or are scheduled to play, the maximum allowed rounds for the season." +
                     "<BR><BR>Please remove the player(s) or return to the tee sheet.";
      }

   } else if (slotParms.club.equals("gulfharbourgcc")) {     // GULF HARBOUR G&CC
      
      //
      //  Gulf Harbour - Check for Sports Membership Quotas
      //
      error = checkGulfHarbourSports(slotParms, con);
      
      if (error == true) {         // if we hit an error
         
         returnMsg = "<BR><BR><H3>Quota Exceeded for Membership</H3>" +
                     "<BR>Sorry, but " +slotParms.player+ " (and family) have already played, or are scheduled to play, the maximum allowed rounds for the month." +
                     "<BR><BR>Please remove the player(s) or return to the tee sheet.";
      }

   } else if (slotParms.club.equals("noaks")) {     // NORTH OAKS GC
      
      //
      //  North Oaks - check for juniors accompanied by an adult during specific times
      //
      error = checkNorthOaksJrs(slotParms, con);
      
      if (error == true) {         // if we hit an error
         
         returnMsg = "<BR><BR><H3>Unaccompanied Juniors Restricted</H3>" +
                     "<BR>Sorry, but juniors are not allowed to play at this time without an adult." +
                     "<BR><BR>Please remove the junior(s), add an adult, or return to the tee sheet.";
      }

   } else if (slotParms.club.equals("foresthighlands")) {     // FOREST HIGHLANDS CC
      
      if (slotParms.user.startsWith("proshop")) {        // only check this custom if proshop user
         
         //
         //  Forest Highlands Proshop User - check if any members have exceed their limit for a Blue Flag Cart 
         //
         int errorFlag = checkFHBlueCarts(slotParms, con);

         if (errorFlag == 3) {         // if member is now at 3 uses

            returnMsg = "<BR><BR><H3>Blue Flag Cart Limit</H3>" +
                        "<BR>Limit reached for player: <b>" +slotParms.player + "</b><BR>" +
                        "<BR>WARNING: This is the third temporary flag used without a doctor's note. The next time a flag " +
                        "<BR>is requested by the member, a doctor's note must be provided or " +
                        "a flag will not be granted. " +
                        "<BR><BR>Please contact the Professional Staff with any concerns. ";
            
         } else  if (errorFlag > 3) {         // if member has now exceeded 3 uses

            returnMsg = "<BR><BR><H3>Blue Flag Cart Limit</H3>" +
                        "<BR>Limit exceeded. This would make <b>" +errorFlag+ "</b> Blue Flag Cart Rounds for player: <b>" +slotParms.player + "</b><BR>" +
                        "<BR>WARNING: This member has exceeded the three rounds allowed with a temporary flag without a " +
                        "<BR>doctor's note on file.  Club policy requires a note be on file for use of a medical flag. " +
                        "<BR><BR>Please contact the Professional Staff with any concerns. ";
         } 
      }

   } else if (slotParms.club.equals("bonniebriar")) {

      //
      //  Bonnie Briar CC - Check for Associate/Sports Membership Quotas
      //
      error = checkBonnieBriarMships(slotParms, con);

      if (error == true) {         // if we hit an error

         returnMsg = "<BR><BR><H3>Quota Exceeded for Membership</H3>" +
                     "<BR>Sorry, but " +slotParms.player+ " has already played, or are scheduled to play, the maximum allowed weekend rounds for this month." +
                     "<BR><BR>Please remove the player(s) or return to the tee sheet.";
      }

   } else if (slotParms.club.equals("pmarshgc")) {     // Pelican Marsh GC

      //
      //  Gulf Harbour - Check for Sports Membership Quotas
      //
      error = checkPMarshMNums(slotParms, con);

      if (error == true) {         // if we hit an error

         returnMsg = "<BR><BR><H3>Quota Exceeded for Membership</H3>" +
                     "<BR>Sorry, but " +slotParms.player+ " (and family) have already scheduled the maximum allowed rounds for the day." +
                     "<BR><BR>Please remove the player(s) or return to the tee sheet.";
      }

   } else if (slotParms.club.equals("foxdencountryclub") && !slotParms.user.startsWith("proshop")) {     // Fox Den & Member

      //
      //  Fox Den - check for X's on weekends
      //
      error = checkFoxDenX(slotParms, con);

      if (error == true) {         // if we hit an error

         returnMsg = "<BR><BR><H3>Invalid Use of X's</H3>" +
                     "<BR>Sorry, but you cannot specify more than one X per group on weekends." +
                     "<BR><BR>Please remove the extra X's or return to the tee sheet.";
      }

   } else if (slotParms.club.equals("desertforestgolfclub")) {

      //
      //  Desert Forest - check for 2-some time and if more than 2 players 
      //
      error = checkDesertForest(slotParms.date, slotParms.time, slotParms.fb, slotParms.day);

      if (error == true && !slotParms.player3.equals("")) {    // if 2-some time and more than 2 players

         returnMsg = "<BR><BR><H3>Max Number of Members Exceeded</H3>" +
                     "<BR>Sorry, this is a 2-some time and you requested more than 2 players." +
                     "<BR><BR>Please remove the extra players or return to the tee sheet.";
      }

   } else if (slotParms.club.equals("oahucc") && slotParms.date > 20100101) {    // if Oahu and 2010 or beyond

      //
      //  Oahu  
      //
      if (slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday")) {
         
         error = checkOahuWeekends(slotParms);

         if (error == true) {        // if less than 3 players

            returnMsg = "<BR><BR><H3>X's Not Allowed</H3>" +
                        "<BR>Sorry, X's cannot be selected prior to 11 AM." +
                        "<BR><BR>Please remove all X's or return to the tee sheet.";
         }
      }

   } else if (slotParms.club.equals("loscoyotes")) {

      //
      //  Los Coyotes - check if requested tee time is for Primary Members Only
      //                If so, check for members that are not Primary
      //
      error = checkLosCoyotesTimes(slotParms.date, slotParms.time, slotParms.day, slotParms.course);

      if (error == true) {              // if Primary Only time
         
         if (slotParms.ind > 3) {       // if more than 3 days in advance - reject

               returnMsg = "<BR><BR><H3>Days in Advance Exceed</H3>" +
                           "<BR>Sorry, the tee time you requested is reserved for Primary members " +
                           "<BR>and cannot be reserved this far in advance." +
                           "<BR><BR>Please return to the tee sheet and select a differnt time.";
            
         } else {

            error = checkLosCoyotesPrimary(slotParms, con);     // make sure all members are mtype=Primary...

            if (error == true) {                   // if 1 or more non-Primary Members in request

               returnMsg = "<BR><BR><H3>Member Not Allowed</H3>" +
                           "<BR>Sorry, " +slotParms.player+ " is not a Primary member." +
                           "<BR>The tee time you requested is reserved for Primary members and their guests." +
                           "<BR><BR>Please remove the player or return to the tee sheet.";
            }
         }
      }

   } else if (slotParms.club.equals("ccnaples")) {

      //
      //  CC of Naples - check for Associate B mships - restricted in season (case 1704)
      //
      error = checkNaplesAssocB(slotParms);    // check for restricted time of day

      if (error == true) {              // if Assoc B during restricted time
         
            returnMsg = "<BR><BR><H3>Member Not Allowed</H3>" +
                        "<BR>Sorry, " +slotParms.player+ " is not allowed to play before 12:30. " +
                        "<BR><BR>Please remove this player or return to the tee sheet and select a different time.";

      } else {

         error = checkNaplesAssocBQuota(slotParms, con);     // check max rounds for Assoc B mships

         if (error == true) {                   // if 1 or more non-Primary Members in request

            returnMsg = "<BR><BR><H3>Member Exceeded Max Rounds</H3>" +
                        "<BR>Sorry, " +slotParms.player+ " has exceeded his/her max allowed rounds for this month." +
                        "<BR><BR>Please remove the player or return to the tee sheet.";
         }
      }

   } else if (slotParms.club.equals("longuevueclub")) {

      error = check2LongueVue(slotParms.date, slotParms.time, slotParms.day);    // check for 2-some time

      if (error == true && !slotParms.player3.equals("")) {    // if 2-some time and more than 2 players requested
         
            returnMsg = "<BR><BR><H3>Invalid Request</H3>" +
                        "<BR>Sorry, the time you requested is reserved for 2-somes only. " +
                        "<BR><BR>Please remove the extra players or return to the tee sheet and select a different time.";
      }

   } else if (slotParms.club.equals("canterburygc")) {

      //
      //  Canterbury - check for guest time only - any time beyond the normal 7 days in advance (case 1800)
      //
      error = checkCanterburyGst(slotParms);    

      if (error == true) {              // No guests in request 
         
            returnMsg = "<BR><BR><H3>Invalid Request</H3>" +
                        "<BR>Sorry, but there must be at least one member and one guest in the request. " +
                        "<BR>Only guest times are allowed when reserving a tee time more than 7 days in advance. " +
                        "<BR><BR>Please add the missing player or return to the tee sheet and select a different date.";
      }

   } else if (slotParms.club.equals("southernhillscc")) {

      //
      //  Southern Hills CC - Friday times during certain dates/time periods must contain at least 1 member and 2 guests (case 1689)
      //
      error = checkSouthernHillsGst(slotParms);

      if (error == true) {              // No guests in request

            returnMsg = "<BR><BR><H3>Invalid Request</H3>" +
                        "<BR>Sorry, but there must be at least one member and two guests in this request. " +
                        "<BR><BR>Please add the missing player/guests or return to the tee sheet and select a different time or date.";
      }

   } else if (slotParms.club.equals("yankeehill") || slotParms.club.equals("dhgc") || slotParms.club.equals("oakleycountryclub")) {

      //
      //  Make sure there are at least 2 players (members and/or guests) in every tee time
      //
      error = check2Players(slotParms);    

      if (error == true) {              // No guests in request 
         
            returnMsg = "<BR><BR><H3>Invalid Request</H3>" +
                        "<BR>Sorry, but there must be at least 2 players in the request. " +
                        "<BR>The request must contain at least 2 members, or 1 member and 1 guest. " +
                        "<BR><BR>Please add the missing player or return to the tee sheet.";
      }

   } else if (slotParms.club.equals("ramseycountryclub")) {

      //
      //  Make sure there are no more than 3 players in tee time if 3-some time
      //
      error = checkRamsey3someTime(slotParms.date, slotParms.time, slotParms.day);    

      if (error == true) {              // if 3-some time 

         if (!slotParms.player4.equals("") && !slotParms.player4.equalsIgnoreCase("x")) {   // if more than 3 players 

            returnMsg = "<BR><BR><H3>Invalid Request</H3>" +
                        "<BR>Sorry, but this is a 3-some only time. " +
                        "<BR>The request cannot contain more than 3 players. " +
                        "<BR><BR>Please limit the request to 3 players or return to the tee sheet.";
         }
      }

   } else if (slotParms.club.equals("morriscgc")) {

       //  Check "House" mship for Weekday and Weekend yearly round limits
       error = checkMorrisCGCmships(slotParms, con);

       if (error) {

           if (slotParms.day.equals("Tuesday") || slotParms.day.equals("Wednesday") || slotParms.day.equals("Thursday") || slotParms.day.equals("Friday")) {
               returnMsg = "<BR><BR><H3>Quota Exceeded for Membership</H3>" +
                       "<BR>Sorry, but " +slotParms.player+ " has already scheduled or played the maximum allowed weekday" +
                       "<BR><BR>rounds for this year.  Please remove the player or return to the tee sheet.";
           } else {
               returnMsg = "<BR><BR><H3>Quota Exceeded for Membership</H3>" +
                       "<BR>Sorry, but " +slotParms.player+ " has already scheduled or played the maximum allowed weekend" +
                       "<BR><BR>rounds for this year.  Please remove the player or return to the tee sheet.";
           }
       }

   } else if (slotParms.club.equals("capecodnational")) {     // Cape Cod National

       //  Check for "Wequassett Guest" (Hotel) type guests and enforce the club's limitations
       error = checkCapeCodGsts(slotParms, con);

       if (error) {

           returnMsg = "<BR><BR><H3>Wequassett Guest Quota Reached</H3>" +
                       "<BR>Sorry, but the quota for Wequassett Guests has already been reached for this day and time." +
                       "<BR><BR>Please select a different time of the day or a different day.";
       }

   } else if (slotParms.club.equals("hazeltine2010")) {     // Hazeltine's temp site
      
      //
      //  Hazeltine temp site - Check for Member Quotas per course (resciprocal clubs)
      //
      error = checkHazeltine2010(slotParms, con);
      
      if (error == true) {         // if we hit an error
         
         returnMsg = "<BR><BR><H3>Quota Exceeded for Member</H3>" +
                     "<BR>Sorry, but " +slotParms.player+ " has already played, or is scheduled to play, the maximum allowed times on this course." +
                     "<BR><BR>Please remove the player or return to the tee sheet and try another course.";
      }

   } else if (slotParms.club.equals("midpacific")) {

       parmMidPacific [] midPacParms = new parmMidPacific[5];

       // Intialize the array objects
       for (int i=0; i<5; i++) {
           midPacParms[i] = new parmMidPacific();
       }

       //
       //  Mid Pacific - Check restrictions for numerous different membership types
       //
       error = MidPacificCustom.checkMidPacificClasses(slotParms, midPacParms, con);

       if (error) {         // if we hit an error

           // Print out the first error we came across
           for (int i=0; i<5; i++) {
               if (returnMsg.equals("") && !midPacParms[i].errorMsg.equals("")) {
                   returnMsg = midPacParms[i].errorMsg;
               }
           }
       } else {

           // Loop through all players to see if we need to tweak any player to be a Prop guest. (skip player 1 since impossible for them to be a prop guest)
           if (midPacParms[1].bookAsPropGuest) {
               slotParms.player2 = MidPacificCustom.gtype_propGuest + " " + slotParms.player2;
               slotParms.custom_disp2 = slotParms.user2;
               slotParms.userg2 = midPacParms[1].propUser;
               slotParms.user2 = "";
               slotParms.mNum2 = "";
               slotParms.mship2 = "";
               slotParms.mtype2 = "";
           }
           if (midPacParms[2].bookAsPropGuest) {
               slotParms.player3 = MidPacificCustom.gtype_propGuest + " " + slotParms.player3;
               slotParms.custom_disp3 = slotParms.user3;
               slotParms.userg3 = midPacParms[2].propUser;
               slotParms.user3 = "";
               slotParms.mNum3 = "";
               slotParms.mship3 = "";
               slotParms.mtype3 = "";
           }
           if (midPacParms[3].bookAsPropGuest) {
               slotParms.player4 = MidPacificCustom.gtype_propGuest + " " + slotParms.player4;
               slotParms.custom_disp4 = slotParms.user4;
               slotParms.userg4 = midPacParms[3].propUser;
               slotParms.user4 = "";
               slotParms.mNum4 = "";
               slotParms.mship4 = "";
               slotParms.mtype4 = "";
           }
           if (midPacParms[4].bookAsPropGuest) {
               slotParms.player5 = MidPacificCustom.gtype_propGuest + " " + slotParms.player5;
               slotParms.custom_disp5 = slotParms.user5;
               slotParms.userg5 = midPacParms[4].propUser;
               slotParms.user5 = "";
               slotParms.mNum5 = "";
               slotParms.mship5 = "";
               slotParms.mtype5 = "";
           }
       }

   } else if (slotParms.club.equals("edisonclub")) {

       // CASE: 1834
       if ((slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday")) && slotParms.time >= 700 && slotParms.time <= 1100 && slotParms.date >= 20100501 && slotParms.date <= 20100907) {

           if (slotParms.members < 2 || (slotParms.members == 2 && slotParms.guests == 0)) {

               error = true;

           }

           if (error) {

               returnMsg = "<BR><H3>Minimum Number of Players Not Present</H3><BR>" +
                          "<BR><BR>When booking tee times between 7:00AM and 11:00AM you must specifiy at least three players.<BR>" +
                          "<BR>TBA or 'X' do not count and there must be at least two members included.";

               if (slotParms.groups > 0) {

                   returnMsg += "<BR><BR>Group #" + slotParms.groups + " starting at " + Utilities.getSimpleTime(slotParms.time) + " is violating this restriction.";

               }

           }

       } // end if within time & date range

       
   } else if (slotParms.club.equals("rhillscc")) {      //  Rolling Hills CC - Colorado

       if ((!slotParms.day.equals("Tuesday") && !slotParms.day.equals("Thursday")) || slotParms.time < 700 || slotParms.time > 933) {

           // CASE: 1852
           error = checkRHCCdays(slotParms);

           if (error) {

                returnMsg = "<BR><H3>Days In Advance Limit Exceeded</H3><BR>" +
                           "<BR><BR>Sorry, " +slotParms.player+ " is not allowed to be part of a tee time this far in advance." +
                           "<BR><BR>Please remove this player or select another day.";

           } else {

              //  Check for Guests and more than one day in advance

              error = checkRHCCguests(slotParms);

              if (error) {

                   returnMsg = "<BR><H3>Days In Advance Limit Exceeded</H3><BR>" +
                              "<BR><BR>Sorry, guests are not allowed to be part of a tee time more than 1 day in advance." +
                              "<BR><BR>Please remove this player or select another day.";

              } else {

                 //  If Sunday - check for non-Primary members in the morning

                 if (slotParms.day.equals("Sunday")) {

                    error = checkRHCCsunday(slotParms);

                    if (error) {

                         returnMsg = "<BR><H3>Member Restricted</H3><BR>" +
                                    "<BR><BR>Sorry, " +slotParms.player+ " is not allowed to be part of this tee time without a primary member." +
                                    "<BR>Secondary members and dependents must be accompanied by a primary member on Sunday mornings." +
                                    "<BR><BR>Please remove this player or select a later time (after 10:00 AM).";
                    }
                 }
              }
           }   // end of rhillscc
       }
   }   // end of club checks

   return(returnMsg);
   
 }
 
 
 
/**
 //************************************************************************
 //
 //  checkCustomsGst 
 //
 //      This method provides a common mechanism to process individual Custom 
 //      Guest restrictions. Each Member and Proshop verify method can call this one
 //      method to check ALL Custom Guest restrictions.
 //
 //      This is called in verify after guests have been processed and assigned.
 //
 //      This method will process each custom based on the club.  A String is
 //      returned to indicate if it hit on an error condition.  The string will
 //      contain the specific error message for the response. 
 //
 //      Any other pertinent information is returned in slotParms.
 //
 //************************************************************************
 **/

 public static String checkCustomsGst(parmSlot slotParms, Connection con) {

   String returnMsg = "";
   
   boolean error = false;
    
   //
   //  Process according to the club
   //
   if (slotParms.club.startsWith("tpc")) {               // ANY TPC
      
      //
      //  TPCs - Check for unaccompanied guests and if member allowed to book them
      //
      if (slotParms.members == 0) {          // if Unaccompanied Guest request

         error = checkTPCmems(slotParms, con);     // see if assigned member(s) are allowed

         if (error == true) {         // if we hit an error

            returnMsg = "<BR><BR><H3>Member Not Allowed To Book Unaccompanied Group</H3>" +
                        "<BR>Sorry, but " + slotParms.player + " is not allowed to book an unaccompanied guest time." +
                        "<BR><BR>Please add a member to the group or return to the tee sheet.";
         } 
      }
         
   } else if (slotParms.club.equals("interlachen")) {               //  Interlachen
      
      //
      //  Interlachen - Check for Guest-Centennial guest types
      //
      error = checkInterlachenGsts(slotParms, con);   

      if (error == true) {         // if we hit an error

         returnMsg = "<BR><BR><H3>Guest Type Not Allowed</H3>" +
                     "<BR>Sorry, but " + slotParms.player + " is not allowed to specify the Guest-Centennial guest type." +
                     "<BR><BR>Please select a different guest type for this member or return to the tee sheet.";
         
      } else {
         
         //
         //  Custom to check for a max of 3 guest times on weekdays between 10:00 and 11:30
         //
         if (slotParms.guests > 1 && !slotParms.day.equals( "Saturday" ) && !slotParms.day.equals( "Sunday" ) && 
             !slotParms.day.equals( "Monday" ) && slotParms.time > 959 && slotParms.time < 1131) {        
         
            error = checkInterlachenFriGsts(slotParms, con);   

            if (error == true) {         // if we hit an error

               returnMsg = "<BR><BR><H3>Guest Time Not Allowed</H3>" +
                           "<BR>Sorry, there are already 3 guest times (with more than 1 guest) scheduled this morning." +
                           "<BR>There can only be 3 tee times with 2 or 3 guests between 10:00 and 11:30 on weekdays." +
                           "<BR><BR>Please limit your request to 1 guest or select a different time of day.";
            }
         } 
      }           // end of Interlachen checks
         
   } else if (slotParms.club.equals("kinsale")) {                   //  Kinsale

       //
       //  Kinsale - Check for Weekend morning guest restriction, but exclude Marquee Golf members
       //
       error = checkKinsaleGuests(slotParms, con);

       if (error == true) {         // if we hit an error

           returnMsg = "<BR><BR><H3>Guest Type Not Allowed</H3>" +
                   "<BR>Sorry, but " + slotParms.player + " is not allowed to specify the Guest-Centennial guest type." +
                   "<BR><BR>Please select a different guest type for this member or return to the tee sheet.";
       }

   } else if (slotParms.club.equals( "beverlygc" )) {

      //
      //  Beverly GC Custom - check for guest quota
      //

      error = beverlyGuests(slotParms, con);

      //
      //  check for any error
      //
      if (error == true) {          // if we hit on a violation

         returnMsg = "<BR><BR><H3>Guest Quota Exceeded</H3><BR>" +
                     "<BR>Sorry, this request would exceed the guest quota for this day." +
                     "<br><br>You will have to remove one or more guests in order to complete this request." +
                     "<br><br>Contact the Golf Shop if you have any questions.<br>";
      }

   } else if (slotParms.club.equals("midpacific")) {

       parmMidPacific [] midPacParms = new parmMidPacific[5];

       // Intialize the array objects
       for (int i=0; i<5; i++) {
           midPacParms[i] = new parmMidPacific();
       }

       //
       //  Mid Pacific - Check restrictions for numerous different membership types
       //
       error = MidPacificCustom.checkMidPacificClasses(slotParms, midPacParms, con);

       if (error) {         // if we hit an error

           // Print out the first error we came across
           for (int i=0; i<5; i++) {
               if (returnMsg.equals("") && !midPacParms[i].errorMsg.equals("")) {
                   returnMsg = midPacParms[i].errorMsg;
               }
           }
       } else {

           // Loop through all players to see if we need to tweak any player to be a Prop guest. (skip player 1 since impossible for them to be a prop guest)
           if (midPacParms[1].bookAsPropGuest) {
               slotParms.player2 = MidPacificCustom.gtype_propGuest + " " + slotParms.player2;
               slotParms.custom_disp2 = slotParms.user2;
               slotParms.userg2 = midPacParms[1].propUser;
               slotParms.user2 = "";
               slotParms.mNum2 = "";
               slotParms.mship2 = "";
               slotParms.mtype2 = "";
           }
           if (midPacParms[2].bookAsPropGuest) {
               slotParms.player3 = MidPacificCustom.gtype_propGuest + " " + slotParms.player3;
               slotParms.custom_disp3 = slotParms.user3;
               slotParms.userg3 = midPacParms[2].propUser;
               slotParms.user3 = "";
               slotParms.mNum3 = "";
               slotParms.mship3 = "";
               slotParms.mtype3 = "";
           }
           if (midPacParms[3].bookAsPropGuest) {
               slotParms.player4 = MidPacificCustom.gtype_propGuest + " " + slotParms.player4;
               slotParms.custom_disp4 = slotParms.user4;
               slotParms.userg4 = midPacParms[3].propUser;
               slotParms.user4 = "";
               slotParms.mNum4 = "";
               slotParms.mship4 = "";
               slotParms.mtype4 = "";
           }
           if (midPacParms[4].bookAsPropGuest) {
               slotParms.player5 = MidPacificCustom.gtype_propGuest + " " + slotParms.player5;
               slotParms.custom_disp5 = slotParms.user5;
               slotParms.userg5 = midPacParms[4].propUser;
               slotParms.user5 = "";
               slotParms.mNum5 = "";
               slotParms.mship5 = "";
               slotParms.mtype5 = "";
           }
       }

   } else if (slotParms.club.equals("weeburn")) {

       //  Wee Burn CC - Check guest counts for 'WAITING FOR GOLF' members on weekends and holidays
       error = checkWeeburnWFGGuests(slotParms, con);

       if (error == true) {         // if we hit an error

           if (slotParms.day.equalsIgnoreCase("Saturday") && slotParms.time >= 1200) {
               returnMsg = "<BR><BR><H3>Guest Limit Exceeded</H3>" +
                       "<BR>Sorry, but " + slotParms.player + " is only allowed to have up to 3 guests in this tee time." +
                       "<BR><BR>Please remove some guests or return to the tee sheet.";
           } else {
               returnMsg = "<BR><BR><H3>Guest Limit Exceeded</H3>" +
                       "<BR>Sorry, but " + slotParms.player + " is not allowed to bring guests for this tee time." +
                       "<BR><BR>Please remove any guests associated with this member or return to the tee sheet.";
           }
       }
   }
   
   return(returnMsg); 

 }    // end of checkCustomsGst
   
    
/**
 //************************************************************************
 //
 //  wellesleyGuests - special Guest processing for Wellesley CC.
 //
 //     At this point we know there is more than one guest
 //     in this tee time and it is Wellesley.
 //
 //     Restrictions:
 //
 //       Social, & 'Child (under age 15)' mship types can never have guests.      
 //
 //       'Junior B (ages 15-24)', Non-Resident mship types cannot have guests on     
 //       Sat, Sun and Holidays (Mem Day, July 4th, Labor Day).
 //
 //       Wait List A, Limited, & Wait List mship types cannot have guests on
 //       Wed, Fri, Sat, Sun and Holidays (Mem Day, July 4th, Labor Day).
 //
 //       'Junior B (ages 15-24)' mship type cannot have guests from
 //       May 1 thru June 30.
 //
 //       Non-Resident mship type can have up to 3 guest rounds per year 
 //       where the year runs May 1 to April 30.
 //
 //
 //************************************************************************
 **/

 public static int wellesleyGuests(parmSlot slotParms, Connection con) {


   PreparedStatement pstmt = null;
   PreparedStatement pstmt1 = null;
   ResultSet rs = null;

   int error = 0;              // error code for return

   int gtimes = 0;         // number of guest times
   int holiday = 0;

   long memDay = Hdate1;       // Memorial Day     !!!!!!!!!! Must keep current !!!!!!!!!!!!!!!!!!
   long july4th = Hdate2;      // 4th of July
   long laborDay = Hdate3;     // Labor Day
     
   long sdate = 0;
   long edate = 0;
   String errorParm = "";

   //
   //  break down date of tee time
   //
   long yy = slotParms.date / 10000;                             // get year
   long mm = (slotParms.date - (yy * 10000)) / 100;              // get month
   long dd = (slotParms.date - (yy * 10000)) - (mm * 100);       // get day

   int omonth = (int)mm;
   int oday = (int)dd;

   int shortDate = (omonth * 100) + oday;
   int i = 0;

   String [] usergA = new String [5];       // array to hold the members' usernames
   String [] userA = new String [5];        // array to hold the usernames
   String [] playerA = new String [5];      // array to hold the player's names
   String [] mshipA = new String [5];       // array to hold the players' membership types

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

   usergA[0] = slotParms.userg1;                  // copy userg values into array
   usergA[1] = slotParms.userg2;
   usergA[2] = slotParms.userg3;
   usergA[3] = slotParms.userg4;
   usergA[4] = slotParms.userg5;

   mshipA[0] = slotParms.mship1;
   mshipA[1] = slotParms.mship2;
   mshipA[2] = slotParms.mship3;
   mshipA[3] = slotParms.mship4;
   mshipA[4] = slotParms.mship5;

   //
   //  First, verify the parms
   //
   for (i = 0; i < 5; i++) {

      if (playerA[i] == null) {   // if null parm
     
         errorParm = "player" +(i+1);
         playerA[i] = "";
      }
   }
     
   for (i = 0; i < 5; i++) {

      if (userA[i] == null) {   // if null parm

         errorParm = "user" +(i+1);
         userA[i] = "";
      }
   }

   for (i = 0; i < 5; i++) {

      if (usergA[i] == null) {   // if null parm

         errorParm = "userg" +(i+1);
         usergA[i] = "";
      }
   }

   for (i = 0; i < 5; i++) {

      if (mshipA[i] == null) {   // if null parm

         errorParm = "mship" +(i+1);
         mshipA[i] = "";
      }
   }


   if (!errorParm.equals( "" )) {   // if null parm

      Utilities.logError("Error checking for Wellesley guests - verifyCustom.wellesleyGuests: null parm received - " +errorParm);
      
   }


   try {

      holiday = 0;      // default no holiday

      if (slotParms.date == memDay || slotParms.date == july4th || slotParms.date == laborDay) {

         holiday = 1;
      }

      //
      //  Check each player
      //
      for (i = 0; i < 5; i++) {

         if (error == 0) {              // if error not already hit
  
            //
            //  First check for any Limited, Social or Child mship types - no guests allowed
            //
            if (mshipA[i].equals( "Social" ) || mshipA[i].startsWith( "Child" )) {

               //
               //  if this user has any guests in this request
               //
               if (usergA[0].equals( userA[i] ) || usergA[1].equals( userA[i] ) || usergA[2].equals( userA[i] ) ||  
                   usergA[3].equals( userA[i] ) || usergA[4].equals( userA[i] )) {

                  error = 1;                           // indicate error
                  slotParms.player = playerA[i];       // save player name for error message 
               }
            }

            //
            //  Now check for Junior B and Non-Resident mship types - no guests on W/E's or Holidays
            //
            if ((mshipA[i].startsWith( "Junior B" ) || mshipA[i].equals( "Non-Resident" )) && 
                (slotParms.day.equals( "Saturday" ) || slotParms.day.equals( "Sunday" ) || holiday == 1)) {

               //
               //  if this user has any guests in this request
               //
               if (usergA[0].equals( userA[i] ) || usergA[1].equals( userA[i] ) || usergA[2].equals( userA[i] ) ||
                   usergA[3].equals( userA[i] ) || usergA[4].equals( userA[i] )) {

                  error = 2;                           // indicate error
                  slotParms.player = playerA[i];       // save player name for error message
               }
            }

            //
            //  Now check for Wait List mship types - no guests on Wed, Fri, W/E's or Holidays
            //
            if ((mshipA[i].equals( "Limited" ) || mshipA[i].startsWith( "Wait List" )) && (slotParms.day.equals( "Wednesday" ) || slotParms.day.equals( "Friday" ) ||
                slotParms.day.equals( "Saturday" ) || slotParms.day.equals( "Sunday" ) || holiday == 1)) {

               //
               //  if this user has any guests in this request
               //
               if (usergA[0].equals( userA[i] ) || usergA[1].equals( userA[i] ) || usergA[2].equals( userA[i] ) ||
                   usergA[3].equals( userA[i] ) || usergA[4].equals( userA[i] )) {

                  error = 3;                           // indicate error
                  slotParms.player = playerA[i];       // save player name for error message
               }
            }

            //
            //  Now check for Junior B mship types - no guests between 5/01 and 6/30
            //
            if (mshipA[i].startsWith( "Junior B" ) && shortDate > 500 && shortDate < 631) {

               //
               //  if this user has any guests in this request
               //
               if (usergA[0].equals( userA[i] ) || usergA[1].equals( userA[i] ) || usergA[2].equals( userA[i] ) ||
                   usergA[3].equals( userA[i] ) || usergA[4].equals( userA[i] )) {

                  error = 4;                           // indicate error
                  slotParms.player = playerA[i];       // save player name for error message
               }
            }

            //
            //  Finally check for Non-Resident mship types - no more than 3 guest times per year (year starts 5/01)
            //
            if (mshipA[i].equals( "Non-Resident" )) {

               //
               //  and this user has a guest in this request
               //
               if (usergA[0].equals( userA[i] ) || usergA[1].equals( userA[i] ) || usergA[2].equals( userA[i] ) ||
                   usergA[3].equals( userA[i] ) || usergA[4].equals( userA[i] )) {

                  //
                  //  Determine date range to query
                  //
                  if (shortDate < 501) {          // if tee time date is earlier than may 1st

                     edate = (yy * 10000) + 431;        // end date = yyyy0431

                     yy--;                              // back up one year

                     sdate = (yy * 10000) + 500;        // start date = yyyy0500 (previous year)

                  } else {

                     sdate = (yy * 10000) + 500;        // start date = yyyy0500 

                     yy++;                              // next year

                     edate = (yy * 10000) + 431;        // end date = yyyy0431 (next year)
                  }

                  gtimes = 0;                          // # of guest times

                  //
                  //   Check teecurr and teepast for other guest times for this member
                  //
                  pstmt = con.prepareStatement (
                     "SELECT time " +
                     "FROM teepast2 " +
                     "WHERE date < ? AND date > ? AND date != ? AND time != ? AND " +
                     "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?)");

                  pstmt.clearParameters();       
                  pstmt.setLong(1, edate);
                  pstmt.setLong(2, sdate);
                  pstmt.setLong(3, slotParms.date);        // not this tee time
                  pstmt.setInt(4, slotParms.time);
                  pstmt.setString(5, userA[i]);
                  pstmt.setString(6, userA[i]);
                  pstmt.setString(7, userA[i]);
                  pstmt.setString(8, userA[i]);
                  pstmt.setString(9, userA[i]);
                  rs = pstmt.executeQuery();     

                  while (rs.next()) {

                     gtimes++;     // bump # of guests
               
                  }      // end of WHILE

                  pstmt.close();

                  pstmt = con.prepareStatement (
                     "SELECT time " +
                     "FROM teecurr2 " +
                     "WHERE date < ? AND date > ? AND date != ? AND time != ? AND " +
                     "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?)");

                  pstmt.clearParameters();
                  pstmt.setLong(1, edate);
                  pstmt.setLong(2, sdate);
                  pstmt.setLong(3, slotParms.date);        // not this tee time
                  pstmt.setInt(4, slotParms.time);
                  pstmt.setString(5, userA[i]);
                  pstmt.setString(6, userA[i]);
                  pstmt.setString(7, userA[i]);
                  pstmt.setString(8, userA[i]);
                  pstmt.setString(9, userA[i]);
                  rs = pstmt.executeQuery();

                  while (rs.next()) {

                     gtimes++;     // bump # of guests

                  }      // end of WHILE

                  pstmt.close();


                  if (gtimes > 2) {                    // if 3 tee times already on this date

                     error = 5;                           // indicate error
                     slotParms.player = playerA[i];       // save player name for error message
                  }
               }
            }

         }
      }              // end of FOR loop (do each player)
        
   } catch (Exception e) {

      Utilities.logError("Error checking for Wellesley guests - verifyCustom.wellesleyGuests " + e.getMessage());
      
   } finally {

      try { rs.close(); }
      catch (Exception ignore) {}

      try { pstmt.close(); }
      catch (Exception ignore) {}

   }

   return(error);
   
 }


/**
 //************************************************************************
 //
 //  checkBearpathMems - special processing for Bearpath CC.
 //
 //
 //      If a 'CD plus' mtype is included, they must be accompanied by
 //      a Primary or Spouse mtype on weekdays between 11 AM and 3 PM, 
 //      and on weekends before noon.
 //
 //
 //************************************************************************
 **/

 public static boolean checkBearpathMems(parmSlot slotParms) {


   boolean error = false;
   boolean check = false;

   String cdplus = "CD plus"; 
     
   long memDay = Hdate1;       // Memorial Day     !!!!!!!!!! Must keep current !!!!!!!!!!!!!!!!!!
   long july4th = Hdate2;      // 4th of July
   long laborDay = Hdate3;     // Labor Day


   //
   //  Check time of day based on day of week
   //
   if (slotParms.day.equals( "Saturday" ) || slotParms.day.equals( "Sunday" ) || slotParms.date == memDay ||
       slotParms.date == july4th || slotParms.date == laborDay) {          // if weekend or holiday

      if (slotParms.time < 1200) {         // if before Noon
        
         check = true;                     // check this request
      }

   } else {     // weekday
    
      if (slotParms.time < 1500 && slotParms.time > 1100) {  // if between 11 AM and 3 PM

         check = true;                     // check this request
      }
   }

   if (check == true) {

      //
      //  Check each player for CD plus mtype
      //
      if (slotParms.mtype1.equals( cdplus ) || slotParms.mtype2.equals( cdplus ) || slotParms.mtype3.equals( cdplus ) ||
          slotParms.mtype4.equals( cdplus ) || slotParms.mtype5.equals( cdplus )) {

         error = true;        // default to error

         //
         //  now check for any Primary members or Spouse members
         //
         if (slotParms.mtype1.startsWith( "Primary" ) || slotParms.mtype1.startsWith( "Spouse" ) ||
             slotParms.mtype2.startsWith( "Primary" ) || slotParms.mtype2.startsWith( "Spouse" ) ||
             slotParms.mtype3.startsWith( "Primary" ) || slotParms.mtype3.startsWith( "Spouse" ) ||
             slotParms.mtype4.startsWith( "Primary" ) || slotParms.mtype4.startsWith( "Spouse" ) ||
             slotParms.mtype5.startsWith( "Primary" ) || slotParms.mtype5.startsWith( "Spouse" )) {

            error = false;        // ok
         }
      }
   }

   return(error);
 }


/**
 //************************************************************************
 //
 //  checkLosCoyotesPrimary 
 //
 //      Primary Only Time - check for non-Primary member types
 //
 //************************************************************************
 **/

 public static boolean checkLosCoyotesPrimary(parmSlot slotParms, Connection con) {


   boolean error = false;

   String primary = "Primary"; 
     
   //
   //  Check each mtype for a non-primary
   //
   if (!slotParms.mtype1.equals( "" ) && !slotParms.mtype1.startsWith( primary )) {

      error = true;                            // error
      slotParms.player = slotParms.player1;    // save player name for error msg
      
   } else if (error == false && !slotParms.mtype2.equals( "" ) && !slotParms.mtype2.startsWith( primary )) {

      error = true;                            // error
      slotParms.player = slotParms.player2;    // save player name for error msg
      
   } else if (error == false && !slotParms.mtype3.equals( "" ) && !slotParms.mtype3.startsWith( primary )) {

      error = true;                            // error
      slotParms.player = slotParms.player3;    // save player name for error msg
      
   } else if (error == false && !slotParms.mtype4.equals( "" ) && !slotParms.mtype4.startsWith( primary )) {

      error = true;                            // error
      slotParms.player = slotParms.player4;    // save player name for error msg
      
   } else if (error == false && !slotParms.mtype5.equals( "" ) && !slotParms.mtype5.startsWith( primary )) {

      error = true;                            // error
      slotParms.player = slotParms.player5;    // save player name for error msg      
   }

   return(error);
 }


 
 
/**
 //************************************************************************
 //
 //  Riverside G&CC - Check for more than 12 guests total on Sunday Mornings.
 //
 //    Called by:  Member_slot & Proshop_slot
 //
 //    Check teecurr for the number of guests requested before noon.
 //
 //************************************************************************
 **/

 public static boolean checkRSguests(parmSlot slotParms, Connection con) {

     
   ResultSet rs = null;
   PreparedStatement pstmt1 = null;

   boolean error = false;

   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";

   int guests = slotParms.guests;    // # of guests in this request

   //
   //  Count all guests already scheduled before noon today (exclude this time)
   //
   try {

      pstmt1 = con.prepareStatement (
         "SELECT " +
         "userg1, userg2, userg3, userg4, userg5 " +
         "FROM teecurr2 " +
         "WHERE date = ? AND time < 1200 AND time != ? AND " +
         "(userg1 != ? OR userg2 != ? OR userg3 != ? OR userg4 != ? OR userg5 != ?)");

      pstmt1.clearParameters();        // clear the parms and check player 1
      pstmt1.setLong(1, slotParms.date);
      pstmt1.setInt(2, slotParms.time);
      pstmt1.setString(3, "");
      pstmt1.setString(4, "");
      pstmt1.setString(5, "");
      pstmt1.setString(6, "");
      pstmt1.setString(7, "");
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
      //  If more then 12 guests scheduled (counting this request), then set error
      //
      if (guests > 12) {

         error = true;
      }

   }
   catch (Exception e) {
       
      Utilities.logError("Error checking for Riverside Guests - verifyCustom.checkRSguests: " + e.getMessage());
      
   } finally {

      try { rs.close(); }
      catch (Exception ignore) {}

      try { pstmt1.close(); }
      catch (Exception ignore) {}

   }

   return(error);
   
 } // end of checkRSguests
 
 
/**
 //************************************************************************
 //
 //  The Patterson Club - Check for more than 12 guests total between 7-9:30am on Weekends & Holidays.
 //
 //    Called by:  Member_slot & Proshop_slot
 //
 //    Check teecurr for the number of guests requested between 7-9:30am.
 //
 //************************************************************************
 **/

 public static boolean checkPattersonGuests(parmSlot slotParms, Connection con) {

     
   ResultSet rs = null;
   PreparedStatement pstmt1 = null;

   boolean error = false;

   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";

   int guests = slotParms.guests;    // # of guests in this request

   //
   //  Count all guests already scheduled before noon today (exclude this time)
   //
   try {

      pstmt1 = con.prepareStatement (
         "SELECT " +
         "userg1, userg2, userg3, userg4, userg5, time, fb " +
         "FROM teecurr2 " +
         "WHERE date = ? AND time > 659 AND time < 931 AND " +
         "(userg1 != '' OR userg2 != '' OR userg3 != '' OR userg4 != '' OR userg5 != '')");

      pstmt1.clearParameters();        // clear the parms and check player 1
      pstmt1.setLong(1, slotParms.date);
      rs = pstmt1.executeQuery();      // execute the prepared stmt

      while (rs.next()) {
          
         if (rs.getInt("time") != slotParms.time || rs.getInt("fb") != slotParms.fb) {
             
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
         }
      }   // end of WHILE

      pstmt1.close();

      //
      //  If more then 12 guests scheduled (counting this request), then set error
      //
      if (guests > 12) {

         error = true;
      }

   } catch (Exception e) {
       
      Utilities.logError("Error checking for Patterson Club Guests - verifyCustom.checkPattersonGuests: " + e.getMessage());
      
   } finally {

      try { rs.close(); }
      catch (Exception ignore) {}

      try { pstmt1.close(); }
      catch (Exception ignore) {}

   }

   return(error);
   
 } // end of checkPattersonGuests

 
/**
 //************************************************************************
 //
 //  Minikahda CC - Check for 2 guest times per hour.
 //
 //    Called by:  Member_slot (new requests only)
 //
 //    Check teecurr for the number of guest times already scheduled.
 //
 //************************************************************************
 **/

 public static boolean checkMiniGuestTimes(parmSlot slotParms, Connection con) {

     
   ResultSet rs = null;
   PreparedStatement pstmt1 = null;

   boolean error = false;

   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
     
   int hour = 0;
   int count = 0;
   int memcount = 0;
     
   long date = slotParms.date;          

   hour = slotParms.time / 100;            // isolate the hour


   //
   //  Count all guest times already scheduled this hour (do not count this one)
   //
   try {

      pstmt1 = con.prepareStatement (
         "SELECT player1, player2, player3, player4, username1, username2, username3, username4 " +
         "FROM teecurr2 " +
         "WHERE date = ? AND hr = ? AND time != ? AND player1 != ''");

      pstmt1.clearParameters();       
      pstmt1.setLong(1, slotParms.date);
      pstmt1.setInt(2, hour);
      pstmt1.setInt(3, slotParms.time);            // not this time
      rs = pstmt1.executeQuery();     

      while (rs.next()) {

         player1 = rs.getString("player1");
         player2 = rs.getString("player2");
         player3 = rs.getString("player3");
         player4 = rs.getString("player4");
         user1 = rs.getString("username1");
         user2 = rs.getString("username2");
         user3 = rs.getString("username3");
         user4 = rs.getString("username4");

         if (!player1.equals( "" ) && !player2.equals( "" ) && !player3.equals( "" ) && !player4.equals( "" )) {       // if 4 players

            memcount = 0;
              
            if (!user1.equals( "" )) {         // 4 players - see how many are members
              
               memcount++;
            }
            if (!user2.equals( "" )) {

               memcount++;
            }
            if (!user3.equals( "" )) {

               memcount++;
            }
            if (!user4.equals( "" )) {

               memcount++;
            }
  
            if (memcount == 1) {         // if 1 member & 3 guests
  
               count++;                 // count # of guest times
            }
         }
      }

      pstmt1.close();

      //
      //  If 2 guest times already scheduled, then set error (3 guests on Tues/Wed/Thurs except between 12-2pm)
      //
      if ((slotParms.day.equals("Tuesday") || slotParms.day.equals("Wednesday") || slotParms.day.equals("Thursday")) && hour < 12 && hour > 14) {

         if (count > 2) {      // only 3 times allowed

             error = true;
         }

      } else if (hour < 8) {          // if before 8:00 AM

         if (count > 0) {      // only one time allowed

            error = true;
         }
           
      } else {
        
         if ((hour >= 12 && hour <= 14) && (date >= Hdate1 && date <= Hdate3)) {  // if between 12 and 2, and between Memorial Day and Labor Day
            
            if (count > 0) {      // only 1 times allowed

               error = true;
            }
            
         } else {
            
            if (count > 1) {      // only 2 times allowed

               error = true;
            }
         }
      }

   } catch (Exception e) {
       
      Utilities.logError("Error checking for Minikahda Guests - verifyCustom.checkMiniGuestTimes: " + e.getMessage());
      
   } finally {

      try { rs.close(); }
      catch (Exception ignore) {}

      try { pstmt1.close(); }
      catch (Exception ignore) {}

   }

   return(error);
 }                   // end of checkMiniGuestTimes
 
 

         
         
/**
 //************************************************************************
 //
 //  Cape Cod National - Check for Hotel guests - maintain a quota during specific times.
 //
 //************************************************************************
 **/

 public static boolean checkCapeCodGsts(parmSlot slotParms, Connection con) {

     
   ResultSet rs = null;
   PreparedStatement pstmt1 = null;

   boolean error = false;

   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   
   int count = 0;
   int quota = 0;
   int stime = 0;
   int etime = 0;
   
   String hotelGst = "Wequassett Guest";
   
   long shortDate = slotParms.date - ((slotParms.date / 10000) * 10000);       // get mmdd (i.e.  20060512 - 20060000 = 512)
   
     

   //
   //  First, check if tee time is after 9:00 AM and there are any hotel guests in this request
   //
   if (slotParms.time > 859 && (slotParms.player1.startsWith( hotelGst ) || slotParms.player2.startsWith( hotelGst ) 
           || slotParms.player3.startsWith( hotelGst ) || slotParms.player4.startsWith( hotelGst ))) {
   
      //
      //  At least one hotel guest in request - determine quota based on date, day and time
      //
      if (shortDate >= 626 && shortDate <= 906) {        // 6/26 thru Labor Day
         
         //
         //  In Season
         //
         if (slotParms.day.equals("Friday") || slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday") || 
             slotParms.date == Hdate1 || slotParms.date == Hdate2 || slotParms.date == Hdate3) {                      // if w/e or holiday
            
            quota = 2;        // max of 2 guest times
            
            if (slotParms.time <= 1159) {
               
               stime = 900;
               etime = 1159;
               
            } else {
               
               stime = 1200;
               etime = 2000;
            }
            
            
         } else {      // week day in season, not a holiday
            
            if (slotParms.time <= 1059) {
               
               stime = 900;
               etime = 1059;
               quota = 2;        // max of 2 guest times before 11:00
               
            } else if (slotParms.time <= 1259) {
               
               stime = 1100;
               etime = 1259;
               quota = 2;        // max of 2 guest times between 11:00 and 1:00
               
            } else {
               
               stime = 1300;
               etime = 2000;
               quota = 3;        // max of 3 guest times after 1:00
            }           
         }        
         
      } else {
         
         //
         //  Off Season (same quotas every day)
         //
         if (slotParms.time <= 959) {

            stime = 900;
            etime = 959;
            quota = 2;        // max of 2 guest times between 9:00-9:59

         } else if (slotParms.time <= 1059) {

            stime = 1000;
            etime = 1059;
            quota = 2;        // max of 2 guest times between 10:00-10:59

         } else if (slotParms.time <= 1159) {

            stime = 1100;
            etime = 1159;
            quota = 2;        // max of 2 guest times between 11:00-11:59

         } else if (slotParms.time <= 1259) {

            stime = 1200;
            etime = 1259;
            quota = 4;        // max of 4 guest times between 12:00-12:59

         } else if (slotParms.time <= 1359) {

            stime = 1300;
            etime = 1359;
            quota = 4;        // max of 4 guest times between 13:00-13:59

         } else if (slotParms.time <= 1459) {

            stime = 1400;
            etime = 1459;
            quota = 4;        // max of 4 guest times between 14:00-14:59

         } else if (slotParms.time <= 1559) {

            stime = 1500;
            etime = 1559;
            quota = 4;        // max of 4 guest times between 15:00-15:59

         } else if (slotParms.time <= 1659) {

            stime = 1600;
            etime = 1659;
            quota = 4;        // max of 4 guest times between 16:00-16:59

         } else if (slotParms.time <= 1759) {

            stime = 1700;
            etime = 1759;
            quota = 4;        // max of 4 guest times between 17:00-17:59

         } else if (slotParms.time <= 1859) {

            stime = 1800;
            etime = 1859;
            quota = 4;        // max of 4 guest times between 18:00-18:59

         } else {

            stime = 1900;
            etime = 2000;
            quota = 4;        // max of 4 guest times between 19:00-20:00
         }           
      }
      
      //
      //  Now check the quota
      //
      if (quota > 0) {
                 
         try {

            pstmt1 = con.prepareStatement (
               "SELECT player1, player2, player3, player4 " +
               "FROM teecurr2 " +
               "WHERE date = ? AND time >= ? AND time <= ? AND player1 != '' AND teecurr_id != ?");

            pstmt1.clearParameters();       
            pstmt1.setLong(1, slotParms.date);
            pstmt1.setInt(2, stime);
            pstmt1.setInt(3, etime);
            pstmt1.setInt(4, slotParms.teecurr_id);            // not this time
            rs = pstmt1.executeQuery();     

            while (rs.next()) {

               player1 = rs.getString("player1");
               player2 = rs.getString("player2");
               player3 = rs.getString("player3");
               player4 = rs.getString("player4");

               if (player1.startsWith( hotelGst ) || player2.startsWith( hotelGst ) || player3.startsWith( hotelGst ) || player4.startsWith( hotelGst )) {

                  count++;                 // count # of guest times
               }
            }

            pstmt1.close();

            if (count >= quota) {      // if quota already reached

                error = true;
            }

         } catch (Exception e) {

            Utilities.logError("Error checking for Cape Cod Guests - verifyCustom.checkCapeCodGsts: " + e.getMessage());

         } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt1.close(); }
            catch (Exception ignore) {}

         }
      }
   }
   
   return(error);
 }                   // end of checkCapeCodeGsts
 
                  
 
 

/**
 //************************************************************************
 //
 //  Wilmington CC - Check for more than 12 guests total during specified days/times.
 //
 //    Called by:  Member_slot(m) & Proshop_slot(m)
 //
 //    Check teecurr for the number of guests already scheduled.
 //
 //************************************************************************
 **/

 public static boolean checkWilmingtonGuests(parmSlot slotParms, Connection con) {


   boolean error = false;
   boolean check = false;

   String day = slotParms.day;

   int stime = 0;
   int etime = 0;
   int time = slotParms.time;

   long date = slotParms.date;
   //long shortDate = date - ((date / 10000) * 10000);       // get mmdd (i.e.  20060512 - 20060000)


   //
   //  Determine time range based on the date
   //
   //       If Tue - Fri, not 7/04, and between 8:00 - 11:30 AM
   //
   if ((day.equals( "Tuesday" ) || day.equals( "Wednesday" ) || day.equals( "Thursday" ) || day.equals( "Friday" )) && 
        date != Hdate2b && time > 759 && time < 1131) { 

      check = true;         // check for max guests
        
      stime = 759;          // time range to check
      etime = 1131;
        
   } else {

      //
      //   OR   If Tue - Sun, or Memorial Day, or Labor Day, and between 1:30 - 7:00 PM
      //
      if ((!day.equals( "Monday" ) || date == Hdate1 || date == Hdate3) &&
           time > 1329 && time < 1901) {

         check = true;         // check for max guests

         stime = 1329;          // time range to check
         etime = 1901;
      }
   }

   if (check == true) {          // check for guests?
     
      error = checkWilDB(slotParms, stime, etime, con);         // go check for too many guests
   }

   return(error);
   
 } // end of checkWilmingtonGuests


 //
 private static boolean checkWilDB(parmSlot slotParms, int stime, int etime, Connection con) {


   ResultSet rs = null;
   PreparedStatement pstmt1 = null;

   boolean error = false;
   
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
     
   int guests = slotParms.guests;    // # of guests in this request
  
   //
   //  Count all guests already scheduled before noon today (exclude this time)
   //
   try {

      pstmt1 = con.prepareStatement (
         "SELECT " +
         "userg1, userg2, userg3, userg4, userg5 " +
         "FROM teecurr2 " +
         "WHERE date = ? AND time > ? AND time < ? AND time != ? AND " +
         "(userg1 != ? OR userg2 != ? OR userg3 != ? OR userg4 != ? OR userg5 != ?)");

      pstmt1.clearParameters();        // clear the parms and check player 1
      pstmt1.setLong(1, slotParms.date);
      pstmt1.setInt(2, stime);
      pstmt1.setInt(3, etime);
      pstmt1.setInt(4, slotParms.time);
      pstmt1.setString(5, "");
      pstmt1.setString(6, "");
      pstmt1.setString(7, "");
      pstmt1.setString(8, "");
      pstmt1.setString(9, "");
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
      //  If more then 12 guests scheduled (counting this request), then set error
      //
      if (guests > 12) {

         error = true;
      }

   } catch (Exception e) {

      Utilities.logError("Error checking for Wilmington Guests - verifyCustom.checkWilDB: " + e.getMessage());        // log the error message
   
   } finally {

      try { rs.close(); }
      catch (Exception ignore) {}

      try { pstmt1.close(); }
      catch (Exception ignore) {}

   }

   return(error);
 }                   // end of checkWilDB


/**
 //************************************************************************
 //
 //  Hazeltine National - Event Signup Check
 //
 //    Called by:  Member_evntSignUp and Proshop_evntSignUp
 //
 //    Check member sub-types to see if they can register for this event.
 //
 //************************************************************************
 **/

 public static String checkHazeltineInvite(String user1, String user2, String user3, String user4, String user5, Connection con) {

     
    PreparedStatement pstmt1 = null;
    ResultSet rs = null;
   
    String user = "";
    String msubtype = "";

    String [] userA = new String [5];        // array to hold the usernames

    userA[0] = user1;                        // put users in array for loop
    userA[1] = user2;
    userA[2] = user3;
    userA[3] = user4;
    userA[4] = user5;

    //
    //  Check each user to see if any are prohibited from this event (Mens Invitational)
    //
    loop1:
    for (int i=0; i<5; i++) {
      
         if (!userA[i].equals( "" )) {
               
            try {

               //
               //  Get the member sub-type for this user
               //
               pstmt1 = con.prepareStatement (
                  "SELECT msub_type FROM member2b WHERE username = ?");

                  pstmt1.clearParameters();        // clear the parms
                  pstmt1.setString(1, userA[i]);
                  rs = pstmt1.executeQuery();      // execute the prepared stmt

                  if (rs.next()) {

                     msubtype = rs.getString(1);
                  }

                  pstmt1.close();
                  
            } catch (Exception ignore) {
            
            } finally {

                try { rs.close(); }
                catch (Exception ignore) {}

                try { pstmt1.close(); }
                catch (Exception ignore) {}

            }

            if (!msubtype.equals( "Invite Priority" )) {         // if NOT Invite Priority - no signup

               user = userA[i]; 
               break loop1;                  // exit with bad user
            }
         }
    }                   
                  
    return(user);
 }
 

/**
 //************************************************************************
 //
 //  Beverly GC - Check for more than 21 guests total during specified days/times.
 //
 //    Called by:  Member_slot(m)
 //
 //    Check teecurr for the number of guests already scheduled.
 //
 //************************************************************************
 **/

 public static boolean beverlyGuests(parmSlot slotParms, Connection con) {

   boolean error = false;
   
   int stime = 1200;
   int etime = 1700;

   long shortDate = slotParms.date - ((slotParms.date / 10000) * 10000);       // get mmdd (i.e.  20060512 - 20060000 = 512)

   //
   //   Only check if Wed or Fri, between Noon and 5:00 PM and between 4/15 and 11/15
   //
   if ((slotParms.day.equals( "Wednesday" ) || slotParms.day.equals( "Friday" )) && (slotParms.time > 1200 && slotParms.time < 1700) &&
        (shortDate > 414 && shortDate < 1116)) { 

       
      ResultSet rs = null;
      PreparedStatement pstmt1 = null;

      String userg1 = "";
      String userg2 = "";
      String userg3 = "";
      String userg4 = "";
      String userg5 = "";
      
      int guests = slotParms.guests;    // # of guests in this request

      //
      //  Count all guests already scheduled between noon and 5 PM today (exclude this time)
      //
      try {

         pstmt1 = con.prepareStatement (
            "SELECT " +
            "userg1, userg2, userg3, userg4, userg5 " +
            "FROM teecurr2 " +
            "WHERE date = ? AND time > ? AND time < ? AND time != ? AND " +
            "(userg1 != ? OR userg2 != ? OR userg3 != ? OR userg4 != ? OR userg5 != ?)");

         pstmt1.clearParameters();        // clear the parms and check player 1
         pstmt1.setLong(1, slotParms.date);
         pstmt1.setInt(2, stime);
         pstmt1.setInt(3, etime);
         pstmt1.setInt(4, slotParms.time);
         pstmt1.setString(5, "");
         pstmt1.setString(6, "");
         pstmt1.setString(7, "");
         pstmt1.setString(8, "");
         pstmt1.setString(9, "");
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
         //  If more then 21 guests scheduled (counting this request), then set error
         //
         if (guests > 21) {

            error = true;
         }

      } catch (Exception e) {

         Utilities.logError("Error checking for Beverly Guests - verifyCustom.beverlyGuests: " + e.getMessage());        // log the error message
      
      } finally {

          try { rs.close(); }
          catch (Exception ignore) {}

          try { pstmt1.close(); }
          catch (Exception ignore) {}

      }
   }

   return(error);
 }                   // end of beverlyGuests


 // *********************************************************
 //  El Niguel CC - check for Juniors w/o an adult
 //
 //    Dependents = mtypes of 'Junior Male' and 'Junior Female'
 //
 //    Restrictions:  Sunday 7 - 11 AM on Back Tee (already known)
 //
 // *********************************************************

 public static boolean checkElNiguelDependents(parmSlot slotParms) {


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
 //  Belle Meade CC - check for Females w/o a Male
 //
 //    Restrictions:  Sunday 8 - 12:50 (already known)
 //
 // *********************************************************

 public static boolean checkBelleMeadeFems(parmSlot slotParms) {


   boolean error = false;


   //
   //  Check for any Primary Females
   //
   if (slotParms.mtype1.endsWith( "Female" ) || slotParms.mtype2.endsWith( "Female" ) || slotParms.mtype3.endsWith( "Female" ) ||
       slotParms.mtype4.endsWith( "Female" ) || slotParms.mtype5.endsWith( "Female" )) {

      //
      //  Make sure at least 1 Male
      //
      if (!slotParms.mtype1.equals( "Primary Male" ) &&
          !slotParms.mtype2.equals( "Primary Male" ) &&
          !slotParms.mtype3.equals( "Primary Male" ) &&
          !slotParms.mtype4.equals( "Primary Male" ) &&
          !slotParms.mtype5.equals( "Primary Male" )) {       // if no Males

         error = true;           // no adult - error
      }
   }

   return(error);
 }



 // *********************************************************
 //  Los Coyotes CC - check for Spouses w/o Primary
 //
 //    Restrictions:  more than 3 days in advance (checked before here)
 //
 //         If a Secondary member in group with at least one Primary member,
 //         the Secondary's spouse must be included.
 //
 // *********************************************************

 public static boolean checkLCSpouses(parmSlot slotParms) {


   boolean error = false;

   //  check if any Secondary members in group
   if (slotParms.mtype1.startsWith( "Secondary" ) || slotParms.mtype2.startsWith( "Secondary" ) || slotParms.mtype3.startsWith( "Secondary" ) ||
       slotParms.mtype4.startsWith( "Secondary" ) || slotParms.mtype5.startsWith( "Secondary" )) {

      //
      //  If any Primary members in group, they must be of the same family
      //
      if (slotParms.mtype1.startsWith( "Primary" ) || slotParms.mtype2.startsWith( "Primary" ) || slotParms.mtype3.startsWith( "Primary" ) ||
          slotParms.mtype4.startsWith( "Primary" ) || slotParms.mtype5.startsWith( "Primary" )) {

         //
         //  We have at least 1 of each - see if they are same family
         //
         error = false;    

         if (slotParms.mtype1.startsWith("Secondary")) {

            error = true;
            
            if (slotParms.mNum1.equals(slotParms.mNum2) || slotParms.mNum1.equals(slotParms.mNum3) || 
                slotParms.mNum1.equals(slotParms.mNum4) || slotParms.mNum1.equals(slotParms.mNum5)) {
               
               error = false;         
            }
         }
               
         if (slotParms.mtype2.startsWith("Secondary") && error == false) {

            error = true;
            
            if (slotParms.mNum2.equals(slotParms.mNum1) || slotParms.mNum2.equals(slotParms.mNum3) || 
                slotParms.mNum2.equals(slotParms.mNum4) || slotParms.mNum2.equals(slotParms.mNum5)) {
               
               error = false;         
            }
         }
               
         if (slotParms.mtype3.startsWith("Secondary") && error == false) {

            error = true;
            
            if (slotParms.mNum3.equals(slotParms.mNum1) || slotParms.mNum3.equals(slotParms.mNum2) || 
                slotParms.mNum3.equals(slotParms.mNum4) || slotParms.mNum3.equals(slotParms.mNum5)) {
               
               error = false;         
            }
         }
               
         if (slotParms.mtype4.startsWith("Secondary") && error == false) {

            error = true;
            
            if (slotParms.mNum4.equals(slotParms.mNum1) || slotParms.mNum4.equals(slotParms.mNum2) || 
                slotParms.mNum4.equals(slotParms.mNum3) || slotParms.mNum4.equals(slotParms.mNum5)) {
               
               error = false;         
            }
         }
               
         if (slotParms.mtype5.startsWith("Secondary") && error == false) {

            error = true;
            
            if (slotParms.mNum5.equals(slotParms.mNum1) || slotParms.mNum5.equals(slotParms.mNum2) || 
                slotParms.mNum5.equals(slotParms.mNum3) || slotParms.mNum5.equals(slotParms.mNum4)) {
               
               error = false;         
            }
         }  
      }
   }

   return(error);
 }


 // *********************************************************
 //  Awbrey Glen - check for Juniors w/o an adult
 //
 //    Dependents = mtypes of 'Junior Male' and 'Junior Female'
 //                 or 'Junior xx 12 and over' (xx = Male or Female)
 //
 //    Restrictions:  all day, every day for Juniors
 //                   before Noon, every day for Juniors 12 and over
 //
 // *********************************************************

 public static boolean checkAwbreyDependents(parmSlot slotParms) {


   boolean error = false;


   //
   //  Check for any dependents
   //
   if (slotParms.mtype1.equals( "Junior Male" ) || slotParms.mtype2.equals( "Junior Male" ) || slotParms.mtype3.equals( "Junior Male" ) ||
       slotParms.mtype4.equals( "Junior Male" ) || slotParms.mtype5.equals( "Junior Male" ) ||
       slotParms.mtype1.equals( "Junior Female" ) || slotParms.mtype2.equals( "Junior Female" ) || slotParms.mtype3.equals( "Junior Female" ) ||
       slotParms.mtype4.equals( "Junior Female" ) || slotParms.mtype5.equals( "Junior Female" )) {

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
        
   } else {

      if (slotParms.mtype1.endsWith( "over" ) || slotParms.mtype2.endsWith( "over" ) || slotParms.mtype3.endsWith( "over" ) ||
          slotParms.mtype4.endsWith( "over" ) || slotParms.mtype5.endsWith( "over" )) {

         if (slotParms.time < 1200) {       // if before Noon
        
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
      }
   }

   return(error);
 }


 // *********************************************************
 //  MN Valley - check for Juniors w/o an adult
 //
 //    Dependents = mtypes of 'Junior Male 11-14' and 'Junior Female 11-14'
 //
 //    Restrictions:  From Mar 1 - Nov 15, Sat 1:00 - 3:00, Sun 1:00 - 4:00
 //
 // *********************************************************

 public static boolean checkMNValleyJrs(parmSlot slotParms) {


   boolean error = false;
   
   String jrMale = "Junior Male 11-14";
   String jrFemale = "Junior Female 11-14";
   String jrMen = "Junior Men 19-23";
   String jrWomen = "Junior Women 19-23";

   //
   //  Determine date values - month, day, year
   //
   long year = slotParms.date / 10000;
   long month = (slotParms.date - (year * 10000)) / 100;
   long day = slotParms.date - ((year * 10000) + (month * 100));
   long shortDate = (month * 100) + day;                        // create mmdd value

   
   //
   //  If in season, Sat between 1:00 and 3:00, OR Sun between 1:00 and 4:00 - check for unaccompanied Juniors
   //
   if (shortDate > 300 && shortDate < 1116 && ((slotParms.day.equals( "Saturday" ) && slotParms.time > 1259 && slotParms.time < 1501) ||
       (slotParms.day.equals( "Sunday" ) && slotParms.time > 1259 && slotParms.time < 1601))) {

      //
      //  Check for any Juniors 11 - 14
      //
      if (slotParms.mtype1.equals( jrMale ) || slotParms.mtype2.equals( jrMale ) || slotParms.mtype3.equals( jrMale ) ||
          slotParms.mtype4.equals( jrMale ) || slotParms.mtype5.equals( jrMale ) ||
          slotParms.mtype1.equals( jrFemale ) || slotParms.mtype2.equals( jrFemale ) || slotParms.mtype3.equals( jrFemale ) ||
          slotParms.mtype4.equals( jrFemale ) || slotParms.mtype5.equals( jrFemale )) {

         error = true;           // default to error if Junior found

         //
         //  Make sure at least 1 Adult or 1 Jr Adult
         //
         if (slotParms.mtype1.startsWith( "Adult" ) || slotParms.mtype1.equals( jrMen ) || slotParms.mtype1.equals( jrWomen ) ||
             slotParms.mtype2.startsWith( "Adult" ) || slotParms.mtype2.equals( jrMen ) || slotParms.mtype2.equals( jrWomen ) ||
             slotParms.mtype3.startsWith( "Adult" ) || slotParms.mtype3.equals( jrMen ) || slotParms.mtype3.equals( jrWomen ) ||
             slotParms.mtype4.startsWith( "Adult" ) || slotParms.mtype4.equals( jrMen ) || slotParms.mtype4.equals( jrWomen ) ||
             slotParms.mtype5.startsWith( "Adult" ) || slotParms.mtype5.equals( jrMen ) || slotParms.mtype5.equals( jrWomen )) {      

            error = false;           // adult found - ok
         }
      }
   }

   return(error);
 }


 // *********************************************************
 //  Tualatin - check for Juniors w/o an adult
 //
 //    Dependents = mtypes of 'Junior 17 Male' and 'Junior 17 Female'
 //                 or 'Junior 18-23 Male' and 'Junior 18-23 Female' 
 //
 //    Restrictions:  T-W-F-S-Sun 11:00 - 2:00
 //                 
 //
 // *********************************************************

 public static boolean checkTualatinJr(parmSlot slotParms, Connection con) {


   boolean error = false;

   
   //
   //  If Tues, Wed, Fri, Sat, or Sun, AND between 11:00 and 2:00 - check for unaccompanied Juniors
   //
   if (!slotParms.day.equals( "Monday" ) && !slotParms.day.equals( "Thursday" ) && slotParms.time > 1059 && slotParms.time < 1401) {

      //
      //  Check for any dependents
      //
      if (slotParms.mtype1.startsWith( "Junior" )) {

         error = true;           // init to error
         
         //
         //  Make sure at least 1 adult
         //
         if (slotParms.mtype2.startsWith( "Primary" ) || slotParms.mtype2.startsWith( "Spouse" ) ||
             slotParms.mtype3.startsWith( "Primary" ) || slotParms.mtype3.startsWith( "Spouse" ) ||
             slotParms.mtype4.startsWith( "Primary" ) || slotParms.mtype4.startsWith( "Spouse" ) ||
             slotParms.mtype5.startsWith( "Primary" ) || slotParms.mtype5.startsWith( "Spouse" )) {       // if adults

            error = false;           // there is an adult - NO error
         }
      }

      if (slotParms.mtype2.startsWith( "Junior" ) && error == false) {

         error = true;           // init to error
         
         //
         //  Make sure at least 1 adult
         //
         if (slotParms.mtype1.startsWith( "Primary" ) || slotParms.mtype1.startsWith( "Spouse" ) ||
             slotParms.mtype3.startsWith( "Primary" ) || slotParms.mtype3.startsWith( "Spouse" ) ||
             slotParms.mtype4.startsWith( "Primary" ) || slotParms.mtype4.startsWith( "Spouse" ) ||
             slotParms.mtype5.startsWith( "Primary" ) || slotParms.mtype5.startsWith( "Spouse" )) {       // if adults

            error = false;           // there is an adult - NO error
         }
      }

      if (slotParms.mtype3.startsWith( "Junior" ) && error == false) {

         error = true;           // init to error
         
         //
         //  Make sure at least 1 adult
         //
         if (slotParms.mtype1.startsWith( "Primary" ) || slotParms.mtype1.startsWith( "Spouse" ) ||
             slotParms.mtype2.startsWith( "Primary" ) || slotParms.mtype2.startsWith( "Spouse" ) ||
             slotParms.mtype4.startsWith( "Primary" ) || slotParms.mtype4.startsWith( "Spouse" ) ||
             slotParms.mtype5.startsWith( "Primary" ) || slotParms.mtype5.startsWith( "Spouse" )) {       // if adults

            error = false;           // there is an adult - NO error
         }
      }

      if (slotParms.mtype4.startsWith( "Junior" ) && error == false) {

         error = true;           // init to error
         
         //
         //  Make sure at least 1 adult
         //
         if (slotParms.mtype1.startsWith( "Primary" ) || slotParms.mtype1.startsWith( "Spouse" ) ||
             slotParms.mtype2.startsWith( "Primary" ) || slotParms.mtype2.startsWith( "Spouse" ) ||
             slotParms.mtype3.startsWith( "Primary" ) || slotParms.mtype3.startsWith( "Spouse" ) ||
             slotParms.mtype5.startsWith( "Primary" ) || slotParms.mtype5.startsWith( "Spouse" )) {       // if adults

            error = false;           // there is an adult - NO error
         }
      }

      if (slotParms.mtype5.startsWith( "Junior" ) && error == false) {

         error = true;           // init to error
         
         //
         //  Make sure at least 1 adult
         //
         if (slotParms.mtype2.startsWith( "Primary" ) || slotParms.mtype2.startsWith( "Spouse" ) ||
             slotParms.mtype3.startsWith( "Primary" ) || slotParms.mtype3.startsWith( "Spouse" ) ||
             slotParms.mtype4.startsWith( "Primary" ) || slotParms.mtype4.startsWith( "Spouse" ) ||
             slotParms.mtype1.startsWith( "Primary" ) || slotParms.mtype1.startsWith( "Spouse" )) {       // if adults

            error = false;           // there is an adult - NO error
         }
      }
   }

   return(error);
 }


 // *********************************************************
 //  Cherokee - check for Juniors w/o an adult
 // *********************************************************

 public static boolean checkCherokeeJr(parmSlot slotParms, Connection con) {


   boolean error = false;

   
   //
   //  Check for any dependents
   //
   if (slotParms.mtype1.equals( "Dependent" )) {    // if only dependents in this request

      error = true;           // init to error
         
      //
      //  Make sure at least 1 adult
      //
      if (slotParms.mtype2.startsWith( "Primary" ) || slotParms.mtype2.startsWith( "Spouse" ) ||
          slotParms.mtype3.startsWith( "Primary" ) || slotParms.mtype3.startsWith( "Spouse" ) ||
          slotParms.mtype4.startsWith( "Primary" ) || slotParms.mtype4.startsWith( "Spouse" ) ||
          slotParms.mtype5.startsWith( "Primary" ) || slotParms.mtype5.startsWith( "Spouse" )) {       // if adults

         error = false;           // there is an adult - NO error
      }
   }  

   return(error);
 }


 // *********************************************************
 //  Elmcrest - check for Juniors w/o an adult
 //
 //    Dependents must be accompanied by an adult at all times.
 //
 // *********************************************************

 public static boolean checkElmcrestJr(parmSlot slotParms, Connection con) {


   boolean error = false;

   
   //
   //  Check for any dependents
   //
   if (slotParms.mtype1.startsWith( "Dependent" )) {

      error = true;           // init to error

      //
      //  Make sure at least 1 adult
      //
      if (slotParms.mtype2.startsWith( "Primary" ) || slotParms.mtype2.startsWith( "Spouse" ) ||
          slotParms.mtype3.startsWith( "Primary" ) || slotParms.mtype3.startsWith( "Spouse" ) ||
          slotParms.mtype4.startsWith( "Primary" ) || slotParms.mtype4.startsWith( "Spouse" ) ||
          slotParms.mtype5.startsWith( "Primary" ) || slotParms.mtype5.startsWith( "Spouse" )) {       // if adults

         error = false;           // there is an adult - NO error
      }
   }

   if (slotParms.mtype2.startsWith( "Dependent" ) && error == false) {

      error = true;           // init to error

      //
      //  Make sure at least 1 adult
      //
      if (slotParms.mtype1.startsWith( "Primary" ) || slotParms.mtype1.startsWith( "Spouse" ) ||
          slotParms.mtype3.startsWith( "Primary" ) || slotParms.mtype3.startsWith( "Spouse" ) ||
          slotParms.mtype4.startsWith( "Primary" ) || slotParms.mtype4.startsWith( "Spouse" ) ||
          slotParms.mtype5.startsWith( "Primary" ) || slotParms.mtype5.startsWith( "Spouse" )) {       // if adults

         error = false;           // there is an adult - NO error
      }
   }

   if (slotParms.mtype3.startsWith( "Dependent" ) && error == false) {

      error = true;           // init to error

      //
      //  Make sure at least 1 adult
      //
      if (slotParms.mtype1.startsWith( "Primary" ) || slotParms.mtype1.startsWith( "Spouse" ) ||
          slotParms.mtype2.startsWith( "Primary" ) || slotParms.mtype2.startsWith( "Spouse" ) ||
          slotParms.mtype4.startsWith( "Primary" ) || slotParms.mtype4.startsWith( "Spouse" ) ||
          slotParms.mtype5.startsWith( "Primary" ) || slotParms.mtype5.startsWith( "Spouse" )) {       // if adults

         error = false;           // there is an adult - NO error
      }
   }

   if (slotParms.mtype4.startsWith( "Dependent" ) && error == false) {

      error = true;           // init to error

      //
      //  Make sure at least 1 adult
      //
      if (slotParms.mtype1.startsWith( "Primary" ) || slotParms.mtype1.startsWith( "Spouse" ) ||
          slotParms.mtype2.startsWith( "Primary" ) || slotParms.mtype2.startsWith( "Spouse" ) ||
          slotParms.mtype3.startsWith( "Primary" ) || slotParms.mtype3.startsWith( "Spouse" ) ||
          slotParms.mtype5.startsWith( "Primary" ) || slotParms.mtype5.startsWith( "Spouse" )) {       // if adults

         error = false;           // there is an adult - NO error
      }
   }

   if (slotParms.mtype5.startsWith( "Dependent" ) && error == false) {

      error = true;           // init to error

      //
      //  Make sure at least 1 adult
      //
      if (slotParms.mtype2.startsWith( "Primary" ) || slotParms.mtype2.startsWith( "Spouse" ) ||
          slotParms.mtype3.startsWith( "Primary" ) || slotParms.mtype3.startsWith( "Spouse" ) ||
          slotParms.mtype4.startsWith( "Primary" ) || slotParms.mtype4.startsWith( "Spouse" ) ||
          slotParms.mtype1.startsWith( "Primary" ) || slotParms.mtype1.startsWith( "Spouse" )) {       // if adults

         error = false;           // there is an adult - NO error
      }
   }
 
   return(error);
 }


 
 
 // *************************************************************************************
 //  Ramsey - Custom Processing (check for 3-some only time)
 // *************************************************************************************

 public static boolean checkRamsey3someTime(long date, int time, String day_name) {


   boolean status = false;

   long year = date / 10000;
   long month = (date - (year * 10000)) / 100;
   long day = date - ((year * 10000) + (month * 100));
   long mmdd = (month * 100) + day;                        // create mmdd value
   
   //
   //   3-some ONLY times from 7:15 AM to 9:03 AM on Thursdays from 4/01 to 10/31 (except mid July)
   //
   if (((mmdd > 400 && mmdd < 709) || (mmdd > 715 && mmdd < 1032)) && day_name.equals( "Thursday" )) {

      if (time > 714 && time < 904) {         // if tee time is between 7:15 and 9:03 AM

         status = true;                        // 3-some only time
      }
   }

   return(status);         // true = 3-somes only time
 }     
         
         
 
 // *************************************************************************************
 //  New Cannan - Custom Processing (check for 2-some only time)
 // *************************************************************************************

 public static boolean checkNewCan(long date, int time, String day_name) {


   boolean status = false;

   //
   //   2-some ONLY times from 7:00 AM to 7:25 AM on Weekends and holidays
   //
   if (date == Hdate1 || date == Hdate2b || date == Hdate3 || day_name.equals( "Saturday" ) || day_name.equals( "Sunday" )) {

      if (time > 659 && time < 726) {         // if tee time is between 7:00 and 7:25 AM

         status = true;                        // 2-some only time
      }
   }

   return(status);         // true = 2-somes only time
 }     
         
         
 // *************************************************************************************
 //  Dorset Field Cbub - Custom Processing (check for 2-some only time)
 // *************************************************************************************

 public static boolean checkDorsetFC(long date, int time, String day_name) {


   boolean status = false;

   long year = date / 10000;
   long month = (date - (year * 10000)) / 100;
   long day = date - ((year * 10000) + (month * 100));
   long mmdd = (month * 100) + day;                        // create mmdd value
   
   //
   //   2-some ONLY times from 7:40AM to 7:59AM on Mon, Tues, Wed, Fri from May 27 thru Aug 30
   //
   if ( (mmdd >= 527 && mmdd <= 830) && 
        (day_name.equals( "Monday" ) || day_name.equals( "Tuesday" ) || 
         day_name.equals( "Wednesday" ) || day_name.equals( "Friday" )) ) {

      if (time >= 740 && time <= 759) {         // if tee time is between 7:40 and 7:59 AM

         status = true;                        // 2-some only time
      }
   }

   return(status);         // true = 2-somes only time
 }


 // *************************************************************************************
 //  Mayfield Sand Ridge - Custom Processing (check for 2-some only time)
 // *************************************************************************************

 public static boolean checkMayfieldSR(long date, int time, String day_name) {


   boolean status = false;

   long year = date / 10000;
   long month = (date - (year * 10000)) / 100;
   long day = date - ((year * 10000) + (month * 100));
   long mmdd = (month * 100) + day;                        // create mmdd value
   
   //
   //   2-some ONLY times from 7:00 AM to 7:25 AM on Weekdends and holidays from May 26 thru Sept 6
   //
   if (((mmdd > 525 && mmdd < 907) && (day_name.equals( "Saturday" ) || day_name.equals( "Sunday" ))) || 
           date == Hdate1 || date == Hdate3) {

      if (time > 659 && time < 726) {         // if tee time is between 7:00 and 7:25 AM

         status = true;                        // 2-some only time
      }
   }

   return(status);         // true = 2-somes only time
 }
 
 
 // *************************************************************************************
 //  Longue Vue Club - Custom Processing (check for 2-some only time)
 // *************************************************************************************

 public static boolean check2LongueVue(long date, int time, String day_name) {


   boolean status = false;

   long year = date / 10000;
   long month = (date - (year * 10000)) / 100;
   long day = date - ((year * 10000) + (month * 100));
   long mmdd = (month * 100) + day;                        // create mmdd value
   
   //
   //   2-some ONLY times on Ladies Day from 7:00 AM to 10:29 AM from Mar 1 thru April 27 and from 7:00 - 10:09 from April 28 to Oct 10
   //
   if ((mmdd > 229 && mmdd < 428) && day_name.equals( "Tuesday" )) {

      if (time > 659 && time < 1030) {         // if tee time is between 7:00 and 10:29 AM

         status = true;                        // 2-some only time
      }
      
   } else if ((mmdd > 427 && mmdd < 1011) && day_name.equals( "Tuesday" )) {
            
      if (time > 659 && time < 1010) {         // if tee time is between 7:00 and 10:09 AM

         status = true;                        // 2-some only time
      }
   }

   return(status);         // true = 2-somes only time
 }
 
 
 // *************************************************************************************
 //  Desert Forest GC - Custom Processing (check for 2-some only time)
 //
 //      MUST UPDATE THIS EVERY YEAR - Change Dates!!!!!!!!!!!!
 //
 // *************************************************************************************

 public static boolean checkDesertForest(long date, int time, int fb, String day_name) {


   boolean status = false;

   long year = date / 10000;
   long month = (date - (year * 10000)) / 100;
   long day = date - ((year * 10000) + (month * 100));
   long mmdd = (month * 100) + day;                        // create mmdd value
   
   //
   //  Only necessary from Oct 1 - May 15
   //
   if ((mmdd > 1000 && mmdd < 1232) || (mmdd > 100 && mmdd < 516)) {   // if in season
      
      //
      //   Check each day for 2-some ONLY times
      //
      if (day_name.equals( "Friday" )) {

         if (time > 832 && time < 1004) {         // if tee time is between 8:33 and 10:03 AM

            //
            //   Alternate Front & Back tees each week
            //
            if (fb == 0 && (mmdd == 1023 || mmdd == 1106 || mmdd == 1120 || mmdd == 1204 || mmdd == 1218 || mmdd == 101 || 
                mmdd == 115 || mmdd == 129 || mmdd == 212 || mmdd == 226 || mmdd == 312 || mmdd == 326 || mmdd == 409 || 
                mmdd == 423 || mmdd == 507)) {

               status = true;                        // 2-some only time

            } else if (fb == 1 && (mmdd == 1030 || mmdd == 1113 || mmdd == 1127 || mmdd == 1211 || mmdd == 1225 || mmdd == 108 || 
                mmdd == 122 || mmdd == 205 || mmdd == 219 || mmdd == 305 || mmdd == 319 || mmdd == 402 || mmdd == 416 || 
                mmdd == 430)) {

               status = true;                        // 2-some only time
            }            
         }

      } else if (day_name.equals( "Sunday" )) {

         if (time > 759 && time < 1031 && fb == 0) {   // if tee time is between 8:00 and 10:30 AM on the Front

            status = true;                        // 2-some only time
         }

      } else if (day_name.equals( "Tuesday" )) {

         if (time > 759 && time < 857) {   // if tee time is between 8:00 and 8:56 AM 

            status = true;                        // 2-some only time
         }

      } else if (day_name.equals( "Wednesday" )) {

         if (time > 759 && time < 833) {   // if tee time is between 8:00 and 8:32 AM 

            status = true;                        // 2-some only time
         }

      } else if (day_name.equals( "Thursday" )) {

         if (time > 759 && time < 857) {   // if tee time is between 8:00 and 8:56 AM 

            status = true;                        // 2-some only time
         }

      }
   }      // end of IF in season
      
   /*    use to do this:
   //
   //   Check each day for 2-some ONLY times
   //
   if (day_name.equals( "Friday" )) {

      if ((mmdd >= 1023 && mmdd <= 1231) || (mmdd >= 101 && mmdd <= 507)) {       // 10/23 - 5/07

         if (time > 832 && time < 1004) {         // if tee time is between 8:33 and 10:03 AM

            //
            //   Alternate Front & Back tees each week
            //
            if (fb == 0 && (mmdd == 1023 || mmdd == 1106 || mmdd == 1120 || mmdd == 1204 || mmdd == 1218 || mmdd == 101 || 
                mmdd == 115 || mmdd == 129 || mmdd == 212 || mmdd == 226 || mmdd == 312 || mmdd == 326 || mmdd == 409 || 
                mmdd == 423 || mmdd == 507)) {

               status = true;                        // 2-some only time

            } else if (fb == 1 && (mmdd == 1030 || mmdd == 1113 || mmdd == 1127 || mmdd == 1211 || mmdd == 1225 || mmdd == 108 || 
                mmdd == 122 || mmdd == 205 || mmdd == 219 || mmdd == 305 || mmdd == 319 || mmdd == 402 || mmdd == 416 || 
                mmdd == 430)) {

               status = true;                        // 2-some only time
            }            
         }
      }


   } else if (day_name.equals( "Sunday" )) {

      if (mmdd > 750 && mmdd < 1018) {            // 8/31 - 10/17

         if (time > 729 && time < 831) {         // if tee time is between 7:30 and 8:30 AM

            status = true;                        // 2-some only time
         }

      } else if ((mmdd >= 1018 && mmdd <= 1231) || (mmdd >= 101 && mmdd <= 509)) {      // 10/18 - 5/09

         if (time > 829 && time < 1031) {         // if tee time is between 8:30 and 10:30 AM

            status = true;                        // 2-some only time
         }
      }
   }
    */    

   return(status);         // true = 2-somes only time
 }

 
 
 // *********************************************************
 //  Oahu CC - check for weekend times 
 //
 //    Weekends before 11 AM - no X's allowed.
 //
 // *********************************************************

 public static boolean checkOahuWeekends(parmSlot slotParms) {


   boolean error = false;

   int count = 0;
   
   //
   //  Saturday or Sunday - Count the players requested
   //
   if (slotParms.time > 629 && slotParms.time < 1100 &&
       (slotParms.player1.equalsIgnoreCase("x") || slotParms.player2.equalsIgnoreCase("x") ||
       slotParms.player3.equalsIgnoreCase("x") || slotParms.player4.equalsIgnoreCase("x") ||
       slotParms.player5.equalsIgnoreCase("x"))) {    // if between 6:30 and 11:00

       error = true;
   }

   return(error);       
 }

 
 
 // *************************************************************************************
 //  Los Coyotes - check for Primary Only tee time
 //
 //      Will have to update this each season and off-season!!!!!!!!!!!!
 //
 //   Called By:  above, and Member_sheet
 //
 // *************************************************************************************

 public static boolean checkLosCoyotesTimes(long date, int time, String day_name, String course) {


   boolean status = false;

   long year = date / 10000;
   long month = (date - (year * 10000)) / 100;
   long day = date - ((year * 10000) + (month * 100));
   long mmdd = (month * 100) + day;                        // create mmdd value
   
   //
   //  Tee Times differ when in season or out of season 
   //
  // if ((mmdd > 1000 && mmdd < 1232) || (mmdd > 100 && mmdd < 516)) {   // if in season  (add dates later)
      
      //
      //   Check for Primary Only time
      //
      if (day_name.equals( "Tuesday" ) && course.equals("Valley Vista")) {

         if (time == 656 || time == 800 || time == 856 || time == 1000 || time == 1056 || time == 1200 || time == 1256 || time == 1400 || time == 1456) {

               status = true;                        // primary only time
         }

      } else if (day_name.equals( "Wednesday" ) && course.equals("Valley Vista")) {

         if (time == 1328 || time == 1400 || time == 1432 || time == 1504 || time == 1528) {

            status = true;                        // primary only time
         }

      } else if (day_name.equals( "Thursday" ) && course.equals("Valley Vista")) {

         if (time == 1256 || time == 1400 || time == 1456) {

            status = true;                        // primary only time
         }

      } else if (day_name.equals( "Thursday" ) && course.equals("Lake Vista")) {

         if (time == 1104) {         

            status = true;                        // primary only time
         }

      } else if (day_name.equals( "Friday" ) && course.equals("Valley Vista")) {

         if (time == 656 || time == 800 || time == 856 || time == 1000 || time == 1056 || time == 1200 || 
             time == 1208 || time == 1256 || time == 1400 || time == 1408 || time == 1456 || time == 1528) {

            status = true;                        // primary only time
         }

      } else if (day_name.equals( "Sunday" ) && course.equals("Valley Vista")) {

         if (time == 1056 || time == 1200 || time == 1256 || time == 1400 || time == 1456) {

            status = true;                        // primary only time
         }
      }
  // }      // end of IF in season
      
   return(status);         // true = Primary Only time
 }
 
 
 
 
 // *************************************************************************************
 //  The CC - Custom Processing (check date and time of day for 2-some only time)
 // *************************************************************************************

 public static boolean checkTheCC(long date, int time, String day_name) {


   boolean status = false;

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
   //   2-some ONLY times from 7:30 AM to 8:00 AM for the following dates:
   //
   //      Every Tues, Wed & Thurs from 4/01 - 8/31
   //
   //      Every Tues, Wed & Thurs from 9/04 - 10/31
   //
   //
   if (mmdd > 400 && mmdd < 832 && (day_name.equals( "Tuesday" ) || day_name.equals( "Wednesday" ) || day_name.equals( "Thursday" ))) {

      if ( (time > 729 && time < 801) || (time > 1629 && time < 1701) ) {         // if tee time is between 7:30 and 8:00 AM OR 4:30-5PM

         status = true;                        // 2-some only time
      }
   }

   if (mmdd > 903 && mmdd < 1032 && (day_name.equals( "Tuesday" ) || day_name.equals( "Wednesday" ) || day_name.equals( "Thursday" ))) {

      if (time > 744 && time < 816) {         // if tee time is between 7:45 and 8:15 AM

         status = true;                        // 2-some only time
      }
   }

   return(status);         // true = 2-somes only time
 }


 // *************************************************************************************
 //  Greenwich CC - Custom Processing (check date and time of day for 2-some only time)
 // *************************************************************************************

 public static boolean checkGreenwich(long date, int time) {


   boolean status = false;

   //
   //  Determine date values - month, day, year
   //
   long year = date / 10000;
   long month = (date - (year * 10000)) / 100;
   long day = date - ((year * 10000) + (month * 100));
   long shortDate = (month * 100) + day;                        // create mmdd value

   //
   //      ************* See also SystemUtils ********************
   //
   //   2-some ONLY times from 7:00 AM to 7:48 AM for the specified dates.
   //
   //
   if (shortDate == 526 || shortDate == 527 || shortDate == 528 || shortDate == 602 || shortDate == 603 ||
       shortDate == 609 || shortDate == 610 || shortDate == 616 || shortDate == 617 || shortDate == 623 ||
       shortDate == 624 || shortDate == 701 || shortDate == 707 || shortDate == 708 || shortDate == 721 ||
       shortDate == 804 || shortDate == 811 || shortDate == 812 || shortDate == 818 || shortDate == 819 ||
       shortDate == 826) {

      if (time > 659 && time < 749) {         // if tee time is between 7:00 and 7:48 AM

         status = true;                        // 2-some only time
      }
   }

   return(status);         // true = 2-somes only time
 }


/**
 //************************************************************************
 //
 //  The CC - special Guest processing.
 //
 //     At this point we know there is more than one guest
 //     in this tee time, it is The CC, it is in season, and on a restricted course.
 //
 //     Restrictions:
 //
 //       Members (per family) can have up to 6 guests per month
 //       and 18 per season, where the season is April 1 to Oct 31.
 //       Event rounds are NOT counted.
 //
 //************************************************************************
 **/

 public static boolean checkTCCguests(parmSlot slotParms, Connection con) {


   PreparedStatement pstmt = null;
   PreparedStatement pstmt4 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   boolean error = false;

   int ttime = 0;
   int countm = 0;         // number of guests for month
   int counts = 0;         // number of guests for season

   String user = "";
   String mNum = "";
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";

   //
   //  break down date of tee time
   //
   long yy = slotParms.date / 10000;                             // get year
   long mm = (slotParms.date - (yy * 10000)) / 100;              // get month
   //long dd = (slotParms.date - (yy * 10000)) - (mm * 100);       // get day

   long sdate = (yy * 10000) + 400;       // yyyy0400
   long edate = (yy * 10000) + 1032;      // yyyy1032
   long tdate = 0;

   int imm = (int)mm;
   int iyy = (int)yy;
   int i = 0;


   String [] usergA = new String [5];       // array to hold the members' usernames
   String [] userA = new String [5];        // array to hold the usernames
   String [] playerA = new String [5];      // array to hold the player's names
   String [] mnumA = new String [5];       // array to hold the players' membership types

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

   usergA[0] = slotParms.userg1;                  // copy userg values into array
   usergA[1] = slotParms.userg2;
   usergA[2] = slotParms.userg3;
   usergA[3] = slotParms.userg4;
   usergA[4] = slotParms.userg5;

   mnumA[0] = slotParms.mNum1;
   mnumA[1] = slotParms.mNum2;
   mnumA[2] = slotParms.mNum3;
   mnumA[3] = slotParms.mNum4;
   mnumA[4] = slotParms.mNum5;


   //
   //  Remove any duplicate family members - only check one user for the family
   //
   if (!mnumA[0].equals( "" )) {        // if mnum exists
     
      if (mnumA[1].equals( mnumA[0] )) {        // if mnum is the same

         mnumA[1] = "";
         userA[1] = "";
      }
      if (mnumA[2].equals( mnumA[0] )) {        // if mnum is the same

         mnumA[2] = "";
         userA[2] = "";
      }
      if (mnumA[3].equals( mnumA[0] )) {        // if mnum is the same

         mnumA[3] = "";
         userA[3] = "";
      }
      if (mnumA[4].equals( mnumA[0] )) {        // if mnum is the same

         mnumA[4] = "";
         userA[4] = "";
      }
   }
     
   if (!mnumA[1].equals( "" )) {        // if mnum exists

      if (mnumA[2].equals( mnumA[1] )) {        // if mnum is the same

         mnumA[2] = "";
         userA[2] = "";
      }
      if (mnumA[3].equals( mnumA[1] )) {        // if mnum is the same

         mnumA[3] = "";
         userA[3] = "";
      }
      if (mnumA[4].equals( mnumA[1] )) {        // if mnum is the same

         mnumA[4] = "";
         userA[4] = "";
      }
   }

   if (!mnumA[2].equals( "" )) {        // if mnum exists

      if (mnumA[3].equals( mnumA[2] )) {        // if mnum is the same

         mnumA[3] = "";
         userA[3] = "";
      }
      if (mnumA[4].equals( mnumA[2] )) {        // if mnum is the same

         mnumA[4] = "";
         userA[4] = "";
      }
   }

   if (!mnumA[3].equals( "" )) {        // if mnum exists

      if (mnumA[4].equals( mnumA[3] )) {        // if mnum is the same

         mnumA[4] = "";
         userA[4] = "";
      }
   }

  
   try {

      //
      //  Check each player
      //
      loop1:
      for (i = 0; i < 5; i++) {

         if (!userA[i].equals( "" )) {       // if member

            countm = 0;
            counts = 0;
              
            //
            //  count # of guests for this user in this tee time
            //
            if (usergA[0].equals( userA[i] ) && !playerA[0].startsWith("Tournament")) {

               countm++;           // count # of guests
               counts++;       
            }
            if (usergA[1].equals( userA[i] ) && !playerA[1].startsWith("Tournament")) {

               countm++;           // count # of guests
               counts++;
            }
            if (usergA[2].equals( userA[i] ) && !playerA[2].startsWith("Tournament")) {

               countm++;           // count # of guests
               counts++;
            }
            if (usergA[3].equals( userA[i] ) && !playerA[3].startsWith("Tournament")) {

               countm++;           // count # of guests
               counts++;
            }
            if (usergA[4].equals( userA[i] ) && !playerA[4].startsWith("Tournament")) {

               countm++;           // count # of guests
               counts++;
            }
              
            if (countm > 0) {
              
               //  get this user's mNum
                 
               mNum = mnumA[i];

               if (!mNum.equals( "" )) {     // if there is one specified

                  //
                  //  get all users with matching mNum
                  //
                  pstmt4 = con.prepareStatement (
                     "SELECT username FROM member2b WHERE memNum = ?");

                  pstmt4.clearParameters();        // clear the parms
                  pstmt4.setString(1, mNum);
                  rs2 = pstmt4.executeQuery();      // execute the prepared stmt

                  while (rs2.next()) {

                     user = rs2.getString(1);       // get the username

                     //
                     //   Check teecurr and teepast for other guest times for this member for the season
                     //
                     pstmt = con.prepareStatement (
                        "SELECT player1, player2, player3, player4, player5, userg1, userg2, userg3, userg4, userg5 " +
                        "FROM teepast2 " +
                        "WHERE date < ? AND date > ? AND event = '' AND " +
                        "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?)");

                     pstmt.clearParameters();
                     pstmt.setLong(1, edate);
                     pstmt.setLong(2, sdate);
                     pstmt.setString(3, user);
                     pstmt.setString(4, user);
                     pstmt.setString(5, user);
                     pstmt.setString(6, user);
                     pstmt.setString(7, user);
                     rs = pstmt.executeQuery();

                     while (rs.next()) {

                        player1 = rs.getString(1);     
                        player2 = rs.getString(2);     
                        player3 = rs.getString(3);     
                        player4 = rs.getString(4);     
                        player5 = rs.getString(5);     
                        userg1 = rs.getString(6);     
                        userg2 = rs.getString(7);
                        userg3 = rs.getString(8);
                        userg4 = rs.getString(9);
                        userg5 = rs.getString(10);
                          
                        if (userg1.equals( user ) && !player1.startsWith("Tournament")) {
                          
                           counts++;     // bump # of guests
                        }
                        if (userg2.equals( user ) && !player2.startsWith("Tournament")) {

                           counts++;     // bump # of guests
                        }
                        if (userg3.equals( user ) && !player3.startsWith("Tournament")) {

                           counts++;     // bump # of guests
                        }
                        if (userg4.equals( user ) && !player4.startsWith("Tournament")) {

                           counts++;     // bump # of guests
                        }
                        if (userg5.equals( user ) && !player5.startsWith("Tournament")) {

                           counts++;     // bump # of guests
                        }
                     }      // end of WHILE

                     pstmt.close();

                     pstmt = con.prepareStatement (
                        "SELECT date, time, player1, player2, player3, player4, player5, userg1, userg2, userg3, userg4, userg5 " +
                        "FROM teecurr2 " +
                        "WHERE date < ? AND date > ? AND event = '' AND " +
                        "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?)");

                     pstmt.clearParameters();
                     pstmt.setLong(1, edate);
                     pstmt.setLong(2, sdate);
                     pstmt.setString(3, user);
                     pstmt.setString(4, user);
                     pstmt.setString(5, user);
                     pstmt.setString(6, user);
                     pstmt.setString(7, user);
                     rs = pstmt.executeQuery();

                     while (rs.next()) {

                        tdate = rs.getLong(1);
                        ttime = rs.getInt(2);
                        player1 = rs.getString(3);     
                        player2 = rs.getString(4);     
                        player3 = rs.getString(5);     
                        player4 = rs.getString(6);     
                        player5 = rs.getString(7);     
                        userg1 = rs.getString(8);
                        userg2 = rs.getString(9);
                        userg3 = rs.getString(10);
                        userg4 = rs.getString(11);
                        userg5 = rs.getString(12);

                        if (tdate != slotParms.date || ttime != slotParms.time) {   // if not this tee time
                          
                           if (userg1.equals( user ) && !player1.startsWith("Tournament")) {

                              counts++;     // bump # of guests
                           }
                           if (userg2.equals( user ) && !player2.startsWith("Tournament")) {

                              counts++;     // bump # of guests
                           }
                           if (userg3.equals( user ) && !player3.startsWith("Tournament")) {

                              counts++;     // bump # of guests
                           }
                           if (userg4.equals( user ) && !player4.startsWith("Tournament")) {

                              counts++;     // bump # of guests
                           }
                           if (userg5.equals( user ) && !player5.startsWith("Tournament")) {

                              counts++;     // bump # of guests
                           }
                        }
                     }      // end of WHILE

                     pstmt.close();

                     //
                     //   Check teecurr and teepast for other guest times for this member for the month
                     //
                     pstmt = con.prepareStatement (
                        "SELECT player1, player2, player3, player4, player5, userg1, userg2, userg3, userg4, userg5 " +
                        "FROM teepast2 " +
                        "WHERE mm = ? AND yy = ? AND event = '' AND " +
                        "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?)");

                     pstmt.clearParameters();
                     pstmt.setInt(1, imm);
                     pstmt.setInt(2, iyy);
                     pstmt.setString(3, user);
                     pstmt.setString(4, user);
                     pstmt.setString(5, user);
                     pstmt.setString(6, user);
                     pstmt.setString(7, user);
                     rs = pstmt.executeQuery();

                     while (rs.next()) {

                        player1 = rs.getString(1);     
                        player2 = rs.getString(2);     
                        player3 = rs.getString(3);     
                        player4 = rs.getString(4);     
                        player5 = rs.getString(5);     
                        userg1 = rs.getString(6);
                        userg2 = rs.getString(7);
                        userg3 = rs.getString(8);
                        userg4 = rs.getString(9);
                        userg5 = rs.getString(10);

                        if (userg1.equals( user ) && !player1.startsWith("Tournament")) {

                           countm++;     // bump # of guests
                        }
                        if (userg2.equals( user ) && !player2.startsWith("Tournament")) {

                           countm++;     // bump # of guests
                        }
                        if (userg3.equals( user ) && !player3.startsWith("Tournament")) {

                           countm++;     // bump # of guests
                        }
                        if (userg4.equals( user ) && !player4.startsWith("Tournament")) {

                           countm++;     // bump # of guests
                        }
                        if (userg5.equals( user ) && !player5.startsWith("Tournament")) {

                           countm++;     // bump # of guests
                        }
                     }      // end of WHILE

                     pstmt.close();

                     pstmt = con.prepareStatement (
                        "SELECT date, time, player1, player2, player3, player4, player5, userg1, userg2, userg3, userg4, userg5 " +
                        "FROM teecurr2 " +
                        "WHERE mm = ? AND yy = ? AND event = '' AND " +
                        "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?)");

                     pstmt.clearParameters();
                     pstmt.setInt(1, imm);
                     pstmt.setInt(2, iyy);
                     pstmt.setString(3, user);
                     pstmt.setString(4, user);
                     pstmt.setString(5, user);
                     pstmt.setString(6, user);
                     pstmt.setString(7, user);
                     rs = pstmt.executeQuery();

                     while (rs.next()) {

                        tdate = rs.getLong(1);
                        ttime = rs.getInt(2);
                        player1 = rs.getString(3);     
                        player2 = rs.getString(4);     
                        player3 = rs.getString(5);     
                        player4 = rs.getString(6);     
                        player5 = rs.getString(7);     
                        userg1 = rs.getString(8);
                        userg2 = rs.getString(9);
                        userg3 = rs.getString(10);
                        userg4 = rs.getString(11);
                        userg5 = rs.getString(12);

                        if (tdate != slotParms.date || ttime != slotParms.time) {   // if not this tee time

                           if (userg1.equals( user ) && !player1.startsWith("Tournament")) {

                              countm++;     // bump # of guests
                           }
                           if (userg2.equals( user ) && !player2.startsWith("Tournament")) {

                              countm++;     // bump # of guests
                           }
                           if (userg3.equals( user ) && !player3.startsWith("Tournament")) {

                              countm++;     // bump # of guests
                           }
                           if (userg4.equals( user ) && !player4.startsWith("Tournament")) {

                              countm++;     // bump # of guests
                           }
                           if (userg5.equals( user ) && !player5.startsWith("Tournament")) {

                              countm++;     // bump # of guests
                           }
                        }
                     }      // end of WHILE

                     pstmt.close();

                  }
                  pstmt4.close();

                  if (counts > 18 || countm > 6) {          // if either count puts user over the limit

                     error = true;                          // indicate error
                     slotParms.player = playerA[i];         // save player name for error message
                     break loop1;
                  }
                    
               }
            }
         }
      }              // end of FOR loop (do each player)

   } catch (Exception e) {

       Utilities.logError("Error checking for The CC guests - verifyCustom.checkTCCguests " + e.getMessage());
      
   } finally {

       try { rs.close(); }
       catch (Exception ignore) {}

       try { rs2.close(); }
       catch (Exception ignore) {}
       
       try { pstmt.close(); }
       catch (Exception ignore) {}

       try { pstmt4.close(); }
       catch (Exception ignore) {}

   }

   return(error);
   
 }


 // *********************************************************
 //
 //  Merrill Hills - check mships for display on pro tee sheet
 //
 // *********************************************************

 public static void checkMerrill(parmSlot slotParms) {


   slotParms.custom_disp1 = "";
   slotParms.custom_disp2 = "";
   slotParms.custom_disp3 = "";
   slotParms.custom_disp4 = "";
   slotParms.custom_disp5 = "";

   if (slotParms.mship1.equals( "Athletic" )) {

      slotParms.custom_disp1 = " *";          // to be added to player name in Proshop_sheet
   }
   if (slotParms.mship2.equals( "Athletic" )) {

      slotParms.custom_disp2 = " *";          // to be added to player name in Proshop_sheet
   }
   if (slotParms.mship3.equals( "Athletic" )) {

      slotParms.custom_disp3 = " *";          // to be added to player name in Proshop_sheet
   }
   if (slotParms.mship4.equals( "Athletic" )) {

      slotParms.custom_disp4 = " *";          // to be added to player name in Proshop_sheet
   }
   if (slotParms.mship5.equals( "Athletic" )) {

      slotParms.custom_disp5 = " *";          // to be added to player name in Proshop_sheet
   }

   if (slotParms.mship1.equals( "Century Club" )) {

      slotParms.custom_disp1 = " $";          // to be added to player name in Proshop_sheet
   }
   if (slotParms.mship2.equals( "Century Club" )) {

      slotParms.custom_disp2 = " $";          // to be added to player name in Proshop_sheet
   }
   if (slotParms.mship3.equals( "Century Club" )) {

      slotParms.custom_disp3 = " $";          // to be added to player name in Proshop_sheet
   }
   if (slotParms.mship4.equals( "Century Club" )) {

      slotParms.custom_disp4 = " $";          // to be added to player name in Proshop_sheet
   }
   if (slotParms.mship5.equals( "Century Club" )) {

      slotParms.custom_disp5 = " $";          // to be added to player name in Proshop_sheet
   }

 }

 // *********************************************************
 //
 //  Merrill Hills - check mships for display on pro tee sheet (for _slotm)
 //
 // *********************************************************

 public static void checkMerrillm(parmSlotm parm) {


   parm.custom_disp1 = "";
   parm.custom_disp2 = "";
   parm.custom_disp3 = "";
   parm.custom_disp4 = "";
   parm.custom_disp5 = "";
   parm.custom_disp6 = "";
   parm.custom_disp7 = "";
   parm.custom_disp8 = "";
   parm.custom_disp9 = "";
   parm.custom_disp10 = "";
   parm.custom_disp11 = "";
   parm.custom_disp12 = "";
   parm.custom_disp13 = "";
   parm.custom_disp14 = "";
   parm.custom_disp15 = "";
   parm.custom_disp16 = "";
   parm.custom_disp17 = "";
   parm.custom_disp18 = "";
   parm.custom_disp19 = "";
   parm.custom_disp20 = "";
   parm.custom_disp21 = "";
   parm.custom_disp22 = "";
   parm.custom_disp23 = "";
   parm.custom_disp24 = "";
   parm.custom_disp25 = "";


   if (parm.mship1.equals( "Athletic" )) {

      parm.custom_disp1 = " *";          
   }
   if (parm.mship2.equals( "Athletic" )) {

      parm.custom_disp2 = " *";          
   }
   if (parm.mship3.equals( "Athletic" )) {

      parm.custom_disp3 = " *";          
   }
   if (parm.mship4.equals( "Athletic" )) {

      parm.custom_disp4 = " *";          
   }
   if (parm.mship5.equals( "Athletic" )) {

      parm.custom_disp5 = " *";          
   }
   if (parm.mship6.equals( "Athletic" )) {

      parm.custom_disp6 = " *";
   }
   if (parm.mship7.equals( "Athletic" )) {

      parm.custom_disp7 = " *";
   }
   if (parm.mship8.equals( "Athletic" )) {

      parm.custom_disp8 = " *";
   }
   if (parm.mship9.equals( "Athletic" )) {

      parm.custom_disp9 = " *";
   }
   if (parm.mship10.equals( "Athletic" )) {

      parm.custom_disp10 = " *";
   }
   if (parm.mship11.equals( "Athletic" )) {

      parm.custom_disp11 = " *";
   }
   if (parm.mship12.equals( "Athletic" )) {

      parm.custom_disp12 = " *";
   }
   if (parm.mship13.equals( "Athletic" )) {

      parm.custom_disp13 = " *";
   }
   if (parm.mship14.equals( "Athletic" )) {

      parm.custom_disp14 = " *";
   }
   if (parm.mship15.equals( "Athletic" )) {

      parm.custom_disp15 = " *";
   }
   if (parm.mship16.equals( "Athletic" )) {

      parm.custom_disp16 = " *";
   }
   if (parm.mship17.equals( "Athletic" )) {

      parm.custom_disp17 = " *";
   }
   if (parm.mship18.equals( "Athletic" )) {

      parm.custom_disp18 = " *";
   }
   if (parm.mship19.equals( "Athletic" )) {

      parm.custom_disp19 = " *";
   }
   if (parm.mship20.equals( "Athletic" )) {

      parm.custom_disp20 = " *";
   }
   if (parm.mship21.equals( "Athletic" )) {

      parm.custom_disp21 = " *";
   }
   if (parm.mship22.equals( "Athletic" )) {

      parm.custom_disp22 = " *";
   }
   if (parm.mship23.equals( "Athletic" )) {

      parm.custom_disp23 = " *";
   }
   if (parm.mship24.equals( "Athletic" )) {

      parm.custom_disp24 = " *";
   }
   if (parm.mship25.equals( "Athletic" )) {

      parm.custom_disp25 = " *";
   }

   if (parm.mship1.equals( "Century Club" )) {

      parm.custom_disp1 = " $";
   }
   if (parm.mship2.equals( "Century Club" )) {

      parm.custom_disp2 = " $";
   }
   if (parm.mship3.equals( "Century Club" )) {

      parm.custom_disp3 = " $";
   }
   if (parm.mship4.equals( "Century Club" )) {

      parm.custom_disp4 = " $";
   }
   if (parm.mship5.equals( "Century Club" )) {

      parm.custom_disp5 = " $";
   }
   if (parm.mship6.equals( "Century Club" )) {

      parm.custom_disp6 = " $";
   }
   if (parm.mship7.equals( "Century Club" )) {

      parm.custom_disp7 = " $";
   }
   if (parm.mship8.equals( "Century Club" )) {

      parm.custom_disp8 = " $";
   }
   if (parm.mship9.equals( "Century Club" )) {

      parm.custom_disp9 = " $";
   }
   if (parm.mship10.equals( "Century Club" )) {

      parm.custom_disp10 = " $";
   }
   if (parm.mship11.equals( "Century Club" )) {

      parm.custom_disp11 = " $";
   }
   if (parm.mship12.equals( "Century Club" )) {

      parm.custom_disp12 = " $";
   }
   if (parm.mship13.equals( "Century Club" )) {

      parm.custom_disp13 = " $";
   }
   if (parm.mship14.equals( "Century Club" )) {

      parm.custom_disp14 = " $";
   }
   if (parm.mship15.equals( "Century Club" )) {

      parm.custom_disp15 = " $";
   }
   if (parm.mship16.equals( "Century Club" )) {

      parm.custom_disp16 = " $";
   }
   if (parm.mship17.equals( "Century Club" )) {

      parm.custom_disp17 = " $";
   }
   if (parm.mship18.equals( "Century Club" )) {

      parm.custom_disp18 = " $";
   }
   if (parm.mship19.equals( "Century Club" )) {

      parm.custom_disp19 = " $";
   }
   if (parm.mship20.equals( "Century Club" )) {

      parm.custom_disp20 = " $";
   }
   if (parm.mship21.equals( "Century Club" )) {

      parm.custom_disp21 = " $";
   }
   if (parm.mship22.equals( "Century Club" )) {

      parm.custom_disp22 = " $";
   }
   if (parm.mship23.equals( "Century Club" )) {

      parm.custom_disp23 = " $";
   }
   if (parm.mship24.equals( "Century Club" )) {

      parm.custom_disp24 = " $";
   }
   if (parm.mship25.equals( "Century Club" )) {

      parm.custom_disp25 = " $";
   }

 }


 // *********************************************************
 //
 //  Wilmington - check mship subtypes for display on pro tee sheet (for _slotm)
 //
 // *********************************************************

 public static void checkWilmington(parmSlotm parm) {


   parm.custom_disp1 = "";
   parm.custom_disp2 = "";
   parm.custom_disp3 = "";
   parm.custom_disp4 = "";
   parm.custom_disp5 = "";
   parm.custom_disp6 = "";
   parm.custom_disp7 = "";
   parm.custom_disp8 = "";
   parm.custom_disp9 = "";
   parm.custom_disp10 = "";
   parm.custom_disp11 = "";
   parm.custom_disp12 = "";
   parm.custom_disp13 = "";
   parm.custom_disp14 = "";
   parm.custom_disp15 = "";
   parm.custom_disp16 = "";
   parm.custom_disp17 = "";
   parm.custom_disp18 = "";
   parm.custom_disp19 = "";
   parm.custom_disp20 = "";
   parm.custom_disp21 = "";
   parm.custom_disp22 = "";
   parm.custom_disp23 = "";
   parm.custom_disp24 = "";
   parm.custom_disp25 = "";


   if (!parm.mstype1.equals( "" )) {

      parm.custom_disp1 = parm.mstype1;
   }
   if (!parm.mstype2.equals( "" )) {

      parm.custom_disp2 = parm.mstype2;
   }
   if (!parm.mstype3.equals( "" )) {

      parm.custom_disp3 = parm.mstype3;
   }
   if (!parm.mstype4.equals( "" )) {

      parm.custom_disp4 = parm.mstype4;
   }
   if (!parm.mstype5.equals( "" )) {

      parm.custom_disp5 = parm.mstype5;
   }
   if (!parm.mstype6.equals( "" )) {

      parm.custom_disp6 = parm.mstype6;
   }
   if (!parm.mstype7.equals( "" )) {

      parm.custom_disp7 = parm.mstype7;
   }
   if (!parm.mstype8.equals( "" )) {

      parm.custom_disp8 = parm.mstype8;
   }
   if (!parm.mstype9.equals( "" )) {

      parm.custom_disp9 = parm.mstype9;
   }
   if (!parm.mstype10.equals( "" )) {

      parm.custom_disp10 = parm.mstype10;
   }
   if (!parm.mstype11.equals( "" )) {

      parm.custom_disp11 = parm.mstype11;
   }
   if (!parm.mstype12.equals( "" )) {

      parm.custom_disp12 = parm.mstype12;
   }
   if (!parm.mstype13.equals( "" )) {

      parm.custom_disp13 = parm.mstype13;
   }
   if (!parm.mstype14.equals( "" )) {

      parm.custom_disp14 = parm.mstype14;
   }
   if (!parm.mstype15.equals( "" )) {

      parm.custom_disp15 = parm.mstype15;
   }
   if (!parm.mstype16.equals( "" )) {

      parm.custom_disp16 = parm.mstype16;
   }
   if (!parm.mstype17.equals( "" )) {

      parm.custom_disp17 = parm.mstype17;
   }
   if (!parm.mstype18.equals( "" )) {

      parm.custom_disp18 = parm.mstype18;
   }
   if (!parm.mstype19.equals( "" )) {

      parm.custom_disp19 = parm.mstype19;
   }
   if (!parm.mstype20.equals( "" )) {

      parm.custom_disp20 = parm.mstype20;
   }
   if (!parm.mstype21.equals( "" )) {

      parm.custom_disp21 = parm.mstype21;
   }
   if (!parm.mstype22.equals( "" )) {

      parm.custom_disp22 = parm.mstype22;
   }
   if (!parm.mstype23.equals( "" )) {

      parm.custom_disp23 = parm.mstype23;
   }
   if (!parm.mstype24.equals( "" )) {

      parm.custom_disp24 = parm.mstype24;
   }
   if (!parm.mstype25.equals( "" )) {

      parm.custom_disp25 = parm.mstype25;
   }

 }



 // *********************************************************
 //
 //  Sonnenalp - get guest rates for each guest type and add to tee time for display on tee sheet
 //
 // *********************************************************

 public static void addGuestRates(parmSlot slotParms) {

   long date = slotParms.date;
   int time = slotParms.time;
   int p91 = slotParms.p91;
   int p92 = slotParms.p92;
   int p93 = slotParms.p93;
   int p94 = slotParms.p94;
   int p95 = slotParms.p95;


   //
   //  Check for any guests
   //
   if (!slotParms.g1.equals( "" )) {

      slotParms.custom_disp1 = getGuestRate(date, time, p91, slotParms.g1);        // get the guest fee for this guest type
   }

   if (!slotParms.g2.equals( "" )) {

      slotParms.custom_disp2 = getGuestRate(date, time, p92, slotParms.g2);        // get the guest fee for this guest type
   }

   if (!slotParms.g3.equals( "" )) {

      slotParms.custom_disp3 = getGuestRate(date, time, p93, slotParms.g3);        // get the guest fee for this guest type
   }

   if (!slotParms.g4.equals( "" )) {

      slotParms.custom_disp4 = getGuestRate(date, time, p94, slotParms.g4);        // get the guest fee for this guest type
   }

   if (!slotParms.g5.equals( "" )) {

      slotParms.custom_disp5 = getGuestRate(date, time, p95, slotParms.g5);        // get the guest fee for this guest type
   }

 }

 // *********************************************************
 //
 //  Sonnenalp - get guest rates for each guest type and add to tee time for display on tee sheet (multiple tee time requests)
 //
 //       NOTE:  Sonnenalp does NOT allow 5-somes!!
 //
 // *********************************************************

 public static void addGuestRatesM(parmSlotm parm) {

   long date = parm.date;
   int time1 = parm.time1;
   int time2 = parm.time2;
   int time3 = parm.time3;
   int time4 = parm.time4;
   int time5 = parm.time5;
   int time = time1;
     
   String g1 = parm.g[0];         // get the guest types
   String g2 = parm.g[1];
   String g3 = parm.g[2];
   String g4 = parm.g[3];
   String g5 = parm.g[4];
   String g6 = parm.g[5];
   String g7 = parm.g[6];
   String g8 = parm.g[7];
   String g9 = parm.g[8];
   String g10 = parm.g[9];
   String g11 = parm.g[10];
   String g12 = parm.g[11];
   String g13 = parm.g[12];
   String g14 = parm.g[13];
   String g15 = parm.g[14];
   String g16 = parm.g[15];
   String g17 = parm.g[16];
   String g18 = parm.g[17];
   String g19 = parm.g[18];
   String g20 = parm.g[19];


   //
   //  Check for any guests
   //
   if (!g1.equals( "" )) {

      parm.custom_disp1 = getGuestRate(date, time, parm.p91, g1);        // get the guest fee for this guest type
   }

   if (!g2.equals( "" )) {

      parm.custom_disp2 = getGuestRate(date, time, parm.p92, g2);
   }

   if (!g3.equals( "" )) {

      parm.custom_disp3 = getGuestRate(date, time, parm.p93, g3);
   }

   if (!g4.equals( "" )) {

      parm.custom_disp4 = getGuestRate(date, time, parm.p94, g4);
   }

   time = time2; 

   if (!g5.equals( "" )) {

      parm.custom_disp5 = getGuestRate(date, time, parm.p95, g5);
   }

   if (!g6.equals( "" )) {

      parm.custom_disp6 = getGuestRate(date, time, parm.p96, g6);
   }

   if (!g7.equals( "" )) {

      parm.custom_disp7 = getGuestRate(date, time, parm.p97, g7);
   }

   if (!g8.equals( "" )) {

      parm.custom_disp8 = getGuestRate(date, time, parm.p98, g8);
   }

   time = time3;

   if (!g9.equals( "" )) {

      parm.custom_disp9 = getGuestRate(date, time, parm.p99, g9);
   }

   if (!g10.equals( "" )) {

      parm.custom_disp10 = getGuestRate(date, time, parm.p910, g10);
   }

   if (!g11.equals( "" )) {

      parm.custom_disp11 = getGuestRate(date, time, parm.p911, g11);        // get the guest fee for this guest type
   }

   if (!g12.equals( "" )) {

      parm.custom_disp12 = getGuestRate(date, time, parm.p912, g12);
   }

   time = time4;

   if (!g13.equals( "" )) {

      parm.custom_disp13 = getGuestRate(date, time, parm.p913, g13);
   }

   if (!g14.equals( "" )) {

      parm.custom_disp14 = getGuestRate(date, time, parm.p914, g14);
   }

   if (!g15.equals( "" )) {

      parm.custom_disp15 = getGuestRate(date, time, parm.p915, g15);
   }

   if (!g16.equals( "" )) {

      parm.custom_disp16 = getGuestRate(date, time, parm.p916, g16);
   }

   time = time5;

   if (!g17.equals( "" )) {

      parm.custom_disp17 = getGuestRate(date, time, parm.p917, g17);
   }

   if (!g18.equals( "" )) {

      parm.custom_disp18 = getGuestRate(date, time, parm.p918, g18);
   }

   if (!g19.equals( "" )) {

      parm.custom_disp19 = getGuestRate(date, time, parm.p919, g19);
   }

   if (!g20.equals( "" )) {

      parm.custom_disp20 = getGuestRate(date, time, parm.p920, g20);
   }

 }

 // *********************************************************
 //  Sonnenalp - get guest rate for specified guest type
 // *********************************************************

 public static String getGuestRate(long date, int time, int p9, String gtype) {


   String cost = "";

   long sdate = date - ((date / 10000) * 10000);       // get mmdd (short date)
     
   int morning = 1330;                //  end of morning times
   int twilight = 1529;                // start of twilight


   //
   //  Sonnenalp Guests - determine fee based on time of year and time of day
   //
   //    Low Season  = 4/01 - 6/14 and 9/16 - 10/28
   //    High Season = 6/15 - 9/15
   //
   //    Morning = open up to 1:20 (inclusive)
   //    Mid Day = 1:30 - 3:20 (inclusive)
   //    Twilight = after 3:29
   //
   if (gtype.equals( "Hotel" ) || gtype.equals( "Unescorted Guest" )) {   // if guest type = Hotel or Unescorted

      if (sdate > 614 && sdate < 916) {       // if High Season

         if (time > twilight) {             // if twilight

            cost = "50.00";

            if (p9 == 1) {             // if 9 hole round

               cost = "25.00";
            }

         } else {        // NOT twilight

            if (time < morning) {             // if normal time (before 1:30)

               cost = "125.00";

               if (p9 == 1) {             // if 9 hole round

                  cost = "62.50";
               }

            } else {         // Mid Day (1:30 - 3:20)

               cost = "100.00";

               if (p9 == 1) {             // if 9 hole round

                  cost = "50.00";
               }
            }
         }

      } else {    // Low Season

         if (time > twilight) {             // if twilight

            cost = "35.00";

            if (p9 == 1) {             // if 9 hole round

               cost = "17.50";
            }

         } else {        // NOT twilight

            if (time < morning) {             // if normal time (before 1:30)

               cost = "85.00";

               if (p9 == 1) {             // if 9 hole round

                  cost = "42.50";
               }

            } else {         // Mid Day (1:30 - 3:20)

               cost = "60.00";

               if (p9 == 1) {             // if 9 hole round

                  cost = "30.00";
               }
            }
         }
      }
   }     // end of Hotel Guest

   //
   //  Escorted Guest
   //
   if (gtype.equals( "Escorted Guest" )) {            // if guest type = Escorted Guest

      if (sdate > 614 && sdate < 916) {       // if High Season

         if (time > twilight) {             // if twilight

            cost = "50.00";

            if (p9 == 1) {             // if 9 hole round

               cost = "25.00";
            }

         } else {        // NOT twilight

            if (time < morning) {             // if normal time (before 1:30)

               cost = "100.00";

               if (p9 == 1) {             // if 9 hole round

                  cost = "50.00";
               }

            } else {         // Mid Day (1:30 - 3:20)

               cost = "75.00";

               if (p9 == 1) {             // if 9 hole round

                  cost = "37.50";
               }
            }
         }

      } else {    // Low Season

         if (time > twilight) {             // if twilight

            cost = "25.00";

            if (p9 == 1) {             // if 9 hole round

               cost = "12.50";
            }

         } else {        // NOT twilight

            if (time < morning) {             // if normal time (before 1:30)

               cost = "75.00";

               if (p9 == 1) {             // if 9 hole round

                  cost = "37.50";
               }

            } else {         // Mid Day (1:30 - 3:20)

               cost = "50.00";

               if (p9 == 1) {             // if 9 hole round

                  cost = "25.00";
               }
            }
         }
      }
   }     // end of Escorted Guest

   /*
   //
   //  Unescorted Guest - see above
   //
   if (gtype.equals( "Unescorted Guest" )) {            // if guest type = Unescorted Guest

      if (sdate > 614 && sdate < 916) {       // if High Season

         if (time > twilight) {             // if twilight

            cost = "50.00";

            if (p9 == 1) {             // if 9 hole round

               cost = "25.00";
            }

         } else {        // NOT twilight

            if (time < morning) {             // if normal time (before 1:30)

               cost = "125.00";          // no 9 hole fee

            } else {         // Mid Day (1:30 - 3:20)

               cost = "100.00";

               if (p9 == 1) {             // if 9 hole round

                  cost = "50.00";
               }
            }
         }

      } else {    // Low Season

         if (time > twilight) {             // if twilight

            cost = "35.00";

            if (p9 == 1) {             // if 9 hole round

               cost = "17.50";
            }

         } else {        // NOT twilight

            if (time < morning) {             // if normal time (before 1:30)

               cost = "85.00";        // no 9 hole fee

            } else {         // Mid Day (1:30 - 3:20)

               cost = "60.00";

               if (p9 == 1) {             // if 9 hole round

                  cost = "30.00";
               }
            }
         }
      }
   }     // end of Unescorted Guest
    */
   

   //
   //  Public Guest
   //
   if (gtype.equals( "Public" )) {            // if guest type = Public Guest

      if (sdate > 614 && sdate < 916) {       // if High Season

         if (time > twilight) {             // if twilight

            cost = "75.00";

            if (p9 == 1) {             // if 9 hole round

               cost = "37.50";
            }

         } else {        // NOT twilight

            if (time < morning) {             // if normal time (before 1:30)

               cost = "150.00";          // no 9 hole fee

            } else {         // Mid Day (1:30 - 3:20)

               cost = "125.00";

               if (p9 == 1) {             // if 9 hole round

                  cost = "62.50";
               }
            }
         }

      } else {    // Low Season

         if (time > twilight) {             // if twilight

            cost = "50.00";

            if (p9 == 1) {             // if 9 hole round

               cost = "25.00";
            }

         } else {        // NOT twilight

            if (time < morning) {             // if normal time (before 1:30)

               cost = "100.00";        // no 9 hole fee

            } else {         // Mid Day (1:30 - 3:20)

               cost = "75.00";

               if (p9 == 1) {             // if 9 hole round

                  cost = "37.50";
               }
            }
         }
      }
   }     // end of Public Guest

   //
   //  Property Owner Guest
   //
   if (gtype.equals( "Property Owner" )) {            // if guest type = Property Owner Guest

      if (sdate > 614 && sdate < 916) {       // if High Season

         if (time > twilight) {             // if twilight

            cost = "75.00";

            if (p9 == 1) {             // if 9 hole round

               cost = "37.50";
            }

         } else {        // NOT twilight

            if (time < morning) {             // if normal time (before 1:30)

               cost = "150.00";          // no 9 hole fee

            } else {         // Mid Day (1:30 - 3:20)

               cost = "100.00";

               if (p9 == 1) {             // if 9 hole round

                  cost = "50.00";
               }
            }
         }

      } else {    // Low Season

         if (time > twilight) {             // if twilight

            cost = "35.00";

            if (p9 == 1) {             // if 9 hole round

               cost = "17.50";
            }

         } else {        // NOT twilight

            if (time < morning) {             // if normal time (before 1:30)

               cost = "100.00";        // no 9 hole fee

            } else {         // Mid Day (1:30 - 3:20)

               cost = "60.00";

               if (p9 == 1) {             // if 9 hole round

                  cost = "30.00";
               }
            }
         }
      }
   }     // end of Property Owner Guest


   if (!cost.equals( "" )) {     // if cost identified

      cost = "$" +cost;          // prefix with a dollar sign
   }
     
   return(cost);
 }



 public static boolean checkMediterraSports(parmSlot slotParms, Connection con) {
     

    boolean error = false;

    //
    //  break down date of tee time
    //
    int yy = (int)slotParms.date / 10000;       // get year
    int sdate = (yy * 10000) + 1001;            // yyyy1001
    int edate = ((yy + 1) * 10000) + 430;       // yyyy0430

    //
    //  Only check quota if tee time is within the Golf Year
    //
    if (slotParms.date > sdate && slotParms.date < edate) {   
       
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        int i = 0;
        int count = 0;                               // number of guests for date range
        int max = 4;                                 // max of rounds

        String [] userA = new String [5];            // array to hold the usernames
        String [] playerA = new String [5];          // array to hold the player's names
        String [] mnumA = new String [5];            // array to hold the players' member numbers
        String [] mshipA = new String [5];           // array to hold the players' membership types

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

        mnumA[0] = slotParms.mNum1;
        mnumA[1] = slotParms.mNum2;
        mnumA[2] = slotParms.mNum3;
        mnumA[3] = slotParms.mNum4;
        mnumA[4] = slotParms.mNum5;

        mshipA[0] = slotParms.mship1;
        mshipA[1] = slotParms.mship2;
        mshipA[2] = slotParms.mship3;
        mshipA[3] = slotParms.mship4;
        mshipA[4] = slotParms.mship5;

        try {

            //
            //  Check each player
            //
            loop1:
            for (i = 0; i < 5; i++) {

                if (mshipA[i].equals("Sports")) {       // if it's a sports member

                    slotParms.player = playerA[i];      // save the player name we are currently checking

                    //
                    //   Check teepast2
                    //
                    pstmt = con.prepareStatement (
                       "SELECT mNum1, mNum2, mNum3, mNum4, mNum5, show1, show2, show3, show4, show5 " +
                       "FROM teepast2 " +
                       "WHERE " +
                            "date <= ? AND date >= ? AND (" +
                            "(mNum1 = ? AND show1 = 1) OR " +
                            "(mNum2 = ? AND show2 = 1) OR " +
                            "(mNum3 = ? AND show3 = 1) OR " +
                            "(mNum4 = ? AND show4 = 1) OR " +
                            "(mNum5 = ? AND show5 = 1))");

                    pstmt.setInt(1, sdate);
                    pstmt.setInt(2, edate);
                    pstmt.setString(3, mnumA[i]);
                    pstmt.setString(4, mnumA[i]);
                    pstmt.setString(5, mnumA[i]);
                    pstmt.setString(6, mnumA[i]);
                    pstmt.setString(7, mnumA[i]);
                    rs = pstmt.executeQuery();      // execute the prepared stmt

                    while (rs.next()) {

                        if (rs.getString("mNum1").equals(mnumA[i]) && rs.getInt("show1") == 1) count++;
                        if (rs.getString("mNum2").equals(mnumA[i]) && rs.getInt("show2") == 1) count++;
                        if (rs.getString("mNum3").equals(mnumA[i]) && rs.getInt("show3") == 1) count++;
                        if (rs.getString("mNum4").equals(mnumA[i]) && rs.getInt("show4") == 1) count++;
                        if (rs.getString("mNum5").equals(mnumA[i]) && rs.getInt("show5") == 1) count++;

                        if (count > max) {                         // if either count puts user over the limit

                            error = true;                          // indicate error
                            //slotParms.player = mnumA[i];           // save member number for error message
                            break loop1;
                        }

                    } // end while of all matching mNum's

                    rs.close();
                    pstmt.close();
                

                    //
                    //   Check teecurr2
                    //
                    pstmt = con.prepareStatement (
                       "SELECT mNum1, mNum2, mNum3, mNum4, mNum5, show1, show2, show3, show4, show5 " +
                       "FROM teecurr2 " +
                       "WHERE " +
                            "date <= ? AND date >= ? AND (" +
                            "(mNum1 = ? AND show1 = 1) OR " +
                            "(mNum2 = ? AND show2 = 1) OR " +
                            "(mNum3 = ? AND show3 = 1) OR " +
                            "(mNum4 = ? AND show4 = 1) OR " +
                            "(mNum5 = ? AND show5 = 1))");

                    pstmt.setInt(1, sdate);
                    pstmt.setInt(2, edate);
                    pstmt.setString(3, mnumA[i]);
                    pstmt.setString(4, mnumA[i]);
                    pstmt.setString(5, mnumA[i]);
                    pstmt.setString(6, mnumA[i]);
                    pstmt.setString(7, mnumA[i]);
                    rs = pstmt.executeQuery();      // execute the prepared stmt

                    while (rs.next()) {

                        if (rs.getString("mNum1").equals(mnumA[i]) && rs.getInt("show1") == 1) count++;
                        if (rs.getString("mNum2").equals(mnumA[i]) && rs.getInt("show2") == 1) count++;
                        if (rs.getString("mNum3").equals(mnumA[i]) && rs.getInt("show3") == 1) count++;
                        if (rs.getString("mNum4").equals(mnumA[i]) && rs.getInt("show4") == 1) count++;
                        if (rs.getString("mNum5").equals(mnumA[i]) && rs.getInt("show5") == 1) count++;

                        if (count > max) {                         // if either count puts user over the limit

                            error = true;                          // indicate error
                            //slotParms.player = mnumA[i];           // save member number for error message
                            break loop1;
                        }

                    } // end while of all matching mNum's

                    rs.close();
                    pstmt.close();

                    // if they are about to book their final allowed tee time then send email to pro
                    if (count == 3) {

                        sendEmail.sendMediterraEmail(slotParms.player, mnumA[i]);
                    }

                }   // end if sports mship

            }  // end of FOR loop (do each player)

            //slotParms.rnds = count;     // return the count of rounds to determin if we need to send email to pro

        } catch (Exception e) {

            Utilities.logError("Error checking for Mediterra Sports - verifyCustom.checkMediterraSports: " + e.getMessage());
        
        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }
        
    } // end if date in season

    if (!error) slotParms.player = "";
    
    return(error);
   
 }  // end checkMediterraSports
 
 
 
 //**************************************************************************************
 //
 //   checkGulfHarbourSports - Sports members can only play 2 rounds per family per month 
 //                            during the season (Nov 1 - Apr 30).
 //
 //**************************************************************************************
 public static boolean checkGulfHarbourSports(parmSlot slotParms, Connection con) {
     
     
    boolean error = false;

    //
    //  break down date of tee time
    //
    int yy = (int)slotParms.date / 10000;                       // get year of tee time
    int mm = (int)(slotParms.date - (yy * 10000)) / 100;        // get month
    int sdate = 0;
    int edate = 0;
    
    if (mm == 11 || mm == 12) {
       
       sdate = (yy * 10000) + 1031;            // yyyy1031
       edate = ((yy + 1) * 10000) + 431;       // yyyy0431
       
    } else {
       
       sdate = ((yy - 1) * 10000) + 1031;      // yyyy1031
       edate = (yy * 10000) + 431;             // yyyy0431
    }


    int max = 2;                                 // max of rounds allowed per month
    
    //
    //  Only check quota if tee time is within the Season (Nov 1 - Apr 30)
    //
    if (mm == 11 || mm == 12 || mm == 1 || mm == 2 || mm == 3 || mm == 4) {   
       
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        int i = 0;
        int count = 0;                               // number of guests for date range

        String mNum = "";

        String [] userA = new String [5];            // array to hold the usernames
        String [] playerA = new String [5];          // array to hold the player's names
        String [] mnumA = new String [5];            // array to hold the players' member numbers
        String [] mshipA = new String [5];           // array to hold the players' membership types

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

        mnumA[0] = slotParms.mNum1;
        mnumA[1] = slotParms.mNum2;
        mnumA[2] = slotParms.mNum3;
        mnumA[3] = slotParms.mNum4;
        mnumA[4] = slotParms.mNum5;

        mshipA[0] = slotParms.mship1;
        mshipA[1] = slotParms.mship2;
        mshipA[2] = slotParms.mship3;
        mshipA[3] = slotParms.mship4;
        mshipA[4] = slotParms.mship5;

        try {

            //
            //  Check each player
            //
            loop1:
            for (i = 0; i < 5; i++) {
               
                count = 0;                                       // init counter for each player

                if (mshipA[i].equalsIgnoreCase("Sports")) {       // if it's a sports member

                    slotParms.player = playerA[i];                // save the player name we are currently checking

                    //
                    //   Check teepast2
                    //
                    pstmt = con.prepareStatement (
                       "SELECT mNum1, mNum2, mNum3, mNum4, mNum5, show1, show2, show3, show4, show5 " +
                       "FROM teepast2 " +
                       "WHERE " +
                            "date > ? AND date < ? AND mm = ? AND (" +
                            "(mNum1 = ? AND show1 = 1) OR " +
                            "(mNum2 = ? AND show2 = 1) OR " +
                            "(mNum3 = ? AND show3 = 1) OR " +
                            "(mNum4 = ? AND show4 = 1) OR " +
                            "(mNum5 = ? AND show5 = 1))");

                    pstmt.setInt(1, sdate);
                    pstmt.setInt(2, edate);
                    pstmt.setInt(3, mm);
                    pstmt.setString(4, mnumA[i]);
                    pstmt.setString(5, mnumA[i]);
                    pstmt.setString(6, mnumA[i]);
                    pstmt.setString(7, mnumA[i]);
                    pstmt.setString(8, mnumA[i]);
                    rs = pstmt.executeQuery();    

                    while (rs.next()) {

                        if (rs.getString("mNum1").equals(mnumA[i]) && rs.getInt("show1") == 1) count++;
                        if (rs.getString("mNum2").equals(mnumA[i]) && rs.getInt("show2") == 1) count++;
                        if (rs.getString("mNum3").equals(mnumA[i]) && rs.getInt("show3") == 1) count++;
                        if (rs.getString("mNum4").equals(mnumA[i]) && rs.getInt("show4") == 1) count++;
                        if (rs.getString("mNum5").equals(mnumA[i]) && rs.getInt("show5") == 1) count++;

                        if (count >= max) {                        // if either count puts user at or over the limit

                            error = true;                          // indicate error
                            break loop1;
                        }

                    } // end while of all matching mNum's

                    rs.close();
                    pstmt.close();
                

                    //
                    //   Check teecurr2
                    //
                    pstmt = con.prepareStatement (
                       "SELECT mNum1, mNum2, mNum3, mNum4, mNum5 " +
                       "FROM teecurr2 " +
                       "WHERE " +
                            "date > ? AND date < ? AND mm = ? AND " +
                            "(mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?) AND " +
                            "(date != ? AND time != ?)");

                    pstmt.setInt(1, sdate);
                    pstmt.setInt(2, edate);
                    pstmt.setInt(3, mm);
                    pstmt.setString(4, mnumA[i]);
                    pstmt.setString(5, mnumA[i]);
                    pstmt.setString(6, mnumA[i]);
                    pstmt.setString(7, mnumA[i]);
                    pstmt.setString(8, mnumA[i]);
                    pstmt.setLong(9, slotParms.date);             // NOT this tee time
                    pstmt.setInt(10, slotParms.time);
                    rs = pstmt.executeQuery();     

                    while (rs.next()) {

                        if (rs.getString("mNum1").equals(mnumA[i])) count++;
                        if (rs.getString("mNum2").equals(mnumA[i])) count++;
                        if (rs.getString("mNum3").equals(mnumA[i])) count++;
                        if (rs.getString("mNum4").equals(mnumA[i])) count++;
                        if (rs.getString("mNum5").equals(mnumA[i])) count++;
                        
                        //
                        //  Now add the number of family members in this tee time request
                        //
                        mNum = mnumA[i];                     // get the mNum we are working on now
                        
                        for (int i2 = 0; i2 < 5; i2++) {
               
                           if (mNum.equals( mnumA[i2])) {
                              
                              count++;                       // include each family member in this tee time
                           }
                        }

                        if (count > max) {                         // if either count puts user at or over the limit

                            error = true;                          // indicate error
                            break loop1;
                        }

                    } // end while of all matching mNum's

                    rs.close();
                    pstmt.close();
                    
                }   // end if sports mship

            }  // end of FOR loop (do each player)

        } catch (Exception e) {

            Utilities.logError("Error checking for Gulf Harbour Sports - verifyCustom.checkGulfHarbourSports: " + e.getMessage());
            
        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }
        
    } // end if date in season

    if (!error) slotParms.player = "";
    
    return(error);
   
 }  // end checkGulfHarbourSports
 
 
 
 
 //**************************************************************************************
 //
 //   checkFHBlueCarts - Forest Highlands
 //                      Check if any members have reached or exceeded their allowed number 
 //                      of Blue Carts used without a doctor's note.
 //
 //**************************************************************************************
 public static int checkFHBlueCarts(parmSlot slotParms, Connection con) {
     
    int error = 0;

    int yy = (int)slotParms.date / 10000;        // get year of tee time

    int max = 3;                                 // max # of carts allowed per year
    
    String blueCart = "TMP";                     // acronym for Blue Flag Cart
    
    
    //
    //  Only check if 1 or more members with Blue Flag Carts (TMP) included in request
    //
    if ((!slotParms.user1.equals("") && slotParms.p1cw.equalsIgnoreCase(blueCart)) || 
        (!slotParms.user2.equals("") && slotParms.p2cw.equalsIgnoreCase(blueCart)) || 
        (!slotParms.user3.equals("") && slotParms.p3cw.equalsIgnoreCase(blueCart)) || 
        (!slotParms.user4.equals("") && slotParms.p4cw.equalsIgnoreCase(blueCart)) || 
        (!slotParms.user5.equals("") && slotParms.p5cw.equalsIgnoreCase(blueCart))) { 
            
            
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        int i = 0;
        int count = 0;                               // number of blue flag carts

        String [] userA = new String [5];            // array to hold the usernames
        String [] playerA = new String [5];          // array to hold the player's names
        String [] pcwA = new String [5];             // array to hold the modes of trans

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

        pcwA[0] = slotParms.p1cw;
        pcwA[1] = slotParms.p2cw;
        pcwA[2] = slotParms.p3cw;
        pcwA[3] = slotParms.p4cw;
        pcwA[4] = slotParms.p5cw;

        try {

            //
            //  Check each player
            //
            loop1:
            for (i = 0; i < 5; i++) {
               
                count = 0;                                       // init counter for each player

                if (!userA[i].equals("") && pcwA[i].equalsIgnoreCase(blueCart)) {    // if member with Blue Flag Cart
                   
                    count = 1;                                 // count this one

                    slotParms.player = playerA[i];            // save the player name we are currently checking

                    //
                    //   Check teepast2
                    //
                    pstmt = con.prepareStatement (
                       "SELECT mm " +
                       "FROM teepast2 " +
                       "WHERE " +
                            "yy = ? AND (" +
                            "(username1 = ? AND p1cw = ?) OR " +
                            "(username2 = ? AND p2cw = ?) OR " +
                            "(username3 = ? AND p3cw = ?) OR " +
                            "(username4 = ? AND p4cw = ?) OR " +
                            "(username5 = ? AND p5cw = ?))");

                    pstmt.setInt(1, yy);
                    pstmt.setString(2, userA[i]);
                    pstmt.setString(3, blueCart);
                    pstmt.setString(4, userA[i]);
                    pstmt.setString(5, blueCart);
                    pstmt.setString(6, userA[i]);
                    pstmt.setString(7, blueCart);
                    pstmt.setString(8, userA[i]);
                    pstmt.setString(9, blueCart);
                    pstmt.setString(10, userA[i]);
                    pstmt.setString(11, blueCart);
                    rs = pstmt.executeQuery();    

                    while (rs.next()) {

                        count++;
                    }

                    rs.close();
                    pstmt.close();
                

                    //
                    //   Check teecurr2
                    //
                    pstmt = con.prepareStatement (
                       "SELECT mm " +
                       "FROM teecurr2 " +
                       "WHERE " +
                            "yy = ? AND (" +
                            "(username1 = ? AND p1cw = ?) OR " +
                            "(username2 = ? AND p2cw = ?) OR " +
                            "(username3 = ? AND p3cw = ?) OR " +
                            "(username4 = ? AND p4cw = ?) OR " +
                            "(username5 = ? AND p5cw = ?)) AND " +
                            "(date != ? AND time != ?)");

                    pstmt.setInt(1, yy);
                    pstmt.setString(2, userA[i]);
                    pstmt.setString(3, blueCart);
                    pstmt.setString(4, userA[i]);
                    pstmt.setString(5, blueCart);
                    pstmt.setString(6, userA[i]);
                    pstmt.setString(7, blueCart);
                    pstmt.setString(8, userA[i]);
                    pstmt.setString(9, blueCart);
                    pstmt.setString(10, userA[i]);
                    pstmt.setString(11, blueCart);
                    pstmt.setLong(12, slotParms.date);             // NOT this tee time
                    pstmt.setInt(13, slotParms.time);
                    rs = pstmt.executeQuery();     

                    while (rs.next()) {

                       count++;
                    } 

                    rs.close();
                    pstmt.close();
                    
                    
                    //
                    //  Check if this member has reached the max allowed
                    //
                    if (count == max) {
                       
                       error = count;        // reached max
                       break loop1;          // done looking
                       
                    } else if (count > max) {
                       
                       error = count;        // exceeded max (return the count for message)
                       break loop1;          // done looking                       
                    }
                    
                }   // end if member with Blue Flag Cart

            }  // end of FOR loop (do each player)

        } catch (Exception e) {

            Utilities.logError("Error checking for Gulf Harbour Sports - verifyCustom.checkGulfHarbourSports: " + e.getMessage());
            
        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }
    }   

    if (error == 0) slotParms.player = "";
    
    return(error);
   
 }  // end checkFHBlueCarts
 
 
 
 
/**
 //************************************************************************
 //
 //  Interlachen - special Guest processing.
 //
 //     At this point we know there is more than one guest
 //     in this tee time, it is The CC, it is in season, and on a restricted course.
 //
 //     Restrictions:
 //
 //       Members (per family) can have up to 6 guests per month
 //       and 18 per season, where the season is April 1 to Oct 31.
 //       Event rounds are NOT counted.
 //
 //************************************************************************
 **/

 public static boolean checkInterlachenGsts(parmSlot slotParms, Connection con) {


   boolean error = false;

   String gstName = "Guest-Centennial";      // guest type to check
   
   //
   //  First, check if any Guest-Centennial guest types are included in tee time
   //
   if (slotParms.player1.startsWith(gstName) || slotParms.player2.startsWith(gstName) || slotParms.player3.startsWith(gstName) ||
       slotParms.player4.startsWith(gstName) || slotParms.player5.startsWith(gstName) ) {
   
       
      PreparedStatement pstmt = null;
      ResultSet rs = null; 

      String msubtype = "";
      String fullname = "";

      String [] playerA = new String [5];       // array to hold the player's names
      String [] usergA = new String [5];        // array to hold the members' usernames

      playerA[0] = slotParms.player1;
      playerA[1] = slotParms.player2;
      playerA[2] = slotParms.player3;
      playerA[3] = slotParms.player4;
      playerA[4] = slotParms.player5;

      usergA[0] = slotParms.userg1;             // copy userg values into array
      usergA[1] = slotParms.userg2;
      usergA[2] = slotParms.userg3;
      usergA[3] = slotParms.userg4;
      usergA[4] = slotParms.userg5;

      int i = 0;
   
      try {

         //
         //  Check each player
         //
         loop1:
         for (i = 0; i < 5; i++) {

            if (playerA[i].startsWith(gstName)) {       // if special guest type

               if (!usergA[i].equals( "" )) {          // if member associated with this guest

                 pstmt = con.prepareStatement (
                    "SELECT name_last, name_first, name_mi, msub_type " +
                    "FROM member2b " +
                    "WHERE username = ?");

                 pstmt.setString(1, usergA[i]);
                 rs = pstmt.executeQuery();    

                 if (rs.next()) {

                     StringBuffer mem_name = new StringBuffer(rs.getString("name_first"));  // get first name

                     fullname = rs.getString("name_mi");                                // middle initial
                     if (!fullname.equals( "" )) {
                        mem_name.append(" ");
                        mem_name.append(fullname);
                     }
                     mem_name.append(" " + rs.getString("name_last"));                     // last name

                     fullname = mem_name.toString();                          // convert to one string

                     msubtype = rs.getString("msub_type");                          // get sub type
                 }

                 rs.close();
                 pstmt.close();
        
                 if (!msubtype.equals("Member Guest Pass")) {        // if NOT allowed

                    error = true;
                    slotParms.player = fullname;            // save member's name for error msg
                    break loop1;
                 }
                  
               } else {         // no member associated with guest - invalid

                    error = true;
                    slotParms.player = "Unknown";            // save member's name for error msg
                    break loop1;                  
               }
            }
         }              // end of FOR loop (do each player)

      } catch (Exception e) {

         Utilities.logError("Error checking for Interlachen guests - verifyCustom.checkInterlachengsts " + e.getMessage());
         
      } finally {

          try { rs.close(); }
          catch (Exception ignore) {}

          try { pstmt.close(); }
          catch (Exception ignore) {}

      }
   }

   return(error);
 }                    // end of checkInterlachenGsts


 
/**
 //************************************************************************
 //
 //  Interlachen - special Guest processing.
 //
 //     At this point we know there is more than one guest and it is
 //     a weekday between 10:00 and 11:30.
 //
 //     Restrictions:
 //
 //       No more than 3 guest times (with more than 1 guest) allowed
 //       during this time range.
 //
 //************************************************************************
 **/

 public static boolean checkInterlachenFriGsts(parmSlot slotParms, Connection con) {


   boolean error = false;
       
   PreparedStatement pstmt = null;
   ResultSet rs = null; 

   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";

   int count = 0;
   int guests = 0;

   try {

        //
        //  Count # of guest times in this time range (10:00 to 11:30)
        //
        pstmt = con.prepareStatement (
           "SELECT userg1, userg2, userg3, userg4, userg5 " +
           "FROM teecurr2 " +
           "WHERE teecurr_id != ? AND date = ? AND time > 959 AND time < 1131 AND (userg1 != '' OR userg2 != '' OR userg3 != '' OR userg4 != '' OR userg5 != '')");

        pstmt.setInt(1, slotParms.teecurr_id);
        pstmt.setLong(2, slotParms.date);
        rs = pstmt.executeQuery();    

        while (rs.next()) {

            userg1 = rs.getString("userg1");              
            userg2 = rs.getString("userg2");              
            userg3 = rs.getString("userg3");              
            userg4 = rs.getString("userg4");              
            userg5 = rs.getString("userg5");         
            
            guests = 0;
            
            if (!userg1.equals("")) guests++;
            if (!userg2.equals("")) guests++;
            if (!userg3.equals("")) guests++;
            if (!userg4.equals("")) guests++;
            if (!userg5.equals("")) guests++;
            
            if (guests > 1) count++;          // count # of guest times
        }

        rs.close();
        pstmt.close();

        if (count > 2) {        // if NOT allowed

           error = true;
        }

   } catch (Exception e) {

      Utilities.logError("Error in verifyCustom.checkInterlachenFriGsts " + e.getMessage());

   } finally {

       try { rs.close(); }
       catch (Exception ignore) {}

       try { pstmt.close(); }
       catch (Exception ignore) {}

   }

   return(error);
 }                    // end of checkInterlachenFriGsts

 

 /**
  * Check tee time to see if any non-Marquee Golf members have more than one 'Guest' in a tee time before 10:30 on Sat or Sun.
  *
  * @param slotParms - Parameter block containing tee time information
  * @param con - Connection to club database
  * @return error - true if player is not a Marquee Golf mship, has more than one guest, and it's before 10:30am Sat/Sun
  */
 public static boolean checkKinsaleGuests(parmSlot slotParms, Connection con) {
     
     boolean error = false;

     int gcount = 0;
     
     if ((slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday")) && slotParms.time < 1030 && slotParms.guests > 1 &&
             ((!slotParms.mship1.equals("") && !slotParms.mship1.equals("Marquee Golf")) || 
              (!slotParms.mship2.equals("") && !slotParms.mship2.equals("Marquee Golf")))) {           // If Sat/Sun, before 10:30am, more than one guest, and at least one player is NOT a Marquee Golf player

         if (!slotParms.mship1.equals("") && !slotParms.mship1.equals("Marquee Golf")) {

             gcount = 0;
             
             if (slotParms.player2.startsWith("Guest") && !slotParms.userg2.equals(slotParms.user1)) gcount++;
             if (slotParms.player3.startsWith("Guest") && !slotParms.userg3.equals(slotParms.user1)) gcount++;
             if (slotParms.player4.startsWith("Guest") && !slotParms.userg4.equals(slotParms.user1)) gcount++;

             if (gcount > 1) error = true;
         }

         if (!slotParms.mship2.equals("") && !slotParms.mship2.equals("Marquee Golf")) {

             gcount = 0;

             if (slotParms.player3.startsWith("Guest") && !slotParms.userg3.equals(slotParms.user2)) gcount++;
             if (slotParms.player4.startsWith("Guest") && !slotParms.userg4.equals(slotParms.user2)) gcount++;

             if (gcount > 1) error = true;
         }

         // Don't need to check for player 3 and 4, since they can't have more than one guest following them anyway!
     }
     
     return error;
 }

 /**
  * Check to see if any player in a weekend tee time has already played a weekend time this month, or has one booked.
  * Only one allowed per month per player!
  *
  * @param slotParms - Parameter block containing all info on this tee time
  * @param con - Connection to club database
  * @return error - true if at least one Associate or Sports member in this tee time has a past or future weekend time already this month
  */
 public static boolean checkBonnieBriarMships(parmSlot slotParms, Connection con) {


     boolean error = false;

     if (slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday")) {

         PreparedStatement pstmt = null;
         ResultSet rs = null;

         // Build arrays to contain player names, mships, and usernames for use below
         String [] playerA = new String[4];
         String [] mshipA = new String[4];
         String [] userA = new String[4];

         playerA[0] = slotParms.player1;
         playerA[1] = slotParms.player2;
         playerA[2] = slotParms.player3;
         playerA[3] = slotParms.player4;

         mshipA[0] = slotParms.mship1;
         mshipA[1] = slotParms.mship2;
         mshipA[2] = slotParms.mship3;
         mshipA[3] = slotParms.mship4;

         userA[0] = slotParms.user1;
         userA[1] = slotParms.user2;
         userA[2] = slotParms.user3;
         userA[3] = slotParms.user4;

         try {

             String stmtCur = "SELECT teecurr_id FROM teecurr2 " +
                     "WHERE yy = ? AND mm = ? AND teecurr_id != ? AND (day = 'Saturday' OR day = 'Sunday') AND " +
                     "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ?)";     // All players can use the same statement and plug in their own username

             String stmtPast = "SELECT teepast_id FROM teepast2 " +
                     "WHERE yy = ? AND mm = ? AND (day = 'Saturday' OR day = 'Sunday') AND " +
                     "(((mship1 = 'Associate' OR mship1 = 'Sports') AND username1 = ? AND show1 = 1) OR ((mship2 = 'Associate' OR mship2 = 'Sports') AND username2 = ? AND show2 = 1) OR " +
                     " ((mship3 = 'Associate' OR mship2 = 'Sports') AND username3 = ? AND show3 = 1) OR ((mship4 = 'Associate' OR mship4 = 'Sports') AND username4 = ? AND show4 = 1))";  // Check teepast for this month as well

             long year = slotParms.date / 10000;                       // break down the tee time date
             long month = (slotParms.date - (year * 10000)) / 100;

             for (int i=0; i<4; i++) {          // Cycle through all 4 players

                 if (!error && mshipA[i].equals("Associate") || mshipA[i].equals("Sports")) {

                     // Check teecurr for any booked tee times
                     pstmt = con.prepareStatement(stmtCur);
                     pstmt.clearParameters();
                     pstmt.setInt(1, (int)year);
                     pstmt.setInt(2, (int)month);
                     pstmt.setInt(3, slotParms.teecurr_id);
                     pstmt.setString(4, userA[i]);
                     pstmt.setString(5, userA[i]);
                     pstmt.setString(6, userA[i]);
                     pstmt.setString(7, userA[i]);

                     rs = pstmt.executeQuery();

                     if (rs.next()) {
                         error = true;
                         slotParms.player = playerA[i];
                     }

                     pstmt.close();

                     if (!error) {      // don't bother checking if error already logged

                         // Check teepast for any previous tee times (only checks times booked by that member with 'Associate' or 'Sports' mships!!)
                         pstmt = con.prepareStatement(stmtPast);
                         pstmt.clearParameters();
                         pstmt.setInt(1, (int)year);
                         pstmt.setInt(2, (int)month);
                         pstmt.setString(3, userA[i]);
                         pstmt.setString(4, userA[i]);
                         pstmt.setString(5, userA[i]);
                         pstmt.setString(6, userA[i]);

                         rs = pstmt.executeQuery();

                         if (rs.next()) {
                             error = true;
                             slotParms.player = playerA[i];
                         }

                         pstmt.close();
                     }
                 }
             }
             
         } catch (Exception ignore) {
             
             error = true;
             
         } finally {

             try { rs.close(); }
             catch (Exception ignore) {}

             try { pstmt.close(); }
             catch (Exception ignore) {}

         }
     }

     return error;
 }

 
 //
 //  check Fox Den requests for X's on weekends (only one per group)
 //
 public static boolean checkFoxDenX(parmSlot slotParms, Connection con) {

     boolean error = false;

     int count = 0;
     int max = 1;

     //
     //  Only check if it is a weekend
     //
     if (slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday")) {
     
        String [] playerA = new String[5];

        playerA[0] = slotParms.player1;
        playerA[1] = slotParms.player2;
        playerA[2] = slotParms.player3;
        playerA[3] = slotParms.player4;
        playerA[4] = slotParms.player5;


        // Count the number of X's in this request
        for (int i=0; i<5; i++) {
           
            if (playerA[i].equalsIgnoreCase( "x" )) {
               
                count++;
            }
        }
        
        if (count > max) {     // if too many
           
           error = true;
        }
     }

     return error;
 }

 
 public static boolean checkPMarshMNums(parmSlot slotParms, Connection con) {

     PreparedStatement pstmt = null;
     PreparedStatement pstmt2 = null;
     ResultSet rs = null;
     ResultSet rs2 = null;

     boolean error = false;

     int count = 0;

     String user = slotParms.user;
     String username = "";
     String mNum = "";

     String [] playerA = new String[4];
     String [] mNumA = new String[4];
     String [] userA = new String[4];

     playerA[0] = slotParms.player1;
     playerA[1] = slotParms.player2;
     playerA[2] = slotParms.player3;
     playerA[3] = slotParms.player4;

     mNumA[0] = slotParms.mNum1;
     mNumA[1] = slotParms.mNum2;
     mNumA[2] = slotParms.mNum3;
     mNumA[3] = slotParms.mNum4;

     userA[0] = slotParms.user1;
     userA[1] = slotParms.user2;
     userA[2] = slotParms.user3;
     userA[3] = slotParms.user4;

     // Find the current user's member number
     for (int i=0; i<4; i++) {
         if (user.equals(userA[i])) {
             mNum = mNumA[i];
             slotParms.player = playerA[i];
         }
     }

     try {

         count = 0;     // reset count

         // Find all usernames in ForeTees sharing the current user's member number (family)
         pstmt = con.prepareStatement("SELECT username FROM member2b WHERE memNum = ?");
         pstmt.clearParameters();
         pstmt.setString(1, mNum);

         rs = pstmt.executeQuery();

         while (rs.next()) {        // Loop through all family members and count rounds originated by them other than this round

             username = rs.getString("username");

             pstmt2 = con.prepareStatement("SELECT teecurr_id FROM teecurr2 WHERE date = ? AND time != ? AND orig_by = ?");
             pstmt2.clearParameters();
             pstmt2.setInt(1, (int)slotParms.date);
             pstmt2.setInt(2, slotParms.time);
             pstmt2.setString(3, username);

             rs2 = pstmt2.executeQuery();

             while (rs2.next()) {
                 count++;
             }

             pstmt2.close();
         }

         if (count > 1) {       // Need one slot open for this tee time
             error = true;
         }

         pstmt.close();

     } catch (Exception ignore) {
         
         error = true;
         
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

     return error;

 }

 
 /**
  * checkTamariskAdvTime - Check for preferred times during the season (times between 8:00am and 11:07am)
  *
  * @param time - Time of the current tee time
  * @return allow - true if before 8:00am or after 11:07am, false otherwise
  * 
  * called by:  Member_sheet
  * 
  */
 public static boolean checkTamariskAdvTime(int time, long date, int index, int curr_time) {
     
     boolean allow = true;

     long shortDate = date - ((date / 10000) * 10000);   // get mmdd
     
     if ((shortDate < 430 || shortDate > 1101) && (time > 759 && time < 1108)) {      // if in-season and preferred time
        
        if (index > 3 || (index == 3 && curr_time < 700)) {       // if more than 3 days in adv, or 3 days and before 7:00 AM PT (already adjusted)
        
            allow = false;      // do not allow
        }
     }
     
     return allow;
 }

 //**************************************************************************************
 //
 //   checkTPCmems - check assigned members for unaccompanied guest times 
 //                          
 //      For ALL TPCs - members must be one of several mship types.
 //
 //**************************************************************************************
 public static boolean checkTPCmems(parmSlot slotParms, Connection con) {
     
    boolean error = false;
   
    String fullname = "";

    String user1 = slotParms.userg1;       // users that have been assigned to the guests
    String user2 = slotParms.userg2;
    String user3 = slotParms.userg3;
    String user4 = slotParms.userg4;
    String user5 = slotParms.userg5;

    if (!user1.equals("")) {              // if assigned
       
       fullname = checkTPCmship(user1, con);      // check this member's mship type
       
       if (!fullname.equals("")) {                // if NOT allowed
          
          error = true;
          slotParms.player = fullname;            // save member's name for error msg
       }
    }
         
    if (!user2.equals("") && !user2.equals(user1) && error == false) {     // if assigned to different user
       
       fullname = checkTPCmship(user2, con);      // check this member's mship type
       
       if (!fullname.equals("")) {                // if NOT allowed
          
          error = true;
          slotParms.player = fullname;            // save member's name for error msg
       }
    }
         
    if (!user3.equals("") && !user3.equals(user1)  && !user3.equals(user2) && error == false) {     // if assigned to different user
       
       fullname = checkTPCmship(user3, con);      // check this member's mship type
       
       if (!fullname.equals("")) {                // if NOT allowed
          
          error = true;
          slotParms.player = fullname;            // save member's name for error msg
       }
    }
         
    if (!user4.equals("") && !user4.equals(user1)  && !user4.equals(user2) && 
        !user4.equals(user3) && error == false) {                              // if assigned to different user
       
       fullname = checkTPCmship(user4, con);      // check this member's mship type
       
       if (!fullname.equals("")) {                // if NOT allowed
          
          error = true;
          slotParms.player = fullname;            // save member's name for error msg
       }
    }
         
    if (!user5.equals("") && !user5.equals(user1)  && !user5.equals(user2) && 
        !user5.equals(user3) && !user5.equals(user4) && error == false) {     // if assigned to different user
       
       fullname = checkTPCmship(user5, con);      // check this member's mship type
       
       if (!fullname.equals("")) {                // if NOT allowed
          
          error = true;
          slotParms.player = fullname;            // save member's name for error msg
       }
    }     
        
    if (error == false) slotParms.player = "";
    
    return(error);
   
 }  // end checkTPCmems
 
 
 
 //**************************************************************************************
 //
 //   checkTPCmship - check mship type for unaccompanied guests
 //                          
 //      Called By:  checkTPCmems (above)
 //
 //**************************************************************************************
 public static String checkTPCmship(String user, Connection con) {
     
    
     PreparedStatement pstmt = null;
     ResultSet rs = null;

     String fullname = "";
     String mship = "";

     try {

        //
        //  Check user's mship type
        //
        pstmt = con.prepareStatement (
           "SELECT name_last, name_first, name_mi, m_ship " +
           "FROM member2b " +
           "WHERE username = ?");

        pstmt.setString(1, user);
        rs = pstmt.executeQuery();    

        if (rs.next()) {

            StringBuffer mem_name = new StringBuffer(rs.getString("name_first"));  // get first name

            fullname = rs.getString("name_mi");                                // middle initial
            if (!fullname.equals( "" )) {
               mem_name.append(" ");
               mem_name.append(fullname);
            }
            mem_name.append(" " + rs.getString("name_last"));                     // last name

            fullname = mem_name.toString();                          // convert to one string

            mship = rs.getString("m_ship");                          // get mship type
        }

        rs.close();
        pstmt.close();
        
        //
        //  Check the mship type of this user
        //
        if (mship.equalsIgnoreCase("AFFINITY PARTNER") || mship.equalsIgnoreCase("CORPORATE") ||  
            mship.equalsIgnoreCase("CORPORATE ASSOCIATE") || mship.equalsIgnoreCase("CORPORATE NON-REFUNDABLE") || 
            mship.equalsIgnoreCase("CORPORATE-MULTI") || mship.equalsIgnoreCase("CORPORATE SINGLE") || 
            mship.equalsIgnoreCase("CHARTER") || mship.equalsIgnoreCase("CHARTER RESIDENT") || 
            mship.equalsIgnoreCase("CHARTER PREMIER") || mship.equalsIgnoreCase("CHARTER FULL PREMIER") ||
            mship.equalsIgnoreCase("CHARTER FULL") || mship.equalsIgnoreCase("CHARTER MULTI-CORPORATE") || 
            mship.equalsIgnoreCase("COMPLIMENTARY MEMBERSHIP") || mship.equalsIgnoreCase("EXECUTIVE") || 
            mship.equalsIgnoreCase("EXECUTIVE BUSINESS") || mship.equalsIgnoreCase("HONORARY") || 
            mship.equalsIgnoreCase("INTERNATIONAL CORPORATE") || mship.equalsIgnoreCase("MASTER FULL") || 
            mship.equalsIgnoreCase("MASTER CORPORATE") || mship.equalsIgnoreCase("PATRON") || 
            mship.equalsIgnoreCase("PROFESSIONAL BUSINESS") || mship.equalsIgnoreCase("REGULAR") ||
            mship.equalsIgnoreCase("CHARTER CORPORATE") ) {
           
           fullname = "";          // this user is ok to book unaccompanied guest
        }
        
     } catch (Exception e) {

         Utilities.logError("Error checking for TPC mship type - verifyCustom.checkTPCmship: " + e.getMessage());
         
     } finally {

         try { rs.close(); }
         catch (Exception ignore) {}

         try { pstmt.close(); }
         catch (Exception ignore) {}

     }
        
     return(fullname);
   
 }  // end checkTPCmship
 
 
 
 
 //**************************************************************************************
 //
 //   checkNorthOaksJrs - Check for unaccompanied juniors. 
 //
 //**************************************************************************************
 public static boolean checkNorthOaksJrs(parmSlot slotParms, Connection con) {
     

  boolean error = false;
  boolean check = false;
  
  //
  //  Check for unaccompanied Juniors. Skip test if any adults included.
  //
  if (!slotParms.mtype1.startsWith("Adult") && !slotParms.mtype2.startsWith("Adult") && !slotParms.mtype3.startsWith("Adult") && 
      !slotParms.mtype4.startsWith("Adult") && !slotParms.mtype5.startsWith("Adult")) { 
        
     //
     //  No Adults - See if this tee time falls into an unaccompanied Junior time
     //
     //   Tues:   Noon - 4:00    
     //   Wed:    Covered by other restrictions
     //   Thurs:  Noon - 4:00
     //   Fri:    covered by other restrictions
     //   Sat:    10:00 - 3:00
     //   Sun:    before 3:00
     //
     if ((slotParms.day.equals("Tuesday") || slotParms.day.equals("Thursday")) && 
          slotParms.time > 1200 && slotParms.time < 1600) { 
  
        check = true;           // check this request for a junior
        
     } else if (slotParms.day.equals("Saturday") && slotParms.time > 1000 && slotParms.time < 1500) {
        
        check = true;           // check this request for a junior
        
     } else if (slotParms.day.equals("Sunday") && slotParms.time < 1500) {
        
        check = true;           // check this request for a junior
     }
  
     if (check == true) {

        //
        //  Check all slots as one or more may be a guest
        //
        if (slotParms.mtype1.startsWith("Jr") || slotParms.mtype2.startsWith("Jr") || slotParms.mtype3.startsWith("Jr") ||  
            slotParms.mtype4.startsWith("Jr") || slotParms.mtype5.startsWith("Jr")) {

           error = true;       // Junior with no adult - reject
        }
     }
  }        // end of IF adults

  return(error);
   
 }  // end checkNorthOaksJrs
      
 
 
 
 
 //**************************************************************************************
 //
 //   Rolling Hills CC - CO (check days in advance)
 //
 //***********************************//
 public static boolean checkRHCCdays(parmSlot slotParms) {
     

  boolean error = false;
  
  String player = "";
  
  //
  //  Get the current server time (CT)
  //
  Calendar cal2 = new GregorianCalendar();       
  int cal_hour = cal2.get(Calendar.HOUR_OF_DAY);
  int cal_min = cal2.get(Calendar.MINUTE);

  int cal_time = (cal_hour * 100) + cal_min;     // CT

  
  //
  //  Check for secondary members or dependents - primary members are ok (fall under normal days in advance settings).
  //
  if (!slotParms.mtype1.equals("") && !slotParms.mtype1.startsWith("Primary")) { 
     
     player = slotParms.player1;       // save name of Secondary member
     
  } else if (!slotParms.mtype2.equals("") && !slotParms.mtype2.startsWith("Primary")) { 
     
     player = slotParms.player2;       // save name of Secondary member
     
  } else if (!slotParms.mtype3.equals("") && !slotParms.mtype3.startsWith("Primary")) { 
     
     player = slotParms.player3;       // save name of Secondary member
     
  } else if (!slotParms.mtype4.equals("") && !slotParms.mtype4.startsWith("Primary")) { 
     
     player = slotParms.player4;       // save name of Secondary member
     
  } else if (!slotParms.mtype5.equals("") && !slotParms.mtype5.startsWith("Primary")) { 
     
     player = slotParms.player5;       // save name of Secondary member
  }     
   
  if (!player.equals("")) {  
        
     //
     //  At least one player is a Secondary or Dependent - check for a Primary Member with them
     //
     if (slotParms.mtype1.startsWith("Primary") || slotParms.mtype2.startsWith("Primary") || slotParms.mtype3.startsWith("Primary") || 
         slotParms.mtype4.startsWith("Primary") || slotParms.mtype5.startsWith("Primary")) { 
        
        //
        //  Primary included - 2 days in advance starting at 7:00 AM MT
        //
        if (slotParms.ind > 2 || (slotParms.ind == 2 && cal_time < 800)) { 
           
           error = true; 
           slotParms.player = player;
        }
        
     } else {
        
        //
        //  No Primary member in tee time - only 1 day in advance starting at 7:00 AM MT
        //
        if (slotParms.ind > 1 || (slotParms.ind == 1 && cal_time < 800)) { 
           
           error = true; 
           slotParms.player = player;
        }
     }
     
  }        // end of IF Secondary member or Dependent

  return(error);
   
 }  // end checkRHCCdays
      
 
 
 //**************************************************************************************
 //
 //   Rolling Hills CC - CO (check days in advance for Guests)
 //
 //***********************************//
 public static boolean checkRHCCguests(parmSlot slotParms) {
     

  boolean error = false;
  
  //
  //  Get the current server time (CT)
  //
  Calendar cal2 = new GregorianCalendar();       
  int cal_hour = cal2.get(Calendar.HOUR_OF_DAY);
  int cal_min = cal2.get(Calendar.MINUTE);

  int cal_time = (cal_hour * 100) + cal_min;     // CT

  
  //
  //  Check for any guests in this request
  //
  if (slotParms.guests > 0) {      
     
     //
     //  1 or more Guests - allowed to book 1 day in advance starting at 7:00 AM MT
     //
     if (slotParms.ind > 1 || (slotParms.ind == 1 && cal_time < 800)) { 

        error = true; 
     }
  }        // end of IF guests

  return(error);
   
 }  // end checkRHCCguests
      
 
 
 //**************************************************************************************
 //
 //   Rolling Hills CC - CO (check for primary member on Sunday)
 //
 //***********************************//
 public static boolean checkRHCCsunday(parmSlot slotParms) {
     

  boolean error = false;
  
  String player = "";
  
  
  if (slotParms.time > 759 && slotParms.time < 1001) {       //  if between 8:00 and 10:00 AM (we already know it is Sunday)
  
     //
     //  Check for secondary members or dependents - primary members are ok (fall under normal days in advance settings).
     //
     if (!slotParms.mtype1.equals("") && !slotParms.mtype1.startsWith("Primary")) { 

        player = slotParms.player1;       // save name of Secondary member

     } else if (!slotParms.mtype2.equals("") && !slotParms.mtype2.startsWith("Primary")) { 

        player = slotParms.player2;       // save name of Secondary member

     } else if (!slotParms.mtype3.equals("") && !slotParms.mtype3.startsWith("Primary")) { 

        player = slotParms.player3;       // save name of Secondary member

     } else if (!slotParms.mtype4.equals("") && !slotParms.mtype4.startsWith("Primary")) { 

        player = slotParms.player4;       // save name of Secondary member

     } else if (!slotParms.mtype5.equals("") && !slotParms.mtype5.startsWith("Primary")) { 

        player = slotParms.player5;       // save name of Secondary member
     }     

     if (!player.equals("")) {  

        //
        //  At least one player is a Secondary or Dependent - check for a Primary Member with them
        //
        if (!slotParms.mtype1.startsWith("Primary") && !slotParms.mtype2.startsWith("Primary") && !slotParms.mtype3.startsWith("Primary") && 
            !slotParms.mtype4.startsWith("Primary") && !slotParms.mtype5.startsWith("Primary")) { 

           error = true;                 // no primary member in request
           slotParms.player = player;
        }

     }        // end of IF Secondary member or Dependent
  }

  return(error);
   
 }  // end checkRHCCsunday
      
 
 
 
 
 
 public static boolean checkEagleCreekSocial(parmSlot slotParms, Connection con) {
     

    boolean error = false;

    //
    //  break down date of tee time
    //
    int yy = (int)slotParms.date / 10000;       // get year
    int sdate = (yy * 10000) + 1101;            // yyyy1101
    int edate = ((yy + 1) * 10000) + 430;       // yyyy0430

    //
    //  Only check quota if tee time is within the Golf Year
    //
    if (slotParms.date > sdate && slotParms.date < edate) {   
       
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        int i = 0;
        int count = 0;                               // number of guests for date range
        int max = 6;  

        String [] userA = new String [5];            // array to hold the usernames
        String [] playerA = new String [5];          // array to hold the player's names
        String [] mshipA = new String [5];          // array to hold the players' membership types

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

        mshipA[0] = slotParms.mship1;
        mshipA[1] = slotParms.mship2;
        mshipA[2] = slotParms.mship3;
        mshipA[3] = slotParms.mship4;
        mshipA[4] = slotParms.mship5;


        try {

            //
            //  Check each player
            //
            loop1:
            for (i = 0; i < 5; i++) {

                if (mshipA[i].equals("Social")) {          // if it's a social member

                    slotParms.player = playerA[i];       // save the player name we are currently checking

                    //
                    //   Check teepast2
                    //
                    pstmt = con.prepareStatement (
                       "SELECT COUNT(*) " +
                       "FROM teepast2 " +
                       "WHERE " +
                            "date <= ? AND date >= ? AND (" +
                            "(username1 = ? AND show1 = 1) OR " +
                            "(username2 = ? AND show2 = 1) OR " +
                            "(username3 = ? AND show3 = 1) OR " +
                            "(username4 = ? AND show4 = 1) OR " +
                            "(username5 = ? AND show5 = 1))");

                    pstmt.setInt(1, sdate);
                    pstmt.setInt(2, edate);
                    pstmt.setString(3, userA[i]);
                    pstmt.setString(4, userA[i]);
                    pstmt.setString(5, userA[i]);
                    pstmt.setString(6, userA[i]);
                    pstmt.setString(7, userA[i]);
                    rs = pstmt.executeQuery();      // execute the prepared stmt

                    if (rs.next()) count = count + rs.getInt(1);

                    if (count > max) {                        // if either count puts user over the limit

                        error = true;                          // indicate error
                        break loop1;
                    }

                    rs.close();
                    pstmt.close();
                

                    //
                    //   Check teecurr2
                    //
                    pstmt = con.prepareStatement (
                       "SELECT COUNT(*) " +
                       "FROM teecurr2 " +
                       "WHERE " +
                            "date <= ? AND date >= ? AND (" +
                            "(username1 = ? AND show1 = 1) OR " +
                            "(username2 = ? AND show2 = 1) OR " +
                            "(username3 = ? AND show3 = 1) OR " +
                            "(username4 = ? AND show4 = 1) OR " +
                            "(username5 = ? AND show5 = 1))");

                    pstmt.setInt(1, sdate);
                    pstmt.setInt(2, edate);
                    pstmt.setString(3, userA[i]);
                    pstmt.setString(4, userA[i]);
                    pstmt.setString(5, userA[i]);
                    pstmt.setString(6, userA[i]);
                    pstmt.setString(7, userA[i]);
                    rs = pstmt.executeQuery();      // execute the prepared stmt

                    if (rs.next()) count = count + rs.getInt(1);

                    if (count > max) {                         // if either count puts user over the limit

                        error = true;                          // indicate error
                        break loop1;
                    }

                    rs.close();
                    pstmt.close();

                }   // end if social mship

            }  // end of FOR loop (do each player)

        } catch (Exception e) {

            Utilities.logError("Error checking for Eagle Creek guests - verifyCustom.checkEagleCreekSocial: " + e.getMessage());
            
        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }
        
    } // end if date in season

    if (!error) slotParms.player = "";
    
    return(error);
   
 }  // end checkEagleCreekSocial
 
 
/**
 //************************************************************************
 //
 //  checkPVCCmships - checks certain membership types for max rounds per season.
 //
 //************************************************************************
 **/

 public static boolean checkPVCCmships(parmSlot slotParms, Connection con) {


   PreparedStatement pstmt2m = null;
   PreparedStatement pstmt3m = null;
   ResultSet rs = null;

   int count = 0;
   int i = 0;
   int max = 4;               // max of 4 rounds per season
   
   long sdate = 1020;         // season is defined as 10/20 thru 5/31
   long edate = 531;

   String mship = "";         // Membership type to check
   String user = "";
   
   String [] mshipA = new String [5];
   String [] userA = new String [5];
  
   mshipA[0] = slotParms.mship1;
   mshipA[1] = slotParms.mship2;
   mshipA[2] = slotParms.mship3;
   mshipA[3] = slotParms.mship4;
   mshipA[4] = slotParms.mship5;
   userA[0] = slotParms.user1;
   userA[1] = slotParms.user2;
   userA[2] = slotParms.user3;
   userA[3] = slotParms.user4;
   userA[4] = slotParms.user5;

   boolean error = false;
   
   
   long year = slotParms.date / 10000;                       // break down the tee time date
   long month = (slotParms.date - (year * 10000)) / 100;
   long day = slotParms.date - ((year * 10000) + (month * 100));
   long mmdd = (month * 100) + day;                        // create mmdd value

   
   //
   //  Create the start date and end date for queries.
   //  The season is from 10/20 to 5/31, so we must determine which years to use.
   //
   if (mmdd > edate && mmdd < sdate) {         // if out of season
      
      sdate = 0;
      edate = 0;       // out of season - skip checks
      
   } else {
      
      if (month > 9) {         // if fall of the year

         sdate = sdate + (year * 10000);          // start date is 10/20/yyyy (this year)
         edate = edate + ((year + 1) * 10000);    // end date is 5/31/yyyy (next year)

      } else {

         if (month < 6) {         // if start of the year

            sdate = sdate + ((year - 1) * 10000);    // start date is 10/20/yyyy (last year)
            edate = edate + (year * 10000);          // end date is 5/31/yyyy (this year)       
         }
      }
   }

   //
   //  Count the existing tee times if in season and at least one of the members is one of the following mship types:
   //
   //       Proprietary
   //       Spa
   //       Single Spa
   //       Tennis/Spa
   //       Tennis Only
   //       Homeowner
   //
   if (sdate > 0  &&
       (mshipA[0].equals("Proprietary") || mshipA[0].equals("Spa") || mshipA[0].equals("Single Spa") || mshipA[0].equals("Tennis/Spa") || mshipA[0].equals("Tennis Only") || mshipA[0].equals("Homeowner") ||
        mshipA[1].equals("Proprietary") || mshipA[1].equals("Spa") || mshipA[1].equals("Single Spa") || mshipA[1].equals("Tennis/Spa") || mshipA[1].equals("Tennis Only") || mshipA[1].equals("Homeowner") ||
        mshipA[2].equals("Proprietary") || mshipA[2].equals("Spa") || mshipA[2].equals("Single Spa") || mshipA[2].equals("Tennis/Spa") || mshipA[2].equals("Tennis Only") || mshipA[2].equals("Homeowner") ||
        mshipA[3].equals("Proprietary") || mshipA[3].equals("Spa") || mshipA[3].equals("Single Spa") || mshipA[3].equals("Tennis/Spa") || mshipA[3].equals("Tennis Only") || mshipA[3].equals("Homeowner") ||
        mshipA[4].equals("Proprietary") || mshipA[4].equals("Spa") || mshipA[4].equals("Single Spa") || mshipA[4].equals("Tennis/Spa") || mshipA[4].equals("Tennis Only") || mshipA[4].equals("Homeowner"))) {
       
      try {

         //
         // statements for queries
         //
         pstmt2m = con.prepareStatement (
            "SELECT COUNT(*) " +
            "FROM teecurr2 WHERE date >= ? AND date <= ? AND date != ? AND " +
                       "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)");

         pstmt3m = con.prepareStatement (
            "SELECT COUNT(*) " +
            "FROM teepast2 WHERE date >= ? AND date <= ? AND date != ? AND " +
                       "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)");


         //
         //  Check each player
         //
         i = 0;
         while (i < 5 && error == false) {
            
            if (mshipA[i].equals("Proprietary") || mshipA[i].equals("Spa") || mshipA[i].equals("Single Spa") || mshipA[i].equals("Tennis/Spa") || mshipA[i].equals("Tennis Only") || mshipA[i].equals("Homeowner")) {

               count = 0;
               
               //
               //  Count the rounds for this member (excluding the day of this tee time)
               //
               pstmt2m.clearParameters();                 
               pstmt2m.setLong(1, sdate);
               pstmt2m.setLong(2, edate);
               pstmt2m.setLong(3, slotParms.date);
               pstmt2m.setString(4, userA[i]);
               pstmt2m.setString(5, userA[i]);
               pstmt2m.setString(6, userA[i]);
               pstmt2m.setString(7, userA[i]);
               pstmt2m.setString(8, userA[i]);
               rs = pstmt2m.executeQuery();

               if (rs.next()) {

                  count = rs.getInt(1);                // get count of tee times from teecurr
               }

               pstmt3m.clearParameters();                    
               pstmt3m.setLong(1, sdate);
               pstmt3m.setLong(2, edate);
               pstmt3m.setLong(3, slotParms.date);
               pstmt3m.setString(4, userA[i]);
               pstmt3m.setString(5, userA[i]);
               pstmt3m.setString(6, userA[i]);
               pstmt3m.setString(7, userA[i]);
               pstmt3m.setString(8, userA[i]);
               rs = pstmt3m.executeQuery();

               if (rs.next()) {

                  count += rs.getInt(1);                // add number of tee times from teepast
               }


               if (count >= max)  {               // if limit already reached

                  error = true;                   // reject this member
                  slotParms.mship = mship;
                  
                  if (i == 0) {
                      slotParms.player = slotParms.player1;
                  } else if (i == 1) {
                      slotParms.player = slotParms.player2;
                  } else if (i == 2) {
                      slotParms.player = slotParms.player3;
                  } else if (i == 3) {
                      slotParms.player = slotParms.player4;
                  } else if (i == 4) {
                      slotParms.player = slotParms.player5;
                  }
               }
            }        // end of IF player is mship to check
            
            i++;     // do next player
                    
         }          // end of WHILE player

         pstmt2m.close();
         pstmt3m.close();

      } catch (SQLException e1) {

         Utilities.logError("SQL Error Checking Max Rounds - verifyCustom.checkPVCCmships " + e1.getMessage());        // log the error message
      
      } catch (Exception e) {

         Utilities.logError("Exception Checking Max Rounds - verifyCustom.checkPVCCmships " + e.getMessage());        // log the error message
      
      } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt2m.close(); }
            catch (Exception ignore) {}

            try { pstmt3m.close(); }
            catch (Exception ignore) {}

      }
   
   }       // end of if date in season

   return(error);
   
 }                  // end of checkPVCCmships


/**
 //************************************************************************
 //
 //  checkPVCCmships - checks certain membership types for max rounds per season.
 //
 //************************************************************************
 **/

 public static boolean checkMorrisCGCmships(parmSlot slotParms, Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   int i = 0;
   int count_weekday = 0;
   int count_weekend = 0;
   int max_weekday = 10;               // max of 10 weekday rounds per year
   int max_weekend = 5;               // max of 5 weekend rounds per year

   long sdate = 101;          // range to check is the current year
   long edate = 1231;

   String query_curr_weekday = "";
   String query_curr_weekend = "";
   String query_past_weekday = "";
   String query_past_weekend = "";

   String [] mshipA = new String [5];
   String [] userA = new String [5];

   mshipA[0] = slotParms.mship1;
   mshipA[1] = slotParms.mship2;
   mshipA[2] = slotParms.mship3;
   mshipA[3] = slotParms.mship4;
   mshipA[4] = slotParms.mship5;
   userA[0] = slotParms.user1;
   userA[1] = slotParms.user2;
   userA[2] = slotParms.user3;
   userA[3] = slotParms.user4;
   userA[4] = slotParms.user5;

   boolean error = false;

   long year = slotParms.date / 10000;                       // break down the tee time date            // create mmdd value


   //
   //  Create the start date and end date for queries.
   //
   sdate = sdate + (year * 10000);
   edate = edate + (year * 10000);

   //
   //  Count the existing weekday (Tues-Fri) and weekend (Sat-Mon) times for House mship
   //
   if (mshipA[0].equals("House") || mshipA[1].equals("House") || mshipA[2].equals("House") || mshipA[3].equals("House") || mshipA[4].equals("House")) {

      try {

         //
         // build queries
         //
         query_curr_weekday =
            "SELECT COUNT(*) " +
            "FROM teecurr2 WHERE date >= ? AND date <= ? AND (date != ? OR time != ?) AND " +
            "(day = 'Tuesday' OR day = 'Wednesday' OR day = 'Thursday' OR day = 'Friday') AND " +
            "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)";

         query_past_weekday =
            "SELECT COUNT(*) " +
            "FROM teepast2 WHERE date >= ? AND date <= ? AND (date != ? OR time != ?) AND " +
            "(day = 'Tuesday' OR day = 'Wednesday' OR day = 'Thursday' OR day = 'Friday') AND " +
            "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)";

         query_curr_weekend =
            "SELECT COUNT(*) " +
            "FROM teecurr2 WHERE date >= ? AND date <= ? AND (date != ? OR time != ?) AND " +
            "(day = 'Saturday' OR day = 'Sunday' OR day = 'Monday') AND " +
            "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)";

         query_past_weekend =
            "SELECT COUNT(*) " +
            "FROM teepast2 WHERE date >= ? AND date <= ? AND (date != ? OR time != ?) AND " +
            "(day = 'Saturday' OR day = 'Sunday' OR day = 'Monday') AND " +
            "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)";


         //  Only check weekday or weekend rounds, depending on the day their currently booking on.
         if (slotParms.day.equals("Tuesday") || slotParms.day.equals("Wednesday") || slotParms.day.equals("Thursday") || slotParms.day.equals("Friday")) {

             //
             //  Check each player
             //
             i = 0;

             while (i < 5 && error == false) {

                if (mshipA[i].equals("House")) {

                   count_weekday = 0;

                   //
                   //  Count the rounds for this member (excluding the day of this tee time)
                   //
                   //  Weekday rounds from teecurr
                   pstmt = con.prepareStatement(query_curr_weekday);
                   pstmt.clearParameters();
                   pstmt.setLong(1, sdate);
                   pstmt.setLong(2, edate);
                   pstmt.setLong(3, slotParms.date);
                   pstmt.setInt(4, slotParms.time);
                   pstmt.setString(5, userA[i]);
                   pstmt.setString(6, userA[i]);
                   pstmt.setString(7, userA[i]);
                   pstmt.setString(8, userA[i]);
                   pstmt.setString(9, userA[i]);
                   rs = pstmt.executeQuery();

                   if (rs.next()) {

                      count_weekday = rs.getInt(1);                // get count of tee times from teecurr
                   }

                   pstmt.close();

                   //  Weekday rounds from teepast
                   pstmt = con.prepareStatement(query_past_weekday);
                   pstmt.clearParameters();
                   pstmt.setLong(1, sdate);
                   pstmt.setLong(2, edate);
                   pstmt.setLong(3, slotParms.date);
                   pstmt.setInt(4, slotParms.time);
                   pstmt.setString(5, userA[i]);
                   pstmt.setString(6, userA[i]);
                   pstmt.setString(7, userA[i]);
                   pstmt.setString(8, userA[i]);
                   pstmt.setString(9, userA[i]);
                   rs = pstmt.executeQuery();

                   if (rs.next()) {

                      count_weekday += rs.getInt(1);                // add number of tee times from teepast
                   }

                   pstmt.close();

                   if (count_weekday >= max_weekday)  {               // if limit already reached

                      error = true;                   // reject this member
                      slotParms.mship = mshipA[i];

                      if (i == 0) {
                          slotParms.player = slotParms.player1;
                      } else if (i == 1) {
                          slotParms.player = slotParms.player2;
                      } else if (i == 2) {
                          slotParms.player = slotParms.player3;
                      } else if (i == 3) {
                          slotParms.player = slotParms.player4;
                      } else if (i == 4) {
                          slotParms.player = slotParms.player5;
                      }
                   }
                }        // end of IF player is mship to check

                i++;     // do next player

             }          // end of WHILE player
             
         } else if (slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday") || slotParms.day.equals("Monday")) {       // Weekend round

             //
             //  Check each player
             //
             i = 0;
             
             while (i < 5 && error == false) {

                if (mshipA[i].equals("House")) {

                   count_weekend = 0;

                   //
                   //  Count the rounds for this member (excluding the day of this tee time)
                   //
                   //  Weekend rounds from teecurr
                   pstmt = con.prepareStatement(query_curr_weekend);
                   pstmt.clearParameters();
                   pstmt.setLong(1, sdate);
                   pstmt.setLong(2, edate);
                   pstmt.setLong(3, slotParms.date);
                   pstmt.setInt(4, slotParms.time);
                   pstmt.setString(5, userA[i]);
                   pstmt.setString(6, userA[i]);
                   pstmt.setString(7, userA[i]);
                   pstmt.setString(8, userA[i]);
                   pstmt.setString(9, userA[i]);
                   rs = pstmt.executeQuery();

                   if (rs.next()) {

                      count_weekend = rs.getInt(1);                // get count of tee times from teecurr
                   }

                   pstmt.close();

                   //  Weekend rounds from teepast
                   pstmt = con.prepareStatement(query_past_weekend);
                   pstmt.clearParameters();
                   pstmt.setLong(1, sdate);
                   pstmt.setLong(2, edate);
                   pstmt.setLong(3, slotParms.date);
                   pstmt.setInt(4, slotParms.time);
                   pstmt.setString(5, userA[i]);
                   pstmt.setString(6, userA[i]);
                   pstmt.setString(7, userA[i]);
                   pstmt.setString(8, userA[i]);
                   pstmt.setString(9, userA[i]);
                   rs = pstmt.executeQuery();

                   if (rs.next()) {

                      count_weekend += rs.getInt(1);                // add number of tee times from teepast
                   }

                   pstmt.close();

                   if (count_weekend >= max_weekend)  {               // if limit already reached

                      error = true;                   // reject this member
                      slotParms.mship = mshipA[i];

                      if (i == 0) {
                          slotParms.player = slotParms.player1;
                      } else if (i == 1) {
                          slotParms.player = slotParms.player2;
                      } else if (i == 2) {
                          slotParms.player = slotParms.player3;
                      } else if (i == 3) {
                          slotParms.player = slotParms.player4;
                      } else if (i == 4) {
                          slotParms.player = slotParms.player5;
                      }
                   }
                }        // end of IF player is mship to check

                i++;     // do next player

             }          // end of WHILE player
         }

      } catch (SQLException e1) {

         Utilities.logError("SQL Error Checking Max Rounds - verifyCustom.checkMorrisCGCmships " + e1.getMessage());        // log the error message

      } catch (Exception e) {

         Utilities.logError("Exception Checking Max Rounds - verifyCustom.checkMorrisCGCmships " + e.getMessage());        // log the error message

      } finally {

            try { pstmt.close(); }
            catch (Exception ignore) {}
      }

   }       // end of if date in season

   return(error);

 }

/**
 //************************************************************************
 //
 //  Jonathans Landing
 //
 //  checkJLGCmships - checks certain membership types for max rounds per month during the season.
 //
 //************************************************************************
 **/

 public static boolean checkJLGCmships(parmSlot slotParms, Connection con) {


   ResultSet rs = null;
   PreparedStatement pstmt2m = null;
   PreparedStatement pstmt3m = null;

   boolean error = false;
   
   int count = 0;
   int i = 0;
   int i2 = 0;
   int max = 8;               // max rounds per season
   
   long sdate = 1101;         // season is defined as 11/01 thru 4/30
   long edate = 430;

   String mship = "";         // Membership type to check
   String user = "";
   String mNum1 = "";
   String mNum2 = "";
   String mNum3 = "";
   String mNum4 = "";
   String mNum5 = "";
   
   String [] mshipA = new String [5];
   String [] mnumA = new String [5];
  
   mshipA[0] = slotParms.mship1;
   mshipA[1] = slotParms.mship2;
   mshipA[2] = slotParms.mship3;
   mshipA[3] = slotParms.mship4;
   mshipA[4] = slotParms.mship5;
   mnumA[0] = slotParms.mNum1;
   mnumA[1] = slotParms.mNum2;
   mnumA[2] = slotParms.mNum3;
   mnumA[3] = slotParms.mNum4;
   mnumA[4] = slotParms.mNum5;


   //
   //  Remove any duplicate family members - only check one user for the family
   //
   if (!mnumA[0].equals( "" )) {        // if mnum exists
     
      if (mnumA[1].equals( mnumA[0] )) {        // if mnum is the same

         mshipA[1] = "";
      }
      if (mnumA[2].equals( mnumA[0] )) {        // if mnum is the same

         mshipA[2] = "";
      }
      if (mnumA[3].equals( mnumA[0] )) {        // if mnum is the same

         mshipA[3] = "";
      }
      if (mnumA[4].equals( mnumA[0] )) {        // if mnum is the same

         mshipA[4] = "";
      }
   }
     
   if (!mnumA[1].equals( "" )) {        // if mnum exists

      if (mnumA[2].equals( mnumA[1] )) {        // if mnum is the same

         mshipA[2] = "";
      }
      if (mnumA[3].equals( mnumA[1] )) {        // if mnum is the same

         mshipA[3] = "";
      }
      if (mnumA[4].equals( mnumA[1] )) {        // if mnum is the same

         mshipA[4] = "";
      }
   }

   if (!mnumA[2].equals( "" )) {        // if mnum exists

      if (mnumA[3].equals( mnumA[2] )) {        // if mnum is the same

         mshipA[3] = "";
      }
      if (mnumA[4].equals( mnumA[2] )) {        // if mnum is the same

         mshipA[4] = "";
      }
   }

   if (!mnumA[3].equals( "" )) {        // if mnum exists

      if (mnumA[4].equals( mnumA[3] )) {        // if mnum is the same

         mshipA[4] = "";
      }
   }

  
   
   long year = slotParms.date / 10000;                       // break down the tee time date
   long month = (slotParms.date - (year * 10000)) / 100;
   long day = slotParms.date - ((year * 10000) + (month * 100));
   long mmdd = (month * 100) + day;                        // create mmdd value

   
   //
   //  Create the start date and end date for queries.
   //  The season is from 11/01 to 4/30, so we must determine which years to use.
   //
   if (mmdd > edate && mmdd < sdate) {         // if out of season
      
      sdate = 0;
      edate = 0;       // out of season - skip checks
      
   } else {
      
      sdate = (year * 10000) + (month * 100) + 01;         // mm/01/yyyy -  we will check for max rounds per month
      edate = (year * 10000) + (month * 100) + 31;         // mm/31/yyyy
   }

   //
   //  Count the existing tee times if in season and at least one of the members is one of the following mship types:
   //
   //       Renter Sports
   //       Sports
   //
   if (sdate > 0  &&
       (mshipA[0].equals("Renter Sports") || mshipA[0].equals("Sports") ||
        mshipA[1].equals("Renter Sports") || mshipA[1].equals("Sports") || 
        mshipA[2].equals("Renter Sports") || mshipA[2].equals("Sports") || 
        mshipA[3].equals("Renter Sports") || mshipA[3].equals("Sports") || 
        mshipA[4].equals("Renter Sports") || mshipA[4].equals("Sports"))) {
       
      try {

         //
         // statements for queries    
         //
         pstmt2m = con.prepareStatement (
            "SELECT mNum1, mNum2, mNum3, mNum4, mNum5 " +
            "FROM teecurr2 WHERE date >= ? AND date <= ? AND (date != ? AND courseName != ?) AND " +
                       "(mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?)");

         pstmt3m = con.prepareStatement (
            "SELECT mNum1, mNum2, mNum3, mNum4, mNum5 " +
            "FROM teepast2 WHERE date >= ? AND date <= ? AND (date != ? AND courseName != ?) AND " +
                       "(mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?)");


         //
         //  Check each player/family
         //
         i = 0;
         while (i < 5 && error == false) {
            
            if (mshipA[i].equals("Renter Sports") || mshipA[i].equals("Sports")) {
               
               count = 1;         // count this member
               
               //
               //  Count number of family members in this tee time
               //
               i2 = i + 1;        // next player
                  
               while (i2 < 5) {
               
                  if (mnumA[i].equals(mnumA[i2])) {
                     
                     count++;              // add family member
                  }
                  i2++;
               }

               //
               //  Count the rounds for this member (excluding the day of this tee time)
               //
               pstmt2m.clearParameters();                  
               pstmt2m.setLong(1, sdate);
               pstmt2m.setLong(2, edate);
               pstmt2m.setLong(3, slotParms.date);      // make sure not this date
               pstmt2m.setString(4, slotParms.course);      // and course
               pstmt2m.setString(5, mnumA[i]);
               pstmt2m.setString(6, mnumA[i]);
               pstmt2m.setString(7, mnumA[i]);
               pstmt2m.setString(8, mnumA[i]);
               pstmt2m.setString(9, mnumA[i]);
               rs = pstmt2m.executeQuery();

               while (rs.next()) {

                   mNum1 = rs.getString("mNum1");
                   mNum2 = rs.getString("mNum2");
                   mNum3 = rs.getString("mNum3");
                   mNum4 = rs.getString("mNum4");
                   mNum5 = rs.getString("mNum5");
                 
                  if (mnumA[i].equals(mNum1)) {
                     count++;              // add family member
                  }
                  if (mnumA[i].equals(mNum2)) {
                     count++;              // add family member
                  }
                  if (mnumA[i].equals(mNum3)) {
                     count++;              // add family member
                  }
                  if (mnumA[i].equals(mNum4)) {
                     count++;              // add family member
                  }
                  if (mnumA[i].equals(mNum5)) {
                     count++;              // add family member
                  }
               }

               pstmt3m.clearParameters();                    
               pstmt3m.setLong(1, sdate);
               pstmt3m.setLong(2, edate);
               pstmt3m.setLong(3, slotParms.date);
               pstmt3m.setString(4, slotParms.course);      // and course
               pstmt3m.setString(5, mnumA[i]);
               pstmt3m.setString(6, mnumA[i]);
               pstmt3m.setString(7, mnumA[i]);
               pstmt3m.setString(8, mnumA[i]);
               pstmt3m.setString(9, mnumA[i]);
               rs = pstmt3m.executeQuery();

               while (rs.next()) {

                   mNum1 = rs.getString("mNum1");
                   mNum2 = rs.getString("mNum2");
                   mNum3 = rs.getString("mNum3");
                   mNum4 = rs.getString("mNum4");
                   mNum5 = rs.getString("mNum5");
                 
                  if (mnumA[i].equals(mNum1)) {
                     count++;              // add family member
                  }
                  if (mnumA[i].equals(mNum2)) {
                     count++;              // add family member
                  }
                  if (mnumA[i].equals(mNum3)) {
                     count++;              // add family member
                  }
                  if (mnumA[i].equals(mNum4)) {
                     count++;              // add family member
                  }
                  if (mnumA[i].equals(mNum5)) {
                     count++;              // add family member
                  }                  
               }


               if (count > max)  {                // if this time would put the family over the max

                  error = true;                   // reject this member
                  slotParms.mship = mship;
                  slotParms.player = slotParms.player1;
               }
            }        // end of IF player is mship to check
            
            i++;     // do next player
                    
         }          // end of WHILE player

         pstmt2m.close();
         pstmt3m.close();

      }
      catch (SQLException e1) {

          Utilities.logError("SQL Error Checking Max Rounds - verifyCustom.checkJLGCmships " + e1.getMessage());        // log the error message
      }

      catch (Exception e) {

          Utilities.logError("Exception Checking Max Rounds - verifyCustom.checkJLGCmships " + e.getMessage());        // log the error message
      
      } finally {

          try { rs.close(); }
          catch (Exception ignore) {}

          try { pstmt2m.close(); }
          catch (Exception ignore) {}

          try { pstmt3m.close(); }
          catch (Exception ignore) {}
          
      }
   
   }       // end of if date in season

   return(error);
 }                  // end of checkJLGCmships



/**
 //************************************************************************
 //
 //  Hazeltine2010 - temp site while their course is down
 //
 //  checkHazeltine2010 - checks members for max rounds per month per course.
 //
 //************************************************************************
 **/

 public static boolean checkHazeltine2010(parmSlot slotParms, Connection con) {


   ResultSet rs = null;
   PreparedStatement pstmt2m = null;
   PreparedStatement pstmt3m = null;

   boolean error = false;
   
   int count = 0;
   int i = 0;
   int max = 2;               // max rounds per month
   
   String [] userA = new String [5];
   String [] playerA = new String [5];

   userA[0] = slotParms.user1;              // get values from this request
   userA[1] = slotParms.user2;
   userA[2] = slotParms.user3;
   userA[3] = slotParms.user4;
   userA[4] = slotParms.user5;
   playerA[0] = slotParms.player1;
   playerA[1] = slotParms.player2;
   playerA[2] = slotParms.player3;
   playerA[3] = slotParms.player4;
   playerA[4] = slotParms.player5;
   
   
   int year = (int)slotParms.date / 10000;                       // extract the month from the tee time date
   int month = (int)(slotParms.date - (year * 10000)) / 100;

   
   //
   //  Count the existing tee times for the month and course of this tee time
   //
   try {

      //
      // statements for queries    
      //
      pstmt2m = con.prepareStatement (
         "SELECT COUNT(*) " +
         "FROM teecurr2 WHERE teecurr_id != ? AND mm = ? AND yy = ? AND courseName = ? AND " +
                    "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)");

      pstmt3m = con.prepareStatement (
         "SELECT COUNT(*) " +
         "FROM teepast2 WHERE mm = ? AND yy = ? AND courseName = ? AND " +
                    "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)");

      //
      //  Check each member
      //
      i = 0;
      while (i < 5 && error == false) {
          
         count = 0;

         if (!userA[i].equals("")) {       // if member

            //
            //  Count the rounds for this member (excluding the day of this tee time)
            //
            pstmt2m.clearParameters();                  
            pstmt2m.setInt(1, slotParms.teecurr_id);
            pstmt2m.setInt(2, month);
            pstmt2m.setInt(3, year);
            pstmt2m.setString(4, slotParms.course);      // and course
            pstmt2m.setString(5, userA[i]);
            pstmt2m.setString(6, userA[i]);
            pstmt2m.setString(7, userA[i]);
            pstmt2m.setString(8, userA[i]);
            pstmt2m.setString(9, userA[i]);
            rs = pstmt2m.executeQuery();

            if (rs.next()) {

                count = rs.getInt(1);
            }

            pstmt3m.clearParameters();                    
            pstmt3m.setInt(1, month);
            pstmt3m.setInt(2, year);
            pstmt3m.setString(3, slotParms.course);      // and course
            pstmt3m.setString(4, userA[i]);
            pstmt3m.setString(5, userA[i]);
            pstmt3m.setString(6, userA[i]);
            pstmt3m.setString(7, userA[i]);
            pstmt3m.setString(8, userA[i]);
            rs = pstmt3m.executeQuery();

            if (rs.next()) {

                count += rs.getInt(1);
            }

            if (count >= max)  {                // if this time would put the member over the max

               error = true;                   // reject this member
               slotParms.player = playerA[i];  // get member name
            }
         }        // end of IF player is a member

         i++;     // do next player

      }          // end of WHILE player

      pstmt2m.close();
      pstmt3m.close();

   }
   catch (SQLException e1) {

       Utilities.logError("SQL Error Checking Max Rounds - verifyCustom.checkHazeltine2010 " + e1.getMessage());        // log the error message
   }

   catch (Exception e) {

       Utilities.logError("Exception Checking Max Rounds - verifyCustom.checkHazeltine2010 " + e.getMessage());        // log the error message

   } finally {

       try { rs.close(); }
       catch (Exception ignore) {}

       try { pstmt2m.close(); }
       catch (Exception ignore) {}

       try { pstmt3m.close(); }
       catch (Exception ignore) {}

   }

   return(error);
 }                  // end of checkHazeltine2010



/**
 //************************************************************************
 //
 //  checkOakmontGuestQuota - special Guest processing for Oakmont CC.
 //
 //     At this point we know this is Oakmont, there is more than one guest
 //     in this tee time and it is Feb, Mar or Apr.
 //
 //     Restrictions:
 //
 //         Members can book guest times in advance (all year), however they   
 //         can only book up to 8 (was 10) per month during Feb, Mar and Apr.  The guest times
 //         can be for any time of the season, we only check when the time was booked. 
 //         Therefore, we must track when the tee time was actually created.
 //
 //         1/06/10 - changed max from 10 to 8 per pro's instructions (case 1364).
 //
 //      Note:  5-somes not allowed at Oakmont
 //
 //      **** See also Proshop_slotm.checkOakGuestQuota ************************
 //
 //************************************************************************
 **/

 public static boolean checkOakmontGuestQuota(parmSlot slotParms, int month, Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   boolean error = false;
   
   int i = 0;
   int i2 = 0;
   int count = 0;
   int max = 7;                             // max is 8 advance guest times per month - use > 7 for compare (was 10 in 2009)

   String [] usergA = new String [4];       // array to hold the members' usernames
   String [] userA = new String [4];        // array to hold the usernames
   String [] playerA = new String [4];      // array to hold the player names

   userA[0] = slotParms.user1;              // get values from this request
   userA[1] = slotParms.user2;
   userA[2] = slotParms.user3;
   userA[3] = slotParms.user4;

   usergA[0] = slotParms.userg1;                
   usergA[1] = slotParms.userg2;
   usergA[2] = slotParms.userg3;
   usergA[3] = slotParms.userg4;

   playerA[0] = slotParms.player1;                
   playerA[1] = slotParms.player2;                
   playerA[2] = slotParms.player3;                
   playerA[3] = slotParms.player4;                


   try {

      //
      //  Check each player for member followed by guest
      //
      for (i = 0; i < 3; i++) {             // check first 3 players (no 5-somes at Oakmont, if 4th not guest then doesn't matter)
         
         i2 = i + 1;

         if (error == false) {              // if error not already hit
  
            if (!userA[i].equals( "" ) && userA[i].equals( usergA[i2] )) {       // if player followed by his/her guest
               
               count = 0;
        
               //
               //   Check teecurr for other guest times for this member that were scheduled during this month
               //
               pstmt = con.prepareStatement (
                  "SELECT COUNT(*) " +
                  "FROM teecurr2 " +
                  "WHERE custom_int = ? AND " +
                  "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ?)");

               pstmt.clearParameters();
               pstmt.setInt(1, month);
               pstmt.setString(2, userA[i]);
               pstmt.setString(3, userA[i]);
               pstmt.setString(4, userA[i]);
               pstmt.setString(5, userA[i]);
               rs = pstmt.executeQuery();

               if (rs.next()) {

                  count = rs.getInt("COUNT(*)");
               }    

               pstmt.close();


               if (count > max) {                         // if 10 advance guest times already created this month

                  error = true;                           // indicate error
                  slotParms.player = playerA[i];          // save player name for error message
               }           
            }
         }
      }              // end of FOR loop (do each player)
        
   } catch (Exception e) {

      Utilities.logError("Error checking for Oakmont guests - verifyCustom.checkOakmontGuestQuota " + e.getMessage());
      
   } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }
   
    return(error);
   
 }


/**
 //************************************************************************
 //
 //  checkBaltusrolGuestQuota - special Guest processing for Baltusrol GC.
 //
 //     At this point we know this is baltusrol and there is more than one guest
 //     in this tee time.
 //
 //     Restrictions:
 //
 //         Members can have up to 5 guest times scheduled in advance.  
 //
 //      Note:  5-somes not allowed at Baltusrol
 //
 //      **** See also Proshop_slotm.checkBaltGuestQuota ************************
 //
 //************************************************************************
 **/

 public static boolean checkBaltusrolGuestQuota(parmSlot slotParms, Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   boolean error = false;
   
   int i = 0;
   int i2 = 0;
   int count = 0;
  // int max = 2;                           // max is 3 advance guest times (use 2 to allow for this one) 
   int max = 4;                             // max is 5 advance guest times (use 4 to allow for this one) 

   String [] usergA = new String [4];       // array to hold the members' usernames
   String [] userA = new String [4];        // array to hold the usernames
   String [] playerA = new String [4];      // array to hold the player names

   userA[0] = slotParms.user1;              // get values from this request
   userA[1] = slotParms.user2;
   userA[2] = slotParms.user3;
   userA[3] = slotParms.user4;

   usergA[0] = slotParms.userg1;                
   usergA[1] = slotParms.userg2;
   usergA[2] = slotParms.userg3;
   usergA[3] = slotParms.userg4;

   playerA[0] = slotParms.player1;                
   playerA[1] = slotParms.player2;                
   playerA[2] = slotParms.player3;                
   playerA[3] = slotParms.player4;                


   try {

      //
      //  Check each player for member followed by guest
      //
      for (i = 0; i < 3; i++) {             // check first 3 players (no 5-somes, if 4th not guest then doesn't matter)
         
         i2 = i + 1;

         if (error == false) {              // if error not already hit
  
            if (!userA[i].equals( "" ) && userA[i].equals( usergA[i2] )) {       // if player followed by his/her guest
               
               count = 0;
        
               //
               //   Check teecurr for other guest times for this member that are scheduled 
               //
               pstmt = con.prepareStatement (
                  "SELECT COUNT(*) " +
                  "FROM teecurr2 " +
                  "WHERE teecurr_id != ? AND (userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ?)");

               pstmt.clearParameters();
               pstmt.setInt(1, slotParms.teecurr_id);    // do not include this one!
               pstmt.setString(2, userA[i]);
               pstmt.setString(3, userA[i]);
               pstmt.setString(4, userA[i]);
               pstmt.setString(5, userA[i]);
               rs = pstmt.executeQuery();

               if (rs.next()) {

                  count = rs.getInt("COUNT(*)");
               }    

               pstmt.close();


               if (count > max) {                         // if 5 advance guest times already exist for this member

                  error = true;                           // indicate error
                  slotParms.player = playerA[i];          // save player name for error message
               }           
            }
         }
      }              // end of FOR loop (do each player)
        
   } catch (Exception e) {

      Utilities.logError("Error checking for Baltusrol guests - verifyCustom.checkBaltusrolGuestQuota " + e.getMessage());
      
   } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return(error);

 }


 // *********************************************************
 //  Meadow Club - Custom Processing (check date and time of day for 3-some only time)
 // *********************************************************

 public static boolean checkMeadowClub(long date, int time, String name) {


   boolean status = false;

   long shortDate = date - ((date / 10000) * 10000);       // get mmdd (i.e.  20060512 - 20060000 = 512)

   //
   //     Special 3-some times for 2010 (must change for other years!!!)
   //
   //
   if (shortDate == 204 || shortDate == 304 || shortDate == 401 || shortDate == 527 || shortDate == 610 || 
       shortDate == 708 || shortDate == 812 || shortDate == 902 || shortDate == 1007 || shortDate == 1104) {      

      if (time > 659 && time < 1011) {         // if between 7:00 and 10:10 AM   

         status = true;                        // 3-some only time
      }
   }

   return(status);         // true = 3-somes only time
 }


 
 /*
 // *********************************************************
 //  Rivercrest - Custom Processing (check date and time of day for 3-some only time)
 // *********************************************************

 public static boolean checkRivercrest(long date, int time, String name) {


   boolean status = false;

   long shortDate = date - ((date / 10000) * 10000);       // get mmdd (i.e.  20060512 - 20060000 = 512)

   //
   //     Special 3-some times (for women) on Thurs between mid April and mid Sept
   //
   //
   if (name.equals( "Thursday" ) && shortDate > 403 && shortDate < 919) {      

      if (date != 20080626 && date != 20080717 && date != 20080828 && date != 20080904 && date != 20080911) {   // skip event days in 2008

         if (time > 829 && time < 1031) {  // if 8:30 - 10:30

            status = true;                        // 3-some only time
         }
      }
   }

   return(status);         // true = 3-somes only time
 }


 // *********************************************************
 //  Rivercrest - Custom Processing (check date for 3-some only times)
 // *********************************************************

 public static boolean checkRivercrestDay(long date, String name) {


   boolean status = false;

   long shortDate = date - ((date / 10000) * 10000);     

   //
   //     Special 3-some days (for women) on Thurs between mid April and mid Sept - do not allow consecutive tee times before or during their times
   //
   //
   if (name.equals( "Thursday" ) && shortDate > 403 && shortDate < 919) {      

      if (date != 20080626 && date != 20080717 && date != 20080828 && date != 20080904 && date != 20080911) {   // skip event days in 2008

         status = true;                        // 3-some only day
      }
   }

   return(status);         // true = 3-somes only day
 }
  */


 // *********************************************************
 //  Portland GC - Custom Processing (check tee time for Walk-Up only time)
 // *********************************************************

 public static boolean checkPGCwalkup(long date, int time, String day) {


   boolean status = false;

   long shortDate = date - ((date / 10000) * 10000);   
   
   int stime = 0;
   int etime = 0;
   int min = 0;
  
   //  
   //   get minute value of tee time
   //
   min = time/100;                // get hour vallue
   min = time - (min * 100);      // get minute value

   //
   //    Check if tee time is for Walk-Ups Only
   //
   if (day.equals( "Friday" )) {      

      /*
      if (shortDate > 1009 || shortDate < 424) {      // winter season
         
        stime = 730;         // 7:30 AM
        
      } else {                                         // summer season
         
        stime = 1000;         // 10:00 AM
      }
       */
      
      stime = 730;         // 7:30 AM (all year now - changed 4/19/2010 per club's request)
        
      etime = 1930;         // to 7:30 PM
      
   } else {
      
      stime = 630;          // 6:30 AM
      etime = 1930;         // to 7:30 PM      
   }

   //
   //  Check times within the range for Walk-Up Only
   //
   if (time >= stime && time <= etime) {
      
    //  if (day.equals("Tuesday") && shortDate > 331 && shortDate < 1101 && time > 759 && time < 1001) {  // exception for Ladies Day
      if (day.equals("Tuesday") && shortDate > 310 && shortDate < 1110 && time < 1300) {  // exception for Ladies Day (Tues open to 1:00 PM)
         
         status = false;
         
      } else if (day.equals("Wednesday") && (shortDate == 617 || shortDate == 701 || shortDate == 715 || shortDate == 729 || shortDate == 812 || 
              shortDate == 819) && (time > 1551 && time < 1801)) {      // exception for Two Ball outings
         
         status = false;
         
      } else if (day.equals("Wednesday") && (shortDate > 501 && shortDate < 912) && (time > 729 && time < 959)) {  // exception for Ladies 9-Holers
         
         status = false;         
         
      } else {
         
         //
         //  Tee Times = 700, 707, 715, 722, 730, 737, 745, 752, 800, etc.  -  every other is a walk up
         //
         if (min == 7 || min == 22 || min == 37 || min == 52) {    

            status = true;      // indicate walk-up time
         }
      }
   }
   
   return(status);         // true = Walk-Up Only time
 }


 // ******************************************************************************
 //  Los Coyotes - Get a member's Gender and appeand to the Member Number
 // ******************************************************************************

 public static String getLCGender(String user, String mnum, Connection con) {


    ResultSet rs = null;
    PreparedStatement pstmt = null;
    
    String mtype = "";

    try {

      if (!user.equals( "" )) {

         pstmt = con.prepareStatement (
                  "SELECT m_type FROM member2b WHERE username = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setString(1, user);
         rs = pstmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            mtype = rs.getString(1);         // user's member type
         }

         pstmt.close();                  // close the stmt
           
         //
         //  Add Primary/Secondary/Junior and gender to mnum
         //
         if (mtype.startsWith( "Primary" )) {
           
            mnum = mnum + " P";            // Primary
              
         } else {
           
            if (mtype.startsWith( "Secondary" )) {

               mnum = mnum + " S";            // Secondary

            } else {

               mnum = mnum + " J";            // Junior
            }
         }
           
         if (mtype.endsWith( "Female" ) || mtype.endsWith( "Ladies" )) {
           
            mnum = mnum + "F";            // Female
              
         } else {
           
            mnum = mnum + "M";            // Male
         }
           
      }

    } catch (Exception ignore) {

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return(mnum);

 }                   // end of getLCGender
 
/**
  ***************************************************************************************
  *
  * Checks to see that no guests are present in a Fri/Sat/Sun/Holidate teetime for member type 'Restricted Golf'
  *
  * @param slotParms contains all information for the teetime
  * @param con connection
  * @return true when unauthorized guests detected, false otherwise
  *
  ***************************************************************************************
  **/
 public static boolean checkWoodwayGuests(parmSlot slotParms, Connection con) {
     
     boolean check = false;
     
     int holiday = 0;
     
     long memDay = Hdate1;       // Memorial Day     !!!!!!!!!! Must keep current !!!!!!!!!!!!!!!!!!
     long july4th = Hdate2;      // 4th of July
     long laborDay = Hdate3;     // Labor Day
     
     String day = slotParms.day;
   
     String [] usergA = new String [5];       // array to hold the members' usernames
     String [] userA = new String [5];        // array to hold the usernames
     String [] playerA = new String [5];      // array to hold the player's names
     String [] mshipA = new String [5];       // array to hold the players' membership types
   
     playerA[0] = slotParms.player1;
     playerA[1] = slotParms.player2;
     playerA[2] = slotParms.player3;
     playerA[3] = slotParms.player4;
     playerA[4] = slotParms.player5;
   
     usergA[0] = slotParms.userg1;                  
     usergA[1] = slotParms.userg2;
     usergA[2] = slotParms.userg3;
     usergA[3] = slotParms.userg4;
     usergA[4] = slotParms.userg5;
     
     mshipA[0] = slotParms.mship1;
     mshipA[1] = slotParms.mship2;
     mshipA[2] = slotParms.mship3;
     mshipA[3] = slotParms.mship4;
     mshipA[4] = slotParms.mship5;
     
     userA[0] = slotParms.user1;
     userA[1] = slotParms.user2;
     userA[2] = slotParms.user3;
     userA[3] = slotParms.user4;
     userA[4] = slotParms.user5;
     
     holiday = 0;      // default no holiday
     
     if (slotParms.date == memDay || slotParms.date == july4th || slotParms.date == laborDay) {
         
         holiday = 1;
     }
     
     if (day.equals("Friday") || day.equals("Saturday") || day.equals("Sunday") || holiday == 1) {
         
         for (int i=0; i<5; i++) {
             
             if (!check) {
                 //Restricted Golf
                 if (mshipA[i].equals("Restricted Golf") && (usergA[0].equals( userA[i] ) || usergA[1].equals( userA[i] ) || 
                         usergA[2].equals( userA[i] ) || usergA[3].equals( userA[i] ) || usergA[4].equals( userA[i] ))) {
                     
                     check = true;                           // indicate error
                     slotParms.player = playerA[i];       // save player name for error message 
                 }
             }
         }
     }
     
     return check;
 }
 
 

 
/**
  ***************************************************************************************
  *
  *   Canterbury - check if tee time contains a member and a guest 
  *
  ***************************************************************************************
  **/
 public static boolean checkCanterburyGst(parmSlot slotParms) {
     
     boolean check = false;
     
     
      //
      //  Get the current server time (CT)
      //
      Calendar cal2 = new GregorianCalendar();       
      int cal_hour = cal2.get(Calendar.HOUR_OF_DAY);
      int cal_min = cal2.get(Calendar.MINUTE);

      int cal_time = (cal_hour * 100) + cal_min;     // CT

     //
     //  Members can book normal tee times 7 days in advance starting at 8:00 AM ET.
     //  They can book guest times (at least one member and one guest) up to 6 months in advance.
     //  We configured the members for 180 days in advance so now we need to make sure any tee time
     //  that is more than 7 days in advance contains at least 1 mem and 1 guest.
     //
     if (slotParms.ind > 7 || (slotParms.ind == 7 && cal_time < 700)) {   // if more than 7 days in adv
                        
        //
        //  Must be at least one member and one guest
        //
        if (slotParms.members == 0 || slotParms.guests == 0) {

           check = true;                           // indicate error
        }
     }
     
     return check;
 }
  

/**
  ***************************************************************************************
  *
  *   Southern Hills CC - If Friday,during a specific date and time range, check if tee time contains a member and 2 guests
  *
  ***************************************************************************************
  **/
 public static boolean checkSouthernHillsGst(parmSlot slotParms) {

     boolean error = false;

     int shortdate = (slotParms.mm * 100) + slotParms.dd;

     /*
      During the following times, each tee time must contain at least 1 member and 2 guests 
       
      During March 28 - May 31 and August 16 - October 31.
      9:33 - 10:33
      1:31 - 2:31

      During June 1st - August 15th
      9:29 - 10:28
      1:27 - 2:26
     */
     if (slotParms.day.equals("Friday") &&
        ((((shortdate >= 328 && shortdate <= 531) || (shortdate >= 816 && shortdate <= 1031)) && ((slotParms.time >= 933 && slotParms.time <= 1033) || (slotParms.time >= 1331 && slotParms.time <= 1431))) ||
        (shortdate >= 601 && shortdate <= 815 && ((slotParms.time >= 929 && slotParms.time <= 1028) || (slotParms.time >= 1327 && slotParms.time <= 1426))))) {
        
        //  Must be at least one member and one guest
        if (slotParms.members < 1 || slotParms.guests < 2) {

           error = true;                           // indicate error
        }
     }
 
     return error;
 }
 
 
/**
  ***************************************************************************************
  *
  *   Wollaston GC - check to see if today is Monday (block all member tee time access on Mondays when shop is closed) 
  *
  ***************************************************************************************
  **/
 public static boolean checkWollastonMon() {
     
     boolean check = false;
     
     
      //
      //  Get the day of the week
      //
      Calendar cal = new GregorianCalendar();       
      int day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07, Sun - Sat)


     //
     //  Members cannot access the tee times when the golf shop is closed.
     //  They use the standard 'Day Before' config option to block access the day before at 6:30 PM each day.
     //  This will make sure that members cannot access tee times all day Monday too.  Case 1819.
     //
     if (day_num == 2) {         // if Monday
                        
        check = true;            // indicate so
     }
     
     return check;
 }
 
 
 

/**
  ***************************************************************************************
  *
  *   Check if tee time contains at least 2 players (members or guests) 
  *
  ***************************************************************************************
  **/
 public static boolean check2Players(parmSlot slotParms) {
     
     boolean check = false;
     
     int count = 0;
     
     if (!slotParms.player1.equals("") && !slotParms.player1.equalsIgnoreCase("x")) {     // if player is a member or guest

        count++;;
     }
     if (!slotParms.player2.equals("") && !slotParms.player2.equalsIgnoreCase("x")) {     // if player is a member or guest

        count++;;
     }
     if (!slotParms.player3.equals("") && !slotParms.player3.equalsIgnoreCase("x")) {     // if player is a member or guest

        count++;;
     }
     if (!slotParms.player4.equals("") && !slotParms.player4.equalsIgnoreCase("x")) {     // if player is a member or guest

        count++;;
     }
     if (!slotParms.player5.equals("") && !slotParms.player5.equalsIgnoreCase("x")) {     // if player is a member or guest

        count++;;
     }
     
     if (count < 2) check = true;        // error if only 1 player
     
     return check;
 }
 
 

/**
  ***************************************************************************************
  *
  *   CC of Naples - check if Associate B mship durinmg restricted time. 
  *
  ***************************************************************************************
  **/
 public static boolean checkNaplesAssocB(parmSlot slotParms) {
     
     boolean check = false;
     
     //
     //  break down date of tee time
     //
     long yy = slotParms.date / 10000;                             // get year
     long mm = (slotParms.date - (yy * 10000)) / 100;              // get month
     long dd = (slotParms.date - (yy * 10000)) - (mm * 100);       // get day

     long dateShort = (mm * 100) + dd;
     
     if ((dateShort > 1031 || dateShort < 431) && slotParms.time < 1230) {   // if in season and before 12:30 
                        
        //
        //  Check for any "Associate B..." mships - not allowed
        //
        String [] playerA = new String [5];      // array to hold the player's names
        String [] mshipA = new String [5];       // array to hold the players' membership types

        playerA[0] = slotParms.player1;
        playerA[1] = slotParms.player2;
        playerA[2] = slotParms.player3;
        playerA[3] = slotParms.player4;
        playerA[4] = slotParms.player5;

        mshipA[0] = slotParms.mship1;
        mshipA[1] = slotParms.mship2;
        mshipA[2] = slotParms.mship3;
        mshipA[3] = slotParms.mship4;
        mshipA[4] = slotParms.mship5;

        for (int i=0; i<5; i++) {
             
           if (mshipA[i].startsWith("Associate B") || mshipA[i].startsWith("Associate  B")) {
                     
              check = true;                           // indicate error
              slotParms.player = playerA[i];       // save player name for error message 
              break;
           }
        }
     }
     
     return check;
 }
 
 

/**
  ***************************************************************************************
  *
  *   CC of Naples - check if Associate B mship has reached quota this month 
  *
  ***************************************************************************************
  **/
 public static boolean checkNaplesAssocBQuota(parmSlot slotParms, Connection con) {
     
  boolean error = false;

  //
  //  break down date of tee time
  //
  long yy = slotParms.date / 10000;                             // get year
  long mm = (slotParms.date - (yy * 10000)) / 100;              // get month
  long dd = (slotParms.date - (yy * 10000)) - (mm * 100);       // get day

  long dateShort = (mm * 100) + dd;
  long sdate = (yy * 10000) + (mm * 100);          // start date for this month
  long edate = (yy * 10000) + (mm * 100) + 32;     // end date for this month
 

  //
  //  Check if in season
  //
  if (dateShort > 1031 || dateShort < 431) {   // if in season
                        
     PreparedStatement pstmt = null;
     ResultSet rs = null;

     int i = 0;
     int count = 0;                               // number of guests for date range
     int max = 0;                                 // max of rounds
     int sid = 0;      // event signup_id

     String [] userA = new String [5];            // array to hold the usernames
     String [] playerA = new String [5];          // array to hold the player's names
     String [] mnumA = new String [5];            // array to hold the players' member numbers
     String [] mshipA = new String [5];           // array to hold the players' membership types

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

     mnumA[0] = slotParms.mNum1;
     mnumA[1] = slotParms.mNum2;
     mnumA[2] = slotParms.mNum3;
     mnumA[3] = slotParms.mNum4;
     mnumA[4] = slotParms.mNum5;

     mshipA[0] = slotParms.mship1;
     mshipA[1] = slotParms.mship2;
     mshipA[2] = slotParms.mship3;
     mshipA[3] = slotParms.mship4;
     mshipA[4] = slotParms.mship5;

     sid = slotParms.signup_id;

     try {

         //
         //  Check each player
         //
         loop1:
         for (i = 0; i < 5; i++) {
            
             count = 0;         // init counter for each player

             slotParms.player = playerA[i];      // save the player name we are currently checking
             
             if (mshipA[i].equals("Associate B Single")) {
                               
                 //
                 //  Check this member's tee quota for this month (max = 4 per month)
                 //
                 max = 3;        // max for Singles (no more than 4 - assume this is one)
 
                 //
                 //   Check teepast2
                 //
                 pstmt = con.prepareStatement (
                    "SELECT username1, username2, username3, username4, username5, show1, show2, show3, show4, show5 " +
                    "FROM teepast2 " +
                    "WHERE " +
                         "date > ? AND date < ? AND (" +
                         "(username1 = ? AND show1 = 1) OR " +
                         "(username2 = ? AND show2 = 1) OR " +
                         "(username3 = ? AND show3 = 1) OR " +
                         "(username4 = ? AND show4 = 1) OR " +
                         "(username5 = ? AND show5 = 1))");

                 pstmt.setLong(1, sdate);
                 pstmt.setLong(2, edate);
                 pstmt.setString(3, userA[i]);
                 pstmt.setString(4, userA[i]);
                 pstmt.setString(5, userA[i]);
                 pstmt.setString(6, userA[i]);
                 pstmt.setString(7, userA[i]);
                 rs = pstmt.executeQuery();      // execute the prepared stmt

                 while (rs.next()) {

                     if (rs.getString("username1").equals(userA[i]) && rs.getInt("show1") == 1) count++;
                     if (rs.getString("username2").equals(userA[i]) && rs.getInt("show2") == 1) count++;
                     if (rs.getString("username3").equals(userA[i]) && rs.getInt("show3") == 1) count++;
                     if (rs.getString("username4").equals(userA[i]) && rs.getInt("show4") == 1) count++;
                     if (rs.getString("username5").equals(userA[i]) && rs.getInt("show5") == 1) count++;

                     if (count > max) {                         // if over the limit

                         error = true;                          // indicate error
                         break loop1;
                     }

                 } // end while of all matching username's

                 rs.close();
                 pstmt.close();


                 //
                 //   Check teecurr2
                 //
                 pstmt = con.prepareStatement (
                    "SELECT username1, username2, username3, username4, username5 " +
                    "FROM teecurr2 " +
                    "WHERE " +
                         "date > ? AND date < ? AND " +
                         "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?) AND " +
                         "teecurr_id != ?");

                 pstmt.setLong(1, sdate);
                 pstmt.setLong(2, edate);
                 pstmt.setString(3, userA[i]);
                 pstmt.setString(4, userA[i]);
                 pstmt.setString(5, userA[i]);
                 pstmt.setString(6, userA[i]);
                 pstmt.setString(7, userA[i]);
                 pstmt.setInt(8, slotParms.teecurr_id);
                 rs = pstmt.executeQuery();      // execute the prepared stmt

                 while (rs.next()) {

                     if (rs.getString("username1").equals(userA[i]) ) count++;
                     if (rs.getString("username2").equals(userA[i]) ) count++;
                     if (rs.getString("username3").equals(userA[i]) ) count++;
                     if (rs.getString("username4").equals(userA[i]) ) count++;
                     if (rs.getString("username5").equals(userA[i]) ) count++;

                     if (count > max) {                         // if over the limit

                         error = true;                          // indicate error
                         break loop1;
                     }

                 } // end while of all matching user

                 rs.close();
                 pstmt.close();


                 //
                 //   Check evntSup2b
                 //
                 pstmt = con.prepareStatement(
                         "SELECT s.username1, s.username2, s.username3, s.username4, s.username5 " +
                         "FROM evntsup2b s " +
                         "LEFT OUTER JOIN events2b e ON e.name = s.name " +
                         "WHERE s.moved = 0 AND e.date > ? AND e.date < ? AND " +
                         "(s.username1 = ? OR s.username2 = ? OR s.username3 = ? OR s.username4 = ? OR s.username5 = ?) AND " +
                         "s.id != ?");
                 pstmt.clearParameters();
                 pstmt.setLong(1, sdate);
                 pstmt.setLong(2, edate);
                 pstmt.setString(3, userA[i]);
                 pstmt.setString(4, userA[i]);
                 pstmt.setString(5, userA[i]);
                 pstmt.setString(6, userA[i]);
                 pstmt.setString(7, userA[i]);
                 pstmt.setInt(8, sid);

                 rs = pstmt.executeQuery();

                 while (rs.next()) {

                     if (rs.getString("s.username1").equals(userA[i]) ) count++;
                     if (rs.getString("s.username2").equals(userA[i]) ) count++;
                     if (rs.getString("s.username3").equals(userA[i]) ) count++;
                     if (rs.getString("s.username4").equals(userA[i]) ) count++;
                     if (rs.getString("s.username5").equals(userA[i]) ) count++;

                     if (count > max) {                         // if over the limit

                         error = true;                          // indicate error
                         break loop1;
                     }
                 }

                 rs.close();
                 pstmt.close();

             } else if (mshipA[i].equals("Associate  B Family")) {   // note: pro entered this with 2 spaces!

                 //
                 //  Check this families' tee quota for this month (max = 8 per month)
                 //
                 max = 8;          // max rounds for family
                 count = 0;
                 
                 //
                 //  Start with the number of family members in this request
                 //
                 if (mnumA[0].equals(mnumA[i]) ) count++;
                 if (mnumA[1].equals(mnumA[i]) ) count++;
                 if (mnumA[2].equals(mnumA[i]) ) count++;
                 if (mnumA[3].equals(mnumA[i]) ) count++;
                 if (mnumA[4].equals(mnumA[i]) ) count++;

                 //
                 //   Check teepast2
                 //
                 pstmt = con.prepareStatement (
                    "SELECT mNum1, mNum2, mNum3, mNum4, mNum5, show1, show2, show3, show4, show5 " +
                    "FROM teepast2 " +
                    "WHERE " +
                         "date > ? AND date < ? AND (" +
                         "(mNum1 = ? AND show1 = 1) OR " +
                         "(mNum2 = ? AND show2 = 1) OR " +
                         "(mNum3 = ? AND show3 = 1) OR " +
                         "(mNum4 = ? AND show4 = 1) OR " +
                         "(mNum5 = ? AND show5 = 1))");

                 pstmt.setLong(1, sdate);
                 pstmt.setLong(2, edate);
                 pstmt.setString(3, mnumA[i]);
                 pstmt.setString(4, mnumA[i]);
                 pstmt.setString(5, mnumA[i]);
                 pstmt.setString(6, mnumA[i]);
                 pstmt.setString(7, mnumA[i]);
                 rs = pstmt.executeQuery();      // execute the prepared stmt

                 while (rs.next()) {

                     if (rs.getString("mNum1").equals(mnumA[i]) && rs.getInt("show1") == 1) count++;
                     if (rs.getString("mNum2").equals(mnumA[i]) && rs.getInt("show2") == 1) count++;
                     if (rs.getString("mNum3").equals(mnumA[i]) && rs.getInt("show3") == 1) count++;
                     if (rs.getString("mNum4").equals(mnumA[i]) && rs.getInt("show4") == 1) count++;
                     if (rs.getString("mNum5").equals(mnumA[i]) && rs.getInt("show5") == 1) count++;

                     if (count > max) {                         // if either count puts user over the limit

                         error = true;                          // indicate error
                         break loop1;
                     }

                 } // end while of all matching mNum's

                 rs.close();
                 pstmt.close();


                 //
                 //   Check teecurr2
                 //
                 pstmt = con.prepareStatement (
                    "SELECT mNum1, mNum2, mNum3, mNum4, mNum5, show1, show2, show3, show4, show5 " +
                    "FROM teecurr2 " +
                    "WHERE " +
                         "date > ? AND date < ? AND " +
                         "(mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?) AND " +
                         "teecurr_id != ?");

                 pstmt.setLong(1, sdate);
                 pstmt.setLong(2, edate);
                 pstmt.setString(3, mnumA[i]);
                 pstmt.setString(4, mnumA[i]);
                 pstmt.setString(5, mnumA[i]);
                 pstmt.setString(6, mnumA[i]);
                 pstmt.setString(7, mnumA[i]);
                 pstmt.setInt(8, slotParms.teecurr_id);
                 rs = pstmt.executeQuery();   

                 while (rs.next()) {

                     if (rs.getString("mNum1").equals(mnumA[i]) ) count++;
                     if (rs.getString("mNum2").equals(mnumA[i]) ) count++;
                     if (rs.getString("mNum3").equals(mnumA[i]) ) count++;
                     if (rs.getString("mNum4").equals(mnumA[i]) ) count++;
                     if (rs.getString("mNum5").equals(mnumA[i]) ) count++;

                     if (count > max) {                         // if either count puts user over the limit

                         error = true;                          // indicate error
                         break loop1;
                     }

                 } // end while of all matching mNum's

                 rs.close();
                 pstmt.close();

                 //
                 //   Check evntSup2b
                 //
                 pstmt = con.prepareStatement(
                         "SELECT m.memNum " +
                         "FROM evntsup2b s " +
                         "LEFT OUTER JOIN events2b e ON e.name = s.name " +
                         "LEFT OUTER JOIN member2b m ON (s.username1 = m.username) OR (s.username2 = m.username) OR (s.username3 = m.username) OR (s.username4 = m.username) OR (s.username5 = m.username) " +
                         "WHERE s.moved = 0 AND e.date > ? AND e.date < ? AND m.memNum = ? AND s.id != ?");
                 pstmt.clearParameters();
                 pstmt.setLong(1, sdate);
                 pstmt.setLong(2, edate);
                 pstmt.setString(3, mnumA[i]);
                 pstmt.setInt(4, sid);

                 rs = pstmt.executeQuery();

                 while (rs.next()) {

                     count++;

                     if (count > max) {                         // if over the limit

                         error = true;                          // indicate error
                         break loop1;
                     }
                 }

                 rs.close();
                 pstmt.close();

             }   // end if Assoc B mship

         }  // end of FOR loop (do each player)

     } catch (Exception e) {

         Utilities.logError("Error checking for Naples Assoc B - verifyCustom.checkNaplesAssocBQuota: " + e.getMessage());

     } finally {

         try { rs.close(); }
         catch (Exception ignore) {}

         try { pstmt.close(); }
         catch (Exception ignore) {}

     }
        
  } // end if date in season

  if (error == false) slotParms.player = "";
     
  return error;
 }
 
 
 
/**
 //************************************************************************
 //
 //   checkOceanReefMship - Ocean Reef Club - Dolphin Course (only)
 //                         
 //        Check days in advance based on mship type.
 //
 //   called by:  Member_sheet
 //
 //************************************************************************
 **/

 public static boolean checkOceanReefMship(long date, int index2, int cal_time, String mship) {

     boolean allow = true;  
     
     if (date > 1014 || date < 516) {      // if between Oct 15 and May 15 inclusive
        
        if (mship.equals("Social") || mship.equals("Social Legacy") || mship.equals("Charter") || 
            mship.equals("Charter Legacy") || mship.equals("Multi-Game Card")) {   // days = 3 at 7:00 AM
           
           if (index2 > 3) {
              
              allow = false;
              
           } else if (index2 == 3 && cal_time < 700) {
              
              allow = false;
           }
        
        } else if (mship.equals("Charter w/Trail Pass")) {      // days = 7 at 7:00 AM  
           
           if (index2 > 7) {
              
              allow = false;
              
           } else if (index2 == 7 && cal_time < 700) {
              
              allow = false;
           }
                
        } else if (mship.equals("Patron") || mship.equals("Patron Legacy")) {      // days = 14 at 7:00 AM
           
           if (index2 > 14) {
              
              allow = false;
              
           } else if (index2 == 14 && cal_time < 700) {
              
              allow = false;
           }                     
        }      
     }
     
     return allow;
 }

 public static boolean checkWeeburnWFGGuests(parmSlot slotParms, Connection con) {

     boolean error = false;

     //
     //  break down date of tee time
     //
     long memDay = Hdate1;       // Memorial Day
     long july4th = Hdate2;      // 4th of July
     long laborDay = Hdate3;     // Labor Day

     long yy = slotParms.date / 10000;                             // get year
     long mm = (slotParms.date - (yy * 10000)) / 100;              // get month
     long dd = (slotParms.date - (yy * 10000)) - (mm * 100);       // get day

     int guestCount = 0;

     String [] userA = new String [5];            // array to hold the usernames
     String [] playerA = new String [5];          // array to hold the player's names
     String [] mshipA = new String [5];           // array to hold the players' membership types
     String [] usergA = new String [5];           // array to hold the userg values

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

     mshipA[0] = slotParms.mship1;
     mshipA[1] = slotParms.mship2;
     mshipA[2] = slotParms.mship3;
     mshipA[3] = slotParms.mship4;
     mshipA[4] = slotParms.mship5;

     usergA[0] = slotParms.userg1;
     usergA[1] = slotParms.userg2;
     usergA[2] = slotParms.userg3;
     usergA[3] = slotParms.userg4;
     usergA[4] = slotParms.userg5;

     // Loop through all players and determine if any 'WAITING FOR GOLF' member has guests during restricted times
     loop1:
     for (int i=0; i<5; i++) {

         if (mshipA[i].equalsIgnoreCase("WAITING FOR GOLF") && 
            (slotParms.day.equalsIgnoreCase("Friday") || slotParms.day.equalsIgnoreCase("Saturday") || slotParms.day.equalsIgnoreCase("Sunday") ||
             slotParms.date == memDay || slotParms.date == july4th || slotParms.date == laborDay)) {

             // count guests associated with this member
             guestCount = 0;
             
             for (int j=0; j<5; j++) {
                 if (usergA[j].equals(userA[i])) {
                     guestCount++;
                 }
             }

             if (slotParms.day.equalsIgnoreCase("Saturday") && slotParms.time >= 1200) {
                 if (guestCount > 3) {
                     error = true;
                     slotParms.player = playerA[i];
                     break loop1;
                 }
             } else {
                 if (guestCount > 0) {
                     error = true;
                     slotParms.player = playerA[i];
                     break loop1;
                 }
             }
         }
     }

     return error;
 }      // end checkWeeburnWFGGuests


 /**
  * checkParryparkMship - Checks mship for this username and returns true if mship = 'Annual Pass Member'
  *
  * @param username Username of member to check
  * @param con Connection to club database
  *
  * @return color - True if player slot should be colored, false if not
  */
 public static boolean checkPerryParkMship(String username, Connection con) {

     PreparedStatement pstmt = null;
     ResultSet rs = null;

     boolean color = false;

     try {
         pstmt = con.prepareStatement("SELECT m_ship FROM member2b WHERE username = ?");
         pstmt.clearParameters();
         pstmt.setString(1, username);

         rs = pstmt.executeQuery();

         if (rs.next()) {
             if (rs.getString("m_ship").equals("Annual Pass Member")) {
                 color = true;
             }
         }

         pstmt.close();

     } catch (Exception exc) {
         color = false;
     }

     return color;
 }
 

/**
 //************************************************************************
 //
 //   removeHist (Tamarack) - remove lottery history if member deleted  
 //                           from tee time.
 //
 //
 //   called by:  Member_slot & Proshop_slot
 //
 //************************************************************************
 **/

 public static void removeHist(parmSlot slotParms, Connection con) {


   PreparedStatement pstmt = null;

   int i = 0;

   String [] userA = new String [5];           // array to hold the usernames
   String [] olduserA = new String [5];        // array to hold the old usernames

   userA[0] = slotParms.user1;                 // get the new users
   userA[1] = slotParms.user2;
   userA[2] = slotParms.user3;
   userA[3] = slotParms.user4;
   userA[4] = slotParms.user4;

   olduserA[0] = slotParms.oldUser1;           // get the old users
   olduserA[1] = slotParms.oldUser2;
   olduserA[2] = slotParms.oldUser3;
   olduserA[3] = slotParms.oldUser4;
   olduserA[4] = slotParms.oldUser4;


   try {

      for (i = 0; i < 5; i++) {          // check each player    
         
         // check if member no longer part of tee time
         
         if (!olduserA[i].equals( "" ) && !olduserA[i].equals( userA[0] ) && !olduserA[i].equals( userA[1] ) && 
             !olduserA[i].equals( userA[2] ) && !olduserA[i].equals( userA[3] ) && !olduserA[i].equals( userA[4] )) {       
               
            //
            //  A member has been removed from the tee time - remove any lottery history (weights) for this member
            //                                                on this date (assuming the lottery history was for this time).
            //
            pstmt = con.prepareStatement (
                     "DELETE FROM lassigns5 WHERE username = ? AND date = ?");

            pstmt.clearParameters();
            pstmt.setString(1, olduserA[i]);
            pstmt.setLong(2, slotParms.date);
            pstmt.executeUpdate();

            pstmt.close();
         }
      }              // end of FOR loop (do each player)
        
   } catch (Exception e) {

       Utilities.logError("Error in verifyCustom.removeHist " + e.getMessage());        // log the error message
   
   } finally {

       try { pstmt.close(); }
       catch (Exception ignore) {}

   }
   
 }

}  // end of verifyCustom class
