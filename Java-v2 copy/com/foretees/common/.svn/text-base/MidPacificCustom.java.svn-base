/**************************************************************************************************************
 *   medinahCustom:  This will provide the common methods for processing custom requests for Mid Pacific
 *
 *       called by:  verifyCustom
 *
 *
 *   created:  8/12/2009   Brad K.
 *
 *
 *   last updated:
 *
 *  12/03/13   Changed "year" to refer to Sept 1 - Aug 31 instead of the assumed current calendar year.
 *  12/03/13   Updated ClassH1 to allow for 9 or 18 hole rounds to combine to a total of 24 rounds (18 hole count as 2).
 *   6/17/13   Updated unrestrictedTot_month_classJ from 8 to 16 (4 18 hole rounds to 8).
 *  12/28/12   Updated checkMidPacificMemberClass() to add 'Social Golf Part Ti' to the member class mappings.
 *  12/10/12   The rounds check will now use the year of the tee time, if possible, instead of always using the current year.
 *   9/25/12   Updated custom so all classes have access to all back nine times on Saturday and Sunday.
 *   7/01/11   Updated Category I times (wkendRest1) to run from 6:30 - 9:11.
 *   2/18/11   Changes for class J (Social Golf) members: No longer allowed to book restricted times, weekend restricted times changed to 6:30am-1:30pm
 *  10/22/10   Updated unrestrictedTot_month_classJ from 6 to 8 (3 18 hole rounds to 4).
 *  10/18/10   Removed temprary error message output
 *   8/06/10   Updated restricted times for weekends and added 1 guest rule for spouses during these times.
 *   9/17/09   checkMidPacificRounds will now only return past rounds played under their current membership type
 *   9/14/09   Assorted tweaks/updates
 *   9/10/09   Added missing javadoc documentation
 *
 *
 **************************************************************************************************************
 */


package com.foretees.common;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;


public class MidPacificCustom {

    private static String rev = ProcessConstants.REV;

    //
    //  **NOTE** For definitions of which mships fall under which classes, they can be found in the checkMidPacificMemberClass() method
    //

    // Member Class Restricted Times - restricted time values should be referenced from here if possible to make any future changes simple
    public static int wkdayRest_stime = 1128;         // Start time for weekday restrictions (all mships except class A, Wed/Thurs/Fri only)
    public static int wkdayRest_etime = 1329;         // End time for weekday restrictions (all mships except class A, Wed/Thurs/Fri only)
    public static int wkendRest_classJ_stime = 630;   // Start time for weekend restriction (class J only, Sat/Sun only)
    public static int wkendRest_classJ_etime = 1330;  // End time for weekend restriction (class J only, Sat/Sun only)
    public static int wkendRest1_stime = 630;         // Start time for first weekend restriction (all mships except class A, Sat/Sun only)
    public static int wkendRest1_etime = 911;         // End time for first weekend restriction (all mships except class A, Sat/Sun only)
    public static int wkendRest2_norm_stime = 1201;   // Start time for second weekend restriction (mships without Family Time access, Sat/Sun only)
    public static int wkendRest2_norm_etime = 1300;   // End time for second weekend restriction (mships without Family Time access, Sat/Sun only)
    public static int wkendRest2_fam_stime = 830;     // Start time for second weekend restriction (mships with Family Time access, Sat/Sun only)
    public static int wkendRest2_fam_etime = 1200;    // End time for second weekend restriction (mships with Family Time access, Sat/Sun only)

    // Member Class Round Limits - any round limits should be referenced from here if possible to make any future changes simple
    public static int restricted_month_classB = 1;          // # of allowed rounds per month during restricted times for class B
    public static int restricted_month_classC = 1;          // # of allowed rounds per month during restricted times for class C
    public static int restricted_month_classH3 = 4;         // # of allowed rounds per month during restricted times for class H3
    public static int restricted_month_classJ = 1;          // # of allowed rounds per month during restricted times for class J (no longer allowed any restricted rounds)
    public static int unrestricted_year_classH1 = 24;       // # of allowed rounds per year during unrestricted times for class H1 (18-hole rounds count as 2, 9-hole rounds count as 1)
    public static int unrestrictedTot_month_classH2 = 2;    // # of allowed rounds per month during unrestricted times for class H2 (18-hole rounds count as 2, 9-hole rounds count as 1)
    public static int unrestrictedTot_month_classJ = 16;     // # of allowed rounds per month during unrestricted times for class J (18-hole rounds count as 2, 9-hole rounds count as 1)
    public static int total_month_classH3 = 8;              // total # of allowed rounds (restricted or unrestricted) per month for class H3
    public static int propGuest_classB = 1;                 // # of allowed rounds per month as the guest of a Proprietary member during restricted times for class B
    public static int propGuest_classC = 1;                 // # of allowed rounds per month as the guest of a Proprietary member during restricted times for class C
    public static int propGuest_classJ = 1;                 // # of allowed rounds per month as the guest of a Proprietary member during restricted times for class J
    public static int playerRounds_year_classH3 = 168;      // # of allowed player rounds (their rounds + their designated guests) per year for class H3
    public static int wkendGuestTime_classD = 1445;         // Time after which class D members are allowed to have guests on (Sat/Sun only)

    // Miscellaneous Vars
    public static String gtype_classH3 = "SOCLEG";                  // Guest type used to denote a guest to be counted towards a Social Legacy (H3) member's player round total
    public static String gtype_propGuest = "Guest";                // Guest type used to denote a guest to be counted towards a member's Prop.
    public static String excludedEvent_classC = "Nine and Dine";      // Designated event that will not count towards monthly round total for classC (contains this string in name)



 /**
  * Check various Mid Pacific membership types for each of their associated restrictions.  Call out to a seperate method for each.
  *
  * @param slotParms Parameter block holding details regarding the current tee time
  * @param midPacParms Parameter block holding various counters used for Mid Pacific custom restrictions.  Used to return multiple values for some mships
  * @param con Connection to club database
  *
  * @return - error - True if member is restricted from booking this tee time, False if no problems are found.
  */
 public static boolean checkMidPacificClasses(parmSlot slotParms, parmMidPacific [] midPacParms, Connection con) {

     boolean error = false;
     boolean restricted = false;
     boolean isPropGuest = false;
     boolean pastOnly = false;

     int gcount = 0;        // Guest count
     int roundTotal = 0;    // Total rounds
     int curRoundTotal = 0; // Round count in current tee time
     int fb = 0;            // Front (0) or Back (1) tee time indicator

     String gtype = "";         // Guest type to search for
     String propUser = "";      // Username of Prop. member to be guest of
     String memberClass = "";   // Member class of current member

     String errorMsg_general = "";                      // Generic error message for when there's a problem during round count gathering
     String errorMsg_restrictedRounds = "";             // Error message used when member has hit or exceeded their limit of restricted time rounds this month
     String errorMsg_propGuestRounds = "";              // Error message used when member has hit or exceeded their limit of prop guest rounds this month
     String errorMsg_guestsNotAllowed = "";             // Error message used when member has guests in a time they are not allowed to bring guests
     String errorMsg_restrictedTime = "";               // Error message used when member is not allowed to play at the current time

     String [] playerA = new String[5]; // Array to hold the player names
     String [] userA = new String[5];   // Array to hold the usernames
     String [] mshipA = new String[5];  // Array to hold the membership types

     // Populate arrays
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
     
     fb = slotParms.fb;

     // Populate general error messages
     errorMsg_general = "<br><br><h3>Error Processing Round</h3><br>" +
             "A problem was encountered during the booking process." +
             "<br><br>Please contact the golf shop to book this round or return to the tee sheet.";

     // Loop through users and check restrictions on each
     for (int i=0; i<5; i++) {

         // Reset variables
         restricted = false;
         isPropGuest = false;
         propUser = "";
         memberClass = "";
         gcount = 0;
         roundTotal = 0;
         curRoundTotal = 0;

         // Don't check further players if error is found
         if (!error && !userA[i].equals("")) {

             // Set current player and username
             midPacParms[i].player = playerA[i];
             midPacParms[i].user = userA[i];
             midPacParms[i].mship = mshipA[i];

             // Get current player's member class
             memberClass = checkMidPacificMemberClass(userA[i], mshipA[i], con);

             // Determine number of guests in current tee time for later use
             gtype = "";        // always searching for all guest types here
             gcount = checkMidPacificGuests(userA[i], gtype, slotParms);

             // Populate player specific error messages
             errorMsg_restrictedRounds = "<br><br><h3>Restricted Rounds Limit Reached</h3><br>" +
                     midPacParms[i].player + " may only play <b>1</b> round per month during restricted times.  This tee time would exceed that limit." +
                     "<br><br>Please remove this player or return to the tee sheet.";
             errorMsg_propGuestRounds = "<br><br><h3>Limit of Rounds as Guest of a Proprietary Member Reached</h3><br>" +
                     midPacParms[i].player + " may only play <b>1</b> round per month as the guest of a Proprietary member.  This tee time would exceed that limit." +
                     "<br><br>Please remove this player or return to the tee sheet.";
             errorMsg_guestsNotAllowed = "<br><br><h3>Unauthorized Guests Found</h3><br>" +
                     midPacParms[i].player + " is only allowed to bring guests during unrestricted times.  Since this tee time falls within restricted times, they are not allowed guests." +
                     "<br><br>Please remove any guests associated with this member or return to the tee sheet.";
             errorMsg_restrictedTime = "<br><br><h3>Restricted Player Found</h3><br>" +
                     midPacParms[i].player + " is not allowed to play during restricted times." +
                     "<br><br>Please remove this player or return to the tee sheet.";

             // No membership type is allowed more than 3 guests at a time (they allow 5 somes)
             if (gcount > 3) {

                 error = true;
                 midPacParms[i].errorMsg = "<br><br><h3>Guest Limit Exceeded</h3><br>" +
                         midPacParms[i].player + " is allowed up to 3 guests in a tee time, but currently has " + gcount + " guests entered under their name." +
                         "<br><br>Please ensure that all guests are directly following the member they are to be associated with in the tee time, " +
                         "<br>remove any outstanding guests, or return to the tee sheet.";
             }

             // If still no error, continue
             if (!error) {

                 // Class B mships (restrictions listed in class method documentation)
                 if (memberClass.equalsIgnoreCase("B")) {

                     // Set the time mode and teecurr_id
                     midPacParms[i].time_mode = slotParms.mm;
                     midPacParms[i].teecurr_id = slotParms.teecurr_id;

                     if (checkMidPacificClassB(midPacParms[i], con, pastOnly)) {

                         // There was an error while processing round counts.  Either time_mode not 0-12 or exception thrown during database queries
                         error = true;
                         midPacParms[i].errorMsg = errorMsg_general;

                     } else {

                         // Check if this tee time is during restricted or unrestricted times
                         restricted = checkMidPacificTimes("B", slotParms.day, slotParms.time, fb);

                         // If during restricted times, no guests allowed for this member
                         if (restricted && gcount > 0) {
                             error = true;
                             midPacParms[i].errorMsg = errorMsg_guestsNotAllowed;
                         }

                         // Don't bother checking the rest if error present already
                         if (!error) {

                             // If restricted and member has already used their restricted tee time, see if they're set up to play as Prop. guest in the current round
                             if (restricted && midPacParms[i].res_month >= restricted_month_classB) {

                                 propUser = checkMidPacificIsPropGuest(slotParms, midPacParms, userA[i]);

                                 if (!propUser.equals("")) {
                                     isPropGuest = true;
                                     midPacParms[i].propUser = propUser;
                                 }
                             }

                             if (isPropGuest && midPacParms[i].propGuestRounds < propGuest_classB) {

                                 // Is set up to be booked as the guest of a Prop member, and hasn't played any such rounds already this month
                                 midPacParms[i].bookAsPropGuest = true;

                             } else if (isPropGuest && midPacParms[i].propGuestRounds >= propGuest_classB) {

                                 // Is set up to be booked as the guest of a Prop member, but has already played their limit of Prop. guest rounds this month
                                 error = true;
                                 midPacParms[i].errorMsg = errorMsg_propGuestRounds;

                             } else if (!isPropGuest && restricted && midPacParms[i].res_month < restricted_month_classB) {

                                 // Is ok to book this restricted time, but not eligable to be a Prop guest this round
                                 midPacParms[i].bookAsPropGuest = false;

                             } else if (!isPropGuest && restricted && midPacParms[i].res_month >= restricted_month_classB) {

                                 // Is not eligable to be a Prop. guest this round, and has already played their limit of restricted rounds this month
                                 error = true;
                                 midPacParms[i].errorMsg = errorMsg_restrictedRounds;
                             }
                         }
                     }

                 } else if (memberClass.equalsIgnoreCase("C")) { // Class C mships (restrictions listed in class method documentation)

                     // Set the time mode and teecurr_id
                     midPacParms[i].time_mode = slotParms.mm;
                     midPacParms[i].teecurr_id = slotParms.teecurr_id;

                     if (checkMidPacificClassC(midPacParms[i], con, pastOnly)) {

                         // There was an error while processing round counts.  Either time_mode not 0-12 or exception thrown during database queries
                         error = true;
                         midPacParms[i].errorMsg = errorMsg_general;

                     } else {

                         // Check if this tee time is during restricted or unrestricted times
                         restricted = checkMidPacificTimes("C", slotParms.day, slotParms.time, fb);

                         // If during restricted times, no guests allowed for this member
                         if (restricted && gcount > 0) {
                             error = true;
                             midPacParms[i].errorMsg = errorMsg_guestsNotAllowed;
                         }

                         // Don't bother checking the rest if error present already
                         if (!error) {

                             // If restricted and member has already used their restricted tee time, see if they're set up to play as Prop. guest in the current round
                             if (restricted && midPacParms[i].res_month >= restricted_month_classC) {

                                 propUser = checkMidPacificIsPropGuest(slotParms, midPacParms, userA[i]);

                                 if (!propUser.equals("")) {
                                     isPropGuest = true;
                                     midPacParms[i].propUser = propUser;
                                 }
                             }

                             if (isPropGuest && midPacParms[i].propGuestRounds < propGuest_classC) {

                                 // Is set up to be booked as the guest of a Prop member, and hasn't played any such rounds already this month
                                 midPacParms[i].bookAsPropGuest = true;

                             } else if (isPropGuest && midPacParms[i].propGuestRounds >= propGuest_classC) {

                                 // Is set up to be booked as the guest of a Prop member, but has already played their limit of Prop. guest rounds this month
                                 error = true;
                                 midPacParms[i].errorMsg = errorMsg_propGuestRounds;

                             } else if (!isPropGuest && restricted && midPacParms[i].res_month < restricted_month_classC) {

                                 // Is ok to book this restricted time, but not eligable to be a Prop guest this round
                                 midPacParms[i].bookAsPropGuest = false;

                             } else if (!isPropGuest && restricted && midPacParms[i].res_month >= restricted_month_classC) {

                                 // Is not eligable to be a Prop. guest this round, and has already played their limit of restricted rounds this month
                                 error = true;
                                 midPacParms[i].errorMsg = errorMsg_restrictedRounds;
                             }
                         }
                     }

                 } else if (memberClass.equalsIgnoreCase("D")) { // Class D mships (restrictions listed in class method documentation)

                     // No guests allowed on Sat/Sun until after 2:45pm
                     if ((slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday")) && userA[i].endsWith("-1") && 
                             slotParms.time > wkendRest2_fam_stime && slotParms.time < wkendRest2_fam_etime && gcount > 1) {
                         error = true;
                         midPacParms[i].errorMsg = "<br><br><h3>Unauthorized Guests Found</h3><br>" +
                                 midPacParms[i].player + " is only allowed to bring one guest at this time." +
                                 "<br><br>Please remove any excess guests associated with this member or return to the tee sheet.";
                     }

                 } else if (memberClass.equalsIgnoreCase("H1")) { // Class H1 mship (restrictions listed in class method documentation)

                     // Set the time mode and teecurr_id
                     midPacParms[i].time_mode = 0;
                     midPacParms[i].teecurr_id = slotParms.teecurr_id;

                     if (checkMidPacificClassH1(midPacParms[i], con, pastOnly)) {

                         // There was an error while processing round counts.  Either time_mode not 0-12 or exception thrown during database queries
                         error = true;
                         midPacParms[i].errorMsg = errorMsg_general;

                     } else {
                         
                         // Check if this tee time is during restricted or unrestricted times
                         restricted = checkMidPacificTimes("H1", slotParms.day, slotParms.time, fb);

                         // If during restricted times, no guests allowed for this member
                         if (restricted) {
                             error = true;
                             midPacParms[i].errorMsg = errorMsg_restrictedTime;
                         }
                         
                         // Don't bother checking the rest if error present already
                         if (!error) {

                             // Count round total for this year (18-hole count for 2, 9-hole count for 1)
                             roundTotal = (midPacParms[i].rounds18_year * 2) + midPacParms[i].rounds9_year;

                             curRoundTotal = checkMidPacificCurRoundTotal(userA[i], slotParms);

Utilities.logDebug("BSK", "TEST midpac roundTotal: " + roundTotal + " (" + midPacParms[i].rounds18_year + " * 2 + " + midPacParms[i].rounds9_year + "), curRoundTotal: " + curRoundTotal);                             
                             if ((roundTotal + curRoundTotal) > unrestricted_year_classH1) {

                                 // Member has gone over their round quota for this year
                                 error = true;
                                 midPacParms[i].errorMsg = "<br><br><h3>Round Limit Exceeded</h3><br>" +
                                         midPacParms[i].player + " is only allowed to play " + (unrestricted_year_classH1 / 2) + " 18-hole round(s), or " + unrestricted_year_classH1 + " 9-hole round(s) per year.  "
                                         + "This tee time would exceed that limit, and they must contact the golf shop to purchase additional rounds." +
                                         "<br><br>Please remove this player or return to the tee sheet.";
                             }
                         }
                     }

                 } else if (memberClass.equalsIgnoreCase("H2")) { // Class H2 mship (restrictions listed in class method documentation)

                     // Set the time mode and teecurr_id
                     midPacParms[i].time_mode = slotParms.mm;
                     midPacParms[i].teecurr_id = slotParms.teecurr_id;

                     if (checkMidPacificClassH2(midPacParms[i], con, pastOnly)) {

                         // There was an error while processing round counts.  Either time_mode not 0-12 or exception thrown during database queries
                         error = true;
                         midPacParms[i].errorMsg = errorMsg_general;

                     } else {

                         // Check if this tee time is during restricted or unrestricted times
                         restricted = checkMidPacificTimes("H2", slotParms.day, slotParms.time, fb);

                         // If during restricted times, not allowed to play!
                         if (restricted) {
                             error = true;
                             midPacParms[i].errorMsg = errorMsg_restrictedTime;
                         }

                         // Don't bother checking the rest if error present already
                         if (!error) {

                             // Count round total for this month (18-hole count for 2, 9-hole count for 1)
                             roundTotal = (midPacParms[i].rounds18_month * 2) + midPacParms[i].rounds9_month;

                             curRoundTotal = checkMidPacificCurRoundTotal(userA[i], slotParms);

                             if (roundTotal > (unrestrictedTot_month_classH2 - curRoundTotal)) {

                                 // Member has gone over their round quota for this month
                                 error = true;
                                 midPacParms[i].errorMsg = "<br><br><h3>Round Limit Exceeded</h3><br>" +
                                         midPacParms[i].player + " is only allowed to play " + (unrestrictedTot_month_classH2 / 2) + " 18-hole round(s), or " + unrestrictedTot_month_classH2 + " 9-hole round(s) per month.  This tee time would exceed that limit." +
                                         "<br><br>Please remove this player or return to the tee sheet.";
                             }
                         }
                     }

                 } else if (memberClass.equalsIgnoreCase("H3")) { // Class H3 mship (restrictions listed in class method documentation)

                     // Set the time mode and teecurr_id
                     midPacParms[i].time_mode = slotParms.mm;
                     midPacParms[i].teecurr_id = slotParms.teecurr_id;

                     boolean includeTotalRounds = true;

                     if (checkMidPacificClassH3(midPacParms[i], con, includeTotalRounds, pastOnly)) {

                         // There was an error while processing round counts.  Either time_mode not 0-12 or exception thrown during database queries
                         error = true;
                         midPacParms[i].errorMsg = errorMsg_general;

                     } else {

                         // Check if this tee time is during restricted or unrestricted times
                         restricted = checkMidPacificTimes("H3", slotParms.day, slotParms.time, fb);

                         // See how many relevent guests they have in this tee time
                         gcount = checkMidPacificGuests(userA[i], gtype_classH3, slotParms);

                         // Check to see if this member has enough player rounds left this year to book the current tee time
                         if (midPacParms[i].playerRounds_year > (playerRounds_year_classH3 - (gcount + 1))) {

                             error = true;
                             midPacParms[i].errorMsg = "<br><br><h3>Round Limit Exceeded</h3><br>" +
                                     midPacParms[i].player + " is only allowed " + playerRounds_year_classH3 + " player rounds per year, and has already played or scheduled " + midPacParms[i].playerRounds_year + " player rounds this year.  This round contains " + (gcount + 1) + " player round(s), which would exceed that limit." +
                                     "<br><br>Please remove " + gtype_classH3 + " guests of " + midPacParms[i].player + " from this tee time, as well as the player themself, as needed, or return to the tee sheet.";

                         } else if (restricted && midPacParms[i].res_month >= restricted_month_classH3) {

                             error = true;
                             midPacParms[i].errorMsg = "<br><br><h3>Tee Time Limit Exceeded</h3><br>" +
                                     midPacParms[i].player + " is only allowed 4 tee times during restricted times per month.  This tee time would exceed that limit." +
                                     "<br><br>Please remove this player and any guests associated with them from this tee time, or return to the tee sheet.";

                         } else if ((midPacParms[i].res_month + midPacParms[i].non_month) >= total_month_classH3) {

                             error = true;
                             midPacParms[i].errorMsg = "<br><br><h3>Tee Time Limit Exceeded</h3><br>" +
                                     midPacParms[i].player + " is only allowed 8 tee times total per month.  This tee time would exceed that limit." +
                                     "<br><br>Please remove this player and any guests associated with them from this tee time, or return to the tee sheet.";
                         }



                         // Don't bother checking the rest if error present already
                         if (!error) {

                         }
                     }

                 } else if (memberClass.equalsIgnoreCase("J")) { // Class J mship (restrictions listed in class method documentation)

                     // Set the time mode and teecurr_id
                     midPacParms[i].time_mode = slotParms.mm;
                     midPacParms[i].teecurr_id = slotParms.teecurr_id;

                     if (checkMidPacificClassJ(midPacParms[i], con, pastOnly)) {

                         // There was an error while processing round counts.  Either time_mode not 0-12 or exception thrown during database queries
                         error = true;
                         midPacParms[i].errorMsg = errorMsg_general;

                     } else {

                         // Check if this tee time is during restricted or unrestricted times
                         restricted = checkMidPacificTimes("J", slotParms.day, slotParms.time, fb);

                         // If during restricted times, no guests allowed for this member
                         if (restricted && gcount > 0) {
                             error = true;
                             midPacParms[i].errorMsg = errorMsg_guestsNotAllowed;
                         }

                         // Don't bother checking the rest if error present already
                         if (!error) {

                             if (!restricted) {
                                 // Count round total for this month (18-hole count for 2, 9-hole count for 1)
                                 roundTotal = (midPacParms[i].rounds18_month * 2) + midPacParms[i].rounds9_month;

                                 curRoundTotal = checkMidPacificCurRoundTotal(userA[i], slotParms);

                                 if (roundTotal > (unrestrictedTot_month_classJ - curRoundTotal)) {

                                     // Member has gone over their round quota for this month
                                     error = true;
                                     midPacParms[i].errorMsg = "<br><br><h3>Round Limit Exceeded</h3><br>" +
                                             midPacParms[i].player + " is only allowed to play " + (unrestrictedTot_month_classJ / 2) + " 18-hole round(s), or " + unrestrictedTot_month_classJ + " 9-hole round(s) per month.  This tee time would exceed that limit." +
                                             "<br><br>Please remove this player or return to the tee sheet.";
                                 }
                             } else {

                                 // If restricted and member has already used their restricted tee time, see if they're set up to play as Prop. guest in the current round
                                 if (restricted) {

                                     propUser = checkMidPacificIsPropGuest(slotParms, midPacParms, userA[i]);

                                     if (!propUser.equals("")) {
                                         isPropGuest = true;
                                         midPacParms[i].propUser = propUser;
                                     }
                                 }

                                 if (isPropGuest && midPacParms[i].propGuestRounds < propGuest_classJ) {

                                     // Is set up to be booked as the guest of a Prop member, and hasn't played any such rounds already this month
                                     midPacParms[i].bookAsPropGuest = true;

                                 } else if (isPropGuest && midPacParms[i].propGuestRounds >= propGuest_classJ) {

                                     // Is set up to be booked as the guest of a Prop member, but has already played their limit of Prop. guest rounds this month
                                     error = true;
                                     midPacParms[i].errorMsg = errorMsg_propGuestRounds;

                                 } else if (!isPropGuest && restricted) {

                                     // Is not eligable to be a Prop. guest this round, and has already played their limit of restricted rounds this month
                                     error = true;
                                     midPacParms[i].errorMsg = errorMsg_restrictedTime;
                                 }
                             }
                         }
                     }
                 } else if (memberClass.equalsIgnoreCase("A")) {

                     // Only 1 guest allowed Sat/Sun mornings
                     if ((slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday")) && userA[i].endsWith("-1") &&
                             slotParms.time > wkendRest2_fam_stime && slotParms.time < wkendRest2_fam_etime && gcount > 1) {
                         error = true;
                         midPacParms[i].errorMsg = "<br><br><h3>Unauthorized Guests Found</h3><br>" +
                                 midPacParms[i].player + " is only allowed to bring one guest at this time." +
                                 "<br><br>Please remove any excess guests associated with this member or return to the tee sheet.";
                     }


                 } else if (memberClass.equals("") || !memberClass.equalsIgnoreCase("A")) {
                     error = true;
                     midPacParms[i].errorMsg = "<br><br><h3>Unknown Membership Type Found</h3><br>" +
                             midPacParms[i].player + " does not have a recognized membership type and will need to contact the golf shop to resolve the situation." +
                             "<br><br>Please remove this player or return to the tee sheet.";
                 }
             }
         }
     }

     return error;
 }

 /**
  * checkMidPacificMemberClass - Returns the member class of the passed user
  *
  * @param user username to check the member class of
  * @param mship_in mship of user, if passed (if blank, will look up in database)
  * @param con connection to club database
  *
  * @return memberClass - member class of the passed user
  */
 public static String checkMidPacificMemberClass(String user, String mship_in, Connection con) {

     PreparedStatement pstmt = null;
     ResultSet rs = null;

     String memberClass = "";
     String mship = "";

     // Use passed mship if not blank, otherwise look up in database
     if (!mship_in.equals("")) {

         mship = mship_in;

     } else {

         try {
             pstmt = con.prepareStatement("SELECT m_ship FROM member2b WHERE username = ?");
             pstmt.clearParameters();
             pstmt.setString(1, user);

             rs = pstmt.executeQuery();

             if (rs.next()) {
                 mship = rs.getString("m_ship");
             }

             pstmt.close();

         } catch (Exception exc) {
             memberClass = "";
         }
     }

     if (mship.equalsIgnoreCase("Proprietary") || mship.equalsIgnoreCase("Proprietary-Certificate") || mship.equalsIgnoreCase("Proprietary Life") ||
         mship.equalsIgnoreCase("Prop Trial") || mship.equalsIgnoreCase("Non-Resident") || mship.equalsIgnoreCase("Business-Sponsored") ||
         mship.equalsIgnoreCase("Bus Spon Non-Res")) {
         memberClass = "A";
     } else if (mship.equalsIgnoreCase("Limited Golf") || mship.equalsIgnoreCase("Legacy Intermediate/18-40") || mship.equalsIgnoreCase("Regular Intermediate/18-40")) {
         memberClass = "B";
     } else if (mship.equalsIgnoreCase("Senior") || mship.equalsIgnoreCase("Senior/Legacy")) {
         memberClass = "C";
     } else if (mship.equalsIgnoreCase("Surviving Spouse") || mship.equalsIgnoreCase("Lady")) {
         memberClass = "D";
     } else if (mship.equalsIgnoreCase("Social Non-Res")) {
         memberClass = "H1";
     } else if (mship.equalsIgnoreCase("Social") || mship.equalsIgnoreCase("Social Trial")) {
         memberClass = "H2";
     } else if (mship.equalsIgnoreCase("Social/Legacy")) {
         memberClass = "H3";
     } else if (mship.equalsIgnoreCase("Social Golf") || mship.equalsIgnoreCase("Social Golf Part Ti")) {
         memberClass = "J";
     }

     return memberClass;
 }

 /**
  * Checks guests in current tee time but assigns them 1 round for a 9-hole guest round, and 2 for an 18-hole guest round
  *
  * @param user Member to check guests for
  * @param slotParms Parameter block holding details for the current tee time
  *
  * @return curRoundTotal - Contains the current number of tallied rounds for this tee time (9-hole = 1, 18-hole = 2)
  */
 public static int checkMidPacificCurRoundTotal(String user, parmSlot slotParms) {

     int curRoundTotal = 0;

     if (slotParms.user1.equals(user) || slotParms.userg1.equals(user)) {
         if (slotParms.p91 == 1) {
             curRoundTotal += 1;
         } else {
             curRoundTotal += 2;
         }
     }
     if (slotParms.user2.equals(user) || slotParms.userg2.equals(user)) {
         if (slotParms.p92 == 1) {
             curRoundTotal += 1;
         } else {
             curRoundTotal += 2;
         }
     }
     if (slotParms.user3.equals(user) || slotParms.userg3.equals(user)) {
         if (slotParms.p93 == 1) {
             curRoundTotal += 1;
         } else {
             curRoundTotal += 2;
         }
     }
     if (slotParms.user4.equals(user) || slotParms.userg4.equals(user)) {
         if (slotParms.p94 == 1) {
             curRoundTotal += 1;
         } else {
             curRoundTotal += 2;
         }
     }
     if (slotParms.user5.equals(user) || slotParms.userg5.equals(user)) {
         if (slotParms.p95 == 1) {
             curRoundTotal += 1;
         } else {
             curRoundTotal += 2;
         }
     }

     return curRoundTotal;
 }

 /**
  * Count the number of guests associated with this user in the current tee time and return the count
  *
  * @param user Username to check # of guests for
  * @param gtype Guest type to count - pass blank string if irrelevent
  * @param slotParms Parameter block for current tee time
  *
  * @return count - # of guests tied to the current user
  */
 public static int checkMidPacificGuests(String user, String gtype, parmSlot slotParms) {

     int count = 0;

     if (gtype.equals("")) {
         if (slotParms.userg1.equals(user)) count++;
         if (slotParms.userg2.equals(user)) count++;
         if (slotParms.userg3.equals(user)) count++;
         if (slotParms.userg4.equals(user)) count++;
         if (slotParms.userg5.equals(user)) count++;
     } else {
         if (slotParms.userg1.equals(user) && slotParms.player1.startsWith(gtype)) count++;
         if (slotParms.userg2.equals(user) && slotParms.player2.startsWith(gtype)) count++;
         if (slotParms.userg3.equals(user) && slotParms.player3.startsWith(gtype)) count++;
         if (slotParms.userg4.equals(user) && slotParms.player4.startsWith(gtype)) count++;
         if (slotParms.userg5.equals(user) && slotParms.player5.startsWith(gtype)) count++;
     }

     return count;
 }

 /**
  * Checks to see if the given member is possibly entered as a guest of a Proprietary member in the current tee time.  If so, return the username of the Prop. member
  * they are set up to be the guest of
  *
  * @param slotParms Parameter block for the current tee time
  * @param user Username of the current member
  *
  * @return isPropGuest - Contains the username of the Prop. member this member is set up to be the possible guest of, otherwise returns a blank String
  */
 public static String checkMidPacificIsPropGuest(parmSlot slotParms, parmMidPacific [] midPacParms, String user) {

     String isPropGuest = "";

     if (!slotParms.user1.equals("") && slotParms.mship1.startsWith("Prop") &&
             (slotParms.user2.equals(user) ||
             ((slotParms.user2.equals("") || midPacParms[1].bookAsPropGuest) && slotParms.user3.equals(user)) ||
             ((slotParms.user2.equals("") || midPacParms[1].bookAsPropGuest) && (slotParms.user3.equals("") || midPacParms[2].bookAsPropGuest) && slotParms.user4.equals(user)) ||
             ((slotParms.user2.equals("") || midPacParms[1].bookAsPropGuest) && (slotParms.user3.equals("") || midPacParms[2].bookAsPropGuest) && (slotParms.user4.equals("") || midPacParms[3].bookAsPropGuest) && slotParms.user5.equals(user)))) {

         isPropGuest = slotParms.user1;

     } else if (!slotParms.user2.equals("") && slotParms.mship2.startsWith("Prop") &&
             (slotParms.user3.equals(user) ||
             ((slotParms.user3.equals("") || midPacParms[2].bookAsPropGuest) && slotParms.user4.equals(user)) ||
             ((slotParms.user3.equals("") || midPacParms[2].bookAsPropGuest) && (slotParms.user4.equals("") || midPacParms[3].bookAsPropGuest) && slotParms.user5.equals(user)))) {

         isPropGuest = slotParms.user2;

     } else if (!slotParms.user3.equals("") && slotParms.mship3.startsWith("Prop") &&
             (slotParms.user4.equals(user) ||
             ((slotParms.user4.equals("") || midPacParms[3].bookAsPropGuest) && slotParms.user5.equals(user)))) {

         isPropGuest = slotParms.user3;

     } else if (!slotParms.user4.equals("") && slotParms.mship4.startsWith("Prop") &&
             (slotParms.user5.equals(user))) {

         isPropGuest = slotParms.user4;
     }

     return isPropGuest;
 }

/**
 * Checks whether a given date/time falls within restricted tee times for the given member class.  Returns true if restricted, false if unrestricted
 *
 * @param memberClass Member class to check restricted times for
 * @param day_name Name of day to check
 * @param tee_time Time of day to check
 *
 * @return restricted - True if time is during restricted times for this member class, false if unrestricted
 */
public static boolean checkMidPacificTimes(String memberClass, String day_name, int tee_time, int fb) {

    boolean restricted = false;

    if (memberClass.equalsIgnoreCase("B") || memberClass.equalsIgnoreCase("C") || memberClass.equalsIgnoreCase("H1") ||
        memberClass.equalsIgnoreCase("H2") || memberClass.equalsIgnoreCase("H3")) {

         if (((day_name.equalsIgnoreCase("Wednesday") || day_name.equalsIgnoreCase("Thursday") || day_name.equalsIgnoreCase("Friday")) &&
                 tee_time >= wkdayRest_stime && tee_time <= wkdayRest_etime) ||
                 ((day_name.equalsIgnoreCase("Saturday") || day_name.equalsIgnoreCase("Sunday")) && fb == 0 && 
                 ((tee_time >= wkendRest1_stime && tee_time <= wkendRest1_etime) ||
                 (tee_time >= wkendRest2_norm_stime && tee_time <= wkendRest2_norm_etime)))) {

             restricted = true;
         }
    } else if (memberClass.equalsIgnoreCase("D")) {

         if (((day_name.equalsIgnoreCase("Wednesday") || day_name.equalsIgnoreCase("Thursday") || day_name.equalsIgnoreCase("Friday")) &&
                 tee_time >= wkdayRest_stime && tee_time <= wkdayRest_etime) ||
                 ((day_name.equalsIgnoreCase("Saturday") || day_name.equalsIgnoreCase("Sunday")) && fb == 0 &&
                 ((tee_time >= wkendRest1_stime && tee_time <= wkendRest1_etime) ||
                 (tee_time >= wkendRest2_fam_stime && tee_time <= wkendRest2_fam_etime)))) {

             restricted = true;
         }
    } else if (memberClass.equalsIgnoreCase("J")) {
        
         if (((day_name.equalsIgnoreCase("Wednesday") || day_name.equalsIgnoreCase("Thursday") || day_name.equalsIgnoreCase("Friday")) &&
                 tee_time >= wkdayRest_stime && tee_time <= wkdayRest_etime) ||
                 ((day_name.equalsIgnoreCase("Saturday") || day_name.equalsIgnoreCase("Sunday")) && fb == 0 &&
                 tee_time >= wkendRest_classJ_stime && tee_time <= wkendRest_classJ_etime)) {

             restricted = true;
         }
    }

    return restricted;
}

 /**
  * Searches teecurr and teepast to count rounds played by a specific user.  Search parameters based on indicated search timeframe (month, year), 9 or 18 hole rounds, and whether or not the tee time is during restricted or unrestricted play
  *
  * @param user username to get round count for
  * @param mship membership type to get past rounds for.
  * @param teecurr_id teecurr_id of current tee time, so it can be ignored during database queries.  Pass -1 for teecurr_id if not calling method from tee time verification
  * @param time_mode timeframe to search within. Pass 0 to search current year, and 1-12 for a specific month within the current year. (use -1 for last year? add later maybe)
  * @param p9 Number of holes in round: 0 = 18 holes, 1 = 9 holes
  * @param restricted Whether or not to search for rounds in restricted times (for specified class) or unrestricted times
  * @param checkPropGuests Whether or not we're looking for Proprietary Guest rounds
  * @param pastOnly Whether or not to search only past times (most likely used when checking for additional pos charges, not when booking)
  * @param memberClass Member class letter (A, B, C, D, H1, H2, H3, J) used to determine what times are restricted/unrestricted, and, if class 'C', to ignore rounds from a particular event
  * @param con Connection to club database
  *
  * @return count - # of rounds played under the given conditions
  */
 public static int checkMidPacificRounds(String user, String mship, int teecurr_id, int time_mode, boolean p9, boolean restricted, boolean checkPropGuests, boolean countGuests, boolean pastOnly, String memberClass, Connection con) {

     PreparedStatement pstmt = null;
     ResultSet rs = null;

     boolean skip = false;      // If set to true, return -1 to indicate error

     int count = 0;
     int timeframe_val_1 = 0;
     int timeframe_val_2 = 0;
     int p9Check = -1;         // Holds whether tee time is 9 or 18 holes, used in userCheck

     String dataToGet = "";         // Query component for what to return from database (based on if we just need a simple count or a player listing)
     String userCheck = "";         // Query component to check username & 9/18 holes
     String pastUserCheck = "";     // Query component to limit returned past rounds to only those under their current membership type
     String timeframeCheck = "";    // Query component to check current year or specific month
     String teecurrIdCheck = "";    // Query component to ignore the current teecurr_id in the search
     String timeCheck = "";         // Query component to check time of day restrictions
     String eventCheck = "";        // Query component to check if time is for a certain event (Class C only!!)


     // Determine what we need to return from the database
     if (countGuests) {
         // Need a listing of players/guests for the count
         dataToGet = "username1, username2, username3, username4, username5, player1, player2, player3, player4, player5, " +
                     "userg1, userg2, userg3, userg4, userg5, p91, p92, p93, p94, p95 ";
     } else {
         // Just need a tee time count
         dataToGet = "count(*) ";
     }

     // Determine if we're checking for Proprietary Guest rounds, specificly 18 or 9 hole rounds, or just any rounds
     if (checkPropGuests) {

         userCheck = "(custom_disp1 = ? OR custom_disp2 = ? OR custom_disp3 = ? OR custom_disp4 = ? OR custom_disp5 = ?) ";
         pastUserCheck = "((custom_disp1 = ? AND mship1 = ?) OR (custom_disp2 = ? AND mship2 = ?) OR (custom_disp3 = ? AND mship3 = ?) OR " +
                          "(custom_disp4 = ? AND mship4 = ?) OR (custom_disp5 = ? AND mship5 = ?)) ";

     } else {       // If member class H!, H2 or J, may need to search for ONLY 18 hole rounds, for others, not important

         if (p9 || (memberClass.equals("H1") || memberClass.equals("H2") || memberClass.equals("J"))) {
             if (p9) {
                 p9Check = 1;
             } else {
                 p9Check = 0;
             }
         }

         userCheck = "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?) ";
         pastUserCheck = "((username1 = ? AND mship1 = ?) OR (username2 = ? AND mship2 = ?) OR (username3 = ? AND mship3 = ?) OR " +
                          "(username4 = ? AND mship4 = ?) OR (username5 = ? AND mship5 = ?)) ";
     }


     // Get the current year and use as default
     Calendar cal = new GregorianCalendar();
     int year = cal.get(Calendar.YEAR);
     int month = 0;

     // Calculate the timeframe to search within:
     //   -If time_mode is 0, search year of tee time being booked
     //   -If time_mode is 1-12 search specified month
     if (time_mode == 0) {

         // If teecurr_id is postiive, we're coming from a tee time booking and need to check the year of the tee time
         if (teecurr_id > 0) {
             
             try {

                 // Look up the year of the tee time being booked to use when looking up round counts
                 pstmt = con.prepareStatement("SELECT mm, yy FROM teecurr2 WHERE teecurr_id = ?");
                 pstmt.clearParameters();
                 pstmt.setInt(1, teecurr_id);

                 rs = pstmt.executeQuery();

                 if (rs.next()) {
                     month = rs.getInt("mm");
                     year = rs.getInt("yy");
                 }


             } catch (Exception exc) {
                 Utilities.logError("MidPacificCustom.checkMidPacificRounds - midpacific - Error looking up date of tee time - ERR: " + exc.toString());
             } finally {

                 try { rs.close(); }
                 catch (Exception ignore) {}

                 try { pstmt.close(); }
                 catch (Exception ignore) {}
             }
         }
         
         if (month >= 9) {
             timeframe_val_1 = (year * 10000) + 901;
             timeframe_val_2 = ((year + 1) * 10000) + 831;
         } else {
             timeframe_val_1 = ((year - 1) * 10000) + 901;
             timeframe_val_2 = (year * 10000) + 831;
         }      

         // Populate timeframe check for use in query later
         timeframeCheck = "AND date >= ? AND date <= ? ";

     } else if (time_mode > 0 && time_mode <= 12) {

         // Set timeframe for later
         timeframe_val_1 = time_mode;
         timeframe_val_2 = year;

         // Populate timeframe check for use in query later
         timeframeCheck = "AND mm = ? AND yy = ? ";

     } else {

         skip = true;
         count = -1;
     }

     // Populate timeCheck variable
     if (!memberClass.equals("A")) {
         if (restricted) {
             timeCheck = "AND ((day = 'Monday' OR day = 'Tuesday') OR " +
                     "((day = 'Wednesday' OR day = 'Thursday' OR day = 'Friday') AND (time >= ? AND time <= ?)) OR " +
                     "((day = 'Saturday' OR day = 'Sunday') AND ((time >= ? AND time <= ?) OR (time >= ? AND time <= ?)))) ";
         } else {

             timeCheck = "AND ((day = 'Monday' OR day = 'Tuesday') OR " +
                     "((day = 'Wednesday' OR day = 'Thursday' OR day = 'Friday') AND (time < ? OR time > ?)) OR " +
                     "((day = 'Saturday' OR day = 'Sunday') AND (time < ? OR (time > ? AND time < ?) OR time > ?))) ";
         }
     }

     // Populate teecurrIdCheck variable
     if (teecurr_id >= 0) {

         teecurrIdCheck = "AND teecurr_id != ? ";
     }

     if (memberClass.equals("C")) {
         eventCheck = "AND event not like '%" + excludedEvent_classC + "%' ";
     }

     if (!skip) {
         try {

             //
             //  First check current tee times (skip if pastOnly is true)
             //
             if (!pastOnly) {
                 pstmt = con.prepareStatement("SELECT " +
                         dataToGet +
                         "FROM teecurr2 WHERE " +
                         userCheck +
                         timeframeCheck +
                         timeCheck +
                         eventCheck +
                         teecurrIdCheck);

                 pstmt.clearParameters();
                 pstmt.setString(1, user);
                 pstmt.setString(2, user);
                 pstmt.setString(3, user);
                 pstmt.setString(4, user);
                 pstmt.setString(5, user);
                 pstmt.setInt(6, timeframe_val_1);
                 pstmt.setInt(7, timeframe_val_2);

                 if (!memberClass.equals("A")) {

                     pstmt.setInt(8, wkdayRest_stime);
                     pstmt.setInt(9, wkdayRest_etime);
                     pstmt.setInt(10, wkendRest1_stime);
                     pstmt.setInt(11, wkendRest1_etime);

                     // Use family time restriction values for class D only
                     if (memberClass.equals("D")) {
                         pstmt.setInt(12, wkendRest2_fam_stime);
                         pstmt.setInt(13, wkendRest2_fam_etime);
                     } else {
                         pstmt.setInt(12, wkendRest2_norm_stime);
                         pstmt.setInt(13, wkendRest2_norm_etime);
                     }
                 }

                 if (teecurr_id >= 0) {

                     if (memberClass.equals("A")) {
                         pstmt.setInt(8, teecurr_id);
                     } else {
                         pstmt.setInt(14, teecurr_id);
                     }
                 }

                 rs = pstmt.executeQuery();

                 if (countGuests) {

                     // Increment count for each member and guest of this member in the tee time
                     if (memberClass.equalsIgnoreCase("H3")) {
                         // For class H3, make sure to only count the related guest type
                         while (rs.next()) {
                             if (rs.getString("username1").equals(user) || (rs.getString("userg1").equals(user) && rs.getString("player1").startsWith(gtype_classH3))) count++;
                             if (rs.getString("username2").equals(user) || (rs.getString("userg2").equals(user) && rs.getString("player2").startsWith(gtype_classH3))) count++;
                             if (rs.getString("username3").equals(user) || (rs.getString("userg3").equals(user) && rs.getString("player3").startsWith(gtype_classH3))) count++;
                             if (rs.getString("username4").equals(user) || (rs.getString("userg4").equals(user) && rs.getString("player4").startsWith(gtype_classH3))) count++;
                             if (rs.getString("username5").equals(user) || (rs.getString("userg5").equals(user) && rs.getString("player5").startsWith(gtype_classH3))) count++;
                         }
                     } else if (p9Check >= 0) {
                         while (rs.next()) {
                             if ((rs.getString("username1").equals(user) || rs.getString("userg1").equals(user)) && rs.getInt("p91") == p9Check) count++;
                             if ((rs.getString("username2").equals(user) || rs.getString("userg2").equals(user)) && rs.getInt("p92") == p9Check) count++;
                             if ((rs.getString("username3").equals(user) || rs.getString("userg3").equals(user)) && rs.getInt("p93") == p9Check) count++;
                             if ((rs.getString("username4").equals(user) || rs.getString("userg4").equals(user)) && rs.getInt("p94") == p9Check) count++;
                             if ((rs.getString("username5").equals(user) || rs.getString("userg5").equals(user)) && rs.getInt("p95") == p9Check) count++;
                         }
                     } else {
                         while (rs.next()) {
                             if (rs.getString("username1").equals(user) || rs.getString("userg1").equals(user)) count++;
                             if (rs.getString("username2").equals(user) || rs.getString("userg2").equals(user)) count++;
                             if (rs.getString("username3").equals(user) || rs.getString("userg3").equals(user)) count++;
                             if (rs.getString("username4").equals(user) || rs.getString("userg4").equals(user)) count++;
                             if (rs.getString("username5").equals(user) || rs.getString("userg5").equals(user)) count++;
                         }
                     }
                 } else {

                     // Get a simple tee time count
                     if (rs.next()) {
                         count += rs.getInt(1);     // Get round count from ResultSet
                     }
                 }

                 pstmt.close();
             }
         } catch (Exception exc) {

             skip = true;
             count = -1;
         }

         try {

             //
             //  Now check past tee times
             //
             if (countGuests) {
                 dataToGet = "gtype1, gtype2, gtype3, gtype4, gtype5, " + dataToGet;    // Also get the gtypes from teepast
             }

             pstmt = con.prepareStatement("SELECT " +
                     dataToGet +
                     "FROM teepast2 WHERE " +
                     pastUserCheck +
                     timeframeCheck +
                     timeCheck +
                     eventCheck);

             pstmt.clearParameters();
             pstmt.setString(1, user);
             pstmt.setString(2, mship);
             pstmt.setString(3, user);
             pstmt.setString(4, mship);
             pstmt.setString(5, user);
             pstmt.setString(6, mship);
             pstmt.setString(7, user);
             pstmt.setString(8, mship);
             pstmt.setString(9, user);
             pstmt.setString(10, mship);
             pstmt.setInt(11, timeframe_val_1);
             pstmt.setInt(12, timeframe_val_2);

             if (!memberClass.equals("A")) {

                 pstmt.setInt(13, wkdayRest_stime);
                 pstmt.setInt(14, wkdayRest_etime);
                 pstmt.setInt(15, wkendRest1_stime);
                 pstmt.setInt(16, wkendRest1_etime);

                 // Use family time restriction values for class D only
                 if (memberClass.equals("D")) {
                     pstmt.setInt(17, wkendRest2_fam_stime);
                     pstmt.setInt(18, wkendRest2_fam_etime);
                 } else {
                     pstmt.setInt(17, wkendRest2_norm_stime);
                     pstmt.setInt(18, wkendRest2_norm_etime);
                 }
             }

             rs = pstmt.executeQuery();

             if (countGuests) {

                 // Increment count for each member and guest of this member in the tee time
                 if (memberClass.equalsIgnoreCase("H3")) {
                     // For class H3, make sure to only count the related guest type
                     while (rs.next()) {
                         if (rs.getString("username1").equals(user) || (rs.getString("userg1").equals(user) && rs.getString("gtype1").equalsIgnoreCase(gtype_classH3))) count++;
                         if (rs.getString("username2").equals(user) || (rs.getString("userg2").equals(user) && rs.getString("gtype2").equalsIgnoreCase(gtype_classH3))) count++;
                         if (rs.getString("username3").equals(user) || (rs.getString("userg3").equals(user) && rs.getString("gtype3").equalsIgnoreCase(gtype_classH3))) count++;
                         if (rs.getString("username4").equals(user) || (rs.getString("userg4").equals(user) && rs.getString("gtype4").equalsIgnoreCase(gtype_classH3))) count++;
                         if (rs.getString("username5").equals(user) || (rs.getString("userg5").equals(user) && rs.getString("gtype5").equalsIgnoreCase(gtype_classH3))) count++;
                     }
                 } else if (p9Check >= 0) {
                     while (rs.next()) {
                         if ((rs.getString("username1").equals(user) || rs.getString("userg1").equals(user)) && rs.getInt("p91") == p9Check) count++;
                         if ((rs.getString("username2").equals(user) || rs.getString("userg2").equals(user)) && rs.getInt("p92") == p9Check) count++;
                         if ((rs.getString("username3").equals(user) || rs.getString("userg3").equals(user)) && rs.getInt("p93") == p9Check) count++;
                         if ((rs.getString("username4").equals(user) || rs.getString("userg4").equals(user)) && rs.getInt("p94") == p9Check) count++;
                         if ((rs.getString("username5").equals(user) || rs.getString("userg5").equals(user)) && rs.getInt("p95") == p9Check) count++;
                     }
                 } else {
                     while (rs.next()) {
                         if (rs.getString("username1").equals(user) || rs.getString("userg1").equals(user)) count++;
                         if (rs.getString("username2").equals(user) || rs.getString("userg2").equals(user)) count++;
                         if (rs.getString("username3").equals(user) || rs.getString("userg3").equals(user)) count++;
                         if (rs.getString("username4").equals(user) || rs.getString("userg4").equals(user)) count++;
                         if (rs.getString("username5").equals(user) || rs.getString("userg5").equals(user)) count++;
                     }
                 }
             } else {

                 // Get a simple tee time count
                 if (rs.next()) {
                     count += rs.getInt(1);     // Get round count from ResultSet
                 }
             }

             pstmt.close();



         } catch (Exception exc) {

             skip = true;
             count = -1;
         }
     }


     //if (skip) count = -1;

     return count;
 }

 /**
  * Check # of unrestricted rounds for any class
  * 
  * @param midPacParms Parameter block holding various counters used for Mid Pacific custom restrictions.  Used to return multiple values for some mships
  * @param con Connection to club database
  * @param pastOnly Set to true if only looking for past times and not currently booked times, false if not
  *
  * @return error - True if error was encountered, false otherwise
  */
 public static boolean checkMidPacificUnrestrictedRounds(parmMidPacific midPacParms, Connection con, boolean pastOnly) {

     boolean error = false;
     boolean p9 = false;                // set to true if looking for 9 hole rounds, false if not
     boolean restricted = false;        // set to true if searching restricted times, false if unrestricted
     boolean checkPropGuests = false;   // set to true if looking for prop guest rounds, false if not
     boolean countGuests = false;       // set to true if counting guest rounds as well as player rounds, false if only tee time count

     int count = 0;

     // Get count of restricted rounds for the month of this tee time
     count = 0;
     restricted = false;
     p9 = false;
     checkPropGuests = false;
     countGuests = true;

     count = checkMidPacificRounds(midPacParms.user, midPacParms.mship, midPacParms.teecurr_id, midPacParms.time_mode, p9, restricted, checkPropGuests, countGuests, pastOnly, midPacParms.memberClass, con);

     if (count >= 0) {
         midPacParms.non_month = count;
     } else {
         error = true;
         midPacParms.non_month = -1;
     }

     return error;
 }

 /**
  * Check # of rounds for Class A membership types.  No round restrictions.
  *
  * @param midPacParms Parameter block holding various counters used for Mid Pacific custom restrictions.  Used to return multiple values for some mships
  * @param con Connection to club database
  * @param pastOnly Set to true if only looking for past times and not currently booked times, false if notg for past times and not currently booked times, false if not
  *
  * @return - error - True if member is restricted from booking this tee time, False if no problems are found.
  *         - non_month - # of restricted rounds (player + guest) this month
  */
 public static boolean checkMidPacificClassA(parmMidPacific midPacParms, Connection con, boolean pastOnly) {

     boolean error = false;
     boolean p9 = false;                // set to true if looking for 9 hole rounds, false if not
     boolean restricted = false;        // set to true if searching restricted times, false if unrestricted
     boolean checkPropGuests = false;   // set to true if looking for prop guest rounds, false if not
     boolean countGuests = false;       // set to true if counting guest rounds as well as player rounds, false if only tee time count

     int count = 0;

     // Get count of restricted rounds for the month of this tee time
     count = 0;
     restricted = false;
     p9 = false;
     checkPropGuests = false;
     countGuests = true;

     count = checkMidPacificRounds(midPacParms.user, midPacParms.mship, midPacParms.teecurr_id, midPacParms.time_mode, p9, restricted, checkPropGuests, countGuests, pastOnly, "A", con);

     if (count >= 0) {
         midPacParms.non_month = count;
     } else {
         error = true;
         midPacParms.non_month = -1;
     }

     return error;
 }

 /**
  * Check restrictions for the Class B membership types.  Restrictions are the following:
  * -Unlimited rounds during unrestricted times
  * -May only have guests during unrestricted times (up to 3)
  * -Allowed one (1) tee time per month within restricted times
  * -Allowed one (1) additional restricted tee time per month as the guest of a Proprietary member
  *
  * Restricted times:
  *  -Wed/Thu/Fri - 11:28-1:29
  *  -Sat/Sun - 6:30-8:00 & 10:30-12:30
  *
  * @param midPacParms Parameter block holding various counters used for Mid Pacific custom restrictions.  Used to return multiple values for some mships
  * @param con Connection to club database
  * @param pastOnly Set to true if only looking for past times and not currently booked times, false if not
  *
  * @return - error - True if member is restricted from booking this tee time, False if no problems are found.
  *         - res_month - # of restricted rounds (player + guest) this month
  *         - propGuestRounds - # of tee times as the guest of a Proprietary member
  */
 public static boolean checkMidPacificClassB(parmMidPacific midPacParms, Connection con, boolean pastOnly) {

     boolean error = false;
     boolean p9 = false;                // set to true if looking for 9 hole rounds, false if not
     boolean restricted = false;        // set to true if searching restricted times, false if unrestricted
     boolean checkPropGuests = false;   // set to true if looking for prop guest rounds, false if not
     boolean countGuests = false;       // set to true if counting guest rounds as well as player rounds, false if only tee time count

     int count = 0;

     // Get count of restricted rounds for the month of this tee time
     count = 0;
     restricted = true;
     p9 = false;
     checkPropGuests = false;
     countGuests = false;

     count = checkMidPacificRounds(midPacParms.user, midPacParms.mship, midPacParms.teecurr_id, midPacParms.time_mode, p9, restricted, checkPropGuests, countGuests, pastOnly, "B", con);

     if (count >= 0) {
         midPacParms.res_month = count;
     } else {
         error = true;
         midPacParms.res_month = -1;
     }

     // Skip check if error already encountered
     if (!error) {

         // Get count of rounds as the guest of a proprietary member
         count = 0;
         restricted = true;
         p9 = false;
         checkPropGuests = true;
         countGuests = false;

         count = checkMidPacificRounds(midPacParms.user, midPacParms.mship, midPacParms.teecurr_id, midPacParms.time_mode, p9, restricted, checkPropGuests, countGuests, pastOnly, "B", con);

         if (count >= 0) {
             midPacParms.propGuestRounds = count;
         } else {
             error = true;
             midPacParms.propGuestRounds = -1;
         }
     }

     return error;
 }


 /**
  * Check restrictions for the Class C membership types.  Restrictions are the following:
  * -Unlimited rounds during unrestricted times
  * -May only have guests during unrestricted times (up to 3)
  * -Allowed one (1) tee time per month within restricted times
  * -Allowed one (1) additional restricted tee time per month as the guest of a Proprietary member
  * -'Nine & Dine' event does not count towards their one round per month
  *
  * Restricted times:
  *  -Wed/Thu/Fri - 11:28-1:29
  *  -Sat/Sun - 6:30-8:00 & 10:30-12:30
  *
  * @param midPacParms Parameter block holding various counters used for Mid Pacific custom restrictions.  Used to return multiple values for some mships
  * @param con Connection to club database
  * @param pastOnly Set to true if only looking for past times and not currently booked times, false if not
  *
  * @return - error - True if member is restricted from booking this tee time, False if no problems are found.
  *         - res_month - # of restricted rounds (player + guest) this month
  *         - propGuestRounds - # of tee times as the guest of a Proprietary member
  */
 public static boolean checkMidPacificClassC(parmMidPacific midPacParms, Connection con, boolean pastOnly) {

     boolean error = false;
     boolean p9 = false;                // set to true if looking for 9 hole rounds, false if not
     boolean restricted = false;        // set to true if searching restricted times, false if unrestricted
     boolean checkPropGuests = false;   // set to true if looking for prop guest rounds, false if not
     boolean countGuests = false;       // set to true if counting guest rounds as well as player rounds, false if only tee time count

     int count = 0;

     // Get count of restricted rounds for the month of this tee time
     count = 0;
     restricted = true;
     p9 = false;
     checkPropGuests = false;
     countGuests = false;

     count = checkMidPacificRounds(midPacParms.user, midPacParms.mship, midPacParms.teecurr_id, midPacParms.time_mode, p9, restricted, checkPropGuests, countGuests, pastOnly, "C", con);

     if (count >= 0) {
         midPacParms.res_month = count;
     } else {
         error = true;
         midPacParms.res_month = -1;
     }

     // Skip check if error already encountered
     if (!error) {

         // Get count of rounds as the guest of a proprietary member
         count = 0;
         restricted = true;
         p9 = false;
         checkPropGuests = true;
         countGuests = false;

         count = checkMidPacificRounds(midPacParms.user, midPacParms.mship, midPacParms.teecurr_id, midPacParms.time_mode, p9, restricted, checkPropGuests, countGuests, pastOnly, "C", con);

         if (count >= 0) {
             midPacParms.propGuestRounds = count;
         } else {
             error = true;
             midPacParms.propGuestRounds = -1;
         }
     }


     return error;
 }


 /**
  * Check restrictions for the Class D membership types.  Restrictions are the following:
  * -Unlimited rounds during unrestricted times
  * -May only have guests during unrestricted times (up to 3)
  * -Guests (up to 3) allowed only after 2:45 pm on Sat/Sun
  * -Allowed to play during Family Times (10:30-12:22) on Sat/Sun (no guests)
  *
  * Restricted times:
  *  -Wed/Thu/Fri - 11:28-1:29
  *  -Sat/Sun - 6:30-8:00 & 12:22-2:45
  *
  * @param midPacParms Parameter block holding various counters used for Mid Pacific custom restrictions.  Used to return multiple values for some mships
  * @param con Connection to club database
  * @param pastOnly Set to true if only looking for past times and not currently booked times, false if not
  *
  * @return - error - True if member is restricted from booking this tee time, False if no problems are found.
  *         - non_month - # of unrestricted rounds (player + guest) this month
  */
 public static boolean checkMidPacificClassD(parmMidPacific midPacParms, Connection con, boolean pastOnly) {

     boolean error = false;
     boolean p9 = false;                // set to true if looking for 9 hole rounds, false if not
     boolean restricted = false;        // set to true if searching restricted times, false if unrestricted
     boolean checkPropGuests = false;   // set to true if looking for prop guest rounds, false if not
     boolean countGuests = false;       // set to true if counting guest rounds as well as player rounds, false if only tee time count

     int count = 0;

     // Get count of unrestricted rounds for the month of this tee time (no round restrictions, simply built for possible report usage)
     count = 0;
     restricted = false;
     p9 = false;
     checkPropGuests = false;
     countGuests = true;

     count = checkMidPacificRounds(midPacParms.user, midPacParms.mship, midPacParms.teecurr_id, midPacParms.time_mode, p9, restricted, checkPropGuests, countGuests, pastOnly, "D", con);

     if (count >= 0) {
         midPacParms.non_month = count;
     } else {
         error = true;
         midPacParms.non_month = -1;
     }

     return error;
 }


 /**
  * Check restrictions for the Class H1 membership types.  Restrictions are the following:
  * -Twelve (12) 18-hole rounds or twenty-four (24) 9-hole rounds per year during unrestricted times
  * -May only have guests during unrestricted times (up to 3)
  * -May purchase extra rounds from golf shop ($130.00)
  *
  * Restricted times:
  *  -Wed/Thu/Fri - 11:28-1:29
  *  -Sat/Sun - 6:30-8:00 & 10:30-12:30
  *
  * @param midPacParms Parameter block holding various counters used for Mid Pacific custom restrictions.  Used to return multiple values for some mships
  * @param con Connection to club database
  * @param pastOnly Set to true if only looking for past times and not currently booked times, false if not
  *
  * @return - error - True if member is restricted from booking this tee time, False if no problems are found.
  *         - rounds9_year - # of unrestricted 9-hole rounds (player + guest) this year.
  *         - rounds18_year - # of unrestricted 18-hole rounds (player + guest) this year.
  */
 public static boolean checkMidPacificClassH1(parmMidPacific midPacParms, Connection con, boolean pastOnly) {

     boolean error = false;
     boolean p9 = false;                // set to true if looking for 9 hole rounds, false if not
     boolean restricted = false;        // set to true if searching restricted times, false if unrestricted
     boolean checkPropGuests = false;   // set to true if looking for prop guest rounds, false if not
     boolean countGuests = false;       // set to true if counting guest rounds as well as player rounds, false if only tee time count

     int count = 0;

     // Get count of unrestricted 9-hole rounds for this year
     count = 0;
     restricted = false;
     p9 = true;
     checkPropGuests = false;
     countGuests = true;

     count = checkMidPacificRounds(midPacParms.user, midPacParms.mship, midPacParms.teecurr_id, midPacParms.time_mode, p9, restricted, checkPropGuests, countGuests, pastOnly, "H1", con);

     if (count >= 0) {
         midPacParms.rounds9_year = count;
     } else {
         error = true;
         midPacParms.rounds9_year = -1;
     }
     
     if (!error) {

         // Get count of unrestricted 18-hole rounds for this year
         count = 0;
         restricted = false;
         p9 = false;
         checkPropGuests = false;
         countGuests = true;

         count = checkMidPacificRounds(midPacParms.user, midPacParms.mship, midPacParms.teecurr_id, midPacParms.time_mode, p9, restricted, checkPropGuests, countGuests, pastOnly, "H1", con);

         if (count >= 0) {
             midPacParms.rounds18_year = count;
         } else {
             error = true;
             midPacParms.rounds18_year = -1;
         }

     }

     return error;
 }


 /**
  * Check restrictions for the Class H2 membership types.  Restrictions are the following:
  * -Allowed one (1) 18-hole or two (2) 9-hole rounds per month during unrestricted times
  * -May only have guests during unrestricted times (up to 3)
  *
  * Restricted times:
  *  -Wed/Thu/Fri - 11:28-1:29
  *  -Sat/Sun - 6:30-8:00 & 10:30-12:30
  *
  * @param midPacParms Parameter block holding various counters used for Mid Pacific custom restrictions.  Used to return multiple values for some mships
  * @param con Connection to club database
  * @param pastOnly Set to true if only looking for past times and not currently booked times, false if not
  *
  * @return - error - True if member is restricted from booking this tee time, False if no problems are found.
  *         - rounds9_month - # of unrestricted 9 hole rounds (player + guest) this month
  *         - rounds18_month - # of unrestricted 18 hole rounds (player + guest) this month
  */
 public static boolean checkMidPacificClassH2(parmMidPacific midPacParms, Connection con, boolean pastOnly) {

     boolean error = false;
     boolean p9 = false;                // set to true if looking for 9 hole rounds, false if not
     boolean restricted = false;        // set to true if searching restricted times, false if unrestricted
     boolean checkPropGuests = false;   // set to true if looking for prop guest rounds, false if not
     boolean countGuests = false;       // set to true if counting guest rounds as well as player rounds, false if only tee time count

     int count = 0;

     // Get count of unrestricted 9 hole rounds for the month of this tee time
     count = 0;
     restricted = false;
     p9 = true;
     checkPropGuests = false;
     countGuests = true;

     count = checkMidPacificRounds(midPacParms.user, midPacParms.mship, midPacParms.teecurr_id, midPacParms.time_mode, p9, restricted, checkPropGuests, countGuests, pastOnly, "H2", con);

     if (count >= 0) {
         midPacParms.rounds9_month = count;
     } else {
         error = true;
         midPacParms.rounds9_month = -1;
     }

     // Skip check if error already encountered
     if (!error) {

         // Get count of unrestricted 18 hole rounds for the month of this tee time
         count = 0;
         restricted = false;
         p9 = false;
         checkPropGuests = false;
         countGuests = true;

         count = checkMidPacificRounds(midPacParms.user, midPacParms.mship, midPacParms.teecurr_id, midPacParms.time_mode, p9, restricted, checkPropGuests, countGuests, pastOnly, "H2", con);

         if (count >= 0) {
             midPacParms.rounds18_month = count;
         } else {
             error = true;
             midPacParms.rounds18_month = -1;
         }
     }

     return error;
 }


 /**
  * Check restrictions for the Class H3 membership types.  Restrictions are the following:
  * -Allowed (168) player rounds of golf per year.  A player round consists of 1 for each round by the member, and 1 for each 'SOCLEG' guest of the member
  * -Allowed up to eight (8) individual tee times per month.  Up to four (4) of these may be during restricted times
  * -May have guests at any time (up to 3)
  *
  * Restricted times:
  *  -Wed/Thu/Fri - 11:28-1:29
  *  -Sat/Sun - 6:30-8:00 & 10:30-12:30
  *
  * @param midPacParms Parameter block holding various counters used for Mid Pacific custom restrictions.  Used to return multiple values for some mships
  * @param con Connection to club database
  * @param pastOnly Set to true if only looking for past times and not currently booked times, false if not
  *
  * @return - error - True if member is restricted from booking this tee time, False if no problems are found.
  *         - playerRounds_year - total rounds (player + guest) this year
  *         - res_month - # of restricted tee times this month
  *         - non_month - # of unrestricted tee times this month
  */
 public static boolean checkMidPacificClassH3(parmMidPacific midPacParms, Connection con, boolean includeTotalRounds, boolean pastOnly) {

     boolean error = false;
     boolean p9 = false;                // set to true if looking for 9 hole rounds, false if not
     boolean restricted = false;        // set to true if searching restricted times, false if unrestricted
     boolean checkPropGuests = false;   // set to true if looking for prop guest rounds, false if not
     boolean countGuests = false;       // set to true if counting guest rounds as well as player rounds, false if only tee time count

     int count = 0;
     int time_mode = 0;

     if (includeTotalRounds) {
         // Get count of unrestricted rounds for this year
         count = 0;
         restricted = false;
         p9 = false;
         checkPropGuests = false;
         countGuests = true;

         count = checkMidPacificRounds(midPacParms.user, midPacParms.mship, midPacParms.teecurr_id, time_mode, p9, restricted, checkPropGuests, countGuests, pastOnly, "H3", con);

         if (count >= 0) {
             midPacParms.playerRounds_year = count;
         } else {
             error = true;
             midPacParms.playerRounds_year = -1;
         }

         if (!error) {
             // Add the restricted rounds to the count
             count = 0;
             restricted = true;
             p9 = false;
             checkPropGuests = false;
             countGuests = true;

             count = checkMidPacificRounds(midPacParms.user, midPacParms.mship, midPacParms.teecurr_id, time_mode, p9, restricted, checkPropGuests, countGuests, pastOnly, "H3", con);

             if (count >= 0) {
                 midPacParms.playerRounds_year += count;
             } else {
                 error = true;
                 midPacParms.playerRounds_year = -1;
             }
         }
     }

     // Skip check if error already encountered
     if (!error) {

         // Get count of restricted tee times this month
         count = 0;
         restricted = true;
         p9 = false;
         checkPropGuests = false;
         countGuests = false;

         count = checkMidPacificRounds(midPacParms.user, midPacParms.mship, midPacParms.teecurr_id, midPacParms.time_mode, p9, restricted, checkPropGuests, countGuests, pastOnly, "H3", con);

         if (count >= 0) {
             midPacParms.res_month = count;
         } else {
             error = true;
             midPacParms.res_month = -1;
         }
     }

     // Skip check if error already encountered
     if (!error) {

         // Get count of unrestricted tee times this month
         count = 0;
         restricted = false;
         p9 = false;
         checkPropGuests = false;
         countGuests = false;

         count = checkMidPacificRounds(midPacParms.user, midPacParms.mship, midPacParms.teecurr_id, midPacParms.time_mode, p9, restricted, checkPropGuests, countGuests, pastOnly, "H3", con);

         if (count >= 0) {
             midPacParms.non_month = count;
         } else {
             error = true;
             midPacParms.non_month = -1;
         }
     }

     return error;
 }


 /**
  * Check restrictions for the Class H2 membership types.  Restrictions are the following:
  * -Allowed three (3) 18-hole or six (6) 9-hole rounds per month during unrestricted times
  * -May only have guests during unrestricted times (up to 3)
  * -Allowed one (1) tee time per month within restricted times
  * -Allowed one (1) additional restricted tee time per month as the guest of a Proprietary member
  *
  * Restricted times:
  *  -Wed/Thu/Fri - 11:28-1:29
  *  -Sat/Sun - 6:30-8:00 & 10:30-12:30
  *
  * @param midPacParms Parameter block holding various counters used for Mid Pacific custom restrictions.  Used to return multiple values for some mships
  * @param con Connection to club database
  * @param pastOnly Set to true if only looking for past times and not currently booked times, false if not
  *
  * @return - error - True if member is restricted from booking this tee time, False if no problems are found.
  *         - rounds9_month - # of unrestricted 9 hole rounds (player + guest) this month
  *         - rounds18_month - # of unrestricted 18 hole rounds (player + guest) this month
  *         - res_month - # of restricted rounds (player + guest) this month
  *         - propGuestRounds - # of tee times as the guest of a Proprietary member
  */
 public static boolean checkMidPacificClassJ(parmMidPacific midPacParms, Connection con, boolean pastOnly) {

     boolean error = false;
     boolean p9 = false;                // set to true if looking for 9 hole rounds, false if not
     boolean restricted = false;        // set to true if searching restricted times, false if unrestricted
     boolean checkPropGuests = false;   // set to true if looking for prop guest rounds, false if not
     boolean countGuests = false;       // set to true if counting guest rounds as well as player rounds, false if only tee time count

     int count = 0;

     // Get count of unrestricted 9 hole rounds for the month of this tee time
     count = 0;
     restricted = false;
     p9 = true;
     checkPropGuests = false;
     countGuests = true;

     count = checkMidPacificRounds(midPacParms.user, midPacParms.mship, midPacParms.teecurr_id, midPacParms.time_mode, p9, restricted, checkPropGuests, countGuests, pastOnly, "J", con);

     if (count >= 0) {
         midPacParms.rounds9_month = count;
     } else {
         error = true;
         midPacParms.rounds9_month = -1;
     }

     // Skip check if error already encountered
     if (!error) {

         // Get count of unrestricted 18 hole rounds for the month of this tee time
         count = 0;
         restricted = false;
         p9 = false;
         checkPropGuests = false;
         countGuests = true;

         count = checkMidPacificRounds(midPacParms.user, midPacParms.mship, midPacParms.teecurr_id, midPacParms.time_mode, p9, restricted, checkPropGuests, countGuests, pastOnly, "J", con);

         if (count >= 0) {
             midPacParms.rounds18_month = count;
         } else {
             error = true;
             midPacParms.rounds18_month = -1;
         }
     }

     // Skip check if error already encountered
     if (!error) {

         // Get count of restricted rounds for the month of this tee time
         count = 0;
         restricted = true;
         p9 = false;
         checkPropGuests = false;
         countGuests = true;
         count = checkMidPacificRounds(midPacParms.user, midPacParms.mship, midPacParms.teecurr_id, midPacParms.time_mode, p9, restricted, checkPropGuests, countGuests, pastOnly, "J", con);

         if (count >= 0) {
             midPacParms.res_month = count;
         } else {
             error = true;
             midPacParms.res_month = -1;
         }
     }

     // Skip check if error already encountered
     if (!error) {

         // Get count of rounds as the guest of a proprietary member
         count = 0;
         restricted = true;
         p9 = false;
         checkPropGuests = true;
         countGuests = false;
         count = checkMidPacificRounds(midPacParms.user, midPacParms.mship, midPacParms.teecurr_id, midPacParms.time_mode, p9, restricted, checkPropGuests, countGuests, pastOnly, "J", con);

         if (count >= 0) {
             midPacParms.propGuestRounds = count;
         } else {
             error = true;
             midPacParms.propGuestRounds = -1;
         }
     }


     return error;
 }

}